package com.android.server.p006am;

import android.app.ActivityManagerInternal;
import android.app.IProcessObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.SomeArgs;
import com.android.server.backup.BackupManagerConstants;
import com.android.server.p006am.BaseAppStateEvents;
import com.android.server.p006am.BaseAppStateEventsTracker;
import com.android.server.p006am.BaseAppStateTimeEvents;
import com.android.server.p006am.BaseAppStateTracker;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
/* renamed from: com.android.server.am.AppFGSTracker */
/* loaded from: classes.dex */
public final class AppFGSTracker extends BaseAppStateDurationsTracker<AppFGSPolicy, PackageDurations> implements ActivityManagerInternal.ForegroundServiceStateListener {
    @GuardedBy({"mLock"})
    public final UidProcessMap<SparseBooleanArray> mFGSNotificationIDs;
    public final MyHandler mHandler;
    @VisibleForTesting
    final NotificationListener mNotificationListener;
    public final IProcessObserver.Stub mProcessObserver;
    public final ArrayMap<PackageDurations, Long> mTmpPkgDurations;

    @Override // com.android.server.p006am.BaseAppStateTracker
    public int getType() {
        return 3;
    }

    public void onForegroundServiceStateChanged(String str, int i, int i2, boolean z) {
        this.mHandler.obtainMessage(!z ? 1 : 0, i2, i, str).sendToTarget();
    }

    public void onForegroundServiceNotificationUpdated(String str, int i, int i2, boolean z) {
        SomeArgs obtain = SomeArgs.obtain();
        obtain.argi1 = i;
        obtain.argi2 = i2;
        obtain.arg1 = str;
        obtain.arg2 = z ? Boolean.TRUE : Boolean.FALSE;
        this.mHandler.obtainMessage(3, obtain).sendToTarget();
    }

    /* renamed from: com.android.server.am.AppFGSTracker$MyHandler */
    /* loaded from: classes.dex */
    public static class MyHandler extends Handler {
        public final AppFGSTracker mTracker;

