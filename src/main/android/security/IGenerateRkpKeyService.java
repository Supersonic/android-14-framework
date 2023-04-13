package android.security;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IGenerateRkpKeyService extends IInterface {
    public static final String DESCRIPTOR = "android.security.IGenerateRkpKeyService";

    /* loaded from: classes3.dex */
    public @interface Status {
        public static final int DEVICE_NOT_REGISTERED = 4;
        public static final int HTTP_CLIENT_ERROR = 5;
        public static final int HTTP_SERVER_ERROR = 6;
        public static final int HTTP_UNKNOWN_ERROR = 7;
        public static final int INTERNAL_ERROR = 8;
        public static final int NETWORK_COMMUNICATION_ERROR = 2;
        public static final int NO_NETWORK_CONNECTIVITY = 1;

        /* renamed from: OK */
        public static final int f412OK = 0;
    }

    int generateKey(int i) throws RemoteException;

    void notifyKeyGenerated(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IGenerateRkpKeyService {
        @Override // android.security.IGenerateRkpKeyService
        public void notifyKeyGenerated(int securityLevel) throws RemoteException {
        }

        @Override // android.security.IGenerateRkpKeyService
        public int generateKey(int securityLevel) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IGenerateRkpKeyService {
        static final int TRANSACTION_generateKey = 2;
        static final int TRANSACTION_notifyKeyGenerated = 1;

        public Stub() {
            attachInterface(this, IGenerateRkpKeyService.DESCRIPTOR);
        }

        public static IGenerateRkpKeyService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGenerateRkpKeyService.DESCRIPTOR);
            if (iin != null && (iin instanceof IGenerateRkpKeyService)) {
                return (IGenerateRkpKeyService) iin;
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
                    return "notifyKeyGenerated";
                case 2:
                    return "generateKey";
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
                data.enforceInterface(IGenerateRkpKeyService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGenerateRkpKeyService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyKeyGenerated(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = generateKey(_arg02);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IGenerateRkpKeyService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGenerateRkpKeyService.DESCRIPTOR;
            }

            @Override // android.security.IGenerateRkpKeyService
            public void notifyKeyGenerated(int securityLevel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGenerateRkpKeyService.DESCRIPTOR);
                    _data.writeInt(securityLevel);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.security.IGenerateRkpKeyService
            public int generateKey(int securityLevel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IGenerateRkpKeyService.DESCRIPTOR);
                    _data.writeInt(securityLevel);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
