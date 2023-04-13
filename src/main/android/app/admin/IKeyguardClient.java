package android.app.admin;

import android.app.admin.IKeyguardCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IKeyguardClient extends IInterface {
    public static final String DESCRIPTOR = "android.app.admin.IKeyguardClient";

    void onCreateKeyguardSurface(IBinder iBinder, IKeyguardCallback iKeyguardCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IKeyguardClient {
        @Override // android.app.admin.IKeyguardClient
        public void onCreateKeyguardSurface(IBinder hostInputToken, IKeyguardCallback keyguardCallback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IKeyguardClient {
        static final int TRANSACTION_onCreateKeyguardSurface = 1;

        public Stub() {
            attachInterface(this, IKeyguardClient.DESCRIPTOR);
        }

        public static IKeyguardClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IKeyguardClient.DESCRIPTOR);
            if (iin != null && (iin instanceof IKeyguardClient)) {
                return (IKeyguardClient) iin;
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
                    return "onCreateKeyguardSurface";
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
                data.enforceInterface(IKeyguardClient.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IKeyguardClient.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IBinder _arg0 = data.readStrongBinder();
                            IKeyguardCallback _arg1 = IKeyguardCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            onCreateKeyguardSurface(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IKeyguardClient {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IKeyguardClient.DESCRIPTOR;
            }

            @Override // android.app.admin.IKeyguardClient
            public void onCreateKeyguardSurface(IBinder hostInputToken, IKeyguardCallback keyguardCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IKeyguardClient.DESCRIPTOR);
                    _data.writeStrongBinder(hostInputToken);
                    _data.writeStrongInterface(keyguardCallback);
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
