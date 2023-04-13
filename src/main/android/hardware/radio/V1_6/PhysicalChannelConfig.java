package android.hardware.radio.V1_6;

import android.hardware.radio.V1_1.EutranBands;
import android.hardware.radio.V1_1.GeranBands;
import android.hardware.radio.V1_1.UtranBands;
import android.hardware.radio.V1_2.CellConnectionStatus;
import android.hardware.radio.V1_4.RadioTechnology;
import android.media.MediaMetrics;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class PhysicalChannelConfig {
    public int status = 0;
    public int rat = 0;
    public int downlinkChannelNumber = 0;
    public int uplinkChannelNumber = 0;
    public int cellBandwidthDownlinkKhz = 0;
    public int cellBandwidthUplinkKhz = 0;
    public ArrayList<Integer> contextIds = new ArrayList<>();
    public int physicalCellId = 0;
    public Band band = new Band();

    /* loaded from: classes2.dex */
    public static final class Band {
        private byte hidl_d = 0;
        private Object hidl_o;

        public Band() {
            this.hidl_o = null;
            this.hidl_o = 0;
        }

        /* loaded from: classes2.dex */
        public static final class hidl_discriminator {
            public static final byte eutranBand = 2;
            public static final byte geranBand = 0;
            public static final byte ngranBand = 3;
            public static final byte utranBand = 1;

            public static final String getName(byte value) {
                switch (value) {
                    case 0:
                        return "geranBand";
                    case 1:
                        return "utranBand";
                    case 2:
                        return "eutranBand";
                    case 3:
                        return "ngranBand";
                    default:
                        return "Unknown";
                }
            }

            private hidl_discriminator() {
            }
        }

        public void geranBand(int geranBand) {
            this.hidl_d = (byte) 0;
            this.hidl_o = Integer.valueOf(geranBand);
        }

        public int geranBand() {
            if (this.hidl_d != 0) {
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

        public void utranBand(int utranBand) {
            this.hidl_d = (byte) 1;
            this.hidl_o = Integer.valueOf(utranBand);
        }

        public int utranBand() {
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

        public void eutranBand(int eutranBand) {
            this.hidl_d = (byte) 2;
            this.hidl_o = Integer.valueOf(eutranBand);
        }

        public int eutranBand() {
            if (this.hidl_d != 2) {
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

        public void ngranBand(int ngranBand) {
            this.hidl_d = (byte) 3;
            this.hidl_o = Integer.valueOf(ngranBand);
        }

        public int ngranBand() {
            if (this.hidl_d != 3) {
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
            if (otherObject == null || otherObject.getClass() != Band.class) {
                return false;
            }
            Band other = (Band) otherObject;
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
                    builder.append(".geranBand = ");
                    builder.append(GeranBands.toString(geranBand()));
                    break;
                case 1:
                    builder.append(".utranBand = ");
                    builder.append(UtranBands.toString(utranBand()));
                    break;
                case 2:
                    builder.append(".eutranBand = ");
                    builder.append(EutranBands.toString(eutranBand()));
                    break;
                case 3:
                    builder.append(".ngranBand = ");
                    builder.append(NgranBands.toString(ngranBand()));
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

        public static final ArrayList<Band> readVectorFromParcel(HwParcel parcel) {
            ArrayList<Band> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 8, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                Band _hidl_vec_element = new Band();
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
                    this.hidl_o = 0;
                    this.hidl_o = Integer.valueOf(_hidl_blob.getInt32(4 + _hidl_offset));
                    return;
                case 1:
                    this.hidl_o = 0;
                    this.hidl_o = Integer.valueOf(_hidl_blob.getInt32(4 + _hidl_offset));
                    return;
                case 2:
                    this.hidl_o = 0;
                    this.hidl_o = Integer.valueOf(_hidl_blob.getInt32(4 + _hidl_offset));
                    return;
                case 3:
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

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Band> _hidl_vec) {
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
                    _hidl_blob.putInt32(4 + _hidl_offset, geranBand());
                    return;
                case 1:
                    _hidl_blob.putInt32(4 + _hidl_offset, utranBand());
                    return;
                case 2:
                    _hidl_blob.putInt32(4 + _hidl_offset, eutranBand());
                    return;
                case 3:
                    _hidl_blob.putInt32(4 + _hidl_offset, ngranBand());
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
        if (otherObject == null || otherObject.getClass() != PhysicalChannelConfig.class) {
            return false;
        }
        PhysicalChannelConfig other = (PhysicalChannelConfig) otherObject;
        if (this.status == other.status && this.rat == other.rat && this.downlinkChannelNumber == other.downlinkChannelNumber && this.uplinkChannelNumber == other.uplinkChannelNumber && this.cellBandwidthDownlinkKhz == other.cellBandwidthDownlinkKhz && this.cellBandwidthUplinkKhz == other.cellBandwidthUplinkKhz && HidlSupport.deepEquals(this.contextIds, other.contextIds) && this.physicalCellId == other.physicalCellId && HidlSupport.deepEquals(this.band, other.band)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.status))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.rat))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.downlinkChannelNumber))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.uplinkChannelNumber))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cellBandwidthDownlinkKhz))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.cellBandwidthUplinkKhz))), Integer.valueOf(HidlSupport.deepHashCode(this.contextIds)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.physicalCellId))), Integer.valueOf(HidlSupport.deepHashCode(this.band)));
    }

    public final String toString() {
        return "{.status = " + CellConnectionStatus.toString(this.status) + ", .rat = " + RadioTechnology.toString(this.rat) + ", .downlinkChannelNumber = " + this.downlinkChannelNumber + ", .uplinkChannelNumber = " + this.uplinkChannelNumber + ", .cellBandwidthDownlinkKhz = " + this.cellBandwidthDownlinkKhz + ", .cellBandwidthUplinkKhz = " + this.cellBandwidthUplinkKhz + ", .contextIds = " + this.contextIds + ", .physicalCellId = " + this.physicalCellId + ", .band = " + this.band + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(56L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<PhysicalChannelConfig> readVectorFromParcel(HwParcel parcel) {
        ArrayList<PhysicalChannelConfig> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 56, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            PhysicalChannelConfig _hidl_vec_element = new PhysicalChannelConfig();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 56);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.status = _hidl_blob.getInt32(_hidl_offset + 0);
        this.rat = _hidl_blob.getInt32(_hidl_offset + 4);
        this.downlinkChannelNumber = _hidl_blob.getInt32(_hidl_offset + 8);
        this.uplinkChannelNumber = _hidl_blob.getInt32(_hidl_offset + 12);
        this.cellBandwidthDownlinkKhz = _hidl_blob.getInt32(_hidl_offset + 16);
        this.cellBandwidthUplinkKhz = _hidl_blob.getInt32(_hidl_offset + 20);
        int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 24 + 8);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 4, _hidl_blob.handle(), _hidl_offset + 24 + 0, true);
        this.contextIds.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            int _hidl_vec_element = childBlob.getInt32(_hidl_index_0 * 4);
            this.contextIds.add(Integer.valueOf(_hidl_vec_element));
        }
        this.physicalCellId = _hidl_blob.getInt32(_hidl_offset + 40);
        this.band.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 44);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(56);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<PhysicalChannelConfig> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 56);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 56);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(_hidl_offset + 0, this.status);
        _hidl_blob.putInt32(4 + _hidl_offset, this.rat);
        _hidl_blob.putInt32(_hidl_offset + 8, this.downlinkChannelNumber);
        _hidl_blob.putInt32(_hidl_offset + 12, this.uplinkChannelNumber);
        _hidl_blob.putInt32(16 + _hidl_offset, this.cellBandwidthDownlinkKhz);
        _hidl_blob.putInt32(20 + _hidl_offset, this.cellBandwidthUplinkKhz);
        int _hidl_vec_size = this.contextIds.size();
        _hidl_blob.putInt32(_hidl_offset + 24 + 8, _hidl_vec_size);
        _hidl_blob.putBool(_hidl_offset + 24 + 12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 4);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            childBlob.putInt32(_hidl_index_0 * 4, this.contextIds.get(_hidl_index_0).intValue());
        }
        _hidl_blob.putBlob(24 + _hidl_offset + 0, childBlob);
        _hidl_blob.putInt32(40 + _hidl_offset, this.physicalCellId);
        this.band.writeEmbeddedToBlob(_hidl_blob, 44 + _hidl_offset);
    }
}
