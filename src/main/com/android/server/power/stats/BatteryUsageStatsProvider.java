package com.android.server.power.stats;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.BatteryConsumer;
import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.SystemClock;
import android.os.UidBatteryConsumer;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.PowerProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public class BatteryUsageStatsProvider {
    public static boolean sErrorReported;
    public final BatteryUsageStatsStore mBatteryUsageStatsStore;
    public final Context mContext;
    public final Object mLock;
    public List<PowerCalculator> mPowerCalculators;
    public final PowerProfile mPowerProfile;
    public final BatteryStats mStats;

    public BatteryUsageStatsProvider(Context context, BatteryStats batteryStats) {
        this(context, batteryStats, null);
    }

    @VisibleForTesting
    public BatteryUsageStatsProvider(Context context, BatteryStats batteryStats, BatteryUsageStatsStore batteryUsageStatsStore) {
        PowerProfile powerProfile;
        this.mLock = new Object();
        this.mContext = context;
        this.mStats = batteryStats;
        this.mBatteryUsageStatsStore = batteryUsageStatsStore;
        if (batteryStats instanceof BatteryStatsImpl) {
            powerProfile = ((BatteryStatsImpl) batteryStats).getPowerProfile();
        } else {
            powerProfile = new PowerProfile(context);
        }
        this.mPowerProfile = powerProfile;
    }

    public final List<PowerCalculator> getPowerCalculators() {
        synchronized (this.mLock) {
            if (this.mPowerCalculators == null) {
                ArrayList arrayList = new ArrayList();
                this.mPowerCalculators = arrayList;
                arrayList.add(new BatteryChargeCalculator());
                this.mPowerCalculators.add(new CpuPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new MemoryPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new WakelockPowerCalculator(this.mPowerProfile));
                if (!BatteryStats.checkWifiOnly(this.mContext)) {
                    this.mPowerCalculators.add(new MobileRadioPowerCalculator(this.mPowerProfile));
                }
                this.mPowerCalculators.add(new WifiPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new BluetoothPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new SensorPowerCalculator((SensorManager) this.mContext.getSystemService(SensorManager.class)));
                this.mPowerCalculators.add(new GnssPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new CameraPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new FlashlightPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new AudioPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new VideoPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new PhonePowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new ScreenPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new AmbientDisplayPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new IdlePowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new CustomEnergyConsumerPowerCalculator(this.mPowerProfile));
                this.mPowerCalculators.add(new UserPowerCalculator());
                this.mPowerCalculators.add(new SystemServicePowerCalculator(this.mPowerProfile));
            }
        }
        return this.mPowerCalculators;
    }

    public boolean shouldUpdateStats(List<BatteryUsageStatsQuery> list, long j) {
        long j2 = Long.MAX_VALUE;
        for (int size = list.size() - 1; size >= 0; size--) {
            j2 = Math.min(j2, list.get(size).getMaxStatsAge());
        }
        return elapsedRealtime() - j > j2;
    }

    public List<BatteryUsageStats> getBatteryUsageStats(List<BatteryUsageStatsQuery> list) {
        ArrayList arrayList = new ArrayList(list.size());
        synchronized (this.mStats) {
            this.mStats.prepareForDumpLocked();
            long currentTimeMillis = currentTimeMillis();
            for (int i = 0; i < list.size(); i++) {
                arrayList.add(getBatteryUsageStats(list.get(i), currentTimeMillis));
            }
        }
        return arrayList;
    }

    @VisibleForTesting
    public BatteryUsageStats getBatteryUsageStats(BatteryUsageStatsQuery batteryUsageStatsQuery) {
        BatteryUsageStats batteryUsageStats;
        synchronized (this.mStats) {
            batteryUsageStats = getBatteryUsageStats(batteryUsageStatsQuery, currentTimeMillis());
        }
        return batteryUsageStats;
    }

    @GuardedBy({"mStats"})
    public final BatteryUsageStats getBatteryUsageStats(BatteryUsageStatsQuery batteryUsageStatsQuery, long j) {
        if (batteryUsageStatsQuery.getToTimestamp() == 0) {
            return getCurrentBatteryUsageStats(batteryUsageStatsQuery, j);
        }
        return getAggregatedBatteryUsageStats(batteryUsageStatsQuery);
    }

    @GuardedBy({"mStats"})
    public final BatteryUsageStats getCurrentBatteryUsageStats(BatteryUsageStatsQuery batteryUsageStatsQuery, long j) {
        int i;
        int i2;
        int[] iArr;
        List<PowerCalculator> list;
        BatteryUsageStats.Builder builder;
        int i3;
        long elapsedRealtime = elapsedRealtime() * 1000;
        long uptimeMillis = 1000 * uptimeMillis();
        int i4 = 0;
        boolean z = (batteryUsageStatsQuery.getFlags() & 4) != 0;
        boolean z2 = (batteryUsageStatsQuery.getFlags() & 8) != 0 && this.mStats.isProcessStateDataAvailable();
        boolean z3 = (batteryUsageStatsQuery.getFlags() & 16) != 0;
        BatteryUsageStats.Builder builder2 = new BatteryUsageStats.Builder(this.mStats.getCustomEnergyConsumerNames(), z, z2);
        builder2.setStatsStartTimestamp(this.mStats.getStartClockTime());
        builder2.setStatsEndTimestamp(j);
        SparseArray uidStats = this.mStats.getUidStats();
        for (int size = uidStats.size() - 1; size >= 0; size--) {
            BatteryStats.Uid uid = (BatteryStats.Uid) uidStats.valueAt(size);
            if (z3 || uid.getUid() != 1090) {
                builder2.getOrCreateUidBatteryConsumerBuilder(uid).setTimeInStateMs(1, getProcessBackgroundTimeMs(uid, elapsedRealtime)).setTimeInStateMs(0, getProcessForegroundTimeMs(uid, elapsedRealtime));
            }
        }
        int[] powerComponents = batteryUsageStatsQuery.getPowerComponents();
        List<PowerCalculator> powerCalculators = getPowerCalculators();
        int size2 = powerCalculators.size();
        int i5 = 0;
        while (i5 < size2) {
            PowerCalculator powerCalculator = powerCalculators.get(i5);
            if (powerComponents != null) {
                int i6 = i4;
                while (true) {
                    if (i6 >= powerComponents.length) {
                        i3 = i4;
                        break;
                    } else if (powerCalculator.isPowerComponentSupported(powerComponents[i6])) {
                        i3 = 1;
                        break;
                    } else {
                        i6++;
                    }
                }
                if (i3 == 0) {
                    i = size2;
                    i2 = i5;
                    iArr = powerComponents;
                    list = powerCalculators;
                    builder = builder2;
                    i5 = i2 + 1;
                    builder2 = builder;
                    size2 = i;
                    powerComponents = iArr;
                    powerCalculators = list;
                    i4 = 0;
                }
            }
            i = size2;
            i2 = i5;
            iArr = powerComponents;
            list = powerCalculators;
            builder = builder2;
            powerCalculator.calculate(builder2, this.mStats, elapsedRealtime, uptimeMillis, batteryUsageStatsQuery);
            i5 = i2 + 1;
            builder2 = builder;
            size2 = i;
            powerComponents = iArr;
            powerCalculators = list;
            i4 = 0;
        }
        BatteryUsageStats.Builder builder3 = builder2;
        if ((batteryUsageStatsQuery.getFlags() & 2) != 0) {
            BatteryStats batteryStats = this.mStats;
            if (!(batteryStats instanceof BatteryStatsImpl)) {
                throw new UnsupportedOperationException("History cannot be included for " + getClass().getName());
            }
            builder3.setBatteryHistory(((BatteryStatsImpl) batteryStats).copyHistory());
        }
        BatteryUsageStats build = builder3.build();
        if (z2) {
            verify(build);
        }
        return build;
    }

    public final void verify(BatteryUsageStats batteryUsageStats) {
        if (sErrorReported) {
            return;
        }
        int[] iArr = {1, 8, 11, 2};
        int[] iArr2 = {1, 2, 3, 4};
        for (UidBatteryConsumer uidBatteryConsumer : batteryUsageStats.getUidBatteryConsumers()) {
            for (int i = 0; i < 4; i++) {
                int i2 = iArr[i];
                double consumedPower = uidBatteryConsumer.getConsumedPower(uidBatteryConsumer.getKey(i2));
                double d = 0.0d;
                for (int i3 = 0; i3 < 4; i3++) {
                    d += uidBatteryConsumer.getConsumedPower(uidBatteryConsumer.getKey(i2, iArr2[i3]));
                }
                if (d > 2.0d + consumedPower) {
                    String str = "Sum of states exceeds total. UID = " + uidBatteryConsumer.getUid() + " " + BatteryConsumer.powerComponentIdToString(i2) + " total = " + consumedPower + " states = " + d;
                    if (!sErrorReported) {
                        Slog.wtf("BatteryUsageStatsProv", str);
                        sErrorReported = true;
                        return;
                    }
                    Slog.e("BatteryUsageStatsProv", str);
                    return;
                }
            }
        }
    }

    public final long getProcessForegroundTimeMs(BatteryStats.Uid uid, long j) {
        long processStateTime = uid.getProcessStateTime(0, j, 0);
        BatteryStats.Timer foregroundActivityTimer = uid.getForegroundActivityTimer();
        return (Math.min(processStateTime, foregroundActivityTimer != null ? foregroundActivityTimer.getTotalTimeLocked(j, 0) : 0L) + uid.getProcessStateTime(2, j, 0)) / 1000;
    }

    public final long getProcessBackgroundTimeMs(BatteryStats.Uid uid, long j) {
        return (uid.getProcessStateTime(3, j, 0) + uid.getProcessStateTime(1, j, 0)) / 1000;
    }

    public final BatteryUsageStats getAggregatedBatteryUsageStats(BatteryUsageStatsQuery batteryUsageStatsQuery) {
        long[] listBatteryUsageStatsTimestamps;
        BatteryUsageStats loadBatteryUsageStats;
        boolean z = true;
        boolean z2 = (batteryUsageStatsQuery.getFlags() & 4) != 0;
        z = ((batteryUsageStatsQuery.getFlags() & 8) == 0 || !this.mStats.isProcessStateDataAvailable()) ? false : false;
        String[] customEnergyConsumerNames = this.mStats.getCustomEnergyConsumerNames();
        BatteryUsageStats.Builder builder = new BatteryUsageStats.Builder(customEnergyConsumerNames, z2, z);
        BatteryUsageStatsStore batteryUsageStatsStore = this.mBatteryUsageStatsStore;
        if (batteryUsageStatsStore == null) {
            Log.e("BatteryUsageStatsProv", "BatteryUsageStatsStore is unavailable");
            return builder.build();
        }
        for (long j : batteryUsageStatsStore.listBatteryUsageStatsTimestamps()) {
            if (j > batteryUsageStatsQuery.getFromTimestamp() && j <= batteryUsageStatsQuery.getToTimestamp() && (loadBatteryUsageStats = this.mBatteryUsageStatsStore.loadBatteryUsageStats(j)) != null) {
                if (!Arrays.equals(loadBatteryUsageStats.getCustomPowerComponentNames(), customEnergyConsumerNames)) {
                    Log.w("BatteryUsageStatsProv", "Ignoring older BatteryUsageStats snapshot, which has different custom power components: " + Arrays.toString(loadBatteryUsageStats.getCustomPowerComponentNames()));
                } else if (z && !loadBatteryUsageStats.isProcessStateDataIncluded()) {
                    Log.w("BatteryUsageStatsProv", "Ignoring older BatteryUsageStats snapshot, which  does not include process state data");
                } else {
                    builder.add(loadBatteryUsageStats);
                }
            }
        }
        return builder.build();
    }

    public final long elapsedRealtime() {
        BatteryStats batteryStats = this.mStats;
        if (batteryStats instanceof BatteryStatsImpl) {
            return ((BatteryStatsImpl) batteryStats).mClock.elapsedRealtime();
        }
        return SystemClock.elapsedRealtime();
    }

    public final long uptimeMillis() {
        BatteryStats batteryStats = this.mStats;
        if (batteryStats instanceof BatteryStatsImpl) {
            return ((BatteryStatsImpl) batteryStats).mClock.uptimeMillis();
        }
        return SystemClock.uptimeMillis();
    }

    public final long currentTimeMillis() {
        BatteryStats batteryStats = this.mStats;
        if (batteryStats instanceof BatteryStatsImpl) {
            return ((BatteryStatsImpl) batteryStats).mClock.currentTimeMillis();
        }
        return System.currentTimeMillis();
    }
}
