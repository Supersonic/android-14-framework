package android.net.vcn;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IVcnStatusCallback extends IInterface {
    public static final String DESCRIPTOR = "android.net.vcn.IVcnStatusCallback";

    void onGatewayConnectionError(String str, int i, String str2, String str3) throws RemoteException;

    void onVcnStatusChanged(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IVcnStatusCallback {
        @Override // android.net.vcn.IVcnStatusCallback
        public void onVcnStatusChanged(int statusCode) throws RemoteException {
        }

        @Override // android.net.vcn.IVcnStatusCallback
        public void onGatewayConnectionError(String gatewayConnectionName, int errorCode, String exceptionClass, String exceptionMessage) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IVcnStatusCallback {
        static final int TRANSACTION_onGatewayConnectionError = 2;
        static final int TRANSACTION_onVcnStatusChanged = 1;

        public Stub() {
            attachInterface(this, IVcnStatusCallback.DESCRIPTOR);
        }

        public static IVcnStatusCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IVcnStatusCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IVcnStatusCallback)) {
                return (IVcnStatusCallback) iin;
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
                    return "onVcnStatusChanged";
                case 2:
                    return "onGatewayConnectionError";
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
                data.enforceInterface(IVcnStatusCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IVcnStatusCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onVcnStatusChanged(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg1 = data.readInt();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            onGatewayConnectionError(_arg02, _arg1, _arg2, _arg3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IVcnStatusCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IVcnStatusCallback.DESCRIPTOR;
            }

            @Override // android.net.vcn.IVcnStatusCallback
            public void onVcnStatusChanged(int statusCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVcnStatusCallback.DESCRIPTOR);
                    _data.writeInt(statusCode);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.vcn.IVcnStatusCallback
            public void onGatewayConnectionError(String gatewayConnectionName, int errorCode, String exceptionClass, String exceptionMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IVcnStatusCallback.DESCRIPTOR);
                    _data.writeString(gatewayConnectionName);
                    _data.writeInt(errorCode);
                    _data.writeString(exceptionClass);
                    _data.writeString(exceptionMessage);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
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
