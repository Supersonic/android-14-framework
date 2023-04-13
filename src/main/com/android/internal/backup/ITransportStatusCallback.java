package com.android.internal.backup;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ITransportStatusCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.backup.ITransportStatusCallback";

    void onOperationComplete() throws RemoteException;

    void onOperationCompleteWithStatus(int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ITransportStatusCallback {
        @Override // com.android.internal.backup.ITransportStatusCallback
        public void onOperationCompleteWithStatus(int status) throws RemoteException {
        }

        @Override // com.android.internal.backup.ITransportStatusCallback
        public void onOperationComplete() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ITransportStatusCallback {
        static final int TRANSACTION_onOperationComplete = 2;
        static final int TRANSACTION_onOperationCompleteWithStatus = 1;

        public Stub() {
            attachInterface(this, ITransportStatusCallback.DESCRIPTOR);
        }

        public static ITransportStatusCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ITransportStatusCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ITransportStatusCallback)) {
                return (ITransportStatusCallback) iin;
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
                    return "onOperationCompleteWithStatus";
                case 2:
                    return "onOperationComplete";
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
                data.enforceInterface(ITransportStatusCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ITransportStatusCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onOperationCompleteWithStatus(_arg0);
                            break;
                        case 2:
                            onOperationComplete();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ITransportStatusCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ITransportStatusCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.backup.ITransportStatusCallback
            public void onOperationCompleteWithStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITransportStatusCallback.DESCRIPTOR);
                    _data.writeInt(status);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.backup.ITransportStatusCallback
            public void onOperationComplete() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ITransportStatusCallback.DESCRIPTOR);
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
