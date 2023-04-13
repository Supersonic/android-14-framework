package android.hardware.biometrics.fingerprint;

import android.hardware.keymaster.HardwareAuthToken;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ISessionCallback extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$biometrics$fingerprint$ISessionCallback".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 3;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void onAcquired(byte b, int i) throws RemoteException;

    void onAuthenticationFailed() throws RemoteException;

    void onAuthenticationSucceeded(int i, HardwareAuthToken hardwareAuthToken) throws RemoteException;

    void onAuthenticatorIdInvalidated(long j) throws RemoteException;

    void onAuthenticatorIdRetrieved(long j) throws RemoteException;

    void onChallengeGenerated(long j) throws RemoteException;

    void onChallengeRevoked(long j) throws RemoteException;

    void onEnrollmentProgress(int i, int i2) throws RemoteException;

    void onEnrollmentsEnumerated(int[] iArr) throws RemoteException;

    void onEnrollmentsRemoved(int[] iArr) throws RemoteException;

    void onError(byte b, int i) throws RemoteException;

    void onInteractionDetected() throws RemoteException;

    void onLockoutCleared() throws RemoteException;

    void onLockoutPermanent() throws RemoteException;

    void onLockoutTimed(long j) throws RemoteException;

    void onSessionClosed() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ISessionCallback {
        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onChallengeGenerated(long challenge) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onChallengeRevoked(long challenge) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onAcquired(byte info, int vendorCode) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onError(byte error, int vendorCode) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onEnrollmentProgress(int enrollmentId, int remaining) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onAuthenticationSucceeded(int enrollmentId, HardwareAuthToken hat) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onAuthenticationFailed() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onLockoutTimed(long durationMillis) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onLockoutPermanent() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onLockoutCleared() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onInteractionDetected() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onEnrollmentsEnumerated(int[] enrollmentIds) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onEnrollmentsRemoved(int[] enrollmentIds) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onAuthenticatorIdRetrieved(long authenticatorId) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onAuthenticatorIdInvalidated(long newAuthenticatorId) throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public void onSessionClosed() throws RemoteException {
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.biometrics.fingerprint.ISessionCallback
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ISessionCallback {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_onAcquired = 3;
        static final int TRANSACTION_onAuthenticationFailed = 7;
        static final int TRANSACTION_onAuthenticationSucceeded = 6;
        static final int TRANSACTION_onAuthenticatorIdInvalidated = 15;
        static final int TRANSACTION_onAuthenticatorIdRetrieved = 14;
        static final int TRANSACTION_onChallengeGenerated = 1;
        static final int TRANSACTION_onChallengeRevoked = 2;
        static final int TRANSACTION_onEnrollmentProgress = 5;
        static final int TRANSACTION_onEnrollmentsEnumerated = 12;
        static final int TRANSACTION_onEnrollmentsRemoved = 13;
        static final int TRANSACTION_onError = 4;
        static final int TRANSACTION_onInteractionDetected = 11;
        static final int TRANSACTION_onLockoutCleared = 10;
        static final int TRANSACTION_onLockoutPermanent = 9;
        static final int TRANSACTION_onLockoutTimed = 8;
        static final int TRANSACTION_onSessionClosed = 16;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ISessionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISessionCallback)) {
                return (ISessionCallback) iin;
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
                    return "onChallengeGenerated";
                case 2:
                    return "onChallengeRevoked";
                case 3:
                    return "onAcquired";
                case 4:
                    return "onError";
                case 5:
                    return "onEnrollmentProgress";
                case 6:
                    return "onAuthenticationSucceeded";
                case 7:
                    return "onAuthenticationFailed";
                case 8:
                    return "onLockoutTimed";
                case 9:
                    return "onLockoutPermanent";
                case 10:
                    return "onLockoutCleared";
                case 11:
                    return "onInteractionDetected";
                case 12:
                    return "onEnrollmentsEnumerated";
                case 13:
                    return "onEnrollmentsRemoved";
                case 14:
                    return "onAuthenticatorIdRetrieved";
                case 15:
                    return "onAuthenticatorIdInvalidated";
                case 16:
                    return "onSessionClosed";
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
                            long _arg0 = data.readLong();
                            data.enforceNoDataAvail();
                            onChallengeGenerated(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            long _arg02 = data.readLong();
                            data.enforceNoDataAvail();
                            onChallengeRevoked(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            byte _arg03 = data.readByte();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onAcquired(_arg03, _arg1);
                            reply.writeNoException();
                            break;
                        case 4:
                            byte _arg04 = data.readByte();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onError(_arg04, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            onEnrollmentProgress(_arg05, _arg13);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            HardwareAuthToken _arg14 = (HardwareAuthToken) data.readTypedObject(HardwareAuthToken.CREATOR);
                            data.enforceNoDataAvail();
                            onAuthenticationSucceeded(_arg06, _arg14);
                            reply.writeNoException();
                            break;
                        case 7:
                            onAuthenticationFailed();
                            reply.writeNoException();
                            break;
                        case 8:
                            long _arg07 = data.readLong();
                            data.enforceNoDataAvail();
                            onLockoutTimed(_arg07);
                            reply.writeNoException();
                            break;
                        case 9:
                            onLockoutPermanent();
                            reply.writeNoException();
                            break;
                        case 10:
                            onLockoutCleared();
                            reply.writeNoException();
                            break;
                        case 11:
                            onInteractionDetected();
                            reply.writeNoException();
                            break;
                        case 12:
                            int[] _arg08 = data.createIntArray();
                            data.enforceNoDataAvail();
                            onEnrollmentsEnumerated(_arg08);
                            reply.writeNoException();
                            break;
                        case 13:
                            int[] _arg09 = data.createIntArray();
                            data.enforceNoDataAvail();
                            onEnrollmentsRemoved(_arg09);
                            reply.writeNoException();
                            break;
                        case 14:
                            long _arg010 = data.readLong();
                            data.enforceNoDataAvail();
                            onAuthenticatorIdRetrieved(_arg010);
                            reply.writeNoException();
                            break;
                        case 15:
                            long _arg011 = data.readLong();
                            data.enforceNoDataAvail();
                            onAuthenticatorIdInvalidated(_arg011);
                            reply.writeNoException();
                            break;
                        case 16:
                            onSessionClosed();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ISessionCallback {
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

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onChallengeGenerated(long challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(challenge);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onChallengeGenerated is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onChallengeRevoked(long challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(challenge);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onChallengeRevoked is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onAcquired(byte info, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByte(info);
                    _data.writeInt(vendorCode);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onAcquired is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onError(byte error, int vendorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeByte(error);
                    _data.writeInt(vendorCode);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onError is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onEnrollmentProgress(int enrollmentId, int remaining) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(enrollmentId);
                    _data.writeInt(remaining);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onEnrollmentProgress is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onAuthenticationSucceeded(int enrollmentId, HardwareAuthToken hat) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(enrollmentId);
                    _data.writeTypedObject(hat, 0);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onAuthenticationSucceeded is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onAuthenticationFailed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onAuthenticationFailed is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onLockoutTimed(long durationMillis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(durationMillis);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onLockoutTimed is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onLockoutPermanent() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onLockoutPermanent is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onLockoutCleared() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onLockoutCleared is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onInteractionDetected() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onInteractionDetected is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onEnrollmentsEnumerated(int[] enrollmentIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeIntArray(enrollmentIds);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onEnrollmentsEnumerated is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onEnrollmentsRemoved(int[] enrollmentIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeIntArray(enrollmentIds);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onEnrollmentsRemoved is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onAuthenticatorIdRetrieved(long authenticatorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(authenticatorId);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onAuthenticatorIdRetrieved is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onAuthenticatorIdInvalidated(long newAuthenticatorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(newAuthenticatorId);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onAuthenticatorIdInvalidated is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
            public void onSessionClosed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(16, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method onSessionClosed is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
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

            @Override // android.hardware.biometrics.fingerprint.ISessionCallback
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
