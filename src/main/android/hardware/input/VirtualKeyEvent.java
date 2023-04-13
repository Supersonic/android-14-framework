package android.hardware.input;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes2.dex */
public final class VirtualKeyEvent implements Parcelable {
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UNKNOWN = -1;
    public static final int ACTION_UP = 1;
    public static final Parcelable.Creator<VirtualKeyEvent> CREATOR = new Parcelable.Creator<VirtualKeyEvent>() { // from class: android.hardware.input.VirtualKeyEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualKeyEvent createFromParcel(Parcel source) {
            return new VirtualKeyEvent(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualKeyEvent[] newArray(int size) {
            return new VirtualKeyEvent[size];
        }
    };
    private final int mAction;
    private final int mKeyCode;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Action {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SupportedKeycode {
    }

    private VirtualKeyEvent(int action, int keyCode) {
        this.mAction = action;
        this.mKeyCode = keyCode;
    }

    private VirtualKeyEvent(Parcel parcel) {
        this.mAction = parcel.readInt();
        this.mKeyCode = parcel.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int parcelableFlags) {
        parcel.writeInt(this.mAction);
        parcel.writeInt(this.mKeyCode);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getKeyCode() {
        return this.mKeyCode;
    }

    public int getAction() {
        return this.mAction;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mAction = -1;
        private int mKeyCode = -1;

        public VirtualKeyEvent build() {
            if (this.mAction == -1 || this.mKeyCode == -1) {
                throw new IllegalArgumentException("Cannot build virtual key event with unset fields");
            }
            return new VirtualKeyEvent(this.mAction, this.mKeyCode);
        }

        public Builder setKeyCode(int keyCode) {
            this.mKeyCode = keyCode;
            return this;
        }

        public Builder setAction(int action) {
            if (action != 0 && action != 1) {
                throw new IllegalArgumentException("Unsupported action type");
            }
            this.mAction = action;
            return this;
        }
    }
}
