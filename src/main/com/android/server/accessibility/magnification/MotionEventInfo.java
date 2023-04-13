package com.android.server.accessibility.magnification;

import android.view.MotionEvent;
/* loaded from: classes.dex */
public final class MotionEventInfo {
    public MotionEvent mEvent;
    public int mPolicyFlags;
    public MotionEvent mRawEvent;

    public static MotionEventInfo obtain(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        return new MotionEventInfo(MotionEvent.obtain(motionEvent), MotionEvent.obtain(motionEvent2), i);
    }

    public MotionEventInfo(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        this.mEvent = motionEvent;
        this.mRawEvent = motionEvent2;
        this.mPolicyFlags = i;
    }

    public void recycle() {
        this.mEvent = recycleAndNullify(this.mEvent);
        this.mRawEvent = recycleAndNullify(this.mRawEvent);
    }

    public String toString() {
        return MotionEvent.actionToString(this.mEvent.getAction()).replace("ACTION_", "");
    }

    public static MotionEvent recycleAndNullify(MotionEvent motionEvent) {
        if (motionEvent != null) {
            motionEvent.recycle();
            return null;
        }
        return null;
    }
}
