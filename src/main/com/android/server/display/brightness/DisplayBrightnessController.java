package com.android.server.display.brightness;

import android.content.Context;
import android.hardware.display.DisplayManagerInternal;
import android.os.HandlerExecutor;
import android.util.IndentingPrintWriter;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.display.AutomaticBrightnessController;
import com.android.server.display.BrightnessSetting;
import com.android.server.display.DisplayBrightnessState;
import com.android.server.display.brightness.strategy.DisplayBrightnessStrategy;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class DisplayBrightnessController {
    public AutomaticBrightnessController mAutomaticBrightnessController;
    public final HandlerExecutor mBrightnessChangeExecutor;
    public final BrightnessSetting mBrightnessSetting;
    public BrightnessSetting.BrightnessSettingListener mBrightnessSettingListener;
    @GuardedBy({"mLock"})
    public float mCurrentScreenBrightness;
    @GuardedBy({"mLock"})
    public DisplayBrightnessStrategy mDisplayBrightnessStrategy;
    @GuardedBy({"mLock"})
    public DisplayBrightnessStrategySelector mDisplayBrightnessStrategySelector;
    public final int mDisplayId;
    public Runnable mOnBrightnessChangeRunnable;
    @GuardedBy({"mLock"})
    public float mPendingScreenBrightness;
    public final boolean mPersistBrightnessNitsForDefaultDisplay;
    public final float mScreenBrightnessDefault;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public float mLastUserSetScreenBrightness = Float.NaN;

    public DisplayBrightnessController(Context context, Injector injector, int i, float f, BrightnessSetting brightnessSetting, Runnable runnable, HandlerExecutor handlerExecutor) {
        injector = injector == null ? new Injector() : injector;
        this.mDisplayId = i;
        this.mBrightnessSetting = brightnessSetting;
        this.mPendingScreenBrightness = Float.NaN;
        this.mScreenBrightnessDefault = BrightnessUtils.clampAbsoluteBrightness(f);
        this.mCurrentScreenBrightness = getScreenBrightnessSetting();
        this.mOnBrightnessChangeRunnable = runnable;
        this.mDisplayBrightnessStrategySelector = injector.getDisplayBrightnessStrategySelector(context, i);
        this.mBrightnessChangeExecutor = handlerExecutor;
        this.mPersistBrightnessNitsForDefaultDisplay = context.getResources().getBoolean(17891761);
    }

    public DisplayBrightnessState updateBrightness(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest, int i) {
        DisplayBrightnessState updateBrightness;
        synchronized (this.mLock) {
            DisplayBrightnessStrategy selectStrategy = this.mDisplayBrightnessStrategySelector.selectStrategy(displayPowerRequest, i);
            this.mDisplayBrightnessStrategy = selectStrategy;
            updateBrightness = selectStrategy.updateBrightness(displayPowerRequest);
        }
        return updateBrightness;
    }

    public void setTemporaryBrightness(Float f) {
        synchronized (this.mLock) {
            setTemporaryBrightnessLocked(f.floatValue());
        }
    }

    public void setBrightnessToFollow(Float f) {
        synchronized (this.mLock) {
            this.mDisplayBrightnessStrategySelector.getFollowerDisplayBrightnessStrategy().setBrightnessToFollow(f.floatValue());
        }
    }

    public boolean isAllowAutoBrightnessWhileDozingConfig() {
        boolean isAllowAutoBrightnessWhileDozingConfig;
        synchronized (this.mLock) {
            isAllowAutoBrightnessWhileDozingConfig = this.mDisplayBrightnessStrategySelector.isAllowAutoBrightnessWhileDozingConfig();
        }
        return isAllowAutoBrightnessWhileDozingConfig;
    }

    public void setAndNotifyCurrentScreenBrightness(float f) {
        boolean z;
        synchronized (this.mLock) {
            z = f != this.mCurrentScreenBrightness;
            setCurrentScreenBrightnessLocked(f);
        }
        if (z) {
            notifyCurrentScreenBrightness();
        }
    }

    public float getCurrentBrightness() {
        float f;
        synchronized (this.mLock) {
            f = this.mCurrentScreenBrightness;
        }
        return f;
    }

    public float getPendingScreenBrightness() {
        float f;
        synchronized (this.mLock) {
            f = this.mPendingScreenBrightness;
        }
        return f;
    }

    public void setPendingScreenBrightness(float f) {
        synchronized (this.mLock) {
            this.mPendingScreenBrightness = f;
        }
    }

    public boolean updateUserSetScreenBrightness() {
        synchronized (this.mLock) {
            if (BrightnessUtils.isValidBrightnessValue(this.mPendingScreenBrightness)) {
                float f = this.mCurrentScreenBrightness;
                float f2 = this.mPendingScreenBrightness;
                if (f == f2) {
                    this.mPendingScreenBrightness = Float.NaN;
                    setTemporaryBrightnessLocked(Float.NaN);
                    return false;
                }
                setCurrentScreenBrightnessLocked(f2);
                this.mLastUserSetScreenBrightness = this.mPendingScreenBrightness;
                this.mPendingScreenBrightness = Float.NaN;
                setTemporaryBrightnessLocked(Float.NaN);
                notifyCurrentScreenBrightness();
                return true;
            }
            return false;
        }
    }

    public void registerBrightnessSettingChangeListener(BrightnessSetting.BrightnessSettingListener brightnessSettingListener) {
        this.mBrightnessSettingListener = brightnessSettingListener;
        this.mBrightnessSetting.registerListener(brightnessSettingListener);
    }

    public float getLastUserSetScreenBrightness() {
        float f;
        synchronized (this.mLock) {
            f = this.mLastUserSetScreenBrightness;
        }
        return f;
    }

    public float getScreenBrightnessSetting() {
        float clampAbsoluteBrightness;
        float brightness = this.mBrightnessSetting.getBrightness();
        synchronized (this.mLock) {
            if (Float.isNaN(brightness)) {
                brightness = this.mScreenBrightnessDefault;
            }
            clampAbsoluteBrightness = BrightnessUtils.clampAbsoluteBrightness(brightness);
        }
        return clampAbsoluteBrightness;
    }

    public void setBrightness(float f) {
        this.mBrightnessSetting.setBrightness(f);
        if (this.mDisplayId == 0 && this.mPersistBrightnessNitsForDefaultDisplay) {
            float convertToNits = convertToNits(f);
            if (convertToNits >= 0.0f) {
                this.mBrightnessSetting.setBrightnessNitsForDefaultDisplay(convertToNits);
            }
        }
    }

    public void updateScreenBrightnessSetting(float f) {
        synchronized (this.mLock) {
            if (BrightnessUtils.isValidBrightnessValue(f) && f != this.mCurrentScreenBrightness) {
                setCurrentScreenBrightnessLocked(f);
                notifyCurrentScreenBrightness();
                setBrightness(f);
            }
        }
    }

    public void setAutomaticBrightnessController(AutomaticBrightnessController automaticBrightnessController) {
        this.mAutomaticBrightnessController = automaticBrightnessController;
        loadNitBasedBrightnessSetting();
    }

    public float convertToNits(float f) {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController == null) {
            return -1.0f;
        }
        return automaticBrightnessController.convertToNits(f);
    }

    public float convertToFloatScale(float f) {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController == null) {
            return Float.NaN;
        }
        return automaticBrightnessController.convertToFloatScale(f);
    }

    public void stop() {
        BrightnessSetting brightnessSetting = this.mBrightnessSetting;
        if (brightnessSetting != null) {
            brightnessSetting.unregisterListener(this.mBrightnessSettingListener);
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("DisplayBrightnessController:");
        printWriter.println("  mDisplayId=: " + this.mDisplayId);
        printWriter.println("  mScreenBrightnessDefault=" + this.mScreenBrightnessDefault);
        printWriter.println("  mPersistBrightnessNitsForDefaultDisplay=" + this.mPersistBrightnessNitsForDefaultDisplay);
        synchronized (this.mLock) {
            printWriter.println("  mPendingScreenBrightness=" + this.mPendingScreenBrightness);
            printWriter.println("  mCurrentScreenBrightness=" + this.mCurrentScreenBrightness);
            printWriter.println("  mLastUserSetScreenBrightness=" + this.mLastUserSetScreenBrightness);
            if (this.mDisplayBrightnessStrategy != null) {
                printWriter.println("  Last selected DisplayBrightnessStrategy= " + this.mDisplayBrightnessStrategy.getName());
            }
            this.mDisplayBrightnessStrategySelector.dump(new IndentingPrintWriter(printWriter, " "));
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public DisplayBrightnessStrategySelector getDisplayBrightnessStrategySelector(Context context, int i) {
            return new DisplayBrightnessStrategySelector(context, null, i);
        }
    }

    @VisibleForTesting
    public BrightnessSetting.BrightnessSettingListener getBrightnessSettingListener() {
        return this.mBrightnessSettingListener;
    }

    @VisibleForTesting
    public DisplayBrightnessStrategy getCurrentDisplayBrightnessStrategy() {
        DisplayBrightnessStrategy displayBrightnessStrategy;
        synchronized (this.mLock) {
            displayBrightnessStrategy = this.mDisplayBrightnessStrategy;
        }
        return displayBrightnessStrategy;
    }

    @GuardedBy({"mLock"})
    public final void setTemporaryBrightnessLocked(float f) {
        this.mDisplayBrightnessStrategySelector.getTemporaryDisplayBrightnessStrategy().setTemporaryScreenBrightness(f);
    }

    @GuardedBy({"mLock"})
    public final void setCurrentScreenBrightnessLocked(float f) {
        if (f != this.mCurrentScreenBrightness) {
            this.mCurrentScreenBrightness = f;
        }
    }

    public final void notifyCurrentScreenBrightness() {
        this.mBrightnessChangeExecutor.execute(this.mOnBrightnessChangeRunnable);
    }

    public final void loadNitBasedBrightnessSetting() {
        if (this.mDisplayId == 0 && this.mPersistBrightnessNitsForDefaultDisplay) {
            float brightnessNitsForDefaultDisplay = this.mBrightnessSetting.getBrightnessNitsForDefaultDisplay();
            if (brightnessNitsForDefaultDisplay >= 0.0f) {
                float convertToFloatScale = convertToFloatScale(brightnessNitsForDefaultDisplay);
                if (BrightnessUtils.isValidBrightnessValue(convertToFloatScale)) {
                    this.mBrightnessSetting.setBrightness(convertToFloatScale);
                    synchronized (this.mLock) {
                        this.mCurrentScreenBrightness = convertToFloatScale;
                    }
                    return;
                }
            }
        }
        synchronized (this.mLock) {
            this.mCurrentScreenBrightness = getScreenBrightnessSetting();
        }
    }
}
