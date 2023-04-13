package com.android.server.p011pm;

import android.apex.ApexInfo;
import android.apex.ApexInfoList;
import android.apex.ApexSessionInfo;
import android.apex.ApexSessionParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.SigningDetails;
import android.content.pm.parsing.result.ParseResult;
import android.content.pm.parsing.result.ParseTypeImpl;
import android.content.rollback.RollbackInfo;
import android.content.rollback.RollbackManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.IntArray;
import android.util.Slog;
import android.util.apk.ApkSignatureVerifier;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.InstallLocationUtils;
import com.android.server.LocalServices;
import com.android.server.SystemConfig;
import com.android.server.p011pm.StagingManager;
import com.android.server.p011pm.parsing.PackageParser2;
import com.android.server.p011pm.parsing.pkg.ParsedPackage;
import com.android.server.rollback.RollbackManagerInternal;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
/* renamed from: com.android.server.pm.PackageSessionVerifier */
/* loaded from: classes2.dex */
public final class PackageSessionVerifier {
    public final ApexManager mApexManager;
    public final Context mContext;
    public final Handler mHandler;
    public final Supplier<PackageParser2> mPackageParserSupplier;
    public final PackageManagerService mPm;
    public final List<StagingManager.StagedSession> mStagedSessions;

    /* renamed from: com.android.server.pm.PackageSessionVerifier$Callback */
    /* loaded from: classes2.dex */
    public interface Callback {
        void onResult(int i, String str);
    }

    public PackageSessionVerifier(Context context, PackageManagerService packageManagerService, ApexManager apexManager, Supplier<PackageParser2> supplier, Looper looper) {
        this.mStagedSessions = new ArrayList();
        this.mContext = context;
        this.mPm = packageManagerService;
        this.mApexManager = apexManager;
        this.mPackageParserSupplier = supplier;
        this.mHandler = new Handler(looper);
    }

    @VisibleForTesting
    public PackageSessionVerifier() {
        this.mStagedSessions = new ArrayList();
        this.mContext = null;
        this.mPm = null;
        this.mApexManager = null;
        this.mPackageParserSupplier = null;
        this.mHandler = null;
    }

