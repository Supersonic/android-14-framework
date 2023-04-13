package android.hardware.biometrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public interface BiometricOverlayConstants {
    public static final int REASON_AUTH_BP = 3;
    public static final int REASON_AUTH_KEYGUARD = 4;
    public static final int REASON_AUTH_OTHER = 5;
    public static final int REASON_AUTH_SETTINGS = 6;
    public static final int REASON_ENROLL_ENROLLING = 2;
    public static final int REASON_ENROLL_FIND_SENSOR = 1;
    public static final int REASON_UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ShowReason {
    }
}
