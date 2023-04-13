package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerVersionChecker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.FrontendStatus */
/* loaded from: classes2.dex */
public class FrontendStatus {
    public static final int FRONTEND_STATUS_TYPE_AGC = 14;
    public static final int FRONTEND_STATUS_TYPE_ATSC3_ALL_PLP_INFO = 41;
    public static final int FRONTEND_STATUS_TYPE_ATSC3_PLP_INFO = 21;
    public static final int FRONTEND_STATUS_TYPE_BANDWIDTH = 25;
    public static final int FRONTEND_STATUS_TYPE_BER = 2;
    public static final int FRONTEND_STATUS_TYPE_BERS = 23;
    public static final int FRONTEND_STATUS_TYPE_CODERATES = 24;
    public static final int FRONTEND_STATUS_TYPE_DEMOD_LOCK = 0;
    public static final int FRONTEND_STATUS_TYPE_DVBT_CELL_IDS = 40;
    public static final int FRONTEND_STATUS_TYPE_EWBS = 13;
    public static final int FRONTEND_STATUS_TYPE_FEC = 8;
    public static final int FRONTEND_STATUS_TYPE_FREQ_OFFSET = 18;
    public static final int FRONTEND_STATUS_TYPE_GUARD_INTERVAL = 26;
    public static final int FRONTEND_STATUS_TYPE_HIERARCHY = 19;
    public static final int FRONTEND_STATUS_TYPE_INTERLEAVINGS = 30;
    public static final int FRONTEND_STATUS_TYPE_IPTV_AVERAGE_JITTER_MS = 46;
    public static final int FRONTEND_STATUS_TYPE_IPTV_CONTENT_URL = 42;
    public static final int FRONTEND_STATUS_TYPE_IPTV_PACKETS_LOST = 43;
    public static final int FRONTEND_STATUS_TYPE_IPTV_PACKETS_RECEIVED = 44;
    public static final int FRONTEND_STATUS_TYPE_IPTV_WORST_JITTER_MS = 45;
    public static final int FRONTEND_STATUS_TYPE_ISDBT_MODE = 37;
    public static final int FRONTEND_STATUS_TYPE_ISDBT_PARTIAL_RECEPTION_FLAG = 38;
    public static final int FRONTEND_STATUS_TYPE_ISDBT_SEGMENTS = 31;
    public static final int FRONTEND_STATUS_TYPE_IS_LINEAR = 35;
    public static final int FRONTEND_STATUS_TYPE_IS_MISO_ENABLED = 34;
    public static final int FRONTEND_STATUS_TYPE_IS_SHORT_FRAMES_ENABLED = 36;
    public static final int FRONTEND_STATUS_TYPE_LAYER_ERROR = 16;
    public static final int FRONTEND_STATUS_TYPE_LNA = 15;
    public static final int FRONTEND_STATUS_TYPE_LNB_VOLTAGE = 11;
    public static final int FRONTEND_STATUS_TYPE_MER = 17;
    public static final int FRONTEND_STATUS_TYPE_MODULATION = 9;
    public static final int FRONTEND_STATUS_TYPE_MODULATIONS_EXT = 22;
    public static final int FRONTEND_STATUS_TYPE_PER = 3;
    public static final int FRONTEND_STATUS_TYPE_PLP_ID = 12;
    public static final int FRONTEND_STATUS_TYPE_PRE_BER = 4;
    public static final int FRONTEND_STATUS_TYPE_RF_LOCK = 20;
    public static final int FRONTEND_STATUS_TYPE_ROLL_OFF = 33;
    public static final int FRONTEND_STATUS_TYPE_SIGNAL_QUALITY = 5;
    public static final int FRONTEND_STATUS_TYPE_SIGNAL_STRENGTH = 6;
    public static final int FRONTEND_STATUS_TYPE_SNR = 1;
    public static final int FRONTEND_STATUS_TYPE_SPECTRAL = 10;
    public static final int FRONTEND_STATUS_TYPE_STREAM_IDS = 39;
    public static final int FRONTEND_STATUS_TYPE_SYMBOL_RATE = 7;
    public static final int FRONTEND_STATUS_TYPE_T2_SYSTEM_ID = 29;
    public static final int FRONTEND_STATUS_TYPE_TRANSMISSION_MODE = 27;
    public static final int FRONTEND_STATUS_TYPE_TS_DATA_RATES = 32;
    public static final int FRONTEND_STATUS_TYPE_UEC = 28;
    private Integer mAgc;
    private Atsc3PlpInfo[] mAllPlpInfo;
    private Integer mBandwidth;
    private Integer mBer;
    private int[] mBers;
    private int[] mCodeRates;
    private int[] mDvbtCellIds;
    private Long mFreqOffset;
    private Integer mGuardInterval;
    private Integer mHierarchy;
    private Long mInnerFec;
    private int[] mInterleaving;
    private Integer mInversion;
    private Integer mIptvAverageJitterMs;
    private String mIptvContentUrl;
    private Long mIptvPacketsLost;
    private Long mIptvPacketsReceived;
    private Integer mIptvWorstJitterMs;
    private Boolean mIsDemodLocked;
    private Boolean mIsEwbs;
    private boolean[] mIsLayerErrors;
    private Boolean mIsLinear;
    private Boolean mIsLnaOn;
    private Boolean mIsMisoEnabled;
    private Boolean mIsRfLocked;
    private Boolean mIsShortFrames;
    private Integer mIsdbtMode;
    private Integer mIsdbtPartialReceptionFlag;
    private int[] mIsdbtSegment;
    private Integer mLnbVoltage;
    private Integer mMer;
    private Integer mModulation;
    private int[] mModulationsExt;
    private Integer mPer;
    private Integer mPerBer;
    private Integer mPlpId;
    private Atsc3PlpTuningInfo[] mPlpInfo;
    private Integer mRollOff;
    private Integer mSignalQuality;
    private Integer mSignalStrength;
    private Integer mSnr;
    private int[] mStreamIds;
    private Integer mSymbolRate;
    private Integer mSystemId;
    private Integer mTransmissionMode;
    private int[] mTsDataRate;
    private Integer mUec;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendStatus$FrontendBandwidth */
    /* loaded from: classes2.dex */
    public @interface FrontendBandwidth {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendStatus$FrontendGuardInterval */
    /* loaded from: classes2.dex */
    public @interface FrontendGuardInterval {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendStatus$FrontendInterleaveMode */
    /* loaded from: classes2.dex */
    public @interface FrontendInterleaveMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendStatus$FrontendModulation */
    /* loaded from: classes2.dex */
    public @interface FrontendModulation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendStatus$FrontendRollOff */
    /* loaded from: classes2.dex */
    public @interface FrontendRollOff {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendStatus$FrontendStatusType */
    /* loaded from: classes2.dex */
    public @interface FrontendStatusType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendStatus$FrontendTransmissionMode */
    /* loaded from: classes2.dex */
    public @interface FrontendTransmissionMode {
    }

