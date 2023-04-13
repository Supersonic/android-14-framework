package com.android.server.vibrator;

import android.os.SystemClock;
import java.util.List;
/* loaded from: classes2.dex */
public abstract class Step implements Comparable<Step> {
    public final VibrationStepConductor conductor;
    public final long startTime;

    public boolean acceptVibratorCompleteCallback(int i) {
        return false;
    }

    public abstract List<Step> cancel();

    public abstract void cancelImmediately();

    public long getVibratorOnDuration() {
        return 0L;
    }

    public boolean isCleanUp() {
        return false;
    }

    public abstract List<Step> play();

    public Step(VibrationStepConductor vibrationStepConductor, long j) {
        this.conductor = vibrationStepConductor;
        this.startTime = j;
    }

    public HalVibration getVibration() {
        return this.conductor.getVibration();
    }

    public long calculateWaitTime() {
        long j = this.startTime;
        if (j == Long.MAX_VALUE) {
            return 0L;
        }
        return Math.max(0L, j - SystemClock.uptimeMillis());
    }

    @Override // java.lang.Comparable
    public int compareTo(Step step) {
        return Long.compare(this.startTime, step.startTime);
    }
}
