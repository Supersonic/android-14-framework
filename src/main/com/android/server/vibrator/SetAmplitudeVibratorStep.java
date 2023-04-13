package com.android.server.vibrator;

import android.os.SystemClock;
import android.os.Trace;
import android.os.VibrationEffect;
import android.os.vibrator.StepSegment;
import android.os.vibrator.VibrationEffectSegment;
import android.util.Slog;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public final class SetAmplitudeVibratorStep extends AbstractVibratorStep {
    public SetAmplitudeVibratorStep(VibrationStepConductor vibrationStepConductor, long j, VibratorController vibratorController, VibrationEffect.Composed composed, int i, long j2) {
        super(vibrationStepConductor, j, vibratorController, composed, i, j2);
    }

    @Override // com.android.server.vibrator.AbstractVibratorStep, com.android.server.vibrator.Step
    public boolean acceptVibratorCompleteCallback(int i) {
        return super.acceptVibratorCompleteCallback(i) && SystemClock.uptimeMillis() < this.startTime && this.controller.getCurrentAmplitude() > 0.0f;
    }

    @Override // com.android.server.vibrator.Step
    public List<Step> play() {
        Trace.traceBegin(8388608L, "SetAmplitudeVibratorStep");
        try {
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = uptimeMillis - this.startTime;
            if (this.mVibratorCompleteCallbackReceived && j < 0) {
                turnVibratorBackOn(-j);
                return Arrays.asList(new SetAmplitudeVibratorStep(this.conductor, this.startTime, this.controller, this.effect, this.segmentIndex, this.mPendingVibratorOffDeadline));
            }
            StepSegment stepSegment = (VibrationEffectSegment) this.effect.getSegments().get(this.segmentIndex);
            if (!(stepSegment instanceof StepSegment)) {
                Slog.w("VibrationThread", "Ignoring wrong segment for a SetAmplitudeVibratorStep: " + stepSegment);
                return nextSteps(this.startTime, 1);
            }
            StepSegment stepSegment2 = stepSegment;
            if (stepSegment2.getDuration() == 0) {
                return nextSteps(this.startTime, 1);
            }
            float amplitude = stepSegment2.getAmplitude();
            if (amplitude != 0.0f) {
                if (this.startTime >= this.mPendingVibratorOffDeadline) {
                    long vibratorOnDuration = getVibratorOnDuration(this.effect, this.segmentIndex);
                    if (vibratorOnDuration > 0) {
                        startVibrating(vibratorOnDuration);
                    }
                }
                changeAmplitude(amplitude);
            } else if (this.mPendingVibratorOffDeadline > uptimeMillis) {
                stopVibrating();
            }
            return nextSteps(this.startTime + stepSegment.getDuration(), 1);
        } finally {
            Trace.traceEnd(8388608L);
        }
    }

    public final void turnVibratorBackOn(long j) {
        long vibratorOnDuration = getVibratorOnDuration(this.effect, this.segmentIndex);
        if (vibratorOnDuration <= 0) {
            return;
        }
        long j2 = vibratorOnDuration + j;
        float currentAmplitude = this.controller.getCurrentAmplitude();
        if (startVibrating(j2) > 0) {
            changeAmplitude(currentAmplitude);
        }
    }

    public final long startVibrating(long j) {
        long m9on = this.controller.m9on(j, getVibration().f1160id);
        handleVibratorOnResult(m9on);
        getVibration().stats.reportVibratorOn(m9on);
        return m9on;
    }

    public final long getVibratorOnDuration(VibrationEffect.Composed composed, int i) {
        List segments = composed.getSegments();
        int size = segments.size();
        int repeatIndex = composed.getRepeatIndex();
        long j = 0;
        int i2 = i;
        while (i2 < size) {
            StepSegment stepSegment = (VibrationEffectSegment) segments.get(i2);
            if (!(stepSegment instanceof StepSegment) || stepSegment.getAmplitude() == 0.0f) {
                break;
            }
            j += stepSegment.getDuration();
            i2++;
            if (i2 == size && repeatIndex >= 0) {
                i2 = repeatIndex;
                repeatIndex = -1;
                continue;
            }
            if (i2 == i) {
                return Math.max(j, 5000L);
            }
        }
        return (i2 != size || composed.getRepeatIndex() >= 0) ? j : j + this.conductor.vibrationSettings.getRampDownDuration();
    }
}
