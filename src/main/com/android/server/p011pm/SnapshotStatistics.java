package com.android.server.p011pm;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.EventLogTags;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
/* renamed from: com.android.server.pm.SnapshotStatistics */
/* loaded from: classes2.dex */
public class SnapshotStatistics {
    public static final long SNAPSHOT_LOG_INTERVAL_US = TimeUnit.DAYS.toMicros(1);
    public Handler mHandler;
    public long mLastLogTimeUs;
    public Stats[] mLong;
    public int mPackageCount;
    public Stats[] mShort;
    public final Object mLock = new Object();
    public int mEventsReported = 0;
    public final BinMap mTimeBins = new BinMap(new int[]{1, 2, 5, 10, 20, 50, 100});
    public final BinMap mUseBins = new BinMap(new int[]{1, 10, 100, 1000, FrameworkStatsLog.WIFI_BYTES_TRANSFER});

    /* renamed from: com.android.server.pm.SnapshotStatistics$BinMap */
    /* loaded from: classes2.dex */
    public static class BinMap {
        public int mCount;
        public int mMaxBin;
        public int[] mUserKey;

        public BinMap(int[] iArr) {
            int[] copyOf = Arrays.copyOf(iArr, iArr.length);
            this.mUserKey = copyOf;
            this.mCount = copyOf.length + 1;
            this.mMaxBin = copyOf[copyOf.length - 1] + 1;
        }

        public int getBin(int i) {
            if (i < 0 || i >= this.mMaxBin) {
                if (i >= this.mMaxBin) {
                    return this.mUserKey.length;
                }
                return 0;
            }
            int i2 = 0;
            while (true) {
                int[] iArr = this.mUserKey;
                if (i2 >= iArr.length) {
                    return 0;
                }
                if (i <= iArr[i2]) {
                    return i2;
                }
                i2++;
            }
        }

        public int count() {
            return this.mCount;
        }

        public int[] userKeys() {
            return this.mUserKey;
        }
    }

    /* renamed from: com.android.server.pm.SnapshotStatistics$Stats */
    /* loaded from: classes2.dex */
    public class Stats {
        public int mBigBuilds;
        public int mMaxBuildTimeUs;
        public int mMaxUsedCount;
        public int mShortLived;
        public long mStartTimeUs;
        public long mStopTimeUs;
        public int[] mTimes;
        public int mTotalBuilds;
        public long mTotalTimeUs;
        public int mTotalUsed;
        public int[] mUsed;

        public final void rebuild(int i, int i2, int i3, int i4, boolean z, boolean z2) {
            this.mTotalBuilds++;
            int[] iArr = this.mTimes;
            iArr[i3] = iArr[i3] + 1;
            if (i2 >= 0) {
                this.mTotalUsed += i2;
                int[] iArr2 = this.mUsed;
                iArr2[i4] = iArr2[i4] + 1;
            }
            this.mTotalTimeUs += i;
            if (z) {
                this.mBigBuilds++;
            }
            if (z2) {
                this.mShortLived++;
            }
            if (this.mMaxBuildTimeUs < i) {
                this.mMaxBuildTimeUs = i;
            }
            if (this.mMaxUsedCount < i2) {
                this.mMaxUsedCount = i2;
            }
        }

        public Stats(long j) {
            this.mStopTimeUs = 0L;
            this.mTotalBuilds = 0;
            this.mTotalUsed = 0;
            this.mBigBuilds = 0;
            this.mShortLived = 0;
            this.mTotalTimeUs = 0L;
            this.mMaxBuildTimeUs = 0;
            this.mMaxUsedCount = 0;
            this.mStartTimeUs = j;
            this.mTimes = new int[SnapshotStatistics.this.mTimeBins.count()];
            this.mUsed = new int[SnapshotStatistics.this.mUseBins.count()];
        }

        public Stats(Stats stats) {
            this.mStartTimeUs = 0L;
            this.mStopTimeUs = 0L;
            this.mTotalBuilds = 0;
            this.mTotalUsed = 0;
            this.mBigBuilds = 0;
            this.mShortLived = 0;
            this.mTotalTimeUs = 0L;
            this.mMaxBuildTimeUs = 0;
            this.mMaxUsedCount = 0;
            this.mStartTimeUs = stats.mStartTimeUs;
            this.mStopTimeUs = stats.mStopTimeUs;
            int[] iArr = stats.mTimes;
            this.mTimes = Arrays.copyOf(iArr, iArr.length);
            int[] iArr2 = stats.mUsed;
            this.mUsed = Arrays.copyOf(iArr2, iArr2.length);
            this.mTotalBuilds = stats.mTotalBuilds;
            this.mTotalUsed = stats.mTotalUsed;
            this.mBigBuilds = stats.mBigBuilds;
            this.mShortLived = stats.mShortLived;
            this.mTotalTimeUs = stats.mTotalTimeUs;
            this.mMaxBuildTimeUs = stats.mMaxBuildTimeUs;
            this.mMaxUsedCount = stats.mMaxUsedCount;
        }

