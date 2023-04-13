package android.hardware.radio.V1_5;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class SignalThresholdInfo {
    public int signalMeasurement = 0;
    public int hysteresisMs = 0;
    public int hysteresisDb = 0;
    public ArrayList<Integer> thresholds = new ArrayList<>();
    public boolean isEnabled = false;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != SignalThresholdInfo.class) {
            return false;
        }
        SignalThresholdInfo other = (SignalThresholdInfo) otherObject;
        if (this.signalMeasurement == other.signalMeasurement && this.hysteresisMs == other.hysteresisMs && this.hysteresisDb == other.hysteresisDb && HidlSupport.deepEquals(this.thresholds, other.thresholds) && this.isEnabled == other.isEnabled) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.signalMeasurement))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.hysteresisMs))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.hysteresisDb))), Integer.valueOf(HidlSupport.deepHashCode(this.thresholds)), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isEnabled))));
    }

    public final String toString() {
        return "{.signalMeasurement = " + SignalMeasurementType.toString(this.signalMeasurement) + ", .hysteresisMs = " + this.hysteresisMs + ", .hysteresisDb = " + this.hysteresisDb + ", .thresholds = " + this.thresholds + ", .isEnabled = " + this.isEnabled + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(40L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<SignalThresholdInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<SignalThresholdInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SignalThresholdInfo _hidl_vec_element = new SignalThresholdInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.signalMeasurement = _hidl_blob.getInt32(_hidl_offset + 0);
        this.hysteresisMs = _hidl_blob.getInt32(_hidl_offset + 4);
        this.hysteresisDb = _hidl_blob.getInt32(_hidl_offset + 8);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 16 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 4, _hidl_blob.handle(), _hidl_offset + 16 + 0, true);
        this.thresholds.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            int _hidl_vec_element = childBlob.getInt32(_hidl_index_0 * 4);
            this.thresholds.add(Integer.valueOf(_hidl_vec_element));
        }
        this.isEnabled = _hidl_blob.getBool(_hidl_offset + 32);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(40);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<SignalThresholdInfo> _hidl_vec) {
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
        _hidl_blob.putInt32(_hidl_offset + 0, this.signalMeasurement);
        _hidl_blob.putInt32(4 + _hidl_offset, this.hysteresisMs);
        _hidl_blob.putInt32(_hidl_offset + 8, this.hysteresisDb);
        int _hidl_vec_size = this.thresholds.size();
        _hidl_blob.putInt32(_hidl_offset + 16 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 16 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 4);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt32(_hidl_index_0 * 4, this.thresholds.get(_hidl_index_0).intValue());
        }
        _hidl_blob.putBlob(16 + _hidl_offset + 0, childBlob);
        _hidl_blob.putBool(32 + _hidl_offset, this.isEnabled);
    }
}
