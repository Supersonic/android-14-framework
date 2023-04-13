package android.hardware.radio.V1_6;

import android.internal.hidl.safe_union.V1_0.Monostate;
import android.media.MediaMetrics;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class OptionalSliceInfo {
    private byte hidl_d = 0;
    private Object hidl_o;

    public OptionalSliceInfo() {
        this.hidl_o = null;
        this.hidl_o = new Monostate();
    }

    /* loaded from: classes2.dex */
    public static final class hidl_discriminator {
        public static final byte noinit = 0;
        public static final byte value = 1;

        public static final String getName(byte value2) {
            switch (value2) {
                case 0:
                    return "noinit";
                case 1:
                    return "value";
                default:
                    return "Unknown";
            }
        }

        private hidl_discriminator() {
        }
    }

    public void noinit(Monostate noinit) {
        this.hidl_d = (byte) 0;
        this.hidl_o = noinit;
    }

    public Monostate noinit() {
        if (this.hidl_d != 0) {
            Object obj = this.hidl_o;
            String className = obj != null ? obj.getClass().getName() : "null";
            throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
        }
        Object obj2 = this.hidl_o;
        if (obj2 != null && !Monostate.class.isInstance(obj2)) {
            throw new Error("Union is in a corrupted state.");
        }
        return (Monostate) this.hidl_o;
    }

    public void value(SliceInfo value) {
        this.hidl_d = (byte) 1;
        this.hidl_o = value;
    }

    public SliceInfo value() {
        if (this.hidl_d != 1) {
            Object obj = this.hidl_o;
            String className = obj != null ? obj.getClass().getName() : "null";
            throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
        }
        Object obj2 = this.hidl_o;
        if (obj2 != null && !SliceInfo.class.isInstance(obj2)) {
            throw new Error("Union is in a corrupted state.");
        }
        return (SliceInfo) this.hidl_o;
    }

    public byte getDiscriminator() {
        return this.hidl_d;
    }

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != OptionalSliceInfo.class) {
            return false;
        }
        OptionalSliceInfo other = (OptionalSliceInfo) otherObject;
        if (this.hidl_d == other.hidl_d && HidlSupport.deepEquals(this.hidl_o, other.hidl_o)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.hidl_o)), Integer.valueOf(Objects.hashCode(Byte.valueOf(this.hidl_d))));
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        switch (this.hidl_d) {
            case 0:
                builder.append(".noinit = ");
                builder.append(noinit());
                break;
            case 1:
                builder.append(".value = ");
                builder.append(value());
                break;
            default:
                throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
        }
        builder.append("}");
        return builder.toString();
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(24L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<OptionalSliceInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<OptionalSliceInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            OptionalSliceInfo _hidl_vec_element = new OptionalSliceInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        byte int8 = _hidl_blob.getInt8(0 + _hidl_offset);
        this.hidl_d = int8;
        switch (int8) {
            case 0:
                Monostate monostate = new Monostate();
                this.hidl_o = monostate;
                monostate.readEmbeddedFromParcel(parcel, _hidl_blob, 4 + _hidl_offset);
                return;
            case 1:
                SliceInfo sliceInfo = new SliceInfo();
                this.hidl_o = sliceInfo;
                sliceInfo.readEmbeddedFromParcel(parcel, _hidl_blob, 4 + _hidl_offset);
                return;
            default:
                throw new IllegalStateException("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(24);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<OptionalSliceInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 24);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 24);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt8(0 + _hidl_offset, this.hidl_d);
        switch (this.hidl_d) {
            case 0:
                noinit().writeEmbeddedToBlob(_hidl_blob, 4 + _hidl_offset);
                return;
            case 1:
                value().writeEmbeddedToBlob(_hidl_blob, 4 + _hidl_offset);
                return;
            default:
                throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
        }
    }
}
