package com.android.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.metrics.LogMaker;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.OverScroller;
import android.widget.ScrollView;
import com.android.internal.C4057R;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.widget.RecyclerView;
/* loaded from: classes5.dex */
public class ResolverDrawerLayout extends ViewGroup {
    private static final String TAG = "ResolverDrawerLayout";
    private int mActivePointerId;
    private int mAlwaysShowHeight;
    private float mCollapseOffset;
    private int mCollapsibleHeight;
    private int mCollapsibleHeightReserved;
    private boolean mDismissLocked;
    private boolean mDismissOnScrollerFinished;
    private float mDragRemainder;
    private int mIgnoreOffsetTopLimitViewId;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private boolean mIsDragging;
    private final boolean mIsMaxCollapsedHeightSmallExplicit;
    private float mLastTouchY;
    private int mMaxCollapsedHeight;
    private int mMaxCollapsedHeightSmall;
    private final int mMaxWidth;
    private MetricsLogger mMetricsLogger;
    private final float mMinFlingVelocity;
    private AbsListView mNestedListChild;
    private RecyclerView mNestedRecyclerChild;
    private OnCollapsedChangedListener mOnCollapsedChangedListener;
    private OnDismissedListener mOnDismissedListener;
    private boolean mOpenOnClick;
    private boolean mOpenOnLayout;
    private RunOnDismissedListener mRunOnDismissedListener;
    private Drawable mScrollIndicatorDrawable;
    private final OverScroller mScroller;
    private boolean mShowAtTop;
    private boolean mSmallCollapsed;
    private final Rect mTempRect;
    private int mTopOffset;
    private final ViewTreeObserver.OnTouchModeChangeListener mTouchModeChangeListener;
    private final int mTouchSlop;
    private int mUncollapsibleHeight;
    private final VelocityTracker mVelocityTracker;

    /* loaded from: classes5.dex */
    public interface OnCollapsedChangedListener {
        void onCollapsedChanged(boolean z);
    }

    /* loaded from: classes5.dex */
    public interface OnDismissedListener {
        void onDismissed();
    }

    public ResolverDrawerLayout(Context context) {
        this(context, null);
    }

    public ResolverDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResolverDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDragRemainder = 0.0f;
        this.mIgnoreOffsetTopLimitViewId = 0;
        this.mActivePointerId = -1;
        this.mTempRect = new Rect();
        this.mTouchModeChangeListener = new ViewTreeObserver.OnTouchModeChangeListener() { // from class: com.android.internal.widget.ResolverDrawerLayout.1
            @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
            public void onTouchModeChanged(boolean isInTouchMode) {
                if (isInTouchMode || !ResolverDrawerLayout.this.hasFocus()) {
                    return;
                }
                ResolverDrawerLayout resolverDrawerLayout = ResolverDrawerLayout.this;
                if (resolverDrawerLayout.isDescendantClipped(resolverDrawerLayout.getFocusedChild())) {
                    ResolverDrawerLayout.this.smoothScrollTo(0, 0.0f);
                }
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.ResolverDrawerLayout, defStyleAttr, 0);
        this.mMaxWidth = a.getDimensionPixelSize(0, -1);
        int dimensionPixelSize = a.getDimensionPixelSize(2, 0);
        this.mMaxCollapsedHeight = dimensionPixelSize;
        this.mMaxCollapsedHeightSmall = a.getDimensionPixelSize(3, dimensionPixelSize);
        this.mIsMaxCollapsedHeightSmallExplicit = a.hasValue(3);
        this.mShowAtTop = a.getBoolean(4, false);
        if (a.hasValue(1)) {
            this.mIgnoreOffsetTopLimitViewId = a.getResourceId(1, 0);
        }
        a.recycle();
        this.mScrollIndicatorDrawable = this.mContext.getDrawable(C4057R.C4058drawable.scroll_indicator_material);
        this.mScroller = new OverScroller(context, AnimationUtils.loadInterpolator(context, 17563653));
        this.mVelocityTracker = VelocityTracker.obtain();
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.mTouchSlop = vc.getScaledTouchSlop();
        this.mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        setImportantForAccessibility(1);
    }

    public void setMaxCollapsedHeight(int heightInPixels) {
        if (heightInPixels == this.mMaxCollapsedHeight) {
            return;
        }
        this.mMaxCollapsedHeight = heightInPixels;
        if (!this.mIsMaxCollapsedHeightSmallExplicit) {
            this.mMaxCollapsedHeightSmall = heightInPixels;
        }
        requestLayout();
    }

