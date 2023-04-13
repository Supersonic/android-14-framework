package com.android.internal.view;

import android.graphics.Rect;
import android.p008os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.view.ScrollCaptureViewHelper;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class RecyclerViewCaptureHelper implements ScrollCaptureViewHelper<ViewGroup> {
    private static final String TAG = "RVCaptureHelper";
    private int mOverScrollMode;
    private boolean mScrollBarWasEnabled;
    private int mScrollDelta;

    @Override // com.android.internal.view.ScrollCaptureViewHelper
    public /* bridge */ /* synthetic */ void onScrollRequested(ViewGroup viewGroup, Rect rect, Rect rect2, CancellationSignal cancellationSignal, Consumer consumer) {
        onScrollRequested2(viewGroup, rect, rect2, cancellationSignal, (Consumer<ScrollCaptureViewHelper.ScrollResult>) consumer);
    }

    @Override // com.android.internal.view.ScrollCaptureViewHelper
    public boolean onAcceptSession(ViewGroup view) {
        return view.isVisibleToUser() && (view.canScrollVertically(-1) || view.canScrollVertically(1));
    }

    @Override // com.android.internal.view.ScrollCaptureViewHelper
    public void onPrepareForStart(ViewGroup view, Rect scrollBounds) {
        this.mScrollDelta = 0;
        this.mOverScrollMode = view.getOverScrollMode();
        view.setOverScrollMode(2);
        this.mScrollBarWasEnabled = view.isVerticalScrollBarEnabled();
        view.setVerticalScrollBarEnabled(false);
    }

    /* renamed from: onScrollRequested  reason: avoid collision after fix types in other method */
    public void onScrollRequested2(ViewGroup recyclerView, Rect scrollBounds, Rect requestRect, CancellationSignal signal, Consumer<ScrollCaptureViewHelper.ScrollResult> resultConsumer) {
        ScrollCaptureViewHelper.ScrollResult result = new ScrollCaptureViewHelper.ScrollResult();
        result.requestedArea = new Rect(requestRect);
        result.scrollDelta = this.mScrollDelta;
        result.availableArea = new Rect();
        if (!recyclerView.isVisibleToUser() || recyclerView.getChildCount() == 0) {
            Log.m104w(TAG, "recyclerView is empty or not visible, cannot continue");
            resultConsumer.accept(result);
            return;
        }
        Rect requestedContainerBounds = new Rect(requestRect);
        requestedContainerBounds.offset(0, -this.mScrollDelta);
        requestedContainerBounds.offset(scrollBounds.left, scrollBounds.top);
        View anchor = findChildNearestTarget(recyclerView, requestedContainerBounds);
        if (anchor == null) {
            Log.m104w(TAG, "Failed to locate anchor view");
            resultConsumer.accept(result);
            return;
        }
        Rect requestedContentBounds = new Rect(requestedContainerBounds);
        recyclerView.offsetRectIntoDescendantCoords(anchor, requestedContentBounds);
        int prevAnchorTop = anchor.getTop();
        Rect input = new Rect(requestedContentBounds);
        int remainingHeight = ((recyclerView.getHeight() - recyclerView.getPaddingTop()) - recyclerView.getPaddingBottom()) - input.height();
        if (remainingHeight > 0) {
            input.inset(0, (-remainingHeight) / 2);
        }
        if (recyclerView.requestChildRectangleOnScreen(anchor, input, true)) {
            int scrolled = prevAnchorTop - anchor.getTop();
            int i = this.mScrollDelta + scrolled;
            this.mScrollDelta = i;
            result.scrollDelta = i;
        }
        requestedContainerBounds.set(requestedContentBounds);
        recyclerView.offsetDescendantRectToMyCoords(anchor, requestedContainerBounds);
        Rect recyclerLocalVisible = new Rect(scrollBounds);
        recyclerView.getLocalVisibleRect(recyclerLocalVisible);
        if (!requestedContainerBounds.intersect(recyclerLocalVisible)) {
            resultConsumer.accept(result);
            return;
        }
        Rect available = new Rect(requestedContainerBounds);
        available.offset(-scrollBounds.left, -scrollBounds.top);
        available.offset(0, this.mScrollDelta);
        result.availableArea = available;
        resultConsumer.accept(result);
    }

    static View findChildNearestTarget(ViewGroup parent, Rect targetRect) {
        View selected = null;
        int minCenterDistance = Integer.MAX_VALUE;
        int preferredDistance = (int) (targetRect.height() * 0.25f);
        Rect parentLocalVis = new Rect();
        parent.getLocalVisibleRect(parentLocalVis);
        Rect frame = new Rect();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            child.getHitRect(frame);
            if (child.getVisibility() == 0) {
                int centerDistance = Math.abs(targetRect.centerY() - frame.centerY());
                if (centerDistance < minCenterDistance) {
                    minCenterDistance = centerDistance;
                    selected = child;
                } else if (frame.intersect(targetRect) && frame.height() > preferredDistance) {
                    selected = child;
                }
            }
        }
        return selected;
    }

    @Override // com.android.internal.view.ScrollCaptureViewHelper
    public void onPrepareForEnd(ViewGroup view) {
        view.scrollBy(0, -this.mScrollDelta);
        view.setOverScrollMode(this.mOverScrollMode);
        view.setVerticalScrollBarEnabled(this.mScrollBarWasEnabled);
    }
}
