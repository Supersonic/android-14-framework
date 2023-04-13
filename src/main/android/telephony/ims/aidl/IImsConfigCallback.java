package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IImsConfigCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsConfigCallback";

    void onIntConfigChanged(int i, int i2) throws RemoteException;

    void onStringConfigChanged(int i, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsConfigCallback {
        @Override // android.telephony.ims.aidl.IImsConfigCallback
        public void onIntConfigChanged(int item, int value) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsConfigCallback
        public void onStringConfigChanged(int item, String value) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsConfigCallback {
        static final int TRANSACTION_onIntConfigChanged = 1;
        static final int TRANSACTION_onStringConfigChanged = 2;

        public Stub() {
            attachInterface(this, IImsConfigCallback.DESCRIPTOR);
        }

        public static IImsConfigCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsConfigCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsConfigCallback)) {
                return (IImsConfigCallback) iin;
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
                    return "onIntConfigChanged";
                case 2:
                    return "onStringConfigChanged";
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
                data.enforceInterface(IImsConfigCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsConfigCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onIntConfigChanged(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            onStringConfigChanged(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IImsConfigCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsConfigCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsConfigCallback
            public void onIntConfigChanged(int item, int value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsConfigCallback.DESCRIPTOR);
                    _data.writeInt(item);
                    _data.writeInt(value);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsConfigCallback
            public void onStringConfigChanged(int item, String value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsConfigCallback.DESCRIPTOR);
                    _data.writeInt(item);
                    _data.writeString(value);
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
