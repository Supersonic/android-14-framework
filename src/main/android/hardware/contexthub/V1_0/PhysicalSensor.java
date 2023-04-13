package android.hardware.contexthub.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class PhysicalSensor {
    public int sensorType = 0;
    public String type = new String();
    public String name = new String();
    public String vendor = new String();
    public int version = 0;
    public int fifoReservedCount = 0;
    public int fifoMaxCount = 0;
    public long minDelayMs = 0;
    public long maxDelayMs = 0;
    public float peakPowerMw = 0.0f;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != PhysicalSensor.class) {
            return false;
        }
        PhysicalSensor other = (PhysicalSensor) otherObject;
        if (this.sensorType == other.sensorType && HidlSupport.deepEquals(this.type, other.type) && HidlSupport.deepEquals(this.name, other.name) && HidlSupport.deepEquals(this.vendor, other.vendor) && this.version == other.version && this.fifoReservedCount == other.fifoReservedCount && this.fifoMaxCount == other.fifoMaxCount && this.minDelayMs == other.minDelayMs && this.maxDelayMs == other.maxDelayMs && this.peakPowerMw == other.peakPowerMw) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.sensorType))), Integer.valueOf(HidlSupport.deepHashCode(this.type)), Integer.valueOf(HidlSupport.deepHashCode(this.name)), Integer.valueOf(HidlSupport.deepHashCode(this.vendor)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.version))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.fifoReservedCount))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.fifoMaxCount))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.minDelayMs))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.maxDelayMs))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.peakPowerMw))));
    }

    public final String toString() {
        return "{.sensorType = " + SensorType.toString(this.sensorType) + ", .type = " + this.type + ", .name = " + this.name + ", .vendor = " + this.vendor + ", .version = " + this.version + ", .fifoReservedCount = " + this.fifoReservedCount + ", .fifoMaxCount = " + this.fifoMaxCount + ", .minDelayMs = " + this.minDelayMs + ", .maxDelayMs = " + this.maxDelayMs + ", .peakPowerMw = " + this.peakPowerMw + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(96L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<PhysicalSensor> readVectorFromParcel(HwParcel parcel) {
        ArrayList<PhysicalSensor> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 96, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            PhysicalSensor _hidl_vec_element = new PhysicalSensor();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 96);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.sensorType = _hidl_blob.getInt32(_hidl_offset + 0);
        String string = _hidl_blob.getString(_hidl_offset + 8);
        this.type = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 24);
        this.name = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
        String string3 = _hidl_blob.getString(_hidl_offset + 40);
        this.vendor = string3;
        parcel.readEmbeddedBuffer(string3.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 40 + 0, false);
        this.version = _hidl_blob.getInt32(_hidl_offset + 56);
        this.fifoReservedCount = _hidl_blob.getInt32(_hidl_offset + 60);
        this.fifoMaxCount = _hidl_blob.getInt32(_hidl_offset + 64);
        this.minDelayMs = _hidl_blob.getInt64(_hidl_offset + 72);
        this.maxDelayMs = _hidl_blob.getInt64(_hidl_offset + 80);
        this.peakPowerMw = _hidl_blob.getFloat(_hidl_offset + 88);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(96);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<PhysicalSensor> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 96);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 96);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.sensorType);
        _hidl_blob.putString(8 + _hidl_offset, this.type);
        _hidl_blob.putString(24 + _hidl_offset, this.name);
        _hidl_blob.putString(40 + _hidl_offset, this.vendor);
        _hidl_blob.putInt32(56 + _hidl_offset, this.version);
        _hidl_blob.putInt32(60 + _hidl_offset, this.fifoReservedCount);
        _hidl_blob.putInt32(64 + _hidl_offset, this.fifoMaxCount);
        _hidl_blob.putInt64(72 + _hidl_offset, this.minDelayMs);
        _hidl_blob.putInt64(80 + _hidl_offset, this.maxDelayMs);
        _hidl_blob.putFloat(88 + _hidl_offset, this.peakPowerMw);
    }
}
