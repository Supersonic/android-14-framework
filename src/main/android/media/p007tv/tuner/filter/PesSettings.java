package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerUtils;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.PesSettings */
/* loaded from: classes2.dex */
public class PesSettings extends Settings {
    private final boolean mIsRaw;
    private final int mStreamId;

    private PesSettings(int mainType, int streamId, boolean isRaw) {
        super(TunerUtils.getFilterSubtype(mainType, 2));
        this.mStreamId = streamId;
        this.mIsRaw = isRaw;
    }

    public int getStreamId() {
        return this.mStreamId;
    }

    public boolean isRaw() {
        return this.mIsRaw;
    }

    public static Builder builder(int mainType) {
        return new Builder(mainType);
    }

    /* renamed from: android.media.tv.tuner.filter.PesSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private boolean mIsRaw;
        private final int mMainType;
        private int mStreamId;

        private Builder(int mainType) {
            this.mMainType = mainType;
        }

        public Builder setStreamId(int streamId) {
            this.mStreamId = streamId;
            return this;
        }

        public Builder setRaw(boolean isRaw) {
            this.mIsRaw = isRaw;
            return this;
        }

        public PesSettings build() {
            return new PesSettings(this.mMainType, this.mStreamId, this.mIsRaw);
        }
    }
}
