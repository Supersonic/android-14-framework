package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerVersionChecker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings */
/* loaded from: classes2.dex */
public class DvbtFrontendSettings extends FrontendSettings {
    public static final int BANDWIDTH_10MHZ = 64;
    public static final int BANDWIDTH_1_7MHZ = 32;
    public static final int BANDWIDTH_5MHZ = 16;
    public static final int BANDWIDTH_6MHZ = 8;
    public static final int BANDWIDTH_7MHZ = 4;
    public static final int BANDWIDTH_8MHZ = 2;
    public static final int BANDWIDTH_AUTO = 1;
    public static final int BANDWIDTH_UNDEFINED = 0;
    public static final int CODERATE_1_2 = 2;
    public static final int CODERATE_2_3 = 4;
    public static final int CODERATE_3_4 = 8;
    public static final int CODERATE_3_5 = 64;
    public static final int CODERATE_4_5 = 128;
    public static final int CODERATE_5_6 = 16;
    public static final int CODERATE_6_7 = 256;
    public static final int CODERATE_7_8 = 32;
    public static final int CODERATE_8_9 = 512;
    public static final int CODERATE_AUTO = 1;
    public static final int CODERATE_UNDEFINED = 0;
    public static final int CONSTELLATION_16QAM = 4;
    public static final int CONSTELLATION_16QAM_R = 64;
    public static final int CONSTELLATION_256QAM = 16;
    public static final int CONSTELLATION_256QAM_R = 256;
    public static final int CONSTELLATION_64QAM = 8;
    public static final int CONSTELLATION_64QAM_R = 128;
    public static final int CONSTELLATION_AUTO = 1;
    public static final int CONSTELLATION_QPSK = 2;
    public static final int CONSTELLATION_QPSK_R = 32;
    public static final int CONSTELLATION_UNDEFINED = 0;
    public static final int GUARD_INTERVAL_19_128 = 64;
    public static final int GUARD_INTERVAL_19_256 = 128;
    public static final int GUARD_INTERVAL_1_128 = 32;
    public static final int GUARD_INTERVAL_1_16 = 4;
    public static final int GUARD_INTERVAL_1_32 = 2;
    public static final int GUARD_INTERVAL_1_4 = 16;
    public static final int GUARD_INTERVAL_1_8 = 8;
    public static final int GUARD_INTERVAL_AUTO = 1;
    public static final int GUARD_INTERVAL_UNDEFINED = 0;
    public static final int HIERARCHY_1_INDEPTH = 64;
    public static final int HIERARCHY_1_NATIVE = 4;
    public static final int HIERARCHY_2_INDEPTH = 128;
    public static final int HIERARCHY_2_NATIVE = 8;
    public static final int HIERARCHY_4_INDEPTH = 256;
    public static final int HIERARCHY_4_NATIVE = 16;
    public static final int HIERARCHY_AUTO = 1;
    public static final int HIERARCHY_NON_INDEPTH = 32;
    public static final int HIERARCHY_NON_NATIVE = 2;
    public static final int HIERARCHY_UNDEFINED = 0;
    public static final int PLP_MODE_AUTO = 1;
    public static final int PLP_MODE_MANUAL = 2;
    public static final int PLP_MODE_UNDEFINED = 0;
    public static final int STANDARD_AUTO = 1;
    public static final int STANDARD_T = 2;
    public static final int STANDARD_T2 = 4;
    public static final int TRANSMISSION_MODE_16K = 32;
    public static final int TRANSMISSION_MODE_1K = 16;
    public static final int TRANSMISSION_MODE_2K = 2;
    public static final int TRANSMISSION_MODE_32K = 64;
    public static final int TRANSMISSION_MODE_4K = 8;
    public static final int TRANSMISSION_MODE_8K = 4;
    public static final int TRANSMISSION_MODE_AUTO = 1;
    public static final int TRANSMISSION_MODE_EXTENDED_16K = 256;
    public static final int TRANSMISSION_MODE_EXTENDED_32K = 512;
    public static final int TRANSMISSION_MODE_EXTENDED_8K = 128;
    public static final int TRANSMISSION_MODE_UNDEFINED = 0;
    private final int mBandwidth;
    private final int mConstellation;
    private final int mGuardInterval;
    private final int mHierarchy;
    private final int mHpCodeRate;
    private final boolean mIsHighPriority;
    private final boolean mIsMiso;
    private final int mLpCodeRate;
    private final int mPlpGroupId;
    private final int mPlpId;
    private final int mPlpMode;
    private final int mStandard;
    private int mTransmissionMode;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$Bandwidth */
    /* loaded from: classes2.dex */
    public @interface Bandwidth {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$CodeRate */
    /* loaded from: classes2.dex */
    public @interface CodeRate {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$Constellation */
    /* loaded from: classes2.dex */
    public @interface Constellation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$GuardInterval */
    /* loaded from: classes2.dex */
    public @interface GuardInterval {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$Hierarchy */
    /* loaded from: classes2.dex */
    public @interface Hierarchy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$PlpMode */
    /* loaded from: classes2.dex */
    public @interface PlpMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$Standard */
    /* loaded from: classes2.dex */
    public @interface Standard {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$TransmissionMode */
    /* loaded from: classes2.dex */
    public @interface TransmissionMode {
    }

