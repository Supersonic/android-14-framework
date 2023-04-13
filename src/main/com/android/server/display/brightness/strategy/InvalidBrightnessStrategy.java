package com.android.server.display.brightness.strategy;

import android.hardware.display.DisplayManagerInternal;
import com.android.server.display.DisplayBrightnessState;
import com.android.server.display.brightness.BrightnessUtils;
/* loaded from: classes.dex */
public class InvalidBrightnessStrategy implements DisplayBrightnessStrategy {
    @Override // com.android.server.display.brightness.strategy.DisplayBrightnessStrategy
    public String getName() {
        return "InvalidBrightnessStrategy";
    }

    @Override // com.android.server.display.brightness.strategy.DisplayBrightnessStrategy
    public DisplayBrightnessState updateBrightness(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest) {
        return BrightnessUtils.constructDisplayBrightnessState(0, Float.NaN, Float.NaN, getName());
    }
}
