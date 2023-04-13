package com.android.server;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.BroadcastOptions;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.health.HealthInfo;
import android.metrics.LogMaker;
import android.os.BatteryManagerInternal;
import android.os.BatteryProperty;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBatteryPropertiesRegistrar;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UEventObserver;
import android.os.UserHandle;
import android.provider.Settings;
import android.sysprop.PowerProperties;
import android.util.EventLog;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.app.IBatteryStats;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FunctionalUtils;
import com.android.server.health.HealthInfoCallback;
import com.android.server.health.HealthServiceWrapper;
import com.android.server.health.Utils;
import com.android.server.lights.LightsManager;
import com.android.server.lights.LogicalLight;
import com.android.server.p006am.BatteryStatsService;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.NoSuchElementException;
/* loaded from: classes.dex */
public final class BatteryService extends SystemService {
    public static final String[] DUMPSYS_ARGS = {"--checkin", "--unplugged"};
    public static final String TAG = "BatteryService";
    public ActivityManagerInternal mActivityManagerInternal;
    public Bundle mBatteryChangedOptions;
    public boolean mBatteryInputSuspended;
    public boolean mBatteryLevelCritical;
    public boolean mBatteryLevelLow;
    public ArrayDeque<Bundle> mBatteryLevelsEventQueue;
    public int mBatteryNearlyFullLevel;
    public Bundle mBatteryOptions;
    public BatteryPropertiesRegistrar mBatteryPropertiesRegistrar;
    public final IBatteryStats mBatteryStats;
    public BinderService mBinderService;
    public int mChargeStartLevel;
    public long mChargeStartTime;
    public final Context mContext;
    public int mCriticalBatteryLevel;
    public int mDischargeStartLevel;
    public long mDischargeStartTime;
    public final Handler mHandler;
    public HealthInfo mHealthInfo;
    public HealthServiceWrapper mHealthServiceWrapper;
    public int mInvalidCharger;
    public int mLastBatteryCycleCount;
    public int mLastBatteryHealth;
    public int mLastBatteryLevel;
    public long mLastBatteryLevelChangedSentMs;
    public boolean mLastBatteryLevelCritical;
    public boolean mLastBatteryPresent;
    public int mLastBatteryStatus;
    public int mLastBatteryTemperature;
    public int mLastBatteryVoltage;
    public int mLastChargeCounter;
    public int mLastCharingState;
    public final HealthInfo mLastHealthInfo;
    public int mLastInvalidCharger;
    public int mLastLowBatteryWarningLevel;
    public int mLastMaxChargingCurrent;
    public int mLastMaxChargingVoltage;
    public int mLastPlugType;
    public Led mLed;
    public final Object mLock;
    public int mLowBatteryCloseWarningLevel;
    public int mLowBatteryWarningLevel;
    public MetricsLogger mMetricsLogger;
    public int mPlugType;
    public Bundle mPowerOptions;
    public boolean mSentLowBatteryBroadcast;
    public int mSequence;
    public int mShutdownBatteryTemperature;
    public boolean mUpdatesStopped;

