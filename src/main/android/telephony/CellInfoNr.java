package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CellInfoNr extends CellInfo {
    public static final Parcelable.Creator<CellInfoNr> CREATOR = new Parcelable.Creator<CellInfoNr>() { // from class: android.telephony.CellInfoNr.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoNr createFromParcel(Parcel in) {
            in.readInt();
            return new CellInfoNr(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfoNr[] newArray(int size) {
            return new CellInfoNr[size];
        }
    };
    private static final String TAG = "CellInfoNr";
    private CellIdentityNr mCellIdentity;
    private final CellSignalStrengthNr mCellSignalStrength;

    @Override // android.telephony.CellInfo
    public /* bridge */ /* synthetic */ CellIdentityNr getCellIdentity() {
        return (CellIdentityNr) getCellIdentity();
    }

    @Override // android.telephony.CellInfo
    public /* bridge */ /* synthetic */ CellSignalStrengthNr getCellSignalStrength() {
        return (CellSignalStrengthNr) getCellSignalStrength();
    }

    public CellInfoNr() {
        this.mCellIdentity = new CellIdentityNr();
        this.mCellSignalStrength = new CellSignalStrengthNr();
    }

    private CellInfoNr(Parcel in) {
        super(in);
        this.mCellIdentity = CellIdentityNr.CREATOR.createFromParcel(in);
        this.mCellSignalStrength = CellSignalStrengthNr.CREATOR.createFromParcel(in);
    }

    private CellInfoNr(CellInfoNr other, boolean sanitizeLocationInfo) {
        super(other);
        this.mCellIdentity = sanitizeLocationInfo ? other.mCellIdentity.sanitizeLocationInfo() : other.mCellIdentity;
        this.mCellSignalStrength = other.mCellSignalStrength;
    }

    public CellInfoNr(int connectionStatus, boolean registered, long timeStamp, CellIdentityNr cellIdentityNr, CellSignalStrengthNr cellSignalStrengthNr) {
        super(connectionStatus, registered, timeStamp);
        this.mCellIdentity = cellIdentityNr;
        this.mCellSignalStrength = cellSignalStrengthNr;
    }

    @Override // android.telephony.CellInfo
    public CellIdentity getCellIdentity() {
        return this.mCellIdentity;
    }

    public void setCellIdentity(CellIdentityNr cid) {
        this.mCellIdentity = cid;
    }

    @Override // android.telephony.CellInfo
    public CellSignalStrength getCellSignalStrength() {
        return this.mCellSignalStrength;
    }

    @Override // android.telephony.CellInfo
    public CellInfo sanitizeLocationInfo() {
        return new CellInfoNr(this, true);
    }

    @Override // android.telephony.CellInfo
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), this.mCellIdentity, this.mCellSignalStrength);
    }

    @Override // android.telephony.CellInfo
    public boolean equals(Object other) {
        if (other instanceof CellInfoNr) {
            CellInfoNr o = (CellInfoNr) other;
            return super.equals(o) && this.mCellIdentity.equals(o.mCellIdentity) && this.mCellSignalStrength.equals(o.mCellSignalStrength);
        }
        return false;
    }

    @Override // android.telephony.CellInfo
    public String toString() {
        return "CellInfoNr:{" + (" " + super.toString()) + (" " + this.mCellIdentity) + (" " + this.mCellSignalStrength) + " }";
    }

    @Override // android.telephony.CellInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags, 6);
        this.mCellIdentity.writeToParcel(dest, flags);
        this.mCellSignalStrength.writeToParcel(dest, flags);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellInfoNr createFromParcelBody(Parcel in) {
        return new CellInfoNr(in);
    }
}
