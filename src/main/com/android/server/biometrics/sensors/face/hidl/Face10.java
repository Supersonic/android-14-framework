package com.android.server.biometrics.sensors.face.hidl;

import android.app.ActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.app.UserSwitchObserver;
import android.content.Context;
import android.content.pm.UserInfo;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.hardware.biometrics.face.V1_0.IBiometricsFaceClientCallback;
import android.hardware.face.Face;
import android.hardware.face.FaceAuthenticateOptions;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.hardware.face.IFaceServiceReceiver;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.IHwBinder;
import android.os.Looper;
import android.os.NativeHandle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.Surface;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.biometrics.Utils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.AcquisitionClient;
import com.android.server.biometrics.sensors.AuthenticationConsumer;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.biometrics.sensors.BiometricNotificationUtils;
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
import com.android.server.biometrics.sensors.face.FaceUtils;
import com.android.server.biometrics.sensors.face.LockoutHalImpl;
import com.android.server.biometrics.sensors.face.ServiceProvider;
import com.android.server.biometrics.sensors.face.UsageStats;
import com.android.server.biometrics.sensors.face.hidl.Face10;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class Face10 implements IHwBinder.DeathRecipient, ServiceProvider {
    @VisibleForTesting
    public static Clock sSystemClock = Clock.systemUTC();
    public final Map<Integer, Long> mAuthenticatorIds;
    public final BiometricContext mBiometricContext;
    public final BiometricStateCallback mBiometricStateCallback;
    public final Context mContext;
    public IBiometricsFace mDaemon;
    public final HalResultController mHalResultController;
    public final Handler mHandler;
    public final Supplier<IBiometricsFace> mLazyDaemon;
    public final LockoutHalImpl mLockoutTracker;
    public final BiometricScheduler mScheduler;
    public final int mSensorId;
    public final FaceSensorPropertiesInternal mSensorProperties;
    public boolean mTestHalEnabled;
    public final UsageStats mUsageStats;
    public final UserSwitchObserver mUserSwitchObserver;
    public final AtomicLong mRequestCounter = new AtomicLong(0);
    public int mCurrentUserId = -10000;
    public final List<Long> mGeneratedChallengeCount = new ArrayList();
    public FaceGenerateChallengeClient mGeneratedChallengeCache = null;

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public void dumpProtoMetrics(int i, FileDescriptor fileDescriptor) {
    }

    /* loaded from: classes.dex */
    public static class HalResultController extends IBiometricsFaceClientCallback.Stub {
        public Callback mCallback;
        public final Context mContext;
        public final Handler mHandler;
        public final LockoutResetDispatcher mLockoutResetDispatcher;
        public final LockoutHalImpl mLockoutTracker;
        public final BiometricScheduler mScheduler;
        public final int mSensorId;

        /* loaded from: classes.dex */
        public interface Callback {
            void onHardwareUnavailable();
        }

        public HalResultController(int i, Context context, Handler handler, BiometricScheduler biometricScheduler, LockoutHalImpl lockoutHalImpl, LockoutResetDispatcher lockoutResetDispatcher) {
            this.mSensorId = i;
            this.mContext = context;
            this.mHandler = handler;
            this.mScheduler = biometricScheduler;
            this.mLockoutTracker = lockoutHalImpl;
            this.mLockoutResetDispatcher = lockoutResetDispatcher;
        }

        public void setCallback(Callback callback) {
            this.mCallback = callback;
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFaceClientCallback
        public void onEnrollResult(final long j, final int i, final int i2, final int i3) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$HalResultController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Face10.HalResultController.this.lambda$onEnrollResult$0(i2, i, j, i3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnrollResult$0(int i, int i2, long j, int i3) {
            BiometricAuthenticator.Identifier face = new Face(FaceUtils.getLegacyInstance(this.mSensorId).getUniqueName(this.mContext, i), i2, j);
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FaceEnrollClient)) {
                Slog.e("Face10", "onEnrollResult for non-enroll client: " + Utils.getClientName(currentClient));
                return;
            }
            ((FaceEnrollClient) currentClient).onEnrollResult(face, i3);
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFaceClientCallback
        public void onAuthenticated(final long j, final int i, int i2, final ArrayList<Byte> arrayList) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$HalResultController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Face10.HalResultController.this.lambda$onAuthenticated$1(i, j, arrayList);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticated$1(int i, long j, ArrayList arrayList) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AuthenticationConsumer)) {
                Slog.e("Face10", "onAuthenticated for non-authentication consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((AuthenticationConsumer) currentClient).onAuthenticated(new Face("", i, j), i != 0, arrayList);
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFaceClientCallback
        public void onAcquired(long j, int i, final int i2, final int i3) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$HalResultController$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    Face10.HalResultController.this.lambda$onAcquired$2(i2, i3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAcquired$2(int i, int i2) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AcquisitionClient)) {
                Slog.e("Face10", "onAcquired for non-acquire client: " + Utils.getClientName(currentClient));
                return;
            }
            ((AcquisitionClient) currentClient).onAcquired(i, i2);
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFaceClientCallback
        public void onError(long j, int i, final int i2, final int i3) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$HalResultController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    Face10.HalResultController.this.lambda$onError$3(i2, i3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$3(int i, int i2) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            StringBuilder sb = new StringBuilder();
            sb.append("handleError, client: ");
            sb.append(currentClient != null ? currentClient.getOwnerString() : null);
            sb.append(", error: ");
            sb.append(i);
            sb.append(", vendorCode: ");
            sb.append(i2);
            Slog.d("Face10", sb.toString());
            if (!(currentClient instanceof ErrorConsumer)) {
                Slog.e("Face10", "onError for non-error consumer: " + Utils.getClientName(currentClient));
                return;
            }
            ((ErrorConsumer) currentClient).onError(i, i2);
            if (i == 1) {
                Slog.e("Face10", "Got ERROR_HW_UNAVAILABLE");
                Callback callback = this.mCallback;
                if (callback != null) {
                    callback.onHardwareUnavailable();
                }
            }
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFaceClientCallback
        public void onRemoved(final long j, final ArrayList<Integer> arrayList, int i) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$HalResultController$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    Face10.HalResultController.this.lambda$onRemoved$4(arrayList, j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onRemoved$4(ArrayList arrayList, long j) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof RemovalConsumer)) {
                Slog.e("Face10", "onRemoved for non-removal consumer: " + Utils.getClientName(currentClient));
                return;
            }
            RemovalConsumer removalConsumer = (RemovalConsumer) currentClient;
            if (!arrayList.isEmpty()) {
                for (int i = 0; i < arrayList.size(); i++) {
                    int intValue = ((Integer) arrayList.get(i)).intValue();
                    Face face = new Face("", intValue, j);
                    int size = (arrayList.size() - i) - 1;
                    Slog.d("Face10", "Removed, faceId: " + intValue + ", remaining: " + size);
                    removalConsumer.onRemoved(face, size);
                }
            } else {
                removalConsumer.onRemoved(null, 0);
            }
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "face_unlock_re_enroll", 0, -2);
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFaceClientCallback
        public void onEnumerate(final long j, final ArrayList<Integer> arrayList, int i) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$HalResultController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Face10.HalResultController.this.lambda$onEnumerate$5(arrayList, j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onEnumerate$5(ArrayList arrayList, long j) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof EnumerateConsumer)) {
                Slog.e("Face10", "onEnumerate for non-enumerate consumer: " + Utils.getClientName(currentClient));
                return;
            }
            EnumerateConsumer enumerateConsumer = (EnumerateConsumer) currentClient;
            if (!arrayList.isEmpty()) {
                for (int i = 0; i < arrayList.size(); i++) {
                    enumerateConsumer.onEnumerationResult(new Face("", ((Integer) arrayList.get(i)).intValue(), j), (arrayList.size() - i) - 1);
                }
                return;
            }
            enumerateConsumer.onEnumerationResult(null, 0);
        }

        @Override // android.hardware.biometrics.face.V1_0.IBiometricsFaceClientCallback
        public void onLockoutChanged(final long j) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$HalResultController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Face10.HalResultController.this.lambda$onLockoutChanged$6(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLockoutChanged$6(long j) {
            Slog.d("Face10", "onLockoutChanged: " + j);
            int i = (j > 0L ? 1 : (j == 0L ? 0 : -1));
            this.mLockoutTracker.setCurrentUserLockoutMode(i == 0 ? 0 : (j == -1 || j == Long.MAX_VALUE) ? 2 : 1);
            if (i == 0) {
                this.mLockoutResetDispatcher.notifyLockoutResetCallbacks(this.mSensorId);
            }
        }
    }

    @VisibleForTesting
    public Face10(Context context, BiometricStateCallback biometricStateCallback, FaceSensorPropertiesInternal faceSensorPropertiesInternal, LockoutResetDispatcher lockoutResetDispatcher, Handler handler, BiometricScheduler biometricScheduler, BiometricContext biometricContext) {
        SynchronousUserSwitchObserver synchronousUserSwitchObserver = new SynchronousUserSwitchObserver() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10.1
            public void onUserSwitching(int i) {
                Face10.this.scheduleInternalCleanup(i, null);
                Face10 face10 = Face10.this;
                face10.scheduleGetFeature(face10.mSensorId, new Binder(), i, 1, null, Face10.this.mContext.getOpPackageName());
            }
        };
        this.mUserSwitchObserver = synchronousUserSwitchObserver;
        this.mSensorProperties = faceSensorPropertiesInternal;
        this.mContext = context;
        this.mBiometricStateCallback = biometricStateCallback;
        this.mSensorId = faceSensorPropertiesInternal.sensorId;
        this.mScheduler = biometricScheduler;
        this.mHandler = handler;
        this.mBiometricContext = biometricContext;
        this.mUsageStats = new UsageStats(context);
        this.mAuthenticatorIds = new HashMap();
        this.mLazyDaemon = new Supplier() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda3
            @Override // java.util.function.Supplier
            public final Object get() {
                IBiometricsFace daemon;
                daemon = Face10.this.getDaemon();
                return daemon;
            }
        };
        LockoutHalImpl lockoutHalImpl = new LockoutHalImpl();
        this.mLockoutTracker = lockoutHalImpl;
        HalResultController halResultController = new HalResultController(faceSensorPropertiesInternal.sensorId, context, handler, biometricScheduler, lockoutHalImpl, lockoutResetDispatcher);
        this.mHalResultController = halResultController;
        halResultController.setCallback(new HalResultController.Callback() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda4
            @Override // com.android.server.biometrics.sensors.face.hidl.Face10.HalResultController.Callback
            public final void onHardwareUnavailable() {
                Face10.this.lambda$new$0();
            }
        });
        try {
            ActivityManager.getService().registerUserSwitchObserver(synchronousUserSwitchObserver, "Face10");
        } catch (RemoteException unused) {
            Slog.e("Face10", "Unable to register user switch observer");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mDaemon = null;
        this.mCurrentUserId = -10000;
    }

    public static Face10 newInstance(Context context, BiometricStateCallback biometricStateCallback, FaceSensorPropertiesInternal faceSensorPropertiesInternal, LockoutResetDispatcher lockoutResetDispatcher) {
        return new Face10(context, biometricStateCallback, faceSensorPropertiesInternal, lockoutResetDispatcher, new Handler(Looper.getMainLooper()), new BiometricScheduler("Face10", 1, null), BiometricContext.getInstance(context));
    }

    public void serviceDied(long j) {
        Slog.e("Face10", "HAL died");
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$serviceDied$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$serviceDied$1() {
        PerformanceTracker.getInstanceForSensorId(this.mSensorId).incrementHALDeathCount();
        this.mDaemon = null;
        this.mCurrentUserId = -10000;
        BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
        if (currentClient instanceof ErrorConsumer) {
            Slog.e("Face10", "Sending ERROR_HW_UNAVAILABLE for client: " + currentClient);
            ((ErrorConsumer) currentClient).onError(1, 0);
            FrameworkStatsLog.write(148, 4, 1, -1);
        }
        this.mScheduler.recordCrashState();
        this.mScheduler.reset();
    }

    public final synchronized IBiometricsFace getDaemon() {
        long j;
        if (this.mTestHalEnabled) {
            TestHal testHal = new TestHal(this.mContext, this.mSensorId);
            testHal.setCallback(this.mHalResultController);
            return testHal;
        }
        IBiometricsFace iBiometricsFace = this.mDaemon;
        if (iBiometricsFace != null) {
            return iBiometricsFace;
        }
        Slog.d("Face10", "Daemon was null, reconnecting, current operation: " + this.mScheduler.getCurrentClient());
        try {
            try {
                this.mDaemon = IBiometricsFace.getService();
            } catch (RemoteException e) {
                Slog.e("Face10", "Failed to get face HAL", e);
            }
        } catch (NoSuchElementException e2) {
            Slog.w("Face10", "NoSuchElementException", e2);
        }
        IBiometricsFace iBiometricsFace2 = this.mDaemon;
        if (iBiometricsFace2 == null) {
            Slog.w("Face10", "Face HAL not available");
            return null;
        }
        iBiometricsFace2.asBinder().linkToDeath(this, 0L);
        try {
            j = this.mDaemon.setCallback(this.mHalResultController).value;
        } catch (RemoteException e3) {
            Slog.e("Face10", "Failed to set callback for face HAL", e3);
            this.mDaemon = null;
            j = 0;
        }
        Slog.d("Face10", "Face HAL ready, HAL ID: " + j);
        if (j != 0) {
            scheduleLoadAuthenticatorIds();
            scheduleInternalCleanup(ActivityManager.getCurrentUser(), null);
            scheduleGetFeature(this.mSensorId, new Binder(), ActivityManager.getCurrentUser(), 1, null, this.mContext.getOpPackageName());
        } else {
            Slog.e("Face10", "Unable to set callback");
            this.mDaemon = null;
        }
        return this.mDaemon;
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public boolean containsSensor(int i) {
        return this.mSensorId == i;
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public List<FaceSensorPropertiesInternal> getSensorProperties() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mSensorProperties);
        return arrayList;
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public FaceSensorPropertiesInternal getSensorProperties(int i) {
        return this.mSensorProperties;
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public List<Face> getEnrolledFaces(int i, int i2) {
        return FaceUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, i2);
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public boolean hasEnrollments(int i, int i2) {
        return !getEnrolledFaces(i, i2).isEmpty();
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public int getLockoutModeForUser(int i, int i2) {
        return this.mLockoutTracker.getLockoutModeForUser(i2);
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public long getAuthenticatorId(int i, int i2) {
        return this.mAuthenticatorIds.getOrDefault(Integer.valueOf(i2), 0L).longValue();
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public boolean isHardwareDetected(int i) {
        return getDaemon() != null;
    }

    public final boolean isGeneratedChallengeCacheValid() {
        return this.mGeneratedChallengeCache != null && sSystemClock.millis() - this.mGeneratedChallengeCache.getCreatedAt() < 60000;
    }

    public final void incrementChallengeCount() {
        this.mGeneratedChallengeCount.add(0, Long.valueOf(sSystemClock.millis()));
    }

    public final int decrementChallengeCount() {
        final long millis = sSystemClock.millis();
        this.mGeneratedChallengeCount.removeIf(new Predicate() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda17
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$decrementChallengeCount$2;
                lambda$decrementChallengeCount$2 = Face10.lambda$decrementChallengeCount$2(millis, (Long) obj);
                return lambda$decrementChallengeCount$2;
            }
        });
        if (!this.mGeneratedChallengeCount.isEmpty()) {
            this.mGeneratedChallengeCount.remove(0);
        }
        return this.mGeneratedChallengeCount.size();
    }

    public static /* synthetic */ boolean lambda$decrementChallengeCount$2(long j, Long l) {
        return j - l.longValue() > 600000;
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleGenerateChallenge(int i, final int i2, final IBinder iBinder, final IFaceServiceReceiver iFaceServiceReceiver, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleGenerateChallenge$3(iFaceServiceReceiver, i2, iBinder, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleGenerateChallenge$3(IFaceServiceReceiver iFaceServiceReceiver, int i, IBinder iBinder, String str) {
        incrementChallengeCount();
        if (isGeneratedChallengeCacheValid()) {
            Slog.d("Face10", "Current challenge is cached and will be reused");
            this.mGeneratedChallengeCache.reuseResult(iFaceServiceReceiver);
            return;
        }
        scheduleUpdateActiveUserWithoutHandler(i);
        final FaceGenerateChallengeClient faceGenerateChallengeClient = new FaceGenerateChallengeClient(this.mContext, this.mLazyDaemon, iBinder, new ClientMonitorCallbackConverter(iFaceServiceReceiver), i, str, this.mSensorId, createLogger(0, 0), this.mBiometricContext, sSystemClock.millis());
        this.mGeneratedChallengeCache = faceGenerateChallengeClient;
        this.mScheduler.scheduleClientMonitor(faceGenerateChallengeClient, new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10.2
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientStarted(BaseClientMonitor baseClientMonitor) {
                if (faceGenerateChallengeClient != baseClientMonitor) {
                    Slog.e("Face10", "scheduleGenerateChallenge onClientStarted, mismatched client. Expecting: " + faceGenerateChallengeClient + ", received: " + baseClientMonitor);
                }
            }
        });
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleRevokeChallenge(int i, final int i2, final IBinder iBinder, final String str, long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleRevokeChallenge$4(iBinder, i2, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleRevokeChallenge$4(IBinder iBinder, int i, String str) {
        if (!(decrementChallengeCount() == 0)) {
            Slog.w("Face10", "scheduleRevokeChallenge skipped - challenge still in use: " + this.mGeneratedChallengeCount);
            return;
        }
        Slog.d("Face10", "scheduleRevokeChallenge executing - no active clients");
        this.mGeneratedChallengeCache = null;
        final FaceRevokeChallengeClient faceRevokeChallengeClient = new FaceRevokeChallengeClient(this.mContext, this.mLazyDaemon, iBinder, i, str, this.mSensorId, createLogger(0, 0), this.mBiometricContext);
        this.mScheduler.scheduleClientMonitor(faceRevokeChallengeClient, new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10.3
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                if (faceRevokeChallengeClient != baseClientMonitor) {
                    Slog.e("Face10", "scheduleRevokeChallenge, mismatched client.Expecting: " + faceRevokeChallengeClient + ", received: " + baseClientMonitor);
                }
            }
        });
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public long scheduleEnroll(int i, final IBinder iBinder, final byte[] bArr, final int i2, final IFaceServiceReceiver iFaceServiceReceiver, final String str, final int[] iArr, final Surface surface, boolean z) {
        final long incrementAndGet = this.mRequestCounter.incrementAndGet();
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleEnroll$5(i2, iBinder, iFaceServiceReceiver, bArr, str, incrementAndGet, iArr, surface);
            }
        });
        return incrementAndGet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleEnroll$5(int i, IBinder iBinder, IFaceServiceReceiver iFaceServiceReceiver, byte[] bArr, String str, long j, int[] iArr, Surface surface) {
        scheduleUpdateActiveUserWithoutHandler(i);
        BiometricNotificationUtils.cancelReEnrollNotification(this.mContext);
        final FaceEnrollClient faceEnrollClient = new FaceEnrollClient(this.mContext, this.mLazyDaemon, iBinder, new ClientMonitorCallbackConverter(iFaceServiceReceiver), i, bArr, str, j, FaceUtils.getLegacyInstance(this.mSensorId), iArr, 75, surface, this.mSensorId, createLogger(1, 0), this.mBiometricContext);
        this.mScheduler.scheduleClientMonitor(faceEnrollClient, new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10.4
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientStarted(BaseClientMonitor baseClientMonitor) {
                Face10.this.mBiometricStateCallback.onClientStarted(baseClientMonitor);
            }

            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onBiometricAction(int i2) {
                Face10.this.mBiometricStateCallback.onBiometricAction(i2);
            }

            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                Face10.this.mBiometricStateCallback.onClientFinished(baseClientMonitor, z);
                if (z) {
                    Face10.this.scheduleUpdateActiveUserWithoutHandler(faceEnrollClient.getTargetUserId());
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelEnrollment$6(IBinder iBinder, long j) {
        this.mScheduler.cancelEnrollment(iBinder, j);
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void cancelEnrollment(int i, final IBinder iBinder, final long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$cancelEnrollment$6(iBinder, j);
            }
        });
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public long scheduleFaceDetect(IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FaceAuthenticateOptions faceAuthenticateOptions, int i) {
        throw new IllegalStateException("Face detect not supported by IBiometricsFace@1.0. Did youforget to check the supportsFaceDetection flag?");
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void cancelFaceDetect(int i, IBinder iBinder, long j) {
        throw new IllegalStateException("Face detect not supported by IBiometricsFace@1.0. Did youforget to check the supportsFaceDetection flag?");
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleAuthenticate(final IBinder iBinder, final long j, final int i, final ClientMonitorCallbackConverter clientMonitorCallbackConverter, final FaceAuthenticateOptions faceAuthenticateOptions, final long j2, final boolean z, final int i2, final boolean z2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleAuthenticate$7(faceAuthenticateOptions, iBinder, j2, clientMonitorCallbackConverter, j, z, i, i2, z2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleAuthenticate$7(FaceAuthenticateOptions faceAuthenticateOptions, IBinder iBinder, long j, ClientMonitorCallbackConverter clientMonitorCallbackConverter, long j2, boolean z, int i, int i2, boolean z2) {
        scheduleUpdateActiveUserWithoutHandler(faceAuthenticateOptions.getUserId());
        this.mScheduler.scheduleClientMonitor(new FaceAuthenticationClient(this.mContext, this.mLazyDaemon, iBinder, j, clientMonitorCallbackConverter, j2, z, faceAuthenticateOptions, i, false, createLogger(2, i2), this.mBiometricContext, Utils.isStrongBiometric(this.mSensorId), this.mLockoutTracker, this.mUsageStats, z2, Utils.getCurrentStrength(this.mSensorId)));
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public long scheduleAuthenticate(IBinder iBinder, long j, int i, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FaceAuthenticateOptions faceAuthenticateOptions, boolean z, int i2, boolean z2) {
        long incrementAndGet = this.mRequestCounter.incrementAndGet();
        scheduleAuthenticate(iBinder, j, i, clientMonitorCallbackConverter, faceAuthenticateOptions, incrementAndGet, z, i2, z2);
        return incrementAndGet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelAuthentication$8(IBinder iBinder, long j) {
        this.mScheduler.cancelAuthenticationOrDetection(iBinder, j);
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void cancelAuthentication(int i, final IBinder iBinder, final long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$cancelAuthentication$8(iBinder, j);
            }
        });
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleRemove(int i, final IBinder iBinder, final int i2, final int i3, final IFaceServiceReceiver iFaceServiceReceiver, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleRemove$9(i3, iBinder, iFaceServiceReceiver, i2, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleRemove$9(int i, IBinder iBinder, IFaceServiceReceiver iFaceServiceReceiver, int i2, String str) {
        scheduleUpdateActiveUserWithoutHandler(i);
        this.mScheduler.scheduleClientMonitor(new FaceRemovalClient(this.mContext, this.mLazyDaemon, iBinder, new ClientMonitorCallbackConverter(iFaceServiceReceiver), i2, i, str, FaceUtils.getLegacyInstance(this.mSensorId), this.mSensorId, createLogger(4, 0), this.mBiometricContext, this.mAuthenticatorIds), this.mBiometricStateCallback);
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleRemoveAll(int i, final IBinder iBinder, final int i2, final IFaceServiceReceiver iFaceServiceReceiver, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleRemoveAll$10(i2, iBinder, iFaceServiceReceiver, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleRemoveAll$10(int i, IBinder iBinder, IFaceServiceReceiver iFaceServiceReceiver, String str) {
        scheduleUpdateActiveUserWithoutHandler(i);
        this.mScheduler.scheduleClientMonitor(new FaceRemovalClient(this.mContext, this.mLazyDaemon, iBinder, new ClientMonitorCallbackConverter(iFaceServiceReceiver), 0, i, str, FaceUtils.getLegacyInstance(this.mSensorId), this.mSensorId, createLogger(4, 0), this.mBiometricContext, this.mAuthenticatorIds), this.mBiometricStateCallback);
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleResetLockout(final int i, final int i2, final byte[] bArr) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleResetLockout$11(i, i2, bArr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleResetLockout$11(int i, int i2, byte[] bArr) {
        if (getEnrolledFaces(i, i2).isEmpty()) {
            Slog.w("Face10", "Ignoring lockout reset, no templates enrolled for user: " + i2);
            return;
        }
        scheduleUpdateActiveUserWithoutHandler(i2);
        Context context = this.mContext;
        this.mScheduler.scheduleClientMonitor(new FaceResetLockoutClient(context, this.mLazyDaemon, i2, context.getOpPackageName(), this.mSensorId, createLogger(0, 0), this.mBiometricContext, bArr));
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleSetFeature(final int i, final IBinder iBinder, final int i2, final int i3, final boolean z, final byte[] bArr, final IFaceServiceReceiver iFaceServiceReceiver, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleSetFeature$12(i, i2, iBinder, iFaceServiceReceiver, str, i3, z, bArr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleSetFeature$12(int i, int i2, IBinder iBinder, IFaceServiceReceiver iFaceServiceReceiver, String str, int i3, boolean z, byte[] bArr) {
        List<Face> enrolledFaces = getEnrolledFaces(i, i2);
        if (enrolledFaces.isEmpty()) {
            Slog.w("Face10", "Ignoring setFeature, no templates enrolled for user: " + i2);
            return;
        }
        scheduleUpdateActiveUserWithoutHandler(i2);
        this.mScheduler.scheduleClientMonitor(new FaceSetFeatureClient(this.mContext, this.mLazyDaemon, iBinder, new ClientMonitorCallbackConverter(iFaceServiceReceiver), i2, str, this.mSensorId, BiometricLogger.ofUnknown(this.mContext), this.mBiometricContext, i3, z, bArr, enrolledFaces.get(0).getBiometricId()));
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleGetFeature(final int i, final IBinder iBinder, final int i2, final int i3, final ClientMonitorCallbackConverter clientMonitorCallbackConverter, final String str) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleGetFeature$13(i, i2, iBinder, clientMonitorCallbackConverter, str, i3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleGetFeature$13(int i, final int i2, IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, String str, final int i3) {
        List<Face> enrolledFaces = getEnrolledFaces(i, i2);
        if (enrolledFaces.isEmpty()) {
            Slog.w("Face10", "Ignoring getFeature, no templates enrolled for user: " + i2);
            return;
        }
        scheduleUpdateActiveUserWithoutHandler(i2);
        int biometricId = enrolledFaces.get(0).getBiometricId();
        Context context = this.mContext;
        final FaceGetFeatureClient faceGetFeatureClient = new FaceGetFeatureClient(context, this.mLazyDaemon, iBinder, clientMonitorCallbackConverter, i2, str, this.mSensorId, BiometricLogger.ofUnknown(context), this.mBiometricContext, i3, biometricId);
        this.mScheduler.scheduleClientMonitor(faceGetFeatureClient, new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10.5
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                if (z && i3 == 1) {
                    boolean value = faceGetFeatureClient.getValue();
                    Slog.d("Face10", "Updating attention value for user: " + i2 + " to value: " + (value ? 1 : 0));
                    Settings.Secure.putIntForUser(Face10.this.mContext.getContentResolver(), "face_unlock_attention_required", value ? 1 : 0, i2);
                }
            }
        });
    }

    public final void scheduleInternalCleanup(final int i, final ClientMonitorCallback clientMonitorCallback) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleInternalCleanup$14(i, clientMonitorCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleInternalCleanup$14(int i, ClientMonitorCallback clientMonitorCallback) {
        scheduleUpdateActiveUserWithoutHandler(i);
        Context context = this.mContext;
        this.mScheduler.scheduleClientMonitor(new FaceInternalCleanupClient(context, this.mLazyDaemon, i, context.getOpPackageName(), this.mSensorId, createLogger(3, 0), this.mBiometricContext, FaceUtils.getLegacyInstance(this.mSensorId), this.mAuthenticatorIds), new ClientMonitorCompositeCallback(clientMonitorCallback, this.mBiometricStateCallback));
    }

    public void scheduleInternalCleanup(int i, int i2, ClientMonitorCallback clientMonitorCallback) {
        scheduleInternalCleanup(i2, this.mBiometricStateCallback);
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void scheduleInternalCleanup(int i, int i2, ClientMonitorCallback clientMonitorCallback, boolean z) {
        scheduleInternalCleanup(i2, clientMonitorCallback);
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void startPreparedClient(int i, final int i2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$startPreparedClient$15(i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startPreparedClient$15(int i) {
        this.mScheduler.startPreparedClient(i);
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public void dumpProtoState(int i, ProtoOutputStream protoOutputStream, boolean z) {
        long start = protoOutputStream.start(2246267895809L);
        protoOutputStream.write(1120986464257L, this.mSensorProperties.sensorId);
        protoOutputStream.write(1159641169922L, 2);
        protoOutputStream.write(1120986464259L, Utils.getCurrentStrength(this.mSensorProperties.sensorId));
        protoOutputStream.write(1146756268036L, this.mScheduler.dumpProtoState(z));
        for (UserInfo userInfo : UserManager.get(this.mContext).getUsers()) {
            int identifier = userInfo.getUserHandle().getIdentifier();
            long start2 = protoOutputStream.start(2246267895813L);
            protoOutputStream.write(1120986464257L, identifier);
            protoOutputStream.write(1120986464258L, FaceUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, identifier).size());
            protoOutputStream.end(start2);
        }
        protoOutputStream.write(1133871366150L, this.mSensorProperties.resetLockoutRequiresHardwareAuthToken);
        protoOutputStream.write(1133871366151L, this.mSensorProperties.resetLockoutRequiresChallenge);
        protoOutputStream.end(start);
    }

    @Override // com.android.server.biometrics.sensors.BiometricServiceProvider
    public void dumpInternal(int i, PrintWriter printWriter) {
        PerformanceTracker instanceForSensorId = PerformanceTracker.getInstanceForSensorId(this.mSensorId);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("service", "Face10");
            JSONArray jSONArray = new JSONArray();
            for (UserInfo userInfo : UserManager.get(this.mContext).getUsers()) {
                int identifier = userInfo.getUserHandle().getIdentifier();
                int size = FaceUtils.getLegacyInstance(this.mSensorId).getBiometricsForUser(this.mContext, identifier).size();
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
            Slog.e("Face10", "dump formatting failure", e);
        }
        printWriter.println(jSONObject);
        printWriter.println("HAL deaths since last reboot: " + instanceForSensorId.getHALDeathCount());
        this.mScheduler.dump(printWriter);
        this.mUsageStats.print(printWriter);
    }

    public final void scheduleLoadAuthenticatorIds() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                Face10.this.lambda$scheduleLoadAuthenticatorIds$16();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleLoadAuthenticatorIds$16() {
        for (UserInfo userInfo : UserManager.get(this.mContext).getAliveUsers()) {
            int i = userInfo.id;
            if (!this.mAuthenticatorIds.containsKey(Integer.valueOf(i))) {
                scheduleUpdateActiveUserWithoutHandler(i);
            }
        }
    }

    public final void scheduleUpdateActiveUserWithoutHandler(final int i) {
        Context context = this.mContext;
        this.mScheduler.scheduleClientMonitor(new FaceUpdateActiveUserClient(context, this.mLazyDaemon, i, context.getOpPackageName(), this.mSensorId, createLogger(0, 0), this.mBiometricContext, !getEnrolledFaces(this.mSensorId, i).isEmpty(), this.mAuthenticatorIds), new ClientMonitorCallback() { // from class: com.android.server.biometrics.sensors.face.hidl.Face10.6
            @Override // com.android.server.biometrics.sensors.ClientMonitorCallback
            public void onClientFinished(BaseClientMonitor baseClientMonitor, boolean z) {
                if (z) {
                    Face10.this.mCurrentUserId = i;
                    return;
                }
                Slog.w("Face10", "Failed to change user, still: " + Face10.this.mCurrentUserId);
            }
        });
    }

    public final BiometricLogger createLogger(int i, int i2) {
        return new BiometricLogger(this.mContext, 4, i, i2);
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public void dumpHal(int i, FileDescriptor fileDescriptor, String[] strArr) {
        IBiometricsFace daemon;
        NativeHandle fileOutputStream;
        if ((!Build.IS_ENG && !Build.IS_USERDEBUG) || SystemProperties.getBoolean("ro.face.disable_debug_data", false) || SystemProperties.getBoolean("persist.face.disable_debug_data", false) || (daemon = getDaemon()) == null) {
            return;
        }
        NativeHandle nativeHandle = null;
        try {
            try {
                try {
                    fileOutputStream = new FileOutputStream("/dev/null");
                } catch (IOException unused) {
                    return;
                }
            } catch (RemoteException | IOException e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            nativeHandle = new NativeHandle(new FileDescriptor[]{fileOutputStream.getFD(), fileDescriptor}, new int[0], false);
            daemon.debug(nativeHandle, new ArrayList<>(Arrays.asList(strArr)));
            fileOutputStream.close();
        } catch (RemoteException | IOException e2) {
            e = e2;
            nativeHandle = fileOutputStream;
            Slog.d("Face10", "error while reading face debugging data", e);
            if (nativeHandle != null) {
                nativeHandle.close();
            }
        } catch (Throwable th2) {
            th = th2;
            nativeHandle = fileOutputStream;
            if (nativeHandle != null) {
                try {
                    nativeHandle.close();
                } catch (IOException unused2) {
                }
            }
            throw th;
        }
    }

    public void setTestHalEnabled(boolean z) {
        this.mTestHalEnabled = z;
    }

    @Override // com.android.server.biometrics.sensors.face.ServiceProvider
    public ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str) {
        return new BiometricTestSessionImpl(this.mContext, this.mSensorId, iTestSessionCallback, this, this.mHalResultController);
    }
}
