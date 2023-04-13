package com.android.internal.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.EmergencyRegResult;
/* loaded from: classes3.dex */
public interface IWwanSelectorResultCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telephony.IWwanSelectorResultCallback";

    void onComplete(EmergencyRegResult emergencyRegResult) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IWwanSelectorResultCallback {
        @Override // com.android.internal.telephony.IWwanSelectorResultCallback
        public void onComplete(EmergencyRegResult result) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IWwanSelectorResultCallback {
        static final int TRANSACTION_onComplete = 1;

        public Stub() {
            attachInterface(this, IWwanSelectorResultCallback.DESCRIPTOR);
        }

        public static IWwanSelectorResultCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWwanSelectorResultCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IWwanSelectorResultCallback)) {
                return (IWwanSelectorResultCallback) iin;
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
                data.enforceInterface(IWwanSelectorResultCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWwanSelectorResultCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            EmergencyRegResult _arg0 = (EmergencyRegResult) data.readTypedObject(EmergencyRegResult.CREATOR);
                            data.enforceNoDataAvail();
                            onComplete(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IWwanSelectorResultCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWwanSelectorResultCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.IWwanSelectorResultCallback
            public void onComplete(EmergencyRegResult result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWwanSelectorResultCallback.DESCRIPTOR);
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
