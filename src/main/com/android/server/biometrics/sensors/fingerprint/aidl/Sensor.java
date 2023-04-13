package com.android.server.biometrics.sensors.fingerprint.aidl;

import android.app.ActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.app.UserSwitchObserver;
import android.content.Context;
import android.content.pm.UserInfo;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.fingerprint.ISession;
import android.hardware.biometrics.fingerprint.ISessionCallback;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.keymaster.HardwareAuthToken;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.biometrics.HardwareAuthTokenUtils;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.AcquisitionClient;
import com.android.server.biometrics.sensors.AuthenticationConsumer;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.biometrics.sensors.BiometricScheduler;
import com.android.server.biometrics.sensors.BiometricStateCallback;
import com.android.server.biometrics.sensors.EnumerateConsumer;
import com.android.server.biometrics.sensors.ErrorConsumer;
import com.android.server.biometrics.sensors.LockoutCache;
import com.android.server.biometrics.sensors.LockoutConsumer;
import com.android.server.biometrics.sensors.LockoutResetDispatcher;
import com.android.server.biometrics.sensors.RemovalConsumer;
import com.android.server.biometrics.sensors.StartUserClient;
import com.android.server.biometrics.sensors.StopUserClient;
import com.android.server.biometrics.sensors.UserAwareBiometricScheduler;
import com.android.server.biometrics.sensors.fingerprint.FingerprintUtils;
import com.android.server.biometrics.sensors.fingerprint.GestureAvailabilityDispatcher;
import com.android.server.biometrics.sensors.fingerprint.aidl.Sensor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class Sensor {
    public final Map<Integer, Long> mAuthenticatorIds;
    public final Context mContext;
    public AidlSession mCurrentSession;
    public final Handler mHandler;
    public final Supplier<AidlSession> mLazySession;
    public final LockoutCache mLockoutCache;
    public final FingerprintProvider mProvider;
    public final UserAwareBiometricScheduler mScheduler;
    public final FingerprintSensorPropertiesInternal mSensorProperties;
    public final String mTag;
    public boolean mTestHalEnabled;
    public final IBinder mToken;
    public final UserSwitchObserver mUserSwitchObserver;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class HalSessionCallback extends ISessionCallback.Stub {
        public final Callback mCallback;
        public final Context mContext;
        public final Handler mHandler;
        public final LockoutCache mLockoutCache;
        public final LockoutResetDispatcher mLockoutResetDispatcher;
        public final UserAwareBiometricScheduler mScheduler;
        public final int mSensorId;
        public final String mTag;
        public final int mUserId;

        /* loaded from: classes.dex */
        public interface Callback {
            void onHardwareUnavailable();
        }

        public String getInterfaceHash() {
            return "notfrozen";
        }

        public int getInterfaceVersion() {
            return 3;
        }

        public HalSessionCallback(Context context, Handler handler, String str, UserAwareBiometricScheduler userAwareBiometricScheduler, int i, int i2, LockoutCache lockoutCache, LockoutResetDispatcher lockoutResetDispatcher, Callback callback) {
            this.mContext = context;
            this.mHandler = handler;
            this.mTag = str;
            this.mScheduler = userAwareBiometricScheduler;
            this.mSensorId = i;
            this.mUserId = i2;
            this.mLockoutCache = lockoutCache;
            this.mLockoutResetDispatcher = lockoutResetDispatcher;
            this.mCallback = callback;
        }

        public void onChallengeGenerated(final long j) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onChallengeGenerated$0(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onChallengeGenerated$0(long j) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintGenerateChallengeClient)) {
                String str = this.mTag;
                Slog.e(str, "onChallengeGenerated for wrong client: " + Utils.getClientName(currentClient));
                return;
            }
            ((FingerprintGenerateChallengeClient) currentClient).onChallengeGenerated(this.mSensorId, this.mUserId, j);
        }

        public void onChallengeRevoked(final long j) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onChallengeRevoked$1(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onChallengeRevoked$1(long j) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintRevokeChallengeClient)) {
                String str = this.mTag;
                Slog.e(str, "onChallengeRevoked for wrong client: " + Utils.getClientName(currentClient));
                return;
            }
            ((FingerprintRevokeChallengeClient) currentClient).onChallengeRevoked(this.mSensorId, this.mUserId, j);
        }

        public void onAcquired(final byte b, final int i) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onAcquired$2(b, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAcquired$2(byte b, int i) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AcquisitionClient)) {
                String str = this.mTag;
                Slog.e(str, "onAcquired for non-acquisition client: " + Utils.getClientName(currentClient));
                return;
            }
            ((AcquisitionClient) currentClient).onAcquired(AidlConversionUtils.toFrameworkAcquiredInfo(b), i);
        }

        public void onError(final byte b, final int i) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onError$3(b, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$3(byte b, int i) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            String str = this.mTag;
            Slog.d(str, "onError, client: " + Utils.getClientName(currentClient) + ", error: " + ((int) b) + ", vendorCode: " + i);
            if (!(currentClient instanceof ErrorConsumer)) {
                String str2 = this.mTag;
                Slog.e(str2, "onError for non-error consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((ErrorConsumer) currentClient).onError(AidlConversionUtils.toFrameworkError(b), i);
            if (b == 1) {
                this.mCallback.onHardwareUnavailable();
            }
        }

        public void onEnrollmentProgress(final int i, final int i2) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onEnrollmentProgress$4(i, i2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnrollmentProgress$4(int i, int i2) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintEnrollClient)) {
                String str = this.mTag;
                Slog.e(str, "onEnrollmentProgress for non-enroll client: " + Utils.getClientName(currentClient));
                return;
            }
            int targetUserId = currentClient.getTargetUserId();
            ((FingerprintEnrollClient) currentClient).onEnrollResult(new Fingerprint(FingerprintUtils.getInstance(this.mSensorId).getUniqueName(this.mContext, targetUserId), i, this.mSensorId), i2);
        }

        public void onAuthenticationSucceeded(final int i, final HardwareAuthToken hardwareAuthToken) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onAuthenticationSucceeded$5(i, hardwareAuthToken);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticationSucceeded$5(int i, HardwareAuthToken hardwareAuthToken) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AuthenticationConsumer)) {
                Slog.e(this.mTag, "onAuthenticationSucceeded for non-authentication consumer: " + Utils.getClientName(currentClient));
                return;
            }
            AuthenticationConsumer authenticationConsumer = (AuthenticationConsumer) currentClient;
            Fingerprint fingerprint = new Fingerprint("", i, this.mSensorId);
            byte[] byteArray = HardwareAuthTokenUtils.toByteArray(hardwareAuthToken);
            ArrayList<Byte> arrayList = new ArrayList<>();
            for (byte b : byteArray) {
                arrayList.add(Byte.valueOf(b));
            }
            authenticationConsumer.onAuthenticated(fingerprint, true, arrayList);
        }

        public void onAuthenticationFailed() {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onAuthenticationFailed$6();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticationFailed$6() {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AuthenticationConsumer)) {
                String str = this.mTag;
                Slog.e(str, "onAuthenticationFailed for non-authentication consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((AuthenticationConsumer) currentClient).onAuthenticated(new Fingerprint("", 0, this.mSensorId), false, null);
        }

        public void onLockoutTimed(final long j) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onLockoutTimed$7(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLockoutTimed$7(long j) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof LockoutConsumer)) {
                String str = this.mTag;
                Slog.e(str, "onLockoutTimed for non-lockout consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((LockoutConsumer) currentClient).onLockoutTimed(j);
        }

        public void onLockoutPermanent() {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onLockoutPermanent$8();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLockoutPermanent$8() {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof LockoutConsumer)) {
                String str = this.mTag;
                Slog.e(str, "onLockoutPermanent for non-lockout consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((LockoutConsumer) currentClient).onLockoutPermanent();
        }

        public void onLockoutCleared() {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onLockoutCleared$9();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLockoutCleared$9() {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintResetLockoutClient)) {
                Slog.d(this.mTag, "onLockoutCleared outside of resetLockout by HAL");
                FingerprintResetLockoutClient.resetLocalLockoutStateToNone(this.mSensorId, this.mUserId, this.mLockoutCache, this.mLockoutResetDispatcher);
                return;
            }
            Slog.d(this.mTag, "onLockoutCleared after resetLockout");
            ((FingerprintResetLockoutClient) currentClient).onLockoutCleared();
        }

        public void onInteractionDetected() {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onInteractionDetected$10();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onInteractionDetected$10() {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintDetectClient)) {
                String str = this.mTag;
                Slog.e(str, "onInteractionDetected for non-detect client: " + Utils.getClientName(currentClient));
                return;
            }
            ((FingerprintDetectClient) currentClient).onInteractionDetected();
        }

        public void onEnrollmentsEnumerated(final int[] iArr) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onEnrollmentsEnumerated$11(iArr);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnrollmentsEnumerated$11(int[] iArr) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof EnumerateConsumer)) {
                String str = this.mTag;
                Slog.e(str, "onEnrollmentsEnumerated for non-enumerate consumer: " + Utils.getClientName(currentClient));
                return;
            }
            EnumerateConsumer enumerateConsumer = (EnumerateConsumer) currentClient;
            if (iArr.length > 0) {
                for (int i = 0; i < iArr.length; i++) {
                    enumerateConsumer.onEnumerationResult(new Fingerprint("", iArr[i], this.mSensorId), (iArr.length - i) - 1);
                }
                return;
            }
            enumerateConsumer.onEnumerationResult(null, 0);
        }

        public void onEnrollmentsRemoved(final int[] iArr) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onEnrollmentsRemoved$12(iArr);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnrollmentsRemoved$12(int[] iArr) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof RemovalConsumer)) {
                String str = this.mTag;
                Slog.e(str, "onRemoved for non-removal consumer: " + Utils.getClientName(currentClient));
                return;
            }
            RemovalConsumer removalConsumer = (RemovalConsumer) currentClient;
            if (iArr.length > 0) {
                for (int i = 0; i < iArr.length; i++) {
                    removalConsumer.onRemoved(new Fingerprint("", iArr[i], this.mSensorId), (iArr.length - i) - 1);
                }
                return;
            }
            removalConsumer.onRemoved(null, 0);
        }

        public void onAuthenticatorIdRetrieved(final long j) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onAuthenticatorIdRetrieved$13(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticatorIdRetrieved$13(long j) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintGetAuthenticatorIdClient)) {
                String str = this.mTag;
                Slog.e(str, "onAuthenticatorIdRetrieved for wrong consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((FingerprintGetAuthenticatorIdClient) currentClient).onAuthenticatorIdRetrieved(j);
        }

        public void onAuthenticatorIdInvalidated(final long j) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Sensor.HalSessionCallback.this.lambda$onAuthenticatorIdInvalidated$14(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticatorIdInvalidated$14(long j) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintInvalidationClient)) {
                String str = this.mTag;
                Slog.e(str, "onAuthenticatorIdInvalidated for wrong consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((FingerprintInvalidationClient) currentClient).onAuthenticatorIdInvalidated(j);
        }

        public void onSessionClosed() {
            Handler handler = this.mHandler;
            UserAwareBiometricScheduler userAwareBiometricScheduler = this.mScheduler;
            Objects.requireNonNull(userAwareBiometricScheduler);
            handler.post(new com.android.server.biometrics.sensors.face.aidl.Sensor$HalSessionCallback$$ExternalSyntheticLambda2(userAwareBiometricScheduler));
        }
    }

    public Sensor(String str, FingerprintProvider fingerprintProvider, Context context, Handler handler, FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal, LockoutResetDispatcher lockoutResetDispatcher, GestureAvailabilityDispatcher gestureAvailabilityDispatcher, BiometricContext biometricContext) {
        SynchronousUserSwitchObserver synchronousUserSwitchObserver = new SynchronousUserSwitchObserver() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor.1
            public void onUserSwitching(int i) {
                Sensor.this.mProvider.scheduleInternalCleanup(Sensor.this.mSensorProperties.sensorId, i, null);
            }
        };
        this.mUserSwitchObserver = synchronousUserSwitchObserver;
        this.mTag = str;
        this.mProvider = fingerprintProvider;
        this.mContext = context;
        this.mToken = new Binder();
        this.mHandler = handler;
        this.mSensorProperties = fingerprintSensorPropertiesInternal;
        this.mLockoutCache = new LockoutCache();
        this.mScheduler = new UserAwareBiometricScheduler(str, BiometricScheduler.sensorTypeFromFingerprintProperties(fingerprintSensorPropertiesInternal), gestureAvailabilityDispatcher, new UserAwareBiometricScheduler.CurrentUserRetriever() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$$ExternalSyntheticLambda0
            @Override // com.android.server.biometrics.sensors.UserAwareBiometricScheduler.CurrentUserRetriever
            public final int getCurrentUserId() {
                int lambda$new$0;
                lambda$new$0 = Sensor.this.lambda$new$0();
                return lambda$new$0;
            }
        }, new C06192(biometricContext, lockoutResetDispatcher, fingerprintProvider));
        this.mAuthenticatorIds = new HashMap();
        this.mLazySession = new Supplier() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                AidlSession lambda$new$1;
                lambda$new$1 = Sensor.this.lambda$new$1();
                return lambda$new$1;
            }
        };
        try {
            ActivityManager.getService().registerUserSwitchObserver(synchronousUserSwitchObserver, str);
        } catch (RemoteException unused) {
            Slog.e(this.mTag, "Unable to register user switch observer");
        }
    }

    /* renamed from: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$2 */
    /* loaded from: classes.dex */
    public class C06192 implements UserAwareBiometricScheduler.UserSwitchCallback {
        public final /* synthetic */ BiometricContext val$biometricContext;
        public final /* synthetic */ LockoutResetDispatcher val$lockoutResetDispatcher;
        public final /* synthetic */ FingerprintProvider val$provider;

        public C06192(BiometricContext biometricContext, LockoutResetDispatcher lockoutResetDispatcher, FingerprintProvider fingerprintProvider) {
            this.val$biometricContext = biometricContext;
            this.val$lockoutResetDispatcher = lockoutResetDispatcher;
            this.val$provider = fingerprintProvider;
        }

        @Override // com.android.server.biometrics.sensors.UserAwareBiometricScheduler.UserSwitchCallback
        public StopUserClient<?> getStopUserClient(int i) {
            return new FingerprintStopUserClient(Sensor.this.mContext, Sensor.this.mLazySession, Sensor.this.mToken, i, Sensor.this.mSensorProperties.sensorId, BiometricLogger.ofUnknown(Sensor.this.mContext), this.val$biometricContext, new StopUserClient.UserStoppedCallback() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$2$$ExternalSyntheticLambda0
                @Override // com.android.server.biometrics.sensors.StopUserClient.UserStoppedCallback
                public final void onUserStopped() {
                    Sensor.C06192.this.lambda$getStopUserClient$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getStopUserClient$0() {
            Sensor.this.mCurrentSession = null;
        }

        @Override // com.android.server.biometrics.sensors.UserAwareBiometricScheduler.UserSwitchCallback
        public StartUserClient<?, ?> getStartUserClient(int i) {
            final int i2 = Sensor.this.mSensorProperties.sensorId;
            final HalSessionCallback halSessionCallback = new HalSessionCallback(Sensor.this.mContext, Sensor.this.mHandler, Sensor.this.mTag, Sensor.this.mScheduler, i2, i, Sensor.this.mLockoutCache, this.val$lockoutResetDispatcher, new HalSessionCallback.Callback() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$2$$ExternalSyntheticLambda1
                @Override // com.android.server.biometrics.sensors.fingerprint.aidl.Sensor.HalSessionCallback.Callback
                public final void onHardwareUnavailable() {
                    Sensor.C06192.this.lambda$getStartUserClient$1();
                }
            });
            final FingerprintProvider fingerprintProvider = this.val$provider;
            StartUserClient.UserStartedCallback userStartedCallback = new StartUserClient.UserStartedCallback() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$2$$ExternalSyntheticLambda2
                @Override // com.android.server.biometrics.sensors.StartUserClient.UserStartedCallback
                public final void onUserStarted(int i3, Object obj, int i4) {
                    Sensor.C06192.this.lambda$getStartUserClient$2(halSessionCallback, i2, fingerprintProvider, i3, (ISession) obj, i4);
                }
            };
            Context context = Sensor.this.mContext;
            final FingerprintProvider fingerprintProvider2 = this.val$provider;
            Objects.requireNonNull(fingerprintProvider2);
            return new FingerprintStartUserClient(context, new Supplier() { // from class: com.android.server.biometrics.sensors.fingerprint.aidl.Sensor$2$$ExternalSyntheticLambda3
                @Override // java.util.function.Supplier
                public final Object get() {
                    return FingerprintProvider.this.getHalInstance();
                }
            }, Sensor.this.mToken, i, Sensor.this.mSensorProperties.sensorId, BiometricLogger.ofUnknown(Sensor.this.mContext), this.val$biometricContext, halSessionCallback, userStartedCallback);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getStartUserClient$1() {
            Slog.e(Sensor.this.mTag, "Got ERROR_HW_UNAVAILABLE");
            Sensor.this.mCurrentSession = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getStartUserClient$2(HalSessionCallback halSessionCallback, int i, FingerprintProvider fingerprintProvider, int i2, ISession iSession, int i3) {
            String str = Sensor.this.mTag;
            Slog.d(str, "New session created for user: " + i2 + " with hal version: " + i3);
            Sensor.this.mCurrentSession = new AidlSession(i3, iSession, i2, halSessionCallback);
            if (FingerprintUtils.getInstance(i).isInvalidationInProgress(Sensor.this.mContext, i2)) {
                String str2 = Sensor.this.mTag;
                Slog.w(str2, "Scheduling unfinished invalidation request for sensor: " + i + ", user: " + i2);
                fingerprintProvider.scheduleInvalidationRequest(i, i2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$new$0() {
        AidlSession aidlSession = this.mCurrentSession;
        if (aidlSession != null) {
            return aidlSession.getUserId();
        }
        return -10000;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ AidlSession lambda$new$1() {
        AidlSession aidlSession = this.mCurrentSession;
        if (aidlSession != null) {
            return aidlSession;
        }
        return null;
    }

    public Supplier<AidlSession> getLazySession() {
        return this.mLazySession;
    }

    public FingerprintSensorPropertiesInternal getSensorProperties() {
        return this.mSensorProperties;
    }

    public AidlSession getSessionForUser(int i) {
        AidlSession aidlSession = this.mCurrentSession;
        if (aidlSession == null || aidlSession.getUserId() != i) {
            return null;
        }
        return this.mCurrentSession;
    }

    public ITestSession createTestSession(ITestSessionCallback iTestSessionCallback, BiometricStateCallback biometricStateCallback) {
        return new BiometricTestSessionImpl(this.mContext, this.mSensorProperties.sensorId, iTestSessionCallback, biometricStateCallback, this.mProvider, this);
    }

    public BiometricScheduler getScheduler() {
        return this.mScheduler;
    }

    public LockoutCache getLockoutCache() {
        return this.mLockoutCache;
    }

    public Map<Integer, Long> getAuthenticatorIds() {
        return this.mAuthenticatorIds;
    }

    public void setTestHalEnabled(boolean z) {
        String str = this.mTag;
        Slog.w(str, "setTestHalEnabled: " + z);
        if (z != this.mTestHalEnabled) {
            try {
                if (this.mCurrentSession != null) {
                    Slog.d(this.mTag, "Closing old session");
                    this.mCurrentSession.getSession().close();
                }
            } catch (RemoteException e) {
                Slog.e(this.mTag, "RemoteException", e);
            }
            this.mCurrentSession = null;
        }
        this.mTestHalEnabled = z;
    }

    public void dumpProtoState(int i, ProtoOutputStream protoOutputStream, boolean z) {
        long start = protoOutputStream.start(2246267895809L);
        protoOutputStream.write(1120986464257L, this.mSensorProperties.sensorId);
        protoOutputStream.write(1159641169922L, 1);
        if (this.mSensorProperties.isAnyUdfpsType()) {
            protoOutputStream.write(2259152797704L, 0);
        }
        protoOutputStream.write(1120986464259L, Utils.getCurrentStrength(this.mSensorProperties.sensorId));
        protoOutputStream.write(1146756268036L, this.mScheduler.dumpProtoState(z));
        for (UserInfo userInfo : UserManager.get(this.mContext).getUsers()) {
            int identifier = userInfo.getUserHandle().getIdentifier();
            long start2 = protoOutputStream.start(2246267895813L);
            protoOutputStream.write(1120986464257L, identifier);
            protoOutputStream.write(1120986464258L, FingerprintUtils.getInstance(this.mSensorProperties.sensorId).getBiometricsForUser(this.mContext, identifier).size());
            protoOutputStream.end(start2);
        }
        protoOutputStream.write(1133871366150L, this.mSensorProperties.resetLockoutRequiresHardwareAuthToken);
        protoOutputStream.write(1133871366151L, this.mSensorProperties.resetLockoutRequiresChallenge);
        protoOutputStream.end(start);
    }

    public void onBinderDied() {
        BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
        if (currentClient instanceof ErrorConsumer) {
            String str = this.mTag;
            Slog.e(str, "Sending ERROR_HW_UNAVAILABLE for client: " + currentClient);
            ((ErrorConsumer) currentClient).onError(1, 0);
            FrameworkStatsLog.write(148, 1, 1, -1);
        }
        this.mScheduler.recordCrashState();
        this.mScheduler.reset();
        this.mCurrentSession = null;
    }
}
