package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.Atsc3PlpSettings */
/* loaded from: classes2.dex */
public class Atsc3PlpSettings {
    private final int mCodeRate;
    private final int mFec;
    private final int mInterleaveMode;
    private final int mModulation;
    private final int mPlpId;

    private Atsc3PlpSettings(int plpId, int modulation, int interleaveMode, int codeRate, int fec) {
        this.mPlpId = plpId;
        this.mModulation = modulation;
        this.mInterleaveMode = interleaveMode;
        this.mCodeRate = codeRate;
        this.mFec = fec;
    }

    public int getPlpId() {
        return this.mPlpId;
    }

    public int getModulation() {
        return this.mModulation;
    }

    public int getInterleaveMode() {
        return this.mInterleaveMode;
    }

    public int getCodeRate() {
        return this.mCodeRate;
    }

    public int getFec() {
        return this.mFec;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.frontend.Atsc3PlpSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mCodeRate;
        private int mFec;
        private int mInterleaveMode;
        private int mModulation;
        private int mPlpId;

        private Builder() {
        }

        public Builder setPlpId(int plpId) {
            this.mPlpId = plpId;
            return this;
        }

        public Builder setModulation(int modulation) {
            this.mModulation = modulation;
            return this;
        }

        public Builder setInterleaveMode(int interleaveMode) {
            this.mInterleaveMode = interleaveMode;
            return this;
        }

        public Builder setCodeRate(int codeRate) {
            this.mCodeRate = codeRate;
            return this;
        }

        public Builder setFec(int fec) {
            this.mFec = fec;
            return this;
        }

        public Atsc3PlpSettings build() {
            return new Atsc3PlpSettings(this.mPlpId, this.mModulation, this.mInterleaveMode, this.mCodeRate, this.mFec);
        }
    }
}
