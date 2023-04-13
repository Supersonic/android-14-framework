package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormatSymbols;
import android.p008os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
/* loaded from: classes4.dex */
public class NumberPicker extends LinearLayout {
    private static final int DEFAULT_LAYOUT_RESOURCE_ID = 17367248;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 1;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private AccessibilityNodeProviderImpl mAccessibilityNodeProvider;
    private final Scroller mAdjustScroller;
    private BeginSoftInputOnLongPressCommand mBeginSoftInputOnLongPressCommand;
    private int mBottomSelectionDividerBottom;
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    private final boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    private final ImageButton mDecrementButton;
    private boolean mDecrementVirtualButtonPressed;
    private String[] mDisplayedValues;
    private final Scroller mFlingScroller;
    private Formatter mFormatter;
    private final boolean mHasSelectorWheel;
    private boolean mHideWheelUntilFocused;
    private boolean mIgnoreMoveEvents;
    private final ImageButton mIncrementButton;
    private boolean mIncrementVirtualButtonPressed;
    private int mInitialScrollOffset;
    private final EditText mInputText;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval;
    private final int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private final int mMinHeight;
    private int mMinValue;
    private final int mMinWidth;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private boolean mPerformClickOnTap;
    private final PressedStateHelper mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState;
    private final Drawable mSelectionDivider;
    private int mSelectionDividerHeight;
    private final int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private final Paint mSelectorWheelPaint;
    private SetSelectionCommand mSetSelectionCommand;
    private final int mSolidColor;
    private final int mTextSize;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private final Drawable mVirtualButtonPressedDrawable;
    private boolean mWrapSelectorWheel;
    private boolean mWrapSelectorWheelPreferred;
    private static final TwoDigitFormatter sTwoDigitFormatter = new TwoDigitFormatter();
    private static final char[] DIGIT_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641, 1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785, 2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415, 2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543, 3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311};

    /* loaded from: classes4.dex */
    public interface Formatter {
        String format(int i);
    }

    /* loaded from: classes4.dex */
    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes4.dex */
        public @interface ScrollState {
        }

        void onScrollStateChange(NumberPicker numberPicker, int i);
    }

    /* loaded from: classes4.dex */
    public interface OnValueChangeListener {
        void onValueChange(NumberPicker numberPicker, int i, int i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class TwoDigitFormatter implements Formatter {
        java.util.Formatter mFmt;
        char mZeroDigit;
        final StringBuilder mBuilder = new StringBuilder();
        final Object[] mArgs = new Object[1];

        TwoDigitFormatter() {
            Locale locale = Locale.getDefault();
            init(locale);
        }

        private void init(Locale locale) {
            this.mFmt = createFormatter(locale);
            this.mZeroDigit = getZeroDigit(locale);
        }

        @Override // android.widget.NumberPicker.Formatter
        public String format(int value) {
            Locale currentLocale = Locale.getDefault();
            if (this.mZeroDigit != getZeroDigit(currentLocale)) {
                init(currentLocale);
            }
            this.mArgs[0] = Integer.valueOf(value);
            StringBuilder sb = this.mBuilder;
            sb.delete(0, sb.length());
            this.mFmt.format("%02d", this.mArgs);
            return this.mFmt.toString();
        }

        private static char getZeroDigit(Locale locale) {
            return DecimalFormatSymbols.getInstance(locale).getZeroDigit();
        }

        private java.util.Formatter createFormatter(Locale locale) {
            return new java.util.Formatter(this.mBuilder, locale);
        }
    }

    public static final Formatter getTwoDigitFormatter() {
        return sTwoDigitFormatter;
    }

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 16844068);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v16 */
    /* JADX WARN: Type inference failed for: r0v17, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r0v18 */
    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        boolean z;
        boolean z2;
        ?? r0;
        this.mWrapSelectorWheelPreferred = true;
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray<>();
        this.mSelectorIndices = new int[3];
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = 0;
        this.mLastHandledDownDpadKeyCode = -1;
        TypedArray attributesArray = context.obtainStyledAttributes(attrs, C4057R.styleable.NumberPicker, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.NumberPicker, attrs, attributesArray, defStyleAttr, defStyleRes);
        int layoutResId = attributesArray.getResourceId(3, 17367248);
        if (layoutResId != 17367248) {
            z = true;
        } else {
            z = false;
        }
        this.mHasSelectorWheel = z;
        this.mHideWheelUntilFocused = attributesArray.getBoolean(2, false);
        this.mSolidColor = attributesArray.getColor(0, 0);
        Drawable selectionDivider = attributesArray.getDrawable(8);
        if (selectionDivider != null) {
            selectionDivider.setCallback(this);
            selectionDivider.setLayoutDirection(getLayoutDirection());
            if (selectionDivider.isStateful()) {
                selectionDivider.setState(getDrawableState());
            }
        }
        this.mSelectionDivider = selectionDivider;
        int defSelectionDividerHeight = (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics());
        this.mSelectionDividerHeight = attributesArray.getDimensionPixelSize(1, defSelectionDividerHeight);
        int defSelectionDividerDistance = (int) TypedValue.applyDimension(1, 48.0f, getResources().getDisplayMetrics());
        this.mSelectionDividersDistance = attributesArray.getDimensionPixelSize(9, defSelectionDividerDistance);
        int dimensionPixelSize = attributesArray.getDimensionPixelSize(6, -1);
        this.mMinHeight = dimensionPixelSize;
        int dimensionPixelSize2 = attributesArray.getDimensionPixelSize(4, -1);
        this.mMaxHeight = dimensionPixelSize2;
        if (dimensionPixelSize != -1 && dimensionPixelSize2 != -1 && dimensionPixelSize > dimensionPixelSize2) {
            throw new IllegalArgumentException("minHeight > maxHeight");
        }
        int dimensionPixelSize3 = attributesArray.getDimensionPixelSize(7, -1);
        this.mMinWidth = dimensionPixelSize3;
        int dimensionPixelSize4 = attributesArray.getDimensionPixelSize(5, -1);
        this.mMaxWidth = dimensionPixelSize4;
        if (dimensionPixelSize3 != -1 && dimensionPixelSize4 != -1 && dimensionPixelSize3 > dimensionPixelSize4) {
            throw new IllegalArgumentException("minWidth > maxWidth");
        }
        if (dimensionPixelSize4 == -1) {
            z2 = true;
        } else {
            z2 = false;
        }
        this.mComputeMaxWidth = z2;
        this.mVirtualButtonPressedDrawable = attributesArray.getDrawable(10);
        attributesArray.recycle();
        this.mPressedStateHelper = new PressedStateHelper();
        setWillNotDraw(!z);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResId, (ViewGroup) this, true);
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: android.widget.NumberPicker.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                NumberPicker.this.hideSoftInput();
                NumberPicker.this.mInputText.clearFocus();
                if (v.getId() == 16909124) {
                    NumberPicker.this.changeValueByOne(true);
                } else {
                    NumberPicker.this.changeValueByOne(false);
                }
            }
        };
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() { // from class: android.widget.NumberPicker.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                NumberPicker.this.hideSoftInput();
                NumberPicker.this.mInputText.clearFocus();
                if (v.getId() == 16909124) {
                    NumberPicker.this.postChangeCurrentByOneFromLongPress(true, 0L);
                } else {
                    NumberPicker.this.postChangeCurrentByOneFromLongPress(false, 0L);
                }
                return true;
            }
        };
        if (!z) {
            ImageButton imageButton = (ImageButton) findViewById(C4057R.C4059id.increment);
            this.mIncrementButton = imageButton;
            imageButton.setOnClickListener(onClickListener);
            imageButton.setOnLongClickListener(onLongClickListener);
        } else {
            this.mIncrementButton = null;
        }
        if (!z) {
            ImageButton imageButton2 = (ImageButton) findViewById(C4057R.C4059id.decrement);
            this.mDecrementButton = imageButton2;
            imageButton2.setOnClickListener(onClickListener);
            imageButton2.setOnLongClickListener(onLongClickListener);
        } else {
            this.mDecrementButton = null;
        }
        EditText editText = (EditText) findViewById(C4057R.C4059id.numberpicker_input);
        this.mInputText = editText;
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: android.widget.NumberPicker.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    NumberPicker.this.mInputText.selectAll();
                    return;
                }
                NumberPicker.this.mInputText.setSelection(0, 0);
                NumberPicker.this.validateInputTextView(v);
            }
        });
        editText.setFilters(new InputFilter[]{new InputTextFilter()});
        editText.setRawInputType(2);
        editText.setImeOptions(6);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity() / 8;
        int textSize = (int) editText.getTextSize();
        this.mTextSize = textSize;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);
        paint.setTypeface(editText.getTypeface());
        ColorStateList colors = editText.getTextColors();
        int color = colors.getColorForState(ENABLED_STATE_SET, -1);
        paint.setColor(color);
        this.mSelectorWheelPaint = paint;
        this.mFlingScroller = new Scroller(getContext(), null, true);
        this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
        updateInputTextView();
        if (getImportantForAccessibility() != 0) {
            r0 = 1;
        } else {
            r0 = 1;
            setImportantForAccessibility(1);
        }
        if (getFocusable() == 16) {
            setFocusable((int) r0);
            setFocusableInTouchMode(r0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!this.mHasSelectorWheel) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        int msrdWdth = getMeasuredWidth();
        int msrdHght = getMeasuredHeight();
        int inptTxtMsrdWdth = this.mInputText.getMeasuredWidth();
        int inptTxtMsrdHght = this.mInputText.getMeasuredHeight();
        int inptTxtLeft = (msrdWdth - inptTxtMsrdWdth) / 2;
        int inptTxtTop = (msrdHght - inptTxtMsrdHght) / 2;
        int inptTxtRight = inptTxtLeft + inptTxtMsrdWdth;
        int inptTxtBottom = inptTxtTop + inptTxtMsrdHght;
        this.mInputText.layout(inptTxtLeft, inptTxtTop, inptTxtRight, inptTxtBottom);
        if (changed) {
            initializeSelectorWheel();
            initializeFadingEdges();
            int height = getHeight();
            int i = this.mSelectionDividersDistance;
            int i2 = this.mSelectionDividerHeight;
            int i3 = ((height - i) / 2) - i2;
            this.mTopSelectionDividerTop = i3;
            this.mBottomSelectionDividerBottom = i3 + (i2 * 2) + i;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!this.mHasSelectorWheel) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int newWidthMeasureSpec = makeMeasureSpec(widthMeasureSpec, this.mMaxWidth);
        int newHeightMeasureSpec = makeMeasureSpec(heightMeasureSpec, this.mMaxHeight);
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec);
        int widthSize = resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), widthMeasureSpec);
        int heightSize = resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int amountToScroll = scroller.getFinalY() - scroller.getCurrY();
        int futureScrollOffset = (this.mCurrentScrollOffset + amountToScroll) % this.mSelectorElementHeight;
        int overshootAdjustment = this.mInitialScrollOffset - futureScrollOffset;
        if (overshootAdjustment == 0) {
            return false;
        }
        int abs = Math.abs(overshootAdjustment);
        int i = this.mSelectorElementHeight;
        if (abs > i / 2) {
            if (overshootAdjustment > 0) {
                overshootAdjustment -= i;
            } else {
                overshootAdjustment += i;
            }
        }
        scrollBy(0, amountToScroll + overshootAdjustment);
        return true;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.mHasSelectorWheel && isEnabled()) {
            int action = event.getActionMasked();
            switch (action) {
                case 0:
                    removeAllCallbacks();
                    hideSoftInput();
                    float y = event.getY();
                    this.mLastDownEventY = y;
                    this.mLastDownOrMoveEventY = y;
                    this.mLastDownEventTime = event.getEventTime();
                    this.mIgnoreMoveEvents = false;
                    this.mPerformClickOnTap = false;
                    float f = this.mLastDownEventY;
                    if (f < this.mTopSelectionDividerTop) {
                        if (this.mScrollState == 0) {
                            this.mPressedStateHelper.buttonPressDelayed(2);
                        }
                    } else if (f > this.mBottomSelectionDividerBottom && this.mScrollState == 0) {
                        this.mPressedStateHelper.buttonPressDelayed(1);
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (!this.mFlingScroller.isFinished()) {
                        this.mFlingScroller.forceFinished(true);
                        this.mAdjustScroller.forceFinished(true);
                        onScrollerFinished(this.mFlingScroller);
                        onScrollStateChange(0);
                    } else if (!this.mAdjustScroller.isFinished()) {
                        this.mFlingScroller.forceFinished(true);
                        this.mAdjustScroller.forceFinished(true);
                        onScrollerFinished(this.mAdjustScroller);
                    } else {
                        float f2 = this.mLastDownEventY;
                        if (f2 < this.mTopSelectionDividerTop) {
                            postChangeCurrentByOneFromLongPress(false, ViewConfiguration.getLongPressTimeout());
                        } else if (f2 > this.mBottomSelectionDividerBottom) {
                            postChangeCurrentByOneFromLongPress(true, ViewConfiguration.getLongPressTimeout());
                        } else {
                            this.mPerformClickOnTap = true;
                            postBeginSoftInputOnLongPressCommand();
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled() && this.mHasSelectorWheel) {
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(event);
            int action = event.getActionMasked();
            switch (action) {
                case 1:
                    removeBeginSoftInputCommand();
                    removeChangeCurrentByOneFromLongPress();
                    this.mPressedStateHelper.cancel();
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                    int initialVelocity = (int) velocityTracker.getYVelocity();
                    if (Math.abs(initialVelocity) > this.mMinimumFlingVelocity) {
                        fling(initialVelocity);
                        onScrollStateChange(2);
                    } else {
                        int eventY = (int) event.getY();
                        int deltaMoveY = (int) Math.abs(eventY - this.mLastDownEventY);
                        long deltaTime = event.getEventTime() - this.mLastDownEventTime;
                        if (deltaMoveY <= this.mTouchSlop && deltaTime < ViewConfiguration.getTapTimeout()) {
                            if (!this.mPerformClickOnTap) {
                                int selectorIndexOffset = (eventY / this.mSelectorElementHeight) - 1;
                                if (selectorIndexOffset > 0) {
                                    changeValueByOne(true);
                                    this.mPressedStateHelper.buttonTapped(1);
                                } else if (selectorIndexOffset < 0) {
                                    changeValueByOne(false);
                                    this.mPressedStateHelper.buttonTapped(2);
                                }
                            } else {
                                this.mPerformClickOnTap = false;
                                performClick();
                            }
                        } else {
                            ensureScrollWheelAdjusted();
                        }
                        onScrollStateChange(0);
                    }
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    break;
                case 2:
                    if (!this.mIgnoreMoveEvents) {
                        float currentMoveY = event.getY();
                        if (this.mScrollState != 1) {
                            int deltaDownY = (int) Math.abs(currentMoveY - this.mLastDownEventY);
                            if (deltaDownY > this.mTouchSlop) {
                                removeAllCallbacks();
                                onScrollStateChange(1);
                            }
                        } else {
                            int deltaMoveY2 = (int) (currentMoveY - this.mLastDownOrMoveEventY);
                            scrollBy(0, deltaMoveY2);
                            invalidate();
                        }
                        this.mLastDownOrMoveEventY = currentMoveY;
                        break;
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case 1:
            case 3:
                removeAllCallbacks();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case 19:
            case 20:
                if (this.mHasSelectorWheel) {
                    switch (event.getAction()) {
                        case 0:
                            if (this.mWrapSelectorWheel || (keyCode != 20 ? getValue() > getMinValue() : getValue() < getMaxValue())) {
                                requestFocus();
                                this.mLastHandledDownDpadKeyCode = keyCode;
                                removeAllCallbacks();
                                if (this.mFlingScroller.isFinished()) {
                                    changeValueByOne(keyCode == 20);
                                }
                                return true;
                            }
                        case 1:
                            if (this.mLastHandledDownDpadKeyCode == keyCode) {
                                this.mLastHandledDownDpadKeyCode = -1;
                                return true;
                            }
                            break;
                    }
                }
                break;
            case 23:
            case 66:
            case 160:
                removeAllCallbacks();
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTrackballEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case 1:
            case 3:
                removeAllCallbacks();
                break;
        }
        return super.dispatchTrackballEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchHoverEvent(MotionEvent event) {
        int hoveredVirtualViewId;
        if (!this.mHasSelectorWheel) {
            return super.dispatchHoverEvent(event);
        }
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            int eventY = (int) event.getY();
            if (eventY < this.mTopSelectionDividerTop) {
                hoveredVirtualViewId = 3;
            } else {
                int hoveredVirtualViewId2 = this.mBottomSelectionDividerBottom;
                if (eventY > hoveredVirtualViewId2) {
                    hoveredVirtualViewId = 1;
                } else {
                    hoveredVirtualViewId = 2;
                }
            }
            int action = event.getActionMasked();
            AccessibilityNodeProviderImpl provider = (AccessibilityNodeProviderImpl) getAccessibilityNodeProvider();
            switch (action) {
                case 7:
                    int i = this.mLastHoveredChildVirtualViewId;
                    if (i != hoveredVirtualViewId && i != -1) {
                        provider.sendAccessibilityEventForVirtualView(i, 256);
                        provider.sendAccessibilityEventForVirtualView(hoveredVirtualViewId, 128);
                        this.mLastHoveredChildVirtualViewId = hoveredVirtualViewId;
                        provider.performAction(hoveredVirtualViewId, 64, null);
                        return false;
                    }
                    return false;
                case 8:
                default:
                    return false;
                case 9:
                    provider.sendAccessibilityEventForVirtualView(hoveredVirtualViewId, 128);
                    this.mLastHoveredChildVirtualViewId = hoveredVirtualViewId;
                    provider.performAction(hoveredVirtualViewId, 64, null);
                    return false;
                case 10:
                    provider.sendAccessibilityEventForVirtualView(hoveredVirtualViewId, 256);
                    this.mLastHoveredChildVirtualViewId = -1;
                    return false;
            }
        }
        return false;
    }

    @Override // android.view.View
    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currentScrollerY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currentScrollerY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currentScrollerY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!this.mHasSelectorWheel) {
            this.mIncrementButton.setEnabled(enabled);
        }
        if (!this.mHasSelectorWheel) {
            this.mDecrementButton.setEnabled(enabled);
        }
        this.mInputText.setEnabled(enabled);
    }

    @Override // android.view.View
    public void scrollBy(int x, int y) {
        int i;
        int[] selectorIndices = this.mSelectorIndices;
        int startScrollOffset = this.mCurrentScrollOffset;
        boolean z = this.mWrapSelectorWheel;
        if (!z && y > 0 && selectorIndices[1] <= this.mMinValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        } else if (!z && y < 0 && selectorIndices[1] >= this.mMaxValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
        } else {
            this.mCurrentScrollOffset += y;
            while (true) {
                int i2 = this.mCurrentScrollOffset;
                if (i2 - this.mInitialScrollOffset <= this.mSelectorTextGapHeight) {
                    break;
                }
                this.mCurrentScrollOffset = i2 - this.mSelectorElementHeight;
                decrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[1], true);
                if (!this.mWrapSelectorWheel && selectorIndices[1] <= this.mMinValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            while (true) {
                i = this.mCurrentScrollOffset;
                if (i - this.mInitialScrollOffset >= (-this.mSelectorTextGapHeight)) {
                    break;
                }
                this.mCurrentScrollOffset = i + this.mSelectorElementHeight;
                incrementSelectorIndices(selectorIndices);
                setValueInternal(selectorIndices[1], true);
                if (!this.mWrapSelectorWheel && selectorIndices[1] >= this.mMaxValue) {
                    this.mCurrentScrollOffset = this.mInitialScrollOffset;
                }
            }
            if (startScrollOffset != i) {
                onScrollChanged(0, i, 0, startScrollOffset);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + 1) * this.mSelectorElementHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public int computeVerticalScrollExtent() {
        return getHeight();
    }

    @Override // android.view.View
    public int getSolidColor() {
        return this.mSolidColor;
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        this.mOnValueChangeListener = onValueChangedListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setFormatter(Formatter formatter) {
        if (formatter == this.mFormatter) {
            return;
        }
        this.mFormatter = formatter;
        initializeSelectorWheelIndices();
        updateInputTextView();
    }

    public void setValue(int value) {
        setValueInternal(value, false);
    }

    @Override // android.view.View
    public boolean performClick() {
        if (!this.mHasSelectorWheel) {
            return super.performClick();
        }
        if (!super.performClick()) {
            showSoftInput();
            return true;
        }
        return true;
    }

    @Override // android.view.View
    public boolean performLongClick() {
        if (!this.mHasSelectorWheel) {
            return super.performLongClick();
        }
        if (!super.performLongClick()) {
            showSoftInput();
            this.mIgnoreMoveEvents = true;
        }
        return true;
    }

    private void showSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(InputMethodManager.class);
        if (inputMethodManager != null) {
            if (this.mHasSelectorWheel) {
                this.mInputText.setVisibility(0);
            }
            this.mInputText.requestFocus();
            inputMethodManager.showSoftInput(this.mInputText, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(InputMethodManager.class);
        if (inputMethodManager != null && inputMethodManager.isActive(this.mInputText)) {
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        if (this.mHasSelectorWheel) {
            this.mInputText.setVisibility(4);
        }
    }

    private void tryComputeMaxWidth() {
        if (!this.mComputeMaxWidth) {
            return;
        }
        int maxTextWidth = 0;
        String[] strArr = this.mDisplayedValues;
        if (strArr == null) {
            float maxDigitWidth = 0.0f;
            for (int i = 0; i <= 9; i++) {
                float digitWidth = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i));
                if (digitWidth > maxDigitWidth) {
                    maxDigitWidth = digitWidth;
                }
            }
            int numberOfDigits = 0;
            for (int current = this.mMaxValue; current > 0; current /= 10) {
                numberOfDigits++;
            }
            maxTextWidth = (int) (numberOfDigits * maxDigitWidth);
        } else {
            int valueCount = strArr.length;
            for (int i2 = 0; i2 < valueCount; i2++) {
                float textWidth = this.mSelectorWheelPaint.measureText(this.mDisplayedValues[i2]);
                if (textWidth > maxTextWidth) {
                    maxTextWidth = (int) textWidth;
                }
            }
        }
        int maxTextWidth2 = maxTextWidth + this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight();
        if (this.mMaxWidth != maxTextWidth2) {
            int i3 = this.mMinWidth;
            if (maxTextWidth2 > i3) {
                this.mMaxWidth = maxTextWidth2;
            } else {
                this.mMaxWidth = i3;
            }
            invalidate();
        }
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        this.mWrapSelectorWheelPreferred = wrapSelectorWheel;
        updateWrapSelectorWheel();
    }

    private void updateWrapSelectorWheel() {
        boolean z = true;
        boolean wrappingAllowed = this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length;
        if (!wrappingAllowed || !this.mWrapSelectorWheelPreferred) {
            z = false;
        }
        this.mWrapSelectorWheel = z;
    }

    public void setOnLongPressUpdateInterval(long intervalMillis) {
        this.mLongPressUpdateInterval = intervalMillis;
    }

    public int getValue() {
        return this.mValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public void setMinValue(int minValue) {
        if (this.mMinValue == minValue) {
            return;
        }
        if (minValue < 0) {
            throw new IllegalArgumentException("minValue must be >= 0");
        }
        this.mMinValue = minValue;
        if (minValue > this.mValue) {
            this.mValue = minValue;
        }
        updateWrapSelectorWheel();
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        if (this.mMaxValue == maxValue) {
            return;
        }
        if (maxValue < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        this.mMaxValue = maxValue;
        if (maxValue < this.mValue) {
            this.mValue = maxValue;
        }
        updateWrapSelectorWheel();
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public void setDisplayedValues(String[] displayedValues) {
        if (this.mDisplayedValues == displayedValues) {
            return;
        }
        this.mDisplayedValues = displayedValues;
        if (displayedValues != null) {
            this.mInputText.setRawInputType(524289);
        } else {
            this.mInputText.setRawInputType(2);
        }
        updateInputTextView();
        initializeSelectorWheelIndices();
        tryComputeMaxWidth();
    }

    public CharSequence getDisplayedValueForCurrentSelection() {
        return this.mSelectorIndexToStringCache.get(getValue());
    }

    public void setSelectionDividerHeight(int height) {
        this.mSelectionDividerHeight = height;
        invalidate();
    }

    public int getSelectionDividerHeight() {
        return this.mSelectionDividerHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable selectionDivider = this.mSelectionDivider;
        if (selectionDivider != null && selectionDivider.isStateful() && selectionDivider.setState(getDrawableState())) {
            invalidateDrawable(selectionDivider);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mSelectionDivider;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    public void onResolveDrawables(int layoutDirection) {
        super.onResolveDrawables(layoutDirection);
        Drawable drawable = this.mSelectionDivider;
        if (drawable != null) {
            drawable.setLayoutDirection(layoutDirection);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onDraw(Canvas canvas) {
        Drawable drawable;
        Drawable drawable2;
        if (!this.mHasSelectorWheel) {
            super.onDraw(canvas);
            return;
        }
        boolean showSelectorWheel = this.mHideWheelUntilFocused ? hasFocus() : true;
        float x = (this.mRight - this.mLeft) / 2;
        float y = this.mCurrentScrollOffset;
        if (showSelectorWheel && (drawable2 = this.mVirtualButtonPressedDrawable) != null && this.mScrollState == 0) {
            if (this.mDecrementVirtualButtonPressed) {
                drawable2.setState(PRESSED_STATE_SET);
                this.mVirtualButtonPressedDrawable.setBounds(0, 0, this.mRight, this.mTopSelectionDividerTop);
                this.mVirtualButtonPressedDrawable.draw(canvas);
            }
            if (this.mIncrementVirtualButtonPressed) {
                this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                this.mVirtualButtonPressedDrawable.setBounds(0, this.mBottomSelectionDividerBottom, this.mRight, this.mBottom);
                this.mVirtualButtonPressedDrawable.draw(canvas);
            }
        }
        int[] selectorIndices = this.mSelectorIndices;
        for (int i = 0; i < selectorIndices.length; i++) {
            int selectorIndex = selectorIndices[i];
            String scrollSelectorValue = this.mSelectorIndexToStringCache.get(selectorIndex);
            if ((showSelectorWheel && i != 1) || (i == 1 && this.mInputText.getVisibility() != 0)) {
                canvas.drawText(scrollSelectorValue, x, y, this.mSelectorWheelPaint);
            }
            y += this.mSelectorElementHeight;
        }
        if (showSelectorWheel && (drawable = this.mSelectionDivider) != null) {
            int topOfTopDivider = this.mTopSelectionDividerTop;
            int bottomOfTopDivider = this.mSelectionDividerHeight + topOfTopDivider;
            drawable.setBounds(0, topOfTopDivider, this.mRight, bottomOfTopDivider);
            this.mSelectionDivider.draw(canvas);
            int bottomOfBottomDivider = this.mBottomSelectionDividerBottom;
            int topOfBottomDivider = bottomOfBottomDivider - this.mSelectionDividerHeight;
            this.mSelectionDivider.setBounds(0, topOfBottomDivider, this.mRight, bottomOfBottomDivider);
            this.mSelectionDivider.draw(canvas);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        super.onInitializeAccessibilityEventInternal(event);
        event.setClassName(NumberPicker.class.getName());
        event.setScrollable(true);
        event.setScrollY((this.mMinValue + this.mValue) * this.mSelectorElementHeight);
        event.setMaxScrollY((this.mMaxValue - this.mMinValue) * this.mSelectorElementHeight);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (!this.mHasSelectorWheel) {
            return super.getAccessibilityNodeProvider();
        }
        if (this.mAccessibilityNodeProvider == null) {
            this.mAccessibilityNodeProvider = new AccessibilityNodeProviderImpl();
        }
        return this.mAccessibilityNodeProvider;
    }

    public void setTextColor(int color) {
        this.mSelectorWheelPaint.setColor(color);
        this.mInputText.setTextColor(color);
        invalidate();
    }

    public int getTextColor() {
        return this.mSelectorWheelPaint.getColor();
    }

    public void setTextSize(float size) {
        this.mSelectorWheelPaint.setTextSize(size);
        this.mInputText.setTextSize(0, size);
        invalidate();
    }

    public float getTextSize() {
        return this.mSelectorWheelPaint.getTextSize();
    }

    private int makeMeasureSpec(int measureSpec, int maxSize) {
        if (maxSize == -1) {
            return measureSpec;
        }
        int size = View.MeasureSpec.getSize(measureSpec);
        int mode = View.MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case Integer.MIN_VALUE:
                return View.MeasureSpec.makeMeasureSpec(Math.min(size, maxSize), 1073741824);
            case 0:
                return View.MeasureSpec.makeMeasureSpec(maxSize, 1073741824);
            case 1073741824:
                return measureSpec;
            default:
                throw new IllegalArgumentException("Unknown measure mode: " + mode);
        }
    }

    private int resolveSizeAndStateRespectingMinSize(int minSize, int measuredSize, int measureSpec) {
        if (minSize != -1) {
            int desiredWidth = Math.max(minSize, measuredSize);
            return resolveSizeAndState(desiredWidth, measureSpec, 0);
        }
        return measuredSize;
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] selectorIndices = this.mSelectorIndices;
        int current = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int selectorIndex = (i - 1) + current;
            if (this.mWrapSelectorWheel) {
                selectorIndex = getWrappedSelectorIndex(selectorIndex);
            }
            selectorIndices[i] = selectorIndex;
            ensureCachedScrollSelectorValue(selectorIndices[i]);
        }
    }

    private void setValueInternal(int current, boolean notifyChange) {
        int current2;
        if (this.mValue == current) {
            return;
        }
        if (this.mWrapSelectorWheel) {
            current2 = getWrappedSelectorIndex(current);
        } else {
            current2 = Math.min(Math.max(current, this.mMinValue), this.mMaxValue);
        }
        int previous = this.mValue;
        this.mValue = current2;
        if (this.mScrollState != 2) {
            updateInputTextView();
        }
        if (notifyChange) {
            notifyChange(previous, current2);
        }
        initializeSelectorWheelIndices();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeValueByOne(boolean increment) {
        if (!this.mHasSelectorWheel) {
            if (increment) {
                setValueInternal(this.mValue + 1, true);
                return;
            } else {
                setValueInternal(this.mValue - 1, true);
                return;
            }
        }
        hideSoftInput();
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = 0;
        if (increment) {
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, 300);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, 300);
        }
        invalidate();
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] selectorIndices = this.mSelectorIndices;
        int totalTextHeight = selectorIndices.length * this.mTextSize;
        float totalTextGapHeight = (this.mBottom - this.mTop) - totalTextHeight;
        float textGapCount = selectorIndices.length;
        int i = (int) ((totalTextGapHeight / textGapCount) + 0.5f);
        this.mSelectorTextGapHeight = i;
        this.mSelectorElementHeight = this.mTextSize + i;
        int editTextTextPosition = this.mInputText.getBaseline() + this.mInputText.getTop();
        int i2 = editTextTextPosition - (this.mSelectorElementHeight * 1);
        this.mInitialScrollOffset = i2;
        this.mCurrentScrollOffset = i2;
        updateInputTextView();
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((this.mBottom - this.mTop) - this.mTextSize) / 2);
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            ensureScrollWheelAdjusted();
            updateInputTextView();
            onScrollStateChange(0);
        } else if (this.mScrollState != 1) {
            updateInputTextView();
        }
    }

    private void onScrollStateChange(int scrollState) {
        if (this.mScrollState == scrollState) {
            return;
        }
        this.mScrollState = scrollState;
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChange(this, scrollState);
        }
    }

    private void fling(int velocityY) {
        this.mPreviousScrollerY = 0;
        if (velocityY > 0) {
            this.mFlingScroller.fling(0, 0, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            this.mFlingScroller.fling(0, Integer.MAX_VALUE, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWrappedSelectorIndex(int selectorIndex) {
        int i = this.mMaxValue;
        if (selectorIndex > i) {
            int i2 = this.mMinValue;
            return (i2 + ((selectorIndex - i) % (i - i2))) - 1;
        }
        int i3 = this.mMinValue;
        if (selectorIndex < i3) {
            return (i - ((i3 - selectorIndex) % (i - i3))) + 1;
        }
        return selectorIndex;
    }

    private void incrementSelectorIndices(int[] selectorIndices) {
        for (int i = 0; i < selectorIndices.length - 1; i++) {
            selectorIndices[i] = selectorIndices[i + 1];
        }
        int i2 = selectorIndices.length;
        int nextScrollSelectorIndex = selectorIndices[i2 - 2] + 1;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex > this.mMaxValue) {
            nextScrollSelectorIndex = this.mMinValue;
        }
        selectorIndices[selectorIndices.length - 1] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void decrementSelectorIndices(int[] selectorIndices) {
        for (int i = selectorIndices.length - 1; i > 0; i--) {
            selectorIndices[i] = selectorIndices[i - 1];
        }
        int i2 = selectorIndices[1];
        int nextScrollSelectorIndex = i2 - 1;
        if (this.mWrapSelectorWheel && nextScrollSelectorIndex < this.mMinValue) {
            nextScrollSelectorIndex = this.mMaxValue;
        }
        selectorIndices[0] = nextScrollSelectorIndex;
        ensureCachedScrollSelectorValue(nextScrollSelectorIndex);
    }

    private void ensureCachedScrollSelectorValue(int selectorIndex) {
        String scrollSelectorValue;
        SparseArray<String> cache = this.mSelectorIndexToStringCache;
        String scrollSelectorValue2 = cache.get(selectorIndex);
        if (scrollSelectorValue2 != null) {
            return;
        }
        int i = this.mMinValue;
        if (selectorIndex < i || selectorIndex > this.mMaxValue) {
            scrollSelectorValue = "";
        } else {
            String[] strArr = this.mDisplayedValues;
            if (strArr != null) {
                int displayedValueIndex = selectorIndex - i;
                scrollSelectorValue = strArr[displayedValueIndex];
            } else {
                scrollSelectorValue = formatNumber(selectorIndex);
            }
        }
        cache.put(selectorIndex, scrollSelectorValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatNumber(int value) {
        Formatter formatter = this.mFormatter;
        return formatter != null ? formatter.format(value) : formatNumberWithLocale(value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void validateInputTextView(View v) {
        String str = String.valueOf(((TextView) v).getText());
        if (TextUtils.isEmpty(str)) {
            updateInputTextView();
            return;
        }
        int current = getSelectedPos(str.toString());
        setValueInternal(current, true);
    }

    private boolean updateInputTextView() {
        String[] strArr = this.mDisplayedValues;
        String text = strArr == null ? formatNumber(this.mValue) : strArr[this.mValue - this.mMinValue];
        if (!TextUtils.isEmpty(text)) {
            CharSequence beforeText = this.mInputText.getText();
            if (!text.equals(beforeText.toString())) {
                this.mInputText.setText(text);
                if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
                    AccessibilityEvent event = AccessibilityEvent.obtain(16);
                    this.mInputText.onInitializeAccessibilityEvent(event);
                    this.mInputText.onPopulateAccessibilityEvent(event);
                    event.setFromIndex(0);
                    event.setRemovedCount(beforeText.length());
                    event.setAddedCount(text.length());
                    event.setBeforeText(beforeText);
                    event.setSource(this, 2);
                    requestSendAccessibilityEvent(this, event);
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    private void notifyChange(int previous, int current) {
        OnValueChangeListener onValueChangeListener = this.mOnValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChange(this, previous, this.mValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postChangeCurrentByOneFromLongPress(boolean increment, long delayMillis) {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(increment);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, delayMillis);
    }

    private void removeChangeCurrentByOneFromLongPress() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
    }

    private void postBeginSoftInputOnLongPressCommand() {
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand == null) {
            this.mBeginSoftInputOnLongPressCommand = new BeginSoftInputOnLongPressCommand();
        } else {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
        postDelayed(this.mBeginSoftInputOnLongPressCommand, ViewConfiguration.getLongPressTimeout());
    }

    private void removeBeginSoftInputCommand() {
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand != null) {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
    }

    private void removeAllCallbacks() {
        ChangeCurrentByOneFromLongPressCommand changeCurrentByOneFromLongPressCommand = this.mChangeCurrentByOneFromLongPressCommand;
        if (changeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(changeCurrentByOneFromLongPressCommand);
        }
        SetSelectionCommand setSelectionCommand = this.mSetSelectionCommand;
        if (setSelectionCommand != null) {
            setSelectionCommand.cancel();
        }
        BeginSoftInputOnLongPressCommand beginSoftInputOnLongPressCommand = this.mBeginSoftInputOnLongPressCommand;
        if (beginSoftInputOnLongPressCommand != null) {
            removeCallbacks(beginSoftInputOnLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSelectedPos(String value) {
        if (this.mDisplayedValues == null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
            }
        } else {
            for (int i = 0; i < this.mDisplayedValues.length; i++) {
                value = value.toLowerCase();
                if (this.mDisplayedValues[i].toLowerCase().startsWith(value)) {
                    return this.mMinValue + i;
                }
            }
            try {
                int i2 = Integer.parseInt(value);
                return i2;
            } catch (NumberFormatException e2) {
            }
        }
        return this.mMinValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postSetSelectionCommand(int selectionStart, int selectionEnd) {
        if (this.mSetSelectionCommand == null) {
            this.mSetSelectionCommand = new SetSelectionCommand(this.mInputText);
        }
        this.mSetSelectionCommand.post(selectionStart, selectionEnd);
    }

    /* loaded from: classes4.dex */
    class InputTextFilter extends NumberKeyListener {
        InputTextFilter() {
        }

        @Override // android.text.method.KeyListener
        public int getInputType() {
            return 1;
        }

        @Override // android.text.method.NumberKeyListener
        protected char[] getAcceptedChars() {
            return NumberPicker.DIGIT_CHARACTERS;
        }

        @Override // android.text.method.NumberKeyListener, android.text.InputFilter
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String[] strArr;
            if (NumberPicker.this.mSetSelectionCommand != null) {
                NumberPicker.this.mSetSelectionCommand.cancel();
            }
            if (NumberPicker.this.mDisplayedValues == null) {
                CharSequence filtered = super.filter(source, start, end, dest, dstart, dend);
                if (filtered == null) {
                    filtered = source.subSequence(start, end);
                }
                String result = String.valueOf(dest.subSequence(0, dstart)) + ((Object) filtered) + ((Object) dest.subSequence(dend, dest.length()));
                if ("".equals(result)) {
                    return result;
                }
                return (NumberPicker.this.getSelectedPos(result) > NumberPicker.this.mMaxValue || result.length() > String.valueOf(NumberPicker.this.mMaxValue).length()) ? "" : filtered;
            }
            CharSequence filtered2 = String.valueOf(source.subSequence(start, end));
            if (TextUtils.isEmpty(filtered2)) {
                return "";
            }
            String result2 = String.valueOf(dest.subSequence(0, dstart)) + ((Object) filtered2) + ((Object) dest.subSequence(dend, dest.length()));
            String str = String.valueOf(result2).toLowerCase();
            for (String val : NumberPicker.this.mDisplayedValues) {
                String valLowerCase = val.toLowerCase();
                if (valLowerCase.startsWith(str)) {
                    NumberPicker.this.postSetSelectionCommand(result2.length(), val.length());
                    return val.subSequence(dstart, val.length());
                }
            }
            return "";
        }
    }

    private boolean ensureScrollWheelAdjusted() {
        int deltaY = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (deltaY == 0) {
            return false;
        }
        this.mPreviousScrollerY = 0;
        int abs = Math.abs(deltaY);
        int i = this.mSelectorElementHeight;
        if (abs > i / 2) {
            if (deltaY > 0) {
                i = -i;
            }
            deltaY += i;
        }
        this.mAdjustScroller.startScroll(0, 0, 0, deltaY, 800);
        invalidate();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class PressedStateHelper implements Runnable {
        public static final int BUTTON_DECREMENT = 2;
        public static final int BUTTON_INCREMENT = 1;
        private final int MODE_PRESS = 1;
        private final int MODE_TAPPED = 2;
        private int mManagedButton;
        private int mMode;

        PressedStateHelper() {
        }

        public void cancel() {
            this.mMode = 0;
            this.mManagedButton = 0;
            NumberPicker.this.removeCallbacks(this);
            if (NumberPicker.this.mIncrementVirtualButtonPressed) {
                NumberPicker.this.mIncrementVirtualButtonPressed = false;
                NumberPicker numberPicker = NumberPicker.this;
                numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
            }
            NumberPicker.this.mDecrementVirtualButtonPressed = false;
            if (NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker numberPicker2 = NumberPicker.this;
                numberPicker2.invalidate(0, 0, numberPicker2.mRight, NumberPicker.this.mTopSelectionDividerTop);
            }
        }

        public void buttonPressDelayed(int button) {
            cancel();
            this.mMode = 1;
            this.mManagedButton = button;
            NumberPicker.this.postDelayed(this, ViewConfiguration.getTapTimeout());
        }

        public void buttonTapped(int button) {
            cancel();
            this.mMode = 2;
            this.mManagedButton = button;
            NumberPicker.this.post(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            switch (this.mMode) {
                case 1:
                    switch (this.mManagedButton) {
                        case 1:
                            NumberPicker.this.mIncrementVirtualButtonPressed = true;
                            NumberPicker numberPicker = NumberPicker.this;
                            numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                            return;
                        case 2:
                            NumberPicker.this.mDecrementVirtualButtonPressed = true;
                            NumberPicker numberPicker2 = NumberPicker.this;
                            numberPicker2.invalidate(0, 0, numberPicker2.mRight, NumberPicker.this.mTopSelectionDividerTop);
                            return;
                        default:
                            return;
                    }
                case 2:
                    switch (this.mManagedButton) {
                        case 1:
                            if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker numberPicker3 = NumberPicker.this;
                            numberPicker3.mIncrementVirtualButtonPressed = true ^ numberPicker3.mIncrementVirtualButtonPressed;
                            NumberPicker numberPicker4 = NumberPicker.this;
                            numberPicker4.invalidate(0, numberPicker4.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                            return;
                        case 2:
                            if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker numberPicker5 = NumberPicker.this;
                            numberPicker5.mDecrementVirtualButtonPressed = true ^ numberPicker5.mDecrementVirtualButtonPressed;
                            NumberPicker numberPicker6 = NumberPicker.this;
                            numberPicker6.invalidate(0, 0, numberPicker6.mRight, NumberPicker.this.mTopSelectionDividerTop);
                            return;
                        default:
                            return;
                    }
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class SetSelectionCommand implements Runnable {
        private final EditText mInputText;
        private boolean mPosted;
        private int mSelectionEnd;
        private int mSelectionStart;

        public SetSelectionCommand(EditText inputText) {
            this.mInputText = inputText;
        }

        public void post(int selectionStart, int selectionEnd) {
            this.mSelectionStart = selectionStart;
            this.mSelectionEnd = selectionEnd;
            if (!this.mPosted) {
                this.mInputText.post(this);
                this.mPosted = true;
            }
        }

        public void cancel() {
            if (this.mPosted) {
                this.mInputText.removeCallbacks(this);
                this.mPosted = false;
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mPosted = false;
            this.mInputText.setSelection(this.mSelectionStart, this.mSelectionEnd);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setStep(boolean increment) {
            this.mIncrement = increment;
        }

        @Override // java.lang.Runnable
        public void run() {
            NumberPicker.this.changeValueByOne(this.mIncrement);
            NumberPicker numberPicker = NumberPicker.this;
            numberPicker.postDelayed(this, numberPicker.mLongPressUpdateInterval);
        }
    }

    /* loaded from: classes4.dex */
    public static class CustomEditText extends EditText {
        public CustomEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override // android.widget.TextView
        public void onEditorAction(int actionCode) {
            super.onEditorAction(actionCode);
            if (actionCode == 6) {
                clearFocus();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class BeginSoftInputOnLongPressCommand implements Runnable {
        BeginSoftInputOnLongPressCommand() {
        }

        @Override // java.lang.Runnable
        public void run() {
            NumberPicker.this.performLongClick();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class AccessibilityNodeProviderImpl extends AccessibilityNodeProvider {
        private static final int UNDEFINED = Integer.MIN_VALUE;
        private static final int VIRTUAL_VIEW_ID_DECREMENT = 3;
        private static final int VIRTUAL_VIEW_ID_INCREMENT = 1;
        private static final int VIRTUAL_VIEW_ID_INPUT = 2;
        private final Rect mTempRect = new Rect();
        private final int[] mTempArray = new int[2];
        private int mAccessibilityFocusedView = Integer.MIN_VALUE;

        AccessibilityNodeProviderImpl() {
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
            switch (virtualViewId) {
                case -1:
                    return createAccessibilityNodeInfoForNumberPicker(NumberPicker.this.mScrollX, NumberPicker.this.mScrollY, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mScrollY + (NumberPicker.this.mBottom - NumberPicker.this.mTop));
                case 0:
                default:
                    return super.createAccessibilityNodeInfo(virtualViewId);
                case 1:
                    return createAccessibilityNodeInfoForVirtualButton(1, getVirtualIncrementButtonText(), NumberPicker.this.mScrollX, NumberPicker.this.mBottomSelectionDividerBottom - NumberPicker.this.mSelectionDividerHeight, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mScrollY + (NumberPicker.this.mBottom - NumberPicker.this.mTop));
                case 2:
                    return createAccessibiltyNodeInfoForInputText(NumberPicker.this.mScrollX, NumberPicker.this.mTopSelectionDividerTop + NumberPicker.this.mSelectionDividerHeight, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mBottomSelectionDividerBottom - NumberPicker.this.mSelectionDividerHeight);
                case 3:
                    return createAccessibilityNodeInfoForVirtualButton(3, getVirtualDecrementButtonText(), NumberPicker.this.mScrollX, NumberPicker.this.mScrollY, (NumberPicker.this.mRight - NumberPicker.this.mLeft) + NumberPicker.this.mScrollX, NumberPicker.this.mSelectionDividerHeight + NumberPicker.this.mTopSelectionDividerTop);
            }
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String searched, int virtualViewId) {
            if (TextUtils.isEmpty(searched)) {
                return Collections.emptyList();
            }
            String searchedLowerCase = searched.toLowerCase();
            List<AccessibilityNodeInfo> result = new ArrayList<>();
            switch (virtualViewId) {
                case -1:
                    findAccessibilityNodeInfosByTextInChild(searchedLowerCase, 3, result);
                    findAccessibilityNodeInfosByTextInChild(searchedLowerCase, 2, result);
                    findAccessibilityNodeInfosByTextInChild(searchedLowerCase, 1, result);
                    return result;
                case 0:
                default:
                    return super.findAccessibilityNodeInfosByText(searched, virtualViewId);
                case 1:
                case 2:
                case 3:
                    findAccessibilityNodeInfosByTextInChild(searchedLowerCase, virtualViewId, result);
                    return result;
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.view.accessibility.AccessibilityNodeProvider
        public boolean performAction(int virtualViewId, int action, Bundle arguments) {
            switch (virtualViewId) {
                case -1:
                    switch (action) {
                        case 64:
                            if (this.mAccessibilityFocusedView != virtualViewId) {
                                this.mAccessibilityFocusedView = virtualViewId;
                                NumberPicker.this.requestAccessibilityFocus();
                                return true;
                            }
                            return false;
                        case 128:
                            if (this.mAccessibilityFocusedView == virtualViewId) {
                                this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                                NumberPicker.this.clearAccessibilityFocus();
                                return true;
                            }
                            return false;
                        case 4096:
                        case 16908346:
                            if (!NumberPicker.this.isEnabled() || (!NumberPicker.this.getWrapSelectorWheel() && NumberPicker.this.getValue() >= NumberPicker.this.getMaxValue())) {
                                return false;
                            }
                            NumberPicker.this.changeValueByOne(true);
                            return true;
                        case 8192:
                        case 16908344:
                            if (!NumberPicker.this.isEnabled() || (!NumberPicker.this.getWrapSelectorWheel() && NumberPicker.this.getValue() <= NumberPicker.this.getMinValue())) {
                                return false;
                            }
                            NumberPicker.this.changeValueByOne(false);
                            return true;
                    }
                case 1:
                    switch (action) {
                        case 16:
                            if (NumberPicker.this.isEnabled()) {
                                NumberPicker.this.changeValueByOne(true);
                                sendAccessibilityEventForVirtualView(virtualViewId, 1);
                                return true;
                            }
                            return false;
                        case 64:
                            if (this.mAccessibilityFocusedView != virtualViewId) {
                                this.mAccessibilityFocusedView = virtualViewId;
                                sendAccessibilityEventForVirtualView(virtualViewId, 32768);
                                NumberPicker numberPicker = NumberPicker.this;
                                numberPicker.invalidate(0, numberPicker.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                                return true;
                            }
                            return false;
                        case 128:
                            if (this.mAccessibilityFocusedView == virtualViewId) {
                                this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                                sendAccessibilityEventForVirtualView(virtualViewId, 65536);
                                NumberPicker numberPicker2 = NumberPicker.this;
                                numberPicker2.invalidate(0, numberPicker2.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                                return true;
                            }
                            return false;
                        default:
                            return false;
                    }
                case 2:
                    switch (action) {
                        case 1:
                            if (!NumberPicker.this.isEnabled() || NumberPicker.this.mInputText.isFocused()) {
                                return false;
                            }
                            return NumberPicker.this.mInputText.requestFocus();
                        case 2:
                            if (NumberPicker.this.isEnabled() && NumberPicker.this.mInputText.isFocused()) {
                                NumberPicker.this.mInputText.clearFocus();
                                return true;
                            }
                            return false;
                        case 16:
                            if (NumberPicker.this.isEnabled()) {
                                NumberPicker.this.performClick();
                                return true;
                            }
                            return false;
                        case 32:
                            if (NumberPicker.this.isEnabled()) {
                                NumberPicker.this.performLongClick();
                                return true;
                            }
                            return false;
                        case 64:
                            if (this.mAccessibilityFocusedView != virtualViewId) {
                                this.mAccessibilityFocusedView = virtualViewId;
                                sendAccessibilityEventForVirtualView(virtualViewId, 32768);
                                NumberPicker.this.mInputText.invalidate();
                                return true;
                            }
                            return false;
                        case 128:
                            if (this.mAccessibilityFocusedView == virtualViewId) {
                                this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                                sendAccessibilityEventForVirtualView(virtualViewId, 65536);
                                NumberPicker.this.mInputText.invalidate();
                                return true;
                            }
                            return false;
                        default:
                            return NumberPicker.this.mInputText.performAccessibilityAction(action, arguments);
                    }
                case 3:
                    switch (action) {
                        case 16:
                            if (NumberPicker.this.isEnabled()) {
                                boolean increment = virtualViewId == 1;
                                NumberPicker.this.changeValueByOne(increment);
                                sendAccessibilityEventForVirtualView(virtualViewId, 1);
                                return true;
                            }
                            return false;
                        case 64:
                            if (this.mAccessibilityFocusedView != virtualViewId) {
                                this.mAccessibilityFocusedView = virtualViewId;
                                sendAccessibilityEventForVirtualView(virtualViewId, 32768);
                                NumberPicker numberPicker3 = NumberPicker.this;
                                numberPicker3.invalidate(0, 0, numberPicker3.mRight, NumberPicker.this.mTopSelectionDividerTop);
                                return true;
                            }
                            return false;
                        case 128:
                            if (this.mAccessibilityFocusedView == virtualViewId) {
                                this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                                sendAccessibilityEventForVirtualView(virtualViewId, 65536);
                                NumberPicker numberPicker4 = NumberPicker.this;
                                numberPicker4.invalidate(0, 0, numberPicker4.mRight, NumberPicker.this.mTopSelectionDividerTop);
                                return true;
                            }
                            return false;
                        default:
                            return false;
                    }
            }
            return super.performAction(virtualViewId, action, arguments);
        }

        public void sendAccessibilityEventForVirtualView(int virtualViewId, int eventType) {
            switch (virtualViewId) {
                case 1:
                    if (hasVirtualIncrementButton()) {
                        sendAccessibilityEventForVirtualButton(virtualViewId, eventType, getVirtualIncrementButtonText());
                        return;
                    }
                    return;
                case 2:
                    sendAccessibilityEventForVirtualText(eventType);
                    return;
                case 3:
                    if (hasVirtualDecrementButton()) {
                        sendAccessibilityEventForVirtualButton(virtualViewId, eventType, getVirtualDecrementButtonText());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        private void sendAccessibilityEventForVirtualText(int eventType) {
            if (AccessibilityManager.getInstance(NumberPicker.this.mContext).isEnabled()) {
                AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
                NumberPicker.this.mInputText.onInitializeAccessibilityEvent(event);
                NumberPicker.this.mInputText.onPopulateAccessibilityEvent(event);
                event.setSource(NumberPicker.this, 2);
                NumberPicker numberPicker = NumberPicker.this;
                numberPicker.requestSendAccessibilityEvent(numberPicker, event);
            }
        }

        private void sendAccessibilityEventForVirtualButton(int virtualViewId, int eventType, String text) {
            if (AccessibilityManager.getInstance(NumberPicker.this.mContext).isEnabled()) {
                AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
                event.setClassName(Button.class.getName());
                event.setPackageName(NumberPicker.this.mContext.getPackageName());
                event.getText().add(text);
                event.setEnabled(NumberPicker.this.isEnabled());
                event.setSource(NumberPicker.this, virtualViewId);
                NumberPicker numberPicker = NumberPicker.this;
                numberPicker.requestSendAccessibilityEvent(numberPicker, event);
            }
        }

        private void findAccessibilityNodeInfosByTextInChild(String searchedLowerCase, int virtualViewId, List<AccessibilityNodeInfo> outResult) {
            switch (virtualViewId) {
                case 1:
                    String text = getVirtualIncrementButtonText();
                    if (!TextUtils.isEmpty(text) && text.toString().toLowerCase().contains(searchedLowerCase)) {
                        outResult.add(createAccessibilityNodeInfo(1));
                        return;
                    }
                    return;
                case 2:
                    CharSequence text2 = NumberPicker.this.mInputText.getText();
                    if (!TextUtils.isEmpty(text2) && text2.toString().toLowerCase().contains(searchedLowerCase)) {
                        outResult.add(createAccessibilityNodeInfo(2));
                        return;
                    }
                    CharSequence contentDesc = NumberPicker.this.mInputText.getText();
                    if (!TextUtils.isEmpty(contentDesc) && contentDesc.toString().toLowerCase().contains(searchedLowerCase)) {
                        outResult.add(createAccessibilityNodeInfo(2));
                        return;
                    }
                    return;
                case 3:
                    String text3 = getVirtualDecrementButtonText();
                    if (!TextUtils.isEmpty(text3) && text3.toString().toLowerCase().contains(searchedLowerCase)) {
                        outResult.add(createAccessibilityNodeInfo(3));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        private AccessibilityNodeInfo createAccessibiltyNodeInfoForInputText(int left, int top, int right, int bottom) {
            AccessibilityNodeInfo info = NumberPicker.this.mInputText.createAccessibilityNodeInfo();
            info.setSource(NumberPicker.this, 2);
            info.setAccessibilityFocused(this.mAccessibilityFocusedView == 2);
            if (this.mAccessibilityFocusedView != 2) {
                info.addAction(64);
            }
            if (this.mAccessibilityFocusedView == 2) {
                info.addAction(128);
            }
            Rect boundsInParent = this.mTempRect;
            boundsInParent.set(left, top, right, bottom);
            info.setVisibleToUser(NumberPicker.this.isVisibleToUser(boundsInParent));
            info.setBoundsInParent(boundsInParent);
            int[] locationOnScreen = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(locationOnScreen);
            boundsInParent.offset(locationOnScreen[0], locationOnScreen[1]);
            info.setBoundsInScreen(boundsInParent);
            return info;
        }

        private AccessibilityNodeInfo createAccessibilityNodeInfoForVirtualButton(int virtualViewId, String text, int left, int top, int right, int bottom) {
            AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain();
            info.setClassName(Button.class.getName());
            info.setPackageName(NumberPicker.this.mContext.getPackageName());
            info.setSource(NumberPicker.this, virtualViewId);
            info.setParent(NumberPicker.this);
            info.setText(text);
            info.setClickable(true);
            info.setLongClickable(true);
            info.setEnabled(NumberPicker.this.isEnabled());
            info.setAccessibilityFocused(this.mAccessibilityFocusedView == virtualViewId);
            Rect boundsInParent = this.mTempRect;
            boundsInParent.set(left, top, right, bottom);
            info.setVisibleToUser(NumberPicker.this.isVisibleToUser(boundsInParent));
            info.setBoundsInParent(boundsInParent);
            int[] locationOnScreen = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(locationOnScreen);
            boundsInParent.offset(locationOnScreen[0], locationOnScreen[1]);
            info.setBoundsInScreen(boundsInParent);
            if (this.mAccessibilityFocusedView != virtualViewId) {
                info.addAction(64);
            }
            if (this.mAccessibilityFocusedView == virtualViewId) {
                info.addAction(128);
            }
            if (NumberPicker.this.isEnabled()) {
                info.addAction(16);
            }
            return info;
        }

        private AccessibilityNodeInfo createAccessibilityNodeInfoForNumberPicker(int left, int top, int right, int bottom) {
            AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain();
            info.setClassName(NumberPicker.class.getName());
            info.setPackageName(NumberPicker.this.mContext.getPackageName());
            info.setSource(NumberPicker.this);
            if (hasVirtualDecrementButton()) {
                info.addChild(NumberPicker.this, 3);
            }
            info.addChild(NumberPicker.this, 2);
            if (hasVirtualIncrementButton()) {
                info.addChild(NumberPicker.this, 1);
            }
            info.setParent((View) NumberPicker.this.getParentForAccessibility());
            info.setEnabled(NumberPicker.this.isEnabled());
            info.setScrollable(true);
            info.setAccessibilityFocused(this.mAccessibilityFocusedView == -1);
            float applicationScale = NumberPicker.this.getContext().getResources().getCompatibilityInfo().applicationScale;
            Rect boundsInParent = this.mTempRect;
            boundsInParent.set(left, top, right, bottom);
            boundsInParent.scale(applicationScale);
            info.setBoundsInParent(boundsInParent);
            info.setVisibleToUser(NumberPicker.this.isVisibleToUser());
            int[] locationOnScreen = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(locationOnScreen);
            boundsInParent.offset(locationOnScreen[0], locationOnScreen[1]);
            boundsInParent.scale(applicationScale);
            info.setBoundsInScreen(boundsInParent);
            if (this.mAccessibilityFocusedView != -1) {
                info.addAction(64);
            }
            if (this.mAccessibilityFocusedView == -1) {
                info.addAction(128);
            }
            if (NumberPicker.this.isEnabled()) {
                if (NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() < NumberPicker.this.getMaxValue()) {
                    info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
                    info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN);
                }
                if (NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() > NumberPicker.this.getMinValue()) {
                    info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
                    info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP);
                }
            }
            return info;
        }

        private boolean hasVirtualDecrementButton() {
            return NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() > NumberPicker.this.getMinValue();
        }

        private boolean hasVirtualIncrementButton() {
            return NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() < NumberPicker.this.getMaxValue();
        }

        private String getVirtualDecrementButtonText() {
            int value = NumberPicker.this.mValue - 1;
            if (NumberPicker.this.mWrapSelectorWheel) {
                value = NumberPicker.this.getWrappedSelectorIndex(value);
            }
            if (value >= NumberPicker.this.mMinValue) {
                return NumberPicker.this.mDisplayedValues == null ? NumberPicker.this.formatNumber(value) : NumberPicker.this.mDisplayedValues[value - NumberPicker.this.mMinValue];
            }
            return null;
        }

        private String getVirtualIncrementButtonText() {
            int value = NumberPicker.this.mValue + 1;
            if (NumberPicker.this.mWrapSelectorWheel) {
                value = NumberPicker.this.getWrappedSelectorIndex(value);
            }
            if (value <= NumberPicker.this.mMaxValue) {
                return NumberPicker.this.mDisplayedValues == null ? NumberPicker.this.formatNumber(value) : NumberPicker.this.mDisplayedValues[value - NumberPicker.this.mMinValue];
            }
            return null;
        }
    }

    private static String formatNumberWithLocale(int value) {
        return String.format(Locale.getDefault(), "%d", Integer.valueOf(value));
    }
}
