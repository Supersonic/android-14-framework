package com.android.internal.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.p008os.VibrationAttributes;
import android.p008os.VibrationEffect;
import android.p008os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.C4057R;
/* loaded from: classes5.dex */
public class SlidingTab extends ViewGroup {
    private static final int ANIM_DURATION = 250;
    private static final int ANIM_TARGET_TIME = 500;
    private static final boolean DBG = false;
    private static final int HORIZONTAL = 0;
    private static final String LOG_TAG = "SlidingTab";
    private static final float THRESHOLD = 0.6666667f;
    private static final VibrationAttributes TOUCH_VIBRATION_ATTRIBUTES = VibrationAttributes.createForUsage(18);
    private static final int TRACKING_MARGIN = 50;
    private static final int VERTICAL = 1;
    private static final long VIBRATE_LONG = 40;
    private static final long VIBRATE_SHORT = 30;
    private boolean mAnimating;
    private final Animation.AnimationListener mAnimationDoneListener;
    private Slider mCurrentSlider;
    private final float mDensity;
    private int mGrabbedState;
    private boolean mHoldLeftOnTransition;
    private boolean mHoldRightOnTransition;
    private final Slider mLeftSlider;
    private OnTriggerListener mOnTriggerListener;
    private final int mOrientation;
    private Slider mOtherSlider;
    private final Slider mRightSlider;
    private float mThreshold;
    private final Rect mTmpRect;
    private boolean mTracking;
    private boolean mTriggered;
    private Vibrator mVibrator;

    /* loaded from: classes5.dex */
    public interface OnTriggerListener {
        public static final int LEFT_HANDLE = 1;
        public static final int NO_HANDLE = 0;
        public static final int RIGHT_HANDLE = 2;

        void onGrabbedStateChange(View view, int i);

