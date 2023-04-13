package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CdmaSmsMessage {
    public int teleserviceId = 0;
    public boolean isServicePresent = false;
    public int serviceCategory = 0;
    public CdmaSmsAddress address = new CdmaSmsAddress();
    public CdmaSmsSubaddress subAddress = new CdmaSmsSubaddress();
    public ArrayList<Byte> bearerData = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CdmaSmsMessage.class) {
            return false;
        }
        CdmaSmsMessage other = (CdmaSmsMessage) otherObject;
        if (this.teleserviceId == other.teleserviceId && this.isServicePresent == other.isServicePresent && this.serviceCategory == other.serviceCategory && HidlSupport.deepEquals(this.address, other.address) && HidlSupport.deepEquals(this.subAddress, other.subAddress) && HidlSupport.deepEquals(this.bearerData, other.bearerData)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.teleserviceId))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isServicePresent))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.serviceCategory))), Integer.valueOf(HidlSupport.deepHashCode(this.address)), Integer.valueOf(HidlSupport.deepHashCode(this.subAddress)), Integer.valueOf(HidlSupport.deepHashCode(this.bearerData)));
    }

    public final String toString() {
        return "{.teleserviceId = " + this.teleserviceId + ", .isServicePresent = " + this.isServicePresent + ", .serviceCategory = " + this.serviceCategory + ", .address = " + this.address + ", .subAddress = " + this.subAddress + ", .bearerData = " + this.bearerData + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(88L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CdmaSmsMessage> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CdmaSmsMessage> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 88, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CdmaSmsMessage _hidl_vec_element = new CdmaSmsMessage();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 88);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.teleserviceId = _hidl_blob.getInt32(_hidl_offset + 0);
        this.isServicePresent = _hidl_blob.getBool(_hidl_offset + 4);
        this.serviceCategory = _hidl_blob.getInt32(_hidl_offset + 8);
        this.address.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 16);
        this.subAddress.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 48);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 72 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 1, _hidl_blob.handle(), _hidl_offset + 72 + 0, true);
        this.bearerData.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            byte _hidl_vec_element = childBlob.getInt8(_hidl_index_0 * 1);
            this.bearerData.add(Byte.valueOf(_hidl_vec_element));
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(88);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CdmaSmsMessage> _hidl_vec) {
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
        _hidl_blob.putInt32(_hidl_offset + 0, this.teleserviceId);
        _hidl_blob.putBool(4 + _hidl_offset, this.isServicePresent);
        _hidl_blob.putInt32(_hidl_offset + 8, this.serviceCategory);
        this.address.writeEmbeddedToBlob(_hidl_blob, 16 + _hidl_offset);
        this.subAddress.writeEmbeddedToBlob(_hidl_blob, 48 + _hidl_offset);
        int _hidl_vec_size = this.bearerData.size();
        _hidl_blob.putInt32(_hidl_offset + 72 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 72 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 1);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt8(_hidl_index_0 * 1, this.bearerData.get(_hidl_index_0).byteValue());
        }
        _hidl_blob.putBlob(72 + _hidl_offset + 0, childBlob);
    }
}
