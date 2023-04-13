package android.media.projection;

import android.media.projection.IMediaProjectionCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IMediaProjection extends IInterface {
    int applyVirtualDisplayFlags(int i) throws RemoteException;

    boolean canProjectAudio() throws RemoteException;

    boolean canProjectSecureVideo() throws RemoteException;

    boolean canProjectVideo() throws RemoteException;

    IBinder getLaunchCookie() throws RemoteException;

    void registerCallback(IMediaProjectionCallback iMediaProjectionCallback) throws RemoteException;

    void setLaunchCookie(IBinder iBinder) throws RemoteException;

    void start(IMediaProjectionCallback iMediaProjectionCallback) throws RemoteException;

    void stop() throws RemoteException;

    void unregisterCallback(IMediaProjectionCallback iMediaProjectionCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IMediaProjection {
        @Override // android.media.projection.IMediaProjection
        public void start(IMediaProjectionCallback callback) throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjection
        public void stop() throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjection
        public boolean canProjectAudio() throws RemoteException {
            return false;
        }

        @Override // android.media.projection.IMediaProjection
        public boolean canProjectVideo() throws RemoteException {
            return false;
        }

        @Override // android.media.projection.IMediaProjection
        public boolean canProjectSecureVideo() throws RemoteException {
            return false;
        }

        @Override // android.media.projection.IMediaProjection
        public int applyVirtualDisplayFlags(int flags) throws RemoteException {
            return 0;
        }

        @Override // android.media.projection.IMediaProjection
        public void registerCallback(IMediaProjectionCallback callback) throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjection
        public void unregisterCallback(IMediaProjectionCallback callback) throws RemoteException {
        }

        @Override // android.media.projection.IMediaProjection
        public IBinder getLaunchCookie() throws RemoteException {
            return null;
        }

        @Override // android.media.projection.IMediaProjection
        public void setLaunchCookie(IBinder launchCookie) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IMediaProjection {
        public static final String DESCRIPTOR = "android.media.projection.IMediaProjection";
        static final int TRANSACTION_applyVirtualDisplayFlags = 6;
        static final int TRANSACTION_canProjectAudio = 3;
        static final int TRANSACTION_canProjectSecureVideo = 5;
        static final int TRANSACTION_canProjectVideo = 4;
        static final int TRANSACTION_getLaunchCookie = 9;
        static final int TRANSACTION_registerCallback = 7;
        static final int TRANSACTION_setLaunchCookie = 10;
        static final int TRANSACTION_start = 1;
        static final int TRANSACTION_stop = 2;
        static final int TRANSACTION_unregisterCallback = 8;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMediaProjection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMediaProjection)) {
                return (IMediaProjection) iin;
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
                    return "start";
                case 2:
                    return "stop";
                case 3:
                    return "canProjectAudio";
                case 4:
                    return "canProjectVideo";
                case 5:
                    return "canProjectSecureVideo";
                case 6:
                    return "applyVirtualDisplayFlags";
                case 7:
                    return "registerCallback";
                case 8:
                    return "unregisterCallback";
                case 9:
                    return "getLaunchCookie";
                case 10:
                    return "setLaunchCookie";
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
                            IMediaProjectionCallback _arg0 = IMediaProjectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            start(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            stop();
                            reply.writeNoException();
                            break;
                        case 3:
                            boolean _result = canProjectAudio();
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 4:
                            boolean _result2 = canProjectVideo();
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            boolean _result3 = canProjectSecureVideo();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 6:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result4 = applyVirtualDisplayFlags(_arg02);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            break;
                        case 7:
                            IMediaProjectionCallback _arg03 = IMediaProjectionCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerCallback(_arg03);
                            reply.writeNoException();
                            break;
                        case 8:
                            IBinder _result5 = data.readStrongBinder();
                            IMediaProjectionCallback _arg04 = IMediaProjectionCallback.Stub.asInterface(_result5);
                            data.enforceNoDataAvail();
                            unregisterCallback(_arg04);
                            reply.writeNoException();
                            break;
                        case 9:
                            IBinder _result6 = getLaunchCookie();
                            reply.writeNoException();
                            reply.writeStrongBinder(_result6);
                            break;
                        case 10:
                            IBinder _arg05 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            setLaunchCookie(_arg05);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IMediaProjection {
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

            @Override // android.media.projection.IMediaProjection
            public void start(IMediaProjectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public void stop() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public boolean canProjectAudio() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public boolean canProjectVideo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public boolean canProjectSecureVideo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public int applyVirtualDisplayFlags(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public void registerCallback(IMediaProjectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public void unregisterCallback(IMediaProjectionCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public IBinder getLaunchCookie() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.projection.IMediaProjection
            public void setLaunchCookie(IBinder launchCookie) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(launchCookie);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
