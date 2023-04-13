package android.p008os.health;

import android.p008os.health.HealthKeys;
/* renamed from: android.os.health.PidHealthStats */
/* loaded from: classes3.dex */
public final class PidHealthStats {
    public static final HealthKeys.Constants CONSTANTS = new HealthKeys.Constants(PidHealthStats.class);
    @HealthKeys.Constant(type = 1)
    public static final int MEASUREMENT_WAKE_NESTING_COUNT = 20001;
    @HealthKeys.Constant(type = 1)
    public static final int MEASUREMENT_WAKE_START_MS = 20003;
    @HealthKeys.Constant(type = 1)
    public static final int MEASUREMENT_WAKE_SUM_MS = 20002;

    private PidHealthStats() {
    }
}
