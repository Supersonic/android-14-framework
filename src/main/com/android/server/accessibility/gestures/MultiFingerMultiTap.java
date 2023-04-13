package com.android.server.accessibility.gestures;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.internal.util.Preconditions;
import com.android.server.accessibility.gestures.GestureMatcher;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class MultiFingerMultiTap extends GestureMatcher {
    public PointF[] mBases;
    public int mCompletedTapCount;
    public int mDoubleTapSlop;
    public ArrayList<PointF> mExcludedPointsForDownSlopChecked;
    public boolean mIsTargetFingerCountReached;
    public final int mTargetFingerCount;
    public final int mTargetTapCount;
    public int mTouchSlop;

    public MultiFingerMultiTap(Context context, int i, int i2, int i3, GestureMatcher.StateChangeListener stateChangeListener) {
        super(i3, new Handler(context.getMainLooper()), stateChangeListener);
        int i4 = 0;
        this.mIsTargetFingerCountReached = false;
        Preconditions.checkArgument(i >= 2);
        Preconditions.checkArgumentPositive(i2, "Tap count must greater than 0.");
        this.mTargetTapCount = i2;
        this.mTargetFingerCount = i;
        this.mDoubleTapSlop = ViewConfiguration.get(context).getScaledDoubleTapSlop() * i;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * i;
        this.mBases = new PointF[i];
        while (true) {
            PointF[] pointFArr = this.mBases;
            if (i4 < pointFArr.length) {
                pointFArr[i4] = new PointF();
                i4++;
            } else {
                this.mExcludedPointsForDownSlopChecked = new ArrayList<>(this.mTargetFingerCount);
                clear();
                return;
            }
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void clear() {
        int i = 0;
        this.mCompletedTapCount = 0;
        this.mIsTargetFingerCountReached = false;
        while (true) {
            PointF[] pointFArr = this.mBases;
            if (i < pointFArr.length) {
                pointFArr[i].set(Float.NaN, Float.NaN);
                i++;
            } else {
                this.mExcludedPointsForDownSlopChecked.clear();
                super.clear();
                return;
            }
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (this.mCompletedTapCount == this.mTargetTapCount) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        cancelAfterTapTimeout(motionEvent, motionEvent2, i);
        if (this.mCompletedTapCount == 0) {
            initBaseLocation(motionEvent2);
            return;
        }
        PointF findNearestPoint = findNearestPoint(motionEvent2, this.mDoubleTapSlop, true);
        if (findNearestPoint != null) {
            int actionIndex = motionEvent.getActionIndex();
            findNearestPoint.set(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
            return;
        }
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelAfterDoubleTapTimeout(motionEvent, motionEvent2, i);
        PointF findNearestPoint = findNearestPoint(motionEvent2, this.mTouchSlop, false);
        if ((getState() == 1 || getState() == 0) && findNearestPoint != null) {
            if (this.mIsTargetFingerCountReached) {
                this.mCompletedTapCount++;
                this.mIsTargetFingerCountReached = false;
                this.mExcludedPointsForDownSlopChecked.clear();
            }
            if (this.mCompletedTapCount == 1) {
                startGesture(motionEvent, motionEvent2, i);
            }
            if (this.mCompletedTapCount == this.mTargetTapCount) {
                completeAfterDoubleTapTimeout(motionEvent, motionEvent2, i);
                return;
            }
            return;
        }
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onMove(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (findNearestPoint(motionEvent2, this.mTouchSlop, false) == null) {
            cancelGesture(motionEvent, motionEvent2, i);
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        PointF findNearestPoint;
        cancelAfterTapTimeout(motionEvent, motionEvent2, i);
        int pointerCount = motionEvent.getPointerCount();
        if (pointerCount > this.mTargetFingerCount || this.mIsTargetFingerCountReached) {
            this.mIsTargetFingerCountReached = false;
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        if (this.mCompletedTapCount == 0) {
            findNearestPoint = initBaseLocation(motionEvent2);
        } else {
            findNearestPoint = findNearestPoint(motionEvent2, this.mDoubleTapSlop, true);
        }
        if ((getState() == 1 || getState() == 0) && findNearestPoint != null) {
            if (pointerCount == this.mTargetFingerCount) {
                this.mIsTargetFingerCountReached = true;
            }
            int actionIndex = motionEvent.getActionIndex();
            findNearestPoint.set(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
            return;
        }
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (!this.mIsTargetFingerCountReached) {
            cancelGesture(motionEvent, motionEvent2, i);
        } else if (getState() == 1 || getState() == 0) {
            cancelAfterTapTimeout(motionEvent, motionEvent2, i);
        } else {
            cancelGesture(motionEvent, motionEvent2, i);
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
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
        sb.append(" Tap");
        return sb.toString();
    }

    public final PointF initBaseLocation(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        PointF pointF = this.mBases[motionEvent.getPointerCount() - 1];
        if (Float.isNaN(pointF.x) && Float.isNaN(pointF.y)) {
            pointF.set(motionEvent.getX(actionIndex), motionEvent.getY(actionIndex));
        }
        return pointF;
    }

    public final PointF findNearestPoint(MotionEvent motionEvent, float f, boolean z) {
        float f2 = Float.MAX_VALUE;
        int i = 0;
        PointF pointF = null;
        while (true) {
            PointF[] pointFArr = this.mBases;
            if (i >= pointFArr.length) {
                if (f2 < f) {
                    if (z) {
                        this.mExcludedPointsForDownSlopChecked.add(pointF);
                    }
                    return pointF;
                }
                return null;
            }
            PointF pointF2 = pointFArr[i];
            if ((!Float.isNaN(pointF2.x) || !Float.isNaN(pointF2.y)) && (!z || !this.mExcludedPointsForDownSlopChecked.contains(pointF2))) {
                int actionIndex = motionEvent.getActionIndex();
                float x = pointF2.x - motionEvent.getX(actionIndex);
                float y = pointF2.y - motionEvent.getY(actionIndex);
                if (x == 0.0f && y == 0.0f) {
                    if (z) {
                        this.mExcludedPointsForDownSlopChecked.add(pointF2);
                    }
                    return pointF2;
                }
                float hypot = (float) Math.hypot(x, y);
                if (f2 > hypot) {
                    pointF = pointF2;
                    f2 = hypot;
                }
            }
            i++;
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (getState() != 3) {
            sb.append(", CompletedTapCount: ");
            sb.append(this.mCompletedTapCount);
            sb.append(", IsTargetFingerCountReached: ");
            sb.append(this.mIsTargetFingerCountReached);
            sb.append(", Bases: ");
            sb.append(Arrays.toString(this.mBases));
            sb.append(", ExcludedPointsForDownSlopChecked: ");
            sb.append(this.mExcludedPointsForDownSlopChecked.toString());
        }
        return sb.toString();
    }
}
