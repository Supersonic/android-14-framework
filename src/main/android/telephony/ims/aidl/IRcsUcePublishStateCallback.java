package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.PublishAttributes;
/* loaded from: classes3.dex */
public interface IRcsUcePublishStateCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IRcsUcePublishStateCallback";

    void onPublishUpdated(PublishAttributes publishAttributes) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IRcsUcePublishStateCallback {
        @Override // android.telephony.ims.aidl.IRcsUcePublishStateCallback
        public void onPublishUpdated(PublishAttributes attributes) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IRcsUcePublishStateCallback {
        static final int TRANSACTION_onPublishUpdated = 1;

        public Stub() {
            attachInterface(this, IRcsUcePublishStateCallback.DESCRIPTOR);
        }

        public static IRcsUcePublishStateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRcsUcePublishStateCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IRcsUcePublishStateCallback)) {
                return (IRcsUcePublishStateCallback) iin;
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
                    return "onPublishUpdated";
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
                data.enforceInterface(IRcsUcePublishStateCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRcsUcePublishStateCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            PublishAttributes _arg0 = (PublishAttributes) data.readTypedObject(PublishAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            onPublishUpdated(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IRcsUcePublishStateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRcsUcePublishStateCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IRcsUcePublishStateCallback
            public void onPublishUpdated(PublishAttributes attributes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRcsUcePublishStateCallback.DESCRIPTOR);
                    _data.writeTypedObject(attributes, 0);
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
