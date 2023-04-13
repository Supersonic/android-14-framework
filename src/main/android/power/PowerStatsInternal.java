package android.power;

import android.hardware.power.stats.Channel;
import android.hardware.power.stats.EnergyConsumer;
import android.hardware.power.stats.EnergyConsumerResult;
import android.hardware.power.stats.EnergyMeasurement;
import android.hardware.power.stats.PowerEntity;
import android.hardware.power.stats.StateResidencyResult;
import java.util.concurrent.CompletableFuture;
/* loaded from: classes.dex */
public abstract class PowerStatsInternal {
    public abstract CompletableFuture<EnergyConsumerResult[]> getEnergyConsumedAsync(int[] iArr);

    public abstract EnergyConsumer[] getEnergyConsumerInfo();

    public abstract Channel[] getEnergyMeterInfo();

    public abstract PowerEntity[] getPowerEntityInfo();

    public abstract CompletableFuture<StateResidencyResult[]> getStateResidencyAsync(int[] iArr);

    public abstract CompletableFuture<EnergyMeasurement[]> readEnergyMeterAsync(int[] iArr);
}
