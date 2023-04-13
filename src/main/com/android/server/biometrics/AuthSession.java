package com.android.server.biometrics;

import android.content.Context;
import android.hardware.biometrics.IBiometricSensorReceiver;
import android.hardware.biometrics.IBiometricServiceReceiver;
import android.hardware.biometrics.IBiometricSysuiReceiver;
import android.hardware.biometrics.PromptInfo;
import android.hardware.face.FaceManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.IBinder;
import android.os.RemoteException;
import android.security.KeyStore;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricFrameworkStatsLogger;
import com.android.server.biometrics.log.OperationContextExt;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
/* loaded from: classes.dex */
public final class AuthSession implements IBinder.DeathRecipient {
    public long mAuthenticatedTimeMs;
    public final BiometricContext mBiometricContext;
    public boolean mCancelled;
    public final ClientDeathReceiver mClientDeathReceiver;
    public final IBiometricServiceReceiver mClientReceiver;
    public final Context mContext;
    public final boolean mDebugEnabled;
    public int mErrorEscrow;
    public final List<FingerprintSensorPropertiesInternal> mFingerprintSensorProperties;
    public final KeyStore mKeyStore;
    public int mMultiSensorMode;
    public final String mOpPackageName;
    public final long mOperationId;
    public final PreAuthInfo mPreAuthInfo;
    @VisibleForTesting
    final PromptInfo mPromptInfo;
    public final Random mRandom;
    public final long mRequestId;
    @VisibleForTesting
    final IBiometricSensorReceiver mSensorReceiver;
    public int[] mSensors;
    public long mStartTimeMs;
    public final IStatusBarService mStatusBarService;
    @VisibleForTesting
    final IBiometricSysuiReceiver mSysuiReceiver;
    @VisibleForTesting
    final IBinder mToken;
    public byte[] mTokenEscrow;
    public final int mUserId;
    public int mVendorCodeEscrow;
    public int mState = 0;
    public int mAuthenticatedSensorId = -1;

    /* loaded from: classes.dex */
    public interface ClientDeathReceiver {
        void onClientDied();
    }

    public AuthSession(Context context, BiometricContext biometricContext, IStatusBarService iStatusBarService, IBiometricSysuiReceiver iBiometricSysuiReceiver, KeyStore keyStore, Random random, ClientDeathReceiver clientDeathReceiver, PreAuthInfo preAuthInfo, IBinder iBinder, long j, long j2, int i, IBiometricSensorReceiver iBiometricSensorReceiver, IBiometricServiceReceiver iBiometricServiceReceiver, String str, PromptInfo promptInfo, boolean z, List<FingerprintSensorPropertiesInternal> list) {
        Slog.d("BiometricService/AuthSession", "Creating AuthSession with: " + preAuthInfo);
        this.mContext = context;
        this.mBiometricContext = biometricContext;
        this.mStatusBarService = iStatusBarService;
        this.mSysuiReceiver = iBiometricSysuiReceiver;
        this.mKeyStore = keyStore;
        this.mRandom = random;
        this.mClientDeathReceiver = clientDeathReceiver;
        this.mPreAuthInfo = preAuthInfo;
        this.mToken = iBinder;
        this.mRequestId = j;
        this.mOperationId = j2;
        this.mUserId = i;
        this.mSensorReceiver = iBiometricSensorReceiver;
        this.mClientReceiver = iBiometricServiceReceiver;
        this.mOpPackageName = str;
        this.mPromptInfo = promptInfo;
        this.mDebugEnabled = z;
        this.mFingerprintSensorProperties = list;
        this.mCancelled = false;
        try {
            iBiometricServiceReceiver.asBinder().linkToDeath(this, 0);
        } catch (RemoteException unused) {
            Slog.w("BiometricService/AuthSession", "Unable to link to death");
        }
        setSensorsToStateUnknown();
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        Slog.e("BiometricService/AuthSession", "Binder died, session: " + this);
        this.mClientDeathReceiver.onClientDied();
    }

    public final int getEligibleModalities() {
        return this.mPreAuthInfo.getEligibleModalities();
    }

