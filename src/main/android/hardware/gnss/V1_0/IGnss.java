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
public interface IGnss extends IBase {
    public static final String kInterfaceName = "android.hardware.gnss@1.0::IGnss";

    @Override // android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    void cleanup() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    void deleteAidingData(short s) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    IAGnss getExtensionAGnss() throws RemoteException;

    IAGnssRil getExtensionAGnssRil() throws RemoteException;

    IGnssBatching getExtensionGnssBatching() throws RemoteException;

    IGnssConfiguration getExtensionGnssConfiguration() throws RemoteException;

    IGnssDebug getExtensionGnssDebug() throws RemoteException;

    IGnssGeofencing getExtensionGnssGeofencing() throws RemoteException;

    IGnssMeasurement getExtensionGnssMeasurement() throws RemoteException;

    IGnssNavigationMessage getExtensionGnssNavigationMessage() throws RemoteException;

    IGnssNi getExtensionGnssNi() throws RemoteException;

    IGnssXtra getExtensionXtra() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    boolean injectLocation(double d, double d2, float f) throws RemoteException;

    boolean injectTime(long j, long j2, int i) throws RemoteException;

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

    boolean setCallback(IGnssCallback iGnssCallback) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    boolean setPositionMode(byte b, int i, int i2, int i3, int i4) throws RemoteException;

    boolean start() throws RemoteException;

