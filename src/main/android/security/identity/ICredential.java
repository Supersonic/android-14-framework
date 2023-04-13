package android.security.identity;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.security.identity.IWritableCredential;
/* loaded from: classes3.dex */
public interface ICredential extends IInterface {
    public static final String DESCRIPTOR = "android.security.identity.ICredential";
    public static final int STATUS_NOT_IN_REQUEST_MESSAGE = 3;
    public static final int STATUS_NOT_REQUESTED = 2;
    public static final int STATUS_NO_ACCESS_CONTROL_PROFILES = 6;
    public static final int STATUS_NO_SUCH_ENTRY = 1;
    public static final int STATUS_OK = 0;
    public static final int STATUS_READER_AUTHENTICATION_FAILED = 5;
    public static final int STATUS_USER_AUTHENTICATION_FAILED = 4;

    byte[] createEphemeralKeyPair() throws RemoteException;

    byte[] deleteCredential() throws RemoteException;

    byte[] deleteWithChallenge(byte[] bArr) throws RemoteException;

    AuthKeyParcel[] getAuthKeysNeedingCertification() throws RemoteException;

    long[] getAuthenticationDataExpirations() throws RemoteException;

    int[] getAuthenticationDataUsageCount() throws RemoteException;

    byte[] getCredentialKeyCertificateChain() throws RemoteException;

    GetEntriesResultParcel getEntries(byte[] bArr, RequestNamespaceParcel[] requestNamespaceParcelArr, byte[] bArr2, byte[] bArr3, boolean z, boolean z2, boolean z3) throws RemoteException;

    byte[] proveOwnership(byte[] bArr) throws RemoteException;

    long selectAuthKey(boolean z, boolean z2, boolean z3) throws RemoteException;

    void setAvailableAuthenticationKeys(int i, int i2, long j) throws RemoteException;

    void setReaderEphemeralPublicKey(byte[] bArr) throws RemoteException;

    void storeStaticAuthenticationData(AuthKeyParcel authKeyParcel, byte[] bArr) throws RemoteException;

    void storeStaticAuthenticationDataWithExpiration(AuthKeyParcel authKeyParcel, long j, byte[] bArr) throws RemoteException;

