package com.android.server.vibrator;

import android.os.SystemClock;
import android.os.Trace;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public final class CompleteEffectVibratorStep extends AbstractVibratorStep {
    public final boolean mCancelled;

    public CompleteEffectVibratorStep(VibrationStepConductor vibrationStepConductor, long j, boolean z, VibratorController vibratorController, long j2) {
        super(vibrationStepConductor, j, vibratorController, null, -1, j2);
        this.mCancelled = z;
    }

    @Override // com.android.server.vibrator.Step
    public boolean isCleanUp() {
        return this.mCancelled;
    }

    @Override // com.android.server.vibrator.AbstractVibratorStep, com.android.server.vibrator.Step
    public List<Step> cancel() {
        if (this.mCancelled) {
            return Arrays.asList(new TurnOffVibratorStep(this.conductor, SystemClock.uptimeMillis(), this.controller));
        }
        return super.cancel();
    }

    @Override // com.android.server.vibrator.Step
    public List<Step> play() {
        Trace.traceBegin(8388608L, "CompleteEffectVibratorStep");
        try {
            if (this.mVibratorCompleteCallbackReceived) {
                stopVibrating();
                return VibrationStepConductor.EMPTY_STEP_LIST;
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            float currentAmplitude = this.controller.getCurrentAmplitude();
            long min = Math.min((this.mPendingVibratorOffDeadline - uptimeMillis) - 1000, this.conductor.vibrationSettings.getRampDownDuration());
            long rampStepDuration = this.conductor.vibrationSettings.getRampStepDuration();
            if (currentAmplitude >= 0.001f && min > rampStepDuration) {
                float f = currentAmplitude / ((float) (min / rampStepDuration));
                return Arrays.asList(new RampOffVibratorStep(this.conductor, this.startTime, currentAmplitude - f, f, this.controller, this.mCancelled ? uptimeMillis + min : this.mPendingVibratorOffDeadline));
            }
            if (this.mCancelled) {
                stopVibrating();
                return VibrationStepConductor.EMPTY_STEP_LIST;
            }
            return Arrays.asList(new TurnOffVibratorStep(this.conductor, this.mPendingVibratorOffDeadline, this.controller));
        } finally {
            Trace.traceEnd(8388608L);
        }
    }
}
