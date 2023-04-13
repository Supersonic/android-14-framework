package android.service.dreams;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.service.dreams.IDreamOverlayClient;
/* loaded from: classes3.dex */
public interface IDreamOverlayClientCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.dreams.IDreamOverlayClientCallback";

    void onDreamOverlayClient(IDreamOverlayClient iDreamOverlayClient) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IDreamOverlayClientCallback {
        @Override // android.service.dreams.IDreamOverlayClientCallback
        public void onDreamOverlayClient(IDreamOverlayClient client) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IDreamOverlayClientCallback {
        static final int TRANSACTION_onDreamOverlayClient = 1;

        public Stub() {
            attachInterface(this, IDreamOverlayClientCallback.DESCRIPTOR);
        }

        public static IDreamOverlayClientCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IDreamOverlayClientCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IDreamOverlayClientCallback)) {
                return (IDreamOverlayClientCallback) iin;
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
                    return "onDreamOverlayClient";
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
                data.enforceInterface(IDreamOverlayClientCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IDreamOverlayClientCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IDreamOverlayClient _arg0 = IDreamOverlayClient.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onDreamOverlayClient(_arg0);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IDreamOverlayClientCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IDreamOverlayClientCallback.DESCRIPTOR;
            }

            @Override // android.service.dreams.IDreamOverlayClientCallback
            public void onDreamOverlayClient(IDreamOverlayClient client) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IDreamOverlayClientCallback.DESCRIPTOR);
                    _data.writeStrongInterface(client);
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
