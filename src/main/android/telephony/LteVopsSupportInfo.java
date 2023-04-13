package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class LteVopsSupportInfo extends VopsSupportInfo {
    public static final Parcelable.Creator<LteVopsSupportInfo> CREATOR = new Parcelable.Creator<LteVopsSupportInfo>() { // from class: android.telephony.LteVopsSupportInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LteVopsSupportInfo createFromParcel(Parcel in) {
            in.readInt();
            return new LteVopsSupportInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LteVopsSupportInfo[] newArray(int size) {
            return new LteVopsSupportInfo[size];
        }
    };
    @Deprecated
    public static final int LTE_STATUS_NOT_AVAILABLE = 1;
    public static final int LTE_STATUS_NOT_SUPPORTED = 3;
    public static final int LTE_STATUS_SUPPORTED = 2;
    private final int mEmcBearerSupport;
    private final int mVopsSupport;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface LteVopsStatus {
    }

    public LteVopsSupportInfo(int vops, int emergency) {
        this.mVopsSupport = vops;
        this.mEmcBearerSupport = emergency;
    }

    public int getVopsSupport() {
        return this.mVopsSupport;
    }

    public int getEmcBearerSupport() {
        return this.mEmcBearerSupport;
    }

    @Override // android.telephony.VopsSupportInfo
    public boolean isVopsSupported() {
        return this.mVopsSupport == 2;
    }

    @Override // android.telephony.VopsSupportInfo
    public boolean isEmergencyServiceSupported() {
        return this.mEmcBearerSupport == 2;
    }

    @Override // android.telephony.VopsSupportInfo
    public boolean isEmergencyServiceFallbackSupported() {
        return false;
    }

    @Override // android.telephony.VopsSupportInfo, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.VopsSupportInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags, 3);
        out.writeInt(this.mVopsSupport);
        out.writeInt(this.mEmcBearerSupport);
    }

    @Override // android.telephony.VopsSupportInfo
    public boolean equals(Object o) {
        if (o == null || !(o instanceof LteVopsSupportInfo)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        LteVopsSupportInfo other = (LteVopsSupportInfo) o;
        if (this.mVopsSupport != other.mVopsSupport || this.mEmcBearerSupport != other.mEmcBearerSupport) {
            return false;
        }
        return true;
    }

    @Override // android.telephony.VopsSupportInfo
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mVopsSupport), Integer.valueOf(this.mEmcBearerSupport));
    }

    public String toString() {
        return "LteVopsSupportInfo :  mVopsSupport = " + this.mVopsSupport + " mEmcBearerSupport = " + this.mEmcBearerSupport;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static LteVopsSupportInfo createFromParcelBody(Parcel in) {
        return new LteVopsSupportInfo(in);
    }

    private LteVopsSupportInfo(Parcel in) {
        this.mVopsSupport = in.readInt();
        this.mEmcBearerSupport = in.readInt();
    }
}
