package com.android.server.policy;

import android.metrics.LogMaker;
import android.os.SystemClock;
import com.android.internal.logging.MetricsLogger;
/* loaded from: classes2.dex */
public class DisplayFoldDurationLogger {
    public volatile int mScreenState = -1;
    public volatile Long mLastChanged = null;
    public final MetricsLogger mLogger = new MetricsLogger();

    public void onFinishedWakingUp(Boolean bool) {
        if (bool == null) {
            this.mScreenState = -1;
        } else if (bool.booleanValue()) {
            this.mScreenState = 2;
        } else {
            this.mScreenState = 1;
        }
        this.mLastChanged = Long.valueOf(SystemClock.uptimeMillis());
    }

    public void onFinishedGoingToSleep() {
        log();
        this.mScreenState = 0;
        this.mLastChanged = null;
    }

    public void setDeviceFolded(boolean z) {
        if (isOn()) {
            log();
            this.mScreenState = z ? 2 : 1;
            this.mLastChanged = Long.valueOf(SystemClock.uptimeMillis());
        }
    }

    public void logFocusedAppWithFoldState(boolean z, String str) {
        this.mLogger.write(new LogMaker(1594).setType(4).setSubtype(z ? 1 : 0).setPackageName(str));
    }

    public final void log() {
        int i;
        if (this.mLastChanged == null) {
            return;
        }
        int i2 = this.mScreenState;
        if (i2 == 1) {
            i = Integer.MIN_VALUE;
        } else if (i2 != 2) {
            return;
        } else {
            i = -2147483647;
        }
        this.mLogger.write(new LogMaker(1594).setType(4).setSubtype(i).setLatency(SystemClock.uptimeMillis() - this.mLastChanged.longValue()));
    }

    public final boolean isOn() {
        return this.mScreenState == 1 || this.mScreenState == 2;
    }
}
