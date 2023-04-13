package com.android.server.display.brightness;

import android.hardware.display.BrightnessInfo;
import android.os.SystemClock;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public final class BrightnessEvent {
    public int mAdjustmentFlags;
    public boolean mAutomaticBrightnessEnabled;
    public float mBrightness;
    public String mDisplayBrightnessStrategyName;
    public int mDisplayId;
    public int mFlags;
    public float mHbmMax;
    public int mHbmMode;
    public float mInitialBrightness;
    public float mLux;
    public String mPhysicalDisplayId;
    public float mPowerFactor;
    public float mPreThresholdBrightness;
    public float mPreThresholdLux;
    public int mRbcStrength;
    public BrightnessReason mReason = new BrightnessReason();
    public float mRecommendedBrightness;
    public float mThermalMax;
    public long mTime;
    public boolean mWasShortTermModelActive;

    public BrightnessEvent(BrightnessEvent brightnessEvent) {
        copyFrom(brightnessEvent);
    }

    public BrightnessEvent(int i) {
        this.mDisplayId = i;
        reset();
    }

    public void copyFrom(BrightnessEvent brightnessEvent) {
        this.mReason.set(brightnessEvent.getReason());
        this.mDisplayId = brightnessEvent.getDisplayId();
        this.mPhysicalDisplayId = brightnessEvent.getPhysicalDisplayId();
        this.mTime = brightnessEvent.getTime();
        this.mLux = brightnessEvent.getLux();
        this.mPreThresholdLux = brightnessEvent.getPreThresholdLux();
        this.mInitialBrightness = brightnessEvent.getInitialBrightness();
        this.mBrightness = brightnessEvent.getBrightness();
        this.mRecommendedBrightness = brightnessEvent.getRecommendedBrightness();
        this.mPreThresholdBrightness = brightnessEvent.getPreThresholdBrightness();
        this.mHbmMode = brightnessEvent.getHbmMode();
        this.mHbmMax = brightnessEvent.getHbmMax();
        this.mRbcStrength = brightnessEvent.getRbcStrength();
        this.mThermalMax = brightnessEvent.getThermalMax();
        this.mPowerFactor = brightnessEvent.getPowerFactor();
        this.mWasShortTermModelActive = brightnessEvent.wasShortTermModelActive();
        this.mFlags = brightnessEvent.getFlags();
        this.mAdjustmentFlags = brightnessEvent.getAdjustmentFlags();
        this.mAutomaticBrightnessEnabled = brightnessEvent.isAutomaticBrightnessEnabled();
        this.mDisplayBrightnessStrategyName = brightnessEvent.getDisplayBrightnessStrategyName();
    }

    public void reset() {
        this.mReason = new BrightnessReason();
        this.mTime = SystemClock.uptimeMillis();
        this.mPhysicalDisplayId = "";
        this.mLux = 0.0f;
        this.mPreThresholdLux = 0.0f;
        this.mInitialBrightness = Float.NaN;
        this.mBrightness = Float.NaN;
        this.mRecommendedBrightness = Float.NaN;
        this.mPreThresholdBrightness = Float.NaN;
        this.mHbmMode = 0;
        this.mHbmMax = 1.0f;
        this.mRbcStrength = 0;
        this.mThermalMax = 1.0f;
        this.mPowerFactor = 1.0f;
        this.mWasShortTermModelActive = false;
        this.mFlags = 0;
        this.mAdjustmentFlags = 0;
        this.mAutomaticBrightnessEnabled = true;
        this.mDisplayBrightnessStrategyName = "";
    }

    public boolean equalsMainData(BrightnessEvent brightnessEvent) {
        return this.mReason.equals(brightnessEvent.mReason) && this.mDisplayId == brightnessEvent.mDisplayId && this.mPhysicalDisplayId.equals(brightnessEvent.mPhysicalDisplayId) && Float.floatToRawIntBits(this.mLux) == Float.floatToRawIntBits(brightnessEvent.mLux) && Float.floatToRawIntBits(this.mPreThresholdLux) == Float.floatToRawIntBits(brightnessEvent.mPreThresholdLux) && Float.floatToRawIntBits(this.mInitialBrightness) == Float.floatToRawIntBits(brightnessEvent.mInitialBrightness) && Float.floatToRawIntBits(this.mBrightness) == Float.floatToRawIntBits(brightnessEvent.mBrightness) && Float.floatToRawIntBits(this.mRecommendedBrightness) == Float.floatToRawIntBits(brightnessEvent.mRecommendedBrightness) && Float.floatToRawIntBits(this.mPreThresholdBrightness) == Float.floatToRawIntBits(brightnessEvent.mPreThresholdBrightness) && this.mHbmMode == brightnessEvent.mHbmMode && Float.floatToRawIntBits(this.mHbmMax) == Float.floatToRawIntBits(brightnessEvent.mHbmMax) && this.mRbcStrength == brightnessEvent.mRbcStrength && Float.floatToRawIntBits(this.mThermalMax) == Float.floatToRawIntBits(brightnessEvent.mThermalMax) && Float.floatToRawIntBits(this.mPowerFactor) == Float.floatToRawIntBits(brightnessEvent.mPowerFactor) && this.mWasShortTermModelActive == brightnessEvent.mWasShortTermModelActive && this.mFlags == brightnessEvent.mFlags && this.mAdjustmentFlags == brightnessEvent.mAdjustmentFlags && this.mAutomaticBrightnessEnabled == brightnessEvent.mAutomaticBrightnessEnabled && this.mDisplayBrightnessStrategyName.equals(brightnessEvent.mDisplayBrightnessStrategyName);
    }

    public String toString(boolean z) {
        String str;
        StringBuilder sb = new StringBuilder();
        if (z) {
            str = TimeUtils.formatForLogging(this.mTime) + " - ";
        } else {
            str = "";
        }
        sb.append(str);
        sb.append("BrightnessEvent: disp=");
        sb.append(this.mDisplayId);
        sb.append(", physDisp=");
        sb.append(this.mPhysicalDisplayId);
        sb.append(", brt=");
        sb.append(this.mBrightness);
        sb.append((this.mFlags & 8) != 0 ? "(user_set)" : "");
        sb.append(", initBrt=");
        sb.append(this.mInitialBrightness);
        sb.append(", rcmdBrt=");
        sb.append(this.mRecommendedBrightness);
        sb.append(", preBrt=");
        sb.append(this.mPreThresholdBrightness);
        sb.append(", lux=");
        sb.append(this.mLux);
        sb.append(", preLux=");
        sb.append(this.mPreThresholdLux);
        sb.append(", hbmMax=");
        sb.append(this.mHbmMax);
        sb.append(", hbmMode=");
        sb.append(BrightnessInfo.hbmToString(this.mHbmMode));
        sb.append(", rbcStrength=");
        sb.append(this.mRbcStrength);
        sb.append(", thrmMax=");
        sb.append(this.mThermalMax);
        sb.append(", powerFactor=");
        sb.append(this.mPowerFactor);
        sb.append(", wasShortTermModelActive=");
        sb.append(this.mWasShortTermModelActive);
        sb.append(", flags=");
        sb.append(flagsToString());
        sb.append(", reason=");
        sb.append(this.mReason.toString(this.mAdjustmentFlags));
        sb.append(", autoBrightness=");
        sb.append(this.mAutomaticBrightnessEnabled);
        sb.append(", strategy=");
        sb.append(this.mDisplayBrightnessStrategyName);
        return sb.toString();
    }

    public String toString() {
        return toString(true);
    }

    public BrightnessReason getReason() {
        return this.mReason;
    }

    public void setReason(BrightnessReason brightnessReason) {
        this.mReason = brightnessReason;
    }

    public long getTime() {
        return this.mTime;
    }

    public void setTime(long j) {
        this.mTime = j;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public String getPhysicalDisplayId() {
        return this.mPhysicalDisplayId;
    }

    public void setPhysicalDisplayId(String str) {
        this.mPhysicalDisplayId = str;
    }

    public float getLux() {
        return this.mLux;
    }

    public void setLux(float f) {
        this.mLux = f;
    }

    public float getPreThresholdLux() {
        return this.mPreThresholdLux;
    }

    public void setPreThresholdLux(float f) {
        this.mPreThresholdLux = f;
    }

    public float getInitialBrightness() {
        return this.mInitialBrightness;
    }

    public void setInitialBrightness(float f) {
        this.mInitialBrightness = f;
    }

    public float getBrightness() {
        return this.mBrightness;
    }

    public void setBrightness(float f) {
        this.mBrightness = f;
    }

    public float getRecommendedBrightness() {
        return this.mRecommendedBrightness;
    }

    public void setRecommendedBrightness(float f) {
        this.mRecommendedBrightness = f;
    }

    public float getPreThresholdBrightness() {
        return this.mPreThresholdBrightness;
    }

    public void setPreThresholdBrightness(float f) {
        this.mPreThresholdBrightness = f;
    }

    public int getHbmMode() {
        return this.mHbmMode;
    }

    public void setHbmMode(int i) {
        this.mHbmMode = i;
    }

    public float getHbmMax() {
        return this.mHbmMax;
    }

    public void setHbmMax(float f) {
        this.mHbmMax = f;
    }

    public int getRbcStrength() {
        return this.mRbcStrength;
    }

    public void setRbcStrength(int i) {
        this.mRbcStrength = i;
    }

    public boolean isRbcEnabled() {
        return (this.mFlags & 1) != 0;
    }

    public float getThermalMax() {
        return this.mThermalMax;
    }

    public void setThermalMax(float f) {
        this.mThermalMax = f;
    }

    public float getPowerFactor() {
        return this.mPowerFactor;
    }

    public void setPowerFactor(float f) {
        this.mPowerFactor = f;
    }

    public boolean isLowPowerModeSet() {
        return (this.mFlags & 32) != 0;
    }

    public boolean setWasShortTermModelActive(boolean z) {
        this.mWasShortTermModelActive = z;
        return z;
    }

    public boolean wasShortTermModelActive() {
        return this.mWasShortTermModelActive;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public void setFlags(int i) {
        this.mFlags = i;
    }

    public int getAdjustmentFlags() {
        return this.mAdjustmentFlags;
    }

    public void setAdjustmentFlags(int i) {
        this.mAdjustmentFlags = i;
    }

    public boolean isAutomaticBrightnessEnabled() {
        return this.mAutomaticBrightnessEnabled;
    }

    public void setDisplayBrightnessStrategyName(String str) {
        this.mDisplayBrightnessStrategyName = str;
    }

    public String getDisplayBrightnessStrategyName() {
        return this.mDisplayBrightnessStrategyName;
    }

    public void setAutomaticBrightnessEnabled(boolean z) {
        this.mAutomaticBrightnessEnabled = z;
    }

    @VisibleForTesting
    public String flagsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append((this.mFlags & 8) != 0 ? "user_set " : "");
        sb.append((this.mFlags & 1) != 0 ? "rbc " : "");
        sb.append((this.mFlags & 2) != 0 ? "invalid_lux " : "");
        sb.append((this.mFlags & 4) != 0 ? "doze_scale " : "");
        sb.append((this.mFlags & 16) != 0 ? "idle_curve " : "");
        sb.append((this.mFlags & 32) != 0 ? "low_power_mode " : "");
        return sb.toString();
    }
}
