package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerUtils;
import android.media.p007tv.tuner.TunerVersionChecker;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.DownloadSettings */
/* loaded from: classes2.dex */
public class DownloadSettings extends Settings {
    private final int mDownloadId;
    private final boolean mUseDownloadId;

    private DownloadSettings(int mainType, boolean useDownloadId, int downloadId) {
        super(TunerUtils.getFilterSubtype(mainType, 5));
        this.mUseDownloadId = useDownloadId;
        this.mDownloadId = downloadId;
    }

    public int getDownloadId() {
        return this.mDownloadId;
    }

    public boolean useDownloadId() {
        return this.mUseDownloadId;
    }

    public static Builder builder(int mainType) {
        return new Builder(mainType);
    }

    /* renamed from: android.media.tv.tuner.filter.DownloadSettings$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        private int mDownloadId;
        private final int mMainType;
        private boolean mUseDownloadId;

        private Builder(int mainType) {
            this.mUseDownloadId = false;
            this.mMainType = mainType;
        }

        public Builder setUseDownloadId(boolean useDownloadId) {
            if (TunerVersionChecker.checkHigherOrEqualVersionTo(131072, "setUseDownloadId")) {
                this.mUseDownloadId = useDownloadId;
            }
            return this;
        }

        public Builder setDownloadId(int downloadId) {
            this.mDownloadId = downloadId;
            return this;
        }

        public DownloadSettings build() {
            return new DownloadSettings(this.mMainType, this.mUseDownloadId, this.mDownloadId);
        }
    }
}
