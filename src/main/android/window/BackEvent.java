package android.window;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public final class BackEvent {
    public static final int EDGE_LEFT = 0;
    public static final int EDGE_RIGHT = 1;
    private final float mProgress;
    private final int mSwipeEdge;
    private final float mTouchX;
    private final float mTouchY;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface SwipeEdge {
    }

    public BackEvent(float touchX, float touchY, float progress, int swipeEdge) {
        this.mTouchX = touchX;
        this.mTouchY = touchY;
        this.mProgress = progress;
        this.mSwipeEdge = swipeEdge;
    }

    public float getProgress() {
        return this.mProgress;
    }

    public float getTouchX() {
        return this.mTouchX;
    }

    public float getTouchY() {
        return this.mTouchY;
    }

    public int getSwipeEdge() {
        return this.mSwipeEdge;
    }

    public String toString() {
        return "BackEvent{mTouchX=" + this.mTouchX + ", mTouchY=" + this.mTouchY + ", mProgress=" + this.mProgress + ", mSwipeEdge" + this.mSwipeEdge + "}";
    }
}
