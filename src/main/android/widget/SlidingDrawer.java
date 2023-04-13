package android.widget;

import android.C0001R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.p008os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
@Deprecated
/* loaded from: classes4.dex */
public class SlidingDrawer extends ViewGroup {
    private static final int ANIMATION_FRAME_DURATION = 16;
    private static final int COLLAPSED_FULL_CLOSED = -10002;
    private static final int EXPANDED_FULL_OPEN = -10001;
    private static final float MAXIMUM_ACCELERATION = 2000.0f;
    private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;
    private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;
    private static final float MAXIMUM_TAP_VELOCITY = 100.0f;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    private static final int TAP_THRESHOLD = 6;
    private static final int VELOCITY_UNITS = 1000;
    private boolean mAllowSingleTap;
    private boolean mAnimateOnClick;
    private float mAnimatedAcceleration;
    private float mAnimatedVelocity;
    private boolean mAnimating;
    private long mAnimationLastTime;
    private float mAnimationPosition;
    private int mBottomOffset;
    private View mContent;
    private final int mContentId;
    private long mCurrentAnimationTime;
    private boolean mExpanded;
    private final Rect mFrame;
    private View mHandle;
    private int mHandleHeight;
    private final int mHandleId;
    private int mHandleWidth;
    private final Rect mInvalidate;
    private boolean mLocked;
    private final int mMaximumAcceleration;
    private final int mMaximumMajorVelocity;
    private final int mMaximumMinorVelocity;
    private final int mMaximumTapVelocity;
    private OnDrawerCloseListener mOnDrawerCloseListener;
    private OnDrawerOpenListener mOnDrawerOpenListener;
    private OnDrawerScrollListener mOnDrawerScrollListener;
    private final Runnable mSlidingRunnable;
    private final int mTapThreshold;
    private int mTopOffset;
    private int mTouchDelta;
    private boolean mTracking;
    private VelocityTracker mVelocityTracker;
    private final int mVelocityUnits;
    private boolean mVertical;

    /* loaded from: classes4.dex */
    public interface OnDrawerCloseListener {
        void onDrawerClosed();
    }

    /* loaded from: classes4.dex */
    public interface OnDrawerOpenListener {
        void onDrawerOpened();
    }

    /* loaded from: classes4.dex */
    public interface OnDrawerScrollListener {
        void onScrollEnded();

        void onScrollStarted();
    }

