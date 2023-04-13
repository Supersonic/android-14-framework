package android.window;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface ISurfaceSyncGroupCompletedListener extends IInterface {
    public static final String DESCRIPTOR = "android.window.ISurfaceSyncGroupCompletedListener";

    void onSurfaceSyncGroupComplete() throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements ISurfaceSyncGroupCompletedListener {
        @Override // android.window.ISurfaceSyncGroupCompletedListener
        public void onSurfaceSyncGroupComplete() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements ISurfaceSyncGroupCompletedListener {
        static final int TRANSACTION_onSurfaceSyncGroupComplete = 1;

        public Stub() {
            attachInterface(this, ISurfaceSyncGroupCompletedListener.DESCRIPTOR);
        }

        public static ISurfaceSyncGroupCompletedListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISurfaceSyncGroupCompletedListener.DESCRIPTOR);
            if (iin != null && (iin instanceof ISurfaceSyncGroupCompletedListener)) {
                return (ISurfaceSyncGroupCompletedListener) iin;
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
                    return "onSurfaceSyncGroupComplete";
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
                data.enforceInterface(ISurfaceSyncGroupCompletedListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISurfaceSyncGroupCompletedListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onSurfaceSyncGroupComplete();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements ISurfaceSyncGroupCompletedListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISurfaceSyncGroupCompletedListener.DESCRIPTOR;
            }

            @Override // android.window.ISurfaceSyncGroupCompletedListener
            public void onSurfaceSyncGroupComplete() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISurfaceSyncGroupCompletedListener.DESCRIPTOR);
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
