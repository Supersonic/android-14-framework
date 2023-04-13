package com.android.server.contentcapture;

import android.app.ActivityManagerInternal;
import android.app.assist.ActivityId;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.ContentCaptureOptions;
import android.content.Context;
import android.content.pm.ActivityPresentationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.contentcapture.ActivityEvent;
import android.service.contentcapture.ContentCaptureService;
import android.service.contentcapture.ContentCaptureServiceInfo;
import android.service.contentcapture.FlushMetrics;
import android.service.contentcapture.IContentCaptureServiceCallback;
import android.service.contentcapture.IDataShareCallback;
import android.service.contentcapture.SnapshotData;
import android.service.voice.VoiceInteractionManagerInternal;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.contentcapture.ContentCaptureCondition;
import android.view.contentcapture.DataRemovalRequest;
import android.view.contentcapture.DataShareRequest;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.infra.AbstractRemoteService;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.CollectionUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.infra.AbstractPerUserSystemService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class ContentCapturePerUserService extends AbstractPerUserSystemService<ContentCapturePerUserService, ContentCaptureManagerService> implements AbstractRemoteService.VultureCallback {
    public static final String TAG = ContentCapturePerUserService.class.getSimpleName();
    @GuardedBy({"mLock"})
    public final ArrayMap<String, ArraySet<ContentCaptureCondition>> mConditionsByPkg;
    @GuardedBy({"mLock"})
    public ContentCaptureServiceInfo mInfo;
    @GuardedBy({"mLock"})
    public RemoteContentCaptureService mRemoteService;
    public final ContentCaptureServiceRemoteCallback mRemoteServiceCallback;
    @GuardedBy({"mLock"})
    public final SparseArray<ContentCaptureServerSession> mSessions;
    @GuardedBy({"mLock"})
    public boolean mZombie;

    public ContentCapturePerUserService(ContentCaptureManagerService contentCaptureManagerService, Object obj, boolean z, int i) {
        super(contentCaptureManagerService, obj, i);
        this.mSessions = new SparseArray<>();
        this.mRemoteServiceCallback = new ContentCaptureServiceRemoteCallback();
        this.mConditionsByPkg = new ArrayMap<>();
        updateRemoteServiceLocked(z);
    }

    public final void updateRemoteServiceLocked(boolean z) {
        if (((ContentCaptureManagerService) this.mMaster).verbose) {
            String str = TAG;
            Slog.v(str, "updateRemoteService(disabled=" + z + ")");
        }
        if (this.mRemoteService != null) {
            if (((ContentCaptureManagerService) this.mMaster).debug) {
                Slog.d(TAG, "updateRemoteService(): destroying old remote service");
            }
            this.mRemoteService.destroy();
            this.mRemoteService = null;
            resetContentCaptureWhitelistLocked();
        }
        ComponentName updateServiceInfoLocked = updateServiceInfoLocked();
        if (updateServiceInfoLocked == null) {
            if (((ContentCaptureManagerService) this.mMaster).debug) {
                Slog.d(TAG, "updateRemoteService(): no service component name");
            }
        } else if (z) {
        } else {
            if (((ContentCaptureManagerService) this.mMaster).debug) {
                String str2 = TAG;
                Slog.d(str2, "updateRemoteService(): creating new remote service for " + updateServiceInfoLocked);
            }
            Context context = ((ContentCaptureManagerService) this.mMaster).getContext();
            ContentCaptureServiceRemoteCallback contentCaptureServiceRemoteCallback = this.mRemoteServiceCallback;
            int i = this.mUserId;
            boolean isBindInstantServiceAllowed = ((ContentCaptureManagerService) this.mMaster).isBindInstantServiceAllowed();
            M m = this.mMaster;
            this.mRemoteService = new RemoteContentCaptureService(context, "android.service.contentcapture.ContentCaptureService", updateServiceInfoLocked, contentCaptureServiceRemoteCallback, i, this, isBindInstantServiceAllowed, ((ContentCaptureManagerService) m).verbose, ((ContentCaptureManagerService) m).mDevCfgIdleUnbindTimeoutMs);
        }
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    public ServiceInfo newServiceInfoLocked(ComponentName componentName) throws PackageManager.NameNotFoundException {
        ContentCaptureServiceInfo contentCaptureServiceInfo = new ContentCaptureServiceInfo(getContext(), componentName, isTemporaryServiceSetLocked(), this.mUserId);
        this.mInfo = contentCaptureServiceInfo;
        return contentCaptureServiceInfo.getServiceInfo();
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    @GuardedBy({"mLock"})
    public boolean updateLocked(boolean z) {
        boolean updateLocked = super.updateLocked(z);
        if (updateLocked) {
            for (int i = 0; i < this.mSessions.size(); i++) {
                this.mSessions.valueAt(i).setContentCaptureEnabledLocked(!z);
            }
        }
        destroyLocked();
        updateRemoteServiceLocked(z);
        return updateLocked;
    }

    public void onServiceDied(RemoteContentCaptureService remoteContentCaptureService) {
        String str = TAG;
        Slog.w(str, "remote service died: " + remoteContentCaptureService);
        synchronized (this.mLock) {
            this.mZombie = true;
            ContentCaptureMetricsLogger.writeServiceEvent(16, getServiceComponentName());
            EventLog.writeEvent(53200, Integer.valueOf(this.mUserId), 0, 0);
        }
    }

    public void onConnected() {
        synchronized (this.mLock) {
            if (this.mZombie) {
                if (this.mRemoteService == null) {
                    Slog.w(TAG, "Cannot ressurect sessions because remote service is null");
                } else {
                    this.mZombie = false;
                    resurrectSessionsLocked();
                }
            }
        }
    }

    public final void resurrectSessionsLocked() {
        int size = this.mSessions.size();
        if (((ContentCaptureManagerService) this.mMaster).debug) {
            String str = TAG;
            Slog.d(str, "Ressurrecting remote service (" + this.mRemoteService + ") on " + size + " sessions");
        }
        for (int i = 0; i < size; i++) {
            this.mSessions.valueAt(i).resurrectLocked();
        }
    }

    public void onPackageUpdatingLocked() {
        int size = this.mSessions.size();
        if (((ContentCaptureManagerService) this.mMaster).debug) {
            String str = TAG;
            Slog.d(str, "Pausing " + size + " sessions while package is updating");
        }
        for (int i = 0; i < size; i++) {
            this.mSessions.valueAt(i).pauseLocked();
        }
    }

    public void onPackageUpdatedLocked() {
        updateRemoteServiceLocked(!isEnabledLocked());
        resurrectSessionsLocked();
    }

    @GuardedBy({"mLock"})
    public void startSessionLocked(IBinder iBinder, IBinder iBinder2, ActivityPresentationInfo activityPresentationInfo, int i, int i2, int i3, IResultReceiver iResultReceiver) {
        if (activityPresentationInfo == null) {
            Slog.w(TAG, "basic activity info is null");
            ContentCaptureService.setClientState(iResultReceiver, 260, (IBinder) null);
            return;
        }
        int i4 = activityPresentationInfo.taskId;
        int i5 = activityPresentationInfo.displayId;
        ComponentName componentName = activityPresentationInfo.componentName;
        boolean z = ((ContentCaptureManagerService) this.mMaster).mGlobalContentCaptureOptions.isWhitelisted(this.mUserId, componentName) || ((ContentCaptureManagerService) this.mMaster).mGlobalContentCaptureOptions.isWhitelisted(this.mUserId, componentName.getPackageName());
        ComponentName serviceComponentName = getServiceComponentName();
        boolean isEnabledLocked = isEnabledLocked();
        if (((ContentCaptureManagerService) this.mMaster).mRequestsHistory != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("id=");
            sb.append(i);
            sb.append(" uid=");
            sb.append(i2);
            sb.append(" a=");
            sb.append(ComponentName.flattenToShortString(componentName));
            sb.append(" t=");
            sb.append(i4);
            sb.append(" d=");
            sb.append(i5);
            sb.append(" s=");
            sb.append(ComponentName.flattenToShortString(serviceComponentName));
            sb.append(" u=");
            sb.append(this.mUserId);
            sb.append(" f=");
            sb.append(i3);
            sb.append(isEnabledLocked ? "" : " (disabled)");
            sb.append(" w=");
            sb.append(z);
            ((ContentCaptureManagerService) this.mMaster).mRequestsHistory.log(sb.toString());
        }
        if (!isEnabledLocked) {
            ContentCaptureService.setClientState(iResultReceiver, 20, (IBinder) null);
            ContentCaptureMetricsLogger.writeSessionEvent(i, 3, 20, serviceComponentName, false);
        } else if (serviceComponentName == null) {
            if (((ContentCaptureManagerService) this.mMaster).debug) {
                String str = TAG;
                Slog.d(str, "startSession(" + iBinder + "): hold your horses");
            }
        } else if (!z) {
            if (((ContentCaptureManagerService) this.mMaster).debug) {
                String str2 = TAG;
                Slog.d(str2, "startSession(" + componentName + "): package or component not whitelisted");
            }
            ContentCaptureService.setClientState(iResultReceiver, (int) FrameworkStatsLog.ANR_LATENCY_REPORTED, (IBinder) null);
            ContentCaptureMetricsLogger.writeSessionEvent(i, 3, FrameworkStatsLog.ANR_LATENCY_REPORTED, serviceComponentName, false);
        } else {
            ContentCaptureServerSession contentCaptureServerSession = this.mSessions.get(i);
            if (contentCaptureServerSession != null) {
                String str3 = TAG;
                Slog.w(str3, "startSession(id=" + contentCaptureServerSession + ", token=" + iBinder + ": ignoring because it already exists for " + contentCaptureServerSession.mActivityToken);
                ContentCaptureService.setClientState(iResultReceiver, 12, (IBinder) null);
                ContentCaptureMetricsLogger.writeSessionEvent(i, 3, 12, serviceComponentName, false);
                return;
            }
            if (this.mRemoteService == null) {
                updateRemoteServiceLocked(false);
            }
            RemoteContentCaptureService remoteContentCaptureService = this.mRemoteService;
            if (remoteContentCaptureService == null) {
                String str4 = TAG;
                Slog.w(str4, "startSession(id=" + contentCaptureServerSession + ", token=" + iBinder + ": ignoring because service is not set");
                ContentCaptureService.setClientState(iResultReceiver, 20, (IBinder) null);
                ContentCaptureMetricsLogger.writeSessionEvent(i, 3, 20, serviceComponentName, false);
                return;
            }
            remoteContentCaptureService.ensureBoundLocked();
            ContentCaptureServerSession contentCaptureServerSession2 = new ContentCaptureServerSession(this.mLock, iBinder, new ActivityId(i4, iBinder2), this, componentName, iResultReceiver, i4, i5, i, i2, i3);
            if (((ContentCaptureManagerService) this.mMaster).verbose) {
                String str5 = TAG;
                Slog.v(str5, "startSession(): new session for " + ComponentName.flattenToShortString(componentName) + " and id " + i);
            }
            this.mSessions.put(i, contentCaptureServerSession2);
            contentCaptureServerSession2.notifySessionStartedLocked(iResultReceiver);
        }
    }

    @GuardedBy({"mLock"})
    public void finishSessionLocked(int i) {
        if (isEnabledLocked()) {
            ContentCaptureServerSession contentCaptureServerSession = this.mSessions.get(i);
            if (contentCaptureServerSession == null) {
                if (((ContentCaptureManagerService) this.mMaster).debug) {
                    String str = TAG;
                    Slog.d(str, "finishSession(): no session with id" + i);
                    return;
                }
                return;
            }
            if (((ContentCaptureManagerService) this.mMaster).verbose) {
                String str2 = TAG;
                Slog.v(str2, "finishSession(): id=" + i);
            }
            contentCaptureServerSession.removeSelfLocked(true);
        }
    }

    @GuardedBy({"mLock"})
    public void removeDataLocked(DataRemovalRequest dataRemovalRequest) {
        if (isEnabledLocked()) {
            assertCallerLocked(dataRemovalRequest.getPackageName());
            this.mRemoteService.onDataRemovalRequest(dataRemovalRequest);
        }
    }

    @GuardedBy({"mLock"})
    public void onDataSharedLocked(DataShareRequest dataShareRequest, IDataShareCallback.Stub stub) {
        if (isEnabledLocked()) {
            assertCallerLocked(dataShareRequest.getPackageName());
            this.mRemoteService.onDataShareRequest(dataShareRequest, stub);
        }
    }

    @GuardedBy({"mLock"})
    public ComponentName getServiceSettingsActivityLocked() {
        String settingsActivity;
        ContentCaptureServiceInfo contentCaptureServiceInfo = this.mInfo;
        if (contentCaptureServiceInfo == null || (settingsActivity = contentCaptureServiceInfo.getSettingsActivity()) == null) {
            return null;
        }
        return new ComponentName(this.mInfo.getServiceInfo().packageName, settingsActivity);
    }

    @GuardedBy({"mLock"})
    public final void assertCallerLocked(String str) {
        String str2;
        PackageManager packageManager = getContext().getPackageManager();
        int callingUid = Binder.getCallingUid();
        try {
            int packageUidAsUser = packageManager.getPackageUidAsUser(str, UserHandle.getCallingUserId());
            if (callingUid == packageUidAsUser || ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).hasRunningActivity(callingUid, str)) {
                return;
            }
            VoiceInteractionManagerInternal.HotwordDetectionServiceIdentity hotwordDetectionServiceIdentity = ((VoiceInteractionManagerInternal) LocalServices.getService(VoiceInteractionManagerInternal.class)).getHotwordDetectionServiceIdentity();
            if (hotwordDetectionServiceIdentity != null && callingUid == hotwordDetectionServiceIdentity.getIsolatedUid() && packageUidAsUser == hotwordDetectionServiceIdentity.getOwnerUid()) {
                return;
            }
            String[] packagesForUid = packageManager.getPackagesForUid(callingUid);
            if (packagesForUid != null) {
                str2 = packagesForUid[0];
            } else {
                str2 = "uid-" + callingUid;
            }
            Slog.w(TAG, "App (package=" + str2 + ", UID=" + callingUid + ") passed package (" + str + ") owned by UID " + packageUidAsUser);
            throw new SecurityException("Invalid package: " + str);
        } catch (PackageManager.NameNotFoundException unused) {
            throw new SecurityException("Could not verify UID for " + str);
        }
    }

    @GuardedBy({"mLock"})
    public boolean sendActivityAssistDataLocked(IBinder iBinder, Bundle bundle) {
        int sessionId = getSessionId(iBinder);
        SnapshotData snapshotData = new SnapshotData(bundle.getBundle("data"), (AssistStructure) bundle.getParcelable("structure", AssistStructure.class), (AssistContent) bundle.getParcelable("content", AssistContent.class));
        if (sessionId != 0) {
            this.mSessions.get(sessionId).sendActivitySnapshotLocked(snapshotData);
            return true;
        }
        RemoteContentCaptureService remoteContentCaptureService = this.mRemoteService;
        if (remoteContentCaptureService != null) {
            remoteContentCaptureService.onActivitySnapshotRequest(0, snapshotData);
            String str = TAG;
            Slog.d(str, "Notified activity assist data for activity: " + iBinder + " without a session Id");
            return true;
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public void removeSessionLocked(int i) {
        this.mSessions.remove(i);
    }

    @GuardedBy({"mLock"})
    public boolean isContentCaptureServiceForUserLocked(int i) {
        return i == getServiceUidLocked();
    }

    @GuardedBy({"mLock"})
    public void destroyLocked() {
        if (((ContentCaptureManagerService) this.mMaster).debug) {
            Slog.d(TAG, "destroyLocked()");
        }
        RemoteContentCaptureService remoteContentCaptureService = this.mRemoteService;
        if (remoteContentCaptureService != null) {
            remoteContentCaptureService.destroy();
        }
        destroySessionsLocked();
    }

    @GuardedBy({"mLock"})
    public void destroySessionsLocked() {
        int size = this.mSessions.size();
        for (int i = 0; i < size; i++) {
            this.mSessions.valueAt(i).destroyLocked(true);
        }
        this.mSessions.clear();
    }

    @GuardedBy({"mLock"})
    public void listSessionsLocked(ArrayList<String> arrayList) {
        int size = this.mSessions.size();
        for (int i = 0; i < size; i++) {
            arrayList.add(this.mSessions.valueAt(i).toShortString());
        }
    }

    @GuardedBy({"mLock"})
    public ArraySet<ContentCaptureCondition> getContentCaptureConditionsLocked(String str) {
        return this.mConditionsByPkg.get(str);
    }

    public ArraySet<String> getContentCaptureAllowlist() {
        ArraySet<String> whitelistedPackages;
        synchronized (this.mLock) {
            whitelistedPackages = ((ContentCaptureManagerService) this.mMaster).mGlobalContentCaptureOptions.getWhitelistedPackages(this.mUserId);
        }
        return whitelistedPackages;
    }

    @GuardedBy({"mLock"})
    public void onActivityEventLocked(ActivityId activityId, ComponentName componentName, int i) {
        if (this.mRemoteService == null) {
            if (((ContentCaptureManagerService) this.mMaster).debug) {
                Slog.d(this.mTag, "onActivityEvent(): no remote service");
                return;
            }
            return;
        }
        ActivityEvent activityEvent = new ActivityEvent(activityId, componentName, i);
        if (((ContentCaptureManagerService) this.mMaster).verbose) {
            String str = this.mTag;
            Slog.v(str, "onActivityEvent(): " + activityEvent);
        }
        this.mRemoteService.onActivityLifecycleEvent(activityEvent);
    }

    @Override // com.android.server.infra.AbstractPerUserSystemService
    public void dumpLocked(String str, PrintWriter printWriter) {
        super.dumpLocked(str, printWriter);
        String str2 = str + "  ";
        printWriter.print(str);
        printWriter.print("Service Info: ");
        if (this.mInfo == null) {
            printWriter.println("N/A");
        } else {
            printWriter.println();
            this.mInfo.dump(str2, printWriter);
        }
        printWriter.print(str);
        printWriter.print("Zombie: ");
        printWriter.println(this.mZombie);
        if (this.mRemoteService != null) {
            printWriter.print(str);
            printWriter.println("remote service:");
            this.mRemoteService.dump(str2, printWriter);
        }
        if (this.mSessions.size() == 0) {
            printWriter.print(str);
            printWriter.println("no sessions");
            return;
        }
        int size = this.mSessions.size();
        printWriter.print(str);
        printWriter.print("number sessions: ");
        printWriter.println(size);
        for (int i = 0; i < size; i++) {
            printWriter.print(str);
            printWriter.print("#");
            printWriter.println(i);
            this.mSessions.valueAt(i).dumpLocked(str2, printWriter);
            printWriter.println();
        }
    }

    @GuardedBy({"mLock"})
    public final int getSessionId(IBinder iBinder) {
        for (int i = 0; i < this.mSessions.size(); i++) {
            if (this.mSessions.valueAt(i).isActivitySession(iBinder)) {
                return this.mSessions.keyAt(i);
            }
        }
        return 0;
    }

    @GuardedBy({"mLock"})
    public final void resetContentCaptureWhitelistLocked() {
        if (((ContentCaptureManagerService) this.mMaster).verbose) {
            Slog.v(TAG, "resetting content capture whitelist");
        }
        ((ContentCaptureManagerService) this.mMaster).mGlobalContentCaptureOptions.resetWhitelist(this.mUserId);
    }

    /* loaded from: classes.dex */
    public final class ContentCaptureServiceRemoteCallback extends IContentCaptureServiceCallback.Stub {
        public ContentCaptureServiceRemoteCallback() {
        }

        public void setContentCaptureWhitelist(List<String> list, List<ComponentName> list2) {
            ContentCaptureServerSession contentCaptureServerSession;
            if (((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).verbose) {
                String str = ContentCapturePerUserService.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("setContentCaptureWhitelist(");
                sb.append(list == null ? "null_packages" : list.size() + " packages");
                sb.append(", ");
                sb.append(list2 == null ? "null_activities" : list2.size() + " activities");
                sb.append(") for user ");
                sb.append(ContentCapturePerUserService.this.mUserId);
                Slog.v(str, sb.toString());
            }
            ArraySet<String> whitelistedPackages = ((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).mGlobalContentCaptureOptions.getWhitelistedPackages(ContentCapturePerUserService.this.mUserId);
            EventLog.writeEvent(53202, Integer.valueOf(ContentCapturePerUserService.this.mUserId), Integer.valueOf(CollectionUtils.size(whitelistedPackages)));
            ((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).mGlobalContentCaptureOptions.setWhitelist(ContentCapturePerUserService.this.mUserId, list, list2);
            EventLog.writeEvent(53201, Integer.valueOf(ContentCapturePerUserService.this.mUserId), Integer.valueOf(CollectionUtils.size(list)), Integer.valueOf(CollectionUtils.size(list2)));
            ContentCaptureMetricsLogger.writeSetWhitelistEvent(ContentCapturePerUserService.this.getServiceComponentName(), list, list2);
            updateContentCaptureOptions(whitelistedPackages);
            int size = ContentCapturePerUserService.this.mSessions.size();
            if (size <= 0) {
                return;
            }
            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray(size);
            for (int i = 0; i < size; i++) {
                if (!((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).mGlobalContentCaptureOptions.isWhitelisted(ContentCapturePerUserService.this.mUserId, ((ContentCaptureServerSession) ContentCapturePerUserService.this.mSessions.valueAt(i)).appComponentName)) {
                    int keyAt = ContentCapturePerUserService.this.mSessions.keyAt(i);
                    if (((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).debug) {
                        Slog.d(ContentCapturePerUserService.TAG, "marking session " + keyAt + " (" + contentCaptureServerSession.appComponentName + ") for un-whitelisting");
                    }
                    sparseBooleanArray.append(keyAt, true);
                }
            }
            int size2 = sparseBooleanArray.size();
            if (size2 <= 0) {
                return;
            }
            synchronized (ContentCapturePerUserService.this.mLock) {
                for (int i2 = 0; i2 < size2; i2++) {
                    int keyAt2 = sparseBooleanArray.keyAt(i2);
                    if (((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).debug) {
                        Slog.d(ContentCapturePerUserService.TAG, "un-whitelisting " + keyAt2);
                    }
                    ((ContentCaptureServerSession) ContentCapturePerUserService.this.mSessions.get(keyAt2)).setContentCaptureEnabledLocked(false);
                }
            }
        }

        public void setContentCaptureConditions(String str, List<ContentCaptureCondition> list) {
            String str2;
            if (((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).verbose) {
                String str3 = ContentCapturePerUserService.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("setContentCaptureConditions(");
                sb.append(str);
                sb.append("): ");
                if (list == null) {
                    str2 = "null";
                } else {
                    str2 = list.size() + " conditions";
                }
                sb.append(str2);
                Slog.v(str3, sb.toString());
            }
            synchronized (ContentCapturePerUserService.this.mLock) {
                if (list == null) {
                    ContentCapturePerUserService.this.mConditionsByPkg.remove(str);
                } else {
                    ContentCapturePerUserService.this.mConditionsByPkg.put(str, new ArraySet(list));
                }
            }
        }

        public void disableSelf() {
            if (((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).verbose) {
                Slog.v(ContentCapturePerUserService.TAG, "disableSelf()");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                Settings.Secure.putStringForUser(ContentCapturePerUserService.this.getContext().getContentResolver(), "content_capture_enabled", "0", ContentCapturePerUserService.this.mUserId);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                ContentCaptureMetricsLogger.writeServiceEvent(4, ContentCapturePerUserService.this.getServiceComponentName());
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }

        public void writeSessionFlush(int i, ComponentName componentName, FlushMetrics flushMetrics, ContentCaptureOptions contentCaptureOptions, int i2) {
            ContentCaptureMetricsLogger.writeSessionFlush(i, ContentCapturePerUserService.this.getServiceComponentName(), flushMetrics, contentCaptureOptions, i2);
        }

        public final void updateContentCaptureOptions(ArraySet<String> arraySet) {
            ArraySet whitelistedPackages = ((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).mGlobalContentCaptureOptions.getWhitelistedPackages(ContentCapturePerUserService.this.mUserId);
            EventLog.writeEvent(53202, Integer.valueOf(ContentCapturePerUserService.this.mUserId), Integer.valueOf(CollectionUtils.size(whitelistedPackages)));
            if (arraySet != null && whitelistedPackages != null) {
                whitelistedPackages.removeAll((ArraySet) arraySet);
            }
            int size = CollectionUtils.size(whitelistedPackages);
            EventLog.writeEvent(53203, Integer.valueOf(ContentCapturePerUserService.this.mUserId), Integer.valueOf(size));
            for (int i = 0; i < size; i++) {
                String str = (String) whitelistedPackages.valueAt(i);
                ((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).updateOptions(str, ((ContentCaptureManagerService) ContentCapturePerUserService.this.mMaster).mGlobalContentCaptureOptions.getOptions(ContentCapturePerUserService.this.mUserId, str));
            }
        }
    }
}
