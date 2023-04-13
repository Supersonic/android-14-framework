package android.p005os;

import android.net.Network;
import com.android.internal.os.BinderCallsStats;
import com.android.server.power.stats.SystemServerCpuThreadReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.List;
/* renamed from: android.os.BatteryStatsInternal */
/* loaded from: classes.dex */
public abstract class BatteryStatsInternal {
    public static final int CPU_WAKEUP_SUBSYSTEM_ALARM = 1;
    public static final int CPU_WAKEUP_SUBSYSTEM_UNKNOWN = -1;
    public static final int CPU_WAKEUP_SUBSYSTEM_WIFI = 2;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryStatsInternal$CpuWakeupSubsystem */
    /* loaded from: classes.dex */
    public @interface CpuWakeupSubsystem {
    }

    public abstract List<BatteryUsageStats> getBatteryUsageStats(List<BatteryUsageStatsQuery> list);

    public abstract String[] getMobileIfaces();

    public abstract SystemServerCpuThreadReader.SystemServiceCpuThreadTimes getSystemServiceCpuThreadTimes();

    public abstract String[] getWifiIfaces();

    public abstract void noteBinderCallStats(int i, long j, Collection<BinderCallsStats.CallStat> collection);

    public abstract void noteBinderThreadNativeIds(int[] iArr);

    public abstract void noteCpuWakingActivity(int i, long j, int... iArr);

    public abstract void noteCpuWakingNetworkPacket(Network network, long j, int i);

    public abstract void noteJobsDeferred(int i, int i2, long j);

    public abstract void noteWakingSoundTrigger(long j, int i);
}
