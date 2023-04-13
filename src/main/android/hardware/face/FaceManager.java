package android.hardware.face;

import android.Manifest;
import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.BiometricFaceConstants;
import android.hardware.biometrics.BiometricStateListener;
import android.hardware.biometrics.CryptoObject;
import android.hardware.biometrics.IBiometricServiceLockoutResetCallback;
import android.hardware.face.FaceAuthenticateOptions;
import android.hardware.face.FaceManager;
import android.hardware.face.IFaceAuthenticatorsRegisteredCallback;
import android.hardware.face.IFaceServiceReceiver;
import android.p008os.Binder;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.IRemoteCallback;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.PowerManager;
import android.p008os.RemoteException;
import android.p008os.Trace;
import android.p008os.UserHandle;
import android.util.Slog;
import android.view.Surface;
import com.android.internal.C4057R;
import com.android.internal.p028os.SomeArgs;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FaceManager implements BiometricAuthenticator, BiometricFaceConstants {
    private static final int MSG_ACQUIRED = 101;
    private static final int MSG_AUTHENTICATION_FAILED = 103;
    private static final int MSG_AUTHENTICATION_FRAME = 112;
    private static final int MSG_AUTHENTICATION_SUCCEEDED = 102;
    private static final int MSG_CHALLENGE_GENERATED = 108;
    private static final int MSG_ENROLLMENT_FRAME = 113;
    private static final int MSG_ENROLL_RESULT = 100;
    private static final int MSG_ERROR = 104;
    private static final int MSG_FACE_DETECTED = 109;
    private static final int MSG_GET_FEATURE_COMPLETED = 106;
    private static final int MSG_REMOVED = 105;
    private static final int MSG_SET_FEATURE_COMPLETED = 107;
    private static final String TAG = "FaceManager";
    private AuthenticationCallback mAuthenticationCallback;
    private final Context mContext;
    private CryptoObject mCryptoObject;
    private EnrollmentCallback mEnrollmentCallback;
    private FaceDetectionCallback mFaceDetectionCallback;
    private GenerateChallengeCallback mGenerateChallengeCallback;
    private GetFeatureCallback mGetFeatureCallback;
    private Handler mHandler;
    private RemovalCallback mRemovalCallback;
    private Face mRemovalFace;
    private final IFaceService mService;
    private SetFeatureCallback mSetFeatureCallback;
    private final IBinder mToken = new Binder();
    private List<FaceSensorPropertiesInternal> mProps = new ArrayList();
    private final IFaceServiceReceiver mServiceReceiver = new IFaceServiceReceiver.Stub() { // from class: android.hardware.face.FaceManager.1
        @Override // android.hardware.face.IFaceServiceReceiver
        public void onEnrollResult(Face face, int remaining) {
            FaceManager.this.mHandler.obtainMessage(100, remaining, 0, face).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onAcquired(int acquireInfo, int vendorCode) {
            FaceManager.this.mHandler.obtainMessage(101, acquireInfo, vendorCode).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onAuthenticationSucceeded(Face face, int userId, boolean isStrongBiometric) {
            FaceManager.this.mHandler.obtainMessage(102, userId, isStrongBiometric ? 1 : 0, face).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onFaceDetected(int sensorId, int userId, boolean isStrongBiometric) {
            FaceManager.this.mHandler.obtainMessage(109, sensorId, userId, Boolean.valueOf(isStrongBiometric)).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onAuthenticationFailed() {
            FaceManager.this.mHandler.obtainMessage(103).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onError(int error, int vendorCode) {
            FaceManager.this.mHandler.obtainMessage(104, error, vendorCode).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onRemoved(Face face, int remaining) {
            FaceManager.this.mHandler.obtainMessage(105, remaining, 0, face).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onFeatureSet(boolean success, int feature) {
            FaceManager.this.mHandler.obtainMessage(107, feature, 0, Boolean.valueOf(success)).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onFeatureGet(boolean success, int[] features, boolean[] featureState) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = Boolean.valueOf(success);
            args.arg2 = features;
            args.arg3 = featureState;
            FaceManager.this.mHandler.obtainMessage(106, args).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onChallengeGenerated(int sensorId, int userId, long challenge) {
            FaceManager.this.mHandler.obtainMessage(108, sensorId, userId, Long.valueOf(challenge)).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onAuthenticationFrame(FaceAuthenticationFrame frame) {
            FaceManager.this.mHandler.obtainMessage(112, frame).sendToTarget();
        }

        @Override // android.hardware.face.IFaceServiceReceiver
        public void onEnrollmentFrame(FaceEnrollFrame frame) {
            FaceManager.this.mHandler.obtainMessage(113, frame).sendToTarget();
        }
    };

    /* loaded from: classes.dex */
    public interface FaceDetectionCallback {
        void onFaceDetected(int i, int i2, boolean z);
    }

    /* loaded from: classes.dex */
    public interface GenerateChallengeCallback {
        void onGenerateChallengeResult(int i, int i2, long j);
    }

    /* loaded from: classes.dex */
    public static abstract class GetFeatureCallback {
        public abstract void onCompleted(boolean z, int[] iArr, boolean[] zArr);
    }

    /* loaded from: classes.dex */
    public static abstract class SetFeatureCallback {
        public abstract void onCompleted(boolean z, int i);
    }

    public FaceManager(Context context, IFaceService service) {
        this.mContext = context;
        this.mService = service;
        if (service == null) {
            Slog.m92v(TAG, "FaceAuthenticationManagerService was null");
        }
        this.mHandler = new MyHandler(context);
        if (context.checkCallingOrSelfPermission(Manifest.C0000permission.USE_BIOMETRIC_INTERNAL) == 0) {
            addAuthenticatorsRegisteredCallback(new IFaceAuthenticatorsRegisteredCallback.Stub() { // from class: android.hardware.face.FaceManager.2
                @Override // android.hardware.face.IFaceAuthenticatorsRegisteredCallback
                public void onAllAuthenticatorsRegistered(List<FaceSensorPropertiesInternal> sensors) {
                    FaceManager.this.mProps = sensors;
                }
            });
        }
    }

    private void useHandler(Handler handler) {
        if (handler != null) {
            this.mHandler = new MyHandler(handler.getLooper());
        } else if (this.mHandler.getLooper() != this.mContext.getMainLooper()) {
            this.mHandler = new MyHandler(this.mContext.getMainLooper());
        }
    }

    @Deprecated
    public void authenticate(CryptoObject crypto, CancellationSignal cancel, AuthenticationCallback callback, Handler handler, int userId) {
        authenticate(crypto, cancel, callback, handler, new FaceAuthenticateOptions.Builder().setUserId(userId).build());
    }

    public void authenticate(CryptoObject crypto, CancellationSignal cancel, AuthenticationCallback callback, Handler handler, FaceAuthenticateOptions options) {
        if (callback == null) {
            throw new IllegalArgumentException("Must supply an authentication callback");
        }
        if (cancel != null && cancel.isCanceled()) {
            Slog.m90w(TAG, "authentication already canceled");
            return;
        }
        options.setOpPackageName(this.mContext.getOpPackageName());
        options.setAttributionTag(this.mContext.getAttributionTag());
        try {
            if (this.mService != null) {
                try {
                    useHandler(handler);
                    this.mAuthenticationCallback = callback;
                    this.mCryptoObject = crypto;
                    long operationId = crypto != null ? crypto.getOpId() : 0L;
                    Trace.beginSection("FaceManager#authenticate");
                    long authId = this.mService.authenticate(this.mToken, operationId, this.mServiceReceiver, options);
                    if (cancel != null) {
                        cancel.setOnCancelListener(new OnAuthenticationCancelListener(authId));
                    }
                } catch (RemoteException e) {
                    Slog.m89w(TAG, "Remote exception while authenticating: ", e);
                    callback.onAuthenticationError(1, getErrorString(this.mContext, 1, 0));
                }
            }
        } finally {
            Trace.endSection();
        }
    }

    public void detectFace(CancellationSignal cancel, FaceDetectionCallback callback, FaceAuthenticateOptions options) {
        if (this.mService == null) {
            return;
        }
        if (cancel.isCanceled()) {
            Slog.m90w(TAG, "Detection already cancelled");
            return;
        }
        options.setOpPackageName(this.mContext.getOpPackageName());
        options.setAttributionTag(this.mContext.getAttributionTag());
        this.mFaceDetectionCallback = callback;
        try {
            long authId = this.mService.detectFace(this.mToken, this.mServiceReceiver, options);
            cancel.setOnCancelListener(new OnFaceDetectionCancelListener(authId));
        } catch (RemoteException e) {
            Slog.m89w(TAG, "Remote exception when requesting finger detect", e);
        }
    }

    public void enroll(int userId, byte[] hardwareAuthToken, CancellationSignal cancel, EnrollmentCallback callback, int[] disabledFeatures) {
        enroll(userId, hardwareAuthToken, cancel, callback, disabledFeatures, null, false);
    }

    public void enroll(int userId, byte[] hardwareAuthToken, CancellationSignal cancel, EnrollmentCallback callback, int[] disabledFeatures, Surface previewSurface, boolean debugConsent) {
        if (callback == null) {
            throw new IllegalArgumentException("Must supply an enrollment callback");
        }
        if (cancel != null && cancel.isCanceled()) {
            Slog.m90w(TAG, "enrollment already canceled");
            return;
        }
        try {
            if (this.mService != null) {
                try {
                    this.mEnrollmentCallback = callback;
                    Trace.beginSection("FaceManager#enroll");
                    long enrollId = this.mService.enroll(userId, this.mToken, hardwareAuthToken, this.mServiceReceiver, this.mContext.getOpPackageName(), disabledFeatures, previewSurface, debugConsent);
                    if (cancel != null) {
                        cancel.setOnCancelListener(new OnEnrollCancelListener(enrollId));
                    }
                } catch (RemoteException e) {
                    Slog.m89w(TAG, "Remote exception in enroll: ", e);
                    callback.onEnrollmentError(1, getErrorString(this.mContext, 1, 0));
                }
            }
        } finally {
            Trace.endSection();
        }
    }

    public void enrollRemotely(int userId, byte[] hardwareAuthToken, CancellationSignal cancel, EnrollmentCallback callback, int[] disabledFeatures) {
        if (callback == null) {
            throw new IllegalArgumentException("Must supply an enrollment callback");
        }
        if (cancel != null && cancel.isCanceled()) {
            Slog.m90w(TAG, "enrollRemotely is already canceled.");
        } else if (this.mService != null) {
            try {
                try {
                    this.mEnrollmentCallback = callback;
                    Trace.beginSection("FaceManager#enrollRemotely");
                    long enrolId = this.mService.enrollRemotely(userId, this.mToken, hardwareAuthToken, this.mServiceReceiver, this.mContext.getOpPackageName(), disabledFeatures);
                    if (cancel != null) {
                        cancel.setOnCancelListener(new OnEnrollCancelListener(enrolId));
                    }
                } catch (RemoteException e) {
                    Slog.m89w(TAG, "Remote exception in enrollRemotely: ", e);
                    callback.onEnrollmentError(1, getErrorString(this.mContext, 1, 0));
                }
            } finally {
                Trace.endSection();
            }
        }
    }

    public void generateChallenge(int sensorId, int userId, GenerateChallengeCallback callback) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                this.mGenerateChallengeCallback = callback;
                iFaceService.generateChallenge(this.mToken, sensorId, userId, this.mServiceReceiver, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void generateChallenge(int userId, GenerateChallengeCallback callback) {
        List<FaceSensorPropertiesInternal> faceSensorProperties = getSensorPropertiesInternal();
        if (faceSensorProperties.isEmpty()) {
            Slog.m96e(TAG, "No sensors");
            return;
        }
        int sensorId = faceSensorProperties.get(0).sensorId;
        generateChallenge(sensorId, userId, callback);
    }

    public void revokeChallenge(int sensorId, int userId, long challenge) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                iFaceService.revokeChallenge(this.mToken, sensorId, userId, this.mContext.getOpPackageName(), challenge);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void resetLockout(int sensorId, int userId, byte[] hardwareAuthToken) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                iFaceService.resetLockout(this.mToken, sensorId, userId, hardwareAuthToken, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setFeature(int userId, int feature, boolean enabled, byte[] hardwareAuthToken, SetFeatureCallback callback) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                this.mSetFeatureCallback = callback;
                iFaceService.setFeature(this.mToken, userId, feature, enabled, hardwareAuthToken, this.mServiceReceiver, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void getFeature(int userId, int feature, GetFeatureCallback callback) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                this.mGetFeatureCallback = callback;
                iFaceService.getFeature(this.mToken, userId, feature, this.mServiceReceiver, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void remove(Face face, int userId, RemovalCallback callback) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                this.mRemovalCallback = callback;
                this.mRemovalFace = face;
                iFaceService.remove(this.mToken, face.getBiometricId(), userId, this.mServiceReceiver, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void removeAll(int userId, RemovalCallback callback) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                this.mRemovalCallback = callback;
                iFaceService.removeAll(this.mToken, userId, this.mServiceReceiver, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public List<Face> getEnrolledFaces(int userId) {
        List<FaceSensorPropertiesInternal> faceSensorProperties = getSensorPropertiesInternal();
        if (faceSensorProperties.isEmpty()) {
            Slog.m96e(TAG, "No sensors");
            return new ArrayList();
        }
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                return iFaceService.getEnrolledFaces(faceSensorProperties.get(0).sensorId, userId, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return null;
    }

    public List<Face> getEnrolledFaces() {
        return getEnrolledFaces(UserHandle.myUserId());
    }

    public boolean hasEnrolledTemplates() {
        return hasEnrolledTemplates(UserHandle.myUserId());
    }

    public boolean hasEnrolledTemplates(int userId) {
        List<FaceSensorPropertiesInternal> faceSensorProperties = getSensorPropertiesInternal();
        if (faceSensorProperties.isEmpty()) {
            Slog.m96e(TAG, "No sensors");
            return false;
        }
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                return iFaceService.hasEnrolledFaces(faceSensorProperties.get(0).sensorId, userId, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return false;
    }

    public boolean isHardwareDetected() {
        List<FaceSensorPropertiesInternal> faceSensorProperties = getSensorPropertiesInternal();
        if (faceSensorProperties.isEmpty()) {
            Slog.m96e(TAG, "No sensors");
            return false;
        }
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                return iFaceService.isHardwareDetected(faceSensorProperties.get(0).sensorId, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        Slog.m90w(TAG, "isFaceHardwareDetected(): Service not connected!");
        return false;
    }

    public List<FaceSensorProperties> getSensorProperties() {
        List<FaceSensorProperties> properties = new ArrayList<>();
        List<FaceSensorPropertiesInternal> internalProperties = getSensorPropertiesInternal();
        for (FaceSensorPropertiesInternal internalProp : internalProperties) {
            properties.add(FaceSensorProperties.from(internalProp));
        }
        return properties;
    }

    public List<FaceSensorPropertiesInternal> getSensorPropertiesInternal() {
        IFaceService iFaceService;
        try {
            if (this.mProps.isEmpty() && (iFaceService = this.mService) != null) {
                return iFaceService.getSensorPropertiesInternal(this.mContext.getOpPackageName());
            }
            return this.mProps;
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return this.mProps;
        }
    }

    public void registerBiometricStateListener(BiometricStateListener listener) {
        try {
            this.mService.registerBiometricStateListener(listener);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addAuthenticatorsRegisteredCallback(IFaceAuthenticatorsRegisteredCallback callback) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                iFaceService.addAuthenticatorsRegisteredCallback(callback);
                return;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        Slog.m90w(TAG, "addAuthenticatorsRegisteredCallback(): Service not connected!");
    }

    public int getLockoutModeForUser(int sensorId, int userId) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                return iFaceService.getLockoutModeForUser(sensorId, userId);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return 0;
            }
        }
        return 0;
    }

    public void addLockoutResetCallback(LockoutResetCallback callback) {
        if (this.mService != null) {
            try {
                PowerManager powerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
                this.mService.addLockoutResetCallback(new BinderC10963(powerManager, callback), this.mContext.getOpPackageName());
                return;
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        Slog.m90w(TAG, "addLockoutResetCallback(): Service not connected!");
    }

    /* renamed from: android.hardware.face.FaceManager$3 */
    /* loaded from: classes.dex */
    class BinderC10963 extends IBiometricServiceLockoutResetCallback.Stub {
        final /* synthetic */ LockoutResetCallback val$callback;
        final /* synthetic */ PowerManager val$powerManager;

        BinderC10963(PowerManager powerManager, LockoutResetCallback lockoutResetCallback) {
            this.val$powerManager = powerManager;
            this.val$callback = lockoutResetCallback;
        }

        @Override // android.hardware.biometrics.IBiometricServiceLockoutResetCallback
        public void onLockoutReset(final int sensorId, IRemoteCallback serverCallback) throws RemoteException {
            try {
                final PowerManager.WakeLock wakeLock = this.val$powerManager.newWakeLock(1, "faceLockoutResetCallback");
                wakeLock.acquire();
                Handler handler = FaceManager.this.mHandler;
                final LockoutResetCallback lockoutResetCallback = this.val$callback;
                handler.post(new Runnable() { // from class: android.hardware.face.FaceManager$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        FaceManager.BinderC10963.lambda$onLockoutReset$0(FaceManager.LockoutResetCallback.this, sensorId, wakeLock);
                    }
                });
            } finally {
                serverCallback.sendResult(null);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$onLockoutReset$0(LockoutResetCallback callback, int sensorId, PowerManager.WakeLock wakeLock) {
            try {
                callback.onLockoutReset(sensorId);
            } finally {
                wakeLock.release();
            }
        }
    }

    public void scheduleWatchdog() {
        try {
            this.mService.scheduleWatchdog();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelEnrollment(long requestId) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                iFaceService.cancelEnrollment(this.mToken, requestId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelAuthentication(long requestId) {
        IFaceService iFaceService = this.mService;
        if (iFaceService != null) {
            try {
                iFaceService.cancelAuthentication(this.mToken, this.mContext.getOpPackageName(), requestId);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelFaceDetect(long requestId) {
        IFaceService iFaceService = this.mService;
        if (iFaceService == null) {
            return;
        }
        try {
            iFaceService.cancelFaceDetect(this.mToken, this.mContext.getOpPackageName(), requestId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static String getErrorString(Context context, int errMsg, int vendorCode) {
        switch (errMsg) {
            case 1:
                return context.getString(C4057R.string.face_error_hw_not_available);
            case 2:
                return context.getString(C4057R.string.face_error_unable_to_process);
            case 3:
                return context.getString(C4057R.string.face_error_timeout);
            case 4:
                return context.getString(C4057R.string.face_error_no_space);
            case 5:
                return context.getString(C4057R.string.face_error_canceled);
            case 7:
                return context.getString(C4057R.string.face_error_lockout);
            case 8:
                String[] msgArray = context.getResources().getStringArray(C4057R.array.face_error_vendor);
                if (vendorCode < msgArray.length) {
                    return msgArray[vendorCode];
                }
                break;
            case 9:
                return context.getString(C4057R.string.face_error_lockout_permanent);
            case 10:
                return context.getString(C4057R.string.face_error_user_canceled);
            case 11:
                return context.getString(C4057R.string.face_error_not_enrolled);
            case 12:
                return context.getString(C4057R.string.face_error_hw_not_present);
            case 15:
                return context.getString(C4057R.string.face_error_security_update_required);
            case 16:
                return context.getString(C4057R.string.face_recalibrate_notification_content);
        }
        Slog.m90w(TAG, "Invalid error message: " + errMsg + ", " + vendorCode);
        return context.getString(C4057R.string.face_error_vendor_unknown);
    }

    public static int getMappedAcquiredInfo(int acquireInfo, int vendorCode) {
        switch (acquireInfo) {
            case 0:
                return 0;
            case 1:
            case 2:
            case 3:
                return 2;
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return 1;
            case 10:
            case 11:
            case 12:
            case 13:
                return 2;
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            default:
                return 0;
            case 22:
                return vendorCode + 1000;
        }
    }

    /* loaded from: classes.dex */
    public static class AuthenticationResult {
        private final CryptoObject mCryptoObject;
        private final Face mFace;
        private final boolean mIsStrongBiometric;
        private final int mUserId;

        public AuthenticationResult(CryptoObject crypto, Face face, int userId, boolean isStrongBiometric) {
            this.mCryptoObject = crypto;
            this.mFace = face;
            this.mUserId = userId;
            this.mIsStrongBiometric = isStrongBiometric;
        }

        public CryptoObject getCryptoObject() {
            return this.mCryptoObject;
        }

        public Face getFace() {
            return this.mFace;
        }

        public int getUserId() {
            return this.mUserId;
        }

        public boolean isStrongBiometric() {
            return this.mIsStrongBiometric;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class AuthenticationCallback extends BiometricAuthenticator.AuthenticationCallback {
        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationError(int errorCode, CharSequence errString) {
        }

        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        }

        public void onAuthenticationSucceeded(AuthenticationResult result) {
        }

        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationFailed() {
        }

        @Override // android.hardware.biometrics.BiometricAuthenticator.AuthenticationCallback
        public void onAuthenticationAcquired(int acquireInfo) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class EnrollmentCallback {
        public void onEnrollmentError(int errMsgId, CharSequence errString) {
        }

        public void onEnrollmentHelp(int helpMsgId, CharSequence helpString) {
        }

        public void onEnrollmentFrame(int helpCode, CharSequence helpMessage, FaceEnrollCell cell, int stage, float pan, float tilt, float distance) {
            onEnrollmentHelp(helpCode, helpMessage);
        }

        public void onEnrollmentProgress(int remaining) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class RemovalCallback {
        public void onRemovalError(Face face, int errMsgId, CharSequence errString) {
        }

        public void onRemovalSucceeded(Face face, int remaining) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class LockoutResetCallback {
        public void onLockoutReset(int sensorId) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OnEnrollCancelListener implements CancellationSignal.OnCancelListener {
        private final long mAuthRequestId;

        private OnEnrollCancelListener(long id) {
            this.mAuthRequestId = id;
        }

        @Override // android.p008os.CancellationSignal.OnCancelListener
        public void onCancel() {
            Slog.m98d(FaceManager.TAG, "Cancel face enrollment requested for: " + this.mAuthRequestId);
            FaceManager.this.cancelEnrollment(this.mAuthRequestId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class OnAuthenticationCancelListener implements CancellationSignal.OnCancelListener {
        private final long mAuthRequestId;

        OnAuthenticationCancelListener(long id) {
            this.mAuthRequestId = id;
        }

        @Override // android.p008os.CancellationSignal.OnCancelListener
        public void onCancel() {
            Slog.m98d(FaceManager.TAG, "Cancel face authentication requested for: " + this.mAuthRequestId);
            FaceManager.this.cancelAuthentication(this.mAuthRequestId);
        }
    }

    /* loaded from: classes.dex */
    private class OnFaceDetectionCancelListener implements CancellationSignal.OnCancelListener {
        private final long mAuthRequestId;

        OnFaceDetectionCancelListener(long id) {
            this.mAuthRequestId = id;
        }

        @Override // android.p008os.CancellationSignal.OnCancelListener
        public void onCancel() {
            Slog.m98d(FaceManager.TAG, "Cancel face detect requested for: " + this.mAuthRequestId);
            FaceManager.this.cancelFaceDetect(this.mAuthRequestId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyHandler extends Handler {
        private MyHandler(Context context) {
            super(context.getMainLooper());
        }

        private MyHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            Trace.beginSection("FaceManager#handleMessage: " + Integer.toString(msg.what));
            switch (msg.what) {
                case 100:
                    FaceManager.this.sendEnrollResult((Face) msg.obj, msg.arg1);
                    break;
                case 101:
                    FaceManager.this.sendAcquiredResult(msg.arg1, msg.arg2);
                    break;
                case 102:
                    FaceManager.this.sendAuthenticatedSucceeded((Face) msg.obj, msg.arg1, msg.arg2 == 1);
                    break;
                case 103:
                    FaceManager.this.sendAuthenticatedFailed();
                    break;
                case 104:
                    FaceManager.this.sendErrorResult(msg.arg1, msg.arg2);
                    break;
                case 105:
                    FaceManager.this.sendRemovedResult((Face) msg.obj, msg.arg1);
                    break;
                case 106:
                    SomeArgs args = (SomeArgs) msg.obj;
                    FaceManager.this.sendGetFeatureCompleted(((Boolean) args.arg1).booleanValue(), (int[]) args.arg2, (boolean[]) args.arg3);
                    args.recycle();
                    break;
                case 107:
                    FaceManager.this.sendSetFeatureCompleted(((Boolean) msg.obj).booleanValue(), msg.arg1);
                    break;
                case 108:
                    FaceManager.this.sendChallengeGenerated(msg.arg1, msg.arg2, ((Long) msg.obj).longValue());
                    break;
                case 109:
                    FaceManager.this.sendFaceDetected(msg.arg1, msg.arg2, ((Boolean) msg.obj).booleanValue());
                    break;
                case 110:
                case 111:
                default:
                    Slog.m90w(FaceManager.TAG, "Unknown message: " + msg.what);
                    break;
                case 112:
                    FaceManager.this.sendAuthenticationFrame((FaceAuthenticationFrame) msg.obj);
                    break;
                case 113:
                    FaceManager.this.sendEnrollmentFrame((FaceEnrollFrame) msg.obj);
                    break;
            }
            Trace.endSection();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSetFeatureCompleted(boolean success, int feature) {
        SetFeatureCallback setFeatureCallback = this.mSetFeatureCallback;
        if (setFeatureCallback == null) {
            return;
        }
        setFeatureCallback.onCompleted(success, feature);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendGetFeatureCompleted(boolean success, int[] features, boolean[] featureState) {
        GetFeatureCallback getFeatureCallback = this.mGetFeatureCallback;
        if (getFeatureCallback == null) {
            return;
        }
        getFeatureCallback.onCompleted(success, features, featureState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendChallengeGenerated(int sensorId, int userId, long challenge) {
        GenerateChallengeCallback generateChallengeCallback = this.mGenerateChallengeCallback;
        if (generateChallengeCallback == null) {
            return;
        }
        generateChallengeCallback.onGenerateChallengeResult(sensorId, userId, challenge);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendFaceDetected(int sensorId, int userId, boolean isStrongBiometric) {
        FaceDetectionCallback faceDetectionCallback = this.mFaceDetectionCallback;
        if (faceDetectionCallback == null) {
            Slog.m96e(TAG, "sendFaceDetected, callback null");
        } else {
            faceDetectionCallback.onFaceDetected(sensorId, userId, isStrongBiometric);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRemovedResult(Face face, int remaining) {
        RemovalCallback removalCallback = this.mRemovalCallback;
        if (removalCallback == null) {
            return;
        }
        removalCallback.onRemovalSucceeded(face, remaining);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendErrorResult(int errMsgId, int vendorCode) {
        int clientErrMsgId = errMsgId == 8 ? vendorCode + 1000 : errMsgId;
        EnrollmentCallback enrollmentCallback = this.mEnrollmentCallback;
        if (enrollmentCallback != null) {
            enrollmentCallback.onEnrollmentError(clientErrMsgId, getErrorString(this.mContext, errMsgId, vendorCode));
            return;
        }
        AuthenticationCallback authenticationCallback = this.mAuthenticationCallback;
        if (authenticationCallback != null) {
            authenticationCallback.onAuthenticationError(clientErrMsgId, getErrorString(this.mContext, errMsgId, vendorCode));
            return;
        }
        RemovalCallback removalCallback = this.mRemovalCallback;
        if (removalCallback != null) {
            removalCallback.onRemovalError(this.mRemovalFace, clientErrMsgId, getErrorString(this.mContext, errMsgId, vendorCode));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEnrollResult(Face face, int remaining) {
        EnrollmentCallback enrollmentCallback = this.mEnrollmentCallback;
        if (enrollmentCallback != null) {
            enrollmentCallback.onEnrollmentProgress(remaining);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAuthenticatedSucceeded(Face face, int userId, boolean isStrongBiometric) {
        if (this.mAuthenticationCallback != null) {
            AuthenticationResult result = new AuthenticationResult(this.mCryptoObject, face, userId, isStrongBiometric);
            this.mAuthenticationCallback.onAuthenticationSucceeded(result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAuthenticatedFailed() {
        AuthenticationCallback authenticationCallback = this.mAuthenticationCallback;
        if (authenticationCallback != null) {
            authenticationCallback.onAuthenticationFailed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAcquiredResult(int acquireInfo, int vendorCode) {
        if (this.mAuthenticationCallback != null) {
            FaceAuthenticationFrame frame = new FaceAuthenticationFrame(new FaceDataFrame(acquireInfo, vendorCode));
            sendAuthenticationFrame(frame);
        } else if (this.mEnrollmentCallback != null) {
            FaceEnrollFrame frame2 = new FaceEnrollFrame(null, 0, new FaceDataFrame(acquireInfo, vendorCode));
            sendEnrollmentFrame(frame2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendAuthenticationFrame(FaceAuthenticationFrame frame) {
        if (frame == null) {
            Slog.m90w(TAG, "Received null authentication frame");
        } else if (this.mAuthenticationCallback != null) {
            int acquireInfo = frame.getData().getAcquiredInfo();
            int vendorCode = frame.getData().getVendorCode();
            int helpCode = getHelpCode(acquireInfo, vendorCode);
            String helpMessage = getAuthHelpMessage(this.mContext, acquireInfo, vendorCode);
            this.mAuthenticationCallback.onAuthenticationAcquired(acquireInfo);
            if (helpMessage != null) {
                this.mAuthenticationCallback.onAuthenticationHelp(helpCode, helpMessage);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEnrollmentFrame(FaceEnrollFrame frame) {
        if (frame == null) {
            Slog.m90w(TAG, "Received null enrollment frame");
        } else if (this.mEnrollmentCallback != null) {
            FaceDataFrame data = frame.getData();
            int acquireInfo = data.getAcquiredInfo();
            int vendorCode = data.getVendorCode();
            int helpCode = getHelpCode(acquireInfo, vendorCode);
            String helpMessage = getEnrollHelpMessage(this.mContext, acquireInfo, vendorCode);
            this.mEnrollmentCallback.onEnrollmentFrame(helpCode, helpMessage, frame.getCell(), frame.getStage(), data.getPan(), data.getTilt(), data.getDistance());
        }
    }

    private static int getHelpCode(int acquireInfo, int vendorCode) {
        if (acquireInfo == 22) {
            return vendorCode + 1000;
        }
        return acquireInfo;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static String getAuthHelpMessage(Context context, int acquireInfo, int vendorCode) {
        switch (acquireInfo) {
            case 0:
            case 20:
                return null;
            case 1:
                return context.getString(C4057R.string.face_acquired_insufficient);
            case 2:
                return context.getString(C4057R.string.face_acquired_too_bright);
            case 3:
                return context.getString(C4057R.string.face_acquired_too_dark);
            case 4:
                return context.getString(C4057R.string.face_acquired_too_close);
            case 5:
                return context.getString(C4057R.string.face_acquired_too_far);
            case 6:
                return context.getString(C4057R.string.face_acquired_too_low);
            case 7:
                return context.getString(C4057R.string.face_acquired_too_high);
            case 8:
                return context.getString(C4057R.string.face_acquired_too_left);
            case 9:
                return context.getString(C4057R.string.face_acquired_too_right);
            case 10:
                return context.getString(C4057R.string.face_acquired_poor_gaze);
            case 11:
                return context.getString(C4057R.string.face_acquired_not_detected);
            case 12:
                return context.getString(C4057R.string.face_acquired_too_much_motion);
            case 13:
                return context.getString(C4057R.string.face_acquired_recalibrate);
            case 14:
                return context.getString(C4057R.string.face_acquired_too_different);
            case 15:
                return context.getString(C4057R.string.face_acquired_too_similar);
            case 16:
                return context.getString(C4057R.string.face_acquired_pan_too_extreme);
            case 17:
                return context.getString(C4057R.string.face_acquired_tilt_too_extreme);
            case 18:
                return context.getString(C4057R.string.face_acquired_roll_too_extreme);
            case 19:
                return context.getString(C4057R.string.face_acquired_obscured);
            case 21:
                return context.getString(C4057R.string.face_acquired_sensor_dirty);
            case 22:
                String[] msgArray = context.getResources().getStringArray(C4057R.array.face_acquired_vendor);
                if (vendorCode < msgArray.length) {
                    return msgArray[vendorCode];
                }
                break;
            case 25:
                return context.getString(C4057R.string.face_acquired_dark_glasses_detected);
            case 26:
                return context.getString(C4057R.string.face_acquired_mouth_covering_detected);
        }
        Slog.m90w(TAG, "Unknown authentication acquired message: " + acquireInfo + ", " + vendorCode);
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static String getEnrollHelpMessage(Context context, int acquireInfo, int vendorCode) {
        switch (acquireInfo) {
            case 0:
            case 20:
                return null;
            case 1:
                return context.getString(C4057R.string.face_acquired_insufficient);
            case 2:
                return context.getString(C4057R.string.face_acquired_too_bright);
            case 3:
                return context.getString(C4057R.string.face_acquired_too_dark);
            case 4:
                return context.getString(C4057R.string.face_acquired_too_close);
            case 5:
                return context.getString(C4057R.string.face_acquired_too_far);
            case 6:
                return context.getString(C4057R.string.face_acquired_too_low);
            case 7:
                return context.getString(C4057R.string.face_acquired_too_high);
            case 8:
                return context.getString(C4057R.string.face_acquired_too_left);
            case 9:
                return context.getString(C4057R.string.face_acquired_too_right);
            case 10:
                return context.getString(C4057R.string.face_acquired_poor_gaze);
            case 11:
                return context.getString(C4057R.string.face_acquired_not_detected);
            case 12:
                return context.getString(C4057R.string.face_acquired_too_much_motion);
            case 13:
                return context.getString(C4057R.string.face_acquired_recalibrate);
            case 14:
                return context.getString(C4057R.string.face_acquired_too_different);
            case 15:
                return context.getString(C4057R.string.face_acquired_too_similar);
            case 16:
                return context.getString(C4057R.string.face_acquired_pan_too_extreme);
            case 17:
                return context.getString(C4057R.string.face_acquired_tilt_too_extreme);
            case 18:
                return context.getString(C4057R.string.face_acquired_roll_too_extreme);
            case 19:
                return context.getString(C4057R.string.face_acquired_obscured);
            case 21:
                return context.getString(C4057R.string.face_acquired_sensor_dirty);
            case 22:
                String[] msgArray = context.getResources().getStringArray(C4057R.array.face_acquired_vendor);
                if (vendorCode < msgArray.length) {
                    return msgArray[vendorCode];
                }
                break;
            case 25:
                return context.getString(C4057R.string.face_acquired_dark_glasses_detected);
            case 26:
                return context.getString(C4057R.string.face_acquired_mouth_covering_detected);
        }
        Slog.m90w(TAG, "Unknown enrollment acquired message: " + acquireInfo + ", " + vendorCode);
        return null;
    }
}
