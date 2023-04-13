package com.android.server.display;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.AmbientBrightnessDayStats;
import android.hardware.display.BrightnessChangeEvent;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.BrightnessInfo;
import android.hardware.display.DisplayManagerInternal;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.provider.Settings;
import android.util.FloatProperty;
import android.util.Log;
import android.util.MathUtils;
import android.util.MutableFloat;
import android.util.MutableInt;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.Display;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.RingBuffer;
import com.android.server.LocalServices;
import com.android.server.display.AutomaticBrightnessController;
import com.android.server.display.BrightnessSetting;
import com.android.server.display.DisplayDeviceConfig;
import com.android.server.display.HighBrightnessModeController;
import com.android.server.display.RampAnimator;
import com.android.server.display.ScreenOffBrightnessSensorController;
import com.android.server.display.brightness.BrightnessEvent;
import com.android.server.display.brightness.BrightnessReason;
import com.android.server.display.color.ColorDisplayService;
import com.android.server.display.utils.SensorUtils;
import com.android.server.display.whitebalance.DisplayWhiteBalanceController;
import com.android.server.display.whitebalance.DisplayWhiteBalanceFactory;
import com.android.server.display.whitebalance.DisplayWhiteBalanceSettings;
import com.android.server.p006am.BatteryStatsService;
import com.android.server.policy.WindowManagerPolicy;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class DisplayPowerController implements AutomaticBrightnessController.Callbacks, DisplayWhiteBalanceController.Callbacks, DisplayPowerControllerInterface {
    public final boolean mAllowAutoBrightnessWhileDozingConfig;
    public boolean mAppliedAutoBrightness;
    public boolean mAppliedBrightnessBoost;
    public boolean mAppliedDimming;
    public boolean mAppliedLowPower;
    public boolean mAppliedScreenBrightnessOverride;
    public boolean mAppliedTemporaryAutoBrightnessAdjustment;
    public boolean mAppliedTemporaryBrightness;
    public boolean mAppliedThrottling;
    public float mAutoBrightnessAdjustment;
    public AutomaticBrightnessController mAutomaticBrightnessController;
    public final IBatteryStats mBatteryStats;
    public final DisplayBlanker mBlanker;
    public boolean mBootCompleted;
    public final boolean mBrightnessBucketsInDozeConfig;
    public BrightnessConfiguration mBrightnessConfiguration;
    public RingBuffer<BrightnessEvent> mBrightnessEventRingBuffer;
    public long mBrightnessRampDecreaseMaxTimeMillis;
    public long mBrightnessRampIncreaseMaxTimeMillis;
    public float mBrightnessRampRateFastDecrease;
    public float mBrightnessRampRateFastIncrease;
    public float mBrightnessRampRateSlowDecrease;
    public float mBrightnessRampRateSlowIncrease;
    public final BrightnessSetting mBrightnessSetting;
    public BrightnessSetting.BrightnessSettingListener mBrightnessSettingListener;
    public final BrightnessThrottler mBrightnessThrottler;
    public String mBrightnessThrottlingDataId;
    public float mBrightnessToFollow;
    public final BrightnessTracker mBrightnessTracker;
    public final DisplayManagerInternal.DisplayPowerCallbacks mCallbacks;
    public final ColorDisplayService.ColorDisplayServiceInternal mCdsi;
    public final Clock mClock;
    public final boolean mColorFadeEnabled;
    public final boolean mColorFadeFadesConfig;
    public ObjectAnimator mColorFadeOffAnimator;
    public ObjectAnimator mColorFadeOnAnimator;
    public final Context mContext;
    public float mCurrentScreenBrightnessSetting;
    public final boolean mDisplayBlanksAfterDozeConfig;
    public DisplayDevice mDisplayDevice;
    public DisplayDeviceConfig mDisplayDeviceConfig;
    public final int mDisplayId;
    @GuardedBy({"mLock"})
    public boolean mDisplayReadyLocked;
    public int mDisplayStatsId;
    public final DisplayWhiteBalanceController mDisplayWhiteBalanceController;
    public final DisplayWhiteBalanceSettings mDisplayWhiteBalanceSettings;
    public boolean mDozing;
    public final DisplayControllerHandler mHandler;
    public final HighBrightnessModeController mHbmController;
    public final HighBrightnessModeMetadata mHighBrightnessModeMetadata;
    public BrightnessMappingStrategy mIdleModeBrightnessMapper;
    public boolean mIgnoreProximityUntilChanged;
    public float mInitialAutoBrightness;
    public final Injector mInjector;
    public BrightnessMappingStrategy mInteractiveModeBrightnessMapper;
    public boolean mIsEnabled;
    public boolean mIsInTransition;
    public boolean mIsRbcActive;
    public final BrightnessEvent mLastBrightnessEvent;
    public Sensor mLightSensor;
    public final LogicalDisplay mLogicalDisplay;
    public float[] mNitsRange;
    public final Runnable mOnBrightnessChangeRunnable;
    public int mOnProximityNegativeMessages;
    public int mOnProximityPositiveMessages;
    public boolean mOnStateChangedPending;
    public float mPendingAutoBrightnessAdjustment;
    @GuardedBy({"mLock"})
    public boolean mPendingRequestChangedLocked;
    @GuardedBy({"mLock"})
    public DisplayManagerInternal.DisplayPowerRequest mPendingRequestLocked;
    public float mPendingScreenBrightnessSetting;
    public boolean mPendingScreenOff;
    public ScreenOffUnblocker mPendingScreenOffUnblocker;
    public ScreenOnUnblocker mPendingScreenOnUnblocker;
    @GuardedBy({"mLock"})
    public boolean mPendingUpdatePowerStateLocked;
    @GuardedBy({"mLock"})
    public boolean mPendingWaitForNegativeProximityLocked;
    public final boolean mPersistBrightnessNitsForDefaultDisplay;
    public DisplayManagerInternal.DisplayPowerRequest mPowerRequest;
    public DisplayPowerState mPowerState;
    public Sensor mProximitySensor;
    public boolean mProximitySensorEnabled;
    public float mProximityThreshold;
    public final float mScreenBrightnessDefault;
    public final float mScreenBrightnessDimConfig;
    public final float mScreenBrightnessDozeConfig;
    public final float mScreenBrightnessMinimumDimAmount;
    public RampAnimator.DualRampAnimator<DisplayPowerState> mScreenBrightnessRampAnimator;
    public boolean mScreenOffBecauseOfProximity;
    public long mScreenOffBlockStartRealTime;
    public Sensor mScreenOffBrightnessSensor;
    public ScreenOffBrightnessSensorController mScreenOffBrightnessSensorController;
    public long mScreenOnBlockStartRealTime;
    public final SensorManager mSensorManager;
    public final SettingsObserver mSettingsObserver;
    public boolean mShouldResetShortTermModel;
    public final boolean mSkipScreenOnBrightnessRamp;
    public boolean mStopped;
    public final String mSuspendBlockerIdOnStateChanged;
    public final String mSuspendBlockerIdProxDebounce;
    public final String mSuspendBlockerIdProxNegative;
    public final String mSuspendBlockerIdProxPositive;
    public final String mSuspendBlockerIdUnfinishedBusiness;
    public final String mTag;
    public final BrightnessEvent mTempBrightnessEvent;
    public float mTemporaryAutoBrightnessAdjustment;
    public float mTemporaryScreenBrightness;
    public boolean mUnfinishedBusiness;
    public String mUniqueDisplayId;
    public boolean mUseAutoBrightness;
    public boolean mUseSoftwareAutoBrightnessConfig;
    public boolean mWaitingForNegativeProximity;
    public final WindowManagerPolicy mWindowManagerPolicy;
    public final Object mLock = new Object();
    public int mLeadDisplayId = -1;
    @GuardedBy({"mCachedBrightnessInfo"})
    public final CachedBrightnessInfo mCachedBrightnessInfo = new CachedBrightnessInfo();
    public int mProximity = -1;
    public int mPendingProximity = -1;
    public long mPendingProximityDebounceTime = -1;
    public int mReportedScreenStateToPolicy = -1;
    public final BrightnessReason mBrightnessReason = new BrightnessReason();
    public final BrightnessReason mBrightnessReasonTemp = new BrightnessReason();
    public float mLastStatsBrightness = 0.0f;
    public int mSkipRampState = 0;
    public float mLastUserSetScreenBrightness = Float.NaN;
    @GuardedBy({"mLock"})
    public final SparseArray<DisplayPowerControllerInterface> mDisplayBrightnessFollowers = new SparseArray<>();
    public final Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() { // from class: com.android.server.display.DisplayPowerController.3
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            DisplayPowerController.this.sendUpdatePowerState();
        }
    };
    public final RampAnimator.Listener mRampAnimatorListener = new RampAnimator.Listener() { // from class: com.android.server.display.DisplayPowerController.4
        @Override // com.android.server.display.RampAnimator.Listener
        public void onAnimationEnd() {
            DisplayPowerController.this.sendUpdatePowerState();
            DisplayPowerController.this.mHandler.sendMessageAtTime(DisplayPowerController.this.mHandler.obtainMessage(12), DisplayPowerController.this.mClock.uptimeMillis());
        }
    };
    public final Runnable mCleanListener = new Runnable() { // from class: com.android.server.display.DisplayPowerController$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            DisplayPowerController.this.sendUpdatePowerState();
        }
    };
    public final Runnable mOnStateChangedRunnable = new Runnable() { // from class: com.android.server.display.DisplayPowerController.6
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerController.this.mOnStateChangedPending = false;
            DisplayPowerController.this.mCallbacks.onStateChanged();
            DisplayPowerController.this.mCallbacks.releaseSuspendBlocker(DisplayPowerController.this.mSuspendBlockerIdOnStateChanged);
        }
    };
    public final Runnable mOnProximityPositiveRunnable = new Runnable() { // from class: com.android.server.display.DisplayPowerController.7
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerController displayPowerController = DisplayPowerController.this;
            displayPowerController.mOnProximityPositiveMessages--;
            DisplayPowerController.this.mCallbacks.onProximityPositive();
            DisplayPowerController.this.mCallbacks.releaseSuspendBlocker(DisplayPowerController.this.mSuspendBlockerIdProxPositive);
        }
    };
    public final Runnable mOnProximityNegativeRunnable = new Runnable() { // from class: com.android.server.display.DisplayPowerController.8
        @Override // java.lang.Runnable
        public void run() {
            DisplayPowerController displayPowerController = DisplayPowerController.this;
            displayPowerController.mOnProximityNegativeMessages--;
            DisplayPowerController.this.mCallbacks.onProximityNegative();
            DisplayPowerController.this.mCallbacks.releaseSuspendBlocker(DisplayPowerController.this.mSuspendBlockerIdProxNegative);
        }
    };
    public final SensorEventListener mProximitySensorListener = new SensorEventListener() { // from class: com.android.server.display.DisplayPowerController.9
        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (DisplayPowerController.this.mProximitySensorEnabled) {
                long uptimeMillis = DisplayPowerController.this.mClock.uptimeMillis();
                boolean z = false;
                float f = sensorEvent.values[0];
                if (f >= 0.0f && f < DisplayPowerController.this.mProximityThreshold) {
                    z = true;
                }
                DisplayPowerController.this.handleProximitySensorEvent(uptimeMillis, z);
            }
        }
    };

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface Clock {
        long uptimeMillis();
    }

    public final boolean isValidBrightnessValue(float f) {
        return f >= 0.0f && f <= 1.0f;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x01da  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x01f3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public DisplayPowerController(Context context, Injector injector, DisplayManagerInternal.DisplayPowerCallbacks displayPowerCallbacks, Handler handler, SensorManager sensorManager, DisplayBlanker displayBlanker, LogicalDisplay logicalDisplay, BrightnessTracker brightnessTracker, BrightnessSetting brightnessSetting, Runnable runnable, HighBrightnessModeMetadata highBrightnessModeMetadata, boolean z) {
        DisplayWhiteBalanceController displayWhiteBalanceController;
        DisplayWhiteBalanceSettings displayWhiteBalanceSettings;
        injector = injector == null ? new Injector() : injector;
        this.mInjector = injector;
        this.mClock = injector.getClock();
        this.mLogicalDisplay = logicalDisplay;
        int displayIdLocked = logicalDisplay.getDisplayIdLocked();
        this.mDisplayId = displayIdLocked;
        this.mTag = "DisplayPowerController[" + displayIdLocked + "]";
        this.mHighBrightnessModeMetadata = highBrightnessModeMetadata;
        this.mSuspendBlockerIdUnfinishedBusiness = getSuspendBlockerUnfinishedBusinessId(displayIdLocked);
        this.mSuspendBlockerIdOnStateChanged = getSuspendBlockerOnStateChangedId(displayIdLocked);
        this.mSuspendBlockerIdProxPositive = getSuspendBlockerProxPositiveId(displayIdLocked);
        this.mSuspendBlockerIdProxNegative = getSuspendBlockerProxNegativeId(displayIdLocked);
        this.mSuspendBlockerIdProxDebounce = getSuspendBlockerProxDebounceId(displayIdLocked);
        this.mDisplayDevice = logicalDisplay.getPrimaryDisplayDeviceLocked();
        String uniqueId = logicalDisplay.getPrimaryDisplayDeviceLocked().getUniqueId();
        this.mUniqueDisplayId = uniqueId;
        this.mDisplayStatsId = uniqueId.hashCode();
        this.mIsEnabled = logicalDisplay.isEnabledLocked();
        this.mIsInTransition = logicalDisplay.isInTransitionLocked();
        DisplayControllerHandler displayControllerHandler = new DisplayControllerHandler(handler.getLooper());
        this.mHandler = displayControllerHandler;
        this.mLastBrightnessEvent = new BrightnessEvent(displayIdLocked);
        this.mTempBrightnessEvent = new BrightnessEvent(displayIdLocked);
        this.mBrightnessThrottlingDataId = logicalDisplay.getBrightnessThrottlingDataIdLocked();
        if (displayIdLocked == 0) {
            this.mBatteryStats = BatteryStatsService.getService();
        } else {
            this.mBatteryStats = null;
        }
        this.mSettingsObserver = new SettingsObserver(displayControllerHandler);
        this.mCallbacks = displayPowerCallbacks;
        this.mSensorManager = sensorManager;
        this.mWindowManagerPolicy = (WindowManagerPolicy) LocalServices.getService(WindowManagerPolicy.class);
        this.mBlanker = displayBlanker;
        this.mContext = context;
        this.mBrightnessTracker = brightnessTracker;
        this.mBrightnessSetting = brightnessSetting;
        this.mOnBrightnessChangeRunnable = runnable;
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        Resources resources = context.getResources();
        this.mScreenBrightnessDozeConfig = clampAbsoluteBrightness(powerManager.getBrightnessConstraint(4));
        this.mScreenBrightnessDimConfig = clampAbsoluteBrightness(powerManager.getBrightnessConstraint(3));
        this.mScreenBrightnessMinimumDimAmount = resources.getFloat(17105106);
        this.mScreenBrightnessDefault = clampAbsoluteBrightness(logicalDisplay.getDisplayInfoLocked().brightnessDefault);
        this.mAllowAutoBrightnessWhileDozingConfig = resources.getBoolean(17891347);
        this.mPersistBrightnessNitsForDefaultDisplay = resources.getBoolean(17891761);
        this.mDisplayDeviceConfig = logicalDisplay.getPrimaryDisplayDeviceLocked().getDisplayDeviceConfig();
        loadBrightnessRampRates();
        this.mSkipScreenOnBrightnessRamp = resources.getBoolean(17891798);
        this.mHbmController = createHbmControllerLocked();
        this.mBrightnessThrottler = createBrightnessThrottlerLocked();
        saveBrightnessInfo(getScreenBrightnessSetting());
        if (displayIdLocked == 0) {
            try {
                displayWhiteBalanceSettings = new DisplayWhiteBalanceSettings(context, displayControllerHandler);
                try {
                    displayWhiteBalanceController = DisplayWhiteBalanceFactory.create(displayControllerHandler, sensorManager, resources);
                } catch (Exception e) {
                    e = e;
                    displayWhiteBalanceController = null;
                }
                try {
                    displayWhiteBalanceSettings.setCallbacks(this);
                    displayWhiteBalanceController.setCallbacks(this);
                } catch (Exception e2) {
                    e = e2;
                    Slog.e(this.mTag, "failed to set up display white-balance: " + e);
                    this.mDisplayWhiteBalanceSettings = displayWhiteBalanceSettings;
                    this.mDisplayWhiteBalanceController = displayWhiteBalanceController;
                    loadNitsRange(resources);
                    if (this.mDisplayId != 0) {
                    }
                    setUpAutoBrightness(resources, handler);
                    this.mColorFadeEnabled = !ActivityManager.isLowRamDeviceStatic();
                    this.mColorFadeFadesConfig = resources.getBoolean(17891367);
                    this.mDisplayBlanksAfterDozeConfig = resources.getBoolean(17891612);
                    this.mBrightnessBucketsInDozeConfig = resources.getBoolean(17891613);
                    loadProximitySensor();
                    loadNitBasedBrightnessSetting();
                    this.mBrightnessToFollow = Float.NaN;
                    this.mAutoBrightnessAdjustment = getAutoBrightnessAdjustmentSetting();
                    this.mTemporaryScreenBrightness = Float.NaN;
                    this.mPendingScreenBrightnessSetting = Float.NaN;
                    this.mTemporaryAutoBrightnessAdjustment = Float.NaN;
                    this.mPendingAutoBrightnessAdjustment = Float.NaN;
                    this.mBootCompleted = z;
                }
            } catch (Exception e3) {
                e = e3;
                displayWhiteBalanceController = null;
                displayWhiteBalanceSettings = null;
            }
        } else {
            displayWhiteBalanceController = null;
            displayWhiteBalanceSettings = null;
        }
        this.mDisplayWhiteBalanceSettings = displayWhiteBalanceSettings;
        this.mDisplayWhiteBalanceController = displayWhiteBalanceController;
        loadNitsRange(resources);
        if (this.mDisplayId != 0) {
            ColorDisplayService.ColorDisplayServiceInternal colorDisplayServiceInternal = (ColorDisplayService.ColorDisplayServiceInternal) LocalServices.getService(ColorDisplayService.ColorDisplayServiceInternal.class);
            this.mCdsi = colorDisplayServiceInternal;
            if (colorDisplayServiceInternal.setReduceBrightColorsListener(new ColorDisplayService.ReduceBrightColorsListener() { // from class: com.android.server.display.DisplayPowerController.1
                @Override // com.android.server.display.color.ColorDisplayService.ReduceBrightColorsListener
                public void onReduceBrightColorsActivationChanged(boolean z2, boolean z3) {
                    DisplayPowerController.this.applyReduceBrightColorsSplineAdjustment();
                }

                @Override // com.android.server.display.color.ColorDisplayService.ReduceBrightColorsListener
                public void onReduceBrightColorsStrengthChanged(int i) {
                    DisplayPowerController.this.applyReduceBrightColorsSplineAdjustment();
                }
            })) {
                applyReduceBrightColorsSplineAdjustment();
            }
        } else {
            this.mCdsi = null;
        }
        setUpAutoBrightness(resources, handler);
        this.mColorFadeEnabled = !ActivityManager.isLowRamDeviceStatic();
        this.mColorFadeFadesConfig = resources.getBoolean(17891367);
        this.mDisplayBlanksAfterDozeConfig = resources.getBoolean(17891612);
        this.mBrightnessBucketsInDozeConfig = resources.getBoolean(17891613);
        loadProximitySensor();
        loadNitBasedBrightnessSetting();
        this.mBrightnessToFollow = Float.NaN;
        this.mAutoBrightnessAdjustment = getAutoBrightnessAdjustmentSetting();
        this.mTemporaryScreenBrightness = Float.NaN;
        this.mPendingScreenBrightnessSetting = Float.NaN;
        this.mTemporaryAutoBrightnessAdjustment = Float.NaN;
        this.mPendingAutoBrightnessAdjustment = Float.NaN;
        this.mBootCompleted = z;
    }

    public final void applyReduceBrightColorsSplineAdjustment() {
        this.mHandler.obtainMessage(11).sendToTarget();
        sendUpdatePowerState();
    }

    public final void handleRbcChanged() {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController == null) {
            return;
        }
        if ((!automaticBrightnessController.isInIdleMode() && this.mInteractiveModeBrightnessMapper == null) || (this.mAutomaticBrightnessController.isInIdleMode() && this.mIdleModeBrightnessMapper == null)) {
            Log.w(this.mTag, "No brightness mapping available to recalculate splines for this mode");
            return;
        }
        float[] fArr = new float[this.mNitsRange.length];
        int i = 0;
        while (true) {
            float[] fArr2 = this.mNitsRange;
            if (i < fArr2.length) {
                fArr[i] = this.mCdsi.getReduceBrightColorsAdjustedBrightnessNits(fArr2[i]);
                i++;
            } else {
                boolean isReduceBrightColorsActivated = this.mCdsi.isReduceBrightColorsActivated();
                this.mIsRbcActive = isReduceBrightColorsActivated;
                this.mAutomaticBrightnessController.recalculateSplines(isReduceBrightColorsActivated, fArr);
                return;
            }
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public boolean isProximitySensorAvailable() {
        return this.mProximitySensor != null;
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public ParceledListSlice<BrightnessChangeEvent> getBrightnessEvents(int i, boolean z) {
        BrightnessTracker brightnessTracker = this.mBrightnessTracker;
        if (brightnessTracker == null) {
            return null;
        }
        return brightnessTracker.getEvents(i, z);
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void onSwitchUser(int i) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(14, Integer.valueOf(i)));
    }

    public final void handleOnSwitchUser(int i) {
        handleSettingsChange(true);
        handleBrightnessModeChange();
        BrightnessTracker brightnessTracker = this.mBrightnessTracker;
        if (brightnessTracker != null) {
            brightnessTracker.onSwitchUser(i);
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public int getDisplayId() {
        return this.mDisplayId;
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public int getLeadDisplayId() {
        return this.mLeadDisplayId;
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setBrightnessToFollow(float f, float f2, float f3) {
        this.mHbmController.onAmbientLuxChange(f3);
        if (f2 < 0.0f) {
            this.mBrightnessToFollow = f;
        } else {
            float convertToFloatScale = convertToFloatScale(f2);
            if (isValidBrightnessValue(convertToFloatScale)) {
                this.mBrightnessToFollow = convertToFloatScale;
            } else {
                this.mBrightnessToFollow = f;
            }
        }
        sendUpdatePowerState();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void addDisplayBrightnessFollower(DisplayPowerControllerInterface displayPowerControllerInterface) {
        synchronized (this.mLock) {
            this.mDisplayBrightnessFollowers.append(displayPowerControllerInterface.getDisplayId(), displayPowerControllerInterface);
            sendUpdatePowerStateLocked();
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void removeDisplayBrightnessFollower(final DisplayPowerControllerInterface displayPowerControllerInterface) {
        synchronized (this.mLock) {
            this.mDisplayBrightnessFollowers.remove(displayPowerControllerInterface.getDisplayId());
            this.mHandler.postAtTime(new Runnable() { // from class: com.android.server.display.DisplayPowerController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayPowerControllerInterface.this.setBrightnessToFollow(Float.NaN, -1.0f, 0.0f);
                }
            }, this.mClock.uptimeMillis());
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public ParceledListSlice<AmbientBrightnessDayStats> getAmbientBrightnessStats(int i) {
        BrightnessTracker brightnessTracker = this.mBrightnessTracker;
        if (brightnessTracker == null) {
            return null;
        }
        return brightnessTracker.getAmbientBrightnessStats(i);
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void persistBrightnessTrackerState() {
        BrightnessTracker brightnessTracker = this.mBrightnessTracker;
        if (brightnessTracker != null) {
            brightnessTracker.persistBrightnessTrackerState();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0031 A[Catch: all -> 0x0040, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0008, B:10:0x000d, B:12:0x0011, B:14:0x0016, B:16:0x001a, B:22:0x0031, B:24:0x0037, B:25:0x003c, B:26:0x003e, B:18:0x0023, B:20:0x0029), top: B:31:0x0003 }] */
    @Override // com.android.server.display.DisplayPowerControllerInterface
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean requestPowerState(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest, boolean z) {
        boolean z2;
        synchronized (this.mLock) {
            if (this.mStopped) {
                return true;
            }
            if (!z || this.mPendingWaitForNegativeProximityLocked) {
                z2 = false;
            } else {
                this.mPendingWaitForNegativeProximityLocked = true;
                z2 = true;
            }
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest2 = this.mPendingRequestLocked;
            if (displayPowerRequest2 == null) {
                this.mPendingRequestLocked = new DisplayManagerInternal.DisplayPowerRequest(displayPowerRequest);
            } else {
                if (!displayPowerRequest2.equals(displayPowerRequest)) {
                    this.mPendingRequestLocked.copyFrom(displayPowerRequest);
                }
                if (z2) {
                    this.mDisplayReadyLocked = false;
                    if (!this.mPendingRequestChangedLocked) {
                        this.mPendingRequestChangedLocked = true;
                        sendUpdatePowerStateLocked();
                    }
                }
                return this.mDisplayReadyLocked;
            }
            z2 = true;
            if (z2) {
            }
            return this.mDisplayReadyLocked;
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public BrightnessConfiguration getDefaultBrightnessConfiguration() {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController == null) {
            return null;
        }
        return automaticBrightnessController.getDefaultConfig();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void onDisplayChanged(final HighBrightnessModeMetadata highBrightnessModeMetadata, int i) {
        this.mLeadDisplayId = i;
        final DisplayDevice primaryDisplayDeviceLocked = this.mLogicalDisplay.getPrimaryDisplayDeviceLocked();
        if (primaryDisplayDeviceLocked == null) {
            String str = this.mTag;
            Slog.wtf(str, "Display Device is null in DisplayPowerController for display: " + this.mLogicalDisplay.getDisplayIdLocked());
            return;
        }
        final String uniqueId = primaryDisplayDeviceLocked.getUniqueId();
        final DisplayDeviceConfig displayDeviceConfig = primaryDisplayDeviceLocked.getDisplayDeviceConfig();
        final IBinder displayTokenLocked = primaryDisplayDeviceLocked.getDisplayTokenLocked();
        final DisplayDeviceInfo displayDeviceInfoLocked = primaryDisplayDeviceLocked.getDisplayDeviceInfoLocked();
        final boolean isEnabledLocked = this.mLogicalDisplay.isEnabledLocked();
        final boolean isInTransitionLocked = this.mLogicalDisplay.isInTransitionLocked();
        final String brightnessThrottlingDataIdLocked = this.mLogicalDisplay.getBrightnessThrottlingDataIdLocked();
        this.mHandler.postAtTime(new Runnable() { // from class: com.android.server.display.DisplayPowerController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController.this.lambda$onDisplayChanged$1(primaryDisplayDeviceLocked, uniqueId, displayDeviceConfig, brightnessThrottlingDataIdLocked, displayTokenLocked, displayDeviceInfoLocked, highBrightnessModeMetadata, isEnabledLocked, isInTransitionLocked);
            }
        }, this.mClock.uptimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0048  */
    /* JADX WARN: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public /* synthetic */ void lambda$onDisplayChanged$1(DisplayDevice displayDevice, String str, DisplayDeviceConfig displayDeviceConfig, String str2, IBinder iBinder, DisplayDeviceInfo displayDeviceInfo, HighBrightnessModeMetadata highBrightnessModeMetadata, boolean z, boolean z2) {
        boolean z3;
        boolean z4 = true;
        if (this.mDisplayDevice != displayDevice) {
            this.mDisplayDevice = displayDevice;
            this.mUniqueDisplayId = str;
            this.mDisplayStatsId = str.hashCode();
            this.mDisplayDeviceConfig = displayDeviceConfig;
            this.mBrightnessThrottlingDataId = str2;
            loadFromDisplayDeviceConfig(iBinder, displayDeviceInfo, highBrightnessModeMetadata);
            loadNitBasedBrightnessSetting();
            this.mPowerState.resetScreenState();
        } else if (!this.mBrightnessThrottlingDataId.equals(str2)) {
            this.mBrightnessThrottlingDataId = str2;
            this.mBrightnessThrottler.resetThrottlingData(displayDeviceConfig.getBrightnessThrottlingData(str2), this.mUniqueDisplayId);
        } else {
            z3 = false;
            if (this.mIsEnabled == z || this.mIsInTransition != z2) {
                this.mIsEnabled = z;
                this.mIsInTransition = z2;
            } else {
                z4 = z3;
            }
            if (z4) {
                return;
            }
            updatePowerState();
            return;
        }
        z3 = true;
        if (this.mIsEnabled == z) {
        }
        this.mIsEnabled = z;
        this.mIsInTransition = z2;
        if (z4) {
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void stop() {
        synchronized (this.mLock) {
            this.mStopped = true;
            this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(9), this.mClock.uptimeMillis());
            DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
            if (displayWhiteBalanceController != null) {
                displayWhiteBalanceController.setEnabled(false);
            }
            AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
            if (automaticBrightnessController != null) {
                automaticBrightnessController.stop();
            }
            BrightnessSetting brightnessSetting = this.mBrightnessSetting;
            if (brightnessSetting != null) {
                brightnessSetting.unregisterListener(this.mBrightnessSettingListener);
            }
            this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        }
    }

    public final void loadFromDisplayDeviceConfig(IBinder iBinder, DisplayDeviceInfo displayDeviceInfo, HighBrightnessModeMetadata highBrightnessModeMetadata) {
        loadBrightnessRampRates();
        loadProximitySensor();
        loadNitsRange(this.mContext.getResources());
        setUpAutoBrightness(this.mContext.getResources(), this.mHandler);
        reloadReduceBrightColours();
        RampAnimator.DualRampAnimator<DisplayPowerState> dualRampAnimator = this.mScreenBrightnessRampAnimator;
        if (dualRampAnimator != null) {
            dualRampAnimator.setAnimationTimeLimits(this.mBrightnessRampIncreaseMaxTimeMillis, this.mBrightnessRampDecreaseMaxTimeMillis);
        }
        this.mHbmController.setHighBrightnessModeMetadata(highBrightnessModeMetadata);
        this.mHbmController.resetHbmData(displayDeviceInfo.width, displayDeviceInfo.height, iBinder, displayDeviceInfo.uniqueId, this.mDisplayDeviceConfig.getHighBrightnessModeData(), new HighBrightnessModeController.HdrBrightnessDeviceConfig() { // from class: com.android.server.display.DisplayPowerController.2
            @Override // com.android.server.display.HighBrightnessModeController.HdrBrightnessDeviceConfig
            public float getHdrBrightnessFromSdr(float f) {
                return DisplayPowerController.this.mDisplayDeviceConfig.getHdrBrightnessFromSdr(f);
            }
        });
        this.mBrightnessThrottler.resetThrottlingData(this.mDisplayDeviceConfig.getBrightnessThrottlingData(this.mBrightnessThrottlingDataId), this.mUniqueDisplayId);
    }

    public final void sendUpdatePowerState() {
        synchronized (this.mLock) {
            sendUpdatePowerStateLocked();
        }
    }

    @GuardedBy({"mLock"})
    public final void sendUpdatePowerStateLocked() {
        if (this.mStopped || this.mPendingUpdatePowerStateLocked) {
            return;
        }
        this.mPendingUpdatePowerStateLocked = true;
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(1), this.mClock.uptimeMillis());
    }

    public final void initialize(int i) {
        DisplayPowerState displayPowerState = this.mInjector.getDisplayPowerState(this.mBlanker, this.mColorFadeEnabled ? new ColorFade(this.mDisplayId) : null, this.mDisplayId, i);
        this.mPowerState = displayPowerState;
        if (this.mColorFadeEnabled) {
            FloatProperty<DisplayPowerState> floatProperty = DisplayPowerState.COLOR_FADE_LEVEL;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(displayPowerState, floatProperty, 0.0f, 1.0f);
            this.mColorFadeOnAnimator = ofFloat;
            ofFloat.setDuration(250L);
            this.mColorFadeOnAnimator.addListener(this.mAnimatorListener);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.mPowerState, floatProperty, 1.0f, 0.0f);
            this.mColorFadeOffAnimator = ofFloat2;
            ofFloat2.setDuration(400L);
            this.mColorFadeOffAnimator.addListener(this.mAnimatorListener);
        }
        RampAnimator.DualRampAnimator<DisplayPowerState> dualRampAnimator = this.mInjector.getDualRampAnimator(this.mPowerState, DisplayPowerState.SCREEN_BRIGHTNESS_FLOAT, DisplayPowerState.SCREEN_SDR_BRIGHTNESS_FLOAT);
        this.mScreenBrightnessRampAnimator = dualRampAnimator;
        dualRampAnimator.setAnimationTimeLimits(this.mBrightnessRampIncreaseMaxTimeMillis, this.mBrightnessRampDecreaseMaxTimeMillis);
        this.mScreenBrightnessRampAnimator.setListener(this.mRampAnimatorListener);
        noteScreenState(this.mPowerState.getScreenState());
        noteScreenBrightness(this.mPowerState.getScreenBrightness());
        float convertToNits = convertToNits(this.mPowerState.getScreenBrightness());
        BrightnessTracker brightnessTracker = this.mBrightnessTracker;
        if (brightnessTracker != null && convertToNits >= 0.0f) {
            brightnessTracker.start(convertToNits);
        }
        BrightnessSetting.BrightnessSettingListener brightnessSettingListener = new BrightnessSetting.BrightnessSettingListener() { // from class: com.android.server.display.DisplayPowerController$$ExternalSyntheticLambda7
            @Override // com.android.server.display.BrightnessSetting.BrightnessSettingListener
            public final void onBrightnessChanged(float f) {
                DisplayPowerController.this.lambda$initialize$2(f);
            }
        };
        this.mBrightnessSettingListener = brightnessSettingListener;
        this.mBrightnessSetting.registerListener(brightnessSettingListener);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_auto_brightness_adj"), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, this.mSettingsObserver, -1);
        handleBrightnessModeChange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initialize$2(float f) {
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(10, Float.valueOf(f)), this.mClock.uptimeMillis());
    }

    public final void setUpAutoBrightness(Resources resources, Handler handler) {
        float f;
        float f2;
        int i;
        boolean isAutoBrightnessAvailable = this.mDisplayDeviceConfig.isAutoBrightnessAvailable();
        this.mUseSoftwareAutoBrightnessConfig = isAutoBrightnessAvailable;
        if (isAutoBrightnessAvailable) {
            BrightnessMappingStrategy brightnessMappingStrategy = this.mInteractiveModeBrightnessMapper;
            if (brightnessMappingStrategy != null) {
                f = brightnessMappingStrategy.getUserLux();
                f2 = this.mInteractiveModeBrightnessMapper.getUserBrightness();
            } else {
                f = -1.0f;
                f2 = -1.0f;
            }
            boolean z = resources.getBoolean(17891655);
            this.mInteractiveModeBrightnessMapper = this.mInjector.getInteractiveModeBrightnessMapper(resources, this.mDisplayDeviceConfig, this.mDisplayWhiteBalanceController);
            if (z) {
                this.mIdleModeBrightnessMapper = BrightnessMappingStrategy.createForIdleMode(resources, this.mDisplayDeviceConfig, this.mDisplayWhiteBalanceController);
            }
            if (this.mInteractiveModeBrightnessMapper != null) {
                float fraction = resources.getFraction(18022406, 1, 1);
                HysteresisLevels hysteresisLevels = this.mInjector.getHysteresisLevels(this.mDisplayDeviceConfig.getAmbientBrighteningPercentages(), this.mDisplayDeviceConfig.getAmbientDarkeningPercentages(), this.mDisplayDeviceConfig.getAmbientBrighteningLevels(), this.mDisplayDeviceConfig.getAmbientDarkeningLevels(), this.mDisplayDeviceConfig.getAmbientLuxDarkeningMinThreshold(), this.mDisplayDeviceConfig.getAmbientLuxBrighteningMinThreshold());
                HysteresisLevels hysteresisLevels2 = this.mInjector.getHysteresisLevels(this.mDisplayDeviceConfig.getScreenBrighteningPercentages(), this.mDisplayDeviceConfig.getScreenDarkeningPercentages(), this.mDisplayDeviceConfig.getScreenBrighteningLevels(), this.mDisplayDeviceConfig.getScreenDarkeningLevels(), this.mDisplayDeviceConfig.getScreenDarkeningMinThreshold(), this.mDisplayDeviceConfig.getScreenBrighteningMinThreshold(), true);
                HysteresisLevels hysteresisLevels3 = this.mInjector.getHysteresisLevels(this.mDisplayDeviceConfig.getAmbientBrighteningPercentagesIdle(), this.mDisplayDeviceConfig.getAmbientDarkeningPercentagesIdle(), this.mDisplayDeviceConfig.getAmbientBrighteningLevelsIdle(), this.mDisplayDeviceConfig.getAmbientDarkeningLevelsIdle(), this.mDisplayDeviceConfig.getAmbientLuxDarkeningMinThresholdIdle(), this.mDisplayDeviceConfig.getAmbientLuxBrighteningMinThresholdIdle());
                HysteresisLevels hysteresisLevels4 = this.mInjector.getHysteresisLevels(this.mDisplayDeviceConfig.getScreenBrighteningPercentagesIdle(), this.mDisplayDeviceConfig.getScreenDarkeningPercentagesIdle(), this.mDisplayDeviceConfig.getScreenBrighteningLevelsIdle(), this.mDisplayDeviceConfig.getScreenDarkeningLevelsIdle(), this.mDisplayDeviceConfig.getScreenDarkeningMinThresholdIdle(), this.mDisplayDeviceConfig.getScreenBrighteningMinThresholdIdle());
                long autoBrightnessBrighteningLightDebounce = this.mDisplayDeviceConfig.getAutoBrightnessBrighteningLightDebounce();
                long autoBrightnessDarkeningLightDebounce = this.mDisplayDeviceConfig.getAutoBrightnessDarkeningLightDebounce();
                boolean z2 = resources.getBoolean(17891373);
                int integer = resources.getInteger(17694865);
                int integer2 = resources.getInteger(17694747);
                int integer3 = resources.getInteger(17694746);
                if (integer3 == -1) {
                    i = integer2;
                } else {
                    if (integer3 > integer2) {
                        Slog.w(this.mTag, "Expected config_autoBrightnessInitialLightSensorRate (" + integer3 + ") to be less than or equal to config_autoBrightnessLightSensorRate (" + integer2 + ").");
                    }
                    i = integer3;
                }
                loadAmbientLightSensor();
                BrightnessTracker brightnessTracker = this.mBrightnessTracker;
                if (brightnessTracker != null && this.mDisplayId == 0) {
                    brightnessTracker.setLightSensor(this.mLightSensor);
                }
                AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
                if (automaticBrightnessController != null) {
                    automaticBrightnessController.stop();
                }
                this.mAutomaticBrightnessController = this.mInjector.getAutomaticBrightnessController(this, handler.getLooper(), this.mSensorManager, this.mLightSensor, this.mInteractiveModeBrightnessMapper, integer, 0.0f, 1.0f, fraction, integer2, i, autoBrightnessBrighteningLightDebounce, autoBrightnessDarkeningLightDebounce, z2, hysteresisLevels, hysteresisLevels2, hysteresisLevels3, hysteresisLevels4, this.mContext, this.mHbmController, this.mBrightnessThrottler, this.mIdleModeBrightnessMapper, this.mDisplayDeviceConfig.getAmbientHorizonShort(), this.mDisplayDeviceConfig.getAmbientHorizonLong(), f, f2);
                this.mBrightnessEventRingBuffer = new RingBuffer<>(BrightnessEvent.class, 100);
                ScreenOffBrightnessSensorController screenOffBrightnessSensorController = this.mScreenOffBrightnessSensorController;
                if (screenOffBrightnessSensorController != null) {
                    screenOffBrightnessSensorController.stop();
                    this.mScreenOffBrightnessSensorController = null;
                }
                loadScreenOffBrightnessSensor();
                int[] screenOffBrightnessSensorValueToLux = this.mDisplayDeviceConfig.getScreenOffBrightnessSensorValueToLux();
                Sensor sensor = this.mScreenOffBrightnessSensor;
                if (sensor == null || screenOffBrightnessSensorValueToLux == null) {
                    return;
                }
                this.mScreenOffBrightnessSensorController = this.mInjector.getScreenOffBrightnessSensorController(this.mSensorManager, sensor, this.mHandler, new DisplayPowerController$$ExternalSyntheticLambda6(), screenOffBrightnessSensorValueToLux, this.mInteractiveModeBrightnessMapper);
                return;
            }
            this.mUseSoftwareAutoBrightnessConfig = false;
        }
    }

    public final void loadBrightnessRampRates() {
        this.mBrightnessRampRateFastDecrease = this.mDisplayDeviceConfig.getBrightnessRampFastDecrease();
        this.mBrightnessRampRateFastIncrease = this.mDisplayDeviceConfig.getBrightnessRampFastIncrease();
        this.mBrightnessRampRateSlowDecrease = this.mDisplayDeviceConfig.getBrightnessRampSlowDecrease();
        this.mBrightnessRampRateSlowIncrease = this.mDisplayDeviceConfig.getBrightnessRampSlowIncrease();
        this.mBrightnessRampDecreaseMaxTimeMillis = this.mDisplayDeviceConfig.getBrightnessRampDecreaseMaxMillis();
        this.mBrightnessRampIncreaseMaxTimeMillis = this.mDisplayDeviceConfig.getBrightnessRampIncreaseMaxMillis();
    }

    public final void loadNitsRange(Resources resources) {
        DisplayDeviceConfig displayDeviceConfig = this.mDisplayDeviceConfig;
        if (displayDeviceConfig != null && displayDeviceConfig.getNits() != null) {
            this.mNitsRange = this.mDisplayDeviceConfig.getNits();
            return;
        }
        Slog.w(this.mTag, "Screen brightness nits configuration is unavailable; falling back");
        this.mNitsRange = BrightnessMappingStrategy.getFloatArray(resources.obtainTypedArray(17236132));
    }

    public final void reloadReduceBrightColours() {
        ColorDisplayService.ColorDisplayServiceInternal colorDisplayServiceInternal = this.mCdsi;
        if (colorDisplayServiceInternal == null || !colorDisplayServiceInternal.isReduceBrightColorsActivated()) {
            return;
        }
        applyReduceBrightColorsSplineAdjustment();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setAutomaticScreenBrightnessMode(boolean z) {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController != null) {
            if (z) {
                automaticBrightnessController.switchToIdleMode();
            } else {
                automaticBrightnessController.switchToInteractiveScreenBrightnessMode();
            }
        }
        DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
        if (displayWhiteBalanceController != null) {
            displayWhiteBalanceController.setStrongModeEnabled(z);
        }
    }

    public final void cleanupHandlerThreadAfterStop() {
        setProximitySensorEnabled(false);
        this.mHbmController.stop();
        this.mBrightnessThrottler.stop();
        this.mHandler.removeCallbacksAndMessages(null);
        if (this.mUnfinishedBusiness) {
            this.mCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdUnfinishedBusiness);
            this.mUnfinishedBusiness = false;
        }
        if (this.mOnStateChangedPending) {
            this.mCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdOnStateChanged);
            this.mOnStateChangedPending = false;
        }
        for (int i = 0; i < this.mOnProximityPositiveMessages; i++) {
            this.mCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdProxPositive);
        }
        this.mOnProximityPositiveMessages = 0;
        for (int i2 = 0; i2 < this.mOnProximityNegativeMessages; i2++) {
            this.mCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdProxNegative);
        }
        this.mOnProximityNegativeMessages = 0;
        DisplayPowerState displayPowerState = this.mPowerState;
        reportStats(displayPowerState != null ? displayPowerState.getScreenBrightness() : 0.0f);
        DisplayPowerState displayPowerState2 = this.mPowerState;
        if (displayPowerState2 != null) {
            displayPowerState2.stop();
            this.mPowerState = null;
        }
        ScreenOffBrightnessSensorController screenOffBrightnessSensorController = this.mScreenOffBrightnessSensorController;
        if (screenOffBrightnessSensorController != null) {
            screenOffBrightnessSensorController.stop();
        }
    }

    public final void updatePowerState() {
        Trace.traceBegin(131072L, "DisplayPowerController#updatePowerState");
        updatePowerStateInternal();
        Trace.traceEnd(131072L);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:136:0x018b  */
    /* JADX WARN: Removed duplicated region for block: B:137:0x018e  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x01a2  */
    /* JADX WARN: Removed duplicated region for block: B:143:0x01ac  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x01ba  */
    /* JADX WARN: Removed duplicated region for block: B:147:0x01c1  */
    /* JADX WARN: Removed duplicated region for block: B:162:0x01f4  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x021a  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x0221  */
    /* JADX WARN: Removed duplicated region for block: B:167:0x0223  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x022b  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x0241  */
    /* JADX WARN: Removed duplicated region for block: B:205:0x02a5  */
    /* JADX WARN: Removed duplicated region for block: B:218:0x02df  */
    /* JADX WARN: Removed duplicated region for block: B:223:0x02f5  */
    /* JADX WARN: Removed duplicated region for block: B:226:0x02fc  */
    /* JADX WARN: Removed duplicated region for block: B:232:0x0317  */
    /* JADX WARN: Removed duplicated region for block: B:236:0x033c  */
    /* JADX WARN: Removed duplicated region for block: B:242:0x034a  */
    /* JADX WARN: Removed duplicated region for block: B:243:0x034c  */
    /* JADX WARN: Removed duplicated region for block: B:247:0x0359 A[LOOP:0: B:245:0x0353->B:247:0x0359, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:249:0x036f  */
    /* JADX WARN: Removed duplicated region for block: B:252:0x0379  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x039d  */
    /* JADX WARN: Removed duplicated region for block: B:265:0x03ac  */
    /* JADX WARN: Removed duplicated region for block: B:273:0x03d1  */
    /* JADX WARN: Removed duplicated region for block: B:285:0x03f5  */
    /* JADX WARN: Removed duplicated region for block: B:372:0x04ed  */
    /* JADX WARN: Removed duplicated region for block: B:387:0x05ab  */
    /* JADX WARN: Removed duplicated region for block: B:388:0x05ae  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x05b9  */
    /* JADX WARN: Removed duplicated region for block: B:392:0x05be  */
    /* JADX WARN: Removed duplicated region for block: B:405:0x0623  */
    /* JADX WARN: Removed duplicated region for block: B:406:0x0626  */
    /* JADX WARN: Removed duplicated region for block: B:409:0x0637  */
    /* JADX WARN: Removed duplicated region for block: B:412:0x063e  */
    /* JADX WARN: Removed duplicated region for block: B:415:0x0645  */
    /* JADX WARN: Removed duplicated region for block: B:423:0x0666  */
    /* JADX WARN: Removed duplicated region for block: B:434:0x0689  */
    /* JADX WARN: Removed duplicated region for block: B:439:0x0696  */
    /* JADX WARN: Removed duplicated region for block: B:445:0x06aa  */
    /* JADX WARN: Removed duplicated region for block: B:449:0x06ba A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:463:0x06d3  */
    /* JADX WARN: Removed duplicated region for block: B:469:0x06e6  */
    /* JADX WARN: Removed duplicated region for block: B:472:0x06ef  */
    /* JADX WARN: Removed duplicated region for block: B:481:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:92:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x0117  */
    /* JADX WARN: Type inference failed for: r12v13 */
    /* JADX WARN: Type inference failed for: r12v2 */
    /* JADX WARN: Type inference failed for: r12v3 */
    /* JADX WARN: Type inference failed for: r14v19 */
    /* JADX WARN: Type inference failed for: r14v2 */
    /* JADX WARN: Type inference failed for: r14v3 */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v91 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r9v10 */
    /* JADX WARN: Type inference failed for: r9v11 */
    /* JADX WARN: Type inference failed for: r9v13 */
    /* JADX WARN: Type inference failed for: r9v15 */
    /* JADX WARN: Type inference failed for: r9v8 */
    /* JADX WARN: Type inference failed for: r9v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updatePowerStateInternal() {
        int i;
        ?? r3;
        boolean z;
        int i2;
        ?? r12;
        int screenState;
        boolean updateUserSetScreenBrightness;
        float f;
        int i3;
        AutomaticBrightnessController automaticBrightnessController;
        boolean z2;
        BrightnessTracker brightnessTracker;
        float f2;
        boolean z3;
        boolean z4;
        int i4;
        float f3;
        int i5;
        DisplayManagerInternal.DisplayPowerRequest displayPowerRequest;
        boolean saveBrightnessInfo;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        int i6;
        RingBuffer<BrightnessEvent> ringBuffer;
        int i7;
        boolean z9;
        float clampScreenBrightness;
        float hdrBrightnessValue;
        AutomaticBrightnessController automaticBrightnessController2;
        AutomaticBrightnessController automaticBrightnessController3;
        int i8;
        float f4;
        boolean z10;
        boolean z11;
        ScreenOffBrightnessSensorController screenOffBrightnessSensorController;
        float f5;
        int i9;
        this.mBrightnessReasonTemp.set(null);
        this.mTempBrightnessEvent.reset();
        synchronized (this.mLock) {
            if (this.mStopped) {
                return;
            }
            this.mPendingUpdatePowerStateLocked = false;
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest2 = this.mPendingRequestLocked;
            if (displayPowerRequest2 == null) {
                return;
            }
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest3 = this.mPowerRequest;
            if (displayPowerRequest3 == null) {
                this.mPowerRequest = new DisplayManagerInternal.DisplayPowerRequest(this.mPendingRequestLocked);
                updatePendingProximityRequestsLocked();
                this.mPendingRequestChangedLocked = false;
                i = 3;
                r3 = true;
            } else {
                if (this.mPendingRequestChangedLocked) {
                    i = displayPowerRequest3.policy;
                    displayPowerRequest3.copyFrom(displayPowerRequest2);
                    updatePendingProximityRequestsLocked();
                    this.mPendingRequestChangedLocked = false;
                    this.mDisplayReadyLocked = false;
                } else {
                    i = displayPowerRequest3.policy;
                }
                r3 = false;
            }
            ?? r4 = !this.mDisplayReadyLocked;
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest4 = this.mPowerRequest;
            int i10 = displayPowerRequest4.policy;
            float f6 = Float.NaN;
            if (i10 == 0) {
                z = true;
                i2 = 1;
            } else if (i10 != 1) {
                z = false;
                i2 = 2;
            } else {
                i2 = displayPowerRequest4.dozeScreenState;
                if (i2 == 0) {
                    i2 = 3;
                }
                if (!this.mAllowAutoBrightnessWhileDozingConfig) {
                    f6 = displayPowerRequest4.dozeScreenBrightness;
                    this.mBrightnessReasonTemp.setReason(2);
                }
                z = false;
            }
            ScreenOffBrightnessSensorController screenOffBrightnessSensorController2 = this.mScreenOffBrightnessSensorController;
            if (screenOffBrightnessSensorController2 != null) {
                screenOffBrightnessSensorController2.setLightSensorEnabled(this.mUseAutoBrightness && this.mIsEnabled && (i2 == 1 || (i2 == 3 && !this.mAllowAutoBrightnessWhileDozingConfig)) && this.mLeadDisplayId == -1);
            }
            if (this.mProximitySensor != null) {
                if (this.mPowerRequest.useProximitySensor && i2 != 1) {
                    setProximitySensorEnabled(true);
                    if (!this.mScreenOffBecauseOfProximity && this.mProximity == 1 && !this.mIgnoreProximityUntilChanged) {
                        this.mScreenOffBecauseOfProximity = true;
                        sendOnProximityPositiveWithWakelock();
                    }
                } else if (this.mWaitingForNegativeProximity && this.mScreenOffBecauseOfProximity && this.mProximity == 1 && i2 != 1) {
                    setProximitySensorEnabled(true);
                } else {
                    setProximitySensorEnabled(false);
                    this.mWaitingForNegativeProximity = false;
                }
                if (this.mScreenOffBecauseOfProximity && (this.mProximity != 1 || this.mIgnoreProximityUntilChanged)) {
                    this.mScreenOffBecauseOfProximity = false;
                    sendOnProximityNegativeWithWakelock();
                    r12 = true;
                    i2 = (this.mIsEnabled || this.mIsInTransition || this.mScreenOffBecauseOfProximity) ? 1 : 1;
                    if (r3 != false) {
                        initialize(readyToUpdateDisplayState() ? i2 : 0);
                    }
                    this.mPowerState.getScreenState();
                    animateScreenStateChange(i2, z);
                    screenState = this.mPowerState.getScreenState();
                    if (screenState == 1) {
                        this.mBrightnessReasonTemp.setReason(5);
                        f6 = -1.0f;
                    }
                    if (Float.isNaN(f6) && isValidBrightnessValue(this.mBrightnessToFollow)) {
                        f6 = this.mBrightnessToFollow;
                        this.mBrightnessReasonTemp.setReason(10);
                    }
                    if (!Float.isNaN(f6) && isValidBrightnessValue(this.mPowerRequest.screenBrightnessOverride)) {
                        f6 = this.mPowerRequest.screenBrightnessOverride;
                        this.mBrightnessReasonTemp.setReason(6);
                        this.mAppliedScreenBrightnessOverride = true;
                    } else {
                        this.mAppliedScreenBrightnessOverride = false;
                    }
                    ?? r9 = !this.mAllowAutoBrightnessWhileDozingConfig && Display.isDozeState(screenState);
                    ?? r14 = !this.mUseAutoBrightness && (screenState == 2 || r9 == true) && Float.isNaN(f6) && this.mAutomaticBrightnessController != null && this.mBrightnessReasonTemp.getReason() != 10;
                    int i11 = r14 == false ? 1 : (!this.mUseAutoBrightness && screenState != 2 && r9 == false) != false ? 3 : 2;
                    updateUserSetScreenBrightness = updateUserSetScreenBrightness();
                    if (!isValidBrightnessValue(this.mTemporaryScreenBrightness)) {
                        f6 = this.mTemporaryScreenBrightness;
                        this.mAppliedTemporaryBrightness = true;
                        this.mBrightnessReasonTemp.setReason(7);
                    } else {
                        this.mAppliedTemporaryBrightness = false;
                    }
                    boolean updateAutoBrightnessAdjustment = updateAutoBrightnessAdjustment();
                    if (Float.isNaN(this.mTemporaryAutoBrightnessAdjustment)) {
                        f = this.mTemporaryAutoBrightnessAdjustment;
                        this.mAppliedTemporaryAutoBrightnessAdjustment = true;
                        i3 = 1;
                    } else {
                        f = this.mAutoBrightnessAdjustment;
                        this.mAppliedTemporaryAutoBrightnessAdjustment = false;
                        i3 = 2;
                    }
                    float f7 = f;
                    if (!this.mPowerRequest.boostScreenBrightness && f6 != -1.0f) {
                        this.mBrightnessReasonTemp.setReason(8);
                        this.mAppliedBrightnessBoost = true;
                        f6 = 1.0f;
                    } else {
                        this.mAppliedBrightnessBoost = false;
                    }
                    boolean z12 = !Float.isNaN(f6) && (updateAutoBrightnessAdjustment || updateUserSetScreenBrightness);
                    automaticBrightnessController = this.mAutomaticBrightnessController;
                    if (automaticBrightnessController == null) {
                        boolean hasUserDataPoints = automaticBrightnessController.hasUserDataPoints();
                        this.mAutomaticBrightnessController.configure(i11, this.mBrightnessConfiguration, this.mLastUserSetScreenBrightness, updateUserSetScreenBrightness, f7, updateAutoBrightnessAdjustment, this.mPowerRequest.policy, this.mShouldResetShortTermModel);
                        this.mShouldResetShortTermModel = false;
                        z2 = hasUserDataPoints;
                    } else {
                        z2 = false;
                    }
                    this.mHbmController.setAutoBrightnessEnabled(!this.mUseAutoBrightness ? 1 : 2);
                    brightnessTracker = this.mBrightnessTracker;
                    if (brightnessTracker != null) {
                        BrightnessConfiguration brightnessConfiguration = this.mBrightnessConfiguration;
                        brightnessTracker.setShouldCollectColorSample(brightnessConfiguration != null && brightnessConfiguration.shouldCollectColorSamples());
                    }
                    if (!Float.isNaN(f6)) {
                        if (r14 == true) {
                            float rawAutomaticScreenBrightness = this.mAutomaticBrightnessController.getRawAutomaticScreenBrightness();
                            float automaticScreenBrightness = this.mAutomaticBrightnessController.getAutomaticScreenBrightness(this.mTempBrightnessEvent);
                            f5 = this.mAutomaticBrightnessController.getAutomaticScreenBrightnessAdjustment();
                            f6 = automaticScreenBrightness;
                            f2 = rawAutomaticScreenBrightness;
                        } else {
                            f2 = f6;
                            f5 = f7;
                        }
                        if (isValidBrightnessValue(f6) || f6 == -1.0f) {
                            f6 = clampScreenBrightness(f6);
                            z3 = this.mAppliedAutoBrightness && !updateAutoBrightnessAdjustment;
                            z4 = this.mCurrentScreenBrightnessSetting != f6;
                            this.mAppliedAutoBrightness = true;
                            this.mBrightnessReasonTemp.setReason(4);
                            ScreenOffBrightnessSensorController screenOffBrightnessSensorController3 = this.mScreenOffBrightnessSensorController;
                            i9 = 0;
                            if (screenOffBrightnessSensorController3 != null) {
                                screenOffBrightnessSensorController3.setLightSensorEnabled(false);
                            }
                        } else {
                            this.mAppliedAutoBrightness = false;
                            z3 = false;
                            z4 = false;
                            i9 = 0;
                        }
                        if (f7 != f5) {
                            putAutoBrightnessAdjustmentSetting(f5);
                        } else {
                            i3 = i9;
                        }
                        i4 = i3;
                    } else {
                        float clampScreenBrightness2 = clampScreenBrightness(f6);
                        this.mAppliedAutoBrightness = false;
                        f2 = f6;
                        z3 = false;
                        z4 = false;
                        f6 = clampScreenBrightness2;
                        i4 = 0;
                    }
                    if (Float.isNaN(f6) && Display.isDozeState(screenState)) {
                        f2 = this.mScreenBrightnessDozeConfig;
                        f6 = clampScreenBrightness(f2);
                        this.mBrightnessReasonTemp.setReason(3);
                    }
                    if (Float.isNaN(f6) && r14 != false && (screenOffBrightnessSensorController = this.mScreenOffBrightnessSensorController) != null) {
                        f2 = screenOffBrightnessSensorController.getAutomaticScreenBrightness();
                        if (isValidBrightnessValue(f2)) {
                            f6 = f2;
                        } else {
                            f6 = clampScreenBrightness(f2);
                            boolean z13 = this.mCurrentScreenBrightnessSetting != f6;
                            this.mBrightnessReasonTemp.setReason(9);
                            z4 = z13;
                        }
                    }
                    if (Float.isNaN(f6)) {
                        f2 = this.mCurrentScreenBrightnessSetting;
                        f6 = clampScreenBrightness(f2);
                        if (f6 != this.mCurrentScreenBrightnessSetting) {
                            z4 = true;
                        }
                        this.mBrightnessReasonTemp.setReason(1);
                    }
                    if (!this.mBrightnessThrottler.isThrottled()) {
                        this.mTempBrightnessEvent.setThermalMax(this.mBrightnessThrottler.getBrightnessCap());
                        f3 = Math.min(f6, this.mBrightnessThrottler.getBrightnessCap());
                        this.mBrightnessReasonTemp.addModifier(8);
                        if (!this.mAppliedThrottling) {
                            z3 = false;
                        }
                        this.mAppliedThrottling = true;
                    } else {
                        if (this.mAppliedThrottling) {
                            this.mAppliedThrottling = false;
                        }
                        f3 = f6;
                    }
                    AutomaticBrightnessController automaticBrightnessController4 = this.mAutomaticBrightnessController;
                    float ambientLux = automaticBrightnessController4 != null ? 0.0f : automaticBrightnessController4.getAmbientLux();
                    boolean z14 = z3;
                    i5 = 0;
                    for (SparseArray<DisplayPowerControllerInterface> clone = this.mDisplayBrightnessFollowers.clone(); i5 < clone.size(); clone = clone) {
                        clone.valueAt(i5).setBrightnessToFollow(f2, convertToNits(f2), ambientLux);
                        i5++;
                    }
                    if (z4) {
                        updateScreenBrightnessSetting(f3);
                    }
                    if (this.mPowerRequest.policy != 2) {
                        if (f3 > 0.0f) {
                            float max = Math.max(Math.min(f3 - this.mScreenBrightnessMinimumDimAmount, this.mScreenBrightnessDimConfig), 0.0f);
                            z11 = true;
                            this.mBrightnessReasonTemp.addModifier(1);
                            f3 = max;
                        } else {
                            z11 = true;
                        }
                        if (!this.mAppliedDimming) {
                            z14 = false;
                        }
                        this.mAppliedDimming = z11;
                    } else if (this.mAppliedDimming) {
                        this.mAppliedDimming = false;
                        z14 = false;
                    }
                    displayPowerRequest = this.mPowerRequest;
                    if (!displayPowerRequest.lowPowerMode) {
                        if (f3 > 0.0f) {
                            float max2 = Math.max(f3 * Math.min(displayPowerRequest.screenLowPowerBrightnessFactor, 1.0f), 0.0f);
                            this.mBrightnessReasonTemp.addModifier(2);
                            f3 = max2;
                        }
                        if (this.mAppliedLowPower) {
                            z10 = true;
                        } else {
                            z10 = true;
                            z14 = false;
                        }
                        this.mAppliedLowPower = z10;
                    } else if (this.mAppliedLowPower) {
                        this.mAppliedLowPower = false;
                        z14 = false;
                    }
                    this.mHbmController.onBrightnessChanged(f3, f6, this.mBrightnessThrottler.getBrightnessMaxReason());
                    boolean z15 = !this.mAppliedTemporaryBrightness || this.mAppliedTemporaryAutoBrightnessAdjustment;
                    if (this.mPendingScreenOff) {
                        if (this.mSkipScreenOnBrightnessRamp) {
                            i7 = 2;
                            if (screenState == 2) {
                                int i12 = this.mSkipRampState;
                                if (i12 == 0 && this.mDozing) {
                                    this.mInitialAutoBrightness = f3;
                                    this.mSkipRampState = 1;
                                } else if (i12 == 1 && this.mUseSoftwareAutoBrightnessConfig && !BrightnessSynchronizer.floatEquals(f3, this.mInitialAutoBrightness)) {
                                    i7 = 2;
                                    this.mSkipRampState = 2;
                                } else {
                                    i7 = 2;
                                    if (this.mSkipRampState == 2) {
                                        this.mSkipRampState = 0;
                                    }
                                }
                            } else {
                                this.mSkipRampState = 0;
                            }
                            z9 = !(screenState == i7 || this.mSkipRampState == 0) || r12 == true;
                            boolean z16 = !Display.isDozeState(screenState) && this.mBrightnessBucketsInDozeConfig;
                            boolean z17 = !this.mColorFadeEnabled && this.mPowerState.getColorFadeLevel() == 1.0f;
                            clampScreenBrightness = clampScreenBrightness(f3);
                            hdrBrightnessValue = (this.mHbmController.getHighBrightnessMode() != 2 && (this.mBrightnessReason.getModifier() & 1) == 0 && (this.mBrightnessReason.getModifier() & 2) == 0) ? this.mHbmController.getHdrBrightnessValue() : clampScreenBrightness;
                            float screenBrightness = this.mPowerState.getScreenBrightness();
                            float sdrScreenBrightness = this.mPowerState.getSdrScreenBrightness();
                            if (isValidBrightnessValue(hdrBrightnessValue) && (hdrBrightnessValue != screenBrightness || clampScreenBrightness != sdrScreenBrightness)) {
                                if (!z9 || z16 || !z17 || z15) {
                                    animateScreenBrightness(hdrBrightnessValue, clampScreenBrightness, 0.0f);
                                } else {
                                    boolean z18 = i8 > 0;
                                    if (z18 && z14) {
                                        f4 = this.mBrightnessRampRateSlowIncrease;
                                    } else if (z18 && !z14) {
                                        f4 = this.mBrightnessRampRateFastIncrease;
                                    } else if (!z18 && z14) {
                                        f4 = this.mBrightnessRampRateSlowDecrease;
                                    } else {
                                        f4 = this.mBrightnessRampRateFastDecrease;
                                    }
                                    animateScreenBrightness(hdrBrightnessValue, clampScreenBrightness, f4);
                                }
                            }
                            if (!z15 && (automaticBrightnessController2 = this.mAutomaticBrightnessController) != null && !automaticBrightnessController2.isInIdleMode()) {
                                notifyBrightnessTrackerChanged(f3, (z12 || ((automaticBrightnessController3 = this.mAutomaticBrightnessController) != null && automaticBrightnessController3.hasValidAmbientLux())) ? z12 : false, z2);
                            }
                            saveBrightnessInfo = saveBrightnessInfo(getScreenBrightnessSetting(), hdrBrightnessValue);
                        }
                        i7 = 2;
                        if (screenState == i7) {
                        }
                        if (Display.isDozeState(screenState)) {
                        }
                        if (this.mColorFadeEnabled) {
                        }
                        clampScreenBrightness = clampScreenBrightness(f3);
                        if (this.mHbmController.getHighBrightnessMode() != 2) {
                        }
                        float screenBrightness2 = this.mPowerState.getScreenBrightness();
                        float sdrScreenBrightness2 = this.mPowerState.getSdrScreenBrightness();
                        if (isValidBrightnessValue(hdrBrightnessValue)) {
                            if (!z9) {
                            }
                            animateScreenBrightness(hdrBrightnessValue, clampScreenBrightness, 0.0f);
                        }
                        if (!z15) {
                            notifyBrightnessTrackerChanged(f3, (z12 || ((automaticBrightnessController3 = this.mAutomaticBrightnessController) != null && automaticBrightnessController3.hasValidAmbientLux())) ? z12 : false, z2);
                        }
                        saveBrightnessInfo = saveBrightnessInfo(getScreenBrightnessSetting(), hdrBrightnessValue);
                    } else {
                        saveBrightnessInfo = saveBrightnessInfo(getScreenBrightnessSetting());
                    }
                    if (saveBrightnessInfo && !z15) {
                        postBrightnessChangeRunnable();
                    }
                    if (this.mBrightnessReasonTemp.equals(this.mBrightnessReason) || i4 != 0) {
                        Slog.v(this.mTag, "Brightness [" + f3 + "] reason changing to: '" + this.mBrightnessReasonTemp.toString(i4) + "', previous reason: '" + this.mBrightnessReason + "'.");
                        this.mBrightnessReason.set(this.mBrightnessReasonTemp);
                    } else if (this.mBrightnessReasonTemp.getReason() == 1 && updateUserSetScreenBrightness) {
                        Slog.v(this.mTag, "Brightness [" + f3 + "] manual adjustment.");
                    }
                    this.mTempBrightnessEvent.setTime(System.currentTimeMillis());
                    this.mTempBrightnessEvent.setBrightness(f3);
                    this.mTempBrightnessEvent.setPhysicalDisplayId(this.mUniqueDisplayId);
                    this.mTempBrightnessEvent.setReason(this.mBrightnessReason);
                    this.mTempBrightnessEvent.setHbmMax(this.mHbmController.getCurrentBrightnessMax());
                    this.mTempBrightnessEvent.setHbmMode(this.mHbmController.getHighBrightnessMode());
                    BrightnessEvent brightnessEvent = this.mTempBrightnessEvent;
                    brightnessEvent.setFlags(brightnessEvent.getFlags() | (this.mIsRbcActive ? 1 : 0) | (!this.mPowerRequest.lowPowerMode ? 32 : 0));
                    BrightnessEvent brightnessEvent2 = this.mTempBrightnessEvent;
                    ColorDisplayService.ColorDisplayServiceInternal colorDisplayServiceInternal = this.mCdsi;
                    brightnessEvent2.setRbcStrength(colorDisplayServiceInternal == null ? colorDisplayServiceInternal.getReduceBrightColorsStrength() : -1);
                    this.mTempBrightnessEvent.setPowerFactor(this.mPowerRequest.screenLowPowerBrightnessFactor);
                    this.mTempBrightnessEvent.setWasShortTermModelActive(z2);
                    boolean z19 = this.mTempBrightnessEvent.getReason().getReason() != 7 && this.mLastBrightnessEvent.getReason().getReason() == 7;
                    if ((!this.mTempBrightnessEvent.equalsMainData(this.mLastBrightnessEvent) && !z19) || i4 != 0) {
                        this.mTempBrightnessEvent.setInitialBrightness(this.mLastBrightnessEvent.getBrightness());
                        this.mTempBrightnessEvent.setAutomaticBrightnessEnabled(this.mUseAutoBrightness);
                        this.mLastBrightnessEvent.copyFrom(this.mTempBrightnessEvent);
                        BrightnessEvent brightnessEvent3 = new BrightnessEvent(this.mTempBrightnessEvent);
                        brightnessEvent3.setAdjustmentFlags(i4);
                        brightnessEvent3.setFlags(brightnessEvent3.getFlags() | (updateUserSetScreenBrightness ? 8 : 0));
                        Slog.i(this.mTag, brightnessEvent3.toString(false));
                        if (updateUserSetScreenBrightness) {
                            logManualBrightnessEvent(brightnessEvent3);
                        }
                        ringBuffer = this.mBrightnessEventRingBuffer;
                        if (ringBuffer != null) {
                            ringBuffer.append(brightnessEvent3);
                        }
                    }
                    if (this.mDisplayWhiteBalanceController != null) {
                        if (screenState == 2 && this.mDisplayWhiteBalanceSettings.isEnabled()) {
                            this.mDisplayWhiteBalanceController.setEnabled(true);
                            this.mDisplayWhiteBalanceController.updateDisplayColorTemperature();
                        } else {
                            this.mDisplayWhiteBalanceController.setEnabled(false);
                        }
                    }
                    z5 = this.mPendingScreenOnUnblocker != null && !(this.mColorFadeEnabled && (this.mColorFadeOnAnimator.isStarted() || this.mColorFadeOffAnimator.isStarted())) && this.mPowerState.waitUntilClean(this.mCleanListener);
                    z6 = (z5 || this.mScreenBrightnessRampAnimator.isAnimating()) ? false : true;
                    if (z5 && screenState != 1 && this.mReportedScreenStateToPolicy == 1) {
                        setReportedScreenState(2);
                        this.mWindowManagerPolicy.screenTurnedOn(this.mDisplayId);
                    }
                    if (!z6 && !this.mUnfinishedBusiness) {
                        this.mCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdUnfinishedBusiness);
                        this.mUnfinishedBusiness = true;
                    }
                    if (z5 || !r4 == true) {
                        z7 = true;
                    } else {
                        synchronized (this.mLock) {
                            if (this.mPendingRequestChangedLocked) {
                                z7 = true;
                            } else {
                                z7 = true;
                                this.mDisplayReadyLocked = true;
                            }
                        }
                        sendOnStateChangedWithWakelock();
                    }
                    if (z6 || !this.mUnfinishedBusiness) {
                        z8 = false;
                    } else {
                        z8 = false;
                        this.mUnfinishedBusiness = false;
                        this.mCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdUnfinishedBusiness);
                    }
                    if (screenState != 2) {
                        z8 = z7;
                    }
                    this.mDozing = z8;
                    i6 = this.mPowerRequest.policy;
                    if (i != i6) {
                        logDisplayPolicyChanged(i6);
                        return;
                    }
                    return;
                }
            } else {
                this.mWaitingForNegativeProximity = false;
                this.mIgnoreProximityUntilChanged = false;
            }
            r12 = false;
            if (this.mIsEnabled) {
            }
            if (r3 != false) {
            }
            this.mPowerState.getScreenState();
            animateScreenStateChange(i2, z);
            screenState = this.mPowerState.getScreenState();
            if (screenState == 1) {
            }
            if (Float.isNaN(f6)) {
                f6 = this.mBrightnessToFollow;
                this.mBrightnessReasonTemp.setReason(10);
            }
            if (!Float.isNaN(f6)) {
            }
            this.mAppliedScreenBrightnessOverride = false;
            if (this.mAllowAutoBrightnessWhileDozingConfig) {
            }
            if (this.mUseAutoBrightness) {
            }
            if (r14 == false) {
            }
            updateUserSetScreenBrightness = updateUserSetScreenBrightness();
            if (!isValidBrightnessValue(this.mTemporaryScreenBrightness)) {
            }
            boolean updateAutoBrightnessAdjustment2 = updateAutoBrightnessAdjustment();
            if (Float.isNaN(this.mTemporaryAutoBrightnessAdjustment)) {
            }
            float f72 = f;
            if (!this.mPowerRequest.boostScreenBrightness) {
            }
            this.mAppliedBrightnessBoost = false;
            if (Float.isNaN(f6)) {
            }
            automaticBrightnessController = this.mAutomaticBrightnessController;
            if (automaticBrightnessController == null) {
            }
            this.mHbmController.setAutoBrightnessEnabled(!this.mUseAutoBrightness ? 1 : 2);
            brightnessTracker = this.mBrightnessTracker;
            if (brightnessTracker != null) {
            }
            if (!Float.isNaN(f6)) {
            }
            if (Float.isNaN(f6)) {
                f2 = this.mScreenBrightnessDozeConfig;
                f6 = clampScreenBrightness(f2);
                this.mBrightnessReasonTemp.setReason(3);
            }
            if (Float.isNaN(f6)) {
                f2 = screenOffBrightnessSensorController.getAutomaticScreenBrightness();
                if (isValidBrightnessValue(f2)) {
                }
            }
            if (Float.isNaN(f6)) {
            }
            if (!this.mBrightnessThrottler.isThrottled()) {
            }
            AutomaticBrightnessController automaticBrightnessController42 = this.mAutomaticBrightnessController;
            if (automaticBrightnessController42 != null) {
            }
            boolean z142 = z3;
            i5 = 0;
            while (i5 < clone.size()) {
            }
            if (z4) {
            }
            if (this.mPowerRequest.policy != 2) {
            }
            displayPowerRequest = this.mPowerRequest;
            if (!displayPowerRequest.lowPowerMode) {
            }
            this.mHbmController.onBrightnessChanged(f3, f6, this.mBrightnessThrottler.getBrightnessMaxReason());
            if (this.mAppliedTemporaryBrightness) {
            }
            if (this.mPendingScreenOff) {
            }
            if (saveBrightnessInfo) {
                postBrightnessChangeRunnable();
            }
            if (this.mBrightnessReasonTemp.equals(this.mBrightnessReason)) {
            }
            Slog.v(this.mTag, "Brightness [" + f3 + "] reason changing to: '" + this.mBrightnessReasonTemp.toString(i4) + "', previous reason: '" + this.mBrightnessReason + "'.");
            this.mBrightnessReason.set(this.mBrightnessReasonTemp);
            this.mTempBrightnessEvent.setTime(System.currentTimeMillis());
            this.mTempBrightnessEvent.setBrightness(f3);
            this.mTempBrightnessEvent.setPhysicalDisplayId(this.mUniqueDisplayId);
            this.mTempBrightnessEvent.setReason(this.mBrightnessReason);
            this.mTempBrightnessEvent.setHbmMax(this.mHbmController.getCurrentBrightnessMax());
            this.mTempBrightnessEvent.setHbmMode(this.mHbmController.getHighBrightnessMode());
            BrightnessEvent brightnessEvent4 = this.mTempBrightnessEvent;
            brightnessEvent4.setFlags(brightnessEvent4.getFlags() | (this.mIsRbcActive ? 1 : 0) | (!this.mPowerRequest.lowPowerMode ? 32 : 0));
            BrightnessEvent brightnessEvent22 = this.mTempBrightnessEvent;
            ColorDisplayService.ColorDisplayServiceInternal colorDisplayServiceInternal2 = this.mCdsi;
            brightnessEvent22.setRbcStrength(colorDisplayServiceInternal2 == null ? colorDisplayServiceInternal2.getReduceBrightColorsStrength() : -1);
            this.mTempBrightnessEvent.setPowerFactor(this.mPowerRequest.screenLowPowerBrightnessFactor);
            this.mTempBrightnessEvent.setWasShortTermModelActive(z2);
            if (this.mTempBrightnessEvent.getReason().getReason() != 7) {
            }
            if (!this.mTempBrightnessEvent.equalsMainData(this.mLastBrightnessEvent)) {
                this.mTempBrightnessEvent.setInitialBrightness(this.mLastBrightnessEvent.getBrightness());
                this.mTempBrightnessEvent.setAutomaticBrightnessEnabled(this.mUseAutoBrightness);
                this.mLastBrightnessEvent.copyFrom(this.mTempBrightnessEvent);
                BrightnessEvent brightnessEvent32 = new BrightnessEvent(this.mTempBrightnessEvent);
                brightnessEvent32.setAdjustmentFlags(i4);
                brightnessEvent32.setFlags(brightnessEvent32.getFlags() | (updateUserSetScreenBrightness ? 8 : 0));
                Slog.i(this.mTag, brightnessEvent32.toString(false));
                if (updateUserSetScreenBrightness) {
                }
                ringBuffer = this.mBrightnessEventRingBuffer;
                if (ringBuffer != null) {
                }
                if (this.mDisplayWhiteBalanceController != null) {
                }
                if (this.mPendingScreenOnUnblocker != null) {
                }
                if (z5) {
                }
                if (z5) {
                    setReportedScreenState(2);
                    this.mWindowManagerPolicy.screenTurnedOn(this.mDisplayId);
                }
                if (!z6) {
                    this.mCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdUnfinishedBusiness);
                    this.mUnfinishedBusiness = true;
                }
                if (z5) {
                }
                z7 = true;
                if (z6) {
                }
                z8 = false;
                if (screenState != 2) {
                }
                this.mDozing = z8;
                i6 = this.mPowerRequest.policy;
                if (i != i6) {
                }
            }
            this.mTempBrightnessEvent.setInitialBrightness(this.mLastBrightnessEvent.getBrightness());
            this.mTempBrightnessEvent.setAutomaticBrightnessEnabled(this.mUseAutoBrightness);
            this.mLastBrightnessEvent.copyFrom(this.mTempBrightnessEvent);
            BrightnessEvent brightnessEvent322 = new BrightnessEvent(this.mTempBrightnessEvent);
            brightnessEvent322.setAdjustmentFlags(i4);
            brightnessEvent322.setFlags(brightnessEvent322.getFlags() | (updateUserSetScreenBrightness ? 8 : 0));
            Slog.i(this.mTag, brightnessEvent322.toString(false));
            if (updateUserSetScreenBrightness) {
            }
            ringBuffer = this.mBrightnessEventRingBuffer;
            if (ringBuffer != null) {
            }
            if (this.mDisplayWhiteBalanceController != null) {
            }
            if (this.mPendingScreenOnUnblocker != null) {
            }
            if (z5) {
            }
            if (z5) {
            }
            if (!z6) {
            }
            if (z5) {
            }
            z7 = true;
            if (z6) {
            }
            z8 = false;
            if (screenState != 2) {
            }
            this.mDozing = z8;
            i6 = this.mPowerRequest.policy;
            if (i != i6) {
            }
        }
    }

    @Override // com.android.server.display.AutomaticBrightnessController.Callbacks
    public void updateBrightness() {
        sendUpdatePowerState();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void ignoreProximitySensorUntilChanged() {
        this.mHandler.sendEmptyMessage(8);
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setBrightnessConfiguration(BrightnessConfiguration brightnessConfiguration, boolean z) {
        this.mHandler.obtainMessage(5, z ? 1 : 0, 0, brightnessConfiguration).sendToTarget();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setTemporaryBrightness(float f) {
        this.mHandler.obtainMessage(6, Float.floatToIntBits(f), 0).sendToTarget();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setTemporaryAutoBrightnessAdjustment(float f) {
        this.mHandler.obtainMessage(7, Float.floatToIntBits(f), 0).sendToTarget();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public BrightnessInfo getBrightnessInfo() {
        BrightnessInfo brightnessInfo;
        synchronized (this.mCachedBrightnessInfo) {
            CachedBrightnessInfo cachedBrightnessInfo = this.mCachedBrightnessInfo;
            brightnessInfo = new BrightnessInfo(cachedBrightnessInfo.brightness.value, cachedBrightnessInfo.adjustedBrightness.value, cachedBrightnessInfo.brightnessMin.value, cachedBrightnessInfo.brightnessMax.value, cachedBrightnessInfo.hbmMode.value, cachedBrightnessInfo.hbmTransitionPoint.value, cachedBrightnessInfo.brightnessMaxReason.value);
        }
        return brightnessInfo;
    }

    public final boolean saveBrightnessInfo(float f) {
        return saveBrightnessInfo(f, f);
    }

    public final boolean saveBrightnessInfo(float f, float f2) {
        boolean checkAndSetInt;
        synchronized (this.mCachedBrightnessInfo) {
            float min = Math.min(this.mHbmController.getCurrentBrightnessMin(), this.mBrightnessThrottler.getBrightnessCap());
            float min2 = Math.min(this.mHbmController.getCurrentBrightnessMax(), this.mBrightnessThrottler.getBrightnessCap());
            CachedBrightnessInfo cachedBrightnessInfo = this.mCachedBrightnessInfo;
            CachedBrightnessInfo cachedBrightnessInfo2 = this.mCachedBrightnessInfo;
            boolean checkAndSetFloat = cachedBrightnessInfo.checkAndSetFloat(cachedBrightnessInfo.brightness, f) | false | cachedBrightnessInfo2.checkAndSetFloat(cachedBrightnessInfo2.adjustedBrightness, f2);
            CachedBrightnessInfo cachedBrightnessInfo3 = this.mCachedBrightnessInfo;
            boolean checkAndSetFloat2 = checkAndSetFloat | cachedBrightnessInfo3.checkAndSetFloat(cachedBrightnessInfo3.brightnessMin, min);
            CachedBrightnessInfo cachedBrightnessInfo4 = this.mCachedBrightnessInfo;
            boolean checkAndSetFloat3 = checkAndSetFloat2 | cachedBrightnessInfo4.checkAndSetFloat(cachedBrightnessInfo4.brightnessMax, min2);
            CachedBrightnessInfo cachedBrightnessInfo5 = this.mCachedBrightnessInfo;
            boolean checkAndSetInt2 = checkAndSetFloat3 | cachedBrightnessInfo5.checkAndSetInt(cachedBrightnessInfo5.hbmMode, this.mHbmController.getHighBrightnessMode());
            CachedBrightnessInfo cachedBrightnessInfo6 = this.mCachedBrightnessInfo;
            boolean checkAndSetFloat4 = checkAndSetInt2 | cachedBrightnessInfo6.checkAndSetFloat(cachedBrightnessInfo6.hbmTransitionPoint, this.mHbmController.getTransitionPoint());
            CachedBrightnessInfo cachedBrightnessInfo7 = this.mCachedBrightnessInfo;
            checkAndSetInt = cachedBrightnessInfo7.checkAndSetInt(cachedBrightnessInfo7.brightnessMaxReason, this.mBrightnessThrottler.getBrightnessMaxReason()) | checkAndSetFloat4;
        }
        return checkAndSetInt;
    }

    public void postBrightnessChangeRunnable() {
        this.mHandler.post(this.mOnBrightnessChangeRunnable);
    }

    public final HighBrightnessModeController createHbmControllerLocked() {
        DisplayDevice primaryDisplayDeviceLocked = this.mLogicalDisplay.getPrimaryDisplayDeviceLocked();
        DisplayDeviceConfig displayDeviceConfig = primaryDisplayDeviceLocked.getDisplayDeviceConfig();
        IBinder displayTokenLocked = this.mLogicalDisplay.getPrimaryDisplayDeviceLocked().getDisplayTokenLocked();
        String uniqueId = this.mLogicalDisplay.getPrimaryDisplayDeviceLocked().getUniqueId();
        DisplayDeviceConfig.HighBrightnessModeData highBrightnessModeData = displayDeviceConfig != null ? displayDeviceConfig.getHighBrightnessModeData() : null;
        DisplayDeviceInfo displayDeviceInfoLocked = primaryDisplayDeviceLocked.getDisplayDeviceInfoLocked();
        return new HighBrightnessModeController(this.mHandler, displayDeviceInfoLocked.width, displayDeviceInfoLocked.height, displayTokenLocked, uniqueId, 0.0f, 1.0f, highBrightnessModeData, new HighBrightnessModeController.HdrBrightnessDeviceConfig() { // from class: com.android.server.display.DisplayPowerController.5
            @Override // com.android.server.display.HighBrightnessModeController.HdrBrightnessDeviceConfig
            public float getHdrBrightnessFromSdr(float f) {
                return DisplayPowerController.this.mDisplayDeviceConfig.getHdrBrightnessFromSdr(f);
            }
        }, new Runnable() { // from class: com.android.server.display.DisplayPowerController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController.this.lambda$createHbmControllerLocked$3();
            }
        }, this.mHighBrightnessModeMetadata, this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createHbmControllerLocked$3() {
        sendUpdatePowerState();
        postBrightnessChangeRunnable();
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController != null) {
            automaticBrightnessController.update();
        }
    }

    public final BrightnessThrottler createBrightnessThrottlerLocked() {
        return new BrightnessThrottler(this.mHandler, this.mLogicalDisplay.getPrimaryDisplayDeviceLocked().getDisplayDeviceConfig().getBrightnessThrottlingData(this.mBrightnessThrottlingDataId), new Runnable() { // from class: com.android.server.display.DisplayPowerController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController.this.lambda$createBrightnessThrottlerLocked$4();
            }
        }, this.mUniqueDisplayId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createBrightnessThrottlerLocked$4() {
        sendUpdatePowerState();
        postBrightnessChangeRunnable();
    }

    public final void blockScreenOn() {
        if (this.mPendingScreenOnUnblocker == null) {
            Trace.asyncTraceBegin(131072L, "Screen on blocked", 0);
            this.mPendingScreenOnUnblocker = new ScreenOnUnblocker();
            this.mScreenOnBlockStartRealTime = SystemClock.elapsedRealtime();
            Slog.i(this.mTag, "Blocking screen on until initial contents have been drawn.");
        }
    }

    public final void unblockScreenOn() {
        if (this.mPendingScreenOnUnblocker != null) {
            this.mPendingScreenOnUnblocker = null;
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.mScreenOnBlockStartRealTime;
            String str = this.mTag;
            Slog.i(str, "Unblocked screen on after " + elapsedRealtime + " ms");
            Trace.asyncTraceEnd(131072L, "Screen on blocked", 0);
        }
    }

    public final void blockScreenOff() {
        if (this.mPendingScreenOffUnblocker == null) {
            Trace.asyncTraceBegin(131072L, "Screen off blocked", 0);
            this.mPendingScreenOffUnblocker = new ScreenOffUnblocker();
            this.mScreenOffBlockStartRealTime = SystemClock.elapsedRealtime();
            Slog.i(this.mTag, "Blocking screen off");
        }
    }

    public final void unblockScreenOff() {
        if (this.mPendingScreenOffUnblocker != null) {
            this.mPendingScreenOffUnblocker = null;
            long elapsedRealtime = SystemClock.elapsedRealtime() - this.mScreenOffBlockStartRealTime;
            String str = this.mTag;
            Slog.i(str, "Unblocked screen off after " + elapsedRealtime + " ms");
            Trace.asyncTraceEnd(131072L, "Screen off blocked", 0);
        }
    }

    public final boolean setScreenState(int i) {
        return setScreenState(i, false);
    }

    public final boolean setScreenState(int i, boolean z) {
        int i2;
        boolean z2 = i == 1;
        if (this.mPowerState.getScreenState() != i || this.mReportedScreenStateToPolicy == -1) {
            if (z2 && !this.mScreenOffBecauseOfProximity) {
                int i3 = this.mReportedScreenStateToPolicy;
                if (i3 == 2 || i3 == -1) {
                    setReportedScreenState(3);
                    blockScreenOff();
                    this.mWindowManagerPolicy.screenTurningOff(this.mDisplayId, this.mPendingScreenOffUnblocker);
                    unblockScreenOff();
                } else if (this.mPendingScreenOffUnblocker != null) {
                    return false;
                }
            }
            if (!z && this.mPowerState.getScreenState() != i && readyToUpdateDisplayState()) {
                Trace.traceCounter(131072L, "ScreenState", i);
                SystemProperties.set("debug.tracing.screen_state", String.valueOf(i));
                this.mPowerState.setScreenState(i);
                noteScreenState(i);
            }
        }
        if (z2 && this.mReportedScreenStateToPolicy != 0 && !this.mScreenOffBecauseOfProximity) {
            setReportedScreenState(0);
            unblockScreenOn();
            this.mWindowManagerPolicy.screenTurnedOff(this.mDisplayId);
        } else if (!z2 && this.mReportedScreenStateToPolicy == 3) {
            unblockScreenOff();
            this.mWindowManagerPolicy.screenTurnedOff(this.mDisplayId);
            setReportedScreenState(0);
        }
        if (!z2 && ((i2 = this.mReportedScreenStateToPolicy) == 0 || i2 == -1)) {
            setReportedScreenState(1);
            if (this.mPowerState.getColorFadeLevel() == 0.0f) {
                blockScreenOn();
            } else {
                unblockScreenOn();
            }
            this.mWindowManagerPolicy.screenTurningOn(this.mDisplayId, this.mPendingScreenOnUnblocker);
        }
        return this.mPendingScreenOnUnblocker == null;
    }

    public final void setReportedScreenState(int i) {
        Trace.traceCounter(131072L, "ReportedScreenStateToPolicy", i);
        this.mReportedScreenStateToPolicy = i;
    }

    public final void loadAmbientLightSensor() {
        DisplayDeviceConfig.SensorData ambientLightSensor = this.mDisplayDeviceConfig.getAmbientLightSensor();
        this.mLightSensor = SensorUtils.findSensor(this.mSensorManager, ambientLightSensor.type, ambientLightSensor.name, this.mDisplayId == 0 ? 5 : 0);
    }

    public final void loadScreenOffBrightnessSensor() {
        DisplayDeviceConfig.SensorData screenOffBrightnessSensor = this.mDisplayDeviceConfig.getScreenOffBrightnessSensor();
        this.mScreenOffBrightnessSensor = SensorUtils.findSensor(this.mSensorManager, screenOffBrightnessSensor.type, screenOffBrightnessSensor.name, 0);
    }

    public final void loadProximitySensor() {
        if (this.mDisplayId != 0) {
            return;
        }
        DisplayDeviceConfig.SensorData proximitySensor = this.mDisplayDeviceConfig.getProximitySensor();
        Sensor findSensor = SensorUtils.findSensor(this.mSensorManager, proximitySensor.type, proximitySensor.name, 8);
        this.mProximitySensor = findSensor;
        if (findSensor != null) {
            this.mProximityThreshold = Math.min(findSensor.getMaximumRange(), 5.0f);
        }
    }

    public final float clampScreenBrightness(float f) {
        if (Float.isNaN(f)) {
            f = 0.0f;
        }
        return MathUtils.constrain(f, this.mHbmController.getCurrentBrightnessMin(), this.mHbmController.getCurrentBrightnessMax());
    }

    public final void animateScreenBrightness(float f, float f2, float f3) {
        if (this.mScreenBrightnessRampAnimator.animateTo(f, f2, f3)) {
            Trace.traceCounter(131072L, "TargetScreenBrightness", (int) f);
            SystemProperties.set("debug.tracing.screen_brightness", String.valueOf(f));
            noteScreenBrightness(f);
        }
    }

    public final void animateScreenStateChange(int i, boolean z) {
        if (this.mColorFadeEnabled && (this.mColorFadeOnAnimator.isStarted() || this.mColorFadeOffAnimator.isStarted())) {
            if (i != 2) {
                return;
            }
            this.mPendingScreenOff = false;
        }
        if (this.mDisplayBlanksAfterDozeConfig && Display.isDozeState(this.mPowerState.getScreenState()) && !Display.isDozeState(i)) {
            this.mPowerState.prepareColorFade(this.mContext, this.mColorFadeFadesConfig ? 2 : 0);
            ObjectAnimator objectAnimator = this.mColorFadeOffAnimator;
            if (objectAnimator != null) {
                objectAnimator.end();
            }
            setScreenState(1, i != 1);
        }
        if (this.mPendingScreenOff && i != 1) {
            setScreenState(1);
            this.mPendingScreenOff = false;
            this.mPowerState.dismissColorFadeResources();
        }
        if (i == 2) {
            if (setScreenState(2)) {
                this.mPowerState.setColorFadeLevel(1.0f);
                this.mPowerState.dismissColorFade();
            }
        } else if (i == 3) {
            if (!(this.mScreenBrightnessRampAnimator.isAnimating() && this.mPowerState.getScreenState() == 2) && setScreenState(3)) {
                this.mPowerState.setColorFadeLevel(1.0f);
                this.mPowerState.dismissColorFade();
            }
        } else if (i == 4) {
            if (!this.mScreenBrightnessRampAnimator.isAnimating() || this.mPowerState.getScreenState() == 4) {
                if (this.mPowerState.getScreenState() != 4) {
                    if (!setScreenState(3)) {
                        return;
                    }
                    setScreenState(4);
                }
                this.mPowerState.setColorFadeLevel(1.0f);
                this.mPowerState.dismissColorFade();
            }
        } else if (i == 6) {
            if (!this.mScreenBrightnessRampAnimator.isAnimating() || this.mPowerState.getScreenState() == 6) {
                if (this.mPowerState.getScreenState() != 6) {
                    if (!setScreenState(2)) {
                        return;
                    }
                    setScreenState(6);
                }
                this.mPowerState.setColorFadeLevel(1.0f);
                this.mPowerState.dismissColorFade();
            }
        } else {
            this.mPendingScreenOff = true;
            if (!this.mColorFadeEnabled) {
                this.mPowerState.setColorFadeLevel(0.0f);
            }
            if (this.mPowerState.getColorFadeLevel() == 0.0f) {
                setScreenState(1);
                this.mPendingScreenOff = false;
                this.mPowerState.dismissColorFadeResources();
                return;
            }
            if (z) {
                if (this.mPowerState.prepareColorFade(this.mContext, this.mColorFadeFadesConfig ? 2 : 1) && this.mPowerState.getScreenState() != 1) {
                    this.mColorFadeOffAnimator.start();
                    return;
                }
            }
            this.mColorFadeOffAnimator.end();
        }
    }

    public final void setProximitySensorEnabled(boolean z) {
        if (z) {
            if (this.mProximitySensorEnabled) {
                return;
            }
            this.mProximitySensorEnabled = true;
            this.mIgnoreProximityUntilChanged = false;
            this.mSensorManager.registerListener(this.mProximitySensorListener, this.mProximitySensor, 3, this.mHandler);
        } else if (this.mProximitySensorEnabled) {
            this.mProximitySensorEnabled = false;
            this.mProximity = -1;
            this.mIgnoreProximityUntilChanged = false;
            this.mPendingProximity = -1;
            this.mHandler.removeMessages(2);
            this.mSensorManager.unregisterListener(this.mProximitySensorListener);
            clearPendingProximityDebounceTime();
        }
    }

    public final void handleProximitySensorEvent(long j, boolean z) {
        if (this.mProximitySensorEnabled) {
            int i = this.mPendingProximity;
            if (i != 0 || z) {
                if (i == 1 && z) {
                    return;
                }
                this.mHandler.removeMessages(2);
                if (z) {
                    this.mPendingProximity = 1;
                    setPendingProximityDebounceTime(j + 0);
                } else {
                    this.mPendingProximity = 0;
                    setPendingProximityDebounceTime(j + 250);
                }
                debounceProximitySensor();
            }
        }
    }

    public final void debounceProximitySensor() {
        if (!this.mProximitySensorEnabled || this.mPendingProximity == -1 || this.mPendingProximityDebounceTime < 0) {
            return;
        }
        if (this.mPendingProximityDebounceTime <= this.mClock.uptimeMillis()) {
            if (this.mProximity != this.mPendingProximity) {
                this.mIgnoreProximityUntilChanged = false;
                String str = this.mTag;
                Slog.i(str, "No longer ignoring proximity [" + this.mPendingProximity + "]");
            }
            this.mProximity = this.mPendingProximity;
            updatePowerState();
            clearPendingProximityDebounceTime();
            return;
        }
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(2), this.mPendingProximityDebounceTime);
    }

    public final void clearPendingProximityDebounceTime() {
        if (this.mPendingProximityDebounceTime >= 0) {
            this.mPendingProximityDebounceTime = -1L;
            this.mCallbacks.releaseSuspendBlocker(this.mSuspendBlockerIdProxDebounce);
        }
    }

    public final void setPendingProximityDebounceTime(long j) {
        if (this.mPendingProximityDebounceTime < 0) {
            this.mCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdProxDebounce);
        }
        this.mPendingProximityDebounceTime = j;
    }

    public final void sendOnStateChangedWithWakelock() {
        if (this.mOnStateChangedPending) {
            return;
        }
        this.mOnStateChangedPending = true;
        this.mCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdOnStateChanged);
        this.mHandler.post(this.mOnStateChangedRunnable);
    }

    public final void logDisplayPolicyChanged(int i) {
        LogMaker logMaker = new LogMaker(1696);
        logMaker.setType(6);
        logMaker.setSubtype(i);
        MetricsLogger.action(logMaker);
    }

    public final void handleSettingsChange(boolean z) {
        this.mPendingScreenBrightnessSetting = getScreenBrightnessSetting();
        this.mPendingAutoBrightnessAdjustment = getAutoBrightnessAdjustmentSetting();
        if (z) {
            setCurrentScreenBrightness(this.mPendingScreenBrightnessSetting);
            updateAutoBrightnessAdjustment();
            AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
            if (automaticBrightnessController != null) {
                automaticBrightnessController.resetShortTermModel();
            }
        }
        sendUpdatePowerState();
    }

    public final void handleBrightnessModeChange() {
        final int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2);
        this.mHandler.postAtTime(new Runnable() { // from class: com.android.server.display.DisplayPowerController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController.this.lambda$handleBrightnessModeChange$5(intForUser);
            }
        }, this.mClock.uptimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleBrightnessModeChange$5(int i) {
        this.mUseAutoBrightness = i == 1;
        updatePowerState();
    }

    public final float getAutoBrightnessAdjustmentSetting() {
        float floatForUser = Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", 0.0f, -2);
        if (Float.isNaN(floatForUser)) {
            return 0.0f;
        }
        return clampAutoBrightnessAdjustment(floatForUser);
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public float getScreenBrightnessSetting() {
        float brightness = this.mBrightnessSetting.getBrightness();
        if (Float.isNaN(brightness)) {
            brightness = this.mScreenBrightnessDefault;
        }
        return clampAbsoluteBrightness(brightness);
    }

    public final void loadNitBasedBrightnessSetting() {
        if (this.mDisplayId == 0 && this.mPersistBrightnessNitsForDefaultDisplay) {
            float brightnessNitsForDefaultDisplay = this.mBrightnessSetting.getBrightnessNitsForDefaultDisplay();
            if (brightnessNitsForDefaultDisplay >= 0.0f) {
                float convertToFloatScale = convertToFloatScale(brightnessNitsForDefaultDisplay);
                if (isValidBrightnessValue(convertToFloatScale)) {
                    this.mBrightnessSetting.setBrightness(convertToFloatScale);
                    this.mCurrentScreenBrightnessSetting = convertToFloatScale;
                    return;
                }
            }
        }
        this.mCurrentScreenBrightnessSetting = getScreenBrightnessSetting();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setBrightness(float f) {
        this.mBrightnessSetting.setBrightness(f);
        if (this.mDisplayId == 0 && this.mPersistBrightnessNitsForDefaultDisplay) {
            float convertToNits = convertToNits(f);
            if (convertToNits >= 0.0f) {
                this.mBrightnessSetting.setBrightnessNitsForDefaultDisplay(convertToNits);
            }
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void onBootCompleted() {
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(15), this.mClock.uptimeMillis());
    }

    public final void updateScreenBrightnessSetting(float f) {
        if (!isValidBrightnessValue(f) || f == this.mCurrentScreenBrightnessSetting) {
            return;
        }
        setCurrentScreenBrightness(f);
        setBrightness(f);
    }

    public final void setCurrentScreenBrightness(float f) {
        if (f != this.mCurrentScreenBrightnessSetting) {
            this.mCurrentScreenBrightnessSetting = f;
            postBrightnessChangeRunnable();
        }
    }

    public final void putAutoBrightnessAdjustmentSetting(float f) {
        if (this.mDisplayId == 0) {
            this.mAutoBrightnessAdjustment = f;
            Settings.System.putFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", f, -2);
        }
    }

    public final boolean updateAutoBrightnessAdjustment() {
        if (Float.isNaN(this.mPendingAutoBrightnessAdjustment)) {
            return false;
        }
        float f = this.mAutoBrightnessAdjustment;
        float f2 = this.mPendingAutoBrightnessAdjustment;
        if (f == f2) {
            this.mPendingAutoBrightnessAdjustment = Float.NaN;
            return false;
        }
        this.mAutoBrightnessAdjustment = f2;
        this.mPendingAutoBrightnessAdjustment = Float.NaN;
        this.mTemporaryAutoBrightnessAdjustment = Float.NaN;
        return true;
    }

    public final boolean updateUserSetScreenBrightness() {
        if (!Float.isNaN(this.mPendingScreenBrightnessSetting)) {
            float f = this.mPendingScreenBrightnessSetting;
            if (f >= 0.0f) {
                if (this.mCurrentScreenBrightnessSetting == f) {
                    this.mPendingScreenBrightnessSetting = Float.NaN;
                    this.mTemporaryScreenBrightness = Float.NaN;
                    return false;
                }
                setCurrentScreenBrightness(f);
                this.mLastUserSetScreenBrightness = this.mPendingScreenBrightnessSetting;
                this.mPendingScreenBrightnessSetting = Float.NaN;
                this.mTemporaryScreenBrightness = Float.NaN;
                return true;
            }
        }
        return false;
    }

    public final void notifyBrightnessTrackerChanged(float f, boolean z, boolean z2) {
        AutomaticBrightnessController automaticBrightnessController;
        BrightnessTracker brightnessTracker;
        float convertToNits = convertToNits(f);
        if (!this.mUseAutoBrightness || convertToNits < 0.0f || (automaticBrightnessController = this.mAutomaticBrightnessController) == null || (brightnessTracker = this.mBrightnessTracker) == null) {
            return;
        }
        DisplayManagerInternal.DisplayPowerRequest displayPowerRequest = this.mPowerRequest;
        brightnessTracker.notifyBrightnessChanged(convertToNits, z, displayPowerRequest.lowPowerMode ? displayPowerRequest.screenLowPowerBrightnessFactor : 1.0f, z2, automaticBrightnessController.isDefaultConfig(), this.mUniqueDisplayId, this.mAutomaticBrightnessController.getLastSensorValues(), this.mAutomaticBrightnessController.getLastSensorTimestamps());
    }

    public final float convertToNits(float f) {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController == null) {
            return -1.0f;
        }
        return automaticBrightnessController.convertToNits(f);
    }

    public final float convertToFloatScale(float f) {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController == null) {
            return Float.NaN;
        }
        return automaticBrightnessController.convertToFloatScale(f);
    }

    @GuardedBy({"mLock"})
    public final void updatePendingProximityRequestsLocked() {
        this.mWaitingForNegativeProximity |= this.mPendingWaitForNegativeProximityLocked;
        this.mPendingWaitForNegativeProximityLocked = false;
        if (this.mIgnoreProximityUntilChanged) {
            this.mWaitingForNegativeProximity = false;
        }
    }

    public final void ignoreProximitySensorUntilChangedInternal() {
        if (this.mIgnoreProximityUntilChanged || this.mProximity != 1) {
            return;
        }
        this.mIgnoreProximityUntilChanged = true;
        Slog.i(this.mTag, "Ignoring proximity");
        updatePowerState();
    }

    public final void sendOnProximityPositiveWithWakelock() {
        this.mCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdProxPositive);
        this.mHandler.post(this.mOnProximityPositiveRunnable);
        this.mOnProximityPositiveMessages++;
    }

    public final void sendOnProximityNegativeWithWakelock() {
        this.mOnProximityNegativeMessages++;
        this.mCallbacks.acquireSuspendBlocker(this.mSuspendBlockerIdProxNegative);
        this.mHandler.post(this.mOnProximityNegativeRunnable);
    }

    public final boolean readyToUpdateDisplayState() {
        return this.mDisplayId == 0 || this.mBootCompleted;
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void dump(final PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.println();
            printWriter.println("Display Power Controller:");
            printWriter.println("  mDisplayId=" + this.mDisplayId);
            printWriter.println("  mLeadDisplayId=" + this.mLeadDisplayId);
            printWriter.println("  mLightSensor=" + this.mLightSensor);
            printWriter.println();
            printWriter.println("Display Power Controller Locked State:");
            printWriter.println("  mDisplayReadyLocked=" + this.mDisplayReadyLocked);
            printWriter.println("  mPendingRequestLocked=" + this.mPendingRequestLocked);
            printWriter.println("  mPendingRequestChangedLocked=" + this.mPendingRequestChangedLocked);
            printWriter.println("  mPendingWaitForNegativeProximityLocked=" + this.mPendingWaitForNegativeProximityLocked);
            printWriter.println("  mPendingUpdatePowerStateLocked=" + this.mPendingUpdatePowerStateLocked);
        }
        printWriter.println();
        printWriter.println("Display Power Controller Configuration:");
        printWriter.println("  mScreenBrightnessRangeDefault=" + this.mScreenBrightnessDefault);
        printWriter.println("  mScreenBrightnessDozeConfig=" + this.mScreenBrightnessDozeConfig);
        printWriter.println("  mScreenBrightnessDimConfig=" + this.mScreenBrightnessDimConfig);
        printWriter.println("  mUseSoftwareAutoBrightnessConfig=" + this.mUseSoftwareAutoBrightnessConfig);
        printWriter.println("  mAllowAutoBrightnessWhileDozingConfig=" + this.mAllowAutoBrightnessWhileDozingConfig);
        printWriter.println("  mPersistBrightnessNitsForDefaultDisplay=" + this.mPersistBrightnessNitsForDefaultDisplay);
        printWriter.println("  mSkipScreenOnBrightnessRamp=" + this.mSkipScreenOnBrightnessRamp);
        printWriter.println("  mColorFadeFadesConfig=" + this.mColorFadeFadesConfig);
        printWriter.println("  mColorFadeEnabled=" + this.mColorFadeEnabled);
        synchronized (this.mCachedBrightnessInfo) {
            printWriter.println("  mCachedBrightnessInfo.brightness=" + this.mCachedBrightnessInfo.brightness.value);
            printWriter.println("  mCachedBrightnessInfo.adjustedBrightness=" + this.mCachedBrightnessInfo.adjustedBrightness.value);
            printWriter.println("  mCachedBrightnessInfo.brightnessMin=" + this.mCachedBrightnessInfo.brightnessMin.value);
            printWriter.println("  mCachedBrightnessInfo.brightnessMax=" + this.mCachedBrightnessInfo.brightnessMax.value);
            printWriter.println("  mCachedBrightnessInfo.hbmMode=" + this.mCachedBrightnessInfo.hbmMode.value);
            printWriter.println("  mCachedBrightnessInfo.hbmTransitionPoint=" + this.mCachedBrightnessInfo.hbmTransitionPoint.value);
            printWriter.println("  mCachedBrightnessInfo.brightnessMaxReason =" + this.mCachedBrightnessInfo.brightnessMaxReason.value);
        }
        printWriter.println("  mDisplayBlanksAfterDozeConfig=" + this.mDisplayBlanksAfterDozeConfig);
        printWriter.println("  mBrightnessBucketsInDozeConfig=" + this.mBrightnessBucketsInDozeConfig);
        this.mHandler.runWithScissors(new Runnable() { // from class: com.android.server.display.DisplayPowerController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController.this.lambda$dump$6(printWriter);
            }
        }, 1000L);
    }

    /* renamed from: dumpLocal */
    public final void lambda$dump$6(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("Display Power Controller Thread State:");
        printWriter.println("  mPowerRequest=" + this.mPowerRequest);
        printWriter.println("  mUnfinishedBusiness=" + this.mUnfinishedBusiness);
        printWriter.println("  mWaitingForNegativeProximity=" + this.mWaitingForNegativeProximity);
        printWriter.println("  mProximitySensor=" + this.mProximitySensor);
        printWriter.println("  mProximitySensorEnabled=" + this.mProximitySensorEnabled);
        printWriter.println("  mProximityThreshold=" + this.mProximityThreshold);
        printWriter.println("  mProximity=" + proximityToString(this.mProximity));
        printWriter.println("  mPendingProximity=" + proximityToString(this.mPendingProximity));
        printWriter.println("  mPendingProximityDebounceTime=" + TimeUtils.formatUptime(this.mPendingProximityDebounceTime));
        printWriter.println("  mScreenOffBecauseOfProximity=" + this.mScreenOffBecauseOfProximity);
        printWriter.println("  mLastUserSetScreenBrightness=" + this.mLastUserSetScreenBrightness);
        printWriter.println("  mPendingScreenBrightnessSetting=" + this.mPendingScreenBrightnessSetting);
        printWriter.println("  mTemporaryScreenBrightness=" + this.mTemporaryScreenBrightness);
        printWriter.println("  mBrightnessToFollow=" + this.mBrightnessToFollow);
        printWriter.println("  mAutoBrightnessAdjustment=" + this.mAutoBrightnessAdjustment);
        printWriter.println("  mBrightnessReason=" + this.mBrightnessReason);
        printWriter.println("  mTemporaryAutoBrightnessAdjustment=" + this.mTemporaryAutoBrightnessAdjustment);
        printWriter.println("  mPendingAutoBrightnessAdjustment=" + this.mPendingAutoBrightnessAdjustment);
        printWriter.println("  mAppliedAutoBrightness=" + this.mAppliedAutoBrightness);
        printWriter.println("  mAppliedDimming=" + this.mAppliedDimming);
        printWriter.println("  mAppliedLowPower=" + this.mAppliedLowPower);
        printWriter.println("  mAppliedThrottling=" + this.mAppliedThrottling);
        printWriter.println("  mAppliedScreenBrightnessOverride=" + this.mAppliedScreenBrightnessOverride);
        printWriter.println("  mAppliedTemporaryBrightness=" + this.mAppliedTemporaryBrightness);
        printWriter.println("  mAppliedTemporaryAutoBrightnessAdjustment=" + this.mAppliedTemporaryAutoBrightnessAdjustment);
        printWriter.println("  mAppliedBrightnessBoost=" + this.mAppliedBrightnessBoost);
        printWriter.println("  mDozing=" + this.mDozing);
        printWriter.println("  mSkipRampState=" + skipRampStateToString(this.mSkipRampState));
        printWriter.println("  mScreenOnBlockStartRealTime=" + this.mScreenOnBlockStartRealTime);
        printWriter.println("  mScreenOffBlockStartRealTime=" + this.mScreenOffBlockStartRealTime);
        printWriter.println("  mPendingScreenOnUnblocker=" + this.mPendingScreenOnUnblocker);
        printWriter.println("  mPendingScreenOffUnblocker=" + this.mPendingScreenOffUnblocker);
        printWriter.println("  mPendingScreenOff=" + this.mPendingScreenOff);
        printWriter.println("  mReportedToPolicy=" + reportedToPolicyToString(this.mReportedScreenStateToPolicy));
        printWriter.println("  mIsRbcActive=" + this.mIsRbcActive);
        printWriter.println("  mOnStateChangePending=" + this.mOnStateChangedPending);
        printWriter.println("  mOnProximityPositiveMessages=" + this.mOnProximityPositiveMessages);
        printWriter.println("  mOnProximityNegativeMessages=" + this.mOnProximityNegativeMessages);
        if (this.mScreenBrightnessRampAnimator != null) {
            printWriter.println("  mScreenBrightnessRampAnimator.isAnimating()=" + this.mScreenBrightnessRampAnimator.isAnimating());
        }
        if (this.mColorFadeOnAnimator != null) {
            printWriter.println("  mColorFadeOnAnimator.isStarted()=" + this.mColorFadeOnAnimator.isStarted());
        }
        if (this.mColorFadeOffAnimator != null) {
            printWriter.println("  mColorFadeOffAnimator.isStarted()=" + this.mColorFadeOffAnimator.isStarted());
        }
        DisplayPowerState displayPowerState = this.mPowerState;
        if (displayPowerState != null) {
            displayPowerState.dump(printWriter);
        }
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController != null) {
            automaticBrightnessController.dump(printWriter);
            dumpBrightnessEvents(printWriter);
        }
        ScreenOffBrightnessSensorController screenOffBrightnessSensorController = this.mScreenOffBrightnessSensorController;
        if (screenOffBrightnessSensorController != null) {
            screenOffBrightnessSensorController.dump(printWriter);
        }
        HighBrightnessModeController highBrightnessModeController = this.mHbmController;
        if (highBrightnessModeController != null) {
            highBrightnessModeController.dump(printWriter);
        }
        BrightnessThrottler brightnessThrottler = this.mBrightnessThrottler;
        if (brightnessThrottler != null) {
            brightnessThrottler.dump(printWriter);
        }
        printWriter.println();
        DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
        if (displayWhiteBalanceController != null) {
            displayWhiteBalanceController.dump(printWriter);
            this.mDisplayWhiteBalanceSettings.dump(printWriter);
        }
    }

    public static String proximityToString(int i) {
        return i != -1 ? i != 0 ? i != 1 ? Integer.toString(i) : "Positive" : "Negative" : "Unknown";
    }

    public static String reportedToPolicyToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? Integer.toString(i) : "REPORTED_TO_POLICY_SCREEN_ON" : "REPORTED_TO_POLICY_SCREEN_TURNING_ON" : "REPORTED_TO_POLICY_SCREEN_OFF";
    }

    public static String skipRampStateToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? Integer.toString(i) : "RAMP_STATE_SKIP_AUTOBRIGHT" : "RAMP_STATE_SKIP_INITIAL" : "RAMP_STATE_SKIP_NONE";
    }

    public final void dumpBrightnessEvents(PrintWriter printWriter) {
        int size = this.mBrightnessEventRingBuffer.size();
        if (size < 1) {
            printWriter.println("No Automatic Brightness Adjustments");
            return;
        }
        printWriter.println("Automatic Brightness Adjustments Last " + size + " Events: ");
        BrightnessEvent[] brightnessEventArr = (BrightnessEvent[]) this.mBrightnessEventRingBuffer.toArray();
        for (int i = 0; i < this.mBrightnessEventRingBuffer.size(); i++) {
            printWriter.println("  " + brightnessEventArr[i].toString());
        }
    }

    public static float clampAbsoluteBrightness(float f) {
        return MathUtils.constrain(f, 0.0f, 1.0f);
    }

    public static float clampAutoBrightnessAdjustment(float f) {
        return MathUtils.constrain(f, -1.0f, 1.0f);
    }

    public final void noteScreenState(int i) {
        FrameworkStatsLog.write((int) FrameworkStatsLog.SCREEN_STATE_CHANGED_V2, i, this.mDisplayStatsId);
        IBatteryStats iBatteryStats = this.mBatteryStats;
        if (iBatteryStats != null) {
            try {
                iBatteryStats.noteScreenState(i);
            } catch (RemoteException unused) {
            }
        }
    }

    public final void noteScreenBrightness(float f) {
        IBatteryStats iBatteryStats = this.mBatteryStats;
        if (iBatteryStats != null) {
            try {
                iBatteryStats.noteScreenBrightness(BrightnessSynchronizer.brightnessFloatToInt(f));
            } catch (RemoteException unused) {
            }
        }
    }

    public final void reportStats(float f) {
        if (this.mLastStatsBrightness == f) {
            return;
        }
        synchronized (this.mCachedBrightnessInfo) {
            MutableFloat mutableFloat = this.mCachedBrightnessInfo.hbmTransitionPoint;
            if (mutableFloat == null) {
                return;
            }
            float f2 = mutableFloat.value;
            boolean z = f > f2;
            boolean z2 = this.mLastStatsBrightness > f2;
            if (z || z2) {
                this.mLastStatsBrightness = f;
                this.mHandler.removeMessages(13);
                if (z != z2) {
                    logHbmBrightnessStats(f, this.mDisplayStatsId);
                    return;
                }
                Message obtainMessage = this.mHandler.obtainMessage();
                obtainMessage.what = 13;
                obtainMessage.arg1 = Float.floatToIntBits(f);
                obtainMessage.arg2 = this.mDisplayStatsId;
                this.mHandler.sendMessageAtTime(obtainMessage, this.mClock.uptimeMillis() + 500);
            }
        }
    }

    public final void logHbmBrightnessStats(float f, int i) {
        synchronized (this.mHandler) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.DISPLAY_HBM_BRIGHTNESS_CHANGED, i, f);
        }
    }

    public final void logManualBrightnessEvent(BrightnessEvent brightnessEvent) {
        float powerFactor = brightnessEvent.isLowPowerModeSet() ? brightnessEvent.getPowerFactor() : -1.0f;
        int rbcStrength = brightnessEvent.isRbcEnabled() ? brightnessEvent.getRbcStrength() : -1;
        float convertToNits = brightnessEvent.getHbmMode() == 0 ? -1.0f : convertToNits(brightnessEvent.getHbmMax());
        float convertToNits2 = brightnessEvent.getThermalMax() != 1.0f ? convertToNits(brightnessEvent.getThermalMax()) : -1.0f;
        if (this.mLogicalDisplay.getPrimaryDisplayDeviceLocked() == null || this.mLogicalDisplay.getPrimaryDisplayDeviceLocked().getDisplayDeviceInfoLocked().type != 1) {
            return;
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.DISPLAY_BRIGHTNESS_CHANGED, convertToNits(brightnessEvent.getInitialBrightness()), convertToNits(brightnessEvent.getBrightness()), brightnessEvent.getLux(), brightnessEvent.getPhysicalDisplayId(), brightnessEvent.wasShortTermModelActive(), powerFactor, rbcStrength, convertToNits, convertToNits2, brightnessEvent.isAutomaticBrightnessEnabled(), 1);
    }

    /* loaded from: classes.dex */
    public final class DisplayControllerHandler extends Handler {
        public DisplayControllerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    DisplayPowerController.this.updatePowerState();
                    return;
                case 2:
                    DisplayPowerController.this.debounceProximitySensor();
                    return;
                case 3:
                    if (DisplayPowerController.this.mPendingScreenOnUnblocker == message.obj) {
                        DisplayPowerController.this.unblockScreenOn();
                        DisplayPowerController.this.updatePowerState();
                        return;
                    }
                    return;
                case 4:
                    if (DisplayPowerController.this.mPendingScreenOffUnblocker == message.obj) {
                        DisplayPowerController.this.unblockScreenOff();
                        DisplayPowerController.this.updatePowerState();
                        return;
                    }
                    return;
                case 5:
                    DisplayPowerController.this.mBrightnessConfiguration = (BrightnessConfiguration) message.obj;
                    DisplayPowerController.this.mShouldResetShortTermModel = message.arg1 == 1;
                    DisplayPowerController.this.updatePowerState();
                    return;
                case 6:
                    DisplayPowerController.this.mTemporaryScreenBrightness = Float.intBitsToFloat(message.arg1);
                    DisplayPowerController.this.updatePowerState();
                    return;
                case 7:
                    DisplayPowerController.this.mTemporaryAutoBrightnessAdjustment = Float.intBitsToFloat(message.arg1);
                    DisplayPowerController.this.updatePowerState();
                    return;
                case 8:
                    DisplayPowerController.this.ignoreProximitySensorUntilChangedInternal();
                    return;
                case 9:
                    DisplayPowerController.this.cleanupHandlerThreadAfterStop();
                    return;
                case 10:
                    if (DisplayPowerController.this.mStopped) {
                        return;
                    }
                    DisplayPowerController.this.handleSettingsChange(false);
                    return;
                case 11:
                    DisplayPowerController.this.handleRbcChanged();
                    return;
                case 12:
                    if (DisplayPowerController.this.mPowerState != null) {
                        DisplayPowerController.this.reportStats(DisplayPowerController.this.mPowerState.getScreenBrightness());
                        return;
                    }
                    return;
                case 13:
                    DisplayPowerController.this.logHbmBrightnessStats(Float.intBitsToFloat(message.arg1), message.arg2);
                    return;
                case 14:
                    DisplayPowerController.this.handleOnSwitchUser(message.arg1);
                    return;
                case 15:
                    DisplayPowerController.this.mBootCompleted = true;
                    DisplayPowerController.this.updatePowerState();
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes.dex */
    public final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (uri.equals(Settings.System.getUriFor("screen_brightness_mode"))) {
                DisplayPowerController.this.handleBrightnessModeChange();
            } else {
                DisplayPowerController.this.handleSettingsChange(false);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class ScreenOnUnblocker implements WindowManagerPolicy.ScreenOnListener {
        public ScreenOnUnblocker() {
        }

        @Override // com.android.server.policy.WindowManagerPolicy.ScreenOnListener
        public void onScreenOn() {
            DisplayPowerController.this.mHandler.sendMessageAtTime(DisplayPowerController.this.mHandler.obtainMessage(3, this), DisplayPowerController.this.mClock.uptimeMillis());
        }
    }

    /* loaded from: classes.dex */
    public final class ScreenOffUnblocker implements WindowManagerPolicy.ScreenOffListener {
        public ScreenOffUnblocker() {
        }

        @Override // com.android.server.policy.WindowManagerPolicy.ScreenOffListener
        public void onScreenOff() {
            DisplayPowerController.this.mHandler.sendMessageAtTime(DisplayPowerController.this.mHandler.obtainMessage(4, this), DisplayPowerController.this.mClock.uptimeMillis());
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setAutoBrightnessLoggingEnabled(boolean z) {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController != null) {
            automaticBrightnessController.setLoggingEnabled(z);
        }
    }

    @Override // com.android.server.display.whitebalance.DisplayWhiteBalanceController.Callbacks
    public void updateWhiteBalance() {
        sendUpdatePowerState();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setDisplayWhiteBalanceLoggingEnabled(boolean z) {
        DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
        if (displayWhiteBalanceController != null) {
            displayWhiteBalanceController.setLoggingEnabled(z);
            this.mDisplayWhiteBalanceSettings.setLoggingEnabled(z);
        }
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setAmbientColorTemperatureOverride(float f) {
        DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
        if (displayWhiteBalanceController != null) {
            displayWhiteBalanceController.setAmbientColorTemperatureOverride(f);
            sendUpdatePowerState();
        }
    }

    @VisibleForTesting
    public String getSuspendBlockerUnfinishedBusinessId(int i) {
        return "[" + i + "]unfinished business";
    }

    public String getSuspendBlockerOnStateChangedId(int i) {
        return "[" + i + "]on state changed";
    }

    public String getSuspendBlockerProxPositiveId(int i) {
        return "[" + i + "]prox positive";
    }

    public String getSuspendBlockerProxNegativeId(int i) {
        return "[" + i + "]prox negative";
    }

    @VisibleForTesting
    public String getSuspendBlockerProxDebounceId(int i) {
        return "[" + i + "]prox debounce";
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public Clock getClock() {
            return new Clock() { // from class: com.android.server.display.DisplayPowerController$Injector$$ExternalSyntheticLambda0
                @Override // com.android.server.display.DisplayPowerController.Clock
                public final long uptimeMillis() {
                    return SystemClock.uptimeMillis();
                }
            };
        }

        public DisplayPowerState getDisplayPowerState(DisplayBlanker displayBlanker, ColorFade colorFade, int i, int i2) {
            return new DisplayPowerState(displayBlanker, colorFade, i, i2);
        }

        public RampAnimator.DualRampAnimator<DisplayPowerState> getDualRampAnimator(DisplayPowerState displayPowerState, FloatProperty<DisplayPowerState> floatProperty, FloatProperty<DisplayPowerState> floatProperty2) {
            return new RampAnimator.DualRampAnimator<>(displayPowerState, floatProperty, floatProperty2);
        }

        public AutomaticBrightnessController getAutomaticBrightnessController(AutomaticBrightnessController.Callbacks callbacks, Looper looper, SensorManager sensorManager, Sensor sensor, BrightnessMappingStrategy brightnessMappingStrategy, int i, float f, float f2, float f3, int i2, int i3, long j, long j2, boolean z, HysteresisLevels hysteresisLevels, HysteresisLevels hysteresisLevels2, HysteresisLevels hysteresisLevels3, HysteresisLevels hysteresisLevels4, Context context, HighBrightnessModeController highBrightnessModeController, BrightnessThrottler brightnessThrottler, BrightnessMappingStrategy brightnessMappingStrategy2, int i4, int i5, float f4, float f5) {
            return new AutomaticBrightnessController(callbacks, looper, sensorManager, sensor, brightnessMappingStrategy, i, f, f2, f3, i2, i3, j, j2, z, hysteresisLevels, hysteresisLevels2, hysteresisLevels3, hysteresisLevels4, context, highBrightnessModeController, brightnessThrottler, brightnessMappingStrategy2, i4, i5, f4, f5);
        }

        public BrightnessMappingStrategy getInteractiveModeBrightnessMapper(Resources resources, DisplayDeviceConfig displayDeviceConfig, DisplayWhiteBalanceController displayWhiteBalanceController) {
            return BrightnessMappingStrategy.create(resources, displayDeviceConfig, displayWhiteBalanceController);
        }

        public HysteresisLevels getHysteresisLevels(float[] fArr, float[] fArr2, float[] fArr3, float[] fArr4, float f, float f2) {
            return new HysteresisLevels(fArr, fArr2, fArr3, fArr4, f, f2);
        }

        public HysteresisLevels getHysteresisLevels(float[] fArr, float[] fArr2, float[] fArr3, float[] fArr4, float f, float f2, boolean z) {
            return new HysteresisLevels(fArr, fArr2, fArr3, fArr4, f, f2, z);
        }

        public ScreenOffBrightnessSensorController getScreenOffBrightnessSensorController(SensorManager sensorManager, Sensor sensor, Handler handler, ScreenOffBrightnessSensorController.Clock clock, int[] iArr, BrightnessMappingStrategy brightnessMappingStrategy) {
            return new ScreenOffBrightnessSensorController(sensorManager, sensor, handler, clock, iArr, brightnessMappingStrategy);
        }
    }

    /* loaded from: classes.dex */
    public static class CachedBrightnessInfo {
        public MutableFloat brightness = new MutableFloat(Float.NaN);
        public MutableFloat adjustedBrightness = new MutableFloat(Float.NaN);
        public MutableFloat brightnessMin = new MutableFloat(Float.NaN);
        public MutableFloat brightnessMax = new MutableFloat(Float.NaN);
        public MutableInt hbmMode = new MutableInt(0);
        public MutableFloat hbmTransitionPoint = new MutableFloat(Float.POSITIVE_INFINITY);
        public MutableInt brightnessMaxReason = new MutableInt(0);

        public boolean checkAndSetFloat(MutableFloat mutableFloat, float f) {
            if (mutableFloat.value != f) {
                mutableFloat.value = f;
                return true;
            }
            return false;
        }

        public boolean checkAndSetInt(MutableInt mutableInt, int i) {
            if (mutableInt.value != i) {
                mutableInt.value = i;
                return true;
            }
            return false;
        }
    }
}
