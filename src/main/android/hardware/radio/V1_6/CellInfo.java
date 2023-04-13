package android.hardware.radio.V1_6;

import android.hardware.radio.V1_2.CellConnectionStatus;
import android.hardware.radio.V1_2.CellInfoCdma;
import android.hardware.radio.V1_5.CellInfoGsm;
import android.hardware.radio.V1_5.CellInfoTdscdma;
import android.hardware.radio.V1_5.CellInfoWcdma;
import android.media.MediaMetrics;
import android.p008os.HidlSupport;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class CellInfo {
    public boolean registered = false;
    public int connectionStatus = 0;
    public CellInfoRatSpecificInfo ratSpecificInfo = new CellInfoRatSpecificInfo();

    /* loaded from: classes2.dex */
    public static final class CellInfoRatSpecificInfo {
        private byte hidl_d = 0;
        private Object hidl_o;

        public CellInfoRatSpecificInfo() {
            this.hidl_o = null;
            this.hidl_o = new CellInfoGsm();
        }

        /* loaded from: classes2.dex */
        public static final class hidl_discriminator {
            public static final byte cdma = 5;
            public static final byte gsm = 0;
            public static final byte lte = 3;

            /* renamed from: nr */
            public static final byte f186nr = 4;
            public static final byte tdscdma = 2;
            public static final byte wcdma = 1;

            public static final String getName(byte value) {
                switch (value) {
                    case 0:
                        return "gsm";
                    case 1:
                        return "wcdma";
                    case 2:
                        return "tdscdma";
                    case 3:
                        return "lte";
                    case 4:
                        return "nr";
                    case 5:
                        return "cdma";
                    default:
                        return "Unknown";
                }
            }

            private hidl_discriminator() {
            }
        }

        public void gsm(CellInfoGsm gsm) {
            this.hidl_d = (byte) 0;
            this.hidl_o = gsm;
        }

        public CellInfoGsm gsm() {
            if (this.hidl_d != 0) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !CellInfoGsm.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (CellInfoGsm) this.hidl_o;
        }

        public void wcdma(CellInfoWcdma wcdma) {
            this.hidl_d = (byte) 1;
            this.hidl_o = wcdma;
        }

        public CellInfoWcdma wcdma() {
            if (this.hidl_d != 1) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !CellInfoWcdma.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (CellInfoWcdma) this.hidl_o;
        }

        public void tdscdma(CellInfoTdscdma tdscdma) {
            this.hidl_d = (byte) 2;
            this.hidl_o = tdscdma;
        }

        public CellInfoTdscdma tdscdma() {
            if (this.hidl_d != 2) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !CellInfoTdscdma.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (CellInfoTdscdma) this.hidl_o;
        }

        public void lte(CellInfoLte lte) {
            this.hidl_d = (byte) 3;
            this.hidl_o = lte;
        }

        public CellInfoLte lte() {
            if (this.hidl_d != 3) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !CellInfoLte.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (CellInfoLte) this.hidl_o;
        }

        /* renamed from: nr */
        public void m167nr(CellInfoNr nr) {
            this.hidl_d = (byte) 4;
            this.hidl_o = nr;
        }

        /* renamed from: nr */
        public CellInfoNr m168nr() {
            if (this.hidl_d != 4) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !CellInfoNr.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (CellInfoNr) this.hidl_o;
        }

        public void cdma(CellInfoCdma cdma) {
            this.hidl_d = (byte) 5;
            this.hidl_o = cdma;
        }

        public CellInfoCdma cdma() {
            if (this.hidl_d != 5) {
                Object obj = this.hidl_o;
                String className = obj != null ? obj.getClass().getName() : "null";
                throw new IllegalStateException("Read access to inactive union components is disallowed. Discriminator value is " + ((int) this.hidl_d) + " (corresponding to " + hidl_discriminator.getName(this.hidl_d) + "), and hidl_o is of type " + className + MediaMetrics.SEPARATOR);
            }
            Object obj2 = this.hidl_o;
            if (obj2 != null && !CellInfoCdma.class.isInstance(obj2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (CellInfoCdma) this.hidl_o;
        }

        public byte getDiscriminator() {
            return this.hidl_d;
        }

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != CellInfoRatSpecificInfo.class) {
                return false;
            }
            CellInfoRatSpecificInfo other = (CellInfoRatSpecificInfo) otherObject;
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
                    builder.append(".gsm = ");
                    builder.append(gsm());
                    break;
                case 1:
                    builder.append(".wcdma = ");
                    builder.append(wcdma());
                    break;
                case 2:
                    builder.append(".tdscdma = ");
                    builder.append(tdscdma());
                    break;
                case 3:
                    builder.append(".lte = ");
                    builder.append(lte());
                    break;
                case 4:
                    builder.append(".nr = ");
                    builder.append(m168nr());
                    break;
                case 5:
                    builder.append(".cdma = ");
                    builder.append(cdma());
                    break;
                default:
                    throw new Error("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
            builder.append("}");
            return builder.toString();
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(200L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<CellInfoRatSpecificInfo> readVectorFromParcel(HwParcel parcel) {
            ArrayList<CellInfoRatSpecificInfo> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 200, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                CellInfoRatSpecificInfo _hidl_vec_element = new CellInfoRatSpecificInfo();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 200);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            byte int8 = _hidl_blob.getInt8(0 + _hidl_offset);
            this.hidl_d = int8;
            switch (int8) {
                case 0:
                    CellInfoGsm cellInfoGsm = new CellInfoGsm();
                    this.hidl_o = cellInfoGsm;
                    cellInfoGsm.readEmbeddedFromParcel(parcel, _hidl_blob, 8 + _hidl_offset);
                    return;
                case 1:
                    CellInfoWcdma cellInfoWcdma = new CellInfoWcdma();
                    this.hidl_o = cellInfoWcdma;
                    cellInfoWcdma.readEmbeddedFromParcel(parcel, _hidl_blob, 8 + _hidl_offset);
                    return;
                case 2:
                    CellInfoTdscdma cellInfoTdscdma = new CellInfoTdscdma();
                    this.hidl_o = cellInfoTdscdma;
                    cellInfoTdscdma.readEmbeddedFromParcel(parcel, _hidl_blob, 8 + _hidl_offset);
                    return;
                case 3:
                    CellInfoLte cellInfoLte = new CellInfoLte();
                    this.hidl_o = cellInfoLte;
                    cellInfoLte.readEmbeddedFromParcel(parcel, _hidl_blob, 8 + _hidl_offset);
                    return;
                case 4:
                    CellInfoNr cellInfoNr = new CellInfoNr();
                    this.hidl_o = cellInfoNr;
                    cellInfoNr.readEmbeddedFromParcel(parcel, _hidl_blob, 8 + _hidl_offset);
                    return;
                case 5:
                    CellInfoCdma cellInfoCdma = new CellInfoCdma();
                    this.hidl_o = cellInfoCdma;
                    cellInfoCdma.readEmbeddedFromParcel(parcel, _hidl_blob, 8 + _hidl_offset);
                    return;
                default:
                    throw new IllegalStateException("Unknown union discriminator (value: " + ((int) this.hidl_d) + ").");
            }
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(200);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CellInfoRatSpecificInfo> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 200);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 200);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            _hidl_blob.putInt8(0 + _hidl_offset, this.hidl_d);
            switch (this.hidl_d) {
                case 0:
                    gsm().writeEmbeddedToBlob(_hidl_blob, 8 + _hidl_offset);
                    return;
                case 1:
                    wcdma().writeEmbeddedToBlob(_hidl_blob, 8 + _hidl_offset);
                    return;
                case 2:
                    tdscdma().writeEmbeddedToBlob(_hidl_blob, 8 + _hidl_offset);
                    return;
                case 3:
                    lte().writeEmbeddedToBlob(_hidl_blob, 8 + _hidl_offset);
                    return;
                case 4:
                    m168nr().writeEmbeddedToBlob(_hidl_blob, 8 + _hidl_offset);
                    return;
                case 5:
                    cdma().writeEmbeddedToBlob(_hidl_blob, 8 + _hidl_offset);
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
        if (otherObject == null || otherObject.getClass() != CellInfo.class) {
            return false;
        }
        CellInfo other = (CellInfo) otherObject;
        if (this.registered == other.registered && this.connectionStatus == other.connectionStatus && HidlSupport.deepEquals(this.ratSpecificInfo, other.ratSpecificInfo)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.registered))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.connectionStatus))), Integer.valueOf(HidlSupport.deepHashCode(this.ratSpecificInfo)));
    }

    public final String toString() {
        return "{.registered = " + this.registered + ", .connectionStatus = " + CellConnectionStatus.toString(this.connectionStatus) + ", .ratSpecificInfo = " + this.ratSpecificInfo + "}";
    }

    public final void readFromParcel(HwParcel parcel) {
        HwBlob blob = parcel.readBuffer(208L);
        readEmbeddedFromParcel(parcel, blob, 0L);
    }

    public static final ArrayList<CellInfo> readVectorFromParcel(HwParcel parcel) {
        ArrayList<CellInfo> _hidl_vec = new ArrayList<>();
        HwBlob _hidl_blob = parcel.readBuffer(16L);
        int _hidl_vec_size = _hidl_blob.getInt32(8L);
        HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 208, _hidl_blob.handle(), 0L, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            CellInfo _hidl_vec_element = new CellInfo();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 208);
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.registered = _hidl_blob.getBool(0 + _hidl_offset);
        this.connectionStatus = _hidl_blob.getInt32(4 + _hidl_offset);
        this.ratSpecificInfo.readEmbeddedFromParcel(parcel, _hidl_blob, 8 + _hidl_offset);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(208);
        writeEmbeddedToBlob(_hidl_blob, 0L);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<CellInfo> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8L, _hidl_vec_size);
        _hidl_blob.putBool(12L, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 208);
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 208);
        }
        _hidl_blob.putBlob(0L, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putBool(0 + _hidl_offset, this.registered);
        _hidl_blob.putInt32(4 + _hidl_offset, this.connectionStatus);
        this.ratSpecificInfo.writeEmbeddedToBlob(_hidl_blob, 8 + _hidl_offset);
    }
}
