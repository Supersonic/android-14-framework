package android.media.p007tv;

import android.content.AttributionSource;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.media.p007tv.ITvInputServiceCallback;
import android.media.p007tv.ITvInputSessionCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.InputChannel;
import java.util.List;
/* renamed from: android.media.tv.ITvInputService */
/* loaded from: classes2.dex */
public interface ITvInputService extends IInterface {
    void createRecordingSession(ITvInputSessionCallback iTvInputSessionCallback, String str, String str2) throws RemoteException;

    void createSession(InputChannel inputChannel, ITvInputSessionCallback iTvInputSessionCallback, String str, String str2, AttributionSource attributionSource) throws RemoteException;

    List<String> getAvailableExtensionInterfaceNames() throws RemoteException;

    IBinder getExtensionInterface(String str) throws RemoteException;

    String getExtensionInterfacePermission(String str) throws RemoteException;

    void notifyHardwareAdded(TvInputHardwareInfo tvInputHardwareInfo) throws RemoteException;

    void notifyHardwareRemoved(TvInputHardwareInfo tvInputHardwareInfo) throws RemoteException;

    void notifyHdmiDeviceAdded(HdmiDeviceInfo hdmiDeviceInfo) throws RemoteException;

    void notifyHdmiDeviceRemoved(HdmiDeviceInfo hdmiDeviceInfo) throws RemoteException;

    void notifyHdmiDeviceUpdated(HdmiDeviceInfo hdmiDeviceInfo) throws RemoteException;

    void registerCallback(ITvInputServiceCallback iTvInputServiceCallback) throws RemoteException;

    void unregisterCallback(ITvInputServiceCallback iTvInputServiceCallback) throws RemoteException;

    /* renamed from: android.media.tv.ITvInputService$Default */
    /* loaded from: classes2.dex */
    public static class Default implements ITvInputService {
        @Override // android.media.p007tv.ITvInputService
        public void registerCallback(ITvInputServiceCallback callback) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputService
        public void unregisterCallback(ITvInputServiceCallback callback) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputService
        public void createSession(InputChannel channel, ITvInputSessionCallback callback, String inputId, String sessionId, AttributionSource tvAppAttributionSource) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputService
        public void createRecordingSession(ITvInputSessionCallback callback, String inputId, String sessionId) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputService
        public List<String> getAvailableExtensionInterfaceNames() throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputService
        public IBinder getExtensionInterface(String name) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputService
        public String getExtensionInterfacePermission(String name) throws RemoteException {
            return null;
        }

        @Override // android.media.p007tv.ITvInputService
        public void notifyHardwareAdded(TvInputHardwareInfo hardwareInfo) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputService
        public void notifyHardwareRemoved(TvInputHardwareInfo hardwareInfo) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputService
        public void notifyHdmiDeviceAdded(HdmiDeviceInfo deviceInfo) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputService
        public void notifyHdmiDeviceRemoved(HdmiDeviceInfo deviceInfo) throws RemoteException {
        }

