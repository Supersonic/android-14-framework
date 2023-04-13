package com.android.server.display.whitebalance;

import android.util.Slog;
import android.util.Spline;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.display.color.ColorDisplayService;
import com.android.server.display.utils.AmbientFilter;
import com.android.server.display.utils.History;
import com.android.server.display.whitebalance.AmbientSensor;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes.dex */
public class DisplayWhiteBalanceController implements AmbientSensor.AmbientBrightnessSensor.Callbacks, AmbientSensor.AmbientColorTemperatureSensor.Callbacks {
    public float mAmbientColorTemperature;
    public final History mAmbientColorTemperatureHistory;
    public float mAmbientColorTemperatureOverride;
    public Spline.LinearSpline mAmbientToDisplayColorTemperatureSpline;
    @VisibleForTesting
    AmbientFilter mBrightnessFilter;
    public final AmbientSensor.AmbientBrightnessSensor mBrightnessSensor;
    public final ColorDisplayService.ColorDisplayServiceInternal mColorDisplayServiceInternal;
    @VisibleForTesting
    AmbientFilter mColorTemperatureFilter;
    public final AmbientSensor.AmbientColorTemperatureSensor mColorTemperatureSensor;
    public Callbacks mDisplayPowerControllerCallbacks;
    public boolean mEnabled;
    public Spline.LinearSpline mHighLightAmbientBrightnessToBiasSpline;
    public final float mHighLightAmbientColorTemperature;
    public float mLastAmbientColorTemperature;
    public float mLatestAmbientBrightness;
    public float mLatestAmbientColorTemperature;
    public float mLatestHighLightBias;
    public float mLatestLowLightBias;
    public final boolean mLightModeAllowed;
    public boolean mLoggingEnabled;
    public Spline.LinearSpline mLowLightAmbientBrightnessToBiasSpline;
    public final float mLowLightAmbientColorTemperature;
    @VisibleForTesting
    float mPendingAmbientColorTemperature;
    public Spline.LinearSpline mStrongAmbientToDisplayColorTemperatureSpline;
    public boolean mStrongModeEnabled;
    public final DisplayWhiteBalanceThrottler mThrottler;

    /* loaded from: classes.dex */
    public interface Callbacks {
        void updateWhiteBalance();
    }

