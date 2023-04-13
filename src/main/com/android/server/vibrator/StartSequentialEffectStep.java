package com.android.server.vibrator;

import android.os.CombinedVibration;
import android.os.SystemClock;
import android.os.Trace;
import android.os.VibrationEffect;
import android.os.vibrator.PrebakedSegment;
import android.os.vibrator.PrimitiveSegment;
import android.os.vibrator.StepSegment;
import android.os.vibrator.VibrationEffectSegment;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class StartSequentialEffectStep extends Step {
    public final int currentIndex;
    public long mVibratorsOnMaxDuration;
    public final CombinedVibration.Sequential sequentialEffect;

    @Override // com.android.server.vibrator.Step
    public void cancelImmediately() {
    }

    public StartSequentialEffectStep(VibrationStepConductor vibrationStepConductor, CombinedVibration.Sequential sequential) {
        this(vibrationStepConductor, SystemClock.uptimeMillis() + ((Integer) sequential.getDelays().get(0)).intValue(), sequential, 0);
    }

    public StartSequentialEffectStep(VibrationStepConductor vibrationStepConductor, long j, CombinedVibration.Sequential sequential, int i) {
        super(vibrationStepConductor, j);
        this.sequentialEffect = sequential;
        this.currentIndex = i;
    }

    @Override // com.android.server.vibrator.Step
    public long getVibratorOnDuration() {
        return this.mVibratorsOnMaxDuration;
    }

    @Override // com.android.server.vibrator.Step
    public List<Step> play() {
        Step nextStep;
        Step nextStep2;
        Trace.traceBegin(8388608L, "StartSequentialEffectStep");
        ArrayList arrayList = new ArrayList();
        this.mVibratorsOnMaxDuration = -1L;
        try {
            DeviceEffectMap createEffectToVibratorMapping = createEffectToVibratorMapping((CombinedVibration) this.sequentialEffect.getEffects().get(this.currentIndex));
            if (createEffectToVibratorMapping == null) {
                return arrayList;
            }
            this.mVibratorsOnMaxDuration = startVibrating(createEffectToVibratorMapping, arrayList);
            VibrationStepConductor vibrationStepConductor = this.conductor;
            vibrationStepConductor.vibratorManagerHooks.noteVibratorOn(vibrationStepConductor.getVibration().callerInfo.uid, this.mVibratorsOnMaxDuration);
            long j = this.mVibratorsOnMaxDuration;
            if (j >= 0) {
                if (j > 0) {
                    nextStep2 = new FinishSequentialEffectStep(this);
                } else {
                    nextStep2 = nextStep();
                }
                if (nextStep2 != null) {
                    arrayList.add(nextStep2);
                }
            }
            Trace.traceEnd(8388608L);
            return arrayList;
        } finally {
            long j2 = this.mVibratorsOnMaxDuration;
            if (j2 >= 0) {
                if (j2 > 0) {
                    nextStep = new FinishSequentialEffectStep(this);
                } else {
                    nextStep = nextStep();
                }
                if (nextStep != null) {
                    arrayList.add(nextStep);
                }
            }
            Trace.traceEnd(8388608L);
        }
    }

    @Override // com.android.server.vibrator.Step
    public List<Step> cancel() {
        return VibrationStepConductor.EMPTY_STEP_LIST;
    }

    public Step nextStep() {
        int i = this.currentIndex + 1;
        if (i >= this.sequentialEffect.getEffects().size()) {
            return null;
        }
        return new StartSequentialEffectStep(this.conductor, SystemClock.uptimeMillis() + ((Integer) this.sequentialEffect.getDelays().get(i)).intValue(), this.sequentialEffect, i);
    }

    public final DeviceEffectMap createEffectToVibratorMapping(CombinedVibration combinedVibration) {
        if (combinedVibration instanceof CombinedVibration.Mono) {
            return new DeviceEffectMap((CombinedVibration.Mono) combinedVibration);
        }
        if (combinedVibration instanceof CombinedVibration.Stereo) {
            return new DeviceEffectMap((CombinedVibration.Stereo) combinedVibration);
        }
        return null;
    }

    public final long startVibrating(DeviceEffectMap deviceEffectMap, List<Step> list) {
        boolean z;
        int size = deviceEffectMap.size();
        if (size == 0) {
            return 0L;
        }
        AbstractVibratorStep[] abstractVibratorStepArr = new AbstractVibratorStep[size];
        long uptimeMillis = SystemClock.uptimeMillis();
        boolean z2 = false;
        int i = 0;
        while (i < size) {
            VibrationStepConductor vibrationStepConductor = this.conductor;
            int i2 = i;
            abstractVibratorStepArr[i2] = vibrationStepConductor.nextVibrateStep(uptimeMillis, vibrationStepConductor.getVibrators().get(deviceEffectMap.vibratorIdAt(i)), deviceEffectMap.effectAt(i), 0, 0L);
            i = i2 + 1;
        }
        if (size == 1) {
            return startVibrating(abstractVibratorStepArr[0], list);
        }
        boolean prepareSyncedVibration = this.conductor.vibratorManagerHooks.prepareSyncedVibration(deviceEffectMap.getRequiredSyncCapabilities(), deviceEffectMap.getVibratorIds());
        long j = 0;
        int i3 = 0;
        while (true) {
            if (i3 >= size) {
                z = false;
                break;
            }
            long startVibrating = startVibrating(abstractVibratorStepArr[i3], list);
            if (startVibrating < 0) {
                z = true;
                break;
            }
            j = Math.max(j, startVibrating);
            i3++;
        }
        if (prepareSyncedVibration && !z && j > 0) {
            z2 = this.conductor.vibratorManagerHooks.triggerSyncedVibration(getVibration().f1160id);
            z &= z2;
        }
        if (z) {
            for (int size2 = list.size() - 1; size2 >= 0; size2--) {
                list.remove(size2).cancelImmediately();
            }
        }
        if (prepareSyncedVibration && !z2) {
            this.conductor.vibratorManagerHooks.cancelSyncedVibration();
        }
        if (z) {
            return -1L;
        }
        return j;
    }

    public final long startVibrating(AbstractVibratorStep abstractVibratorStep, List<Step> list) {
        list.addAll(abstractVibratorStep.play());
        long vibratorOnDuration = abstractVibratorStep.getVibratorOnDuration();
        return vibratorOnDuration < 0 ? vibratorOnDuration : Math.max(vibratorOnDuration, abstractVibratorStep.effect.getDuration());
    }

    /* loaded from: classes2.dex */
    public final class DeviceEffectMap {
        public final long mRequiredSyncCapabilities;
        public final SparseArray<VibrationEffect.Composed> mVibratorEffects;
        public final int[] mVibratorIds;

        public final boolean requireMixedTriggerCapability(long j, long j2) {
            return ((j & j2) == 0 || (j & (~j2)) == 0) ? false : true;
        }

        public DeviceEffectMap(CombinedVibration.Mono mono) {
            SparseArray<VibratorController> vibrators = StartSequentialEffectStep.this.conductor.getVibrators();
            this.mVibratorEffects = new SparseArray<>(vibrators.size());
            this.mVibratorIds = new int[vibrators.size()];
            for (int i = 0; i < vibrators.size(); i++) {
                int keyAt = vibrators.keyAt(i);
                VibrationEffect.Composed apply = StartSequentialEffectStep.this.conductor.deviceEffectAdapter.apply(mono.getEffect(), vibrators.valueAt(i).getVibratorInfo());
                if (apply instanceof VibrationEffect.Composed) {
                    this.mVibratorEffects.put(keyAt, apply);
                    this.mVibratorIds[i] = keyAt;
                }
            }
            this.mRequiredSyncCapabilities = calculateRequiredSyncCapabilities(this.mVibratorEffects);
        }

        public DeviceEffectMap(CombinedVibration.Stereo stereo) {
            SparseArray<VibratorController> vibrators = StartSequentialEffectStep.this.conductor.getVibrators();
            SparseArray effects = stereo.getEffects();
            this.mVibratorEffects = new SparseArray<>();
            for (int i = 0; i < effects.size(); i++) {
                int keyAt = effects.keyAt(i);
                if (vibrators.contains(keyAt)) {
                    VibrationEffect.Composed apply = StartSequentialEffectStep.this.conductor.deviceEffectAdapter.apply((VibrationEffect) effects.valueAt(i), vibrators.valueAt(i).getVibratorInfo());
                    if (apply instanceof VibrationEffect.Composed) {
                        this.mVibratorEffects.put(keyAt, apply);
                    }
                }
            }
            this.mVibratorIds = new int[this.mVibratorEffects.size()];
            for (int i2 = 0; i2 < this.mVibratorEffects.size(); i2++) {
                this.mVibratorIds[i2] = this.mVibratorEffects.keyAt(i2);
            }
            this.mRequiredSyncCapabilities = calculateRequiredSyncCapabilities(this.mVibratorEffects);
        }

        public int size() {
            return this.mVibratorIds.length;
        }

        public long getRequiredSyncCapabilities() {
            return this.mRequiredSyncCapabilities;
        }

        public int[] getVibratorIds() {
            return this.mVibratorIds;
        }

        public int vibratorIdAt(int i) {
            return this.mVibratorEffects.keyAt(i);
        }

        public VibrationEffect.Composed effectAt(int i) {
            return this.mVibratorEffects.valueAt(i);
        }

        public final long calculateRequiredSyncCapabilities(SparseArray<VibrationEffect.Composed> sparseArray) {
            long j = 0;
            for (int i = 0; i < sparseArray.size(); i++) {
                VibrationEffectSegment vibrationEffectSegment = (VibrationEffectSegment) sparseArray.valueAt(i).getSegments().get(0);
                if (vibrationEffectSegment instanceof StepSegment) {
                    j |= 2;
                } else if (vibrationEffectSegment instanceof PrebakedSegment) {
                    j |= 4;
                } else if (vibrationEffectSegment instanceof PrimitiveSegment) {
                    j |= 8;
                }
            }
            int i2 = requireMixedTriggerCapability(j, 2L) ? 16 : 0;
            if (requireMixedTriggerCapability(j, 4L)) {
                i2 |= 32;
            }
            if (requireMixedTriggerCapability(j, 8L)) {
                i2 |= 64;
            }
            return 1 | j | i2;
        }
    }
}
