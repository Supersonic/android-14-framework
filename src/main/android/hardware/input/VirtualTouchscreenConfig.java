package android.hardware.input;

import android.annotation.SystemApi;
import android.hardware.input.VirtualInputDeviceConfig;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes2.dex */
public final class VirtualTouchscreenConfig extends VirtualInputDeviceConfig implements Parcelable {
    public static final Parcelable.Creator<VirtualTouchscreenConfig> CREATOR = new Parcelable.Creator<VirtualTouchscreenConfig>() { // from class: android.hardware.input.VirtualTouchscreenConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualTouchscreenConfig createFromParcel(Parcel in) {
            return new VirtualTouchscreenConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualTouchscreenConfig[] newArray(int size) {
            return new VirtualTouchscreenConfig[size];
        }
    };
    private final int mHeight;
    private final int mWidth;

    private VirtualTouchscreenConfig(Builder builder) {
        super(builder);
        this.mWidth = builder.mWidth;
        this.mHeight = builder.mHeight;
    }

    private VirtualTouchscreenConfig(Parcel in) {
        super(in);
        this.mWidth = in.readInt();
        this.mHeight = in.readInt();
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.hardware.input.VirtualInputDeviceConfig, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
    }

    /* loaded from: classes2.dex */
    public static final class Builder extends VirtualInputDeviceConfig.Builder<Builder> {
        private int mHeight;
        private int mWidth;

        public Builder(int touchscreenWidth, int touchscreenHeight) {
            if (touchscreenHeight <= 0 || touchscreenWidth <= 0) {
                throw new IllegalArgumentException("Cannot create a virtual touchscreen, touchscreen dimensions must be positive. Got: (" + touchscreenHeight + ", " + touchscreenWidth + NavigationBarInflaterView.KEY_CODE_END);
            }
            this.mHeight = touchscreenHeight;
            this.mWidth = touchscreenWidth;
        }

        public VirtualTouchscreenConfig build() {
            return new VirtualTouchscreenConfig(this);
        }
    }
}
