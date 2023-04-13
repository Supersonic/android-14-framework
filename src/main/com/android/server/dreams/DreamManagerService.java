package com.android.server.dreams;

import android.app.ActivityManager;
import android.app.TaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.hardware.display.AmbientDisplayConfiguration;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.dreams.DreamManagerInternal;
import android.service.dreams.IDreamManager;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.util.DumpUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.dreams.DreamController;
import com.android.server.dreams.DreamUiEventLogger;
import com.android.server.input.InputManagerInternal;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p014wm.ActivityInterceptorCallback;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes.dex */
public final class DreamManagerService extends SystemService {
    public final ActivityInterceptorCallback mActivityInterceptorCallback;
    public final ComponentName mAmbientDisplayComponent;
    public final ActivityTaskManagerInternal mAtmInternal;
    public final BroadcastReceiver mChargingReceiver;
    public final Context mContext;
    public final DreamController mController;
    public final DreamController.Listener mControllerListener;
    @GuardedBy({"mLock"})
    public DreamRecord mCurrentDream;
    public final boolean mDismissDreamOnActivityStart;
    public final BroadcastReceiver mDockStateReceiver;
    public final AmbientDisplayConfiguration mDozeConfig;
    public final ContentObserver mDozeEnabledObserver;
    public final PowerManager.WakeLock mDozeWakeLock;
    public final CopyOnWriteArrayList<DreamManagerInternal.DreamManagerStateListener> mDreamManagerStateListeners;
    public ComponentName mDreamOverlayServiceName;
    public final DreamUiEventLogger mDreamUiEventLogger;
    public final boolean mDreamsActivatedOnChargeByDefault;
    public final boolean mDreamsActivatedOnDockByDefault;
    public final boolean mDreamsEnabledByDefaultConfig;
    public boolean mDreamsEnabledSetting;
    public final boolean mDreamsOnlyEnabledForDockUser;
    public boolean mForceAmbientDisplayEnabled;
    public final DreamHandler mHandler;
    public boolean mIsCharging;
    public boolean mIsDocked;
    public final boolean mKeepDreamingWhenUndockedDefault;
    public final Object mLock;
    public final PowerManager mPowerManager;
    public final PowerManagerInternal mPowerManagerInternal;
    public SettingsObserver mSettingsObserver;
    public ComponentName mSystemDreamComponent;
    public final Runnable mSystemPropertiesChanged;
    public final UiEventLogger mUiEventLogger;
    public final UserManager mUserManager;
    public int mWhenToDream;

