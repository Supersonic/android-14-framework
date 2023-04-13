package android.hardware.radio.V1_6;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class RouteSelectionDescriptor {
    public byte precedence = 0;
    public OptionalPdpProtocolType sessionType = new OptionalPdpProtocolType();
    public OptionalSscMode sscMode = new OptionalSscMode();
    public ArrayList<SliceInfo> sliceInfo = new ArrayList<>();
    public ArrayList<String> dnn = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != RouteSelectionDescriptor.class) {
            return false;
        }
        RouteSelectionDescriptor other = (RouteSelectionDescriptor) otherObject;
        if (this.precedence == other.precedence && HidlSupport.deepEquals(this.sessionType, other.sessionType) && HidlSupport.deepEquals(this.sscMode, other.sscMode) && HidlSupport.deepEquals(this.sliceInfo, other.sliceInfo) && HidlSupport.deepEquals(this.dnn, other.dnn)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.precedence))), Integer.valueOf(HidlSupport.deepHashCode(this.sessionType)), Integer.valueOf(HidlSupport.deepHashCode(this.sscMode)), Integer.valueOf(HidlSupport.deepHashCode(this.sliceInfo)), Integer.valueOf(HidlSupport.deepHashCode(this.dnn)));
    }

    public final String toString() {
        return "{.precedence = " + ((int) this.precedence) + ", .sessionType = " + this.sessionType + ", .sscMode = " + this.sscMode + ", .sliceInfo = " + this.sliceInfo + ", .dnn = " + this.dnn + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(48L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<RouteSelectionDescriptor> readVectorFromParcel(HwParcel parcel) {
        ArrayList<RouteSelectionDescriptor> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 48, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            RouteSelectionDescriptor _hidl_vec_element = new RouteSelectionDescriptor();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 48);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.precedence = _hidl_blob.getInt8(_hidl_offset + 0);
        this.sessionType.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 4);
        this.sscMode.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 12);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 16 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 20, _hidl_blob.handle(), _hidl_offset + 16 + 0, true);
        this.sliceInfo.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            SliceInfo _hidl_vec_element = new SliceInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 20);
            this.sliceInfo.add(_hidl_vec_element);
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 32 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 16, _hidl_blob.handle(), _hidl_offset + 32 + 0, true);
        this.dnn.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            new String();
            String _hidl_vec_element2 = childBlob2.getString(_hidl_index_02 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element2.getBytes().length + 1, childBlob2.handle(), (_hidl_index_02 * 16) + 0, false);
            this.dnn.add(_hidl_vec_element2);
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(48);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<RouteSelectionDescriptor> _hidl_vec) {
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
        _hidl_blob.putInt8(_hidl_offset + 0, this.precedence);
        this.sessionType.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 4);
        this.sscMode.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 12);
        int _hidl_vec_size = this.sliceInfo.size();
        _hidl_blob.putInt32(_hidl_offset + 16 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 16 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 20);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            this.sliceInfo.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 20);
        }
        _hidl_blob.putBlob(_hidl_offset + 16 + 0, childBlob);
        int _hidl_vec_size2 = this.dnn.size();
        _hidl_blob.putInt32(_hidl_offset + 32 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 32 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 16);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            childBlob2.putString(_hidl_index_02 * 16, this.dnn.get(_hidl_index_02));
        }
        _hidl_blob.putBlob(_hidl_offset + 32 + 0, childBlob2);
    }
}
