package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CdmaCallWaiting {
    public String number = new String();
    public int numberPresentation = 0;
    public String name = new String();
    public CdmaSignalInfoRecord signalInfoRecord = new CdmaSignalInfoRecord();
    public int numberType = 0;
    public int numberPlan = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CdmaCallWaiting.class) {
            return false;
        }
        CdmaCallWaiting other = (CdmaCallWaiting) otherObject;
        if (HidlSupport.deepEquals(this.number, other.number) && this.numberPresentation == other.numberPresentation && HidlSupport.deepEquals(this.name, other.name) && HidlSupport.deepEquals(this.signalInfoRecord, other.signalInfoRecord) && this.numberType == other.numberType && this.numberPlan == other.numberPlan) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.number)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.numberPresentation))), Integer.valueOf(HidlSupport.deepHashCode(this.name)), Integer.valueOf(HidlSupport.deepHashCode(this.signalInfoRecord)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.numberType))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.numberPlan))));
    }

    public final String toString() {
        return "{.number = " + this.number + ", .numberPresentation = " + CdmaCallWaitingNumberPresentation.toString(this.numberPresentation) + ", .name = " + this.name + ", .signalInfoRecord = " + this.signalInfoRecord + ", .numberType = " + CdmaCallWaitingNumberType.toString(this.numberType) + ", .numberPlan = " + CdmaCallWaitingNumberPlan.toString(this.numberPlan) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(56L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CdmaCallWaiting> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CdmaCallWaiting> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 56, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CdmaCallWaiting _hidl_vec_element = new CdmaCallWaiting();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 56);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        String string = _hidl_blob.getString(_hidl_offset + 0);
        this.number = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        this.numberPresentation = _hidl_blob.getInt32(_hidl_offset + 16);
        String string2 = _hidl_blob.getString(_hidl_offset + 24);
        this.name = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
        this.signalInfoRecord.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 40);
        this.numberType = _hidl_blob.getInt32(_hidl_offset + 44);
        this.numberPlan = _hidl_blob.getInt32(_hidl_offset + 48);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(56);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CdmaCallWaiting> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 56);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 56);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(0 + _hidl_offset, this.number);
        _hidl_blob.putInt32(16 + _hidl_offset, this.numberPresentation);
        _hidl_blob.putString(24 + _hidl_offset, this.name);
        this.signalInfoRecord.writeEmbeddedToBlob(_hidl_blob, 40 + _hidl_offset);
        _hidl_blob.putInt32(44 + _hidl_offset, this.numberType);
        _hidl_blob.putInt32(48 + _hidl_offset, this.numberPlan);
    }
}
