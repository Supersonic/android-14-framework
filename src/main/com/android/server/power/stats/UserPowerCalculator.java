package com.android.server.power.stats;

import android.os.BatteryStats;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.UidBatteryConsumer;
import android.os.UserHandle;
import android.util.SparseArray;
import com.android.internal.util.ArrayUtils;
/* loaded from: classes2.dex */
public class UserPowerCalculator extends PowerCalculator {
    @Override // com.android.server.power.stats.PowerCalculator
    public boolean isPowerComponentSupported(int i) {
        return true;
    }

    @Override // com.android.server.power.stats.PowerCalculator
    public void calculate(BatteryUsageStats.Builder builder, BatteryStats batteryStats, long j, long j2, BatteryUsageStatsQuery batteryUsageStatsQuery) {
        int[] userIds = batteryUsageStatsQuery.getUserIds();
        if (ArrayUtils.contains(userIds, -1)) {
            return;
        }
        SparseArray uidBatteryConsumerBuilders = builder.getUidBatteryConsumerBuilders();
        for (int size = uidBatteryConsumerBuilders.size() - 1; size >= 0; size--) {
            UidBatteryConsumer.Builder builder2 = (UidBatteryConsumer.Builder) uidBatteryConsumerBuilders.valueAt(size);
            if (!builder2.isVirtualUid()) {
                int uid = builder2.getUid();
                if (UserHandle.getAppId(uid) >= 10000) {
                    int userId = UserHandle.getUserId(uid);
                    if (!ArrayUtils.contains(userIds, userId)) {
                        builder2.excludeFromBatteryUsageStats();
                        builder.getOrCreateUserBatteryConsumerBuilder(userId).addUidBatteryConsumer(builder2);
                    }
                }
            }
        }
    }
}
