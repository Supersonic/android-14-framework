package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerVersionChecker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.AnalogFrontendSettings */
/* loaded from: classes2.dex */
public class AnalogFrontendSettings extends FrontendSettings {
    public static final int AFT_FLAG_FALSE = 2;
    public static final int AFT_FLAG_TRUE = 1;
    public static final int AFT_FLAG_UNDEFINED = 0;
    public static final int SIF_AUTO = 1;
    public static final int SIF_BG = 2;
    public static final int SIF_BG_A2 = 4;
    public static final int SIF_BG_NICAM = 8;
    public static final int SIF_DK = 32;
    public static final int SIF_DK1_A2 = 64;
    public static final int SIF_DK2_A2 = 128;
    public static final int SIF_DK3_A2 = 256;
    public static final int SIF_DK_NICAM = 512;
    public static final int SIF_I = 16;
    public static final int SIF_I_NICAM = 32768;
    public static final int SIF_L = 1024;
    public static final int SIF_L_NICAM = 65536;
    public static final int SIF_L_PRIME = 131072;
    public static final int SIF_M = 2048;
    public static final int SIF_M_A2 = 8192;
    public static final int SIF_M_BTSC = 4096;
    public static final int SIF_M_EIAJ = 16384;
    public static final int SIF_UNDEFINED = 0;
    public static final int SIGNAL_TYPE_AUTO = 1;
    public static final int SIGNAL_TYPE_NTSC = 32;
    public static final int SIGNAL_TYPE_NTSC_443 = 64;
    public static final int SIGNAL_TYPE_PAL = 2;
    public static final int SIGNAL_TYPE_PAL_60 = 16;
    public static final int SIGNAL_TYPE_PAL_M = 4;
    public static final int SIGNAL_TYPE_PAL_N = 8;
    public static final int SIGNAL_TYPE_SECAM = 128;
    public static final int SIGNAL_TYPE_UNDEFINED = 0;
    private final int mAftFlag;
    private final int mSifStandard;
    private final int mSignalType;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.AnalogFrontendSettings$AftFlag */
    /* loaded from: classes2.dex */
    public @interface AftFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.AnalogFrontendSettings$SifStandard */
    /* loaded from: classes2.dex */
    public @interface SifStandard {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.AnalogFrontendSettings$SignalType */
    /* loaded from: classes2.dex */
    public @interface SignalType {
    }

    @Override // android.media.p007tv.tuner.frontend.FrontendSettings
    public int getType() {
        return 1;
    }

    public int getSignalType() {
        return this.mSignalType;
    }

    public int getSifStandard() {
        return this.mSifStandard;
    }

    public int getAftFlag() {
        return this.mAftFlag;
    }

    public static Builder builder() {
        return new Builder();
    }

    private AnalogFrontendSettings(long frequency, int signalType, int sifStandard, int aftFlag) {
        super(frequency);
        this.mSignalType = signalType;
        this.mSifStandard = sifStandard;
        this.mAftFlag = aftFlag;
    }

    /* renamed from: android.media.tv.tuner.frontend.AnalogFrontendSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mAftFlag;
        private long mFrequency;
        private int mSifStandard;
        private int mSignalType;

        private Builder() {
            this.mFrequency = 0L;
            this.mSignalType = 0;
            this.mSifStandard = 0;
            this.mAftFlag = 0;
        }

        @Deprecated
        public Builder setFrequency(int frequency) {
            return setFrequencyLong(frequency);
        }

        public Builder setFrequencyLong(long frequency) {
            this.mFrequency = frequency;
            return this;
        }

        public Builder setAftFlag(int aftFlag) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "setAftFlag")) {
                this.mAftFlag = aftFlag;
            }
            return this;
        }

        public Builder setSignalType(int signalType) {
            this.mSignalType = signalType;
            return this;
        }

        public Builder setSifStandard(int sifStandard) {
            this.mSifStandard = sifStandard;
            return this;
        }

        public AnalogFrontendSettings build() {
            return new AnalogFrontendSettings(this.mFrequency, this.mSignalType, this.mSifStandard, this.mAftFlag);
        }
    }
}
