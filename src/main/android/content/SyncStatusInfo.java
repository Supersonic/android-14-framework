package android.content;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import android.util.Pair;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
/* loaded from: classes.dex */
public class SyncStatusInfo implements Parcelable {
    public static final Parcelable.Creator<SyncStatusInfo> CREATOR = new Parcelable.Creator<SyncStatusInfo>() { // from class: android.content.SyncStatusInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SyncStatusInfo createFromParcel(Parcel in) {
            return new SyncStatusInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SyncStatusInfo[] newArray(int size) {
            return new SyncStatusInfo[size];
        }
    };
    private static final int MAX_EVENT_COUNT = 10;
    private static final int SOURCE_COUNT = 6;
    private static final String TAG = "Sync";
    static final int VERSION = 6;
    public final int authorityId;
    public long initialFailureTime;
    public boolean initialize;
    public String lastFailureMesg;
    public int lastFailureSource;
    public long lastFailureTime;
    public int lastSuccessSource;
    public long lastSuccessTime;
    public long lastTodayResetTime;
    private final ArrayList<Long> mLastEventTimes;
    private final ArrayList<String> mLastEvents;
    public boolean pending;
    public final long[] perSourceLastFailureTimes;
    public final long[] perSourceLastSuccessTimes;
    private ArrayList<Long> periodicSyncTimes;
    public final Stats todayStats;
    public final Stats totalStats;
    public final Stats yesterdayStats;

    /* loaded from: classes.dex */
    public static class Stats {
        public int numCancels;
        public int numFailures;
        public int numSourceFeed;
        public int numSourceLocal;
        public int numSourceOther;
        public int numSourcePeriodic;
        public int numSourcePoll;
        public int numSourceUser;
        public int numSyncs;
        public long totalElapsedTime;

        public void copyTo(Stats to) {
            to.totalElapsedTime = this.totalElapsedTime;
            to.numSyncs = this.numSyncs;
            to.numSourcePoll = this.numSourcePoll;
            to.numSourceOther = this.numSourceOther;
            to.numSourceLocal = this.numSourceLocal;
            to.numSourceUser = this.numSourceUser;
            to.numSourcePeriodic = this.numSourcePeriodic;
            to.numSourceFeed = this.numSourceFeed;
            to.numFailures = this.numFailures;
            to.numCancels = this.numCancels;
        }

        public void clear() {
            this.totalElapsedTime = 0L;
            this.numSyncs = 0;
            this.numSourcePoll = 0;
            this.numSourceOther = 0;
            this.numSourceLocal = 0;
            this.numSourceUser = 0;
            this.numSourcePeriodic = 0;
            this.numSourceFeed = 0;
            this.numFailures = 0;
            this.numCancels = 0;
        }

        public void writeToParcel(Parcel parcel) {
            parcel.writeLong(this.totalElapsedTime);
            parcel.writeInt(this.numSyncs);
            parcel.writeInt(this.numSourcePoll);
            parcel.writeInt(this.numSourceOther);
            parcel.writeInt(this.numSourceLocal);
            parcel.writeInt(this.numSourceUser);
            parcel.writeInt(this.numSourcePeriodic);
            parcel.writeInt(this.numSourceFeed);
            parcel.writeInt(this.numFailures);
            parcel.writeInt(this.numCancels);
        }

        public void readFromParcel(Parcel parcel) {
            this.totalElapsedTime = parcel.readLong();
            this.numSyncs = parcel.readInt();
            this.numSourcePoll = parcel.readInt();
            this.numSourceOther = parcel.readInt();
            this.numSourceLocal = parcel.readInt();
            this.numSourceUser = parcel.readInt();
            this.numSourcePeriodic = parcel.readInt();
            this.numSourceFeed = parcel.readInt();
            this.numFailures = parcel.readInt();
            this.numCancels = parcel.readInt();
        }
    }

    public SyncStatusInfo(int authorityId) {
        this.totalStats = new Stats();
        this.todayStats = new Stats();
        this.yesterdayStats = new Stats();
        this.perSourceLastSuccessTimes = new long[6];
        this.perSourceLastFailureTimes = new long[6];
        this.mLastEventTimes = new ArrayList<>();
        this.mLastEvents = new ArrayList<>();
        this.authorityId = authorityId;
    }

