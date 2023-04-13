package android.hardware.gnss.measurement_corrections.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class ReflectingPlane {
    public double latitudeDegrees = 0.0d;
    public double longitudeDegrees = 0.0d;
    public double altitudeMeters = 0.0d;
    public double azimuthDegrees = 0.0d;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != ReflectingPlane.class) {
            return false;
        }
        ReflectingPlane other = (ReflectingPlane) otherObject;
        if (this.latitudeDegrees == other.latitudeDegrees && this.longitudeDegrees == other.longitudeDegrees && this.altitudeMeters == other.altitudeMeters && this.azimuthDegrees == other.azimuthDegrees) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.latitudeDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.longitudeDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.altitudeMeters))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.azimuthDegrees))));
    }

    public final String toString() {
        return "{.latitudeDegrees = " + this.latitudeDegrees + ", .longitudeDegrees = " + this.longitudeDegrees + ", .altitudeMeters = " + this.altitudeMeters + ", .azimuthDegrees = " + this.azimuthDegrees + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(32L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<ReflectingPlane> readVectorFromParcel(HwParcel parcel) {
        ArrayList<ReflectingPlane> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 32, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ReflectingPlane _hidl_vec_element = new ReflectingPlane();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 32);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.latitudeDegrees = _hidl_blob.getDouble(0 + _hidl_offset);
        this.longitudeDegrees = _hidl_blob.getDouble(8 + _hidl_offset);
        this.altitudeMeters = _hidl_blob.getDouble(16 + _hidl_offset);
        this.azimuthDegrees = _hidl_blob.getDouble(24 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(32);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<ReflectingPlane> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 32);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 32);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putDouble(0 + _hidl_offset, this.latitudeDegrees);
        _hidl_blob.putDouble(8 + _hidl_offset, this.longitudeDegrees);
        _hidl_blob.putDouble(16 + _hidl_offset, this.altitudeMeters);
        _hidl_blob.putDouble(24 + _hidl_offset, this.azimuthDegrees);
    }
}
