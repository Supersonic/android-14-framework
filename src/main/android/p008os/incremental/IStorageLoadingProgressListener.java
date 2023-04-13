package android.p008os.incremental;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* renamed from: android.os.incremental.IStorageLoadingProgressListener */
/* loaded from: classes3.dex */
public interface IStorageLoadingProgressListener extends IInterface {
    public static final String DESCRIPTOR = "android.os.incremental.IStorageLoadingProgressListener";

    void onStorageLoadingProgressChanged(int i, float f) throws RemoteException;

    /* renamed from: android.os.incremental.IStorageLoadingProgressListener$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IStorageLoadingProgressListener {
        @Override // android.p008os.incremental.IStorageLoadingProgressListener
        public void onStorageLoadingProgressChanged(int storageId, float progress) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.incremental.IStorageLoadingProgressListener$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IStorageLoadingProgressListener {
        static final int TRANSACTION_onStorageLoadingProgressChanged = 1;

        public Stub() {
            attachInterface(this, IStorageLoadingProgressListener.DESCRIPTOR);
        }

        public static IStorageLoadingProgressListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IStorageLoadingProgressListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IStorageLoadingProgressListener)) {
                return (IStorageLoadingProgressListener) iin;
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
                    return "onStorageLoadingProgressChanged";
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
                data.enforceInterface(IStorageLoadingProgressListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IStorageLoadingProgressListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            float _arg1 = data.readFloat();
                            data.enforceNoDataAvail();
                            onStorageLoadingProgressChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.incremental.IStorageLoadingProgressListener$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IStorageLoadingProgressListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IStorageLoadingProgressListener.DESCRIPTOR;
            }

            @Override // android.p008os.incremental.IStorageLoadingProgressListener
            public void onStorageLoadingProgressChanged(int storageId, float progress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IStorageLoadingProgressListener.DESCRIPTOR);
                    _data.writeInt(storageId);
                    _data.writeFloat(progress);
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
