package android.telephony.data;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.TelephonyManager;
import android.telephony.data.ApnSetting;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class DataProfile implements Parcelable {
    public static final Parcelable.Creator<DataProfile> CREATOR = new Parcelable.Creator<DataProfile>() { // from class: android.telephony.data.DataProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataProfile createFromParcel(Parcel source) {
            return new DataProfile(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataProfile[] newArray(int size) {
            return new DataProfile[size];
        }
    };
    public static final int TYPE_3GPP = 1;
    public static final int TYPE_3GPP2 = 2;
    public static final int TYPE_COMMON = 0;
    private final ApnSetting mApnSetting;
    private boolean mPreferred;
    private long mSetupTimestamp;
    private final TrafficDescriptor mTrafficDescriptor;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Type {
    }

    private DataProfile(Builder builder) {
        ApnSetting apnSetting = builder.mApnSetting;
        this.mApnSetting = apnSetting;
        this.mTrafficDescriptor = builder.mTrafficDescriptor;
        this.mPreferred = builder.mPreferred;
        if (builder.mType != -1) {
            this.mType = builder.mType;
        } else if (apnSetting != null) {
            int networkTypes = apnSetting.getNetworkTypeBitmask();
            if (networkTypes == 0) {
                this.mType = 0;
            } else if ((networkTypes & TelephonyManager.NETWORK_STANDARDS_FAMILY_BITMASK_3GPP2) == networkTypes) {
                this.mType = 2;
            } else if ((networkTypes & TelephonyManager.NETWORK_STANDARDS_FAMILY_BITMASK_3GPP) == networkTypes) {
                this.mType = 1;
            } else {
                this.mType = 0;
            }
        } else {
            this.mType = 0;
        }
    }

    private DataProfile(Parcel source) {
        this.mType = source.readInt();
        this.mApnSetting = (ApnSetting) source.readParcelable(ApnSetting.class.getClassLoader(), ApnSetting.class);
        this.mTrafficDescriptor = (TrafficDescriptor) source.readParcelable(TrafficDescriptor.class.getClassLoader(), TrafficDescriptor.class);
        this.mPreferred = source.readBoolean();
        this.mSetupTimestamp = source.readLong();
    }

    @Deprecated
    public int getProfileId() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getProfileId();
        }
        return 0;
    }

    @Deprecated
    public String getApn() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return TextUtils.emptyIfNull(apnSetting.getApnName());
        }
        return "";
    }

    @Deprecated
    public int getProtocolType() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getProtocol();
        }
        return 2;
    }

    @Deprecated
    public int getAuthType() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getAuthType();
        }
        return 0;
    }

    @Deprecated
    public String getUserName() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getUser();
        }
        return null;
    }

    @Deprecated
    public String getPassword() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getPassword();
        }
        return null;
    }

    public int getType() {
        return this.mType;
    }

    public int getMaxConnectionsTime() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getMaxConnsTime();
        }
        return 0;
    }

    public int getMaxConnections() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getMaxConns();
        }
        return 0;
    }

    public int getWaitTime() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getWaitTime();
        }
        return 0;
    }

    public boolean isEnabled() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.isEnabled();
        }
        return true;
    }

    @Deprecated
    public int getSupportedApnTypesBitmask() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getApnTypeBitmask();
        }
        return 0;
    }

    @Deprecated
    public int getRoamingProtocolType() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getRoamingProtocol();
        }
        return 0;
    }

    @Deprecated
    public int getBearerBitmask() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getNetworkTypeBitmask();
        }
        return 0;
    }

    @Deprecated
    public int getMtu() {
        return getMtuV4();
    }

    @Deprecated
    public int getMtuV4() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getMtuV4();
        }
        return 0;
    }

    @Deprecated
    public int getMtuV6() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getMtuV6();
        }
        return 0;
    }

    @Deprecated
    public boolean isPersistent() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.isPersistent();
        }
        return false;
    }

    public void setPreferred(boolean preferred) {
        this.mPreferred = preferred;
    }

    public boolean isPreferred() {
        return this.mPreferred;
    }

    public ApnSetting getApnSetting() {
        return this.mApnSetting;
    }

    public TrafficDescriptor getTrafficDescriptor() {
        return this.mTrafficDescriptor;
    }

    public boolean canSatisfy(int[] networkCapabilities) {
        if (this.mApnSetting != null) {
            for (int netCap : networkCapabilities) {
                if (!canSatisfy(netCap)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean canSatisfy(int networkCapability) {
        ApnSetting apnSetting = this.mApnSetting;
        return apnSetting != null && apnSetting.canHandleType(networkCapabilityToApnType(networkCapability));
    }

    private static int networkCapabilityToApnType(int networkCapability) {
        switch (networkCapability) {
            case 0:
                return 2;
            case 1:
                return 4;
            case 2:
                return 8;
            case 3:
                return 32;
            case 4:
                return 64;
            case 5:
                return 128;
            case 7:
                return 256;
            case 9:
                return 2048;
            case 10:
                return 512;
            case 12:
                return 17;
            case 23:
                return 1024;
            case 29:
                return 16384;
            case 30:
                return 4096;
            case 31:
                return 8192;
            default:
                return 0;
        }
    }

    public void setLastSetupTimestamp(long timestamp) {
        this.mSetupTimestamp = timestamp;
    }

    public long getLastSetupTimestamp() {
        return this.mSetupTimestamp;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "[DataProfile=" + this.mApnSetting + ", " + this.mTrafficDescriptor + ", preferred=" + this.mPreferred + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeParcelable(this.mApnSetting, flags);
        dest.writeParcelable(this.mTrafficDescriptor, flags);
        dest.writeBoolean(this.mPreferred);
        dest.writeLong(this.mSetupTimestamp);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataProfile that = (DataProfile) o;
        if (this.mType == that.mType && Objects.equals(this.mApnSetting, that.mApnSetting) && Objects.equals(this.mTrafficDescriptor, that.mTrafficDescriptor)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mType), this.mApnSetting, this.mTrafficDescriptor);
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private String mApn;
        private ApnSetting mApnSetting;
        private int mAuthType;
        private int mBearerBitmask;
        private int mMtuV4;
        private int mMtuV6;
        private String mPassword;
        private boolean mPersistent;
        private boolean mPreferred;
        private int mProfileId;
        private int mProtocolType;
        private int mRoamingProtocolType;
        private int mSupportedApnTypesBitmask;
        private TrafficDescriptor mTrafficDescriptor;
        private String mUserName;
        private int mType = -1;
        private boolean mEnabled = true;

        @Deprecated
        public Builder setProfileId(int profileId) {
            this.mProfileId = profileId;
            return this;
        }

        @Deprecated
        public Builder setApn(String apn) {
            this.mApn = apn;
            return this;
        }

        @Deprecated
        public Builder setProtocolType(int protocolType) {
            this.mProtocolType = protocolType;
            return this;
        }

        @Deprecated
        public Builder setAuthType(int authType) {
            this.mAuthType = authType;
            return this;
        }

        @Deprecated
        public Builder setUserName(String userName) {
            this.mUserName = userName;
            return this;
        }

        @Deprecated
        public Builder setPassword(String password) {
            this.mPassword = password;
            return this;
        }

        public Builder setType(int type) {
            this.mType = type;
            return this;
        }

        public Builder enable(boolean isEnabled) {
            this.mEnabled = isEnabled;
            return this;
        }

        @Deprecated
        public Builder setSupportedApnTypesBitmask(int supportedApnTypesBitmask) {
            this.mSupportedApnTypesBitmask = supportedApnTypesBitmask;
            return this;
        }

        @Deprecated
        public Builder setRoamingProtocolType(int protocolType) {
            this.mRoamingProtocolType = protocolType;
            return this;
        }

        @Deprecated
        public Builder setBearerBitmask(int bearerBitmask) {
            this.mBearerBitmask = bearerBitmask;
            return this;
        }

        @Deprecated
        public Builder setMtu(int mtu) {
            this.mMtuV6 = mtu;
            this.mMtuV4 = mtu;
            return this;
        }

        @Deprecated
        public Builder setMtuV4(int mtu) {
            this.mMtuV4 = mtu;
            return this;
        }

        @Deprecated
        public Builder setMtuV6(int mtu) {
            this.mMtuV6 = mtu;
            return this;
        }

        public Builder setPreferred(boolean isPreferred) {
            this.mPreferred = isPreferred;
            return this;
        }

        @Deprecated
        public Builder setPersistent(boolean isPersistent) {
            this.mPersistent = isPersistent;
            return this;
        }

        public Builder setApnSetting(ApnSetting apnSetting) {
            this.mApnSetting = apnSetting;
            return this;
        }

        public Builder setTrafficDescriptor(TrafficDescriptor trafficDescriptor) {
            this.mTrafficDescriptor = trafficDescriptor;
            return this;
        }

        public DataProfile build() {
            if (this.mApnSetting == null && this.mApn != null) {
                this.mApnSetting = new ApnSetting.Builder().setEntryName(this.mApn).setApnName(this.mApn).setApnTypeBitmask(this.mSupportedApnTypesBitmask).setAuthType(this.mAuthType).setCarrierEnabled(this.mEnabled).setModemCognitive(this.mPersistent).setMtuV4(this.mMtuV4).setMtuV6(this.mMtuV6).setNetworkTypeBitmask(this.mBearerBitmask).setProfileId(this.mProfileId).setPassword(this.mPassword).setProtocol(this.mProtocolType).setRoamingProtocol(this.mRoamingProtocolType).setUser(this.mUserName).build();
            }
            if (this.mApnSetting == null && this.mTrafficDescriptor == null) {
                throw new IllegalArgumentException("APN setting and traffic descriptor can't be both null.");
            }
            return new DataProfile(this);
        }
    }
}
