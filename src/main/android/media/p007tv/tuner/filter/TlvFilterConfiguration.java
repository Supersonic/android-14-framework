package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.TlvFilterConfiguration */
/* loaded from: classes2.dex */
public final class TlvFilterConfiguration extends FilterConfiguration {
    public static final int PACKET_TYPE_COMPRESSED = 3;
    public static final int PACKET_TYPE_IPV4 = 1;
    public static final int PACKET_TYPE_IPV6 = 2;
    public static final int PACKET_TYPE_NULL = 255;
    public static final int PACKET_TYPE_SIGNALING = 254;
    private final boolean mIsCompressedIpPacket;
    private final int mPacketType;
    private final boolean mPassthrough;

    private TlvFilterConfiguration(Settings settings, int packetType, boolean isCompressed, boolean passthrough) {
        super(settings);
        this.mPacketType = packetType;
        this.mIsCompressedIpPacket = isCompressed;
        this.mPassthrough = passthrough;
    }

    @Override // android.media.p007tv.tuner.filter.FilterConfiguration
    public int getType() {
        return 8;
    }

    public int getPacketType() {
        return this.mPacketType;
    }

    public boolean isCompressedIpPacket() {
        return this.mIsCompressedIpPacket;
    }

    public boolean isPassthrough() {
        return this.mPassthrough;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.filter.TlvFilterConfiguration$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private boolean mIsCompressedIpPacket;
        private int mPacketType;
        private boolean mPassthrough;
        private Settings mSettings;

        private Builder() {
            this.mPacketType = 255;
            this.mIsCompressedIpPacket = false;
            this.mPassthrough = false;
        }

        public Builder setPacketType(int packetType) {
            this.mPacketType = packetType;
            return this;
        }

        public Builder setCompressedIpPacket(boolean isCompressedIpPacket) {
            this.mIsCompressedIpPacket = isCompressedIpPacket;
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

        public TlvFilterConfiguration build() {
            return new TlvFilterConfiguration(this.mSettings, this.mPacketType, this.mIsCompressedIpPacket, this.mPassthrough);
        }
    }
}
