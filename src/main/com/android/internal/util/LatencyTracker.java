package com.android.internal.util;

import android.Manifest;
import android.app.ActivityThread;
import android.content.Context;
import android.p008os.Build;
import android.p008os.ConditionVariable;
import android.p008os.SystemClock;
import android.p008os.Trace;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.p028os.BackgroundThread;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
/* loaded from: classes3.dex */
public class LatencyTracker {
    public static final int ACTION_CHECK_CREDENTIAL = 3;
    public static final int ACTION_CHECK_CREDENTIAL_UNLOCKED = 4;
    public static final int ACTION_EXPAND_PANEL = 0;
    public static final int ACTION_FACE_WAKE_AND_UNLOCK = 7;
    public static final int ACTION_FINGERPRINT_WAKE_AND_UNLOCK = 2;
    public static final int ACTION_FOLD_TO_AOD = 18;
    public static final int ACTION_LOAD_SHARE_SHEET = 16;
    public static final int ACTION_LOCKSCREEN_UNLOCK = 11;
    public static final int ACTION_REQUEST_IME_HIDDEN = 21;
    public static final int ACTION_REQUEST_IME_SHOWN = 20;
    public static final int ACTION_ROTATE_SCREEN = 6;
    public static final int ACTION_ROTATE_SCREEN_CAMERA_CHECK = 9;
    public static final int ACTION_ROTATE_SCREEN_SENSOR = 10;
    public static final int ACTION_SHOW_BACK_ARROW = 15;
    public static final int ACTION_SHOW_SELECTION_TOOLBAR = 17;
    public static final int ACTION_SHOW_VOICE_INTERACTION = 19;
    public static final int ACTION_START_RECENTS_ANIMATION = 8;
    public static final int ACTION_SWITCH_DISPLAY_UNFOLD = 13;
    public static final int ACTION_TOGGLE_RECENTS = 1;
    public static final int ACTION_TURN_ON_SCREEN = 5;
    public static final int ACTION_UDFPS_ILLUMINATE = 14;
    public static final int ACTION_USER_SWITCH = 12;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_SAMPLING_INTERVAL = 5;
    public static final String SETTINGS_ENABLED_KEY = "enabled";
    private static final String SETTINGS_SAMPLING_INTERVAL_KEY = "sampling_interval";
    private static final String TAG = "LatencyTracker";
    private static LatencyTracker sLatencyTracker;
    private static final boolean DEFAULT_ENABLED = Build.IS_DEBUGGABLE;
    private static final int[] ACTIONS_ALL = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
    public static final int[] STATSD_ACTION = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
    private final Object mLock = new Object();
    private final SparseArray<Session> mSessions = new SparseArray<>();
    private final SparseArray<ActionProperties> mActionPropertiesMap = new SparseArray<>();
    public final ConditionVariable mDeviceConfigPropertiesUpdated = new ConditionVariable();
    private boolean mEnabled = DEFAULT_ENABLED;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Action {
    }

    public static LatencyTracker getInstance(Context context) {
        if (sLatencyTracker == null) {
            synchronized (LatencyTracker.class) {
                if (sLatencyTracker == null) {
                    sLatencyTracker = new LatencyTracker();
                }
            }
        }
        return sLatencyTracker;
    }

