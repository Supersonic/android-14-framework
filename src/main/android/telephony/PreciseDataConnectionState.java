package android.telephony;

import android.annotation.SystemApi;
import android.compat.Compatibility;
import android.net.LinkProperties;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.data.ApnSetting;
import com.android.internal.telephony.util.TelephonyUtils;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class PreciseDataConnectionState implements Parcelable {
    public static final Parcelable.Creator<PreciseDataConnectionState> CREATOR = new Parcelable.Creator<PreciseDataConnectionState>() { // from class: android.telephony.PreciseDataConnectionState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PreciseDataConnectionState createFromParcel(Parcel in) {
            return new PreciseDataConnectionState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PreciseDataConnectionState[] newArray(int size) {
            return new PreciseDataConnectionState[size];
        }
    };
    private static final long GET_DATA_CONNECTION_STATE_R_VERSION = 148535736;
    private final ApnSetting mApnSetting;
    private final int mFailCause;
    private final int mId;
    private final LinkProperties mLinkProperties;
    private final int mNetworkType;
    private final int mState;
    private final int mTransportType;

    @Deprecated
    public PreciseDataConnectionState(int state, int networkType, int apnTypes, String apn, LinkProperties linkProperties, int failCause) {
        this(-1, -1, state, networkType, linkProperties, failCause, new ApnSetting.Builder().setApnTypeBitmask(apnTypes).setApnName(apn).setEntryName(apn).build());
    }

    private PreciseDataConnectionState(int transportType, int id, int state, int networkType, LinkProperties linkProperties, int failCause, ApnSetting apnSetting) {
        this.mTransportType = transportType;
        this.mId = id;
        this.mState = state;
        this.mNetworkType = networkType;
        this.mLinkProperties = linkProperties;
        this.mFailCause = failCause;
        this.mApnSetting = apnSetting;
    }

    private PreciseDataConnectionState(Parcel in) {
        this.mTransportType = in.readInt();
        this.mId = in.readInt();
        this.mState = in.readInt();
        this.mNetworkType = in.readInt();
        this.mLinkProperties = (LinkProperties) in.readParcelable(LinkProperties.class.getClassLoader(), LinkProperties.class);
        this.mFailCause = in.readInt();
        this.mApnSetting = (ApnSetting) in.readParcelable(ApnSetting.class.getClassLoader(), ApnSetting.class);
    }

    @SystemApi
    @Deprecated
    public int getDataConnectionState() {
        if (this.mState == 4 && !Compatibility.isChangeEnabled((long) GET_DATA_CONNECTION_STATE_R_VERSION)) {
            return 2;
        }
        return this.mState;
    }

    public int getTransportType() {
        return this.mTransportType;
    }

    public int getId() {
        return this.mId;
    }

    public int getState() {
        return this.mState;
    }

    public int getNetworkType() {
        return this.mNetworkType;
    }

    @SystemApi
    @Deprecated
    public int getDataConnectionApnTypeBitMask() {
        ApnSetting apnSetting = this.mApnSetting;
        if (apnSetting != null) {
            return apnSetting.getApnTypeBitmask();
        }
        return 0;
    }

    @SystemApi
    @Deprecated
    public String getDataConnectionApn() {
        ApnSetting apnSetting = this.mApnSetting;
        return apnSetting != null ? apnSetting.getApnName() : "";
    }

    public LinkProperties getLinkProperties() {
        return this.mLinkProperties;
    }

    @SystemApi
    @Deprecated
    public int getDataConnectionFailCause() {
        return this.mFailCause;
    }

    public int getLastCauseCode() {
        return this.mFailCause;
    }

    public ApnSetting getApnSetting() {
        return this.mApnSetting;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mTransportType);
        out.writeInt(this.mId);
        out.writeInt(this.mState);
        out.writeInt(this.mNetworkType);
        out.writeParcelable(this.mLinkProperties, flags);
        out.writeInt(this.mFailCause);
        out.writeParcelable(this.mApnSetting, flags);
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mTransportType), Integer.valueOf(this.mId), Integer.valueOf(this.mState), Integer.valueOf(this.mNetworkType), Integer.valueOf(this.mFailCause), this.mLinkProperties, this.mApnSetting);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PreciseDataConnectionState that = (PreciseDataConnectionState) o;
        if (this.mTransportType == that.mTransportType && this.mId == that.mId && this.mState == that.mState && this.mNetworkType == that.mNetworkType && this.mFailCause == that.mFailCause && Objects.equals(this.mLinkProperties, that.mLinkProperties) && Objects.equals(this.mApnSetting, that.mApnSetting)) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" state: " + TelephonyUtils.dataStateToString(this.mState));
        sb.append(", transport: " + AccessNetworkConstants.transportTypeToString(this.mTransportType));
        sb.append(", id: " + this.mId);
        sb.append(", network type: " + TelephonyManager.getNetworkTypeName(this.mNetworkType));
        sb.append(", APN Setting: " + this.mApnSetting);
        sb.append(", link properties: " + this.mLinkProperties);
        sb.append(", fail cause: " + DataFailCause.toString(this.mFailCause));
        return sb.toString();
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mTransportType = -1;
        private int mId = -1;
        private int mState = -1;
        private int mNetworkType = 0;
        private LinkProperties mLinkProperties = null;
        private int mFailCause = 0;
        private ApnSetting mApnSetting = null;

        public Builder setTransportType(int transportType) {
            this.mTransportType = transportType;
            return this;
        }

        public Builder setId(int id) {
            this.mId = id;
            return this;
        }

        public Builder setState(int state) {
            this.mState = state;
            return this;
        }

        public Builder setNetworkType(int networkType) {
            this.mNetworkType = networkType;
            return this;
        }

        public Builder setLinkProperties(LinkProperties linkProperties) {
            this.mLinkProperties = linkProperties;
            return this;
        }

        public Builder setFailCause(int failCause) {
            this.mFailCause = failCause;
            return this;
        }

        public Builder setApnSetting(ApnSetting apnSetting) {
            this.mApnSetting = apnSetting;
            return this;
        }

        public PreciseDataConnectionState build() {
            return new PreciseDataConnectionState(this.mTransportType, this.mId, this.mState, this.mNetworkType, this.mLinkProperties, this.mFailCause, this.mApnSetting);
        }
    }
}
