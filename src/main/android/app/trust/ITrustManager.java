package android.app.trust;

import android.Manifest;
import android.app.ActivityThread;
import android.app.trust.ITrustListener;
import android.content.AttributionSource;
import android.hardware.biometrics.BiometricSourceType;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface ITrustManager extends IInterface {
    void clearAllBiometricRecognized(BiometricSourceType biometricSourceType, int i) throws RemoteException;

    boolean isDeviceLocked(int i, int i2) throws RemoteException;

    boolean isDeviceSecure(int i, int i2) throws RemoteException;

    boolean isTrustUsuallyManaged(int i) throws RemoteException;

    void registerTrustListener(ITrustListener iTrustListener) throws RemoteException;

    void reportEnabledTrustAgentsChanged(int i) throws RemoteException;

    void reportKeyguardShowingChanged() throws RemoteException;

    void reportUnlockAttempt(boolean z, int i) throws RemoteException;

    void reportUnlockLockout(int i, int i2) throws RemoteException;

    void reportUserMayRequestUnlock(int i) throws RemoteException;

    void reportUserRequestedUnlock(int i, boolean z) throws RemoteException;

    void setDeviceLockedForUser(int i, boolean z) throws RemoteException;

    void unlockedByBiometricForUser(int i, BiometricSourceType biometricSourceType) throws RemoteException;

    void unregisterTrustListener(ITrustListener iTrustListener) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements ITrustManager {
        @Override // android.app.trust.ITrustManager
        public void reportUnlockAttempt(boolean successful, int userId) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void reportUserRequestedUnlock(int userId, boolean dismissKeyguard) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void reportUserMayRequestUnlock(int userId) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void reportUnlockLockout(int timeoutMs, int userId) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void reportEnabledTrustAgentsChanged(int userId) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void registerTrustListener(ITrustListener trustListener) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void unregisterTrustListener(ITrustListener trustListener) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void reportKeyguardShowingChanged() throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void setDeviceLockedForUser(int userId, boolean locked) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public boolean isDeviceLocked(int userId, int displayId) throws RemoteException {
            return false;
        }

        @Override // android.app.trust.ITrustManager
        public boolean isDeviceSecure(int userId, int displayId) throws RemoteException {
            return false;
        }

        @Override // android.app.trust.ITrustManager
        public boolean isTrustUsuallyManaged(int userId) throws RemoteException {
            return false;
        }

        @Override // android.app.trust.ITrustManager
        public void unlockedByBiometricForUser(int userId, BiometricSourceType source) throws RemoteException {
        }

        @Override // android.app.trust.ITrustManager
        public void clearAllBiometricRecognized(BiometricSourceType target, int unlockedUser) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ITrustManager {
        public static final String DESCRIPTOR = "android.app.trust.ITrustManager";
        static final int TRANSACTION_clearAllBiometricRecognized = 14;
        static final int TRANSACTION_isDeviceLocked = 10;
        static final int TRANSACTION_isDeviceSecure = 11;
        static final int TRANSACTION_isTrustUsuallyManaged = 12;
        static final int TRANSACTION_registerTrustListener = 6;
        static final int TRANSACTION_reportEnabledTrustAgentsChanged = 5;
        static final int TRANSACTION_reportKeyguardShowingChanged = 8;
        static final int TRANSACTION_reportUnlockAttempt = 1;
        static final int TRANSACTION_reportUnlockLockout = 4;
        static final int TRANSACTION_reportUserMayRequestUnlock = 3;
        static final int TRANSACTION_reportUserRequestedUnlock = 2;
        static final int TRANSACTION_setDeviceLockedForUser = 9;
        static final int TRANSACTION_unlockedByBiometricForUser = 13;
        static final int TRANSACTION_unregisterTrustListener = 7;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static ITrustManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITrustManager)) {
                return (ITrustManager) iin;
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
                    return "reportUnlockAttempt";
                case 2:
                    return "reportUserRequestedUnlock";
                case 3:
                    return "reportUserMayRequestUnlock";
                case 4:
                    return "reportUnlockLockout";
                case 5:
                    return "reportEnabledTrustAgentsChanged";
                case 6:
                    return "registerTrustListener";
                case 7:
                    return "unregisterTrustListener";
                case 8:
                    return "reportKeyguardShowingChanged";
                case 9:
                    return "setDeviceLockedForUser";
                case 10:
                    return "isDeviceLocked";
                case 11:
                    return "isDeviceSecure";
                case 12:
                    return "isTrustUsuallyManaged";
                case 13:
                    return "unlockedByBiometricForUser";
                case 14:
                    return "clearAllBiometricRecognized";
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
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            reportUnlockAttempt(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            reportUserRequestedUnlock(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            reportUserMayRequestUnlock(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            reportUnlockLockout(_arg04, _arg13);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            reportEnabledTrustAgentsChanged(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            ITrustListener _arg06 = ITrustListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerTrustListener(_arg06);
                            reply.writeNoException();
                            break;
                        case 7:
                            ITrustListener _arg07 = ITrustListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterTrustListener(_arg07);
                            reply.writeNoException();
                            break;
                        case 8:
                            reportKeyguardShowingChanged();
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg08 = data.readInt();
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDeviceLockedForUser(_arg08, _arg14);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg09 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = isDeviceLocked(_arg09, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 11:
                            int _arg010 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = isDeviceSecure(_arg010, _arg16);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 12:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = isTrustUsuallyManaged(_arg011);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 13:
                            int _arg012 = data.readInt();
                            BiometricSourceType _arg17 = (BiometricSourceType) data.readTypedObject(BiometricSourceType.CREATOR);
                            data.enforceNoDataAvail();
                            unlockedByBiometricForUser(_arg012, _arg17);
                            reply.writeNoException();
                            break;
                        case 14:
                            BiometricSourceType _arg013 = (BiometricSourceType) data.readTypedObject(BiometricSourceType.CREATOR);
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            clearAllBiometricRecognized(_arg013, _arg18);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Proxy implements ITrustManager {
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

            @Override // android.app.trust.ITrustManager
            public void reportUnlockAttempt(boolean successful, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(successful);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void reportUserRequestedUnlock(int userId, boolean dismissKeyguard) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(dismissKeyguard);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void reportUserMayRequestUnlock(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void reportUnlockLockout(int timeoutMs, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(timeoutMs);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void reportEnabledTrustAgentsChanged(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void registerTrustListener(ITrustListener trustListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(trustListener);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void unregisterTrustListener(ITrustListener trustListener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(trustListener);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void reportKeyguardShowingChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void setDeviceLockedForUser(int userId, boolean locked) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(locked);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public boolean isDeviceLocked(int userId, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(displayId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public boolean isDeviceSecure(int userId, int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(displayId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public boolean isTrustUsuallyManaged(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void unlockedByBiometricForUser(int userId, BiometricSourceType source) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeTypedObject(source, 0);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.app.trust.ITrustManager
            public void clearAllBiometricRecognized(BiometricSourceType target, int unlockedUser) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(target, 0);
                    _data.writeInt(unlockedUser);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void isTrustUsuallyManaged_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.TRUST_LISTENER, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 13;
        }
    }
}
