package com.android.server.power.batterysaver;

import android.os.BatteryManagerInternal;
import android.os.SystemClock;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import java.text.SimpleDateFormat;
import java.util.Date;
/* loaded from: classes2.dex */
public class BatterySavingStats {
    public final Object mLock;
    @GuardedBy({"mLock"})
    public int mCurrentState = -1;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    final SparseArray<Stat> mStats = new SparseArray<>();
    @GuardedBy({"mLock"})
    public int mBatterySaverEnabledCount = 0;
    @GuardedBy({"mLock"})
    public long mLastBatterySaverEnabledTime = 0;
    @GuardedBy({"mLock"})
    public long mLastBatterySaverDisabledTime = 0;
    @GuardedBy({"mLock"})
    public int mAdaptiveBatterySaverEnabledCount = 0;
    @GuardedBy({"mLock"})
    public long mLastAdaptiveBatterySaverEnabledTime = 0;
    @GuardedBy({"mLock"})
    public long mLastAdaptiveBatterySaverDisabledTime = 0;
    public BatteryManagerInternal mBatteryManagerInternal = (BatteryManagerInternal) LocalServices.getService(BatteryManagerInternal.class);

    /* loaded from: classes2.dex */
    public interface BatterySaverState {
        static int fromIndex(int i) {
            return (i >> 0) & 3;
        }
    }

    /* loaded from: classes2.dex */
    public interface DozeState {
        static int fromIndex(int i) {
            return (i >> 3) & 3;
        }
    }

    /* loaded from: classes2.dex */
    public interface InteractiveState {
        static int fromIndex(int i) {
            return (i >> 2) & 1;
        }
    }

    /* loaded from: classes2.dex */
    public interface PlugState {
        static int fromIndex(int i) {
            return (i >> 5) & 1;
        }
    }

    @VisibleForTesting
    public static int statesToIndex(int i, int i2, int i3, int i4) {
        return (i & 3) | ((i2 & 1) << 2) | ((i3 & 3) << 3) | ((i4 & 1) << 5);
    }

    /* loaded from: classes2.dex */
    public static class Stat {
        public int endBatteryLevel;
        public int endBatteryPercent;
        public long endTime;
        public int startBatteryLevel;
        public int startBatteryPercent;
        public long startTime;
        public int totalBatteryDrain;
        public int totalBatteryDrainPercent;
        public long totalTimeMillis;

        public long totalMinutes() {
            return this.totalTimeMillis / 60000;
        }

        public double drainPerHour() {
            long j = this.totalTimeMillis;
            if (j == 0) {
                return 0.0d;
            }
            return this.totalBatteryDrain / (j / 3600000.0d);
        }

        public double drainPercentPerHour() {
            long j = this.totalTimeMillis;
            if (j == 0) {
                return 0.0d;
            }
            return this.totalBatteryDrainPercent / (j / 3600000.0d);
        }

        @VisibleForTesting
        public String toStringForTest() {
            return "{" + totalMinutes() + "m," + this.totalBatteryDrain + "," + String.format("%.2f", Double.valueOf(drainPerHour())) + "uA/H," + String.format("%.2f", Double.valueOf(drainPercentPerHour())) + "%}";
        }
    }

    @VisibleForTesting
    public BatterySavingStats(Object obj) {
        this.mLock = obj;
    }

    public final BatteryManagerInternal getBatteryManagerInternal() {
        if (this.mBatteryManagerInternal == null) {
            BatteryManagerInternal batteryManagerInternal = (BatteryManagerInternal) LocalServices.getService(BatteryManagerInternal.class);
            this.mBatteryManagerInternal = batteryManagerInternal;
            if (batteryManagerInternal == null) {
                Slog.wtf("BatterySavingStats", "BatteryManagerInternal not initialized");
            }
        }
        return this.mBatteryManagerInternal;
    }

    @VisibleForTesting
    public static String stateToString(int i) {
        if (i != -1) {
            return "BS=" + BatterySaverState.fromIndex(i) + ",I=" + InteractiveState.fromIndex(i) + ",D=" + DozeState.fromIndex(i) + ",P=" + PlugState.fromIndex(i);
        }
        return "NotInitialized";
    }

    @VisibleForTesting
    public Stat getStat(int i) {
        Stat stat;
        synchronized (this.mLock) {
            stat = this.mStats.get(i);
            if (stat == null) {
                stat = new Stat();
                this.mStats.put(i, stat);
            }
        }
        return stat;
    }

