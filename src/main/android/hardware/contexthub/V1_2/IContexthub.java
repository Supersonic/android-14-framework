package android.hardware.contexthub.V1_2;

import android.hardware.contexthub.V1_0.ContextHub;
import android.hardware.contexthub.V1_0.NanoAppBinary;
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
public interface IContexthub extends android.hardware.contexthub.V1_1.IContexthub {
    public static final String kInterfaceName = "android.hardware.contexthub@1.2::IContexthub";

    @FunctionalInterface
    /* loaded from: classes.dex */
    public interface getHubs_1_2Callback {
        void onValues(ArrayList<ContextHub> arrayList, ArrayList<String> arrayList2);
    }

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void getHubs_1_2(getHubs_1_2Callback gethubs_1_2callback) throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    void onSettingChanged_1_2(byte b, byte b2) throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    int registerCallback_1_2(int i, IContexthubCallback iContexthubCallback) throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IContexthub asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IContexthub)) {
            return (IContexthub) iface;
        }
        IContexthub proxy = new Proxy(binder);
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

    static IContexthub castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IContexthub getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IContexthub getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IContexthub getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IContexthub getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes.dex */
    public static final class Proxy implements IContexthub {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.contexthub@1.2::IContexthub]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.contexthub.V1_0.IContexthub
        public ArrayList<ContextHub> getHubs() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<ContextHub> _hidl_out_hubs = ContextHub.readVectorFromParcel(_hidl_reply);
                return _hidl_out_hubs;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_0.IContexthub
        public int registerCallback(int hubId, android.hardware.contexthub.V1_0.IContexthubCallback cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
            _hidl_request.writeInt32(hubId);
            _hidl_request.writeStrongBinder(cb == null ? null : cb.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_result = _hidl_reply.readInt32();
                return _hidl_out_result;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_0.IContexthub
        public int sendMessageToHub(int hubId, android.hardware.contexthub.V1_0.ContextHubMsg msg) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
            _hidl_request.writeInt32(hubId);
            msg.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_result = _hidl_reply.readInt32();
                return _hidl_out_result;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_0.IContexthub
        public int loadNanoApp(int hubId, NanoAppBinary appBinary, int transactionId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
            _hidl_request.writeInt32(hubId);
            appBinary.writeToParcel(_hidl_request);
            _hidl_request.writeInt32(transactionId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_result = _hidl_reply.readInt32();
                return _hidl_out_result;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_0.IContexthub
        public int unloadNanoApp(int hubId, long appId, int transactionId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
            _hidl_request.writeInt32(hubId);
            _hidl_request.writeInt64(appId);
            _hidl_request.writeInt32(transactionId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_result = _hidl_reply.readInt32();
                return _hidl_out_result;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_0.IContexthub
        public int enableNanoApp(int hubId, long appId, int transactionId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
            _hidl_request.writeInt32(hubId);
            _hidl_request.writeInt64(appId);
            _hidl_request.writeInt32(transactionId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_result = _hidl_reply.readInt32();
                return _hidl_out_result;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_0.IContexthub
        public int disableNanoApp(int hubId, long appId, int transactionId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
            _hidl_request.writeInt32(hubId);
            _hidl_request.writeInt64(appId);
            _hidl_request.writeInt32(transactionId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_result = _hidl_reply.readInt32();
                return _hidl_out_result;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_0.IContexthub
        public int queryApps(int hubId) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
            _hidl_request.writeInt32(hubId);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(8, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_result = _hidl_reply.readInt32();
                return _hidl_out_result;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_1.IContexthub
        public void onSettingChanged(byte setting, byte newValue) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.contexthub.V1_1.IContexthub.kInterfaceName);
            _hidl_request.writeInt8(setting);
            _hidl_request.writeInt8(newValue);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(9, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub
        public void getHubs_1_2(getHubs_1_2Callback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IContexthub.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(10, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<ContextHub> _hidl_out_hubs = ContextHub.readVectorFromParcel(_hidl_reply);
                ArrayList<String> _hidl_out_supportedPermissions = _hidl_reply.readStringVector();
                _hidl_cb.onValues(_hidl_out_hubs, _hidl_out_supportedPermissions);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub
        public int registerCallback_1_2(int hubId, IContexthubCallback cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IContexthub.kInterfaceName);
            _hidl_request.writeInt32(hubId);
            _hidl_request.writeStrongBinder(cb == null ? null : cb.asBinder());
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(11, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_result = _hidl_reply.readInt32();
                return _hidl_out_result;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub
        public void onSettingChanged_1_2(byte setting, byte newValue) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IContexthub.kInterfaceName);
            _hidl_request.writeInt8(setting);
            _hidl_request.writeInt8(newValue);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(12, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends HwBinder implements IContexthub {
        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IContexthub.kInterfaceName, android.hardware.contexthub.V1_1.IContexthub.kInterfaceName, android.hardware.contexthub.V1_0.IContexthub.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IContexthub.kInterfaceName;
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{53, 0, -45, -60, -30, -44, -98, -18, -46, MidiConstants.STATUS_SONG_SELECT, 35, -109, 48, -95, 102, -66, -78, -37, 45, 80, 113, -72, 77, -100, 115, -117, 4, -116, 45, 84, -93, -39}, new byte[]{-125, 81, -52, 1, -18, -44, MidiConstants.STATUS_PROGRAM_CHANGE, -76, 72, 45, -107, 114, -75, -57, -35, -3, 23, -121, 77, -114, -37, 81, -42, 118, 29, 52, -127, 22, -4, -111, -35, 24}, new byte[]{MidiConstants.STATUS_PITCH_BEND, 66, 82, 45, -86, 75, 95, Byte.MAX_VALUE, -44, MidiConstants.STATUS_POLYPHONIC_AFTERTOUCH, -95, -101, -51, -83, -71, 60, 121, -95, MidiConstants.STATUS_CONTROL_CHANGE, 76, 9, -17, 44, -104, 19, -93, -88, -108, 16, 50, MidiConstants.STATUS_SONG_SELECT, -11}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.contexthub.V1_2.IContexthub, android.hardware.contexthub.V1_1.IContexthub, android.hardware.contexthub.V1_0.IContexthub, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IContexthub.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
                    ArrayList<ContextHub> _hidl_out_hubs = getHubs();
                    _hidl_reply.writeStatus(0);
                    ContextHub.writeVectorToParcel(_hidl_reply, _hidl_out_hubs);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
                    int hubId = _hidl_request.readInt32();
                    android.hardware.contexthub.V1_0.IContexthubCallback cb = android.hardware.contexthub.V1_0.IContexthubCallback.asInterface(_hidl_request.readStrongBinder());
                    int _hidl_out_result = registerCallback(hubId, cb);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_result);
                    _hidl_reply.send();
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
                    int hubId2 = _hidl_request.readInt32();
                    android.hardware.contexthub.V1_0.ContextHubMsg msg = new android.hardware.contexthub.V1_0.ContextHubMsg();
                    msg.readFromParcel(_hidl_request);
                    int _hidl_out_result2 = sendMessageToHub(hubId2, msg);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_result2);
                    _hidl_reply.send();
                    return;
                case 4:
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
                    int hubId3 = _hidl_request.readInt32();
                    NanoAppBinary appBinary = new NanoAppBinary();
                    appBinary.readFromParcel(_hidl_request);
                    int transactionId = _hidl_request.readInt32();
                    int _hidl_out_result3 = loadNanoApp(hubId3, appBinary, transactionId);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_result3);
                    _hidl_reply.send();
                    return;
                case 5:
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
                    int hubId4 = _hidl_request.readInt32();
                    long appId = _hidl_request.readInt64();
                    int transactionId2 = _hidl_request.readInt32();
                    int _hidl_out_result4 = unloadNanoApp(hubId4, appId, transactionId2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_result4);
                    _hidl_reply.send();
                    return;
                case 6:
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
                    int hubId5 = _hidl_request.readInt32();
                    long appId2 = _hidl_request.readInt64();
                    int transactionId3 = _hidl_request.readInt32();
                    int _hidl_out_result5 = enableNanoApp(hubId5, appId2, transactionId3);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_result5);
                    _hidl_reply.send();
                    return;
                case 7:
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
                    int hubId6 = _hidl_request.readInt32();
                    long appId3 = _hidl_request.readInt64();
                    int transactionId4 = _hidl_request.readInt32();
                    int _hidl_out_result6 = disableNanoApp(hubId6, appId3, transactionId4);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_result6);
                    _hidl_reply.send();
                    return;
                case 8:
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_0.IContexthub.kInterfaceName);
                    int hubId7 = _hidl_request.readInt32();
                    int _hidl_out_result7 = queryApps(hubId7);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_result7);
                    _hidl_reply.send();
                    return;
                case 9:
                    _hidl_request.enforceInterface(android.hardware.contexthub.V1_1.IContexthub.kInterfaceName);
                    byte setting = _hidl_request.readInt8();
                    byte newValue = _hidl_request.readInt8();
                    onSettingChanged(setting, newValue);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 10:
                    _hidl_request.enforceInterface(IContexthub.kInterfaceName);
                    getHubs_1_2(new getHubs_1_2Callback() { // from class: android.hardware.contexthub.V1_2.IContexthub.Stub.1
                        @Override // android.hardware.contexthub.V1_2.IContexthub.getHubs_1_2Callback
                        public void onValues(ArrayList<ContextHub> hubs, ArrayList<String> supportedPermissions) {
                            _hidl_reply.writeStatus(0);
                            ContextHub.writeVectorToParcel(_hidl_reply, hubs);
                            _hidl_reply.writeStringVector(supportedPermissions);
                            _hidl_reply.send();
                        }
                    });
                    return;
                case 11:
                    _hidl_request.enforceInterface(IContexthub.kInterfaceName);
                    int hubId8 = _hidl_request.readInt32();
                    IContexthubCallback cb2 = IContexthubCallback.asInterface(_hidl_request.readStrongBinder());
                    int _hidl_out_result8 = registerCallback_1_2(hubId8, cb2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_result8);
                    _hidl_reply.send();
                    return;
                case 12:
                    _hidl_request.enforceInterface(IContexthub.kInterfaceName);
                    byte setting2 = _hidl_request.readInt8();
                    byte newValue2 = _hidl_request.readInt8();
                    onSettingChanged_1_2(setting2, newValue2);
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
