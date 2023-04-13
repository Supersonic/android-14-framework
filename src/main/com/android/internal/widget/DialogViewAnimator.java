package com.android.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ViewAnimator;
import java.util.ArrayList;
/* loaded from: classes5.dex */
public class DialogViewAnimator extends ViewAnimator {
    private final ArrayList<View> mMatchParentChildren;

    public DialogViewAnimator(Context context) {
        super(context);
        this.mMatchParentChildren = new ArrayList<>(1);
    }

    public DialogViewAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMatchParentChildren = new ArrayList<>(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight;
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        int i;
        int i2 = 1073741824;
        boolean measureMatchParentChildren = (View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824 && View.MeasureSpec.getMode(heightMeasureSpec) == 1073741824) ? false : true;
        int count = getChildCount();
        int maxHeight2 = 0;
        int maxWidth = 0;
        int childState = 0;
        int i3 = 0;
        while (true) {
            maxHeight = -1;
            if (i3 >= count) {
                break;
            }
            View child = getChildAt(i3);
            if (getMeasureAllChildren() || child.getVisibility() != 8) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                boolean matchWidth = lp.width == -1;
                boolean matchHeight = lp.height == -1;
                if (measureMatchParentChildren && (matchWidth || matchHeight)) {
                    this.mMatchParentChildren.add(child);
                }
                i = i3;
                int childState2 = childState;
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int state = 0;
                if (measureMatchParentChildren && !matchWidth) {
                    maxWidth = Math.max(maxWidth, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                    state = 0 | (child.getMeasuredWidthAndState() & (-16777216));
                }
                if (measureMatchParentChildren && !matchHeight) {
                    maxHeight2 = Math.max(maxHeight2, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                    state |= (child.getMeasuredHeightAndState() >> 16) & (-256);
                }
                childState = combineMeasuredStates(childState2, state);
            } else {
                i = i3;
            }
            i3 = i + 1;
        }
        int childState3 = childState;
        int maxWidth2 = maxWidth + getPaddingLeft() + getPaddingRight();
        int maxHeight3 = Math.max(maxHeight2 + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight());
        int maxWidth3 = Math.max(maxWidth2, getSuggestedMinimumWidth());
        Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight3 = Math.max(maxHeight3, drawable.getMinimumHeight());
            maxWidth3 = Math.max(maxWidth3, drawable.getMinimumWidth());
        }
        setMeasuredDimension(resolveSizeAndState(maxWidth3, widthMeasureSpec, childState3), resolveSizeAndState(maxHeight3, heightMeasureSpec, childState3 << 16));
        int matchCount = this.mMatchParentChildren.size();
        int i4 = 0;
        while (i4 < matchCount) {
            View child2 = this.mMatchParentChildren.get(i4);
            ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) child2.getLayoutParams();
            if (lp2.width == maxHeight) {
                childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec((((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - lp2.leftMargin) - lp2.rightMargin, i2);
            } else {
                childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight() + lp2.leftMargin + lp2.rightMargin, lp2.width);
            }
            if (lp2.height == -1) {
                childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec((((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom()) - lp2.topMargin) - lp2.bottomMargin, 1073741824);
            } else {
                childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom() + lp2.topMargin + lp2.bottomMargin, lp2.height);
            }
            child2.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            i4++;
            maxHeight = -1;
            i2 = 1073741824;
        }
        this.mMatchParentChildren.clear();
    }
}
