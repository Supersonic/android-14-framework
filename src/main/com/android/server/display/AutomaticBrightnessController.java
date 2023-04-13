package com.android.server.display;

import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.DisplayManagerInternal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.util.EventLog;
import android.util.MathUtils;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.internal.os.BackgroundThread;
import com.android.server.display.brightness.BrightnessEvent;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class AutomaticBrightnessController {
    public IActivityTaskManager mActivityTaskManager;
    public float mAmbientBrighteningThreshold;
    public final HysteresisLevels mAmbientBrightnessThresholds;
    public final HysteresisLevels mAmbientBrightnessThresholdsIdle;
    public float mAmbientDarkeningThreshold;
    public final int mAmbientLightHorizonLong;
    public final int mAmbientLightHorizonShort;
    public AmbientLightRingBuffer mAmbientLightRingBuffer;
    public float mAmbientLux;
    public boolean mAmbientLuxValid;
    public final long mBrighteningLightDebounceConfig;
    public float mBrightnessAdjustmentSampleOldBrightness;
    public float mBrightnessAdjustmentSampleOldLux;
    public boolean mBrightnessAdjustmentSamplePending;
    public BrightnessThrottler mBrightnessThrottler;
    public final Callbacks mCallbacks;
    public Clock mClock;
    public Context mContext;
    public BrightnessMappingStrategy mCurrentBrightnessMapper;
    public int mCurrentLightSensorRate;
    public final long mDarkeningLightDebounceConfig;
    public int mDisplayPolicy;
    public final float mDozeScaleFactor;
    public float mFastAmbientLux;
    public int mForegroundAppCategory;
    public String mForegroundAppPackageName;
    public AutomaticBrightnessHandler mHandler;
    public HighBrightnessModeController mHbmController;
    public final BrightnessMappingStrategy mIdleModeBrightnessMapper;
    public final int mInitialLightSensorRate;
    public final Injector mInjector;
    public final BrightnessMappingStrategy mInteractiveModeBrightnessMapper;
    public boolean mIsBrightnessThrottled;
    public float mLastObservedLux;
    public long mLastObservedLuxTime;
    public final Sensor mLightSensor;
    public long mLightSensorEnableTime;
    public boolean mLightSensorEnabled;
    public final SensorEventListener mLightSensorListener;
    public int mLightSensorWarmUpTimeConfig;
    public boolean mLoggingEnabled;
    public final int mNormalLightSensorRate;
    public PackageManager mPackageManager;
    public int mPendingForegroundAppCategory;
    public String mPendingForegroundAppPackageName;
    public float mPreThresholdBrightness;
    public float mPreThresholdLux;
    public float mRawScreenAutoBrightness;
    public int mRecentLightSamples;
    public final boolean mResetAmbientLuxAfterWarmUpConfig;
    public float mScreenAutoBrightness;
    public float mScreenBrighteningThreshold;
    public final float mScreenBrightnessRangeMaximum;
    public final float mScreenBrightnessRangeMinimum;
    public final HysteresisLevels mScreenBrightnessThresholds;
    public final HysteresisLevels mScreenBrightnessThresholdsIdle;
    public float mScreenDarkeningThreshold;
    public final SensorManager mSensorManager;
    public float mShortTermModelAnchor;
    public boolean mShortTermModelValid;
    public float mSlowAmbientLux;
    public int mState;
    public TaskStackListenerImpl mTaskStackListener;
    public final int mWeightingIntercept;

    /* loaded from: classes.dex */
    public interface Callbacks {
        void updateBrightness();
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface Clock {
        long uptimeMillis();
    }

    public static boolean isInteractivePolicy(int i) {
        return i == 3 || i == 2;
    }

    public AutomaticBrightnessController(Callbacks callbacks, Looper looper, SensorManager sensorManager, Sensor sensor, BrightnessMappingStrategy brightnessMappingStrategy, int i, float f, float f2, float f3, int i2, int i3, long j, long j2, boolean z, HysteresisLevels hysteresisLevels, HysteresisLevels hysteresisLevels2, HysteresisLevels hysteresisLevels3, HysteresisLevels hysteresisLevels4, Context context, HighBrightnessModeController highBrightnessModeController, BrightnessThrottler brightnessThrottler, BrightnessMappingStrategy brightnessMappingStrategy2, int i4, int i5, float f4, float f5) {
        this(new Injector(), callbacks, looper, sensorManager, sensor, brightnessMappingStrategy, i, f, f2, f3, i2, i3, j, j2, z, hysteresisLevels, hysteresisLevels2, hysteresisLevels3, hysteresisLevels4, context, highBrightnessModeController, brightnessThrottler, brightnessMappingStrategy2, i4, i5, f4, f5);
    }

    @VisibleForTesting
    public AutomaticBrightnessController(Injector injector, Callbacks callbacks, Looper looper, SensorManager sensorManager, Sensor sensor, BrightnessMappingStrategy brightnessMappingStrategy, int i, float f, float f2, float f3, int i2, int i3, long j, long j2, boolean z, HysteresisLevels hysteresisLevels, HysteresisLevels hysteresisLevels2, HysteresisLevels hysteresisLevels3, HysteresisLevels hysteresisLevels4, Context context, HighBrightnessModeController highBrightnessModeController, BrightnessThrottler brightnessThrottler, BrightnessMappingStrategy brightnessMappingStrategy2, int i4, int i5, float f4, float f5) {
        this.mScreenAutoBrightness = Float.NaN;
        this.mRawScreenAutoBrightness = Float.NaN;
        this.mDisplayPolicy = 0;
        this.mState = 2;
        this.mLightSensorListener = new SensorEventListener() { // from class: com.android.server.display.AutomaticBrightnessController.2
            @Override // android.hardware.SensorEventListener
            public void onAccuracyChanged(Sensor sensor2, int i6) {
            }

            @Override // android.hardware.SensorEventListener
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (AutomaticBrightnessController.this.mLightSensorEnabled) {
                    AutomaticBrightnessController.this.handleLightSensorEvent(AutomaticBrightnessController.this.mClock.uptimeMillis(), sensorEvent.values[0]);
                }
            }
        };
        this.mInjector = injector;
        this.mClock = injector.createClock();
        this.mContext = context;
        this.mCallbacks = callbacks;
        this.mSensorManager = sensorManager;
        this.mCurrentBrightnessMapper = brightnessMappingStrategy;
        this.mScreenBrightnessRangeMinimum = f;
        this.mScreenBrightnessRangeMaximum = f2;
        this.mLightSensorWarmUpTimeConfig = i;
        this.mDozeScaleFactor = f3;
        this.mNormalLightSensorRate = i2;
        this.mInitialLightSensorRate = i3;
        this.mCurrentLightSensorRate = -1;
        this.mBrighteningLightDebounceConfig = j;
        this.mDarkeningLightDebounceConfig = j2;
        this.mResetAmbientLuxAfterWarmUpConfig = z;
        this.mAmbientLightHorizonLong = i5;
        this.mAmbientLightHorizonShort = i4;
        this.mWeightingIntercept = i5;
        this.mAmbientBrightnessThresholds = hysteresisLevels;
        this.mAmbientBrightnessThresholdsIdle = hysteresisLevels3;
        this.mScreenBrightnessThresholds = hysteresisLevels2;
        this.mScreenBrightnessThresholdsIdle = hysteresisLevels4;
        this.mShortTermModelValid = true;
        this.mShortTermModelAnchor = -1.0f;
        this.mHandler = new AutomaticBrightnessHandler(looper);
        this.mAmbientLightRingBuffer = new AmbientLightRingBuffer(i2, i5, this.mClock);
        this.mLightSensor = sensor;
        this.mActivityTaskManager = ActivityTaskManager.getService();
        this.mPackageManager = this.mContext.getPackageManager();
        this.mTaskStackListener = new TaskStackListenerImpl();
        this.mForegroundAppPackageName = null;
        this.mPendingForegroundAppPackageName = null;
        this.mForegroundAppCategory = -1;
        this.mPendingForegroundAppCategory = -1;
        this.mHbmController = highBrightnessModeController;
        this.mBrightnessThrottler = brightnessThrottler;
        this.mInteractiveModeBrightnessMapper = brightnessMappingStrategy;
        this.mIdleModeBrightnessMapper = brightnessMappingStrategy2;
        switchToInteractiveScreenBrightnessMode();
        if (f4 == -1.0f || f5 == -1.0f) {
            return;
        }
        setScreenBrightnessByUser(f4, f5);
    }

    public boolean setLoggingEnabled(boolean z) {
        if (this.mLoggingEnabled == z) {
            return false;
        }
        BrightnessMappingStrategy brightnessMappingStrategy = this.mInteractiveModeBrightnessMapper;
        if (brightnessMappingStrategy != null) {
            brightnessMappingStrategy.setLoggingEnabled(z);
        }
        BrightnessMappingStrategy brightnessMappingStrategy2 = this.mIdleModeBrightnessMapper;
        if (brightnessMappingStrategy2 != null) {
            brightnessMappingStrategy2.setLoggingEnabled(z);
        }
        this.mLoggingEnabled = z;
        return true;
    }

    public float getAutomaticScreenBrightness() {
        return getAutomaticScreenBrightness(null);
    }

    public float getAutomaticScreenBrightness(BrightnessEvent brightnessEvent) {
        if (brightnessEvent != null) {
            brightnessEvent.setLux(this.mAmbientLuxValid ? this.mAmbientLux : Float.NaN);
            brightnessEvent.setPreThresholdLux(this.mPreThresholdLux);
            brightnessEvent.setPreThresholdBrightness(this.mPreThresholdBrightness);
            brightnessEvent.setRecommendedBrightness(this.mScreenAutoBrightness);
            brightnessEvent.setFlags(brightnessEvent.getFlags() | (!this.mAmbientLuxValid ? 2 : 0) | (this.mDisplayPolicy == 1 ? 4 : 0) | (this.mCurrentBrightnessMapper.isForIdleMode() ? 16 : 0));
        }
        if (this.mAmbientLuxValid) {
            if (this.mDisplayPolicy == 1) {
                return this.mScreenAutoBrightness * this.mDozeScaleFactor;
            }
            return this.mScreenAutoBrightness;
        }
        return Float.NaN;
    }

    public float getRawAutomaticScreenBrightness() {
        return this.mRawScreenAutoBrightness;
    }

    public boolean hasValidAmbientLux() {
        return this.mAmbientLuxValid;
    }

    public float getAutomaticScreenBrightnessAdjustment() {
        return this.mCurrentBrightnessMapper.getAutoBrightnessAdjustment();
    }

    public void configure(int i, BrightnessConfiguration brightnessConfiguration, float f, boolean z, float f2, boolean z2, int i2, boolean z3) {
        this.mState = i;
        boolean z4 = true;
        boolean z5 = i2 == 1;
        boolean brightnessConfiguration2 = setBrightnessConfiguration(brightnessConfiguration, z3) | setDisplayPolicy(i2);
        if (z2) {
            brightnessConfiguration2 |= setAutoBrightnessAdjustment(f2);
        }
        boolean z6 = this.mState == 1;
        if (z && z6) {
            brightnessConfiguration2 |= setScreenBrightnessByUser(f);
        }
        boolean z7 = z || z2;
        if (z7 && z6 && !z5) {
            prepareBrightnessAdjustmentSample();
        }
        boolean lightSensorEnabled = brightnessConfiguration2 | setLightSensorEnabled(z6 && !z5);
        if (this.mIsBrightnessThrottled != this.mBrightnessThrottler.isThrottled()) {
            this.mIsBrightnessThrottled = this.mBrightnessThrottler.isThrottled();
        } else {
            z4 = lightSensorEnabled;
        }
        if (z4) {
            updateAutoBrightness(false, z7);
        }
    }

    public void stop() {
        setLightSensorEnabled(false);
    }

    public boolean hasUserDataPoints() {
        return this.mCurrentBrightnessMapper.hasUserDataPoints();
    }

    public boolean isDefaultConfig() {
        if (isInIdleMode()) {
            return false;
        }
        return this.mInteractiveModeBrightnessMapper.isDefaultConfig();
    }

    public BrightnessConfiguration getDefaultConfig() {
        return this.mInteractiveModeBrightnessMapper.getDefaultConfig();
    }

    public void update() {
        this.mHandler.sendEmptyMessage(6);
    }

    public float getAmbientLux() {
        return this.mAmbientLux;
    }

    public final boolean setDisplayPolicy(int i) {
        int i2 = this.mDisplayPolicy;
        if (i2 == i) {
            return false;
        }
        this.mDisplayPolicy = i;
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "Display policy transitioning from " + i2 + " to " + i);
        }
        if (!isInteractivePolicy(i) && isInteractivePolicy(i2) && !isInIdleMode()) {
            this.mHandler.sendEmptyMessageDelayed(3, this.mCurrentBrightnessMapper.getShortTermModelTimeout());
            return true;
        } else if (!isInteractivePolicy(i) || isInteractivePolicy(i2)) {
            return true;
        } else {
            this.mHandler.removeMessages(3);
            return true;
        }
    }

    public final boolean setScreenBrightnessByUser(float f) {
        if (this.mAmbientLuxValid) {
            return setScreenBrightnessByUser(this.mAmbientLux, f);
        }
        return false;
    }

    public final boolean setScreenBrightnessByUser(float f, float f2) {
        this.mCurrentBrightnessMapper.addUserDataPoint(f, f2);
        this.mShortTermModelValid = true;
        this.mShortTermModelAnchor = f;
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "ShortTermModel: anchor=" + this.mShortTermModelAnchor);
        }
        return true;
    }

    public void resetShortTermModel() {
        this.mCurrentBrightnessMapper.clearUserDataPoints();
        this.mShortTermModelValid = true;
        this.mShortTermModelAnchor = -1.0f;
    }

    public final void invalidateShortTermModel() {
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "ShortTermModel: invalidate user data");
        }
        this.mShortTermModelValid = false;
    }

    public boolean setBrightnessConfiguration(BrightnessConfiguration brightnessConfiguration, boolean z) {
        if (this.mInteractiveModeBrightnessMapper.setBrightnessConfiguration(brightnessConfiguration)) {
            if (isInIdleMode() || !z) {
                return true;
            }
            resetShortTermModel();
            return true;
        }
        return false;
    }

    public boolean isInIdleMode() {
        return this.mCurrentBrightnessMapper.isForIdleMode();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("Automatic Brightness Controller Configuration:");
        printWriter.println("  mState=" + configStateToString(this.mState));
        printWriter.println("  mScreenBrightnessRangeMinimum=" + this.mScreenBrightnessRangeMinimum);
        printWriter.println("  mScreenBrightnessRangeMaximum=" + this.mScreenBrightnessRangeMaximum);
        printWriter.println("  mDozeScaleFactor=" + this.mDozeScaleFactor);
        printWriter.println("  mInitialLightSensorRate=" + this.mInitialLightSensorRate);
        printWriter.println("  mNormalLightSensorRate=" + this.mNormalLightSensorRate);
        printWriter.println("  mLightSensorWarmUpTimeConfig=" + this.mLightSensorWarmUpTimeConfig);
        printWriter.println("  mBrighteningLightDebounceConfig=" + this.mBrighteningLightDebounceConfig);
        printWriter.println("  mDarkeningLightDebounceConfig=" + this.mDarkeningLightDebounceConfig);
        printWriter.println("  mResetAmbientLuxAfterWarmUpConfig=" + this.mResetAmbientLuxAfterWarmUpConfig);
        printWriter.println("  mAmbientLightHorizonLong=" + this.mAmbientLightHorizonLong);
        printWriter.println("  mAmbientLightHorizonShort=" + this.mAmbientLightHorizonShort);
        printWriter.println("  mWeightingIntercept=" + this.mWeightingIntercept);
        printWriter.println();
        printWriter.println("Automatic Brightness Controller State:");
        printWriter.println("  mLightSensor=" + this.mLightSensor);
        printWriter.println("  mLightSensorEnabled=" + this.mLightSensorEnabled);
        printWriter.println("  mLightSensorEnableTime=" + TimeUtils.formatUptime(this.mLightSensorEnableTime));
        printWriter.println("  mCurrentLightSensorRate=" + this.mCurrentLightSensorRate);
        printWriter.println("  mAmbientLux=" + this.mAmbientLux);
        printWriter.println("  mAmbientLuxValid=" + this.mAmbientLuxValid);
        printWriter.println("  mPreThesholdLux=" + this.mPreThresholdLux);
        printWriter.println("  mPreThesholdBrightness=" + this.mPreThresholdBrightness);
        printWriter.println("  mAmbientBrighteningThreshold=" + this.mAmbientBrighteningThreshold);
        printWriter.println("  mAmbientDarkeningThreshold=" + this.mAmbientDarkeningThreshold);
        printWriter.println("  mScreenBrighteningThreshold=" + this.mScreenBrighteningThreshold);
        printWriter.println("  mScreenDarkeningThreshold=" + this.mScreenDarkeningThreshold);
        printWriter.println("  mLastObservedLux=" + this.mLastObservedLux);
        printWriter.println("  mLastObservedLuxTime=" + TimeUtils.formatUptime(this.mLastObservedLuxTime));
        printWriter.println("  mRecentLightSamples=" + this.mRecentLightSamples);
        printWriter.println("  mAmbientLightRingBuffer=" + this.mAmbientLightRingBuffer);
        printWriter.println("  mScreenAutoBrightness=" + this.mScreenAutoBrightness);
        printWriter.println("  mDisplayPolicy=" + DisplayManagerInternal.DisplayPowerRequest.policyToString(this.mDisplayPolicy));
        printWriter.println("  mShortTermModelTimeout(active)=" + this.mInteractiveModeBrightnessMapper.getShortTermModelTimeout());
        if (this.mIdleModeBrightnessMapper != null) {
            printWriter.println("  mShortTermModelTimeout(idle)=" + this.mIdleModeBrightnessMapper.getShortTermModelTimeout());
        }
        printWriter.println("  mShortTermModelAnchor=" + this.mShortTermModelAnchor);
        printWriter.println("  mShortTermModelValid=" + this.mShortTermModelValid);
        printWriter.println("  mBrightnessAdjustmentSamplePending=" + this.mBrightnessAdjustmentSamplePending);
        printWriter.println("  mBrightnessAdjustmentSampleOldLux=" + this.mBrightnessAdjustmentSampleOldLux);
        printWriter.println("  mBrightnessAdjustmentSampleOldBrightness=" + this.mBrightnessAdjustmentSampleOldBrightness);
        printWriter.println("  mForegroundAppPackageName=" + this.mForegroundAppPackageName);
        printWriter.println("  mPendingForegroundAppPackageName=" + this.mPendingForegroundAppPackageName);
        printWriter.println("  mForegroundAppCategory=" + this.mForegroundAppCategory);
        printWriter.println("  mPendingForegroundAppCategory=" + this.mPendingForegroundAppCategory);
        printWriter.println("  Idle mode active=" + this.mCurrentBrightnessMapper.isForIdleMode());
        printWriter.println();
        printWriter.println("  mInteractiveMapper=");
        this.mInteractiveModeBrightnessMapper.dump(printWriter, this.mHbmController.getNormalBrightnessMax());
        if (this.mIdleModeBrightnessMapper != null) {
            printWriter.println("  mIdleMapper=");
            this.mIdleModeBrightnessMapper.dump(printWriter, this.mHbmController.getNormalBrightnessMax());
        }
        printWriter.println();
        printWriter.println("  mAmbientBrightnessThresholds=");
        this.mAmbientBrightnessThresholds.dump(printWriter);
        printWriter.println("  mScreenBrightnessThresholds=");
        this.mScreenBrightnessThresholds.dump(printWriter);
        printWriter.println("  mScreenBrightnessThresholdsIdle=");
        this.mScreenBrightnessThresholdsIdle.dump(printWriter);
        printWriter.println("  mAmbientBrightnessThresholdsIdle=");
        this.mAmbientBrightnessThresholdsIdle.dump(printWriter);
    }

    public float[] getLastSensorValues() {
        return this.mAmbientLightRingBuffer.getAllLuxValues();
    }

    public long[] getLastSensorTimestamps() {
        return this.mAmbientLightRingBuffer.getAllTimestamps();
    }

    public final String configStateToString(int i) {
        return i != 1 ? i != 2 ? i != 3 ? String.valueOf(i) : "AUTO_BRIGHTNESS_OFF_DUE_TO_DISPLAY_STATE" : "AUTO_BRIGHTNESS_DISABLED" : "AUTO_BRIGHTNESS_ENABLED";
    }

    public final boolean setLightSensorEnabled(boolean z) {
        if (z) {
            if (!this.mLightSensorEnabled) {
                this.mLightSensorEnabled = true;
                this.mLightSensorEnableTime = this.mClock.uptimeMillis();
                this.mCurrentLightSensorRate = this.mInitialLightSensorRate;
                registerForegroundAppUpdater();
                this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, this.mCurrentLightSensorRate * 1000, this.mHandler);
                return true;
            }
        } else if (this.mLightSensorEnabled) {
            this.mLightSensorEnabled = false;
            boolean z2 = !this.mResetAmbientLuxAfterWarmUpConfig;
            this.mAmbientLuxValid = z2;
            if (!z2) {
                this.mPreThresholdLux = Float.NaN;
            }
            this.mScreenAutoBrightness = Float.NaN;
            this.mRawScreenAutoBrightness = Float.NaN;
            this.mPreThresholdBrightness = Float.NaN;
            this.mRecentLightSamples = 0;
            this.mAmbientLightRingBuffer.clear();
            this.mCurrentLightSensorRate = -1;
            this.mHandler.removeMessages(1);
            unregisterForegroundAppUpdater();
            this.mSensorManager.unregisterListener(this.mLightSensorListener);
        }
        return false;
    }

    public final void handleLightSensorEvent(long j, float f) {
        Trace.traceCounter(131072L, "ALS", (int) f);
        this.mHandler.removeMessages(1);
        if (this.mAmbientLightRingBuffer.size() == 0) {
            adjustLightSensorRate(this.mNormalLightSensorRate);
        }
        applyLightSensorMeasurement(j, f);
        updateAmbientLux(j);
    }

    public final void applyLightSensorMeasurement(long j, float f) {
        this.mRecentLightSamples++;
        this.mAmbientLightRingBuffer.prune(j - this.mAmbientLightHorizonLong);
        this.mAmbientLightRingBuffer.push(j, f);
        this.mLastObservedLux = f;
        this.mLastObservedLuxTime = j;
    }

    public final void adjustLightSensorRate(int i) {
        if (i != this.mCurrentLightSensorRate) {
            if (this.mLoggingEnabled) {
                Slog.d("AutomaticBrightnessController", "adjustLightSensorRate: previousRate=" + this.mCurrentLightSensorRate + ", currentRate=" + i);
            }
            this.mCurrentLightSensorRate = i;
            this.mSensorManager.unregisterListener(this.mLightSensorListener);
            this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, i * 1000, this.mHandler);
        }
    }

    public final boolean setAutoBrightnessAdjustment(float f) {
        return this.mCurrentBrightnessMapper.setAutoBrightnessAdjustment(f);
    }

    public final void setAmbientLux(float f) {
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "setAmbientLux(" + f + ")");
        }
        if (f < 0.0f) {
            Slog.w("AutomaticBrightnessController", "Ambient lux was negative, ignoring and setting to 0");
            f = 0.0f;
        }
        this.mAmbientLux = f;
        if (isInIdleMode()) {
            this.mAmbientBrighteningThreshold = this.mAmbientBrightnessThresholdsIdle.getBrighteningThreshold(f);
            this.mAmbientDarkeningThreshold = this.mAmbientBrightnessThresholdsIdle.getDarkeningThreshold(f);
        } else {
            this.mAmbientBrighteningThreshold = this.mAmbientBrightnessThresholds.getBrighteningThreshold(f);
            this.mAmbientDarkeningThreshold = this.mAmbientBrightnessThresholds.getDarkeningThreshold(f);
        }
        this.mHbmController.onAmbientLuxChange(this.mAmbientLux);
        if (this.mShortTermModelValid) {
            return;
        }
        float f2 = this.mShortTermModelAnchor;
        if (f2 != -1.0f) {
            if (this.mCurrentBrightnessMapper.shouldResetShortTermModel(this.mAmbientLux, f2)) {
                resetShortTermModel();
            } else {
                this.mShortTermModelValid = true;
            }
        }
    }

    public final float calculateAmbientLux(long j, long j2) {
        int i;
        long j3;
        long j4 = j;
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "calculateAmbientLux(" + j4 + ", " + j2 + ")");
        }
        int size = this.mAmbientLightRingBuffer.size();
        if (size == 0) {
            Slog.e("AutomaticBrightnessController", "calculateAmbientLux: No ambient light readings available");
            return -1.0f;
        }
        long j5 = j4 - j2;
        int i2 = 0;
        int i3 = 0;
        while (true) {
            i = size - 1;
            if (i2 >= i) {
                break;
            }
            i2++;
            if (this.mAmbientLightRingBuffer.getTime(i2) > j5) {
                break;
            }
            i3++;
        }
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "calculateAmbientLux: selected endIndex=" + i3 + ", point=(" + this.mAmbientLightRingBuffer.getTime(i3) + ", " + this.mAmbientLightRingBuffer.getLux(i3) + ")");
        }
        float f = 0.0f;
        long j6 = 100;
        float f2 = 0.0f;
        while (i >= i3) {
            long time = this.mAmbientLightRingBuffer.getTime(i);
            if (i == i3 && time < j5) {
                time = j5;
            }
            long j7 = time - j4;
            float calculateWeight = calculateWeight(j7, j6);
            float lux = this.mAmbientLightRingBuffer.getLux(i);
            if (this.mLoggingEnabled) {
                StringBuilder sb = new StringBuilder();
                j3 = j5;
                sb.append("calculateAmbientLux: [");
                sb.append(j7);
                sb.append(", ");
                sb.append(j6);
                sb.append("]: lux=");
                sb.append(lux);
                sb.append(", weight=");
                sb.append(calculateWeight);
                Slog.d("AutomaticBrightnessController", sb.toString());
            } else {
                j3 = j5;
            }
            f += calculateWeight;
            f2 += lux * calculateWeight;
            i--;
            j4 = j;
            j5 = j3;
            j6 = j7;
        }
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "calculateAmbientLux: totalWeight=" + f + ", newAmbientLux=" + (f2 / f));
        }
        return f2 / f;
    }

    public final float calculateWeight(long j, long j2) {
        return weightIntegral(j2) - weightIntegral(j);
    }

    public final float weightIntegral(long j) {
        float f = (float) j;
        return f * ((0.5f * f) + this.mWeightingIntercept);
    }

    public final long nextAmbientLightBrighteningTransition(long j) {
        for (int size = this.mAmbientLightRingBuffer.size() - 1; size >= 0 && this.mAmbientLightRingBuffer.getLux(size) > this.mAmbientBrighteningThreshold; size--) {
            j = this.mAmbientLightRingBuffer.getTime(size);
        }
        return j + this.mBrighteningLightDebounceConfig;
    }

    public final long nextAmbientLightDarkeningTransition(long j) {
        for (int size = this.mAmbientLightRingBuffer.size() - 1; size >= 0 && this.mAmbientLightRingBuffer.getLux(size) < this.mAmbientDarkeningThreshold; size--) {
            j = this.mAmbientLightRingBuffer.getTime(size);
        }
        return j + this.mDarkeningLightDebounceConfig;
    }

    public final void updateAmbientLux() {
        long uptimeMillis = this.mClock.uptimeMillis();
        this.mAmbientLightRingBuffer.prune(uptimeMillis - this.mAmbientLightHorizonLong);
        updateAmbientLux(uptimeMillis);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x00a3, code lost:
        if (r7 <= r13) goto L21;
     */
    /* JADX WARN: Removed duplicated region for block: B:38:0x010c  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0114  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updateAmbientLux(long j) {
        long min;
        if (!this.mAmbientLuxValid) {
            long j2 = this.mLightSensorWarmUpTimeConfig + this.mLightSensorEnableTime;
            if (j < j2) {
                if (this.mLoggingEnabled) {
                    Slog.d("AutomaticBrightnessController", "updateAmbientLux: Sensor not ready yet: time=" + j + ", timeWhenSensorWarmedUp=" + j2);
                }
                this.mHandler.sendEmptyMessageAtTime(1, j2);
                return;
            }
            setAmbientLux(calculateAmbientLux(j, this.mAmbientLightHorizonShort));
            this.mAmbientLuxValid = true;
            if (this.mLoggingEnabled) {
                Slog.d("AutomaticBrightnessController", "updateAmbientLux: Initializing: mAmbientLightRingBuffer=" + this.mAmbientLightRingBuffer + ", mAmbientLux=" + this.mAmbientLux);
            }
            updateAutoBrightness(true, false);
        }
        long nextAmbientLightBrighteningTransition = nextAmbientLightBrighteningTransition(j);
        long nextAmbientLightDarkeningTransition = nextAmbientLightDarkeningTransition(j);
        this.mSlowAmbientLux = calculateAmbientLux(j, this.mAmbientLightHorizonLong);
        float calculateAmbientLux = calculateAmbientLux(j, this.mAmbientLightHorizonShort);
        this.mFastAmbientLux = calculateAmbientLux;
        float f = this.mSlowAmbientLux;
        float f2 = this.mAmbientBrighteningThreshold;
        if (f < f2 || calculateAmbientLux < f2 || nextAmbientLightBrighteningTransition > j) {
            float f3 = this.mAmbientDarkeningThreshold;
            if (f <= f3) {
                if (calculateAmbientLux <= f3) {
                }
            }
            min = Math.min(nextAmbientLightDarkeningTransition, nextAmbientLightBrighteningTransition);
            if (min <= j) {
                min = this.mNormalLightSensorRate + j;
            }
            if (this.mLoggingEnabled) {
                Slog.d("AutomaticBrightnessController", "updateAmbientLux: Scheduling ambient lux update for " + min + TimeUtils.formatUptime(min));
            }
            this.mHandler.sendEmptyMessageAtTime(1, min);
        }
        this.mPreThresholdLux = this.mAmbientLux;
        setAmbientLux(calculateAmbientLux);
        if (this.mLoggingEnabled) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateAmbientLux: ");
            sb.append(this.mFastAmbientLux > this.mAmbientLux ? "Brightened" : "Darkened");
            sb.append(": mBrighteningLuxThreshold=");
            sb.append(this.mAmbientBrighteningThreshold);
            sb.append(", mAmbientDarkeningThreshold=");
            sb.append(this.mAmbientDarkeningThreshold);
            sb.append(", mAmbientLightRingBuffer=");
            sb.append(this.mAmbientLightRingBuffer);
            sb.append(", mAmbientLux=");
            sb.append(this.mAmbientLux);
            Slog.d("AutomaticBrightnessController", sb.toString());
        }
        updateAutoBrightness(true, false);
        nextAmbientLightBrighteningTransition = nextAmbientLightBrighteningTransition(j);
        nextAmbientLightDarkeningTransition = nextAmbientLightDarkeningTransition(j);
        min = Math.min(nextAmbientLightDarkeningTransition, nextAmbientLightBrighteningTransition);
        if (min <= j) {
        }
        if (this.mLoggingEnabled) {
        }
        this.mHandler.sendEmptyMessageAtTime(1, min);
    }

    public final void updateAutoBrightness(boolean z, boolean z2) {
        if (this.mAmbientLuxValid) {
            float brightness = this.mCurrentBrightnessMapper.getBrightness(this.mAmbientLux, this.mForegroundAppPackageName, this.mForegroundAppCategory);
            this.mRawScreenAutoBrightness = brightness;
            float clampScreenBrightness = clampScreenBrightness(brightness);
            float f = this.mScreenAutoBrightness;
            boolean floatEquals = BrightnessSynchronizer.floatEquals(f, clampScreenBrightness(f));
            boolean z3 = !Float.isNaN(this.mScreenAutoBrightness) && clampScreenBrightness > this.mScreenDarkeningThreshold && clampScreenBrightness < this.mScreenBrighteningThreshold;
            if (z3 && !z2 && floatEquals) {
                if (this.mLoggingEnabled) {
                    Slog.d("AutomaticBrightnessController", "ignoring newScreenAutoBrightness: " + this.mScreenDarkeningThreshold + " < " + clampScreenBrightness + " < " + this.mScreenBrighteningThreshold);
                }
            } else if (BrightnessSynchronizer.floatEquals(this.mScreenAutoBrightness, clampScreenBrightness)) {
            } else {
                if (this.mLoggingEnabled) {
                    Slog.d("AutomaticBrightnessController", "updateAutoBrightness: mScreenAutoBrightness=" + this.mScreenAutoBrightness + ", newScreenAutoBrightness=" + clampScreenBrightness);
                }
                if (!z3) {
                    this.mPreThresholdBrightness = this.mScreenAutoBrightness;
                }
                this.mScreenAutoBrightness = clampScreenBrightness;
                if (isInIdleMode()) {
                    this.mScreenBrighteningThreshold = clampScreenBrightness(this.mScreenBrightnessThresholdsIdle.getBrighteningThreshold(clampScreenBrightness));
                    this.mScreenDarkeningThreshold = clampScreenBrightness(this.mScreenBrightnessThresholdsIdle.getDarkeningThreshold(clampScreenBrightness));
                } else {
                    this.mScreenBrighteningThreshold = clampScreenBrightness(this.mScreenBrightnessThresholds.getBrighteningThreshold(clampScreenBrightness));
                    this.mScreenDarkeningThreshold = clampScreenBrightness(this.mScreenBrightnessThresholds.getDarkeningThreshold(clampScreenBrightness));
                }
                if (z) {
                    this.mCallbacks.updateBrightness();
                }
            }
        }
    }

    public final float clampScreenBrightness(float f) {
        return MathUtils.constrain(f, Math.min(this.mHbmController.getCurrentBrightnessMin(), this.mBrightnessThrottler.getBrightnessCap()), Math.min(this.mHbmController.getCurrentBrightnessMax(), this.mBrightnessThrottler.getBrightnessCap()));
    }

    public final void prepareBrightnessAdjustmentSample() {
        if (!this.mBrightnessAdjustmentSamplePending) {
            this.mBrightnessAdjustmentSamplePending = true;
            this.mBrightnessAdjustmentSampleOldLux = this.mAmbientLuxValid ? this.mAmbientLux : -1.0f;
            this.mBrightnessAdjustmentSampleOldBrightness = this.mScreenAutoBrightness;
        } else {
            this.mHandler.removeMessages(2);
        }
        this.mHandler.sendEmptyMessageDelayed(2, 10000L);
    }

    public final void collectBrightnessAdjustmentSample() {
        if (this.mBrightnessAdjustmentSamplePending) {
            this.mBrightnessAdjustmentSamplePending = false;
            if (this.mAmbientLuxValid) {
                float f = this.mScreenAutoBrightness;
                if (f >= 0.0f || f == -1.0f) {
                    if (this.mLoggingEnabled) {
                        Slog.d("AutomaticBrightnessController", "Auto-brightness adjustment changed by user: lux=" + this.mAmbientLux + ", brightness=" + this.mScreenAutoBrightness + ", ring=" + this.mAmbientLightRingBuffer);
                    }
                    EventLog.writeEvent(35000, Float.valueOf(this.mBrightnessAdjustmentSampleOldLux), Float.valueOf(this.mBrightnessAdjustmentSampleOldBrightness), Float.valueOf(this.mAmbientLux), Float.valueOf(this.mScreenAutoBrightness));
                }
            }
        }
    }

    public final void registerForegroundAppUpdater() {
        try {
            this.mActivityTaskManager.registerTaskStackListener(this.mTaskStackListener);
            updateForegroundApp();
        } catch (RemoteException e) {
            if (this.mLoggingEnabled) {
                Slog.e("AutomaticBrightnessController", "Failed to register foreground app updater: " + e);
            }
        }
    }

    public final void unregisterForegroundAppUpdater() {
        try {
            this.mActivityTaskManager.unregisterTaskStackListener(this.mTaskStackListener);
        } catch (RemoteException unused) {
        }
        this.mForegroundAppPackageName = null;
        this.mForegroundAppCategory = -1;
    }

    public final void updateForegroundApp() {
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "Attempting to update foreground app");
        }
        this.mInjector.getBackgroundThreadHandler().post(new Runnable() { // from class: com.android.server.display.AutomaticBrightnessController.1
            @Override // java.lang.Runnable
            public void run() {
                ComponentName componentName;
                try {
                    ActivityTaskManager.RootTaskInfo focusedRootTaskInfo = AutomaticBrightnessController.this.mActivityTaskManager.getFocusedRootTaskInfo();
                    if (focusedRootTaskInfo != null && (componentName = focusedRootTaskInfo.topActivity) != null) {
                        String packageName = componentName.getPackageName();
                        String str = AutomaticBrightnessController.this.mForegroundAppPackageName;
                        if (str == null || !str.equals(packageName)) {
                            AutomaticBrightnessController.this.mPendingForegroundAppPackageName = packageName;
                            AutomaticBrightnessController.this.mPendingForegroundAppCategory = -1;
                            try {
                                AutomaticBrightnessController.this.mPendingForegroundAppCategory = AutomaticBrightnessController.this.mPackageManager.getApplicationInfo(packageName, 4194304).category;
                            } catch (PackageManager.NameNotFoundException unused) {
                            }
                            AutomaticBrightnessController.this.mHandler.sendEmptyMessage(5);
                        }
                    }
                } catch (RemoteException unused2) {
                }
            }
        });
    }

    public final void updateForegroundAppSync() {
        if (this.mLoggingEnabled) {
            Slog.d("AutomaticBrightnessController", "Updating foreground app: packageName=" + this.mPendingForegroundAppPackageName + ", category=" + this.mPendingForegroundAppCategory);
        }
        this.mForegroundAppPackageName = this.mPendingForegroundAppPackageName;
        this.mPendingForegroundAppPackageName = null;
        this.mForegroundAppCategory = this.mPendingForegroundAppCategory;
        this.mPendingForegroundAppCategory = -1;
        updateAutoBrightness(true, false);
    }

    public void switchToIdleMode() {
        if (this.mIdleModeBrightnessMapper == null || this.mCurrentBrightnessMapper.isForIdleMode()) {
            return;
        }
        Slog.i("AutomaticBrightnessController", "Switching to Idle Screen Brightness Mode");
        this.mCurrentBrightnessMapper = this.mIdleModeBrightnessMapper;
        resetShortTermModel();
        update();
    }

    public void switchToInteractiveScreenBrightnessMode() {
        if (this.mCurrentBrightnessMapper.isForIdleMode()) {
            Slog.i("AutomaticBrightnessController", "Switching to Interactive Screen Brightness Mode");
            this.mCurrentBrightnessMapper = this.mInteractiveModeBrightnessMapper;
            resetShortTermModel();
            update();
        }
    }

    public float convertToNits(float f) {
        BrightnessMappingStrategy brightnessMappingStrategy = this.mCurrentBrightnessMapper;
        if (brightnessMappingStrategy != null) {
            return brightnessMappingStrategy.convertToNits(f);
        }
        return -1.0f;
    }

    public float convertToFloatScale(float f) {
        BrightnessMappingStrategy brightnessMappingStrategy = this.mCurrentBrightnessMapper;
        if (brightnessMappingStrategy != null) {
            return brightnessMappingStrategy.convertToFloatScale(f);
        }
        return Float.NaN;
    }

    public void recalculateSplines(boolean z, float[] fArr) {
        this.mCurrentBrightnessMapper.recalculateSplines(z, fArr);
        resetShortTermModel();
        if (z) {
            setScreenBrightnessByUser(getAutomaticScreenBrightness());
        }
    }

    /* loaded from: classes.dex */
    public final class AutomaticBrightnessHandler extends Handler {
        public AutomaticBrightnessHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    AutomaticBrightnessController.this.updateAmbientLux();
                    return;
                case 2:
                    AutomaticBrightnessController.this.collectBrightnessAdjustmentSample();
                    return;
                case 3:
                    AutomaticBrightnessController.this.invalidateShortTermModel();
                    return;
                case 4:
                    AutomaticBrightnessController.this.updateForegroundApp();
                    return;
                case 5:
                    AutomaticBrightnessController.this.updateForegroundAppSync();
                    return;
                case 6:
                    AutomaticBrightnessController.this.updateAutoBrightness(true, false);
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes.dex */
    public class TaskStackListenerImpl extends TaskStackListener {
        public TaskStackListenerImpl() {
        }

        public void onTaskStackChanged() {
            AutomaticBrightnessController.this.mHandler.sendEmptyMessage(4);
        }
    }

    /* loaded from: classes.dex */
    public static final class AmbientLightRingBuffer {
        public int mCapacity;
        public Clock mClock;
        public int mCount;
        public int mEnd;
        public float[] mRingLux;
        public long[] mRingTime;
        public int mStart;

        public AmbientLightRingBuffer(long j, int i, Clock clock) {
            if (j <= 0) {
                throw new IllegalArgumentException("lightSensorRate must be above 0");
            }
            int ceil = (int) Math.ceil((i * 1.5f) / ((float) j));
            this.mCapacity = ceil;
            this.mRingLux = new float[ceil];
            this.mRingTime = new long[ceil];
            this.mClock = clock;
        }

        public float getLux(int i) {
            return this.mRingLux[offsetOf(i)];
        }

        public float[] getAllLuxValues() {
            int i = this.mCount;
            float[] fArr = new float[i];
            if (i == 0) {
                return fArr;
            }
            int i2 = this.mStart;
            if (i2 < this.mEnd) {
                System.arraycopy(this.mRingLux, i2, fArr, 0, i);
            } else {
                System.arraycopy(this.mRingLux, i2, fArr, 0, this.mCapacity - i2);
                System.arraycopy(this.mRingLux, 0, fArr, this.mCapacity - this.mStart, this.mEnd);
            }
            return fArr;
        }

        public long getTime(int i) {
            return this.mRingTime[offsetOf(i)];
        }

        public long[] getAllTimestamps() {
            int i = this.mCount;
            long[] jArr = new long[i];
            if (i == 0) {
                return jArr;
            }
            int i2 = this.mStart;
            if (i2 < this.mEnd) {
                System.arraycopy(this.mRingTime, i2, jArr, 0, i);
            } else {
                System.arraycopy(this.mRingTime, i2, jArr, 0, this.mCapacity - i2);
                System.arraycopy(this.mRingTime, 0, jArr, this.mCapacity - this.mStart, this.mEnd);
            }
            return jArr;
        }

        public void push(long j, float f) {
            int i = this.mEnd;
            int i2 = this.mCount;
            int i3 = this.mCapacity;
            if (i2 == i3) {
                int i4 = i3 * 2;
                float[] fArr = new float[i4];
                long[] jArr = new long[i4];
                int i5 = this.mStart;
                int i6 = i3 - i5;
                System.arraycopy(this.mRingLux, i5, fArr, 0, i6);
                System.arraycopy(this.mRingTime, this.mStart, jArr, 0, i6);
                int i7 = this.mStart;
                if (i7 != 0) {
                    System.arraycopy(this.mRingLux, 0, fArr, i6, i7);
                    System.arraycopy(this.mRingTime, 0, jArr, i6, this.mStart);
                }
                this.mRingLux = fArr;
                this.mRingTime = jArr;
                int i8 = this.mCapacity;
                this.mCapacity = i4;
                this.mStart = 0;
                i = i8;
            }
            this.mRingTime[i] = j;
            this.mRingLux[i] = f;
            int i9 = i + 1;
            this.mEnd = i9;
            if (i9 == this.mCapacity) {
                this.mEnd = 0;
            }
            this.mCount++;
        }

        public void prune(long j) {
            if (this.mCount == 0) {
                return;
            }
            while (true) {
                int i = this.mCount;
                if (i <= 1) {
                    break;
                }
                int i2 = this.mStart + 1;
                int i3 = this.mCapacity;
                if (i2 >= i3) {
                    i2 -= i3;
                }
                if (this.mRingTime[i2] > j) {
                    break;
                }
                this.mStart = i2;
                this.mCount = i - 1;
            }
            long[] jArr = this.mRingTime;
            int i4 = this.mStart;
            if (jArr[i4] < j) {
                jArr[i4] = j;
            }
        }

        public int size() {
            return this.mCount;
        }

        public void clear() {
            this.mStart = 0;
            this.mEnd = 0;
            this.mCount = 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            int i = 0;
            while (true) {
                int i2 = this.mCount;
                if (i < i2) {
                    int i3 = i + 1;
                    long time = i3 < i2 ? getTime(i3) : this.mClock.uptimeMillis();
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append(getLux(i));
                    sb.append(" / ");
                    sb.append(time - getTime(i));
                    sb.append("ms");
                    i = i3;
                } else {
                    sb.append(']');
                    return sb.toString();
                }
            }
        }

        public final int offsetOf(int i) {
            if (i >= this.mCount || i < 0) {
                throw new ArrayIndexOutOfBoundsException(i);
            }
            int i2 = i + this.mStart;
            int i3 = this.mCapacity;
            return i2 >= i3 ? i2 - i3 : i2;
        }
    }

    /* loaded from: classes.dex */
    public static class Injector {
        public Handler getBackgroundThreadHandler() {
            return BackgroundThread.getHandler();
        }

        public Clock createClock() {
            return new Clock() { // from class: com.android.server.display.AutomaticBrightnessController$Injector$$ExternalSyntheticLambda0
                @Override // com.android.server.display.AutomaticBrightnessController.Clock
                public final long uptimeMillis() {
                    return SystemClock.uptimeMillis();
                }
            };
        }
    }
}
