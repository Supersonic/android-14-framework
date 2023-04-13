package android.service.credentials;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
/* loaded from: classes3.dex */
public interface IBeginGetCredentialCallback extends IInterface {
    public static final String DESCRIPTOR = "android.service.credentials.IBeginGetCredentialCallback";

    void onFailure(String str, CharSequence charSequence) throws RemoteException;

    void onSuccess(BeginGetCredentialResponse beginGetCredentialResponse) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IBeginGetCredentialCallback {
        @Override // android.service.credentials.IBeginGetCredentialCallback
        public void onSuccess(BeginGetCredentialResponse response) throws RemoteException {
        }

        @Override // android.service.credentials.IBeginGetCredentialCallback
        public void onFailure(String errorType, CharSequence message) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IBeginGetCredentialCallback {
        static final int TRANSACTION_onFailure = 2;
        static final int TRANSACTION_onSuccess = 1;

        public Stub() {
            attachInterface(this, IBeginGetCredentialCallback.DESCRIPTOR);
        }

        public static IBeginGetCredentialCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBeginGetCredentialCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IBeginGetCredentialCallback)) {
                return (IBeginGetCredentialCallback) iin;
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
                data.enforceInterface(IBeginGetCredentialCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBeginGetCredentialCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            BeginGetCredentialResponse _arg0 = (BeginGetCredentialResponse) data.readTypedObject(BeginGetCredentialResponse.CREATOR);
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
        private static class Proxy implements IBeginGetCredentialCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBeginGetCredentialCallback.DESCRIPTOR;
            }

            @Override // android.service.credentials.IBeginGetCredentialCallback
            public void onSuccess(BeginGetCredentialResponse response) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBeginGetCredentialCallback.DESCRIPTOR);
                    _data.writeTypedObject(response, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.credentials.IBeginGetCredentialCallback
            public void onFailure(String errorType, CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBeginGetCredentialCallback.DESCRIPTOR);
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
