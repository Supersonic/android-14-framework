package com.android.server.accessibility.magnification;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.server.accessibility.gestures.GestureMatcher;
import com.android.server.accessibility.gestures.GestureUtils;
/* loaded from: classes.dex */
public class SimpleSwipe extends GestureMatcher {
    public final int mDetectionDurationMillis;
    public MotionEvent mLastDown;
    public final int mSwipeMinDistance;

    public SimpleSwipe(Context context) {
        super(102, new Handler(context.getMainLooper()), null);
        this.mSwipeMinDistance = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mDetectionDurationMillis = MagnificationGestureMatcher.getMagnificationMultiTapTimeout(context);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        this.mLastDown = MotionEvent.obtain(motionEvent);
        cancelAfter(this.mDetectionDurationMillis, motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onMove(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (gestureMatched(motionEvent, motionEvent2, i)) {
            completeGesture(motionEvent, motionEvent2, i);
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (gestureMatched(motionEvent, motionEvent2, i)) {
            completeGesture(motionEvent, motionEvent2, i);
        } else {
            cancelGesture(motionEvent, motionEvent2, i);
        }
    }

    public final boolean gestureMatched(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        MotionEvent motionEvent3 = this.mLastDown;
        return motionEvent3 != null && GestureUtils.distance(motionEvent3, motionEvent) > ((double) this.mSwipeMinDistance);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void clear() {
        MotionEvent motionEvent = this.mLastDown;
        if (motionEvent != null) {
            motionEvent.recycle();
        }
        this.mLastDown = null;
        super.clear();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String getGestureName() {
        return getClass().getSimpleName();
    }
}
