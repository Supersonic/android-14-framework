package android.hardware.radio.V1_5;

import android.hardware.radio.V1_4.DataCallFailCause;
import android.hardware.radio.V1_4.DataConnActiveStatus;
import android.hardware.radio.V1_4.PdpProtocolType;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class SetupDataCallResult {
    public int cause = 0;
    public int suggestedRetryTime = 0;
    public int cid = 0;
    public int active = 0;
    public int type = 0;
    public String ifname = new String();
    public ArrayList<LinkAddress> addresses = new ArrayList<>();
    public ArrayList<String> dnses = new ArrayList<>();
    public ArrayList<String> gateways = new ArrayList<>();
    public ArrayList<String> pcscf = new ArrayList<>();
    public int mtuV4 = 0;
    public int mtuV6 = 0;

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != SetupDataCallResult.class) {
            return false;
        }
        SetupDataCallResult other = (SetupDataCallResult) otherObject;
        if (this.cause == other.cause && this.suggestedRetryTime == other.suggestedRetryTime && this.cid == other.cid && this.active == other.active && this.type == other.type && HidlSupport.deepEquals(this.ifname, other.ifname) && HidlSupport.deepEquals(this.addresses, other.addresses) && HidlSupport.deepEquals(this.dnses, other.dnses) && HidlSupport.deepEquals(this.gateways, other.gateways) && HidlSupport.deepEquals(this.pcscf, other.pcscf) && this.mtuV4 == other.mtuV4 && this.mtuV6 == other.mtuV6) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cause))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.suggestedRetryTime))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cid))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.active))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.type))), Integer.valueOf(HidlSupport.deepHashCode(this.ifname)), Integer.valueOf(HidlSupport.deepHashCode(this.addresses)), Integer.valueOf(HidlSupport.deepHashCode(this.dnses)), Integer.valueOf(HidlSupport.deepHashCode(this.gateways)), Integer.valueOf(HidlSupport.deepHashCode(this.pcscf)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.mtuV4))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.mtuV6))));
    }

    public final String toString() {
        return "{.cause = " + DataCallFailCause.toString(this.cause) + ", .suggestedRetryTime = " + this.suggestedRetryTime + ", .cid = " + this.cid + ", .active = " + DataConnActiveStatus.toString(this.active) + ", .type = " + PdpProtocolType.toString(this.type) + ", .ifname = " + this.ifname + ", .addresses = " + this.addresses + ", .dnses = " + this.dnses + ", .gateways = " + this.gateways + ", .pcscf = " + this.pcscf + ", .mtuV4 = " + this.mtuV4 + ", .mtuV6 = " + this.mtuV6 + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(112L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<SetupDataCallResult> readVectorFromParcel(HwParcel parcel) {
        ArrayList<SetupDataCallResult> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 112, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SetupDataCallResult _hidl_vec_element = new SetupDataCallResult();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 112);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.cause = _hidl_blob.getInt32(_hidl_offset + 0);
        this.suggestedRetryTime = _hidl_blob.getInt32(_hidl_offset + 4);
        this.cid = _hidl_blob.getInt32(_hidl_offset + 8);
        this.active = _hidl_blob.getInt32(_hidl_offset + 12);
        this.type = _hidl_blob.getInt32(_hidl_offset + 16);
        String string = _hidl_blob.getString(_hidl_offset + 24);
        this.ifname = string;
        parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 40 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), _hidl_offset + 40 + 0, true);
        this.addresses.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            LinkAddress _hidl_vec_element = new LinkAddress();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            this.addresses.add(_hidl_vec_element);
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 56 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 16, _hidl_blob.handle(), _hidl_offset + 56 + 0, true);
        this.dnses.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            new String();
            String _hidl_vec_element2 = childBlob2.getString(_hidl_index_02 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element2.getBytes().length + 1, childBlob2.handle(), (_hidl_index_02 * 16) + 0, false);
            this.dnses.add(_hidl_vec_element2);
        }
        int _hidl_vec_size3 = _hidl_blob.getInt32(_hidl_offset + 72 + 8);
        HwBlob childBlob3 = parcel.readEmbeddedBuffer(_hidl_vec_size3 * 16, _hidl_blob.handle(), _hidl_offset + 72 + 0, true);
        this.gateways.clear();
        for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
            new String();
            String _hidl_vec_element3 = childBlob3.getString(_hidl_index_03 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element3.getBytes().length + 1, childBlob3.handle(), (_hidl_index_03 * 16) + 0, false);
            this.gateways.add(_hidl_vec_element3);
        }
        int _hidl_vec_size4 = _hidl_blob.getInt32(_hidl_offset + 88 + 8);
        HwBlob childBlob4 = parcel.readEmbeddedBuffer(_hidl_vec_size4 * 16, _hidl_blob.handle(), _hidl_offset + 88 + 0, true);
        this.pcscf.clear();
        for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
            new String();
            String _hidl_vec_element4 = childBlob4.getString(_hidl_index_04 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element4.getBytes().length + 1, childBlob4.handle(), (_hidl_index_04 * 16) + 0, false);
            this.pcscf.add(_hidl_vec_element4);
        }
        this.mtuV4 = _hidl_blob.getInt32(_hidl_offset + 104);
        this.mtuV6 = _hidl_blob.getInt32(_hidl_offset + 108);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(112);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<SetupDataCallResult> _hidl_vec) {
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
        _hidl_blob.putInt32(_hidl_offset + 0, this.cause);
        _hidl_blob.putInt32(_hidl_offset + 4, this.suggestedRetryTime);
        _hidl_blob.putInt32(_hidl_offset + 8, this.cid);
        _hidl_blob.putInt32(_hidl_offset + 12, this.active);
        _hidl_blob.putInt32(_hidl_offset + 16, this.type);
        _hidl_blob.putString(_hidl_offset + 24, this.ifname);
        int _hidl_vec_size = this.addresses.size();
        _hidl_blob.putInt32(_hidl_offset + 40 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 40 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 40);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.addresses.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 40);
        }
        _hidl_blob.putBlob(_hidl_offset + 40 + 0, childBlob);
        int _hidl_vec_size2 = this.dnses.size();
        _hidl_blob.putInt32(_hidl_offset + 56 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 56 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 16);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            childBlob2.putString(_hidl_index_02 * 16, this.dnses.get(_hidl_index_02));
        }
        _hidl_blob.putBlob(_hidl_offset + 56 + 0, childBlob2);
        int _hidl_vec_size3 = this.gateways.size();
        _hidl_blob.putInt32(_hidl_offset + 72 + 8, _hidl_vec_size3);
        _hidl_blob.putBool(_hidl_offset + 72 + 12, false);
        HwBlob childBlob3 = new HwBlob(_hidl_vec_size3 * 16);
        for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
            childBlob3.putString(_hidl_index_03 * 16, this.gateways.get(_hidl_index_03));
        }
        _hidl_blob.putBlob(_hidl_offset + 72 + 0, childBlob3);
        int _hidl_vec_size4 = this.pcscf.size();
        _hidl_blob.putInt32(_hidl_offset + 88 + 8, _hidl_vec_size4);
        _hidl_blob.putBool(_hidl_offset + 88 + 12, false);
        HwBlob childBlob4 = new HwBlob(_hidl_vec_size4 * 16);
        for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
            childBlob4.putString(_hidl_index_04 * 16, this.pcscf.get(_hidl_index_04));
        }
        _hidl_blob.putBlob(_hidl_offset + 88 + 0, childBlob4);
        _hidl_blob.putInt32(_hidl_offset + 104, this.mtuV4);
        _hidl_blob.putInt32(_hidl_offset + 108, this.mtuV6);
    }
}
