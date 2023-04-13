package android.service.carrier;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.telephony.Rlog;
import java.util.Objects;
/* loaded from: classes3.dex */
public class CarrierIdentifier implements Parcelable {
    public static final Parcelable.Creator<CarrierIdentifier> CREATOR = new Parcelable.Creator<CarrierIdentifier>() { // from class: android.service.carrier.CarrierIdentifier.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CarrierIdentifier createFromParcel(Parcel parcel) {
            return new CarrierIdentifier(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CarrierIdentifier[] newArray(int i) {
            return new CarrierIdentifier[i];
        }
    };
    private int mCarrierId;
    private String mGid1;
    private String mGid2;
    private String mImsi;
    private String mMcc;
    private String mMnc;
    private int mSpecificCarrierId;
    private String mSpn;

    /* loaded from: classes3.dex */
    public interface MatchType {
        public static final int ALL = 0;
        public static final int GID1 = 3;
        public static final int GID2 = 4;
        public static final int IMSI_PREFIX = 2;
        public static final int SPN = 1;
    }

    public CarrierIdentifier(String mcc, String mnc, String spn, String imsi, String gid1, String gid2) {
        this(mcc, mnc, spn, imsi, gid1, gid2, -1, -1);
    }

    public CarrierIdentifier(String mcc, String mnc, String spn, String imsi, String gid1, String gid2, int carrierid, int specificCarrierId) {
        this.mCarrierId = -1;
        this.mSpecificCarrierId = -1;
        this.mMcc = mcc;
        this.mMnc = mnc;
        this.mSpn = spn;
        this.mImsi = imsi;
        this.mGid1 = gid1;
        this.mGid2 = gid2;
        this.mCarrierId = carrierid;
        this.mSpecificCarrierId = specificCarrierId;
    }

    public CarrierIdentifier(byte[] mccMnc, String gid1, String gid2) {
        this.mCarrierId = -1;
        this.mSpecificCarrierId = -1;
        if (mccMnc.length != 3) {
            throw new IllegalArgumentException("MCC & MNC must be set by a 3-byte array: byte[" + mccMnc.length + NavigationBarInflaterView.SIZE_MOD_END);
        }
        String hex = IccUtils.bytesToHexString(mccMnc);
        this.mMcc = new String(new char[]{hex.charAt(1), hex.charAt(0), hex.charAt(3)});
        if (hex.charAt(2) == 'F') {
            this.mMnc = new String(new char[]{hex.charAt(5), hex.charAt(4)});
        } else {
            this.mMnc = new String(new char[]{hex.charAt(5), hex.charAt(4), hex.charAt(2)});
        }
        this.mGid1 = gid1;
        this.mGid2 = gid2;
        this.mSpn = null;
        this.mImsi = null;
    }

    public CarrierIdentifier(Parcel parcel) {
        this.mCarrierId = -1;
        this.mSpecificCarrierId = -1;
        readFromParcel(parcel);
    }

    public String getMcc() {
        return this.mMcc;
    }

    public String getMnc() {
        return this.mMnc;
    }

    public String getSpn() {
        return this.mSpn;
    }

    public String getImsi() {
        return this.mImsi;
    }

    public String getGid1() {
        return this.mGid1;
    }

    public String getGid2() {
        return this.mGid2;
    }

    public int getCarrierId() {
        return this.mCarrierId;
    }

    public int getSpecificCarrierId() {
        return this.mSpecificCarrierId;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CarrierIdentifier that = (CarrierIdentifier) obj;
        if (Objects.equals(this.mMcc, that.mMcc) && Objects.equals(this.mMnc, that.mMnc) && Objects.equals(this.mSpn, that.mSpn) && Objects.equals(this.mImsi, that.mImsi) && Objects.equals(this.mGid1, that.mGid1) && Objects.equals(this.mGid2, that.mGid2) && Objects.equals(Integer.valueOf(this.mCarrierId), Integer.valueOf(that.mCarrierId)) && Objects.equals(Integer.valueOf(this.mSpecificCarrierId), Integer.valueOf(that.mSpecificCarrierId))) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mMcc, this.mMnc, this.mSpn, this.mImsi, this.mGid1, this.mGid2, Integer.valueOf(this.mCarrierId), Integer.valueOf(this.mSpecificCarrierId));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mMcc);
        out.writeString(this.mMnc);
        out.writeString(this.mSpn);
        out.writeString(this.mImsi);
        out.writeString(this.mGid1);
        out.writeString(this.mGid2);
        out.writeInt(this.mCarrierId);
        out.writeInt(this.mSpecificCarrierId);
    }

    public String toString() {
        return "CarrierIdentifier{mcc=" + this.mMcc + ",mnc=" + this.mMnc + ",spn=" + this.mSpn + ",imsi=" + Rlog.pii(false, (Object) this.mImsi) + ",gid1=" + this.mGid1 + ",gid2=" + this.mGid2 + ",carrierid=" + this.mCarrierId + ",specificCarrierId=" + this.mSpecificCarrierId + "}";
    }

    public void readFromParcel(Parcel in) {
        this.mMcc = in.readString();
        this.mMnc = in.readString();
        this.mSpn = in.readString();
        this.mImsi = in.readString();
        this.mGid1 = in.readString();
        this.mGid2 = in.readString();
        this.mCarrierId = in.readInt();
        this.mSpecificCarrierId = in.readInt();
    }
}
