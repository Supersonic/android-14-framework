package com.android.server.p011pm;

import android.apex.ApexInfo;
import android.apex.ApexSessionInfo;
import android.apex.ApexSessionParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApexStagedEvent;
import android.content.pm.IStagedApexObserver;
import android.content.pm.PackageInstaller;
import android.content.pm.StagedApexInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimingsTraceLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.InstallLocationUtils;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.p011pm.StagingManager;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.pkg.PackageStateUtils;
import com.android.server.rollback.RollbackManagerInternal;
import com.android.server.rollback.WatchdogRollbackLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
/* renamed from: com.android.server.pm.StagingManager */
/* loaded from: classes2.dex */
public class StagingManager {
    public final ApexManager mApexManager;
    public final CompletableFuture<Void> mBootCompleted;
    public final Context mContext;
    @GuardedBy({"mFailedPackageNames"})
    public final List<String> mFailedPackageNames;
    public String mFailureReason;
    public final File mFailureReasonFile;
    public String mNativeFailureReason;
    public final PowerManager mPowerManager;
    @GuardedBy({"mStagedApexObservers"})
    public final List<IStagedApexObserver> mStagedApexObservers;
    @GuardedBy({"mStagedSessions"})
    public final SparseArray<StagedSession> mStagedSessions;
    @GuardedBy({"mSuccessfulStagedSessionIds"})
    public final List<Integer> mSuccessfulStagedSessionIds;

    /* renamed from: com.android.server.pm.StagingManager$StagedSession */
    /* loaded from: classes2.dex */
    public interface StagedSession {
        void abandon();

        boolean containsApexSession();

        List<StagedSession> getChildSessions();

        long getCommittedMillis();

        String getPackageName();

        int getParentSessionId();

        boolean hasParentSessionId();

        CompletableFuture<Void> installSession();

        boolean isApexSession();

        boolean isCommitted();

        boolean isDestroyed();

        boolean isInTerminalState();

        boolean isMultiPackage();

        boolean isSessionFailed();

        boolean isSessionReady();

        boolean sessionContains(Predicate<StagedSession> predicate);

        int sessionId();

        PackageInstaller.SessionParams sessionParams();

        void setSessionFailed(int i, String str);

        void setSessionReady();

        void verifySession();
    }

    public StagingManager(Context context) {
        this(context, ApexManager.getInstance());
    }

