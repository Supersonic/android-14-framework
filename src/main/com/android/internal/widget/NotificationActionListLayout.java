package com.android.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.widget.NotificationActionListLayout;
import java.util.ArrayList;
import java.util.Comparator;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class NotificationActionListLayout extends LinearLayout {
    public static final Comparator<TextViewInfo> MEASURE_ORDER_COMPARATOR = new Comparator() { // from class: com.android.internal.widget.NotificationActionListLayout$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return NotificationActionListLayout.lambda$static$0((NotificationActionListLayout.TextViewInfo) obj, (NotificationActionListLayout.TextViewInfo) obj2);
        }
    };
    private int mCollapsibleIndentDimen;
    private int mDefaultPaddingBottom;
    private int mDefaultPaddingTop;
    private int mEmphasizedHeight;
    private boolean mEmphasizedMode;
    private int mExtraStartPadding;
    private final int mGravity;
    private ArrayList<View> mMeasureOrderOther;
    private ArrayList<TextViewInfo> mMeasureOrderTextViews;
    int mNumNotGoneChildren;
    int mNumPriorityChildren;
    private int mRegularHeight;
    private int mTotalWidth;

    public NotificationActionListLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationActionListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationActionListLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTotalWidth = 0;
        this.mExtraStartPadding = 0;
        this.mMeasureOrderTextViews = new ArrayList<>();
        this.mMeasureOrderOther = new ArrayList<>();
        this.mCollapsibleIndentDimen = C4057R.dimen.notification_actions_padding_start;
        int[] attrIds = {16842927};
        TypedArray ta = context.obtainStyledAttributes(attrs, attrIds, defStyleAttr, defStyleRes);
        this.mGravity = ta.getInt(0, 0);
        ta.recycle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isPriority(View actionView) {
        return (actionView instanceof EmphasizedNotificationButton) && ((EmphasizedNotificationButton) actionView).isPriority();
    }

    private void countAndRebuildMeasureOrder() {
        int numChildren = getChildCount();
        int textViews = 0;
        int otherViews = 0;
        this.mNumNotGoneChildren = 0;
        this.mNumPriorityChildren = 0;
        for (int i = 0; i < numChildren; i++) {
            View c = getChildAt(i);
            if (c instanceof TextView) {
                textViews++;
            } else {
                otherViews++;
            }
            if (c.getVisibility() != 8) {
                this.mNumNotGoneChildren++;
                if (isPriority(c)) {
                    this.mNumPriorityChildren++;
                }
            }
        }
        boolean needRebuild = false;
        needRebuild = (textViews == this.mMeasureOrderTextViews.size() && otherViews == this.mMeasureOrderOther.size()) ? true : true;
        if (!needRebuild) {
            int size = this.mMeasureOrderTextViews.size();
            int i2 = 0;
            while (true) {
                if (i2 < size) {
                    if (!this.mMeasureOrderTextViews.get(i2).needsRebuild()) {
                        i2++;
                    } else {
                        needRebuild = true;
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if (needRebuild) {
            rebuildMeasureOrder(textViews, otherViews);
        }
    }

    private int measureAndGetUsedWidth(int widthMeasureSpec, int heightMeasureSpec, int innerWidth, boolean collapsePriorityActions) {
        View c;
        boolean isPriority;
        int usedWidthForChild;
        int maxPriorityWidth;
        int numChildren;
        int numChildren2 = getChildCount();
        boolean constrained = View.MeasureSpec.getMode(widthMeasureSpec) != 0;
        int otherSize = this.mMeasureOrderOther.size();
        int maxPriorityWidth2 = 0;
        int usedWidth = 0;
        int measuredChildren = 0;
        int measuredPriorityChildren = 0;
        int i = 0;
        while (i < numChildren2) {
            if (i < otherSize) {
                View c2 = this.mMeasureOrderOther.get(i);
                c = c2;
                isPriority = false;
            } else {
                TextViewInfo info = this.mMeasureOrderTextViews.get(i - otherSize);
                c = info.mTextView;
                isPriority = info.mIsPriority;
            }
            if (c.getVisibility() == 8) {
                numChildren = numChildren2;
            } else {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) c.getLayoutParams();
                int usedWidthForChild2 = usedWidth;
                if (!constrained) {
                    usedWidthForChild = usedWidthForChild2;
                    maxPriorityWidth = maxPriorityWidth2;
                } else {
                    int availableWidth = innerWidth - usedWidth;
                    int unmeasuredChildren = this.mNumNotGoneChildren - measuredChildren;
                    int maxWidthForChild = availableWidth / unmeasuredChildren;
                    if (isPriority && collapsePriorityActions) {
                        if (maxPriorityWidth2 == 0) {
                            maxPriorityWidth2 = getResources().getDimensionPixelSize(C4057R.dimen.notification_actions_collapsed_priority_width);
                        }
                        int usedWidthForChild3 = lp.leftMargin;
                        maxWidthForChild = usedWidthForChild3 + maxPriorityWidth2 + lp.rightMargin;
                    } else if (isPriority) {
                        int unmeasuredPriorityChildren = this.mNumPriorityChildren - measuredPriorityChildren;
                        int unmeasuredOtherChildren = unmeasuredChildren - unmeasuredPriorityChildren;
                        int widthReservedForOtherChildren = (innerWidth * unmeasuredOtherChildren) / 4;
                        int widthAvailableForPriority = availableWidth - widthReservedForOtherChildren;
                        maxWidthForChild = widthAvailableForPriority / unmeasuredPriorityChildren;
                    }
                    usedWidthForChild = innerWidth - maxWidthForChild;
                    maxPriorityWidth = maxPriorityWidth2;
                }
                numChildren = numChildren2;
                measureChildWithMargins(c, widthMeasureSpec, usedWidthForChild, heightMeasureSpec, 0);
                usedWidth += c.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
                measuredChildren++;
                if (!isPriority) {
                    maxPriorityWidth2 = maxPriorityWidth;
                } else {
                    measuredPriorityChildren++;
                    maxPriorityWidth2 = maxPriorityWidth;
                }
            }
            i++;
            numChildren2 = numChildren;
        }
        int collapsibleIndent = this.mCollapsibleIndentDimen == 0 ? 0 : getResources().getDimensionPixelOffset(this.mCollapsibleIndentDimen);
        if (innerWidth - usedWidth <= collapsibleIndent) {
            this.mExtraStartPadding = 0;
        } else {
            this.mExtraStartPadding = collapsibleIndent;
        }
        return usedWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        countAndRebuildMeasureOrder();
        int innerWidth = (View.MeasureSpec.getSize(widthMeasureSpec) - this.mPaddingLeft) - this.mPaddingRight;
        int usedWidth = measureAndGetUsedWidth(widthMeasureSpec, heightMeasureSpec, innerWidth, false);
        if (this.mNumPriorityChildren != 0 && usedWidth >= innerWidth) {
            usedWidth = measureAndGetUsedWidth(widthMeasureSpec, heightMeasureSpec, innerWidth, true);
        }
        this.mTotalWidth = this.mPaddingRight + usedWidth + this.mPaddingLeft + this.mExtraStartPadding;
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    private void rebuildMeasureOrder(int capacityText, int capacityOther) {
        clearMeasureOrder();
        this.mMeasureOrderTextViews.ensureCapacity(capacityText);
        this.mMeasureOrderOther.ensureCapacity(capacityOther);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View c = getChildAt(i);
            if ((c instanceof TextView) && ((TextView) c).getText().length() > 0) {
                this.mMeasureOrderTextViews.add(new TextViewInfo((TextView) c));
            } else {
                this.mMeasureOrderOther.add(c);
            }
        }
        this.mMeasureOrderTextViews.sort(MEASURE_ORDER_COMPARATOR);
    }

    private void clearMeasureOrder() {
        this.mMeasureOrderOther.clear();
        this.mMeasureOrderTextViews.clear();
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        clearMeasureOrder();
        if (child.getBackground() instanceof RippleDrawable) {
            ((RippleDrawable) child.getBackground()).setForceSoftware(true);
        }
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        clearMeasureOrder();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft;
        boolean isLayoutRtl;
        int paddingTop;
        NotificationActionListLayout notificationActionListLayout = this;
        boolean isLayoutRtl2 = isLayoutRtl();
        int paddingTop2 = notificationActionListLayout.mPaddingTop;
        boolean centerAligned = (notificationActionListLayout.mGravity & 1) != 0;
        if (centerAligned) {
            childLeft = ((notificationActionListLayout.mPaddingLeft + left) + ((right - left) / 2)) - (notificationActionListLayout.mTotalWidth / 2);
        } else {
            int childLeft2 = notificationActionListLayout.mPaddingLeft;
            int absoluteGravity = Gravity.getAbsoluteGravity(Gravity.START, getLayoutDirection());
            if (absoluteGravity == 5) {
                childLeft = childLeft2 + ((right - left) - notificationActionListLayout.mTotalWidth);
            } else {
                childLeft = childLeft2 + notificationActionListLayout.mExtraStartPadding;
            }
        }
        int absoluteGravity2 = bottom - top;
        int innerHeight = (absoluteGravity2 - paddingTop2) - notificationActionListLayout.mPaddingBottom;
        int count = getChildCount();
        int start = 0;
        int dir = 1;
        if (isLayoutRtl2) {
            start = count - 1;
            dir = -1;
        }
        int i = 0;
        while (i < count) {
            int childIndex = (dir * i) + start;
            View child = notificationActionListLayout.getChildAt(childIndex);
            if (child.getVisibility() == 8) {
                isLayoutRtl = isLayoutRtl2;
                paddingTop = paddingTop2;
            } else {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                int childTop = ((paddingTop2 + ((innerHeight - childHeight) / 2)) + lp.topMargin) - lp.bottomMargin;
                isLayoutRtl = isLayoutRtl2;
                int childLeft3 = childLeft + lp.leftMargin;
                paddingTop = paddingTop2;
                child.layout(childLeft3, childTop, childLeft3 + childWidth, childTop + childHeight);
                childLeft = childLeft3 + lp.rightMargin + childWidth;
            }
            i++;
            notificationActionListLayout = this;
            isLayoutRtl2 = isLayoutRtl;
            paddingTop2 = paddingTop;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDefaultPaddingBottom = getPaddingBottom();
        this.mDefaultPaddingTop = getPaddingTop();
        updateHeights();
    }

    private void updateHeights() {
        int paddingTop = getResources().getDimensionPixelSize(C4057R.dimen.notification_content_margin);
        int paddingBottom = getResources().getDimensionPixelSize(C4057R.dimen.notification_content_margin_end);
        this.mEmphasizedHeight = paddingBottom + paddingTop + getResources().getDimensionPixelSize(C4057R.dimen.notification_action_emphasized_height);
        this.mRegularHeight = getResources().getDimensionPixelSize(C4057R.dimen.notification_action_list_height);
    }

    @RemotableViewMethod
    public void setCollapsibleIndentDimen(int collapsibleIndentDimen) {
        if (this.mCollapsibleIndentDimen != collapsibleIndentDimen) {
            this.mCollapsibleIndentDimen = collapsibleIndentDimen;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setEmphasizedMode(boolean emphasizedMode) {
        int height;
        this.mEmphasizedMode = emphasizedMode;
        if (emphasizedMode) {
            int paddingTop = getResources().getDimensionPixelSize(C4057R.dimen.notification_content_margin);
            int paddingBottom = getResources().getDimensionPixelSize(C4057R.dimen.notification_content_margin_end);
            height = this.mEmphasizedHeight;
            int buttonPaddingInternal = getResources().getDimensionPixelSize(C4057R.dimen.button_inset_vertical_material);
            setPaddingRelative(getPaddingStart(), paddingTop - buttonPaddingInternal, getPaddingEnd(), paddingBottom - buttonPaddingInternal);
        } else {
            setPaddingRelative(getPaddingStart(), this.mDefaultPaddingTop, getPaddingEnd(), this.mDefaultPaddingBottom);
            height = this.mRegularHeight;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    public int getExtraMeasureHeight() {
        if (this.mEmphasizedMode) {
            return this.mEmphasizedHeight - this.mRegularHeight;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$static$0(TextViewInfo a, TextViewInfo b) {
        int priorityComparison = -Boolean.compare(a.mIsPriority, b.mIsPriority);
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        return Integer.compare(a.mTextLength, b.mTextLength);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static final class TextViewInfo {
        final boolean mIsPriority;
        final int mTextLength;
        final TextView mTextView;

        TextViewInfo(TextView textView) {
            this.mIsPriority = NotificationActionListLayout.isPriority(textView);
            this.mTextLength = textView.getText().length();
            this.mTextView = textView;
        }

        boolean needsRebuild() {
            return (this.mTextView.getText().length() == this.mTextLength && NotificationActionListLayout.isPriority(this.mTextView) == this.mIsPriority) ? false : true;
        }
    }
}
