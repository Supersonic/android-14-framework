package com.android.server.biometrics.sensors.face;

import android.hardware.biometrics.IInvalidationCallback;
import android.hardware.biometrics.ITestSession;
import android.hardware.biometrics.ITestSessionCallback;
import android.hardware.face.Face;
import android.hardware.face.FaceAuthenticateOptions;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.hardware.face.IFaceServiceReceiver;
import android.os.IBinder;
import android.view.Surface;
import com.android.server.biometrics.sensors.BiometricServiceProvider;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.ClientMonitorCallbackConverter;
import java.io.FileDescriptor;
import java.util.List;
/* loaded from: classes.dex */
public interface ServiceProvider extends BiometricServiceProvider<FaceSensorPropertiesInternal> {
    void cancelAuthentication(int i, IBinder iBinder, long j);

    void cancelEnrollment(int i, IBinder iBinder, long j);

    void cancelFaceDetect(int i, IBinder iBinder, long j);

    ITestSession createTestSession(int i, ITestSessionCallback iTestSessionCallback, String str);

    void dumpHal(int i, FileDescriptor fileDescriptor, String[] strArr);

    List<Face> getEnrolledFaces(int i, int i2);

    long scheduleAuthenticate(IBinder iBinder, long j, int i, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FaceAuthenticateOptions faceAuthenticateOptions, boolean z, int i2, boolean z2);

    void scheduleAuthenticate(IBinder iBinder, long j, int i, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FaceAuthenticateOptions faceAuthenticateOptions, long j2, boolean z, int i2, boolean z2);

    long scheduleEnroll(int i, IBinder iBinder, byte[] bArr, int i2, IFaceServiceReceiver iFaceServiceReceiver, String str, int[] iArr, Surface surface, boolean z);

    long scheduleFaceDetect(IBinder iBinder, ClientMonitorCallbackConverter clientMonitorCallbackConverter, FaceAuthenticateOptions faceAuthenticateOptions, int i);

    void scheduleGenerateChallenge(int i, int i2, IBinder iBinder, IFaceServiceReceiver iFaceServiceReceiver, String str);

    void scheduleGetFeature(int i, IBinder iBinder, int i2, int i3, ClientMonitorCallbackConverter clientMonitorCallbackConverter, String str);

    void scheduleInternalCleanup(int i, int i2, ClientMonitorCallback clientMonitorCallback, boolean z);

    void scheduleRemove(int i, IBinder iBinder, int i2, int i3, IFaceServiceReceiver iFaceServiceReceiver, String str);

    void scheduleRemoveAll(int i, IBinder iBinder, int i2, IFaceServiceReceiver iFaceServiceReceiver, String str);

    void scheduleResetLockout(int i, int i2, byte[] bArr);

    void scheduleRevokeChallenge(int i, int i2, IBinder iBinder, String str, long j);

    void scheduleSetFeature(int i, IBinder iBinder, int i2, int i3, boolean z, byte[] bArr, IFaceServiceReceiver iFaceServiceReceiver, String str);

    default void scheduleWatchdog(int i) {
    }

    void startPreparedClient(int i, int i2);

    default void scheduleInvalidateAuthenticatorId(int i, int i2, IInvalidationCallback iInvalidationCallback) {
        throw new IllegalStateException("Providers that support invalidation must override this method");
    }
}
