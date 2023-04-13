package android.hardware.radio.V1_6;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class NrVopsInfo {
    public byte vopsSupported = 0;
    public byte emcSupported = 0;
    public byte emfSupported = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != NrVopsInfo.class) {
            return false;
        }
        NrVopsInfo other = (NrVopsInfo) otherObject;
        if (this.vopsSupported == other.vopsSupported && this.emcSupported == other.emcSupported && this.emfSupported == other.emfSupported) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.vopsSupported))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.emcSupported))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.emfSupported))));
    }

    public final String toString() {
        return "{.vopsSupported = " + VopsIndicator.toString(this.vopsSupported) + ", .emcSupported = " + EmcIndicator.toString(this.emcSupported) + ", .emfSupported = " + EmfIndicator.toString(this.emfSupported) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(3L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<NrVopsInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<NrVopsInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 3, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            NrVopsInfo _hidl_vec_element = new NrVopsInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 3);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.vopsSupported = _hidl_blob.getInt8(0 + _hidl_offset);
        this.emcSupported = _hidl_blob.getInt8(1 + _hidl_offset);
        this.emfSupported = _hidl_blob.getInt8(2 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(3);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<NrVopsInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 3);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 3);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt8(0 + _hidl_offset, this.vopsSupported);
        _hidl_blob.putInt8(1 + _hidl_offset, this.emcSupported);
        _hidl_blob.putInt8(2 + _hidl_offset, this.emfSupported);
    }
}