    public void verify(final PackageInstallerSession packageInstallerSession, final Callback callback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageSessionVerifier$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PackageSessionVerifier.this.lambda$verify$0(packageInstallerSession, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$verify$0(PackageInstallerSession packageInstallerSession, Callback callback) {
        try {
            storeSession(packageInstallerSession.mStagedSession);
            if (packageInstallerSession.isMultiPackage()) {
                for (PackageInstallerSession packageInstallerSession2 : packageInstallerSession.getChildSessions()) {
                    checkApexUpdateAllowed(packageInstallerSession2);
                    checkRebootlessApex(packageInstallerSession2);
                    checkApexSignature(packageInstallerSession2);
                }
            } else {
                checkApexUpdateAllowed(packageInstallerSession);
                checkRebootlessApex(packageInstallerSession);
                checkApexSignature(packageInstallerSession);
            }
            verifyAPK(packageInstallerSession, callback);
        } catch (PackageManagerException e) {
            packageInstallerSession.setSessionFailed(e.error, PackageManager.installStatusToString(e.error, e.getMessage()));
            callback.onResult(e.error, e.getMessage());
        }
    }

    public final SigningDetails getSigningDetails(PackageInfo packageInfo) throws PackageManagerException {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        String str = applicationInfo.sourceDir;
        ParseResult verify = ApkSignatureVerifier.verify(ParseTypeImpl.forDefaultParsing(), str, ApkSignatureVerifier.getMinimumSignatureSchemeVersionForTargetSdk(applicationInfo.targetSdkVersion));
        if (verify.isError()) {
            throw new PackageManagerException(-22, "Failed to verify APEX package " + str + " : " + verify.getException(), verify.getException());
        }
        return (SigningDetails) verify.getResult();
    }

    public final void checkApexSignature(PackageInstallerSession packageInstallerSession) throws PackageManagerException {
        if (packageInstallerSession.isApexSession()) {
            String packageName = packageInstallerSession.getPackageName();
            PackageInfo packageInfo = this.mPm.snapshotComputer().getPackageInfo(packageInstallerSession.getPackageName(), 1073741824L, 0);
            if (packageInfo == null) {
                throw new PackageManagerException(-23, "Attempting to install new APEX package " + packageName);
            }
            SigningDetails signingDetails = getSigningDetails(packageInfo);
            SigningDetails signingDetails2 = packageInstallerSession.getSigningDetails();
            if (signingDetails2.checkCapability(signingDetails, 1) || signingDetails.checkCapability(signingDetails2, 8)) {
                return;
            }
            throw new PackageManagerException(-22, "APK container signature of APEX package " + packageName + " is not compatible with the one currently installed on device");
        }
    }

    public final void verifyAPK(final PackageInstallerSession packageInstallerSession, final Callback callback) throws PackageManagerException {
        VerifyingSession createVerifyingSession = createVerifyingSession(packageInstallerSession, new IPackageInstallObserver2.Stub() { // from class: com.android.server.pm.PackageSessionVerifier.1
            public void onUserActionRequired(Intent intent) {
                throw new IllegalStateException();
            }

            public void onPackageInstalled(String str, int i, String str2, Bundle bundle) {
                if (packageInstallerSession.isStaged() && i == 1) {
                    PackageSessionVerifier.this.verifyStaged(packageInstallerSession.mStagedSession, callback);
                } else if (i != 1) {
                    packageInstallerSession.setSessionFailed(i, PackageManager.installStatusToString(i, str2));
                    callback.onResult(i, str2);
                } else {
                    packageInstallerSession.setSessionReady();
                    callback.onResult(1, null);
                }
            }
        });
        if (packageInstallerSession.isMultiPackage()) {
            List<PackageInstallerSession> childSessions = packageInstallerSession.getChildSessions();
            ArrayList arrayList = new ArrayList(childSessions.size());
            for (PackageInstallerSession packageInstallerSession2 : childSessions) {
                arrayList.add(createVerifyingSession(packageInstallerSession2, null));
            }
            createVerifyingSession.verifyStage(arrayList);
            return;
        }
        createVerifyingSession.verifyStage();
    }

    public final VerifyingSession createVerifyingSession(PackageInstallerSession packageInstallerSession, IPackageInstallObserver2 iPackageInstallObserver2) {
        UserHandle userHandle;
        if ((packageInstallerSession.params.installFlags & 64) != 0) {
            userHandle = UserHandle.ALL;
        } else {
            userHandle = new UserHandle(packageInstallerSession.userId);
        }
        return new VerifyingSession(userHandle, packageInstallerSession.stageDir, iPackageInstallObserver2, packageInstallerSession.params, packageInstallerSession.getInstallSource(), packageInstallerSession.getInstallerUid(), packageInstallerSession.getSigningDetails(), packageInstallerSession.sessionId, packageInstallerSession.getPackageLite(), packageInstallerSession.getUserActionRequired(), this.mPm);
    }

    public final void verifyStaged(final StagingManager.StagedSession stagedSession, final Callback callback) {
        Slog.d("PackageSessionVerifier", "Starting preRebootVerification for session " + stagedSession.sessionId());
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageSessionVerifier$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PackageSessionVerifier.this.lambda$verifyStaged$1(stagedSession, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$verifyStaged$1(StagingManager.StagedSession stagedSession, Callback callback) {
        try {
            checkActiveSessions();
            checkRollbacks(stagedSession);
            if (stagedSession.isMultiPackage()) {
                for (StagingManager.StagedSession stagedSession2 : stagedSession.getChildSessions()) {
                    checkOverlaps(stagedSession, stagedSession2);
                }
            } else {
                checkOverlaps(stagedSession, stagedSession);
            }
            dispatchVerifyApex(stagedSession, callback);
        } catch (PackageManagerException e) {
            onVerificationFailure(stagedSession, callback, e.error, e.getMessage());
        }
    }

    @VisibleForTesting
    public void storeSession(StagingManager.StagedSession stagedSession) {
        if (stagedSession != null) {
            this.mStagedSessions.add(stagedSession);
        }
    }

    public final void onVerificationSuccess(StagingManager.StagedSession stagedSession, Callback callback) {
        callback.onResult(1, null);
    }

    public final void onVerificationFailure(StagingManager.StagedSession stagedSession, Callback callback, int i, String str) {
        if (!ensureActiveApexSessionIsAborted(stagedSession)) {
            Slog.e("PackageSessionVerifier", "Failed to abort apex session " + stagedSession.sessionId());
        }
        stagedSession.setSessionFailed(i, str);
        callback.onResult(-22, str);
    }

    public final void dispatchVerifyApex(final StagingManager.StagedSession stagedSession, final Callback callback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageSessionVerifier$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                PackageSessionVerifier.this.lambda$dispatchVerifyApex$2(stagedSession, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dispatchVerifyApex$2(StagingManager.StagedSession stagedSession, Callback callback) {
        try {
            verifyApex(stagedSession);
            dispatchEndVerification(stagedSession, callback);
        } catch (PackageManagerException e) {
            onVerificationFailure(stagedSession, callback, e.error, e.getMessage());
        }
    }

    public final void dispatchEndVerification(final StagingManager.StagedSession stagedSession, final Callback callback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.pm.PackageSessionVerifier$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                PackageSessionVerifier.this.lambda$dispatchEndVerification$3(stagedSession, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dispatchEndVerification$3(StagingManager.StagedSession stagedSession, Callback callback) {
        try {
            endVerification(stagedSession);
            onVerificationSuccess(stagedSession, callback);
        } catch (PackageManagerException e) {
            onVerificationFailure(stagedSession, callback, e.error, e.getMessage());
        }
    }

    public final void verifyApex(StagingManager.StagedSession stagedSession) throws PackageManagerException {
        int i = -1;
        if ((stagedSession.sessionParams().installFlags & 262144) != 0) {
            try {
                i = ((RollbackManagerInternal) LocalServices.getService(RollbackManagerInternal.class)).notifyStagedSession(stagedSession.sessionId());
            } catch (RuntimeException e) {
                Slog.e("PackageSessionVerifier", "Failed to notifyStagedSession for session: " + stagedSession.sessionId(), e);
            }
        } else if (isRollback(stagedSession)) {
            i = retrieveRollbackIdForCommitSession(stagedSession.sessionId());
        }
        if (stagedSession.containsApexSession()) {
            submitSessionToApexService(stagedSession, i);
        }
    }

    public final void endVerification(StagingManager.StagedSession stagedSession) throws PackageManagerException {
        try {
            if (InstallLocationUtils.getStorageManager().supportsCheckpoint()) {
                InstallLocationUtils.getStorageManager().startCheckpoint(2);
            }
            Slog.d("PackageSessionVerifier", "Marking session " + stagedSession.sessionId() + " as ready");
            stagedSession.setSessionReady();
            if (stagedSession.isSessionReady() && stagedSession.containsApexSession()) {
                this.mApexManager.markStagedSessionReady(stagedSession.sessionId());
            }
        } catch (Exception e) {
            Slog.e("PackageSessionVerifier", "Failed to get hold of StorageManager", e);
            throw new PackageManagerException(-110, "Failed to get hold of StorageManager");
        }
    }

    public final void submitSessionToApexService(StagingManager.StagedSession stagedSession, int i) throws PackageManagerException {
        ApexInfo[] apexInfoArr;
        IntArray intArray = new IntArray();
        if (stagedSession.isMultiPackage()) {
            for (StagingManager.StagedSession stagedSession2 : stagedSession.getChildSessions()) {
                if (stagedSession2.isApexSession()) {
                    intArray.add(stagedSession2.sessionId());
                }
            }
        }
        ApexSessionParams apexSessionParams = new ApexSessionParams();
        apexSessionParams.sessionId = stagedSession.sessionId();
        apexSessionParams.childSessionIds = intArray.toArray();
        if (stagedSession.sessionParams().installReason == 5) {
            apexSessionParams.isRollback = true;
            apexSessionParams.rollbackId = i;
        } else if (i != -1) {
            apexSessionParams.hasRollbackEnabled = true;
            apexSessionParams.rollbackId = i;
        }
        ApexInfoList submitStagedSession = this.mApexManager.submitStagedSession(apexSessionParams);
        ArrayList arrayList = new ArrayList();
        for (ApexInfo apexInfo : submitStagedSession.apexInfos) {
            try {
                PackageParser2 packageParser2 = this.mPackageParserSupplier.get();
                ParsedPackage parsePackage = packageParser2.parsePackage(new File(apexInfo.modulePath), 0, false);
                packageParser2.close();
                arrayList.add(parsePackage.getPackageName());
            } catch (PackageManagerException e) {
                throw new PackageManagerException(-22, "Failed to parse APEX package " + apexInfo.modulePath + " : " + e, e);
            }
        }
        Slog.d("PackageSessionVerifier", "Session " + stagedSession.sessionId() + " has following APEX packages: " + arrayList);
    }

    public final int retrieveRollbackIdForCommitSession(int i) throws PackageManagerException {
        List recentlyCommittedRollbacks = ((RollbackManager) this.mContext.getSystemService(RollbackManager.class)).getRecentlyCommittedRollbacks();
        int size = recentlyCommittedRollbacks.size();
        for (int i2 = 0; i2 < size; i2++) {
            RollbackInfo rollbackInfo = (RollbackInfo) recentlyCommittedRollbacks.get(i2);
            if (rollbackInfo.getCommittedSessionId() == i) {
                return rollbackInfo.getRollbackId();
            }
        }
        throw new PackageManagerException(-22, "Could not find rollback id for commit session: " + i);
    }

    public static boolean isRollback(StagingManager.StagedSession stagedSession) {
        return stagedSession.sessionParams().installReason == 5;
    }

    public static boolean isApexSessionFinalized(ApexSessionInfo apexSessionInfo) {
        return apexSessionInfo.isUnknown || apexSessionInfo.isActivationFailed || apexSessionInfo.isSuccess || apexSessionInfo.isReverted;
    }

    public final boolean ensureActiveApexSessionIsAborted(StagingManager.StagedSession stagedSession) {
        int sessionId;
        ApexSessionInfo stagedSessionInfo;
        if (!stagedSession.containsApexSession() || (stagedSessionInfo = this.mApexManager.getStagedSessionInfo((sessionId = stagedSession.sessionId()))) == null || isApexSessionFinalized(stagedSessionInfo)) {
            return true;
        }
        return this.mApexManager.abortStagedSession(sessionId);
    }

    public final boolean isApexUpdateAllowed(String str, String str2) {
        if (this.mPm.getModuleInfo(str, 0) != null) {
            String modulesInstallerPackageName = SystemConfig.getInstance().getModulesInstallerPackageName();
            if (modulesInstallerPackageName == null) {
                Slog.w("PackageSessionVerifier", "No modules installer defined");
                return false;
            }
            return modulesInstallerPackageName.equals(str2);
        }
        String str3 = SystemConfig.getInstance().getAllowedVendorApexes().get(str);
        if (str3 == null) {
            Slog.w("PackageSessionVerifier", str + " is not allowed to be updated");
            return false;
        }
        return str3.equals(str2);
    }

    public final void checkApexUpdateAllowed(PackageInstallerSession packageInstallerSession) throws PackageManagerException {
        if (packageInstallerSession.isApexSession() && (packageInstallerSession.params.installFlags & 8388608) == 0) {
            String packageName = packageInstallerSession.getPackageName();
            String str = packageInstallerSession.getInstallSource().mInstallerPackageName;
            if (isApexUpdateAllowed(packageName, str)) {
                return;
            }
            throw new PackageManagerException(-22, "Update of APEX package " + packageName + " is not allowed for " + str);
        }
    }

    @VisibleForTesting
    public void checkRebootlessApex(PackageInstallerSession packageInstallerSession) throws PackageManagerException {
        if (packageInstallerSession.isStaged() || !packageInstallerSession.isApexSession()) {
            return;
        }
        final String packageName = packageInstallerSession.getPackageName();
        if (packageName == null) {
            throw new PackageManagerException(-22, "Invalid session " + packageInstallerSession.sessionId + " with package name null");
        }
        for (StagingManager.StagedSession stagedSession : this.mStagedSessions) {
            if (!stagedSession.isDestroyed() && !stagedSession.isInTerminalState() && stagedSession.sessionContains(new Predicate() { // from class: com.android.server.pm.PackageSessionVerifier$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$checkRebootlessApex$4;
                    lambda$checkRebootlessApex$4 = PackageSessionVerifier.lambda$checkRebootlessApex$4(packageName, (StagingManager.StagedSession) obj);
                    return lambda$checkRebootlessApex$4;
                }
            })) {
                throw new PackageManagerException(-22, "Staged session " + stagedSession.sessionId() + " already contains " + packageName);
            }
        }
    }

    public static /* synthetic */ boolean lambda$checkRebootlessApex$4(String str, StagingManager.StagedSession stagedSession) {
        return str.equals(stagedSession.getPackageName());
    }

    public final void checkActiveSessions() throws PackageManagerException {
        try {
            checkActiveSessions(InstallLocationUtils.getStorageManager().supportsCheckpoint());
        } catch (RemoteException e) {
            throw new PackageManagerException(-110, "Can't query fs-checkpoint status : " + e);
        }
    }

    @VisibleForTesting
    public void checkActiveSessions(boolean z) throws PackageManagerException {
        int i = 0;
        for (StagingManager.StagedSession stagedSession : this.mStagedSessions) {
            if (!stagedSession.isDestroyed() && !stagedSession.isInTerminalState()) {
                i++;
            }
        }
        if (!z && i > 1) {
            throw new PackageManagerException(-119, "Cannot stage multiple sessions without checkpoint support");
        }
    }

    @VisibleForTesting
    public void checkRollbacks(StagingManager.StagedSession stagedSession) throws PackageManagerException {
        if (stagedSession.isDestroyed() || stagedSession.isInTerminalState()) {
            return;
        }
        for (StagingManager.StagedSession stagedSession2 : this.mStagedSessions) {
            if (!stagedSession2.isDestroyed() && !stagedSession2.isInTerminalState()) {
                if (isRollback(stagedSession) && !isRollback(stagedSession2)) {
                    if (!ensureActiveApexSessionIsAborted(stagedSession2)) {
                        Slog.e("PackageSessionVerifier", "Failed to abort apex session " + stagedSession2.sessionId());
                    }
                    stagedSession2.setSessionFailed(-119, "Session was failed by rollback session: " + stagedSession.sessionId());
                    Slog.i("PackageSessionVerifier", "Session " + stagedSession2.sessionId() + " is marked failed due to rollback session: " + stagedSession.sessionId());
                } else if (!isRollback(stagedSession) && isRollback(stagedSession2)) {
                    throw new PackageManagerException(-119, "Session was failed by rollback session: " + stagedSession2.sessionId());
                }
            }
        }
    }

    @VisibleForTesting
    public void checkOverlaps(StagingManager.StagedSession stagedSession, StagingManager.StagedSession stagedSession2) throws PackageManagerException {
        if (stagedSession.isDestroyed() || stagedSession.isInTerminalState()) {
            return;
        }
        final String packageName = stagedSession2.getPackageName();
        if (packageName == null) {
            throw new PackageManagerException(-22, "Cannot stage session " + stagedSession2.sessionId() + " with package name null");
        }
        for (StagingManager.StagedSession stagedSession3 : this.mStagedSessions) {
            if (!stagedSession3.isDestroyed() && !stagedSession3.isInTerminalState() && stagedSession3.sessionId() != stagedSession.sessionId() && stagedSession3.sessionContains(new Predicate() { // from class: com.android.server.pm.PackageSessionVerifier$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$checkOverlaps$5;
                    lambda$checkOverlaps$5 = PackageSessionVerifier.lambda$checkOverlaps$5(packageName, (StagingManager.StagedSession) obj);
                    return lambda$checkOverlaps$5;
                }
            })) {
                if (stagedSession3.getCommittedMillis() < stagedSession.getCommittedMillis()) {
                    throw new PackageManagerException(-119, "Package: " + packageName + " in session: " + stagedSession2.sessionId() + " has been staged already by session: " + stagedSession3.sessionId());
                }
                stagedSession3.setSessionFailed(-119, "Package: " + packageName + " in session: " + stagedSession3.sessionId() + " has been staged already by session: " + stagedSession2.sessionId());
            }
        }
    }

    public static /* synthetic */ boolean lambda$checkOverlaps$5(String str, StagingManager.StagedSession stagedSession) {
        return str.equals(stagedSession.getPackageName());
    }
}
