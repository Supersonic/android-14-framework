package android.hardware.gnss.V1_0;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes.dex */
public interface IGnssMeasurementCallback extends IBase {
    public static final String kInterfaceName = "android.hardware.gnss@1.0::IGnssMeasurementCallback";

    void GnssMeasurementCb(GnssData gnssData) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
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

    /* loaded from: classes.dex */
    public static final class GnssClockFlags {
        public static final short HAS_BIAS = 8;
        public static final short HAS_BIAS_UNCERTAINTY = 16;
        public static final short HAS_DRIFT = 32;
        public static final short HAS_DRIFT_UNCERTAINTY = 64;
        public static final short HAS_FULL_BIAS = 4;
        public static final short HAS_LEAP_SECOND = 1;
        public static final short HAS_TIME_UNCERTAINTY = 2;

        public static final String toString(short o) {
            if (o == 1) {
                return "HAS_LEAP_SECOND";
            }
            if (o == 2) {
                return "HAS_TIME_UNCERTAINTY";
            }
            if (o == 4) {
                return "HAS_FULL_BIAS";
            }
            if (o == 8) {
                return "HAS_BIAS";
            }
            if (o == 16) {
                return "HAS_BIAS_UNCERTAINTY";
            }
            if (o == 32) {
                return "HAS_DRIFT";
            }
            if (o == 64) {
                return "HAS_DRIFT_UNCERTAINTY";
            }
            return "0x" + Integer.toHexString(Short.toUnsignedInt(o));
        }

