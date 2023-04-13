package android.hardware.input;

import android.annotation.SystemApi;
import android.hardware.input.VirtualInputDeviceConfig;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes2.dex */
public final class VirtualNavigationTouchpadConfig extends VirtualInputDeviceConfig implements Parcelable {
    public static final Parcelable.Creator<VirtualNavigationTouchpadConfig> CREATOR = new Parcelable.Creator<VirtualNavigationTouchpadConfig>() { // from class: android.hardware.input.VirtualNavigationTouchpadConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualNavigationTouchpadConfig createFromParcel(Parcel in) {
            return new VirtualNavigationTouchpadConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualNavigationTouchpadConfig[] newArray(int size) {
            return new VirtualNavigationTouchpadConfig[size];
        }
    };
    private final int mHeight;
    private final int mWidth;

    private VirtualNavigationTouchpadConfig(Builder builder) {
        super(builder);
        this.mHeight = builder.mHeight;
        this.mWidth = builder.mWidth;
    }

    private VirtualNavigationTouchpadConfig(Parcel in) {
        super(in);
        this.mHeight = in.readInt();
        this.mWidth = in.readInt();
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getWidth() {
        return this.mWidth;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.hardware.input.VirtualInputDeviceConfig, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mWidth);
    }

    /* loaded from: classes2.dex */
    public static final class Builder extends VirtualInputDeviceConfig.Builder<Builder> {
        private final int mHeight;
        private final int mWidth;

        public Builder(int touchpadWidth, int touchpadHeight) {
            if (touchpadHeight <= 0 || touchpadWidth <= 0) {
                throw new IllegalArgumentException("Cannot create a virtual navigation touchpad, touchpad dimensions must be positive. Got: (" + touchpadHeight + ", " + touchpadWidth + NavigationBarInflaterView.KEY_CODE_END);
            }
            this.mHeight = touchpadHeight;
            this.mWidth = touchpadWidth;
        }

        public VirtualNavigationTouchpadConfig build() {
            return new VirtualNavigationTouchpadConfig(this);
        }
    }
}
