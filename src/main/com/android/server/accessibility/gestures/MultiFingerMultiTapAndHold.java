package com.android.server.accessibility.gestures;

import android.content.Context;
import android.view.MotionEvent;
import com.android.server.accessibility.gestures.GestureMatcher;
/* loaded from: classes.dex */
public class MultiFingerMultiTapAndHold extends MultiFingerMultiTap {
    public MultiFingerMultiTapAndHold(Context context, int i, int i2, int i3, GestureMatcher.StateChangeListener stateChangeListener) {
        super(context, i, i2, i3, stateChangeListener);
    }

    @Override // com.android.server.accessibility.gestures.MultiFingerMultiTap, com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        super.onPointerDown(motionEvent, motionEvent2, i);
        if (this.mIsTargetFingerCountReached && this.mCompletedTapCount + 1 == this.mTargetTapCount) {
            completeAfterLongPressTimeout(motionEvent, motionEvent2, i);
        }
    }

    @Override // com.android.server.accessibility.gestures.MultiFingerMultiTap, com.android.server.accessibility.gestures.GestureMatcher
    public void onUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (this.mCompletedTapCount + 1 == this.mTargetTapCount) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        super.onUp(motionEvent, motionEvent2, i);
        cancelAfterDoubleTapTimeout(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.MultiFingerMultiTap, com.android.server.accessibility.gestures.GestureMatcher
    public String getGestureName() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.mTargetFingerCount);
        sb.append("-Finger ");
        int i = this.mTargetTapCount;
        if (i == 1) {
            sb.append("Single");
        } else if (i == 2) {
            sb.append("Double");
        } else if (i == 3) {
            sb.append("Triple");
        } else if (i > 3) {
            sb.append(i);
        }
        sb.append(" Tap and hold");
        return sb.toString();
    }
}
