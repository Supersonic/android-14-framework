package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CellInfoWcdma extends CellInfo implements Parcelable {
    public static final Parcelable.Creator<CellInfoWcdma> CREATOR = new Parcelable.Creator<CellInfoWcdma>() { // from class: android.telephony.CellInfoWcdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoWcdma createFromParcel(Parcel in) {
            in.readInt();
            return CellInfoWcdma.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoWcdma[] newArray(int size) {
            return new CellInfoWcdma[size];
        }
    };
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CellInfoWcdma";
    private CellIdentityWcdma mCellIdentityWcdma;
    private CellSignalStrengthWcdma mCellSignalStrengthWcdma;

    public CellInfoWcdma() {
        this.mCellIdentityWcdma = new CellIdentityWcdma();
        this.mCellSignalStrengthWcdma = new CellSignalStrengthWcdma();
    }

    public CellInfoWcdma(CellInfoWcdma ci) {
        super(ci);
        this.mCellIdentityWcdma = ci.mCellIdentityWcdma.copy();
        this.mCellSignalStrengthWcdma = ci.mCellSignalStrengthWcdma.copy();
    }

    public CellInfoWcdma(int connectionStatus, boolean registered, long timeStamp, CellIdentityWcdma cellIdentityWcdma, CellSignalStrengthWcdma cellSignalStrengthWcdma) {
        super(connectionStatus, registered, timeStamp);
        this.mCellIdentityWcdma = cellIdentityWcdma;
        this.mCellSignalStrengthWcdma = cellSignalStrengthWcdma;
    }

    @Override // android.telephony.CellInfo
    public CellIdentityWcdma getCellIdentity() {
        return this.mCellIdentityWcdma;
    }

    public void setCellIdentity(CellIdentityWcdma cid) {
        this.mCellIdentityWcdma = cid;
    }

    @Override // android.telephony.CellInfo
    public CellSignalStrengthWcdma getCellSignalStrength() {
        return this.mCellSignalStrengthWcdma;
    }

    @Override // android.telephony.CellInfo
    public CellInfo sanitizeLocationInfo() {
        CellInfoWcdma result = new CellInfoWcdma(this);
        result.mCellIdentityWcdma = this.mCellIdentityWcdma.sanitizeLocationInfo();
        return result;
    }

    public void setCellSignalStrength(CellSignalStrengthWcdma css) {
        this.mCellSignalStrengthWcdma = css;
    }

    @Override // android.telephony.CellInfo
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mCellIdentityWcdma, this.mCellSignalStrengthWcdma);
    }

    @Override // android.telephony.CellInfo
    public boolean equals(Object other) {
        if (super.equals(other)) {
            try {
                CellInfoWcdma o = (CellInfoWcdma) other;
                if (this.mCellIdentityWcdma.equals(o.mCellIdentityWcdma)) {
                    return this.mCellSignalStrengthWcdma.equals(o.mCellSignalStrengthWcdma);
                }
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }
        return false;
    }

    @Override // android.telephony.CellInfo
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CellInfoWcdma:{");
        sb.append(super.toString());
        sb.append(" ").append(this.mCellIdentityWcdma);
        sb.append(" ").append(this.mCellSignalStrengthWcdma);
        sb.append("}");
        return sb.toString();
    }

    @Override // android.telephony.CellInfo, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.CellInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags, 4);
        this.mCellIdentityWcdma.writeToParcel(dest, flags);
        this.mCellSignalStrengthWcdma.writeToParcel(dest, flags);
    }

    private CellInfoWcdma(Parcel in) {
        super(in);
        this.mCellIdentityWcdma = CellIdentityWcdma.CREATOR.createFromParcel(in);
        this.mCellSignalStrengthWcdma = CellSignalStrengthWcdma.CREATOR.createFromParcel(in);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellInfoWcdma createFromParcelBody(Parcel in) {
        return new CellInfoWcdma(in);
    }

    private static void log(String s) {
        com.android.telephony.Rlog.m2w(LOG_TAG, s);
    }
}
