package android.telephony.data;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.security.keystore.KeyProperties;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class NetworkSliceInfo implements Parcelable {
    public static final Parcelable.Creator<NetworkSliceInfo> CREATOR = new Parcelable.Creator<NetworkSliceInfo>() { // from class: android.telephony.data.NetworkSliceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkSliceInfo createFromParcel(Parcel source) {
            return new NetworkSliceInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkSliceInfo[] newArray(int size) {
            return new NetworkSliceInfo[size];
        }
    };
    public static final int MAX_SLICE_DIFFERENTIATOR = 16777214;
    public static final int MAX_SLICE_STATUS = 5;
    public static final int MIN_SLICE_DIFFERENTIATOR = -1;
    public static final int MIN_SLICE_STATUS = 0;
    public static final int SLICE_DIFFERENTIATOR_NO_SLICE = -1;
    public static final int SLICE_SERVICE_TYPE_EMBB = 1;
    public static final int SLICE_SERVICE_TYPE_MIOT = 3;
    public static final int SLICE_SERVICE_TYPE_NONE = 0;
    public static final int SLICE_SERVICE_TYPE_URLLC = 2;
    public static final int SLICE_STATUS_ALLOWED = 2;
    public static final int SLICE_STATUS_CONFIGURED = 1;
    public static final int SLICE_STATUS_DEFAULT_CONFIGURED = 5;
    public static final int SLICE_STATUS_REJECTED_NOT_AVAILABLE_IN_PLMN = 3;
    public static final int SLICE_STATUS_REJECTED_NOT_AVAILABLE_IN_REGISTERED_AREA = 4;
    public static final int SLICE_STATUS_UNKNOWN = 0;
    private final int mMappedHplmnSliceDifferentiator;
    private final int mMappedHplmnSliceServiceType;
    private final int mSliceDifferentiator;
    private final int mSliceServiceType;
    private final int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SliceServiceType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SliceStatus {
    }

    private NetworkSliceInfo(int sliceServiceType, int sliceDifferentiator, int mappedHplmnSliceServiceType, int mappedHplmnSliceDifferentiator, int status) {
        this.mSliceServiceType = sliceServiceType;
        this.mSliceDifferentiator = sliceDifferentiator;
        this.mMappedHplmnSliceDifferentiator = mappedHplmnSliceDifferentiator;
        this.mMappedHplmnSliceServiceType = mappedHplmnSliceServiceType;
        this.mStatus = status;
    }

    public int getSliceServiceType() {
        return this.mSliceServiceType;
    }

    public int getSliceDifferentiator() {
        return this.mSliceDifferentiator;
    }

    public int getMappedHplmnSliceServiceType() {
        return this.mMappedHplmnSliceServiceType;
    }

    public int getMappedHplmnSliceDifferentiator() {
        return this.mMappedHplmnSliceDifferentiator;
    }

    public int getStatus() {
        return this.mStatus;
    }

    private NetworkSliceInfo(Parcel in) {
        this.mSliceServiceType = in.readInt();
        this.mSliceDifferentiator = in.readInt();
        this.mMappedHplmnSliceServiceType = in.readInt();
        this.mMappedHplmnSliceDifferentiator = in.readInt();
        this.mStatus = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSliceServiceType);
        dest.writeInt(this.mSliceDifferentiator);
        dest.writeInt(this.mMappedHplmnSliceServiceType);
        dest.writeInt(this.mMappedHplmnSliceDifferentiator);
        dest.writeInt(this.mStatus);
    }

    public String toString() {
        return "SliceInfo{mSliceServiceType=" + sliceServiceTypeToString(this.mSliceServiceType) + ", mSliceDifferentiator=" + this.mSliceDifferentiator + ", mMappedHplmnSliceServiceType=" + sliceServiceTypeToString(this.mMappedHplmnSliceServiceType) + ", mMappedHplmnSliceDifferentiator=" + this.mMappedHplmnSliceDifferentiator + ", mStatus=" + sliceStatusToString(this.mStatus) + '}';
    }

    private static String sliceServiceTypeToString(int sliceServiceType) {
        switch (sliceServiceType) {
            case 0:
                return KeyProperties.DIGEST_NONE;
            case 1:
                return "EMBB";
            case 2:
                return "URLLC";
            case 3:
                return "MIOT";
            default:
                return Integer.toString(sliceServiceType);
        }
    }

    private static String sliceStatusToString(int sliceStatus) {
        switch (sliceStatus) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "CONFIGURED";
            case 2:
                return "ALLOWED";
            case 3:
                return "REJECTED_NOT_AVAILABLE_IN_PLMN";
            case 4:
                return "REJECTED_NOT_AVAILABLE_IN_REGISTERED_AREA";
            case 5:
                return "DEFAULT_CONFIGURED";
            default:
                return Integer.toString(sliceStatus);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetworkSliceInfo sliceInfo = (NetworkSliceInfo) o;
        if (this.mSliceServiceType == sliceInfo.mSliceServiceType && this.mSliceDifferentiator == sliceInfo.mSliceDifferentiator && this.mMappedHplmnSliceServiceType == sliceInfo.mMappedHplmnSliceServiceType && this.mMappedHplmnSliceDifferentiator == sliceInfo.mMappedHplmnSliceDifferentiator && this.mStatus == sliceInfo.mStatus) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mSliceServiceType), Integer.valueOf(this.mSliceDifferentiator), Integer.valueOf(this.mMappedHplmnSliceServiceType), Integer.valueOf(this.mMappedHplmnSliceDifferentiator), Integer.valueOf(this.mStatus));
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mSliceServiceType = 0;
        private int mSliceDifferentiator = -1;
        private int mMappedHplmnSliceServiceType = 0;
        private int mMappedHplmnSliceDifferentiator = -1;
        private int mStatus = 0;

        public Builder setSliceServiceType(int mSliceServiceType) {
            this.mSliceServiceType = mSliceServiceType;
            return this;
        }

        public Builder setSliceDifferentiator(int sliceDifferentiator) {
            if (sliceDifferentiator < -1 || sliceDifferentiator > 16777214) {
                throw new IllegalArgumentException("The slice diffentiator value is out of range");
            }
            this.mSliceDifferentiator = sliceDifferentiator;
            return this;
        }

        public Builder setMappedHplmnSliceServiceType(int mappedHplmnSliceServiceType) {
            this.mMappedHplmnSliceServiceType = mappedHplmnSliceServiceType;
            return this;
        }

        public Builder setMappedHplmnSliceDifferentiator(int mappedHplmnSliceDifferentiator) {
            if (mappedHplmnSliceDifferentiator < -1 || mappedHplmnSliceDifferentiator > 16777214) {
                throw new IllegalArgumentException("The slice diffentiator value is out of range");
            }
            this.mMappedHplmnSliceDifferentiator = mappedHplmnSliceDifferentiator;
            return this;
        }

        public Builder setStatus(int status) {
            if (status < 0 || status > 5) {
                throw new IllegalArgumentException("The slice status is not valid");
            }
            this.mStatus = status;
            return this;
        }

        public NetworkSliceInfo build() {
            return new NetworkSliceInfo(this.mSliceServiceType, this.mSliceDifferentiator, this.mMappedHplmnSliceServiceType, this.mMappedHplmnSliceDifferentiator, this.mStatus);
        }
    }
}