    @VisibleForTesting
    public StagingManager(Context context, ApexManager apexManager) {
        File file = new File("/metadata/staged-install/failure_reason.txt");
        this.mFailureReasonFile = file;
        this.mStagedSessions = new SparseArray<>();
        this.mFailedPackageNames = new ArrayList();
        this.mSuccessfulStagedSessionIds = new ArrayList();
        this.mStagedApexObservers = new ArrayList();
        this.mBootCompleted = new CompletableFuture<>();
        this.mContext = context;
        this.mApexManager = apexManager;
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                this.mFailureReason = bufferedReader.readLine();
                bufferedReader.close();
            } catch (Exception unused) {
            }
        }
    }

    /* renamed from: com.android.server.pm.StagingManager$Lifecycle */
    /* loaded from: classes2.dex */
    public static final class Lifecycle extends SystemService {
        public static StagingManager sStagingManager;

        @Override // com.android.server.SystemService
        public void onStart() {
        }

        public Lifecycle(Context context) {
            super(context);
        }

        public void startService(StagingManager stagingManager) {
            sStagingManager = stagingManager;
            ((SystemServiceManager) LocalServices.getService(SystemServiceManager.class)).startService(this);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            StagingManager stagingManager;
            if (i != 1000 || (stagingManager = sStagingManager) == null) {
                return;
            }
            stagingManager.markStagedSessionsAsSuccessful();
            sStagingManager.markBootCompleted();
        }
    }

    public final void markBootCompleted() {
        this.mApexManager.markBootCompleted();
    }

    public void registerStagedApexObserver(final IStagedApexObserver iStagedApexObserver) {
        if (iStagedApexObserver == null) {
            return;
        }
        if (iStagedApexObserver.asBinder() != null) {
            try {
                iStagedApexObserver.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.pm.StagingManager.1
                    @Override // android.os.IBinder.DeathRecipient
                    public void binderDied() {
                        synchronized (StagingManager.this.mStagedApexObservers) {
                            StagingManager.this.mStagedApexObservers.remove(iStagedApexObserver);
                        }
                    }
                }, 0);
            } catch (RemoteException e) {
                Slog.w("StagingManager", e.getMessage());
            }
        }
        synchronized (this.mStagedApexObservers) {
            this.mStagedApexObservers.add(iStagedApexObserver);
        }
    }

    public void unregisterStagedApexObserver(IStagedApexObserver iStagedApexObserver) {
        synchronized (this.mStagedApexObservers) {
            this.mStagedApexObservers.remove(iStagedApexObserver);
        }
    }

    public final void abortCheckpoint(String str, boolean z, boolean z2) {
        Slog.e("StagingManager", str);
        if (z && z2) {
            try {
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.mFailureReasonFile));
                    bufferedWriter.write(str);
                    bufferedWriter.close();
                } catch (Exception e) {
                    Slog.w("StagingManager", "Failed to save failure reason: ", e);
                }
                if (this.mApexManager.isApexSupported()) {
                    this.mApexManager.revertActiveSessions();
                }
                InstallLocationUtils.getStorageManager().abortChanges("abort-staged-install", false);
            } catch (Exception e2) {
                Slog.wtf("StagingManager", "Failed to abort checkpoint", e2);
                if (this.mApexManager.isApexSupported()) {
                    this.mApexManager.revertActiveSessions();
                }
                this.mPowerManager.reboot(null);
            }
        }
    }

    public final List<StagedSession> extractApexSessions(StagedSession stagedSession) {
        ArrayList arrayList = new ArrayList();
        if (stagedSession.isMultiPackage()) {
            for (StagedSession stagedSession2 : stagedSession.getChildSessions()) {
                if (stagedSession2.containsApexSession()) {
                    arrayList.add(stagedSession2);
                }
            }
        } else {
            arrayList.add(stagedSession);
        }
        return arrayList;
    }

    public final void checkInstallationOfApkInApexSuccessful(StagedSession stagedSession) throws PackageManagerException {
        List<StagedSession> extractApexSessions = extractApexSessions(stagedSession);
        if (extractApexSessions.isEmpty()) {
            return;
        }
        for (StagedSession stagedSession2 : extractApexSessions) {
            String packageName = stagedSession2.getPackageName();
            String apkInApexInstallError = this.mApexManager.getApkInApexInstallError(packageName);
            if (apkInApexInstallError != null) {
                throw new PackageManagerException(-128, "Failed to install apk-in-apex of " + packageName + " : " + apkInApexInstallError);
            }
        }
    }

    public final void snapshotAndRestoreForApexSession(StagedSession stagedSession) {
        if ((stagedSession.sessionParams().installFlags & 262144) != 0 || stagedSession.sessionParams().installReason == 5) {
            List<StagedSession> extractApexSessions = extractApexSessions(stagedSession);
            if (extractApexSessions.isEmpty()) {
                return;
            }
            int[] userIds = ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getUserIds();
            RollbackManagerInternal rollbackManagerInternal = (RollbackManagerInternal) LocalServices.getService(RollbackManagerInternal.class);
            int size = extractApexSessions.size();
            for (int i = 0; i < size; i++) {
                String packageName = extractApexSessions.get(i).getPackageName();
                snapshotAndRestoreApexUserData(packageName, userIds, rollbackManagerInternal);
                List<String> apksInApex = this.mApexManager.getApksInApex(packageName);
                int size2 = apksInApex.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    snapshotAndRestoreApkInApexUserData(apksInApex.get(i2), userIds, rollbackManagerInternal);
                }
            }
        }
    }

    public final void snapshotAndRestoreApexUserData(String str, int[] iArr, RollbackManagerInternal rollbackManagerInternal) {
        rollbackManagerInternal.snapshotAndRestoreUserData(str, UserHandle.toUserHandles(iArr), 0, 0L, null, 0);
    }

    public final void snapshotAndRestoreApkInApexUserData(String str, int[] iArr, RollbackManagerInternal rollbackManagerInternal) {
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        if (packageManagerInternal.getPackage(str) == null) {
            Slog.e("StagingManager", "Could not find package: " + str + "for snapshotting/restoring user data.");
            return;
        }
        PackageStateInternal packageStateInternal = packageManagerInternal.getPackageStateInternal(str);
        if (packageStateInternal != null) {
            int appId = packageStateInternal.getAppId();
            long ceDataInode = packageStateInternal.getUserStateOrDefault(0).getCeDataInode();
            int[] queryInstalledUsers = PackageStateUtils.queryInstalledUsers(packageStateInternal, iArr, true);
            rollbackManagerInternal.snapshotAndRestoreUserData(str, UserHandle.toUserHandles(queryInstalledUsers), appId, ceDataInode, packageStateInternal.getSeInfo(), 0);
        }
    }

    public final void prepareForLoggingApexdRevert(StagedSession stagedSession, String str) {
        synchronized (this.mFailedPackageNames) {
            this.mNativeFailureReason = str;
            if (stagedSession.getPackageName() != null) {
                this.mFailedPackageNames.add(stagedSession.getPackageName());
            }
        }
    }

    public final void resumeSession(StagedSession stagedSession, boolean z, boolean z2) throws PackageManagerException {
        Slog.d("StagingManager", "Resuming session " + stagedSession.sessionId());
        boolean containsApexSession = stagedSession.containsApexSession();
        if (z && !z2) {
            String str = "Reverting back to safe state. Marking " + stagedSession.sessionId() + " as failed.";
            String reasonForRevert = getReasonForRevert();
            if (!TextUtils.isEmpty(reasonForRevert)) {
                str = str + " Reason for revert: " + reasonForRevert;
            }
            Slog.d("StagingManager", str);
            stagedSession.setSessionFailed(-110, str);
            return;
        }
        if (containsApexSession) {
            checkInstallationOfApkInApexSuccessful(stagedSession);
            checkDuplicateApkInApex(stagedSession);
            snapshotAndRestoreForApexSession(stagedSession);
            Slog.i("StagingManager", "APEX packages in session " + stagedSession.sessionId() + " were successfully activated. Proceeding with APK packages, if any");
        }
        Slog.d("StagingManager", "Installing APK packages in session " + stagedSession.sessionId());
        TimingsTraceLog timingsTraceLog = new TimingsTraceLog("StagingManagerTiming", 262144L);
        timingsTraceLog.traceBegin("installApksInSession");
        installApksInSession(stagedSession);
        timingsTraceLog.traceEnd();
        if (containsApexSession) {
            if (z) {
                synchronized (this.mSuccessfulStagedSessionIds) {
                    this.mSuccessfulStagedSessionIds.add(Integer.valueOf(stagedSession.sessionId()));
                }
                return;
            }
            this.mApexManager.markStagedSessionSuccessful(stagedSession.sessionId());
        }
    }

    public void onInstallationFailure(StagedSession stagedSession, PackageManagerException packageManagerException, boolean z, boolean z2) {
        stagedSession.setSessionFailed(packageManagerException.error, packageManagerException.getMessage());
        abortCheckpoint("Failed to install sessionId: " + stagedSession.sessionId() + " Error: " + packageManagerException.getMessage(), z, z2);
        if (stagedSession.containsApexSession()) {
            if (!this.mApexManager.revertActiveSessions()) {
                Slog.e("StagingManager", "Failed to abort APEXd session");
                return;
            }
            Slog.e("StagingManager", "Successfully aborted apexd session. Rebooting device in order to revert to the previous state of APEXd.");
            this.mPowerManager.reboot(null);
        }
    }

    public final String getReasonForRevert() {
        if (!TextUtils.isEmpty(this.mFailureReason)) {
            return this.mFailureReason;
        }
        if (TextUtils.isEmpty(this.mNativeFailureReason)) {
            return "";
        }
        return "Session reverted due to crashing native process: " + this.mNativeFailureReason;
    }

    public final void checkDuplicateApkInApex(StagedSession stagedSession) throws PackageManagerException {
        if (stagedSession.isMultiPackage()) {
            ArraySet arraySet = new ArraySet();
            for (StagedSession stagedSession2 : stagedSession.getChildSessions()) {
                if (!stagedSession2.isApexSession()) {
                    arraySet.add(stagedSession2.getPackageName());
                }
            }
            for (StagedSession stagedSession3 : extractApexSessions(stagedSession)) {
                String packageName = stagedSession3.getPackageName();
                for (String str : this.mApexManager.getApksInApex(packageName)) {
                    if (!arraySet.add(str)) {
                        throw new PackageManagerException(-128, "Package: " + packageName + " in session: " + stagedSession3.sessionId() + " has duplicate apk-in-apex: " + str, (Throwable) null);
                    }
                }
            }
        }
    }

    public final void installApksInSession(StagedSession stagedSession) throws PackageManagerException {
        try {
            stagedSession.installSession().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e2) {
            throw ((PackageManagerException) e2.getCause());
        }
    }

    @VisibleForTesting
    public void commitSession(StagedSession stagedSession) {
        createSession(stagedSession);
        handleCommittedSession(stagedSession);
    }

    public final void handleCommittedSession(StagedSession stagedSession) {
        if (stagedSession.isSessionReady() && stagedSession.containsApexSession()) {
            notifyStagedApexObservers();
        }
    }

    @VisibleForTesting
    public void createSession(StagedSession stagedSession) {
        synchronized (this.mStagedSessions) {
            this.mStagedSessions.append(stagedSession.sessionId(), stagedSession);
        }
    }

    public void abortSession(StagedSession stagedSession) {
        synchronized (this.mStagedSessions) {
            this.mStagedSessions.remove(stagedSession.sessionId());
        }
    }

    public void abortCommittedSession(StagedSession stagedSession) {
        int sessionId = stagedSession.sessionId();
        if (stagedSession.isInTerminalState()) {
            Slog.w("StagingManager", "Cannot abort session in final state: " + sessionId);
        } else if (!stagedSession.isDestroyed()) {
            throw new IllegalStateException("Committed session must be destroyed before aborting it from StagingManager");
        } else {
            if (getStagedSession(sessionId) == null) {
                Slog.w("StagingManager", "Session " + sessionId + " has been abandoned already");
                return;
            }
            if (stagedSession.isSessionReady()) {
                if (!ensureActiveApexSessionIsAborted(stagedSession)) {
                    Slog.e("StagingManager", "Failed to abort apex session " + stagedSession.sessionId());
                }
                if (stagedSession.containsApexSession()) {
                    notifyStagedApexObservers();
                }
            }
            abortSession(stagedSession);
        }
    }

    public final boolean ensureActiveApexSessionIsAborted(StagedSession stagedSession) {
        ApexSessionInfo stagedSessionInfo;
        if (!stagedSession.containsApexSession() || (stagedSessionInfo = this.mApexManager.getStagedSessionInfo(stagedSession.sessionId())) == null || isApexSessionFinalized(stagedSessionInfo)) {
            return true;
        }
        return this.mApexManager.abortStagedSession(stagedSession.sessionId());
    }

    public final boolean isApexSessionFinalized(ApexSessionInfo apexSessionInfo) {
        return apexSessionInfo.isUnknown || apexSessionInfo.isActivationFailed || apexSessionInfo.isSuccess || apexSessionInfo.isReverted;
    }

    public static boolean isApexSessionFailed(ApexSessionInfo apexSessionInfo) {
        return apexSessionInfo.isActivationFailed || apexSessionInfo.isUnknown || apexSessionInfo.isReverted || apexSessionInfo.isRevertInProgress || apexSessionInfo.isRevertFailed;
    }

    public final void handleNonReadyAndDestroyedSessions(List<StagedSession> list) {
        int size = list.size();
        int i = 0;
        while (i < size) {
            final StagedSession stagedSession = list.get(i);
            if (stagedSession.isDestroyed()) {
                stagedSession.abandon();
                list.set(i, list.set(size - 1, stagedSession));
            } else if (stagedSession.isSessionReady()) {
                i++;
            } else {
                Slog.i("StagingManager", "Restart verification for session=" + stagedSession.sessionId());
                this.mBootCompleted.thenRun(new Runnable() { // from class: com.android.server.pm.StagingManager$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        StagingManager.StagedSession.this.verifySession();
                    }
                });
                list.set(i, list.set(size + (-1), stagedSession));
            }
            size--;
        }
        list.subList(size, list.size()).clear();
    }

    public void restoreSessions(List<StagedSession> list, boolean z) {
        TimingsTraceLog timingsTraceLog = new TimingsTraceLog("StagingManagerTiming", 262144L);
        timingsTraceLog.traceBegin("restoreSessions");
        if (SystemProperties.getBoolean("sys.boot_completed", false)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            StagedSession stagedSession = list.get(i);
            Preconditions.checkArgument(!stagedSession.hasParentSessionId(), stagedSession.sessionId() + " is a child session");
            Preconditions.checkArgument(stagedSession.isCommitted(), stagedSession.sessionId() + " is not committed");
            Preconditions.checkArgument(true ^ stagedSession.isInTerminalState(), stagedSession.sessionId() + " is in terminal state");
            createSession(stagedSession);
        }
        if (z) {
            for (int i2 = 0; i2 < list.size(); i2++) {
                list.get(i2).setSessionFailed(-128, "Build fingerprint has changed");
            }
            return;
        }
        try {
            boolean supportsCheckpoint = InstallLocationUtils.getStorageManager().supportsCheckpoint();
            boolean needsCheckpoint = InstallLocationUtils.getStorageManager().needsCheckpoint();
            if (list.size() > 1 && !supportsCheckpoint) {
                throw new IllegalStateException("Detected multiple staged sessions on a device without fs-checkpoint support");
            }
            handleNonReadyAndDestroyedSessions(list);
            SparseArray<ApexSessionInfo> sessions = this.mApexManager.getSessions();
            boolean z2 = false;
            boolean z3 = false;
            for (int i3 = 0; i3 < list.size(); i3++) {
                StagedSession stagedSession2 = list.get(i3);
                if (stagedSession2.containsApexSession()) {
                    ApexSessionInfo apexSessionInfo = sessions.get(stagedSession2.sessionId());
                    if (apexSessionInfo == null || apexSessionInfo.isUnknown) {
                        stagedSession2.setSessionFailed(-128, "apexd did not know anything about a staged session supposed to be activated");
                    } else if (isApexSessionFailed(apexSessionInfo)) {
                        if (!TextUtils.isEmpty(apexSessionInfo.crashingNativeProcess)) {
                            prepareForLoggingApexdRevert(stagedSession2, apexSessionInfo.crashingNativeProcess);
                        }
                        String reasonForRevert = getReasonForRevert();
                        String str = "APEX activation failed.";
                        if (!TextUtils.isEmpty(reasonForRevert)) {
                            str = "APEX activation failed. Reason: " + reasonForRevert;
                        } else if (!TextUtils.isEmpty(apexSessionInfo.errorMessage)) {
                            str = "APEX activation failed. Error: " + apexSessionInfo.errorMessage;
                        }
                        Slog.d("StagingManager", str);
                        stagedSession2.setSessionFailed(-128, str);
                    } else if (apexSessionInfo.isActivated || apexSessionInfo.isSuccess) {
                        z2 = true;
                    } else if (apexSessionInfo.isStaged) {
                        stagedSession2.setSessionFailed(-128, "Staged session " + stagedSession2.sessionId() + " at boot didn't activate nor fail. Marking it as failed anyway.");
                    } else {
                        Slog.w("StagingManager", "Apex session " + stagedSession2.sessionId() + " is in impossible state");
                        stagedSession2.setSessionFailed(-128, "Impossible state");
                    }
                    z3 = true;
                }
            }
            if (z2 && z3) {
                abortCheckpoint("Found both applied and failed apex sessions", supportsCheckpoint, needsCheckpoint);
            } else if (z3) {
                for (int i4 = 0; i4 < list.size(); i4++) {
                    StagedSession stagedSession3 = list.get(i4);
                    if (!stagedSession3.isSessionFailed()) {
                        stagedSession3.setSessionFailed(-128, "Another apex session failed");
                    }
                }
            } else {
                for (int i5 = 0; i5 < list.size(); i5++) {
                    StagedSession stagedSession4 = list.get(i5);
                    try {
                        resumeSession(stagedSession4, supportsCheckpoint, needsCheckpoint);
                    } catch (PackageManagerException e) {
                        onInstallationFailure(stagedSession4, e, supportsCheckpoint, needsCheckpoint);
                    } catch (Exception e2) {
                        Slog.e("StagingManager", "Staged install failed due to unhandled exception", e2);
                        onInstallationFailure(stagedSession4, new PackageManagerException(-110, "Staged install failed due to unhandled exception: " + e2), supportsCheckpoint, needsCheckpoint);
                    }
                }
                timingsTraceLog.traceEnd();
            }
        } catch (RemoteException e3) {
            throw new IllegalStateException("Failed to get checkpoint status", e3);
        }
    }

    /* renamed from: logFailedApexSessionsIfNecessary */
    public final void lambda$onBootCompletedBroadcastReceived$1() {
        synchronized (this.mFailedPackageNames) {
            if (!this.mFailedPackageNames.isEmpty()) {
                WatchdogRollbackLogger.logApexdRevert(this.mContext, this.mFailedPackageNames, this.mNativeFailureReason);
            }
        }
    }

    public final void markStagedSessionsAsSuccessful() {
        synchronized (this.mSuccessfulStagedSessionIds) {
            for (int i = 0; i < this.mSuccessfulStagedSessionIds.size(); i++) {
                this.mApexManager.markStagedSessionSuccessful(this.mSuccessfulStagedSessionIds.get(i).intValue());
            }
        }
    }

    public void systemReady() {
        new Lifecycle(this.mContext).startService(this);
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.pm.StagingManager.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                StagingManager.this.onBootCompletedBroadcastReceived();
                context.unregisterReceiver(this);
            }
        }, new IntentFilter("android.intent.action.BOOT_COMPLETED"));
        this.mFailureReasonFile.delete();
    }

    @VisibleForTesting
    public void onBootCompletedBroadcastReceived() {
        this.mBootCompleted.complete(null);
        BackgroundThread.getExecutor().execute(new Runnable() { // from class: com.android.server.pm.StagingManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                StagingManager.this.lambda$onBootCompletedBroadcastReceived$1();
            }
        });
    }

    public final StagedSession getStagedSession(int i) {
        StagedSession stagedSession;
        synchronized (this.mStagedSessions) {
            stagedSession = this.mStagedSessions.get(i);
        }
        return stagedSession;
    }

    @VisibleForTesting
    public Map<String, ApexInfo> getStagedApexInfos(StagedSession stagedSession) {
        Preconditions.checkArgument(stagedSession != null, "Session is null");
        Preconditions.checkArgument(true ^ stagedSession.hasParentSessionId(), stagedSession.sessionId() + " session has parent session");
        Preconditions.checkArgument(stagedSession.containsApexSession(), stagedSession.sessionId() + " session does not contain apex");
        if (!stagedSession.isSessionReady() || stagedSession.isDestroyed()) {
            return Collections.emptyMap();
        }
        ApexSessionParams apexSessionParams = new ApexSessionParams();
        apexSessionParams.sessionId = stagedSession.sessionId();
        IntArray intArray = new IntArray();
        if (stagedSession.isMultiPackage()) {
            for (StagedSession stagedSession2 : stagedSession.getChildSessions()) {
                if (stagedSession2.isApexSession()) {
                    intArray.add(stagedSession2.sessionId());
                }
            }
        }
        apexSessionParams.childSessionIds = intArray.toArray();
        ApexInfo[] stagedApexInfos = this.mApexManager.getStagedApexInfos(apexSessionParams);
        ArrayMap arrayMap = new ArrayMap();
        for (ApexInfo apexInfo : stagedApexInfos) {
            arrayMap.put(apexInfo.moduleName, apexInfo);
        }
        return arrayMap;
    }

    public List<String> getStagedApexModuleNames() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mStagedSessions) {
            for (int i = 0; i < this.mStagedSessions.size(); i++) {
                StagedSession valueAt = this.mStagedSessions.valueAt(i);
                if (valueAt.isSessionReady() && !valueAt.isDestroyed() && !valueAt.hasParentSessionId() && valueAt.containsApexSession()) {
                    arrayList.addAll(getStagedApexInfos(valueAt).keySet());
                }
            }
        }
        return arrayList;
    }

    public StagedApexInfo getStagedApexInfo(String str) {
        ApexInfo apexInfo;
        synchronized (this.mStagedSessions) {
            for (int i = 0; i < this.mStagedSessions.size(); i++) {
                StagedSession valueAt = this.mStagedSessions.valueAt(i);
                if (valueAt.isSessionReady() && !valueAt.isDestroyed() && !valueAt.hasParentSessionId() && valueAt.containsApexSession() && (apexInfo = getStagedApexInfos(valueAt).get(str)) != null) {
                    StagedApexInfo stagedApexInfo = new StagedApexInfo();
                    stagedApexInfo.moduleName = apexInfo.moduleName;
                    stagedApexInfo.diskImagePath = apexInfo.modulePath;
                    stagedApexInfo.versionCode = apexInfo.versionCode;
                    stagedApexInfo.versionName = apexInfo.versionName;
                    stagedApexInfo.hasClassPathJars = apexInfo.hasClassPathJars;
                    return stagedApexInfo;
                }
            }
            return null;
        }
    }

    public final void notifyStagedApexObservers() {
        synchronized (this.mStagedApexObservers) {
            for (IStagedApexObserver iStagedApexObserver : this.mStagedApexObservers) {
                ApexStagedEvent apexStagedEvent = new ApexStagedEvent();
                apexStagedEvent.stagedApexModuleNames = (String[]) getStagedApexModuleNames().toArray(new String[0]);
                try {
                    iStagedApexObserver.onApexStaged(apexStagedEvent);
                } catch (RemoteException e) {
                    Slog.w("StagingManager", "Failed to contact the observer " + e.getMessage());
                }
            }
        }
    }
}
