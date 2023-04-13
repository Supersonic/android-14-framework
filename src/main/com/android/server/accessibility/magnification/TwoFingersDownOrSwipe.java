package com.android.server.accessibility.magnification;

import android.content.Context;
import android.os.Handler;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.server.accessibility.gestures.GestureMatcher;
/* loaded from: classes.dex */
public final class TwoFingersDownOrSwipe extends GestureMatcher {
    public final int mDetectionDurationMillis;
    public final int mDoubleTapTimeout;
    public MotionEvent mFirstPointerDown;
    public MotionEvent mSecondPointerDown;
    public final int mSwipeMinDistance;

    public TwoFingersDownOrSwipe(Context context) {
        super(101, new Handler(context.getMainLooper()), null);
        this.mDetectionDurationMillis = MagnificationGestureMatcher.getMagnificationMultiTapTimeout(context);
        this.mDoubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();
        this.mSwipeMinDistance = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        this.mFirstPointerDown = MotionEvent.obtain(motionEvent);
        cancelAfter(this.mDetectionDurationMillis, motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (this.mFirstPointerDown == null) {
            cancelGesture(motionEvent, motionEvent2, i);
        }
        if (motionEvent.getPointerCount() == 2) {
            this.mSecondPointerDown = MotionEvent.obtain(motionEvent);
            completeAfter(this.mDoubleTapTimeout, motionEvent, motionEvent2, i);
            return;
        }
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onMove(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        MotionEvent motionEvent3 = this.mFirstPointerDown;
        if (motionEvent3 == null || this.mSecondPointerDown == null) {
            return;
        }
        if (distance(motionEvent3, motionEvent) > this.mSwipeMinDistance) {
            completeGesture(motionEvent, motionEvent2, i);
        } else if (distance(this.mSecondPointerDown, motionEvent) > this.mSwipeMinDistance) {
            completeGesture(motionEvent, motionEvent2, i);
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void clear() {
        MotionEvent motionEvent = this.mFirstPointerDown;
        if (motionEvent != null) {
            motionEvent.recycle();
            this.mFirstPointerDown = null;
        }
        MotionEvent motionEvent2 = this.mSecondPointerDown;
        if (motionEvent2 != null) {
            motionEvent2.recycle();
            this.mSecondPointerDown = null;
        }
        super.clear();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String getGestureName() {
        return TwoFingersDownOrSwipe.class.getSimpleName();
    }

    public static double distance(MotionEvent motionEvent, MotionEvent motionEvent2) {
        int actionIndex = motionEvent.getActionIndex();
        int findPointerIndex = motionEvent2.findPointerIndex(motionEvent.getPointerId(actionIndex));
        if (findPointerIndex < 0) {
            return -1.0d;
        }
        return MathUtils.dist(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex), motionEvent2.getX(findPointerIndex), motionEvent2.getY(findPointerIndex));
    }
}
