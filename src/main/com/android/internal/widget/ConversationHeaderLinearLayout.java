package com.android.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class ConversationHeaderLinearLayout extends LinearLayout {
    public ConversationHeaderLinearLayout(Context context) {
        super(context);
    }

    public ConversationHeaderLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConversationHeaderLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int calculateTotalChildLength() {
        int count = getChildCount();
        int totalLength = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                totalLength += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
        }
        int i2 = getPaddingLeft();
        return i2 + totalLength + getPaddingRight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int containerWidth = getMeasuredWidth();
        int contentsWidth = calculateTotalChildLength();
        int excessContents = contentsWidth - containerWidth;
        if (excessContents <= 0) {
            return;
        }
        int count = getChildCount();
        float remainingWeight = 0.0f;
        List<ViewInfo> visibleChildrenToShorten = null;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != 8) {
                float weight = ((LinearLayout.LayoutParams) child.getLayoutParams()).weight;
                if (weight != 0.0f && child.getMeasuredWidth() != 0) {
                    if (visibleChildrenToShorten == null) {
                        visibleChildrenToShorten = new ArrayList<>(count);
                    }
                    visibleChildrenToShorten.add(new ViewInfo(child));
                    remainingWeight += Math.max(0.0f, weight);
                }
            }
        }
        if (visibleChildrenToShorten == null || visibleChildrenToShorten.isEmpty()) {
            return;
        }
        balanceViewWidths(visibleChildrenToShorten, remainingWeight, excessContents);
        remeasureChangedChildren(visibleChildrenToShorten);
    }

    private void remeasureChangedChildren(List<ViewInfo> childrenInfo) {
        for (ViewInfo info : childrenInfo) {
            if (info.mWidth != info.mStartWidth) {
                int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.max(0, info.mWidth), 1073741824);
                int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(info.mView.getMeasuredHeight(), 1073741824);
                info.mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    void balanceViewWidths(List<ViewInfo> viewInfos, float weightSum, int excessContents) {
        boolean performAnotherPass = true;
        while (performAnotherPass && excessContents > 0 && weightSum > 0.0f) {
            int excessRemovedDuringThisPass = 0;
            float weightSumForNextPass = 0.0f;
            performAnotherPass = false;
            for (ViewInfo info : viewInfos) {
                if (info.mWeight > 0.0f && info.mWidth > 0) {
                    int newWidth = (int) (info.mWidth - (excessContents * (info.mWeight / weightSum)));
                    if (newWidth < 0) {
                        newWidth = 0;
                        performAnotherPass = true;
                    }
                    excessRemovedDuringThisPass += info.mWidth - newWidth;
                    info.mWidth = newWidth;
                    if (info.mWidth > 0) {
                        weightSumForNextPass += info.mWeight;
                    }
                }
            }
            excessContents -= excessRemovedDuringThisPass;
            weightSum = weightSumForNextPass;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class ViewInfo {
        final int mStartWidth;
        final View mView;
        final float mWeight;
        int mWidth;

        ViewInfo(View view) {
            this.mView = view;
            this.mWeight = ((LinearLayout.LayoutParams) view.getLayoutParams()).weight;
            int measuredWidth = view.getMeasuredWidth();
            this.mWidth = measuredWidth;
            this.mStartWidth = measuredWidth;
        }
    }
}