    public final Stat getStat(int i, int i2, int i3, int i4) {
        return getStat(statesToIndex(i, i2, i3, i4));
    }

    @VisibleForTesting
    public long injectCurrentTime() {
        return SystemClock.elapsedRealtime();
    }

    @VisibleForTesting
    public int injectBatteryLevel() {
        BatteryManagerInternal batteryManagerInternal = getBatteryManagerInternal();
        if (batteryManagerInternal == null) {
            return 0;
        }
        return batteryManagerInternal.getBatteryChargeCounter();
    }

    @VisibleForTesting
    public int injectBatteryPercent() {
        BatteryManagerInternal batteryManagerInternal = getBatteryManagerInternal();
        if (batteryManagerInternal == null) {
            return 0;
        }
        return batteryManagerInternal.getBatteryLevel();
    }

    public void transitionState(int i, int i2, int i3, int i4) {
        synchronized (this.mLock) {
            transitionStateLocked(statesToIndex(i, i2, i3, i4));
        }
    }

    @GuardedBy({"mLock"})
    public final void transitionStateLocked(int i) {
        if (this.mCurrentState == i) {
            return;
        }
        long injectCurrentTime = injectCurrentTime();
        int injectBatteryLevel = injectBatteryLevel();
        int injectBatteryPercent = injectBatteryPercent();
        int i2 = this.mCurrentState;
        int fromIndex = i2 < 0 ? 0 : BatterySaverState.fromIndex(i2);
        int fromIndex2 = i >= 0 ? BatterySaverState.fromIndex(i) : 0;
        if (fromIndex != fromIndex2) {
            if (fromIndex2 != 0) {
                if (fromIndex2 == 1) {
                    this.mBatterySaverEnabledCount++;
                    this.mLastBatterySaverEnabledTime = injectCurrentTime;
                    if (fromIndex == 2) {
                        this.mLastAdaptiveBatterySaverDisabledTime = injectCurrentTime;
                    }
                } else if (fromIndex2 == 2) {
                    this.mAdaptiveBatterySaverEnabledCount++;
                    this.mLastAdaptiveBatterySaverEnabledTime = injectCurrentTime;
                    if (fromIndex == 1) {
                        this.mLastBatterySaverDisabledTime = injectCurrentTime;
                    }
                }
            } else if (fromIndex == 1) {
                this.mLastBatterySaverDisabledTime = injectCurrentTime;
            } else {
                this.mLastAdaptiveBatterySaverDisabledTime = injectCurrentTime;
            }
        }
        endLastStateLocked(injectCurrentTime, injectBatteryLevel, injectBatteryPercent);
        startNewStateLocked(i, injectCurrentTime, injectBatteryLevel, injectBatteryPercent);
    }

    @GuardedBy({"mLock"})
    public final void endLastStateLocked(long j, int i, int i2) {
        int i3 = this.mCurrentState;
        if (i3 < 0) {
            return;
        }
        Stat stat = getStat(i3);
        stat.endBatteryLevel = i;
        stat.endBatteryPercent = i2;
        stat.endTime = j;
        long j2 = j - stat.startTime;
        int i4 = stat.startBatteryLevel - i;
        int i5 = stat.startBatteryPercent - i2;
        stat.totalTimeMillis += j2;
        stat.totalBatteryDrain += i4;
        stat.totalBatteryDrainPercent += i5;
        EventLogTags.writeBatterySavingStats(BatterySaverState.fromIndex(this.mCurrentState), InteractiveState.fromIndex(this.mCurrentState), DozeState.fromIndex(this.mCurrentState), j2, i4, i5, stat.totalTimeMillis, stat.totalBatteryDrain, stat.totalBatteryDrainPercent);
    }

