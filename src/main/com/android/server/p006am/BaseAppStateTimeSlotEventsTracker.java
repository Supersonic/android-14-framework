package com.android.server.p006am;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.DeviceConfig;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ProcessMap;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.p006am.BaseAppStateEvents;
import com.android.server.p006am.BaseAppStateEventsTracker;
import com.android.server.p006am.BaseAppStateTimeSlotEventsTracker.BaseAppStateTimeSlotEventsPolicy;
import com.android.server.p006am.BaseAppStateTimeSlotEventsTracker.SimpleAppStateTimeslotEvents;
import com.android.server.p006am.BaseAppStateTracker;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.Objects;
/* renamed from: com.android.server.am.BaseAppStateTimeSlotEventsTracker */
/* loaded from: classes.dex */
public abstract class BaseAppStateTimeSlotEventsTracker<T extends BaseAppStateTimeSlotEventsPolicy, U extends SimpleAppStateTimeslotEvents> extends BaseAppStateEventsTracker<T, U> {
    public HandlerC0380H mHandler;
    public final ArrayMap<U, Integer> mTmpPkgs;

    public BaseAppStateTimeSlotEventsTracker(Context context, AppRestrictionController appRestrictionController, Constructor<? extends BaseAppStateTracker.Injector<T>> constructor, Object obj) {
        super(context, appRestrictionController, constructor, obj);
        this.mTmpPkgs = new ArrayMap<>();
        this.mHandler = new HandlerC0380H(this);
    }

    public void onNewEvent(String str, int i) {
        this.mHandler.obtainMessage(0, i, 0, str).sendToTarget();
    }

