package android.telephony.gsm;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.provider.Telephony;
import android.telephony.CellLocation;
@Deprecated
/* loaded from: classes3.dex */
public class GsmCellLocation extends CellLocation {
    private int mCid;
    private int mLac;
    private int mPsc;

    public GsmCellLocation() {
        this.mLac = -1;
        this.mCid = -1;
        this.mPsc = -1;
    }

    public GsmCellLocation(Bundle bundle) {
        this.mLac = bundle.getInt(Telephony.CellBroadcasts.LAC, -1);
        this.mCid = bundle.getInt("cid", -1);
        this.mPsc = bundle.getInt("psc", -1);
    }

    public int getLac() {
        return this.mLac;
    }

    public int getCid() {
        return this.mCid;
    }

    public int getPsc() {
        return this.mPsc;
    }

    @Override // android.telephony.CellLocation
    public void setStateInvalid() {
        this.mLac = -1;
        this.mCid = -1;
        this.mPsc = -1;
    }

    public void setLacAndCid(int lac, int cid) {
        this.mLac = lac;
        this.mCid = cid;
    }

    public void setPsc(int psc) {
        this.mPsc = psc;
    }

    public int hashCode() {
        return this.mLac ^ this.mCid;
    }

    public boolean equals(Object o) {
        try {
            GsmCellLocation s = (GsmCellLocation) o;
            return o != null && equalsHandlesNulls(Integer.valueOf(this.mLac), Integer.valueOf(s.mLac)) && equalsHandlesNulls(Integer.valueOf(this.mCid), Integer.valueOf(s.mCid)) && equalsHandlesNulls(Integer.valueOf(this.mPsc), Integer.valueOf(s.mPsc));
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return NavigationBarInflaterView.SIZE_MOD_START + this.mLac + "," + this.mCid + "," + this.mPsc + NavigationBarInflaterView.SIZE_MOD_END;
    }

    private static boolean equalsHandlesNulls(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override // android.telephony.CellLocation
    public void fillInNotifierBundle(Bundle m) {
        m.putInt(Telephony.CellBroadcasts.LAC, this.mLac);
        m.putInt("cid", this.mCid);
        m.putInt("psc", this.mPsc);
    }

    @Override // android.telephony.CellLocation
    public boolean isEmpty() {
        return this.mLac == -1 && this.mCid == -1 && this.mPsc == -1;
    }
}