        public MyHandler(AppFGSTracker appFGSTracker) {
            super(appFGSTracker.mBgHandler.getLooper());
            this.mTracker = appFGSTracker;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    this.mTracker.handleForegroundServicesChanged((String) message.obj, message.arg1, message.arg2, true);
                    return;
                case 1:
                    this.mTracker.handleForegroundServicesChanged((String) message.obj, message.arg1, message.arg2, false);
                    return;
                case 2:
                    this.mTracker.handleForegroundServicesChanged((String) message.obj, message.arg1, message.arg2);
                    return;
                case 3:
                    SomeArgs someArgs = (SomeArgs) message.obj;
                    this.mTracker.handleForegroundServiceNotificationUpdated((String) someArgs.arg1, someArgs.argi1, someArgs.argi2, ((Boolean) someArgs.arg2).booleanValue());
                    someArgs.recycle();
                    return;
                case 4:
                    this.mTracker.checkLongRunningFgs();
                    return;
                case 5:
                    this.mTracker.handleNotificationPosted((String) message.obj, message.arg1, message.arg2);
                    return;
                case 6:
                    this.mTracker.handleNotificationRemoved((String) message.obj, message.arg1, message.arg2);
                    return;
                default:
                    return;
            }
        }
    }

    public AppFGSTracker(Context context, AppRestrictionController appRestrictionController) {
        this(context, appRestrictionController, null, null);
    }

    public AppFGSTracker(Context context, AppRestrictionController appRestrictionController, Constructor<? extends BaseAppStateTracker.Injector<AppFGSPolicy>> constructor, Object obj) {
        super(context, appRestrictionController, constructor, obj);
        this.mFGSNotificationIDs = new UidProcessMap<>();
        this.mTmpPkgDurations = new ArrayMap<>();
        this.mNotificationListener = new NotificationListener();
        this.mProcessObserver = new IProcessObserver.Stub() { // from class: com.android.server.am.AppFGSTracker.1
            public void onForegroundActivitiesChanged(int i, int i2, boolean z) {
            }

            public void onProcessDied(int i, int i2) {
            }

            public void onForegroundServicesChanged(int i, int i2, int i3) {
                String packageName = AppFGSTracker.this.mAppRestrictionController.getPackageName(i);
                if (packageName != null) {
                    AppFGSTracker.this.mHandler.obtainMessage(2, i2, i3, packageName).sendToTarget();
                }
            }
        };
        this.mHandler = new MyHandler(this);
        BaseAppStateTracker.Injector<T> injector = this.mInjector;
        injector.setPolicy(new AppFGSPolicy(injector, this));
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public void onSystemReady() {
        super.onSystemReady();
        this.mInjector.getActivityManagerInternal().addForegroundServiceStateListener(this);
        this.mInjector.getActivityManagerInternal().registerProcessObserver(this.mProcessObserver);
    }

    @Override // com.android.server.p006am.BaseAppStateDurationsTracker, com.android.server.p006am.BaseAppStateEventsTracker
    @VisibleForTesting
    public void reset() {
        this.mHandler.removeMessages(4);
        super.reset();
    }

    @Override // com.android.server.p006am.BaseAppStateEvents.Factory
    public PackageDurations createAppStateEvents(int i, String str) {
        return new PackageDurations(i, str, (BaseAppStateEvents.MaxTrackingDurationConfig) this.mInjector.getPolicy(), this);
    }

    @Override // com.android.server.p006am.BaseAppStateEvents.Factory
    public PackageDurations createAppStateEvents(PackageDurations packageDurations) {
        return new PackageDurations(packageDurations);
    }

    public final void handleForegroundServicesChanged(String str, int i, int i2, boolean z) {
        boolean z2;
        if (((AppFGSPolicy) this.mInjector.getPolicy()).isEnabled()) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            int shouldExemptUid = ((AppFGSPolicy) this.mInjector.getPolicy()).shouldExemptUid(i2);
            synchronized (this.mLock) {
                PackageDurations packageDurations = (PackageDurations) this.mPkgEvents.get(i2, str);
                if (packageDurations == null) {
                    packageDurations = createAppStateEvents(i2, str);
                    this.mPkgEvents.put(i2, str, packageDurations);
                }
                boolean isLongRunning = packageDurations.isLongRunning();
                packageDurations.addEvent(z, elapsedRealtime);
                z2 = isLongRunning && !packageDurations.hasForegroundServices();
                if (z2) {
                    packageDurations.setIsLongRunning(false);
                }
                packageDurations.mExemptReason = shouldExemptUid;
                scheduleDurationCheckLocked(elapsedRealtime);
            }
            if (z2) {
                ((AppFGSPolicy) this.mInjector.getPolicy()).onLongRunningFgsGone(str, i2);
            }
        }
    }

    public final void handleForegroundServiceNotificationUpdated(String str, int i, int i2, boolean z) {
        int indexOfKey;
        synchronized (this.mLock) {
            SparseBooleanArray sparseBooleanArray = this.mFGSNotificationIDs.get(i, str);
            if (!z) {
                if (sparseBooleanArray == null) {
                    sparseBooleanArray = new SparseBooleanArray();
                    this.mFGSNotificationIDs.put(i, str, sparseBooleanArray);
                }
                sparseBooleanArray.put(i2, false);
            } else if (sparseBooleanArray != null && (indexOfKey = sparseBooleanArray.indexOfKey(i2)) >= 0) {
                boolean valueAt = sparseBooleanArray.valueAt(indexOfKey);
                sparseBooleanArray.removeAt(indexOfKey);
                if (sparseBooleanArray.size() == 0) {
                    this.mFGSNotificationIDs.remove(i, str);
                }
                for (int size = sparseBooleanArray.size() - 1; size >= 0; size--) {
                    if (sparseBooleanArray.valueAt(size)) {
                        return;
                    }
                }
                if (valueAt) {
                    notifyListenersOnStateChange(i, str, false, SystemClock.elapsedRealtime(), 8);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final boolean hasForegroundServiceNotificationsLocked(String str, int i) {
        SparseBooleanArray sparseBooleanArray = this.mFGSNotificationIDs.get(i, str);
        if (sparseBooleanArray != null && sparseBooleanArray.size() != 0) {
            for (int size = sparseBooleanArray.size() - 1; size >= 0; size--) {
                if (sparseBooleanArray.valueAt(size)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final void handleNotificationPosted(String str, int i, int i2) {
        int indexOfKey;
        boolean z;
        synchronized (this.mLock) {
            SparseBooleanArray sparseBooleanArray = this.mFGSNotificationIDs.get(i, str);
            if (sparseBooleanArray != null && (indexOfKey = sparseBooleanArray.indexOfKey(i2)) >= 0) {
                if (sparseBooleanArray.valueAt(indexOfKey)) {
                    return;
                }
                int size = sparseBooleanArray.size() - 1;
                while (true) {
                    if (size < 0) {
                        z = false;
                        break;
                    } else if (sparseBooleanArray.valueAt(size)) {
                        z = true;
                        break;
                    } else {
                        size--;
                    }
                }
                sparseBooleanArray.setValueAt(indexOfKey, true);
                if (!z) {
                    notifyListenersOnStateChange(i, str, true, SystemClock.elapsedRealtime(), 8);
                }
            }
        }
    }

    public final void handleNotificationRemoved(String str, int i, int i2) {
        int indexOfKey;
        synchronized (this.mLock) {
            SparseBooleanArray sparseBooleanArray = this.mFGSNotificationIDs.get(i, str);
            if (sparseBooleanArray != null && (indexOfKey = sparseBooleanArray.indexOfKey(i2)) >= 0) {
                if (sparseBooleanArray.valueAt(indexOfKey)) {
                    sparseBooleanArray.setValueAt(indexOfKey, false);
                    for (int size = sparseBooleanArray.size() - 1; size >= 0; size--) {
                        if (sparseBooleanArray.valueAt(size)) {
                            return;
                        }
                    }
                    notifyListenersOnStateChange(i, str, false, SystemClock.elapsedRealtime(), 8);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void scheduleDurationCheckLocked(long j) {
        SparseArray map = this.mPkgEvents.getMap();
        long j2 = -1;
        for (int size = map.size() - 1; size >= 0; size--) {
            ArrayMap arrayMap = (ArrayMap) map.valueAt(size);
            for (int size2 = arrayMap.size() - 1; size2 >= 0; size2--) {
                PackageDurations packageDurations = (PackageDurations) arrayMap.valueAt(size2);
                if (packageDurations.hasForegroundServices() && !packageDurations.isLongRunning()) {
                    j2 = Math.max(getTotalDurations(packageDurations, j), j2);
                }
            }
        }
        this.mHandler.removeMessages(4);
        if (j2 >= 0) {
            this.mHandler.sendEmptyMessageDelayed(4, this.mInjector.getServiceStartForegroundTimeout() + Math.max(0L, ((AppFGSPolicy) this.mInjector.getPolicy()).getFgsLongRunningThreshold() - j2));
        }
    }

    public final void checkLongRunningFgs() {
        AppFGSPolicy appFGSPolicy = (AppFGSPolicy) this.mInjector.getPolicy();
        final ArrayMap<PackageDurations, Long> arrayMap = this.mTmpPkgDurations;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long fgsLongRunningThreshold = appFGSPolicy.getFgsLongRunningThreshold();
        long max = Math.max(0L, elapsedRealtime - appFGSPolicy.getFgsLongRunningWindowSize());
        synchronized (this.mLock) {
            SparseArray map = this.mPkgEvents.getMap();
            int i = 1;
            int size = map.size() - 1;
            while (size >= 0) {
                ArrayMap arrayMap2 = (ArrayMap) map.valueAt(size);
                for (int size2 = arrayMap2.size() - i; size2 >= 0; size2--) {
                    PackageDurations packageDurations = (PackageDurations) arrayMap2.valueAt(size2);
                    if (packageDurations.hasForegroundServices() && !packageDurations.isLongRunning()) {
                        long totalDurations = getTotalDurations(packageDurations, elapsedRealtime);
                        if (totalDurations >= fgsLongRunningThreshold) {
                            arrayMap.put(packageDurations, Long.valueOf(totalDurations));
                            packageDurations.setIsLongRunning(true);
                        }
                    }
                }
                size--;
                i = 1;
            }
            trim(max);
        }
        int size3 = arrayMap.size();
        if (size3 > 0) {
            Integer[] numArr = new Integer[size3];
            for (int i2 = 0; i2 < size3; i2++) {
                numArr[i2] = Integer.valueOf(i2);
            }
            Arrays.sort(numArr, new Comparator() { // from class: com.android.server.am.AppFGSTracker$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$checkLongRunningFgs$0;
                    lambda$checkLongRunningFgs$0 = AppFGSTracker.lambda$checkLongRunningFgs$0(arrayMap, (Integer) obj, (Integer) obj2);
                    return lambda$checkLongRunningFgs$0;
                }
            });
            for (int i3 = size3 - 1; i3 >= 0; i3--) {
                PackageDurations keyAt = arrayMap.keyAt(numArr[i3].intValue());
                appFGSPolicy.onLongRunningFgs(keyAt.mPackageName, keyAt.mUid, keyAt.mExemptReason);
            }
            arrayMap.clear();
        }
        synchronized (this.mLock) {
            scheduleDurationCheckLocked(elapsedRealtime);
        }
    }

    public static /* synthetic */ int lambda$checkLongRunningFgs$0(ArrayMap arrayMap, Integer num, Integer num2) {
        return Long.compare(((Long) arrayMap.valueAt(num.intValue())).longValue(), ((Long) arrayMap.valueAt(num2.intValue())).longValue());
    }

    public final void handleForegroundServicesChanged(String str, int i, int i2) {
        if (((AppFGSPolicy) this.mInjector.getPolicy()).isEnabled()) {
            int shouldExemptUid = ((AppFGSPolicy) this.mInjector.getPolicy()).shouldExemptUid(i);
            long elapsedRealtime = SystemClock.elapsedRealtime();
            synchronized (this.mLock) {
                PackageDurations packageDurations = (PackageDurations) this.mPkgEvents.get(i, str);
                if (packageDurations == null) {
                    packageDurations = new PackageDurations(i, str, (BaseAppStateEvents.MaxTrackingDurationConfig) this.mInjector.getPolicy(), this);
                    this.mPkgEvents.put(i, str, packageDurations);
                }
                packageDurations.setForegroundServiceType(i2, elapsedRealtime);
                packageDurations.mExemptReason = shouldExemptUid;
            }
        }
    }

    public final void onBgFgsMonitorEnabled(boolean z) {
        if (z) {
            synchronized (this.mLock) {
                scheduleDurationCheckLocked(SystemClock.elapsedRealtime());
            }
            try {
                this.mNotificationListener.registerAsSystemService(this.mContext, new ComponentName(this.mContext, NotificationListener.class), -1);
                return;
            } catch (RemoteException unused) {
                return;
            }
        }
        try {
            this.mNotificationListener.unregisterAsSystemService();
        } catch (RemoteException unused2) {
        }
        this.mHandler.removeMessages(4);
        synchronized (this.mLock) {
            this.mPkgEvents.clear();
        }
    }

    public final void onBgFgsLongRunningThresholdChanged() {
        synchronized (this.mLock) {
            if (((AppFGSPolicy) this.mInjector.getPolicy()).isEnabled()) {
                scheduleDurationCheckLocked(SystemClock.elapsedRealtime());
            }
        }
    }

    public static int foregroundServiceTypeToIndex(int i) {
        if (i == 0) {
            return 0;
        }
        return Integer.numberOfTrailingZeros(i) + 1;
    }

    public static int indexToForegroundServiceType(int i) {
        if (i == PackageDurations.DEFAULT_INDEX) {
            return 0;
        }
        return 1 << (i - 1);
    }

    public long getTotalDurations(PackageDurations packageDurations, long j) {
        return getTotalDurations(packageDurations.mPackageName, packageDurations.mUid, j, foregroundServiceTypeToIndex(0));
    }

    public long getTotalDurations(int i, long j) {
        return getTotalDurations(i, j, foregroundServiceTypeToIndex(0));
    }

    public boolean hasForegroundServices(String str, int i) {
        boolean z;
        synchronized (this.mLock) {
            PackageDurations packageDurations = (PackageDurations) this.mPkgEvents.get(i, str);
            z = packageDurations != null && packageDurations.hasForegroundServices();
        }
        return z;
    }

    public boolean hasForegroundServiceNotifications(String str, int i) {
        boolean hasForegroundServiceNotificationsLocked;
        synchronized (this.mLock) {
            hasForegroundServiceNotificationsLocked = hasForegroundServiceNotificationsLocked(str, i);
        }
        return hasForegroundServiceNotificationsLocked;
    }

    public boolean hasForegroundServiceNotifications(int i) {
        synchronized (this.mLock) {
            ArrayMap<String, SparseBooleanArray> arrayMap = this.mFGSNotificationIDs.getMap().get(i);
            if (arrayMap != null) {
                for (int size = arrayMap.size() - 1; size >= 0; size--) {
                    if (hasForegroundServiceNotificationsLocked(arrayMap.keyAt(size), i)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public byte[] getTrackerInfoForStatsd(int i) {
        long totalDurations = getTotalDurations(i, SystemClock.elapsedRealtime());
        if (totalDurations == 0) {
            return null;
        }
        ProtoOutputStream protoOutputStream = new ProtoOutputStream();
        protoOutputStream.write(1133871366145L, hasForegroundServiceNotifications(i));
        protoOutputStream.write(1112396529666L, totalDurations);
        protoOutputStream.flush();
        return protoOutputStream.getBytes();
    }

    @Override // com.android.server.p006am.BaseAppStateEventsTracker, com.android.server.p006am.BaseAppStateTracker
    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.println("APP FOREGROUND SERVICE TRACKER:");
        super.dump(printWriter, "  " + str);
    }

    @Override // com.android.server.p006am.BaseAppStateEventsTracker
    public void dumpOthers(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.println("APPS WITH ACTIVE FOREGROUND SERVICES:");
        String str2 = "  " + str;
        synchronized (this.mLock) {
            SparseArray<ArrayMap<String, SparseBooleanArray>> map = this.mFGSNotificationIDs.getMap();
            if (map.size() == 0) {
                printWriter.print(str2);
                printWriter.println("(none)");
            }
            int size = map.size();
            for (int i = 0; i < size; i++) {
                int keyAt = map.keyAt(i);
                String formatUid = UserHandle.formatUid(keyAt);
                ArrayMap<String, SparseBooleanArray> valueAt = map.valueAt(i);
                int size2 = valueAt.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    String keyAt2 = valueAt.keyAt(i2);
                    printWriter.print(str2);
                    printWriter.print(keyAt2);
                    printWriter.print('/');
                    printWriter.print(formatUid);
                    printWriter.print(" notification=");
                    printWriter.println(hasForegroundServiceNotificationsLocked(keyAt2, keyAt));
                }
            }
        }
    }

    /* renamed from: com.android.server.am.AppFGSTracker$PackageDurations */
    /* loaded from: classes.dex */
    public static class PackageDurations extends BaseAppStateDurations<BaseAppStateTimeEvents.BaseTimeEvent> {
        public static final int DEFAULT_INDEX = AppFGSTracker.foregroundServiceTypeToIndex(0);
        public int mForegroundServiceTypes;
        public boolean mIsLongRunning;
        public final AppFGSTracker mTracker;

        /* JADX WARN: Multi-variable type inference failed */
        public PackageDurations(int i, String str, BaseAppStateEvents.MaxTrackingDurationConfig maxTrackingDurationConfig, AppFGSTracker appFGSTracker) {
            super(i, str, 31, "ActivityManager", maxTrackingDurationConfig);
            this.mEvents[DEFAULT_INDEX] = new LinkedList();
            this.mTracker = appFGSTracker;
        }

        public PackageDurations(PackageDurations packageDurations) {
            super(packageDurations);
            this.mIsLongRunning = packageDurations.mIsLongRunning;
            this.mForegroundServiceTypes = packageDurations.mForegroundServiceTypes;
            this.mTracker = packageDurations.mTracker;
        }

        public void addEvent(boolean z, long j) {
            addEvent(z, new BaseAppStateTimeEvents.BaseTimeEvent(j), DEFAULT_INDEX);
            if (!z && !hasForegroundServices()) {
                this.mIsLongRunning = false;
            }
            if (z || this.mForegroundServiceTypes == 0) {
                return;
            }
            int i = 1;
            while (true) {
                Object[] objArr = this.mEvents;
                if (i < objArr.length) {
                    if (objArr[i] != null && isActive(i)) {
                        this.mEvents[i].add(new BaseAppStateTimeEvents.BaseTimeEvent(j));
                        notifyListenersOnStateChangeIfNecessary(false, j, AppFGSTracker.indexToForegroundServiceType(i));
                    }
                    i++;
                } else {
                    this.mForegroundServiceTypes = 0;
                    return;
                }
            }
        }

        public void setForegroundServiceType(int i, long j) {
            if (i == this.mForegroundServiceTypes || !hasForegroundServices()) {
                return;
            }
            int i2 = this.mForegroundServiceTypes ^ i;
            int highestOneBit = Integer.highestOneBit(i2);
            while (highestOneBit != 0) {
                int foregroundServiceTypeToIndex = AppFGSTracker.foregroundServiceTypeToIndex(highestOneBit);
                Object[] objArr = this.mEvents;
                if (foregroundServiceTypeToIndex < objArr.length) {
                    if ((i & highestOneBit) != 0) {
                        if (objArr[foregroundServiceTypeToIndex] == null) {
                            objArr[foregroundServiceTypeToIndex] = new LinkedList();
                        }
                        if (!isActive(foregroundServiceTypeToIndex)) {
                            this.mEvents[foregroundServiceTypeToIndex].add(new BaseAppStateTimeEvents.BaseTimeEvent(j));
                            notifyListenersOnStateChangeIfNecessary(true, j, highestOneBit);
                        }
                    } else if (objArr[foregroundServiceTypeToIndex] != null && isActive(foregroundServiceTypeToIndex)) {
                        this.mEvents[foregroundServiceTypeToIndex].add(new BaseAppStateTimeEvents.BaseTimeEvent(j));
                        notifyListenersOnStateChangeIfNecessary(false, j, highestOneBit);
                    }
                }
                i2 &= ~highestOneBit;
                highestOneBit = Integer.highestOneBit(i2);
            }
            this.mForegroundServiceTypes = i;
        }

        public final void notifyListenersOnStateChangeIfNecessary(boolean z, long j, int i) {
            int i2 = 2;
            if (i != 2) {
                if (i != 8) {
                    return;
                }
                i2 = 4;
            }
            this.mTracker.notifyListenersOnStateChange(this.mUid, this.mPackageName, z, j, i2);
        }

        public void setIsLongRunning(boolean z) {
            this.mIsLongRunning = z;
        }

        public boolean isLongRunning() {
            return this.mIsLongRunning;
        }

        public boolean hasForegroundServices() {
            return isActive(DEFAULT_INDEX);
        }

        @Override // com.android.server.p006am.BaseAppStateEvents
        public String formatEventTypeLabel(int i) {
            if (i == DEFAULT_INDEX) {
                return "Overall foreground services: ";
            }
            return ServiceInfo.foregroundServiceTypeToLabel(AppFGSTracker.indexToForegroundServiceType(i)) + ": ";
        }
    }

    @VisibleForTesting
    /* renamed from: com.android.server.am.AppFGSTracker$NotificationListener */
    /* loaded from: classes.dex */
    public class NotificationListener extends NotificationListenerService {
        public NotificationListener() {
        }

        @Override // android.service.notification.NotificationListenerService
        public void onNotificationPosted(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap) {
            AppFGSTracker.this.mHandler.obtainMessage(5, statusBarNotification.getUid(), statusBarNotification.getId(), statusBarNotification.getPackageName()).sendToTarget();
        }

        @Override // android.service.notification.NotificationListenerService
        public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
            AppFGSTracker.this.mHandler.obtainMessage(6, statusBarNotification.getUid(), statusBarNotification.getId(), statusBarNotification.getPackageName()).sendToTarget();
        }
    }

    /* renamed from: com.android.server.am.AppFGSTracker$AppFGSPolicy */
    /* loaded from: classes.dex */
    public static final class AppFGSPolicy extends BaseAppStateEventsTracker.BaseAppStateEventsPolicy<AppFGSTracker> {
        public volatile long mBgFgsLocationThresholdMs;
        public volatile long mBgFgsLongRunningThresholdMs;
        public volatile long mBgFgsMediaPlaybackThresholdMs;

        public AppFGSPolicy(BaseAppStateTracker.Injector injector, AppFGSTracker appFGSTracker) {
            super(injector, appFGSTracker, "bg_fgs_monitor_enabled", true, "bg_fgs_long_running_window", BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS);
            this.mBgFgsLongRunningThresholdMs = 72000000L;
            this.mBgFgsMediaPlaybackThresholdMs = BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS;
            this.mBgFgsLocationThresholdMs = BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS;
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy, com.android.server.p006am.BaseAppStatePolicy
        public void onSystemReady() {
            super.onSystemReady();
            updateBgFgsLongRunningThreshold();
            updateBgFgsMediaPlaybackThreshold();
            updateBgFgsLocationThreshold();
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy, com.android.server.p006am.BaseAppStatePolicy
        public void onPropertiesChanged(String str) {
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -2001687768:
                    if (str.equals("bg_fgs_location_threshold")) {
                        c = 0;
                        break;
                    }
                    break;
                case 351955503:
                    if (str.equals("bg_fgs_long_running_threshold")) {
                        c = 1;
                        break;
                    }
                    break;
                case 803245321:
                    if (str.equals("bg_fgs_media_playback_threshold")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    updateBgFgsLocationThreshold();
                    return;
                case 1:
                    updateBgFgsLongRunningThreshold();
                    return;
                case 2:
                    updateBgFgsMediaPlaybackThreshold();
                    return;
                default:
                    super.onPropertiesChanged(str);
                    return;
            }
        }

        @Override // com.android.server.p006am.BaseAppStatePolicy
        public void onTrackerEnabled(boolean z) {
            ((AppFGSTracker) this.mTracker).onBgFgsMonitorEnabled(z);
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy
        public void onMaxTrackingDurationChanged(long j) {
            ((AppFGSTracker) this.mTracker).onBgFgsLongRunningThresholdChanged();
        }

        public final void updateBgFgsLongRunningThreshold() {
            long j = DeviceConfig.getLong("activity_manager", "bg_fgs_long_running_threshold", 72000000L);
            if (j != this.mBgFgsLongRunningThresholdMs) {
                this.mBgFgsLongRunningThresholdMs = j;
                ((AppFGSTracker) this.mTracker).onBgFgsLongRunningThresholdChanged();
            }
        }

        public final void updateBgFgsMediaPlaybackThreshold() {
            this.mBgFgsMediaPlaybackThresholdMs = DeviceConfig.getLong("activity_manager", "bg_fgs_media_playback_threshold", (long) BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
        }

        public final void updateBgFgsLocationThreshold() {
            this.mBgFgsLocationThresholdMs = DeviceConfig.getLong("activity_manager", "bg_fgs_location_threshold", (long) BackupManagerConstants.DEFAULT_KEY_VALUE_BACKUP_INTERVAL_MILLISECONDS);
        }

        public long getFgsLongRunningThreshold() {
            return this.mBgFgsLongRunningThresholdMs;
        }

        public long getFgsLongRunningWindowSize() {
            return getMaxTrackingDuration();
        }

        public long getFGSMediaPlaybackThreshold() {
            return this.mBgFgsMediaPlaybackThresholdMs;
        }

        public long getLocationFGSThreshold() {
            return this.mBgFgsLocationThresholdMs;
        }

        public void onLongRunningFgs(String str, int i, int i2) {
            if (i2 != -1) {
                return;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long fgsLongRunningWindowSize = getFgsLongRunningWindowSize();
            long max = Math.max(0L, elapsedRealtime - fgsLongRunningWindowSize);
            if (shouldExemptMediaPlaybackFGS(str, i, elapsedRealtime, fgsLongRunningWindowSize) || shouldExemptLocationFGS(str, i, elapsedRealtime, max)) {
                return;
            }
            ((AppFGSTracker) this.mTracker).mAppRestrictionController.postLongRunningFgsIfNecessary(str, i);
        }

        public boolean shouldExemptMediaPlaybackFGS(String str, int i, long j, long j2) {
            long compositeMediaPlaybackDurations = ((AppFGSTracker) this.mTracker).mAppRestrictionController.getCompositeMediaPlaybackDurations(str, i, j, j2);
            return compositeMediaPlaybackDurations > 0 && compositeMediaPlaybackDurations >= getFGSMediaPlaybackThreshold();
        }

        public boolean shouldExemptLocationFGS(String str, int i, long j, long j2) {
            long foregroundServiceTotalDurationsSince = ((AppFGSTracker) this.mTracker).mAppRestrictionController.getForegroundServiceTotalDurationsSince(str, i, j2, j, 8);
            return foregroundServiceTotalDurationsSince > 0 && foregroundServiceTotalDurationsSince >= getLocationFGSThreshold();
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy
        public String getExemptionReasonString(String str, int i, int i2) {
            if (i2 != -1) {
                return super.getExemptionReasonString(str, i, i2);
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long fgsLongRunningWindowSize = getFgsLongRunningWindowSize();
            long max = Math.max(0L, elapsedRealtime - getFgsLongRunningWindowSize());
            return "{mediaPlayback=" + shouldExemptMediaPlaybackFGS(str, i, elapsedRealtime, fgsLongRunningWindowSize) + ", location=" + shouldExemptLocationFGS(str, i, elapsedRealtime, max) + "}";
        }

        public void onLongRunningFgsGone(String str, int i) {
            ((AppFGSTracker) this.mTracker).mAppRestrictionController.cancelLongRunningFGSNotificationIfNecessary(str, i);
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy, com.android.server.p006am.BaseAppStatePolicy
        public void dump(PrintWriter printWriter, String str) {
            printWriter.print(str);
            printWriter.println("APP FOREGROUND SERVICE TRACKER POLICY SETTINGS:");
            String str2 = "  " + str;
            super.dump(printWriter, str2);
            if (isEnabled()) {
                printWriter.print(str2);
                printWriter.print("bg_fgs_long_running_threshold");
                printWriter.print('=');
                printWriter.println(this.mBgFgsLongRunningThresholdMs);
                printWriter.print(str2);
                printWriter.print("bg_fgs_media_playback_threshold");
                printWriter.print('=');
                printWriter.println(this.mBgFgsMediaPlaybackThresholdMs);
                printWriter.print(str2);
                printWriter.print("bg_fgs_location_threshold");
                printWriter.print('=');
                printWriter.println(this.mBgFgsLocationThresholdMs);
            }
        }
    }
}
