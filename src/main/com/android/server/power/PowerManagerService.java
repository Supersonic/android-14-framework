package com.android.server.power;

import android.annotation.RequiresPermission;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.SynchronousUserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;
import android.hardware.devicestate.DeviceStateManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.hardware.display.DisplayManagerInternal;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.BatteryManagerInternal;
import android.os.BatterySaverPolicyConfig;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.IWakeLockCallback;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelDuration;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.p005os.IInstalld;
import android.provider.Settings;
import android.service.dreams.DreamManagerInternal;
import android.sysprop.InitProperties;
import android.sysprop.PowerProperties;
import android.util.ArrayMap;
import android.util.IntArray;
import android.util.KeyValueListParser;
import android.util.LongArray;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.DisplayInfo;
import android.view.KeyEvent;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.LatencyTracker;
import com.android.internal.util.Preconditions;
import com.android.server.EventLogTags;
import com.android.server.LockGuard;
import com.android.server.RescueParty;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.UiThread;
import com.android.server.UserspaceRebootLogger;
import com.android.server.Watchdog;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.lights.LightsManager;
import com.android.server.lights.LogicalLight;
import com.android.server.location.gnss.hal.GnssNative;
import com.android.server.p006am.BatteryStatsService;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.power.AmbientDisplaySuppressionController;
import com.android.server.power.PowerGroup;
import com.android.server.power.batterysaver.BatterySaverController;
import com.android.server.power.batterysaver.BatterySaverPolicy;
import com.android.server.power.batterysaver.BatterySaverStateMachine;
import com.android.server.power.batterysaver.BatterySavingStats;
import dalvik.annotation.optimization.NeverCompile;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public final class PowerManagerService extends SystemService implements Watchdog.Monitor {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    public static final IntArray DEFAULT_DISPLAY_GROUP_IDS = IntArray.wrap(new int[]{0});
    public static boolean sQuiescent;
    public boolean mAlwaysOnEnabled;
    public final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    public final AmbientDisplaySuppressionController mAmbientDisplaySuppressionController;
    public final AmbientDisplaySuppressionController.AmbientDisplaySuppressionChangedCallback mAmbientSuppressionChangedCallback;
    public AppOpsManager mAppOpsManager;
    public final AttentionDetector mAttentionDetector;
    public LogicalLight mAttentionLight;
    public int mAttentiveTimeoutConfig;
    public long mAttentiveTimeoutSetting;
    public long mAttentiveWarningDurationConfig;
    public int mBatteryLevel;
    public boolean mBatteryLevelLow;
    public BatteryManagerInternal mBatteryManagerInternal;
    public final BatterySaverController mBatterySaverController;
    public final BatterySaverPolicy mBatterySaverPolicy;
    public final BatterySaverStateMachine mBatterySaverStateMachine;
    public final BatterySavingStats mBatterySavingStats;
    public IBatteryStats mBatteryStats;
    public final BinderService mBinderService;
    public boolean mBootCompleted;
    public final SuspendBlocker mBootingSuspendBlocker;
    public final Clock mClock;
    public final Constants mConstants;
    public final Context mContext;
    public boolean mDecoupleHalAutoSuspendModeFromDisplayConfig;
    public boolean mDecoupleHalInteractiveModeFromDisplayConfig;
    public boolean mDeviceIdleMode;
    public int[] mDeviceIdleTempWhitelist;
    public int[] mDeviceIdleWhitelist;
    public int mDirty;
    public DisplayManagerInternal mDisplayManagerInternal;
    public final DisplayManagerInternal.DisplayPowerCallbacks mDisplayPowerCallbacks;
    public final SuspendBlocker mDisplaySuspendBlocker;
    public int mDockState;
    public boolean mDoubleTapWakeEnabled;
    public boolean mDozeAfterScreenOff;
    public int mDozeScreenBrightnessOverrideFromDreamManager;
    public float mDozeScreenBrightnessOverrideFromDreamManagerFloat;
    public int mDozeScreenStateOverrideFromDreamManager;
    public boolean mDozeStartInProgress;
    public boolean mDrawWakeLockOverrideFromSidekick;
    public DreamManagerInternal mDreamManager;
    public boolean mDreamsActivateOnDockSetting;
    public boolean mDreamsActivateOnSleepSetting;
    public boolean mDreamsActivatedOnDockByDefaultConfig;
    public boolean mDreamsActivatedOnSleepByDefaultConfig;
    public int mDreamsBatteryLevelDrain;
    public int mDreamsBatteryLevelDrainCutoffConfig;
    public int mDreamsBatteryLevelMinimumWhenNotPoweredConfig;
    public int mDreamsBatteryLevelMinimumWhenPoweredConfig;
    public boolean mDreamsDisabledByAmbientModeSuppressionConfig;
    public boolean mDreamsEnabledByDefaultConfig;
    public boolean mDreamsEnabledOnBatteryConfig;
    public boolean mDreamsEnabledSetting;
    public boolean mDreamsSupportedConfig;
    @GuardedBy({"mEnhancedDischargeTimeLock"})
    public boolean mEnhancedDischargePredictionIsPersonalized;
    @GuardedBy({"mEnhancedDischargeTimeLock"})
    public long mEnhancedDischargeTimeElapsed;
    public final Object mEnhancedDischargeTimeLock;
    public final FaceDownDetector mFaceDownDetector;
    public boolean mForceSuspendActive;
    public int mForegroundProfile;
    public boolean mHalAutoSuspendModeEnabled;
    public boolean mHalInteractiveModeEnabled;
    public final Handler mHandler;
    public final ServiceThread mHandlerThread;
    public boolean mHoldingBootingSuspendBlocker;
    public boolean mHoldingDisplaySuspendBlocker;
    public boolean mHoldingWakeLockSuspendBlocker;
    public final InattentiveSleepWarningController mInattentiveSleepWarningOverlayController;
    public final Injector mInjector;
    public boolean mInterceptedPowerKeyForProximity;
    public boolean mIsFaceDown;
    public boolean mIsPowered;
    public boolean mKeepDreamingWhenUndocked;
    @GuardedBy({"mEnhancedDischargeTimeLock"})
    public long mLastEnhancedDischargeTimeUpdatedElapsed;
    public long mLastFlipTime;
    public int mLastGlobalSleepReason;
    public long mLastGlobalSleepTime;
    public long mLastGlobalSleepTimeRealtime;
    public int mLastGlobalWakeReason;
    public long mLastGlobalWakeTime;
    public long mLastGlobalWakeTimeRealtime;
    public long mLastInteractivePowerHintTime;
    public long mLastScreenBrightnessBoostTime;
    public long mLastWarningAboutUserActivityPermission;
    public boolean mLightDeviceIdleMode;
    public LightsManager mLightsManager;
    public final LocalService mLocalService;
    public final Object mLock;
    public boolean mLowPowerStandbyActive;
    public int[] mLowPowerStandbyAllowlist;
    public final LowPowerStandbyController mLowPowerStandbyController;
    public long mMaximumScreenDimDurationConfig;
    public float mMaximumScreenDimRatioConfig;
    public long mMaximumScreenOffTimeoutFromDeviceAdmin;
    public long mMinimumScreenOffTimeoutConfig;
    public final NativeWrapper mNativeWrapper;
    public Notifier mNotifier;
    public long mNotifyLongDispatched;
    public long mNotifyLongNextCheck;
    public long mNotifyLongScheduled;
    public long mOverriddenTimeout;
    public int mPlugType;
    public WindowManagerPolicy mPolicy;
    public final PowerGroupWakefulnessChangeListener mPowerGroupWakefulnessChangeListener;
    @GuardedBy({"mLock"})
    public final SparseArray<PowerGroup> mPowerGroups;
    public final SparseArray<ProfilePowerState> mProfilePowerState;
    public boolean mProximityPositive;
    public boolean mRequestWaitForNegativeProximity;
    public boolean mSandmanScheduled;
    public boolean mScreenBrightnessBoostInProgress;
    public final float mScreenBrightnessDefault;
    public final float mScreenBrightnessDim;
    public final float mScreenBrightnessDoze;
    public final float mScreenBrightnessMaximum;
    public final float mScreenBrightnessMinimum;
    public float mScreenBrightnessOverrideFromWindowManager;
    public long mScreenOffTimeoutSetting;
    public final ScreenUndimDetector mScreenUndimDetector;
    public SettingsObserver mSettingsObserver;
    public long mSleepTimeoutSetting;
    public boolean mStayOn;
    public int mStayOnWhilePluggedInSetting;
    public boolean mSupportsDoubleTapWakeConfig;
    public final ArrayList<SuspendBlocker> mSuspendBlockers;
    public boolean mSuspendWhenScreenOffDueToProximityConfig;
    public final SystemPropertiesWrapper mSystemProperties;
    public boolean mSystemReady;
    public boolean mTheaterModeEnabled;
    public final SparseArray<UidState> mUidState;
    public boolean mUidsChanged;
    public boolean mUidsChanging;
    public boolean mUpdatePowerStateInProgress;
    public long mUserActivityTimeoutOverrideFromWindowManager;
    public int mUserId;
    public boolean mUserInactiveOverrideFromWindowManager;
    public int mWakeLockSummary;
    public final SuspendBlocker mWakeLockSuspendBlocker;
    public final ArrayList<WakeLock> mWakeLocks;
    public boolean mWakeUpWhenPluggedOrUnpluggedConfig;
    public boolean mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig;
    public boolean mWakefulnessChanging;
    public int mWakefulnessRaw;
    public WirelessChargerDetector mWirelessChargerDetector;

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public interface Clock {
        long elapsedRealtime();

        long uptimeMillis();
    }

    public static int adjustWakeLockSummary(int i, int i2) {
        if (i != 3) {
            i2 &= -193;
        }
        if (i == 0 || (i2 & 64) != 0) {
            i2 &= -15;
            if (i == 0) {
                i2 &= -17;
            }
        }
        if ((i2 & 6) != 0) {
            if (i == 1) {
                i2 |= 33;
            } else if (i == 2) {
                i2 |= 1;
            }
        }
        return (i2 & 128) != 0 ? i2 | 1 : i2;
    }

    public static boolean isValidBrightness(float f) {
        return f >= 0.0f && f <= 1.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeAcquireSuspendBlocker(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean nativeForceSuspend();

    /* JADX INFO: Access modifiers changed from: private */
    public native void nativeInit();

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeReleaseSuspendBlocker(String str);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetAutoSuspend(boolean z);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeSetPowerBoost(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native boolean nativeSetPowerMode(int i, boolean z);

    /* loaded from: classes2.dex */
    public final class DreamManagerStateListener implements DreamManagerInternal.DreamManagerStateListener {
        public DreamManagerStateListener() {
        }

        public void onKeepDreamingWhenUndockedChanged(boolean z) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.mKeepDreamingWhenUndocked = z;
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class PowerGroupWakefulnessChangeListener implements PowerGroup.PowerGroupListener {
        public PowerGroupWakefulnessChangeListener() {
        }

        @Override // com.android.server.power.PowerGroup.PowerGroupListener
        @GuardedBy({"mLock"})
        public void onWakefulnessChangedLocked(int i, int i2, long j, int i3, int i4, int i5, String str, String str2) {
            if (i2 == 1) {
                int i6 = i3 != 13 ? 0 : 1;
                PowerManagerService powerManagerService = PowerManagerService.this;
                powerManagerService.userActivityNoUpdateLocked((PowerGroup) powerManagerService.mPowerGroups.get(i), j, 0, i6, i4);
            }
            PowerManagerService.this.mDirty |= 65536;
            PowerManagerService.this.updateGlobalWakefulnessLocked(j, i3, i4, i5, str, str2);
            PowerManagerService.this.mNotifier.onPowerGroupWakefulnessChanged(i, i2, i3, PowerManagerService.this.getGlobalWakefulnessLocked());
            PowerManagerService.this.updatePowerStateLocked();
        }
    }

    /* loaded from: classes2.dex */
    public final class DisplayGroupPowerChangeListener implements DisplayManagerInternal.DisplayGroupListener {
        public DisplayGroupPowerChangeListener() {
        }

        public void onDisplayGroupAdded(int i) {
            synchronized (PowerManagerService.this.mLock) {
                if (PowerManagerService.this.mPowerGroups.contains(i)) {
                    Slog.e("PowerManagerService", "Tried to add already existing group:" + i);
                    return;
                }
                PowerGroup powerGroup = new PowerGroup(i, PowerManagerService.this.mPowerGroupWakefulnessChangeListener, PowerManagerService.this.mNotifier, PowerManagerService.this.mDisplayManagerInternal, 1, false, i == 0, PowerManagerService.this.mClock.uptimeMillis());
                PowerManagerService.this.mPowerGroups.append(i, powerGroup);
                PowerManagerService.this.onPowerGroupEventLocked(0, powerGroup);
            }
        }

        public void onDisplayGroupRemoved(int i) {
            synchronized (PowerManagerService.this.mLock) {
                if (i == 0) {
                    Slog.wtf("PowerManagerService", "Tried to remove default display group: " + i);
                } else if (!PowerManagerService.this.mPowerGroups.contains(i)) {
                    Slog.e("PowerManagerService", "Tried to remove non-existent group:" + i);
                } else {
                    PowerManagerService powerManagerService = PowerManagerService.this;
                    powerManagerService.onPowerGroupEventLocked(1, (PowerGroup) powerManagerService.mPowerGroups.get(i));
                }
            }
        }

        public void onDisplayGroupChanged(int i) {
            synchronized (PowerManagerService.this.mLock) {
                if (!PowerManagerService.this.mPowerGroups.contains(i)) {
                    Slog.e("PowerManagerService", "Tried to change non-existent group: " + i);
                    return;
                }
                PowerManagerService powerManagerService = PowerManagerService.this;
                powerManagerService.onPowerGroupEventLocked(2, (PowerGroup) powerManagerService.mPowerGroups.get(i));
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class ForegroundProfileObserver extends SynchronousUserSwitchObserver {
        public ForegroundProfileObserver() {
        }

        public void onUserSwitching(int i) throws RemoteException {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.mUserId = i;
            }
        }

        public void onForegroundProfileSwitch(int i) throws RemoteException {
            long uptimeMillis = PowerManagerService.this.mClock.uptimeMillis();
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.mForegroundProfile = i;
                PowerManagerService.this.maybeUpdateForegroundProfileLastActivityLocked(uptimeMillis);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class ProfilePowerState {
        public long mLastUserActivityTime;
        public boolean mLockingNotified;
        public long mScreenOffTimeout;
        public final int mUserId;
        public int mWakeLockSummary;

        public ProfilePowerState(int i, long j, long j2) {
            this.mUserId = i;
            this.mScreenOffTimeout = j;
            this.mLastUserActivityTime = j2;
        }
    }

    /* loaded from: classes2.dex */
    public final class Constants extends ContentObserver {
        public boolean NO_CACHED_WAKE_LOCKS;
        public final KeyValueListParser mParser;
        public ContentResolver mResolver;

        public Constants(Handler handler) {
            super(handler);
            this.NO_CACHED_WAKE_LOCKS = true;
            this.mParser = new KeyValueListParser(',');
        }

        public void start(ContentResolver contentResolver) {
            this.mResolver = contentResolver;
            contentResolver.registerContentObserver(Settings.Global.getUriFor("power_manager_constants"), false, this);
            updateConstants();
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            updateConstants();
        }

        public final void updateConstants() {
            synchronized (PowerManagerService.this.mLock) {
                try {
                    this.mParser.setString(Settings.Global.getString(this.mResolver, "power_manager_constants"));
                } catch (IllegalArgumentException e) {
                    Slog.e("PowerManagerService", "Bad alarm manager settings", e);
                }
                this.NO_CACHED_WAKE_LOCKS = this.mParser.getBoolean("no_cached_wake_locks", true);
            }
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("  Settings power_manager_constants:");
            printWriter.print("    ");
            printWriter.print("no_cached_wake_locks");
            printWriter.print("=");
            printWriter.println(this.NO_CACHED_WAKE_LOCKS);
        }

        public void dumpProto(ProtoOutputStream protoOutputStream) {
            long start = protoOutputStream.start(1146756268033L);
            protoOutputStream.write(1133871366145L, this.NO_CACHED_WAKE_LOCKS);
            protoOutputStream.end(start);
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class NativeWrapper {
        public void nativeInit(PowerManagerService powerManagerService) {
            powerManagerService.nativeInit();
        }

        public void nativeAcquireSuspendBlocker(String str) {
            PowerManagerService.nativeAcquireSuspendBlocker(str);
        }

        public void nativeReleaseSuspendBlocker(String str) {
            PowerManagerService.nativeReleaseSuspendBlocker(str);
        }

        public void nativeSetAutoSuspend(boolean z) {
            PowerManagerService.nativeSetAutoSuspend(z);
        }

        public void nativeSetPowerBoost(int i, int i2) {
            PowerManagerService.nativeSetPowerBoost(i, i2);
        }

        public boolean nativeSetPowerMode(int i, boolean z) {
            return PowerManagerService.nativeSetPowerMode(i, z);
        }

        public boolean nativeForceSuspend() {
            return PowerManagerService.nativeForceSuspend();
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class Injector {
        public Notifier createNotifier(Looper looper, Context context, IBatteryStats iBatteryStats, SuspendBlocker suspendBlocker, WindowManagerPolicy windowManagerPolicy, FaceDownDetector faceDownDetector, ScreenUndimDetector screenUndimDetector, Executor executor) {
            return new Notifier(looper, context, iBatteryStats, suspendBlocker, windowManagerPolicy, faceDownDetector, screenUndimDetector, executor);
        }

        public SuspendBlocker createSuspendBlocker(PowerManagerService powerManagerService, String str) {
            Objects.requireNonNull(powerManagerService);
            SuspendBlockerImpl suspendBlockerImpl = new SuspendBlockerImpl(str);
            powerManagerService.mSuspendBlockers.add(suspendBlockerImpl);
            return suspendBlockerImpl;
        }

        public BatterySaverPolicy createBatterySaverPolicy(Object obj, Context context, BatterySavingStats batterySavingStats) {
            return new BatterySaverPolicy(obj, context, batterySavingStats);
        }

        public BatterySaverController createBatterySaverController(Object obj, Context context, BatterySaverPolicy batterySaverPolicy, BatterySavingStats batterySavingStats) {
            return new BatterySaverController(obj, context, BackgroundThread.get().getLooper(), batterySaverPolicy, batterySavingStats);
        }

        public BatterySaverStateMachine createBatterySaverStateMachine(Object obj, Context context, BatterySaverController batterySaverController) {
            return new BatterySaverStateMachine(obj, context, batterySaverController);
        }

        public NativeWrapper createNativeWrapper() {
            return new NativeWrapper();
        }

        public WirelessChargerDetector createWirelessChargerDetector(SensorManager sensorManager, SuspendBlocker suspendBlocker, Handler handler) {
            return new WirelessChargerDetector(sensorManager, suspendBlocker, handler);
        }

        public AmbientDisplayConfiguration createAmbientDisplayConfiguration(Context context) {
            return new AmbientDisplayConfiguration(context);
        }

        public AmbientDisplaySuppressionController createAmbientDisplaySuppressionController(AmbientDisplaySuppressionController.AmbientDisplaySuppressionChangedCallback ambientDisplaySuppressionChangedCallback) {
            return new AmbientDisplaySuppressionController(ambientDisplaySuppressionChangedCallback);
        }

        public InattentiveSleepWarningController createInattentiveSleepWarningController() {
            return new InattentiveSleepWarningController();
        }

        public SystemPropertiesWrapper createSystemPropertiesWrapper() {
            return new SystemPropertiesWrapper() { // from class: com.android.server.power.PowerManagerService.Injector.1
                @Override // com.android.server.power.SystemPropertiesWrapper
                public String get(String str, String str2) {
                    return SystemProperties.get(str, str2);
                }

                @Override // com.android.server.power.SystemPropertiesWrapper
                public void set(String str, String str2) {
                    SystemProperties.set(str, str2);
                }
            };
        }

        public Clock createClock() {
            return new Clock() { // from class: com.android.server.power.PowerManagerService.Injector.2
                @Override // com.android.server.power.PowerManagerService.Clock
                public long uptimeMillis() {
                    return SystemClock.uptimeMillis();
                }

                @Override // com.android.server.power.PowerManagerService.Clock
                public long elapsedRealtime() {
                    return SystemClock.elapsedRealtime();
                }
            };
        }

        public Handler createHandler(Looper looper, Handler.Callback callback) {
            return new Handler(looper, callback, true);
        }

        public void invalidateIsInteractiveCaches() {
            PowerManager.invalidateIsInteractiveCaches();
        }

        public LowPowerStandbyController createLowPowerStandbyController(Context context, Looper looper) {
            return new LowPowerStandbyController(context, looper);
        }

        public AppOpsManager createAppOpsManager(Context context) {
            return (AppOpsManager) context.getSystemService(AppOpsManager.class);
        }
    }

    public PowerManagerService(Context context) {
        this(context, new Injector());
    }

    @VisibleForTesting
    public PowerManagerService(Context context, Injector injector) {
        super(context);
        boolean z;
        Object installNewLock = LockGuard.installNewLock(1);
        this.mLock = installNewLock;
        this.mSuspendBlockers = new ArrayList<>();
        this.mWakeLocks = new ArrayList<>();
        this.mEnhancedDischargeTimeLock = new Object();
        this.mDockState = 0;
        this.mMaximumScreenOffTimeoutFromDeviceAdmin = Long.MAX_VALUE;
        this.mIsFaceDown = false;
        this.mLastFlipTime = 0L;
        this.mScreenBrightnessOverrideFromWindowManager = Float.NaN;
        this.mOverriddenTimeout = -1L;
        this.mUserActivityTimeoutOverrideFromWindowManager = -1L;
        this.mDozeScreenStateOverrideFromDreamManager = 0;
        this.mDozeScreenBrightnessOverrideFromDreamManager = -1;
        this.mDozeScreenBrightnessOverrideFromDreamManagerFloat = Float.NaN;
        this.mLastWarningAboutUserActivityPermission = Long.MIN_VALUE;
        this.mDeviceIdleWhitelist = new int[0];
        this.mDeviceIdleTempWhitelist = new int[0];
        this.mLowPowerStandbyAllowlist = new int[0];
        this.mUidState = new SparseArray<>();
        this.mPowerGroups = new SparseArray<>();
        this.mProfilePowerState = new SparseArray<>();
        this.mDisplayPowerCallbacks = new DisplayManagerInternal.DisplayPowerCallbacks() { // from class: com.android.server.power.PowerManagerService.1
            public void onStateChanged() {
                synchronized (PowerManagerService.this.mLock) {
                    PowerManagerService.this.mDirty |= 8;
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }

            public void onProximityPositive() {
                synchronized (PowerManagerService.this.mLock) {
                    PowerManagerService.this.mProximityPositive = true;
                    PowerManagerService.this.mDirty |= 512;
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }

            public void onProximityNegative() {
                synchronized (PowerManagerService.this.mLock) {
                    PowerManagerService.this.mProximityPositive = false;
                    PowerManagerService.this.mInterceptedPowerKeyForProximity = false;
                    PowerManagerService.this.mDirty |= 512;
                    PowerManagerService powerManagerService = PowerManagerService.this;
                    powerManagerService.userActivityNoUpdateLocked((PowerGroup) powerManagerService.mPowerGroups.get(0), PowerManagerService.this.mClock.uptimeMillis(), 0, 0, 1000);
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }

            public void onDisplayStateChange(boolean z2, boolean z3) {
                synchronized (PowerManagerService.this.mLock) {
                    PowerManagerService.this.setPowerModeInternal(9, z2);
                    if (z3) {
                        if (!PowerManagerService.this.mDecoupleHalInteractiveModeFromDisplayConfig) {
                            PowerManagerService.this.setHalInteractiveModeLocked(false);
                        }
                        if (!PowerManagerService.this.mDecoupleHalAutoSuspendModeFromDisplayConfig) {
                            PowerManagerService.this.setHalAutoSuspendModeLocked(true);
                        }
                    } else {
                        if (!PowerManagerService.this.mDecoupleHalAutoSuspendModeFromDisplayConfig) {
                            PowerManagerService.this.setHalAutoSuspendModeLocked(false);
                        }
                        if (!PowerManagerService.this.mDecoupleHalInteractiveModeFromDisplayConfig) {
                            PowerManagerService.this.setHalInteractiveModeLocked(true);
                        }
                    }
                }
            }

            public void acquireSuspendBlocker(String str) {
                PowerManagerService.this.mDisplaySuspendBlocker.acquire(str);
            }

            public void releaseSuspendBlocker(String str) {
                PowerManagerService.this.mDisplaySuspendBlocker.release(str);
            }
        };
        AmbientDisplaySuppressionController.AmbientDisplaySuppressionChangedCallback ambientDisplaySuppressionChangedCallback = new AmbientDisplaySuppressionController.AmbientDisplaySuppressionChangedCallback() { // from class: com.android.server.power.PowerManagerService.4
            @Override // com.android.server.power.AmbientDisplaySuppressionController.AmbientDisplaySuppressionChangedCallback
            public void onSuppressionChanged(boolean z2) {
                synchronized (PowerManagerService.this.mLock) {
                    PowerManagerService.this.onDreamSuppressionChangedLocked(z2);
                }
            }
        };
        this.mAmbientSuppressionChangedCallback = ambientDisplaySuppressionChangedCallback;
        this.mContext = context;
        this.mBinderService = new BinderService(context);
        this.mLocalService = new LocalService();
        NativeWrapper createNativeWrapper = injector.createNativeWrapper();
        this.mNativeWrapper = createNativeWrapper;
        SystemPropertiesWrapper createSystemPropertiesWrapper = injector.createSystemPropertiesWrapper();
        this.mSystemProperties = createSystemPropertiesWrapper;
        this.mClock = injector.createClock();
        this.mInjector = injector;
        ServiceThread serviceThread = new ServiceThread("PowerManagerService", -4, false);
        this.mHandlerThread = serviceThread;
        serviceThread.start();
        Handler createHandler = injector.createHandler(serviceThread.getLooper(), new PowerManagerHandlerCallback());
        this.mHandler = createHandler;
        this.mConstants = new Constants(createHandler);
        this.mAmbientDisplayConfiguration = injector.createAmbientDisplayConfiguration(context);
        this.mAmbientDisplaySuppressionController = injector.createAmbientDisplaySuppressionController(ambientDisplaySuppressionChangedCallback);
        this.mAttentionDetector = new AttentionDetector(new Runnable() { // from class: com.android.server.power.PowerManagerService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PowerManagerService.this.onUserAttention();
            }
        }, installNewLock);
        this.mFaceDownDetector = new FaceDownDetector(new Consumer() { // from class: com.android.server.power.PowerManagerService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PowerManagerService.this.onFlip(((Boolean) obj).booleanValue());
            }
        });
        this.mScreenUndimDetector = new ScreenUndimDetector();
        BatterySavingStats batterySavingStats = new BatterySavingStats(installNewLock);
        this.mBatterySavingStats = batterySavingStats;
        BatterySaverPolicy createBatterySaverPolicy = injector.createBatterySaverPolicy(installNewLock, context, batterySavingStats);
        this.mBatterySaverPolicy = createBatterySaverPolicy;
        BatterySaverController createBatterySaverController = injector.createBatterySaverController(installNewLock, context, createBatterySaverPolicy, batterySavingStats);
        this.mBatterySaverController = createBatterySaverController;
        this.mBatterySaverStateMachine = injector.createBatterySaverStateMachine(installNewLock, context, createBatterySaverController);
        this.mLowPowerStandbyController = injector.createLowPowerStandbyController(context, Looper.getMainLooper());
        this.mInattentiveSleepWarningOverlayController = injector.createInattentiveSleepWarningController();
        this.mAppOpsManager = injector.createAppOpsManager(context);
        this.mPowerGroupWakefulnessChangeListener = new PowerGroupWakefulnessChangeListener();
        float f = context.getResources().getFloat(17105109);
        float f2 = context.getResources().getFloat(17105108);
        float f3 = context.getResources().getFloat(17105107);
        float f4 = context.getResources().getFloat(17105105);
        float f5 = context.getResources().getFloat(17105104);
        if (f == -2.0f || f2 == -2.0f || f3 == -2.0f) {
            this.mScreenBrightnessMinimum = BrightnessSynchronizer.brightnessIntToFloat(context.getResources().getInteger(17694945));
            this.mScreenBrightnessMaximum = BrightnessSynchronizer.brightnessIntToFloat(context.getResources().getInteger(17694944));
            this.mScreenBrightnessDefault = BrightnessSynchronizer.brightnessIntToFloat(context.getResources().getInteger(17694943));
        } else {
            this.mScreenBrightnessMinimum = f;
            this.mScreenBrightnessMaximum = f2;
            this.mScreenBrightnessDefault = f3;
        }
        if (f4 == -2.0f) {
            this.mScreenBrightnessDoze = BrightnessSynchronizer.brightnessIntToFloat(context.getResources().getInteger(17694942));
        } else {
            this.mScreenBrightnessDoze = f4;
        }
        if (f5 == -2.0f) {
            this.mScreenBrightnessDim = BrightnessSynchronizer.brightnessIntToFloat(context.getResources().getInteger(17694941));
        } else {
            this.mScreenBrightnessDim = f5;
        }
        synchronized (installNewLock) {
            SuspendBlocker createSuspendBlocker = injector.createSuspendBlocker(this, "PowerManagerService.Booting");
            this.mBootingSuspendBlocker = createSuspendBlocker;
            this.mWakeLockSuspendBlocker = injector.createSuspendBlocker(this, "PowerManagerService.WakeLocks");
            SuspendBlocker createSuspendBlocker2 = injector.createSuspendBlocker(this, "PowerManagerService.Display");
            this.mDisplaySuspendBlocker = createSuspendBlocker2;
            if (createSuspendBlocker != null) {
                createSuspendBlocker.acquire();
                this.mHoldingBootingSuspendBlocker = true;
            }
            if (createSuspendBlocker2 != null) {
                createSuspendBlocker2.acquire("holding display");
                this.mHoldingDisplaySuspendBlocker = true;
            }
            this.mHalAutoSuspendModeEnabled = false;
            this.mHalInteractiveModeEnabled = true;
            this.mWakefulnessRaw = 1;
            if (!createSystemPropertiesWrapper.get("ro.boot.quiescent", "0").equals("1") && !((Boolean) InitProperties.userspace_reboot_in_progress().orElse(Boolean.FALSE)).booleanValue()) {
                z = false;
                sQuiescent = z;
                createNativeWrapper.nativeInit(this);
                createNativeWrapper.nativeSetAutoSuspend(false);
                createNativeWrapper.nativeSetPowerMode(7, true);
                createNativeWrapper.nativeSetPowerMode(0, false);
                injector.invalidateIsInteractiveCaches();
            }
            z = true;
            sQuiescent = z;
            createNativeWrapper.nativeInit(this);
            createNativeWrapper.nativeSetAutoSuspend(false);
            createNativeWrapper.nativeSetPowerMode(7, true);
            createNativeWrapper.nativeSetPowerMode(0, false);
            injector.invalidateIsInteractiveCaches();
        }
    }

    public final void onFlip(boolean z) {
        long j;
        synchronized (this.mLock) {
            if (this.mBootCompleted) {
                StringBuilder sb = new StringBuilder();
                sb.append("onFlip(): Face ");
                sb.append(z ? "down." : "up.");
                Slog.i("PowerManagerService", sb.toString());
                this.mIsFaceDown = z;
                if (z) {
                    long uptimeMillis = this.mClock.uptimeMillis();
                    this.mLastFlipTime = uptimeMillis;
                    j = (this.mPowerGroups.get(0).getLastUserActivityTimeLocked() + getScreenOffTimeoutLocked(getSleepTimeoutLocked(-1L), -1L)) - uptimeMillis;
                    userActivityInternal(0, uptimeMillis, 5, 1, 1000);
                } else {
                    j = 0;
                }
                if (z) {
                    this.mFaceDownDetector.setMillisSaved(j);
                }
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("power", this.mBinderService, false, 9);
        publishLocalService(PowerManagerInternal.class, this.mLocalService);
        Watchdog.getInstance().addMonitor(this);
        Watchdog.getInstance().addThread(this.mHandler);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            systemReady();
        } else if (i == 600) {
            incrementBootCount();
        } else if (i == 1000) {
            synchronized (this.mLock) {
                long uptimeMillis = this.mClock.uptimeMillis();
                this.mBootCompleted = true;
                this.mDirty |= 16;
                this.mBatterySaverStateMachine.onBootCompleted();
                userActivityNoUpdateLocked(uptimeMillis, 0, 0, 1000);
                updatePowerStateLocked();
                if (sQuiescent) {
                    sleepPowerGroupLocked(this.mPowerGroups.get(0), this.mClock.uptimeMillis(), 10, 1000);
                }
                ((DeviceStateManager) this.mContext.getSystemService(DeviceStateManager.class)).registerCallback(new HandlerExecutor(this.mHandler), new DeviceStateListener());
            }
        }
    }

    public final void systemReady() {
        synchronized (this.mLock) {
            this.mSystemReady = true;
            this.mDreamManager = (DreamManagerInternal) getLocalService(DreamManagerInternal.class);
            this.mDisplayManagerInternal = (DisplayManagerInternal) getLocalService(DisplayManagerInternal.class);
            this.mPolicy = (WindowManagerPolicy) getLocalService(WindowManagerPolicy.class);
            this.mBatteryManagerInternal = (BatteryManagerInternal) getLocalService(BatteryManagerInternal.class);
            this.mAttentionDetector.systemReady(this.mContext);
            SensorManager systemSensorManager = new SystemSensorManager(this.mContext, this.mHandler.getLooper());
            this.mBatteryStats = BatteryStatsService.getService();
            this.mNotifier = this.mInjector.createNotifier(Looper.getMainLooper(), this.mContext, this.mBatteryStats, this.mInjector.createSuspendBlocker(this, "PowerManagerService.Broadcasts"), this.mPolicy, this.mFaceDownDetector, this.mScreenUndimDetector, BackgroundThread.getExecutor());
            this.mPowerGroups.append(0, new PowerGroup(1, this.mPowerGroupWakefulnessChangeListener, this.mNotifier, this.mDisplayManagerInternal, this.mClock.uptimeMillis()));
            this.mDisplayManagerInternal.registerDisplayGroupListener(new DisplayGroupPowerChangeListener());
            this.mDreamManager.registerDreamManagerStateListener(new DreamManagerStateListener());
            Injector injector = this.mInjector;
            this.mWirelessChargerDetector = injector.createWirelessChargerDetector(systemSensorManager, injector.createSuspendBlocker(this, "PowerManagerService.WirelessChargerDetector"), this.mHandler);
            this.mSettingsObserver = new SettingsObserver(this.mHandler);
            LightsManager lightsManager = (LightsManager) getLocalService(LightsManager.class);
            this.mLightsManager = lightsManager;
            this.mAttentionLight = lightsManager.getLight(5);
            this.mDisplayManagerInternal.initPowerManagement(this.mDisplayPowerCallbacks, this.mHandler, systemSensorManager);
            addPowerGroupsForNonDefaultDisplayGroupLocked();
            try {
                ActivityManager.getService().registerUserSwitchObserver(new ForegroundProfileObserver(), "PowerManagerService");
            } catch (RemoteException unused) {
            }
            this.mLowPowerStandbyController.systemReady();
            readConfigurationLocked();
            updateSettingsLocked();
            this.mDirty |= 256;
            updatePowerStateLocked();
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mConstants.start(contentResolver);
        this.mBatterySaverController.systemReady();
        this.mBatterySaverPolicy.systemReady();
        this.mFaceDownDetector.systemReady(this.mContext);
        this.mScreenUndimDetector.systemReady(this.mContext);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("screensaver_enabled"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("screensaver_activate_on_sleep"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("screensaver_activate_on_dock"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.System.getUriFor("screen_off_timeout"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("sleep_timeout"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("attentive_timeout"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Global.getUriFor("stay_on_while_plugged_in"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.System.getUriFor("screen_auto_brightness_adj"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Global.getUriFor("theater_mode_on"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("doze_always_on"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("double_tap_to_wake"), false, this.mSettingsObserver, -1);
        contentResolver.registerContentObserver(Settings.Global.getUriFor("device_demo_mode"), false, this.mSettingsObserver, 0);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.setPriority(1000);
        this.mContext.registerReceiver(new BatteryReceiver(), intentFilter, null, this.mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.DREAMING_STARTED");
        intentFilter2.addAction("android.intent.action.DREAMING_STOPPED");
        this.mContext.registerReceiver(new DreamReceiver(), intentFilter2, null, this.mHandler);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.intent.action.USER_SWITCHED");
        this.mContext.registerReceiver(new UserSwitchedReceiver(), intentFilter3, null, this.mHandler);
        IntentFilter intentFilter4 = new IntentFilter();
        intentFilter4.addAction("android.intent.action.DOCK_EVENT");
        this.mContext.registerReceiver(new DockReceiver(), intentFilter4, null, this.mHandler);
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void readConfigurationLocked() {
        Resources resources = this.mContext.getResources();
        this.mDecoupleHalAutoSuspendModeFromDisplayConfig = resources.getBoolean(17891765);
        this.mDecoupleHalInteractiveModeFromDisplayConfig = resources.getBoolean(17891766);
        this.mWakeUpWhenPluggedOrUnpluggedConfig = resources.getBoolean(17891852);
        this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig = resources.getBoolean(17891361);
        this.mSuspendWhenScreenOffDueToProximityConfig = resources.getBoolean(17891838);
        this.mAttentiveTimeoutConfig = resources.getInteger(17694738);
        this.mAttentiveWarningDurationConfig = resources.getInteger(17694739);
        this.mDreamsSupportedConfig = resources.getBoolean(17891633);
        this.mDreamsEnabledByDefaultConfig = resources.getBoolean(17891630);
        this.mDreamsActivatedOnSleepByDefaultConfig = resources.getBoolean(17891628);
        this.mDreamsActivatedOnDockByDefaultConfig = resources.getBoolean(17891627);
        this.mDreamsEnabledOnBatteryConfig = resources.getBoolean(17891631);
        this.mDreamsBatteryLevelMinimumWhenPoweredConfig = resources.getInteger(17694839);
        this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig = resources.getInteger(17694838);
        this.mDreamsBatteryLevelDrainCutoffConfig = resources.getInteger(17694837);
        this.mDreamsDisabledByAmbientModeSuppressionConfig = resources.getBoolean(17891629);
        this.mDozeAfterScreenOff = resources.getBoolean(17891620);
        this.mMinimumScreenOffTimeoutConfig = resources.getInteger(17694893);
        this.mMaximumScreenDimDurationConfig = resources.getInteger(17694886);
        this.mMaximumScreenDimRatioConfig = resources.getFraction(18022402, 1, 1);
        this.mSupportsDoubleTapWakeConfig = resources.getBoolean(17891818);
    }

    @GuardedBy({"mLock"})
    public final void updateSettingsLocked() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mDreamsEnabledSetting = Settings.Secure.getIntForUser(contentResolver, "screensaver_enabled", this.mDreamsEnabledByDefaultConfig ? 1 : 0, -2) != 0;
        this.mDreamsActivateOnSleepSetting = Settings.Secure.getIntForUser(contentResolver, "screensaver_activate_on_sleep", this.mDreamsActivatedOnSleepByDefaultConfig ? 1 : 0, -2) != 0;
        this.mDreamsActivateOnDockSetting = Settings.Secure.getIntForUser(contentResolver, "screensaver_activate_on_dock", this.mDreamsActivatedOnDockByDefaultConfig ? 1 : 0, -2) != 0;
        this.mScreenOffTimeoutSetting = Settings.System.getIntForUser(contentResolver, "screen_off_timeout", 15000, -2);
        this.mSleepTimeoutSetting = Settings.Secure.getIntForUser(contentResolver, "sleep_timeout", -1, -2);
        this.mAttentiveTimeoutSetting = Settings.Secure.getIntForUser(contentResolver, "attentive_timeout", this.mAttentiveTimeoutConfig, -2);
        this.mStayOnWhilePluggedInSetting = Settings.Global.getInt(contentResolver, "stay_on_while_plugged_in", 1);
        this.mTheaterModeEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), "theater_mode_on", 0) == 1;
        this.mAlwaysOnEnabled = this.mAmbientDisplayConfiguration.alwaysOnEnabled(-2);
        if (this.mSupportsDoubleTapWakeConfig) {
            boolean z = Settings.Secure.getIntForUser(contentResolver, "double_tap_to_wake", 0, -2) != 0;
            if (z != this.mDoubleTapWakeEnabled) {
                this.mDoubleTapWakeEnabled = z;
                this.mNativeWrapper.nativeSetPowerMode(0, z);
            }
        }
        String str = UserManager.isDeviceInDemoMode(this.mContext) ? "1" : "0";
        if (!str.equals(this.mSystemProperties.get("sys.retaildemo.enabled", null))) {
            this.mSystemProperties.set("sys.retaildemo.enabled", str);
        }
        this.mDirty |= 32;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void handleSettingsChangedLocked() {
        updateSettingsLocked();
        updatePowerStateLocked();
    }

    @RequiresPermission(conditional = true, value = "android.permission.TURN_SCREEN_ON")
    public final void acquireWakeLockInternal(IBinder iBinder, int i, int i2, String str, String str2, WorkSource workSource, String str3, int i3, int i4, IWakeLockCallback iWakeLockCallback) {
        WakeLock wakeLock;
        boolean z;
        synchronized (this.mLock) {
            if (i != -1) {
                DisplayInfo displayInfo = this.mSystemReady ? this.mDisplayManagerInternal.getDisplayInfo(i) : null;
                if (displayInfo == null) {
                    Slog.wtf("PowerManagerService", "Tried to acquire wake lock for invalid display: " + i);
                    return;
                } else if (!displayInfo.hasAccess(i3)) {
                    throw new SecurityException("Caller does not have access to display");
                }
            }
            int findWakeLockIndexLocked = findWakeLockIndexLocked(iBinder);
            if (findWakeLockIndexLocked >= 0) {
                wakeLock = this.mWakeLocks.get(findWakeLockIndexLocked);
                if (!wakeLock.hasSameProperties(i2, str, workSource, i3, i4, iWakeLockCallback)) {
                    notifyWakeLockChangingLocked(wakeLock, i2, str, str2, i3, i4, workSource, str3, iWakeLockCallback);
                    wakeLock.updateProperties(i2, str, str2, workSource, str3, i3, i4, iWakeLockCallback);
                }
                z = false;
            } else {
                UidState uidState = this.mUidState.get(i3);
                if (uidState == null) {
                    uidState = new UidState(i3);
                    uidState.mProcState = 20;
                    this.mUidState.put(i3, uidState);
                }
                UidState uidState2 = uidState;
                uidState2.mNumWakeLocks++;
                WakeLock wakeLock2 = new WakeLock(iBinder, i, i2, str, str2, workSource, str3, i3, i4, uidState2, iWakeLockCallback);
                this.mWakeLocks.add(wakeLock2);
                setWakeLockDisabledStateLocked(wakeLock2);
                wakeLock = wakeLock2;
                z = true;
            }
            applyWakeLockFlagsOnAcquireLocked(wakeLock);
            this.mDirty |= 1;
            updatePowerStateLocked();
            if (z) {
                notifyWakeLockAcquiredLocked(wakeLock);
            }
        }
    }

    public static boolean isScreenLock(WakeLock wakeLock) {
        int i = wakeLock.mFlags & GnssNative.GNSS_AIDING_TYPE_ALL;
        return i == 6 || i == 10 || i == 26;
    }

    public static WorkSource.WorkChain getFirstNonEmptyWorkChain(WorkSource workSource) {
        if (workSource.getWorkChains() == null) {
            return null;
        }
        for (WorkSource.WorkChain workChain : workSource.getWorkChains()) {
            if (workChain.getSize() > 0) {
                return workChain;
            }
        }
        return null;
    }

    @RequiresPermission(conditional = true, value = "android.permission.TURN_SCREEN_ON")
    public final boolean isAcquireCausesWakeupFlagAllowed(String str, int i) {
        if (str == null) {
            return false;
        }
        if (this.mAppOpsManager.checkOpNoThrow(61, i, str) == 0 && this.mContext.checkCallingOrSelfPermission("android.permission.TURN_SCREEN_ON") == 0) {
            Slog.i("PowerManagerService", "Allowing device wake-up from app " + str);
            return true;
        } else if (((Boolean) PowerProperties.permissionless_turn_screen_on().orElse(Boolean.FALSE)).booleanValue()) {
            Slog.d("PowerManagerService", "Device wake-up allowed by debug.power.permissionless_turn_screen_on");
            return true;
        } else {
            Slog.w("PowerManagerService", "Not allowing device wake-up for " + str);
            return false;
        }
    }

    @RequiresPermission(conditional = true, value = "android.permission.TURN_SCREEN_ON")
    @GuardedBy({"mLock"})
    public final void applyWakeLockFlagsOnAcquireLocked(WakeLock wakeLock) {
        String str;
        int i;
        if ((wakeLock.mFlags & 268435456) == 0 || !isScreenLock(wakeLock)) {
            return;
        }
        WorkSource workSource = wakeLock.mWorkSource;
        if (workSource != null && !workSource.isEmpty()) {
            WorkSource workSource2 = wakeLock.mWorkSource;
            WorkSource.WorkChain firstNonEmptyWorkChain = getFirstNonEmptyWorkChain(workSource2);
            if (firstNonEmptyWorkChain != null) {
                str = firstNonEmptyWorkChain.getAttributionTag();
                i = firstNonEmptyWorkChain.getAttributionUid();
            } else {
                String packageName = workSource2.getPackageName(0) != null ? workSource2.getPackageName(0) : wakeLock.mPackageName;
                i = workSource2.getUid(0);
                str = packageName;
            }
        } else {
            str = wakeLock.mPackageName;
            i = wakeLock.mOwnerUid;
        }
        Integer powerGroupId = wakeLock.getPowerGroupId();
        if (powerGroupId == null || !isAcquireCausesWakeupFlagAllowed(str, i)) {
            return;
        }
        if (powerGroupId.intValue() == -1) {
            for (int i2 = 0; i2 < this.mPowerGroups.size(); i2++) {
                wakePowerGroupLocked(this.mPowerGroups.valueAt(i2), this.mClock.uptimeMillis(), 2, wakeLock.mTag, i, str, i);
            }
        } else if (this.mPowerGroups.contains(powerGroupId.intValue())) {
            wakePowerGroupLocked(this.mPowerGroups.get(powerGroupId.intValue()), this.mClock.uptimeMillis(), 2, wakeLock.mTag, i, str, i);
        }
    }

    public final void releaseWakeLockInternal(IBinder iBinder, int i) {
        synchronized (this.mLock) {
            int findWakeLockIndexLocked = findWakeLockIndexLocked(iBinder);
            if (findWakeLockIndexLocked < 0) {
                return;
            }
            WakeLock wakeLock = this.mWakeLocks.get(findWakeLockIndexLocked);
            if ((i & 1) != 0) {
                this.mRequestWaitForNegativeProximity = true;
            }
            wakeLock.unlinkToDeath();
            wakeLock.setDisabled(true);
            removeWakeLockLocked(wakeLock, findWakeLockIndexLocked);
        }
    }

    public final void handleWakeLockDeath(WakeLock wakeLock) {
        synchronized (this.mLock) {
            int indexOf = this.mWakeLocks.indexOf(wakeLock);
            if (indexOf < 0) {
                return;
            }
            removeWakeLockLocked(wakeLock, indexOf);
        }
    }

    @GuardedBy({"mLock"})
    public final void removeWakeLockLocked(WakeLock wakeLock, int i) {
        this.mWakeLocks.remove(i);
        UidState uidState = wakeLock.mUidState;
        int i2 = uidState.mNumWakeLocks - 1;
        uidState.mNumWakeLocks = i2;
        if (i2 <= 0 && uidState.mProcState == 20) {
            this.mUidState.remove(uidState.mUid);
        }
        notifyWakeLockReleasedLocked(wakeLock);
        applyWakeLockFlagsOnReleaseLocked(wakeLock);
        this.mDirty |= 1;
        updatePowerStateLocked();
    }

    @GuardedBy({"mLock"})
    public final void applyWakeLockFlagsOnReleaseLocked(WakeLock wakeLock) {
        if ((wakeLock.mFlags & 536870912) == 0 || !isScreenLock(wakeLock)) {
            return;
        }
        userActivityNoUpdateLocked(this.mClock.uptimeMillis(), 0, 1, wakeLock.mOwnerUid);
    }

    public final void updateWakeLockWorkSourceInternal(IBinder iBinder, WorkSource workSource, String str, int i) {
        synchronized (this.mLock) {
            int findWakeLockIndexLocked = findWakeLockIndexLocked(iBinder);
            if (findWakeLockIndexLocked < 0) {
                throw new IllegalArgumentException("Wake lock not active: " + iBinder + " from uid " + i);
            }
            WakeLock wakeLock = this.mWakeLocks.get(findWakeLockIndexLocked);
            if (!wakeLock.hasSameWorkSource(workSource)) {
                notifyWakeLockChangingLocked(wakeLock, wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, workSource, str, null);
                wakeLock.mHistoryTag = str;
                wakeLock.updateWorkSource(workSource);
            }
        }
    }

    public final void updateWakeLockCallbackInternal(IBinder iBinder, IWakeLockCallback iWakeLockCallback, int i) {
        synchronized (this.mLock) {
            int findWakeLockIndexLocked = findWakeLockIndexLocked(iBinder);
            if (findWakeLockIndexLocked < 0) {
                throw new IllegalArgumentException("Wake lock not active: " + iBinder + " from uid " + i);
            }
            WakeLock wakeLock = this.mWakeLocks.get(findWakeLockIndexLocked);
            if (!isSameCallback(iWakeLockCallback, wakeLock.mCallback)) {
                notifyWakeLockChangingLocked(wakeLock, wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource, wakeLock.mHistoryTag, iWakeLockCallback);
                wakeLock.mCallback = iWakeLockCallback;
            }
        }
    }

    @GuardedBy({"mLock"})
    public final int findWakeLockIndexLocked(IBinder iBinder) {
        int size = this.mWakeLocks.size();
        for (int i = 0; i < size; i++) {
            if (this.mWakeLocks.get(i).mLock == iBinder) {
                return i;
            }
        }
        return -1;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public WakeLock findWakeLockLocked(IBinder iBinder) {
        int findWakeLockIndexLocked = findWakeLockIndexLocked(iBinder);
        if (findWakeLockIndexLocked == -1) {
            return null;
        }
        return this.mWakeLocks.get(findWakeLockIndexLocked);
    }

    @GuardedBy({"mLock"})
    public final void notifyWakeLockAcquiredLocked(WakeLock wakeLock) {
        if (!this.mSystemReady || wakeLock.mDisabled) {
            return;
        }
        wakeLock.mNotifiedAcquired = true;
        this.mNotifier.onWakeLockAcquired(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource, wakeLock.mHistoryTag, wakeLock.mCallback);
        restartNofifyLongTimerLocked(wakeLock);
    }

    @GuardedBy({"mLock"})
    public final void enqueueNotifyLongMsgLocked(long j) {
        this.mNotifyLongScheduled = j;
        Message obtainMessage = this.mHandler.obtainMessage(4);
        obtainMessage.setAsynchronous(true);
        this.mHandler.sendMessageAtTime(obtainMessage, j);
    }

    @GuardedBy({"mLock"})
    public final void restartNofifyLongTimerLocked(WakeLock wakeLock) {
        long uptimeMillis = this.mClock.uptimeMillis();
        wakeLock.mAcquireTime = uptimeMillis;
        if ((wakeLock.mFlags & GnssNative.GNSS_AIDING_TYPE_ALL) == 1 && this.mNotifyLongScheduled == 0) {
            enqueueNotifyLongMsgLocked(uptimeMillis + 60000);
        }
    }

    @GuardedBy({"mLock"})
    public final void notifyWakeLockLongStartedLocked(WakeLock wakeLock) {
        if (!this.mSystemReady || wakeLock.mDisabled) {
            return;
        }
        wakeLock.mNotifiedLong = true;
        this.mNotifier.onLongPartialWakeLockStart(wakeLock.mTag, wakeLock.mOwnerUid, wakeLock.mWorkSource, wakeLock.mHistoryTag);
    }

    @GuardedBy({"mLock"})
    public final void notifyWakeLockLongFinishedLocked(WakeLock wakeLock) {
        if (wakeLock.mNotifiedLong) {
            wakeLock.mNotifiedLong = false;
            this.mNotifier.onLongPartialWakeLockFinish(wakeLock.mTag, wakeLock.mOwnerUid, wakeLock.mWorkSource, wakeLock.mHistoryTag);
        }
    }

    @GuardedBy({"mLock"})
    public final void notifyWakeLockChangingLocked(WakeLock wakeLock, int i, String str, String str2, int i2, int i3, WorkSource workSource, String str3, IWakeLockCallback iWakeLockCallback) {
        if (this.mSystemReady && wakeLock.mNotifiedAcquired) {
            this.mNotifier.onWakeLockChanging(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource, wakeLock.mHistoryTag, wakeLock.mCallback, i, str, str2, i2, i3, workSource, str3, iWakeLockCallback);
            notifyWakeLockLongFinishedLocked(wakeLock);
            restartNofifyLongTimerLocked(wakeLock);
        }
    }

    @GuardedBy({"mLock"})
    public final void notifyWakeLockReleasedLocked(WakeLock wakeLock) {
        if (this.mSystemReady && wakeLock.mNotifiedAcquired) {
            wakeLock.mNotifiedAcquired = false;
            wakeLock.mAcquireTime = 0L;
            this.mNotifier.onWakeLockReleased(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource, wakeLock.mHistoryTag, wakeLock.mCallback);
            notifyWakeLockLongFinishedLocked(wakeLock);
        }
    }

    public final boolean isWakeLockLevelSupportedInternal(int i) {
        synchronized (this.mLock) {
            boolean z = true;
            if (i != 1 && i != 6 && i != 10 && i != 26) {
                if (i == 32) {
                    if (!this.mSystemReady || !this.mDisplayManagerInternal.isProximitySensorAvailable()) {
                        z = false;
                    }
                    return z;
                } else if (i != 64 && i != 128) {
                    return false;
                }
            }
            return true;
        }
    }

    public final void userActivityFromNative(long j, int i, int i2, int i3) {
        userActivityInternal(i2, j, i, i3, 1000);
    }

    public final void userActivityInternal(int i, long j, int i2, int i3, int i4) {
        synchronized (this.mLock) {
            if (i == -1) {
                if (userActivityNoUpdateLocked(j, i2, i3, i4)) {
                    updatePowerStateLocked();
                }
                return;
            }
            DisplayInfo displayInfo = this.mDisplayManagerInternal.getDisplayInfo(i);
            if (displayInfo == null) {
                return;
            }
            int i5 = displayInfo.displayGroupId;
            if (i5 == -1) {
                return;
            }
            if (userActivityNoUpdateLocked(this.mPowerGroups.get(i5), j, i2, i3, i4)) {
                updatePowerStateLocked();
            }
        }
    }

    public final void napInternal(long j, int i, boolean z) {
        synchronized (this.mLock) {
            dreamPowerGroupLocked(this.mPowerGroups.get(0), j, i, z);
        }
    }

    public final void onUserAttention() {
        synchronized (this.mLock) {
            if (userActivityNoUpdateLocked(this.mPowerGroups.get(0), this.mClock.uptimeMillis(), 4, 0, 1000)) {
                updatePowerStateLocked();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final boolean userActivityNoUpdateLocked(long j, int i, int i2, int i3) {
        boolean z = false;
        for (int i4 = 0; i4 < this.mPowerGroups.size(); i4++) {
            if (userActivityNoUpdateLocked(this.mPowerGroups.valueAt(i4), j, i, i2, i3)) {
                z = true;
            }
        }
        return z;
    }

    @GuardedBy({"mLock"})
    public final boolean userActivityNoUpdateLocked(PowerGroup powerGroup, long j, int i, int i2, int i3) {
        powerGroup.getGroupId();
        if (j < powerGroup.getLastSleepTimeLocked() || j < powerGroup.getLastWakeTimeLocked() || !this.mSystemReady) {
            return false;
        }
        Trace.traceBegin(131072L, "userActivity");
        try {
            if (j > this.mLastInteractivePowerHintTime) {
                setPowerBoostInternal(0, 0);
                this.mLastInteractivePowerHintTime = j;
            }
            this.mNotifier.onUserActivity(powerGroup.getGroupId(), i, i3);
            this.mAttentionDetector.onUserActivity(j, i);
            if (this.mUserInactiveOverrideFromWindowManager) {
                this.mUserInactiveOverrideFromWindowManager = false;
                this.mOverriddenTimeout = -1L;
            }
            int wakefulnessLocked = powerGroup.getWakefulnessLocked();
            if (wakefulnessLocked != 0 && wakefulnessLocked != 3 && (i2 & 2) == 0) {
                maybeUpdateForegroundProfileLastActivityLocked(j);
                if ((i2 & 1) != 0) {
                    if (j > powerGroup.getLastUserActivityTimeNoChangeLightsLocked() && j > powerGroup.getLastUserActivityTimeLocked()) {
                        powerGroup.setLastUserActivityTimeNoChangeLightsLocked(j, i);
                        int i4 = this.mDirty | 4;
                        this.mDirty = i4;
                        if (i == 1) {
                            this.mDirty = i4 | IInstalld.FLAG_USE_QUOTA;
                        }
                        return true;
                    }
                } else if (j > powerGroup.getLastUserActivityTimeLocked()) {
                    powerGroup.setLastUserActivityTimeLocked(j, i);
                    int i5 = this.mDirty | 4;
                    this.mDirty = i5;
                    if (i == 1) {
                        this.mDirty = i5 | IInstalld.FLAG_USE_QUOTA;
                    }
                    return true;
                }
                return false;
            }
            return false;
        } finally {
            Trace.traceEnd(131072L);
        }
    }

    @GuardedBy({"mLock"})
    public final void maybeUpdateForegroundProfileLastActivityLocked(long j) {
        ProfilePowerState profilePowerState = this.mProfilePowerState.get(this.mForegroundProfile);
        if (profilePowerState == null || j <= profilePowerState.mLastUserActivityTime) {
            return;
        }
        profilePowerState.mLastUserActivityTime = j;
    }

    @GuardedBy({"mLock"})
    public final void wakePowerGroupLocked(PowerGroup powerGroup, long j, int i, String str, int i2, String str2, int i3) {
        if (this.mForceSuspendActive || !this.mSystemReady) {
            return;
        }
        powerGroup.wakeUpLocked(j, i, str, i2, str2, i3, LatencyTracker.getInstance(this.mContext));
    }

    @GuardedBy({"mLock"})
    public final boolean dreamPowerGroupLocked(PowerGroup powerGroup, long j, int i, boolean z) {
        if (this.mBootCompleted && this.mSystemReady) {
            return powerGroup.dreamLocked(j, i, z);
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final boolean dozePowerGroupLocked(PowerGroup powerGroup, long j, int i, int i2) {
        if (this.mSystemReady && this.mBootCompleted) {
            return powerGroup.dozeLocked(j, i2, i);
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final boolean sleepPowerGroupLocked(PowerGroup powerGroup, long j, int i, int i2) {
        if (this.mBootCompleted && this.mSystemReady) {
            return powerGroup.sleepLocked(j, i2, i);
        }
        return false;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void setWakefulnessLocked(int i, int i2, long j, int i3, int i4, int i5, String str, String str2) {
        this.mPowerGroups.get(i).setWakefulnessLocked(i2, j, i3, i4, i5, str, str2);
    }

    @GuardedBy({"mLock"})
    public final void updateGlobalWakefulnessLocked(long j, int i, int i2, int i3, String str, String str2) {
        String str3;
        int recalculateGlobalWakefulnessLocked = recalculateGlobalWakefulnessLocked();
        int globalWakefulnessLocked = getGlobalWakefulnessLocked();
        if (globalWakefulnessLocked == recalculateGlobalWakefulnessLocked) {
            return;
        }
        if (recalculateGlobalWakefulnessLocked == 0) {
            Slog.i("PowerManagerService", "Sleeping (uid " + i2 + ")...");
            if (globalWakefulnessLocked != 3) {
                this.mLastGlobalSleepTime = j;
                this.mLastGlobalSleepReason = i;
            }
            str3 = "reallyGoToSleep";
        } else if (recalculateGlobalWakefulnessLocked == 1) {
            Slog.i("PowerManagerService", "Waking up from " + PowerManagerInternal.wakefulnessToString(globalWakefulnessLocked) + " (uid=" + i2 + ", reason=" + PowerManager.wakeReasonToString(i) + ", details=" + str2 + ")...");
            this.mLastGlobalWakeTime = j;
            this.mLastGlobalWakeReason = i;
            this.mLastGlobalWakeTimeRealtime = this.mClock.elapsedRealtime();
            str3 = "wakeUp";
        } else if (recalculateGlobalWakefulnessLocked == 2) {
            Slog.i("PowerManagerService", "Nap time (uid " + i2 + ")...");
            str3 = "nap";
        } else if (recalculateGlobalWakefulnessLocked == 3) {
            Slog.i("PowerManagerService", "Going to sleep due to " + PowerManager.sleepReasonToString(i) + " (uid " + i2 + ", screenOffTimeout=" + this.mScreenOffTimeoutSetting + ", activityTimeoutWM=" + this.mUserActivityTimeoutOverrideFromWindowManager + ", maxDimRatio=" + this.mMaximumScreenDimRatioConfig + ", maxDimDur=" + this.mMaximumScreenDimDurationConfig + ")...");
            this.mLastGlobalSleepTime = j;
            this.mLastGlobalSleepReason = i;
            this.mLastGlobalSleepTimeRealtime = this.mClock.elapsedRealtime();
            this.mDozeStartInProgress = true;
            str3 = "goToSleep";
        } else {
            throw new IllegalArgumentException("Unexpected wakefulness: " + recalculateGlobalWakefulnessLocked);
        }
        Trace.traceBegin(131072L, str3);
        try {
            this.mInjector.invalidateIsInteractiveCaches();
            this.mWakefulnessRaw = recalculateGlobalWakefulnessLocked;
            this.mWakefulnessChanging = true;
            this.mDirty = 2 | this.mDirty;
            this.mDozeStartInProgress &= recalculateGlobalWakefulnessLocked == 3;
            Notifier notifier = this.mNotifier;
            if (notifier != null) {
                notifier.onWakefulnessChangeStarted(recalculateGlobalWakefulnessLocked, i, j);
            }
            this.mAttentionDetector.onWakefulnessChangeStarted(recalculateGlobalWakefulnessLocked);
            if (recalculateGlobalWakefulnessLocked != 0) {
                if (recalculateGlobalWakefulnessLocked == 1) {
                    this.mNotifier.onWakeUp(i, str2, i2, str, i3);
                    if (sQuiescent) {
                        this.mDirty |= IInstalld.FLAG_USE_QUOTA;
                    }
                    PowerGroup powerGroup = this.mPowerGroups.get(0);
                    if (powerGroup.getWakefulnessLocked() == 3) {
                        powerGroup.setSandmanSummonedLocked(true);
                    }
                } else if (recalculateGlobalWakefulnessLocked != 3) {
                }
            }
            if (PowerManagerInternal.isInteractive(globalWakefulnessLocked)) {
                int size = this.mWakeLocks.size();
                int i4 = 0;
                for (int i5 = 0; i5 < size; i5++) {
                    int i6 = this.mWakeLocks.get(i5).mFlags & GnssNative.GNSS_AIDING_TYPE_ALL;
                    if (i6 == 6 || i6 == 10 || i6 == 26) {
                        i4++;
                    }
                }
                EventLogTags.writePowerSleepRequested(i4);
            }
        } finally {
            Trace.traceEnd(131072L);
        }
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public int getGlobalWakefulnessLocked() {
        return this.mWakefulnessRaw;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public int getWakefulnessLocked(int i) {
        return this.mPowerGroups.get(i).getWakefulnessLocked();
    }

    @GuardedBy({"mLock"})
    public int recalculateGlobalWakefulnessLocked() {
        int i = 0;
        for (int i2 = 0; i2 < this.mPowerGroups.size(); i2++) {
            int wakefulnessLocked = this.mPowerGroups.valueAt(i2).getWakefulnessLocked();
            if (wakefulnessLocked == 1) {
                return 1;
            }
            if (wakefulnessLocked == 2 && (i == 0 || i == 3)) {
                i = 2;
            } else if (wakefulnessLocked == 3 && i == 0) {
                i = 3;
            }
        }
        return i;
    }

    @GuardedBy({"mLock"})
    public void onPowerGroupEventLocked(int i, PowerGroup powerGroup) {
        int i2;
        int groupId = powerGroup.getGroupId();
        if (i == 1) {
            this.mPowerGroups.delete(groupId);
        }
        int globalWakefulnessLocked = getGlobalWakefulnessLocked();
        int recalculateGlobalWakefulnessLocked = recalculateGlobalWakefulnessLocked();
        if (i == 0 && recalculateGlobalWakefulnessLocked == 1) {
            userActivityNoUpdateLocked(powerGroup, this.mClock.uptimeMillis(), 0, 0, 1000);
        }
        if (globalWakefulnessLocked != recalculateGlobalWakefulnessLocked) {
            int i3 = 11;
            if (recalculateGlobalWakefulnessLocked != 1) {
                if (recalculateGlobalWakefulnessLocked != 3) {
                    i2 = 0;
                    updateGlobalWakefulnessLocked(this.mClock.uptimeMillis(), i2, 1000, 1000, this.mContext.getOpPackageName(), "groupId: " + groupId);
                } else if (i != 1) {
                    i3 = 12;
                }
            } else if (i == 0) {
                i3 = 10;
            }
            i2 = i3;
            updateGlobalWakefulnessLocked(this.mClock.uptimeMillis(), i2, 1000, 1000, this.mContext.getOpPackageName(), "groupId: " + groupId);
        }
        this.mDirty |= 65536;
        updatePowerStateLocked();
    }

    @GuardedBy({"mLock"})
    public final void logSleepTimeoutRecapturedLocked() {
        long uptimeMillis = this.mOverriddenTimeout - this.mClock.uptimeMillis();
        if (uptimeMillis >= 0) {
            EventLogTags.writePowerSoftSleepRequested(uptimeMillis);
            this.mOverriddenTimeout = -1L;
        }
    }

    @GuardedBy({"mLock"})
    public final void finishWakefulnessChangeIfNeededLocked() {
        if (this.mWakefulnessChanging && areAllPowerGroupsReadyLocked()) {
            if (getGlobalWakefulnessLocked() == 3 && (this.mWakeLockSummary & 64) == 0) {
                return;
            }
            this.mDozeStartInProgress = false;
            if (getGlobalWakefulnessLocked() == 3 || getGlobalWakefulnessLocked() == 0) {
                logSleepTimeoutRecapturedLocked();
            }
            this.mWakefulnessChanging = false;
            this.mNotifier.onWakefulnessChangeFinished();
        }
    }

    @GuardedBy({"mLock"})
    public final boolean areAllPowerGroupsReadyLocked() {
        int size = this.mPowerGroups.size();
        for (int i = 0; i < size; i++) {
            if (!this.mPowerGroups.valueAt(i).isReadyLocked()) {
                return false;
            }
        }
        return true;
    }

    @GuardedBy({"mLock"})
    public final void updatePowerStateLocked() {
        int i;
        if (!this.mSystemReady || this.mDirty == 0 || this.mUpdatePowerStateInProgress) {
            return;
        }
        if (!Thread.holdsLock(this.mLock)) {
            Slog.wtf("PowerManagerService", "Power manager lock was not held when calling updatePowerStateLocked");
        }
        Trace.traceBegin(131072L, "updatePowerState");
        this.mUpdatePowerStateInProgress = true;
        try {
            updateIsPoweredLocked(this.mDirty);
            updateStayOnLocked(this.mDirty);
            updateScreenBrightnessBoostLocked(this.mDirty);
            long uptimeMillis = this.mClock.uptimeMillis();
            int i2 = 0;
            do {
                i = this.mDirty;
                i2 |= i;
                this.mDirty = 0;
                updateWakeLockSummaryLocked(i);
                updateUserActivitySummaryLocked(uptimeMillis, i);
                updateAttentiveStateLocked(uptimeMillis, i);
            } while (updateWakefulnessLocked(i));
            updateProfilesLocked(uptimeMillis);
            updateDreamLocked(i2, updatePowerGroupsLocked(i2));
            finishWakefulnessChangeIfNeededLocked();
            updateSuspendBlockerLocked();
        } finally {
            Trace.traceEnd(131072L);
            this.mUpdatePowerStateInProgress = false;
        }
    }

    @GuardedBy({"mLock"})
    public final void updateProfilesLocked(long j) {
        int size = this.mProfilePowerState.size();
        for (int i = 0; i < size; i++) {
            ProfilePowerState valueAt = this.mProfilePowerState.valueAt(i);
            if (isProfileBeingKeptAwakeLocked(valueAt, j)) {
                valueAt.mLockingNotified = false;
            } else if (!valueAt.mLockingNotified) {
                valueAt.mLockingNotified = true;
                this.mNotifier.onProfileTimeout(valueAt.mUserId);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final boolean isProfileBeingKeptAwakeLocked(ProfilePowerState profilePowerState, long j) {
        if (profilePowerState.mLastUserActivityTime + profilePowerState.mScreenOffTimeout <= j) {
            int i = profilePowerState.mWakeLockSummary;
            if ((i & 32) == 0 && (!this.mProximityPositive || (i & 16) == 0)) {
                return false;
            }
        }
        return true;
    }

    @GuardedBy({"mLock"})
    public final void updateIsPoweredLocked(int i) {
        if ((i & 256) != 0) {
            boolean z = this.mIsPowered;
            int i2 = this.mPlugType;
            this.mIsPowered = this.mBatteryManagerInternal.isPowered(15);
            this.mPlugType = this.mBatteryManagerInternal.getPlugType();
            int i3 = this.mBatteryLevel;
            this.mBatteryLevel = this.mBatteryManagerInternal.getBatteryLevel();
            this.mBatteryLevelLow = this.mBatteryManagerInternal.getBatteryLevelLow();
            if (!(this.mBatteryManagerInternal.getBatteryHealth() == 3) && i3 > 0 && getGlobalWakefulnessLocked() == 2) {
                this.mDreamsBatteryLevelDrain += i3 - this.mBatteryLevel;
            }
            boolean z2 = this.mIsPowered;
            if (z != z2 || i2 != this.mPlugType) {
                this.mDirty |= 64;
                boolean update = this.mWirelessChargerDetector.update(z2, this.mPlugType);
                long uptimeMillis = this.mClock.uptimeMillis();
                if (shouldWakeUpWhenPluggedOrUnpluggedLocked(z, i2, update)) {
                    wakePowerGroupLocked(this.mPowerGroups.get(0), uptimeMillis, 3, "android.server.power:PLUGGED:" + this.mIsPowered, 1000, this.mContext.getOpPackageName(), 1000);
                }
                userActivityNoUpdateLocked(this.mPowerGroups.get(0), uptimeMillis, 0, 0, 1000);
                if (this.mBootCompleted) {
                    if (this.mIsPowered && !BatteryManager.isPlugWired(i2) && BatteryManager.isPlugWired(this.mPlugType)) {
                        this.mNotifier.onWiredChargingStarted(this.mUserId);
                    } else if (update) {
                        this.mNotifier.onWirelessChargingStarted(this.mBatteryLevel, this.mUserId);
                    }
                }
            }
            this.mBatterySaverStateMachine.setBatteryStatus(this.mIsPowered, this.mBatteryLevel, this.mBatteryLevelLow);
        }
    }

    @GuardedBy({"mLock"})
    public final boolean shouldWakeUpWhenPluggedOrUnpluggedLocked(boolean z, int i, boolean z2) {
        if (this.mWakeUpWhenPluggedOrUnpluggedConfig) {
            if (this.mKeepDreamingWhenUndocked && getGlobalWakefulnessLocked() == 2 && z && !this.mIsPowered && i == 8) {
                return false;
            }
            if (z && !this.mIsPowered && i == 4) {
                return false;
            }
            if (z || !this.mIsPowered || this.mPlugType != 4 || z2) {
                if (this.mIsPowered && getGlobalWakefulnessLocked() == 2) {
                    return false;
                }
                if (!this.mTheaterModeEnabled || this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig) {
                    return (this.mAlwaysOnEnabled && getGlobalWakefulnessLocked() == 3) ? false : true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final void updateStayOnLocked(int i) {
        if ((i & 288) != 0) {
            boolean z = this.mStayOn;
            if (this.mStayOnWhilePluggedInSetting != 0 && !isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked()) {
                this.mStayOn = this.mBatteryManagerInternal.isPowered(this.mStayOnWhilePluggedInSetting);
            } else {
                this.mStayOn = false;
            }
            if (this.mStayOn != z) {
                this.mDirty |= 128;
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void updateWakeLockSummaryLocked(int i) {
        if ((i & 65539) != 0) {
            this.mWakeLockSummary = 0;
            int size = this.mProfilePowerState.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.mProfilePowerState.valueAt(i2).mWakeLockSummary = 0;
            }
            for (int i3 = 0; i3 < this.mPowerGroups.size(); i3++) {
                this.mPowerGroups.valueAt(i3).setWakeLockSummaryLocked(0);
            }
            int size2 = this.mWakeLocks.size();
            int i4 = 0;
            for (int i5 = 0; i5 < size2; i5++) {
                WakeLock wakeLock = this.mWakeLocks.get(i5);
                Integer powerGroupId = wakeLock.getPowerGroupId();
                if (powerGroupId != null && (powerGroupId.intValue() == -1 || this.mPowerGroups.contains(powerGroupId.intValue()))) {
                    PowerGroup powerGroup = this.mPowerGroups.get(powerGroupId.intValue());
                    int wakeLockSummaryFlags = getWakeLockSummaryFlags(wakeLock);
                    this.mWakeLockSummary |= wakeLockSummaryFlags;
                    if (powerGroupId.intValue() != -1) {
                        powerGroup.setWakeLockSummaryLocked(powerGroup.getWakeLockSummaryLocked() | wakeLockSummaryFlags);
                    } else {
                        i4 |= wakeLockSummaryFlags;
                    }
                    for (int i6 = 0; i6 < size; i6++) {
                        ProfilePowerState valueAt = this.mProfilePowerState.valueAt(i6);
                        if (wakeLockAffectsUser(wakeLock, valueAt.mUserId)) {
                            valueAt.mWakeLockSummary |= wakeLockSummaryFlags;
                        }
                    }
                }
            }
            for (int i7 = 0; i7 < this.mPowerGroups.size(); i7++) {
                PowerGroup valueAt2 = this.mPowerGroups.valueAt(i7);
                valueAt2.setWakeLockSummaryLocked(adjustWakeLockSummary(valueAt2.getWakefulnessLocked(), valueAt2.getWakeLockSummaryLocked() | i4));
            }
            this.mWakeLockSummary = adjustWakeLockSummary(getGlobalWakefulnessLocked(), this.mWakeLockSummary);
            for (int i8 = 0; i8 < size; i8++) {
                ProfilePowerState valueAt3 = this.mProfilePowerState.valueAt(i8);
                valueAt3.mWakeLockSummary = adjustWakeLockSummary(getGlobalWakefulnessLocked(), valueAt3.mWakeLockSummary);
            }
        }
    }

    public final int getWakeLockSummaryFlags(WakeLock wakeLock) {
        int i = wakeLock.mFlags & GnssNative.GNSS_AIDING_TYPE_ALL;
        if (i == 1) {
            return !wakeLock.mDisabled ? 1 : 0;
        } else if (i != 6) {
            if (i != 10) {
                if (i != 26) {
                    if (i != 32) {
                        int i2 = 64;
                        if (i != 64) {
                            i2 = 128;
                            if (i != 128) {
                                return 0;
                            }
                        }
                        return i2;
                    }
                    return 16;
                }
                return 10;
            }
            return 2;
        } else {
            return 4;
        }
    }

    public final boolean wakeLockAffectsUser(WakeLock wakeLock, int i) {
        if (wakeLock.mWorkSource != null) {
            for (int i2 = 0; i2 < wakeLock.mWorkSource.size(); i2++) {
                if (i == UserHandle.getUserId(wakeLock.mWorkSource.getUid(i2))) {
                    return true;
                }
            }
            List workChains = wakeLock.mWorkSource.getWorkChains();
            if (workChains != null) {
                for (int i3 = 0; i3 < workChains.size(); i3++) {
                    if (i == UserHandle.getUserId(((WorkSource.WorkChain) workChains.get(i3)).getAttributionUid())) {
                        return true;
                    }
                }
            }
        }
        return i == UserHandle.getUserId(wakeLock.mOwnerUid);
    }

    public void checkForLongWakeLocks() {
        synchronized (this.mLock) {
            long uptimeMillis = this.mClock.uptimeMillis();
            this.mNotifyLongDispatched = uptimeMillis;
            long j = uptimeMillis - 60000;
            int size = this.mWakeLocks.size();
            long j2 = Long.MAX_VALUE;
            for (int i = 0; i < size; i++) {
                WakeLock wakeLock = this.mWakeLocks.get(i);
                if ((wakeLock.mFlags & GnssNative.GNSS_AIDING_TYPE_ALL) == 1 && wakeLock.mNotifiedAcquired && !wakeLock.mNotifiedLong) {
                    long j3 = wakeLock.mAcquireTime;
                    if (j3 < j) {
                        notifyWakeLockLongStartedLocked(wakeLock);
                    } else {
                        long j4 = j3 + 60000;
                        if (j4 < j2) {
                            j2 = j4;
                        }
                    }
                }
            }
            this.mNotifyLongScheduled = 0L;
            this.mHandler.removeMessages(4);
            if (j2 != Long.MAX_VALUE) {
                this.mNotifyLongNextCheck = j2;
                enqueueNotifyLongMsgLocked(j2);
            } else {
                this.mNotifyLongNextCheck = 0L;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x009c  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00c3 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00db  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x00f1  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00f5  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x00f9  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00fb  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0107  */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updateUserActivitySummaryLocked(long j, int i) {
        long j2;
        int i2;
        long j3;
        int i3;
        long j4;
        int i4;
        int i5;
        long j5;
        long j6;
        if ((i & 81959) == 0) {
            return;
        }
        this.mHandler.removeMessages(1);
        long attentiveTimeoutLocked = getAttentiveTimeoutLocked();
        long sleepTimeoutLocked = getSleepTimeoutLocked(attentiveTimeoutLocked);
        long screenOffTimeoutLocked = getScreenOffTimeoutLocked(sleepTimeoutLocked, attentiveTimeoutLocked);
        long screenDimDurationLocked = getScreenDimDurationLocked(screenOffTimeoutLocked);
        long screenOffTimeoutWithFaceDownLocked = getScreenOffTimeoutWithFaceDownLocked(screenOffTimeoutLocked, screenDimDurationLocked);
        boolean z = this.mUserInactiveOverrideFromWindowManager;
        int i6 = 0;
        long j7 = -1;
        boolean z2 = false;
        while (i6 < this.mPowerGroups.size()) {
            PowerGroup valueAt = this.mPowerGroups.valueAt(i6);
            if (valueAt.getWakefulnessLocked() != 0) {
                long lastUserActivityTimeLocked = valueAt.getLastUserActivityTimeLocked();
                i2 = i6;
                long lastUserActivityTimeNoChangeLightsLocked = valueAt.getLastUserActivityTimeNoChangeLightsLocked();
                int i7 = 2;
                if (lastUserActivityTimeLocked >= valueAt.getLastWakeTimeLocked()) {
                    j4 = lastUserActivityTimeLocked + screenOffTimeoutWithFaceDownLocked;
                    long j8 = j4 - screenDimDurationLocked;
                    if (j < j8) {
                        j4 = j8;
                        i4 = 1;
                    } else if (j < j4) {
                        i4 = 2;
                    }
                    if (i4 == 0 && lastUserActivityTimeNoChangeLightsLocked >= valueAt.getLastWakeTimeLocked()) {
                        j4 = lastUserActivityTimeNoChangeLightsLocked + screenOffTimeoutWithFaceDownLocked;
                        if (j < j4) {
                            if (valueAt.isPolicyBrightLocked()) {
                                j2 = screenOffTimeoutWithFaceDownLocked;
                                i7 = 1;
                            } else if (valueAt.isPolicyDimLocked()) {
                                j2 = screenOffTimeoutWithFaceDownLocked;
                            }
                            i3 = 4;
                            if (i7 != 0) {
                                i5 = i7;
                                j5 = j4;
                            } else if (sleepTimeoutLocked >= 0) {
                                long max = Math.max(lastUserActivityTimeLocked, lastUserActivityTimeNoChangeLightsLocked);
                                if (max >= valueAt.getLastWakeTimeLocked()) {
                                    j5 = max + sleepTimeoutLocked;
                                    if (j < j5) {
                                        i7 = 4;
                                    }
                                } else {
                                    j5 = j4;
                                }
                                i5 = i7;
                            } else {
                                i5 = 4;
                                j5 = -1;
                            }
                            if (i5 == 4 && z) {
                                if ((i5 & 3) != 0 && this.mOverriddenTimeout == -1) {
                                    this.mOverriddenTimeout = j5;
                                }
                                j5 = -1;
                            } else {
                                i3 = i5;
                            }
                            if ((i3 & 1) != 0 && (valueAt.getWakeLockSummaryLocked() & 32) == 0) {
                                j5 = this.mAttentionDetector.updateUserActivity(j5, screenDimDurationLocked);
                            }
                            long j9 = j5;
                            if (isAttentiveTimeoutExpired(valueAt, j)) {
                                i3 = 0;
                                j6 = -1;
                            } else {
                                j6 = j9;
                            }
                            z2 |= i3 != 0;
                            j3 = j7;
                            if (j3 != -1) {
                                if (j6 != -1) {
                                    j6 = Math.min(j3, j6);
                                }
                            }
                            j3 = j6;
                        }
                    }
                    j2 = screenOffTimeoutWithFaceDownLocked;
                    i7 = i4;
                    i3 = 4;
                    if (i7 != 0) {
                    }
                    if (i5 == 4) {
                    }
                    i3 = i5;
                    if ((i3 & 1) != 0) {
                        j5 = this.mAttentionDetector.updateUserActivity(j5, screenDimDurationLocked);
                    }
                    long j92 = j5;
                    if (isAttentiveTimeoutExpired(valueAt, j)) {
                    }
                    z2 |= i3 != 0;
                    j3 = j7;
                    if (j3 != -1) {
                    }
                    j3 = j6;
                } else {
                    j4 = 0;
                }
                i4 = 0;
                if (i4 == 0) {
                    j4 = lastUserActivityTimeNoChangeLightsLocked + screenOffTimeoutWithFaceDownLocked;
                    if (j < j4) {
                    }
                }
                j2 = screenOffTimeoutWithFaceDownLocked;
                i7 = i4;
                i3 = 4;
                if (i7 != 0) {
                }
                if (i5 == 4) {
                }
                i3 = i5;
                if ((i3 & 1) != 0) {
                }
                long j922 = j5;
                if (isAttentiveTimeoutExpired(valueAt, j)) {
                }
                z2 |= i3 != 0;
                j3 = j7;
                if (j3 != -1) {
                }
                j3 = j6;
            } else {
                j2 = screenOffTimeoutWithFaceDownLocked;
                i2 = i6;
                j3 = j7;
                i3 = 0;
            }
            valueAt.setUserActivitySummaryLocked(i3);
            j7 = j3;
            screenOffTimeoutWithFaceDownLocked = j2;
            i6 = i2 + 1;
        }
        long j10 = j7;
        long nextProfileTimeoutLocked = getNextProfileTimeoutLocked(j);
        if (nextProfileTimeoutLocked > 0) {
            j10 = Math.min(j10, nextProfileTimeoutLocked);
        }
        if (!z2 || j10 < 0) {
            return;
        }
        scheduleUserInactivityTimeout(j10);
    }

    public final void scheduleUserInactivityTimeout(long j) {
        Message obtainMessage = this.mHandler.obtainMessage(1);
        obtainMessage.setAsynchronous(true);
        this.mHandler.sendMessageAtTime(obtainMessage, j);
    }

    public final void scheduleAttentiveTimeout(long j) {
        Message obtainMessage = this.mHandler.obtainMessage(5);
        obtainMessage.setAsynchronous(true);
        this.mHandler.sendMessageAtTime(obtainMessage, j);
    }

    @GuardedBy({"mLock"})
    public final long getNextProfileTimeoutLocked(long j) {
        int size = this.mProfilePowerState.size();
        long j2 = -1;
        for (int i = 0; i < size; i++) {
            ProfilePowerState valueAt = this.mProfilePowerState.valueAt(i);
            long j3 = valueAt.mLastUserActivityTime + valueAt.mScreenOffTimeout;
            if (j3 > j && (j2 == -1 || j3 < j2)) {
                j2 = j3;
            }
        }
        return j2;
    }

    @GuardedBy({"mLock"})
    public final void updateAttentiveStateLocked(long j, int i) {
        long attentiveTimeoutLocked = getAttentiveTimeoutLocked();
        long lastUserActivityTimeLocked = this.mPowerGroups.get(0).getLastUserActivityTimeLocked() + attentiveTimeoutLocked;
        long j2 = lastUserActivityTimeLocked - this.mAttentiveWarningDurationConfig;
        boolean maybeHideInattentiveSleepWarningLocked = maybeHideInattentiveSleepWarningLocked(j, j2);
        if (attentiveTimeoutLocked >= 0) {
            if (maybeHideInattentiveSleepWarningLocked || (i & 19122) != 0) {
                this.mHandler.removeMessages(5);
                if (getGlobalWakefulnessLocked() == 0 || isBeingKeptFromInattentiveSleepLocked()) {
                    return;
                }
                if (j < j2) {
                    lastUserActivityTimeLocked = j2;
                } else if (j < lastUserActivityTimeLocked) {
                    this.mInattentiveSleepWarningOverlayController.show();
                } else {
                    lastUserActivityTimeLocked = -1;
                }
                if (lastUserActivityTimeLocked >= 0) {
                    scheduleAttentiveTimeout(lastUserActivityTimeLocked);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final boolean maybeHideInattentiveSleepWarningLocked(long j, long j2) {
        long attentiveTimeoutLocked = getAttentiveTimeoutLocked();
        if (this.mInattentiveSleepWarningOverlayController.isShown()) {
            if (getGlobalWakefulnessLocked() == 0) {
                this.mInattentiveSleepWarningOverlayController.dismiss(false);
                return true;
            } else if (attentiveTimeoutLocked < 0 || isBeingKeptFromInattentiveSleepLocked() || j < j2) {
                this.mInattentiveSleepWarningOverlayController.dismiss(true);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final boolean isAttentiveTimeoutExpired(PowerGroup powerGroup, long j) {
        long attentiveTimeoutLocked = getAttentiveTimeoutLocked();
        return powerGroup.getGroupId() == 0 && attentiveTimeoutLocked >= 0 && j >= powerGroup.getLastUserActivityTimeLocked() + attentiveTimeoutLocked;
    }

    public final void handleUserActivityTimeout() {
        synchronized (this.mLock) {
            this.mDirty |= 4;
            updatePowerStateLocked();
        }
    }

    public final void handleAttentiveTimeout() {
        synchronized (this.mLock) {
            this.mDirty |= 16384;
            updatePowerStateLocked();
        }
    }

    @GuardedBy({"mLock"})
    public final long getAttentiveTimeoutLocked() {
        long j = this.mAttentiveTimeoutSetting;
        if (j <= 0) {
            return -1L;
        }
        return Math.max(j, this.mMinimumScreenOffTimeoutConfig);
    }

    @GuardedBy({"mLock"})
    public final long getSleepTimeoutLocked(long j) {
        long j2 = this.mSleepTimeoutSetting;
        if (j2 <= 0) {
            return -1L;
        }
        if (j >= 0) {
            j2 = Math.min(j2, j);
        }
        return Math.max(j2, this.mMinimumScreenOffTimeoutConfig);
    }

    @GuardedBy({"mLock"})
    public final long getScreenOffTimeoutLocked(long j, long j2) {
        long j3 = this.mScreenOffTimeoutSetting;
        if (isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked()) {
            j3 = Math.min(j3, this.mMaximumScreenOffTimeoutFromDeviceAdmin);
        }
        long j4 = this.mUserActivityTimeoutOverrideFromWindowManager;
        if (j4 >= 0) {
            j3 = Math.min(j3, j4);
        }
        if (j >= 0) {
            j3 = Math.min(j3, j);
        }
        if (j2 >= 0) {
            j3 = Math.min(j3, j2);
        }
        return Math.max(j3, this.mMinimumScreenOffTimeoutConfig);
    }

    @GuardedBy({"mLock"})
    public final long getScreenDimDurationLocked(long j) {
        return Math.min(this.mMaximumScreenDimDurationConfig, ((float) j) * this.mMaximumScreenDimRatioConfig);
    }

    @GuardedBy({"mLock"})
    public final long getScreenOffTimeoutWithFaceDownLocked(long j, long j2) {
        return this.mIsFaceDown ? Math.min(j2, j) : j;
    }

    @GuardedBy({"mLock"})
    public final boolean updateWakefulnessLocked(int i) {
        if ((i & 20151) == 0) {
            return false;
        }
        long uptimeMillis = this.mClock.uptimeMillis();
        boolean z = false;
        for (int i2 = 0; i2 < this.mPowerGroups.size(); i2++) {
            PowerGroup valueAt = this.mPowerGroups.valueAt(i2);
            if (valueAt.getWakefulnessLocked() == 1 && isItBedTimeYetLocked(valueAt)) {
                if (isAttentiveTimeoutExpired(valueAt, uptimeMillis)) {
                    z = sleepPowerGroupLocked(valueAt, uptimeMillis, 9, 1000);
                } else if (shouldNapAtBedTimeLocked()) {
                    z = dreamPowerGroupLocked(valueAt, uptimeMillis, 1000, false);
                } else {
                    z = dozePowerGroupLocked(valueAt, uptimeMillis, 2, 1000);
                }
            }
        }
        return z;
    }

    @GuardedBy({"mLock"})
    public final boolean shouldNapAtBedTimeLocked() {
        return this.mDreamsActivateOnSleepSetting || (this.mDreamsActivateOnDockSetting && this.mDockState != 0);
    }

    @GuardedBy({"mLock"})
    public final boolean isItBedTimeYetLocked(PowerGroup powerGroup) {
        if (this.mBootCompleted) {
            if (isAttentiveTimeoutExpired(powerGroup, this.mClock.uptimeMillis())) {
                return !isBeingKeptFromInattentiveSleepLocked();
            }
            return !isBeingKeptAwakeLocked(powerGroup);
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final boolean isBeingKeptAwakeLocked(PowerGroup powerGroup) {
        return this.mStayOn || this.mProximityPositive || (powerGroup.getWakeLockSummaryLocked() & 32) != 0 || (powerGroup.getUserActivitySummaryLocked() & 3) != 0 || this.mScreenBrightnessBoostInProgress;
    }

    @GuardedBy({"mLock"})
    public final boolean isBeingKeptFromInattentiveSleepLocked() {
        return this.mStayOn || this.mScreenBrightnessBoostInProgress || this.mProximityPositive || !this.mBootCompleted;
    }

    @GuardedBy({"mLock"})
    public final void updateDreamLocked(int i, boolean z) {
        if (((i & 17407) != 0 || z) && areAllPowerGroupsReadyLocked()) {
            scheduleSandmanLocked();
        }
    }

    @GuardedBy({"mLock"})
    public final void scheduleSandmanLocked() {
        if (this.mSandmanScheduled) {
            return;
        }
        this.mSandmanScheduled = true;
        for (int i = 0; i < this.mPowerGroups.size(); i++) {
            PowerGroup valueAt = this.mPowerGroups.valueAt(i);
            if (valueAt.supportsSandmanLocked()) {
                Message obtainMessage = this.mHandler.obtainMessage(2);
                obtainMessage.arg1 = valueAt.getGroupId();
                obtainMessage.setAsynchronous(true);
                this.mHandler.sendMessageAtTime(obtainMessage, this.mClock.uptimeMillis());
            }
        }
    }

    public final void handleSandman(int i) {
        boolean z;
        boolean z2;
        synchronized (this.mLock) {
            this.mSandmanScheduled = false;
            if (this.mPowerGroups.contains(i)) {
                PowerGroup powerGroup = this.mPowerGroups.get(i);
                int wakefulnessLocked = powerGroup.getWakefulnessLocked();
                if (powerGroup.isSandmanSummonedLocked() && powerGroup.isReadyLocked()) {
                    if (!canDreamLocked(powerGroup) && !canDozeLocked(powerGroup)) {
                        z = false;
                        powerGroup.setSandmanSummonedLocked(false);
                    }
                    z = true;
                    powerGroup.setSandmanSummonedLocked(false);
                } else {
                    z = false;
                }
                DreamManagerInternal dreamManagerInternal = this.mDreamManager;
                if (dreamManagerInternal != null) {
                    if (z) {
                        dreamManagerInternal.stopDream(false, "power manager request before starting dream");
                        this.mDreamManager.startDream(wakefulnessLocked == 3, "power manager request");
                    }
                    z2 = this.mDreamManager.isDreaming();
                } else {
                    z2 = false;
                }
                this.mDozeStartInProgress = false;
                synchronized (this.mLock) {
                    if (this.mPowerGroups.contains(i)) {
                        if (z && z2) {
                            this.mDreamsBatteryLevelDrain = 0;
                            if (wakefulnessLocked == 3) {
                                Slog.i("PowerManagerService", "Dozing...");
                            } else {
                                Slog.i("PowerManagerService", "Dreaming...");
                            }
                        }
                        PowerGroup powerGroup2 = this.mPowerGroups.get(i);
                        if (!powerGroup2.isSandmanSummonedLocked() && powerGroup2.getWakefulnessLocked() == wakefulnessLocked) {
                            long uptimeMillis = this.mClock.uptimeMillis();
                            if (wakefulnessLocked == 2) {
                                if (z2 && canDreamLocked(powerGroup2)) {
                                    int i2 = this.mDreamsBatteryLevelDrainCutoffConfig;
                                    if (i2 < 0 || this.mDreamsBatteryLevelDrain <= i2 || isBeingKeptAwakeLocked(powerGroup2)) {
                                        return;
                                    }
                                    Slog.i("PowerManagerService", "Stopping dream because the battery appears to be draining faster than it is charging.  Battery level drained while dreaming: " + this.mDreamsBatteryLevelDrain + "%.  Battery level now: " + this.mBatteryLevel + "%.");
                                }
                                if (isItBedTimeYetLocked(powerGroup2)) {
                                    if (isAttentiveTimeoutExpired(powerGroup2, uptimeMillis)) {
                                        sleepPowerGroupLocked(powerGroup2, uptimeMillis, 2, 1000);
                                    } else {
                                        dozePowerGroupLocked(powerGroup2, uptimeMillis, 2, 1000);
                                    }
                                } else {
                                    wakePowerGroupLocked(powerGroup2, uptimeMillis, 13, "android.server.power:DREAM_FINISHED", 1000, this.mContext.getOpPackageName(), 1000);
                                }
                            } else if (wakefulnessLocked == 3) {
                                if (z2) {
                                    return;
                                }
                                sleepPowerGroupLocked(powerGroup2, uptimeMillis, 2, 1000);
                            }
                            if (z2) {
                                this.mDreamManager.stopDream(false, "power manager request");
                            }
                        }
                    }
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void onDreamSuppressionChangedLocked(boolean z) {
        if (this.mDreamsDisabledByAmbientModeSuppressionConfig) {
            if (!z && this.mIsPowered && this.mDreamsSupportedConfig && this.mDreamsEnabledSetting && shouldNapAtBedTimeLocked() && isItBedTimeYetLocked(this.mPowerGroups.get(0))) {
                napInternal(SystemClock.uptimeMillis(), 1000, true);
            } else if (z) {
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final boolean canDreamLocked(PowerGroup powerGroup) {
        int i;
        int i2;
        boolean z = this.mDreamsDisabledByAmbientModeSuppressionConfig && this.mAmbientDisplaySuppressionController.isSuppressed();
        if (this.mBootCompleted && !z && getGlobalWakefulnessLocked() == 2 && this.mDreamsSupportedConfig && this.mDreamsEnabledSetting && powerGroup.isBrightOrDimLocked() && (powerGroup.getUserActivitySummaryLocked() & 7) != 0) {
            if (isBeingKeptAwakeLocked(powerGroup)) {
                return true;
            }
            boolean z2 = this.mIsPowered;
            if (z2 || this.mDreamsEnabledOnBatteryConfig) {
                if (z2 || (i2 = this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig) < 0 || this.mBatteryLevel >= i2) {
                    return !z2 || (i = this.mDreamsBatteryLevelMinimumWhenPoweredConfig) < 0 || this.mBatteryLevel >= i;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final boolean canDozeLocked(PowerGroup powerGroup) {
        return powerGroup.supportsSandmanLocked() && powerGroup.getWakefulnessLocked() == 3;
    }

    @GuardedBy({"mLock"})
    public final boolean updatePowerGroupsLocked(int i) {
        boolean z;
        boolean z2;
        boolean z3;
        float f;
        boolean areAllPowerGroupsReadyLocked = areAllPowerGroupsReadyLocked();
        boolean z4 = false;
        if ((71743 & i) != 0) {
            if ((i & IInstalld.FLAG_USE_QUOTA) != 0) {
                if (areAllPowerGroupsReadyLocked()) {
                    sQuiescent = false;
                } else {
                    this.mDirty |= IInstalld.FLAG_USE_QUOTA;
                }
            }
            int i2 = 0;
            while (i2 < this.mPowerGroups.size()) {
                PowerGroup valueAt = this.mPowerGroups.valueAt(i2);
                int groupId = valueAt.getGroupId();
                if (!this.mBootCompleted) {
                    f = this.mScreenBrightnessDefault;
                } else {
                    f = isValidBrightness(this.mScreenBrightnessOverrideFromWindowManager) ? this.mScreenBrightnessOverrideFromWindowManager : Float.NaN;
                }
                boolean z5 = areAllPowerGroupsReadyLocked;
                int i3 = i2;
                boolean updateLocked = valueAt.updateLocked(f, shouldUseProximitySensorLocked(), shouldBoostScreenBrightness(), this.mDozeScreenStateOverrideFromDreamManager, this.mDozeScreenBrightnessOverrideFromDreamManagerFloat, this.mDrawWakeLockOverrideFromSidekick, this.mBatterySaverPolicy.getBatterySaverPolicy(7), sQuiescent, this.mDozeAfterScreenOff, this.mBootCompleted, this.mScreenBrightnessBoostInProgress, this.mRequestWaitForNegativeProximity);
                int wakefulnessLocked = valueAt.getWakefulnessLocked();
                boolean readyLocked = valueAt.setReadyLocked(updateLocked);
                boolean isPoweringOnLocked = valueAt.isPoweringOnLocked();
                if (updateLocked && readyLocked && isPoweringOnLocked) {
                    if (wakefulnessLocked == 1) {
                        valueAt.setIsPoweringOnLocked(false);
                        LatencyTracker.getInstance(this.mContext).onActionEnd(5);
                        Trace.asyncTraceEnd(131072L, "Screen turning on", groupId);
                        int uptimeMillis = (int) (this.mClock.uptimeMillis() - valueAt.getLastPowerOnTimeLocked());
                        if (uptimeMillis >= 200) {
                            Slog.w("PowerManagerService", "Screen on took " + uptimeMillis + " ms");
                        }
                    }
                }
                i2 = i3 + 1;
                areAllPowerGroupsReadyLocked = z5;
                z4 = false;
            }
            z = areAllPowerGroupsReadyLocked;
            z2 = z4;
            z3 = true;
            this.mRequestWaitForNegativeProximity = z2;
        } else {
            z = areAllPowerGroupsReadyLocked;
            z2 = false;
            z3 = true;
        }
        return (!areAllPowerGroupsReadyLocked() || z) ? z2 : z3;
    }

    @GuardedBy({"mLock"})
    public final void updateScreenBrightnessBoostLocked(int i) {
        if ((i & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) == 0 || !this.mScreenBrightnessBoostInProgress) {
            return;
        }
        long uptimeMillis = this.mClock.uptimeMillis();
        this.mHandler.removeMessages(3);
        long j = this.mLastScreenBrightnessBoostTime;
        if (j > this.mLastGlobalSleepTime) {
            long j2 = j + 5000;
            if (j2 > uptimeMillis) {
                Message obtainMessage = this.mHandler.obtainMessage(3);
                obtainMessage.setAsynchronous(true);
                this.mHandler.sendMessageAtTime(obtainMessage, j2);
                return;
            }
        }
        this.mScreenBrightnessBoostInProgress = false;
        userActivityNoUpdateLocked(uptimeMillis, 0, 0, 1000);
    }

    public final boolean shouldBoostScreenBrightness() {
        return this.mScreenBrightnessBoostInProgress;
    }

    @GuardedBy({"mLock"})
    @VisibleForTesting
    public int getDesiredScreenPolicyLocked(int i) {
        return this.mPowerGroups.get(i).getDesiredScreenPolicyLocked(sQuiescent, this.mDozeAfterScreenOff, this.mBootCompleted, this.mScreenBrightnessBoostInProgress);
    }

    @VisibleForTesting
    public int getDreamsBatteryLevelDrain() {
        return this.mDreamsBatteryLevelDrain;
    }

    @GuardedBy({"mLock"})
    public final boolean shouldUseProximitySensorLocked() {
        return (this.mPowerGroups.get(0).getWakeLockSummaryLocked() & 16) != 0;
    }

    @GuardedBy({"mLock"})
    public final void updateSuspendBlockerLocked() {
        boolean z = (this.mWakeLockSummary & 1) != 0;
        boolean needSuspendBlockerLocked = needSuspendBlockerLocked();
        boolean z2 = !needSuspendBlockerLocked;
        boolean z3 = false;
        for (int i = 0; i < this.mPowerGroups.size() && !z3; i++) {
            z3 = this.mPowerGroups.valueAt(i).isBrightOrDimLocked();
        }
        if (!z2 && this.mDecoupleHalAutoSuspendModeFromDisplayConfig) {
            setHalAutoSuspendModeLocked(false);
        }
        if (!this.mBootCompleted && !this.mHoldingBootingSuspendBlocker) {
            this.mBootingSuspendBlocker.acquire();
            this.mHoldingBootingSuspendBlocker = true;
        }
        if (z && !this.mHoldingWakeLockSuspendBlocker) {
            this.mWakeLockSuspendBlocker.acquire();
            this.mHoldingWakeLockSuspendBlocker = true;
        }
        if (needSuspendBlockerLocked && !this.mHoldingDisplaySuspendBlocker) {
            this.mDisplaySuspendBlocker.acquire("holding display");
            this.mHoldingDisplaySuspendBlocker = true;
        }
        if (this.mDecoupleHalInteractiveModeFromDisplayConfig && (z3 || areAllPowerGroupsReadyLocked())) {
            setHalInteractiveModeLocked(z3);
        }
        if (this.mBootCompleted && this.mHoldingBootingSuspendBlocker) {
            this.mBootingSuspendBlocker.release();
            this.mHoldingBootingSuspendBlocker = false;
        }
        if (!z && this.mHoldingWakeLockSuspendBlocker) {
            this.mWakeLockSuspendBlocker.release();
            this.mHoldingWakeLockSuspendBlocker = false;
        }
        if (!needSuspendBlockerLocked && this.mHoldingDisplaySuspendBlocker) {
            this.mDisplaySuspendBlocker.release("holding display");
            this.mHoldingDisplaySuspendBlocker = false;
        }
        if (z2 && this.mDecoupleHalAutoSuspendModeFromDisplayConfig) {
            setHalAutoSuspendModeLocked(true);
        }
    }

    @GuardedBy({"mLock"})
    public final boolean needSuspendBlockerLocked() {
        if (areAllPowerGroupsReadyLocked() && !this.mScreenBrightnessBoostInProgress) {
            if (getGlobalWakefulnessLocked() == 3 && this.mDozeStartInProgress) {
                return true;
            }
            for (int i = 0; i < this.mPowerGroups.size(); i++) {
                if (this.mPowerGroups.valueAt(i).needSuspendBlockerLocked(this.mProximityPositive, this.mSuspendWhenScreenOffDueToProximityConfig)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @GuardedBy({"mLock"})
    public final void setHalAutoSuspendModeLocked(boolean z) {
        if (z != this.mHalAutoSuspendModeEnabled) {
            this.mHalAutoSuspendModeEnabled = z;
            Trace.traceBegin(131072L, "setHalAutoSuspend(" + z + ")");
            try {
                this.mNativeWrapper.nativeSetAutoSuspend(z);
            } finally {
                Trace.traceEnd(131072L);
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void setHalInteractiveModeLocked(boolean z) {
        if (z != this.mHalInteractiveModeEnabled) {
            this.mHalInteractiveModeEnabled = z;
            Trace.traceBegin(131072L, "setHalInteractive(" + z + ")");
            try {
                this.mNativeWrapper.nativeSetPowerMode(7, z);
            } finally {
                Trace.traceEnd(131072L);
            }
        }
    }

    public final boolean isInteractiveInternal() {
        boolean isInteractive;
        synchronized (this.mLock) {
            isInteractive = PowerManagerInternal.isInteractive(getGlobalWakefulnessLocked());
        }
        return isInteractive;
    }

    public final boolean setLowPowerModeInternal(boolean z) {
        synchronized (this.mLock) {
            if (this.mIsPowered) {
                return false;
            }
            this.mBatterySaverStateMachine.setBatterySaverEnabledManually(z);
            return true;
        }
    }

    public boolean isDeviceIdleModeInternal() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDeviceIdleMode;
        }
        return z;
    }

    public boolean isLightDeviceIdleModeInternal() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mLightDeviceIdleMode;
        }
        return z;
    }

    @GuardedBy({"mLock"})
    public final void handleBatteryStateChangedLocked() {
        this.mDirty |= 256;
        updatePowerStateLocked();
    }

    public final void shutdownOrRebootInternal(final int i, final boolean z, final String str, boolean z2) {
        if ("userspace".equals(str)) {
            if (!PowerManager.isRebootingUserspaceSupportedImpl()) {
                throw new UnsupportedOperationException("Attempted userspace reboot on a device that doesn't support it");
            }
            UserspaceRebootLogger.noteUserspaceRebootWasRequested();
        }
        if (this.mHandler == null || !this.mSystemReady) {
            if (RescueParty.isAttemptingFactoryReset()) {
                lowLevelReboot(str);
            } else {
                throw new IllegalStateException("Too early to call shutdown() or reboot()");
            }
        }
        Runnable runnable = new Runnable() { // from class: com.android.server.power.PowerManagerService.2
            @Override // java.lang.Runnable
            public void run() {
                synchronized (this) {
                    int i2 = i;
                    if (i2 == 2) {
                        ShutdownThread.rebootSafeMode(PowerManagerService.this.getUiContext(), z);
                    } else if (i2 == 1) {
                        ShutdownThread.reboot(PowerManagerService.this.getUiContext(), str, z);
                    } else {
                        ShutdownThread.shutdown(PowerManagerService.this.getUiContext(), str, z);
                    }
                }
            }
        };
        Message obtain = Message.obtain(UiThread.getHandler(), runnable);
        obtain.setAsynchronous(true);
        UiThread.getHandler().sendMessage(obtain);
        if (!z2) {
            return;
        }
        synchronized (runnable) {
            while (true) {
                try {
                    runnable.wait();
                } catch (InterruptedException unused) {
                }
            }
        }
    }

    public final void crashInternal(final String str) {
        Thread thread = new Thread("PowerManagerService.crash()") { // from class: com.android.server.power.PowerManagerService.3
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                throw new RuntimeException(str);
            }
        };
        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            Slog.wtf("PowerManagerService", e);
        }
    }

    public void setStayOnSettingInternal(int i) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", i);
    }

    public void setMaximumScreenOffTimeoutFromDeviceAdminInternal(int i, long j) {
        if (i < 0) {
            Slog.wtf("PowerManagerService", "Attempt to set screen off timeout for invalid user: " + i);
            return;
        }
        synchronized (this.mLock) {
            try {
                if (i == 0) {
                    this.mMaximumScreenOffTimeoutFromDeviceAdmin = j;
                } else {
                    if (j != Long.MAX_VALUE && j != 0) {
                        ProfilePowerState profilePowerState = this.mProfilePowerState.get(i);
                        if (profilePowerState != null) {
                            profilePowerState.mScreenOffTimeout = j;
                        } else {
                            this.mProfilePowerState.put(i, new ProfilePowerState(i, j, this.mClock.uptimeMillis()));
                            this.mDirty |= 1;
                        }
                    }
                    this.mProfilePowerState.delete(i);
                }
                this.mDirty |= 32;
                updatePowerStateLocked();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean setDeviceIdleModeInternal(boolean z) {
        synchronized (this.mLock) {
            boolean z2 = false;
            if (this.mDeviceIdleMode == z) {
                return false;
            }
            this.mDeviceIdleMode = z;
            updateWakeLockDisabledStatesLocked();
            if (this.mDeviceIdleMode || this.mLightDeviceIdleMode) {
                z2 = true;
            }
            setPowerModeInternal(8, z2);
            if (z) {
                EventLogTags.writeDeviceIdleOnPhase("power");
            } else {
                EventLogTags.writeDeviceIdleOffPhase("power");
            }
            return true;
        }
    }

    public boolean setLightDeviceIdleModeInternal(boolean z) {
        synchronized (this.mLock) {
            boolean z2 = false;
            if (this.mLightDeviceIdleMode != z) {
                this.mLightDeviceIdleMode = z;
                if (this.mDeviceIdleMode || z) {
                    z2 = true;
                }
                setPowerModeInternal(8, z2);
                return true;
            }
            return false;
        }
    }

    public void setDeviceIdleWhitelistInternal(int[] iArr) {
        synchronized (this.mLock) {
            this.mDeviceIdleWhitelist = iArr;
            if (this.mDeviceIdleMode) {
                updateWakeLockDisabledStatesLocked();
            }
        }
    }

    public void setDeviceIdleTempWhitelistInternal(int[] iArr) {
        synchronized (this.mLock) {
            this.mDeviceIdleTempWhitelist = iArr;
            if (this.mDeviceIdleMode) {
                updateWakeLockDisabledStatesLocked();
            }
        }
    }

    public void setLowPowerStandbyAllowlistInternal(int[] iArr) {
        synchronized (this.mLock) {
            this.mLowPowerStandbyAllowlist = iArr;
            if (this.mLowPowerStandbyActive) {
                updateWakeLockDisabledStatesLocked();
            }
        }
    }

    public void setLowPowerStandbyActiveInternal(boolean z) {
        synchronized (this.mLock) {
            if (this.mLowPowerStandbyActive != z) {
                this.mLowPowerStandbyActive = z;
                updateWakeLockDisabledStatesLocked();
            }
        }
    }

    public void startUidChangesInternal() {
        synchronized (this.mLock) {
            this.mUidsChanging = true;
        }
    }

    public void finishUidChangesInternal() {
        synchronized (this.mLock) {
            this.mUidsChanging = false;
            if (this.mUidsChanged) {
                updateWakeLockDisabledStatesLocked();
                this.mUidsChanged = false;
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void handleUidStateChangeLocked() {
        if (this.mUidsChanging) {
            this.mUidsChanged = true;
        } else {
            updateWakeLockDisabledStatesLocked();
        }
    }

    public void updateUidProcStateInternal(int i, int i2) {
        synchronized (this.mLock) {
            UidState uidState = this.mUidState.get(i);
            if (uidState == null) {
                uidState = new UidState(i);
                this.mUidState.put(i, uidState);
            }
            boolean z = true;
            boolean z2 = uidState.mProcState <= 11;
            uidState.mProcState = i2;
            if (uidState.mNumWakeLocks > 0) {
                if (!this.mDeviceIdleMode && !this.mLowPowerStandbyActive) {
                    if (!uidState.mActive) {
                        if (i2 > 11) {
                            z = false;
                        }
                        if (z2 != z) {
                            handleUidStateChangeLocked();
                        }
                    }
                }
                handleUidStateChangeLocked();
            }
        }
    }

    public void uidGoneInternal(int i) {
        synchronized (this.mLock) {
            int indexOfKey = this.mUidState.indexOfKey(i);
            if (indexOfKey >= 0) {
                UidState valueAt = this.mUidState.valueAt(indexOfKey);
                valueAt.mProcState = 20;
                valueAt.mActive = false;
                this.mUidState.removeAt(indexOfKey);
                if ((this.mDeviceIdleMode || this.mLowPowerStandbyActive) && valueAt.mNumWakeLocks > 0) {
                    handleUidStateChangeLocked();
                }
            }
        }
    }

    public void uidActiveInternal(int i) {
        synchronized (this.mLock) {
            UidState uidState = this.mUidState.get(i);
            if (uidState == null) {
                uidState = new UidState(i);
                uidState.mProcState = 19;
                this.mUidState.put(i, uidState);
            }
            uidState.mActive = true;
            if (uidState.mNumWakeLocks > 0) {
                handleUidStateChangeLocked();
            }
        }
    }

    public void uidIdleInternal(int i) {
        synchronized (this.mLock) {
            UidState uidState = this.mUidState.get(i);
            if (uidState != null) {
                uidState.mActive = false;
                if (uidState.mNumWakeLocks > 0) {
                    handleUidStateChangeLocked();
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void updateWakeLockDisabledStatesLocked() {
        int size = this.mWakeLocks.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            WakeLock wakeLock = this.mWakeLocks.get(i);
            if ((wakeLock.mFlags & GnssNative.GNSS_AIDING_TYPE_ALL) == 1 && setWakeLockDisabledStateLocked(wakeLock)) {
                if (wakeLock.mDisabled) {
                    notifyWakeLockReleasedLocked(wakeLock);
                } else {
                    notifyWakeLockAcquiredLocked(wakeLock);
                }
                z = true;
            }
        }
        if (z) {
            this.mDirty |= 1;
            updatePowerStateLocked();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x002c, code lost:
        if (r3 > 11) goto L15;
     */
    @GuardedBy({"mLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean setWakeLockDisabledStateLocked(WakeLock wakeLock) {
        int i;
        int i2;
        boolean z = false;
        if ((wakeLock.mFlags & GnssNative.GNSS_AIDING_TYPE_ALL) == 1) {
            int appId = UserHandle.getAppId(wakeLock.mOwnerUid);
            if (appId >= 10000) {
                if (this.mConstants.NO_CACHED_WAKE_LOCKS) {
                    if (!this.mForceSuspendActive) {
                        UidState uidState = wakeLock.mUidState;
                        if (!uidState.mActive) {
                            int i3 = uidState.mProcState;
                            if (i3 != 20) {
                            }
                        }
                    }
                    z = true;
                }
                if (this.mDeviceIdleMode) {
                    UidState uidState2 = wakeLock.mUidState;
                    if (Arrays.binarySearch(this.mDeviceIdleWhitelist, appId) < 0 && Arrays.binarySearch(this.mDeviceIdleTempWhitelist, appId) < 0 && (i2 = uidState2.mProcState) != 20 && i2 > 5) {
                        z = true;
                    }
                }
                if (this.mLowPowerStandbyActive) {
                    UidState uidState3 = wakeLock.mUidState;
                    if (Arrays.binarySearch(this.mLowPowerStandbyAllowlist, wakeLock.mOwnerUid) < 0 && (i = uidState3.mProcState) != 20 && i > 3) {
                        z = true;
                    }
                }
            }
            return wakeLock.setDisabled(z);
        }
        return false;
    }

    @GuardedBy({"mLock"})
    public final boolean isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked() {
        long j = this.mMaximumScreenOffTimeoutFromDeviceAdmin;
        return j >= 0 && j < Long.MAX_VALUE;
    }

    public final void setAttentionLightInternal(boolean z, int i) {
        synchronized (this.mLock) {
            if (this.mSystemReady) {
                LogicalLight logicalLight = this.mAttentionLight;
                if (logicalLight != null) {
                    logicalLight.setFlashing(i, 2, z ? 3 : 0, 0);
                }
            }
        }
    }

    public final void setDozeAfterScreenOffInternal(boolean z) {
        synchronized (this.mLock) {
            this.mDozeAfterScreenOff = z;
        }
    }

    public final void boostScreenBrightnessInternal(long j, int i) {
        synchronized (this.mLock) {
            if (this.mSystemReady && getGlobalWakefulnessLocked() != 0 && j >= this.mLastScreenBrightnessBoostTime) {
                Slog.i("PowerManagerService", "Brightness boost activated (uid " + i + ")...");
                this.mLastScreenBrightnessBoostTime = j;
                this.mScreenBrightnessBoostInProgress = true;
                this.mDirty = this.mDirty | IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES;
                userActivityNoUpdateLocked(this.mPowerGroups.get(0), j, 0, 0, i);
                updatePowerStateLocked();
            }
        }
    }

    public final boolean isScreenBrightnessBoostedInternal() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mScreenBrightnessBoostInProgress;
        }
        return z;
    }

    public final void handleScreenBrightnessBoostTimeout() {
        synchronized (this.mLock) {
            this.mDirty |= IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES;
            updatePowerStateLocked();
        }
    }

    public final void setScreenBrightnessOverrideFromWindowManagerInternal(float f) {
        synchronized (this.mLock) {
            if (!BrightnessSynchronizer.floatEquals(this.mScreenBrightnessOverrideFromWindowManager, f)) {
                this.mScreenBrightnessOverrideFromWindowManager = f;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    public final void setUserInactiveOverrideFromWindowManagerInternal() {
        synchronized (this.mLock) {
            this.mUserInactiveOverrideFromWindowManager = true;
            this.mDirty |= 4;
            updatePowerStateLocked();
        }
    }

    public final void setUserActivityTimeoutOverrideFromWindowManagerInternal(long j) {
        synchronized (this.mLock) {
            if (this.mUserActivityTimeoutOverrideFromWindowManager != j) {
                this.mUserActivityTimeoutOverrideFromWindowManager = j;
                EventLogTags.writeUserActivityTimeoutOverride(j);
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    public final void setDozeOverrideFromDreamManagerInternal(int i, int i2) {
        synchronized (this.mLock) {
            if (this.mDozeScreenStateOverrideFromDreamManager != i || this.mDozeScreenBrightnessOverrideFromDreamManager != i2) {
                this.mDozeScreenStateOverrideFromDreamManager = i;
                this.mDozeScreenBrightnessOverrideFromDreamManager = i2;
                this.mDozeScreenBrightnessOverrideFromDreamManagerFloat = BrightnessSynchronizer.brightnessIntToFloat(i2);
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    public final void setDrawWakeLockOverrideFromSidekickInternal(boolean z) {
        synchronized (this.mLock) {
            if (this.mDrawWakeLockOverrideFromSidekick != z) {
                this.mDrawWakeLockOverrideFromSidekick = z;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    public final void setPowerBoostInternal(int i, int i2) {
        this.mNativeWrapper.nativeSetPowerBoost(i, i2);
    }

    public final boolean setPowerModeInternal(int i, boolean z) {
        if (i == 5 && z && this.mBatterySaverController.isLaunchBoostDisabled()) {
            return false;
        }
        return this.mNativeWrapper.nativeSetPowerMode(i, z);
    }

    @VisibleForTesting
    public boolean wasDeviceIdleForInternal(long j) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mPowerGroups.get(0).getLastUserActivityTimeLocked() + j < this.mClock.uptimeMillis();
        }
        return z;
    }

    @VisibleForTesting
    public void onUserActivity() {
        synchronized (this.mLock) {
            this.mPowerGroups.get(0).setLastUserActivityTimeLocked(this.mClock.uptimeMillis(), 0);
        }
    }

    public final boolean forceSuspendInternal(int i) {
        boolean nativeForceSuspend;
        synchronized (this.mLock) {
            this.mForceSuspendActive = true;
            for (int i2 = 0; i2 < this.mPowerGroups.size(); i2++) {
                sleepPowerGroupLocked(this.mPowerGroups.valueAt(i2), this.mClock.uptimeMillis(), 8, i);
            }
            updateWakeLockDisabledStatesLocked();
            Slog.i("PowerManagerService", "Force-Suspending (uid " + i + ")...");
            nativeForceSuspend = this.mNativeWrapper.nativeForceSuspend();
            if (!nativeForceSuspend) {
                Slog.i("PowerManagerService", "Force-Suspending failed in native.");
            }
            this.mForceSuspendActive = false;
            updateWakeLockDisabledStatesLocked();
        }
        return nativeForceSuspend;
    }

    @GuardedBy({"mLock"})
    public final void addPowerGroupsForNonDefaultDisplayGroupLocked() {
        IntArray displayGroupIds = this.mDisplayManagerInternal.getDisplayGroupIds();
        if (displayGroupIds == null) {
            return;
        }
        for (int i = 0; i < displayGroupIds.size(); i++) {
            int i2 = displayGroupIds.get(i);
            if (i2 != 0) {
                if (this.mPowerGroups.contains(i2)) {
                    Slog.e("PowerManagerService", "Tried to add already existing group:" + i2);
                } else {
                    this.mPowerGroups.append(i2, new PowerGroup(i2, this.mPowerGroupWakefulnessChangeListener, this.mNotifier, this.mDisplayManagerInternal, 1, false, false, this.mClock.uptimeMillis()));
                }
            }
        }
        this.mDirty |= 65536;
    }

    public static void lowLevelShutdown(String str) {
        if (str == null) {
            str = "";
        }
        SystemProperties.set("sys.powerctl", "shutdown," + str);
    }

    public static void lowLevelReboot(String str) {
        if (str == null) {
            str = "";
        }
        if (str.equals("quiescent")) {
            sQuiescent = true;
            str = "";
        } else if (str.endsWith(",quiescent")) {
            sQuiescent = true;
            str = str.substring(0, (str.length() - 9) - 1);
        }
        str = (str.equals("recovery") || str.equals("recovery-update")) ? "recovery" : "recovery";
        if (sQuiescent) {
            if (!"".equals(str)) {
                str = str + ",";
            }
            str = str + "quiescent";
        }
        SystemProperties.set("sys.powerctl", "reboot," + str);
        try {
            Thread.sleep(20000L);
        } catch (InterruptedException unused) {
            Thread.currentThread().interrupt();
        }
        Slog.wtf("PowerManagerService", "Unexpected return from lowLevelReboot!");
    }

    @Override // com.android.server.Watchdog.Monitor
    public void monitor() {
        synchronized (this.mLock) {
        }
    }

    @NeverCompile
    public final void dumpInternal(PrintWriter printWriter) {
        WirelessChargerDetector wirelessChargerDetector;
        printWriter.println("POWER MANAGER (dumpsys power)\n");
        synchronized (this.mLock) {
            printWriter.println("Power Manager State:");
            this.mConstants.dump(printWriter);
            printWriter.println("  mDirty=0x" + Integer.toHexString(this.mDirty));
            printWriter.println("  mWakefulness=" + PowerManagerInternal.wakefulnessToString(getGlobalWakefulnessLocked()));
            printWriter.println("  mWakefulnessChanging=" + this.mWakefulnessChanging);
            printWriter.println("  mIsPowered=" + this.mIsPowered);
            printWriter.println("  mPlugType=" + this.mPlugType);
            printWriter.println("  mBatteryLevel=" + this.mBatteryLevel);
            printWriter.println("  mDreamsBatteryLevelDrain=" + this.mDreamsBatteryLevelDrain);
            printWriter.println("  mDockState=" + this.mDockState);
            printWriter.println("  mStayOn=" + this.mStayOn);
            printWriter.println("  mProximityPositive=" + this.mProximityPositive);
            printWriter.println("  mBootCompleted=" + this.mBootCompleted);
            printWriter.println("  mSystemReady=" + this.mSystemReady);
            synchronized (this.mEnhancedDischargeTimeLock) {
                printWriter.println("  mEnhancedDischargeTimeElapsed=" + this.mEnhancedDischargeTimeElapsed);
                printWriter.println("  mLastEnhancedDischargeTimeUpdatedElapsed=" + this.mLastEnhancedDischargeTimeUpdatedElapsed);
                printWriter.println("  mEnhancedDischargePredictionIsPersonalized=" + this.mEnhancedDischargePredictionIsPersonalized);
            }
            printWriter.println("  mHalAutoSuspendModeEnabled=" + this.mHalAutoSuspendModeEnabled);
            printWriter.println("  mHalInteractiveModeEnabled=" + this.mHalInteractiveModeEnabled);
            printWriter.println("  mWakeLockSummary=0x" + Integer.toHexString(this.mWakeLockSummary));
            printWriter.print("  mNotifyLongScheduled=");
            long j = this.mNotifyLongScheduled;
            if (j == 0) {
                printWriter.print("(none)");
            } else {
                TimeUtils.formatDuration(j, this.mClock.uptimeMillis(), printWriter);
            }
            printWriter.println();
            printWriter.print("  mNotifyLongDispatched=");
            long j2 = this.mNotifyLongDispatched;
            if (j2 == 0) {
                printWriter.print("(none)");
            } else {
                TimeUtils.formatDuration(j2, this.mClock.uptimeMillis(), printWriter);
            }
            printWriter.println();
            printWriter.print("  mNotifyLongNextCheck=");
            long j3 = this.mNotifyLongNextCheck;
            if (j3 == 0) {
                printWriter.print("(none)");
            } else {
                TimeUtils.formatDuration(j3, this.mClock.uptimeMillis(), printWriter);
            }
            printWriter.println();
            printWriter.println("  mRequestWaitForNegativeProximity=" + this.mRequestWaitForNegativeProximity);
            printWriter.println("  mInterceptedPowerKeyForProximity=" + this.mInterceptedPowerKeyForProximity);
            printWriter.println("  mSandmanScheduled=" + this.mSandmanScheduled);
            printWriter.println("  mBatteryLevelLow=" + this.mBatteryLevelLow);
            printWriter.println("  mLightDeviceIdleMode=" + this.mLightDeviceIdleMode);
            printWriter.println("  mDeviceIdleMode=" + this.mDeviceIdleMode);
            printWriter.println("  mDeviceIdleWhitelist=" + Arrays.toString(this.mDeviceIdleWhitelist));
            printWriter.println("  mDeviceIdleTempWhitelist=" + Arrays.toString(this.mDeviceIdleTempWhitelist));
            printWriter.println("  mLowPowerStandbyActive=" + this.mLowPowerStandbyActive);
            printWriter.println("  mLastWakeTime=" + TimeUtils.formatUptime(this.mLastGlobalWakeTime));
            printWriter.println("  mLastSleepTime=" + TimeUtils.formatUptime(this.mLastGlobalSleepTime));
            printWriter.println("  mLastSleepReason=" + PowerManager.sleepReasonToString(this.mLastGlobalSleepReason));
            printWriter.println("  mLastGlobalWakeTimeRealtime=" + TimeUtils.formatUptime(this.mLastGlobalWakeTimeRealtime));
            printWriter.println("  mLastGlobalSleepTimeRealtime=" + TimeUtils.formatUptime(this.mLastGlobalSleepTimeRealtime));
            printWriter.println("  mLastInteractivePowerHintTime=" + TimeUtils.formatUptime(this.mLastInteractivePowerHintTime));
            printWriter.println("  mLastScreenBrightnessBoostTime=" + TimeUtils.formatUptime(this.mLastScreenBrightnessBoostTime));
            printWriter.println("  mScreenBrightnessBoostInProgress=" + this.mScreenBrightnessBoostInProgress);
            printWriter.println("  mHoldingWakeLockSuspendBlocker=" + this.mHoldingWakeLockSuspendBlocker);
            printWriter.println("  mHoldingDisplaySuspendBlocker=" + this.mHoldingDisplaySuspendBlocker);
            printWriter.println("  mLastFlipTime=" + this.mLastFlipTime);
            printWriter.println("  mIsFaceDown=" + this.mIsFaceDown);
            printWriter.println();
            printWriter.println("Settings and Configuration:");
            printWriter.println("  mDecoupleHalAutoSuspendModeFromDisplayConfig=" + this.mDecoupleHalAutoSuspendModeFromDisplayConfig);
            printWriter.println("  mDecoupleHalInteractiveModeFromDisplayConfig=" + this.mDecoupleHalInteractiveModeFromDisplayConfig);
            printWriter.println("  mWakeUpWhenPluggedOrUnpluggedConfig=" + this.mWakeUpWhenPluggedOrUnpluggedConfig);
            printWriter.println("  mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig=" + this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig);
            printWriter.println("  mTheaterModeEnabled=" + this.mTheaterModeEnabled);
            printWriter.println("  mKeepDreamingWhenUndocked=" + this.mKeepDreamingWhenUndocked);
            printWriter.println("  mSuspendWhenScreenOffDueToProximityConfig=" + this.mSuspendWhenScreenOffDueToProximityConfig);
            printWriter.println("  mDreamsSupportedConfig=" + this.mDreamsSupportedConfig);
            printWriter.println("  mDreamsEnabledByDefaultConfig=" + this.mDreamsEnabledByDefaultConfig);
            printWriter.println("  mDreamsActivatedOnSleepByDefaultConfig=" + this.mDreamsActivatedOnSleepByDefaultConfig);
            printWriter.println("  mDreamsActivatedOnDockByDefaultConfig=" + this.mDreamsActivatedOnDockByDefaultConfig);
            printWriter.println("  mDreamsEnabledOnBatteryConfig=" + this.mDreamsEnabledOnBatteryConfig);
            printWriter.println("  mDreamsBatteryLevelMinimumWhenPoweredConfig=" + this.mDreamsBatteryLevelMinimumWhenPoweredConfig);
            printWriter.println("  mDreamsBatteryLevelMinimumWhenNotPoweredConfig=" + this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig);
            printWriter.println("  mDreamsBatteryLevelDrainCutoffConfig=" + this.mDreamsBatteryLevelDrainCutoffConfig);
            printWriter.println("  mDreamsEnabledSetting=" + this.mDreamsEnabledSetting);
            printWriter.println("  mDreamsActivateOnSleepSetting=" + this.mDreamsActivateOnSleepSetting);
            printWriter.println("  mDreamsActivateOnDockSetting=" + this.mDreamsActivateOnDockSetting);
            printWriter.println("  mDozeAfterScreenOff=" + this.mDozeAfterScreenOff);
            printWriter.println("  mMinimumScreenOffTimeoutConfig=" + this.mMinimumScreenOffTimeoutConfig);
            printWriter.println("  mMaximumScreenDimDurationConfig=" + this.mMaximumScreenDimDurationConfig);
            printWriter.println("  mMaximumScreenDimRatioConfig=" + this.mMaximumScreenDimRatioConfig);
            printWriter.println("  mAttentiveTimeoutConfig=" + this.mAttentiveTimeoutConfig);
            printWriter.println("  mAttentiveTimeoutSetting=" + this.mAttentiveTimeoutSetting);
            printWriter.println("  mAttentiveWarningDurationConfig=" + this.mAttentiveWarningDurationConfig);
            printWriter.println("  mScreenOffTimeoutSetting=" + this.mScreenOffTimeoutSetting);
            printWriter.println("  mSleepTimeoutSetting=" + this.mSleepTimeoutSetting);
            printWriter.println("  mMaximumScreenOffTimeoutFromDeviceAdmin=" + this.mMaximumScreenOffTimeoutFromDeviceAdmin + " (enforced=" + isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked() + ")");
            StringBuilder sb = new StringBuilder();
            sb.append("  mStayOnWhilePluggedInSetting=");
            sb.append(this.mStayOnWhilePluggedInSetting);
            printWriter.println(sb.toString());
            printWriter.println("  mScreenBrightnessOverrideFromWindowManager=" + this.mScreenBrightnessOverrideFromWindowManager);
            printWriter.println("  mUserActivityTimeoutOverrideFromWindowManager=" + this.mUserActivityTimeoutOverrideFromWindowManager);
            printWriter.println("  mUserInactiveOverrideFromWindowManager=" + this.mUserInactiveOverrideFromWindowManager);
            printWriter.println("  mDozeScreenStateOverrideFromDreamManager=" + this.mDozeScreenStateOverrideFromDreamManager);
            printWriter.println("  mDrawWakeLockOverrideFromSidekick=" + this.mDrawWakeLockOverrideFromSidekick);
            printWriter.println("  mDozeScreenBrightnessOverrideFromDreamManager=" + this.mDozeScreenBrightnessOverrideFromDreamManager);
            printWriter.println("  mScreenBrightnessMinimum=" + this.mScreenBrightnessMinimum);
            printWriter.println("  mScreenBrightnessMaximum=" + this.mScreenBrightnessMaximum);
            printWriter.println("  mScreenBrightnessDefault=" + this.mScreenBrightnessDefault);
            printWriter.println("  mDoubleTapWakeEnabled=" + this.mDoubleTapWakeEnabled);
            printWriter.println("  mForegroundProfile=" + this.mForegroundProfile);
            printWriter.println("  mUserId=" + this.mUserId);
            long attentiveTimeoutLocked = getAttentiveTimeoutLocked();
            long sleepTimeoutLocked = getSleepTimeoutLocked(attentiveTimeoutLocked);
            long screenOffTimeoutLocked = getScreenOffTimeoutLocked(sleepTimeoutLocked, attentiveTimeoutLocked);
            long screenDimDurationLocked = getScreenDimDurationLocked(screenOffTimeoutLocked);
            printWriter.println();
            printWriter.println("Attentive timeout: " + attentiveTimeoutLocked + " ms");
            printWriter.println("Sleep timeout: " + sleepTimeoutLocked + " ms");
            printWriter.println("Screen off timeout: " + screenOffTimeoutLocked + " ms");
            printWriter.println("Screen dim duration: " + screenDimDurationLocked + " ms");
            printWriter.println();
            printWriter.print("UID states (changing=");
            printWriter.print(this.mUidsChanging);
            printWriter.print(" changed=");
            printWriter.print(this.mUidsChanged);
            printWriter.println("):");
            for (int i = 0; i < this.mUidState.size(); i++) {
                UidState valueAt = this.mUidState.valueAt(i);
                printWriter.print("  UID ");
                UserHandle.formatUid(printWriter, this.mUidState.keyAt(i));
                printWriter.print(": ");
                if (valueAt.mActive) {
                    printWriter.print("  ACTIVE ");
                } else {
                    printWriter.print("INACTIVE ");
                }
                printWriter.print(" count=");
                printWriter.print(valueAt.mNumWakeLocks);
                printWriter.print(" state=");
                printWriter.println(valueAt.mProcState);
            }
            printWriter.println();
            printWriter.println("Looper state:");
            this.mHandler.getLooper().dump(new PrintWriterPrinter(printWriter), "  ");
            printWriter.println();
            printWriter.println("Wake Locks: size=" + this.mWakeLocks.size());
            Iterator<WakeLock> it = this.mWakeLocks.iterator();
            while (it.hasNext()) {
                printWriter.println("  " + it.next());
            }
            printWriter.println();
            printWriter.println("Suspend Blockers: size=" + this.mSuspendBlockers.size());
            Iterator<SuspendBlocker> it2 = this.mSuspendBlockers.iterator();
            while (it2.hasNext()) {
                printWriter.println("  " + it2.next());
            }
            printWriter.println();
            printWriter.println("Display Power: " + this.mDisplayPowerCallbacks);
            this.mBatterySaverPolicy.dump(printWriter);
            this.mBatterySaverStateMachine.dump(printWriter);
            this.mAttentionDetector.dump(printWriter);
            printWriter.println();
            int size = this.mProfilePowerState.size();
            printWriter.println("Profile power states: size=" + size);
            for (int i2 = 0; i2 < size; i2++) {
                ProfilePowerState valueAt2 = this.mProfilePowerState.valueAt(i2);
                printWriter.print("  mUserId=");
                printWriter.print(valueAt2.mUserId);
                printWriter.print(" mScreenOffTimeout=");
                printWriter.print(valueAt2.mScreenOffTimeout);
                printWriter.print(" mWakeLockSummary=");
                printWriter.print(valueAt2.mWakeLockSummary);
                printWriter.print(" mLastUserActivityTime=");
                printWriter.print(valueAt2.mLastUserActivityTime);
                printWriter.print(" mLockingNotified=");
                printWriter.println(valueAt2.mLockingNotified);
            }
            printWriter.println("Display Group User Activity:");
            for (int i3 = 0; i3 < this.mPowerGroups.size(); i3++) {
                PowerGroup valueAt3 = this.mPowerGroups.valueAt(i3);
                printWriter.println("  displayGroupId=" + valueAt3.getGroupId());
                printWriter.println("  userActivitySummary=0x" + Integer.toHexString(valueAt3.getUserActivitySummaryLocked()));
                printWriter.println("  lastUserActivityTime=" + TimeUtils.formatUptime(valueAt3.getLastUserActivityTimeLocked()));
                printWriter.println("  lastUserActivityTimeNoChangeLights=" + TimeUtils.formatUptime(valueAt3.getLastUserActivityTimeNoChangeLightsLocked()));
                printWriter.println("  mWakeLockSummary=0x" + Integer.toHexString(valueAt3.getWakeLockSummaryLocked()));
            }
            wirelessChargerDetector = this.mWirelessChargerDetector;
        }
        if (wirelessChargerDetector != null) {
            wirelessChargerDetector.dump(printWriter);
        }
        Notifier notifier = this.mNotifier;
        if (notifier != null) {
            notifier.dump(printWriter);
        }
        this.mFaceDownDetector.dump(printWriter);
        this.mAmbientDisplaySuppressionController.dump(printWriter);
        this.mLowPowerStandbyController.dump(printWriter);
    }

    public final void dumpProto(FileDescriptor fileDescriptor) {
        WirelessChargerDetector wirelessChargerDetector;
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        synchronized (this.mLock) {
            this.mConstants.dumpProto(protoOutputStream);
            protoOutputStream.write(1120986464258L, this.mDirty);
            protoOutputStream.write(1159641169923L, getGlobalWakefulnessLocked());
            protoOutputStream.write(1133871366148L, this.mWakefulnessChanging);
            protoOutputStream.write(1133871366149L, this.mIsPowered);
            protoOutputStream.write(1159641169926L, this.mPlugType);
            protoOutputStream.write(1120986464263L, this.mBatteryLevel);
            protoOutputStream.write(1120986464313L, this.mDreamsBatteryLevelDrain);
            protoOutputStream.write(1159641169929L, this.mDockState);
            protoOutputStream.write(1133871366154L, this.mStayOn);
            protoOutputStream.write(1133871366155L, this.mProximityPositive);
            protoOutputStream.write(1133871366156L, this.mBootCompleted);
            protoOutputStream.write(1133871366157L, this.mSystemReady);
            synchronized (this.mEnhancedDischargeTimeLock) {
                protoOutputStream.write(1112396529716L, this.mEnhancedDischargeTimeElapsed);
                protoOutputStream.write(1112396529717L, this.mLastEnhancedDischargeTimeUpdatedElapsed);
                protoOutputStream.write(1133871366198L, this.mEnhancedDischargePredictionIsPersonalized);
            }
            protoOutputStream.write(1133871366158L, this.mHalAutoSuspendModeEnabled);
            protoOutputStream.write(1133871366159L, this.mHalInteractiveModeEnabled);
            long start = protoOutputStream.start(1146756268048L);
            protoOutputStream.write(1133871366145L, (this.mWakeLockSummary & 1) != 0);
            protoOutputStream.write(1133871366146L, (this.mWakeLockSummary & 2) != 0);
            long j = 1133871366147L;
            protoOutputStream.write(1133871366147L, (this.mWakeLockSummary & 4) != 0);
            protoOutputStream.write(1133871366148L, (this.mWakeLockSummary & 8) != 0);
            protoOutputStream.write(1133871366149L, (this.mWakeLockSummary & 16) != 0);
            protoOutputStream.write(1133871366150L, (this.mWakeLockSummary & 32) != 0);
            protoOutputStream.write(1133871366151L, (this.mWakeLockSummary & 64) != 0);
            protoOutputStream.write(1133871366152L, (this.mWakeLockSummary & 128) != 0);
            protoOutputStream.end(start);
            protoOutputStream.write(1112396529681L, this.mNotifyLongScheduled);
            protoOutputStream.write(1112396529682L, this.mNotifyLongDispatched);
            protoOutputStream.write(1112396529683L, this.mNotifyLongNextCheck);
            int i = 0;
            while (i < this.mPowerGroups.size()) {
                PowerGroup valueAt = this.mPowerGroups.valueAt(i);
                long start2 = protoOutputStream.start(2246267895828L);
                protoOutputStream.write(1120986464262L, valueAt.getGroupId());
                long userActivitySummaryLocked = valueAt.getUserActivitySummaryLocked();
                protoOutputStream.write(1133871366145L, (userActivitySummaryLocked & 1) != 0);
                protoOutputStream.write(1133871366146L, (userActivitySummaryLocked & 2) != 0);
                protoOutputStream.write(j, (userActivitySummaryLocked & 4) != 0);
                protoOutputStream.write(1112396529668L, valueAt.getLastUserActivityTimeLocked());
                protoOutputStream.write(1112396529669L, valueAt.getLastUserActivityTimeNoChangeLightsLocked());
                protoOutputStream.end(start2);
                i++;
                j = 1133871366147L;
            }
            protoOutputStream.write(1133871366165L, this.mRequestWaitForNegativeProximity);
            protoOutputStream.write(1133871366166L, this.mSandmanScheduled);
            protoOutputStream.write(1133871366168L, this.mBatteryLevelLow);
            protoOutputStream.write(1133871366169L, this.mLightDeviceIdleMode);
            protoOutputStream.write(1133871366170L, this.mDeviceIdleMode);
            for (int i2 : this.mDeviceIdleWhitelist) {
                protoOutputStream.write(2220498092059L, i2);
            }
            for (int i3 : this.mDeviceIdleTempWhitelist) {
                protoOutputStream.write(2220498092060L, i3);
            }
            protoOutputStream.write(1133871366199L, this.mLowPowerStandbyActive);
            protoOutputStream.write(1112396529693L, this.mLastGlobalWakeTime);
            protoOutputStream.write(1112396529694L, this.mLastGlobalSleepTime);
            protoOutputStream.write(1112396529697L, this.mLastInteractivePowerHintTime);
            protoOutputStream.write(1112396529698L, this.mLastScreenBrightnessBoostTime);
            protoOutputStream.write(1133871366179L, this.mScreenBrightnessBoostInProgress);
            protoOutputStream.write(1133871366181L, this.mHoldingWakeLockSuspendBlocker);
            protoOutputStream.write(1133871366182L, this.mHoldingDisplaySuspendBlocker);
            long start3 = protoOutputStream.start(1146756268071L);
            protoOutputStream.write(1133871366145L, this.mDecoupleHalAutoSuspendModeFromDisplayConfig);
            protoOutputStream.write(1133871366146L, this.mDecoupleHalInteractiveModeFromDisplayConfig);
            protoOutputStream.write(1133871366147L, this.mWakeUpWhenPluggedOrUnpluggedConfig);
            protoOutputStream.write(1133871366148L, this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig);
            protoOutputStream.write(1133871366149L, this.mTheaterModeEnabled);
            protoOutputStream.write(1133871366150L, this.mSuspendWhenScreenOffDueToProximityConfig);
            protoOutputStream.write(1133871366151L, this.mDreamsSupportedConfig);
            protoOutputStream.write(1133871366152L, this.mDreamsEnabledByDefaultConfig);
            protoOutputStream.write(1133871366153L, this.mDreamsActivatedOnSleepByDefaultConfig);
            protoOutputStream.write(1133871366154L, this.mDreamsActivatedOnDockByDefaultConfig);
            protoOutputStream.write(1133871366155L, this.mDreamsEnabledOnBatteryConfig);
            protoOutputStream.write(1172526071820L, this.mDreamsBatteryLevelMinimumWhenPoweredConfig);
            protoOutputStream.write(1172526071821L, this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig);
            protoOutputStream.write(1172526071822L, this.mDreamsBatteryLevelDrainCutoffConfig);
            protoOutputStream.write(1133871366159L, this.mDreamsEnabledSetting);
            protoOutputStream.write(1133871366160L, this.mDreamsActivateOnSleepSetting);
            protoOutputStream.write(1133871366161L, this.mDreamsActivateOnDockSetting);
            protoOutputStream.write(1133871366162L, this.mDozeAfterScreenOff);
            protoOutputStream.write(1120986464275L, this.mMinimumScreenOffTimeoutConfig);
            protoOutputStream.write(1120986464276L, this.mMaximumScreenDimDurationConfig);
            protoOutputStream.write(1108101562389L, this.mMaximumScreenDimRatioConfig);
            protoOutputStream.write(1120986464278L, this.mScreenOffTimeoutSetting);
            protoOutputStream.write(1172526071831L, this.mSleepTimeoutSetting);
            protoOutputStream.write(1172526071845L, this.mAttentiveTimeoutSetting);
            protoOutputStream.write(1172526071846L, this.mAttentiveTimeoutConfig);
            protoOutputStream.write(1172526071847L, this.mAttentiveWarningDurationConfig);
            protoOutputStream.write(1120986464280L, Math.min(this.mMaximumScreenOffTimeoutFromDeviceAdmin, 2147483647L));
            protoOutputStream.write(1133871366169L, isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked());
            long start4 = protoOutputStream.start(1146756268058L);
            protoOutputStream.write(1133871366145L, (this.mStayOnWhilePluggedInSetting & 1) != 0);
            protoOutputStream.write(1133871366146L, (this.mStayOnWhilePluggedInSetting & 2) != 0);
            protoOutputStream.write(1133871366147L, (this.mStayOnWhilePluggedInSetting & 4) != 0);
            protoOutputStream.write(1133871366148L, (this.mStayOnWhilePluggedInSetting & 8) != 0);
            protoOutputStream.end(start4);
            protoOutputStream.write(1172526071836L, this.mScreenBrightnessOverrideFromWindowManager);
            protoOutputStream.write(1176821039133L, this.mUserActivityTimeoutOverrideFromWindowManager);
            protoOutputStream.write(1133871366174L, this.mUserInactiveOverrideFromWindowManager);
            protoOutputStream.write(1159641169951L, this.mDozeScreenStateOverrideFromDreamManager);
            protoOutputStream.write(1133871366180L, this.mDrawWakeLockOverrideFromSidekick);
            protoOutputStream.write(1108101562400L, this.mDozeScreenBrightnessOverrideFromDreamManager);
            long start5 = protoOutputStream.start(1146756268065L);
            protoOutputStream.write(1108101562372L, this.mScreenBrightnessMinimum);
            protoOutputStream.write(1108101562373L, this.mScreenBrightnessMaximum);
            protoOutputStream.write(1108101562374L, this.mScreenBrightnessDefault);
            protoOutputStream.end(start5);
            protoOutputStream.write(1133871366178L, this.mDoubleTapWakeEnabled);
            protoOutputStream.end(start3);
            long attentiveTimeoutLocked = getAttentiveTimeoutLocked();
            long sleepTimeoutLocked = getSleepTimeoutLocked(attentiveTimeoutLocked);
            long screenOffTimeoutLocked = getScreenOffTimeoutLocked(sleepTimeoutLocked, attentiveTimeoutLocked);
            long screenDimDurationLocked = getScreenDimDurationLocked(screenOffTimeoutLocked);
            protoOutputStream.write(1172526071859L, attentiveTimeoutLocked);
            protoOutputStream.write(1172526071848L, sleepTimeoutLocked);
            protoOutputStream.write(1120986464297L, screenOffTimeoutLocked);
            protoOutputStream.write(1120986464298L, screenDimDurationLocked);
            protoOutputStream.write(1133871366187L, this.mUidsChanging);
            protoOutputStream.write(1133871366188L, this.mUidsChanged);
            for (int i4 = 0; i4 < this.mUidState.size(); i4++) {
                UidState valueAt2 = this.mUidState.valueAt(i4);
                long start6 = protoOutputStream.start(2246267895853L);
                int keyAt = this.mUidState.keyAt(i4);
                protoOutputStream.write(1120986464257L, keyAt);
                protoOutputStream.write(1138166333442L, UserHandle.formatUid(keyAt));
                protoOutputStream.write(1133871366147L, valueAt2.mActive);
                protoOutputStream.write(1120986464260L, valueAt2.mNumWakeLocks);
                protoOutputStream.write(1159641169925L, ActivityManager.processStateAmToProto(valueAt2.mProcState));
                protoOutputStream.end(start6);
            }
            this.mBatterySaverStateMachine.dumpProto(protoOutputStream, 1146756268082L);
            this.mHandler.getLooper().dumpDebug(protoOutputStream, 1146756268078L);
            Iterator<WakeLock> it = this.mWakeLocks.iterator();
            while (it.hasNext()) {
                it.next().dumpDebug(protoOutputStream, 2246267895855L);
            }
            Iterator<SuspendBlocker> it2 = this.mSuspendBlockers.iterator();
            while (it2.hasNext()) {
                it2.next().dumpDebug(protoOutputStream, 2246267895856L);
            }
            wirelessChargerDetector = this.mWirelessChargerDetector;
        }
        if (wirelessChargerDetector != null) {
            wirelessChargerDetector.dumpDebug(protoOutputStream, 1146756268081L);
        }
        this.mLowPowerStandbyController.dumpProto(protoOutputStream, 1146756268088L);
        protoOutputStream.flush();
    }

    public final void incrementBootCount() {
        int i;
        synchronized (this.mLock) {
            try {
                i = Settings.Global.getInt(getContext().getContentResolver(), "boot_count");
            } catch (Settings.SettingNotFoundException unused) {
                i = 0;
            }
            Settings.Global.putInt(getContext().getContentResolver(), "boot_count", i + 1);
        }
    }

    public static WorkSource copyWorkSource(WorkSource workSource) {
        if (workSource != null) {
            return new WorkSource(workSource);
        }
        return null;
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class BatteryReceiver extends BroadcastReceiver {
        public BatteryReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.handleBatteryStateChangedLocked();
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class DreamReceiver extends BroadcastReceiver {
        public DreamReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.scheduleSandmanLocked();
            }
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class UserSwitchedReceiver extends BroadcastReceiver {
        public UserSwitchedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.handleSettingsChangedLocked();
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class DockReceiver extends BroadcastReceiver {
        public DockReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                int intExtra = intent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
                if (PowerManagerService.this.mDockState != intExtra) {
                    FrameworkStatsLog.write((int) FrameworkStatsLog.DOCK_STATE_CHANGED, intExtra);
                    PowerManagerService.this.mDockState = intExtra;
                    PowerManagerService.this.mDirty |= 1024;
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.handleSettingsChangedLocked();
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class PowerManagerHandlerCallback implements Handler.Callback {
        public PowerManagerHandlerCallback() {
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                PowerManagerService.this.handleUserActivityTimeout();
            } else if (i == 2) {
                PowerManagerService.this.handleSandman(message.arg1);
            } else if (i == 3) {
                PowerManagerService.this.handleScreenBrightnessBoostTimeout();
            } else if (i == 4) {
                PowerManagerService.this.checkForLongWakeLocks();
            } else if (i == 5) {
                PowerManagerService.this.handleAttentiveTimeout();
            }
            return true;
        }
    }

    /* loaded from: classes2.dex */
    public final class WakeLock implements IBinder.DeathRecipient {
        public long mAcquireTime;
        public IWakeLockCallback mCallback;
        public boolean mDisabled;
        public final int mDisplayId;
        public int mFlags;
        public String mHistoryTag;
        public final IBinder mLock;
        public boolean mNotifiedAcquired;
        public boolean mNotifiedLong;
        public final int mOwnerPid;
        public final int mOwnerUid;
        public final String mPackageName;
        public String mTag;
        public final UidState mUidState;
        public WorkSource mWorkSource;

        public WakeLock(IBinder iBinder, int i, int i2, String str, String str2, WorkSource workSource, String str3, int i3, int i4, UidState uidState, IWakeLockCallback iWakeLockCallback) {
            this.mLock = iBinder;
            this.mDisplayId = i;
            this.mFlags = i2;
            this.mTag = str;
            this.mPackageName = str2;
            this.mWorkSource = PowerManagerService.copyWorkSource(workSource);
            this.mHistoryTag = str3;
            this.mOwnerUid = i3;
            this.mOwnerPid = i4;
            this.mUidState = uidState;
            this.mCallback = iWakeLockCallback;
            linkToDeath();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            unlinkToDeath();
            PowerManagerService.this.handleWakeLockDeath(this);
        }

        public final void linkToDeath() {
            try {
                this.mLock.linkToDeath(this, 0);
            } catch (RemoteException unused) {
                throw new IllegalArgumentException("Wakelock.mLock is already dead.");
            }
        }

        @GuardedBy({"mLock"})
        public void unlinkToDeath() {
            try {
                this.mLock.unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
                Slog.wtf("PowerManagerService", "Failed to unlink Wakelock.mLock", e);
            }
        }

        public boolean setDisabled(boolean z) {
            if (this.mDisabled != z) {
                this.mDisabled = z;
                return true;
            }
            return false;
        }

        public boolean hasSameProperties(int i, String str, WorkSource workSource, int i2, int i3, IWakeLockCallback iWakeLockCallback) {
            return this.mFlags == i && this.mTag.equals(str) && hasSameWorkSource(workSource) && this.mOwnerUid == i2 && this.mOwnerPid == i3;
        }

        public void updateProperties(int i, String str, String str2, WorkSource workSource, String str3, int i2, int i3, IWakeLockCallback iWakeLockCallback) {
            if (!this.mPackageName.equals(str2)) {
                throw new IllegalStateException("Existing wake lock package name changed: " + this.mPackageName + " to " + str2);
            } else if (this.mOwnerUid != i2) {
                throw new IllegalStateException("Existing wake lock uid changed: " + this.mOwnerUid + " to " + i2);
            } else if (this.mOwnerPid != i3) {
                throw new IllegalStateException("Existing wake lock pid changed: " + this.mOwnerPid + " to " + i3);
            } else {
                this.mFlags = i;
                this.mTag = str;
                updateWorkSource(workSource);
                this.mHistoryTag = str3;
                this.mCallback = iWakeLockCallback;
            }
        }

        public boolean hasSameWorkSource(WorkSource workSource) {
            return Objects.equals(this.mWorkSource, workSource);
        }

        public void updateWorkSource(WorkSource workSource) {
            this.mWorkSource = PowerManagerService.copyWorkSource(workSource);
        }

        public Integer getPowerGroupId() {
            if (!PowerManagerService.this.mSystemReady || this.mDisplayId == -1) {
                return -1;
            }
            DisplayInfo displayInfo = PowerManagerService.this.mDisplayManagerInternal.getDisplayInfo(this.mDisplayId);
            if (displayInfo != null) {
                return Integer.valueOf(displayInfo.displayGroupId);
            }
            return null;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getLockLevelString());
            sb.append(" '");
            sb.append(this.mTag);
            sb.append("'");
            sb.append(getLockFlagsString());
            if (this.mDisabled) {
                sb.append(" DISABLED");
            }
            if (this.mNotifiedAcquired) {
                sb.append(" ACQ=");
                TimeUtils.formatDuration(this.mAcquireTime - PowerManagerService.this.mClock.uptimeMillis(), sb);
            }
            if (this.mNotifiedLong) {
                sb.append(" LONG");
            }
            sb.append(" (uid=");
            sb.append(this.mOwnerUid);
            if (this.mOwnerPid != 0) {
                sb.append(" pid=");
                sb.append(this.mOwnerPid);
            }
            if (this.mWorkSource != null) {
                sb.append(" ws=");
                sb.append(this.mWorkSource);
            }
            sb.append(")");
            return sb.toString();
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1159641169921L, this.mFlags & GnssNative.GNSS_AIDING_TYPE_ALL);
            protoOutputStream.write(1138166333442L, this.mTag);
            long start2 = protoOutputStream.start(1146756268035L);
            protoOutputStream.write(1133871366145L, (this.mFlags & 268435456) != 0);
            protoOutputStream.write(1133871366146L, (this.mFlags & 536870912) != 0);
            protoOutputStream.write(1133871366147L, (this.mFlags & Integer.MIN_VALUE) != 0);
            protoOutputStream.end(start2);
            protoOutputStream.write(1133871366148L, this.mDisabled);
            if (this.mNotifiedAcquired) {
                protoOutputStream.write(1112396529669L, this.mAcquireTime);
            }
            protoOutputStream.write(1133871366150L, this.mNotifiedLong);
            protoOutputStream.write(1120986464263L, this.mOwnerUid);
            protoOutputStream.write(1120986464264L, this.mOwnerPid);
            WorkSource workSource = this.mWorkSource;
            if (workSource != null) {
                workSource.dumpDebug(protoOutputStream, 1146756268041L);
            }
            protoOutputStream.end(start);
        }

        public final String getLockLevelString() {
            int i = this.mFlags & GnssNative.GNSS_AIDING_TYPE_ALL;
            return i != 1 ? i != 6 ? i != 10 ? i != 26 ? i != 32 ? i != 64 ? i != 128 ? "???                           " : "DRAW_WAKE_LOCK                " : "DOZE_WAKE_LOCK                " : "PROXIMITY_SCREEN_OFF_WAKE_LOCK" : "FULL_WAKE_LOCK                " : "SCREEN_BRIGHT_WAKE_LOCK       " : "SCREEN_DIM_WAKE_LOCK          " : "PARTIAL_WAKE_LOCK             ";
        }

        public final String getLockFlagsString() {
            String str = "";
            if ((this.mFlags & 268435456) != 0) {
                str = " ACQUIRE_CAUSES_WAKEUP";
            }
            if ((this.mFlags & 536870912) != 0) {
                str = str + " ON_AFTER_RELEASE";
            }
            if ((this.mFlags & Integer.MIN_VALUE) != 0) {
                return str + " SYSTEM_WAKELOCK";
            }
            return str;
        }
    }

    /* loaded from: classes2.dex */
    public final class SuspendBlockerImpl implements SuspendBlocker {
        public final String mName;
        public final int mNameHash;
        public final ArrayMap<String, LongArray> mOpenReferenceTimes = new ArrayMap<>();
        public int mReferenceCount;

        public SuspendBlockerImpl(String str) {
            this.mName = str;
            this.mNameHash = str.hashCode();
        }

        public void finalize() throws Throwable {
            try {
                if (this.mReferenceCount != 0) {
                    Slog.wtf("PowerManagerService", "Suspend blocker \"" + this.mName + "\" was finalized without being released!");
                    this.mReferenceCount = 0;
                    PowerManagerService.this.mNativeWrapper.nativeReleaseSuspendBlocker(this.mName);
                    Trace.asyncTraceForTrackEnd(131072L, "SuspendBlockers", this.mNameHash);
                }
            } finally {
                super.finalize();
            }
        }

        @Override // com.android.server.power.SuspendBlocker
        public void acquire() {
            acquire("unknown");
        }

        @Override // com.android.server.power.SuspendBlocker
        public void acquire(String str) {
            synchronized (this) {
                recordReferenceLocked(str);
                int i = this.mReferenceCount + 1;
                this.mReferenceCount = i;
                if (i == 1) {
                    Trace.asyncTraceForTrackBegin(131072L, "SuspendBlockers", this.mName, this.mNameHash);
                    PowerManagerService.this.mNativeWrapper.nativeAcquireSuspendBlocker(this.mName);
                }
            }
        }

        @Override // com.android.server.power.SuspendBlocker
        public void release() {
            release("unknown");
        }

        @Override // com.android.server.power.SuspendBlocker
        public void release(String str) {
            synchronized (this) {
                removeReferenceLocked(str);
                int i = this.mReferenceCount - 1;
                this.mReferenceCount = i;
                if (i == 0) {
                    PowerManagerService.this.mNativeWrapper.nativeReleaseSuspendBlocker(this.mName);
                    if (Trace.isTagEnabled(131072L)) {
                        Trace.asyncTraceForTrackEnd(131072L, "SuspendBlockers", this.mNameHash);
                    }
                } else if (i < 0) {
                    Slog.wtf("PowerManagerService", "Suspend blocker \"" + this.mName + "\" was released without being acquired!", new Throwable());
                    this.mReferenceCount = 0;
                }
            }
        }

        public String toString() {
            String sb;
            synchronized (this) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(this.mName);
                sb2.append(": ref count=");
                sb2.append(this.mReferenceCount);
                sb2.append(" [");
                int size = this.mOpenReferenceTimes.size();
                for (int i = 0; i < size; i++) {
                    String keyAt = this.mOpenReferenceTimes.keyAt(i);
                    LongArray valueAt = this.mOpenReferenceTimes.valueAt(i);
                    if (valueAt != null && valueAt.size() != 0) {
                        if (i > 0) {
                            sb2.append(", ");
                        }
                        sb2.append(keyAt);
                        sb2.append(": (");
                        for (int i2 = 0; i2 < valueAt.size(); i2++) {
                            if (i2 > 0) {
                                sb2.append(", ");
                            }
                            sb2.append(PowerManagerService.DATE_FORMAT.format(new Date(valueAt.get(i2))));
                        }
                        sb2.append(")");
                    }
                }
                sb2.append("]");
                sb = sb2.toString();
            }
            return sb;
        }

        @Override // com.android.server.power.SuspendBlocker
        public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            synchronized (this) {
                protoOutputStream.write(1138166333441L, this.mName);
                protoOutputStream.write(1120986464258L, this.mReferenceCount);
            }
            protoOutputStream.end(start);
        }

        public final void recordReferenceLocked(String str) {
            LongArray longArray = this.mOpenReferenceTimes.get(str);
            if (longArray == null) {
                longArray = new LongArray();
                this.mOpenReferenceTimes.put(str, longArray);
            }
            longArray.add(System.currentTimeMillis());
        }

        public final void removeReferenceLocked(String str) {
            LongArray longArray = this.mOpenReferenceTimes.get(str);
            if (longArray == null || longArray.size() <= 0) {
                return;
            }
            longArray.remove(longArray.size() - 1);
        }
    }

    /* loaded from: classes2.dex */
    public static final class UidState {
        public boolean mActive;
        public int mNumWakeLocks;
        public int mProcState;
        public final int mUid;

        public UidState(int i) {
            this.mUid = i;
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class BinderService extends IPowerManager.Stub {
        public final PowerManagerShellCommand mShellCommand;

        public BinderService(Context context) {
            this.mShellCommand = new PowerManagerShellCommand(context, this);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            this.mShellCommand.exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public void acquireWakeLockWithUid(IBinder iBinder, int i, String str, String str2, int i2, int i3, IWakeLockCallback iWakeLockCallback) {
            acquireWakeLock(iBinder, i, str, str2, new WorkSource(i2 < 0 ? Binder.getCallingUid() : i2), null, i3, iWakeLockCallback);
        }

        public void setPowerBoost(int i, int i2) {
            if (PowerManagerService.this.mSystemReady) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
                PowerManagerService.this.setPowerBoostInternal(i, i2);
            }
        }

        public void setPowerMode(int i, boolean z) {
            if (PowerManagerService.this.mSystemReady) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
                PowerManagerService.this.setPowerModeInternal(i, z);
            }
        }

        public boolean setPowerModeChecked(int i, boolean z) {
            if (PowerManagerService.this.mSystemReady) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
                return PowerManagerService.this.setPowerModeInternal(i, z);
            }
            return false;
        }

        @RequiresPermission(conditional = true, value = "android.permission.TURN_SCREEN_ON")
        public void acquireWakeLock(IBinder iBinder, int i, String str, String str2, WorkSource workSource, String str3, int i2, IWakeLockCallback iWakeLockCallback) {
            WorkSource workSource2;
            int i3;
            int i4;
            WorkSource workSource3;
            if (iBinder == null) {
                throw new IllegalArgumentException("lock must not be null");
            }
            if (str2 == null) {
                throw new IllegalArgumentException("packageName must not be null");
            }
            PowerManager.validateWakeLockParameters(i, str);
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
            if ((i & 64) != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            }
            if (workSource == null || workSource.isEmpty()) {
                workSource2 = null;
            } else {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", null);
                workSource2 = workSource;
            }
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            if ((Integer.MIN_VALUE & i) != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
                WorkSource workSource4 = new WorkSource(Binder.getCallingUid(), str2);
                if (workSource2 != null && !workSource2.isEmpty()) {
                    workSource4.add(workSource2);
                }
                i3 = Process.myUid();
                workSource3 = workSource4;
                i4 = Process.myPid();
            } else {
                i3 = callingUid;
                i4 = callingPid;
                workSource3 = workSource2;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.acquireWakeLockInternal(iBinder, i2, i, str, str2, workSource3, str3, i3, i4, iWakeLockCallback);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void acquireWakeLockAsync(IBinder iBinder, int i, String str, String str2, WorkSource workSource, String str3) {
            acquireWakeLock(iBinder, i, str, str2, workSource, str3, -1, null);
        }

        public void releaseWakeLock(IBinder iBinder, int i) {
            if (iBinder == null) {
                throw new IllegalArgumentException("lock must not be null");
            }
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.releaseWakeLockInternal(iBinder, i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void releaseWakeLockAsync(IBinder iBinder, int i) {
            releaseWakeLock(iBinder, i);
        }

        public void updateWakeLockUids(IBinder iBinder, int[] iArr) {
            WorkSource workSource;
            if (iArr != null) {
                workSource = new WorkSource();
                for (int i : iArr) {
                    workSource.add(i);
                }
            } else {
                workSource = null;
            }
            updateWakeLockWorkSource(iBinder, workSource, null);
        }

        public void updateWakeLockUidsAsync(IBinder iBinder, int[] iArr) {
            updateWakeLockUids(iBinder, iArr);
        }

        public void updateWakeLockWorkSource(IBinder iBinder, WorkSource workSource, String str) {
            if (iBinder == null) {
                throw new IllegalArgumentException("lock must not be null");
            }
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
            if (workSource == null || workSource.isEmpty()) {
                workSource = null;
            } else {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", null);
            }
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.updateWakeLockWorkSourceInternal(iBinder, workSource, str, callingUid);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void updateWakeLockCallback(IBinder iBinder, IWakeLockCallback iWakeLockCallback) {
            if (iBinder == null) {
                throw new IllegalArgumentException("lock must not be null");
            }
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", null);
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.updateWakeLockCallbackInternal(iBinder, iWakeLockCallback, callingUid);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isWakeLockLevelSupported(int i) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isWakeLockLevelSupportedInternal(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void userActivity(int i, long j, int i2, int i3) {
            long uptimeMillis = PowerManagerService.this.mClock.uptimeMillis();
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0 && PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.USER_ACTIVITY") != 0) {
                synchronized (PowerManagerService.this.mLock) {
                    if (uptimeMillis >= PowerManagerService.this.mLastWarningAboutUserActivityPermission + BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS) {
                        PowerManagerService.this.mLastWarningAboutUserActivityPermission = uptimeMillis;
                        Slog.w("PowerManagerService", "Ignoring call to PowerManager.userActivity() because the caller does not have DEVICE_POWER or USER_ACTIVITY permission.  Please fix your app!   pid=" + Binder.getCallingPid() + " uid=" + Binder.getCallingUid());
                    }
                }
            } else if (j > uptimeMillis) {
                Slog.e("PowerManagerService", "Event time " + j + " cannot be newer than " + uptimeMillis);
                throw new IllegalArgumentException("event time must not be in the future");
            } else {
                int callingUid = Binder.getCallingUid();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.userActivityInternal(i, j, i2, i3, callingUid);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void wakeUp(long j, int i, String str, String str2) {
            long uptimeMillis = PowerManagerService.this.mClock.uptimeMillis();
            if (j > uptimeMillis) {
                Slog.e("PowerManagerService", "Event time " + j + " cannot be newer than " + uptimeMillis);
                throw new IllegalArgumentException("event time must not be in the future");
            }
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (PowerManagerService.this.mLock) {
                    if (!PowerManagerService.this.mBootCompleted && PowerManagerService.sQuiescent) {
                        PowerManagerService.this.mDirty |= IInstalld.FLAG_USE_QUOTA;
                        PowerManagerService.this.updatePowerStateLocked();
                        return;
                    }
                    PowerManagerService powerManagerService = PowerManagerService.this;
                    powerManagerService.wakePowerGroupLocked((PowerGroup) powerManagerService.mPowerGroups.get(0), j, i, str, callingUid, str2, callingUid);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission("android.permission.DEVICE_POWER")
        public void goToSleep(long j, int i, int i2) {
            PowerManagerService.this.goToSleepInternal(PowerManagerService.DEFAULT_DISPLAY_GROUP_IDS, j, i, i2);
        }

        @RequiresPermission("android.permission.DEVICE_POWER")
        public void goToSleepWithDisplayId(int i, long j, int i2, int i3) {
            IntArray wrap;
            if (i == -1) {
                wrap = PowerManagerService.this.mDisplayManagerInternal.getDisplayGroupIds();
            } else {
                DisplayInfo displayInfo = PowerManagerService.this.mDisplayManagerInternal.getDisplayInfo(i);
                Preconditions.checkArgument(displayInfo != null, "display ID(%d) doesn't exist", new Object[]{Integer.valueOf(i)});
                int i4 = displayInfo.displayGroupId;
                if (i4 == -1) {
                    throw new IllegalArgumentException("invalid display group ID");
                }
                wrap = IntArray.wrap(new int[]{i4});
            }
            PowerManagerService.this.goToSleepInternal(wrap, j, i2, i3);
        }

        public void nap(long j) {
            long uptimeMillis = PowerManagerService.this.mClock.uptimeMillis();
            if (j > uptimeMillis) {
                Slog.e("PowerManagerService", "Event time " + j + " cannot be newer than " + uptimeMillis);
                throw new IllegalArgumentException("event time must not be in the future");
            }
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.napInternal(j, callingUid, false);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public float getBrightnessConstraint(int i) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        if (i != 3) {
                            if (i != 4) {
                                return Float.NaN;
                            }
                            return PowerManagerService.this.mScreenBrightnessDoze;
                        }
                        return PowerManagerService.this.mScreenBrightnessDim;
                    }
                    return PowerManagerService.this.mScreenBrightnessDefault;
                }
                return PowerManagerService.this.mScreenBrightnessMaximum;
            }
            return PowerManagerService.this.mScreenBrightnessMinimum;
        }

        public boolean isInteractive() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isInteractiveInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean areAutoPowerSaveModesEnabled() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mContext.getResources().getBoolean(17891644);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isPowerSaveMode() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverController.isEnabled();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public PowerSaveState getPowerSaveState(int i) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverPolicy.getBatterySaverPolicy(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean setPowerSaveModeEnabled(boolean z) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.setLowPowerModeInternal(z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public BatterySaverPolicyConfig getFullPowerSavePolicy() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverStateMachine.getFullBatterySaverPolicy();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean setFullPowerSavePolicy(BatterySaverPolicyConfig batterySaverPolicyConfig) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", "setFullPowerSavePolicy");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverStateMachine.setFullBatterySaverPolicy(batterySaverPolicyConfig);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean setDynamicPowerSaveHint(boolean z, int i) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.POWER_SAVER", "updateDynamicPowerSavings");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                ContentResolver contentResolver = PowerManagerService.this.mContext.getContentResolver();
                boolean putInt = Settings.Global.putInt(contentResolver, "dynamic_power_savings_disable_threshold", i);
                if (putInt) {
                    putInt &= Settings.Global.putInt(contentResolver, "dynamic_power_savings_enabled", z ? 1 : 0);
                }
                return putInt;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig batterySaverPolicyConfig) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", "setAdaptivePowerSavePolicy");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverStateMachine.setAdaptiveBatterySaverPolicy(batterySaverPolicyConfig);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean setAdaptivePowerSaveEnabled(boolean z) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", "setAdaptivePowerSaveEnabled");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverStateMachine.setAdaptiveBatterySaverEnabled(z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int getPowerSaveModeTrigger() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return Settings.Global.getInt(PowerManagerService.this.mContext.getContentResolver(), "automatic_power_save_mode", 0);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setBatteryDischargePrediction(ParcelDuration parcelDuration, boolean z) {
            long elapsedRealtime = PowerManagerService.this.mClock.elapsedRealtime();
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.BATTERY_PREDICTION") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", "setBatteryDischargePrediction");
            }
            long millis = parcelDuration.getDuration().toMillis();
            Preconditions.checkArgumentPositive((float) millis, "Given time remaining is not positive: " + millis);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (PowerManagerService.this.mLock) {
                    if (PowerManagerService.this.mIsPowered) {
                        throw new IllegalStateException("Discharge prediction can't be set while the device is charging");
                    }
                }
                synchronized (PowerManagerService.this.mEnhancedDischargeTimeLock) {
                    if (PowerManagerService.this.mLastEnhancedDischargeTimeUpdatedElapsed > elapsedRealtime) {
                        return;
                    }
                    long max = Math.max(0L, 60000 - (elapsedRealtime - PowerManagerService.this.mLastEnhancedDischargeTimeUpdatedElapsed));
                    PowerManagerService.this.mEnhancedDischargeTimeElapsed = millis + elapsedRealtime;
                    PowerManagerService.this.mEnhancedDischargePredictionIsPersonalized = z;
                    PowerManagerService.this.mLastEnhancedDischargeTimeUpdatedElapsed = elapsedRealtime;
                    PowerManagerService.this.mNotifier.postEnhancedDischargePredictionBroadcast(max);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @GuardedBy({"PowerManagerService.this.mEnhancedDischargeTimeLock"})
        public final boolean isEnhancedDischargePredictionValidLocked(long j) {
            return PowerManagerService.this.mLastEnhancedDischargeTimeUpdatedElapsed > 0 && j < PowerManagerService.this.mEnhancedDischargeTimeElapsed && j - PowerManagerService.this.mLastEnhancedDischargeTimeUpdatedElapsed < 1800000;
        }

        public ParcelDuration getBatteryDischargePrediction() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (PowerManagerService.this.mLock) {
                    if (PowerManagerService.this.mIsPowered) {
                        return null;
                    }
                    synchronized (PowerManagerService.this.mEnhancedDischargeTimeLock) {
                        long elapsedRealtime = PowerManagerService.this.mClock.elapsedRealtime();
                        if (isEnhancedDischargePredictionValidLocked(elapsedRealtime)) {
                            return new ParcelDuration(PowerManagerService.this.mEnhancedDischargeTimeElapsed - elapsedRealtime);
                        }
                        return new ParcelDuration(PowerManagerService.this.mBatteryStats.computeBatteryTimeRemaining());
                    }
                }
            } catch (RemoteException unused) {
                return null;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isBatteryDischargePredictionPersonalized() {
            boolean z;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (PowerManagerService.this.mEnhancedDischargeTimeLock) {
                    z = isEnhancedDischargePredictionValidLocked(PowerManagerService.this.mClock.elapsedRealtime()) && PowerManagerService.this.mEnhancedDischargePredictionIsPersonalized;
                }
                return z;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isDeviceIdleMode() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isDeviceIdleModeInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isLightDeviceIdleMode() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isLightDeviceIdleModeInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission(anyOf = {"android.permission.MANAGE_LOW_POWER_STANDBY", "android.permission.DEVICE_POWER"})
        public boolean isLowPowerStandbySupported() {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_LOW_POWER_STANDBY", "isLowPowerStandbySupported");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mLowPowerStandbyController.isSupported();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isLowPowerStandbyEnabled() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mLowPowerStandbyController.isEnabled();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission(anyOf = {"android.permission.MANAGE_LOW_POWER_STANDBY", "android.permission.DEVICE_POWER"})
        public void setLowPowerStandbyEnabled(boolean z) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_LOW_POWER_STANDBY", "setLowPowerStandbyEnabled");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.mLowPowerStandbyController.setEnabled(z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission(anyOf = {"android.permission.MANAGE_LOW_POWER_STANDBY", "android.permission.DEVICE_POWER"})
        public void setLowPowerStandbyActiveDuringMaintenance(boolean z) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_LOW_POWER_STANDBY", "setLowPowerStandbyActiveDuringMaintenance");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.mLowPowerStandbyController.setActiveDuringMaintenance(z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission(anyOf = {"android.permission.MANAGE_LOW_POWER_STANDBY", "android.permission.DEVICE_POWER"})
        public void forceLowPowerStandbyActive(boolean z) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_LOW_POWER_STANDBY", "forceLowPowerStandbyActive");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.mLowPowerStandbyController.forceActive(z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission(anyOf = {"android.permission.MANAGE_LOW_POWER_STANDBY", "android.permission.DEVICE_POWER"})
        public void setLowPowerStandbyPolicy(IPowerManager.LowPowerStandbyPolicy lowPowerStandbyPolicy) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_LOW_POWER_STANDBY", "setLowPowerStandbyPolicy");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.mLowPowerStandbyController.setPolicy(PowerManager.LowPowerStandbyPolicy.fromParcelable(lowPowerStandbyPolicy));
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission(anyOf = {"android.permission.MANAGE_LOW_POWER_STANDBY", "android.permission.DEVICE_POWER"})
        public IPowerManager.LowPowerStandbyPolicy getLowPowerStandbyPolicy() {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_LOW_POWER_STANDBY", "getLowPowerStandbyPolicy");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManager.LowPowerStandbyPolicy.toParcelable(PowerManagerService.this.mLowPowerStandbyController.getPolicy());
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isExemptFromLowPowerStandby() {
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mLowPowerStandbyController.isPackageExempt(callingUid);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isReasonAllowedInLowPowerStandby(int i) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mLowPowerStandbyController.isAllowed(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isFeatureAllowedInLowPowerStandby(String str) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mLowPowerStandbyController.isAllowed(str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission("android.permission.SET_LOW_POWER_STANDBY_PORTS")
        public void acquireLowPowerStandbyPorts(IBinder iBinder, List<IPowerManager.LowPowerStandbyPortDescription> list) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.SET_LOW_POWER_STANDBY_PORTS", "acquireLowPowerStandbyPorts");
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.mLowPowerStandbyController.acquireStandbyPorts(iBinder, callingUid, PowerManager.LowPowerStandbyPortDescription.fromParcelable(list));
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission("android.permission.SET_LOW_POWER_STANDBY_PORTS")
        public void releaseLowPowerStandbyPorts(IBinder iBinder) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.SET_LOW_POWER_STANDBY_PORTS", "releaseLowPowerStandbyPorts");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.mLowPowerStandbyController.releaseStandbyPorts(iBinder);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        @RequiresPermission(anyOf = {"android.permission.MANAGE_LOW_POWER_STANDBY", "android.permission.DEVICE_POWER"})
        public List<IPowerManager.LowPowerStandbyPortDescription> getActiveLowPowerStandbyPorts() {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_LOW_POWER_STANDBY", "getActiveLowPowerStandbyPorts");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManager.LowPowerStandbyPortDescription.toParcelable(PowerManagerService.this.mLowPowerStandbyController.getActiveStandbyPorts());
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int getLastShutdownReason() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.getLastShutdownReasonInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int getLastSleepReason() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.getLastSleepReasonInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void reboot(boolean z, String str, boolean z2) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", null);
            if ("recovery".equals(str) || "recovery-update".equals(str)) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.RECOVERY", null);
            }
            ShutdownCheckPoints.recordCheckPoint(Binder.getCallingPid(), str);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.shutdownOrRebootInternal(1, z, str, z2);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void rebootSafeMode(boolean z, boolean z2) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", null);
            ShutdownCheckPoints.recordCheckPoint(Binder.getCallingPid(), "safemode");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.shutdownOrRebootInternal(2, z, "safemode", z2);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void shutdown(boolean z, String str, boolean z2) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", null);
            ShutdownCheckPoints.recordCheckPoint(Binder.getCallingPid(), str);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.shutdownOrRebootInternal(0, z, str, z2);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void crash(String str) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.crashInternal(str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setStayOnSetting(int i) {
            int callingUid = Binder.getCallingUid();
            if (callingUid == 0 || Settings.checkAndNoteWriteSettingsOperation(PowerManagerService.this.mContext, callingUid, Settings.getPackageNameForUid(PowerManagerService.this.mContext, callingUid), null, true)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.setStayOnSettingInternal(i);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void setAttentionLight(boolean z, int i) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.setAttentionLightInternal(z, i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setDozeAfterScreenOff(boolean z) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.setDozeAfterScreenOffInternal(z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isAmbientDisplayAvailable() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.READ_DREAM_STATE", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mAmbientDisplayConfiguration.ambientDisplayAvailable();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void suppressAmbientDisplay(String str, boolean z) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.WRITE_DREAM_STATE", null);
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.mAmbientDisplaySuppressionController.suppress(str, callingUid, z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isAmbientDisplaySuppressedForToken(String str) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.READ_DREAM_STATE", null);
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mAmbientDisplaySuppressionController.isSuppressed(str, callingUid);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isAmbientDisplaySuppressedForTokenByApp(String str, int i) {
            boolean z;
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.READ_DREAM_STATE", null);
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.READ_DREAM_SUPPRESSION", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (isAmbientDisplayAvailable()) {
                    if (PowerManagerService.this.mAmbientDisplaySuppressionController.isSuppressed(str, i)) {
                        z = true;
                        return z;
                    }
                }
                z = false;
                return z;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isAmbientDisplaySuppressed() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.READ_DREAM_STATE", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mAmbientDisplaySuppressionController.isSuppressed();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void boostScreenBrightness(long j) {
            long uptimeMillis = PowerManagerService.this.mClock.uptimeMillis();
            if (j > PowerManagerService.this.mClock.uptimeMillis()) {
                Slog.e("PowerManagerService", "Event time " + j + " cannot be newer than " + uptimeMillis);
                throw new IllegalArgumentException("event time must not be in the future");
            }
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.boostScreenBrightnessInternal(j, callingUid);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isScreenBrightnessBoosted() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isScreenBrightnessBoostedInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean forceSuspend() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.forceSuspendInternal(callingUid);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(PowerManagerService.this.mContext, "PowerManagerService", printWriter)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                boolean z = false;
                for (String str : strArr) {
                    if (str.equals("--proto")) {
                        z = true;
                        break;
                    }
                }
                try {
                    if (z) {
                        PowerManagerService.this.dumpProto(fileDescriptor);
                    } else {
                        PowerManagerService.this.dumpInternal(printWriter);
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public List<String> getAmbientDisplaySuppressionTokens() {
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mAmbientDisplaySuppressionController.getSuppressionTokens(callingUid);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    @VisibleForTesting
    public BinderService getBinderServiceInstance() {
        return this.mBinderService;
    }

    @VisibleForTesting
    public LocalService getLocalServiceInstance() {
        return this.mLocalService;
    }

    @VisibleForTesting
    public int getLastShutdownReasonInternal() {
        String str = this.mSystemProperties.get("sys.boot.reason", null);
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2117951935:
                if (str.equals("shutdown,thermal")) {
                    c = 0;
                    break;
                }
                break;
            case -1099647817:
                if (str.equals("shutdown,battery")) {
                    c = 1;
                    break;
                }
                break;
            case -934938715:
                if (str.equals("reboot")) {
                    c = 2;
                    break;
                }
                break;
            case -852189395:
                if (str.equals("shutdown,userrequested")) {
                    c = 3;
                    break;
                }
                break;
            case -169343402:
                if (str.equals("shutdown")) {
                    c = 4;
                    break;
                }
                break;
            case 1218064802:
                if (str.equals("shutdown,thermal,battery")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 4;
            case 1:
                return 5;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 1;
            case 5:
                return 6;
            default:
                return 0;
        }
    }

    public final int getLastSleepReasonInternal() {
        int i;
        synchronized (this.mLock) {
            i = this.mLastGlobalSleepReason;
        }
        return i;
    }

    public final PowerManager.WakeData getLastWakeupInternal() {
        PowerManager.WakeData wakeData;
        synchronized (this.mLock) {
            wakeData = new PowerManager.WakeData(this.mLastGlobalWakeTime, this.mLastGlobalWakeReason, this.mLastGlobalWakeTimeRealtime - this.mLastGlobalSleepTimeRealtime);
        }
        return wakeData;
    }

    public final PowerManager.SleepData getLastGoToSleepInternal() {
        PowerManager.SleepData sleepData;
        synchronized (this.mLock) {
            sleepData = new PowerManager.SleepData(this.mLastGlobalSleepTime, this.mLastGlobalSleepReason);
        }
        return sleepData;
    }

    public final boolean interceptPowerKeyDownInternal(KeyEvent keyEvent) {
        synchronized (this.mLock) {
            if (!this.mProximityPositive || this.mInterceptedPowerKeyForProximity) {
                return false;
            }
            this.mDisplayManagerInternal.ignoreProximitySensorUntilChanged();
            this.mInterceptedPowerKeyForProximity = true;
            return true;
        }
    }

    @RequiresPermission("android.permission.DEVICE_POWER")
    public final void goToSleepInternal(IntArray intArray, long j, int i, int i2) {
        long uptimeMillis = this.mClock.uptimeMillis();
        if (j > uptimeMillis) {
            Slog.e("PowerManagerService", "Event time " + j + " cannot be newer than " + uptimeMillis);
            throw new IllegalArgumentException("event time must not be in the future");
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
        boolean z = (i2 & 1) != 0;
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                for (int i3 = 0; i3 < intArray.size(); i3++) {
                    int i4 = intArray.get(i3);
                    PowerGroup powerGroup = this.mPowerGroups.get(i4);
                    if (powerGroup == null) {
                        throw new IllegalArgumentException("power group(" + i4 + ") doesn't exist");
                    }
                    if ((i2 & 2) == 0 || !powerGroup.hasWakeLockKeepingScreenOnLocked()) {
                        if (z) {
                            sleepPowerGroupLocked(powerGroup, j, i, callingUid);
                        } else {
                            dozePowerGroupLocked(powerGroup, j, i, callingUid);
                        }
                    }
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public final class LocalService extends PowerManagerInternal {
        public LocalService() {
        }

        public void setScreenBrightnessOverrideFromWindowManager(float f) {
            PowerManagerService.this.setScreenBrightnessOverrideFromWindowManagerInternal((f < 0.0f || f > 1.0f) ? Float.NaN : Float.NaN);
        }

        public void setDozeOverrideFromDreamManager(int i, int i2) {
            if (i != 0 && i != 1 && i != 2 && i != 3 && i != 4 && i != 6) {
                i = 0;
            }
            if (i2 < -1 || i2 > 255) {
                i2 = -1;
            }
            PowerManagerService.this.setDozeOverrideFromDreamManagerInternal(i, i2);
        }

        public void setUserInactiveOverrideFromWindowManager() {
            PowerManagerService.this.setUserInactiveOverrideFromWindowManagerInternal();
        }

        public void setUserActivityTimeoutOverrideFromWindowManager(long j) {
            PowerManagerService.this.setUserActivityTimeoutOverrideFromWindowManagerInternal(j);
        }

        public void setDrawWakeLockOverrideFromSidekick(boolean z) {
            PowerManagerService.this.setDrawWakeLockOverrideFromSidekickInternal(z);
        }

        public void setMaximumScreenOffTimeoutFromDeviceAdmin(int i, long j) {
            PowerManagerService.this.setMaximumScreenOffTimeoutFromDeviceAdminInternal(i, j);
        }

        public PowerSaveState getLowPowerState(int i) {
            return PowerManagerService.this.mBatterySaverPolicy.getBatterySaverPolicy(i);
        }

        public void registerLowPowerModeObserver(PowerManagerInternal.LowPowerModeListener lowPowerModeListener) {
            PowerManagerService.this.mBatterySaverController.addListener(lowPowerModeListener);
        }

        public boolean setDeviceIdleMode(boolean z) {
            return PowerManagerService.this.setDeviceIdleModeInternal(z);
        }

        public boolean setLightDeviceIdleMode(boolean z) {
            return PowerManagerService.this.setLightDeviceIdleModeInternal(z);
        }

        public void setDeviceIdleWhitelist(int[] iArr) {
            PowerManagerService.this.setDeviceIdleWhitelistInternal(iArr);
        }

        public void setDeviceIdleTempWhitelist(int[] iArr) {
            PowerManagerService.this.setDeviceIdleTempWhitelistInternal(iArr);
        }

        public void setLowPowerStandbyAllowlist(int[] iArr) {
            PowerManagerService.this.setLowPowerStandbyAllowlistInternal(iArr);
        }

        public void setLowPowerStandbyActive(boolean z) {
            PowerManagerService.this.setLowPowerStandbyActiveInternal(z);
        }

        public void startUidChanges() {
            PowerManagerService.this.startUidChangesInternal();
        }

        public void finishUidChanges() {
            PowerManagerService.this.finishUidChangesInternal();
        }

        public void updateUidProcState(int i, int i2) {
            PowerManagerService.this.updateUidProcStateInternal(i, i2);
        }

        public void uidGone(int i) {
            PowerManagerService.this.uidGoneInternal(i);
        }

        public void uidActive(int i) {
            PowerManagerService.this.uidActiveInternal(i);
        }

        public void uidIdle(int i) {
            PowerManagerService.this.uidIdleInternal(i);
        }

        public void setPowerBoost(int i, int i2) {
            PowerManagerService.this.setPowerBoostInternal(i, i2);
        }

        public void setPowerMode(int i, boolean z) {
            PowerManagerService.this.setPowerModeInternal(i, z);
        }

        public boolean wasDeviceIdleFor(long j) {
            return PowerManagerService.this.wasDeviceIdleForInternal(j);
        }

        public PowerManager.WakeData getLastWakeup() {
            return PowerManagerService.this.getLastWakeupInternal();
        }

        public PowerManager.SleepData getLastGoToSleep() {
            return PowerManagerService.this.getLastGoToSleepInternal();
        }

        public boolean interceptPowerKeyDown(KeyEvent keyEvent) {
            return PowerManagerService.this.interceptPowerKeyDownInternal(keyEvent);
        }

        public void nap(long j, boolean z) {
            PowerManagerService.this.napInternal(j, 1000, z);
        }

        public boolean isAmbientDisplaySuppressed() {
            return PowerManagerService.this.mAmbientDisplaySuppressionController.isSuppressed();
        }
    }

    /* loaded from: classes2.dex */
    public class DeviceStateListener implements DeviceStateManager.DeviceStateCallback {
        public int mDeviceState = -1;

        public DeviceStateListener() {
        }

        public void onStateChanged(int i) {
            if (this.mDeviceState != i) {
                this.mDeviceState = i;
                PowerManagerService powerManagerService = PowerManagerService.this;
                powerManagerService.userActivityInternal(0, powerManagerService.mClock.uptimeMillis(), 6, 0, 1000);
            }
        }
    }

    public static boolean isSameCallback(IWakeLockCallback iWakeLockCallback, IWakeLockCallback iWakeLockCallback2) {
        if (iWakeLockCallback == iWakeLockCallback2) {
            return true;
        }
        return (iWakeLockCallback == null || iWakeLockCallback2 == null || iWakeLockCallback.asBinder() != iWakeLockCallback2.asBinder()) ? false : true;
    }
}
