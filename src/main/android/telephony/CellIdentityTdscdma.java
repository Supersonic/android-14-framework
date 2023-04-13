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
public final class CellIdentityTdscdma extends CellIdentity {
    private static final boolean DBG = false;
    private static final int MAX_CID = 268435455;
    private static final int MAX_CPID = 127;
    private static final int MAX_LAC = 65535;
    private static final int MAX_UARFCN = 65535;
    private final ArraySet<String> mAdditionalPlmns;
    private final int mCid;
    private final int mCpid;
    private ClosedSubscriberGroupInfo mCsgInfo;
    private final int mLac;
    private final int mUarfcn;
    private static final String TAG = CellIdentityTdscdma.class.getSimpleName();
    public static final Parcelable.Creator<CellIdentityTdscdma> CREATOR = new Parcelable.Creator<CellIdentityTdscdma>() { // from class: android.telephony.CellIdentityTdscdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentityTdscdma createFromParcel(Parcel in) {
            in.readInt();
            return CellIdentityTdscdma.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentityTdscdma[] newArray(int size) {
            return new CellIdentityTdscdma[size];
        }
    };

    public CellIdentityTdscdma() {
        super(TAG, 5, null, null, null, null);
        this.mLac = Integer.MAX_VALUE;
        this.mCid = Integer.MAX_VALUE;
        this.mCpid = Integer.MAX_VALUE;
        this.mUarfcn = Integer.MAX_VALUE;
        this.mAdditionalPlmns = new ArraySet<>();
        this.mCsgInfo = null;
        this.mGlobalCellId = null;
    }

    public CellIdentityTdscdma(String mcc, String mnc, int lac, int cid, int cpid, int uarfcn, String alphal, String alphas, Collection<String> additionalPlmns, ClosedSubscriberGroupInfo csgInfo) {
        super(TAG, 5, mcc, mnc, alphal, alphas);
        this.mLac = inRangeOrUnavailable(lac, 0, 65535);
        this.mCid = inRangeOrUnavailable(cid, 0, (int) MAX_CID);
        this.mCpid = inRangeOrUnavailable(cpid, 0, 127);
        this.mUarfcn = inRangeOrUnavailable(uarfcn, 0, 65535);
        this.mAdditionalPlmns = new ArraySet<>(additionalPlmns.size());
        for (String plmn : additionalPlmns) {
            if (isValidPlmn(plmn)) {
                this.mAdditionalPlmns.add(plmn);
            }
        }
        this.mCsgInfo = csgInfo;
        updateGlobalCellId();
    }

    private CellIdentityTdscdma(CellIdentityTdscdma cid) {
        this(cid.mMccStr, cid.mMncStr, cid.mLac, cid.mCid, cid.mCpid, cid.mUarfcn, cid.mAlphaLong, cid.mAlphaShort, cid.mAdditionalPlmns, cid.mCsgInfo);
    }

    @Override // android.telephony.CellIdentity
    public CellIdentityTdscdma sanitizeLocationInfo() {
        return new CellIdentityTdscdma(this.mMccStr, this.mMncStr, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, this.mAlphaLong, this.mAlphaShort, this.mAdditionalPlmns, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CellIdentityTdscdma copy() {
        return new CellIdentityTdscdma(this);
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

    @Override // android.telephony.CellIdentity
    public String getMccString() {
        return this.mMccStr;
    }

    @Override // android.telephony.CellIdentity
    public String getMncString() {
        return this.mMncStr;
    }

    public String getMobileNetworkOperator() {
        if (this.mMccStr == null || this.mMncStr == null) {
            return null;
        }
        return this.mMccStr + this.mMncStr;
    }

    public int getLac() {
        return this.mLac;
    }

    public int getCid() {
        return this.mCid;
    }

    public int getCpid() {
        return this.mCpid;
    }

    public int getUarfcn() {
        return this.mUarfcn;
    }

    @Override // android.telephony.CellIdentity
    public int getChannelNumber() {
        return this.mUarfcn;
    }

    public Set<String> getAdditionalPlmns() {
        return Collections.unmodifiableSet(this.mAdditionalPlmns);
    }

    public ClosedSubscriberGroupInfo getClosedSubscriberGroupInfo() {
        return this.mCsgInfo;
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
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof CellIdentityTdscdma) {
            CellIdentityTdscdma o = (CellIdentityTdscdma) other;
            return this.mLac == o.mLac && this.mCid == o.mCid && this.mCpid == o.mCpid && this.mUarfcn == o.mUarfcn && this.mAdditionalPlmns.equals(o.mAdditionalPlmns) && Objects.equals(this.mCsgInfo, o.mCsgInfo) && super.equals(other);
        }
        return false;
    }

    @Override // android.telephony.CellIdentity
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mLac), Integer.valueOf(this.mCid), Integer.valueOf(this.mCpid), Integer.valueOf(this.mUarfcn), Integer.valueOf(this.mAdditionalPlmns.hashCode()), this.mCsgInfo, Integer.valueOf(super.hashCode()));
    }

    public String toString() {
        return TAG + ":{ mMcc=" + this.mMccStr + " mMnc=" + this.mMncStr + " mAlphaLong=" + this.mAlphaLong + " mAlphaShort=" + this.mAlphaShort + " mLac=" + this.mLac + " mCid=" + this.mCid + " mCpid=" + this.mCpid + " mUarfcn=" + this.mUarfcn + " mAdditionalPlmns=" + this.mAdditionalPlmns + " mCsgInfo=" + this.mCsgInfo + "}";
    }

    @Override // android.telephony.CellIdentity, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.CellIdentity, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, 5);
        dest.writeInt(this.mLac);
        dest.writeInt(this.mCid);
        dest.writeInt(this.mCpid);
        dest.writeInt(this.mUarfcn);
        dest.writeArraySet(this.mAdditionalPlmns);
        dest.writeParcelable(this.mCsgInfo, flags);
    }

    private CellIdentityTdscdma(Parcel in) {
        super(TAG, 5, in);
        this.mLac = in.readInt();
        this.mCid = in.readInt();
        this.mCpid = in.readInt();
        this.mUarfcn = in.readInt();
        this.mAdditionalPlmns = in.readArraySet(null);
        this.mCsgInfo = (ClosedSubscriberGroupInfo) in.readParcelable(null, ClosedSubscriberGroupInfo.class);
        updateGlobalCellId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellIdentityTdscdma createFromParcelBody(Parcel in) {
        return new CellIdentityTdscdma(in);
    }
}
