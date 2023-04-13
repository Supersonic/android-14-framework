package android.hardware.contexthub.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class MemRange {
    public int flags;
    public int totalBytes = 0;
    public int freeBytes = 0;
    public int type = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != MemRange.class) {
            return false;
        }
        MemRange other = (MemRange) otherObject;
        if (this.totalBytes == other.totalBytes && this.freeBytes == other.freeBytes && this.type == other.type && HidlSupport.deepEquals(Integer.valueOf(this.flags), Integer.valueOf(other.flags))) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.totalBytes))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.freeBytes))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.flags))));
    }

    public final String toString() {
        return "{.totalBytes = " + this.totalBytes + ", .freeBytes = " + this.freeBytes + ", .type = " + HubMemoryType.toString(this.type) + ", .flags = " + HubMemoryFlag.dumpBitfield(this.flags) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(16L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<MemRange> readVectorFromParcel(HwParcel parcel) {
        ArrayList<MemRange> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            MemRange _hidl_vec_element = new MemRange();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 16);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.totalBytes = _hidl_blob.getInt32(0 + _hidl_offset);
        this.freeBytes = _hidl_blob.getInt32(4 + _hidl_offset);
        this.type = _hidl_blob.getInt32(8 + _hidl_offset);
        this.flags = _hidl_blob.getInt32(12 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(16);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<MemRange> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 16);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 16);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.totalBytes);
        _hidl_blob.putInt32(4 + _hidl_offset, this.freeBytes);
        _hidl_blob.putInt32(8 + _hidl_offset, this.type);
        _hidl_blob.putInt32(12 + _hidl_offset, this.flags);
    }
}
