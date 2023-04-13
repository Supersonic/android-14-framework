package android.hardware.biometrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public interface BiometricFaceConstants {
    public static final int BIOMETRIC_ERROR_NO_DEVICE_CREDENTIAL = 14;
    public static final int BIOMETRIC_ERROR_POWER_PRESSED = 19;
    public static final int BIOMETRIC_ERROR_RE_ENROLL = 16;
    public static final int BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = 15;
    public static final int FACE_ACQUIRED_DARK_GLASSES_DETECTED = 25;
    public static final int FACE_ACQUIRED_FACE_OBSCURED = 19;
    public static final int FACE_ACQUIRED_FIRST_FRAME_RECEIVED = 24;
    public static final int FACE_ACQUIRED_GOOD = 0;
    public static final int FACE_ACQUIRED_INSUFFICIENT = 1;
    public static final int FACE_ACQUIRED_MOUTH_COVERING_DETECTED = 26;
    public static final int FACE_ACQUIRED_NOT_DETECTED = 11;
    public static final int FACE_ACQUIRED_PAN_TOO_EXTREME = 16;
    public static final int FACE_ACQUIRED_POOR_GAZE = 10;
    public static final int FACE_ACQUIRED_RECALIBRATE = 13;
    public static final int FACE_ACQUIRED_ROLL_TOO_EXTREME = 18;
    public static final int FACE_ACQUIRED_SENSOR_DIRTY = 21;
    public static final int FACE_ACQUIRED_START = 20;
    public static final int FACE_ACQUIRED_TILT_TOO_EXTREME = 17;
    public static final int FACE_ACQUIRED_TOO_BRIGHT = 2;
    public static final int FACE_ACQUIRED_TOO_CLOSE = 4;
    public static final int FACE_ACQUIRED_TOO_DARK = 3;
    public static final int FACE_ACQUIRED_TOO_DIFFERENT = 14;
    public static final int FACE_ACQUIRED_TOO_FAR = 5;
    public static final int FACE_ACQUIRED_TOO_HIGH = 6;
    public static final int FACE_ACQUIRED_TOO_LEFT = 9;
    public static final int FACE_ACQUIRED_TOO_LOW = 7;
    public static final int FACE_ACQUIRED_TOO_MUCH_MOTION = 12;
    public static final int FACE_ACQUIRED_TOO_RIGHT = 8;
    public static final int FACE_ACQUIRED_TOO_SIMILAR = 15;
    public static final int FACE_ACQUIRED_UNKNOWN = 23;
    public static final int FACE_ACQUIRED_VENDOR = 22;
    public static final int FACE_ACQUIRED_VENDOR_BASE = 1000;
    public static final int FACE_ERROR_CANCELED = 5;
    public static final int FACE_ERROR_HW_NOT_PRESENT = 12;
    public static final int FACE_ERROR_HW_UNAVAILABLE = 1;
    public static final int FACE_ERROR_LOCKOUT = 7;
    public static final int FACE_ERROR_LOCKOUT_PERMANENT = 9;
    public static final int FACE_ERROR_NEGATIVE_BUTTON = 13;
    public static final int FACE_ERROR_NOT_ENROLLED = 11;
    public static final int FACE_ERROR_NO_SPACE = 4;
    public static final int FACE_ERROR_TIMEOUT = 3;
    public static final int FACE_ERROR_UNABLE_TO_PROCESS = 2;
    public static final int FACE_ERROR_UNABLE_TO_REMOVE = 6;
    public static final int FACE_ERROR_UNKNOWN = 17;
    public static final int FACE_ERROR_USER_CANCELED = 10;
    public static final int FACE_ERROR_VENDOR = 8;
    public static final int FACE_ERROR_VENDOR_BASE = 1000;
    public static final int FEATURE_REQUIRE_ATTENTION = 1;
    public static final int FEATURE_REQUIRE_REQUIRE_DIVERSITY = 2;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FaceAcquired {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FaceError {
    }
}