        void onTrigger(View view, int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class Slider {
        public static final int ALIGN_BOTTOM = 3;
        public static final int ALIGN_LEFT = 0;
        public static final int ALIGN_RIGHT = 1;
        public static final int ALIGN_TOP = 2;
        public static final int ALIGN_UNKNOWN = 4;
        private static final int STATE_ACTIVE = 2;
        private static final int STATE_NORMAL = 0;
        private static final int STATE_PRESSED = 1;
        private int alignment_value;
        private final ImageView tab;
        private final ImageView target;
        private final TextView text;
        private int currentState = 0;
        private int alignment = 4;

        Slider(ViewGroup parent, int tabId, int barId, int targetId) {
            ImageView imageView = new ImageView(parent.getContext());
            this.tab = imageView;
            imageView.setBackgroundResource(tabId);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            TextView textView = new TextView(parent.getContext());
            this.text = textView;
            textView.setLayoutParams(new ViewGroup.LayoutParams(-2, -1));
            textView.setBackgroundResource(barId);
            textView.setTextAppearance(parent.getContext(), C4057R.C4062style.TextAppearance_SlidingTabNormal);
            ImageView imageView2 = new ImageView(parent.getContext());
            this.target = imageView2;
            imageView2.setImageResource(targetId);
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            imageView2.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            imageView2.setVisibility(4);
            parent.addView(imageView2);
            parent.addView(imageView);
            parent.addView(textView);
        }

        void setIcon(int iconId) {
            this.tab.setImageResource(iconId);
        }

        void setTabBackgroundResource(int tabId) {
            this.tab.setBackgroundResource(tabId);
        }

        void setBarBackgroundResource(int barId) {
            this.text.setBackgroundResource(barId);
        }

        void setHintText(int resId) {
            this.text.setText(resId);
        }

        void hide() {
            int dx;
            int i = this.alignment;
            int dy = 0;
            boolean horiz = i == 0 || i == 1;
            if (horiz) {
                dx = i == 0 ? this.alignment_value - this.tab.getRight() : this.alignment_value - this.tab.getLeft();
            } else {
                dx = 0;
            }
            if (!horiz) {
                dy = this.alignment == 2 ? this.alignment_value - this.tab.getBottom() : this.alignment_value - this.tab.getTop();
            }
            Animation trans = new TranslateAnimation(0.0f, dx, 0.0f, dy);
            trans.setDuration(250L);
            trans.setFillAfter(true);
            this.tab.startAnimation(trans);
            this.text.startAnimation(trans);
            this.target.setVisibility(4);
        }

        void show(boolean animate) {
            this.text.setVisibility(0);
            this.tab.setVisibility(0);
            if (animate) {
                int i = this.alignment;
                boolean horiz = true;
                if (i != 0 && i != 1) {
                    horiz = false;
                }
                int dx = horiz ? i == 0 ? this.tab.getWidth() : -this.tab.getWidth() : 0;
                int dy = horiz ? 0 : this.alignment == 2 ? this.tab.getHeight() : -this.tab.getHeight();
                Animation trans = new TranslateAnimation(-dx, 0.0f, -dy, 0.0f);
                trans.setDuration(250L);
                this.tab.startAnimation(trans);
                this.text.startAnimation(trans);
            }
        }

        void setState(int state) {
            this.text.setPressed(state == 1);
            this.tab.setPressed(state == 1);
            if (state == 2) {
                int[] activeState = {16842914};
                if (this.text.getBackground().isStateful()) {
                    this.text.getBackground().setState(activeState);
                }
                if (this.tab.getBackground().isStateful()) {
                    this.tab.getBackground().setState(activeState);
                }
                TextView textView = this.text;
                textView.setTextAppearance(textView.getContext(), C4057R.C4062style.TextAppearance_SlidingTabActive);
            } else {
                TextView textView2 = this.text;
                textView2.setTextAppearance(textView2.getContext(), C4057R.C4062style.TextAppearance_SlidingTabNormal);
            }
            this.currentState = state;
        }

        void showTarget() {
            AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
            alphaAnim.setDuration(500L);
            this.target.startAnimation(alphaAnim);
            this.target.setVisibility(0);
        }

        void reset(boolean animate) {
            int dx;
            int dy;
            setState(0);
            this.text.setVisibility(0);
            TextView textView = this.text;
            textView.setTextAppearance(textView.getContext(), C4057R.C4062style.TextAppearance_SlidingTabNormal);
            this.tab.setVisibility(0);
            this.target.setVisibility(4);
            int i = this.alignment;
            boolean horiz = true;
            if (i != 0 && i != 1) {
                horiz = false;
            }
            if (!horiz) {
                dx = 0;
            } else {
                dx = i == 0 ? this.alignment_value - this.tab.getLeft() : this.alignment_value - this.tab.getRight();
            }
            if (horiz) {
                dy = 0;
            } else {
                dy = this.alignment == 2 ? this.alignment_value - this.tab.getTop() : this.alignment_value - this.tab.getBottom();
            }
            if (animate) {
                TranslateAnimation trans = new TranslateAnimation(0.0f, dx, 0.0f, dy);
                trans.setDuration(250L);
                trans.setFillAfter(false);
                this.text.startAnimation(trans);
                this.tab.startAnimation(trans);
                return;
            }
            if (horiz) {
                this.text.offsetLeftAndRight(dx);
                this.tab.offsetLeftAndRight(dx);
            } else {
                this.text.offsetTopAndBottom(dy);
                this.tab.offsetTopAndBottom(dy);
            }
            this.text.clearAnimation();
            this.tab.clearAnimation();
            this.target.clearAnimation();
        }

        void setTarget(int targetId) {
            this.target.setImageResource(targetId);
        }

        void layout(int l, int t, int r, int b, int alignment) {
            int handleWidth;
            int targetWidth;
            int parentWidth;
            int leftTarget;
            int rightTarget;
            this.alignment = alignment;
            Drawable tabBackground = this.tab.getBackground();
            int handleWidth2 = tabBackground.getIntrinsicWidth();
            int handleHeight = tabBackground.getIntrinsicHeight();
            Drawable targetDrawable = this.target.getDrawable();
            int targetWidth2 = targetDrawable.getIntrinsicWidth();
            int targetHeight = targetDrawable.getIntrinsicHeight();
            int parentWidth2 = r - l;
            int parentHeight = b - t;
            int leftTarget2 = (((int) (parentWidth2 * SlidingTab.THRESHOLD)) - targetWidth2) + (handleWidth2 / 2);
            int rightTarget2 = ((int) (parentWidth2 * 0.3333333f)) - (handleWidth2 / 2);
            int left = (parentWidth2 - handleWidth2) / 2;
            int right = left + handleWidth2;
            if (alignment == 0) {
                handleWidth = handleWidth2;
                targetWidth = targetWidth2;
                parentWidth = parentWidth2;
                leftTarget = leftTarget2;
                rightTarget = rightTarget2;
            } else if (alignment != 1) {
                int targetLeft = (parentWidth2 - targetWidth2) / 2;
                int rightTarget3 = (parentWidth2 + targetWidth2) / 2;
                int top = (((int) (parentHeight * SlidingTab.THRESHOLD)) + (handleHeight / 2)) - targetHeight;
                int bottom = ((int) (parentHeight * 0.3333333f)) - (handleHeight / 2);
                if (alignment != 2) {
                    this.tab.layout(left, parentHeight - handleHeight, right, parentHeight);
                    this.text.layout(left, parentHeight, right, parentHeight + parentHeight);
                    this.target.layout(targetLeft, bottom, rightTarget3, bottom + targetHeight);
                    this.alignment_value = b;
                    return;
                }
                this.tab.layout(left, 0, right, handleHeight);
                this.text.layout(left, 0 - parentHeight, right, 0);
                this.target.layout(targetLeft, top, rightTarget3, top + targetHeight);
                this.alignment_value = t;
                return;
            } else {
                handleWidth = handleWidth2;
                targetWidth = targetWidth2;
                parentWidth = parentWidth2;
                leftTarget = leftTarget2;
                rightTarget = rightTarget2;
            }
            int targetTop = (parentHeight - targetHeight) / 2;
            int targetBottom = targetTop + targetHeight;
            int top2 = (parentHeight - handleHeight) / 2;
            int bottom2 = (parentHeight + handleHeight) / 2;
            if (alignment != 0) {
                int parentWidth3 = parentWidth;
                this.tab.layout(parentWidth - handleWidth, top2, parentWidth3, bottom2);
                this.text.layout(parentWidth3, top2, parentWidth3 + parentWidth3, bottom2);
                this.target.layout(rightTarget, targetTop, rightTarget + targetWidth, targetBottom);
                this.text.setGravity(48);
                this.alignment_value = r;
                return;
            }
            this.tab.layout(0, top2, handleWidth, bottom2);
            this.text.layout(0 - parentWidth, top2, 0, bottom2);
            this.text.setGravity(5);
            this.target.layout(leftTarget, targetTop, leftTarget + targetWidth, targetBottom);
            this.alignment_value = l;
        }

        public void updateDrawableStates() {
            setState(this.currentState);
        }

        public void measure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            this.tab.measure(View.MeasureSpec.makeSafeMeasureSpec(width, 0), View.MeasureSpec.makeSafeMeasureSpec(height, 0));
            this.text.measure(View.MeasureSpec.makeSafeMeasureSpec(width, 0), View.MeasureSpec.makeSafeMeasureSpec(height, 0));
        }

        public int getTabWidth() {
            return this.tab.getMeasuredWidth();
        }

        public int getTabHeight() {
            return this.tab.getMeasuredHeight();
        }

        public void startAnimation(Animation anim1, Animation anim2) {
            this.tab.startAnimation(anim1);
            this.text.startAnimation(anim2);
        }

        public void hideTarget() {
            this.target.clearAnimation();
            this.target.setVisibility(4);
        }
    }

