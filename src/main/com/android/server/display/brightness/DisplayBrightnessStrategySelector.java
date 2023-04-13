package com.android.server.display.brightness;

import android.content.Context;
import android.hardware.display.DisplayManagerInternal;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.display.brightness.strategy.BoostBrightnessStrategy;
import com.android.server.display.brightness.strategy.DisplayBrightnessStrategy;
import com.android.server.display.brightness.strategy.DozeBrightnessStrategy;
import com.android.server.display.brightness.strategy.FollowerBrightnessStrategy;
import com.android.server.display.brightness.strategy.InvalidBrightnessStrategy;
import com.android.server.display.brightness.strategy.OverrideBrightnessStrategy;
import com.android.server.display.brightness.strategy.ScreenOffBrightnessStrategy;
import com.android.server.display.brightness.strategy.TemporaryBrightnessStrategy;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class DisplayBrightnessStrategySelector {
    public final boolean mAllowAutoBrightnessWhileDozingConfig;
    public final BoostBrightnessStrategy mBoostBrightnessStrategy;
    public final int mDisplayId;
    public final DozeBrightnessStrategy mDozeBrightnessStrategy;
    public final FollowerBrightnessStrategy mFollowerBrightnessStrategy;
    public final InvalidBrightnessStrategy mInvalidBrightnessStrategy;
    public String mOldBrightnessStrategyName;
    public final OverrideBrightnessStrategy mOverrideBrightnessStrategy;
    public final ScreenOffBrightnessStrategy mScreenOffBrightnessStrategy;
    public final TemporaryBrightnessStrategy mTemporaryBrightnessStrategy;

    public DisplayBrightnessStrategySelector(Context context, Injector injector, int i) {
        injector = injector == null ? new Injector() : injector;
        this.mDisplayId = i;
        this.mDozeBrightnessStrategy = injector.getDozeBrightnessStrategy();
        this.mScreenOffBrightnessStrategy = injector.getScreenOffBrightnessStrategy();
        this.mOverrideBrightnessStrategy = injector.getOverrideBrightnessStrategy();
        this.mTemporaryBrightnessStrategy = injector.getTemporaryBrightnessStrategy();
        this.mBoostBrightnessStrategy = injector.getBoostBrightnessStrategy();
        this.mFollowerBrightnessStrategy = injector.getFollowerBrightnessStrategy(i);
        InvalidBrightnessStrategy invalidBrightnessStrategy = injector.getInvalidBrightnessStrategy();
        this.mInvalidBrightnessStrategy = invalidBrightnessStrategy;
        this.mAllowAutoBrightnessWhileDozingConfig = context.getResources().getBoolean(17891347);
        this.mOldBrightnessStrategyName = invalidBrightnessStrategy.getName();
    }

    public DisplayBrightnessStrategy selectStrategy(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest, int i) {
        DisplayBrightnessStrategy displayBrightnessStrategy = this.mInvalidBrightnessStrategy;
        if (i == 1) {
            displayBrightnessStrategy = this.mScreenOffBrightnessStrategy;
        } else if (shouldUseDozeBrightnessStrategy(displayPowerRequest)) {
            displayBrightnessStrategy = this.mDozeBrightnessStrategy;
        } else if (BrightnessUtils.isValidBrightnessValue(this.mFollowerBrightnessStrategy.getBrightnessToFollow())) {
            displayBrightnessStrategy = this.mFollowerBrightnessStrategy;
        } else if (displayPowerRequest.boostScreenBrightness) {
            displayBrightnessStrategy = this.mBoostBrightnessStrategy;
        } else if (BrightnessUtils.isValidBrightnessValue(displayPowerRequest.screenBrightnessOverride)) {
            displayBrightnessStrategy = this.mOverrideBrightnessStrategy;
        } else if (BrightnessUtils.isValidBrightnessValue(this.mTemporaryBrightnessStrategy.getTemporaryScreenBrightness())) {
            displayBrightnessStrategy = this.mTemporaryBrightnessStrategy;
        }
        if (!this.mOldBrightnessStrategyName.equals(displayBrightnessStrategy.getName())) {
            Slog.i("DisplayBrightnessStrategySelector", "Changing the DisplayBrightnessStrategy from " + this.mOldBrightnessStrategyName + " to" + displayBrightnessStrategy.getName() + " for display " + this.mDisplayId);
            this.mOldBrightnessStrategyName = displayBrightnessStrategy.getName();
        }
        return displayBrightnessStrategy;
    }

    public TemporaryBrightnessStrategy getTemporaryDisplayBrightnessStrategy() {
        return this.mTemporaryBrightnessStrategy;
    }

    public FollowerBrightnessStrategy getFollowerDisplayBrightnessStrategy() {
        return this.mFollowerBrightnessStrategy;
    }

    public boolean isAllowAutoBrightnessWhileDozingConfig() {
        return this.mAllowAutoBrightnessWhileDozingConfig;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("DisplayBrightnessStrategySelector:");
        printWriter.println("  mDisplayId= " + this.mDisplayId);
        printWriter.println("  mOldBrightnessStrategyName= " + this.mOldBrightnessStrategyName);
        printWriter.println("  mAllowAutoBrightnessWhileDozingConfig= " + this.mAllowAutoBrightnessWhileDozingConfig);
        this.mTemporaryBrightnessStrategy.dump(new IndentingPrintWriter(printWriter, " "));
    }

    public final boolean shouldUseDozeBrightnessStrategy(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest) {
        return displayPowerRequest.policy == 1 && !this.mAllowAutoBrightnessWhileDozingConfig;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public ScreenOffBrightnessStrategy getScreenOffBrightnessStrategy() {
            return new ScreenOffBrightnessStrategy();
        }

        public DozeBrightnessStrategy getDozeBrightnessStrategy() {
            return new DozeBrightnessStrategy();
        }

        public OverrideBrightnessStrategy getOverrideBrightnessStrategy() {
            return new OverrideBrightnessStrategy();
        }

        public TemporaryBrightnessStrategy getTemporaryBrightnessStrategy() {
            return new TemporaryBrightnessStrategy();
        }

        public BoostBrightnessStrategy getBoostBrightnessStrategy() {
            return new BoostBrightnessStrategy();
        }

        public FollowerBrightnessStrategy getFollowerBrightnessStrategy(int i) {
            return new FollowerBrightnessStrategy(i);
        }

        public InvalidBrightnessStrategy getInvalidBrightnessStrategy() {
            return new InvalidBrightnessStrategy();
        }
    }
}