        public static final String dumpBitfield(short o) {
            ArrayList<String> list = new ArrayList<>();
            short flipped = 0;
            if ((o & 1) == 1) {
                list.add("HAS_LEAP_SECOND");
                flipped = (short) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("HAS_TIME_UNCERTAINTY");
                flipped = (short) (flipped | 2);
            }
            if ((o & 4) == 4) {
                list.add("HAS_FULL_BIAS");
                flipped = (short) (flipped | 4);
            }
            if ((o & 8) == 8) {
                list.add("HAS_BIAS");
                flipped = (short) (flipped | 8);
            }
            if ((o & 16) == 16) {
                list.add("HAS_BIAS_UNCERTAINTY");
                flipped = (short) (flipped | 16);
            }
            if ((o & 32) == 32) {
                list.add("HAS_DRIFT");
                flipped = (short) (flipped | 32);
            }
            if ((o & 64) == 64) {
                list.add("HAS_DRIFT_UNCERTAINTY");
                flipped = (short) (flipped | 64);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Short.toUnsignedInt((short) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssMeasurementFlags {
        public static final int HAS_AUTOMATIC_GAIN_CONTROL = 8192;
        public static final int HAS_CARRIER_CYCLES = 1024;
        public static final int HAS_CARRIER_FREQUENCY = 512;
        public static final int HAS_CARRIER_PHASE = 2048;
        public static final int HAS_CARRIER_PHASE_UNCERTAINTY = 4096;
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
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssMultipathIndicator {
        public static final byte INDICATIOR_NOT_PRESENT = 2;
        public static final byte INDICATOR_PRESENT = 1;
        public static final byte INDICATOR_UNKNOWN = 0;

        public static final String toString(byte o) {
            if (o == 0) {
                return "INDICATOR_UNKNOWN";
            }
            if (o == 1) {
                return "INDICATOR_PRESENT";
            }
            if (o == 2) {
                return "INDICATIOR_NOT_PRESENT";
            }
            return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
        }

        public static final String dumpBitfield(byte o) {
            ArrayList<String> list = new ArrayList<>();
            byte flipped = 0;
            list.add("INDICATOR_UNKNOWN");
            if ((o & 1) == 1) {
                list.add("INDICATOR_PRESENT");
                flipped = (byte) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("INDICATIOR_NOT_PRESENT");
                flipped = (byte) (flipped | 2);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssMeasurementState {
        public static final int STATE_BDS_D2_BIT_SYNC = 256;
        public static final int STATE_BDS_D2_SUBFRAME_SYNC = 512;
        public static final int STATE_BIT_SYNC = 2;
        public static final int STATE_CODE_LOCK = 1;
        public static final int STATE_GAL_E1BC_CODE_LOCK = 1024;
        public static final int STATE_GAL_E1B_PAGE_SYNC = 4096;
        public static final int STATE_GAL_E1C_2ND_CODE_LOCK = 2048;
        public static final int STATE_GLO_STRING_SYNC = 64;
        public static final int STATE_GLO_TOD_DECODED = 128;
        public static final int STATE_GLO_TOD_KNOWN = 32768;
        public static final int STATE_MSEC_AMBIGUOUS = 16;
        public static final int STATE_SBAS_SYNC = 8192;
        public static final int STATE_SUBFRAME_SYNC = 4;
        public static final int STATE_SYMBOL_SYNC = 32;
        public static final int STATE_TOW_DECODED = 8;
        public static final int STATE_TOW_KNOWN = 16384;
        public static final int STATE_UNKNOWN = 0;

        public static final String toString(int o) {
            if (o == 0) {
                return "STATE_UNKNOWN";
            }
            if (o == 1) {
                return "STATE_CODE_LOCK";
            }
            if (o == 2) {
                return "STATE_BIT_SYNC";
            }
            if (o == 4) {
                return "STATE_SUBFRAME_SYNC";
            }
            if (o == 8) {
                return "STATE_TOW_DECODED";
            }
            if (o == 16) {
                return "STATE_MSEC_AMBIGUOUS";
            }
            if (o == 32) {
                return "STATE_SYMBOL_SYNC";
            }
            if (o == 64) {
                return "STATE_GLO_STRING_SYNC";
            }
            if (o == 128) {
                return "STATE_GLO_TOD_DECODED";
            }
            if (o == 256) {
                return "STATE_BDS_D2_BIT_SYNC";
            }
            if (o == 512) {
                return "STATE_BDS_D2_SUBFRAME_SYNC";
            }
            if (o == 1024) {
                return "STATE_GAL_E1BC_CODE_LOCK";
            }
            if (o == 2048) {
                return "STATE_GAL_E1C_2ND_CODE_LOCK";
            }
            if (o == 4096) {
                return "STATE_GAL_E1B_PAGE_SYNC";
            }
            if (o == 8192) {
                return "STATE_SBAS_SYNC";
            }
            if (o == 16384) {
                return "STATE_TOW_KNOWN";
            }
            if (o == 32768) {
                return "STATE_GLO_TOD_KNOWN";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            list.add("STATE_UNKNOWN");
            if ((o & 1) == 1) {
                list.add("STATE_CODE_LOCK");
                flipped = 0 | 1;
            }
            if ((o & 2) == 2) {
                list.add("STATE_BIT_SYNC");
                flipped |= 2;
            }
            if ((o & 4) == 4) {
                list.add("STATE_SUBFRAME_SYNC");
                flipped |= 4;
            }
            if ((o & 8) == 8) {
                list.add("STATE_TOW_DECODED");
                flipped |= 8;
            }
            if ((o & 16) == 16) {
                list.add("STATE_MSEC_AMBIGUOUS");
                flipped |= 16;
            }
            if ((o & 32) == 32) {
                list.add("STATE_SYMBOL_SYNC");
                flipped |= 32;
            }
            if ((o & 64) == 64) {
                list.add("STATE_GLO_STRING_SYNC");
                flipped |= 64;
            }
            if ((o & 128) == 128) {
                list.add("STATE_GLO_TOD_DECODED");
                flipped |= 128;
            }
            if ((o & 256) == 256) {
                list.add("STATE_BDS_D2_BIT_SYNC");
                flipped |= 256;
            }
            if ((o & 512) == 512) {
                list.add("STATE_BDS_D2_SUBFRAME_SYNC");
                flipped |= 512;
            }
            if ((o & 1024) == 1024) {
                list.add("STATE_GAL_E1BC_CODE_LOCK");
                flipped |= 1024;
            }
            if ((o & 2048) == 2048) {
                list.add("STATE_GAL_E1C_2ND_CODE_LOCK");
                flipped |= 2048;
            }
            if ((o & 4096) == 4096) {
                list.add("STATE_GAL_E1B_PAGE_SYNC");
                flipped |= 4096;
            }
            if ((o & 8192) == 8192) {
                list.add("STATE_SBAS_SYNC");
                flipped |= 8192;
            }
            if ((o & 16384) == 16384) {
                list.add("STATE_TOW_KNOWN");
                flipped |= 16384;
            }
            if ((o & 32768) == 32768) {
                list.add("STATE_GLO_TOD_KNOWN");
                flipped |= 32768;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssAccumulatedDeltaRangeState {
        public static final short ADR_STATE_CYCLE_SLIP = 4;
        public static final short ADR_STATE_RESET = 2;
        public static final short ADR_STATE_UNKNOWN = 0;
        public static final short ADR_STATE_VALID = 1;

        public static final String toString(short o) {
            if (o == 0) {
                return "ADR_STATE_UNKNOWN";
            }
            if (o == 1) {
                return "ADR_STATE_VALID";
            }
            if (o == 2) {
                return "ADR_STATE_RESET";
            }
            if (o == 4) {
                return "ADR_STATE_CYCLE_SLIP";
            }
            return "0x" + Integer.toHexString(Short.toUnsignedInt(o));
        }

        public static final String dumpBitfield(short o) {
            ArrayList<String> list = new ArrayList<>();
            short flipped = 0;
            list.add("ADR_STATE_UNKNOWN");
            if ((o & 1) == 1) {
                list.add("ADR_STATE_VALID");
                flipped = (short) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("ADR_STATE_RESET");
                flipped = (short) (flipped | 2);
            }
            if ((o & 4) == 4) {
                list.add("ADR_STATE_CYCLE_SLIP");
                flipped = (short) (flipped | 4);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Short.toUnsignedInt((short) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssClock {
        public short gnssClockFlags;
        public short leapSecond = 0;
        public long timeNs = 0;
        public double timeUncertaintyNs = 0.0d;
        public long fullBiasNs = 0;
        public double biasNs = 0.0d;
        public double biasUncertaintyNs = 0.0d;
        public double driftNsps = 0.0d;
        public double driftUncertaintyNsps = 0.0d;
        public int hwClockDiscontinuityCount = 0;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssClock.class) {
                return false;
            }
            GnssClock other = (GnssClock) otherObject;
            if (HidlSupport.deepEquals(Short.valueOf(this.gnssClockFlags), Short.valueOf(other.gnssClockFlags)) && this.leapSecond == other.leapSecond && this.timeNs == other.timeNs && this.timeUncertaintyNs == other.timeUncertaintyNs && this.fullBiasNs == other.fullBiasNs && this.biasNs == other.biasNs && this.biasUncertaintyNs == other.biasUncertaintyNs && this.driftNsps == other.driftNsps && this.driftUncertaintyNsps == other.driftUncertaintyNsps && this.hwClockDiscontinuityCount == other.hwClockDiscontinuityCount) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.gnssClockFlags))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.leapSecond))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.timeNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.timeUncertaintyNs))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.fullBiasNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.biasNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.biasUncertaintyNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.driftNsps))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.driftUncertaintyNsps))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.hwClockDiscontinuityCount))));
        }

