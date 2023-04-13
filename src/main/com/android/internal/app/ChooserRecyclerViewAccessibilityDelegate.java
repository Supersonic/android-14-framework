package com.android.internal.app;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.widget.RecyclerView;
import com.android.internal.widget.RecyclerViewAccessibilityDelegate;
/* loaded from: classes4.dex */
class ChooserRecyclerViewAccessibilityDelegate extends RecyclerViewAccessibilityDelegate {
    private final int[] mConsumed;
    private final Rect mTempRect;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChooserRecyclerViewAccessibilityDelegate(RecyclerView recyclerView) {
        super(recyclerView);
        this.mTempRect = new Rect();
        this.mConsumed = new int[2];
    }

    @Override // android.view.View.AccessibilityDelegate
    public boolean onRequestSendAccessibilityEvent(ViewGroup host, View view, AccessibilityEvent event) {
        boolean result = super.onRequestSendAccessibilityEvent(host, view, event);
        if (result && event.getEventType() == 32768) {
            ensureViewOnScreenVisibility((RecyclerView) host, view);
        }
        return result;
    }

    private void ensureViewOnScreenVisibility(RecyclerView recyclerView, View view) {
        View child = recyclerView.findContainingItemView(view);
        if (child == null) {
            return;
        }
        recyclerView.getBoundsOnScreen(this.mTempRect, true);
        int recyclerOnScreenTop = this.mTempRect.top;
        int recyclerOnScreenBottom = this.mTempRect.bottom;
        child.getBoundsOnScreen(this.mTempRect);
        int dy = 0;
        if (this.mTempRect.top < recyclerOnScreenTop) {
            dy = this.mTempRect.bottom - recyclerOnScreenBottom;
        } else if (this.mTempRect.bottom > recyclerOnScreenBottom) {
            dy = this.mTempRect.top - recyclerOnScreenTop;
        }
        nestedVerticalScrollBy(recyclerView, dy);
    }

    private void nestedVerticalScrollBy(RecyclerView recyclerView, int dy) {
        if (dy == 0) {
            return;
        }
        recyclerView.startNestedScroll(2);
        if (recyclerView.dispatchNestedPreScroll(0, dy, this.mConsumed, null)) {
            dy -= this.mConsumed[1];
        }
        recyclerView.scrollBy(0, dy);
        recyclerView.stopNestedScroll();
    }
}
