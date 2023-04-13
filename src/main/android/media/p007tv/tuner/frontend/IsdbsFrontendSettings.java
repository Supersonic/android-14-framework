package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.IsdbsFrontendSettings */
/* loaded from: classes2.dex */
public class IsdbsFrontendSettings extends FrontendSettings {
    public static final int CODERATE_1_2 = 2;
    public static final int CODERATE_2_3 = 4;
    public static final int CODERATE_3_4 = 8;
    public static final int CODERATE_5_6 = 16;
    public static final int CODERATE_7_8 = 32;
    public static final int CODERATE_AUTO = 1;
    public static final int CODERATE_UNDEFINED = 0;
    public static final int MODULATION_AUTO = 1;
    public static final int MODULATION_MOD_BPSK = 2;
    public static final int MODULATION_MOD_QPSK = 4;
    public static final int MODULATION_MOD_TC8PSK = 8;
    public static final int MODULATION_UNDEFINED = 0;
    public static final int ROLLOFF_0_35 = 1;
    public static final int ROLLOFF_UNDEFINED = 0;
    public static final int STREAM_ID_TYPE_ID = 0;
    public static final int STREAM_ID_TYPE_RELATIVE_NUMBER = 1;
    private final int mCodeRate;
    private final int mModulation;
    private final int mRolloff;
    private final int mStreamId;
    private final int mStreamIdType;
    private final int mSymbolRate;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbsFrontendSettings$CodeRate */
    /* loaded from: classes2.dex */
    public @interface CodeRate {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbsFrontendSettings$Modulation */
    /* loaded from: classes2.dex */
    public @interface Modulation {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbsFrontendSettings$Rolloff */
    /* loaded from: classes2.dex */
    public @interface Rolloff {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IsdbsFrontendSettings$StreamIdType */
    /* loaded from: classes2.dex */
    public @interface StreamIdType {
    }

    private IsdbsFrontendSettings(long frequency, int streamId, int streamIdType, int modulation, int codeRate, int symbolRate, int rolloff) {
        super(frequency);
        this.mStreamId = streamId;
        this.mStreamIdType = streamIdType;
        this.mModulation = modulation;
        this.mCodeRate = codeRate;
        this.mSymbolRate = symbolRate;
        this.mRolloff = rolloff;
    }

    public int getStreamId() {
        return this.mStreamId;
    }

    public int getStreamIdType() {
        return this.mStreamIdType;
    }

    public int getModulation() {
        return this.mModulation;
    }

    public int getCodeRate() {
        return this.mCodeRate;
    }

    public int getSymbolRate() {
        return this.mSymbolRate;
    }

    public int getRolloff() {
        return this.mRolloff;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.frontend.IsdbsFrontendSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mCodeRate;
        private long mFrequency;
        private int mModulation;
        private int mRolloff;
        private int mStreamId;
        private int mStreamIdType;
        private int mSymbolRate;

        private Builder() {
            this.mFrequency = 0L;
            this.mStreamId = 65535;
            this.mStreamIdType = 0;
            this.mModulation = 0;
            this.mCodeRate = 0;
            this.mSymbolRate = 0;
            this.mRolloff = 0;
        }

        @Deprecated
        public Builder setFrequency(int frequency) {
            return setFrequencyLong(frequency);
        }

        public Builder setFrequencyLong(long frequency) {
            this.mFrequency = frequency;
            return this;
        }

        public Builder setStreamId(int streamId) {
            this.mStreamId = streamId;
            return this;
        }

        public Builder setStreamIdType(int streamIdType) {
            this.mStreamIdType = streamIdType;
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

        public Builder setSymbolRate(int symbolRate) {
            this.mSymbolRate = symbolRate;
            return this;
        }

        public Builder setRolloff(int rolloff) {
            this.mRolloff = rolloff;
            return this;
        }

        public IsdbsFrontendSettings build() {
            return new IsdbsFrontendSettings(this.mFrequency, this.mStreamId, this.mStreamIdType, this.mModulation, this.mCodeRate, this.mSymbolRate, this.mRolloff);
        }
    }

    @Override // android.media.p007tv.tuner.frontend.FrontendSettings
    public int getType() {
        return 7;
    }
}
