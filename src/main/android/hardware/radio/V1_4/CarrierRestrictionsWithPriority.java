package android.hardware.radio.V1_4;

import android.hardware.radio.V1_0.Carrier;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CarrierRestrictionsWithPriority {
    public ArrayList<Carrier> allowedCarriers = new ArrayList<>();
    public ArrayList<Carrier> excludedCarriers = new ArrayList<>();
    public boolean allowedCarriersPrioritized = false;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CarrierRestrictionsWithPriority.class) {
            return false;
        }
        CarrierRestrictionsWithPriority other = (CarrierRestrictionsWithPriority) otherObject;
        if (HidlSupport.deepEquals(this.allowedCarriers, other.allowedCarriers) && HidlSupport.deepEquals(this.excludedCarriers, other.excludedCarriers) && this.allowedCarriersPrioritized == other.allowedCarriersPrioritized) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.allowedCarriers)), Integer.valueOf(HidlSupport.deepHashCode(this.excludedCarriers)), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.allowedCarriersPrioritized))));
    }

    public final String toString() {
        return "{.allowedCarriers = " + this.allowedCarriers + ", .excludedCarriers = " + this.excludedCarriers + ", .allowedCarriersPrioritized = " + this.allowedCarriersPrioritized + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(40L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CarrierRestrictionsWithPriority> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CarrierRestrictionsWithPriority> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CarrierRestrictionsWithPriority _hidl_vec_element = new CarrierRestrictionsWithPriority();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 0 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 56, _hidl_blob.handle(), _hidl_offset + 0 + 0, true);
        this.allowedCarriers.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            Carrier _hidl_vec_element = new Carrier();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 56);
            this.allowedCarriers.add(_hidl_vec_element);
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 16 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 56, _hidl_blob.handle(), _hidl_offset + 16 + 0, true);
        this.excludedCarriers.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            Carrier _hidl_vec_element2 = new Carrier();
            _hidl_vec_element2.readEmbeddedFromParcel(parcel, childBlob2, _hidl_index_02 * 56);
            this.excludedCarriers.add(_hidl_vec_element2);
        }
        this.allowedCarriersPrioritized = _hidl_blob.getBool(_hidl_offset + 32);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(40);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CarrierRestrictionsWithPriority> _hidl_vec) {
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
        int _hidl_vec_size = this.allowedCarriers.size();
        _hidl_blob.putInt32(_hidl_offset + 0 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 0 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 56);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.allowedCarriers.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 56);
        }
        _hidl_blob.putBlob(_hidl_offset + 0 + 0, childBlob);
        int _hidl_vec_size2 = this.excludedCarriers.size();
        _hidl_blob.putInt32(_hidl_offset + 16 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 16 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 56);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            this.excludedCarriers.get(_hidl_index_02).writeEmbeddedToBlob(childBlob2, _hidl_index_02 * 56);
        }
        _hidl_blob.putBlob(_hidl_offset + 16 + 0, childBlob2);
        _hidl_blob.putBool(_hidl_offset + 32, this.allowedCarriersPrioritized);
    }
}
