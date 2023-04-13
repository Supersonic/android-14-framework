package android.hardware.biometrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public interface AuthenticateOptions {
    public static final int DISPLAY_STATE_AOD = 4;
    public static final int DISPLAY_STATE_LOCKSCREEN = 1;
    public static final int DISPLAY_STATE_NO_UI = 2;
    public static final int DISPLAY_STATE_SCREENSAVER = 3;
    public static final int DISPLAY_STATE_UNKNOWN = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DisplayState {
    }

    String getAttributionTag();

    int getDisplayState();

    String getOpPackageName();

    int getSensorId();

    int getUserId();
}
