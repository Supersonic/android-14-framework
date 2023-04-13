package android.hardware.gnss.measurement_corrections.V1_0;

import android.hardware.gnss.V1_0.GnssConstellationType;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class SingleSatCorrection {
    public short singleSatCorrectionFlags;
    public byte constellation = 0;
    public short svid = 0;
    public float carrierFrequencyHz = 0.0f;
    public float probSatIsLos = 0.0f;
    public float excessPathLengthMeters = 0.0f;
    public float excessPathLengthUncertaintyMeters = 0.0f;
    public ReflectingPlane reflectingPlane = new ReflectingPlane();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != SingleSatCorrection.class) {
            return false;
        }
        SingleSatCorrection other = (SingleSatCorrection) otherObject;
        if (HidlSupport.deepEquals(Short.valueOf(this.singleSatCorrectionFlags), Short.valueOf(other.singleSatCorrectionFlags)) && this.constellation == other.constellation && this.svid == other.svid && this.carrierFrequencyHz == other.carrierFrequencyHz && this.probSatIsLos == other.probSatIsLos && this.excessPathLengthMeters == other.excessPathLengthMeters && this.excessPathLengthUncertaintyMeters == other.excessPathLengthUncertaintyMeters && HidlSupport.deepEquals(this.reflectingPlane, other.reflectingPlane)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.singleSatCorrectionFlags))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.constellation))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.svid))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.carrierFrequencyHz))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.probSatIsLos))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.excessPathLengthMeters))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.excessPathLengthUncertaintyMeters))), Integer.valueOf(HidlSupport.deepHashCode(this.reflectingPlane)));
    }

    public final String toString() {
        return "{.singleSatCorrectionFlags = " + GnssSingleSatCorrectionFlags.dumpBitfield(this.singleSatCorrectionFlags) + ", .constellation = " + GnssConstellationType.toString(this.constellation) + ", .svid = " + ((int) this.svid) + ", .carrierFrequencyHz = " + this.carrierFrequencyHz + ", .probSatIsLos = " + this.probSatIsLos + ", .excessPathLengthMeters = " + this.excessPathLengthMeters + ", .excessPathLengthUncertaintyMeters = " + this.excessPathLengthUncertaintyMeters + ", .reflectingPlane = " + this.reflectingPlane + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(56L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<SingleSatCorrection> readVectorFromParcel(HwParcel parcel) {
        ArrayList<SingleSatCorrection> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 56, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SingleSatCorrection _hidl_vec_element = new SingleSatCorrection();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 56);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.singleSatCorrectionFlags = _hidl_blob.getInt16(0 + _hidl_offset);
        this.constellation = _hidl_blob.getInt8(2 + _hidl_offset);
        this.svid = _hidl_blob.getInt16(4 + _hidl_offset);
        this.carrierFrequencyHz = _hidl_blob.getFloat(8 + _hidl_offset);
        this.probSatIsLos = _hidl_blob.getFloat(12 + _hidl_offset);
        this.excessPathLengthMeters = _hidl_blob.getFloat(16 + _hidl_offset);
        this.excessPathLengthUncertaintyMeters = _hidl_blob.getFloat(20 + _hidl_offset);
        this.reflectingPlane.readEmbeddedFromParcel(parcel, _hidl_blob, 24 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(56);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<SingleSatCorrection> _hidl_vec) {
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
        _hidl_blob.putInt16(0 + _hidl_offset, this.singleSatCorrectionFlags);
        _hidl_blob.putInt8(2 + _hidl_offset, this.constellation);
        _hidl_blob.putInt16(4 + _hidl_offset, this.svid);
        _hidl_blob.putFloat(8 + _hidl_offset, this.carrierFrequencyHz);
        _hidl_blob.putFloat(12 + _hidl_offset, this.probSatIsLos);
        _hidl_blob.putFloat(16 + _hidl_offset, this.excessPathLengthMeters);
        _hidl_blob.putFloat(20 + _hidl_offset, this.excessPathLengthUncertaintyMeters);
        this.reflectingPlane.writeEmbeddedToBlob(_hidl_blob, 24 + _hidl_offset);
    }
}