    public int getLastFailureMesgAsInt(int def) {
        int i = ContentResolver.syncErrorStringToInt(this.lastFailureMesg);
        if (i > 0) {
            return i;
        }
        Log.m112d(TAG, "Unknown lastFailureMesg:" + this.lastFailureMesg);
        return def;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(6);
        parcel.writeInt(this.authorityId);
        parcel.writeLong(this.totalStats.totalElapsedTime);
        parcel.writeInt(this.totalStats.numSyncs);
        parcel.writeInt(this.totalStats.numSourcePoll);
        parcel.writeInt(this.totalStats.numSourceOther);
        parcel.writeInt(this.totalStats.numSourceLocal);
        parcel.writeInt(this.totalStats.numSourceUser);
        parcel.writeLong(this.lastSuccessTime);
        parcel.writeInt(this.lastSuccessSource);
        parcel.writeLong(this.lastFailureTime);
        parcel.writeInt(this.lastFailureSource);
        parcel.writeString(this.lastFailureMesg);
        parcel.writeLong(this.initialFailureTime);
        parcel.writeInt(this.pending ? 1 : 0);
        parcel.writeInt(this.initialize ? 1 : 0);
        ArrayList<Long> arrayList = this.periodicSyncTimes;
        if (arrayList != null) {
            parcel.writeInt(arrayList.size());
            Iterator<Long> it = this.periodicSyncTimes.iterator();
            while (it.hasNext()) {
                long periodicSyncTime = it.next().longValue();
                parcel.writeLong(periodicSyncTime);
            }
        } else {
            parcel.writeInt(-1);
        }
        parcel.writeInt(this.mLastEventTimes.size());
        for (int i = 0; i < this.mLastEventTimes.size(); i++) {
            parcel.writeLong(this.mLastEventTimes.get(i).longValue());
            parcel.writeString(this.mLastEvents.get(i));
        }
        parcel.writeInt(this.totalStats.numSourcePeriodic);
        parcel.writeInt(this.totalStats.numSourceFeed);
        parcel.writeInt(this.totalStats.numFailures);
        parcel.writeInt(this.totalStats.numCancels);
        parcel.writeLong(this.lastTodayResetTime);
        this.todayStats.writeToParcel(parcel);
        this.yesterdayStats.writeToParcel(parcel);
        parcel.writeLongArray(this.perSourceLastSuccessTimes);
        parcel.writeLongArray(this.perSourceLastFailureTimes);
    }

