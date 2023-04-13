package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CellInfoLte extends CellInfo implements Parcelable {
    public static final Parcelable.Creator<CellInfoLte> CREATOR = new Parcelable.Creator<CellInfoLte>() { // from class: android.telephony.CellInfoLte.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoLte createFromParcel(Parcel in) {
            in.readInt();
            return CellInfoLte.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoLte[] newArray(int size) {
            return new CellInfoLte[size];
        }
    };
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CellInfoLte";
    private CellConfigLte mCellConfig;
    private CellIdentityLte mCellIdentityLte;
    private CellSignalStrengthLte mCellSignalStrengthLte;

    public CellInfoLte() {
        this.mCellIdentityLte = new CellIdentityLte();
        this.mCellSignalStrengthLte = new CellSignalStrengthLte();
        this.mCellConfig = new CellConfigLte();
    }

    public CellInfoLte(CellInfoLte ci) {
        super(ci);
        this.mCellIdentityLte = ci.mCellIdentityLte.copy();
        this.mCellSignalStrengthLte = ci.mCellSignalStrengthLte.copy();
        this.mCellConfig = new CellConfigLte(ci.mCellConfig);
    }

    public CellInfoLte(int connectionStatus, boolean registered, long timeStamp, CellIdentityLte cellIdentityLte, CellSignalStrengthLte cellSignalStrengthLte, CellConfigLte cellConfig) {
        super(connectionStatus, registered, timeStamp);
        this.mCellIdentityLte = cellIdentityLte;
        this.mCellSignalStrengthLte = cellSignalStrengthLte;
        this.mCellConfig = cellConfig;
    }

    @Override // android.telephony.CellInfo
    public CellIdentityLte getCellIdentity() {
        return this.mCellIdentityLte;
    }

    public void setCellIdentity(CellIdentityLte cid) {
        this.mCellIdentityLte = cid;
    }

    @Override // android.telephony.CellInfo
    public CellSignalStrengthLte getCellSignalStrength() {
        return this.mCellSignalStrengthLte;
    }

    @Override // android.telephony.CellInfo
    public CellInfo sanitizeLocationInfo() {
        CellInfoLte result = new CellInfoLte(this);
        result.mCellIdentityLte = this.mCellIdentityLte.sanitizeLocationInfo();
        return result;
    }

    public void setCellSignalStrength(CellSignalStrengthLte css) {
        this.mCellSignalStrengthLte = css;
    }

    public void setCellConfig(CellConfigLte cellConfig) {
        this.mCellConfig = cellConfig;
    }

    public CellConfigLte getCellConfig() {
        return this.mCellConfig;
    }

    @Override // android.telephony.CellInfo
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), Integer.valueOf(this.mCellIdentityLte.hashCode()), Integer.valueOf(this.mCellSignalStrengthLte.hashCode()), Integer.valueOf(this.mCellConfig.hashCode()));
    }

    @Override // android.telephony.CellInfo
    public boolean equals(Object other) {
        if (other instanceof CellInfoLte) {
            CellInfoLte o = (CellInfoLte) other;
            return super.equals(o) && this.mCellIdentityLte.equals(o.mCellIdentityLte) && this.mCellSignalStrengthLte.equals(o.mCellSignalStrengthLte) && this.mCellConfig.equals(o.mCellConfig);
        }
        return false;
    }

    @Override // android.telephony.CellInfo
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CellInfoLte:{");
        sb.append(super.toString());
        sb.append(" ").append(this.mCellIdentityLte);
        sb.append(" ").append(this.mCellSignalStrengthLte);
        sb.append(" ").append(this.mCellConfig);
        sb.append("}");
        return sb.toString();
    }

    @Override // android.telephony.CellInfo, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.CellInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags, 3);
        this.mCellIdentityLte.writeToParcel(dest, flags);
        this.mCellSignalStrengthLte.writeToParcel(dest, flags);
        this.mCellConfig.writeToParcel(dest, flags);
    }

    private CellInfoLte(Parcel in) {
        super(in);
        this.mCellIdentityLte = CellIdentityLte.CREATOR.createFromParcel(in);
        this.mCellSignalStrengthLte = CellSignalStrengthLte.CREATOR.createFromParcel(in);
        this.mCellConfig = CellConfigLte.CREATOR.createFromParcel(in);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellInfoLte createFromParcelBody(Parcel in) {
        return new CellInfoLte(in);
    }

    private static void log(String s) {
        com.android.telephony.Rlog.m2w(LOG_TAG, s);
    }
}
