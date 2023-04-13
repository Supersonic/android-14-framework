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
public interface IGnssDebug extends IBase {
    public static final String kInterfaceName = "android.hardware.gnss@1.0::IGnssDebug";

    @Override // android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    DebugData getDebugData() throws RemoteException;

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

    static IGnssDebug asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IGnssDebug)) {
            return (IGnssDebug) iface;
        }
        IGnssDebug proxy = new Proxy(binder);
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

    static IGnssDebug castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IGnssDebug getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IGnssDebug getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IGnssDebug getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IGnssDebug getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes.dex */
    public static final class SatelliteEphemerisType {
        public static final byte ALMANAC_ONLY = 1;
        public static final byte EPHEMERIS = 0;
        public static final byte NOT_AVAILABLE = 2;

        public static final String toString(byte o) {
            if (o == 0) {
                return "EPHEMERIS";
            }
            if (o == 1) {
                return "ALMANAC_ONLY";
            }
            if (o == 2) {
                return "NOT_AVAILABLE";
            }
            return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
        }

        public static final String dumpBitfield(byte o) {
            ArrayList<String> list = new ArrayList<>();
            byte flipped = 0;
            list.add("EPHEMERIS");
            if ((o & 1) == 1) {
                list.add("ALMANAC_ONLY");
                flipped = (byte) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("NOT_AVAILABLE");
                flipped = (byte) (flipped | 2);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class SatelliteEphemerisSource {
        public static final byte DEMODULATED = 0;
        public static final byte OTHER = 3;
        public static final byte OTHER_SERVER_PROVIDED = 2;
        public static final byte SUPL_PROVIDED = 1;

        public static final String toString(byte o) {
            if (o == 0) {
                return "DEMODULATED";
            }
            if (o == 1) {
                return "SUPL_PROVIDED";
            }
            if (o == 2) {
                return "OTHER_SERVER_PROVIDED";
            }
            if (o == 3) {
                return "OTHER";
            }
            return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
        }

        public static final String dumpBitfield(byte o) {
            ArrayList<String> list = new ArrayList<>();
            byte flipped = 0;
            list.add("DEMODULATED");
            if ((o & 1) == 1) {
                list.add("SUPL_PROVIDED");
                flipped = (byte) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("OTHER_SERVER_PROVIDED");
                flipped = (byte) (flipped | 2);
            }
            if ((o & 3) == 3) {
                list.add("OTHER");
                flipped = (byte) (flipped | 3);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class SatelliteEphemerisHealth {
        public static final byte BAD = 1;
        public static final byte GOOD = 0;
        public static final byte UNKNOWN = 2;

        public static final String toString(byte o) {
            if (o == 0) {
                return "GOOD";
            }
            if (o == 1) {
                return "BAD";
            }
            if (o == 2) {
                return "UNKNOWN";
            }
            return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
        }

        public static final String dumpBitfield(byte o) {
            ArrayList<String> list = new ArrayList<>();
            byte flipped = 0;
            list.add("GOOD");
            if ((o & 1) == 1) {
                list.add("BAD");
                flipped = (byte) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("UNKNOWN");
                flipped = (byte) (flipped | 2);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class PositionDebug {
        public boolean valid = false;
        public double latitudeDegrees = 0.0d;
        public double longitudeDegrees = 0.0d;
        public float altitudeMeters = 0.0f;
        public float speedMetersPerSec = 0.0f;
        public float bearingDegrees = 0.0f;
        public double horizontalAccuracyMeters = 0.0d;
        public double verticalAccuracyMeters = 0.0d;
        public double speedAccuracyMetersPerSecond = 0.0d;
        public double bearingAccuracyDegrees = 0.0d;
        public float ageSeconds = 0.0f;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != PositionDebug.class) {
                return false;
            }
            PositionDebug other = (PositionDebug) otherObject;
            if (this.valid == other.valid && this.latitudeDegrees == other.latitudeDegrees && this.longitudeDegrees == other.longitudeDegrees && this.altitudeMeters == other.altitudeMeters && this.speedMetersPerSec == other.speedMetersPerSec && this.bearingDegrees == other.bearingDegrees && this.horizontalAccuracyMeters == other.horizontalAccuracyMeters && this.verticalAccuracyMeters == other.verticalAccuracyMeters && this.speedAccuracyMetersPerSecond == other.speedAccuracyMetersPerSecond && this.bearingAccuracyDegrees == other.bearingAccuracyDegrees && this.ageSeconds == other.ageSeconds) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.valid))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.latitudeDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.longitudeDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.altitudeMeters))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.speedMetersPerSec))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.bearingDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.horizontalAccuracyMeters))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.verticalAccuracyMeters))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.speedAccuracyMetersPerSecond))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.bearingAccuracyDegrees))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.ageSeconds))));
        }

        public final String toString() {
            return "{.valid = " + this.valid + ", .latitudeDegrees = " + this.latitudeDegrees + ", .longitudeDegrees = " + this.longitudeDegrees + ", .altitudeMeters = " + this.altitudeMeters + ", .speedMetersPerSec = " + this.speedMetersPerSec + ", .bearingDegrees = " + this.bearingDegrees + ", .horizontalAccuracyMeters = " + this.horizontalAccuracyMeters + ", .verticalAccuracyMeters = " + this.verticalAccuracyMeters + ", .speedAccuracyMetersPerSecond = " + this.speedAccuracyMetersPerSecond + ", .bearingAccuracyDegrees = " + this.bearingAccuracyDegrees + ", .ageSeconds = " + this.ageSeconds + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(80L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<PositionDebug> readVectorFromParcel(HwParcel parcel) {
            ArrayList<PositionDebug> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 80, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                PositionDebug _hidl_vec_element = new PositionDebug();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 80);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.valid = _hidl_blob.getBool(0 + _hidl_offset);
            this.latitudeDegrees = _hidl_blob.getDouble(8 + _hidl_offset);
            this.longitudeDegrees = _hidl_blob.getDouble(16 + _hidl_offset);
            this.altitudeMeters = _hidl_blob.getFloat(24 + _hidl_offset);
            this.speedMetersPerSec = _hidl_blob.getFloat(28 + _hidl_offset);
            this.bearingDegrees = _hidl_blob.getFloat(32 + _hidl_offset);
            this.horizontalAccuracyMeters = _hidl_blob.getDouble(40 + _hidl_offset);
            this.verticalAccuracyMeters = _hidl_blob.getDouble(48 + _hidl_offset);
            this.speedAccuracyMetersPerSecond = _hidl_blob.getDouble(56 + _hidl_offset);
            this.bearingAccuracyDegrees = _hidl_blob.getDouble(64 + _hidl_offset);
            this.ageSeconds = _hidl_blob.getFloat(72 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(80);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<PositionDebug> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 80);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 80);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            _hidl_blob.putBool(0 + _hidl_offset, this.valid);
            _hidl_blob.putDouble(8 + _hidl_offset, this.latitudeDegrees);
            _hidl_blob.putDouble(16 + _hidl_offset, this.longitudeDegrees);
            _hidl_blob.putFloat(24 + _hidl_offset, this.altitudeMeters);
            _hidl_blob.putFloat(28 + _hidl_offset, this.speedMetersPerSec);
            _hidl_blob.putFloat(32 + _hidl_offset, this.bearingDegrees);
            _hidl_blob.putDouble(40 + _hidl_offset, this.horizontalAccuracyMeters);
            _hidl_blob.putDouble(48 + _hidl_offset, this.verticalAccuracyMeters);
            _hidl_blob.putDouble(56 + _hidl_offset, this.speedAccuracyMetersPerSecond);
            _hidl_blob.putDouble(64 + _hidl_offset, this.bearingAccuracyDegrees);
            _hidl_blob.putFloat(72 + _hidl_offset, this.ageSeconds);
        }
    }

    /* loaded from: classes.dex */
    public static final class TimeDebug {
        public long timeEstimate = 0;
        public float timeUncertaintyNs = 0.0f;
        public float frequencyUncertaintyNsPerSec = 0.0f;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != TimeDebug.class) {
                return false;
            }
            TimeDebug other = (TimeDebug) otherObject;
            if (this.timeEstimate == other.timeEstimate && this.timeUncertaintyNs == other.timeUncertaintyNs && this.frequencyUncertaintyNsPerSec == other.frequencyUncertaintyNsPerSec) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Long.valueOf(this.timeEstimate))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.timeUncertaintyNs))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.frequencyUncertaintyNsPerSec))));
        }

        public final String toString() {
            return "{.timeEstimate = " + this.timeEstimate + ", .timeUncertaintyNs = " + this.timeUncertaintyNs + ", .frequencyUncertaintyNsPerSec = " + this.frequencyUncertaintyNsPerSec + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(16L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<TimeDebug> readVectorFromParcel(HwParcel parcel) {
            ArrayList<TimeDebug> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                TimeDebug _hidl_vec_element = new TimeDebug();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 16);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.timeEstimate = _hidl_blob.getInt64(0 + _hidl_offset);
            this.timeUncertaintyNs = _hidl_blob.getFloat(8 + _hidl_offset);
            this.frequencyUncertaintyNsPerSec = _hidl_blob.getFloat(12 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(16);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<TimeDebug> _hidl_vec) {
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
            _hidl_blob.putInt64(0 + _hidl_offset, this.timeEstimate);
            _hidl_blob.putFloat(8 + _hidl_offset, this.timeUncertaintyNs);
            _hidl_blob.putFloat(12 + _hidl_offset, this.frequencyUncertaintyNsPerSec);
        }
    }

    /* loaded from: classes.dex */
    public static final class SatelliteData {
        public short svid = 0;
        public byte constellation = 0;
        public byte ephemerisType = 0;
        public byte ephemerisSource = 0;
        public byte ephemerisHealth = 0;
        public float ephemerisAgeSeconds = 0.0f;
        public boolean serverPredictionIsAvailable = false;
        public float serverPredictionAgeSeconds = 0.0f;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != SatelliteData.class) {
                return false;
            }
            SatelliteData other = (SatelliteData) otherObject;
            if (this.svid == other.svid && this.constellation == other.constellation && this.ephemerisType == other.ephemerisType && this.ephemerisSource == other.ephemerisSource && this.ephemerisHealth == other.ephemerisHealth && this.ephemerisAgeSeconds == other.ephemerisAgeSeconds && this.serverPredictionIsAvailable == other.serverPredictionIsAvailable && this.serverPredictionAgeSeconds == other.serverPredictionAgeSeconds) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Short.valueOf(this.svid))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.constellation))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.ephemerisType))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.ephemerisSource))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.ephemerisHealth))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.ephemerisAgeSeconds))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.serverPredictionIsAvailable))), Integer.valueOf(HidlSupport.deepHashCode(Float.valueOf(this.serverPredictionAgeSeconds))));
        }

        public final String toString() {
            return "{.svid = " + ((int) this.svid) + ", .constellation = " + GnssConstellationType.toString(this.constellation) + ", .ephemerisType = " + SatelliteEphemerisType.toString(this.ephemerisType) + ", .ephemerisSource = " + SatelliteEphemerisSource.toString(this.ephemerisSource) + ", .ephemerisHealth = " + SatelliteEphemerisHealth.toString(this.ephemerisHealth) + ", .ephemerisAgeSeconds = " + this.ephemerisAgeSeconds + ", .serverPredictionIsAvailable = " + this.serverPredictionIsAvailable + ", .serverPredictionAgeSeconds = " + this.serverPredictionAgeSeconds + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(20L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<SatelliteData> readVectorFromParcel(HwParcel parcel) {
            ArrayList<SatelliteData> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 20, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                SatelliteData _hidl_vec_element = new SatelliteData();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 20);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.svid = _hidl_blob.getInt16(0 + _hidl_offset);
            this.constellation = _hidl_blob.getInt8(2 + _hidl_offset);
            this.ephemerisType = _hidl_blob.getInt8(3 + _hidl_offset);
            this.ephemerisSource = _hidl_blob.getInt8(4 + _hidl_offset);
            this.ephemerisHealth = _hidl_blob.getInt8(5 + _hidl_offset);
            this.ephemerisAgeSeconds = _hidl_blob.getFloat(8 + _hidl_offset);
            this.serverPredictionIsAvailable = _hidl_blob.getBool(12 + _hidl_offset);
            this.serverPredictionAgeSeconds = _hidl_blob.getFloat(16 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(20);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<SatelliteData> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 20);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 20);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            _hidl_blob.putInt16(0 + _hidl_offset, this.svid);
            _hidl_blob.putInt8(2 + _hidl_offset, this.constellation);
            _hidl_blob.putInt8(3 + _hidl_offset, this.ephemerisType);
            _hidl_blob.putInt8(4 + _hidl_offset, this.ephemerisSource);
            _hidl_blob.putInt8(5 + _hidl_offset, this.ephemerisHealth);
            _hidl_blob.putFloat(8 + _hidl_offset, this.ephemerisAgeSeconds);
            _hidl_blob.putBool(12 + _hidl_offset, this.serverPredictionIsAvailable);
            _hidl_blob.putFloat(16 + _hidl_offset, this.serverPredictionAgeSeconds);
        }
    }

    /* loaded from: classes.dex */
    public static final class DebugData {
        public PositionDebug position = new PositionDebug();
        public TimeDebug time = new TimeDebug();
        public ArrayList<SatelliteData> satelliteDataArray = new ArrayList<>();

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != DebugData.class) {
                return false;
            }
            DebugData other = (DebugData) otherObject;
            if (HidlSupport.deepEquals(this.position, other.position) && HidlSupport.deepEquals(this.time, other.time) && HidlSupport.deepEquals(this.satelliteDataArray, other.satelliteDataArray)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.position)), Integer.valueOf(HidlSupport.deepHashCode(this.time)), Integer.valueOf(HidlSupport.deepHashCode(this.satelliteDataArray)));
        }

        public final String toString() {
            return "{.position = " + this.position + ", .time = " + this.time + ", .satelliteDataArray = " + this.satelliteDataArray + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(112L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<DebugData> readVectorFromParcel(HwParcel parcel) {
            ArrayList<DebugData> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 112, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                DebugData _hidl_vec_element = new DebugData();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 112);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.position.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 0);
            this.time.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 80);
            int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 96 + 8);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 20, _hidl_blob.handle(), _hidl_offset + 96 + 0, true);
            this.satelliteDataArray.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                SatelliteData _hidl_vec_element = new SatelliteData();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 20);
                this.satelliteDataArray.add(_hidl_vec_element);
            }
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(112);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<DebugData> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 112);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 112);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            this.position.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 0);
            this.time.writeEmbeddedToBlob(_hidl_blob, 80 + _hidl_offset);
            int _hidl_vec_size = this.satelliteDataArray.size();
            _hidl_blob.putInt32(_hidl_offset + 96 + 8, _hidl_vec_size);
            _hidl_blob.putBool(_hidl_offset + 96 + 12, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 20);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                this.satelliteDataArray.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 20);
            }
            _hidl_blob.putBlob(96 + _hidl_offset + 0, childBlob);
        }
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IGnssDebug {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss@1.0::IGnssDebug]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug
        public DebugData getDebugData() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssDebug.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                DebugData _hidl_out_debugData = new DebugData();
                _hidl_out_debugData.readFromParcel(_hidl_reply);
                return _hidl_out_debugData;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends HwBinder implements IGnssDebug {
        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnssDebug.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnssDebug.kInterfaceName;
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{69, 66, 18, 43, -106, -5, MidiConstants.STATUS_SONG_POSITION, 113, 1, -53, -126, 34, -70, -5, 118, -25, -56, MidiConstants.STATUS_CHANNEL_PRESSURE, 50, -39, 119, -35, 16, 88, -19, -40, -27, -120, 28, -91, 117, 47}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.V1_0.IGnssDebug, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IGnssDebug.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(IGnssDebug.kInterfaceName);
                    DebugData _hidl_out_debugData = getDebugData();
                    _hidl_reply.writeStatus(0);
                    _hidl_out_debugData.writeToParcel(_hidl_reply);
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
