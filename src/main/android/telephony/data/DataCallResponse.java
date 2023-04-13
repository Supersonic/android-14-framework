package android.telephony.data;

import android.annotation.SystemApi;
import android.app.admin.PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2;
import android.net.LinkAddress;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.DataFailCause;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
@SystemApi
/* loaded from: classes3.dex */
public final class DataCallResponse implements Parcelable {
    public static final Parcelable.Creator<DataCallResponse> CREATOR = new Parcelable.Creator<DataCallResponse>() { // from class: android.telephony.data.DataCallResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataCallResponse createFromParcel(Parcel source) {
            return new DataCallResponse(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataCallResponse[] newArray(int size) {
            return new DataCallResponse[size];
        }
    };
    public static final int HANDOVER_FAILURE_MODE_DO_FALLBACK = 1;
    public static final int HANDOVER_FAILURE_MODE_LEGACY = 0;
    public static final int HANDOVER_FAILURE_MODE_NO_FALLBACK_RETRY_HANDOVER = 2;
    public static final int HANDOVER_FAILURE_MODE_NO_FALLBACK_RETRY_SETUP_NORMAL = 3;
    public static final int HANDOVER_FAILURE_MODE_UNKNOWN = -1;
    public static final int LINK_STATUS_ACTIVE = 2;
    public static final int LINK_STATUS_DORMANT = 1;
    public static final int LINK_STATUS_INACTIVE = 0;
    public static final int LINK_STATUS_UNKNOWN = -1;
    public static final int PDU_SESSION_ID_NOT_SET = 0;
    public static final int RETRY_DURATION_UNDEFINED = -1;
    private final List<LinkAddress> mAddresses;
    private final int mCause;
    private final Qos mDefaultQos;
    private final List<InetAddress> mDnsAddresses;
    private final List<InetAddress> mGatewayAddresses;
    private final int mHandoverFailureMode;
    private final int mId;
    private final String mInterfaceName;
    private final int mLinkStatus;
    private final int mMtu;
    private final int mMtuV4;
    private final int mMtuV6;
    private final List<InetAddress> mPcscfAddresses;
    private final int mPduSessionId;
    private final int mProtocolType;
    private final List<QosBearerSession> mQosBearerSessions;
    private final NetworkSliceInfo mSliceInfo;
    private final long mSuggestedRetryTime;
    private final List<TrafficDescriptor> mTrafficDescriptors;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface HandoverFailureMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LinkStatus {
    }

    public DataCallResponse(int cause, int suggestedRetryTime, int id, int linkStatus, int protocolType, String interfaceName, List<LinkAddress> addresses, List<InetAddress> dnsAddresses, List<InetAddress> gatewayAddresses, List<InetAddress> pcscfAddresses, int mtu) {
        this.mCause = cause;
        this.mSuggestedRetryTime = suggestedRetryTime;
        this.mId = id;
        this.mLinkStatus = linkStatus;
        this.mProtocolType = protocolType;
        this.mInterfaceName = interfaceName == null ? "" : interfaceName;
        this.mAddresses = addresses == null ? new ArrayList() : new ArrayList(addresses);
        this.mDnsAddresses = dnsAddresses == null ? new ArrayList() : new ArrayList(dnsAddresses);
        this.mGatewayAddresses = gatewayAddresses == null ? new ArrayList() : new ArrayList(gatewayAddresses);
        this.mPcscfAddresses = pcscfAddresses == null ? new ArrayList() : new ArrayList(pcscfAddresses);
        this.mMtuV6 = mtu;
        this.mMtuV4 = mtu;
        this.mMtu = mtu;
        this.mHandoverFailureMode = 0;
        this.mPduSessionId = 0;
        this.mDefaultQos = null;
        this.mQosBearerSessions = new ArrayList();
        this.mSliceInfo = null;
        this.mTrafficDescriptors = new ArrayList();
    }

    private DataCallResponse(int cause, long suggestedRetryTime, int id, int linkStatus, int protocolType, String interfaceName, List<LinkAddress> addresses, List<InetAddress> dnsAddresses, List<InetAddress> gatewayAddresses, List<InetAddress> pcscfAddresses, int mtu, int mtuV4, int mtuV6, int handoverFailureMode, int pduSessionId, Qos defaultQos, List<QosBearerSession> qosBearerSessions, NetworkSliceInfo sliceInfo, List<TrafficDescriptor> trafficDescriptors) {
        this.mCause = cause;
        this.mSuggestedRetryTime = suggestedRetryTime;
        this.mId = id;
        this.mLinkStatus = linkStatus;
        this.mProtocolType = protocolType;
        this.mInterfaceName = interfaceName == null ? "" : interfaceName;
        this.mAddresses = addresses == null ? new ArrayList() : new ArrayList(addresses);
        this.mDnsAddresses = dnsAddresses == null ? new ArrayList() : new ArrayList(dnsAddresses);
        this.mGatewayAddresses = gatewayAddresses == null ? new ArrayList() : new ArrayList(gatewayAddresses);
        this.mPcscfAddresses = pcscfAddresses == null ? new ArrayList() : new ArrayList(pcscfAddresses);
        this.mMtu = mtu;
        this.mMtuV4 = mtuV4;
        this.mMtuV6 = mtuV6;
        this.mHandoverFailureMode = handoverFailureMode;
        this.mPduSessionId = pduSessionId;
        this.mDefaultQos = defaultQos;
        this.mQosBearerSessions = qosBearerSessions == null ? new ArrayList() : new ArrayList(qosBearerSessions);
        this.mSliceInfo = sliceInfo;
        this.mTrafficDescriptors = trafficDescriptors == null ? new ArrayList() : new ArrayList(trafficDescriptors);
    }

    public DataCallResponse(Parcel source) {
        this.mCause = source.readInt();
        this.mSuggestedRetryTime = source.readLong();
        this.mId = source.readInt();
        this.mLinkStatus = source.readInt();
        this.mProtocolType = source.readInt();
        this.mInterfaceName = source.readString();
        ArrayList arrayList = new ArrayList();
        this.mAddresses = arrayList;
        source.readList(arrayList, LinkAddress.class.getClassLoader(), LinkAddress.class);
        ArrayList arrayList2 = new ArrayList();
        this.mDnsAddresses = arrayList2;
        source.readList(arrayList2, InetAddress.class.getClassLoader(), InetAddress.class);
        ArrayList arrayList3 = new ArrayList();
        this.mGatewayAddresses = arrayList3;
        source.readList(arrayList3, InetAddress.class.getClassLoader(), InetAddress.class);
        ArrayList arrayList4 = new ArrayList();
        this.mPcscfAddresses = arrayList4;
        source.readList(arrayList4, InetAddress.class.getClassLoader(), InetAddress.class);
        this.mMtu = source.readInt();
        this.mMtuV4 = source.readInt();
        this.mMtuV6 = source.readInt();
        this.mHandoverFailureMode = source.readInt();
        this.mPduSessionId = source.readInt();
        this.mDefaultQos = (Qos) source.readParcelable(Qos.class.getClassLoader(), Qos.class);
        ArrayList arrayList5 = new ArrayList();
        this.mQosBearerSessions = arrayList5;
        source.readList(arrayList5, QosBearerSession.class.getClassLoader(), QosBearerSession.class);
        this.mSliceInfo = (NetworkSliceInfo) source.readParcelable(NetworkSliceInfo.class.getClassLoader(), NetworkSliceInfo.class);
        ArrayList arrayList6 = new ArrayList();
        this.mTrafficDescriptors = arrayList6;
        source.readList(arrayList6, TrafficDescriptor.class.getClassLoader(), TrafficDescriptor.class);
    }

    public int getCause() {
        return this.mCause;
    }

    @Deprecated
    public int getSuggestedRetryTime() {
        long j = this.mSuggestedRetryTime;
        if (j == -1) {
            return 0;
        }
        if (j > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) j;
    }

    public long getRetryDurationMillis() {
        return this.mSuggestedRetryTime;
    }

    public int getId() {
        return this.mId;
    }

    public int getLinkStatus() {
        return this.mLinkStatus;
    }

    public int getProtocolType() {
        return this.mProtocolType;
    }

    public String getInterfaceName() {
        return this.mInterfaceName;
    }

    public List<LinkAddress> getAddresses() {
        return this.mAddresses;
    }

    public List<InetAddress> getDnsAddresses() {
        return this.mDnsAddresses;
    }

    public List<InetAddress> getGatewayAddresses() {
        return this.mGatewayAddresses;
    }

    public List<InetAddress> getPcscfAddresses() {
        return this.mPcscfAddresses;
    }

    @Deprecated
    public int getMtu() {
        return this.mMtu;
    }

    public int getMtuV4() {
        return this.mMtuV4;
    }

    public int getMtuV6() {
        return this.mMtuV6;
    }

    public int getHandoverFailureMode() {
        return this.mHandoverFailureMode;
    }

    public int getPduSessionId() {
        return this.mPduSessionId;
    }

    public Qos getDefaultQos() {
        return this.mDefaultQos;
    }

    public List<QosBearerSession> getQosBearerSessions() {
        return this.mQosBearerSessions;
    }

    public NetworkSliceInfo getSliceInfo() {
        return this.mSliceInfo;
    }

    public List<TrafficDescriptor> getTrafficDescriptors() {
        return this.mTrafficDescriptors;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("DataCallResponse: {").append(" cause=").append(DataFailCause.toString(this.mCause)).append(" retry=").append(this.mSuggestedRetryTime).append(" cid=").append(this.mId).append(" linkStatus=").append(this.mLinkStatus).append(" protocolType=").append(this.mProtocolType).append(" ifname=").append(this.mInterfaceName).append(" addresses=").append(this.mAddresses).append(" dnses=").append(this.mDnsAddresses).append(" gateways=").append(this.mGatewayAddresses).append(" pcscf=").append(this.mPcscfAddresses).append(" mtu=").append(getMtu()).append(" mtuV4=").append(getMtuV4()).append(" mtuV6=").append(getMtuV6()).append(" handoverFailureMode=").append(failureModeToString(this.mHandoverFailureMode)).append(" pduSessionId=").append(getPduSessionId()).append(" defaultQos=").append(this.mDefaultQos).append(" qosBearerSessions=").append(this.mQosBearerSessions).append(" sliceInfo=").append(this.mSliceInfo).append(" trafficDescriptors=").append(this.mTrafficDescriptors).append("}");
        return sb.toString();
    }

    public boolean equals(Object o) {
        boolean isQosSame;
        boolean isQosBearerSessionsSame;
        boolean isTrafficDescriptorsSame;
        Qos qos;
        if (this == o) {
            return true;
        }
        if (o instanceof DataCallResponse) {
            DataCallResponse other = (DataCallResponse) o;
            Qos qos2 = this.mDefaultQos;
            if (qos2 == null || (qos = other.mDefaultQos) == null) {
                isQosSame = qos2 == other.mDefaultQos;
            } else {
                isQosSame = qos2.equals(qos);
            }
            List<QosBearerSession> list = this.mQosBearerSessions;
            if (list == null || other.mQosBearerSessions == null) {
                isQosBearerSessionsSame = list == other.mQosBearerSessions;
            } else {
                isQosBearerSessionsSame = list.size() == other.mQosBearerSessions.size() && this.mQosBearerSessions.containsAll(other.mQosBearerSessions);
            }
            List<TrafficDescriptor> list2 = this.mTrafficDescriptors;
            if (list2 == null || other.mTrafficDescriptors == null) {
                isTrafficDescriptorsSame = list2 == other.mTrafficDescriptors;
            } else {
                isTrafficDescriptorsSame = list2.size() == other.mTrafficDescriptors.size() && this.mTrafficDescriptors.containsAll(other.mTrafficDescriptors);
            }
            return this.mCause == other.mCause && this.mSuggestedRetryTime == other.mSuggestedRetryTime && this.mId == other.mId && this.mLinkStatus == other.mLinkStatus && this.mProtocolType == other.mProtocolType && this.mInterfaceName.equals(other.mInterfaceName) && this.mAddresses.size() == other.mAddresses.size() && this.mAddresses.containsAll(other.mAddresses) && this.mDnsAddresses.size() == other.mDnsAddresses.size() && this.mDnsAddresses.containsAll(other.mDnsAddresses) && this.mGatewayAddresses.size() == other.mGatewayAddresses.size() && this.mGatewayAddresses.containsAll(other.mGatewayAddresses) && this.mPcscfAddresses.size() == other.mPcscfAddresses.size() && this.mPcscfAddresses.containsAll(other.mPcscfAddresses) && this.mMtu == other.mMtu && this.mMtuV4 == other.mMtuV4 && this.mMtuV6 == other.mMtuV6 && this.mHandoverFailureMode == other.mHandoverFailureMode && this.mPduSessionId == other.mPduSessionId && isQosSame && isQosBearerSessionsSame && Objects.equals(this.mSliceInfo, other.mSliceInfo) && isTrafficDescriptorsSame;
        }
        return false;
    }

    public int hashCode() {
        int addressesHash = this.mAddresses.stream().map(new Function() { // from class: android.telephony.data.DataCallResponse$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((LinkAddress) obj).hashCode());
            }
        }).mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).sum();
        int dnsAddressesHash = this.mDnsAddresses.stream().map(new Function() { // from class: android.telephony.data.DataCallResponse$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((InetAddress) obj).hashCode());
            }
        }).mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).sum();
        int gatewayAddressesHash = this.mGatewayAddresses.stream().map(new Function() { // from class: android.telephony.data.DataCallResponse$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((InetAddress) obj).hashCode());
            }
        }).mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).sum();
        int pcscfAddressesHash = this.mPcscfAddresses.stream().map(new Function() { // from class: android.telephony.data.DataCallResponse$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((InetAddress) obj).hashCode());
            }
        }).mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).sum();
        int qosBearerSessionsHash = this.mQosBearerSessions.stream().map(new Function() { // from class: android.telephony.data.DataCallResponse$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((QosBearerSession) obj).hashCode());
            }
        }).mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).sum();
        int trafficDescriptorsHash = this.mTrafficDescriptors.stream().map(new Function() { // from class: android.telephony.data.DataCallResponse$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((TrafficDescriptor) obj).hashCode());
            }
        }).mapToInt(new PreferentialNetworkServiceConfig$$ExternalSyntheticLambda2()).sum();
        return Objects.hash(Integer.valueOf(this.mCause), Long.valueOf(this.mSuggestedRetryTime), Integer.valueOf(this.mId), Integer.valueOf(this.mLinkStatus), Integer.valueOf(this.mProtocolType), this.mInterfaceName, Integer.valueOf(addressesHash), Integer.valueOf(dnsAddressesHash), Integer.valueOf(gatewayAddressesHash), Integer.valueOf(pcscfAddressesHash), Integer.valueOf(this.mMtu), Integer.valueOf(this.mMtuV4), Integer.valueOf(this.mMtuV6), Integer.valueOf(this.mHandoverFailureMode), Integer.valueOf(this.mPduSessionId), this.mDefaultQos, Integer.valueOf(qosBearerSessionsHash), this.mSliceInfo, Integer.valueOf(trafficDescriptorsHash));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCause);
        dest.writeLong(this.mSuggestedRetryTime);
        dest.writeInt(this.mId);
        dest.writeInt(this.mLinkStatus);
        dest.writeInt(this.mProtocolType);
        dest.writeString(this.mInterfaceName);
        dest.writeList(this.mAddresses);
        dest.writeList(this.mDnsAddresses);
        dest.writeList(this.mGatewayAddresses);
        dest.writeList(this.mPcscfAddresses);
        dest.writeInt(this.mMtu);
        dest.writeInt(this.mMtuV4);
        dest.writeInt(this.mMtuV6);
        dest.writeInt(this.mHandoverFailureMode);
        dest.writeInt(this.mPduSessionId);
        Qos qos = this.mDefaultQos;
        if (qos != null) {
            if (qos.getType() == 1) {
                dest.writeParcelable((EpsQos) this.mDefaultQos, flags);
            } else {
                dest.writeParcelable((NrQos) this.mDefaultQos, flags);
            }
        } else {
            dest.writeParcelable(null, flags);
        }
        dest.writeList(this.mQosBearerSessions);
        dest.writeParcelable(this.mSliceInfo, flags);
        dest.writeList(this.mTrafficDescriptors);
    }

    public static String failureModeToString(int handoverFailureMode) {
        switch (handoverFailureMode) {
            case -1:
                return "unknown";
            case 0:
                return "legacy";
            case 1:
                return "fallback";
            case 2:
                return "retry handover";
            case 3:
                return "retry setup new one";
            default:
                return Integer.toString(handoverFailureMode);
        }
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private List<LinkAddress> mAddresses;
        private int mCause;
        private Qos mDefaultQos;
        private List<InetAddress> mDnsAddresses;
        private List<InetAddress> mGatewayAddresses;
        private int mId;
        private String mInterfaceName;
        private int mLinkStatus;
        private int mMtu;
        private int mMtuV4;
        private int mMtuV6;
        private List<InetAddress> mPcscfAddresses;
        private int mProtocolType;
        private NetworkSliceInfo mSliceInfo;
        private long mSuggestedRetryTime = -1;
        private int mHandoverFailureMode = 0;
        private int mPduSessionId = 0;
        private List<QosBearerSession> mQosBearerSessions = new ArrayList();
        private List<TrafficDescriptor> mTrafficDescriptors = new ArrayList();

        public Builder setCause(int cause) {
            this.mCause = cause;
            return this;
        }

        @Deprecated
        public Builder setSuggestedRetryTime(int suggestedRetryTime) {
            this.mSuggestedRetryTime = suggestedRetryTime;
            return this;
        }

        public Builder setRetryDurationMillis(long retryDurationMillis) {
            this.mSuggestedRetryTime = retryDurationMillis;
            return this;
        }

        public Builder setId(int id) {
            this.mId = id;
            return this;
        }

        public Builder setLinkStatus(int linkStatus) {
            this.mLinkStatus = linkStatus;
            return this;
        }

        public Builder setProtocolType(int protocolType) {
            this.mProtocolType = protocolType;
            return this;
        }

        public Builder setInterfaceName(String interfaceName) {
            this.mInterfaceName = interfaceName;
            return this;
        }

        public Builder setAddresses(List<LinkAddress> addresses) {
            this.mAddresses = addresses;
            return this;
        }

        public Builder setDnsAddresses(List<InetAddress> dnsAddresses) {
            this.mDnsAddresses = dnsAddresses;
            return this;
        }

        public Builder setGatewayAddresses(List<InetAddress> gatewayAddresses) {
            this.mGatewayAddresses = gatewayAddresses;
            return this;
        }

        public Builder setPcscfAddresses(List<InetAddress> pcscfAddresses) {
            this.mPcscfAddresses = pcscfAddresses;
            return this;
        }

        public Builder setMtu(int mtu) {
            this.mMtu = mtu;
            return this;
        }

        public Builder setMtuV4(int mtu) {
            this.mMtuV4 = mtu;
            return this;
        }

        public Builder setMtuV6(int mtu) {
            this.mMtuV6 = mtu;
            return this;
        }

        public Builder setHandoverFailureMode(int failureMode) {
            this.mHandoverFailureMode = failureMode;
            return this;
        }

        public Builder setPduSessionId(int pduSessionId) {
            Preconditions.checkArgument(pduSessionId >= 0, "pduSessionId must be greater than or equal to0");
            Preconditions.checkArgument(pduSessionId <= 15, "pduSessionId must be less than or equal to 15.");
            this.mPduSessionId = pduSessionId;
            return this;
        }

        public Builder setDefaultQos(Qos defaultQos) {
            this.mDefaultQos = defaultQos;
            return this;
        }

        public Builder setQosBearerSessions(List<QosBearerSession> qosBearerSessions) {
            Objects.requireNonNull(qosBearerSessions);
            this.mQosBearerSessions = qosBearerSessions;
            return this;
        }

        public Builder setSliceInfo(NetworkSliceInfo sliceInfo) {
            this.mSliceInfo = sliceInfo;
            return this;
        }

        public Builder setTrafficDescriptors(List<TrafficDescriptor> trafficDescriptors) {
            Objects.requireNonNull(trafficDescriptors);
            this.mTrafficDescriptors = trafficDescriptors;
            return this;
        }

        public DataCallResponse build() {
            return new DataCallResponse(this.mCause, this.mSuggestedRetryTime, this.mId, this.mLinkStatus, this.mProtocolType, this.mInterfaceName, this.mAddresses, this.mDnsAddresses, this.mGatewayAddresses, this.mPcscfAddresses, this.mMtu, this.mMtuV4, this.mMtuV6, this.mHandoverFailureMode, this.mPduSessionId, this.mDefaultQos, this.mQosBearerSessions, this.mSliceInfo, this.mTrafficDescriptors);
        }
    }
}
