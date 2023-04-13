package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.RecordSettings */
/* loaded from: classes2.dex */
public class RecordSettings extends Settings {
    public static final int INDEX_TYPE_NONE = 0;
    public static final int INDEX_TYPE_SC = 1;
    public static final int INDEX_TYPE_SC_AVC = 3;
    public static final int INDEX_TYPE_SC_HEVC = 2;
    public static final int INDEX_TYPE_SC_VVC = 4;
    public static final int MPT_INDEX_AUDIO = 262144;
    public static final int MPT_INDEX_MPT = 65536;
    public static final int MPT_INDEX_TIMESTAMP_TARGET_AUDIO = 1048576;
    public static final int MPT_INDEX_TIMESTAMP_TARGET_VIDEO = 524288;
    public static final int MPT_INDEX_VIDEO = 131072;
    public static final int SC_HEVC_INDEX_AUD = 2;
    public static final int SC_HEVC_INDEX_SLICE_BLA_N_LP = 16;
    public static final int SC_HEVC_INDEX_SLICE_BLA_W_RADL = 8;
    public static final int SC_HEVC_INDEX_SLICE_CE_BLA_W_LP = 4;
    public static final int SC_HEVC_INDEX_SLICE_IDR_N_LP = 64;
    public static final int SC_HEVC_INDEX_SLICE_IDR_W_RADL = 32;
    public static final int SC_HEVC_INDEX_SLICE_TRAIL_CRA = 128;
    public static final int SC_HEVC_INDEX_SPS = 1;
    public static final int SC_INDEX_B_FRAME = 4;
    public static final int SC_INDEX_B_SLICE = 64;
    public static final int SC_INDEX_I_FRAME = 1;
    public static final int SC_INDEX_I_SLICE = 16;
    public static final int SC_INDEX_P_FRAME = 2;
    public static final int SC_INDEX_P_SLICE = 32;
    public static final int SC_INDEX_SEQUENCE = 8;
    public static final int SC_INDEX_SI_SLICE = 128;
    public static final int SC_INDEX_SP_SLICE = 256;
    public static final int SC_VVC_INDEX_AUD = 64;
    public static final int SC_VVC_INDEX_SLICE_CRA = 4;
    public static final int SC_VVC_INDEX_SLICE_GDR = 8;
    public static final int SC_VVC_INDEX_SLICE_IDR_N_LP = 2;
    public static final int SC_VVC_INDEX_SLICE_IDR_W_RADL = 1;
    public static final int SC_VVC_INDEX_SPS = 32;
    public static final int SC_VVC_INDEX_VPS = 16;
    public static final int TS_INDEX_ADAPTATION_EXTENSION_FLAG = 4096;
    public static final int TS_INDEX_CHANGE_TO_EVEN_SCRAMBLED = 8;
    public static final int TS_INDEX_CHANGE_TO_NOT_SCRAMBLED = 4;
    public static final int TS_INDEX_CHANGE_TO_ODD_SCRAMBLED = 16;
    public static final int TS_INDEX_DISCONTINUITY_INDICATOR = 32;
    public static final int TS_INDEX_FIRST_PACKET = 1;
    public static final int TS_INDEX_INVALID = 0;
    public static final int TS_INDEX_OPCR_FLAG = 512;
    public static final int TS_INDEX_PAYLOAD_UNIT_START_INDICATOR = 2;
    public static final int TS_INDEX_PCR_FLAG = 256;
    public static final int TS_INDEX_PRIORITY_INDICATOR = 128;
    public static final int TS_INDEX_PRIVATE_DATA = 2048;
    public static final int TS_INDEX_RANDOM_ACCESS_INDICATOR = 64;
    public static final int TS_INDEX_SPLICING_POINT_FLAG = 1024;
    private final int mScIndexMask;
    private final int mScIndexType;
    private final int mTsIndexMask;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.RecordSettings$ScHevcIndex */
    /* loaded from: classes2.dex */
    public @interface ScHevcIndex {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.RecordSettings$ScIndex */
    /* loaded from: classes2.dex */
    public @interface ScIndex {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.RecordSettings$ScIndexMask */
    /* loaded from: classes2.dex */
    public @interface ScIndexMask {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.RecordSettings$ScIndexType */
    /* loaded from: classes2.dex */
    public @interface ScIndexType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.RecordSettings$ScVvcIndex */
    /* loaded from: classes2.dex */
    public @interface ScVvcIndex {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.filter.RecordSettings$TsIndexMask */
    /* loaded from: classes2.dex */
    public @interface TsIndexMask {
    }

    private RecordSettings(int mainType, int tsIndexType, int scIndexType, int scIndexMask) {
        super(TunerUtils.getFilterSubtype(mainType, 6));
        this.mTsIndexMask = tsIndexType;
        this.mScIndexType = scIndexType;
        this.mScIndexMask = scIndexMask;
    }

    public int getTsIndexMask() {
        return this.mTsIndexMask;
    }

    public int getScIndexType() {
        return this.mScIndexType;
    }

    public int getScIndexMask() {
        return this.mScIndexMask;
    }

    public static Builder builder(int mainType) {
        return new Builder(mainType);
    }

    /* renamed from: android.media.tv.tuner.filter.RecordSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private final int mMainType;
        private int mScIndexMask;
        private int mScIndexType;
        private int mTsIndexMask;

        private Builder(int mainType) {
            this.mMainType = mainType;
        }

        public Builder setTsIndexMask(int indexMask) {
            this.mTsIndexMask = indexMask;
            return this;
        }

        public Builder setScIndexType(int indexType) {
            this.mScIndexType = indexType;
            return this;
        }

        public Builder setScIndexMask(int indexMask) {
            this.mScIndexMask = indexMask;
            return this;
        }

        public RecordSettings build() {
            return new RecordSettings(this.mMainType, this.mTsIndexMask, this.mScIndexType, this.mScIndexMask);
        }
    }
}
