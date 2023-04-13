package com.android.server.accessibility.magnification;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Slog;
import android.view.MotionEvent;
import com.android.server.accessibility.gestures.GestureMatcher;
import com.android.server.accessibility.magnification.GesturesObserver;
import java.util.LinkedList;
import java.util.List;
/* loaded from: classes.dex */
public class MagnificationGesturesObserver implements GesturesObserver.Listener {
    @SuppressLint({"LongLogTag"})
    public static final boolean DBG = Log.isLoggable("MagnificationGesturesObserver", 3);
    public final Callback mCallback;
    public List<MotionEventInfo> mDelayedEventQueue;
    public final GesturesObserver mGesturesObserver;
    public long mLastDownEventTime = 0;
    public MotionEvent mLastEvent;

    /* loaded from: classes.dex */
    public interface Callback {
        void onGestureCancelled(long j, List<MotionEventInfo> list, MotionEvent motionEvent);

        void onGestureCompleted(int i, long j, List<MotionEventInfo> list, MotionEvent motionEvent);

        boolean shouldStopDetection(MotionEvent motionEvent);
    }

    public MagnificationGesturesObserver(Callback callback, GestureMatcher... gestureMatcherArr) {
        this.mGesturesObserver = new GesturesObserver(this, gestureMatcherArr);
        this.mCallback = callback;
    }

    public boolean onMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (DBG) {
            Slog.d("MagnificationGesturesObserver", "DetectGesture: event = " + motionEvent);
        }
        cacheDelayedMotionEvent(motionEvent, motionEvent2, i);
        if (this.mCallback.shouldStopDetection(motionEvent)) {
            notifyDetectionCancel();
            return false;
        }
        if (motionEvent.getActionMasked() == 0) {
            this.mLastDownEventTime = motionEvent.getDownTime();
        }
        return this.mGesturesObserver.onMotionEvent(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.magnification.GesturesObserver.Listener
    public void onGestureCompleted(int i, MotionEvent motionEvent, MotionEvent motionEvent2, int i2) {
        if (DBG) {
            Slog.d("MagnificationGesturesObserver", "onGestureCompleted: " + MagnificationGestureMatcher.gestureIdToString(i) + " event = " + motionEvent);
        }
        List<MotionEventInfo> list = this.mDelayedEventQueue;
        this.mDelayedEventQueue = null;
        this.mCallback.onGestureCompleted(i, this.mLastDownEventTime, list, motionEvent);
        clear();
    }

    @Override // com.android.server.accessibility.magnification.GesturesObserver.Listener
    public void onGestureCancelled(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (DBG) {
            Slog.d("MagnificationGesturesObserver", "onGestureCancelled:  event = " + motionEvent);
        }
        notifyDetectionCancel();
    }

    public final void notifyDetectionCancel() {
        List<MotionEventInfo> list = this.mDelayedEventQueue;
        this.mDelayedEventQueue = null;
        this.mCallback.onGestureCancelled(this.mLastDownEventTime, list, this.mLastEvent);
        clear();
    }

    public final void clear() {
        if (DBG) {
            Slog.d("MagnificationGesturesObserver", "clear:" + this.mDelayedEventQueue);
        }
        recycleLastEvent();
        this.mLastDownEventTime = 0L;
        List<MotionEventInfo> list = this.mDelayedEventQueue;
        if (list != null) {
            for (MotionEventInfo motionEventInfo : list) {
                motionEventInfo.recycle();
            }
            this.mDelayedEventQueue.clear();
            this.mDelayedEventQueue = null;
        }
    }

    public final void recycleLastEvent() {
        MotionEvent motionEvent = this.mLastEvent;
        if (motionEvent == null) {
            return;
        }
        motionEvent.recycle();
        this.mLastEvent = null;
    }

    public final void cacheDelayedMotionEvent(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        this.mLastEvent = MotionEvent.obtain(motionEvent);
        MotionEventInfo obtain = MotionEventInfo.obtain(motionEvent, motionEvent2, i);
        if (this.mDelayedEventQueue == null) {
            this.mDelayedEventQueue = new LinkedList();
        }
        this.mDelayedEventQueue.add(obtain);
    }

    public String toString() {
        return "MagnificationGesturesObserver{mDelayedEventQueue=" + this.mDelayedEventQueue + '}';
    }
}
