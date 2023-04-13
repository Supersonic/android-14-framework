package android.hardware.thermal.V2_0;

import android.hardware.thermal.V1_0.CpuUsage;
import android.hardware.thermal.V1_0.IThermal;
import android.hardware.thermal.V1_0.ThermalStatus;
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
public interface IThermal extends android.hardware.thermal.V1_0.IThermal {
    public static final String kInterfaceName = "android.hardware.thermal@2.0::IThermal";

    @FunctionalInterface
    /* loaded from: classes2.dex */
    public interface getCurrentCoolingDevicesCallback {
        void onValues(ThermalStatus thermalStatus, ArrayList<CoolingDevice> arrayList);
    }

    @FunctionalInterface
    /* loaded from: classes2.dex */
    public interface getCurrentTemperaturesCallback {
        void onValues(ThermalStatus thermalStatus, ArrayList<Temperature> arrayList);
    }

    @FunctionalInterface
    /* loaded from: classes2.dex */
    public interface getTemperatureThresholdsCallback {
        void onValues(ThermalStatus thermalStatus, ArrayList<TemperatureThreshold> arrayList);
    }

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    void getCurrentCoolingDevices(boolean z, int i, getCurrentCoolingDevicesCallback getcurrentcoolingdevicescallback) throws RemoteException;

    void getCurrentTemperatures(boolean z, int i, getCurrentTemperaturesCallback getcurrenttemperaturescallback) throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void getTemperatureThresholds(boolean z, int i, getTemperatureThresholdsCallback gettemperaturethresholdscallback) throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    ThermalStatus registerThermalChangedCallback(IThermalChangedCallback iThermalChangedCallback, boolean z, int i) throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    ThermalStatus unregisterThermalChangedCallback(IThermalChangedCallback iThermalChangedCallback) throws RemoteException;

