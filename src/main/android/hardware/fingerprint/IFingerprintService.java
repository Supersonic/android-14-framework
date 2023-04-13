package android.hardware.fingerprint;

import android.Manifest;
import android.app.ActivityThread;
import android.content.AttributionSource;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.biometrics.IBiometricServiceLockoutResetCallback;
import android.hardware.biometrics.IBiometricStateListener;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.fingerprint.PointerContext;
import android.hardware.fingerprint.IFingerprintAuthenticatorsRegisteredCallback;
import android.hardware.fingerprint.IFingerprintClientActiveCallback;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.hardware.fingerprint.ISidefpsController;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes.dex */
public interface IFingerprintService extends IInterface {
    void addAuthenticatorsRegisteredCallback(IFingerprintAuthenticatorsRegisteredCallback iFingerprintAuthenticatorsRegisteredCallback) throws RemoteException;

    void addClientActiveCallback(IFingerprintClientActiveCallback iFingerprintClientActiveCallback) throws RemoteException;

    void addLockoutResetCallback(IBiometricServiceLockoutResetCallback iBiometricServiceLockoutResetCallback, String str) throws RemoteException;

    long authenticate(IBinder iBinder, long j, IFingerprintServiceReceiver iFingerprintServiceReceiver, FingerprintAuthenticateOptions fingerprintAuthenticateOptions) throws RemoteException;

    void cancelAuthentication(IBinder iBinder, String str, String str2, long j) throws RemoteException;

    void cancelAuthenticationFromService(int i, IBinder iBinder, String str, long j) throws RemoteException;

    void cancelEnrollment(IBinder iBinder, long j) throws RemoteException;

