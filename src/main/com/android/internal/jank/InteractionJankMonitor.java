package com.android.internal.jank;

import android.Manifest;
import android.app.ActivityThread;
import android.content.Context;
import android.p008os.Build;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.HandlerThread;
import android.p008os.SystemClock;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Choreographer;
import android.view.SurfaceControl;
import android.view.View;
import com.android.internal.jank.FrameTracker;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.util.PerfettoTrigger;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
/* loaded from: classes4.dex */
public class InteractionJankMonitor {
    private static final String ACTION_PREFIX;
    public static final String ACTION_SESSION_CANCEL;
    public static final String ACTION_SESSION_END;
    public static final int CUJ_BIOMETRIC_PROMPT_TRANSITION = 56;
    public static final int CUJ_IME_INSETS_ANIMATION = 69;
    public static final int CUJ_LAUNCHER_ALL_APPS_SCROLL = 26;
    public static final int CUJ_LAUNCHER_APP_CLOSE_TO_HOME = 9;
    public static final int CUJ_LAUNCHER_APP_CLOSE_TO_PIP = 10;
    public static final int CUJ_LAUNCHER_APP_LAUNCH_FROM_ICON = 8;
    public static final int CUJ_LAUNCHER_APP_LAUNCH_FROM_RECENTS = 7;
    public static final int CUJ_LAUNCHER_APP_LAUNCH_FROM_WIDGET = 27;
    public static final int CUJ_LAUNCHER_APP_SWIPE_TO_RECENTS = 66;
    public static final int CUJ_LAUNCHER_CLOSE_ALL_APPS_SWIPE = 67;
    public static final int CUJ_LAUNCHER_CLOSE_ALL_APPS_TO_HOME = 68;
    public static final int CUJ_LAUNCHER_OPEN_ALL_APPS = 25;
    public static final int CUJ_LAUNCHER_QUICK_SWITCH = 11;
    public static final int CUJ_LAUNCHER_UNLOCK_ENTRANCE_ANIMATION = 63;
    public static final int CUJ_LOCKSCREEN_CLOCK_MOVE_ANIMATION = 70;
    public static final int CUJ_LOCKSCREEN_LAUNCH_CAMERA = 51;
    public static final int CUJ_LOCKSCREEN_OCCLUSION = 64;
    public static final int CUJ_LOCKSCREEN_PASSWORD_APPEAR = 17;
    public static final int CUJ_LOCKSCREEN_PASSWORD_DISAPPEAR = 20;
    public static final int CUJ_LOCKSCREEN_PATTERN_APPEAR = 18;
    public static final int CUJ_LOCKSCREEN_PATTERN_DISAPPEAR = 21;
    public static final int CUJ_LOCKSCREEN_PIN_APPEAR = 19;
    public static final int CUJ_LOCKSCREEN_PIN_DISAPPEAR = 22;
    public static final int CUJ_LOCKSCREEN_TRANSITION_FROM_AOD = 23;
    public static final int CUJ_LOCKSCREEN_TRANSITION_TO_AOD = 24;
    public static final int CUJ_LOCKSCREEN_UNLOCK_ANIMATION = 29;
    public static final int CUJ_NOTIFICATION_ADD = 14;
    public static final int CUJ_NOTIFICATION_APP_START = 16;
    public static final int CUJ_NOTIFICATION_HEADS_UP_APPEAR = 12;
    public static final int CUJ_NOTIFICATION_HEADS_UP_DISAPPEAR = 13;
    public static final int CUJ_NOTIFICATION_REMOVE = 15;
    public static final int CUJ_NOTIFICATION_SHADE_EXPAND_COLLAPSE = 0;
    public static final int CUJ_NOTIFICATION_SHADE_QS_EXPAND_COLLAPSE = 5;
    public static final int CUJ_NOTIFICATION_SHADE_QS_SCROLL_SWIPE = 6;
    public static final int CUJ_NOTIFICATION_SHADE_ROW_EXPAND = 3;
    public static final int CUJ_NOTIFICATION_SHADE_ROW_SWIPE = 4;
    public static final int CUJ_NOTIFICATION_SHADE_SCROLL_FLING = 2;
    public static final int CUJ_ONE_HANDED_ENTER_TRANSITION = 42;
    public static final int CUJ_ONE_HANDED_EXIT_TRANSITION = 43;
    public static final int CUJ_PIP_TRANSITION = 35;
    public static final int CUJ_RECENTS_SCROLLING = 65;
    public static final int CUJ_SCREEN_OFF = 40;
    public static final int CUJ_SCREEN_OFF_SHOW_AOD = 41;
    public static final int CUJ_SETTINGS_PAGE_SCROLL = 28;
    public static final int CUJ_SETTINGS_SLIDER = 53;
    public static final int CUJ_SETTINGS_TOGGLE = 57;
    public static final int CUJ_SHADE_APP_LAUNCH_FROM_HISTORY_BUTTON = 30;
    public static final int CUJ_SHADE_APP_LAUNCH_FROM_MEDIA_PLAYER = 31;
    public static final int CUJ_SHADE_APP_LAUNCH_FROM_QS_TILE = 32;
    public static final int CUJ_SHADE_APP_LAUNCH_FROM_SETTINGS_BUTTON = 33;
    public static final int CUJ_SHADE_CLEAR_ALL = 62;
    public static final int CUJ_SHADE_DIALOG_OPEN = 58;
    public static final int CUJ_SPLASHSCREEN_AVD = 38;
    public static final int CUJ_SPLASHSCREEN_EXIT_ANIM = 39;
    public static final int CUJ_SPLIT_SCREEN_ENTER = 49;
    public static final int CUJ_SPLIT_SCREEN_EXIT = 50;
    public static final int CUJ_SPLIT_SCREEN_RESIZE = 52;
    public static final int CUJ_STATUS_BAR_APP_LAUNCH_FROM_CALL_CHIP = 34;
    public static final int CUJ_SUW_LOADING_SCREEN_FOR_STATUS = 48;
    public static final int CUJ_SUW_LOADING_TO_NEXT_FLOW = 47;
    public static final int CUJ_SUW_LOADING_TO_SHOW_INFO_WITH_ACTIONS = 45;
    public static final int CUJ_SUW_SHOW_FUNCTION_SCREEN_WITH_ACTIONS = 46;
    public static final int CUJ_TAKE_SCREENSHOT = 54;
    public static final int CUJ_TASKBAR_COLLAPSE = 61;
    public static final int CUJ_TASKBAR_EXPAND = 60;
    public static final int[] CUJ_TO_STATSD_INTERACTION_TYPE;
    public static final int CUJ_UNFOLD_ANIM = 44;
    public static final int CUJ_USER_DIALOG_OPEN = 59;
    public static final int CUJ_USER_SWITCH = 37;
    public static final int CUJ_VOLUME_CONTROL = 55;
    public static final int CUJ_WALLPAPER_TRANSITION = 36;
    private static final boolean DEBUG = false;
    private static final boolean DEFAULT_ENABLED;
    private static final int DEFAULT_SAMPLING_INTERVAL = 1;
    private static final long DEFAULT_TIMEOUT_MS;
    private static final int DEFAULT_TRACE_THRESHOLD_FRAME_TIME_MILLIS = 64;
    private static final int DEFAULT_TRACE_THRESHOLD_MISSED_FRAMES = 3;
    private static final String DEFAULT_WORKER_NAME;
    static final long EXECUTOR_TASK_TIMEOUT = 500;
    public static final int MAX_LENGTH_OF_CUJ_NAME = 80;
    private static final int MAX_LENGTH_SESSION_NAME = 100;
    private static final int NO_STATSD_LOGGING = -1;
    private static final String SETTINGS_ENABLED_KEY = "enabled";
    private static final String SETTINGS_SAMPLING_INTERVAL_KEY = "sampling_interval";
    private static final String SETTINGS_THRESHOLD_FRAME_TIME_MILLIS_KEY = "trace_threshold_frame_time_millis";
    private static final String SETTINGS_THRESHOLD_MISSED_FRAMES_KEY = "trace_threshold_missed_frames";
    private static final String TAG;
    private final DisplayResolutionTracker mDisplayResolutionTracker;
    private volatile boolean mEnabled;
    private final Object mLock;
    private final DeviceConfig.OnPropertiesChangedListener mPropertiesChangedListener;
    private final SparseArray<FrameTracker> mRunningTrackers;
    private int mSamplingInterval;
    private final SparseArray<Runnable> mTimeoutActions;
    private int mTraceThresholdFrameTimeMillis;
    private int mTraceThresholdMissedFrames;
    private final HandlerThread mWorker;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface CujType {
    }

