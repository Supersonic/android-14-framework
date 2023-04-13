package android.app.smartspace;

import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISmartspaceCallback extends IInterface {
    public static final String DESCRIPTOR = "android.app.smartspace.ISmartspaceCallback";

    void onResult(ParceledListSlice parceledListSlice) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISmartspaceCallback {
        @Override // android.app.smartspace.ISmartspaceCallback
        public void onResult(ParceledListSlice result) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISmartspaceCallback {
        static final int TRANSACTION_onResult = 1;

        public Stub() {
            attachInterface(this, ISmartspaceCallback.DESCRIPTOR);
        }

        public static ISmartspaceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISmartspaceCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISmartspaceCallback)) {
                return (ISmartspaceCallback) iin;
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
                    return "onResult";
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
                data.enforceInterface(ISmartspaceCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISmartspaceCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ParceledListSlice _arg0 = (ParceledListSlice) data.readTypedObject(ParceledListSlice.CREATOR);
                            data.enforceNoDataAvail();
                            onResult(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISmartspaceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISmartspaceCallback.DESCRIPTOR;
            }

            @Override // android.app.smartspace.ISmartspaceCallback
            public void onResult(ParceledListSlice result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISmartspaceCallback.DESCRIPTOR);
                    _data.writeTypedObject(result, 0);
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