    public SlidingDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mFrame = new Rect();
        this.mInvalidate = new Rect();
        this.mSlidingRunnable = new Runnable() { // from class: android.widget.SlidingDrawer.1
            @Override // java.lang.Runnable
            public void run() {
                SlidingDrawer.this.doAnimation();
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, C0001R.styleable.SlidingDrawer, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C0001R.styleable.SlidingDrawer, attrs, a, defStyleAttr, defStyleRes);
        int orientation = a.getInt(0, 1);
        this.mVertical = orientation == 1;
        this.mBottomOffset = (int) a.getDimension(1, 0.0f);
        this.mTopOffset = (int) a.getDimension(2, 0.0f);
        this.mAllowSingleTap = a.getBoolean(3, true);
        this.mAnimateOnClick = a.getBoolean(6, true);
        int handleId = a.getResourceId(4, 0);
        if (handleId != 0) {
            int contentId = a.getResourceId(5, 0);
            if (contentId == 0) {
                throw new IllegalArgumentException("The content attribute is required and must refer to a valid child.");
            }
            if (handleId == contentId) {
                throw new IllegalArgumentException("The content and handle attributes must refer to different children.");
            }
            this.mHandleId = handleId;
            this.mContentId = contentId;
            float density = getResources().getDisplayMetrics().density;
            this.mTapThreshold = (int) ((6.0f * density) + 0.5f);
            this.mMaximumTapVelocity = (int) ((100.0f * density) + 0.5f);
            this.mMaximumMinorVelocity = (int) ((MAXIMUM_MINOR_VELOCITY * density) + 0.5f);
            this.mMaximumMajorVelocity = (int) ((200.0f * density) + 0.5f);
            this.mMaximumAcceleration = (int) ((MAXIMUM_ACCELERATION * density) + 0.5f);
            this.mVelocityUnits = (int) ((1000.0f * density) + 0.5f);
            a.recycle();
            setAlwaysDrawnWithCacheEnabled(false);
            return;
        }
        throw new IllegalArgumentException("The handle attribute is required and must refer to a valid child.");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        View findViewById = findViewById(this.mHandleId);
        this.mHandle = findViewById;
        if (findViewById == null) {
            throw new IllegalArgumentException("The handle attribute is must refer to an existing child.");
        }
        findViewById.setOnClickListener(new DrawerToggler());
        View findViewById2 = findViewById(this.mContentId);
        this.mContent = findViewById2;
        if (findViewById2 == null) {
            throw new IllegalArgumentException("The content attribute is must refer to an existing child.");
        }
        findViewById2.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == 0 || heightSpecMode == 0) {
            throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
        }
        View handle = this.mHandle;
        measureChild(handle, widthMeasureSpec, heightMeasureSpec);
        if (this.mVertical) {
            int height = (heightSpecSize - handle.getMeasuredHeight()) - this.mTopOffset;
            this.mContent.measure(View.MeasureSpec.makeMeasureSpec(widthSpecSize, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
        } else {
            int width = (widthSpecSize - handle.getMeasuredWidth()) - this.mTopOffset;
            this.mContent.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(heightSpecSize, 1073741824));
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        long drawingTime = getDrawingTime();
        View handle = this.mHandle;
        boolean isVertical = this.mVertical;
        drawChild(canvas, handle, drawingTime);
        if (this.mTracking || this.mAnimating) {
            Bitmap cache = this.mContent.getDrawingCache();
            if (cache != null) {
                if (isVertical) {
                    canvas.drawBitmap(cache, 0.0f, handle.getBottom(), (Paint) null);
                    return;
                } else {
                    canvas.drawBitmap(cache, handle.getRight(), 0.0f, (Paint) null);
                    return;
                }
            }
            canvas.save();
            canvas.translate(isVertical ? 0.0f : handle.getLeft() - this.mTopOffset, isVertical ? handle.getTop() - this.mTopOffset : 0.0f);
            drawChild(canvas, this.mContent, drawingTime);
            canvas.restore();
        } else if (this.mExpanded) {
            drawChild(canvas, this.mContent, drawingTime);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft;
        int childTop;
        if (this.mTracking) {
            return;
        }
        int width = r - l;
        int height = b - t;
        View handle = this.mHandle;
        int childWidth = handle.getMeasuredWidth();
        int childHeight = handle.getMeasuredHeight();
        View content = this.mContent;
        if (this.mVertical) {
            childLeft = (width - childWidth) / 2;
            childTop = this.mExpanded ? this.mTopOffset : (height - childHeight) + this.mBottomOffset;
            content.layout(0, this.mTopOffset + childHeight, content.getMeasuredWidth(), this.mTopOffset + childHeight + content.getMeasuredHeight());
        } else {
            childLeft = this.mExpanded ? this.mTopOffset : (width - childWidth) + this.mBottomOffset;
            childTop = (height - childHeight) / 2;
            int i = this.mTopOffset;
            content.layout(i + childWidth, 0, i + childWidth + content.getMeasuredWidth(), content.getMeasuredHeight());
        }
        handle.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        this.mHandleHeight = handle.getHeight();
        this.mHandleWidth = handle.getWidth();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mLocked) {
            return false;
        }
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        Rect frame = this.mFrame;
        View handle = this.mHandle;
        handle.getHitRect(frame);
        if (this.mTracking || frame.contains((int) x, (int) y)) {
            if (action == 0) {
                this.mTracking = true;
                handle.setPressed(true);
                prepareContent();
                OnDrawerScrollListener onDrawerScrollListener = this.mOnDrawerScrollListener;
                if (onDrawerScrollListener != null) {
                    onDrawerScrollListener.onScrollStarted();
                }
                if (this.mVertical) {
                    int top = this.mHandle.getTop();
                    this.mTouchDelta = ((int) y) - top;
                    prepareTracking(top);
                } else {
                    int left = this.mHandle.getLeft();
                    this.mTouchDelta = ((int) x) - left;
                    prepareTracking(left);
                }
                this.mVelocityTracker.addMovement(event);
            }
            return true;
        }
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        boolean negative;
        if (this.mLocked) {
            return true;
        }
        if (this.mTracking) {
            this.mVelocityTracker.addMovement(event);
            int action = event.getAction();
            switch (action) {
                case 1:
                case 3:
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(this.mVelocityUnits);
                    float yVelocity = velocityTracker.getYVelocity();
                    float xVelocity = velocityTracker.getXVelocity();
                    boolean vertical = this.mVertical;
                    if (vertical) {
                        negative = yVelocity < 0.0f;
                        if (xVelocity < 0.0f) {
                            xVelocity = -xVelocity;
                        }
                        int i = this.mMaximumMinorVelocity;
                        if (xVelocity > i) {
                            xVelocity = i;
                        }
                    } else {
                        negative = xVelocity < 0.0f;
                        if (yVelocity < 0.0f) {
                            yVelocity = -yVelocity;
                        }
                        int i2 = this.mMaximumMinorVelocity;
                        if (yVelocity > i2) {
                            yVelocity = i2;
                        }
                    }
                    float velocity = (float) Math.hypot(xVelocity, yVelocity);
                    if (negative) {
                        velocity = -velocity;
                    }
                    int top = this.mHandle.getTop();
                    int left = this.mHandle.getLeft();
                    if (Math.abs(velocity) < this.mMaximumTapVelocity) {
                        boolean z = this.mExpanded;
                        if (!vertical ? !((!z || left >= this.mTapThreshold + this.mTopOffset) && (z || left <= (((this.mBottomOffset + this.mRight) - this.mLeft) - this.mHandleWidth) - this.mTapThreshold)) : !((!z || top >= this.mTapThreshold + this.mTopOffset) && (z || top <= (((this.mBottomOffset + this.mBottom) - this.mTop) - this.mHandleHeight) - this.mTapThreshold))) {
                            if (this.mAllowSingleTap) {
                                playSoundEffect(0);
                                if (this.mExpanded) {
                                    animateClose(vertical ? top : left, true);
                                    break;
                                } else {
                                    animateOpen(vertical ? top : left, true);
                                    break;
                                }
                            } else {
                                performFling(vertical ? top : left, velocity, false, true);
                                break;
                            }
                        } else {
                            performFling(vertical ? top : left, velocity, false, true);
                            break;
                        }
                    } else {
                        performFling(vertical ? top : left, velocity, false, true);
                        break;
                    }
                case 2:
                    moveHandle(((int) (this.mVertical ? event.getY() : event.getX())) - this.mTouchDelta);
                    break;
            }
        }
        return this.mTracking || this.mAnimating || super.onTouchEvent(event);
    }

    private void animateClose(int position, boolean notifyScrollListener) {
        prepareTracking(position);
        performFling(position, this.mMaximumAcceleration, true, notifyScrollListener);
    }

    private void animateOpen(int position, boolean notifyScrollListener) {
        prepareTracking(position);
        performFling(position, -this.mMaximumAcceleration, true, notifyScrollListener);
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x0061, code lost:
        if (r8 > (-r6.mMaximumMajorVelocity)) goto L34;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void performFling(int position, float velocity, boolean always, boolean notifyScrollListener) {
        this.mAnimationPosition = position;
        this.mAnimatedVelocity = velocity;
        if (this.mExpanded) {
            if (!always) {
                int i = this.mMaximumMajorVelocity;
                if (velocity <= i) {
                    if (position <= this.mTopOffset + (this.mVertical ? this.mHandleHeight : this.mHandleWidth) || velocity <= (-i)) {
                        this.mAnimatedAcceleration = -this.mMaximumAcceleration;
                        if (velocity > 0.0f) {
                            this.mAnimatedVelocity = 0.0f;
                        }
                    }
                }
            }
            this.mAnimatedAcceleration = this.mMaximumAcceleration;
            if (velocity < 0.0f) {
                this.mAnimatedVelocity = 0.0f;
            }
        } else {
            if (!always) {
                if (velocity <= this.mMaximumMajorVelocity) {
                    if (position > (this.mVertical ? getHeight() : getWidth()) / 2) {
                    }
                }
                this.mAnimatedAcceleration = this.mMaximumAcceleration;
                if (velocity < 0.0f) {
                    this.mAnimatedVelocity = 0.0f;
                }
            }
            this.mAnimatedAcceleration = -this.mMaximumAcceleration;
            if (velocity > 0.0f) {
                this.mAnimatedVelocity = 0.0f;
            }
        }
        long now = SystemClock.uptimeMillis();
        this.mAnimationLastTime = now;
        this.mCurrentAnimationTime = now + 16;
        this.mAnimating = true;
        removeCallbacks(this.mSlidingRunnable);
        postDelayed(this.mSlidingRunnable, 16L);
        stopTracking(notifyScrollListener);
    }

    private void prepareTracking(int position) {
        int width;
        int i;
        this.mTracking = true;
        this.mVelocityTracker = VelocityTracker.obtain();
        boolean opening = !this.mExpanded;
        if (opening) {
            this.mAnimatedAcceleration = this.mMaximumAcceleration;
            this.mAnimatedVelocity = this.mMaximumMajorVelocity;
            int i2 = this.mBottomOffset;
            if (this.mVertical) {
                width = getHeight();
                i = this.mHandleHeight;
            } else {
                width = getWidth();
                i = this.mHandleWidth;
            }
            float f = i2 + (width - i);
            this.mAnimationPosition = f;
            moveHandle((int) f);
            this.mAnimating = true;
            removeCallbacks(this.mSlidingRunnable);
            long now = SystemClock.uptimeMillis();
            this.mAnimationLastTime = now;
            this.mCurrentAnimationTime = 16 + now;
            this.mAnimating = true;
            return;
        }
        if (this.mAnimating) {
            this.mAnimating = false;
            removeCallbacks(this.mSlidingRunnable);
        }
        moveHandle(position);
    }

    private void moveHandle(int position) {
        View handle = this.mHandle;
        if (this.mVertical) {
            if (position == -10001) {
                handle.offsetTopAndBottom(this.mTopOffset - handle.getTop());
                invalidate();
            } else if (position == -10002) {
                handle.offsetTopAndBottom((((this.mBottomOffset + this.mBottom) - this.mTop) - this.mHandleHeight) - handle.getTop());
                invalidate();
            } else {
                int top = handle.getTop();
                int deltaY = position - top;
                int i = this.mTopOffset;
                if (position < i) {
                    deltaY = i - top;
                } else if (deltaY > (((this.mBottomOffset + this.mBottom) - this.mTop) - this.mHandleHeight) - top) {
                    deltaY = (((this.mBottomOffset + this.mBottom) - this.mTop) - this.mHandleHeight) - top;
                }
                handle.offsetTopAndBottom(deltaY);
                Rect frame = this.mFrame;
                Rect region = this.mInvalidate;
                handle.getHitRect(frame);
                region.set(frame);
                region.union(frame.left, frame.top - deltaY, frame.right, frame.bottom - deltaY);
                region.union(0, frame.bottom - deltaY, getWidth(), (frame.bottom - deltaY) + this.mContent.getHeight());
                invalidate(region);
            }
        } else if (position == -10001) {
            handle.offsetLeftAndRight(this.mTopOffset - handle.getLeft());
            invalidate();
        } else if (position == -10002) {
            handle.offsetLeftAndRight((((this.mBottomOffset + this.mRight) - this.mLeft) - this.mHandleWidth) - handle.getLeft());
            invalidate();
        } else {
            int left = handle.getLeft();
            int deltaX = position - left;
            int i2 = this.mTopOffset;
            if (position < i2) {
                deltaX = i2 - left;
            } else if (deltaX > (((this.mBottomOffset + this.mRight) - this.mLeft) - this.mHandleWidth) - left) {
                deltaX = (((this.mBottomOffset + this.mRight) - this.mLeft) - this.mHandleWidth) - left;
            }
            handle.offsetLeftAndRight(deltaX);
            Rect frame2 = this.mFrame;
            Rect region2 = this.mInvalidate;
            handle.getHitRect(frame2);
            region2.set(frame2);
            region2.union(frame2.left - deltaX, frame2.top, frame2.right - deltaX, frame2.bottom);
            region2.union(frame2.right - deltaX, 0, (frame2.right - deltaX) + this.mContent.getWidth(), getHeight());
            invalidate(region2);
        }
    }

    private void prepareContent() {
        if (this.mAnimating) {
            return;
        }
        View content = this.mContent;
        if (content.isLayoutRequested()) {
            if (this.mVertical) {
                int childHeight = this.mHandleHeight;
                int height = ((this.mBottom - this.mTop) - childHeight) - this.mTopOffset;
                content.measure(View.MeasureSpec.makeMeasureSpec(this.mRight - this.mLeft, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                content.layout(0, this.mTopOffset + childHeight, content.getMeasuredWidth(), this.mTopOffset + childHeight + content.getMeasuredHeight());
            } else {
                int childWidth = this.mHandle.getWidth();
                int width = ((this.mRight - this.mLeft) - childWidth) - this.mTopOffset;
                content.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mBottom - this.mTop, 1073741824));
                int i = this.mTopOffset;
                content.layout(childWidth + i, 0, i + childWidth + content.getMeasuredWidth(), content.getMeasuredHeight());
            }
        }
        content.getViewTreeObserver().dispatchOnPreDraw();
        if (!content.isHardwareAccelerated()) {
            content.buildDrawingCache();
        }
        content.setVisibility(8);
    }

    private void stopTracking(boolean notifyScrollListener) {
        OnDrawerScrollListener onDrawerScrollListener;
        this.mHandle.setPressed(false);
        this.mTracking = false;
        if (notifyScrollListener && (onDrawerScrollListener = this.mOnDrawerScrollListener) != null) {
            onDrawerScrollListener.onScrollEnded();
        }
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doAnimation() {
        if (this.mAnimating) {
            incrementAnimation();
            if (this.mAnimationPosition >= (this.mBottomOffset + (this.mVertical ? getHeight() : getWidth())) - 1) {
                this.mAnimating = false;
                closeDrawer();
                return;
            }
            float f = this.mAnimationPosition;
            if (f < this.mTopOffset) {
                this.mAnimating = false;
                openDrawer();
                return;
            }
            moveHandle((int) f);
            this.mCurrentAnimationTime += 16;
            postDelayed(this.mSlidingRunnable, 16L);
        }
    }

    private void incrementAnimation() {
        long now = SystemClock.uptimeMillis();
        float t = ((float) (now - this.mAnimationLastTime)) / 1000.0f;
        float position = this.mAnimationPosition;
        float v = this.mAnimatedVelocity;
        float a = this.mAnimatedAcceleration;
        this.mAnimationPosition = (v * t) + position + (0.5f * a * t * t);
        this.mAnimatedVelocity = (a * t) + v;
        this.mAnimationLastTime = now;
    }

    public void toggle() {
        if (!this.mExpanded) {
            openDrawer();
        } else {
            closeDrawer();
        }
        invalidate();
        requestLayout();
    }

    public void animateToggle() {
        if (!this.mExpanded) {
            animateOpen();
        } else {
            animateClose();
        }
    }

    public void open() {
        openDrawer();
        invalidate();
        requestLayout();
        sendAccessibilityEvent(32);
    }

    public void close() {
        closeDrawer();
        invalidate();
        requestLayout();
    }

    public void animateClose() {
        prepareContent();
        OnDrawerScrollListener scrollListener = this.mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }
        animateClose(this.mVertical ? this.mHandle.getTop() : this.mHandle.getLeft(), false);
        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    public void animateOpen() {
        prepareContent();
        OnDrawerScrollListener scrollListener = this.mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }
        animateOpen(this.mVertical ? this.mHandle.getTop() : this.mHandle.getLeft(), false);
        sendAccessibilityEvent(32);
        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return SlidingDrawer.class.getName();
    }

    private void closeDrawer() {
        moveHandle(-10002);
        this.mContent.setVisibility(8);
        this.mContent.destroyDrawingCache();
        if (!this.mExpanded) {
            return;
        }
        this.mExpanded = false;
        OnDrawerCloseListener onDrawerCloseListener = this.mOnDrawerCloseListener;
        if (onDrawerCloseListener != null) {
            onDrawerCloseListener.onDrawerClosed();
        }
    }

    private void openDrawer() {
        moveHandle(-10001);
        this.mContent.setVisibility(0);
        if (this.mExpanded) {
            return;
        }
        this.mExpanded = true;
        OnDrawerOpenListener onDrawerOpenListener = this.mOnDrawerOpenListener;
        if (onDrawerOpenListener != null) {
            onDrawerOpenListener.onDrawerOpened();
        }
    }

    public void setOnDrawerOpenListener(OnDrawerOpenListener onDrawerOpenListener) {
        this.mOnDrawerOpenListener = onDrawerOpenListener;
    }

    public void setOnDrawerCloseListener(OnDrawerCloseListener onDrawerCloseListener) {
        this.mOnDrawerCloseListener = onDrawerCloseListener;
    }

    public void setOnDrawerScrollListener(OnDrawerScrollListener onDrawerScrollListener) {
        this.mOnDrawerScrollListener = onDrawerScrollListener;
    }

    public View getHandle() {
        return this.mHandle;
    }

    public View getContent() {
        return this.mContent;
    }

    public void unlock() {
        this.mLocked = false;
    }

    public void lock() {
        this.mLocked = true;
    }

    public boolean isOpened() {
        return this.mExpanded;
    }

    public boolean isMoving() {
        return this.mTracking || this.mAnimating;
    }

    /* loaded from: classes4.dex */
    private class DrawerToggler implements View.OnClickListener {
        private DrawerToggler() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (SlidingDrawer.this.mLocked) {
                return;
            }
            if (SlidingDrawer.this.mAnimateOnClick) {
                SlidingDrawer.this.animateToggle();
            } else {
                SlidingDrawer.this.toggle();
            }
        }
    }
}
