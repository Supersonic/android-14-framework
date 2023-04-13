package android.p008os.vibrator;

import android.p008os.VibratorInfo;
import com.android.internal.util.Preconditions;
/* renamed from: android.os.vibrator.VibratorFrequencyProfile */
/* loaded from: classes3.dex */
public final class VibratorFrequencyProfile {
    private final VibratorInfo.FrequencyProfile mFrequencyProfile;

    public VibratorFrequencyProfile(VibratorInfo.FrequencyProfile frequencyProfile) {
        Preconditions.checkArgument(!frequencyProfile.isEmpty(), "Frequency profile must have a non-empty frequency range");
        this.mFrequencyProfile = frequencyProfile;
    }

    public float[] getMaxAmplitudeMeasurements() {
        return this.mFrequencyProfile.getMaxAmplitudes();
    }

    public float getMaxAmplitudeMeasurementInterval() {
        return this.mFrequencyProfile.getFrequencyResolutionHz();
    }

    public float getMinFrequency() {
        return this.mFrequencyProfile.getFrequencyRangeHz().getLower().floatValue();
    }

    public float getMaxFrequency() {
        return this.mFrequencyProfile.getFrequencyRangeHz().getUpper().floatValue();
    }
}
