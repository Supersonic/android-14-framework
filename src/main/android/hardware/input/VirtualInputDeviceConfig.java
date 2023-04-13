package android.hardware.input;

import android.annotation.SystemApi;
import android.p008os.Parcel;
@SystemApi
/* loaded from: classes2.dex */
public abstract class VirtualInputDeviceConfig {
    private final int mAssociatedDisplayId;
    private final String mInputDeviceName;
    private final int mProductId;
    private final int mVendorId;

    /* JADX INFO: Access modifiers changed from: protected */
    public VirtualInputDeviceConfig(Builder<? extends Builder<?>> builder) {
        this.mVendorId = ((Builder) builder).mVendorId;
        this.mProductId = ((Builder) builder).mProductId;
        this.mAssociatedDisplayId = ((Builder) builder).mAssociatedDisplayId;
        this.mInputDeviceName = ((Builder) builder).mInputDeviceName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VirtualInputDeviceConfig(Parcel in) {
        this.mVendorId = in.readInt();
        this.mProductId = in.readInt();
        this.mAssociatedDisplayId = in.readInt();
        this.mInputDeviceName = in.readString8();
    }

    public int getVendorId() {
        return this.mVendorId;
    }

    public int getProductId() {
        return this.mProductId;
    }

    public int getAssociatedDisplayId() {
        return this.mAssociatedDisplayId;
    }

    public String getInputDeviceName() {
        return this.mInputDeviceName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mVendorId);
        dest.writeInt(this.mProductId);
        dest.writeInt(this.mAssociatedDisplayId);
        dest.writeString8(this.mInputDeviceName);
    }

    /* loaded from: classes2.dex */
    public static abstract class Builder<T extends Builder<T>> {
        private int mAssociatedDisplayId;
        private String mInputDeviceName;
        private int mProductId;
        private int mVendorId;

        public T setVendorId(int vendorId) {
            this.mVendorId = vendorId;
            return self();
        }

        public T setProductId(int productId) {
            this.mProductId = productId;
            return self();
        }

        public T setAssociatedDisplayId(int displayId) {
            this.mAssociatedDisplayId = displayId;
            return self();
        }

        public T setInputDeviceName(String deviceName) {
            this.mInputDeviceName = deviceName;
            return self();
        }

        T self() {
            return this;
        }
    }
}
