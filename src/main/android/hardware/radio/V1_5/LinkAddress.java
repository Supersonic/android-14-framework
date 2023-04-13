package android.hardware.radio.V1_5;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class LinkAddress {
    public String address = new String();
    public long deprecationTime = 0;
    public long expirationTime = 0;
    public int properties;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != LinkAddress.class) {
            return false;
        }
        LinkAddress other = (LinkAddress) otherObject;
        if (HidlSupport.deepEquals(this.address, other.address) && HidlSupport.deepEquals(Integer.valueOf(this.properties), Integer.valueOf(other.properties)) && this.deprecationTime == other.deprecationTime && this.expirationTime == other.expirationTime) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.address)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.properties))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.deprecationTime))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.expirationTime))));
    }

    public final String toString() {
        return "{.address = " + this.address + ", .properties = " + AddressProperty.dumpBitfield(this.properties) + ", .deprecationTime = " + this.deprecationTime + ", .expirationTime = " + this.expirationTime + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(40L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<LinkAddress> readVectorFromParcel(HwParcel parcel) {
        ArrayList<LinkAddress> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            LinkAddress _hidl_vec_element = new LinkAddress();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        String string = _hidl_blob.getString(_hidl_offset + 0);
        this.address = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        this.properties = _hidl_blob.getInt32(16 + _hidl_offset);
        this.deprecationTime = _hidl_blob.getInt64(24 + _hidl_offset);
        this.expirationTime = _hidl_blob.getInt64(32 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(40);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<LinkAddress> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 40);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 40);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(0 + _hidl_offset, this.address);
        _hidl_blob.putInt32(16 + _hidl_offset, this.properties);
        _hidl_blob.putInt64(24 + _hidl_offset, this.deprecationTime);
        _hidl_blob.putInt64(32 + _hidl_offset, this.expirationTime);
    }
}
