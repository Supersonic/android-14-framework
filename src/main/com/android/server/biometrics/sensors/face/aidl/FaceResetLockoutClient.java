package com.android.server.biometrics.sensors.face.aidl;

import android.content.Context;
import android.hardware.biometrics.BiometricManager;
import android.hardware.keymaster.HardwareAuthToken;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.HardwareAuthTokenUtils;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ErrorConsumer;
import com.android.server.biometrics.sensors.HalClientMonitor;
import com.android.server.biometrics.sensors.LockoutCache;
import com.android.server.biometrics.sensors.LockoutResetDispatcher;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class FaceResetLockoutClient extends HalClientMonitor<AidlSession> implements ErrorConsumer {
    public final int mBiometricStrength;
    public final HardwareAuthToken mHardwareAuthToken;
    public final LockoutCache mLockoutCache;
    public final LockoutResetDispatcher mLockoutResetDispatcher;

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public int getProtoEnum() {
        return 12;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public boolean interruptsPrecedingClients() {
        return true;
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void unableToStart() {
    }

    public FaceResetLockoutClient(Context context, Supplier<AidlSession> supplier, int i, String str, int i2, BiometricLogger biometricLogger, BiometricContext biometricContext, byte[] bArr, LockoutCache lockoutCache, LockoutResetDispatcher lockoutResetDispatcher, @BiometricManager.Authenticators.Types int i3) {
        super(context, supplier, null, null, i, str, 0, i2, biometricLogger, biometricContext);
        this.mHardwareAuthToken = HardwareAuthTokenUtils.toHardwareAuthToken(bArr);
        this.mLockoutCache = lockoutCache;
        this.mLockoutResetDispatcher = lockoutResetDispatcher;
        this.mBiometricStrength = i3;
    }

    @Override // com.android.server.biometrics.sensors.BaseClientMonitor
    public void start(ClientMonitorCallback clientMonitorCallback) {
        super.start(clientMonitorCallback);
        startHalOperation();
    }

    @Override // com.android.server.biometrics.sensors.HalClientMonitor
    public void startHalOperation() {
        try {
            getFreshDaemon().getSession().resetLockout(this.mHardwareAuthToken);
        } catch (RemoteException e) {
            Slog.e("FaceResetLockoutClient", "Unable to reset lockout", e);
            this.mCallback.onClientFinished(this, false);
        }
    }

    public void onLockoutCleared() {
        resetLocalLockoutStateToNone(getSensorId(), getTargetUserId(), this.mLockoutCache, this.mLockoutResetDispatcher);
        this.mCallback.onClientFinished(this, true);
        getBiometricContext().getAuthSessionCoordinator().resetLockoutFor(getTargetUserId(), this.mBiometricStrength, getRequestId());
    }

    public static void resetLocalLockoutStateToNone(int i, int i2, LockoutCache lockoutCache, LockoutResetDispatcher lockoutResetDispatcher) {
        lockoutCache.setLockoutModeForUser(i2, 0);
        lockoutResetDispatcher.notifyLockoutResetCallbacks(i);
    }

    @Override // com.android.server.biometrics.sensors.ErrorConsumer
    public void onError(int i, int i2) {
        Slog.e("FaceResetLockoutClient", "Error during resetLockout: " + i);
        this.mCallback.onClientFinished(this, false);
    }
}