    public DisplayWhiteBalanceController(AmbientSensor.AmbientBrightnessSensor ambientBrightnessSensor, AmbientFilter ambientFilter, AmbientSensor.AmbientColorTemperatureSensor ambientColorTemperatureSensor, AmbientFilter ambientFilter2, DisplayWhiteBalanceThrottler displayWhiteBalanceThrottler, float[] fArr, float[] fArr2, float f, float[] fArr3, float[] fArr4, float f2, float[] fArr5, float[] fArr6, float[] fArr7, float[] fArr8, boolean z) {
        validateArguments(ambientBrightnessSensor, ambientFilter, ambientColorTemperatureSensor, ambientFilter2, displayWhiteBalanceThrottler);
        this.mBrightnessSensor = ambientBrightnessSensor;
        this.mBrightnessFilter = ambientFilter;
        this.mColorTemperatureSensor = ambientColorTemperatureSensor;
        this.mColorTemperatureFilter = ambientFilter2;
        this.mThrottler = displayWhiteBalanceThrottler;
        this.mLowLightAmbientColorTemperature = f;
        this.mHighLightAmbientColorTemperature = f2;
        this.mAmbientColorTemperature = -1.0f;
        this.mPendingAmbientColorTemperature = -1.0f;
        this.mLastAmbientColorTemperature = -1.0f;
        this.mAmbientColorTemperatureHistory = new History(50);
        this.mAmbientColorTemperatureOverride = -1.0f;
        this.mLightModeAllowed = z;
        try {
            this.mLowLightAmbientBrightnessToBiasSpline = new Spline.LinearSpline(fArr, fArr2);
        } catch (Exception e) {
            Slog.e("DisplayWhiteBalanceController", "failed to create low light ambient brightness to bias spline.", e);
            this.mLowLightAmbientBrightnessToBiasSpline = null;
        }
        Spline.LinearSpline linearSpline = this.mLowLightAmbientBrightnessToBiasSpline;
        if (linearSpline != null && (linearSpline.interpolate(0.0f) != 0.0f || this.mLowLightAmbientBrightnessToBiasSpline.interpolate(Float.POSITIVE_INFINITY) != 1.0f)) {
            Slog.d("DisplayWhiteBalanceController", "invalid low light ambient brightness to bias spline, bias must begin at 0.0 and end at 1.0.");
            this.mLowLightAmbientBrightnessToBiasSpline = null;
        }
        try {
            this.mHighLightAmbientBrightnessToBiasSpline = new Spline.LinearSpline(fArr3, fArr4);
        } catch (Exception e2) {
            Slog.e("DisplayWhiteBalanceController", "failed to create high light ambient brightness to bias spline.", e2);
            this.mHighLightAmbientBrightnessToBiasSpline = null;
        }
        Spline.LinearSpline linearSpline2 = this.mHighLightAmbientBrightnessToBiasSpline;
        if (linearSpline2 != null && (linearSpline2.interpolate(0.0f) != 0.0f || this.mHighLightAmbientBrightnessToBiasSpline.interpolate(Float.POSITIVE_INFINITY) != 1.0f)) {
            Slog.d("DisplayWhiteBalanceController", "invalid high light ambient brightness to bias spline, bias must begin at 0.0 and end at 1.0.");
            this.mHighLightAmbientBrightnessToBiasSpline = null;
        }
        if (this.mLowLightAmbientBrightnessToBiasSpline != null && this.mHighLightAmbientBrightnessToBiasSpline != null && fArr[fArr.length - 1] > fArr3[0]) {
            Slog.d("DisplayWhiteBalanceController", "invalid low light and high light ambient brightness to bias spline combination, defined domains must not intersect.");
            this.mLowLightAmbientBrightnessToBiasSpline = null;
            this.mHighLightAmbientBrightnessToBiasSpline = null;
        }
        try {
            this.mAmbientToDisplayColorTemperatureSpline = new Spline.LinearSpline(fArr5, fArr6);
        } catch (Exception e3) {
            Slog.e("DisplayWhiteBalanceController", "failed to create ambient to display color temperature spline.", e3);
            this.mAmbientToDisplayColorTemperatureSpline = null;
        }
        try {
            this.mStrongAmbientToDisplayColorTemperatureSpline = new Spline.LinearSpline(fArr7, fArr8);
        } catch (Exception e4) {
            Slog.e("DisplayWhiteBalanceController", "Failed to create strong ambient to display color temperature spline", e4);
        }
        this.mColorDisplayServiceInternal = (ColorDisplayService.ColorDisplayServiceInternal) LocalServices.getService(ColorDisplayService.ColorDisplayServiceInternal.class);
    }

    public boolean setEnabled(boolean z) {
        if (z) {
            return enable();
        }
        return disable();
    }

    public void setStrongModeEnabled(boolean z) {
        this.mStrongModeEnabled = z;
        this.mColorDisplayServiceInternal.setDisplayWhiteBalanceAllowed(this.mLightModeAllowed || z);
        if (this.mEnabled) {
            updateAmbientColorTemperature();
            updateDisplayColorTemperature();
        }
    }

    public boolean setCallbacks(Callbacks callbacks) {
        if (this.mDisplayPowerControllerCallbacks == callbacks) {
            return false;
        }
        this.mDisplayPowerControllerCallbacks = callbacks;
        return true;
    }

    public boolean setLoggingEnabled(boolean z) {
        if (this.mLoggingEnabled == z) {
            return false;
        }
        this.mLoggingEnabled = z;
        this.mBrightnessSensor.setLoggingEnabled(z);
        this.mBrightnessFilter.setLoggingEnabled(z);
        this.mColorTemperatureSensor.setLoggingEnabled(z);
        this.mColorTemperatureFilter.setLoggingEnabled(z);
        this.mThrottler.setLoggingEnabled(z);
        return true;
    }

