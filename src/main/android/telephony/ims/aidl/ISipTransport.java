package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.DelegateRequest;
import android.telephony.ims.aidl.ISipDelegate;
import android.telephony.ims.aidl.ISipDelegateMessageCallback;
import android.telephony.ims.aidl.ISipDelegateStateCallback;
/* loaded from: classes3.dex */
public interface ISipTransport extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.ISipTransport";

    void createSipDelegate(int i, DelegateRequest delegateRequest, ISipDelegateStateCallback iSipDelegateStateCallback, ISipDelegateMessageCallback iSipDelegateMessageCallback) throws RemoteException;

    void destroySipDelegate(ISipDelegate iSipDelegate, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ISipTransport {
        @Override // android.telephony.ims.aidl.ISipTransport
        public void createSipDelegate(int subId, DelegateRequest request, ISipDelegateStateCallback dc, ISipDelegateMessageCallback mc) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.ISipTransport
        public void destroySipDelegate(ISipDelegate delegate, int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ISipTransport {
        static final int TRANSACTION_createSipDelegate = 1;
        static final int TRANSACTION_destroySipDelegate = 2;

        public Stub() {
            attachInterface(this, ISipTransport.DESCRIPTOR);
        }

        public static ISipTransport asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISipTransport.DESCRIPTOR);
            if (iin != null && (iin instanceof ISipTransport)) {
                return (ISipTransport) iin;
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
                    return "createSipDelegate";
                case 2:
                    return "destroySipDelegate";
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
                data.enforceInterface(ISipTransport.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISipTransport.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            DelegateRequest _arg1 = (DelegateRequest) data.readTypedObject(DelegateRequest.CREATOR);
                            ISipDelegateStateCallback _arg2 = ISipDelegateStateCallback.Stub.asInterface(data.readStrongBinder());
                            ISipDelegateMessageCallback _arg3 = ISipDelegateMessageCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            createSipDelegate(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            ISipDelegate _arg02 = ISipDelegate.Stub.asInterface(data.readStrongBinder());
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            destroySipDelegate(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ISipTransport {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISipTransport.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.ISipTransport
            public void createSipDelegate(int subId, DelegateRequest request, ISipDelegateStateCallback dc, ISipDelegateMessageCallback mc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipTransport.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeTypedObject(request, 0);
                    _data.writeStrongInterface(dc);
                    _data.writeStrongInterface(mc);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.ISipTransport
            public void destroySipDelegate(ISipDelegate delegate, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(ISipTransport.DESCRIPTOR);
                    _data.writeStrongInterface(delegate);
                    _data.writeInt(reason);
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