    public void setSmallCollapsed(boolean smallCollapsed) {
        if (this.mSmallCollapsed != smallCollapsed) {
            this.mSmallCollapsed = smallCollapsed;
            requestLayout();
        }
    }

    public boolean isSmallCollapsed() {
        return this.mSmallCollapsed;
    }

    public boolean isCollapsed() {
        return this.mCollapseOffset > 0.0f;
    }

    public void setShowAtTop(boolean showOnTop) {
        if (this.mShowAtTop != showOnTop) {
            this.mShowAtTop = showOnTop;
            requestLayout();
        }
    }

    public boolean getShowAtTop() {
        return this.mShowAtTop;
    }

    public void setCollapsed(boolean collapsed) {
        if (!isLaidOut()) {
            this.mOpenOnLayout = !collapsed;
        } else {
            smoothScrollTo(collapsed ? this.mCollapsibleHeight : 0, 0.0f);
        }
    }

    public void setCollapsibleHeightReserved(int heightPixels) {
        int oldReserved = this.mCollapsibleHeightReserved;
        this.mCollapsibleHeightReserved = heightPixels;
        if (oldReserved != heightPixels) {
            requestLayout();
        }
        int dReserved = this.mCollapsibleHeightReserved - oldReserved;
        if (dReserved != 0 && this.mIsDragging) {
            this.mLastTouchY -= dReserved;
        }
        int oldCollapsibleHeight = this.mCollapsibleHeight;
        this.mCollapsibleHeight = Math.min(this.mCollapsibleHeight, getMaxCollapsedHeight());
        if (updateCollapseOffset(oldCollapsibleHeight, !isDragging())) {
            return;
        }
        invalidate();
    }

    public void setDismissLocked(boolean locked) {
        this.mDismissLocked = locked;
    }

    private boolean isMoving() {
        return this.mIsDragging || !this.mScroller.isFinished();
    }

    private boolean isDragging() {
        return this.mIsDragging || getNestedScrollAxes() == 2;
    }

    private boolean updateCollapseOffset(int oldCollapsibleHeight, boolean remainClosed) {
        int i;
        if (oldCollapsibleHeight == this.mCollapsibleHeight) {
            return false;
        }
        if (getShowAtTop()) {
            setCollapseOffset(0.0f);
            return false;
        }
        if (isLaidOut()) {
            float f = this.mCollapseOffset;
            boolean isCollapsedOld = f != 0.0f;
            if (remainClosed && oldCollapsibleHeight < (i = this.mCollapsibleHeight) && f == oldCollapsibleHeight) {
                setCollapseOffset(i);
            } else {
                setCollapseOffset(Math.min(f, this.mCollapsibleHeight));
            }
            boolean isCollapsedNew = this.mCollapseOffset != 0.0f;
            if (isCollapsedOld != isCollapsedNew) {
                onCollapsedChanged(isCollapsedNew);
            }
        } else {
            setCollapseOffset(this.mOpenOnLayout ? 0.0f : this.mCollapsibleHeight);
        }
        return true;
    }

    private void setCollapseOffset(float collapseOffset) {
        if (this.mCollapseOffset != collapseOffset) {
            this.mCollapseOffset = collapseOffset;
            requestLayout();
        }
    }

    private int getMaxCollapsedHeight() {
        return (isSmallCollapsed() ? this.mMaxCollapsedHeightSmall : this.mMaxCollapsedHeight) + this.mCollapsibleHeightReserved;
    }

    public void setOnDismissedListener(OnDismissedListener listener) {
        this.mOnDismissedListener = listener;
    }

    private boolean isDismissable() {
        return (this.mOnDismissedListener == null || this.mDismissLocked) ? false : true;
    }

