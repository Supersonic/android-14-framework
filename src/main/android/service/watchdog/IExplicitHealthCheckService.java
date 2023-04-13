package android.service.watchdog;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IExplicitHealthCheckService extends IInterface {
    public static final String DESCRIPTOR = "android.service.watchdog.IExplicitHealthCheckService";

    void cancel(String str) throws RemoteException;

    void getRequestedPackages(RemoteCallback remoteCallback) throws RemoteException;

    void getSupportedPackages(RemoteCallback remoteCallback) throws RemoteException;

    void request(String str) throws RemoteException;

    void setCallback(RemoteCallback remoteCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IExplicitHealthCheckService {
        @Override // android.service.watchdog.IExplicitHealthCheckService
        public void setCallback(RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.watchdog.IExplicitHealthCheckService
        public void request(String packageName) throws RemoteException {
        }

        @Override // android.service.watchdog.IExplicitHealthCheckService
        public void cancel(String packageName) throws RemoteException {
        }

        @Override // android.service.watchdog.IExplicitHealthCheckService
        public void getSupportedPackages(RemoteCallback callback) throws RemoteException {
        }

        @Override // android.service.watchdog.IExplicitHealthCheckService
        public void getRequestedPackages(RemoteCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IExplicitHealthCheckService {
        static final int TRANSACTION_cancel = 3;
        static final int TRANSACTION_getRequestedPackages = 5;
        static final int TRANSACTION_getSupportedPackages = 4;
        static final int TRANSACTION_request = 2;
        static final int TRANSACTION_setCallback = 1;

        public Stub() {
            attachInterface(this, IExplicitHealthCheckService.DESCRIPTOR);
        }

        public static IExplicitHealthCheckService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IExplicitHealthCheckService.DESCRIPTOR);
            if (iin != null && (iin instanceof IExplicitHealthCheckService)) {
                return (IExplicitHealthCheckService) iin;
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
                    return "setCallback";
                case 2:
                    return "request";
                case 3:
                    return "cancel";
                case 4:
                    return "getSupportedPackages";
                case 5:
                    return "getRequestedPackages";
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
                data.enforceInterface(IExplicitHealthCheckService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IExplicitHealthCheckService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            RemoteCallback _arg0 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            setCallback(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            request(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            cancel(_arg03);
                            break;
                        case 4:
                            RemoteCallback _arg04 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            getSupportedPackages(_arg04);
                            break;
                        case 5:
                            RemoteCallback _arg05 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            getRequestedPackages(_arg05);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IExplicitHealthCheckService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IExplicitHealthCheckService.DESCRIPTOR;
            }

            @Override // android.service.watchdog.IExplicitHealthCheckService
            public void setCallback(RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExplicitHealthCheckService.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.watchdog.IExplicitHealthCheckService
            public void request(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExplicitHealthCheckService.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.watchdog.IExplicitHealthCheckService
            public void cancel(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExplicitHealthCheckService.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.watchdog.IExplicitHealthCheckService
            public void getSupportedPackages(RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExplicitHealthCheckService.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.watchdog.IExplicitHealthCheckService
            public void getRequestedPackages(RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IExplicitHealthCheckService.DESCRIPTOR);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
