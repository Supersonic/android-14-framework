package android.telephony.ims.aidl;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.telephony.ims.aidl.IImsRegistrationCallback;
/* loaded from: classes3.dex */
public interface IImsRegistration extends IInterface {
    public static final String DESCRIPTOR = "android.telephony.ims.aidl.IImsRegistration";

    void addRegistrationCallback(IImsRegistrationCallback iImsRegistrationCallback) throws RemoteException;

    int getRegistrationTechnology() throws RemoteException;

    void removeRegistrationCallback(IImsRegistrationCallback iImsRegistrationCallback) throws RemoteException;

    void triggerDeregistration(int i) throws RemoteException;

    void triggerFullNetworkRegistration(int i, String str) throws RemoteException;

    void triggerSipDelegateDeregistration() throws RemoteException;

    void triggerUpdateSipDelegateRegistration() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IImsRegistration {
        @Override // android.telephony.ims.aidl.IImsRegistration
        public int getRegistrationTechnology() throws RemoteException {
            return 0;
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void addRegistrationCallback(IImsRegistrationCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void removeRegistrationCallback(IImsRegistrationCallback c) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void triggerFullNetworkRegistration(int sipCode, String sipReason) throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void triggerUpdateSipDelegateRegistration() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void triggerSipDelegateDeregistration() throws RemoteException {
        }

        @Override // android.telephony.ims.aidl.IImsRegistration
        public void triggerDeregistration(int reason) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IImsRegistration {
        static final int TRANSACTION_addRegistrationCallback = 2;
        static final int TRANSACTION_getRegistrationTechnology = 1;
        static final int TRANSACTION_removeRegistrationCallback = 3;
        static final int TRANSACTION_triggerDeregistration = 7;
        static final int TRANSACTION_triggerFullNetworkRegistration = 4;
        static final int TRANSACTION_triggerSipDelegateDeregistration = 6;
        static final int TRANSACTION_triggerUpdateSipDelegateRegistration = 5;

        public Stub() {
            attachInterface(this, IImsRegistration.DESCRIPTOR);
        }

        public static IImsRegistration asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IImsRegistration.DESCRIPTOR);
            if (iin != null && (iin instanceof IImsRegistration)) {
                return (IImsRegistration) iin;
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
                    return "getRegistrationTechnology";
                case 2:
                    return "addRegistrationCallback";
                case 3:
                    return "removeRegistrationCallback";
                case 4:
                    return "triggerFullNetworkRegistration";
                case 5:
                    return "triggerUpdateSipDelegateRegistration";
                case 6:
                    return "triggerSipDelegateDeregistration";
                case 7:
                    return "triggerDeregistration";
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
                data.enforceInterface(IImsRegistration.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IImsRegistration.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _result = getRegistrationTechnology();
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            IImsRegistrationCallback _arg0 = IImsRegistrationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addRegistrationCallback(_arg0);
                            break;
                        case 3:
                            IImsRegistrationCallback _arg02 = IImsRegistrationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeRegistrationCallback(_arg02);
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            triggerFullNetworkRegistration(_arg03, _arg1);
                            break;
                        case 5:
                            triggerUpdateSipDelegateRegistration();
                            break;
                        case 6:
                            triggerSipDelegateDeregistration();
                            break;
                        case 7:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            triggerDeregistration(_arg04);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IImsRegistration {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IImsRegistration.DESCRIPTOR;
            }

            @Override // android.telephony.ims.aidl.IImsRegistration
            public int getRegistrationTechnology() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IImsRegistration.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRegistration
            public void addRegistrationCallback(IImsRegistrationCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRegistration.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRegistration
            public void removeRegistrationCallback(IImsRegistrationCallback c) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRegistration.DESCRIPTOR);
                    _data.writeStrongInterface(c);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRegistration
            public void triggerFullNetworkRegistration(int sipCode, String sipReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRegistration.DESCRIPTOR);
                    _data.writeInt(sipCode);
                    _data.writeString(sipReason);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRegistration
            public void triggerUpdateSipDelegateRegistration() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRegistration.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRegistration
            public void triggerSipDelegateDeregistration() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRegistration.DESCRIPTOR);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.telephony.ims.aidl.IImsRegistration
            public void triggerDeregistration(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IImsRegistration.DESCRIPTOR);
                    _data.writeInt(reason);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 6;
        }
    }
}
