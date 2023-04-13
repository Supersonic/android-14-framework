package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.MmtpFilterConfiguration */
/* loaded from: classes2.dex */
public final class MmtpFilterConfiguration extends FilterConfiguration {
    private final int mMmtpPid;

    private MmtpFilterConfiguration(Settings settings, int mmtpPid) {
        super(settings);
        this.mMmtpPid = mmtpPid;
    }

    @Override // android.media.p007tv.tuner.filter.FilterConfiguration
    public int getType() {
        return 2;
    }

    public int getMmtpPacketId() {
        return this.mMmtpPid;
    }

    public static Builder builder() {
        return new Builder();
    }

    /* renamed from: android.media.tv.tuner.filter.MmtpFilterConfiguration$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mMmtpPid;
        private Settings mSettings;

        private Builder() {
            this.mMmtpPid = 65535;
        }

        public Builder setMmtpPacketId(int mmtpPid) {
            this.mMmtpPid = mmtpPid;
            return this;
        }

        public Builder setSettings(Settings settings) {
            this.mSettings = settings;
            return this;
        }

        public MmtpFilterConfiguration build() {
            return new MmtpFilterConfiguration(this.mSettings, this.mMmtpPid);
        }
    }
}
