package android.hardware.vibrator.V1_2;

import android.hardware.vibrator.V1_0.IVibrator;
import android.hardware.vibrator.V1_1.IVibrator;
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
public interface IVibrator extends android.hardware.vibrator.V1_1.IVibrator {
    public static final String kInterfaceName = "android.hardware.vibrator@1.2::IVibrator";

    @FunctionalInterface
    /* loaded from: classes2.dex */
    public interface perform_1_2Callback {
        void onValues(int i, int i2);
    }

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    void perform_1_2(int i, byte b, perform_1_2Callback perform_1_2callback) throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IVibrator asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IVibrator)) {
            return (IVibrator) iface;
        }
        IVibrator proxy = new Proxy(binder);
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

    static IVibrator castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IVibrator getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IVibrator getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IVibrator getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IVibrator getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IVibrator {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.vibrator@1.2::IVibrator]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.vibrator.V1_0.IVibrator
        /* renamed from: on */
        public int mo153on(int timeoutMs) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
            _hidl_request.writeInt32(timeoutMs);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_vibratorOnRet = _hidl_reply.readInt32();
                return _hidl_out_vibratorOnRet;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.vibrator.V1_0.IVibrator
        public int off() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_vibratorOffRet = _hidl_reply.readInt32();
                return _hidl_out_vibratorOffRet;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.vibrator.V1_0.IVibrator
        public boolean supportsAmplitudeControl() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                boolean _hidl_out_supports = _hidl_reply.readBool();
                return _hidl_out_supports;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.vibrator.V1_0.IVibrator
        public int setAmplitude(byte amplitude) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
            _hidl_request.writeInt8(amplitude);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(4, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_status = _hidl_reply.readInt32();
                return _hidl_out_status;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.vibrator.V1_0.IVibrator
        public void perform(int effect, byte strength, IVibrator.performCallback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
            _hidl_request.writeInt32(effect);
            _hidl_request.writeInt8(strength);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(5, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_status = _hidl_reply.readInt32();
                int _hidl_out_lengthMs = _hidl_reply.readInt32();
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_lengthMs);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.vibrator.V1_1.IVibrator
        public void perform_1_1(int effect, byte strength, IVibrator.perform_1_1Callback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.vibrator.V1_1.IVibrator.kInterfaceName);
            _hidl_request.writeInt32(effect);
            _hidl_request.writeInt8(strength);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(6, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_status = _hidl_reply.readInt32();
                int _hidl_out_lengthMs = _hidl_reply.readInt32();
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_lengthMs);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator
        public void perform_1_2(int effect, byte strength, perform_1_2Callback _hidl_cb) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IVibrator.kInterfaceName);
            _hidl_request.writeInt32(effect);
            _hidl_request.writeInt8(strength);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(7, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                int _hidl_out_status = _hidl_reply.readInt32();
                int _hidl_out_lengthMs = _hidl_reply.readInt32();
                _hidl_cb.onValues(_hidl_out_status, _hidl_out_lengthMs);
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IVibrator {
        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IVibrator.kInterfaceName, android.hardware.vibrator.V1_1.IVibrator.kInterfaceName, android.hardware.vibrator.V1_0.IVibrator.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IVibrator.kInterfaceName;
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{GsmAlphabet.GSM_EXTENDED_ESCAPE, -4, -97, -39, 83, 110, MidiConstants.STATUS_CHANNEL_PRESSURE, -97, 4, -68, -81, 34, 42, 51, 43, -55, 25, MidiConstants.STATUS_MIDI_TIME_CODE, 86, 93, 77, 8, -67, -36, -51, -21, -31, -65, -54, -113, 1, -75}, new byte[]{-7, 90, 30, -123, 97, 47, 45, 13, 97, 110, -84, -46, -21, 99, -59, 45, 16, -33, -88, -119, MidiConstants.STATUS_MIDI_TIME_CODE, 101, -33, 87, 105, 124, 48, -31, -12, 123, 71, -123}, new byte[]{6, -22, 100, -52, 53, 101, 119, Byte.MAX_VALUE, 59, 37, -98, 64, MidiConstants.STATUS_CHANNEL_MASK, -6, 113, 0, MidiConstants.STATUS_CHANNEL_PRESSURE, Byte.MAX_VALUE, 56, 39, -83, -109, 87, MidiConstants.STATUS_CONTROL_CHANGE, -59, -45, -58, 81, 56, 78, 85, 83}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.vibrator.V1_2.IVibrator, android.hardware.vibrator.V1_1.IVibrator, android.hardware.vibrator.V1_0.IVibrator, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IVibrator.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
                    int timeoutMs = _hidl_request.readInt32();
                    int _hidl_out_vibratorOnRet = mo153on(timeoutMs);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_vibratorOnRet);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
                    int _hidl_out_vibratorOffRet = off();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_vibratorOffRet);
                    _hidl_reply.send();
                    return;
                case 3:
                    _hidl_request.enforceInterface(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
                    boolean _hidl_out_supports = supportsAmplitudeControl();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeBool(_hidl_out_supports);
                    _hidl_reply.send();
                    return;
                case 4:
                    _hidl_request.enforceInterface(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
                    byte amplitude = _hidl_request.readInt8();
                    int _hidl_out_status = setAmplitude(amplitude);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeInt32(_hidl_out_status);
                    _hidl_reply.send();
                    return;
                case 5:
                    _hidl_request.enforceInterface(android.hardware.vibrator.V1_0.IVibrator.kInterfaceName);
                    int effect = _hidl_request.readInt32();
                    byte strength = _hidl_request.readInt8();
                    perform(effect, strength, new IVibrator.performCallback() { // from class: android.hardware.vibrator.V1_2.IVibrator.Stub.1
                        @Override // android.hardware.vibrator.V1_0.IVibrator.performCallback
                        public void onValues(int status, int lengthMs) {
                            _hidl_reply.writeStatus(0);
                            _hidl_reply.writeInt32(status);
                            _hidl_reply.writeInt32(lengthMs);
                            _hidl_reply.send();
                        }
                    });
                    return;
                case 6:
                    _hidl_request.enforceInterface(android.hardware.vibrator.V1_1.IVibrator.kInterfaceName);
                    int effect2 = _hidl_request.readInt32();
                    byte strength2 = _hidl_request.readInt8();
                    perform_1_1(effect2, strength2, new IVibrator.perform_1_1Callback() { // from class: android.hardware.vibrator.V1_2.IVibrator.Stub.2
                        @Override // android.hardware.vibrator.V1_1.IVibrator.perform_1_1Callback
                        public void onValues(int status, int lengthMs) {
                            _hidl_reply.writeStatus(0);
                            _hidl_reply.writeInt32(status);
                            _hidl_reply.writeInt32(lengthMs);
                            _hidl_reply.send();
                        }
                    });
                    return;
                case 7:
                    _hidl_request.enforceInterface(IVibrator.kInterfaceName);
                    int effect3 = _hidl_request.readInt32();
                    byte strength3 = _hidl_request.readInt8();
                    perform_1_2(effect3, strength3, new perform_1_2Callback() { // from class: android.hardware.vibrator.V1_2.IVibrator.Stub.3
                        @Override // android.hardware.vibrator.V1_2.IVibrator.perform_1_2Callback
                        public void onValues(int status, int lengthMs) {
                            _hidl_reply.writeStatus(0);
                            _hidl_reply.writeInt32(status);
                            _hidl_reply.writeInt32(lengthMs);
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
