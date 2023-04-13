package android.hardware.radio.V1_5;

import android.media.MediaMetrics;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class RadioAccessSpecifier {
    public int radioAccessNetwork = 0;
    public Bands bands = new Bands();
    public ArrayList<Integer> channels = new ArrayList<>();

    /* loaded from: classes2.dex */
    public static final class Bands {
        private byte hidl_d = 0;
        private Object hidl_o;

        public Bands() {
            this.hidl_o = null;
            this.hidl_o = new ArrayList();
        }

        /* loaded from: classes2.dex */
        public static final class hidl_discriminator {
            public static final byte eutranBands = 2;
            public static final byte geranBands = 0;
            public static final byte ngranBands = 3;
            public static final byte utranBands = 1;

            public static final String getName(byte value) {
                switch (value) {
                    case 0:
                        return "geranBands";
                    case 1:
                        return "utranBands";
                    case 2:
                        return "eutranBands";
                    case 3:
                        return "ngranBands";
                    default:
                        return "Unknown";
                }
            }

            private hidl_discriminator() {
            }
        }

        public void geranBands(ArrayList<Integer> geranBands) {
            this.hidl_d = (byte) 0;
            this.hidl_o = geranBands;
        }

        public ArrayList<Integer> geranBands() {
            if (this.hidl_d != 0) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !ArrayList.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (ArrayList) this.hidl_o;
        }

        public void utranBands(ArrayList<Integer> utranBands) {
            this.hidl_d = (byte) 1;
            this.hidl_o = utranBands;
        }

        public ArrayList<Integer> utranBands() {
            if (this.hidl_d != 1) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !ArrayList.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (ArrayList) this.hidl_o;
        }

        public void eutranBands(ArrayList<Integer> eutranBands) {
            this.hidl_d = (byte) 2;
            this.hidl_o = eutranBands;
        }

        public ArrayList<Integer> eutranBands() {
            if (this.hidl_d != 2) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !ArrayList.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (ArrayList) this.hidl_o;
        }

        public void ngranBands(ArrayList<Integer> ngranBands) {
            this.hidl_d = (byte) 3;
            this.hidl_o = ngranBands;
        }

        public ArrayList<Integer> ngranBands() {
            if (this.hidl_d != 3) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !ArrayList.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (ArrayList) this.hidl_o;
        }

        public byte getDiscriminator() {
            return this.hidl_d;
        }

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != Bands.class) {
                return false;
            }
            Bands other = (Bands) otherObject;
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
                    builder.append(".geranBands = ");
                    builder.append(geranBands());
                    break;
                case 1:
                    builder.append(".utranBands = ");
                    builder.append(utranBands());
                    break;
                case 2:
                    builder.append(".eutranBands = ");
                    builder.append(eutranBands());
                    break;
                case 3:
                    builder.append(".ngranBands = ");
                    builder.append(ngranBands());
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

        public static final ArrayList<Bands> readVectorFromParcel(HwParcel parcel) {
            ArrayList<Bands> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                Bands _hidl_vec_element = new Bands();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            byte int8 = _hidl_blob.getInt8(_hidl_offset + 0);
            this.hidl_d = int8;
            switch (int8) {
                case 0:
                    this.hidl_o = new ArrayList();
                    int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 8 + 8);
                    HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 4, _hidl_blob.handle(), _hidl_offset + 8 + 0, true);
                    ((ArrayList) this.hidl_o).clear();
                    for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                        int _hidl_vec_element = childBlob.getInt32(_hidl_index_0 * 4);
                        ((ArrayList) this.hidl_o).add(Integer.valueOf(_hidl_vec_element));
                    }
                    return;
                case 1:
                    this.hidl_o = new ArrayList();
                    int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 8 + 8);
                    HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 4, _hidl_blob.handle(), _hidl_offset + 8 + 0, true);
                    ((ArrayList) this.hidl_o).clear();
                    for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
                        int _hidl_vec_element2 = childBlob2.getInt32(_hidl_index_02 * 4);
                        ((ArrayList) this.hidl_o).add(Integer.valueOf(_hidl_vec_element2));
                    }
                    return;
                case 2:
                    this.hidl_o = new ArrayList();
                    int _hidl_vec_size3 = _hidl_blob.getInt32(_hidl_offset + 8 + 8);
                    HwBlob childBlob3 = parcel.readEmbeddedBuffer(_hidl_vec_size3 * 4, _hidl_blob.handle(), _hidl_offset + 8 + 0, true);
                    ((ArrayList) this.hidl_o).clear();
                    for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
                        int _hidl_vec_element3 = childBlob3.getInt32(_hidl_index_03 * 4);
                        ((ArrayList) this.hidl_o).add(Integer.valueOf(_hidl_vec_element3));
                    }
                    return;
                case 3:
                    this.hidl_o = new ArrayList();
                    int _hidl_vec_size4 = _hidl_blob.getInt32(_hidl_offset + 8 + 8);
                    HwBlob childBlob4 = parcel.readEmbeddedBuffer(_hidl_vec_size4 * 4, _hidl_blob.handle(), _hidl_offset + 8 + 0, true);
                    ((ArrayList) this.hidl_o).clear();
                    for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
                        int _hidl_vec_element4 = childBlob4.getInt32(_hidl_index_04 * 4);
                        ((ArrayList) this.hidl_o).add(Integer.valueOf(_hidl_vec_element4));
                    }
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

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Bands> _hidl_vec) {
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
            _hidl_blob.putInt8(_hidl_offset + 0, this.hidl_d);
            switch (this.hidl_d) {
                case 0:
                    int _hidl_vec_size = geranBands().size();
                    _hidl_blob.putInt32(_hidl_offset + 8 + 8, _hidl_vec_size);
                    _hidl_blob.putBool(_hidl_offset + 8 + 12, false);
                    HwBlob childBlob = new HwBlob(_hidl_vec_size * 4);
                    for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                        childBlob.putInt32(_hidl_index_0 * 4, geranBands().get(_hidl_index_0).intValue());
                    }
                    _hidl_blob.putBlob(8 + _hidl_offset + 0, childBlob);
                    return;
                case 1:
                    int _hidl_vec_size2 = utranBands().size();
                    _hidl_blob.putInt32(_hidl_offset + 8 + 8, _hidl_vec_size2);
                    _hidl_blob.putBool(_hidl_offset + 8 + 12, false);
                    HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 4);
                    for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
                        childBlob2.putInt32(_hidl_index_02 * 4, utranBands().get(_hidl_index_02).intValue());
                    }
                    _hidl_blob.putBlob(8 + _hidl_offset + 0, childBlob2);
                    return;
                case 2:
                    int _hidl_vec_size3 = eutranBands().size();
                    _hidl_blob.putInt32(_hidl_offset + 8 + 8, _hidl_vec_size3);
                    _hidl_blob.putBool(_hidl_offset + 8 + 12, false);
                    HwBlob childBlob3 = new HwBlob(_hidl_vec_size3 * 4);
                    for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
                        childBlob3.putInt32(_hidl_index_03 * 4, eutranBands().get(_hidl_index_03).intValue());
                    }
                    _hidl_blob.putBlob(8 + _hidl_offset + 0, childBlob3);
                    return;
                case 3:
                    int _hidl_vec_size4 = ngranBands().size();
                    _hidl_blob.putInt32(_hidl_offset + 8 + 8, _hidl_vec_size4);
                    _hidl_blob.putBool(_hidl_offset + 8 + 12, false);
                    HwBlob childBlob4 = new HwBlob(_hidl_vec_size4 * 4);
                    for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
                        childBlob4.putInt32(_hidl_index_04 * 4, ngranBands().get(_hidl_index_04).intValue());
                    }
                    _hidl_blob.putBlob(8 + _hidl_offset + 0, childBlob4);
                    return;
                default:
                    throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
        }
    }

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != RadioAccessSpecifier.class) {
            return false;
        }
        RadioAccessSpecifier other = (RadioAccessSpecifier) otherObject;
        if (this.radioAccessNetwork == other.radioAccessNetwork && HidlSupport.deepEquals(this.bands, other.bands) && HidlSupport.deepEquals(this.channels, other.channels)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.radioAccessNetwork))), Integer.valueOf(HidlSupport.deepHashCode(this.bands)), Integer.valueOf(HidlSupport.deepHashCode(this.channels)));
    }

    public final String toString() {
        return "{.radioAccessNetwork = " + RadioAccessNetworks.toString(this.radioAccessNetwork) + ", .bands = " + this.bands + ", .channels = " + this.channels + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(48L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<RadioAccessSpecifier> readVectorFromParcel(HwParcel parcel) {
        ArrayList<RadioAccessSpecifier> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 48, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            RadioAccessSpecifier _hidl_vec_element = new RadioAccessSpecifier();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 48);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.radioAccessNetwork = _hidl_blob.getInt32(_hidl_offset + 0);
        this.bands.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 8);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 32 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 4, _hidl_blob.handle(), 0 + _hidl_offset + 32, true);
        this.channels.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            int _hidl_vec_element = childBlob.getInt32(_hidl_index_0 * 4);
            this.channels.add(Integer.valueOf(_hidl_vec_element));
        }
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(48);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<RadioAccessSpecifier> _hidl_vec) {
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
        _hidl_blob.putInt32(_hidl_offset + 0, this.radioAccessNetwork);
        this.bands.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 8);
        int _hidl_vec_size = this.channels.size();
        _hidl_blob.putInt32(_hidl_offset + 32 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 32 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 4);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt32(_hidl_index_0 * 4, this.channels.get(_hidl_index_0).intValue());
        }
        _hidl_blob.putBlob(32 + _hidl_offset + 0, childBlob);
    }
}
