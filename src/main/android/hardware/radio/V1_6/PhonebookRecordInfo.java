package android.hardware.radio.V1_6;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class PhonebookRecordInfo {
    public int recordId = 0;
    public String name = new String();
    public String number = new String();
    public ArrayList<String> emails = new ArrayList<>();
    public ArrayList<String> additionalNumbers = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != PhonebookRecordInfo.class) {
            return false;
        }
        PhonebookRecordInfo other = (PhonebookRecordInfo) otherObject;
        if (this.recordId == other.recordId && HidlSupport.deepEquals(this.name, other.name) && HidlSupport.deepEquals(this.number, other.number) && HidlSupport.deepEquals(this.emails, other.emails) && HidlSupport.deepEquals(this.additionalNumbers, other.additionalNumbers)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.recordId))), Integer.valueOf(HidlSupport.deepHashCode(this.name)), Integer.valueOf(HidlSupport.deepHashCode(this.number)), Integer.valueOf(HidlSupport.deepHashCode(this.emails)), Integer.valueOf(HidlSupport.deepHashCode(this.additionalNumbers)));
    }

    public final String toString() {
        return "{.recordId = " + this.recordId + ", .name = " + this.name + ", .number = " + this.number + ", .emails = " + this.emails + ", .additionalNumbers = " + this.additionalNumbers + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(72L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<PhonebookRecordInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<PhonebookRecordInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 72, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            PhonebookRecordInfo _hidl_vec_element = new PhonebookRecordInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 72);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.recordId = _hidl_blob.getInt32(_hidl_offset + 0);
        String string = _hidl_blob.getString(_hidl_offset + 8);
        this.name = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 24);
        this.number = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 40 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), _hidl_offset + 40 + 0, true);
        this.emails.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            new String();
            String _hidl_vec_element = childBlob.getString(_hidl_index_0 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element.getBytes().length + 1, childBlob.handle(), (_hidl_index_0 * 16) + 0, false);
            this.emails.add(_hidl_vec_element);
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 56 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 16, _hidl_blob.handle(), _hidl_offset + 56 + 0, true);
        this.additionalNumbers.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            new String();
            String _hidl_vec_element2 = childBlob2.getString(_hidl_index_02 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element2.getBytes().length + 1, childBlob2.handle(), (_hidl_index_02 * 16) + 0, false);
            this.additionalNumbers.add(_hidl_vec_element2);
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(72);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<PhonebookRecordInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 72);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 72);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(_hidl_offset + 0, this.recordId);
        _hidl_blob.putString(_hidl_offset + 8, this.name);
        _hidl_blob.putString(_hidl_offset + 24, this.number);
        int _hidl_vec_size = this.emails.size();
        _hidl_blob.putInt32(_hidl_offset + 40 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 40 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 16);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putString(_hidl_index_0 * 16, this.emails.get(_hidl_index_0));
        }
        _hidl_blob.putBlob(_hidl_offset + 40 + 0, childBlob);
        int _hidl_vec_size2 = this.additionalNumbers.size();
        _hidl_blob.putInt32(_hidl_offset + 56 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 56 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 16);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            childBlob2.putString(_hidl_index_02 * 16, this.additionalNumbers.get(_hidl_index_02));
        }
        _hidl_blob.putBlob(_hidl_offset + 56 + 0, childBlob2);
    }
}
