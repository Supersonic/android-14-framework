package android.hardware.input;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes2.dex */
public final class VirtualMouseRelativeEvent implements Parcelable {
    public static final Parcelable.Creator<VirtualMouseRelativeEvent> CREATOR = new Parcelable.Creator<VirtualMouseRelativeEvent>() { // from class: android.hardware.input.VirtualMouseRelativeEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualMouseRelativeEvent createFromParcel(Parcel source) {
            return new VirtualMouseRelativeEvent(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualMouseRelativeEvent[] newArray(int size) {
            return new VirtualMouseRelativeEvent[size];
        }
    };
    private final float mRelativeX;
    private final float mRelativeY;

    private VirtualMouseRelativeEvent(float relativeX, float relativeY) {
        this.mRelativeX = relativeX;
        this.mRelativeY = relativeY;
    }

    private VirtualMouseRelativeEvent(Parcel parcel) {
        this.mRelativeX = parcel.readFloat();
        this.mRelativeY = parcel.readFloat();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeFloat(this.mRelativeX);
        parcel.writeFloat(this.mRelativeY);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public float getRelativeX() {
        return this.mRelativeX;
    }

    public float getRelativeY() {
        return this.mRelativeY;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private float mRelativeX;
        private float mRelativeY;

        public VirtualMouseRelativeEvent build() {
            return new VirtualMouseRelativeEvent(this.mRelativeX, this.mRelativeY);
        }

        public Builder setRelativeX(float relativeX) {
            this.mRelativeX = relativeX;
            return this;
        }

        public Builder setRelativeY(float relativeY) {
            this.mRelativeY = relativeY;
            return this;
        }
    }
}
