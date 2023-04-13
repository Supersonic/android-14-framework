package com.android.server.powerstats;

import android.content.Context;
import android.os.Message;
/* loaded from: classes2.dex */
public abstract class PowerStatsLogTrigger {
    public Context mContext;
    public PowerStatsLogger mPowerStatsLogger;

    public void logPowerStatsData(int i) {
        Message.obtain(this.mPowerStatsLogger, i).sendToTarget();
    }

    public PowerStatsLogTrigger(Context context, PowerStatsLogger powerStatsLogger) {
        this.mContext = context;
        this.mPowerStatsLogger = powerStatsLogger;
    }
}
