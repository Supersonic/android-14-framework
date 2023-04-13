package android.hardware.location;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IContextHubClientCallback extends IInterface {
    void onClientAuthorizationChanged(long j, int i) throws RemoteException;

    void onHubReset() throws RemoteException;

    void onMessageFromNanoApp(NanoAppMessage nanoAppMessage) throws RemoteException;

    void onNanoAppAborted(long j, int i) throws RemoteException;

    void onNanoAppDisabled(long j) throws RemoteException;

    void onNanoAppEnabled(long j) throws RemoteException;

    void onNanoAppLoaded(long j) throws RemoteException;

    void onNanoAppUnloaded(long j) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IContextHubClientCallback {
        @Override // android.hardware.location.IContextHubClientCallback
        public void onMessageFromNanoApp(NanoAppMessage message) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onHubReset() throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppAborted(long nanoAppId, int abortCode) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppLoaded(long nanoAppId) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppUnloaded(long nanoAppId) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppEnabled(long nanoAppId) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onNanoAppDisabled(long nanoAppId) throws RemoteException {
        }

        @Override // android.hardware.location.IContextHubClientCallback
        public void onClientAuthorizationChanged(long nanoAppId, int authorization) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IContextHubClientCallback {
        public static final String DESCRIPTOR = "android.hardware.location.IContextHubClientCallback";
        static final int TRANSACTION_onClientAuthorizationChanged = 8;
        static final int TRANSACTION_onHubReset = 2;
        static final int TRANSACTION_onMessageFromNanoApp = 1;
        static final int TRANSACTION_onNanoAppAborted = 3;
        static final int TRANSACTION_onNanoAppDisabled = 7;
        static final int TRANSACTION_onNanoAppEnabled = 6;
        static final int TRANSACTION_onNanoAppLoaded = 4;
        static final int TRANSACTION_onNanoAppUnloaded = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IContextHubClientCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IContextHubClientCallback)) {
                return (IContextHubClientCallback) iin;
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
                    return "onMessageFromNanoApp";
                case 2:
                    return "onHubReset";
                case 3:
                    return "onNanoAppAborted";
                case 4:
                    return "onNanoAppLoaded";
                case 5:
                    return "onNanoAppUnloaded";
                case 6:
                    return "onNanoAppEnabled";
                case 7:
                    return "onNanoAppDisabled";
                case 8:
                    return "onClientAuthorizationChanged";
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
                            NanoAppMessage _arg0 = (NanoAppMessage) data.readTypedObject(NanoAppMessage.CREATOR);
                            data.enforceNoDataAvail();
                            onMessageFromNanoApp(_arg0);
                            break;
                        case 2:
                            onHubReset();
                            break;
                        case 3:
                            long _arg02 = data.readLong();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onNanoAppAborted(_arg02, _arg1);
                            break;
                        case 4:
                            long _arg03 = data.readLong();
                            data.enforceNoDataAvail();
                            onNanoAppLoaded(_arg03);
                            break;
                        case 5:
                            long _arg04 = data.readLong();
                            data.enforceNoDataAvail();
                            onNanoAppUnloaded(_arg04);
                            break;
                        case 6:
                            long _arg05 = data.readLong();
                            data.enforceNoDataAvail();
                            onNanoAppEnabled(_arg05);
                            break;
                        case 7:
                            long _arg06 = data.readLong();
                            data.enforceNoDataAvail();
                            onNanoAppDisabled(_arg06);
                            break;
                        case 8:
                            long _arg07 = data.readLong();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onClientAuthorizationChanged(_arg07, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IContextHubClientCallback {
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

            @Override // android.hardware.location.IContextHubClientCallback
            public void onMessageFromNanoApp(NanoAppMessage message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(message, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClientCallback
            public void onHubReset() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClientCallback
            public void onNanoAppAborted(long nanoAppId, int abortCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(nanoAppId);
                    _data.writeInt(abortCode);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClientCallback
            public void onNanoAppLoaded(long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(nanoAppId);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClientCallback
            public void onNanoAppUnloaded(long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(nanoAppId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClientCallback
            public void onNanoAppEnabled(long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(nanoAppId);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClientCallback
            public void onNanoAppDisabled(long nanoAppId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(nanoAppId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.location.IContextHubClientCallback
            public void onClientAuthorizationChanged(long nanoAppId, int authorization) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(nanoAppId);
                    _data.writeInt(authorization);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 7;
        }
    }
}
