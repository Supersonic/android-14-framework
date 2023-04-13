package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerUtils;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.SectionSettings */
/* loaded from: classes2.dex */
public abstract class SectionSettings extends Settings {
    final int mBitWidthOfLengthField;
    final boolean mCrcEnabled;
    final boolean mIsRaw;
    final boolean mIsRepeat;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SectionSettings(int mainType, boolean crcEnabled, boolean isRepeat, boolean isRaw, int bitWidthOfLengthField) {
        super(TunerUtils.getFilterSubtype(mainType, 1));
        this.mCrcEnabled = crcEnabled;
        this.mIsRepeat = isRepeat;
        this.mIsRaw = isRaw;
        this.mBitWidthOfLengthField = bitWidthOfLengthField;
    }

    public boolean isCrcEnabled() {
        return this.mCrcEnabled;
    }

    public boolean isRepeat() {
        return this.mIsRepeat;
    }

    public boolean isRaw() {
        return this.mIsRaw;
    }

    public int getLengthFieldBitWidth() {
        return this.mBitWidthOfLengthField;
    }

    /* renamed from: android.media.tv.tuner.filter.SectionSettings$Builder */
    /* loaded from: classes2.dex */
    public static abstract class Builder<T extends Builder<T>> {
        int mBitWidthOfLengthField;
        boolean mCrcEnabled;
        boolean mIsRaw;
        boolean mIsRepeat;
        final int mMainType;

        abstract T self();

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder(int mainType) {
            this.mMainType = mainType;
        }

        public T setCrcEnabled(boolean crcEnabled) {
            this.mCrcEnabled = crcEnabled;
            return self();
        }

        public T setRepeat(boolean isRepeat) {
            this.mIsRepeat = isRepeat;
            return self();
        }

        public T setRaw(boolean isRaw) {
            this.mIsRaw = isRaw;
            return self();
        }

        public T setBitWidthOfLengthField(int bitWidthOfLengthField) {
            this.mBitWidthOfLengthField = bitWidthOfLengthField;
            return self();
        }
    }
}
