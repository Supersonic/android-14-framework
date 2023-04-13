package android.hardware.lights;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes2.dex */
public final class LightState implements Parcelable {
    public static final Parcelable.Creator<LightState> CREATOR = new Parcelable.Creator<LightState>() { // from class: android.hardware.lights.LightState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LightState createFromParcel(Parcel in) {
            return new LightState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LightState[] newArray(int size) {
            return new LightState[size];
        }
    };
    private final int mColor;
    private final int mPlayerId;

    @SystemApi
    @Deprecated
    public LightState(int color) {
        this(color, 0);
    }

    public LightState(int color, int playerId) {
        this.mColor = color;
        this.mPlayerId = playerId;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mValue = 0;
        private boolean mIsForPlayerId = false;

        public Builder setColor(int color) {
            this.mIsForPlayerId = false;
            this.mValue = color;
            return this;
        }

        public Builder setPlayerId(int playerId) {
            this.mIsForPlayerId = true;
            this.mValue = playerId;
            return this;
        }

        public LightState build() {
            if (!this.mIsForPlayerId) {
                return new LightState(this.mValue, 0);
            }
            return new LightState(0, this.mValue);
        }
    }

    private LightState(Parcel in) {
        this.mColor = in.readInt();
        this.mPlayerId = in.readInt();
    }

    public int getColor() {
        return this.mColor;
    }

    public int getPlayerId() {
        return this.mPlayerId;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mColor);
        dest.writeInt(this.mPlayerId);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "LightState{Color=0x" + Integer.toHexString(this.mColor) + ", PlayerId=" + this.mPlayerId + "}";
    }
}
