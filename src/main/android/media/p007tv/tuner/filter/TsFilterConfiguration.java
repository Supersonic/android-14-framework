package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.TsFilterConfiguration */
/* loaded from: classes2.dex */
public final class TsFilterConfiguration extends FilterConfiguration {
    private final int mTpid;

    private TsFilterConfiguration(Settings settings, int tpid) {
        super(settings);
        this.mTpid = tpid;
    }

    @Override // android.media.p007tv.tuner.filter.FilterConfiguration
    public int getType() {
        return 1;
    }

    public int getTpid() {
        return this.mTpid;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.filter.TsFilterConfiguration$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private Settings mSettings;
        private int mTpid;

        private Builder() {
            this.mTpid = 0;
        }

        public Builder setTpid(int tpid) {
            this.mTpid = tpid;
            return this;
        }

        public Builder setSettings(Settings settings) {
            this.mSettings = settings;
            return this;
        }

        public TsFilterConfiguration build() {
            return new TsFilterConfiguration(this.mSettings, this.mTpid);
        }
    }
}
