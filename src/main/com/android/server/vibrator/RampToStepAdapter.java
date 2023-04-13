package com.android.server.vibrator;

import android.os.VibratorInfo;
import android.os.vibrator.RampSegment;
import android.os.vibrator.StepSegment;
import android.os.vibrator.VibrationEffectSegment;
import android.util.MathUtils;
import com.android.server.vibrator.VibrationEffectAdapters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public final class RampToStepAdapter implements VibrationEffectAdapters.SegmentsAdapter<VibratorInfo> {
    public final int mStepDuration;

    @Override // com.android.server.vibrator.VibrationEffectAdapters.SegmentsAdapter
    public /* bridge */ /* synthetic */ int apply(List list, int i, VibratorInfo vibratorInfo) {
        return apply2((List<VibrationEffectSegment>) list, i, vibratorInfo);
    }

    public RampToStepAdapter(int i) {
        this.mStepDuration = i;
    }

    /* renamed from: apply  reason: avoid collision after fix types in other method */
    public int apply2(List<VibrationEffectSegment> list, int i, VibratorInfo vibratorInfo) {
        if (vibratorInfo.hasCapability(1024L)) {
            return i;
        }
        int size = list.size();
        int i2 = 0;
        while (i2 < size) {
            VibrationEffectSegment vibrationEffectSegment = list.get(i2);
            if (vibrationEffectSegment instanceof RampSegment) {
                List<StepSegment> apply = apply(vibratorInfo, (RampSegment) vibrationEffectSegment);
                list.remove(i2);
                list.addAll(i2, apply);
                int size2 = apply.size() - 1;
                if (i > i2) {
                    i += size2;
                }
                i2 += size2;
                size += size2;
            }
            i2++;
        }
        return i;
    }

    public final List<StepSegment> apply(VibratorInfo vibratorInfo, RampSegment rampSegment) {
        if (Float.compare(rampSegment.getStartAmplitude(), rampSegment.getEndAmplitude()) == 0) {
            return Arrays.asList(new StepSegment(rampSegment.getStartAmplitude(), fillEmptyFrequency(vibratorInfo, rampSegment.getStartFrequencyHz()), (int) rampSegment.getDuration()));
        }
        ArrayList arrayList = new ArrayList();
        long duration = rampSegment.getDuration();
        int i = this.mStepDuration;
        int i2 = ((int) ((duration + i) - 1)) / i;
        int i3 = 0;
        while (true) {
            int i4 = i2 - 1;
            if (i3 < i4) {
                float f = i3 / i2;
                arrayList.add(new StepSegment(MathUtils.lerp(rampSegment.getStartAmplitude(), rampSegment.getEndAmplitude(), f), MathUtils.lerp(fillEmptyFrequency(vibratorInfo, rampSegment.getStartFrequencyHz()), fillEmptyFrequency(vibratorInfo, rampSegment.getEndFrequencyHz()), f), this.mStepDuration));
                i3++;
            } else {
                arrayList.add(new StepSegment(rampSegment.getEndAmplitude(), fillEmptyFrequency(vibratorInfo, rampSegment.getEndFrequencyHz()), ((int) rampSegment.getDuration()) - (this.mStepDuration * i4)));
                return arrayList;
            }
        }
    }

    public static float fillEmptyFrequency(VibratorInfo vibratorInfo, float f) {
        return f == 0.0f ? vibratorInfo.getResonantFrequencyHz() : f;
    }
}
