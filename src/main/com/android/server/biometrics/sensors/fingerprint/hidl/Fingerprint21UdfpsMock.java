package com.android.server.biometrics.sensors.fingerprint.hidl;

import android.app.trust.TrustManager;
import android.content.ContentResolver;
import android.content.Context;
import android.hardware.biometrics.fingerprint.PointerContext;
import android.hardware.fingerprint.FingerprintAuthenticateOptions;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.sensors.AuthenticationConsumer;
import com.android.server.biometrics.sensors.BaseClientMonitor;
import com.android.server.biometrics.sensors.BiometricScheduler;
import com.android.server.biometrics.sensors.BiometricStateCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import com.android.server.biometrics.sensors.LockoutResetDispatcher;
import com.android.server.biometrics.sensors.fingerprint.GestureAvailabilityDispatcher;
import com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21;
import com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21UdfpsMock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/* loaded from: classes.dex */
public class Fingerprint21UdfpsMock extends Fingerprint21 implements TrustManager.TrustListener {
    public final FakeAcceptRunnable mFakeAcceptRunnable;
    public final FakeRejectRunnable mFakeRejectRunnable;
    public final Handler mHandler;
    public final MockHalResultController mMockHalResultController;
    public final Random mRandom;
    public final RestartAuthRunnable mRestartAuthRunnable;
    public final TestableBiometricScheduler mScheduler;
    public final FingerprintSensorPropertiesInternal mSensorProperties;
    public final TrustManager mTrustManager;
    public final SparseBooleanArray mUserHasTrust;

    public void onTrustError(CharSequence charSequence) {
    }

    public void onTrustManagedChanged(boolean z, int i) {
    }

    /* loaded from: classes.dex */
    public static class TestableBiometricScheduler extends BiometricScheduler {
        public Fingerprint21UdfpsMock mFingerprint21;

        public TestableBiometricScheduler(String str, Handler handler, GestureAvailabilityDispatcher gestureAvailabilityDispatcher) {
            super(str, 3, gestureAvailabilityDispatcher);
        }

        public void init(Fingerprint21UdfpsMock fingerprint21UdfpsMock) {
            this.mFingerprint21 = fingerprint21UdfpsMock;
        }
    }

    /* loaded from: classes.dex */
    public static class MockHalResultController extends Fingerprint21.HalResultController {
        public Fingerprint21UdfpsMock mFingerprint21;
        public LastAuthArgs mLastAuthArgs;
        public RestartAuthRunnable mRestartAuthRunnable;

        /* loaded from: classes.dex */
        public static class LastAuthArgs {
            public final long deviceId;
            public final int fingerId;
            public final int groupId;
            public final AuthenticationConsumer lastAuthenticatedClient;
            public final ArrayList<Byte> token;

            public LastAuthArgs(AuthenticationConsumer authenticationConsumer, long j, int i, int i2, ArrayList<Byte> arrayList) {
                this.lastAuthenticatedClient = authenticationConsumer;
                this.deviceId = j;
                this.fingerId = i;
                this.groupId = i2;
                if (arrayList == null) {
                    this.token = null;
                } else {
                    this.token = new ArrayList<>(arrayList);
                }
            }
        }

        public MockHalResultController(int i, Context context, Handler handler, BiometricScheduler biometricScheduler) {
            super(i, context, handler, biometricScheduler);
        }

        public void init(RestartAuthRunnable restartAuthRunnable, Fingerprint21UdfpsMock fingerprint21UdfpsMock) {
            this.mRestartAuthRunnable = restartAuthRunnable;
            this.mFingerprint21 = fingerprint21UdfpsMock;
        }

        public AuthenticationConsumer getLastAuthenticatedClient() {
            LastAuthArgs lastAuthArgs = this.mLastAuthArgs;
            if (lastAuthArgs != null) {
                return lastAuthArgs.lastAuthenticatedClient;
            }
            return null;
        }

