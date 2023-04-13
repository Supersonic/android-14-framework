package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CdmaSmsAddress {
    public int digitMode = 0;
    public int numberMode = 0;
    public int numberType = 0;
    public int numberPlan = 0;
    public ArrayList<Byte> digits = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CdmaSmsAddress.class) {
            return false;
        }
        CdmaSmsAddress other = (CdmaSmsAddress) otherObject;
        if (this.digitMode == other.digitMode && this.numberMode == other.numberMode && this.numberType == other.numberType && this.numberPlan == other.numberPlan && HidlSupport.deepEquals(this.digits, other.digits)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.digitMode))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.numberMode))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.numberType))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.numberPlan))), Integer.valueOf(HidlSupport.deepHashCode(this.digits)));
    }

    public final String toString() {
        return "{.digitMode = " + CdmaSmsDigitMode.toString(this.digitMode) + ", .numberMode = " + CdmaSmsNumberMode.toString(this.numberMode) + ", .numberType = " + CdmaSmsNumberType.toString(this.numberType) + ", .numberPlan = " + CdmaSmsNumberPlan.toString(this.numberPlan) + ", .digits = " + this.digits + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(32L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CdmaSmsAddress> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CdmaSmsAddress> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 32, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CdmaSmsAddress _hidl_vec_element = new CdmaSmsAddress();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 32);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.digitMode = _hidl_blob.getInt32(_hidl_offset + 0);
        this.numberMode = _hidl_blob.getInt32(_hidl_offset + 4);
        this.numberType = _hidl_blob.getInt32(_hidl_offset + 8);
        this.numberPlan = _hidl_blob.getInt32(_hidl_offset + 12);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 16 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, true);
        this.digits.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            byte _hidl_vec_element = childBlob.getInt8(_hidl_index_0 * 1);
            this.digits.add(Byte.valueOf(_hidl_vec_element));
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(32);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CdmaSmsAddress> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 32);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 32);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(_hidl_offset + 0, this.digitMode);
        _hidl_blob.putInt32(4 + _hidl_offset, this.numberMode);
        _hidl_blob.putInt32(_hidl_offset + 8, this.numberType);
        _hidl_blob.putInt32(_hidl_offset + 12, this.numberPlan);
        int _hidl_vec_size = this.digits.size();
        _hidl_blob.putInt32(_hidl_offset + 16 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 16 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 1);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt8(_hidl_index_0 * 1, this.digits.get(_hidl_index_0).byteValue());
        }
        _hidl_blob.putBlob(16 + _hidl_offset + 0, childBlob);
    }
}