    public boolean setAmbientColorTemperatureOverride(float f) {
        if (this.mAmbientColorTemperatureOverride == f) {
            return false;
        }
        this.mAmbientColorTemperatureOverride = f;
        return true;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("DisplayWhiteBalanceController");
        printWriter.println("  mLoggingEnabled=" + this.mLoggingEnabled);
        printWriter.println("  mEnabled=" + this.mEnabled);
        printWriter.println("  mStrongModeEnabled=" + this.mStrongModeEnabled);
        printWriter.println("  mDisplayPowerControllerCallbacks=" + this.mDisplayPowerControllerCallbacks);
        this.mBrightnessSensor.dump(printWriter);
        this.mBrightnessFilter.dump(printWriter);
        this.mColorTemperatureSensor.dump(printWriter);
        this.mColorTemperatureFilter.dump(printWriter);
        this.mThrottler.dump(printWriter);
        printWriter.println("  mLowLightAmbientColorTemperature=" + this.mLowLightAmbientColorTemperature);
        printWriter.println("  mHighLightAmbientColorTemperature=" + this.mHighLightAmbientColorTemperature);
        printWriter.println("  mAmbientColorTemperature=" + this.mAmbientColorTemperature);
        printWriter.println("  mPendingAmbientColorTemperature=" + this.mPendingAmbientColorTemperature);
        printWriter.println("  mLastAmbientColorTemperature=" + this.mLastAmbientColorTemperature);
        printWriter.println("  mAmbientColorTemperatureHistory=" + this.mAmbientColorTemperatureHistory);
        printWriter.println("  mAmbientColorTemperatureOverride=" + this.mAmbientColorTemperatureOverride);
        printWriter.println("  mAmbientToDisplayColorTemperatureSpline=" + this.mAmbientToDisplayColorTemperatureSpline);
        printWriter.println("  mStrongAmbientToDisplayColorTemperatureSpline=" + this.mStrongAmbientToDisplayColorTemperatureSpline);
        printWriter.println("  mLowLightAmbientBrightnessToBiasSpline=" + this.mLowLightAmbientBrightnessToBiasSpline);
        printWriter.println("  mHighLightAmbientBrightnessToBiasSpline=" + this.mHighLightAmbientBrightnessToBiasSpline);
    }

    @Override // com.android.server.display.whitebalance.AmbientSensor.AmbientBrightnessSensor.Callbacks
    public void onAmbientBrightnessChanged(float f) {
        this.mBrightnessFilter.addValue(System.currentTimeMillis(), f);
        updateAmbientColorTemperature();
    }

    @Override // com.android.server.display.whitebalance.AmbientSensor.AmbientColorTemperatureSensor.Callbacks
    public void onAmbientColorTemperatureChanged(float f) {
        this.mColorTemperatureFilter.addValue(System.currentTimeMillis(), f);
        updateAmbientColorTemperature();
    }

    public void updateAmbientColorTemperature() {
        Spline.LinearSpline linearSpline;
        Spline.LinearSpline linearSpline2;
        long currentTimeMillis = System.currentTimeMillis();
        float estimate = this.mColorTemperatureFilter.getEstimate(currentTimeMillis);
        this.mLatestAmbientColorTemperature = estimate;
        if (this.mStrongModeEnabled) {
            Spline.LinearSpline linearSpline3 = this.mStrongAmbientToDisplayColorTemperatureSpline;
            if (linearSpline3 != null && estimate != -1.0f) {
                estimate = linearSpline3.interpolate(estimate);
            }
        } else {
            Spline.LinearSpline linearSpline4 = this.mAmbientToDisplayColorTemperatureSpline;
            if (linearSpline4 != null && estimate != -1.0f) {
                estimate = linearSpline4.interpolate(estimate);
            }
        }
        float estimate2 = this.mBrightnessFilter.getEstimate(currentTimeMillis);
        this.mLatestAmbientBrightness = estimate2;
        if (estimate != -1.0f && estimate2 != -1.0f && (linearSpline2 = this.mLowLightAmbientBrightnessToBiasSpline) != null) {
            float interpolate = linearSpline2.interpolate(estimate2);
            estimate = (estimate * interpolate) + ((1.0f - interpolate) * this.mLowLightAmbientColorTemperature);
            this.mLatestLowLightBias = interpolate;
        }
        if (estimate != -1.0f && estimate2 != -1.0f && (linearSpline = this.mHighLightAmbientBrightnessToBiasSpline) != null) {
            float interpolate2 = linearSpline.interpolate(estimate2);
            estimate = ((1.0f - interpolate2) * estimate) + (this.mHighLightAmbientColorTemperature * interpolate2);
            this.mLatestHighLightBias = interpolate2;
        }
        if (this.mAmbientColorTemperatureOverride != -1.0f) {
            if (this.mLoggingEnabled) {
                Slog.d("DisplayWhiteBalanceController", "override ambient color temperature: " + estimate + " => " + this.mAmbientColorTemperatureOverride);
            }
            estimate = this.mAmbientColorTemperatureOverride;
        }
        if (estimate == -1.0f || this.mThrottler.throttle(estimate)) {
            return;
        }
        if (this.mLoggingEnabled) {
            Slog.d("DisplayWhiteBalanceController", "pending ambient color temperature: " + estimate);
        }
        this.mPendingAmbientColorTemperature = estimate;
        Callbacks callbacks = this.mDisplayPowerControllerCallbacks;
        if (callbacks != null) {
            callbacks.updateWhiteBalance();
        }
    }

