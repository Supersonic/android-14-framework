package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerVersionChecker;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings */
/* loaded from: classes2.dex */
public class IsdbtFrontendSettings extends FrontendSettings {
    public static final int BANDWIDTH_6MHZ = 8;
    public static final int BANDWIDTH_7MHZ = 4;
    public static final int BANDWIDTH_8MHZ = 2;
    public static final int BANDWIDTH_AUTO = 1;
    public static final int BANDWIDTH_UNDEFINED = 0;
    public static final int MODE_1 = 2;
    public static final int MODE_2 = 4;
    public static final int MODE_3 = 8;
    public static final int MODE_AUTO = 1;
    public static final int MODE_UNDEFINED = 0;
    public static final int MODULATION_AUTO = 1;
    public static final int MODULATION_MOD_16QAM = 8;
    public static final int MODULATION_MOD_64QAM = 16;
    public static final int MODULATION_MOD_DQPSK = 2;
    public static final int MODULATION_MOD_QPSK = 4;
    public static final int MODULATION_UNDEFINED = 0;
    public static final int PARTIAL_RECEPTION_FLAG_FALSE = 2;
    public static final int PARTIAL_RECEPTION_FLAG_TRUE = 4;
    public static final int PARTIAL_RECEPTION_FLAG_UNDEFINED = 0;
    private static final String TAG = "IsdbtFrontendSettings";
    public static final int TIME_INTERLEAVE_MODE_1_0 = 2;
    public static final int TIME_INTERLEAVE_MODE_1_16 = 16;
    public static final int TIME_INTERLEAVE_MODE_1_4 = 4;
    public static final int TIME_INTERLEAVE_MODE_1_8 = 8;
    public static final int TIME_INTERLEAVE_MODE_2_0 = 32;
    public static final int TIME_INTERLEAVE_MODE_2_2 = 64;
    public static final int TIME_INTERLEAVE_MODE_2_4 = 128;
    public static final int TIME_INTERLEAVE_MODE_2_8 = 256;
    public static final int TIME_INTERLEAVE_MODE_3_0 = 512;
    public static final int TIME_INTERLEAVE_MODE_3_1 = 1024;
    public static final int TIME_INTERLEAVE_MODE_3_2 = 2048;
    public static final int TIME_INTERLEAVE_MODE_3_4 = 4096;
    public static final int TIME_INTERLEAVE_MODE_AUTO = 1;
    public static final int TIME_INTERLEAVE_MODE_UNDEFINED = 0;
    private final int mBandwidth;
    private final int mGuardInterval;
    private final IsdbtLayerSettings[] mLayerSettings;
    private final int mMode;
    private final int mPartialReceptionFlag;
    private final int mServiceAreaId;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings$Bandwidth */
    /* loaded from: classes2.dex */
    public @interface Bandwidth {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings$Mode */
    /* loaded from: classes2.dex */
    public @interface Mode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings$Modulation */
    /* loaded from: classes2.dex */
    public @interface Modulation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings$PartialReceptionFlag */
    /* loaded from: classes2.dex */
    public @interface PartialReceptionFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings$TimeInterleaveMode */
    /* loaded from: classes2.dex */
    public @interface TimeInterleaveMode {
    }

    private IsdbtFrontendSettings(long frequency, int bandwidth, int mode, int guardInterval, int serviceAreaId, IsdbtLayerSettings[] layerSettings, int partialReceptionFlag) {
        super(frequency);
        this.mBandwidth = bandwidth;
        this.mMode = mode;
        this.mGuardInterval = guardInterval;
        this.mServiceAreaId = serviceAreaId;
        this.mLayerSettings = new IsdbtLayerSettings[layerSettings.length];
        for (int i = 0; i < layerSettings.length; i++) {
            this.mLayerSettings[i] = layerSettings[i];
        }
        this.mPartialReceptionFlag = partialReceptionFlag;
    }

    @Deprecated
    public int getModulation() {
        if (TunerVersionChecker.isHigherOrEqualVersionTo(131072)) {
            return 0;
        }
        IsdbtLayerSettings[] isdbtLayerSettingsArr = this.mLayerSettings;
        if (isdbtLayerSettingsArr.length > 0) {
            return isdbtLayerSettingsArr[0].getModulation();
        }
        return 0;
    }

    public int getBandwidth() {
        return this.mBandwidth;
    }

    public int getMode() {
        return this.mMode;
    }

    @Deprecated
    public int getCodeRate() {
        if (TunerVersionChecker.isHigherOrEqualVersionTo(131072)) {
            return 0;
        }
        IsdbtLayerSettings[] isdbtLayerSettingsArr = this.mLayerSettings;
        if (isdbtLayerSettingsArr.length > 0) {
            return isdbtLayerSettingsArr[0].getCodeRate();
        }
        return 0;
    }

    public int getGuardInterval() {
        return this.mGuardInterval;
    }

    public int getServiceAreaId() {
        return this.mServiceAreaId;
    }

    public IsdbtLayerSettings[] getLayerSettings() {
        return this.mLayerSettings;
    }

