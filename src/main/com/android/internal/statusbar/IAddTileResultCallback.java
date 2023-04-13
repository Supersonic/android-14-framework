package com.android.internal.statusbar;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IAddTileResultCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.statusbar.IAddTileResultCallback";

    void onTileRequest(int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAddTileResultCallback {
        @Override // com.android.internal.statusbar.IAddTileResultCallback
        public void onTileRequest(int userResponse) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAddTileResultCallback {
        static final int TRANSACTION_onTileRequest = 1;

        public Stub() {
            attachInterface(this, IAddTileResultCallback.DESCRIPTOR);
        }

        public static IAddTileResultCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAddTileResultCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IAddTileResultCallback)) {
                return (IAddTileResultCallback) iin;
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
                    return "onTileRequest";
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
                data.enforceInterface(IAddTileResultCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAddTileResultCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onTileRequest(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IAddTileResultCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAddTileResultCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.statusbar.IAddTileResultCallback
            public void onTileRequest(int userResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAddTileResultCallback.DESCRIPTOR);
                    _data.writeInt(userResponse);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