        public final void complete(long j) {
            this.mStopTimeUs = j;
        }

        public final String durationToString(long j) {
            int i = (int) (j / 1000000);
            int i2 = i / 60;
            int i3 = i % 60;
            int i4 = i2 / 60;
            int i5 = i2 % 60;
            int i6 = i4 / 24;
            int i7 = i4 % 24;
            if (i6 != 0) {
                return TextUtils.formatSimple("%2d:%02d:%02d:%02d", new Object[]{Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i5), Integer.valueOf(i3)});
            }
            if (i7 != 0) {
                return TextUtils.formatSimple("%2s %02d:%02d:%02d", new Object[]{"", Integer.valueOf(i7), Integer.valueOf(i5), Integer.valueOf(i3)});
            }
            return TextUtils.formatSimple("%2s %2s %2d:%02d", new Object[]{"", "", Integer.valueOf(i5), Integer.valueOf(i3)});
        }

        public final void dumpPrefix(PrintWriter printWriter, String str, long j, boolean z, String str2) {
            printWriter.print(str + " ");
            if (z) {
                printWriter.format(Locale.US, "%-23s", str2);
                return;
            }
            Locale locale = Locale.US;
            printWriter.format(locale, "%11s", durationToString(j - this.mStartTimeUs));
            long j2 = this.mStopTimeUs;
            if (j2 != 0) {
                printWriter.format(locale, " %11s", durationToString(j - j2));
            } else {
                printWriter.format(locale, " %11s", "now");
            }
        }

        public final void dumpStats(PrintWriter printWriter, String str, long j, boolean z) {
            dumpPrefix(printWriter, str, j, z, "Summary stats");
            if (z) {
                printWriter.format(Locale.US, "  %10s  %10s  %10s  %10s  %10s  %10s", "TotBlds", "TotUsed", "BigBlds", "ShortLvd", "TotTime", "MaxTime");
            } else {
                printWriter.format(Locale.US, "  %10d  %10d  %10d  %10d  %10d  %10d", Integer.valueOf(this.mTotalBuilds), Integer.valueOf(this.mTotalUsed), Integer.valueOf(this.mBigBuilds), Integer.valueOf(this.mShortLived), Long.valueOf(this.mTotalTimeUs / 1000), Integer.valueOf(this.mMaxBuildTimeUs / 1000));
            }
            printWriter.println();
        }

        public final void dumpTimes(PrintWriter printWriter, String str, long j, boolean z) {
            dumpPrefix(printWriter, str, j, z, "Build times");
            int i = 0;
            if (!z) {
                while (true) {
                    int[] iArr = this.mTimes;
                    if (i >= iArr.length) {
                        break;
                    }
                    printWriter.format(Locale.US, "  %10d", Integer.valueOf(iArr[i]));
                    i++;
                }
            } else {
                int[] userKeys = SnapshotStatistics.this.mTimeBins.userKeys();
                while (i < userKeys.length) {
                    printWriter.format(Locale.US, "  %10s", TextUtils.formatSimple("<= %dms", new Object[]{Integer.valueOf(userKeys[i])}));
                    i++;
                }
                printWriter.format(Locale.US, "  %10s", TextUtils.formatSimple("> %dms", new Object[]{Integer.valueOf(userKeys[userKeys.length - 1])}));
            }
            printWriter.println();
        }

        public final void dumpUsage(PrintWriter printWriter, String str, long j, boolean z) {
            dumpPrefix(printWriter, str, j, z, "Use counters");
            int i = 0;
            if (!z) {
                while (true) {
                    int[] iArr = this.mUsed;
                    if (i >= iArr.length) {
                        break;
                    }
                    printWriter.format(Locale.US, "  %10d", Integer.valueOf(iArr[i]));
                    i++;
                }
            } else {
                int[] userKeys = SnapshotStatistics.this.mUseBins.userKeys();
                while (i < userKeys.length) {
                    printWriter.format(Locale.US, "  %10s", TextUtils.formatSimple("<= %d", new Object[]{Integer.valueOf(userKeys[i])}));
                    i++;
                }
                printWriter.format(Locale.US, "  %10s", TextUtils.formatSimple("> %d", new Object[]{Integer.valueOf(userKeys[userKeys.length - 1])}));
            }
            printWriter.println();
        }

        public final void dump(PrintWriter printWriter, String str, long j, boolean z, String str2) {
            if (str2.equals("stats")) {
                dumpStats(printWriter, str, j, z);
            } else if (str2.equals("times")) {
                dumpTimes(printWriter, str, j, z);
            } else if (str2.equals("usage")) {
                dumpUsage(printWriter, str, j, z);
            } else {
                throw new IllegalArgumentException("unrecognized choice: " + str2);
            }
        }

