package android.hardware.contexthub.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ContextHub {
    public String name = new String();
    public String vendor = new String();
    public String toolchain = new String();
    public int platformVersion = 0;
    public int toolchainVersion = 0;
    public int hubId = 0;
    public float peakMips = 0.0f;
    public float stoppedPowerDrawMw = 0.0f;
    public float sleepPowerDrawMw = 0.0f;
    public float peakPowerDrawMw = 0.0f;
    public ArrayList<PhysicalSensor> connectedSensors = new ArrayList<>();
    public int maxSupportedMsgLen = 0;
    public long chrePlatformId = 0;
    public byte chreApiMajorVersion = 0;
    public byte chreApiMinorVersion = 0;
    public short chrePatchVersion = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != ContextHub.class) {
            return false;
        }
        ContextHub other = (ContextHub) otherObject;
        if (HidlSupport.deepEquals(this.name, other.name) && HidlSupport.deepEquals(this.vendor, other.vendor) && HidlSupport.deepEquals(this.toolchain, other.toolchain) && this.platformVersion == other.platformVersion && this.toolchainVersion == other.toolchainVersion && this.hubId == other.hubId && this.peakMips == other.peakMips && this.stoppedPowerDrawMw == other.stoppedPowerDrawMw && this.sleepPowerDrawMw == other.sleepPowerDrawMw && this.peakPowerDrawMw == other.peakPowerDrawMw && HidlSupport.deepEquals(this.connectedSensors, other.connectedSensors) && this.maxSupportedMsgLen == other.maxSupportedMsgLen && this.chrePlatformId == other.chrePlatformId && this.chreApiMajorVersion == other.chreApiMajorVersion && this.chreApiMinorVersion == other.chreApiMinorVersion && this.chrePatchVersion == other.chrePatchVersion) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.name)), Integer.valueOf(HidlSupport.deepHashCode(this.vendor)), Integer.valueOf(HidlSupport.deepHashCode(this.toolchain)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.platformVersion))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.toolchainVersion))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.hubId))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.peakMips))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.stoppedPowerDrawMw))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.sleepPowerDrawMw))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.peakPowerDrawMw))), Integer.valueOf(HidlSupport.deepHashCode(this.connectedSensors)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxSupportedMsgLen))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.chrePlatformId))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.chreApiMajorVersion))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.chreApiMinorVersion))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.chrePatchVersion))));
    }

    public final String toString() {
        return "{.name = " + this.name + ", .vendor = " + this.vendor + ", .toolchain = " + this.toolchain + ", .platformVersion = " + this.platformVersion + ", .toolchainVersion = " + this.toolchainVersion + ", .hubId = " + this.hubId + ", .peakMips = " + this.peakMips + ", .stoppedPowerDrawMw = " + this.stoppedPowerDrawMw + ", .sleepPowerDrawMw = " + this.sleepPowerDrawMw + ", .peakPowerDrawMw = " + this.peakPowerDrawMw + ", .connectedSensors = " + this.connectedSensors + ", .maxSupportedMsgLen = " + this.maxSupportedMsgLen + ", .chrePlatformId = " + this.chrePlatformId + ", .chreApiMajorVersion = " + ((int) this.chreApiMajorVersion) + ", .chreApiMinorVersion = " + ((int) this.chreApiMinorVersion) + ", .chrePatchVersion = " + ((int) this.chrePatchVersion) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(120L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<ContextHub> readVectorFromParcel(HwParcel parcel) {
        ArrayList<ContextHub> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 120, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ContextHub _hidl_vec_element = new ContextHub();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 120);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        String string = _hidl_blob.getString(_hidl_offset + 0);
        this.name = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 16);
        this.vendor = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        String string3 = _hidl_blob.getString(_hidl_offset + 32);
        this.toolchain = string3;
        parcel.readEmbeddedBuffer(string3.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 32 + 0, false);
        this.platformVersion = _hidl_blob.getInt32(_hidl_offset + 48);
        this.toolchainVersion = _hidl_blob.getInt32(_hidl_offset + 52);
        this.hubId = _hidl_blob.getInt32(_hidl_offset + 56);
        this.peakMips = _hidl_blob.getFloat(_hidl_offset + 60);
        this.stoppedPowerDrawMw = _hidl_blob.getFloat(_hidl_offset + 64);
        this.sleepPowerDrawMw = _hidl_blob.getFloat(_hidl_offset + 68);
        this.peakPowerDrawMw = _hidl_blob.getFloat(_hidl_offset + 72);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 80 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 96, _hidl_blob.handle(), _hidl_offset + 80 + 0, true);
        this.connectedSensors.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            PhysicalSensor _hidl_vec_element = new PhysicalSensor();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 96);
            this.connectedSensors.add(_hidl_vec_element);
        }
        this.maxSupportedMsgLen = _hidl_blob.getInt32(_hidl_offset + 96);
        this.chrePlatformId = _hidl_blob.getInt64(_hidl_offset + 104);
        this.chreApiMajorVersion = _hidl_blob.getInt8(_hidl_offset + 112);
        this.chreApiMinorVersion = _hidl_blob.getInt8(_hidl_offset + 113);
        this.chrePatchVersion = _hidl_blob.getInt16(_hidl_offset + 114);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(120);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<ContextHub> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 120);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 120);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(_hidl_offset + 0, this.name);
        _hidl_blob.putString(16 + _hidl_offset, this.vendor);
        _hidl_blob.putString(32 + _hidl_offset, this.toolchain);
        _hidl_blob.putInt32(48 + _hidl_offset, this.platformVersion);
        _hidl_blob.putInt32(52 + _hidl_offset, this.toolchainVersion);
        _hidl_blob.putInt32(56 + _hidl_offset, this.hubId);
        _hidl_blob.putFloat(60 + _hidl_offset, this.peakMips);
        _hidl_blob.putFloat(64 + _hidl_offset, this.stoppedPowerDrawMw);
        _hidl_blob.putFloat(68 + _hidl_offset, this.sleepPowerDrawMw);
        _hidl_blob.putFloat(72 + _hidl_offset, this.peakPowerDrawMw);
        int _hidl_vec_size = this.connectedSensors.size();
        _hidl_blob.putInt32(_hidl_offset + 80 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 80 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 96);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.connectedSensors.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 96);
        }
        _hidl_blob.putBlob(80 + _hidl_offset + 0, childBlob);
        _hidl_blob.putInt32(96 + _hidl_offset, this.maxSupportedMsgLen);
        _hidl_blob.putInt64(104 + _hidl_offset, this.chrePlatformId);
        _hidl_blob.putInt8(112 + _hidl_offset, this.chreApiMajorVersion);
        _hidl_blob.putInt8(113 + _hidl_offset, this.chreApiMinorVersion);
        _hidl_blob.putInt16(114 + _hidl_offset, this.chrePatchVersion);
    }
}
