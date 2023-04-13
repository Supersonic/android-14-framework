package com.android.server.powerstats;

import android.content.Context;
import android.os.Handler;
import com.android.server.clipboard.ClipboardService;
/* loaded from: classes2.dex */
public final class TimerTrigger extends PowerStatsLogTrigger {
    public final Handler mHandler;
    public Runnable mLogDataHighFrequency;
    public Runnable mLogDataLowFrequency;

    public TimerTrigger(Context context, PowerStatsLogger powerStatsLogger, boolean z) {
        super(context, powerStatsLogger);
        this.mLogDataLowFrequency = new Runnable() { // from class: com.android.server.powerstats.TimerTrigger.1
            @Override // java.lang.Runnable
            public void run() {
                TimerTrigger.this.mHandler.postDelayed(TimerTrigger.this.mLogDataLowFrequency, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
                TimerTrigger.this.logPowerStatsData(1);
            }
        };
        this.mLogDataHighFrequency = new Runnable() { // from class: com.android.server.powerstats.TimerTrigger.2
            @Override // java.lang.Runnable
            public void run() {
                TimerTrigger.this.mHandler.postDelayed(TimerTrigger.this.mLogDataHighFrequency, 120000L);
                TimerTrigger.this.logPowerStatsData(2);
            }
        };
        this.mHandler = this.mContext.getMainThreadHandler();
        if (z) {
            this.mLogDataLowFrequency.run();
            this.mLogDataHighFrequency.run();
        }
    }
}
