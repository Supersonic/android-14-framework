package android.hardware.radio.V1_1;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class RadioAccessSpecifier {
    public int radioAccessNetwork = 0;
    public ArrayList<Integer> geranBands = new ArrayList<>();
    public ArrayList<Integer> utranBands = new ArrayList<>();
    public ArrayList<Integer> eutranBands = new ArrayList<>();
    public ArrayList<Integer> channels = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != RadioAccessSpecifier.class) {
            return false;
        }
        RadioAccessSpecifier other = (RadioAccessSpecifier) otherObject;
        if (this.radioAccessNetwork == other.radioAccessNetwork && HidlSupport.deepEquals(this.geranBands, other.geranBands) && HidlSupport.deepEquals(this.utranBands, other.utranBands) && HidlSupport.deepEquals(this.eutranBands, other.eutranBands) && HidlSupport.deepEquals(this.channels, other.channels)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.radioAccessNetwork))), Integer.valueOf(HidlSupport.deepHashCode(this.geranBands)), Integer.valueOf(HidlSupport.deepHashCode(this.utranBands)), Integer.valueOf(HidlSupport.deepHashCode(this.eutranBands)), Integer.valueOf(HidlSupport.deepHashCode(this.channels)));
    }

    public final String toString() {
        return "{.radioAccessNetwork = " + RadioAccessNetworks.toString(this.radioAccessNetwork) + ", .geranBands = " + this.geranBands + ", .utranBands = " + this.utranBands + ", .eutranBands = " + this.eutranBands + ", .channels = " + this.channels + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(72L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<RadioAccessSpecifier> readVectorFromParcel(HwParcel parcel) {
        ArrayList<RadioAccessSpecifier> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 72, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            RadioAccessSpecifier _hidl_vec_element = new RadioAccessSpecifier();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 72);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.radioAccessNetwork = _hidl_blob.getInt32(_hidl_offset + 0);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 8 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 4, _hidl_blob.handle(), _hidl_offset + 8 + 0, true);
        this.geranBands.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            int _hidl_vec_element = childBlob.getInt32(_hidl_index_0 * 4);
            this.geranBands.add(Integer.valueOf(_hidl_vec_element));
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 24 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 4, _hidl_blob.handle(), _hidl_offset + 24 + 0, true);
        this.utranBands.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            int _hidl_vec_element2 = childBlob2.getInt32(_hidl_index_02 * 4);
            this.utranBands.add(Integer.valueOf(_hidl_vec_element2));
        }
        int _hidl_vec_size3 = _hidl_blob.getInt32(_hidl_offset + 40 + 8);
        HwBlob childBlob3 = parcel.readEmbeddedBuffer(_hidl_vec_size3 * 4, _hidl_blob.handle(), _hidl_offset + 40 + 0, true);
        this.eutranBands.clear();
        for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
            int _hidl_vec_element3 = childBlob3.getInt32(_hidl_index_03 * 4);
            this.eutranBands.add(Integer.valueOf(_hidl_vec_element3));
        }
        int _hidl_vec_size4 = _hidl_blob.getInt32(_hidl_offset + 56 + 8);
        HwBlob childBlob4 = parcel.readEmbeddedBuffer(_hidl_vec_size4 * 4, _hidl_blob.handle(), _hidl_offset + 56 + 0, true);
        this.channels.clear();
        for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
            int _hidl_vec_element4 = childBlob4.getInt32(_hidl_index_04 * 4);
            this.channels.add(Integer.valueOf(_hidl_vec_element4));
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(72);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<RadioAccessSpecifier> _hidl_vec) {
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
        _hidl_blob.putInt32(_hidl_offset + 0, this.radioAccessNetwork);
        int _hidl_vec_size = this.geranBands.size();
        _hidl_blob.putInt32(_hidl_offset + 8 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 8 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 4);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt32(_hidl_index_0 * 4, this.geranBands.get(_hidl_index_0).intValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 8 + 0, childBlob);
        int _hidl_vec_size2 = this.utranBands.size();
        _hidl_blob.putInt32(_hidl_offset + 24 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 24 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 4);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            childBlob2.putInt32(_hidl_index_02 * 4, this.utranBands.get(_hidl_index_02).intValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 24 + 0, childBlob2);
        int _hidl_vec_size3 = this.eutranBands.size();
        _hidl_blob.putInt32(_hidl_offset + 40 + 8, _hidl_vec_size3);
        _hidl_blob.putBool(_hidl_offset + 40 + 12, false);
        HwBlob childBlob3 = new HwBlob(_hidl_vec_size3 * 4);
        for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
            childBlob3.putInt32(_hidl_index_03 * 4, this.eutranBands.get(_hidl_index_03).intValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 40 + 0, childBlob3);
        int _hidl_vec_size4 = this.channels.size();
        _hidl_blob.putInt32(_hidl_offset + 56 + 8, _hidl_vec_size4);
        _hidl_blob.putBool(_hidl_offset + 56 + 12, false);
        HwBlob childBlob4 = new HwBlob(_hidl_vec_size4 * 4);
        for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
            childBlob4.putInt32(_hidl_index_04 * 4, this.channels.get(_hidl_index_04).intValue());
        }
        _hidl_blob.putBlob(_hidl_offset + 56 + 0, childBlob4);
    }
}
