package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.DvbsCodeRate */
/* loaded from: classes2.dex */
public class DvbsCodeRate {
    private final int mBitsPer1000Symbol;
    private final long mInnerFec;
    private final boolean mIsLinear;
    private final boolean mIsShortFrames;

    private DvbsCodeRate(long fec, boolean isLinear, boolean isShortFrames, int bitsPer1000Symbol) {
        this.mInnerFec = fec;
        this.mIsLinear = isLinear;
        this.mIsShortFrames = isShortFrames;
        this.mBitsPer1000Symbol = bitsPer1000Symbol;
    }

    public long getInnerFec() {
        return this.mInnerFec;
    }

    public boolean isLinear() {
        return this.mIsLinear;
    }

    public boolean isShortFrameEnabled() {
        return this.mIsShortFrames;
    }

    public int getBitsPer1000Symbol() {
        return this.mBitsPer1000Symbol;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.frontend.DvbsCodeRate$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mBitsPer1000Symbol;
        private long mFec;
        private boolean mIsLinear;
        private boolean mIsShortFrames;

        private Builder() {
        }

        public Builder setInnerFec(long fec) {
            this.mFec = fec;
            return this;
        }

        public Builder setLinear(boolean isLinear) {
            this.mIsLinear = isLinear;
            return this;
        }

        public Builder setShortFrameEnabled(boolean isShortFrames) {
            this.mIsShortFrames = isShortFrames;
            return this;
        }

        public Builder setBitsPer1000Symbol(int bitsPer1000Symbol) {
            this.mBitsPer1000Symbol = bitsPer1000Symbol;
            return this;
        }

        public DvbsCodeRate build() {
            return new DvbsCodeRate(this.mFec, this.mIsLinear, this.mIsShortFrames, this.mBitsPer1000Symbol);
        }
    }
}
