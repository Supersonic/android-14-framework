package com.android.server.accessibility.magnification;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
/* loaded from: classes.dex */
public class PanningScalingHandler extends GestureDetector.SimpleOnGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
    public static final boolean DEBUG = Log.isLoggable("PanningScalingHandler", 3);
    public final boolean mBlockScroll;
    public final int mDisplayId;
    public boolean mEnable;
    public float mInitialScaleFactor = -1.0f;
    public final MagnificationDelegate mMagnificationDelegate;
    public final float mMaxScale;
    public final float mMinScale;
    public final ScaleGestureDetector mScaleGestureDetector;
    public boolean mScaling;
    public final float mScalingThreshold;
    public final GestureDetector mScrollGestureDetector;

    /* loaded from: classes.dex */
    public interface MagnificationDelegate {
        float getScale(int i);

        boolean processScroll(int i, float f, float f2);

        void setScale(int i, float f);
    }

    public PanningScalingHandler(Context context, float f, float f2, boolean z, MagnificationDelegate magnificationDelegate) {
        this.mDisplayId = context.getDisplayId();
        this.mMaxScale = f;
        this.mMinScale = f2;
        this.mBlockScroll = z;
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, this, Handler.getMain());
        this.mScaleGestureDetector = scaleGestureDetector;
        this.mScrollGestureDetector = new GestureDetector(context, this, Handler.getMain());
        scaleGestureDetector.setQuickScaleEnabled(false);
        this.mMagnificationDelegate = magnificationDelegate;
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(17105110, typedValue, false);
        this.mScalingThreshold = typedValue.getFloat();
    }

    public void setEnabled(boolean z) {
        clear();
        this.mEnable = z;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        this.mScaleGestureDetector.onTouchEvent(motionEvent);
        this.mScrollGestureDetector.onTouchEvent(motionEvent);
    }

    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.mEnable) {
            if (this.mBlockScroll && this.mScaling) {
                return true;
            }
            return this.mMagnificationDelegate.processScroll(this.mDisplayId, f, f2);
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0057, code lost:
        if (r7 < r2) goto L20;
     */
    /* JADX WARN: Removed duplicated region for block: B:28:0x005c  */
    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        boolean z = DEBUG;
        if (z) {
            Slog.i("PanningScalingHandler", "onScale: triggered ");
        }
        if (!this.mScaling) {
            if (this.mInitialScaleFactor < 0.0f) {
                this.mInitialScaleFactor = scaleGestureDetector.getScaleFactor();
                return false;
            }
            boolean z2 = Math.abs(scaleGestureDetector.getScaleFactor() - this.mInitialScaleFactor) > this.mScalingThreshold;
            this.mScaling = z2;
            return z2;
        }
        float scale = this.mMagnificationDelegate.getScale(this.mDisplayId);
        float scaleFactor = scaleGestureDetector.getScaleFactor() * scale;
        float f = this.mMaxScale;
        if (scaleFactor <= f || scaleFactor <= scale) {
            f = this.mMinScale;
            if (scaleFactor < f) {
            }
            if (z) {
                Slog.i("PanningScalingHandler", "Scaled content to: " + scaleFactor + "x");
            }
            this.mMagnificationDelegate.setScale(this.mDisplayId, scaleFactor);
            return true;
        }
        scaleFactor = f;
        if (z) {
        }
        this.mMagnificationDelegate.setScale(this.mDisplayId, scaleFactor);
        return true;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return this.mEnable;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        clear();
    }

    public void clear() {
        this.mInitialScaleFactor = -1.0f;
        this.mScaling = false;
    }

    public String toString() {
        return "PanningScalingHandler{mInitialScaleFactor=" + this.mInitialScaleFactor + ", mScaling=" + this.mScaling + '}';
    }
}
