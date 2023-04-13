package com.android.server;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.MutableBoolean;
import android.util.Slog;
import android.view.KeyEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.server.p014wm.WindowManagerInternal;
import com.android.server.statusbar.StatusBarManagerInternal;
/* loaded from: classes.dex */
public class GestureLauncherService extends SystemService {
    @VisibleForTesting
    static final long CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS = 300;
    @VisibleForTesting
    static final int EMERGENCY_GESTURE_POWER_BUTTON_COOLDOWN_PERIOD_MS_MAX = 5000;
    @VisibleForTesting
    static final int EMERGENCY_GESTURE_TAP_DETECTION_MIN_TIME_MS = 200;
    @VisibleForTesting
    static final long POWER_SHORT_TAP_SEQUENCE_MAX_INTERVAL_MS = 500;
    public boolean mCameraDoubleTapPowerEnabled;
    public long mCameraGestureLastEventTime;
    public long mCameraGestureOnTimeMs;
    public long mCameraGestureSensor1LastOnTimeMs;
    public long mCameraGestureSensor2LastOnTimeMs;
    public int mCameraLaunchLastEventExtra;
    public boolean mCameraLaunchRegistered;
    public Sensor mCameraLaunchSensor;
    public boolean mCameraLiftRegistered;
    public final CameraLiftTriggerEventListener mCameraLiftTriggerListener;
    public Sensor mCameraLiftTriggerSensor;
    public Context mContext;
    public boolean mEmergencyGestureEnabled;
    public int mEmergencyGesturePowerButtonCooldownPeriodMs;
    public long mFirstPowerDown;
    public final GestureEventListener mGestureListener;
    public boolean mHasFeatureWatch;
    public long mLastEmergencyGestureTriggered;
    public long mLastPowerDown;
    public final MetricsLogger mMetricsLogger;
    public int mPowerButtonConsecutiveTaps;
    public int mPowerButtonSlowConsecutiveTaps;
    public PowerManager mPowerManager;
    public final ContentObserver mSettingObserver;
    public final UiEventLogger mUiEventLogger;
    public int mUserId;
    public final BroadcastReceiver mUserReceiver;
    public long mVibrateMilliSecondsForPanicGesture;
    public PowerManager.WakeLock mWakeLock;
    public WindowManagerInternal mWindowManagerInternal;

    @VisibleForTesting
    /* loaded from: classes.dex */
    public enum GestureLauncherEvent implements UiEventLogger.UiEventEnum {
        GESTURE_CAMERA_LIFT(658),
        GESTURE_CAMERA_WIGGLE(659),
        GESTURE_CAMERA_DOUBLE_TAP_POWER(660),
        GESTURE_EMERGENCY_TAP_POWER(661);
        
        private final int mId;

        GestureLauncherEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    public GestureLauncherService(Context context) {
        this(context, new MetricsLogger(), new UiEventLoggerImpl());
    }