    private FrontendStatus() {
    }

    public boolean isDemodLocked() {
        Boolean bool = this.mIsDemodLocked;
        if (bool == null) {
            throw new IllegalStateException("DemodLocked status is empty");
        }
        return bool.booleanValue();
    }

    public int getSnr() {
        Integer num = this.mSnr;
        if (num == null) {
            throw new IllegalStateException("Snr status is empty");
        }
        return num.intValue();
    }

    public int getBer() {
        Integer num = this.mBer;
        if (num == null) {
            throw new IllegalStateException("Ber status is empty");
        }
        return num.intValue();
    }

    public int getPer() {
        Integer num = this.mPer;
        if (num == null) {
            throw new IllegalStateException("Per status is empty");
        }
        return num.intValue();
    }

    public int getPerBer() {
        Integer num = this.mPerBer;
        if (num == null) {
            throw new IllegalStateException("PerBer status is empty");
        }
        return num.intValue();
    }

    public int getSignalQuality() {
        Integer num = this.mSignalQuality;
        if (num == null) {
            throw new IllegalStateException("SignalQuality status is empty");
        }
        return num.intValue();
    }

    public int getSignalStrength() {
        Integer num = this.mSignalStrength;
        if (num == null) {
            throw new IllegalStateException("SignalStrength status is empty");
        }
        return num.intValue();
    }

    public int getSymbolRate() {
        Integer num = this.mSymbolRate;
        if (num == null) {
            throw new IllegalStateException("SymbolRate status is empty");
        }
        return num.intValue();
    }

    public long getInnerFec() {
        Long l = this.mInnerFec;
        if (l == null) {
            throw new IllegalStateException("InnerFec status is empty");
        }
        return l.longValue();
    }

    public int getModulation() {
        Integer num = this.mModulation;
        if (num == null) {
            throw new IllegalStateException("Modulation status is empty");
        }
        return num.intValue();
    }

    public int getSpectralInversion() {
        Integer num = this.mInversion;
        if (num == null) {
            throw new IllegalStateException("SpectralInversion status is empty");
        }
        return num.intValue();
    }

    public int getLnbVoltage() {
        Integer num = this.mLnbVoltage;
        if (num == null) {
            throw new IllegalStateException("LnbVoltage status is empty");
        }
        return num.intValue();
    }

