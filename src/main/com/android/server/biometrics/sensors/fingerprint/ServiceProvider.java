package com.android.server.biometrics.sensors.fingerprint;

import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.biometrics.fingerprint.PointerContext;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintAuthenticateOptions;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.hardware.fingerprint.ISidefpsController;
import android.hardware.fingerprint.IUdfpsOverlay;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.os.IBinder;
import com.android.server.biometrics.sensors.BiometricServiceProvider;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import java.util.List;
/* loaded from: classes.dex */
public interface ServiceProvider extends BiometricServiceProvider<FingerprintSensorPropertiesInternal> {
    void cancelAuthentication(int i, IBinder iBinder, long j);

    void cancelEnrollment(int i, IBinder iBinder, long j);

    ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str);

    List<Fingerprint> getEnrolledFingerprints(int i, int i2);

    void onPointerDown(long j, int i, PointerContext pointerContext);

    void onPointerUp(long j, int i, PointerContext pointerContext);

    void onPowerPressed();

    void onUiReady(long j, int i);

    void rename(int i, int i2, int i3, String str);

    long scheduleAuthenticate(IBinder iBinder, long j, int i, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FingerprintAuthenticateOptions fingerprintAuthenticateOptions, boolean z, int i2, boolean z2);

    void scheduleAuthenticate(IBinder iBinder, long j, int i, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FingerprintAuthenticateOptions fingerprintAuthenticateOptions, long j2, boolean z, int i2, boolean z2);

    long scheduleEnroll(int i, IBinder iBinder, byte[] bArr, int i2, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str, int i3);

    long scheduleFingerDetect(IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FingerprintAuthenticateOptions fingerprintAuthenticateOptions, int i);

    void scheduleGenerateChallenge(int i, int i2, IBinder iBinder, IFingerprintServiceReceiver iFingerprintServiceReceiver, String str);

    void scheduleInternalCleanup(int i, int i2, ClientMonitorCallback clientMonitorCallback, boolean z);

    void scheduleInvalidateAuthenticatorId(int i, int i2, IInvalidationCallback iInvalidationCallback);

    void scheduleRemove(int i, IBinder iBinder, IFingerprintServiceReceiver iFingerprintServiceReceiver, int i2, int i3, String str);

    void scheduleRemoveAll(int i, IBinder iBinder, IFingerprintServiceReceiver iFingerprintServiceReceiver, int i2, String str);

    void scheduleResetLockout(int i, int i2, byte[] bArr);

    void scheduleRevokeChallenge(int i, int i2, IBinder iBinder, String str, long j);

    default void scheduleWatchdog(int i) {
    }

    void setSidefpsController(ISidefpsController iSidefpsController);

    void setUdfpsOverlay(IUdfpsOverlay iUdfpsOverlay);

    void setUdfpsOverlayController(IUdfpsOverlayController iUdfpsOverlayController);

    void startPreparedClient(int i, int i2);
}
