package android.hardware.contexthub.V1_0;

import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public final class NanoAppBinary {
    public int flags;
    public long appId = 0;
    public int appVersion = 0;
    public byte targetChreApiMajorVersion = 0;
    public byte targetChreApiMinorVersion = 0;
    public ArrayList<Byte> customBinary = new ArrayList<>();

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != NanoAppBinary.class) {
            return false;
        }
        NanoAppBinary other = (NanoAppBinary) otherObject;
        if (this.appId == other.appId && this.appVersion == other.appVersion && HidlSupport.deepEquals(Integer.valueOf(this.flags), Integer.valueOf(other.flags)) && this.targetChreApiMajorVersion == other.targetChreApiMajorVersion && this.targetChreApiMinorVersion == other.targetChreApiMinorVersion && HidlSupport.deepEquals(this.customBinary, other.customBinary)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.appId))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.appVersion))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.flags))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.targetChreApiMajorVersion))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.targetChreApiMinorVersion))), Integer.valueOf(HidlSupport.deepHashCode(this.customBinary)));
    }

    public final String toString() {
        return "{.appId = " + this.appId + ", .appVersion = " + this.appVersion + ", .flags = " + NanoAppFlags.dumpBitfield(this.flags) + ", .targetChreApiMajorVersion = " + ((int) this.targetChreApiMajorVersion) + ", .targetChreApiMinorVersion = " + ((int) this.targetChreApiMinorVersion) + ", .customBinary = " + this.customBinary + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(40L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<NanoAppBinary> readVectorFromParcel(HwParcel parcel) {
        ArrayList<NanoAppBinary> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            NanoAppBinary _hidl_vec_element = new NanoAppBinary();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.appId = _hidl_blob.getInt64(_hidl_offset + 0);
        this.appVersion = _hidl_blob.getInt32(_hidl_offset + 8);
        this.flags = _hidl_blob.getInt32(_hidl_offset + 12);
        this.targetChreApiMajorVersion = _hidl_blob.getInt8(_hidl_offset + 16);
        this.targetChreApiMinorVersion = _hidl_blob.getInt8(_hidl_offset + 17);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 24 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, true);
        this.customBinary.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            byte _hidl_vec_element = childBlob.getInt8(_hidl_index_0 * 1);
            this.customBinary.add(Byte.valueOf(_hidl_vec_element));
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(40);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<NanoAppBinary> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 40);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 40);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt64(_hidl_offset + 0, this.appId);
        _hidl_blob.putInt32(_hidl_offset + 8, this.appVersion);
        _hidl_blob.putInt32(_hidl_offset + 12, this.flags);
        _hidl_blob.putInt8(16 + _hidl_offset, this.targetChreApiMajorVersion);
        _hidl_blob.putInt8(17 + _hidl_offset, this.targetChreApiMinorVersion);
        int _hidl_vec_size = this.customBinary.size();
        _hidl_blob.putInt32(_hidl_offset + 24 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 24 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 1);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt8(_hidl_index_0 * 1, this.customBinary.get(_hidl_index_0).byteValue());
        }
        _hidl_blob.putBlob(24 + _hidl_offset + 0, childBlob);
    }
}
