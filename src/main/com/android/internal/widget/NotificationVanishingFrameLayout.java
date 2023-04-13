package com.android.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class NotificationVanishingFrameLayout extends FrameLayout {
    public NotificationVanishingFrameLayout(Context context) {
        this(context, null, 0, 0);
    }

    public NotificationVanishingFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public NotificationVanishingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationVanishingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (allChildrenGone()) {
            int zeroSpec = View.MeasureSpec.makeMeasureSpec(0, 1073741824);
            super.onMeasure(zeroSpec, zeroSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean allChildrenGone() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                return false;
            }
        }
        return true;
    }
}