    private DvbtFrontendSettings(long frequency, int transmissionMode, int bandwidth, int constellation, int hierarchy, int hpCodeRate, int lpCodeRate, int guardInterval, boolean isHighPriority, int standard, boolean isMiso, int plpMode, int plpId, int plpGroupId) {
        super(frequency);
        this.mTransmissionMode = transmissionMode;
        this.mBandwidth = bandwidth;
        this.mConstellation = constellation;
        this.mHierarchy = hierarchy;
        this.mHpCodeRate = hpCodeRate;
        this.mLpCodeRate = lpCodeRate;
        this.mGuardInterval = guardInterval;
        this.mIsHighPriority = isHighPriority;
        this.mStandard = standard;
        this.mIsMiso = isMiso;
        this.mPlpMode = plpMode;
        this.mPlpId = plpId;
        this.mPlpGroupId = plpGroupId;
    }

    public int getTransmissionMode() {
        return this.mTransmissionMode;
    }

    public int getBandwidth() {
        return this.mBandwidth;
    }

    public int getConstellation() {
        return this.mConstellation;
    }

    public int getHierarchy() {
        return this.mHierarchy;
    }

    public int getHighPriorityCodeRate() {
        return this.mHpCodeRate;
    }

    public int getLowPriorityCodeRate() {
        return this.mLpCodeRate;
    }

    public int getGuardInterval() {
        return this.mGuardInterval;
    }

    public boolean isHighPriority() {
        return this.mIsHighPriority;
    }

    public int getStandard() {
        return this.mStandard;
    }

    public boolean isMiso() {
        return this.mIsMiso;
    }

    public int getPlpMode() {
        return this.mPlpMode;
    }

    public int getPlpId() {
        return this.mPlpId;
    }

    public int getPlpGroupId() {
        return this.mPlpGroupId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isExtendedTransmissionMode(int transmissionMode) {
        return transmissionMode == 128 || transmissionMode == 256 || transmissionMode == 512;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isExtendedConstellation(int constellation) {
        return constellation == 32 || constellation == 64 || constellation == 128 || constellation == 256;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.frontend.DvbtFrontendSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mBandwidth;
        private int mConstellation;
        private long mFrequency;
        private int mGuardInterval;
        private int mHierarchy;
        private int mHpCodeRate;
        private boolean mIsHighPriority;
        private boolean mIsMiso;
        private int mLpCodeRate;
        private int mPlpGroupId;
        private int mPlpId;
        private int mPlpMode;
        private int mStandard;
        private int mTransmissionMode;

        private Builder() {
            this.mFrequency = 0L;
            this.mTransmissionMode = 0;
            this.mBandwidth = 0;
            this.mConstellation = 0;
            this.mHierarchy = 0;
            this.mHpCodeRate = 0;
            this.mLpCodeRate = 0;
            this.mGuardInterval = 0;
            this.mIsHighPriority = false;
            this.mStandard = 1;
            this.mIsMiso = false;
            this.mPlpMode = 0;
            this.mPlpId = 0;
            this.mPlpGroupId = 0;
        }

        @Deprecated
        public Builder setFrequency(int frequency) {
            return setFrequencyLong(frequency);
        }

        public Builder setFrequencyLong(long frequency) {
            this.mFrequency = frequency;
            return this;
        }

        public Builder setTransmissionMode(int transmissionMode) {
            if (!DvbtFrontendSettings.isExtendedTransmissionMode(transmissionMode) || TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "set TransmissionMode Ext")) {
                this.mTransmissionMode = transmissionMode;
            }
            return this;
        }

        public Builder setBandwidth(int bandwidth) {
            this.mBandwidth = bandwidth;
            return this;
        }

        public Builder setConstellation(int constellation) {
            if (!DvbtFrontendSettings.isExtendedConstellation(constellation) || TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "set Constellation Ext")) {
                this.mConstellation = constellation;
            }
            return this;
        }

        public Builder setHierarchy(int hierarchy) {
            this.mHierarchy = hierarchy;
            return this;
        }

        public Builder setHighPriorityCodeRate(int hpCodeRate) {
            this.mHpCodeRate = hpCodeRate;
            return this;
        }

        public Builder setLowPriorityCodeRate(int lpCodeRate) {
            this.mLpCodeRate = lpCodeRate;
            return this;
        }

        public Builder setGuardInterval(int guardInterval) {
            this.mGuardInterval = guardInterval;
            return this;
        }

        public Builder setHighPriority(boolean isHighPriority) {
            this.mIsHighPriority = isHighPriority;
            return this;
        }

        public Builder setStandard(int standard) {
            this.mStandard = standard;
            return this;
        }

        public Builder setMiso(boolean isMiso) {
            this.mIsMiso = isMiso;
            return this;
        }

        public Builder setPlpMode(int plpMode) {
            this.mPlpMode = plpMode;
            return this;
        }

        public Builder setPlpId(int plpId) {
            this.mPlpId = plpId;
            return this;
        }

        public Builder setPlpGroupId(int plpGroupId) {
            this.mPlpGroupId = plpGroupId;
            return this;
        }

        public DvbtFrontendSettings build() {
            return new DvbtFrontendSettings(this.mFrequency, this.mTransmissionMode, this.mBandwidth, this.mConstellation, this.mHierarchy, this.mHpCodeRate, this.mLpCodeRate, this.mGuardInterval, this.mIsHighPriority, this.mStandard, this.mIsMiso, this.mPlpMode, this.mPlpId, this.mPlpGroupId);
        }
    }

    @Override // android.media.p007tv.tuner.frontend.FrontendSettings
    public int getType() {
        return 6;
    }
}
