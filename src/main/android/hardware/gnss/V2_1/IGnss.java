package android.hardware.gnss.V2_1;

import android.hardware.gnss.V1_0.GnssLocation;
import android.hardware.gnss.V1_0.IAGnss;
import android.hardware.gnss.V1_0.IAGnssRil;
import android.hardware.gnss.V1_0.IGnssBatching;
import android.hardware.gnss.V1_0.IGnssDebug;
import android.hardware.gnss.V1_0.IGnssGeofencing;
import android.hardware.gnss.V1_0.IGnssNavigationMessage;
import android.hardware.gnss.V1_0.IGnssNi;
import android.hardware.gnss.V1_0.IGnssXtra;
import android.hardware.gnss.measurement_corrections.V1_1.IMeasurementCorrections;
import android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControl;
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
/* loaded from: classes2.dex */
public interface IGnss extends android.hardware.gnss.V2_0.IGnss {
    public static final String kInterfaceName = "android.hardware.gnss@2.1::IGnss";

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    IGnssAntennaInfo getExtensionGnssAntennaInfo() throws RemoteException;

    IGnssConfiguration getExtensionGnssConfiguration_2_1() throws RemoteException;

    IGnssMeasurement getExtensionGnssMeasurement_2_1() throws RemoteException;

    IMeasurementCorrections getExtensionMeasurementCorrections_1_1() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    boolean setCallback_2_1(IGnssCallback iGnssCallback) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

    /* loaded from: classes2.dex */
    public static final class Proxy implements IGnss {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss@2.1::IGnss]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public boolean setCallback(android.hardware.gnss.V1_0.IGnssCallback callback) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
        public android.hardware.gnss.V1_0.IGnssMeasurement getExtensionGnssMeasurement() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V1_0.IGnssMeasurement _hidl_out_gnssMeasurementIface = android.hardware.gnss.V1_0.IGnssMeasurement.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssMeasurementIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssNavigationMessage getExtensionGnssNavigationMessage() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
        public android.hardware.gnss.V1_0.IGnssConfiguration getExtensionGnssConfiguration() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V1_0.IGnssConfiguration _hidl_out_gnssConfigIface = android.hardware.gnss.V1_0.IGnssConfiguration.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssConfigIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnss
        public IGnssDebug getExtensionGnssDebug() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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

