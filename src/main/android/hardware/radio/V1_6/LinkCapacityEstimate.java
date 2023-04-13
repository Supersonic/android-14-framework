package android.hardware.radio.V1_6;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class LinkCapacityEstimate {
    public int downlinkCapacityKbps = 0;
    public int uplinkCapacityKbps = 0;
    public int secondaryDownlinkCapacityKbps = 0;
    public int secondaryUplinkCapacityKbps = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != LinkCapacityEstimate.class) {
            return false;
        }
        LinkCapacityEstimate other = (LinkCapacityEstimate) otherObject;
        if (this.downlinkCapacityKbps == other.downlinkCapacityKbps && this.uplinkCapacityKbps == other.uplinkCapacityKbps && this.secondaryDownlinkCapacityKbps == other.secondaryDownlinkCapacityKbps && this.secondaryUplinkCapacityKbps == other.secondaryUplinkCapacityKbps) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.downlinkCapacityKbps))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.uplinkCapacityKbps))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.secondaryDownlinkCapacityKbps))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.secondaryUplinkCapacityKbps))));
    }

    public final String toString() {
        return "{.downlinkCapacityKbps = " + this.downlinkCapacityKbps + ", .uplinkCapacityKbps = " + this.uplinkCapacityKbps + ", .secondaryDownlinkCapacityKbps = " + this.secondaryDownlinkCapacityKbps + ", .secondaryUplinkCapacityKbps = " + this.secondaryUplinkCapacityKbps + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(16L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<LinkCapacityEstimate> readVectorFromParcel(HwParcel parcel) {
        ArrayList<LinkCapacityEstimate> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            LinkCapacityEstimate _hidl_vec_element = new LinkCapacityEstimate();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 16);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.downlinkCapacityKbps = _hidl_blob.getInt32(0 + _hidl_offset);
        this.uplinkCapacityKbps = _hidl_blob.getInt32(4 + _hidl_offset);
        this.secondaryDownlinkCapacityKbps = _hidl_blob.getInt32(8 + _hidl_offset);
        this.secondaryUplinkCapacityKbps = _hidl_blob.getInt32(12 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(16);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<LinkCapacityEstimate> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.downlinkCapacityKbps);
        _hidl_blob.putInt32(4 + _hidl_offset, this.uplinkCapacityKbps);
        _hidl_blob.putInt32(8 + _hidl_offset, this.secondaryDownlinkCapacityKbps);
        _hidl_blob.putInt32(12 + _hidl_offset, this.secondaryUplinkCapacityKbps);
    }
}
