package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class RadioCapability {
    public int raf;
    public int session = 0;
    public int phase = 0;
    public String logicalModemUuid = new String();
    public int status = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != RadioCapability.class) {
            return false;
        }
        RadioCapability other = (RadioCapability) otherObject;
        if (this.session == other.session && this.phase == other.phase && HidlSupport.deepEquals(Integer.valueOf(this.raf), Integer.valueOf(other.raf)) && HidlSupport.deepEquals(this.logicalModemUuid, other.logicalModemUuid) && this.status == other.status) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.session))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.phase))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.raf))), Integer.valueOf(HidlSupport.deepHashCode(this.logicalModemUuid)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.status))));
    }

    public final String toString() {
        return "{.session = " + this.session + ", .phase = " + RadioCapabilityPhase.toString(this.phase) + ", .raf = " + RadioAccessFamily.dumpBitfield(this.raf) + ", .logicalModemUuid = " + this.logicalModemUuid + ", .status = " + RadioCapabilityStatus.toString(this.status) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(40L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<RadioCapability> readVectorFromParcel(HwParcel parcel) {
        ArrayList<RadioCapability> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            RadioCapability _hidl_vec_element = new RadioCapability();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.session = _hidl_blob.getInt32(_hidl_offset + 0);
        this.phase = _hidl_blob.getInt32(_hidl_offset + 4);
        this.raf = _hidl_blob.getInt32(_hidl_offset + 8);
        String string = _hidl_blob.getString(_hidl_offset + 16);
        this.logicalModemUuid = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        this.status = _hidl_blob.getInt32(_hidl_offset + 32);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(40);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<RadioCapability> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.session);
        _hidl_blob.putInt32(4 + _hidl_offset, this.phase);
        _hidl_blob.putInt32(8 + _hidl_offset, this.raf);
        _hidl_blob.putString(16 + _hidl_offset, this.logicalModemUuid);
        _hidl_blob.putInt32(32 + _hidl_offset, this.status);
    }
}
