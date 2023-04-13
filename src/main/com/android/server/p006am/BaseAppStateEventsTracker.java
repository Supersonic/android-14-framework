package com.android.server.p006am;

import android.content.Context;
import android.os.PowerExemptionManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p006am.BaseAppStateEvents;
import com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy;
import com.android.server.p006am.BaseAppStateTracker;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
/* renamed from: com.android.server.am.BaseAppStateEventsTracker */
/* loaded from: classes.dex */
public abstract class BaseAppStateEventsTracker<T extends BaseAppStateEventsPolicy, U extends BaseAppStateEvents> extends BaseAppStateTracker<T> implements BaseAppStateEvents.Factory<U> {
    @GuardedBy({"mLock"})
    public final UidProcessMap<U> mPkgEvents;
    @GuardedBy({"mLock"})
    public final ArraySet<Integer> mTopUids;

    public void dumpOthers(PrintWriter printWriter, String str) {
    }

    @GuardedBy({"mLock"})
    public void onUntrackingUidLocked(int i) {
    }

    public BaseAppStateEventsTracker(Context context, AppRestrictionController appRestrictionController, Constructor<? extends BaseAppStateTracker.Injector<T>> constructor, Object obj) {
        super(context, appRestrictionController, constructor, obj);
        this.mPkgEvents = new UidProcessMap<>();
        this.mTopUids = new ArraySet<>();
    }

    @VisibleForTesting
    public void reset() {
        synchronized (this.mLock) {
            this.mPkgEvents.clear();
            this.mTopUids.clear();
        }
    }

