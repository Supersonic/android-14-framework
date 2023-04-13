package android.hardware.thermal.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class Temperature {
    public int type = 0;
    public String name = new String();
    public float currentValue = 0.0f;
    public float throttlingThreshold = 0.0f;
    public float shutdownThreshold = 0.0f;
    public float vrThrottlingThreshold = 0.0f;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != Temperature.class) {
            return false;
        }
        Temperature other = (Temperature) otherObject;
        if (this.type == other.type && HidlSupport.deepEquals(this.name, other.name) && this.currentValue == other.currentValue && this.throttlingThreshold == other.throttlingThreshold && this.shutdownThreshold == other.shutdownThreshold && this.vrThrottlingThreshold == other.vrThrottlingThreshold) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(this.name)), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.currentValue))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.throttlingThreshold))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.shutdownThreshold))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.vrThrottlingThreshold))));
    }

    public final String toString() {
        return "{.type = " + TemperatureType.toString(this.type) + ", .name = " + this.name + ", .currentValue = " + this.currentValue + ", .throttlingThreshold = " + this.throttlingThreshold + ", .shutdownThreshold = " + this.shutdownThreshold + ", .vrThrottlingThreshold = " + this.vrThrottlingThreshold + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(40L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<Temperature> readVectorFromParcel(HwParcel parcel) {
        ArrayList<Temperature> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            Temperature _hidl_vec_element = new Temperature();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.type = _hidl_blob.getInt32(_hidl_offset + 0);
        String string = _hidl_blob.getString(_hidl_offset + 8);
        this.name = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
        this.currentValue = _hidl_blob.getFloat(_hidl_offset + 24);
        this.throttlingThreshold = _hidl_blob.getFloat(_hidl_offset + 28);
        this.shutdownThreshold = _hidl_blob.getFloat(_hidl_offset + 32);
        this.vrThrottlingThreshold = _hidl_blob.getFloat(_hidl_offset + 36);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(40);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Temperature> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.type);
        _hidl_blob.putString(8 + _hidl_offset, this.name);
        _hidl_blob.putFloat(24 + _hidl_offset, this.currentValue);
        _hidl_blob.putFloat(28 + _hidl_offset, this.throttlingThreshold);
        _hidl_blob.putFloat(32 + _hidl_offset, this.shutdownThreshold);
        _hidl_blob.putFloat(36 + _hidl_offset, this.vrThrottlingThreshold);
    }
}
