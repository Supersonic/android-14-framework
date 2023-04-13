package com.android.server.display.brightness.strategy;

import android.hardware.display.DisplayManagerInternal;
import com.android.server.display.DisplayBrightnessState;
import com.android.server.display.brightness.BrightnessUtils;
/* loaded from: classes.dex */
public class OverrideBrightnessStrategy implements DisplayBrightnessStrategy {
    @Override // com.android.server.display.brightness.strategy.DisplayBrightnessStrategy
    public String getName() {
        return "OverrideBrightnessStrategy";
    }

    @Override // com.android.server.display.brightness.strategy.DisplayBrightnessStrategy
    public DisplayBrightnessState updateBrightness(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest) {
        float f = displayPowerRequest.screenBrightnessOverride;
        return BrightnessUtils.constructDisplayBrightnessState(6, f, f, getName());
    }
}