    boolean stop() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IGnss asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IGnss)) {
            return (IGnss) iface;
        }
        IGnss proxy = new Proxy(binder);
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

    static IGnss castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IGnss getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IGnss getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IGnss getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IGnss getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes.dex */
    public static final class GnssPositionMode {
        public static final byte MS_ASSISTED = 2;
        public static final byte MS_BASED = 1;
        public static final byte STANDALONE = 0;

        public static final String toString(byte o) {
            if (o == 0) {
                return "STANDALONE";
            }
            if (o == 1) {
                return "MS_BASED";
            }
            if (o == 2) {
                return "MS_ASSISTED";
            }
            return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
        }

        public static final String dumpBitfield(byte o) {
            ArrayList<String> list = new ArrayList<>();
            byte flipped = 0;
            list.add("STANDALONE");
            if ((o & 1) == 1) {
                list.add("MS_BASED");
                flipped = (byte) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("MS_ASSISTED");
                flipped = (byte) (flipped | 2);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssPositionRecurrence {
        public static final int RECURRENCE_PERIODIC = 0;
        public static final int RECURRENCE_SINGLE = 1;

        public static final String toString(int o) {
            if (o == 0) {
                return "RECURRENCE_PERIODIC";
            }
            if (o == 1) {
                return "RECURRENCE_SINGLE";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            list.add("RECURRENCE_PERIODIC");
            if ((o & 1) == 1) {
                list.add("RECURRENCE_SINGLE");
                flipped = 0 | 1;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class GnssAidingData {
        public static final short DELETE_ALL = -1;
        public static final short DELETE_ALMANAC = 2;
        public static final short DELETE_CELLDB_INFO = Short.MIN_VALUE;
        public static final short DELETE_EPHEMERIS = 1;
        public static final short DELETE_HEALTH = 64;
        public static final short DELETE_IONO = 16;
        public static final short DELETE_POSITION = 4;
        public static final short DELETE_RTI = 1024;
        public static final short DELETE_SADATA = 512;
        public static final short DELETE_SVDIR = 128;
        public static final short DELETE_SVSTEER = 256;
        public static final short DELETE_TIME = 8;
        public static final short DELETE_UTC = 32;

        public static final String toString(short o) {
            if (o == 1) {
                return "DELETE_EPHEMERIS";
            }
            if (o == 2) {
                return "DELETE_ALMANAC";
            }
            if (o == 4) {
                return "DELETE_POSITION";
            }
            if (o == 8) {
                return "DELETE_TIME";
            }
            if (o == 16) {
                return "DELETE_IONO";
            }
            if (o == 32) {
                return "DELETE_UTC";
            }
            if (o == 64) {
                return "DELETE_HEALTH";
            }
            if (o == 128) {
                return "DELETE_SVDIR";
            }
            if (o == 256) {
                return "DELETE_SVSTEER";
            }
            if (o == 512) {
                return "DELETE_SADATA";
            }
            if (o == 1024) {
                return "DELETE_RTI";
            }
            if (o == Short.MIN_VALUE) {
                return "DELETE_CELLDB_INFO";
            }
            if (o == -1) {
                return "DELETE_ALL";
            }
            return "0x" + Integer.toHexString(Short.toUnsignedInt(o));
        }

        public static final String dumpBitfield(short o) {
            ArrayList<String> list = new ArrayList<>();
            short flipped = 0;
            if ((o & 1) == 1) {
                list.add("DELETE_EPHEMERIS");
                flipped = (short) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("DELETE_ALMANAC");
                flipped = (short) (flipped | 2);
            }
            if ((o & 4) == 4) {
                list.add("DELETE_POSITION");
                flipped = (short) (flipped | 4);
            }
            if ((o & 8) == 8) {
                list.add("DELETE_TIME");
                flipped = (short) (flipped | 8);
            }
            if ((o & 16) == 16) {
                list.add("DELETE_IONO");
                flipped = (short) (flipped | 16);
            }
            if ((o & 32) == 32) {
                list.add("DELETE_UTC");
                flipped = (short) (flipped | 32);
            }
            if ((o & 64) == 64) {
                list.add("DELETE_HEALTH");
                flipped = (short) (flipped | 64);
            }
            if ((o & 128) == 128) {
                list.add("DELETE_SVDIR");
                flipped = (short) (flipped | 128);
            }
            if ((o & DELETE_SVSTEER) == 256) {
                list.add("DELETE_SVSTEER");
                flipped = (short) (flipped | DELETE_SVSTEER);
            }
            if ((o & DELETE_SADATA) == 512) {
                list.add("DELETE_SADATA");
                flipped = (short) (flipped | DELETE_SADATA);
            }
            if ((o & 1024) == 1024) {
                list.add("DELETE_RTI");
                flipped = (short) (flipped | 1024);
            }
            if ((o & Short.MIN_VALUE) == -32768) {
                list.add("DELETE_CELLDB_INFO");
                flipped = (short) (flipped | Short.MIN_VALUE);
            }
            if ((o & (-1)) == -1) {
                list.add("DELETE_ALL");
                flipped = (short) (flipped | (-1));
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Short.toUnsignedInt((short) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IGnss {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss@1.0::IGnss]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public boolean setCallback(IGnssCallback callback) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            _hidl_request.writeStrongBinder(callback == null ? null : callback.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public boolean start() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public boolean stop() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public void cleanup() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public boolean injectTime(long timeMs, long timeReferenceMs, int uncertaintyMs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            _hidl_request.writeInt64(timeMs);
            _hidl_request.writeInt64(timeReferenceMs);
            _hidl_request.writeInt32(uncertaintyMs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public boolean injectLocation(double latitudeDegrees, double longitudeDegrees, float accuracyMeters) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            _hidl_request.writeDouble(latitudeDegrees);
            _hidl_request.writeDouble(longitudeDegrees);
            _hidl_request.writeFloat(accuracyMeters);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public void deleteAidingData(short aidingDataFlags) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            _hidl_request.writeInt16(aidingDataFlags);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public boolean setPositionMode(byte mode, int recurrence, int minIntervalMs, int preferredAccuracyMeters, int preferredTimeMs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            _hidl_request.writeInt8(mode);
            _hidl_request.writeInt32(recurrence);
            _hidl_request.writeInt32(minIntervalMs);
            _hidl_request.writeInt32(preferredAccuracyMeters);
            _hidl_request.writeInt32(preferredTimeMs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IAGnssRil getExtensionAGnssRil() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IAGnssRil _hidl_out_aGnssRilIface = IAGnssRil.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_aGnssRilIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssGeofencing getExtensionGnssGeofencing() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssGeofencing _hidl_out_gnssGeofencingIface = IGnssGeofencing.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssGeofencingIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IAGnss getExtensionAGnss() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IAGnss _hidl_out_aGnssIface = IAGnss.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_aGnssIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssNi getExtensionGnssNi() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssNi _hidl_out_gnssNiIface = IGnssNi.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssNiIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssMeasurement getExtensionGnssMeasurement() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssMeasurement _hidl_out_gnssMeasurementIface = IGnssMeasurement.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssMeasurementIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssNavigationMessage getExtensionGnssNavigationMessage() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(14, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssNavigationMessage _hidl_out_gnssNavigationIface = IGnssNavigationMessage.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssNavigationIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssXtra getExtensionXtra() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(15, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssXtra _hidl_out_xtraIface = IGnssXtra.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_xtraIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssConfiguration getExtensionGnssConfiguration() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssConfiguration _hidl_out_gnssConfigIface = IGnssConfiguration.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssConfigIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssDebug getExtensionGnssDebug() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(17, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssDebug _hidl_out_debugIface = IGnssDebug.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_debugIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssBatching getExtensionGnssBatching() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(18, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssBatching _hidl_out_batchingIface = IGnssBatching.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_batchingIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends HwBinder implements IGnss {
        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnss.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnss.kInterfaceName;
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-19, -26, -105, 16, -61, -87, 92, 44, -66, -127, -114, 108, -117, -73, 44, 120, 22, -126, 63, -84, -27, -4, 33, -63, 119, 49, -78, 111, 65, -39, 77, 101}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IGnss.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssCallback callback = IGnssCallback.asInterface(_hidl_request.readStrongBinder());
                    boolean _hidl_out_success = setCallback(callback);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    boolean _hidl_out_success2 = start();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success2);
                    _hidl_reply.send();
                    return;
                case 3:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    boolean _hidl_out_success3 = stop();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success3);
                    _hidl_reply.send();
                    return;
                case 4:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    cleanup();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 5:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    long timeMs = _hidl_request.readInt64();
                    long timeReferenceMs = _hidl_request.readInt64();
                    int uncertaintyMs = _hidl_request.readInt32();
                    boolean _hidl_out_success4 = injectTime(timeMs, timeReferenceMs, uncertaintyMs);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success4);
                    _hidl_reply.send();
                    return;
                case 6:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    double latitudeDegrees = _hidl_request.readDouble();
                    double longitudeDegrees = _hidl_request.readDouble();
                    float accuracyMeters = _hidl_request.readFloat();
                    boolean _hidl_out_success5 = injectLocation(latitudeDegrees, longitudeDegrees, accuracyMeters);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success5);
                    _hidl_reply.send();
                    return;
                case 7:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    short aidingDataFlags = _hidl_request.readInt16();
                    deleteAidingData(aidingDataFlags);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 8:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    byte mode = _hidl_request.readInt8();
                    int recurrence = _hidl_request.readInt32();
                    int minIntervalMs = _hidl_request.readInt32();
                    int preferredAccuracyMeters = _hidl_request.readInt32();
                    int preferredTimeMs = _hidl_request.readInt32();
                    boolean _hidl_out_success6 = setPositionMode(mode, recurrence, minIntervalMs, preferredAccuracyMeters, preferredTimeMs);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success6);
                    _hidl_reply.send();
                    return;
                case 9:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IAGnssRil _hidl_out_aGnssRilIface = getExtensionAGnssRil();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_aGnssRilIface != null ? _hidl_out_aGnssRilIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 10:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssGeofencing _hidl_out_gnssGeofencingIface = getExtensionGnssGeofencing();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssGeofencingIface != null ? _hidl_out_gnssGeofencingIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 11:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IAGnss _hidl_out_aGnssIface = getExtensionAGnss();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_aGnssIface != null ? _hidl_out_aGnssIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 12:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssNi _hidl_out_gnssNiIface = getExtensionGnssNi();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssNiIface != null ? _hidl_out_gnssNiIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 13:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssMeasurement _hidl_out_gnssMeasurementIface = getExtensionGnssMeasurement();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssMeasurementIface != null ? _hidl_out_gnssMeasurementIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 14:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssNavigationMessage _hidl_out_gnssNavigationIface = getExtensionGnssNavigationMessage();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssNavigationIface != null ? _hidl_out_gnssNavigationIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 15:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssXtra _hidl_out_xtraIface = getExtensionXtra();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_xtraIface != null ? _hidl_out_xtraIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 16:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssConfiguration _hidl_out_gnssConfigIface = getExtensionGnssConfiguration();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssConfigIface != null ? _hidl_out_gnssConfigIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 17:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssDebug _hidl_out_debugIface = getExtensionGnssDebug();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_debugIface != null ? _hidl_out_debugIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 18:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssBatching _hidl_out_batchingIface = getExtensionGnssBatching();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_batchingIface != null ? _hidl_out_batchingIface.asBinder() : null);
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
