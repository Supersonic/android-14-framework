package android.telephony.ims;

import android.annotation.SystemApi;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class SipDelegateConfiguration implements Parcelable {
    public static final Parcelable.Creator<SipDelegateConfiguration> CREATOR = new Parcelable.Creator<SipDelegateConfiguration>() { // from class: android.telephony.ims.SipDelegateConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SipDelegateConfiguration createFromParcel(Parcel source) {
            return new SipDelegateConfiguration(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SipDelegateConfiguration[] newArray(int size) {
            return new SipDelegateConfiguration[size];
        }
    };
    public static final int SIP_TRANSPORT_TCP = 1;
    public static final int SIP_TRANSPORT_UDP = 0;
    public static final int UDP_PAYLOAD_SIZE_UNDEFINED = -1;
    private String mAssociatedUriHeader;
    private String mCniHeader;
    private String mContactUserParam;
    private Uri mGruu;
    private String mHomeDomain;
    private String mImei;
    private IpSecConfiguration mIpSecConfiguration;
    private boolean mIsSipCompactFormEnabled;
    private boolean mIsSipKeepaliveEnabled;
    private final InetSocketAddress mLocalAddress;
    private int mMaxUdpPayloadSize;
    private InetSocketAddress mNatAddress;
    private String mPaniHeader;
    private String mPathHeader;
    private String mPlaniHeader;
    private String mPrivateUserIdentifier;
    private String mPublicUserIdentifier;
    private String mServiceRouteHeader;
    private String mSipAuthHeader;
    private String mSipAuthNonce;
    private final InetSocketAddress mSipServerAddress;
    private final int mTransportType;
    private String mUserAgentHeader;
    private final long mVersion;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface TransportType {
    }

    /* loaded from: classes3.dex */
    public static final class IpSecConfiguration {
        private final int mLastLocalTxPort;
        private final int mLastRemoteTxPort;
        private final int mLocalRxPort;
        private final int mLocalTxPort;
        private final int mRemoteRxPort;
        private final int mRemoteTxPort;
        private final String mSecurityHeader;

        public IpSecConfiguration(int localTxPort, int localRxPort, int lastLocalTxPort, int remoteTxPort, int remoteRxPort, int lastRemoteTxPort, String securityHeader) {
            this.mLocalTxPort = localTxPort;
            this.mLocalRxPort = localRxPort;
            this.mLastLocalTxPort = lastLocalTxPort;
            this.mRemoteTxPort = remoteTxPort;
            this.mRemoteRxPort = remoteRxPort;
            this.mLastRemoteTxPort = lastRemoteTxPort;
            this.mSecurityHeader = securityHeader;
        }

        public int getLocalTxPort() {
            return this.mLocalTxPort;
        }

        public int getLocalRxPort() {
            return this.mLocalRxPort;
        }

        public int getLastLocalTxPort() {
            return this.mLastLocalTxPort;
        }

        public int getRemoteTxPort() {
            return this.mRemoteTxPort;
        }

        public int getRemoteRxPort() {
            return this.mRemoteRxPort;
        }

        public int getLastRemoteTxPort() {
            return this.mLastRemoteTxPort;
        }

        public String getSipSecurityVerifyHeader() {
            return this.mSecurityHeader;
        }

        public void addToParcel(Parcel dest) {
            dest.writeInt(this.mLocalTxPort);
            dest.writeInt(this.mLocalRxPort);
            dest.writeInt(this.mLastLocalTxPort);
            dest.writeInt(this.mRemoteTxPort);
            dest.writeInt(this.mRemoteRxPort);
            dest.writeInt(this.mLastRemoteTxPort);
            dest.writeString(this.mSecurityHeader);
        }

        public static IpSecConfiguration fromParcel(Parcel source) {
            return new IpSecConfiguration(source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readString());
        }

        public String toString() {
            return "IpSecConfiguration{localTx=" + this.mLocalTxPort + ", localRx=" + this.mLocalRxPort + ", lastLocalTx=" + this.mLastLocalTxPort + ", remoteTx=" + this.mRemoteTxPort + ", remoteRx=" + this.mRemoteRxPort + ", lastRemoteTx=" + this.mLastRemoteTxPort + ", securityHeader=" + this.mSecurityHeader + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IpSecConfiguration that = (IpSecConfiguration) o;
            if (this.mLocalTxPort == that.mLocalTxPort && this.mLocalRxPort == that.mLocalRxPort && this.mLastLocalTxPort == that.mLastLocalTxPort && this.mRemoteTxPort == that.mRemoteTxPort && this.mRemoteRxPort == that.mRemoteRxPort && this.mLastRemoteTxPort == that.mLastRemoteTxPort && Objects.equals(this.mSecurityHeader, that.mSecurityHeader)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mLocalTxPort), Integer.valueOf(this.mLocalRxPort), Integer.valueOf(this.mLastLocalTxPort), Integer.valueOf(this.mRemoteTxPort), Integer.valueOf(this.mRemoteRxPort), Integer.valueOf(this.mLastRemoteTxPort), this.mSecurityHeader);
        }
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private final SipDelegateConfiguration mConfig;

        public Builder(long version, int transportType, InetSocketAddress localAddr, InetSocketAddress serverAddr) {
            this.mConfig = new SipDelegateConfiguration(version, transportType, localAddr, serverAddr);
        }

        public Builder(SipDelegateConfiguration c) {
            this.mConfig = c.copyAndIncrementVersion();
        }

        public Builder setSipCompactFormEnabled(boolean isEnabled) {
            this.mConfig.mIsSipCompactFormEnabled = isEnabled;
            return this;
        }

        public Builder setSipKeepaliveEnabled(boolean isEnabled) {
            this.mConfig.mIsSipKeepaliveEnabled = isEnabled;
            return this;
        }

        public Builder setMaxUdpPayloadSizeBytes(int size) {
            this.mConfig.mMaxUdpPayloadSize = size;
            return this;
        }

        public Builder setPublicUserIdentifier(String id) {
            this.mConfig.mPublicUserIdentifier = id;
            return this;
        }

        public Builder setPrivateUserIdentifier(String id) {
            this.mConfig.mPrivateUserIdentifier = id;
            return this;
        }

        public Builder setHomeDomain(String domain) {
            this.mConfig.mHomeDomain = domain;
            return this;
        }

        public Builder setImei(String imei) {
            this.mConfig.mImei = imei;
            return this;
        }

        public Builder setIpSecConfiguration(IpSecConfiguration c) {
            this.mConfig.mIpSecConfiguration = c;
            return this;
        }

        public Builder setNatSocketAddress(InetSocketAddress addr) {
            this.mConfig.mNatAddress = addr;
            return this;
        }

        public Builder setPublicGruuUri(Uri uri) {
            this.mConfig.mGruu = uri;
            return this;
        }

        public Builder setSipAuthenticationHeader(String header) {
            this.mConfig.mSipAuthHeader = header;
            return this;
        }

        public Builder setSipAuthenticationNonce(String nonce) {
            this.mConfig.mSipAuthNonce = nonce;
            return this;
        }

        public Builder setSipServiceRouteHeader(String header) {
            this.mConfig.mServiceRouteHeader = header;
            return this;
        }

        public Builder setSipPathHeader(String header) {
            this.mConfig.mPathHeader = header;
            return this;
        }

        public Builder setSipUserAgentHeader(String header) {
            this.mConfig.mUserAgentHeader = header;
            return this;
        }

        public Builder setSipContactUserParameter(String param) {
            this.mConfig.mContactUserParam = param;
            return this;
        }

        public Builder setSipPaniHeader(String header) {
            this.mConfig.mPaniHeader = header;
            return this;
        }

        public Builder setSipPlaniHeader(String header) {
            this.mConfig.mPlaniHeader = header;
            return this;
        }

        public Builder setSipCniHeader(String header) {
            this.mConfig.mCniHeader = header;
            return this;
        }

        public Builder setSipAssociatedUriHeader(String header) {
            this.mConfig.mAssociatedUriHeader = header;
            return this;
        }

        public SipDelegateConfiguration build() {
            return this.mConfig;
        }
    }

    private SipDelegateConfiguration(long version, int transportType, InetSocketAddress localAddress, InetSocketAddress sipServerAddress) {
        this.mIsSipCompactFormEnabled = false;
        this.mIsSipKeepaliveEnabled = false;
        this.mMaxUdpPayloadSize = -1;
        this.mPublicUserIdentifier = null;
        this.mPrivateUserIdentifier = null;
        this.mHomeDomain = null;
        this.mImei = null;
        this.mGruu = null;
        this.mSipAuthHeader = null;
        this.mSipAuthNonce = null;
        this.mServiceRouteHeader = null;
        this.mPathHeader = null;
        this.mUserAgentHeader = null;
        this.mContactUserParam = null;
        this.mPaniHeader = null;
        this.mPlaniHeader = null;
        this.mCniHeader = null;
        this.mAssociatedUriHeader = null;
        this.mIpSecConfiguration = null;
        this.mNatAddress = null;
        this.mVersion = version;
        this.mTransportType = transportType;
        this.mLocalAddress = localAddress;
        this.mSipServerAddress = sipServerAddress;
    }

    private SipDelegateConfiguration(Parcel source) {
        this.mIsSipCompactFormEnabled = false;
        this.mIsSipKeepaliveEnabled = false;
        this.mMaxUdpPayloadSize = -1;
        this.mPublicUserIdentifier = null;
        this.mPrivateUserIdentifier = null;
        this.mHomeDomain = null;
        this.mImei = null;
        this.mGruu = null;
        this.mSipAuthHeader = null;
        this.mSipAuthNonce = null;
        this.mServiceRouteHeader = null;
        this.mPathHeader = null;
        this.mUserAgentHeader = null;
        this.mContactUserParam = null;
        this.mPaniHeader = null;
        this.mPlaniHeader = null;
        this.mCniHeader = null;
        this.mAssociatedUriHeader = null;
        this.mIpSecConfiguration = null;
        this.mNatAddress = null;
        this.mVersion = source.readLong();
        this.mTransportType = source.readInt();
        this.mLocalAddress = readAddressFromParcel(source);
        this.mSipServerAddress = readAddressFromParcel(source);
        this.mIsSipCompactFormEnabled = source.readBoolean();
        this.mIsSipKeepaliveEnabled = source.readBoolean();
        this.mMaxUdpPayloadSize = source.readInt();
        this.mPublicUserIdentifier = source.readString();
        this.mPrivateUserIdentifier = source.readString();
        this.mHomeDomain = source.readString();
        this.mImei = source.readString();
        this.mGruu = (Uri) source.readParcelable(null, Uri.class);
        this.mSipAuthHeader = source.readString();
        this.mSipAuthNonce = source.readString();
        this.mServiceRouteHeader = source.readString();
        this.mPathHeader = source.readString();
        this.mUserAgentHeader = source.readString();
        this.mContactUserParam = source.readString();
        this.mPaniHeader = source.readString();
        this.mPlaniHeader = source.readString();
        this.mCniHeader = source.readString();
        this.mAssociatedUriHeader = source.readString();
        boolean isIpsecConfigAvailable = source.readBoolean();
        if (isIpsecConfigAvailable) {
            this.mIpSecConfiguration = IpSecConfiguration.fromParcel(source);
        }
        boolean isNatConfigAvailable = source.readBoolean();
        if (isNatConfigAvailable) {
            this.mNatAddress = readAddressFromParcel(source);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mVersion);
        dest.writeInt(this.mTransportType);
        writeAddressToParcel(this.mLocalAddress, dest);
        writeAddressToParcel(this.mSipServerAddress, dest);
        dest.writeBoolean(this.mIsSipCompactFormEnabled);
        dest.writeBoolean(this.mIsSipKeepaliveEnabled);
        dest.writeInt(this.mMaxUdpPayloadSize);
        dest.writeString(this.mPublicUserIdentifier);
        dest.writeString(this.mPrivateUserIdentifier);
        dest.writeString(this.mHomeDomain);
        dest.writeString(this.mImei);
        dest.writeParcelable(this.mGruu, flags);
        dest.writeString(this.mSipAuthHeader);
        dest.writeString(this.mSipAuthNonce);
        dest.writeString(this.mServiceRouteHeader);
        dest.writeString(this.mPathHeader);
        dest.writeString(this.mUserAgentHeader);
        dest.writeString(this.mContactUserParam);
        dest.writeString(this.mPaniHeader);
        dest.writeString(this.mPlaniHeader);
        dest.writeString(this.mCniHeader);
        dest.writeString(this.mAssociatedUriHeader);
        dest.writeBoolean(this.mIpSecConfiguration != null);
        IpSecConfiguration ipSecConfiguration = this.mIpSecConfiguration;
        if (ipSecConfiguration != null) {
            ipSecConfiguration.addToParcel(dest);
        }
        dest.writeBoolean(this.mNatAddress != null);
        InetSocketAddress inetSocketAddress = this.mNatAddress;
        if (inetSocketAddress != null) {
            writeAddressToParcel(inetSocketAddress, dest);
        }
    }

    public SipDelegateConfiguration copyAndIncrementVersion() {
        SipDelegateConfiguration c = new SipDelegateConfiguration(getVersion() + 1, this.mTransportType, this.mLocalAddress, this.mSipServerAddress);
        c.mIsSipCompactFormEnabled = this.mIsSipCompactFormEnabled;
        c.mIsSipKeepaliveEnabled = this.mIsSipKeepaliveEnabled;
        c.mMaxUdpPayloadSize = this.mMaxUdpPayloadSize;
        c.mIpSecConfiguration = this.mIpSecConfiguration;
        c.mNatAddress = this.mNatAddress;
        c.mPublicUserIdentifier = this.mPublicUserIdentifier;
        c.mPrivateUserIdentifier = this.mPrivateUserIdentifier;
        c.mHomeDomain = this.mHomeDomain;
        c.mImei = this.mImei;
        c.mGruu = this.mGruu;
        c.mSipAuthHeader = this.mSipAuthHeader;
        c.mSipAuthNonce = this.mSipAuthNonce;
        c.mServiceRouteHeader = this.mServiceRouteHeader;
        c.mPathHeader = this.mPathHeader;
        c.mUserAgentHeader = this.mUserAgentHeader;
        c.mContactUserParam = this.mContactUserParam;
        c.mPaniHeader = this.mPaniHeader;
        c.mPlaniHeader = this.mPlaniHeader;
        c.mCniHeader = this.mCniHeader;
        c.mAssociatedUriHeader = this.mAssociatedUriHeader;
        return c;
    }

    public long getVersion() {
        return this.mVersion;
    }

    public int getTransportType() {
        return this.mTransportType;
    }

    public InetSocketAddress getLocalAddress() {
        return this.mLocalAddress;
    }

    public InetSocketAddress getSipServerAddress() {
        return this.mSipServerAddress;
    }

    public boolean isSipCompactFormEnabled() {
        return this.mIsSipCompactFormEnabled;
    }

    public boolean isSipKeepaliveEnabled() {
        return this.mIsSipKeepaliveEnabled;
    }

    public int getMaxUdpPayloadSizeBytes() {
        return this.mMaxUdpPayloadSize;
    }

    public String getPublicUserIdentifier() {
        return this.mPublicUserIdentifier;
    }

    public String getPrivateUserIdentifier() {
        return this.mPrivateUserIdentifier;
    }

    public String getHomeDomain() {
        return this.mHomeDomain;
    }

    public String getImei() {
        return this.mImei;
    }

    public IpSecConfiguration getIpSecConfiguration() {
        return this.mIpSecConfiguration;
    }

    public InetSocketAddress getNatSocketAddress() {
        return this.mNatAddress;
    }

    public Uri getPublicGruuUri() {
        return this.mGruu;
    }

    public String getSipAuthenticationHeader() {
        return this.mSipAuthHeader;
    }

    public String getSipAuthenticationNonce() {
        return this.mSipAuthNonce;
    }

    public String getSipServiceRouteHeader() {
        return this.mServiceRouteHeader;
    }

    public String getSipPathHeader() {
        return this.mPathHeader;
    }

    public String getSipUserAgentHeader() {
        return this.mUserAgentHeader;
    }

    public String getSipContactUserParameter() {
        return this.mContactUserParam;
    }

    public String getSipPaniHeader() {
        return this.mPaniHeader;
    }

    public String getSipPlaniHeader() {
        return this.mPlaniHeader;
    }

    public String getSipCniHeader() {
        return this.mCniHeader;
    }

    public String getSipAssociatedUriHeader() {
        return this.mAssociatedUriHeader;
    }

    private void writeAddressToParcel(InetSocketAddress addr, Parcel dest) {
        dest.writeByteArray(addr.getAddress().getAddress());
        dest.writeInt(addr.getPort());
    }

    private InetSocketAddress readAddressFromParcel(Parcel source) {
        byte[] addressBytes = source.createByteArray();
        int port = source.readInt();
        try {
            return new InetSocketAddress(InetAddress.getByAddress(addressBytes), port);
        } catch (UnknownHostException e) {
            Log.m110e("SipDelegateConfiguration", "exception reading address, returning null");
            return null;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SipDelegateConfiguration that = (SipDelegateConfiguration) o;
        if (this.mVersion == that.mVersion && this.mTransportType == that.mTransportType && this.mIsSipCompactFormEnabled == that.mIsSipCompactFormEnabled && this.mIsSipKeepaliveEnabled == that.mIsSipKeepaliveEnabled && this.mMaxUdpPayloadSize == that.mMaxUdpPayloadSize && Objects.equals(this.mLocalAddress, that.mLocalAddress) && Objects.equals(this.mSipServerAddress, that.mSipServerAddress) && Objects.equals(this.mPublicUserIdentifier, that.mPublicUserIdentifier) && Objects.equals(this.mPrivateUserIdentifier, that.mPrivateUserIdentifier) && Objects.equals(this.mHomeDomain, that.mHomeDomain) && Objects.equals(this.mImei, that.mImei) && Objects.equals(this.mGruu, that.mGruu) && Objects.equals(this.mSipAuthHeader, that.mSipAuthHeader) && Objects.equals(this.mSipAuthNonce, that.mSipAuthNonce) && Objects.equals(this.mServiceRouteHeader, that.mServiceRouteHeader) && Objects.equals(this.mPathHeader, that.mPathHeader) && Objects.equals(this.mUserAgentHeader, that.mUserAgentHeader) && Objects.equals(this.mContactUserParam, that.mContactUserParam) && Objects.equals(this.mPaniHeader, that.mPaniHeader) && Objects.equals(this.mPlaniHeader, that.mPlaniHeader) && Objects.equals(this.mCniHeader, that.mCniHeader) && Objects.equals(this.mAssociatedUriHeader, that.mAssociatedUriHeader) && Objects.equals(this.mIpSecConfiguration, that.mIpSecConfiguration) && Objects.equals(this.mNatAddress, that.mNatAddress)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mVersion), Integer.valueOf(this.mTransportType), this.mLocalAddress, this.mSipServerAddress, Boolean.valueOf(this.mIsSipCompactFormEnabled), Boolean.valueOf(this.mIsSipKeepaliveEnabled), Integer.valueOf(this.mMaxUdpPayloadSize), this.mPublicUserIdentifier, this.mPrivateUserIdentifier, this.mHomeDomain, this.mImei, this.mGruu, this.mSipAuthHeader, this.mSipAuthNonce, this.mServiceRouteHeader, this.mPathHeader, this.mUserAgentHeader, this.mContactUserParam, this.mPaniHeader, this.mPlaniHeader, this.mCniHeader, this.mAssociatedUriHeader, this.mIpSecConfiguration, this.mNatAddress);
    }

    public String toString() {
        return "SipDelegateConfiguration{ mVersion=" + this.mVersion + ", mTransportType=" + this.mTransportType + '}';
    }
}
