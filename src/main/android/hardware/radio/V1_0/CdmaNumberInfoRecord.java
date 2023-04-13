package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CdmaNumberInfoRecord {
    public String number = new String();
    public byte numberType = 0;
    public byte numberPlan = 0;

    /* renamed from: pi */
    public byte f161pi = 0;

    /* renamed from: si */
    public byte f162si = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CdmaNumberInfoRecord.class) {
            return false;
        }
        CdmaNumberInfoRecord other = (CdmaNumberInfoRecord) otherObject;
        if (HidlSupport.deepEquals(this.number, other.number) && this.numberType == other.numberType && this.numberPlan == other.numberPlan && this.f161pi == other.f161pi && this.f162si == other.f162si) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.number)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.numberType))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.numberPlan))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.f161pi))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.f162si))));
    }

    public final String toString() {
        return "{.number = " + this.number + ", .numberType = " + ((int) this.numberType) + ", .numberPlan = " + ((int) this.numberPlan) + ", .pi = " + ((int) this.f161pi) + ", .si = " + ((int) this.f162si) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(24L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CdmaNumberInfoRecord> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CdmaNumberInfoRecord> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CdmaNumberInfoRecord _hidl_vec_element = new CdmaNumberInfoRecord();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        String string = _hidl_blob.getString(_hidl_offset + 0);
        this.number = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        this.numberType = _hidl_blob.getInt8(16 + _hidl_offset);
        this.numberPlan = _hidl_blob.getInt8(17 + _hidl_offset);
        this.f161pi = _hidl_blob.getInt8(18 + _hidl_offset);
        this.f162si = _hidl_blob.getInt8(19 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(24);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CdmaNumberInfoRecord> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 24);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 24);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(0 + _hidl_offset, this.number);
        _hidl_blob.putInt8(16 + _hidl_offset, this.numberType);
        _hidl_blob.putInt8(17 + _hidl_offset, this.numberPlan);
        _hidl_blob.putInt8(18 + _hidl_offset, this.f161pi);
        _hidl_blob.putInt8(19 + _hidl_offset, this.f162si);
    }
}
