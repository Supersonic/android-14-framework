package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class LteSignalStrength {
    public int signalStrength = 0;
    public int rsrp = 0;
    public int rsrq = 0;
    public int rssnr = 0;
    public int cqi = 0;
    public int timingAdvance = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != LteSignalStrength.class) {
            return false;
        }
        LteSignalStrength other = (LteSignalStrength) otherObject;
        if (this.signalStrength == other.signalStrength && this.rsrp == other.rsrp && this.rsrq == other.rsrq && this.rssnr == other.rssnr && this.cqi == other.cqi && this.timingAdvance == other.timingAdvance) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.signalStrength))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.rsrp))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.rsrq))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.rssnr))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cqi))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.timingAdvance))));
    }

    public final String toString() {
        return "{.signalStrength = " + this.signalStrength + ", .rsrp = " + this.rsrp + ", .rsrq = " + this.rsrq + ", .rssnr = " + this.rssnr + ", .cqi = " + this.cqi + ", .timingAdvance = " + this.timingAdvance + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(24L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<LteSignalStrength> readVectorFromParcel(HwParcel parcel) {
        ArrayList<LteSignalStrength> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            LteSignalStrength _hidl_vec_element = new LteSignalStrength();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.signalStrength = _hidl_blob.getInt32(0 + _hidl_offset);
        this.rsrp = _hidl_blob.getInt32(4 + _hidl_offset);
        this.rsrq = _hidl_blob.getInt32(8 + _hidl_offset);
        this.rssnr = _hidl_blob.getInt32(12 + _hidl_offset);
        this.cqi = _hidl_blob.getInt32(16 + _hidl_offset);
        this.timingAdvance = _hidl_blob.getInt32(20 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(24);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<LteSignalStrength> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.signalStrength);
        _hidl_blob.putInt32(4 + _hidl_offset, this.rsrp);
        _hidl_blob.putInt32(8 + _hidl_offset, this.rsrq);
        _hidl_blob.putInt32(12 + _hidl_offset, this.rssnr);
        _hidl_blob.putInt32(16 + _hidl_offset, this.cqi);
        _hidl_blob.putInt32(20 + _hidl_offset, this.timingAdvance);
    }
}