    public final void setSensorsToStateUnknown() {
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            biometricSensor.goToStateUnknown();
        }
    }

    public final void setSensorsToStateWaitingForCookie(boolean z) throws RemoteException {
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            int sensorState = biometricSensor.getSensorState();
            if (!z || sensorState == 5 || sensorState == 4) {
                biometricSensor.goToStateWaitingForCookie(isConfirmationRequired(biometricSensor), this.mToken, this.mOperationId, this.mUserId, this.mSensorReceiver, this.mOpPackageName, this.mRequestId, this.mRandom.nextInt(2147483646) + 1, this.mPromptInfo.isAllowBackgroundAuthentication());
            } else {
                Slog.d("BiometricService/AuthSession", "Skip retry because sensor: " + biometricSensor.f1133id + " is: " + sensorState);
            }
        }
    }

    public void goToInitialState() throws RemoteException {
        PreAuthInfo preAuthInfo = this.mPreAuthInfo;
        if (preAuthInfo.credentialAvailable && preAuthInfo.eligibleSensors.isEmpty()) {
            this.mState = 9;
            int[] iArr = new int[0];
            this.mSensors = iArr;
            this.mMultiSensorMode = 0;
            this.mStatusBarService.showAuthenticationDialog(this.mPromptInfo, this.mSysuiReceiver, iArr, true, false, this.mUserId, this.mOperationId, this.mOpPackageName, this.mRequestId, 0);
        } else if (!this.mPreAuthInfo.eligibleSensors.isEmpty()) {
            setSensorsToStateWaitingForCookie(false);
            this.mState = 1;
        } else {
            throw new IllegalStateException("No authenticators requested");
        }
    }

    public void onCookieReceived(int i) {
        if (this.mCancelled) {
            Slog.w("BiometricService/AuthSession", "Received cookie but already cancelled (ignoring): " + i);
        } else if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onCookieReceived after successful auth");
        } else {
            for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
                biometricSensor.goToStateCookieReturnedIfCookieMatches(i);
            }
            if (allCookiesReceived()) {
                this.mStartTimeMs = System.currentTimeMillis();
                startAllPreparedSensorsExceptFingerprint();
                if (this.mState != 5) {
                    try {
                        boolean isConfirmationRequiredByAnyEligibleSensor = isConfirmationRequiredByAnyEligibleSensor();
                        this.mSensors = new int[this.mPreAuthInfo.eligibleSensors.size()];
                        for (int i2 = 0; i2 < this.mPreAuthInfo.eligibleSensors.size(); i2++) {
                            this.mSensors[i2] = this.mPreAuthInfo.eligibleSensors.get(i2).f1133id;
                        }
                        this.mMultiSensorMode = getMultiSensorModeForNewSession(this.mPreAuthInfo.eligibleSensors);
                        this.mStatusBarService.showAuthenticationDialog(this.mPromptInfo, this.mSysuiReceiver, this.mSensors, this.mPreAuthInfo.shouldShowCredential(), isConfirmationRequiredByAnyEligibleSensor, this.mUserId, this.mOperationId, this.mOpPackageName, this.mRequestId, this.mMultiSensorMode);
                        this.mState = 2;
                        return;
                    } catch (RemoteException e) {
                        Slog.e("BiometricService/AuthSession", "Remote exception", e);
                        return;
                    }
                }
                this.mState = 3;
                return;
            }
            Slog.v("BiometricService/AuthSession", "onCookieReceived: still waiting");
        }
    }

    public final boolean isConfirmationRequired(BiometricSensor biometricSensor) {
        return biometricSensor.confirmationSupported() && (biometricSensor.confirmationAlwaysRequired(this.mUserId) || this.mPreAuthInfo.confirmationRequested);
    }

    public final boolean isConfirmationRequiredByAnyEligibleSensor() {
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            if (isConfirmationRequired(biometricSensor)) {
                return true;
            }
        }
        return false;
    }

    public static /* synthetic */ Boolean lambda$startAllPreparedSensorsExceptFingerprint$0(BiometricSensor biometricSensor) {
        return Boolean.valueOf(biometricSensor.modality != 2);
    }

    public final void startAllPreparedSensorsExceptFingerprint() {
        startAllPreparedSensors(new Function() { // from class: com.android.server.biometrics.AuthSession$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean lambda$startAllPreparedSensorsExceptFingerprint$0;
                lambda$startAllPreparedSensorsExceptFingerprint$0 = AuthSession.lambda$startAllPreparedSensorsExceptFingerprint$0((BiometricSensor) obj);
                return lambda$startAllPreparedSensorsExceptFingerprint$0;
            }
        });
    }

    public static /* synthetic */ Boolean lambda$startAllPreparedFingerprintSensors$1(BiometricSensor biometricSensor) {
        return Boolean.valueOf(biometricSensor.modality == 2);
    }

    public final void startAllPreparedFingerprintSensors() {
        startAllPreparedSensors(new Function() { // from class: com.android.server.biometrics.AuthSession$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean lambda$startAllPreparedFingerprintSensors$1;
                lambda$startAllPreparedFingerprintSensors$1 = AuthSession.lambda$startAllPreparedFingerprintSensors$1((BiometricSensor) obj);
                return lambda$startAllPreparedFingerprintSensors$1;
            }
        });
    }

    public final void startAllPreparedSensors(Function<BiometricSensor, Boolean> function) {
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            if (function.apply(biometricSensor).booleanValue()) {
                try {
                    biometricSensor.startSensor();
                } catch (RemoteException e) {
                    Slog.e("BiometricService/AuthSession", "Unable to start prepared client, sensor: " + biometricSensor, e);
                }
            }
        }
    }

    public final void cancelAllSensors() {
        cancelAllSensors(new Function() { // from class: com.android.server.biometrics.AuthSession$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean bool;
                BiometricSensor biometricSensor = (BiometricSensor) obj;
                bool = Boolean.TRUE;
                return bool;
            }
        });
    }

    public final void cancelAllSensors(Function<BiometricSensor, Boolean> function) {
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            try {
                if (function.apply(biometricSensor).booleanValue()) {
                    Slog.d("BiometricService/AuthSession", "Cancelling sensorId: " + biometricSensor.f1133id);
                    biometricSensor.goToStateCancelling(this.mToken, this.mOpPackageName, this.mRequestId);
                }
            } catch (RemoteException unused) {
                Slog.e("BiometricService/AuthSession", "Unable to cancel authentication");
            }
        }
    }

    public boolean onErrorReceived(int i, int i2, int i3, int i4) throws RemoteException {
        Slog.d("BiometricService/AuthSession", "onErrorReceived sensor: " + i + " error: " + i3);
        if (!containsCookie(i2)) {
            Slog.e("BiometricService/AuthSession", "Unknown/expired cookie: " + i2);
            return false;
        }
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            if (biometricSensor.getSensorState() == 3) {
                biometricSensor.goToStoppedStateIfCookieMatches(i2, i3);
            }
        }
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onErrorReceived after successful auth (ignoring)");
            return false;
        }
        this.mErrorEscrow = i3;
        this.mVendorCodeEscrow = i4;
        int sensorIdToModality = sensorIdToModality(i);
        int i5 = this.mState;
        if (i5 != 1) {
            if (i5 == 2 || i5 == 3) {
                boolean z = i3 == 7 || i3 == 9;
                if (isAllowDeviceCredential() && z) {
                    this.mState = 9;
                    this.mStatusBarService.onBiometricError(sensorIdToModality, i3, i4);
                } else if (i3 == 5) {
                    this.mStatusBarService.hideAuthenticationDialog(this.mRequestId);
                    this.mClientReceiver.onError(sensorIdToModality, i3, i4);
                    return true;
                } else {
                    this.mState = 8;
                    this.mStatusBarService.onBiometricError(sensorIdToModality, i3, i4);
                }
            } else if (i5 == 4) {
                this.mClientReceiver.onError(sensorIdToModality, i3, i4);
                this.mStatusBarService.hideAuthenticationDialog(this.mRequestId);
                return true;
            } else if (i5 == 9) {
                Slog.d("BiometricService/AuthSession", "Biometric canceled, ignoring from state: " + this.mState);
            } else if (i5 == 10) {
                this.mStatusBarService.hideAuthenticationDialog(this.mRequestId);
                return true;
            } else {
                Slog.e("BiometricService/AuthSession", "Unhandled error state, mState: " + this.mState);
            }
        } else if (isAllowDeviceCredential()) {
            this.mPromptInfo.setAuthenticators(Utils.removeBiometricBits(this.mPromptInfo.getAuthenticators()));
            this.mState = 9;
            this.mMultiSensorMode = 0;
            int[] iArr = new int[0];
            this.mSensors = iArr;
            this.mStatusBarService.showAuthenticationDialog(this.mPromptInfo, this.mSysuiReceiver, iArr, true, false, this.mUserId, this.mOperationId, this.mOpPackageName, this.mRequestId, 0);
        } else {
            this.mClientReceiver.onError(sensorIdToModality, i3, i4);
            return true;
        }
        return false;
    }

    public void onAcquired(int i, int i2, int i3) {
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onAcquired after successful auth");
            return;
        }
        String acquiredMessageForSensor = getAcquiredMessageForSensor(i, i2, i3);
        Slog.d("BiometricService/AuthSession", "sensorId: " + i + " acquiredInfo: " + i2 + " message: " + acquiredMessageForSensor);
        if (acquiredMessageForSensor == null) {
            return;
        }
        try {
            this.mStatusBarService.onBiometricHelp(sensorIdToModality(i), acquiredMessageForSensor);
        } catch (RemoteException e) {
            Slog.e("BiometricService/AuthSession", "Remote exception", e);
        }
    }

    public void onSystemEvent(int i) {
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onSystemEvent after successful auth");
        } else if (this.mPromptInfo.isReceiveSystemEvents()) {
            try {
                this.mClientReceiver.onSystemEvent(i);
            } catch (RemoteException e) {
                Slog.e("BiometricService/AuthSession", "RemoteException", e);
            }
        }
    }

    public void onDialogAnimatedIn() {
        if (this.mState != 2) {
            Slog.e("BiometricService/AuthSession", "onDialogAnimatedIn, unexpected state: " + this.mState);
            return;
        }
        this.mState = 3;
        startAllPreparedFingerprintSensors();
    }

    public void onTryAgainPressed() {
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onTryAgainPressed after successful auth");
            return;
        }
        if (this.mState != 4) {
            Slog.w("BiometricService/AuthSession", "onTryAgainPressed, state: " + this.mState);
        }
        try {
            setSensorsToStateWaitingForCookie(true);
            this.mState = 5;
        } catch (RemoteException e) {
            Slog.e("BiometricService/AuthSession", "RemoteException: " + e);
        }
    }

    public void onAuthenticationSucceeded(final int i, boolean z, byte[] bArr) {
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onAuthenticationSucceeded after successful auth");
            return;
        }
        this.mAuthenticatedSensorId = i;
        if (z) {
            this.mTokenEscrow = bArr;
        } else if (bArr != null) {
            Slog.w("BiometricService/AuthSession", "Dropping authToken for non-strong biometric, id: " + i);
        }
        try {
            this.mStatusBarService.onBiometricAuthenticated(sensorIdToModality(i));
            if (!isConfirmationRequiredByAnyEligibleSensor()) {
                this.mState = 7;
            } else {
                this.mAuthenticatedTimeMs = System.currentTimeMillis();
                this.mState = 6;
            }
        } catch (RemoteException e) {
            Slog.e("BiometricService/AuthSession", "RemoteException", e);
        }
        cancelAllSensors(new Function() { // from class: com.android.server.biometrics.AuthSession$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Boolean lambda$onAuthenticationSucceeded$3;
                lambda$onAuthenticationSucceeded$3 = AuthSession.lambda$onAuthenticationSucceeded$3(i, (BiometricSensor) obj);
                return lambda$onAuthenticationSucceeded$3;
            }
        });
    }

    public static /* synthetic */ Boolean lambda$onAuthenticationSucceeded$3(int i, BiometricSensor biometricSensor) {
        return Boolean.valueOf(biometricSensor.f1133id != i);
    }

    public void onAuthenticationRejected(int i) {
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onAuthenticationRejected after successful auth");
            return;
        }
        try {
            this.mStatusBarService.onBiometricError(sensorIdToModality(i), 100, 0);
            if (pauseSensorIfSupported(i)) {
                this.mState = 4;
            }
            this.mClientReceiver.onAuthenticationFailed();
        } catch (RemoteException e) {
            Slog.e("BiometricService/AuthSession", "RemoteException", e);
        }
    }

    public void onAuthenticationTimedOut(int i, int i2, int i3, int i4) {
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onAuthenticationTimedOut after successful auth");
            return;
        }
        try {
            this.mStatusBarService.onBiometricError(sensorIdToModality(i), i3, i4);
            pauseSensorIfSupported(i);
            this.mState = 4;
        } catch (RemoteException e) {
            Slog.e("BiometricService/AuthSession", "RemoteException", e);
        }
    }

    public final boolean pauseSensorIfSupported(final int i) {
        if (sensorIdToModality(i) == 8) {
            cancelAllSensors(new Function() { // from class: com.android.server.biometrics.AuthSession$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Boolean lambda$pauseSensorIfSupported$4;
                    lambda$pauseSensorIfSupported$4 = AuthSession.lambda$pauseSensorIfSupported$4(i, (BiometricSensor) obj);
                    return lambda$pauseSensorIfSupported$4;
                }
            });
            return true;
        }
        return false;
    }

    public static /* synthetic */ Boolean lambda$pauseSensorIfSupported$4(int i, BiometricSensor biometricSensor) {
        return Boolean.valueOf(biometricSensor.f1133id == i);
    }

    public void onDeviceCredentialPressed() {
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onDeviceCredentialPressed after successful auth");
            return;
        }
        cancelAllSensors();
        this.mState = 9;
    }

    public boolean onClientDied() {
        try {
            int i = this.mState;
            if (i == 2 || i == 3) {
                this.mState = 10;
                cancelAllSensors();
                return false;
            }
            this.mStatusBarService.hideAuthenticationDialog(this.mRequestId);
            return true;
        } catch (RemoteException e) {
            Slog.e("BiometricService/AuthSession", "Remote Exception: " + e);
            return true;
        }
    }

    public final boolean hasAuthenticated() {
        return this.mAuthenticatedSensorId != -1;
    }

    public final void logOnDialogDismissed(int i) {
        if (i == 1) {
            BiometricFrameworkStatsLogger.getInstance().authenticate(this.mBiometricContext.updateContext(new OperationContextExt(), isCrypto()), statsModality(), 0, 2, this.mDebugEnabled, System.currentTimeMillis() - this.mAuthenticatedTimeMs, 3, this.mPreAuthInfo.confirmationRequested, this.mUserId, -1.0f);
        } else {
            BiometricFrameworkStatsLogger.getInstance().error(this.mBiometricContext.updateContext(new OperationContextExt(), isCrypto()), statsModality(), 2, 2, this.mDebugEnabled, System.currentTimeMillis() - this.mStartTimeMs, i == 2 ? 13 : i == 3 ? 10 : 0, 0, this.mUserId);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:13:0x003c A[Catch: all -> 0x0067, RemoteException -> 0x0069, TryCatch #1 {RemoteException -> 0x0069, blocks: (B:6:0x000b, B:7:0x0011, B:8:0x0017, B:9:0x0025, B:10:0x0032, B:11:0x0038, B:13:0x003c, B:15:0x005d, B:14:0x0057, B:20:0x006b), top: B:27:0x0005, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0057 A[Catch: all -> 0x0067, RemoteException -> 0x0069, TryCatch #1 {RemoteException -> 0x0069, blocks: (B:6:0x000b, B:7:0x0011, B:8:0x0017, B:9:0x0025, B:10:0x0032, B:11:0x0038, B:13:0x003c, B:15:0x005d, B:14:0x0057, B:20:0x006b), top: B:27:0x0005, outer: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onDialogDismissed(int i, byte[] bArr) {
        byte[] bArr2;
        logOnDialogDismissed(i);
        try {
            try {
                switch (i) {
                    case 1:
                    case 4:
                        bArr2 = this.mTokenEscrow;
                        if (bArr2 == null) {
                            int addAuthToken = this.mKeyStore.addAuthToken(bArr2);
                            Slog.d("BiometricService/AuthSession", "addAuthToken: " + addAuthToken);
                        } else {
                            Slog.e("BiometricService/AuthSession", "mTokenEscrow is null");
                        }
                        this.mClientReceiver.onAuthenticationSucceeded(Utils.getAuthenticationTypeForResult(i));
                        break;
                    case 2:
                        this.mClientReceiver.onDialogDismissed(i);
                        break;
                    case 3:
                        this.mClientReceiver.onError(getEligibleModalities(), 10, 0);
                        break;
                    case 5:
                    case 6:
                        this.mClientReceiver.onError(getEligibleModalities(), this.mErrorEscrow, this.mVendorCodeEscrow);
                        break;
                    case 7:
                        if (bArr != null) {
                            this.mKeyStore.addAuthToken(bArr);
                        } else {
                            Slog.e("BiometricService/AuthSession", "credentialAttestation is null");
                        }
                        bArr2 = this.mTokenEscrow;
                        if (bArr2 == null) {
                        }
                        this.mClientReceiver.onAuthenticationSucceeded(Utils.getAuthenticationTypeForResult(i));
                        break;
                    default:
                        Slog.w("BiometricService/AuthSession", "Unhandled reason: " + i);
                        break;
                }
            } catch (RemoteException e) {
                Slog.e("BiometricService/AuthSession", "Remote exception", e);
            }
            cancelAllSensors();
        } catch (Throwable th) {
            cancelAllSensors();
            throw th;
        }
    }

    public boolean onCancelAuthSession(boolean z) {
        if (hasAuthenticated()) {
            Slog.d("BiometricService/AuthSession", "onCancelAuthSession after successful auth");
            return true;
        }
        this.mCancelled = true;
        int i = this.mState;
        boolean z2 = i == 1 || i == 2 || i == 3;
        cancelAllSensors();
        if (!z2 || z) {
            try {
                this.mClientReceiver.onError(getEligibleModalities(), 5, 0);
                this.mStatusBarService.hideAuthenticationDialog(this.mRequestId);
                return true;
            } catch (RemoteException e) {
                Slog.e("BiometricService/AuthSession", "Remote exception", e);
                return false;
            }
        }
        return false;
    }

    public boolean isCrypto() {
        return this.mOperationId != 0;
    }

    public final boolean containsCookie(int i) {
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            if (biometricSensor.getCookie() == i) {
                return true;
            }
        }
        return false;
    }

    public final boolean isAllowDeviceCredential() {
        return Utils.isCredentialRequested(this.mPromptInfo);
    }

    @VisibleForTesting
    public boolean allCookiesReceived() {
        int numSensorsWaitingForCookie = this.mPreAuthInfo.numSensorsWaitingForCookie();
        Slog.d("BiometricService/AuthSession", "Remaining cookies: " + numSensorsWaitingForCookie);
        return numSensorsWaitingForCookie == 0;
    }

    public int getState() {
        return this.mState;
    }

    public long getRequestId() {
        return this.mRequestId;
    }

    public final int statsModality() {
        int i = 0;
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            int i2 = biometricSensor.modality;
            if ((i2 & 2) != 0) {
                i |= 1;
            }
            if ((i2 & 4) != 0) {
                i |= 2;
            }
            if ((i2 & 8) != 0) {
                i |= 4;
            }
        }
        return i;
    }

    public final int sensorIdToModality(int i) {
        for (BiometricSensor biometricSensor : this.mPreAuthInfo.eligibleSensors) {
            if (i == biometricSensor.f1133id) {
                return biometricSensor.modality;
            }
        }
        Slog.e("BiometricService/AuthSession", "Unknown sensor: " + i);
        return 0;
    }

    public final String getAcquiredMessageForSensor(int i, int i2, int i3) {
        int sensorIdToModality = sensorIdToModality(i);
        if (sensorIdToModality != 2) {
            if (sensorIdToModality != 8) {
                return null;
            }
            return FaceManager.getAuthHelpMessage(this.mContext, i2, i3);
        }
        return FingerprintManager.getAcquiredString(this.mContext, i2, i3);
    }

    public static int getMultiSensorModeForNewSession(Collection<BiometricSensor> collection) {
        boolean z = false;
        boolean z2 = false;
        for (BiometricSensor biometricSensor : collection) {
            int i = biometricSensor.modality;
            if (i == 8) {
                z = true;
            } else if (i == 2) {
                z2 = true;
            }
        }
        return (z && z2) ? 1 : 0;
    }

    public String toString() {
        return "State: " + this.mState + ", cancelled: " + this.mCancelled + ", isCrypto: " + isCrypto() + ", PreAuthInfo: " + this.mPreAuthInfo + ", requestId: " + this.mRequestId;
    }
}
