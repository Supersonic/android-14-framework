package android.security.authorization;

import android.hardware.security.keymint.HardwareAuthToken;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IKeystoreAuthorization extends IInterface {
    public static final String DESCRIPTOR = "android$security$authorization$IKeystoreAuthorization".replace('$', '.');

    void addAuthToken(HardwareAuthToken hardwareAuthToken) throws RemoteException;

    AuthorizationTokens getAuthTokensForCredStore(long j, long j2, long j3) throws RemoteException;

    void onLockScreenEvent(int i, int i2, byte[] bArr, long[] jArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IKeystoreAuthorization {
        @Override // android.security.authorization.IKeystoreAuthorization
        public void addAuthToken(HardwareAuthToken authToken) throws RemoteException {
        }

        @Override // android.security.authorization.IKeystoreAuthorization
        public void onLockScreenEvent(int lockScreenEvent, int userId, byte[] password, long[] unlockingSids) throws RemoteException {
        }

        @Override // android.security.authorization.IKeystoreAuthorization
        public AuthorizationTokens getAuthTokensForCredStore(long challenge, long secureUserId, long authTokenMaxAgeMillis) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IKeystoreAuthorization {
        static final int TRANSACTION_addAuthToken = 1;
        static final int TRANSACTION_getAuthTokensForCredStore = 3;
        static final int TRANSACTION_onLockScreenEvent = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeystoreAuthorization asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IKeystoreAuthorization)) {
                return (IKeystoreAuthorization) iin;
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
                    return "addAuthToken";
                case 2:
                    return "onLockScreenEvent";
                case 3:
                    return "getAuthTokensForCredStore";
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
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            HardwareAuthToken _arg0 = (HardwareAuthToken) data.readTypedObject(HardwareAuthToken.CREATOR);
                            data.enforceNoDataAvail();
                            addAuthToken(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            byte[] _arg2 = data.createByteArray();
                            long[] _arg3 = data.createLongArray();
                            data.enforceNoDataAvail();
                            onLockScreenEvent(_arg02, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 3:
                            long _arg03 = data.readLong();
                            long _arg12 = data.readLong();
                            long _arg22 = data.readLong();
                            data.enforceNoDataAvail();
                            AuthorizationTokens _result = getAuthTokensForCredStore(_arg03, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IKeystoreAuthorization {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.security.authorization.IKeystoreAuthorization
            public void addAuthToken(HardwareAuthToken authToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(authToken, 0);
                    this.mRemote.transact(1, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.authorization.IKeystoreAuthorization
            public void onLockScreenEvent(int lockScreenEvent, int userId, byte[] password, long[] unlockingSids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(lockScreenEvent);
                    _data.writeInt(userId);
                    _data.writeByteArray(password);
                    _data.writeLongArray(unlockingSids);
                    this.mRemote.transact(2, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.authorization.IKeystoreAuthorization
            public AuthorizationTokens getAuthTokensForCredStore(long challenge, long secureUserId, long authTokenMaxAgeMillis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(challenge);
                    _data.writeLong(secureUserId);
                    _data.writeLong(authTokenMaxAgeMillis);
                    this.mRemote.transact(3, _data, _reply, 32);
                    _reply.readException();
                    AuthorizationTokens _result = (AuthorizationTokens) _reply.readTypedObject(AuthorizationTokens.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
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
