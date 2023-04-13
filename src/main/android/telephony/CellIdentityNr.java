package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.ArraySet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes3.dex */
public final class CellIdentityNr extends CellIdentity {
    public static final Parcelable.Creator<CellIdentityNr> CREATOR = new Parcelable.Creator<CellIdentityNr>() { // from class: android.telephony.CellIdentityNr.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentityNr createFromParcel(Parcel in) {
            in.readInt();
            return CellIdentityNr.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentityNr[] newArray(int size) {
            return new CellIdentityNr[size];
        }
    };
    private static final long MAX_NCI = 68719476735L;
    private static final int MAX_NRARFCN = 3279165;
    private static final int MAX_PCI = 1007;
    private static final int MAX_TAC = 16777215;
    private static final String TAG = "CellIdentityNr";
    private final ArraySet<String> mAdditionalPlmns;
    private final int[] mBands;
    private final long mNci;
    private final int mNrArfcn;
    private final int mPci;
    private final int mTac;

    public CellIdentityNr() {
        super(TAG, 6, null, null, null, null);
        this.mNrArfcn = Integer.MAX_VALUE;
        this.mPci = Integer.MAX_VALUE;
        this.mTac = Integer.MAX_VALUE;
        this.mNci = 2147483647L;
        this.mBands = new int[0];
        this.mAdditionalPlmns = new ArraySet<>();
        this.mGlobalCellId = null;
    }

    public CellIdentityNr(int pci, int tac, int nrArfcn, int[] bands, String mccStr, String mncStr, long nci, String alphal, String alphas, Collection<String> additionalPlmns) {
        super(TAG, 6, mccStr, mncStr, alphal, alphas);
        this.mPci = inRangeOrUnavailable(pci, 0, 1007);
        this.mTac = inRangeOrUnavailable(tac, 0, 16777215);
        this.mNrArfcn = inRangeOrUnavailable(nrArfcn, 0, (int) MAX_NRARFCN);
        this.mBands = bands;
        this.mNci = inRangeOrUnavailable(nci, 0L, (long) MAX_NCI);
        this.mAdditionalPlmns = new ArraySet<>(additionalPlmns.size());
        for (String plmn : additionalPlmns) {
            if (isValidPlmn(plmn)) {
                this.mAdditionalPlmns.add(plmn);
            }
        }
        updateGlobalCellId();
    }

    @Override // android.telephony.CellIdentity
    public CellIdentityNr sanitizeLocationInfo() {
        return new CellIdentityNr(Integer.MAX_VALUE, Integer.MAX_VALUE, this.mNrArfcn, this.mBands, this.mMccStr, this.mMncStr, Long.MAX_VALUE, this.mAlphaLong, this.mAlphaShort, this.mAdditionalPlmns);
    }

    @Override // android.telephony.CellIdentity
    protected void updateGlobalCellId() {
        this.mGlobalCellId = null;
        String plmn = getPlmn();
        if (plmn == null || this.mNci == Long.MAX_VALUE) {
            return;
        }
        this.mGlobalCellId = plmn + TextUtils.formatSimple("%09x", Long.valueOf(this.mNci));
    }

    @Override // android.telephony.CellIdentity
    public CellLocation asCellLocation() {
        GsmCellLocation cl = new GsmCellLocation();
        int tac = this.mTac;
        if (tac == Integer.MAX_VALUE) {
            tac = -1;
        }
        cl.setLacAndCid(tac, -1);
        cl.setPsc(0);
        return cl;
    }

    @Override // android.telephony.CellIdentity
    public int hashCode() {
        return Objects.hash(Integer.valueOf(super.hashCode()), Integer.valueOf(this.mPci), Integer.valueOf(this.mTac), Integer.valueOf(this.mNrArfcn), Integer.valueOf(Arrays.hashCode(this.mBands)), Long.valueOf(this.mNci), Integer.valueOf(this.mAdditionalPlmns.hashCode()));
    }

    @Override // android.telephony.CellIdentity
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof CellIdentityNr) {
            CellIdentityNr o = (CellIdentityNr) other;
            return super.equals(o) && this.mPci == o.mPci && this.mTac == o.mTac && this.mNrArfcn == o.mNrArfcn && Arrays.equals(this.mBands, o.mBands) && this.mNci == o.mNci && this.mAdditionalPlmns.equals(o.mAdditionalPlmns);
        }
        return false;
    }

    public long getNci() {
        return this.mNci;
    }

    public int getNrarfcn() {
        return this.mNrArfcn;
    }

    public int[] getBands() {
        int[] iArr = this.mBands;
        return Arrays.copyOf(iArr, iArr.length);
    }

    public int getPci() {
        return this.mPci;
    }

    public int getTac() {
        return this.mTac;
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
        return this.mNrArfcn;
    }

    public Set<String> getAdditionalPlmns() {
        return Collections.unmodifiableSet(this.mAdditionalPlmns);
    }

    public String toString() {
        return "CellIdentityNr:{ mPci = " + this.mPci + " mTac = " + this.mTac + " mNrArfcn = " + this.mNrArfcn + " mBands = " + Arrays.toString(this.mBands) + " mMcc = " + this.mMccStr + " mMnc = " + this.mMncStr + " mNci = " + this.mNci + " mAlphaLong = " + this.mAlphaLong + " mAlphaShort = " + this.mAlphaShort + " mAdditionalPlmns = " + this.mAdditionalPlmns + " }";
    }

    @Override // android.telephony.CellIdentity, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int type) {
        super.writeToParcel(dest, 6);
        dest.writeInt(this.mPci);
        dest.writeInt(this.mTac);
        dest.writeInt(this.mNrArfcn);
        dest.writeIntArray(this.mBands);
        dest.writeLong(this.mNci);
        dest.writeArraySet(this.mAdditionalPlmns);
    }

    private CellIdentityNr(Parcel in) {
        super(TAG, 6, in);
        this.mPci = in.readInt();
        this.mTac = in.readInt();
        this.mNrArfcn = in.readInt();
        this.mBands = in.createIntArray();
        this.mNci = in.readLong();
        this.mAdditionalPlmns = in.readArraySet(null);
        updateGlobalCellId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellIdentityNr createFromParcelBody(Parcel in) {
        return new CellIdentityNr(in);
    }
}
