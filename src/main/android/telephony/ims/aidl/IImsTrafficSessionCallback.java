package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.feature.ConnectionFailureInfo;
/* loaded from: classes3.dex */
public interface IImsTrafficSessionCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsTrafficSessionCallback";

    void onError(ConnectionFailureInfo connectionFailureInfo) throws RemoteException;

    void onReady() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsTrafficSessionCallback {
        @Override // android.telephony.ims.aidl.IImsTrafficSessionCallback
        public void onReady() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsTrafficSessionCallback
        public void onError(ConnectionFailureInfo info) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsTrafficSessionCallback {
        static final int TRANSACTION_onError = 2;
        static final int TRANSACTION_onReady = 1;

        public Stub() {
            attachInterface(this, IImsTrafficSessionCallback.DESCRIPTOR);
        }

        public static IImsTrafficSessionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsTrafficSessionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsTrafficSessionCallback)) {
                return (IImsTrafficSessionCallback) iin;
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
                    return "onReady";
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
                data.enforceInterface(IImsTrafficSessionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsTrafficSessionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            onReady();
                            break;
                        case 2:
                            ConnectionFailureInfo _arg0 = (ConnectionFailureInfo) data.readTypedObject(ConnectionFailureInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onError(_arg0);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IImsTrafficSessionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsTrafficSessionCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsTrafficSessionCallback
            public void onReady() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsTrafficSessionCallback.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsTrafficSessionCallback
            public void onError(ConnectionFailureInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsTrafficSessionCallback.DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
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
