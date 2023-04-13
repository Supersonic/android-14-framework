package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class DataProfileInfo {
    public int bearerBitmap;
    public int supportedApnTypesBitmap;
    public int profileId = 0;
    public String apn = new String();
    public String protocol = new String();
    public String roamingProtocol = new String();
    public int authType = 0;
    public String user = new String();
    public String password = new String();
    public int type = 0;
    public int maxConnsTime = 0;
    public int maxConns = 0;
    public int waitTime = 0;
    public boolean enabled = false;
    public int mtu = 0;
    public int mvnoType = 0;
    public String mvnoMatchData = new String();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != DataProfileInfo.class) {
            return false;
        }
        DataProfileInfo other = (DataProfileInfo) otherObject;
        if (this.profileId == other.profileId && HidlSupport.deepEquals(this.apn, other.apn) && HidlSupport.deepEquals(this.protocol, other.protocol) && HidlSupport.deepEquals(this.roamingProtocol, other.roamingProtocol) && this.authType == other.authType && HidlSupport.deepEquals(this.user, other.user) && HidlSupport.deepEquals(this.password, other.password) && this.type == other.type && this.maxConnsTime == other.maxConnsTime && this.maxConns == other.maxConns && this.waitTime == other.waitTime && this.enabled == other.enabled && HidlSupport.deepEquals(Integer.valueOf(this.supportedApnTypesBitmap), Integer.valueOf(other.supportedApnTypesBitmap)) && HidlSupport.deepEquals(Integer.valueOf(this.bearerBitmap), Integer.valueOf(other.bearerBitmap)) && this.mtu == other.mtu && this.mvnoType == other.mvnoType && HidlSupport.deepEquals(this.mvnoMatchData, other.mvnoMatchData)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.profileId))), Integer.valueOf(HidlSupport.deepHashCode(this.apn)), Integer.valueOf(HidlSupport.deepHashCode(this.protocol)), Integer.valueOf(HidlSupport.deepHashCode(this.roamingProtocol)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.authType))), Integer.valueOf(HidlSupport.deepHashCode(this.user)), Integer.valueOf(HidlSupport.deepHashCode(this.password)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxConnsTime))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.maxConns))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.waitTime))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.enabled))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.supportedApnTypesBitmap))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.bearerBitmap))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.mtu))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.mvnoType))), Integer.valueOf(HidlSupport.deepHashCode(this.mvnoMatchData)));
    }

    public final String toString() {
        return "{.profileId = " + DataProfileId.toString(this.profileId) + ", .apn = " + this.apn + ", .protocol = " + this.protocol + ", .roamingProtocol = " + this.roamingProtocol + ", .authType = " + ApnAuthType.toString(this.authType) + ", .user = " + this.user + ", .password = " + this.password + ", .type = " + DataProfileInfoType.toString(this.type) + ", .maxConnsTime = " + this.maxConnsTime + ", .maxConns = " + this.maxConns + ", .waitTime = " + this.waitTime + ", .enabled = " + this.enabled + ", .supportedApnTypesBitmap = " + ApnTypes.dumpBitfield(this.supportedApnTypesBitmap) + ", .bearerBitmap = " + RadioAccessFamily.dumpBitfield(this.bearerBitmap) + ", .mtu = " + this.mtu + ", .mvnoType = " + MvnoType.toString(this.mvnoType) + ", .mvnoMatchData = " + this.mvnoMatchData + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(152L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<DataProfileInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<DataProfileInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 152, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            DataProfileInfo _hidl_vec_element = new DataProfileInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 152);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.profileId = _hidl_blob.getInt32(_hidl_offset + 0);
        String string = _hidl_blob.getString(_hidl_offset + 8);
        this.apn = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 8 + 0, false);
        String string2 = _hidl_blob.getString(_hidl_offset + 24);
        this.protocol = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
        String string3 = _hidl_blob.getString(_hidl_offset + 40);
        this.roamingProtocol = string3;
        parcel.readEmbeddedBuffer(string3.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 40 + 0, false);
        this.authType = _hidl_blob.getInt32(_hidl_offset + 56);
        String string4 = _hidl_blob.getString(_hidl_offset + 64);
        this.user = string4;
        parcel.readEmbeddedBuffer(string4.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 64 + 0, false);
        String string5 = _hidl_blob.getString(_hidl_offset + 80);
        this.password = string5;
        parcel.readEmbeddedBuffer(string5.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 80 + 0, false);
        this.type = _hidl_blob.getInt32(_hidl_offset + 96);
        this.maxConnsTime = _hidl_blob.getInt32(_hidl_offset + 100);
        this.maxConns = _hidl_blob.getInt32(_hidl_offset + 104);
        this.waitTime = _hidl_blob.getInt32(_hidl_offset + 108);
        this.enabled = _hidl_blob.getBool(_hidl_offset + 112);
        this.supportedApnTypesBitmap = _hidl_blob.getInt32(_hidl_offset + 116);
        this.bearerBitmap = _hidl_blob.getInt32(_hidl_offset + 120);
        this.mtu = _hidl_blob.getInt32(_hidl_offset + 124);
        this.mvnoType = _hidl_blob.getInt32(_hidl_offset + 128);
        String string6 = _hidl_blob.getString(_hidl_offset + 136);
        this.mvnoMatchData = string6;
        parcel.readEmbeddedBuffer(string6.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 136 + 0, false);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(152);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<DataProfileInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 152);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 152);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.profileId);
        _hidl_blob.putString(8 + _hidl_offset, this.apn);
        _hidl_blob.putString(24 + _hidl_offset, this.protocol);
        _hidl_blob.putString(40 + _hidl_offset, this.roamingProtocol);
        _hidl_blob.putInt32(56 + _hidl_offset, this.authType);
        _hidl_blob.putString(64 + _hidl_offset, this.user);
        _hidl_blob.putString(80 + _hidl_offset, this.password);
        _hidl_blob.putInt32(96 + _hidl_offset, this.type);
        _hidl_blob.putInt32(100 + _hidl_offset, this.maxConnsTime);
        _hidl_blob.putInt32(104 + _hidl_offset, this.maxConns);
        _hidl_blob.putInt32(108 + _hidl_offset, this.waitTime);
        _hidl_blob.putBool(112 + _hidl_offset, this.enabled);
        _hidl_blob.putInt32(116 + _hidl_offset, this.supportedApnTypesBitmap);
        _hidl_blob.putInt32(120 + _hidl_offset, this.bearerBitmap);
        _hidl_blob.putInt32(124 + _hidl_offset, this.mtu);
        _hidl_blob.putInt32(128 + _hidl_offset, this.mvnoType);
        _hidl_blob.putString(136 + _hidl_offset, this.mvnoMatchData);
    }
}
