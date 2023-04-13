package android.hardware.radio.V1_4;

import android.hardware.radio.V1_2.CellIdentityOperatorNames;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CellIdentityNr {
    public String mcc = new String();
    public String mnc = new String();
    public long nci = 0;
    public int pci = 0;
    public int tac = 0;
    public int nrarfcn = 0;
    public CellIdentityOperatorNames operatorNames = new CellIdentityOperatorNames();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CellIdentityNr.class) {
            return false;
        }
        CellIdentityNr other = (CellIdentityNr) otherObject;
        if (HidlSupport.deepEquals(this.mcc, other.mcc) && HidlSupport.deepEquals(this.mnc, other.mnc) && this.nci == other.nci && this.pci == other.pci && this.tac == other.tac && this.nrarfcn == other.nrarfcn && HidlSupport.deepEquals(this.operatorNames, other.operatorNames)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.mcc)), Integer.valueOf(HidlSupport.deepHashCode(this.mnc)), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.nci))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.pci))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.tac))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.nrarfcn))), Integer.valueOf(HidlSupport.deepHashCode(this.operatorNames)));
    }

    public final String toString() {
        return "{.mcc = " + this.mcc + ", .mnc = " + this.mnc + ", .nci = " + this.nci + ", .pci = " + this.pci + ", .tac = " + this.tac + ", .nrarfcn = " + this.nrarfcn + ", .operatorNames = " + this.operatorNames + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(88L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CellIdentityNr> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CellIdentityNr> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 88, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CellIdentityNr _hidl_vec_element = new CellIdentityNr();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 88);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        String string = _hidl_blob.getString(_hidl_offset + 0);
        this.mcc = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 16);
        this.mnc = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        this.nci = _hidl_blob.getInt64(_hidl_offset + 32);
        this.pci = _hidl_blob.getInt32(_hidl_offset + 40);
        this.tac = _hidl_blob.getInt32(_hidl_offset + 44);
        this.nrarfcn = _hidl_blob.getInt32(_hidl_offset + 48);
        this.operatorNames.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 56);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(88);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CellIdentityNr> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 88);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 88);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(0 + _hidl_offset, this.mcc);
        _hidl_blob.putString(16 + _hidl_offset, this.mnc);
        _hidl_blob.putInt64(32 + _hidl_offset, this.nci);
        _hidl_blob.putInt32(40 + _hidl_offset, this.pci);
        _hidl_blob.putInt32(44 + _hidl_offset, this.tac);
        _hidl_blob.putInt32(48 + _hidl_offset, this.nrarfcn);
        this.operatorNames.writeEmbeddedToBlob(_hidl_blob, 56 + _hidl_offset);
    }
}