        public final void logSnapshotStatistics(int i) {
            int i2 = this.mTotalBuilds;
            FrameworkStatsLog.write((int) FrameworkStatsLog.PACKAGE_MANAGER_SNAPSHOT_REPORTED, this.mTimes, this.mUsed, this.mMaxBuildTimeUs, this.mMaxUsedCount, i2 == 0 ? 0L : this.mTotalTimeUs / i2, i2 == 0 ? 0 : this.mTotalUsed / i2, i);
        }
    }

    public SnapshotStatistics() {
        this.mHandler = null;
        long currentTimeMicro = SystemClock.currentTimeMicro();
        Stats[] statsArr = new Stats[2];
        this.mLong = statsArr;
        statsArr[0] = new Stats(currentTimeMicro);
        Stats[] statsArr2 = new Stats[10];
        this.mShort = statsArr2;
        statsArr2[0] = new Stats(currentTimeMicro);
        this.mLastLogTimeUs = currentTimeMicro;
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.server.pm.SnapshotStatistics.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                SnapshotStatistics.this.handleMessage(message);
            }
        };
        scheduleTick();
    }

    public final void handleMessage(Message message) {
        tick();
        scheduleTick();
    }

    public final void scheduleTick() {
        this.mHandler.sendEmptyMessageDelayed(0, 60000L);
    }

    public final void rebuild(long j, long j2, int i, int i2) {
        boolean z;
        int i3 = (int) (j2 - j);
        synchronized (this.mLock) {
            this.mPackageCount = i2;
            int bin = this.mTimeBins.getBin(i3 / 1000);
            int bin2 = this.mUseBins.getBin(i);
            z = true;
            boolean z2 = i3 >= 10000;
            boolean z3 = i <= 5;
            this.mShort[0].rebuild(i3, i, bin, bin2, z2, z3);
            this.mLong[0].rebuild(i3, i, bin, bin2, z2, z3);
            if (i3 >= 30000) {
                int i4 = this.mEventsReported;
                this.mEventsReported = i4 + 1;
                if (i4 < 10) {
                }
            }
            z = false;
        }
        if (z) {
            EventLogTags.writePmSnapshotRebuild(i3 / 1000, i);
        }
    }

    @GuardedBy({"mLock"})
    public final void shift(Stats[] statsArr, long j) {
        statsArr[0].complete(j);
        for (int length = statsArr.length - 1; length > 0; length--) {
            statsArr[length] = statsArr[length - 1];
        }
        statsArr[0] = new Stats(j);
    }

    public final void tick() {
        synchronized (this.mLock) {
            long currentTimeMicro = SystemClock.currentTimeMicro();
            if (currentTimeMicro - this.mLastLogTimeUs > SNAPSHOT_LOG_INTERVAL_US) {
                shift(this.mLong, currentTimeMicro);
                this.mLastLogTimeUs = currentTimeMicro;
                Stats[] statsArr = this.mLong;
                statsArr[statsArr.length - 1].logSnapshotStatistics(this.mPackageCount);
            }
            shift(this.mShort, currentTimeMicro);
            this.mEventsReported = 0;
        }
    }

    public final void dump(PrintWriter printWriter, String str, long j, Stats[] statsArr, Stats[] statsArr2, String str2) {
        statsArr[0].dump(printWriter, str, j, true, str2);
        for (Stats stats : statsArr2) {
            if (stats != null) {
                stats.dump(printWriter, str, j, false, str2);
            }
        }
        for (Stats stats2 : statsArr) {
            if (stats2 != null) {
                stats2.dump(printWriter, str, j, false, str2);
            }
        }
    }

    public void dump(PrintWriter printWriter, String str, long j, int i, boolean z) {
        Stats[] statsArr;
        Stats[] statsArr2;
        synchronized (this.mLock) {
            Stats[] statsArr3 = this.mLong;
            statsArr = (Stats[]) Arrays.copyOf(statsArr3, statsArr3.length);
            statsArr[0] = new Stats(statsArr[0]);
            Stats[] statsArr4 = this.mShort;
            statsArr2 = (Stats[]) Arrays.copyOf(statsArr4, statsArr4.length);
            statsArr2[0] = new Stats(statsArr2[0]);
        }
        printWriter.format(Locale.US, "%s Unrecorded-hits: %d", str, Integer.valueOf(i));
        printWriter.println();
        dump(printWriter, str, j, statsArr, statsArr2, "stats");
        if (z) {
            return;
        }
        printWriter.println();
        dump(printWriter, str, j, statsArr, statsArr2, "times");
        printWriter.println();
        dump(printWriter, str, j, statsArr, statsArr2, "usage");
    }
}
