package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerVersionChecker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.DvbcFrontendSettings */
/* loaded from: classes2.dex */
public class DvbcFrontendSettings extends FrontendSettings {
    public static final int ANNEX_A = 1;
    public static final int ANNEX_B = 2;
    public static final int ANNEX_C = 4;
    public static final int ANNEX_UNDEFINED = 0;
    public static final int BANDWIDTH_5MHZ = 1;
    public static final int BANDWIDTH_6MHZ = 2;
    public static final int BANDWIDTH_7MHZ = 4;
    public static final int BANDWIDTH_8MHZ = 8;
    public static final int BANDWIDTH_UNDEFINED = 0;
    public static final int MODULATION_AUTO = 1;
    public static final int MODULATION_MOD_128QAM = 16;
    public static final int MODULATION_MOD_16QAM = 2;
    public static final int MODULATION_MOD_256QAM = 32;
    public static final int MODULATION_MOD_32QAM = 4;
    public static final int MODULATION_MOD_64QAM = 8;
    public static final int MODULATION_UNDEFINED = 0;
    public static final int OUTER_FEC_OUTER_FEC_NONE = 1;
    public static final int OUTER_FEC_OUTER_FEC_RS = 2;
    public static final int OUTER_FEC_UNDEFINED = 0;
    @Deprecated
    public static final int SPECTRAL_INVERSION_INVERTED = 2;
    @Deprecated
    public static final int SPECTRAL_INVERSION_NORMAL = 1;
    @Deprecated
    public static final int SPECTRAL_INVERSION_UNDEFINED = 0;
    public static final int TIME_INTERLEAVE_MODE_128_1_0 = 2;
    public static final int TIME_INTERLEAVE_MODE_128_1_1 = 4;
    public static final int TIME_INTERLEAVE_MODE_128_2 = 128;
    public static final int TIME_INTERLEAVE_MODE_128_3 = 256;
    public static final int TIME_INTERLEAVE_MODE_128_4 = 512;
    public static final int TIME_INTERLEAVE_MODE_16_8 = 32;
    public static final int TIME_INTERLEAVE_MODE_32_4 = 16;
    public static final int TIME_INTERLEAVE_MODE_64_2 = 8;
    public static final int TIME_INTERLEAVE_MODE_8_16 = 64;
    public static final int TIME_INTERLEAVE_MODE_AUTO = 1;
    public static final int TIME_INTERLEAVE_MODE_UNDEFINED = 0;
    private final int mAnnex;
    private final int mBandwidth;
    private final long mInnerFec;
    private final int mInterleaveMode;
    private final int mModulation;
    private final int mOuterFec;
    private final int mSpectralInversion;
    private final int mSymbolRate;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbcFrontendSettings$Annex */
    /* loaded from: classes2.dex */
    public @interface Annex {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbcFrontendSettings$Bandwidth */
    /* loaded from: classes2.dex */
    public @interface Bandwidth {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbcFrontendSettings$Modulation */
    /* loaded from: classes2.dex */
    public @interface Modulation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbcFrontendSettings$OuterFec */
    /* loaded from: classes2.dex */
    public @interface OuterFec {
    }

    @Retention(RetentionPolicy.SOURCE)
    @Deprecated
    /* renamed from: android.media.tv.tuner.frontend.DvbcFrontendSettings$SpectralInversion */
    /* loaded from: classes2.dex */
    public @interface SpectralInversion {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbcFrontendSettings$TimeInterleaveMode */
    /* loaded from: classes2.dex */
    public @interface TimeInterleaveMode {
    }

    private DvbcFrontendSettings(long frequency, int modulation, long innerFec, int symbolRate, int outerFec, int annex, int spectralInversion, int interleaveMode, int bandwidth) {
        super(frequency);
        this.mModulation = modulation;
        this.mInnerFec = innerFec;
        this.mSymbolRate = symbolRate;
        this.mOuterFec = outerFec;
        this.mAnnex = annex;
        this.mSpectralInversion = spectralInversion;
        this.mInterleaveMode = interleaveMode;
        this.mBandwidth = bandwidth;
    }

    public int getModulation() {
        return this.mModulation;
    }

    public long getInnerFec() {
        return this.mInnerFec;
    }

    public int getSymbolRate() {
        return this.mSymbolRate;
    }

    public int getOuterFec() {
        return this.mOuterFec;
    }

    public int getAnnex() {
        return this.mAnnex;
    }

    public int getSpectralInversion() {
        return this.mSpectralInversion;
    }

    public int getTimeInterleaveMode() {
        return this.mInterleaveMode;
    }

    public int getBandwidth() {
        return this.mBandwidth;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.frontend.DvbcFrontendSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mAnnex;
        private int mBandwidth;
        private long mFrequency;
        private long mInnerFec;
        private int mInterleaveMode;
        private int mModulation;
        private int mOuterFec;
        private int mSpectralInversion;
        private int mSymbolRate;

        private Builder() {
            this.mFrequency = 0L;
            this.mModulation = 0;
            this.mInnerFec = 0L;
            this.mSymbolRate = 0;
            this.mOuterFec = 0;
            this.mAnnex = 0;
            this.mSpectralInversion = 0;
            this.mInterleaveMode = 0;
            this.mBandwidth = 0;
        }

        @Deprecated
        public Builder setFrequency(int frequency) {
            return setFrequencyLong(frequency);
        }

        public Builder setFrequencyLong(long frequency) {
            this.mFrequency = frequency;
            return this;
        }

        public Builder setModulation(int modulation) {
            this.mModulation = modulation;
            return this;
        }

        public Builder setInnerFec(long fec) {
            this.mInnerFec = fec;
            return this;
        }

        public Builder setSymbolRate(int symbolRate) {
            this.mSymbolRate = symbolRate;
            return this;
        }

        public Builder setOuterFec(int outerFec) {
            this.mOuterFec = outerFec;
            return this;
        }

        public Builder setAnnex(int annex) {
            this.mAnnex = annex;
            return this;
        }

        public Builder setSpectralInversion(int spectralInversion) {
            this.mSpectralInversion = spectralInversion;
            return this;
        }

        public Builder setTimeInterleaveMode(int interleaveMode) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "setTimeInterleaveMode")) {
                this.mInterleaveMode = interleaveMode;
            }
            return this;
        }

        public Builder setBandwidth(int bandwidth) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "setBandwidth")) {
                this.mBandwidth = bandwidth;
            }
            return this;
        }

        public DvbcFrontendSettings build() {
            return new DvbcFrontendSettings(this.mFrequency, this.mModulation, this.mInnerFec, this.mSymbolRate, this.mOuterFec, this.mAnnex, this.mSpectralInversion, this.mInterleaveMode, this.mBandwidth);
        }
    }

    @Override // android.media.p007tv.tuner.frontend.FrontendSettings
    public int getType() {
        return 4;
    }
}
