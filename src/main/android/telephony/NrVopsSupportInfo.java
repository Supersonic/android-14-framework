package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class NrVopsSupportInfo extends VopsSupportInfo {
    public static final Parcelable.Creator<NrVopsSupportInfo> CREATOR = new Parcelable.Creator<NrVopsSupportInfo>() { // from class: android.telephony.NrVopsSupportInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NrVopsSupportInfo createFromParcel(Parcel in) {
            in.readInt();
            return new NrVopsSupportInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NrVopsSupportInfo[] newArray(int size) {
            return new NrVopsSupportInfo[size];
        }
    };
    public static final int NR_STATUS_EMC_5GCN_ONLY = 1;
    public static final int NR_STATUS_EMC_EUTRA_5GCN_ONLY = 2;
    public static final int NR_STATUS_EMC_NOT_SUPPORTED = 0;
    public static final int NR_STATUS_EMC_NR_EUTRA_5GCN = 3;
    public static final int NR_STATUS_EMF_5GCN_ONLY = 1;
    public static final int NR_STATUS_EMF_EUTRA_5GCN_ONLY = 2;
    public static final int NR_STATUS_EMF_NOT_SUPPORTED = 0;
    public static final int NR_STATUS_EMF_NR_EUTRA_5GCN = 3;
    public static final int NR_STATUS_VOPS_3GPP_SUPPORTED = 1;
    public static final int NR_STATUS_VOPS_NON_3GPP_SUPPORTED = 2;
    public static final int NR_STATUS_VOPS_NOT_SUPPORTED = 0;
    private final int mEmcSupport;
    private final int mEmfSupport;
    private final int mVopsSupport;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface NrEmcStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface NrEmfStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface NrVopsStatus {
    }

    public NrVopsSupportInfo(int vops, int emc, int emf) {
        this.mVopsSupport = vops;
        this.mEmcSupport = emc;
        this.mEmfSupport = emf;
    }

    public int getVopsSupport() {
        return this.mVopsSupport;
    }

    public int getEmcSupport() {
        return this.mEmcSupport;
    }

    public int getEmfSupport() {
        return this.mEmfSupport;
    }

    @Override // android.telephony.VopsSupportInfo
    public boolean isVopsSupported() {
        return this.mVopsSupport != 0;
    }

    @Override // android.telephony.VopsSupportInfo
    public boolean isEmergencyServiceSupported() {
        return this.mEmcSupport != 0;
    }

    @Override // android.telephony.VopsSupportInfo
    public boolean isEmergencyServiceFallbackSupported() {
        return this.mEmfSupport != 0;
    }

    @Override // android.telephony.VopsSupportInfo, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.VopsSupportInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags, 6);
        out.writeInt(this.mVopsSupport);
        out.writeInt(this.mEmcSupport);
        out.writeInt(this.mEmfSupport);
    }

    @Override // android.telephony.VopsSupportInfo
    public boolean equals(Object o) {
        if (o == null || !(o instanceof NrVopsSupportInfo)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        NrVopsSupportInfo other = (NrVopsSupportInfo) o;
        if (this.mVopsSupport != other.mVopsSupport || this.mEmcSupport != other.mEmcSupport || this.mEmfSupport != other.mEmfSupport) {
            return false;
        }
        return true;
    }

    @Override // android.telephony.VopsSupportInfo
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mVopsSupport), Integer.valueOf(this.mEmcSupport), Integer.valueOf(this.mEmfSupport));
    }

    public String toString() {
        return "NrVopsSupportInfo :  mVopsSupport = " + this.mVopsSupport + " mEmcSupport = " + this.mEmcSupport + " mEmfSupport = " + this.mEmfSupport;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static NrVopsSupportInfo createFromParcelBody(Parcel in) {
        return new NrVopsSupportInfo(in);
    }

    private NrVopsSupportInfo(Parcel in) {
        this.mVopsSupport = in.readInt();
        this.mEmcSupport = in.readInt();
        this.mEmfSupport = in.readInt();
    }
}
