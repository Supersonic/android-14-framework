package android.service.trust;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes3.dex */
public interface ITrustAgentServiceCallback extends IInterface {
    void addEscrowToken(byte[] bArr, int i) throws RemoteException;

    void grantTrust(CharSequence charSequence, long j, int i, AndroidFuture androidFuture) throws RemoteException;

    void isEscrowTokenActive(long j, int i) throws RemoteException;

    void lockUser() throws RemoteException;

    void onConfigureCompleted(boolean z, IBinder iBinder) throws RemoteException;

    void removeEscrowToken(long j, int i) throws RemoteException;

    void revokeTrust() throws RemoteException;

    void setManagingTrust(boolean z) throws RemoteException;

    void showKeyguardErrorMessage(CharSequence charSequence) throws RemoteException;

    void unlockUserWithToken(long j, byte[] bArr, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITrustAgentServiceCallback {
        @Override // android.service.trust.ITrustAgentServiceCallback
        public void grantTrust(CharSequence message, long durationMs, int flags, AndroidFuture resultCallback) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void revokeTrust() throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void lockUser() throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void setManagingTrust(boolean managingTrust) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void onConfigureCompleted(boolean result, IBinder token) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void addEscrowToken(byte[] token, int userId) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void isEscrowTokenActive(long handle, int userId) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void removeEscrowToken(long handle, int userId) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void unlockUserWithToken(long handle, byte[] token, int userId) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentServiceCallback
        public void showKeyguardErrorMessage(CharSequence message) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITrustAgentServiceCallback {
        public static final String DESCRIPTOR = "android.service.trust.ITrustAgentServiceCallback";
        static final int TRANSACTION_addEscrowToken = 6;
        static final int TRANSACTION_grantTrust = 1;
        static final int TRANSACTION_isEscrowTokenActive = 7;
        static final int TRANSACTION_lockUser = 3;
        static final int TRANSACTION_onConfigureCompleted = 5;
        static final int TRANSACTION_removeEscrowToken = 8;
        static final int TRANSACTION_revokeTrust = 2;
        static final int TRANSACTION_setManagingTrust = 4;
        static final int TRANSACTION_showKeyguardErrorMessage = 10;
        static final int TRANSACTION_unlockUserWithToken = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITrustAgentServiceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITrustAgentServiceCallback)) {
                return (ITrustAgentServiceCallback) iin;
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
                    return "grantTrust";
                case 2:
                    return "revokeTrust";
                case 3:
                    return "lockUser";
                case 4:
                    return "setManagingTrust";
                case 5:
                    return "onConfigureCompleted";
                case 6:
                    return "addEscrowToken";
                case 7:
                    return "isEscrowTokenActive";
                case 8:
                    return "removeEscrowToken";
                case 9:
                    return "unlockUserWithToken";
                case 10:
                    return "showKeyguardErrorMessage";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            CharSequence _arg0 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            long _arg1 = data.readLong();
                            int _arg2 = data.readInt();
                            AndroidFuture _arg3 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            grantTrust(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            revokeTrust();
                            break;
                        case 3:
                            lockUser();
                            break;
                        case 4:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setManagingTrust(_arg02);
                            break;
                        case 5:
                            boolean _arg03 = data.readBoolean();
                            IBinder _arg12 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onConfigureCompleted(_arg03, _arg12);
                            break;
                        case 6:
                            byte[] _arg04 = data.createByteArray();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            addEscrowToken(_arg04, _arg13);
                            break;
                        case 7:
                            long _arg05 = data.readLong();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            isEscrowTokenActive(_arg05, _arg14);
                            break;
                        case 8:
                            long _arg06 = data.readLong();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            removeEscrowToken(_arg06, _arg15);
                            break;
                        case 9:
                            long _arg07 = data.readLong();
                            byte[] _arg16 = data.createByteArray();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            unlockUserWithToken(_arg07, _arg16, _arg22);
                            break;
                        case 10:
                            CharSequence _arg08 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            data.enforceNoDataAvail();
                            showKeyguardErrorMessage(_arg08);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITrustAgentServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void grantTrust(CharSequence message, long durationMs, int flags, AndroidFuture resultCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(durationMs);
                    _data.writeInt(flags);
                    _data.writeTypedObject(resultCallback, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void revokeTrust() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void lockUser() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void setManagingTrust(boolean managingTrust) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(managingTrust);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void onConfigureCompleted(boolean result, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(result);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void addEscrowToken(byte[] token, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(token);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void isEscrowTokenActive(long handle, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(handle);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void removeEscrowToken(long handle, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(handle);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void unlockUserWithToken(long handle, byte[] token, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(handle);
                    _data.writeByteArray(token);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentServiceCallback
            public void showKeyguardErrorMessage(CharSequence message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (message != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(message, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
