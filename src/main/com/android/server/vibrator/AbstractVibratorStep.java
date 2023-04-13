package com.android.server.vibrator;

import android.os.SystemClock;
import android.os.VibrationEffect;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public abstract class AbstractVibratorStep extends Step {
    public final VibratorController controller;
    public final VibrationEffect.Composed effect;
    public long mPendingVibratorOffDeadline;
    public boolean mVibratorCompleteCallbackReceived;
    public long mVibratorOnResult;
    public final int segmentIndex;

    public AbstractVibratorStep(VibrationStepConductor vibrationStepConductor, long j, VibratorController vibratorController, VibrationEffect.Composed composed, int i, long j2) {
        super(vibrationStepConductor, j);
        this.controller = vibratorController;
        this.effect = composed;
        this.segmentIndex = i;
        this.mPendingVibratorOffDeadline = j2;
    }

    public int getVibratorId() {
        return this.controller.getVibratorInfo().getId();
    }

    @Override // com.android.server.vibrator.Step
    public long getVibratorOnDuration() {
        return this.mVibratorOnResult;
    }

    @Override // com.android.server.vibrator.Step
    public boolean acceptVibratorCompleteCallback(int i) {
        if (getVibratorId() != i) {
            return false;
        }
        boolean z = this.mPendingVibratorOffDeadline > SystemClock.uptimeMillis();
        this.mPendingVibratorOffDeadline = 0L;
        this.mVibratorCompleteCallbackReceived = true;
        return z;
    }

    @Override // com.android.server.vibrator.Step
    public List<Step> cancel() {
        return Arrays.asList(new CompleteEffectVibratorStep(this.conductor, SystemClock.uptimeMillis(), true, this.controller, this.mPendingVibratorOffDeadline));
    }

    @Override // com.android.server.vibrator.Step
    public void cancelImmediately() {
        if (this.mPendingVibratorOffDeadline > SystemClock.uptimeMillis()) {
            stopVibrating();
        }
    }

    public long handleVibratorOnResult(long j) {
        this.mVibratorOnResult = j;
        if (j > 0) {
            this.mPendingVibratorOffDeadline = SystemClock.uptimeMillis() + this.mVibratorOnResult + 1000;
        } else {
            this.mPendingVibratorOffDeadline = 0L;
        }
        return this.mVibratorOnResult;
    }

    public void stopVibrating() {
        this.controller.off();
        getVibration().stats.reportVibratorOff();
        this.mPendingVibratorOffDeadline = 0L;
    }

    public void changeAmplitude(float f) {
        this.controller.setAmplitude(f);
        getVibration().stats.reportSetAmplitude();
    }

    public List<Step> nextSteps(int i) {
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = this.mVibratorOnResult;
        if (j > 0) {
            uptimeMillis += j;
        }
        return nextSteps(uptimeMillis, i);
    }

    public List<Step> nextSteps(long j, int i) {
        int i2 = this.segmentIndex + i;
        int size = this.effect.getSegments().size();
        int repeatIndex = this.effect.getRepeatIndex();
        if (i2 >= size && repeatIndex >= 0) {
            int i3 = size - repeatIndex;
            getVibration().stats.reportRepetition((i2 - repeatIndex) / i3);
            i2 = ((i2 - size) % i3) + repeatIndex;
        }
        AbstractVibratorStep nextVibrateStep = this.conductor.nextVibrateStep(j, this.controller, this.effect, i2, this.mPendingVibratorOffDeadline);
        return nextVibrateStep == null ? VibrationStepConductor.EMPTY_STEP_LIST : Arrays.asList(nextVibrateStep);
    }
}
