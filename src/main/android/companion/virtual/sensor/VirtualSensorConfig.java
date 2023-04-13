package android.companion.virtual.sensor;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class VirtualSensorConfig implements Parcelable {
    public static final Parcelable.Creator<VirtualSensorConfig> CREATOR = new Parcelable.Creator<VirtualSensorConfig>() { // from class: android.companion.virtual.sensor.VirtualSensorConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualSensorConfig createFromParcel(Parcel source) {
            return new VirtualSensorConfig(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualSensorConfig[] newArray(int size) {
            return new VirtualSensorConfig[size];
        }
    };
    private static final int DIRECT_CHANNEL_SHIFT = 10;
    private static final int DIRECT_REPORT_MASK = 896;
    private static final int DIRECT_REPORT_SHIFT = 7;
    private static final String TAG = "VirtualSensorConfig";
    private final int mFlags;
    private final String mName;
    private final int mType;
    private final String mVendor;

    private VirtualSensorConfig(int type, String name, String vendor, int flags) {
        this.mType = type;
        this.mName = name;
        this.mVendor = vendor;
        this.mFlags = flags;
    }

    private VirtualSensorConfig(Parcel parcel) {
        this.mType = parcel.readInt();
        this.mName = parcel.readString8();
        this.mVendor = parcel.readString8();
        this.mFlags = parcel.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mType);
        parcel.writeString8(this.mName);
        parcel.writeString8(this.mVendor);
        parcel.writeInt(this.mFlags);
    }

    public int getType() {
        return this.mType;
    }

    public String getName() {
        return this.mName;
    }

    public String getVendor() {
        return this.mVendor;
    }

    public int getHighestDirectReportRateLevel() {
        int rateLevel = (this.mFlags & 896) >> 7;
        if (rateLevel > 3) {
            return 3;
        }
        return rateLevel;
    }

    public int getDirectChannelTypesSupported() {
        int memoryTypes = 0;
        int i = this.mFlags;
        if ((i & 1024) > 0) {
            memoryTypes = 0 | 1;
        }
        if ((i & 2048) > 0) {
            return memoryTypes | 2;
        }
        return memoryTypes;
    }

    public int getFlags() {
        return this.mFlags;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private static final int FLAG_MEMORY_FILE_DIRECT_CHANNEL_SUPPORTED = 1024;
        private int mFlags;
        int mHighestDirectReportRateLevel;
        private final String mName;
        private final int mType;
        private String mVendor;

        public Builder(int type, String name) {
            if (type <= 0) {
                throw new IllegalArgumentException("Virtual sensor type must be positive");
            }
            this.mType = type;
            this.mName = (String) Objects.requireNonNull(name);
        }

        public VirtualSensorConfig build() {
            int i = this.mHighestDirectReportRateLevel;
            if (i > 0) {
                int i2 = this.mFlags;
                if ((i2 & 1024) == 0) {
                    throw new IllegalArgumentException("Setting direct channel type is required for sensors with direct channel support.");
                }
                this.mFlags = i2 | (i << 7);
            }
            if ((this.mFlags & 1024) > 0 && i == 0) {
                throw new IllegalArgumentException("Highest direct report rate level is required for sensors with direct channel support.");
            }
            return new VirtualSensorConfig(this.mType, this.mName, this.mVendor, this.mFlags);
        }

        public Builder setVendor(String vendor) {
            this.mVendor = vendor;
            return this;
        }

        public Builder setHighestDirectReportRateLevel(int rateLevel) {
            this.mHighestDirectReportRateLevel = rateLevel;
            return this;
        }

        public Builder setDirectChannelTypesSupported(int memoryTypes) {
            if ((memoryTypes & 1) > 0) {
                this.mFlags |= 1024;
            } else {
                this.mFlags &= -1025;
            }
            if ((memoryTypes & (-2)) > 0) {
                throw new IllegalArgumentException("Only TYPE_MEMORY_FILE direct channels can be supported for virtual sensors.");
            }
            return this;
        }
    }
}
