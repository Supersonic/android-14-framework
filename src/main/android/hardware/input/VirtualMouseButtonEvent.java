package android.hardware.input;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes2.dex */
public final class VirtualMouseButtonEvent implements Parcelable {
    public static final int ACTION_BUTTON_PRESS = 11;
    public static final int ACTION_BUTTON_RELEASE = 12;
    public static final int ACTION_UNKNOWN = -1;
    public static final int BUTTON_BACK = 8;
    public static final int BUTTON_FORWARD = 16;
    public static final int BUTTON_PRIMARY = 1;
    public static final int BUTTON_SECONDARY = 2;
    public static final int BUTTON_TERTIARY = 4;
    public static final int BUTTON_UNKNOWN = -1;
    public static final Parcelable.Creator<VirtualMouseButtonEvent> CREATOR = new Parcelable.Creator<VirtualMouseButtonEvent>() { // from class: android.hardware.input.VirtualMouseButtonEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualMouseButtonEvent createFromParcel(Parcel source) {
            return new VirtualMouseButtonEvent(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualMouseButtonEvent[] newArray(int size) {
            return new VirtualMouseButtonEvent[size];
        }
    };
    private final int mAction;
    private final int mButtonCode;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Action {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Button {
    }

    private VirtualMouseButtonEvent(int action, int buttonCode) {
        this.mAction = action;
        this.mButtonCode = buttonCode;
    }

    private VirtualMouseButtonEvent(Parcel parcel) {
        this.mAction = parcel.readInt();
        this.mButtonCode = parcel.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeInt(this.mAction);
        parcel.writeInt(this.mButtonCode);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getButtonCode() {
        return this.mButtonCode;
    }

    public int getAction() {
        return this.mAction;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mAction = -1;
        private int mButtonCode = -1;

        public VirtualMouseButtonEvent build() {
            if (this.mAction == -1 || this.mButtonCode == -1) {
                throw new IllegalArgumentException("Cannot build virtual mouse button event with unset fields");
            }
            return new VirtualMouseButtonEvent(this.mAction, this.mButtonCode);
        }

        public Builder setButtonCode(int buttonCode) {
            if (buttonCode != 1 && buttonCode != 4 && buttonCode != 2 && buttonCode != 8 && buttonCode != 16) {
                throw new IllegalArgumentException("Unsupported mouse button code");
            }
            this.mButtonCode = buttonCode;
            return this;
        }

        public Builder setAction(int action) {
            if (action != 11 && action != 12) {
                throw new IllegalArgumentException("Unsupported mouse button action type");
            }
            this.mAction = action;
            return this;
        }
    }
}
