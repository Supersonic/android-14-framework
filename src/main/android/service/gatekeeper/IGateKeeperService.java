package android.service.gatekeeper;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IGateKeeperService extends IInterface {
    public static final String DESCRIPTOR = "android.service.gatekeeper.IGateKeeperService";

    void clearSecureUserId(int i) throws RemoteException;

    GateKeeperResponse enroll(int i, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    long getSecureUserId(int i) throws RemoteException;

    void reportDeviceSetupComplete() throws RemoteException;

    GateKeeperResponse verify(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    GateKeeperResponse verifyChallenge(int i, long j, byte[] bArr, byte[] bArr2) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IGateKeeperService {
        @Override // android.service.gatekeeper.IGateKeeperService
        public GateKeeperResponse enroll(int userId, byte[] currentPasswordHandle, byte[] currentPassword, byte[] desiredPassword) throws RemoteException {
            return null;
        }

        @Override // android.service.gatekeeper.IGateKeeperService
        public GateKeeperResponse verify(int userId, byte[] enrolledPasswordHandle, byte[] providedPassword) throws RemoteException {
            return null;
        }

        @Override // android.service.gatekeeper.IGateKeeperService
        public GateKeeperResponse verifyChallenge(int userId, long challenge, byte[] enrolledPasswordHandle, byte[] providedPassword) throws RemoteException {
            return null;
        }

        @Override // android.service.gatekeeper.IGateKeeperService
        public long getSecureUserId(int userId) throws RemoteException {
            return 0L;
        }

        @Override // android.service.gatekeeper.IGateKeeperService
        public void clearSecureUserId(int userId) throws RemoteException {
        }

        @Override // android.service.gatekeeper.IGateKeeperService
        public void reportDeviceSetupComplete() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IGateKeeperService {
        static final int TRANSACTION_clearSecureUserId = 5;
        static final int TRANSACTION_enroll = 1;
        static final int TRANSACTION_getSecureUserId = 4;
        static final int TRANSACTION_reportDeviceSetupComplete = 6;
        static final int TRANSACTION_verify = 2;
        static final int TRANSACTION_verifyChallenge = 3;

        public Stub() {
            attachInterface(this, "android.service.gatekeeper.IGateKeeperService");
        }

        public static IGateKeeperService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("android.service.gatekeeper.IGateKeeperService");
            if (iin != null && (iin instanceof IGateKeeperService)) {
                return (IGateKeeperService) iin;
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
                    return "enroll";
                case 2:
                    return "verify";
                case 3:
                    return "verifyChallenge";
                case 4:
                    return "getSecureUserId";
                case 5:
                    return "clearSecureUserId";
                case 6:
                    return "reportDeviceSetupComplete";
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
                data.enforceInterface("android.service.gatekeeper.IGateKeeperService");
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString("android.service.gatekeeper.IGateKeeperService");
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            byte[] _arg1 = data.createByteArray();
                            byte[] _arg2 = data.createByteArray();
                            byte[] _arg3 = data.createByteArray();
                            data.enforceNoDataAvail();
                            GateKeeperResponse _result = enroll(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            byte[] _arg12 = data.createByteArray();
                            byte[] _arg22 = data.createByteArray();
                            data.enforceNoDataAvail();
                            GateKeeperResponse _result2 = verify(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            long _arg13 = data.readLong();
                            byte[] _arg23 = data.createByteArray();
                            byte[] _arg32 = data.createByteArray();
                            data.enforceNoDataAvail();
                            GateKeeperResponse _result3 = verifyChallenge(_arg03, _arg13, _arg23, _arg32);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result4 = getSecureUserId(_arg04);
                            reply.writeNoException();
                            reply.writeLong(_result4);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            clearSecureUserId(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            reportDeviceSetupComplete();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IGateKeeperService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "android.service.gatekeeper.IGateKeeperService";
            }

            @Override // android.service.gatekeeper.IGateKeeperService
            public GateKeeperResponse enroll(int userId, byte[] currentPasswordHandle, byte[] currentPassword, byte[] desiredPassword) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.service.gatekeeper.IGateKeeperService");
                    _data.writeInt(userId);
                    _data.writeByteArray(currentPasswordHandle);
                    _data.writeByteArray(currentPassword);
                    _data.writeByteArray(desiredPassword);
                    this.mRemote.transact(1, _data, _reply, 32);
                    _reply.readException();
                    GateKeeperResponse _result = (GateKeeperResponse) _reply.readTypedObject(GateKeeperResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.gatekeeper.IGateKeeperService
            public GateKeeperResponse verify(int userId, byte[] enrolledPasswordHandle, byte[] providedPassword) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.service.gatekeeper.IGateKeeperService");
                    _data.writeInt(userId);
                    _data.writeByteArray(enrolledPasswordHandle);
                    _data.writeByteArray(providedPassword);
                    this.mRemote.transact(2, _data, _reply, 32);
                    _reply.readException();
                    GateKeeperResponse _result = (GateKeeperResponse) _reply.readTypedObject(GateKeeperResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.gatekeeper.IGateKeeperService
            public GateKeeperResponse verifyChallenge(int userId, long challenge, byte[] enrolledPasswordHandle, byte[] providedPassword) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.service.gatekeeper.IGateKeeperService");
                    _data.writeInt(userId);
                    _data.writeLong(challenge);
                    _data.writeByteArray(enrolledPasswordHandle);
                    _data.writeByteArray(providedPassword);
                    this.mRemote.transact(3, _data, _reply, 32);
                    _reply.readException();
                    GateKeeperResponse _result = (GateKeeperResponse) _reply.readTypedObject(GateKeeperResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.gatekeeper.IGateKeeperService
            public long getSecureUserId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.service.gatekeeper.IGateKeeperService");
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 32);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.gatekeeper.IGateKeeperService
            public void clearSecureUserId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.service.gatekeeper.IGateKeeperService");
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.service.gatekeeper.IGateKeeperService
            public void reportDeviceSetupComplete() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                _data.markSensitive();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.service.gatekeeper.IGateKeeperService");
                    this.mRemote.transact(6, _data, _reply, 32);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 5;
        }
    }
}
