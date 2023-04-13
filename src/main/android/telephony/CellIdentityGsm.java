package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.ArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes3.dex */
public final class CellIdentityGsm extends CellIdentity {
    private static final boolean DBG = false;
    private static final int MAX_ARFCN = 65535;
    private static final int MAX_BSIC = 63;
    private static final int MAX_CID = 65535;
    private static final int MAX_LAC = 65535;
    private final ArraySet<String> mAdditionalPlmns;
    private final int mArfcn;
    private final int mBsic;
    private final int mCid;
    private final int mLac;
    private static final String TAG = CellIdentityGsm.class.getSimpleName();
    public static final Parcelable.Creator<CellIdentityGsm> CREATOR = new Parcelable.Creator<CellIdentityGsm>() { // from class: android.telephony.CellIdentityGsm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentityGsm createFromParcel(Parcel in) {
            in.readInt();
            return CellIdentityGsm.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentityGsm[] newArray(int size) {
            return new CellIdentityGsm[size];
        }
    };

    public CellIdentityGsm() {
        super(TAG, 1, null, null, null, null);
        this.mLac = Integer.MAX_VALUE;
        this.mCid = Integer.MAX_VALUE;
        this.mArfcn = Integer.MAX_VALUE;
        this.mBsic = Integer.MAX_VALUE;
        this.mAdditionalPlmns = new ArraySet<>();
        this.mGlobalCellId = null;
    }

    public CellIdentityGsm(int lac, int cid, int arfcn, int bsic, String mccStr, String mncStr, String alphal, String alphas, Collection<String> additionalPlmns) {
        super(TAG, 1, mccStr, mncStr, alphal, alphas);
        this.mLac = inRangeOrUnavailable(lac, 0, 65535);
        this.mCid = inRangeOrUnavailable(cid, 0, 65535);
        this.mArfcn = inRangeOrUnavailable(arfcn, 0, 65535);
        this.mBsic = inRangeOrUnavailable(bsic, 0, 63);
        this.mAdditionalPlmns = new ArraySet<>(additionalPlmns.size());
        for (String plmn : additionalPlmns) {
            if (isValidPlmn(plmn)) {
                this.mAdditionalPlmns.add(plmn);
            }
        }
        updateGlobalCellId();
    }

    private CellIdentityGsm(CellIdentityGsm cid) {
        this(cid.mLac, cid.mCid, cid.mArfcn, cid.mBsic, cid.mMccStr, cid.mMncStr, cid.mAlphaLong, cid.mAlphaShort, cid.mAdditionalPlmns);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CellIdentityGsm copy() {
        return new CellIdentityGsm(this);
    }

    @Override // android.telephony.CellIdentity
    public CellIdentityGsm sanitizeLocationInfo() {
        return new CellIdentityGsm(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, this.mMccStr, this.mMncStr, this.mAlphaLong, this.mAlphaShort, this.mAdditionalPlmns);
    }

    @Override // android.telephony.CellIdentity
    protected void updateGlobalCellId() {
        this.mGlobalCellId = null;
        String plmn = getPlmn();
        if (plmn == null || this.mLac == Integer.MAX_VALUE || this.mCid == Integer.MAX_VALUE) {
            return;
        }
        this.mGlobalCellId = plmn + TextUtils.formatSimple("%04x%04x", Integer.valueOf(this.mLac), Integer.valueOf(this.mCid));
    }

    @Deprecated
    public int getMcc() {
        if (this.mMccStr != null) {
            return Integer.valueOf(this.mMccStr).intValue();
        }
        return Integer.MAX_VALUE;
    }

    @Deprecated
    public int getMnc() {
        if (this.mMncStr != null) {
            return Integer.valueOf(this.mMncStr).intValue();
        }
        return Integer.MAX_VALUE;
    }

    public int getLac() {
        return this.mLac;
    }

    public int getCid() {
        return this.mCid;
    }

    public int getArfcn() {
        return this.mArfcn;
    }

    public int getBsic() {
        return this.mBsic;
    }

    public String getMobileNetworkOperator() {
        if (this.mMccStr == null || this.mMncStr == null) {
            return null;
        }
        return this.mMccStr + this.mMncStr;
    }

    @Override // android.telephony.CellIdentity
    public String getMccString() {
        return this.mMccStr;
    }

    @Override // android.telephony.CellIdentity
    public String getMncString() {
        return this.mMncStr;
    }

    @Override // android.telephony.CellIdentity
    public int getChannelNumber() {
        return this.mArfcn;
    }

    public Set<String> getAdditionalPlmns() {
        return Collections.unmodifiableSet(this.mAdditionalPlmns);
    }

    @Deprecated
    public int getPsc() {
        return Integer.MAX_VALUE;
    }

    @Override // android.telephony.CellIdentity
    public GsmCellLocation asCellLocation() {
        GsmCellLocation cl = new GsmCellLocation();
        int lac = this.mLac;
        if (lac == Integer.MAX_VALUE) {
            lac = -1;
        }
        int i = this.mCid;
        if (i == Integer.MAX_VALUE) {
            i = -1;
        }
        int cid = i;
        cl.setLacAndCid(lac, cid);
        cl.setPsc(-1);
        return cl;
    }

    @Override // android.telephony.CellIdentity
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mLac), Integer.valueOf(this.mCid), Integer.valueOf(this.mAdditionalPlmns.hashCode()), Integer.valueOf(super.hashCode()));
    }

    @Override // android.telephony.CellIdentity
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof CellIdentityGsm) {
            CellIdentityGsm o = (CellIdentityGsm) other;
            return this.mLac == o.mLac && this.mCid == o.mCid && this.mArfcn == o.mArfcn && this.mBsic == o.mBsic && TextUtils.equals(this.mMccStr, o.mMccStr) && TextUtils.equals(this.mMncStr, o.mMncStr) && this.mAdditionalPlmns.equals(o.mAdditionalPlmns) && super.equals(other);
        }
        return false;
    }

    public String toString() {
        return TAG + ":{ mLac=" + this.mLac + " mCid=" + this.mCid + " mArfcn=" + this.mArfcn + " mBsic=0x" + Integer.toHexString(this.mBsic) + " mMcc=" + this.mMccStr + " mMnc=" + this.mMncStr + " mAlphaLong=" + this.mAlphaLong + " mAlphaShort=" + this.mAlphaShort + " mAdditionalPlmns=" + this.mAdditionalPlmns + "}";
    }

    @Override // android.telephony.CellIdentity, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, 1);
        dest.writeInt(this.mLac);
        dest.writeInt(this.mCid);
        dest.writeInt(this.mArfcn);
        dest.writeInt(this.mBsic);
        dest.writeArraySet(this.mAdditionalPlmns);
    }

    private CellIdentityGsm(Parcel in) {
        super(TAG, 1, in);
        this.mLac = in.readInt();
        this.mCid = in.readInt();
        this.mArfcn = in.readInt();
        this.mBsic = in.readInt();
        this.mAdditionalPlmns = in.readArraySet(null);
        updateGlobalCellId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellIdentityGsm createFromParcelBody(Parcel in) {
        return new CellIdentityGsm(in);
    }
}