    public int getPartialReceptionFlag() {
        return this.mPartialReceptionFlag;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mBandwidth;
        private long mFrequency;
        private int mGuardInterval;
        private IsdbtLayerSettings[] mLayerSettings;
        private int mMode;
        private int mPartialReceptionFlag;
        private int mServiceAreaId;

        private Builder() {
            this.mFrequency = 0L;
            this.mBandwidth = 0;
            this.mMode = 0;
            this.mGuardInterval = 0;
            this.mServiceAreaId = 0;
            this.mLayerSettings = new IsdbtLayerSettings[0];
            this.mPartialReceptionFlag = 0;
        }

        @Deprecated
        public Builder setFrequency(int frequency) {
            return setFrequencyLong(frequency);
        }

        public Builder setFrequencyLong(long frequency) {
            this.mFrequency = frequency;
            return this;
        }

        @Deprecated
        public Builder setModulation(int modulation) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "setModulation")) {
                Log.m112d(IsdbtFrontendSettings.TAG, "Use IsdbtLayerSettings on HAL 2.0 or higher");
            } else {
                IsdbtLayerSettings.Builder layerBuilder = IsdbtLayerSettings.builder();
                layerBuilder.setModulation(modulation);
                IsdbtLayerSettings[] isdbtLayerSettingsArr = this.mLayerSettings;
                if (isdbtLayerSettingsArr.length == 0) {
                    this.mLayerSettings = new IsdbtLayerSettings[1];
                } else {
                    layerBuilder.setCodeRate(isdbtLayerSettingsArr[0].getCodeRate());
                }
                this.mLayerSettings[0] = layerBuilder.build();
            }
            return this;
        }

        public Builder setBandwidth(int bandwidth) {
            this.mBandwidth = bandwidth;
            return this;
        }

        public Builder setMode(int mode) {
            this.mMode = mode;
            return this;
        }

        @Deprecated
        public Builder setCodeRate(int codeRate) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "setModulation")) {
                Log.m112d(IsdbtFrontendSettings.TAG, "Use IsdbtLayerSettings on HAL 2.0 or higher");
            } else {
                IsdbtLayerSettings.Builder layerBuilder = IsdbtLayerSettings.builder();
                layerBuilder.setCodeRate(codeRate);
                IsdbtLayerSettings[] isdbtLayerSettingsArr = this.mLayerSettings;
                if (isdbtLayerSettingsArr.length == 0) {
                    this.mLayerSettings = new IsdbtLayerSettings[1];
                } else {
                    layerBuilder.setModulation(isdbtLayerSettingsArr[0].getModulation());
                }
                this.mLayerSettings[0] = layerBuilder.build();
            }
            return this;
        }

        public Builder setGuardInterval(int guardInterval) {
            this.mGuardInterval = guardInterval;
            return this;
        }

        public Builder setServiceAreaId(int serviceAreaId) {
            this.mServiceAreaId = serviceAreaId;
            return this;
        }

        public Builder setLayerSettings(IsdbtLayerSettings[] layerSettings) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "setLayerSettings")) {
                this.mLayerSettings = new IsdbtLayerSettings[layerSettings.length];
                for (int i = 0; i < layerSettings.length; i++) {
                    this.mLayerSettings[i] = layerSettings[i];
                }
            }
            return this;
        }

        public Builder setPartialReceptionFlag(int flag) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "setPartialReceptionFlag")) {
                this.mPartialReceptionFlag = flag;
            }
            return this;
        }

        public IsdbtFrontendSettings build() {
            return new IsdbtFrontendSettings(this.mFrequency, this.mBandwidth, this.mMode, this.mGuardInterval, this.mServiceAreaId, this.mLayerSettings, this.mPartialReceptionFlag);
        }
    }

    @Override // android.media.p007tv.tuner.frontend.FrontendSettings
    public int getType() {
        return 9;
    }

    /* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings$IsdbtLayerSettings */
    /* loaded from: classes2.dex */
    public static final class IsdbtLayerSettings {
        private final int mCodeRate;
        private final int mModulation;
        private final int mNumOfSegments;
        private final int mTimeInterleaveMode;

        private IsdbtLayerSettings(int modulation, int timeInterleaveMode, int codeRate, int numOfSegments) {
            this.mModulation = modulation;
            this.mTimeInterleaveMode = timeInterleaveMode;
            this.mCodeRate = codeRate;
            this.mNumOfSegments = numOfSegments;
        }

        public int getModulation() {
            return this.mModulation;
        }

        public int getTimeInterleaveMode() {
            return this.mTimeInterleaveMode;
        }

        public int getCodeRate() {
            return this.mCodeRate;
        }

        public int getNumberOfSegments() {
            return this.mNumOfSegments;
        }

        public static Builder builder() {
            return new Builder();
        }

        /* renamed from: android.media.tv.tuner.frontend.IsdbtFrontendSettings$IsdbtLayerSettings$Builder */
        /* loaded from: classes2.dex */
        public static final class Builder {
            private int mCodeRate;
            private int mModulation;
            private int mNumOfSegments;
            private int mTimeInterleaveMode;

            private Builder() {
                this.mModulation = 0;
                this.mTimeInterleaveMode = 0;
                this.mCodeRate = 0;
                this.mNumOfSegments = 0;
            }

            public Builder setModulation(int modulation) {
                this.mModulation = modulation;
                return this;
            }

            public Builder setTimeInterleaveMode(int mode) {
                this.mTimeInterleaveMode = mode;
                return this;
            }

            public Builder setCodeRate(int codeRate) {
                this.mCodeRate = codeRate;
                return this;
            }

            public Builder setNumberOfSegments(int numOfSegments) {
                this.mNumOfSegments = numOfSegments;
                return this;
            }

            public IsdbtLayerSettings build() {
                return new IsdbtLayerSettings(this.mModulation, this.mTimeInterleaveMode, this.mCodeRate, this.mNumOfSegments);
            }
        }
    }
}
