package android.hardware.input;

import android.annotation.SystemApi;
import android.hardware.input.VirtualInputDeviceConfig;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes2.dex */
public final class VirtualMouseConfig extends VirtualInputDeviceConfig implements Parcelable {
    public static final Parcelable.Creator<VirtualMouseConfig> CREATOR = new Parcelable.Creator<VirtualMouseConfig>() { // from class: android.hardware.input.VirtualMouseConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualMouseConfig createFromParcel(Parcel in) {
            return new VirtualMouseConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualMouseConfig[] newArray(int size) {
            return new VirtualMouseConfig[size];
        }
    };

    private VirtualMouseConfig(Builder builder) {
        super(builder);
    }

    private VirtualMouseConfig(Parcel in) {
        super(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.hardware.input.VirtualInputDeviceConfig, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    /* loaded from: classes2.dex */
    public static final class Builder extends VirtualInputDeviceConfig.Builder<Builder> {
        public VirtualMouseConfig build() {
            return new VirtualMouseConfig(this);
        }
    }
}
