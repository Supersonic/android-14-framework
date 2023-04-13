package android.p008os.vibrator;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.VibrationEffect;
import android.p008os.Vibrator;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* renamed from: android.os.vibrator.StepSegment */
/* loaded from: classes3.dex */
public final class StepSegment extends VibrationEffectSegment {
    public static final Parcelable.Creator<StepSegment> CREATOR = new Parcelable.Creator<StepSegment>() { // from class: android.os.vibrator.StepSegment.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StepSegment createFromParcel(Parcel in) {
            in.readInt();
            return new StepSegment(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StepSegment[] newArray(int size) {
            return new StepSegment[size];
        }
    };
    private final float mAmplitude;
    private final int mDuration;
    private final float mFrequencyHz;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StepSegment(Parcel in) {
        this(in.readFloat(), in.readFloat(), in.readInt());
    }

    public StepSegment(float amplitude, float frequencyHz, int duration) {
        this.mAmplitude = amplitude;
        this.mFrequencyHz = frequencyHz;
        this.mDuration = duration;
    }

    public boolean equals(Object o) {
        if (o instanceof StepSegment) {
            StepSegment other = (StepSegment) o;
            return Float.compare(this.mAmplitude, other.mAmplitude) == 0 && Float.compare(this.mFrequencyHz, other.mFrequencyHz) == 0 && this.mDuration == other.mDuration;
        }
        return false;
    }

    public float getAmplitude() {
        return this.mAmplitude;
    }

    public float getFrequencyHz() {
        return this.mFrequencyHz;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public long getDuration() {
        return this.mDuration;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public boolean areVibrationFeaturesSupported(Vibrator vibrator) {
        boolean areFeaturesSupported = frequencyRequiresFrequencyControl(this.mFrequencyHz) ? true & vibrator.hasFrequencyControl() : true;
        if (amplitudeRequiresAmplitudeControl(this.mAmplitude)) {
            return areFeaturesSupported & vibrator.hasAmplitudeControl();
        }
        return areFeaturesSupported;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public boolean isHapticFeedbackCandidate() {
        return true;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public boolean hasNonZeroAmplitude() {
        return Float.compare(this.mAmplitude, 0.0f) != 0;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public void validate() {
        VibrationEffectSegment.checkFrequencyArgument(this.mFrequencyHz, "frequencyHz");
        VibrationEffectSegment.checkDurationArgument(this.mDuration, "duration");
        if (Float.compare(this.mAmplitude, -1.0f) != 0) {
            Preconditions.checkArgumentInRange(this.mAmplitude, 0.0f, 1.0f, "amplitude");
        }
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public StepSegment resolve(int defaultAmplitude) {
        if (defaultAmplitude > 255 || defaultAmplitude <= 0) {
            throw new IllegalArgumentException("amplitude must be between 1 and 255 inclusive (amplitude=" + defaultAmplitude + NavigationBarInflaterView.KEY_CODE_END);
        }
        if (Float.compare(this.mAmplitude, -1.0f) != 0) {
            return this;
        }
        return new StepSegment(defaultAmplitude / 255.0f, this.mFrequencyHz, this.mDuration);
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public StepSegment scale(float scaleFactor) {
        if (Float.compare(this.mAmplitude, -1.0f) == 0) {
            return this;
        }
        return new StepSegment(VibrationEffect.scale(this.mAmplitude, scaleFactor), this.mFrequencyHz, this.mDuration);
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public StepSegment applyEffectStrength(int effectStrength) {
        return this;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.mAmplitude), Float.valueOf(this.mFrequencyHz), Integer.valueOf(this.mDuration));
    }

    public String toString() {
        return "Step{amplitude=" + this.mAmplitude + ", frequencyHz=" + this.mFrequencyHz + ", duration=" + this.mDuration + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(3);
        out.writeFloat(this.mAmplitude);
        out.writeFloat(this.mFrequencyHz);
        out.writeInt(this.mDuration);
    }
}
