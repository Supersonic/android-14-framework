package com.android.server.vibrator;

import android.os.VibratorInfo;
import android.os.vibrator.RampSegment;
import android.os.vibrator.StepSegment;
import android.os.vibrator.VibrationEffectSegment;
import android.util.MathUtils;
import com.android.server.vibrator.VibrationEffectAdapters;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class StepToRampAdapter implements VibrationEffectAdapters.SegmentsAdapter<VibratorInfo> {
    @Override // com.android.server.vibrator.VibrationEffectAdapters.SegmentsAdapter
    public /* bridge */ /* synthetic */ int apply(List list, int i, VibratorInfo vibratorInfo) {
        return apply2((List<VibrationEffectSegment>) list, i, vibratorInfo);
    }

    /* renamed from: apply  reason: avoid collision after fix types in other method */
    public int apply2(List<VibrationEffectSegment> list, int i, VibratorInfo vibratorInfo) {
        if (vibratorInfo.hasCapability(1024L)) {
            convertStepsToRamps(vibratorInfo, list);
            return splitLongRampSegments(vibratorInfo, list, i);
        }
        return i;
    }

    public final void convertStepsToRamps(VibratorInfo vibratorInfo, List<VibrationEffectSegment> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            StepSegment stepSegment = (VibrationEffectSegment) list.get(i);
            if (isStep(stepSegment)) {
                StepSegment stepSegment2 = stepSegment;
                if (stepSegment2.getFrequencyHz() != 0.0f) {
                    list.set(i, convertStepToRamp(vibratorInfo, stepSegment2));
                }
            }
        }
        for (int i2 = 0; i2 < size; i2++) {
            if (list.get(i2) instanceof RampSegment) {
                for (int i3 = i2 - 1; i3 >= 0 && isStep(list.get(i3)); i3--) {
                    list.set(i3, convertStepToRamp(vibratorInfo, list.get(i3)));
                }
                for (int i4 = i2 + 1; i4 < size && isStep(list.get(i4)); i4++) {
                    list.set(i4, convertStepToRamp(vibratorInfo, list.get(i4)));
                }
            }
        }
    }

    public final int splitLongRampSegments(VibratorInfo vibratorInfo, List<VibrationEffectSegment> list, int i) {
        int pwlePrimitiveDurationMax = vibratorInfo.getPwlePrimitiveDurationMax();
        if (pwlePrimitiveDurationMax <= 0) {
            return i;
        }
        int size = list.size();
        int i2 = 0;
        while (i2 < size) {
            if (list.get(i2) instanceof RampSegment) {
                RampSegment rampSegment = list.get(i2);
                int duration = ((((int) rampSegment.getDuration()) + pwlePrimitiveDurationMax) - 1) / pwlePrimitiveDurationMax;
                if (duration > 1) {
                    list.remove(i2);
                    list.addAll(i2, splitRampSegment(vibratorInfo, rampSegment, duration));
                    int i3 = duration - 1;
                    if (i > i2) {
                        i += i3;
                    }
                    i2 += i3;
                    size += i3;
                }
            }
            i2++;
        }
        return i;
    }

    public static RampSegment convertStepToRamp(VibratorInfo vibratorInfo, StepSegment stepSegment) {
        float fillEmptyFrequency = fillEmptyFrequency(vibratorInfo, stepSegment.getFrequencyHz());
        return new RampSegment(stepSegment.getAmplitude(), stepSegment.getAmplitude(), fillEmptyFrequency, fillEmptyFrequency, (int) stepSegment.getDuration());
    }

    public static List<RampSegment> splitRampSegment(VibratorInfo vibratorInfo, RampSegment rampSegment, int i) {
        ArrayList arrayList = new ArrayList(i);
        float fillEmptyFrequency = fillEmptyFrequency(vibratorInfo, rampSegment.getStartFrequencyHz());
        float fillEmptyFrequency2 = fillEmptyFrequency(vibratorInfo, rampSegment.getEndFrequencyHz());
        long duration = rampSegment.getDuration() / i;
        long j = 0;
        float startAmplitude = rampSegment.getStartAmplitude();
        float f = fillEmptyFrequency;
        for (int i2 = 1; i2 < i; i2++) {
            j += duration;
            float duration2 = ((float) j) / ((float) rampSegment.getDuration());
            RampSegment rampSegment2 = new RampSegment(startAmplitude, MathUtils.lerp(rampSegment.getStartAmplitude(), rampSegment.getEndAmplitude(), duration2), f, MathUtils.lerp(fillEmptyFrequency, fillEmptyFrequency2, duration2), (int) duration);
            arrayList.add(rampSegment2);
            startAmplitude = rampSegment2.getEndAmplitude();
            f = rampSegment2.getEndFrequencyHz();
        }
        arrayList.add(new RampSegment(startAmplitude, rampSegment.getEndAmplitude(), f, fillEmptyFrequency2, (int) (rampSegment.getDuration() - j)));
        return arrayList;
    }

    public static boolean isStep(VibrationEffectSegment vibrationEffectSegment) {
        return vibrationEffectSegment instanceof StepSegment;
    }

    public static float fillEmptyFrequency(VibratorInfo vibratorInfo, float f) {
        return f == 0.0f ? vibratorInfo.getResonantFrequencyHz() : f;
    }
}
