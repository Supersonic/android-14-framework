package android.view;

import android.p008os.Looper;
import android.p008os.MessageQueue;
import android.util.Log;
import dalvik.annotation.optimization.FastNative;
import java.lang.ref.WeakReference;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes4.dex */
public abstract class DisplayEventReceiver {
    public static final int EVENT_REGISTRATION_FRAME_RATE_OVERRIDE_FLAG = 2;
    public static final int EVENT_REGISTRATION_MODE_CHANGED_FLAG = 1;
    private static final String TAG = "DisplayEventReceiver";
    public static final int VSYNC_SOURCE_APP = 0;
    public static final int VSYNC_SOURCE_SURFACE_FLINGER = 1;
    private static final NativeAllocationRegistry sNativeAllocationRegistry = NativeAllocationRegistry.createMalloced(DisplayEventReceiver.class.getClassLoader(), nativeGetDisplayEventReceiverFinalizer());
    private Runnable mFreeNativeResources;
    private MessageQueue mMessageQueue;
    private long mReceiverPtr;
    private final VsyncEventData mVsyncEventData;

    private static native long nativeGetDisplayEventReceiverFinalizer();

    private static native VsyncEventData nativeGetLatestVsyncEventData(long j);

    private static native long nativeInit(WeakReference<DisplayEventReceiver> weakReference, WeakReference<VsyncEventData> weakReference2, MessageQueue messageQueue, int i, int i2, long j);

    @FastNative
    private static native void nativeScheduleVsync(long j);

    public DisplayEventReceiver(Looper looper) {
        this(looper, 0, 0, 0L);
    }

    public DisplayEventReceiver(Looper looper, int vsyncSource, int eventRegistration) {
        this(looper, vsyncSource, eventRegistration, 0L);
    }

    public DisplayEventReceiver(Looper looper, int vsyncSource, int eventRegistration, long layerHandle) {
        VsyncEventData vsyncEventData = new VsyncEventData();
        this.mVsyncEventData = vsyncEventData;
        if (looper == null) {
            throw new IllegalArgumentException("looper must not be null");
        }
        this.mMessageQueue = looper.getQueue();
        long nativeInit = nativeInit(new WeakReference(this), new WeakReference(vsyncEventData), this.mMessageQueue, vsyncSource, eventRegistration, layerHandle);
        this.mReceiverPtr = nativeInit;
        this.mFreeNativeResources = sNativeAllocationRegistry.registerNativeAllocation(this, nativeInit);
    }

    public void dispose() {
        if (this.mReceiverPtr != 0) {
            this.mFreeNativeResources.run();
            this.mReceiverPtr = 0L;
        }
        this.mMessageQueue = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class VsyncEventData {
        static final int FRAME_TIMELINES_LENGTH = 7;
        public long frameInterval;
        public final FrameTimeline[] frameTimelines;
        public int preferredFrameTimelineIndex;

        /* loaded from: classes4.dex */
        public static class FrameTimeline {
            public long deadline;
            public long expectedPresentationTime;
            public long vsyncId;

            FrameTimeline() {
                this.vsyncId = -1L;
                this.expectedPresentationTime = Long.MAX_VALUE;
                this.deadline = Long.MAX_VALUE;
            }

            FrameTimeline(long vsyncId, long expectedPresentationTime, long deadline) {
                this.vsyncId = -1L;
                this.expectedPresentationTime = Long.MAX_VALUE;
                this.deadline = Long.MAX_VALUE;
                this.vsyncId = vsyncId;
                this.expectedPresentationTime = expectedPresentationTime;
                this.deadline = deadline;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public VsyncEventData() {
            this.frameInterval = -1L;
            this.preferredFrameTimelineIndex = 0;
            this.frameTimelines = new FrameTimeline[7];
            int i = 0;
            while (true) {
                FrameTimeline[] frameTimelineArr = this.frameTimelines;
                if (i < frameTimelineArr.length) {
                    frameTimelineArr[i] = new FrameTimeline();
                    i++;
                } else {
                    return;
                }
            }
        }

        VsyncEventData(FrameTimeline[] frameTimelines, int preferredFrameTimelineIndex, long frameInterval) {
            this.frameInterval = -1L;
            this.preferredFrameTimelineIndex = 0;
            this.frameTimelines = frameTimelines;
            this.preferredFrameTimelineIndex = preferredFrameTimelineIndex;
            this.frameInterval = frameInterval;
        }

        public FrameTimeline preferredFrameTimeline() {
            return this.frameTimelines[this.preferredFrameTimelineIndex];
        }
    }

    public void onVsync(long timestampNanos, long physicalDisplayId, int frame, VsyncEventData vsyncEventData) {
    }

    public void onHotplug(long timestampNanos, long physicalDisplayId, boolean connected) {
    }

    public void onModeChanged(long timestampNanos, long physicalDisplayId, int modeId, long renderPeriod) {
    }

    /* loaded from: classes4.dex */
    public static class FrameRateOverride {
        public final float frameRateHz;
        public final int uid;

        public FrameRateOverride(int uid, float frameRateHz) {
            this.uid = uid;
            this.frameRateHz = frameRateHz;
        }

        public String toString() {
            return "{uid=" + this.uid + " frameRateHz=" + this.frameRateHz + "}";
        }
    }

    public void onFrameRateOverridesChanged(long timestampNanos, long physicalDisplayId, FrameRateOverride[] overrides) {
    }

    public void scheduleVsync() {
        long j = this.mReceiverPtr;
        if (j == 0) {
            Log.m104w(TAG, "Attempted to schedule a vertical sync pulse but the display event receiver has already been disposed.");
        } else {
            nativeScheduleVsync(j);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VsyncEventData getLatestVsyncEventData() {
        return nativeGetLatestVsyncEventData(this.mReceiverPtr);
    }

    private void dispatchVsync(long timestampNanos, long physicalDisplayId, int frame) {
        onVsync(timestampNanos, physicalDisplayId, frame, this.mVsyncEventData);
    }

    private void dispatchHotplug(long timestampNanos, long physicalDisplayId, boolean connected) {
        onHotplug(timestampNanos, physicalDisplayId, connected);
    }

    private void dispatchModeChanged(long timestampNanos, long physicalDisplayId, int modeId, long renderPeriod) {
        onModeChanged(timestampNanos, physicalDisplayId, modeId, renderPeriod);
    }

    private void dispatchFrameRateOverrides(long timestampNanos, long physicalDisplayId, FrameRateOverride[] overrides) {
        onFrameRateOverridesChanged(timestampNanos, physicalDisplayId, overrides);
    }
}