        @Override // android.media.p007tv.ITvInputService
        public void notifyHdmiDeviceUpdated(HdmiDeviceInfo deviceInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.media.tv.ITvInputService$Stub */
    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ITvInputService {
        public static final String DESCRIPTOR = "android.media.tv.ITvInputService";
        static final int TRANSACTION_createRecordingSession = 4;
        static final int TRANSACTION_createSession = 3;
        static final int TRANSACTION_getAvailableExtensionInterfaceNames = 5;
        static final int TRANSACTION_getExtensionInterface = 6;
        static final int TRANSACTION_getExtensionInterfacePermission = 7;
        static final int TRANSACTION_notifyHardwareAdded = 8;
        static final int TRANSACTION_notifyHardwareRemoved = 9;
        static final int TRANSACTION_notifyHdmiDeviceAdded = 10;
        static final int TRANSACTION_notifyHdmiDeviceRemoved = 11;
        static final int TRANSACTION_notifyHdmiDeviceUpdated = 12;
        static final int TRANSACTION_registerCallback = 1;
        static final int TRANSACTION_unregisterCallback = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITvInputService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITvInputService)) {
                return (ITvInputService) iin;
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
                    return "registerCallback";
                case 2:
                    return "unregisterCallback";
                case 3:
                    return "createSession";
                case 4:
                    return "createRecordingSession";
                case 5:
                    return "getAvailableExtensionInterfaceNames";
                case 6:
                    return "getExtensionInterface";
                case 7:
                    return "getExtensionInterfacePermission";
                case 8:
                    return "notifyHardwareAdded";
                case 9:
                    return "notifyHardwareRemoved";
                case 10:
                    return "notifyHdmiDeviceAdded";
                case 11:
                    return "notifyHdmiDeviceRemoved";
                case 12:
                    return "notifyHdmiDeviceUpdated";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ITvInputServiceCallback _arg0 = ITvInputServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerCallback(_arg0);
                            break;
                        case 2:
                            ITvInputServiceCallback _arg02 = ITvInputServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterCallback(_arg02);
                            break;
                        case 3:
                            InputChannel _arg03 = (InputChannel) data.readTypedObject(InputChannel.CREATOR);
                            ITvInputSessionCallback _arg1 = ITvInputSessionCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            AttributionSource _arg4 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            data.enforceNoDataAvail();
                            createSession(_arg03, _arg1, _arg2, _arg3, _arg4);
                            break;
                        case 4:
                            ITvInputSessionCallback _arg04 = ITvInputSessionCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            createRecordingSession(_arg04, _arg12, _arg22);
                            break;
                        case 5:
                            List<String> _result = getAvailableExtensionInterfaceNames();
                            reply.writeNoException();
                            reply.writeStringList(_result);
                            break;
                        case 6:
                            String _arg05 = data.readString();
                            data.enforceNoDataAvail();
                            IBinder _result2 = getExtensionInterface(_arg05);
                            reply.writeNoException();
                            reply.writeStrongBinder(_result2);
                            break;
                        case 7:
                            String _arg06 = data.readString();
                            data.enforceNoDataAvail();
                            String _result3 = getExtensionInterfacePermission(_arg06);
                            reply.writeNoException();
                            reply.writeString(_result3);
                            break;
                        case 8:
                            TvInputHardwareInfo _arg07 = (TvInputHardwareInfo) data.readTypedObject(TvInputHardwareInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyHardwareAdded(_arg07);
                            break;
                        case 9:
                            TvInputHardwareInfo _arg08 = (TvInputHardwareInfo) data.readTypedObject(TvInputHardwareInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyHardwareRemoved(_arg08);
                            break;
                        case 10:
                            HdmiDeviceInfo _arg09 = (HdmiDeviceInfo) data.readTypedObject(HdmiDeviceInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyHdmiDeviceAdded(_arg09);
                            break;
                        case 11:
                            HdmiDeviceInfo _arg010 = (HdmiDeviceInfo) data.readTypedObject(HdmiDeviceInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyHdmiDeviceRemoved(_arg010);
                            break;
                        case 12:
                            HdmiDeviceInfo _arg011 = (HdmiDeviceInfo) data.readTypedObject(HdmiDeviceInfo.CREATOR);
                            data.enforceNoDataAvail();
                            notifyHdmiDeviceUpdated(_arg011);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.media.tv.ITvInputService$Stub$Proxy */
        /* loaded from: classes2.dex */
        private static class Proxy implements ITvInputService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.media.p007tv.ITvInputService
            public void registerCallback(ITvInputServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public void unregisterCallback(ITvInputServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public void createSession(InputChannel channel, ITvInputSessionCallback callback, String inputId, String sessionId, AttributionSource tvAppAttributionSource) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(channel, 0);
                    _data.writeStrongInterface(callback);
                    _data.writeString(inputId);
                    _data.writeString(sessionId);
                    _data.writeTypedObject(tvAppAttributionSource, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public void createRecordingSession(ITvInputSessionCallback callback, String inputId, String sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeString(inputId);
                    _data.writeString(sessionId);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public List<String> getAvailableExtensionInterfaceNames() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public IBinder getExtensionInterface(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public String getExtensionInterfacePermission(String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public void notifyHardwareAdded(TvInputHardwareInfo hardwareInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(hardwareInfo, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public void notifyHardwareRemoved(TvInputHardwareInfo hardwareInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(hardwareInfo, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public void notifyHdmiDeviceAdded(HdmiDeviceInfo deviceInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(deviceInfo, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public void notifyHdmiDeviceRemoved(HdmiDeviceInfo deviceInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(deviceInfo, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.p007tv.ITvInputService
            public void notifyHdmiDeviceUpdated(HdmiDeviceInfo deviceInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(deviceInfo, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
