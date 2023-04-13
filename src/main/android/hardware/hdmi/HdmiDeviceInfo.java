package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public class HdmiDeviceInfo implements Parcelable {
    public static final int ADDR_INTERNAL = 0;
    public static final int ADDR_INVALID = -1;
    public static final int DEVICE_AUDIO_SYSTEM = 5;
    public static final int DEVICE_INACTIVE = -1;
    public static final int DEVICE_PLAYBACK = 4;
    public static final int DEVICE_PURE_CEC_SWITCH = 6;
    public static final int DEVICE_RECORDER = 1;
    public static final int DEVICE_RESERVED = 2;
    public static final int DEVICE_TUNER = 3;
    public static final int DEVICE_TV = 0;
    public static final int DEVICE_VIDEO_PROCESSOR = 7;
    private static final int HDMI_DEVICE_TYPE_CEC = 0;
    private static final int HDMI_DEVICE_TYPE_HARDWARE = 2;
    private static final int HDMI_DEVICE_TYPE_INACTIVE = 100;
    private static final int HDMI_DEVICE_TYPE_MHL = 1;
    public static final int ID_INVALID = 65535;
    private static final int ID_OFFSET_CEC = 0;
    private static final int ID_OFFSET_HARDWARE = 192;
    private static final int ID_OFFSET_MHL = 128;
    public static final int PATH_INTERNAL = 0;
    public static final int PATH_INVALID = 65535;
    public static final int PORT_INVALID = -1;
    public static final int VENDOR_ID_UNKNOWN = 16777215;
    private final int mAdopterId;
    private final int mCecVersion;
    private final DeviceFeatures mDeviceFeatures;
    private final int mDeviceId;
    private final int mDevicePowerStatus;
    private final int mDeviceType;
    private final String mDisplayName;
    private final int mHdmiDeviceType;
    private final int mId;
    private final int mLogicalAddress;
    private final int mPhysicalAddress;
    private final int mPortId;
    private final int mVendorId;
    public static final HdmiDeviceInfo INACTIVE_DEVICE = new HdmiDeviceInfo();
    public static final Parcelable.Creator<HdmiDeviceInfo> CREATOR = new Parcelable.Creator<HdmiDeviceInfo>() { // from class: android.hardware.hdmi.HdmiDeviceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HdmiDeviceInfo createFromParcel(Parcel source) {
            int hdmiDeviceType = source.readInt();
            int physicalAddress = source.readInt();
            int portId = source.readInt();
            switch (hdmiDeviceType) {
                case 0:
                    int logicalAddress = source.readInt();
                    int deviceType = source.readInt();
                    int vendorId = source.readInt();
                    int powerStatus = source.readInt();
                    String displayName = source.readString();
                    int cecVersion = source.readInt();
                    return HdmiDeviceInfo.cecDeviceBuilder().setLogicalAddress(logicalAddress).setPhysicalAddress(physicalAddress).setPortId(portId).setDeviceType(deviceType).setVendorId(vendorId).setDisplayName(displayName).setDevicePowerStatus(powerStatus).setCecVersion(cecVersion).build();
                case 1:
                    int deviceId = source.readInt();
                    int adopterId = source.readInt();
                    return HdmiDeviceInfo.mhlDevice(physicalAddress, portId, adopterId, deviceId);
                case 2:
                    return HdmiDeviceInfo.hardwarePort(physicalAddress, portId);
                case 100:
                    return HdmiDeviceInfo.INACTIVE_DEVICE;
                default:
                    return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HdmiDeviceInfo[] newArray(int size) {
            return new HdmiDeviceInfo[size];
        }
    };

    @Deprecated
    public HdmiDeviceInfo() {
        this.mHdmiDeviceType = 100;
        this.mPhysicalAddress = 65535;
        this.mId = 65535;
        this.mLogicalAddress = -1;
        this.mDeviceType = -1;
        this.mCecVersion = 5;
        this.mPortId = -1;
        this.mDevicePowerStatus = -1;
        this.mDisplayName = "Inactive";
        this.mVendorId = 0;
        this.mDeviceFeatures = DeviceFeatures.ALL_FEATURES_SUPPORT_UNKNOWN;
        this.mDeviceId = -1;
        this.mAdopterId = -1;
    }

    public Builder toBuilder() {
        return new Builder();
    }

    private HdmiDeviceInfo(Builder builder) {
        int i = builder.mHdmiDeviceType;
        this.mHdmiDeviceType = i;
        this.mPhysicalAddress = builder.mPhysicalAddress;
        int i2 = builder.mPortId;
        this.mPortId = i2;
        int i3 = builder.mLogicalAddress;
        this.mLogicalAddress = i3;
        this.mDeviceType = builder.mDeviceType;
        this.mCecVersion = builder.mCecVersion;
        this.mVendorId = builder.mVendorId;
        this.mDisplayName = builder.mDisplayName;
        this.mDevicePowerStatus = builder.mDevicePowerStatus;
        this.mDeviceFeatures = builder.mDeviceFeatures;
        this.mDeviceId = builder.mDeviceId;
        this.mAdopterId = builder.mAdopterId;
        switch (i) {
            case 0:
                this.mId = idForCecDevice(i3);
                return;
            case 1:
                this.mId = idForMhlDevice(i2);
                return;
            case 2:
                this.mId = idForHardware(i2);
                return;
            default:
                this.mId = 65535;
                return;
        }
    }

    public static Builder cecDeviceBuilder() {
        return new Builder(0);
    }

    public static HdmiDeviceInfo mhlDevice(int physicalAddress, int portId, int adopterId, int deviceId) {
        return new Builder(1).setPhysicalAddress(physicalAddress).setPortId(portId).setVendorId(0).setDisplayName("Mobile").setDeviceId(adopterId).setAdopterId(deviceId).build();
    }

    public static HdmiDeviceInfo hardwarePort(int physicalAddress, int portId) {
        return new Builder(2).setPhysicalAddress(physicalAddress).setPortId(portId).setVendorId(0).setDisplayName("HDMI" + portId).build();
    }

    public int getId() {
        return this.mId;
    }

    public DeviceFeatures getDeviceFeatures() {
        return this.mDeviceFeatures;
    }

    public static int idForCecDevice(int address) {
        return address + 0;
    }

    public static int idForMhlDevice(int portId) {
        return portId + 128;
    }

    public static int idForHardware(int portId) {
        return portId + 192;
    }

    public int getLogicalAddress() {
        return this.mLogicalAddress;
    }

    public int getPhysicalAddress() {
        return this.mPhysicalAddress;
    }

    public int getPortId() {
        return this.mPortId;
    }

    public int getDeviceType() {
        return this.mDeviceType;
    }

    public int getCecVersion() {
        return this.mCecVersion;
    }

    public int getDevicePowerStatus() {
        return this.mDevicePowerStatus;
    }

    public int getDeviceId() {
        return this.mDeviceId;
    }

    public int getAdopterId() {
        return this.mAdopterId;
    }

    public boolean isSourceType() {
        if (!isCecDevice()) {
            return isMhlDevice();
        }
        int i = this.mDeviceType;
        return i == 4 || i == 1 || i == 3;
    }

    public boolean isCecDevice() {
        return this.mHdmiDeviceType == 0;
    }

    public boolean isMhlDevice() {
        return this.mHdmiDeviceType == 1;
    }

    public boolean isInactivated() {
        return this.mHdmiDeviceType == 100;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public int getVendorId() {
        return this.mVendorId;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mHdmiDeviceType);
        dest.writeInt(this.mPhysicalAddress);
        dest.writeInt(this.mPortId);
        switch (this.mHdmiDeviceType) {
            case 0:
                dest.writeInt(this.mLogicalAddress);
                dest.writeInt(this.mDeviceType);
                dest.writeInt(this.mVendorId);
                dest.writeInt(this.mDevicePowerStatus);
                dest.writeString(this.mDisplayName);
                dest.writeInt(this.mCecVersion);
                return;
            case 1:
                dest.writeInt(this.mDeviceId);
                dest.writeInt(this.mAdopterId);
                return;
            default:
                return;
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        switch (this.mHdmiDeviceType) {
            case 0:
                s.append("CEC: ");
                s.append("logical_address: ").append(String.format("0x%02X", Integer.valueOf(this.mLogicalAddress)));
                s.append(" ");
                s.append("device_type: ").append(this.mDeviceType).append(" ");
                s.append("cec_version: ").append(this.mCecVersion).append(" ");
                s.append("vendor_id: ").append(this.mVendorId).append(" ");
                s.append("display_name: ").append(this.mDisplayName).append(" ");
                s.append("power_status: ").append(this.mDevicePowerStatus).append(" ");
                break;
            case 1:
                s.append("MHL: ");
                s.append("device_id: ").append(String.format("0x%04X", Integer.valueOf(this.mDeviceId))).append(" ");
                s.append("adopter_id: ").append(String.format("0x%04X", Integer.valueOf(this.mAdopterId))).append(" ");
                break;
            case 2:
                s.append("Hardware: ");
                break;
            case 100:
                s.append("Inactivated: ");
                break;
            default:
                return "";
        }
        s.append("physical_address: ").append(String.format("0x%04X", Integer.valueOf(this.mPhysicalAddress)));
        s.append(" ");
        s.append("port_id: ").append(this.mPortId);
        if (this.mHdmiDeviceType == 0) {
            s.append("\n  ").append(this.mDeviceFeatures.toString());
        }
        return s.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof HdmiDeviceInfo) {
            HdmiDeviceInfo other = (HdmiDeviceInfo) obj;
            return this.mHdmiDeviceType == other.mHdmiDeviceType && this.mPhysicalAddress == other.mPhysicalAddress && this.mPortId == other.mPortId && this.mLogicalAddress == other.mLogicalAddress && this.mDeviceType == other.mDeviceType && this.mCecVersion == other.mCecVersion && this.mVendorId == other.mVendorId && this.mDevicePowerStatus == other.mDevicePowerStatus && this.mDisplayName.equals(other.mDisplayName) && this.mDeviceId == other.mDeviceId && this.mAdopterId == other.mAdopterId;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mHdmiDeviceType), Integer.valueOf(this.mPhysicalAddress), Integer.valueOf(this.mPortId), Integer.valueOf(this.mLogicalAddress), Integer.valueOf(this.mDeviceType), Integer.valueOf(this.mCecVersion), Integer.valueOf(this.mVendorId), Integer.valueOf(this.mDevicePowerStatus), this.mDisplayName, Integer.valueOf(this.mDeviceId), Integer.valueOf(this.mAdopterId));
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mAdopterId;
        private int mCecVersion;
        private DeviceFeatures mDeviceFeatures;
        private int mDeviceId;
        private int mDevicePowerStatus;
        private int mDeviceType;
        private String mDisplayName;
        private final int mHdmiDeviceType;
        private int mLogicalAddress;
        private int mPhysicalAddress;
        private int mPortId;
        private int mVendorId;

        private Builder(int hdmiDeviceType) {
            this.mPhysicalAddress = 65535;
            this.mPortId = -1;
            this.mLogicalAddress = -1;
            this.mDeviceType = 2;
            this.mCecVersion = 5;
            this.mVendorId = 16777215;
            this.mDisplayName = "";
            this.mDevicePowerStatus = -1;
            this.mDeviceId = -1;
            this.mAdopterId = -1;
            this.mHdmiDeviceType = hdmiDeviceType;
            if (hdmiDeviceType == 0) {
                this.mDeviceFeatures = DeviceFeatures.ALL_FEATURES_SUPPORT_UNKNOWN;
            } else {
                this.mDeviceFeatures = DeviceFeatures.NO_FEATURES_SUPPORTED;
            }
        }

        private Builder(HdmiDeviceInfo hdmiDeviceInfo) {
            this.mPhysicalAddress = 65535;
            this.mPortId = -1;
            this.mLogicalAddress = -1;
            this.mDeviceType = 2;
            this.mCecVersion = 5;
            this.mVendorId = 16777215;
            this.mDisplayName = "";
            this.mDevicePowerStatus = -1;
            this.mDeviceId = -1;
            this.mAdopterId = -1;
            this.mHdmiDeviceType = hdmiDeviceInfo.mHdmiDeviceType;
            this.mPhysicalAddress = hdmiDeviceInfo.mPhysicalAddress;
            this.mPortId = hdmiDeviceInfo.mPortId;
            this.mLogicalAddress = hdmiDeviceInfo.mLogicalAddress;
            this.mDeviceType = hdmiDeviceInfo.mDeviceType;
            this.mCecVersion = hdmiDeviceInfo.mCecVersion;
            this.mVendorId = hdmiDeviceInfo.mVendorId;
            this.mDisplayName = hdmiDeviceInfo.mDisplayName;
            this.mDevicePowerStatus = hdmiDeviceInfo.mDevicePowerStatus;
            this.mDeviceId = hdmiDeviceInfo.mDeviceId;
            this.mAdopterId = hdmiDeviceInfo.mAdopterId;
            this.mDeviceFeatures = hdmiDeviceInfo.mDeviceFeatures;
        }

        public HdmiDeviceInfo build() {
            return new HdmiDeviceInfo(this);
        }

        public Builder setPhysicalAddress(int physicalAddress) {
            this.mPhysicalAddress = physicalAddress;
            return this;
        }

        public Builder setPortId(int portId) {
            this.mPortId = portId;
            return this;
        }

        public Builder setLogicalAddress(int logicalAddress) {
            this.mLogicalAddress = logicalAddress;
            return this;
        }

        public Builder setDeviceType(int deviceType) {
            this.mDeviceType = deviceType;
            return this;
        }

        public Builder setCecVersion(int hdmiCecVersion) {
            this.mCecVersion = hdmiCecVersion;
            return this;
        }

        public Builder setVendorId(int vendorId) {
            this.mVendorId = vendorId;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.mDisplayName = displayName;
            return this;
        }

        public Builder setDevicePowerStatus(int devicePowerStatus) {
            this.mDevicePowerStatus = devicePowerStatus;
            return this;
        }

        public Builder setDeviceFeatures(DeviceFeatures deviceFeatures) {
            this.mDeviceFeatures = deviceFeatures;
            return this;
        }

        public Builder setDeviceId(int deviceId) {
            this.mDeviceId = deviceId;
            return this;
        }

        public Builder setAdopterId(int adopterId) {
            this.mAdopterId = adopterId;
            return this;
        }

        public Builder updateDeviceFeatures(DeviceFeatures deviceFeatures) {
            this.mDeviceFeatures = this.mDeviceFeatures.toBuilder().update(deviceFeatures).build();
            return this;
        }
    }
}
