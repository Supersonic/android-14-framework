package com.android.server.biometrics;

import android.annotation.EnforcePermission;
import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.UserSwitchObserver;
import android.app.admin.DevicePolicyManager;
import android.app.trust.ITrustManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.IBiometricAuthenticator;
import android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.biometrics.IBiometricService;
import android.hardware.biometrics.IBiometricServiceReceiver;
import android.hardware.biometrics.IBiometricSysuiReceiver;
import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.PromptInfo;
import android.hardware.biometrics.SensorPropertiesInternal;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.security.KeyStore;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.SomeArgs;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.DumpUtils;
import com.android.server.SystemService;
import com.android.server.biometrics.AuthSession;
import com.android.server.biometrics.BiometricService;
import com.android.server.biometrics.log.BiometricContext;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class BiometricService extends SystemService {
    @VisibleForTesting
    AuthSession mAuthSession;
    public final BiometricContext mBiometricContext;
    @VisibleForTesting
    BiometricStrengthController mBiometricStrengthController;
    public final DevicePolicyManager mDevicePolicyManager;
    public final List<EnabledOnKeyguardCallback> mEnabledOnKeyguardCallbacks;
    public final Handler mHandler;
    @VisibleForTesting
    final IBiometricService.Stub mImpl;
    public final Injector mInjector;
    @VisibleForTesting
    KeyStore mKeyStore;
    public final Random mRandom;
    public final Supplier<Long> mRequestCounter;
    public final ArrayList<BiometricSensor> mSensors;
    @VisibleForTesting
    final SettingObserver mSettingObserver;
    @VisibleForTesting
    IStatusBarService mStatusBarService;
    @VisibleForTesting
    ITrustManager mTrustManager;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class InvalidationTracker {
        public final IInvalidationCallback mClientCallback;
        public final Set<Integer> mSensorsPendingInvalidation = new ArraySet();

        public static InvalidationTracker start(Context context, ArrayList<BiometricSensor> arrayList, int i, int i2, IInvalidationCallback iInvalidationCallback) {
            return new InvalidationTracker(context, arrayList, i, i2, iInvalidationCallback);
        }

        public InvalidationTracker(Context context, ArrayList<BiometricSensor> arrayList, int i, int i2, IInvalidationCallback iInvalidationCallback) {
            this.mClientCallback = iInvalidationCallback;
            Iterator<BiometricSensor> it = arrayList.iterator();
            while (it.hasNext()) {
                final BiometricSensor next = it.next();
                if (next.f1133id != i2 && Utils.isAtLeastStrength(next.oemStrength, 15)) {
                    try {
                    } catch (RemoteException e) {
                        Slog.e("BiometricService", "Remote Exception", e);
                    }
                    if (next.impl.hasEnrolledTemplates(i, context.getOpPackageName())) {
                        Slog.d("BiometricService", "Requesting authenticatorId invalidation for sensor: " + next.f1133id);
                        synchronized (this) {
                            this.mSensorsPendingInvalidation.add(Integer.valueOf(next.f1133id));
                        }
                        try {
                            next.impl.invalidateAuthenticatorId(i, new IInvalidationCallback.Stub() { // from class: com.android.server.biometrics.BiometricService.InvalidationTracker.1
                                public void onCompleted() {
                                    InvalidationTracker.this.onInvalidated(next.f1133id);
                                }
                            });
                        } catch (RemoteException e2) {
                            Slog.d("BiometricService", "RemoteException", e2);
                        }
                    } else {
                        continue;
                    }
                }
            }
            synchronized (this) {
                if (this.mSensorsPendingInvalidation.isEmpty()) {
                    try {
                        Slog.d("BiometricService", "No sensors require invalidation");
                        this.mClientCallback.onCompleted();
                    } catch (RemoteException e3) {
                        Slog.e("BiometricService", "Remote Exception", e3);
                    }
                }
            }
        }

        @VisibleForTesting
        public void onInvalidated(int i) {
            synchronized (this) {
                this.mSensorsPendingInvalidation.remove(Integer.valueOf(i));
                Slog.d("BiometricService", "Sensor " + i + " invalidated, remaining size: " + this.mSensorsPendingInvalidation.size());
                if (this.mSensorsPendingInvalidation.isEmpty()) {
                    try {
                        this.mClientCallback.onCompleted();
                    } catch (RemoteException e) {
                        Slog.e("BiometricService", "Remote Exception", e);
                    }
                }
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class SettingObserver extends ContentObserver {
        public final Uri BIOMETRIC_APP_ENABLED;
        public final Uri BIOMETRIC_KEYGUARD_ENABLED;
        public final Uri FACE_UNLOCK_ALWAYS_REQUIRE_CONFIRMATION;
        public final Uri FACE_UNLOCK_APP_ENABLED;
        public final Uri FACE_UNLOCK_KEYGUARD_ENABLED;
        public final Map<Integer, Boolean> mBiometricEnabledForApps;
        public final Map<Integer, Boolean> mBiometricEnabledOnKeyguard;
        public final List<EnabledOnKeyguardCallback> mCallbacks;
        public final ContentResolver mContentResolver;
        public final Map<Integer, Boolean> mFaceAlwaysRequireConfirmation;
        public final boolean mUseLegacyFaceOnlySettings;

        public SettingObserver(Context context, Handler handler, List<EnabledOnKeyguardCallback> list) {
            super(handler);
            this.FACE_UNLOCK_KEYGUARD_ENABLED = Settings.Secure.getUriFor("face_unlock_keyguard_enabled");
            this.FACE_UNLOCK_APP_ENABLED = Settings.Secure.getUriFor("face_unlock_app_enabled");
            this.FACE_UNLOCK_ALWAYS_REQUIRE_CONFIRMATION = Settings.Secure.getUriFor("face_unlock_always_require_confirmation");
            this.BIOMETRIC_KEYGUARD_ENABLED = Settings.Secure.getUriFor("biometric_keyguard_enabled");
            this.BIOMETRIC_APP_ENABLED = Settings.Secure.getUriFor("biometric_app_enabled");
            this.mBiometricEnabledOnKeyguard = new HashMap();
            this.mBiometricEnabledForApps = new HashMap();
            this.mFaceAlwaysRequireConfirmation = new HashMap();
            this.mContentResolver = context.getContentResolver();
            this.mCallbacks = list;
            this.mUseLegacyFaceOnlySettings = Build.VERSION.DEVICE_INITIAL_SDK_INT <= 29 && context.getPackageManager().hasSystemFeature("android.hardware.biometrics.face") && !context.getPackageManager().hasSystemFeature("android.hardware.fingerprint");
            updateContentObserver();
        }

        public void updateContentObserver() {
            this.mContentResolver.unregisterContentObserver(this);
            if (this.mUseLegacyFaceOnlySettings) {
                this.mContentResolver.registerContentObserver(this.FACE_UNLOCK_KEYGUARD_ENABLED, false, this, -1);
                this.mContentResolver.registerContentObserver(this.FACE_UNLOCK_APP_ENABLED, false, this, -1);
            } else {
                this.mContentResolver.registerContentObserver(this.BIOMETRIC_KEYGUARD_ENABLED, false, this, -1);
                this.mContentResolver.registerContentObserver(this.BIOMETRIC_APP_ENABLED, false, this, -1);
            }
            this.mContentResolver.registerContentObserver(this.FACE_UNLOCK_ALWAYS_REQUIRE_CONFIRMATION, false, this, -1);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri, int i) {
            if (this.FACE_UNLOCK_KEYGUARD_ENABLED.equals(uri)) {
                this.mBiometricEnabledOnKeyguard.put(Integer.valueOf(i), Boolean.valueOf(Settings.Secure.getIntForUser(this.mContentResolver, "face_unlock_keyguard_enabled", 1, i) != 0));
                if (i != ActivityManager.getCurrentUser() || z) {
                    return;
                }
                notifyEnabledOnKeyguardCallbacks(i);
            } else if (this.FACE_UNLOCK_APP_ENABLED.equals(uri)) {
                this.mBiometricEnabledForApps.put(Integer.valueOf(i), Boolean.valueOf(Settings.Secure.getIntForUser(this.mContentResolver, "face_unlock_app_enabled", 1, i) != 0));
            } else if (this.FACE_UNLOCK_ALWAYS_REQUIRE_CONFIRMATION.equals(uri)) {
                this.mFaceAlwaysRequireConfirmation.put(Integer.valueOf(i), Boolean.valueOf(Settings.Secure.getIntForUser(this.mContentResolver, "face_unlock_always_require_confirmation", 0, i) != 0));
            } else if (this.BIOMETRIC_KEYGUARD_ENABLED.equals(uri)) {
                this.mBiometricEnabledOnKeyguard.put(Integer.valueOf(i), Boolean.valueOf(Settings.Secure.getIntForUser(this.mContentResolver, "biometric_keyguard_enabled", 1, i) != 0));
                if (i != ActivityManager.getCurrentUser() || z) {
                    return;
                }
                notifyEnabledOnKeyguardCallbacks(i);
            } else if (this.BIOMETRIC_APP_ENABLED.equals(uri)) {
                this.mBiometricEnabledForApps.put(Integer.valueOf(i), Boolean.valueOf(Settings.Secure.getIntForUser(this.mContentResolver, "biometric_app_enabled", 1, i) != 0));
            }
        }

        public boolean getEnabledOnKeyguard(int i) {
            if (!this.mBiometricEnabledOnKeyguard.containsKey(Integer.valueOf(i))) {
                if (this.mUseLegacyFaceOnlySettings) {
                    onChange(true, this.FACE_UNLOCK_KEYGUARD_ENABLED, i);
                } else {
                    onChange(true, this.BIOMETRIC_KEYGUARD_ENABLED, i);
                }
            }
            return this.mBiometricEnabledOnKeyguard.get(Integer.valueOf(i)).booleanValue();
        }

        public boolean getEnabledForApps(int i) {
            if (!this.mBiometricEnabledForApps.containsKey(Integer.valueOf(i))) {
                if (this.mUseLegacyFaceOnlySettings) {
                    onChange(true, this.FACE_UNLOCK_APP_ENABLED, i);
                } else {
                    onChange(true, this.BIOMETRIC_APP_ENABLED, i);
                }
            }
            return this.mBiometricEnabledForApps.getOrDefault(Integer.valueOf(i), Boolean.TRUE).booleanValue();
        }

        public boolean getConfirmationAlwaysRequired(int i, int i2) {
            if (i != 8) {
                return false;
            }
            if (!this.mFaceAlwaysRequireConfirmation.containsKey(Integer.valueOf(i2))) {
                onChange(true, this.FACE_UNLOCK_ALWAYS_REQUIRE_CONFIRMATION, i2);
            }
            return this.mFaceAlwaysRequireConfirmation.get(Integer.valueOf(i2)).booleanValue();
        }

        public void notifyEnabledOnKeyguardCallbacks(int i) {
            List<EnabledOnKeyguardCallback> list = this.mCallbacks;
            for (int i2 = 0; i2 < list.size(); i2++) {
                list.get(i2).notify(this.mBiometricEnabledOnKeyguard.getOrDefault(Integer.valueOf(i), Boolean.TRUE).booleanValue(), i);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class EnabledOnKeyguardCallback implements IBinder.DeathRecipient {
        public final IBiometricEnabledOnKeyguardCallback mCallback;

        public EnabledOnKeyguardCallback(IBiometricEnabledOnKeyguardCallback iBiometricEnabledOnKeyguardCallback) {
            this.mCallback = iBiometricEnabledOnKeyguardCallback;
            try {
                iBiometricEnabledOnKeyguardCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
                Slog.w("BiometricService", "Unable to linkToDeath", e);
            }
        }

        public void notify(boolean z, int i) {
            try {
                this.mCallback.onChanged(z, i);
            } catch (DeadObjectException e) {
                Slog.w("BiometricService", "Death while invoking notify", e);
                BiometricService.this.mEnabledOnKeyguardCallbacks.remove(this);
            } catch (RemoteException e2) {
                Slog.w("BiometricService", "Failed to invoke onChanged", e2);
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Slog.e("BiometricService", "Enabled callback binder died");
            BiometricService.this.mEnabledOnKeyguardCallbacks.remove(this);
        }
    }

    /* renamed from: com.android.server.biometrics.BiometricService$1 */
    /* loaded from: classes.dex */
    public class C05641 extends IBiometricSensorReceiver.Stub {
        public final /* synthetic */ long val$requestId;

        public C05641(long j) {
            this.val$requestId = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticationSucceeded$0(long j, int i, byte[] bArr) {
            BiometricService.this.handleAuthenticationSucceeded(j, i, bArr);
        }

        public void onAuthenticationSucceeded(final int i, final byte[] bArr) {
            Handler handler = BiometricService.this.mHandler;
            final long j = this.val$requestId;
            handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$1$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05641.this.lambda$onAuthenticationSucceeded$0(j, i, bArr);
                }
            });
        }

        public void onAuthenticationFailed(final int i) {
            Slog.v("BiometricService", "onAuthenticationFailed");
            Handler handler = BiometricService.this.mHandler;
            final long j = this.val$requestId;
            handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05641.this.lambda$onAuthenticationFailed$1(j, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAuthenticationFailed$1(long j, int i) {
            BiometricService.this.handleAuthenticationRejected(j, i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$2(long j, int i, int i2, int i3, int i4) {
            BiometricService.this.handleAuthenticationTimedOut(j, i, i2, i3, i4);
        }

        public void onError(final int i, final int i2, final int i3, final int i4) {
            if (i3 == 3) {
                Handler handler = BiometricService.this.mHandler;
                final long j = this.val$requestId;
                handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        BiometricService.C05641.this.lambda$onError$2(j, i, i2, i3, i4);
                    }
                });
                return;
            }
            Handler handler2 = BiometricService.this.mHandler;
            final long j2 = this.val$requestId;
            handler2.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05641.this.lambda$onError$3(j2, i, i2, i3, i4);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onError$3(long j, int i, int i2, int i3, int i4) {
            BiometricService.this.handleOnError(j, i, i2, i3, i4);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAcquired$4(long j, int i, int i2, int i3) {
            BiometricService.this.handleOnAcquired(j, i, i2, i3);
        }

        public void onAcquired(final int i, final int i2, final int i3) {
            Handler handler = BiometricService.this.mHandler;
            final long j = this.val$requestId;
            handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$1$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05641.this.lambda$onAcquired$4(j, i, i2, i3);
                }
            });
        }
    }

    public final IBiometricSensorReceiver createBiometricSensorReceiver(long j) {
        return new C05641(j);
    }

    /* renamed from: com.android.server.biometrics.BiometricService$2 */
    /* loaded from: classes.dex */
    public class C05652 extends IBiometricSysuiReceiver.Stub {
        public final /* synthetic */ long val$requestId;

        public C05652(long j) {
            this.val$requestId = j;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDialogDismissed$0(long j, int i, byte[] bArr) {
            BiometricService.this.handleOnDismissed(j, i, bArr);
        }

        public void onDialogDismissed(final int i, final byte[] bArr) {
            Handler handler = BiometricService.this.mHandler;
            final long j = this.val$requestId;
            handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$2$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05652.this.lambda$onDialogDismissed$0(j, i, bArr);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTryAgainPressed$1(long j) {
            BiometricService.this.handleOnTryAgainPressed(j);
        }

        public void onTryAgainPressed() {
            Handler handler = BiometricService.this.mHandler;
            final long j = this.val$requestId;
            handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05652.this.lambda$onTryAgainPressed$1(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDeviceCredentialPressed$2(long j) {
            BiometricService.this.handleOnDeviceCredentialPressed(j);
        }

        public void onDeviceCredentialPressed() {
            Handler handler = BiometricService.this.mHandler;
            final long j = this.val$requestId;
            handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$2$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05652.this.lambda$onDeviceCredentialPressed$2(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSystemEvent$3(long j, int i) {
            BiometricService.this.handleOnSystemEvent(j, i);
        }

        public void onSystemEvent(final int i) {
            Handler handler = BiometricService.this.mHandler;
            final long j = this.val$requestId;
            handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05652.this.lambda$onSystemEvent$3(j, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onDialogAnimatedIn$4(long j) {
            BiometricService.this.handleOnDialogAnimatedIn(j);
        }

        public void onDialogAnimatedIn() {
            Handler handler = BiometricService.this.mHandler;
            final long j = this.val$requestId;
            handler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$2$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.C05652.this.lambda$onDialogAnimatedIn$4(j);
                }
            });
        }
    }

    public final IBiometricSysuiReceiver createSysuiReceiver(long j) {
        return new C05652(j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createClientDeathReceiver$1(final long j) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                BiometricService.this.lambda$createClientDeathReceiver$0(j);
            }
        });
    }

    public final AuthSession.ClientDeathReceiver createClientDeathReceiver(final long j) {
        return new AuthSession.ClientDeathReceiver() { // from class: com.android.server.biometrics.BiometricService$$ExternalSyntheticLambda1
            @Override // com.android.server.biometrics.AuthSession.ClientDeathReceiver
            public final void onClientDied() {
                BiometricService.this.lambda$createClientDeathReceiver$1(j);
            }
        };
    }

    /* loaded from: classes.dex */
    public final class BiometricServiceWrapper extends IBiometricService.Stub {
        public BiometricServiceWrapper() {
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str) throws RemoteException {
            super.createTestSession_enforcePermission();
            Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
            while (it.hasNext()) {
                BiometricSensor next = it.next();
                if (next.f1133id == i) {
                    return next.impl.createTestSession(iTestSessionCallback, str);
                }
            }
            Slog.e("BiometricService", "Unknown sensor for createTestSession: " + i);
            return null;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public List<SensorPropertiesInternal> getSensorProperties(String str) throws RemoteException {
            super.getSensorProperties_enforcePermission();
            ArrayList arrayList = new ArrayList();
            Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
            while (it.hasNext()) {
                arrayList.add(SensorPropertiesInternal.from(it.next().impl.getSensorProperties(str)));
            }
            return arrayList;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void onReadyForAuthentication(final long j, final int i) {
            super.onReadyForAuthentication_enforcePermission();
            BiometricService.this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$BiometricServiceWrapper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.BiometricServiceWrapper.this.lambda$onReadyForAuthentication$0(j, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReadyForAuthentication$0(long j, int i) {
            BiometricService.this.handleOnReadyForAuthentication(j, i);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public long authenticate(final IBinder iBinder, final long j, final int i, final IBiometricServiceReceiver iBiometricServiceReceiver, final String str, final PromptInfo promptInfo) {
            super.authenticate_enforcePermission();
            if (iBinder == null || iBiometricServiceReceiver == null || str == null || promptInfo == null) {
                Slog.e("BiometricService", "Unable to authenticate, one or more null arguments");
                return -1L;
            } else if (!Utils.isValidAuthenticatorConfig(promptInfo)) {
                throw new SecurityException("Invalid authenticator configuration");
            } else {
                Utils.combineAuthenticatorBundles(promptInfo);
                if (promptInfo.isUseDefaultTitle() && TextUtils.isEmpty(promptInfo.getTitle())) {
                    promptInfo.setTitle(BiometricService.this.getContext().getString(17039747));
                }
                if (promptInfo.isUseDefaultSubtitle() && TextUtils.isEmpty(promptInfo.getSubtitle())) {
                    promptInfo.setSubtitle(BiometricService.this.getContext().getString(17039746));
                }
                final long longValue = ((Long) BiometricService.this.mRequestCounter.get()).longValue();
                BiometricService.this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$BiometricServiceWrapper$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        BiometricService.BiometricServiceWrapper.this.lambda$authenticate$1(iBinder, longValue, j, i, iBiometricServiceReceiver, str, promptInfo);
                    }
                });
                return longValue;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$authenticate$1(IBinder iBinder, long j, long j2, int i, IBiometricServiceReceiver iBiometricServiceReceiver, String str, PromptInfo promptInfo) {
            BiometricService.this.handleAuthenticate(iBinder, j, j2, i, iBiometricServiceReceiver, str, promptInfo);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void cancelAuthentication(IBinder iBinder, String str, final long j) {
            super.cancelAuthentication_enforcePermission();
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = iBinder;
            obtain.arg2 = str;
            obtain.arg3 = Long.valueOf(j);
            BiometricService.this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$BiometricServiceWrapper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    BiometricService.BiometricServiceWrapper.this.lambda$cancelAuthentication$2(j);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$cancelAuthentication$2(long j) {
            BiometricService.this.handleCancelAuthentication(j);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public int canAuthenticate(String str, int i, int i2, @BiometricManager.Authenticators.Types int i3) {
            super.canAuthenticate_enforcePermission();
            Slog.d("BiometricService", "canAuthenticate: User=" + i + ", Caller=" + i2 + ", Authenticators=" + i3);
            if (!Utils.isValidAuthenticatorConfig(i3)) {
                throw new SecurityException("Invalid authenticator configuration");
            }
            try {
                return BiometricService.this.createPreAuthInfo(str, i, i3).getCanAuthenticateResult();
            } catch (RemoteException e) {
                Slog.e("BiometricService", "Remote exception", e);
                return 1;
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public boolean hasEnrolledBiometrics(int i, String str) {
            super.hasEnrolledBiometrics_enforcePermission();
            try {
                Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
                while (it.hasNext()) {
                    if (it.next().impl.hasEnrolledTemplates(i, str)) {
                        return true;
                    }
                }
                return false;
            } catch (RemoteException e) {
                Slog.e("BiometricService", "Remote exception", e);
                return false;
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public synchronized void registerAuthenticator(int i, int i2, @BiometricManager.Authenticators.Types int i3, IBiometricAuthenticator iBiometricAuthenticator) {
            super.registerAuthenticator_enforcePermission();
            Slog.d("BiometricService", "Registering ID: " + i + " Modality: " + i2 + " Strength: " + i3);
            if (iBiometricAuthenticator == null) {
                throw new IllegalArgumentException("Authenticator must not be null. Did you forget to modify the core/res/res/values/xml overlay for config_biometric_sensors?");
            }
            if (i3 != 15 && i3 != 255 && i3 != 4095) {
                throw new IllegalStateException("Unsupported strength");
            }
            Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
            while (it.hasNext()) {
                if (it.next().f1133id == i) {
                    throw new IllegalStateException("Cannot register duplicate authenticator");
                }
            }
            BiometricService biometricService = BiometricService.this;
            biometricService.mSensors.add(new BiometricSensor(biometricService.getContext(), i, i2, i3, iBiometricAuthenticator) { // from class: com.android.server.biometrics.BiometricService.BiometricServiceWrapper.1
                @Override // com.android.server.biometrics.BiometricSensor
                public boolean confirmationAlwaysRequired(int i4) {
                    return BiometricService.this.mSettingObserver.getConfirmationAlwaysRequired(this.modality, i4);
                }

                @Override // com.android.server.biometrics.BiometricSensor
                public boolean confirmationSupported() {
                    return Utils.isConfirmationSupported(this.modality);
                }
            });
            BiometricService.this.mBiometricStrengthController.updateStrengths();
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void registerEnabledOnKeyguardCallback(IBiometricEnabledOnKeyguardCallback iBiometricEnabledOnKeyguardCallback, int i) {
            super.registerEnabledOnKeyguardCallback_enforcePermission();
            BiometricService.this.mEnabledOnKeyguardCallbacks.add(new EnabledOnKeyguardCallback(iBiometricEnabledOnKeyguardCallback));
            try {
                iBiometricEnabledOnKeyguardCallback.onChanged(BiometricService.this.mSettingObserver.getEnabledOnKeyguard(i), i);
            } catch (RemoteException e) {
                Slog.w("BiometricService", "Remote exception", e);
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void invalidateAuthenticatorIds(int i, int i2, IInvalidationCallback iInvalidationCallback) {
            super.invalidateAuthenticatorIds_enforcePermission();
            InvalidationTracker.start(BiometricService.this.getContext(), BiometricService.this.mSensors, i, i2, iInvalidationCallback);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public long[] getAuthenticatorIds(int i) {
            super.getAuthenticatorIds_enforcePermission();
            ArrayList arrayList = new ArrayList();
            Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
            while (it.hasNext()) {
                BiometricSensor next = it.next();
                try {
                    boolean hasEnrolledTemplates = next.impl.hasEnrolledTemplates(i, BiometricService.this.getContext().getOpPackageName());
                    long authenticatorId = next.impl.getAuthenticatorId(i);
                    if (hasEnrolledTemplates && Utils.isAtLeastStrength(next.getCurrentStrength(), 15)) {
                        arrayList.add(Long.valueOf(authenticatorId));
                    } else {
                        Slog.d("BiometricService", "Sensor " + next + ", sensorId " + next.f1133id + ", hasEnrollments: " + hasEnrolledTemplates + " cannot participate in Keystore operations");
                    }
                } catch (RemoteException e) {
                    Slog.e("BiometricService", "RemoteException", e);
                }
            }
            long[] jArr = new long[arrayList.size()];
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                jArr[i2] = ((Long) arrayList.get(i2)).longValue();
            }
            return jArr;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void resetLockoutTimeBound(IBinder iBinder, String str, int i, int i2, byte[] bArr) {
            super.resetLockoutTimeBound_enforcePermission();
            if (!Utils.isAtLeastStrength(BiometricService.this.getSensorForId(i).getCurrentStrength(), 15)) {
                Slog.w("BiometricService", "Sensor: " + i + " is does not meet the required strength to request resetLockout");
                return;
            }
            Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
            while (it.hasNext()) {
                BiometricSensor next = it.next();
                if (next.f1133id != i) {
                    try {
                        SensorPropertiesInternal sensorProperties = next.impl.getSensorProperties(BiometricService.this.getContext().getOpPackageName());
                        boolean z = sensorProperties.resetLockoutRequiresHardwareAuthToken;
                        boolean z2 = z && !sensorProperties.resetLockoutRequiresChallenge;
                        boolean z3 = !z;
                        if (z2 || z3) {
                            Slog.d("BiometricService", "resetLockout from: " + i + ", for: " + next.f1133id + ", userId: " + i2);
                            next.impl.resetLockout(iBinder, str, i2, bArr);
                        }
                    } catch (RemoteException e) {
                        Slog.e("BiometricService", "Remote exception", e);
                    }
                }
            }
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public void resetLockout(int i, byte[] bArr) {
            super.resetLockout_enforcePermission();
            StringBuilder sb = new StringBuilder();
            sb.append("resetLockout(userId=");
            sb.append(i);
            sb.append(", hat=");
            sb.append(bArr == null ? "null " : "present");
            sb.append(")");
            Slog.d("BiometricService", sb.toString());
            BiometricService.this.mBiometricContext.getAuthSessionCoordinator().resetLockoutFor(i, 15, -1L);
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public int getCurrentStrength(int i) {
            super.getCurrentStrength_enforcePermission();
            Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
            while (it.hasNext()) {
                BiometricSensor next = it.next();
                if (next.f1133id == i) {
                    return next.getCurrentStrength();
                }
            }
            Slog.e("BiometricService", "Unknown sensorId: " + i);
            return 0;
        }

        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public int getCurrentModality(String str, int i, int i2, @BiometricManager.Authenticators.Types int i3) {
            super.getCurrentModality_enforcePermission();
            Slog.d("BiometricService", "getCurrentModality: User=" + i + ", Caller=" + i2 + ", Authenticators=" + i3);
            if (!Utils.isValidAuthenticatorConfig(i3)) {
                throw new SecurityException("Invalid authenticator configuration");
            }
            try {
                return ((Integer) BiometricService.this.createPreAuthInfo(str, i, i3).getPreAuthenticateStatus().first).intValue();
            } catch (RemoteException e) {
                Slog.e("BiometricService", "Remote exception", e);
                return 0;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v3, types: [boolean] */
        /* JADX WARN: Type inference failed for: r0v5 */
        /* JADX WARN: Type inference failed for: r0v6 */
        /* JADX WARN: Type inference failed for: r0v7 */
        @EnforcePermission("android.permission.USE_BIOMETRIC_INTERNAL")
        public int getSupportedModalities(@BiometricManager.Authenticators.Types int i) {
            super.getSupportedModalities_enforcePermission();
            Slog.d("BiometricService", "getSupportedModalities: Authenticators=" + i);
            if (!Utils.isValidAuthenticatorConfig(i)) {
                throw new SecurityException("Invalid authenticator configuration");
            }
            int isCredentialRequested = Utils.isCredentialRequested(i);
            if (Utils.isBiometricRequested(i)) {
                int publicBiometricStrength = Utils.getPublicBiometricStrength(i);
                Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
                while (it.hasNext()) {
                    BiometricSensor next = it.next();
                    if (Utils.isAtLeastStrength(next.getCurrentStrength(), publicBiometricStrength)) {
                        isCredentialRequested = (isCredentialRequested == true ? 1 : 0) | next.modality;
                    }
                }
            }
            return isCredentialRequested;
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(BiometricService.this.getContext(), "BiometricService", printWriter)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    try {
                    } catch (RemoteException e) {
                        Slog.e("BiometricService", "Remote exception", e);
                    }
                    if (strArr.length > 0) {
                        if ("--proto".equals(strArr[0])) {
                            boolean z = true;
                            if (strArr.length <= 1 || !"--clear-scheduler-buffer".equals(strArr[1])) {
                                z = false;
                            }
                            Slog.d("BiometricService", "ClearSchedulerBuffer: " + z);
                            ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
                            AuthSession authSession = BiometricService.this.mAuthSession;
                            protoOutputStream.write(1159641169922L, authSession != null ? authSession.getState() : 0);
                            Iterator<BiometricSensor> it = BiometricService.this.mSensors.iterator();
                            while (it.hasNext()) {
                                protoOutputStream.write(2246267895809L, it.next().impl.dumpSensorServiceStateProto(z));
                            }
                            protoOutputStream.flush();
                        }
                    }
                    BiometricService.this.dumpInternal(printWriter);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    public final PreAuthInfo createPreAuthInfo(String str, int i, @BiometricManager.Authenticators.Types int i2) throws RemoteException {
        PromptInfo promptInfo = new PromptInfo();
        promptInfo.setAuthenticators(i2);
        return PreAuthInfo.create(this.mTrustManager, this.mDevicePolicyManager, this.mSettingObserver, this.mSensors, i, promptInfo, str, false, getContext());
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public IActivityManager getActivityManagerService() {
            return ActivityManager.getService();
        }

        public ITrustManager getTrustManager() {
            return ITrustManager.Stub.asInterface(ServiceManager.getService("trust"));
        }

        public IStatusBarService getStatusBarService() {
            return IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        }

        public SettingObserver getSettingObserver(Context context, Handler handler, List<EnabledOnKeyguardCallback> list) {
            return new SettingObserver(context, handler, list);
        }

        public KeyStore getKeyStore() {
            return KeyStore.getInstance();
        }

        public boolean isDebugEnabled(Context context, int i) {
            return Utils.isDebugEnabled(context, i);
        }

        public void publishBinderService(BiometricService biometricService, IBiometricService.Stub stub) {
            biometricService.publishBinderService("biometric", stub);
        }

        public BiometricStrengthController getBiometricStrengthController(BiometricService biometricService) {
            return new BiometricStrengthController(biometricService);
        }

        public DevicePolicyManager getDevicePolicyManager(Context context) {
            return (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        }

        public List<FingerprintSensorPropertiesInternal> getFingerprintSensorProperties(Context context) {
            FingerprintManager fingerprintManager;
            if (context.getPackageManager().hasSystemFeature("android.hardware.fingerprint") && (fingerprintManager = (FingerprintManager) context.getSystemService(FingerprintManager.class)) != null) {
                return fingerprintManager.getSensorPropertiesInternal();
            }
            return new ArrayList();
        }

        public Supplier<Long> getRequestGenerator() {
            final AtomicLong atomicLong = new AtomicLong(0L);
            return new Supplier() { // from class: com.android.server.biometrics.BiometricService$Injector$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    Long lambda$getRequestGenerator$0;
                    lambda$getRequestGenerator$0 = BiometricService.Injector.lambda$getRequestGenerator$0(atomicLong);
                    return lambda$getRequestGenerator$0;
                }
            };
        }

        public static /* synthetic */ Long lambda$getRequestGenerator$0(AtomicLong atomicLong) {
            return Long.valueOf(atomicLong.incrementAndGet());
        }

        public BiometricContext getBiometricContext(Context context) {
            return BiometricContext.getInstance(context);
        }
    }

    public BiometricService(Context context) {
        this(context, new Injector());
    }

    @VisibleForTesting
    public BiometricService(Context context, Injector injector) {
        super(context);
        this.mRandom = new Random();
        this.mSensors = new ArrayList<>();
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mInjector = injector;
        this.mDevicePolicyManager = injector.getDevicePolicyManager(context);
        this.mImpl = new BiometricServiceWrapper();
        ArrayList arrayList = new ArrayList();
        this.mEnabledOnKeyguardCallbacks = arrayList;
        this.mSettingObserver = injector.getSettingObserver(context, handler, arrayList);
        this.mRequestCounter = injector.getRequestGenerator();
        this.mBiometricContext = injector.getBiometricContext(context);
        try {
            injector.getActivityManagerService().registerUserSwitchObserver(new UserSwitchObserver() { // from class: com.android.server.biometrics.BiometricService.3
                public void onUserSwitchComplete(int i) {
                    BiometricService.this.mSettingObserver.updateContentObserver();
                    BiometricService.this.mSettingObserver.notifyEnabledOnKeyguardCallbacks(i);
                }
            }, BiometricService.class.getName());
        } catch (RemoteException e) {
            Slog.e("BiometricService", "Failed to register user switch observer", e);
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        this.mKeyStore = this.mInjector.getKeyStore();
        this.mStatusBarService = this.mInjector.getStatusBarService();
        this.mTrustManager = this.mInjector.getTrustManager();
        this.mInjector.publishBinderService(this, this.mImpl);
        BiometricStrengthController biometricStrengthController = this.mInjector.getBiometricStrengthController(this);
        this.mBiometricStrengthController = biometricStrengthController;
        biometricStrengthController.startListening();
    }

    public final boolean isStrongBiometric(int i) {
        Iterator<BiometricSensor> it = this.mSensors.iterator();
        while (it.hasNext()) {
            BiometricSensor next = it.next();
            if (next.f1133id == i) {
                return Utils.isAtLeastStrength(next.getCurrentStrength(), 15);
            }
        }
        Slog.e("BiometricService", "Unknown sensorId: " + i);
        return false;
    }

    public final AuthSession getAuthSessionIfCurrent(long j) {
        AuthSession authSession = this.mAuthSession;
        if (authSession == null || authSession.getRequestId() != j) {
            return null;
        }
        return authSession;
    }

    public final void handleAuthenticationSucceeded(long j, int i, byte[] bArr) {
        Slog.v("BiometricService", "handleAuthenticationSucceeded(), sensorId: " + i);
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.e("BiometricService", "handleAuthenticationSucceeded: AuthSession is null");
        } else {
            authSessionIfCurrent.onAuthenticationSucceeded(i, isStrongBiometric(i), bArr);
        }
    }

    public final void handleAuthenticationRejected(long j, int i) {
        Slog.v("BiometricService", "handleAuthenticationRejected()");
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleAuthenticationRejected: AuthSession is not current");
        } else {
            authSessionIfCurrent.onAuthenticationRejected(i);
        }
    }

    public final void handleAuthenticationTimedOut(long j, int i, int i2, int i3, int i4) {
        Slog.v("BiometricService", "handleAuthenticationTimedOut(), sensorId: " + i + ", cookie: " + i2 + ", error: " + i3 + ", vendorCode: " + i4);
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleAuthenticationTimedOut: AuthSession is not current");
        } else {
            authSessionIfCurrent.onAuthenticationTimedOut(i, i2, i3, i4);
        }
    }

    public final void handleOnError(long j, int i, int i2, int i3, int i4) {
        Slog.d("BiometricService", "handleOnError() sensorId: " + i + ", cookie: " + i2 + ", error: " + i3 + ", vendorCode: " + i4);
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleOnError: AuthSession is not current");
            return;
        }
        try {
            if (authSessionIfCurrent.onErrorReceived(i, i2, i3, i4)) {
                Slog.d("BiometricService", "handleOnError: AuthSession finished");
                this.mAuthSession = null;
            }
        } catch (RemoteException e) {
            Slog.e("BiometricService", "RemoteException", e);
        }
    }

    public final void handleOnAcquired(long j, int i, int i2, int i3) {
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "onAcquired: AuthSession is not current");
        } else {
            authSessionIfCurrent.onAcquired(i, i2, i3);
        }
    }

    public final void handleOnDismissed(long j, int i, byte[] bArr) {
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.e("BiometricService", "onDismissed: " + i + ", AuthSession is not current");
            return;
        }
        authSessionIfCurrent.onDialogDismissed(i, bArr);
        this.mAuthSession = null;
    }

    public final void handleOnTryAgainPressed(long j) {
        Slog.d("BiometricService", "onTryAgainPressed");
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleOnTryAgainPressed: AuthSession is not current");
        } else {
            authSessionIfCurrent.onTryAgainPressed();
        }
    }

    public final void handleOnDeviceCredentialPressed(long j) {
        Slog.d("BiometricService", "onDeviceCredentialPressed");
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleOnDeviceCredentialPressed: AuthSession is not current");
        } else {
            authSessionIfCurrent.onDeviceCredentialPressed();
        }
    }

    public final void handleOnSystemEvent(long j, int i) {
        Slog.d("BiometricService", "onSystemEvent: " + i);
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleOnSystemEvent: AuthSession is not current");
        } else {
            authSessionIfCurrent.onSystemEvent(i);
        }
    }

    /* renamed from: handleClientDied */
    public final void lambda$createClientDeathReceiver$0(long j) {
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleClientDied: AuthSession is not current");
            return;
        }
        Slog.e("BiometricService", "Session: " + authSessionIfCurrent);
        if (authSessionIfCurrent.onClientDied()) {
            this.mAuthSession = null;
        }
    }

    public final void handleOnDialogAnimatedIn(long j) {
        Slog.d("BiometricService", "handleOnDialogAnimatedIn");
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleOnDialogAnimatedIn: AuthSession is not current");
        } else {
            authSessionIfCurrent.onDialogAnimatedIn();
        }
    }

    public final void handleOnReadyForAuthentication(long j, int i) {
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleOnReadyForAuthentication: AuthSession is not current");
        } else {
            authSessionIfCurrent.onCookieReceived(i);
        }
    }

    public final void handleAuthenticate(final IBinder iBinder, final long j, final long j2, final int i, final IBiometricServiceReceiver iBiometricServiceReceiver, final String str, final PromptInfo promptInfo) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.biometrics.BiometricService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BiometricService.this.lambda$handleAuthenticate$2(i, promptInfo, str, j, iBinder, j2, iBiometricServiceReceiver);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleAuthenticate$2(int i, PromptInfo promptInfo, String str, long j, IBinder iBinder, long j2, IBiometricServiceReceiver iBiometricServiceReceiver) {
        try {
            PreAuthInfo create = PreAuthInfo.create(this.mTrustManager, this.mDevicePolicyManager, this.mSettingObserver, this.mSensors, i, promptInfo, str, promptInfo.isDisallowBiometricsIfPolicyExists(), getContext());
            Pair<Integer, Integer> preAuthenticateStatus = create.getPreAuthenticateStatus();
            Slog.d("BiometricService", "handleAuthenticate: modality(" + preAuthenticateStatus.first + "), status(" + preAuthenticateStatus.second + "), preAuthInfo: " + create + " requestId: " + j + " promptInfo.isIgnoreEnrollmentState: " + promptInfo.isIgnoreEnrollmentState());
            if (((Integer) preAuthenticateStatus.second).intValue() != 0 && ((Integer) preAuthenticateStatus.second).intValue() != 18) {
                iBiometricServiceReceiver.onError(((Integer) preAuthenticateStatus.first).intValue(), ((Integer) preAuthenticateStatus.second).intValue(), 0);
                return;
            }
            if (create.credentialRequested && create.credentialAvailable && create.eligibleSensors.isEmpty()) {
                promptInfo.setAuthenticators(32768);
            }
            authenticateInternal(iBinder, j, j2, i, iBiometricServiceReceiver, str, promptInfo, create);
        } catch (RemoteException e) {
            Slog.e("BiometricService", "Remote exception", e);
        }
    }

    public final void authenticateInternal(IBinder iBinder, long j, long j2, int i, IBiometricServiceReceiver iBiometricServiceReceiver, String str, PromptInfo promptInfo, PreAuthInfo preAuthInfo) {
        Slog.d("BiometricService", "Creating authSession with authRequest: " + preAuthInfo);
        if (this.mAuthSession != null) {
            Slog.w("BiometricService", "Existing AuthSession: " + this.mAuthSession);
            this.mAuthSession.onCancelAuthSession(true);
            this.mAuthSession = null;
        }
        AuthSession authSession = new AuthSession(getContext(), this.mBiometricContext, this.mStatusBarService, createSysuiReceiver(j), this.mKeyStore, this.mRandom, createClientDeathReceiver(j), preAuthInfo, iBinder, j, j2, i, createBiometricSensorReceiver(j), iBiometricServiceReceiver, str, promptInfo, this.mInjector.isDebugEnabled(getContext(), i), this.mInjector.getFingerprintSensorProperties(getContext()));
        this.mAuthSession = authSession;
        try {
            authSession.goToInitialState();
        } catch (RemoteException e) {
            Slog.e("BiometricService", "RemoteException", e);
        }
    }

    public final void handleCancelAuthentication(long j) {
        AuthSession authSessionIfCurrent = getAuthSessionIfCurrent(j);
        if (authSessionIfCurrent == null) {
            Slog.w("BiometricService", "handleCancelAuthentication: AuthSession is not current");
        } else if (authSessionIfCurrent.onCancelAuthSession(false)) {
            Slog.d("BiometricService", "handleCancelAuthentication: AuthSession finished");
            this.mAuthSession = null;
        }
    }

    public final BiometricSensor getSensorForId(int i) {
        Iterator<BiometricSensor> it = this.mSensors.iterator();
        while (it.hasNext()) {
            BiometricSensor next = it.next();
            if (next.f1133id == i) {
                return next;
            }
        }
        return null;
    }

    public final void dumpInternal(PrintWriter printWriter) {
        printWriter.println("Legacy Settings: " + this.mSettingObserver.mUseLegacyFaceOnlySettings);
        printWriter.println();
        printWriter.println("Sensors:");
        Iterator<BiometricSensor> it = this.mSensors.iterator();
        while (it.hasNext()) {
            printWriter.println(" " + it.next());
        }
        printWriter.println();
        printWriter.println("CurrentSession: " + this.mAuthSession);
        printWriter.println();
    }
}
