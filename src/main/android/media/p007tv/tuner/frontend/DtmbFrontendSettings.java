package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.DtmbFrontendSettings */
/* loaded from: classes2.dex */
public final class DtmbFrontendSettings extends FrontendSettings {
    public static final int BANDWIDTH_6MHZ = 4;
    public static final int BANDWIDTH_8MHZ = 2;
    public static final int BANDWIDTH_AUTO = 1;
    public static final int BANDWIDTH_UNDEFINED = 0;
    public static final int CODERATE_2_5 = 2;
    public static final int CODERATE_3_5 = 4;
    public static final int CODERATE_4_5 = 8;
    public static final int CODERATE_AUTO = 1;
    public static final int CODERATE_UNDEFINED = 0;
    public static final int GUARD_INTERVAL_AUTO = 1;
    public static final int GUARD_INTERVAL_PN_420_CONST = 16;
    public static final int GUARD_INTERVAL_PN_420_VARIOUS = 2;
    public static final int GUARD_INTERVAL_PN_595_CONST = 4;
    public static final int GUARD_INTERVAL_PN_945_CONST = 32;
    public static final int GUARD_INTERVAL_PN_945_VARIOUS = 8;
    public static final int GUARD_INTERVAL_PN_RESERVED = 64;
    public static final int GUARD_INTERVAL_UNDEFINED = 0;
    public static final int MODULATION_CONSTELLATION_16QAM = 8;
    public static final int MODULATION_CONSTELLATION_32QAM = 16;
    public static final int MODULATION_CONSTELLATION_4QAM = 2;
    public static final int MODULATION_CONSTELLATION_4QAM_NR = 4;
    public static final int MODULATION_CONSTELLATION_64QAM = 32;
    public static final int MODULATION_CONSTELLATION_AUTO = 1;
    public static final int MODULATION_CONSTELLATION_UNDEFINED = 0;
    public static final int TIME_INTERLEAVE_MODE_AUTO = 1;
    public static final int TIME_INTERLEAVE_MODE_TIMER_INT_240 = 2;
    public static final int TIME_INTERLEAVE_MODE_TIMER_INT_720 = 4;
    public static final int TIME_INTERLEAVE_MODE_UNDEFINED = 0;
    public static final int TRANSMISSION_MODE_AUTO = 1;
    public static final int TRANSMISSION_MODE_C1 = 2;
    public static final int TRANSMISSION_MODE_C3780 = 4;
    public static final int TRANSMISSION_MODE_UNDEFINED = 0;
    private final int mBandwidth;
    private final int mCodeRate;
    private final int mGuardInterval;
    private final int mModulation;
    private final int mTimeInterleaveMode;
    private final int mTransmissionMode;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DtmbFrontendSettings$Bandwidth */
    /* loaded from: classes2.dex */
    public @interface Bandwidth {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DtmbFrontendSettings$CodeRate */
    /* loaded from: classes2.dex */
    public @interface CodeRate {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DtmbFrontendSettings$GuardInterval */
    /* loaded from: classes2.dex */
    public @interface GuardInterval {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DtmbFrontendSettings$Modulation */
    /* loaded from: classes2.dex */
    public @interface Modulation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DtmbFrontendSettings$TimeInterleaveMode */
    /* loaded from: classes2.dex */
    public @interface TimeInterleaveMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DtmbFrontendSettings$TransmissionMode */
    /* loaded from: classes2.dex */
    public @interface TransmissionMode {
    }

    private DtmbFrontendSettings(long frequency, int modulation, int codeRate, int transmissionMode, int guardInterval, int timeInterleaveMode, int bandwidth) {
        super(frequency);
        this.mModulation = modulation;
        this.mCodeRate = codeRate;
        this.mTransmissionMode = transmissionMode;
        this.mGuardInterval = guardInterval;
        this.mTimeInterleaveMode = timeInterleaveMode;
        this.mBandwidth = bandwidth;
    }

    public int getModulation() {
        return this.mModulation;
    }

    public int getCodeRate() {
        return this.mCodeRate;
    }

    public int getTransmissionMode() {
        return this.mTransmissionMode;
    }

    public int getBandwidth() {
        return this.mBandwidth;
    }

    public int getTimeInterleaveMode() {
        return this.mTimeInterleaveMode;
    }

    public int getGuardInterval() {
        return this.mGuardInterval;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.frontend.DtmbFrontendSettings$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mBandwidth;
        private int mCodeRate;
        private long mFrequency;
        private int mGuardInterval;
        private int mModulation;
        private int mTimeInterleaveMode;
        private int mTransmissionMode;

        private Builder() {
            this.mFrequency = 0L;
            this.mModulation = 0;
            this.mCodeRate = 0;
            this.mTransmissionMode = 0;
            this.mBandwidth = 0;
            this.mTimeInterleaveMode = 0;
            this.mGuardInterval = 0;
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

        public Builder setCodeRate(int codeRate) {
            this.mCodeRate = codeRate;
            return this;
        }

        public Builder setBandwidth(int bandwidth) {
            this.mBandwidth = bandwidth;
            return this;
        }

        public Builder setTimeInterleaveMode(int timeInterleaveMode) {
            this.mTimeInterleaveMode = timeInterleaveMode;
            return this;
        }

        public Builder setGuardInterval(int guardInterval) {
            this.mGuardInterval = guardInterval;
            return this;
        }

        public Builder setTransmissionMode(int transmissionMode) {
            this.mTransmissionMode = transmissionMode;
            return this;
        }

        public DtmbFrontendSettings build() {
            return new DtmbFrontendSettings(this.mFrequency, this.mModulation, this.mCodeRate, this.mTransmissionMode, this.mGuardInterval, this.mTimeInterleaveMode, this.mBandwidth);
        }
    }

    @Override // android.media.p007tv.tuner.frontend.FrontendSettings
    public int getType() {
        return 10;
    }
}
