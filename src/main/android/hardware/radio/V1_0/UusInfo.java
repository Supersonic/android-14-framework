package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class UusInfo {
    public int uusType = 0;
    public int uusDcs = 0;
    public String uusData = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != UusInfo.class) {
            return false;
        }
        UusInfo other = (UusInfo) otherObject;
        if (this.uusType == other.uusType && this.uusDcs == other.uusDcs && HidlSupport.deepEquals(this.uusData, other.uusData)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.uusType))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.uusDcs))), Integer.valueOf(HidlSupport.deepHashCode(this.uusData)));
    }

    public final String toString() {
        return "{.uusType = " + UusType.toString(this.uusType) + ", .uusDcs = " + UusDcs.toString(this.uusDcs) + ", .uusData = " + this.uusData + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(24L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<UusInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<UusInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            UusInfo _hidl_vec_element = new UusInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.uusType = _hidl_blob.getInt32(_hidl_offset + 0);
        this.uusDcs = _hidl_blob.getInt32(_hidl_offset + 4);
        String string = _hidl_blob.getString(_hidl_offset + 8);
        this.uusData = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(24);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<UusInfo> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.uusType);
        _hidl_blob.putInt32(4 + _hidl_offset, this.uusDcs);
        _hidl_blob.putString(8 + _hidl_offset, this.uusData);
    }
}