        @Override // android.hardware.gnss.V1_1.IGnss
        public boolean setCallback_1_1(android.hardware.gnss.V1_1.IGnssCallback callback) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
            _hidl_request.writeStrongBinder(callback == null ? null : callback.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(19, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_1.IGnss
        public boolean setPositionMode_1_1(byte mode, int recurrence, int minIntervalMs, int preferredAccuracyMeters, int preferredTimeMs, boolean lowPowerMode) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
            _hidl_request.writeInt8(mode);
            _hidl_request.writeInt32(recurrence);
            _hidl_request.writeInt32(minIntervalMs);
            _hidl_request.writeInt32(preferredAccuracyMeters);
            _hidl_request.writeInt32(preferredTimeMs);
            _hidl_request.writeBool(lowPowerMode);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(20, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_1.IGnss
        public android.hardware.gnss.V1_1.IGnssConfiguration getExtensionGnssConfiguration_1_1() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(21, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V1_1.IGnssConfiguration _hidl_out_gnssConfigurationIface = android.hardware.gnss.V1_1.IGnssConfiguration.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssConfigurationIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_1.IGnss
        public android.hardware.gnss.V1_1.IGnssMeasurement getExtensionGnssMeasurement_1_1() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(22, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V1_1.IGnssMeasurement _hidl_out_gnssMeasurementIface = android.hardware.gnss.V1_1.IGnssMeasurement.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssMeasurementIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_1.IGnss
        public boolean injectBestLocation(GnssLocation location) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
            location.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(23, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public boolean setCallback_2_0(android.hardware.gnss.V2_0.IGnssCallback callback) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            _hidl_request.writeStrongBinder(callback == null ? null : callback.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(24, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public android.hardware.gnss.V2_0.IGnssConfiguration getExtensionGnssConfiguration_2_0() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(25, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V2_0.IGnssConfiguration _hidl_out_gnssConfigurationIface = android.hardware.gnss.V2_0.IGnssConfiguration.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssConfigurationIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public android.hardware.gnss.V2_0.IGnssDebug getExtensionGnssDebug_2_0() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(26, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V2_0.IGnssDebug _hidl_out_gnssDebugIface = android.hardware.gnss.V2_0.IGnssDebug.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssDebugIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public android.hardware.gnss.V2_0.IAGnss getExtensionAGnss_2_0() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(27, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V2_0.IAGnss _hidl_out_aGnssIface = android.hardware.gnss.V2_0.IAGnss.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_aGnssIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public android.hardware.gnss.V2_0.IAGnssRil getExtensionAGnssRil_2_0() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(28, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V2_0.IAGnssRil _hidl_out_aGnssRilIface = android.hardware.gnss.V2_0.IAGnssRil.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_aGnssRilIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public android.hardware.gnss.V2_0.IGnssMeasurement getExtensionGnssMeasurement_2_0() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(29, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V2_0.IGnssMeasurement _hidl_out_gnssMeasurementIface = android.hardware.gnss.V2_0.IGnssMeasurement.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssMeasurementIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public android.hardware.gnss.measurement_corrections.V1_0.IMeasurementCorrections getExtensionMeasurementCorrections() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(30, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.measurement_corrections.V1_0.IMeasurementCorrections _hidl_out_measurementCorrectionsIface = android.hardware.gnss.measurement_corrections.V1_0.IMeasurementCorrections.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_measurementCorrectionsIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public IGnssVisibilityControl getExtensionVisibilityControl() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(31, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssVisibilityControl _hidl_out_visibilityControlIface = IGnssVisibilityControl.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_visibilityControlIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public android.hardware.gnss.V2_0.IGnssBatching getExtensionGnssBatching_2_0() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(32, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                android.hardware.gnss.V2_0.IGnssBatching _hidl_out_batchingIface = android.hardware.gnss.V2_0.IGnssBatching.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_batchingIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnss
        public boolean injectBestLocation_2_0(android.hardware.gnss.V2_0.GnssLocation location) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
            location.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(33, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnss
        public boolean setCallback_2_1(IGnssCallback callback) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            _hidl_request.writeStrongBinder(callback == null ? null : callback.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(34, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_success = _hidl_reply.readBool();
                return _hidl_out_success;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnss
        public IGnssMeasurement getExtensionGnssMeasurement_2_1() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(35, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssMeasurement _hidl_out_gnssMeasurementIface = IGnssMeasurement.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssMeasurementIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnss
        public IGnssConfiguration getExtensionGnssConfiguration_2_1() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(36, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssConfiguration _hidl_out_gnssConfigurationIface = IGnssConfiguration.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssConfigurationIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnss
        public IMeasurementCorrections getExtensionMeasurementCorrections_1_1() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(37, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IMeasurementCorrections _hidl_out_measurementCorrectionsIface = IMeasurementCorrections.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_measurementCorrectionsIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnss
        public IGnssAntennaInfo getExtensionGnssAntennaInfo() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnss.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(38, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                IGnssAntennaInfo _hidl_out_gnssAntennaInfoIface = IGnssAntennaInfo.asInterface(_hidl_reply.readStrongBinder());
                return _hidl_out_gnssAntennaInfoIface;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IGnss {
        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnss.kInterfaceName, android.hardware.gnss.V2_0.IGnss.kInterfaceName, android.hardware.gnss.V1_1.IGnss.kInterfaceName, android.hardware.gnss.V1_0.IGnss.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnss.kInterfaceName;
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-61, 25, -26, -117, 3, -126, -103, 88, 64, 68, 2, -62, -39, -58, -126, 1, -106, 120, 8, 125, 96, 73, 88, 7, MidiConstants.STATUS_PROGRAM_CHANGE, -89, 68, 78, 10, 106, -7, -127}, new byte[]{-11, 96, 95, 72, -62, -5, -97, 35, 22, 21, -35, -109, 43, -9, 48, -82, -107, 64, -12, -7, -117, 91, 122, -30, -78, 105, -105, 95, 69, 47, 109, 115}, new byte[]{-75, MidiConstants.STATUS_MIDI_TIME_CODE, -12, -63, -67, 109, -25, 26, -114, 113, -41, MidiConstants.STATUS_CHANNEL_MASK, 87, -51, -85, MidiConstants.STATUS_NOTE_ON, 74, MidiConstants.STATUS_PROGRAM_CHANGE, 36, -95, 47, 61, -18, 62, 33, 115, 119, 10, 69, -125, -68, -62}, new byte[]{-19, -26, -105, 16, -61, -87, 92, 44, -66, -127, -114, 108, -117, -73, 44, 120, 22, -126, 63, -84, -27, -4, 33, -63, 119, 49, -78, 111, 65, -39, 77, 101}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.V2_1.IGnss, android.hardware.gnss.V2_0.IGnss, android.hardware.gnss.V1_1.IGnss, android.hardware.gnss.V1_0.IGnss, android.internal.hidl.base.V1_0.IBase
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
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V1_0.IGnssCallback callback = android.hardware.gnss.V1_0.IGnssCallback.asInterface(_hidl_request.readStrongBinder());
                    boolean _hidl_out_success = setCallback(callback);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    boolean _hidl_out_success2 = start();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success2);
                    _hidl_reply.send();
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    boolean _hidl_out_success3 = stop();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success3);
                    _hidl_reply.send();
                    return;
                case 4:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    cleanup();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 5:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    long timeMs = _hidl_request.readInt64();
                    long timeReferenceMs = _hidl_request.readInt64();
                    int uncertaintyMs = _hidl_request.readInt32();
                    boolean _hidl_out_success4 = injectTime(timeMs, timeReferenceMs, uncertaintyMs);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success4);
                    _hidl_reply.send();
                    return;
                case 6:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    double latitudeDegrees = _hidl_request.readDouble();
                    double longitudeDegrees = _hidl_request.readDouble();
                    float accuracyMeters = _hidl_request.readFloat();
                    boolean _hidl_out_success5 = injectLocation(latitudeDegrees, longitudeDegrees, accuracyMeters);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success5);
                    _hidl_reply.send();
                    return;
                case 7:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    short aidingDataFlags = _hidl_request.readInt16();
                    deleteAidingData(aidingDataFlags);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 8:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
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
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    IAGnssRil _hidl_out_aGnssRilIface = getExtensionAGnssRil();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_aGnssRilIface != null ? _hidl_out_aGnssRilIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 10:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    IGnssGeofencing _hidl_out_gnssGeofencingIface = getExtensionGnssGeofencing();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssGeofencingIface != null ? _hidl_out_gnssGeofencingIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 11:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    IAGnss _hidl_out_aGnssIface = getExtensionAGnss();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_aGnssIface != null ? _hidl_out_aGnssIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 12:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    IGnssNi _hidl_out_gnssNiIface = getExtensionGnssNi();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssNiIface != null ? _hidl_out_gnssNiIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 13:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V1_0.IGnssMeasurement _hidl_out_gnssMeasurementIface = getExtensionGnssMeasurement();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssMeasurementIface != null ? _hidl_out_gnssMeasurementIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 14:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    IGnssNavigationMessage _hidl_out_gnssNavigationIface = getExtensionGnssNavigationMessage();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssNavigationIface != null ? _hidl_out_gnssNavigationIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 15:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    IGnssXtra _hidl_out_xtraIface = getExtensionXtra();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_xtraIface != null ? _hidl_out_xtraIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 16:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V1_0.IGnssConfiguration _hidl_out_gnssConfigIface = getExtensionGnssConfiguration();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssConfigIface != null ? _hidl_out_gnssConfigIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 17:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    IGnssDebug _hidl_out_debugIface = getExtensionGnssDebug();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_debugIface != null ? _hidl_out_debugIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 18:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnss.kInterfaceName);
                    IGnssBatching _hidl_out_batchingIface = getExtensionGnssBatching();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_batchingIface != null ? _hidl_out_batchingIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 19:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
                    android.hardware.gnss.V1_1.IGnssCallback callback2 = android.hardware.gnss.V1_1.IGnssCallback.asInterface(_hidl_request.readStrongBinder());
                    boolean _hidl_out_success7 = setCallback_1_1(callback2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success7);
                    _hidl_reply.send();
                    return;
                case 20:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
                    byte mode2 = _hidl_request.readInt8();
                    int recurrence2 = _hidl_request.readInt32();
                    int minIntervalMs2 = _hidl_request.readInt32();
                    int preferredAccuracyMeters2 = _hidl_request.readInt32();
                    int preferredTimeMs2 = _hidl_request.readInt32();
                    boolean lowPowerMode = _hidl_request.readBool();
                    boolean _hidl_out_success8 = setPositionMode_1_1(mode2, recurrence2, minIntervalMs2, preferredAccuracyMeters2, preferredTimeMs2, lowPowerMode);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success8);
                    _hidl_reply.send();
                    return;
                case 21:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
                    android.hardware.gnss.V1_1.IGnssConfiguration _hidl_out_gnssConfigurationIface = getExtensionGnssConfiguration_1_1();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssConfigurationIface != null ? _hidl_out_gnssConfigurationIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 22:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
                    android.hardware.gnss.V1_1.IGnssMeasurement _hidl_out_gnssMeasurementIface2 = getExtensionGnssMeasurement_1_1();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssMeasurementIface2 != null ? _hidl_out_gnssMeasurementIface2.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 23:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnss.kInterfaceName);
                    GnssLocation location = new GnssLocation();
                    location.readFromParcel(_hidl_request);
                    boolean _hidl_out_success9 = injectBestLocation(location);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success9);
                    _hidl_reply.send();
                    return;
                case 24:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V2_0.IGnssCallback callback3 = android.hardware.gnss.V2_0.IGnssCallback.asInterface(_hidl_request.readStrongBinder());
                    boolean _hidl_out_success10 = setCallback_2_0(callback3);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success10);
                    _hidl_reply.send();
                    return;
                case 25:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V2_0.IGnssConfiguration _hidl_out_gnssConfigurationIface2 = getExtensionGnssConfiguration_2_0();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssConfigurationIface2 != null ? _hidl_out_gnssConfigurationIface2.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 26:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V2_0.IGnssDebug _hidl_out_gnssDebugIface = getExtensionGnssDebug_2_0();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssDebugIface != null ? _hidl_out_gnssDebugIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 27:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V2_0.IAGnss _hidl_out_aGnssIface2 = getExtensionAGnss_2_0();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_aGnssIface2 != null ? _hidl_out_aGnssIface2.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 28:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V2_0.IAGnssRil _hidl_out_aGnssRilIface2 = getExtensionAGnssRil_2_0();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_aGnssRilIface2 != null ? _hidl_out_aGnssRilIface2.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 29:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V2_0.IGnssMeasurement _hidl_out_gnssMeasurementIface3 = getExtensionGnssMeasurement_2_0();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssMeasurementIface3 != null ? _hidl_out_gnssMeasurementIface3.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 30:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.measurement_corrections.V1_0.IMeasurementCorrections _hidl_out_measurementCorrectionsIface = getExtensionMeasurementCorrections();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_measurementCorrectionsIface != null ? _hidl_out_measurementCorrectionsIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 31:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    IGnssVisibilityControl _hidl_out_visibilityControlIface = getExtensionVisibilityControl();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_visibilityControlIface != null ? _hidl_out_visibilityControlIface.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 32:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V2_0.IGnssBatching _hidl_out_batchingIface2 = getExtensionGnssBatching_2_0();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_batchingIface2 != null ? _hidl_out_batchingIface2.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 33:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnss.kInterfaceName);
                    android.hardware.gnss.V2_0.GnssLocation location2 = new android.hardware.gnss.V2_0.GnssLocation();
                    location2.readFromParcel(_hidl_request);
                    boolean _hidl_out_success11 = injectBestLocation_2_0(location2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success11);
                    _hidl_reply.send();
                    return;
                case 34:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssCallback callback4 = IGnssCallback.asInterface(_hidl_request.readStrongBinder());
                    boolean _hidl_out_success12 = setCallback_2_1(callback4);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success12);
                    _hidl_reply.send();
                    return;
                case 35:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssMeasurement _hidl_out_gnssMeasurementIface4 = getExtensionGnssMeasurement_2_1();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssMeasurementIface4 != null ? _hidl_out_gnssMeasurementIface4.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 36:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssConfiguration _hidl_out_gnssConfigurationIface3 = getExtensionGnssConfiguration_2_1();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssConfigurationIface3 != null ? _hidl_out_gnssConfigurationIface3.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 37:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IMeasurementCorrections _hidl_out_measurementCorrectionsIface2 = getExtensionMeasurementCorrections_1_1();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_measurementCorrectionsIface2 != null ? _hidl_out_measurementCorrectionsIface2.asBinder() : null);
                    _hidl_reply.send();
                    return;
                case 38:
                    _hidl_request.enforceInterface(IGnss.kInterfaceName);
                    IGnssAntennaInfo _hidl_out_gnssAntennaInfoIface = getExtensionGnssAntennaInfo();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStrongBinder(_hidl_out_gnssAntennaInfoIface != null ? _hidl_out_gnssAntennaInfoIface.asBinder() : null);
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
