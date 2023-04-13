package android.hardware.gnss.measurement_corrections.V1_1;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class MeasurementCorrections {
    public android.hardware.gnss.measurement_corrections.V1_0.MeasurementCorrections v1_0 = new android.hardware.gnss.measurement_corrections.V1_0.MeasurementCorrections();
    public boolean hasEnvironmentBearing = false;
    public float environmentBearingDegrees = 0.0f;
    public float environmentBearingUncertaintyDegrees = 0.0f;
    public ArrayList<SingleSatCorrection> satCorrections = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != MeasurementCorrections.class) {
            return false;
        }
        MeasurementCorrections other = (MeasurementCorrections) otherObject;
        if (HidlSupport.deepEquals(this.v1_0, other.v1_0) && this.hasEnvironmentBearing == other.hasEnvironmentBearing && this.environmentBearingDegrees == other.environmentBearingDegrees && this.environmentBearingUncertaintyDegrees == other.environmentBearingUncertaintyDegrees && HidlSupport.deepEquals(this.satCorrections, other.satCorrections)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.v1_0)), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.hasEnvironmentBearing))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.environmentBearingDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.environmentBearingUncertaintyDegrees))), Integer.valueOf(HidlSupport.deepHashCode(this.satCorrections)));
    }

    public final String toString() {
        return "{.v1_0 = " + this.v1_0 + ", .hasEnvironmentBearing = " + this.hasEnvironmentBearing + ", .environmentBearingDegrees = " + this.environmentBearingDegrees + ", .environmentBearingUncertaintyDegrees = " + this.environmentBearingUncertaintyDegrees + ", .satCorrections = " + this.satCorrections + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(96L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<MeasurementCorrections> readVectorFromParcel(HwParcel parcel) {
        ArrayList<MeasurementCorrections> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 96, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            MeasurementCorrections _hidl_vec_element = new MeasurementCorrections();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 96);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.v1_0.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 0);
        this.hasEnvironmentBearing = _hidl_blob.getBool(_hidl_offset + 64);
        this.environmentBearingDegrees = _hidl_blob.getFloat(_hidl_offset + 68);
        this.environmentBearingUncertaintyDegrees = _hidl_blob.getFloat(_hidl_offset + 72);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 80 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 64, _hidl_blob.handle(), _hidl_offset + 80 + 0, true);
        this.satCorrections.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SingleSatCorrection _hidl_vec_element = new SingleSatCorrection();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 64);
            this.satCorrections.add(_hidl_vec_element);
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(96);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<MeasurementCorrections> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 96);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 96);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        this.v1_0.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 0);
        _hidl_blob.putBool(64 + _hidl_offset, this.hasEnvironmentBearing);
        _hidl_blob.putFloat(68 + _hidl_offset, this.environmentBearingDegrees);
        _hidl_blob.putFloat(72 + _hidl_offset, this.environmentBearingUncertaintyDegrees);
        int _hidl_vec_size = this.satCorrections.size();
        _hidl_blob.putInt32(_hidl_offset + 80 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 80 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 64);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.satCorrections.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 64);
        }
        _hidl_blob.putBlob(80 + _hidl_offset + 0, childBlob);
    }
}
