package android.companion.virtual;

import android.companion.virtual.IVirtualDevice;
import android.companion.virtual.IVirtualDeviceActivityListener;
import android.companion.virtual.IVirtualDeviceSoundEffectListener;
import android.hardware.display.IVirtualDisplayCallback;
import android.hardware.display.VirtualDisplayConfig;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IVirtualDeviceManager extends IInterface {
    public static final String DESCRIPTOR = "android.companion.virtual.IVirtualDeviceManager";

    IVirtualDevice createVirtualDevice(IBinder iBinder, String str, int i, VirtualDeviceParams virtualDeviceParams, IVirtualDeviceActivityListener iVirtualDeviceActivityListener, IVirtualDeviceSoundEffectListener iVirtualDeviceSoundEffectListener) throws RemoteException;

    int createVirtualDisplay(VirtualDisplayConfig virtualDisplayConfig, IVirtualDisplayCallback iVirtualDisplayCallback, IVirtualDevice iVirtualDevice, String str) throws RemoteException;

    int getAudioPlaybackSessionId(int i) throws RemoteException;

    int getAudioRecordingSessionId(int i) throws RemoteException;

    int getDeviceIdForDisplayId(int i) throws RemoteException;

    int getDevicePolicy(int i, int i2) throws RemoteException;

    List<VirtualDevice> getVirtualDevices() throws RemoteException;

    boolean isValidVirtualDeviceId(int i) throws RemoteException;

    void playSoundEffect(int i, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IVirtualDeviceManager {
        @Override // android.companion.virtual.IVirtualDeviceManager
        public IVirtualDevice createVirtualDevice(IBinder token, String packageName, int associationId, VirtualDeviceParams params, IVirtualDeviceActivityListener activityListener, IVirtualDeviceSoundEffectListener soundEffectListener) throws RemoteException {
            return null;
        }

        @Override // android.companion.virtual.IVirtualDeviceManager
        public List<VirtualDevice> getVirtualDevices() throws RemoteException {
            return null;
        }

        @Override // android.companion.virtual.IVirtualDeviceManager
        public int getDeviceIdForDisplayId(int displayId) throws RemoteException {
            return 0;
        }

        @Override // android.companion.virtual.IVirtualDeviceManager
        public boolean isValidVirtualDeviceId(int deviceId) throws RemoteException {
            return false;
        }

        @Override // android.companion.virtual.IVirtualDeviceManager
        public int getDevicePolicy(int deviceId, int policyType) throws RemoteException {
            return 0;
        }

        @Override // android.companion.virtual.IVirtualDeviceManager
        public int createVirtualDisplay(VirtualDisplayConfig virtualDisplayConfig, IVirtualDisplayCallback callback, IVirtualDevice virtualDevice, String packageName) throws RemoteException {
            return 0;
        }

        @Override // android.companion.virtual.IVirtualDeviceManager
        public int getAudioPlaybackSessionId(int deviceId) throws RemoteException {
            return 0;
        }

        @Override // android.companion.virtual.IVirtualDeviceManager
        public int getAudioRecordingSessionId(int deviceId) throws RemoteException {
            return 0;
        }

        @Override // android.companion.virtual.IVirtualDeviceManager
        public void playSoundEffect(int deviceId, int effectType) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IVirtualDeviceManager {
        static final int TRANSACTION_createVirtualDevice = 1;
        static final int TRANSACTION_createVirtualDisplay = 6;
        static final int TRANSACTION_getAudioPlaybackSessionId = 7;
        static final int TRANSACTION_getAudioRecordingSessionId = 8;
        static final int TRANSACTION_getDeviceIdForDisplayId = 3;
        static final int TRANSACTION_getDevicePolicy = 5;
        static final int TRANSACTION_getVirtualDevices = 2;
        static final int TRANSACTION_isValidVirtualDeviceId = 4;
        static final int TRANSACTION_playSoundEffect = 9;

        public Stub() {
            attachInterface(this, IVirtualDeviceManager.DESCRIPTOR);
        }

        public static IVirtualDeviceManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVirtualDeviceManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IVirtualDeviceManager)) {
                return (IVirtualDeviceManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "createVirtualDevice";
                case 2:
                    return "getVirtualDevices";
                case 3:
                    return "getDeviceIdForDisplayId";
                case 4:
                    return "isValidVirtualDeviceId";
                case 5:
                    return "getDevicePolicy";
                case 6:
                    return "createVirtualDisplay";
                case 7:
                    return "getAudioPlaybackSessionId";
                case 8:
                    return "getAudioRecordingSessionId";
                case 9:
                    return "playSoundEffect";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IVirtualDeviceManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVirtualDeviceManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            VirtualDeviceParams _arg3 = (VirtualDeviceParams) data.readTypedObject(VirtualDeviceParams.CREATOR);
                            IVirtualDeviceActivityListener _arg4 = IVirtualDeviceActivityListener.Stub.asInterface(data.readStrongBinder());
                            IVirtualDeviceSoundEffectListener _arg5 = IVirtualDeviceSoundEffectListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            IVirtualDevice _result = createVirtualDevice(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            List<VirtualDevice> _result2 = getVirtualDevices();
                            reply.writeNoException();
                            reply.writeTypedList(_result2, 1);
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result3 = getDeviceIdForDisplayId(_arg02);
                            reply.writeNoException();
                            reply.writeInt(_result3);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = isValidVirtualDeviceId(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result5 = getDevicePolicy(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            break;
                        case 6:
                            VirtualDisplayConfig _arg05 = (VirtualDisplayConfig) data.readTypedObject(VirtualDisplayConfig.CREATOR);
                            IVirtualDisplayCallback _arg13 = IVirtualDisplayCallback.Stub.asInterface(data.readStrongBinder());
                            IVirtualDevice _arg22 = IVirtualDevice.Stub.asInterface(data.readStrongBinder());
                            String _arg32 = data.readString();
                            data.enforceNoDataAvail();
                            int _result6 = createVirtualDisplay(_arg05, _arg13, _arg22, _arg32);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            break;
                        case 7:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result7 = getAudioPlaybackSessionId(_arg06);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            break;
                        case 8:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result8 = getAudioRecordingSessionId(_arg07);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            playSoundEffect(_arg08, _arg14);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements IVirtualDeviceManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVirtualDeviceManager.DESCRIPTOR;
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public IVirtualDevice createVirtualDevice(IBinder token, String packageName, int associationId, VirtualDeviceParams params, IVirtualDeviceActivityListener activityListener, IVirtualDeviceSoundEffectListener soundEffectListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(packageName);
                    _data.writeInt(associationId);
                    _data.writeTypedObject(params, 0);
                    _data.writeStrongInterface(activityListener);
                    _data.writeStrongInterface(soundEffectListener);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    IVirtualDevice _result = IVirtualDevice.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public List<VirtualDevice> getVirtualDevices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    List<VirtualDevice> _result = _reply.createTypedArrayList(VirtualDevice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public int getDeviceIdForDisplayId(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public boolean isValidVirtualDeviceId(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public int getDevicePolicy(int deviceId, int policyType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(policyType);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public int createVirtualDisplay(VirtualDisplayConfig virtualDisplayConfig, IVirtualDisplayCallback callback, IVirtualDevice virtualDevice, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    _data.writeTypedObject(virtualDisplayConfig, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeStrongInterface(virtualDevice);
                    _data.writeString(packageName);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public int getAudioPlaybackSessionId(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public int getAudioRecordingSessionId(int deviceId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.companion.virtual.IVirtualDeviceManager
            public void playSoundEffect(int deviceId, int effectType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IVirtualDeviceManager.DESCRIPTOR);
                    _data.writeInt(deviceId);
                    _data.writeInt(effectType);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
