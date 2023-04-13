package com.android.modules.utils;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.modules.utils.SynchronousResultReceiver;
/* loaded from: classes5.dex */
public interface ISynchronousResultReceiver extends IInterface {
    public static final String DESCRIPTOR = "com.android.modules.utils.ISynchronousResultReceiver";

    void send(SynchronousResultReceiver.Result result) throws RemoteException;

    /* loaded from: classes5.dex */
    public static class Default implements ISynchronousResultReceiver {
        @Override // com.android.modules.utils.ISynchronousResultReceiver
        public void send(SynchronousResultReceiver.Result resultData) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class Stub extends Binder implements ISynchronousResultReceiver {
        static final int TRANSACTION_send = 1;

        public Stub() {
            attachInterface(this, ISynchronousResultReceiver.DESCRIPTOR);
        }

        public static ISynchronousResultReceiver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISynchronousResultReceiver.DESCRIPTOR);
            if (iin != null && (iin instanceof ISynchronousResultReceiver)) {
                return (ISynchronousResultReceiver) iin;
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
                    return "send";
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
                data.enforceInterface(ISynchronousResultReceiver.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISynchronousResultReceiver.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SynchronousResultReceiver.Result _arg0 = (SynchronousResultReceiver.Result) data.readTypedObject(SynchronousResultReceiver.Result.CREATOR);
                            send(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes5.dex */
        public static class Proxy implements ISynchronousResultReceiver {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISynchronousResultReceiver.DESCRIPTOR;
            }

            @Override // com.android.modules.utils.ISynchronousResultReceiver
            public void send(SynchronousResultReceiver.Result resultData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISynchronousResultReceiver.DESCRIPTOR);
                    _data.writeTypedObject(resultData, 0);
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