    public void handleNewEvent(String str, int i) {
        int totalEvents;
        boolean z;
        if (((BaseAppStateTimeSlotEventsPolicy) this.mInjector.getPolicy()).shouldExempt(str, i) != -1) {
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        synchronized (this.mLock) {
            SimpleAppStateTimeslotEvents simpleAppStateTimeslotEvents = (SimpleAppStateTimeslotEvents) this.mPkgEvents.get(i, str);
            if (simpleAppStateTimeslotEvents == null) {
                simpleAppStateTimeslotEvents = (SimpleAppStateTimeslotEvents) createAppStateEvents(i, str);
                this.mPkgEvents.put(i, str, simpleAppStateTimeslotEvents);
            }
            simpleAppStateTimeslotEvents.addEvent(elapsedRealtime, 0);
            totalEvents = simpleAppStateTimeslotEvents.getTotalEvents(elapsedRealtime, 0);
            z = totalEvents >= ((BaseAppStateTimeSlotEventsPolicy) this.mInjector.getPolicy()).getNumOfEventsThreshold();
        }
        if (z) {
            ((BaseAppStateTimeSlotEventsPolicy) this.mInjector.getPolicy()).onExcessiveEvents(str, i, totalEvents, elapsedRealtime);
        }
    }

    public void onMonitorEnabled(boolean z) {
        if (z) {
            return;
        }
        synchronized (this.mLock) {
            this.mPkgEvents.clear();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onNumOfEventsThresholdChanged(int i) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        synchronized (this.mLock) {
            SparseArray map = this.mPkgEvents.getMap();
            for (int size = map.size() - 1; size >= 0; size--) {
                ArrayMap arrayMap = (ArrayMap) map.valueAt(size);
                for (int size2 = arrayMap.size() - 1; size2 >= 0; size2--) {
                    SimpleAppStateTimeslotEvents simpleAppStateTimeslotEvents = (SimpleAppStateTimeslotEvents) arrayMap.valueAt(size2);
                    int totalEvents = simpleAppStateTimeslotEvents.getTotalEvents(elapsedRealtime, 0);
                    if (totalEvents >= i) {
                        this.mTmpPkgs.put(simpleAppStateTimeslotEvents, Integer.valueOf(totalEvents));
                    }
                }
            }
        }
        for (int size3 = this.mTmpPkgs.size() - 1; size3 >= 0; size3--) {
            U keyAt = this.mTmpPkgs.keyAt(size3);
            ((BaseAppStateTimeSlotEventsPolicy) this.mInjector.getPolicy()).onExcessiveEvents(keyAt.mPackageName, keyAt.mUid, this.mTmpPkgs.valueAt(size3).intValue(), elapsedRealtime);
        }
        this.mTmpPkgs.clear();
    }

    @GuardedBy({"mLock"})
    public int getTotalEventsLocked(int i, long j) {
        SimpleAppStateTimeslotEvents simpleAppStateTimeslotEvents = (SimpleAppStateTimeslotEvents) getUidEventsLocked(i);
        if (simpleAppStateTimeslotEvents == null) {
            return 0;
        }
        return simpleAppStateTimeslotEvents.getTotalEvents(j, 0);
    }

    public final void trimEvents() {
        trim(Math.max(0L, SystemClock.elapsedRealtime() - ((BaseAppStateTimeSlotEventsPolicy) this.mInjector.getPolicy()).getMaxTrackingDuration()));
    }

    @Override // com.android.server.p006am.BaseAppStateTracker
    public void onUserInteractionStarted(String str, int i) {
        ((BaseAppStateTimeSlotEventsPolicy) this.mInjector.getPolicy()).onUserInteractionStarted(str, i);
    }

    /* renamed from: com.android.server.am.BaseAppStateTimeSlotEventsTracker$H */
    /* loaded from: classes.dex */
    public static class HandlerC0380H extends Handler {
        public final BaseAppStateTimeSlotEventsTracker mTracker;

        public HandlerC0380H(BaseAppStateTimeSlotEventsTracker baseAppStateTimeSlotEventsTracker) {
            super(baseAppStateTimeSlotEventsTracker.mBgHandler.getLooper());
            this.mTracker = baseAppStateTimeSlotEventsTracker;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 0) {
                return;
            }
            this.mTracker.handleNewEvent((String) message.obj, message.arg1);
        }
    }

    /* renamed from: com.android.server.am.BaseAppStateTimeSlotEventsTracker$BaseAppStateTimeSlotEventsPolicy */
    /* loaded from: classes.dex */
    public static class BaseAppStateTimeSlotEventsPolicy<E extends BaseAppStateTimeSlotEventsTracker> extends BaseAppStateEventsTracker.BaseAppStateEventsPolicy<E> {
        public final int mDefaultNumOfEventsThreshold;
        @GuardedBy({"mLock"})
        public final ProcessMap<Long> mExcessiveEventPkgs;
        public final String mKeyNumOfEventsThreshold;
        public final Object mLock;
        public volatile int mNumOfEventsThreshold;
        public long mTimeSlotSize;

        public BaseAppStateTimeSlotEventsPolicy(BaseAppStateTracker.Injector injector, E e, String str, boolean z, String str2, long j, String str3, int i) {
            super(injector, e, str, z, str2, j);
            this.mExcessiveEventPkgs = new ProcessMap<>();
            this.mTimeSlotSize = 900000L;
            this.mKeyNumOfEventsThreshold = str3;
            this.mDefaultNumOfEventsThreshold = i;
            this.mLock = e.mLock;
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy, com.android.server.p006am.BaseAppStatePolicy
        public void onSystemReady() {
            super.onSystemReady();
            updateNumOfEventsThreshold();
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy, com.android.server.p006am.BaseAppStatePolicy
        public void onPropertiesChanged(String str) {
            if (this.mKeyNumOfEventsThreshold.equals(str)) {
                updateNumOfEventsThreshold();
            } else {
                super.onPropertiesChanged(str);
            }
        }

        @Override // com.android.server.p006am.BaseAppStatePolicy
        public void onTrackerEnabled(boolean z) {
            ((BaseAppStateTimeSlotEventsTracker) this.mTracker).onMonitorEnabled(z);
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy
        public void onMaxTrackingDurationChanged(long j) {
            T t = this.mTracker;
            Handler handler = ((BaseAppStateTimeSlotEventsTracker) t).mBgHandler;
            final BaseAppStateTimeSlotEventsTracker baseAppStateTimeSlotEventsTracker = (BaseAppStateTimeSlotEventsTracker) t;
            Objects.requireNonNull(baseAppStateTimeSlotEventsTracker);
            handler.post(new Runnable() { // from class: com.android.server.am.BaseAppStateTimeSlotEventsTracker$BaseAppStateTimeSlotEventsPolicy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BaseAppStateTimeSlotEventsTracker.this.trimEvents();
                }
            });
        }

        public final void updateNumOfEventsThreshold() {
            int i = DeviceConfig.getInt("activity_manager", this.mKeyNumOfEventsThreshold, this.mDefaultNumOfEventsThreshold);
            if (i != this.mNumOfEventsThreshold) {
                this.mNumOfEventsThreshold = i;
                ((BaseAppStateTimeSlotEventsTracker) this.mTracker).onNumOfEventsThresholdChanged(i);
            }
        }

        public int getNumOfEventsThreshold() {
            return this.mNumOfEventsThreshold;
        }

        public long getTimeSlotSize() {
            return this.mTimeSlotSize;
        }

        @VisibleForTesting
        public void setTimeSlotSize(long j) {
            this.mTimeSlotSize = j;
        }

        public void onExcessiveEvents(String str, int i, int i2, long j) {
            boolean z;
            synchronized (this.mLock) {
                if (((Long) this.mExcessiveEventPkgs.get(str, i)) == null) {
                    this.mExcessiveEventPkgs.put(str, i, Long.valueOf(j));
                    z = true;
                } else {
                    z = false;
                }
            }
            if (z) {
                ((BaseAppStateTimeSlotEventsTracker) this.mTracker).mAppRestrictionController.refreshAppRestrictionLevelForUid(i, FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM, 2, true);
            }
        }

        public int shouldExempt(String str, int i) {
            if (((BaseAppStateTimeSlotEventsTracker) this.mTracker).isUidOnTop(i)) {
                return 12;
            }
            if (((BaseAppStateTimeSlotEventsTracker) this.mTracker).mAppRestrictionController.hasForegroundServices(str, i)) {
                return 14;
            }
            int shouldExemptUid = shouldExemptUid(i);
            if (shouldExemptUid != -1) {
                return shouldExemptUid;
            }
            return -1;
        }

        /* JADX WARN: Removed duplicated region for block: B:12:0x0021 A[Catch: all -> 0x002a, DONT_GENERATE, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x000f, B:12:0x0021, B:15:0x0025, B:17:0x0027), top: B:23:0x0003 }] */
        /* JADX WARN: Removed duplicated region for block: B:14:0x0023  */
        @Override // com.android.server.p006am.BaseAppStatePolicy
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int getProposedRestrictionLevel(String str, int i, int i2) {
            int i3;
            synchronized (this.mLock) {
                if (this.mExcessiveEventPkgs.get(str, i) != null && ((BaseAppStateTimeSlotEventsTracker) this.mTracker).mAppRestrictionController.isAutoRestrictAbusiveAppEnabled()) {
                    i3 = 40;
                    return i2 <= 40 ? i3 : i2 == 40 ? 30 : 0;
                }
                i3 = 30;
                if (i2 <= 40) {
                }
            }
        }

        public void onUserInteractionStarted(String str, int i) {
            synchronized (this.mLock) {
                this.mExcessiveEventPkgs.remove(str, i);
            }
            ((BaseAppStateTimeSlotEventsTracker) this.mTracker).mAppRestrictionController.refreshAppRestrictionLevelForUid(i, FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_USAGE, 3, true);
        }

        @Override // com.android.server.p006am.BaseAppStateEventsTracker.BaseAppStateEventsPolicy, com.android.server.p006am.BaseAppStatePolicy
        public void dump(PrintWriter printWriter, String str) {
            super.dump(printWriter, str);
            if (isEnabled()) {
                printWriter.print(str);
                printWriter.print(this.mKeyNumOfEventsThreshold);
                printWriter.print('=');
                printWriter.println(this.mDefaultNumOfEventsThreshold);
            }
            printWriter.print(str);
            printWriter.print("event_time_slot_size=");
            printWriter.println(getTimeSlotSize());
        }
    }

    /* renamed from: com.android.server.am.BaseAppStateTimeSlotEventsTracker$SimpleAppStateTimeslotEvents */
    /* loaded from: classes.dex */
    public static class SimpleAppStateTimeslotEvents extends BaseAppStateTimeSlotEvents {
        @Override // com.android.server.p006am.BaseAppStateEvents
        public String formatEventTypeLabel(int i) {
            return "";
        }

        public SimpleAppStateTimeslotEvents(int i, String str, long j, String str2, BaseAppStateEvents.MaxTrackingDurationConfig maxTrackingDurationConfig) {
            super(i, str, 1, j, str2, maxTrackingDurationConfig);
        }

        @Override // com.android.server.p006am.BaseAppStateEvents
        public String formatEventSummary(long j, int i) {
            LinkedList linkedList = this.mEvents[0];
            if (linkedList == null || linkedList.size() == 0) {
                return "(none)";
            }
            int totalEvents = getTotalEvents(j, 0);
            return "total=" + totalEvents + ", latest=" + getTotalEventsSince(this.mCurSlotStartTime[0], j, 0) + "(slot=" + TimeUtils.formatTime(this.mCurSlotStartTime[0], j) + ")";
        }
    }
}