    public SyncStatusInfo(Parcel parcel) {
        Stats stats = new Stats();
        this.totalStats = stats;
        this.todayStats = new Stats();
        this.yesterdayStats = new Stats();
        this.perSourceLastSuccessTimes = new long[6];
        this.perSourceLastFailureTimes = new long[6];
        this.mLastEventTimes = new ArrayList<>();
        this.mLastEvents = new ArrayList<>();
        int version = parcel.readInt();
        if (version != 6 && version != 1) {
            Log.m104w("SyncStatusInfo", "Unknown version: " + version);
        }
        this.authorityId = parcel.readInt();
        stats.totalElapsedTime = parcel.readLong();
        stats.numSyncs = parcel.readInt();
        stats.numSourcePoll = parcel.readInt();
        stats.numSourceOther = parcel.readInt();
        stats.numSourceLocal = parcel.readInt();
        stats.numSourceUser = parcel.readInt();
        this.lastSuccessTime = parcel.readLong();
        this.lastSuccessSource = parcel.readInt();
        this.lastFailureTime = parcel.readLong();
        this.lastFailureSource = parcel.readInt();
        this.lastFailureMesg = parcel.readString();
        this.initialFailureTime = parcel.readLong();
        this.pending = parcel.readInt() != 0;
        this.initialize = parcel.readInt() != 0;
        if (version == 1) {
            this.periodicSyncTimes = null;
        } else {
            int count = parcel.readInt();
            if (count < 0) {
                this.periodicSyncTimes = null;
            } else {
                this.periodicSyncTimes = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    this.periodicSyncTimes.add(Long.valueOf(parcel.readLong()));
                }
            }
            if (version >= 3) {
                this.mLastEventTimes.clear();
                this.mLastEvents.clear();
                int nEvents = parcel.readInt();
                for (int i2 = 0; i2 < nEvents; i2++) {
                    this.mLastEventTimes.add(Long.valueOf(parcel.readLong()));
                    this.mLastEvents.add(parcel.readString());
                }
            }
        }
        if (version < 4) {
            Stats stats2 = this.totalStats;
            stats2.numSourcePeriodic = (((stats2.numSyncs - this.totalStats.numSourceLocal) - this.totalStats.numSourcePoll) - this.totalStats.numSourceOther) - this.totalStats.numSourceUser;
            if (this.totalStats.numSourcePeriodic < 0) {
                this.totalStats.numSourcePeriodic = 0;
            }
        } else {
            this.totalStats.numSourcePeriodic = parcel.readInt();
        }
        if (version >= 5) {
            this.totalStats.numSourceFeed = parcel.readInt();
            this.totalStats.numFailures = parcel.readInt();
            this.totalStats.numCancels = parcel.readInt();
            this.lastTodayResetTime = parcel.readLong();
            this.todayStats.readFromParcel(parcel);
            this.yesterdayStats.readFromParcel(parcel);
        }
        if (version >= 6) {
            parcel.readLongArray(this.perSourceLastSuccessTimes);
            parcel.readLongArray(this.perSourceLastFailureTimes);
        }
    }

    public SyncStatusInfo(SyncStatusInfo other) {
        this.totalStats = new Stats();
        this.todayStats = new Stats();
        this.yesterdayStats = new Stats();
        this.perSourceLastSuccessTimes = new long[6];
        this.perSourceLastFailureTimes = new long[6];
        this.mLastEventTimes = new ArrayList<>();
        this.mLastEvents = new ArrayList<>();
        this.authorityId = other.authorityId;
        copyFrom(other);
    }

    public SyncStatusInfo(int authorityId, SyncStatusInfo other) {
        this.totalStats = new Stats();
        this.todayStats = new Stats();
        this.yesterdayStats = new Stats();
        this.perSourceLastSuccessTimes = new long[6];
        this.perSourceLastFailureTimes = new long[6];
        this.mLastEventTimes = new ArrayList<>();
        this.mLastEvents = new ArrayList<>();
        this.authorityId = authorityId;
        copyFrom(other);
    }

    private void copyFrom(SyncStatusInfo other) {
        other.totalStats.copyTo(this.totalStats);
        other.todayStats.copyTo(this.todayStats);
        other.yesterdayStats.copyTo(this.yesterdayStats);
        this.lastTodayResetTime = other.lastTodayResetTime;
        this.lastSuccessTime = other.lastSuccessTime;
        this.lastSuccessSource = other.lastSuccessSource;
        this.lastFailureTime = other.lastFailureTime;
        this.lastFailureSource = other.lastFailureSource;
        this.lastFailureMesg = other.lastFailureMesg;
        this.initialFailureTime = other.initialFailureTime;
        this.pending = other.pending;
        this.initialize = other.initialize;
        if (other.periodicSyncTimes != null) {
            this.periodicSyncTimes = new ArrayList<>(other.periodicSyncTimes);
        }
        this.mLastEventTimes.addAll(other.mLastEventTimes);
        this.mLastEvents.addAll(other.mLastEvents);
        copy(this.perSourceLastSuccessTimes, other.perSourceLastSuccessTimes);
        copy(this.perSourceLastFailureTimes, other.perSourceLastFailureTimes);
    }

    private static void copy(long[] to, long[] from) {
        System.arraycopy(from, 0, to, 0, to.length);
    }

    public int getPeriodicSyncTimesSize() {
        ArrayList<Long> arrayList = this.periodicSyncTimes;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    public void addPeriodicSyncTime(long time) {
        this.periodicSyncTimes = ArrayUtils.add(this.periodicSyncTimes, Long.valueOf(time));
    }

    public void setPeriodicSyncTime(int index, long when) {
        ensurePeriodicSyncTimeSize(index);
        this.periodicSyncTimes.set(index, Long.valueOf(when));
    }

    public long getPeriodicSyncTime(int index) {
        ArrayList<Long> arrayList = this.periodicSyncTimes;
        if (arrayList != null && index < arrayList.size()) {
            return this.periodicSyncTimes.get(index).longValue();
        }
        return 0L;
    }

    public void removePeriodicSyncTime(int index) {
        ArrayList<Long> arrayList = this.periodicSyncTimes;
        if (arrayList != null && index < arrayList.size()) {
            this.periodicSyncTimes.remove(index);
        }
    }

    public void populateLastEventsInformation(ArrayList<Pair<Long, String>> lastEventInformation) {
        this.mLastEventTimes.clear();
        this.mLastEvents.clear();
        int size = lastEventInformation.size();
        for (int i = 0; i < size; i++) {
            Pair<Long, String> lastEventInfo = lastEventInformation.get(i);
            this.mLastEventTimes.add(lastEventInfo.first);
            this.mLastEvents.add(lastEventInfo.second);
        }
    }

    public void addEvent(String message) {
        if (this.mLastEventTimes.size() >= 10) {
            this.mLastEventTimes.remove(9);
            this.mLastEvents.remove(9);
        }
        this.mLastEventTimes.add(0, Long.valueOf(System.currentTimeMillis()));
        this.mLastEvents.add(0, message);
    }

    public int getEventCount() {
        return this.mLastEventTimes.size();
    }

    public long getEventTime(int i) {
        return this.mLastEventTimes.get(i).longValue();
    }

    public String getEvent(int i) {
        return this.mLastEvents.get(i);
    }

    public void setLastSuccess(int source, long lastSyncTime) {
        this.lastSuccessTime = lastSyncTime;
        this.lastSuccessSource = source;
        this.lastFailureTime = 0L;
        this.lastFailureSource = -1;
        this.lastFailureMesg = null;
        this.initialFailureTime = 0L;
        if (source >= 0) {
            long[] jArr = this.perSourceLastSuccessTimes;
            if (source < jArr.length) {
                jArr[source] = lastSyncTime;
            }
        }
    }

    public void setLastFailure(int source, long lastSyncTime, String failureMessage) {
        this.lastFailureTime = lastSyncTime;
        this.lastFailureSource = source;
        this.lastFailureMesg = failureMessage;
        if (this.initialFailureTime == 0) {
            this.initialFailureTime = lastSyncTime;
        }
        if (source >= 0) {
            long[] jArr = this.perSourceLastFailureTimes;
            if (source < jArr.length) {
                jArr[source] = lastSyncTime;
            }
        }
    }

    private void ensurePeriodicSyncTimeSize(int index) {
        if (this.periodicSyncTimes == null) {
            this.periodicSyncTimes = new ArrayList<>(0);
        }
        int requiredSize = index + 1;
        if (this.periodicSyncTimes.size() < requiredSize) {
            for (int i = this.periodicSyncTimes.size(); i < requiredSize; i++) {
                this.periodicSyncTimes.add(0L);
            }
        }
    }

    public void maybeResetTodayStats(boolean clockValid, boolean force) {
        long now = System.currentTimeMillis();
        if (!force) {
            if (areSameDates(now, this.lastTodayResetTime)) {
                return;
            }
            if (now < this.lastTodayResetTime && !clockValid) {
                return;
            }
        }
        this.lastTodayResetTime = now;
        this.todayStats.copyTo(this.yesterdayStats);
        this.todayStats.clear();
    }

    private static boolean areSameDates(long time1, long time2) {
        Calendar c1 = new GregorianCalendar();
        Calendar c2 = new GregorianCalendar();
        c1.setTimeInMillis(time1);
        c2.setTimeInMillis(time2);
        return c1.get(1) == c2.get(1) && c1.get(6) == c2.get(6);
    }
}
