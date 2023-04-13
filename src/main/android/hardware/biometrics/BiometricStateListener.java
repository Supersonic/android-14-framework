package android.hardware.biometrics;

import android.hardware.biometrics.IBiometricStateListener;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public abstract class BiometricStateListener extends IBiometricStateListener.Stub {
    public static final int ACTION_SENSOR_TOUCH = 0;
    public static final int STATE_AUTH_OTHER = 4;
    public static final int STATE_BP_AUTH = 3;
    public static final int STATE_ENROLLING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_KEYGUARD_AUTH = 2;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Action {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface State {
    }

    @Override // android.hardware.biometrics.IBiometricStateListener
    public void onStateChanged(int newState) {
    }

    @Override // android.hardware.biometrics.IBiometricStateListener
    public void onBiometricAction(int action) {
    }

    @Override // android.hardware.biometrics.IBiometricStateListener
    public void onEnrollmentsChanged(int userId, int sensorId, boolean hasEnrollments) {
    }
}
