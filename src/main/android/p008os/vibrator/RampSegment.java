package android.p008os.vibrator;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.VibrationEffect;
import android.p008os.Vibrator;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* renamed from: android.os.vibrator.RampSegment */
/* loaded from: classes3.dex */
public final class RampSegment extends VibrationEffectSegment {
    public static final Parcelable.Creator<RampSegment> CREATOR = new Parcelable.Creator<RampSegment>() { // from class: android.os.vibrator.RampSegment.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RampSegment createFromParcel(Parcel in) {
            in.readInt();
            return new RampSegment(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RampSegment[] newArray(int size) {
            return new RampSegment[size];
        }
    };
    private final int mDuration;
    private final float mEndAmplitude;
    private final float mEndFrequencyHz;
    private final float mStartAmplitude;
    private final float mStartFrequencyHz;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RampSegment(Parcel in) {
        this(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat(), in.readInt());
    }

    public RampSegment(float startAmplitude, float endAmplitude, float startFrequencyHz, float endFrequencyHz, int duration) {
        this.mStartAmplitude = startAmplitude;
        this.mEndAmplitude = endAmplitude;
        this.mStartFrequencyHz = startFrequencyHz;
        this.mEndFrequencyHz = endFrequencyHz;
        this.mDuration = duration;
    }

    public boolean equals(Object o) {
        if (o instanceof RampSegment) {
            RampSegment other = (RampSegment) o;
            return Float.compare(this.mStartAmplitude, other.mStartAmplitude) == 0 && Float.compare(this.mEndAmplitude, other.mEndAmplitude) == 0 && Float.compare(this.mStartFrequencyHz, other.mStartFrequencyHz) == 0 && Float.compare(this.mEndFrequencyHz, other.mEndFrequencyHz) == 0 && this.mDuration == other.mDuration;
        }
        return false;
    }

    public float getStartAmplitude() {
        return this.mStartAmplitude;
    }

    public float getEndAmplitude() {
        return this.mEndAmplitude;
    }

    public float getStartFrequencyHz() {
        return this.mStartFrequencyHz;
    }

    public float getEndFrequencyHz() {
        return this.mEndFrequencyHz;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public long getDuration() {
        return this.mDuration;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public boolean areVibrationFeaturesSupported(Vibrator vibrator) {
        boolean areFeaturesSupported = true;
        float f = this.mStartFrequencyHz;
        if (f != this.mEndFrequencyHz || frequencyRequiresFrequencyControl(f)) {
            areFeaturesSupported = true & vibrator.hasFrequencyControl();
        }
        float f2 = this.mStartAmplitude;
        if (f2 != this.mEndAmplitude || amplitudeRequiresAmplitudeControl(f2)) {
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
        return this.mStartAmplitude > 0.0f || this.mEndAmplitude > 0.0f;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public void validate() {
        VibrationEffectSegment.checkFrequencyArgument(this.mStartFrequencyHz, "startFrequencyHz");
        VibrationEffectSegment.checkFrequencyArgument(this.mEndFrequencyHz, "endFrequencyHz");
        VibrationEffectSegment.checkDurationArgument(this.mDuration, "duration");
        Preconditions.checkArgumentInRange(this.mStartAmplitude, 0.0f, 1.0f, "startAmplitude");
        Preconditions.checkArgumentInRange(this.mEndAmplitude, 0.0f, 1.0f, "endAmplitude");
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public RampSegment resolve(int defaultAmplitude) {
        return this;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public RampSegment scale(float scaleFactor) {
        float newStartAmplitude = VibrationEffect.scale(this.mStartAmplitude, scaleFactor);
        float newEndAmplitude = VibrationEffect.scale(this.mEndAmplitude, scaleFactor);
        if (Float.compare(this.mStartAmplitude, newStartAmplitude) == 0 && Float.compare(this.mEndAmplitude, newEndAmplitude) == 0) {
            return this;
        }
        return new RampSegment(newStartAmplitude, newEndAmplitude, this.mStartFrequencyHz, this.mEndFrequencyHz, this.mDuration);
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public RampSegment applyEffectStrength(int effectStrength) {
        return this;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.mStartAmplitude), Float.valueOf(this.mEndAmplitude), Float.valueOf(this.mStartFrequencyHz), Float.valueOf(this.mEndFrequencyHz), Integer.valueOf(this.mDuration));
    }

    public String toString() {
        return "Ramp{startAmplitude=" + this.mStartAmplitude + ", endAmplitude=" + this.mEndAmplitude + ", startFrequencyHz=" + this.mStartFrequencyHz + ", endFrequencyHz=" + this.mEndFrequencyHz + ", duration=" + this.mDuration + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(4);
        out.writeFloat(this.mStartAmplitude);
        out.writeFloat(this.mEndAmplitude);
        out.writeFloat(this.mStartFrequencyHz);
        out.writeFloat(this.mEndFrequencyHz);
        out.writeInt(this.mDuration);
    }
}
