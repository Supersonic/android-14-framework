package android.p008os.storage;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.os.storage.IStorageShutdownObserver */
/* loaded from: classes3.dex */
public interface IStorageShutdownObserver extends IInterface {
    void onShutDownComplete(int i) throws RemoteException;

    /* renamed from: android.os.storage.IStorageShutdownObserver$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IStorageShutdownObserver {
        @Override // android.p008os.storage.IStorageShutdownObserver
        public void onShutDownComplete(int statusCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.storage.IStorageShutdownObserver$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IStorageShutdownObserver {
        public static final String DESCRIPTOR = "android.os.storage.IStorageShutdownObserver";
        static final int TRANSACTION_onShutDownComplete = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStorageShutdownObserver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStorageShutdownObserver)) {
                return (IStorageShutdownObserver) iin;
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
                    return "onShutDownComplete";
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
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onShutDownComplete(_arg0);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* renamed from: android.os.storage.IStorageShutdownObserver$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IStorageShutdownObserver {
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

            @Override // android.p008os.storage.IStorageShutdownObserver
            public void onShutDownComplete(int statusCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(statusCode);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
