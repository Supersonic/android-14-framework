package android.view;

import android.media.AudioManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;
/* loaded from: classes4.dex */
public class SoundEffectConstants {
    public static final int CLICK = 0;
    public static final int NAVIGATION_DOWN = 4;
    public static final int NAVIGATION_LEFT = 1;
    public static final int NAVIGATION_REPEAT_DOWN = 8;
    public static final int NAVIGATION_REPEAT_LEFT = 5;
    public static final int NAVIGATION_REPEAT_RIGHT = 7;
    public static final int NAVIGATION_REPEAT_UP = 6;
    public static final int NAVIGATION_RIGHT = 3;
    public static final int NAVIGATION_UP = 2;
    private static final Random NAVIGATION_REPEAT_RANDOMIZER = new Random();
    private static int sLastNavigationRepeatSoundEffectId = -1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface NavigationSoundEffect {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface SoundEffect {
    }

    private SoundEffectConstants() {
    }

    public static int getContantForFocusDirection(int direction) {
        switch (direction) {
            case 1:
            case 33:
                return 2;
            case 2:
            case 130:
                return 4;
            case 17:
                return 1;
            case 66:
                return 3;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
    }

    public static int getConstantForFocusDirection(int direction, boolean repeating) {
        if (repeating) {
            switch (direction) {
                case 1:
                case 33:
                    return 6;
                case 2:
                case 130:
                    return 8;
                case 17:
                    return 5;
                case 66:
                    return 7;
                default:
                    throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
            }
        }
        return getContantForFocusDirection(direction);
    }

    public static boolean isNavigationRepeat(int effectId) {
        return effectId == 8 || effectId == 5 || effectId == 7 || effectId == 6;
    }

    public static int nextNavigationRepeatSoundEffectId() {
        int next = NAVIGATION_REPEAT_RANDOMIZER.nextInt(3);
        if (next >= sLastNavigationRepeatSoundEffectId) {
            next++;
        }
        sLastNavigationRepeatSoundEffectId = next;
        return AudioManager.getNthNavigationRepeatSoundEffect(next);
    }
}
