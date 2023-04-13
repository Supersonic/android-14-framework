package com.android.server.accessibility.gestures;

import android.os.Handler;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.internal.util.jobs.XmlUtils;
/* loaded from: classes.dex */
public abstract class GestureMatcher {
    public final int mGestureId;
    public final Handler mHandler;
    public StateChangeListener mListener;
    public int mState = 0;
    public final DelayedTransition mDelayedTransition = new DelayedTransition();

    /* loaded from: classes.dex */
    public interface StateChangeListener {
        void onStateChanged(int i, int i2, MotionEvent motionEvent, MotionEvent motionEvent2, int i3);
    }

    public abstract String getGestureName();

    public void onDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
    }

    public void onMove(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
    }

    public void onPointerDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
    }

    public void onPointerUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
    }

    public void onUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
    }

    public GestureMatcher(int i, Handler handler, StateChangeListener stateChangeListener) {
        this.mGestureId = i;
        this.mHandler = handler;
        this.mListener = stateChangeListener;
    }

    public void clear() {
        this.mState = 0;
        cancelPendingTransitions();
    }

    public final int getState() {
        return this.mState;
    }

    public final void setState(int i, MotionEvent motionEvent, MotionEvent motionEvent2, int i2) {
        this.mState = i;
        cancelPendingTransitions();
        StateChangeListener stateChangeListener = this.mListener;
        if (stateChangeListener != null) {
            stateChangeListener.onStateChanged(this.mGestureId, this.mState, motionEvent, motionEvent2, i2);
        }
    }

    public final void startGesture(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        setState(1, motionEvent, motionEvent2, i);
    }

    public final void cancelGesture(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        setState(3, motionEvent, motionEvent2, i);
    }

    public final void completeGesture(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        setState(2, motionEvent, motionEvent2, i);
    }

    public final void setListener(StateChangeListener stateChangeListener) {
        this.mListener = stateChangeListener;
    }

    public final int onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        int i2 = this.mState;
        if (i2 == 3 || i2 == 2) {
            return i2;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            onDown(motionEvent, motionEvent2, i);
        } else if (actionMasked == 1) {
            onUp(motionEvent, motionEvent2, i);
        } else if (actionMasked == 2) {
            onMove(motionEvent, motionEvent2, i);
        } else if (actionMasked == 5) {
            onPointerDown(motionEvent, motionEvent2, i);
        } else if (actionMasked == 6) {
            onPointerUp(motionEvent, motionEvent2, i);
        } else {
            setState(3, motionEvent, motionEvent2, i);
        }
        return this.mState;
    }

    public void cancelAfterTapTimeout(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelAfter(ViewConfiguration.getTapTimeout(), motionEvent, motionEvent2, i);
    }

    public final void cancelAfterDoubleTapTimeout(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelAfter(ViewConfiguration.getDoubleTapTimeout(), motionEvent, motionEvent2, i);
    }

    public final void cancelAfter(long j, MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        this.mDelayedTransition.cancel();
        this.mDelayedTransition.post(3, j, motionEvent, motionEvent2, i);
    }

    public final void cancelPendingTransitions() {
        this.mDelayedTransition.cancel();
    }

    public final void completeAfterLongPressTimeout(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        completeAfter(ViewConfiguration.getLongPressTimeout(), motionEvent, motionEvent2, i);
    }

    public final void completeAfter(long j, MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        this.mDelayedTransition.cancel();
        this.mDelayedTransition.post(2, j, motionEvent, motionEvent2, i);
    }

    public final void completeAfterDoubleTapTimeout(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        completeAfter(ViewConfiguration.getDoubleTapTimeout(), motionEvent, motionEvent2, i);
    }

    public static String getStateSymbolicName(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return "Unknown state: " + i;
                    }
                    return "STATE_GESTURE_CANCELED";
                }
                return "STATE_GESTURE_COMPLETED";
            }
            return "STATE_GESTURE_STARTED";
        }
        return "STATE_CLEAR";
    }

    public String toString() {
        return getGestureName() + XmlUtils.STRING_ARRAY_SEPARATOR + getStateSymbolicName(this.mState);
    }

    /* loaded from: classes.dex */
    public final class DelayedTransition implements Runnable {
        public MotionEvent mEvent;
        public int mPolicyFlags;
        public MotionEvent mRawEvent;
        public int mTargetState;

        public DelayedTransition() {
        }

        public void cancel() {
            if (TouchExplorer.DEBUG && isPending()) {
                Slog.d("GestureMatcher.DelayedTransition", GestureMatcher.this.getGestureName() + ": canceling delayed transition to " + GestureMatcher.getStateSymbolicName(this.mTargetState));
            }
            GestureMatcher.this.mHandler.removeCallbacks(this);
        }

        public void post(int i, long j, MotionEvent motionEvent, MotionEvent motionEvent2, int i2) {
            this.mTargetState = i;
            this.mEvent = motionEvent;
            this.mRawEvent = motionEvent2;
            this.mPolicyFlags = i2;
            GestureMatcher.this.mHandler.postDelayed(this, j);
            if (TouchExplorer.DEBUG) {
                Slog.d("GestureMatcher.DelayedTransition", GestureMatcher.this.getGestureName() + ": posting delayed transition to " + GestureMatcher.getStateSymbolicName(this.mTargetState));
            }
        }

        public boolean isPending() {
            return GestureMatcher.this.mHandler.hasCallbacks(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (TouchExplorer.DEBUG) {
                Slog.d("GestureMatcher.DelayedTransition", GestureMatcher.this.getGestureName() + ": executing delayed transition to " + GestureMatcher.getStateSymbolicName(this.mTargetState));
            }
            GestureMatcher.this.setState(this.mTargetState, this.mEvent, this.mRawEvent, this.mPolicyFlags);
        }
    }
}
