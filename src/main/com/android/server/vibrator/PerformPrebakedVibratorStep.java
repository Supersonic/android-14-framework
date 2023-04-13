package com.android.server.vibrator;

import android.os.Trace;
import android.os.VibrationEffect;
import android.os.vibrator.PrebakedSegment;
import android.os.vibrator.VibrationEffectSegment;
import android.util.Slog;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class PerformPrebakedVibratorStep extends AbstractVibratorStep {
    public PerformPrebakedVibratorStep(VibrationStepConductor vibrationStepConductor, long j, VibratorController vibratorController, VibrationEffect.Composed composed, int i, long j2) {
        super(vibrationStepConductor, Math.max(j, j2), vibratorController, composed, i, j2);
    }

    @Override // com.android.server.vibrator.Step
    public List<Step> play() {
        Trace.traceBegin(8388608L, "PerformPrebakedVibratorStep");
        try {
            PrebakedSegment prebakedSegment = (VibrationEffectSegment) this.effect.getSegments().get(this.segmentIndex);
            if (!(prebakedSegment instanceof PrebakedSegment)) {
                Slog.w("VibrationThread", "Ignoring wrong segment for a PerformPrebakedVibratorStep: " + prebakedSegment);
                return nextSteps(1);
            }
            PrebakedSegment prebakedSegment2 = prebakedSegment;
            VibrationEffect fallback = getVibration().getFallback(prebakedSegment2.getEffectId());
            long m8on = this.controller.m8on(prebakedSegment2, getVibration().f1160id);
            handleVibratorOnResult(m8on);
            getVibration().stats.reportPerformEffect(m8on, prebakedSegment2);
            if (m8on == 0 && prebakedSegment2.shouldFallback() && (fallback instanceof VibrationEffect.Composed)) {
                AbstractVibratorStep nextVibrateStep = this.conductor.nextVibrateStep(this.startTime, this.controller, replaceCurrentSegment((VibrationEffect.Composed) fallback), this.segmentIndex, this.mPendingVibratorOffDeadline);
                List<Step> play = nextVibrateStep.play();
                handleVibratorOnResult(nextVibrateStep.getVibratorOnDuration());
                return play;
            }
            return nextSteps(1);
        } finally {
            Trace.traceEnd(8388608L);
        }
    }

    public final VibrationEffect.Composed replaceCurrentSegment(VibrationEffect.Composed composed) {
        ArrayList arrayList = new ArrayList(this.effect.getSegments());
        int repeatIndex = this.effect.getRepeatIndex();
        arrayList.remove(this.segmentIndex);
        arrayList.addAll(this.segmentIndex, composed.getSegments());
        if (this.segmentIndex < this.effect.getRepeatIndex()) {
            repeatIndex += composed.getSegments().size() - 1;
        }
        return new VibrationEffect.Composed(arrayList, repeatIndex);
    }
}
