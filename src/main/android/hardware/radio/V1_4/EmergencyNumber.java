package android.hardware.radio.V1_4;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class EmergencyNumber {
    public int categories;
    public int sources;
    public String number = new String();
    public String mcc = new String();
    public String mnc = new String();
    public ArrayList<String> urns = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != EmergencyNumber.class) {
            return false;
        }
        EmergencyNumber other = (EmergencyNumber) otherObject;
        if (HidlSupport.deepEquals(this.number, other.number) && HidlSupport.deepEquals(this.mcc, other.mcc) && HidlSupport.deepEquals(this.mnc, other.mnc) && HidlSupport.deepEquals(Integer.valueOf(this.categories), Integer.valueOf(other.categories)) && HidlSupport.deepEquals(this.urns, other.urns) && HidlSupport.deepEquals(Integer.valueOf(this.sources), Integer.valueOf(other.sources))) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.number)), Integer.valueOf(HidlSupport.deepHashCode(this.mcc)), Integer.valueOf(HidlSupport.deepHashCode(this.mnc)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.categories))), Integer.valueOf(HidlSupport.deepHashCode(this.urns)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.sources))));
    }

    public final String toString() {
        return "{.number = " + this.number + ", .mcc = " + this.mcc + ", .mnc = " + this.mnc + ", .categories = " + EmergencyServiceCategory.dumpBitfield(this.categories) + ", .urns = " + this.urns + ", .sources = " + EmergencyNumberSource.dumpBitfield(this.sources) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(80L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<EmergencyNumber> readVectorFromParcel(HwParcel parcel) {
        ArrayList<EmergencyNumber> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 80, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            EmergencyNumber _hidl_vec_element = new EmergencyNumber();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 80);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        String string = _hidl_blob.getString(_hidl_offset + 0);
        this.number = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 16);
        this.mcc = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 16 + 0, false);
        String string3 = _hidl_blob.getString(_hidl_offset + 32);
        this.mnc = string3;
        parcel.readEmbeddedBuffer(string3.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 32 + 0, false);
        this.categories = _hidl_blob.getInt32(_hidl_offset + 48);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 56 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), _hidl_offset + 56 + 0, true);
        this.urns.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            new String();
            String _hidl_vec_element = childBlob.getString(_hidl_index_0 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element.getBytes().length + 1, childBlob.handle(), (_hidl_index_0 * 16) + 0, false);
            this.urns.add(_hidl_vec_element);
        }
        this.sources = _hidl_blob.getInt32(_hidl_offset + 72);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(80);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<EmergencyNumber> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 80);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 80);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putString(_hidl_offset + 0, this.number);
        _hidl_blob.putString(16 + _hidl_offset, this.mcc);
        _hidl_blob.putString(32 + _hidl_offset, this.mnc);
        _hidl_blob.putInt32(48 + _hidl_offset, this.categories);
        int _hidl_vec_size = this.urns.size();
        _hidl_blob.putInt32(_hidl_offset + 56 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 56 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 16);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putString(_hidl_index_0 * 16, this.urns.get(_hidl_index_0));
        }
        _hidl_blob.putBlob(56 + _hidl_offset + 0, childBlob);
        _hidl_blob.putInt32(72 + _hidl_offset, this.sources);
    }
}
