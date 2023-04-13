package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.SipDetails;
/* loaded from: classes3.dex */
public interface IPublishResponseCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IPublishResponseCallback";

    void onCommandError(int i) throws RemoteException;

    void onNetworkResponse(SipDetails sipDetails) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IPublishResponseCallback {
        @Override // android.telephony.ims.aidl.IPublishResponseCallback
        public void onCommandError(int code) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IPublishResponseCallback
        public void onNetworkResponse(SipDetails details) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IPublishResponseCallback {
        static final int TRANSACTION_onCommandError = 1;
        static final int TRANSACTION_onNetworkResponse = 2;

        public Stub() {
            attachInterface(this, IPublishResponseCallback.DESCRIPTOR);
        }

        public static IPublishResponseCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPublishResponseCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IPublishResponseCallback)) {
                return (IPublishResponseCallback) iin;
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
                    return "onCommandError";
                case 2:
                    return "onNetworkResponse";
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
                data.enforceInterface(IPublishResponseCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPublishResponseCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onCommandError(_arg0);
                            break;
                        case 2:
                            SipDetails _arg02 = (SipDetails) data.readTypedObject(SipDetails.CREATOR);
                            data.enforceNoDataAvail();
                            onNetworkResponse(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IPublishResponseCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPublishResponseCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IPublishResponseCallback
            public void onCommandError(int code) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPublishResponseCallback.DESCRIPTOR);
                    _data.writeInt(code);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IPublishResponseCallback
            public void onNetworkResponse(SipDetails details) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPublishResponseCallback.DESCRIPTOR);
                    _data.writeTypedObject(details, 0);
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
