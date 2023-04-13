package android.hardware.radio.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class ImsSmsMessage {
    public int tech = 0;
    public boolean retry = false;
    public int messageRef = 0;
    public ArrayList<CdmaSmsMessage> cdmaMessage = new ArrayList<>();
    public ArrayList<GsmSmsMessage> gsmMessage = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != ImsSmsMessage.class) {
            return false;
        }
        ImsSmsMessage other = (ImsSmsMessage) otherObject;
        if (this.tech == other.tech && this.retry == other.retry && this.messageRef == other.messageRef && HidlSupport.deepEquals(this.cdmaMessage, other.cdmaMessage) && HidlSupport.deepEquals(this.gsmMessage, other.gsmMessage)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.tech))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.retry))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.messageRef))), Integer.valueOf(HidlSupport.deepHashCode(this.cdmaMessage)), Integer.valueOf(HidlSupport.deepHashCode(this.gsmMessage)));
    }

    public final String toString() {
        return "{.tech = " + RadioTechnologyFamily.toString(this.tech) + ", .retry = " + this.retry + ", .messageRef = " + this.messageRef + ", .cdmaMessage = " + this.cdmaMessage + ", .gsmMessage = " + this.gsmMessage + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(48L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<ImsSmsMessage> readVectorFromParcel(HwParcel parcel) {
        ArrayList<ImsSmsMessage> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 48, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            ImsSmsMessage _hidl_vec_element = new ImsSmsMessage();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 48);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.tech = _hidl_blob.getInt32(_hidl_offset + 0);
        this.retry = _hidl_blob.getBool(_hidl_offset + 4);
        this.messageRef = _hidl_blob.getInt32(_hidl_offset + 8);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 16 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 88, _hidl_blob.handle(), _hidl_offset + 16 + 0, true);
        this.cdmaMessage.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CdmaSmsMessage _hidl_vec_element = new CdmaSmsMessage();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 88);
            this.cdmaMessage.add(_hidl_vec_element);
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 32 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 32, _hidl_blob.handle(), _hidl_offset + 32 + 0, true);
        this.gsmMessage.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            GsmSmsMessage _hidl_vec_element2 = new GsmSmsMessage();
            _hidl_vec_element2.readEmbeddedFromParcel(parcel, childBlob2, _hidl_index_02 * 32);
            this.gsmMessage.add(_hidl_vec_element2);
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(48);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<ImsSmsMessage> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 48);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 48);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(_hidl_offset + 0, this.tech);
        _hidl_blob.putBool(_hidl_offset + 4, this.retry);
        _hidl_blob.putInt32(_hidl_offset + 8, this.messageRef);
        int _hidl_vec_size = this.cdmaMessage.size();
        _hidl_blob.putInt32(_hidl_offset + 16 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 16 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 88);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.cdmaMessage.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 88);
        }
        _hidl_blob.putBlob(_hidl_offset + 16 + 0, childBlob);
        int _hidl_vec_size2 = this.gsmMessage.size();
        _hidl_blob.putInt32(_hidl_offset + 32 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 32 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 32);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            this.gsmMessage.get(_hidl_index_02).writeEmbeddedToBlob(childBlob2, _hidl_index_02 * 32);
        }
        _hidl_blob.putBlob(_hidl_offset + 32 + 0, childBlob2);
    }
}
