package android.service.trust;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.service.trust.ITrustAgentServiceCallback;
import java.util.List;
/* loaded from: classes3.dex */
public interface ITrustAgentService extends IInterface {
    void onConfigure(List<PersistableBundle> list, IBinder iBinder) throws RemoteException;

    void onDeviceLocked() throws RemoteException;

    void onDeviceUnlocked() throws RemoteException;

    void onEscrowTokenAdded(byte[] bArr, long j, UserHandle userHandle) throws RemoteException;

    void onEscrowTokenRemoved(long j, boolean z) throws RemoteException;

    void onTokenStateReceived(long j, int i) throws RemoteException;

    void onTrustTimeout() throws RemoteException;

    void onUnlockAttempt(boolean z) throws RemoteException;

    void onUnlockLockout(int i) throws RemoteException;

    void onUserMayRequestUnlock() throws RemoteException;

    void onUserRequestedUnlock(boolean z) throws RemoteException;

    void setCallback(ITrustAgentServiceCallback iTrustAgentServiceCallback) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ITrustAgentService {
        @Override // android.service.trust.ITrustAgentService
        public void onUnlockAttempt(boolean successful) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onUserRequestedUnlock(boolean dismissKeyguard) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onUserMayRequestUnlock() throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onUnlockLockout(int timeoutMs) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onTrustTimeout() throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onDeviceLocked() throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onDeviceUnlocked() throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onConfigure(List<PersistableBundle> options, IBinder token) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void setCallback(ITrustAgentServiceCallback callback) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onEscrowTokenAdded(byte[] token, long handle, UserHandle user) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onTokenStateReceived(long handle, int tokenState) throws RemoteException {
        }

        @Override // android.service.trust.ITrustAgentService
        public void onEscrowTokenRemoved(long handle, boolean successful) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ITrustAgentService {
        public static final String DESCRIPTOR = "android.service.trust.ITrustAgentService";
        static final int TRANSACTION_onConfigure = 8;
        static final int TRANSACTION_onDeviceLocked = 6;
        static final int TRANSACTION_onDeviceUnlocked = 7;
        static final int TRANSACTION_onEscrowTokenAdded = 10;
        static final int TRANSACTION_onEscrowTokenRemoved = 12;
        static final int TRANSACTION_onTokenStateReceived = 11;
        static final int TRANSACTION_onTrustTimeout = 5;
        static final int TRANSACTION_onUnlockAttempt = 1;
        static final int TRANSACTION_onUnlockLockout = 4;
        static final int TRANSACTION_onUserMayRequestUnlock = 3;
        static final int TRANSACTION_onUserRequestedUnlock = 2;
        static final int TRANSACTION_setCallback = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITrustAgentService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITrustAgentService)) {
                return (ITrustAgentService) iin;
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
                    return "onUnlockAttempt";
                case 2:
                    return "onUserRequestedUnlock";
                case 3:
                    return "onUserMayRequestUnlock";
                case 4:
                    return "onUnlockLockout";
                case 5:
                    return "onTrustTimeout";
                case 6:
                    return "onDeviceLocked";
                case 7:
                    return "onDeviceUnlocked";
                case 8:
                    return "onConfigure";
                case 9:
                    return "setCallback";
                case 10:
                    return "onEscrowTokenAdded";
                case 11:
                    return "onTokenStateReceived";
                case 12:
                    return "onEscrowTokenRemoved";
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
                            boolean _arg0 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onUnlockAttempt(_arg0);
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onUserRequestedUnlock(_arg02);
                            break;
                        case 3:
                            onUserMayRequestUnlock();
                            break;
                        case 4:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            onUnlockLockout(_arg03);
                            break;
                        case 5:
                            onTrustTimeout();
                            break;
                        case 6:
                            onDeviceLocked();
                            break;
                        case 7:
                            onDeviceUnlocked();
                            break;
                        case 8:
                            List<PersistableBundle> _arg04 = data.createTypedArrayList(PersistableBundle.CREATOR);
                            IBinder _arg1 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            onConfigure(_arg04, _arg1);
                            break;
                        case 9:
                            ITrustAgentServiceCallback _arg05 = ITrustAgentServiceCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg05);
                            break;
                        case 10:
                            byte[] _arg06 = data.createByteArray();
                            long _arg12 = data.readLong();
                            UserHandle _arg2 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            data.enforceNoDataAvail();
                            onEscrowTokenAdded(_arg06, _arg12, _arg2);
                            break;
                        case 11:
                            long _arg07 = data.readLong();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onTokenStateReceived(_arg07, _arg13);
                            break;
                        case 12:
                            long _arg08 = data.readLong();
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onEscrowTokenRemoved(_arg08, _arg14);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ITrustAgentService {
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

            @Override // android.service.trust.ITrustAgentService
            public void onUnlockAttempt(boolean successful) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(successful);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onUserRequestedUnlock(boolean dismissKeyguard) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(dismissKeyguard);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onUserMayRequestUnlock() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onUnlockLockout(int timeoutMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(timeoutMs);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onTrustTimeout() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onDeviceLocked() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onDeviceUnlocked() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onConfigure(List<PersistableBundle> options, IBinder token) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(options, 0);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void setCallback(ITrustAgentServiceCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onEscrowTokenAdded(byte[] token, long handle, UserHandle user) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(token);
                    _data.writeLong(handle);
                    _data.writeTypedObject(user, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onTokenStateReceived(long handle, int tokenState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(handle);
                    _data.writeInt(tokenState);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.trust.ITrustAgentService
            public void onEscrowTokenRemoved(long handle, boolean successful) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(handle);
                    _data.writeBoolean(successful);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 11;
        }
    }
}