    IWritableCredential update() throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements ICredential {
        @Override // android.security.identity.ICredential
        public byte[] createEphemeralKeyPair() throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public void setReaderEphemeralPublicKey(byte[] publicKey) throws RemoteException {
        }

        @Override // android.security.identity.ICredential
        public byte[] deleteCredential() throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public byte[] deleteWithChallenge(byte[] challenge) throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public byte[] proveOwnership(byte[] challenge) throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public byte[] getCredentialKeyCertificateChain() throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public long selectAuthKey(boolean allowUsingExhaustedKeys, boolean allowUsingExpiredKeys, boolean incrementUsageCount) throws RemoteException {
            return 0L;
        }

        @Override // android.security.identity.ICredential
        public GetEntriesResultParcel getEntries(byte[] requestMessage, RequestNamespaceParcel[] requestNamespaces, byte[] sessionTranscript, byte[] readerSignature, boolean allowUsingExhaustedKeys, boolean allowUsingExpiredKeys, boolean incrementUsageCount) throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public void setAvailableAuthenticationKeys(int keyCount, int maxUsesPerKey, long minValidTimeMillis) throws RemoteException {
        }

        @Override // android.security.identity.ICredential
        public AuthKeyParcel[] getAuthKeysNeedingCertification() throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public void storeStaticAuthenticationData(AuthKeyParcel authenticationKey, byte[] staticAuthData) throws RemoteException {
        }

        @Override // android.security.identity.ICredential
        public void storeStaticAuthenticationDataWithExpiration(AuthKeyParcel authenticationKey, long expirationDateMillisSinceEpoch, byte[] staticAuthData) throws RemoteException {
        }

        @Override // android.security.identity.ICredential
        public int[] getAuthenticationDataUsageCount() throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public long[] getAuthenticationDataExpirations() throws RemoteException {
            return null;
        }

        @Override // android.security.identity.ICredential
        public IWritableCredential update() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements ICredential {
        static final int TRANSACTION_createEphemeralKeyPair = 1;
        static final int TRANSACTION_deleteCredential = 3;
        static final int TRANSACTION_deleteWithChallenge = 4;
        static final int TRANSACTION_getAuthKeysNeedingCertification = 10;
        static final int TRANSACTION_getAuthenticationDataExpirations = 14;
        static final int TRANSACTION_getAuthenticationDataUsageCount = 13;
        static final int TRANSACTION_getCredentialKeyCertificateChain = 6;
        static final int TRANSACTION_getEntries = 8;
        static final int TRANSACTION_proveOwnership = 5;
        static final int TRANSACTION_selectAuthKey = 7;
        static final int TRANSACTION_setAvailableAuthenticationKeys = 9;
        static final int TRANSACTION_setReaderEphemeralPublicKey = 2;
        static final int TRANSACTION_storeStaticAuthenticationData = 11;
        static final int TRANSACTION_storeStaticAuthenticationDataWithExpiration = 12;
        static final int TRANSACTION_update = 15;

        public Stub() {
            attachInterface(this, ICredential.DESCRIPTOR);
        }

        public static ICredential asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ICredential.DESCRIPTOR);
            if (iin != null && (iin instanceof ICredential)) {
                return (ICredential) iin;
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
                    return "createEphemeralKeyPair";
                case 2:
                    return "setReaderEphemeralPublicKey";
                case 3:
                    return "deleteCredential";
                case 4:
                    return "deleteWithChallenge";
                case 5:
                    return "proveOwnership";
                case 6:
                    return "getCredentialKeyCertificateChain";
                case 7:
                    return "selectAuthKey";
                case 8:
                    return "getEntries";
                case 9:
                    return "setAvailableAuthenticationKeys";
                case 10:
                    return "getAuthKeysNeedingCertification";
                case 11:
                    return "storeStaticAuthenticationData";
                case 12:
                    return "storeStaticAuthenticationDataWithExpiration";
                case 13:
                    return "getAuthenticationDataUsageCount";
                case 14:
                    return "getAuthenticationDataExpirations";
                case 15:
                    return "update";
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
                data.enforceInterface(ICredential.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ICredential.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            byte[] _result = createEphemeralKeyPair();
                            reply.writeNoException();
                            reply.writeByteArray(_result);
                            break;
                        case 2:
                            byte[] _arg0 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setReaderEphemeralPublicKey(_arg0);
                            reply.writeNoException();
                            break;
                        case 3:
                            byte[] _result2 = deleteCredential();
                            reply.writeNoException();
                            reply.writeByteArray(_result2);
                            break;
                        case 4:
                            byte[] _arg02 = data.createByteArray();
                            data.enforceNoDataAvail();
                            byte[] _result3 = deleteWithChallenge(_arg02);
                            reply.writeNoException();
                            reply.writeByteArray(_result3);
                            break;
                        case 5:
                            byte[] _arg03 = data.createByteArray();
                            data.enforceNoDataAvail();
                            byte[] _result4 = proveOwnership(_arg03);
                            reply.writeNoException();
                            reply.writeByteArray(_result4);
                            break;
                        case 6:
                            byte[] _result5 = getCredentialKeyCertificateChain();
                            reply.writeNoException();
                            reply.writeByteArray(_result5);
                            break;
                        case 7:
                            boolean _arg04 = data.readBoolean();
                            boolean _arg1 = data.readBoolean();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            long _result6 = selectAuthKey(_arg04, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeLong(_result6);
                            break;
                        case 8:
                            byte[] _arg05 = data.createByteArray();
                            RequestNamespaceParcel[] _arg12 = (RequestNamespaceParcel[]) data.createTypedArray(RequestNamespaceParcel.CREATOR);
                            byte[] _arg22 = data.createByteArray();
                            byte[] _arg3 = data.createByteArray();
                            boolean _arg4 = data.readBoolean();
                            boolean _arg5 = data.readBoolean();
                            boolean _arg6 = data.readBoolean();
                            data.enforceNoDataAvail();
                            GetEntriesResultParcel _result7 = getEntries(_arg05, _arg12, _arg22, _arg3, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 9:
                            int _arg06 = data.readInt();
                            int _arg13 = data.readInt();
                            long _arg23 = data.readLong();
                            data.enforceNoDataAvail();
                            setAvailableAuthenticationKeys(_arg06, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 10:
                            AuthKeyParcel[] _result8 = getAuthKeysNeedingCertification();
                            reply.writeNoException();
                            reply.writeTypedArray(_result8, 1);
                            break;
                        case 11:
                            AuthKeyParcel _arg07 = (AuthKeyParcel) data.readTypedObject(AuthKeyParcel.CREATOR);
                            byte[] _arg14 = data.createByteArray();
                            data.enforceNoDataAvail();
                            storeStaticAuthenticationData(_arg07, _arg14);
                            reply.writeNoException();
                            break;
                        case 12:
                            AuthKeyParcel _arg08 = (AuthKeyParcel) data.readTypedObject(AuthKeyParcel.CREATOR);
                            long _arg15 = data.readLong();
                            byte[] _arg24 = data.createByteArray();
                            data.enforceNoDataAvail();
                            storeStaticAuthenticationDataWithExpiration(_arg08, _arg15, _arg24);
                            reply.writeNoException();
                            break;
                        case 13:
                            int[] _result9 = getAuthenticationDataUsageCount();
                            reply.writeNoException();
                            reply.writeIntArray(_result9);
                            break;
                        case 14:
                            long[] _result10 = getAuthenticationDataExpirations();
                            reply.writeNoException();
                            reply.writeLongArray(_result10);
                            break;
                        case 15:
                            IWritableCredential _result11 = update();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result11);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements ICredential {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ICredential.DESCRIPTOR;
            }

            @Override // android.security.identity.ICredential
            public byte[] createEphemeralKeyPair() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public void setReaderEphemeralPublicKey(byte[] publicKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    _data.writeByteArray(publicKey);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public byte[] deleteCredential() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public byte[] deleteWithChallenge(byte[] challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    _data.writeByteArray(challenge);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public byte[] proveOwnership(byte[] challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    _data.writeByteArray(challenge);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public byte[] getCredentialKeyCertificateChain() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public long selectAuthKey(boolean allowUsingExhaustedKeys, boolean allowUsingExpiredKeys, boolean incrementUsageCount) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    _data.writeBoolean(allowUsingExhaustedKeys);
                    _data.writeBoolean(allowUsingExpiredKeys);
                    _data.writeBoolean(incrementUsageCount);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public GetEntriesResultParcel getEntries(byte[] requestMessage, RequestNamespaceParcel[] requestNamespaces, byte[] sessionTranscript, byte[] readerSignature, boolean allowUsingExhaustedKeys, boolean allowUsingExpiredKeys, boolean incrementUsageCount) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    _data.writeByteArray(requestMessage);
                    _data.writeTypedArray(requestNamespaces, 0);
                    _data.writeByteArray(sessionTranscript);
                    _data.writeByteArray(readerSignature);
                    _data.writeBoolean(allowUsingExhaustedKeys);
                    _data.writeBoolean(allowUsingExpiredKeys);
                    _data.writeBoolean(incrementUsageCount);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    GetEntriesResultParcel _result = (GetEntriesResultParcel) _reply.readTypedObject(GetEntriesResultParcel.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public void setAvailableAuthenticationKeys(int keyCount, int maxUsesPerKey, long minValidTimeMillis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    _data.writeInt(keyCount);
                    _data.writeInt(maxUsesPerKey);
                    _data.writeLong(minValidTimeMillis);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public AuthKeyParcel[] getAuthKeysNeedingCertification() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    AuthKeyParcel[] _result = (AuthKeyParcel[]) _reply.createTypedArray(AuthKeyParcel.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public void storeStaticAuthenticationData(AuthKeyParcel authenticationKey, byte[] staticAuthData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    _data.writeTypedObject(authenticationKey, 0);
                    _data.writeByteArray(staticAuthData);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public void storeStaticAuthenticationDataWithExpiration(AuthKeyParcel authenticationKey, long expirationDateMillisSinceEpoch, byte[] staticAuthData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    _data.writeTypedObject(authenticationKey, 0);
                    _data.writeLong(expirationDateMillisSinceEpoch);
                    _data.writeByteArray(staticAuthData);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public int[] getAuthenticationDataUsageCount() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public long[] getAuthenticationDataExpirations() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    long[] _result = _reply.createLongArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.security.identity.ICredential
            public IWritableCredential update() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ICredential.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    IWritableCredential _result = IWritableCredential.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 14;
        }
    }
}
