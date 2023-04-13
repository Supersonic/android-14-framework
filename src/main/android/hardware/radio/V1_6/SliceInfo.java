package android.hardware.radio.V1_6;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class SliceInfo {
    public byte sst = 0;
    public int sliceDifferentiator = 0;
    public byte mappedHplmnSst = 0;
    public int mappedHplmnSD = 0;
    public byte status = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != SliceInfo.class) {
            return false;
        }
        SliceInfo other = (SliceInfo) otherObject;
        if (this.sst == other.sst && this.sliceDifferentiator == other.sliceDifferentiator && this.mappedHplmnSst == other.mappedHplmnSst && this.mappedHplmnSD == other.mappedHplmnSD && this.status == other.status) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.sst))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.sliceDifferentiator))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.mappedHplmnSst))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.mappedHplmnSD))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.status))));
    }

    public final String toString() {
        return "{.sst = " + SliceServiceType.toString(this.sst) + ", .sliceDifferentiator = " + this.sliceDifferentiator + ", .mappedHplmnSst = " + SliceServiceType.toString(this.mappedHplmnSst) + ", .mappedHplmnSD = " + this.mappedHplmnSD + ", .status = " + SliceStatus.toString(this.status) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(20L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<SliceInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<SliceInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 20, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SliceInfo _hidl_vec_element = new SliceInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 20);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.sst = _hidl_blob.getInt8(0 + _hidl_offset);
        this.sliceDifferentiator = _hidl_blob.getInt32(4 + _hidl_offset);
        this.mappedHplmnSst = _hidl_blob.getInt8(8 + _hidl_offset);
        this.mappedHplmnSD = _hidl_blob.getInt32(12 + _hidl_offset);
        this.status = _hidl_blob.getInt8(16 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(20);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<SliceInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 20);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 20);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt8(0 + _hidl_offset, this.sst);
        _hidl_blob.putInt32(4 + _hidl_offset, this.sliceDifferentiator);
        _hidl_blob.putInt8(8 + _hidl_offset, this.mappedHplmnSst);
        _hidl_blob.putInt32(12 + _hidl_offset, this.mappedHplmnSD);
        _hidl_blob.putInt8(16 + _hidl_offset, this.status);
    }
}
