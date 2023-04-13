package android.service.euicc;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IEuiccServiceDumpResultCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.euicc.IEuiccServiceDumpResultCallback";

    void onComplete(String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IEuiccServiceDumpResultCallback {
        @Override // android.service.euicc.IEuiccServiceDumpResultCallback
        public void onComplete(String logs) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IEuiccServiceDumpResultCallback {
        static final int TRANSACTION_onComplete = 1;

        public Stub() {
            attachInterface(this, IEuiccServiceDumpResultCallback.DESCRIPTOR);
        }

        public static IEuiccServiceDumpResultCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IEuiccServiceDumpResultCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IEuiccServiceDumpResultCallback)) {
                return (IEuiccServiceDumpResultCallback) iin;
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
                    return "onComplete";
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
                data.enforceInterface(IEuiccServiceDumpResultCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IEuiccServiceDumpResultCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            onComplete(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IEuiccServiceDumpResultCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IEuiccServiceDumpResultCallback.DESCRIPTOR;
            }

            @Override // android.service.euicc.IEuiccServiceDumpResultCallback
            public void onComplete(String logs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IEuiccServiceDumpResultCallback.DESCRIPTOR);
                    _data.writeString(logs);
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