    public int getPlpId() {
        Integer num = this.mPlpId;
        if (num == null) {
            throw new IllegalStateException("PlpId status is empty");
        }
        return num.intValue();
    }

    public boolean isEwbs() {
        Boolean bool = this.mIsEwbs;
        if (bool == null) {
            throw new IllegalStateException("Ewbs status is empty");
        }
        return bool.booleanValue();
    }

    public int getAgc() {
        Integer num = this.mAgc;
        if (num == null) {
            throw new IllegalStateException("Agc status is empty");
        }
        return num.intValue();
    }

    public boolean isLnaOn() {
        Boolean bool = this.mIsLnaOn;
        if (bool == null) {
            throw new IllegalStateException("LnaOn status is empty");
        }
        return bool.booleanValue();
    }

    public boolean[] getLayerErrors() {
        boolean[] zArr = this.mIsLayerErrors;
        if (zArr == null) {
            throw new IllegalStateException("LayerErrors status is empty");
        }
        return zArr;
    }

    public int getMer() {
        Integer num = this.mMer;
        if (num == null) {
            throw new IllegalStateException("Mer status is empty");
        }
        return num.intValue();
    }

    @Deprecated
    public int getFreqOffset() {
        return (int) getFreqOffsetLong();
    }

    public long getFreqOffsetLong() {
        Long l = this.mFreqOffset;
        if (l == null) {
            throw new IllegalStateException("FreqOffset status is empty");
        }
        return l.longValue();
    }

    public int getHierarchy() {
        Integer num = this.mHierarchy;
        if (num == null) {
            throw new IllegalStateException("Hierarchy status is empty");
        }
        return num.intValue();
    }

    public boolean isRfLocked() {
        Boolean bool = this.mIsRfLocked;
        if (bool == null) {
            throw new IllegalStateException("isRfLocked status is empty");
        }
        return bool.booleanValue();
    }

    public Atsc3PlpTuningInfo[] getAtsc3PlpTuningInfo() {
        Atsc3PlpTuningInfo[] atsc3PlpTuningInfoArr = this.mPlpInfo;
        if (atsc3PlpTuningInfoArr == null) {
            throw new IllegalStateException("Atsc3PlpTuningInfo status is empty");
        }
        return atsc3PlpTuningInfoArr;
    }

    public int[] getBers() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getBers status");
        int[] iArr = this.mBers;
        if (iArr == null) {
            throw new IllegalStateException("Bers status is empty");
        }
        return iArr;
    }

    public int[] getCodeRates() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getCodeRates status");
        int[] iArr = this.mCodeRates;
        if (iArr == null) {
            throw new IllegalStateException("CodeRates status is empty");
        }
        return iArr;
    }

    public int getBandwidth() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getBandwidth status");
        Integer num = this.mBandwidth;
        if (num == null) {
            throw new IllegalStateException("Bandwidth status is empty");
        }
        return num.intValue();
    }