        public final String toString() {
            return "{.gnssClockFlags = " + GnssClockFlags.dumpBitfield(this.gnssClockFlags) + ", .leapSecond = " + ((int) this.leapSecond) + ", .timeNs = " + this.timeNs + ", .timeUncertaintyNs = " + this.timeUncertaintyNs + ", .fullBiasNs = " + this.fullBiasNs + ", .biasNs = " + this.biasNs + ", .biasUncertaintyNs = " + this.biasUncertaintyNs + ", .driftNsps = " + this.driftNsps + ", .driftUncertaintyNsps = " + this.driftUncertaintyNsps + ", .hwClockDiscontinuityCount = " + this.hwClockDiscontinuityCount + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(72L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssClock> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssClock> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 72, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssClock _hidl_vec_element = new GnssClock();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 72);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.gnssClockFlags = _hidl_blob.getInt16(0 + _hidl_offset);
            this.leapSecond = _hidl_blob.getInt16(2 + _hidl_offset);
            this.timeNs = _hidl_blob.getInt64(8 + _hidl_offset);
            this.timeUncertaintyNs = _hidl_blob.getDouble(16 + _hidl_offset);
            this.fullBiasNs = _hidl_blob.getInt64(24 + _hidl_offset);
            this.biasNs = _hidl_blob.getDouble(32 + _hidl_offset);
            this.biasUncertaintyNs = _hidl_blob.getDouble(40 + _hidl_offset);
            this.driftNsps = _hidl_blob.getDouble(48 + _hidl_offset);
            this.driftUncertaintyNsps = _hidl_blob.getDouble(56 + _hidl_offset);
            this.hwClockDiscontinuityCount = _hidl_blob.getInt32(64 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(72);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssClock> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 72);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 72);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            _hidl_blob.putInt16(0 + _hidl_offset, this.gnssClockFlags);
            _hidl_blob.putInt16(2 + _hidl_offset, this.leapSecond);
            _hidl_blob.putInt64(8 + _hidl_offset, this.timeNs);
            _hidl_blob.putDouble(16 + _hidl_offset, this.timeUncertaintyNs);
            _hidl_blob.putInt64(24 + _hidl_offset, this.fullBiasNs);
            _hidl_blob.putDouble(32 + _hidl_offset, this.biasNs);
            _hidl_blob.putDouble(40 + _hidl_offset, this.biasUncertaintyNs);
            _hidl_blob.putDouble(48 + _hidl_offset, this.driftNsps);
            _hidl_blob.putDouble(56 + _hidl_offset, this.driftUncertaintyNsps);
            _hidl_blob.putInt32(64 + _hidl_offset, this.hwClockDiscontinuityCount);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssMeasurement {
        public short accumulatedDeltaRangeState;
        public int flags;
        public int state;
        public short svid = 0;
        public byte constellation = 0;
        public double timeOffsetNs = 0.0d;
        public long receivedSvTimeInNs = 0;
        public long receivedSvTimeUncertaintyInNs = 0;
        public double cN0DbHz = 0.0d;
        public double pseudorangeRateMps = 0.0d;
        public double pseudorangeRateUncertaintyMps = 0.0d;
        public double accumulatedDeltaRangeM = 0.0d;
        public double accumulatedDeltaRangeUncertaintyM = 0.0d;
        public float carrierFrequencyHz = 0.0f;
        public long carrierCycles = 0;
        public double carrierPhase = 0.0d;
        public double carrierPhaseUncertainty = 0.0d;
        public byte multipathIndicator = 0;
        public double snrDb = 0.0d;
        public double agcLevelDb = 0.0d;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssMeasurement.class) {
                return false;
            }
            GnssMeasurement other = (GnssMeasurement) otherObject;
            if (HidlSupport.deepEquals(Integer.valueOf(this.flags), Integer.valueOf(other.flags)) && this.svid == other.svid && this.constellation == other.constellation && this.timeOffsetNs == other.timeOffsetNs && HidlSupport.deepEquals(Integer.valueOf(this.state), Integer.valueOf(other.state)) && this.receivedSvTimeInNs == other.receivedSvTimeInNs && this.receivedSvTimeUncertaintyInNs == other.receivedSvTimeUncertaintyInNs && this.cN0DbHz == other.cN0DbHz && this.pseudorangeRateMps == other.pseudorangeRateMps && this.pseudorangeRateUncertaintyMps == other.pseudorangeRateUncertaintyMps && HidlSupport.deepEquals(Short.valueOf(this.accumulatedDeltaRangeState), Short.valueOf(other.accumulatedDeltaRangeState)) && this.accumulatedDeltaRangeM == other.accumulatedDeltaRangeM && this.accumulatedDeltaRangeUncertaintyM == other.accumulatedDeltaRangeUncertaintyM && this.carrierFrequencyHz == other.carrierFrequencyHz && this.carrierCycles == other.carrierCycles && this.carrierPhase == other.carrierPhase && this.carrierPhaseUncertainty == other.carrierPhaseUncertainty && this.multipathIndicator == other.multipathIndicator && this.snrDb == other.snrDb && this.agcLevelDb == other.agcLevelDb) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.flags))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.svid))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.constellation))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.timeOffsetNs))), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.state))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.receivedSvTimeInNs))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.receivedSvTimeUncertaintyInNs))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.cN0DbHz))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.pseudorangeRateMps))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.pseudorangeRateUncertaintyMps))), Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.accumulatedDeltaRangeState))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.accumulatedDeltaRangeM))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.accumulatedDeltaRangeUncertaintyM))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.carrierFrequencyHz))), Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.carrierCycles))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.carrierPhase))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.carrierPhaseUncertainty))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.multipathIndicator))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.snrDb))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.agcLevelDb))));
        }

        public final String toString() {
            return "{.flags = " + GnssMeasurementFlags.dumpBitfield(this.flags) + ", .svid = " + ((int) this.svid) + ", .constellation = " + GnssConstellationType.toString(this.constellation) + ", .timeOffsetNs = " + this.timeOffsetNs + ", .state = " + GnssMeasurementState.dumpBitfield(this.state) + ", .receivedSvTimeInNs = " + this.receivedSvTimeInNs + ", .receivedSvTimeUncertaintyInNs = " + this.receivedSvTimeUncertaintyInNs + ", .cN0DbHz = " + this.cN0DbHz + ", .pseudorangeRateMps = " + this.pseudorangeRateMps + ", .pseudorangeRateUncertaintyMps = " + this.pseudorangeRateUncertaintyMps + ", .accumulatedDeltaRangeState = " + GnssAccumulatedDeltaRangeState.dumpBitfield(this.accumulatedDeltaRangeState) + ", .accumulatedDeltaRangeM = " + this.accumulatedDeltaRangeM + ", .accumulatedDeltaRangeUncertaintyM = " + this.accumulatedDeltaRangeUncertaintyM + ", .carrierFrequencyHz = " + this.carrierFrequencyHz + ", .carrierCycles = " + this.carrierCycles + ", .carrierPhase = " + this.carrierPhase + ", .carrierPhaseUncertainty = " + this.carrierPhaseUncertainty + ", .multipathIndicator = " + GnssMultipathIndicator.toString(this.multipathIndicator) + ", .snrDb = " + this.snrDb + ", .agcLevelDb = " + this.agcLevelDb + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(144L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssMeasurement> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssMeasurement> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 144, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssMeasurement _hidl_vec_element = new GnssMeasurement();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 144);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.flags = _hidl_blob.getInt32(0 + _hidl_offset);
            this.svid = _hidl_blob.getInt16(4 + _hidl_offset);
            this.constellation = _hidl_blob.getInt8(6 + _hidl_offset);
            this.timeOffsetNs = _hidl_blob.getDouble(8 + _hidl_offset);
            this.state = _hidl_blob.getInt32(16 + _hidl_offset);
            this.receivedSvTimeInNs = _hidl_blob.getInt64(24 + _hidl_offset);
            this.receivedSvTimeUncertaintyInNs = _hidl_blob.getInt64(32 + _hidl_offset);
            this.cN0DbHz = _hidl_blob.getDouble(40 + _hidl_offset);
            this.pseudorangeRateMps = _hidl_blob.getDouble(48 + _hidl_offset);
            this.pseudorangeRateUncertaintyMps = _hidl_blob.getDouble(56 + _hidl_offset);
            this.accumulatedDeltaRangeState = _hidl_blob.getInt16(64 + _hidl_offset);
            this.accumulatedDeltaRangeM = _hidl_blob.getDouble(72 + _hidl_offset);
            this.accumulatedDeltaRangeUncertaintyM = _hidl_blob.getDouble(80 + _hidl_offset);
            this.carrierFrequencyHz = _hidl_blob.getFloat(88 + _hidl_offset);
            this.carrierCycles = _hidl_blob.getInt64(96 + _hidl_offset);
            this.carrierPhase = _hidl_blob.getDouble(104 + _hidl_offset);
            this.carrierPhaseUncertainty = _hidl_blob.getDouble(112 + _hidl_offset);
            this.multipathIndicator = _hidl_blob.getInt8(120 + _hidl_offset);
            this.snrDb = _hidl_blob.getDouble(128 + _hidl_offset);
            this.agcLevelDb = _hidl_blob.getDouble(136 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(144);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssMeasurement> _hidl_vec) {
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
            _hidl_blob.putInt32(0 + _hidl_offset, this.flags);
            _hidl_blob.putInt16(4 + _hidl_offset, this.svid);
            _hidl_blob.putInt8(6 + _hidl_offset, this.constellation);
            _hidl_blob.putDouble(8 + _hidl_offset, this.timeOffsetNs);
            _hidl_blob.putInt32(16 + _hidl_offset, this.state);
            _hidl_blob.putInt64(24 + _hidl_offset, this.receivedSvTimeInNs);
            _hidl_blob.putInt64(32 + _hidl_offset, this.receivedSvTimeUncertaintyInNs);
            _hidl_blob.putDouble(40 + _hidl_offset, this.cN0DbHz);
            _hidl_blob.putDouble(48 + _hidl_offset, this.pseudorangeRateMps);
            _hidl_blob.putDouble(56 + _hidl_offset, this.pseudorangeRateUncertaintyMps);
            _hidl_blob.putInt16(64 + _hidl_offset, this.accumulatedDeltaRangeState);
            _hidl_blob.putDouble(72 + _hidl_offset, this.accumulatedDeltaRangeM);
            _hidl_blob.putDouble(80 + _hidl_offset, this.accumulatedDeltaRangeUncertaintyM);
            _hidl_blob.putFloat(88 + _hidl_offset, this.carrierFrequencyHz);
            _hidl_blob.putInt64(96 + _hidl_offset, this.carrierCycles);
            _hidl_blob.putDouble(104 + _hidl_offset, this.carrierPhase);
            _hidl_blob.putDouble(112 + _hidl_offset, this.carrierPhaseUncertainty);
            _hidl_blob.putInt8(120 + _hidl_offset, this.multipathIndicator);
            _hidl_blob.putDouble(128 + _hidl_offset, this.snrDb);
            _hidl_blob.putDouble(136 + _hidl_offset, this.agcLevelDb);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssData {
        public int measurementCount = 0;
        public GnssMeasurement[] measurements = new GnssMeasurement[64];
        public GnssClock clock = new GnssClock();

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssData.class) {
                return false;
            }
            GnssData other = (GnssData) otherObject;
            if (this.measurementCount == other.measurementCount && HidlSupport.deepEquals(this.measurements, other.measurements) && HidlSupport.deepEquals(this.clock, other.clock)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.measurementCount))), Integer.valueOf(HidlSupport.deepHashCode(this.measurements)), Integer.valueOf(HidlSupport.deepHashCode(this.clock)));
        }

        public final String toString() {
            return "{.measurementCount = " + this.measurementCount + ", .measurements = " + Arrays.toString(this.measurements) + ", .clock = " + this.clock + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(9296L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssData> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssData> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 9296, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssData _hidl_vec_element = new GnssData();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 9296);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.measurementCount = _hidl_blob.getInt32(0 + _hidl_offset);
            long _hidl_array_offset_0 = 8 + _hidl_offset;
            for (int _hidl_index_0_0 = 0; _hidl_index_0_0 < 64; _hidl_index_0_0++) {
                this.measurements[_hidl_index_0_0] = new GnssMeasurement();
                this.measurements[_hidl_index_0_0].readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_array_offset_0);
                _hidl_array_offset_0 += 144;
            }
            this.clock.readEmbeddedFromParcel(parcel, _hidl_blob, 9224 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(9296);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssData> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 9296);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 9296);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            _hidl_blob.putInt32(0 + _hidl_offset, this.measurementCount);
            long _hidl_array_offset_0 = 8 + _hidl_offset;
            for (int _hidl_index_0_0 = 0; _hidl_index_0_0 < 64; _hidl_index_0_0++) {
                this.measurements[_hidl_index_0_0].writeEmbeddedToBlob(_hidl_blob, _hidl_array_offset_0);
                _hidl_array_offset_0 += 144;
            }
            this.clock.writeEmbeddedToBlob(_hidl_blob, 9224 + _hidl_offset);
        }
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IGnssMeasurementCallback {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss@1.0::IGnssMeasurementCallback]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback
        public void GnssMeasurementCb(GnssData data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssMeasurementCallback.kInterfaceName);
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends HwBinder implements IGnssMeasurementCallback {
        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnssMeasurementCallback.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnssMeasurementCallback.kInterfaceName;
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-67, -92, -110, -20, 64, 33, -47, 56, 105, -34, 114, -67, 111, -116, 21, -59, -125, 123, 120, -42, 19, 107, -115, 83, -114, -2, -59, 50, 5, 115, -91, -20}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
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
                    _hidl_request.enforceInterface(IGnssMeasurementCallback.kInterfaceName);
                    GnssData data = new GnssData();
                    data.readFromParcel(_hidl_request);
                    GnssMeasurementCb(data);
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
