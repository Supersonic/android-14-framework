package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class SignalStrength {

    /* renamed from: gw */
    public GsmSignalStrength f171gw = new GsmSignalStrength();
    public CdmaSignalStrength cdma = new CdmaSignalStrength();
    public EvdoSignalStrength evdo = new EvdoSignalStrength();
    public LteSignalStrength lte = new LteSignalStrength();
    public TdScdmaSignalStrength tdScdma = new TdScdmaSignalStrength();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != SignalStrength.class) {
            return false;
        }
        SignalStrength other = (SignalStrength) otherObject;
        if (HidlSupport.deepEquals(this.f171gw, other.f171gw) && HidlSupport.deepEquals(this.cdma, other.cdma) && HidlSupport.deepEquals(this.evdo, other.evdo) && HidlSupport.deepEquals(this.lte, other.lte) && HidlSupport.deepEquals(this.tdScdma, other.tdScdma)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.f171gw)), Integer.valueOf(HidlSupport.deepHashCode(this.cdma)), Integer.valueOf(HidlSupport.deepHashCode(this.evdo)), Integer.valueOf(HidlSupport.deepHashCode(this.lte)), Integer.valueOf(HidlSupport.deepHashCode(this.tdScdma)));
    }

    public final String toString() {
        return "{.gw = " + this.f171gw + ", .cdma = " + this.cdma + ", .evdo = " + this.evdo + ", .lte = " + this.lte + ", .tdScdma = " + this.tdScdma + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(60L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<SignalStrength> readVectorFromParcel(HwParcel parcel) {
        ArrayList<SignalStrength> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 60, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SignalStrength _hidl_vec_element = new SignalStrength();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 60);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.f171gw.readEmbeddedFromParcel(parcel, _hidl_blob, 0 + _hidl_offset);
        this.cdma.readEmbeddedFromParcel(parcel, _hidl_blob, 12 + _hidl_offset);
        this.evdo.readEmbeddedFromParcel(parcel, _hidl_blob, 20 + _hidl_offset);
        this.lte.readEmbeddedFromParcel(parcel, _hidl_blob, 32 + _hidl_offset);
        this.tdScdma.readEmbeddedFromParcel(parcel, _hidl_blob, 56 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(60);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<SignalStrength> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 60);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 60);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        this.f171gw.writeEmbeddedToBlob(_hidl_blob, 0 + _hidl_offset);
        this.cdma.writeEmbeddedToBlob(_hidl_blob, 12 + _hidl_offset);
        this.evdo.writeEmbeddedToBlob(_hidl_blob, 20 + _hidl_offset);
        this.lte.writeEmbeddedToBlob(_hidl_blob, 32 + _hidl_offset);
        this.tdScdma.writeEmbeddedToBlob(_hidl_blob, 56 + _hidl_offset);
    }
}
