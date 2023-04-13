package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.RcsContactUceCapability;
/* loaded from: classes3.dex */
public interface IOptionsRequestCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IOptionsRequestCallback";

    void respondToCapabilityRequest(RcsContactUceCapability rcsContactUceCapability, boolean z) throws RemoteException;

    void respondToCapabilityRequestWithError(int i, String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IOptionsRequestCallback {
        @Override // android.telephony.ims.aidl.IOptionsRequestCallback
        public void respondToCapabilityRequest(RcsContactUceCapability ownCapabilities, boolean isBlocked) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IOptionsRequestCallback
        public void respondToCapabilityRequestWithError(int code, String reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IOptionsRequestCallback {
        static final int TRANSACTION_respondToCapabilityRequest = 1;
        static final int TRANSACTION_respondToCapabilityRequestWithError = 2;

        public Stub() {
            attachInterface(this, IOptionsRequestCallback.DESCRIPTOR);
        }

        public static IOptionsRequestCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IOptionsRequestCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IOptionsRequestCallback)) {
                return (IOptionsRequestCallback) iin;
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
                    return "respondToCapabilityRequest";
                case 2:
                    return "respondToCapabilityRequestWithError";
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
                data.enforceInterface(IOptionsRequestCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IOptionsRequestCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            RcsContactUceCapability _arg0 = (RcsContactUceCapability) data.readTypedObject(RcsContactUceCapability.CREATOR);
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            respondToCapabilityRequest(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            respondToCapabilityRequestWithError(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IOptionsRequestCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IOptionsRequestCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IOptionsRequestCallback
            public void respondToCapabilityRequest(RcsContactUceCapability ownCapabilities, boolean isBlocked) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOptionsRequestCallback.DESCRIPTOR);
                    _data.writeTypedObject(ownCapabilities, 0);
                    _data.writeBoolean(isBlocked);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IOptionsRequestCallback
            public void respondToCapabilityRequestWithError(int code, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IOptionsRequestCallback.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(reason);
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
