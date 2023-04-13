package com.android.server.location.eventlog;

import android.location.LocationRequest;
import android.location.provider.ProviderRequest;
import android.location.util.identity.CallerIdentity;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.server.location.LocationManagerService;
import com.android.server.location.eventlog.LocalEventLog;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class LocationEventLog extends LocalEventLog<Object> {
    public static final LocationEventLog EVENT_LOG = new LocationEventLog();
    @GuardedBy({"mAggregateStats"})
    public final ArrayMap<String, ArrayMap<CallerIdentity, AggregateStats>> mAggregateStats;
    @GuardedBy({"this"})
    public final LocationsEventLog mLocationsLog;

    public static int getLogSize() {
        return LocationManagerService.f1147D ? 600 : 300;
    }

    public static int getLocationsLogSize() {
        return LocationManagerService.f1147D ? 200 : 100;
    }

    public LocationEventLog() {
        super(getLogSize(), Object.class);
        this.mAggregateStats = new ArrayMap<>(4);
        this.mLocationsLog = new LocationsEventLog(getLocationsLogSize());
    }

    public ArrayMap<String, ArrayMap<CallerIdentity, AggregateStats>> copyAggregateStats() {
        ArrayMap<String, ArrayMap<CallerIdentity, AggregateStats>> arrayMap;
        synchronized (this.mAggregateStats) {
            arrayMap = new ArrayMap<>(this.mAggregateStats);
            for (int i = 0; i < arrayMap.size(); i++) {
                arrayMap.setValueAt(i, new ArrayMap<>(arrayMap.valueAt(i)));
            }
        }
        return arrayMap;
    }

    public final AggregateStats getAggregateStats(String str, CallerIdentity callerIdentity) {
        AggregateStats aggregateStats;
        synchronized (this.mAggregateStats) {
            ArrayMap<CallerIdentity, AggregateStats> arrayMap = this.mAggregateStats.get(str);
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>(2);
                this.mAggregateStats.put(str, arrayMap);
            }
            CallerIdentity forAggregation = CallerIdentity.forAggregation(callerIdentity);
            aggregateStats = arrayMap.get(forAggregation);
            if (aggregateStats == null) {
                aggregateStats = new AggregateStats();
                arrayMap.put(forAggregation, aggregateStats);
            }
        }
        return aggregateStats;
    }

    public void logUserSwitched(int i, int i2) {
        addLog(new UserSwitchedEvent(i, i2));
    }

    public void logUserVisibilityChanged(int i, boolean z) {
        addLog(new UserVisibilityChangedEvent(i, z));
    }

    public void logLocationEnabled(int i, boolean z) {
        addLog(new LocationEnabledEvent(i, z));
    }

    public void logAdasLocationEnabled(int i, boolean z) {
        addLog(new LocationAdasEnabledEvent(i, z));
    }

    public void logProviderEnabled(String str, int i, boolean z) {
        addLog(new ProviderEnabledEvent(str, i, z));
    }

    public void logProviderMocked(String str, boolean z) {
        addLog(new ProviderMockedEvent(str, z));
    }

    public void logProviderClientRegistered(String str, CallerIdentity callerIdentity, LocationRequest locationRequest) {
        addLog(new ProviderClientRegisterEvent(str, true, callerIdentity, locationRequest));
        getAggregateStats(str, callerIdentity).markRequestAdded(locationRequest.getIntervalMillis());
    }

    public void logProviderClientUnregistered(String str, CallerIdentity callerIdentity) {
        addLog(new ProviderClientRegisterEvent(str, false, callerIdentity, null));
        getAggregateStats(str, callerIdentity).markRequestRemoved();
    }

    public void logProviderClientActive(String str, CallerIdentity callerIdentity) {
        getAggregateStats(str, callerIdentity).markRequestActive();
    }

    public void logProviderClientInactive(String str, CallerIdentity callerIdentity) {
        getAggregateStats(str, callerIdentity).markRequestInactive();
    }

    public void logProviderClientForeground(String str, CallerIdentity callerIdentity) {
        if (LocationManagerService.f1147D) {
            addLog(new ProviderClientForegroundEvent(str, true, callerIdentity));
        }
        getAggregateStats(str, callerIdentity).markRequestForeground();
    }

    public void logProviderClientBackground(String str, CallerIdentity callerIdentity) {
        if (LocationManagerService.f1147D) {
            addLog(new ProviderClientForegroundEvent(str, false, callerIdentity));
        }
        getAggregateStats(str, callerIdentity).markRequestBackground();
    }

    public void logProviderClientPermitted(String str, CallerIdentity callerIdentity) {
        if (LocationManagerService.f1147D) {
            addLog(new ProviderClientPermittedEvent(str, true, callerIdentity));
        }
    }

    public void logProviderClientUnpermitted(String str, CallerIdentity callerIdentity) {
        if (LocationManagerService.f1147D) {
            addLog(new ProviderClientPermittedEvent(str, false, callerIdentity));
        }
    }

    public void logProviderUpdateRequest(String str, ProviderRequest providerRequest) {
        addLog(new ProviderUpdateEvent(str, providerRequest));
    }

    public void logProviderReceivedLocations(String str, int i) {
        synchronized (this) {
            this.mLocationsLog.logProviderReceivedLocations(str, i);
        }
    }

    public void logProviderDeliveredLocations(String str, int i, CallerIdentity callerIdentity) {
        synchronized (this) {
            this.mLocationsLog.logProviderDeliveredLocations(str, i, callerIdentity);
        }
        getAggregateStats(str, callerIdentity).markLocationDelivered();
    }

    public void logProviderStationaryThrottled(String str, boolean z, ProviderRequest providerRequest) {
        addLog(new ProviderStationaryThrottledEvent(str, z, providerRequest));
    }

    public void logLocationPowerSaveMode(int i) {
        addLog(new LocationPowerSaveModeEvent(i));
    }

    public final void addLog(Object obj) {
        addLog(SystemClock.elapsedRealtime(), obj);
    }

    public synchronized void iterate(LocalEventLog.LogConsumer<? super Object> logConsumer) {
        LocalEventLog.iterate(logConsumer, this, this.mLocationsLog);
    }

    public void iterate(Consumer<String> consumer) {
        iterate(consumer, (String) null);
    }

    public void iterate(final Consumer<String> consumer, final String str) {
        final long currentTimeMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        final StringBuilder sb = new StringBuilder();
        iterate(new LocalEventLog.LogConsumer() { // from class: com.android.server.location.eventlog.LocationEventLog$$ExternalSyntheticLambda0
            @Override // com.android.server.location.eventlog.LocalEventLog.LogConsumer
            public final void acceptLog(long j, Object obj) {
                LocationEventLog.lambda$iterate$0(str, sb, currentTimeMillis, consumer, j, obj);
            }
        });
    }

    public static /* synthetic */ void lambda$iterate$0(String str, StringBuilder sb, long j, Consumer consumer, long j2, Object obj) {
        if (str == null || ((obj instanceof ProviderEvent) && str.equals(((ProviderEvent) obj).mProvider))) {
            sb.setLength(0);
            sb.append(TimeUtils.logTimeOfDay(j2 + j));
            sb.append(": ");
            sb.append(obj);
            consumer.accept(sb.toString());
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ProviderEvent {
        public final String mProvider;

        public ProviderEvent(String str) {
            this.mProvider = str;
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderEnabledEvent extends ProviderEvent {
        public final boolean mEnabled;
        public final int mUserId;

        public ProviderEnabledEvent(String str, int i, boolean z) {
            super(str);
            this.mUserId = i;
            this.mEnabled = z;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mProvider);
            sb.append(" provider [u");
            sb.append(this.mUserId);
            sb.append("] ");
            sb.append(this.mEnabled ? "enabled" : "disabled");
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderMockedEvent extends ProviderEvent {
        public final boolean mMocked;

        public ProviderMockedEvent(String str, boolean z) {
            super(str);
            this.mMocked = z;
        }

        public String toString() {
            if (this.mMocked) {
                return this.mProvider + " provider added mock provider override";
            }
            return this.mProvider + " provider removed mock provider override";
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderClientRegisterEvent extends ProviderEvent {
        public final CallerIdentity mIdentity;
        public final LocationRequest mLocationRequest;
        public final boolean mRegistered;

        public ProviderClientRegisterEvent(String str, boolean z, CallerIdentity callerIdentity, LocationRequest locationRequest) {
            super(str);
            this.mRegistered = z;
            this.mIdentity = callerIdentity;
            this.mLocationRequest = locationRequest;
        }

        public String toString() {
            if (this.mRegistered) {
                return this.mProvider + " provider +registration " + this.mIdentity + " -> " + this.mLocationRequest;
            }
            return this.mProvider + " provider -registration " + this.mIdentity;
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderClientForegroundEvent extends ProviderEvent {
        public final boolean mForeground;
        public final CallerIdentity mIdentity;

        public ProviderClientForegroundEvent(String str, boolean z, CallerIdentity callerIdentity) {
            super(str);
            this.mForeground = z;
            this.mIdentity = callerIdentity;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mProvider);
            sb.append(" provider client ");
            sb.append(this.mIdentity);
            sb.append(" -> ");
            sb.append(this.mForeground ? "foreground" : "background");
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderClientPermittedEvent extends ProviderEvent {
        public final CallerIdentity mIdentity;
        public final boolean mPermitted;

        public ProviderClientPermittedEvent(String str, boolean z, CallerIdentity callerIdentity) {
            super(str);
            this.mPermitted = z;
            this.mIdentity = callerIdentity;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mProvider);
            sb.append(" provider client ");
            sb.append(this.mIdentity);
            sb.append(" -> ");
            sb.append(this.mPermitted ? "permitted" : "unpermitted");
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderUpdateEvent extends ProviderEvent {
        public final ProviderRequest mRequest;

        public ProviderUpdateEvent(String str, ProviderRequest providerRequest) {
            super(str);
            this.mRequest = providerRequest;
        }

        public String toString() {
            return this.mProvider + " provider request = " + this.mRequest;
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderReceiveLocationEvent extends ProviderEvent {
        public final int mNumLocations;

        public ProviderReceiveLocationEvent(String str, int i) {
            super(str);
            this.mNumLocations = i;
        }

        public String toString() {
            return this.mProvider + " provider received location[" + this.mNumLocations + "]";
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderDeliverLocationEvent extends ProviderEvent {
        public final CallerIdentity mIdentity;
        public final int mNumLocations;

        public ProviderDeliverLocationEvent(String str, int i, CallerIdentity callerIdentity) {
            super(str);
            this.mNumLocations = i;
            this.mIdentity = callerIdentity;
        }

        public String toString() {
            return this.mProvider + " provider delivered location[" + this.mNumLocations + "] to " + this.mIdentity;
        }
    }

    /* loaded from: classes.dex */
    public static final class ProviderStationaryThrottledEvent extends ProviderEvent {
        public final ProviderRequest mRequest;
        public final boolean mStationaryThrottled;

        public ProviderStationaryThrottledEvent(String str, boolean z, ProviderRequest providerRequest) {
            super(str);
            this.mStationaryThrottled = z;
            this.mRequest = providerRequest;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mProvider);
            sb.append(" provider stationary/idle ");
            sb.append(this.mStationaryThrottled ? "throttled" : "unthrottled");
            sb.append(", request = ");
            sb.append(this.mRequest);
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static final class LocationPowerSaveModeEvent {
        public final int mLocationPowerSaveMode;

        public LocationPowerSaveModeEvent(int i) {
            this.mLocationPowerSaveMode = i;
        }

        public String toString() {
            int i = this.mLocationPowerSaveMode;
            String str = i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? "UNKNOWN" : "THROTTLE_REQUESTS_WHEN_SCREEN_OFF" : "FOREGROUND_ONLY" : "ALL_DISABLED_WHEN_SCREEN_OFF" : "GPS_DISABLED_WHEN_SCREEN_OFF" : "NO_CHANGE";
            return "location power save mode changed to " + str;
        }
    }

    /* loaded from: classes.dex */
    public static final class UserSwitchedEvent {
        public final int mUserIdFrom;
        public final int mUserIdTo;

        public UserSwitchedEvent(int i, int i2) {
            this.mUserIdFrom = i;
            this.mUserIdTo = i2;
        }

        public String toString() {
            return "current user switched from u" + this.mUserIdFrom + " to u" + this.mUserIdTo;
        }
    }

    /* loaded from: classes.dex */
    public static final class UserVisibilityChangedEvent {
        public final int mUserId;
        public final boolean mVisible;

        public UserVisibilityChangedEvent(int i, boolean z) {
            this.mUserId = i;
            this.mVisible = z;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[u");
            sb.append(this.mUserId);
            sb.append("] ");
            sb.append(this.mVisible ? "visible" : "invisible");
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static final class LocationEnabledEvent {
        public final boolean mEnabled;
        public final int mUserId;

        public LocationEnabledEvent(int i, boolean z) {
            this.mUserId = i;
            this.mEnabled = z;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("location [u");
            sb.append(this.mUserId);
            sb.append("] ");
            sb.append(this.mEnabled ? "enabled" : "disabled");
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static final class LocationAdasEnabledEvent {
        public final boolean mEnabled;
        public final int mUserId;

        public LocationAdasEnabledEvent(int i, boolean z) {
            this.mUserId = i;
            this.mEnabled = z;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("adas location [u");
            sb.append(this.mUserId);
            sb.append("] ");
            sb.append(this.mEnabled ? "enabled" : "disabled");
            return sb.toString();
        }
    }

    /* loaded from: classes.dex */
    public static final class LocationsEventLog extends LocalEventLog<Object> {
        public LocationsEventLog(int i) {
            super(i, Object.class);
        }

        public void logProviderReceivedLocations(String str, int i) {
            addLog(new ProviderReceiveLocationEvent(str, i));
        }

        public void logProviderDeliveredLocations(String str, int i, CallerIdentity callerIdentity) {
            addLog(new ProviderDeliverLocationEvent(str, i, callerIdentity));
        }

        public final void addLog(Object obj) {
            addLog(SystemClock.elapsedRealtime(), obj);
        }
    }

    /* loaded from: classes.dex */
    public static final class AggregateStats {
        @GuardedBy({"this"})
        public int mActiveRequestCount;
        @GuardedBy({"this"})
        public long mActiveTimeLastUpdateRealtimeMs;
        @GuardedBy({"this"})
        public long mActiveTimeTotalMs;
        @GuardedBy({"this"})
        public int mAddedRequestCount;
        @GuardedBy({"this"})
        public long mAddedTimeLastUpdateRealtimeMs;
        @GuardedBy({"this"})
        public long mAddedTimeTotalMs;
        @GuardedBy({"this"})
        public int mDeliveredLocationCount;
        @GuardedBy({"this"})
        public int mForegroundRequestCount;
        @GuardedBy({"this"})
        public long mForegroundTimeLastUpdateRealtimeMs;
        @GuardedBy({"this"})
        public long mForegroundTimeTotalMs;
        @GuardedBy({"this"})
        public long mFastestIntervalMs = Long.MAX_VALUE;
        @GuardedBy({"this"})
        public long mSlowestIntervalMs = 0;

        public synchronized void markRequestAdded(long j) {
            int i = this.mAddedRequestCount;
            this.mAddedRequestCount = i + 1;
            if (i == 0) {
                this.mAddedTimeLastUpdateRealtimeMs = SystemClock.elapsedRealtime();
            }
            this.mFastestIntervalMs = Math.min(j, this.mFastestIntervalMs);
            this.mSlowestIntervalMs = Math.max(j, this.mSlowestIntervalMs);
        }

        public synchronized void markRequestRemoved() {
            updateTotals();
            boolean z = true;
            int i = this.mAddedRequestCount - 1;
            this.mAddedRequestCount = i;
            if (i < 0) {
                z = false;
            }
            Preconditions.checkState(z);
            this.mActiveRequestCount = Math.min(this.mAddedRequestCount, this.mActiveRequestCount);
            this.mForegroundRequestCount = Math.min(this.mAddedRequestCount, this.mForegroundRequestCount);
        }

        public synchronized void markRequestActive() {
            Preconditions.checkState(this.mAddedRequestCount > 0);
            int i = this.mActiveRequestCount;
            this.mActiveRequestCount = i + 1;
            if (i == 0) {
                this.mActiveTimeLastUpdateRealtimeMs = SystemClock.elapsedRealtime();
            }
        }

        public synchronized void markRequestInactive() {
            updateTotals();
            boolean z = true;
            int i = this.mActiveRequestCount - 1;
            this.mActiveRequestCount = i;
            if (i < 0) {
                z = false;
            }
            Preconditions.checkState(z);
        }

        public synchronized void markRequestForeground() {
            Preconditions.checkState(this.mAddedRequestCount > 0);
            int i = this.mForegroundRequestCount;
            this.mForegroundRequestCount = i + 1;
            if (i == 0) {
                this.mForegroundTimeLastUpdateRealtimeMs = SystemClock.elapsedRealtime();
            }
        }

        public synchronized void markRequestBackground() {
            updateTotals();
            boolean z = true;
            int i = this.mForegroundRequestCount - 1;
            this.mForegroundRequestCount = i;
            if (i < 0) {
                z = false;
            }
            Preconditions.checkState(z);
        }

        public synchronized void markLocationDelivered() {
            this.mDeliveredLocationCount++;
        }

        public synchronized void updateTotals() {
            if (this.mAddedRequestCount > 0) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                this.mAddedTimeTotalMs += elapsedRealtime - this.mAddedTimeLastUpdateRealtimeMs;
                this.mAddedTimeLastUpdateRealtimeMs = elapsedRealtime;
            }
            if (this.mActiveRequestCount > 0) {
                long elapsedRealtime2 = SystemClock.elapsedRealtime();
                this.mActiveTimeTotalMs += elapsedRealtime2 - this.mActiveTimeLastUpdateRealtimeMs;
                this.mActiveTimeLastUpdateRealtimeMs = elapsedRealtime2;
            }
            if (this.mForegroundRequestCount > 0) {
                long elapsedRealtime3 = SystemClock.elapsedRealtime();
                this.mForegroundTimeTotalMs += elapsedRealtime3 - this.mForegroundTimeLastUpdateRealtimeMs;
                this.mForegroundTimeLastUpdateRealtimeMs = elapsedRealtime3;
            }
        }

        public synchronized String toString() {
            return "min/max interval = " + intervalToString(this.mFastestIntervalMs) + "/" + intervalToString(this.mSlowestIntervalMs) + ", total/active/foreground duration = " + TimeUtils.formatDuration(this.mAddedTimeTotalMs) + "/" + TimeUtils.formatDuration(this.mActiveTimeTotalMs) + "/" + TimeUtils.formatDuration(this.mForegroundTimeTotalMs) + ", locations = " + this.mDeliveredLocationCount;
        }

        public static String intervalToString(long j) {
            if (j == Long.MAX_VALUE) {
                return "passive";
            }
            return TimeUnit.MILLISECONDS.toSeconds(j) + "s";
        }
    }
}
