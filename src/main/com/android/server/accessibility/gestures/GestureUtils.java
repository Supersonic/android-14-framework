package com.android.server.accessibility.gestures;

import android.graphics.PointF;
import android.util.MathUtils;
import android.view.MotionEvent;
/* loaded from: classes.dex */
public final class GestureUtils {
    public static float CM_PER_INCH = 2.54f;
    public static int MM_PER_CM = 10;

    public static boolean isMultiTap(MotionEvent motionEvent, MotionEvent motionEvent2, int i, int i2) {
        if (motionEvent == null || motionEvent2 == null) {
            return false;
        }
        return eventsWithinTimeAndDistanceSlop(motionEvent, motionEvent2, i, i2);
    }

    public static boolean eventsWithinTimeAndDistanceSlop(MotionEvent motionEvent, MotionEvent motionEvent2, int i, int i2) {
        return !isTimedOut(motionEvent, motionEvent2, i) && distance(motionEvent, motionEvent2) < ((double) i2);
    }

    public static double distance(MotionEvent motionEvent, MotionEvent motionEvent2) {
        return MathUtils.dist(motionEvent.getX(), motionEvent.getY(), motionEvent2.getX(), motionEvent2.getY());
    }

    public static double distanceClosestPointerToPoint(PointF pointF, MotionEvent motionEvent) {
        float f = Float.MAX_VALUE;
        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            float dist = MathUtils.dist(pointF.x, pointF.y, motionEvent.getX(i), motionEvent.getY(i));
            if (f > dist) {
                f = dist;
            }
        }
        return f;
    }

    public static boolean isTimedOut(MotionEvent motionEvent, MotionEvent motionEvent2, int i) {
        return motionEvent2.getEventTime() - motionEvent.getEventTime() >= ((long) i);
    }

    public static boolean isDraggingGesture(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9) {
        float f10 = f5 - f;
        float f11 = f6 - f2;
        if (f10 == 0.0f && f11 == 0.0f) {
            return true;
        }
        float hypot = (float) Math.hypot(f10, f11);
        int i = (hypot > 0.0f ? 1 : (hypot == 0.0f ? 0 : -1));
        if (i > 0) {
            f10 /= hypot;
        }
        if (i > 0) {
            f11 /= hypot;
        }
        float f12 = f7 - f3;
        float f13 = f8 - f4;
        if (f12 == 0.0f && f13 == 0.0f) {
            return true;
        }
        float hypot2 = (float) Math.hypot(f12, f13);
        int i2 = (hypot2 > 0.0f ? 1 : (hypot2 == 0.0f ? 0 : -1));
        if (i2 > 0) {
            f12 /= hypot2;
        }
        if (i2 > 0) {
            f13 /= hypot2;
        }
        return (f10 * f12) + (f11 * f13) >= f9;
    }

    public static int getActionIndex(MotionEvent motionEvent) {
        return (motionEvent.getAction() & 65280) >> 8;
    }
}
