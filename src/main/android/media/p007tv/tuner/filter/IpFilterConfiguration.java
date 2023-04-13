package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerVersionChecker;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.IpFilterConfiguration */
/* loaded from: classes2.dex */
public final class IpFilterConfiguration extends FilterConfiguration {
    public static final int INVALID_IP_FILTER_CONTEXT_ID = -1;
    private final byte[] mDstIpAddress;
    private final int mDstPort;
    private final int mIpFilterContextId;
    private final boolean mPassthrough;
    private final byte[] mSrcIpAddress;
    private final int mSrcPort;

    private IpFilterConfiguration(Settings settings, byte[] srcAddr, byte[] dstAddr, int srcPort, int dstPort, boolean passthrough, int ipCid) {
        super(settings);
        this.mSrcIpAddress = srcAddr;
        this.mDstIpAddress = dstAddr;
        this.mSrcPort = srcPort;
        this.mDstPort = dstPort;
        this.mPassthrough = passthrough;
        this.mIpFilterContextId = ipCid;
    }

    @Override // android.media.p007tv.tuner.filter.FilterConfiguration
    public int getType() {
        return 4;
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

    public boolean isPassthrough() {
        return this.mPassthrough;
    }

    public int getIpFilterContextId() {
        return this.mIpFilterContextId;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.filter.IpFilterConfiguration$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private byte[] mDstIpAddress;
        private int mDstPort;
        private int mIpCid;
        private boolean mPassthrough;
        private Settings mSettings;
        private byte[] mSrcIpAddress;
        private int mSrcPort;

        private Builder() {
            this.mSrcIpAddress = new byte[]{0, 0, 0, 0};
            this.mDstIpAddress = new byte[]{0, 0, 0, 0};
            this.mSrcPort = 0;
            this.mDstPort = 0;
            this.mPassthrough = false;
            this.mIpCid = -1;
        }

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

        public Builder setPassthrough(boolean passthrough) {
            this.mPassthrough = passthrough;
            return this;
        }

        public Builder setSettings(Settings settings) {
            this.mSettings = settings;
            return this;
        }

        public Builder setIpFilterContextId(int ipContextId) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "setIpFilterContextId")) {
                this.mIpCid = ipContextId;
            }
            return this;
        }

        public IpFilterConfiguration build() {
            byte[] bArr = this.mSrcIpAddress;
            int ipAddrLength = bArr.length;
            byte[] bArr2 = this.mDstIpAddress;
            if (ipAddrLength != bArr2.length || (ipAddrLength != 4 && ipAddrLength != 16)) {
                throw new IllegalArgumentException("The lengths of src and dst IP address must be 4 or 16 and must be the same.srcLength=" + ipAddrLength + ", dstLength=" + this.mDstIpAddress.length);
            }
            return new IpFilterConfiguration(this.mSettings, bArr, bArr2, this.mSrcPort, this.mDstPort, this.mPassthrough, this.mIpCid);
        }
    }
}
