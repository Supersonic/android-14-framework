package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class GsmBroadcastSmsConfigInfo {
    public int fromServiceId = 0;
    public int toServiceId = 0;
    public int fromCodeScheme = 0;
    public int toCodeScheme = 0;
    public boolean selected = false;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != GsmBroadcastSmsConfigInfo.class) {
            return false;
        }
        GsmBroadcastSmsConfigInfo other = (GsmBroadcastSmsConfigInfo) otherObject;
        if (this.fromServiceId == other.fromServiceId && this.toServiceId == other.toServiceId && this.fromCodeScheme == other.fromCodeScheme && this.toCodeScheme == other.toCodeScheme && this.selected == other.selected) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.fromServiceId))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.toServiceId))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.fromCodeScheme))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.toCodeScheme))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.selected))));
    }

    public final String toString() {
        return "{.fromServiceId = " + this.fromServiceId + ", .toServiceId = " + this.toServiceId + ", .fromCodeScheme = " + this.fromCodeScheme + ", .toCodeScheme = " + this.toCodeScheme + ", .selected = " + this.selected + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(20L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<GsmBroadcastSmsConfigInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<GsmBroadcastSmsConfigInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 20, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            GsmBroadcastSmsConfigInfo _hidl_vec_element = new GsmBroadcastSmsConfigInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 20);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.fromServiceId = _hidl_blob.getInt32(0 + _hidl_offset);
        this.toServiceId = _hidl_blob.getInt32(4 + _hidl_offset);
        this.fromCodeScheme = _hidl_blob.getInt32(8 + _hidl_offset);
        this.toCodeScheme = _hidl_blob.getInt32(12 + _hidl_offset);
        this.selected = _hidl_blob.getBool(16 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(20);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GsmBroadcastSmsConfigInfo> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.fromServiceId);
        _hidl_blob.putInt32(4 + _hidl_offset, this.toServiceId);
        _hidl_blob.putInt32(8 + _hidl_offset, this.fromCodeScheme);
        _hidl_blob.putInt32(12 + _hidl_offset, this.toCodeScheme);
        _hidl_blob.putBool(16 + _hidl_offset, this.selected);
    }
}
