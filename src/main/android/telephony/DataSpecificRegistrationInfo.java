package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class DataSpecificRegistrationInfo implements Parcelable {
    public static final Parcelable.Creator<DataSpecificRegistrationInfo> CREATOR = new Parcelable.Creator<DataSpecificRegistrationInfo>() { // from class: android.telephony.DataSpecificRegistrationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataSpecificRegistrationInfo createFromParcel(Parcel source) {
            return new DataSpecificRegistrationInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DataSpecificRegistrationInfo[] newArray(int size) {
            return new DataSpecificRegistrationInfo[size];
        }
    };
    public static final int LTE_ATTACH_EXTRA_INFO_CSFB_NOT_PREFERRED = 1;
    public static final int LTE_ATTACH_EXTRA_INFO_NONE = 0;
    public static final int LTE_ATTACH_EXTRA_INFO_SMS_ONLY = 2;
    public static final int LTE_ATTACH_TYPE_COMBINED = 2;
    public static final int LTE_ATTACH_TYPE_EPS_ONLY = 1;
    public static final int LTE_ATTACH_TYPE_UNKNOWN = 0;
    public final boolean isDcNrRestricted;
    public final boolean isEnDcAvailable;
    public final boolean isNrAvailable;
    private final int mLteAttachExtraInfo;
    private final int mLteAttachResultType;
    private final VopsSupportInfo mVopsSupportInfo;
    public final int maxDataCalls;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LteAttachExtraInfo {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LteAttachResultType {
    }

    private DataSpecificRegistrationInfo(Builder builder) {
        this.maxDataCalls = builder.mMaxDataCalls;
        this.isDcNrRestricted = builder.mIsDcNrRestricted;
        this.isNrAvailable = builder.mIsNrAvailable;
        this.isEnDcAvailable = builder.mIsEnDcAvailable;
        this.mVopsSupportInfo = builder.mVopsSupportInfo;
        this.mLteAttachResultType = builder.mLteAttachResultType;
        this.mLteAttachExtraInfo = builder.mLteAttachExtraInfo;
    }

    public DataSpecificRegistrationInfo(int maxDataCalls, boolean isDcNrRestricted, boolean isNrAvailable, boolean isEnDcAvailable, VopsSupportInfo vops) {
        this.maxDataCalls = maxDataCalls;
        this.isDcNrRestricted = isDcNrRestricted;
        this.isNrAvailable = isNrAvailable;
        this.isEnDcAvailable = isEnDcAvailable;
        this.mVopsSupportInfo = vops;
        this.mLteAttachResultType = 0;
        this.mLteAttachExtraInfo = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataSpecificRegistrationInfo(DataSpecificRegistrationInfo dsri) {
        this.maxDataCalls = dsri.maxDataCalls;
        this.isDcNrRestricted = dsri.isDcNrRestricted;
        this.isNrAvailable = dsri.isNrAvailable;
        this.isEnDcAvailable = dsri.isEnDcAvailable;
        this.mVopsSupportInfo = dsri.mVopsSupportInfo;
        this.mLteAttachResultType = dsri.mLteAttachResultType;
        this.mLteAttachExtraInfo = dsri.mLteAttachExtraInfo;
    }

    private DataSpecificRegistrationInfo(Parcel source) {
        this.maxDataCalls = source.readInt();
        this.isDcNrRestricted = source.readBoolean();
        this.isNrAvailable = source.readBoolean();
        this.isEnDcAvailable = source.readBoolean();
        this.mVopsSupportInfo = (VopsSupportInfo) source.readParcelable(VopsSupportInfo.class.getClassLoader(), VopsSupportInfo.class);
        this.mLteAttachResultType = source.readInt();
        this.mLteAttachExtraInfo = source.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxDataCalls);
        dest.writeBoolean(this.isDcNrRestricted);
        dest.writeBoolean(this.isNrAvailable);
        dest.writeBoolean(this.isEnDcAvailable);
        dest.writeParcelable(this.mVopsSupportInfo, flags);
        dest.writeInt(this.mLteAttachResultType);
        dest.writeInt(this.mLteAttachExtraInfo);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return getClass().getName() + " :{" + (" maxDataCalls = " + this.maxDataCalls) + (" isDcNrRestricted = " + this.isDcNrRestricted) + (" isNrAvailable = " + this.isNrAvailable) + (" isEnDcAvailable = " + this.isEnDcAvailable) + (" mLteAttachResultType = " + this.mLteAttachResultType) + (" mLteAttachExtraInfo = " + this.mLteAttachExtraInfo) + (" " + this.mVopsSupportInfo) + " }";
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.maxDataCalls), Boolean.valueOf(this.isDcNrRestricted), Boolean.valueOf(this.isNrAvailable), Boolean.valueOf(this.isEnDcAvailable), this.mVopsSupportInfo, Integer.valueOf(this.mLteAttachResultType), Integer.valueOf(this.mLteAttachExtraInfo));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DataSpecificRegistrationInfo) {
            DataSpecificRegistrationInfo other = (DataSpecificRegistrationInfo) o;
            return this.maxDataCalls == other.maxDataCalls && this.isDcNrRestricted == other.isDcNrRestricted && this.isNrAvailable == other.isNrAvailable && this.isEnDcAvailable == other.isEnDcAvailable && Objects.equals(this.mVopsSupportInfo, other.mVopsSupportInfo) && this.mLteAttachResultType == other.mLteAttachResultType && this.mLteAttachExtraInfo == other.mLteAttachExtraInfo;
        }
        return false;
    }

    @Deprecated
    public LteVopsSupportInfo getLteVopsSupportInfo() {
        VopsSupportInfo vopsSupportInfo = this.mVopsSupportInfo;
        if (vopsSupportInfo instanceof LteVopsSupportInfo) {
            return (LteVopsSupportInfo) vopsSupportInfo;
        }
        return new LteVopsSupportInfo(1, 1);
    }

    public VopsSupportInfo getVopsSupportInfo() {
        return this.mVopsSupportInfo;
    }

    public int getLteAttachResultType() {
        return this.mLteAttachResultType;
    }

    public int getLteAttachExtraInfo() {
        return this.mLteAttachExtraInfo;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private boolean mIsDcNrRestricted;
        private boolean mIsEnDcAvailable;
        private boolean mIsNrAvailable;
        private final int mMaxDataCalls;
        private VopsSupportInfo mVopsSupportInfo;
        private int mLteAttachResultType = 0;
        private int mLteAttachExtraInfo = 0;

        public Builder(int maxDataCalls) {
            this.mMaxDataCalls = maxDataCalls;
        }

        public Builder setDcNrRestricted(boolean isDcNrRestricted) {
            this.mIsDcNrRestricted = isDcNrRestricted;
            return this;
        }

        public Builder setNrAvailable(boolean isNrAvailable) {
            this.mIsNrAvailable = isNrAvailable;
            return this;
        }

        public Builder setEnDcAvailable(boolean isEnDcAvailable) {
            this.mIsEnDcAvailable = isEnDcAvailable;
            return this;
        }

        public Builder setVopsSupportInfo(VopsSupportInfo vops) {
            this.mVopsSupportInfo = vops;
            return this;
        }

        public Builder setLteAttachResultType(int lteAttachResultType) {
            this.mLteAttachResultType = lteAttachResultType;
            return this;
        }

        public Builder setLteAttachExtraInfo(int lteAttachExtraInfo) {
            this.mLteAttachExtraInfo = lteAttachExtraInfo;
            return this;
        }

        public DataSpecificRegistrationInfo build() {
            return new DataSpecificRegistrationInfo(this);
        }
    }
}
