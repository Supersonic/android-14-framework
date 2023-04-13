package com.android.server.display.brightness.strategy;

import android.hardware.display.DisplayManagerInternal;
import com.android.server.display.DisplayBrightnessState;
/* loaded from: classes.dex */
public interface DisplayBrightnessStrategy {
    String getName();

    DisplayBrightnessState updateBrightness(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest);
}
