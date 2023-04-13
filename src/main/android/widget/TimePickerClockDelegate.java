package android.widget;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.icu.text.DecimalFormatSymbols;
import android.p008os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.style.TtsSpan;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadialTimePickerView;
import android.widget.RelativeLayout;
import android.widget.TextInputTimePickerView;
import android.widget.TimePicker;
import com.android.internal.C4057R;
import com.android.internal.widget.NumericTextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class TimePickerClockDelegate extends TimePicker.AbstractTimePickerDelegate {

    /* renamed from: AM */
    private static final int f542AM = 0;
    private static final long DELAY_COMMIT_MILLIS = 2000;
    private static final int FROM_EXTERNAL_API = 0;
    private static final int FROM_INPUT_PICKER = 2;
    private static final int FROM_RADIAL_PICKER = 1;
    private static final int HOURS_IN_HALF_DAY = 12;
    private static final int HOUR_INDEX = 0;
    private static final int MINUTE_INDEX = 1;

    /* renamed from: PM */
    private static final int f543PM = 1;
    private boolean mAllowAutoAdvance;
    private final RadioButton mAmLabel;
    private final View mAmPmLayout;
    private final View.OnClickListener mClickListener;
    private final Runnable mCommitHour;
    private final Runnable mCommitMinute;
    private int mCurrentHour;
    private int mCurrentMinute;
    private final NumericTextView.OnValueChangedListener mDigitEnteredListener;
    private final View.OnFocusChangeListener mFocusListener;
    private boolean mHourFormatShowLeadingZero;
    private boolean mHourFormatStartsAtZero;
    private final NumericTextView mHourView;
    private boolean mIs24Hour;
    private boolean mIsAmPmAtLeft;
    private boolean mIsAmPmAtTop;
    private boolean mIsEnabled;
    private boolean mLastAnnouncedIsHour;
    private CharSequence mLastAnnouncedText;
    private final NumericTextView mMinuteView;
    private final RadialTimePickerView.OnValueSelectedListener mOnValueSelectedListener;
    private final TextInputTimePickerView.OnValueTypedListener mOnValueTypedListener;
    private final RadioButton mPmLabel;
    private boolean mRadialPickerModeEnabled;
    private final View mRadialTimePickerHeader;
    private final ImageButton mRadialTimePickerModeButton;
    private final String mRadialTimePickerModeEnabledDescription;
    private final RadialTimePickerView mRadialTimePickerView;
    private final String mSelectHours;
    private final String mSelectMinutes;
    private final TextView mSeparatorView;
    private final Calendar mTempCalendar;
    private final View mTextInputPickerHeader;
    private final String mTextInputPickerModeEnabledDescription;
    private final TextInputTimePickerView mTextInputPickerView;
    private static final int[] ATTRS_TEXT_COLOR = {16842904};
    private static final int[] ATTRS_DISABLED_ALPHA = {16842803};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    private @interface ChangeSource {
    }

    public TimePickerClockDelegate(TimePicker delegator, Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(delegator, context);
        RadialTimePickerView.OnValueSelectedListener onValueSelectedListener;
        ColorStateList headerTextColor;
        this.mRadialPickerModeEnabled = true;
        this.mIsEnabled = true;
        this.mIsAmPmAtLeft = false;
        this.mIsAmPmAtTop = false;
        RadialTimePickerView.OnValueSelectedListener onValueSelectedListener2 = new RadialTimePickerView.OnValueSelectedListener() { // from class: android.widget.TimePickerClockDelegate.2
            @Override // android.widget.RadialTimePickerView.OnValueSelectedListener
            public void onValueSelected(int pickerType, int newValue, boolean autoAdvance) {
                boolean valueChanged = false;
                switch (pickerType) {
                    case 0:
                        if (TimePickerClockDelegate.this.getHour() != newValue) {
                            valueChanged = true;
                        }
                        boolean isTransition = TimePickerClockDelegate.this.mAllowAutoAdvance && autoAdvance;
                        TimePickerClockDelegate.this.setHourInternal(newValue, 1, !isTransition, true);
                        if (isTransition) {
                            TimePickerClockDelegate.this.setCurrentItemShowing(1, true, false);
                            int localizedHour = TimePickerClockDelegate.this.getLocalizedHour(newValue);
                            TimePickerClockDelegate.this.mDelegator.announceForAccessibility(localizedHour + ". " + TimePickerClockDelegate.this.mSelectMinutes);
                            break;
                        }
                        break;
                    case 1:
                        if (TimePickerClockDelegate.this.getMinute() != newValue) {
                            valueChanged = true;
                        }
                        TimePickerClockDelegate.this.setMinuteInternal(newValue, 1, true);
                        break;
                }
                if (TimePickerClockDelegate.this.mOnTimeChangedListener != null && valueChanged) {
                    TimePickerClockDelegate.this.mOnTimeChangedListener.onTimeChanged(TimePickerClockDelegate.this.mDelegator, TimePickerClockDelegate.this.getHour(), TimePickerClockDelegate.this.getMinute());
                }
            }
        };
        this.mOnValueSelectedListener = onValueSelectedListener2;
        TextInputTimePickerView.OnValueTypedListener onValueTypedListener = new TextInputTimePickerView.OnValueTypedListener() { // from class: android.widget.TimePickerClockDelegate.3
            @Override // android.widget.TextInputTimePickerView.OnValueTypedListener
            public void onValueChanged(int pickerType, int newValue) {
                switch (pickerType) {
                    case 0:
                        TimePickerClockDelegate.this.setHourInternal(newValue, 2, false, true);
                        return;
                    case 1:
                        TimePickerClockDelegate.this.setMinuteInternal(newValue, 2, true);
                        return;
                    case 2:
                        TimePickerClockDelegate.this.setAmOrPm(newValue);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mOnValueTypedListener = onValueTypedListener;
        NumericTextView.OnValueChangedListener onValueChangedListener = new NumericTextView.OnValueChangedListener() { // from class: android.widget.TimePickerClockDelegate.4
            @Override // com.android.internal.widget.NumericTextView.OnValueChangedListener
            public void onValueChanged(NumericTextView view, int value, boolean isValid, boolean isFinished) {
                Runnable commitCallback;
                View nextFocusTarget;
                if (view == TimePickerClockDelegate.this.mHourView) {
                    commitCallback = TimePickerClockDelegate.this.mCommitHour;
                    nextFocusTarget = view.isFocused() ? TimePickerClockDelegate.this.mMinuteView : null;
                } else if (view == TimePickerClockDelegate.this.mMinuteView) {
                    commitCallback = TimePickerClockDelegate.this.mCommitMinute;
                    nextFocusTarget = null;
                } else {
                    return;
                }
                view.removeCallbacks(commitCallback);
                if (isValid) {
                    if (isFinished) {
                        commitCallback.run();
                        if (nextFocusTarget != null) {
                            nextFocusTarget.requestFocus();
                            return;
                        }
                        return;
                    }
                    view.postDelayed(commitCallback, TimePickerClockDelegate.DELAY_COMMIT_MILLIS);
                }
            }
        };
        this.mDigitEnteredListener = onValueChangedListener;
        this.mCommitHour = new Runnable() { // from class: android.widget.TimePickerClockDelegate.5
            @Override // java.lang.Runnable
            public void run() {
                TimePickerClockDelegate timePickerClockDelegate = TimePickerClockDelegate.this;
                timePickerClockDelegate.setHour(timePickerClockDelegate.mHourView.getValue());
            }
        };
        this.mCommitMinute = new Runnable() { // from class: android.widget.TimePickerClockDelegate.6
            @Override // java.lang.Runnable
            public void run() {
                TimePickerClockDelegate timePickerClockDelegate = TimePickerClockDelegate.this;
                timePickerClockDelegate.setMinute(timePickerClockDelegate.mMinuteView.getValue());
            }
        };
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() { // from class: android.widget.TimePickerClockDelegate.7
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean focused) {
                if (focused) {
                    switch (v.getId()) {
                        case C4057R.C4059id.am_label /* 16908777 */:
                            TimePickerClockDelegate.this.setAmOrPm(0);
                            break;
                        case C4057R.C4059id.hours /* 16909099 */:
                            TimePickerClockDelegate.this.setCurrentItemShowing(0, true, true);
                            break;
                        case C4057R.C4059id.minutes /* 16909244 */:
                            TimePickerClockDelegate.this.setCurrentItemShowing(1, true, true);
                            break;
                        case C4057R.C4059id.pm_label /* 16909372 */:
                            TimePickerClockDelegate.this.setAmOrPm(1);
                            break;
                        default:
                            return;
                    }
                    TimePickerClockDelegate.this.tryVibrate();
                }
            }
        };
        this.mFocusListener = onFocusChangeListener;
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: android.widget.TimePickerClockDelegate.8
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                switch (v.getId()) {
                    case C4057R.C4059id.am_label /* 16908777 */:
                        TimePickerClockDelegate.this.setAmOrPm(0);
                        break;
                    case C4057R.C4059id.hours /* 16909099 */:
                        TimePickerClockDelegate.this.setCurrentItemShowing(0, true, true);
                        break;
                    case C4057R.C4059id.minutes /* 16909244 */:
                        TimePickerClockDelegate.this.setCurrentItemShowing(1, true, true);
                        break;
                    case C4057R.C4059id.pm_label /* 16909372 */:
                        TimePickerClockDelegate.this.setAmOrPm(1);
                        break;
                    default:
                        return;
                }
                TimePickerClockDelegate.this.tryVibrate();
            }
        };
        this.mClickListener = onClickListener;
        TypedArray a = this.mContext.obtainStyledAttributes(attrs, C4057R.styleable.TimePicker, defStyleAttr, defStyleRes);
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resources res = this.mContext.getResources();
        this.mSelectHours = res.getString(C4057R.string.select_hours);
        this.mSelectMinutes = res.getString(C4057R.string.select_minutes);
        int layoutResourceId = a.getResourceId(12, C4057R.layout.time_picker_material);
        View mainView = inflater.inflate(layoutResourceId, delegator);
        mainView.setSaveFromParentEnabled(false);
        View findViewById = mainView.findViewById(C4057R.C4059id.time_header);
        this.mRadialTimePickerHeader = findViewById;
        findViewById.setOnTouchListener(new NearestTouchDelegate());
        NumericTextView numericTextView = (NumericTextView) mainView.findViewById(C4057R.C4059id.hours);
        this.mHourView = numericTextView;
        numericTextView.setOnClickListener(onClickListener);
        numericTextView.setOnFocusChangeListener(onFocusChangeListener);
        numericTextView.setOnDigitEnteredListener(onValueChangedListener);
        numericTextView.setAccessibilityDelegate(new ClickActionDelegate(context, C4057R.string.select_hours));
        TextView textView = (TextView) mainView.findViewById(C4057R.C4059id.separator);
        this.mSeparatorView = textView;
        NumericTextView numericTextView2 = (NumericTextView) mainView.findViewById(C4057R.C4059id.minutes);
        this.mMinuteView = numericTextView2;
        numericTextView2.setOnClickListener(onClickListener);
        numericTextView2.setOnFocusChangeListener(onFocusChangeListener);
        numericTextView2.setOnDigitEnteredListener(onValueChangedListener);
        numericTextView2.setAccessibilityDelegate(new ClickActionDelegate(context, C4057R.string.select_minutes));
        numericTextView2.setRange(0, 59);
        View findViewById2 = mainView.findViewById(C4057R.C4059id.ampm_layout);
        this.mAmPmLayout = findViewById2;
        findViewById2.setOnTouchListener(new NearestTouchDelegate());
        String[] amPmStrings = TimePicker.getAmPmStrings(context);
        RadioButton radioButton = (RadioButton) findViewById2.findViewById(C4057R.C4059id.am_label);
        this.mAmLabel = radioButton;
        radioButton.setText(obtainVerbatim(amPmStrings[0]));
        radioButton.setOnClickListener(onClickListener);
        ensureMinimumTextWidth(radioButton);
        RadioButton radioButton2 = (RadioButton) findViewById2.findViewById(C4057R.C4059id.pm_label);
        this.mPmLabel = radioButton2;
        radioButton2.setText(obtainVerbatim(amPmStrings[1]));
        radioButton2.setOnClickListener(onClickListener);
        ensureMinimumTextWidth(radioButton2);
        int timeHeaderTextAppearance = a.getResourceId(1, 0);
        if (timeHeaderTextAppearance == 0) {
            onValueSelectedListener = onValueSelectedListener2;
            headerTextColor = null;
        } else {
            onValueSelectedListener = onValueSelectedListener2;
            TypedArray textAppearance = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, timeHeaderTextAppearance);
            ColorStateList legacyHeaderTextColor = textAppearance.getColorStateList(0);
            headerTextColor = applyLegacyColorFixes(legacyHeaderTextColor);
            textAppearance.recycle();
        }
        headerTextColor = headerTextColor == null ? a.getColorStateList(11) : headerTextColor;
        View findViewById3 = mainView.findViewById(C4057R.C4059id.input_header);
        this.mTextInputPickerHeader = findViewById3;
        if (headerTextColor != null) {
            numericTextView.setTextColor(headerTextColor);
            textView.setTextColor(headerTextColor);
            numericTextView2.setTextColor(headerTextColor);
            radioButton.setTextColor(headerTextColor);
            radioButton2.setTextColor(headerTextColor);
        }
        if (a.hasValueOrEmpty(0)) {
            findViewById.setBackground(a.getDrawable(0));
            findViewById3.setBackground(a.getDrawable(0));
        }
        a.recycle();
        RadialTimePickerView radialTimePickerView = (RadialTimePickerView) mainView.findViewById(C4057R.C4059id.radial_picker);
        this.mRadialTimePickerView = radialTimePickerView;
        radialTimePickerView.applyAttributes(attrs, defStyleAttr, defStyleRes);
        radialTimePickerView.setOnValueSelectedListener(onValueSelectedListener);
        TextInputTimePickerView textInputTimePickerView = (TextInputTimePickerView) mainView.findViewById(C4057R.C4059id.input_mode);
        this.mTextInputPickerView = textInputTimePickerView;
        textInputTimePickerView.setListener(onValueTypedListener);
        ImageButton imageButton = (ImageButton) mainView.findViewById(C4057R.C4059id.toggle_mode);
        this.mRadialTimePickerModeButton = imageButton;
        imageButton.setOnClickListener(new View.OnClickListener() { // from class: android.widget.TimePickerClockDelegate.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                TimePickerClockDelegate.this.toggleRadialPickerMode();
            }
        });
        this.mRadialTimePickerModeEnabledDescription = context.getResources().getString(C4057R.string.time_picker_radial_mode_description);
        this.mTextInputPickerModeEnabledDescription = context.getResources().getString(C4057R.string.time_picker_text_input_mode_description);
        this.mAllowAutoAdvance = true;
        updateHourFormat();
        Calendar calendar = Calendar.getInstance(this.mLocale);
        this.mTempCalendar = calendar;
        int currentHour = calendar.get(11);
        int currentMinute = calendar.get(12);
        initialize(currentHour, currentMinute, this.mIs24Hour, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleRadialPickerMode() {
        if (this.mRadialPickerModeEnabled) {
            this.mRadialTimePickerView.setVisibility(8);
            this.mRadialTimePickerHeader.setVisibility(8);
            this.mTextInputPickerHeader.setVisibility(0);
            this.mTextInputPickerView.setVisibility(0);
            this.mRadialTimePickerModeButton.setImageResource(C4057R.C4058drawable.btn_clock_material);
            this.mRadialTimePickerModeButton.setContentDescription(this.mRadialTimePickerModeEnabledDescription);
            this.mRadialPickerModeEnabled = false;
            return;
        }
        this.mRadialTimePickerView.setVisibility(0);
        this.mRadialTimePickerHeader.setVisibility(0);
        this.mTextInputPickerHeader.setVisibility(8);
        this.mTextInputPickerView.setVisibility(8);
        this.mRadialTimePickerModeButton.setImageResource(C4057R.C4058drawable.btn_keyboard_key_material);
        this.mRadialTimePickerModeButton.setContentDescription(this.mTextInputPickerModeEnabledDescription);
        updateTextInputPicker();
        InputMethodManager imm = (InputMethodManager) this.mContext.getSystemService(InputMethodManager.class);
        if (imm != null) {
            imm.hideSoftInputFromWindow(this.mDelegator.getWindowToken(), 0);
        }
        this.mRadialPickerModeEnabled = true;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public boolean validateInput() {
        return this.mTextInputPickerView.validateInput();
    }

    private static void ensureMinimumTextWidth(TextView v) {
        v.measure(0, 0);
        int minWidth = v.getMeasuredWidth();
        v.setMinWidth(minWidth);
        v.setMinimumWidth(minWidth);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0050  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0072 A[LOOP:1: B:34:0x006e->B:36:0x0072, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void updateHourFormat() {
        int i;
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(this.mLocale, this.mIs24Hour ? "Hm" : "hm");
        int lengthPattern = bestDateTimePattern.length();
        boolean showLeadingZero = false;
        char hourFormat = 0;
        for (int i2 = 0; i2 < lengthPattern; i2++) {
            char c = bestDateTimePattern.charAt(i2);
            if (c == 'H' || c == 'h' || c == 'K' || c == 'k') {
                hourFormat = c;
                if (i2 + 1 < lengthPattern && c == bestDateTimePattern.charAt(i2 + 1)) {
                    showLeadingZero = true;
                }
                this.mHourFormatShowLeadingZero = showLeadingZero;
                boolean z = (hourFormat != 'K' || hourFormat == 'H') ? 1 : 0;
                this.mHourFormatStartsAtZero = z;
                int minHour = 1 ^ z;
                int maxHour = (!this.mIs24Hour ? 23 : 11) + minHour;
                this.mHourView.setRange(minHour, maxHour);
                this.mHourView.setShowLeadingZeroes(this.mHourFormatShowLeadingZero);
                String[] digits = DecimalFormatSymbols.getInstance(this.mLocale).getDigitStrings();
                int maxCharLength = 0;
                for (i = 0; i < 10; i++) {
                    maxCharLength = Math.max(maxCharLength, digits[i].length());
                }
                this.mTextInputPickerView.setHourFormat(maxCharLength * 2);
            }
        }
        this.mHourFormatShowLeadingZero = showLeadingZero;
        if (hourFormat != 'K') {
        }
        this.mHourFormatStartsAtZero = z;
        int minHour2 = 1 ^ z;
        int maxHour2 = (!this.mIs24Hour ? 23 : 11) + minHour2;
        this.mHourView.setRange(minHour2, maxHour2);
        this.mHourView.setShowLeadingZeroes(this.mHourFormatShowLeadingZero);
        String[] digits2 = DecimalFormatSymbols.getInstance(this.mLocale).getDigitStrings();
        int maxCharLength2 = 0;
        while (i < 10) {
        }
        this.mTextInputPickerView.setHourFormat(maxCharLength2 * 2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final CharSequence obtainVerbatim(String text) {
        return new SpannableStringBuilder().append(text, new TtsSpan.VerbatimBuilder(text).build(), 0);
    }

    private ColorStateList applyLegacyColorFixes(ColorStateList color) {
        int activatedColor;
        int defaultColor;
        if (color == null || color.hasState(16843518)) {
            return color;
        }
        if (color.hasState(16842913)) {
            activatedColor = color.getColorForState(StateSet.get(10), 0);
            defaultColor = color.getColorForState(StateSet.get(8), 0);
        } else {
            activatedColor = color.getDefaultColor();
            TypedArray ta = this.mContext.obtainStyledAttributes(ATTRS_DISABLED_ALPHA);
            float disabledAlpha = ta.getFloat(0, 0.3f);
            ta.recycle();
            defaultColor = multiplyAlphaComponent(activatedColor, disabledAlpha);
        }
        if (activatedColor == 0 || defaultColor == 0) {
            return null;
        }
        int[][] stateSet = {new int[]{16843518}, new int[0]};
        int[] colors = {activatedColor, defaultColor};
        return new ColorStateList(stateSet, colors);
    }

    private int multiplyAlphaComponent(int color, float alphaMod) {
        int srcRgb = 16777215 & color;
        int srcAlpha = (color >> 24) & 255;
        int dstAlpha = (int) ((srcAlpha * alphaMod) + 0.5f);
        return (dstAlpha << 24) | srcRgb;
    }

    /* loaded from: classes4.dex */
    private static class ClickActionDelegate extends View.AccessibilityDelegate {
        private final AccessibilityNodeInfo.AccessibilityAction mClickAction;

        public ClickActionDelegate(Context context, int resId) {
            this.mClickAction = new AccessibilityNodeInfo.AccessibilityAction(16, context.getString(resId));
        }

        @Override // android.view.View.AccessibilityDelegate
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.addAction(this.mClickAction);
        }
    }

    private void initialize(int hourOfDay, int minute, boolean is24HourView, int index) {
        this.mCurrentHour = hourOfDay;
        this.mCurrentMinute = minute;
        this.mIs24Hour = is24HourView;
        updateUI(index);
    }

    private void updateUI(int index) {
        updateHeaderAmPm();
        updateHeaderHour(this.mCurrentHour, false);
        updateHeaderSeparator();
        updateHeaderMinute(this.mCurrentMinute, false);
        updateRadialPicker(index);
        updateTextInputPicker();
        this.mDelegator.invalidate();
    }

    private void updateTextInputPicker() {
        this.mTextInputPickerView.updateTextInputValues(getLocalizedHour(this.mCurrentHour), this.mCurrentMinute, this.mCurrentHour < 12 ? 0 : 1, this.mIs24Hour, this.mHourFormatStartsAtZero);
    }

    private void updateRadialPicker(int index) {
        this.mRadialTimePickerView.initialize(this.mCurrentHour, this.mCurrentMinute, this.mIs24Hour);
        setCurrentItemShowing(index, false, true);
    }

    private void updateHeaderAmPm() {
        if (this.mIs24Hour) {
            this.mAmPmLayout.setVisibility(8);
            return;
        }
        String dateTimePattern = DateFormat.getBestDateTimePattern(this.mLocale, "hm");
        boolean isAmPmAtStart = dateTimePattern.startsWith(FullBackup.APK_TREE_TOKEN);
        setAmPmStart(isAmPmAtStart);
        updateAmPmLabelStates(this.mCurrentHour < 12 ? 0 : 1);
    }

    private void setAmPmStart(boolean isAmPmAtStart) {
        boolean isAmPmAtLeft;
        int otherViewId;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.mAmPmLayout.getLayoutParams();
        if (params.getRule(1) != 0 || params.getRule(0) != 0) {
            int margin = (int) (this.mContext.getResources().getDisplayMetrics().density * 8.0f);
            if (TextUtils.getLayoutDirectionFromLocale(this.mLocale) == 0) {
                isAmPmAtLeft = isAmPmAtStart;
            } else {
                isAmPmAtLeft = !isAmPmAtStart;
            }
            if (isAmPmAtLeft) {
                params.removeRule(1);
                params.addRule(0, this.mHourView.getId());
            } else {
                params.removeRule(0);
                params.addRule(1, this.mMinuteView.getId());
            }
            if (isAmPmAtStart) {
                params.setMarginStart(0);
                params.setMarginEnd(margin);
            } else {
                params.setMarginStart(margin);
                params.setMarginEnd(0);
            }
            this.mIsAmPmAtLeft = isAmPmAtLeft;
        } else if (params.getRule(3) != 0 || params.getRule(2) != 0) {
            if (this.mIsAmPmAtTop == isAmPmAtStart) {
                return;
            }
            if (isAmPmAtStart) {
                otherViewId = params.getRule(3);
                params.removeRule(3);
                params.addRule(2, otherViewId);
            } else {
                otherViewId = params.getRule(2);
                params.removeRule(2);
                params.addRule(3, otherViewId);
            }
            View otherView = this.mRadialTimePickerHeader.findViewById(otherViewId);
            int top = otherView.getPaddingTop();
            int bottom = otherView.getPaddingBottom();
            int left = otherView.getPaddingLeft();
            int right = otherView.getPaddingRight();
            otherView.setPadding(left, bottom, right, top);
            this.mIsAmPmAtTop = isAmPmAtStart;
        }
        this.mAmPmLayout.setLayoutParams(params);
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setDate(int hour, int minute) {
        setHourInternal(hour, 0, true, false);
        setMinuteInternal(minute, 0, false);
        onTimeChanged();
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setHour(int hour) {
        setHourInternal(hour, 0, true, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHourInternal(int hour, int source, boolean announce, boolean notify) {
        if (this.mCurrentHour == hour) {
            return;
        }
        resetAutofilledValue();
        this.mCurrentHour = hour;
        updateHeaderHour(hour, announce);
        updateHeaderAmPm();
        if (source != 1) {
            this.mRadialTimePickerView.setCurrentHour(hour);
            this.mRadialTimePickerView.setAmOrPm(hour < 12 ? 0 : 1);
        }
        if (source != 2) {
            updateTextInputPicker();
        }
        this.mDelegator.invalidate();
        if (notify) {
            onTimeChanged();
        }
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public int getHour() {
        int currentHour = this.mRadialTimePickerView.getCurrentHour();
        if (this.mIs24Hour) {
            return currentHour;
        }
        if (this.mRadialTimePickerView.getAmOrPm() == 1) {
            return (currentHour % 12) + 12;
        }
        return currentHour % 12;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setMinute(int minute) {
        setMinuteInternal(minute, 0, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMinuteInternal(int minute, int source, boolean notify) {
        if (this.mCurrentMinute == minute) {
            return;
        }
        resetAutofilledValue();
        this.mCurrentMinute = minute;
        updateHeaderMinute(minute, true);
        if (source != 1) {
            this.mRadialTimePickerView.setCurrentMinute(minute);
        }
        if (source != 2) {
            updateTextInputPicker();
        }
        this.mDelegator.invalidate();
        if (notify) {
            onTimeChanged();
        }
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public int getMinute() {
        return this.mRadialTimePickerView.getCurrentMinute();
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setIs24Hour(boolean is24Hour) {
        if (this.mIs24Hour != is24Hour) {
            this.mIs24Hour = is24Hour;
            this.mCurrentHour = getHour();
            updateHourFormat();
            updateUI(this.mRadialTimePickerView.getCurrentItemShowing());
        }
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public boolean is24Hour() {
        return this.mIs24Hour;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void setEnabled(boolean enabled) {
        this.mHourView.setEnabled(enabled);
        this.mMinuteView.setEnabled(enabled);
        this.mAmLabel.setEnabled(enabled);
        this.mPmLabel.setEnabled(enabled);
        this.mRadialTimePickerView.setEnabled(enabled);
        this.mIsEnabled = enabled;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public int getBaseline() {
        return -1;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public Parcelable onSaveInstanceState(Parcelable superState) {
        return new TimePicker.AbstractTimePickerDelegate.SavedState(superState, getHour(), getMinute(), is24Hour(), getCurrentItemShowing());
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof TimePicker.AbstractTimePickerDelegate.SavedState) {
            TimePicker.AbstractTimePickerDelegate.SavedState ss = (TimePicker.AbstractTimePickerDelegate.SavedState) state;
            initialize(ss.getHour(), ss.getMinute(), ss.is24HourMode(), ss.getCurrentItemShowing());
            this.mRadialTimePickerView.invalidate();
        }
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        int flags;
        if (this.mIs24Hour) {
            flags = 1 | 128;
        } else {
            flags = 1 | 64;
        }
        this.mTempCalendar.set(11, getHour());
        this.mTempCalendar.set(12, getMinute());
        String selectedTime = DateUtils.formatDateTime(this.mContext, this.mTempCalendar.getTimeInMillis(), flags);
        String selectionMode = this.mRadialTimePickerView.getCurrentItemShowing() == 0 ? this.mSelectHours : this.mSelectMinutes;
        event.getText().add(selectedTime + " " + selectionMode);
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public View getHourView() {
        return this.mHourView;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public View getMinuteView() {
        return this.mMinuteView;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public View getAmView() {
        return this.mAmLabel;
    }

    @Override // android.widget.TimePicker.TimePickerDelegate
    public View getPmView() {
        return this.mPmLabel;
    }

    private int getCurrentItemShowing() {
        return this.mRadialTimePickerView.getCurrentItemShowing();
    }

    private void onTimeChanged() {
        this.mDelegator.sendAccessibilityEvent(4);
        if (this.mOnTimeChangedListener != null) {
            this.mOnTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
        }
        if (this.mAutoFillChangeListener != null) {
            this.mAutoFillChangeListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void tryVibrate() {
        this.mDelegator.performHapticFeedback(4);
    }

    private void updateAmPmLabelStates(int amOrPm) {
        boolean isAm = amOrPm == 0;
        this.mAmLabel.setActivated(isAm);
        this.mAmLabel.setChecked(isAm);
        boolean isPm = amOrPm == 1;
        this.mPmLabel.setActivated(isPm);
        this.mPmLabel.setChecked(isPm);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getLocalizedHour(int hourOfDay) {
        boolean z = this.mIs24Hour;
        if (!z) {
            hourOfDay %= 12;
        }
        if (!this.mHourFormatStartsAtZero && hourOfDay == 0) {
            return z ? 24 : 12;
        }
        return hourOfDay;
    }

    private void updateHeaderHour(int hourOfDay, boolean announce) {
        int localizedHour = getLocalizedHour(hourOfDay);
        this.mHourView.setValue(localizedHour);
        if (announce) {
            tryAnnounceForAccessibility(this.mHourView.getText(), true);
        }
    }

    private void updateHeaderMinute(int minuteOfHour, boolean announce) {
        this.mMinuteView.setValue(minuteOfHour);
        if (announce) {
            tryAnnounceForAccessibility(this.mMinuteView.getText(), false);
        }
    }

    private void updateHeaderSeparator() {
        String bestDateTimePattern = DateFormat.getBestDateTimePattern(this.mLocale, this.mIs24Hour ? "Hm" : "hm");
        String separatorText = getHourMinSeparatorFromPattern(bestDateTimePattern);
        this.mSeparatorView.setText(separatorText);
        this.mTextInputPickerView.updateSeparator(separatorText);
    }

    private static String getHourMinSeparatorFromPattern(String dateTimePattern) {
        boolean foundHourPattern = false;
        for (int i = 0; i < dateTimePattern.length(); i++) {
            switch (dateTimePattern.charAt(i)) {
                case ' ':
                    break;
                case '\'':
                    if (foundHourPattern) {
                        SpannableStringBuilder quotedSubstring = new SpannableStringBuilder(dateTimePattern.substring(i));
                        int quotedTextLength = DateFormat.appendQuotedText(quotedSubstring, 0);
                        return quotedSubstring.subSequence(0, quotedTextLength).toString();
                    }
                    break;
                case 'H':
                case 'K':
                case 'h':
                case 'k':
                    foundHourPattern = true;
                    break;
                default:
                    if (!foundHourPattern) {
                        break;
                    } else {
                        return Character.toString(dateTimePattern.charAt(i));
                    }
            }
        }
        return ":";
    }

    private static int lastIndexOfAny(String str, char[] any) {
        int lengthAny = any.length;
        if (lengthAny > 0) {
            for (int i = str.length() - 1; i >= 0; i--) {
                char c = str.charAt(i);
                for (char c2 : any) {
                    if (c == c2) {
                        return i;
                    }
                }
            }
            return -1;
        }
        return -1;
    }

    private void tryAnnounceForAccessibility(CharSequence text, boolean isHour) {
        if (this.mLastAnnouncedIsHour != isHour || !text.equals(this.mLastAnnouncedText)) {
            this.mDelegator.announceForAccessibility(text);
            this.mLastAnnouncedText = text;
            this.mLastAnnouncedIsHour = isHour;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCurrentItemShowing(int index, boolean animateCircle, boolean announce) {
        this.mRadialTimePickerView.setCurrentItemShowing(index, animateCircle);
        if (index == 0) {
            if (announce) {
                this.mDelegator.announceForAccessibility(this.mSelectHours);
            }
        } else if (announce) {
            this.mDelegator.announceForAccessibility(this.mSelectMinutes);
        }
        this.mHourView.setActivated(index == 0);
        this.mMinuteView.setActivated(index == 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAmOrPm(int amOrPm) {
        updateAmPmLabelStates(amOrPm);
        if (this.mRadialTimePickerView.setAmOrPm(amOrPm)) {
            this.mCurrentHour = getHour();
            updateTextInputPicker();
            if (this.mOnTimeChangedListener != null) {
                this.mOnTimeChangedListener.onTimeChanged(this.mDelegator, getHour(), getMinute());
            }
        }
    }

    /* loaded from: classes4.dex */
    private static class NearestTouchDelegate implements View.OnTouchListener {
        private View mInitialTouchTarget;

        private NearestTouchDelegate() {
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                if (view instanceof ViewGroup) {
                    this.mInitialTouchTarget = findNearestChild((ViewGroup) view, (int) motionEvent.getX(), (int) motionEvent.getY());
                } else {
                    this.mInitialTouchTarget = null;
                }
            }
            View child = this.mInitialTouchTarget;
            if (child == null) {
                return false;
            }
            float offsetX = view.getScrollX() - child.getLeft();
            float offsetY = view.getScrollY() - child.getTop();
            motionEvent.offsetLocation(offsetX, offsetY);
            boolean handled = child.dispatchTouchEvent(motionEvent);
            motionEvent.offsetLocation(-offsetX, -offsetY);
            if (actionMasked == 1 || actionMasked == 3) {
                this.mInitialTouchTarget = null;
            }
            return handled;
        }

        private View findNearestChild(ViewGroup v, int x, int y) {
            View bestChild = null;
            int bestDist = Integer.MAX_VALUE;
            int count = v.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = v.getChildAt(i);
                int dX = x - (child.getLeft() + (child.getWidth() / 2));
                int dY = y - (child.getTop() + (child.getHeight() / 2));
                int dist = (dX * dX) + (dY * dY);
                if (bestDist > dist) {
                    bestChild = child;
                    bestDist = dist;
                }
            }
            return bestChild;
        }
    }
}
