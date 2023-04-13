package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.filter.SectionSettings;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.SectionSettingsWithSectionBits */
/* loaded from: classes2.dex */
public class SectionSettingsWithSectionBits extends SectionSettings {
    private final byte[] mFilter;
    private final byte[] mMask;
    private final byte[] mMode;

    private SectionSettingsWithSectionBits(int mainType, boolean isCheckCrc, boolean isRepeat, boolean isRaw, int bitWidthOfLengthField, byte[] filter, byte[] mask, byte[] mode) {
        super(mainType, isCheckCrc, isRepeat, isRaw, bitWidthOfLengthField);
        this.mFilter = filter;
        this.mMask = mask;
        this.mMode = mode;
    }

    public byte[] getFilterBytes() {
        return this.mFilter;
    }

    public byte[] getMask() {
        return this.mMask;
    }

    public byte[] getMode() {
        return this.mMode;
    }

    public static Builder builder(int mainType) {
        return new Builder(mainType);
    }

    /* renamed from: android.media.tv.tuner.filter.SectionSettingsWithSectionBits$Builder */
    /* loaded from: classes2.dex */
    public static class Builder extends SectionSettings.Builder<Builder> {
        private byte[] mFilter;
        private byte[] mMask;
        private byte[] mMode;

        private Builder(int mainType) {
            super(mainType);
            this.mFilter = new byte[0];
            this.mMask = new byte[0];
            this.mMode = new byte[0];
        }

        public Builder setFilter(byte[] filter) {
            this.mFilter = filter;
            return this;
        }

        public Builder setMask(byte[] mask) {
            this.mMask = mask;
            return this;
        }

        public Builder setMode(byte[] mode) {
            this.mMode = mode;
            return this;
        }

        public SectionSettingsWithSectionBits build() {
            return new SectionSettingsWithSectionBits(this.mMainType, this.mCrcEnabled, this.mIsRepeat, this.mIsRaw, this.mBitWidthOfLengthField, this.mFilter, this.mMask, this.mMode);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // android.media.p007tv.tuner.filter.SectionSettings.Builder
        public Builder self() {
            return this;
        }
    }
}