    public SlidingTab(Context context) {
        this(context, null);
    }

    public SlidingTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mHoldLeftOnTransition = true;
        this.mHoldRightOnTransition = true;
        this.mGrabbedState = 0;
        this.mTriggered = false;
        this.mAnimationDoneListener = new Animation.AnimationListener() { // from class: com.android.internal.widget.SlidingTab.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                SlidingTab.this.onAnimationDone();
            }
        };
        this.mTmpRect = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.SlidingTab);
        this.mOrientation = a.getInt(0, 0);
        a.recycle();
        Resources r = getResources();
        this.mDensity = r.getDisplayMetrics().density;
        this.mLeftSlider = new Slider(this, C4057R.C4058drawable.jog_tab_left_generic, C4057R.C4058drawable.jog_tab_bar_left_generic, C4057R.C4058drawable.jog_tab_target_gray);
        this.mRightSlider = new Slider(this, C4057R.C4058drawable.jog_tab_right_generic, C4057R.C4058drawable.jog_tab_bar_right_generic, C4057R.C4058drawable.jog_tab_target_gray);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        this.mLeftSlider.measure(widthMeasureSpec, heightMeasureSpec);
        this.mRightSlider.measure(widthMeasureSpec, heightMeasureSpec);
        int leftTabWidth = this.mLeftSlider.getTabWidth();
        int rightTabWidth = this.mRightSlider.getTabWidth();
        int leftTabHeight = this.mLeftSlider.getTabHeight();
        int rightTabHeight = this.mRightSlider.getTabHeight();
        if (isHorizontal()) {
            width = Math.max(widthSpecSize, leftTabWidth + rightTabWidth);
            height = Math.max(leftTabHeight, rightTabHeight);
        } else {
            width = Math.max(leftTabWidth, rightTabHeight);
            height = Math.max(heightSpecSize, leftTabHeight + rightTabHeight);
        }
        setMeasuredDimension(width, height);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (this.mAnimating) {
            return false;
        }
        View leftHandle = this.mLeftSlider.tab;
        leftHandle.getHitRect(this.mTmpRect);
        boolean leftHit = this.mTmpRect.contains((int) x, (int) y);
        View rightHandle = this.mRightSlider.tab;
        rightHandle.getHitRect(this.mTmpRect);
        boolean rightHit = this.mTmpRect.contains((int) x, (int) y);
        if (this.mTracking || leftHit || rightHit) {
            switch (action) {
                case 0:
                    this.mTracking = true;
                    this.mTriggered = false;
                    vibrate(VIBRATE_SHORT);
                    if (leftHit) {
                        this.mCurrentSlider = this.mLeftSlider;
                        this.mOtherSlider = this.mRightSlider;
                        this.mThreshold = isHorizontal() ? 0.6666667f : 0.3333333f;
                        setGrabbedState(1);
                    } else {
                        this.mCurrentSlider = this.mRightSlider;
                        this.mOtherSlider = this.mLeftSlider;
                        this.mThreshold = isHorizontal() ? 0.3333333f : 0.6666667f;
                        setGrabbedState(2);
                    }
                    this.mCurrentSlider.setState(1);
                    this.mCurrentSlider.showTarget();
                    this.mOtherSlider.hide();
                    break;
            }
            return true;
        }
        return false;
    }

    public void reset(boolean animate) {
        this.mLeftSlider.reset(animate);
        this.mRightSlider.reset(animate);
        if (!animate) {
            this.mAnimating = false;
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        if (visibility != getVisibility() && visibility == 4) {
            reset(false);
        }
        super.setVisibility(visibility);
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0051, code lost:
        r7 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0064, code lost:
        r7 = true;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean onTouchEvent(MotionEvent event) {
        boolean thresholdReached;
        if (this.mTracking) {
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();
            switch (action) {
                case 2:
                    if (withinView(x, y, this)) {
                        moveHandle(x, y);
                        float position = isHorizontal() ? x : y;
                        float target = this.mThreshold * (isHorizontal() ? getWidth() : getHeight());
                        if (isHorizontal()) {
                            if (this.mCurrentSlider != this.mLeftSlider) {
                                thresholdReached = false;
                                break;
                            } else {
                                thresholdReached = false;
                                break;
                            }
                        } else if (this.mCurrentSlider != this.mLeftSlider) {
                            thresholdReached = false;
                            break;
                        } else {
                            thresholdReached = false;
                            break;
                        }
                        if (!this.mTriggered && thresholdReached) {
                            this.mTriggered = true;
                            this.mTracking = false;
                            this.mCurrentSlider.setState(2);
                            boolean isLeft = this.mCurrentSlider == this.mLeftSlider;
                            dispatchTriggerEvent(isLeft ? 1 : 2);
                            startAnimating(isLeft ? this.mHoldLeftOnTransition : this.mHoldRightOnTransition);
                            setGrabbedState(0);
                            break;
                        }
                    }
                    break;
                case 1:
                case 3:
                    cancelGrab();
                    break;
            }
        }
        return this.mTracking || super.onTouchEvent(event);
    }

    private void cancelGrab() {
        this.mTracking = false;
        this.mTriggered = false;
        this.mOtherSlider.show(true);
        this.mCurrentSlider.reset(false);
        this.mCurrentSlider.hideTarget();
        this.mCurrentSlider = null;
        this.mOtherSlider = null;
        setGrabbedState(0);
    }

    void startAnimating(final boolean holdAfter) {
        int holdOffset;
        final int dx;
        final int right;
        this.mAnimating = true;
        Slider slider = this.mCurrentSlider;
        Slider slider2 = this.mOtherSlider;
        if (isHorizontal()) {
            int right2 = slider.tab.getRight();
            int width = slider.tab.getWidth();
            int left = slider.tab.getLeft();
            int viewWidth = getWidth();
            holdOffset = holdAfter ? 0 : width;
            dx = slider == this.mRightSlider ? -((right2 + viewWidth) - holdOffset) : ((viewWidth - left) + viewWidth) - holdOffset;
            right = 0;
        } else {
            int top = slider.tab.getTop();
            int bottom = slider.tab.getBottom();
            int height = slider.tab.getHeight();
            int viewHeight = getHeight();
            holdOffset = holdAfter ? 0 : height;
            dx = 0;
            right = slider == this.mRightSlider ? (top + viewHeight) - holdOffset : -(((viewHeight - bottom) + viewHeight) - holdOffset);
        }
        Animation trans1 = new TranslateAnimation(0.0f, dx, 0.0f, right);
        trans1.setDuration(250L);
        trans1.setInterpolator(new LinearInterpolator());
        trans1.setFillAfter(true);
        Animation trans2 = new TranslateAnimation(0.0f, dx, 0.0f, right);
        trans2.setDuration(250L);
        trans2.setInterpolator(new LinearInterpolator());
        trans2.setFillAfter(true);
        trans1.setAnimationListener(new Animation.AnimationListener() { // from class: com.android.internal.widget.SlidingTab.2
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                Animation anim;
                if (holdAfter) {
                    int i = dx;
                    int i2 = right;
                    anim = new TranslateAnimation(i, i, i2, i2);
                    anim.setDuration(1000L);
                    SlidingTab.this.mAnimating = false;
                } else {
                    anim = new AlphaAnimation(0.5f, 1.0f);
                    anim.setDuration(250L);
                    SlidingTab.this.resetView();
                }
                anim.setAnimationListener(SlidingTab.this.mAnimationDoneListener);
                SlidingTab.this.mLeftSlider.startAnimation(anim, anim);
                SlidingTab.this.mRightSlider.startAnimation(anim, anim);
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
            }
        });
        slider.hideTarget();
        slider.startAnimation(trans1, trans2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAnimationDone() {
        resetView();
        this.mAnimating = false;
    }

    private boolean withinView(float x, float y, View view) {
        return (isHorizontal() && y > -50.0f && y < ((float) (view.getHeight() + 50))) || (!isHorizontal() && x > -50.0f && x < ((float) (view.getWidth() + 50)));
    }

    private boolean isHorizontal() {
        return this.mOrientation == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetView() {
        this.mLeftSlider.reset(false);
        this.mRightSlider.reset(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            this.mLeftSlider.layout(l, t, r, b, isHorizontal() ? 0 : 3);
            this.mRightSlider.layout(l, t, r, b, isHorizontal() ? 1 : 2);
        }
    }

    private void moveHandle(float x, float y) {
        View handle = this.mCurrentSlider.tab;
        View content = this.mCurrentSlider.text;
        if (isHorizontal()) {
            int deltaX = (((int) x) - handle.getLeft()) - (handle.getWidth() / 2);
            handle.offsetLeftAndRight(deltaX);
            content.offsetLeftAndRight(deltaX);
        } else {
            int deltaY = (((int) y) - handle.getTop()) - (handle.getHeight() / 2);
            handle.offsetTopAndBottom(deltaY);
            content.offsetTopAndBottom(deltaY);
        }
        invalidate();
    }

    public void setLeftTabResources(int iconId, int targetId, int barId, int tabId) {
        this.mLeftSlider.setIcon(iconId);
        this.mLeftSlider.setTarget(targetId);
        this.mLeftSlider.setBarBackgroundResource(barId);
        this.mLeftSlider.setTabBackgroundResource(tabId);
        this.mLeftSlider.updateDrawableStates();
    }

    public void setLeftHintText(int resId) {
        if (isHorizontal()) {
            this.mLeftSlider.setHintText(resId);
        }
    }

    public void setRightTabResources(int iconId, int targetId, int barId, int tabId) {
        this.mRightSlider.setIcon(iconId);
        this.mRightSlider.setTarget(targetId);
        this.mRightSlider.setBarBackgroundResource(barId);
        this.mRightSlider.setTabBackgroundResource(tabId);
        this.mRightSlider.updateDrawableStates();
    }

    public void setRightHintText(int resId) {
        if (isHorizontal()) {
            this.mRightSlider.setHintText(resId);
        }
    }

    public void setHoldAfterTrigger(boolean holdLeft, boolean holdRight) {
        this.mHoldLeftOnTransition = holdLeft;
        this.mHoldRightOnTransition = holdRight;
    }

    private synchronized void vibrate(long duration) {
        if (this.mVibrator == null) {
            this.mVibrator = (Vibrator) getContext().getSystemService(Vibrator.class);
        }
        this.mVibrator.vibrate(VibrationEffect.createOneShot(duration, -1), TOUCH_VIBRATION_ATTRIBUTES);
    }

    public void setOnTriggerListener(OnTriggerListener listener) {
        this.mOnTriggerListener = listener;
    }

    private void dispatchTriggerEvent(int whichHandle) {
        vibrate(VIBRATE_LONG);
        OnTriggerListener onTriggerListener = this.mOnTriggerListener;
        if (onTriggerListener != null) {
            onTriggerListener.onTrigger(this, whichHandle);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && visibility != 0 && this.mGrabbedState != 0) {
            cancelGrab();
        }
    }

    private void setGrabbedState(int newState) {
        if (newState != this.mGrabbedState) {
            this.mGrabbedState = newState;
            OnTriggerListener onTriggerListener = this.mOnTriggerListener;
            if (onTriggerListener != null) {
                onTriggerListener.onGrabbedStateChange(this, newState);
            }
        }
    }

    private void log(String msg) {
        Log.m112d(LOG_TAG, msg);
    }
}
