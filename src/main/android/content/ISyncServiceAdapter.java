package android.content;

import android.content.ISyncContext;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISyncServiceAdapter extends IInterface {
    void cancelSync(ISyncContext iSyncContext) throws RemoteException;

    void startSync(ISyncContext iSyncContext, Bundle bundle) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISyncServiceAdapter {
        @Override // android.content.ISyncServiceAdapter
        public void startSync(ISyncContext syncContext, Bundle extras) throws RemoteException {
        }

        @Override // android.content.ISyncServiceAdapter
        public void cancelSync(ISyncContext syncContext) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISyncServiceAdapter {
        public static final String DESCRIPTOR = "android.content.ISyncServiceAdapter";
        static final int TRANSACTION_cancelSync = 2;
        static final int TRANSACTION_startSync = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISyncServiceAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISyncServiceAdapter)) {
                return (ISyncServiceAdapter) iin;
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
                    return "startSync";
                case 2:
                    return "cancelSync";
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
                            ISyncContext _arg0 = ISyncContext.Stub.asInterface(data.readStrongBinder());
                            Bundle _arg1 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            startSync(_arg0, _arg1);
                            break;
                        case 2:
                            ISyncContext _arg02 = ISyncContext.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            cancelSync(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISyncServiceAdapter {
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

            @Override // android.content.ISyncServiceAdapter
            public void startSync(ISyncContext syncContext, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(syncContext);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.content.ISyncServiceAdapter
            public void cancelSync(ISyncContext syncContext) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(syncContext);
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
