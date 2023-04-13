package android.hardware.radio.V1_2;

import android.hardware.radio.V1_0.CellInfoType;
import android.hardware.radio.V1_0.TimeStampType;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CellInfo {
    public int cellInfoType = 0;
    public boolean registered = false;
    public int timeStampType = 0;
    public long timeStamp = 0;
    public ArrayList<CellInfoGsm> gsm = new ArrayList<>();
    public ArrayList<CellInfoCdma> cdma = new ArrayList<>();
    public ArrayList<CellInfoLte> lte = new ArrayList<>();
    public ArrayList<CellInfoWcdma> wcdma = new ArrayList<>();
    public ArrayList<CellInfoTdscdma> tdscdma = new ArrayList<>();
    public int connectionStatus = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != CellInfo.class) {
            return false;
        }
        CellInfo other = (CellInfo) otherObject;
        if (this.cellInfoType == other.cellInfoType && this.registered == other.registered && this.timeStampType == other.timeStampType && this.timeStamp == other.timeStamp && HidlSupport.deepEquals(this.gsm, other.gsm) && HidlSupport.deepEquals(this.cdma, other.cdma) && HidlSupport.deepEquals(this.lte, other.lte) && HidlSupport.deepEquals(this.wcdma, other.wcdma) && HidlSupport.deepEquals(this.tdscdma, other.tdscdma) && this.connectionStatus == other.connectionStatus) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cellInfoType))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.registered))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.timeStampType))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.timeStamp))), Integer.valueOf(HidlSupport.deepHashCode(this.gsm)), Integer.valueOf(HidlSupport.deepHashCode(this.cdma)), Integer.valueOf(HidlSupport.deepHashCode(this.lte)), Integer.valueOf(HidlSupport.deepHashCode(this.wcdma)), Integer.valueOf(HidlSupport.deepHashCode(this.tdscdma)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.connectionStatus))));
    }

    public final String toString() {
        return "{.cellInfoType = " + CellInfoType.toString(this.cellInfoType) + ", .registered = " + this.registered + ", .timeStampType = " + TimeStampType.toString(this.timeStampType) + ", .timeStamp = " + this.timeStamp + ", .gsm = " + this.gsm + ", .cdma = " + this.cdma + ", .lte = " + this.lte + ", .wcdma = " + this.wcdma + ", .tdscdma = " + this.tdscdma + ", .connectionStatus = " + CellConnectionStatus.toString(this.connectionStatus) + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(112L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CellInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CellInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 112, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CellInfo _hidl_vec_element = new CellInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 112);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.cellInfoType = _hidl_blob.getInt32(_hidl_offset + 0);
        this.registered = _hidl_blob.getBool(_hidl_offset + 4);
        this.timeStampType = _hidl_blob.getInt32(_hidl_offset + 8);
        this.timeStamp = _hidl_blob.getInt64(_hidl_offset + 16);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 24 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 96, _hidl_blob.handle(), _hidl_offset + 24 + 0, true);
        this.gsm.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CellInfoGsm _hidl_vec_element = new CellInfoGsm();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 96);
            this.gsm.add(_hidl_vec_element);
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 40 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 80, _hidl_blob.handle(), _hidl_offset + 40 + 0, true);
        this.cdma.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            CellInfoCdma _hidl_vec_element2 = new CellInfoCdma();
            _hidl_vec_element2.readEmbeddedFromParcel(parcel, childBlob2, _hidl_index_02 * 80);
            this.cdma.add(_hidl_vec_element2);
        }
        int _hidl_vec_size3 = _hidl_blob.getInt32(_hidl_offset + 56 + 8);
        HwBlob childBlob3 = parcel.readEmbeddedBuffer(_hidl_vec_size3 * 112, _hidl_blob.handle(), _hidl_offset + 56 + 0, true);
        this.lte.clear();
        for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
            CellInfoLte _hidl_vec_element3 = new CellInfoLte();
            _hidl_vec_element3.readEmbeddedFromParcel(parcel, childBlob3, _hidl_index_03 * 112);
            this.lte.add(_hidl_vec_element3);
        }
        int _hidl_vec_size4 = _hidl_blob.getInt32(_hidl_offset + 72 + 8);
        HwBlob childBlob4 = parcel.readEmbeddedBuffer(_hidl_vec_size4 * 96, _hidl_blob.handle(), _hidl_offset + 72 + 0, true);
        this.wcdma.clear();
        for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
            CellInfoWcdma _hidl_vec_element4 = new CellInfoWcdma();
            _hidl_vec_element4.readEmbeddedFromParcel(parcel, childBlob4, _hidl_index_04 * 96);
            this.wcdma.add(_hidl_vec_element4);
        }
        int _hidl_vec_size5 = _hidl_blob.getInt32(_hidl_offset + 88 + 8);
        HwBlob childBlob5 = parcel.readEmbeddedBuffer(_hidl_vec_size5 * 104, _hidl_blob.handle(), _hidl_offset + 88 + 0, true);
        this.tdscdma.clear();
        for (int _hidl_index_05 = 0; _hidl_index_05 < _hidl_vec_size5; _hidl_index_05++) {
            CellInfoTdscdma _hidl_vec_element5 = new CellInfoTdscdma();
            _hidl_vec_element5.readEmbeddedFromParcel(parcel, childBlob5, _hidl_index_05 * 104);
            this.tdscdma.add(_hidl_vec_element5);
        }
        this.connectionStatus = _hidl_blob.getInt32(_hidl_offset + 104);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(112);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CellInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 112);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 112);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(_hidl_offset + 0, this.cellInfoType);
        _hidl_blob.putBool(_hidl_offset + 4, this.registered);
        _hidl_blob.putInt32(_hidl_offset + 8, this.timeStampType);
        _hidl_blob.putInt64(_hidl_offset + 16, this.timeStamp);
        int _hidl_vec_size = this.gsm.size();
        _hidl_blob.putInt32(_hidl_offset + 24 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 24 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 96);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.gsm.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 96);
        }
        _hidl_blob.putBlob(_hidl_offset + 24 + 0, childBlob);
        int _hidl_vec_size2 = this.cdma.size();
        _hidl_blob.putInt32(_hidl_offset + 40 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 40 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 80);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            this.cdma.get(_hidl_index_02).writeEmbeddedToBlob(childBlob2, _hidl_index_02 * 80);
        }
        _hidl_blob.putBlob(_hidl_offset + 40 + 0, childBlob2);
        int _hidl_vec_size3 = this.lte.size();
        _hidl_blob.putInt32(_hidl_offset + 56 + 8, _hidl_vec_size3);
        _hidl_blob.putBool(_hidl_offset + 56 + 12, false);
        HwBlob childBlob3 = new HwBlob(_hidl_vec_size3 * 112);
        for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
            this.lte.get(_hidl_index_03).writeEmbeddedToBlob(childBlob3, _hidl_index_03 * 112);
        }
        _hidl_blob.putBlob(_hidl_offset + 56 + 0, childBlob3);
        int _hidl_vec_size4 = this.wcdma.size();
        _hidl_blob.putInt32(_hidl_offset + 72 + 8, _hidl_vec_size4);
        _hidl_blob.putBool(_hidl_offset + 72 + 12, false);
        HwBlob childBlob4 = new HwBlob(_hidl_vec_size4 * 96);
        for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
            this.wcdma.get(_hidl_index_04).writeEmbeddedToBlob(childBlob4, _hidl_index_04 * 96);
        }
        _hidl_blob.putBlob(_hidl_offset + 72 + 0, childBlob4);
        int _hidl_vec_size5 = this.tdscdma.size();
        _hidl_blob.putInt32(_hidl_offset + 88 + 8, _hidl_vec_size5);
        _hidl_blob.putBool(_hidl_offset + 88 + 12, false);
        HwBlob childBlob5 = new HwBlob(_hidl_vec_size5 * 104);
        for (int _hidl_index_05 = 0; _hidl_index_05 < _hidl_vec_size5; _hidl_index_05++) {
            this.tdscdma.get(_hidl_index_05).writeEmbeddedToBlob(childBlob5, _hidl_index_05 * 104);
        }
        _hidl_blob.putBlob(_hidl_offset + 88 + 0, childBlob5);
        _hidl_blob.putInt32(_hidl_offset + 104, this.connectionStatus);
    }
}
