package android.telephony;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class PhysicalChannelConfig implements Parcelable {
    public static final int BAND_UNKNOWN = 0;
    public static final int CELL_BANDWIDTH_UNKNOWN = 0;
    public static final int CHANNEL_NUMBER_UNKNOWN = Integer.MAX_VALUE;
    @Deprecated
    public static final int CONNECTION_PRIMARY_SERVING = 1;
    @Deprecated
    public static final int CONNECTION_SECONDARY_SERVING = 2;
    @Deprecated
    public static final int CONNECTION_UNKNOWN = -1;
    public static final Parcelable.Creator<PhysicalChannelConfig> CREATOR = new Parcelable.Creator<PhysicalChannelConfig>() { // from class: android.telephony.PhysicalChannelConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhysicalChannelConfig createFromParcel(Parcel in) {
            return new PhysicalChannelConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhysicalChannelConfig[] newArray(int size) {
            return new PhysicalChannelConfig[size];
        }
    };
    public static final int FREQUENCY_UNKNOWN = -1;
    public static final int PHYSICAL_CELL_ID_MAXIMUM_VALUE = 1007;
    public static final int PHYSICAL_CELL_ID_UNKNOWN = -1;
    private int mBand;
    private int mCellBandwidthDownlinkKhz;
    private int mCellBandwidthUplinkKhz;
    private int mCellConnectionStatus;
    private int[] mContextIds;
    private int mDownlinkChannelNumber;
    private int mDownlinkFrequency;
    private int mFrequencyRange;
    private int mNetworkType;
    private int mPhysicalCellId;
    private int mUplinkChannelNumber;
    private int mUplinkFrequency;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ConnectionStatus {
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCellConnectionStatus);
        dest.writeInt(this.mCellBandwidthDownlinkKhz);
        dest.writeInt(this.mCellBandwidthUplinkKhz);
        dest.writeInt(this.mNetworkType);
        dest.writeInt(this.mDownlinkChannelNumber);
        dest.writeInt(this.mUplinkChannelNumber);
        dest.writeInt(this.mFrequencyRange);
        dest.writeIntArray(this.mContextIds);
        dest.writeInt(this.mPhysicalCellId);
        dest.writeInt(this.mBand);
    }

    public int getCellBandwidthDownlinkKhz() {
        return this.mCellBandwidthDownlinkKhz;
    }

    public int getCellBandwidthUplinkKhz() {
        return this.mCellBandwidthUplinkKhz;
    }

    public int[] getContextIds() {
        return this.mContextIds;
    }

    public int getFrequencyRange() {
        return this.mFrequencyRange;
    }

    public int getDownlinkChannelNumber() {
        return this.mDownlinkChannelNumber;
    }

    public int getUplinkChannelNumber() {
        return this.mUplinkChannelNumber;
    }

    public int getBand() {
        return this.mBand;
    }

    public int getDownlinkFrequencyKhz() {
        return this.mDownlinkFrequency;
    }

    public int getUplinkFrequencyKhz() {
        return this.mUplinkFrequency;
    }

    public int getPhysicalCellId() {
        return this.mPhysicalCellId;
    }

    public int getNetworkType() {
        return this.mNetworkType;
    }

    public int getConnectionStatus() {
        return this.mCellConnectionStatus;
    }

    public PhysicalChannelConfig createLocationInfoSanitizedCopy() {
        return new Builder(this).setPhysicalCellId(-1).build();
    }

    private String getConnectionStatusString() {
        switch (this.mCellConnectionStatus) {
            case -1:
                return "Unknown";
            case 0:
            default:
                return "Invalid(" + this.mCellConnectionStatus + NavigationBarInflaterView.KEY_CODE_END;
            case 1:
                return "PrimaryServing";
            case 2:
                return "SecondaryServing";
        }
    }

    private void setDownlinkFrequency() {
        switch (this.mNetworkType) {
            case 1:
            case 2:
            case 16:
                this.mDownlinkFrequency = AccessNetworkUtils.getFrequencyFromArfcn(this.mBand, this.mDownlinkChannelNumber, false);
                return;
            case 3:
            case 8:
            case 9:
            case 10:
            case 15:
            case 17:
                this.mDownlinkFrequency = AccessNetworkUtils.getFrequencyFromUarfcn(this.mBand, this.mDownlinkChannelNumber, false);
                return;
            case 4:
            case 5:
            case 6:
            case 7:
            case 11:
            case 12:
            case 14:
            case 18:
            case 19:
            default:
                return;
            case 13:
                this.mDownlinkFrequency = AccessNetworkUtils.getFrequencyFromEarfcn(this.mBand, this.mDownlinkChannelNumber, false);
                return;
            case 20:
                this.mDownlinkFrequency = AccessNetworkUtils.getFrequencyFromNrArfcn(this.mDownlinkChannelNumber);
                return;
        }
    }

    private void setUplinkFrequency() {
        switch (this.mNetworkType) {
            case 1:
            case 2:
            case 16:
                this.mUplinkFrequency = AccessNetworkUtils.getFrequencyFromArfcn(this.mBand, this.mUplinkChannelNumber, true);
                return;
            case 3:
            case 8:
            case 9:
            case 10:
            case 15:
            case 17:
                this.mUplinkFrequency = AccessNetworkUtils.getFrequencyFromUarfcn(this.mBand, this.mUplinkChannelNumber, true);
                return;
            case 4:
            case 5:
            case 6:
            case 7:
            case 11:
            case 12:
            case 14:
            case 18:
            case 19:
            default:
                return;
            case 13:
                this.mUplinkFrequency = AccessNetworkUtils.getFrequencyFromEarfcn(this.mBand, this.mUplinkChannelNumber, true);
                return;
            case 20:
                this.mUplinkFrequency = AccessNetworkUtils.getFrequencyFromNrArfcn(this.mUplinkChannelNumber);
                return;
        }
    }

    private void setFrequencyRange() {
        if (this.mFrequencyRange != 0) {
            return;
        }
        switch (this.mNetworkType) {
            case 1:
            case 2:
            case 16:
                this.mFrequencyRange = AccessNetworkUtils.getFrequencyRangeGroupFromGeranBand(this.mBand);
                break;
            case 3:
            case 8:
            case 9:
            case 10:
            case 15:
            case 17:
                this.mFrequencyRange = AccessNetworkUtils.getFrequencyRangeGroupFromUtranBand(this.mBand);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 11:
            case 12:
            case 14:
            case 18:
            case 19:
            default:
                this.mFrequencyRange = 0;
                break;
            case 13:
                this.mFrequencyRange = AccessNetworkUtils.getFrequencyRangeGroupFromEutranBand(this.mBand);
                break;
            case 20:
                this.mFrequencyRange = AccessNetworkUtils.getFrequencyRangeGroupFromNrBand(this.mBand);
                break;
        }
        if (this.mFrequencyRange == 0) {
            this.mFrequencyRange = AccessNetworkUtils.getFrequencyRangeFromArfcn(this.mDownlinkFrequency);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PhysicalChannelConfig) {
            PhysicalChannelConfig config = (PhysicalChannelConfig) o;
            return this.mCellConnectionStatus == config.mCellConnectionStatus && this.mCellBandwidthDownlinkKhz == config.mCellBandwidthDownlinkKhz && this.mCellBandwidthUplinkKhz == config.mCellBandwidthUplinkKhz && this.mNetworkType == config.mNetworkType && this.mFrequencyRange == config.mFrequencyRange && this.mDownlinkChannelNumber == config.mDownlinkChannelNumber && this.mUplinkChannelNumber == config.mUplinkChannelNumber && this.mPhysicalCellId == config.mPhysicalCellId && Arrays.equals(this.mContextIds, config.mContextIds) && this.mBand == config.mBand && this.mDownlinkFrequency == config.mDownlinkFrequency && this.mUplinkFrequency == config.mUplinkFrequency;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mCellConnectionStatus), Integer.valueOf(this.mCellBandwidthDownlinkKhz), Integer.valueOf(this.mCellBandwidthUplinkKhz), Integer.valueOf(this.mNetworkType), Integer.valueOf(this.mFrequencyRange), Integer.valueOf(this.mDownlinkChannelNumber), Integer.valueOf(this.mUplinkChannelNumber), Integer.valueOf(Arrays.hashCode(this.mContextIds)), Integer.valueOf(this.mPhysicalCellId), Integer.valueOf(this.mBand), Integer.valueOf(this.mDownlinkFrequency), Integer.valueOf(this.mUplinkFrequency));
    }

    public String toString() {
        return "{mConnectionStatus=" + getConnectionStatusString() + ",mCellBandwidthDownlinkKhz=" + this.mCellBandwidthDownlinkKhz + ",mCellBandwidthUplinkKhz=" + this.mCellBandwidthUplinkKhz + ",mNetworkType=" + TelephonyManager.getNetworkTypeName(this.mNetworkType) + ",mFrequencyRange=" + ServiceState.frequencyRangeToString(this.mFrequencyRange) + ",mDownlinkChannelNumber=" + this.mDownlinkChannelNumber + ",mUplinkChannelNumber=" + this.mUplinkChannelNumber + ",mContextIds=" + Arrays.toString(this.mContextIds) + ",mPhysicalCellId=" + this.mPhysicalCellId + ",mBand=" + this.mBand + ",mDownlinkFrequency=" + this.mDownlinkFrequency + ",mUplinkFrequency=" + this.mUplinkFrequency + "}";
    }

    private PhysicalChannelConfig(Parcel in) {
        this.mCellConnectionStatus = in.readInt();
        this.mCellBandwidthDownlinkKhz = in.readInt();
        this.mCellBandwidthUplinkKhz = in.readInt();
        this.mNetworkType = in.readInt();
        this.mDownlinkChannelNumber = in.readInt();
        this.mUplinkChannelNumber = in.readInt();
        this.mFrequencyRange = in.readInt();
        this.mContextIds = in.createIntArray();
        this.mPhysicalCellId = in.readInt();
        int readInt = in.readInt();
        this.mBand = readInt;
        if (readInt > 0) {
            setDownlinkFrequency();
            setUplinkFrequency();
            setFrequencyRange();
        }
    }

    private PhysicalChannelConfig(Builder builder) {
        this.mCellConnectionStatus = builder.mCellConnectionStatus;
        this.mCellBandwidthDownlinkKhz = builder.mCellBandwidthDownlinkKhz;
        this.mCellBandwidthUplinkKhz = builder.mCellBandwidthUplinkKhz;
        this.mNetworkType = builder.mNetworkType;
        this.mDownlinkChannelNumber = builder.mDownlinkChannelNumber;
        this.mUplinkChannelNumber = builder.mUplinkChannelNumber;
        this.mFrequencyRange = builder.mFrequencyRange;
        this.mContextIds = builder.mContextIds;
        this.mPhysicalCellId = builder.mPhysicalCellId;
        int i = builder.mBand;
        this.mBand = i;
        if (i > 0) {
            setDownlinkFrequency();
            setUplinkFrequency();
            setFrequencyRange();
        }
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mBand;
        private int mCellBandwidthDownlinkKhz;
        private int mCellBandwidthUplinkKhz;
        private int mCellConnectionStatus;
        private int[] mContextIds;
        private int mDownlinkChannelNumber;
        private int mFrequencyRange;
        private int mNetworkType;
        private int mPhysicalCellId;
        private int mUplinkChannelNumber;

        public Builder() {
            this.mNetworkType = 0;
            this.mFrequencyRange = 0;
            this.mDownlinkChannelNumber = Integer.MAX_VALUE;
            this.mUplinkChannelNumber = Integer.MAX_VALUE;
            this.mCellBandwidthDownlinkKhz = 0;
            this.mCellBandwidthUplinkKhz = 0;
            this.mCellConnectionStatus = -1;
            this.mContextIds = new int[0];
            this.mPhysicalCellId = -1;
            this.mBand = 0;
        }

        public Builder(PhysicalChannelConfig config) {
            this.mNetworkType = config.getNetworkType();
            this.mFrequencyRange = config.getFrequencyRange();
            this.mDownlinkChannelNumber = config.getDownlinkChannelNumber();
            this.mUplinkChannelNumber = config.getUplinkChannelNumber();
            this.mCellBandwidthDownlinkKhz = config.getCellBandwidthDownlinkKhz();
            this.mCellBandwidthUplinkKhz = config.getCellBandwidthUplinkKhz();
            this.mCellConnectionStatus = config.getConnectionStatus();
            this.mContextIds = Arrays.copyOf(config.getContextIds(), config.getContextIds().length);
            this.mPhysicalCellId = config.getPhysicalCellId();
            this.mBand = config.getBand();
        }

        public PhysicalChannelConfig build() {
            return new PhysicalChannelConfig(this);
        }

        public Builder setNetworkType(int networkType) {
            if (!TelephonyManager.isNetworkTypeValid(networkType)) {
                throw new IllegalArgumentException("Network type " + networkType + " is invalid.");
            }
            this.mNetworkType = networkType;
            return this;
        }

        public Builder setFrequencyRange(int frequencyRange) {
            if (!ServiceState.isFrequencyRangeValid(frequencyRange) && frequencyRange != 0) {
                throw new IllegalArgumentException("Frequency range " + frequencyRange + " is invalid.");
            }
            this.mFrequencyRange = frequencyRange;
            return this;
        }

        public Builder setDownlinkChannelNumber(int downlinkChannelNumber) {
            this.mDownlinkChannelNumber = downlinkChannelNumber;
            return this;
        }

        public Builder setUplinkChannelNumber(int uplinkChannelNumber) {
            this.mUplinkChannelNumber = uplinkChannelNumber;
            return this;
        }

        public Builder setCellBandwidthDownlinkKhz(int cellBandwidthDownlinkKhz) {
            if (cellBandwidthDownlinkKhz < 0) {
                throw new IllegalArgumentException("Cell downlink bandwidth(kHz) " + cellBandwidthDownlinkKhz + " is invalid.");
            }
            this.mCellBandwidthDownlinkKhz = cellBandwidthDownlinkKhz;
            return this;
        }

        public Builder setCellBandwidthUplinkKhz(int cellBandwidthUplinkKhz) {
            if (cellBandwidthUplinkKhz < 0) {
                throw new IllegalArgumentException("Cell uplink bandwidth(kHz) " + cellBandwidthUplinkKhz + " is invalid.");
            }
            this.mCellBandwidthUplinkKhz = cellBandwidthUplinkKhz;
            return this;
        }

        public Builder setCellConnectionStatus(int connectionStatus) {
            this.mCellConnectionStatus = connectionStatus;
            return this;
        }

        public Builder setContextIds(int[] contextIds) {
            if (contextIds != null) {
                Arrays.sort(contextIds);
            }
            this.mContextIds = contextIds;
            return this;
        }

        public Builder setPhysicalCellId(int physicalCellId) {
            if (physicalCellId > 1007) {
                throw new IllegalArgumentException("Physical cell ID " + physicalCellId + " is over limit.");
            }
            this.mPhysicalCellId = physicalCellId;
            return this;
        }

        public Builder setBand(int band) {
            if (band <= 0) {
                throw new IllegalArgumentException("Band " + band + " is invalid.");
            }
            this.mBand = band;
            return this;
        }
    }
}