        @Override // com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21.HalResultController, android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback
        public void onAuthenticated(final long j, final int i, final int i2, final ArrayList<Byte> arrayList) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21UdfpsMock$MockHalResultController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Fingerprint21UdfpsMock.MockHalResultController.this.lambda$onAuthenticated$0(i, j, i2, arrayList);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticated$0(int i, long j, int i2, ArrayList arrayList) {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof AuthenticationConsumer)) {
                Slog.e("Fingerprint21UdfpsMock", "Non authentication consumer: " + currentClient);
                return;
            }
            if (i != 0) {
                this.mFingerprint21.setDebugMessage("Finger accepted");
            } else {
                this.mFingerprint21.setDebugMessage("Finger rejected");
            }
            AuthenticationConsumer authenticationConsumer = (AuthenticationConsumer) currentClient;
            this.mLastAuthArgs = new LastAuthArgs(authenticationConsumer, j, i, i2, arrayList);
            this.mHandler.removeCallbacks(this.mRestartAuthRunnable);
            this.mRestartAuthRunnable.setLastAuthReference(authenticationConsumer);
            this.mHandler.postDelayed(this.mRestartAuthRunnable, 10000L);
        }

        public void sendAuthenticated(long j, int i, int i2, ArrayList<Byte> arrayList) {
            StringBuilder sb = new StringBuilder();
            sb.append("sendAuthenticated: ");
            sb.append(i != 0);
            Slog.d("Fingerprint21UdfpsMock", sb.toString());
            Fingerprint21UdfpsMock fingerprint21UdfpsMock = this.mFingerprint21;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Udfps match: ");
            sb2.append(i != 0);
            fingerprint21UdfpsMock.setDebugMessage(sb2.toString());
            super.onAuthenticated(j, i, i2, arrayList);
        }
    }

    public static Fingerprint21UdfpsMock newInstance(Context context, BiometricStateCallback biometricStateCallback, FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal, LockoutResetDispatcher lockoutResetDispatcher, GestureAvailabilityDispatcher gestureAvailabilityDispatcher, BiometricContext biometricContext) {
        Slog.d("Fingerprint21UdfpsMock", "Creating Fingerprint23Mock!");
        Handler handler = new Handler(Looper.getMainLooper());
        TestableBiometricScheduler testableBiometricScheduler = new TestableBiometricScheduler("Fingerprint21UdfpsMock", handler, gestureAvailabilityDispatcher);
        return new Fingerprint21UdfpsMock(context, biometricStateCallback, fingerprintSensorPropertiesInternal, testableBiometricScheduler, handler, lockoutResetDispatcher, new MockHalResultController(fingerprintSensorPropertiesInternal.sensorId, context, handler, testableBiometricScheduler), biometricContext);
    }

    /* loaded from: classes.dex */
    public static abstract class FakeFingerRunnable implements Runnable {
        public int mCaptureDuration;
        public long mFingerDownTime;

        public FakeFingerRunnable() {
        }

        public void setSimulationTime(long j, int i) {
            this.mFingerDownTime = j;
            this.mCaptureDuration = i;
        }

        public boolean isImageCaptureComplete() {
            return System.currentTimeMillis() - this.mFingerDownTime > ((long) this.mCaptureDuration);
        }
    }

    /* loaded from: classes.dex */
    public final class FakeRejectRunnable extends FakeFingerRunnable {
        public FakeRejectRunnable() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            Fingerprint21UdfpsMock.this.mMockHalResultController.sendAuthenticated(0L, 0, 0, null);
        }
    }

    /* loaded from: classes.dex */
    public final class FakeAcceptRunnable extends FakeFingerRunnable {
        public FakeAcceptRunnable() {
            super();
        }

        @Override // java.lang.Runnable
        public void run() {
            if (Fingerprint21UdfpsMock.this.mMockHalResultController.mLastAuthArgs == null) {
                Slog.d("Fingerprint21UdfpsMock", "Sending fake finger");
                Fingerprint21UdfpsMock.this.mMockHalResultController.sendAuthenticated(1L, 1, 1, null);
                return;
            }
            Fingerprint21UdfpsMock.this.mMockHalResultController.sendAuthenticated(Fingerprint21UdfpsMock.this.mMockHalResultController.mLastAuthArgs.deviceId, Fingerprint21UdfpsMock.this.mMockHalResultController.mLastAuthArgs.fingerId, Fingerprint21UdfpsMock.this.mMockHalResultController.mLastAuthArgs.groupId, Fingerprint21UdfpsMock.this.mMockHalResultController.mLastAuthArgs.token);
        }
    }

    /* loaded from: classes.dex */
    public static final class RestartAuthRunnable implements Runnable {
        public final Fingerprint21UdfpsMock mFingerprint21;
        public AuthenticationConsumer mLastAuthConsumer;
        public final TestableBiometricScheduler mScheduler;

        public RestartAuthRunnable(Fingerprint21UdfpsMock fingerprint21UdfpsMock, TestableBiometricScheduler testableBiometricScheduler) {
            this.mFingerprint21 = fingerprint21UdfpsMock;
            this.mScheduler = testableBiometricScheduler;
        }

        public void setLastAuthReference(AuthenticationConsumer authenticationConsumer) {
            this.mLastAuthConsumer = authenticationConsumer;
        }

        @Override // java.lang.Runnable
        public void run() {
            BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
            if (!(currentClient instanceof FingerprintAuthenticationClient)) {
                Slog.e("Fingerprint21UdfpsMock", "Non-FingerprintAuthenticationClient client: " + currentClient);
            } else if (currentClient != this.mLastAuthConsumer) {
                Slog.e("Fingerprint21UdfpsMock", "Current client: " + currentClient + " does not match mLastAuthConsumer: " + this.mLastAuthConsumer);
            } else {
                Slog.d("Fingerprint21UdfpsMock", "Restarting auth, current: " + currentClient);
                this.mFingerprint21.setDebugMessage("Auth timed out");
                FingerprintAuthenticationClient fingerprintAuthenticationClient = (FingerprintAuthenticationClient) currentClient;
                IBinder token = currentClient.getToken();
                long operationId = fingerprintAuthenticationClient.getOperationId();
                int cookie = currentClient.getCookie();
                ClientMonitorCallbackConverter listener = currentClient.getListener();
                boolean isRestricted = fingerprintAuthenticationClient.isRestricted();
                int statsClient = currentClient.getLogger().getStatsClient();
                boolean isKeyguard = fingerprintAuthenticationClient.isKeyguard();
                FingerprintAuthenticateOptions build = new FingerprintAuthenticateOptions.Builder().setUserId(currentClient.getTargetUserId()).setOpPackageName(currentClient.getOwnerString()).build();
                this.mScheduler.getInternalCallback().onClientFinished(currentClient, true);
                this.mFingerprint21.scheduleAuthenticate(token, operationId, cookie, listener, build, isRestricted, statsClient, isKeyguard);
            }
        }
    }

    public Fingerprint21UdfpsMock(Context context, BiometricStateCallback biometricStateCallback, FingerprintSensorPropertiesInternal fingerprintSensorPropertiesInternal, TestableBiometricScheduler testableBiometricScheduler, Handler handler, LockoutResetDispatcher lockoutResetDispatcher, MockHalResultController mockHalResultController, BiometricContext biometricContext) {
        super(context, biometricStateCallback, fingerprintSensorPropertiesInternal, testableBiometricScheduler, handler, lockoutResetDispatcher, mockHalResultController, biometricContext);
        this.mScheduler = testableBiometricScheduler;
        testableBiometricScheduler.init(this);
        this.mHandler = handler;
        this.mSensorProperties = new FingerprintSensorPropertiesInternal(fingerprintSensorPropertiesInternal.sensorId, fingerprintSensorPropertiesInternal.sensorStrength, this.mContext.getResources().getInteger(17694845), fingerprintSensorPropertiesInternal.componentInfo, 3, false, false, fingerprintSensorPropertiesInternal.getAllLocations());
        this.mMockHalResultController = mockHalResultController;
        this.mUserHasTrust = new SparseBooleanArray();
        TrustManager trustManager = (TrustManager) context.getSystemService(TrustManager.class);
        this.mTrustManager = trustManager;
        trustManager.registerTrustListener(this);
        this.mRandom = new Random();
        this.mFakeRejectRunnable = new FakeRejectRunnable();
        this.mFakeAcceptRunnable = new FakeAcceptRunnable();
        RestartAuthRunnable restartAuthRunnable = new RestartAuthRunnable(this, testableBiometricScheduler);
        this.mRestartAuthRunnable = restartAuthRunnable;
        mockHalResultController.init(restartAuthRunnable, this);
    }

    public void onTrustChanged(boolean z, boolean z2, int i, int i2, List<String> list) {
        this.mUserHasTrust.put(i, z);
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21, com.android.server.biometrics.sensors.BiometricServiceProvider
    public List<FingerprintSensorPropertiesInternal> getSensorProperties() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mSensorProperties);
        return arrayList;
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21, com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void onPointerDown(long j, int i, PointerContext pointerContext) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21UdfpsMock$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21UdfpsMock.this.lambda$onPointerDown$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPointerDown$0() {
        Slog.d("Fingerprint21UdfpsMock", "onFingerDown");
        AuthenticationConsumer lastAuthenticatedClient = this.mMockHalResultController.getLastAuthenticatedClient();
        BaseClientMonitor currentClient = this.mScheduler.getCurrentClient();
        if (currentClient == null) {
            Slog.d("Fingerprint21UdfpsMock", "Not authenticating");
            return;
        }
        this.mHandler.removeCallbacks(this.mFakeRejectRunnable);
        this.mHandler.removeCallbacks(this.mFakeAcceptRunnable);
        boolean z = true;
        boolean z2 = false;
        boolean z3 = lastAuthenticatedClient != null && lastAuthenticatedClient == currentClient;
        if (currentClient instanceof FingerprintAuthenticationClient) {
            if (!((FingerprintAuthenticationClient) currentClient).isKeyguard() || !this.mUserHasTrust.get(currentClient.getTargetUserId(), false)) {
                z = false;
            }
            z2 = z;
        }
        int newCaptureDuration = getNewCaptureDuration();
        int matchingDuration = getMatchingDuration();
        int i = newCaptureDuration + matchingDuration;
        setDebugMessage("Duration: " + i + " (" + newCaptureDuration + " + " + matchingDuration + ")");
        if (z3 || z2) {
            this.mFakeAcceptRunnable.setSimulationTime(System.currentTimeMillis(), newCaptureDuration);
            this.mHandler.postDelayed(this.mFakeAcceptRunnable, i);
        } else if (currentClient instanceof AuthenticationConsumer) {
            this.mFakeRejectRunnable.setSimulationTime(System.currentTimeMillis(), newCaptureDuration);
            this.mHandler.postDelayed(this.mFakeRejectRunnable, i);
        }
    }

    @Override // com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21, com.android.server.biometrics.sensors.fingerprint.ServiceProvider
    public void onPointerUp(long j, int i, PointerContext pointerContext) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.sensors.fingerprint.hidl.Fingerprint21UdfpsMock$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Fingerprint21UdfpsMock.this.lambda$onPointerUp$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPointerUp$1() {
        Slog.d("Fingerprint21UdfpsMock", "onFingerUp");
        if (this.mHandler.hasCallbacks(this.mFakeRejectRunnable) && !this.mFakeRejectRunnable.isImageCaptureComplete()) {
            this.mHandler.removeCallbacks(this.mFakeRejectRunnable);
            this.mMockHalResultController.onAcquired(0L, 5, 0);
        } else if (!this.mHandler.hasCallbacks(this.mFakeAcceptRunnable) || this.mFakeAcceptRunnable.isImageCaptureComplete()) {
        } else {
            this.mHandler.removeCallbacks(this.mFakeAcceptRunnable);
            this.mMockHalResultController.onAcquired(0L, 5, 0);
        }
    }

    public final int getNewCaptureDuration() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int intForUser = Settings.Secure.getIntForUser(contentResolver, "com.android.server.biometrics.sensors.fingerprint.test_udfps.auth_delay_pt1", 300, -2);
        int intForUser2 = Settings.Secure.getIntForUser(contentResolver, "com.android.server.biometrics.sensors.fingerprint.test_udfps.auth_delay_randomness", 100, -2);
        return Math.max(intForUser + (this.mRandom.nextInt(intForUser2 * 2) - intForUser2), 0);
    }

    public final int getMatchingDuration() {
        return Math.max(Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "com.android.server.biometrics.sensors.fingerprint.test_udfps.auth_delay_pt2", FrameworkStatsLog.APP_PROCESS_DIED__IMPORTANCE__IMPORTANCE_BACKGROUND, -2), 0);
    }

    public final void setDebugMessage(String str) {
        try {
            IUdfpsOverlayController udfpsOverlayController = getUdfpsOverlayController();
            if (udfpsOverlayController != null) {
                Slog.d("Fingerprint21UdfpsMock", "setDebugMessage: " + str);
                udfpsOverlayController.setDebugMessage(this.mSensorProperties.sensorId, str);
            }
        } catch (RemoteException e) {
            Slog.e("Fingerprint21UdfpsMock", "Remote exception when sending message: " + str, e);
        }
    }
}
