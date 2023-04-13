package android.hardware.gnss.V2_1;

import android.hardware.gnss.V1_0.GnssLocation;
import android.hardware.gnss.V1_0.IGnssCallback;
import android.hardware.gnss.V2_0.IGnssCallback;
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
import com.android.net.module.util.NetworkStackConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface IGnssCallback extends android.hardware.gnss.V2_0.IGnssCallback {
    public static final String kInterfaceName = "android.hardware.gnss@2.1::IGnssCallback";

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void gnssSetCapabilitiesCb_2_1(int i) throws RemoteException;

    void gnssSvStatusCb_2_1(ArrayList<GnssSvInfo> arrayList) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IGnssCallback asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IGnssCallback)) {
            return (IGnssCallback) iface;
        }
        IGnssCallback proxy = new Proxy(binder);
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

    static IGnssCallback castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IGnssCallback getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IGnssCallback getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IGnssCallback getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IGnssCallback getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class Capabilities {
        public static final int ANTENNA_INFO = 2048;
        public static final int GEOFENCING = 32;
        public static final int LOW_POWER_MODE = 256;
        public static final int MEASUREMENTS = 64;
        public static final int MEASUREMENT_CORRECTIONS = 1024;
        public static final int MSA = 4;
        public static final int MSB = 2;
        public static final int NAV_MESSAGES = 128;
        public static final int ON_DEMAND_TIME = 16;
        public static final int SATELLITE_BLACKLIST = 512;
        public static final int SCHEDULING = 1;
        public static final int SINGLE_SHOT = 8;

        public static final String toString(int o) {
            if (o == 1) {
                return "SCHEDULING";
            }
            if (o == 2) {
                return "MSB";
            }
            if (o == 4) {
                return "MSA";
            }
            if (o == 8) {
                return "SINGLE_SHOT";
            }
            if (o == 16) {
                return "ON_DEMAND_TIME";
            }
            if (o == 32) {
                return "GEOFENCING";
            }
            if (o == 64) {
                return "MEASUREMENTS";
            }
            if (o == 128) {
                return "NAV_MESSAGES";
            }
            if (o == 256) {
                return "LOW_POWER_MODE";
            }
            if (o == 512) {
                return "SATELLITE_BLACKLIST";
            }
            if (o == 1024) {
                return "MEASUREMENT_CORRECTIONS";
            }
            if (o == 2048) {
                return "ANTENNA_INFO";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            if ((o & 1) == 1) {
                list.add("SCHEDULING");
                flipped = 0 | 1;
            }
            if ((o & 2) == 2) {
                list.add("MSB");
                flipped |= 2;
            }
            if ((o & 4) == 4) {
                list.add("MSA");
                flipped |= 4;
            }
            if ((o & 8) == 8) {
                list.add("SINGLE_SHOT");
                flipped |= 8;
            }
            if ((o & 16) == 16) {
                list.add("ON_DEMAND_TIME");
                flipped |= 16;
            }
            if ((o & 32) == 32) {
                list.add("GEOFENCING");
                flipped |= 32;
            }
            if ((o & 64) == 64) {
                list.add("MEASUREMENTS");
                flipped |= 64;
            }
            if ((o & 128) == 128) {
                list.add("NAV_MESSAGES");
                flipped |= 128;
            }
            if ((o & 256) == 256) {
                list.add("LOW_POWER_MODE");
                flipped |= 256;
            }
            if ((o & 512) == 512) {
                list.add("SATELLITE_BLACKLIST");
                flipped |= 512;
            }
            if ((o & 1024) == 1024) {
                list.add("MEASUREMENT_CORRECTIONS");
                flipped |= 1024;
            }
            if ((o & 2048) == 2048) {
                list.add("ANTENNA_INFO");
                flipped |= 2048;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GnssSvInfo {
        public IGnssCallback.GnssSvInfo v2_0 = new IGnssCallback.GnssSvInfo();
        public double basebandCN0DbHz = 0.0d;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssSvInfo.class) {
                return false;
            }
            GnssSvInfo other = (GnssSvInfo) otherObject;
            if (HidlSupport.deepEquals(this.v2_0, other.v2_0) && this.basebandCN0DbHz == other.basebandCN0DbHz) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.v2_0)), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.basebandCN0DbHz))));
        }

        public final String toString() {
            return "{.v2_0 = " + this.v2_0 + ", .basebandCN0DbHz = " + this.basebandCN0DbHz + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(40L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssSvInfo> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssSvInfo> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 40, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssSvInfo _hidl_vec_element = new GnssSvInfo();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 40);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.v2_0.readEmbeddedFromParcel(parcel, _hidl_blob, 0 + _hidl_offset);
            this.basebandCN0DbHz = _hidl_blob.getDouble(32 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(40);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssSvInfo> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 40);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 40);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            this.v2_0.writeEmbeddedToBlob(_hidl_blob, 0 + _hidl_offset);
            _hidl_blob.putDouble(32 + _hidl_offset, this.basebandCN0DbHz);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IGnssCallback {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss@2.1::IGnssCallback]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssLocationCb(GnssLocation location) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            location.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssStatusCb(byte status) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            _hidl_request.writeInt8(status);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssSvStatusCb(IGnssCallback.GnssSvStatus svInfo) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            svInfo.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssNmeaCb(long timestamp, String nmea) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            _hidl_request.writeInt64(timestamp);
            _hidl_request.writeString(nmea);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssSetCapabilitesCb(int capabilities) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            _hidl_request.writeInt32(capabilities);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssAcquireWakelockCb() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssReleaseWakelockCb() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssRequestTimeCb() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_0.IGnssCallback
        public void gnssSetSystemInfoCb(IGnssCallback.GnssSystemInfo info) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
            info.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_1.IGnssCallback
        public void gnssNameCb(String name) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnssCallback.kInterfaceName);
            _hidl_request.writeString(name);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_1.IGnssCallback
        public void gnssRequestLocationCb(boolean independentFromGnss) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnssCallback.kInterfaceName);
            _hidl_request.writeBool(independentFromGnss);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssCallback
        public void gnssSetCapabilitiesCb_2_0(int capabilities) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName);
            _hidl_request.writeInt32(capabilities);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssCallback
        public void gnssLocationCb_2_0(android.hardware.gnss.V2_0.GnssLocation location) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName);
            location.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(13, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssCallback
        public void gnssRequestLocationCb_2_0(boolean independentFromGnss, boolean isUserEmergency) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName);
            _hidl_request.writeBool(independentFromGnss);
            _hidl_request.writeBool(isUserEmergency);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(14, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssCallback
        public void gnssSvStatusCb_2_0(ArrayList<IGnssCallback.GnssSvInfo> svInfoList) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName);
            IGnssCallback.GnssSvInfo.writeVectorToParcel(_hidl_request, svInfoList);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(15, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback
        public void gnssSetCapabilitiesCb_2_1(int capabilities) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssCallback.kInterfaceName);
            _hidl_request.writeInt32(capabilities);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(16, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback
        public void gnssSvStatusCb_2_1(ArrayList<GnssSvInfo> svInfoList) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssCallback.kInterfaceName);
            GnssSvInfo.writeVectorToParcel(_hidl_request, svInfoList);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(17, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IGnssCallback {
        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnssCallback.kInterfaceName, android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName, android.hardware.gnss.V1_1.IGnssCallback.kInterfaceName, android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnssCallback.kInterfaceName;
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{53, 65, -40, 58, -33, -22, -63, 110, -29, -28, 93, 24, 58, 88, -33, -2, 6, 1, 44, -53, 90, -91, -68, -46, -28, -10, -18, -82, 38, -97, 105, -51}, new byte[]{100, 35, NetworkStackConstants.TCPHDR_URG, 55, 16, -102, 94, 95, 83, -85, 3, 119, -25, 85, -20, 73, 74, -23, 63, -53, 82, 121, -26, -18, -89, 29, -20, 46, 122, -58, -5, -4}, new byte[]{-118, -43, 91, -61, 91, -77, -88, 62, 101, MidiConstants.STATUS_PROGRAM_CHANGE, 24, -67, -3, -25, -82, 94, -68, 116, -97, MidiConstants.STATUS_SONG_POSITION, -65, 107, 121, 65, 45, -19, 11, -58, -56, -101, -105, -40}, new byte[]{-94, -5, -39, 116, Byte.MAX_VALUE, -69, -100, -21, -116, 16, MidiConstants.STATUS_NOTE_ON, -75, -94, 65, 56, 49, 34, 70, 80, 45, 90, -16, 101, 74, -116, 43, 96, 58, -101, -11, 33, -4}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.V2_1.IGnssCallback, android.hardware.gnss.V2_0.IGnssCallback, android.hardware.gnss.V1_1.IGnssCallback, android.hardware.gnss.V1_0.IGnssCallback, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IGnssCallback.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    GnssLocation location = new GnssLocation();
                    location.readFromParcel(_hidl_request);
                    gnssLocationCb(location);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    byte status = _hidl_request.readInt8();
                    gnssStatusCb(status);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    IGnssCallback.GnssSvStatus svInfo = new IGnssCallback.GnssSvStatus();
                    svInfo.readFromParcel(_hidl_request);
                    gnssSvStatusCb(svInfo);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 4:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    long timestamp = _hidl_request.readInt64();
                    String nmea = _hidl_request.readString();
                    gnssNmeaCb(timestamp, nmea);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 5:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    int capabilities = _hidl_request.readInt32();
                    gnssSetCapabilitesCb(capabilities);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 6:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    gnssAcquireWakelockCb();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 7:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    gnssReleaseWakelockCb();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 8:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    gnssRequestTimeCb();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 9:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssCallback.kInterfaceName);
                    IGnssCallback.GnssSystemInfo info = new IGnssCallback.GnssSystemInfo();
                    info.readFromParcel(_hidl_request);
                    gnssSetSystemInfoCb(info);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 10:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnssCallback.kInterfaceName);
                    String name = _hidl_request.readString();
                    gnssNameCb(name);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 11:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnssCallback.kInterfaceName);
                    boolean independentFromGnss = _hidl_request.readBool();
                    gnssRequestLocationCb(independentFromGnss);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 12:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName);
                    int capabilities2 = _hidl_request.readInt32();
                    gnssSetCapabilitiesCb_2_0(capabilities2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 13:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName);
                    android.hardware.gnss.V2_0.GnssLocation location2 = new android.hardware.gnss.V2_0.GnssLocation();
                    location2.readFromParcel(_hidl_request);
                    gnssLocationCb_2_0(location2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 14:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName);
                    boolean independentFromGnss2 = _hidl_request.readBool();
                    boolean isUserEmergency = _hidl_request.readBool();
                    gnssRequestLocationCb_2_0(independentFromGnss2, isUserEmergency);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 15:
                    _hidl_request.enforceInterface(android.hardware.gnss.V2_0.IGnssCallback.kInterfaceName);
                    ArrayList<IGnssCallback.GnssSvInfo> svInfoList = IGnssCallback.GnssSvInfo.readVectorFromParcel(_hidl_request);
                    gnssSvStatusCb_2_0(svInfoList);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 16:
                    _hidl_request.enforceInterface(IGnssCallback.kInterfaceName);
                    int capabilities3 = _hidl_request.readInt32();
                    gnssSetCapabilitiesCb_2_1(capabilities3);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 17:
                    _hidl_request.enforceInterface(IGnssCallback.kInterfaceName);
                    ArrayList<GnssSvInfo> svInfoList2 = GnssSvInfo.readVectorFromParcel(_hidl_request);
                    gnssSvStatusCb_2_1(svInfoList2);
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
