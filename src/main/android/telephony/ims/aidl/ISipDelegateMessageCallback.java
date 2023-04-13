package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.SipMessage;
/* loaded from: classes3.dex */
public interface ISipDelegateMessageCallback extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.ISipDelegateMessageCallback";

    void onMessageReceived(SipMessage sipMessage) throws RemoteException;

    void onMessageSendFailure(String str, int i) throws RemoteException;

    void onMessageSent(String str) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISipDelegateMessageCallback {
        @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
        public void onMessageReceived(SipMessage message) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
        public void onMessageSent(String viaTransactionId) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
        public void onMessageSendFailure(String viaTransactionId, int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISipDelegateMessageCallback {
        static final int TRANSACTION_onMessageReceived = 1;
        static final int TRANSACTION_onMessageSendFailure = 3;
        static final int TRANSACTION_onMessageSent = 2;

        public Stub() {
            attachInterface(this, ISipDelegateMessageCallback.DESCRIPTOR);
        }

        public static ISipDelegateMessageCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISipDelegateMessageCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof ISipDelegateMessageCallback)) {
                return (ISipDelegateMessageCallback) iin;
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
                    return "onMessageReceived";
                case 2:
                    return "onMessageSent";
                case 3:
                    return "onMessageSendFailure";
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
                data.enforceInterface(ISipDelegateMessageCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISipDelegateMessageCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            SipMessage _arg0 = (SipMessage) data.readTypedObject(SipMessage.CREATOR);
                            data.enforceNoDataAvail();
                            onMessageReceived(_arg0);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            onMessageSent(_arg02);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onMessageSendFailure(_arg03, _arg1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISipDelegateMessageCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISipDelegateMessageCallback.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
            public void onMessageReceived(SipMessage message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateMessageCallback.DESCRIPTOR);
                    _data.writeTypedObject(message, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
            public void onMessageSent(String viaTransactionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateMessageCallback.DESCRIPTOR);
                    _data.writeString(viaTransactionId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipDelegateMessageCallback
            public void onMessageSendFailure(String viaTransactionId, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipDelegateMessageCallback.DESCRIPTOR);
                    _data.writeString(viaTransactionId);
                    _data.writeInt(reason);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}