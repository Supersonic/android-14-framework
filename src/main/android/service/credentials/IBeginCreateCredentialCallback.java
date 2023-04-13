package android.service.credentials;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
/* loaded from: classes3.dex */
public interface IBeginCreateCredentialCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.credentials.IBeginCreateCredentialCallback";

    void onFailure(String str, CharSequence charSequence) throws RemoteException;

    void onSuccess(BeginCreateCredentialResponse beginCreateCredentialResponse) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IBeginCreateCredentialCallback {
        @Override // android.service.credentials.IBeginCreateCredentialCallback
        public void onSuccess(BeginCreateCredentialResponse request) throws RemoteException {
        }

        @Override // android.service.credentials.IBeginCreateCredentialCallback
        public void onFailure(String errorType, CharSequence message) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IBeginCreateCredentialCallback {
        static final int TRANSACTION_onFailure = 2;
        static final int TRANSACTION_onSuccess = 1;

        public Stub() {
            attachInterface(this, IBeginCreateCredentialCallback.DESCRIPTOR);
        }

        public static IBeginCreateCredentialCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBeginCreateCredentialCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IBeginCreateCredentialCallback)) {
                return (IBeginCreateCredentialCallback) iin;
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
                    return "onSuccess";
                case 2:
                    return "onFailure";
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
                data.enforceInterface(IBeginCreateCredentialCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBeginCreateCredentialCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            BeginCreateCredentialResponse _arg0 = (BeginCreateCredentialResponse) data.readTypedObject(BeginCreateCredentialResponse.CREATOR);
                            data.enforceNoDataAvail();
                            onSuccess(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            CharSequence _arg1 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            onFailure(_arg02, _arg1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IBeginCreateCredentialCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBeginCreateCredentialCallback.DESCRIPTOR;
            }

            @Override // android.service.credentials.IBeginCreateCredentialCallback
            public void onSuccess(BeginCreateCredentialResponse request) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBeginCreateCredentialCallback.DESCRIPTOR);
                    _data.writeTypedObject(request, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.credentials.IBeginCreateCredentialCallback
            public void onFailure(String errorType, CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBeginCreateCredentialCallback.DESCRIPTOR);
                    _data.writeString(errorType);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
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
