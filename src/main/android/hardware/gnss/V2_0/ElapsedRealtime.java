package android.hardware.gnss.V2_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class ElapsedRealtime {
    public short flags;
    public long timestampNs = 0;
    public long timeUncertaintyNs = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != ElapsedRealtime.class) {
            return false;
        }
        ElapsedRealtime other = (ElapsedRealtime) otherObject;
        if (HidlSupport.deepEquals(Short.valueOf(this.flags), Short.valueOf(other.flags)) && this.timestampNs == other.timestampNs && this.timeUncertaintyNs == other.timeUncertaintyNs) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.flags))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.timestampNs))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.timeUncertaintyNs))));
    }

    public final String toString() {
        return "{.flags = " + ElapsedRealtimeFlags.dumpBitfield(this.flags) + ", .timestampNs = " + this.timestampNs + ", .timeUncertaintyNs = " + this.timeUncertaintyNs + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(24L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<ElapsedRealtime> readVectorFromParcel(HwParcel parcel) {
        ArrayList<ElapsedRealtime> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ElapsedRealtime _hidl_vec_element = new ElapsedRealtime();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.flags = _hidl_blob.getInt16(0 + _hidl_offset);
        this.timestampNs = _hidl_blob.getInt64(8 + _hidl_offset);
        this.timeUncertaintyNs = _hidl_blob.getInt64(16 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(24);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<ElapsedRealtime> _hidl_vec) {
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
        _hidl_blob.putInt16(0 + _hidl_offset, this.flags);
        _hidl_blob.putInt64(8 + _hidl_offset, this.timestampNs);
        _hidl_blob.putInt64(16 + _hidl_offset, this.timeUncertaintyNs);
    }
}
