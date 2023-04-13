package com.android.server.display.brightness;

import android.util.MathUtils;
import com.android.server.display.DisplayBrightnessState;
/* loaded from: classes.dex */
public final class BrightnessUtils {
    public static boolean isValidBrightnessValue(float f) {
        return !Float.isNaN(f) && f >= 0.0f && f <= 1.0f;
    }

    public static float clampAbsoluteBrightness(float f) {
        return MathUtils.constrain(f, 0.0f, 1.0f);
    }

    public static DisplayBrightnessState constructDisplayBrightnessState(int i, float f, float f2, String str) {
        BrightnessReason brightnessReason = new BrightnessReason();
        brightnessReason.setReason(i);
        return new DisplayBrightnessState.Builder().setBrightness(f).setSdrBrightness(f2).setBrightnessReason(brightnessReason).setDisplayBrightnessStrategyName(str).build();
    }
}
