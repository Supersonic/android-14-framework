package android.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.p008os.Bundle;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.IntArray;
import android.util.Log;
import android.util.MathUtils;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.C4057R;
import com.android.internal.widget.ExploreByTouchHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Locale;
/* loaded from: classes4.dex */
public class RadialTimePickerView extends View {

    /* renamed from: AM */
    private static final int f528AM = 0;
    private static final int ANIM_DURATION_NORMAL = 500;
    private static final int ANIM_DURATION_TOUCH = 60;
    private static final int DEGREES_FOR_ONE_HOUR = 30;
    private static final int DEGREES_FOR_ONE_MINUTE = 6;
    public static final int HOURS = 0;
    private static final int HOURS_INNER = 2;
    private static final int HOURS_IN_CIRCLE = 12;
    public static final int MINUTES = 1;
    private static final int MINUTES_IN_CIRCLE = 60;
    private static final int MISSING_COLOR = -65281;
    private static final int NUM_POSITIONS = 12;

    /* renamed from: PM */
    private static final int f529PM = 1;
    private static final int SELECTOR_CIRCLE = 0;
    private static final int SELECTOR_DOT = 1;
    private static final int SELECTOR_LINE = 2;
    private static final String TAG = "RadialTimePickerView";
    private final FloatProperty<RadialTimePickerView> HOURS_TO_MINUTES;
    private int mAmOrPm;
    private int mCenterDotRadius;
    boolean mChangedDuringTouch;
    private int mCircleRadius;
    private float mDisabledAlpha;
    private int mHalfwayDist;
    private final String[] mHours12Texts;
    private float mHoursToMinutes;
    private ObjectAnimator mHoursToMinutesAnimator;
    private final String[] mInnerHours24Texts;
    private String[] mInnerTextHours;
    private final float[] mInnerTextX;
    private final float[] mInnerTextY;
    private boolean mInputEnabled;
    private boolean mIs24HourMode;
    private boolean mIsOnInnerCircle;
    private OnValueSelectedListener mListener;
    private int mMaxDistForOuterNumber;
    private int mMinDistForInnerNumber;
    private String[] mMinutesText;
    private final String[] mMinutesTexts;
    private final String[] mOuterHours24Texts;
    private String[] mOuterTextHours;
    private final float[][] mOuterTextX;
    private final float[][] mOuterTextY;
    private final Paint[] mPaint;
    private final Paint mPaintBackground;
    private final Paint mPaintCenter;
    private final Paint[] mPaintSelector;
    private final int[] mSelectionDegrees;
    private int mSelectorColor;
    private int mSelectorDotColor;
    private int mSelectorDotRadius;
    private final Path mSelectorPath;
    private int mSelectorRadius;
    private int mSelectorStroke;
    private boolean mShowHours;
    private final ColorStateList[] mTextColor;
    private final int[] mTextInset;
    private final int[] mTextSize;
    private final RadialPickerTouchHelper mTouchHelper;
    private final Typeface mTypeface;
    private int mXCenter;
    private int mYCenter;
    private static final int[] HOURS_NUMBERS = {12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    private static final int[] HOURS_NUMBERS_24 = {0, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
    private static final int[] MINUTES_NUMBERS = {0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55};
    private static final int[] SNAP_PREFER_30S_MAP = new int[361];
    private static final float[] COS_30 = new float[12];
    private static final float[] SIN_30 = new float[12];

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public interface OnValueSelectedListener {
        void onValueSelected(int i, int i2, boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    @interface PickerType {
    }

    static {
        preparePrefer30sMap();
        double angle = 1.5707963267948966d;
        for (int i = 0; i < 12; i++) {
            COS_30[i] = (float) Math.cos(angle);
            SIN_30[i] = (float) Math.sin(angle);
            angle += 0.5235987755982988d;
        }
    }

    private static void preparePrefer30sMap() {
        int snappedOutputDegrees = 0;
        int count = 1;
        int expectedCount = 8;
        for (int degrees = 0; degrees < 361; degrees++) {
            SNAP_PREFER_30S_MAP[degrees] = snappedOutputDegrees;
            if (count == expectedCount) {
                snappedOutputDegrees += 6;
                if (snappedOutputDegrees == 360) {
                    expectedCount = 7;
                } else if (snappedOutputDegrees % 30 == 0) {
                    expectedCount = 14;
                } else {
                    expectedCount = 4;
                }
                count = 1;
            } else {
                count++;
            }
        }
    }

    private static int snapPrefer30s(int degrees) {
        int[] iArr = SNAP_PREFER_30S_MAP;
        if (iArr == null) {
            return -1;
        }
        return iArr[degrees];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int snapOnly30s(int degrees, int forceHigherOrLower) {
        int floor = (degrees / 30) * 30;
        int ceiling = floor + 30;
        if (forceHigherOrLower == 1) {
            return ceiling;
        }
        if (forceHigherOrLower == -1) {
            if (degrees == floor) {
                floor -= 30;
            }
            return floor;
        } else if (degrees - floor < ceiling - degrees) {
            return floor;
        } else {
            return ceiling;
        }
    }

    public RadialTimePickerView(Context context) {
        this(context, null);
    }

    public RadialTimePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 16843933);
    }

    public RadialTimePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RadialTimePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs);
        this.HOURS_TO_MINUTES = new FloatProperty<RadialTimePickerView>("hoursToMinutes") { // from class: android.widget.RadialTimePickerView.1
            @Override // android.util.Property
            public Float get(RadialTimePickerView radialTimePickerView) {
                return Float.valueOf(radialTimePickerView.mHoursToMinutes);
            }

            @Override // android.util.FloatProperty
            public void setValue(RadialTimePickerView object, float value) {
                object.mHoursToMinutes = value;
                object.invalidate();
            }
        };
        this.mHours12Texts = new String[12];
        this.mOuterHours24Texts = new String[12];
        this.mInnerHours24Texts = new String[12];
        this.mMinutesTexts = new String[12];
        this.mPaint = r3;
        Paint paint = new Paint();
        this.mPaintCenter = paint;
        this.mPaintSelector = r6;
        Paint paint2 = new Paint();
        this.mPaintBackground = paint2;
        this.mTextColor = new ColorStateList[3];
        this.mTextSize = r8;
        this.mTextInset = r5;
        this.mOuterTextX = (float[][]) Array.newInstance(Float.TYPE, 2, 12);
        this.mOuterTextY = (float[][]) Array.newInstance(Float.TYPE, 2, 12);
        this.mInnerTextX = new float[12];
        this.mInnerTextY = new float[12];
        this.mSelectionDegrees = new int[2];
        this.mSelectorPath = new Path();
        this.mInputEnabled = true;
        this.mChangedDuringTouch = false;
        applyAttributes(attrs, defStyleAttr, defStyleRes);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(16842803, outValue, true);
        this.mDisabledAlpha = outValue.getFloat();
        this.mTypeface = Typeface.create(Typeface.DEFAULT_FAMILY, 0);
        Paint paint3 = new Paint();
        paint3.setAntiAlias(true);
        r3[0].setTextAlign(Paint.Align.CENTER);
        Paint paint4 = new Paint();
        Paint[] paintArr = {paint3, paint4};
        paint4.setAntiAlias(true);
        paintArr[1].setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        Paint paint5 = new Paint();
        paint5.setAntiAlias(true);
        Paint paint6 = new Paint();
        paint6.setAntiAlias(true);
        Paint paint7 = new Paint();
        Paint[] paintArr2 = {paint5, paint6, paint7};
        paint7.setAntiAlias(true);
        paintArr2[2].setStrokeWidth(2.0f);
        paint2.setAntiAlias(true);
        Resources res = getResources();
        this.mSelectorRadius = res.getDimensionPixelSize(C4057R.dimen.timepicker_selector_radius);
        this.mSelectorStroke = res.getDimensionPixelSize(C4057R.dimen.timepicker_selector_stroke);
        this.mSelectorDotRadius = res.getDimensionPixelSize(C4057R.dimen.timepicker_selector_dot_radius);
        this.mCenterDotRadius = res.getDimensionPixelSize(C4057R.dimen.timepicker_center_dot_radius);
        int[] iArr = {res.getDimensionPixelSize(C4057R.dimen.timepicker_text_size_normal), res.getDimensionPixelSize(C4057R.dimen.timepicker_text_size_normal), res.getDimensionPixelSize(C4057R.dimen.timepicker_text_size_inner)};
        int[] iArr2 = {res.getDimensionPixelSize(C4057R.dimen.timepicker_text_inset_normal), res.getDimensionPixelSize(C4057R.dimen.timepicker_text_inset_normal), res.getDimensionPixelSize(C4057R.dimen.timepicker_text_inset_inner)};
        this.mShowHours = true;
        this.mHoursToMinutes = 0.0f;
        this.mIs24HourMode = false;
        this.mAmOrPm = 0;
        RadialPickerTouchHelper radialPickerTouchHelper = new RadialPickerTouchHelper();
        this.mTouchHelper = radialPickerTouchHelper;
        setAccessibilityDelegate(radialPickerTouchHelper);
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        initHoursAndMinutesText();
        initData();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int currentHour = calendar.get(11);
        int currentMinute = calendar.get(12);
        setCurrentHourInternal(currentHour, false, false);
        setCurrentMinuteInternal(currentMinute, false);
        setHapticFeedbackEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyAttributes(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        int selectorActivatedColor;
        Context context = getContext();
        TypedArray a = getContext().obtainStyledAttributes(attrs, C4057R.styleable.TimePicker, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.TimePicker, attrs, a, defStyleAttr, defStyleRes);
        ColorStateList numbersTextColor = a.getColorStateList(3);
        ColorStateList numbersInnerTextColor = a.getColorStateList(9);
        this.mTextColor[0] = numbersTextColor == null ? ColorStateList.valueOf(-65281) : numbersTextColor;
        this.mTextColor[2] = numbersInnerTextColor == null ? ColorStateList.valueOf(-65281) : numbersInnerTextColor;
        ColorStateList[] colorStateListArr = this.mTextColor;
        colorStateListArr[1] = colorStateListArr[0];
        ColorStateList selectorColors = a.getColorStateList(5);
        if (selectorColors != null) {
            int[] stateSetEnabledActivated = StateSet.get(40);
            selectorActivatedColor = selectorColors.getColorForState(stateSetEnabledActivated, 0);
        } else {
            selectorActivatedColor = -65281;
        }
        this.mPaintCenter.setColor(selectorActivatedColor);
        int[] stateSetActivated = StateSet.get(40);
        this.mSelectorColor = selectorActivatedColor;
        this.mSelectorDotColor = this.mTextColor[0].getColorForState(stateSetActivated, 0);
        this.mPaintBackground.setColor(a.getColor(4, context.getColor(C4057R.color.timepicker_default_numbers_background_color_material)));
        a.recycle();
    }

    public void initialize(int hour, int minute, boolean is24HourMode) {
        if (this.mIs24HourMode != is24HourMode) {
            this.mIs24HourMode = is24HourMode;
            initData();
        }
        setCurrentHourInternal(hour, false, false);
        setCurrentMinuteInternal(minute, false);
    }

    public void setCurrentItemShowing(int item, boolean animate) {
        switch (item) {
            case 0:
                showHours(animate);
                return;
            case 1:
                showMinutes(animate);
                return;
            default:
                Log.m110e(TAG, "ClockView does not support showing item " + item);
                return;
        }
    }

    public int getCurrentItemShowing() {
        return !this.mShowHours ? 1 : 0;
    }

    public void setOnValueSelectedListener(OnValueSelectedListener listener) {
        this.mListener = listener;
    }

    public void setCurrentHour(int hour) {
        setCurrentHourInternal(hour, true, false);
    }

    private void setCurrentHourInternal(int hour, boolean callback, boolean autoAdvance) {
        OnValueSelectedListener onValueSelectedListener;
        int degrees = (hour % 12) * 30;
        this.mSelectionDegrees[0] = degrees;
        int amOrPm = (hour == 0 || hour % 24 < 12) ? 0 : 1;
        boolean isOnInnerCircle = getInnerCircleForHour(hour);
        if (this.mAmOrPm != amOrPm || this.mIsOnInnerCircle != isOnInnerCircle) {
            this.mAmOrPm = amOrPm;
            this.mIsOnInnerCircle = isOnInnerCircle;
            initData();
            this.mTouchHelper.invalidateRoot();
        }
        invalidate();
        if (callback && (onValueSelectedListener = this.mListener) != null) {
            onValueSelectedListener.onValueSelected(0, hour, autoAdvance);
        }
    }

    public int getCurrentHour() {
        return getHourForDegrees(this.mSelectionDegrees[0], this.mIsOnInnerCircle);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getHourForDegrees(int degrees, boolean innerCircle) {
        int hour = (degrees / 30) % 12;
        if (this.mIs24HourMode) {
            if (!innerCircle && hour == 0) {
                return 12;
            }
            if (innerCircle && hour != 0) {
                return hour + 12;
            }
            return hour;
        } else if (this.mAmOrPm == 1) {
            return hour + 12;
        } else {
            return hour;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDegreesForHour(int hour) {
        if (this.mIs24HourMode) {
            if (hour >= 12) {
                hour -= 12;
            }
        } else if (hour == 12) {
            hour = 0;
        }
        return hour * 30;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getInnerCircleForHour(int hour) {
        return this.mIs24HourMode && (hour == 0 || hour > 12);
    }

    public void setCurrentMinute(int minute) {
        setCurrentMinuteInternal(minute, true);
    }

    private void setCurrentMinuteInternal(int minute, boolean callback) {
        OnValueSelectedListener onValueSelectedListener;
        this.mSelectionDegrees[1] = (minute % 60) * 6;
        invalidate();
        if (callback && (onValueSelectedListener = this.mListener) != null) {
            onValueSelectedListener.onValueSelected(1, minute, false);
        }
    }

    public int getCurrentMinute() {
        return getMinuteForDegrees(this.mSelectionDegrees[1]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getMinuteForDegrees(int degrees) {
        return degrees / 6;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDegreesForMinute(int minute) {
        return minute * 6;
    }

    public boolean setAmOrPm(int amOrPm) {
        if (this.mAmOrPm == amOrPm || this.mIs24HourMode) {
            return false;
        }
        this.mAmOrPm = amOrPm;
        invalidate();
        this.mTouchHelper.invalidateRoot();
        return true;
    }

    public int getAmOrPm() {
        return this.mAmOrPm;
    }

    public void showHours(boolean animate) {
        showPicker(true, animate);
    }

    public void showMinutes(boolean animate) {
        showPicker(false, animate);
    }

    private void initHoursAndMinutesText() {
        for (int i = 0; i < 12; i++) {
            String[] strArr = this.mHours12Texts;
            int[] iArr = HOURS_NUMBERS;
            strArr[i] = String.format("%d", Integer.valueOf(iArr[i]));
            this.mInnerHours24Texts[i] = String.format("%02d", Integer.valueOf(HOURS_NUMBERS_24[i]));
            this.mOuterHours24Texts[i] = String.format("%d", Integer.valueOf(iArr[i]));
            this.mMinutesTexts[i] = String.format("%02d", Integer.valueOf(MINUTES_NUMBERS[i]));
        }
    }

    private void initData() {
        if (this.mIs24HourMode) {
            this.mOuterTextHours = this.mOuterHours24Texts;
            this.mInnerTextHours = this.mInnerHours24Texts;
        } else {
            String[] strArr = this.mHours12Texts;
            this.mOuterTextHours = strArr;
            this.mInnerTextHours = strArr;
        }
        this.mMinutesText = this.mMinutesTexts;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }
        this.mXCenter = getWidth() / 2;
        int height = getHeight() / 2;
        this.mYCenter = height;
        int min = Math.min(this.mXCenter, height);
        this.mCircleRadius = min;
        int[] iArr = this.mTextInset;
        int i = iArr[2];
        int i2 = this.mSelectorRadius;
        this.mMinDistForInnerNumber = (min - i) - i2;
        int i3 = iArr[0];
        this.mMaxDistForOuterNumber = (min - i3) + i2;
        this.mHalfwayDist = min - ((i3 + i) / 2);
        calculatePositionsHours();
        calculatePositionsMinutes();
        this.mTouchHelper.invalidateRoot();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        float alphaMod = this.mInputEnabled ? 1.0f : this.mDisabledAlpha;
        drawCircleBackground(canvas);
        Path selectorPath = this.mSelectorPath;
        drawSelector(canvas, selectorPath);
        drawHours(canvas, selectorPath, alphaMod);
        drawMinutes(canvas, selectorPath, alphaMod);
        drawCenter(canvas, alphaMod);
    }

    private void showPicker(boolean hours, boolean animate) {
        if (this.mShowHours == hours) {
            return;
        }
        this.mShowHours = hours;
        if (animate) {
            animatePicker(hours, 500L);
        } else {
            ObjectAnimator objectAnimator = this.mHoursToMinutesAnimator;
            if (objectAnimator != null && objectAnimator.isStarted()) {
                this.mHoursToMinutesAnimator.cancel();
                this.mHoursToMinutesAnimator = null;
            }
            this.mHoursToMinutes = hours ? 0.0f : 1.0f;
        }
        initData();
        invalidate();
        this.mTouchHelper.invalidateRoot();
    }

    private void animatePicker(boolean hoursToMinutes, long duration) {
        float target = hoursToMinutes ? 0.0f : 1.0f;
        if (this.mHoursToMinutes == target) {
            ObjectAnimator objectAnimator = this.mHoursToMinutesAnimator;
            if (objectAnimator != null && objectAnimator.isStarted()) {
                this.mHoursToMinutesAnimator.cancel();
                this.mHoursToMinutesAnimator = null;
                return;
            }
            return;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, this.HOURS_TO_MINUTES, target);
        this.mHoursToMinutesAnimator = ofFloat;
        ofFloat.setAutoCancel(true);
        this.mHoursToMinutesAnimator.setDuration(duration);
        this.mHoursToMinutesAnimator.start();
    }

    private void drawCircleBackground(Canvas canvas) {
        canvas.drawCircle(this.mXCenter, this.mYCenter, this.mCircleRadius, this.mPaintBackground);
    }

    private void drawHours(Canvas canvas, Path selectorPath, float alphaMod) {
        int hoursAlpha = (int) (((1.0f - this.mHoursToMinutes) * 255.0f * alphaMod) + 0.5f);
        if (hoursAlpha > 0) {
            canvas.save(2);
            canvas.clipPath(selectorPath, Region.EnumC0813Op.DIFFERENCE);
            drawHoursClipped(canvas, hoursAlpha, false);
            canvas.restore();
            canvas.save(2);
            canvas.clipPath(selectorPath, Region.EnumC0813Op.INTERSECT);
            drawHoursClipped(canvas, hoursAlpha, true);
            canvas.restore();
        }
    }

    private void drawHoursClipped(Canvas canvas, int hoursAlpha, boolean showActivated) {
        String[] strArr;
        drawTextElements(canvas, this.mTextSize[0], this.mTypeface, this.mTextColor[0], this.mOuterTextHours, this.mOuterTextX[0], this.mOuterTextY[0], this.mPaint[0], hoursAlpha, showActivated && !this.mIsOnInnerCircle, this.mSelectionDegrees[0], showActivated);
        if (this.mIs24HourMode && (strArr = this.mInnerTextHours) != null) {
            drawTextElements(canvas, this.mTextSize[2], this.mTypeface, this.mTextColor[2], strArr, this.mInnerTextX, this.mInnerTextY, this.mPaint[0], hoursAlpha, showActivated && this.mIsOnInnerCircle, this.mSelectionDegrees[0], showActivated);
        }
    }

    private void drawMinutes(Canvas canvas, Path selectorPath, float alphaMod) {
        int minutesAlpha = (int) ((this.mHoursToMinutes * 255.0f * alphaMod) + 0.5f);
        if (minutesAlpha > 0) {
            canvas.save(2);
            canvas.clipPath(selectorPath, Region.EnumC0813Op.DIFFERENCE);
            drawMinutesClipped(canvas, minutesAlpha, false);
            canvas.restore();
            canvas.save(2);
            canvas.clipPath(selectorPath, Region.EnumC0813Op.INTERSECT);
            drawMinutesClipped(canvas, minutesAlpha, true);
            canvas.restore();
        }
    }

    private void drawMinutesClipped(Canvas canvas, int minutesAlpha, boolean showActivated) {
        drawTextElements(canvas, this.mTextSize[1], this.mTypeface, this.mTextColor[1], this.mMinutesText, this.mOuterTextX[1], this.mOuterTextY[1], this.mPaint[1], minutesAlpha, showActivated, this.mSelectionDegrees[1], showActivated);
    }

    private void drawCenter(Canvas canvas, float alphaMod) {
        this.mPaintCenter.setAlpha((int) ((255.0f * alphaMod) + 0.5f));
        canvas.drawCircle(this.mXCenter, this.mYCenter, this.mCenterDotRadius, this.mPaintCenter);
    }

    private int getMultipliedAlpha(int argb, int alpha) {
        return (int) ((Color.alpha(argb) * (alpha / 255.0d)) + 0.5d);
    }

    private void drawSelector(Canvas canvas, Path selectorPath) {
        int minutesAngleDeg;
        int hoursIndex = this.mIsOnInnerCircle ? 2 : 0;
        int[] iArr = this.mTextInset;
        int hoursInset = iArr[hoursIndex];
        int[] iArr2 = this.mSelectionDegrees;
        int hoursAngleDeg = iArr2[hoursIndex % 2];
        float hoursDotScale = iArr2[hoursIndex % 2] % 30 != 0 ? 1.0f : 0.0f;
        int minutesInset = iArr[1];
        int minutesAngleDeg2 = iArr2[1];
        float minutesDotScale = iArr2[1] % 30 == 0 ? 0.0f : 1.0f;
        int selRadius = this.mSelectorRadius;
        float selLength = this.mCircleRadius - MathUtils.lerp(hoursInset, minutesInset, this.mHoursToMinutes);
        double selAngleRad = Math.toRadians(MathUtils.lerpDeg(hoursAngleDeg, minutesAngleDeg2, this.mHoursToMinutes));
        float minutesDotScale2 = minutesDotScale;
        float selCenterX = this.mXCenter + (((float) Math.sin(selAngleRad)) * selLength);
        float selCenterY = this.mYCenter - (((float) Math.cos(selAngleRad)) * selLength);
        Paint paint = this.mPaintSelector[0];
        paint.setColor(this.mSelectorColor);
        canvas.drawCircle(selCenterX, selCenterY, selRadius, paint);
        if (selectorPath == null) {
            minutesAngleDeg = minutesAngleDeg2;
        } else {
            selectorPath.reset();
            minutesAngleDeg = minutesAngleDeg2;
            selectorPath.addCircle(selCenterX, selCenterY, selRadius, Path.Direction.CCW);
        }
        float dotScale = MathUtils.lerp(hoursDotScale, minutesDotScale2, this.mHoursToMinutes);
        if (dotScale > 0.0f) {
            Paint dotPaint = this.mPaintSelector[1];
            dotPaint.setColor(this.mSelectorDotColor);
            canvas.drawCircle(selCenterX, selCenterY, this.mSelectorDotRadius * dotScale, dotPaint);
        }
        double sin = Math.sin(selAngleRad);
        double cos = Math.cos(selAngleRad);
        float lineLength = selLength - selRadius;
        int i = this.mXCenter;
        int i2 = this.mCenterDotRadius;
        int centerX = i + ((int) (i2 * sin));
        int centerY = this.mYCenter - ((int) (i2 * cos));
        float linePointX = centerX + ((int) (lineLength * sin));
        float linePointY = centerY - ((int) (lineLength * cos));
        Paint linePaint = this.mPaintSelector[2];
        linePaint.setColor(this.mSelectorColor);
        linePaint.setStrokeWidth(this.mSelectorStroke);
        canvas.drawLine(this.mXCenter, this.mYCenter, linePointX, linePointY, linePaint);
    }

    private void calculatePositionsHours() {
        float numbersRadius = this.mCircleRadius - this.mTextInset[0];
        calculatePositions(this.mPaint[0], numbersRadius, this.mXCenter, this.mYCenter, this.mTextSize[0], this.mOuterTextX[0], this.mOuterTextY[0]);
        if (this.mIs24HourMode) {
            int innerNumbersRadius = this.mCircleRadius - this.mTextInset[2];
            calculatePositions(this.mPaint[0], innerNumbersRadius, this.mXCenter, this.mYCenter, this.mTextSize[2], this.mInnerTextX, this.mInnerTextY);
        }
    }

    private void calculatePositionsMinutes() {
        float numbersRadius = this.mCircleRadius - this.mTextInset[1];
        calculatePositions(this.mPaint[1], numbersRadius, this.mXCenter, this.mYCenter, this.mTextSize[1], this.mOuterTextX[1], this.mOuterTextY[1]);
    }

    private static void calculatePositions(Paint paint, float radius, float xCenter, float yCenter, float textSize, float[] x, float[] y) {
        paint.setTextSize(textSize);
        float yCenter2 = yCenter - ((paint.descent() + paint.ascent()) / 2.0f);
        for (int i = 0; i < 12; i++) {
            x[i] = xCenter - (COS_30[i] * radius);
            y[i] = yCenter2 - (SIN_30[i] * radius);
        }
    }

    private void drawTextElements(Canvas canvas, float textSize, Typeface typeface, ColorStateList textColor, String[] texts, float[] textX, float[] textY, Paint paint, int alpha, boolean showActivated, int activatedDegrees, boolean activatedOnly) {
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);
        float activatedIndex = activatedDegrees / 30.0f;
        int activatedFloor = (int) activatedIndex;
        int activatedCeil = ((int) Math.ceil(activatedIndex)) % 12;
        int i = 0;
        for (int i2 = 12; i < i2; i2 = 12) {
            boolean activated = activatedFloor == i || activatedCeil == i;
            if (!activatedOnly || activated) {
                int stateMask = ((showActivated && activated) ? 32 : 0) | 8;
                int color = textColor.getColorForState(StateSet.get(stateMask), 0);
                paint.setColor(color);
                paint.setAlpha(getMultipliedAlpha(color, alpha));
                canvas.drawText(texts[i], textX[i], textY[i], paint);
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDegreesFromXY(float x, float y, boolean constrainOutside) {
        int center;
        int index;
        if (this.mIs24HourMode && this.mShowHours) {
            index = this.mMinDistForInnerNumber;
            center = this.mMaxDistForOuterNumber;
        } else {
            int index2 = !this.mShowHours ? 1 : 0;
            int center2 = this.mCircleRadius - this.mTextInset[index2];
            int i = this.mSelectorRadius;
            int innerBound = center2 - i;
            center = i + center2;
            index = innerBound;
        }
        double dX = x - this.mXCenter;
        double dY = y - this.mYCenter;
        double distFromCenter = Math.sqrt((dX * dX) + (dY * dY));
        if (distFromCenter >= index) {
            if (constrainOutside && distFromCenter > center) {
                return -1;
            }
            int degrees = (int) (Math.toDegrees(Math.atan2(dY, dX) + 1.5707963267948966d) + 0.5d);
            if (degrees < 0) {
                return degrees + 360;
            }
            return degrees;
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getInnerCircleFromXY(float x, float y) {
        if (this.mIs24HourMode && this.mShowHours) {
            double dX = x - this.mXCenter;
            double dY = y - this.mYCenter;
            double distFromCenter = Math.sqrt((dX * dX) + (dY * dY));
            return distFromCenter <= ((double) this.mHalfwayDist);
        }
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mInputEnabled) {
            int action = event.getActionMasked();
            if (action == 2 || action == 1 || action == 0) {
                boolean forceSelection = false;
                boolean autoAdvance = false;
                if (action == 0) {
                    this.mChangedDuringTouch = false;
                } else if (action == 1) {
                    autoAdvance = true;
                    if (!this.mChangedDuringTouch) {
                        forceSelection = true;
                    }
                }
                this.mChangedDuringTouch |= handleTouchInput(event.getX(), event.getY(), forceSelection, autoAdvance);
            }
            return true;
        }
        return true;
    }

    private boolean handleTouchInput(float x, float y, boolean forceSelection, boolean autoAdvance) {
        int snapDegrees;
        int type;
        boolean valueChanged;
        boolean isOnInnerCircle = getInnerCircleFromXY(x, y);
        int degrees = getDegreesFromXY(x, y, false);
        if (degrees == -1) {
            return false;
        }
        animatePicker(this.mShowHours, 60L);
        if (this.mShowHours) {
            int snapDegrees2 = snapOnly30s(degrees, 0) % 360;
            valueChanged = (this.mIsOnInnerCircle == isOnInnerCircle && this.mSelectionDegrees[0] == snapDegrees2) ? false : true;
            this.mIsOnInnerCircle = isOnInnerCircle;
            this.mSelectionDegrees[0] = snapDegrees2;
            type = 0;
            snapDegrees = getCurrentHour();
        } else {
            int newValue = snapPrefer30s(degrees);
            int snapDegrees3 = newValue % 360;
            int[] iArr = this.mSelectionDegrees;
            boolean valueChanged2 = iArr[1] != snapDegrees3;
            iArr[1] = snapDegrees3;
            snapDegrees = getCurrentMinute();
            boolean z = valueChanged2;
            type = 1;
            valueChanged = z;
        }
        if (!valueChanged && !forceSelection && !autoAdvance) {
            return false;
        }
        OnValueSelectedListener onValueSelectedListener = this.mListener;
        if (onValueSelectedListener != null) {
            onValueSelectedListener.onValueSelected(type, snapDegrees, autoAdvance);
        }
        if (valueChanged || forceSelection) {
            performHapticFeedback(4);
            invalidate();
        }
        return true;
    }

    @Override // android.view.View
    public boolean dispatchHoverEvent(MotionEvent event) {
        if (this.mTouchHelper.dispatchHoverEvent(event)) {
            return true;
        }
        return super.dispatchHoverEvent(event);
    }

    public void setInputEnabled(boolean inputEnabled) {
        this.mInputEnabled = inputEnabled;
        invalidate();
    }

    @Override // android.view.View
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        if (!isEnabled()) {
            return null;
        }
        int degrees = getDegreesFromXY(event.getX(), event.getY(), false);
        if (degrees != -1) {
            return PointerIcon.getSystemIcon(getContext(), 1002);
        }
        return super.onResolvePointerIcon(event, pointerIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class RadialPickerTouchHelper extends ExploreByTouchHelper {
        private final int MASK_TYPE;
        private final int MASK_VALUE;
        private final int MINUTE_INCREMENT;
        private final int SHIFT_TYPE;
        private final int SHIFT_VALUE;
        private final int TYPE_HOUR;
        private final int TYPE_MINUTE;
        private final Rect mTempRect;

        public RadialPickerTouchHelper() {
            super(RadialTimePickerView.this);
            this.mTempRect = new Rect();
            this.TYPE_HOUR = 1;
            this.TYPE_MINUTE = 2;
            this.SHIFT_TYPE = 0;
            this.MASK_TYPE = 15;
            this.SHIFT_VALUE = 8;
            this.MASK_VALUE = 255;
            this.MINUTE_INCREMENT = 5;
        }

        @Override // android.view.View.AccessibilityDelegate
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
        }

        @Override // android.view.View.AccessibilityDelegate
        public boolean performAccessibilityAction(View host, int action, Bundle arguments) {
            if (super.performAccessibilityAction(host, action, arguments)) {
                return true;
            }
            switch (action) {
                case 4096:
                    adjustPicker(1);
                    return true;
                case 8192:
                    adjustPicker(-1);
                    return true;
                default:
                    return false;
            }
        }

        private void adjustPicker(int step) {
            int stepSize;
            int initialStep;
            int minValue;
            int maxValue;
            if (RadialTimePickerView.this.mShowHours) {
                stepSize = 1;
                int currentHour24 = RadialTimePickerView.this.getCurrentHour();
                if (RadialTimePickerView.this.mIs24HourMode) {
                    initialStep = currentHour24;
                    minValue = 0;
                    maxValue = 23;
                } else {
                    initialStep = hour24To12(currentHour24);
                    minValue = 1;
                    maxValue = 12;
                }
            } else {
                stepSize = 5;
                initialStep = RadialTimePickerView.this.getCurrentMinute() / 5;
                minValue = 0;
                maxValue = 55;
            }
            int nextValue = (initialStep + step) * stepSize;
            int clampedValue = MathUtils.constrain(nextValue, minValue, maxValue);
            if (RadialTimePickerView.this.mShowHours) {
                RadialTimePickerView.this.setCurrentHour(clampedValue);
            } else {
                RadialTimePickerView.this.setCurrentMinute(clampedValue);
            }
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float x, float y) {
            int minute;
            int degrees = RadialTimePickerView.this.getDegreesFromXY(x, y, true);
            if (degrees != -1) {
                int snapDegrees = RadialTimePickerView.snapOnly30s(degrees, 0) % 360;
                if (RadialTimePickerView.this.mShowHours) {
                    boolean isOnInnerCircle = RadialTimePickerView.this.getInnerCircleFromXY(x, y);
                    int hour24 = RadialTimePickerView.this.getHourForDegrees(snapDegrees, isOnInnerCircle);
                    int hour = RadialTimePickerView.this.mIs24HourMode ? hour24 : hour24To12(hour24);
                    int id = makeId(1, hour);
                    return id;
                }
                int current = RadialTimePickerView.this.getCurrentMinute();
                int touched = RadialTimePickerView.this.getMinuteForDegrees(degrees);
                int snapped = RadialTimePickerView.this.getMinuteForDegrees(snapDegrees);
                int currentOffset = getCircularDiff(current, touched, 60);
                int snappedOffset = getCircularDiff(snapped, touched, 60);
                if (currentOffset < snappedOffset) {
                    minute = current;
                } else {
                    minute = snapped;
                }
                int id2 = makeId(2, minute);
                return id2;
            }
            return Integer.MIN_VALUE;
        }

        private int getCircularDiff(int first, int second, int max) {
            int diff = Math.abs(first - second);
            int midpoint = max / 2;
            return diff > midpoint ? max - diff : diff;
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(IntArray virtualViewIds) {
            if (RadialTimePickerView.this.mShowHours) {
                int min = !RadialTimePickerView.this.mIs24HourMode ? 1 : 0;
                int max = RadialTimePickerView.this.mIs24HourMode ? 23 : 12;
                for (int i = min; i <= max; i++) {
                    virtualViewIds.add(makeId(1, i));
                }
                return;
            }
            int current = RadialTimePickerView.this.getCurrentMinute();
            for (int i2 = 0; i2 < 60; i2 += 5) {
                virtualViewIds.add(makeId(2, i2));
                if (current > i2 && current < i2 + 5) {
                    virtualViewIds.add(makeId(2, current));
                }
            }
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected void onPopulateEventForVirtualView(int virtualViewId, AccessibilityEvent event) {
            event.setClassName(getClass().getName());
            int type = getTypeFromId(virtualViewId);
            int value = getValueFromId(virtualViewId);
            CharSequence description = getVirtualViewDescription(type, value);
            event.setContentDescription(description);
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfo node) {
            node.setClassName(getClass().getName());
            node.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            int type = getTypeFromId(virtualViewId);
            int value = getValueFromId(virtualViewId);
            CharSequence description = getVirtualViewDescription(type, value);
            node.setContentDescription(description);
            getBoundsForVirtualView(virtualViewId, this.mTempRect);
            node.setBoundsInParent(this.mTempRect);
            boolean selected = isVirtualViewSelected(type, value);
            node.setSelected(selected);
            int nextId = getVirtualViewIdAfter(type, value);
            if (nextId != Integer.MIN_VALUE) {
                node.setTraversalBefore(RadialTimePickerView.this, nextId);
            }
        }

        private int getVirtualViewIdAfter(int type, int value) {
            if (type == 1) {
                int nextValue = value + 1;
                int max = RadialTimePickerView.this.mIs24HourMode ? 23 : 12;
                if (nextValue <= max) {
                    return makeId(type, nextValue);
                }
                return Integer.MIN_VALUE;
            } else if (type == 2) {
                int current = RadialTimePickerView.this.getCurrentMinute();
                int snapValue = value - (value % 5);
                int nextValue2 = snapValue + 5;
                if (value < current && nextValue2 > current) {
                    return makeId(type, current);
                }
                if (nextValue2 < 60) {
                    return makeId(type, nextValue2);
                }
                return Integer.MIN_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }

        @Override // com.android.internal.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments) {
            if (action == 16) {
                int type = getTypeFromId(virtualViewId);
                int value = getValueFromId(virtualViewId);
                if (type == 1) {
                    int hour = RadialTimePickerView.this.mIs24HourMode ? value : hour12To24(value, RadialTimePickerView.this.mAmOrPm);
                    RadialTimePickerView.this.setCurrentHour(hour);
                    return true;
                } else if (type == 2) {
                    RadialTimePickerView.this.setCurrentMinute(value);
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        private int hour12To24(int hour12, int amOrPm) {
            if (hour12 == 12) {
                if (amOrPm != 0) {
                    return hour12;
                }
                return 0;
            } else if (amOrPm != 1) {
                return hour12;
            } else {
                int hour24 = hour12 + 12;
                return hour24;
            }
        }

        private int hour24To12(int hour24) {
            if (hour24 == 0) {
                return 12;
            }
            if (hour24 > 12) {
                return hour24 - 12;
            }
            return hour24;
        }

        private void getBoundsForVirtualView(int virtualViewId, Rect bounds) {
            float centerRadius;
            float degrees;
            float radius;
            int type = getTypeFromId(virtualViewId);
            int value = getValueFromId(virtualViewId);
            if (type != 1) {
                if (type == 2) {
                    centerRadius = RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[1];
                    degrees = RadialTimePickerView.this.getDegreesForMinute(value);
                    radius = RadialTimePickerView.this.mSelectorRadius;
                } else {
                    centerRadius = 0.0f;
                    degrees = 0.0f;
                    radius = 0.0f;
                }
            } else {
                boolean innerCircle = RadialTimePickerView.this.getInnerCircleForHour(value);
                if (innerCircle) {
                    centerRadius = RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[2];
                    radius = RadialTimePickerView.this.mSelectorRadius;
                } else {
                    centerRadius = RadialTimePickerView.this.mCircleRadius - RadialTimePickerView.this.mTextInset[0];
                    radius = RadialTimePickerView.this.mSelectorRadius;
                }
                degrees = RadialTimePickerView.this.getDegreesForHour(value);
            }
            double radians = Math.toRadians(degrees);
            float xCenter = RadialTimePickerView.this.mXCenter + (((float) Math.sin(radians)) * centerRadius);
            float yCenter = RadialTimePickerView.this.mYCenter - (((float) Math.cos(radians)) * centerRadius);
            bounds.set((int) (xCenter - radius), (int) (yCenter - radius), (int) (xCenter + radius), (int) (yCenter + radius));
        }

        private CharSequence getVirtualViewDescription(int type, int value) {
            if (type == 1 || type == 2) {
                CharSequence description = Integer.toString(value);
                return description;
            }
            return null;
        }

        private boolean isVirtualViewSelected(int type, int value) {
            return type == 1 ? RadialTimePickerView.this.getCurrentHour() == value : type == 2 && RadialTimePickerView.this.getCurrentMinute() == value;
        }

        private int makeId(int type, int value) {
            return (type << 0) | (value << 8);
        }

        private int getTypeFromId(int id) {
            return (id >>> 0) & 15;
        }

        private int getValueFromId(int id) {
            return (id >>> 8) & 255;
        }
    }
}
