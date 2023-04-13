package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class AppStatus {
    public int appType = 0;
    public int appState = 0;
    public int persoSubstate = 0;
    public String aidPtr = new String();
    public String appLabelPtr = new String();
    public int pin1Replaced = 0;
    public int pin1 = 0;
    public int pin2 = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != AppStatus.class) {
            return false;
        }
        AppStatus other = (AppStatus) otherObject;
        if (this.appType == other.appType && this.appState == other.appState && this.persoSubstate == other.persoSubstate && HidlSupport.deepEquals(this.aidPtr, other.aidPtr) && HidlSupport.deepEquals(this.appLabelPtr, other.appLabelPtr) && this.pin1Replaced == other.pin1Replaced && this.pin1 == other.pin1 && this.pin2 == other.pin2) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.appType))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.appState))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.persoSubstate))), Integer.valueOf(HidlSupport.deepHashCode(this.aidPtr)), Integer.valueOf(HidlSupport.deepHashCode(this.appLabelPtr)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.pin1Replaced))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.pin1))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.pin2))));
    }

    public final String toString() {
        return "{.appType = " + AppType.toString(this.appType) + ", .appState = " + AppState.toString(this.appState) + ", .persoSubstate = " + PersoSubstate.toString(this.persoSubstate) + ", .aidPtr = " + this.aidPtr + ", .appLabelPtr = " + this.appLabelPtr + ", .pin1Replaced = " + this.pin1Replaced + ", .pin1 = " + PinState.toString(this.pin1) + ", .pin2 = " + PinState.toString(this.pin2) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(64L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<AppStatus> readVectorFromParcel(HwParcel parcel) {
        ArrayList<AppStatus> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 64, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            AppStatus _hidl_vec_element = new AppStatus();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 64);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.appType = _hidl_blob.getInt32(_hidl_offset + 0);
        this.appState = _hidl_blob.getInt32(_hidl_offset + 4);
        this.persoSubstate = _hidl_blob.getInt32(_hidl_offset + 8);
        String string = _hidl_blob.getString(_hidl_offset + 16);
        this.aidPtr = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 32);
        this.appLabelPtr = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 32 + 0, false);
        this.pin1Replaced = _hidl_blob.getInt32(_hidl_offset + 48);
        this.pin1 = _hidl_blob.getInt32(_hidl_offset + 52);
        this.pin2 = _hidl_blob.getInt32(_hidl_offset + 56);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(64);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<AppStatus> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.appType);
        _hidl_blob.putInt32(4 + _hidl_offset, this.appState);
        _hidl_blob.putInt32(8 + _hidl_offset, this.persoSubstate);
        _hidl_blob.putString(16 + _hidl_offset, this.aidPtr);
        _hidl_blob.putString(32 + _hidl_offset, this.appLabelPtr);
        _hidl_blob.putInt32(48 + _hidl_offset, this.pin1Replaced);
        _hidl_blob.putInt32(52 + _hidl_offset, this.pin1);
        _hidl_blob.putInt32(56 + _hidl_offset, this.pin2);
    }
}
