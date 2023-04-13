package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class HardwareConfig {
    public int type = 0;
    public String uuid = new String();
    public int state = 0;
    public ArrayList<HardwareConfigModem> modem = new ArrayList<>();
    public ArrayList<HardwareConfigSim> sim = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != HardwareConfig.class) {
            return false;
        }
        HardwareConfig other = (HardwareConfig) otherObject;
        if (this.type == other.type && HidlSupport.deepEquals(this.uuid, other.uuid) && this.state == other.state && HidlSupport.deepEquals(this.modem, other.modem) && HidlSupport.deepEquals(this.sim, other.sim)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(this.uuid)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.state))), Integer.valueOf(HidlSupport.deepHashCode(this.modem)), Integer.valueOf(HidlSupport.deepHashCode(this.sim)));
    }

    public final String toString() {
        return "{.type = " + HardwareConfigType.toString(this.type) + ", .uuid = " + this.uuid + ", .state = " + HardwareConfigState.toString(this.state) + ", .modem = " + this.modem + ", .sim = " + this.sim + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(64L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<HardwareConfig> readVectorFromParcel(HwParcel parcel) {
        ArrayList<HardwareConfig> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 64, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            HardwareConfig _hidl_vec_element = new HardwareConfig();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 64);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.type = _hidl_blob.getInt32(_hidl_offset + 0);
        String string = _hidl_blob.getString(_hidl_offset + 8);
        this.uuid = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
        this.state = _hidl_blob.getInt32(_hidl_offset + 24);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 32 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 20, _hidl_blob.handle(), _hidl_offset + 32 + 0, true);
        this.modem.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            HardwareConfigModem _hidl_vec_element = new HardwareConfigModem();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 20);
            this.modem.add(_hidl_vec_element);
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 48 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 16, _hidl_blob.handle(), _hidl_offset + 48 + 0, true);
        this.sim.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            HardwareConfigSim _hidl_vec_element2 = new HardwareConfigSim();
            _hidl_vec_element2.readEmbeddedFromParcel(parcel, childBlob2, _hidl_index_02 * 16);
            this.sim.add(_hidl_vec_element2);
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(64);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<HardwareConfig> _hidl_vec) {
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
        _hidl_blob.putString(_hidl_offset + 8, this.uuid);
        _hidl_blob.putInt32(_hidl_offset + 24, this.state);
        int _hidl_vec_size = this.modem.size();
        _hidl_blob.putInt32(_hidl_offset + 32 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 32 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 20);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.modem.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 20);
        }
        _hidl_blob.putBlob(_hidl_offset + 32 + 0, childBlob);
        int _hidl_vec_size2 = this.sim.size();
        _hidl_blob.putInt32(_hidl_offset + 48 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 48 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 16);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            this.sim.get(_hidl_index_02).writeEmbeddedToBlob(childBlob2, _hidl_index_02 * 16);
        }
        _hidl_blob.putBlob(_hidl_offset + 48 + 0, childBlob2);
    }
}
