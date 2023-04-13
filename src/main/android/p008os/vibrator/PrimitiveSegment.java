package android.p008os.vibrator;

import android.p008os.BatteryManager;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.VibrationEffect;
import android.p008os.Vibrator;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* renamed from: android.os.vibrator.PrimitiveSegment */
/* loaded from: classes3.dex */
public final class PrimitiveSegment extends VibrationEffectSegment {
    public static final Parcelable.Creator<PrimitiveSegment> CREATOR = new Parcelable.Creator<PrimitiveSegment>() { // from class: android.os.vibrator.PrimitiveSegment.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrimitiveSegment createFromParcel(Parcel in) {
            in.readInt();
            return new PrimitiveSegment(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrimitiveSegment[] newArray(int size) {
            return new PrimitiveSegment[size];
        }
    };
    private final int mDelay;
    private final int mPrimitiveId;
    private final float mScale;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrimitiveSegment(Parcel in) {
        this(in.readInt(), in.readFloat(), in.readInt());
    }

    public PrimitiveSegment(int id, float scale, int delay) {
        this.mPrimitiveId = id;
        this.mScale = scale;
        this.mDelay = delay;
    }

    public int getPrimitiveId() {
        return this.mPrimitiveId;
    }

    public float getScale() {
        return this.mScale;
    }

    public int getDelay() {
        return this.mDelay;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public long getDuration() {
        return -1L;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public boolean areVibrationFeaturesSupported(Vibrator vibrator) {
        return vibrator.areAllPrimitivesSupported(this.mPrimitiveId);
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public boolean isHapticFeedbackCandidate() {
        return true;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public boolean hasNonZeroAmplitude() {
        return true;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public PrimitiveSegment resolve(int defaultAmplitude) {
        return this;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public PrimitiveSegment scale(float scaleFactor) {
        return new PrimitiveSegment(this.mPrimitiveId, VibrationEffect.scale(this.mScale, scaleFactor), this.mDelay);
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public PrimitiveSegment applyEffectStrength(int effectStrength) {
        return this;
    }

    @Override // android.p008os.vibrator.VibrationEffectSegment
    public void validate() {
        Preconditions.checkArgumentInRange(this.mPrimitiveId, 0, 8, "primitiveId");
        Preconditions.checkArgumentInRange(this.mScale, 0.0f, 1.0f, BatteryManager.EXTRA_SCALE);
        VibrationEffectSegment.checkDurationArgument(this.mDelay, "delay");
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(2);
        dest.writeInt(this.mPrimitiveId);
        dest.writeFloat(this.mScale);
        dest.writeInt(this.mDelay);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "Primitive{primitive=" + VibrationEffect.Composition.primitiveToString(this.mPrimitiveId) + ", scale=" + this.mScale + ", delay=" + this.mDelay + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PrimitiveSegment that = (PrimitiveSegment) o;
        if (this.mPrimitiveId == that.mPrimitiveId && Float.compare(that.mScale, this.mScale) == 0 && this.mDelay == that.mDelay) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPrimitiveId), Float.valueOf(this.mScale), Integer.valueOf(this.mDelay));
    }
}