    public void setOnCollapsedChangedListener(OnCollapsedChangedListener listener) {
        this.mOnCollapsedChangedListener = listener;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == 0) {
            this.mVelocityTracker.clear();
        }
        this.mVelocityTracker.addMovement(ev);
        switch (action) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                this.mInitialTouchX = x;
                this.mLastTouchY = y;
                this.mInitialTouchY = y;
                this.mOpenOnClick = isListChildUnderClipped(x, y) && this.mCollapseOffset > 0.0f;
                break;
            case 1:
            case 3:
                resetTouch();
                break;
            case 2:
                float x2 = ev.getX();
                float y2 = ev.getY();
                float dy = y2 - this.mInitialTouchY;
                if (Math.abs(dy) > this.mTouchSlop && findChildUnder(x2, y2) != null && (getNestedScrollAxes() & 2) == 0) {
                    this.mActivePointerId = ev.getPointerId(0);
                    this.mIsDragging = true;
                    float f = this.mLastTouchY;
                    int i = this.mTouchSlop;
                    this.mLastTouchY = Math.max(f - i, Math.min(f + dy, f + i));
                    break;
                }
                break;
            case 6:
                onSecondaryPointerUp(ev);
                break;
        }
        if (this.mIsDragging) {
            abortAnimation();
        }
        return this.mIsDragging || this.mOpenOnClick;
    }

    private boolean isNestedListChildScrolled() {
        AbsListView absListView = this.mNestedListChild;
        if (absListView == null || absListView.getChildCount() <= 0) {
            return false;
        }
        return this.mNestedListChild.getFirstVisiblePosition() > 0 || this.mNestedListChild.getChildAt(0).getTop() < 0;
    }

    private boolean isNestedRecyclerChildScrolled() {
        RecyclerView recyclerView = this.mNestedRecyclerChild;
        if (recyclerView == null || recyclerView.getChildCount() <= 0) {
            return false;
        }
        RecyclerView.ViewHolder vh = this.mNestedRecyclerChild.findViewHolderForAdapterPosition(0);
        return vh == null || vh.itemView.getTop() < 0;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        this.mVelocityTracker.addMovement(ev);
        boolean handled = false;
        boolean z = true;
        switch (action) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                this.mInitialTouchX = x;
                this.mLastTouchY = y;
                this.mInitialTouchY = y;
                this.mActivePointerId = ev.getPointerId(0);
                boolean hitView = findChildUnder(this.mInitialTouchX, this.mInitialTouchY) != null;
                handled = isDismissable() || this.mCollapsibleHeight > 0;
                if (!hitView || !handled) {
                    z = false;
                }
                this.mIsDragging = z;
                abortAnimation();
                break;
            case 1:
                boolean wasDragging = this.mIsDragging;
                this.mIsDragging = false;
                if (!wasDragging && findChildUnder(this.mInitialTouchX, this.mInitialTouchY) == null && findChildUnder(ev.getX(), ev.getY()) == null && isDismissable()) {
                    dispatchOnDismissed();
                    resetTouch();
                    return true;
                } else if (this.mOpenOnClick && Math.abs(ev.getX() - this.mInitialTouchX) < this.mTouchSlop && Math.abs(ev.getY() - this.mInitialTouchY) < this.mTouchSlop) {
                    smoothScrollTo(0, 0.0f);
                    return true;
                } else {
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    float yvel = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                    if (Math.abs(yvel) > this.mMinFlingVelocity) {
                        if (getShowAtTop()) {
                            if (isDismissable() && yvel < 0.0f) {
                                abortAnimation();
                                dismiss();
                            } else {
                                smoothScrollTo(yvel >= 0.0f ? this.mCollapsibleHeight : 0, yvel);
                            }
                        } else {
                            if (isDismissable() && yvel > 0.0f) {
                                float f = this.mCollapseOffset;
                                int i = this.mCollapsibleHeight;
                                if (f > i) {
                                    smoothScrollTo(i + this.mUncollapsibleHeight, yvel);
                                    this.mDismissOnScrollerFinished = true;
                                }
                            }
                            scrollNestedScrollableChildBackToTop();
                            smoothScrollTo(yvel >= 0.0f ? this.mCollapsibleHeight : 0, yvel);
                        }
                    } else {
                        float f2 = this.mCollapseOffset;
                        int i2 = this.mCollapsibleHeight;
                        smoothScrollTo(f2 >= ((float) (i2 / 2)) ? i2 : 0, 0.0f);
                    }
                    resetTouch();
                    break;
                }
            case 2:
                int index = ev.findPointerIndex(this.mActivePointerId);
                if (index < 0) {
                    Log.m110e(TAG, "Bad pointer id " + this.mActivePointerId + ", resetting");
                    index = 0;
                    this.mActivePointerId = ev.getPointerId(0);
                    this.mInitialTouchX = ev.getX();
                    float y2 = ev.getY();
                    this.mLastTouchY = y2;
                    this.mInitialTouchY = y2;
                }
                float x2 = ev.getX(index);
                float y3 = ev.getY(index);
                if (!this.mIsDragging) {
                    float dy = y3 - this.mInitialTouchY;
                    if (Math.abs(dy) > this.mTouchSlop && findChildUnder(x2, y3) != null) {
                        this.mIsDragging = true;
                        handled = true;
                        float f3 = this.mLastTouchY;
                        int i3 = this.mTouchSlop;
                        this.mLastTouchY = Math.max(f3 - i3, Math.min(f3 + dy, f3 + i3));
                    }
                }
                if (this.mIsDragging) {
                    float dy2 = y3 - this.mLastTouchY;
                    if (dy2 > 0.0f && isNestedListChildScrolled()) {
                        this.mNestedListChild.smoothScrollBy((int) (-dy2), 0);
                    } else if (dy2 > 0.0f && isNestedRecyclerChildScrolled()) {
                        this.mNestedRecyclerChild.scrollBy(0, (int) (-dy2));
                    } else {
                        performDrag(dy2);
                    }
                }
                this.mLastTouchY = y3;
                break;
            case 3:
                if (this.mIsDragging) {
                    float f4 = this.mCollapseOffset;
                    int i4 = this.mCollapsibleHeight;
                    smoothScrollTo(f4 >= ((float) (i4 / 2)) ? i4 : 0, 0.0f);
                }
                resetTouch();
                return true;
            case 5:
                int pointerIndex = ev.getActionIndex();
                this.mActivePointerId = ev.getPointerId(pointerIndex);
                this.mInitialTouchX = ev.getX(pointerIndex);
                float y4 = ev.getY(pointerIndex);
                this.mLastTouchY = y4;
                this.mInitialTouchY = y4;
                break;
            case 6:
                onSecondaryPointerUp(ev);
                break;
        }
        return handled;
    }

    public void scrollNestedScrollableChildBackToTop() {
        if (isNestedListChildScrolled()) {
            this.mNestedListChild.smoothScrollToPosition(0);
        } else if (isNestedRecyclerChildScrolled()) {
            this.mNestedRecyclerChild.smoothScrollToPosition(0);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = ev.getActionIndex();
        int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mInitialTouchX = ev.getX(newPointerIndex);
            float y = ev.getY(newPointerIndex);
            this.mLastTouchY = y;
            this.mInitialTouchY = y;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void resetTouch() {
        this.mActivePointerId = -1;
        this.mIsDragging = false;
        this.mOpenOnClick = false;
        this.mLastTouchY = 0.0f;
        this.mInitialTouchY = 0.0f;
        this.mInitialTouchX = 0.0f;
        this.mVelocityTracker.clear();
    }

    private void dismiss() {
        RunOnDismissedListener runOnDismissedListener = new RunOnDismissedListener();
        this.mRunOnDismissedListener = runOnDismissedListener;
        post(runOnDismissedListener);
    }

    @Override // android.view.View
    public void computeScroll() {
        super.computeScroll();
        if (this.mScroller.computeScrollOffset()) {
            boolean keepGoing = !this.mScroller.isFinished();
            performDrag(this.mScroller.getCurrY() - this.mCollapseOffset);
            if (keepGoing) {
                postInvalidateOnAnimation();
            } else if (this.mDismissOnScrollerFinished && this.mOnDismissedListener != null) {
                dismiss();
            }
        }
    }

    private void abortAnimation() {
        this.mScroller.abortAnimation();
        this.mRunOnDismissedListener = null;
        this.mDismissOnScrollerFinished = false;
    }

    private float performDrag(float dy) {
        if (getShowAtTop()) {
            return 0.0f;
        }
        float newPos = Math.max(0.0f, Math.min(this.mCollapseOffset + dy, this.mCollapsibleHeight + this.mUncollapsibleHeight));
        float f = this.mCollapseOffset;
        if (newPos != f) {
            float dy2 = newPos - f;
            float f2 = this.mDragRemainder + (dy2 - ((int) dy2));
            this.mDragRemainder = f2;
            if (f2 >= 1.0f) {
                this.mDragRemainder = f2 - 1.0f;
                dy2 += 1.0f;
            } else if (f2 <= -1.0f) {
                this.mDragRemainder = f2 + 1.0f;
                dy2 -= 1.0f;
            }
            boolean isIgnoreOffsetLimitSet = false;
            int ignoreOffsetLimit = 0;
            View ignoreOffsetLimitView = findIgnoreOffsetLimitView();
            if (ignoreOffsetLimitView != null) {
                ignoreOffsetLimit = ignoreOffsetLimitView.getBottom() + ((LayoutParams) ignoreOffsetLimitView.getLayoutParams()).bottomMargin;
                isIgnoreOffsetLimitSet = true;
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (!lp.ignoreOffset) {
                        child.offsetTopAndBottom((int) dy2);
                    } else if (isIgnoreOffsetLimitSet) {
                        int top = child.getTop();
                        int targetTop = Math.max((int) (lp.topMargin + ignoreOffsetLimit + dy2), lp.mFixedTop);
                        if (top != targetTop) {
                            child.offsetTopAndBottom(targetTop - top);
                        }
                        ignoreOffsetLimit = child.getBottom() + lp.bottomMargin;
                    }
                }
            }
            boolean isCollapsedOld = this.mCollapseOffset != 0.0f;
            this.mCollapseOffset = newPos;
            this.mTopOffset = (int) (this.mTopOffset + dy2);
            boolean isCollapsedNew = newPos != 0.0f;
            if (isCollapsedOld != isCollapsedNew) {
                onCollapsedChanged(isCollapsedNew);
                getMetricsLogger().write(new LogMaker((int) MetricsProto.MetricsEvent.ACTION_SHARESHEET_COLLAPSED_CHANGED).setSubtype(isCollapsedNew ? 1 : 0));
            }
            onScrollChanged(0, (int) newPos, 0, (int) (newPos - dy2));
            postInvalidateOnAnimation();
            return dy2;
        }
        return 0.0f;
    }

    private void onCollapsedChanged(boolean isCollapsed) {
        notifyViewAccessibilityStateChangedIfNeeded(0);
        if (this.mScrollIndicatorDrawable != null) {
            setWillNotDraw(!isCollapsed);
        }
        OnCollapsedChangedListener onCollapsedChangedListener = this.mOnCollapsedChangedListener;
        if (onCollapsedChangedListener != null) {
            onCollapsedChangedListener.onCollapsedChanged(isCollapsed);
        }
    }

    void dispatchOnDismissed() {
        OnDismissedListener onDismissedListener = this.mOnDismissedListener;
        if (onDismissedListener != null) {
            onDismissedListener.onDismissed();
        }
        RunOnDismissedListener runOnDismissedListener = this.mRunOnDismissedListener;
        if (runOnDismissedListener != null) {
            removeCallbacks(runOnDismissedListener);
            this.mRunOnDismissedListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void smoothScrollTo(int yOffset, float velocity) {
        int duration;
        abortAnimation();
        int sy = (int) this.mCollapseOffset;
        int dy = yOffset - sy;
        if (dy == 0) {
            return;
        }
        int height = getHeight();
        int halfHeight = height / 2;
        float distanceRatio = Math.min(1.0f, (Math.abs(dy) * 1.0f) / height);
        float distance = halfHeight + (halfHeight * distanceInfluenceForSnapDuration(distanceRatio));
        float velocity2 = Math.abs(velocity);
        if (velocity2 > 0.0f) {
            duration = Math.round(Math.abs(distance / velocity2) * 1000.0f) * 4;
        } else {
            float pageDelta = Math.abs(dy) / height;
            duration = (int) ((1.0f + pageDelta) * 100.0f);
        }
        this.mScroller.startScroll(0, sy, 0, dy, Math.min(duration, 300));
        postInvalidateOnAnimation();
    }

    private float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((float) ((f - 0.5f) * 0.4712389167638204d));
    }

    private View findChildUnder(float x, float y) {
        return findChildUnder(this, x, y);
    }

    private static View findChildUnder(ViewGroup parent, float x, float y) {
        int childCount = parent.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = parent.getChildAt(i);
            if (isChildUnder(child, x, y)) {
                return child;
            }
        }
        return null;
    }

    private View findListChildUnder(float x, float y) {
        View v = findChildUnder(x, y);
        while (v != null) {
            x -= v.getX();
            y -= v.getY();
            if (v instanceof AbsListView) {
                return findChildUnder((ViewGroup) v, x, y);
            }
            v = v instanceof ViewGroup ? findChildUnder((ViewGroup) v, x, y) : null;
        }
        return v;
    }

    private boolean isListChildUnderClipped(float x, float y) {
        View listChild = findListChildUnder(x, y);
        return listChild != null && isDescendantClipped(listChild);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDescendantClipped(View child) {
        View v;
        this.mTempRect.set(0, 0, child.getWidth(), child.getHeight());
        offsetDescendantRectToMyCoords(child, this.mTempRect);
        if (child.getParent() == this) {
            v = child;
        } else {
            v = child;
            ViewParent p = child.getParent();
            while (p != this) {
                v = (View) p;
                p = v.getParent();
            }
        }
        int clipEdge = getHeight() - getPaddingBottom();
        int childCount = getChildCount();
        for (int i = indexOfChild(v) + 1; i < childCount; i++) {
            View nextChild = getChildAt(i);
            if (nextChild.getVisibility() != 8) {
                clipEdge = Math.min(clipEdge, nextChild.getTop());
            }
        }
        return this.mTempRect.bottom > clipEdge;
    }

    private static boolean isChildUnder(View child, float x, float y) {
        float left = child.getX();
        float top = child.getY();
        float right = child.getWidth() + left;
        float bottom = child.getHeight() + top;
        return x >= left && y >= top && x < right && y < bottom;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (!isInTouchMode() && isDescendantClipped(focused)) {
            smoothScrollTo(0, 0.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnTouchModeChangeListener(this.mTouchModeChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnTouchModeChangeListener(this.mTouchModeChangeListener);
        abortAnimation();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if ((nestedScrollAxes & 2) != 0) {
            if (target instanceof AbsListView) {
                this.mNestedListChild = (AbsListView) target;
            }
            if (target instanceof RecyclerView) {
                this.mNestedRecyclerChild = (RecyclerView) target;
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        if (this.mScroller.isFinished()) {
            float f = this.mCollapseOffset;
            int i = this.mCollapsibleHeight;
            if (f < i / 2) {
                i = 0;
            }
            smoothScrollTo(i, 0.0f);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0) {
            performDrag(-dyUnconsumed);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (dy > 0) {
            consumed[1] = (int) (-performDrag(-dy));
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (getShowAtTop() || velocityY <= this.mMinFlingVelocity || this.mCollapseOffset == 0.0f) {
            return false;
        }
        smoothScrollTo(0, velocityY);
        return true;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (consumed || Math.abs(velocityY) <= this.mMinFlingVelocity) {
            return false;
        }
        if (getShowAtTop()) {
            if (isDismissable() && velocityY > 0.0f) {
                abortAnimation();
                dismiss();
            } else {
                smoothScrollTo(velocityY < 0.0f ? this.mCollapsibleHeight : 0, velocityY);
            }
        } else {
            if (isDismissable() && velocityY < 0.0f) {
                float f = this.mCollapseOffset;
                int i = this.mCollapsibleHeight;
                if (f > i) {
                    smoothScrollTo(i + this.mUncollapsibleHeight, velocityY);
                    this.mDismissOnScrollerFinished = true;
                }
            }
            smoothScrollTo(velocityY <= 0.0f ? this.mCollapsibleHeight : 0, velocityY);
        }
        return true;
    }

    private boolean performAccessibilityActionCommon(int action) {
        switch (action) {
            case 4096:
            case 262144:
            case 16908346:
                if (this.mCollapseOffset != 0.0f) {
                    smoothScrollTo(0, 0.0f);
                    return true;
                }
                break;
            case 524288:
                float f = this.mCollapseOffset;
                int i = this.mCollapsibleHeight;
                if (f < i) {
                    smoothScrollTo(i, 0.0f);
                    return true;
                }
                break;
            case 1048576:
                if (this.mCollapseOffset < this.mCollapsibleHeight + this.mUncollapsibleHeight && isDismissable()) {
                    smoothScrollTo(this.mCollapsibleHeight + this.mUncollapsibleHeight, 0.0f);
                    this.mDismissOnScrollerFinished = true;
                    return true;
                }
                break;
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean onNestedPrePerformAccessibilityAction(View target, int action, Bundle args) {
        if (super.onNestedPrePerformAccessibilityAction(target, action, args)) {
            return true;
        }
        return performAccessibilityActionCommon(action);
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ScrollView.class.getName();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        if (isEnabled()) {
            if (this.mCollapseOffset != 0.0f) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
                info.setScrollable(true);
            }
            float f = this.mCollapseOffset;
            int i = this.mCollapsibleHeight;
            if (f < this.mUncollapsibleHeight + i && (f < i || isDismissable())) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
                info.setScrollable(true);
            }
            if (this.mCollapseOffset < this.mCollapsibleHeight) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE);
            }
            if (this.mCollapseOffset < this.mCollapsibleHeight + this.mUncollapsibleHeight && isDismissable()) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS);
            }
        }
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_ACCESSIBILITY_FOCUS);
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        if (action == AccessibilityNodeInfo.AccessibilityAction.ACTION_ACCESSIBILITY_FOCUS.getId()) {
            return false;
        }
        if (super.performAccessibilityActionInternal(action, arguments)) {
            return true;
        }
        return performAccessibilityActionCommon(action);
    }

    @Override // android.view.View
    public void onDrawForeground(Canvas canvas) {
        Drawable drawable = this.mScrollIndicatorDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        super.onDrawForeground(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize;
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int sourceWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int i8 = this.mMaxWidth;
        if (i8 >= 0) {
            int widthSize2 = Math.min(sourceWidth, i8 + getPaddingLeft() + getPaddingRight());
            widthSize = widthSize2;
        } else {
            widthSize = sourceWidth;
        }
        int widthSpec = View.MeasureSpec.makeMeasureSpec(widthSize, 1073741824);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(heightSize, 1073741824);
        int childCount = getChildCount();
        int heightUsed = 0;
        int i9 = 0;
        while (true) {
            i = Integer.MIN_VALUE;
            i2 = -1;
            i3 = 8;
            i4 = 0;
            if (i9 >= childCount) {
                break;
            }
            View child = getChildAt(i9);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.alwaysShow && child.getVisibility() != 8) {
                if (lp.maxHeight != -1) {
                    int remainingHeight = heightSize - heightUsed;
                    measureChildWithMargins(child, widthSpec, 0, View.MeasureSpec.makeMeasureSpec(lp.maxHeight, Integer.MIN_VALUE), lp.maxHeight > remainingHeight ? lp.maxHeight - remainingHeight : 0);
                } else {
                    measureChildWithMargins(child, widthSpec, 0, heightSpec, heightUsed);
                }
                heightUsed += child.getMeasuredHeight();
            }
            i9++;
        }
        this.mAlwaysShowHeight = heightUsed;
        int heightUsed2 = heightUsed;
        int heightUsed3 = 0;
        while (heightUsed3 < childCount) {
            View child2 = getChildAt(heightUsed3);
            LayoutParams lp2 = (LayoutParams) child2.getLayoutParams();
            if (lp2.alwaysShow || child2.getVisibility() == i3) {
                i5 = i4;
                i6 = i3;
                i7 = i2;
            } else {
                if (lp2.maxHeight != i2) {
                    int remainingHeight2 = heightSize - heightUsed2;
                    int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(lp2.maxHeight, i);
                    i5 = i4;
                    i6 = i3;
                    i7 = i2;
                    measureChildWithMargins(child2, widthSpec, 0, makeMeasureSpec, lp2.maxHeight > remainingHeight2 ? lp2.maxHeight - remainingHeight2 : i4);
                } else {
                    i5 = i4;
                    i6 = i3;
                    i7 = i2;
                    measureChildWithMargins(child2, widthSpec, 0, heightSpec, heightUsed2);
                }
                heightUsed2 += child2.getMeasuredHeight();
            }
            heightUsed3++;
            i4 = i5;
            i3 = i6;
            i2 = i7;
            i = Integer.MIN_VALUE;
        }
        int i10 = i4;
        int oldCollapsibleHeight = this.mCollapsibleHeight;
        int max = Math.max(i10, (heightUsed2 - this.mAlwaysShowHeight) - getMaxCollapsedHeight());
        this.mCollapsibleHeight = max;
        this.mUncollapsibleHeight = heightUsed2 - max;
        updateCollapseOffset(oldCollapsibleHeight, !isDragging());
        if (!getShowAtTop()) {
            this.mTopOffset = Math.max(i10, heightSize - heightUsed2) + ((int) this.mCollapseOffset);
        } else {
            this.mTopOffset = i10;
        }
        setMeasuredDimension(sourceWidth, heightSize);
    }

    public int getAlwaysShowHeight() {
        return this.mAlwaysShowHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int width;
        View indicatorHost;
        int width2 = getWidth();
        View indicatorHost2 = null;
        int ypos = this.mTopOffset;
        int leftEdge = getPaddingLeft();
        int rightEdge = width2 - getPaddingRight();
        int widthAvailable = rightEdge - leftEdge;
        boolean isIgnoreOffsetLimitSet = false;
        int ignoreOffsetLimit = 0;
        int childCount = getChildCount();
        int i = 0;
        while (i < childCount) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.hasNestedScrollIndicator) {
                indicatorHost2 = child;
            }
            if (child.getVisibility() == 8) {
                width = width2;
                indicatorHost = indicatorHost2;
            } else {
                int i2 = this.mIgnoreOffsetTopLimitViewId;
                if (i2 != 0 && !isIgnoreOffsetLimitSet && i2 == child.getId()) {
                    ignoreOffsetLimit = child.getBottom() + lp.bottomMargin;
                    isIgnoreOffsetLimitSet = true;
                }
                int top = lp.topMargin + ypos;
                if (!lp.ignoreOffset) {
                    width = width2;
                } else {
                    if (!isDragging()) {
                        lp.mFixedTop = (int) (top - this.mCollapseOffset);
                    }
                    if (isIgnoreOffsetLimitSet) {
                        width = width2;
                        top = Math.max(lp.topMargin + ignoreOffsetLimit, (int) (top - this.mCollapseOffset));
                        ignoreOffsetLimit = child.getMeasuredHeight() + top + lp.bottomMargin;
                    } else {
                        width = width2;
                        top = (int) (top - this.mCollapseOffset);
                    }
                }
                int bottom = child.getMeasuredHeight() + top;
                int childWidth = child.getMeasuredWidth();
                int left = ((widthAvailable - childWidth) / 2) + leftEdge;
                indicatorHost = indicatorHost2;
                int right = left + childWidth;
                child.layout(left, top, right, bottom);
                int right2 = lp.bottomMargin;
                ypos = right2 + bottom;
            }
            i++;
            width2 = width;
            indicatorHost2 = indicatorHost;
        }
        if (this.mScrollIndicatorDrawable != null) {
            if (indicatorHost2 == null) {
                this.mScrollIndicatorDrawable = null;
                setWillNotDraw(true);
                return;
            }
            int left2 = indicatorHost2.getLeft();
            int right3 = indicatorHost2.getRight();
            int bottom2 = indicatorHost2.getTop();
            this.mScrollIndicatorDrawable.setBounds(left2, bottom2 - this.mScrollIndicatorDrawable.getIntrinsicHeight(), right3, bottom2);
            setWillNotDraw(true ^ isCollapsed());
        }
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) p);
        }
        if (p instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.open = this.mCollapsibleHeight > 0 && this.mCollapseOffset == 0.0f;
        ss.mCollapsibleHeightReserved = this.mCollapsibleHeightReserved;
        return ss;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mOpenOnLayout = ss.open;
        this.mCollapsibleHeightReserved = ss.mCollapsibleHeightReserved;
    }

    private View findIgnoreOffsetLimitView() {
        View v;
        int i = this.mIgnoreOffsetTopLimitViewId;
        if (i == 0 || (v = findViewById(i)) == null || v == this || v.getParent() != this || v.getVisibility() == 8) {
            return null;
        }
        return v;
    }

    /* loaded from: classes5.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public boolean alwaysShow;
        public boolean hasNestedScrollIndicator;
        public boolean ignoreOffset;
        int mFixedTop;
        public int maxHeight;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, C4057R.styleable.ResolverDrawerLayout_LayoutParams);
            this.alwaysShow = a.getBoolean(1, false);
            this.ignoreOffset = a.getBoolean(3, false);
            this.hasNestedScrollIndicator = a.getBoolean(2, false);
            this.maxHeight = a.getDimensionPixelSize(4, -1);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.alwaysShow = source.alwaysShow;
            this.ignoreOffset = source.ignoreOffset;
            this.hasNestedScrollIndicator = source.hasNestedScrollIndicator;
            this.maxHeight = source.maxHeight;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.internal.widget.ResolverDrawerLayout.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private int mCollapsibleHeightReserved;
        boolean open;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.open = in.readInt() != 0;
            this.mCollapsibleHeightReserved = in.readInt();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.open ? 1 : 0);
            out.writeInt(this.mCollapsibleHeightReserved);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class RunOnDismissedListener implements Runnable {
        private RunOnDismissedListener() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ResolverDrawerLayout.this.dispatchOnDismissed();
        }
    }

    private MetricsLogger getMetricsLogger() {
        if (this.mMetricsLogger == null) {
            this.mMetricsLogger = new MetricsLogger();
        }
        return this.mMetricsLogger;
    }
}
