package com.android.server.p006am;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Pair;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p006am.AppBatteryTracker;
import com.android.server.p006am.BaseAppStateEvents;
import com.android.server.p006am.BaseAppStateEventsTracker;
import com.android.server.p006am.BaseAppStateTimeEvents;
import com.android.server.p006am.BaseAppStateTracker;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;
/* renamed from: com.android.server.am.AppBatteryExemptionTracker */
/* loaded from: classes.dex */
public final class AppBatteryExemptionTracker extends BaseAppStateDurationsTracker<AppBatteryExemptionPolicy, UidBatteryStates> implements BaseAppStateTracker.StateListener {
    @GuardedBy({"mLock"})
    public UidProcessMap<Integer> mUidPackageStates;

    @Override // com.android.server.p006am.BaseAppStateTracker
    public int getType() {
        return 2;
    }

    public AppBatteryExemptionTracker(Context context, AppRestrictionController appRestrictionController) {
        this(context, appRestrictionController, null, null);
    }

    public AppBatteryExemptionTracker(Context context, AppRestrictionController appRestrictionController, Constructor<? extends BaseAppStateTracker.Injector<AppBatteryExemptionPolicy>> constructor, Object obj) {
        super(context, appRestrictionController, constructor, obj);
        this.mUidPackageStates = new UidProcessMap<>();
        BaseAppStateTracker.Injector<T> injector = this.mInjector;
        injector.setPolicy(new AppBatteryExemptionPolicy(injector, this));
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public void onSystemReady() {
        super.onSystemReady();
        this.mAppRestrictionController.forEachTracker(new Consumer() { // from class: com.android.server.am.AppBatteryExemptionTracker$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AppBatteryExemptionTracker.this.lambda$onSystemReady$0((BaseAppStateTracker) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSystemReady$0(BaseAppStateTracker baseAppStateTracker) {
        baseAppStateTracker.registerStateListener(this);
    }

    @Override // com.android.server.p006am.BaseAppStateEvents.Factory
    public UidBatteryStates createAppStateEvents(int i, String str) {
        return new UidBatteryStates(i, "ActivityManager", (BaseAppStateEvents.MaxTrackingDurationConfig) this.mInjector.getPolicy());
    }

    @Override // com.android.server.p006am.BaseAppStateEvents.Factory
    public UidBatteryStates createAppStateEvents(UidBatteryStates uidBatteryStates) {
        return new UidBatteryStates(uidBatteryStates);
    }

    @Override // com.android.server.p006am.BaseAppStateTracker.StateListener
    public void onStateChange(int i, String str, boolean z, long j, int i2) {
        int i3;
        boolean z2;
        if (((AppBatteryExemptionPolicy) this.mInjector.getPolicy()).isEnabled()) {
            AppBatteryTracker.ImmutableBatteryUsage uidBatteryUsage = this.mAppRestrictionController.getUidBatteryUsage(i);
            int stateTypeToIndex = BaseAppStateTracker.stateTypeToIndex(i2);
            synchronized (this.mLock) {
                SparseArray<ArrayMap<String, Integer>> map = this.mUidPackageStates.getMap();
                ArrayMap<String, Integer> arrayMap = map.get(i);
                if (arrayMap == null) {
                    arrayMap = new ArrayMap<>();
                    map.put(i, arrayMap);
                }
                int indexOfKey = arrayMap.indexOfKey(str);
                boolean z3 = false;
                if (indexOfKey >= 0) {
                    i3 = arrayMap.valueAt(indexOfKey).intValue();
                } else {
                    arrayMap.put(str, 0);
                    indexOfKey = arrayMap.indexOfKey(str);
                    i3 = 0;
                }
                if (z) {
                    int size = arrayMap.size() - 1;
                    while (true) {
                        if (size < 0) {
                            break;
                        } else if ((arrayMap.valueAt(size).intValue() & i2) != 0) {
                            z3 = true;
                            break;
                        } else {
                            size--;
                        }
                    }
                    arrayMap.setValueAt(indexOfKey, Integer.valueOf(i3 | i2));
                    z2 = !z3;
                } else {
                    int i4 = i3 & (~i2);
                    arrayMap.setValueAt(indexOfKey, Integer.valueOf(i4));
                    int size2 = arrayMap.size() - 1;
                    while (true) {
                        if (size2 < 0) {
                            z3 = true;
                            break;
                        } else if ((arrayMap.valueAt(size2).intValue() & i2) != 0) {
                            break;
                        } else {
                            size2--;
                        }
                    }
                    if (i4 == 0) {
                        arrayMap.removeAt(indexOfKey);
                        if (arrayMap.size() == 0) {
                            map.remove(i);
                        }
                    }
                    z2 = z3;
                }
                if (z2) {
                    UidBatteryStates uidBatteryStates = (UidBatteryStates) this.mPkgEvents.get(i, "");
                    if (uidBatteryStates == null) {
                        uidBatteryStates = createAppStateEvents(i, "");
                        this.mPkgEvents.put(i, "", uidBatteryStates);
                    }
                    uidBatteryStates.addEvent(z, j, uidBatteryUsage, stateTypeToIndex);
                }
            }
        }
    }

    @Override // com.android.server.p006am.BaseAppStateDurationsTracker, com.android.server.p006am.BaseAppStateEventsTracker
    @VisibleForTesting
    public void reset() {
        super.reset();
        synchronized (this.mLock) {
            this.mUidPackageStates.clear();
        }
    }

    public final void onTrackerEnabled(boolean z) {
        if (z) {
            return;
        }
        synchronized (this.mLock) {
            this.mPkgEvents.clear();
            this.mUidPackageStates.clear();
        }
    }

    public AppBatteryTracker.ImmutableBatteryUsage getUidBatteryExemptedUsageSince(int i, long j, long j2, int i2) {
        if (!((AppBatteryExemptionPolicy) this.mInjector.getPolicy()).isEnabled()) {
            return AppBatteryTracker.BATTERY_USAGE_NONE;
        }
        synchronized (this.mLock) {
            UidBatteryStates uidBatteryStates = (UidBatteryStates) this.mPkgEvents.get(i, "");
            if (uidBatteryStates == null) {
                return AppBatteryTracker.BATTERY_USAGE_NONE;
            }
            Pair<AppBatteryTracker.ImmutableBatteryUsage, AppBatteryTracker.ImmutableBatteryUsage> batteryUsageSince = uidBatteryStates.getBatteryUsageSince(j, j2, i2);
            if (!((AppBatteryTracker.ImmutableBatteryUsage) batteryUsageSince.second).isEmpty()) {
                return ((AppBatteryTracker.ImmutableBatteryUsage) batteryUsageSince.first).mutate().add(this.mAppRestrictionController.getUidBatteryUsage(i)).subtract((AppBatteryTracker.BatteryUsage) batteryUsageSince.second).unmutate();
            }
            return (AppBatteryTracker.ImmutableBatteryUsage) batteryUsageSince.first;
        }
    }

    /* renamed from: com.android.server.am.AppBatteryExemptionTracker$UidBatteryStates */
    /* loaded from: classes.dex */
    public static final class UidBatteryStates extends BaseAppStateDurations<UidStateEventWithBattery> {
        public UidBatteryStates(int i, String str, BaseAppStateEvents.MaxTrackingDurationConfig maxTrackingDurationConfig) {
            super(i, "", 5, str, maxTrackingDurationConfig);
        }

        public UidBatteryStates(UidBatteryStates uidBatteryStates) {
            super(uidBatteryStates);
        }

        public void addEvent(boolean z, long j, AppBatteryTracker.ImmutableBatteryUsage immutableBatteryUsage, int i) {
            if (z) {
                addEvent(z, new UidStateEventWithBattery(z, j, immutableBatteryUsage, null), i);
                return;
            }
            UidStateEventWithBattery lastEvent = getLastEvent(i);
            if (lastEvent == null || !lastEvent.isStart()) {
                return;
            }
            addEvent(z, new UidStateEventWithBattery(z, j, immutableBatteryUsage.mutate().subtract(lastEvent.getBatteryUsage()).unmutate(), lastEvent), i);
        }

        public UidStateEventWithBattery getLastEvent(int i) {
            LinkedList linkedList = this.mEvents[i];
            if (linkedList != null) {
                return (UidStateEventWithBattery) linkedList.peekLast();
            }
            return null;
        }

        public final Pair<AppBatteryTracker.ImmutableBatteryUsage, AppBatteryTracker.ImmutableBatteryUsage> getBatteryUsageSince(long j, long j2, LinkedList<UidStateEventWithBattery> linkedList) {
            if (linkedList == null || linkedList.size() == 0) {
                AppBatteryTracker.ImmutableBatteryUsage immutableBatteryUsage = AppBatteryTracker.BATTERY_USAGE_NONE;
                return Pair.create(immutableBatteryUsage, immutableBatteryUsage);
            }
            AppBatteryTracker.BatteryUsage batteryUsage = new AppBatteryTracker.BatteryUsage();
            Iterator<UidStateEventWithBattery> it = linkedList.iterator();
            UidStateEventWithBattery uidStateEventWithBattery = null;
            while (it.hasNext()) {
                uidStateEventWithBattery = it.next();
                if (uidStateEventWithBattery.getTimestamp() >= j && !uidStateEventWithBattery.isStart()) {
                    batteryUsage.add(uidStateEventWithBattery.getBatteryUsage(j, Math.min(j2, uidStateEventWithBattery.getTimestamp())));
                    if (j2 <= uidStateEventWithBattery.getTimestamp()) {
                        break;
                    }
                }
            }
            return Pair.create(batteryUsage.unmutate(), uidStateEventWithBattery.isStart() ? uidStateEventWithBattery.getBatteryUsage() : AppBatteryTracker.BATTERY_USAGE_NONE);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Pair<AppBatteryTracker.ImmutableBatteryUsage, AppBatteryTracker.ImmutableBatteryUsage> getBatteryUsageSince(long j, long j2, int i) {
            LinkedList<UidStateEventWithBattery> linkedList = new LinkedList<>();
            for (int i2 = 0; i2 < this.mEvents.length; i2++) {
                if ((BaseAppStateTracker.stateIndexToType(i2) & i) != 0) {
                    linkedList = add(linkedList, this.mEvents[i2]);
                }
            }
            return getBatteryUsageSince(j, j2, linkedList);
        }

        /* JADX WARN: Removed duplicated region for block: B:78:0x0120  */
        /* JADX WARN: Removed duplicated region for block: B:87:0x016c  */
        @Override // com.android.server.p006am.BaseAppStateDurations, com.android.server.p006am.BaseAppStateTimeEvents, com.android.server.p006am.BaseAppStateEvents
        @VisibleForTesting
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public LinkedList<UidStateEventWithBattery> add(LinkedList<UidStateEventWithBattery> linkedList, LinkedList<UidStateEventWithBattery> linkedList2) {
            UidStateEventWithBattery uidStateEventWithBattery;
            long j;
            UidStateEventWithBattery uidStateEventWithBattery2;
            Iterator<UidStateEventWithBattery> it;
            Iterator<UidStateEventWithBattery> it2;
            long j2;
            if (linkedList2 == null || linkedList2.size() == 0) {
                return linkedList;
            }
            if (linkedList == null || linkedList.size() == 0) {
                return (LinkedList) linkedList2.clone();
            }
            Iterator<UidStateEventWithBattery> it3 = linkedList.iterator();
            Iterator<UidStateEventWithBattery> it4 = linkedList2.iterator();
            UidStateEventWithBattery next = it3.next();
            UidStateEventWithBattery next2 = it4.next();
            LinkedList<UidStateEventWithBattery> linkedList3 = new LinkedList<>();
            AppBatteryTracker.BatteryUsage batteryUsage = new AppBatteryTracker.BatteryUsage();
            long timestamp = next.getTimestamp();
            long timestamp2 = next2.getTimestamp();
            boolean z = false;
            boolean z2 = false;
            boolean z3 = false;
            long j3 = 0;
            long j4 = 0;
            while (true) {
                long j5 = Long.MAX_VALUE;
                if (timestamp == Long.MAX_VALUE && timestamp2 == Long.MAX_VALUE) {
                    return linkedList3;
                }
                boolean z4 = z || z2;
                int i = (timestamp > timestamp2 ? 1 : (timestamp == timestamp2 ? 0 : -1));
                if (i == 0) {
                    if (z) {
                        batteryUsage.add(next.getBatteryUsage());
                    }
                    if (z2) {
                        batteryUsage.add(next2.getBatteryUsage());
                    }
                    j3 += (z3 && (z || z2)) ? timestamp - j4 : 0L;
                    z = !z;
                    z2 = !z2;
                    if (it3.hasNext()) {
                        uidStateEventWithBattery2 = it3.next();
                        j2 = uidStateEventWithBattery2.getTimestamp();
                    } else {
                        uidStateEventWithBattery2 = next;
                        j2 = Long.MAX_VALUE;
                    }
                    if (it4.hasNext()) {
                        next2 = it4.next();
                        j5 = next2.getTimestamp();
                    }
                    j = j2;
                } else if (i < 0) {
                    if (z) {
                        batteryUsage.add(next.getBatteryUsage());
                    }
                    j3 += (z3 && z) ? timestamp - j4 : 0L;
                    z = !z;
                    if (it3.hasNext()) {
                        uidStateEventWithBattery2 = it3.next();
                        j5 = uidStateEventWithBattery2.getTimestamp();
                    } else {
                        uidStateEventWithBattery2 = next;
                    }
                    z3 = !z && z2;
                    if (!z || z2) {
                        j4 = next.getTimestamp();
                    }
                    if (z4 == (!z || z2)) {
                        UidStateEventWithBattery uidStateEventWithBattery3 = (UidStateEventWithBattery) next.clone();
                        if (z4) {
                            UidStateEventWithBattery peekLast = linkedList3.peekLast();
                            long timestamp3 = uidStateEventWithBattery3.getTimestamp() - peekLast.getTimestamp();
                            it = it3;
                            it2 = it4;
                            long j6 = timestamp3 + j3;
                            if (j6 != 0) {
                                batteryUsage.scale((timestamp3 * 1.0d) / j6);
                                uidStateEventWithBattery3.update(peekLast, new AppBatteryTracker.ImmutableBatteryUsage(batteryUsage));
                            } else {
                                uidStateEventWithBattery3.update(peekLast, AppBatteryTracker.BATTERY_USAGE_NONE);
                            }
                            batteryUsage.setTo(AppBatteryTracker.BATTERY_USAGE_NONE);
                            j3 = 0;
                        } else {
                            it = it3;
                            it2 = it4;
                        }
                        linkedList3.add(uidStateEventWithBattery3);
                    } else {
                        it = it3;
                        it2 = it4;
                    }
                    it3 = it;
                    next = uidStateEventWithBattery2;
                    it4 = it2;
                    timestamp = j5;
                } else {
                    if (z2) {
                        batteryUsage.add(next2.getBatteryUsage());
                    }
                    j3 += (z3 && z2) ? timestamp2 - j4 : 0L;
                    z2 = !z2;
                    if (it4.hasNext()) {
                        uidStateEventWithBattery = it4.next();
                        j5 = uidStateEventWithBattery.getTimestamp();
                    } else {
                        uidStateEventWithBattery = next2;
                    }
                    j = timestamp;
                    uidStateEventWithBattery2 = next;
                    next = next2;
                    next2 = uidStateEventWithBattery;
                }
                timestamp2 = j5;
                j5 = j;
                if (z) {
                }
                if (!z) {
                }
                j4 = next.getTimestamp();
                if (z4 == (!z || z2)) {
                }
                it3 = it;
                next = uidStateEventWithBattery2;
                it4 = it2;
                timestamp = j5;
            }
        }
    }

    public final void trimDurations() {
        trim(Math.max(0L, SystemClock.elapsedRealtime() - ((AppBatteryExemptionPolicy) this.mInjector.getPolicy()).getMaxTrackingDuration()));
    }

    @Override // com.android.server.p006am.BaseAppStateEventsTracker, com.android.server.p006am.BaseAppStateTracker
    public void dump(PrintWriter printWriter, String str) {
        ((AppBatteryExemptionPolicy) this.mInjector.getPolicy()).dump(printWriter, str);
    }

    /* renamed from: com.android.server.am.AppBatteryExemptionTracker$UidStateEventWithBattery */
    /* loaded from: classes.dex */
    public static final class UidStateEventWithBattery extends BaseAppStateTimeEvents.BaseTimeEvent {
        public AppBatteryTracker.ImmutableBatteryUsage mBatteryUsage;
        public boolean mIsStart;
        public UidStateEventWithBattery mPeer;

        public UidStateEventWithBattery(boolean z, long j, AppBatteryTracker.ImmutableBatteryUsage immutableBatteryUsage, UidStateEventWithBattery uidStateEventWithBattery) {
            super(j);
            this.mIsStart = z;
            this.mBatteryUsage = immutableBatteryUsage;
            this.mPeer = uidStateEventWithBattery;
            if (uidStateEventWithBattery != null) {
                uidStateEventWithBattery.mPeer = this;
            }
        }

        public UidStateEventWithBattery(UidStateEventWithBattery uidStateEventWithBattery) {
            super(uidStateEventWithBattery);
            this.mIsStart = uidStateEventWithBattery.mIsStart;
            this.mBatteryUsage = uidStateEventWithBattery.mBatteryUsage;
        }

        @Override // com.android.server.p006am.BaseAppStateTimeEvents.BaseTimeEvent
        public void trimTo(long j) {
            if (!this.mIsStart || j < this.mTimestamp) {
                return;
            }
            UidStateEventWithBattery uidStateEventWithBattery = this.mPeer;
            if (uidStateEventWithBattery != null) {
                AppBatteryTracker.ImmutableBatteryUsage batteryUsage = uidStateEventWithBattery.getBatteryUsage();
                UidStateEventWithBattery uidStateEventWithBattery2 = this.mPeer;
                uidStateEventWithBattery2.mBatteryUsage = uidStateEventWithBattery2.getBatteryUsage(j, uidStateEventWithBattery2.mTimestamp);
                this.mBatteryUsage = this.mBatteryUsage.mutate().add(batteryUsage).subtract(this.mPeer.mBatteryUsage).unmutate();
            }
            this.mTimestamp = j;
        }

        public void update(UidStateEventWithBattery uidStateEventWithBattery, AppBatteryTracker.ImmutableBatteryUsage immutableBatteryUsage) {
            this.mPeer = uidStateEventWithBattery;
            uidStateEventWithBattery.mPeer = this;
            this.mBatteryUsage = immutableBatteryUsage;
        }

        public boolean isStart() {
            return this.mIsStart;
        }

        public AppBatteryTracker.ImmutableBatteryUsage getBatteryUsage(long j, long j2) {
            if (this.mIsStart || j >= this.mTimestamp || j2 <= j) {
                return AppBatteryTracker.BATTERY_USAGE_NONE;
            }
            long max = Math.max(j, this.mPeer.mTimestamp);
            long min = Math.min(j2, this.mTimestamp);
            long j3 = this.mTimestamp - this.mPeer.mTimestamp;
            long j4 = min - max;
            if (j3 != 0) {
                if (j3 == j4) {
                    return this.mBatteryUsage;
                }
                return this.mBatteryUsage.mutate().scale((j4 * 1.0d) / j3).unmutate();
            }
            return AppBatteryTracker.BATTERY_USAGE_NONE;
        }

        public AppBatteryTracker.ImmutableBatteryUsage getBatteryUsage() {
            return this.mBatteryUsage;
        }

        @Override // com.android.server.p006am.BaseAppStateTimeEvents.BaseTimeEvent
        public Object clone() {
            return new UidStateEventWithBattery(this);
        }

        @Override // com.android.server.p006am.BaseAppStateTimeEvents.BaseTimeEvent
        public boolean equals(Object obj) {
            if (obj != null && obj.getClass() == UidStateEventWithBattery.class) {
                UidStateEventWithBattery uidStateEventWithBattery = (UidStateEventWithBattery) obj;
                return uidStateEventWithBattery.mIsStart == this.mIsStart && uidStateEventWithBattery.mTimestamp == this.mTimestamp && this.mBatteryUsage.equals(uidStateEventWithBattery.mBatteryUsage);
            }
            return false;
        }

        public String toString() {
            return "UidStateEventWithBattery(" + this.mIsStart + ", " + this.mTimestamp + ", " + this.mBatteryUsage + ")";
        }

        @Override // com.android.server.p006am.BaseAppStateTimeEvents.BaseTimeEvent
        public int hashCode() {
            return (((Boolean.hashCode(this.mIsStart) * 31) + Long.hashCode(this.mTimestamp)) * 31) + this.mBatteryUsage.hashCode();
        }
    }

    /* renamed from: com.android.server.am.AppBatteryExemptionTracker$AppBatteryExemptionPolicy */
    /* loaded from: classes.dex */
    public static final class AppBatteryExemptionPolicy extends BaseAppStateEventsTracker.BaseAppStateEventsPolicy<AppBatteryExemptionTracker> {
        public AppBatteryExemptionPolicy(BaseAppStateTracker.Injector injector, AppBatteryExemptionTracker appBatteryExemptionTracker) {
            super(injector, appBatteryExemptionTracker, "bg_battery_exemption_enabled", true, "bg_current_drain_window", appBatteryExemptionTracker.mContext.getResources().getInteger(17694758));
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy
        public void onMaxTrackingDurationChanged(long j) {
            T t = this.mTracker;
            Handler handler = ((AppBatteryExemptionTracker) t).mBgHandler;
            final AppBatteryExemptionTracker appBatteryExemptionTracker = (AppBatteryExemptionTracker) t;
            Objects.requireNonNull(appBatteryExemptionTracker);
            handler.post(new Runnable() { // from class: com.android.server.am.AppBatteryExemptionTracker$AppBatteryExemptionPolicy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AppBatteryExemptionTracker.this.trimDurations();
                }
            });
        }

        @Override // com.android.server.p006am.BaseAppStatePolicy
        public void onTrackerEnabled(boolean z) {
            ((AppBatteryExemptionTracker) this.mTracker).onTrackerEnabled(z);
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy, com.android.server.p006am.BaseAppStatePolicy
        public void dump(PrintWriter printWriter, String str) {
            printWriter.print(str);
            printWriter.println("APP BATTERY EXEMPTION TRACKER POLICY SETTINGS:");
            super.dump(printWriter, "  " + str);
        }
    }
}