    /* JADX INFO: Access modifiers changed from: private */
    @FunctionalInterface
    /* loaded from: classes4.dex */
    public interface TimeFunction {
        void invoke(long j, long j2, long j3);
    }

    static {
        String simpleName = InteractionJankMonitor.class.getSimpleName();
        TAG = simpleName;
        String canonicalName = InteractionJankMonitor.class.getCanonicalName();
        ACTION_PREFIX = canonicalName;
        DEFAULT_WORKER_NAME = simpleName + "-Worker";
        DEFAULT_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(2L);
        DEFAULT_ENABLED = Build.IS_DEBUGGABLE;
        ACTION_SESSION_END = canonicalName + ".ACTION_SESSION_END";
        ACTION_SESSION_CANCEL = canonicalName + ".ACTION_SESSION_CANCEL";
        CUJ_TO_STATSD_INTERACTION_TYPE = new int[]{1, -1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71};
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class InstanceHolder {
        public static final InteractionJankMonitor INSTANCE = new InteractionJankMonitor(new HandlerThread(InteractionJankMonitor.DEFAULT_WORKER_NAME));

        private InstanceHolder() {
        }
    }

    public static InteractionJankMonitor getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public InteractionJankMonitor(HandlerThread worker) {
        DeviceConfig.OnPropertiesChangedListener onPropertiesChangedListener = new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda1
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                InteractionJankMonitor.this.updateProperties(properties);
            }
        };
        this.mPropertiesChangedListener = onPropertiesChangedListener;
        this.mLock = new Object();
        boolean z = DEFAULT_ENABLED;
        this.mEnabled = z;
        this.mSamplingInterval = 1;
        this.mTraceThresholdMissedFrames = 3;
        this.mTraceThresholdFrameTimeMillis = 64;
        this.mRunningTrackers = new SparseArray<>();
        this.mTimeoutActions = new SparseArray<>();
        this.mWorker = worker;
        worker.start();
        this.mDisplayResolutionTracker = new DisplayResolutionTracker(worker.getThreadHandler());
        this.mSamplingInterval = 1;
        this.mEnabled = z;
        Context context = ActivityThread.currentApplication();
        if (context.checkCallingOrSelfPermission(Manifest.C0000permission.READ_DEVICE_CONFIG) == 0) {
            worker.getThreadHandler().post(new Runnable() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    InteractionJankMonitor.this.lambda$new$0();
                }
            });
            DeviceConfig.addOnPropertiesChangedListener("interaction_jank_monitor", new HandlerExecutor(worker.getThreadHandler()), onPropertiesChangedListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.mPropertiesChangedListener.onPropertiesChanged(DeviceConfig.getProperties("interaction_jank_monitor", new String[0]));
    }

    public FrameTracker createFrameTracker(Configuration config, Session session) {
        View view = config.mView;
        if (!config.hasValidView()) {
            boolean attached = false;
            boolean hasViewRoot = false;
            boolean hasRenderer = false;
            if (view != null) {
                attached = view.isAttachedToWindow();
                hasViewRoot = view.getViewRootImpl() != null;
                hasRenderer = view.getThreadedRenderer() != null;
            }
            Log.m111d(TAG, "create FrameTracker fails: view=" + view + ", attached=" + attached + ", hasViewRoot=" + hasViewRoot + ", hasRenderer=" + hasRenderer, new Throwable());
            return null;
        }
        FrameTracker.ThreadedRendererWrapper threadedRenderer = view == null ? null : new FrameTracker.ThreadedRendererWrapper(view.getThreadedRenderer());
        FrameTracker.ViewRootWrapper viewRoot = view == null ? null : new FrameTracker.ViewRootWrapper(view.getViewRootImpl());
        FrameTracker.SurfaceControlWrapper surfaceControl = new FrameTracker.SurfaceControlWrapper();
        FrameTracker.ChoreographerWrapper choreographer = new FrameTracker.ChoreographerWrapper(Choreographer.getInstance());
        FrameTracker.FrameTrackerListener eventsListener = new FrameTracker.FrameTrackerListener() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda12
            @Override // com.android.internal.jank.FrameTracker.FrameTrackerListener
            public final void onCujEvents(InteractionJankMonitor.Session session2, String str) {
                InteractionJankMonitor.this.lambda$createFrameTracker$1(session2, str);
            }
        };
        FrameTracker.FrameMetricsWrapper frameMetrics = new FrameTracker.FrameMetricsWrapper();
        return new FrameTracker(this, session, config.getHandler(), threadedRenderer, viewRoot, surfaceControl, choreographer, frameMetrics, new FrameTracker.StatsLogWrapper(this.mDisplayResolutionTracker), this.mTraceThresholdMissedFrames, this.mTraceThresholdFrameTimeMillis, eventsListener, config);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: handleCujEvents */
    public void lambda$createFrameTracker$1(String action, final Session session) {
        if (needRemoveTasks(action, session)) {
            getTracker(session.getCuj()).getHandler().runWithScissors(new Runnable() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InteractionJankMonitor.this.lambda$handleCujEvents$2(session);
                }
            }, EXECUTOR_TASK_TIMEOUT);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleCujEvents$2(Session session) {
        removeTimeout(session.getCuj());
        removeTracker(session.getCuj());
    }

    private boolean needRemoveTasks(String action, Session session) {
        boolean badEnd = action.equals(ACTION_SESSION_END) && session.getReason() != 0;
        boolean badCancel = (!action.equals(ACTION_SESSION_CANCEL) || session.getReason() == 16 || session.getReason() == 19) ? false : true;
        return badEnd || badCancel;
    }

    private void removeTimeout(int cujType) {
        synchronized (this.mLock) {
            Runnable timeout = this.mTimeoutActions.get(cujType);
            if (timeout != null) {
                getTracker(cujType).getHandler().removeCallbacks(timeout);
                this.mTimeoutActions.remove(cujType);
            }
        }
    }

    public boolean isInstrumenting(int cujType) {
        boolean contains;
        synchronized (this.mLock) {
            contains = this.mRunningTrackers.contains(cujType);
        }
        return contains;
    }

    public boolean begin(View v, int cujType) {
        try {
            return begin(Configuration.Builder.withView(cujType, v));
        } catch (IllegalArgumentException ex) {
            Log.m111d(TAG, "Build configuration failed!", ex);
            return false;
        }
    }

    public boolean begin(Configuration.Builder builder) {
        try {
            final Configuration config = builder.build();
            postEventLogToWorkerThread(new TimeFunction() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda6
                @Override // com.android.internal.jank.InteractionJankMonitor.TimeFunction
                public final void invoke(long j, long j2, long j3) {
                    EventLogTags.writeJankCujEventsBeginRequest(InteractionJankMonitor.Configuration.this.mCujType, j, j2, j3);
                }
            });
            final TrackerResult result = new TrackerResult();
            boolean success = config.getHandler().runWithScissors(new Runnable() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    InteractionJankMonitor.this.lambda$begin$4(result, config);
                }
            }, EXECUTOR_TASK_TIMEOUT);
            if (!success) {
                Log.m112d(TAG, "begin failed due to timeout, CUJ=" + getNameOfCuj(config.mCujType));
                return false;
            }
            return result.mResult;
        } catch (IllegalArgumentException ex) {
            Log.m111d(TAG, "Build configuration failed!", ex);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$begin$4(TrackerResult result, Configuration config) {
        result.mResult = beginInternal(config);
    }

    private boolean beginInternal(Configuration conf) {
        FrameTracker tracker;
        final int cujType = conf.mCujType;
        if (shouldMonitor(cujType) && getTracker(cujType) == null && (tracker = createFrameTracker(conf, new Session(cujType, conf.mTag))) != null) {
            putTracker(cujType, tracker);
            tracker.begin();
            scheduleTimeoutAction(cujType, conf.mTimeout, new Runnable() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    InteractionJankMonitor.this.lambda$beginInternal$5(cujType);
                }
            });
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$beginInternal$5(int cujType) {
        cancel(cujType, 19);
    }

    public boolean shouldMonitor(int cujType) {
        boolean shouldSample = ThreadLocalRandom.current().nextInt() % this.mSamplingInterval == 0;
        return this.mEnabled && shouldSample;
    }

    public void scheduleTimeoutAction(int cuj, long timeout, Runnable action) {
        synchronized (this.mLock) {
            this.mTimeoutActions.put(cuj, action);
            getTracker(cuj).getHandler().postDelayed(action, timeout);
        }
    }

    public boolean end(final int cujType) {
        postEventLogToWorkerThread(new TimeFunction() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda9
            @Override // com.android.internal.jank.InteractionJankMonitor.TimeFunction
            public final void invoke(long j, long j2, long j3) {
                EventLogTags.writeJankCujEventsEndRequest(cujType, j, j2, j3);
            }
        });
        FrameTracker tracker = getTracker(cujType);
        if (tracker == null) {
            return false;
        }
        try {
            final TrackerResult result = new TrackerResult();
            boolean success = tracker.getHandler().runWithScissors(new Runnable() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    InteractionJankMonitor.this.lambda$end$7(result, cujType);
                }
            }, EXECUTOR_TASK_TIMEOUT);
            if (!success) {
                Log.m112d(TAG, "end failed due to timeout, CUJ=" + getNameOfCuj(cujType));
                return false;
            }
            return result.mResult;
        } catch (IllegalArgumentException ex) {
            Log.m111d(TAG, "Execute end task failed!", ex);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$end$7(TrackerResult result, int cujType) {
        result.mResult = endInternal(cujType);
    }

    private boolean endInternal(int cujType) {
        removeTimeout(cujType);
        FrameTracker tracker = getTracker(cujType);
        if (tracker == null) {
            return false;
        }
        if (tracker.end(0)) {
            removeTracker(cujType);
            return true;
        }
        return true;
    }

    public boolean cancel(final int cujType) {
        postEventLogToWorkerThread(new TimeFunction() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda8
            @Override // com.android.internal.jank.InteractionJankMonitor.TimeFunction
            public final void invoke(long j, long j2, long j3) {
                EventLogTags.writeJankCujEventsCancelRequest(cujType, j, j2, j3);
            }
        });
        return cancel(cujType, 16);
    }

    public boolean cancel(final int cujType, final int reason) {
        FrameTracker tracker = getTracker(cujType);
        if (tracker == null) {
            return false;
        }
        try {
            final TrackerResult result = new TrackerResult();
            boolean success = tracker.getHandler().runWithScissors(new Runnable() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    InteractionJankMonitor.this.lambda$cancel$9(result, cujType, reason);
                }
            }, EXECUTOR_TASK_TIMEOUT);
            if (!success) {
                Log.m112d(TAG, "cancel failed due to timeout, CUJ=" + getNameOfCuj(cujType));
                return false;
            }
            return result.mResult;
        } catch (IllegalArgumentException ex) {
            Log.m111d(TAG, "Execute cancel task failed!", ex);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancel$9(TrackerResult result, int cujType, int reason) {
        result.mResult = cancelInternal(cujType, reason);
    }

    private boolean cancelInternal(int cujType, int reason) {
        removeTimeout(cujType);
        FrameTracker tracker = getTracker(cujType);
        if (tracker == null) {
            return false;
        }
        if (tracker.cancel(reason)) {
            removeTracker(cujType);
            return true;
        }
        return true;
    }

    private void putTracker(int cuj, FrameTracker tracker) {
        synchronized (this.mLock) {
            this.mRunningTrackers.put(cuj, tracker);
        }
    }

    private FrameTracker getTracker(int cuj) {
        FrameTracker frameTracker;
        synchronized (this.mLock) {
            frameTracker = this.mRunningTrackers.get(cuj);
        }
        return frameTracker;
    }

    private void removeTracker(int cuj) {
        synchronized (this.mLock) {
            this.mRunningTrackers.remove(cuj);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateProperties(DeviceConfig.Properties properties) {
        this.mSamplingInterval = properties.getInt("sampling_interval", 1);
        this.mTraceThresholdMissedFrames = properties.getInt(SETTINGS_THRESHOLD_MISSED_FRAMES_KEY, 3);
        this.mTraceThresholdFrameTimeMillis = properties.getInt(SETTINGS_THRESHOLD_FRAME_TIME_MILLIS_KEY, 64);
        this.mEnabled = properties.getBoolean("enabled", DEFAULT_ENABLED);
    }

    public DeviceConfig.OnPropertiesChangedListener getPropertiesChangedListener() {
        return this.mPropertiesChangedListener;
    }

    public void trigger(final Session session) {
        this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PerfettoTrigger.trigger(InteractionJankMonitor.Session.this.getPerfettoTrigger());
            }
        });
    }

    public static String getNameOfInteraction(int interactionType) {
        return getNameOfCuj(getCujTypeFromInteraction(interactionType));
    }

    private static int getCujTypeFromInteraction(int interactionType) {
        return interactionType - 1;
    }

    public static String getNameOfCuj(int cujType) {
        switch (cujType) {
            case 0:
                return "NOTIFICATION_SHADE_EXPAND_COLLAPSE";
            case 1:
            default:
                return "UNKNOWN";
            case 2:
                return "NOTIFICATION_SHADE_SCROLL_FLING";
            case 3:
                return "NOTIFICATION_SHADE_ROW_EXPAND";
            case 4:
                return "NOTIFICATION_SHADE_ROW_SWIPE";
            case 5:
                return "NOTIFICATION_SHADE_QS_EXPAND_COLLAPSE";
            case 6:
                return "NOTIFICATION_SHADE_QS_SCROLL_SWIPE";
            case 7:
                return "LAUNCHER_APP_LAUNCH_FROM_RECENTS";
            case 8:
                return "LAUNCHER_APP_LAUNCH_FROM_ICON";
            case 9:
                return "LAUNCHER_APP_CLOSE_TO_HOME";
            case 10:
                return "LAUNCHER_APP_CLOSE_TO_PIP";
            case 11:
                return "LAUNCHER_QUICK_SWITCH";
            case 12:
                return "NOTIFICATION_HEADS_UP_APPEAR";
            case 13:
                return "NOTIFICATION_HEADS_UP_DISAPPEAR";
            case 14:
                return "NOTIFICATION_ADD";
            case 15:
                return "NOTIFICATION_REMOVE";
            case 16:
                return "NOTIFICATION_APP_START";
            case 17:
                return "LOCKSCREEN_PASSWORD_APPEAR";
            case 18:
                return "LOCKSCREEN_PATTERN_APPEAR";
            case 19:
                return "LOCKSCREEN_PIN_APPEAR";
            case 20:
                return "LOCKSCREEN_PASSWORD_DISAPPEAR";
            case 21:
                return "LOCKSCREEN_PATTERN_DISAPPEAR";
            case 22:
                return "LOCKSCREEN_PIN_DISAPPEAR";
            case 23:
                return "LOCKSCREEN_TRANSITION_FROM_AOD";
            case 24:
                return "LOCKSCREEN_TRANSITION_TO_AOD";
            case 25:
                return "LAUNCHER_OPEN_ALL_APPS";
            case 26:
                return "LAUNCHER_ALL_APPS_SCROLL";
            case 27:
                return "LAUNCHER_APP_LAUNCH_FROM_WIDGET";
            case 28:
                return "SETTINGS_PAGE_SCROLL";
            case 29:
                return "LOCKSCREEN_UNLOCK_ANIMATION";
            case 30:
                return "SHADE_APP_LAUNCH_FROM_HISTORY_BUTTON";
            case 31:
                return "SHADE_APP_LAUNCH_FROM_MEDIA_PLAYER";
            case 32:
                return "SHADE_APP_LAUNCH_FROM_QS_TILE";
            case 33:
                return "SHADE_APP_LAUNCH_FROM_SETTINGS_BUTTON";
            case 34:
                return "STATUS_BAR_APP_LAUNCH_FROM_CALL_CHIP";
            case 35:
                return "PIP_TRANSITION";
            case 36:
                return "WALLPAPER_TRANSITION";
            case 37:
                return "USER_SWITCH";
            case 38:
                return "SPLASHSCREEN_AVD";
            case 39:
                return "SPLASHSCREEN_EXIT_ANIM";
            case 40:
                return "SCREEN_OFF";
            case 41:
                return "SCREEN_OFF_SHOW_AOD";
            case 42:
                return "ONE_HANDED_ENTER_TRANSITION";
            case 43:
                return "ONE_HANDED_EXIT_TRANSITION";
            case 44:
                return "UNFOLD_ANIM";
            case 45:
                return "SUW_LOADING_TO_SHOW_INFO_WITH_ACTIONS";
            case 46:
                return "SUW_SHOW_FUNCTION_SCREEN_WITH_ACTIONS";
            case 47:
                return "SUW_LOADING_TO_NEXT_FLOW";
            case 48:
                return "SUW_LOADING_SCREEN_FOR_STATUS";
            case 49:
                return "SPLIT_SCREEN_ENTER";
            case 50:
                return "SPLIT_SCREEN_EXIT";
            case 51:
                return "LOCKSCREEN_LAUNCH_CAMERA";
            case 52:
                return "SPLIT_SCREEN_RESIZE";
            case 53:
                return "SETTINGS_SLIDER";
            case 54:
                return "TAKE_SCREENSHOT";
            case 55:
                return "VOLUME_CONTROL";
            case 56:
                return "BIOMETRIC_PROMPT_TRANSITION";
            case 57:
                return "SETTINGS_TOGGLE";
            case 58:
                return "SHADE_DIALOG_OPEN";
            case 59:
                return "USER_DIALOG_OPEN";
            case 60:
                return "TASKBAR_EXPAND";
            case 61:
                return "TASKBAR_COLLAPSE";
            case 62:
                return "SHADE_CLEAR_ALL";
            case 63:
                return "LAUNCHER_UNLOCK_ENTRANCE_ANIMATION";
            case 64:
                return "LOCKSCREEN_OCCLUSION";
            case 65:
                return "RECENTS_SCROLLING";
            case 66:
                return "LAUNCHER_APP_SWIPE_TO_RECENTS";
            case 67:
                return "LAUNCHER_CLOSE_ALL_APPS_SWIPE";
            case 68:
                return "LAUNCHER_CLOSE_ALL_APPS_TO_HOME";
            case 69:
                return "IME_INSETS_ANIMATION";
            case 70:
                return "LOCKSCREEN_CLOCK_MOVE_ANIMATION";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class TrackerResult {
        private boolean mResult;

        private TrackerResult() {
        }
    }

    /* loaded from: classes4.dex */
    public static class Configuration {
        private final Context mContext;
        private final int mCujType;
        private final boolean mDeferMonitor;
        private final Handler mHandler;
        private final SurfaceControl mSurfaceControl;
        private final boolean mSurfaceOnly;
        private final String mTag;
        private final long mTimeout;
        private final View mView;

        /* loaded from: classes4.dex */
        public static class Builder {
            private int mAttrCujType;
            private SurfaceControl mAttrSurfaceControl;
            private boolean mAttrSurfaceOnly;
            private View mAttrView = null;
            private Context mAttrContext = null;
            private long mAttrTimeout = InteractionJankMonitor.DEFAULT_TIMEOUT_MS;
            private String mAttrTag = "";
            private boolean mAttrDeferMonitor = true;

            public static Builder withSurface(int cuj, Context context, SurfaceControl surfaceControl) {
                return new Builder(cuj).setContext(context).setSurfaceControl(surfaceControl).setSurfaceOnly(true);
            }

            public static Builder withView(int cuj, View view) {
                return new Builder(cuj).setView(view).setContext(view.getContext());
            }

            private Builder(int cuj) {
                this.mAttrCujType = cuj;
            }

            private Builder setView(View view) {
                this.mAttrView = view;
                return this;
            }

            public Builder setTimeout(long timeout) {
                this.mAttrTimeout = timeout;
                return this;
            }

            public Builder setTag(String tag) {
                this.mAttrTag = tag;
                return this;
            }

            private Builder setSurfaceOnly(boolean surfaceOnly) {
                this.mAttrSurfaceOnly = surfaceOnly;
                return this;
            }

            private Builder setContext(Context context) {
                this.mAttrContext = context;
                return this;
            }

            private Builder setSurfaceControl(SurfaceControl surfaceControl) {
                this.mAttrSurfaceControl = surfaceControl;
                return this;
            }

            public Builder setDeferMonitorForAnimationStart(boolean defer) {
                this.mAttrDeferMonitor = defer;
                return this;
            }

            public Configuration build() throws IllegalArgumentException {
                return new Configuration(this.mAttrCujType, this.mAttrView, this.mAttrTag, this.mAttrTimeout, this.mAttrSurfaceOnly, this.mAttrContext, this.mAttrSurfaceControl, this.mAttrDeferMonitor);
            }
        }

        private Configuration(int cuj, View view, String tag, long timeout, boolean surfaceOnly, Context context, SurfaceControl surfaceControl, boolean deferMonitor) {
            Context applicationContext;
            this.mCujType = cuj;
            this.mTag = tag;
            this.mTimeout = timeout;
            this.mView = view;
            this.mSurfaceOnly = surfaceOnly;
            if (context != null) {
                applicationContext = context;
            } else {
                applicationContext = view != null ? view.getContext().getApplicationContext() : null;
            }
            this.mContext = applicationContext;
            this.mSurfaceControl = surfaceControl;
            this.mDeferMonitor = deferMonitor;
            validate();
            this.mHandler = surfaceOnly ? applicationContext.getMainThreadHandler() : view.getHandler();
        }

        private void validate() {
            boolean shouldThrow = false;
            StringBuilder msg = new StringBuilder();
            if (this.mTag == null) {
                shouldThrow = true;
                msg.append("Invalid tag; ");
            }
            if (this.mTimeout < 0) {
                shouldThrow = true;
                msg.append("Invalid timeout value; ");
            }
            if (this.mSurfaceOnly) {
                if (this.mContext == null) {
                    shouldThrow = true;
                    msg.append("Must pass in a context if only instrument surface; ");
                }
                SurfaceControl surfaceControl = this.mSurfaceControl;
                if (surfaceControl == null || !surfaceControl.isValid()) {
                    shouldThrow = true;
                    msg.append("Must pass in a valid surface control if only instrument surface; ");
                }
            } else if (!hasValidView()) {
                shouldThrow = true;
                boolean attached = false;
                boolean hasViewRoot = false;
                boolean hasRenderer = false;
                View view = this.mView;
                if (view != null) {
                    attached = view.isAttachedToWindow();
                    hasViewRoot = this.mView.getViewRootImpl() != null;
                    hasRenderer = this.mView.getThreadedRenderer() != null;
                }
                String err = "invalid view: view=" + this.mView + ", attached=" + attached + ", hasViewRoot=" + hasViewRoot + ", hasRenderer=" + hasRenderer;
                msg.append(err);
            }
            if (shouldThrow) {
                throw new IllegalArgumentException(msg.toString());
            }
        }

        boolean hasValidView() {
            View view;
            return this.mSurfaceOnly || !((view = this.mView) == null || !view.isAttachedToWindow() || this.mView.getViewRootImpl() == null || this.mView.getThreadedRenderer() == null);
        }

        public boolean isSurfaceOnly() {
            return this.mSurfaceOnly;
        }

        public SurfaceControl getSurfaceControl() {
            return this.mSurfaceControl;
        }

        public View getView() {
            return this.mView;
        }

        public boolean shouldDeferMonitor() {
            return this.mDeferMonitor;
        }

        public Handler getHandler() {
            return this.mHandler;
        }

        public int getDisplayId() {
            return (this.mSurfaceOnly ? this.mContext : this.mView.getContext()).getDisplayId();
        }
    }

    /* loaded from: classes4.dex */
    public static class Session {
        private final int mCujType;
        private final String mName;
        private int mReason = -1;
        private final long mTimeStamp = System.nanoTime();

        public Session(int cujType, String postfix) {
            this.mCujType = cujType;
            this.mName = generateSessionName(InteractionJankMonitor.getNameOfCuj(cujType), postfix);
        }

        private String generateSessionName(String cujName, String cujPostfix) {
            int remaining;
            boolean hasPostfix = !TextUtils.isEmpty(cujPostfix);
            if (cujName.length() > 80) {
                throw new IllegalArgumentException(TextUtils.formatSimple("The length of cuj name <%s> exceeds %d", cujName, 80));
            }
            if (hasPostfix && cujPostfix.length() > (remaining = 100 - cujName.length())) {
                cujPostfix = cujPostfix.substring(0, remaining - 3).concat(android.telecom.Logging.Session.TRUNCATE_STRING);
            }
            if (hasPostfix) {
                return TextUtils.formatSimple("J<%s::%s>", cujName, cujPostfix);
            }
            return TextUtils.formatSimple("J<%s>", cujName);
        }

        public int getCuj() {
            return this.mCujType;
        }

        public int getStatsdInteractionType() {
            return InteractionJankMonitor.CUJ_TO_STATSD_INTERACTION_TYPE[this.mCujType];
        }

        public boolean logToStatsd() {
            return getStatsdInteractionType() != -1;
        }

        public String getPerfettoTrigger() {
            return String.format(Locale.US, "com.android.telemetry.interaction-jank-monitor-%d", Integer.valueOf(this.mCujType));
        }

        public String getName() {
            return this.mName;
        }

        public long getTimeStamp() {
            return this.mTimeStamp;
        }

        public void setReason(int reason) {
            this.mReason = reason;
        }

        public int getReason() {
            return this.mReason;
        }
    }

    private void postEventLogToWorkerThread(final TimeFunction logFunction) {
        Instant now = Instant.now();
        final long unixNanos = TimeUnit.NANOSECONDS.convert(now.getEpochSecond(), TimeUnit.SECONDS) + now.getNano();
        final long elapsedNanos = SystemClock.elapsedRealtimeNanos();
        final long realtimeNanos = SystemClock.uptimeNanos();
        this.mWorker.getThreadHandler().post(new Runnable() { // from class: com.android.internal.jank.InteractionJankMonitor$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                InteractionJankMonitor.TimeFunction.this.invoke(unixNanos, elapsedNanos, realtimeNanos);
            }
        });
    }
}
