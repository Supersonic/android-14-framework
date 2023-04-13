package android.hardware.radio.V1_6;

import android.internal.hidl.safe_union.V1_0.Monostate;
import android.media.MediaMetrics;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class QosFilter {
    public ArrayList<String> localAddresses = new ArrayList<>();
    public ArrayList<String> remoteAddresses = new ArrayList<>();
    public MaybePort localPort = new MaybePort();
    public MaybePort remotePort = new MaybePort();
    public byte protocol = 0;
    public TypeOfService tos = new TypeOfService();
    public Ipv6FlowLabel flowLabel = new Ipv6FlowLabel();
    public IpsecSpi spi = new IpsecSpi();
    public byte direction = 0;
    public int precedence = 0;

    /* loaded from: classes2.dex */
    public static final class TypeOfService {
        private byte hidl_d = 0;
        private Object hidl_o;

        public TypeOfService() {
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

        public void value(byte value) {
            this.hidl_d = (byte) 1;
            this.hidl_o = Byte.valueOf(value);
        }

        public byte value() {
            if (this.hidl_d != 1) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !Byte.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return ((Byte) this.hidl_o).byteValue();
        }

        public byte getDiscriminator() {
            return this.hidl_d;
        }

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != TypeOfService.class) {
                return false;
            }
            TypeOfService other = (TypeOfService) otherObject;
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
                    builder.append((int) value());
                    break;
                default:
                    throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
            builder.append("}");
            return builder.toString();
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(2L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<TypeOfService> readVectorFromParcel(HwParcel parcel) {
            ArrayList<TypeOfService> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 2, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                TypeOfService _hidl_vec_element = new TypeOfService();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 2);
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
                    monostate.readEmbeddedFromParcel(parcel, _hidl_blob, 1 + _hidl_offset);
                    return;
                case 1:
                    this.hidl_o = 0;
                    this.hidl_o = Byte.valueOf(_hidl_blob.getInt8(1 + _hidl_offset));
                    return;
                default:
                    throw new IllegalStateException("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(2);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<TypeOfService> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 2);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 2);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            _hidl_blob.putInt8(0 + _hidl_offset, this.hidl_d);
            switch (this.hidl_d) {
                case 0:
                    noinit().writeEmbeddedToBlob(_hidl_blob, 1 + _hidl_offset);
                    return;
                case 1:
                    _hidl_blob.putInt8(1 + _hidl_offset, value());
                    return;
                default:
                    throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class Ipv6FlowLabel {
        private byte hidl_d = 0;
        private Object hidl_o;

        public Ipv6FlowLabel() {
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

        public void value(int value) {
            this.hidl_d = (byte) 1;
            this.hidl_o = Integer.valueOf(value);
        }

        public int value() {
            if (this.hidl_d != 1) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !Integer.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return ((Integer) this.hidl_o).intValue();
        }

        public byte getDiscriminator() {
            return this.hidl_d;
        }

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != Ipv6FlowLabel.class) {
                return false;
            }
            Ipv6FlowLabel other = (Ipv6FlowLabel) otherObject;
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
            HwBlob blob = parcel.readBuffer(8L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<Ipv6FlowLabel> readVectorFromParcel(HwParcel parcel) {
            ArrayList<Ipv6FlowLabel> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 8, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                Ipv6FlowLabel _hidl_vec_element = new Ipv6FlowLabel();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 8);
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
                    this.hidl_o = 0;
                    this.hidl_o = Integer.valueOf(_hidl_blob.getInt32(4 + _hidl_offset));
                    return;
                default:
                    throw new IllegalStateException("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(8);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Ipv6FlowLabel> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 8);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 8);
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
                    _hidl_blob.putInt32(4 + _hidl_offset, value());
                    return;
                default:
                    throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class IpsecSpi {
        private byte hidl_d = 0;
        private Object hidl_o;

        public IpsecSpi() {
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

        public void value(int value) {
            this.hidl_d = (byte) 1;
            this.hidl_o = Integer.valueOf(value);
        }

        public int value() {
            if (this.hidl_d != 1) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !Integer.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return ((Integer) this.hidl_o).intValue();
        }

        public byte getDiscriminator() {
            return this.hidl_d;
        }

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != IpsecSpi.class) {
                return false;
            }
            IpsecSpi other = (IpsecSpi) otherObject;
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
            HwBlob blob = parcel.readBuffer(8L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<IpsecSpi> readVectorFromParcel(HwParcel parcel) {
            ArrayList<IpsecSpi> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 8, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                IpsecSpi _hidl_vec_element = new IpsecSpi();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 8);
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
                    this.hidl_o = 0;
                    this.hidl_o = Integer.valueOf(_hidl_blob.getInt32(4 + _hidl_offset));
                    return;
                default:
                    throw new IllegalStateException("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(8);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<IpsecSpi> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 8);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 8);
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
                    _hidl_blob.putInt32(4 + _hidl_offset, value());
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
        if (otherObject == null || otherObject.getClass() != QosFilter.class) {
            return false;
        }
        QosFilter other = (QosFilter) otherObject;
        if (HidlSupport.deepEquals(this.localAddresses, other.localAddresses) && HidlSupport.deepEquals(this.remoteAddresses, other.remoteAddresses) && HidlSupport.deepEquals(this.localPort, other.localPort) && HidlSupport.deepEquals(this.remotePort, other.remotePort) && this.protocol == other.protocol && HidlSupport.deepEquals(this.tos, other.tos) && HidlSupport.deepEquals(this.flowLabel, other.flowLabel) && HidlSupport.deepEquals(this.spi, other.spi) && this.direction == other.direction && this.precedence == other.precedence) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.localAddresses)), Integer.valueOf(HidlSupport.deepHashCode(this.remoteAddresses)), Integer.valueOf(HidlSupport.deepHashCode(this.localPort)), Integer.valueOf(HidlSupport.deepHashCode(this.remotePort)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.protocol))), Integer.valueOf(HidlSupport.deepHashCode(this.tos)), Integer.valueOf(HidlSupport.deepHashCode(this.flowLabel)), Integer.valueOf(HidlSupport.deepHashCode(this.spi)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.direction))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.precedence))));
    }

    public final String toString() {
        return "{.localAddresses = " + this.localAddresses + ", .remoteAddresses = " + this.remoteAddresses + ", .localPort = " + this.localPort + ", .remotePort = " + this.remotePort + ", .protocol = " + QosProtocol.toString(this.protocol) + ", .tos = " + this.tos + ", .flowLabel = " + this.flowLabel + ", .spi = " + this.spi + ", .direction = " + QosFilterDirection.toString(this.direction) + ", .precedence = " + this.precedence + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(88L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<QosFilter> readVectorFromParcel(HwParcel parcel) {
        ArrayList<QosFilter> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 88, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            QosFilter _hidl_vec_element = new QosFilter();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 88);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 0 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), _hidl_offset + 0 + 0, true);
        this.localAddresses.clear();
        int _hidl_index_0 = 0;
        while (_hidl_index_0 < _hidl_vec_size) {
            new String();
            String _hidl_vec_element = childBlob.getString(_hidl_index_0 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element.getBytes().length + 1, childBlob.handle(), (_hidl_index_0 * 16) + 0, false);
            this.localAddresses.add(_hidl_vec_element);
            _hidl_index_0++;
            childBlob = childBlob;
        }
        int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 16 + 8);
        HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 16, _hidl_blob.handle(), 0 + _hidl_offset + 16, true);
        this.remoteAddresses.clear();
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            new String();
            String _hidl_vec_element2 = childBlob2.getString(_hidl_index_02 * 16);
            parcel.readEmbeddedBuffer(_hidl_vec_element2.getBytes().length + 1, childBlob2.handle(), (_hidl_index_02 * 16) + 0, false);
            this.remoteAddresses.add(_hidl_vec_element2);
        }
        this.localPort.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 32);
        this.remotePort.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 44);
        this.protocol = _hidl_blob.getInt8(_hidl_offset + 56);
        this.tos.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 57);
        this.flowLabel.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 60);
        this.spi.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 68);
        this.direction = _hidl_blob.getInt8(_hidl_offset + 76);
        this.precedence = _hidl_blob.getInt32(_hidl_offset + 80);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(88);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<QosFilter> _hidl_vec) {
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
        int _hidl_vec_size = this.localAddresses.size();
        _hidl_blob.putInt32(_hidl_offset + 0 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 0 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 16);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putString(_hidl_index_0 * 16, this.localAddresses.get(_hidl_index_0));
        }
        _hidl_blob.putBlob(_hidl_offset + 0 + 0, childBlob);
        int _hidl_vec_size2 = this.remoteAddresses.size();
        _hidl_blob.putInt32(_hidl_offset + 16 + 8, _hidl_vec_size2);
        _hidl_blob.putBool(_hidl_offset + 16 + 12, false);
        HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 16);
        for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
            childBlob2.putString(_hidl_index_02 * 16, this.remoteAddresses.get(_hidl_index_02));
        }
        _hidl_blob.putBlob(_hidl_offset + 16 + 0, childBlob2);
        this.localPort.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 32);
        this.remotePort.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 44);
        _hidl_blob.putInt8(_hidl_offset + 56, this.protocol);
        this.tos.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 57);
        this.flowLabel.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 60);
        this.spi.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 68);
        _hidl_blob.putInt8(_hidl_offset + 76, this.direction);
        _hidl_blob.putInt32(_hidl_offset + 80, this.precedence);
    }
}
