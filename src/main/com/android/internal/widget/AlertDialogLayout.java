package com.android.internal.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.internal.C4057R;
/* loaded from: classes5.dex */
public class AlertDialogLayout extends LinearLayout {
    public AlertDialogLayout(Context context) {
        super(context);
    }

    public AlertDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlertDialogLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AlertDialogLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!tryOnMeasure(widthMeasureSpec, heightMeasureSpec)) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean tryOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childHeightSpec;
        View topPanel = null;
        View buttonPanel = null;
        View middlePanel = null;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int id = child.getId();
                switch (id) {
                    case C4057R.C4059id.buttonPanel /* 16908841 */:
                        buttonPanel = child;
                        continue;
                    case C4057R.C4059id.contentPanel /* 16908901 */:
                    case C4057R.C4059id.customPanel /* 16908937 */:
                        if (middlePanel != null) {
                            return false;
                        }
                        middlePanel = child;
                        continue;
                    case C4057R.C4059id.topPanel /* 16909648 */:
                        topPanel = child;
                        continue;
                    default:
                        return false;
                }
            }
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int childState = 0;
        int usedHeight = getPaddingTop() + getPaddingBottom();
        if (topPanel != null) {
            topPanel.measure(widthMeasureSpec, 0);
            usedHeight += topPanel.getMeasuredHeight();
            childState = combineMeasuredStates(0, topPanel.getMeasuredState());
        }
        int buttonHeight = 0;
        int buttonWantsHeight = 0;
        if (buttonPanel != null) {
            buttonPanel.measure(widthMeasureSpec, 0);
            buttonHeight = resolveMinimumHeight(buttonPanel);
            buttonWantsHeight = buttonPanel.getMeasuredHeight() - buttonHeight;
            usedHeight += buttonHeight;
            childState = combineMeasuredStates(childState, buttonPanel.getMeasuredState());
        }
        int middleHeight = 0;
        if (middlePanel != null) {
            if (heightMode == 0) {
                childHeightSpec = 0;
            } else {
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(Math.max(0, heightSize - usedHeight), heightMode);
            }
            middlePanel.measure(widthMeasureSpec, childHeightSpec);
            middleHeight = middlePanel.getMeasuredHeight();
            usedHeight += middleHeight;
            childState = combineMeasuredStates(childState, middlePanel.getMeasuredState());
        }
        int remainingHeight = heightSize - usedHeight;
        if (buttonPanel != null) {
            int usedHeight2 = usedHeight - buttonHeight;
            int heightToGive = Math.min(remainingHeight, buttonWantsHeight);
            if (heightToGive > 0) {
                remainingHeight -= heightToGive;
                buttonHeight += heightToGive;
            }
            int remainingHeight2 = remainingHeight;
            int childHeightSpec2 = View.MeasureSpec.makeMeasureSpec(buttonHeight, 1073741824);
            buttonPanel.measure(widthMeasureSpec, childHeightSpec2);
            usedHeight = usedHeight2 + buttonPanel.getMeasuredHeight();
            childState = combineMeasuredStates(childState, buttonPanel.getMeasuredState());
            remainingHeight = remainingHeight2;
        }
        if (middlePanel != null && remainingHeight > 0) {
            int heightToGive2 = remainingHeight;
            int remainingHeight3 = remainingHeight - heightToGive2;
            int childHeightSpec3 = View.MeasureSpec.makeMeasureSpec(middleHeight + heightToGive2, heightMode);
            middlePanel.measure(widthMeasureSpec, childHeightSpec3);
            usedHeight = (usedHeight - middleHeight) + middlePanel.getMeasuredHeight();
            int childHeightSpec4 = middlePanel.getMeasuredState();
            childState = combineMeasuredStates(childState, childHeightSpec4);
            remainingHeight = remainingHeight3;
        }
        int maxWidth = 0;
        int remainingHeight4 = 0;
        while (remainingHeight4 < count) {
            View child2 = getChildAt(remainingHeight4);
            View buttonPanel2 = buttonPanel;
            View middlePanel2 = middlePanel;
            if (child2.getVisibility() != 8) {
                maxWidth = Math.max(maxWidth, child2.getMeasuredWidth());
            }
            remainingHeight4++;
            buttonPanel = buttonPanel2;
            middlePanel = middlePanel2;
        }
        int i2 = getPaddingLeft();
        int widthSizeAndState = resolveSizeAndState(maxWidth + i2 + getPaddingRight(), widthMeasureSpec, childState);
        int heightSizeAndState = resolveSizeAndState(usedHeight, heightMeasureSpec, 0);
        setMeasuredDimension(widthSizeAndState, heightSizeAndState);
        if (widthMode != 1073741824) {
            forceUniformWidth(count, heightMeasureSpec);
            return true;
        }
        return true;
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                if (lp.width == -1) {
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    private int resolveMinimumHeight(View v) {
        int minHeight = v.getMinimumHeight();
        if (minHeight > 0) {
            return minHeight;
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            if (vg.getChildCount() == 1) {
                return resolveMinimumHeight(vg.getChildAt(0));
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childTop;
        int i;
        int layoutGravity;
        int childLeft;
        AlertDialogLayout alertDialogLayout = this;
        int paddingLeft = alertDialogLayout.mPaddingLeft;
        int width = right - left;
        int childRight = width - alertDialogLayout.mPaddingRight;
        int childSpace = (width - paddingLeft) - alertDialogLayout.mPaddingRight;
        int totalLength = getMeasuredHeight();
        int count = getChildCount();
        int gravity = getGravity();
        int majorGravity = gravity & 112;
        int minorGravity = gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        switch (majorGravity) {
            case 16:
                int childTop2 = alertDialogLayout.mPaddingTop;
                childTop = childTop2 + (((bottom - top) - totalLength) / 2);
                break;
            case 80:
                int childTop3 = alertDialogLayout.mPaddingTop;
                childTop = ((childTop3 + bottom) - top) - totalLength;
                break;
            default:
                childTop = alertDialogLayout.mPaddingTop;
                break;
        }
        Drawable dividerDrawable = getDividerDrawable();
        int dividerHeight = dividerDrawable == null ? 0 : dividerDrawable.getIntrinsicHeight();
        int i2 = 0;
        while (i2 < count) {
            View child = alertDialogLayout.getChildAt(i2);
            if (child == null || child.getVisibility() == 8) {
                i = i2;
            } else {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                int layoutGravity2 = lp.gravity;
                if (layoutGravity2 >= 0) {
                    layoutGravity = layoutGravity2;
                } else {
                    layoutGravity = minorGravity;
                }
                int layoutDirection = getLayoutDirection();
                int absoluteGravity = Gravity.getAbsoluteGravity(layoutGravity, layoutDirection);
                switch (absoluteGravity & 7) {
                    case 1:
                        int childLeft2 = ((((childSpace - childWidth) / 2) + paddingLeft) + lp.leftMargin) - lp.rightMargin;
                        childLeft = childLeft2;
                        break;
                    case 5:
                        int childLeft3 = childRight - childWidth;
                        int layoutDirection2 = lp.rightMargin;
                        childLeft = childLeft3 - layoutDirection2;
                        break;
                    default:
                        childLeft = lp.leftMargin + paddingLeft;
                        break;
                }
                if (alertDialogLayout.hasDividerBeforeChildAt(i2)) {
                    childTop += dividerHeight;
                }
                int childTop4 = childTop + lp.topMargin;
                i = i2;
                setChildFrame(child, childLeft, childTop4, childWidth, childHeight);
                childTop = childTop4 + childHeight + lp.bottomMargin;
            }
            i2 = i + 1;
            alertDialogLayout = this;
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }
}