    void cancelFingerprintDetect(IBinder iBinder, String str, long j) throws RemoteException;

    ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str) throws RemoteException;

    long detectFingerprint(IBinder iBinder, IFingerprintServiceReceiver iFingerprintServiceReceiver, FingerprintAuthenticateOptions fingerprintAuthenticateOptions) throws RemoteException;

    byte[] dumpSensorServiceStateProto(int i, boolean z) throws RemoteException;

    long enroll(IBinder iBinder, byte[] bArr, int i, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str, int i2) throws RemoteException;

    void generateChallenge(IBinder iBinder, int i, int i2, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str) throws RemoteException;

    long getAuthenticatorId(int i, int i2) throws RemoteException;

    List<Fingerprint> getEnrolledFingerprints(int i, String str, String str2) throws RemoteException;

    int getLockoutModeForUser(int i, int i2) throws RemoteException;

    FingerprintSensorPropertiesInternal getSensorProperties(int i, String str) throws RemoteException;

    List<FingerprintSensorPropertiesInternal> getSensorPropertiesInternal(String str) throws RemoteException;

    boolean hasEnrolledFingerprints(int i, int i2, String str) throws RemoteException;

    boolean hasEnrolledFingerprintsDeprecated(int i, String str, String str2) throws RemoteException;

    void invalidateAuthenticatorId(int i, int i2, IInvalidationCallback iInvalidationCallback) throws RemoteException;

    boolean isClientActive() throws RemoteException;

    boolean isHardwareDetected(int i, String str) throws RemoteException;

    boolean isHardwareDetectedDeprecated(String str, String str2) throws RemoteException;

    void onPointerDown(long j, int i, PointerContext pointerContext) throws RemoteException;

    void onPointerUp(long j, int i, PointerContext pointerContext) throws RemoteException;

    void onPowerPressed() throws RemoteException;

    void onUiReady(long j, int i) throws RemoteException;

    void prepareForAuthentication(IBinder iBinder, long j, IBiometricSensorReceiver iBiometricSensorReceiver, FingerprintAuthenticateOptions fingerprintAuthenticateOptions, long j2, int i, boolean z) throws RemoteException;

    void registerAuthenticators(List<FingerprintSensorPropertiesInternal> list) throws RemoteException;

    void registerBiometricStateListener(IBiometricStateListener iBiometricStateListener) throws RemoteException;

    void remove(IBinder iBinder, int i, int i2, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str) throws RemoteException;

    void removeAll(IBinder iBinder, int i, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str) throws RemoteException;

    void removeClientActiveCallback(IFingerprintClientActiveCallback iFingerprintClientActiveCallback) throws RemoteException;

    void rename(int i, int i2, String str) throws RemoteException;

    void resetLockout(IBinder iBinder, int i, int i2, byte[] bArr, String str) throws RemoteException;

    void revokeChallenge(IBinder iBinder, int i, int i2, String str, long j) throws RemoteException;

    void scheduleWatchdog() throws RemoteException;

    void setSidefpsController(ISidefpsController iSidefpsController) throws RemoteException;

    void setUdfpsOverlay(IUdfpsOverlay iUdfpsOverlay) throws RemoteException;

    void setUdfpsOverlayController(IUdfpsOverlayController iUdfpsOverlayController) throws RemoteException;

    void startPreparedClient(int i, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IFingerprintService {
        @Override // android.hardware.fingerprint.IFingerprintService
        public ITestSession createTestSession(int sensorId, ITestSessionCallback callback, String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public byte[] dumpSensorServiceStateProto(int sensorId, boolean clearSchedulerBuffer) throws RemoteException {
            return null;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public List<FingerprintSensorPropertiesInternal> getSensorPropertiesInternal(String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public FingerprintSensorPropertiesInternal getSensorProperties(int sensorId, String opPackageName) throws RemoteException {
            return null;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public long authenticate(IBinder token, long operationId, IFingerprintServiceReceiver receiver, FingerprintAuthenticateOptions options) throws RemoteException {
            return 0L;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public long detectFingerprint(IBinder token, IFingerprintServiceReceiver receiver, FingerprintAuthenticateOptions options) throws RemoteException {
            return 0L;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void prepareForAuthentication(IBinder token, long operationId, IBiometricSensorReceiver sensorReceiver, FingerprintAuthenticateOptions options, long requestId, int cookie, boolean allowBackgroundAuthentication) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void startPreparedClient(int sensorId, int cookie) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void cancelAuthentication(IBinder token, String opPackageName, String attributionTag, long requestId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void cancelFingerprintDetect(IBinder token, String opPackageName, long requestId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void cancelAuthenticationFromService(int sensorId, IBinder token, String opPackageName, long requestId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public long enroll(IBinder token, byte[] hardwareAuthToken, int userId, IFingerprintServiceReceiver receiver, String opPackageName, int enrollReason) throws RemoteException {
            return 0L;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void cancelEnrollment(IBinder token, long requestId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void remove(IBinder token, int fingerId, int userId, IFingerprintServiceReceiver receiver, String opPackageName) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void removeAll(IBinder token, int userId, IFingerprintServiceReceiver receiver, String opPackageName) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void rename(int fingerId, int userId, String name) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public List<Fingerprint> getEnrolledFingerprints(int userId, String opPackageName, String attributionTag) throws RemoteException {
            return null;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public boolean isHardwareDetectedDeprecated(String opPackageName, String attributionTag) throws RemoteException {
            return false;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public boolean isHardwareDetected(int sensorId, String opPackageName) throws RemoteException {
            return false;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void generateChallenge(IBinder token, int sensorId, int userId, IFingerprintServiceReceiver receiver, String opPackageName) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void revokeChallenge(IBinder token, int sensorId, int userId, String opPackageName, long challenge) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public boolean hasEnrolledFingerprintsDeprecated(int userId, String opPackageName, String attributionTag) throws RemoteException {
            return false;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public boolean hasEnrolledFingerprints(int sensorId, int userId, String opPackageName) throws RemoteException {
            return false;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public int getLockoutModeForUser(int sensorId, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void invalidateAuthenticatorId(int sensorId, int userId, IInvalidationCallback callback) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public long getAuthenticatorId(int sensorId, int callingUserId) throws RemoteException {
            return 0L;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void resetLockout(IBinder token, int sensorId, int userId, byte[] hardwareAuthToken, String opPackageNAame) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void addLockoutResetCallback(IBiometricServiceLockoutResetCallback callback, String opPackageName) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public boolean isClientActive() throws RemoteException {
            return false;
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void addClientActiveCallback(IFingerprintClientActiveCallback callback) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void removeClientActiveCallback(IFingerprintClientActiveCallback callback) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void registerAuthenticators(List<FingerprintSensorPropertiesInternal> hidlSensors) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void addAuthenticatorsRegisteredCallback(IFingerprintAuthenticatorsRegisteredCallback callback) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void onPointerDown(long requestId, int sensorId, PointerContext pc) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void onPointerUp(long requestId, int sensorId, PointerContext pc) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void onUiReady(long requestId, int sensorId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void setUdfpsOverlayController(IUdfpsOverlayController controller) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void setSidefpsController(ISidefpsController controller) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void setUdfpsOverlay(IUdfpsOverlay controller) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void registerBiometricStateListener(IBiometricStateListener listener) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void onPowerPressed() throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IFingerprintService
        public void scheduleWatchdog() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IFingerprintService {
        public static final String DESCRIPTOR = "android.hardware.fingerprint.IFingerprintService";
        static final int TRANSACTION_addAuthenticatorsRegisteredCallback = 33;
        static final int TRANSACTION_addClientActiveCallback = 30;
        static final int TRANSACTION_addLockoutResetCallback = 28;
        static final int TRANSACTION_authenticate = 5;
        static final int TRANSACTION_cancelAuthentication = 9;
        static final int TRANSACTION_cancelAuthenticationFromService = 11;
        static final int TRANSACTION_cancelEnrollment = 13;
        static final int TRANSACTION_cancelFingerprintDetect = 10;
        static final int TRANSACTION_createTestSession = 1;
        static final int TRANSACTION_detectFingerprint = 6;
        static final int TRANSACTION_dumpSensorServiceStateProto = 2;
        static final int TRANSACTION_enroll = 12;
        static final int TRANSACTION_generateChallenge = 20;
        static final int TRANSACTION_getAuthenticatorId = 26;
        static final int TRANSACTION_getEnrolledFingerprints = 17;
        static final int TRANSACTION_getLockoutModeForUser = 24;
        static final int TRANSACTION_getSensorProperties = 4;
        static final int TRANSACTION_getSensorPropertiesInternal = 3;
        static final int TRANSACTION_hasEnrolledFingerprints = 23;
        static final int TRANSACTION_hasEnrolledFingerprintsDeprecated = 22;
        static final int TRANSACTION_invalidateAuthenticatorId = 25;
        static final int TRANSACTION_isClientActive = 29;
        static final int TRANSACTION_isHardwareDetected = 19;
        static final int TRANSACTION_isHardwareDetectedDeprecated = 18;
        static final int TRANSACTION_onPointerDown = 34;
        static final int TRANSACTION_onPointerUp = 35;
        static final int TRANSACTION_onPowerPressed = 41;
        static final int TRANSACTION_onUiReady = 36;
        static final int TRANSACTION_prepareForAuthentication = 7;
        static final int TRANSACTION_registerAuthenticators = 32;
        static final int TRANSACTION_registerBiometricStateListener = 40;
        static final int TRANSACTION_remove = 14;
        static final int TRANSACTION_removeAll = 15;
        static final int TRANSACTION_removeClientActiveCallback = 31;
        static final int TRANSACTION_rename = 16;
        static final int TRANSACTION_resetLockout = 27;
        static final int TRANSACTION_revokeChallenge = 21;
        static final int TRANSACTION_scheduleWatchdog = 42;
        static final int TRANSACTION_setSidefpsController = 38;
        static final int TRANSACTION_setUdfpsOverlay = 39;
        static final int TRANSACTION_setUdfpsOverlayController = 37;
        static final int TRANSACTION_startPreparedClient = 8;
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

        public static IFingerprintService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IFingerprintService)) {
                return (IFingerprintService) iin;
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
                    return "dumpSensorServiceStateProto";
                case 3:
                    return "getSensorPropertiesInternal";
                case 4:
                    return "getSensorProperties";
                case 5:
                    return "authenticate";
                case 6:
                    return "detectFingerprint";
                case 7:
                    return "prepareForAuthentication";
                case 8:
                    return "startPreparedClient";
                case 9:
                    return "cancelAuthentication";
                case 10:
                    return "cancelFingerprintDetect";
                case 11:
                    return "cancelAuthenticationFromService";
                case 12:
                    return "enroll";
                case 13:
                    return "cancelEnrollment";
                case 14:
                    return "remove";
                case 15:
                    return "removeAll";
                case 16:
                    return "rename";
                case 17:
                    return "getEnrolledFingerprints";
                case 18:
                    return "isHardwareDetectedDeprecated";
                case 19:
                    return "isHardwareDetected";
                case 20:
                    return "generateChallenge";
                case 21:
                    return "revokeChallenge";
                case 22:
                    return "hasEnrolledFingerprintsDeprecated";
                case 23:
                    return "hasEnrolledFingerprints";
                case 24:
                    return "getLockoutModeForUser";
                case 25:
                    return "invalidateAuthenticatorId";
                case 26:
                    return "getAuthenticatorId";
                case 27:
                    return "resetLockout";
                case 28:
                    return "addLockoutResetCallback";
                case 29:
                    return "isClientActive";
                case 30:
                    return "addClientActiveCallback";
                case 31:
                    return "removeClientActiveCallback";
                case 32:
                    return "registerAuthenticators";
                case 33:
                    return "addAuthenticatorsRegisteredCallback";
                case 34:
                    return "onPointerDown";
                case 35:
                    return "onPointerUp";
                case 36:
                    return "onUiReady";
                case 37:
                    return "setUdfpsOverlayController";
                case 38:
                    return "setSidefpsController";
                case 39:
                    return "setUdfpsOverlay";
                case 40:
                    return "registerBiometricStateListener";
                case 41:
                    return "onPowerPressed";
                case 42:
                    return "scheduleWatchdog";
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
                            int _arg0 = data.readInt();
                            ITestSessionCallback _arg1 = ITestSessionCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            ITestSession _result = createTestSession(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            byte[] _result2 = dumpSensorServiceStateProto(_arg02, _arg12);
                            reply.writeNoException();
                            reply.writeByteArray(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            List<FingerprintSensorPropertiesInternal> _result3 = getSensorPropertiesInternal(_arg03);
                            reply.writeNoException();
                            reply.writeTypedList(_result3, 1);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            FingerprintSensorPropertiesInternal _result4 = getSensorProperties(_arg04, _arg13);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            IBinder _arg05 = data.readStrongBinder();
                            long _arg14 = data.readLong();
                            IFingerprintServiceReceiver _arg22 = IFingerprintServiceReceiver.Stub.asInterface(data.readStrongBinder());
                            FingerprintAuthenticateOptions _arg3 = (FingerprintAuthenticateOptions) data.readTypedObject(FingerprintAuthenticateOptions.CREATOR);
                            data.enforceNoDataAvail();
                            long _result5 = authenticate(_arg05, _arg14, _arg22, _arg3);
                            reply.writeNoException();
                            reply.writeLong(_result5);
                            break;
                        case 6:
                            IBinder _arg06 = data.readStrongBinder();
                            IFingerprintServiceReceiver _arg15 = IFingerprintServiceReceiver.Stub.asInterface(data.readStrongBinder());
                            FingerprintAuthenticateOptions _arg23 = (FingerprintAuthenticateOptions) data.readTypedObject(FingerprintAuthenticateOptions.CREATOR);
                            data.enforceNoDataAvail();
                            long _result6 = detectFingerprint(_arg06, _arg15, _arg23);
                            reply.writeNoException();
                            reply.writeLong(_result6);
                            break;
                        case 7:
                            IBinder _arg07 = data.readStrongBinder();
                            long _arg16 = data.readLong();
                            IBiometricSensorReceiver _arg24 = IBiometricSensorReceiver.Stub.asInterface(data.readStrongBinder());
                            FingerprintAuthenticateOptions _arg32 = (FingerprintAuthenticateOptions) data.readTypedObject(FingerprintAuthenticateOptions.CREATOR);
                            long _arg4 = data.readLong();
                            int _arg5 = data.readInt();
                            boolean _arg6 = data.readBoolean();
                            data.enforceNoDataAvail();
                            prepareForAuthentication(_arg07, _arg16, _arg24, _arg32, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            startPreparedClient(_arg08, _arg17);
                            reply.writeNoException();
                            break;
                        case 9:
                            IBinder _arg09 = data.readStrongBinder();
                            String _arg18 = data.readString();
                            String _arg25 = data.readString();
                            long _arg33 = data.readLong();
                            data.enforceNoDataAvail();
                            cancelAuthentication(_arg09, _arg18, _arg25, _arg33);
                            reply.writeNoException();
                            break;
                        case 10:
                            IBinder _arg010 = data.readStrongBinder();
                            String _arg19 = data.readString();
                            long _arg26 = data.readLong();
                            data.enforceNoDataAvail();
                            cancelFingerprintDetect(_arg010, _arg19, _arg26);
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            IBinder _arg110 = data.readStrongBinder();
                            String _arg27 = data.readString();
                            long _arg34 = data.readLong();
                            data.enforceNoDataAvail();
                            cancelAuthenticationFromService(_arg011, _arg110, _arg27, _arg34);
                            reply.writeNoException();
                            break;
                        case 12:
                            IBinder _arg012 = data.readStrongBinder();
                            byte[] _arg111 = data.createByteArray();
                            int _arg28 = data.readInt();
                            IFingerprintServiceReceiver _arg35 = IFingerprintServiceReceiver.Stub.asInterface(data.readStrongBinder());
                            String _arg42 = data.readString();
                            int _arg52 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result7 = enroll(_arg012, _arg111, _arg28, _arg35, _arg42, _arg52);
                            reply.writeNoException();
                            reply.writeLong(_result7);
                            break;
                        case 13:
                            IBinder _arg013 = data.readStrongBinder();
                            long _arg112 = data.readLong();
                            data.enforceNoDataAvail();
                            cancelEnrollment(_arg013, _arg112);
                            reply.writeNoException();
                            break;
                        case 14:
                            IBinder _arg014 = data.readStrongBinder();
                            int _arg113 = data.readInt();
                            int _arg29 = data.readInt();
                            IFingerprintServiceReceiver _arg36 = IFingerprintServiceReceiver.Stub.asInterface(data.readStrongBinder());
                            String _arg43 = data.readString();
                            data.enforceNoDataAvail();
                            remove(_arg014, _arg113, _arg29, _arg36, _arg43);
                            reply.writeNoException();
                            break;
                        case 15:
                            IBinder _arg015 = data.readStrongBinder();
                            int _arg114 = data.readInt();
                            IFingerprintServiceReceiver _arg210 = IFingerprintServiceReceiver.Stub.asInterface(data.readStrongBinder());
                            String _arg37 = data.readString();
                            data.enforceNoDataAvail();
                            removeAll(_arg015, _arg114, _arg210, _arg37);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            int _arg115 = data.readInt();
                            String _arg211 = data.readString();
                            data.enforceNoDataAvail();
                            rename(_arg016, _arg115, _arg211);
                            reply.writeNoException();
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            String _arg116 = data.readString();
                            String _arg212 = data.readString();
                            data.enforceNoDataAvail();
                            List<Fingerprint> _result8 = getEnrolledFingerprints(_arg017, _arg116, _arg212);
                            reply.writeNoException();
                            reply.writeTypedList(_result8, 1);
                            break;
                        case 18:
                            String _arg018 = data.readString();
                            String _arg117 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result9 = isHardwareDetectedDeprecated(_arg018, _arg117);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 19:
                            int _arg019 = data.readInt();
                            String _arg118 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result10 = isHardwareDetected(_arg019, _arg118);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 20:
                            IBinder _arg020 = data.readStrongBinder();
                            int _arg119 = data.readInt();
                            int _arg213 = data.readInt();
                            IFingerprintServiceReceiver _arg38 = IFingerprintServiceReceiver.Stub.asInterface(data.readStrongBinder());
                            String _arg44 = data.readString();
                            data.enforceNoDataAvail();
                            generateChallenge(_arg020, _arg119, _arg213, _arg38, _arg44);
                            reply.writeNoException();
                            break;
                        case 21:
                            IBinder _arg021 = data.readStrongBinder();
                            int _arg120 = data.readInt();
                            int _arg214 = data.readInt();
                            String _arg39 = data.readString();
                            long _arg45 = data.readLong();
                            data.enforceNoDataAvail();
                            revokeChallenge(_arg021, _arg120, _arg214, _arg39, _arg45);
                            reply.writeNoException();
                            break;
                        case 22:
                            int _arg022 = data.readInt();
                            String _arg121 = data.readString();
                            String _arg215 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result11 = hasEnrolledFingerprintsDeprecated(_arg022, _arg121, _arg215);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 23:
                            int _arg023 = data.readInt();
                            int _arg122 = data.readInt();
                            String _arg216 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result12 = hasEnrolledFingerprints(_arg023, _arg122, _arg216);
                            reply.writeNoException();
                            reply.writeBoolean(_result12);
                            break;
                        case 24:
                            int _arg024 = data.readInt();
                            int _arg123 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result13 = getLockoutModeForUser(_arg024, _arg123);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 25:
                            int _arg025 = data.readInt();
                            int _arg124 = data.readInt();
                            IInvalidationCallback _arg217 = IInvalidationCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            invalidateAuthenticatorId(_arg025, _arg124, _arg217);
                            reply.writeNoException();
                            break;
                        case 26:
                            int _arg026 = data.readInt();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result14 = getAuthenticatorId(_arg026, _arg125);
                            reply.writeNoException();
                            reply.writeLong(_result14);
                            break;
                        case 27:
                            IBinder _arg027 = data.readStrongBinder();
                            int _arg126 = data.readInt();
                            int _arg218 = data.readInt();
                            byte[] _arg310 = data.createByteArray();
                            String _arg46 = data.readString();
                            data.enforceNoDataAvail();
                            resetLockout(_arg027, _arg126, _arg218, _arg310, _arg46);
                            reply.writeNoException();
                            break;
                        case 28:
                            IBiometricServiceLockoutResetCallback _arg028 = IBiometricServiceLockoutResetCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg127 = data.readString();
                            data.enforceNoDataAvail();
                            addLockoutResetCallback(_arg028, _arg127);
                            reply.writeNoException();
                            break;
                        case 29:
                            boolean _result15 = isClientActive();
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 30:
                            IFingerprintClientActiveCallback _arg029 = IFingerprintClientActiveCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addClientActiveCallback(_arg029);
                            reply.writeNoException();
                            break;
                        case 31:
                            IFingerprintClientActiveCallback _arg030 = IFingerprintClientActiveCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeClientActiveCallback(_arg030);
                            reply.writeNoException();
                            break;
                        case 32:
                            List<FingerprintSensorPropertiesInternal> _arg031 = data.createTypedArrayList(FingerprintSensorPropertiesInternal.CREATOR);
                            data.enforceNoDataAvail();
                            registerAuthenticators(_arg031);
                            reply.writeNoException();
                            break;
                        case 33:
                            IFingerprintAuthenticatorsRegisteredCallback _arg032 = IFingerprintAuthenticatorsRegisteredCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addAuthenticatorsRegisteredCallback(_arg032);
                            reply.writeNoException();
                            break;
                        case 34:
                            long _arg033 = data.readLong();
                            int _arg128 = data.readInt();
                            PointerContext _arg219 = (PointerContext) data.readTypedObject(PointerContext.CREATOR);
                            data.enforceNoDataAvail();
                            onPointerDown(_arg033, _arg128, _arg219);
                            reply.writeNoException();
                            break;
                        case 35:
                            long _arg034 = data.readLong();
                            int _arg129 = data.readInt();
                            PointerContext _arg220 = (PointerContext) data.readTypedObject(PointerContext.CREATOR);
                            data.enforceNoDataAvail();
                            onPointerUp(_arg034, _arg129, _arg220);
                            reply.writeNoException();
                            break;
                        case 36:
                            long _arg035 = data.readLong();
                            int _arg130 = data.readInt();
                            data.enforceNoDataAvail();
                            onUiReady(_arg035, _arg130);
                            reply.writeNoException();
                            break;
                        case 37:
                            IUdfpsOverlayController _arg036 = IUdfpsOverlayController.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setUdfpsOverlayController(_arg036);
                            reply.writeNoException();
                            break;
                        case 38:
                            ISidefpsController _arg037 = ISidefpsController.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setSidefpsController(_arg037);
                            reply.writeNoException();
                            break;
                        case 39:
                            IUdfpsOverlay _arg038 = IUdfpsOverlay.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setUdfpsOverlay(_arg038);
                            reply.writeNoException();
                            break;
                        case 40:
                            IBiometricStateListener _arg039 = IBiometricStateListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerBiometricStateListener(_arg039);
                            reply.writeNoException();
                            break;
                        case 41:
                            onPowerPressed();
                            break;
                        case 42:
                            scheduleWatchdog();
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
        public static class Proxy implements IFingerprintService {
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

            @Override // android.hardware.fingerprint.IFingerprintService
            public ITestSession createTestSession(int sensorId, ITestSessionCallback callback, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
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

            @Override // android.hardware.fingerprint.IFingerprintService
            public byte[] dumpSensorServiceStateProto(int sensorId, boolean clearSchedulerBuffer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeBoolean(clearSchedulerBuffer);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public List<FingerprintSensorPropertiesInternal> getSensorPropertiesInternal(String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    List<FingerprintSensorPropertiesInternal> _result = _reply.createTypedArrayList(FingerprintSensorPropertiesInternal.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public FingerprintSensorPropertiesInternal getSensorProperties(int sensorId, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    FingerprintSensorPropertiesInternal _result = (FingerprintSensorPropertiesInternal) _reply.readTypedObject(FingerprintSensorPropertiesInternal.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public long authenticate(IBinder token, long operationId, IFingerprintServiceReceiver receiver, FingerprintAuthenticateOptions options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeLong(operationId);
                    _data.writeStrongInterface(receiver);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public long detectFingerprint(IBinder token, IFingerprintServiceReceiver receiver, FingerprintAuthenticateOptions options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeStrongInterface(receiver);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void prepareForAuthentication(IBinder token, long operationId, IBiometricSensorReceiver sensorReceiver, FingerprintAuthenticateOptions options, long requestId, int cookie, boolean allowBackgroundAuthentication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeLong(operationId);
                    _data.writeStrongInterface(sensorReceiver);
                    _data.writeTypedObject(options, 0);
                    _data.writeLong(requestId);
                    _data.writeInt(cookie);
                    _data.writeBoolean(allowBackgroundAuthentication);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void startPreparedClient(int sensorId, int cookie) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(cookie);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void cancelAuthentication(IBinder token, String opPackageName, String attributionTag, long requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(opPackageName);
                    _data.writeString(attributionTag);
                    _data.writeLong(requestId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void cancelFingerprintDetect(IBinder token, String opPackageName, long requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(opPackageName);
                    _data.writeLong(requestId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void cancelAuthenticationFromService(int sensorId, IBinder token, String opPackageName, long requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeStrongBinder(token);
                    _data.writeString(opPackageName);
                    _data.writeLong(requestId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public long enroll(IBinder token, byte[] hardwareAuthToken, int userId, IFingerprintServiceReceiver receiver, String opPackageName, int enrollReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeByteArray(hardwareAuthToken);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(receiver);
                    _data.writeString(opPackageName);
                    _data.writeInt(enrollReason);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void cancelEnrollment(IBinder token, long requestId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeLong(requestId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void remove(IBinder token, int fingerId, int userId, IFingerprintServiceReceiver receiver, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(fingerId);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(receiver);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void removeAll(IBinder token, int userId, IFingerprintServiceReceiver receiver, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(receiver);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void rename(int fingerId, int userId, String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(fingerId);
                    _data.writeInt(userId);
                    _data.writeString(name);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public List<Fingerprint> getEnrolledFingerprints(int userId, String opPackageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(opPackageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    List<Fingerprint> _result = _reply.createTypedArrayList(Fingerprint.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public boolean isHardwareDetectedDeprecated(String opPackageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(opPackageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public boolean isHardwareDetected(int sensorId, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void generateChallenge(IBinder token, int sensorId, int userId, IFingerprintServiceReceiver receiver, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(receiver);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void revokeChallenge(IBinder token, int sensorId, int userId, String opPackageName, long challenge) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeString(opPackageName);
                    _data.writeLong(challenge);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public boolean hasEnrolledFingerprintsDeprecated(int userId, String opPackageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(opPackageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public boolean hasEnrolledFingerprints(int sensorId, int userId, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public int getLockoutModeForUser(int sensorId, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void invalidateAuthenticatorId(int sensorId, int userId, IInvalidationCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public long getAuthenticatorId(int sensorId, int callingUserId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sensorId);
                    _data.writeInt(callingUserId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void resetLockout(IBinder token, int sensorId, int userId, byte[] hardwareAuthToken, String opPackageNAame) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(sensorId);
                    _data.writeInt(userId);
                    _data.writeByteArray(hardwareAuthToken);
                    _data.writeString(opPackageNAame);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void addLockoutResetCallback(IBiometricServiceLockoutResetCallback callback, String opPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeString(opPackageName);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public boolean isClientActive() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void addClientActiveCallback(IFingerprintClientActiveCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void removeClientActiveCallback(IFingerprintClientActiveCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void registerAuthenticators(List<FingerprintSensorPropertiesInternal> hidlSensors) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(hidlSensors, 0);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void addAuthenticatorsRegisteredCallback(IFingerprintAuthenticatorsRegisteredCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void onPointerDown(long requestId, int sensorId, PointerContext pc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeInt(sensorId);
                    _data.writeTypedObject(pc, 0);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void onPointerUp(long requestId, int sensorId, PointerContext pc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeInt(sensorId);
                    _data.writeTypedObject(pc, 0);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void onUiReady(long requestId, int sensorId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(requestId);
                    _data.writeInt(sensorId);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void setUdfpsOverlayController(IUdfpsOverlayController controller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(controller);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void setSidefpsController(ISidefpsController controller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(controller);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void setUdfpsOverlay(IUdfpsOverlay controller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(controller);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void registerBiometricStateListener(IBiometricStateListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void onPowerPressed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(41, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IFingerprintService
            public void scheduleWatchdog() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void createTestSession_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.TEST_BIOMETRIC, source);
        }

        protected void dumpSensorServiceStateProto_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void getSensorProperties_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void detectFingerprint_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void prepareForAuthentication_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_BIOMETRIC, source);
        }

        protected void startPreparedClient_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_BIOMETRIC, source);
        }

        protected void cancelFingerprintDetect_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void cancelAuthenticationFromService_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_BIOMETRIC, source);
        }

        protected void enroll_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void cancelEnrollment_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void remove_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void removeAll_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void rename_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void isHardwareDetected_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void generateChallenge_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void revokeChallenge_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void hasEnrolledFingerprints_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void getLockoutModeForUser_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void invalidateAuthenticatorId_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void getAuthenticatorId_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void resetLockout_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.RESET_FINGERPRINT_LOCKOUT, source);
        }

        protected void addLockoutResetCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void isClientActive_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void addClientActiveCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void removeClientActiveCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MANAGE_FINGERPRINT, source);
        }

        protected void registerAuthenticators_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void addAuthenticatorsRegisteredCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void onPointerDown_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void onPointerUp_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void onUiReady_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void setUdfpsOverlayController_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void setSidefpsController_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void setUdfpsOverlay_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void registerBiometricStateListener_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void onPowerPressed_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        protected void scheduleWatchdog_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 41;
        }
    }
}
