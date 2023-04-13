package com.android.internal.telephony.euicc;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface ISetNicknameCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telephony.euicc.ISetNicknameCallback";

    void onComplete(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISetNicknameCallback {
        @Override // com.android.internal.telephony.euicc.ISetNicknameCallback
        public void onComplete(int resultCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISetNicknameCallback {
        static final int TRANSACTION_onComplete = 1;

        public Stub() {
            attachInterface(this, ISetNicknameCallback.DESCRIPTOR);
        }

        public static ISetNicknameCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISetNicknameCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISetNicknameCallback)) {
                return (ISetNicknameCallback) iin;
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
                data.enforceInterface(ISetNicknameCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISetNicknameCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onComplete(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISetNicknameCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISetNicknameCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.euicc.ISetNicknameCallback
            public void onComplete(int resultCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISetNicknameCallback.DESCRIPTOR);
                    _data.writeInt(resultCode);
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
