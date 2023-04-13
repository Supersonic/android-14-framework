package com.android.server.display.brightness.strategy;

import android.hardware.display.DisplayManagerInternal;
import com.android.server.display.DisplayBrightnessState;
import com.android.server.display.brightness.BrightnessUtils;
/* loaded from: classes.dex */
public class DozeBrightnessStrategy implements DisplayBrightnessStrategy {
    @Override // com.android.server.display.brightness.strategy.DisplayBrightnessStrategy
    public String getName() {
        return "DozeBrightnessStrategy";
    }

    @Override // com.android.server.display.brightness.strategy.DisplayBrightnessStrategy
    public DisplayBrightnessState updateBrightness(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest) {
        float f = displayPowerRequest.dozeScreenBrightness;
        return BrightnessUtils.constructDisplayBrightnessState(2, f, f, getName());
    }
}
