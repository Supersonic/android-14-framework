package android.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IBootstrapAuthenticationCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.IBootstrapAuthenticationCallback";

    void onAuthenticationFailure(int i, int i2) throws RemoteException;

    void onKeysAvailable(int i, byte[] bArr, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IBootstrapAuthenticationCallback {
        @Override // android.telephony.IBootstrapAuthenticationCallback
        public void onKeysAvailable(int token, byte[] gbaKey, String btId) throws RemoteException {
        }

        @Override // android.telephony.IBootstrapAuthenticationCallback
        public void onAuthenticationFailure(int token, int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IBootstrapAuthenticationCallback {
        static final int TRANSACTION_onAuthenticationFailure = 2;
        static final int TRANSACTION_onKeysAvailable = 1;

        public Stub() {
            attachInterface(this, IBootstrapAuthenticationCallback.DESCRIPTOR);
        }

        public static IBootstrapAuthenticationCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBootstrapAuthenticationCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IBootstrapAuthenticationCallback)) {
                return (IBootstrapAuthenticationCallback) iin;
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
                    return "onKeysAvailable";
                case 2:
                    return "onAuthenticationFailure";
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
                data.enforceInterface(IBootstrapAuthenticationCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBootstrapAuthenticationCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            byte[] _arg1 = data.createByteArray();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            onKeysAvailable(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onAuthenticationFailure(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IBootstrapAuthenticationCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBootstrapAuthenticationCallback.DESCRIPTOR;
            }

            @Override // android.telephony.IBootstrapAuthenticationCallback
            public void onKeysAvailable(int token, byte[] gbaKey, String btId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBootstrapAuthenticationCallback.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeByteArray(gbaKey);
                    _data.writeString(btId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.IBootstrapAuthenticationCallback
            public void onAuthenticationFailure(int token, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBootstrapAuthenticationCallback.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeInt(reason);
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
