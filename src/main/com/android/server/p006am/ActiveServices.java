package com.android.server.p006am;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.BackgroundStartPrivileges;
import android.app.ForegroundServiceDelegationOptions;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.ForegroundServiceTypePolicy;
import android.app.IApplicationThread;
import android.app.IForegroundServiceObserver;
import android.app.IServiceConnection;
import android.app.InvalidForegroundServiceTypeException;
import android.app.MissingForegroundServiceTypeException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteServiceException;
import android.app.ServiceStartArgs;
import android.app.StartForegroundCalledOnStoppedServiceException;
import android.app.admin.DevicePolicyEventLogger;
import android.app.compat.CompatChanges;
import android.appwidget.AppWidgetManagerInternal;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.CompatibilityInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerExemptionManager;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.os.UserManager;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Pair;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.webkit.WebViewZygote;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.os.SomeArgs;
import com.android.internal.os.TimeoutRecord;
import com.android.internal.os.TransferPipe;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.AppStateTracker;
import com.android.server.LocalServices;
import com.android.server.appop.AppOpsService;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p006am.ComponentAliasResolver;
import com.android.server.p006am.ServiceRecord;
import com.android.server.p014wm.ActivityServiceConnectionsHolder;
import com.android.server.uri.NeededUriGrants;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
/* renamed from: com.android.server.am.ActiveServices */
/* loaded from: classes.dex */
public final class ActiveServices {
    public final ActivityManagerService mAm;
    public AppStateTracker mAppStateTracker;
    public AppWidgetManagerInternal mAppWidgetManagerInternal;
    public final ForegroundServiceTypeLoggerModule mFGSLogger;
    public String mLastAnrDump;
    public final int mMaxStartingBackground;
    public static final AtomicReference<Pair<Integer, Integer>> sNumForegroundServices = new AtomicReference<>(new Pair(0, 0));
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final SparseArray<ServiceMap> mServiceMap = new SparseArray<>();
    public final ArrayMap<IBinder, ArrayList<ConnectionRecord>> mServiceConnections = new ArrayMap<>();
    public final ArrayList<ServiceRecord> mPendingServices = new ArrayList<>();
    public final ArrayList<ServiceRecord> mRestartingServices = new ArrayList<>();
    public final ArrayList<ServiceRecord> mDestroyingServices = new ArrayList<>();
    public final ArrayList<ServiceRecord> mPendingFgsNotifications = new ArrayList<>();
    public final ArrayMap<ForegroundServiceDelegation, ServiceRecord> mFgsDelegations = new ArrayMap<>();
    public boolean mFgsDeferralRateLimited = true;
    public final SparseLongArray mFgsDeferralEligible = new SparseLongArray();
    public final RemoteCallbackList<IForegroundServiceObserver> mFgsObservers = new RemoteCallbackList<>();
    public ArrayMap<ServiceRecord, ArrayList<Runnable>> mPendingBringups = new ArrayMap<>();
    public ArrayList<ServiceRecord> mTmpCollectionResults = null;
    @GuardedBy({"mAm"})
    public final SparseArray<AppOpCallback> mFgsAppOpCallbacks = new SparseArray<>();
    @GuardedBy({"mAm"})
    public final ArraySet<String> mRestartBackoffDisabledPackages = new ArraySet<>();
    public boolean mScreenOn = true;
    public ArraySet<String> mAllowListWhileInUsePermissionInFgs = new ArraySet<>();
    public final Runnable mLastAnrDumpClearer = new Runnable() { // from class: com.android.server.am.ActiveServices.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (ActiveServices.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActiveServices.this.mLastAnrDump = null;
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }
    };
    public final Runnable mPostDeferredFGSNotifications = new Runnable() { // from class: com.android.server.am.ActiveServices.5
        @Override // java.lang.Runnable
        public void run() {
            long uptimeMillis = SystemClock.uptimeMillis();
            synchronized (ActiveServices.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    for (int size = ActiveServices.this.mPendingFgsNotifications.size() - 1; size >= 0; size--) {
                        ServiceRecord serviceRecord = ActiveServices.this.mPendingFgsNotifications.get(size);
                        if (serviceRecord.fgDisplayTime <= uptimeMillis) {
                            ActiveServices.this.mPendingFgsNotifications.remove(size);
                            if (serviceRecord.isForeground && serviceRecord.app != null) {
                                serviceRecord.postNotification();
                                serviceRecord.mFgsNotificationShown = true;
                            }
                        }
                    }
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }
    };

    /* renamed from: com.android.server.am.ActiveServices$ActiveForegroundApp */
    /* loaded from: classes.dex */
    public static final class ActiveForegroundApp {
        public boolean mAppOnTop;
        public long mEndTime;
        public long mHideTime;
        public CharSequence mLabel;
        public int mNumActive;
        public String mPackageName;
        public boolean mShownWhileScreenOn;
        public boolean mShownWhileTop;
        public long mStartTime;
        public long mStartVisibleTime;
        public int mUid;
    }

    public static String fgsStopReasonToString(int i) {
        return i != 1 ? i != 2 ? "UNKNOWN" : "STOP_SERVICE" : "STOP_FOREGROUND";
    }

    /* renamed from: com.android.server.am.ActiveServices$BackgroundRestrictedListener */
    /* loaded from: classes.dex */
    public class BackgroundRestrictedListener implements AppStateTracker.BackgroundRestrictedAppListener {
        public BackgroundRestrictedListener() {
        }

        public void updateBackgroundRestrictedForUidPackage(int i, String str, boolean z) {
            synchronized (ActiveServices.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    if (!ActiveServices.this.isForegroundServiceAllowedInBackgroundRestricted(i, str)) {
                        ActiveServices.this.stopAllForegroundServicesLocked(i, str);
                    }
                    ActiveServices.this.mAm.mProcessList.updateBackgroundRestrictedForUidPackageLocked(i, str, z);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }
    }

    public void stopAllForegroundServicesLocked(int i, String str) {
        ServiceMap serviceMapLocked = getServiceMapLocked(UserHandle.getUserId(i));
        int size = serviceMapLocked.mServicesByInstanceName.size();
        ArrayList arrayList = new ArrayList(size);
        for (int i2 = 0; i2 < size; i2++) {
            ServiceRecord valueAt = serviceMapLocked.mServicesByInstanceName.valueAt(i2);
            ServiceInfo serviceInfo = valueAt.serviceInfo;
            if ((i == serviceInfo.applicationInfo.uid || str.equals(serviceInfo.packageName)) && valueAt.isForeground) {
                arrayList.add(valueAt);
            }
        }
        int size2 = arrayList.size();
        for (int i3 = 0; i3 < size2; i3++) {
            setServiceForegroundInnerLocked((ServiceRecord) arrayList.get(i3), 0, null, 0, 0);
        }
    }

    /* renamed from: com.android.server.am.ActiveServices$ServiceMap */
    /* loaded from: classes.dex */
    public final class ServiceMap extends Handler {
        public final ArrayMap<String, ActiveForegroundApp> mActiveForegroundApps;
        public boolean mActiveForegroundAppsChanged;
        public final ArrayList<ServiceRecord> mDelayedStartList;
        public final ArrayList<String> mPendingRemoveForegroundApps;
        public final ArrayMap<ComponentName, ServiceRecord> mServicesByInstanceName;
        public final ArrayMap<Intent.FilterComparison, ServiceRecord> mServicesByIntent;
        public final ArrayList<ServiceRecord> mStartingBackground;
        public final int mUserId;

        public ServiceMap(Looper looper, int i) {
            super(looper);
            this.mServicesByInstanceName = new ArrayMap<>();
            this.mServicesByIntent = new ArrayMap<>();
            this.mDelayedStartList = new ArrayList<>();
            this.mStartingBackground = new ArrayList<>();
            this.mActiveForegroundApps = new ArrayMap<>();
            this.mPendingRemoveForegroundApps = new ArrayList<>();
            this.mUserId = i;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                synchronized (ActiveServices.this.mAm) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        rescheduleDelayedStartsLocked();
                    } finally {
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } else if (i == 2) {
                ActiveServices.this.updateForegroundApps(this);
            } else if (i != 3) {
            } else {
                synchronized (ActiveServices.this.mAm) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        rescheduleDelayedStartsLocked();
                    } finally {
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }

        public void ensureNotStartingBackgroundLocked(ServiceRecord serviceRecord) {
            if (this.mStartingBackground.remove(serviceRecord)) {
                removeMessages(3);
                sendMessage(obtainMessage(3));
            }
            this.mDelayedStartList.remove(serviceRecord);
        }

        public void rescheduleDelayedStartsLocked() {
            removeMessages(1);
            long uptimeMillis = SystemClock.uptimeMillis();
            int size = this.mStartingBackground.size();
            int i = 0;
            while (i < size) {
                ServiceRecord serviceRecord = this.mStartingBackground.get(i);
                if (serviceRecord.startingBgTimeout <= uptimeMillis) {
                    Slog.i("ActivityManager", "Waited long enough for: " + serviceRecord);
                    this.mStartingBackground.remove(i);
                    size += -1;
                    i += -1;
                }
                i++;
            }
            while (this.mDelayedStartList.size() > 0 && this.mStartingBackground.size() < ActiveServices.this.mMaxStartingBackground) {
                ServiceRecord remove = this.mDelayedStartList.remove(0);
                remove.delayed = false;
                if (remove.pendingStarts.size() <= 0) {
                    Slog.wtf("ActivityManager", "**** NO PENDING STARTS! " + remove + " startReq=" + remove.startRequested + " delayedStop=" + remove.delayedStop);
                } else {
                    try {
                        ServiceRecord.StartItem startItem = remove.pendingStarts.get(0);
                        ActiveServices.this.startServiceInnerLocked(this, startItem.intent, remove, false, true, startItem.callingId, startItem.mCallingProcessName, remove.startRequested, startItem.mCallingPackageName);
                    } catch (TransactionTooLargeException unused) {
                    }
                }
            }
            if (this.mStartingBackground.size() > 0) {
                long j = this.mStartingBackground.get(0).startingBgTimeout;
                if (j > uptimeMillis) {
                    uptimeMillis = j;
                }
                sendMessageAtTime(obtainMessage(1), uptimeMillis);
            }
            int size2 = this.mStartingBackground.size();
            ActiveServices activeServices = ActiveServices.this;
            if (size2 < activeServices.mMaxStartingBackground) {
                activeServices.mAm.backgroundServicesFinishedLocked(this.mUserId);
            }
        }
    }

    public ActiveServices(ActivityManagerService activityManagerService) {
        int i;
        int i2 = 1;
        this.mAm = activityManagerService;
        try {
            i = Integer.parseInt(SystemProperties.get("ro.config.max_starting_bg", "0"));
        } catch (RuntimeException unused) {
            i = 0;
        }
        if (i > 0) {
            i2 = i;
        } else if (!ActivityManager.isLowRamDeviceStatic()) {
            i2 = 8;
        }
        this.mMaxStartingBackground = i2;
        ServiceManager.getService("platform_compat");
        this.mFGSLogger = new ForegroundServiceTypeLoggerModule();
    }

    public void systemServicesReady() {
        getAppStateTracker().addBackgroundRestrictedAppListener(new BackgroundRestrictedListener());
        this.mAppWidgetManagerInternal = (AppWidgetManagerInternal) LocalServices.getService(AppWidgetManagerInternal.class);
        setAllowListWhileInUsePermissionInFgs();
        initSystemExemptedFgsTypePermission();
    }

    public final AppStateTracker getAppStateTracker() {
        if (this.mAppStateTracker == null) {
            this.mAppStateTracker = (AppStateTracker) LocalServices.getService(AppStateTracker.class);
        }
        return this.mAppStateTracker;
    }

    public final void setAllowListWhileInUsePermissionInFgs() {
        String attentionServicePackageName = this.mAm.mContext.getPackageManager().getAttentionServicePackageName();
        if (!TextUtils.isEmpty(attentionServicePackageName)) {
            this.mAllowListWhileInUsePermissionInFgs.add(attentionServicePackageName);
        }
        String systemCaptionsServicePackageName = this.mAm.mContext.getPackageManager().getSystemCaptionsServicePackageName();
        if (TextUtils.isEmpty(systemCaptionsServicePackageName)) {
            return;
        }
        this.mAllowListWhileInUsePermissionInFgs.add(systemCaptionsServicePackageName);
    }

    public ServiceRecord getServiceByNameLocked(ComponentName componentName, int i) {
        return getServiceMapLocked(i).mServicesByInstanceName.get(componentName);
    }

    public boolean hasBackgroundServicesLocked(int i) {
        ServiceMap serviceMap = this.mServiceMap.get(i);
        return serviceMap != null && serviceMap.mStartingBackground.size() >= this.mMaxStartingBackground;
    }

    public boolean hasForegroundServiceNotificationLocked(String str, int i, String str2) {
        ServiceMap serviceMap = this.mServiceMap.get(i);
        if (serviceMap != null) {
            for (int i2 = 0; i2 < serviceMap.mServicesByInstanceName.size(); i2++) {
                ServiceRecord valueAt = serviceMap.mServicesByInstanceName.valueAt(i2);
                if (valueAt.appInfo.packageName.equals(str) && valueAt.isForeground && Objects.equals(valueAt.foregroundNoti.getChannelId(), str2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final ServiceMap getServiceMapLocked(int i) {
        ServiceMap serviceMap = this.mServiceMap.get(i);
        if (serviceMap == null) {
            ServiceMap serviceMap2 = new ServiceMap(this.mAm.mHandler.getLooper(), i);
            this.mServiceMap.put(i, serviceMap2);
            return serviceMap2;
        }
        return serviceMap;
    }

    public ArrayMap<ComponentName, ServiceRecord> getServicesLocked(int i) {
        return getServiceMapLocked(i).mServicesByInstanceName;
    }

    public final boolean appRestrictedAnyInBackground(int i, String str) {
        AppStateTracker appStateTracker = getAppStateTracker();
        if (appStateTracker != null) {
            return appStateTracker.isAppBackgroundRestricted(i, str);
        }
        return false;
    }

    public static String getProcessNameForService(ServiceInfo serviceInfo, ComponentName componentName, String str, String str2, boolean z, boolean z2) {
        if (z) {
            return str2;
        }
        if ((serviceInfo.flags & 2) == 0) {
            return serviceInfo.processName;
        }
        if (z2) {
            return str + ":ishared:" + str2;
        }
        return serviceInfo.processName + XmlUtils.STRING_ARRAY_SEPARATOR + componentName.getClassName();
    }

    public ComponentName startServiceLocked(IApplicationThread iApplicationThread, Intent intent, String str, int i, int i2, boolean z, String str2, String str3, int i3, boolean z2, int i4, String str4, String str5) throws TransactionTooLargeException {
        return startServiceLocked(iApplicationThread, intent, str, i, i2, z, str2, str3, i3, BackgroundStartPrivileges.NONE, z2, i4, str4, str5);
    }

    public ComponentName startServiceLocked(IApplicationThread iApplicationThread, Intent intent, String str, int i, int i2, boolean z, String str2, String str3, int i3, BackgroundStartPrivileges backgroundStartPrivileges) throws TransactionTooLargeException {
        return startServiceLocked(iApplicationThread, intent, str, i, i2, z, str2, str3, i3, backgroundStartPrivileges, false, -1, null, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x0106  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0170  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x017d  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x01cd  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0207  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x0276  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ComponentName startServiceLocked(IApplicationThread iApplicationThread, Intent intent, String str, int i, int i2, boolean z, String str2, String str3, int i3, BackgroundStartPrivileges backgroundStartPrivileges, boolean z2, int i4, String str4, String str5) throws TransactionTooLargeException {
        boolean z3;
        boolean z4;
        Intent intent2;
        String str6;
        String str7;
        String str8;
        boolean z5;
        boolean z6;
        String str9;
        ServiceLookupResult serviceLookupResult;
        int i5;
        int i6;
        int appStartModeLOSP;
        int i7;
        boolean z7;
        int checkOpNoThrow;
        boolean z8 = false;
        if (iApplicationThread != null) {
            ProcessRecord recordForAppLOSP = this.mAm.getRecordForAppLOSP(iApplicationThread);
            if (recordForAppLOSP == null) {
                throw new SecurityException("Unable to find app for caller " + iApplicationThread + " (pid=" + i + ") when starting service " + intent);
            }
            z3 = recordForAppLOSP.mState.getSetSchedGroup() != 0;
        } else {
            z3 = true;
        }
        ServiceLookupResult retrieveServiceLocked = retrieveServiceLocked(intent, str5, z2, i4, str4, str, str2, i, i2, i3, true, z3, false, false, null, false);
        if (retrieveServiceLocked == null) {
            return null;
        }
        ServiceRecord serviceRecord = retrieveServiceLocked.record;
        if (serviceRecord == null) {
            String str10 = retrieveServiceLocked.permission;
            if (str10 == null) {
                str10 = "private to package";
            }
            return new ComponentName("!", str10);
        }
        setFgsRestrictionLocked(str2, i, i2, intent, serviceRecord, i3, backgroundStartPrivileges, false);
        if (!this.mAm.mUserController.exists(serviceRecord.userId)) {
            Slog.w("ActivityManager", "Trying to start service with non-existent user! " + serviceRecord.userId);
            return null;
        }
        int i8 = z2 ? i4 : serviceRecord.appInfo.uid;
        String str11 = z2 ? str4 : serviceRecord.packageName;
        int i9 = serviceRecord.appInfo.targetSdkVersion;
        if (z2) {
            try {
                try {
                    i9 = AppGlobals.getPackageManager().getApplicationInfo(str11, 1024L, i3).targetSdkVersion;
                } catch (RemoteException unused) {
                }
            } catch (RemoteException unused2) {
            }
            int i10 = i9;
            z4 = !(this.mAm.isUidActiveLOSP(i8) ^ true) && appRestrictedAnyInBackground(i8, str11);
            if (z) {
                logFgsBackgroundStart(serviceRecord);
                if (serviceRecord.mAllowStartForeground == -1 && isBgFgsRestrictionEnabled(serviceRecord)) {
                    String str12 = "startForegroundService() not allowed due to mAllowStartForeground false: service " + serviceRecord.shortInstanceName;
                    Slog.w("ActivityManager", str12);
                    showFgsBgRestrictedNotificationLocked(serviceRecord);
                    logFGSStateChangeLocked(serviceRecord, 3, 0, 0, 0);
                    if (CompatChanges.isChangeEnabled(174041399L, i2)) {
                        throw new ForegroundServiceStartNotAllowedException(str12);
                    }
                    return null;
                }
            }
            if (z && (checkOpNoThrow = this.mAm.getAppOpsManager().checkOpNoThrow(76, i8, str11)) != 0) {
                if (checkOpNoThrow != 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("startForegroundService not allowed due to app op: service ");
                    intent2 = intent;
                    sb.append(intent2);
                    sb.append(" to ");
                    str6 = " to ";
                    sb.append(serviceRecord.shortInstanceName);
                    sb.append(" from pid=");
                    sb.append(i);
                    sb.append(" uid=");
                    sb.append(i2);
                    sb.append(" pkg=");
                    str7 = "!";
                    str8 = str2;
                    sb.append(str8);
                    Slog.w("ActivityManager", sb.toString());
                    z5 = false;
                    z6 = true;
                    if (z4 && (serviceRecord.startRequested || z5)) {
                        str9 = "?";
                        z7 = z5;
                        i6 = i10;
                        serviceLookupResult = retrieveServiceLocked;
                        i7 = i;
                        i5 = i2;
                    } else {
                        String str13 = str6;
                        str9 = "?";
                        serviceLookupResult = retrieveServiceLocked;
                        i5 = i2;
                        boolean z9 = z5;
                        i6 = i10;
                        appStartModeLOSP = this.mAm.getAppStartModeLOSP(i8, str11, i10, i, false, false, z4);
                        if (appStartModeLOSP != 0) {
                            Slog.w("ActivityManager", "Background start not allowed: service " + intent2 + str13 + serviceRecord.shortInstanceName + " from pid=" + i + " uid=" + i5 + " pkg=" + str8 + " startFg?=" + z9);
                            if (appStartModeLOSP == 1 || z6) {
                                return null;
                            }
                            if (z4 && z9) {
                                return null;
                            }
                            return new ComponentName(str9, "app is in background uid " + this.mAm.mProcessList.getUidRecordLOSP(i8));
                        }
                        i7 = i;
                        z7 = z9;
                    }
                    if (i6 >= 26 || !z7) {
                        z8 = z7;
                    }
                    String str14 = str9;
                    String str15 = str7;
                    if (!deferServiceBringupIfFrozenLocked(serviceRecord, intent, str2, str3, i2, i, z8, z3, i3, backgroundStartPrivileges, false, null) && requestStartTargetPermissionsReviewIfNeededLocked(serviceRecord, str2, str3, i2, intent, z3, i3, false, null)) {
                        ComponentName startServiceInnerLocked = startServiceInnerLocked(serviceRecord, intent, i2, i, getCallingProcessNameLocked(i5, i, str2), z8, z3, backgroundStartPrivileges, str2);
                        ServiceLookupResult serviceLookupResult2 = serviceLookupResult;
                        return (serviceLookupResult2.aliasComponent == null || startServiceInnerLocked.getPackageName().startsWith(str15) || startServiceInnerLocked.getPackageName().startsWith(str14)) ? startServiceInnerLocked : serviceLookupResult2.aliasComponent;
                    }
                    return null;
                } else if (checkOpNoThrow != 3) {
                    return new ComponentName("!!", "foreground not allowed as per app op");
                }
            }
            intent2 = intent;
            str6 = " to ";
            str7 = "!";
            str8 = str2;
            z5 = z;
            z6 = false;
            if (z4) {
            }
            String str132 = str6;
            str9 = "?";
            serviceLookupResult = retrieveServiceLocked;
            i5 = i2;
            boolean z92 = z5;
            i6 = i10;
            appStartModeLOSP = this.mAm.getAppStartModeLOSP(i8, str11, i10, i, false, false, z4);
            if (appStartModeLOSP != 0) {
            }
        }
        int i102 = i9;
        if (this.mAm.isUidActiveLOSP(i8) ^ true) {
        }
        if (z) {
        }
        if (z) {
            if (checkOpNoThrow != 1) {
            }
        }
        intent2 = intent;
        str6 = " to ";
        str7 = "!";
        str8 = str2;
        z5 = z;
        z6 = false;
        if (z4) {
        }
        String str1322 = str6;
        str9 = "?";
        serviceLookupResult = retrieveServiceLocked;
        i5 = i2;
        boolean z922 = z5;
        i6 = i102;
        appStartModeLOSP = this.mAm.getAppStartModeLOSP(i8, str11, i102, i, false, false, z4);
        if (appStartModeLOSP != 0) {
        }
    }

    public final String getCallingProcessNameLocked(int i, int i2, String str) {
        synchronized (this.mAm.mPidsSelfLocked) {
            ProcessRecord processRecord = this.mAm.mPidsSelfLocked.get(i2);
            if (processRecord != null) {
                str = processRecord.processName;
            }
        }
        return str;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x00ce, code lost:
        if (r1.mState.getCurProcState() >= 10) goto L29;
     */
    /* JADX WARN: Removed duplicated region for block: B:41:0x010b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final ComponentName startServiceInnerLocked(ServiceRecord serviceRecord, Intent intent, int i, int i2, String str, boolean z, boolean z2, BackgroundStartPrivileges backgroundStartPrivileges, String str2) throws TransactionTooLargeException {
        boolean z3;
        NeededUriGrants checkGrantUriPermissionFromIntent = this.mAm.mUgmInternal.checkGrantUriPermissionFromIntent(intent, i, serviceRecord.packageName, serviceRecord.userId);
        unscheduleServiceRestartLocked(serviceRecord, i, false);
        boolean z4 = serviceRecord.startRequested;
        serviceRecord.lastActivity = SystemClock.uptimeMillis();
        serviceRecord.startRequested = true;
        serviceRecord.delayedStop = false;
        serviceRecord.fgRequired = z;
        serviceRecord.pendingStarts.add(new ServiceRecord.StartItem(serviceRecord, false, serviceRecord.makeNextStartId(), intent, checkGrantUriPermissionFromIntent, i, str, str2));
        if (z) {
            synchronized (this.mAm.mProcessStats.mLock) {
                ServiceState tracker = serviceRecord.getTracker();
                if (tracker != null) {
                    tracker.setForeground(true, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
            }
            AppOpsService appOpsService = this.mAm.mAppOpsService;
            appOpsService.startOperation(AppOpsManager.getToken(appOpsService), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, null, true, false, null, false, 0, -1);
        }
        ServiceMap serviceMapLocked = getServiceMapLocked(serviceRecord.userId);
        if (!z2 && !z && serviceRecord.app == null && this.mAm.mUserController.hasStartedUserState(serviceRecord.userId)) {
            ProcessRecord processRecordLocked = this.mAm.getProcessRecordLocked(serviceRecord.processName, serviceRecord.appInfo.uid);
            if (processRecordLocked == null || processRecordLocked.mState.getCurProcState() > 11) {
                if (serviceRecord.delayed) {
                    return serviceRecord.name;
                }
                if (serviceMapLocked.mStartingBackground.size() >= this.mMaxStartingBackground) {
                    Slog.i("ActivityManager", "Delaying start of: " + serviceRecord);
                    serviceMapLocked.mDelayedStartList.add(serviceRecord);
                    serviceRecord.delayed = true;
                    return serviceRecord.name;
                }
            }
            z3 = true;
            if (backgroundStartPrivileges.allowsAny()) {
                serviceRecord.allowBgActivityStartsOnServiceStart(backgroundStartPrivileges);
            }
            return startServiceInnerLocked(serviceMapLocked, intent, serviceRecord, z2, z3, i, str, z4, str2);
        }
        z3 = false;
        if (backgroundStartPrivileges.allowsAny()) {
        }
        return startServiceInnerLocked(serviceMapLocked, intent, serviceRecord, z2, z3, i, str, z4, str2);
    }

    public final boolean requestStartTargetPermissionsReviewIfNeededLocked(final ServiceRecord serviceRecord, String str, String str2, int i, final Intent intent, final boolean z, final int i2, boolean z2, final IServiceConnection iServiceConnection) {
        if (this.mAm.getPackageManagerInternal().isPermissionsReviewRequired(serviceRecord.packageName, serviceRecord.userId)) {
            if (!z) {
                StringBuilder sb = new StringBuilder();
                sb.append("u");
                sb.append(serviceRecord.userId);
                sb.append(z2 ? " Binding" : " Starting");
                sb.append(" a service in package");
                sb.append(serviceRecord.packageName);
                sb.append(" requires a permissions review");
                Slog.w("ActivityManager", sb.toString());
                return false;
            }
            final Intent intent2 = new Intent("android.intent.action.REVIEW_PERMISSIONS");
            intent2.addFlags(411041792);
            intent2.putExtra("android.intent.extra.PACKAGE_NAME", serviceRecord.packageName);
            if (z2) {
                intent2.putExtra("android.intent.extra.REMOTE_CALLBACK", (Parcelable) new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.server.am.ActiveServices.2
                    public void onResult(Bundle bundle) {
                        ActivityManagerService activityManagerService;
                        synchronized (ActiveServices.this.mAm) {
                            try {
                                ActivityManagerService.boostPriorityForLockedSection();
                                long clearCallingIdentity = Binder.clearCallingIdentity();
                                if (ActiveServices.this.mPendingServices.contains(serviceRecord)) {
                                    PackageManagerInternal packageManagerInternal = ActiveServices.this.mAm.getPackageManagerInternal();
                                    ServiceRecord serviceRecord2 = serviceRecord;
                                    if (!packageManagerInternal.isPermissionsReviewRequired(serviceRecord2.packageName, serviceRecord2.userId)) {
                                        try {
                                            ActiveServices.this.bringUpServiceLocked(serviceRecord, intent.getFlags(), z, false, false, false, true);
                                            activityManagerService = ActiveServices.this.mAm;
                                        } catch (RemoteException unused) {
                                            activityManagerService = ActiveServices.this.mAm;
                                        } catch (Throwable th) {
                                            ActiveServices.this.mAm.updateOomAdjPendingTargetsLocked(6);
                                            throw th;
                                        }
                                        activityManagerService.updateOomAdjPendingTargetsLocked(6);
                                    } else {
                                        ActiveServices.this.unbindServiceLocked(iServiceConnection);
                                    }
                                    Binder.restoreCallingIdentity(clearCallingIdentity);
                                    ActivityManagerService.resetPriorityAfterLockedSection();
                                    return;
                                }
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                ActivityManagerService.resetPriorityAfterLockedSection();
                            } catch (Throwable th2) {
                                ActivityManagerService.resetPriorityAfterLockedSection();
                                throw th2;
                            }
                        }
                    }
                }));
            } else {
                ActivityManagerService activityManagerService = this.mAm;
                intent2.putExtra("android.intent.extra.INTENT", new IntentSender(activityManagerService.mPendingIntentController.getIntentSender(4, str, str2, i, i2, null, null, 0, new Intent[]{intent}, new String[]{intent.resolveType(activityManagerService.mContext.getContentResolver())}, 1409286144, null)));
            }
            this.mAm.mHandler.post(new Runnable() { // from class: com.android.server.am.ActiveServices.3
                @Override // java.lang.Runnable
                public void run() {
                    ActiveServices.this.mAm.mContext.startActivityAsUser(intent2, new UserHandle(i2));
                }
            });
            return false;
        }
        return true;
    }

    @GuardedBy({"mAm"})
    public final boolean deferServiceBringupIfFrozenLocked(final ServiceRecord serviceRecord, final Intent intent, final String str, final String str2, final int i, final int i2, final boolean z, final boolean z2, final int i3, final BackgroundStartPrivileges backgroundStartPrivileges, final boolean z3, final IServiceConnection iServiceConnection) {
        if (this.mAm.getPackageManagerInternal().isPackageFrozen(serviceRecord.packageName, i, serviceRecord.userId)) {
            ArrayList<Runnable> arrayList = this.mPendingBringups.get(serviceRecord);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.mPendingBringups.put(serviceRecord, arrayList);
            }
            final String callingProcessNameLocked = getCallingProcessNameLocked(i, i2, str);
            arrayList.add(new Runnable() { // from class: com.android.server.am.ActiveServices.4
                @Override // java.lang.Runnable
                public void run() {
                    ActivityManagerService activityManagerService;
                    synchronized (ActiveServices.this.mAm) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            if (!ActiveServices.this.mPendingBringups.containsKey(serviceRecord)) {
                                ActivityManagerService.resetPriorityAfterLockedSection();
                            } else if (!ActiveServices.this.requestStartTargetPermissionsReviewIfNeededLocked(serviceRecord, str, str2, i, intent, z2, i3, z3, iServiceConnection)) {
                                ActivityManagerService.resetPriorityAfterLockedSection();
                            } else {
                                if (z3) {
                                    try {
                                        ActiveServices.this.bringUpServiceLocked(serviceRecord, intent.getFlags(), z2, false, false, false, true);
                                        activityManagerService = ActiveServices.this.mAm;
                                    } catch (TransactionTooLargeException unused) {
                                        activityManagerService = ActiveServices.this.mAm;
                                    } catch (Throwable th) {
                                        ActiveServices.this.mAm.updateOomAdjPendingTargetsLocked(6);
                                        throw th;
                                    }
                                    activityManagerService.updateOomAdjPendingTargetsLocked(6);
                                } else {
                                    try {
                                        ActiveServices.this.startServiceInnerLocked(serviceRecord, intent, i, i2, callingProcessNameLocked, z, z2, backgroundStartPrivileges, str);
                                    } catch (TransactionTooLargeException unused2) {
                                    }
                                }
                                ActivityManagerService.resetPriorityAfterLockedSection();
                            }
                        } catch (Throwable th2) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th2;
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }

    @GuardedBy({"mAm"})
    public void schedulePendingServiceStartLocked(String str, int i) {
        int size = this.mPendingBringups.size();
        while (true) {
            for (int i2 = size - 1; i2 >= 0 && size > 0; i2--) {
                ServiceRecord keyAt = this.mPendingBringups.keyAt(i2);
                if (keyAt.userId == i && TextUtils.equals(keyAt.packageName, str)) {
                    ArrayList<Runnable> valueAt = this.mPendingBringups.valueAt(i2);
                    if (valueAt != null) {
                        for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                            valueAt.get(size2).run();
                        }
                        valueAt.clear();
                    }
                    int size3 = this.mPendingBringups.size();
                    this.mPendingBringups.remove(keyAt);
                    if (size != size3) {
                        break;
                    }
                    size = this.mPendingBringups.size();
                }
            }
            return;
            size = this.mPendingBringups.size();
        }
    }

    public ComponentName startServiceInnerLocked(ServiceMap serviceMap, Intent intent, ServiceRecord serviceRecord, boolean z, boolean z2, int i, String str, boolean z3, String str2) throws TransactionTooLargeException {
        String str3;
        int i2;
        int i3;
        synchronized (this.mAm.mProcessStats.mLock) {
            ServiceState tracker = serviceRecord.getTracker();
            if (tracker != null) {
                tracker.setStarted(true, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
            }
        }
        serviceRecord.callStart = false;
        int i4 = serviceRecord.appInfo.uid;
        String packageName = serviceRecord.name.getPackageName();
        String className = serviceRecord.name.getClassName();
        FrameworkStatsLog.write(99, i4, packageName, className, 1);
        this.mAm.mBatteryStatsService.noteServiceStartRunning(i4, packageName, className);
        String bringUpServiceLocked = bringUpServiceLocked(serviceRecord, intent.getFlags(), z, false, false, false, true);
        this.mAm.updateOomAdjPendingTargetsLocked(6);
        if (bringUpServiceLocked != null) {
            return new ComponentName("!!", bringUpServiceLocked);
        }
        int i5 = (serviceRecord.appInfo.flags & 2097152) != 0 ? 2 : 1;
        String action = intent.getAction();
        ProcessRecord processRecord = serviceRecord.app;
        if (processRecord == null || processRecord.getThread() == null) {
            str3 = str;
            i2 = 3;
            i3 = i;
        } else if (z3 || !serviceRecord.getConnections().isEmpty()) {
            i3 = i;
            i2 = 2;
            str3 = str;
        } else {
            i3 = i;
            str3 = str;
            i2 = 1;
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.SERVICE_REQUEST_EVENT_REPORTED, i4, i, action, 1, false, i2, getShortProcessNameForStats(i3, str3), getShortServiceNameForStats(serviceRecord), i5, packageName, str2);
        if (serviceRecord.startRequested && z2) {
            boolean z4 = serviceMap.mStartingBackground.size() == 0;
            serviceMap.mStartingBackground.add(serviceRecord);
            serviceRecord.startingBgTimeout = SystemClock.uptimeMillis() + this.mAm.mConstants.BG_START_TIMEOUT;
            if (z4) {
                serviceMap.rescheduleDelayedStartsLocked();
            }
        } else if (z || serviceRecord.fgRequired) {
            serviceMap.ensureNotStartingBackgroundLocked(serviceRecord);
        }
        return serviceRecord.name;
    }

    public final String getShortProcessNameForStats(int i, String str) {
        String[] packagesForUid = this.mAm.mContext.getPackageManager().getPackagesForUid(i);
        if (packagesForUid != null && packagesForUid.length == 1) {
            if (TextUtils.equals(packagesForUid[0], str)) {
                return null;
            }
            if (str != null && str.startsWith(packagesForUid[0])) {
                return str.substring(packagesForUid[0].length());
            }
        }
        return str;
    }

    public final String getShortServiceNameForStats(ServiceRecord serviceRecord) {
        ComponentName componentName = serviceRecord.getComponentName();
        if (componentName != null) {
            return componentName.getShortClassName();
        }
        return null;
    }

    public final void stopServiceLocked(ServiceRecord serviceRecord, boolean z) {
        try {
            Trace.traceBegin(64L, "stopServiceLocked()");
            if (serviceRecord.delayed) {
                serviceRecord.delayedStop = true;
                return;
            }
            maybeStopShortFgsTimeoutLocked(serviceRecord);
            int i = serviceRecord.appInfo.uid;
            String packageName = serviceRecord.name.getPackageName();
            String className = serviceRecord.name.getClassName();
            FrameworkStatsLog.write(99, i, packageName, className, 2);
            this.mAm.mBatteryStatsService.noteServiceStopRunning(i, packageName, className);
            serviceRecord.startRequested = false;
            if (serviceRecord.tracker != null) {
                synchronized (this.mAm.mProcessStats.mLock) {
                    serviceRecord.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
            }
            serviceRecord.callStart = false;
            bringDownServiceIfNeededLocked(serviceRecord, false, false, z, "stopService");
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public int stopServiceLocked(IApplicationThread iApplicationThread, Intent intent, String str, int i, boolean z, int i2, String str2, String str3) {
        ProcessRecord recordForAppLOSP = this.mAm.getRecordForAppLOSP(iApplicationThread);
        if (iApplicationThread != null && recordForAppLOSP == null) {
            throw new SecurityException("Unable to find app for caller " + iApplicationThread + " (pid=" + Binder.getCallingPid() + ") when stopping service " + intent);
        }
        ServiceLookupResult retrieveServiceLocked = retrieveServiceLocked(intent, str3, z, i2, str2, str, null, Binder.getCallingPid(), Binder.getCallingUid(), i, false, false, false, false, null, false);
        if (retrieveServiceLocked != null) {
            if (retrieveServiceLocked.record != null) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    stopServiceLocked(retrieveServiceLocked.record, false);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return 1;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    throw th;
                }
            }
            return -1;
        }
        return 0;
    }

    public void stopInBackgroundLocked(int i) {
        ServiceMap serviceMap = this.mServiceMap.get(UserHandle.getUserId(i));
        if (serviceMap != null) {
            ArrayList arrayList = null;
            for (int size = serviceMap.mServicesByInstanceName.size() - 1; size >= 0; size--) {
                ServiceRecord valueAt = serviceMap.mServicesByInstanceName.valueAt(size);
                ApplicationInfo applicationInfo = valueAt.appInfo;
                int i2 = applicationInfo.uid;
                if (i2 == i && valueAt.startRequested && this.mAm.getAppStartModeLOSP(i2, valueAt.packageName, applicationInfo.targetSdkVersion, -1, false, false, false) != 0) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    String str = valueAt.shortInstanceName;
                    EventLogTags.writeAmStopIdleService(valueAt.appInfo.uid, str);
                    StringBuilder sb = new StringBuilder(64);
                    sb.append("Stopping service due to app idle: ");
                    UserHandle.formatUid(sb, valueAt.appInfo.uid);
                    sb.append(" ");
                    TimeUtils.formatDuration(valueAt.createRealTime - SystemClock.elapsedRealtime(), sb);
                    sb.append(" ");
                    sb.append(str);
                    Slog.w("ActivityManager", sb.toString());
                    arrayList.add(valueAt);
                    if (appRestrictedAnyInBackground(valueAt.appInfo.uid, valueAt.packageName)) {
                        cancelForegroundNotificationLocked(valueAt);
                    }
                }
            }
            if (arrayList != null) {
                int size2 = arrayList.size();
                for (int i3 = size2 - 1; i3 >= 0; i3--) {
                    ServiceRecord serviceRecord = (ServiceRecord) arrayList.get(i3);
                    serviceRecord.delayed = false;
                    serviceMap.ensureNotStartingBackgroundLocked(serviceRecord);
                    stopServiceLocked(serviceRecord, true);
                }
                if (size2 > 0) {
                    this.mAm.updateOomAdjPendingTargetsLocked(5);
                }
            }
        }
    }

    public void killMisbehavingService(ServiceRecord serviceRecord, int i, int i2, String str, int i3) {
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (!serviceRecord.destroying) {
                    stopServiceLocked(serviceRecord, false);
                } else {
                    ServiceRecord remove = getServiceMapLocked(serviceRecord.userId).mServicesByInstanceName.remove(serviceRecord.instanceName);
                    if (remove != null) {
                        stopServiceLocked(remove, false);
                    }
                }
                this.mAm.crashApplicationWithType(i, i2, str, -1, "Bad notification for startForeground", true, i3);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public IBinder peekServiceLocked(Intent intent, String str, String str2) {
        ServiceLookupResult retrieveServiceLocked = retrieveServiceLocked(intent, null, str, str2, Binder.getCallingPid(), Binder.getCallingUid(), UserHandle.getCallingUserId(), false, false, false, false, false);
        if (retrieveServiceLocked != null) {
            ServiceRecord serviceRecord = retrieveServiceLocked.record;
            if (serviceRecord == null) {
                throw new SecurityException("Permission Denial: Accessing service from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + retrieveServiceLocked.permission);
            }
            IntentBindRecord intentBindRecord = serviceRecord.bindings.get(serviceRecord.intent);
            if (intentBindRecord != null) {
                return intentBindRecord.binder;
            }
        }
        return null;
    }

    public boolean stopServiceTokenLocked(ComponentName componentName, IBinder iBinder, int i) {
        ServiceRecord findServiceLocked = findServiceLocked(componentName, iBinder, UserHandle.getCallingUserId());
        if (findServiceLocked != null) {
            if (i >= 0) {
                ServiceRecord.StartItem findDeliveredStart = findServiceLocked.findDeliveredStart(i, false, false);
                if (findDeliveredStart != null) {
                    while (findServiceLocked.deliveredStarts.size() > 0) {
                        ServiceRecord.StartItem remove = findServiceLocked.deliveredStarts.remove(0);
                        remove.removeUriPermissionsLocked();
                        if (remove == findDeliveredStart) {
                            break;
                        }
                    }
                }
                if (findServiceLocked.getLastStartId() != i) {
                    return false;
                }
                if (findServiceLocked.deliveredStarts.size() > 0) {
                    Slog.w("ActivityManager", "stopServiceToken startId " + i + " is last, but have " + findServiceLocked.deliveredStarts.size() + " remaining args");
                }
            }
            maybeStopShortFgsTimeoutLocked(findServiceLocked);
            int i2 = findServiceLocked.appInfo.uid;
            String packageName = findServiceLocked.name.getPackageName();
            String className = findServiceLocked.name.getClassName();
            FrameworkStatsLog.write(99, i2, packageName, className, 2);
            this.mAm.mBatteryStatsService.noteServiceStopRunning(i2, packageName, className);
            findServiceLocked.startRequested = false;
            if (findServiceLocked.tracker != null) {
                synchronized (this.mAm.mProcessStats.mLock) {
                    findServiceLocked.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
            }
            findServiceLocked.callStart = false;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            bringDownServiceIfNeededLocked(findServiceLocked, false, false, false, "stopServiceToken");
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return true;
        }
        return false;
    }

    @GuardedBy({"mAm"})
    public void setServiceForegroundLocked(ComponentName componentName, IBinder iBinder, int i, Notification notification, int i2, int i3) {
        int callingUserId = UserHandle.getCallingUserId();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ServiceRecord findServiceLocked = findServiceLocked(componentName, iBinder, callingUserId);
            if (findServiceLocked != null) {
                setServiceForegroundInnerLocked(findServiceLocked, i, notification, i2, i3);
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getForegroundServiceTypeLocked(ComponentName componentName, IBinder iBinder) {
        int callingUserId = UserHandle.getCallingUserId();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ServiceRecord findServiceLocked = findServiceLocked(componentName, iBinder, callingUserId);
            return findServiceLocked != null ? findServiceLocked.foregroundServiceType : 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean foregroundAppShownEnoughLocked(ActiveForegroundApp activeForegroundApp, long j) {
        long j2;
        activeForegroundApp.mHideTime = Long.MAX_VALUE;
        if (activeForegroundApp.mShownWhileTop) {
            return true;
        }
        if (this.mScreenOn || activeForegroundApp.mShownWhileScreenOn) {
            long j3 = activeForegroundApp.mStartVisibleTime;
            if (activeForegroundApp.mStartTime != j3) {
                j2 = this.mAm.mConstants.FGSERVICE_SCREEN_ON_AFTER_TIME;
            } else {
                j2 = this.mAm.mConstants.FGSERVICE_MIN_SHOWN_TIME;
            }
            long j4 = j3 + j2;
            if (j >= j4) {
                return true;
            }
            long j5 = j + this.mAm.mConstants.FGSERVICE_MIN_REPORT_TIME;
            if (j5 > j4) {
                j4 = j5;
            }
            activeForegroundApp.mHideTime = j4;
        } else {
            long j6 = activeForegroundApp.mEndTime + this.mAm.mConstants.FGSERVICE_SCREEN_ON_BEFORE_TIME;
            if (j >= j6) {
                return true;
            }
            activeForegroundApp.mHideTime = j6;
        }
        return false;
    }

    public void updateForegroundApps(ServiceMap serviceMap) {
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                if (serviceMap != null) {
                    serviceMap.mPendingRemoveForegroundApps.clear();
                    long j = Long.MAX_VALUE;
                    for (int size = serviceMap.mActiveForegroundApps.size() - 1; size >= 0; size--) {
                        ActiveForegroundApp valueAt = serviceMap.mActiveForegroundApps.valueAt(size);
                        if (valueAt.mEndTime != 0) {
                            if (foregroundAppShownEnoughLocked(valueAt, elapsedRealtime)) {
                                serviceMap.mPendingRemoveForegroundApps.add(serviceMap.mActiveForegroundApps.keyAt(size));
                                serviceMap.mActiveForegroundAppsChanged = true;
                            } else {
                                long j2 = valueAt.mHideTime;
                                if (j2 < j) {
                                    j = j2;
                                }
                            }
                        }
                        if (!valueAt.mAppOnTop && !isForegroundServiceAllowedInBackgroundRestricted(valueAt.mUid, valueAt.mPackageName)) {
                            stopAllForegroundServicesLocked(valueAt.mUid, valueAt.mPackageName);
                        }
                    }
                    for (int size2 = serviceMap.mPendingRemoveForegroundApps.size() - 1; size2 >= 0; size2--) {
                        serviceMap.mActiveForegroundApps.remove(serviceMap.mPendingRemoveForegroundApps.get(size2));
                    }
                    serviceMap.removeMessages(2);
                    if (j < Long.MAX_VALUE) {
                        serviceMap.sendMessageAtTime(serviceMap.obtainMessage(2), (j + SystemClock.uptimeMillis()) - SystemClock.elapsedRealtime());
                    }
                }
                serviceMap.mActiveForegroundAppsChanged = false;
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public final void requestUpdateActiveForegroundAppsLocked(ServiceMap serviceMap, long j) {
        Message obtainMessage = serviceMap.obtainMessage(2);
        if (j != 0) {
            serviceMap.sendMessageAtTime(obtainMessage, (j + SystemClock.uptimeMillis()) - SystemClock.elapsedRealtime());
            return;
        }
        serviceMap.mActiveForegroundAppsChanged = true;
        serviceMap.sendMessage(obtainMessage);
    }

    public final void decActiveForegroundAppLocked(ServiceMap serviceMap, ServiceRecord serviceRecord) {
        ActiveForegroundApp activeForegroundApp = serviceMap.mActiveForegroundApps.get(serviceRecord.packageName);
        if (activeForegroundApp != null) {
            int i = activeForegroundApp.mNumActive - 1;
            activeForegroundApp.mNumActive = i;
            if (i <= 0) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                activeForegroundApp.mEndTime = elapsedRealtime;
                if (foregroundAppShownEnoughLocked(activeForegroundApp, elapsedRealtime)) {
                    serviceMap.mActiveForegroundApps.remove(serviceRecord.packageName);
                    serviceMap.mActiveForegroundAppsChanged = true;
                    requestUpdateActiveForegroundAppsLocked(serviceMap, 0L);
                    return;
                }
                long j = activeForegroundApp.mHideTime;
                if (j < Long.MAX_VALUE) {
                    requestUpdateActiveForegroundAppsLocked(serviceMap, j);
                }
            }
        }
    }

    public void updateScreenStateLocked(boolean z) {
        if (this.mScreenOn != z) {
            this.mScreenOn = z;
            if (z) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                for (int size = this.mServiceMap.size() - 1; size >= 0; size--) {
                    ServiceMap valueAt = this.mServiceMap.valueAt(size);
                    boolean z2 = false;
                    long j = Long.MAX_VALUE;
                    for (int size2 = valueAt.mActiveForegroundApps.size() - 1; size2 >= 0; size2--) {
                        ActiveForegroundApp valueAt2 = valueAt.mActiveForegroundApps.valueAt(size2);
                        if (valueAt2.mEndTime == 0) {
                            if (!valueAt2.mShownWhileScreenOn) {
                                valueAt2.mShownWhileScreenOn = true;
                                valueAt2.mStartVisibleTime = elapsedRealtime;
                            }
                        } else {
                            if (!valueAt2.mShownWhileScreenOn && valueAt2.mStartVisibleTime == valueAt2.mStartTime) {
                                valueAt2.mStartVisibleTime = elapsedRealtime;
                                valueAt2.mEndTime = elapsedRealtime;
                            }
                            if (foregroundAppShownEnoughLocked(valueAt2, elapsedRealtime)) {
                                valueAt.mActiveForegroundApps.remove(valueAt2.mPackageName);
                                valueAt.mActiveForegroundAppsChanged = true;
                                z2 = true;
                            } else {
                                long j2 = valueAt2.mHideTime;
                                if (j2 < j) {
                                    j = j2;
                                }
                            }
                        }
                    }
                    if (z2) {
                        requestUpdateActiveForegroundAppsLocked(valueAt, 0L);
                    } else if (j < Long.MAX_VALUE) {
                        requestUpdateActiveForegroundAppsLocked(valueAt, j);
                    }
                }
            }
        }
    }

    public void foregroundServiceProcStateChangedLocked(UidRecord uidRecord) {
        ServiceMap serviceMap = this.mServiceMap.get(UserHandle.getUserId(uidRecord.getUid()));
        if (serviceMap != null) {
            boolean z = false;
            for (int size = serviceMap.mActiveForegroundApps.size() - 1; size >= 0; size--) {
                ActiveForegroundApp valueAt = serviceMap.mActiveForegroundApps.valueAt(size);
                if (valueAt.mUid == uidRecord.getUid()) {
                    if (uidRecord.getCurProcState() <= 2) {
                        if (!valueAt.mAppOnTop) {
                            valueAt.mAppOnTop = true;
                            z = true;
                        }
                        valueAt.mShownWhileTop = true;
                    } else if (valueAt.mAppOnTop) {
                        valueAt.mAppOnTop = false;
                        z = true;
                    }
                }
            }
            if (z) {
                requestUpdateActiveForegroundAppsLocked(serviceMap, 0L);
            }
        }
    }

    public final boolean isForegroundServiceAllowedInBackgroundRestricted(ProcessRecord processRecord) {
        ProcessStateRecord processStateRecord = processRecord.mState;
        if (!processStateRecord.isBackgroundRestricted() || processStateRecord.getSetProcState() <= 3) {
            return true;
        }
        return processStateRecord.getSetProcState() == 4 && processStateRecord.isSetBoundByNonBgRestrictedApp();
    }

    public final boolean isForegroundServiceAllowedInBackgroundRestricted(int i, String str) {
        ProcessRecord processInPackage;
        UidRecord uidRecordLOSP = this.mAm.mProcessList.getUidRecordLOSP(i);
        return (uidRecordLOSP == null || (processInPackage = uidRecordLOSP.getProcessInPackage(str)) == null || !isForegroundServiceAllowedInBackgroundRestricted(processInPackage)) ? false : true;
    }

    public void logFgsApiBeginLocked(int i, int i2, int i3) {
        synchronized (this.mFGSLogger) {
            this.mFGSLogger.logForegroundServiceApiEventBegin(i, i2, i3, "");
        }
    }

    public void logFgsApiEndLocked(int i, int i2, int i3) {
        synchronized (this.mFGSLogger) {
            this.mFGSLogger.logForegroundServiceApiEventEnd(i, i2, i3);
        }
    }

    public void logFgsApiStateChangedLocked(int i, int i2, int i3, int i4) {
        synchronized (this.mFGSLogger) {
            this.mFGSLogger.logForegroundServiceApiStateChanged(i, i2, i3, i4);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:134:0x03c2  */
    /* JADX WARN: Removed duplicated region for block: B:150:0x0402  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x0405 A[Catch: all -> 0x0592, TryCatch #2 {all -> 0x0592, blocks: (B:43:0x0155, B:48:0x016f, B:49:0x0176, B:50:0x0177, B:53:0x0194, B:55:0x019c, B:57:0x01ba, B:60:0x01c2, B:63:0x01c7, B:64:0x01cf, B:65:0x01d0, B:69:0x01db, B:73:0x01e7, B:77:0x01fd, B:78:0x0213, B:81:0x0219, B:83:0x0252, B:86:0x0278, B:115:0x0349, B:117:0x034d, B:119:0x0367, B:122:0x0370, B:126:0x0386, B:135:0x03c4, B:148:0x03f6, B:156:0x042a, B:158:0x042e, B:159:0x0433, B:161:0x0443, B:163:0x044b, B:165:0x0457, B:167:0x046e, B:169:0x0474, B:173:0x047e, B:174:0x0482, B:175:0x0496, B:176:0x049b, B:178:0x04b2, B:179:0x04b8, B:189:0x04d5, B:190:0x0514, B:193:0x0520, B:198:0x0527, B:200:0x0531, B:201:0x0534, B:151:0x0405, B:152:0x041b, B:137:0x03cb, B:141:0x03d3, B:143:0x03dc, B:146:0x03ed, B:129:0x03b4, B:130:0x03b9, B:84:0x025a, B:98:0x0296, B:100:0x02a1, B:102:0x02a5, B:104:0x02b7, B:106:0x02fd, B:108:0x0318, B:107:0x0316, B:113:0x0327, B:180:0x04b9, B:182:0x04bf, B:183:0x04ce, B:191:0x0515, B:192:0x051f), top: B:274:0x0155 }] */
    /* JADX WARN: Removed duplicated region for block: B:153:0x041c  */
    /* JADX WARN: Removed duplicated region for block: B:154:0x041f  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x042a A[Catch: all -> 0x0592, TryCatch #2 {all -> 0x0592, blocks: (B:43:0x0155, B:48:0x016f, B:49:0x0176, B:50:0x0177, B:53:0x0194, B:55:0x019c, B:57:0x01ba, B:60:0x01c2, B:63:0x01c7, B:64:0x01cf, B:65:0x01d0, B:69:0x01db, B:73:0x01e7, B:77:0x01fd, B:78:0x0213, B:81:0x0219, B:83:0x0252, B:86:0x0278, B:115:0x0349, B:117:0x034d, B:119:0x0367, B:122:0x0370, B:126:0x0386, B:135:0x03c4, B:148:0x03f6, B:156:0x042a, B:158:0x042e, B:159:0x0433, B:161:0x0443, B:163:0x044b, B:165:0x0457, B:167:0x046e, B:169:0x0474, B:173:0x047e, B:174:0x0482, B:175:0x0496, B:176:0x049b, B:178:0x04b2, B:179:0x04b8, B:189:0x04d5, B:190:0x0514, B:193:0x0520, B:198:0x0527, B:200:0x0531, B:201:0x0534, B:151:0x0405, B:152:0x041b, B:137:0x03cb, B:141:0x03d3, B:143:0x03dc, B:146:0x03ed, B:129:0x03b4, B:130:0x03b9, B:84:0x025a, B:98:0x0296, B:100:0x02a1, B:102:0x02a5, B:104:0x02b7, B:106:0x02fd, B:108:0x0318, B:107:0x0316, B:113:0x0327, B:180:0x04b9, B:182:0x04bf, B:183:0x04ce, B:191:0x0515, B:192:0x051f), top: B:274:0x0155 }] */
    /* JADX WARN: Removed duplicated region for block: B:203:0x054c  */
    /* JADX WARN: Removed duplicated region for block: B:214:0x0570  */
    /* JADX WARN: Removed duplicated region for block: B:273:0x06b4 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:281:0x0632 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x01c0  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x0282  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0287  */
    /* JADX WARN: Type inference failed for: r14v22 */
    /* JADX WARN: Type inference failed for: r14v23 */
    /* JADX WARN: Type inference failed for: r14v4 */
    /* JADX WARN: Type inference failed for: r14v5, types: [int, boolean] */
    @GuardedBy({"mAm"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setServiceForegroundInnerLocked(ServiceRecord serviceRecord, int i, Notification notification, int i2, int i3) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        int i4;
        ?? r14;
        ProcessServiceRecord processServiceRecord;
        boolean z5;
        int i5;
        boolean z6;
        UidRecord uidRecord;
        int i6;
        boolean z7;
        int i7;
        ProcessServiceRecord processServiceRecord2;
        boolean z8;
        boolean z9;
        boolean z10;
        boolean z11;
        boolean z12;
        Pair<Integer, RuntimeException> pair;
        Pair<Integer, RuntimeException> validateForegroundServiceType;
        boolean z13;
        boolean z14;
        boolean z15;
        if (i != 0) {
            if (notification == null) {
                throw new IllegalArgumentException("null notification");
            }
            if (serviceRecord.appInfo.isInstantApp()) {
                AppOpsManager appOpsManager = this.mAm.getAppOpsManager();
                ApplicationInfo applicationInfo = serviceRecord.appInfo;
                int checkOpNoThrow = appOpsManager.checkOpNoThrow(68, applicationInfo.uid, applicationInfo.packageName);
                if (checkOpNoThrow != 0) {
                    if (checkOpNoThrow == 1) {
                        Slog.w("ActivityManager", "Instant app " + serviceRecord.appInfo.packageName + " does not have permission to create foreground services, ignoring.");
                        return;
                    } else if (checkOpNoThrow == 2) {
                        throw new SecurityException("Instant app " + serviceRecord.appInfo.packageName + " does not have permission to create foreground services");
                    } else {
                        this.mAm.enforcePermission("android.permission.INSTANT_APP_FOREGROUND_SERVICE", serviceRecord.app.getPid(), serviceRecord.appInfo.uid, "startForeground");
                    }
                }
            } else if (serviceRecord.appInfo.targetSdkVersion >= 28) {
                this.mAm.enforcePermission("android.permission.FOREGROUND_SERVICE", serviceRecord.app.getPid(), serviceRecord.appInfo.uid, "startForeground");
            }
            int foregroundServiceType = serviceRecord.serviceInfo.getForegroundServiceType();
            int i8 = i3 == -1 ? foregroundServiceType : i3;
            if ((i8 & foregroundServiceType) != i8 && !SystemProperties.getBoolean("debug.skip_fgs_manifest_type_check", false)) {
                String str = "foregroundServiceType " + String.format("0x%08X", Integer.valueOf(i8)) + " is not a subset of foregroundServiceType attribute " + String.format("0x%08X", Integer.valueOf(foregroundServiceType)) + " in service element of manifest file";
                if (!serviceRecord.appInfo.isInstantApp() || CompatChanges.isChangeEnabled(261055255L, serviceRecord.appInfo.uid)) {
                    throw new IllegalArgumentException(str);
                }
                Slog.w("ActivityManager", str + "\nThis will be an exception once the target SDK level is UDC");
            }
            if ((i8 & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) != 0 && i8 != 2048) {
                Slog.w("ActivityManager", "startForeground(): FOREGROUND_SERVICE_TYPE_SHORT_SERVICE is combined with other types. SHORT_SERVICE will be ignored.");
                i8 &= -2049;
            }
            int i9 = i8;
            if (serviceRecord.fgRequired) {
                serviceRecord.fgRequired = false;
                serviceRecord.fgWaiting = false;
                this.mAm.mHandler.removeMessages(66, serviceRecord);
                z2 = true;
            } else {
                z2 = false;
            }
            boolean z16 = z2;
            ProcessServiceRecord processServiceRecord3 = serviceRecord.app.mServices;
            try {
                int checkOpNoThrow2 = this.mAm.getAppOpsManager().checkOpNoThrow(76, serviceRecord.appInfo.uid, serviceRecord.packageName);
                if (checkOpNoThrow2 != 0) {
                    if (checkOpNoThrow2 == 1) {
                        Slog.w("ActivityManager", "Service.startForeground() not allowed due to app op: service " + serviceRecord.shortInstanceName);
                        z3 = true;
                        if (!z3 || isForegroundServiceAllowedInBackgroundRestricted(serviceRecord.app)) {
                            z4 = z3;
                        } else {
                            Slog.w("ActivityManager", "Service.startForeground() not allowed due to bg restriction: service " + serviceRecord.shortInstanceName);
                            updateServiceForegroundLocked(processServiceRecord3, false);
                            z4 = true;
                        }
                        boolean isBgFgsRestrictionEnabled = isBgFgsRestrictionEnabled(serviceRecord);
                        if (z4) {
                            if (i9 == 2048 && !serviceRecord.startRequested) {
                                throw new StartForegroundCalledOnStoppedServiceException("startForeground(SHORT_SERVICE) called on a service that's not started.");
                            }
                            boolean isShortFgs = serviceRecord.isShortFgs();
                            boolean z17 = i9 == 2048;
                            boolean shouldTriggerShortFgsTimeout = serviceRecord.shouldTriggerShortFgsTimeout();
                            if (serviceRecord.isForeground && (isShortFgs || z17)) {
                                Object[] objArr = new Object[4];
                                objArr[0] = Integer.valueOf(serviceRecord.foregroundServiceType);
                                objArr[1] = shouldTriggerShortFgsTimeout ? "(timed out short FGS)" : "";
                                objArr[2] = Integer.valueOf(i3);
                                objArr[3] = serviceRecord.toString();
                                Slog.i("ActivityManager", String.format("FGS type changing from %x%s to %x: %s", objArr));
                            }
                            if (serviceRecord.isForeground && isShortFgs) {
                                serviceRecord.mAllowStartForeground = -1;
                                i6 = i9;
                                z7 = z4;
                                setFgsRestrictionLocked(serviceRecord.serviceInfo.packageName, serviceRecord.app.getPid(), serviceRecord.appInfo.uid, serviceRecord.intent.getIntent(), serviceRecord, serviceRecord.userId, BackgroundStartPrivileges.NONE, false);
                                if (serviceRecord.mAllowStartForeground == -1) {
                                    Slog.w("ActivityManager", "FGS type change to/from SHORT_SERVICE:  BFSL DENIED.");
                                } else {
                                    Slog.w("ActivityManager", "FGS type change to/from SHORT_SERVICE:  BFSL Allowed: " + PowerExemptionManager.reasonCodeToString(serviceRecord.mAllowStartForeground));
                                }
                                if (isBgFgsRestrictionEnabled && serviceRecord.mAllowStartForeground == -1) {
                                    z13 = false;
                                    if (z13) {
                                        if (z17) {
                                            z14 = false;
                                            z15 = true;
                                            z10 = z15;
                                            z9 = z14;
                                            i7 = -1;
                                            processServiceRecord2 = processServiceRecord3;
                                            z11 = true;
                                        }
                                        z15 = false;
                                        z14 = false;
                                        z10 = z15;
                                        z9 = z14;
                                        i7 = -1;
                                        processServiceRecord2 = processServiceRecord3;
                                        z11 = true;
                                    } else {
                                        if (z17) {
                                            z15 = false;
                                            z14 = true;
                                            z10 = z15;
                                            z9 = z14;
                                            i7 = -1;
                                            processServiceRecord2 = processServiceRecord3;
                                            z11 = true;
                                        }
                                        z15 = false;
                                        z14 = false;
                                        z10 = z15;
                                        z9 = z14;
                                        i7 = -1;
                                        processServiceRecord2 = processServiceRecord3;
                                        z11 = true;
                                    }
                                }
                                z13 = true;
                                if (z13) {
                                }
                            } else {
                                i6 = i9;
                                z7 = z4;
                                int i10 = serviceRecord.mStartForegroundCount;
                                if (i10 == 0) {
                                    if (!serviceRecord.fgRequired) {
                                        long elapsedRealtime = SystemClock.elapsedRealtime() - serviceRecord.createRealTime;
                                        if (elapsedRealtime > this.mAm.mConstants.mFgsStartForegroundTimeoutMs) {
                                            resetFgsRestrictionLocked(serviceRecord);
                                            processServiceRecord2 = processServiceRecord3;
                                            i7 = -1;
                                            setFgsRestrictionLocked(serviceRecord.serviceInfo.packageName, serviceRecord.app.getPid(), serviceRecord.appInfo.uid, serviceRecord.intent.getIntent(), serviceRecord, serviceRecord.userId, BackgroundStartPrivileges.NONE, false);
                                            String str2 = "startForegroundDelayMs:" + elapsedRealtime;
                                            if (serviceRecord.mInfoAllowStartForeground != null) {
                                                serviceRecord.mInfoAllowStartForeground += "; " + str2;
                                            } else {
                                                serviceRecord.mInfoAllowStartForeground = str2;
                                            }
                                            serviceRecord.mLoggedInfoAllowStartForeground = false;
                                            z8 = true;
                                        }
                                    }
                                    i7 = -1;
                                    processServiceRecord2 = processServiceRecord3;
                                    z8 = true;
                                } else {
                                    i7 = -1;
                                    processServiceRecord2 = processServiceRecord3;
                                    z8 = true;
                                    z8 = true;
                                    if (i10 >= 1) {
                                        setFgsRestrictionLocked(serviceRecord.serviceInfo.packageName, serviceRecord.app.getPid(), serviceRecord.appInfo.uid, serviceRecord.intent.getIntent(), serviceRecord, serviceRecord.userId, BackgroundStartPrivileges.NONE, false);
                                    }
                                }
                                z9 = false;
                                z10 = false;
                                z11 = z8;
                            }
                            if (!serviceRecord.mAllowWhileInUsePermissionInFgs) {
                                Slog.w("ActivityManager", "Foreground service started from background can not have location/camera/microphone access: service " + serviceRecord.shortInstanceName);
                            }
                            if (!z10) {
                                logFgsBackgroundStart(serviceRecord);
                                if (serviceRecord.mAllowStartForeground == i7 && isBgFgsRestrictionEnabled) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Service.startForeground() not allowed due to mAllowStartForeground false: service ");
                                    sb.append(serviceRecord.shortInstanceName);
                                    sb.append(isShortFgs ? " (Called on SHORT_SERVICE)" : "");
                                    String sb2 = sb.toString();
                                    Slog.w("ActivityManager", sb2);
                                    showFgsBgRestrictedNotificationLocked(serviceRecord);
                                    processServiceRecord = processServiceRecord2;
                                    updateServiceForegroundLocked(processServiceRecord, z11);
                                    logFGSStateChangeLocked(serviceRecord, 3, 0, 0, 0);
                                    if (CompatChanges.isChangeEnabled(174041399L, serviceRecord.appInfo.uid)) {
                                        throw new ForegroundServiceStartNotAllowedException(sb2);
                                    }
                                    z12 = z11 ? 1 : 0;
                                    i4 = i6;
                                    if (z12) {
                                        if (i4 == 0) {
                                            validateForegroundServiceType = validateForegroundServiceType(serviceRecord, i4, 0, i3);
                                        } else {
                                            int i11 = 1073741824;
                                            if ((i4 & 1073741824) == 0) {
                                                i11 = 0;
                                            }
                                            int highestOneBit = Integer.highestOneBit(i4);
                                            int i12 = i4;
                                            Pair<Integer, RuntimeException> pair2 = null;
                                            while (highestOneBit != 0) {
                                                validateForegroundServiceType = validateForegroundServiceType(serviceRecord, highestOneBit, i11, i3);
                                                i12 &= ~highestOneBit;
                                                if (((Integer) validateForegroundServiceType.first).intValue() == z11) {
                                                    highestOneBit = Integer.highestOneBit(i12);
                                                    pair2 = validateForegroundServiceType;
                                                }
                                            }
                                            pair = pair2;
                                            int intValue = ((Integer) pair.first).intValue();
                                            if (pair.second == null) {
                                                logFGSStateChangeLocked(serviceRecord, 3, 0, 0, ((Integer) pair.first).intValue());
                                                throw ((RuntimeException) pair.second);
                                            }
                                            i5 = intValue;
                                            z6 = z9;
                                            z5 = z12;
                                            r14 = z11;
                                        }
                                        pair = validateForegroundServiceType;
                                        int intValue2 = ((Integer) pair.first).intValue();
                                        if (pair.second == null) {
                                        }
                                    } else {
                                        z6 = z9;
                                        i5 = 0;
                                        z5 = z12;
                                        r14 = z11;
                                    }
                                }
                            }
                            processServiceRecord = processServiceRecord2;
                            z12 = z7;
                            i4 = i6;
                            if (z12) {
                            }
                        } else {
                            i4 = i9;
                            boolean z18 = z4;
                            r14 = 1;
                            processServiceRecord = processServiceRecord3;
                            z5 = z18 ? 1 : 0;
                            i5 = 0;
                            z6 = false;
                        }
                        if (!z5) {
                            if (serviceRecord.foregroundId != i) {
                                cancelForegroundNotificationLocked(serviceRecord);
                                serviceRecord.foregroundId = i;
                            }
                            notification.flags |= 64;
                            serviceRecord.foregroundNoti = notification;
                            serviceRecord.foregroundServiceType = i4;
                            if (!serviceRecord.isForeground) {
                                ServiceMap serviceMapLocked = getServiceMapLocked(serviceRecord.userId);
                                if (serviceMapLocked != null) {
                                    ActiveForegroundApp activeForegroundApp = serviceMapLocked.mActiveForegroundApps.get(serviceRecord.packageName);
                                    if (activeForegroundApp == null) {
                                        activeForegroundApp = new ActiveForegroundApp();
                                        activeForegroundApp.mPackageName = serviceRecord.packageName;
                                        activeForegroundApp.mUid = serviceRecord.appInfo.uid;
                                        activeForegroundApp.mShownWhileScreenOn = this.mScreenOn;
                                        ProcessRecord processRecord = serviceRecord.app;
                                        if (processRecord != null && (uidRecord = processRecord.getUidRecord()) != null) {
                                            boolean z19 = uidRecord.getCurProcState() <= 2 ? r14 : false;
                                            activeForegroundApp.mShownWhileTop = z19;
                                            activeForegroundApp.mAppOnTop = z19;
                                        }
                                        long elapsedRealtime2 = SystemClock.elapsedRealtime();
                                        activeForegroundApp.mStartVisibleTime = elapsedRealtime2;
                                        activeForegroundApp.mStartTime = elapsedRealtime2;
                                        serviceMapLocked.mActiveForegroundApps.put(serviceRecord.packageName, activeForegroundApp);
                                        requestUpdateActiveForegroundAppsLocked(serviceMapLocked, 0L);
                                    }
                                    activeForegroundApp.mNumActive += r14;
                                }
                                serviceRecord.isForeground = r14;
                                serviceRecord.mAllowStartForegroundAtEntering = serviceRecord.mAllowStartForeground;
                                serviceRecord.mAllowWhileInUsePermissionInFgsAtEntering = serviceRecord.mAllowWhileInUsePermissionInFgs;
                                serviceRecord.mStartForegroundCount += r14;
                                serviceRecord.mFgsEnterTime = SystemClock.uptimeMillis();
                                if (z16) {
                                    z16 = false;
                                } else {
                                    synchronized (this.mAm.mProcessStats.mLock) {
                                        ServiceState tracker = serviceRecord.getTracker();
                                        if (tracker != null) {
                                            tracker.setForeground((boolean) r14, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                                        }
                                    }
                                }
                                AppOpsService appOpsService = this.mAm.mAppOpsService;
                                appOpsService.startOperation(AppOpsManager.getToken(appOpsService), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, null, true, false, "", false, 0, -1);
                                registerAppOpCallbackLocked(serviceRecord);
                                this.mAm.updateForegroundServiceUsageStats(serviceRecord.name, serviceRecord.userId, r14);
                                logFGSStateChangeLocked(serviceRecord, 1, 0, 0, i5);
                                synchronized (this.mFGSLogger) {
                                    this.mFGSLogger.logForegroundServiceStart(serviceRecord.appInfo.uid, 0, serviceRecord);
                                }
                                updateNumForegroundServicesLocked();
                            }
                            signalForegroundServiceObserversLocked(serviceRecord);
                            serviceRecord.postNotification();
                            if (serviceRecord.app != null) {
                                updateServiceForegroundLocked(processServiceRecord, r14);
                            }
                            getServiceMapLocked(serviceRecord.userId).ensureNotStartingBackgroundLocked(serviceRecord);
                            this.mAm.notifyPackageUse(serviceRecord.serviceInfo.packageName, 2);
                            maybeUpdateShortFgsTrackingLocked(serviceRecord, z6);
                        }
                        if (z16) {
                            synchronized (this.mAm.mProcessStats.mLock) {
                                ServiceState tracker2 = serviceRecord.getTracker();
                                if (tracker2 != null) {
                                    tracker2.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                                }
                            }
                        }
                        if (z2) {
                            return;
                        }
                        AppOpsService appOpsService2 = this.mAm.mAppOpsService;
                        appOpsService2.finishOperation(AppOpsManager.getToken(appOpsService2), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, null);
                        return;
                    } else if (checkOpNoThrow2 != 3) {
                        throw new SecurityException("Foreground not allowed as per app op");
                    }
                }
                z3 = false;
                if (z3) {
                }
                z4 = z3;
                boolean isBgFgsRestrictionEnabled2 = isBgFgsRestrictionEnabled(serviceRecord);
                if (z4) {
                }
                if (!z5) {
                }
                if (z16) {
                }
                if (z2) {
                }
            } catch (Throwable th) {
                if (z16) {
                    synchronized (this.mAm.mProcessStats.mLock) {
                        ServiceState tracker3 = serviceRecord.getTracker();
                        if (tracker3 != null) {
                            tracker3.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                        }
                    }
                }
                if (z2) {
                    AppOpsService appOpsService3 = this.mAm.mAppOpsService;
                    appOpsService3.finishOperation(AppOpsManager.getToken(appOpsService3), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, null);
                }
                throw th;
            }
        } else if (!serviceRecord.isForeground) {
        } else {
            ServiceMap serviceMapLocked2 = getServiceMapLocked(serviceRecord.userId);
            if (serviceMapLocked2 != null) {
                decActiveForegroundAppLocked(serviceMapLocked2, serviceRecord);
            }
            maybeStopShortFgsTimeoutLocked(serviceRecord);
            if ((i2 & 1) != 0) {
                cancelForegroundNotificationLocked(serviceRecord);
                serviceRecord.foregroundId = 0;
                serviceRecord.foregroundNoti = null;
            } else if (serviceRecord.appInfo.targetSdkVersion >= 21) {
                if (!serviceRecord.mFgsNotificationShown) {
                    serviceRecord.postNotification();
                }
                dropFgsNotificationStateLocked(serviceRecord);
                if ((i2 & 2) != 0) {
                    z = false;
                    serviceRecord.foregroundId = 0;
                    serviceRecord.foregroundNoti = null;
                    serviceRecord.isForeground = z;
                    serviceRecord.mFgsExitTime = SystemClock.uptimeMillis();
                    synchronized (this.mAm.mProcessStats.mLock) {
                        ServiceState tracker4 = serviceRecord.getTracker();
                        if (tracker4 != null) {
                            tracker4.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                        }
                    }
                    AppOpsService appOpsService4 = this.mAm.mAppOpsService;
                    appOpsService4.finishOperation(AppOpsManager.getToken(appOpsService4), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, null);
                    unregisterAppOpCallbackLocked(serviceRecord);
                    long j = serviceRecord.mFgsExitTime;
                    long j2 = serviceRecord.mFgsEnterTime;
                    logFGSStateChangeLocked(serviceRecord, 2, j > j2 ? (int) (j - j2) : 0, 1, 0);
                    serviceRecord.foregroundServiceType = 0;
                    synchronized (this.mFGSLogger) {
                        this.mFGSLogger.logForegroundServiceStop(serviceRecord.appInfo.uid, serviceRecord);
                    }
                    serviceRecord.mFgsNotificationWasDeferred = false;
                    signalForegroundServiceObserversLocked(serviceRecord);
                    resetFgsRestrictionLocked(serviceRecord);
                    this.mAm.updateForegroundServiceUsageStats(serviceRecord.name, serviceRecord.userId, false);
                    ProcessRecord processRecord2 = serviceRecord.app;
                    if (processRecord2 != null) {
                        this.mAm.updateLruProcessLocked(processRecord2, false, null);
                        updateServiceForegroundLocked(serviceRecord.app.mServices, true);
                    }
                    updateNumForegroundServicesLocked();
                    return;
                }
            }
            z = false;
            serviceRecord.isForeground = z;
            serviceRecord.mFgsExitTime = SystemClock.uptimeMillis();
            synchronized (this.mAm.mProcessStats.mLock) {
            }
        }
    }

    public final boolean withinFgsDeferRateLimit(ServiceRecord serviceRecord, long j) {
        if (j < serviceRecord.fgDisplayTime) {
            return false;
        }
        return j < this.mFgsDeferralEligible.get(serviceRecord.appInfo.uid, 0L);
    }

    public final Pair<Integer, RuntimeException> validateForegroundServiceType(ServiceRecord serviceRecord, int i, int i2, int i3) {
        SecurityException securityException;
        ForegroundServiceTypePolicy defaultPolicy = ForegroundServiceTypePolicy.getDefaultPolicy();
        ForegroundServiceTypePolicy.ForegroundServiceTypePolicyInfo foregroundServiceTypePolicyInfo = defaultPolicy.getForegroundServiceTypePolicyInfo(i, i2);
        Context context = this.mAm.mContext;
        String str = serviceRecord.packageName;
        ProcessRecord processRecord = serviceRecord.app;
        int checkForegroundServiceTypePolicy = defaultPolicy.checkForegroundServiceTypePolicy(context, str, processRecord.uid, processRecord.getPid(), serviceRecord.mAllowWhileInUsePermissionInFgs, foregroundServiceTypePolicyInfo);
        if (checkForegroundServiceTypePolicy == 2) {
            String str2 = "Starting FGS with type " + ServiceInfo.foregroundServiceTypeToLabel(i) + " code=" + checkForegroundServiceTypePolicy + " callerApp=" + serviceRecord.app + " targetSDK=" + serviceRecord.app.info.targetSdkVersion;
            Slog.wtfQuiet("ActivityManager", str2);
            Slog.w("ActivityManager", str2);
        } else {
            if (checkForegroundServiceTypePolicy != 3) {
                if (checkForegroundServiceTypePolicy == 4) {
                    String str3 = "Starting FGS with type " + ServiceInfo.foregroundServiceTypeToLabel(i) + " code=" + checkForegroundServiceTypePolicy + " callerApp=" + serviceRecord.app + " targetSDK=" + serviceRecord.app.info.targetSdkVersion + " requiredPermissions=" + foregroundServiceTypePolicyInfo.toPermissionString();
                    Slog.wtfQuiet("ActivityManager", str3);
                    Slog.w("ActivityManager", str3);
                } else if (checkForegroundServiceTypePolicy == 5) {
                    securityException = new SecurityException("Starting FGS with type " + ServiceInfo.foregroundServiceTypeToLabel(i) + " callerApp=" + serviceRecord.app + " targetSDK=" + serviceRecord.app.info.targetSdkVersion + " requires permissions: " + foregroundServiceTypePolicyInfo.toPermissionString());
                }
            } else if (i3 == -1 && i == 0) {
                securityException = new MissingForegroundServiceTypeException("Starting FGS without a type  callerApp=" + serviceRecord.app + " targetSDK=" + serviceRecord.app.info.targetSdkVersion);
            } else {
                securityException = new InvalidForegroundServiceTypeException("Starting FGS with type " + ServiceInfo.foregroundServiceTypeToLabel(i) + " callerApp=" + serviceRecord.app + " targetSDK=" + serviceRecord.app.info.targetSdkVersion + " has been prohibited");
            }
            return Pair.create(Integer.valueOf(checkForegroundServiceTypePolicy), securityException);
        }
        securityException = null;
        return Pair.create(Integer.valueOf(checkForegroundServiceTypePolicy), securityException);
    }

    /* renamed from: com.android.server.am.ActiveServices$SystemExemptedFgsTypePermission */
    /* loaded from: classes.dex */
    public class SystemExemptedFgsTypePermission extends ForegroundServiceTypePolicy.ForegroundServiceTypePermission {
        public SystemExemptedFgsTypePermission() {
            super("System exempted");
        }

        public int checkPermission(Context context, int i, int i2, String str, boolean z) {
            AppRestrictionController appRestrictionController = ActiveServices.this.mAm.mAppRestrictionController;
            int potentialSystemExemptionReason = appRestrictionController.getPotentialSystemExemptionReason(i);
            if (potentialSystemExemptionReason == -1 && (potentialSystemExemptionReason = appRestrictionController.getPotentialSystemExemptionReason(i, str)) == -1) {
                potentialSystemExemptionReason = appRestrictionController.getPotentialUserAllowedExemptionReason(i, str);
            }
            if (potentialSystemExemptionReason == -1 && ArrayUtils.contains(ActiveServices.this.mAm.getPackageManagerInternal().getKnownPackageNames(2, 0), str)) {
                potentialSystemExemptionReason = 326;
            }
            if (potentialSystemExemptionReason != 10 && potentialSystemExemptionReason != 11 && potentialSystemExemptionReason != 51 && potentialSystemExemptionReason != 63 && potentialSystemExemptionReason != 65 && potentialSystemExemptionReason != 300 && potentialSystemExemptionReason != 55 && potentialSystemExemptionReason != 56 && potentialSystemExemptionReason != 326 && potentialSystemExemptionReason != 327) {
                switch (potentialSystemExemptionReason) {
                    case FrameworkStatsLog.f107x9a09c896 /* 319 */:
                    case 320:
                    case 321:
                    case 322:
                    case 323:
                    case FrameworkStatsLog.f56x60da79b1 /* 324 */:
                        break;
                    default:
                        return -1;
                }
            }
            return 0;
        }
    }

    public final void initSystemExemptedFgsTypePermission() {
        ForegroundServiceTypePolicy.ForegroundServiceTypePolicyInfo foregroundServiceTypePolicyInfo = ForegroundServiceTypePolicy.getDefaultPolicy().getForegroundServiceTypePolicyInfo(1024, 0);
        if (foregroundServiceTypePolicyInfo != null) {
            foregroundServiceTypePolicyInfo.setCustomPermission(new SystemExemptedFgsTypePermission());
        }
    }

    public ActivityManagerInternal.ServiceNotificationPolicy applyForegroundServiceNotificationLocked(Notification notification, String str, int i, String str2, int i2) {
        if (str != null) {
            return ActivityManagerInternal.ServiceNotificationPolicy.NOT_FOREGROUND_SERVICE;
        }
        ServiceMap serviceMap = this.mServiceMap.get(i2);
        if (serviceMap == null) {
            return ActivityManagerInternal.ServiceNotificationPolicy.NOT_FOREGROUND_SERVICE;
        }
        for (int i3 = 0; i3 < serviceMap.mServicesByInstanceName.size(); i3++) {
            ServiceRecord valueAt = serviceMap.mServicesByInstanceName.valueAt(i3);
            if (valueAt.isForeground && i == valueAt.foregroundId && str2.equals(valueAt.appInfo.packageName)) {
                notification.flags |= 64;
                valueAt.foregroundNoti = notification;
                if (shouldShowFgsNotificationLocked(valueAt)) {
                    valueAt.mFgsNotificationDeferred = false;
                    return ActivityManagerInternal.ServiceNotificationPolicy.SHOW_IMMEDIATELY;
                }
                startFgsDeferralTimerLocked(valueAt);
                return ActivityManagerInternal.ServiceNotificationPolicy.UPDATE_ONLY;
            }
        }
        return ActivityManagerInternal.ServiceNotificationPolicy.NOT_FOREGROUND_SERVICE;
    }

    public final boolean shouldShowFgsNotificationLocked(ServiceRecord serviceRecord) {
        long uptimeMillis = SystemClock.uptimeMillis();
        if (this.mAm.mConstants.mFlagFgsNotificationDeferralEnabled) {
            if ((!serviceRecord.mFgsNotificationDeferred || uptimeMillis < serviceRecord.fgDisplayTime) && !withinFgsDeferRateLimit(serviceRecord, uptimeMillis)) {
                if (this.mAm.mConstants.mFlagFgsNotificationDeferralApiGated) {
                    if (serviceRecord.appInfo.targetSdkVersion < 31) {
                        return true;
                    }
                }
                if (serviceRecord.mFgsNotificationShown) {
                    return true;
                }
                return !serviceRecord.foregroundNoti.isForegroundDisplayForceDeferred() && (serviceRecord.foregroundNoti.shouldShowForegroundImmediately() || (serviceRecord.foregroundServiceType & 54) != 0);
            }
            return true;
        }
        return true;
    }

    public final void startFgsDeferralTimerLocked(ServiceRecord serviceRecord) {
        long uptimeMillis = SystemClock.uptimeMillis();
        int i = serviceRecord.appInfo.uid;
        long j = uptimeMillis + (serviceRecord.isShortFgs() ? this.mAm.mConstants.mFgsNotificationDeferralIntervalForShort : this.mAm.mConstants.mFgsNotificationDeferralInterval);
        for (int i2 = 0; i2 < this.mPendingFgsNotifications.size(); i2++) {
            ServiceRecord serviceRecord2 = this.mPendingFgsNotifications.get(i2);
            if (serviceRecord2 == serviceRecord) {
                return;
            }
            if (i == serviceRecord2.appInfo.uid) {
                j = Math.min(j, serviceRecord2.fgDisplayTime);
            }
        }
        if (this.mFgsDeferralRateLimited) {
            this.mFgsDeferralEligible.put(i, (serviceRecord.isShortFgs() ? this.mAm.mConstants.mFgsNotificationDeferralExclusionTimeForShort : this.mAm.mConstants.mFgsNotificationDeferralExclusionTime) + j);
        }
        serviceRecord.fgDisplayTime = j;
        serviceRecord.mFgsNotificationDeferred = true;
        serviceRecord.mFgsNotificationWasDeferred = true;
        serviceRecord.mFgsNotificationShown = false;
        this.mPendingFgsNotifications.add(serviceRecord);
        if (serviceRecord.appInfo.targetSdkVersion < 31) {
            Slog.i("ActivityManager", "Deferring FGS notification in legacy app " + serviceRecord.appInfo.packageName + "/" + UserHandle.formatUid(serviceRecord.appInfo.uid) + " : " + serviceRecord.foregroundNoti);
        }
        this.mAm.mHandler.postAtTime(this.mPostDeferredFGSNotifications, j);
    }

    public boolean enableFgsNotificationRateLimitLocked(boolean z) {
        if (z != this.mFgsDeferralRateLimited) {
            this.mFgsDeferralRateLimited = z;
            if (!z) {
                this.mFgsDeferralEligible.clear();
            }
        }
        return z;
    }

    public final void removeServiceNotificationDeferralsLocked(String str, int i) {
        for (int size = this.mPendingFgsNotifications.size() - 1; size >= 0; size--) {
            ServiceRecord serviceRecord = this.mPendingFgsNotifications.get(size);
            if (i == serviceRecord.userId && serviceRecord.appInfo.packageName.equals(str)) {
                this.mPendingFgsNotifications.remove(size);
            }
        }
    }

    public void onForegroundServiceNotificationUpdateLocked(boolean z, Notification notification, int i, String str, int i2) {
        int i3;
        int size = this.mPendingFgsNotifications.size() - 1;
        while (true) {
            if (size < 0) {
                break;
            }
            ServiceRecord serviceRecord = this.mPendingFgsNotifications.get(size);
            if (i2 == serviceRecord.userId && i == serviceRecord.foregroundId && serviceRecord.appInfo.packageName.equals(str) && z) {
                serviceRecord.mFgsNotificationShown = true;
                serviceRecord.mFgsNotificationDeferred = false;
                this.mPendingFgsNotifications.remove(size);
            }
            size--;
        }
        ServiceMap serviceMap = this.mServiceMap.get(i2);
        if (serviceMap != null) {
            for (i3 = 0; i3 < serviceMap.mServicesByInstanceName.size(); i3++) {
                ServiceRecord valueAt = serviceMap.mServicesByInstanceName.valueAt(i3);
                if (valueAt.isForeground && i == valueAt.foregroundId && valueAt.appInfo.packageName.equals(str)) {
                    valueAt.foregroundNoti = notification;
                }
            }
        }
    }

    public final void registerAppOpCallbackLocked(ServiceRecord serviceRecord) {
        if (serviceRecord.app == null) {
            return;
        }
        int i = serviceRecord.appInfo.uid;
        AppOpCallback appOpCallback = this.mFgsAppOpCallbacks.get(i);
        if (appOpCallback == null) {
            appOpCallback = new AppOpCallback(serviceRecord.app, this.mAm.getAppOpsManager());
            this.mFgsAppOpCallbacks.put(i, appOpCallback);
        }
        appOpCallback.registerLocked();
    }

    public final void unregisterAppOpCallbackLocked(ServiceRecord serviceRecord) {
        int i = serviceRecord.appInfo.uid;
        AppOpCallback appOpCallback = this.mFgsAppOpCallbacks.get(i);
        if (appOpCallback != null) {
            appOpCallback.unregisterLocked();
            if (appOpCallback.isObsoleteLocked()) {
                this.mFgsAppOpCallbacks.remove(i);
            }
        }
    }

    /* renamed from: com.android.server.am.ActiveServices$AppOpCallback */
    /* loaded from: classes.dex */
    public static final class AppOpCallback {
        public static final int[] LOGGED_AP_OPS = {0, 1, 27, 26};
        public final AppOpsManager mAppOpsManager;
        public final ProcessRecord mProcessRecord;
        @GuardedBy({"mCounterLock"})
        public final SparseIntArray mAcceptedOps = new SparseIntArray();
        @GuardedBy({"mCounterLock"})
        public final SparseIntArray mRejectedOps = new SparseIntArray();
        public final Object mCounterLock = new Object();
        public final SparseIntArray mAppOpModes = new SparseIntArray();
        @GuardedBy({"mAm"})
        public int mNumFgs = 0;
        @GuardedBy({"mAm"})
        public boolean mDestroyed = false;
        public final AppOpsManager.OnOpNotedInternalListener mOpNotedCallback = new AppOpsManager.OnOpNotedInternalListener() { // from class: com.android.server.am.ActiveServices.AppOpCallback.1
            public void onOpNoted(int i, int i2, String str, String str2, int i3, int i4) {
                AppOpCallback.this.incrementOpCountIfNeeded(i, i2, i4);
            }
        };
        public final AppOpsManager.OnOpStartedListener mOpStartedCallback = new AppOpsManager.OnOpStartedListener() { // from class: com.android.server.am.ActiveServices.AppOpCallback.2
            public void onOpStarted(int i, int i2, String str, String str2, int i3, int i4) {
                AppOpCallback.this.incrementOpCountIfNeeded(i, i2, i4);
            }
        };

        public static int modeToEnum(int i) {
            if (i != 0) {
                if (i != 1) {
                    return i != 4 ? 0 : 3;
                }
                return 2;
            }
            return 1;
        }

        public AppOpCallback(ProcessRecord processRecord, AppOpsManager appOpsManager) {
            int[] iArr;
            this.mProcessRecord = processRecord;
            this.mAppOpsManager = appOpsManager;
            for (int i : LOGGED_AP_OPS) {
                this.mAppOpModes.put(i, appOpsManager.unsafeCheckOpRawNoThrow(i, processRecord.uid, processRecord.info.packageName));
            }
        }

        public final void incrementOpCountIfNeeded(int i, int i2, int i3) {
            if (i2 == this.mProcessRecord.uid && isNotTop()) {
                incrementOpCount(i, i3 == 0);
            }
        }

        public final boolean isNotTop() {
            return this.mProcessRecord.mState.getCurProcState() != 2;
        }

        public final void incrementOpCount(int i, boolean z) {
            synchronized (this.mCounterLock) {
                SparseIntArray sparseIntArray = z ? this.mAcceptedOps : this.mRejectedOps;
                int indexOfKey = sparseIntArray.indexOfKey(i);
                if (indexOfKey < 0) {
                    sparseIntArray.put(i, 1);
                } else {
                    sparseIntArray.setValueAt(indexOfKey, sparseIntArray.valueAt(indexOfKey) + 1);
                }
            }
        }

        public void registerLocked() {
            if (isObsoleteLocked()) {
                Slog.wtf("ActivityManager", "Trying to register on a stale AppOpCallback.");
                return;
            }
            int i = this.mNumFgs + 1;
            this.mNumFgs = i;
            if (i == 1) {
                AppOpsManager appOpsManager = this.mAppOpsManager;
                int[] iArr = LOGGED_AP_OPS;
                appOpsManager.startWatchingNoted(iArr, this.mOpNotedCallback);
                this.mAppOpsManager.startWatchingStarted(iArr, this.mOpStartedCallback);
            }
        }

        public void unregisterLocked() {
            int i = this.mNumFgs - 1;
            this.mNumFgs = i;
            if (i <= 0) {
                this.mDestroyed = true;
                logFinalValues();
                this.mAppOpsManager.stopWatchingNoted(this.mOpNotedCallback);
                this.mAppOpsManager.stopWatchingStarted(this.mOpStartedCallback);
            }
        }

        public boolean isObsoleteLocked() {
            return this.mDestroyed;
        }

        public final void logFinalValues() {
            int[] iArr;
            synchronized (this.mCounterLock) {
                for (int i : LOGGED_AP_OPS) {
                    int i2 = this.mAcceptedOps.get(i);
                    int i3 = this.mRejectedOps.get(i);
                    if (i2 > 0 || i3 > 0) {
                        FrameworkStatsLog.write(256, this.mProcessRecord.uid, i, modeToEnum(this.mAppOpModes.get(i)), i2, i3);
                    }
                }
            }
        }
    }

    public final void cancelForegroundNotificationLocked(ServiceRecord serviceRecord) {
        if (serviceRecord.foregroundNoti != null) {
            ServiceMap serviceMapLocked = getServiceMapLocked(serviceRecord.userId);
            if (serviceMapLocked != null) {
                for (int size = serviceMapLocked.mServicesByInstanceName.size() - 1; size >= 0; size--) {
                    ServiceRecord valueAt = serviceMapLocked.mServicesByInstanceName.valueAt(size);
                    if (valueAt != serviceRecord && valueAt.isForeground && valueAt.foregroundId == serviceRecord.foregroundId && valueAt.packageName.equals(serviceRecord.packageName)) {
                        return;
                    }
                }
            }
            serviceRecord.cancelNotification();
        }
    }

    public final void updateServiceForegroundLocked(ProcessServiceRecord processServiceRecord, boolean z) {
        int i = 0;
        boolean z2 = false;
        boolean z3 = false;
        for (int numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1; numberOfRunningServices >= 0; numberOfRunningServices--) {
            ServiceRecord runningServiceAt = processServiceRecord.getRunningServiceAt(numberOfRunningServices);
            if (runningServiceAt.isForeground || runningServiceAt.fgRequired) {
                int i2 = runningServiceAt.foregroundServiceType;
                int i3 = i | i2;
                if (i2 == 0) {
                    z2 = true;
                    z3 = true;
                } else {
                    z3 = true;
                }
                i = i3;
            }
        }
        this.mAm.updateProcessForegroundLocked(processServiceRecord.mApp, z3, i, z2, z);
        processServiceRecord.setHasReportedForegroundServices(z3);
    }

    public void unscheduleShortFgsTimeoutLocked(ServiceRecord serviceRecord) {
        this.mAm.mHandler.removeMessages(78, serviceRecord);
        this.mAm.mHandler.removeMessages(77, serviceRecord);
        this.mAm.mHandler.removeMessages(76, serviceRecord);
    }

    public final void maybeUpdateShortFgsTrackingLocked(ServiceRecord serviceRecord, boolean z) {
        if (!serviceRecord.isShortFgs()) {
            serviceRecord.clearShortFgsInfo();
            unscheduleShortFgsTimeoutLocked(serviceRecord);
            return;
        }
        boolean hasShortFgsInfo = serviceRecord.hasShortFgsInfo();
        if (z || !hasShortFgsInfo) {
            if (hasShortFgsInfo) {
                Slog.i("ActivityManager", "Extending SHORT_SERVICE time out: " + serviceRecord);
            } else {
                Slog.i("ActivityManager", "Short FGS started: " + serviceRecord);
            }
            serviceRecord.setShortFgsInfo(SystemClock.uptimeMillis());
            unscheduleShortFgsTimeoutLocked(serviceRecord);
            this.mAm.mHandler.sendMessageAtTime(this.mAm.mHandler.obtainMessage(76, serviceRecord), serviceRecord.getShortFgsInfo().getTimeoutTime());
            return;
        }
        Slog.w("ActivityManager", "NOT extending SHORT_SERVICE time out: " + serviceRecord);
        serviceRecord.getShortFgsInfo().update();
    }

    public final void maybeStopShortFgsTimeoutLocked(ServiceRecord serviceRecord) {
        serviceRecord.clearShortFgsInfo();
        if (serviceRecord.isShortFgs()) {
            Slog.i("ActivityManager", "Stop short FGS timeout: " + serviceRecord);
            unscheduleShortFgsTimeoutLocked(serviceRecord);
        }
    }

    public void onShortFgsTimeout(ServiceRecord serviceRecord) {
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (!serviceRecord.shouldTriggerShortFgsTimeout()) {
                    Slog.d("ActivityManager", "[STALE] Short FGS timed out: " + serviceRecord);
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                Slog.e("ActivityManager", "Short FGS timed out: " + serviceRecord);
                try {
                    serviceRecord.app.getThread().scheduleTimeoutService(serviceRecord, serviceRecord.getShortFgsInfo().getStartId());
                } catch (RemoteException e) {
                    Slog.w("ActivityManager", "Exception from scheduleTimeoutService: " + e.toString());
                }
                this.mAm.mHandler.sendMessageAtTime(this.mAm.mHandler.obtainMessage(77, serviceRecord), serviceRecord.getShortFgsInfo().getProcStateDemoteTime());
                this.mAm.mHandler.sendMessageAtTime(this.mAm.mHandler.obtainMessage(78, serviceRecord), serviceRecord.getShortFgsInfo().getAnrTime());
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public boolean shouldServiceTimeOutLocked(ComponentName componentName, IBinder iBinder) {
        int callingUserId = UserHandle.getCallingUserId();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ServiceRecord findServiceLocked = findServiceLocked(componentName, iBinder, callingUserId);
            if (findServiceLocked != null) {
                return findServiceLocked.shouldTriggerShortFgsTimeout();
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onShortFgsProcstateTimeout(ServiceRecord serviceRecord) {
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (!serviceRecord.shouldDemoteShortFgsProcState()) {
                    Slog.d("ActivityManager", "[STALE] Short FGS procstate demotion: " + serviceRecord);
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                Slog.e("ActivityManager", "Short FGS procstate demoted: " + serviceRecord);
                this.mAm.updateOomAdjLocked(serviceRecord.app, 13);
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void onShortFgsAnrTimeout(ServiceRecord serviceRecord) {
        TimeoutRecord forShortFgsTimeout = TimeoutRecord.forShortFgsTimeout("A foreground service of FOREGROUND_SERVICE_TYPE_SHORT_SERVICE did not stop within a timeout: " + serviceRecord.getComponentName());
        forShortFgsTimeout.mLatencyTracker.waitingOnAMSLockStarted();
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                forShortFgsTimeout.mLatencyTracker.waitingOnAMSLockEnded();
                if (!serviceRecord.shouldTriggerShortFgsAnr()) {
                    Slog.d("ActivityManager", "[STALE] Short FGS ANR'ed: " + serviceRecord);
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return;
                }
                Slog.wtf("ActivityManager", "Short FGS ANR'ed: " + serviceRecord);
                this.mAm.appNotResponding(serviceRecord.app, forShortFgsTimeout);
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final void updateAllowlistManagerLocked(ProcessServiceRecord processServiceRecord) {
        processServiceRecord.mAllowlistManager = false;
        for (int numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1; numberOfRunningServices >= 0; numberOfRunningServices--) {
            if (processServiceRecord.getRunningServiceAt(numberOfRunningServices).allowlistManager) {
                processServiceRecord.mAllowlistManager = true;
                return;
            }
        }
    }

    public final void stopServiceAndUpdateAllowlistManagerLocked(ServiceRecord serviceRecord) {
        maybeStopShortFgsTimeoutLocked(serviceRecord);
        ProcessServiceRecord processServiceRecord = serviceRecord.app.mServices;
        processServiceRecord.stopService(serviceRecord);
        processServiceRecord.updateBoundClientUids();
        if (serviceRecord.allowlistManager) {
            updateAllowlistManagerLocked(processServiceRecord);
        }
    }

    public void updateServiceConnectionActivitiesLocked(ProcessServiceRecord processServiceRecord) {
        ArraySet arraySet = null;
        for (int i = 0; i < processServiceRecord.numberOfConnections(); i++) {
            ProcessRecord processRecord = processServiceRecord.getConnectionAt(i).binding.service.app;
            if (processRecord != null && processRecord != processServiceRecord.mApp) {
                if (arraySet == null) {
                    arraySet = new ArraySet();
                } else if (arraySet.contains(processRecord)) {
                }
                arraySet.add(processRecord);
                updateServiceClientActivitiesLocked(processRecord.mServices, null, false);
            }
        }
    }

    public final boolean updateServiceClientActivitiesLocked(ProcessServiceRecord processServiceRecord, ConnectionRecord connectionRecord, boolean z) {
        ProcessRecord processRecord;
        if (connectionRecord == null || (processRecord = connectionRecord.binding.client) == null || processRecord.hasActivities()) {
            boolean z2 = false;
            for (int numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1; numberOfRunningServices >= 0 && !z2; numberOfRunningServices--) {
                ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = processServiceRecord.getRunningServiceAt(numberOfRunningServices).getConnections();
                for (int size = connections.size() - 1; size >= 0 && !z2; size--) {
                    ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
                    int size2 = valueAt.size() - 1;
                    while (true) {
                        if (size2 < 0) {
                            break;
                        }
                        ProcessRecord processRecord2 = valueAt.get(size2).binding.client;
                        if (processRecord2 != null && processRecord2 != processServiceRecord.mApp && processRecord2.hasActivities()) {
                            z2 = true;
                            break;
                        }
                        size2--;
                    }
                }
            }
            if (z2 != processServiceRecord.hasClientActivities()) {
                processServiceRecord.setHasClientActivities(z2);
                if (z) {
                    this.mAm.updateLruProcessLocked(processServiceRecord.mApp, z2, null);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:116:0x02b8 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:119:0x02ca  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x02cd  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x02db  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x02de  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x035e A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x0371 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:131:0x0376  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x037f A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:137:0x0389 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:140:0x0395 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x03b8 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:155:0x03c8 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:158:0x03dd A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:164:0x040b  */
    /* JADX WARN: Removed duplicated region for block: B:167:0x0428 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:187:0x0473  */
    /* JADX WARN: Removed duplicated region for block: B:189:0x0477 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:191:0x047e  */
    /* JADX WARN: Removed duplicated region for block: B:192:0x0480  */
    /* JADX WARN: Removed duplicated region for block: B:205:0x04d5 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:222:0x0546 A[Catch: all -> 0x0566, TryCatch #3 {all -> 0x0566, blocks: (B:98:0x026a, B:100:0x027e, B:102:0x028a, B:103:0x0290, B:114:0x02af, B:116:0x02b8, B:117:0x02c1, B:121:0x02cf, B:125:0x02e0, B:127:0x035e, B:128:0x0361, B:130:0x0371, B:132:0x0377, B:134:0x037f, B:135:0x0381, B:137:0x0389, B:138:0x038c, B:140:0x0395, B:141:0x0397, B:143:0x039c, B:145:0x03a0, B:147:0x03a6, B:149:0x03ae, B:150:0x03b4, B:152:0x03b8, B:153:0x03be, B:155:0x03c8, B:156:0x03d2, B:158:0x03dd, B:160:0x03ff, B:165:0x040c, B:167:0x0428, B:169:0x0432, B:171:0x0438, B:173:0x043c, B:174:0x043e, B:176:0x0448, B:186:0x0465, B:189:0x0477, B:193:0x0481, B:195:0x0494, B:203:0x04a3, B:205:0x04d5, B:207:0x04db, B:211:0x04e4, B:214:0x04ee, B:215:0x0527, B:217:0x0532, B:219:0x0538, B:223:0x054a, B:210:0x04e2, B:220:0x053e, B:222:0x0546, B:180:0x0452, B:182:0x045c, B:104:0x0291, B:106:0x0297, B:108:0x02a9), top: B:233:0x026a }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0083  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0151  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0163  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0174  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0176  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0191  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0194  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x019e  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x01a1  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x01a5  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x01df A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x01e1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int bindServiceLocked(IApplicationThread iApplicationThread, IBinder iBinder, Intent intent, String str, IServiceConnection iServiceConnection, long j, String str2, boolean z, int i, String str3, IApplicationThread iApplicationThread2, String str4, int i2) throws TransactionTooLargeException {
        ActivityServiceConnectionsHolder activityServiceConnectionsHolder;
        Intent intent2;
        PendingIntent pendingIntent;
        int i3;
        int i4;
        ServiceLookupResult retrieveServiceLocked;
        boolean z2;
        boolean z3;
        ConnectionRecord connectionRecord;
        boolean z4;
        ProcessRecord processRecord;
        int i5;
        ProcessRecord processRecord2;
        ArrayList<ConnectionRecord> arrayList;
        boolean z5;
        ProcessRecord processRecord3;
        ProcessRecord processRecord4;
        ProcessRecord processRecord5;
        IntentBindRecord intentBindRecord;
        boolean z6;
        boolean z7;
        ProcessStateRecord processStateRecord;
        Intent intent3 = intent;
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        ProcessRecord recordForAppLOSP = this.mAm.getRecordForAppLOSP(iApplicationThread);
        if (recordForAppLOSP == null) {
            throw new SecurityException("Unable to find app for caller " + iApplicationThread + " (pid=" + callingPid + ") when binding service " + intent3);
        }
        if (iBinder != null) {
            ActivityServiceConnectionsHolder serviceConnectionsHolder = this.mAm.mAtmInternal.getServiceConnectionsHolder(iBinder);
            if (serviceConnectionsHolder == null) {
                Slog.w("ActivityManager", "Binding with unknown activity: " + iBinder);
                return 0;
            }
            activityServiceConnectionsHolder = serviceConnectionsHolder;
        } else {
            activityServiceConnectionsHolder = null;
        }
        boolean z8 = recordForAppLOSP.info.uid == 1000;
        if (z8) {
            intent3.setDefusable(true);
            PendingIntent pendingIntent2 = (PendingIntent) intent3.getParcelableExtra("android.intent.extra.client_intent");
            if (pendingIntent2 != null) {
                int intExtra = intent3.getIntExtra("android.intent.extra.client_label", 0);
                if (intExtra != 0) {
                    intent3 = intent.cloneFilter();
                }
                intent2 = intent3;
                pendingIntent = pendingIntent2;
                i3 = intExtra;
                if ((j & 134217728) != 0) {
                    this.mAm.enforceCallingPermission("android.permission.MANAGE_ACTIVITY_TASKS", "BIND_TREAT_LIKE_ACTIVITY");
                }
                if ((j & 524288) == 0 && !z8) {
                    throw new SecurityException("Non-system caller (pid=" + callingPid + ") set BIND_SCHEDULE_LIKE_TOP_APP when binding service " + intent2);
                } else if ((j & 16777216) == 0 && !z8) {
                    throw new SecurityException("Non-system caller " + iApplicationThread + " (pid=" + callingPid + ") set BIND_ALLOW_WHITELIST_MANAGEMENT when binding service " + intent2);
                } else {
                    i4 = ((j & 4194304) > 0L ? 1 : ((j & 4194304) == 0L ? 0 : -1));
                    if (i4 == 0 && !z8) {
                        throw new SecurityException("Non-system caller " + iApplicationThread + " (pid=" + callingPid + ") set BIND_ALLOW_INSTANT when binding service " + intent2);
                    } else if ((j & 65536) == 0 && !z8) {
                        throw new SecurityException("Non-system caller (pid=" + callingPid + ") set BIND_ALMOST_PERCEPTIBLE when binding service " + intent2);
                    } else {
                        if ((j & 1048576) != 0) {
                            this.mAm.enforceCallingPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", "BIND_ALLOW_BACKGROUND_ACTIVITY_STARTS");
                        }
                        if ((j & 262144) != 0) {
                            this.mAm.enforceCallingPermission("android.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND", "BIND_ALLOW_FOREGROUND_SERVICE_STARTS_FROM_BACKGROUND");
                        }
                        boolean z9 = recordForAppLOSP.mState.getSetSchedGroup() == 0;
                        boolean z10 = (j & Integer.toUnsignedLong(Integer.MIN_VALUE)) == 0 || (j & 4611686018427387904L) != 0;
                        boolean z11 = i4 == 0;
                        boolean z12 = (j & 8192) == 0;
                        ProcessRecord recordForAppLOSP2 = i > 0 ? this.mAm.getRecordForAppLOSP(iApplicationThread2) : null;
                        boolean z13 = z9;
                        Intent intent4 = intent2;
                        ActivityServiceConnectionsHolder activityServiceConnectionsHolder2 = activityServiceConnectionsHolder;
                        retrieveServiceLocked = retrieveServiceLocked(intent2, str2, z, i, str3, str, str4, callingPid, callingUid, i2, true, z13, z10, z11, null, z12);
                        if (retrieveServiceLocked != null) {
                            return 0;
                        }
                        ServiceRecord serviceRecord = retrieveServiceLocked.record;
                        if (serviceRecord == null) {
                            return -1;
                        }
                        AppBindRecord retrieveAppBindingLocked = serviceRecord.retrieveAppBindingLocked(intent4, recordForAppLOSP, recordForAppLOSP2);
                        ProcessServiceRecord processServiceRecord = retrieveAppBindingLocked.client.mServices;
                        if (processServiceRecord.numberOfConnections() >= this.mAm.mConstants.mMaxServiceConnectionsPerProcess) {
                            Slog.w("ActivityManager", "bindService exceeded max service connection number per process, callerApp:" + recordForAppLOSP.processName + " intent:" + intent4);
                            return 0;
                        }
                        boolean deferServiceBringupIfFrozenLocked = deferServiceBringupIfFrozenLocked(serviceRecord, intent4, str4, null, callingUid, callingPid, false, z13, i2, BackgroundStartPrivileges.NONE, true, iServiceConnection);
                        boolean z14 = (deferServiceBringupIfFrozenLocked || requestStartTargetPermissionsReviewIfNeededLocked(serviceRecord, str4, null, callingUid, intent4, z13, i2, true, iServiceConnection)) ? false : true;
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        try {
                            unscheduleServiceRestartLocked(serviceRecord, recordForAppLOSP.info.uid, false);
                            if ((j & 1) != 0) {
                                serviceRecord.lastActivity = SystemClock.uptimeMillis();
                                if (!serviceRecord.hasAutoCreateConnections()) {
                                    synchronized (this.mAm.mProcessStats.mLock) {
                                        ServiceState tracker = serviceRecord.getTracker();
                                        if (tracker != null) {
                                            z2 = true;
                                            tracker.setBound(true, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                                        } else {
                                            z2 = true;
                                        }
                                    }
                                    if ((j & 2097152) != 0) {
                                        this.mAm.requireAllowedAssociationsLocked(serviceRecord.appInfo.packageName);
                                    }
                                    boolean z15 = (serviceRecord.appInfo.flags & 2097152) == 0 ? z2 : false;
                                    z3 = serviceRecord.startRequested;
                                    boolean z16 = serviceRecord.getConnections().isEmpty() ? z2 : false;
                                    ActivityManagerService activityManagerService = this.mAm;
                                    int i6 = recordForAppLOSP.uid;
                                    String str5 = recordForAppLOSP.processName;
                                    int curProcState = recordForAppLOSP.mState.getCurProcState();
                                    ApplicationInfo applicationInfo = serviceRecord.appInfo;
                                    activityManagerService.startAssociationLocked(i6, str5, curProcState, applicationInfo.uid, applicationInfo.longVersionCode, serviceRecord.instanceName, serviceRecord.processName);
                                    this.mAm.grantImplicitAccess(recordForAppLOSP.userId, intent4, recordForAppLOSP.uid, UserHandle.getAppId(serviceRecord.appInfo.uid));
                                    connectionRecord = new ConnectionRecord(retrieveAppBindingLocked, activityServiceConnectionsHolder2, iServiceConnection, j, i3, pendingIntent, recordForAppLOSP.uid, recordForAppLOSP.processName, str4, retrieveServiceLocked.aliasComponent);
                                    IBinder asBinder = iServiceConnection.asBinder();
                                    serviceRecord.addConnection(asBinder, connectionRecord);
                                    retrieveAppBindingLocked.connections.add(connectionRecord);
                                    if (activityServiceConnectionsHolder2 != null) {
                                        activityServiceConnectionsHolder2.addConnection(connectionRecord);
                                    }
                                    processServiceRecord.addConnection(connectionRecord);
                                    connectionRecord.startAssociationIfNeeded();
                                    if (connectionRecord.hasFlag(8)) {
                                        z4 = true;
                                    } else {
                                        z4 = true;
                                        processServiceRecord.setHasAboveClient(true);
                                    }
                                    if (connectionRecord.hasFlag(16777216)) {
                                        serviceRecord.allowlistManager = z4;
                                    }
                                    if (connectionRecord.hasFlag(1048576)) {
                                        serviceRecord.setAllowedBgActivityStartsByBinding(z4);
                                    }
                                    if (connectionRecord.hasFlag(32768)) {
                                        serviceRecord.isNotAppComponentUsage = z4;
                                    }
                                    processRecord = serviceRecord.app;
                                    i5 = 2;
                                    if (processRecord != null && (processStateRecord = processRecord.mState) != null && processStateRecord.getCurProcState() <= 2 && connectionRecord.hasFlag(65536)) {
                                        serviceRecord.lastTopAlmostPerceptibleBindRequestUptimeMs = SystemClock.uptimeMillis();
                                    }
                                    processRecord2 = serviceRecord.app;
                                    if (processRecord2 != null) {
                                        updateServiceClientActivitiesLocked(processRecord2.mServices, connectionRecord, true);
                                    }
                                    arrayList = this.mServiceConnections.get(asBinder);
                                    if (arrayList == null) {
                                        arrayList = new ArrayList<>();
                                        this.mServiceConnections.put(asBinder, arrayList);
                                    }
                                    arrayList.add(connectionRecord);
                                    if (connectionRecord.hasFlag(1)) {
                                        z5 = false;
                                    } else {
                                        serviceRecord.lastActivity = SystemClock.uptimeMillis();
                                        if (bringUpServiceLocked(serviceRecord, intent4.getFlags(), z13, false, z14, deferServiceBringupIfFrozenLocked, true) != null) {
                                            this.mAm.updateOomAdjPendingTargetsLocked(4);
                                            Binder.restoreCallingIdentity(clearCallingIdentity);
                                            return 0;
                                        }
                                        z5 = true;
                                    }
                                    setFgsRestrictionLocked(str4, callingPid, callingUid, intent4, serviceRecord, i2, BackgroundStartPrivileges.NONE, true);
                                    processRecord3 = serviceRecord.app;
                                    if (processRecord3 == null) {
                                        ProcessServiceRecord processServiceRecord2 = processRecord3.mServices;
                                        if (connectionRecord.hasFlag(134217728)) {
                                            z6 = true;
                                            processServiceRecord2.setTreatLikeActivity(true);
                                        } else {
                                            z6 = true;
                                        }
                                        if (serviceRecord.allowlistManager) {
                                            processServiceRecord2.mAllowlistManager = z6;
                                        }
                                        ActivityManagerService activityManagerService2 = this.mAm;
                                        ProcessRecord processRecord6 = serviceRecord.app;
                                        if (recordForAppLOSP.hasActivitiesOrRecentTasks() && processServiceRecord2.hasClientActivities()) {
                                            processRecord4 = recordForAppLOSP;
                                            z7 = true;
                                            activityManagerService2.updateLruProcessLocked(processRecord6, z7, retrieveAppBindingLocked.client);
                                            this.mAm.enqueueOomAdjTargetLocked(serviceRecord.app);
                                            z5 = true;
                                        }
                                        processRecord4 = recordForAppLOSP;
                                        if (processRecord4.mState.getCurProcState() > 2 || !connectionRecord.hasFlag(134217728)) {
                                            z7 = false;
                                            activityManagerService2.updateLruProcessLocked(processRecord6, z7, retrieveAppBindingLocked.client);
                                            this.mAm.enqueueOomAdjTargetLocked(serviceRecord.app);
                                            z5 = true;
                                        }
                                        z7 = true;
                                        activityManagerService2.updateLruProcessLocked(processRecord6, z7, retrieveAppBindingLocked.client);
                                        this.mAm.enqueueOomAdjTargetLocked(serviceRecord.app);
                                        z5 = true;
                                    } else {
                                        processRecord4 = recordForAppLOSP;
                                    }
                                    if (z5) {
                                        this.mAm.updateOomAdjPendingTargetsLocked(4);
                                    }
                                    int i7 = !z15 ? 2 : 1;
                                    int i8 = serviceRecord.appInfo.uid;
                                    String shortAction = ActivityManagerService.getShortAction(intent4.getAction());
                                    processRecord5 = serviceRecord.app;
                                    if (processRecord5 != null && processRecord5.getThread() != null) {
                                        if (!z3 && !z16) {
                                            i5 = 1;
                                        }
                                        FrameworkStatsLog.write((int) FrameworkStatsLog.SERVICE_REQUEST_EVENT_REPORTED, i8, callingUid, shortAction, 2, false, i5, getShortProcessNameForStats(callingUid, processRecord4.processName), getShortServiceNameForStats(serviceRecord), i7, serviceRecord.packageName, processRecord4.info.packageName);
                                        if (serviceRecord.app != null) {
                                            IntentBindRecord intentBindRecord2 = retrieveAppBindingLocked.intent;
                                            if (intentBindRecord2.received) {
                                                ComponentName componentName = retrieveServiceLocked.aliasComponent;
                                                if (componentName == null) {
                                                    componentName = serviceRecord.name;
                                                }
                                                try {
                                                    connectionRecord.conn.connected(componentName, intentBindRecord2.binder, false);
                                                } catch (Exception e) {
                                                    Slog.w("ActivityManager", "Failure sending service " + serviceRecord.shortInstanceName + " to connection " + connectionRecord.conn.asBinder() + " (in " + connectionRecord.binding.client.processName + ")", e);
                                                }
                                                if (retrieveAppBindingLocked.intent.apps.size() == 1) {
                                                    IntentBindRecord intentBindRecord3 = retrieveAppBindingLocked.intent;
                                                    if (intentBindRecord3.doRebind) {
                                                        requestServiceBindingLocked(serviceRecord, intentBindRecord3, z13, true);
                                                    }
                                                }
                                                maybeLogBindCrossProfileService(i2, str4, processRecord4.info.uid);
                                                getServiceMapLocked(serviceRecord.userId).ensureNotStartingBackgroundLocked(serviceRecord);
                                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                                notifyBindingServiceEventLocked(processRecord4, str4);
                                                return 1;
                                            }
                                        }
                                        intentBindRecord = retrieveAppBindingLocked.intent;
                                        if (!intentBindRecord.requested) {
                                            requestServiceBindingLocked(serviceRecord, intentBindRecord, z13, false);
                                        }
                                        maybeLogBindCrossProfileService(i2, str4, processRecord4.info.uid);
                                        getServiceMapLocked(serviceRecord.userId).ensureNotStartingBackgroundLocked(serviceRecord);
                                        Binder.restoreCallingIdentity(clearCallingIdentity);
                                        notifyBindingServiceEventLocked(processRecord4, str4);
                                        return 1;
                                    }
                                    i5 = 3;
                                    FrameworkStatsLog.write((int) FrameworkStatsLog.SERVICE_REQUEST_EVENT_REPORTED, i8, callingUid, shortAction, 2, false, i5, getShortProcessNameForStats(callingUid, processRecord4.processName), getShortServiceNameForStats(serviceRecord), i7, serviceRecord.packageName, processRecord4.info.packageName);
                                    if (serviceRecord.app != null) {
                                    }
                                    intentBindRecord = retrieveAppBindingLocked.intent;
                                    if (!intentBindRecord.requested) {
                                    }
                                    maybeLogBindCrossProfileService(i2, str4, processRecord4.info.uid);
                                    getServiceMapLocked(serviceRecord.userId).ensureNotStartingBackgroundLocked(serviceRecord);
                                    Binder.restoreCallingIdentity(clearCallingIdentity);
                                    notifyBindingServiceEventLocked(processRecord4, str4);
                                    return 1;
                                }
                            }
                            z2 = true;
                            if ((j & 2097152) != 0) {
                            }
                            if ((serviceRecord.appInfo.flags & 2097152) == 0) {
                            }
                            z3 = serviceRecord.startRequested;
                            if (serviceRecord.getConnections().isEmpty()) {
                            }
                            ActivityManagerService activityManagerService3 = this.mAm;
                            int i62 = recordForAppLOSP.uid;
                            String str52 = recordForAppLOSP.processName;
                            int curProcState2 = recordForAppLOSP.mState.getCurProcState();
                            ApplicationInfo applicationInfo2 = serviceRecord.appInfo;
                            activityManagerService3.startAssociationLocked(i62, str52, curProcState2, applicationInfo2.uid, applicationInfo2.longVersionCode, serviceRecord.instanceName, serviceRecord.processName);
                            this.mAm.grantImplicitAccess(recordForAppLOSP.userId, intent4, recordForAppLOSP.uid, UserHandle.getAppId(serviceRecord.appInfo.uid));
                            connectionRecord = new ConnectionRecord(retrieveAppBindingLocked, activityServiceConnectionsHolder2, iServiceConnection, j, i3, pendingIntent, recordForAppLOSP.uid, recordForAppLOSP.processName, str4, retrieveServiceLocked.aliasComponent);
                            IBinder asBinder2 = iServiceConnection.asBinder();
                            serviceRecord.addConnection(asBinder2, connectionRecord);
                            retrieveAppBindingLocked.connections.add(connectionRecord);
                            if (activityServiceConnectionsHolder2 != null) {
                            }
                            processServiceRecord.addConnection(connectionRecord);
                            connectionRecord.startAssociationIfNeeded();
                            if (connectionRecord.hasFlag(8)) {
                            }
                            if (connectionRecord.hasFlag(16777216)) {
                            }
                            if (connectionRecord.hasFlag(1048576)) {
                            }
                            if (connectionRecord.hasFlag(32768)) {
                            }
                            processRecord = serviceRecord.app;
                            i5 = 2;
                            if (processRecord != null) {
                                serviceRecord.lastTopAlmostPerceptibleBindRequestUptimeMs = SystemClock.uptimeMillis();
                            }
                            processRecord2 = serviceRecord.app;
                            if (processRecord2 != null) {
                            }
                            arrayList = this.mServiceConnections.get(asBinder2);
                            if (arrayList == null) {
                            }
                            arrayList.add(connectionRecord);
                            if (connectionRecord.hasFlag(1)) {
                            }
                            setFgsRestrictionLocked(str4, callingPid, callingUid, intent4, serviceRecord, i2, BackgroundStartPrivileges.NONE, true);
                            processRecord3 = serviceRecord.app;
                            if (processRecord3 == null) {
                            }
                            if (z5) {
                            }
                            if (!z15) {
                            }
                            int i82 = serviceRecord.appInfo.uid;
                            String shortAction2 = ActivityManagerService.getShortAction(intent4.getAction());
                            processRecord5 = serviceRecord.app;
                            if (processRecord5 != null) {
                                if (!z3) {
                                    i5 = 1;
                                }
                                FrameworkStatsLog.write((int) FrameworkStatsLog.SERVICE_REQUEST_EVENT_REPORTED, i82, callingUid, shortAction2, 2, false, i5, getShortProcessNameForStats(callingUid, processRecord4.processName), getShortServiceNameForStats(serviceRecord), i7, serviceRecord.packageName, processRecord4.info.packageName);
                                if (serviceRecord.app != null) {
                                }
                                intentBindRecord = retrieveAppBindingLocked.intent;
                                if (!intentBindRecord.requested) {
                                }
                                maybeLogBindCrossProfileService(i2, str4, processRecord4.info.uid);
                                getServiceMapLocked(serviceRecord.userId).ensureNotStartingBackgroundLocked(serviceRecord);
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                                notifyBindingServiceEventLocked(processRecord4, str4);
                                return 1;
                            }
                            i5 = 3;
                            FrameworkStatsLog.write((int) FrameworkStatsLog.SERVICE_REQUEST_EVENT_REPORTED, i82, callingUid, shortAction2, 2, false, i5, getShortProcessNameForStats(callingUid, processRecord4.processName), getShortServiceNameForStats(serviceRecord), i7, serviceRecord.packageName, processRecord4.info.packageName);
                            if (serviceRecord.app != null) {
                            }
                            intentBindRecord = retrieveAppBindingLocked.intent;
                            if (!intentBindRecord.requested) {
                            }
                            maybeLogBindCrossProfileService(i2, str4, processRecord4.info.uid);
                            getServiceMapLocked(serviceRecord.userId).ensureNotStartingBackgroundLocked(serviceRecord);
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            notifyBindingServiceEventLocked(processRecord4, str4);
                            return 1;
                        } catch (Throwable th) {
                            Binder.restoreCallingIdentity(clearCallingIdentity);
                            throw th;
                        }
                    }
                }
            }
            intent2 = intent3;
            pendingIntent = pendingIntent2;
        } else {
            intent2 = intent3;
            pendingIntent = null;
        }
        i3 = 0;
        if ((j & 134217728) != 0) {
        }
        if ((j & 524288) == 0) {
        }
        if ((j & 16777216) == 0) {
        }
        i4 = ((j & 4194304) > 0L ? 1 : ((j & 4194304) == 0L ? 0 : -1));
        if (i4 == 0) {
        }
        if ((j & 65536) == 0) {
        }
        if ((j & 1048576) != 0) {
        }
        if ((j & 262144) != 0) {
        }
        if (recordForAppLOSP.mState.getSetSchedGroup() == 0) {
        }
        if ((j & Integer.toUnsignedLong(Integer.MIN_VALUE)) == 0) {
        }
        if (i4 == 0) {
        }
        if ((j & 8192) == 0) {
        }
        ProcessRecord recordForAppLOSP22 = i > 0 ? this.mAm.getRecordForAppLOSP(iApplicationThread2) : null;
        boolean z132 = z9;
        Intent intent42 = intent2;
        ActivityServiceConnectionsHolder activityServiceConnectionsHolder22 = activityServiceConnectionsHolder;
        retrieveServiceLocked = retrieveServiceLocked(intent2, str2, z, i, str3, str, str4, callingPid, callingUid, i2, true, z132, z10, z11, null, z12);
        if (retrieveServiceLocked != null) {
        }
    }

    @GuardedBy({"mAm"})
    public final void notifyBindingServiceEventLocked(ProcessRecord processRecord, String str) {
        ApplicationInfo applicationInfo = processRecord.info;
        if (applicationInfo != null) {
            str = applicationInfo.packageName;
        }
        if (str != null) {
            this.mAm.mHandler.obtainMessage(75, processRecord.uid, 0, str).sendToTarget();
        }
    }

    public final void maybeLogBindCrossProfileService(int i, String str, int i2) {
        int callingUserId;
        if (UserHandle.isCore(i2) || (callingUserId = UserHandle.getCallingUserId()) == i || !this.mAm.mUserController.isSameProfileGroup(callingUserId, i)) {
            return;
        }
        DevicePolicyEventLogger.createEvent((int) FrameworkStatsLog.DEVICE_POLICY_EVENT__EVENT_ID__BIND_CROSS_PROFILE_SERVICE).setStrings(new String[]{str}).write();
    }

    public void publishServiceLocked(ServiceRecord serviceRecord, Intent intent, IBinder iBinder) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        if (serviceRecord != null) {
            try {
                Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent);
                IntentBindRecord intentBindRecord = serviceRecord.bindings.get(filterComparison);
                if (intentBindRecord != null && !intentBindRecord.received) {
                    intentBindRecord.binder = iBinder;
                    intentBindRecord.requested = true;
                    intentBindRecord.received = true;
                    ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = serviceRecord.getConnections();
                    for (int size = connections.size() - 1; size >= 0; size--) {
                        ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
                        for (int i = 0; i < valueAt.size(); i++) {
                            ConnectionRecord connectionRecord = valueAt.get(i);
                            if (filterComparison.equals(connectionRecord.binding.intent.intent)) {
                                ComponentName componentName = connectionRecord.aliasComponent;
                                if (componentName == null) {
                                    componentName = serviceRecord.name;
                                }
                                try {
                                    connectionRecord.conn.connected(componentName, iBinder, false);
                                } catch (Exception e) {
                                    Slog.w("ActivityManager", "Failure sending service " + serviceRecord.shortInstanceName + " to connection " + connectionRecord.conn.asBinder() + " (in " + connectionRecord.binding.client.processName + ")", e);
                                }
                            }
                        }
                    }
                }
                serviceDoneExecutingLocked(serviceRecord, this.mDestroyingServices.contains(serviceRecord), false, false);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public void updateServiceGroupLocked(IServiceConnection iServiceConnection, int i, int i2) {
        ArrayList<ConnectionRecord> arrayList = this.mServiceConnections.get(iServiceConnection.asBinder());
        if (arrayList == null) {
            throw new IllegalArgumentException("Could not find connection for " + iServiceConnection.asBinder());
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ServiceRecord serviceRecord = arrayList.get(size).binding.service;
            if (serviceRecord != null && (serviceRecord.serviceInfo.flags & 2) != 0) {
                ProcessRecord processRecord = serviceRecord.app;
                if (processRecord != null) {
                    ProcessServiceRecord processServiceRecord = processRecord.mServices;
                    if (i > 0) {
                        processServiceRecord.setConnectionService(serviceRecord);
                        processServiceRecord.setConnectionGroup(i);
                        processServiceRecord.setConnectionImportance(i2);
                    } else {
                        processServiceRecord.setConnectionService(null);
                        processServiceRecord.setConnectionGroup(0);
                        processServiceRecord.setConnectionImportance(0);
                    }
                } else if (i > 0) {
                    serviceRecord.pendingConnectionGroup = i;
                    serviceRecord.pendingConnectionImportance = i2;
                } else {
                    serviceRecord.pendingConnectionGroup = 0;
                    serviceRecord.pendingConnectionImportance = 0;
                }
            }
        }
    }

    public boolean unbindServiceLocked(IServiceConnection iServiceConnection) {
        String num;
        IBinder asBinder = iServiceConnection.asBinder();
        ArrayList<ConnectionRecord> arrayList = this.mServiceConnections.get(asBinder);
        if (arrayList == null) {
            Slog.w("ActivityManager", "Unbind failed: could not find connection for " + iServiceConnection.asBinder());
            return false;
        }
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (Trace.isTagEnabled(64L)) {
                if (arrayList.size() > 0) {
                    ConnectionRecord connectionRecord = arrayList.get(0);
                    num = connectionRecord.binding.service.shortInstanceName + " from " + connectionRecord.clientProcessName;
                } else {
                    num = Integer.toString(callingPid);
                }
                Trace.traceBegin(64L, "unbindServiceLocked: " + num);
            }
            while (arrayList.size() > 0) {
                ConnectionRecord connectionRecord2 = arrayList.get(0);
                removeConnectionLocked(connectionRecord2, null, null, true);
                if (arrayList.size() > 0 && arrayList.get(0) == connectionRecord2) {
                    Slog.wtf("ActivityManager", "Connection " + connectionRecord2 + " not removed for binder " + asBinder);
                    arrayList.remove(0);
                }
                ProcessRecord processRecord = connectionRecord2.binding.service.app;
                if (processRecord != null) {
                    ProcessServiceRecord processServiceRecord = processRecord.mServices;
                    if (processServiceRecord.mAllowlistManager) {
                        updateAllowlistManagerLocked(processServiceRecord);
                    }
                    if (connectionRecord2.hasFlag(134217728)) {
                        processServiceRecord.setTreatLikeActivity(true);
                        this.mAm.updateLruProcessLocked(processRecord, true, null);
                    }
                    this.mAm.enqueueOomAdjTargetLocked(processRecord);
                }
            }
            this.mAm.updateOomAdjPendingTargetsLocked(5);
            return true;
        } finally {
            Trace.traceEnd(64L);
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void unbindFinishedLocked(ServiceRecord serviceRecord, Intent intent, boolean z) {
        boolean z2;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        if (serviceRecord != null) {
            try {
                IntentBindRecord intentBindRecord = serviceRecord.bindings.get(new Intent.FilterComparison(intent));
                boolean contains = this.mDestroyingServices.contains(serviceRecord);
                if (intentBindRecord != null) {
                    if (intentBindRecord.apps.size() > 0 && !contains) {
                        int size = intentBindRecord.apps.size() - 1;
                        while (true) {
                            if (size >= 0) {
                                ProcessRecord processRecord = intentBindRecord.apps.valueAt(size).client;
                                if (processRecord != null && processRecord.mState.getSetSchedGroup() != 0) {
                                    z2 = true;
                                    break;
                                }
                                size--;
                            } else {
                                z2 = false;
                                break;
                            }
                        }
                        try {
                            requestServiceBindingLocked(serviceRecord, intentBindRecord, z2, true);
                        } catch (TransactionTooLargeException unused) {
                        }
                    } else {
                        intentBindRecord.doRebind = true;
                    }
                }
                serviceDoneExecutingLocked(serviceRecord, contains, false, false);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final ServiceRecord findServiceLocked(ComponentName componentName, IBinder iBinder, int i) {
        ServiceRecord serviceByNameLocked = getServiceByNameLocked(componentName, i);
        if (serviceByNameLocked == iBinder) {
            return serviceByNameLocked;
        }
        return null;
    }

    /* renamed from: com.android.server.am.ActiveServices$ServiceLookupResult */
    /* loaded from: classes.dex */
    public final class ServiceLookupResult {
        public final ComponentName aliasComponent;
        public final String permission;
        public final ServiceRecord record;

        public ServiceLookupResult(ServiceRecord serviceRecord, ComponentName componentName) {
            this.record = serviceRecord;
            this.permission = null;
            this.aliasComponent = componentName;
        }

        public ServiceLookupResult(String str) {
            this.record = null;
            this.permission = str;
            this.aliasComponent = null;
        }
    }

    /* renamed from: com.android.server.am.ActiveServices$ServiceRestarter */
    /* loaded from: classes.dex */
    public class ServiceRestarter implements Runnable {
        public ServiceRecord mService;

        public ServiceRestarter() {
        }

        public void setService(ServiceRecord serviceRecord) {
            this.mService = serviceRecord;
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (ActiveServices.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ActiveServices.this.performServiceRestartLocked(this.mService);
                } catch (Throwable th) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }
    }

    public final ServiceLookupResult retrieveServiceLocked(Intent intent, String str, String str2, String str3, int i, int i2, int i3, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        return retrieveServiceLocked(intent, str, false, -1, null, str2, str3, i, i2, i3, z, z2, z3, z4, null, z5);
    }

    public final String generateAdditionalSeInfoFromService(Intent intent) {
        return (intent == null || intent.getAction() == null) ? "" : (intent.getAction().equals("android.service.voice.HotwordDetectionService") || intent.getAction().equals("android.service.voice.VisualQueryDetectionService") || intent.getAction().equals("android.service.wearable.WearableSensingService")) ? ":isolatedComputeApp" : "";
    }

    /* JADX WARN: Code restructure failed: missing block: B:83:0x0282, code lost:
        if ((r6.flags & 2) != 0) goto L79;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x028c, code lost:
        throw new java.lang.IllegalArgumentException("Service cannot be both sdk sandbox and isolated");
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:219:0x060b  */
    /* JADX WARN: Removed duplicated region for block: B:253:0x07cf A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x01ea  */
    /* JADX WARN: Type inference failed for: r0v46, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r0v53, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r0v63, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r10v1, types: [int] */
    /* JADX WARN: Type inference failed for: r10v11 */
    /* JADX WARN: Type inference failed for: r10v19 */
    /* JADX WARN: Type inference failed for: r10v2 */
    /* JADX WARN: Type inference failed for: r10v3, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r10v4 */
    /* JADX WARN: Type inference failed for: r10v5 */
    /* JADX WARN: Type inference failed for: r10v7 */
    /* JADX WARN: Type inference failed for: r10v8 */
    /* JADX WARN: Type inference failed for: r11v10 */
    /* JADX WARN: Type inference failed for: r11v11 */
    /* JADX WARN: Type inference failed for: r11v15 */
    /* JADX WARN: Type inference failed for: r11v16 */
    /* JADX WARN: Type inference failed for: r11v24 */
    /* JADX WARN: Type inference failed for: r11v3 */
    /* JADX WARN: Type inference failed for: r11v4, types: [int] */
    /* JADX WARN: Type inference failed for: r11v7 */
    /* JADX WARN: Type inference failed for: r11v8 */
    /* JADX WARN: Type inference failed for: r11v9, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r1v3, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v11, types: [com.android.server.am.ActivityManagerService] */
    /* JADX WARN: Type inference failed for: r2v2, types: [com.android.server.am.ComponentAliasResolver] */
    /* JADX WARN: Type inference failed for: r2v26, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v29, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v32, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v36, types: [java.lang.StringBuilder] */
    /* JADX WARN: Type inference failed for: r2v64, types: [com.android.server.am.ActivityManagerService] */
    /* JADX WARN: Type inference failed for: r3v32, types: [com.android.server.am.ActivityManagerService] */
    /* JADX WARN: Type inference failed for: r48v0, types: [com.android.server.am.ActiveServices] */
    /* JADX WARN: Type inference failed for: r7v10 */
    /* JADX WARN: Type inference failed for: r7v11 */
    /* JADX WARN: Type inference failed for: r7v22, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v9, types: [int] */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v19 */
    /* JADX WARN: Type inference failed for: r8v2, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v5 */
    /* JADX WARN: Type inference failed for: r8v6 */
    /* JADX WARN: Type inference failed for: r8v7, types: [int] */
    /* JADX WARN: Type inference failed for: r8v8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final ServiceLookupResult retrieveServiceLocked(Intent intent, String str, boolean z, int i, String str2, String str3, String str4, int i2, int i3, int i4, boolean z2, boolean z3, boolean z4, boolean z5, ForegroundServiceDelegationOptions foregroundServiceDelegationOptions, boolean z6) {
        ComponentName componentName;
        String str5;
        ServiceRecord serviceRecord;
        String str6;
        String str7;
        int i5;
        String str8;
        String str9;
        ServiceRecord serviceRecord2;
        int permissionToOpCode;
        String str10;
        long j;
        String str11;
        Object obj;
        String str12;
        String str13;
        ServiceInfo serviceInfo;
        String str14;
        String str15;
        String str16;
        ComponentName componentName2;
        ComponentName componentName3;
        int i6;
        String str17;
        ServiceInfo serviceInfo2;
        ServiceMap serviceMap;
        ServiceRecord serviceRecord3;
        boolean z7;
        int i7;
        String str18;
        Object obj2;
        ServiceMap serviceMap2;
        ApplicationInfo applicationInfo;
        ServiceRecord serviceRecord4;
        if (z && str == null) {
            throw new IllegalArgumentException("No instanceName provided for sdk sandbox process");
        }
        Object handleIncomingUser = this.mAm.mUserController.handleIncomingUser(i2, i3, i4, false, getAllowMode(intent, str4), "service", str4);
        ServiceMap serviceMapLocked = getServiceMapLocked(handleIncomingUser);
        ComponentAliasResolver.Resolution<ComponentName> resolveService = this.mAm.mComponentAliasResolver.resolveService(intent, str3, 0, handleIncomingUser, i3);
        if (str == null) {
            componentName = intent.getComponent();
        } else {
            ComponentName component = intent.getComponent();
            if (component == null) {
                throw new IllegalArgumentException("Can't use custom instance name '" + str + "' without expicit component in Intent");
            }
            componentName = new ComponentName(component.getPackageName(), component.getClassName() + XmlUtils.STRING_ARRAY_SEPARATOR + str);
        }
        ServiceRecord serviceRecord5 = componentName != null ? serviceMapLocked.mServicesByInstanceName.get(componentName) : null;
        if (serviceRecord5 == null && !z4 && str == null) {
            serviceRecord5 = serviceMapLocked.mServicesByIntent.get(new Intent.FilterComparison(intent));
        }
        ComponentName componentName4 = componentName;
        if (serviceRecord5 != null) {
            PackageManagerInternal packageManagerInternal = this.mAm.getPackageManagerInternal();
            str5 = XmlUtils.STRING_ARRAY_SEPARATOR;
            if (packageManagerInternal.filterAppAccess(serviceRecord5.packageName, i3, (int) handleIncomingUser)) {
                Slog.w("ActivityManager", "Unable to start service " + intent + " U=" + handleIncomingUser + ": not found");
                return null;
            } else if ((serviceRecord5.serviceInfo.flags & 4) != 0 && !str4.equals(serviceRecord5.packageName)) {
                str6 = " U=";
                serviceRecord = null;
                if (serviceRecord != null && foregroundServiceDelegationOptions != null) {
                    ServiceInfo serviceInfo3 = new ServiceInfo();
                    try {
                        applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(foregroundServiceDelegationOptions.mClientPackageName, 1024L, (int) handleIncomingUser);
                    } catch (RemoteException unused) {
                        applicationInfo = null;
                    }
                    if (applicationInfo == null) {
                        throw new SecurityException("startForegroundServiceDelegate failed, could not resolve client package " + str4);
                    } else if (applicationInfo.uid != foregroundServiceDelegationOptions.mClientUid) {
                        throw new SecurityException("startForegroundServiceDelegate failed, uid:" + applicationInfo.uid + " does not match clientUid:" + foregroundServiceDelegationOptions.mClientUid);
                    } else {
                        serviceInfo3.applicationInfo = applicationInfo;
                        serviceInfo3.packageName = applicationInfo.packageName;
                        serviceInfo3.mForegroundServiceType = foregroundServiceDelegationOptions.mForegroundServiceTypes;
                        serviceInfo3.processName = applicationInfo.processName;
                        ComponentName component2 = intent.getComponent();
                        serviceInfo3.name = component2.getClassName();
                        if (z2) {
                            Intent.FilterComparison filterComparison = new Intent.FilterComparison(intent.cloneFilter());
                            ServiceRestarter serviceRestarter = new ServiceRestarter();
                            String processNameForService = getProcessNameForService(serviceInfo3, component2, str4, null, false, false);
                            ActivityManagerService activityManagerService = this.mAm;
                            ApplicationInfo applicationInfo2 = serviceInfo3.applicationInfo;
                            serviceRecord4 = new ServiceRecord(activityManagerService, component2, component2, applicationInfo2.packageName, applicationInfo2.uid, filterComparison, serviceInfo3, z3, serviceRestarter, processNameForService, -1, null, false);
                            serviceRestarter.setService(serviceRecord4);
                            serviceMapLocked.mServicesByInstanceName.put(component2, serviceRecord4);
                            serviceMapLocked.mServicesByIntent.put(filterComparison, serviceRecord4);
                            serviceRecord4.mRecentCallingPackage = str4;
                            serviceRecord4.mRecentCallingUid = i3;
                        } else {
                            serviceRecord4 = serviceRecord;
                        }
                        StringBuilder sb = new StringBuilder();
                        ApplicationInfo applicationInfo3 = serviceRecord4.appInfo;
                        sb.append(applicationInfo3.seInfo);
                        sb.append(generateAdditionalSeInfoFromService(intent));
                        applicationInfo3.seInfo = sb.toString();
                        return new ServiceLookupResult(serviceRecord4, resolveService.getAlias());
                    }
                }
                String str19 = "Service lookup failed: ";
                if (serviceRecord == null) {
                    try {
                        str10 = "ActivityManager";
                        j = z5 ? 276825088 : 268436480;
                        str7 = " and ";
                        str11 = str6;
                        obj = "association not allowed between packages ";
                        str12 = str5;
                        try {
                            ResolveInfo resolveService2 = this.mAm.getPackageManagerInternal().resolveService(intent, str3, j, handleIncomingUser, i3);
                            serviceInfo = resolveService2 != null ? resolveService2.serviceInfo : null;
                            try {
                            } catch (RemoteException unused2) {
                                str8 = str4;
                                str9 = "Unable to start service ";
                                handleIncomingUser = obj;
                                i5 = i3;
                            }
                        } catch (RemoteException unused3) {
                            str8 = str4;
                            i5 = i3;
                            str13 = str10;
                        }
                    } catch (RemoteException unused4) {
                    }
                    if (serviceInfo == null) {
                        Slog.w(str10, "Unable to start service " + intent + str11 + handleIncomingUser + ": not found");
                        return null;
                    }
                    i5 = str10;
                    if (str != null && (serviceInfo.flags & 2) == 0 && !z) {
                        throw new IllegalArgumentException("Can't use instance name '" + str + "' with non-isolated non-sdk sandbox service '" + serviceInfo.name + "'");
                    }
                    ComponentName componentName5 = new ComponentName(serviceInfo.applicationInfo.packageName, serviceInfo.name);
                    ComponentName componentName6 = componentName4 != null ? componentName4 : componentName5;
                    ?? r3 = this.mAm;
                    String packageName = componentName6.getPackageName();
                    ?? r7 = serviceInfo.applicationInfo.uid;
                    str9 = i3;
                    str8 = str4;
                    if (!r3.validateAssociationAllowedLocked(str8, str9, packageName, r7)) {
                        ?? sb2 = new StringBuilder();
                        r7 = obj;
                        try {
                            sb2.append(r7);
                            sb2.append(str8);
                            packageName = str7;
                            sb2.append(packageName);
                            sb2.append(componentName6.getPackageName());
                            String sb3 = sb2.toString();
                            StringBuilder sb4 = new StringBuilder();
                            try {
                                sb4.append(str19);
                                sb4.append(sb3);
                                Slog.w((String) i5, sb4.toString());
                                return new ServiceLookupResult(sb3);
                            } catch (RemoteException unused5) {
                                str19 = str19;
                                str7 = packageName;
                                handleIncomingUser = r7;
                                i5 = str9;
                                str9 = i5;
                                serviceRecord2 = serviceRecord;
                                if (serviceRecord2 == null) {
                                }
                            }
                        } catch (RemoteException unused6) {
                            handleIncomingUser = r7;
                            i5 = str9;
                            str9 = i5;
                            serviceRecord2 = serviceRecord;
                            if (serviceRecord2 == null) {
                            }
                        }
                    } else {
                        ApplicationInfo applicationInfo4 = serviceInfo.applicationInfo;
                        obj = obj;
                        try {
                            String str20 = applicationInfo4.packageName;
                            int i8 = applicationInfo4.uid;
                            int i9 = serviceInfo.flags;
                            int i10 = i9 & 4;
                            str7 = str7;
                            try {
                                try {
                                    if (i10 == 0) {
                                        str14 = i5;
                                        str15 = str11;
                                        str16 = ": not found";
                                        if (z4) {
                                            throw new SecurityException("BIND_EXTERNAL_SERVICE failed, " + componentName6 + " is not an externalService");
                                        }
                                        componentName2 = componentName6;
                                        componentName3 = componentName5;
                                    } else if (z4) {
                                        str14 = i5;
                                        if (!serviceInfo.exported) {
                                            throw new SecurityException("BIND_EXTERNAL_SERVICE failed, " + componentName5 + " is not exported");
                                        } else if ((i9 & 2) == 0) {
                                            throw new SecurityException("BIND_EXTERNAL_SERVICE failed, " + componentName5 + " is not an isolatedProcess");
                                        } else if (!this.mAm.getPackageManagerInternal().isSameApp(str8, str9, handleIncomingUser)) {
                                            throw new SecurityException("BIND_EXTERNAL_SERVICE failed, calling package not owned by calling UID ");
                                        } else {
                                            str15 = str11;
                                            str16 = ": not found";
                                            ApplicationInfo applicationInfo5 = AppGlobals.getPackageManager().getApplicationInfo(str8, 1024L, (int) handleIncomingUser);
                                            if (applicationInfo5 == null) {
                                                throw new SecurityException("BIND_EXTERNAL_SERVICE failed, could not resolve client package " + str8);
                                            }
                                            ServiceInfo serviceInfo4 = new ServiceInfo(serviceInfo);
                                            ApplicationInfo applicationInfo6 = new ApplicationInfo(serviceInfo4.applicationInfo);
                                            serviceInfo4.applicationInfo = applicationInfo6;
                                            applicationInfo6.packageName = applicationInfo5.packageName;
                                            applicationInfo6.uid = applicationInfo5.uid;
                                            ComponentName componentName7 = new ComponentName(applicationInfo5.packageName, componentName6.getClassName());
                                            ComponentName componentName8 = new ComponentName(applicationInfo5.packageName, str == null ? componentName5.getClassName() : componentName5.getClassName() + str12 + str);
                                            intent.setComponent(componentName7);
                                            componentName3 = componentName8;
                                            componentName2 = componentName7;
                                            serviceInfo = serviceInfo4;
                                        }
                                    } else {
                                        throw new SecurityException("BIND_EXTERNAL_SERVICE required for " + componentName6);
                                    }
                                    if (z6) {
                                        int i11 = serviceInfo.flags;
                                        if ((i11 & 2) == 0) {
                                            throw new SecurityException("BIND_SHARED_ISOLATED_PROCESS failed, " + componentName3 + " is not an isolatedProcess");
                                        } else if ((i11 & 16) == 0) {
                                            throw new SecurityException("BIND_SHARED_ISOLATED_PROCESS failed, " + componentName3 + " has not set the allowSharedIsolatedProcess  attribute.");
                                        } else if (str == null) {
                                            throw new IllegalArgumentException("instanceName must be provided for binding a service into a shared isolated process.");
                                        }
                                    }
                                    if (handleIncomingUser > 0) {
                                        if (this.mAm.isSingleton(serviceInfo.processName, serviceInfo.applicationInfo, serviceInfo.name, serviceInfo.flags) && this.mAm.isValidSingletonCall(str9, serviceInfo.applicationInfo.uid)) {
                                            ServiceMap serviceMapLocked2 = getServiceMapLocked(0);
                                            long clearCallingIdentity = Binder.clearCallingIdentity();
                                            try {
                                                i6 = i8;
                                                String str21 = str16;
                                                str19 = str19;
                                                obj2 = obj;
                                                str17 = str20;
                                                str7 = str7;
                                                z7 = str9;
                                                try {
                                                    ResolveInfo resolveService3 = this.mAm.getPackageManagerInternal().resolveService(intent, str3, j, 0, i3);
                                                    try {
                                                        if (resolveService3 == null) {
                                                            Slog.w(str14, "Unable to resolve service " + intent + str15 + 0 + str21);
                                                            Binder.restoreCallingIdentity(clearCallingIdentity);
                                                            return null;
                                                        }
                                                        str18 = str14;
                                                        serviceInfo = resolveService3.serviceInfo;
                                                        Binder.restoreCallingIdentity(clearCallingIdentity);
                                                        serviceMap2 = serviceMapLocked2;
                                                        i7 = 0;
                                                    } catch (Throwable th) {
                                                        th = th;
                                                        Binder.restoreCallingIdentity(clearCallingIdentity);
                                                        throw th;
                                                    }
                                                } catch (Throwable th2) {
                                                    th = th2;
                                                }
                                            } catch (Throwable th3) {
                                                th = th3;
                                            }
                                        } else {
                                            z7 = str9;
                                            i7 = handleIncomingUser;
                                            i6 = i8;
                                            str18 = str14;
                                            obj2 = obj;
                                            str17 = str20;
                                            serviceMap2 = serviceMapLocked;
                                        }
                                        ServiceInfo serviceInfo5 = new ServiceInfo(serviceInfo);
                                        serviceInfo5.applicationInfo = this.mAm.getAppInfoForUser(serviceInfo5.applicationInfo, i7);
                                        serviceMap = serviceMap2;
                                        serviceInfo2 = serviceInfo5;
                                        str9 = str18;
                                        handleIncomingUser = obj2;
                                        i5 = z7;
                                    } else {
                                        i5 = str9;
                                        i6 = i8;
                                        str9 = str14;
                                        handleIncomingUser = obj;
                                        str17 = str20;
                                        serviceInfo2 = serviceInfo;
                                        serviceMap = serviceMapLocked;
                                    }
                                    serviceRecord3 = serviceMap.mServicesByInstanceName.get(componentName2);
                                } catch (RemoteException unused7) {
                                    i5 = str9;
                                    str13 = i10;
                                    str9 = str13;
                                    handleIncomingUser = obj;
                                    serviceRecord2 = serviceRecord;
                                    if (serviceRecord2 == null) {
                                    }
                                }
                            } catch (RemoteException unused8) {
                            }
                        } catch (RemoteException unused9) {
                            str7 = str7;
                            handleIncomingUser = obj;
                            i5 = str9;
                            str9 = i5;
                            serviceRecord2 = serviceRecord;
                            if (serviceRecord2 == null) {
                            }
                        }
                        if (serviceRecord3 == null && z2) {
                            try {
                                Intent.FilterComparison filterComparison2 = new Intent.FilterComparison(intent.cloneFilter());
                                ServiceRestarter serviceRestarter2 = new ServiceRestarter();
                                ServiceMap serviceMap3 = serviceMap;
                                ServiceInfo serviceInfo6 = serviceInfo2;
                                ServiceRecord serviceRecord6 = new ServiceRecord(this.mAm, componentName3, componentName2, str17, i6, filterComparison2, serviceInfo6, z3, serviceRestarter2, getProcessNameForService(serviceInfo2, componentName2, str4, str, z, z6), i, str2, z6);
                                try {
                                    serviceRestarter2.setService(serviceRecord6);
                                    serviceMap3.mServicesByInstanceName.put(componentName2, serviceRecord6);
                                    serviceMap3.mServicesByIntent.put(filterComparison2, serviceRecord6);
                                    int size = this.mPendingServices.size() - 1;
                                    while (size >= 0) {
                                        ServiceRecord serviceRecord7 = this.mPendingServices.get(size);
                                        ServiceInfo serviceInfo7 = serviceInfo6;
                                        if (serviceRecord7.serviceInfo.applicationInfo.uid == serviceInfo7.applicationInfo.uid && serviceRecord7.instanceName.equals(componentName2)) {
                                            this.mPendingServices.remove(size);
                                        }
                                        size--;
                                        serviceInfo6 = serviceInfo7;
                                    }
                                    ServiceInfo serviceInfo8 = serviceInfo6;
                                    for (int size2 = this.mPendingBringups.size() - 1; size2 >= 0; size2--) {
                                        ServiceRecord keyAt = this.mPendingBringups.keyAt(size2);
                                        if (keyAt.serviceInfo.applicationInfo.uid == serviceInfo8.applicationInfo.uid && keyAt.instanceName.equals(componentName2)) {
                                            this.mPendingBringups.removeAt(size2);
                                        }
                                    }
                                } catch (RemoteException unused10) {
                                }
                                serviceRecord = serviceRecord6;
                            } catch (RemoteException unused11) {
                            }
                            serviceRecord2 = serviceRecord;
                            if (serviceRecord2 == null) {
                                serviceRecord2.mRecentCallingPackage = str8;
                                serviceRecord2.mRecentCallingUid = i5;
                                try {
                                    serviceRecord2.mRecentCallerApplicationInfo = this.mAm.mContext.getPackageManager().getApplicationInfoAsUser(str8, 0, UserHandle.getUserId(i3));
                                } catch (PackageManager.NameNotFoundException unused12) {
                                }
                                if (!this.mAm.validateAssociationAllowedLocked(str8, i5, serviceRecord2.packageName, serviceRecord2.appInfo.uid)) {
                                    String str22 = handleIncomingUser + str8 + str7 + serviceRecord2.packageName;
                                    Slog.w((String) str9, str19 + str22);
                                    return new ServiceLookupResult(str22);
                                } else if (!this.mAm.mIntentFirewall.checkService(serviceRecord2.name, intent, i3, i2, str3, serviceRecord2.appInfo)) {
                                    return new ServiceLookupResult("blocked by firewall");
                                } else {
                                    String str23 = str9;
                                    if (ActivityManagerService.checkComponentPermission(serviceRecord2.permission, i2, i5, serviceRecord2.appInfo.uid, serviceRecord2.exported) != 0) {
                                        if (!serviceRecord2.exported) {
                                            Slog.w(str23, "Permission Denial: Accessing service " + serviceRecord2.shortInstanceName + " from pid=" + i2 + ", uid=" + i5 + " that is not exported from uid " + serviceRecord2.appInfo.uid);
                                            return new ServiceLookupResult("not exported from uid " + serviceRecord2.appInfo.uid);
                                        }
                                        Slog.w(str23, "Permission Denial: Accessing service " + serviceRecord2.shortInstanceName + " from pid=" + i2 + ", uid=" + i5 + " requires " + serviceRecord2.permission);
                                        return new ServiceLookupResult(serviceRecord2.permission);
                                    } else if ("android.permission.BIND_HOTWORD_DETECTION_SERVICE".equals(serviceRecord2.permission) && i5 != 1000) {
                                        Slog.w(str23, "Permission Denial: Accessing service " + serviceRecord2.shortInstanceName + " from pid=" + i2 + ", uid=" + i5 + " requiring permission " + serviceRecord2.permission + " can only be bound to from the system.");
                                        return new ServiceLookupResult("can only be bound to by the system.");
                                    } else {
                                        String str24 = serviceRecord2.permission;
                                        if (str24 != null && str8 != null && (permissionToOpCode = AppOpsManager.permissionToOpCode(str24)) != -1 && this.mAm.getAppOpsManager().checkOpNoThrow(permissionToOpCode, (int) i5, str8) != 0) {
                                            Slog.w(str23, "Appop Denial: Accessing service " + serviceRecord2.shortInstanceName + " from pid=" + i2 + ", uid=" + i5 + " requires appop " + AppOpsManager.opToName(permissionToOpCode));
                                            return null;
                                        }
                                        StringBuilder sb5 = new StringBuilder();
                                        ApplicationInfo applicationInfo7 = serviceRecord2.appInfo;
                                        sb5.append(applicationInfo7.seInfo);
                                        sb5.append(generateAdditionalSeInfoFromService(intent));
                                        applicationInfo7.seInfo = sb5.toString();
                                        return new ServiceLookupResult(serviceRecord2, resolveService.getAlias());
                                    }
                                }
                            }
                            return null;
                        }
                        serviceRecord = serviceRecord3;
                        serviceRecord2 = serviceRecord;
                        if (serviceRecord2 == null) {
                        }
                    }
                }
                str7 = " and ";
                i5 = i3;
                handleIncomingUser = "association not allowed between packages ";
                str8 = str4;
                str9 = "ActivityManager";
                serviceRecord2 = serviceRecord;
                if (serviceRecord2 == null) {
                }
            }
        } else {
            str5 = XmlUtils.STRING_ARRAY_SEPARATOR;
        }
        serviceRecord = serviceRecord5;
        str6 = " U=";
        if (serviceRecord != null) {
        }
        String str192 = "Service lookup failed: ";
        if (serviceRecord == null) {
        }
        str7 = " and ";
        i5 = i3;
        handleIncomingUser = "association not allowed between packages ";
        str8 = str4;
        str9 = "ActivityManager";
        serviceRecord2 = serviceRecord;
        if (serviceRecord2 == null) {
        }
    }

    public final int getAllowMode(Intent intent, String str) {
        return (str == null || intent.getComponent() == null || !str.equals(intent.getComponent().getPackageName())) ? 1 : 3;
    }

    public final boolean bumpServiceExecutingLocked(ServiceRecord serviceRecord, boolean z, String str, int i) {
        boolean z2;
        ProcessRecord processRecord;
        ProcessRecord processRecord2;
        boolean z3 = false;
        if (this.mAm.mBootPhase >= 600 || (processRecord2 = serviceRecord.app) == null || processRecord2.getPid() != ActivityManagerService.MY_PID) {
            z2 = true;
        } else {
            Slog.w("ActivityManager", "Too early to start/bind service in system_server: Phase=" + this.mAm.mBootPhase + " " + serviceRecord.getComponentName());
            z2 = false;
        }
        if (serviceRecord.executeNesting == 0) {
            serviceRecord.executeFg = z;
            synchronized (this.mAm.mProcessStats.mLock) {
                ServiceState tracker = serviceRecord.getTracker();
                if (tracker != null) {
                    tracker.setExecuting(true, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
            }
            ProcessRecord processRecord3 = serviceRecord.app;
            if (processRecord3 != null) {
                ProcessServiceRecord processServiceRecord = processRecord3.mServices;
                processServiceRecord.startExecutingService(serviceRecord);
                processServiceRecord.setExecServicesFg(processServiceRecord.shouldExecServicesFg() || z);
                if (z2 && processServiceRecord.numberOfExecutingServices() == 1) {
                    scheduleServiceTimeoutLocked(serviceRecord.app);
                }
            }
        } else {
            ProcessRecord processRecord4 = serviceRecord.app;
            if (processRecord4 != null && z) {
                ProcessServiceRecord processServiceRecord2 = processRecord4.mServices;
                if (!processServiceRecord2.shouldExecServicesFg()) {
                    processServiceRecord2.setExecServicesFg(true);
                    if (z2) {
                        scheduleServiceTimeoutLocked(serviceRecord.app);
                    }
                }
            }
        }
        if (i != 0 && (processRecord = serviceRecord.app) != null && processRecord.mState.getCurProcState() > 10) {
            this.mAm.enqueueOomAdjTargetLocked(serviceRecord.app);
            this.mAm.updateOomAdjPendingTargetsLocked(i);
            z3 = true;
        }
        serviceRecord.executeFg |= z;
        serviceRecord.executeNesting++;
        serviceRecord.executingStart = SystemClock.uptimeMillis();
        return z3;
    }

    public final boolean requestServiceBindingLocked(ServiceRecord serviceRecord, IntentBindRecord intentBindRecord, boolean z, boolean z2) throws TransactionTooLargeException {
        ProcessRecord processRecord = serviceRecord.app;
        if (processRecord == null || processRecord.getThread() == null) {
            return false;
        }
        if ((!intentBindRecord.requested || z2) && intentBindRecord.apps.size() > 0) {
            try {
                bumpServiceExecutingLocked(serviceRecord, z, "bind", 4);
                serviceRecord.app.getThread().scheduleBindService(serviceRecord, intentBindRecord.intent.getIntent(), z2, serviceRecord.app.mState.getReportedProcState());
                if (!z2) {
                    intentBindRecord.requested = true;
                }
                intentBindRecord.hasBound = true;
                intentBindRecord.doRebind = false;
            } catch (TransactionTooLargeException e) {
                boolean contains = this.mDestroyingServices.contains(serviceRecord);
                serviceDoneExecutingLocked(serviceRecord, contains, contains, false);
                throw e;
            } catch (RemoteException unused) {
                boolean contains2 = this.mDestroyingServices.contains(serviceRecord);
                serviceDoneExecutingLocked(serviceRecord, contains2, contains2, false);
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:67:0x016b  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01d7  */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v6, types: [int] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean scheduleServiceRestartLocked(ServiceRecord serviceRecord, boolean z) {
        ServiceMap serviceMapLocked;
        boolean z2;
        boolean z3;
        String str;
        boolean z4;
        String str2;
        int i;
        boolean z5;
        boolean z6;
        boolean z7;
        int i2 = 0;
        if (this.mAm.mAtmInternal.isShuttingDown()) {
            Slog.w("ActivityManager", "Not scheduling restart of crashed service " + serviceRecord.shortInstanceName + " - system is shutting down");
            return false;
        }
        if (getServiceMapLocked(serviceRecord.userId).mServicesByInstanceName.get(serviceRecord.instanceName) != serviceRecord) {
            Slog.wtf("ActivityManager", "Attempting to schedule restart of " + serviceRecord + " when found in map: " + serviceMapLocked.mServicesByInstanceName.get(serviceRecord.instanceName));
            return false;
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        int indexOf = this.mRestartingServices.indexOf(serviceRecord);
        boolean z8 = indexOf != -1;
        if ((serviceRecord.serviceInfo.applicationInfo.flags & 8) == 0) {
            ActivityManagerConstants activityManagerConstants = this.mAm.mConstants;
            long j = activityManagerConstants.SERVICE_RESTART_DURATION;
            long j2 = activityManagerConstants.SERVICE_RESET_RUN_DURATION;
            int size = serviceRecord.deliveredStarts.size();
            if (size > 0) {
                int i3 = size - 1;
                boolean z9 = false;
                while (i3 >= 0) {
                    ServiceRecord.StartItem startItem = serviceRecord.deliveredStarts.get(i3);
                    startItem.removeUriPermissionsLocked();
                    if (startItem.intent != null) {
                        if (!z || (startItem.deliveryCount < 3 && startItem.doneExecutingCount < 6)) {
                            serviceRecord.pendingStarts.add(i2, startItem);
                            long uptimeMillis2 = (SystemClock.uptimeMillis() - startItem.deliveredTime) * 2;
                            if (j < uptimeMillis2) {
                                j = uptimeMillis2;
                            }
                            if (j2 < uptimeMillis2) {
                                j2 = uptimeMillis2;
                            }
                        } else {
                            Slog.w("ActivityManager", "Canceling start item " + startItem.intent + " in service " + serviceRecord.shortInstanceName);
                            z9 = true;
                        }
                    }
                    i3--;
                    i2 = 0;
                }
                serviceRecord.deliveredStarts.clear();
                z4 = z9;
            } else {
                z4 = false;
            }
            if (z) {
                boolean canStopIfKilled = serviceRecord.canStopIfKilled(z4);
                if (canStopIfKilled && !serviceRecord.hasAutoCreateConnections()) {
                    return false;
                }
                str2 = (!serviceRecord.startRequested || canStopIfKilled) ? "connection" : "start-requested";
            } else {
                str2 = "always";
            }
            serviceRecord.totalRestartCount++;
            long j3 = serviceRecord.restartDelay;
            if (j3 == 0) {
                serviceRecord.restartCount++;
                serviceRecord.restartDelay = j;
            } else {
                if (serviceRecord.crashCount > 1) {
                    serviceRecord.restartDelay = this.mAm.mConstants.BOUND_SERVICE_CRASH_RESTART_DURATION * (i - 1);
                } else {
                    z5 = z8;
                    if (uptimeMillis > serviceRecord.restartTime + j2) {
                        serviceRecord.restartCount = 1;
                        serviceRecord.restartDelay = j;
                    } else {
                        long j4 = j3 * this.mAm.mConstants.SERVICE_RESTART_DURATION_FACTOR;
                        serviceRecord.restartDelay = j4;
                        if (j4 < j) {
                            serviceRecord.restartDelay = j;
                        }
                    }
                    if (!isServiceRestartBackoffEnabledLocked(serviceRecord.packageName)) {
                        long j5 = serviceRecord.restartDelay + uptimeMillis;
                        serviceRecord.mEarliestRestartTime = j5;
                        serviceRecord.nextRestartTime = j5;
                        if (z5) {
                            this.mRestartingServices.remove(indexOf);
                            z6 = false;
                        } else {
                            z6 = z5;
                        }
                        if (this.mRestartingServices.isEmpty()) {
                            long max = Math.max(getExtraRestartTimeInBetweenLocked() + uptimeMillis, serviceRecord.nextRestartTime);
                            serviceRecord.nextRestartTime = max;
                            serviceRecord.restartDelay = max - uptimeMillis;
                        } else {
                            long extraRestartTimeInBetweenLocked = getExtraRestartTimeInBetweenLocked() + this.mAm.mConstants.SERVICE_MIN_RESTART_TIME_BETWEEN;
                            do {
                                long j6 = serviceRecord.nextRestartTime;
                                for (int size2 = this.mRestartingServices.size() - 1; size2 >= 0; size2--) {
                                    long j7 = this.mRestartingServices.get(size2).nextRestartTime;
                                    if (j6 >= j7 - extraRestartTimeInBetweenLocked) {
                                        long j8 = j7 + extraRestartTimeInBetweenLocked;
                                        if (j6 < j8) {
                                            serviceRecord.nextRestartTime = j8;
                                            serviceRecord.restartDelay = j8 - uptimeMillis;
                                            z7 = true;
                                            continue;
                                            break;
                                        }
                                    }
                                    if (j6 >= j7 + extraRestartTimeInBetweenLocked) {
                                        break;
                                    }
                                }
                                z7 = false;
                                continue;
                            } while (z7);
                        }
                        z3 = z6;
                    } else {
                        long j9 = this.mAm.mConstants.SERVICE_RESTART_DURATION;
                        serviceRecord.restartDelay = j9;
                        serviceRecord.nextRestartTime = j9 + uptimeMillis;
                        z3 = z5;
                    }
                    str = str2;
                    z2 = false;
                }
            }
            z5 = z8;
            if (!isServiceRestartBackoffEnabledLocked(serviceRecord.packageName)) {
            }
            str = str2;
            z2 = false;
        } else {
            serviceRecord.totalRestartCount++;
            z2 = false;
            serviceRecord.restartCount = 0;
            serviceRecord.restartDelay = 0L;
            serviceRecord.mEarliestRestartTime = 0L;
            serviceRecord.nextRestartTime = uptimeMillis;
            z3 = z8;
            str = "persistent";
        }
        serviceRecord.mRestartSchedulingTime = uptimeMillis;
        if (!z3) {
            if (indexOf == -1) {
                serviceRecord.createdFromFg = z2;
                synchronized (this.mAm.mProcessStats.mLock) {
                    serviceRecord.makeRestarting(this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
            }
            int size3 = this.mRestartingServices.size();
            int i4 = z2;
            while (true) {
                if (i4 >= size3) {
                    break;
                } else if (this.mRestartingServices.get(i4).nextRestartTime > serviceRecord.nextRestartTime) {
                    this.mRestartingServices.add(i4, serviceRecord);
                    z2 = true;
                    break;
                } else {
                    i4++;
                }
            }
            if (!z2) {
                this.mRestartingServices.add(serviceRecord);
            }
        }
        cancelForegroundNotificationLocked(serviceRecord);
        performScheduleRestartLocked(serviceRecord, "Scheduling", str, uptimeMillis);
        return true;
    }

    @GuardedBy({"mAm"})
    public void performScheduleRestartLocked(ServiceRecord serviceRecord, String str, String str2, long j) {
        if (serviceRecord.fgRequired && serviceRecord.fgWaiting) {
            this.mAm.mHandler.removeMessages(66, serviceRecord);
            serviceRecord.fgWaiting = false;
        }
        this.mAm.mHandler.removeCallbacks(serviceRecord.restarter);
        this.mAm.mHandler.postAtTime(serviceRecord.restarter, serviceRecord.nextRestartTime);
        serviceRecord.nextRestartTime = j + serviceRecord.restartDelay;
        Slog.w("ActivityManager", str + " restart of crashed service " + serviceRecord.shortInstanceName + " in " + serviceRecord.restartDelay + "ms for " + str2);
        EventLog.writeEvent(30035, Integer.valueOf(serviceRecord.userId), serviceRecord.shortInstanceName, Long.valueOf(serviceRecord.restartDelay));
    }

    @GuardedBy({"mAm"})
    public void rescheduleServiceRestartOnMemoryPressureIfNeededLocked(int i, int i2, String str, long j) {
        ActivityManagerConstants activityManagerConstants = this.mAm.mConstants;
        if (activityManagerConstants.mEnableExtraServiceRestartDelayOnMemPressure) {
            long[] jArr = activityManagerConstants.mExtraServiceRestartDelayOnMemPressure;
            performRescheduleServiceRestartOnMemoryPressureLocked(jArr[i], jArr[i2], str, j);
        }
    }

    @GuardedBy({"mAm"})
    public void rescheduleServiceRestartOnMemoryPressureIfNeededLocked(boolean z, boolean z2, long j) {
        if (z == z2) {
            return;
        }
        long j2 = this.mAm.mConstants.mExtraServiceRestartDelayOnMemPressure[this.mAm.mAppProfiler.getLastMemoryLevelLocked()];
        long j3 = z ? j2 : 0L;
        if (!z2) {
            j2 = 0;
        }
        performRescheduleServiceRestartOnMemoryPressureLocked(j3, j2, "config", j);
    }

    @GuardedBy({"mAm"})
    public void rescheduleServiceRestartIfPossibleLocked(long j, long j2, String str, long j3) {
        long j4;
        long j5;
        long j6;
        long j7 = j + j2;
        long j8 = j7 * 2;
        int size = this.mRestartingServices.size();
        int i = -1;
        int i2 = 0;
        long j9 = j3;
        while (i2 < size) {
            ServiceRecord serviceRecord = this.mRestartingServices.get(i2);
            if ((serviceRecord.serviceInfo.applicationInfo.flags & 8) != 0 || !isServiceRestartBackoffEnabledLocked(serviceRecord.packageName)) {
                j4 = j7;
                j5 = j8;
                j9 = serviceRecord.nextRestartTime;
                i = i2;
            } else {
                long j10 = j9 + j7;
                long j11 = j8;
                long j12 = serviceRecord.mEarliestRestartTime;
                if (j10 <= j12) {
                    serviceRecord.nextRestartTime = Math.max(j3, Math.max(j12, i2 > 0 ? this.mRestartingServices.get(i2 - 1).nextRestartTime + j7 : 0L));
                } else {
                    if (j9 <= j3) {
                        serviceRecord.nextRestartTime = Math.max(j3, Math.max(j12, serviceRecord.mRestartSchedulingTime + j));
                    } else {
                        serviceRecord.nextRestartTime = Math.max(j3, j10);
                    }
                    int i3 = i + 1;
                    if (i2 > i3) {
                        this.mRestartingServices.remove(i2);
                        this.mRestartingServices.add(i3, serviceRecord);
                    }
                }
                int i4 = i;
                long j13 = j9;
                int i5 = i + 1;
                while (true) {
                    if (i5 > i2) {
                        j4 = j7;
                        j5 = j11;
                        break;
                    }
                    ServiceRecord serviceRecord2 = this.mRestartingServices.get(i5);
                    long j14 = serviceRecord2.nextRestartTime;
                    if (i5 == 0) {
                        j4 = j7;
                        j6 = j13;
                    } else {
                        j4 = j7;
                        j6 = this.mRestartingServices.get(i5 - 1).nextRestartTime;
                    }
                    long j15 = j14 - j6;
                    j5 = j11;
                    if (j15 >= j5) {
                        break;
                    }
                    i4 = i5;
                    j13 = serviceRecord2.nextRestartTime;
                    i5++;
                    j11 = j5;
                    j7 = j4;
                }
                serviceRecord.restartDelay = serviceRecord.nextRestartTime - j3;
                performScheduleRestartLocked(serviceRecord, "Rescheduling", str, j3);
                i = i4;
                j9 = j13;
            }
            i2++;
            j8 = j5;
            j7 = j4;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0055, code lost:
        if (r3 != r0) goto L23;
     */
    @GuardedBy({"mAm"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void performRescheduleServiceRestartOnMemoryPressureLocked(long j, long j2, String str, long j3) {
        boolean z;
        int i = ((j2 - j) > 0L ? 1 : ((j2 - j) == 0L ? 0 : -1));
        if (i == 0) {
            return;
        }
        if (i <= 0) {
            if (i < 0) {
                rescheduleServiceRestartIfPossibleLocked(j2, this.mAm.mConstants.SERVICE_MIN_RESTART_TIME_BETWEEN, str, j3);
                return;
            }
            return;
        }
        long j4 = this.mAm.mConstants.SERVICE_MIN_RESTART_TIME_BETWEEN + j2;
        int size = this.mRestartingServices.size();
        long j5 = j3;
        for (int i2 = 0; i2 < size; i2++) {
            ServiceRecord serviceRecord = this.mRestartingServices.get(i2);
            if ((serviceRecord.serviceInfo.applicationInfo.flags & 8) != 0 || !isServiceRestartBackoffEnabledLocked(serviceRecord.packageName)) {
                j5 = serviceRecord.nextRestartTime;
            } else {
                if (j5 <= j3) {
                    long j6 = serviceRecord.nextRestartTime;
                    long max = Math.max(j3, Math.max(serviceRecord.mEarliestRestartTime, serviceRecord.mRestartSchedulingTime + j2));
                    serviceRecord.nextRestartTime = max;
                } else {
                    if (serviceRecord.nextRestartTime - j5 < j4) {
                        serviceRecord.nextRestartTime = Math.max(j5 + j4, j3);
                        z = true;
                    }
                    z = false;
                }
                long j7 = serviceRecord.nextRestartTime;
                serviceRecord.restartDelay = j7 - j3;
                if (z) {
                    performScheduleRestartLocked(serviceRecord, "Rescheduling", str, j3);
                }
                j5 = j7;
            }
        }
    }

    @GuardedBy({"mAm"})
    public long getExtraRestartTimeInBetweenLocked() {
        ActivityManagerService activityManagerService = this.mAm;
        if (activityManagerService.mConstants.mEnableExtraServiceRestartDelayOnMemPressure) {
            return this.mAm.mConstants.mExtraServiceRestartDelayOnMemPressure[activityManagerService.mAppProfiler.getLastMemoryLevelLocked()];
        }
        return 0L;
    }

    public final void performServiceRestartLocked(ServiceRecord serviceRecord) {
        if (this.mRestartingServices.contains(serviceRecord)) {
            if (!isServiceNeededLocked(serviceRecord, false, false)) {
                Slog.wtf("ActivityManager", "Restarting service that is not needed: " + serviceRecord);
                return;
            }
            try {
                bringUpServiceLocked(serviceRecord, serviceRecord.intent.getIntent().getFlags(), serviceRecord.createdFromFg, true, false, false, true);
            } catch (TransactionTooLargeException unused) {
            } catch (Throwable th) {
                this.mAm.updateOomAdjPendingTargetsLocked(6);
                throw th;
            }
            this.mAm.updateOomAdjPendingTargetsLocked(6);
        }
    }

    public final boolean unscheduleServiceRestartLocked(ServiceRecord serviceRecord, int i, boolean z) {
        if (z || serviceRecord.restartDelay != 0) {
            boolean remove = this.mRestartingServices.remove(serviceRecord);
            if (remove || i != serviceRecord.appInfo.uid) {
                serviceRecord.resetRestartCounter();
            }
            if (remove) {
                clearRestartingIfNeededLocked(serviceRecord);
            }
            this.mAm.mHandler.removeCallbacks(serviceRecord.restarter);
            return true;
        }
        return false;
    }

    public final void clearRestartingIfNeededLocked(ServiceRecord serviceRecord) {
        if (serviceRecord.restartTracker != null) {
            boolean z = true;
            int size = this.mRestartingServices.size() - 1;
            while (true) {
                if (size < 0) {
                    z = false;
                    break;
                } else if (this.mRestartingServices.get(size).restartTracker == serviceRecord.restartTracker) {
                    break;
                } else {
                    size--;
                }
            }
            if (z) {
                return;
            }
            synchronized (this.mAm.mProcessStats.mLock) {
                serviceRecord.restartTracker.setRestarting(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
            }
            serviceRecord.restartTracker = null;
        }
    }

    @GuardedBy({"mAm"})
    public void setServiceRestartBackoffEnabledLocked(String str, boolean z, String str2) {
        if (!z) {
            if (this.mRestartBackoffDisabledPackages.contains(str)) {
                return;
            }
            this.mRestartBackoffDisabledPackages.add(str);
            long uptimeMillis = SystemClock.uptimeMillis();
            int size = this.mRestartingServices.size();
            for (int i = 0; i < size; i++) {
                ServiceRecord serviceRecord = this.mRestartingServices.get(i);
                if (TextUtils.equals(serviceRecord.packageName, str)) {
                    long j = this.mAm.mConstants.SERVICE_RESTART_DURATION;
                    if (serviceRecord.nextRestartTime - uptimeMillis > j) {
                        serviceRecord.restartDelay = j;
                        serviceRecord.nextRestartTime = j + uptimeMillis;
                        performScheduleRestartLocked(serviceRecord, "Rescheduling", str2, uptimeMillis);
                    }
                }
                Collections.sort(this.mRestartingServices, new Comparator() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda2
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        int lambda$setServiceRestartBackoffEnabledLocked$0;
                        lambda$setServiceRestartBackoffEnabledLocked$0 = ActiveServices.lambda$setServiceRestartBackoffEnabledLocked$0((ServiceRecord) obj, (ServiceRecord) obj2);
                        return lambda$setServiceRestartBackoffEnabledLocked$0;
                    }
                });
            }
            return;
        }
        removeServiceRestartBackoffEnabledLocked(str);
    }

    public static /* synthetic */ int lambda$setServiceRestartBackoffEnabledLocked$0(ServiceRecord serviceRecord, ServiceRecord serviceRecord2) {
        return (int) (serviceRecord.nextRestartTime - serviceRecord2.nextRestartTime);
    }

    @GuardedBy({"mAm"})
    public final void removeServiceRestartBackoffEnabledLocked(String str) {
        this.mRestartBackoffDisabledPackages.remove(str);
    }

    @GuardedBy({"mAm"})
    public boolean isServiceRestartBackoffEnabledLocked(String str) {
        return !this.mRestartBackoffDisabledPackages.contains(str);
    }

    public final String bringUpServiceLocked(ServiceRecord serviceRecord, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) throws TransactionTooLargeException {
        try {
            if (Trace.isTagEnabled(64L)) {
                Trace.traceBegin(64L, "bringUpServiceLocked: " + serviceRecord.shortInstanceName);
            }
            return bringUpServiceInnerLocked(serviceRecord, i, z, z2, z3, z4, z5);
        } finally {
            Trace.traceEnd(64L);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:126:0x02b8 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x02c0  */
    /* JADX WARN: Removed duplicated region for block: B:131:0x02e1  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x02fb  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x0332  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x033a  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0357  */
    /* JADX WARN: Removed duplicated region for block: B:145:0x0360  */
    /* JADX WARN: Type inference failed for: r18v0, types: [android.content.ComponentName] */
    /* JADX WARN: Type inference failed for: r18v2 */
    /* JADX WARN: Type inference failed for: r18v7 */
    /* JADX WARN: Type inference failed for: r21v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r21v11 */
    /* JADX WARN: Type inference failed for: r21v7 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final String bringUpServiceInnerLocked(ServiceRecord serviceRecord, int i, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) throws TransactionTooLargeException {
        String str;
        String str2;
        String str3;
        ProcessRecord processRecord;
        HostingRecord byAppZygote;
        String str4;
        String str5;
        long j;
        long j2;
        ActivityManagerService activityManagerService;
        ProcessRecord startProcessLocked;
        String str6;
        long j3;
        String str7;
        String str8;
        ApplicationInfo applicationInfo;
        ProcessRecord processRecord2 = serviceRecord.app;
        if (processRecord2 != null && processRecord2.getThread() != null) {
            sendServiceArgsLocked(serviceRecord, z, false);
            return null;
        } else if (z2 || !this.mRestartingServices.contains(serviceRecord)) {
            if (this.mRestartingServices.remove(serviceRecord)) {
                clearRestartingIfNeededLocked(serviceRecord);
            }
            if (serviceRecord.delayed) {
                getServiceMapLocked(serviceRecord.userId).mDelayedStartList.remove(serviceRecord);
                serviceRecord.delayed = false;
            }
            String str9 = "ActivityManager";
            if (!this.mAm.mUserController.hasStartedUserState(serviceRecord.userId)) {
                String str10 = "Unable to launch app " + serviceRecord.appInfo.packageName + "/" + serviceRecord.appInfo.uid + " for service " + serviceRecord.intent.getIntent() + ": user " + serviceRecord.userId + " is stopped";
                Slog.w("ActivityManager", str10);
                bringDownServiceLocked(serviceRecord, z5);
                return str10;
            }
            if (!serviceRecord.appInfo.packageName.equals(serviceRecord.mRecentCallingPackage) && !serviceRecord.isNotAppComponentUsage) {
                this.mAm.mUsageStatsService.reportEvent(serviceRecord.packageName, serviceRecord.userId, 31);
            }
            try {
                this.mAm.mPackageManagerInt.setPackageStoppedState(serviceRecord.packageName, false, serviceRecord.userId);
            } catch (IllegalArgumentException e) {
                Slog.w("ActivityManager", "Failed trying to unstop package " + serviceRecord.packageName + ": " + e);
            }
            ServiceInfo serviceInfo = serviceRecord.serviceInfo;
            boolean z6 = (serviceInfo.flags & 2) != 0;
            String str11 = serviceRecord.processName;
            ComponentName componentName = serviceRecord.instanceName;
            String str12 = serviceRecord.definingPackageName;
            int i2 = serviceRecord.definingUid;
            long j4 = componentName;
            long j5 = serviceInfo.processName;
            HostingRecord hostingRecord = new HostingRecord("service", j4, str12, i2, j5, getHostingRecordTriggerType(serviceRecord));
            if (!z6) {
                ProcessRecord processRecordLocked = this.mAm.getProcessRecordLocked(str11, serviceRecord.appInfo.uid);
                if (processRecordLocked != null) {
                    IApplicationThread thread = processRecordLocked.getThread();
                    int pid = processRecordLocked.getPid();
                    UidRecord uidRecord = processRecordLocked.getUidRecord();
                    try {
                        if (thread != null) {
                            try {
                                if (Trace.isTagEnabled(64L)) {
                                    Trace.traceBegin(64L, "realStartServiceLocked: " + serviceRecord.shortInstanceName);
                                }
                                applicationInfo = serviceRecord.appInfo;
                            } catch (TransactionTooLargeException e2) {
                                throw e2;
                            } catch (RemoteException e3) {
                                e = e3;
                                j3 = 64;
                                str7 = "Exception when starting service ";
                                str6 = str11;
                                str8 = "ActivityManager";
                                str = "Unable to launch app ";
                            } catch (Throwable th) {
                                th = th;
                                j5 = 64;
                            }
                            try {
                                processRecordLocked.addPackage(applicationInfo.packageName, applicationInfo.longVersionCode, this.mAm.mProcessStats);
                                j3 = 64;
                                str7 = "Exception when starting service ";
                                str6 = str11;
                                str8 = "ActivityManager";
                                str = "Unable to launch app ";
                                try {
                                    realStartServiceLocked(serviceRecord, processRecordLocked, thread, pid, uidRecord, z, z5);
                                    Trace.traceEnd(64L);
                                    return null;
                                } catch (TransactionTooLargeException e4) {
                                    throw e4;
                                } catch (RemoteException e5) {
                                    e = e5;
                                    str9 = str8;
                                    Slog.w(str9, str7 + serviceRecord.shortInstanceName, e);
                                    Trace.traceEnd(j3);
                                    str2 = str9;
                                    processRecord = processRecordLocked;
                                    byAppZygote = hostingRecord;
                                    str3 = str6;
                                    if (processRecord == null) {
                                    }
                                    if (serviceRecord.fgRequired) {
                                    }
                                    if (!this.mPendingServices.contains(serviceRecord)) {
                                    }
                                    if (serviceRecord.delayedStop) {
                                    }
                                    return null;
                                }
                            } catch (TransactionTooLargeException e6) {
                                throw e6;
                            } catch (RemoteException e7) {
                                e = e7;
                                str7 = "Exception when starting service ";
                                str6 = str11;
                                str8 = "ActivityManager";
                                str = "Unable to launch app ";
                                j3 = 64;
                            } catch (Throwable th2) {
                                th = th2;
                                j5 = 64;
                                Trace.traceEnd(j5);
                                throw th;
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                    }
                }
                str6 = str11;
                str = "Unable to launch app ";
                str2 = str9;
                processRecord = processRecordLocked;
                byAppZygote = hostingRecord;
                str3 = str6;
            } else {
                str = "Unable to launch app ";
                if (serviceRecord.inSharedIsolatedProcess) {
                    ProcessList processList = this.mAm.mProcessList;
                    ApplicationInfo applicationInfo2 = serviceRecord.appInfo;
                    str3 = str11;
                    ProcessRecord sharedIsolatedProcess = processList.getSharedIsolatedProcess(str3, applicationInfo2.uid, applicationInfo2.packageName);
                    if (sharedIsolatedProcess != null) {
                        IApplicationThread thread2 = sharedIsolatedProcess.getThread();
                        int pid2 = sharedIsolatedProcess.getPid();
                        UidRecord uidRecord2 = sharedIsolatedProcess.getUidRecord();
                        try {
                            if (thread2 != null) {
                                try {
                                    if (Trace.isTagEnabled(64L)) {
                                        j2 = 64;
                                        try {
                                            Trace.traceBegin(64L, "realStartServiceLocked: " + serviceRecord.shortInstanceName);
                                        } catch (TransactionTooLargeException e8) {
                                            throw e8;
                                        } catch (RemoteException e9) {
                                            e = e9;
                                            j = 64;
                                            str4 = "ActivityManager";
                                            str5 = "Exception when starting service ";
                                            str2 = str4;
                                            Slog.w(str2, str5 + serviceRecord.shortInstanceName, e);
                                            Trace.traceEnd(j);
                                            processRecord = sharedIsolatedProcess;
                                            byAppZygote = hostingRecord;
                                            if (processRecord == null) {
                                            }
                                            if (serviceRecord.fgRequired) {
                                            }
                                            if (!this.mPendingServices.contains(serviceRecord)) {
                                            }
                                            if (serviceRecord.delayedStop) {
                                            }
                                            return null;
                                        } catch (Throwable th4) {
                                            th = th4;
                                            j4 = 64;
                                            Trace.traceEnd(j4);
                                            throw th;
                                        }
                                    } else {
                                        j2 = 64;
                                    }
                                    j = j2;
                                    str4 = "ActivityManager";
                                    str5 = "Exception when starting service ";
                                    try {
                                        realStartServiceLocked(serviceRecord, sharedIsolatedProcess, thread2, pid2, uidRecord2, z, z5);
                                        Trace.traceEnd(j);
                                        return null;
                                    } catch (TransactionTooLargeException e10) {
                                        throw e10;
                                    } catch (RemoteException e11) {
                                        e = e11;
                                        str2 = str4;
                                        Slog.w(str2, str5 + serviceRecord.shortInstanceName, e);
                                        Trace.traceEnd(j);
                                        processRecord = sharedIsolatedProcess;
                                        byAppZygote = hostingRecord;
                                        if (processRecord == null) {
                                            if (!serviceRecord.isSdkSandbox) {
                                            }
                                            if (startProcessLocked != null) {
                                            }
                                        }
                                        if (serviceRecord.fgRequired) {
                                        }
                                        if (!this.mPendingServices.contains(serviceRecord)) {
                                        }
                                        if (serviceRecord.delayedStop) {
                                        }
                                        return null;
                                    }
                                } catch (TransactionTooLargeException e12) {
                                    throw e12;
                                } catch (RemoteException e13) {
                                    e = e13;
                                    str4 = "ActivityManager";
                                    str5 = "Exception when starting service ";
                                    j = 64;
                                } catch (Throwable th5) {
                                    th = th5;
                                    j4 = 64;
                                }
                            }
                        } catch (Throwable th6) {
                            th = th6;
                        }
                    }
                    str2 = "ActivityManager";
                    processRecord = sharedIsolatedProcess;
                } else {
                    str2 = "ActivityManager";
                    str3 = str11;
                    processRecord = serviceRecord.isolationHostProc;
                    if (WebViewZygote.isMultiprocessEnabled() && serviceRecord.serviceInfo.packageName.equals(WebViewZygote.getPackageName())) {
                        hostingRecord = HostingRecord.byWebviewZygote(serviceRecord.instanceName, serviceRecord.definingPackageName, serviceRecord.definingUid, serviceRecord.serviceInfo.processName);
                    }
                    ServiceInfo serviceInfo2 = serviceRecord.serviceInfo;
                    if ((serviceInfo2.flags & 8) != 0) {
                        byAppZygote = HostingRecord.byAppZygote(serviceRecord.instanceName, serviceRecord.definingPackageName, serviceRecord.definingUid, serviceInfo2.processName);
                    }
                }
                byAppZygote = hostingRecord;
            }
            if (processRecord == null && !z3 && !z4) {
                if (!serviceRecord.isSdkSandbox) {
                    startProcessLocked = this.mAm.startSdkSandboxProcessLocked(str3, serviceRecord.appInfo, true, i, byAppZygote, 0, Process.toSdkSandboxUid(serviceRecord.sdkSandboxClientAppUid), serviceRecord.sdkSandboxClientAppPackage);
                    serviceRecord.isolationHostProc = startProcessLocked;
                } else {
                    startProcessLocked = this.mAm.startProcessLocked(str3, serviceRecord.appInfo, true, i, byAppZygote, 0, false, z6);
                }
                if (startProcessLocked != null) {
                    String str13 = str + serviceRecord.appInfo.packageName + "/" + serviceRecord.appInfo.uid + " for service " + serviceRecord.intent.getIntent() + ": process is bad";
                    Slog.w(str2, str13);
                    bringDownServiceLocked(serviceRecord, z5);
                    return str13;
                } else if (z6) {
                    serviceRecord.isolationHostProc = startProcessLocked;
                }
            }
            if (serviceRecord.fgRequired) {
                this.mAm.tempAllowlistUidLocked(serviceRecord.appInfo.uid, activityManagerService.mConstants.mServiceStartForegroundTimeoutMs, FrameworkStatsLog.f109x2b18bc4b, "fg-service-launch", 0, serviceRecord.mRecentCallingUid);
            }
            if (!this.mPendingServices.contains(serviceRecord)) {
                this.mPendingServices.add(serviceRecord);
            }
            if (serviceRecord.delayedStop) {
                serviceRecord.delayedStop = false;
                if (serviceRecord.startRequested) {
                    stopServiceLocked(serviceRecord, z5);
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public final String getHostingRecordTriggerType(ServiceRecord serviceRecord) {
        return ("android.permission.BIND_JOB_SERVICE".equals(serviceRecord.permission) && serviceRecord.mRecentCallingUid == 1000) ? "job" : "unknown";
    }

    public final void requestServiceBindingsLocked(ServiceRecord serviceRecord, boolean z) throws TransactionTooLargeException {
        for (int size = serviceRecord.bindings.size() - 1; size >= 0 && requestServiceBindingLocked(serviceRecord, serviceRecord.bindings.valueAt(size), z, false); size--) {
        }
    }

    public final void realStartServiceLocked(ServiceRecord serviceRecord, ProcessRecord processRecord, IApplicationThread iApplicationThread, int i, UidRecord uidRecord, boolean z, boolean z2) throws RemoteException {
        if (iApplicationThread == null) {
            throw new RemoteException();
        }
        serviceRecord.setProcess(processRecord, iApplicationThread, i, uidRecord);
        long uptimeMillis = SystemClock.uptimeMillis();
        serviceRecord.lastActivity = uptimeMillis;
        serviceRecord.restartTime = uptimeMillis;
        ProcessServiceRecord processServiceRecord = processRecord.mServices;
        boolean startService = processServiceRecord.startService(serviceRecord);
        bumpServiceExecutingLocked(serviceRecord, z, "create", 0);
        this.mAm.updateLruProcessLocked(processRecord, false, null);
        updateServiceForegroundLocked(processServiceRecord, false);
        this.mAm.enqueueOomAdjTargetLocked(processRecord);
        this.mAm.updateOomAdjLocked(processRecord, 6);
        try {
            try {
                int i2 = serviceRecord.appInfo.uid;
                String packageName = serviceRecord.name.getPackageName();
                String className = serviceRecord.name.getClassName();
                FrameworkStatsLog.write(100, i2, packageName, className);
                this.mAm.mBatteryStatsService.noteServiceStartLaunch(i2, packageName, className);
                this.mAm.notifyPackageUse(serviceRecord.serviceInfo.packageName, 1);
                iApplicationThread.scheduleCreateService(serviceRecord, serviceRecord.serviceInfo, (CompatibilityInfo) null, processRecord.mState.getReportedProcState());
                serviceRecord.postNotification();
                if (serviceRecord.allowlistManager) {
                    processServiceRecord.mAllowlistManager = true;
                }
                requestServiceBindingsLocked(serviceRecord, z);
                updateServiceClientActivitiesLocked(processServiceRecord, null, true);
                if (startService) {
                    processServiceRecord.addBoundClientUidsOfNewService(serviceRecord);
                }
                if (serviceRecord.startRequested && serviceRecord.callStart && serviceRecord.pendingStarts.size() == 0) {
                    serviceRecord.pendingStarts.add(new ServiceRecord.StartItem(serviceRecord, false, serviceRecord.makeNextStartId(), null, null, 0, null, null));
                }
                sendServiceArgsLocked(serviceRecord, z, true);
                if (serviceRecord.delayed) {
                    getServiceMapLocked(serviceRecord.userId).mDelayedStartList.remove(serviceRecord);
                    serviceRecord.delayed = false;
                }
                if (serviceRecord.delayedStop) {
                    serviceRecord.delayedStop = false;
                    if (serviceRecord.startRequested) {
                        stopServiceLocked(serviceRecord, z2);
                    }
                }
            } catch (DeadObjectException e) {
                Slog.w("ActivityManager", "Application dead when creating service " + serviceRecord);
                this.mAm.appDiedLocked(processRecord, "Died when creating service");
                throw e;
            }
        } catch (Throwable th) {
            boolean contains = this.mDestroyingServices.contains(serviceRecord);
            serviceDoneExecutingLocked(serviceRecord, contains, contains, false);
            if (startService) {
                processServiceRecord.stopService(serviceRecord);
                serviceRecord.setProcess(null, null, 0, null);
            }
            if (!contains) {
                scheduleServiceRestartLocked(serviceRecord, false);
            }
            throw th;
        }
    }

    public final void sendServiceArgsLocked(ServiceRecord serviceRecord, boolean z, boolean z2) throws TransactionTooLargeException {
        int i;
        int size = serviceRecord.pendingStarts.size();
        if (size == 0) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        while (true) {
            if (serviceRecord.pendingStarts.size() <= 0) {
                break;
            }
            ServiceRecord.StartItem remove = serviceRecord.pendingStarts.remove(0);
            if (remove.intent != null || size <= 1) {
                remove.deliveredTime = SystemClock.uptimeMillis();
                serviceRecord.deliveredStarts.add(remove);
                remove.deliveryCount++;
                NeededUriGrants neededUriGrants = remove.neededGrants;
                if (neededUriGrants != null) {
                    this.mAm.mUgmInternal.grantUriPermissionUncheckedFromIntent(neededUriGrants, remove.getUriPermissionsLocked());
                }
                this.mAm.grantImplicitAccess(serviceRecord.userId, remove.intent, remove.callingId, UserHandle.getAppId(serviceRecord.appInfo.uid));
                bumpServiceExecutingLocked(serviceRecord, z, "start", 0);
                if (serviceRecord.fgRequired && !serviceRecord.fgWaiting) {
                    if (!serviceRecord.isForeground) {
                        scheduleServiceForegroundTransitionTimeoutLocked(serviceRecord);
                    } else {
                        serviceRecord.fgRequired = false;
                    }
                }
                i = remove.deliveryCount > 1 ? 2 : 0;
                if (remove.doneExecutingCount > 0) {
                    i |= 1;
                }
                arrayList.add(new ServiceStartArgs(remove.taskRemoved, remove.f1121id, i, remove.intent));
            }
        }
        if (!z2) {
            this.mAm.enqueueOomAdjTargetLocked(serviceRecord.app);
            this.mAm.updateOomAdjPendingTargetsLocked(6);
        }
        ParceledListSlice parceledListSlice = new ParceledListSlice(arrayList);
        parceledListSlice.setInlineCountLimit(4);
        try {
            serviceRecord.app.getThread().scheduleServiceArgs(serviceRecord, parceledListSlice);
            e = null;
        } catch (TransactionTooLargeException e) {
            e = e;
            Slog.w("ActivityManager", "Failed delivering service starts", e);
        } catch (RemoteException e2) {
            e = e2;
            Slog.w("ActivityManager", "Failed delivering service starts", e);
        } catch (Exception e3) {
            e = e3;
            Slog.w("ActivityManager", "Unexpected exception", e);
        }
        if (e != null) {
            boolean contains = this.mDestroyingServices.contains(serviceRecord);
            int size2 = arrayList.size();
            while (i < size2) {
                serviceDoneExecutingLocked(serviceRecord, contains, contains, true);
                i++;
            }
            this.mAm.updateOomAdjPendingTargetsLocked(5);
            if (e instanceof TransactionTooLargeException) {
                throw ((TransactionTooLargeException) e);
            }
        }
    }

    public final boolean isServiceNeededLocked(ServiceRecord serviceRecord, boolean z, boolean z2) {
        if (serviceRecord.startRequested) {
            return true;
        }
        if (!z) {
            z2 = serviceRecord.hasAutoCreateConnections();
        }
        return z2;
    }

    public final void bringDownServiceIfNeededLocked(ServiceRecord serviceRecord, boolean z, boolean z2, boolean z3, String str) {
        if (isServiceNeededLocked(serviceRecord, z, z2) || this.mPendingServices.contains(serviceRecord)) {
            return;
        }
        bringDownServiceLocked(serviceRecord, z3);
    }

    public final void bringDownServiceLocked(final ServiceRecord serviceRecord, boolean z) {
        boolean z2;
        if (serviceRecord.isShortFgs()) {
            Slog.w("ActivityManager", "Short FGS brought down without stopping: " + serviceRecord);
            maybeStopShortFgsTimeoutLocked(serviceRecord);
        }
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = serviceRecord.getConnections();
        int size = connections.size() - 1;
        while (true) {
            if (size < 0) {
                break;
            }
            ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
            for (int i = 0; i < valueAt.size(); i++) {
                ConnectionRecord connectionRecord = valueAt.get(i);
                connectionRecord.serviceDead = true;
                connectionRecord.stopAssociation();
                try {
                    connectionRecord.conn.connected(serviceRecord.name, (IBinder) null, true);
                } catch (Exception e) {
                    Slog.w("ActivityManager", "Failure disconnecting service " + serviceRecord.shortInstanceName + " to connection " + valueAt.get(i).conn.asBinder() + " (in " + valueAt.get(i).binding.client.processName + ")", e);
                }
            }
            size--;
        }
        ProcessRecord processRecord = serviceRecord.app;
        if (processRecord == null || processRecord.getThread() == null) {
            z2 = false;
        } else {
            boolean z3 = false;
            for (int size2 = serviceRecord.bindings.size() - 1; size2 >= 0; size2--) {
                IntentBindRecord valueAt2 = serviceRecord.bindings.valueAt(size2);
                if (valueAt2.hasBound) {
                    try {
                        z3 |= bumpServiceExecutingLocked(serviceRecord, false, "bring down unbind", 5);
                        valueAt2.hasBound = false;
                        valueAt2.requested = false;
                        serviceRecord.app.getThread().scheduleUnbindService(serviceRecord, valueAt2.intent.getIntent());
                    } catch (Exception e2) {
                        Slog.w("ActivityManager", "Exception when unbinding service " + serviceRecord.shortInstanceName, e2);
                        serviceProcessGoneLocked(serviceRecord, z);
                    }
                }
            }
            z2 = z3;
        }
        if (serviceRecord.fgRequired) {
            Slog.w("ActivityManager", "Bringing down service while still waiting for start foreground: " + serviceRecord);
            serviceRecord.fgRequired = false;
            serviceRecord.fgWaiting = false;
            synchronized (this.mAm.mProcessStats.mLock) {
                ServiceState tracker = serviceRecord.getTracker();
                if (tracker != null) {
                    tracker.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
            }
            AppOpsService appOpsService = this.mAm.mAppOpsService;
            appOpsService.finishOperation(AppOpsManager.getToken(appOpsService), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, null);
            this.mAm.mHandler.removeMessages(66, serviceRecord);
            if (serviceRecord.app != null) {
                Message obtainMessage = this.mAm.mHandler.obtainMessage(69);
                SomeArgs obtain = SomeArgs.obtain();
                obtain.arg1 = serviceRecord.app;
                obtain.arg2 = serviceRecord.toString();
                obtain.arg3 = serviceRecord.getComponentName();
                obtainMessage.obj = obtain;
                this.mAm.mHandler.sendMessage(obtainMessage);
            }
        }
        serviceRecord.destroyTime = SystemClock.uptimeMillis();
        ServiceMap serviceMapLocked = getServiceMapLocked(serviceRecord.userId);
        ServiceRecord remove = serviceMapLocked.mServicesByInstanceName.remove(serviceRecord.instanceName);
        if (remove != null && remove != serviceRecord) {
            serviceMapLocked.mServicesByInstanceName.put(serviceRecord.instanceName, remove);
            throw new IllegalStateException("Bringing down " + serviceRecord + " but actually running " + remove);
        }
        serviceMapLocked.mServicesByIntent.remove(serviceRecord.intent);
        serviceRecord.totalRestartCount = 0;
        unscheduleServiceRestartLocked(serviceRecord, 0, true);
        for (int size3 = this.mPendingServices.size() - 1; size3 >= 0; size3--) {
            if (this.mPendingServices.get(size3) == serviceRecord) {
                this.mPendingServices.remove(size3);
            }
        }
        this.mPendingBringups.remove(serviceRecord);
        cancelForegroundNotificationLocked(serviceRecord);
        boolean z4 = serviceRecord.isForeground;
        if (z4) {
            maybeStopShortFgsTimeoutLocked(serviceRecord);
            decActiveForegroundAppLocked(serviceMapLocked, serviceRecord);
            synchronized (this.mAm.mProcessStats.mLock) {
                ServiceState tracker2 = serviceRecord.getTracker();
                if (tracker2 != null) {
                    tracker2.setForeground(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
            }
            AppOpsService appOpsService2 = this.mAm.mAppOpsService;
            appOpsService2.finishOperation(AppOpsManager.getToken(appOpsService2), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, null);
            unregisterAppOpCallbackLocked(serviceRecord);
            long uptimeMillis = SystemClock.uptimeMillis();
            serviceRecord.mFgsExitTime = uptimeMillis;
            long j = serviceRecord.mFgsEnterTime;
            logFGSStateChangeLocked(serviceRecord, 2, uptimeMillis > j ? (int) (uptimeMillis - j) : 0, 2, 0);
            synchronized (this.mFGSLogger) {
                this.mFGSLogger.logForegroundServiceStop(serviceRecord.appInfo.uid, serviceRecord);
            }
            this.mAm.updateForegroundServiceUsageStats(serviceRecord.name, serviceRecord.userId, false);
        }
        serviceRecord.isForeground = false;
        serviceRecord.mFgsNotificationWasDeferred = false;
        dropFgsNotificationStateLocked(serviceRecord);
        serviceRecord.foregroundId = 0;
        serviceRecord.foregroundNoti = null;
        resetFgsRestrictionLocked(serviceRecord);
        if (z4) {
            signalForegroundServiceObserversLocked(serviceRecord);
        }
        serviceRecord.clearDeliveredStartsLocked();
        serviceRecord.pendingStarts.clear();
        serviceMapLocked.mDelayedStartList.remove(serviceRecord);
        if (serviceRecord.app != null) {
            this.mAm.mBatteryStatsService.noteServiceStopLaunch(serviceRecord.appInfo.uid, serviceRecord.name.getPackageName(), serviceRecord.name.getClassName());
            stopServiceAndUpdateAllowlistManagerLocked(serviceRecord);
            if (serviceRecord.app.getThread() != null) {
                this.mAm.updateLruProcessLocked(serviceRecord.app, false, null);
                updateServiceForegroundLocked(serviceRecord.app.mServices, false);
                if (serviceRecord.mIsFgsDelegate) {
                    if (serviceRecord.mFgsDelegation.mConnection != null) {
                        this.mAm.mHandler.post(new Runnable() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                ActiveServices.lambda$bringDownServiceLocked$1(ServiceRecord.this);
                            }
                        });
                    }
                    int size4 = this.mFgsDelegations.size() - 1;
                    while (true) {
                        if (size4 < 0) {
                            break;
                        } else if (this.mFgsDelegations.valueAt(size4) == serviceRecord) {
                            this.mFgsDelegations.removeAt(size4);
                            break;
                        } else {
                            size4--;
                        }
                    }
                } else {
                    try {
                        z2 |= bumpServiceExecutingLocked(serviceRecord, false, "destroy", z2 ? 0 : 5);
                        this.mDestroyingServices.add(serviceRecord);
                        serviceRecord.destroying = true;
                        serviceRecord.app.getThread().scheduleStopService(serviceRecord);
                    } catch (Exception e3) {
                        Slog.w("ActivityManager", "Exception when destroying service " + serviceRecord.shortInstanceName, e3);
                        serviceProcessGoneLocked(serviceRecord, z);
                    }
                }
            }
        }
        if (!z2) {
            this.mAm.enqueueOomAdjTargetLocked(serviceRecord.app);
            if (!z) {
                this.mAm.updateOomAdjPendingTargetsLocked(5);
            }
        }
        if (serviceRecord.bindings.size() > 0) {
            serviceRecord.bindings.clear();
        }
        Runnable runnable = serviceRecord.restarter;
        if (runnable instanceof ServiceRestarter) {
            ((ServiceRestarter) runnable).setService(null);
        }
        synchronized (this.mAm.mProcessStats.mLock) {
            int memFactorLocked = this.mAm.mProcessStats.getMemFactorLocked();
            if (serviceRecord.tracker != null) {
                long uptimeMillis2 = SystemClock.uptimeMillis();
                serviceRecord.tracker.setStarted(false, memFactorLocked, uptimeMillis2);
                serviceRecord.tracker.setBound(false, memFactorLocked, uptimeMillis2);
                if (serviceRecord.executeNesting == 0) {
                    serviceRecord.tracker.clearCurrentOwner(serviceRecord, false);
                    serviceRecord.tracker = null;
                }
            }
        }
        serviceMapLocked.ensureNotStartingBackgroundLocked(serviceRecord);
        updateNumForegroundServicesLocked();
    }

    public static /* synthetic */ void lambda$bringDownServiceLocked$1(ServiceRecord serviceRecord) {
        ForegroundServiceDelegation foregroundServiceDelegation = serviceRecord.mFgsDelegation;
        foregroundServiceDelegation.mConnection.onServiceDisconnected(foregroundServiceDelegation.mOptions.getComponentName());
    }

    public final void dropFgsNotificationStateLocked(ServiceRecord serviceRecord) {
        if (serviceRecord.foregroundNoti == null) {
            return;
        }
        ServiceMap serviceMap = this.mServiceMap.get(serviceRecord.userId);
        boolean z = false;
        if (serviceMap != null) {
            int size = serviceMap.mServicesByInstanceName.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                ServiceRecord valueAt = serviceMap.mServicesByInstanceName.valueAt(i);
                if (valueAt != serviceRecord && valueAt.isForeground && serviceRecord.foregroundId == valueAt.foregroundId && serviceRecord.appInfo.packageName.equals(valueAt.appInfo.packageName)) {
                    z = true;
                    break;
                }
                i++;
            }
        } else {
            Slog.wtf("ActivityManager", "FGS " + serviceRecord + " not found!");
        }
        if (z) {
            return;
        }
        serviceRecord.stripForegroundServiceFlagFromNotification();
    }

    public void removeConnectionLocked(ConnectionRecord connectionRecord, ProcessRecord processRecord, ActivityServiceConnectionsHolder activityServiceConnectionsHolder, boolean z) {
        ProcessRecord processRecord2;
        IBinder asBinder = connectionRecord.conn.asBinder();
        AppBindRecord appBindRecord = connectionRecord.binding;
        ServiceRecord serviceRecord = appBindRecord.service;
        ArrayList<ConnectionRecord> arrayList = serviceRecord.getConnections().get(asBinder);
        if (arrayList != null) {
            arrayList.remove(connectionRecord);
            if (arrayList.size() == 0) {
                serviceRecord.removeConnection(asBinder);
            }
        }
        appBindRecord.connections.remove(connectionRecord);
        connectionRecord.stopAssociation();
        ActivityServiceConnectionsHolder<ConnectionRecord> activityServiceConnectionsHolder2 = connectionRecord.activity;
        if (activityServiceConnectionsHolder2 != null && activityServiceConnectionsHolder2 != activityServiceConnectionsHolder) {
            activityServiceConnectionsHolder2.removeConnection(connectionRecord);
        }
        ProcessRecord processRecord3 = appBindRecord.client;
        if (processRecord3 != processRecord) {
            ProcessServiceRecord processServiceRecord = processRecord3.mServices;
            processServiceRecord.removeConnection(connectionRecord);
            if (connectionRecord.hasFlag(8)) {
                processServiceRecord.updateHasAboveClientLocked();
            }
            if (connectionRecord.hasFlag(16777216)) {
                serviceRecord.updateAllowlistManager();
                if (!serviceRecord.allowlistManager && (processRecord2 = serviceRecord.app) != null) {
                    updateAllowlistManagerLocked(processRecord2.mServices);
                }
            }
            if (connectionRecord.hasFlag(1048576)) {
                serviceRecord.updateIsAllowedBgActivityStartsByBinding();
            }
            if (connectionRecord.hasFlag(65536)) {
                processServiceRecord.updateHasTopStartedAlmostPerceptibleServices();
            }
            ProcessRecord processRecord4 = serviceRecord.app;
            if (processRecord4 != null) {
                updateServiceClientActivitiesLocked(processRecord4.mServices, connectionRecord, true);
            }
        }
        ArrayList<ConnectionRecord> arrayList2 = this.mServiceConnections.get(asBinder);
        if (arrayList2 != null) {
            arrayList2.remove(connectionRecord);
            if (arrayList2.size() == 0) {
                this.mServiceConnections.remove(asBinder);
            }
        }
        ActivityManagerService activityManagerService = this.mAm;
        ProcessRecord processRecord5 = appBindRecord.client;
        int i = processRecord5.uid;
        String str = processRecord5.processName;
        ApplicationInfo applicationInfo = serviceRecord.appInfo;
        activityManagerService.stopAssociationLocked(i, str, applicationInfo.uid, applicationInfo.longVersionCode, serviceRecord.instanceName, serviceRecord.processName);
        if (appBindRecord.connections.size() == 0) {
            appBindRecord.intent.apps.remove(appBindRecord.client);
        }
        if (connectionRecord.serviceDead) {
            return;
        }
        ProcessRecord processRecord6 = serviceRecord.app;
        if (processRecord6 != null && processRecord6.getThread() != null && appBindRecord.intent.apps.size() == 0 && appBindRecord.intent.hasBound) {
            try {
                bumpServiceExecutingLocked(serviceRecord, false, "unbind", 5);
                if (appBindRecord.client != serviceRecord.app && connectionRecord.notHasFlag(32) && serviceRecord.app.mState.getSetProcState() <= 13) {
                    this.mAm.updateLruProcessLocked(serviceRecord.app, false, null);
                }
                IntentBindRecord intentBindRecord = appBindRecord.intent;
                intentBindRecord.hasBound = false;
                intentBindRecord.doRebind = false;
                serviceRecord.app.getThread().scheduleUnbindService(serviceRecord, appBindRecord.intent.intent.getIntent());
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception when unbinding service " + serviceRecord.shortInstanceName, e);
                serviceProcessGoneLocked(serviceRecord, z);
            }
        }
        if (serviceRecord.getConnections().isEmpty()) {
            this.mPendingServices.remove(serviceRecord);
            this.mPendingBringups.remove(serviceRecord);
        }
        if (connectionRecord.hasFlag(1)) {
            boolean hasAutoCreateConnections = serviceRecord.hasAutoCreateConnections();
            if (!hasAutoCreateConnections && serviceRecord.tracker != null) {
                synchronized (this.mAm.mProcessStats.mLock) {
                    serviceRecord.tracker.setBound(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                }
            }
            bringDownServiceIfNeededLocked(serviceRecord, true, hasAutoCreateConnections, z, "removeConnection");
        }
    }

    public void serviceDoneExecutingLocked(ServiceRecord serviceRecord, int i, int i2, int i3, boolean z) {
        boolean contains = this.mDestroyingServices.contains(serviceRecord);
        if (serviceRecord != null) {
            if (i == 1) {
                serviceRecord.callStart = true;
                if (i3 != 1000) {
                    serviceRecord.startCommandResult = i3;
                }
                if (i3 == 0 || i3 == 1) {
                    serviceRecord.findDeliveredStart(i2, false, true);
                    serviceRecord.stopIfKilled = false;
                } else if (i3 == 2) {
                    serviceRecord.findDeliveredStart(i2, false, true);
                    if (serviceRecord.getLastStartId() == i2) {
                        serviceRecord.stopIfKilled = true;
                    }
                } else if (i3 == 3) {
                    ServiceRecord.StartItem findDeliveredStart = serviceRecord.findDeliveredStart(i2, false, false);
                    if (findDeliveredStart != null) {
                        findDeliveredStart.deliveryCount = 0;
                        findDeliveredStart.doneExecutingCount++;
                        serviceRecord.stopIfKilled = true;
                    }
                } else if (i3 == 1000) {
                    serviceRecord.findDeliveredStart(i2, true, true);
                } else {
                    throw new IllegalArgumentException("Unknown service start result: " + i3);
                }
                if (i3 == 0) {
                    serviceRecord.callStart = false;
                }
            } else if (i == 2) {
                if (!contains) {
                    if (serviceRecord.app != null) {
                        Slog.w("ActivityManager", "Service done with onDestroy, but not inDestroying: " + serviceRecord + ", app=" + serviceRecord.app);
                    }
                } else if (serviceRecord.executeNesting != 1) {
                    Slog.w("ActivityManager", "Service done with onDestroy, but executeNesting=" + serviceRecord.executeNesting + ": " + serviceRecord);
                    serviceRecord.executeNesting = 1;
                }
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            serviceDoneExecutingLocked(serviceRecord, contains, contains, z);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return;
        }
        Slog.w("ActivityManager", "Done executing unknown service from pid " + Binder.getCallingPid());
    }

    public final void serviceProcessGoneLocked(ServiceRecord serviceRecord, boolean z) {
        if (serviceRecord.tracker != null) {
            synchronized (this.mAm.mProcessStats.mLock) {
                int memFactorLocked = this.mAm.mProcessStats.getMemFactorLocked();
                long uptimeMillis = SystemClock.uptimeMillis();
                serviceRecord.tracker.setExecuting(false, memFactorLocked, uptimeMillis);
                serviceRecord.tracker.setForeground(false, memFactorLocked, uptimeMillis);
                serviceRecord.tracker.setBound(false, memFactorLocked, uptimeMillis);
                serviceRecord.tracker.setStarted(false, memFactorLocked, uptimeMillis);
            }
        }
        serviceDoneExecutingLocked(serviceRecord, true, true, z);
    }

    public final void serviceDoneExecutingLocked(ServiceRecord serviceRecord, boolean z, boolean z2, boolean z3) {
        int i = serviceRecord.executeNesting - 1;
        serviceRecord.executeNesting = i;
        if (i <= 0) {
            ProcessRecord processRecord = serviceRecord.app;
            if (processRecord != null) {
                ProcessServiceRecord processServiceRecord = processRecord.mServices;
                processServiceRecord.setExecServicesFg(false);
                processServiceRecord.stopExecutingService(serviceRecord);
                if (processServiceRecord.numberOfExecutingServices() == 0) {
                    this.mAm.mHandler.removeMessages(12, serviceRecord.app);
                } else if (serviceRecord.executeFg) {
                    int numberOfExecutingServices = processServiceRecord.numberOfExecutingServices() - 1;
                    while (true) {
                        if (numberOfExecutingServices < 0) {
                            break;
                        } else if (processServiceRecord.getExecutingServiceAt(numberOfExecutingServices).executeFg) {
                            processServiceRecord.setExecServicesFg(true);
                            break;
                        } else {
                            numberOfExecutingServices--;
                        }
                    }
                }
                if (z) {
                    this.mDestroyingServices.remove(serviceRecord);
                    serviceRecord.bindings.clear();
                }
                if (z3) {
                    this.mAm.enqueueOomAdjTargetLocked(serviceRecord.app);
                } else {
                    this.mAm.updateOomAdjLocked(serviceRecord.app, 5);
                }
            }
            serviceRecord.executeFg = false;
            if (serviceRecord.tracker != null) {
                synchronized (this.mAm.mProcessStats.mLock) {
                    int memFactorLocked = this.mAm.mProcessStats.getMemFactorLocked();
                    long uptimeMillis = SystemClock.uptimeMillis();
                    serviceRecord.tracker.setExecuting(false, memFactorLocked, uptimeMillis);
                    serviceRecord.tracker.setForeground(false, memFactorLocked, uptimeMillis);
                    if (z2) {
                        serviceRecord.tracker.clearCurrentOwner(serviceRecord, false);
                        serviceRecord.tracker = null;
                    }
                }
            }
            if (z2) {
                ProcessRecord processRecord2 = serviceRecord.app;
                if (processRecord2 != null && !processRecord2.isPersistent()) {
                    stopServiceAndUpdateAllowlistManagerLocked(serviceRecord);
                }
                serviceRecord.setProcess(null, null, 0, null);
            }
        }
    }

    public boolean attachApplicationLocked(ProcessRecord processRecord, String str) throws RemoteException {
        boolean z;
        ServiceRecord serviceRecord;
        long j;
        processRecord.mState.setBackgroundRestricted(appRestrictedAnyInBackground(processRecord.uid, processRecord.info.packageName));
        if (this.mPendingServices.size() > 0) {
            ServiceRecord serviceRecord2 = null;
            int i = 0;
            z = false;
            while (i < this.mPendingServices.size()) {
                try {
                    serviceRecord = this.mPendingServices.get(i);
                } catch (RemoteException e) {
                    e = e;
                }
                try {
                    if (processRecord == serviceRecord.isolationHostProc || (processRecord.uid == serviceRecord.appInfo.uid && str.equals(serviceRecord.processName))) {
                        IApplicationThread thread = processRecord.getThread();
                        int pid = processRecord.getPid();
                        UidRecord uidRecord = processRecord.getUidRecord();
                        this.mPendingServices.remove(i);
                        int i2 = i - 1;
                        ApplicationInfo applicationInfo = serviceRecord.appInfo;
                        processRecord.addPackage(applicationInfo.packageName, applicationInfo.longVersionCode, this.mAm.mProcessStats);
                        try {
                            if (Trace.isTagEnabled(64L)) {
                                Trace.traceBegin(64L, "realStartServiceLocked: " + serviceRecord.shortInstanceName);
                            }
                            j = 64;
                            try {
                                realStartServiceLocked(serviceRecord, processRecord, thread, pid, uidRecord, serviceRecord.createdFromFg, true);
                                Trace.traceEnd(64L);
                                if (!isServiceNeededLocked(serviceRecord, false, false)) {
                                    bringDownServiceLocked(serviceRecord, true);
                                }
                                this.mAm.updateOomAdjPendingTargetsLocked(6);
                                z = true;
                                i = i2;
                            } catch (Throwable th) {
                                th = th;
                                Trace.traceEnd(j);
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            j = 64;
                        }
                    }
                    i++;
                    serviceRecord2 = serviceRecord;
                } catch (RemoteException e2) {
                    e = e2;
                    serviceRecord2 = serviceRecord;
                    Slog.w("ActivityManager", "Exception in new application when starting service " + serviceRecord2.shortInstanceName, e);
                    throw e;
                }
            }
        } else {
            z = false;
        }
        if (this.mRestartingServices.size() > 0) {
            boolean z2 = false;
            for (int i3 = 0; i3 < this.mRestartingServices.size(); i3++) {
                ServiceRecord serviceRecord3 = this.mRestartingServices.get(i3);
                if (processRecord == serviceRecord3.isolationHostProc || (processRecord.uid == serviceRecord3.appInfo.uid && str.equals(serviceRecord3.processName))) {
                    this.mAm.mHandler.removeCallbacks(serviceRecord3.restarter);
                    this.mAm.mHandler.post(serviceRecord3.restarter);
                    z2 = true;
                }
            }
            if (z2) {
                this.mAm.mHandler.post(new Runnable() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActiveServices.this.lambda$attachApplicationLocked$2();
                    }
                });
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$attachApplicationLocked$2() {
        long uptimeMillis = SystemClock.uptimeMillis();
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                rescheduleServiceRestartIfPossibleLocked(getExtraRestartTimeInBetweenLocked(), this.mAm.mConstants.SERVICE_MIN_RESTART_TIME_BETWEEN, "other", uptimeMillis);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public void processStartTimedOutLocked(ProcessRecord processRecord) {
        int size = this.mPendingServices.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            ServiceRecord serviceRecord = this.mPendingServices.get(i);
            if ((processRecord.uid == serviceRecord.appInfo.uid && processRecord.processName.equals(serviceRecord.processName)) || serviceRecord.isolationHostProc == processRecord) {
                Slog.w("ActivityManager", "Forcing bringing down service: " + serviceRecord);
                serviceRecord.isolationHostProc = null;
                this.mPendingServices.remove(i);
                size = this.mPendingServices.size();
                i--;
                bringDownServiceLocked(serviceRecord, true);
                z = true;
            }
            i++;
        }
        if (z) {
            this.mAm.updateOomAdjPendingTargetsLocked(5);
        }
    }

    public final boolean collectPackageServicesLocked(String str, Set<String> set, boolean z, boolean z2, ArrayMap<ComponentName, ServiceRecord> arrayMap) {
        ProcessRecord processRecord;
        boolean z3 = false;
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            ServiceRecord valueAt = arrayMap.valueAt(size);
            if ((str == null || (valueAt.packageName.equals(str) && (set == null || set.contains(valueAt.name.getClassName())))) && ((processRecord = valueAt.app) == null || z || !processRecord.isPersistent())) {
                if (!z2) {
                    return true;
                }
                Slog.i("ActivityManager", "  Force stopping service " + valueAt);
                ProcessRecord processRecord2 = valueAt.app;
                if (processRecord2 != null && !processRecord2.isPersistent()) {
                    stopServiceAndUpdateAllowlistManagerLocked(valueAt);
                }
                valueAt.setProcess(null, null, 0, null);
                valueAt.isolationHostProc = null;
                if (this.mTmpCollectionResults == null) {
                    this.mTmpCollectionResults = new ArrayList<>();
                }
                this.mTmpCollectionResults.add(valueAt);
                z3 = true;
            }
        }
        return z3;
    }

    public boolean bringDownDisabledPackageServicesLocked(String str, Set<String> set, int i, boolean z, boolean z2, boolean z3) {
        ArrayList<ServiceRecord> arrayList = this.mTmpCollectionResults;
        if (arrayList != null) {
            arrayList.clear();
        }
        if (i == -1) {
            for (int size = this.mServiceMap.size() - 1; size >= 0; size--) {
                r2 |= collectPackageServicesLocked(str, set, z, z3, this.mServiceMap.valueAt(size).mServicesByInstanceName);
                if (!z3 && r2) {
                    return true;
                }
                if (z3 && set == null) {
                    forceStopPackageLocked(str, this.mServiceMap.valueAt(size).mUserId);
                }
            }
        } else {
            ServiceMap serviceMap = this.mServiceMap.get(i);
            r2 = serviceMap != null ? collectPackageServicesLocked(str, set, z, z3, serviceMap.mServicesByInstanceName) : false;
            if (z3 && set == null) {
                forceStopPackageLocked(str, i);
            }
        }
        ArrayList<ServiceRecord> arrayList2 = this.mTmpCollectionResults;
        if (arrayList2 != null) {
            int size2 = arrayList2.size();
            for (int i2 = size2 - 1; i2 >= 0; i2--) {
                bringDownServiceLocked(this.mTmpCollectionResults.get(i2), true);
            }
            if (size2 > 0) {
                this.mAm.updateOomAdjPendingTargetsLocked(5);
            }
            if (z2 && !this.mTmpCollectionResults.isEmpty()) {
                final ArrayList arrayList3 = (ArrayList) this.mTmpCollectionResults.clone();
                this.mAm.mHandler.postDelayed(new Runnable() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        ActiveServices.lambda$bringDownDisabledPackageServicesLocked$3(arrayList3);
                    }
                }, 250L);
            }
            this.mTmpCollectionResults.clear();
        }
        return r2;
    }

    public static /* synthetic */ void lambda$bringDownDisabledPackageServicesLocked$3(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            ((ServiceRecord) arrayList.get(i)).cancelNotification();
        }
    }

    @GuardedBy({"mAm"})
    public final void signalForegroundServiceObserversLocked(ServiceRecord serviceRecord) {
        int beginBroadcast = this.mFgsObservers.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mFgsObservers.getBroadcastItem(i).onForegroundStateChanged(serviceRecord, serviceRecord.appInfo.packageName, serviceRecord.userId, serviceRecord.isForeground);
            } catch (RemoteException unused) {
            }
        }
        this.mFgsObservers.finishBroadcast();
    }

    @GuardedBy({"mAm"})
    public boolean registerForegroundServiceObserverLocked(int i, IForegroundServiceObserver iForegroundServiceObserver) {
        try {
            int size = this.mServiceMap.size();
            for (int i2 = 0; i2 < size; i2++) {
                ServiceMap valueAt = this.mServiceMap.valueAt(i2);
                if (valueAt != null) {
                    int size2 = valueAt.mServicesByInstanceName.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        ServiceRecord valueAt2 = valueAt.mServicesByInstanceName.valueAt(i3);
                        if (valueAt2.isForeground) {
                            ApplicationInfo applicationInfo = valueAt2.appInfo;
                            if (i == applicationInfo.uid) {
                                iForegroundServiceObserver.onForegroundStateChanged(valueAt2, applicationInfo.packageName, valueAt2.userId, true);
                            }
                        }
                    }
                }
            }
            this.mFgsObservers.register(iForegroundServiceObserver);
            return true;
        } catch (RemoteException unused) {
            Slog.e("ActivityManager", "Bad FGS observer from uid " + i);
            return false;
        }
    }

    public void forceStopPackageLocked(String str, int i) {
        ServiceMap serviceMap = this.mServiceMap.get(i);
        if (serviceMap != null && serviceMap.mActiveForegroundApps.size() > 0) {
            for (int size = serviceMap.mActiveForegroundApps.size() - 1; size >= 0; size--) {
                if (serviceMap.mActiveForegroundApps.valueAt(size).mPackageName.equals(str)) {
                    serviceMap.mActiveForegroundApps.removeAt(size);
                    serviceMap.mActiveForegroundAppsChanged = true;
                }
            }
            if (serviceMap.mActiveForegroundAppsChanged) {
                requestUpdateActiveForegroundAppsLocked(serviceMap, 0L);
            }
        }
        for (int size2 = this.mPendingBringups.size() - 1; size2 >= 0; size2--) {
            ServiceRecord keyAt = this.mPendingBringups.keyAt(size2);
            if (TextUtils.equals(keyAt.packageName, str) && keyAt.userId == i) {
                this.mPendingBringups.removeAt(size2);
            }
        }
        removeServiceRestartBackoffEnabledLocked(str);
        removeServiceNotificationDeferralsLocked(str, i);
    }

    public void cleanUpServices(int i, ComponentName componentName, Intent intent) {
        ArrayList arrayList = new ArrayList();
        ArrayMap<ComponentName, ServiceRecord> servicesLocked = getServicesLocked(i);
        boolean z = true;
        for (int size = servicesLocked.size() - 1; size >= 0; size--) {
            ServiceRecord valueAt = servicesLocked.valueAt(size);
            if (valueAt.packageName.equals(componentName.getPackageName())) {
                arrayList.add(valueAt);
            }
        }
        boolean z2 = false;
        for (int size2 = arrayList.size() - 1; size2 >= 0; size2--) {
            ServiceRecord serviceRecord = (ServiceRecord) arrayList.get(size2);
            if (serviceRecord.startRequested) {
                if ((serviceRecord.serviceInfo.flags & (z ? 1 : 0)) != 0) {
                    Slog.i("ActivityManager", "Stopping service " + serviceRecord.shortInstanceName + ": remove task");
                    stopServiceLocked(serviceRecord, z);
                    z2 = z ? 1 : 0;
                } else {
                    serviceRecord.pendingStarts.add(new ServiceRecord.StartItem(serviceRecord, true, serviceRecord.getLastStartId(), intent, null, 0, null, null));
                    ProcessRecord processRecord = serviceRecord.app;
                    if (processRecord == null || processRecord.getThread() == null) {
                        z = true;
                    } else {
                        z = true;
                        z = true;
                        try {
                            sendServiceArgsLocked(serviceRecord, true, false);
                        } catch (TransactionTooLargeException unused) {
                        }
                    }
                }
            }
        }
        if (z2) {
            this.mAm.updateOomAdjPendingTargetsLocked(5);
        }
    }

    public final void killServicesLocked(ProcessRecord processRecord, boolean z) {
        ProcessServiceRecord processServiceRecord = processRecord.mServices;
        for (int numberOfConnections = processServiceRecord.numberOfConnections() - 1; numberOfConnections >= 0; numberOfConnections--) {
            removeConnectionLocked(processServiceRecord.getConnectionAt(numberOfConnections), processRecord, null, true);
        }
        updateServiceConnectionActivitiesLocked(processServiceRecord);
        processServiceRecord.removeAllConnections();
        processServiceRecord.mAllowlistManager = false;
        for (int numberOfRunningServices = processServiceRecord.numberOfRunningServices() - 1; numberOfRunningServices >= 0; numberOfRunningServices--) {
            ServiceRecord runningServiceAt = processServiceRecord.getRunningServiceAt(numberOfRunningServices);
            this.mAm.mBatteryStatsService.noteServiceStopLaunch(runningServiceAt.appInfo.uid, runningServiceAt.name.getPackageName(), runningServiceAt.name.getClassName());
            ProcessRecord processRecord2 = runningServiceAt.app;
            if (processRecord2 != processRecord && processRecord2 != null && !processRecord2.isPersistent()) {
                runningServiceAt.app.mServices.stopService(runningServiceAt);
                runningServiceAt.app.mServices.updateBoundClientUids();
            }
            runningServiceAt.setProcess(null, null, 0, null);
            runningServiceAt.isolationHostProc = null;
            runningServiceAt.executeNesting = 0;
            synchronized (this.mAm.mProcessStats.mLock) {
                runningServiceAt.forceClearTracker();
            }
            this.mDestroyingServices.remove(runningServiceAt);
            for (int size = runningServiceAt.bindings.size() - 1; size >= 0; size--) {
                IntentBindRecord valueAt = runningServiceAt.bindings.valueAt(size);
                valueAt.binder = null;
                valueAt.hasBound = false;
                valueAt.received = false;
                valueAt.requested = false;
                for (int size2 = valueAt.apps.size() - 1; size2 >= 0; size2--) {
                    ProcessRecord keyAt = valueAt.apps.keyAt(size2);
                    if (!keyAt.isKilledByAm() && keyAt.getThread() != null) {
                        AppBindRecord valueAt2 = valueAt.apps.valueAt(size2);
                        for (int size3 = valueAt2.connections.size() - 1; size3 >= 0; size3--) {
                            ConnectionRecord valueAt3 = valueAt2.connections.valueAt(size3);
                            if (!valueAt3.hasFlag(1) || !valueAt3.notHasFlag(48)) {
                            }
                        }
                    }
                }
            }
        }
        ServiceMap serviceMapLocked = getServiceMapLocked(processRecord.userId);
        for (int numberOfRunningServices2 = processServiceRecord.numberOfRunningServices() - 1; numberOfRunningServices2 >= 0; numberOfRunningServices2--) {
            ServiceRecord runningServiceAt2 = processServiceRecord.getRunningServiceAt(numberOfRunningServices2);
            if (!processRecord.isPersistent()) {
                processServiceRecord.stopService(runningServiceAt2);
                processServiceRecord.updateBoundClientUids();
            }
            ServiceRecord serviceRecord = serviceMapLocked.mServicesByInstanceName.get(runningServiceAt2.instanceName);
            if (serviceRecord != runningServiceAt2) {
                if (serviceRecord != null) {
                    Slog.wtf("ActivityManager", "Service " + runningServiceAt2 + " in process " + processRecord + " not same as in map: " + serviceRecord);
                }
            } else if (z && runningServiceAt2.crashCount >= this.mAm.mConstants.BOUND_SERVICE_MAX_CRASH_RETRY && (runningServiceAt2.serviceInfo.applicationInfo.flags & 8) == 0) {
                Slog.w("ActivityManager", "Service crashed " + runningServiceAt2.crashCount + " times, stopping: " + runningServiceAt2);
                Object[] objArr = new Object[4];
                objArr[0] = Integer.valueOf(runningServiceAt2.userId);
                objArr[1] = Integer.valueOf(runningServiceAt2.crashCount);
                objArr[2] = runningServiceAt2.shortInstanceName;
                ProcessRecord processRecord3 = runningServiceAt2.app;
                objArr[3] = Integer.valueOf(processRecord3 != null ? processRecord3.getPid() : -1);
                EventLog.writeEvent(30034, objArr);
                bringDownServiceLocked(runningServiceAt2, true);
            } else if (!z || !this.mAm.mUserController.isUserRunning(runningServiceAt2.userId, 0)) {
                bringDownServiceLocked(runningServiceAt2, true);
            } else if (!scheduleServiceRestartLocked(runningServiceAt2, true)) {
                bringDownServiceLocked(runningServiceAt2, true);
            } else if (runningServiceAt2.canStopIfKilled(false)) {
                runningServiceAt2.startRequested = false;
                if (runningServiceAt2.tracker != null) {
                    synchronized (this.mAm.mProcessStats.mLock) {
                        runningServiceAt2.tracker.setStarted(false, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
                    }
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
        this.mAm.updateOomAdjPendingTargetsLocked(5);
        if (!z) {
            processServiceRecord.stopAllServices();
            processServiceRecord.clearBoundClientUids();
            for (int size4 = this.mRestartingServices.size() - 1; size4 >= 0; size4--) {
                ServiceRecord serviceRecord2 = this.mRestartingServices.get(size4);
                if (serviceRecord2.processName.equals(processRecord.processName) && serviceRecord2.serviceInfo.applicationInfo.uid == processRecord.info.uid) {
                    this.mRestartingServices.remove(size4);
                    clearRestartingIfNeededLocked(serviceRecord2);
                }
            }
            for (int size5 = this.mPendingServices.size() - 1; size5 >= 0; size5--) {
                ServiceRecord serviceRecord3 = this.mPendingServices.get(size5);
                if (serviceRecord3.processName.equals(processRecord.processName) && serviceRecord3.serviceInfo.applicationInfo.uid == processRecord.info.uid) {
                    this.mPendingServices.remove(size5);
                }
            }
            for (int size6 = this.mPendingBringups.size() - 1; size6 >= 0; size6--) {
                ServiceRecord keyAt2 = this.mPendingBringups.keyAt(size6);
                if (keyAt2.processName.equals(processRecord.processName) && keyAt2.serviceInfo.applicationInfo.uid == processRecord.info.uid) {
                    this.mPendingBringups.removeAt(size6);
                }
            }
        }
        int size7 = this.mDestroyingServices.size();
        while (size7 > 0) {
            size7--;
            ServiceRecord serviceRecord4 = this.mDestroyingServices.get(size7);
            if (serviceRecord4.app == processRecord) {
                synchronized (this.mAm.mProcessStats.mLock) {
                    serviceRecord4.forceClearTracker();
                }
                this.mDestroyingServices.remove(size7);
            }
        }
        processServiceRecord.stopAllExecutingServices();
    }

    public ActivityManager.RunningServiceInfo makeRunningServiceInfoLocked(ServiceRecord serviceRecord) {
        ActivityManager.RunningServiceInfo runningServiceInfo = new ActivityManager.RunningServiceInfo();
        runningServiceInfo.service = serviceRecord.name;
        ProcessRecord processRecord = serviceRecord.app;
        if (processRecord != null) {
            runningServiceInfo.pid = processRecord.getPid();
        }
        runningServiceInfo.uid = serviceRecord.appInfo.uid;
        runningServiceInfo.process = serviceRecord.processName;
        runningServiceInfo.foreground = serviceRecord.isForeground;
        runningServiceInfo.activeSince = serviceRecord.createRealTime;
        runningServiceInfo.started = serviceRecord.startRequested;
        runningServiceInfo.clientCount = serviceRecord.getConnections().size();
        runningServiceInfo.crashCount = serviceRecord.crashCount;
        runningServiceInfo.lastActivityTime = serviceRecord.lastActivity;
        if (serviceRecord.isForeground) {
            runningServiceInfo.flags |= 2;
        }
        if (serviceRecord.startRequested) {
            runningServiceInfo.flags |= 1;
        }
        ProcessRecord processRecord2 = serviceRecord.app;
        if (processRecord2 != null && processRecord2.getPid() == ActivityManagerService.MY_PID) {
            runningServiceInfo.flags |= 4;
        }
        ProcessRecord processRecord3 = serviceRecord.app;
        if (processRecord3 != null && processRecord3.isPersistent()) {
            runningServiceInfo.flags |= 8;
        }
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = serviceRecord.getConnections();
        for (int size = connections.size() - 1; size >= 0; size--) {
            ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
            for (int i = 0; i < valueAt.size(); i++) {
                ConnectionRecord connectionRecord = valueAt.get(i);
                if (connectionRecord.clientLabel != 0) {
                    runningServiceInfo.clientPackage = connectionRecord.binding.client.info.packageName;
                    runningServiceInfo.clientLabel = connectionRecord.clientLabel;
                    return runningServiceInfo;
                }
            }
        }
        return runningServiceInfo;
    }

    public List<ActivityManager.RunningServiceInfo> getRunningServiceInfoLocked(int i, int i2, int i3, boolean z, boolean z2) {
        ProcessRecord processRecord;
        ProcessRecord processRecord2;
        ArrayList arrayList = new ArrayList();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        int i4 = 0;
        try {
            if (z2) {
                int[] users = this.mAm.mUserController.getUsers();
                for (int i5 = 0; i5 < users.length && arrayList.size() < i; i5++) {
                    ArrayMap<ComponentName, ServiceRecord> servicesLocked = getServicesLocked(users[i5]);
                    for (int i6 = 0; i6 < servicesLocked.size() && arrayList.size() < i; i6++) {
                        arrayList.add(makeRunningServiceInfoLocked(servicesLocked.valueAt(i6)));
                    }
                }
                while (i4 < this.mRestartingServices.size() && arrayList.size() < i) {
                    ServiceRecord serviceRecord = this.mRestartingServices.get(i4);
                    ActivityManager.RunningServiceInfo makeRunningServiceInfoLocked = makeRunningServiceInfoLocked(serviceRecord);
                    makeRunningServiceInfoLocked.restarting = serviceRecord.nextRestartTime;
                    arrayList.add(makeRunningServiceInfoLocked);
                    i4++;
                }
            } else {
                int userId = UserHandle.getUserId(i3);
                ArrayMap<ComponentName, ServiceRecord> servicesLocked2 = getServicesLocked(userId);
                for (int i7 = 0; i7 < servicesLocked2.size() && arrayList.size() < i; i7++) {
                    ServiceRecord valueAt = servicesLocked2.valueAt(i7);
                    if (z || ((processRecord2 = valueAt.app) != null && processRecord2.uid == i3)) {
                        arrayList.add(makeRunningServiceInfoLocked(valueAt));
                    }
                }
                while (i4 < this.mRestartingServices.size() && arrayList.size() < i) {
                    ServiceRecord serviceRecord2 = this.mRestartingServices.get(i4);
                    if (serviceRecord2.userId == userId && (z || ((processRecord = serviceRecord2.app) != null && processRecord.uid == i3))) {
                        ActivityManager.RunningServiceInfo makeRunningServiceInfoLocked2 = makeRunningServiceInfoLocked(serviceRecord2);
                        makeRunningServiceInfoLocked2.restarting = serviceRecord2.nextRestartTime;
                        arrayList.add(makeRunningServiceInfoLocked2);
                    }
                    i4++;
                }
            }
            return arrayList;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public PendingIntent getRunningServiceControlPanelLocked(ComponentName componentName) {
        ServiceRecord serviceByNameLocked = getServiceByNameLocked(componentName, UserHandle.getUserId(Binder.getCallingUid()));
        if (serviceByNameLocked != null) {
            ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = serviceByNameLocked.getConnections();
            for (int size = connections.size() - 1; size >= 0; size--) {
                ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
                for (int i = 0; i < valueAt.size(); i++) {
                    if (valueAt.get(i).clientIntent != null) {
                        return valueAt.get(i).clientIntent;
                    }
                }
            }
            return null;
        }
        return null;
    }

    public void serviceTimeout(ProcessRecord processRecord) {
        long j;
        TimeoutRecord timeoutRecord;
        ServiceRecord serviceRecord;
        long j2;
        try {
            Trace.traceBegin(64L, "serviceTimeout()");
            synchronized (this.mAm) {
                ActivityManagerService.boostPriorityForLockedSection();
                if (!processRecord.isDebugging()) {
                    ProcessServiceRecord processServiceRecord = processRecord.mServices;
                    if (processServiceRecord.numberOfExecutingServices() != 0 && processRecord.getThread() != null && !processRecord.isKilled()) {
                        long uptimeMillis = SystemClock.uptimeMillis();
                        if (processServiceRecord.shouldExecServicesFg()) {
                            j = this.mAm.mConstants.SERVICE_TIMEOUT;
                        } else {
                            j = this.mAm.mConstants.SERVICE_BACKGROUND_TIMEOUT;
                        }
                        long j3 = uptimeMillis - j;
                        int numberOfExecutingServices = processServiceRecord.numberOfExecutingServices() - 1;
                        long j4 = 0;
                        while (true) {
                            timeoutRecord = null;
                            if (numberOfExecutingServices < 0) {
                                serviceRecord = null;
                                break;
                            }
                            serviceRecord = processServiceRecord.getExecutingServiceAt(numberOfExecutingServices);
                            long j5 = serviceRecord.executingStart;
                            if (j5 < j3) {
                                break;
                            }
                            if (j5 > j4) {
                                j4 = j5;
                            }
                            numberOfExecutingServices--;
                        }
                        if (serviceRecord != null && this.mAm.mProcessList.isInLruListLOSP(processRecord)) {
                            Slog.w("ActivityManager", "Timeout executing service: " + serviceRecord);
                            StringWriter stringWriter = new StringWriter();
                            FastPrintWriter fastPrintWriter = new FastPrintWriter(stringWriter, false, 1024);
                            fastPrintWriter.println(serviceRecord);
                            serviceRecord.dump((PrintWriter) fastPrintWriter, "    ");
                            fastPrintWriter.close();
                            this.mLastAnrDump = stringWriter.toString();
                            this.mAm.mHandler.removeCallbacks(this.mLastAnrDumpClearer);
                            this.mAm.mHandler.postDelayed(this.mLastAnrDumpClearer, 7200000L);
                            timeoutRecord = TimeoutRecord.forServiceExec("executing service " + serviceRecord.shortInstanceName);
                        } else {
                            Message obtainMessage = this.mAm.mHandler.obtainMessage(12);
                            obtainMessage.obj = processRecord;
                            ActivityManagerService.MainHandler mainHandler = this.mAm.mHandler;
                            if (processServiceRecord.shouldExecServicesFg()) {
                                j2 = this.mAm.mConstants.SERVICE_TIMEOUT;
                            } else {
                                j2 = this.mAm.mConstants.SERVICE_BACKGROUND_TIMEOUT;
                            }
                            mainHandler.sendMessageAtTime(obtainMessage, j4 + j2);
                        }
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        if (timeoutRecord != null) {
                            this.mAm.mAnrHelper.appNotResponding(processRecord, timeoutRecord);
                        }
                        return;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void serviceForegroundTimeout(ServiceRecord serviceRecord) {
        try {
            Trace.traceBegin(64L, "serviceForegroundTimeout()");
            TimeoutRecord forServiceStartWithEndTime = TimeoutRecord.forServiceStartWithEndTime("Context.startForegroundService() did not then call Service.startForeground(): " + serviceRecord, SystemClock.uptimeMillis());
            forServiceStartWithEndTime.mLatencyTracker.waitingOnAMSLockStarted();
            synchronized (this.mAm) {
                ActivityManagerService.boostPriorityForLockedSection();
                forServiceStartWithEndTime.mLatencyTracker.waitingOnAMSLockEnded();
                if (serviceRecord.fgRequired && serviceRecord.fgWaiting && !serviceRecord.destroying) {
                    ProcessRecord processRecord = serviceRecord.app;
                    if (processRecord == null || !processRecord.isDebugging()) {
                        serviceRecord.fgWaiting = false;
                        stopServiceLocked(serviceRecord, false);
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        if (processRecord != null) {
                            Message obtainMessage = this.mAm.mHandler.obtainMessage(67);
                            SomeArgs obtain = SomeArgs.obtain();
                            obtain.arg1 = processRecord;
                            obtain.arg2 = forServiceStartWithEndTime;
                            obtainMessage.obj = obtain;
                            ActivityManagerService activityManagerService = this.mAm;
                            activityManagerService.mHandler.sendMessageDelayed(obtainMessage, activityManagerService.mConstants.mServiceStartForegroundAnrDelayMs);
                        }
                        return;
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public void serviceForegroundTimeoutANR(ProcessRecord processRecord, TimeoutRecord timeoutRecord) {
        this.mAm.mAnrHelper.appNotResponding(processRecord, timeoutRecord);
    }

    public void updateServiceApplicationInfoLocked(ApplicationInfo applicationInfo) {
        ServiceMap serviceMap = this.mServiceMap.get(UserHandle.getUserId(applicationInfo.uid));
        if (serviceMap != null) {
            ArrayMap<ComponentName, ServiceRecord> arrayMap = serviceMap.mServicesByInstanceName;
            for (int size = arrayMap.size() - 1; size >= 0; size--) {
                ServiceRecord valueAt = arrayMap.valueAt(size);
                if (applicationInfo.packageName.equals(valueAt.appInfo.packageName)) {
                    valueAt.appInfo = applicationInfo;
                    valueAt.serviceInfo.applicationInfo = applicationInfo;
                }
            }
        }
    }

    public void serviceForegroundCrash(ProcessRecord processRecord, String str, ComponentName componentName) {
        ActivityManagerService activityManagerService = this.mAm;
        int i = processRecord.uid;
        int pid = processRecord.getPid();
        String str2 = processRecord.info.packageName;
        int i2 = processRecord.userId;
        activityManagerService.crashApplicationWithTypeWithExtras(i, pid, str2, i2, "Context.startForegroundService() did not then call Service.startForeground(): " + str, false, 1, RemoteServiceException.ForegroundServiceDidNotStartInTimeException.createExtrasForService(componentName));
    }

    public void scheduleServiceTimeoutLocked(ProcessRecord processRecord) {
        if (processRecord.mServices.numberOfExecutingServices() == 0 || processRecord.getThread() == null) {
            return;
        }
        Message obtainMessage = this.mAm.mHandler.obtainMessage(12);
        obtainMessage.obj = processRecord;
        this.mAm.mHandler.sendMessageDelayed(obtainMessage, processRecord.mServices.shouldExecServicesFg() ? this.mAm.mConstants.SERVICE_TIMEOUT : this.mAm.mConstants.SERVICE_BACKGROUND_TIMEOUT);
    }

    public void scheduleServiceForegroundTransitionTimeoutLocked(ServiceRecord serviceRecord) {
        if (serviceRecord.app.mServices.numberOfExecutingServices() == 0 || serviceRecord.app.getThread() == null) {
            return;
        }
        Message obtainMessage = this.mAm.mHandler.obtainMessage(66);
        obtainMessage.obj = serviceRecord;
        serviceRecord.fgWaiting = true;
        ActivityManagerService activityManagerService = this.mAm;
        activityManagerService.mHandler.sendMessageDelayed(obtainMessage, activityManagerService.mConstants.mServiceStartForegroundTimeoutMs);
    }

    /* renamed from: com.android.server.am.ActiveServices$ServiceDumper */
    /* loaded from: classes.dex */
    public final class ServiceDumper {
        public final String[] args;
        public final boolean dumpAll;
        public final String dumpPackage;

        /* renamed from: fd */
        public final FileDescriptor f1116fd;
        public final ActivityManagerService.ItemMatcher matcher;

        /* renamed from: pw */
        public final PrintWriter f1117pw;
        public final ArrayList<ServiceRecord> services = new ArrayList<>();
        public final long nowReal = SystemClock.elapsedRealtime();
        public boolean needSep = false;
        public boolean printedAnything = false;
        public boolean printed = false;

        public ServiceDumper(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str) {
            this.f1116fd = fileDescriptor;
            this.f1117pw = printWriter;
            this.args = strArr;
            this.dumpAll = z;
            this.dumpPackage = str;
            ActivityManagerService.ItemMatcher itemMatcher = new ActivityManagerService.ItemMatcher();
            this.matcher = itemMatcher;
            itemMatcher.build(strArr, i);
            for (int i2 : ActiveServices.this.mAm.mUserController.getUsers()) {
                ServiceMap serviceMapLocked = ActiveServices.this.getServiceMapLocked(i2);
                if (serviceMapLocked.mServicesByInstanceName.size() > 0) {
                    for (int i3 = 0; i3 < serviceMapLocked.mServicesByInstanceName.size(); i3++) {
                        ServiceRecord valueAt = serviceMapLocked.mServicesByInstanceName.valueAt(i3);
                        if (this.matcher.match(valueAt, valueAt.name) && (str == null || str.equals(valueAt.appInfo.packageName))) {
                            this.services.add(valueAt);
                        }
                    }
                }
            }
        }

        public final void dumpHeaderLocked() {
            this.f1117pw.println("ACTIVITY MANAGER SERVICES (dumpsys activity services)");
            if (ActiveServices.this.mLastAnrDump != null) {
                this.f1117pw.println("  Last ANR service:");
                this.f1117pw.print(ActiveServices.this.mLastAnrDump);
                this.f1117pw.println();
            }
        }

        public void dumpLocked() {
            int[] users;
            dumpHeaderLocked();
            try {
                for (int i : ActiveServices.this.mAm.mUserController.getUsers()) {
                    int i2 = 0;
                    while (i2 < this.services.size() && this.services.get(i2).userId != i) {
                        i2++;
                    }
                    this.printed = false;
                    if (i2 < this.services.size()) {
                        this.needSep = false;
                        while (i2 < this.services.size()) {
                            ServiceRecord serviceRecord = this.services.get(i2);
                            i2++;
                            if (serviceRecord.userId != i) {
                                break;
                            }
                            dumpServiceLocalLocked(serviceRecord);
                        }
                        this.needSep |= this.printed;
                    }
                    dumpUserRemainsLocked(i);
                }
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception in dumpServicesLocked", e);
            }
            dumpRemainsLocked();
        }

        public void dumpWithClient() {
            int[] users;
            synchronized (ActiveServices.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    dumpHeaderLocked();
                } finally {
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            try {
                for (int i : ActiveServices.this.mAm.mUserController.getUsers()) {
                    int i2 = 0;
                    while (i2 < this.services.size() && this.services.get(i2).userId != i) {
                        i2++;
                    }
                    this.printed = false;
                    if (i2 < this.services.size()) {
                        this.needSep = false;
                        while (i2 < this.services.size()) {
                            ServiceRecord serviceRecord = this.services.get(i2);
                            i2++;
                            if (serviceRecord.userId != i) {
                                break;
                            }
                            synchronized (ActiveServices.this.mAm) {
                                ActivityManagerService.boostPriorityForLockedSection();
                                dumpServiceLocalLocked(serviceRecord);
                            }
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            dumpServiceClient(serviceRecord);
                        }
                        this.needSep |= this.printed;
                    }
                    synchronized (ActiveServices.this.mAm) {
                        ActivityManagerService.boostPriorityForLockedSection();
                        dumpUserRemainsLocked(i);
                    }
                }
            } catch (Exception e) {
                Slog.w("ActivityManager", "Exception in dumpServicesLocked", e);
            }
            synchronized (ActiveServices.this.mAm) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    dumpRemainsLocked();
                } finally {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
        }

        public final void dumpUserHeaderLocked(int i) {
            if (!this.printed) {
                if (this.printedAnything) {
                    this.f1117pw.println();
                }
                PrintWriter printWriter = this.f1117pw;
                printWriter.println("  User " + i + " active services:");
                this.printed = true;
            }
            this.printedAnything = true;
            if (this.needSep) {
                this.f1117pw.println();
            }
        }

        public final void dumpServiceLocalLocked(ServiceRecord serviceRecord) {
            dumpUserHeaderLocked(serviceRecord.userId);
            this.f1117pw.print("  * ");
            this.f1117pw.println(serviceRecord);
            if (this.dumpAll) {
                serviceRecord.dump(this.f1117pw, "    ");
                this.needSep = true;
                return;
            }
            this.f1117pw.print("    app=");
            this.f1117pw.println(serviceRecord.app);
            this.f1117pw.print("    created=");
            TimeUtils.formatDuration(serviceRecord.createRealTime, this.nowReal, this.f1117pw);
            this.f1117pw.print(" started=");
            this.f1117pw.print(serviceRecord.startRequested);
            this.f1117pw.print(" connections=");
            ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = serviceRecord.getConnections();
            this.f1117pw.println(connections.size());
            if (connections.size() > 0) {
                this.f1117pw.println("    Connections:");
                for (int i = 0; i < connections.size(); i++) {
                    ArrayList<ConnectionRecord> valueAt = connections.valueAt(i);
                    for (int i2 = 0; i2 < valueAt.size(); i2++) {
                        ConnectionRecord connectionRecord = valueAt.get(i2);
                        this.f1117pw.print("      ");
                        this.f1117pw.print(connectionRecord.binding.intent.intent.getIntent().toShortString(false, false, false, false));
                        this.f1117pw.print(" -> ");
                        ProcessRecord processRecord = connectionRecord.binding.client;
                        this.f1117pw.println(processRecord != null ? processRecord.toShortString() : "null");
                    }
                }
            }
        }

        public final void dumpServiceClient(ServiceRecord serviceRecord) {
            IApplicationThread thread;
            ProcessRecord processRecord = serviceRecord.app;
            if (processRecord == null || (thread = processRecord.getThread()) == null) {
                return;
            }
            this.f1117pw.println("    Client:");
            this.f1117pw.flush();
            try {
                TransferPipe transferPipe = new TransferPipe();
                try {
                    thread.dumpService(transferPipe.getWriteFd(), serviceRecord, this.args);
                    transferPipe.setBufferPrefix("      ");
                    transferPipe.go(this.f1116fd, 2000L);
                    transferPipe.kill();
                } catch (Throwable th) {
                    transferPipe.kill();
                    throw th;
                }
            } catch (RemoteException unused) {
                this.f1117pw.println("      Got a RemoteException while dumping the service");
            } catch (IOException e) {
                PrintWriter printWriter = this.f1117pw;
                printWriter.println("      Failure while dumping the service: " + e);
            }
            this.needSep = true;
        }

        public final void dumpUserRemainsLocked(int i) {
            String str;
            String str2;
            ServiceMap serviceMapLocked = ActiveServices.this.getServiceMapLocked(i);
            this.printed = false;
            int size = serviceMapLocked.mDelayedStartList.size();
            for (int i2 = 0; i2 < size; i2++) {
                ServiceRecord serviceRecord = serviceMapLocked.mDelayedStartList.get(i2);
                if (this.matcher.match(serviceRecord, serviceRecord.name) && ((str2 = this.dumpPackage) == null || str2.equals(serviceRecord.appInfo.packageName))) {
                    if (!this.printed) {
                        if (this.printedAnything) {
                            this.f1117pw.println();
                        }
                        this.f1117pw.println("  User " + i + " delayed start services:");
                        this.printed = true;
                    }
                    this.printedAnything = true;
                    this.f1117pw.print("  * Delayed start ");
                    this.f1117pw.println(serviceRecord);
                }
            }
            this.printed = false;
            int size2 = serviceMapLocked.mStartingBackground.size();
            for (int i3 = 0; i3 < size2; i3++) {
                ServiceRecord serviceRecord2 = serviceMapLocked.mStartingBackground.get(i3);
                if (this.matcher.match(serviceRecord2, serviceRecord2.name) && ((str = this.dumpPackage) == null || str.equals(serviceRecord2.appInfo.packageName))) {
                    if (!this.printed) {
                        if (this.printedAnything) {
                            this.f1117pw.println();
                        }
                        this.f1117pw.println("  User " + i + " starting in background:");
                        this.printed = true;
                    }
                    this.printedAnything = true;
                    this.f1117pw.print("  * Starting bg ");
                    this.f1117pw.println(serviceRecord2);
                }
            }
        }

        public final void dumpRemainsLocked() {
            String str;
            ProcessRecord processRecord;
            String str2;
            String str3;
            String str4;
            boolean z = false;
            if (ActiveServices.this.mPendingServices.size() > 0) {
                this.printed = false;
                for (int i = 0; i < ActiveServices.this.mPendingServices.size(); i++) {
                    ServiceRecord serviceRecord = ActiveServices.this.mPendingServices.get(i);
                    if (this.matcher.match(serviceRecord, serviceRecord.name) && ((str4 = this.dumpPackage) == null || str4.equals(serviceRecord.appInfo.packageName))) {
                        this.printedAnything = true;
                        if (!this.printed) {
                            if (this.needSep) {
                                this.f1117pw.println();
                            }
                            this.needSep = true;
                            this.f1117pw.println("  Pending services:");
                            this.printed = true;
                        }
                        this.f1117pw.print("  * Pending ");
                        this.f1117pw.println(serviceRecord);
                        serviceRecord.dump(this.f1117pw, "    ");
                    }
                }
                this.needSep = true;
            }
            if (ActiveServices.this.mRestartingServices.size() > 0) {
                this.printed = false;
                for (int i2 = 0; i2 < ActiveServices.this.mRestartingServices.size(); i2++) {
                    ServiceRecord serviceRecord2 = ActiveServices.this.mRestartingServices.get(i2);
                    if (this.matcher.match(serviceRecord2, serviceRecord2.name) && ((str3 = this.dumpPackage) == null || str3.equals(serviceRecord2.appInfo.packageName))) {
                        this.printedAnything = true;
                        if (!this.printed) {
                            if (this.needSep) {
                                this.f1117pw.println();
                            }
                            this.needSep = true;
                            this.f1117pw.println("  Restarting services:");
                            this.printed = true;
                        }
                        this.f1117pw.print("  * Restarting ");
                        this.f1117pw.println(serviceRecord2);
                        serviceRecord2.dump(this.f1117pw, "    ");
                    }
                }
                this.needSep = true;
            }
            if (ActiveServices.this.mDestroyingServices.size() > 0) {
                this.printed = false;
                for (int i3 = 0; i3 < ActiveServices.this.mDestroyingServices.size(); i3++) {
                    ServiceRecord serviceRecord3 = ActiveServices.this.mDestroyingServices.get(i3);
                    if (this.matcher.match(serviceRecord3, serviceRecord3.name) && ((str2 = this.dumpPackage) == null || str2.equals(serviceRecord3.appInfo.packageName))) {
                        this.printedAnything = true;
                        if (!this.printed) {
                            if (this.needSep) {
                                this.f1117pw.println();
                            }
                            this.needSep = true;
                            this.f1117pw.println("  Destroying services:");
                            this.printed = true;
                        }
                        this.f1117pw.print("  * Destroy ");
                        this.f1117pw.println(serviceRecord3);
                        serviceRecord3.dump(this.f1117pw, "    ");
                    }
                }
                this.needSep = true;
            }
            if (this.dumpAll) {
                this.printed = false;
                for (int i4 = 0; i4 < ActiveServices.this.mServiceConnections.size(); i4++) {
                    ArrayList<ConnectionRecord> valueAt = ActiveServices.this.mServiceConnections.valueAt(i4);
                    for (int i5 = 0; i5 < valueAt.size(); i5++) {
                        ConnectionRecord connectionRecord = valueAt.get(i5);
                        ActivityManagerService.ItemMatcher itemMatcher = this.matcher;
                        ServiceRecord serviceRecord4 = connectionRecord.binding.service;
                        if (itemMatcher.match(serviceRecord4, serviceRecord4.name) && ((str = this.dumpPackage) == null || ((processRecord = connectionRecord.binding.client) != null && str.equals(processRecord.info.packageName)))) {
                            this.printedAnything = true;
                            if (!this.printed) {
                                if (this.needSep) {
                                    this.f1117pw.println();
                                }
                                this.needSep = true;
                                this.f1117pw.println("  Connection bindings to services:");
                                this.printed = true;
                            }
                            this.f1117pw.print("  * ");
                            this.f1117pw.println(connectionRecord);
                            connectionRecord.dump(this.f1117pw, "    ");
                        }
                    }
                }
            }
            if (this.matcher.all) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                int[] users = ActiveServices.this.mAm.mUserController.getUsers();
                int length = users.length;
                int i6 = 0;
                while (i6 < length) {
                    int i7 = users[i6];
                    ServiceMap serviceMap = ActiveServices.this.mServiceMap.get(i7);
                    if (serviceMap != null) {
                        boolean z2 = z;
                        for (int size = serviceMap.mActiveForegroundApps.size() - 1; size >= 0; size--) {
                            ActiveForegroundApp valueAt2 = serviceMap.mActiveForegroundApps.valueAt(size);
                            String str5 = this.dumpPackage;
                            if (str5 == null || str5.equals(valueAt2.mPackageName)) {
                                if (!z2) {
                                    this.printedAnything = true;
                                    if (this.needSep) {
                                        this.f1117pw.println();
                                    }
                                    this.needSep = true;
                                    this.f1117pw.print("Active foreground apps - user ");
                                    this.f1117pw.print(i7);
                                    this.f1117pw.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                                    z2 = true;
                                }
                                this.f1117pw.print("  #");
                                this.f1117pw.print(size);
                                this.f1117pw.print(": ");
                                this.f1117pw.println(valueAt2.mPackageName);
                                if (valueAt2.mLabel != null) {
                                    this.f1117pw.print("    mLabel=");
                                    this.f1117pw.println(valueAt2.mLabel);
                                }
                                this.f1117pw.print("    mNumActive=");
                                this.f1117pw.print(valueAt2.mNumActive);
                                this.f1117pw.print(" mAppOnTop=");
                                this.f1117pw.print(valueAt2.mAppOnTop);
                                this.f1117pw.print(" mShownWhileTop=");
                                this.f1117pw.print(valueAt2.mShownWhileTop);
                                this.f1117pw.print(" mShownWhileScreenOn=");
                                this.f1117pw.println(valueAt2.mShownWhileScreenOn);
                                this.f1117pw.print("    mStartTime=");
                                boolean z3 = z2;
                                TimeUtils.formatDuration(valueAt2.mStartTime - elapsedRealtime, this.f1117pw);
                                this.f1117pw.print(" mStartVisibleTime=");
                                TimeUtils.formatDuration(valueAt2.mStartVisibleTime - elapsedRealtime, this.f1117pw);
                                this.f1117pw.println();
                                if (valueAt2.mEndTime != 0) {
                                    this.f1117pw.print("    mEndTime=");
                                    TimeUtils.formatDuration(valueAt2.mEndTime - elapsedRealtime, this.f1117pw);
                                    this.f1117pw.println();
                                }
                                z2 = z3;
                            }
                        }
                        if (serviceMap.hasMessagesOrCallbacks()) {
                            if (this.needSep) {
                                this.f1117pw.println();
                            }
                            this.printedAnything = true;
                            this.needSep = true;
                            this.f1117pw.print("  Handler - user ");
                            this.f1117pw.print(i7);
                            this.f1117pw.println(XmlUtils.STRING_ARRAY_SEPARATOR);
                            serviceMap.dumpMine(new PrintWriterPrinter(this.f1117pw), "    ");
                        }
                    }
                    i6++;
                    z = false;
                }
            }
            if (this.printedAnything) {
                return;
            }
            this.f1117pw.println("  (nothing)");
        }
    }

    public ServiceDumper newServiceDumperLocked(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, int i, boolean z, String str) {
        return new ServiceDumper(fileDescriptor, printWriter, strArr, i, z, str);
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        int[] users;
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                long start = protoOutputStream.start(j);
                for (int i : this.mAm.mUserController.getUsers()) {
                    ServiceMap serviceMap = this.mServiceMap.get(i);
                    if (serviceMap != null) {
                        long start2 = protoOutputStream.start(2246267895809L);
                        protoOutputStream.write(1120986464257L, i);
                        ArrayMap<ComponentName, ServiceRecord> arrayMap = serviceMap.mServicesByInstanceName;
                        for (int i2 = 0; i2 < arrayMap.size(); i2++) {
                            arrayMap.valueAt(i2).dumpDebug(protoOutputStream, 2246267895810L);
                        }
                        protoOutputStream.end(start2);
                    }
                }
                protoOutputStream.end(start);
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public boolean dumpService(FileDescriptor fileDescriptor, PrintWriter printWriter, String str, int[] iArr, String[] strArr, int i, boolean z) {
        boolean z2;
        ArrayList arrayList = new ArrayList();
        Predicate filterRecord = DumpUtils.filterRecord(str);
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                z2 = false;
                for (int i2 : iArr == null ? this.mAm.mUserController.getUsers() : iArr) {
                    ServiceMap serviceMap = this.mServiceMap.get(i2);
                    if (serviceMap != null) {
                        ArrayMap<ComponentName, ServiceRecord> arrayMap = serviceMap.mServicesByInstanceName;
                        for (int i3 = 0; i3 < arrayMap.size(); i3++) {
                            ServiceRecord valueAt = arrayMap.valueAt(i3);
                            if (filterRecord.test(valueAt)) {
                                arrayList.add(valueAt);
                            }
                        }
                    }
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        if (arrayList.size() <= 0) {
            return false;
        }
        arrayList.sort(Comparator.comparing(new Function() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ServiceRecord) obj).getComponentName();
            }
        }));
        int i4 = 0;
        while (i4 < arrayList.size()) {
            if (z2) {
                printWriter.println();
            }
            dumpService("", fileDescriptor, printWriter, (ServiceRecord) arrayList.get(i4), strArr, z);
            i4++;
            z2 = true;
        }
        return true;
    }

    public final void dumpService(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, ServiceRecord serviceRecord, String[] strArr, boolean z) {
        IApplicationThread thread;
        String str2 = str + "  ";
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                printWriter.print(str);
                printWriter.print("SERVICE ");
                printWriter.print(serviceRecord.shortInstanceName);
                printWriter.print(" ");
                printWriter.print(Integer.toHexString(System.identityHashCode(serviceRecord)));
                printWriter.print(" pid=");
                ProcessRecord processRecord = serviceRecord.app;
                if (processRecord != null) {
                    printWriter.print(processRecord.getPid());
                    printWriter.print(" user=");
                    printWriter.println(serviceRecord.userId);
                } else {
                    printWriter.println("(not running)");
                }
                if (z) {
                    serviceRecord.dump(printWriter, str2);
                }
            } catch (Throwable th) {
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        ProcessRecord processRecord2 = serviceRecord.app;
        if (processRecord2 == null || (thread = processRecord2.getThread()) == null) {
            return;
        }
        printWriter.print(str);
        printWriter.println("  Client:");
        printWriter.flush();
        try {
            TransferPipe transferPipe = new TransferPipe();
            try {
                thread.dumpService(transferPipe.getWriteFd(), serviceRecord, strArr);
                transferPipe.setBufferPrefix(str + "    ");
                transferPipe.go(fileDescriptor);
                transferPipe.kill();
            } catch (Throwable th2) {
                transferPipe.kill();
                throw th2;
            }
        } catch (RemoteException unused) {
            printWriter.println(str + "    Got a RemoteException while dumping the service");
        } catch (IOException e) {
            printWriter.println(str + "    Failure while dumping the service: " + e);
        }
    }

    public final void setFgsRestrictionLocked(String str, int i, int i2, Intent intent, ServiceRecord serviceRecord, int i3, BackgroundStartPrivileges backgroundStartPrivileges, boolean z) {
        serviceRecord.mLastSetFgsRestrictionTime = SystemClock.elapsedRealtime();
        if (!this.mAm.mConstants.mFlagBackgroundFgsStartRestrictionEnabled) {
            serviceRecord.mAllowWhileInUsePermissionInFgs = true;
        }
        if (!serviceRecord.mAllowWhileInUsePermissionInFgs || serviceRecord.mAllowStartForeground == -1) {
            int shouldAllowFgsWhileInUsePermissionLocked = shouldAllowFgsWhileInUsePermissionLocked(str, i, i2, serviceRecord, backgroundStartPrivileges, z);
            if (!serviceRecord.mAllowWhileInUsePermissionInFgs) {
                serviceRecord.mAllowWhileInUsePermissionInFgs = shouldAllowFgsWhileInUsePermissionLocked != -1;
            }
            if (serviceRecord.mAllowStartForeground == -1) {
                serviceRecord.mAllowStartForeground = shouldAllowFgsStartForegroundWithBindingCheckLocked(shouldAllowFgsWhileInUsePermissionLocked, str, i, i2, intent, serviceRecord, backgroundStartPrivileges, z);
            }
        }
    }

    public void resetFgsRestrictionLocked(ServiceRecord serviceRecord) {
        serviceRecord.mAllowWhileInUsePermissionInFgs = false;
        serviceRecord.mAllowStartForeground = -1;
        serviceRecord.mInfoAllowStartForeground = null;
        serviceRecord.mInfoTempFgsAllowListReason = null;
        serviceRecord.mLoggedInfoAllowStartForeground = false;
        serviceRecord.mLastSetFgsRestrictionTime = 0L;
    }

    public boolean canStartForegroundServiceLocked(int i, int i2, String str) {
        if (this.mAm.mConstants.mFlagBackgroundFgsStartRestrictionEnabled) {
            int shouldAllowFgsStartForegroundNoBindingCheckLocked = shouldAllowFgsStartForegroundNoBindingCheckLocked(shouldAllowFgsWhileInUsePermissionLocked(str, i, i2, null, BackgroundStartPrivileges.NONE, false), i, i2, str, null, BackgroundStartPrivileges.NONE);
            if (shouldAllowFgsStartForegroundNoBindingCheckLocked == -1 && canBindingClientStartFgsLocked(i2) != null) {
                shouldAllowFgsStartForegroundNoBindingCheckLocked = 54;
            }
            return shouldAllowFgsStartForegroundNoBindingCheckLocked != -1;
        }
        return true;
    }

    public final int shouldAllowFgsWhileInUsePermissionLocked(String str, int i, final int i2, ServiceRecord serviceRecord, BackgroundStartPrivileges backgroundStartPrivileges, boolean z) {
        ProcessRecord processRecord;
        ActiveInstrumentation activeInstrumentation;
        Integer num;
        int uidStateLocked = this.mAm.getUidStateLocked(i2);
        int reasonCodeFromProcState = uidStateLocked <= 2 ? PowerExemptionManager.getReasonCodeFromProcState(uidStateLocked) : -1;
        if (reasonCodeFromProcState == -1 && this.mAm.mAtmInternal.isUidForeground(i2)) {
            reasonCodeFromProcState = 50;
        }
        if (reasonCodeFromProcState == -1 && backgroundStartPrivileges.allowsBackgroundActivityStarts()) {
            reasonCodeFromProcState = 53;
        }
        if (reasonCodeFromProcState == -1) {
            int appId = UserHandle.getAppId(i2);
            if (appId == 0 || appId == 1000 || appId == 1027 || appId == 2000) {
                reasonCodeFromProcState = 51;
            }
        }
        if (reasonCodeFromProcState == -1 && (num = (Integer) this.mAm.mProcessList.searchEachLruProcessesLOSP(false, new Function() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Integer lambda$shouldAllowFgsWhileInUsePermissionLocked$4;
                lambda$shouldAllowFgsWhileInUsePermissionLocked$4 = ActiveServices.lambda$shouldAllowFgsWhileInUsePermissionLocked$4(i2, (ProcessRecord) obj);
                return lambda$shouldAllowFgsWhileInUsePermissionLocked$4;
            }
        })) != null) {
            reasonCodeFromProcState = num.intValue();
        }
        if (reasonCodeFromProcState == -1 && this.mAm.mInternal.isTempAllowlistedForFgsWhileInUse(i2)) {
            return 70;
        }
        if (reasonCodeFromProcState == -1 && serviceRecord != null && (processRecord = serviceRecord.app) != null && (activeInstrumentation = processRecord.getActiveInstrumentation()) != null && activeInstrumentation.mHasBackgroundActivityStartsPermission) {
            reasonCodeFromProcState = 60;
        }
        if (reasonCodeFromProcState == -1 && this.mAm.checkPermission("android.permission.START_ACTIVITIES_FROM_BACKGROUND", i, i2) == 0) {
            reasonCodeFromProcState = 58;
        }
        if (reasonCodeFromProcState == -1) {
            if (verifyPackage(str, i2)) {
                if (this.mAllowListWhileInUsePermissionInFgs.contains(str)) {
                    reasonCodeFromProcState = 65;
                }
            } else {
                EventLog.writeEvent(1397638484, "215003903", Integer.valueOf(i2), "callingPackage:" + str + " does not belong to callingUid:" + i2);
            }
        }
        if (reasonCodeFromProcState == -1 && this.mAm.mInternal.isDeviceOwner(i2)) {
            return 55;
        }
        return reasonCodeFromProcState;
    }

    public static /* synthetic */ Integer lambda$shouldAllowFgsWhileInUsePermissionLocked$4(int i, ProcessRecord processRecord) {
        return (processRecord.uid == i && processRecord.getWindowProcessController().areBackgroundFgsStartsAllowed()) ? 52 : null;
    }

    public final String canBindingClientStartFgsLocked(final int i) {
        final ArraySet arraySet = new ArraySet();
        Pair pair = (Pair) this.mAm.mProcessList.searchEachLruProcessesLOSP(false, new Function() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Pair lambda$canBindingClientStartFgsLocked$5;
                lambda$canBindingClientStartFgsLocked$5 = ActiveServices.this.lambda$canBindingClientStartFgsLocked$5(i, arraySet, (ProcessRecord) obj);
                return lambda$canBindingClientStartFgsLocked$5;
            }
        });
        if (pair != null) {
            return (String) pair.second;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Pair lambda$canBindingClientStartFgsLocked$5(int i, ArraySet arraySet, ProcessRecord processRecord) {
        if (processRecord.uid == i) {
            ProcessServiceRecord processServiceRecord = processRecord.mServices;
            int size = processServiceRecord.mServices.size();
            for (int i2 = 0; i2 < size; i2++) {
                ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = processServiceRecord.mServices.valueAt(i2).getConnections();
                int size2 = connections.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    ArrayList<ConnectionRecord> valueAt = connections.valueAt(i3);
                    for (int i4 = 0; i4 < valueAt.size(); i4++) {
                        ConnectionRecord connectionRecord = valueAt.get(i4);
                        ProcessRecord processRecord2 = connectionRecord.binding.client;
                        if (!processRecord2.isPersistent()) {
                            int i5 = processRecord2.mPid;
                            int i6 = processRecord2.uid;
                            if (i6 != i && !arraySet.contains(Integer.valueOf(i6))) {
                                String str = connectionRecord.clientPackageName;
                                int shouldAllowFgsStartForegroundNoBindingCheckLocked = shouldAllowFgsStartForegroundNoBindingCheckLocked(shouldAllowFgsWhileInUsePermissionLocked(str, i5, i6, null, BackgroundStartPrivileges.NONE, false), i5, i6, str, null, BackgroundStartPrivileges.NONE);
                                if (shouldAllowFgsStartForegroundNoBindingCheckLocked != -1) {
                                    return new Pair(Integer.valueOf(shouldAllowFgsStartForegroundNoBindingCheckLocked), str);
                                }
                                arraySet.add(Integer.valueOf(i6));
                            }
                        }
                    }
                }
            }
            return null;
        }
        return null;
    }

    public final int shouldAllowFgsStartForegroundWithBindingCheckLocked(int i, String str, int i2, int i3, Intent intent, ServiceRecord serviceRecord, BackgroundStartPrivileges backgroundStartPrivileges, boolean z) {
        String str2;
        ActivityManagerService.FgsTempAllowListItem isAllowlistedForFgsStartLOSP = this.mAm.isAllowlistedForFgsStartLOSP(i3);
        serviceRecord.mInfoTempFgsAllowListReason = isAllowlistedForFgsStartLOSP;
        int shouldAllowFgsStartForegroundNoBindingCheckLocked = shouldAllowFgsStartForegroundNoBindingCheckLocked(i, i2, i3, str, serviceRecord, backgroundStartPrivileges);
        String str3 = null;
        int i4 = -1;
        if (shouldAllowFgsStartForegroundNoBindingCheckLocked == -1) {
            str2 = canBindingClientStartFgsLocked(i3);
            if (str2 != null) {
                shouldAllowFgsStartForegroundNoBindingCheckLocked = 54;
            }
        } else {
            str2 = null;
        }
        int uidStateLocked = this.mAm.getUidStateLocked(i3);
        try {
            i4 = this.mAm.mContext.getPackageManager().getTargetSdkVersion(str);
        } catch (PackageManager.NameNotFoundException unused) {
        }
        boolean z2 = (this.mAm.getUidProcessCapabilityLocked(i3) & 16) != 0;
        StringBuilder sb = new StringBuilder();
        sb.append("[callingPackage: ");
        sb.append(str);
        sb.append("; callingUid: ");
        sb.append(i3);
        sb.append("; uidState: ");
        sb.append(ProcessList.makeProcStateString(uidStateLocked));
        sb.append("; uidBFSL: ");
        sb.append(z2 ? "[BFSL]" : "n/a");
        sb.append("; intent: ");
        sb.append(intent);
        sb.append("; code:");
        sb.append(PowerExemptionManager.reasonCodeToString(shouldAllowFgsStartForegroundNoBindingCheckLocked));
        sb.append("; tempAllowListReason:<");
        if (isAllowlistedForFgsStartLOSP != null) {
            str3 = isAllowlistedForFgsStartLOSP.mReason + ",reasonCode:" + PowerExemptionManager.reasonCodeToString(isAllowlistedForFgsStartLOSP.mReasonCode) + ",duration:" + isAllowlistedForFgsStartLOSP.mDuration + ",callingUid:" + isAllowlistedForFgsStartLOSP.mCallingUid;
        }
        sb.append(str3);
        sb.append(">; targetSdkVersion:");
        sb.append(serviceRecord.appInfo.targetSdkVersion);
        sb.append("; callerTargetSdkVersion:");
        sb.append(i4);
        sb.append("; startForegroundCount:");
        sb.append(serviceRecord.mStartForegroundCount);
        sb.append("; bindFromPackage:");
        sb.append(str2);
        sb.append(": isBindService:");
        sb.append(z);
        sb.append("]");
        String sb2 = sb.toString();
        if (!sb2.equals(serviceRecord.mInfoAllowStartForeground)) {
            serviceRecord.mLoggedInfoAllowStartForeground = false;
            serviceRecord.mInfoAllowStartForeground = sb2;
        }
        return shouldAllowFgsStartForegroundNoBindingCheckLocked;
    }

    public final int shouldAllowFgsStartForegroundNoBindingCheckLocked(int i, int i2, final int i3, String str, ServiceRecord serviceRecord, BackgroundStartPrivileges backgroundStartPrivileges) {
        String stringForUser;
        ComponentName unflattenFromString;
        ActivityManagerService.FgsTempAllowListItem isAllowlistedForFgsStartLOSP;
        int uidStateLocked;
        if (i == -1 && (uidStateLocked = this.mAm.getUidStateLocked(i3)) <= 2) {
            i = PowerExemptionManager.getReasonCodeFromProcState(uidStateLocked);
        }
        if (i == -1) {
            final boolean z = (this.mAm.getUidProcessCapabilityLocked(i3) & 16) != 0;
            Integer num = (Integer) this.mAm.mProcessList.searchEachLruProcessesLOSP(false, new Function() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda4
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Integer lambda$shouldAllowFgsStartForegroundNoBindingCheckLocked$6;
                    lambda$shouldAllowFgsStartForegroundNoBindingCheckLocked$6 = ActiveServices.this.lambda$shouldAllowFgsStartForegroundNoBindingCheckLocked$6(i3, z, (ProcessRecord) obj);
                    return lambda$shouldAllowFgsStartForegroundNoBindingCheckLocked$6;
                }
            });
            if (num != null) {
                i = num.intValue();
            }
        }
        if (i == -1 && this.mAm.checkPermission("android.permission.START_FOREGROUND_SERVICES_FROM_BACKGROUND", i2, i3) == 0) {
            i = 59;
        }
        if (i == -1 && backgroundStartPrivileges.allowsBackgroundFgsStarts()) {
            i = 53;
        }
        if (i == -1 && this.mAm.mAtmInternal.hasSystemAlertWindowPermission(i3, i2, str)) {
            i = 62;
        }
        if (i == -1 && this.mAm.mInternal.isAssociatedCompanionApp(UserHandle.getUserId(i3), i3) && (isPermissionGranted("android.permission.REQUEST_COMPANION_START_FOREGROUND_SERVICES_FROM_BACKGROUND", i2, i3) || isPermissionGranted("android.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND", i2, i3))) {
            i = 57;
        }
        if (i == -1 && (isAllowlistedForFgsStartLOSP = this.mAm.isAllowlistedForFgsStartLOSP(i3)) != null) {
            i = isAllowlistedForFgsStartLOSP == ActivityManagerService.FAKE_TEMP_ALLOW_LIST_ITEM ? 300 : isAllowlistedForFgsStartLOSP.mReasonCode;
        }
        if (i == -1 && UserManager.isDeviceInDemoMode(this.mAm.mContext)) {
            i = 63;
        }
        if (i == -1 && this.mAm.mInternal.isProfileOwner(i3)) {
            i = 56;
        }
        if (i == -1) {
            AppOpsManager appOpsManager = this.mAm.getAppOpsManager();
            if (this.mAm.mConstants.mFlagSystemExemptPowerRestrictionsEnabled && appOpsManager.checkOpNoThrow(128, i3, str) == 0) {
                i = FrameworkStatsLog.TIF_TUNE_CHANGED;
            }
        }
        if (i == -1) {
            AppOpsManager appOpsManager2 = this.mAm.getAppOpsManager();
            if (appOpsManager2.checkOpNoThrow(47, i3, str) == 0) {
                i = 68;
            } else if (appOpsManager2.checkOpNoThrow(94, i3, str) == 0) {
                i = 69;
            }
        }
        if (i == -1 && (stringForUser = Settings.Secure.getStringForUser(this.mAm.mContext.getContentResolver(), "default_input_method", UserHandle.getUserId(i3))) != null && (unflattenFromString = ComponentName.unflattenFromString(stringForUser)) != null && unflattenFromString.getPackageName().equals(str)) {
            i = 71;
        }
        if (i == -1 && this.mAm.mConstants.mFgsAllowOptOut && serviceRecord != null && serviceRecord.appInfo.hasRequestForegroundServiceExemption()) {
            return 1000;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Integer lambda$shouldAllowFgsStartForegroundNoBindingCheckLocked$6(int i, boolean z, ProcessRecord processRecord) {
        if (processRecord.uid == i) {
            int curProcState = processRecord.mState.getCurProcState();
            if (curProcState <= 3 || (z && curProcState <= 5)) {
                return Integer.valueOf(PowerExemptionManager.getReasonCodeFromProcState(curProcState));
            }
            ActiveInstrumentation activeInstrumentation = processRecord.getActiveInstrumentation();
            if (activeInstrumentation != null && activeInstrumentation.mHasBackgroundForegroundServiceStartsPermission) {
                return 61;
            }
            long lastInvisibleTime = processRecord.mState.getLastInvisibleTime();
            return (lastInvisibleTime <= 0 || lastInvisibleTime >= Long.MAX_VALUE || SystemClock.elapsedRealtime() - lastInvisibleTime >= this.mAm.mConstants.mFgToBgFgsGraceDuration) ? null : 67;
        }
        return null;
    }

    public final boolean isPermissionGranted(String str, int i, int i2) {
        return this.mAm.checkPermission(str, i, i2) == 0;
    }

    public final void showFgsBgRestrictedNotificationLocked(ServiceRecord serviceRecord) {
        if (this.mAm.mConstants.mFgsStartRestrictionNotificationEnabled) {
            Context context = this.mAm.mContext;
            long currentTimeMillis = System.currentTimeMillis();
            ((NotificationManager) context.getSystemService(NotificationManager.class)).notifyAsUser(Long.toString(currentTimeMillis), 61, new Notification.Builder(context, SystemNotificationChannels.ALERTS).setGroup("com.android.fgs-bg-restricted").setSmallIcon(17303666).setWhen(0L).setColor(context.getColor(17170460)).setTicker("Foreground Service BG-Launch Restricted").setContentTitle("Foreground Service BG-Launch Restricted").setContentText("App restricted: " + serviceRecord.mRecentCallingPackage).setStyle(new Notification.BigTextStyle().bigText(DATE_FORMATTER.format(Long.valueOf(currentTimeMillis)) + " " + serviceRecord.mInfoAllowStartForeground)).build(), UserHandle.ALL);
        }
    }

    public final boolean isBgFgsRestrictionEnabled(ServiceRecord serviceRecord) {
        return this.mAm.mConstants.mFlagFgsStartRestrictionEnabled && CompatChanges.isChangeEnabled(170668199L, serviceRecord.appInfo.uid) && (!this.mAm.mConstants.mFgsStartRestrictionCheckCallerTargetSdk || CompatChanges.isChangeEnabled(170668199L, serviceRecord.mRecentCallingUid));
    }

    public final void logFgsBackgroundStart(ServiceRecord serviceRecord) {
        if (serviceRecord.mLoggedInfoAllowStartForeground) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Background started FGS: ");
        sb.append(serviceRecord.mAllowStartForeground != -1 ? "Allowed " : "Disallowed ");
        sb.append(serviceRecord.mInfoAllowStartForeground);
        sb.append(serviceRecord.isShortFgs() ? " (Called on SHORT_SERVICE)" : "");
        String sb2 = sb.toString();
        if (serviceRecord.mAllowStartForeground != -1) {
            if (ActivityManagerUtils.shouldSamplePackageForAtom(serviceRecord.packageName, this.mAm.mConstants.mFgsStartAllowedLogSampleRate)) {
                Slog.wtfQuiet("ActivityManager", sb2);
            }
            Slog.i("ActivityManager", sb2);
        } else {
            Slog.wtfQuiet("ActivityManager", sb2);
            Slog.w("ActivityManager", sb2);
        }
        serviceRecord.mLoggedInfoAllowStartForeground = true;
    }

    public final void logFGSStateChangeLocked(ServiceRecord serviceRecord, int i, int i2, int i3, int i4) {
        boolean z;
        int i5;
        char c;
        int i6;
        if (ActivityManagerUtils.shouldSamplePackageForAtom(serviceRecord.packageName, this.mAm.mConstants.mFgsAtomSampleRate)) {
            if (i == 1 || i == 2) {
                z = serviceRecord.mAllowWhileInUsePermissionInFgsAtEntering;
                i5 = serviceRecord.mAllowStartForegroundAtEntering;
            } else {
                z = serviceRecord.mAllowWhileInUsePermissionInFgs;
                i5 = serviceRecord.mAllowStartForeground;
            }
            boolean z2 = z;
            int i7 = i5;
            ApplicationInfo applicationInfo = serviceRecord.mRecentCallerApplicationInfo;
            int i8 = applicationInfo != null ? applicationInfo.targetSdkVersion : 0;
            ApplicationInfo applicationInfo2 = serviceRecord.appInfo;
            int i9 = applicationInfo2.uid;
            String str = serviceRecord.shortInstanceName;
            int i10 = applicationInfo2.targetSdkVersion;
            int i11 = serviceRecord.mRecentCallingUid;
            ActivityManagerService.FgsTempAllowListItem fgsTempAllowListItem = serviceRecord.mInfoTempFgsAllowListReason;
            int i12 = fgsTempAllowListItem != null ? fgsTempAllowListItem.mCallingUid : -1;
            boolean z3 = serviceRecord.mFgsNotificationWasDeferred;
            boolean z4 = serviceRecord.mFgsNotificationShown;
            int i13 = serviceRecord.mStartForegroundCount;
            int hashComponentNameForAtom = ActivityManagerUtils.hashComponentNameForAtom(str);
            boolean z5 = serviceRecord.mFgsHasNotificationPermission;
            int i14 = serviceRecord.foregroundServiceType;
            boolean z6 = serviceRecord.mIsFgsDelegate;
            ForegroundServiceDelegation foregroundServiceDelegation = serviceRecord.mFgsDelegation;
            FrameworkStatsLog.write(60, i9, str, i, z2, i7, i10, i11, i8, i12, z3, z4, i2, i13, hashComponentNameForAtom, z5, i14, i4, z6, foregroundServiceDelegation != null ? foregroundServiceDelegation.mOptions.mClientUid : -1, foregroundServiceDelegation != null ? foregroundServiceDelegation.mOptions.mDelegationService : 0, 0, null, null);
            if (i == 1) {
                i6 = 30100;
                c = 2;
            } else {
                c = 2;
                if (i == 2) {
                    i6 = 30102;
                } else if (i != 3) {
                    return;
                } else {
                    i6 = 30101;
                }
            }
            Object[] objArr = new Object[12];
            objArr[0] = Integer.valueOf(serviceRecord.userId);
            objArr[1] = serviceRecord.shortInstanceName;
            objArr[c] = Integer.valueOf(z2 ? 1 : 0);
            objArr[3] = PowerExemptionManager.reasonCodeToString(i7);
            objArr[4] = Integer.valueOf(serviceRecord.appInfo.targetSdkVersion);
            objArr[5] = Integer.valueOf(i8);
            objArr[6] = Integer.valueOf(serviceRecord.mFgsNotificationWasDeferred ? 1 : 0);
            objArr[7] = Integer.valueOf(serviceRecord.mFgsNotificationShown ? 1 : 0);
            objArr[8] = Integer.valueOf(i2);
            objArr[9] = Integer.valueOf(serviceRecord.mStartForegroundCount);
            objArr[10] = fgsStopReasonToString(i3);
            objArr[11] = Integer.valueOf(serviceRecord.foregroundServiceType);
            EventLog.writeEvent(i6, objArr);
        }
    }

    public final void updateNumForegroundServicesLocked() {
        sNumForegroundServices.set(this.mAm.mProcessList.getNumForegroundServices());
    }

    public boolean canAllowWhileInUsePermissionInFgsLocked(int i, int i2, String str) {
        return shouldAllowFgsWhileInUsePermissionLocked(str, i, i2, null, BackgroundStartPrivileges.NONE, false) != -1;
    }

    public final boolean verifyPackage(String str, int i) {
        if (i == 0 || i == 1000) {
            return true;
        }
        return this.mAm.getPackageManagerInternal().isSameApp(str, i, UserHandle.getUserId(i));
    }

    public boolean startForegroundServiceDelegateLocked(ForegroundServiceDelegationOptions foregroundServiceDelegationOptions, final ServiceConnection serviceConnection) {
        ProcessRecord processRecord;
        IApplicationThread thread;
        ProcessRecord processRecord2;
        IApplicationThread iApplicationThread;
        ServiceRecord serviceRecord;
        Slog.v("ActivityManager", "startForegroundServiceDelegateLocked " + foregroundServiceDelegationOptions.getDescription());
        final ComponentName componentName = foregroundServiceDelegationOptions.getComponentName();
        for (int size = this.mFgsDelegations.size() - 1; size >= 0; size--) {
            if (this.mFgsDelegations.keyAt(size).mOptions.isSameDelegate(foregroundServiceDelegationOptions)) {
                Slog.e("ActivityManager", "startForegroundServiceDelegate " + foregroundServiceDelegationOptions.getDescription() + " already exists, multiple connections are not allowed");
                return false;
            }
        }
        int i = foregroundServiceDelegationOptions.mClientPid;
        int i2 = foregroundServiceDelegationOptions.mClientUid;
        int userId = UserHandle.getUserId(i2);
        String str = foregroundServiceDelegationOptions.mClientPackageName;
        if (!canStartForegroundServiceLocked(i, i2, str)) {
            Slog.d("ActivityManager", "startForegroundServiceDelegateLocked aborted, app is in the background");
            return false;
        }
        IApplicationThread iApplicationThread2 = foregroundServiceDelegationOptions.mClientAppThread;
        if (iApplicationThread2 != null) {
            iApplicationThread = iApplicationThread2;
            processRecord2 = this.mAm.getRecordForAppLOSP(iApplicationThread2);
        } else {
            synchronized (this.mAm.mPidsSelfLocked) {
                processRecord = this.mAm.mPidsSelfLocked.get(i);
                thread = processRecord.getThread();
            }
            processRecord2 = processRecord;
            iApplicationThread = thread;
        }
        if (processRecord2 == null) {
            throw new SecurityException("Unable to find app for caller " + iApplicationThread + " (pid=" + i + ") when startForegroundServiceDelegateLocked " + componentName);
        }
        Intent intent = new Intent();
        intent.setComponent(componentName);
        ProcessRecord processRecord3 = processRecord2;
        IApplicationThread iApplicationThread3 = iApplicationThread;
        ServiceLookupResult retrieveServiceLocked = retrieveServiceLocked(intent, null, false, -1, null, null, str, i, i2, userId, true, false, false, false, foregroundServiceDelegationOptions, false);
        if (retrieveServiceLocked == null || (serviceRecord = retrieveServiceLocked.record) == null) {
            Slog.d("ActivityManager", "startForegroundServiceDelegateLocked retrieveServiceLocked returns null");
            return false;
        }
        serviceRecord.setProcess(processRecord3, iApplicationThread3, i, null);
        serviceRecord.mIsFgsDelegate = true;
        final ForegroundServiceDelegation foregroundServiceDelegation = new ForegroundServiceDelegation(foregroundServiceDelegationOptions, serviceConnection);
        serviceRecord.mFgsDelegation = foregroundServiceDelegation;
        this.mFgsDelegations.put(foregroundServiceDelegation, serviceRecord);
        serviceRecord.isForeground = true;
        serviceRecord.mFgsEnterTime = SystemClock.uptimeMillis();
        serviceRecord.foregroundServiceType = foregroundServiceDelegationOptions.mForegroundServiceTypes;
        setFgsRestrictionLocked(str, i, i2, intent, serviceRecord, userId, BackgroundStartPrivileges.NONE, false);
        ProcessServiceRecord processServiceRecord = processRecord3.mServices;
        processServiceRecord.startService(serviceRecord);
        updateServiceForegroundLocked(processServiceRecord, true);
        synchronized (this.mAm.mProcessStats.mLock) {
            ServiceState tracker = serviceRecord.getTracker();
            if (tracker != null) {
                tracker.setForeground(true, this.mAm.mProcessStats.getMemFactorLocked(), SystemClock.uptimeMillis());
            }
        }
        this.mAm.mBatteryStatsService.noteServiceStartRunning(i2, str, componentName.getClassName());
        AppOpsService appOpsService = this.mAm.mAppOpsService;
        appOpsService.startOperation(AppOpsManager.getToken(appOpsService), 76, serviceRecord.appInfo.uid, serviceRecord.packageName, null, true, false, null, false, 0, -1);
        registerAppOpCallbackLocked(serviceRecord);
        logFGSStateChangeLocked(serviceRecord, 1, 0, 0, 0);
        if (serviceConnection != null) {
            this.mAm.mHandler.post(new Runnable() { // from class: com.android.server.am.ActiveServices$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ActiveServices.lambda$startForegroundServiceDelegateLocked$7(serviceConnection, componentName, foregroundServiceDelegation);
                }
            });
        }
        signalForegroundServiceObserversLocked(serviceRecord);
        return true;
    }

    public static /* synthetic */ void lambda$startForegroundServiceDelegateLocked$7(ServiceConnection serviceConnection, ComponentName componentName, ForegroundServiceDelegation foregroundServiceDelegation) {
        serviceConnection.onServiceConnected(componentName, foregroundServiceDelegation.mBinder);
    }

    public void stopForegroundServiceDelegateLocked(ForegroundServiceDelegationOptions foregroundServiceDelegationOptions) {
        ServiceRecord serviceRecord;
        int size = this.mFgsDelegations.size();
        while (true) {
            size--;
            if (size < 0) {
                serviceRecord = null;
                break;
            } else if (this.mFgsDelegations.keyAt(size).mOptions.isSameDelegate(foregroundServiceDelegationOptions)) {
                Slog.d("ActivityManager", "stopForegroundServiceDelegateLocked " + foregroundServiceDelegationOptions.getDescription());
                serviceRecord = this.mFgsDelegations.valueAt(size);
                break;
            }
        }
        if (serviceRecord != null) {
            bringDownServiceLocked(serviceRecord, false);
            return;
        }
        Slog.e("ActivityManager", "stopForegroundServiceDelegateLocked delegate does not exist " + foregroundServiceDelegationOptions.getDescription());
    }

    public void stopForegroundServiceDelegateLocked(ServiceConnection serviceConnection) {
        ServiceRecord serviceRecord;
        ForegroundServiceDelegation keyAt;
        int size = this.mFgsDelegations.size();
        while (true) {
            size--;
            if (size < 0) {
                serviceRecord = null;
                break;
            }
            if (this.mFgsDelegations.keyAt(size).mConnection == serviceConnection) {
                Slog.d("ActivityManager", "stopForegroundServiceDelegateLocked " + keyAt.mOptions.getDescription());
                serviceRecord = this.mFgsDelegations.valueAt(size);
                break;
            }
        }
        if (serviceRecord != null) {
            bringDownServiceLocked(serviceRecord, false);
        } else {
            Slog.e("ActivityManager", "stopForegroundServiceDelegateLocked delegate does not exist");
        }
    }

    public static void getClientPackages(ServiceRecord serviceRecord, ArraySet<String> arraySet) {
        ArrayMap<IBinder, ArrayList<ConnectionRecord>> connections = serviceRecord.getConnections();
        for (int size = connections.size() - 1; size >= 0; size--) {
            ArrayList<ConnectionRecord> valueAt = connections.valueAt(size);
            int size2 = valueAt.size();
            for (int i = 0; i < size2; i++) {
                ProcessRecord processRecord = valueAt.get(i).binding.client;
                if (processRecord != null) {
                    arraySet.add(processRecord.info.packageName);
                }
            }
        }
    }

    public ArraySet<String> getClientPackagesLocked(String str) {
        ArraySet<String> arraySet = new ArraySet<>();
        for (int i : this.mAm.mUserController.getUsers()) {
            ArrayMap<ComponentName, ServiceRecord> servicesLocked = getServicesLocked(i);
            int size = servicesLocked.size();
            for (int i2 = 0; i2 < size; i2++) {
                ServiceRecord valueAt = servicesLocked.valueAt(i2);
                if (valueAt.name.getPackageName().equals(str)) {
                    getClientPackages(valueAt, arraySet);
                }
            }
        }
        return arraySet;
    }
}
