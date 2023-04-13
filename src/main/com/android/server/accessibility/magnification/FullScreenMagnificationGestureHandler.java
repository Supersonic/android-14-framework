package com.android.server.accessibility.magnification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import com.android.internal.accessibility.util.AccessibilityStatsLogUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.accessibility.AccessibilityTraceManager;
import com.android.server.accessibility.gestures.GestureUtils;
import com.android.server.accessibility.magnification.MagnificationGestureHandler;
import java.util.Arrays;
/* loaded from: classes.dex */
public class FullScreenMagnificationGestureHandler extends MagnificationGestureHandler {
    public static final boolean DEBUG_DETECTING;
    public static final boolean DEBUG_PANNING_SCALING;
    public static final boolean DEBUG_STATE_TRANSITIONS;
    @VisibleForTesting
    State mCurrentState;
    @VisibleForTesting
    final DelegatingState mDelegatingState;
    @VisibleForTesting
    final DetectingState mDetectingState;
    @VisibleForTesting
    final FullScreenMagnificationController mFullScreenMagnificationController;
    @VisibleForTesting
    final PanningScalingState mPanningScalingState;
    @VisibleForTesting
    State mPreviousState;
    public final WindowMagnificationPromptController mPromptController;
    public final ScreenStateReceiver mScreenStateReceiver;
    @VisibleForTesting
    final ViewportDraggingState mViewportDraggingState;

    @Override // com.android.server.accessibility.magnification.MagnificationGestureHandler
    public int getMode() {
        return 1;
    }

    static {
        boolean z = MagnificationGestureHandler.DEBUG_ALL;
        DEBUG_STATE_TRANSITIONS = z | false;
        DEBUG_DETECTING = z | false;
        DEBUG_PANNING_SCALING = z | false;
    }

    public FullScreenMagnificationGestureHandler(Context context, FullScreenMagnificationController fullScreenMagnificationController, AccessibilityTraceManager accessibilityTraceManager, MagnificationGestureHandler.Callback callback, boolean z, boolean z2, WindowMagnificationPromptController windowMagnificationPromptController, int i) {
        super(i, z, z2, accessibilityTraceManager, callback);
        if (MagnificationGestureHandler.DEBUG_ALL) {
            String str = this.mLogTag;
            Log.i(str, "FullScreenMagnificationGestureHandler(detectTripleTap = " + z + ", detectShortcutTrigger = " + z2 + ")");
        }
        this.mFullScreenMagnificationController = fullScreenMagnificationController;
        this.mPromptController = windowMagnificationPromptController;
        this.mDelegatingState = new DelegatingState();
        DetectingState detectingState = new DetectingState(context);
        this.mDetectingState = detectingState;
        this.mViewportDraggingState = new ViewportDraggingState();
        this.mPanningScalingState = new PanningScalingState(context);
        if (this.mDetectShortcutTrigger) {
            ScreenStateReceiver screenStateReceiver = new ScreenStateReceiver(context, this);
            this.mScreenStateReceiver = screenStateReceiver;
            screenStateReceiver.register();
        } else {
            this.mScreenStateReceiver = null;
        }
        transitionTo(detectingState);
    }

    @Override // com.android.server.accessibility.magnification.MagnificationGestureHandler
    public void onMotionEventInternal(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        handleEventWith(this.mCurrentState, motionEvent, motionEvent2, i);
    }