    @GuardedBy({"mLock"})
    public U getUidEventsLocked(int i) {
        ArrayMap<String, U> arrayMap = this.mPkgEvents.getMap().get(i);
        U u = null;
        if (arrayMap == null) {
            return null;
        }
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            U valueAt = arrayMap.valueAt(size);
            if (valueAt != null) {
                if (u == null) {
                    u = (U) createAppStateEvents(i, valueAt.mPackageName);
                }
                u.add(valueAt);
            }
        }
        return u;
    }

    public void trim(long j) {
        synchronized (this.mLock) {
            trimLocked(j);
        }
    }

    @GuardedBy({"mLock"})
    public void trimLocked(long j) {
        SparseArray<ArrayMap<String, U>> map = this.mPkgEvents.getMap();
        for (int size = map.size() - 1; size >= 0; size--) {
            ArrayMap<String, U> valueAt = map.valueAt(size);
            for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                U valueAt2 = valueAt.valueAt(size2);
                valueAt2.trim(j);
                if (valueAt2.isEmpty()) {
                    valueAt.removeAt(size2);
                }
            }
            if (valueAt.size() == 0) {
                map.removeAt(size);
            }
        }
    }

    public boolean isUidOnTop(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = this.mTopUids.contains(Integer.valueOf(i));
        }
        return contains;
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public void onUidProcStateChanged(int i, int i2) {
        synchronized (this.mLock) {
            if (this.mPkgEvents.getMap().indexOfKey(i) < 0) {
                return;
            }
            onUidProcStateChangedUncheckedLocked(i, i2);
        }
    }

    @GuardedBy({"mLock"})
    public void onUidProcStateChangedUncheckedLocked(int i, int i2) {
        if (i2 < 4) {
            this.mTopUids.add(Integer.valueOf(i));
        } else {
            this.mTopUids.remove(Integer.valueOf(i));
        }
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public void onUidGone(int i) {
        synchronized (this.mLock) {
            this.mTopUids.remove(Integer.valueOf(i));
        }
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public void onUidRemoved(int i) {
        synchronized (this.mLock) {
            this.mPkgEvents.getMap().remove(i);
            onUntrackingUidLocked(i);
        }
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public void onUserRemoved(int i) {
        synchronized (this.mLock) {
            SparseArray<ArrayMap<String, U>> map = this.mPkgEvents.getMap();
            for (int size = map.size() - 1; size >= 0; size--) {
                int keyAt = map.keyAt(size);
                if (UserHandle.getUserId(keyAt) == i) {
                    map.removeAt(size);
                    onUntrackingUidLocked(keyAt);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.p006am.BaseAppStateTracker
    public void dump(PrintWriter printWriter, String str) {
        BaseAppStateEventsPolicy baseAppStateEventsPolicy = (BaseAppStateEventsPolicy) this.mInjector.getPolicy();
        synchronized (this.mLock) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            SparseArray<ArrayMap<String, U>> map = this.mPkgEvents.getMap();
            for (int size = map.size() - 1; size >= 0; size--) {
                int keyAt = map.keyAt(size);
                ArrayMap<String, U> valueAt = map.valueAt(size);
                for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                    String keyAt2 = valueAt.keyAt(size2);
                    U valueAt2 = valueAt.valueAt(size2);
                    dumpEventHeaderLocked(printWriter, str, keyAt2, keyAt, valueAt2, baseAppStateEventsPolicy);
                    dumpEventLocked(printWriter, str, valueAt2, elapsedRealtime);
                }
            }
        }
        dumpOthers(printWriter, str);
        baseAppStateEventsPolicy.dump(printWriter, str);
    }

    @GuardedBy({"mLock"})
    public void dumpEventHeaderLocked(PrintWriter printWriter, String str, String str2, int i, U u, T t) {
        printWriter.print(str);
        printWriter.print("* ");
        printWriter.print(str2);
        printWriter.print('/');
        printWriter.print(UserHandle.formatUid(i));
        printWriter.print(" exemption=");
        printWriter.println(t.getExemptionReasonString(str2, i, u.mExemptReason));
    }

    @GuardedBy({"mLock"})
    public void dumpEventLocked(PrintWriter printWriter, String str, U u, long j) {
        u.dump(printWriter, "  " + str, j);
    }

    /* renamed from: com.android.server.am.BaseAppStateEventsTracker$BaseAppStateEventsPolicy */
    /* loaded from: classes.dex */
    public static abstract class BaseAppStateEventsPolicy<V extends BaseAppStateEventsTracker> extends BaseAppStatePolicy<V> implements BaseAppStateEvents.MaxTrackingDurationConfig {
        public final long mDefaultMaxTrackingDuration;
        public final String mKeyMaxTrackingDuration;
        public volatile long mMaxTrackingDuration;

        public abstract void onMaxTrackingDurationChanged(long j);

        public BaseAppStateEventsPolicy(BaseAppStateTracker.Injector<?> injector, V v, String str, boolean z, String str2, long j) {
            super(injector, v, str, z);
            this.mKeyMaxTrackingDuration = str2;
            this.mDefaultMaxTrackingDuration = j;
        }

        @Override // com.android.server.p006am.BaseAppStatePolicy
        public void onPropertiesChanged(String str) {
            if (this.mKeyMaxTrackingDuration.equals(str)) {
                updateMaxTrackingDuration();
            } else {
                super.onPropertiesChanged(str);
            }
        }

        @Override // com.android.server.p006am.BaseAppStatePolicy
        public void onSystemReady() {
            super.onSystemReady();
            updateMaxTrackingDuration();
        }

        public void updateMaxTrackingDuration() {
            long j = DeviceConfig.getLong("activity_manager", this.mKeyMaxTrackingDuration, this.mDefaultMaxTrackingDuration);
            if (j != this.mMaxTrackingDuration) {
                this.mMaxTrackingDuration = j;
                onMaxTrackingDurationChanged(j);
            }
        }

        @Override // com.android.server.p006am.BaseAppStateEvents.MaxTrackingDurationConfig
        public long getMaxTrackingDuration() {
            return this.mMaxTrackingDuration;
        }

        public String getExemptionReasonString(String str, int i, int i2) {
            return PowerExemptionManager.reasonCodeToString(i2);
        }

        @Override // com.android.server.p006am.BaseAppStatePolicy
        public void dump(PrintWriter printWriter, String str) {
            super.dump(printWriter, str);
            if (isEnabled()) {
                printWriter.print(str);
                printWriter.print(this.mKeyMaxTrackingDuration);
                printWriter.print('=');
                printWriter.println(this.mMaxTrackingDuration);
            }
        }
    }
}
