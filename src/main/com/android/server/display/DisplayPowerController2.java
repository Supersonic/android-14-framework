package com.android.server.display;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.display.AmbientBrightnessDayStats;
import android.hardware.display.BrightnessChangeEvent;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.BrightnessInfo;
import android.hardware.display.DisplayManagerInternal;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerExecutor;
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
import com.android.server.display.brightness.DisplayBrightnessController;
import com.android.server.display.color.ColorDisplayService;
import com.android.server.display.state.DisplayStateController;
import com.android.server.display.utils.SensorUtils;
import com.android.server.display.whitebalance.DisplayWhiteBalanceController;
import com.android.server.display.whitebalance.DisplayWhiteBalanceFactory;
import com.android.server.display.whitebalance.DisplayWhiteBalanceSettings;
import com.android.server.p006am.BatteryStatsService;
import com.android.server.policy.WindowManagerPolicy;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class DisplayPowerController2 implements AutomaticBrightnessController.Callbacks, DisplayWhiteBalanceController.Callbacks, DisplayPowerControllerInterface {
    public boolean mAppliedAutoBrightness;
    public boolean mAppliedDimming;
    public boolean mAppliedLowPower;
    public boolean mAppliedTemporaryAutoBrightnessAdjustment;
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
    public final BrightnessThrottler mBrightnessThrottler;
    public String mBrightnessThrottlingDataId;
    public final BrightnessTracker mBrightnessTracker;
    public final ColorDisplayService.ColorDisplayServiceInternal mCdsi;
    public final Clock mClock;
    public final boolean mColorFadeEnabled;
    public final boolean mColorFadeFadesConfig;
    public ObjectAnimator mColorFadeOffAnimator;
    public ObjectAnimator mColorFadeOnAnimator;
    public final Context mContext;
    public final boolean mDisplayBlanksAfterDozeConfig;
    public final DisplayBrightnessController mDisplayBrightnessController;
    public DisplayDevice mDisplayDevice;
    public DisplayDeviceConfig mDisplayDeviceConfig;
    public final int mDisplayId;
    public final DisplayPowerProximityStateController mDisplayPowerProximityStateController;
    @GuardedBy({"mLock"})
    public boolean mDisplayReadyLocked;
    public final DisplayStateController mDisplayStateController;
    public int mDisplayStatsId;
    public final DisplayWhiteBalanceController mDisplayWhiteBalanceController;
    public final DisplayWhiteBalanceSettings mDisplayWhiteBalanceSettings;
    public boolean mDozing;
    public final DisplayControllerHandler mHandler;
    public final HighBrightnessModeController mHbmController;
    public final HighBrightnessModeMetadata mHighBrightnessModeMetadata;
    public BrightnessMappingStrategy mIdleModeBrightnessMapper;
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
    public float mPendingAutoBrightnessAdjustment;
    @GuardedBy({"mLock"})
    public boolean mPendingRequestChangedLocked;
    @GuardedBy({"mLock"})
    public DisplayManagerInternal.DisplayPowerRequest mPendingRequestLocked;
    public boolean mPendingScreenOff;
    public ScreenOffUnblocker mPendingScreenOffUnblocker;
    public ScreenOnUnblocker mPendingScreenOnUnblocker;
    @GuardedBy({"mLock"})
    public boolean mPendingUpdatePowerStateLocked;
    public DisplayManagerInternal.DisplayPowerRequest mPowerRequest;
    public DisplayPowerState mPowerState;
    public final float mScreenBrightnessDimConfig;
    public final float mScreenBrightnessDozeConfig;
    public final float mScreenBrightnessMinimumDimAmount;
    public RampAnimator.DualRampAnimator<DisplayPowerState> mScreenBrightnessRampAnimator;
    public long mScreenOffBlockStartRealTime;
    public Sensor mScreenOffBrightnessSensor;
    public ScreenOffBrightnessSensorController mScreenOffBrightnessSensorController;
    public long mScreenOnBlockStartRealTime;
    public final SensorManager mSensorManager;
    public final SettingsObserver mSettingsObserver;
    public boolean mShouldResetShortTermModel;
    public final boolean mSkipScreenOnBrightnessRamp;
    public boolean mStopped;
    public final String mTag;
    public final BrightnessEvent mTempBrightnessEvent;
    public float mTemporaryAutoBrightnessAdjustment;
    public String mUniqueDisplayId;
    public boolean mUseAutoBrightness;
    public boolean mUseSoftwareAutoBrightnessConfig;
    public final WakelockController mWakelockController;
    public final WindowManagerPolicy mWindowManagerPolicy;
    public final Object mLock = new Object();
    public int mLeadDisplayId = -1;
    @GuardedBy({"mCachedBrightnessInfo"})
    public final CachedBrightnessInfo mCachedBrightnessInfo = new CachedBrightnessInfo();
    public int mReportedScreenStateToPolicy = -1;
    public final BrightnessReason mBrightnessReason = new BrightnessReason();
    public final BrightnessReason mBrightnessReasonTemp = new BrightnessReason();
    public float mLastStatsBrightness = 0.0f;
    public int mSkipRampState = 0;
    @GuardedBy({"mLock"})
    public SparseArray<DisplayPowerControllerInterface> mDisplayBrightnessFollowers = new SparseArray<>();
    public final Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() { // from class: com.android.server.display.DisplayPowerController2.3
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
            DisplayPowerController2.this.sendUpdatePowerState();
        }
    };
    public final RampAnimator.Listener mRampAnimatorListener = new RampAnimator.Listener() { // from class: com.android.server.display.DisplayPowerController2.4
        @Override // com.android.server.display.RampAnimator.Listener
        public void onAnimationEnd() {
            DisplayPowerController2.this.sendUpdatePowerState();
            DisplayPowerController2.this.mHandler.sendMessageAtTime(DisplayPowerController2.this.mHandler.obtainMessage(10), DisplayPowerController2.this.mClock.uptimeMillis());
        }
    };
    public final Runnable mCleanListener = new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            DisplayPowerController2.this.sendUpdatePowerState();
        }
    };

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface Clock {
        long uptimeMillis();
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x01c5  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x01de  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public DisplayPowerController2(Context context, Injector injector, DisplayManagerInternal.DisplayPowerCallbacks displayPowerCallbacks, Handler handler, SensorManager sensorManager, DisplayBlanker displayBlanker, LogicalDisplay logicalDisplay, BrightnessTracker brightnessTracker, BrightnessSetting brightnessSetting, Runnable runnable, HighBrightnessModeMetadata highBrightnessModeMetadata, boolean z) {
        DisplayWhiteBalanceSettings displayWhiteBalanceSettings;
        DisplayWhiteBalanceController displayWhiteBalanceController;
        Injector injector2 = injector != null ? injector : new Injector();
        this.mInjector = injector2;
        this.mClock = injector2.getClock();
        this.mLogicalDisplay = logicalDisplay;
        int displayIdLocked = logicalDisplay.getDisplayIdLocked();
        this.mDisplayId = displayIdLocked;
        this.mSensorManager = sensorManager;
        DisplayControllerHandler displayControllerHandler = new DisplayControllerHandler(handler.getLooper());
        this.mHandler = displayControllerHandler;
        this.mDisplayDeviceConfig = logicalDisplay.getPrimaryDisplayDeviceLocked().getDisplayDeviceConfig();
        this.mIsEnabled = logicalDisplay.isEnabledLocked();
        this.mIsInTransition = logicalDisplay.isInTransitionLocked();
        WakelockController wakelockController = injector2.getWakelockController(displayIdLocked, displayPowerCallbacks);
        this.mWakelockController = wakelockController;
        DisplayPowerProximityStateController displayPowerProximityStateController = injector2.getDisplayPowerProximityStateController(wakelockController, this.mDisplayDeviceConfig, displayControllerHandler.getLooper(), new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController2.this.lambda$new$0();
            }
        }, displayIdLocked, sensorManager);
        this.mDisplayPowerProximityStateController = displayPowerProximityStateController;
        this.mHighBrightnessModeMetadata = highBrightnessModeMetadata;
        this.mDisplayStateController = new DisplayStateController(displayPowerProximityStateController);
        this.mTag = "DisplayPowerController2[" + displayIdLocked + "]";
        this.mBrightnessThrottlingDataId = logicalDisplay.getBrightnessThrottlingDataIdLocked();
        this.mDisplayDevice = logicalDisplay.getPrimaryDisplayDeviceLocked();
        String uniqueId = logicalDisplay.getPrimaryDisplayDeviceLocked().getUniqueId();
        this.mUniqueDisplayId = uniqueId;
        this.mDisplayStatsId = uniqueId.hashCode();
        this.mLastBrightnessEvent = new BrightnessEvent(displayIdLocked);
        this.mTempBrightnessEvent = new BrightnessEvent(displayIdLocked);
        if (displayIdLocked == 0) {
            this.mBatteryStats = BatteryStatsService.getService();
        } else {
            this.mBatteryStats = null;
        }
        this.mSettingsObserver = new SettingsObserver(displayControllerHandler);
        this.mWindowManagerPolicy = (WindowManagerPolicy) LocalServices.getService(WindowManagerPolicy.class);
        this.mBlanker = displayBlanker;
        this.mContext = context;
        this.mBrightnessTracker = brightnessTracker;
        this.mOnBrightnessChangeRunnable = runnable;
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        Resources resources = context.getResources();
        this.mScreenBrightnessDozeConfig = com.android.server.display.brightness.BrightnessUtils.clampAbsoluteBrightness(powerManager.getBrightnessConstraint(4));
        this.mScreenBrightnessDimConfig = com.android.server.display.brightness.BrightnessUtils.clampAbsoluteBrightness(powerManager.getBrightnessConstraint(3));
        this.mScreenBrightnessMinimumDimAmount = resources.getFloat(17105106);
        loadBrightnessRampRates();
        this.mSkipScreenOnBrightnessRamp = resources.getBoolean(17891798);
        this.mHbmController = createHbmControllerLocked();
        this.mBrightnessThrottler = createBrightnessThrottlerLocked();
        this.mDisplayBrightnessController = new DisplayBrightnessController(context, null, displayIdLocked, logicalDisplay.getDisplayInfoLocked().brightnessDefault, brightnessSetting, new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController2.this.lambda$new$1();
            }
        }, new HandlerExecutor(displayControllerHandler));
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
            } catch (Exception e2) {
                e = e2;
                displayWhiteBalanceSettings = null;
                displayWhiteBalanceController = null;
            }
            try {
                displayWhiteBalanceSettings.setCallbacks(this);
                displayWhiteBalanceController.setCallbacks(this);
            } catch (Exception e3) {
                e = e3;
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
                this.mAutoBrightnessAdjustment = getAutoBrightnessAdjustmentSetting();
                this.mTemporaryAutoBrightnessAdjustment = Float.NaN;
                this.mPendingAutoBrightnessAdjustment = Float.NaN;
                this.mBootCompleted = z;
            }
        } else {
            displayWhiteBalanceSettings = null;
            displayWhiteBalanceController = null;
        }
        this.mDisplayWhiteBalanceSettings = displayWhiteBalanceSettings;
        this.mDisplayWhiteBalanceController = displayWhiteBalanceController;
        loadNitsRange(resources);
        if (this.mDisplayId != 0) {
            ColorDisplayService.ColorDisplayServiceInternal colorDisplayServiceInternal = (ColorDisplayService.ColorDisplayServiceInternal) LocalServices.getService(ColorDisplayService.ColorDisplayServiceInternal.class);
            this.mCdsi = colorDisplayServiceInternal;
            if (colorDisplayServiceInternal.setReduceBrightColorsListener(new ColorDisplayService.ReduceBrightColorsListener() { // from class: com.android.server.display.DisplayPowerController2.1
                @Override // com.android.server.display.color.ColorDisplayService.ReduceBrightColorsListener
                public void onReduceBrightColorsActivationChanged(boolean z2, boolean z3) {
                    DisplayPowerController2.this.applyReduceBrightColorsSplineAdjustment();
                }

                @Override // com.android.server.display.color.ColorDisplayService.ReduceBrightColorsListener
                public void onReduceBrightColorsStrengthChanged(int i) {
                    DisplayPowerController2.this.applyReduceBrightColorsSplineAdjustment();
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
        this.mAutoBrightnessAdjustment = getAutoBrightnessAdjustmentSetting();
        this.mTemporaryAutoBrightnessAdjustment = Float.NaN;
        this.mPendingAutoBrightnessAdjustment = Float.NaN;
        this.mBootCompleted = z;
    }

    public final void applyReduceBrightColorsSplineAdjustment() {
        this.mHandler.obtainMessage(9).sendToTarget();
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
        return this.mDisplayPowerProximityStateController.isProximitySensorAvailable();
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
        this.mHandler.sendMessage(this.mHandler.obtainMessage(12, Integer.valueOf(i)));
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

    /* JADX WARN: Removed duplicated region for block: B:16:0x002b A[Catch: all -> 0x003b, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x0008, B:8:0x000a, B:10:0x0014, B:16:0x002b, B:18:0x0032, B:19:0x0037, B:20:0x0039, B:12:0x001d, B:14:0x0023), top: B:25:0x0003 }] */
    @Override // com.android.server.display.DisplayPowerControllerInterface
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean requestPowerState(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest, boolean z) {
        synchronized (this.mLock) {
            if (this.mStopped) {
                return true;
            }
            boolean pendingWaitForNegativeProximityLocked = this.mDisplayPowerProximityStateController.setPendingWaitForNegativeProximityLocked(z);
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest2 = this.mPendingRequestLocked;
            if (displayPowerRequest2 == null) {
                this.mPendingRequestLocked = new DisplayManagerInternal.DisplayPowerRequest(displayPowerRequest);
            } else {
                if (!displayPowerRequest2.equals(displayPowerRequest)) {
                    this.mPendingRequestLocked.copyFrom(displayPowerRequest);
                }
                if (pendingWaitForNegativeProximityLocked) {
                    this.mDisplayReadyLocked = false;
                    if (!this.mPendingRequestChangedLocked) {
                        this.mPendingRequestChangedLocked = true;
                        sendUpdatePowerStateLocked();
                    }
                }
                return this.mDisplayReadyLocked;
            }
            pendingWaitForNegativeProximityLocked = true;
            if (pendingWaitForNegativeProximityLocked) {
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
            Slog.wtf(str, "Display Device is null in DisplayPowerController2 for display: " + this.mLogicalDisplay.getDisplayIdLocked());
            return;
        }
        final String uniqueId = primaryDisplayDeviceLocked.getUniqueId();
        final DisplayDeviceConfig displayDeviceConfig = primaryDisplayDeviceLocked.getDisplayDeviceConfig();
        final IBinder displayTokenLocked = primaryDisplayDeviceLocked.getDisplayTokenLocked();
        final DisplayDeviceInfo displayDeviceInfoLocked = primaryDisplayDeviceLocked.getDisplayDeviceInfoLocked();
        final boolean isEnabledLocked = this.mLogicalDisplay.isEnabledLocked();
        final boolean isInTransitionLocked = this.mLogicalDisplay.isInTransitionLocked();
        final String brightnessThrottlingDataIdLocked = this.mLogicalDisplay.getBrightnessThrottlingDataIdLocked();
        this.mHandler.postAtTime(new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController2.this.lambda$onDisplayChanged$2(primaryDisplayDeviceLocked, uniqueId, displayDeviceConfig, brightnessThrottlingDataIdLocked, displayTokenLocked, displayDeviceInfoLocked, highBrightnessModeMetadata, isEnabledLocked, isInTransitionLocked);
            }
        }, this.mClock.uptimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:18:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:20:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public /* synthetic */ void lambda$onDisplayChanged$2(DisplayDevice displayDevice, String str, DisplayDeviceConfig displayDeviceConfig, String str2, IBinder iBinder, DisplayDeviceInfo displayDeviceInfo, HighBrightnessModeMetadata highBrightnessModeMetadata, boolean z, boolean z2) {
        boolean z3;
        boolean z4 = true;
        if (this.mDisplayDevice != displayDevice) {
            this.mDisplayDevice = displayDevice;
            this.mUniqueDisplayId = str;
            this.mDisplayStatsId = str.hashCode();
            this.mDisplayDeviceConfig = displayDeviceConfig;
            this.mBrightnessThrottlingDataId = str2;
            loadFromDisplayDeviceConfig(iBinder, displayDeviceInfo, highBrightnessModeMetadata);
            this.mDisplayPowerProximityStateController.notifyDisplayDeviceChanged(displayDeviceConfig);
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
            lambda$new$0();
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
            this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(7), this.mClock.uptimeMillis());
            DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
            if (displayWhiteBalanceController != null) {
                displayWhiteBalanceController.setEnabled(false);
            }
            AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
            if (automaticBrightnessController != null) {
                automaticBrightnessController.stop();
            }
            this.mDisplayBrightnessController.stop();
            this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        }
    }

    public final void loadFromDisplayDeviceConfig(IBinder iBinder, DisplayDeviceInfo displayDeviceInfo, HighBrightnessModeMetadata highBrightnessModeMetadata) {
        loadBrightnessRampRates();
        loadNitsRange(this.mContext.getResources());
        setUpAutoBrightness(this.mContext.getResources(), this.mHandler);
        reloadReduceBrightColours();
        RampAnimator.DualRampAnimator<DisplayPowerState> dualRampAnimator = this.mScreenBrightnessRampAnimator;
        if (dualRampAnimator != null) {
            dualRampAnimator.setAnimationTimeLimits(this.mBrightnessRampIncreaseMaxTimeMillis, this.mBrightnessRampDecreaseMaxTimeMillis);
        }
        this.mHbmController.setHighBrightnessModeMetadata(highBrightnessModeMetadata);
        this.mHbmController.resetHbmData(displayDeviceInfo.width, displayDeviceInfo.height, iBinder, displayDeviceInfo.uniqueId, this.mDisplayDeviceConfig.getHighBrightnessModeData(), new HighBrightnessModeController.HdrBrightnessDeviceConfig() { // from class: com.android.server.display.DisplayPowerController2.2
            @Override // com.android.server.display.HighBrightnessModeController.HdrBrightnessDeviceConfig
            public float getHdrBrightnessFromSdr(float f) {
                return DisplayPowerController2.this.mDisplayDeviceConfig.getHdrBrightnessFromSdr(f);
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
        float convertToNits = this.mDisplayBrightnessController.convertToNits(this.mPowerState.getScreenBrightness());
        BrightnessTracker brightnessTracker = this.mBrightnessTracker;
        if (brightnessTracker != null && convertToNits >= 0.0f) {
            brightnessTracker.start(convertToNits);
        }
        this.mDisplayBrightnessController.registerBrightnessSettingChangeListener(new BrightnessSetting.BrightnessSettingListener() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda6
            @Override // com.android.server.display.BrightnessSetting.BrightnessSettingListener
            public final void onBrightnessChanged(float f) {
                DisplayPowerController2.this.lambda$initialize$3(f);
            }
        });
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_auto_brightness_adj"), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, this.mSettingsObserver, -1);
        handleBrightnessModeChange();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initialize$3(float f) {
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(8, Float.valueOf(f)), this.mClock.uptimeMillis());
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
                AutomaticBrightnessController automaticBrightnessController2 = this.mInjector.getAutomaticBrightnessController(this, handler.getLooper(), this.mSensorManager, this.mLightSensor, this.mInteractiveModeBrightnessMapper, integer, 0.0f, 1.0f, fraction, integer2, i, autoBrightnessBrighteningLightDebounce, autoBrightnessDarkeningLightDebounce, z2, hysteresisLevels, hysteresisLevels2, hysteresisLevels3, hysteresisLevels4, this.mContext, this.mHbmController, this.mBrightnessThrottler, this.mIdleModeBrightnessMapper, this.mDisplayDeviceConfig.getAmbientHorizonShort(), this.mDisplayDeviceConfig.getAmbientHorizonLong(), f, f2);
                this.mAutomaticBrightnessController = automaticBrightnessController2;
                this.mDisplayBrightnessController.setAutomaticBrightnessController(automaticBrightnessController2);
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
        this.mDisplayPowerProximityStateController.cleanup();
        this.mHbmController.stop();
        this.mBrightnessThrottler.stop();
        this.mHandler.removeCallbacksAndMessages(null);
        this.mWakelockController.releaseAll();
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

    /* renamed from: updatePowerState */
    public final void lambda$new$0() {
        Trace.traceBegin(131072L, "DisplayPowerController#updatePowerState");
        updatePowerStateInternal();
        Trace.traceEnd(131072L);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:381:0x0643  */
    /* JADX WARN: Removed duplicated region for block: B:397:0x0663  */
    /* JADX WARN: Removed duplicated region for block: B:400:0x066b  */
    /* JADX WARN: Removed duplicated region for block: B:401:0x066d  */
    /* JADX WARN: Removed duplicated region for block: B:404:0x0678  */
    /* JADX WARN: Removed duplicated region for block: B:413:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r12v11 */
    /* JADX WARN: Type inference failed for: r12v3 */
    /* JADX WARN: Type inference failed for: r12v4 */
    /* JADX WARN: Type inference failed for: r12v5 */
    /* JADX WARN: Type inference failed for: r12v6 */
    /* JADX WARN: Type inference failed for: r12v9 */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v25 */
    /* JADX WARN: Type inference failed for: r13v3 */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v31 */
    /* JADX WARN: Type inference failed for: r4v10 */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updatePowerStateInternal() {
        int i;
        ?? r3;
        float f;
        int i2;
        float f2;
        boolean z;
        float clampScreenBrightness;
        float f3;
        boolean z2;
        int i3;
        boolean z3;
        float f4;
        int i4;
        boolean saveBrightnessInfo;
        boolean z4;
        boolean z5;
        boolean z6;
        boolean z7;
        int i5;
        int i6;
        boolean z8;
        float clampScreenBrightness2;
        float hdrBrightnessValue;
        AutomaticBrightnessController automaticBrightnessController;
        AutomaticBrightnessController automaticBrightnessController2;
        int i7;
        float f5;
        boolean z9;
        boolean z10;
        boolean z11;
        ScreenOffBrightnessSensorController screenOffBrightnessSensorController;
        float f6;
        int i8;
        boolean z12;
        this.mBrightnessReasonTemp.set(null);
        this.mTempBrightnessEvent.reset();
        synchronized (this.mLock) {
            if (this.mStopped) {
                return;
            }
            this.mPendingUpdatePowerStateLocked = false;
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest = this.mPendingRequestLocked;
            if (displayPowerRequest == null) {
                return;
            }
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest2 = this.mPowerRequest;
            if (displayPowerRequest2 == null) {
                this.mPowerRequest = new DisplayManagerInternal.DisplayPowerRequest(this.mPendingRequestLocked);
                this.mDisplayPowerProximityStateController.updatePendingProximityRequestsLocked();
                this.mPendingRequestChangedLocked = false;
                i = 3;
                r3 = true;
            } else {
                if (this.mPendingRequestChangedLocked) {
                    i = displayPowerRequest2.policy;
                    displayPowerRequest2.copyFrom(displayPowerRequest);
                    this.mDisplayPowerProximityStateController.updatePendingProximityRequestsLocked();
                    this.mPendingRequestChangedLocked = false;
                    this.mDisplayReadyLocked = false;
                } else {
                    i = displayPowerRequest2.policy;
                }
                r3 = false;
            }
            ?? r4 = !this.mDisplayReadyLocked;
            int updateDisplayState = this.mDisplayStateController.updateDisplayState(this.mPowerRequest, this.mIsEnabled, this.mIsInTransition);
            ScreenOffBrightnessSensorController screenOffBrightnessSensorController2 = this.mScreenOffBrightnessSensorController;
            if (screenOffBrightnessSensorController2 != null) {
                screenOffBrightnessSensorController2.setLightSensorEnabled(this.mUseAutoBrightness && this.mIsEnabled && (updateDisplayState == 1 || (updateDisplayState == 3 && !this.mDisplayBrightnessController.isAllowAutoBrightnessWhileDozingConfig())) && this.mLeadDisplayId == -1);
            }
            if (r3 != false) {
                initialize(readyToUpdateDisplayState() ? updateDisplayState : 0);
            }
            this.mPowerState.getScreenState();
            animateScreenStateChange(updateDisplayState, this.mDisplayStateController.shouldPerformScreenOffTransition());
            int screenState = this.mPowerState.getScreenState();
            DisplayBrightnessState updateBrightness = this.mDisplayBrightnessController.updateBrightness(this.mPowerRequest, screenState);
            float brightness = updateBrightness.getBrightness();
            float brightness2 = updateBrightness.getBrightness();
            this.mBrightnessReasonTemp.set(updateBrightness.getBrightnessReason());
            ?? r12 = this.mDisplayBrightnessController.isAllowAutoBrightnessWhileDozingConfig() && Display.isDozeState(screenState);
            ?? r13 = this.mUseAutoBrightness && (screenState == 2 || r12 == true) && !((!Float.isNaN(brightness) && this.mBrightnessReasonTemp.getReason() != 7 && this.mBrightnessReasonTemp.getReason() != 8) || this.mAutomaticBrightnessController == null || this.mBrightnessReasonTemp.getReason() == 10);
            int i9 = r13 != false ? 1 : (this.mUseAutoBrightness && screenState != 2 && r12 == false) != false ? 3 : 2;
            boolean updateUserSetScreenBrightness = this.mDisplayBrightnessController.updateUserSetScreenBrightness();
            boolean updateAutoBrightnessAdjustment = updateAutoBrightnessAdjustment();
            if (!Float.isNaN(this.mTemporaryAutoBrightnessAdjustment)) {
                f = this.mTemporaryAutoBrightnessAdjustment;
                this.mAppliedTemporaryAutoBrightnessAdjustment = true;
                i2 = 1;
            } else {
                f = this.mAutoBrightnessAdjustment;
                this.mAppliedTemporaryAutoBrightnessAdjustment = false;
                i2 = 2;
            }
            boolean z13 = Float.isNaN(brightness) && (updateAutoBrightnessAdjustment || updateUserSetScreenBrightness);
            AutomaticBrightnessController automaticBrightnessController3 = this.mAutomaticBrightnessController;
            if (automaticBrightnessController3 != null) {
                z = automaticBrightnessController3.hasUserDataPoints();
                f2 = brightness2;
                this.mAutomaticBrightnessController.configure(i9, this.mBrightnessConfiguration, this.mDisplayBrightnessController.getLastUserSetScreenBrightness(), updateUserSetScreenBrightness, f, updateAutoBrightnessAdjustment, this.mPowerRequest.policy, this.mShouldResetShortTermModel);
                this.mShouldResetShortTermModel = false;
            } else {
                f2 = brightness2;
                z = false;
            }
            this.mHbmController.setAutoBrightnessEnabled(this.mUseAutoBrightness ? 1 : 2);
            BrightnessTracker brightnessTracker = this.mBrightnessTracker;
            if (brightnessTracker != null) {
                BrightnessConfiguration brightnessConfiguration = this.mBrightnessConfiguration;
                brightnessTracker.setShouldCollectColorSample(brightnessConfiguration != null && brightnessConfiguration.shouldCollectColorSamples());
            }
            float currentBrightness = this.mDisplayBrightnessController.getCurrentBrightness();
            if (Float.isNaN(brightness)) {
                if (r13 == true) {
                    f3 = this.mAutomaticBrightnessController.getRawAutomaticScreenBrightness();
                    brightness = this.mAutomaticBrightnessController.getAutomaticScreenBrightness(this.mTempBrightnessEvent);
                    f6 = this.mAutomaticBrightnessController.getAutomaticScreenBrightnessAdjustment();
                } else {
                    f6 = f;
                    f3 = f2;
                }
                if (com.android.server.display.brightness.BrightnessUtils.isValidBrightnessValue(brightness) || brightness == -1.0f) {
                    float clampScreenBrightness3 = clampScreenBrightness(brightness);
                    z3 = this.mAppliedAutoBrightness && !updateAutoBrightnessAdjustment;
                    boolean z14 = currentBrightness != clampScreenBrightness3;
                    this.mAppliedAutoBrightness = true;
                    clampScreenBrightness = clampScreenBrightness3;
                    this.mBrightnessReasonTemp.setReason(4);
                    ScreenOffBrightnessSensorController screenOffBrightnessSensorController3 = this.mScreenOffBrightnessSensorController;
                    i8 = 0;
                    if (screenOffBrightnessSensorController3 != null) {
                        screenOffBrightnessSensorController3.setLightSensorEnabled(false);
                    }
                    z12 = z14;
                } else {
                    this.mAppliedAutoBrightness = false;
                    clampScreenBrightness = brightness;
                    i8 = 0;
                    z3 = false;
                    z12 = false;
                }
                if (f != f6) {
                    putAutoBrightnessAdjustmentSetting(f6);
                } else {
                    i2 = i8;
                }
                i3 = i2;
                z2 = z12;
            } else {
                clampScreenBrightness = clampScreenBrightness(brightness);
                this.mAppliedAutoBrightness = false;
                f3 = f2;
                z2 = false;
                i3 = 0;
                z3 = false;
            }
            if (Float.isNaN(clampScreenBrightness) && Display.isDozeState(screenState)) {
                f3 = this.mScreenBrightnessDozeConfig;
                clampScreenBrightness = clampScreenBrightness(f3);
                this.mBrightnessReasonTemp.setReason(3);
            }
            if (Float.isNaN(clampScreenBrightness) && r13 != false && (screenOffBrightnessSensorController = this.mScreenOffBrightnessSensorController) != null) {
                f3 = screenOffBrightnessSensorController.getAutomaticScreenBrightness();
                if (com.android.server.display.brightness.BrightnessUtils.isValidBrightnessValue(f3)) {
                    clampScreenBrightness = clampScreenBrightness(f3);
                    z2 = this.mDisplayBrightnessController.getCurrentBrightness() != clampScreenBrightness;
                    this.mBrightnessReasonTemp.setReason(9);
                } else {
                    clampScreenBrightness = f3;
                }
            }
            if (Float.isNaN(clampScreenBrightness)) {
                clampScreenBrightness = clampScreenBrightness(currentBrightness);
                if (clampScreenBrightness != currentBrightness) {
                    z2 = true;
                }
                this.mBrightnessReasonTemp.setReason(1);
            } else {
                currentBrightness = f3;
            }
            float f7 = clampScreenBrightness;
            if (this.mBrightnessThrottler.isThrottled()) {
                this.mTempBrightnessEvent.setThermalMax(this.mBrightnessThrottler.getBrightnessCap());
                f4 = Math.min(f7, this.mBrightnessThrottler.getBrightnessCap());
                this.mBrightnessReasonTemp.addModifier(8);
                if (this.mAppliedThrottling) {
                    z11 = true;
                } else {
                    z11 = true;
                    z3 = false;
                }
                this.mAppliedThrottling = z11;
            } else {
                if (this.mAppliedThrottling) {
                    this.mAppliedThrottling = false;
                }
                f4 = f7;
            }
            AutomaticBrightnessController automaticBrightnessController4 = this.mAutomaticBrightnessController;
            float ambientLux = automaticBrightnessController4 == null ? 0.0f : automaticBrightnessController4.getAmbientLux();
            int i10 = 0;
            for (SparseArray<DisplayPowerControllerInterface> clone = this.mDisplayBrightnessFollowers.clone(); i10 < clone.size(); clone = clone) {
                clone.valueAt(i10).setBrightnessToFollow(currentBrightness, this.mDisplayBrightnessController.convertToNits(currentBrightness), ambientLux);
                i10++;
            }
            if (z2) {
                this.mDisplayBrightnessController.updateScreenBrightnessSetting(f4);
            }
            if (this.mPowerRequest.policy == 2) {
                if (f4 > 0.0f) {
                    float max = Math.max(Math.min(f4 - this.mScreenBrightnessMinimumDimAmount, this.mScreenBrightnessDimConfig), 0.0f);
                    z10 = true;
                    this.mBrightnessReasonTemp.addModifier(1);
                    f4 = max;
                } else {
                    z10 = true;
                }
                if (!this.mAppliedDimming) {
                    z3 = false;
                }
                this.mAppliedDimming = z10;
            } else if (this.mAppliedDimming) {
                this.mAppliedDimming = false;
                z3 = false;
            }
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest3 = this.mPowerRequest;
            if (displayPowerRequest3.lowPowerMode) {
                if (f4 > 0.0f) {
                    float max2 = Math.max(f4 * Math.min(displayPowerRequest3.screenLowPowerBrightnessFactor, 1.0f), 0.0f);
                    this.mBrightnessReasonTemp.addModifier(2);
                    f4 = max2;
                }
                if (this.mAppliedLowPower) {
                    z9 = true;
                } else {
                    z9 = true;
                    z3 = false;
                }
                this.mAppliedLowPower = z9;
            } else if (this.mAppliedLowPower) {
                this.mAppliedLowPower = false;
                z3 = false;
            }
            this.mHbmController.onBrightnessChanged(f4, f7, this.mBrightnessThrottler.getBrightnessMaxReason());
            boolean z15 = this.mBrightnessReason.getReason() == 7 || this.mAppliedTemporaryAutoBrightnessAdjustment;
            if (!this.mPendingScreenOff) {
                if (this.mSkipScreenOnBrightnessRamp) {
                    i6 = 2;
                    if (screenState == 2) {
                        int i11 = this.mSkipRampState;
                        if (i11 == 0 && this.mDozing) {
                            this.mInitialAutoBrightness = f4;
                            this.mSkipRampState = 1;
                        } else if (i11 == 1 && this.mUseSoftwareAutoBrightnessConfig && !BrightnessSynchronizer.floatEquals(f4, this.mInitialAutoBrightness)) {
                            i6 = 2;
                            this.mSkipRampState = 2;
                        } else {
                            i6 = 2;
                            if (this.mSkipRampState == 2) {
                                this.mSkipRampState = 0;
                            }
                        }
                    } else {
                        this.mSkipRampState = 0;
                    }
                    z8 = !(screenState == i6 || this.mSkipRampState == 0) || this.mDisplayPowerProximityStateController.shouldSkipRampBecauseOfProximityChangeToNegative();
                    boolean z16 = !Display.isDozeState(screenState) && this.mBrightnessBucketsInDozeConfig;
                    boolean z17 = !this.mColorFadeEnabled && this.mPowerState.getColorFadeLevel() == 1.0f;
                    clampScreenBrightness2 = clampScreenBrightness(f4);
                    hdrBrightnessValue = (this.mHbmController.getHighBrightnessMode() != 2 && (this.mBrightnessReason.getModifier() & 1) == 0 && (this.mBrightnessReason.getModifier() & 2) == 0) ? this.mHbmController.getHdrBrightnessValue() : clampScreenBrightness2;
                    float screenBrightness = this.mPowerState.getScreenBrightness();
                    i4 = i;
                    float sdrScreenBrightness = this.mPowerState.getSdrScreenBrightness();
                    if (com.android.server.display.brightness.BrightnessUtils.isValidBrightnessValue(hdrBrightnessValue) && (hdrBrightnessValue != screenBrightness || clampScreenBrightness2 != sdrScreenBrightness)) {
                        if (!z8 || z16 || !z17 || z15) {
                            animateScreenBrightness(hdrBrightnessValue, clampScreenBrightness2, 0.0f);
                        } else {
                            boolean z18 = i7 > 0;
                            if (z18 && z3) {
                                f5 = this.mBrightnessRampRateSlowIncrease;
                            } else if (z18 && !z3) {
                                f5 = this.mBrightnessRampRateFastIncrease;
                            } else if (!z18 && z3) {
                                f5 = this.mBrightnessRampRateSlowDecrease;
                            } else {
                                f5 = this.mBrightnessRampRateFastDecrease;
                            }
                            animateScreenBrightness(hdrBrightnessValue, clampScreenBrightness2, f5);
                        }
                    }
                    if (!z15 && (automaticBrightnessController = this.mAutomaticBrightnessController) != null && !automaticBrightnessController.isInIdleMode()) {
                        notifyBrightnessTrackerChanged(f4, (z13 || ((automaticBrightnessController2 = this.mAutomaticBrightnessController) != null && automaticBrightnessController2.hasValidAmbientLux())) ? z13 : false, z);
                    }
                    saveBrightnessInfo = saveBrightnessInfo(getScreenBrightnessSetting(), hdrBrightnessValue);
                }
                i6 = 2;
                if (screenState == i6) {
                }
                if (Display.isDozeState(screenState)) {
                }
                if (this.mColorFadeEnabled) {
                }
                clampScreenBrightness2 = clampScreenBrightness(f4);
                if (this.mHbmController.getHighBrightnessMode() != 2) {
                }
                float screenBrightness2 = this.mPowerState.getScreenBrightness();
                i4 = i;
                float sdrScreenBrightness2 = this.mPowerState.getSdrScreenBrightness();
                if (com.android.server.display.brightness.BrightnessUtils.isValidBrightnessValue(hdrBrightnessValue)) {
                    if (!z8) {
                    }
                    animateScreenBrightness(hdrBrightnessValue, clampScreenBrightness2, 0.0f);
                }
                if (!z15) {
                    notifyBrightnessTrackerChanged(f4, (z13 || ((automaticBrightnessController2 = this.mAutomaticBrightnessController) != null && automaticBrightnessController2.hasValidAmbientLux())) ? z13 : false, z);
                }
                saveBrightnessInfo = saveBrightnessInfo(getScreenBrightnessSetting(), hdrBrightnessValue);
            } else {
                i4 = i;
                saveBrightnessInfo = saveBrightnessInfo(getScreenBrightnessSetting());
            }
            if (saveBrightnessInfo && !z15) {
                lambda$new$1();
            }
            if (!this.mBrightnessReasonTemp.equals(this.mBrightnessReason) || i3 != 0) {
                Slog.v(this.mTag, "Brightness [" + f4 + "] reason changing to: '" + this.mBrightnessReasonTemp.toString(i3) + "', previous reason: '" + this.mBrightnessReason + "'.");
                this.mBrightnessReason.set(this.mBrightnessReasonTemp);
            } else if (this.mBrightnessReasonTemp.getReason() == 1 && updateUserSetScreenBrightness) {
                Slog.v(this.mTag, "Brightness [" + f4 + "] manual adjustment.");
            }
            this.mTempBrightnessEvent.setTime(System.currentTimeMillis());
            this.mTempBrightnessEvent.setBrightness(f4);
            this.mTempBrightnessEvent.setPhysicalDisplayId(this.mUniqueDisplayId);
            this.mTempBrightnessEvent.setReason(this.mBrightnessReason);
            this.mTempBrightnessEvent.setHbmMax(this.mHbmController.getCurrentBrightnessMax());
            this.mTempBrightnessEvent.setHbmMode(this.mHbmController.getHighBrightnessMode());
            BrightnessEvent brightnessEvent = this.mTempBrightnessEvent;
            brightnessEvent.setFlags(brightnessEvent.getFlags() | (this.mIsRbcActive ? 1 : 0) | (this.mPowerRequest.lowPowerMode ? 32 : 0));
            BrightnessEvent brightnessEvent2 = this.mTempBrightnessEvent;
            ColorDisplayService.ColorDisplayServiceInternal colorDisplayServiceInternal = this.mCdsi;
            brightnessEvent2.setRbcStrength(colorDisplayServiceInternal != null ? colorDisplayServiceInternal.getReduceBrightColorsStrength() : -1);
            this.mTempBrightnessEvent.setPowerFactor(this.mPowerRequest.screenLowPowerBrightnessFactor);
            this.mTempBrightnessEvent.setWasShortTermModelActive(z);
            this.mTempBrightnessEvent.setDisplayBrightnessStrategyName(updateBrightness.getDisplayBrightnessStrategyName());
            boolean z19 = this.mTempBrightnessEvent.getReason().getReason() == 7 && this.mLastBrightnessEvent.getReason().getReason() == 7;
            if ((!this.mTempBrightnessEvent.equalsMainData(this.mLastBrightnessEvent) && !z19) || i3 != 0) {
                this.mTempBrightnessEvent.setInitialBrightness(this.mLastBrightnessEvent.getBrightness());
                this.mTempBrightnessEvent.setAutomaticBrightnessEnabled(this.mUseAutoBrightness);
                this.mLastBrightnessEvent.copyFrom(this.mTempBrightnessEvent);
                BrightnessEvent brightnessEvent3 = new BrightnessEvent(this.mTempBrightnessEvent);
                brightnessEvent3.setAdjustmentFlags(i3);
                brightnessEvent3.setFlags(brightnessEvent3.getFlags() | (updateUserSetScreenBrightness ? 8 : 0));
                Slog.i(this.mTag, brightnessEvent3.toString(false));
                if (updateUserSetScreenBrightness) {
                    logManualBrightnessEvent(brightnessEvent3);
                }
                RingBuffer<BrightnessEvent> ringBuffer = this.mBrightnessEventRingBuffer;
                if (ringBuffer != null) {
                    ringBuffer.append(brightnessEvent3);
                }
            }
            if (this.mDisplayWhiteBalanceController != null) {
                if (screenState == 2 && this.mDisplayWhiteBalanceSettings.isEnabled()) {
                    this.mDisplayWhiteBalanceController.setEnabled(true);
                    this.mDisplayWhiteBalanceController.updateDisplayColorTemperature();
                } else {
                    z4 = false;
                    this.mDisplayWhiteBalanceController.setEnabled(false);
                    z5 = (this.mPendingScreenOnUnblocker == null || (this.mColorFadeEnabled && (this.mColorFadeOnAnimator.isStarted() || this.mColorFadeOffAnimator.isStarted())) || !this.mPowerState.waitUntilClean(this.mCleanListener)) ? z4 : true;
                    z6 = (z5 || this.mScreenBrightnessRampAnimator.isAnimating()) ? z4 : true;
                    if (z5 && screenState != 1 && this.mReportedScreenStateToPolicy == 1) {
                        setReportedScreenState(2);
                        this.mWindowManagerPolicy.screenTurnedOn(this.mDisplayId);
                    }
                    if (!z6) {
                        this.mWakelockController.acquireWakelock(5);
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
                    if (z6) {
                        this.mWakelockController.releaseWakelock(5);
                    }
                    this.mDozing = screenState == 2 ? z7 : z4;
                    i5 = this.mPowerRequest.policy;
                    if (i4 == i5) {
                        logDisplayPolicyChanged(i5);
                        return;
                    }
                    return;
                }
            }
            z4 = false;
            if (this.mPendingScreenOnUnblocker == null) {
            }
            if (z5) {
            }
            if (z5) {
                setReportedScreenState(2);
                this.mWindowManagerPolicy.screenTurnedOn(this.mDisplayId);
            }
            if (!z6) {
            }
            if (z5) {
            }
            z7 = true;
            if (z6) {
            }
            this.mDozing = screenState == 2 ? z7 : z4;
            i5 = this.mPowerRequest.policy;
            if (i4 == i5) {
            }
        }
    }

    @Override // com.android.server.display.AutomaticBrightnessController.Callbacks
    public void updateBrightness() {
        sendUpdatePowerState();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void ignoreProximitySensorUntilChanged() {
        this.mDisplayPowerProximityStateController.ignoreProximitySensorUntilChanged();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setBrightnessConfiguration(BrightnessConfiguration brightnessConfiguration, boolean z) {
        this.mHandler.obtainMessage(4, z ? 1 : 0, 0, brightnessConfiguration).sendToTarget();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setTemporaryBrightness(float f) {
        this.mHandler.obtainMessage(5, Float.floatToIntBits(f), 0).sendToTarget();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setTemporaryAutoBrightnessAdjustment(float f) {
        this.mHandler.obtainMessage(6, Float.floatToIntBits(f), 0).sendToTarget();
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

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void onBootCompleted() {
        this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(13), this.mClock.uptimeMillis());
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

    /* renamed from: postBrightnessChangeRunnable */
    public void lambda$new$1() {
        this.mHandler.post(this.mOnBrightnessChangeRunnable);
    }

    public final HighBrightnessModeController createHbmControllerLocked() {
        DisplayDevice primaryDisplayDeviceLocked = this.mLogicalDisplay.getPrimaryDisplayDeviceLocked();
        DisplayDeviceConfig displayDeviceConfig = primaryDisplayDeviceLocked.getDisplayDeviceConfig();
        IBinder displayTokenLocked = this.mLogicalDisplay.getPrimaryDisplayDeviceLocked().getDisplayTokenLocked();
        String uniqueId = this.mLogicalDisplay.getPrimaryDisplayDeviceLocked().getUniqueId();
        DisplayDeviceConfig.HighBrightnessModeData highBrightnessModeData = displayDeviceConfig != null ? displayDeviceConfig.getHighBrightnessModeData() : null;
        DisplayDeviceInfo displayDeviceInfoLocked = primaryDisplayDeviceLocked.getDisplayDeviceInfoLocked();
        return new HighBrightnessModeController(this.mHandler, displayDeviceInfoLocked.width, displayDeviceInfoLocked.height, displayTokenLocked, uniqueId, 0.0f, 1.0f, highBrightnessModeData, new HighBrightnessModeController.HdrBrightnessDeviceConfig() { // from class: com.android.server.display.DisplayPowerController2.5
            @Override // com.android.server.display.HighBrightnessModeController.HdrBrightnessDeviceConfig
            public float getHdrBrightnessFromSdr(float f) {
                return DisplayPowerController2.this.mDisplayDeviceConfig.getHdrBrightnessFromSdr(f);
            }
        }, new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController2.this.lambda$createHbmControllerLocked$4();
            }
        }, this.mHighBrightnessModeMetadata, this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createHbmControllerLocked$4() {
        sendUpdatePowerState();
        lambda$new$1();
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController != null) {
            automaticBrightnessController.update();
        }
    }

    public final BrightnessThrottler createBrightnessThrottlerLocked() {
        return new BrightnessThrottler(this.mHandler, this.mLogicalDisplay.getPrimaryDisplayDeviceLocked().getDisplayDeviceConfig().getBrightnessThrottlingData(this.mBrightnessThrottlingDataId), new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController2.this.lambda$createBrightnessThrottlerLocked$5();
            }
        }, this.mUniqueDisplayId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createBrightnessThrottlerLocked$5() {
        sendUpdatePowerState();
        lambda$new$1();
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
            if (z2 && !this.mDisplayPowerProximityStateController.isScreenOffBecauseOfProximity()) {
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
        if (z2 && this.mReportedScreenStateToPolicy != 0 && !this.mDisplayPowerProximityStateController.isScreenOffBecauseOfProximity()) {
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

    public final void sendOnStateChangedWithWakelock() {
        if (this.mWakelockController.acquireWakelock(4)) {
            this.mHandler.post(this.mWakelockController.getOnStateChangedRunnable());
        }
    }

    public final void logDisplayPolicyChanged(int i) {
        LogMaker logMaker = new LogMaker(1696);
        logMaker.setType(6);
        logMaker.setSubtype(i);
        MetricsLogger.action(logMaker);
    }

    public final void handleSettingsChange(boolean z) {
        DisplayBrightnessController displayBrightnessController = this.mDisplayBrightnessController;
        displayBrightnessController.setPendingScreenBrightness(displayBrightnessController.getScreenBrightnessSetting());
        this.mPendingAutoBrightnessAdjustment = getAutoBrightnessAdjustmentSetting();
        if (z) {
            DisplayBrightnessController displayBrightnessController2 = this.mDisplayBrightnessController;
            displayBrightnessController2.setAndNotifyCurrentScreenBrightness(displayBrightnessController2.getPendingScreenBrightness());
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
        this.mHandler.postAtTime(new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController2.this.lambda$handleBrightnessModeChange$6(intForUser);
            }
        }, this.mClock.uptimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleBrightnessModeChange$6(int i) {
        this.mUseAutoBrightness = i == 1;
        lambda$new$0();
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
        return this.mDisplayBrightnessController.getScreenBrightnessSetting();
    }

    @Override // com.android.server.display.DisplayPowerControllerInterface
    public void setBrightness(float f) {
        this.mDisplayBrightnessController.setBrightness(f);
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
            this.mDisplayBrightnessController.setBrightnessToFollow(Float.valueOf(f));
        } else {
            float convertToFloatScale = this.mDisplayBrightnessController.convertToFloatScale(f2);
            if (com.android.server.display.brightness.BrightnessUtils.isValidBrightnessValue(convertToFloatScale)) {
                this.mDisplayBrightnessController.setBrightnessToFollow(Float.valueOf(convertToFloatScale));
            } else {
                this.mDisplayBrightnessController.setBrightnessToFollow(Float.valueOf(f));
            }
        }
        sendUpdatePowerState();
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

    public final void notifyBrightnessTrackerChanged(float f, boolean z, boolean z2) {
        AutomaticBrightnessController automaticBrightnessController;
        BrightnessTracker brightnessTracker;
        float convertToNits = this.mDisplayBrightnessController.convertToNits(f);
        if (!this.mUseAutoBrightness || convertToNits < 0.0f || (automaticBrightnessController = this.mAutomaticBrightnessController) == null || (brightnessTracker = this.mBrightnessTracker) == null) {
            return;
        }
        DisplayManagerInternal.DisplayPowerRequest displayPowerRequest = this.mPowerRequest;
        brightnessTracker.notifyBrightnessChanged(convertToNits, z, displayPowerRequest.lowPowerMode ? displayPowerRequest.screenLowPowerBrightnessFactor : 1.0f, z2, automaticBrightnessController.isDefaultConfig(), this.mUniqueDisplayId, this.mAutomaticBrightnessController.getLastSensorValues(), this.mAutomaticBrightnessController.getLastSensorTimestamps());
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
            this.mHandler.postAtTime(new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    DisplayPowerControllerInterface.this.setBrightnessToFollow(Float.NaN, -1.0f, 0.0f);
                }
            }, this.mClock.uptimeMillis());
        }
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
            printWriter.println("  mPendingUpdatePowerStateLocked=" + this.mPendingUpdatePowerStateLocked);
        }
        printWriter.println();
        printWriter.println("Display Power Controller Configuration:");
        printWriter.println("  mScreenBrightnessDozeConfig=" + this.mScreenBrightnessDozeConfig);
        printWriter.println("  mScreenBrightnessDimConfig=" + this.mScreenBrightnessDimConfig);
        printWriter.println("  mUseSoftwareAutoBrightnessConfig=" + this.mUseSoftwareAutoBrightnessConfig);
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
        this.mHandler.runWithScissors(new Runnable() { // from class: com.android.server.display.DisplayPowerController2$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DisplayPowerController2.this.lambda$dump$8(printWriter);
            }
        }, 1000L);
    }

    /* renamed from: dumpLocal */
    public final void lambda$dump$8(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("Display Power Controller Thread State:");
        printWriter.println("  mPowerRequest=" + this.mPowerRequest);
        printWriter.println("  mAutoBrightnessAdjustment=" + this.mAutoBrightnessAdjustment);
        printWriter.println("  mBrightnessReason=" + this.mBrightnessReason);
        printWriter.println("  mTemporaryAutoBrightnessAdjustment=" + this.mTemporaryAutoBrightnessAdjustment);
        printWriter.println("  mPendingAutoBrightnessAdjustment=" + this.mPendingAutoBrightnessAdjustment);
        printWriter.println("  mAppliedAutoBrightness=" + this.mAppliedAutoBrightness);
        printWriter.println("  mAppliedDimming=" + this.mAppliedDimming);
        printWriter.println("  mAppliedLowPower=" + this.mAppliedLowPower);
        printWriter.println("  mAppliedThrottling=" + this.mAppliedThrottling);
        printWriter.println("  mAppliedTemporaryAutoBrightnessAdjustment=" + this.mAppliedTemporaryAutoBrightnessAdjustment);
        printWriter.println("  mDozing=" + this.mDozing);
        printWriter.println("  mSkipRampState=" + skipRampStateToString(this.mSkipRampState));
        printWriter.println("  mScreenOnBlockStartRealTime=" + this.mScreenOnBlockStartRealTime);
        printWriter.println("  mScreenOffBlockStartRealTime=" + this.mScreenOffBlockStartRealTime);
        printWriter.println("  mPendingScreenOnUnblocker=" + this.mPendingScreenOnUnblocker);
        printWriter.println("  mPendingScreenOffUnblocker=" + this.mPendingScreenOffUnblocker);
        printWriter.println("  mPendingScreenOff=" + this.mPendingScreenOff);
        printWriter.println("  mReportedToPolicy=" + reportedToPolicyToString(this.mReportedScreenStateToPolicy));
        printWriter.println("  mIsRbcActive=" + this.mIsRbcActive);
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
        printWriter.println();
        WakelockController wakelockController = this.mWakelockController;
        if (wakelockController != null) {
            wakelockController.dumpLocal(printWriter);
        }
        printWriter.println();
        DisplayBrightnessController displayBrightnessController = this.mDisplayBrightnessController;
        if (displayBrightnessController != null) {
            displayBrightnessController.dump(printWriter);
        }
        printWriter.println();
        DisplayStateController displayStateController = this.mDisplayStateController;
        if (displayStateController != null) {
            displayStateController.dumpsys(printWriter);
        }
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
                this.mHandler.removeMessages(11);
                if (z != z2) {
                    logHbmBrightnessStats(f, this.mDisplayStatsId);
                    return;
                }
                Message obtainMessage = this.mHandler.obtainMessage();
                obtainMessage.what = 11;
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
        float convertToNits = brightnessEvent.getHbmMode() == 0 ? -1.0f : this.mDisplayBrightnessController.convertToNits(brightnessEvent.getHbmMax());
        float convertToNits2 = brightnessEvent.getThermalMax() != 1.0f ? this.mDisplayBrightnessController.convertToNits(brightnessEvent.getThermalMax()) : -1.0f;
        if (this.mLogicalDisplay.getPrimaryDisplayDeviceLocked() == null || this.mLogicalDisplay.getPrimaryDisplayDeviceLocked().getDisplayDeviceInfoLocked().type != 1) {
            return;
        }
        FrameworkStatsLog.write((int) FrameworkStatsLog.DISPLAY_BRIGHTNESS_CHANGED, this.mDisplayBrightnessController.convertToNits(brightnessEvent.getInitialBrightness()), this.mDisplayBrightnessController.convertToNits(brightnessEvent.getBrightness()), brightnessEvent.getLux(), brightnessEvent.getPhysicalDisplayId(), brightnessEvent.wasShortTermModelActive(), powerFactor, rbcStrength, convertToNits, convertToNits2, brightnessEvent.isAutomaticBrightnessEnabled(), 1);
    }

    public final boolean readyToUpdateDisplayState() {
        return this.mDisplayId == 0 || this.mBootCompleted;
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
                    DisplayPowerController2.this.lambda$new$0();
                    return;
                case 2:
                    if (DisplayPowerController2.this.mPendingScreenOnUnblocker == message.obj) {
                        DisplayPowerController2.this.unblockScreenOn();
                        DisplayPowerController2.this.lambda$new$0();
                        return;
                    }
                    return;
                case 3:
                    if (DisplayPowerController2.this.mPendingScreenOffUnblocker == message.obj) {
                        DisplayPowerController2.this.unblockScreenOff();
                        DisplayPowerController2.this.lambda$new$0();
                        return;
                    }
                    return;
                case 4:
                    DisplayPowerController2.this.mBrightnessConfiguration = (BrightnessConfiguration) message.obj;
                    DisplayPowerController2.this.mShouldResetShortTermModel = message.arg1 == 1;
                    DisplayPowerController2.this.lambda$new$0();
                    return;
                case 5:
                    DisplayPowerController2.this.mDisplayBrightnessController.setTemporaryBrightness(Float.valueOf(Float.intBitsToFloat(message.arg1)));
                    DisplayPowerController2.this.lambda$new$0();
                    return;
                case 6:
                    DisplayPowerController2.this.mTemporaryAutoBrightnessAdjustment = Float.intBitsToFloat(message.arg1);
                    DisplayPowerController2.this.lambda$new$0();
                    return;
                case 7:
                    DisplayPowerController2.this.cleanupHandlerThreadAfterStop();
                    return;
                case 8:
                    if (DisplayPowerController2.this.mStopped) {
                        return;
                    }
                    DisplayPowerController2.this.handleSettingsChange(false);
                    return;
                case 9:
                    DisplayPowerController2.this.handleRbcChanged();
                    return;
                case 10:
                    if (DisplayPowerController2.this.mPowerState != null) {
                        DisplayPowerController2.this.reportStats(DisplayPowerController2.this.mPowerState.getScreenBrightness());
                        return;
                    }
                    return;
                case 11:
                    DisplayPowerController2.this.logHbmBrightnessStats(Float.intBitsToFloat(message.arg1), message.arg2);
                    return;
                case 12:
                    DisplayPowerController2.this.handleOnSwitchUser(message.arg1);
                    return;
                case 13:
                    DisplayPowerController2.this.mBootCompleted = true;
                    DisplayPowerController2.this.lambda$new$0();
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
                DisplayPowerController2.this.handleBrightnessModeChange();
            } else {
                DisplayPowerController2.this.handleSettingsChange(false);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class ScreenOnUnblocker implements WindowManagerPolicy.ScreenOnListener {
        public ScreenOnUnblocker() {
        }

        @Override // com.android.server.policy.WindowManagerPolicy.ScreenOnListener
        public void onScreenOn() {
            DisplayPowerController2.this.mHandler.sendMessageAtTime(DisplayPowerController2.this.mHandler.obtainMessage(2, this), DisplayPowerController2.this.mClock.uptimeMillis());
        }
    }

    /* loaded from: classes.dex */
    public final class ScreenOffUnblocker implements WindowManagerPolicy.ScreenOffListener {
        public ScreenOffUnblocker() {
        }

        @Override // com.android.server.policy.WindowManagerPolicy.ScreenOffListener
        public void onScreenOff() {
            DisplayPowerController2.this.mHandler.sendMessageAtTime(DisplayPowerController2.this.mHandler.obtainMessage(3, this), DisplayPowerController2.this.mClock.uptimeMillis());
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
    /* loaded from: classes.dex */
    public static class Injector {
        public Clock getClock() {
            return new Clock() { // from class: com.android.server.display.DisplayPowerController2$Injector$$ExternalSyntheticLambda0
                @Override // com.android.server.display.DisplayPowerController2.Clock
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

        public WakelockController getWakelockController(int i, DisplayManagerInternal.DisplayPowerCallbacks displayPowerCallbacks) {
            return new WakelockController(i, displayPowerCallbacks);
        }

        public DisplayPowerProximityStateController getDisplayPowerProximityStateController(WakelockController wakelockController, DisplayDeviceConfig displayDeviceConfig, Looper looper, Runnable runnable, int i, SensorManager sensorManager) {
            return new DisplayPowerProximityStateController(wakelockController, displayDeviceConfig, looper, runnable, i, sensorManager, null);
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
