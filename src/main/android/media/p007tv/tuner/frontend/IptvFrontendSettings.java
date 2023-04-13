package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.IptvFrontendSettings */
/* loaded from: classes2.dex */
public final class IptvFrontendSettings extends FrontendSettings {
    public static final int IGMP_UNDEFINED = 0;
    public static final int IGMP_V1 = 1;
    public static final int IGMP_V2 = 2;
    public static final int IGMP_V3 = 4;
    public static final int PROTOCOL_RTP = 2;
    public static final int PROTOCOL_UDP = 1;
    public static final int PROTOCOL_UNDEFINED = 0;
    private final long mBitrate;
    private final String mContentUrl;
    private final byte[] mDstIpAddress;
    private final int mDstPort;
    private final IptvFrontendSettingsFec mFec;
    private final int mIgmp;
    private final int mProtocol;
    private final byte[] mSrcIpAddress;
    private final int mSrcPort;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IptvFrontendSettings$Igmp */
    /* loaded from: classes2.dex */
    public @interface Igmp {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IptvFrontendSettings$Protocol */
    /* loaded from: classes2.dex */
    public @interface Protocol {
    }

    private IptvFrontendSettings(byte[] srcIpAddress, byte[] dstIpAddress, int srcPort, int dstPort, IptvFrontendSettingsFec fec, int protocol, int igmp, long bitrate, String contentUrl) {
        super(0L);
        this.mSrcIpAddress = srcIpAddress;
        this.mDstIpAddress = dstIpAddress;
        this.mSrcPort = srcPort;
        this.mDstPort = dstPort;
        this.mFec = fec;
        this.mProtocol = protocol;
        this.mIgmp = igmp;
        this.mBitrate = bitrate;
        this.mContentUrl = contentUrl;
    }

    public byte[] getSrcIpAddress() {
        return this.mSrcIpAddress;
    }

    public byte[] getDstIpAddress() {
        return this.mDstIpAddress;
    }

    public int getSrcPort() {
        return this.mSrcPort;
    }

    public int getDstPort() {
        return this.mDstPort;
    }

    public IptvFrontendSettingsFec getFec() {
        return this.mFec;
    }

    public int getProtocol() {
        return this.mProtocol;
    }

    public int getIgmp() {
        return this.mIgmp;
    }

    public long getBitrate() {
        return this.mBitrate;
    }

    public String getContentUrl() {
        return this.mContentUrl;
    }

    /* renamed from: android.media.tv.tuner.frontend.IptvFrontendSettings$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private byte[] mSrcIpAddress = {0, 0, 0, 0};
        private byte[] mDstIpAddress = {0, 0, 0, 0};
        private int mSrcPort = 0;
        private int mDstPort = 0;
        private IptvFrontendSettingsFec mFec = null;
        private int mProtocol = 0;
        private int mIgmp = 0;
        private long mBitrate = 0;
        private String mContentUrl = "";

        public Builder setSrcIpAddress(byte[] srcIpAddress) {
            this.mSrcIpAddress = srcIpAddress;
            return this;
        }

        public Builder setDstIpAddress(byte[] dstIpAddress) {
            this.mDstIpAddress = dstIpAddress;
            return this;
        }

        public Builder setSrcPort(int srcPort) {
            this.mSrcPort = srcPort;
            return this;
        }

        public Builder setDstPort(int dstPort) {
            this.mDstPort = dstPort;
            return this;
        }

        public Builder setFec(IptvFrontendSettingsFec fec) {
            this.mFec = fec;
            return this;
        }

        public Builder setProtocol(int protocol) {
            this.mProtocol = protocol;
            return this;
        }

        public Builder setIgmp(int igmp) {
            this.mIgmp = igmp;
            return this;
        }

        public Builder setBitrate(long bitrate) {
            this.mBitrate = bitrate;
            return this;
        }

        public Builder setContentUrl(String contentUrl) {
            this.mContentUrl = contentUrl;
            return this;
        }

        public IptvFrontendSettings build() {
            return new IptvFrontendSettings(this.mSrcIpAddress, this.mDstIpAddress, this.mSrcPort, this.mDstPort, this.mFec, this.mProtocol, this.mIgmp, this.mBitrate, this.mContentUrl);
        }
    }

    @Override // android.media.p007tv.tuner.frontend.FrontendSettings
    public int getType() {
        return 11;
    }
}