    /* loaded from: classes.dex */
    public final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            synchronized (DreamManagerService.this.mLock) {
                DreamManagerService.this.updateWhenToDreamSettings();
            }
        }
    }

    public DreamManagerService(Context context) {
        super(context);
        this.mLock = new Object();
        this.mDreamManagerStateListeners = new CopyOnWriteArrayList<>();
        this.mActivityInterceptorCallback = new ActivityInterceptorCallback() { // from class: com.android.server.dreams.DreamManagerService.1
            @Override // com.android.server.p014wm.ActivityInterceptorCallback
            public ActivityInterceptorCallback.ActivityInterceptResult onInterceptActivityLaunch(ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
                return null;
            }

            @Override // com.android.server.p014wm.ActivityInterceptorCallback
            public void onActivityLaunched(TaskInfo taskInfo, ActivityInfo activityInfo, ActivityInterceptorCallback.ActivityInterceptorInfo activityInterceptorInfo) {
                int activityType = taskInfo.getActivityType();
                boolean z = false;
                boolean z2 = activityType == 2 || activityType == 5 || activityType == 4;
                synchronized (DreamManagerService.this.mLock) {
                    if (DreamManagerService.this.mCurrentDream != null && !DreamManagerService.this.mCurrentDream.isWaking && !DreamManagerService.this.mCurrentDream.isDozing && !z2) {
                        z = true;
                    }
                }
                if (z) {
                    DreamManagerService.this.requestAwakenInternal("stopping dream due to activity start: " + activityInfo.name);
                }
            }
        };
        this.mChargingReceiver = new BroadcastReceiver() { // from class: com.android.server.dreams.DreamManagerService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                DreamManagerService.this.mIsCharging = "android.os.action.CHARGING".equals(intent.getAction());
            }
        };
        this.mDockStateReceiver = new BroadcastReceiver() { // from class: com.android.server.dreams.DreamManagerService.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.DOCK_EVENT".equals(intent.getAction())) {
                    int intExtra = intent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
                    DreamManagerService.this.mIsDocked = intExtra != 0;
                }
            }
        };
        DreamController.Listener listener = new DreamController.Listener() { // from class: com.android.server.dreams.DreamManagerService.5
            @Override // com.android.server.dreams.DreamController.Listener
            public void onDreamStopped(Binder binder) {
                synchronized (DreamManagerService.this.mLock) {
                    if (DreamManagerService.this.mCurrentDream != null && DreamManagerService.this.mCurrentDream.token == binder) {
                        DreamManagerService.this.cleanupDreamLocked();
                    }
                }
            }
        };
        this.mControllerListener = listener;
        this.mDozeEnabledObserver = new ContentObserver(null) { // from class: com.android.server.dreams.DreamManagerService.6
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                DreamManagerService.this.writePulseGestureEnabled();
            }
        };
        this.mSystemPropertiesChanged = new Runnable() { // from class: com.android.server.dreams.DreamManagerService.7
            @Override // java.lang.Runnable
            public void run() {
                synchronized (DreamManagerService.this.mLock) {
                    if (DreamManagerService.this.mCurrentDream != null && DreamManagerService.this.mCurrentDream.name != null && DreamManagerService.this.mCurrentDream.canDoze && !DreamManagerService.this.mCurrentDream.name.equals(DreamManagerService.this.getDozeComponent())) {
                        DreamManagerService.this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "android.server.dreams:SYSPROP");
                    }
                }
            }
        };
        this.mContext = context;
        DreamHandler dreamHandler = new DreamHandler(FgThread.get().getLooper());
        this.mHandler = dreamHandler;
        this.mController = new DreamController(context, dreamHandler, listener);
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        this.mPowerManager = powerManager;
        this.mPowerManagerInternal = (PowerManagerInternal) getLocalService(PowerManagerInternal.class);
        this.mAtmInternal = (ActivityTaskManagerInternal) getLocalService(ActivityTaskManagerInternal.class);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mDozeWakeLock = powerManager.newWakeLock(64, "dream:doze");
        this.mDozeConfig = new AmbientDisplayConfiguration(context);
        this.mUiEventLogger = new UiEventLoggerImpl();
        this.mDreamUiEventLogger = new DreamUiEventLoggerImpl(context.getResources().getStringArray(17236090));
        this.mAmbientDisplayComponent = ComponentName.unflattenFromString(new AmbientDisplayConfiguration(context).ambientDisplayComponent());
        this.mDreamsOnlyEnabledForDockUser = context.getResources().getBoolean(17891632);
        this.mDismissDreamOnActivityStart = context.getResources().getBoolean(17891611);
        this.mDreamsEnabledByDefaultConfig = context.getResources().getBoolean(17891630);
        this.mDreamsActivatedOnChargeByDefault = context.getResources().getBoolean(17891628);
        this.mDreamsActivatedOnDockByDefault = context.getResources().getBoolean(17891627);
        this.mSettingsObserver = new SettingsObserver(dreamHandler);
        this.mKeepDreamingWhenUndockedDefault = context.getResources().getBoolean(17891714);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("dreams", new BinderService());
        publishLocalService(DreamManagerInternal.class, new LocalService());
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 600) {
            if (Build.IS_DEBUGGABLE) {
                SystemProperties.addChangeCallback(this.mSystemPropertiesChanged);
            }
            this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.android.server.dreams.DreamManagerService.4
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    DreamManagerService.this.writePulseGestureEnabled();
                    synchronized (DreamManagerService.this.mLock) {
                        DreamManagerService.this.stopDreamLocked(false, "user switched");
                    }
                }
            }, new IntentFilter("android.intent.action.USER_SWITCHED"), null, this.mHandler);
            this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("doze_pulse_on_double_tap"), false, this.mDozeEnabledObserver, -1);
            writePulseGestureEnabled();
            if (this.mDismissDreamOnActivityStart) {
                this.mAtmInternal.registerActivityStartInterceptor(4, this.mActivityInterceptorCallback);
            }
            this.mContext.registerReceiver(this.mDockStateReceiver, new IntentFilter("android.intent.action.DOCK_EVENT"));
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.CHARGING");
            intentFilter.addAction("android.os.action.DISCHARGING");
            this.mContext.registerReceiver(this.mChargingReceiver, intentFilter);
            this.mSettingsObserver = new SettingsObserver(this.mHandler);
            this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("screensaver_activate_on_sleep"), false, this.mSettingsObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("screensaver_activate_on_dock"), false, this.mSettingsObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("screensaver_enabled"), false, this.mSettingsObserver, -1);
            this.mIsCharging = ((BatteryManager) this.mContext.getSystemService(BatteryManager.class)).isCharging();
            updateWhenToDreamSettings();
        }
    }

    public final void dumpInternal(PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.println("DREAM MANAGER (dumpsys dreams)");
            printWriter.println();
            printWriter.println("mCurrentDream=" + this.mCurrentDream);
            printWriter.println("mForceAmbientDisplayEnabled=" + this.mForceAmbientDisplayEnabled);
            printWriter.println("mDreamsOnlyEnabledForDockUser=" + this.mDreamsOnlyEnabledForDockUser);
            printWriter.println("mDreamsEnabledSetting=" + this.mDreamsEnabledSetting);
            printWriter.println("mDreamsActivatedOnDockByDefault=" + this.mDreamsActivatedOnDockByDefault);
            printWriter.println("mDreamsActivatedOnChargeByDefault=" + this.mDreamsActivatedOnChargeByDefault);
            printWriter.println("mIsDocked=" + this.mIsDocked);
            printWriter.println("mIsCharging=" + this.mIsCharging);
            printWriter.println("mWhenToDream=" + this.mWhenToDream);
            printWriter.println("mKeepDreamingWhenUndockedDefault=" + this.mKeepDreamingWhenUndockedDefault);
            printWriter.println("getDozeComponent()=" + getDozeComponent());
            printWriter.println();
            DumpUtils.dumpAsync(this.mHandler, new DumpUtils.Dump() { // from class: com.android.server.dreams.DreamManagerService$$ExternalSyntheticLambda1
                public final void dump(PrintWriter printWriter2, String str) {
                    DreamManagerService.this.lambda$dumpInternal$0(printWriter2, str);
                }
            }, printWriter, "", 200L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dumpInternal$0(PrintWriter printWriter, String str) {
        this.mController.dump(printWriter);
    }

    public final void updateWhenToDreamSettings() {
        synchronized (this.mLock) {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            boolean z = true;
            this.mWhenToDream = (Settings.Secure.getIntForUser(contentResolver, "screensaver_activate_on_sleep", this.mDreamsActivatedOnChargeByDefault ? 1 : 0, -2) != 0 ? 2 : 0) + (Settings.Secure.getIntForUser(contentResolver, "screensaver_activate_on_dock", this.mDreamsActivatedOnDockByDefault ? 1 : 0, -2) != 0 ? 1 : 0);
            if (Settings.Secure.getIntForUser(contentResolver, "screensaver_enabled", this.mDreamsEnabledByDefaultConfig ? 1 : 0, -2) == 0) {
                z = false;
            }
            this.mDreamsEnabledSetting = z;
        }
    }

    public final void reportKeepDreamingWhenUndockedChanged(final boolean z) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamManagerService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                DreamManagerService.this.lambda$reportKeepDreamingWhenUndockedChanged$1(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reportKeepDreamingWhenUndockedChanged$1(boolean z) {
        Iterator<DreamManagerInternal.DreamManagerStateListener> it = this.mDreamManagerStateListeners.iterator();
        while (it.hasNext()) {
            it.next().onKeepDreamingWhenUndockedChanged(z);
        }
    }

    public final boolean isDreamingInternal() {
        boolean z;
        synchronized (this.mLock) {
            DreamRecord dreamRecord = this.mCurrentDream;
            z = (dreamRecord == null || dreamRecord.isPreview || dreamRecord.isWaking) ? false : true;
        }
        return z;
    }

    public final boolean isDozingInternal() {
        boolean z;
        synchronized (this.mLock) {
            DreamRecord dreamRecord = this.mCurrentDream;
            z = dreamRecord != null && dreamRecord.isDozing;
        }
        return z;
    }

    public final boolean isDreamingOrInPreviewInternal() {
        boolean z;
        synchronized (this.mLock) {
            DreamRecord dreamRecord = this.mCurrentDream;
            z = (dreamRecord == null || dreamRecord.isWaking) ? false : true;
        }
        return z;
    }

    public final boolean canStartDreamingInternal(boolean z) {
        synchronized (this.mLock) {
            if (z) {
                if (isDreamingInternal()) {
                    return false;
                }
            }
            if (this.mDreamsEnabledSetting) {
                if (dreamsEnabledForUser(ActivityManager.getCurrentUser())) {
                    if (this.mUserManager.isUserUnlocked()) {
                        int i = this.mWhenToDream;
                        if ((i & 2) == 2) {
                            return this.mIsCharging;
                        } else if ((i & 1) == 1) {
                            return this.mIsDocked;
                        } else {
                            return false;
                        }
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
    }

    public void requestStartDreamFromShell() {
        requestDreamInternal();
    }

    public final void requestDreamInternal() {
        long uptimeMillis = SystemClock.uptimeMillis();
        this.mPowerManager.userActivity(uptimeMillis, true);
        this.mPowerManagerInternal.nap(uptimeMillis, true);
    }

    public final void requestAwakenInternal(String str) {
        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), false);
        stopDreamInternal(false, str);
    }

    public final void finishSelfInternal(IBinder iBinder, boolean z) {
        synchronized (this.mLock) {
            DreamRecord dreamRecord = this.mCurrentDream;
            if (dreamRecord != null && dreamRecord.token == iBinder) {
                stopDreamLocked(z, "finished self");
            }
        }
    }

    public final void testDreamInternal(ComponentName componentName, int i) {
        synchronized (this.mLock) {
            startDreamLocked(componentName, true, false, i, "test dream");
        }
    }

    public final void startDreamInternal(boolean z, String str) {
        int currentUser = ActivityManager.getCurrentUser();
        ComponentName chooseDreamForUser = chooseDreamForUser(z, currentUser);
        if (chooseDreamForUser != null) {
            synchronized (this.mLock) {
                startDreamLocked(chooseDreamForUser, false, z, currentUser, str);
            }
        }
    }

    public void requestStopDreamFromShell() {
        stopDreamInternal(false, "stopping dream from shell");
    }

    public final void stopDreamInternal(boolean z, String str) {
        synchronized (this.mLock) {
            stopDreamLocked(z, str);
        }
    }

    public final void startDozingInternal(IBinder iBinder, int i, int i2) {
        synchronized (this.mLock) {
            DreamRecord dreamRecord = this.mCurrentDream;
            if (dreamRecord != null && dreamRecord.token == iBinder && dreamRecord.canDoze) {
                dreamRecord.dozeScreenState = i;
                dreamRecord.dozeScreenBrightness = i2;
                this.mPowerManagerInternal.setDozeOverrideFromDreamManager(i, i2);
                DreamRecord dreamRecord2 = this.mCurrentDream;
                if (!dreamRecord2.isDozing) {
                    dreamRecord2.isDozing = true;
                    this.mDozeWakeLock.acquire();
                }
            }
        }
    }

    public final void stopDozingInternal(IBinder iBinder) {
        synchronized (this.mLock) {
            DreamRecord dreamRecord = this.mCurrentDream;
            if (dreamRecord != null && dreamRecord.token == iBinder && dreamRecord.isDozing) {
                dreamRecord.isDozing = false;
                this.mDozeWakeLock.release();
                this.mPowerManagerInternal.setDozeOverrideFromDreamManager(0, -1);
            }
        }
    }

    public final void forceAmbientDisplayEnabledInternal(boolean z) {
        synchronized (this.mLock) {
            this.mForceAmbientDisplayEnabled = z;
        }
    }

    public final ComponentName chooseDreamForUser(boolean z, int i) {
        if (z) {
            ComponentName dozeComponent = getDozeComponent(i);
            if (validateDream(dozeComponent)) {
                return dozeComponent;
            }
            return null;
        }
        ComponentName componentName = this.mSystemDreamComponent;
        if (componentName != null) {
            return componentName;
        }
        ComponentName[] dreamComponentsForUser = getDreamComponentsForUser(i);
        if (dreamComponentsForUser == null || dreamComponentsForUser.length == 0) {
            return null;
        }
        return dreamComponentsForUser[0];
    }

    public final boolean validateDream(ComponentName componentName) {
        if (componentName == null) {
            return false;
        }
        ServiceInfo serviceInfo = getServiceInfo(componentName);
        if (serviceInfo == null) {
            Slog.w("DreamManagerService", "Dream " + componentName + " does not exist");
            return false;
        } else if (serviceInfo.applicationInfo.targetSdkVersion < 21 || "android.permission.BIND_DREAM_SERVICE".equals(serviceInfo.permission)) {
            return true;
        } else {
            Slog.w("DreamManagerService", "Dream " + componentName + " is not available because its manifest is missing the android.permission.BIND_DREAM_SERVICE permission on the dream service declaration.");
            return false;
        }
    }

    public final ComponentName[] getDreamComponentsForUser(int i) {
        ComponentName defaultDreamComponentForUser;
        if (dreamsEnabledForUser(i)) {
            ComponentName[] componentsFromString = componentsFromString(Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "screensaver_components", i));
            ArrayList arrayList = new ArrayList();
            if (componentsFromString != null) {
                for (ComponentName componentName : componentsFromString) {
                    if (validateDream(componentName)) {
                        arrayList.add(componentName);
                    }
                }
            }
            if (arrayList.isEmpty() && (defaultDreamComponentForUser = getDefaultDreamComponentForUser(i)) != null) {
                Slog.w("DreamManagerService", "Falling back to default dream " + defaultDreamComponentForUser);
                arrayList.add(defaultDreamComponentForUser);
            }
            return (ComponentName[]) arrayList.toArray(new ComponentName[arrayList.size()]);
        }
        return null;
    }

    public final void setDreamComponentsForUser(int i, ComponentName[] componentNameArr) {
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "screensaver_components", componentsToString(componentNameArr), i);
    }

    public final void setSystemDreamComponentInternal(ComponentName componentName) {
        synchronized (this.mLock) {
            if (Objects.equals(this.mSystemDreamComponent, componentName)) {
                return;
            }
            this.mSystemDreamComponent = componentName;
            reportKeepDreamingWhenUndockedChanged(shouldKeepDreamingWhenUndocked());
            if (isDreamingInternal() && !isDozingInternal()) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.mSystemDreamComponent == null ? "clear" : "set");
                sb.append(" system dream component");
                startDreamInternal(false, sb.toString());
            }
        }
    }

    public final boolean shouldKeepDreamingWhenUndocked() {
        return this.mKeepDreamingWhenUndockedDefault && this.mSystemDreamComponent == null;
    }

    public final ComponentName getDefaultDreamComponentForUser(int i) {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "screensaver_default_component", i);
        if (stringForUser == null) {
            return null;
        }
        return ComponentName.unflattenFromString(stringForUser);
    }

    public final ComponentName getDozeComponent() {
        return getDozeComponent(ActivityManager.getCurrentUser());
    }

    public final ComponentName getDozeComponent(int i) {
        if (this.mForceAmbientDisplayEnabled || this.mDozeConfig.enabled(i)) {
            return ComponentName.unflattenFromString(this.mDozeConfig.ambientDisplayComponent());
        }
        return null;
    }

    public final boolean dreamsEnabledForUser(int i) {
        if (this.mDreamsOnlyEnabledForDockUser) {
            return i >= 0 && i == ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getMainUserId();
        }
        return true;
    }

    public final ServiceInfo getServiceInfo(ComponentName componentName) {
        if (componentName != null) {
            try {
                return this.mContext.getPackageManager().getServiceInfo(componentName, 268435456);
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        return null;
    }

    @GuardedBy({"mLock"})
    public final void startDreamLocked(final ComponentName componentName, final boolean z, final boolean z2, final int i, final String str) {
        DreamRecord dreamRecord = this.mCurrentDream;
        if (dreamRecord != null && !dreamRecord.isWaking && Objects.equals(dreamRecord.name, componentName)) {
            DreamRecord dreamRecord2 = this.mCurrentDream;
            if (dreamRecord2.isPreview == z && dreamRecord2.canDoze == z2 && dreamRecord2.userId == i) {
                Slog.i("DreamManagerService", "Already in target dream.");
                return;
            }
        }
        Slog.i("DreamManagerService", "Entering dreamland.");
        DreamRecord dreamRecord3 = this.mCurrentDream;
        if (dreamRecord3 != null && dreamRecord3.isDozing) {
            stopDozingInternal(dreamRecord3.token);
        }
        DreamRecord dreamRecord4 = new DreamRecord(componentName, i, z, z2);
        this.mCurrentDream = dreamRecord4;
        if (!dreamRecord4.name.equals(this.mAmbientDisplayComponent)) {
            UiEventLogger uiEventLogger = this.mUiEventLogger;
            DreamUiEventLogger.DreamUiEventEnum dreamUiEventEnum = DreamUiEventLogger.DreamUiEventEnum.DREAM_START;
            uiEventLogger.log(dreamUiEventEnum);
            this.mDreamUiEventLogger.log(dreamUiEventEnum, this.mCurrentDream.name.flattenToString());
        }
        final PowerManager.WakeLock newWakeLock = this.mPowerManager.newWakeLock(1, "dream:dream");
        final Binder binder = this.mCurrentDream.token;
        this.mHandler.post(newWakeLock.wrap(new Runnable() { // from class: com.android.server.dreams.DreamManagerService$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                DreamManagerService.this.lambda$startDreamLocked$2(componentName, binder, z, z2, i, newWakeLock, str);
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDreamLocked$2(ComponentName componentName, Binder binder, boolean z, boolean z2, int i, PowerManager.WakeLock wakeLock, String str) {
        this.mAtmInternal.notifyActiveDreamChanged(componentName);
        this.mController.startDream(binder, componentName, z, z2, i, wakeLock, this.mDreamOverlayServiceName, str);
    }

    @GuardedBy({"mLock"})
    public final void stopDreamLocked(final boolean z, final String str) {
        DreamRecord dreamRecord = this.mCurrentDream;
        if (dreamRecord != null) {
            if (z) {
                Slog.i("DreamManagerService", "Leaving dreamland.");
                cleanupDreamLocked();
            } else if (dreamRecord.isWaking) {
                return;
            } else {
                Slog.i("DreamManagerService", "Gently waking up from dream.");
                this.mCurrentDream.isWaking = true;
            }
            this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamManagerService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DreamManagerService.this.lambda$stopDreamLocked$3(z, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$stopDreamLocked$3(boolean z, String str) {
        this.mController.stopDream(z, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanupDreamLocked$4() {
        this.mAtmInternal.notifyActiveDreamChanged(null);
    }

    @GuardedBy({"mLock"})
    public final void cleanupDreamLocked() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.dreams.DreamManagerService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                DreamManagerService.this.lambda$cleanupDreamLocked$4();
            }
        });
        DreamRecord dreamRecord = this.mCurrentDream;
        if (dreamRecord == null) {
            return;
        }
        if (!dreamRecord.name.equals(this.mAmbientDisplayComponent)) {
            UiEventLogger uiEventLogger = this.mUiEventLogger;
            DreamUiEventLogger.DreamUiEventEnum dreamUiEventEnum = DreamUiEventLogger.DreamUiEventEnum.DREAM_STOP;
            uiEventLogger.log(dreamUiEventEnum);
            this.mDreamUiEventLogger.log(dreamUiEventEnum, this.mCurrentDream.name.flattenToString());
        }
        if (this.mCurrentDream.isDozing) {
            this.mDozeWakeLock.release();
        }
        this.mCurrentDream = null;
    }

    public final void checkPermission(String str) {
        if (this.mContext.checkCallingOrSelfPermission(str) == 0) {
            return;
        }
        throw new SecurityException("Access denied to process: " + Binder.getCallingPid() + ", must have permission " + str);
    }

    public final void writePulseGestureEnabled() {
        ((InputManagerInternal) LocalServices.getService(InputManagerInternal.class)).setPulseGestureEnabled(validateDream(getDozeComponent()));
    }

    public static String componentsToString(ComponentName[] componentNameArr) {
        if (componentNameArr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (ComponentName componentName : componentNameArr) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(componentName.flattenToString());
        }
        return sb.toString();
    }

    public static ComponentName[] componentsFromString(String str) {
        if (str == null) {
            return null;
        }
        String[] split = str.split(",");
        ComponentName[] componentNameArr = new ComponentName[split.length];
        for (int i = 0; i < split.length; i++) {
            componentNameArr[i] = ComponentName.unflattenFromString(split[i]);
        }
        return componentNameArr;
    }

    /* loaded from: classes.dex */
    public static final class DreamHandler extends Handler {
        public DreamHandler(Looper looper) {
            super(looper, null, true);
        }
    }

    /* loaded from: classes.dex */
    public final class BinderService extends IDreamManager.Stub {
        public BinderService() {
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(DreamManagerService.this.mContext, "DreamManagerService", printWriter)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    DreamManagerService.this.dumpInternal(printWriter);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
            new DreamShellCommand(DreamManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public ComponentName[] getDreamComponents() {
            return getDreamComponentsForUser(UserHandle.getCallingUserId());
        }

        public ComponentName[] getDreamComponentsForUser(int i) {
            DreamManagerService.this.checkPermission("android.permission.READ_DREAM_STATE");
            int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, true, "getDreamComponents", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return DreamManagerService.this.getDreamComponentsForUser(handleIncomingUser);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setDreamComponents(ComponentName[] componentNameArr) {
            DreamManagerService.this.checkPermission("android.permission.WRITE_DREAM_STATE");
            int callingUserId = UserHandle.getCallingUserId();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                setDreamComponentsForUser(callingUserId, componentNameArr);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setDreamComponentsForUser(int i, ComponentName[] componentNameArr) {
            DreamManagerService.this.checkPermission("android.permission.WRITE_DREAM_STATE");
            int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, true, "setDreamComponents", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.setDreamComponentsForUser(handleIncomingUser, componentNameArr);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setSystemDreamComponent(ComponentName componentName) {
            DreamManagerService.this.checkPermission("android.permission.WRITE_DREAM_STATE");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.setSystemDreamComponentInternal(componentName);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void registerDreamOverlayService(ComponentName componentName) {
            DreamManagerService.this.checkPermission("android.permission.WRITE_DREAM_STATE");
            DreamManagerService.this.mDreamOverlayServiceName = componentName;
        }

        public ComponentName getDefaultDreamComponentForUser(int i) {
            DreamManagerService.this.checkPermission("android.permission.READ_DREAM_STATE");
            int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, true, "getDefaultDreamComponent", null);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return DreamManagerService.this.getDefaultDreamComponentForUser(handleIncomingUser);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isDreaming() {
            DreamManagerService.this.checkPermission("android.permission.READ_DREAM_STATE");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return DreamManagerService.this.isDreamingInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public boolean isDreamingOrInPreview() {
            DreamManagerService.this.checkPermission("android.permission.READ_DREAM_STATE");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return DreamManagerService.this.isDreamingOrInPreviewInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void dream() {
            DreamManagerService.this.checkPermission("android.permission.WRITE_DREAM_STATE");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.requestDreamInternal();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void testDream(int i, ComponentName componentName) {
            if (componentName == null) {
                throw new IllegalArgumentException("dream must not be null");
            }
            DreamManagerService.this.checkPermission("android.permission.WRITE_DREAM_STATE");
            int handleIncomingUser = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, false, true, "testDream", null);
            int currentUser = ActivityManager.getCurrentUser();
            if (handleIncomingUser != currentUser) {
                Slog.w("DreamManagerService", "Aborted attempt to start a test dream while a different  user is active: userId=" + handleIncomingUser + ", currentUserId=" + currentUser);
                return;
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.testDreamInternal(componentName, handleIncomingUser);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void awaken() {
            DreamManagerService.this.checkPermission("android.permission.WRITE_DREAM_STATE");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.requestAwakenInternal("request awaken");
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void finishSelf(IBinder iBinder, boolean z) {
            if (iBinder == null) {
                throw new IllegalArgumentException("token must not be null");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.finishSelfInternal(iBinder, z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void startDozing(IBinder iBinder, int i, int i2) {
            if (iBinder == null) {
                throw new IllegalArgumentException("token must not be null");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.startDozingInternal(iBinder, i, i2);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void stopDozing(IBinder iBinder) {
            if (iBinder == null) {
                throw new IllegalArgumentException("token must not be null");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.stopDozingInternal(iBinder);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void forceAmbientDisplayEnabled(boolean z) {
            DreamManagerService.this.checkPermission("android.permission.DEVICE_POWER");
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                DreamManagerService.this.forceAmbientDisplayEnabledInternal(z);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class LocalService extends DreamManagerInternal {
        public LocalService() {
        }

        public void startDream(boolean z, String str) {
            DreamManagerService.this.startDreamInternal(z, str);
        }

        public void stopDream(boolean z, String str) {
            DreamManagerService.this.stopDreamInternal(z, str);
        }

        public boolean isDreaming() {
            return DreamManagerService.this.isDreamingInternal();
        }

        public boolean canStartDreaming(boolean z) {
            return DreamManagerService.this.canStartDreamingInternal(z);
        }

        public void requestDream() {
            DreamManagerService.this.requestDreamInternal();
        }

        public void registerDreamManagerStateListener(DreamManagerInternal.DreamManagerStateListener dreamManagerStateListener) {
            DreamManagerService.this.mDreamManagerStateListeners.add(dreamManagerStateListener);
            dreamManagerStateListener.onKeepDreamingWhenUndockedChanged(DreamManagerService.this.shouldKeepDreamingWhenUndocked());
        }

        public void unregisterDreamManagerStateListener(DreamManagerInternal.DreamManagerStateListener dreamManagerStateListener) {
            DreamManagerService.this.mDreamManagerStateListeners.remove(dreamManagerStateListener);
        }
    }

    /* loaded from: classes.dex */
    public static final class DreamRecord {
        public final boolean canDoze;
        public final boolean isPreview;
        public final ComponentName name;
        public final int userId;
        public final Binder token = new Binder();
        public boolean isDozing = false;
        public boolean isWaking = false;
        public int dozeScreenState = 0;
        public int dozeScreenBrightness = -1;

        public DreamRecord(ComponentName componentName, int i, boolean z, boolean z2) {
            this.name = componentName;
            this.userId = i;
            this.isPreview = z;
            this.canDoze = z2;
        }

        public String toString() {
            return "DreamRecord{token=" + this.token + ", name=" + this.name + ", userId=" + this.userId + ", isPreview=" + this.isPreview + ", canDoze=" + this.canDoze + ", isDozing=" + this.isDozing + ", isWaking=" + this.isWaking + ", dozeScreenState=" + this.dozeScreenState + ", dozeScreenBrightness=" + this.dozeScreenBrightness + '}';
        }
    }
}
