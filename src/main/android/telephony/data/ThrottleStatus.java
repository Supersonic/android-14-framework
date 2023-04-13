package android.telephony.data;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class ThrottleStatus implements Parcelable {
    public static final Parcelable.Creator<ThrottleStatus> CREATOR = new Parcelable.Creator<ThrottleStatus>() { // from class: android.telephony.data.ThrottleStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ThrottleStatus createFromParcel(Parcel source) {
            return new ThrottleStatus(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ThrottleStatus[] newArray(int size) {
            return new ThrottleStatus[size];
        }
    };
    public static final int RETRY_TYPE_HANDOVER = 3;
    public static final int RETRY_TYPE_NEW_CONNECTION = 2;
    public static final int RETRY_TYPE_NONE = 1;
    public static final int THROTTLE_TYPE_ELAPSED_TIME = 2;
    public static final int THROTTLE_TYPE_NONE = 1;
    private final int mApnType;
    private final int mRetryType;
    private final int mSlotIndex;
    private final long mThrottleExpiryTimeMillis;
    private final int mThrottleType;
    private final int mTransportType;

    /* loaded from: classes3.dex */
    public @interface RetryType {
    }

    /* loaded from: classes3.dex */
    public @interface ThrottleType {
    }

    public int getSlotIndex() {
        return this.mSlotIndex;
    }

    public int getTransportType() {
        return this.mTransportType;
    }

    public int getApnType() {
        return this.mApnType;
    }

    public int getThrottleType() {
        return this.mThrottleType;
    }

    public int getRetryType() {
        return this.mRetryType;
    }

    public long getThrottleExpiryTimeMillis() {
        return this.mThrottleExpiryTimeMillis;
    }

    private ThrottleStatus(int slotIndex, int transportType, int apnTypes, int throttleType, long throttleExpiryTimeMillis, int retryType) {
        this.mSlotIndex = slotIndex;
        this.mTransportType = transportType;
        this.mApnType = apnTypes;
        this.mThrottleType = throttleType;
        this.mThrottleExpiryTimeMillis = throttleExpiryTimeMillis;
        this.mRetryType = retryType;
    }

    private ThrottleStatus(Parcel source) {
        this.mSlotIndex = source.readInt();
        this.mTransportType = source.readInt();
        this.mApnType = source.readInt();
        this.mThrottleExpiryTimeMillis = source.readLong();
        this.mRetryType = source.readInt();
        this.mThrottleType = source.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mSlotIndex);
        dest.writeInt(this.mTransportType);
        dest.writeInt(this.mApnType);
        dest.writeLong(this.mThrottleExpiryTimeMillis);
        dest.writeInt(this.mRetryType);
        dest.writeInt(this.mThrottleType);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mSlotIndex), Integer.valueOf(this.mApnType), Integer.valueOf(this.mRetryType), Integer.valueOf(this.mThrottleType), Long.valueOf(this.mThrottleExpiryTimeMillis), Integer.valueOf(this.mTransportType));
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ThrottleStatus)) {
            return false;
        }
        ThrottleStatus other = (ThrottleStatus) obj;
        return this.mSlotIndex == other.mSlotIndex && this.mApnType == other.mApnType && this.mRetryType == other.mRetryType && this.mThrottleType == other.mThrottleType && this.mThrottleExpiryTimeMillis == other.mThrottleExpiryTimeMillis && this.mTransportType == other.mTransportType;
    }

    public String toString() {
        return "ThrottleStatus{mSlotIndex=" + this.mSlotIndex + ", mTransportType=" + this.mTransportType + ", mApnType=" + ApnSetting.getApnTypeString(this.mApnType) + ", mThrottleExpiryTimeMillis=" + this.mThrottleExpiryTimeMillis + ", mRetryType=" + this.mRetryType + ", mThrottleType=" + this.mThrottleType + '}';
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        public static final long NO_THROTTLE_EXPIRY_TIME = -1;
        private int mApnType;
        private int mRetryType;
        private int mSlotIndex;
        private long mThrottleExpiryTimeMillis;
        private int mThrottleType;
        private int mTransportType;

        public Builder setSlotIndex(int slotIndex) {
            this.mSlotIndex = slotIndex;
            return this;
        }

        public Builder setTransportType(int transportType) {
            this.mTransportType = transportType;
            return this;
        }

        public Builder setApnType(int apnType) {
            this.mApnType = apnType;
            return this;
        }

        public Builder setThrottleExpiryTimeMillis(long throttleExpiryTimeMillis) {
            if (throttleExpiryTimeMillis >= 0) {
                this.mThrottleExpiryTimeMillis = throttleExpiryTimeMillis;
                this.mThrottleType = 2;
                return this;
            }
            throw new IllegalArgumentException("throttleExpiryTimeMillis must be greater than or equal to 0");
        }

        public Builder setNoThrottle() {
            this.mThrottleType = 1;
            this.mThrottleExpiryTimeMillis = -1L;
            return this;
        }

        public Builder setRetryType(int retryType) {
            this.mRetryType = retryType;
            return this;
        }

        public ThrottleStatus build() {
            return new ThrottleStatus(this.mSlotIndex, this.mTransportType, this.mApnType, this.mThrottleType, this.mThrottleExpiryTimeMillis, this.mRetryType);
        }
    }
}