    public void updateDisplayColorTemperature() {
        float f = this.mAmbientColorTemperature;
        float f2 = (f == -1.0f && this.mPendingAmbientColorTemperature == -1.0f) ? this.mLastAmbientColorTemperature : -1.0f;
        float f3 = this.mPendingAmbientColorTemperature;
        if (f3 != -1.0f && f3 != f) {
            f2 = f3;
        }
        if (f2 == -1.0f) {
            return;
        }
        this.mAmbientColorTemperature = f2;
        if (this.mLoggingEnabled) {
            Slog.d("DisplayWhiteBalanceController", "ambient color temperature: " + this.mAmbientColorTemperature);
        }
        this.mPendingAmbientColorTemperature = -1.0f;
        this.mAmbientColorTemperatureHistory.add(this.mAmbientColorTemperature);
        Slog.d("DisplayWhiteBalanceController", "Display cct: " + this.mAmbientColorTemperature + " Latest ambient cct: " + this.mLatestAmbientColorTemperature + " Latest ambient lux: " + this.mLatestAmbientBrightness + " Latest low light bias: " + this.mLatestLowLightBias + " Latest high light bias: " + this.mLatestHighLightBias);
        this.mColorDisplayServiceInternal.setDisplayWhiteBalanceColorTemperature((int) this.mAmbientColorTemperature);
        this.mLastAmbientColorTemperature = this.mAmbientColorTemperature;
    }

    public float calculateAdjustedBrightnessNits(float f) {
        float displayWhiteBalanceLuminance = this.mColorDisplayServiceInternal.getDisplayWhiteBalanceLuminance();
        return displayWhiteBalanceLuminance == -1.0f ? f : (f - (displayWhiteBalanceLuminance * f)) + f;
    }

    public final void validateArguments(AmbientSensor.AmbientBrightnessSensor ambientBrightnessSensor, AmbientFilter ambientFilter, AmbientSensor.AmbientColorTemperatureSensor ambientColorTemperatureSensor, AmbientFilter ambientFilter2, DisplayWhiteBalanceThrottler displayWhiteBalanceThrottler) {
        Objects.requireNonNull(ambientBrightnessSensor, "brightnessSensor must not be null");
        Objects.requireNonNull(ambientFilter, "brightnessFilter must not be null");
        Objects.requireNonNull(ambientColorTemperatureSensor, "colorTemperatureSensor must not be null");
        Objects.requireNonNull(ambientFilter2, "colorTemperatureFilter must not be null");
        Objects.requireNonNull(displayWhiteBalanceThrottler, "throttler cannot be null");
    }

    public final boolean enable() {
        if (this.mEnabled) {
            return false;
        }
        if (this.mLoggingEnabled) {
            Slog.d("DisplayWhiteBalanceController", "enabling");
        }
        this.mEnabled = true;
        this.mBrightnessSensor.setEnabled(true);
        this.mColorTemperatureSensor.setEnabled(true);
        return true;
    }

    public final boolean disable() {
        if (this.mEnabled) {
            if (this.mLoggingEnabled) {
                Slog.d("DisplayWhiteBalanceController", "disabling");
            }
            this.mEnabled = false;
            this.mBrightnessSensor.setEnabled(false);
            this.mBrightnessFilter.clear();
            this.mColorTemperatureSensor.setEnabled(false);
            this.mColorTemperatureFilter.clear();
            this.mThrottler.clear();
            this.mAmbientColorTemperature = -1.0f;
            this.mPendingAmbientColorTemperature = -1.0f;
            this.mColorDisplayServiceInternal.resetDisplayWhiteBalanceColorTemperature();
            return true;
        }
        return false;
    }
}
