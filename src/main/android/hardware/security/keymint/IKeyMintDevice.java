package android.hardware.security.keymint;

import android.hardware.security.secureclock.TimeStampToken;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IKeyMintDevice extends IInterface {
    public static final int AUTH_TOKEN_MAC_LENGTH = 32;
    public static final String DESCRIPTOR = "android$hardware$security$keymint$IKeyMintDevice".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 3;

    void addRngEntropy(byte[] bArr) throws RemoteException;

    BeginResult begin(int i, byte[] bArr, KeyParameter[] keyParameterArr, HardwareAuthToken hardwareAuthToken) throws RemoteException;

    byte[] convertStorageKeyToEphemeral(byte[] bArr) throws RemoteException;

    void deleteAllKeys() throws RemoteException;

    void deleteKey(byte[] bArr) throws RemoteException;

    void destroyAttestationIds() throws RemoteException;

    void deviceLocked(boolean z, TimeStampToken timeStampToken) throws RemoteException;

    void earlyBootEnded() throws RemoteException;

    KeyCreationResult generateKey(KeyParameter[] keyParameterArr, AttestationKey attestationKey) throws RemoteException;

    KeyMintHardwareInfo getHardwareInfo() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    KeyCharacteristics[] getKeyCharacteristics(byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    byte[] getRootOfTrust(byte[] bArr) throws RemoteException;

    byte[] getRootOfTrustChallenge() throws RemoteException;

    KeyCreationResult importKey(KeyParameter[] keyParameterArr, int i, byte[] bArr, AttestationKey attestationKey) throws RemoteException;

    KeyCreationResult importWrappedKey(byte[] bArr, byte[] bArr2, byte[] bArr3, KeyParameter[] keyParameterArr, long j, long j2) throws RemoteException;

    void sendRootOfTrust(byte[] bArr) throws RemoteException;

    byte[] upgradeKey(byte[] bArr, KeyParameter[] keyParameterArr) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IKeyMintDevice {
        @Override // android.hardware.security.keymint.IKeyMintDevice
        public KeyMintHardwareInfo getHardwareInfo() throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public void addRngEntropy(byte[] data) throws RemoteException {
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public KeyCreationResult generateKey(KeyParameter[] keyParams, AttestationKey attestationKey) throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public KeyCreationResult importKey(KeyParameter[] keyParams, int keyFormat, byte[] keyData, AttestationKey attestationKey) throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public KeyCreationResult importWrappedKey(byte[] wrappedKeyData, byte[] wrappingKeyBlob, byte[] maskingKey, KeyParameter[] unwrappingParams, long passwordSid, long biometricSid) throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public byte[] upgradeKey(byte[] keyBlobToUpgrade, KeyParameter[] upgradeParams) throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public void deleteKey(byte[] keyBlob) throws RemoteException {
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public void deleteAllKeys() throws RemoteException {
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public void destroyAttestationIds() throws RemoteException {
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public BeginResult begin(int purpose, byte[] keyBlob, KeyParameter[] params, HardwareAuthToken authToken) throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public void deviceLocked(boolean passwordOnly, TimeStampToken timestampToken) throws RemoteException {
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public void earlyBootEnded() throws RemoteException {
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public byte[] convertStorageKeyToEphemeral(byte[] storageKeyBlob) throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public KeyCharacteristics[] getKeyCharacteristics(byte[] keyBlob, byte[] appId, byte[] appData) throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public byte[] getRootOfTrustChallenge() throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public byte[] getRootOfTrust(byte[] challenge) throws RemoteException {
            return null;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public void sendRootOfTrust(byte[] rootOfTrust) throws RemoteException {
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.security.keymint.IKeyMintDevice
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IKeyMintDevice {
        static final int TRANSACTION_addRngEntropy = 2;
        static final int TRANSACTION_begin = 10;
        static final int TRANSACTION_convertStorageKeyToEphemeral = 13;
        static final int TRANSACTION_deleteAllKeys = 8;
        static final int TRANSACTION_deleteKey = 7;
        static final int TRANSACTION_destroyAttestationIds = 9;
        static final int TRANSACTION_deviceLocked = 11;
        static final int TRANSACTION_earlyBootEnded = 12;
        static final int TRANSACTION_generateKey = 3;
        static final int TRANSACTION_getHardwareInfo = 1;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getKeyCharacteristics = 14;
        static final int TRANSACTION_getRootOfTrust = 16;
        static final int TRANSACTION_getRootOfTrustChallenge = 15;
        static final int TRANSACTION_importKey = 4;
        static final int TRANSACTION_importWrappedKey = 5;
        static final int TRANSACTION_sendRootOfTrust = 17;
        static final int TRANSACTION_upgradeKey = 6;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyMintDevice asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IKeyMintDevice)) {
                return (IKeyMintDevice) iin;
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
                    return "getHardwareInfo";
                case 2:
                    return "addRngEntropy";
                case 3:
                    return "generateKey";
                case 4:
                    return "importKey";
                case 5:
                    return "importWrappedKey";
                case 6:
                    return "upgradeKey";
                case 7:
                    return "deleteKey";
                case 8:
                    return "deleteAllKeys";
                case 9:
                    return "destroyAttestationIds";
                case 10:
                    return "begin";
                case 11:
                    return "deviceLocked";
                case 12:
                    return "earlyBootEnded";
                case 13:
                    return "convertStorageKeyToEphemeral";
                case 14:
                    return "getKeyCharacteristics";
                case 15:
                    return "getRootOfTrustChallenge";
                case 16:
                    return "getRootOfTrust";
                case 17:
                    return "sendRootOfTrust";
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
                            KeyMintHardwareInfo _result = getHardwareInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            byte[] _arg0 = data.createByteArray();
                            data.enforceNoDataAvail();
                            addRngEntropy(_arg0);
                            reply.writeNoException();
                            break;
                        case 3:
                            KeyParameter[] _arg02 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            AttestationKey _arg1 = (AttestationKey) data.readTypedObject(AttestationKey.CREATOR);
                            data.enforceNoDataAvail();
                            KeyCreationResult _result2 = generateKey(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 4:
                            KeyParameter[] _arg03 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            int _arg12 = data.readInt();
                            byte[] _arg2 = data.createByteArray();
                            AttestationKey _arg3 = (AttestationKey) data.readTypedObject(AttestationKey.CREATOR);
                            data.enforceNoDataAvail();
                            KeyCreationResult _result3 = importKey(_arg03, _arg12, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 5:
                            byte[] _arg04 = data.createByteArray();
                            byte[] _arg13 = data.createByteArray();
                            byte[] _arg22 = data.createByteArray();
                            KeyParameter[] _arg32 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            long _arg4 = data.readLong();
                            long _arg5 = data.readLong();
                            data.enforceNoDataAvail();
                            KeyCreationResult _result4 = importWrappedKey(_arg04, _arg13, _arg22, _arg32, _arg4, _arg5);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 6:
                            byte[] _arg05 = data.createByteArray();
                            KeyParameter[] _arg14 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            data.enforceNoDataAvail();
                            byte[] _result5 = upgradeKey(_arg05, _arg14);
                            reply.writeNoException();
                            reply.writeByteArray(_result5);
                            break;
                        case 7:
                            byte[] _arg06 = data.createByteArray();
                            data.enforceNoDataAvail();
                            deleteKey(_arg06);
                            reply.writeNoException();
                            break;
                        case 8:
                            deleteAllKeys();
                            reply.writeNoException();
                            break;
                        case 9:
                            destroyAttestationIds();
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg07 = data.readInt();
                            byte[] _arg15 = data.createByteArray();
                            KeyParameter[] _arg23 = (KeyParameter[]) data.createTypedArray(KeyParameter.CREATOR);
                            HardwareAuthToken _arg33 = (HardwareAuthToken) data.readTypedObject(HardwareAuthToken.CREATOR);
                            data.enforceNoDataAvail();
                            BeginResult _result6 = begin(_arg07, _arg15, _arg23, _arg33);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 11:
                            boolean _arg08 = data.readBoolean();
                            TimeStampToken _arg16 = (TimeStampToken) data.readTypedObject(TimeStampToken.CREATOR);
                            data.enforceNoDataAvail();
                            deviceLocked(_arg08, _arg16);
                            reply.writeNoException();
                            break;
                        case 12:
                            earlyBootEnded();
                            reply.writeNoException();
                            break;
                        case 13:
                            byte[] _arg09 = data.createByteArray();
                            data.enforceNoDataAvail();
                            byte[] _result7 = convertStorageKeyToEphemeral(_arg09);
                            reply.writeNoException();
                            reply.writeByteArray(_result7);
                            break;
                        case 14:
                            byte[] _arg010 = data.createByteArray();
                            byte[] _arg17 = data.createByteArray();
                            byte[] _arg24 = data.createByteArray();
                            data.enforceNoDataAvail();
                            KeyCharacteristics[] _result8 = getKeyCharacteristics(_arg010, _arg17, _arg24);
                            reply.writeNoException();
                            reply.writeTypedArray(_result8, 1);
                            break;
                        case 15:
                            byte[] _result9 = getRootOfTrustChallenge();
                            reply.writeNoException();
                            reply.writeFixedArray(_result9, 1, 16);
                            break;
                        case 16:
                            byte[] _arg011 = (byte[]) data.createFixedArray(byte[].class, 16);
                            data.enforceNoDataAvail();
                            byte[] _result10 = getRootOfTrust(_arg011);
                            reply.writeNoException();
                            reply.writeByteArray(_result10);
                            break;
                        case 17:
                            byte[] _arg012 = data.createByteArray();
                            data.enforceNoDataAvail();
                            sendRootOfTrust(_arg012);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IKeyMintDevice {
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

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public KeyMintHardwareInfo getHardwareInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method getHardwareInfo is unimplemented.");
                    }
                    _reply.readException();
                    KeyMintHardwareInfo _result = (KeyMintHardwareInfo) _reply.readTypedObject(KeyMintHardwareInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public void addRngEntropy(byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method addRngEntropy is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public KeyCreationResult generateKey(KeyParameter[] keyParams, AttestationKey attestationKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedArray(keyParams, 0);
                    _data.writeTypedObject(attestationKey, 0);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method generateKey is unimplemented.");
                    }
                    _reply.readException();
                    KeyCreationResult _result = (KeyCreationResult) _reply.readTypedObject(KeyCreationResult.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public KeyCreationResult importKey(KeyParameter[] keyParams, int keyFormat, byte[] keyData, AttestationKey attestationKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedArray(keyParams, 0);
                    _data.writeInt(keyFormat);
                    _data.writeByteArray(keyData);
                    _data.writeTypedObject(attestationKey, 0);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method importKey is unimplemented.");
                    }
                    _reply.readException();
                    KeyCreationResult _result = (KeyCreationResult) _reply.readTypedObject(KeyCreationResult.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public KeyCreationResult importWrappedKey(byte[] wrappedKeyData, byte[] wrappingKeyBlob, byte[] maskingKey, KeyParameter[] unwrappingParams, long passwordSid, long biometricSid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(wrappedKeyData);
                    _data.writeByteArray(wrappingKeyBlob);
                    _data.writeByteArray(maskingKey);
                    _data.writeTypedArray(unwrappingParams, 0);
                    _data.writeLong(passwordSid);
                    _data.writeLong(biometricSid);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method importWrappedKey is unimplemented.");
                    }
                    _reply.readException();
                    KeyCreationResult _result = (KeyCreationResult) _reply.readTypedObject(KeyCreationResult.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public byte[] upgradeKey(byte[] keyBlobToUpgrade, KeyParameter[] upgradeParams) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(keyBlobToUpgrade);
                    _data.writeTypedArray(upgradeParams, 0);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method upgradeKey is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public void deleteKey(byte[] keyBlob) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(keyBlob);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method deleteKey is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public void deleteAllKeys() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method deleteAllKeys is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public void destroyAttestationIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method destroyAttestationIds is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public BeginResult begin(int purpose, byte[] keyBlob, KeyParameter[] params, HardwareAuthToken authToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(purpose);
                    _data.writeByteArray(keyBlob);
                    _data.writeTypedArray(params, 0);
                    _data.writeTypedObject(authToken, 0);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method begin is unimplemented.");
                    }
                    _reply.readException();
                    BeginResult _result = (BeginResult) _reply.readTypedObject(BeginResult.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public void deviceLocked(boolean passwordOnly, TimeStampToken timestampToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeBoolean(passwordOnly);
                    _data.writeTypedObject(timestampToken, 0);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method deviceLocked is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public void earlyBootEnded() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method earlyBootEnded is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public byte[] convertStorageKeyToEphemeral(byte[] storageKeyBlob) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(storageKeyBlob);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method convertStorageKeyToEphemeral is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public KeyCharacteristics[] getKeyCharacteristics(byte[] keyBlob, byte[] appId, byte[] appData) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(keyBlob);
                    _data.writeByteArray(appId);
                    _data.writeByteArray(appData);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method getKeyCharacteristics is unimplemented.");
                    }
                    _reply.readException();
                    KeyCharacteristics[] _result = (KeyCharacteristics[]) _reply.createTypedArray(KeyCharacteristics.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public byte[] getRootOfTrustChallenge() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method getRootOfTrustChallenge is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = (byte[]) _reply.createFixedArray(byte[].class, 16);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public byte[] getRootOfTrust(byte[] challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeFixedArray(challenge, 0, 16);
                    boolean _status = this.mRemote.transact(16, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method getRootOfTrust is unimplemented.");
                    }
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
            public void sendRootOfTrust(byte[] rootOfTrust) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByteArray(rootOfTrust);
                    boolean _status = this.mRemote.transact(17, _data, _reply, 32);
                    if (!_status) {
                        throw new RemoteException("Method sendRootOfTrust is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.security.keymint.IKeyMintDevice
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

            @Override // android.hardware.security.keymint.IKeyMintDevice
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
