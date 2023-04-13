package com.android.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class RemeasuringLinearLayout extends LinearLayout {
    private ArrayList<View> mMatchParentViews;

    public RemeasuringLinearLayout(Context context) {
        super(context);
        this.mMatchParentViews = new ArrayList<>();
    }

    public RemeasuringLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMatchParentViews = new ArrayList<>();
    }

    public RemeasuringLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMatchParentViews = new ArrayList<>();
    }

    public RemeasuringLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mMatchParentViews = new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        int height = 0;
        boolean isVertical = getOrientation() == 1;
        boolean isWrapContent = getLayoutParams().height == -2;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                if (!isWrapContent || lp.height != -1 || isVertical) {
                    int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                    height = Math.max(height, isVertical ? height + childHeight : childHeight);
                } else {
                    this.mMatchParentViews.add(child);
                }
            }
        }
        if (this.mMatchParentViews.size() > 0) {
            int exactHeightSpec = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
            Iterator<View> it = this.mMatchParentViews.iterator();
            while (it.hasNext()) {
                View child2 = it.next();
                child2.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingStart() + getPaddingEnd(), child2.getLayoutParams().width), exactHeightSpec);
            }
        }
        this.mMatchParentViews.clear();
        setMeasuredDimension(getMeasuredWidth(), height);
    }
}