    public final void handleEventWith(State state, MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        this.mPanningScalingState.mScrollGestureDetector.onTouchEvent(motionEvent);
        this.mPanningScalingState.mScaleGestureDetector.onTouchEvent(motionEvent);
        try {
            state.onMotionEvent(motionEvent, motionEvent2, i);
        } catch (GestureException e) {
            Slog.e(this.mLogTag, "Error processing motion event", e);
            clearAndTransitionToStateDetecting();
        }
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void clearEvents(int i) {
        if (i == 4098) {
            clearAndTransitionToStateDetecting();
        }
        super.clearEvents(i);
    }

    @Override // com.android.server.accessibility.EventStreamTransformation
    public void onDestroy() {
        if (DEBUG_STATE_TRANSITIONS) {
            String str = this.mLogTag;
            Slog.i(str, "onDestroy(); delayed = " + MotionEventInfo.toString(this.mDetectingState.mDelayedEventQueue));
        }
        ScreenStateReceiver screenStateReceiver = this.mScreenStateReceiver;
        if (screenStateReceiver != null) {
            screenStateReceiver.unregister();
        }
        this.mPromptController.onDestroy();
        this.mFullScreenMagnificationController.resetIfNeeded(this.mDisplayId, 0);
        clearAndTransitionToStateDetecting();
    }

    @Override // com.android.server.accessibility.magnification.MagnificationGestureHandler
    public void handleShortcutTriggered() {
        if (this.mFullScreenMagnificationController.isActivated(this.mDisplayId)) {
            zoomOff();
            clearAndTransitionToStateDetecting();
        } else {
            this.mDetectingState.toggleShortcutTriggered();
        }
        if (this.mDetectingState.isShortcutTriggered()) {
            this.mPromptController.showNotificationIfNeeded();
            zoomToScale(1.0f, Float.NaN, Float.NaN);
        }
    }

    public void clearAndTransitionToStateDetecting() {
        DetectingState detectingState = this.mDetectingState;
        this.mCurrentState = detectingState;
        detectingState.clear();
        this.mViewportDraggingState.clear();
        this.mPanningScalingState.clear();
    }

    @VisibleForTesting
    public void transitionTo(State state) {
        if (DEBUG_STATE_TRANSITIONS) {
            String str = this.mLogTag;
            Slog.i(str, (State.nameOf(this.mCurrentState) + " -> " + State.nameOf(state) + " at " + Arrays.asList((StackTraceElement[]) Arrays.copyOfRange(new RuntimeException().getStackTrace(), 1, 5))).replace(getClass().getName(), ""));
        }
        this.mPreviousState = this.mCurrentState;
        PanningScalingState panningScalingState = this.mPanningScalingState;
        if (state == panningScalingState) {
            panningScalingState.prepareForState();
        }
        this.mCurrentState = state;
    }

    /* loaded from: classes.dex */
    public interface State {
        void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) throws GestureException;

        default String name() {
            return getClass().getSimpleName();
        }

        static String nameOf(State state) {
            return state != null ? state.name() : "null";
        }
    }

    /* loaded from: classes.dex */
    public final class PanningScalingState extends GestureDetector.SimpleOnGestureListener implements ScaleGestureDetector.OnScaleGestureListener, State {
        @VisibleForTesting
        static final float CHECK_DETECTING_PASS_PERSISTED_SCALE_THRESHOLD = 0.2f;
        @VisibleForTesting
        static final float PASSING_PERSISTED_SCALE_THRESHOLD = 0.01f;
        public final Context mContext;
        @VisibleForTesting
        boolean mDetectingPassPersistedScale;
        public float mInitialScaleFactor = -1.0f;
        public final ScaleGestureDetector mScaleGestureDetector;
        @VisibleForTesting
        boolean mScaling;
        public final float mScalingThreshold;
        public final GestureDetector mScrollGestureDetector;

        public PanningScalingState(Context context) {
            TypedValue typedValue = new TypedValue();
            context.getResources().getValue(17105110, typedValue, false);
            this.mContext = context;
            this.mScalingThreshold = typedValue.getFloat();
            ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, this, Handler.getMain());
            this.mScaleGestureDetector = scaleGestureDetector;
            scaleGestureDetector.setQuickScaleEnabled(false);
            this.mScrollGestureDetector = new GestureDetector(context, this, Handler.getMain());
        }

