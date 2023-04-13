package com.android.server.vibrator;

import android.os.VibrationEffect;
import android.os.VibratorInfo;
import com.android.server.vibrator.VibrationEffectAdapters;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public final class DeviceVibrationEffectAdapter {
    public final List<VibrationEffectAdapters.SegmentsAdapter<VibratorInfo>> mSegmentAdapters;

    public DeviceVibrationEffectAdapter(VibrationSettings vibrationSettings) {
        this.mSegmentAdapters = Arrays.asList(new RampToStepAdapter(vibrationSettings.getRampStepDuration()), new StepToRampAdapter(), new RampDownAdapter(vibrationSettings.getRampDownDuration(), vibrationSettings.getRampStepDuration()), new ClippingAmplitudeAndFrequencyAdapter());
    }

    public VibrationEffect apply(VibrationEffect vibrationEffect, VibratorInfo vibratorInfo) {
        return VibrationEffectAdapters.apply(vibrationEffect, this.mSegmentAdapters, vibratorInfo);
    }
}
