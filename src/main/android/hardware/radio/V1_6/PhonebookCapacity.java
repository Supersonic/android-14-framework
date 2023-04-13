package android.hardware.radio.V1_6;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class PhonebookCapacity {
    public int maxAdnRecords = 0;
    public int usedAdnRecords = 0;
    public int maxEmailRecords = 0;
    public int usedEmailRecords = 0;
    public int maxAdditionalNumberRecords = 0;
    public int usedAdditionalNumberRecords = 0;
    public int maxNameLen = 0;
    public int maxNumberLen = 0;
    public int maxEmailLen = 0;
    public int maxAdditionalNumberLen = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != PhonebookCapacity.class) {
            return false;
        }
        PhonebookCapacity other = (PhonebookCapacity) otherObject;
        if (this.maxAdnRecords == other.maxAdnRecords && this.usedAdnRecords == other.usedAdnRecords && this.maxEmailRecords == other.maxEmailRecords && this.usedEmailRecords == other.usedEmailRecords && this.maxAdditionalNumberRecords == other.maxAdditionalNumberRecords && this.usedAdditionalNumberRecords == other.usedAdditionalNumberRecords && this.maxNameLen == other.maxNameLen && this.maxNumberLen == other.maxNumberLen && this.maxEmailLen == other.maxEmailLen && this.maxAdditionalNumberLen == other.maxAdditionalNumberLen) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxAdnRecords))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.usedAdnRecords))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxEmailRecords))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.usedEmailRecords))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxAdditionalNumberRecords))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.usedAdditionalNumberRecords))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxNameLen))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxNumberLen))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxEmailLen))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxAdditionalNumberLen))));
    }

    public final String toString() {
        return "{.maxAdnRecords = " + this.maxAdnRecords + ", .usedAdnRecords = " + this.usedAdnRecords + ", .maxEmailRecords = " + this.maxEmailRecords + ", .usedEmailRecords = " + this.usedEmailRecords + ", .maxAdditionalNumberRecords = " + this.maxAdditionalNumberRecords + ", .usedAdditionalNumberRecords = " + this.usedAdditionalNumberRecords + ", .maxNameLen = " + this.maxNameLen + ", .maxNumberLen = " + this.maxNumberLen + ", .maxEmailLen = " + this.maxEmailLen + ", .maxAdditionalNumberLen = " + this.maxAdditionalNumberLen + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(40L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<PhonebookCapacity> readVectorFromParcel(HwParcel parcel) {
        ArrayList<PhonebookCapacity> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            PhonebookCapacity _hidl_vec_element = new PhonebookCapacity();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.maxAdnRecords = _hidl_blob.getInt32(0 + _hidl_offset);
        this.usedAdnRecords = _hidl_blob.getInt32(4 + _hidl_offset);
        this.maxEmailRecords = _hidl_blob.getInt32(8 + _hidl_offset);
        this.usedEmailRecords = _hidl_blob.getInt32(12 + _hidl_offset);
        this.maxAdditionalNumberRecords = _hidl_blob.getInt32(16 + _hidl_offset);
        this.usedAdditionalNumberRecords = _hidl_blob.getInt32(20 + _hidl_offset);
        this.maxNameLen = _hidl_blob.getInt32(24 + _hidl_offset);
        this.maxNumberLen = _hidl_blob.getInt32(28 + _hidl_offset);
        this.maxEmailLen = _hidl_blob.getInt32(32 + _hidl_offset);
        this.maxAdditionalNumberLen = _hidl_blob.getInt32(36 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(40);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<PhonebookCapacity> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.maxAdnRecords);
        _hidl_blob.putInt32(4 + _hidl_offset, this.usedAdnRecords);
        _hidl_blob.putInt32(8 + _hidl_offset, this.maxEmailRecords);
        _hidl_blob.putInt32(12 + _hidl_offset, this.usedEmailRecords);
        _hidl_blob.putInt32(16 + _hidl_offset, this.maxAdditionalNumberRecords);
        _hidl_blob.putInt32(20 + _hidl_offset, this.usedAdditionalNumberRecords);
        _hidl_blob.putInt32(24 + _hidl_offset, this.maxNameLen);
        _hidl_blob.putInt32(28 + _hidl_offset, this.maxNumberLen);
        _hidl_blob.putInt32(32 + _hidl_offset, this.maxEmailLen);
        _hidl_blob.putInt32(36 + _hidl_offset, this.maxAdditionalNumberLen);
    }
}
