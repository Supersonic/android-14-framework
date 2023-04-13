package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.IptvFrontendSettingsFec */
/* loaded from: classes2.dex */
public final class IptvFrontendSettingsFec {
    public static final int FEC_TYPE_COLUMN = 1;
    public static final int FEC_TYPE_COLUMN_ROW = 4;
    public static final int FEC_TYPE_ROW = 2;
    public static final int FEC_TYPE_UNDEFINED = 0;
    private final int mFecColNum;
    private final int mFecRowNum;
    private final int mFecType;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.IptvFrontendSettingsFec$FecType */
    /* loaded from: classes2.dex */
    public @interface FecType {
    }

    private IptvFrontendSettingsFec(int fecType, int fecRowNum, int fecColNum) {
        this.mFecType = fecType;
        this.mFecRowNum = fecRowNum;
        this.mFecColNum = fecColNum;
    }

    public int getFecType() {
        return this.mFecType;
    }

    public int getFecRowNum() {
        return this.mFecRowNum;
    }

    public int getFecColNum() {
        return this.mFecColNum;
    }

    /* renamed from: android.media.tv.tuner.frontend.IptvFrontendSettingsFec$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mFecColNum;
        private int mFecRowNum;
        private int mFecType;

        public Builder setFecType(int fecType) {
            this.mFecType = fecType;
            return this;
        }

        public Builder setFecRowNum(int fecRowNum) {
            this.mFecRowNum = fecRowNum;
            return this;
        }

        public Builder setFecColNum(int fecColNum) {
            this.mFecColNum = fecColNum;
            return this;
        }

        public IptvFrontendSettingsFec build() {
            return new IptvFrontendSettingsFec(this.mFecType, this.mFecRowNum, this.mFecColNum);
        }
    }
}
