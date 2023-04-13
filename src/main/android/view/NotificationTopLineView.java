package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import java.util.HashSet;
import java.util.Set;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class NotificationTopLineView extends ViewGroup {
    private View mAppName;
    private final int mChildHideWidth;
    private final int mChildMinWidth;
    private View mFeedbackIcon;
    private View.OnClickListener mFeedbackListener;
    private final int mGravityY;
    private View mHeaderText;
    private View mHeaderTextDivider;
    private int mHeaderTextMarginEnd;
    private int mMaxAscent;
    private int mMaxDescent;
    private final OverflowAdjuster mOverflowAdjuster;
    private View mSecondaryHeaderText;
    private View mSecondaryHeaderTextDivider;
    private View mTitle;
    private HeaderTouchListener mTouchListener;
    private Set<View> mViewsToDisappear;

    public NotificationTopLineView(Context context) {
        this(context, null);
    }

    public NotificationTopLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationTopLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationTopLineView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mOverflowAdjuster = new OverflowAdjuster();
        this.mTouchListener = new HeaderTouchListener();
        this.mViewsToDisappear = new HashSet();
        Resources res = getResources();
        this.mChildMinWidth = res.getDimensionPixelSize(C4057R.dimen.notification_header_shrink_min_width);
        this.mChildHideWidth = res.getDimensionPixelSize(C4057R.dimen.notification_header_shrink_hide_width);
        int[] attrIds = {16842927};
        TypedArray ta = context.obtainStyledAttributes(attrs, attrIds, defStyleAttr, defStyleRes);
        int gravity = ta.getInt(0, 0);
        ta.recycle();
        if ((gravity & 80) == 80) {
            this.mGravityY = 80;
        } else if ((gravity & 48) == 48) {
            this.mGravityY = 48;
        } else {
            this.mGravityY = 16;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mAppName = findViewById(C4057R.C4059id.app_name_text);
        this.mTitle = findViewById(16908310);
        this.mHeaderText = findViewById(C4057R.C4059id.header_text);
        this.mHeaderTextDivider = findViewById(C4057R.C4059id.header_text_divider);
        this.mSecondaryHeaderText = findViewById(C4057R.C4059id.header_text_secondary);
        this.mSecondaryHeaderTextDivider = findViewById(C4057R.C4059id.header_text_secondary_divider);
        this.mFeedbackIcon = findViewById(C4057R.C4059id.feedback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int givenHeight;
        int givenWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int givenHeight2 = View.MeasureSpec.getSize(heightMeasureSpec);
        boolean wrapHeight = View.MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE;
        int wrapContentWidthSpec = View.MeasureSpec.makeMeasureSpec(givenWidth, Integer.MIN_VALUE);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(givenHeight2, Integer.MIN_VALUE);
        int totalWidth = getPaddingStart();
        int maxChildHeight = -1;
        int i = -1;
        this.mMaxAscent = -1;
        this.mMaxDescent = -1;
        int i2 = 0;
        while (i2 < getChildCount()) {
            View child = getChildAt(i2);
            if (child.getVisibility() == 8) {
                givenHeight = givenHeight2;
            } else {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                int childWidthSpec = getChildMeasureSpec(wrapContentWidthSpec, lp.leftMargin + lp.rightMargin, lp.width);
                int childHeightSpec = getChildMeasureSpec(heightSpec, lp.topMargin + lp.bottomMargin, lp.height);
                child.measure(childWidthSpec, childHeightSpec);
                totalWidth += lp.leftMargin + lp.rightMargin + child.getMeasuredWidth();
                int childBaseline = child.getBaseline();
                int childHeight = child.getMeasuredHeight();
                if (childBaseline != i) {
                    this.mMaxAscent = Math.max(this.mMaxAscent, childBaseline);
                    givenHeight = givenHeight2;
                    this.mMaxDescent = Math.max(this.mMaxDescent, childHeight - childBaseline);
                } else {
                    givenHeight = givenHeight2;
                }
                maxChildHeight = Math.max(maxChildHeight, childHeight);
            }
            i2++;
            givenHeight2 = givenHeight;
            i = -1;
        }
        int givenHeight3 = givenHeight2;
        this.mViewsToDisappear.clear();
        int endMargin = Math.max(this.mHeaderTextMarginEnd, getPaddingEnd());
        if (totalWidth > givenWidth - endMargin) {
            int overFlow = (totalWidth - givenWidth) + endMargin;
            this.mOverflowAdjuster.resetForOverflow(overFlow, heightSpec).adjust(this.mAppName, null, this.mChildMinWidth).adjust(this.mHeaderText, this.mHeaderTextDivider, this.mChildMinWidth).adjust(this.mSecondaryHeaderText, this.mSecondaryHeaderTextDivider, 0).adjust(this.mTitle, null, this.mChildMinWidth).adjust(this.mHeaderText, this.mHeaderTextDivider, 0).adjust(this.mTitle, null, 0).finish();
        }
        setMeasuredDimension(givenWidth, wrapHeight ? maxChildHeight : givenHeight3);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount;
        int childTop;
        boolean isRtl = getLayoutDirection() == 1;
        int width = getWidth();
        int start = getPaddingStart();
        int ownHeight = b - t;
        int childSpace = (ownHeight - this.mPaddingTop) - this.mPaddingBottom;
        int i = this.mPaddingTop;
        int i2 = this.mMaxAscent;
        int baselineY = i + ((childSpace - (this.mMaxDescent + i2)) / 2) + i2;
        int i3 = 0;
        for (int childCount2 = getChildCount(); i3 < childCount2; childCount2 = childCount) {
            View child = getChildAt(i3);
            if (child.getVisibility() == 8) {
                childCount = childCount2;
            } else {
                int childHeight = child.getMeasuredHeight();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                int childBaseline = child.getBaseline();
                switch (this.mGravityY) {
                    case 16:
                        childCount = childCount2;
                        if (childBaseline != -1) {
                            if (childSpace - childHeight > 0) {
                                childTop = baselineY - childBaseline;
                                break;
                            } else {
                                childTop = this.mPaddingTop + ((childSpace - childHeight) / 2);
                                break;
                            }
                        } else {
                            childTop = ((this.mPaddingTop + ((childSpace - childHeight) / 2)) + params.topMargin) - params.bottomMargin;
                            break;
                        }
                    case 48:
                        childCount = childCount2;
                        int childCount3 = this.mPaddingTop;
                        childTop = childCount3 + params.topMargin;
                        if (childBaseline != -1) {
                            childTop += this.mMaxAscent - childBaseline;
                            break;
                        }
                        break;
                    case 80:
                        int childBottom = ownHeight - this.mPaddingBottom;
                        childTop = (childBottom - childHeight) - params.bottomMargin;
                        if (childBaseline == -1) {
                            childCount = childCount2;
                            break;
                        } else {
                            int descent = childHeight - childBaseline;
                            childCount = childCount2;
                            int childCount4 = this.mMaxDescent;
                            childTop -= childCount4 - descent;
                            break;
                        }
                    default:
                        childCount = childCount2;
                        childTop = this.mPaddingTop;
                        break;
                }
                if (this.mViewsToDisappear.contains(child)) {
                    child.layout(start, childTop, start, childTop + childHeight);
                } else {
                    int start2 = start + params.getMarginStart();
                    int end = child.getMeasuredWidth() + start2;
                    int layoutLeft = isRtl ? width - end : start2;
                    int layoutRight = isRtl ? width - start2 : end;
                    int start3 = end + params.getMarginEnd();
                    int start4 = childTop + childHeight;
                    child.layout(layoutLeft, childTop, layoutRight, start4);
                    start = start3;
                }
            }
            i3++;
        }
        updateTouchListener();
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    private void updateTouchListener() {
        if (this.mFeedbackListener == null) {
            setOnTouchListener(null);
            return;
        }
        setOnTouchListener(this.mTouchListener);
        this.mTouchListener.bindTouchRects();
    }

    public void setFeedbackOnClickListener(View.OnClickListener l) {
        this.mFeedbackListener = l;
        this.mFeedbackIcon.setOnClickListener(l);
        updateTouchListener();
    }

    public void setHeaderTextMarginEnd(int headerTextMarginEnd) {
        if (this.mHeaderTextMarginEnd != headerTextMarginEnd) {
            this.mHeaderTextMarginEnd = headerTextMarginEnd;
            requestLayout();
        }
    }

    public int getHeaderTextMarginEnd() {
        return this.mHeaderTextMarginEnd;
    }

    public void setPaddingStart(int paddingStart) {
        setPaddingRelative(paddingStart, getPaddingTop(), getPaddingEnd(), getPaddingBottom());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class HeaderTouchListener implements View.OnTouchListener {
        private float mDownX;
        private float mDownY;
        private Rect mFeedbackRect;
        private int mTouchSlop;
        private boolean mTrackGesture;

        HeaderTouchListener() {
        }

        public void bindTouchRects() {
            this.mFeedbackRect = getRectAroundView(NotificationTopLineView.this.mFeedbackIcon);
            this.mTouchSlop = ViewConfiguration.get(NotificationTopLineView.this.getContext()).getScaledTouchSlop();
        }

        private Rect getRectAroundView(View view) {
            float size = NotificationTopLineView.this.getResources().getDisplayMetrics().density * 48.0f;
            float width = Math.max(size, view.getWidth());
            float height = Math.max(size, view.getHeight());
            Rect r = new Rect();
            if (view.getVisibility() == 8) {
                view = NotificationTopLineView.this.getFirstChildNotGone();
                r.left = (int) (view.getLeft() - (width / 2.0f));
            } else {
                r.left = (int) (((view.getLeft() + view.getRight()) / 2.0f) - (width / 2.0f));
            }
            r.top = (int) (((view.getTop() + view.getBottom()) / 2.0f) - (height / 2.0f));
            r.bottom = (int) (r.top + height);
            r.right = (int) (r.left + width);
            return r;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getActionMasked() & 255) {
                case 0:
                    this.mTrackGesture = false;
                    if (isInside(x, y)) {
                        this.mDownX = x;
                        this.mDownY = y;
                        this.mTrackGesture = true;
                        return true;
                    }
                    break;
                case 1:
                    if (this.mTrackGesture && onTouchUp(x, y, this.mDownX, this.mDownY)) {
                        return true;
                    }
                    break;
                case 2:
                    if (this.mTrackGesture && (Math.abs(this.mDownX - x) > this.mTouchSlop || Math.abs(this.mDownY - y) > this.mTouchSlop)) {
                        this.mTrackGesture = false;
                        break;
                    }
                    break;
            }
            return this.mTrackGesture;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean onTouchUp(float upX, float upY, float downX, float downY) {
            if (NotificationTopLineView.this.mFeedbackIcon.isVisibleToUser()) {
                if (this.mFeedbackRect.contains((int) upX, (int) upY) || this.mFeedbackRect.contains((int) downX, (int) downY)) {
                    NotificationTopLineView.this.mFeedbackIcon.performClick();
                    return true;
                }
                return false;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isInside(float x, float y) {
            return this.mFeedbackRect.contains((int) x, (int) y);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getFirstChildNotGone() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                return child;
            }
        }
        return this;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isInTouchRect(float x, float y) {
        if (this.mFeedbackListener == null) {
            return false;
        }
        return this.mTouchListener.isInside(x, y);
    }

    public boolean onTouchUp(float upX, float upY, float downX, float downY) {
        if (this.mFeedbackListener == null) {
            return false;
        }
        return this.mTouchListener.onTouchUp(upX, upY, downX, downY);
    }

    /* loaded from: classes4.dex */
    private final class OverflowAdjuster {
        private int mHeightSpec;
        private int mOverflow;
        private View mRegrowView;

        private OverflowAdjuster() {
        }

        OverflowAdjuster resetForOverflow(int overflow, int heightSpec) {
            this.mOverflow = overflow;
            this.mHeightSpec = heightSpec;
            this.mRegrowView = null;
            return this;
        }

        OverflowAdjuster adjust(View targetView, View targetDivider, int minimumWidth) {
            View view;
            View view2;
            if (this.mOverflow <= 0 || targetView == null || targetView.getVisibility() == 8) {
                return this;
            }
            int oldWidth = targetView.getMeasuredWidth();
            if (oldWidth <= minimumWidth) {
                return this;
            }
            int newSize = Math.max(minimumWidth, oldWidth - this.mOverflow);
            if (minimumWidth == 0 && newSize < NotificationTopLineView.this.mChildHideWidth && (view2 = this.mRegrowView) != null && view2 != targetView) {
                newSize = 0;
            }
            int childWidthSpec = View.MeasureSpec.makeMeasureSpec(newSize, Integer.MIN_VALUE);
            targetView.measure(childWidthSpec, this.mHeightSpec);
            this.mOverflow -= oldWidth - newSize;
            if (newSize == 0) {
                NotificationTopLineView.this.mViewsToDisappear.add(targetView);
                this.mOverflow -= getHorizontalMargins(targetView);
                if (targetDivider != null && targetDivider.getVisibility() != 8) {
                    NotificationTopLineView.this.mViewsToDisappear.add(targetDivider);
                    int oldDividerWidth = targetDivider.getMeasuredWidth();
                    int dividerWidthSpec = View.MeasureSpec.makeMeasureSpec(0, Integer.MIN_VALUE);
                    targetDivider.measure(dividerWidthSpec, this.mHeightSpec);
                    this.mOverflow -= getHorizontalMargins(targetDivider) + oldDividerWidth;
                }
            }
            int oldDividerWidth2 = this.mOverflow;
            if (oldDividerWidth2 < 0 && (view = this.mRegrowView) != null) {
                int regrowCurrentSize = view.getMeasuredWidth();
                int maxSize = regrowCurrentSize - this.mOverflow;
                int regrowWidthSpec = View.MeasureSpec.makeMeasureSpec(maxSize, Integer.MIN_VALUE);
                this.mRegrowView.measure(regrowWidthSpec, this.mHeightSpec);
                finish();
                return this;
            }
            if (newSize != 0) {
                this.mRegrowView = targetView;
            }
            return this;
        }

        void finish() {
            resetForOverflow(0, 0);
        }

        private int getHorizontalMargins(View view) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            return params.getMarginStart() + params.getMarginEnd();
        }
    }
}
