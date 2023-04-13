package com.android.server.vibrator;

import android.os.VibratorInfo;
import android.os.vibrator.RampSegment;
import android.os.vibrator.StepSegment;
import android.os.vibrator.VibrationEffectSegment;
import com.android.server.vibrator.VibrationEffectAdapters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public final class RampDownAdapter implements VibrationEffectAdapters.SegmentsAdapter<VibratorInfo> {
    public final int mRampDownDuration;
    public final int mStepDuration;

    @Override // com.android.server.vibrator.VibrationEffectAdapters.SegmentsAdapter
    public /* bridge */ /* synthetic */ int apply(List list, int i, VibratorInfo vibratorInfo) {
        return apply2((List<VibrationEffectSegment>) list, i, vibratorInfo);
    }

    public RampDownAdapter(int i, int i2) {
        this.mRampDownDuration = i;
        this.mStepDuration = i2;
    }

    /* renamed from: apply  reason: avoid collision after fix types in other method */
    public int apply2(List<VibrationEffectSegment> list, int i, VibratorInfo vibratorInfo) {
        return this.mRampDownDuration <= 0 ? i : addRampDownToLoop(list, addRampDownToZeroAmplitudeSegments(list, i));
    }

    public final int addRampDownToZeroAmplitudeSegments(List<VibrationEffectSegment> list, int i) {
        List<VibrationEffectSegment> list2;
        int size = list.size();
        int i2 = 1;
        while (i2 < size) {
            StepSegment stepSegment = (VibrationEffectSegment) list.get(i2 - 1);
            if (isOffSegment(list.get(i2)) && endsWithNonZeroAmplitude(stepSegment)) {
                long duration = list.get(i2).getDuration();
                if (stepSegment instanceof StepSegment) {
                    StepSegment stepSegment2 = stepSegment;
                    list2 = createStepsDown(stepSegment2.getAmplitude(), stepSegment2.getFrequencyHz(), duration);
                } else if (stepSegment instanceof RampSegment) {
                    RampSegment rampSegment = (RampSegment) stepSegment;
                    float endAmplitude = rampSegment.getEndAmplitude();
                    float endFrequencyHz = rampSegment.getEndFrequencyHz();
                    int i3 = this.mRampDownDuration;
                    if (duration <= i3) {
                        list2 = Arrays.asList(createRampDown(endAmplitude, endFrequencyHz, duration));
                    } else {
                        list2 = Arrays.asList(createRampDown(endAmplitude, endFrequencyHz, i3), createRampDown(0.0f, endFrequencyHz, duration - this.mRampDownDuration));
                    }
                } else {
                    list2 = null;
                }
                if (list2 != null) {
                    int size2 = list2.size() - 1;
                    VibrationEffectSegment remove = list.remove(i2);
                    list.addAll(i2, list2);
                    if (i >= i2) {
                        if (i == i2) {
                            list.add(remove);
                            i++;
                            size++;
                        }
                        i += size2;
                    }
                    i2 += size2;
                    size += size2;
                }
            }
            i2++;
        }
        return i;
    }

    public final int addRampDownToLoop(List<VibrationEffectSegment> list, int i) {
        if (i < 0) {
            return i;
        }
        int size = list.size() - 1;
        if (endsWithNonZeroAmplitude(list.get(size)) && isOffSegment(list.get(i))) {
            StepSegment stepSegment = (VibrationEffectSegment) list.get(size);
            VibrationEffectSegment vibrationEffectSegment = list.get(i);
            long duration = vibrationEffectSegment.getDuration();
            int i2 = this.mRampDownDuration;
            if (duration > i2) {
                list.set(i, updateDuration(vibrationEffectSegment, duration - i2));
                list.add(i, updateDuration(vibrationEffectSegment, this.mRampDownDuration));
            }
            i++;
            if (stepSegment instanceof StepSegment) {
                StepSegment stepSegment2 = stepSegment;
                list.addAll(createStepsDown(stepSegment2.getAmplitude(), stepSegment2.getFrequencyHz(), Math.min(duration, this.mRampDownDuration)));
            } else if (stepSegment instanceof RampSegment) {
                RampSegment rampSegment = (RampSegment) stepSegment;
                list.add(createRampDown(rampSegment.getEndAmplitude(), rampSegment.getEndFrequencyHz(), Math.min(duration, this.mRampDownDuration)));
            }
        }
        return i;
    }

    public final List<VibrationEffectSegment> createStepsDown(float f, float f2, long j) {
        int min = ((int) Math.min(j, this.mRampDownDuration)) / this.mStepDuration;
        float f3 = f / min;
        ArrayList arrayList = new ArrayList();
        for (int i = 1; i < min; i++) {
            arrayList.add(new StepSegment(f - (i * f3), f2, this.mStepDuration));
        }
        arrayList.add(new StepSegment(0.0f, f2, ((int) j) - (this.mStepDuration * (min - 1))));
        return arrayList;
    }

    public static RampSegment createRampDown(float f, float f2, long j) {
        return new RampSegment(f, 0.0f, f2, f2, (int) j);
    }

    public static VibrationEffectSegment updateDuration(VibrationEffectSegment vibrationEffectSegment, long j) {
        if (vibrationEffectSegment instanceof RampSegment) {
            RampSegment rampSegment = (RampSegment) vibrationEffectSegment;
            return new RampSegment(rampSegment.getStartAmplitude(), rampSegment.getEndAmplitude(), rampSegment.getStartFrequencyHz(), rampSegment.getEndFrequencyHz(), (int) j);
        } else if (vibrationEffectSegment instanceof StepSegment) {
            StepSegment stepSegment = (StepSegment) vibrationEffectSegment;
            return new StepSegment(stepSegment.getAmplitude(), stepSegment.getFrequencyHz(), (int) j);
        } else {
            return vibrationEffectSegment;
        }
    }

    public static boolean isOffSegment(VibrationEffectSegment vibrationEffectSegment) {
        if (vibrationEffectSegment instanceof StepSegment) {
            return ((StepSegment) vibrationEffectSegment).getAmplitude() == 0.0f;
        } else if (vibrationEffectSegment instanceof RampSegment) {
            RampSegment rampSegment = (RampSegment) vibrationEffectSegment;
            return rampSegment.getStartAmplitude() == 0.0f && rampSegment.getEndAmplitude() == 0.0f;
        } else {
            return false;
        }
    }

    public static boolean endsWithNonZeroAmplitude(VibrationEffectSegment vibrationEffectSegment) {
        return vibrationEffectSegment instanceof StepSegment ? ((StepSegment) vibrationEffectSegment).getAmplitude() != 0.0f : (vibrationEffectSegment instanceof RampSegment) && ((RampSegment) vibrationEffectSegment).getEndAmplitude() != 0.0f;
    }
}
