package com.android.internal.view;

import android.graphics.Rect;
import android.p008os.CancellationSignal;
import android.view.View;
import android.view.ViewGroup;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public interface ScrollCaptureViewHelper<V extends View> {
    public static final int DOWN = 1;

    /* renamed from: UP */
    public static final int f2190UP = -1;

    boolean onAcceptSession(V v);

    void onPrepareForEnd(V v);

    void onPrepareForStart(V v, Rect rect);

    void onScrollRequested(V v, Rect rect, Rect rect2, CancellationSignal cancellationSignal, Consumer<ScrollResult> consumer);

    /* loaded from: classes2.dex */
    public static class ScrollResult {
        public Rect availableArea;
        public Rect requestedArea;
        public int scrollDelta;

        public String toString() {
            return "ScrollResult{requestedArea=" + this.requestedArea + ", availableArea=" + this.availableArea + ", scrollDelta=" + this.scrollDelta + '}';
        }
    }

    default Rect onComputeScrollBounds(V view) {
        Rect bounds = new Rect(0, 0, view.getWidth(), view.getHeight());
        if ((view instanceof ViewGroup) && ((ViewGroup) view).getClipToPadding()) {
            bounds.inset(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
        return bounds;
    }
}
