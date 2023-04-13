package com.android.server;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.IApplicationThread;
import android.app.IOnProjectionStateChangedListener;
import android.app.IUiModeManager;
import android.app.IUiModeManagerCallback;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.dreams.Sandman;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.DisableCarModeActivity;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.SystemService;
import com.android.server.UiModeManagerService;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.twilight.TwilightListener;
import com.android.server.twilight.TwilightManager;
import com.android.server.twilight.TwilightState;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class UiModeManagerService extends SystemService {
    public final LocalTime DEFAULT_CUSTOM_NIGHT_END_TIME;
    public final LocalTime DEFAULT_CUSTOM_NIGHT_START_TIME;
    public ActivityTaskManagerInternal mActivityTaskManager;
    public AlarmManager mAlarmManager;
    public final BroadcastReceiver mBatteryReceiver;
    public boolean mCar;
    public int mCarModeEnableFlags;
    public boolean mCarModeEnabled;
    public boolean mCarModeKeepsScreenOn;
    public Map<Integer, String> mCarModePackagePriority;
    public boolean mCharging;
    public boolean mComputedNightMode;
    public Configuration mConfiguration;
    public final ContentObserver mContrastObserver;
    @GuardedBy({"mLock"})
    public final SparseArray<Float> mContrasts;
    public int mCurUiMode;
    public int mCurrentUser;
    public LocalTime mCustomAutoNightModeEndMilliseconds;
    public LocalTime mCustomAutoNightModeStartMilliseconds;
    public final AlarmManager.OnAlarmListener mCustomTimeListener;
    public final ContentObserver mDarkThemeObserver;
    public int mDefaultUiModeType;
    public boolean mDeskModeKeepsScreenOn;
    public final BroadcastReceiver mDockModeReceiver;
    public int mDockState;
    public boolean mDreamsDisabledByAmbientModeSuppression;
    public boolean mEnableCarDockLaunch;
    public final Handler mHandler;
    public boolean mHoldingConfiguration;
    public final Injector mInjector;
    public KeyguardManager mKeyguardManager;
    public boolean mLastBedtimeRequestedNightMode;
    public int mLastBroadcastState;
    public PowerManagerInternal mLocalPowerManager;
    public final LocalService mLocalService;
    public final Object mLock;
    public int mNightMode;
    public int mNightModeCustomType;
    public boolean mNightModeLocked;
    public NotificationManager mNotificationManager;
    public final BroadcastReceiver mOnScreenOffHandler;
    public final BroadcastReceiver mOnShutdown;
    public final BroadcastReceiver mOnTimeChangedHandler;
    public boolean mOverrideNightModeOff;
    public boolean mOverrideNightModeOn;
    public int mOverrideNightModeUser;
    public PowerManager mPowerManager;
    public boolean mPowerSave;
    @GuardedBy({"mLock"})
    public SparseArray<List<ProjectionHolder>> mProjectionHolders;
    @GuardedBy({"mLock"})
    public SparseArray<RemoteCallbackList<IOnProjectionStateChangedListener>> mProjectionListeners;
    public final BroadcastReceiver mResultReceiver;
    public final IUiModeManager.Stub mService;
    public int mSetUiMode;
    public final BroadcastReceiver mSettingsRestored;
    public boolean mSetupWizardComplete;
    public final ContentObserver mSetupWizardObserver;
    public boolean mStartDreamImmediatelyOnDock;
    public StatusBarManager mStatusBarManager;
    public boolean mSystemReady;
    public boolean mTelevision;
    public final TwilightListener mTwilightListener;
    public TwilightManager mTwilightManager;
    public boolean mUiModeLocked;
    @GuardedBy({"mLock"})
    public final RemoteCallbackList<IUiModeManagerCallback> mUiModeManagerCallbacks;
    public boolean mVrHeadset;
    public final IVrStateCallbacks mVrStateCallbacks;
    public boolean mWaitForScreenOff;
    public PowerManager.WakeLock mWakeLock;
    public boolean mWatch;
    public WindowManagerInternal mWindowManager;
    public static final String TAG = UiModeManager.class.getSimpleName();
    @VisibleForTesting
    public static final Set<Integer> SUPPORTED_NIGHT_MODE_CUSTOM_TYPES = new ArraySet(new Integer[]{0, 1});

    public static boolean isDeskDockState(int i) {
        return i == 1 || i == 3 || i == 4;
    }

    public UiModeManagerService(Context context) {
        this(context, false, null, new Injector());
    }

    @VisibleForTesting
    public UiModeManagerService(Context context, boolean z, TwilightManager twilightManager, Injector injector) {
        super(context);
        this.mLock = new Object();
        this.mDockState = 0;
        this.mLastBroadcastState = 0;
        this.mNightMode = 1;
        this.mNightModeCustomType = -1;
        LocalTime of = LocalTime.of(22, 0);
        this.DEFAULT_CUSTOM_NIGHT_START_TIME = of;
        LocalTime of2 = LocalTime.of(6, 0);
        this.DEFAULT_CUSTOM_NIGHT_END_TIME = of2;
        this.mCustomAutoNightModeStartMilliseconds = of;
        this.mCustomAutoNightModeEndMilliseconds = of2;
        this.mCarModePackagePriority = new HashMap();
        this.mCarModeEnabled = false;
        this.mCharging = false;
        this.mPowerSave = false;
        this.mWaitForScreenOff = false;
        this.mLastBedtimeRequestedNightMode = false;
        this.mStartDreamImmediatelyOnDock = true;
        this.mDreamsDisabledByAmbientModeSuppression = false;
        this.mEnableCarDockLaunch = true;
        this.mUiModeLocked = false;
        this.mNightModeLocked = false;
        this.mCurUiMode = 0;
        this.mSetUiMode = 0;
        this.mHoldingConfiguration = false;
        this.mConfiguration = new Configuration();
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mOverrideNightModeUser = 0;
        this.mLocalService = new LocalService();
        this.mUiModeManagerCallbacks = new RemoteCallbackList<>();
        this.mContrasts = new SparseArray<>();
        this.mResultReceiver = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (getResultCode() != -1) {
                    return;
                }
                int intExtra = intent.getIntExtra("enableFlags", 0);
                int intExtra2 = intent.getIntExtra("disableFlags", 0);
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService.this.updateAfterBroadcastLocked(intent.getAction(), intExtra, intExtra2);
                }
            }
        };
        this.mDockModeReceiver = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                UiModeManagerService.this.updateDockState(intent.getIntExtra("android.intent.extra.DOCK_STATE", 0));
            }
        };
        this.mBatteryReceiver = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                    UiModeManagerService.this.mCharging = intent.getIntExtra("plugged", 0) != 0;
                }
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                    if (uiModeManagerService.mSystemReady) {
                        uiModeManagerService.updateLocked(0, 0);
                    }
                }
            }
        };
        this.mTwilightListener = new TwilightListener() { // from class: com.android.server.UiModeManagerService.4
            @Override // com.android.server.twilight.TwilightListener
            public void onTwilightStateChanged(TwilightState twilightState) {
                synchronized (UiModeManagerService.this.mLock) {
                    if (UiModeManagerService.this.mNightMode == 0) {
                        UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                        if (uiModeManagerService.mSystemReady) {
                            if (uiModeManagerService.shouldApplyAutomaticChangesImmediately()) {
                                UiModeManagerService.this.updateLocked(0, 0);
                            } else {
                                UiModeManagerService.this.registerScreenOffEventLocked();
                            }
                        }
                    }
                }
            }
        };
        this.mOnScreenOffHandler = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService.this.unregisterScreenOffEventLocked();
                    UiModeManagerService.this.updateLocked(0, 0);
                }
            }
        };
        this.mOnTimeChangedHandler = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.6
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService.this.updateCustomTimeLocked();
                }
            }
        };
        this.mCustomTimeListener = new AlarmManager.OnAlarmListener() { // from class: com.android.server.UiModeManagerService$$ExternalSyntheticLambda2
            @Override // android.app.AlarmManager.OnAlarmListener
            public final void onAlarm() {
                UiModeManagerService.this.lambda$new$0();
            }
        };
        this.mVrStateCallbacks = new IVrStateCallbacks.Stub() { // from class: com.android.server.UiModeManagerService.7
            public void onVrStateChanged(boolean z2) {
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService.this.mVrHeadset = z2;
                    UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                    if (uiModeManagerService.mSystemReady) {
                        uiModeManagerService.updateLocked(0, 0);
                    }
                }
            }
        };
        this.mSetupWizardObserver = new ContentObserver(handler) { // from class: com.android.server.UiModeManagerService.8
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                synchronized (UiModeManagerService.this.mLock) {
                    if (UiModeManagerService.this.setupWizardCompleteForCurrentUser() && !z2) {
                        UiModeManagerService.this.mSetupWizardComplete = true;
                        UiModeManagerService.this.getContext().getContentResolver().unregisterContentObserver(UiModeManagerService.this.mSetupWizardObserver);
                        Context context2 = UiModeManagerService.this.getContext();
                        UiModeManagerService.this.updateNightModeFromSettingsLocked(context2, context2.getResources(), UserHandle.getCallingUserId());
                        UiModeManagerService.this.updateLocked(0, 0);
                    }
                }
            }
        };
        this.mDarkThemeObserver = new ContentObserver(handler) { // from class: com.android.server.UiModeManagerService.9
            @Override // android.database.ContentObserver
            public void onChange(boolean z2, Uri uri) {
                UiModeManagerService.this.updateSystemProperties();
            }
        };
        this.mContrastObserver = new C024710(handler);
        this.mOnShutdown = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.11
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (UiModeManagerService.this.mNightMode == 0) {
                    UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                    uiModeManagerService.persistComputedNightMode(uiModeManagerService.mCurrentUser);
                }
            }
        };
        this.mSettingsRestored = new BroadcastReceiver() { // from class: com.android.server.UiModeManagerService.12
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (Arrays.asList("ui_night_mode", "dark_theme_custom_start_time", "dark_theme_custom_end_time").contains(intent.getExtras().getCharSequence("setting_name"))) {
                    synchronized (UiModeManagerService.this.mLock) {
                        UiModeManagerService.this.updateNightModeFromSettingsLocked(context2, context2.getResources(), UserHandle.getCallingUserId());
                        UiModeManagerService.this.updateConfigurationLocked();
                    }
                }
            }
        };
        this.mService = new IUiModeManager$StubC025013();
        this.mConfiguration.setToDefaults();
        this.mSetupWizardComplete = z;
        this.mTwilightManager = twilightManager;
        this.mInjector = injector;
    }

    public static Intent buildHomeIntent(String str) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory(str);
        intent.setFlags(270532608);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        synchronized (this.mLock) {
            updateCustomTimeLocked();
        }
    }

    /* renamed from: com.android.server.UiModeManagerService$10 */
    /* loaded from: classes.dex */
    public class C024710 extends ContentObserver {
        public C024710(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            synchronized (UiModeManagerService.this.mLock) {
                if (UiModeManagerService.this.updateContrastLocked()) {
                    final float contrastLocked = UiModeManagerService.this.getContrastLocked();
                    UiModeManagerService.this.mUiModeManagerCallbacks.broadcast(FunctionalUtils.ignoreRemoteException(new FunctionalUtils.RemoteExceptionIgnoringConsumer() { // from class: com.android.server.UiModeManagerService$10$$ExternalSyntheticLambda0
                        public final void acceptOrThrow(Object obj) {
                            ((IUiModeManagerCallback) obj).notifyContrastChanged(contrastLocked);
                        }
                    }));
                }
            }
        }
    }

    public final void updateSystemProperties() {
        int intForUser = Settings.Secure.getIntForUser(getContext().getContentResolver(), "ui_night_mode", this.mNightMode, 0);
        SystemProperties.set("persist.sys.theme", Integer.toString((intForUser == 0 || intForUser == 3) ? 2 : 2));
    }

    @VisibleForTesting
    public void setStartDreamImmediatelyOnDock(boolean z) {
        this.mStartDreamImmediatelyOnDock = z;
    }

    @VisibleForTesting
    public void setDreamsDisabledByAmbientModeSuppression(boolean z) {
        this.mDreamsDisabledByAmbientModeSuppression = z;
    }

    @Override // com.android.server.SystemService
    public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        this.mCurrentUser = targetUser2.getUserIdentifier();
        if (this.mNightMode == 0) {
            persistComputedNightMode(targetUser.getUserIdentifier());
        }
        getContext().getContentResolver().unregisterContentObserver(this.mSetupWizardObserver);
        verifySetupWizardCompleted();
        synchronized (this.mLock) {
            updateNightModeFromSettingsLocked(getContext(), getContext().getResources(), targetUser2.getUserIdentifier());
            updateLocked(0, 0);
        }
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            synchronized (this.mLock) {
                Context context = getContext();
                boolean z = true;
                this.mSystemReady = true;
                this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
                PowerManager powerManager = (PowerManager) context.getSystemService("power");
                this.mPowerManager = powerManager;
                this.mWakeLock = powerManager.newWakeLock(26, TAG);
                this.mWindowManager = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
                this.mActivityTaskManager = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
                this.mAlarmManager = (AlarmManager) getContext().getSystemService("alarm");
                TwilightManager twilightManager = (TwilightManager) getLocalService(TwilightManager.class);
                if (twilightManager != null) {
                    this.mTwilightManager = twilightManager;
                }
                this.mLocalPowerManager = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
                initPowerSave();
                if (this.mDockState != 2) {
                    z = false;
                }
                this.mCarModeEnabled = z;
                registerVrStateListener();
                context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("ui_night_mode"), false, this.mDarkThemeObserver, 0);
                context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("contrast_level"), false, this.mContrastObserver, -1);
                context.registerReceiver(this.mDockModeReceiver, new IntentFilter("android.intent.action.DOCK_EVENT"));
                context.registerReceiver(this.mBatteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
                context.registerReceiver(this.mSettingsRestored, new IntentFilter("android.os.action.SETTING_RESTORED"));
                context.registerReceiver(this.mOnShutdown, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
                updateConfigurationLocked();
                applyConfigurationExternallyLocked();
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        final Context context = getContext();
        verifySetupWizardCompleted();
        final Resources resources = context.getResources();
        this.mStartDreamImmediatelyOnDock = resources.getBoolean(17891810);
        this.mDreamsDisabledByAmbientModeSuppression = resources.getBoolean(17891629);
        this.mNightMode = resources.getInteger(17694796);
        this.mDefaultUiModeType = resources.getInteger(17694807);
        boolean z = false;
        this.mCarModeKeepsScreenOn = resources.getInteger(17694774) == 1;
        this.mDeskModeKeepsScreenOn = resources.getInteger(17694811) == 1;
        this.mEnableCarDockLaunch = resources.getBoolean(17891647);
        this.mUiModeLocked = resources.getBoolean(17891730);
        this.mNightModeLocked = resources.getBoolean(17891729);
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature("android.hardware.type.television") || packageManager.hasSystemFeature("android.software.leanback")) {
            z = true;
        }
        this.mTelevision = z;
        this.mCar = packageManager.hasSystemFeature("android.hardware.type.automotive");
        this.mWatch = packageManager.hasSystemFeature("android.hardware.type.watch");
        SystemServerInitThreadPool.submit(new Runnable() { // from class: com.android.server.UiModeManagerService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                UiModeManagerService.this.lambda$onStart$1(context, resources);
            }
        }, TAG + ".onStart");
        publishBinderService("uimode", this.mService);
        publishLocalService(UiModeManagerInternal.class, this.mLocalService);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$1(Context context, Resources resources) {
        synchronized (this.mLock) {
            TwilightManager twilightManager = (TwilightManager) getLocalService(TwilightManager.class);
            if (twilightManager != null) {
                this.mTwilightManager = twilightManager;
            }
            updateNightModeFromSettingsLocked(context, resources, UserHandle.getCallingUserId());
            updateSystemProperties();
        }
    }

    public final void persistComputedNightMode(int i) {
        Settings.Secure.putIntForUser(getContext().getContentResolver(), "ui_night_mode_last_computed", this.mComputedNightMode ? 1 : 0, i);
    }

    public final void initPowerSave() {
        this.mPowerSave = this.mLocalPowerManager.getLowPowerState(16).batterySaverEnabled;
        this.mLocalPowerManager.registerLowPowerModeObserver(16, new Consumer() { // from class: com.android.server.UiModeManagerService$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UiModeManagerService.this.lambda$initPowerSave$2((PowerSaveState) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initPowerSave$2(PowerSaveState powerSaveState) {
        synchronized (this.mLock) {
            boolean z = this.mPowerSave;
            boolean z2 = powerSaveState.batterySaverEnabled;
            if (z == z2) {
                return;
            }
            this.mPowerSave = z2;
            if (this.mSystemReady) {
                updateLocked(0, 0);
            }
        }
    }

    @VisibleForTesting
    public IUiModeManager getService() {
        return this.mService;
    }

    @VisibleForTesting
    public Configuration getConfiguration() {
        return this.mConfiguration;
    }

    public final void verifySetupWizardCompleted() {
        Context context = getContext();
        int callingUserId = UserHandle.getCallingUserId();
        if (!setupWizardCompleteForCurrentUser()) {
            this.mSetupWizardComplete = false;
            context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, this.mSetupWizardObserver, callingUserId);
            return;
        }
        this.mSetupWizardComplete = true;
    }

    public final boolean setupWizardCompleteForCurrentUser() {
        return Settings.Secure.getIntForUser(getContext().getContentResolver(), "user_setup_complete", 0, UserHandle.getCallingUserId()) == 1;
    }

    public final void updateCustomTimeLocked() {
        if (this.mNightMode != 3) {
            return;
        }
        if (shouldApplyAutomaticChangesImmediately()) {
            updateLocked(0, 0);
        } else {
            registerScreenOffEventLocked();
        }
        scheduleNextCustomTimeListener();
    }

    public final void updateNightModeFromSettingsLocked(Context context, Resources resources, int i) {
        if (this.mCarModeEnabled || this.mCar || !this.mSetupWizardComplete) {
            return;
        }
        this.mNightMode = Settings.Secure.getIntForUser(context.getContentResolver(), "ui_night_mode", resources.getInteger(17694796), i);
        this.mNightModeCustomType = Settings.Secure.getIntForUser(context.getContentResolver(), "ui_night_mode_custom_type", -1, i);
        this.mOverrideNightModeOn = Settings.Secure.getIntForUser(context.getContentResolver(), "ui_night_mode_override_on", 0, i) != 0;
        this.mOverrideNightModeOff = Settings.Secure.getIntForUser(context.getContentResolver(), "ui_night_mode_override_off", 0, i) != 0;
        this.mCustomAutoNightModeStartMilliseconds = LocalTime.ofNanoOfDay(Settings.Secure.getLongForUser(context.getContentResolver(), "dark_theme_custom_start_time", this.DEFAULT_CUSTOM_NIGHT_START_TIME.toNanoOfDay() / 1000, i) * 1000);
        this.mCustomAutoNightModeEndMilliseconds = LocalTime.ofNanoOfDay(Settings.Secure.getLongForUser(context.getContentResolver(), "dark_theme_custom_end_time", this.DEFAULT_CUSTOM_NIGHT_END_TIME.toNanoOfDay() / 1000, i) * 1000);
        if (this.mNightMode == 0) {
            this.mComputedNightMode = Settings.Secure.getIntForUser(context.getContentResolver(), "ui_night_mode_last_computed", 0, i) != 0;
        }
    }

    public static long toMilliSeconds(LocalTime localTime) {
        return localTime.toNanoOfDay() / 1000;
    }

    public static LocalTime fromMilliseconds(long j) {
        return LocalTime.ofNanoOfDay(j * 1000);
    }

    public final void registerScreenOffEventLocked() {
        if (this.mPowerSave) {
            return;
        }
        this.mWaitForScreenOff = true;
        getContext().registerReceiver(this.mOnScreenOffHandler, new IntentFilter("android.intent.action.SCREEN_OFF"));
    }

    public final void cancelCustomAlarm() {
        this.mAlarmManager.cancel(this.mCustomTimeListener);
    }

    public final void unregisterScreenOffEventLocked() {
        this.mWaitForScreenOff = false;
        try {
            getContext().unregisterReceiver(this.mOnScreenOffHandler);
        } catch (IllegalArgumentException unused) {
        }
    }

    public final void registerTimeChangeEvent() {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        getContext().registerReceiver(this.mOnTimeChangedHandler, intentFilter);
    }

    public final void unregisterTimeChangeEvent() {
        try {
            getContext().unregisterReceiver(this.mOnTimeChangedHandler);
        } catch (IllegalArgumentException unused) {
        }
    }

    /* renamed from: com.android.server.UiModeManagerService$13 */
    /* loaded from: classes.dex */
    public class IUiModeManager$StubC025013 extends IUiModeManager.Stub {
        public IUiModeManager$StubC025013() {
        }

        public void addCallback(IUiModeManagerCallback iUiModeManagerCallback) {
            synchronized (UiModeManagerService.this.mLock) {
                UiModeManagerService.this.mUiModeManagerCallbacks.register(iUiModeManagerCallback);
            }
        }

        public void enableCarMode(int i, int i2, String str) {
            if (isUiModeLocked()) {
                Slog.e(UiModeManagerService.TAG, "enableCarMode while UI mode is locked");
            } else if (i2 != 0 && UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.ENTER_CAR_MODE_PRIORITIZED") != 0) {
                throw new SecurityException("Enabling car mode with a priority requires permission ENTER_CAR_MODE_PRIORITIZED");
            } else {
                if (!(UiModeManagerService.this.mInjector.getCallingUid() == 2000)) {
                    UiModeManagerService.this.assertLegit(str);
                }
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    synchronized (UiModeManagerService.this.mLock) {
                        UiModeManagerService.this.setCarModeLocked(true, i, i2, str);
                        UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                        if (uiModeManagerService.mSystemReady) {
                            uiModeManagerService.updateLocked(i, 0);
                        }
                    }
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        public void disableCarMode(int i) {
            disableCarModeByCallingPackage(i, null);
        }

        public void disableCarModeByCallingPackage(int i, final String str) {
            if (isUiModeLocked()) {
                Slog.e(UiModeManagerService.TAG, "disableCarMode while UI mode is locked");
                return;
            }
            int callingUid = UiModeManagerService.this.mInjector.getCallingUid();
            boolean z = callingUid == 1000;
            boolean z2 = callingUid == 2000;
            if (!z && !z2) {
                UiModeManagerService.this.assertLegit(str);
            }
            int i2 = z ? i : i & (-3);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService.this.setCarModeLocked(false, i2, ((Integer) UiModeManagerService.this.mCarModePackagePriority.entrySet().stream().filter(new Predicate() { // from class: com.android.server.UiModeManagerService$13$$ExternalSyntheticLambda0
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            boolean lambda$disableCarModeByCallingPackage$0;
                            lambda$disableCarModeByCallingPackage$0 = UiModeManagerService.IUiModeManager$StubC025013.lambda$disableCarModeByCallingPackage$0(str, (Map.Entry) obj);
                            return lambda$disableCarModeByCallingPackage$0;
                        }
                    }).findFirst().map(new UiModeManagerService$13$$ExternalSyntheticLambda1()).orElse(0)).intValue(), str);
                    UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                    if (uiModeManagerService.mSystemReady) {
                        uiModeManagerService.updateLocked(0, i);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public static /* synthetic */ boolean lambda$disableCarModeByCallingPackage$0(String str, Map.Entry entry) {
            return ((String) entry.getValue()).equals(str);
        }

        public int getCurrentModeType() {
            int i;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (UiModeManagerService.this.mLock) {
                    i = UiModeManagerService.this.mCurUiMode & 15;
                }
                return i;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setNightMode(int i) {
            setNightModeInternal(i, i == 3 ? 0 : -1);
        }

        public final void setNightModeInternal(int i, int i2) {
            if (isNightModeLocked() && UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.MODIFY_DAY_NIGHT_MODE") != 0) {
                Slog.e(UiModeManagerService.TAG, "Night mode locked, requires MODIFY_DAY_NIGHT_MODE permission");
                return;
            }
            if (i != 0 && i != 1 && i != 2) {
                if (i == 3) {
                    if (!UiModeManagerService.SUPPORTED_NIGHT_MODE_CUSTOM_TYPES.contains(Integer.valueOf(i2))) {
                        throw new IllegalArgumentException("Can't set the custom type to " + i2);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown mode: " + i);
                }
            }
            int callingUserId = UserHandle.getCallingUserId();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (UiModeManagerService.this.mLock) {
                    if (UiModeManagerService.this.mNightMode != i || UiModeManagerService.this.mNightModeCustomType != i2) {
                        if (UiModeManagerService.this.mNightMode == 0 || UiModeManagerService.this.mNightMode == 3) {
                            UiModeManagerService.this.unregisterScreenOffEventLocked();
                            UiModeManagerService.this.cancelCustomAlarm();
                        }
                        UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                        if (i != 3) {
                            i2 = -1;
                        }
                        uiModeManagerService.mNightModeCustomType = i2;
                        UiModeManagerService.this.mNightMode = i;
                        UiModeManagerService.this.resetNightModeOverrideLocked();
                        UiModeManagerService.this.persistNightMode(callingUserId);
                        if ((UiModeManagerService.this.mNightMode != 0 && UiModeManagerService.this.mNightMode != 3) || UiModeManagerService.this.shouldApplyAutomaticChangesImmediately()) {
                            UiModeManagerService.this.unregisterScreenOffEventLocked();
                            UiModeManagerService.this.updateLocked(0, 0);
                        } else {
                            UiModeManagerService.this.registerScreenOffEventLocked();
                        }
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int getNightMode() {
            int i;
            synchronized (UiModeManagerService.this.mLock) {
                i = UiModeManagerService.this.mNightMode;
            }
            return i;
        }

        public void setNightModeCustomType(int i) {
            if (UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.MODIFY_DAY_NIGHT_MODE") != 0) {
                throw new SecurityException("setNightModeCustomType requires MODIFY_DAY_NIGHT_MODE permission");
            }
            setNightModeInternal(3, i);
        }

        public int getNightModeCustomType() {
            int i;
            if (UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.MODIFY_DAY_NIGHT_MODE") != 0) {
                throw new SecurityException("getNightModeCustomType requires MODIFY_DAY_NIGHT_MODE permission");
            }
            synchronized (UiModeManagerService.this.mLock) {
                i = UiModeManagerService.this.mNightModeCustomType;
            }
            return i;
        }

        public void setApplicationNightMode(int i) {
            if (i != 0 && i != 1 && i != 2 && i != 3) {
                throw new IllegalArgumentException("Unknown mode: " + i);
            }
            int i2 = i != 1 ? i != 2 ? 0 : 32 : 16;
            ActivityTaskManagerInternal.PackageConfigurationUpdater createPackageConfigurationUpdater = UiModeManagerService.this.mActivityTaskManager.createPackageConfigurationUpdater();
            createPackageConfigurationUpdater.setNightMode(i2);
            createPackageConfigurationUpdater.commit();
        }

        public boolean isUiModeLocked() {
            boolean z;
            synchronized (UiModeManagerService.this.mLock) {
                z = UiModeManagerService.this.mUiModeLocked;
            }
            return z;
        }

        public boolean isNightModeLocked() {
            boolean z;
            synchronized (UiModeManagerService.this.mLock) {
                z = UiModeManagerService.this.mNightModeLocked;
            }
            return z;
        }

        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new Shell(UiModeManagerService.this.mService).exec(UiModeManagerService.this.mService, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(UiModeManagerService.this.getContext(), UiModeManagerService.TAG, printWriter)) {
                UiModeManagerService.this.dumpImpl(printWriter);
            }
        }

        public boolean setNightModeActivatedForCustomMode(int i, boolean z) {
            return setNightModeActivatedForModeInternal(i, z);
        }

        public boolean setNightModeActivated(boolean z) {
            return setNightModeActivatedForModeInternal(UiModeManagerService.this.mNightModeCustomType, z);
        }

        public final boolean setNightModeActivatedForModeInternal(int i, boolean z) {
            if (UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.MODIFY_DAY_NIGHT_MODE") != 0) {
                Slog.e(UiModeManagerService.TAG, "Night mode locked, requires MODIFY_DAY_NIGHT_MODE permission");
                return false;
            } else if (Binder.getCallingUserHandle().getIdentifier() != UiModeManagerService.this.mCurrentUser && UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") != 0) {
                Slog.e(UiModeManagerService.TAG, "Target user is not current user, INTERACT_ACROSS_USERS permission is required");
                return false;
            } else {
                if (i == 1) {
                    UiModeManagerService.this.mLastBedtimeRequestedNightMode = z;
                }
                if (i != UiModeManagerService.this.mNightModeCustomType) {
                    return false;
                }
                synchronized (UiModeManagerService.this.mLock) {
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    if (UiModeManagerService.this.mNightMode != 0 && UiModeManagerService.this.mNightMode != 3) {
                        if (UiModeManagerService.this.mNightMode == 1 && z) {
                            UiModeManagerService.this.mNightMode = 2;
                        } else if (UiModeManagerService.this.mNightMode == 2 && !z) {
                            UiModeManagerService.this.mNightMode = 1;
                        }
                        UiModeManagerService.this.updateConfigurationLocked();
                        UiModeManagerService.this.applyConfigurationExternallyLocked();
                        UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                        uiModeManagerService.persistNightMode(uiModeManagerService.mCurrentUser);
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                    UiModeManagerService.this.unregisterScreenOffEventLocked();
                    UiModeManagerService.this.mOverrideNightModeOff = z ? false : true;
                    UiModeManagerService.this.mOverrideNightModeOn = z;
                    UiModeManagerService uiModeManagerService2 = UiModeManagerService.this;
                    uiModeManagerService2.mOverrideNightModeUser = uiModeManagerService2.mCurrentUser;
                    UiModeManagerService uiModeManagerService3 = UiModeManagerService.this;
                    uiModeManagerService3.persistNightModeOverrides(uiModeManagerService3.mCurrentUser);
                    UiModeManagerService.this.updateConfigurationLocked();
                    UiModeManagerService.this.applyConfigurationExternallyLocked();
                    UiModeManagerService uiModeManagerService4 = UiModeManagerService.this;
                    uiModeManagerService4.persistNightMode(uiModeManagerService4.mCurrentUser);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
                return true;
            }
        }

        public long getCustomNightModeStart() {
            return UiModeManagerService.this.mCustomAutoNightModeStartMilliseconds.toNanoOfDay() / 1000;
        }

        public void setCustomNightModeStart(long j) {
            LocalTime ofNanoOfDay;
            if (isNightModeLocked() && UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.MODIFY_DAY_NIGHT_MODE") != 0) {
                Slog.e(UiModeManagerService.TAG, "Set custom time start, requires MODIFY_DAY_NIGHT_MODE permission");
                return;
            }
            int callingUserId = UserHandle.getCallingUserId();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    ofNanoOfDay = LocalTime.ofNanoOfDay(j * 1000);
                } catch (DateTimeException unused) {
                    UiModeManagerService.this.unregisterScreenOffEventLocked();
                }
                if (ofNanoOfDay == null) {
                    return;
                }
                UiModeManagerService.this.mCustomAutoNightModeStartMilliseconds = ofNanoOfDay;
                UiModeManagerService.this.persistNightMode(callingUserId);
                UiModeManagerService.this.onCustomTimeUpdated(callingUserId);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public long getCustomNightModeEnd() {
            return UiModeManagerService.this.mCustomAutoNightModeEndMilliseconds.toNanoOfDay() / 1000;
        }

        public void setCustomNightModeEnd(long j) {
            LocalTime ofNanoOfDay;
            if (isNightModeLocked() && UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.MODIFY_DAY_NIGHT_MODE") != 0) {
                Slog.e(UiModeManagerService.TAG, "Set custom time end, requires MODIFY_DAY_NIGHT_MODE permission");
                return;
            }
            int callingUserId = UserHandle.getCallingUserId();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                try {
                    ofNanoOfDay = LocalTime.ofNanoOfDay(j * 1000);
                } catch (DateTimeException unused) {
                    UiModeManagerService.this.unregisterScreenOffEventLocked();
                }
                if (ofNanoOfDay == null) {
                    return;
                }
                UiModeManagerService.this.mCustomAutoNightModeEndMilliseconds = ofNanoOfDay;
                UiModeManagerService.this.onCustomTimeUpdated(callingUserId);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean requestProjection(IBinder iBinder, int i, String str) {
            UiModeManagerService.this.assertLegit(str);
            UiModeManagerService.assertSingleProjectionType(i);
            UiModeManagerService.this.enforceProjectionTypePermissions(i);
            synchronized (UiModeManagerService.this.mLock) {
                if (UiModeManagerService.this.mProjectionHolders == null) {
                    UiModeManagerService.this.mProjectionHolders = new SparseArray(1);
                }
                if (!UiModeManagerService.this.mProjectionHolders.contains(i)) {
                    UiModeManagerService.this.mProjectionHolders.put(i, new ArrayList(1));
                }
                List list = (List) UiModeManagerService.this.mProjectionHolders.get(i);
                for (int i2 = 0; i2 < list.size(); i2++) {
                    if (str.equals(((ProjectionHolder) list.get(i2)).mPackageName)) {
                        return true;
                    }
                }
                if (i != 1 || list.isEmpty()) {
                    final UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                    ProjectionHolder projectionHolder = new ProjectionHolder(str, i, iBinder, new ProjectionHolder.ProjectionReleaser() { // from class: com.android.server.UiModeManagerService$13$$ExternalSyntheticLambda2
                        @Override // com.android.server.UiModeManagerService.ProjectionHolder.ProjectionReleaser
                        public final boolean release(int i3, String str2) {
                            boolean releaseProjectionUnchecked;
                            releaseProjectionUnchecked = UiModeManagerService.this.releaseProjectionUnchecked(i3, str2);
                            return releaseProjectionUnchecked;
                        }
                    });
                    if (projectionHolder.linkToDeath()) {
                        list.add(projectionHolder);
                        Slog.d(UiModeManagerService.TAG, "Package " + str + " set projection type " + i + ".");
                        UiModeManagerService.this.onProjectionStateChangedLocked(i);
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }

        public boolean releaseProjection(int i, String str) {
            UiModeManagerService.this.assertLegit(str);
            UiModeManagerService.assertSingleProjectionType(i);
            UiModeManagerService.this.enforceProjectionTypePermissions(i);
            return UiModeManagerService.this.releaseProjectionUnchecked(i, str);
        }

        public int getActiveProjectionTypes() {
            int i;
            UiModeManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.READ_PROJECTION_STATE", "getActiveProjectionTypes");
            synchronized (UiModeManagerService.this.mLock) {
                i = 0;
                if (UiModeManagerService.this.mProjectionHolders != null) {
                    int i2 = 0;
                    while (i < UiModeManagerService.this.mProjectionHolders.size()) {
                        if (!((List) UiModeManagerService.this.mProjectionHolders.valueAt(i)).isEmpty()) {
                            i2 |= UiModeManagerService.this.mProjectionHolders.keyAt(i);
                        }
                        i++;
                    }
                    i = i2;
                }
            }
            return i;
        }

        public List<String> getProjectingPackages(int i) {
            ArrayList arrayList;
            UiModeManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.READ_PROJECTION_STATE", "getProjectionState");
            synchronized (UiModeManagerService.this.mLock) {
                arrayList = new ArrayList();
                UiModeManagerService.this.populateWithRelevantActivePackageNames(i, arrayList);
            }
            return arrayList;
        }

        public void addOnProjectionStateChangedListener(IOnProjectionStateChangedListener iOnProjectionStateChangedListener, int i) {
            UiModeManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.READ_PROJECTION_STATE", "addOnProjectionStateChangedListener");
            if (i == 0) {
                return;
            }
            synchronized (UiModeManagerService.this.mLock) {
                if (UiModeManagerService.this.mProjectionListeners == null) {
                    UiModeManagerService.this.mProjectionListeners = new SparseArray(1);
                }
                if (!UiModeManagerService.this.mProjectionListeners.contains(i)) {
                    UiModeManagerService.this.mProjectionListeners.put(i, new RemoteCallbackList());
                }
                if (((RemoteCallbackList) UiModeManagerService.this.mProjectionListeners.get(i)).register(iOnProjectionStateChangedListener)) {
                    ArrayList arrayList = new ArrayList();
                    int populateWithRelevantActivePackageNames = UiModeManagerService.this.populateWithRelevantActivePackageNames(i, arrayList);
                    if (!arrayList.isEmpty()) {
                        try {
                            iOnProjectionStateChangedListener.onProjectionStateChanged(populateWithRelevantActivePackageNames, arrayList);
                        } catch (RemoteException unused) {
                            Slog.w(UiModeManagerService.TAG, "Failed a call to onProjectionStateChanged() during listener registration.");
                        }
                    }
                }
            }
        }

        public void removeOnProjectionStateChangedListener(IOnProjectionStateChangedListener iOnProjectionStateChangedListener) {
            UiModeManagerService.this.getContext().enforceCallingOrSelfPermission("android.permission.READ_PROJECTION_STATE", "removeOnProjectionStateChangedListener");
            synchronized (UiModeManagerService.this.mLock) {
                if (UiModeManagerService.this.mProjectionListeners != null) {
                    for (int i = 0; i < UiModeManagerService.this.mProjectionListeners.size(); i++) {
                        ((RemoteCallbackList) UiModeManagerService.this.mProjectionListeners.valueAt(i)).unregister(iOnProjectionStateChangedListener);
                    }
                }
            }
        }

        public float getContrast() {
            float contrastLocked;
            synchronized (UiModeManagerService.this.mLock) {
                contrastLocked = UiModeManagerService.this.getContrastLocked();
            }
            return contrastLocked;
        }
    }

    public final void enforceProjectionTypePermissions(int i) {
        if ((i & 1) != 0) {
            getContext().enforceCallingPermission("android.permission.TOGGLE_AUTOMOTIVE_PROJECTION", "toggleProjection");
        }
    }

    public static void assertSingleProjectionType(int i) {
        boolean z = ((i + (-1)) & i) == 0;
        if (i == 0 || !z) {
            throw new IllegalArgumentException("Must specify exactly one projection type.");
        }
    }

    public static List<String> toPackageNameList(Collection<ProjectionHolder> collection) {
        ArrayList arrayList = new ArrayList();
        for (ProjectionHolder projectionHolder : collection) {
            arrayList.add(projectionHolder.mPackageName);
        }
        return arrayList;
    }

    @GuardedBy({"mLock"})
    public final int populateWithRelevantActivePackageNames(int i, List<String> list) {
        list.clear();
        if (this.mProjectionHolders != null) {
            int i2 = 0;
            for (int i3 = 0; i3 < this.mProjectionHolders.size(); i3++) {
                int keyAt = this.mProjectionHolders.keyAt(i3);
                List<ProjectionHolder> valueAt = this.mProjectionHolders.valueAt(i3);
                if ((i & keyAt) != 0 && list.addAll(toPackageNameList(valueAt))) {
                    i2 |= keyAt;
                }
            }
            return i2;
        }
        return 0;
    }

    public final boolean releaseProjectionUnchecked(int i, String str) {
        boolean z;
        List<ProjectionHolder> list;
        synchronized (this.mLock) {
            SparseArray<List<ProjectionHolder>> sparseArray = this.mProjectionHolders;
            z = false;
            if (sparseArray != null && (list = sparseArray.get(i)) != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    ProjectionHolder projectionHolder = list.get(size);
                    if (str.equals(projectionHolder.mPackageName)) {
                        projectionHolder.unlinkToDeath();
                        Slog.d(TAG, "Projection type " + i + " released by " + str + ".");
                        list.remove(size);
                        z = true;
                    }
                }
            }
            if (z) {
                onProjectionStateChangedLocked(i);
            } else {
                Slog.w(TAG, str + " tried to release projection type " + i + " but was not set by that package.");
            }
        }
        return z;
    }

    @GuardedBy({"mLock"})
    public final float getContrastLocked() {
        if (!this.mContrasts.contains(this.mCurrentUser)) {
            updateContrastLocked();
        }
        return this.mContrasts.get(this.mCurrentUser).floatValue();
    }

    @GuardedBy({"mLock"})
    public final boolean updateContrastLocked() {
        float floatForUser = Settings.Secure.getFloatForUser(getContext().getContentResolver(), "contrast_level", 0.0f, this.mCurrentUser);
        if (Math.abs(this.mContrasts.get(this.mCurrentUser, Float.valueOf(Float.MAX_VALUE)).floatValue() - floatForUser) >= 1.0E-10d) {
            this.mContrasts.put(this.mCurrentUser, Float.valueOf(floatForUser));
            return true;
        }
        return false;
    }

    /* loaded from: classes.dex */
    public static class ProjectionHolder implements IBinder.DeathRecipient {
        public final IBinder mBinder;
        public final String mPackageName;
        public final ProjectionReleaser mProjectionReleaser;
        public final int mProjectionType;

        /* loaded from: classes.dex */
        public interface ProjectionReleaser {
            boolean release(int i, String str);
        }

        public ProjectionHolder(String str, int i, IBinder iBinder, ProjectionReleaser projectionReleaser) {
            this.mPackageName = str;
            this.mProjectionType = i;
            this.mBinder = iBinder;
            this.mProjectionReleaser = projectionReleaser;
        }

        public final boolean linkToDeath() {
            try {
                this.mBinder.linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                String str = UiModeManagerService.TAG;
                Slog.e(str, "linkToDeath failed for projection requester: " + this.mPackageName + ".", e);
                return false;
            }
        }

        public final void unlinkToDeath() {
            this.mBinder.unlinkToDeath(this, 0);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            String str = UiModeManagerService.TAG;
            Slog.w(str, "Projection holder " + this.mPackageName + " died. Releasing projection type " + this.mProjectionType + ".");
            this.mProjectionReleaser.release(this.mProjectionType, this.mPackageName);
        }
    }

    public final void assertLegit(String str) {
        if (doesPackageHaveCallingUid(str)) {
            return;
        }
        throw new SecurityException("Caller claimed bogus packageName: " + str + ".");
    }

    public final boolean doesPackageHaveCallingUid(String str) {
        int callingUid = this.mInjector.getCallingUid();
        int userId = UserHandle.getUserId(callingUid);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getContext().getPackageManager().getPackageUidAsUser(str, userId) == callingUid;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @GuardedBy({"mLock"})
    public final void onProjectionStateChangedLocked(int i) {
        if (this.mProjectionListeners == null) {
            return;
        }
        for (int i2 = 0; i2 < this.mProjectionListeners.size(); i2++) {
            int keyAt = this.mProjectionListeners.keyAt(i2);
            if ((i & keyAt) != 0) {
                RemoteCallbackList<IOnProjectionStateChangedListener> valueAt = this.mProjectionListeners.valueAt(i2);
                ArrayList arrayList = new ArrayList();
                int populateWithRelevantActivePackageNames = populateWithRelevantActivePackageNames(keyAt, arrayList);
                int beginBroadcast = valueAt.beginBroadcast();
                for (int i3 = 0; i3 < beginBroadcast; i3++) {
                    try {
                        valueAt.getBroadcastItem(i3).onProjectionStateChanged(populateWithRelevantActivePackageNames, arrayList);
                    } catch (RemoteException unused) {
                        Slog.w(TAG, "Failed a call to onProjectionStateChanged().");
                    }
                }
                valueAt.finishBroadcast();
            }
        }
    }

    public final void onCustomTimeUpdated(int i) {
        persistNightMode(i);
        if (this.mNightMode != 3) {
            return;
        }
        if (shouldApplyAutomaticChangesImmediately()) {
            unregisterScreenOffEventLocked();
            updateLocked(0, 0);
            return;
        }
        registerScreenOffEventLocked();
    }

    public void dumpImpl(PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.println("Current UI Mode Service state:");
            printWriter.print("  mDockState=");
            printWriter.print(this.mDockState);
            printWriter.print(" mLastBroadcastState=");
            printWriter.println(this.mLastBroadcastState);
            printWriter.print(" mStartDreamImmediatelyOnDock=");
            printWriter.print(this.mStartDreamImmediatelyOnDock);
            printWriter.print("  mNightMode=");
            printWriter.print(this.mNightMode);
            printWriter.print(" (");
            printWriter.print(Shell.nightModeToStr(this.mNightMode, this.mNightModeCustomType));
            printWriter.print(") ");
            printWriter.print(" mOverrideOn/Off=");
            printWriter.print(this.mOverrideNightModeOn);
            printWriter.print("/");
            printWriter.print(this.mOverrideNightModeOff);
            printWriter.print(" mNightModeLocked=");
            printWriter.println(this.mNightModeLocked);
            printWriter.print("  mCarModeEnabled=");
            printWriter.print(this.mCarModeEnabled);
            printWriter.print(" (carModeApps=");
            for (Map.Entry<Integer, String> entry : this.mCarModePackagePriority.entrySet()) {
                printWriter.print(entry.getKey());
                printWriter.print(XmlUtils.STRING_ARRAY_SEPARATOR);
                printWriter.print(entry.getValue());
                printWriter.print(" ");
            }
            printWriter.println("");
            printWriter.print(" waitScreenOff=");
            printWriter.print(this.mWaitForScreenOff);
            printWriter.print(" mComputedNightMode=");
            printWriter.print(this.mComputedNightMode);
            printWriter.print(" customStart=");
            printWriter.print(this.mCustomAutoNightModeStartMilliseconds);
            printWriter.print(" customEnd");
            printWriter.print(this.mCustomAutoNightModeEndMilliseconds);
            printWriter.print(" mCarModeEnableFlags=");
            printWriter.print(this.mCarModeEnableFlags);
            printWriter.print(" mEnableCarDockLaunch=");
            printWriter.println(this.mEnableCarDockLaunch);
            printWriter.print("  mCurUiMode=0x");
            printWriter.print(Integer.toHexString(this.mCurUiMode));
            printWriter.print(" mUiModeLocked=");
            printWriter.print(this.mUiModeLocked);
            printWriter.print(" mSetUiMode=0x");
            printWriter.println(Integer.toHexString(this.mSetUiMode));
            printWriter.print("  mHoldingConfiguration=");
            printWriter.print(this.mHoldingConfiguration);
            printWriter.print(" mSystemReady=");
            printWriter.println(this.mSystemReady);
            if (this.mTwilightManager != null) {
                printWriter.print("  mTwilightService.getLastTwilightState()=");
                printWriter.println(this.mTwilightManager.getLastTwilightState());
            }
        }
    }

    public void setCarModeLocked(boolean z, int i, int i2, String str) {
        if (z) {
            enableCarMode(i2, str);
        } else {
            disableCarMode(i, i2, str);
        }
        boolean isCarModeEnabled = isCarModeEnabled();
        if (this.mCarModeEnabled != isCarModeEnabled) {
            this.mCarModeEnabled = isCarModeEnabled;
            if (!isCarModeEnabled) {
                Context context = getContext();
                updateNightModeFromSettingsLocked(context, context.getResources(), UserHandle.getCallingUserId());
            }
        }
        this.mCarModeEnableFlags = i;
    }

    public final void disableCarMode(int i, int i2, String str) {
        boolean z = true;
        boolean z2 = (i & 2) != 0;
        boolean contains = this.mCarModePackagePriority.keySet().contains(Integer.valueOf(i2));
        if (!(i2 == 0) && ((!contains || !this.mCarModePackagePriority.get(Integer.valueOf(i2)).equals(str)) && !z2)) {
            z = false;
        }
        if (z) {
            Slog.d(TAG, "disableCarMode: disabling, priority=" + i2 + ", packageName=" + str);
            if (z2) {
                ArraySet<Map.Entry> arraySet = new ArraySet(this.mCarModePackagePriority.entrySet());
                this.mCarModePackagePriority.clear();
                for (Map.Entry entry : arraySet) {
                    notifyCarModeDisabled(((Integer) entry.getKey()).intValue(), (String) entry.getValue());
                }
                return;
            }
            this.mCarModePackagePriority.remove(Integer.valueOf(i2));
            notifyCarModeDisabled(i2, str);
        }
    }

    public final void enableCarMode(int i, String str) {
        boolean containsKey = this.mCarModePackagePriority.containsKey(Integer.valueOf(i));
        boolean containsValue = this.mCarModePackagePriority.containsValue(str);
        if (!containsKey && !containsValue) {
            String str2 = TAG;
            Slog.d(str2, "enableCarMode: enabled at priority=" + i + ", packageName=" + str);
            this.mCarModePackagePriority.put(Integer.valueOf(i), str);
            notifyCarModeEnabled(i, str);
            return;
        }
        String str3 = TAG;
        Slog.d(str3, "enableCarMode: car mode at priority " + i + " already enabled.");
    }

    public final void notifyCarModeEnabled(int i, String str) {
        Intent intent = new Intent("android.app.action.ENTER_CAR_MODE_PRIORITIZED");
        intent.putExtra("android.app.extra.CALLING_PACKAGE", str);
        intent.putExtra("android.app.extra.PRIORITY", i);
        getContext().sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.HANDLE_CAR_MODE_CHANGES");
    }

    public final void notifyCarModeDisabled(int i, String str) {
        Intent intent = new Intent("android.app.action.EXIT_CAR_MODE_PRIORITIZED");
        intent.putExtra("android.app.extra.CALLING_PACKAGE", str);
        intent.putExtra("android.app.extra.PRIORITY", i);
        getContext().sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.HANDLE_CAR_MODE_CHANGES");
    }

    public final boolean isCarModeEnabled() {
        return this.mCarModePackagePriority.size() > 0;
    }

    public final void updateDockState(int i) {
        synchronized (this.mLock) {
            if (i != this.mDockState) {
                this.mDockState = i;
                setCarModeLocked(i == 2, 0, 0, "");
                if (this.mSystemReady) {
                    updateLocked(1, 0);
                }
            }
        }
    }

    public final void persistNightMode(int i) {
        if (this.mCarModeEnabled || this.mCar) {
            return;
        }
        Settings.Secure.putIntForUser(getContext().getContentResolver(), "ui_night_mode", this.mNightMode, i);
        Settings.Secure.putLongForUser(getContext().getContentResolver(), "ui_night_mode_custom_type", this.mNightModeCustomType, i);
        Settings.Secure.putLongForUser(getContext().getContentResolver(), "dark_theme_custom_start_time", this.mCustomAutoNightModeStartMilliseconds.toNanoOfDay() / 1000, i);
        Settings.Secure.putLongForUser(getContext().getContentResolver(), "dark_theme_custom_end_time", this.mCustomAutoNightModeEndMilliseconds.toNanoOfDay() / 1000, i);
    }

    public final void persistNightModeOverrides(int i) {
        if (this.mCarModeEnabled || this.mCar) {
            return;
        }
        Settings.Secure.putIntForUser(getContext().getContentResolver(), "ui_night_mode_override_on", this.mOverrideNightModeOn ? 1 : 0, i);
        Settings.Secure.putIntForUser(getContext().getContentResolver(), "ui_night_mode_override_off", this.mOverrideNightModeOff ? 1 : 0, i);
    }

    public final void updateConfigurationLocked() {
        int i = this.mDefaultUiModeType;
        if (!this.mUiModeLocked) {
            if (this.mTelevision) {
                i = 4;
            } else if (this.mWatch) {
                i = 6;
            } else if (this.mCarModeEnabled) {
                i = 3;
            } else if (isDeskDockState(this.mDockState)) {
                i = 2;
            } else if (this.mVrHeadset) {
                i = 7;
            }
        }
        int i2 = this.mNightMode;
        if (i2 == 2 || i2 == 1) {
            updateComputedNightModeLocked(i2 == 2);
        }
        if (this.mNightMode == 0) {
            boolean z = this.mComputedNightMode;
            TwilightManager twilightManager = this.mTwilightManager;
            if (twilightManager != null) {
                twilightManager.registerListener(this.mTwilightListener, this.mHandler);
                TwilightState lastTwilightState = this.mTwilightManager.getLastTwilightState();
                z = lastTwilightState == null ? this.mComputedNightMode : lastTwilightState.isNight();
            }
            updateComputedNightModeLocked(z);
        } else {
            TwilightManager twilightManager2 = this.mTwilightManager;
            if (twilightManager2 != null) {
                twilightManager2.unregisterListener(this.mTwilightListener);
            }
        }
        if (this.mNightMode == 3) {
            if (this.mNightModeCustomType == 1) {
                updateComputedNightModeLocked(this.mLastBedtimeRequestedNightMode);
            } else {
                registerTimeChangeEvent();
                updateComputedNightModeLocked(computeCustomNightMode());
                scheduleNextCustomTimeListener();
            }
        } else {
            unregisterTimeChangeEvent();
        }
        int computedUiModeConfiguration = (!this.mPowerSave || this.mCarModeEnabled || this.mCar) ? getComputedUiModeConfiguration(i) : (i & (-17)) | 32;
        this.mCurUiMode = computedUiModeConfiguration;
        if (this.mHoldingConfiguration) {
            return;
        }
        if (!this.mWaitForScreenOff || this.mPowerSave) {
            this.mConfiguration.uiMode = computedUiModeConfiguration;
        }
    }

    public final int getComputedUiModeConfiguration(int i) {
        boolean z = this.mComputedNightMode;
        return (z ? -17 : -33) & (i | (z ? 32 : 16));
    }

    public final boolean computeCustomNightMode() {
        return TimeUtils.isTimeBetween(LocalTime.now(), this.mCustomAutoNightModeStartMilliseconds, this.mCustomAutoNightModeEndMilliseconds);
    }

    public final void applyConfigurationExternallyLocked() {
        int i = this.mSetUiMode;
        int i2 = this.mConfiguration.uiMode;
        if (i != i2) {
            this.mSetUiMode = i2;
            this.mWindowManager.clearSnapshotCache();
            try {
                ActivityTaskManager.getService().updateConfiguration(this.mConfiguration);
            } catch (RemoteException e) {
                Slog.w(TAG, "Failure communicating with activity manager", e);
            } catch (SecurityException e2) {
                Slog.e(TAG, "Activity does not have the ", e2);
            }
        }
    }

    public final boolean shouldApplyAutomaticChangesImmediately() {
        return this.mCar || !this.mPowerManager.isInteractive() || this.mNightModeCustomType == 1;
    }

    public final void scheduleNextCustomTimeListener() {
        LocalDateTime dateTimeAfter;
        cancelCustomAlarm();
        LocalDateTime now = LocalDateTime.now();
        if (computeCustomNightMode()) {
            dateTimeAfter = getDateTimeAfter(this.mCustomAutoNightModeEndMilliseconds, now);
        } else {
            dateTimeAfter = getDateTimeAfter(this.mCustomAutoNightModeStartMilliseconds, now);
        }
        this.mAlarmManager.setExact(1, dateTimeAfter.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), TAG, this.mCustomTimeListener, null);
    }

    public final LocalDateTime getDateTimeAfter(LocalTime localTime, LocalDateTime localDateTime) {
        LocalDateTime of = LocalDateTime.of(localDateTime.toLocalDate(), localTime);
        return of.isBefore(localDateTime) ? of.plusDays(1L) : of;
    }

    public void updateLocked(int i, int i2) {
        String str;
        int i3 = this.mLastBroadcastState;
        String str2 = null;
        if (i3 == 2) {
            adjustStatusBarCarModeLocked();
            str = UiModeManager.ACTION_EXIT_CAR_MODE;
        } else {
            str = isDeskDockState(i3) ? UiModeManager.ACTION_EXIT_DESK_MODE : null;
        }
        boolean z = false;
        if (this.mCarModeEnabled) {
            if (this.mLastBroadcastState != 2) {
                adjustStatusBarCarModeLocked();
                if (str != null) {
                    sendForegroundBroadcastToAllUsers(str);
                }
                this.mLastBroadcastState = 2;
                str = UiModeManager.ACTION_ENTER_CAR_MODE;
            }
            str = null;
        } else if (isDeskDockState(this.mDockState)) {
            if (!isDeskDockState(this.mLastBroadcastState)) {
                if (str != null) {
                    sendForegroundBroadcastToAllUsers(str);
                }
                this.mLastBroadcastState = this.mDockState;
                str = UiModeManager.ACTION_ENTER_DESK_MODE;
            }
            str = null;
        } else {
            this.mLastBroadcastState = 0;
        }
        if (str != null) {
            Intent intent = new Intent(str);
            intent.putExtra("enableFlags", i);
            intent.putExtra("disableFlags", i2);
            intent.addFlags(268435456);
            getContext().sendOrderedBroadcastAsUser(intent, UserHandle.CURRENT, null, this.mResultReceiver, null, -1, null, null);
            this.mHoldingConfiguration = true;
            updateConfigurationLocked();
        } else {
            if (this.mCarModeEnabled) {
                if (this.mEnableCarDockLaunch && (i & 1) != 0) {
                    str2 = "android.intent.category.CAR_DOCK";
                }
            } else if (isDeskDockState(this.mDockState)) {
                if ((i & 1) != 0) {
                    str2 = "android.intent.category.DESK_DOCK";
                }
            } else if ((i2 & 1) != 0) {
                str2 = "android.intent.category.HOME";
            }
            sendConfigurationAndStartDreamOrDockAppLocked(str2);
        }
        if (this.mCharging && ((this.mCarModeEnabled && this.mCarModeKeepsScreenOn && (this.mCarModeEnableFlags & 2) == 0) || (this.mCurUiMode == 2 && this.mDeskModeKeepsScreenOn))) {
            z = true;
        }
        if (z != this.mWakeLock.isHeld()) {
            if (z) {
                this.mWakeLock.acquire();
            } else {
                this.mWakeLock.release();
            }
        }
    }

    public final void sendForegroundBroadcastToAllUsers(String str) {
        getContext().sendBroadcastAsUser(new Intent(str).addFlags(268435456), UserHandle.ALL);
    }

    public final void updateAfterBroadcastLocked(String str, int i, int i2) {
        String str2;
        if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(str)) {
            if (this.mEnableCarDockLaunch && (i & 1) != 0) {
                str2 = "android.intent.category.CAR_DOCK";
            }
            str2 = null;
        } else if (UiModeManager.ACTION_ENTER_DESK_MODE.equals(str)) {
            if ((i & 1) != 0) {
                str2 = "android.intent.category.DESK_DOCK";
            }
            str2 = null;
        } else {
            if ((i2 & 1) != 0) {
                str2 = "android.intent.category.HOME";
            }
            str2 = null;
        }
        sendConfigurationAndStartDreamOrDockAppLocked(str2);
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0098 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:42:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void sendConfigurationAndStartDreamOrDockAppLocked(String str) {
        boolean z;
        Intent intent;
        int startActivityWithConfig;
        this.mHoldingConfiguration = false;
        updateConfigurationLocked();
        if (str != null) {
            Intent buildHomeIntent = buildHomeIntent(str);
            if (Sandman.shouldStartDockApp(getContext(), buildHomeIntent)) {
                try {
                    intent = buildHomeIntent;
                } catch (RemoteException e) {
                    e = e;
                    intent = buildHomeIntent;
                }
                try {
                    startActivityWithConfig = ActivityTaskManager.getService().startActivityWithConfig((IApplicationThread) null, getContext().getBasePackageName(), getContext().getAttributionTag(), buildHomeIntent, (String) null, (IBinder) null, (String) null, 0, 0, this.mConfiguration, (Bundle) null, -2);
                } catch (RemoteException e2) {
                    e = e2;
                    Slog.e(TAG, "Could not start dock app: " + intent, e);
                    z = false;
                    applyConfigurationExternallyLocked();
                    if (this.mDreamsDisabledByAmbientModeSuppression) {
                    }
                    if (str == null) {
                    }
                }
                if (ActivityManager.isStartResultSuccessful(startActivityWithConfig)) {
                    z = true;
                    applyConfigurationExternallyLocked();
                    boolean z2 = !this.mDreamsDisabledByAmbientModeSuppression && this.mLocalPowerManager.isAmbientDisplaySuppressed();
                    if (str == null || z || z2) {
                        return;
                    }
                    if (this.mStartDreamImmediatelyOnDock || this.mWindowManager.isKeyguardShowingAndNotOccluded() || !this.mPowerManager.isInteractive()) {
                        this.mInjector.startDreamWhenDockedIfAppropriate(getContext());
                        return;
                    }
                    return;
                } else if (startActivityWithConfig != -91) {
                    Slog.e(TAG, "Could not start dock app: " + intent + ", startActivityWithConfig result " + startActivityWithConfig);
                }
            }
        }
        z = false;
        applyConfigurationExternallyLocked();
        if (this.mDreamsDisabledByAmbientModeSuppression) {
        }
        if (str == null) {
        }
    }

    public final void adjustStatusBarCarModeLocked() {
        Context context = getContext();
        if (this.mStatusBarManager == null) {
            this.mStatusBarManager = (StatusBarManager) context.getSystemService("statusbar");
        }
        StatusBarManager statusBarManager = this.mStatusBarManager;
        if (statusBarManager != null) {
            statusBarManager.disable(this.mCarModeEnabled ? 524288 : 0);
        }
        if (this.mNotificationManager == null) {
            this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
        }
        NotificationManager notificationManager = this.mNotificationManager;
        if (notificationManager != null) {
            if (this.mCarModeEnabled) {
                this.mNotificationManager.notifyAsUser(null, 10, new Notification.Builder(context, SystemNotificationChannels.CAR_MODE).setSmallIcon(17303585).setDefaults(4).setOngoing(true).setWhen(0L).setColor(context.getColor(17170460)).setContentTitle(context.getString(17039796)).setContentText(context.getString(17039795)).setContentIntent(PendingIntent.getActivityAsUser(context, 0, new Intent(context, DisableCarModeActivity.class), 33554432, null, UserHandle.CURRENT)).build(), UserHandle.ALL);
            } else {
                notificationManager.cancelAsUser(null, 10, UserHandle.ALL);
            }
        }
    }

    public final void updateComputedNightModeLocked(boolean z) {
        TwilightManager twilightManager;
        this.mComputedNightMode = z;
        int i = this.mNightMode;
        if (i == 2 || i == 1) {
            return;
        }
        if (this.mOverrideNightModeOn && !z) {
            this.mComputedNightMode = true;
        } else if (this.mOverrideNightModeOff && z) {
            this.mComputedNightMode = false;
        } else if (i == 0 && ((twilightManager = this.mTwilightManager) == null || twilightManager.getLastTwilightState() == null)) {
        } else {
            resetNightModeOverrideLocked();
        }
    }

    public final boolean resetNightModeOverrideLocked() {
        if (this.mOverrideNightModeOff || this.mOverrideNightModeOn) {
            this.mOverrideNightModeOff = false;
            this.mOverrideNightModeOn = false;
            persistNightModeOverrides(this.mOverrideNightModeUser);
            this.mOverrideNightModeUser = 0;
            return true;
        }
        return false;
    }

    public final void registerVrStateListener() {
        IVrManager asInterface = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        if (asInterface != null) {
            try {
                asInterface.registerListener(this.mVrStateCallbacks);
            } catch (RemoteException e) {
                String str = TAG;
                Slog.e(str, "Failed to register VR mode state listener: " + e);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class Shell extends ShellCommand {
        public final IUiModeManager mInterface;

        public static String nightModeToStr(int i, int i2) {
            return i != 0 ? i != 1 ? i != 2 ? i != 3 ? "unknown" : i2 == 0 ? "custom_schedule" : i2 == 1 ? "custom_bedtime" : "unknown" : "yes" : "no" : "auto";
        }

        public Shell(IUiModeManager iUiModeManager) {
            this.mInterface = iUiModeManager;
        }

        public void onHelp() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("UiModeManager service (uimode) commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Print this help text.");
            outPrintWriter.println("  night [yes|no|auto|custom_schedule|custom_bedtime]");
            outPrintWriter.println("    Set or read night mode.");
            outPrintWriter.println("  car [yes|no]");
            outPrintWriter.println("    Set or read car mode.");
            outPrintWriter.println("  time [start|end] <ISO time>");
            outPrintWriter.println("    Set custom start/end schedule time (night mode must be set to custom to apply).");
        }

        /* JADX WARN: Removed duplicated region for block: B:24:0x0041  */
        /* JADX WARN: Removed duplicated region for block: B:32:0x0054 A[Catch: RemoteException -> 0x0059, TRY_LEAVE, TryCatch #0 {RemoteException -> 0x0059, blocks: (B:6:0x0008, B:26:0x0045, B:28:0x004a, B:30:0x004f, B:32:0x0054, B:13:0x001e, B:16:0x0029, B:19:0x0034), top: B:37:0x0008 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public int onCommand(String str) {
            boolean z;
            if (str == null) {
                return handleDefaultCommands(str);
            }
            try {
                int hashCode = str.hashCode();
                if (hashCode == 98260) {
                    if (str.equals("car")) {
                        z = true;
                        if (z) {
                        }
                    }
                    z = true;
                    if (z) {
                    }
                } else if (hashCode != 3560141) {
                    if (hashCode == 104817688 && str.equals("night")) {
                        z = false;
                        if (z) {
                            return handleNightMode();
                        }
                        if (!z) {
                            if (z) {
                                return handleCustomTime();
                            }
                            return handleDefaultCommands(str);
                        }
                        return handleCarMode();
                    }
                    z = true;
                    if (z) {
                    }
                } else {
                    if (str.equals("time")) {
                        z = true;
                        if (z) {
                        }
                    }
                    z = true;
                    if (z) {
                    }
                }
            } catch (RemoteException e) {
                getErrPrintWriter().println("Remote exception: " + e);
                return -1;
            }
        }

        public final int handleCustomTime() throws RemoteException {
            String nextArg = getNextArg();
            if (nextArg == null) {
                printCustomTime();
                return 0;
            } else if (nextArg.equals("end")) {
                this.mInterface.setCustomNightModeEnd(UiModeManagerService.toMilliSeconds(LocalTime.parse(getNextArg())));
                return 0;
            } else if (nextArg.equals("start")) {
                this.mInterface.setCustomNightModeStart(UiModeManagerService.toMilliSeconds(LocalTime.parse(getNextArg())));
                return 0;
            } else {
                getErrPrintWriter().println("command must be in [start|end]");
                return -1;
            }
        }

        public final void printCustomTime() throws RemoteException {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("start " + UiModeManagerService.fromMilliseconds(this.mInterface.getCustomNightModeStart()).toString());
            PrintWriter outPrintWriter2 = getOutPrintWriter();
            outPrintWriter2.println("end " + UiModeManagerService.fromMilliseconds(this.mInterface.getCustomNightModeEnd()).toString());
        }

        public final int handleNightMode() throws RemoteException {
            PrintWriter errPrintWriter = getErrPrintWriter();
            String nextArg = getNextArg();
            if (nextArg == null) {
                printCurrentNightMode();
                return 0;
            }
            int strToNightMode = strToNightMode(nextArg);
            int strToNightModeCustomType = strToNightModeCustomType(nextArg);
            if (strToNightMode >= 0) {
                this.mInterface.setNightMode(strToNightMode);
                if (strToNightMode == 3) {
                    this.mInterface.setNightModeCustomType(strToNightModeCustomType);
                }
                printCurrentNightMode();
                return 0;
            }
            errPrintWriter.println("Error: mode must be 'yes', 'no', or 'auto', or 'custom_schedule', or 'custom_bedtime'");
            return -1;
        }

        public final void printCurrentNightMode() throws RemoteException {
            PrintWriter outPrintWriter = getOutPrintWriter();
            String nightModeToStr = nightModeToStr(this.mInterface.getNightMode(), this.mInterface.getNightModeCustomType());
            outPrintWriter.println("Night mode: " + nightModeToStr);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        public static int strToNightMode(String str) {
            char c;
            str.hashCode();
            switch (str.hashCode()) {
                case -757868544:
                    if (str.equals("custom_bedtime")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 3521:
                    if (str.equals("no")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 119527:
                    if (str.equals("yes")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 3005871:
                    if (str.equals("auto")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 164399013:
                    if (str.equals("custom_schedule")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                case 4:
                    return 3;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 0;
                default:
                    return -1;
            }
        }

        public static int strToNightModeCustomType(String str) {
            str.hashCode();
            if (str.equals("custom_bedtime")) {
                return 1;
            }
            return !str.equals("custom_schedule") ? -1 : 0;
        }

        public final int handleCarMode() throws RemoteException {
            PrintWriter errPrintWriter = getErrPrintWriter();
            String nextArg = getNextArg();
            if (nextArg == null) {
                printCurrentCarMode();
                return 0;
            } else if (nextArg.equals("yes")) {
                this.mInterface.enableCarMode(0, 0, "");
                printCurrentCarMode();
                return 0;
            } else if (nextArg.equals("no")) {
                this.mInterface.disableCarMode(0);
                printCurrentCarMode();
                return 0;
            } else {
                errPrintWriter.println("Error: mode must be 'yes', or 'no'");
                return -1;
            }
        }

        public final void printCurrentCarMode() throws RemoteException {
            PrintWriter outPrintWriter = getOutPrintWriter();
            int currentModeType = this.mInterface.getCurrentModeType();
            StringBuilder sb = new StringBuilder();
            sb.append("Car mode: ");
            sb.append(currentModeType == 3 ? "yes" : "no");
            outPrintWriter.println(sb.toString());
        }
    }

    /* loaded from: classes.dex */
    public final class LocalService extends UiModeManagerInternal {
        public LocalService() {
        }

        @Override // com.android.server.UiModeManagerInternal
        public boolean isNightMode() {
            boolean z;
            synchronized (UiModeManagerService.this.mLock) {
                z = (UiModeManagerService.this.mConfiguration.uiMode & 32) != 0;
            }
            return z;
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Injector {
        public int getCallingUid() {
            return Binder.getCallingUid();
        }

        public void startDreamWhenDockedIfAppropriate(Context context) {
            Sandman.startDreamWhenDockedIfAppropriate(context);
        }
    }
}
