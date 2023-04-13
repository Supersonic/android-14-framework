package android.app;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
@SystemApi
/* loaded from: classes.dex */
public final class GameModeConfiguration implements Parcelable {
    public static final Parcelable.Creator<GameModeConfiguration> CREATOR = new Parcelable.Creator<GameModeConfiguration>() { // from class: android.app.GameModeConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameModeConfiguration createFromParcel(Parcel in) {
            return new GameModeConfiguration(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GameModeConfiguration[] newArray(int size) {
            return new GameModeConfiguration[size];
        }
    };
    public static final int FPS_OVERRIDE_NONE = 0;
    private final int mFpsOverride;
    private final float mScalingFactor;

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private int mFpsOverride;
        private float mScalingFactor;

        public Builder() {
        }

        public Builder(GameModeConfiguration configuration) {
            this.mFpsOverride = configuration.mFpsOverride;
            this.mScalingFactor = configuration.mScalingFactor;
        }

        public Builder setScalingFactor(float scalingFactor) {
            Preconditions.checkArgument(((double) scalingFactor) >= 0.1d && ((double) scalingFactor) <= 1.0d, "Scaling factor should fall between 0.1 and 1.0 (inclusive)");
            this.mScalingFactor = scalingFactor;
            return this;
        }

        public Builder setFpsOverride(int fpsOverride) {
            Preconditions.checkArgument(fpsOverride >= 0, "FPS override should be non-negative");
            this.mFpsOverride = fpsOverride;
            return this;
        }

        public GameModeConfiguration build() {
            return new GameModeConfiguration(this.mScalingFactor, this.mFpsOverride);
        }
    }

    GameModeConfiguration(float scalingFactor, int fpsOverride) {
        this.mScalingFactor = scalingFactor;
        this.mFpsOverride = fpsOverride;
    }

    GameModeConfiguration(Parcel in) {
        this.mScalingFactor = in.readFloat();
        this.mFpsOverride = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mScalingFactor);
        dest.writeInt(this.mFpsOverride);
    }

    public float getScalingFactor() {
        return this.mScalingFactor;
    }

    public int getFpsOverride() {
        return this.mFpsOverride;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof GameModeConfiguration) {
            GameModeConfiguration config = (GameModeConfiguration) obj;
            return config.mFpsOverride == this.mFpsOverride && config.mScalingFactor == this.mScalingFactor;
        }
        return false;
    }

    public int hashCode() {
        int result = (7 * 31) + this.mFpsOverride;
        return (result * 31) + Float.floatToIntBits(this.mScalingFactor);
    }
}
