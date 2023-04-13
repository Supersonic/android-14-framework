package com.android.server.powerstats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
/* loaded from: classes2.dex */
public final class BatteryTrigger extends PowerStatsLogTrigger {
    public int mBatteryLevel;
    public final BroadcastReceiver mBatteryLevelReceiver;

    public BatteryTrigger(Context context, PowerStatsLogger powerStatsLogger, boolean z) {
        super(context, powerStatsLogger);
        this.mBatteryLevel = 0;
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.powerstats.BatteryTrigger.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                    int intExtra = intent.getIntExtra("level", 0);
                    if (intExtra < BatteryTrigger.this.mBatteryLevel) {
                        BatteryTrigger.this.logPowerStatsData(0);
                    }
                    BatteryTrigger.this.mBatteryLevel = intExtra;
                }
            }
        };
        this.mBatteryLevelReceiver = broadcastReceiver;
        if (z) {
            this.mBatteryLevel = this.mContext.registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("level", 0);
        }
    }
}
