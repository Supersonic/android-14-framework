package android.hardware.gnss.V2_1;

import android.hardware.biometrics.fingerprint.AcquiredInfo;
import android.hardware.gnss.V1_0.IGnssMeasurementCallback;
import android.hardware.gnss.V1_1.IGnssMeasurementCallback;
import android.hardware.gnss.V2_0.ElapsedRealtime;
import android.hardware.gnss.V2_0.IGnssMeasurementCallback;
import android.internal.hidl.base.V1_0.DebugInfo;
import android.internal.hidl.base.V1_0.IBase;
import android.p008os.HidlSupport;
import android.p008os.HwBinder;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import android.p008os.IHwBinder;
import android.p008os.IHwInterface;
import android.p008os.NativeHandle;
import android.p008os.RemoteException;
import com.android.internal.midi.MidiConstants;
import com.android.internal.telephony.GsmAlphabet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface IGnssMeasurementCallback extends android.hardware.gnss.V2_0.IGnssMeasurementCallback {
    public static final String kInterfaceName = "android.hardware.gnss@2.1::IGnssMeasurementCallback";

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void gnssMeasurementCb_2_1(GnssData gnssData) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IGnssMeasurementCallback asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IGnssMeasurementCallback)) {
            return (IGnssMeasurementCallback) iface;
        }
        IGnssMeasurementCallback proxy = new Proxy(binder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                String descriptor = it.next();
                if (descriptor.equals(kInterfaceName)) {
                    return proxy;
                }
            }
        } catch (RemoteException e) {
        }
        return null;
    }

    static IGnssMeasurementCallback castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IGnssMeasurementCallback getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IGnssMeasurementCallback getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IGnssMeasurementCallback getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IGnssMeasurementCallback getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class GnssMeasurementFlags {
        public static final int HAS_AUTOMATIC_GAIN_CONTROL = 8192;
        public static final int HAS_CARRIER_CYCLES = 1024;
        public static final int HAS_CARRIER_FREQUENCY = 512;
        public static final int HAS_CARRIER_PHASE = 2048;
        public static final int HAS_CARRIER_PHASE_UNCERTAINTY = 4096;
        public static final int HAS_FULL_ISB = 65536;
        public static final int HAS_FULL_ISB_UNCERTAINTY = 131072;
        public static final int HAS_SATELLITE_ISB = 262144;
        public static final int HAS_SATELLITE_ISB_UNCERTAINTY = 524288;
        public static final int HAS_SNR = 1;

        public static final String toString(int o) {
            if (o == 1) {
                return "HAS_SNR";
            }
            if (o == 512) {
                return "HAS_CARRIER_FREQUENCY";
            }
            if (o == 1024) {
                return "HAS_CARRIER_CYCLES";
            }
            if (o == 2048) {
                return "HAS_CARRIER_PHASE";
            }
            if (o == 4096) {
                return "HAS_CARRIER_PHASE_UNCERTAINTY";
            }
            if (o == 8192) {
                return "HAS_AUTOMATIC_GAIN_CONTROL";
            }
            if (o == 65536) {
                return "HAS_FULL_ISB";
            }
            if (o == 131072) {
                return "HAS_FULL_ISB_UNCERTAINTY";
            }
            if (o == 262144) {
                return "HAS_SATELLITE_ISB";
            }
            if (o == 524288) {
                return "HAS_SATELLITE_ISB_UNCERTAINTY";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            if ((o & 1) == 1) {
                list.add("HAS_SNR");
                flipped = 0 | 1;
            }
            if ((o & 512) == 512) {
                list.add("HAS_CARRIER_FREQUENCY");
                flipped |= 512;
            }
            if ((o & 1024) == 1024) {
                list.add("HAS_CARRIER_CYCLES");
                flipped |= 1024;
            }
            if ((o & 2048) == 2048) {
                list.add("HAS_CARRIER_PHASE");
                flipped |= 2048;
            }
            if ((o & 4096) == 4096) {
                list.add("HAS_CARRIER_PHASE_UNCERTAINTY");
                flipped |= 4096;
            }
            if ((o & 8192) == 8192) {
                list.add("HAS_AUTOMATIC_GAIN_CONTROL");
                flipped |= 8192;
            }
            if ((o & 65536) == 65536) {
                list.add("HAS_FULL_ISB");
                flipped |= 65536;
            }
            if ((o & 131072) == 131072) {
                list.add("HAS_FULL_ISB_UNCERTAINTY");
                flipped |= 131072;
            }
            if ((o & 262144) == 262144) {
                list.add("HAS_SATELLITE_ISB");
                flipped |= 262144;
            }
            if ((o & 524288) == 524288) {
                list.add("HAS_SATELLITE_ISB_UNCERTAINTY");
                flipped |= 524288;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GnssMeasurement {
        public int flags;
        public IGnssMeasurementCallback.GnssMeasurement v2_0 = new IGnssMeasurementCallback.GnssMeasurement();
        public double fullInterSignalBiasNs = 0.0d;
        public double fullInterSignalBiasUncertaintyNs = 0.0d;
        public double satelliteInterSignalBiasNs = 0.0d;
        public double satelliteInterSignalBiasUncertaintyNs = 0.0d;
        public double basebandCN0DbHz = 0.0d;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssMeasurement.class) {
                return false;
            }
            GnssMeasurement other = (GnssMeasurement) otherObject;
            if (HidlSupport.deepEquals(this.v2_0, other.v2_0) && HidlSupport.deepEquals(Integer.valueOf(this.flags), Integer.valueOf(other.flags)) && this.fullInterSignalBiasNs == other.fullInterSignalBiasNs && this.fullInterSignalBiasUncertaintyNs == other.fullInterSignalBiasUncertaintyNs && this.satelliteInterSignalBiasNs == other.satelliteInterSignalBiasNs && this.satelliteInterSignalBiasUncertaintyNs == other.satelliteInterSignalBiasUncertaintyNs && this.basebandCN0DbHz == other.basebandCN0DbHz) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.v2_0)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.flags))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.fullInterSignalBiasNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.fullInterSignalBiasUncertaintyNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.satelliteInterSignalBiasNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.satelliteInterSignalBiasUncertaintyNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.basebandCN0DbHz))));
        }

        public final String toString() {
            return "{.v2_0 = " + this.v2_0 + ", .flags = " + GnssMeasurementFlags.dumpBitfield(this.flags) + ", .fullInterSignalBiasNs = " + this.fullInterSignalBiasNs + ", .fullInterSignalBiasUncertaintyNs = " + this.fullInterSignalBiasUncertaintyNs + ", .satelliteInterSignalBiasNs = " + this.satelliteInterSignalBiasNs + ", .satelliteInterSignalBiasUncertaintyNs = " + this.satelliteInterSignalBiasUncertaintyNs + ", .basebandCN0DbHz = " + this.basebandCN0DbHz + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(224L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssMeasurement> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssMeasurement> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 224, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssMeasurement _hidl_vec_element = new GnssMeasurement();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 224);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.v2_0.readEmbeddedFromParcel(parcel, _hidl_blob, 0 + _hidl_offset);
            this.flags = _hidl_blob.getInt32(176 + _hidl_offset);
            this.fullInterSignalBiasNs = _hidl_blob.getDouble(184 + _hidl_offset);
            this.fullInterSignalBiasUncertaintyNs = _hidl_blob.getDouble(192 + _hidl_offset);
            this.satelliteInterSignalBiasNs = _hidl_blob.getDouble(200 + _hidl_offset);
            this.satelliteInterSignalBiasUncertaintyNs = _hidl_blob.getDouble(208 + _hidl_offset);
            this.basebandCN0DbHz = _hidl_blob.getDouble(216 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(224);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssMeasurement> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 224);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 224);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            this.v2_0.writeEmbeddedToBlob(_hidl_blob, 0 + _hidl_offset);
            _hidl_blob.putInt32(176 + _hidl_offset, this.flags);
            _hidl_blob.putDouble(184 + _hidl_offset, this.fullInterSignalBiasNs);
            _hidl_blob.putDouble(192 + _hidl_offset, this.fullInterSignalBiasUncertaintyNs);
            _hidl_blob.putDouble(200 + _hidl_offset, this.satelliteInterSignalBiasNs);
            _hidl_blob.putDouble(208 + _hidl_offset, this.satelliteInterSignalBiasUncertaintyNs);
            _hidl_blob.putDouble(216 + _hidl_offset, this.basebandCN0DbHz);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GnssClock {
        public IGnssMeasurementCallback.GnssClock v1_0 = new IGnssMeasurementCallback.GnssClock();
        public GnssSignalType referenceSignalTypeForIsb = new GnssSignalType();

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssClock.class) {
                return false;
            }
            GnssClock other = (GnssClock) otherObject;
            if (HidlSupport.deepEquals(this.v1_0, other.v1_0) && HidlSupport.deepEquals(this.referenceSignalTypeForIsb, other.referenceSignalTypeForIsb)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.v1_0)), Integer.valueOf(HidlSupport.deepHashCode(this.referenceSignalTypeForIsb)));
        }

        public final String toString() {
            return "{.v1_0 = " + this.v1_0 + ", .referenceSignalTypeForIsb = " + this.referenceSignalTypeForIsb + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(104L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssClock> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssClock> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 104, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssClock _hidl_vec_element = new GnssClock();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 104);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.v1_0.readEmbeddedFromParcel(parcel, _hidl_blob, 0 + _hidl_offset);
            this.referenceSignalTypeForIsb.readEmbeddedFromParcel(parcel, _hidl_blob, 72 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(104);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssClock> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 104);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 104);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            this.v1_0.writeEmbeddedToBlob(_hidl_blob, 0 + _hidl_offset);
            this.referenceSignalTypeForIsb.writeEmbeddedToBlob(_hidl_blob, 72 + _hidl_offset);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GnssData {
        public ArrayList<GnssMeasurement> measurements = new ArrayList<>();
        public GnssClock clock = new GnssClock();
        public ElapsedRealtime elapsedRealtime = new ElapsedRealtime();

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssData.class) {
                return false;
            }
            GnssData other = (GnssData) otherObject;
            if (HidlSupport.deepEquals(this.measurements, other.measurements) && HidlSupport.deepEquals(this.clock, other.clock) && HidlSupport.deepEquals(this.elapsedRealtime, other.elapsedRealtime)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.measurements)), Integer.valueOf(HidlSupport.deepHashCode(this.clock)), Integer.valueOf(HidlSupport.deepHashCode(this.elapsedRealtime)));
        }

        public final String toString() {
            return "{.measurements = " + this.measurements + ", .clock = " + this.clock + ", .elapsedRealtime = " + this.elapsedRealtime + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(144L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssData> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssData> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 144, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssData _hidl_vec_element = new GnssData();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 144);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 0 + 8);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 224, _hidl_blob.handle(), _hidl_offset + 0 + 0, true);
            this.measurements.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssMeasurement _hidl_vec_element = new GnssMeasurement();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 224);
                this.measurements.add(_hidl_vec_element);
            }
            this.clock.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 16);
            this.elapsedRealtime.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 120);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(144);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssData> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 144);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 144);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            int _hidl_vec_size = this.measurements.size();
            _hidl_blob.putInt32(_hidl_offset + 0 + 8, _hidl_vec_size);
            _hidl_blob.putBool(_hidl_offset + 0 + 12, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 224);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                this.measurements.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 224);
            }
            _hidl_blob.putBlob(_hidl_offset + 0 + 0, childBlob);
            this.clock.writeEmbeddedToBlob(_hidl_blob, 16 + _hidl_offset);
            this.elapsedRealtime.writeEmbeddedToBlob(_hidl_blob, 120 + _hidl_offset);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IGnssMeasurementCallback {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss@2.1::IGnssMeasurementCallback]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback
        public void GnssMeasurementCb(IGnssMeasurementCallback.GnssData data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssMeasurementCallback.kInterfaceName);
            data.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback
        public void gnssMeasurementCb(IGnssMeasurementCallback.GnssData data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnssMeasurementCallback.kInterfaceName);
            data.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback
        public void gnssMeasurementCb_2_0(IGnssMeasurementCallback.GnssData data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnssMeasurementCallback.kInterfaceName);
            data.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback
        public void gnssMeasurementCb_2_1(GnssData data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssMeasurementCallback.kInterfaceName);
            data.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public ArrayList<String> interfaceChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256067662, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<String> _hidl_out_descriptors = _hidl_reply.readStringVector();
                return _hidl_out_descriptors;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            _hidl_request.writeNativeHandle(fd);
            _hidl_request.writeStringVector(options);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256131655, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public String interfaceDescriptor() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256136003, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                String _hidl_out_descriptor = _hidl_reply.readString();
                return _hidl_out_descriptor;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public ArrayList<byte[]> getHashChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256398152, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<byte[]> _hidl_out_hashchain = new ArrayList<>();
                HwBlob _hidl_blob = _hidl_reply.readBuffer(16L);
                int _hidl_vec_size = _hidl_blob.getInt32(8L);
                HwBlob childBlob = _hidl_reply.readEmbeddedBuffer(_hidl_vec_size * 32, _hidl_blob.handle(), 0L, true);
                _hidl_out_hashchain.clear();
                for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                    byte[] _hidl_vec_element = new byte[32];
                    long _hidl_array_offset_1 = _hidl_index_0 * 32;
                    childBlob.copyToInt8Array(_hidl_array_offset_1, _hidl_vec_element, 32);
                    _hidl_out_hashchain.add(_hidl_vec_element);
                }
                return _hidl_out_hashchain;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void setHALInstrumentation() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256462420, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void ping() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256921159, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public DebugInfo getDebugInfo() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257049926, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                DebugInfo _hidl_out_info = new DebugInfo();
                _hidl_out_info.readFromParcel(_hidl_reply);
                return _hidl_out_info;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void notifySyspropsChanged() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257120595, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IGnssMeasurementCallback {
        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnssMeasurementCallback.kInterfaceName, android.hardware.gnss.V2_0.IGnssMeasurementCallback.kInterfaceName, android.hardware.gnss.V1_1.IGnssMeasurementCallback.kInterfaceName, android.hardware.gnss.V1_0.IGnssMeasurementCallback.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnssMeasurementCallback.kInterfaceName;
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{17, -23, -31, -95, -3, 12, -101, 61, -106, 72, 117, 13, 75, 16, -36, 42, -125, -99, 58, 102, -120, MidiConstants.STATUS_NOTE_ON, 76, 63, -60, -107, 0, -92, -25, -54, 117, MidiConstants.STATUS_CONTROL_CHANGE}, new byte[]{-35, 108, -39, -37, -92, -3, -23, -102, GsmAlphabet.GSM_EXTENDED_ESCAPE, -61, -53, 23, 40, -40, 35, 9, -11, 9, -90, -26, -31, -103, 62, 80, 66, -33, -91, -1, -28, -81, 84, 66}, new byte[]{122, -30, 2, 86, 98, -29, AcquiredInfo.POWER_PRESS, 105, 10, 63, -6, 28, 101, -52, -105, 44, 98, -105, -90, -122, 56, 23, 64, 85, -61, 60, -65, 61, 46, 75, -67, -36}, new byte[]{-67, -92, -110, -20, 64, 33, -47, 56, 105, -34, 114, -67, 111, -116, 21, -59, -125, 123, 120, -42, 19, 107, -115, 83, -114, -2, -59, 50, 5, 115, -91, -20}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.V2_1.IGnssMeasurementCallback, android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IGnssMeasurementCallback.kInterfaceName.equals(descriptor)) {
                return this;
            }
            return null;
        }

        public void registerAsService(String serviceName) throws RemoteException {
            registerService(serviceName);
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        @Override // android.p008os.HwBinder
        public void onTransact(int _hidl_code, HwParcel _hidl_request, HwParcel _hidl_reply, int _hidl_flags) throws RemoteException {
            switch (_hidl_code) {
                case 1:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssMeasurementCallback.kInterfaceName);
                    IGnssMeasurementCallback.GnssData data = new IGnssMeasurementCallback.GnssData();
                    data.readFromParcel(_hidl_request);
                    GnssMeasurementCb(data);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnssMeasurementCallback.kInterfaceName);
                    IGnssMeasurementCallback.GnssData data2 = new IGnssMeasurementCallback.GnssData();
                    data2.readFromParcel(_hidl_request);
                    gnssMeasurementCb(data2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnssMeasurementCallback.kInterfaceName);
                    IGnssMeasurementCallback.GnssData data3 = new IGnssMeasurementCallback.GnssData();
                    data3.readFromParcel(_hidl_request);
                    gnssMeasurementCb_2_0(data3);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 4:
                    _hidl_request.enforceInterface(IGnssMeasurementCallback.kInterfaceName);
                    GnssData data4 = new GnssData();
                    data4.readFromParcel(_hidl_request);
                    gnssMeasurementCb_2_1(data4);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 256067662:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ArrayList<String> _hidl_out_descriptors = interfaceChain();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStringVector(_hidl_out_descriptors);
                    _hidl_reply.send();
                    return;
                case 256131655:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    NativeHandle fd = _hidl_request.readNativeHandle();
                    ArrayList<String> options = _hidl_request.readStringVector();
                    debug(fd, options);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 256136003:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    String _hidl_out_descriptor = interfaceDescriptor();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeString(_hidl_out_descriptor);
                    _hidl_reply.send();
                    return;
                case 256398152:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ArrayList<byte[]> _hidl_out_hashchain = getHashChain();
                    _hidl_reply.writeStatus(0);
                    HwBlob _hidl_blob = new HwBlob(16);
                    int _hidl_vec_size = _hidl_out_hashchain.size();
                    _hidl_blob.putInt32(8L, _hidl_vec_size);
                    _hidl_blob.putBool(12L, false);
                    HwBlob childBlob = new HwBlob(_hidl_vec_size * 32);
                    for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                        long _hidl_array_offset_1 = _hidl_index_0 * 32;
                        byte[] _hidl_array_item_1 = _hidl_out_hashchain.get(_hidl_index_0);
                        if (_hidl_array_item_1 == null || _hidl_array_item_1.length != 32) {
                            throw new IllegalArgumentException("Array element is not of the expected length");
                        }
                        childBlob.putInt8Array(_hidl_array_offset_1, _hidl_array_item_1);
                    }
                    _hidl_blob.putBlob(0L, childBlob);
                    _hidl_reply.writeBuffer(_hidl_blob);
                    _hidl_reply.send();
                    return;
                case 256462420:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    setHALInstrumentation();
                    return;
                case 256660548:
                default:
                    return;
                case 256921159:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ping();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 257049926:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    DebugInfo _hidl_out_info = getDebugInfo();
                    _hidl_reply.writeStatus(0);
                    _hidl_out_info.writeToParcel(_hidl_reply);
                    _hidl_reply.send();
                    return;
                case 257120595:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    notifySyspropsChanged();
                    return;
            }
        }
    }
}