        @Override // com.android.server.accessibility.magnification.FullScreenMagnificationGestureHandler.State
        public void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 6 && motionEvent.getPointerCount() == 2) {
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
                State state = fullScreenMagnificationGestureHandler.mPreviousState;
                ViewportDraggingState viewportDraggingState = fullScreenMagnificationGestureHandler.mViewportDraggingState;
                if (state == viewportDraggingState) {
                    persistScaleAndTransitionTo(viewportDraggingState);
                    return;
                }
            }
            if (actionMasked == 1 || actionMasked == 3) {
                persistScaleAndTransitionTo(FullScreenMagnificationGestureHandler.this.mDetectingState);
            }
        }

        public void prepareForState() {
            checkShouldDetectPassPersistedScale();
        }

        public final void checkShouldDetectPassPersistedScale() {
            if (this.mDetectingPassPersistedScale) {
                return;
            }
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            float scale = fullScreenMagnificationGestureHandler.mFullScreenMagnificationController.getScale(fullScreenMagnificationGestureHandler.mDisplayId);
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler2 = FullScreenMagnificationGestureHandler.this;
            float persistedScale = fullScreenMagnificationGestureHandler2.mFullScreenMagnificationController.getPersistedScale(fullScreenMagnificationGestureHandler2.mDisplayId);
            this.mDetectingPassPersistedScale = Math.abs(scale - persistedScale) / persistedScale >= CHECK_DETECTING_PASS_PERSISTED_SCALE_THRESHOLD;
        }

        public void persistScaleAndTransitionTo(State state) {
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            fullScreenMagnificationGestureHandler.mFullScreenMagnificationController.persistScale(fullScreenMagnificationGestureHandler.mDisplayId);
            clear();
            FullScreenMagnificationGestureHandler.this.transitionTo(state);
        }

        /* JADX WARN: Removed duplicated region for block: B:13:0x003c  */
        @VisibleForTesting
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void setScaleAndClearIfNeeded(float f, float f2, float f3) {
            float f4;
            if (this.mDetectingPassPersistedScale) {
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
                float persistedScale = fullScreenMagnificationGestureHandler.mFullScreenMagnificationController.getPersistedScale(fullScreenMagnificationGestureHandler.mDisplayId);
                if (Math.abs(f - persistedScale) / persistedScale < PASSING_PERSISTED_SCALE_THRESHOLD) {
                    Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Vibrator.class);
                    if (vibrator != null) {
                        vibrator.vibrate(VibrationEffect.createPredefined(2));
                    }
                    clear();
                    f4 = persistedScale;
                    if (FullScreenMagnificationGestureHandler.DEBUG_PANNING_SCALING) {
                        String str = FullScreenMagnificationGestureHandler.this.mLogTag;
                        Slog.i(str, "Scaled content to: " + f4 + "x");
                    }
                    FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler2 = FullScreenMagnificationGestureHandler.this;
                    fullScreenMagnificationGestureHandler2.mFullScreenMagnificationController.setScale(fullScreenMagnificationGestureHandler2.mDisplayId, f4, f2, f3, false, 0);
                    checkShouldDetectPassPersistedScale();
                }
            }
            f4 = f;
            if (FullScreenMagnificationGestureHandler.DEBUG_PANNING_SCALING) {
            }
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler22 = FullScreenMagnificationGestureHandler.this;
            fullScreenMagnificationGestureHandler22.mFullScreenMagnificationController.setScale(fullScreenMagnificationGestureHandler22.mDisplayId, f4, f2, f3, false, 0);
            checkShouldDetectPassPersistedScale();
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            if (fullScreenMagnificationGestureHandler.mCurrentState != fullScreenMagnificationGestureHandler.mPanningScalingState) {
                return true;
            }
            if (FullScreenMagnificationGestureHandler.DEBUG_PANNING_SCALING) {
                String str = FullScreenMagnificationGestureHandler.this.mLogTag;
                Slog.i(str, "Panned content by scrollX: " + f + " scrollY: " + f2);
            }
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler2 = FullScreenMagnificationGestureHandler.this;
            fullScreenMagnificationGestureHandler2.mFullScreenMagnificationController.offsetMagnifiedRegion(fullScreenMagnificationGestureHandler2.mDisplayId, f, f2, 0);
            return true;
        }

        /* JADX WARN: Code restructure failed: missing block: B:22:0x004d, code lost:
            if (r2 < r0) goto L17;
         */
        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            if (!this.mScaling) {
                if (this.mInitialScaleFactor < 0.0f) {
                    this.mInitialScaleFactor = scaleGestureDetector.getScaleFactor();
                    return false;
                }
                boolean z = Math.abs(scaleGestureDetector.getScaleFactor() - this.mInitialScaleFactor) > this.mScalingThreshold;
                this.mScaling = z;
                return z;
            }
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            float scale = fullScreenMagnificationGestureHandler.mFullScreenMagnificationController.getScale(fullScreenMagnificationGestureHandler.mDisplayId);
            float scaleFactor = scaleGestureDetector.getScaleFactor() * scale;
            float f = 8.0f;
            if (scaleFactor <= 8.0f || scaleFactor <= scale) {
                f = 1.0f;
                if (scaleFactor < 1.0f) {
                }
                setScaleAndClearIfNeeded(scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                return true;
            }
            scaleFactor = f;
            setScaleAndClearIfNeeded(scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            return true;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            return fullScreenMagnificationGestureHandler.mCurrentState == fullScreenMagnificationGestureHandler.mPanningScalingState;
        }

        @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            clear();
        }

        public void clear() {
            this.mInitialScaleFactor = -1.0f;
            this.mScaling = false;
            this.mDetectingPassPersistedScale = false;
        }

        public String toString() {
            return "PanningScalingState{mInitialScaleFactor=" + this.mInitialScaleFactor + ", mScaling=" + this.mScaling + '}';
        }
    }

    /* loaded from: classes.dex */
    public final class ViewportDraggingState implements State {
        public boolean mLastMoveOutsideMagnifiedRegion;
        @VisibleForTesting
        float mScaleToRecoverAfterDraggingEnd = Float.NaN;

        public ViewportDraggingState() {
        }

        @Override // com.android.server.accessibility.magnification.FullScreenMagnificationGestureHandler.State
        public void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) throws GestureException {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked == 2) {
                        if (motionEvent.getPointerCount() != 1) {
                            throw new GestureException("Should have one pointer down.");
                        }
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
                        if (fullScreenMagnificationGestureHandler.mFullScreenMagnificationController.magnificationRegionContains(fullScreenMagnificationGestureHandler.mDisplayId, x, y)) {
                            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler2 = FullScreenMagnificationGestureHandler.this;
                            fullScreenMagnificationGestureHandler2.mFullScreenMagnificationController.setCenter(fullScreenMagnificationGestureHandler2.mDisplayId, x, y, this.mLastMoveOutsideMagnifiedRegion, 0);
                            this.mLastMoveOutsideMagnifiedRegion = false;
                            return;
                        }
                        this.mLastMoveOutsideMagnifiedRegion = true;
                        return;
                    } else if (actionMasked != 3) {
                        if (actionMasked == 5) {
                            clearAndTransitToPanningScalingState();
                            return;
                        } else if (actionMasked != 6) {
                            return;
                        }
                    }
                }
                float f = this.mScaleToRecoverAfterDraggingEnd;
                if (f >= 1.0f) {
                    FullScreenMagnificationGestureHandler.this.zoomToScale(f, motionEvent.getX(), motionEvent.getY());
                } else {
                    FullScreenMagnificationGestureHandler.this.zoomOff();
                }
                clear();
                this.mScaleToRecoverAfterDraggingEnd = Float.NaN;
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler3 = FullScreenMagnificationGestureHandler.this;
                fullScreenMagnificationGestureHandler3.transitionTo(fullScreenMagnificationGestureHandler3.mDetectingState);
                return;
            }
            throw new GestureException("Unexpected event type: " + MotionEvent.actionToString(actionMasked));
        }

        public final boolean isAlwaysOnMagnificationEnabled() {
            return FullScreenMagnificationGestureHandler.this.mFullScreenMagnificationController.isAlwaysOnMagnificationEnabled();
        }

        public void prepareForZoomInTemporary(boolean z) {
            boolean z2;
            float f;
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            if (fullScreenMagnificationGestureHandler.mFullScreenMagnificationController.isActivated(fullScreenMagnificationGestureHandler.mDisplayId)) {
                z2 = z ? isAlwaysOnMagnificationEnabled() : true;
            } else {
                z2 = false;
            }
            if (z2) {
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler2 = FullScreenMagnificationGestureHandler.this;
                f = fullScreenMagnificationGestureHandler2.mFullScreenMagnificationController.getScale(fullScreenMagnificationGestureHandler2.mDisplayId);
            } else {
                f = Float.NaN;
            }
            this.mScaleToRecoverAfterDraggingEnd = f;
        }

        public final void clearAndTransitToPanningScalingState() {
            float f = this.mScaleToRecoverAfterDraggingEnd;
            clear();
            this.mScaleToRecoverAfterDraggingEnd = f;
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            fullScreenMagnificationGestureHandler.transitionTo(fullScreenMagnificationGestureHandler.mPanningScalingState);
        }

        public void clear() {
            this.mLastMoveOutsideMagnifiedRegion = false;
            this.mScaleToRecoverAfterDraggingEnd = Float.NaN;
        }

        public String toString() {
            return "ViewportDraggingState{mScaleToRecoverAfterDraggingEnd=" + this.mScaleToRecoverAfterDraggingEnd + ", mLastMoveOutsideMagnifiedRegion=" + this.mLastMoveOutsideMagnifiedRegion + '}';
        }
    }

    /* loaded from: classes.dex */
    public final class DelegatingState implements State {
        public long mLastDelegatedDownEventTime;

        public DelegatingState() {
        }

        @Override // com.android.server.accessibility.magnification.FullScreenMagnificationGestureHandler.State
        public void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
                fullScreenMagnificationGestureHandler.transitionTo(fullScreenMagnificationGestureHandler.mDelegatingState);
                this.mLastDelegatedDownEventTime = motionEvent.getDownTime();
            } else if (actionMasked == 1 || actionMasked == 3) {
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler2 = FullScreenMagnificationGestureHandler.this;
                fullScreenMagnificationGestureHandler2.transitionTo(fullScreenMagnificationGestureHandler2.mDetectingState);
            }
            if (FullScreenMagnificationGestureHandler.this.getNext() != null) {
                motionEvent.setDownTime(this.mLastDelegatedDownEventTime);
                FullScreenMagnificationGestureHandler.this.dispatchTransformedEvent(motionEvent, motionEvent2, i);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class DetectingState implements State, Handler.Callback {
        public MotionEventInfo mDelayedEventQueue;
        public long mLastDetectingDownEventTime;
        public MotionEvent mLastDown;
        public MotionEvent mLastUp;
        public final int mMultiTapMaxDelay;
        public final int mMultiTapMaxDistance;
        public MotionEvent mPreLastDown;
        public MotionEvent mPreLastUp;
        @VisibleForTesting
        boolean mShortcutTriggered;
        public final int mSwipeMinDistance;
        public PointF mSecondPointerDownLocation = new PointF(Float.NaN, Float.NaN);
        @VisibleForTesting
        Handler mHandler = new Handler(Looper.getMainLooper(), this);
        public final int mLongTapMinDelay = ViewConfiguration.getLongPressTimeout();

        public DetectingState(Context context) {
            this.mMultiTapMaxDelay = ViewConfiguration.getDoubleTapTimeout() + context.getResources().getInteger(17694946);
            this.mSwipeMinDistance = ViewConfiguration.get(context).getScaledTouchSlop();
            this.mMultiTapMaxDistance = ViewConfiguration.get(context).getScaledDoubleTapSlop();
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                MotionEvent motionEvent = (MotionEvent) message.obj;
                transitionToViewportDraggingStateAndClear(motionEvent);
                motionEvent.recycle();
            } else if (i == 2) {
                transitionToDelegatingStateAndClear();
            } else if (i == 3) {
                transitToPanningScalingStateAndClear();
            } else {
                throw new IllegalArgumentException("Unknown message type: " + i);
            }
            return true;
        }

        @Override // com.android.server.accessibility.magnification.FullScreenMagnificationGestureHandler.State
        public void onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
            cacheDelayedMotionEvent(motionEvent, motionEvent2, i);
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mLastDetectingDownEventTime = motionEvent.getDownTime();
                this.mHandler.removeMessages(2);
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
                if (!fullScreenMagnificationGestureHandler.mFullScreenMagnificationController.magnificationRegionContains(fullScreenMagnificationGestureHandler.mDisplayId, motionEvent.getX(), motionEvent.getY())) {
                    transitionToDelegatingStateAndClear();
                } else if (isMultiTapTriggered(2)) {
                    afterLongTapTimeoutTransitionToDraggingState(motionEvent);
                } else if (isTapOutOfDistanceSlop()) {
                    transitionToDelegatingStateAndClear();
                } else if (FullScreenMagnificationGestureHandler.this.mDetectTripleTap || isActivated()) {
                    afterMultiTapTimeoutTransitionToDelegatingState();
                } else {
                    transitionToDelegatingStateAndClear();
                }
            } else if (actionMasked == 1) {
                this.mHandler.removeMessages(1);
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler2 = FullScreenMagnificationGestureHandler.this;
                if (!fullScreenMagnificationGestureHandler2.mFullScreenMagnificationController.magnificationRegionContains(fullScreenMagnificationGestureHandler2.mDisplayId, motionEvent.getX(), motionEvent.getY())) {
                    transitionToDelegatingStateAndClear();
                } else if (isMultiTapTriggered(3)) {
                    onTripleTap(motionEvent);
                } else if (isFingerDown()) {
                    if (timeBetween(this.mLastDown, this.mLastUp) >= this.mLongTapMinDelay || GestureUtils.distance(this.mLastDown, this.mLastUp) >= this.mSwipeMinDistance) {
                        transitionToDelegatingStateAndClear();
                    }
                }
            } else if (actionMasked != 2) {
                if (actionMasked != 5) {
                    if (actionMasked != 6) {
                        return;
                    }
                    transitionToDelegatingStateAndClear();
                } else if (isActivated() && motionEvent.getPointerCount() == 2) {
                    storeSecondPointerDownLocation(motionEvent);
                    this.mHandler.sendEmptyMessageDelayed(3, ViewConfiguration.getTapTimeout());
                } else {
                    transitionToDelegatingStateAndClear();
                }
            } else if (isFingerDown() && GestureUtils.distance(this.mLastDown, motionEvent) > this.mSwipeMinDistance) {
                if (isMultiTapTriggered(2) && motionEvent.getPointerCount() == 1) {
                    transitionToViewportDraggingStateAndClear(motionEvent);
                } else if (isActivated() && motionEvent.getPointerCount() == 2) {
                    transitToPanningScalingStateAndClear();
                } else {
                    transitionToDelegatingStateAndClear();
                }
            } else if (isActivated() && secondPointerDownValid() && GestureUtils.distanceClosestPointerToPoint(this.mSecondPointerDownLocation, motionEvent) > this.mSwipeMinDistance) {
                transitToPanningScalingStateAndClear();
            }
        }

        public final void storeSecondPointerDownLocation(MotionEvent motionEvent) {
            int actionIndex = motionEvent.getActionIndex();
            this.mSecondPointerDownLocation.set(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
        }

        public final boolean secondPointerDownValid() {
            return (Float.isNaN(this.mSecondPointerDownLocation.x) && Float.isNaN(this.mSecondPointerDownLocation.y)) ? false : true;
        }

        public final void transitToPanningScalingStateAndClear() {
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            fullScreenMagnificationGestureHandler.transitionTo(fullScreenMagnificationGestureHandler.mPanningScalingState);
            clear();
        }

        public boolean isMultiTapTriggered(int i) {
            boolean z = true;
            if (this.mShortcutTriggered) {
                return tapCount() + 2 >= i;
            }
            if (!FullScreenMagnificationGestureHandler.this.mDetectTripleTap || tapCount() < i || !isMultiTap(this.mPreLastDown, this.mLastDown) || !isMultiTap(this.mPreLastUp, this.mLastUp)) {
                z = false;
            }
            if (z && i > 2) {
                AccessibilityStatsLogUtils.logMagnificationTripleTap(isActivated());
            }
            return z;
        }

        public final boolean isMultiTap(MotionEvent motionEvent, MotionEvent motionEvent2) {
            return GestureUtils.isMultiTap(motionEvent, motionEvent2, this.mMultiTapMaxDelay, this.mMultiTapMaxDistance);
        }

        public boolean isFingerDown() {
            return this.mLastDown != null;
        }

        public final long timeBetween(MotionEvent motionEvent, MotionEvent motionEvent2) {
            if (motionEvent == null && motionEvent2 == null) {
                return 0L;
            }
            return Math.abs(timeOf(motionEvent) - timeOf(motionEvent2));
        }

        public final long timeOf(MotionEvent motionEvent) {
            if (motionEvent != null) {
                return motionEvent.getEventTime();
            }
            return Long.MIN_VALUE;
        }

        public int tapCount() {
            return MotionEventInfo.countOf(this.mDelayedEventQueue, 1);
        }

        public void afterMultiTapTimeoutTransitionToDelegatingState() {
            this.mHandler.sendEmptyMessageDelayed(2, this.mMultiTapMaxDelay);
        }

        public void afterLongTapTimeoutTransitionToDraggingState(MotionEvent motionEvent) {
            Handler handler = this.mHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1, MotionEvent.obtain(motionEvent)), ViewConfiguration.getLongPressTimeout());
        }

        public void clear() {
            setShortcutTriggered(false);
            removePendingDelayedMessages();
            clearDelayedMotionEvents();
            this.mSecondPointerDownLocation.set(Float.NaN, Float.NaN);
        }

        public final void removePendingDelayedMessages() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
        }

        public final void cacheDelayedMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
            if (motionEvent.getActionMasked() == 0) {
                this.mPreLastDown = this.mLastDown;
                this.mLastDown = MotionEvent.obtain(motionEvent);
            } else if (motionEvent.getActionMasked() == 1) {
                this.mPreLastUp = this.mLastUp;
                this.mLastUp = MotionEvent.obtain(motionEvent);
            }
            MotionEventInfo obtain = MotionEventInfo.obtain(motionEvent, motionEvent2, i);
            MotionEventInfo motionEventInfo = this.mDelayedEventQueue;
            if (motionEventInfo == null) {
                this.mDelayedEventQueue = obtain;
                return;
            }
            while (motionEventInfo.mNext != null) {
                motionEventInfo = motionEventInfo.mNext;
            }
            motionEventInfo.mNext = obtain;
        }

        public final void sendDelayedMotionEvents() {
            if (this.mDelayedEventQueue == null) {
                return;
            }
            long min = Math.min(SystemClock.uptimeMillis() - this.mLastDetectingDownEventTime, this.mMultiTapMaxDelay);
            do {
                MotionEventInfo motionEventInfo = this.mDelayedEventQueue;
                this.mDelayedEventQueue = motionEventInfo.mNext;
                MotionEvent motionEvent = motionEventInfo.event;
                motionEvent.setDownTime(motionEvent.getDownTime() + min);
                FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
                fullScreenMagnificationGestureHandler.handleEventWith(fullScreenMagnificationGestureHandler.mDelegatingState, motionEventInfo.event, motionEventInfo.rawEvent, motionEventInfo.policyFlags);
                motionEventInfo.recycle();
            } while (this.mDelayedEventQueue != null);
        }

        public final void clearDelayedMotionEvents() {
            while (true) {
                MotionEventInfo motionEventInfo = this.mDelayedEventQueue;
                if (motionEventInfo != null) {
                    this.mDelayedEventQueue = motionEventInfo.mNext;
                    motionEventInfo.recycle();
                } else {
                    this.mPreLastDown = null;
                    this.mPreLastUp = null;
                    this.mLastDown = null;
                    this.mLastUp = null;
                    return;
                }
            }
        }

        public void transitionToDelegatingStateAndClear() {
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            fullScreenMagnificationGestureHandler.transitionTo(fullScreenMagnificationGestureHandler.mDelegatingState);
            sendDelayedMotionEvents();
            removePendingDelayedMessages();
            this.mSecondPointerDownLocation.set(Float.NaN, Float.NaN);
        }

        public final void onTripleTap(MotionEvent motionEvent) {
            if (FullScreenMagnificationGestureHandler.DEBUG_DETECTING) {
                String str = FullScreenMagnificationGestureHandler.this.mLogTag;
                Slog.i(str, "onTripleTap(); delayed: " + MotionEventInfo.toString(this.mDelayedEventQueue));
            }
            if (!isActivated() || this.mShortcutTriggered) {
                FullScreenMagnificationGestureHandler.this.mPromptController.showNotificationIfNeeded();
                FullScreenMagnificationGestureHandler.this.zoomOn(motionEvent.getX(), motionEvent.getY());
            } else {
                FullScreenMagnificationGestureHandler.this.zoomOff();
            }
            clear();
        }

        public final boolean isActivated() {
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            return fullScreenMagnificationGestureHandler.mFullScreenMagnificationController.isActivated(fullScreenMagnificationGestureHandler.mDisplayId);
        }

        public void transitionToViewportDraggingStateAndClear(MotionEvent motionEvent) {
            if (FullScreenMagnificationGestureHandler.DEBUG_DETECTING) {
                Slog.i(FullScreenMagnificationGestureHandler.this.mLogTag, "onTripleTapAndHold()");
            }
            boolean z = this.mShortcutTriggered;
            clear();
            AccessibilityStatsLogUtils.logMagnificationTripleTap(!isActivated());
            FullScreenMagnificationGestureHandler.this.mViewportDraggingState.prepareForZoomInTemporary(z);
            FullScreenMagnificationGestureHandler.this.zoomInTemporary(motionEvent.getX(), motionEvent.getY());
            FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler = FullScreenMagnificationGestureHandler.this;
            fullScreenMagnificationGestureHandler.transitionTo(fullScreenMagnificationGestureHandler.mViewportDraggingState);
        }

        public String toString() {
            return "DetectingState{tapCount()=" + tapCount() + ", mShortcutTriggered=" + this.mShortcutTriggered + ", mDelayedEventQueue=" + MotionEventInfo.toString(this.mDelayedEventQueue) + '}';
        }

        public void toggleShortcutTriggered() {
            setShortcutTriggered(!this.mShortcutTriggered);
        }

        public void setShortcutTriggered(boolean z) {
            if (this.mShortcutTriggered == z) {
                return;
            }
            if (FullScreenMagnificationGestureHandler.DEBUG_DETECTING) {
                String str = FullScreenMagnificationGestureHandler.this.mLogTag;
                Slog.i(str, "setShortcutTriggered(" + z + ")");
            }
            this.mShortcutTriggered = z;
        }

        public final boolean isShortcutTriggered() {
            return this.mShortcutTriggered;
        }

        public boolean isTapOutOfDistanceSlop() {
            MotionEvent motionEvent;
            MotionEvent motionEvent2;
            if (FullScreenMagnificationGestureHandler.this.mDetectTripleTap && (motionEvent = this.mPreLastDown) != null && (motionEvent2 = this.mLastDown) != null) {
                boolean z = GestureUtils.distance(motionEvent, motionEvent2) > ((double) this.mMultiTapMaxDistance);
                if (tapCount() > 0) {
                    return z;
                }
                if (z && !GestureUtils.isTimedOut(this.mPreLastDown, this.mLastDown, this.mMultiTapMaxDelay)) {
                    return true;
                }
            }
            return false;
        }
    }

    public final void zoomInTemporary(float f, float f2) {
        float scale = this.mFullScreenMagnificationController.getScale(this.mDisplayId);
        float constrain = MathUtils.constrain(this.mFullScreenMagnificationController.getPersistedScale(this.mDisplayId), 1.0f, 8.0f);
        if (this.mFullScreenMagnificationController.isActivated(this.mDisplayId)) {
            constrain = scale + 1.0f;
        }
        zoomToScale(constrain, f, f2);
    }

    public final void zoomOn(float f, float f2) {
        if (DEBUG_DETECTING) {
            String str = this.mLogTag;
            Slog.i(str, "zoomOn(" + f + ", " + f2 + ")");
        }
        zoomToScale(MathUtils.constrain(this.mFullScreenMagnificationController.getPersistedScale(this.mDisplayId), 1.0f, 8.0f), f, f2);
    }

    public final void zoomToScale(float f, float f2, float f3) {
        this.mFullScreenMagnificationController.setScaleAndCenter(this.mDisplayId, MathUtils.constrain(f, 1.0f, 8.0f), f2, f3, true, 0);
    }

    public final void zoomOff() {
        if (DEBUG_DETECTING) {
            Slog.i(this.mLogTag, "zoomOff()");
        }
        this.mFullScreenMagnificationController.reset(this.mDisplayId, true);
    }

    public static MotionEvent recycleAndNullify(MotionEvent motionEvent) {
        if (motionEvent != null) {
            motionEvent.recycle();
            return null;
        }
        return null;
    }

    public String toString() {
        return "MagnificationGesture{mDetectingState=" + this.mDetectingState + ", mDelegatingState=" + this.mDelegatingState + ", mMagnifiedInteractionState=" + this.mPanningScalingState + ", mViewportDraggingState=" + this.mViewportDraggingState + ", mDetectTripleTap=" + this.mDetectTripleTap + ", mDetectShortcutTrigger=" + this.mDetectShortcutTrigger + ", mCurrentState=" + State.nameOf(this.mCurrentState) + ", mPreviousState=" + State.nameOf(this.mPreviousState) + ", mMagnificationController=" + this.mFullScreenMagnificationController + ", mDisplayId=" + this.mDisplayId + '}';
    }

    /* loaded from: classes.dex */
    public static final class MotionEventInfo {
        public static final Object sLock = new Object();
        public static MotionEventInfo sPool;
        public static int sPoolSize;
        public MotionEvent event;
        public boolean mInPool;
        public MotionEventInfo mNext;
        public int policyFlags;
        public MotionEvent rawEvent;

        public static MotionEventInfo obtain(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
            MotionEventInfo obtainInternal;
            synchronized (sLock) {
                obtainInternal = obtainInternal();
                obtainInternal.initialize(motionEvent, motionEvent2, i);
            }
            return obtainInternal;
        }

        public static MotionEventInfo obtainInternal() {
            int i = sPoolSize;
            if (i > 0) {
                sPoolSize = i - 1;
                MotionEventInfo motionEventInfo = sPool;
                sPool = motionEventInfo.mNext;
                motionEventInfo.mNext = null;
                motionEventInfo.mInPool = false;
                return motionEventInfo;
            }
            return new MotionEventInfo();
        }

        public final void initialize(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
            this.event = MotionEvent.obtain(motionEvent);
            this.rawEvent = MotionEvent.obtain(motionEvent2);
            this.policyFlags = i;
        }

        public void recycle() {
            synchronized (sLock) {
                if (this.mInPool) {
                    throw new IllegalStateException("Already recycled.");
                }
                clear();
                int i = sPoolSize;
                if (i < 10) {
                    sPoolSize = i + 1;
                    this.mNext = sPool;
                    sPool = this;
                    this.mInPool = true;
                }
            }
        }

        public final void clear() {
            this.event = FullScreenMagnificationGestureHandler.recycleAndNullify(this.event);
            this.rawEvent = FullScreenMagnificationGestureHandler.recycleAndNullify(this.rawEvent);
            this.policyFlags = 0;
        }

        public static int countOf(MotionEventInfo motionEventInfo, int i) {
            if (motionEventInfo == null) {
                return 0;
            }
            return (motionEventInfo.event.getAction() == i ? 1 : 0) + countOf(motionEventInfo.mNext, i);
        }

        public static String toString(MotionEventInfo motionEventInfo) {
            if (motionEventInfo == null) {
                return "";
            }
            return MotionEvent.actionToString(motionEventInfo.event.getAction()).replace("ACTION_", "") + " " + toString(motionEventInfo.mNext);
        }
    }

    /* loaded from: classes.dex */
    public static class ScreenStateReceiver extends BroadcastReceiver {
        public final Context mContext;
        public final FullScreenMagnificationGestureHandler mGestureHandler;

        public ScreenStateReceiver(Context context, FullScreenMagnificationGestureHandler fullScreenMagnificationGestureHandler) {
            this.mContext = context;
            this.mGestureHandler = fullScreenMagnificationGestureHandler;
        }

        public void register() {
            this.mContext.registerReceiver(this, new IntentFilter("android.intent.action.SCREEN_OFF"));
        }

        public void unregister() {
            this.mContext.unregisterReceiver(this);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.mGestureHandler.mDetectingState.setShortcutTriggered(false);
        }
    }

    /* loaded from: classes.dex */
    public static class GestureException extends Exception {
        public GestureException(String str) {
            super(str);
        }
    }
}