    @GuardedBy({"mLock"})
    public final void startNewStateLocked(int i, long j, int i2, int i3) {
        this.mCurrentState = i;
        if (i < 0) {
            return;
        }
        Stat stat = getStat(i);
        stat.startBatteryLevel = i2;
        stat.startBatteryPercent = i3;
        stat.startTime = j;
        stat.endTime = 0L;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println("Battery saving stats:");
        indentingPrintWriter.increaseIndent();
        synchronized (this.mLock) {
            long currentTimeMillis = System.currentTimeMillis();
            long injectCurrentTime = injectCurrentTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            indentingPrintWriter.print("Battery Saver is currently: ");
            int fromIndex = BatterySaverState.fromIndex(this.mCurrentState);
            if (fromIndex == 0) {
                indentingPrintWriter.println("OFF");
            } else if (fromIndex == 1) {
                indentingPrintWriter.println("ON");
            } else if (fromIndex == 2) {
                indentingPrintWriter.println("ADAPTIVE");
            }
            indentingPrintWriter.increaseIndent();
            if (this.mLastBatterySaverEnabledTime > 0) {
                indentingPrintWriter.print("Last ON time: ");
                indentingPrintWriter.print(simpleDateFormat.format(new Date((currentTimeMillis - injectCurrentTime) + this.mLastBatterySaverEnabledTime)));
                indentingPrintWriter.print(" ");
                TimeUtils.formatDuration(this.mLastBatterySaverEnabledTime, injectCurrentTime, indentingPrintWriter);
                indentingPrintWriter.println();
            }
            if (this.mLastBatterySaverDisabledTime > 0) {
                indentingPrintWriter.print("Last OFF time: ");
                indentingPrintWriter.print(simpleDateFormat.format(new Date((currentTimeMillis - injectCurrentTime) + this.mLastBatterySaverDisabledTime)));
                indentingPrintWriter.print(" ");
                TimeUtils.formatDuration(this.mLastBatterySaverDisabledTime, injectCurrentTime, indentingPrintWriter);
                indentingPrintWriter.println();
            }
            indentingPrintWriter.print("Times full enabled: ");
            indentingPrintWriter.println(this.mBatterySaverEnabledCount);
            if (this.mLastAdaptiveBatterySaverEnabledTime > 0) {
                indentingPrintWriter.print("Last ADAPTIVE ON time: ");
                indentingPrintWriter.print(simpleDateFormat.format(new Date((currentTimeMillis - injectCurrentTime) + this.mLastAdaptiveBatterySaverEnabledTime)));
                indentingPrintWriter.print(" ");
                TimeUtils.formatDuration(this.mLastAdaptiveBatterySaverEnabledTime, injectCurrentTime, indentingPrintWriter);
                indentingPrintWriter.println();
            }
            if (this.mLastAdaptiveBatterySaverDisabledTime > 0) {
                indentingPrintWriter.print("Last ADAPTIVE OFF time: ");
                indentingPrintWriter.print(simpleDateFormat.format(new Date((currentTimeMillis - injectCurrentTime) + this.mLastAdaptiveBatterySaverDisabledTime)));
                indentingPrintWriter.print(" ");
                TimeUtils.formatDuration(this.mLastAdaptiveBatterySaverDisabledTime, injectCurrentTime, indentingPrintWriter);
                indentingPrintWriter.println();
            }
            indentingPrintWriter.print("Times adaptive enabled: ");
            indentingPrintWriter.println(this.mAdaptiveBatterySaverEnabledCount);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println();
            indentingPrintWriter.println("Drain stats:");
            indentingPrintWriter.println("                   Battery saver OFF                          ON");
            dumpLineLocked(indentingPrintWriter, 0, "NonIntr", 0, "NonDoze");
            dumpLineLocked(indentingPrintWriter, 1, "   Intr", 0, "       ");
            dumpLineLocked(indentingPrintWriter, 0, "NonIntr", 2, "Deep   ");
            dumpLineLocked(indentingPrintWriter, 1, "   Intr", 2, "       ");
            dumpLineLocked(indentingPrintWriter, 0, "NonIntr", 1, "Light  ");
            dumpLineLocked(indentingPrintWriter, 1, "   Intr", 1, "       ");
        }
        indentingPrintWriter.decreaseIndent();
    }

    public final void dumpLineLocked(IndentingPrintWriter indentingPrintWriter, int i, String str, int i2, String str2) {
        indentingPrintWriter.print(str2);
        indentingPrintWriter.print(" ");
        indentingPrintWriter.print(str);
        indentingPrintWriter.print(": ");
        Stat stat = getStat(0, i, i2, 0);
        Stat stat2 = getStat(1, i, i2, 0);
        indentingPrintWriter.println(String.format("%6dm %6dmAh(%3d%%) %8.1fmAh/h     %6dm %6dmAh(%3d%%) %8.1fmAh/h", Long.valueOf(stat.totalMinutes()), Integer.valueOf(stat.totalBatteryDrain / 1000), Integer.valueOf(stat.totalBatteryDrainPercent), Double.valueOf(stat.drainPerHour() / 1000.0d), Long.valueOf(stat2.totalMinutes()), Integer.valueOf(stat2.totalBatteryDrain / 1000), Integer.valueOf(stat2.totalBatteryDrainPercent), Double.valueOf(stat2.drainPerHour() / 1000.0d)));
    }
}
