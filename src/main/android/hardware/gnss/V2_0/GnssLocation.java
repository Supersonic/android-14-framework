package android.hardware.gnss.V2_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class GnssLocation {
    public android.hardware.gnss.V1_0.GnssLocation v1_0 = new android.hardware.gnss.V1_0.GnssLocation();
    public ElapsedRealtime elapsedRealtime = new ElapsedRealtime();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != GnssLocation.class) {
            return false;
        }
        GnssLocation other = (GnssLocation) otherObject;
        if (HidlSupport.deepEquals(this.v1_0, other.v1_0) && HidlSupport.deepEquals(this.elapsedRealtime, other.elapsedRealtime)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.v1_0)), Integer.valueOf(HidlSupport.deepHashCode(this.elapsedRealtime)));
    }

    public final String toString() {
        return "{.v1_0 = " + this.v1_0 + ", .elapsedRealtime = " + this.elapsedRealtime + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(88L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<GnssLocation> readVectorFromParcel(HwParcel parcel) {
        ArrayList<GnssLocation> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 88, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            GnssLocation _hidl_vec_element = new GnssLocation();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 88);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.v1_0.readEmbeddedFromParcel(parcel, _hidl_blob, 0 + _hidl_offset);
        this.elapsedRealtime.readEmbeddedFromParcel(parcel, _hidl_blob, 64 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(88);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssLocation> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 88);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 88);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        this.v1_0.writeEmbeddedToBlob(_hidl_blob, 0 + _hidl_offset);
        this.elapsedRealtime.writeEmbeddedToBlob(_hidl_blob, 64 + _hidl_offset);
    }
}
