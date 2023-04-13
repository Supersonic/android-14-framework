package android.view;

import android.graphics.FrameInfo;
import android.hardware.display.DisplayManagerGlobal;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.SystemClock;
import android.p008os.SystemProperties;
import android.p008os.Trace;
import android.util.Log;
import android.util.TimeUtils;
import android.view.DisplayEventReceiver;
import android.view.animation.AnimationUtils;
import java.io.PrintWriter;
/* loaded from: classes4.dex */
public final class Choreographer {
    public static final int CALLBACK_ANIMATION = 1;
    public static final int CALLBACK_COMMIT = 4;
    public static final int CALLBACK_INPUT = 0;
    public static final int CALLBACK_INSETS_ANIMATION = 2;
    private static final int CALLBACK_LAST = 4;
    public static final int CALLBACK_TRAVERSAL = 3;
    private static final boolean DEBUG_FRAMES = false;
    private static final boolean DEBUG_JANK = false;
    private static final long DEFAULT_FRAME_DELAY = 10;
    private static final int MSG_DO_FRAME = 0;
    private static final int MSG_DO_SCHEDULE_CALLBACK = 2;
    private static final int MSG_DO_SCHEDULE_VSYNC = 1;
    private static final String TAG = "Choreographer";
    private static volatile Choreographer mMainInstance;
    private CallbackRecord mCallbackPool;
    private final CallbackQueue[] mCallbackQueues;
    private boolean mCallbacksRunning;
    private boolean mDebugPrintNextFrameTimeDelta;
    private final FrameDisplayEventReceiver mDisplayEventReceiver;
    private int mFPSDivisor;
    private final FrameData mFrameData;
    FrameInfo mFrameInfo;
    @Deprecated
    private long mFrameIntervalNanos;
    private boolean mFrameScheduled;
    private final FrameHandler mHandler;
    private long mLastFrameIntervalNanos;
    private long mLastFrameTimeNanos;
    private DisplayEventReceiver.VsyncEventData mLastVsyncEventData;
    private final Object mLock;
    private final Looper mLooper;
    private static volatile long sFrameDelay = 10;
    private static final ThreadLocal<Choreographer> sThreadInstance = new ThreadLocal<Choreographer>() { // from class: android.view.Choreographer.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Choreographer initialValue() {
            Looper looper = Looper.myLooper();
            if (looper == null) {
                throw new IllegalStateException("The current thread must have a looper!");
            }
            Choreographer choreographer = new Choreographer(looper, 0);
            if (looper == Looper.getMainLooper()) {
                Choreographer.mMainInstance = choreographer;
            }
            return choreographer;
        }
    };
    private static final ThreadLocal<Choreographer> sSfThreadInstance = new ThreadLocal<Choreographer>() { // from class: android.view.Choreographer.2
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Choreographer initialValue() {
            Looper looper = Looper.myLooper();
            if (looper == null) {
                throw new IllegalStateException("The current thread must have a looper!");
            }
            return new Choreographer(looper, 1);
        }
    };
    private static final boolean USE_VSYNC = SystemProperties.getBoolean("debug.choreographer.vsync", true);
    private static final boolean USE_FRAME_TIME = SystemProperties.getBoolean("debug.choreographer.frametime", true);
    private static final int SKIPPED_FRAME_WARNING_LIMIT = SystemProperties.getInt("debug.choreographer.skipwarning", 30);
    private static final Object FRAME_CALLBACK_TOKEN = new Object() { // from class: android.view.Choreographer.3
        public String toString() {
            return "FRAME_CALLBACK_TOKEN";
        }
    };
    private static final Object VSYNC_CALLBACK_TOKEN = new Object() { // from class: android.view.Choreographer.4
        public String toString() {
            return "VSYNC_CALLBACK_TOKEN";
        }
    };
    private static final String[] CALLBACK_TRACE_TITLES = {"input", "animation", "insets_animation", "traversal", "commit"};

    /* loaded from: classes4.dex */
    public interface FrameCallback {
        void doFrame(long j);
    }

    /* loaded from: classes4.dex */
    public interface VsyncCallback {
        void onVsync(FrameData frameData);
    }

    private Choreographer(Looper looper, int vsyncSource) {
        this(looper, vsyncSource, 0L);
    }

    private Choreographer(Looper looper, int vsyncSource, long layerHandle) {
        FrameDisplayEventReceiver frameDisplayEventReceiver;
        this.mLock = new Object();
        this.mFPSDivisor = 1;
        this.mLastVsyncEventData = new DisplayEventReceiver.VsyncEventData();
        this.mFrameData = new FrameData();
        this.mFrameInfo = new FrameInfo();
        this.mLooper = looper;
        this.mHandler = new FrameHandler(looper);
        if (USE_VSYNC) {
            frameDisplayEventReceiver = new FrameDisplayEventReceiver(looper, vsyncSource, layerHandle);
        } else {
            frameDisplayEventReceiver = null;
        }
        this.mDisplayEventReceiver = frameDisplayEventReceiver;
        this.mLastFrameTimeNanos = Long.MIN_VALUE;
        this.mFrameIntervalNanos = 1.0E9f / getRefreshRate();
        this.mCallbackQueues = new CallbackQueue[5];
        for (int i = 0; i <= 4; i++) {
            this.mCallbackQueues[i] = new CallbackQueue();
        }
        setFPSDivisor(SystemProperties.getInt(ThreadedRenderer.DEBUG_FPS_DIVISOR, 1));
    }

    private static float getRefreshRate() {
        DisplayInfo di = DisplayManagerGlobal.getInstance().getDisplayInfo(0);
        return di.getRefreshRate();
    }

    public static Choreographer getInstance() {
        return sThreadInstance.get();
    }

    public static Choreographer getSfInstance() {
        return sSfThreadInstance.get();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Choreographer getInstanceForSurfaceControl(long layerHandle, Looper looper) {
        if (looper == null) {
            throw new IllegalStateException("The current thread must have a looper!");
        }
        return new Choreographer(looper, 0, layerHandle);
    }

    public static Choreographer getMainThreadInstance() {
        return mMainInstance;
    }

    public static void releaseInstance() {
        ThreadLocal<Choreographer> threadLocal = sThreadInstance;
        Choreographer old = threadLocal.get();
        threadLocal.remove();
        old.dispose();
    }

    private void dispose() {
        this.mDisplayEventReceiver.dispose();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        dispose();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTheLooperSame(Looper looper) {
        return this.mLooper == looper;
    }

    public static long getFrameDelay() {
        return sFrameDelay;
    }

    public static void setFrameDelay(long frameDelay) {
        sFrameDelay = frameDelay;
    }

    public static long subtractFrameDelay(long delayMillis) {
        long frameDelay = sFrameDelay;
        if (delayMillis <= frameDelay) {
            return 0L;
        }
        return delayMillis - frameDelay;
    }

    public long getFrameIntervalNanos() {
        long j;
        synchronized (this.mLock) {
            j = this.mLastFrameIntervalNanos;
        }
        return j;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(String prefix, PrintWriter writer) {
        String innerPrefix = prefix + "  ";
        writer.print(prefix);
        writer.println("Choreographer:");
        writer.print(innerPrefix);
        writer.print("mFrameScheduled=");
        writer.println(this.mFrameScheduled);
        writer.print(innerPrefix);
        writer.print("mLastFrameTime=");
        writer.println(TimeUtils.formatUptime(this.mLastFrameTimeNanos / 1000000));
    }

    public void postCallback(int callbackType, Runnable action, Object token) {
        postCallbackDelayed(callbackType, action, token, 0L);
    }

    public void postCallbackDelayed(int callbackType, Runnable action, Object token, long delayMillis) {
        if (action == null) {
            throw new IllegalArgumentException("action must not be null");
        }
        if (callbackType < 0 || callbackType > 4) {
            throw new IllegalArgumentException("callbackType is invalid");
        }
        postCallbackDelayedInternal(callbackType, action, token, delayMillis);
    }

    private void postCallbackDelayedInternal(int callbackType, Object action, Object token, long delayMillis) {
        synchronized (this.mLock) {
            long now = SystemClock.uptimeMillis();
            long dueTime = now + delayMillis;
            this.mCallbackQueues[callbackType].addCallbackLocked(dueTime, action, token);
            if (dueTime <= now) {
                scheduleFrameLocked(now);
            } else {
                Message msg = this.mHandler.obtainMessage(2, action);
                msg.arg1 = callbackType;
                msg.setAsynchronous(true);
                this.mHandler.sendMessageAtTime(msg, dueTime);
            }
        }
    }

    public void postVsyncCallback(VsyncCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        postCallbackDelayedInternal(1, callback, VSYNC_CALLBACK_TOKEN, 0L);
    }

    public void removeCallbacks(int callbackType, Runnable action, Object token) {
        if (callbackType < 0 || callbackType > 4) {
            throw new IllegalArgumentException("callbackType is invalid");
        }
        removeCallbacksInternal(callbackType, action, token);
    }

    private void removeCallbacksInternal(int callbackType, Object action, Object token) {
        synchronized (this.mLock) {
            this.mCallbackQueues[callbackType].removeCallbacksLocked(action, token);
            if (action != null && token == null) {
                this.mHandler.removeMessages(2, action);
            }
        }
    }

    public void postFrameCallback(FrameCallback callback) {
        postFrameCallbackDelayed(callback, 0L);
    }

    public void postFrameCallbackDelayed(FrameCallback callback, long delayMillis) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        postCallbackDelayedInternal(1, callback, FRAME_CALLBACK_TOKEN, delayMillis);
    }

    public void removeFrameCallback(FrameCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        removeCallbacksInternal(1, callback, FRAME_CALLBACK_TOKEN);
    }

    public void removeVsyncCallback(VsyncCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        removeCallbacksInternal(1, callback, VSYNC_CALLBACK_TOKEN);
    }

    public long getFrameTime() {
        return getFrameTimeNanos() / 1000000;
    }

    public long getFrameTimeNanos() {
        long nanoTime;
        synchronized (this.mLock) {
            if (!this.mCallbacksRunning) {
                throw new IllegalStateException("This method must only be called as part of a callback while a frame is in progress.");
            }
            nanoTime = USE_FRAME_TIME ? this.mLastFrameTimeNanos : System.nanoTime();
        }
        return nanoTime;
    }

    public long getLastFrameTimeNanos() {
        long nanoTime;
        synchronized (this.mLock) {
            nanoTime = USE_FRAME_TIME ? this.mLastFrameTimeNanos : System.nanoTime();
        }
        return nanoTime;
    }

    private void scheduleFrameLocked(long now) {
        if (!this.mFrameScheduled) {
            this.mFrameScheduled = true;
            if (USE_VSYNC) {
                if (!isRunningOnLooperThreadLocked()) {
                    Message msg = this.mHandler.obtainMessage(1);
                    msg.setAsynchronous(true);
                    this.mHandler.sendMessageAtFrontOfQueue(msg);
                    return;
                }
                scheduleVsyncLocked();
                return;
            }
            long nextFrameTime = Math.max((this.mLastFrameTimeNanos / 1000000) + sFrameDelay, now);
            Message msg2 = this.mHandler.obtainMessage(0);
            msg2.setAsynchronous(true);
            this.mHandler.sendMessageAtTime(msg2, nextFrameTime);
        }
    }

    public long getVsyncId() {
        return this.mLastVsyncEventData.preferredFrameTimeline().vsyncId;
    }

    public long getFrameDeadline() {
        return this.mLastVsyncEventData.preferredFrameTimeline().deadline;
    }

    void setFPSDivisor(int divisor) {
        if (divisor <= 0) {
            divisor = 1;
        }
        this.mFPSDivisor = divisor;
        ThreadedRenderer.setFPSDivisor(divisor);
    }

    private void traceMessage(String msg) {
        Trace.traceBegin(8L, msg);
        Trace.traceEnd(8L);
    }

    void doFrame(long frameTimeNanos, int frame, DisplayEventReceiver.VsyncEventData vsyncEventData) {
        long j;
        Object obj;
        long frameTimeNanos2;
        long j2;
        long frameIntervalNanos = vsyncEventData.frameInterval;
        long skippedFrames = 8;
        try {
            if (Trace.isTagEnabled(8L)) {
                try {
                    Trace.traceBegin(8L, "Choreographer#doFrame " + vsyncEventData.preferredFrameTimeline().vsyncId);
                } catch (Throwable th) {
                    th = th;
                    j = 8;
                    AnimationUtils.unlockAnimationClock();
                    Trace.traceEnd(j);
                    throw th;
                }
            }
            this.mFrameData.update(frameTimeNanos, vsyncEventData);
            Object obj2 = this.mLock;
            try {
                try {
                } catch (Throwable th2) {
                    th = th2;
                    AnimationUtils.unlockAnimationClock();
                    Trace.traceEnd(j);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
            synchronized (obj2) {
                try {
                    if (this.mFrameScheduled) {
                        long startNanos = System.nanoTime();
                        long jitterNanos = startNanos - frameTimeNanos;
                        if (jitterNanos >= frameIntervalNanos) {
                            long frameTimeNanos3 = startNanos;
                            try {
                                if (frameIntervalNanos == 0) {
                                    Log.m108i(TAG, "Vsync data empty due to timeout");
                                } else {
                                    long lastFrameOffset = jitterNanos % frameIntervalNanos;
                                    frameTimeNanos3 -= lastFrameOffset;
                                    skippedFrames = jitterNanos / frameIntervalNanos;
                                    if (skippedFrames >= SKIPPED_FRAME_WARNING_LIMIT) {
                                        try {
                                            Log.m108i(TAG, "Skipped " + skippedFrames + " frames!  The application may be doing too much work on its main thread.");
                                        } catch (Throwable th4) {
                                            th = th4;
                                            obj = obj2;
                                        }
                                    }
                                }
                                this.mFrameData.update(frameTimeNanos3, this.mDisplayEventReceiver, jitterNanos);
                                frameTimeNanos2 = frameTimeNanos3;
                            } catch (Throwable th5) {
                                th = th5;
                                obj = obj2;
                            }
                        } else {
                            frameTimeNanos2 = frameTimeNanos;
                        }
                        try {
                            long j3 = this.mLastFrameTimeNanos;
                            try {
                                if (frameTimeNanos2 < j3) {
                                    traceMessage("Frame time goes backward");
                                    scheduleVsyncLocked();
                                    AnimationUtils.unlockAnimationClock();
                                    Trace.traceEnd(8L);
                                    return;
                                }
                                int i = this.mFPSDivisor;
                                if (i > 1) {
                                    long timeSinceVsync = frameTimeNanos2 - j3;
                                    if (timeSinceVsync < i * frameIntervalNanos && timeSinceVsync > 0) {
                                        traceMessage("Frame skipped due to FPSDivisor");
                                        scheduleVsyncLocked();
                                        AnimationUtils.unlockAnimationClock();
                                        Trace.traceEnd(8L);
                                        return;
                                    }
                                    j2 = 8;
                                } else {
                                    j2 = 8;
                                }
                                try {
                                    try {
                                        obj = obj2;
                                        long frameTimeNanos4 = frameTimeNanos2;
                                        try {
                                            this.mFrameInfo.setVsync(frameTimeNanos, frameTimeNanos2, vsyncEventData.preferredFrameTimeline().vsyncId, vsyncEventData.preferredFrameTimeline().deadline, startNanos, vsyncEventData.frameInterval);
                                            this.mFrameScheduled = false;
                                            try {
                                                this.mLastFrameTimeNanos = frameTimeNanos4;
                                                this.mLastFrameIntervalNanos = frameIntervalNanos;
                                                this.mLastVsyncEventData = vsyncEventData;
                                                AnimationUtils.lockAnimationClock(frameTimeNanos4 / 1000000);
                                                this.mFrameInfo.markInputHandlingStart();
                                                doCallbacks(0, frameIntervalNanos);
                                                this.mFrameInfo.markAnimationsStart();
                                                doCallbacks(1, frameIntervalNanos);
                                                doCallbacks(2, frameIntervalNanos);
                                                this.mFrameInfo.markPerformTraversalsStart();
                                                doCallbacks(3, frameIntervalNanos);
                                                doCallbacks(4, frameIntervalNanos);
                                                AnimationUtils.unlockAnimationClock();
                                                Trace.traceEnd(j2);
                                                return;
                                            } catch (Throwable th6) {
                                                th = th6;
                                            }
                                        } catch (Throwable th7) {
                                            th = th7;
                                        }
                                    } catch (Throwable th8) {
                                        th = th8;
                                        obj = obj2;
                                    }
                                } catch (Throwable th9) {
                                    th = th9;
                                    obj = obj2;
                                }
                            } catch (Throwable th10) {
                                th = th10;
                                obj = obj2;
                            }
                        } catch (Throwable th11) {
                            th = th11;
                            obj = obj2;
                        }
                    } else {
                        try {
                            traceMessage("Frame not scheduled");
                            AnimationUtils.unlockAnimationClock();
                            Trace.traceEnd(8L);
                            return;
                        } catch (Throwable th12) {
                            th = th12;
                            obj = obj2;
                        }
                    }
                } catch (Throwable th13) {
                    th = th13;
                    obj = obj2;
                }
                throw th;
            }
        } catch (Throwable th14) {
            th = th14;
            j = 8;
        }
    }

    void doCallbacks(int callbackType, long frameIntervalNanos) {
        long frameTimeNanos = this.mFrameData.mFrameTimeNanos;
        synchronized (this.mLock) {
            long now = System.nanoTime();
            CallbackRecord callbacks = this.mCallbackQueues[callbackType].extractDueCallbacksLocked(now / 1000000);
            if (callbacks == null) {
                return;
            }
            this.mCallbacksRunning = true;
            if (callbackType == 4) {
                long jitterNanos = now - frameTimeNanos;
                Trace.traceCounter(8L, "jitterNanos", (int) jitterNanos);
                if (jitterNanos >= 2 * frameIntervalNanos) {
                    long lastFrameOffset = (jitterNanos % frameIntervalNanos) + frameIntervalNanos;
                    long frameTimeNanos2 = now - lastFrameOffset;
                    this.mLastFrameTimeNanos = frameTimeNanos2;
                    this.mFrameData.update(frameTimeNanos2, this.mDisplayEventReceiver, jitterNanos);
                }
            }
            try {
                Trace.traceBegin(8L, CALLBACK_TRACE_TITLES[callbackType]);
                for (CallbackRecord c = callbacks; c != null; c = c.next) {
                    c.run(this.mFrameData);
                }
                synchronized (this.mLock) {
                    this.mCallbacksRunning = false;
                    do {
                        CallbackRecord next = callbacks.next;
                        recycleCallbackLocked(callbacks);
                        callbacks = next;
                    } while (callbacks != null);
                }
                Trace.traceEnd(8L);
            } catch (Throwable th) {
                synchronized (this.mLock) {
                    this.mCallbacksRunning = false;
                    while (true) {
                        CallbackRecord next2 = callbacks.next;
                        recycleCallbackLocked(callbacks);
                        callbacks = next2;
                        if (callbacks == null) {
                            break;
                        }
                    }
                    Trace.traceEnd(8L);
                    throw th;
                }
            }
        }
    }

    void doScheduleVsync() {
        synchronized (this.mLock) {
            if (this.mFrameScheduled) {
                scheduleVsyncLocked();
            }
        }
    }

    void doScheduleCallback(int callbackType) {
        synchronized (this.mLock) {
            if (!this.mFrameScheduled) {
                long now = SystemClock.uptimeMillis();
                if (this.mCallbackQueues[callbackType].hasDueCallbacksLocked(now)) {
                    scheduleFrameLocked(now);
                }
            }
        }
    }

    private void scheduleVsyncLocked() {
        try {
            Trace.traceBegin(8L, "Choreographer#scheduleVsyncLocked");
            this.mDisplayEventReceiver.scheduleVsync();
        } finally {
            Trace.traceEnd(8L);
        }
    }

    private boolean isRunningOnLooperThreadLocked() {
        return Looper.myLooper() == this.mLooper;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CallbackRecord obtainCallbackLocked(long dueTime, Object action, Object token) {
        CallbackRecord callback = this.mCallbackPool;
        if (callback == null) {
            callback = new CallbackRecord();
        } else {
            this.mCallbackPool = callback.next;
            callback.next = null;
        }
        callback.dueTime = dueTime;
        callback.action = action;
        callback.token = token;
        return callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recycleCallbackLocked(CallbackRecord callback) {
        callback.action = null;
        callback.token = null;
        callback.next = this.mCallbackPool;
        this.mCallbackPool = callback;
    }

    /* loaded from: classes4.dex */
    public static class FrameTimeline {
        private long mVsyncId = -1;
        private long mExpectedPresentationTimeNanos = -1;
        private long mDeadlineNanos = -1;
        private boolean mInCallback = false;

        FrameTimeline() {
        }

        void setInCallback(boolean inCallback) {
            this.mInCallback = inCallback;
        }

        private void checkInCallback() {
            if (!this.mInCallback) {
                throw new IllegalStateException("FrameTimeline is not valid outside of the vsync callback");
            }
        }

        void update(long vsyncId, long expectedPresentationTimeNanos, long deadlineNanos) {
            this.mVsyncId = vsyncId;
            this.mExpectedPresentationTimeNanos = expectedPresentationTimeNanos;
            this.mDeadlineNanos = deadlineNanos;
        }

        public long getVsyncId() {
            checkInCallback();
            return this.mVsyncId;
        }

        public long getExpectedPresentationTimeNanos() {
            checkInCallback();
            return this.mExpectedPresentationTimeNanos;
        }

        public long getDeadlineNanos() {
            checkInCallback();
            return this.mDeadlineNanos;
        }
    }

    /* loaded from: classes4.dex */
    public static class FrameData {
        private long mFrameTimeNanos;
        private final FrameTimeline[] mFrameTimelines = new FrameTimeline[7];
        private boolean mInCallback = false;
        private int mPreferredFrameTimelineIndex;

        FrameData() {
            int i = 0;
            while (true) {
                FrameTimeline[] frameTimelineArr = this.mFrameTimelines;
                if (i < frameTimelineArr.length) {
                    frameTimelineArr[i] = new FrameTimeline();
                    i++;
                } else {
                    return;
                }
            }
        }

        public long getFrameTimeNanos() {
            checkInCallback();
            return this.mFrameTimeNanos;
        }

        public FrameTimeline[] getFrameTimelines() {
            checkInCallback();
            return this.mFrameTimelines;
        }

        public FrameTimeline getPreferredFrameTimeline() {
            checkInCallback();
            return this.mFrameTimelines[this.mPreferredFrameTimelineIndex];
        }

        void setInCallback(boolean inCallback) {
            this.mInCallback = inCallback;
            int i = 0;
            while (true) {
                FrameTimeline[] frameTimelineArr = this.mFrameTimelines;
                if (i < frameTimelineArr.length) {
                    frameTimelineArr[i].setInCallback(inCallback);
                    i++;
                } else {
                    return;
                }
            }
        }

        private void checkInCallback() {
            if (!this.mInCallback) {
                throw new IllegalStateException("FrameData is not valid outside of the vsync callback");
            }
        }

        void update(long frameTimeNanos, DisplayEventReceiver.VsyncEventData vsyncEventData) {
            if (vsyncEventData.frameTimelines.length != this.mFrameTimelines.length) {
                throw new IllegalStateException("Length of native frame timelines received does not match Java. Did FRAME_TIMELINES_LENGTH or kFrameTimelinesLength (native) change?");
            }
            this.mFrameTimeNanos = frameTimeNanos;
            this.mPreferredFrameTimelineIndex = vsyncEventData.preferredFrameTimelineIndex;
            for (int i = 0; i < vsyncEventData.frameTimelines.length; i++) {
                DisplayEventReceiver.VsyncEventData.FrameTimeline frameTimeline = vsyncEventData.frameTimelines[i];
                this.mFrameTimelines[i].update(frameTimeline.vsyncId, frameTimeline.expectedPresentationTime, frameTimeline.deadline);
            }
        }

        void update(long frameTimeNanos, DisplayEventReceiver displayEventReceiver, long jitterNanos) {
            int newPreferredIndex = 0;
            long minimumDeadline = this.mFrameTimelines[this.mPreferredFrameTimelineIndex].mDeadlineNanos + jitterNanos;
            while (true) {
                FrameTimeline[] frameTimelineArr = this.mFrameTimelines;
                if (newPreferredIndex >= frameTimelineArr.length - 1 || frameTimelineArr[newPreferredIndex].mDeadlineNanos >= minimumDeadline) {
                    break;
                }
                newPreferredIndex++;
            }
            long newPreferredDeadline = this.mFrameTimelines[newPreferredIndex].mDeadlineNanos;
            if (newPreferredDeadline < minimumDeadline) {
                DisplayEventReceiver.VsyncEventData latestVsyncEventData = displayEventReceiver.getLatestVsyncEventData();
                update(frameTimeNanos, latestVsyncEventData);
                return;
            }
            update(frameTimeNanos, newPreferredIndex);
        }

        void update(long frameTimeNanos, int newPreferredFrameTimelineIndex) {
            this.mFrameTimeNanos = frameTimeNanos;
            this.mPreferredFrameTimelineIndex = newPreferredFrameTimelineIndex;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class FrameHandler extends Handler {
        public FrameHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Choreographer.this.doFrame(System.nanoTime(), 0, new DisplayEventReceiver.VsyncEventData());
                    return;
                case 1:
                    Choreographer.this.doScheduleVsync();
                    return;
                case 2:
                    Choreographer.this.doScheduleCallback(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class FrameDisplayEventReceiver extends DisplayEventReceiver implements Runnable {
        private int mFrame;
        private boolean mHavePendingVsync;
        private DisplayEventReceiver.VsyncEventData mLastVsyncEventData;
        private long mTimestampNanos;

        FrameDisplayEventReceiver(Looper looper, int vsyncSource, long layerHandle) {
            super(looper, vsyncSource, 0, layerHandle);
            this.mLastVsyncEventData = new DisplayEventReceiver.VsyncEventData();
        }

        @Override // android.view.DisplayEventReceiver
        public void onVsync(long timestampNanos, long physicalDisplayId, int frame, DisplayEventReceiver.VsyncEventData vsyncEventData) {
            try {
                if (Trace.isTagEnabled(8L)) {
                    Trace.traceBegin(8L, "Choreographer#onVsync " + vsyncEventData.preferredFrameTimeline().vsyncId);
                }
                long now = System.nanoTime();
                if (timestampNanos > now) {
                    Log.m104w(Choreographer.TAG, "Frame time is " + (((float) (timestampNanos - now)) * 1.0E-6f) + " ms in the future!  Check that graphics HAL is generating vsync timestamps using the correct timebase.");
                    timestampNanos = now;
                }
                if (this.mHavePendingVsync) {
                    Log.m104w(Choreographer.TAG, "Already have a pending vsync event.  There should only be one at a time.");
                } else {
                    this.mHavePendingVsync = true;
                }
                this.mTimestampNanos = timestampNanos;
                this.mFrame = frame;
                this.mLastVsyncEventData = vsyncEventData;
                Message msg = Message.obtain(Choreographer.this.mHandler, this);
                msg.setAsynchronous(true);
                Choreographer.this.mHandler.sendMessageAtTime(msg, timestampNanos / 1000000);
            } finally {
                Trace.traceEnd(8L);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mHavePendingVsync = false;
            Choreographer.this.doFrame(this.mTimestampNanos, this.mFrame, this.mLastVsyncEventData);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class CallbackRecord {
        public Object action;
        public long dueTime;
        public CallbackRecord next;
        public Object token;

        private CallbackRecord() {
        }

        public void run(long frameTimeNanos) {
            if (this.token == Choreographer.FRAME_CALLBACK_TOKEN) {
                ((FrameCallback) this.action).doFrame(frameTimeNanos);
            } else {
                ((Runnable) this.action).run();
            }
        }

        void run(FrameData frameData) {
            frameData.setInCallback(true);
            if (this.token == Choreographer.VSYNC_CALLBACK_TOKEN) {
                ((VsyncCallback) this.action).onVsync(frameData);
            } else {
                run(frameData.getFrameTimeNanos());
            }
            frameData.setInCallback(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class CallbackQueue {
        private CallbackRecord mHead;

        private CallbackQueue() {
        }

        public boolean hasDueCallbacksLocked(long now) {
            CallbackRecord callbackRecord = this.mHead;
            return callbackRecord != null && callbackRecord.dueTime <= now;
        }

        public CallbackRecord extractDueCallbacksLocked(long now) {
            CallbackRecord callbacks = this.mHead;
            if (callbacks == null || callbacks.dueTime > now) {
                return null;
            }
            CallbackRecord last = callbacks;
            CallbackRecord next = last.next;
            while (true) {
                if (next == null) {
                    break;
                } else if (next.dueTime > now) {
                    last.next = null;
                    break;
                } else {
                    last = next;
                    next = next.next;
                }
            }
            this.mHead = next;
            return callbacks;
        }

        public void addCallbackLocked(long dueTime, Object action, Object token) {
            CallbackRecord callback = Choreographer.this.obtainCallbackLocked(dueTime, action, token);
            CallbackRecord entry = this.mHead;
            if (entry == null) {
                this.mHead = callback;
            } else if (dueTime < entry.dueTime) {
                callback.next = entry;
                this.mHead = callback;
            } else {
                while (true) {
                    if (entry.next == null) {
                        break;
                    } else if (dueTime < entry.next.dueTime) {
                        callback.next = entry.next;
                        break;
                    } else {
                        entry = entry.next;
                    }
                }
                entry.next = callback;
            }
        }

        public void removeCallbacksLocked(Object action, Object token) {
            CallbackRecord predecessor = null;
            CallbackRecord callback = this.mHead;
            while (callback != null) {
                CallbackRecord next = callback.next;
                if ((action == null || callback.action == action) && (token == null || callback.token == token)) {
                    if (predecessor != null) {
                        predecessor.next = next;
                    } else {
                        this.mHead = next;
                    }
                    Choreographer.this.recycleCallbackLocked(callback);
                } else {
                    predecessor = callback;
                }
                callback = next;
            }
        }
    }
}
