package android.hardware.radio.V1_1;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class KeepaliveRequest {
    public int type = 0;
    public ArrayList<Byte> sourceAddress = new ArrayList<>();
    public int sourcePort = 0;
    public ArrayList<Byte> destinationAddress = new ArrayList<>();
    public int destinationPort = 0;
    public int maxKeepaliveIntervalMillis = 0;
    public int cid = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != KeepaliveRequest.class) {
            return false;
        }
        KeepaliveRequest other = (KeepaliveRequest) otherObject;
        if (this.type == other.type && HidlSupport.deepEquals(this.sourceAddress, other.sourceAddress) && this.sourcePort == other.sourcePort && HidlSupport.deepEquals(this.destinationAddress, other.destinationAddress) && this.destinationPort == other.destinationPort && this.maxKeepaliveIntervalMillis == other.maxKeepaliveIntervalMillis && this.cid == other.cid) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(this.sourceAddress)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.sourcePort))), Integer.valueOf(HidlSupport.deepHashCode(this.destinationAddress)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.destinationPort))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxKeepaliveIntervalMillis))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cid))));
    }

    public final String toString() {
        return "{.type = " + KeepaliveType.toString(this.type) + ", .sourceAddress = " + this.sourceAddress + ", .sourcePort = " + this.sourcePort + ", .destinationAddress = " + this.destinationAddress + ", .destinationPort = " + this.destinationPort + ", .maxKeepaliveIntervalMillis = " + this.maxKeepaliveIntervalMillis + ", .cid = " + this.cid + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(64L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<KeepaliveRequest> readVectorFromParcel(HwParcel parcel) {
        ArrayList<KeepaliveRequest> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 64, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            KeepaliveRequest _hidl_vec_element = new KeepaliveRequest();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 64);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.type = _hidl_blob.getInt32(_hidl_offset + 0);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 8 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, true);
        this.sourceAddress.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            byte _hidl_vec_element = childBlob.getInt8(_hidl_index_0 * 1);
            this.sourceAddress.add(Byte.valueOf(_hidl_vec_element));
        }
        this.sourcePort = _hidl_blob.getInt32(_hidl_offset + 24);
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 32 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 1, _hidl_blob.handle(), _hidl_offset + 32 + 0, true);
        this.destinationAddress.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            byte _hidl_vec_element2 = childBlob2.getInt8(_hidl_index_02 * 1);
            this.destinationAddress.add(Byte.valueOf(_hidl_vec_element2));
        }
        this.destinationPort = _hidl_blob.getInt32(_hidl_offset + 48);
        this.maxKeepaliveIntervalMillis = _hidl_blob.getInt32(_hidl_offset + 52);
        this.cid = _hidl_blob.getInt32(_hidl_offset + 56);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(64);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<KeepaliveRequest> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 64);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 64);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(_hidl_offset + 0, this.type);
        int _hidl_vec_size = this.sourceAddress.size();
        _hidl_blob.putInt32(_hidl_offset + 8 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 8 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 1);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt8(_hidl_index_0 * 1, this.sourceAddress.get(_hidl_index_0).byteValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 8 + 0, childBlob);
        _hidl_blob.putInt32(_hidl_offset + 24, this.sourcePort);
        int _hidl_vec_size2 = this.destinationAddress.size();
        _hidl_blob.putInt32(_hidl_offset + 32 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 32 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 1);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            childBlob2.putInt8(_hidl_index_02 * 1, this.destinationAddress.get(_hidl_index_02).byteValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 32 + 0, childBlob2);
        _hidl_blob.putInt32(_hidl_offset + 48, this.destinationPort);
        _hidl_blob.putInt32(_hidl_offset + 52, this.maxKeepaliveIntervalMillis);
        _hidl_blob.putInt32(_hidl_offset + 56, this.cid);
    }
}
