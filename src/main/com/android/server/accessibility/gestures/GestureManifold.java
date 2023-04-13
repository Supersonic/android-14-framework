package com.android.server.accessibility.gestures;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.content.Context;
import android.os.Handler;
import android.util.Slog;
import android.view.MotionEvent;
import com.android.server.accessibility.gestures.GestureMatcher;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class GestureManifold implements GestureMatcher.StateChangeListener {
    public final Context mContext;
    public List<MotionEvent> mEvents;
    public final List<GestureMatcher> mGestures;
    public final Handler mHandler;
    public Listener mListener;
    public final List<GestureMatcher> mMultiFingerGestures;
    public boolean mMultiFingerGesturesEnabled;
    public boolean mSendMotionEventsEnabled;
    public boolean mServiceHandlesDoubleTap;
    public TouchState mState;
    public boolean mTwoFingerPassthroughEnabled;
    public final List<GestureMatcher> mTwoFingerSwipes;

    /* loaded from: classes.dex */
    public interface Listener {
        boolean onDoubleTap(MotionEvent motionEvent, MotionEvent motionEvent2, int i);

        void onDoubleTapAndHold(MotionEvent motionEvent, MotionEvent motionEvent2, int i);

        boolean onGestureCancelled(MotionEvent motionEvent, MotionEvent motionEvent2, int i);

        boolean onGestureCompleted(AccessibilityGestureEvent accessibilityGestureEvent);

        boolean onGestureStarted();
    }

    public GestureManifold(Context context, Listener listener, TouchState touchState, Handler handler) {
        ArrayList arrayList = new ArrayList();
        this.mGestures = arrayList;
        this.mServiceHandlesDoubleTap = false;
        this.mSendMotionEventsEnabled = false;
        ArrayList arrayList2 = new ArrayList();
        this.mMultiFingerGestures = arrayList2;
        ArrayList arrayList3 = new ArrayList();
        this.mTwoFingerSwipes = arrayList3;
        this.mEvents = new ArrayList();
        this.mContext = context;
        this.mHandler = handler;
        this.mListener = listener;
        this.mState = touchState;
        this.mMultiFingerGesturesEnabled = false;
        this.mTwoFingerPassthroughEnabled = false;
        arrayList.add(new MultiTap(context, 2, 17, this));
        arrayList.add(new MultiTapAndHold(context, 2, 18, this));
        arrayList.add(new SecondFingerMultiTap(context, 2, 17, this));
        arrayList.add(new Swipe(context, 1, 4, this));
        arrayList.add(new Swipe(context, 0, 3, this));
        arrayList.add(new Swipe(context, 2, 1, this));
        arrayList.add(new Swipe(context, 3, 2, this));
        arrayList.add(new Swipe(context, 0, 1, 5, this));
        arrayList.add(new Swipe(context, 0, 2, 9, this));
        arrayList.add(new Swipe(context, 0, 3, 10, this));
        arrayList.add(new Swipe(context, 1, 2, 11, this));
        arrayList.add(new Swipe(context, 1, 3, 12, this));
        arrayList.add(new Swipe(context, 1, 0, 6, this));
        arrayList.add(new Swipe(context, 3, 2, 8, this));
        arrayList.add(new Swipe(context, 3, 0, 15, this));
        arrayList.add(new Swipe(context, 3, 1, 16, this));
        arrayList.add(new Swipe(context, 2, 3, 7, this));
        arrayList.add(new Swipe(context, 2, 0, 13, this));
        arrayList.add(new Swipe(context, 2, 1, 14, this));
        arrayList2.add(new MultiFingerMultiTap(context, 2, 1, 19, this));
        arrayList2.add(new MultiFingerMultiTap(context, 2, 2, 20, this));
        arrayList2.add(new MultiFingerMultiTapAndHold(context, 2, 2, 40, this));
        arrayList2.add(new MultiFingerMultiTap(context, 2, 3, 21, this));
        arrayList2.add(new MultiFingerMultiTapAndHold(context, 2, 3, 43, this));
        arrayList2.add(new MultiFingerMultiTap(context, 3, 1, 22, this));
        arrayList2.add(new MultiFingerMultiTap(context, 3, 2, 23, this));
        arrayList2.add(new MultiFingerMultiTapAndHold(context, 3, 1, 44, this));
        arrayList2.add(new MultiFingerMultiTapAndHold(context, 3, 2, 41, this));
        arrayList2.add(new MultiFingerMultiTap(context, 3, 3, 24, this));
        arrayList2.add(new MultiFingerMultiTapAndHold(context, 3, 3, 45, this));
        arrayList2.add(new MultiFingerMultiTap(context, 3, 3, 24, this));
        arrayList2.add(new MultiFingerMultiTap(context, 4, 1, 37, this));
        arrayList2.add(new MultiFingerMultiTap(context, 4, 2, 38, this));
        arrayList2.add(new MultiFingerMultiTapAndHold(context, 4, 2, 42, this));
        arrayList2.add(new MultiFingerMultiTap(context, 4, 3, 39, this));
        arrayList3.add(new MultiFingerSwipe(context, 2, 3, 26, this));
        arrayList3.add(new MultiFingerSwipe(context, 2, 0, 27, this));
        arrayList3.add(new MultiFingerSwipe(context, 2, 1, 28, this));
        arrayList3.add(new MultiFingerSwipe(context, 2, 2, 25, this));
        arrayList2.addAll(arrayList3);
        arrayList2.add(new MultiFingerSwipe(context, 3, 3, 30, this));
        arrayList2.add(new MultiFingerSwipe(context, 3, 0, 31, this));
        arrayList2.add(new MultiFingerSwipe(context, 3, 1, 32, this));
        arrayList2.add(new MultiFingerSwipe(context, 3, 2, 29, this));
        arrayList2.add(new MultiFingerSwipe(context, 4, 3, 34, this));
        arrayList2.add(new MultiFingerSwipe(context, 4, 0, 35, this));
        arrayList2.add(new MultiFingerSwipe(context, 4, 1, 36, this));
        arrayList2.add(new MultiFingerSwipe(context, 4, 2, 33, this));
    }

    public boolean onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (this.mState.isClear()) {
            if (motionEvent.getActionMasked() != 0) {
                return false;
            }
            clear();
        }
        if (this.mSendMotionEventsEnabled) {
            this.mEvents.add(MotionEvent.obtainNoHistory(motionEvent2));
        }
        for (GestureMatcher gestureMatcher : this.mGestures) {
            if (gestureMatcher.getState() != 3) {
                boolean z = TouchExplorer.DEBUG;
                if (z) {
                    Slog.d("GestureManifold", gestureMatcher.toString());
                }
                gestureMatcher.onMotionEvent(motionEvent, motionEvent2, i);
                if (z) {
                    Slog.d("GestureManifold", gestureMatcher.toString());
                }
                if (gestureMatcher.getState() == 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clear() {
        for (GestureMatcher gestureMatcher : this.mGestures) {
            gestureMatcher.clear();
        }
        if (this.mEvents != null) {
            while (this.mEvents.size() > 0) {
                this.mEvents.remove(0).recycle();
            }
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher.StateChangeListener
    public void onStateChanged(int i, int i2, MotionEvent motionEvent, MotionEvent motionEvent2, int i3) {
        if (i2 == 1 && !this.mState.isGestureDetecting()) {
            if (i == 17 || i == 18) {
                if (this.mServiceHandlesDoubleTap) {
                    this.mListener.onGestureStarted();
                    return;
                }
                return;
            }
            this.mListener.onGestureStarted();
        } else if (i2 == 2) {
            onGestureCompleted(i, motionEvent, motionEvent2, i3);
        } else if (i2 == 3 && this.mState.isGestureDetecting()) {
            for (GestureMatcher gestureMatcher : this.mGestures) {
                if (gestureMatcher.getState() == 1) {
                    return;
                }
            }
            if (TouchExplorer.DEBUG) {
                Slog.d("GestureManifold", "Cancelling.");
            }
            this.mListener.onGestureCancelled(motionEvent, motionEvent2, i3);
        }
    }

    public final void onGestureCompleted(int i, MotionEvent motionEvent, MotionEvent motionEvent2, int i2) {
        if (i != 17) {
            if (i == 18) {
                if (this.mServiceHandlesDoubleTap) {
                    this.mListener.onGestureCompleted(new AccessibilityGestureEvent(i, motionEvent.getDisplayId(), this.mEvents));
                } else {
                    this.mListener.onDoubleTapAndHold(motionEvent, motionEvent2, i2);
                }
            } else {
                this.mListener.onGestureCompleted(new AccessibilityGestureEvent(i, motionEvent.getDisplayId(), this.mEvents));
            }
        } else if (this.mServiceHandlesDoubleTap) {
            this.mListener.onGestureCompleted(new AccessibilityGestureEvent(i, motionEvent.getDisplayId(), this.mEvents));
        } else {
            this.mListener.onDoubleTap(motionEvent, motionEvent2, i2);
        }
        clear();
    }

    public boolean isMultiFingerGesturesEnabled() {
        return this.mMultiFingerGesturesEnabled;
    }

    public void setMultiFingerGesturesEnabled(boolean z) {
        if (this.mMultiFingerGesturesEnabled != z) {
            this.mMultiFingerGesturesEnabled = z;
            if (z) {
                this.mGestures.addAll(this.mMultiFingerGestures);
            } else {
                this.mGestures.removeAll(this.mMultiFingerGestures);
            }
        }
    }

    public boolean isTwoFingerPassthroughEnabled() {
        return this.mTwoFingerPassthroughEnabled;
    }

    public void setTwoFingerPassthroughEnabled(boolean z) {
        if (this.mTwoFingerPassthroughEnabled != z) {
            this.mTwoFingerPassthroughEnabled = z;
            if (!z) {
                this.mMultiFingerGestures.addAll(this.mTwoFingerSwipes);
                if (this.mMultiFingerGesturesEnabled) {
                    this.mGestures.addAll(this.mTwoFingerSwipes);
                    return;
                }
                return;
            }
            this.mMultiFingerGestures.removeAll(this.mTwoFingerSwipes);
            this.mGestures.removeAll(this.mTwoFingerSwipes);
        }
    }

    public void setServiceHandlesDoubleTap(boolean z) {
        this.mServiceHandlesDoubleTap = z;
    }

    public void setSendMotionEventsEnabled(boolean z) {
        this.mSendMotionEventsEnabled = z;
        if (z) {
            return;
        }
        while (this.mEvents.size() > 0) {
            this.mEvents.remove(0).recycle();
        }
    }

    public boolean isSendMotionEventsEnabled() {
        return this.mSendMotionEventsEnabled;
    }

    public List<MotionEvent> getMotionEvents() {
        return this.mEvents;
    }
}
