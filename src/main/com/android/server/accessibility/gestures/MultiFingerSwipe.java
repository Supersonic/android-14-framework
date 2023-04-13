package com.android.server.accessibility.gestures;

import android.content.Context;
import android.graphics.PointF;
import android.net.INetd;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.server.accessibility.gestures.GestureMatcher;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class MultiFingerSwipe extends GestureMatcher {
    public PointF[] mBase;
    public int mCurrentFingerCount;
    public int mDirection;
    public final float mMinPixelsBetweenSamplesX;
    public final float mMinPixelsBetweenSamplesY;
    public int[] mPointerIds;
    public PointF[] mPreviousGesturePoint;
    public final ArrayList<PointF>[] mStrokeBuffers;
    public int mTargetFingerCount;
    public boolean mTargetFingerCountReached;
    public int mTouchSlop;

    public static String directionToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? "Unknown Direction" : INetd.IF_STATE_DOWN : INetd.IF_STATE_UP : "right" : "left";
    }

    public MultiFingerSwipe(Context context, int i, int i2, int i3, GestureMatcher.StateChangeListener stateChangeListener) {
        super(i3, new Handler(context.getMainLooper()), stateChangeListener);
        this.mTargetFingerCountReached = false;
        this.mTargetFingerCount = i;
        this.mPointerIds = new int[i];
        this.mBase = new PointF[i];
        this.mPreviousGesturePoint = new PointF[i];
        this.mStrokeBuffers = new ArrayList[i];
        this.mDirection = i2;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float f = displayMetrics.xdpi;
        float f2 = GestureUtils.CM_PER_INCH;
        this.mMinPixelsBetweenSamplesX = (f / f2) * 0.25f;
        this.mMinPixelsBetweenSamplesY = (displayMetrics.ydpi / f2) * 0.25f;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        clear();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void clear() {
        this.mTargetFingerCountReached = false;
        this.mCurrentFingerCount = 0;
        for (int i = 0; i < this.mTargetFingerCount; i++) {
            this.mPointerIds[i] = -1;
            PointF[] pointFArr = this.mBase;
            if (pointFArr[i] == null) {
                pointFArr[i] = new PointF();
            }
            PointF pointF = this.mBase[i];
            pointF.x = Float.NaN;
            pointF.y = Float.NaN;
            PointF[] pointFArr2 = this.mPreviousGesturePoint;
            if (pointFArr2[i] == null) {
                pointFArr2[i] = new PointF();
            }
            PointF pointF2 = this.mPreviousGesturePoint[i];
            pointF2.x = Float.NaN;
            pointF2.y = Float.NaN;
            ArrayList<PointF>[] arrayListArr = this.mStrokeBuffers;
            if (arrayListArr[i] == null) {
                arrayListArr[i] = new ArrayList<>(100);
            }
            this.mStrokeBuffers[i].clear();
        }
        super.clear();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (this.mCurrentFingerCount > 0) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        this.mCurrentFingerCount = 1;
        int actionIndex = GestureUtils.getActionIndex(motionEvent2);
        int pointerId = motionEvent2.getPointerId(actionIndex);
        int pointerCount = motionEvent2.getPointerCount() - 1;
        if (pointerId < 0) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        int[] iArr = this.mPointerIds;
        if (iArr[pointerCount] != -1) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        iArr[pointerCount] = pointerId;
        if (Float.isNaN(this.mBase[pointerCount].x) && Float.isNaN(this.mBase[pointerCount].y)) {
            float x = motionEvent2.getX(actionIndex);
            float y = motionEvent2.getY(actionIndex);
            if (x < 0.0f || y < 0.0f) {
                cancelGesture(motionEvent, motionEvent2, i);
                return;
            }
            PointF pointF = this.mBase[pointerCount];
            pointF.x = x;
            pointF.y = y;
            PointF pointF2 = this.mPreviousGesturePoint[pointerCount];
            pointF2.x = x;
            pointF2.y = y;
            return;
        }
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (motionEvent.getPointerCount() > this.mTargetFingerCount) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        int i2 = this.mCurrentFingerCount + 1;
        this.mCurrentFingerCount = i2;
        if (i2 != motionEvent2.getPointerCount()) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        if (this.mCurrentFingerCount == this.mTargetFingerCount) {
            this.mTargetFingerCountReached = true;
        }
        int actionIndex = GestureUtils.getActionIndex(motionEvent2);
        int pointerId = motionEvent2.getPointerId(actionIndex);
        if (pointerId < 0) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        int i3 = this.mCurrentFingerCount - 1;
        int[] iArr = this.mPointerIds;
        if (iArr[i3] != -1) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        iArr[i3] = pointerId;
        if (Float.isNaN(this.mBase[i3].x) && Float.isNaN(this.mBase[i3].y)) {
            float x = motionEvent2.getX(actionIndex);
            float y = motionEvent2.getY(actionIndex);
            if (x < 0.0f || y < 0.0f) {
                cancelGesture(motionEvent, motionEvent2, i);
                return;
            }
            PointF pointF = this.mBase[i3];
            pointF.x = x;
            pointF.y = y;
            PointF pointF2 = this.mPreviousGesturePoint[i3];
            pointF2.x = x;
            pointF2.y = y;
            return;
        }
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (!this.mTargetFingerCountReached) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        this.mCurrentFingerCount--;
        int actionIndex = GestureUtils.getActionIndex(motionEvent);
        int pointerId = motionEvent.getPointerId(actionIndex);
        if (pointerId < 0) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        int binarySearch = Arrays.binarySearch(this.mPointerIds, pointerId);
        if (binarySearch < 0) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        float x = motionEvent2.getX(actionIndex);
        float y = motionEvent2.getY(actionIndex);
        if (x < 0.0f || y < 0.0f) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        float abs = Math.abs(x - this.mPreviousGesturePoint[binarySearch].x);
        float abs2 = Math.abs(y - this.mPreviousGesturePoint[binarySearch].y);
        if (abs >= this.mMinPixelsBetweenSamplesX || abs2 >= this.mMinPixelsBetweenSamplesY) {
            this.mStrokeBuffers[binarySearch].add(new PointF(x, y));
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onMove(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        for (int i2 = 0; i2 < this.mTargetFingerCount; i2++) {
            if (this.mPointerIds[i2] != -1) {
                boolean z = TouchExplorer.DEBUG;
                if (z) {
                    Slog.d(getGestureName(), "Processing move on finger " + i2);
                }
                int findPointerIndex = motionEvent2.findPointerIndex(this.mPointerIds[i2]);
                if (findPointerIndex >= 0) {
                    float x = motionEvent2.getX(findPointerIndex);
                    float y = motionEvent2.getY(findPointerIndex);
                    if (x < 0.0f || y < 0.0f) {
                        cancelGesture(motionEvent, motionEvent2, i);
                        return;
                    }
                    float abs = Math.abs(x - this.mPreviousGesturePoint[i2].x);
                    float abs2 = Math.abs(y - this.mPreviousGesturePoint[i2].y);
                    double hypot = Math.hypot(Math.abs(x - this.mBase[i2].x), Math.abs(y - this.mBase[i2].y));
                    if (z) {
                        Slog.d(getGestureName(), "moveDelta:" + hypot);
                    }
                    if (getState() == 0) {
                        int i3 = this.mTargetFingerCount;
                        if (hypot < this.mTouchSlop * i3) {
                            continue;
                        } else if (this.mCurrentFingerCount != i3) {
                            cancelGesture(motionEvent, motionEvent2, i);
                            return;
                        } else {
                            PointF pointF = this.mBase[i2];
                            if (toDirection(x - pointF.x, y - pointF.y) != this.mDirection) {
                                cancelGesture(motionEvent, motionEvent2, i);
                                return;
                            }
                            startGesture(motionEvent, motionEvent2, i);
                            for (int i4 = 0; i4 < this.mTargetFingerCount; i4++) {
                                this.mStrokeBuffers[i4].add(new PointF(this.mBase[i4]));
                            }
                        }
                    } else if (getState() == 1) {
                        PointF pointF2 = this.mBase[i2];
                        if (toDirection(x - pointF2.x, y - pointF2.y) != this.mDirection) {
                            cancelGesture(motionEvent, motionEvent2, i);
                            return;
                        } else if (abs >= this.mMinPixelsBetweenSamplesX || abs2 >= this.mMinPixelsBetweenSamplesY) {
                            PointF pointF3 = this.mPreviousGesturePoint[i2];
                            pointF3.x = x;
                            pointF3.y = y;
                            this.mStrokeBuffers[i2].add(new PointF(x, y));
                        }
                    } else {
                        continue;
                    }
                } else if (z) {
                    Slog.d(getGestureName(), "Finger " + i2 + " not found in this event. skipping.");
                }
            }
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (getState() != 1) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        this.mCurrentFingerCount = 0;
        int actionIndex = GestureUtils.getActionIndex(motionEvent);
        int binarySearch = Arrays.binarySearch(this.mPointerIds, motionEvent.getPointerId(actionIndex));
        if (binarySearch < 0) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        float x = motionEvent2.getX(actionIndex);
        float y = motionEvent2.getY(actionIndex);
        if (x < 0.0f || y < 0.0f) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        float abs = Math.abs(x - this.mPreviousGesturePoint[binarySearch].x);
        float abs2 = Math.abs(y - this.mPreviousGesturePoint[binarySearch].y);
        if (abs >= this.mMinPixelsBetweenSamplesX || abs2 >= this.mMinPixelsBetweenSamplesY) {
            this.mStrokeBuffers[binarySearch].add(new PointF(x, y));
        }
        recognizeGesture(motionEvent, motionEvent2, i);
    }

    public final void recognizeGesture(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        for (int i2 = 0; i2 < this.mTargetFingerCount; i2++) {
            boolean z = TouchExplorer.DEBUG;
            if (z) {
                String gestureName = getGestureName();
                Slog.d(gestureName, "Recognizing finger: " + i2);
            }
            if (this.mStrokeBuffers[i2].size() < 2) {
                Slog.d(getGestureName(), "Too few points.");
                cancelGesture(motionEvent, motionEvent2, i);
                return;
            }
            ArrayList<PointF> arrayList = this.mStrokeBuffers[i2];
            if (z) {
                String gestureName2 = getGestureName();
                Slog.d(gestureName2, "path=" + arrayList.toString());
            }
            if (!recognizeGesturePath(motionEvent, motionEvent2, i, arrayList)) {
                cancelGesture(motionEvent, motionEvent2, i);
                return;
            }
        }
        completeGesture(motionEvent, motionEvent2, i);
    }

    public final boolean recognizeGesturePath(MotionEvent motionEvent, MotionEvent motionEvent2, int i, ArrayList<PointF> arrayList) {
        int direction;
        motionEvent.getDisplayId();
        int i2 = 0;
        while (i2 < arrayList.size() - 1) {
            PointF pointF = arrayList.get(i2);
            i2++;
            PointF pointF2 = arrayList.get(i2);
            if (toDirection(pointF2.x - pointF.x, pointF2.y - pointF.y) != this.mDirection) {
                if (TouchExplorer.DEBUG) {
                    Slog.d(getGestureName(), "Found direction " + directionToString(direction) + " when expecting " + directionToString(this.mDirection));
                }
                return false;
            }
        }
        if (TouchExplorer.DEBUG) {
            Slog.d(getGestureName(), "Completed.");
        }
        return true;
    }

    public static int toDirection(float f, float f2) {
        return Math.abs(f) > Math.abs(f2) ? f < 0.0f ? 0 : 1 : f2 < 0.0f ? 2 : 3;
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String getGestureName() {
        return this.mTargetFingerCount + "-finger Swipe " + directionToString(this.mDirection);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (getState() != 3) {
            sb.append(", mBase: ");
            sb.append(Arrays.toString(this.mBase));
            sb.append(", mMinPixelsBetweenSamplesX:");
            sb.append(this.mMinPixelsBetweenSamplesX);
            sb.append(", mMinPixelsBetweenSamplesY:");
            sb.append(this.mMinPixelsBetweenSamplesY);
        }
        return sb.toString();
    }
}