    public BatteryService(Context context) {
        super(context);
        this.mLock = new Object();
        this.mLastHealthInfo = new HealthInfo();
        this.mSequence = 1;
        this.mLastPlugType = -1;
        this.mSentLowBatteryBroadcast = false;
        this.mBatteryChangedOptions = BroadcastOptions.makeBasic().setDeliveryGroupPolicy(1).setDeferUntilActive(true).toBundle();
        this.mPowerOptions = BroadcastOptions.makeBasic().setDeliveryGroupPolicy(1).setDeliveryGroupMatchingKey(PackageManagerShellCommandDataLoader.PACKAGE, "android.intent.action.ACTION_POWER_CONNECTED").setDeferUntilActive(true).toBundle();
        this.mBatteryOptions = BroadcastOptions.makeBasic().setDeliveryGroupPolicy(1).setDeliveryGroupMatchingKey(PackageManagerShellCommandDataLoader.PACKAGE, "android.intent.action.BATTERY_OKAY").setDeferUntilActive(true).toBundle();
        this.mContext = context;
        this.mHandler = new Handler(true);
        this.mLed = new Led(context, (LightsManager) getLocalService(LightsManager.class));
        this.mBatteryStats = BatteryStatsService.getService();
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mCriticalBatteryLevel = context.getResources().getInteger(17694778);
        int integer = context.getResources().getInteger(17694874);
        this.mLowBatteryWarningLevel = integer;
        this.mLowBatteryCloseWarningLevel = integer + context.getResources().getInteger(17694873);
        this.mShutdownBatteryTemperature = context.getResources().getInteger(17694959);
        this.mBatteryLevelsEventQueue = new ArrayDeque<>();
        this.mMetricsLogger = new MetricsLogger();
        if (new File("/sys/devices/virtual/switch/invalid_charger/state").exists()) {
            new UEventObserver() { // from class: com.android.server.BatteryService.1
                public void onUEvent(UEventObserver.UEvent uEvent) {
                    boolean equals = "1".equals(uEvent.get("SWITCH_STATE"));
                    synchronized (BatteryService.this.mLock) {
                        if (BatteryService.this.mInvalidCharger != equals) {
                            BatteryService.this.mInvalidCharger = equals ? 1 : 0;
                        }
                    }
                }
            }.startObserving("DEVPATH=/devices/virtual/switch/invalid_charger");
        }
        this.mBatteryInputSuspended = ((Boolean) PowerProperties.battery_input_suspended().orElse(Boolean.FALSE)).booleanValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.android.server.BatteryService$BatteryPropertiesRegistrar, android.os.IBinder] */
    @Override // com.android.server.SystemService
    public void onStart() {
        registerHealthCallback();
        BinderService binderService = new BinderService();
        this.mBinderService = binderService;
        publishBinderService("battery", binderService);
        ?? batteryPropertiesRegistrar = new BatteryPropertiesRegistrar();
        this.mBatteryPropertiesRegistrar = batteryPropertiesRegistrar;
        publishBinderService("batteryproperties", batteryPropertiesRegistrar);
        publishLocalService(BatteryManagerInternal.class, new LocalService());
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 550) {
            synchronized (this.mLock) {
                this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), false, new ContentObserver(this.mHandler) { // from class: com.android.server.BatteryService.2
                    @Override // android.database.ContentObserver
                    public void onChange(boolean z) {
                        synchronized (BatteryService.this.mLock) {
                            BatteryService.this.updateBatteryWarningLevelLocked();
                        }
                    }
                }, -1);
                updateBatteryWarningLevelLocked();
            }
        }
    }

    public final void registerHealthCallback() {
        traceBegin("HealthInitWrapper");
        try {
            try {
                this.mHealthServiceWrapper = HealthServiceWrapper.create(new HealthInfoCallback() { // from class: com.android.server.BatteryService$$ExternalSyntheticLambda3
                    @Override // com.android.server.health.HealthInfoCallback
                    public final void update(HealthInfo healthInfo) {
                        BatteryService.this.update(healthInfo);
                    }
                });
                traceEnd();
                traceBegin("HealthInitWaitUpdate");
                long uptimeMillis = SystemClock.uptimeMillis();
                synchronized (this.mLock) {
                    while (this.mHealthInfo == null) {
                        String str = TAG;
                        Slog.i(str, "health: Waited " + (SystemClock.uptimeMillis() - uptimeMillis) + "ms for callbacks. Waiting another 1000 ms...");
                        try {
                            this.mLock.wait(1000L);
                        } catch (InterruptedException unused) {
                            Slog.i(TAG, "health: InterruptedException when waiting for update.  Continuing...");
                        }
                    }
                }
                String str2 = TAG;
                Slog.i(str2, "health: Waited " + (SystemClock.uptimeMillis() - uptimeMillis) + "ms and received the update.");
            } catch (RemoteException e) {
                Slog.e(TAG, "health: cannot register callback. (RemoteException)");
                throw e.rethrowFromSystemServer();
            } catch (NoSuchElementException e2) {
                Slog.e(TAG, "health: cannot register callback. (no supported health HAL service)");
                throw e2;
            }
        } finally {
            traceEnd();
        }
    }

    public final void updateBatteryWarningLevelLocked() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int integer = this.mContext.getResources().getInteger(17694874);
        this.mLastLowBatteryWarningLevel = this.mLowBatteryWarningLevel;
        int i = Settings.Global.getInt(contentResolver, "low_power_trigger_level", integer);
        this.mLowBatteryWarningLevel = i;
        if (i == 0) {
            this.mLowBatteryWarningLevel = integer;
        }
        int i2 = this.mLowBatteryWarningLevel;
        int i3 = this.mCriticalBatteryLevel;
        if (i2 < i3) {
            this.mLowBatteryWarningLevel = i3;
        }
        this.mLowBatteryCloseWarningLevel = this.mLowBatteryWarningLevel + this.mContext.getResources().getInteger(17694873);
        lambda$setChargerAcOnline$1(true);
    }

    public final boolean isPoweredLocked(int i) {
        HealthInfo healthInfo = this.mHealthInfo;
        if (healthInfo.batteryStatus == 1) {
            return true;
        }
        if ((i & 1) == 0 || !healthInfo.chargerAcOnline) {
            if ((i & 2) == 0 || !healthInfo.chargerUsbOnline) {
                if ((i & 4) == 0 || !healthInfo.chargerWirelessOnline) {
                    return (i & 8) != 0 && healthInfo.chargerDockOnline;
                }
                return true;
            }
            return true;
        }
        return true;
    }

    public final boolean shouldSendBatteryLowLocked() {
        int i;
        int i2;
        boolean z = this.mPlugType != 0;
        boolean z2 = this.mLastPlugType != 0;
        if (z) {
            return false;
        }
        HealthInfo healthInfo = this.mHealthInfo;
        if (healthInfo.batteryStatus == 1 || (i = healthInfo.batteryLevel) > (i2 = this.mLowBatteryWarningLevel)) {
            return false;
        }
        return z2 || this.mLastBatteryLevel > i2 || i > this.mLastLowBatteryWarningLevel;
    }

    public final boolean shouldShutdownLocked() {
        HealthInfo healthInfo = this.mHealthInfo;
        int i = healthInfo.batteryCapacityLevel;
        return i != -1 ? i == 1 : healthInfo.batteryLevel <= 0 && healthInfo.batteryPresent && healthInfo.batteryStatus != 2;
    }

    public final void shutdownIfNoPowerLocked() {
        if (shouldShutdownLocked()) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.3
                @Override // java.lang.Runnable
                public void run() {
                    if (BatteryService.this.mActivityManagerInternal.isSystemReady()) {
                        Intent intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
                        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        intent.putExtra("android.intent.extra.REASON", "battery");
                        intent.setFlags(268435456);
                        BatteryService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                    }
                }
            });
        }
    }

    public final void shutdownIfOverTempLocked() {
        if (this.mHealthInfo.batteryTemperatureTenthsCelsius > this.mShutdownBatteryTemperature) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.4
                @Override // java.lang.Runnable
                public void run() {
                    if (BatteryService.this.mActivityManagerInternal.isSystemReady()) {
                        Intent intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
                        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                        intent.putExtra("android.intent.extra.REASON", "thermal,battery");
                        intent.setFlags(268435456);
                        BatteryService.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                    }
                }
            });
        }
    }

    public final void update(HealthInfo healthInfo) {
        traceBegin("HealthInfoUpdate");
        Trace.traceCounter(131072L, "BatteryChargeCounter", healthInfo.batteryChargeCounterUah);
        Trace.traceCounter(131072L, "BatteryCurrent", healthInfo.batteryCurrentMicroamps);
        Trace.traceCounter(131072L, "PlugType", plugType(healthInfo));
        Trace.traceCounter(131072L, "BatteryStatus", healthInfo.batteryStatus);
        synchronized (this.mLock) {
            if (!this.mUpdatesStopped) {
                this.mHealthInfo = healthInfo;
                lambda$setChargerAcOnline$1(false);
                this.mLock.notifyAll();
            } else {
                Utils.copyV1Battery(this.mLastHealthInfo, healthInfo);
            }
        }
        traceEnd();
    }

    public static int plugType(HealthInfo healthInfo) {
        if (healthInfo.chargerAcOnline) {
            return 1;
        }
        if (healthInfo.chargerUsbOnline) {
            return 2;
        }
        if (healthInfo.chargerWirelessOnline) {
            return 4;
        }
        return healthInfo.chargerDockOnline ? 8 : 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:104:0x0271  */
    /* JADX WARN: Removed duplicated region for block: B:105:0x028d  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01be  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x01df  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x01f3  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x0206  */
    /* renamed from: processValuesLocked */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void lambda$setChargerAcOnline$1(boolean z) {
        boolean z2;
        long j;
        int i;
        int i2;
        boolean z3;
        int i3;
        HealthInfo healthInfo = this.mHealthInfo;
        this.mBatteryLevelCritical = healthInfo.batteryStatus != 1 && healthInfo.batteryLevel <= this.mCriticalBatteryLevel;
        int plugType = plugType(healthInfo);
        this.mPlugType = plugType;
        try {
            IBatteryStats iBatteryStats = this.mBatteryStats;
            HealthInfo healthInfo2 = this.mHealthInfo;
            iBatteryStats.setBatteryState(healthInfo2.batteryStatus, healthInfo2.batteryHealth, plugType, healthInfo2.batteryLevel, healthInfo2.batteryTemperatureTenthsCelsius, healthInfo2.batteryVoltageMillivolts, healthInfo2.batteryChargeCounterUah, healthInfo2.batteryFullChargeUah, healthInfo2.batteryChargeTimeToFullNowSeconds);
        } catch (RemoteException unused) {
        }
        shutdownIfNoPowerLocked();
        shutdownIfOverTempLocked();
        if (!z) {
            HealthInfo healthInfo3 = this.mHealthInfo;
            if (healthInfo3.batteryStatus == this.mLastBatteryStatus && healthInfo3.batteryHealth == this.mLastBatteryHealth && healthInfo3.batteryPresent == this.mLastBatteryPresent && healthInfo3.batteryLevel == this.mLastBatteryLevel && this.mPlugType == this.mLastPlugType && healthInfo3.batteryVoltageMillivolts == this.mLastBatteryVoltage && healthInfo3.batteryTemperatureTenthsCelsius == this.mLastBatteryTemperature && healthInfo3.maxChargingCurrentMicroamps == this.mLastMaxChargingCurrent && healthInfo3.maxChargingVoltageMicrovolts == this.mLastMaxChargingVoltage && healthInfo3.batteryChargeCounterUah == this.mLastChargeCounter && this.mInvalidCharger == this.mLastInvalidCharger && healthInfo3.batteryCycleCount == this.mLastBatteryCycleCount && healthInfo3.chargingState == this.mLastCharingState) {
                return;
            }
        }
        int i4 = this.mPlugType;
        int i5 = this.mLastPlugType;
        if (i4 != i5) {
            if (i5 == 0) {
                this.mChargeStartLevel = this.mHealthInfo.batteryLevel;
                this.mChargeStartTime = SystemClock.elapsedRealtime();
                LogMaker logMaker = new LogMaker(1417);
                logMaker.setType(4);
                logMaker.addTaggedData(1421, Integer.valueOf(this.mPlugType));
                logMaker.addTaggedData(1418, Integer.valueOf(this.mHealthInfo.batteryLevel));
                this.mMetricsLogger.write(logMaker);
                if (this.mDischargeStartTime != 0 && this.mDischargeStartLevel != this.mHealthInfo.batteryLevel) {
                    j = SystemClock.elapsedRealtime() - this.mDischargeStartTime;
                    EventLog.writeEvent(2730, Long.valueOf(j), Integer.valueOf(this.mDischargeStartLevel), Integer.valueOf(this.mHealthInfo.batteryLevel));
                    this.mDischargeStartTime = 0L;
                    z2 = true;
                    HealthInfo healthInfo4 = this.mHealthInfo;
                    i = healthInfo4.batteryStatus;
                    if (i == this.mLastBatteryStatus || healthInfo4.batteryHealth != this.mLastBatteryHealth || healthInfo4.batteryPresent != this.mLastBatteryPresent || this.mPlugType != this.mLastPlugType) {
                        EventLog.writeEvent(2723, Integer.valueOf(i), Integer.valueOf(this.mHealthInfo.batteryHealth), Integer.valueOf(this.mHealthInfo.batteryPresent ? 1 : 0), Integer.valueOf(this.mPlugType), this.mHealthInfo.batteryTechnology);
                        SystemProperties.set("debug.tracing.battery_status", Integer.toString(this.mHealthInfo.batteryStatus));
                        SystemProperties.set("debug.tracing.plug_type", Integer.toString(this.mPlugType));
                    }
                    i2 = this.mHealthInfo.batteryLevel;
                    if (i2 != this.mLastBatteryLevel) {
                        EventLog.writeEvent(2722, Integer.valueOf(i2), Integer.valueOf(this.mHealthInfo.batteryVoltageMillivolts), Integer.valueOf(this.mHealthInfo.batteryTemperatureTenthsCelsius));
                    }
                    z3 = z2;
                    if (this.mBatteryLevelCritical) {
                        z3 = z2;
                        if (!this.mLastBatteryLevelCritical) {
                            z3 = z2;
                            if (this.mPlugType == 0) {
                                j = SystemClock.elapsedRealtime() - this.mDischargeStartTime;
                                z3 = true;
                            }
                        }
                    }
                    if (this.mBatteryLevelLow) {
                        if (this.mPlugType == 0) {
                            HealthInfo healthInfo5 = this.mHealthInfo;
                            if (healthInfo5.batteryStatus != 1 && healthInfo5.batteryLevel <= this.mLowBatteryWarningLevel) {
                                this.mBatteryLevelLow = true;
                            }
                        }
                    } else if (this.mPlugType != 0) {
                        this.mBatteryLevelLow = false;
                    } else {
                        int i6 = this.mHealthInfo.batteryLevel;
                        if (i6 >= this.mLowBatteryCloseWarningLevel) {
                            this.mBatteryLevelLow = false;
                        } else if (z && i6 >= this.mLowBatteryWarningLevel) {
                            this.mBatteryLevelLow = false;
                        }
                    }
                    this.mSequence++;
                    i3 = this.mPlugType;
                    if (i3 == 0 && this.mLastPlugType == 0) {
                        final Intent intent = new Intent("android.intent.action.ACTION_POWER_CONNECTED");
                        intent.setFlags(67108864);
                        intent.putExtra("seq", this.mSequence);
                        this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.5
                            @Override // java.lang.Runnable
                            public void run() {
                                BatteryService.this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, null, BatteryService.this.mPowerOptions);
                            }
                        });
                    } else if (i3 == 0 && this.mLastPlugType != 0) {
                        final Intent intent2 = new Intent("android.intent.action.ACTION_POWER_DISCONNECTED");
                        intent2.setFlags(67108864);
                        intent2.putExtra("seq", this.mSequence);
                        this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.6
                            @Override // java.lang.Runnable
                            public void run() {
                                BatteryService.this.mContext.sendBroadcastAsUser(intent2, UserHandle.ALL, null, BatteryService.this.mPowerOptions);
                            }
                        });
                    }
                    if (!shouldSendBatteryLowLocked()) {
                        this.mSentLowBatteryBroadcast = true;
                        final Intent intent3 = new Intent("android.intent.action.BATTERY_LOW");
                        intent3.setFlags(67108864);
                        intent3.putExtra("seq", this.mSequence);
                        this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.7
                            @Override // java.lang.Runnable
                            public void run() {
                                BatteryService.this.mContext.sendBroadcastAsUser(intent3, UserHandle.ALL, null, BatteryService.this.mBatteryOptions);
                            }
                        });
                    } else if (this.mSentLowBatteryBroadcast && this.mHealthInfo.batteryLevel >= this.mLowBatteryCloseWarningLevel) {
                        this.mSentLowBatteryBroadcast = false;
                        final Intent intent4 = new Intent("android.intent.action.BATTERY_OKAY");
                        intent4.setFlags(67108864);
                        intent4.putExtra("seq", this.mSequence);
                        this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.8
                            @Override // java.lang.Runnable
                            public void run() {
                                BatteryService.this.mContext.sendBroadcastAsUser(intent4, UserHandle.ALL, null, BatteryService.this.mBatteryOptions);
                            }
                        });
                    }
                    sendBatteryChangedIntentLocked();
                    if (this.mLastBatteryLevel == this.mHealthInfo.batteryLevel || this.mLastPlugType != this.mPlugType) {
                        sendBatteryLevelChangedIntentLocked();
                    }
                    this.mLed.updateLightsLocked();
                    if (z3 && j != 0) {
                        logOutlierLocked(j);
                    }
                    HealthInfo healthInfo6 = this.mHealthInfo;
                    this.mLastBatteryStatus = healthInfo6.batteryStatus;
                    this.mLastBatteryHealth = healthInfo6.batteryHealth;
                    this.mLastBatteryPresent = healthInfo6.batteryPresent;
                    this.mLastBatteryLevel = healthInfo6.batteryLevel;
                    this.mLastPlugType = this.mPlugType;
                    this.mLastBatteryVoltage = healthInfo6.batteryVoltageMillivolts;
                    this.mLastBatteryTemperature = healthInfo6.batteryTemperatureTenthsCelsius;
                    this.mLastMaxChargingCurrent = healthInfo6.maxChargingCurrentMicroamps;
                    this.mLastMaxChargingVoltage = healthInfo6.maxChargingVoltageMicrovolts;
                    this.mLastChargeCounter = healthInfo6.batteryChargeCounterUah;
                    this.mLastBatteryLevelCritical = this.mBatteryLevelCritical;
                    this.mLastInvalidCharger = this.mInvalidCharger;
                    this.mLastBatteryCycleCount = healthInfo6.batteryCycleCount;
                    this.mLastCharingState = healthInfo6.chargingState;
                }
            } else if (i4 == 0) {
                this.mDischargeStartTime = SystemClock.elapsedRealtime();
                this.mDischargeStartLevel = this.mHealthInfo.batteryLevel;
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j2 = this.mChargeStartTime;
                long j3 = elapsedRealtime - j2;
                if (j2 != 0 && j3 != 0) {
                    LogMaker logMaker2 = new LogMaker(1417);
                    logMaker2.setType(5);
                    logMaker2.addTaggedData(1421, Integer.valueOf(this.mLastPlugType));
                    logMaker2.addTaggedData(1420, Long.valueOf(j3));
                    logMaker2.addTaggedData(1418, Integer.valueOf(this.mChargeStartLevel));
                    logMaker2.addTaggedData(1419, Integer.valueOf(this.mHealthInfo.batteryLevel));
                    this.mMetricsLogger.write(logMaker2);
                }
                this.mChargeStartTime = 0L;
            }
        }
        z2 = false;
        j = 0;
        HealthInfo healthInfo42 = this.mHealthInfo;
        i = healthInfo42.batteryStatus;
        if (i == this.mLastBatteryStatus) {
        }
        EventLog.writeEvent(2723, Integer.valueOf(i), Integer.valueOf(this.mHealthInfo.batteryHealth), Integer.valueOf(this.mHealthInfo.batteryPresent ? 1 : 0), Integer.valueOf(this.mPlugType), this.mHealthInfo.batteryTechnology);
        SystemProperties.set("debug.tracing.battery_status", Integer.toString(this.mHealthInfo.batteryStatus));
        SystemProperties.set("debug.tracing.plug_type", Integer.toString(this.mPlugType));
        i2 = this.mHealthInfo.batteryLevel;
        if (i2 != this.mLastBatteryLevel) {
        }
        z3 = z2;
        if (this.mBatteryLevelCritical) {
        }
        if (this.mBatteryLevelLow) {
        }
        this.mSequence++;
        i3 = this.mPlugType;
        if (i3 == 0) {
        }
        if (i3 == 0) {
            final Intent intent22 = new Intent("android.intent.action.ACTION_POWER_DISCONNECTED");
            intent22.setFlags(67108864);
            intent22.putExtra("seq", this.mSequence);
            this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService.6
                @Override // java.lang.Runnable
                public void run() {
                    BatteryService.this.mContext.sendBroadcastAsUser(intent22, UserHandle.ALL, null, BatteryService.this.mPowerOptions);
                }
            });
        }
        if (!shouldSendBatteryLowLocked()) {
        }
        sendBatteryChangedIntentLocked();
        if (this.mLastBatteryLevel == this.mHealthInfo.batteryLevel) {
        }
        sendBatteryLevelChangedIntentLocked();
        this.mLed.updateLightsLocked();
        if (z3) {
            logOutlierLocked(j);
        }
        HealthInfo healthInfo62 = this.mHealthInfo;
        this.mLastBatteryStatus = healthInfo62.batteryStatus;
        this.mLastBatteryHealth = healthInfo62.batteryHealth;
        this.mLastBatteryPresent = healthInfo62.batteryPresent;
        this.mLastBatteryLevel = healthInfo62.batteryLevel;
        this.mLastPlugType = this.mPlugType;
        this.mLastBatteryVoltage = healthInfo62.batteryVoltageMillivolts;
        this.mLastBatteryTemperature = healthInfo62.batteryTemperatureTenthsCelsius;
        this.mLastMaxChargingCurrent = healthInfo62.maxChargingCurrentMicroamps;
        this.mLastMaxChargingVoltage = healthInfo62.maxChargingVoltageMicrovolts;
        this.mLastChargeCounter = healthInfo62.batteryChargeCounterUah;
        this.mLastBatteryLevelCritical = this.mBatteryLevelCritical;
        this.mLastInvalidCharger = this.mInvalidCharger;
        this.mLastBatteryCycleCount = healthInfo62.batteryCycleCount;
        this.mLastCharingState = healthInfo62.chargingState;
    }

    public final void sendBatteryChangedIntentLocked() {
        final Intent intent = new Intent("android.intent.action.BATTERY_CHANGED");
        intent.addFlags(1610612736);
        int iconLocked = getIconLocked(this.mHealthInfo.batteryLevel);
        intent.putExtra("seq", this.mSequence);
        intent.putExtra("status", this.mHealthInfo.batteryStatus);
        intent.putExtra("health", this.mHealthInfo.batteryHealth);
        intent.putExtra("present", this.mHealthInfo.batteryPresent);
        intent.putExtra("level", this.mHealthInfo.batteryLevel);
        intent.putExtra("battery_low", this.mSentLowBatteryBroadcast);
        intent.putExtra("scale", 100);
        intent.putExtra("icon-small", iconLocked);
        intent.putExtra("plugged", this.mPlugType);
        intent.putExtra("voltage", this.mHealthInfo.batteryVoltageMillivolts);
        intent.putExtra("temperature", this.mHealthInfo.batteryTemperatureTenthsCelsius);
        intent.putExtra("technology", this.mHealthInfo.batteryTechnology);
        intent.putExtra("invalid_charger", this.mInvalidCharger);
        intent.putExtra("max_charging_current", this.mHealthInfo.maxChargingCurrentMicroamps);
        intent.putExtra("max_charging_voltage", this.mHealthInfo.maxChargingVoltageMicrovolts);
        intent.putExtra("charge_counter", this.mHealthInfo.batteryChargeCounterUah);
        intent.putExtra("android.os.extra.CYCLE_COUNT", this.mHealthInfo.batteryCycleCount);
        intent.putExtra("android.os.extra.CHARGING_STATUS", this.mHealthInfo.chargingState);
        this.mHandler.post(new Runnable() { // from class: com.android.server.BatteryService$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                BatteryService.this.lambda$sendBatteryChangedIntentLocked$0(intent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendBatteryChangedIntentLocked$0(Intent intent) {
        ActivityManager.broadcastStickyIntent(intent, -1, this.mBatteryChangedOptions, -1);
    }

    public final void sendBatteryLevelChangedIntentLocked() {
        Bundle bundle = new Bundle();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        bundle.putInt("seq", this.mSequence);
        bundle.putInt("status", this.mHealthInfo.batteryStatus);
        bundle.putInt("health", this.mHealthInfo.batteryHealth);
        bundle.putBoolean("present", this.mHealthInfo.batteryPresent);
        bundle.putInt("level", this.mHealthInfo.batteryLevel);
        bundle.putBoolean("battery_low", this.mSentLowBatteryBroadcast);
        bundle.putInt("scale", 100);
        bundle.putInt("plugged", this.mPlugType);
        bundle.putInt("voltage", this.mHealthInfo.batteryVoltageMillivolts);
        bundle.putInt("temperature", this.mHealthInfo.batteryTemperatureTenthsCelsius);
        bundle.putInt("charge_counter", this.mHealthInfo.batteryChargeCounterUah);
        bundle.putLong("android.os.extra.EVENT_TIMESTAMP", elapsedRealtime);
        bundle.putInt("android.os.extra.CYCLE_COUNT", this.mHealthInfo.batteryCycleCount);
        bundle.putInt("android.os.extra.CHARGING_STATUS", this.mHealthInfo.chargingState);
        boolean isEmpty = this.mBatteryLevelsEventQueue.isEmpty();
        this.mBatteryLevelsEventQueue.add(bundle);
        if (this.mBatteryLevelsEventQueue.size() > 100) {
            this.mBatteryLevelsEventQueue.removeFirst();
        }
        if (isEmpty) {
            long j = this.mLastBatteryLevelChangedSentMs;
            this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.BatteryService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BatteryService.this.sendEnqueuedBatteryLevelChangedEvents();
                }
            }, elapsedRealtime - j > 60000 ? 0L : (j + 60000) - elapsedRealtime);
        }
    }

    public final void sendEnqueuedBatteryLevelChangedEvents() {
        ArrayList<? extends Parcelable> arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList<>(this.mBatteryLevelsEventQueue);
            this.mBatteryLevelsEventQueue.clear();
        }
        Intent intent = new Intent("android.intent.action.BATTERY_LEVEL_CHANGED");
        intent.addFlags(16777216);
        intent.putParcelableArrayListExtra("android.os.extra.EVENTS", arrayList);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.BATTERY_STATS");
        this.mLastBatteryLevelChangedSentMs = SystemClock.elapsedRealtime();
    }

    public final void logBatteryStatsLocked() {
        DropBoxManager dropBoxManager;
        File file;
        String str;
        StringBuilder sb;
        FileOutputStream fileOutputStream;
        IBinder service = ServiceManager.getService("batterystats");
        if (service == null || (dropBoxManager = (DropBoxManager) this.mContext.getSystemService("dropbox")) == null || !dropBoxManager.isTagEnabled("BATTERY_DISCHARGE_INFO")) {
            return;
        }
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                file = new File("/data/system/batterystats.dump");
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (RemoteException e) {
                    e = e;
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (Throwable th) {
                th = th;
            }
        } catch (RemoteException e3) {
            e = e3;
            file = null;
        } catch (IOException e4) {
            e = e4;
            file = null;
        } catch (Throwable th2) {
            th = th2;
            file = null;
        }
        try {
            service.dump(fileOutputStream.getFD(), DUMPSYS_ARGS);
            FileUtils.sync(fileOutputStream);
            dropBoxManager.addFile("BATTERY_DISCHARGE_INFO", file, 2);
            try {
                fileOutputStream.close();
            } catch (IOException unused) {
                Slog.e(TAG, "failed to close dumpsys output stream");
            }
        } catch (RemoteException e5) {
            e = e5;
            fileOutputStream2 = fileOutputStream;
            Slog.e(TAG, "failed to dump battery service", e);
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException unused2) {
                    Slog.e(TAG, "failed to close dumpsys output stream");
                }
            }
            if (file == null || file.delete()) {
                return;
            }
            str = TAG;
            sb = new StringBuilder();
            sb.append("failed to delete temporary dumpsys file: ");
            sb.append(file.getAbsolutePath());
            Slog.e(str, sb.toString());
        } catch (IOException e6) {
            e = e6;
            fileOutputStream2 = fileOutputStream;
            Slog.e(TAG, "failed to write dumpsys file", e);
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException unused3) {
                    Slog.e(TAG, "failed to close dumpsys output stream");
                }
            }
            if (file == null || file.delete()) {
                return;
            }
            str = TAG;
            sb = new StringBuilder();
            sb.append("failed to delete temporary dumpsys file: ");
            sb.append(file.getAbsolutePath());
            Slog.e(str, sb.toString());
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException unused4) {
                    Slog.e(TAG, "failed to close dumpsys output stream");
                }
            }
            if (file != null && !file.delete()) {
                Slog.e(TAG, "failed to delete temporary dumpsys file: " + file.getAbsolutePath());
            }
            throw th;
        }
        if (file.delete()) {
            return;
        }
        str = TAG;
        sb = new StringBuilder();
        sb.append("failed to delete temporary dumpsys file: ");
        sb.append(file.getAbsolutePath());
        Slog.e(str, sb.toString());
    }

    public final void logOutlierLocked(long j) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String string = Settings.Global.getString(contentResolver, "battery_discharge_threshold");
        String string2 = Settings.Global.getString(contentResolver, "battery_discharge_duration_threshold");
        if (string == null || string2 == null) {
            return;
        }
        try {
            long parseLong = Long.parseLong(string2);
            int parseInt = Integer.parseInt(string);
            if (j > parseLong || this.mDischargeStartLevel - this.mHealthInfo.batteryLevel < parseInt) {
                return;
            }
            logBatteryStatsLocked();
        } catch (NumberFormatException unused) {
            String str = TAG;
            Slog.e(str, "Invalid DischargeThresholds GService string: " + string2 + " or " + string);
        }
    }

    public final int getIconLocked(int i) {
        int i2 = this.mHealthInfo.batteryStatus;
        if (i2 == 2) {
            return 17303611;
        }
        if (i2 == 3) {
            return 17303597;
        }
        if (i2 == 4 || i2 == 5) {
            return (!isPoweredLocked(15) || this.mHealthInfo.batteryLevel < 100) ? 17303597 : 17303611;
        }
        return 17303625;
    }

    /* loaded from: classes.dex */
    public class Shell extends ShellCommand {
        public Shell() {
        }

        public int onCommand(String str) {
            return BatteryService.this.onShellCommand(this, str);
        }

        public void onHelp() {
            BatteryService.dumpHelp(getOutPrintWriter());
        }
    }

    public static void dumpHelp(PrintWriter printWriter) {
        printWriter.println("Battery service (battery) commands:");
        printWriter.println("  help");
        printWriter.println("    Print this help text.");
        printWriter.println("  get [-f] [ac|usb|wireless|dock|status|level|temp|present|counter|invalid]");
        printWriter.println("  set [-f] [ac|usb|wireless|dock|status|level|temp|present|counter|invalid] <value>");
        printWriter.println("    Force a battery property value, freezing battery state.");
        printWriter.println("    -f: force a battery change broadcast be sent, prints new sequence.");
        printWriter.println("  unplug [-f]");
        printWriter.println("    Force battery unplugged, freezing battery state.");
        printWriter.println("    -f: force a battery change broadcast be sent, prints new sequence.");
        printWriter.println("  reset [-f]");
        printWriter.println("    Unfreeze battery state, returning to current hardware values.");
        printWriter.println("    -f: force a battery change broadcast be sent, prints new sequence.");
        if (Build.IS_DEBUGGABLE) {
            printWriter.println("  suspend_input");
            printWriter.println("    Suspend charging even if plugged in. ");
        }
    }

    public int parseOptions(Shell shell) {
        int i = 0;
        while (true) {
            String nextOption = shell.getNextOption();
            if (nextOption == null) {
                return i;
            }
            if ("-f".equals(nextOption)) {
                i |= 1;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public int onShellCommand(Shell shell, String str) {
        char c;
        char c2;
        int i;
        char c3;
        boolean z;
        if (str == null) {
            return shell.handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = shell.getOutPrintWriter();
        switch (str.hashCode()) {
            case -840325209:
                if (str.equals("unplug")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -541966841:
                if (str.equals("suspend_input")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 102230:
                if (str.equals("get")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 113762:
                if (str.equals("set")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 108404047:
                if (str.equals("reset")) {
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
                int parseOptions = parseOptions(shell);
                getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
                unplugBattery((parseOptions & 1) != 0, outPrintWriter);
                return 0;
            case 1:
                getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
                suspendBatteryInput();
                return 0;
            case 2:
                String nextArg = shell.getNextArg();
                if (nextArg == null) {
                    outPrintWriter.println("No property specified");
                    return -1;
                }
                switch (nextArg.hashCode()) {
                    case -1000044642:
                        if (nextArg.equals("wireless")) {
                            c2 = 0;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case -892481550:
                        if (nextArg.equals("status")) {
                            c2 = 1;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case -318277445:
                        if (nextArg.equals("present")) {
                            c2 = 2;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 3106:
                        if (nextArg.equals("ac")) {
                            c2 = 3;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 116100:
                        if (nextArg.equals("usb")) {
                            c2 = 4;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 3088947:
                        if (nextArg.equals("dock")) {
                            c2 = 5;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 3556308:
                        if (nextArg.equals("temp")) {
                            c2 = 6;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 102865796:
                        if (nextArg.equals("level")) {
                            c2 = 7;
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 957830652:
                        if (nextArg.equals("counter")) {
                            c2 = '\b';
                            break;
                        }
                        c2 = 65535;
                        break;
                    case 1959784951:
                        if (nextArg.equals("invalid")) {
                            c2 = '\t';
                            break;
                        }
                        c2 = 65535;
                        break;
                    default:
                        c2 = 65535;
                        break;
                }
                switch (c2) {
                    case 0:
                        outPrintWriter.println(this.mHealthInfo.chargerWirelessOnline);
                        return 0;
                    case 1:
                        outPrintWriter.println(this.mHealthInfo.batteryStatus);
                        return 0;
                    case 2:
                        outPrintWriter.println(this.mHealthInfo.batteryPresent);
                        return 0;
                    case 3:
                        outPrintWriter.println(this.mHealthInfo.chargerAcOnline);
                        return 0;
                    case 4:
                        outPrintWriter.println(this.mHealthInfo.chargerUsbOnline);
                        return 0;
                    case 5:
                        outPrintWriter.println(this.mHealthInfo.chargerDockOnline);
                        return 0;
                    case 6:
                        outPrintWriter.println(this.mHealthInfo.batteryTemperatureTenthsCelsius);
                        return 0;
                    case 7:
                        outPrintWriter.println(this.mHealthInfo.batteryLevel);
                        return 0;
                    case '\b':
                        outPrintWriter.println(this.mHealthInfo.batteryChargeCounterUah);
                        return 0;
                    case '\t':
                        outPrintWriter.println(this.mInvalidCharger);
                        return 0;
                    default:
                        outPrintWriter.println("Unknown get option: " + nextArg);
                        return 0;
                }
            case 3:
                int parseOptions2 = parseOptions(shell);
                getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
                String nextArg2 = shell.getNextArg();
                if (nextArg2 == null) {
                    outPrintWriter.println("No property specified");
                    return -1;
                }
                String nextArg3 = shell.getNextArg();
                if (nextArg3 == null) {
                    outPrintWriter.println("No value specified");
                    return -1;
                }
                try {
                    if (this.mUpdatesStopped) {
                        i = parseOptions2;
                    } else {
                        i = parseOptions2;
                        Utils.copyV1Battery(this.mLastHealthInfo, this.mHealthInfo);
                    }
                    switch (nextArg2.hashCode()) {
                        case -1000044642:
                            if (nextArg2.equals("wireless")) {
                                c3 = 3;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case -892481550:
                            if (nextArg2.equals("status")) {
                                c3 = 5;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case -318277445:
                            if (nextArg2.equals("present")) {
                                c3 = 0;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 3106:
                            if (nextArg2.equals("ac")) {
                                c3 = 1;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 116100:
                            if (nextArg2.equals("usb")) {
                                c3 = 2;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 3088947:
                            if (nextArg2.equals("dock")) {
                                c3 = 4;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 3556308:
                            if (nextArg2.equals("temp")) {
                                c3 = '\b';
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 102865796:
                            if (nextArg2.equals("level")) {
                                c3 = 6;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 957830652:
                            if (nextArg2.equals("counter")) {
                                c3 = 7;
                                break;
                            }
                            c3 = 65535;
                            break;
                        case 1959784951:
                            if (nextArg2.equals("invalid")) {
                                c3 = '\t';
                                break;
                            }
                            c3 = 65535;
                            break;
                        default:
                            c3 = 65535;
                            break;
                    }
                    switch (c3) {
                        case 0:
                            this.mHealthInfo.batteryPresent = Integer.parseInt(nextArg3) != 0;
                            z = true;
                            break;
                        case 1:
                            this.mHealthInfo.chargerAcOnline = Integer.parseInt(nextArg3) != 0;
                            z = true;
                            break;
                        case 2:
                            this.mHealthInfo.chargerUsbOnline = Integer.parseInt(nextArg3) != 0;
                            z = true;
                            break;
                        case 3:
                            this.mHealthInfo.chargerWirelessOnline = Integer.parseInt(nextArg3) != 0;
                            z = true;
                            break;
                        case 4:
                            this.mHealthInfo.chargerDockOnline = Integer.parseInt(nextArg3) != 0;
                            z = true;
                            break;
                        case 5:
                            this.mHealthInfo.batteryStatus = Integer.parseInt(nextArg3);
                            z = true;
                            break;
                        case 6:
                            this.mHealthInfo.batteryLevel = Integer.parseInt(nextArg3);
                            z = true;
                            break;
                        case 7:
                            this.mHealthInfo.batteryChargeCounterUah = Integer.parseInt(nextArg3);
                            z = true;
                            break;
                        case '\b':
                            this.mHealthInfo.batteryTemperatureTenthsCelsius = Integer.parseInt(nextArg3);
                            z = true;
                            break;
                        case '\t':
                            this.mInvalidCharger = Integer.parseInt(nextArg3);
                            z = true;
                            break;
                        default:
                            outPrintWriter.println("Unknown set option: " + nextArg2);
                            z = false;
                            break;
                    }
                    if (z) {
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        this.mUpdatesStopped = true;
                        lambda$unplugBattery$3((i & 1) != 0, outPrintWriter);
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return 0;
                    }
                    return 0;
                } catch (NumberFormatException unused) {
                    outPrintWriter.println("Bad value: " + nextArg3);
                    return -1;
                }
            case 4:
                int parseOptions3 = parseOptions(shell);
                getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
                resetBattery((parseOptions3 & 1) != 0, outPrintWriter);
                return 0;
            default:
                return shell.handleDefaultCommands(str);
        }
    }

    public final void setChargerAcOnline(boolean z, final boolean z2) {
        if (!this.mUpdatesStopped) {
            Utils.copyV1Battery(this.mLastHealthInfo, this.mHealthInfo);
        }
        this.mHealthInfo.chargerAcOnline = z;
        this.mUpdatesStopped = true;
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.BatteryService$$ExternalSyntheticLambda6
            public final void runOrThrow() {
                BatteryService.this.lambda$setChargerAcOnline$1(z2);
            }
        });
    }

    public final void setBatteryLevel(int i, final boolean z) {
        if (!this.mUpdatesStopped) {
            Utils.copyV1Battery(this.mLastHealthInfo, this.mHealthInfo);
        }
        this.mHealthInfo.batteryLevel = i;
        this.mUpdatesStopped = true;
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.BatteryService$$ExternalSyntheticLambda5
            public final void runOrThrow() {
                BatteryService.this.lambda$setBatteryLevel$2(z);
            }
        });
    }

    public final void unplugBattery(final boolean z, final PrintWriter printWriter) {
        if (!this.mUpdatesStopped) {
            Utils.copyV1Battery(this.mLastHealthInfo, this.mHealthInfo);
        }
        HealthInfo healthInfo = this.mHealthInfo;
        healthInfo.chargerAcOnline = false;
        healthInfo.chargerUsbOnline = false;
        healthInfo.chargerWirelessOnline = false;
        healthInfo.chargerDockOnline = false;
        this.mUpdatesStopped = true;
        Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.BatteryService$$ExternalSyntheticLambda4
            public final void runOrThrow() {
                BatteryService.this.lambda$unplugBattery$3(z, printWriter);
            }
        });
    }

    public final void resetBattery(final boolean z, final PrintWriter printWriter) {
        if (this.mUpdatesStopped) {
            this.mUpdatesStopped = false;
            Utils.copyV1Battery(this.mHealthInfo, this.mLastHealthInfo);
            Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.BatteryService$$ExternalSyntheticLambda2
                public final void runOrThrow() {
                    BatteryService.this.lambda$resetBattery$4(z, printWriter);
                }
            });
        }
        if (this.mBatteryInputSuspended) {
            PowerProperties.battery_input_suspended(Boolean.FALSE);
            this.mBatteryInputSuspended = false;
        }
    }

    public final void suspendBatteryInput() {
        if (!Build.IS_DEBUGGABLE) {
            throw new SecurityException("battery suspend_input is only supported on debuggable builds");
        }
        PowerProperties.battery_input_suspended(Boolean.TRUE);
        this.mBatteryInputSuspended = true;
    }

    /* renamed from: processValuesLocked */
    public final void lambda$unplugBattery$3(boolean z, PrintWriter printWriter) {
        lambda$setChargerAcOnline$1(z);
        if (printWriter == null || !z) {
            return;
        }
        printWriter.println(this.mSequence);
    }

    public final void dumpInternal(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        synchronized (this.mLock) {
            if (strArr != null) {
                if (strArr.length != 0 && !"-a".equals(strArr[0])) {
                    new Shell().exec(this.mBinderService, (FileDescriptor) null, fileDescriptor, (FileDescriptor) null, strArr, (ShellCallback) null, new ResultReceiver(null));
                }
            }
            printWriter.println("Current Battery Service state:");
            if (this.mUpdatesStopped) {
                printWriter.println("  (UPDATES STOPPED -- use 'reset' to restart)");
            }
            printWriter.println("  AC powered: " + this.mHealthInfo.chargerAcOnline);
            printWriter.println("  USB powered: " + this.mHealthInfo.chargerUsbOnline);
            printWriter.println("  Wireless powered: " + this.mHealthInfo.chargerWirelessOnline);
            printWriter.println("  Dock powered: " + this.mHealthInfo.chargerDockOnline);
            printWriter.println("  Max charging current: " + this.mHealthInfo.maxChargingCurrentMicroamps);
            printWriter.println("  Max charging voltage: " + this.mHealthInfo.maxChargingVoltageMicrovolts);
            printWriter.println("  Charge counter: " + this.mHealthInfo.batteryChargeCounterUah);
            printWriter.println("  status: " + this.mHealthInfo.batteryStatus);
            printWriter.println("  health: " + this.mHealthInfo.batteryHealth);
            printWriter.println("  present: " + this.mHealthInfo.batteryPresent);
            printWriter.println("  level: " + this.mHealthInfo.batteryLevel);
            printWriter.println("  scale: 100");
            printWriter.println("  voltage: " + this.mHealthInfo.batteryVoltageMillivolts);
            printWriter.println("  temperature: " + this.mHealthInfo.batteryTemperatureTenthsCelsius);
            printWriter.println("  technology: " + this.mHealthInfo.batteryTechnology);
        }
    }

    public final void dumpProto(FileDescriptor fileDescriptor) {
        int i;
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        synchronized (this.mLock) {
            protoOutputStream.write(1133871366145L, this.mUpdatesStopped);
            HealthInfo healthInfo = this.mHealthInfo;
            if (healthInfo.chargerAcOnline) {
                i = 1;
            } else if (healthInfo.chargerUsbOnline) {
                i = 2;
            } else if (healthInfo.chargerWirelessOnline) {
                i = 4;
            } else {
                i = healthInfo.chargerDockOnline ? 8 : 0;
            }
            protoOutputStream.write(1159641169922L, i);
            protoOutputStream.write(1120986464259L, this.mHealthInfo.maxChargingCurrentMicroamps);
            protoOutputStream.write(1120986464260L, this.mHealthInfo.maxChargingVoltageMicrovolts);
            protoOutputStream.write(1120986464261L, this.mHealthInfo.batteryChargeCounterUah);
            protoOutputStream.write(1159641169926L, this.mHealthInfo.batteryStatus);
            protoOutputStream.write(1159641169927L, this.mHealthInfo.batteryHealth);
            protoOutputStream.write(1133871366152L, this.mHealthInfo.batteryPresent);
            protoOutputStream.write(1120986464265L, this.mHealthInfo.batteryLevel);
            protoOutputStream.write(1120986464266L, 100);
            protoOutputStream.write(1120986464267L, this.mHealthInfo.batteryVoltageMillivolts);
            protoOutputStream.write(1120986464268L, this.mHealthInfo.batteryTemperatureTenthsCelsius);
            protoOutputStream.write(1138166333453L, this.mHealthInfo.batteryTechnology);
        }
        protoOutputStream.flush();
    }

    public static void traceBegin(String str) {
        Trace.traceBegin(524288L, str);
    }

    public static void traceEnd() {
        Trace.traceEnd(524288L);
    }

    /* loaded from: classes.dex */
    public final class Led {
        public final int mBatteryFullARGB;
        public final int mBatteryLedOff;
        public final int mBatteryLedOn;
        public final LogicalLight mBatteryLight;
        public final int mBatteryLowARGB;
        public final int mBatteryLowBehavior;
        public final int mBatteryMediumARGB;

        public Led(Context context, LightsManager lightsManager) {
            this.mBatteryLight = lightsManager.getLight(3);
            this.mBatteryLowARGB = context.getResources().getInteger(17694917);
            this.mBatteryMediumARGB = context.getResources().getInteger(17694919);
            this.mBatteryFullARGB = context.getResources().getInteger(17694914);
            this.mBatteryLedOn = context.getResources().getInteger(17694916);
            this.mBatteryLedOff = context.getResources().getInteger(17694915);
            BatteryService.this.mBatteryNearlyFullLevel = context.getResources().getInteger(17694920);
            this.mBatteryLowBehavior = context.getResources().getInteger(17694918);
        }

        public void updateLightsLocked() {
            if (this.mBatteryLight == null) {
                return;
            }
            int i = BatteryService.this.mHealthInfo.batteryLevel;
            int i2 = BatteryService.this.mHealthInfo.batteryStatus;
            if (i >= BatteryService.this.mLowBatteryWarningLevel) {
                if (i2 == 2 || i2 == 5) {
                    if (i2 == 5 || i >= BatteryService.this.mBatteryNearlyFullLevel) {
                        this.mBatteryLight.setColor(this.mBatteryFullARGB);
                        return;
                    } else {
                        this.mBatteryLight.setColor(this.mBatteryMediumARGB);
                        return;
                    }
                }
                this.mBatteryLight.turnOff();
                return;
            }
            int i3 = this.mBatteryLowBehavior;
            if (i3 == 1) {
                this.mBatteryLight.setColor(this.mBatteryLowARGB);
            } else if (i3 == 2) {
                this.mBatteryLight.setFlashing(this.mBatteryLowARGB, 1, this.mBatteryLedOn, this.mBatteryLedOff);
            } else if (i2 == 2) {
                this.mBatteryLight.setColor(this.mBatteryLowARGB);
            } else {
                this.mBatteryLight.setFlashing(this.mBatteryLowARGB, 1, this.mBatteryLedOn, this.mBatteryLedOff);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class BinderService extends Binder {
        public BinderService() {
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(BatteryService.this.mContext, BatteryService.TAG, printWriter)) {
                if (strArr.length > 0 && "--proto".equals(strArr[0])) {
                    BatteryService.this.dumpProto(fileDescriptor);
                } else {
                    BatteryService.this.dumpInternal(fileDescriptor, printWriter, strArr);
                }
            }
        }

        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new Shell().exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }

    /* loaded from: classes.dex */
    public final class BatteryPropertiesRegistrar extends IBatteryPropertiesRegistrar.Stub {
        public BatteryPropertiesRegistrar() {
        }

        public int getProperty(int i, BatteryProperty batteryProperty) throws RemoteException {
            switch (i) {
                case 7:
                case 8:
                case 9:
                case 10:
                    BatteryService.this.mContext.enforceCallingPermission("android.permission.BATTERY_STATS", null);
                    break;
            }
            return BatteryService.this.mHealthServiceWrapper.getProperty(i, batteryProperty);
        }

        public void scheduleUpdate() throws RemoteException {
            BatteryService.this.mHealthServiceWrapper.scheduleUpdate();
        }
    }

    /* loaded from: classes.dex */
    public final class LocalService extends BatteryManagerInternal {
        public LocalService() {
        }

        public boolean isPowered(int i) {
            boolean isPoweredLocked;
            synchronized (BatteryService.this.mLock) {
                isPoweredLocked = BatteryService.this.isPoweredLocked(i);
            }
            return isPoweredLocked;
        }

        public int getPlugType() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mPlugType;
            }
            return i;
        }

        public int getBatteryLevel() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mHealthInfo.batteryLevel;
            }
            return i;
        }

        public int getBatteryChargeCounter() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mHealthInfo.batteryChargeCounterUah;
            }
            return i;
        }

        public int getBatteryFullCharge() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mHealthInfo.batteryFullChargeUah;
            }
            return i;
        }

        public int getBatteryHealth() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mHealthInfo.batteryHealth;
            }
            return i;
        }

        public boolean getBatteryLevelLow() {
            boolean z;
            synchronized (BatteryService.this.mLock) {
                z = BatteryService.this.mBatteryLevelLow;
            }
            return z;
        }

        public int getInvalidCharger() {
            int i;
            synchronized (BatteryService.this.mLock) {
                i = BatteryService.this.mInvalidCharger;
            }
            return i;
        }

        public void setChargerAcOnline(boolean z, boolean z2) {
            BatteryService.this.setChargerAcOnline(z, z2);
        }

        public void setBatteryLevel(int i, boolean z) {
            BatteryService.this.setBatteryLevel(i, z);
        }

        public void unplugBattery(boolean z) {
            BatteryService.this.unplugBattery(z, null);
        }

        public void resetBattery(boolean z) {
            BatteryService.this.resetBattery(z, null);
        }

        public void suspendBatteryInput() {
            BatteryService.this.suspendBatteryInput();
        }
    }
}
