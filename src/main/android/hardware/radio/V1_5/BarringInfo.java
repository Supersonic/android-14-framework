package android.hardware.radio.V1_5;

import android.internal.hidl.safe_union.V1_0.Monostate;
import android.media.MediaMetrics;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import android.security.keystore.KeyProperties;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class BarringInfo {
    public int serviceType = 0;
    public int barringType = 0;
    public BarringTypeSpecificInfo barringTypeSpecificInfo = new BarringTypeSpecificInfo();

    /* loaded from: classes2.dex */
    public static final class ServiceType {
        public static final int CS_FALLBACK = 5;
        public static final int CS_SERVICE = 0;
        public static final int CS_VOICE = 2;
        public static final int EMERGENCY = 8;
        public static final int MMTEL_VIDEO = 7;
        public static final int MMTEL_VOICE = 6;
        public static final int MO_DATA = 4;
        public static final int MO_SIGNALLING = 3;
        public static final int OPERATOR_1 = 1001;
        public static final int OPERATOR_10 = 1010;
        public static final int OPERATOR_11 = 1011;
        public static final int OPERATOR_12 = 1012;
        public static final int OPERATOR_13 = 1013;
        public static final int OPERATOR_14 = 1014;
        public static final int OPERATOR_15 = 1015;
        public static final int OPERATOR_16 = 1016;
        public static final int OPERATOR_17 = 1017;
        public static final int OPERATOR_18 = 1018;
        public static final int OPERATOR_19 = 1019;
        public static final int OPERATOR_2 = 1002;
        public static final int OPERATOR_20 = 1020;
        public static final int OPERATOR_21 = 1021;
        public static final int OPERATOR_22 = 1022;
        public static final int OPERATOR_23 = 1023;
        public static final int OPERATOR_24 = 1024;
        public static final int OPERATOR_25 = 1025;
        public static final int OPERATOR_26 = 1026;
        public static final int OPERATOR_27 = 1027;
        public static final int OPERATOR_28 = 1028;
        public static final int OPERATOR_29 = 1029;
        public static final int OPERATOR_3 = 1003;
        public static final int OPERATOR_30 = 1030;
        public static final int OPERATOR_31 = 1031;
        public static final int OPERATOR_32 = 1032;
        public static final int OPERATOR_4 = 1004;
        public static final int OPERATOR_5 = 1005;
        public static final int OPERATOR_6 = 1006;
        public static final int OPERATOR_7 = 1007;
        public static final int OPERATOR_8 = 1008;
        public static final int OPERATOR_9 = 1009;
        public static final int PS_SERVICE = 1;
        public static final int SMS = 9;

        public static final String toString(int o) {
            if (o == 0) {
                return "CS_SERVICE";
            }
            if (o == 1) {
                return "PS_SERVICE";
            }
            if (o == 2) {
                return "CS_VOICE";
            }
            if (o == 3) {
                return "MO_SIGNALLING";
            }
            if (o == 4) {
                return "MO_DATA";
            }
            if (o == 5) {
                return "CS_FALLBACK";
            }
            if (o == 6) {
                return "MMTEL_VOICE";
            }
            if (o == 7) {
                return "MMTEL_VIDEO";
            }
            if (o == 8) {
                return "EMERGENCY";
            }
            if (o == 9) {
                return "SMS";
            }
            if (o == 1001) {
                return "OPERATOR_1";
            }
            if (o == 1002) {
                return "OPERATOR_2";
            }
            if (o == 1003) {
                return "OPERATOR_3";
            }
            if (o == 1004) {
                return "OPERATOR_4";
            }
            if (o == 1005) {
                return "OPERATOR_5";
            }
            if (o == 1006) {
                return "OPERATOR_6";
            }
            if (o == 1007) {
                return "OPERATOR_7";
            }
            if (o == 1008) {
                return "OPERATOR_8";
            }
            if (o == 1009) {
                return "OPERATOR_9";
            }
            if (o == 1010) {
                return "OPERATOR_10";
            }
            if (o == 1011) {
                return "OPERATOR_11";
            }
            if (o == 1012) {
                return "OPERATOR_12";
            }
            if (o == 1013) {
                return "OPERATOR_13";
            }
            if (o == 1014) {
                return "OPERATOR_14";
            }
            if (o == 1015) {
                return "OPERATOR_15";
            }
            if (o == 1016) {
                return "OPERATOR_16";
            }
            if (o == 1017) {
                return "OPERATOR_17";
            }
            if (o == 1018) {
                return "OPERATOR_18";
            }
            if (o == 1019) {
                return "OPERATOR_19";
            }
            if (o == 1020) {
                return "OPERATOR_20";
            }
            if (o == 1021) {
                return "OPERATOR_21";
            }
            if (o == 1022) {
                return "OPERATOR_22";
            }
            if (o == 1023) {
                return "OPERATOR_23";
            }
            if (o == 1024) {
                return "OPERATOR_24";
            }
            if (o == 1025) {
                return "OPERATOR_25";
            }
            if (o == 1026) {
                return "OPERATOR_26";
            }
            if (o == 1027) {
                return "OPERATOR_27";
            }
            if (o == 1028) {
                return "OPERATOR_28";
            }
            if (o == 1029) {
                return "OPERATOR_29";
            }
            if (o == 1030) {
                return "OPERATOR_30";
            }
            if (o == 1031) {
                return "OPERATOR_31";
            }
            if (o == 1032) {
                return "OPERATOR_32";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            list.add("CS_SERVICE");
            if ((o & 1) == 1) {
                list.add("PS_SERVICE");
                flipped = 0 | 1;
            }
            if ((o & 2) == 2) {
                list.add("CS_VOICE");
                flipped |= 2;
            }
            if ((o & 3) == 3) {
                list.add("MO_SIGNALLING");
                flipped |= 3;
            }
            if ((o & 4) == 4) {
                list.add("MO_DATA");
                flipped |= 4;
            }
            if ((o & 5) == 5) {
                list.add("CS_FALLBACK");
                flipped |= 5;
            }
            if ((o & 6) == 6) {
                list.add("MMTEL_VOICE");
                flipped |= 6;
            }
            if ((o & 7) == 7) {
                list.add("MMTEL_VIDEO");
                flipped |= 7;
            }
            if ((o & 8) == 8) {
                list.add("EMERGENCY");
                flipped |= 8;
            }
            if ((o & 9) == 9) {
                list.add("SMS");
                flipped |= 9;
            }
            if ((o & 1001) == 1001) {
                list.add("OPERATOR_1");
                flipped |= 1001;
            }
            if ((o & 1002) == 1002) {
                list.add("OPERATOR_2");
                flipped |= 1002;
            }
            if ((o & 1003) == 1003) {
                list.add("OPERATOR_3");
                flipped |= 1003;
            }
            if ((o & 1004) == 1004) {
                list.add("OPERATOR_4");
                flipped |= 1004;
            }
            if ((o & 1005) == 1005) {
                list.add("OPERATOR_5");
                flipped |= 1005;
            }
            if ((o & 1006) == 1006) {
                list.add("OPERATOR_6");
                flipped |= 1006;
            }
            if ((o & 1007) == 1007) {
                list.add("OPERATOR_7");
                flipped |= 1007;
            }
            if ((o & 1008) == 1008) {
                list.add("OPERATOR_8");
                flipped |= 1008;
            }
            if ((o & 1009) == 1009) {
                list.add("OPERATOR_9");
                flipped |= 1009;
            }
            if ((o & 1010) == 1010) {
                list.add("OPERATOR_10");
                flipped |= 1010;
            }
            if ((o & 1011) == 1011) {
                list.add("OPERATOR_11");
                flipped |= 1011;
            }
            if ((o & 1012) == 1012) {
                list.add("OPERATOR_12");
                flipped |= 1012;
            }
            if ((o & 1013) == 1013) {
                list.add("OPERATOR_13");
                flipped |= 1013;
            }
            if ((o & 1014) == 1014) {
                list.add("OPERATOR_14");
                flipped |= 1014;
            }
            if ((o & 1015) == 1015) {
                list.add("OPERATOR_15");
                flipped |= 1015;
            }
            if ((o & 1016) == 1016) {
                list.add("OPERATOR_16");
                flipped |= 1016;
            }
            if ((o & 1017) == 1017) {
                list.add("OPERATOR_17");
                flipped |= 1017;
            }
            if ((o & 1018) == 1018) {
                list.add("OPERATOR_18");
                flipped |= 1018;
            }
            if ((o & 1019) == 1019) {
                list.add("OPERATOR_19");
                flipped |= 1019;
            }
            if ((o & 1020) == 1020) {
                list.add("OPERATOR_20");
                flipped |= 1020;
            }
            if ((o & 1021) == 1021) {
                list.add("OPERATOR_21");
                flipped |= 1021;
            }
            if ((o & 1022) == 1022) {
                list.add("OPERATOR_22");
                flipped |= 1022;
            }
            if ((o & 1023) == 1023) {
                list.add("OPERATOR_23");
                flipped |= 1023;
            }
            if ((o & 1024) == 1024) {
                list.add("OPERATOR_24");
                flipped |= 1024;
            }
            if ((o & 1025) == 1025) {
                list.add("OPERATOR_25");
                flipped |= 1025;
            }
            if ((o & 1026) == 1026) {
                list.add("OPERATOR_26");
                flipped |= 1026;
            }
            if ((o & 1027) == 1027) {
                list.add("OPERATOR_27");
                flipped |= 1027;
            }
            if ((o & 1028) == 1028) {
                list.add("OPERATOR_28");
                flipped |= 1028;
            }
            if ((o & 1029) == 1029) {
                list.add("OPERATOR_29");
                flipped |= 1029;
            }
            if ((o & 1030) == 1030) {
                list.add("OPERATOR_30");
                flipped |= 1030;
            }
            if ((o & 1031) == 1031) {
                list.add("OPERATOR_31");
                flipped |= 1031;
            }
            if ((o & 1032) == 1032) {
                list.add("OPERATOR_32");
                flipped |= 1032;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes2.dex */
    public static final class BarringType {
        public static final int CONDITIONAL = 1;
        public static final int NONE = 0;
        public static final int UNCONDITIONAL = 2;

        public static final String toString(int o) {
            if (o == 0) {
                return KeyProperties.DIGEST_NONE;
            }
            if (o == 1) {
                return "CONDITIONAL";
            }
            if (o == 2) {
                return "UNCONDITIONAL";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            list.add(KeyProperties.DIGEST_NONE);
            if ((o & 1) == 1) {
                list.add("CONDITIONAL");
                flipped = 0 | 1;
            }
            if ((o & 2) == 2) {
                list.add("UNCONDITIONAL");
                flipped |= 2;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes2.dex */
    public static final class BarringTypeSpecificInfo {
        private byte hidl_d = 0;
        private Object hidl_o;

        /* loaded from: classes2.dex */
        public static final class Conditional {
            public int factor = 0;
            public int timeSeconds = 0;
            public boolean isBarred = false;

            public final boolean equals(Object otherObject) {
                if (this == otherObject) {
                    return true;
                }
                if (otherObject == null || otherObject.getClass() != Conditional.class) {
                    return false;
                }
                Conditional other = (Conditional) otherObject;
                if (this.factor == other.factor && this.timeSeconds == other.timeSeconds && this.isBarred == other.isBarred) {
                    return true;
                }
                return false;
            }

            public final int hashCode() {
                return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.factor))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.timeSeconds))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isBarred))));
            }

            public final String toString() {
                return "{.factor = " + this.factor + ", .timeSeconds = " + this.timeSeconds + ", .isBarred = " + this.isBarred + "}";
            }

            public final void readFromParcel(HwParcel parcel) {
                HwBlob blob = parcel.readBuffer(12L);
                readEmbeddedFromParcel(parcel, blob, 0L);
            }

            public static final ArrayList<Conditional> readVectorFromParcel(HwParcel parcel) {
                ArrayList<Conditional> _hidl_vec = new ArrayList<>();
                HwBlob _hidl_blob = parcel.readBuffer(16L);
                int _hidl_vec_size = _hidl_blob.getInt32(8L);
                HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 12, _hidl_blob.handle(), 0L, true);
                _hidl_vec.clear();
                for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                    Conditional _hidl_vec_element = new Conditional();
                    _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 12);
                    _hidl_vec.add(_hidl_vec_element);
                }
                return _hidl_vec;
            }

            public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
                this.factor = _hidl_blob.getInt32(0 + _hidl_offset);
                this.timeSeconds = _hidl_blob.getInt32(4 + _hidl_offset);
                this.isBarred = _hidl_blob.getBool(8 + _hidl_offset);
            }

            public final void writeToParcel(HwParcel parcel) {
                HwBlob _hidl_blob = new HwBlob(12);
                writeEmbeddedToBlob(_hidl_blob, 0L);
                parcel.writeBuffer(_hidl_blob);
            }

            public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Conditional> _hidl_vec) {
                HwBlob _hidl_blob = new HwBlob(16);
                int _hidl_vec_size = _hidl_vec.size();
                _hidl_blob.putInt32(8L, _hidl_vec_size);
                _hidl_blob.putBool(12L, false);
                HwBlob childBlob = new HwBlob(_hidl_vec_size * 12);
                for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                    _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 12);
                }
                _hidl_blob.putBlob(0L, childBlob);
                parcel.writeBuffer(_hidl_blob);
            }

            public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
                _hidl_blob.putInt32(0 + _hidl_offset, this.factor);
                _hidl_blob.putInt32(4 + _hidl_offset, this.timeSeconds);
                _hidl_blob.putBool(8 + _hidl_offset, this.isBarred);
            }
        }

        public BarringTypeSpecificInfo() {
            this.hidl_o = null;
            this.hidl_o = new Monostate();
        }

        /* loaded from: classes2.dex */
        public static final class hidl_discriminator {
            public static final byte conditional = 1;
            public static final byte noinit = 0;

            public static final String getName(byte value) {
                switch (value) {
                    case 0:
                        return "noinit";
                    case 1:
                        return "conditional";
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

        public void conditional(Conditional conditional) {
            this.hidl_d = (byte) 1;
            this.hidl_o = conditional;
        }

        public Conditional conditional() {
            if (this.hidl_d != 1) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !Conditional.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (Conditional) this.hidl_o;
        }

        public byte getDiscriminator() {
            return this.hidl_d;
        }

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != BarringTypeSpecificInfo.class) {
                return false;
            }
            BarringTypeSpecificInfo other = (BarringTypeSpecificInfo) otherObject;
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
                    builder.append(".conditional = ");
                    builder.append(conditional());
                    break;
                default:
                    throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
            builder.append("}");
            return builder.toString();
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(16L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<BarringTypeSpecificInfo> readVectorFromParcel(HwParcel parcel) {
            ArrayList<BarringTypeSpecificInfo> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                BarringTypeSpecificInfo _hidl_vec_element = new BarringTypeSpecificInfo();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 16);
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
                    Conditional conditional = new Conditional();
                    this.hidl_o = conditional;
                    conditional.readEmbeddedFromParcel(parcel, _hidl_blob, 4 + _hidl_offset);
                    return;
                default:
                    throw new IllegalStateException("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(16);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<BarringTypeSpecificInfo> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 16);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 16);
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
                    conditional().writeEmbeddedToBlob(_hidl_blob, 4 + _hidl_offset);
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
        if (otherObject == null || otherObject.getClass() != BarringInfo.class) {
            return false;
        }
        BarringInfo other = (BarringInfo) otherObject;
        if (this.serviceType == other.serviceType && this.barringType == other.barringType && HidlSupport.deepEquals(this.barringTypeSpecificInfo, other.barringTypeSpecificInfo)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.serviceType))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.barringType))), Integer.valueOf(HidlSupport.deepHashCode(this.barringTypeSpecificInfo)));
    }

    public final String toString() {
        return "{.serviceType = " + ServiceType.toString(this.serviceType) + ", .barringType = " + BarringType.toString(this.barringType) + ", .barringTypeSpecificInfo = " + this.barringTypeSpecificInfo + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(24L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<BarringInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<BarringInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 24, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            BarringInfo _hidl_vec_element = new BarringInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.serviceType = _hidl_blob.getInt32(0 + _hidl_offset);
        this.barringType = _hidl_blob.getInt32(4 + _hidl_offset);
        this.barringTypeSpecificInfo.readEmbeddedFromParcel(parcel, _hidl_blob, 8 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(24);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<BarringInfo> _hidl_vec) {
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
        _hidl_blob.putInt32(0 + _hidl_offset, this.serviceType);
        _hidl_blob.putInt32(4 + _hidl_offset, this.barringType);
        this.barringTypeSpecificInfo.writeEmbeddedToBlob(_hidl_blob, 8 + _hidl_offset);
    }
}
