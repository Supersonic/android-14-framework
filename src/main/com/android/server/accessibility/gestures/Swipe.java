package com.android.server.accessibility.gestures;

import android.content.Context;
import android.graphics.PointF;
import android.net.INetd;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.server.accessibility.gestures.GestureMatcher;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class Swipe extends GestureMatcher {
    public long mBaseTime;
    public float mBaseX;
    public float mBaseY;
    public int[] mDirections;
    public final float mGestureDetectionThresholdPixels;
    public final float mMinPixelsBetweenSamplesX;
    public final float mMinPixelsBetweenSamplesY;
    public float mPreviousGestureX;
    public float mPreviousGestureY;
    public final ArrayList<PointF> mStrokeBuffer;
    public int mTouchSlop;

    public static String directionToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? "Unknown Direction" : INetd.IF_STATE_DOWN : INetd.IF_STATE_UP : "right" : "left";
    }

    public Swipe(Context context, int i, int i2, GestureMatcher.StateChangeListener stateChangeListener) {
        this(context, new int[]{i}, i2, stateChangeListener);
    }

    public Swipe(Context context, int i, int i2, int i3, GestureMatcher.StateChangeListener stateChangeListener) {
        this(context, new int[]{i, i2}, i3, stateChangeListener);
    }

    public Swipe(Context context, int[] iArr, int i, GestureMatcher.StateChangeListener stateChangeListener) {
        super(i, new Handler(context.getMainLooper()), stateChangeListener);
        this.mStrokeBuffer = new ArrayList<>(100);
        this.mDirections = iArr;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.mGestureDetectionThresholdPixels = TypedValue.applyDimension(5, GestureUtils.MM_PER_CM, displayMetrics) * 1.0f;
        this.mMinPixelsBetweenSamplesX = (displayMetrics.xdpi / 2.54f) * 0.25f;
        this.mMinPixelsBetweenSamplesY = (displayMetrics.ydpi / 2.54f) * 0.25f;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        clear();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void clear() {
        this.mBaseX = Float.NaN;
        this.mBaseY = Float.NaN;
        this.mBaseTime = 0L;
        this.mPreviousGestureX = Float.NaN;
        this.mPreviousGestureY = Float.NaN;
        this.mStrokeBuffer.clear();
        super.clear();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (Float.isNaN(this.mBaseX) && Float.isNaN(this.mBaseY)) {
            this.mBaseX = motionEvent2.getX();
            this.mBaseY = motionEvent2.getY();
            this.mBaseTime = motionEvent2.getEventTime();
            this.mPreviousGestureX = this.mBaseX;
            this.mPreviousGestureY = this.mBaseY;
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onMove(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        float x = motionEvent2.getX();
        float y = motionEvent2.getY();
        long eventTime = motionEvent2.getEventTime();
        float abs = Math.abs(x - this.mPreviousGestureX);
        float abs2 = Math.abs(y - this.mPreviousGestureY);
        double hypot = Math.hypot(Math.abs(x - this.mBaseX), Math.abs(y - this.mBaseY));
        long j = eventTime - this.mBaseTime;
        if (TouchExplorer.DEBUG) {
            String gestureName = getGestureName();
            Slog.d(gestureName, "moveDelta:" + Double.toString(hypot) + " mGestureDetectionThreshold: " + Float.toString(this.mGestureDetectionThresholdPixels));
        }
        if (getState() == 0) {
            if (hypot < this.mTouchSlop) {
                return;
            }
            if (this.mStrokeBuffer.size() == 0) {
                if (toDirection(x - this.mBaseX, y - this.mBaseY) != this.mDirections[0]) {
                    cancelGesture(motionEvent, motionEvent2, i);
                    return;
                }
                this.mStrokeBuffer.add(new PointF(this.mBaseX, this.mBaseY));
            }
        }
        if (hypot > this.mGestureDetectionThresholdPixels) {
            this.mBaseX = x;
            this.mBaseY = y;
            this.mBaseTime = eventTime;
            startGesture(motionEvent, motionEvent2, i);
        } else if (getState() == 0) {
            if (j > 150) {
                cancelGesture(motionEvent, motionEvent2, i);
                return;
            }
        } else if (getState() == 1 && j > 350) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        if (abs >= this.mMinPixelsBetweenSamplesX || abs2 >= this.mMinPixelsBetweenSamplesY) {
            this.mPreviousGestureX = x;
            this.mPreviousGestureY = y;
            this.mStrokeBuffer.add(new PointF(x, y));
        }
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (getState() != 1) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        float x = motionEvent2.getX();
        float y = motionEvent2.getY();
        float abs = Math.abs(x - this.mPreviousGestureX);
        float abs2 = Math.abs(y - this.mPreviousGestureY);
        if (abs >= this.mMinPixelsBetweenSamplesX || abs2 >= this.mMinPixelsBetweenSamplesY) {
            this.mStrokeBuffer.add(new PointF(x, y));
        }
        recognizeGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerDown(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelGesture(motionEvent, motionEvent2, i);
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public void onPointerUp(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        cancelGesture(motionEvent, motionEvent2, i);
    }

    public final void recognizeGesture(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        if (this.mStrokeBuffer.size() < 2) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        ArrayList<PointF> arrayList = new ArrayList<>();
        PointF pointF = this.mStrokeBuffer.get(0);
        arrayList.add(pointF);
        PointF pointF2 = null;
        int i2 = 0;
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        for (int i3 = 1; i3 < this.mStrokeBuffer.size(); i3++) {
            pointF2 = this.mStrokeBuffer.get(i3);
            if (i2 > 0) {
                float f4 = i2;
                float f5 = f / f4;
                float f6 = f2 / f4;
                PointF pointF3 = new PointF((f3 * f5) + pointF.x, (f3 * f6) + pointF.y);
                float f7 = pointF2.x - pointF3.x;
                float f8 = pointF2.y - pointF3.y;
                float sqrt = (float) Math.sqrt((f7 * f7) + (f8 * f8));
                if ((f5 * (f7 / sqrt)) + (f6 * (f8 / sqrt)) < 0.0f) {
                    arrayList.add(pointF3);
                    f = 0.0f;
                    f2 = 0.0f;
                    pointF = pointF3;
                    i2 = 0;
                }
            }
            float f9 = pointF2.x - pointF.x;
            float f10 = pointF2.y - pointF.y;
            f3 = (float) Math.sqrt((f9 * f9) + (f10 * f10));
            i2++;
            f += f9 / f3;
            f2 += f10 / f3;
        }
        arrayList.add(pointF2);
        if (TouchExplorer.DEBUG) {
            Slog.d(getGestureName(), "path=" + arrayList.toString());
        }
        recognizeGesturePath(motionEvent, motionEvent2, i, arrayList);
    }

    public final void recognizeGesturePath(MotionEvent motionEvent, MotionEvent motionEvent2, int i, ArrayList<PointF> arrayList) {
        motionEvent.getDisplayId();
        if (arrayList.size() != this.mDirections.length + 1) {
            cancelGesture(motionEvent, motionEvent2, i);
            return;
        }
        int i2 = 0;
        while (i2 < arrayList.size() - 1) {
            PointF pointF = arrayList.get(i2);
            int i3 = i2 + 1;
            PointF pointF2 = arrayList.get(i3);
            int direction = toDirection(pointF2.x - pointF.x, pointF2.y - pointF.y);
            if (direction != this.mDirections[i2]) {
                if (TouchExplorer.DEBUG) {
                    String gestureName = getGestureName();
                    Slog.d(gestureName, "Found direction " + directionToString(direction) + " when expecting " + directionToString(this.mDirections[i2]));
                }
                cancelGesture(motionEvent, motionEvent2, i);
                return;
            }
            i2 = i3;
        }
        if (TouchExplorer.DEBUG) {
            Slog.d(getGestureName(), "Completed.");
        }
        completeGesture(motionEvent, motionEvent2, i);
    }

    public static int toDirection(float f, float f2) {
        return Math.abs(f) > Math.abs(f2) ? f < 0.0f ? 0 : 1 : f2 < 0.0f ? 2 : 3;
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String getGestureName() {
        StringBuilder sb = new StringBuilder();
        sb.append("Swipe ");
        sb.append(directionToString(this.mDirections[0]));
        for (int i = 1; i < this.mDirections.length; i++) {
            sb.append(" and ");
            sb.append(directionToString(this.mDirections[i]));
        }
        return sb.toString();
    }

    @Override // com.android.server.accessibility.gestures.GestureMatcher
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (getState() != 3) {
            sb.append(", mBaseX: ");
            sb.append(this.mBaseX);
            sb.append(", mBaseY: ");
            sb.append(this.mBaseY);
            sb.append(", mGestureDetectionThreshold:");
            sb.append(this.mGestureDetectionThresholdPixels);
            sb.append(", mMinPixelsBetweenSamplesX:");
            sb.append(this.mMinPixelsBetweenSamplesX);
            sb.append(", mMinPixelsBetweenSamplesY:");
            sb.append(this.mMinPixelsBetweenSamplesY);
        }
        return sb.toString();
    }
}
