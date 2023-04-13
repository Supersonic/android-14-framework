package android.p008os.vibrator;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Vibrator;
/* renamed from: android.os.vibrator.VibrationEffectSegment */
/* loaded from: classes3.dex */
public abstract class VibrationEffectSegment implements Parcelable {
    public static final Parcelable.Creator<VibrationEffectSegment> CREATOR = new Parcelable.Creator<VibrationEffectSegment>() { // from class: android.os.vibrator.VibrationEffectSegment.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VibrationEffectSegment createFromParcel(Parcel in) {
            switch (in.readInt()) {
                case 1:
                    return new PrebakedSegment(in);
                case 2:
                    return new PrimitiveSegment(in);
                case 3:
                    return new StepSegment(in);
                case 4:
                    return new RampSegment(in);
                default:
                    throw new IllegalStateException("Unexpected vibration event type token in parcel.");
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VibrationEffectSegment[] newArray(int size) {
            return new VibrationEffectSegment[size];
        }
    };
    static final int PARCEL_TOKEN_PREBAKED = 1;
    static final int PARCEL_TOKEN_PRIMITIVE = 2;
    static final int PARCEL_TOKEN_RAMP = 4;
    static final int PARCEL_TOKEN_STEP = 3;

    public abstract <T extends VibrationEffectSegment> T applyEffectStrength(int i);

    public abstract boolean areVibrationFeaturesSupported(Vibrator vibrator);

    public abstract long getDuration();

    public abstract boolean hasNonZeroAmplitude();

    public abstract boolean isHapticFeedbackCandidate();

    public abstract <T extends VibrationEffectSegment> T resolve(int i);

    public abstract <T extends VibrationEffectSegment> T scale(float f);

    public abstract void validate();

    public static void checkFrequencyArgument(float value, String name) {
        if (Float.isNaN(value)) {
            throw new IllegalArgumentException(name + " must not be NaN");
        }
        if (Float.isInfinite(value)) {
            throw new IllegalArgumentException(name + " must not be infinite");
        }
        if (value < 0.0f) {
            throw new IllegalArgumentException(name + " must be >= 0, got " + value);
        }
    }

    public static void checkDurationArgument(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must be >= 0, got " + value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean amplitudeRequiresAmplitudeControl(float amplitude) {
        return (amplitude == 0.0f || amplitude == 1.0f || amplitude == -1.0f) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean frequencyRequiresFrequencyControl(float frequency) {
        return frequency != 0.0f;
    }
}
