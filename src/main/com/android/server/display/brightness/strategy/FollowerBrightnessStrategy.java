package com.android.server.display.brightness.strategy;

import android.hardware.display.DisplayManagerInternal;
import com.android.server.display.DisplayBrightnessState;
import com.android.server.display.brightness.BrightnessUtils;
/* loaded from: classes.dex */
public class FollowerBrightnessStrategy implements DisplayBrightnessStrategy {
    public float mBrightnessToFollow = Float.NaN;
    public final int mDisplayId;

    @Override // com.android.server.display.brightness.strategy.DisplayBrightnessStrategy
    public String getName() {
        return "FollowerBrightnessStrategy";
    }

    public FollowerBrightnessStrategy(int i) {
        this.mDisplayId = i;
    }

    @Override // com.android.server.display.brightness.strategy.DisplayBrightnessStrategy
    public DisplayBrightnessState updateBrightness(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest) {
        float f = this.mBrightnessToFollow;
        return BrightnessUtils.constructDisplayBrightnessState(10, f, f, getName());
    }

    public float getBrightnessToFollow() {
        return this.mBrightnessToFollow;
    }

    public void setBrightnessToFollow(float f) {
        this.mBrightnessToFollow = f;
    }
}
