package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.AlpFilterConfiguration */
/* loaded from: classes2.dex */
public final class AlpFilterConfiguration extends FilterConfiguration {
    public static final int LENGTH_TYPE_UNDEFINED = 0;
    public static final int LENGTH_TYPE_WITHOUT_ADDITIONAL_HEADER = 1;
    public static final int LENGTH_TYPE_WITH_ADDITIONAL_HEADER = 2;
    public static final int PACKET_TYPE_COMPRESSED = 2;
    public static final int PACKET_TYPE_EXTENSION = 6;
    public static final int PACKET_TYPE_IPV4 = 0;
    public static final int PACKET_TYPE_MPEG2_TS = 7;
    public static final int PACKET_TYPE_SIGNALING = 4;
    private final int mLengthType;
    private final int mPacketType;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.AlpFilterConfiguration$LengthType */
    /* loaded from: classes2.dex */
    public @interface LengthType {
    }

    private AlpFilterConfiguration(Settings settings, int packetType, int lengthType) {
        super(settings);
        this.mPacketType = packetType;
        this.mLengthType = lengthType;
    }

    @Override // android.media.p007tv.tuner.filter.FilterConfiguration
    public int getType() {
        return 16;
    }

    public int getPacketType() {
        return this.mPacketType;
    }

    public int getLengthType() {
        return this.mLengthType;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.filter.AlpFilterConfiguration$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mLengthType;
        private int mPacketType;
        private Settings mSettings;

        private Builder() {
            this.mPacketType = 0;
            this.mLengthType = 0;
        }

        public Builder setPacketType(int packetType) {
            this.mPacketType = packetType;
            return this;
        }

        public Builder setLengthType(int lengthType) {
            this.mLengthType = lengthType;
            return this;
        }

        public Builder setSettings(Settings settings) {
            this.mSettings = settings;
            return this;
        }

        public AlpFilterConfiguration build() {
            return new AlpFilterConfiguration(this.mSettings, this.mPacketType, this.mLengthType);
        }
    }
}
