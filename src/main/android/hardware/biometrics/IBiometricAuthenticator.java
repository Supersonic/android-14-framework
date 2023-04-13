package android.hardware.biometrics;

import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IBiometricAuthenticator extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.biometrics.IBiometricAuthenticator";

    void cancelAuthenticationFromService(IBinder iBinder, String str, long j) throws RemoteException;

    ITestSession createTestSession(ITestSessionCallback iTestSessionCallback, String str) throws RemoteException;

    byte[] dumpSensorServiceStateProto(boolean z) throws RemoteException;

    long getAuthenticatorId(int i) throws RemoteException;

    int getLockoutModeForUser(int i) throws RemoteException;

    SensorPropertiesInternal getSensorProperties(String str) throws RemoteException;

    boolean hasEnrolledTemplates(int i, String str) throws RemoteException;

    void invalidateAuthenticatorId(int i, IInvalidationCallback iInvalidationCallback) throws RemoteException;

    boolean isHardwareDetected(String str) throws RemoteException;

    void prepareForAuthentication(boolean z, IBinder iBinder, long j, int i, IBiometricSensorReceiver iBiometricSensorReceiver, String str, long j2, int i2, boolean z2) throws RemoteException;

    void resetLockout(IBinder iBinder, String str, int i, byte[] bArr) throws RemoteException;

    void startPreparedClient(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IBiometricAuthenticator {
        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public ITestSession createTestSession(ITestSessionCallback callback, String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public SensorPropertiesInternal getSensorProperties(String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public byte[] dumpSensorServiceStateProto(boolean clearSchedulerBuffer) throws RemoteException {
            return null;
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public void prepareForAuthentication(boolean requireConfirmation, IBinder token, long operationId, int userId, IBiometricSensorReceiver sensorReceiver, String opPackageName, long requestId, int cookie, boolean allowBackgroundAuthentication) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public void startPreparedClient(int cookie) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public void cancelAuthenticationFromService(IBinder token, String opPackageName, long requestId) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public boolean isHardwareDetected(String opPackageName) throws RemoteException {
            return false;
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public boolean hasEnrolledTemplates(int userId, String opPackageName) throws RemoteException {
            return false;
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public int getLockoutModeForUser(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public void invalidateAuthenticatorId(int userId, IInvalidationCallback callback) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public long getAuthenticatorId(int callingUserId) throws RemoteException {
            return 0L;
        }

        @Override // android.hardware.biometrics.IBiometricAuthenticator
        public void resetLockout(IBinder token, String opPackageName, int userId, byte[] hardwareAuthToken) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IBiometricAuthenticator {
        static final int TRANSACTION_cancelAuthenticationFromService = 6;
        static final int TRANSACTION_createTestSession = 1;
        static final int TRANSACTION_dumpSensorServiceStateProto = 3;
        static final int TRANSACTION_getAuthenticatorId = 11;
        static final int TRANSACTION_getLockoutModeForUser = 9;
        static final int TRANSACTION_getSensorProperties = 2;
        static final int TRANSACTION_hasEnrolledTemplates = 8;
        static final int TRANSACTION_invalidateAuthenticatorId = 10;
        static final int TRANSACTION_isHardwareDetected = 7;
        static final int TRANSACTION_prepareForAuthentication = 4;
        static final int TRANSACTION_resetLockout = 12;
        static final int TRANSACTION_startPreparedClient = 5;

        public Stub() {
            attachInterface(this, IBiometricAuthenticator.DESCRIPTOR);
        }

        public static IBiometricAuthenticator asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBiometricAuthenticator.DESCRIPTOR);
            if (iin != null && (iin instanceof IBiometricAuthenticator)) {
                return (IBiometricAuthenticator) iin;
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
                    return "createTestSession";
                case 2:
                    return "getSensorProperties";
                case 3:
                    return "dumpSensorServiceStateProto";
                case 4:
                    return "prepareForAuthentication";
                case 5:
                    return "startPreparedClient";
                case 6:
                    return "cancelAuthenticationFromService";
                case 7:
                    return "isHardwareDetected";
                case 8:
                    return "hasEnrolledTemplates";
                case 9:
                    return "getLockoutModeForUser";
                case 10:
                    return "invalidateAuthenticatorId";
                case 11:
                    return "getAuthenticatorId";
                case 12:
                    return "resetLockout";
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
                data.enforceInterface(IBiometricAuthenticator.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBiometricAuthenticator.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ITestSessionCallback _arg0 = ITestSessionCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            ITestSession _result = createTestSession(_arg0, _arg1);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            return true;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            SensorPropertiesInternal _result2 = getSensorProperties(_arg02);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            return true;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            byte[] _result3 = dumpSensorServiceStateProto(_arg03);
                            reply.writeNoException();
                            reply.writeByteArray(_result3);
                            return true;
                        case 4:
                            boolean _arg04 = data.readBoolean();
                            IBinder _arg12 = data.readStrongBinder();
                            long _arg2 = data.readLong();
                            int _arg3 = data.readInt();
                            IBiometricSensorReceiver _arg4 = IBiometricSensorReceiver.Stub.asInterface(data.readStrongBinder());
                            String _arg5 = data.readString();
                            long _arg6 = data.readLong();
                            int _arg7 = data.readInt();
                            boolean _arg8 = data.readBoolean();
                            data.enforceNoDataAvail();
                            prepareForAuthentication(_arg04, _arg12, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8);
                            reply.writeNoException();
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            startPreparedClient(_arg05);
                            reply.writeNoException();
                            return true;
                        case 6:
                            IBinder _arg06 = data.readStrongBinder();
                            String _arg13 = data.readString();
                            long _arg22 = data.readLong();
                            data.enforceNoDataAvail();
                            cancelAuthenticationFromService(_arg06, _arg13, _arg22);
                            reply.writeNoException();
                            return true;
                        case 7:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = isHardwareDetected(_arg07);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            return true;
                        case 8:
                            int _arg08 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result5 = hasEnrolledTemplates(_arg08, _arg14);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            return true;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result6 = getLockoutModeForUser(_arg09);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            return true;
                        case 10:
                            int _arg010 = data.readInt();
                            IInvalidationCallback _arg15 = IInvalidationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            invalidateAuthenticatorId(_arg010, _arg15);
                            reply.writeNoException();
                            return true;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result7 = getAuthenticatorId(_arg011);
                            reply.writeNoException();
                            reply.writeLong(_result7);
                            return true;
                        case 12:
                            IBinder _arg012 = data.readStrongBinder();
                            String _arg16 = data.readString();
                            int _arg23 = data.readInt();
                            byte[] _arg32 = data.createByteArray();
                            data.enforceNoDataAvail();
                            resetLockout(_arg012, _arg16, _arg23, _arg32);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IBiometricAuthenticator {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBiometricAuthenticator.DESCRIPTOR;
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public ITestSession createTestSession(ITestSessionCallback callback, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ITestSession _result = ITestSession.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public SensorPropertiesInternal getSensorProperties(String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    SensorPropertiesInternal _result = (SensorPropertiesInternal) _reply.readTypedObject(SensorPropertiesInternal.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public byte[] dumpSensorServiceStateProto(boolean clearSchedulerBuffer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeBoolean(clearSchedulerBuffer);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public void prepareForAuthentication(boolean requireConfirmation, IBinder token, long operationId, int userId, IBiometricSensorReceiver sensorReceiver, String opPackageName, long requestId, int cookie, boolean allowBackgroundAuthentication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeBoolean(requireConfirmation);
                    try {
                        _data.writeStrongBinder(token);
                    } catch (Throwable th) {
                        th = th;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
                try {
                    _data.writeLong(operationId);
                    try {
                        _data.writeInt(userId);
                        try {
                            _data.writeStrongInterface(sensorReceiver);
                            try {
                                _data.writeString(opPackageName);
                            } catch (Throwable th3) {
                                th = th3;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th5) {
                        th = th5;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeLong(requestId);
                        try {
                            _data.writeInt(cookie);
                            try {
                                _data.writeBoolean(allowBackgroundAuthentication);
                                try {
                                    this.mRemote.transact(4, _data, _reply, 0);
                                    _reply.readException();
                                    _reply.recycle();
                                    _data.recycle();
                                } catch (Throwable th6) {
                                    th = th6;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th9) {
                        th = th9;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th10) {
                    th = th10;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public void startPreparedClient(int cookie) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeInt(cookie);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public void cancelAuthenticationFromService(IBinder token, String opPackageName, long requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(opPackageName);
                    _data.writeLong(requestId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public boolean isHardwareDetected(String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public boolean hasEnrolledTemplates(int userId, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public int getLockoutModeForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public void invalidateAuthenticatorId(int userId, IInvalidationCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public long getAuthenticatorId(int callingUserId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeInt(callingUserId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricAuthenticator
            public void resetLockout(IBinder token, String opPackageName, int userId, byte[] hardwareAuthToken) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IBiometricAuthenticator.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(opPackageName);
                    _data.writeInt(userId);
                    _data.writeByteArray(hardwareAuthToken);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
