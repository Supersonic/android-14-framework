package com.android.server.accessibility.magnification;

import android.view.MotionEvent;
import com.android.server.accessibility.gestures.GestureMatcher;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class GesturesObserver implements GestureMatcher.StateChangeListener {
    public final Listener mListener;
    public final List<GestureMatcher> mGestureMatchers = new ArrayList();
    public boolean mObserveStarted = false;
    public boolean mProcessMotionEvent = false;
    public int mCancelledMatcherSize = 0;

    /* loaded from: classes.dex */
    public interface Listener {
        void onGestureCancelled(MotionEvent motionEvent, MotionEvent motionEvent2, int i);

        void onGestureCompleted(int i, MotionEvent motionEvent, MotionEvent motionEvent2, int i2);
    }

    public GesturesObserver(Listener listener, GestureMatcher... gestureMatcherArr) {
        this.mListener = listener;
        for (int i = 0; i < gestureMatcherArr.length; i++) {
            gestureMatcherArr[i].setListener(this);
            this.mGestureMatchers.add(gestureMatcherArr[i]);
        }
    }

    public boolean onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (!this.mObserveStarted) {
            if (motionEvent.getActionMasked() != 0) {
                this.mListener.onGestureCancelled(motionEvent, motionEvent2, i);
                clear();
                return false;
            }
            this.mObserveStarted = true;
        }
        this.mProcessMotionEvent = true;
        for (int i2 = 0; i2 < this.mGestureMatchers.size(); i2++) {
            GestureMatcher gestureMatcher = this.mGestureMatchers.get(i2);
            gestureMatcher.onMotionEvent(motionEvent, motionEvent2, i);
            if (gestureMatcher.getState() == 2) {
                clear();
                this.mProcessMotionEvent = false;
                return true;
            }
        }
        this.mProcessMotionEvent = false;
        return false;
    }

    public final void clear() {
        for (GestureMatcher gestureMatcher : this.mGestureMatchers) {
            gestureMatcher.clear();
        }
        this.mCancelledMatcherSize = 0;
        this.mObserveStarted = false;
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher.StateChangeListener
    public void onStateChanged(int i, int i2, MotionEvent motionEvent, MotionEvent motionEvent2, int i3) {
        if (i2 == 2) {
            this.mListener.onGestureCompleted(i, motionEvent, motionEvent2, i3);
            if (this.mProcessMotionEvent) {
                return;
            }
            clear();
        } else if (i2 == 3) {
            int i4 = this.mCancelledMatcherSize + 1;
            this.mCancelledMatcherSize = i4;
            if (i4 == this.mGestureMatchers.size()) {
                this.mListener.onGestureCancelled(motionEvent, motionEvent2, i3);
                clear();
            }
        }
    }
}