    public LatencyTracker() {
        Context context = ActivityThread.currentApplication();
        if (context != null && context.checkCallingOrSelfPermission(Manifest.C0000permission.READ_DEVICE_CONFIG) == 0) {
            BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.internal.util.LatencyTracker$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    LatencyTracker.this.lambda$new$0();
                }
            });
            DeviceConfig.addOnPropertiesChangedListener("latency_tracker", BackgroundThread.getExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.internal.util.LatencyTracker$$ExternalSyntheticLambda2
                public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                    LatencyTracker.this.updateProperties(properties);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateProperties(DeviceConfig.getProperties("latency_tracker", new String[0]));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateProperties(DeviceConfig.Properties properties) {
        int[] iArr;
        synchronized (this.mLock) {
            int samplingInterval = properties.getInt("sampling_interval", 5);
            this.mEnabled = properties.getBoolean("enabled", DEFAULT_ENABLED);
            for (int action : ACTIONS_ALL) {
                String actionName = getNameOfAction(STATSD_ACTION[action]).toLowerCase(Locale.ROOT);
                int legacyActionTraceThreshold = properties.getInt(actionName + "", -1);
                this.mActionPropertiesMap.put(action, new ActionProperties(action, properties.getBoolean(actionName + "_enable", this.mEnabled), properties.getInt(actionName + "_sample_interval", samplingInterval), properties.getInt(actionName + "_trace_threshold", legacyActionTraceThreshold)));
            }
        }
        this.mDeviceConfigPropertiesUpdated.open();
    }

    public static String getNameOfAction(int atomsProtoAction) {
        switch (atomsProtoAction) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "ACTION_EXPAND_PANEL";
            case 2:
                return "ACTION_TOGGLE_RECENTS";
            case 3:
                return "ACTION_FINGERPRINT_WAKE_AND_UNLOCK";
            case 4:
                return "ACTION_CHECK_CREDENTIAL";
            case 5:
                return "ACTION_CHECK_CREDENTIAL_UNLOCKED";
            case 6:
                return "ACTION_TURN_ON_SCREEN";
            case 7:
                return "ACTION_ROTATE_SCREEN";
            case 8:
                return "ACTION_FACE_WAKE_AND_UNLOCK";
            case 9:
                return "ACTION_START_RECENTS_ANIMATION";
            case 10:
                return "ACTION_ROTATE_SCREEN_CAMERA_CHECK";
            case 11:
                return "ACTION_ROTATE_SCREEN_SENSOR";
            case 12:
                return "ACTION_LOCKSCREEN_UNLOCK";
            case 13:
                return "ACTION_USER_SWITCH";
            case 14:
                return "ACTION_SWITCH_DISPLAY_UNFOLD";
            case 15:
                return "ACTION_UDFPS_ILLUMINATE";
            case 16:
                return "ACTION_SHOW_BACK_ARROW";
            case 17:
                return "ACTION_LOAD_SHARE_SHEET";
            case 18:
                return "ACTION_SHOW_SELECTION_TOOLBAR";
            case 19:
                return "ACTION_FOLD_TO_AOD";
            case 20:
                return "ACTION_SHOW_VOICE_INTERACTION";
            case 21:
                return "ACTION_REQUEST_IME_SHOWN";
            case 22:
                return "ACTION_REQUEST_IME_HIDDEN";
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getTraceNameOfAction(int action, String tag) {
        if (TextUtils.isEmpty(tag)) {
            return "L<" + getNameOfAction(STATSD_ACTION[action]) + ">";
        }
        return "L<" + getNameOfAction(STATSD_ACTION[action]) + "::" + tag + ">";
    }

    private static String getTraceTriggerNameForAction(int action) {
        return "com.android.telemetry.latency-tracker-" + getNameOfAction(STATSD_ACTION[action]);
    }

    @Deprecated
    public static boolean isEnabled(Context ctx) {
        return getInstance(ctx).isEnabled();
    }

    @Deprecated
    public boolean isEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mEnabled;
        }
        return z;
    }

    public static boolean isEnabled(Context ctx, int action) {
        return getInstance(ctx).isEnabled(action);
    }

    public boolean isEnabled(int action) {
        synchronized (this.mLock) {
            ActionProperties actionProperties = this.mActionPropertiesMap.get(action);
            if (actionProperties != null) {
                return actionProperties.isEnabled();
            }
            return false;
        }
    }

    public void onActionStart(int action) {
        onActionStart(action, null);
    }

    public void onActionStart(final int action, String tag) {
        synchronized (this.mLock) {
            if (isEnabled()) {
                if (this.mSessions.get(action) != null) {
                    return;
                }
                Session session = new Session(action, tag);
                session.begin(new Runnable() { // from class: com.android.internal.util.LatencyTracker$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LatencyTracker.this.lambda$onActionStart$1(action);
                    }
                });
                this.mSessions.put(action, session);
            }
        }
    }

    public void onActionEnd(int action) {
        synchronized (this.mLock) {
            if (isEnabled()) {
                Session session = this.mSessions.get(action);
                if (session == null) {
                    return;
                }
                session.end();
                this.mSessions.delete(action);
                logAction(action, session.duration());
            }
        }
    }

    /* renamed from: onActionCancel */
    public void lambda$onActionStart$1(int action) {
        synchronized (this.mLock) {
            Session session = this.mSessions.get(action);
            if (session == null) {
                return;
            }
            session.cancel();
            this.mSessions.delete(action);
        }
    }

    public void logAction(int action, int duration) {
        synchronized (this.mLock) {
            ActionProperties actionProperties = this.mActionPropertiesMap.get(action);
            if (actionProperties == null) {
                return;
            }
            int nextRandNum = ThreadLocalRandom.current().nextInt(actionProperties.getSamplingInterval());
            boolean shouldSample = nextRandNum == 0;
            int traceThreshold = actionProperties.getTraceThreshold();
            if (traceThreshold > 0 && duration >= traceThreshold) {
                PerfettoTrigger.trigger(getTraceTriggerNameForAction(action));
            }
            logActionDeprecated(action, duration, shouldSample);
        }
    }

    public static void logActionDeprecated(int action, int duration, boolean writeToStatsLog) {
        StringBuilder sb = new StringBuilder();
        int[] iArr = STATSD_ACTION;
        Log.m108i(TAG, sb.append(getNameOfAction(iArr[action])).append(" latency=").append(duration).toString());
        EventLog.writeEvent(36070, Integer.valueOf(action), Integer.valueOf(duration));
        if (writeToStatsLog) {
            FrameworkStatsLog.write(306, iArr[action], duration);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class Session {
        private final int mAction;
        private final String mName;
        private final String mTag;
        private Runnable mTimeoutRunnable;
        private long mStartRtc = -1;
        private long mEndRtc = -1;

        Session(int action, String tag) {
            String str;
            this.mAction = action;
            this.mTag = tag;
            if (TextUtils.isEmpty(tag)) {
                str = LatencyTracker.getNameOfAction(LatencyTracker.STATSD_ACTION[action]);
            } else {
                str = LatencyTracker.getNameOfAction(LatencyTracker.STATSD_ACTION[action]) + "::" + tag;
            }
            this.mName = str;
        }

        String name() {
            return this.mName;
        }

        String traceName() {
            return LatencyTracker.getTraceNameOfAction(this.mAction, this.mTag);
        }

        void begin(Runnable timeoutAction) {
            this.mStartRtc = SystemClock.elapsedRealtime();
            Trace.asyncTraceBegin(4096L, traceName(), 0);
            this.mTimeoutRunnable = timeoutAction;
            BackgroundThread.getHandler().postDelayed(this.mTimeoutRunnable, TimeUnit.SECONDS.toMillis(15L));
        }

        void end() {
            this.mEndRtc = SystemClock.elapsedRealtime();
            Trace.asyncTraceEnd(4096L, traceName(), 0);
            BackgroundThread.getHandler().removeCallbacks(this.mTimeoutRunnable);
            this.mTimeoutRunnable = null;
        }

        void cancel() {
            Trace.asyncTraceEnd(4096L, traceName(), 0);
            BackgroundThread.getHandler().removeCallbacks(this.mTimeoutRunnable);
            this.mTimeoutRunnable = null;
        }

        int duration() {
            return (int) (this.mEndRtc - this.mStartRtc);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class ActionProperties {
        static final String ENABLE_SUFFIX = "_enable";
        static final String LEGACY_TRACE_THRESHOLD_SUFFIX = "";
        static final String SAMPLE_INTERVAL_SUFFIX = "_sample_interval";
        static final String TRACE_THRESHOLD_SUFFIX = "_trace_threshold";
        private final int mAction;
        private final boolean mEnabled;
        private final int mSamplingInterval;
        private final int mTraceThreshold;

        ActionProperties(int action, boolean enabled, int samplingInterval, int traceThreshold) {
            this.mAction = action;
            AnnotationValidations.validate((Class<? extends Annotation>) Action.class, (Annotation) null, action);
            this.mEnabled = enabled;
            this.mSamplingInterval = samplingInterval;
            this.mTraceThreshold = traceThreshold;
        }

        int getAction() {
            return this.mAction;
        }

        boolean isEnabled() {
            return this.mEnabled;
        }

        int getSamplingInterval() {
            return this.mSamplingInterval;
        }

        int getTraceThreshold() {
            return this.mTraceThreshold;
        }

        public String toString() {
            return "ActionProperties{ mAction=" + this.mAction + ", mEnabled=" + this.mEnabled + ", mSamplingInterval=" + this.mSamplingInterval + ", mTraceThreshold=" + this.mTraceThreshold + "}";
        }
    }
}
