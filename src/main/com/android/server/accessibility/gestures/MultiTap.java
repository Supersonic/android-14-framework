package com.android.server.accessibility.gestures;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.server.accessibility.gestures.GestureMatcher;
/* loaded from: classes.dex */
public class MultiTap extends GestureMatcher {
    public float mBaseX;
    public float mBaseY;
    public int mCurrentTaps;
    public int mDoubleTapSlop;
    public int mDoubleTapTimeout;
    public int mTapTimeout;
    public final int mTargetTaps;
    public int mTouchSlop;

    public MultiTap(Context context, int i, int i2, GestureMatcher.StateChangeListener stateChangeListener) {
        super(i2, new Handler(context.getMainLooper()), stateChangeListener);
        this.mTargetTaps = i;
        this.mDoubleTapSlop = ViewConfiguration.get(context).getScaledDoubleTapSlop();
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mTapTimeout = ViewConfiguration.getTapTimeout();
        this.mDoubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();
        clear();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void clear() {
        this.mCurrentTaps = 0;
        this.mBaseX = Float.NaN;
        this.mBaseY = Float.NaN;
        super.clear();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelAfterTapTimeout(motionEvent, motionEvent2, i);
        if (Float.isNaN(this.mBaseX) && Float.isNaN(this.mBaseY)) {
            this.mBaseX = motionEvent.getX();
            this.mBaseY = motionEvent.getY();
        }
        if (!isInsideSlop(motionEvent2, this.mDoubleTapSlop)) {
            cancelGesture(motionEvent, motionEvent2, i);
        }
        this.mBaseX = motionEvent.getX();
        this.mBaseY = motionEvent.getY();
        if (this.mCurrentTaps + 1 == this.mTargetTaps) {
            startGesture(motionEvent, motionEvent2, i);
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelAfterDoubleTapTimeout(motionEvent, motionEvent2, i);
        if (!isInsideSlop(motionEvent2, this.mTouchSlop)) {
            cancelGesture(motionEvent, motionEvent2, i);
        }
        if (getState() == 1 || getState() == 0) {
            int i2 = this.mCurrentTaps + 1;
            this.mCurrentTaps = i2;
            if (i2 == this.mTargetTaps) {
                completeGesture(motionEvent, motionEvent2, i);
                return;
            } else {
                cancelAfterDoubleTapTimeout(motionEvent, motionEvent2, i);
                return;
            }
        }
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onMove(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (isInsideSlop(motionEvent2, this.mTouchSlop)) {
            return;
        }
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String getGestureName() {
        int i = this.mTargetTaps;
        if (i != 2) {
            if (i != 3) {
                return Integer.toString(this.mTargetTaps) + " Taps";
            }
            return "Triple Tap";
        }
        return "Double Tap";
    }

    public final boolean isInsideSlop(MotionEvent motionEvent, int i) {
        float x = this.mBaseX - motionEvent.getX();
        float y = this.mBaseY - motionEvent.getY();
        return (x == 0.0f && y == 0.0f) || Math.hypot((double) x, (double) y) <= ((double) i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String toString() {
        return super.toString() + ", Taps:" + this.mCurrentTaps + ", mBaseX: " + Float.toString(this.mBaseX) + ", mBaseY: " + Float.toString(this.mBaseY);
    }
}
