package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.SynchronousUserSwitchObserver;
import android.app.TaskStackListener;
import android.app.UserSwitchObserver;
import android.content.Context;
import android.content.pm.UserInfo;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.fingerprint.PointerContext;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.hardware.biometrics.fingerprint.V2_2.IBiometricsFingerprintClientCallback;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintAuthenticateOptions;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.hardware.fingerprint.ISidefpsController;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.os.Handler;
import android.os.IBinder;
import android.os.IHwBinder;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.AcquisitionClient;
import com.android.server.biometrics.sensors.AuthenticationClient;
import com.android.server.biometrics.sensors.AuthenticationConsumer;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.biometrics.sensors.BiometricScheduler;
import com.android.server.biometrics.sensors.BiometricStateCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.ClientMonitorCompositeCallback;
import com.android.server.biometrics.sensors.EnumerateConsumer;
import com.android.server.biometrics.sensors.ErrorConsumer;
import com.android.server.biometrics.sensors.LockoutResetDispatcher;
import com.android.server.biometrics.sensors.PerformanceTracker;
import com.android.server.biometrics.sensors.RemovalConsumer;
import com.android.server.biometrics.sensors.fingerprint.FingerprintUtils;
import com.android.server.biometrics.sensors.fingerprint.GestureAvailabilityDispatcher;
import com.android.server.biometrics.sensors.fingerprint.ServiceProvider;
import com.android.server.biometrics.sensors.fingerprint.Udfps;
import com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21;
import com.android.server.biometrics.sensors.fingerprint.hidl.LockoutFrameworkImpl;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class Fingerprint21 implements IHwBinder.DeathRecipient, ServiceProvider {
    public final ActivityTaskManager mActivityTaskManager;
    public final Map<Integer, Long> mAuthenticatorIds;
    public final BiometricContext mBiometricContext;
    public final BiometricStateCallback mBiometricStateCallback;
    public final Context mContext;
    public IBiometricsFingerprint mDaemon;
    public final HalResultController mHalResultController;
    public final Handler mHandler;
    public final boolean mIsPowerbuttonFps;
    public final boolean mIsUdfps;
    public final Supplier<IBiometricsFingerprint> mLazyDaemon;
    public final LockoutFrameworkImpl.LockoutResetCallback mLockoutResetCallback;
    public final LockoutResetDispatcher mLockoutResetDispatcher;
    public final LockoutFrameworkImpl mLockoutTracker;
    public final BiometricScheduler mScheduler;
    public final int mSensorId;
    public final FingerprintSensorPropertiesInternal mSensorProperties;
    public ISidefpsController mSidefpsController;
    public final BiometricTaskStackListener mTaskStackListener;
    public boolean mTestHalEnabled;
    public IUdfpsOverlay mUdfpsOverlay;
    public IUdfpsOverlayController mUdfpsOverlayController;
    public final UserSwitchObserver mUserSwitchObserver;
    public final AtomicLong mRequestCounter = new AtomicLong(0);
    public int mCurrentUserId = -10000;

    /* loaded from: classes.dex */
    public final class BiometricTaskStackListener extends TaskStackListener {
        public BiometricTaskStackListener() {
        }

        public void onTaskStackChanged() {
            Fingerprint21.this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$BiometricTaskStackListener$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Fingerprint21.BiometricTaskStackListener.this.lambda$onTaskStackChanged$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTaskStackChanged$0() {
            BaseClientMonitor currentClient = Fingerprint21.this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AuthenticationClient)) {
                Slog.e("Fingerprint21", "Task stack changed for client: " + currentClient);
            } else if (Utils.isKeyguard(Fingerprint21.this.mContext, currentClient.getOwnerString()) || Utils.isSystem(Fingerprint21.this.mContext, currentClient.getOwnerString()) || !Utils.isBackground(currentClient.getOwnerString()) || currentClient.isAlreadyDone()) {
            } else {
                Slog.e("Fingerprint21", "Stopping background authentication, currentClient: " + currentClient);
                Fingerprint21.this.mScheduler.cancelAuthenticationOrDetection(currentClient.getToken(), currentClient.getRequestId());
            }
        }
    }

    /* loaded from: classes.dex */
    public static class HalResultController extends IBiometricsFingerprintClientCallback.Stub {
        public Callback mCallback;
        public final Context mContext;
        public final Handler mHandler;
        public final BiometricScheduler mScheduler;
        public final int mSensorId;

        /* loaded from: classes.dex */
        public interface Callback {
            void onHardwareUnavailable();
        }

        public HalResultController(int i, Context context, Handler handler, BiometricScheduler biometricScheduler) {
            this.mSensorId = i;
            this.mContext = context;
            this.mHandler = handler;
            this.mScheduler = biometricScheduler;
        }

        public void setCallback(Callback callback) {
            this.mCallback = callback;
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback
        public void onEnrollResult(final long j, final int i, final int i2, final int i3) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$HalResultController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Fingerprint21.HalResultController.this.lambda$onEnrollResult$0(i2, i, j, i3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnrollResult$0(int i, int i2, long j, int i3) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintEnrollClient)) {
                Slog.e("Fingerprint21", "onEnrollResult for non-enroll client: " + Utils.getClientName(currentClient));
                return;
            }
            int targetUserId = currentClient.getTargetUserId();
            ((FingerprintEnrollClient) currentClient).onEnrollResult(new Fingerprint(FingerprintUtils.getLegacyInstance(this.mSensorId).getUniqueName(this.mContext, targetUserId), i, i2, j), i3);
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback
        public void onAcquired(long j, int i, int i2) {
            onAcquired_2_2(j, i, i2);
        }

        @Override // android.hardware.biometrics.fingerprint.V2_2.IBiometricsFingerprintClientCallback
        public void onAcquired_2_2(long j, final int i, final int i2) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$HalResultController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Fingerprint21.HalResultController.this.lambda$onAcquired_2_2$1(i, i2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAcquired_2_2$1(int i, int i2) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AcquisitionClient)) {
                Slog.e("Fingerprint21", "onAcquired for non-acquisition client: " + Utils.getClientName(currentClient));
                return;
            }
            ((AcquisitionClient) currentClient).onAcquired(i, i2);
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback
        public void onAuthenticated(final long j, final int i, final int i2, final ArrayList<Byte> arrayList) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$HalResultController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Fingerprint21.HalResultController.this.lambda$onAuthenticated$2(i, i2, j, arrayList);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticated$2(int i, int i2, long j, ArrayList arrayList) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AuthenticationConsumer)) {
                Slog.e("Fingerprint21", "onAuthenticated for non-authentication consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((AuthenticationConsumer) currentClient).onAuthenticated(new Fingerprint("", i2, i, j), i != 0, arrayList);
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback
        public void onError(long j, final int i, final int i2) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$HalResultController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    Fingerprint21.HalResultController.this.lambda$onError$3(i, i2);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$3(int i, int i2) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            Slog.d("Fingerprint21", "handleError, client: " + Utils.getClientName(currentClient) + ", error: " + i + ", vendorCode: " + i2);
            if (!(currentClient instanceof ErrorConsumer)) {
                Slog.e("Fingerprint21", "onError for non-error consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((ErrorConsumer) currentClient).onError(i, i2);
            if (i == 1) {
                Slog.e("Fingerprint21", "Got ERROR_HW_UNAVAILABLE");
                Callback callback = this.mCallback;
                if (callback != null) {
                    callback.onHardwareUnavailable();
                }
            }
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback
        public void onRemoved(final long j, final int i, final int i2, final int i3) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$HalResultController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Fingerprint21.HalResultController.this.lambda$onRemoved$4(i, i3, i2, j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRemoved$4(int i, int i2, int i3, long j) {
            Slog.d("Fingerprint21", "Removed, fingerId: " + i + ", remaining: " + i2);
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof RemovalConsumer)) {
                Slog.e("Fingerprint21", "onRemoved for non-removal consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((RemovalConsumer) currentClient).onRemoved(new Fingerprint("", i3, i, j), i2);
        }

        @Override // android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback
        public void onEnumerate(final long j, final int i, final int i2, final int i3) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$HalResultController$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    Fingerprint21.HalResultController.this.lambda$onEnumerate$5(i2, i, j, i3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnumerate$5(int i, int i2, long j, int i3) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof EnumerateConsumer)) {
                Slog.e("Fingerprint21", "onEnumerate for non-enumerate consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((EnumerateConsumer) currentClient).onEnumerationResult(new Fingerprint("", i, i2, j), i3);
        }
    }

    @VisibleForTesting
    public Fingerprint21(Context context, BiometricStateCallback biometricStateCallback, FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal, BiometricScheduler biometricScheduler, Handler handler, LockoutResetDispatcher lockoutResetDispatcher, HalResultController halResultController, BiometricContext biometricContext) {
        LockoutFrameworkImpl.LockoutResetCallback lockoutResetCallback = new LockoutFrameworkImpl.LockoutResetCallback() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21.1
            @Override // com.android.server.biometrics.sensors.fingerprint.hidl.LockoutFrameworkImpl.LockoutResetCallback
            public void onLockoutReset(int i) {
                Fingerprint21.this.mLockoutResetDispatcher.notifyLockoutResetCallbacks(Fingerprint21.this.mSensorProperties.sensorId);
            }
        };
        this.mLockoutResetCallback = lockoutResetCallback;
        SynchronousUserSwitchObserver synchronousUserSwitchObserver = new SynchronousUserSwitchObserver() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21.2
            public void onUserSwitching(int i) {
                Fingerprint21.this.scheduleInternalCleanup(i, null);
            }
        };
        this.mUserSwitchObserver = synchronousUserSwitchObserver;
        this.mContext = context;
        this.mBiometricStateCallback = biometricStateCallback;
        this.mBiometricContext = biometricContext;
        this.mSensorProperties = fingerprintSensorPropertiesInternal;
        this.mSensorId = fingerprintSensorPropertiesInternal.sensorId;
        int i = fingerprintSensorPropertiesInternal.sensorType;
        this.mIsUdfps = i == 3 || i == 2;
        this.mIsPowerbuttonFps = i == 4;
        this.mScheduler = biometricScheduler;
        this.mHandler = handler;
        this.mActivityTaskManager = ActivityTaskManager.getInstance();
        this.mTaskStackListener = new BiometricTaskStackListener();
        this.mAuthenticatorIds = Collections.synchronizedMap(new HashMap());
        this.mLazyDaemon = new Supplier() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda4
            @Override // java.util.function.Supplier
            public final Object get() {
                return Fingerprint21.this.getDaemon();
            }
        };
        this.mLockoutResetDispatcher = lockoutResetDispatcher;
        this.mLockoutTracker = new LockoutFrameworkImpl(context, lockoutResetCallback);
        this.mHalResultController = halResultController;
        halResultController.setCallback(new HalResultController.Callback() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda5
            @Override // com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21.HalResultController.Callback
            public final void onHardwareUnavailable() {
                Fingerprint21.this.lambda$new$0();
            }
        });
        try {
            ActivityManager.getService().registerUserSwitchObserver(synchronousUserSwitchObserver, "Fingerprint21");
        } catch (RemoteException unused) {
            Slog.e("Fingerprint21", "Unable to register user switch observer");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mDaemon = null;
        this.mCurrentUserId = -10000;
    }

    public static Fingerprint21 newInstance(Context context, BiometricStateCallback biometricStateCallback, FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal, Handler handler, LockoutResetDispatcher lockoutResetDispatcher, GestureAvailabilityDispatcher gestureAvailabilityDispatcher) {
        BiometricScheduler biometricScheduler = new BiometricScheduler("Fingerprint21", BiometricScheduler.sensorTypeFromFingerprintProperties(fingerprintSensorPropertiesInternal), gestureAvailabilityDispatcher);
        return new Fingerprint21(context, biometricStateCallback, fingerprintSensorPropertiesInternal, biometricScheduler, handler, lockoutResetDispatcher, new HalResultController(fingerprintSensorPropertiesInternal.sensorId, context, handler, biometricScheduler), BiometricContext.getInstance(context));
    }

    public void serviceDied(long j) {
        Slog.e("Fingerprint21", "HAL died");
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$serviceDied$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$serviceDied$1() {
        PerformanceTracker.getInstanceForSensorId(this.mSensorProperties.sensorId).incrementHALDeathCount();
        this.mDaemon = null;
        this.mCurrentUserId = -10000;
        BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
        if (currentClient instanceof ErrorConsumer) {
            Slog.e("Fingerprint21", "Sending ERROR_HW_UNAVAILABLE for client: " + currentClient);
            ((ErrorConsumer) currentClient).onError(1, 0);
            FrameworkStatsLog.write(148, 1, 1, -1);
        }
        this.mScheduler.recordCrashState();
        this.mScheduler.reset();
    }

    @VisibleForTesting
    public synchronized IBiometricsFingerprint getDaemon() {
        long j;
        if (this.mTestHalEnabled) {
            TestHal testHal = new TestHal(this.mContext, this.mSensorId);
            testHal.setNotify(this.mHalResultController);
            return testHal;
        }
        IBiometricsFingerprint iBiometricsFingerprint = this.mDaemon;
        if (iBiometricsFingerprint != null) {
            return iBiometricsFingerprint;
        }
        Slog.d("Fingerprint21", "Daemon was null, reconnecting, current operation: " + this.mScheduler.getCurrentClient());
        try {
            this.mDaemon = IBiometricsFingerprint.getService();
        } catch (RemoteException e) {
            Slog.e("Fingerprint21", "Failed to get fingerprint HAL", e);
        } catch (NoSuchElementException e2) {
            Slog.w("Fingerprint21", "NoSuchElementException", e2);
        }
        IBiometricsFingerprint iBiometricsFingerprint2 = this.mDaemon;
        if (iBiometricsFingerprint2 == null) {
            Slog.w("Fingerprint21", "Fingerprint HAL not available");
            return null;
        }
        iBiometricsFingerprint2.asBinder().linkToDeath(this, 0L);
        try {
            j = this.mDaemon.setNotify(this.mHalResultController);
        } catch (RemoteException e3) {
            Slog.e("Fingerprint21", "Failed to set callback for fingerprint HAL", e3);
            this.mDaemon = null;
            j = 0;
        }
        Slog.d("Fingerprint21", "Fingerprint HAL ready, HAL ID: " + j);
        if (j != 0) {
            scheduleLoadAuthenticatorIds();
            scheduleInternalCleanup(ActivityManager.getCurrentUser(), null);
        } else {
            Slog.e("Fingerprint21", "Unable to set callback");
            this.mDaemon = null;
        }
        return this.mDaemon;
    }

    public IUdfpsOverlayController getUdfpsOverlayController() {
        return this.mUdfpsOverlayController;
    }

    public final void scheduleLoadAuthenticatorIds() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleLoadAuthenticatorIds$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleLoadAuthenticatorIds$2() {
        for (UserInfo userInfo : UserManager.get(this.mContext).getAliveUsers()) {
            int i = userInfo.id;
            if (!this.mAuthenticatorIds.containsKey(Integer.valueOf(i))) {
                scheduleUpdateActiveUserWithoutHandler(i, true);
            }
        }
    }

    public final void scheduleUpdateActiveUserWithoutHandler(int i) {
        scheduleUpdateActiveUserWithoutHandler(i, false);
    }

    public final void scheduleUpdateActiveUserWithoutHandler(final int i, boolean z) {
        Context context = this.mContext;
        this.mScheduler.scheduleClientMonitor(new FingerprintUpdateActiveUserClient(context, this.mLazyDaemon, i, context.getOpPackageName(), this.mSensorProperties.sensorId, createLogger(0, 0), this.mBiometricContext, new Supplier() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda7
            @Override // java.util.function.Supplier
            public final Object get() {
                int currentUser;
                currentUser = Fingerprint21.this.getCurrentUser();
                return Integer.valueOf(currentUser);
            }
        }, !getEnrolledFingerprints(this.mSensorProperties.sensorId, i).isEmpty(), this.mAuthenticatorIds, z), new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21.3
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z2) {
                if (z2) {
                    Fingerprint21.this.mCurrentUserId = i;
                    return;
                }
                Slog.w("Fingerprint21", "Failed to change user, still: " + Fingerprint21.this.mCurrentUserId);
            }
        });
    }

    public final int getCurrentUser() {
        return this.mCurrentUserId;
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public boolean containsSensor(int i) {
        return this.mSensorProperties.sensorId == i;
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public List<FingerprintSensorPropertiesInternal> getSensorProperties() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mSensorProperties);
        return arrayList;
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public FingerprintSensorPropertiesInternal getSensorProperties(int i) {
        return this.mSensorProperties;
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void scheduleResetLockout(final int i, final int i2, byte[] bArr) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleResetLockout$3(i2, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleResetLockout$3(int i, int i2) {
        Context context = this.mContext;
        this.mScheduler.scheduleClientMonitor(new FingerprintResetLockoutClient(context, i, context.getOpPackageName(), i2, createLogger(0, 0), this.mBiometricContext, this.mLockoutTracker));
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void scheduleGenerateChallenge(int i, final int i2, final IBinder iBinder, final IFingerprintServiceReceiver iFingerprintServiceReceiver, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleGenerateChallenge$4(iBinder, iFingerprintServiceReceiver, i2, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleGenerateChallenge$4(IBinder iBinder, IFingerprintServiceReceiver iFingerprintServiceReceiver, int i, String str) {
        this.mScheduler.scheduleClientMonitor(new FingerprintGenerateChallengeClient(this.mContext, this.mLazyDaemon, iBinder, new ClientMonitorCallbackConverter(iFingerprintServiceReceiver), i, str, this.mSensorProperties.sensorId, createLogger(0, 0), this.mBiometricContext));
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void scheduleRevokeChallenge(int i, final int i2, final IBinder iBinder, final String str, long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleRevokeChallenge$5(iBinder, i2, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleRevokeChallenge$5(IBinder iBinder, int i, String str) {
        this.mScheduler.scheduleClientMonitor(new FingerprintRevokeChallengeClient(this.mContext, this.mLazyDaemon, iBinder, i, str, this.mSensorProperties.sensorId, createLogger(0, 0), this.mBiometricContext));
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public long scheduleEnroll(int i, final IBinder iBinder, final byte[] bArr, final int i2, final IFingerprintServiceReceiver iFingerprintServiceReceiver, final String str, final int i3) {
        final long incrementAndGet = this.mRequestCounter.incrementAndGet();
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleEnroll$6(i2, iBinder, incrementAndGet, iFingerprintServiceReceiver, bArr, str, i3);
            }
        });
        return incrementAndGet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleEnroll$6(int i, IBinder iBinder, long j, IFingerprintServiceReceiver iFingerprintServiceReceiver, byte[] bArr, String str, int i2) {
        scheduleUpdateActiveUserWithoutHandler(i);
        this.mScheduler.scheduleClientMonitor(new FingerprintEnrollClient(this.mContext, this.mLazyDaemon, iBinder, j, new ClientMonitorCallbackConverter(iFingerprintServiceReceiver), i, bArr, str, FingerprintUtils.getLegacyInstance(this.mSensorId), 60, this.mSensorProperties.sensorId, createLogger(1, 0), this.mBiometricContext, this.mUdfpsOverlayController, this.mSidefpsController, this.mUdfpsOverlay, i2), new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21.4
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientStarted(BaseClientMonitor baseClientMonitor) {
                Fingerprint21.this.mBiometricStateCallback.onClientStarted(baseClientMonitor);
            }

            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onBiometricAction(int i3) {
                Fingerprint21.this.mBiometricStateCallback.onBiometricAction(i3);
            }

            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                Fingerprint21.this.mBiometricStateCallback.onClientFinished(baseClientMonitor, z);
                if (z) {
                    Fingerprint21.this.scheduleUpdateActiveUserWithoutHandler(baseClientMonitor.getTargetUserId(), true);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelEnrollment$7(IBinder iBinder, long j) {
        this.mScheduler.cancelEnrollment(iBinder, j);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void cancelEnrollment(int i, final IBinder iBinder, final long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$cancelEnrollment$7(iBinder, j);
            }
        });
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public long scheduleFingerDetect(final IBinder iBinder, final ClientMonitorCallbackConverter clientMonitorCallbackConverter, final FingerprintAuthenticateOptions fingerprintAuthenticateOptions, final int i) {
        final long incrementAndGet = this.mRequestCounter.incrementAndGet();
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleFingerDetect$8(fingerprintAuthenticateOptions, iBinder, incrementAndGet, clientMonitorCallbackConverter, i);
            }
        });
        return incrementAndGet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleFingerDetect$8(FingerprintAuthenticateOptions fingerprintAuthenticateOptions, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, int i) {
        scheduleUpdateActiveUserWithoutHandler(fingerprintAuthenticateOptions.getUserId());
        this.mScheduler.scheduleClientMonitor(new FingerprintDetectClient(this.mContext, this.mLazyDaemon, iBinder, j, clientMonitorCallbackConverter, fingerprintAuthenticateOptions, createLogger(2, i), this.mBiometricContext, this.mUdfpsOverlayController, this.mUdfpsOverlay, Utils.isStrongBiometric(this.mSensorProperties.sensorId)), this.mBiometricStateCallback);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void scheduleAuthenticate(final IBinder iBinder, final long j, final int i, final ClientMonitorCallbackConverter clientMonitorCallbackConverter, final FingerprintAuthenticateOptions fingerprintAuthenticateOptions, final long j2, final boolean z, final int i2, final boolean z2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleAuthenticate$9(fingerprintAuthenticateOptions, iBinder, j2, clientMonitorCallbackConverter, j, z, i, i2, z2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleAuthenticate$9(FingerprintAuthenticateOptions fingerprintAuthenticateOptions, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, long j2, boolean z, int i, int i2, boolean z2) {
        scheduleUpdateActiveUserWithoutHandler(fingerprintAuthenticateOptions.getUserId());
        this.mScheduler.scheduleClientMonitor(new FingerprintAuthenticationClient(this.mContext, this.mLazyDaemon, iBinder, j, clientMonitorCallbackConverter, j2, z, fingerprintAuthenticateOptions, i, false, createLogger(2, i2), this.mBiometricContext, Utils.isStrongBiometric(this.mSensorProperties.sensorId), this.mTaskStackListener, this.mLockoutTracker, this.mUdfpsOverlayController, this.mSidefpsController, this.mUdfpsOverlay, z2, this.mSensorProperties, Utils.getCurrentStrength(this.mSensorId)), this.mBiometricStateCallback);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public long scheduleAuthenticate(IBinder iBinder, long j, int i, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FingerprintAuthenticateOptions fingerprintAuthenticateOptions, boolean z, int i2, boolean z2) {
        long incrementAndGet = this.mRequestCounter.incrementAndGet();
        scheduleAuthenticate(iBinder, j, i, clientMonitorCallbackConverter, fingerprintAuthenticateOptions, incrementAndGet, z, i2, z2);
        return incrementAndGet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startPreparedClient$10(int i) {
        this.mScheduler.startPreparedClient(i);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void startPreparedClient(int i, final int i2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$startPreparedClient$10(i2);
            }
        });
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void cancelAuthentication(int i, final IBinder iBinder, final long j) {
        Slog.d("Fingerprint21", "cancelAuthentication, sensorId: " + i);
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$cancelAuthentication$11(iBinder, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelAuthentication$11(IBinder iBinder, long j) {
        this.mScheduler.cancelAuthenticationOrDetection(iBinder, j);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void scheduleRemove(int i, final IBinder iBinder, final IFingerprintServiceReceiver iFingerprintServiceReceiver, final int i2, final int i3, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleRemove$12(i3, iBinder, iFingerprintServiceReceiver, i2, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleRemove$12(int i, IBinder iBinder, IFingerprintServiceReceiver iFingerprintServiceReceiver, int i2, String str) {
        scheduleUpdateActiveUserWithoutHandler(i);
        this.mScheduler.scheduleClientMonitor(new FingerprintRemovalClient(this.mContext, this.mLazyDaemon, iBinder, new ClientMonitorCallbackConverter(iFingerprintServiceReceiver), i2, i, str, FingerprintUtils.getLegacyInstance(this.mSensorId), this.mSensorProperties.sensorId, createLogger(4, 0), this.mBiometricContext, this.mAuthenticatorIds), this.mBiometricStateCallback);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void scheduleRemoveAll(int i, final IBinder iBinder, final IFingerprintServiceReceiver iFingerprintServiceReceiver, final int i2, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleRemoveAll$13(i2, iBinder, iFingerprintServiceReceiver, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleRemoveAll$13(int i, IBinder iBinder, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str) {
        scheduleUpdateActiveUserWithoutHandler(i);
        this.mScheduler.scheduleClientMonitor(new FingerprintRemovalClient(this.mContext, this.mLazyDaemon, iBinder, new ClientMonitorCallbackConverter(iFingerprintServiceReceiver), 0, i, str, FingerprintUtils.getLegacyInstance(this.mSensorId), this.mSensorProperties.sensorId, createLogger(4, 0), this.mBiometricContext, this.mAuthenticatorIds), this.mBiometricStateCallback);
    }

    public final void scheduleInternalCleanup(final int i, final ClientMonitorCallback clientMonitorCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$scheduleInternalCleanup$14(i, clientMonitorCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleInternalCleanup$14(int i, ClientMonitorCallback clientMonitorCallback) {
        scheduleUpdateActiveUserWithoutHandler(i);
        Context context = this.mContext;
        this.mScheduler.scheduleClientMonitor(new FingerprintInternalCleanupClient(context, this.mLazyDaemon, i, context.getOpPackageName(), this.mSensorProperties.sensorId, createLogger(3, 0), this.mBiometricContext, FingerprintUtils.getLegacyInstance(this.mSensorId), this.mAuthenticatorIds), clientMonitorCallback);
    }

    public void scheduleInternalCleanup(int i, int i2, ClientMonitorCallback clientMonitorCallback) {
        scheduleInternalCleanup(i2, new ClientMonitorCompositeCallback(clientMonitorCallback, this.mBiometricStateCallback));
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void scheduleInternalCleanup(int i, int i2, ClientMonitorCallback clientMonitorCallback, boolean z) {
        scheduleInternalCleanup(i2, new ClientMonitorCompositeCallback(clientMonitorCallback, this.mBiometricStateCallback));
    }

    public final BiometricLogger createLogger(int i, int i2) {
        return new BiometricLogger(this.mContext, 1, i, i2);
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public boolean isHardwareDetected(int i) {
        return getDaemon() != null;
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void rename(int i, final int i2, final int i3, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21.this.lambda$rename$15(i3, i2, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$rename$15(int i, int i2, String str) {
        FingerprintUtils.getLegacyInstance(this.mSensorId).renameBiometricForUser(this.mContext, i, i2, str);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public List<Fingerprint> getEnrolledFingerprints(int i, int i2) {
        return FingerprintUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, i2);
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public boolean hasEnrollments(int i, int i2) {
        return !getEnrolledFingerprints(i, i2).isEmpty();
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public int getLockoutModeForUser(int i, int i2) {
        return this.mLockoutTracker.getLockoutModeForUser(i2);
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public long getAuthenticatorId(int i, int i2) {
        return this.mAuthenticatorIds.getOrDefault(Integer.valueOf(i2), 0L).longValue();
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void onPointerDown(long j, int i, final PointerContext pointerContext) {
        this.mScheduler.getCurrentClientIfMatches(j, new Consumer() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda18
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Fingerprint21.lambda$onPointerDown$16(pointerContext, (BaseClientMonitor) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$onPointerDown$16(PointerContext pointerContext, BaseClientMonitor baseClientMonitor) {
        if (!(baseClientMonitor instanceof Udfps)) {
            Slog.w("Fingerprint21", "onFingerDown received during client: " + baseClientMonitor);
            return;
        }
        ((Udfps) baseClientMonitor).onPointerDown(pointerContext);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void onPointerUp(long j, int i, final PointerContext pointerContext) {
        this.mScheduler.getCurrentClientIfMatches(j, new Consumer() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda15
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Fingerprint21.lambda$onPointerUp$17(pointerContext, (BaseClientMonitor) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$onPointerUp$17(PointerContext pointerContext, BaseClientMonitor baseClientMonitor) {
        if (!(baseClientMonitor instanceof Udfps)) {
            Slog.w("Fingerprint21", "onFingerDown received during client: " + baseClientMonitor);
            return;
        }
        ((Udfps) baseClientMonitor).onPointerUp(pointerContext);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void onUiReady(long j, int i) {
        this.mScheduler.getCurrentClientIfMatches(j, new Consumer() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21$$ExternalSyntheticLambda9
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Fingerprint21.lambda$onUiReady$18((BaseClientMonitor) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$onUiReady$18(BaseClientMonitor baseClientMonitor) {
        if (!(baseClientMonitor instanceof Udfps)) {
            Slog.w("Fingerprint21", "onUiReady received during client: " + baseClientMonitor);
            return;
        }
        ((Udfps) baseClientMonitor).onUiReady();
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void onPowerPressed() {
        Slog.e("Fingerprint21", "onPowerPressed not supported for HIDL clients");
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void setUdfpsOverlayController(IUdfpsOverlayController iUdfpsOverlayController) {
        this.mUdfpsOverlayController = iUdfpsOverlayController;
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void setSidefpsController(ISidefpsController iSidefpsController) {
        this.mSidefpsController = iSidefpsController;
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void setUdfpsOverlay(IUdfpsOverlay iUdfpsOverlay) {
        this.mUdfpsOverlay = iUdfpsOverlay;
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
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
            protoOutputStream.write(1120986464258L, FingerprintUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, identifier).size());
            protoOutputStream.end(start2);
        }
        protoOutputStream.write(1133871366150L, this.mSensorProperties.resetLockoutRequiresHardwareAuthToken);
        protoOutputStream.write(1133871366151L, this.mSensorProperties.resetLockoutRequiresChallenge);
        protoOutputStream.end(start);
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public void dumpProtoMetrics(int i, FileDescriptor fileDescriptor) {
        PerformanceTracker instanceForSensorId = PerformanceTracker.getInstanceForSensorId(this.mSensorProperties.sensorId);
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        for (UserInfo userInfo : UserManager.get(this.mContext).getUsers()) {
            int identifier = userInfo.getUserHandle().getIdentifier();
            long start = protoOutputStream.start(2246267895809L);
            protoOutputStream.write(1120986464257L, identifier);
            protoOutputStream.write(1120986464258L, FingerprintUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, identifier).size());
            long start2 = protoOutputStream.start(1146756268035L);
            protoOutputStream.write(1120986464257L, instanceForSensorId.getAcceptForUser(identifier));
            protoOutputStream.write(1120986464258L, instanceForSensorId.getRejectForUser(identifier));
            protoOutputStream.write(1120986464259L, instanceForSensorId.getAcquireForUser(identifier));
            protoOutputStream.write(1120986464260L, instanceForSensorId.getTimedLockoutForUser(identifier));
            protoOutputStream.write(1120986464261L, instanceForSensorId.getPermanentLockoutForUser(identifier));
            protoOutputStream.end(start2);
            long start3 = protoOutputStream.start(1146756268036L);
            protoOutputStream.write(1120986464257L, instanceForSensorId.getAcceptCryptoForUser(identifier));
            protoOutputStream.write(1120986464258L, instanceForSensorId.getRejectCryptoForUser(identifier));
            protoOutputStream.write(1120986464259L, instanceForSensorId.getAcquireCryptoForUser(identifier));
            protoOutputStream.write(1120986464260L, 0);
            protoOutputStream.write(1120986464261L, 0);
            protoOutputStream.end(start3);
            protoOutputStream.end(start);
        }
        protoOutputStream.flush();
        instanceForSensorId.clear();
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void scheduleInvalidateAuthenticatorId(int i, int i2, IInvalidationCallback iInvalidationCallback) {
        try {
            iInvalidationCallback.onCompleted();
        } catch (RemoteException unused) {
            Slog.e("Fingerprint21", "Failed to complete InvalidateAuthenticatorId");
        }
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public void dumpInternal(int i, PrintWriter printWriter) {
        PerformanceTracker instanceForSensorId = PerformanceTracker.getInstanceForSensorId(this.mSensorProperties.sensorId);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("service", "Fingerprint21");
            jSONObject.put("isUdfps", this.mIsUdfps);
            jSONObject.put("isPowerbuttonFps", this.mIsPowerbuttonFps);
            JSONArray jSONArray = new JSONArray();
            for (UserInfo userInfo : UserManager.get(this.mContext).getUsers()) {
                int identifier = userInfo.getUserHandle().getIdentifier();
                int size = FingerprintUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, identifier).size();
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("id", identifier);
                jSONObject2.put("count", size);
                jSONObject2.put("accept", instanceForSensorId.getAcceptForUser(identifier));
                jSONObject2.put("reject", instanceForSensorId.getRejectForUser(identifier));
                jSONObject2.put("acquire", instanceForSensorId.getAcquireForUser(identifier));
                jSONObject2.put("lockout", instanceForSensorId.getTimedLockoutForUser(identifier));
                jSONObject2.put("permanentLockout", instanceForSensorId.getPermanentLockoutForUser(identifier));
                jSONObject2.put("acceptCrypto", instanceForSensorId.getAcceptCryptoForUser(identifier));
                jSONObject2.put("rejectCrypto", instanceForSensorId.getRejectCryptoForUser(identifier));
                jSONObject2.put("acquireCrypto", instanceForSensorId.getAcquireCryptoForUser(identifier));
                jSONArray.put(jSONObject2);
            }
            jSONObject.put("prints", jSONArray);
        } catch (JSONException e) {
            Slog.e("Fingerprint21", "dump formatting failure", e);
        }
        printWriter.println(jSONObject);
        printWriter.println("HAL deaths since last reboot: " + instanceForSensorId.getHALDeathCount());
        this.mScheduler.dump(printWriter);
    }

    public void setTestHalEnabled(boolean z) {
        this.mTestHalEnabled = z;
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str) {
        return new BiometricTestSessionImpl(this.mContext, this.mSensorProperties.sensorId, iTestSessionCallback, this.mBiometricStateCallback, this, this.mHalResultController);
    }
}
