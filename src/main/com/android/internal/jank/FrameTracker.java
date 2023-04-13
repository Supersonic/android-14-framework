package com.android.internal.jank;

import android.graphics.HardwareRendererObserver;
import android.p008os.Handler;
import android.p008os.Trace;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Choreographer;
import android.view.FrameMetrics;
import android.view.SurfaceControl;
import android.view.ThreadedRenderer;
import android.view.ViewRootImpl;
import com.android.internal.jank.FrameTracker;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.internal.util.FrameworkStatsLog;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;
/* loaded from: classes4.dex */
public class FrameTracker extends SurfaceControl.OnJankDataListener implements HardwareRendererObserver.OnFrameMetricsAvailableListener {
    private static final boolean DEBUG = false;
    private static final int FLUSH_DELAY_MILLISECOND = 60;
    private static final long INVALID_ID = -1;
    private static final int MAX_FLUSH_ATTEMPTS = 3;
    private static final int MAX_LENGTH_EVENT_DESC = 20;
    public static final int NANOS_IN_MILLISECOND = 1000000;
    static final int REASON_CANCEL_NORMAL = 16;
    static final int REASON_CANCEL_NOT_BEGUN = 17;
    static final int REASON_CANCEL_SAME_VSYNC = 18;
    static final int REASON_CANCEL_TIMEOUT = 19;
    static final int REASON_END_NORMAL = 0;
    static final int REASON_END_SURFACE_DESTROYED = 1;
    static final int REASON_END_UNKNOWN = -1;
    private static final String TAG = "FrameTracker";
    private final ChoreographerWrapper mChoreographer;
    private final boolean mDeferMonitoring;
    private final int mDisplayId;
    private final Handler mHandler;
    private final FrameTrackerListener mListener;
    private boolean mMetricsFinalized;
    private final FrameMetricsWrapper mMetricsWrapper;
    public final InteractionJankMonitor mMonitor;
    private final HardwareRendererObserver mObserver;
    private final ThreadedRendererWrapper mRendererWrapper;
    private final InteractionJankMonitor.Session mSession;
    private final StatsLogWrapper mStatsLog;
    private final ViewRootImpl.SurfaceChangedCallback mSurfaceChangedCallback;
    private SurfaceControl mSurfaceControl;
    private final SurfaceControlWrapper mSurfaceControlWrapper;
    public final boolean mSurfaceOnly;
    private final int mTraceThresholdFrameTimeMillis;
    private final int mTraceThresholdMissedFrames;
    private final ViewRootWrapper mViewRoot;
    private Runnable mWaitForFinishTimedOut;
    private final SparseArray<JankInfo> mJankInfos = new SparseArray<>();
    private long mBeginVsyncId = -1;
    private long mEndVsyncId = -1;
    private boolean mCancelled = false;
    private boolean mTracingStarted = false;

