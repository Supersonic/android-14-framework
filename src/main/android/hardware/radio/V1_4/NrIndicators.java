package android.hardware.radio.V1_4;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class NrIndicators {
    public boolean isEndcAvailable = false;
    public boolean isDcNrRestricted = false;
    public boolean isNrAvailable = false;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != NrIndicators.class) {
            return false;
        }
        NrIndicators other = (NrIndicators) otherObject;
        if (this.isEndcAvailable == other.isEndcAvailable && this.isDcNrRestricted == other.isDcNrRestricted && this.isNrAvailable == other.isNrAvailable) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isEndcAvailable))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isDcNrRestricted))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isNrAvailable))));
    }

    public final String toString() {
        return "{.isEndcAvailable = " + this.isEndcAvailable + ", .isDcNrRestricted = " + this.isDcNrRestricted + ", .isNrAvailable = " + this.isNrAvailable + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(3L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<NrIndicators> readVectorFromParcel(HwParcel parcel) {
        ArrayList<NrIndicators> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 3, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            NrIndicators _hidl_vec_element = new NrIndicators();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 3);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.isEndcAvailable = _hidl_blob.getBool(0 + _hidl_offset);
        this.isDcNrRestricted = _hidl_blob.getBool(1 + _hidl_offset);
        this.isNrAvailable = _hidl_blob.getBool(2 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(3);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<NrIndicators> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 3);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 3);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putBool(0 + _hidl_offset, this.isEndcAvailable);
        _hidl_blob.putBool(1 + _hidl_offset, this.isDcNrRestricted);
        _hidl_blob.putBool(2 + _hidl_offset, this.isNrAvailable);
    }
}
