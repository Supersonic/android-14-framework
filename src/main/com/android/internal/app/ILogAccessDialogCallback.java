package com.android.internal.app;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ILogAccessDialogCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.app.ILogAccessDialogCallback";

    void approveAccessForClient(int i, String str) throws RemoteException;

    void declineAccessForClient(int i, String str) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ILogAccessDialogCallback {
        @Override // com.android.internal.app.ILogAccessDialogCallback
        public void approveAccessForClient(int uid, String packageName) throws RemoteException {
        }

        @Override // com.android.internal.app.ILogAccessDialogCallback
        public void declineAccessForClient(int uid, String packageName) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ILogAccessDialogCallback {
        static final int TRANSACTION_approveAccessForClient = 1;
        static final int TRANSACTION_declineAccessForClient = 2;

        public Stub() {
            attachInterface(this, ILogAccessDialogCallback.DESCRIPTOR);
        }

        public static ILogAccessDialogCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ILogAccessDialogCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ILogAccessDialogCallback)) {
                return (ILogAccessDialogCallback) iin;
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
                    return "approveAccessForClient";
                case 2:
                    return "declineAccessForClient";
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
                data.enforceInterface(ILogAccessDialogCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ILogAccessDialogCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            approveAccessForClient(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            declineAccessForClient(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements ILogAccessDialogCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ILogAccessDialogCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.app.ILogAccessDialogCallback
            public void approveAccessForClient(int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILogAccessDialogCallback.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.ILogAccessDialogCallback
            public void declineAccessForClient(int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ILogAccessDialogCallback.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
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
