package android.system.keystore2;

import android.hardware.security.keymint.KeyParameter;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IKeystoreSecurityLevel extends IInterface {
    public static final String DESCRIPTOR = "android$system$keystore2$IKeystoreSecurityLevel".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int KEY_FLAG_AUTH_BOUND_WITHOUT_CRYPTOGRAPHIC_LSKF_BINDING = 1;
    public static final int VERSION = 3;

    EphemeralStorageKeyResponse convertStorageKeyToEphemeral(KeyDescriptor keyDescriptor) throws RemoteException;

    CreateOperationResponse createOperation(KeyDescriptor keyDescriptor, KeyParameter[] keyParameterArr, boolean z) throws RemoteException;

    void deleteKey(KeyDescriptor keyDescriptor) throws RemoteException;

    KeyMetadata generateKey(KeyDescriptor keyDescriptor, KeyDescriptor keyDescriptor2, KeyParameter[] keyParameterArr, int i, byte[] bArr) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    KeyMetadata importKey(KeyDescriptor keyDescriptor, KeyDescriptor keyDescriptor2, KeyParameter[] keyParameterArr, int i, byte[] bArr) throws RemoteException;

    KeyMetadata importWrappedKey(KeyDescriptor keyDescriptor, KeyDescriptor keyDescriptor2, byte[] bArr, KeyParameter[] keyParameterArr, AuthenticatorSpec[] authenticatorSpecArr) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IKeystoreSecurityLevel {
        @Override // android.system.keystore2.IKeystoreSecurityLevel
        public CreateOperationResponse createOperation(KeyDescriptor key, KeyParameter[] operationParameters, boolean forced) throws RemoteException {
            return null;
        }

        @Override // android.system.keystore2.IKeystoreSecurityLevel
        public KeyMetadata generateKey(KeyDescriptor key, KeyDescriptor attestationKey, KeyParameter[] params, int flags, byte[] entropy) throws RemoteException {
            return null;
        }

        @Override // android.system.keystore2.IKeystoreSecurityLevel
        public KeyMetadata importKey(KeyDescriptor key, KeyDescriptor attestationKey, KeyParameter[] params, int flags, byte[] keyData) throws RemoteException {
            return null;
        }

        @Override // android.system.keystore2.IKeystoreSecurityLevel
        public KeyMetadata importWrappedKey(KeyDescriptor key, KeyDescriptor wrappingKey, byte[] maskingKey, KeyParameter[] params, AuthenticatorSpec[] authenticators) throws RemoteException {
            return null;
        }

        @Override // android.system.keystore2.IKeystoreSecurityLevel
        public EphemeralStorageKeyResponse convertStorageKeyToEphemeral(KeyDescriptor storageKey) throws RemoteException {
            return null;
        }

        @Override // android.system.keystore2.IKeystoreSecurityLevel
        public void deleteKey(KeyDescriptor key) throws RemoteException {
        }

        @Override // android.system.keystore2.IKeystoreSecurityLevel
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.system.keystore2.IKeystoreSecurityLevel
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IKeystoreSecurityLevel {
        static final int TRANSACTION_convertStorageKeyToEphemeral = 5;
        static final int TRANSACTION_createOperation = 1;
        static final int TRANSACTION_deleteKey = 6;
        static final int TRANSACTION_generateKey = 2;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_importKey = 3;
        static final int TRANSACTION_importWrappedKey = 4;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeystoreSecurityLevel asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IKeystoreSecurityLevel)) {
                return (IKeystoreSecurityLevel) iin;
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
                    return "createOperation";
                case 2:
                    return "generateKey";
                case 3:
                    return "importKey";
                case 4:
                    return "importWrappedKey";
                case 5:
                    return "convertStorageKeyToEphemeral";
                case 6:
                    return "deleteKey";
                case 16777214:
                    return "getInterfaceHash";
                case 16777215:
                    return "getInterfaceVersion";
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
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            KeyDescriptor _arg0 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            KeyParameter[] _arg1 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            CreateOperationResponse _result = createOperation(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            KeyDescriptor _arg02 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            KeyDescriptor _arg12 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            KeyParameter[] _arg22 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            int _arg3 = data.readInt();
                            byte[] _arg4 = data.createByteArray();
                            data.enforceNoDataAvail();
                            KeyMetadata _result2 = generateKey(_arg02, _arg12, _arg22, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            KeyDescriptor _arg03 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            KeyDescriptor _arg13 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            KeyParameter[] _arg23 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            int _arg32 = data.readInt();
                            byte[] _arg42 = data.createByteArray();
                            data.enforceNoDataAvail();
                            KeyMetadata _result3 = importKey(_arg03, _arg13, _arg23, _arg32, _arg42);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            KeyDescriptor _arg04 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            KeyDescriptor _arg14 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            byte[] _arg24 = data.createByteArray();
                            KeyParameter[] _arg33 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            AuthenticatorSpec[] _arg43 = (AuthenticatorSpec[]) data.createTypedArray(AuthenticatorSpec.CREATOR);
                            data.enforceNoDataAvail();
                            KeyMetadata _result4 = importWrappedKey(_arg04, _arg14, _arg24, _arg33, _arg43);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            KeyDescriptor _arg05 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            EphemeralStorageKeyResponse _result5 = convertStorageKeyToEphemeral(_arg05);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 6:
                            KeyDescriptor _arg06 = (KeyDescriptor) data.readTypedObject(KeyDescriptor.CREATOR);
                            data.enforceNoDataAvail();
                            deleteKey(_arg06);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IKeystoreSecurityLevel {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

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

            @Override // android.system.keystore2.IKeystoreSecurityLevel
            public CreateOperationResponse createOperation(KeyDescriptor key, KeyParameter[] operationParameters, boolean forced) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(key, 0);
                    _data.writeTypedArray(operationParameters, 0);
                    _data.writeBoolean(forced);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method createOperation is unimplemented.");
                    }
                    _reply.readException();
                    CreateOperationResponse _result = (CreateOperationResponse) _reply.readTypedObject(CreateOperationResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.keystore2.IKeystoreSecurityLevel
            public KeyMetadata generateKey(KeyDescriptor key, KeyDescriptor attestationKey, KeyParameter[] params, int flags, byte[] entropy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(key, 0);
                    _data.writeTypedObject(attestationKey, 0);
                    _data.writeTypedArray(params, 0);
                    _data.writeInt(flags);
                    _data.writeByteArray(entropy);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method generateKey is unimplemented.");
                    }
                    _reply.readException();
                    KeyMetadata _result = (KeyMetadata) _reply.readTypedObject(KeyMetadata.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.keystore2.IKeystoreSecurityLevel
            public KeyMetadata importKey(KeyDescriptor key, KeyDescriptor attestationKey, KeyParameter[] params, int flags, byte[] keyData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(key, 0);
                    _data.writeTypedObject(attestationKey, 0);
                    _data.writeTypedArray(params, 0);
                    _data.writeInt(flags);
                    _data.writeByteArray(keyData);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method importKey is unimplemented.");
                    }
                    _reply.readException();
                    KeyMetadata _result = (KeyMetadata) _reply.readTypedObject(KeyMetadata.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.keystore2.IKeystoreSecurityLevel
            public KeyMetadata importWrappedKey(KeyDescriptor key, KeyDescriptor wrappingKey, byte[] maskingKey, KeyParameter[] params, AuthenticatorSpec[] authenticators) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(key, 0);
                    _data.writeTypedObject(wrappingKey, 0);
                    _data.writeByteArray(maskingKey);
                    _data.writeTypedArray(params, 0);
                    _data.writeTypedArray(authenticators, 0);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method importWrappedKey is unimplemented.");
                    }
                    _reply.readException();
                    KeyMetadata _result = (KeyMetadata) _reply.readTypedObject(KeyMetadata.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.keystore2.IKeystoreSecurityLevel
            public EphemeralStorageKeyResponse convertStorageKeyToEphemeral(KeyDescriptor storageKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(storageKey, 0);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method convertStorageKeyToEphemeral is unimplemented.");
                    }
                    _reply.readException();
                    EphemeralStorageKeyResponse _result = (EphemeralStorageKeyResponse) _reply.readTypedObject(EphemeralStorageKeyResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.keystore2.IKeystoreSecurityLevel
            public void deleteKey(KeyDescriptor key) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(key, 0);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method deleteKey is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.system.keystore2.IKeystoreSecurityLevel
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.system.keystore2.IKeystoreSecurityLevel
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 16777214;
        }
    }
}
