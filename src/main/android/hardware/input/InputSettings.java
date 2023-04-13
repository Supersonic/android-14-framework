package android.hardware.input;

import android.content.Context;
import android.provider.Settings;
/* loaded from: classes2.dex */
public class InputSettings {
    public static final float DEFAULT_MAXIMUM_OBSCURING_OPACITY_FOR_TOUCH = 0.8f;
    public static final int DEFAULT_POINTER_SPEED = 0;
    public static final int MAX_POINTER_SPEED = 7;
    public static final int MIN_POINTER_SPEED = -7;

    private InputSettings() {
    }

    public static int getPointerSpeed(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.POINTER_SPEED, 0);
    }

    public static void setPointerSpeed(Context context, int speed) {
        if (speed < -7 || speed > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        Settings.System.putInt(context.getContentResolver(), Settings.System.POINTER_SPEED, speed);
    }

    public static float getMaximumObscuringOpacityForTouch(Context context) {
        return Settings.Global.getFloat(context.getContentResolver(), Settings.Global.MAXIMUM_OBSCURING_OPACITY_FOR_TOUCH, 0.8f);
    }

    public static void setMaximumObscuringOpacityForTouch(Context context, float opacity) {
        if (opacity < 0.0f || opacity > 1.0f) {
            throw new IllegalArgumentException("Maximum obscuring opacity for touch should be >= 0 and <= 1");
        }
        Settings.Global.putFloat(context.getContentResolver(), Settings.Global.MAXIMUM_OBSCURING_OPACITY_FOR_TOUCH, opacity);
    }

    public static boolean isStylusEverUsed(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.STYLUS_EVER_USED, 0) == 1;
    }

    public static void setStylusEverUsed(Context context, boolean stylusEverUsed) {
        Settings.Global.putInt(context.getContentResolver(), Settings.Global.STYLUS_EVER_USED, stylusEverUsed ? 1 : 0);
    }

    public static int getTouchpadPointerSpeed(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), Settings.System.TOUCHPAD_POINTER_SPEED, 0, -2);
    }

    public static void setTouchpadPointerSpeed(Context context, int speed) {
        if (speed < -7 || speed > 7) {
            throw new IllegalArgumentException("speed out of range");
        }
        Settings.System.putIntForUser(context.getContentResolver(), Settings.System.TOUCHPAD_POINTER_SPEED, speed, -2);
    }

    public static boolean useTouchpadNaturalScrolling(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), Settings.System.TOUCHPAD_NATURAL_SCROLLING, 0, -2) == 1;
    }

    public static void setTouchpadNaturalScrolling(Context context, boolean enabled) {
        Settings.System.putIntForUser(context.getContentResolver(), Settings.System.TOUCHPAD_NATURAL_SCROLLING, enabled ? 1 : 0, -2);
    }

    public static boolean useTouchpadTapToClick(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), Settings.System.TOUCHPAD_TAP_TO_CLICK, 0, -2) == 1;
    }

    public static void setTouchpadTapToClick(Context context, boolean enabled) {
        Settings.System.putIntForUser(context.getContentResolver(), Settings.System.TOUCHPAD_TAP_TO_CLICK, enabled ? 1 : 0, -2);
    }

    public static boolean useTouchpadRightClickZone(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), Settings.System.TOUCHPAD_RIGHT_CLICK_ZONE, 0, -2) == 1;
    }

    public static void setTouchpadRightClickZone(Context context, boolean enabled) {
        Settings.System.putIntForUser(context.getContentResolver(), Settings.System.TOUCHPAD_RIGHT_CLICK_ZONE, enabled ? 1 : 0, -2);
    }
}
