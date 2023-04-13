package android.p008os.health;

import android.p008os.health.HealthKeys;
/* renamed from: android.os.health.PackageHealthStats */
/* loaded from: classes3.dex */
public final class PackageHealthStats {
    public static final HealthKeys.Constants CONSTANTS = new HealthKeys.Constants(PackageHealthStats.class);
    @HealthKeys.Constant(type = 4)
    public static final int MEASUREMENTS_WAKEUP_ALARMS_COUNT = 40002;
    @HealthKeys.Constant(type = 2)
    public static final int STATS_SERVICES = 40001;

    private PackageHealthStats() {
    }
}
