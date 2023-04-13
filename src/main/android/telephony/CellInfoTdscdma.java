package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CellInfoTdscdma extends CellInfo implements Parcelable {
    public static final Parcelable.Creator<CellInfoTdscdma> CREATOR = new Parcelable.Creator<CellInfoTdscdma>() { // from class: android.telephony.CellInfoTdscdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoTdscdma createFromParcel(Parcel in) {
            in.readInt();
            return CellInfoTdscdma.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoTdscdma[] newArray(int size) {
            return new CellInfoTdscdma[size];
        }
    };
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CellInfoTdscdma";
    private CellIdentityTdscdma mCellIdentityTdscdma;
    private CellSignalStrengthTdscdma mCellSignalStrengthTdscdma;

    public CellInfoTdscdma() {
        this.mCellIdentityTdscdma = new CellIdentityTdscdma();
        this.mCellSignalStrengthTdscdma = new CellSignalStrengthTdscdma();
    }

    public CellInfoTdscdma(CellInfoTdscdma ci) {
        super(ci);
        this.mCellIdentityTdscdma = ci.mCellIdentityTdscdma.copy();
        this.mCellSignalStrengthTdscdma = ci.mCellSignalStrengthTdscdma.copy();
    }

    public CellInfoTdscdma(int connectionStatus, boolean registered, long timeStamp, CellIdentityTdscdma cellIdentityTdscdma, CellSignalStrengthTdscdma cellSignalStrengthTdscdma) {
        super(connectionStatus, registered, timeStamp);
        this.mCellIdentityTdscdma = cellIdentityTdscdma;
        this.mCellSignalStrengthTdscdma = cellSignalStrengthTdscdma;
    }

    @Override // android.telephony.CellInfo
    public CellIdentityTdscdma getCellIdentity() {
        return this.mCellIdentityTdscdma;
    }

    public void setCellIdentity(CellIdentityTdscdma cid) {
        this.mCellIdentityTdscdma = cid;
    }

    @Override // android.telephony.CellInfo
    public CellSignalStrengthTdscdma getCellSignalStrength() {
        return this.mCellSignalStrengthTdscdma;
    }

    @Override // android.telephony.CellInfo
    public CellInfo sanitizeLocationInfo() {
        CellInfoTdscdma result = new CellInfoTdscdma(this);
        result.mCellIdentityTdscdma = this.mCellIdentityTdscdma.sanitizeLocationInfo();
        return result;
    }

    public void setCellSignalStrength(CellSignalStrengthTdscdma css) {
        this.mCellSignalStrengthTdscdma = css;
    }

    @Override // android.telephony.CellInfo
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mCellIdentityTdscdma, this.mCellSignalStrengthTdscdma);
    }

    @Override // android.telephony.CellInfo
    public boolean equals(Object other) {
        if (super.equals(other)) {
            try {
                CellInfoTdscdma o = (CellInfoTdscdma) other;
                if (this.mCellIdentityTdscdma.equals(o.mCellIdentityTdscdma)) {
                    return this.mCellSignalStrengthTdscdma.equals(o.mCellSignalStrengthTdscdma);
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
        sb.append("CellInfoTdscdma:{");
        sb.append(super.toString());
        sb.append(" ").append(this.mCellIdentityTdscdma);
        sb.append(" ").append(this.mCellSignalStrengthTdscdma);
        sb.append("}");
        return sb.toString();
    }

    @Override // android.telephony.CellInfo, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.CellInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags, 5);
        this.mCellIdentityTdscdma.writeToParcel(dest, flags);
        this.mCellSignalStrengthTdscdma.writeToParcel(dest, flags);
    }

    private CellInfoTdscdma(Parcel in) {
        super(in);
        this.mCellIdentityTdscdma = CellIdentityTdscdma.CREATOR.createFromParcel(in);
        this.mCellSignalStrengthTdscdma = CellSignalStrengthTdscdma.CREATOR.createFromParcel(in);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellInfoTdscdma createFromParcelBody(Parcel in) {
        return new CellInfoTdscdma(in);
    }

    private static void log(String s) {
        com.android.telephony.Rlog.m2w(LOG_TAG, s);
    }
}
