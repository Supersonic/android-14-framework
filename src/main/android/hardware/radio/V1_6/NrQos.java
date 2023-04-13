package android.hardware.radio.V1_6;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class NrQos {
    public short fiveQi = 0;
    public QosBandwidth downlink = new QosBandwidth();
    public QosBandwidth uplink = new QosBandwidth();
    public byte qfi = 0;
    public short averagingWindowMs = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != NrQos.class) {
            return false;
        }
        NrQos other = (NrQos) otherObject;
        if (this.fiveQi == other.fiveQi && HidlSupport.deepEquals(this.downlink, other.downlink) && HidlSupport.deepEquals(this.uplink, other.uplink) && this.qfi == other.qfi && this.averagingWindowMs == other.averagingWindowMs) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.fiveQi))), Integer.valueOf(HidlSupport.deepHashCode(this.downlink)), Integer.valueOf(HidlSupport.deepHashCode(this.uplink)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.qfi))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.averagingWindowMs))));
    }

    public final String toString() {
        return "{.fiveQi = " + ((int) this.fiveQi) + ", .downlink = " + this.downlink + ", .uplink = " + this.uplink + ", .qfi = " + ((int) this.qfi) + ", .averagingWindowMs = " + ((int) this.averagingWindowMs) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(24L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<NrQos> readVectorFromParcel(HwParcel parcel) {
        ArrayList<NrQos> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            NrQos _hidl_vec_element = new NrQos();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.fiveQi = _hidl_blob.getInt16(0 + _hidl_offset);
        this.downlink.readEmbeddedFromParcel(parcel, _hidl_blob, 4 + _hidl_offset);
        this.uplink.readEmbeddedFromParcel(parcel, _hidl_blob, 12 + _hidl_offset);
        this.qfi = _hidl_blob.getInt8(20 + _hidl_offset);
        this.averagingWindowMs = _hidl_blob.getInt16(22 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(24);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<NrQos> _hidl_vec) {
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
        _hidl_blob.putInt16(0 + _hidl_offset, this.fiveQi);
        this.downlink.writeEmbeddedToBlob(_hidl_blob, 4 + _hidl_offset);
        this.uplink.writeEmbeddedToBlob(_hidl_blob, 12 + _hidl_offset);
        _hidl_blob.putInt8(20 + _hidl_offset, this.qfi);
        _hidl_blob.putInt16(22 + _hidl_offset, this.averagingWindowMs);
    }
}