    /* loaded from: classes4.dex */
    public interface FrameTrackerListener {
        void onCujEvents(InteractionJankMonitor.Session session, String str);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Reasons {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class JankInfo {
        long frameVsyncId;
        boolean hwuiCallbackFired;
        boolean isFirstFrame;
        int jankType;
        boolean surfaceControlCallbackFired;
        long totalDurationNanos;

        static JankInfo createFromHwuiCallback(long frameVsyncId, long totalDurationNanos, boolean isFirstFrame) {
            return new JankInfo(frameVsyncId, true, false, 0, totalDurationNanos, isFirstFrame);
        }

        static JankInfo createFromSurfaceControlCallback(long frameVsyncId, int jankType) {
            return new JankInfo(frameVsyncId, false, true, jankType, 0L, false);
        }

        private JankInfo(long frameVsyncId, boolean hwuiCallbackFired, boolean surfaceControlCallbackFired, int jankType, long totalDurationNanos, boolean isFirstFrame) {
            this.frameVsyncId = frameVsyncId;
            this.hwuiCallbackFired = hwuiCallbackFired;
            this.surfaceControlCallbackFired = surfaceControlCallbackFired;
            this.totalDurationNanos = totalDurationNanos;
            this.jankType = jankType;
            this.isFirstFrame = isFirstFrame;
        }

        public String toString() {
            StringBuilder str = new StringBuilder();
            switch (this.jankType) {
                case 0:
                    str.append("JANK_NONE");
                    break;
                case 1:
                    str.append("DISPLAY_HAL");
                    break;
                case 2:
                    str.append("JANK_SURFACEFLINGER_DEADLINE_MISSED");
                    break;
                case 4:
                    str.append("JANK_SURFACEFLINGER_GPU_DEADLINE_MISSED");
                    break;
                case 8:
                    str.append("JANK_APP_DEADLINE_MISSED");
                    break;
                case 16:
                    str.append("PREDICTION_ERROR");
                    break;
                case 32:
                    str.append("SURFACE_FLINGER_SCHEDULING");
                    break;
                default:
                    str.append("UNKNOWN: ").append(this.jankType);
                    break;
            }
            str.append(", ").append(this.frameVsyncId);
            str.append(", ").append(this.totalDurationNanos);
            return str.toString();
        }
    }

    public FrameTracker(InteractionJankMonitor monitor, InteractionJankMonitor.Session session, Handler handler, ThreadedRendererWrapper renderer, ViewRootWrapper viewRootWrapper, SurfaceControlWrapper surfaceControlWrapper, ChoreographerWrapper choreographer, FrameMetricsWrapper metrics, StatsLogWrapper statsLog, int traceThresholdMissedFrames, int traceThresholdFrameTimeMillis, FrameTrackerListener listener, InteractionJankMonitor.Configuration config) {
        HardwareRendererObserver hardwareRendererObserver;
        this.mMonitor = monitor;
        boolean isSurfaceOnly = config.isSurfaceOnly();
        this.mSurfaceOnly = isSurfaceOnly;
        this.mSession = session;
        this.mHandler = handler;
        this.mChoreographer = choreographer;
        this.mSurfaceControlWrapper = surfaceControlWrapper;
        this.mStatsLog = statsLog;
        this.mDeferMonitoring = config.shouldDeferMonitor();
        this.mRendererWrapper = isSurfaceOnly ? null : renderer;
        FrameMetricsWrapper frameMetricsWrapper = isSurfaceOnly ? null : metrics;
        this.mMetricsWrapper = frameMetricsWrapper;
        ViewRootWrapper viewRootWrapper2 = isSurfaceOnly ? null : viewRootWrapper;
        this.mViewRoot = viewRootWrapper2;
        if (isSurfaceOnly) {
            hardwareRendererObserver = null;
        } else {
            hardwareRendererObserver = new HardwareRendererObserver(this, frameMetricsWrapper.getTiming(), handler, false);
        }
        this.mObserver = hardwareRendererObserver;
        this.mTraceThresholdMissedFrames = traceThresholdMissedFrames;
        this.mTraceThresholdFrameTimeMillis = traceThresholdFrameTimeMillis;
        this.mListener = listener;
        this.mDisplayId = config.getDisplayId();
        if (isSurfaceOnly) {
            this.mSurfaceControl = config.getSurfaceControl();
            this.mSurfaceChangedCallback = null;
            return;
        }
        if (viewRootWrapper2.getSurfaceControl().isValid()) {
            this.mSurfaceControl = viewRootWrapper2.getSurfaceControl();
        }
        ViewRootImpl$SurfaceChangedCallbackC41751 viewRootImpl$SurfaceChangedCallbackC41751 = new ViewRootImpl$SurfaceChangedCallbackC41751();
        this.mSurfaceChangedCallback = viewRootImpl$SurfaceChangedCallbackC41751;
        viewRootWrapper2.addSurfaceChangedCallback(viewRootImpl$SurfaceChangedCallbackC41751);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.jank.FrameTracker$1 */
    /* loaded from: classes4.dex */
    public class ViewRootImpl$SurfaceChangedCallbackC41751 implements ViewRootImpl.SurfaceChangedCallback {
        ViewRootImpl$SurfaceChangedCallbackC41751() {
        }

        @Override // android.view.ViewRootImpl.SurfaceChangedCallback
        public void surfaceCreated(SurfaceControl.Transaction t) {
            FrameTracker.this.getHandler().runWithScissors(new Runnable() { // from class: com.android.internal.jank.FrameTracker$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    FrameTracker.ViewRootImpl$SurfaceChangedCallbackC41751.this.lambda$surfaceCreated$0();
                }
            }, 500L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$surfaceCreated$0() {
            if (FrameTracker.this.mSurfaceControl == null) {
                FrameTracker frameTracker = FrameTracker.this;
                frameTracker.mSurfaceControl = frameTracker.mViewRoot.getSurfaceControl();
                if (FrameTracker.this.mBeginVsyncId != -1) {
                    FrameTracker.this.begin();
                }
            }
        }

        @Override // android.view.ViewRootImpl.SurfaceChangedCallback
        public void surfaceReplaced(SurfaceControl.Transaction t) {
        }

        @Override // android.view.ViewRootImpl.SurfaceChangedCallback
        public void surfaceDestroyed() {
            FrameTracker.this.getHandler().postDelayed(new Runnable() { // from class: com.android.internal.jank.FrameTracker$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FrameTracker.ViewRootImpl$SurfaceChangedCallbackC41751.this.lambda$surfaceDestroyed$1();
                }
            }, 50L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$surfaceDestroyed$1() {
            if (!FrameTracker.this.mMetricsFinalized) {
                FrameTracker.this.end(1);
                FrameTracker.this.finish();
            }
        }
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void begin() {
        long currentVsync = this.mChoreographer.getVsyncId();
        if (this.mBeginVsyncId == -1) {
            this.mBeginVsyncId = this.mDeferMonitoring ? 1 + currentVsync : currentVsync;
        }
        if (this.mSurfaceControl != null) {
            if (this.mDeferMonitoring && currentVsync < this.mBeginVsyncId) {
                markEvent("FT#deferMonitoring");
                postTraceStartMarker(new Runnable() { // from class: com.android.internal.jank.FrameTracker$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        FrameTracker.this.beginInternal();
                    }
                });
                return;
            }
            beginInternal();
        }
    }

    public void postTraceStartMarker(Runnable action) {
        this.mChoreographer.mChoreographer.postCallback(0, action, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void beginInternal() {
        if (this.mCancelled || this.mEndVsyncId != -1) {
            return;
        }
        this.mTracingStarted = true;
        markEvent("FT#begin");
        Trace.beginAsyncSection(this.mSession.getName(), (int) this.mBeginVsyncId);
        this.mSurfaceControlWrapper.addJankStatsListener(this, this.mSurfaceControl);
        if (!this.mSurfaceOnly) {
            this.mRendererWrapper.addObserver(this.mObserver);
        }
    }

    public boolean end(int reason) {
        if (this.mCancelled || this.mEndVsyncId != -1) {
            return false;
        }
        long vsyncId = this.mChoreographer.getVsyncId();
        this.mEndVsyncId = vsyncId;
        long j = this.mBeginVsyncId;
        if (j == -1) {
            return cancel(17);
        }
        if (vsyncId <= j) {
            return cancel(18);
        }
        markEvent("FT#end#" + reason);
        Trace.endAsyncSection(this.mSession.getName(), (int) this.mBeginVsyncId);
        this.mSession.setReason(reason);
        this.mWaitForFinishTimedOut = new RunnableC41762();
        getHandler().postDelayed(this.mWaitForFinishTimedOut, 60L);
        notifyCujEvent(InteractionJankMonitor.ACTION_SESSION_END);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.jank.FrameTracker$2 */
    /* loaded from: classes4.dex */
    public class RunnableC41762 implements Runnable {
        private int mFlushAttempts = 0;

        RunnableC41762() {
        }

        @Override // java.lang.Runnable
        public void run() {
            long delay;
            if (FrameTracker.this.mWaitForFinishTimedOut == null || FrameTracker.this.mMetricsFinalized) {
                return;
            }
            if (FrameTracker.this.mSurfaceControl != null && FrameTracker.this.mSurfaceControl.isValid()) {
                SurfaceControl.Transaction.sendSurfaceFlushJankData(FrameTracker.this.mSurfaceControl);
            }
            int i = this.mFlushAttempts;
            if (i < 3) {
                delay = 60;
                this.mFlushAttempts = i + 1;
            } else {
                FrameTracker.this.mWaitForFinishTimedOut = new Runnable() { // from class: com.android.internal.jank.FrameTracker$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        FrameTracker.RunnableC41762.this.lambda$run$0();
                    }
                };
                delay = TimeUnit.SECONDS.toMillis(10L);
            }
            FrameTracker.this.getHandler().postDelayed(FrameTracker.this.mWaitForFinishTimedOut, delay);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0() {
            Log.m110e(FrameTracker.TAG, "force finish cuj, time out: " + FrameTracker.this.mSession.getName());
            FrameTracker.this.finish();
        }
    }

    public boolean cancel(int reason) {
        boolean cancelFromEnd = reason == 17 || reason == 18;
        if (this.mCancelled || !(this.mEndVsyncId == -1 || cancelFromEnd)) {
            return false;
        }
        this.mCancelled = true;
        markEvent("FT#cancel#" + reason);
        if (this.mTracingStarted) {
            Trace.endAsyncSection(this.mSession.getName(), (int) this.mBeginVsyncId);
        }
        removeObservers();
        this.mSession.setReason(reason);
        notifyCujEvent(InteractionJankMonitor.ACTION_SESSION_CANCEL);
        return true;
    }

    private void markEvent(String desc) {
        if (desc.length() > 20) {
            throw new IllegalArgumentException(TextUtils.formatSimple("The length of the trace event description <%s> exceeds %d", desc, 20));
        }
        Trace.beginSection(TextUtils.formatSimple("%s#%s", this.mSession.getName(), desc));
        Trace.endSection();
    }

    private void notifyCujEvent(String action) {
        FrameTrackerListener frameTrackerListener = this.mListener;
        if (frameTrackerListener == null) {
            return;
        }
        frameTrackerListener.onCujEvents(this.mSession, action);
    }

    @Override // android.view.SurfaceControl.OnJankDataListener
    public void onJankDataAvailable(final SurfaceControl.JankData[] jankData) {
        postCallback(new Runnable() { // from class: com.android.internal.jank.FrameTracker$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FrameTracker.this.lambda$onJankDataAvailable$0(jankData);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onJankDataAvailable$0(SurfaceControl.JankData[] jankData) {
        if (this.mCancelled || this.mMetricsFinalized) {
            return;
        }
        for (SurfaceControl.JankData jankStat : jankData) {
            if (isInRange(jankStat.frameVsyncId)) {
                JankInfo info = findJankInfo(jankStat.frameVsyncId);
                if (info != null) {
                    info.surfaceControlCallbackFired = true;
                    info.jankType = jankStat.jankType;
                } else {
                    this.mJankInfos.put((int) jankStat.frameVsyncId, JankInfo.createFromSurfaceControlCallback(jankStat.frameVsyncId, jankStat.jankType));
                }
            }
        }
        processJankInfos();
    }

    public void postCallback(Runnable callback) {
        getHandler().post(callback);
    }

    private JankInfo findJankInfo(long frameVsyncId) {
        return this.mJankInfos.get((int) frameVsyncId);
    }

    private boolean isInRange(long vsyncId) {
        return vsyncId >= this.mBeginVsyncId;
    }

    @Override // android.graphics.HardwareRendererObserver.OnFrameMetricsAvailableListener
    public void onFrameMetricsAvailable(int dropCountSinceLastInvocation) {
        postCallback(new Runnable() { // from class: com.android.internal.jank.FrameTracker$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FrameTracker.this.lambda$onFrameMetricsAvailable$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFrameMetricsAvailable$1() {
        if (this.mCancelled || this.mMetricsFinalized) {
            return;
        }
        long totalDurationNanos = this.mMetricsWrapper.getMetric(8);
        boolean isFirstFrame = this.mMetricsWrapper.getMetric(9) == 1;
        long frameVsyncId = this.mMetricsWrapper.getTiming()[1];
        if (!isInRange(frameVsyncId)) {
            return;
        }
        JankInfo info = findJankInfo(frameVsyncId);
        if (info != null) {
            info.hwuiCallbackFired = true;
            info.totalDurationNanos = totalDurationNanos;
            info.isFirstFrame = isFirstFrame;
        } else {
            this.mJankInfos.put((int) frameVsyncId, JankInfo.createFromHwuiCallback(frameVsyncId, totalDurationNanos, isFirstFrame));
        }
        processJankInfos();
    }

    private boolean hasReceivedCallbacksAfterEnd() {
        JankInfo last;
        if (this.mEndVsyncId == -1) {
            return false;
        }
        if (this.mJankInfos.size() == 0) {
            last = null;
        } else {
            SparseArray<JankInfo> sparseArray = this.mJankInfos;
            last = sparseArray.valueAt(sparseArray.size() - 1);
        }
        if (last != null && last.frameVsyncId >= this.mEndVsyncId) {
            for (int i = this.mJankInfos.size() - 1; i >= 0; i--) {
                JankInfo info = this.mJankInfos.valueAt(i);
                if (info.frameVsyncId >= this.mEndVsyncId && callbacksReceived(info)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void processJankInfos() {
        if (this.mMetricsFinalized || !hasReceivedCallbacksAfterEnd()) {
            return;
        }
        finish();
    }

    private boolean callbacksReceived(JankInfo info) {
        if (this.mSurfaceOnly) {
            return info.surfaceControlCallbackFired;
        }
        return info.hwuiCallbackFired && info.surfaceControlCallbackFired;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finish() {
        int totalFramesCount;
        int totalFramesCount2;
        if (this.mMetricsFinalized || this.mCancelled) {
            return;
        }
        boolean z = true;
        this.mMetricsFinalized = true;
        getHandler().removeCallbacks(this.mWaitForFinishTimedOut);
        this.mWaitForFinishTimedOut = null;
        markEvent("FT#finish#" + this.mJankInfos.size());
        removeObservers();
        int totalFramesCount3 = 0;
        int maxSuccessiveMissedFramesCount = 0;
        int successiveMissedFramesCount = 0;
        int i = 0;
        long maxFrameTimeNanos = 0;
        int missedFramesCount = 0;
        int missedAppFramesCount = 0;
        int missedSfFramesCount = 0;
        while (true) {
            if (i >= this.mJankInfos.size()) {
                totalFramesCount = totalFramesCount3;
                break;
            }
            JankInfo info = this.mJankInfos.valueAt(i);
            boolean isFirstDrawn = (this.mSurfaceOnly || !info.isFirstFrame) ? false : z;
            if (!isFirstDrawn) {
                totalFramesCount = totalFramesCount3;
                if (info.frameVsyncId > this.mEndVsyncId) {
                    break;
                }
                if (!info.surfaceControlCallbackFired) {
                    totalFramesCount2 = totalFramesCount;
                } else {
                    int totalFramesCount4 = totalFramesCount + 1;
                    boolean missedFrame = false;
                    if ((info.jankType & 8) != 0) {
                        totalFramesCount2 = totalFramesCount4;
                        Log.m104w(TAG, "Missed App frame:" + info + ", CUJ=" + this.mSession.getName());
                        missedAppFramesCount++;
                        missedFrame = true;
                    } else {
                        totalFramesCount2 = totalFramesCount4;
                    }
                    int totalFramesCount5 = info.jankType;
                    if ((totalFramesCount5 & 1) != 0 || (info.jankType & 2) != 0 || (info.jankType & 4) != 0 || (info.jankType & 32) != 0 || (info.jankType & 16) != 0) {
                        Log.m104w(TAG, "Missed SF frame:" + info + ", CUJ=" + this.mSession.getName());
                        missedSfFramesCount++;
                        missedFrame = true;
                    }
                    if (missedFrame) {
                        missedFramesCount++;
                        successiveMissedFramesCount++;
                    } else {
                        maxSuccessiveMissedFramesCount = Math.max(maxSuccessiveMissedFramesCount, successiveMissedFramesCount);
                        successiveMissedFramesCount = 0;
                    }
                    if (!this.mSurfaceOnly && !info.hwuiCallbackFired) {
                        Log.m104w(TAG, "Missing HWUI jank callback for vsyncId: " + info.frameVsyncId + ", CUJ=" + this.mSession.getName());
                    }
                }
                if (this.mSurfaceOnly || !info.hwuiCallbackFired) {
                    totalFramesCount3 = totalFramesCount2;
                } else {
                    long maxFrameTimeNanos2 = Math.max(info.totalDurationNanos, maxFrameTimeNanos);
                    if (!info.surfaceControlCallbackFired) {
                        Log.m104w(TAG, "Missing SF jank callback for vsyncId: " + info.frameVsyncId + ", CUJ=" + this.mSession.getName());
                    }
                    maxFrameTimeNanos = maxFrameTimeNanos2;
                    totalFramesCount3 = totalFramesCount2;
                }
            }
            i++;
            z = true;
        }
        int maxSuccessiveMissedFramesCount2 = Math.max(maxSuccessiveMissedFramesCount, successiveMissedFramesCount);
        Trace.traceCounter(4096L, this.mSession.getName() + "#missedFrames", missedFramesCount);
        Trace.traceCounter(4096L, this.mSession.getName() + "#missedAppFrames", missedAppFramesCount);
        Trace.traceCounter(4096L, this.mSession.getName() + "#missedSfFrames", missedSfFramesCount);
        int totalFramesCount6 = totalFramesCount;
        Trace.traceCounter(4096L, this.mSession.getName() + "#totalFrames", totalFramesCount6);
        Trace.traceCounter(4096L, this.mSession.getName() + "#maxFrameTimeMillis", (int) (maxFrameTimeNanos / 1000000));
        Trace.traceCounter(4096L, this.mSession.getName() + "#maxSuccessiveMissedFrames", maxSuccessiveMissedFramesCount2);
        if (shouldTriggerPerfetto(missedFramesCount, (int) maxFrameTimeNanos)) {
            triggerPerfetto();
        }
        if (this.mSession.logToStatsd()) {
            this.mStatsLog.write(305, this.mDisplayId, this.mSession.getStatsdInteractionType(), totalFramesCount6, missedFramesCount, maxFrameTimeNanos, missedSfFramesCount, missedAppFramesCount, maxSuccessiveMissedFramesCount2);
        }
    }

    private boolean shouldTriggerPerfetto(int missedFramesCount, int maxFrameTimeNanos) {
        int i;
        int i2 = this.mTraceThresholdMissedFrames;
        boolean overMissedFramesThreshold = i2 != -1 && missedFramesCount >= i2;
        boolean overFrameTimeThreshold = (this.mSurfaceOnly || (i = this.mTraceThresholdFrameTimeMillis) == -1 || maxFrameTimeNanos < i * 1000000) ? false : true;
        return overMissedFramesThreshold || overFrameTimeThreshold;
    }

    public void removeObservers() {
        this.mSurfaceControlWrapper.removeJankStatsListener(this);
        if (!this.mSurfaceOnly) {
            this.mRendererWrapper.removeObserver(this.mObserver);
            ViewRootImpl.SurfaceChangedCallback surfaceChangedCallback = this.mSurfaceChangedCallback;
            if (surfaceChangedCallback != null) {
                this.mViewRoot.removeSurfaceChangedCallback(surfaceChangedCallback);
            }
        }
    }

    public void triggerPerfetto() {
        this.mMonitor.trigger(this.mSession);
    }

    /* loaded from: classes4.dex */
    public static class FrameMetricsWrapper {
        private final FrameMetrics mFrameMetrics = new FrameMetrics();

        public long[] getTiming() {
            return this.mFrameMetrics.mTimingData;
        }

        public long getMetric(int index) {
            return this.mFrameMetrics.getMetric(index);
        }
    }

    /* loaded from: classes4.dex */
    public static class ThreadedRendererWrapper {
        private final ThreadedRenderer mRenderer;

        public ThreadedRendererWrapper(ThreadedRenderer renderer) {
            this.mRenderer = renderer;
        }

        public void addObserver(HardwareRendererObserver observer) {
            this.mRenderer.addObserver(observer);
        }

        public void removeObserver(HardwareRendererObserver observer) {
            this.mRenderer.removeObserver(observer);
        }
    }

    /* loaded from: classes4.dex */
    public static class ViewRootWrapper {
        private final ViewRootImpl mViewRoot;

        public ViewRootWrapper(ViewRootImpl viewRoot) {
            this.mViewRoot = viewRoot;
        }

        public void addSurfaceChangedCallback(ViewRootImpl.SurfaceChangedCallback callback) {
            this.mViewRoot.addSurfaceChangedCallback(callback);
        }

        public void removeSurfaceChangedCallback(ViewRootImpl.SurfaceChangedCallback callback) {
            this.mViewRoot.removeSurfaceChangedCallback(callback);
        }

        public SurfaceControl getSurfaceControl() {
            return this.mViewRoot.getSurfaceControl();
        }
    }

    /* loaded from: classes4.dex */
    public static class SurfaceControlWrapper {
        public void addJankStatsListener(SurfaceControl.OnJankDataListener listener, SurfaceControl surfaceControl) {
            SurfaceControl.addJankDataListener(listener, surfaceControl);
        }

        public void removeJankStatsListener(SurfaceControl.OnJankDataListener listener) {
            SurfaceControl.removeJankDataListener(listener);
        }
    }

    /* loaded from: classes4.dex */
    public static class ChoreographerWrapper {
        private final Choreographer mChoreographer;

        public ChoreographerWrapper(Choreographer choreographer) {
            this.mChoreographer = choreographer;
        }

        public long getVsyncId() {
            return this.mChoreographer.getVsyncId();
        }
    }

    /* loaded from: classes4.dex */
    public static class StatsLogWrapper {
        private final DisplayResolutionTracker mDisplayResolutionTracker;

        public StatsLogWrapper(DisplayResolutionTracker displayResolutionTracker) {
            this.mDisplayResolutionTracker = displayResolutionTracker;
        }

        public void write(int code, int displayId, int arg1, long arg2, long arg3, long arg4, long arg5, long arg6, long arg7) {
            FrameworkStatsLog.write(code, arg1, arg2, arg3, arg4, arg5, arg6, arg7, this.mDisplayResolutionTracker.getResolution(displayId));
        }
    }
}