    @VisibleForTesting
    public GestureLauncherService(Context context, MetricsLogger metricsLogger, UiEventLogger uiEventLogger) {
        super(context);
        this.mGestureListener = new GestureEventListener();
        this.mCameraLiftTriggerListener = new CameraLiftTriggerEventListener();
        this.mCameraGestureOnTimeMs = 0L;
        this.mCameraGestureLastEventTime = 0L;
        this.mCameraGestureSensor1LastOnTimeMs = 0L;
        this.mCameraGestureSensor2LastOnTimeMs = 0L;
        this.mCameraLaunchLastEventExtra = 0;
        this.mUserReceiver = new BroadcastReceiver() { // from class: com.android.server.GestureLauncherService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                    GestureLauncherService.this.mUserId = intent.getIntExtra("android.intent.extra.user_handle", 0);
                    GestureLauncherService.this.mContext.getContentResolver().unregisterContentObserver(GestureLauncherService.this.mSettingObserver);
                    GestureLauncherService.this.registerContentObservers();
                    GestureLauncherService.this.updateCameraRegistered();
                    GestureLauncherService.this.updateCameraDoubleTapPowerEnabled();
                    GestureLauncherService.this.updateEmergencyGestureEnabled();
                    GestureLauncherService.this.updateEmergencyGesturePowerButtonCooldownPeriodMs();
                }
            }
        };
        this.mSettingObserver = new ContentObserver(new Handler()) { // from class: com.android.server.GestureLauncherService.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri, int i) {
                if (i == GestureLauncherService.this.mUserId) {
                    GestureLauncherService.this.updateCameraRegistered();
                    GestureLauncherService.this.updateCameraDoubleTapPowerEnabled();
                    GestureLauncherService.this.updateEmergencyGestureEnabled();
                    GestureLauncherService.this.updateEmergencyGesturePowerButtonCooldownPeriodMs();
                }
            }
        };
        this.mContext = context;
        this.mMetricsLogger = metricsLogger;
        this.mUiEventLogger = uiEventLogger;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        LocalServices.addService(GestureLauncherService.class, this);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 600) {
            Resources resources = this.mContext.getResources();
            if (isGestureLauncherEnabled(resources)) {
                this.mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
                PowerManager powerManager = (PowerManager) this.mContext.getSystemService("power");
                this.mPowerManager = powerManager;
                this.mWakeLock = powerManager.newWakeLock(1, "GestureLauncherService");
                updateCameraRegistered();
                updateCameraDoubleTapPowerEnabled();
                updateEmergencyGestureEnabled();
                updateEmergencyGesturePowerButtonCooldownPeriodMs();
                this.mUserId = ActivityManager.getCurrentUser();
                this.mContext.registerReceiver(this.mUserReceiver, new IntentFilter("android.intent.action.USER_SWITCHED"));
                registerContentObservers();
                this.mHasFeatureWatch = this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch");
                this.mVibrateMilliSecondsForPanicGesture = resources.getInteger(17694878);
            }
        }
    }

    public final void registerContentObservers() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("camera_gesture_disabled"), false, this.mSettingObserver, this.mUserId);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("camera_double_tap_power_gesture_disabled"), false, this.mSettingObserver, this.mUserId);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("camera_lift_trigger_enabled"), false, this.mSettingObserver, this.mUserId);
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("emergency_gesture_enabled"), false, this.mSettingObserver, this.mUserId);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("emergency_gesture_power_button_cooldown_period_ms"), false, this.mSettingObserver, this.mUserId);
    }

    public final void updateCameraRegistered() {
        Resources resources = this.mContext.getResources();
        if (isCameraLaunchSettingEnabled(this.mContext, this.mUserId)) {
            registerCameraLaunchGesture(resources);
        } else {
            unregisterCameraLaunchGesture();
        }
        if (isCameraLiftTriggerSettingEnabled(this.mContext, this.mUserId)) {
            registerCameraLiftTrigger(resources);
        } else {
            unregisterCameraLiftTrigger();
        }
    }

    @VisibleForTesting
    public void updateCameraDoubleTapPowerEnabled() {
        boolean isCameraDoubleTapPowerSettingEnabled = isCameraDoubleTapPowerSettingEnabled(this.mContext, this.mUserId);
        synchronized (this) {
            this.mCameraDoubleTapPowerEnabled = isCameraDoubleTapPowerSettingEnabled;
        }
    }

    @VisibleForTesting
    public void updateEmergencyGestureEnabled() {
        boolean isEmergencyGestureSettingEnabled = isEmergencyGestureSettingEnabled(this.mContext, this.mUserId);
        synchronized (this) {
            this.mEmergencyGestureEnabled = isEmergencyGestureSettingEnabled;
        }
    }

    @VisibleForTesting
    public void updateEmergencyGesturePowerButtonCooldownPeriodMs() {
        int emergencyGesturePowerButtonCooldownPeriodMs = getEmergencyGesturePowerButtonCooldownPeriodMs(this.mContext, this.mUserId);
        synchronized (this) {
            this.mEmergencyGesturePowerButtonCooldownPeriodMs = emergencyGesturePowerButtonCooldownPeriodMs;
        }
    }

    public final void unregisterCameraLaunchGesture() {
        if (this.mCameraLaunchRegistered) {
            this.mCameraLaunchRegistered = false;
            this.mCameraGestureOnTimeMs = 0L;
            this.mCameraGestureLastEventTime = 0L;
            this.mCameraGestureSensor1LastOnTimeMs = 0L;
            this.mCameraGestureSensor2LastOnTimeMs = 0L;
            this.mCameraLaunchLastEventExtra = 0;
            ((SensorManager) this.mContext.getSystemService("sensor")).unregisterListener(this.mGestureListener);
        }
    }

    public final void registerCameraLaunchGesture(Resources resources) {
        if (this.mCameraLaunchRegistered) {
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mCameraGestureOnTimeMs = elapsedRealtime;
        this.mCameraGestureLastEventTime = elapsedRealtime;
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService("sensor");
        int integer = resources.getInteger(17694770);
        if (integer != -1) {
            this.mCameraLaunchRegistered = false;
            String string = resources.getString(17039845);
            Sensor defaultSensor = sensorManager.getDefaultSensor(integer, true);
            this.mCameraLaunchSensor = defaultSensor;
            if (defaultSensor != null) {
                if (string.equals(defaultSensor.getStringType())) {
                    this.mCameraLaunchRegistered = sensorManager.registerListener(this.mGestureListener, this.mCameraLaunchSensor, 0);
                    return;
                }
                throw new RuntimeException(String.format("Wrong configuration. Sensor type and sensor string type don't match: %s in resources, %s in the sensor.", string, this.mCameraLaunchSensor.getStringType()));
            }
        }
    }

    public final void unregisterCameraLiftTrigger() {
        if (this.mCameraLiftRegistered) {
            this.mCameraLiftRegistered = false;
            ((SensorManager) this.mContext.getSystemService("sensor")).cancelTriggerSensor(this.mCameraLiftTriggerListener, this.mCameraLiftTriggerSensor);
        }
    }

    public final void registerCameraLiftTrigger(Resources resources) {
        if (this.mCameraLiftRegistered) {
            return;
        }
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService("sensor");
        int integer = resources.getInteger(17694771);
        if (integer != -1) {
            this.mCameraLiftRegistered = false;
            String string = resources.getString(17039846);
            Sensor defaultSensor = sensorManager.getDefaultSensor(integer, true);
            this.mCameraLiftTriggerSensor = defaultSensor;
            if (defaultSensor != null) {
                if (string.equals(defaultSensor.getStringType())) {
                    this.mCameraLiftRegistered = sensorManager.requestTriggerSensor(this.mCameraLiftTriggerListener, this.mCameraLiftTriggerSensor);
                    return;
                }
                throw new RuntimeException(String.format("Wrong configuration. Sensor type and sensor string type don't match: %s in resources, %s in the sensor.", string, this.mCameraLiftTriggerSensor.getStringType()));
            }
        }
    }

    public static boolean isCameraLaunchSettingEnabled(Context context, int i) {
        return isCameraLaunchEnabled(context.getResources()) && Settings.Secure.getIntForUser(context.getContentResolver(), "camera_gesture_disabled", 0, i) == 0;
    }

    public static boolean isCameraDoubleTapPowerSettingEnabled(Context context, int i) {
        return isCameraDoubleTapPowerEnabled(context.getResources()) && Settings.Secure.getIntForUser(context.getContentResolver(), "camera_double_tap_power_gesture_disabled", 0, i) == 0;
    }

    public static boolean isCameraLiftTriggerSettingEnabled(Context context, int i) {
        return isCameraLiftTriggerEnabled(context.getResources()) && Settings.Secure.getIntForUser(context.getContentResolver(), "camera_lift_trigger_enabled", 1, i) != 0;
    }

    public static boolean isEmergencyGestureSettingEnabled(Context context, int i) {
        return isEmergencyGestureEnabled(context.getResources()) && Settings.Secure.getIntForUser(context.getContentResolver(), "emergency_gesture_enabled", isDefaultEmergencyGestureEnabled(context.getResources()) ? 1 : 0, i) != 0;
    }

    @VisibleForTesting
    public static int getEmergencyGesturePowerButtonCooldownPeriodMs(Context context, int i) {
        return Math.min(Settings.Global.getInt(context.getContentResolver(), "emergency_gesture_power_button_cooldown_period_ms", 3000), (int) EMERGENCY_GESTURE_POWER_BUTTON_COOLDOWN_PERIOD_MS_MAX);
    }

    public static boolean isCameraLaunchEnabled(Resources resources) {
        return (resources.getInteger(17694770) != -1) && !SystemProperties.getBoolean("gesture.disable_camera_launch", false);
    }

    @VisibleForTesting
    public static boolean isCameraDoubleTapPowerEnabled(Resources resources) {
        return resources.getBoolean(17891400);
    }

    public static boolean isCameraLiftTriggerEnabled(Resources resources) {
        return resources.getInteger(17694771) != -1;
    }

    public static boolean isEmergencyGestureEnabled(Resources resources) {
        return resources.getBoolean(17891641);
    }

    public static boolean isDefaultEmergencyGestureEnabled(Resources resources) {
        return resources.getBoolean(17891594);
    }

    public static boolean isGestureLauncherEnabled(Resources resources) {
        return isCameraLaunchEnabled(resources) || isCameraDoubleTapPowerEnabled(resources) || isCameraLiftTriggerEnabled(resources) || isEmergencyGestureEnabled(resources);
    }

    public boolean interceptPowerKeyDown(KeyEvent keyEvent, boolean z, MutableBoolean mutableBoolean) {
        long eventTime;
        boolean z2;
        boolean z3;
        boolean z4;
        if (this.mEmergencyGestureEnabled && this.mEmergencyGesturePowerButtonCooldownPeriodMs >= 0) {
            int i = this.mEmergencyGesturePowerButtonCooldownPeriodMs;
            if (keyEvent.getEventTime() - this.mLastEmergencyGestureTriggered < i) {
                Slog.i("GestureLauncherService", String.format("Suppressing power button: within %dms cooldown period after Emergency Gesture. Begin=%dms, end=%dms.", Integer.valueOf(i), Long.valueOf(this.mLastEmergencyGestureTriggered), Long.valueOf(this.mLastEmergencyGestureTriggered + this.mEmergencyGesturePowerButtonCooldownPeriodMs)));
                mutableBoolean.value = false;
                return true;
            }
        }
        if (keyEvent.isLongPress()) {
            mutableBoolean.value = false;
            return false;
        }
        synchronized (this) {
            eventTime = keyEvent.getEventTime() - this.mLastPowerDown;
            this.mLastPowerDown = keyEvent.getEventTime();
            if (eventTime >= POWER_SHORT_TAP_SEQUENCE_MAX_INTERVAL_MS) {
                this.mFirstPowerDown = keyEvent.getEventTime();
                this.mPowerButtonConsecutiveTaps = 1;
                this.mPowerButtonSlowConsecutiveTaps = 1;
            } else if (eventTime >= CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS) {
                this.mFirstPowerDown = keyEvent.getEventTime();
                this.mPowerButtonConsecutiveTaps = 1;
                this.mPowerButtonSlowConsecutiveTaps++;
            } else {
                this.mPowerButtonConsecutiveTaps++;
                this.mPowerButtonSlowConsecutiveTaps++;
            }
            if (this.mEmergencyGestureEnabled) {
                int i2 = this.mPowerButtonConsecutiveTaps;
                z3 = i2 > (this.mHasFeatureWatch ? 5 : 1) ? z : false;
                if (i2 == 5) {
                    long eventTime2 = keyEvent.getEventTime() - this.mFirstPowerDown;
                    if (eventTime2 <= Settings.Global.getInt(this.mContext.getContentResolver(), "emergency_gesture_tap_detection_min_time_ms", 200)) {
                        Slog.i("GestureLauncherService", "Emergency gesture detected but it's too fast. Gesture time: " + eventTime2 + " ms");
                        this.mFirstPowerDown = keyEvent.getEventTime();
                        this.mPowerButtonConsecutiveTaps = 1;
                        this.mPowerButtonSlowConsecutiveTaps = 1;
                    } else {
                        Slog.i("GestureLauncherService", "Emergency gesture detected. Gesture time: " + eventTime2 + " ms");
                        this.mMetricsLogger.histogram("emergency_gesture_spent_time", (int) eventTime2);
                        z2 = true;
                    }
                }
                z2 = false;
            } else {
                z2 = false;
                z3 = false;
            }
            if (this.mCameraDoubleTapPowerEnabled && eventTime < CAMERA_POWER_DOUBLE_TAP_MAX_TIME_MS && this.mPowerButtonConsecutiveTaps == 2) {
                z4 = true;
            } else {
                z4 = false;
                z = z3;
            }
        }
        if (this.mPowerButtonConsecutiveTaps > 1 || this.mPowerButtonSlowConsecutiveTaps > 1) {
            Slog.i("GestureLauncherService", Long.valueOf(this.mPowerButtonConsecutiveTaps) + " consecutive power button taps detected, " + Long.valueOf(this.mPowerButtonSlowConsecutiveTaps) + " consecutive slow power button taps detected");
        }
        if (z4) {
            Slog.i("GestureLauncherService", "Power button double tap gesture detected, launching camera. Interval=" + eventTime + "ms");
            z4 = handleCameraGesture(false, 1);
            if (z4) {
                this.mMetricsLogger.action(255, (int) eventTime);
                this.mUiEventLogger.log(GestureLauncherEvent.GESTURE_CAMERA_DOUBLE_TAP_POWER);
            }
        } else if (z2) {
            Slog.i("GestureLauncherService", "Emergency gesture detected, launching.");
            z2 = handleEmergencyGesture();
            this.mUiEventLogger.log(GestureLauncherEvent.GESTURE_EMERGENCY_TAP_POWER);
            if (z2) {
                synchronized (this) {
                    this.mLastEmergencyGestureTriggered = keyEvent.getEventTime();
                }
            }
        }
        this.mMetricsLogger.histogram("power_consecutive_short_tap_count", this.mPowerButtonSlowConsecutiveTaps);
        this.mMetricsLogger.histogram("power_double_tap_interval", (int) eventTime);
        mutableBoolean.value = z4 || z2;
        return z && isUserSetupComplete();
    }

    @VisibleForTesting
    public boolean handleCameraGesture(boolean z, int i) {
        Trace.traceBegin(64L, "GestureLauncher:handleCameraGesture");
        try {
            if (isUserSetupComplete()) {
                if (z) {
                    this.mWakeLock.acquire(POWER_SHORT_TAP_SEQUENCE_MAX_INTERVAL_MS);
                }
                ((StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class)).onCameraLaunchGestureDetected(i);
                Trace.traceEnd(64L);
                return true;
            }
            Trace.traceEnd(64L);
            return false;
        } catch (Throwable th) {
            Trace.traceEnd(64L);
            throw th;
        }
    }

    @VisibleForTesting
    public boolean handleEmergencyGesture() {
        Trace.traceBegin(64L, "GestureLauncher:handleEmergencyGesture");
        try {
            if (isUserSetupComplete()) {
                if (this.mHasFeatureWatch) {
                    onEmergencyGestureDetectedOnWatch();
                    return true;
                }
                ((StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class)).onEmergencyActionLaunchGestureDetected();
                return true;
            }
            Trace.traceEnd(64L);
            return false;
        } finally {
            Trace.traceEnd(64L);
        }
    }

    public final void onEmergencyGestureDetectedOnWatch() {
        Intent intent = new Intent(isInRetailMode() ? "com.android.systemui.action.LAUNCH_EMERGENCY_RETAIL" : "com.android.systemui.action.LAUNCH_EMERGENCY");
        ResolveInfo resolveActivity = this.mContext.getPackageManager().resolveActivity(intent, 0);
        if (resolveActivity == null) {
            Slog.w("GestureLauncherService", "Couldn't find an app to process the emergency intent " + intent.getAction());
            return;
        }
        ((Vibrator) this.mContext.getSystemService(Vibrator.class)).vibrate(VibrationEffect.createOneShot(this.mVibrateMilliSecondsForPanicGesture, -1));
        ActivityInfo activityInfo = resolveActivity.activityInfo;
        intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
        intent.setFlags(268435456);
        intent.putExtra("launch_emergency_via_gesture", true);
        this.mContext.startActivityAsUser(intent, new UserHandle(this.mUserId));
    }

    public final boolean isInRetailMode() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "device_demo_mode", 0) == 1;
    }

    public final boolean isUserSetupComplete() {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", 0, -2) != 0;
    }

    /* loaded from: classes.dex */
    public final class GestureEventListener implements SensorEventListener {
        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public GestureEventListener() {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (GestureLauncherService.this.mCameraLaunchRegistered && sensorEvent.sensor == GestureLauncherService.this.mCameraLaunchSensor && GestureLauncherService.this.handleCameraGesture(true, 0)) {
                GestureLauncherService.this.mMetricsLogger.action(256);
                GestureLauncherService.this.mUiEventLogger.log(GestureLauncherEvent.GESTURE_CAMERA_WIGGLE);
                trackCameraLaunchEvent(sensorEvent);
            }
        }

        public final void trackCameraLaunchEvent(SensorEvent sensorEvent) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            float[] fArr = sensorEvent.values;
            double d = elapsedRealtime - GestureLauncherService.this.mCameraGestureOnTimeMs;
            long j = (long) (fArr[0] * d);
            long j2 = (long) (d * fArr[1]);
            int i = (int) fArr[2];
            long j3 = elapsedRealtime - GestureLauncherService.this.mCameraGestureLastEventTime;
            long j4 = j - GestureLauncherService.this.mCameraGestureSensor1LastOnTimeMs;
            long j5 = j2 - GestureLauncherService.this.mCameraGestureSensor2LastOnTimeMs;
            int i2 = i - GestureLauncherService.this.mCameraLaunchLastEventExtra;
            if (j3 < 0 || j4 < 0 || j5 < 0) {
                return;
            }
            EventLogTags.writeCameraGestureTriggered(j3, j4, j5, i2);
            GestureLauncherService.this.mCameraGestureLastEventTime = elapsedRealtime;
            GestureLauncherService.this.mCameraGestureSensor1LastOnTimeMs = j;
            GestureLauncherService.this.mCameraGestureSensor2LastOnTimeMs = j2;
            GestureLauncherService.this.mCameraLaunchLastEventExtra = i;
        }
    }

    /* loaded from: classes.dex */
    public final class CameraLiftTriggerEventListener extends TriggerEventListener {
        public CameraLiftTriggerEventListener() {
        }

        @Override // android.hardware.TriggerEventListener
        public void onTrigger(TriggerEvent triggerEvent) {
            if (GestureLauncherService.this.mCameraLiftRegistered && triggerEvent.sensor == GestureLauncherService.this.mCameraLiftTriggerSensor) {
                GestureLauncherService.this.mContext.getResources();
                SensorManager sensorManager = (SensorManager) GestureLauncherService.this.mContext.getSystemService("sensor");
                boolean isKeyguardShowingAndNotOccluded = GestureLauncherService.this.mWindowManagerInternal.isKeyguardShowingAndNotOccluded();
                boolean isInteractive = GestureLauncherService.this.mPowerManager.isInteractive();
                if ((isKeyguardShowingAndNotOccluded || !isInteractive) && GestureLauncherService.this.handleCameraGesture(true, 2)) {
                    MetricsLogger.action(GestureLauncherService.this.mContext, 989);
                    GestureLauncherService.this.mUiEventLogger.log(GestureLauncherEvent.GESTURE_CAMERA_LIFT);
                }
                GestureLauncherService gestureLauncherService = GestureLauncherService.this;
                gestureLauncherService.mCameraLiftRegistered = sensorManager.requestTriggerSensor(gestureLauncherService.mCameraLiftTriggerListener, GestureLauncherService.this.mCameraLiftTriggerSensor);
            }
        }
    }
}
