package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class Call {
    public int state = 0;
    public int index = 0;
    public int toa = 0;
    public boolean isMpty = false;
    public boolean isMT = false;
    public byte als = 0;
    public boolean isVoice = false;
    public boolean isVoicePrivacy = false;
    public String number = new String();
    public int numberPresentation = 0;
    public String name = new String();
    public int namePresentation = 0;
    public ArrayList<UusInfo> uusInfo = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != Call.class) {
            return false;
        }
        Call other = (Call) otherObject;
        if (this.state == other.state && this.index == other.index && this.toa == other.toa && this.isMpty == other.isMpty && this.isMT == other.isMT && this.als == other.als && this.isVoice == other.isVoice && this.isVoicePrivacy == other.isVoicePrivacy && HidlSupport.deepEquals(this.number, other.number) && this.numberPresentation == other.numberPresentation && HidlSupport.deepEquals(this.name, other.name) && this.namePresentation == other.namePresentation && HidlSupport.deepEquals(this.uusInfo, other.uusInfo)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.state))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.index))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.toa))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isMpty))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isMT))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.als))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isVoice))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isVoicePrivacy))), Integer.valueOf(HidlSupport.deepHashCode(this.number)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.numberPresentation))), Integer.valueOf(HidlSupport.deepHashCode(this.name)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.namePresentation))), Integer.valueOf(HidlSupport.deepHashCode(this.uusInfo)));
    }

    public final String toString() {
        return "{.state = " + CallState.toString(this.state) + ", .index = " + this.index + ", .toa = " + this.toa + ", .isMpty = " + this.isMpty + ", .isMT = " + this.isMT + ", .als = " + ((int) this.als) + ", .isVoice = " + this.isVoice + ", .isVoicePrivacy = " + this.isVoicePrivacy + ", .number = " + this.number + ", .numberPresentation = " + CallPresentation.toString(this.numberPresentation) + ", .name = " + this.name + ", .namePresentation = " + CallPresentation.toString(this.namePresentation) + ", .uusInfo = " + this.uusInfo + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(88L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<Call> readVectorFromParcel(HwParcel parcel) {
        ArrayList<Call> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 88, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            Call _hidl_vec_element = new Call();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 88);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.state = _hidl_blob.getInt32(_hidl_offset + 0);
        this.index = _hidl_blob.getInt32(_hidl_offset + 4);
        this.toa = _hidl_blob.getInt32(_hidl_offset + 8);
        this.isMpty = _hidl_blob.getBool(_hidl_offset + 12);
        this.isMT = _hidl_blob.getBool(_hidl_offset + 13);
        this.als = _hidl_blob.getInt8(_hidl_offset + 14);
        this.isVoice = _hidl_blob.getBool(_hidl_offset + 15);
        this.isVoicePrivacy = _hidl_blob.getBool(_hidl_offset + 16);
        String string = _hidl_blob.getString(_hidl_offset + 24);
        this.number = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
        this.numberPresentation = _hidl_blob.getInt32(_hidl_offset + 40);
        String string2 = _hidl_blob.getString(_hidl_offset + 48);
        this.name = string2;
        parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 48 + 0, false);
        this.namePresentation = _hidl_blob.getInt32(_hidl_offset + 64);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 72 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), _hidl_offset + 72 + 0, true);
        this.uusInfo.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            UusInfo _hidl_vec_element = new UusInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
            this.uusInfo.add(_hidl_vec_element);
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(88);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Call> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 88);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 88);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(_hidl_offset + 0, this.state);
        _hidl_blob.putInt32(4 + _hidl_offset, this.index);
        _hidl_blob.putInt32(_hidl_offset + 8, this.toa);
        _hidl_blob.putBool(_hidl_offset + 12, this.isMpty);
        _hidl_blob.putBool(13 + _hidl_offset, this.isMT);
        _hidl_blob.putInt8(14 + _hidl_offset, this.als);
        _hidl_blob.putBool(15 + _hidl_offset, this.isVoice);
        _hidl_blob.putBool(16 + _hidl_offset, this.isVoicePrivacy);
        _hidl_blob.putString(24 + _hidl_offset, this.number);
        _hidl_blob.putInt32(40 + _hidl_offset, this.numberPresentation);
        _hidl_blob.putString(48 + _hidl_offset, this.name);
        _hidl_blob.putInt32(64 + _hidl_offset, this.namePresentation);
        int _hidl_vec_size = this.uusInfo.size();
        _hidl_blob.putInt32(_hidl_offset + 72 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 72 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 24);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.uusInfo.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 24);
        }
        _hidl_blob.putBlob(72 + _hidl_offset + 0, childBlob);
    }
}
