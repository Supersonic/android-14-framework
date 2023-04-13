package com.android.server.powerstats;

import android.app.StatsManager;
import android.content.Context;
import android.hardware.power.stats.Channel;
import android.hardware.power.stats.EnergyMeasurement;
import android.hardware.power.stats.PowerEntity;
import android.hardware.power.stats.State;
import android.hardware.power.stats.StateResidency;
import android.hardware.power.stats.StateResidencyResult;
import android.power.PowerStatsInternal;
import android.util.Slog;
import android.util.StatsEvent;
import com.android.internal.util.ConcurrentUtils;
import com.android.internal.util.FrameworkStatsLog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class StatsPullAtomCallbackImpl implements StatsManager.StatsPullAtomCallback {
    public static final String TAG = StatsPullAtomCallbackImpl.class.getSimpleName();
    public Context mContext;
    public PowerStatsInternal mPowerStatsInternal;
    public Map<Integer, Channel> mChannels = new HashMap();
    public Map<Integer, String> mEntityNames = new HashMap();
    public Map<Integer, Map<Integer, String>> mStateNames = new HashMap();

    public int onPullAtom(int i, List<StatsEvent> list) {
        if (i != 10005) {
            if (i == 10038) {
                return pullOnDevicePowerMeasurement(i, list);
            }
            throw new UnsupportedOperationException("Unknown tagId=" + i);
        }
        return pullSubsystemSleepState(i, list);
    }

    public final boolean initPullOnDevicePowerMeasurement() {
        Channel[] energyMeterInfo = this.mPowerStatsInternal.getEnergyMeterInfo();
        if (energyMeterInfo == null || energyMeterInfo.length == 0) {
            Slog.e(TAG, "Failed to init OnDevicePowerMeasurement puller");
            return false;
        }
        for (Channel channel : energyMeterInfo) {
            this.mChannels.put(Integer.valueOf(channel.f4id), channel);
        }
        return true;
    }

    public final int pullOnDevicePowerMeasurement(int i, List<StatsEvent> list) {
        try {
            EnergyMeasurement[] energyMeasurementArr = this.mPowerStatsInternal.readEnergyMeterAsync(new int[0]).get(2000L, TimeUnit.MILLISECONDS);
            if (energyMeasurementArr == null) {
                return 1;
            }
            for (EnergyMeasurement energyMeasurement : energyMeasurementArr) {
                if (energyMeasurement.durationMs == energyMeasurement.timestampMs) {
                    list.add(FrameworkStatsLog.buildStatsEvent(i, this.mChannels.get(Integer.valueOf(energyMeasurement.f7id)).subsystem, this.mChannels.get(Integer.valueOf(energyMeasurement.f7id)).name, energyMeasurement.durationMs, energyMeasurement.energyUWs));
                }
            }
            return 0;
        } catch (Exception e) {
            Slog.e(TAG, "Failed to readEnergyMeterAsync", e);
            return 1;
        }
    }

    public final boolean initSubsystemSleepState() {
        PowerEntity[] powerEntityInfo = this.mPowerStatsInternal.getPowerEntityInfo();
        if (powerEntityInfo == null || powerEntityInfo.length == 0) {
            Slog.e(TAG, "Failed to init SubsystemSleepState puller");
            return false;
        }
        for (PowerEntity powerEntity : powerEntityInfo) {
            HashMap hashMap = new HashMap();
            int i = 0;
            while (true) {
                State[] stateArr = powerEntity.states;
                if (i < stateArr.length) {
                    State state = stateArr[i];
                    hashMap.put(Integer.valueOf(state.f9id), state.name);
                    i++;
                }
            }
            this.mEntityNames.put(Integer.valueOf(powerEntity.f8id), powerEntity.name);
            this.mStateNames.put(Integer.valueOf(powerEntity.f8id), hashMap);
        }
        return true;
    }

    public final int pullSubsystemSleepState(int i, List<StatsEvent> list) {
        try {
            StateResidencyResult[] stateResidencyResultArr = this.mPowerStatsInternal.getStateResidencyAsync(new int[0]).get(2000L, TimeUnit.MILLISECONDS);
            if (stateResidencyResultArr == null) {
                return 1;
            }
            for (StateResidencyResult stateResidencyResult : stateResidencyResultArr) {
                int i2 = 0;
                while (true) {
                    StateResidency[] stateResidencyArr = stateResidencyResult.stateResidencyData;
                    if (i2 < stateResidencyArr.length) {
                        StateResidency stateResidency = stateResidencyArr[i2];
                        list.add(FrameworkStatsLog.buildStatsEvent(i, this.mEntityNames.get(Integer.valueOf(stateResidencyResult.f11id)), this.mStateNames.get(Integer.valueOf(stateResidencyResult.f11id)).get(Integer.valueOf(stateResidency.f10id)), stateResidency.totalStateEntryCount, stateResidency.totalTimeInStateMs));
                        i2++;
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            Slog.e(TAG, "Failed to getStateResidencyAsync", e);
            return 1;
        }
    }

    public StatsPullAtomCallbackImpl(Context context, PowerStatsInternal powerStatsInternal) {
        this.mContext = context;
        this.mPowerStatsInternal = powerStatsInternal;
        if (powerStatsInternal == null) {
            Slog.e(TAG, "Failed to start PowerStatsService statsd pullers");
            return;
        }
        StatsManager statsManager = (StatsManager) context.getSystemService(StatsManager.class);
        if (initPullOnDevicePowerMeasurement()) {
            statsManager.setPullAtomCallback((int) FrameworkStatsLog.ON_DEVICE_POWER_MEASUREMENT, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this);
        }
        if (initSubsystemSleepState()) {
            statsManager.setPullAtomCallback((int) FrameworkStatsLog.SUBSYSTEM_SLEEP_STATE, (StatsManager.PullAtomMetadata) null, ConcurrentUtils.DIRECT_EXECUTOR, this);
        }
    }
}
