package android.hardware.biometrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public interface BiometricConstants {
    public static final int BIOMETRIC_ACQUIRED_GOOD = 0;
    public static final int BIOMETRIC_ACQUIRED_IMAGER_DIRTY = 3;
    public static final int BIOMETRIC_ACQUIRED_INSUFFICIENT = 2;
    public static final int BIOMETRIC_ACQUIRED_PARTIAL = 1;
    public static final int BIOMETRIC_ACQUIRED_TOO_FAST = 5;
    public static final int BIOMETRIC_ACQUIRED_TOO_SLOW = 4;
    public static final int BIOMETRIC_ACQUIRED_VENDOR = 6;
    public static final int BIOMETRIC_ACQUIRED_VENDOR_BASE = 1000;
    public static final int BIOMETRIC_ERROR_CANCELED = 5;
    public static final int BIOMETRIC_ERROR_HW_NOT_PRESENT = 12;
    public static final int BIOMETRIC_ERROR_HW_UNAVAILABLE = 1;
    public static final int BIOMETRIC_ERROR_LOCKOUT = 7;
    public static final int BIOMETRIC_ERROR_LOCKOUT_PERMANENT = 9;
    public static final int BIOMETRIC_ERROR_NEGATIVE_BUTTON = 13;
    public static final int BIOMETRIC_ERROR_NO_BIOMETRICS = 11;
    public static final int BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL = 14;
    public static final int BIOMETRIC_ERROR_NO_SPACE = 4;
    public static final int BIOMETRIC_ERROR_POWER_PRESSED = 19;
    public static final int BIOMETRIC_ERROR_RE_ENROLL = 16;
    public static final int BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = 15;
    public static final int BIOMETRIC_ERROR_SENSOR_PRIVACY_ENABLED = 18;
    public static final int BIOMETRIC_ERROR_TIMEOUT = 3;
    public static final int BIOMETRIC_ERROR_UNABLE_TO_PROCESS = 2;
    public static final int BIOMETRIC_ERROR_UNABLE_TO_REMOVE = 6;
    public static final int BIOMETRIC_ERROR_USER_CANCELED = 10;
    public static final int BIOMETRIC_ERROR_VENDOR = 8;
    public static final int BIOMETRIC_ERROR_VENDOR_BASE = 1000;
    public static final int BIOMETRIC_LOCKOUT_NONE = 0;
    public static final int BIOMETRIC_LOCKOUT_PERMANENT = 2;
    public static final int BIOMETRIC_LOCKOUT_TIMED = 1;
    public static final int BIOMETRIC_PAUSED_REJECTED = 100;
    public static final int BIOMETRIC_SUCCESS = 0;
    public static final int BIOMETRIC_SYSTEM_EVENT_EARLY_USER_CANCEL = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Acquired {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Errors {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface LockoutMode {
    }
}
