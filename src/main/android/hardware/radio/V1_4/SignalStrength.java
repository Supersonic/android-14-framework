package android.hardware.radio.V1_4;

import android.hardware.radio.V1_0.CdmaSignalStrength;
import android.hardware.radio.V1_0.EvdoSignalStrength;
import android.hardware.radio.V1_0.GsmSignalStrength;
import android.hardware.radio.V1_0.LteSignalStrength;
import android.hardware.radio.V1_2.TdscdmaSignalStrength;
import android.hardware.radio.V1_2.WcdmaSignalStrength;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class SignalStrength {
    public GsmSignalStrength gsm = new GsmSignalStrength();
    public CdmaSignalStrength cdma = new CdmaSignalStrength();
    public EvdoSignalStrength evdo = new EvdoSignalStrength();
    public LteSignalStrength lte = new LteSignalStrength();
    public TdscdmaSignalStrength tdscdma = new TdscdmaSignalStrength();
    public WcdmaSignalStrength wcdma = new WcdmaSignalStrength();

    /* renamed from: nr */
    public NrSignalStrength f180nr = new NrSignalStrength();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != SignalStrength.class) {
            return false;
        }
        SignalStrength other = (SignalStrength) otherObject;
        if (HidlSupport.deepEquals(this.gsm, other.gsm) && HidlSupport.deepEquals(this.cdma, other.cdma) && HidlSupport.deepEquals(this.evdo, other.evdo) && HidlSupport.deepEquals(this.lte, other.lte) && HidlSupport.deepEquals(this.tdscdma, other.tdscdma) && HidlSupport.deepEquals(this.wcdma, other.wcdma) && HidlSupport.deepEquals(this.f180nr, other.f180nr)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.gsm)), Integer.valueOf(HidlSupport.deepHashCode(this.cdma)), Integer.valueOf(HidlSupport.deepHashCode(this.evdo)), Integer.valueOf(HidlSupport.deepHashCode(this.lte)), Integer.valueOf(HidlSupport.deepHashCode(this.tdscdma)), Integer.valueOf(HidlSupport.deepHashCode(this.wcdma)), Integer.valueOf(HidlSupport.deepHashCode(this.f180nr)));
    }

    public final String toString() {
        return "{.gsm = " + this.gsm + ", .cdma = " + this.cdma + ", .evdo = " + this.evdo + ", .lte = " + this.lte + ", .tdscdma = " + this.tdscdma + ", .wcdma = " + this.wcdma + ", .nr = " + this.f180nr + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(108L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<SignalStrength> readVectorFromParcel(HwParcel parcel) {
        ArrayList<SignalStrength> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 108, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SignalStrength _hidl_vec_element = new SignalStrength();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 108);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.gsm.readEmbeddedFromParcel(parcel, _hidl_blob, 0 + _hidl_offset);
        this.cdma.readEmbeddedFromParcel(parcel, _hidl_blob, 12 + _hidl_offset);
        this.evdo.readEmbeddedFromParcel(parcel, _hidl_blob, 20 + _hidl_offset);
        this.lte.readEmbeddedFromParcel(parcel, _hidl_blob, 32 + _hidl_offset);
        this.tdscdma.readEmbeddedFromParcel(parcel, _hidl_blob, 56 + _hidl_offset);
        this.wcdma.readEmbeddedFromParcel(parcel, _hidl_blob, 68 + _hidl_offset);
        this.f180nr.readEmbeddedFromParcel(parcel, _hidl_blob, 84 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(108);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<SignalStrength> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 108);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 108);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        this.gsm.writeEmbeddedToBlob(_hidl_blob, 0 + _hidl_offset);
        this.cdma.writeEmbeddedToBlob(_hidl_blob, 12 + _hidl_offset);
        this.evdo.writeEmbeddedToBlob(_hidl_blob, 20 + _hidl_offset);
        this.lte.writeEmbeddedToBlob(_hidl_blob, 32 + _hidl_offset);
        this.tdscdma.writeEmbeddedToBlob(_hidl_blob, 56 + _hidl_offset);
        this.wcdma.writeEmbeddedToBlob(_hidl_blob, 68 + _hidl_offset);
        this.f180nr.writeEmbeddedToBlob(_hidl_blob, 84 + _hidl_offset);
    }
}