    public int getGuardInterval() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getGuardInterval status");
        Integer num = this.mGuardInterval;
        if (num == null) {
            throw new IllegalStateException("GuardInterval status is empty");
        }
        return num.intValue();
    }

    public int getTransmissionMode() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getTransmissionMode status");
        Integer num = this.mTransmissionMode;
        if (num == null) {
            throw new IllegalStateException("TransmissionMode status is empty");
        }
        return num.intValue();
    }

    public int getUec() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getUec status");
        Integer num = this.mUec;
        if (num == null) {
            throw new IllegalStateException("Uec status is empty");
        }
        return num.intValue();
    }

    public int getSystemId() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getSystemId status");
        Integer num = this.mSystemId;
        if (num == null) {
            throw new IllegalStateException("SystemId status is empty");
        }
        return num.intValue();
    }

    public int[] getInterleaving() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getInterleaving status");
        int[] iArr = this.mInterleaving;
        if (iArr == null) {
            throw new IllegalStateException("Interleaving status is empty");
        }
        return iArr;
    }

    public int[] getIsdbtSegment() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getIsdbtSegment status");
        int[] iArr = this.mIsdbtSegment;
        if (iArr == null) {
            throw new IllegalStateException("IsdbtSegment status is empty");
        }
        return iArr;
    }

    public int[] getTsDataRate() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getTsDataRate status");
        int[] iArr = this.mTsDataRate;
        if (iArr == null) {
            throw new IllegalStateException("TsDataRate status is empty");
        }
        return iArr;
    }

    public int[] getExtendedModulations() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getExtendedModulations status");
        int[] iArr = this.mModulationsExt;
        if (iArr == null) {
            throw new IllegalStateException("ExtendedModulations status is empty");
        }
        return iArr;
    }

    public int getRollOff() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "getRollOff status");
        Integer num = this.mRollOff;
        if (num == null) {
            throw new IllegalStateException("RollOff status is empty");
        }
        return num.intValue();
    }

    public boolean isMisoEnabled() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "isMisoEnabled status");
        Boolean bool = this.mIsMisoEnabled;
        if (bool == null) {
            throw new IllegalStateException("isMisoEnabled status is empty");
        }
        return bool.booleanValue();
    }

    public boolean isLinear() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "isLinear status");
        Boolean bool = this.mIsLinear;
        if (bool == null) {
            throw new IllegalStateException("isLinear status is empty");
        }
        return bool.booleanValue();
    }

    public boolean isShortFramesEnabled() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "isShortFramesEnabled status");
        Boolean bool = this.mIsShortFrames;
        if (bool == null) {
            throw new IllegalStateException("isShortFramesEnabled status is empty");
        }
        return bool.booleanValue();
    }

    public int getIsdbtMode() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "IsdbtMode status");
        Integer num = this.mIsdbtMode;
        if (num == null) {
            throw new IllegalStateException("IsdbtMode status is empty");
        }
        return num.intValue();
    }

    public int getIsdbtPartialReceptionFlag() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "IsdbtPartialReceptionFlag status");
        Integer num = this.mIsdbtPartialReceptionFlag;
        if (num == null) {
            throw new IllegalStateException("IsdbtPartialReceptionFlag status is empty");
        }
        return num.intValue();
    }

    public int[] getStreamIds() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "stream ids status");
        int[] iArr = this.mStreamIds;
        if (iArr == null) {
            throw new IllegalStateException("stream ids are empty");
        }
        return iArr;
    }

    public int[] getDvbtCellIds() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "dvbt cell ids status");
        int[] iArr = this.mDvbtCellIds;
        if (iArr == null) {
            throw new IllegalStateException("dvbt cell ids are empty");
        }
        return iArr;
    }

    public List<Atsc3PlpInfo> getAllAtsc3PlpInfo() {
        if (!TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "Atsc3PlpInfo all status")) {
            throw new UnsupportedOperationException("Atsc3PlpInfo all status is empty");
        }
        Atsc3PlpInfo[] atsc3PlpInfoArr = this.mAllPlpInfo;
        if (atsc3PlpInfoArr == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(atsc3PlpInfoArr);
    }

    /* renamed from: android.media.tv.tuner.frontend.FrontendStatus$Atsc3PlpTuningInfo */
    /* loaded from: classes2.dex */
    public static class Atsc3PlpTuningInfo {
        private final boolean mIsLocked;
        private final int mPlpId;
        private final int mUec;

        private Atsc3PlpTuningInfo(int plpId, boolean isLocked, int uec) {
            this.mPlpId = plpId;
            this.mIsLocked = isLocked;
            this.mUec = uec;
        }

        public int getPlpId() {
            return this.mPlpId;
        }

        public boolean isLocked() {
            return this.mIsLocked;
        }

        public int getUec() {
            return this.mUec;
        }
    }

    public String getIptvContentUrl() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(196608, "IptvContentUrl status");
        String str = this.mIptvContentUrl;
        if (str == null) {
            throw new IllegalStateException("IptvContentUrl status is empty");
        }
        return str;
    }

    public long getIptvPacketsLost() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(196608, "IptvPacketsLost status");
        Long l = this.mIptvPacketsLost;
        if (l == null) {
            throw new IllegalStateException("IptvPacketsLost status is empty");
        }
        return l.longValue();
    }

    public long getIptvPacketsReceived() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(196608, "IptvPacketsReceived status");
        Long l = this.mIptvPacketsReceived;
        if (l == null) {
            throw new IllegalStateException("IptvPacketsReceived status is empty");
        }
        return l.longValue();
    }

    public int getIptvWorstJitterMillis() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(196608, "IptvWorstJitterMs status");
        Integer num = this.mIptvWorstJitterMs;
        if (num == null) {
            throw new IllegalStateException("IptvWorstJitterMs status is empty");
        }
        return num.intValue();
    }

    public int getIptvAverageJitterMillis() {
        TunerVersionChecker.checkHigherOrEqualVersionTo(196608, "IptvAverageJitterMs status");
        Integer num = this.mIptvAverageJitterMs;
        if (num == null) {
            throw new IllegalStateException("IptvAverageJitterMs status is empty");
        }
        return num.intValue();
    }
}
