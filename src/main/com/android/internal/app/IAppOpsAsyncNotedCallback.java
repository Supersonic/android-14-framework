package com.android.internal.app;

import android.app.AsyncNotedAppOp;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IAppOpsAsyncNotedCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.app.IAppOpsAsyncNotedCallback";

    void opNoted(AsyncNotedAppOp asyncNotedAppOp) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAppOpsAsyncNotedCallback {
        @Override // com.android.internal.app.IAppOpsAsyncNotedCallback
        public void opNoted(AsyncNotedAppOp op) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAppOpsAsyncNotedCallback {
        static final int TRANSACTION_opNoted = 1;

        public Stub() {
            attachInterface(this, IAppOpsAsyncNotedCallback.DESCRIPTOR);
        }

        public static IAppOpsAsyncNotedCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAppOpsAsyncNotedCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IAppOpsAsyncNotedCallback)) {
                return (IAppOpsAsyncNotedCallback) iin;
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
                    return "opNoted";
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
                data.enforceInterface(IAppOpsAsyncNotedCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAppOpsAsyncNotedCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            AsyncNotedAppOp _arg0 = (AsyncNotedAppOp) data.readTypedObject(AsyncNotedAppOp.CREATOR);
                            data.enforceNoDataAvail();
                            opNoted(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IAppOpsAsyncNotedCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAppOpsAsyncNotedCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.app.IAppOpsAsyncNotedCallback
            public void opNoted(AsyncNotedAppOp op) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAppOpsAsyncNotedCallback.DESCRIPTOR);
                    _data.writeTypedObject(op, 0);
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
