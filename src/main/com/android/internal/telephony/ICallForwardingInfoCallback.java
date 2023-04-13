package com.android.internal.telephony;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.CallForwardingInfo;
/* loaded from: classes3.dex */
public interface ICallForwardingInfoCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.telephony.ICallForwardingInfoCallback";

    void onCallForwardingInfoAvailable(CallForwardingInfo callForwardingInfo) throws RemoteException;

    void onError(int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICallForwardingInfoCallback {
        @Override // com.android.internal.telephony.ICallForwardingInfoCallback
        public void onCallForwardingInfoAvailable(CallForwardingInfo info) throws RemoteException {
        }

        @Override // com.android.internal.telephony.ICallForwardingInfoCallback
        public void onError(int error) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICallForwardingInfoCallback {
        static final int TRANSACTION_onCallForwardingInfoAvailable = 1;
        static final int TRANSACTION_onError = 2;

        public Stub() {
            attachInterface(this, ICallForwardingInfoCallback.DESCRIPTOR);
        }

        public static ICallForwardingInfoCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICallForwardingInfoCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ICallForwardingInfoCallback)) {
                return (ICallForwardingInfoCallback) iin;
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
                    return "onCallForwardingInfoAvailable";
                case 2:
                    return "onError";
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
                data.enforceInterface(ICallForwardingInfoCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICallForwardingInfoCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            CallForwardingInfo _arg0 = (CallForwardingInfo) data.readTypedObject(CallForwardingInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onCallForwardingInfoAvailable(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements ICallForwardingInfoCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICallForwardingInfoCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.telephony.ICallForwardingInfoCallback
            public void onCallForwardingInfoAvailable(CallForwardingInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallForwardingInfoCallback.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.ICallForwardingInfoCallback
            public void onError(int error) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ICallForwardingInfoCallback.DESCRIPTOR);
                    _data.writeInt(error);
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
