package android.security.rkp;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IGetKeyCallback extends IInterface {
    public static final String DESCRIPTOR = "android.security.rkp.IGetKeyCallback";

    /* loaded from: classes3.dex */
    public @interface ErrorCode {
        public static final byte ERROR_PENDING_INTERNET_CONNECTIVITY = 3;
        public static final byte ERROR_PERMANENT = 5;
        public static final byte ERROR_REQUIRES_SECURITY_PATCH = 2;
        public static final byte ERROR_UNKNOWN = 1;
    }

    void onCancel() throws RemoteException;

    void onError(byte b, String str) throws RemoteException;

    void onSuccess(RemotelyProvisionedKey remotelyProvisionedKey) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IGetKeyCallback {
        @Override // android.security.rkp.IGetKeyCallback
        public void onSuccess(RemotelyProvisionedKey key) throws RemoteException {
        }

        @Override // android.security.rkp.IGetKeyCallback
        public void onCancel() throws RemoteException {
        }

        @Override // android.security.rkp.IGetKeyCallback
        public void onError(byte error, String description) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IGetKeyCallback {
        static final int TRANSACTION_onCancel = 2;
        static final int TRANSACTION_onError = 3;
        static final int TRANSACTION_onSuccess = 1;

        public Stub() {
            attachInterface(this, IGetKeyCallback.DESCRIPTOR);
        }

        public static IGetKeyCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGetKeyCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IGetKeyCallback)) {
                return (IGetKeyCallback) iin;
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
                    return "onSuccess";
                case 2:
                    return "onCancel";
                case 3:
                    return "onError";
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
                data.enforceInterface(IGetKeyCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGetKeyCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            RemotelyProvisionedKey _arg0 = (RemotelyProvisionedKey) data.readTypedObject(RemotelyProvisionedKey.CREATOR);
                            data.enforceNoDataAvail();
                            onSuccess(_arg0);
                            break;
                        case 2:
                            onCancel();
                            break;
                        case 3:
                            byte _arg02 = data.readByte();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onError(_arg02, _arg1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IGetKeyCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGetKeyCallback.DESCRIPTOR;
            }

            @Override // android.security.rkp.IGetKeyCallback
            public void onSuccess(RemotelyProvisionedKey key) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGetKeyCallback.DESCRIPTOR);
                    _data.writeTypedObject(key, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.security.rkp.IGetKeyCallback
            public void onCancel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGetKeyCallback.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.security.rkp.IGetKeyCallback
            public void onError(byte error, String description) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGetKeyCallback.DESCRIPTOR);
                    _data.writeByte(error);
                    _data.writeString(description);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
