package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CdmaBroadcastSmsConfigInfo {
    public int serviceCategory = 0;
    public int language = 0;
    public boolean selected = false;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CdmaBroadcastSmsConfigInfo.class) {
            return false;
        }
        CdmaBroadcastSmsConfigInfo other = (CdmaBroadcastSmsConfigInfo) otherObject;
        if (this.serviceCategory == other.serviceCategory && this.language == other.language && this.selected == other.selected) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.serviceCategory))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.language))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.selected))));
    }

    public final String toString() {
        return "{.serviceCategory = " + this.serviceCategory + ", .language = " + this.language + ", .selected = " + this.selected + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(12L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CdmaBroadcastSmsConfigInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CdmaBroadcastSmsConfigInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 12, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CdmaBroadcastSmsConfigInfo _hidl_vec_element = new CdmaBroadcastSmsConfigInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 12);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.serviceCategory = _hidl_blob.getInt32(0 + _hidl_offset);
        this.language = _hidl_blob.getInt32(4 + _hidl_offset);
        this.selected = _hidl_blob.getBool(8 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(12);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CdmaBroadcastSmsConfigInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 12);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 12);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.serviceCategory);
        _hidl_blob.putInt32(4 + _hidl_offset, this.language);
        _hidl_blob.putBool(8 + _hidl_offset, this.selected);
    }
}
