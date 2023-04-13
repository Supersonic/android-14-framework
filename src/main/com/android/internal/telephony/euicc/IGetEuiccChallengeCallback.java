package com.android.internal.telephony.euicc;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IGetEuiccChallengeCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telephony.euicc.IGetEuiccChallengeCallback";

    void onComplete(int i, byte[] bArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IGetEuiccChallengeCallback {
        @Override // com.android.internal.telephony.euicc.IGetEuiccChallengeCallback
        public void onComplete(int resultCode, byte[] challenge) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IGetEuiccChallengeCallback {
        static final int TRANSACTION_onComplete = 1;

        public Stub() {
            attachInterface(this, IGetEuiccChallengeCallback.DESCRIPTOR);
        }

        public static IGetEuiccChallengeCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IGetEuiccChallengeCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IGetEuiccChallengeCallback)) {
                return (IGetEuiccChallengeCallback) iin;
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
                data.enforceInterface(IGetEuiccChallengeCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IGetEuiccChallengeCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            byte[] _arg1 = data.createByteArray();
                            data.enforceNoDataAvail();
                            onComplete(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IGetEuiccChallengeCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IGetEuiccChallengeCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.euicc.IGetEuiccChallengeCallback
            public void onComplete(int resultCode, byte[] challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IGetEuiccChallengeCallback.DESCRIPTOR);
                    _data.writeInt(resultCode);
                    _data.writeByteArray(challenge);
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
