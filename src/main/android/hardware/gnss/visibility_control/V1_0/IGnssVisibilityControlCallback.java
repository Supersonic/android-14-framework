package android.hardware.gnss.visibility_control.V1_0;

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
public interface IGnssVisibilityControlCallback extends IBase {
    public static final String kInterfaceName = "android.hardware.gnss.visibility_control@1.0::IGnssVisibilityControlCallback";

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

    boolean isInEmergencySession() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    void nfwNotifyCb(NfwNotification nfwNotification) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IGnssVisibilityControlCallback asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IGnssVisibilityControlCallback)) {
            return (IGnssVisibilityControlCallback) iface;
        }
        IGnssVisibilityControlCallback proxy = new Proxy(binder);
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

    static IGnssVisibilityControlCallback castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IGnssVisibilityControlCallback getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IGnssVisibilityControlCallback getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IGnssVisibilityControlCallback getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IGnssVisibilityControlCallback getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class NfwProtocolStack {
        public static final byte CTRL_PLANE = 0;
        public static final byte IMS = 10;
        public static final byte OTHER_PROTOCOL_STACK = 100;
        public static final byte SIM = 11;
        public static final byte SUPL = 1;

        public static final String toString(byte o) {
            if (o == 0) {
                return "CTRL_PLANE";
            }
            if (o == 1) {
                return "SUPL";
            }
            if (o == 10) {
                return "IMS";
            }
            if (o == 11) {
                return "SIM";
            }
            if (o == 100) {
                return "OTHER_PROTOCOL_STACK";
            }
            return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
        }

        public static final String dumpBitfield(byte o) {
            ArrayList<String> list = new ArrayList<>();
            byte flipped = 0;
            list.add("CTRL_PLANE");
            if ((o & 1) == 1) {
                list.add("SUPL");
                flipped = (byte) (0 | 1);
            }
            if ((o & 10) == 10) {
                list.add("IMS");
                flipped = (byte) (flipped | 10);
            }
            if ((o & 11) == 11) {
                list.add("SIM");
                flipped = (byte) (flipped | 11);
            }
            if ((o & 100) == 100) {
                list.add("OTHER_PROTOCOL_STACK");
                flipped = (byte) (flipped | 100);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes2.dex */
    public static final class NfwRequestor {
        public static final byte AUTOMOBILE_CLIENT = 20;
        public static final byte CARRIER = 0;
        public static final byte GNSS_CHIPSET_VENDOR = 12;
        public static final byte MODEM_CHIPSET_VENDOR = 11;
        public static final byte OEM = 10;
        public static final byte OTHER_CHIPSET_VENDOR = 13;
        public static final byte OTHER_REQUESTOR = 100;

        public static final String toString(byte o) {
            if (o == 0) {
                return "CARRIER";
            }
            if (o == 10) {
                return "OEM";
            }
            if (o == 11) {
                return "MODEM_CHIPSET_VENDOR";
            }
            if (o == 12) {
                return "GNSS_CHIPSET_VENDOR";
            }
            if (o == 13) {
                return "OTHER_CHIPSET_VENDOR";
            }
            if (o == 20) {
                return "AUTOMOBILE_CLIENT";
            }
            if (o == 100) {
                return "OTHER_REQUESTOR";
            }
            return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
        }

        public static final String dumpBitfield(byte o) {
            ArrayList<String> list = new ArrayList<>();
            byte flipped = 0;
            list.add("CARRIER");
            if ((o & 10) == 10) {
                list.add("OEM");
                flipped = (byte) (0 | 10);
            }
            if ((o & 11) == 11) {
                list.add("MODEM_CHIPSET_VENDOR");
                flipped = (byte) (flipped | 11);
            }
            if ((o & 12) == 12) {
                list.add("GNSS_CHIPSET_VENDOR");
                flipped = (byte) (flipped | 12);
            }
            if ((o & 13) == 13) {
                list.add("OTHER_CHIPSET_VENDOR");
                flipped = (byte) (flipped | 13);
            }
            if ((o & AUTOMOBILE_CLIENT) == 20) {
                list.add("AUTOMOBILE_CLIENT");
                flipped = (byte) (flipped | AUTOMOBILE_CLIENT);
            }
            if ((o & 100) == 100) {
                list.add("OTHER_REQUESTOR");
                flipped = (byte) (flipped | 100);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes2.dex */
    public static final class NfwResponseType {
        public static final byte ACCEPTED_LOCATION_PROVIDED = 2;
        public static final byte ACCEPTED_NO_LOCATION_PROVIDED = 1;
        public static final byte REJECTED = 0;

        public static final String toString(byte o) {
            if (o == 0) {
                return "REJECTED";
            }
            if (o == 1) {
                return "ACCEPTED_NO_LOCATION_PROVIDED";
            }
            if (o == 2) {
                return "ACCEPTED_LOCATION_PROVIDED";
            }
            return "0x" + Integer.toHexString(Byte.toUnsignedInt(o));
        }

        public static final String dumpBitfield(byte o) {
            ArrayList<String> list = new ArrayList<>();
            byte flipped = 0;
            list.add("REJECTED");
            if ((o & 1) == 1) {
                list.add("ACCEPTED_NO_LOCATION_PROVIDED");
                flipped = (byte) (0 | 1);
            }
            if ((o & 2) == 2) {
                list.add("ACCEPTED_LOCATION_PROVIDED");
                flipped = (byte) (flipped | 2);
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(Byte.toUnsignedInt((byte) ((~flipped) & o))));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes2.dex */
    public static final class NfwNotification {
        public String proxyAppPackageName = new String();
        public byte protocolStack = 0;
        public String otherProtocolStackName = new String();
        public byte requestor = 0;
        public String requestorId = new String();
        public byte responseType = 0;
        public boolean inEmergencyMode = false;
        public boolean isCachedLocation = false;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != NfwNotification.class) {
                return false;
            }
            NfwNotification other = (NfwNotification) otherObject;
            if (HidlSupport.deepEquals(this.proxyAppPackageName, other.proxyAppPackageName) && this.protocolStack == other.protocolStack && HidlSupport.deepEquals(this.otherProtocolStackName, other.otherProtocolStackName) && this.requestor == other.requestor && HidlSupport.deepEquals(this.requestorId, other.requestorId) && this.responseType == other.responseType && this.inEmergencyMode == other.inEmergencyMode && this.isCachedLocation == other.isCachedLocation) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.proxyAppPackageName)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.protocolStack))), Integer.valueOf(HidlSupport.deepHashCode(this.otherProtocolStackName)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.requestor))), Integer.valueOf(HidlSupport.deepHashCode(this.requestorId)), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.responseType))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.inEmergencyMode))), Integer.valueOf(HidlSupport.deepHashCode(Boolean.valueOf(this.isCachedLocation))));
        }

        public final String toString() {
            return "{.proxyAppPackageName = " + this.proxyAppPackageName + ", .protocolStack = " + NfwProtocolStack.toString(this.protocolStack) + ", .otherProtocolStackName = " + this.otherProtocolStackName + ", .requestor = " + NfwRequestor.toString(this.requestor) + ", .requestorId = " + this.requestorId + ", .responseType = " + NfwResponseType.toString(this.responseType) + ", .inEmergencyMode = " + this.inEmergencyMode + ", .isCachedLocation = " + this.isCachedLocation + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(72L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<NfwNotification> readVectorFromParcel(HwParcel parcel) {
            ArrayList<NfwNotification> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 72, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                NfwNotification _hidl_vec_element = new NfwNotification();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 72);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            String string = _hidl_blob.getString(_hidl_offset + 0);
            this.proxyAppPackageName = string;
            parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 0 + 0, false);
            this.protocolStack = _hidl_blob.getInt8(_hidl_offset + 16);
            String string2 = _hidl_blob.getString(_hidl_offset + 24);
            this.otherProtocolStackName = string2;
            parcel.readEmbeddedBuffer(string2.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 24 + 0, false);
            this.requestor = _hidl_blob.getInt8(_hidl_offset + 40);
            String string3 = _hidl_blob.getString(_hidl_offset + 48);
            this.requestorId = string3;
            parcel.readEmbeddedBuffer(string3.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 48 + 0, false);
            this.responseType = _hidl_blob.getInt8(_hidl_offset + 64);
            this.inEmergencyMode = _hidl_blob.getBool(_hidl_offset + 65);
            this.isCachedLocation = _hidl_blob.getBool(_hidl_offset + 66);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(72);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<NfwNotification> _hidl_vec) {
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
            _hidl_blob.putString(0 + _hidl_offset, this.proxyAppPackageName);
            _hidl_blob.putInt8(16 + _hidl_offset, this.protocolStack);
            _hidl_blob.putString(24 + _hidl_offset, this.otherProtocolStackName);
            _hidl_blob.putInt8(40 + _hidl_offset, this.requestor);
            _hidl_blob.putString(48 + _hidl_offset, this.requestorId);
            _hidl_blob.putInt8(64 + _hidl_offset, this.responseType);
            _hidl_blob.putBool(65 + _hidl_offset, this.inEmergencyMode);
            _hidl_blob.putBool(66 + _hidl_offset, this.isCachedLocation);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IGnssVisibilityControlCallback {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss.visibility_control@1.0::IGnssVisibilityControlCallback]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback
        public void nfwNotifyCb(NfwNotification notification) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssVisibilityControlCallback.kInterfaceName);
            notification.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback
        public boolean isInEmergencySession() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssVisibilityControlCallback.kInterfaceName);
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IGnssVisibilityControlCallback {
        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnssVisibilityControlCallback.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnssVisibilityControlCallback.kInterfaceName;
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{51, -90, -78, 12, 67, -81, 0, -3, -5, 48, 93, -8, -111, -68, 89, 17, MidiConstants.STATUS_PROGRAM_CHANGE, 109, -102, -111, 48, -71, 18, 117, -106, 73, -109, 46, 90, 74, 110, 109}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IGnssVisibilityControlCallback.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(IGnssVisibilityControlCallback.kInterfaceName);
                    NfwNotification notification = new NfwNotification();
                    notification.readFromParcel(_hidl_request);
                    nfwNotifyCb(notification);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(IGnssVisibilityControlCallback.kInterfaceName);
                    boolean _hidl_out_success = isInEmergencySession();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_success);
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
