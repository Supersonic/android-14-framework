package android.hardware.biometrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public interface BiometricFingerprintConstants {
    public static final int BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL = 14;
    public static final int BIOMETRIC_ERROR_POWER_PRESSED = 19;
    public static final int BIOMETRIC_ERROR_RE_ENROLL = 16;
    public static final int BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = 15;
    public static final int FINGERPRINT_ACQUIRED_GOOD = 0;
    public static final int FINGERPRINT_ACQUIRED_IMAGER_DIRTY = 3;
    public static final int FINGERPRINT_ACQUIRED_IMMOBILE = 9;
    public static final int FINGERPRINT_ACQUIRED_INSUFFICIENT = 2;
    public static final int FINGERPRINT_ACQUIRED_PARTIAL = 1;
    public static final int FINGERPRINT_ACQUIRED_POWER_PRESSED = 11;
    public static final int FINGERPRINT_ACQUIRED_START = 7;
    public static final int FINGERPRINT_ACQUIRED_TOO_BRIGHT = 10;
    public static final int FINGERPRINT_ACQUIRED_TOO_FAST = 5;
    public static final int FINGERPRINT_ACQUIRED_TOO_SLOW = 4;
    public static final int FINGERPRINT_ACQUIRED_UNKNOWN = 8;
    public static final int FINGERPRINT_ACQUIRED_VENDOR = 6;
    public static final int FINGERPRINT_ACQUIRED_VENDOR_BASE = 1000;
    public static final int FINGERPRINT_ERROR_BAD_CALIBRATION = 18;
    public static final int FINGERPRINT_ERROR_CANCELED = 5;
    public static final int FINGERPRINT_ERROR_HW_NOT_PRESENT = 12;
    public static final int FINGERPRINT_ERROR_HW_UNAVAILABLE = 1;
    public static final int FINGERPRINT_ERROR_LOCKOUT = 7;
    public static final int FINGERPRINT_ERROR_LOCKOUT_PERMANENT = 9;
    public static final int FINGERPRINT_ERROR_NEGATIVE_BUTTON = 13;
    public static final int FINGERPRINT_ERROR_NO_FINGERPRINTS = 11;
    public static final int FINGERPRINT_ERROR_NO_SPACE = 4;
    public static final int FINGERPRINT_ERROR_TIMEOUT = 3;
    public static final int FINGERPRINT_ERROR_UNABLE_TO_PROCESS = 2;
    public static final int FINGERPRINT_ERROR_UNABLE_TO_REMOVE = 6;
    public static final int FINGERPRINT_ERROR_UNKNOWN = 17;
    public static final int FINGERPRINT_ERROR_USER_CANCELED = 10;
    public static final int FINGERPRINT_ERROR_VENDOR = 8;
    public static final int FINGERPRINT_ERROR_VENDOR_BASE = 1000;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FingerprintAcquired {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FingerprintError {
    }

    static boolean shouldDisableUdfpsDisplayMode(int acquiredInfo) {
        switch (acquiredInfo) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 9:
            case 10:
                return true;
            case 7:
                return false;
            case 8:
            default:
                return false;
        }
    }
}
