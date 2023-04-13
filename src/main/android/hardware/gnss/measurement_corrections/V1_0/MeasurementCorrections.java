package android.hardware.gnss.measurement_corrections.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class MeasurementCorrections {
    public double latitudeDegrees = 0.0d;
    public double longitudeDegrees = 0.0d;
    public double altitudeMeters = 0.0d;
    public double horizontalPositionUncertaintyMeters = 0.0d;
    public double verticalPositionUncertaintyMeters = 0.0d;
    public long toaGpsNanosecondsOfWeek = 0;
    public ArrayList<SingleSatCorrection> satCorrections = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != MeasurementCorrections.class) {
            return false;
        }
        MeasurementCorrections other = (MeasurementCorrections) otherObject;
        if (this.latitudeDegrees == other.latitudeDegrees && this.longitudeDegrees == other.longitudeDegrees && this.altitudeMeters == other.altitudeMeters && this.horizontalPositionUncertaintyMeters == other.horizontalPositionUncertaintyMeters && this.verticalPositionUncertaintyMeters == other.verticalPositionUncertaintyMeters && this.toaGpsNanosecondsOfWeek == other.toaGpsNanosecondsOfWeek && HidlSupport.deepEquals(this.satCorrections, other.satCorrections)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.latitudeDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.longitudeDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.altitudeMeters))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.horizontalPositionUncertaintyMeters))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.verticalPositionUncertaintyMeters))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.toaGpsNanosecondsOfWeek))), Integer.valueOf(HidlSupport.deepHashCode(this.satCorrections)));
    }

    public final String toString() {
        return "{.latitudeDegrees = " + this.latitudeDegrees + ", .longitudeDegrees = " + this.longitudeDegrees + ", .altitudeMeters = " + this.altitudeMeters + ", .horizontalPositionUncertaintyMeters = " + this.horizontalPositionUncertaintyMeters + ", .verticalPositionUncertaintyMeters = " + this.verticalPositionUncertaintyMeters + ", .toaGpsNanosecondsOfWeek = " + this.toaGpsNanosecondsOfWeek + ", .satCorrections = " + this.satCorrections + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(64L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<MeasurementCorrections> readVectorFromParcel(HwParcel parcel) {
        ArrayList<MeasurementCorrections> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 64, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            MeasurementCorrections _hidl_vec_element = new MeasurementCorrections();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 64);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.latitudeDegrees = _hidl_blob.getDouble(_hidl_offset + 0);
        this.longitudeDegrees = _hidl_blob.getDouble(_hidl_offset + 8);
        this.altitudeMeters = _hidl_blob.getDouble(_hidl_offset + 16);
        this.horizontalPositionUncertaintyMeters = _hidl_blob.getDouble(_hidl_offset + 24);
        this.verticalPositionUncertaintyMeters = _hidl_blob.getDouble(_hidl_offset + 32);
        this.toaGpsNanosecondsOfWeek = _hidl_blob.getInt64(_hidl_offset + 40);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 48 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 56, _hidl_blob.handle(), _hidl_offset + 48 + 0, true);
        this.satCorrections.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SingleSatCorrection _hidl_vec_element = new SingleSatCorrection();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 56);
            this.satCorrections.add(_hidl_vec_element);
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(64);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<MeasurementCorrections> _hidl_vec) {
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
        _hidl_blob.putDouble(_hidl_offset + 0, this.latitudeDegrees);
        _hidl_blob.putDouble(_hidl_offset + 8, this.longitudeDegrees);
        _hidl_blob.putDouble(16 + _hidl_offset, this.altitudeMeters);
        _hidl_blob.putDouble(24 + _hidl_offset, this.horizontalPositionUncertaintyMeters);
        _hidl_blob.putDouble(32 + _hidl_offset, this.verticalPositionUncertaintyMeters);
        _hidl_blob.putInt64(40 + _hidl_offset, this.toaGpsNanosecondsOfWeek);
        int _hidl_vec_size = this.satCorrections.size();
        _hidl_blob.putInt32(_hidl_offset + 48 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 48 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 56);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.satCorrections.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 56);
        }
        _hidl_blob.putBlob(48 + _hidl_offset + 0, childBlob);
    }
}
