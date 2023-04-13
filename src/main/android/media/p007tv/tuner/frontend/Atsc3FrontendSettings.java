package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendSettings */
/* loaded from: classes2.dex */
public class Atsc3FrontendSettings extends FrontendSettings {
    public static final int BANDWIDTH_AUTO = 1;
    public static final int BANDWIDTH_BANDWIDTH_6MHZ = 2;
    public static final int BANDWIDTH_BANDWIDTH_7MHZ = 4;
    public static final int BANDWIDTH_BANDWIDTH_8MHZ = 8;
    public static final int BANDWIDTH_UNDEFINED = 0;
    public static final int CODERATE_10_15 = 512;
    public static final int CODERATE_11_15 = 1024;
    public static final int CODERATE_12_15 = 2048;
    public static final int CODERATE_13_15 = 4096;
    public static final int CODERATE_2_15 = 2;
    public static final int CODERATE_3_15 = 4;
    public static final int CODERATE_4_15 = 8;
    public static final int CODERATE_5_15 = 16;
    public static final int CODERATE_6_15 = 32;
    public static final int CODERATE_7_15 = 64;
    public static final int CODERATE_8_15 = 128;
    public static final int CODERATE_9_15 = 256;
    public static final int CODERATE_AUTO = 1;
    public static final int CODERATE_UNDEFINED = 0;
    public static final int DEMOD_OUTPUT_FORMAT_ATSC3_LINKLAYER_PACKET = 1;
    public static final int DEMOD_OUTPUT_FORMAT_BASEBAND_PACKET = 2;
    public static final int DEMOD_OUTPUT_FORMAT_UNDEFINED = 0;
    public static final int FEC_AUTO = 1;
    public static final int FEC_BCH_LDPC_16K = 2;
    public static final int FEC_BCH_LDPC_64K = 4;
    public static final int FEC_CRC_LDPC_16K = 8;
    public static final int FEC_CRC_LDPC_64K = 16;
    public static final int FEC_LDPC_16K = 32;
    public static final int FEC_LDPC_64K = 64;
    public static final int FEC_UNDEFINED = 0;
    public static final int MODULATION_AUTO = 1;
    public static final int MODULATION_MOD_1024QAM = 32;
    public static final int MODULATION_MOD_16QAM = 4;
    public static final int MODULATION_MOD_256QAM = 16;
    public static final int MODULATION_MOD_4096QAM = 64;
    public static final int MODULATION_MOD_64QAM = 8;
    public static final int MODULATION_MOD_QPSK = 2;
    public static final int MODULATION_UNDEFINED = 0;
    public static final int TIME_INTERLEAVE_MODE_AUTO = 1;
    public static final int TIME_INTERLEAVE_MODE_CTI = 2;
    public static final int TIME_INTERLEAVE_MODE_HTI = 4;
    public static final int TIME_INTERLEAVE_MODE_UNDEFINED = 0;
    private final int mBandwidth;
    private final int mDemodOutputFormat;
    private final Atsc3PlpSettings[] mPlpSettings;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendSettings$Bandwidth */
    /* loaded from: classes2.dex */
    public @interface Bandwidth {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendSettings$CodeRate */
    /* loaded from: classes2.dex */
    public @interface CodeRate {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendSettings$DemodOutputFormat */
    /* loaded from: classes2.dex */
    public @interface DemodOutputFormat {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendSettings$Fec */
    /* loaded from: classes2.dex */
    public @interface Fec {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendSettings$Modulation */
    /* loaded from: classes2.dex */
    public @interface Modulation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendSettings$TimeInterleaveMode */
    /* loaded from: classes2.dex */
    public @interface TimeInterleaveMode {
    }

    private Atsc3FrontendSettings(long frequency, int bandwidth, int demodOutputFormat, Atsc3PlpSettings[] plpSettings) {
        super(frequency);
        this.mBandwidth = bandwidth;
        this.mDemodOutputFormat = demodOutputFormat;
        this.mPlpSettings = plpSettings;
    }

    public int getBandwidth() {
        return this.mBandwidth;
    }

    public int getDemodOutputFormat() {
        return this.mDemodOutputFormat;
    }

    public Atsc3PlpSettings[] getPlpSettings() {
        return this.mPlpSettings;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.frontend.Atsc3FrontendSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mBandwidth;
        private int mDemodOutputFormat;
        private long mFrequency;
        private Atsc3PlpSettings[] mPlpSettings;

        private Builder() {
            this.mFrequency = 0L;
            this.mBandwidth = 0;
            this.mDemodOutputFormat = 0;
            this.mPlpSettings = new Atsc3PlpSettings[0];
        }

        @Deprecated
        public Builder setFrequency(int frequency) {
            return setFrequencyLong(frequency);
        }

        public Builder setFrequencyLong(long frequency) {
            this.mFrequency = frequency;
            return this;
        }

        public Builder setBandwidth(int bandwidth) {
            this.mBandwidth = bandwidth;
            return this;
        }

        public Builder setDemodOutputFormat(int demodOutputFormat) {
            this.mDemodOutputFormat = demodOutputFormat;
            return this;
        }

        public Builder setPlpSettings(Atsc3PlpSettings[] plpSettings) {
            this.mPlpSettings = plpSettings;
            return this;
        }

        public Atsc3FrontendSettings build() {
            return new Atsc3FrontendSettings(this.mFrequency, this.mBandwidth, this.mDemodOutputFormat, this.mPlpSettings);
        }
    }

    @Override // android.media.p007tv.tuner.frontend.FrontendSettings
    public int getType() {
        return 3;
    }
}