    static IThermal asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IThermal)) {
            return (IThermal) iface;
        }
        IThermal proxy = new Proxy(binder);
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

    static IThermal castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IThermal getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IThermal getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IThermal getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IThermal getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IThermal {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.thermal@2.0::IThermal]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.thermal.V1_0.IThermal
        public void getTemperatures(IThermal.getTemperaturesCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.thermal.V1_0.IThermal.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ThermalStatus _hidl_out_status = new ThermalStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                ArrayList<android.hardware.thermal.V1_0.Temperature> _hidl_out_temperatures = android.hardware.thermal.V1_0.Temperature.readVectorFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_temperatures);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.thermal.V1_0.IThermal
        public void getCpuUsages(IThermal.getCpuUsagesCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.thermal.V1_0.IThermal.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ThermalStatus _hidl_out_status = new ThermalStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                ArrayList<CpuUsage> _hidl_out_cpuUsages = CpuUsage.readVectorFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_cpuUsages);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.thermal.V1_0.IThermal
        public void getCoolingDevices(IThermal.getCoolingDevicesCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.thermal.V1_0.IThermal.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ThermalStatus _hidl_out_status = new ThermalStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                ArrayList<android.hardware.thermal.V1_0.CoolingDevice> _hidl_out_devices = android.hardware.thermal.V1_0.CoolingDevice.readVectorFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_devices);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.thermal.V2_0.IThermal
        public void getCurrentTemperatures(boolean filterType, int type, getCurrentTemperaturesCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IThermal.kInterfaceName);
            _hidl_request.writeBool(filterType);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ThermalStatus _hidl_out_status = new ThermalStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                ArrayList<Temperature> _hidl_out_temperatures = Temperature.readVectorFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_temperatures);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.thermal.V2_0.IThermal
        public void getTemperatureThresholds(boolean filterType, int type, getTemperatureThresholdsCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IThermal.kInterfaceName);
            _hidl_request.writeBool(filterType);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ThermalStatus _hidl_out_status = new ThermalStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                ArrayList<TemperatureThreshold> _hidl_out_temperatureThresholds = TemperatureThreshold.readVectorFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_temperatureThresholds);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.thermal.V2_0.IThermal
        public ThermalStatus registerThermalChangedCallback(IThermalChangedCallback callback, boolean filterType, int type) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IThermal.kInterfaceName);
            _hidl_request.writeStrongBinder(callback == null ? null : callback.asBinder());
            _hidl_request.writeBool(filterType);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ThermalStatus _hidl_out_status = new ThermalStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.thermal.V2_0.IThermal
        public ThermalStatus unregisterThermalChangedCallback(IThermalChangedCallback callback) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IThermal.kInterfaceName);
            _hidl_request.writeStrongBinder(callback == null ? null : callback.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ThermalStatus _hidl_out_status = new ThermalStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.thermal.V2_0.IThermal
        public void getCurrentCoolingDevices(boolean filterType, int type, getCurrentCoolingDevicesCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IThermal.kInterfaceName);
            _hidl_request.writeBool(filterType);
            _hidl_request.writeInt32(type);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ThermalStatus _hidl_out_status = new ThermalStatus();
                _hidl_out_status.readFromParcel(_hidl_reply);
                ArrayList<CoolingDevice> _hidl_out_devices = CoolingDevice.readVectorFromParcel(_hidl_reply);
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_devices);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IThermal {
        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IThermal.kInterfaceName, android.hardware.thermal.V1_0.IThermal.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IThermal.kInterfaceName;
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-67, -120, -76, -122, 57, -54, -29, 9, -126, 2, 16, 36, -30, 35, 113, 7, 108, 118, -6, -88, 70, 110, 56, -54, 89, -123, 41, 69, 43, 97, -114, -82}, new byte[]{-105, MidiConstants.STATUS_MIDI_TIME_CODE, -20, 68, 96, 67, -68, 90, 102, 69, -73, 69, 41, -90, 39, 100, -106, -67, -77, 94, 10, -18, 65, -19, -91, 92, -71, 45, 81, -21, 120, 2}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.thermal.V2_0.IThermal, android.hardware.thermal.V1_0.IThermal, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IThermal.kInterfaceName.equals(descriptor)) {
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
        public void onTransact(int _hidl_code, HwParcel _hidl_request, final HwParcel _hidl_reply, int _hidl_flags) throws RemoteException {
            switch (_hidl_code) {
                case 1:
                    _hidl_request.enforceInterface(android.hardware.thermal.V1_0.IThermal.kInterfaceName);
                    getTemperatures(new IThermal.getTemperaturesCallback() { // from class: android.hardware.thermal.V2_0.IThermal.Stub.1
                        @Override // android.hardware.thermal.V1_0.IThermal.getTemperaturesCallback
                        public void onValues(ThermalStatus status, ArrayList<android.hardware.thermal.V1_0.Temperature> temperatures) {
                            _hidl_reply.writeStatus(0);
                            status.writeToParcel(_hidl_reply);
                            android.hardware.thermal.V1_0.Temperature.writeVectorToParcel(_hidl_reply, temperatures);
                            _hidl_reply.send();
                        }
                    });
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.thermal.V1_0.IThermal.kInterfaceName);
                    getCpuUsages(new IThermal.getCpuUsagesCallback() { // from class: android.hardware.thermal.V2_0.IThermal.Stub.2
                        @Override // android.hardware.thermal.V1_0.IThermal.getCpuUsagesCallback
                        public void onValues(ThermalStatus status, ArrayList<CpuUsage> cpuUsages) {
                            _hidl_reply.writeStatus(0);
                            status.writeToParcel(_hidl_reply);
                            CpuUsage.writeVectorToParcel(_hidl_reply, cpuUsages);
                            _hidl_reply.send();
                        }
                    });
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.thermal.V1_0.IThermal.kInterfaceName);
                    getCoolingDevices(new IThermal.getCoolingDevicesCallback() { // from class: android.hardware.thermal.V2_0.IThermal.Stub.3
                        @Override // android.hardware.thermal.V1_0.IThermal.getCoolingDevicesCallback
                        public void onValues(ThermalStatus status, ArrayList<android.hardware.thermal.V1_0.CoolingDevice> devices) {
                            _hidl_reply.writeStatus(0);
                            status.writeToParcel(_hidl_reply);
                            android.hardware.thermal.V1_0.CoolingDevice.writeVectorToParcel(_hidl_reply, devices);
                            _hidl_reply.send();
                        }
                    });
                    return;
                case 4:
                    _hidl_request.enforceInterface(IThermal.kInterfaceName);
                    boolean filterType = _hidl_request.readBool();
                    int type = _hidl_request.readInt32();
                    getCurrentTemperatures(filterType, type, new getCurrentTemperaturesCallback() { // from class: android.hardware.thermal.V2_0.IThermal.Stub.4
                        @Override // android.hardware.thermal.V2_0.IThermal.getCurrentTemperaturesCallback
                        public void onValues(ThermalStatus status, ArrayList<Temperature> temperatures) {
                            _hidl_reply.writeStatus(0);
                            status.writeToParcel(_hidl_reply);
                            Temperature.writeVectorToParcel(_hidl_reply, temperatures);
                            _hidl_reply.send();
                        }
                    });
                    return;
                case 5:
                    _hidl_request.enforceInterface(IThermal.kInterfaceName);
                    boolean filterType2 = _hidl_request.readBool();
                    int type2 = _hidl_request.readInt32();
                    getTemperatureThresholds(filterType2, type2, new getTemperatureThresholdsCallback() { // from class: android.hardware.thermal.V2_0.IThermal.Stub.5
                        @Override // android.hardware.thermal.V2_0.IThermal.getTemperatureThresholdsCallback
                        public void onValues(ThermalStatus status, ArrayList<TemperatureThreshold> temperatureThresholds) {
                            _hidl_reply.writeStatus(0);
                            status.writeToParcel(_hidl_reply);
                            TemperatureThreshold.writeVectorToParcel(_hidl_reply, temperatureThresholds);
                            _hidl_reply.send();
                        }
                    });
                    return;
                case 6:
                    _hidl_request.enforceInterface(IThermal.kInterfaceName);
                    IThermalChangedCallback callback = IThermalChangedCallback.asInterface(_hidl_request.readStrongBinder());
                    boolean filterType3 = _hidl_request.readBool();
                    int type3 = _hidl_request.readInt32();
                    ThermalStatus _hidl_out_status = registerThermalChangedCallback(callback, filterType3, type3);
                    _hidl_reply.writeStatus(0);
                    _hidl_out_status.writeToParcel(_hidl_reply);
                    _hidl_reply.send();
                    return;
                case 7:
                    _hidl_request.enforceInterface(IThermal.kInterfaceName);
                    IThermalChangedCallback callback2 = IThermalChangedCallback.asInterface(_hidl_request.readStrongBinder());
                    ThermalStatus _hidl_out_status2 = unregisterThermalChangedCallback(callback2);
                    _hidl_reply.writeStatus(0);
                    _hidl_out_status2.writeToParcel(_hidl_reply);
                    _hidl_reply.send();
                    return;
                case 8:
                    _hidl_request.enforceInterface(IThermal.kInterfaceName);
                    boolean filterType4 = _hidl_request.readBool();
                    int type4 = _hidl_request.readInt32();
                    getCurrentCoolingDevices(filterType4, type4, new getCurrentCoolingDevicesCallback() { // from class: android.hardware.thermal.V2_0.IThermal.Stub.6
                        @Override // android.hardware.thermal.V2_0.IThermal.getCurrentCoolingDevicesCallback
                        public void onValues(ThermalStatus status, ArrayList<CoolingDevice> devices) {
                            _hidl_reply.writeStatus(0);
                            status.writeToParcel(_hidl_reply);
                            CoolingDevice.writeVectorToParcel(_hidl_reply, devices);
                            _hidl_reply.send();
                        }
                    });
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
