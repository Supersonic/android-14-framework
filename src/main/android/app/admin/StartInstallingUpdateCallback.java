package android.app.admin;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface StartInstallingUpdateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.app.admin.StartInstallingUpdateCallback";

    void onStartInstallingUpdateError(int i, String str) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements StartInstallingUpdateCallback {
        @Override // android.app.admin.StartInstallingUpdateCallback
        public void onStartInstallingUpdateError(int errorCode, String errorMessage) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements StartInstallingUpdateCallback {
        static final int TRANSACTION_onStartInstallingUpdateError = 1;

        public Stub() {
            attachInterface(this, StartInstallingUpdateCallback.DESCRIPTOR);
        }

        public static StartInstallingUpdateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(StartInstallingUpdateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof StartInstallingUpdateCallback)) {
                return (StartInstallingUpdateCallback) iin;
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
                    return "onStartInstallingUpdateError";
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
                data.enforceInterface(StartInstallingUpdateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(StartInstallingUpdateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            onStartInstallingUpdateError(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements StartInstallingUpdateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return StartInstallingUpdateCallback.DESCRIPTOR;
            }

            @Override // android.app.admin.StartInstallingUpdateCallback
            public void onStartInstallingUpdateError(int errorCode, String errorMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(StartInstallingUpdateCallback.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    _data.writeString(errorMessage);
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
