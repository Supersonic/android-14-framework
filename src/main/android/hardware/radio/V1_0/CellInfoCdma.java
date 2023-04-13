package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CellInfoCdma {
    public CellIdentityCdma cellIdentityCdma = new CellIdentityCdma();
    public CdmaSignalStrength signalStrengthCdma = new CdmaSignalStrength();
    public EvdoSignalStrength signalStrengthEvdo = new EvdoSignalStrength();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CellInfoCdma.class) {
            return false;
        }
        CellInfoCdma other = (CellInfoCdma) otherObject;
        if (HidlSupport.deepEquals(this.cellIdentityCdma, other.cellIdentityCdma) && HidlSupport.deepEquals(this.signalStrengthCdma, other.signalStrengthCdma) && HidlSupport.deepEquals(this.signalStrengthEvdo, other.signalStrengthEvdo)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.cellIdentityCdma)), Integer.valueOf(HidlSupport.deepHashCode(this.signalStrengthCdma)), Integer.valueOf(HidlSupport.deepHashCode(this.signalStrengthEvdo)));
    }

    public final String toString() {
        return "{.cellIdentityCdma = " + this.cellIdentityCdma + ", .signalStrengthCdma = " + this.signalStrengthCdma + ", .signalStrengthEvdo = " + this.signalStrengthEvdo + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(40L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CellInfoCdma> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CellInfoCdma> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CellInfoCdma _hidl_vec_element = new CellInfoCdma();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.cellIdentityCdma.readEmbeddedFromParcel(parcel, _hidl_blob, 0 + _hidl_offset);
        this.signalStrengthCdma.readEmbeddedFromParcel(parcel, _hidl_blob, 20 + _hidl_offset);
        this.signalStrengthEvdo.readEmbeddedFromParcel(parcel, _hidl_blob, 28 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(40);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CellInfoCdma> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 40);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 40);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        this.cellIdentityCdma.writeEmbeddedToBlob(_hidl_blob, 0 + _hidl_offset);
        this.signalStrengthCdma.writeEmbeddedToBlob(_hidl_blob, 20 + _hidl_offset);
        this.signalStrengthEvdo.writeEmbeddedToBlob(_hidl_blob, 28 + _hidl_offset);
    }
}
