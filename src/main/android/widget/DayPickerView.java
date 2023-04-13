package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.DayPickerPagerAdapter;
import com.android.internal.C4057R;
import com.android.internal.widget.ViewPager;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class DayPickerView extends ViewGroup {
    private static final int[] ATTRS_TEXT_COLOR = {16842904};
    private static final int DEFAULT_END_YEAR = 2100;
    private static final int DEFAULT_LAYOUT = 17367150;
    private static final int DEFAULT_START_YEAR = 1900;
    private final AccessibilityManager mAccessibilityManager;
    private final DayPickerPagerAdapter mAdapter;
    private final Calendar mMaxDate;
    private final Calendar mMinDate;
    private final ImageButton mNextButton;
    private final View.OnClickListener mOnClickListener;
    private OnDaySelectedListener mOnDaySelectedListener;
    private final ViewPager.OnPageChangeListener mOnPageChangedListener;
    private final ImageButton mPrevButton;
    private final Calendar mSelectedDay;
    private Calendar mTempCalendar;
    private final ViewPager mViewPager;

    /* loaded from: classes4.dex */
    public interface OnDaySelectedListener {
        void onDaySelected(DayPickerView dayPickerView, Calendar calendar);
    }

    public DayPickerView(Context context) {
        this(context, null);
    }

    public DayPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 16843613);
    }

    public DayPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DayPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mSelectedDay = Calendar.getInstance();
        this.mMinDate = Calendar.getInstance();
        this.mMaxDate = Calendar.getInstance();
        this.mOnPageChangedListener = new ViewPager.OnPageChangeListener() { // from class: android.widget.DayPickerView.2
            @Override // com.android.internal.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float alpha = Math.abs(0.5f - positionOffset) * 2.0f;
                DayPickerView.this.mPrevButton.setAlpha(alpha);
                DayPickerView.this.mNextButton.setAlpha(alpha);
            }

            @Override // com.android.internal.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }

            @Override // com.android.internal.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                DayPickerView.this.updateButtonVisibility(position);
            }
        };
        this.mOnClickListener = new View.OnClickListener() { // from class: android.widget.DayPickerView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int direction;
                if (v == DayPickerView.this.mPrevButton) {
                    direction = -1;
                } else if (v == DayPickerView.this.mNextButton) {
                    direction = 1;
                } else {
                    return;
                }
                boolean animate = !DayPickerView.this.mAccessibilityManager.isEnabled();
                int nextItem = DayPickerView.this.mViewPager.getCurrentItem() + direction;
                DayPickerView.this.mViewPager.setCurrentItem(nextItem, animate);
            }
        };
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.CalendarView, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.CalendarView, attrs, a, defStyleAttr, defStyleRes);
        int firstDayOfWeek = a.getInt(0, Calendar.getInstance().getFirstDayOfWeek());
        String minDate = a.getString(2);
        String maxDate = a.getString(3);
        int monthTextAppearanceResId = a.getResourceId(16, C4057R.C4062style.TextAppearance_Material_Widget_Calendar_Month);
        int dayOfWeekTextAppearanceResId = a.getResourceId(11, C4057R.C4062style.TextAppearance_Material_Widget_Calendar_DayOfWeek);
        int dayTextAppearanceResId = a.getResourceId(12, C4057R.C4062style.TextAppearance_Material_Widget_Calendar_Day);
        ColorStateList daySelectorColor = a.getColorStateList(15);
        a.recycle();
        DayPickerPagerAdapter dayPickerPagerAdapter = new DayPickerPagerAdapter(context, C4057R.layout.date_picker_month_item_material, C4057R.C4059id.month_view);
        this.mAdapter = dayPickerPagerAdapter;
        dayPickerPagerAdapter.setMonthTextAppearance(monthTextAppearanceResId);
        dayPickerPagerAdapter.setDayOfWeekTextAppearance(dayOfWeekTextAppearanceResId);
        dayPickerPagerAdapter.setDayTextAppearance(dayTextAppearanceResId);
        dayPickerPagerAdapter.setDaySelectorColor(daySelectorColor);
        LayoutInflater inflater = LayoutInflater.from(context);
        int i = 0;
        ViewGroup content = (ViewGroup) inflater.inflate(17367150, (ViewGroup) this, false);
        while (content.getChildCount() > 0) {
            LayoutInflater inflater2 = inflater;
            View child = content.getChildAt(i);
            content.removeViewAt(i);
            addView(child);
            inflater = inflater2;
            i = 0;
        }
        ImageButton imageButton = (ImageButton) findViewById(C4057R.C4059id.prev);
        this.mPrevButton = imageButton;
        imageButton.setOnClickListener(this.mOnClickListener);
        ImageButton imageButton2 = (ImageButton) findViewById(C4057R.C4059id.next);
        this.mNextButton = imageButton2;
        imageButton2.setOnClickListener(this.mOnClickListener);
        ViewPager viewPager = (ViewPager) findViewById(C4057R.C4059id.day_picker_view_pager);
        this.mViewPager = viewPager;
        viewPager.setAdapter(this.mAdapter);
        viewPager.setOnPageChangeListener(this.mOnPageChangedListener);
        if (monthTextAppearanceResId != 0) {
            TypedArray ta = this.mContext.obtainStyledAttributes(null, ATTRS_TEXT_COLOR, 0, monthTextAppearanceResId);
            ColorStateList monthColor = ta.getColorStateList(0);
            if (monthColor != null) {
                imageButton.setImageTintList(monthColor);
                imageButton2.setImageTintList(monthColor);
            }
            ta.recycle();
        }
        Calendar tempDate = Calendar.getInstance();
        if (!CalendarView.parseDate(minDate, tempDate)) {
            tempDate.set(1900, 0, 1);
        }
        long minDateMillis = tempDate.getTimeInMillis();
        if (!CalendarView.parseDate(maxDate, tempDate)) {
            tempDate.set(2100, 11, 31);
        }
        long maxDateMillis = tempDate.getTimeInMillis();
        if (maxDateMillis < minDateMillis) {
            throw new IllegalArgumentException("maxDate must be >= minDate");
        }
        long setDateMillis = MathUtils.constrain(System.currentTimeMillis(), minDateMillis, maxDateMillis);
        setFirstDayOfWeek(firstDayOfWeek);
        setMinDate(minDateMillis);
        setMaxDate(maxDateMillis);
        setDate(setDateMillis, false);
        this.mAdapter.setOnDaySelectedListener(new DayPickerPagerAdapter.OnDaySelectedListener() { // from class: android.widget.DayPickerView.1
            @Override // android.widget.DayPickerPagerAdapter.OnDaySelectedListener
            public void onDaySelected(DayPickerPagerAdapter adapter, Calendar day) {
                if (DayPickerView.this.mOnDaySelectedListener != null) {
                    DayPickerView.this.mOnDaySelectedListener.onDaySelected(DayPickerView.this, day);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateButtonVisibility(int position) {
        boolean hasPrev = position > 0;
        boolean hasNext = position < this.mAdapter.getCount() - 1;
        this.mPrevButton.setVisibility(hasPrev ? 0 : 4);
        this.mNextButton.setVisibility(hasNext ? 0 : 4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ViewPager viewPager = this.mViewPager;
        measureChild(viewPager, widthMeasureSpec, heightMeasureSpec);
        int measuredWidthAndState = viewPager.getMeasuredWidthAndState();
        int measuredHeightAndState = viewPager.getMeasuredHeightAndState();
        setMeasuredDimension(measuredWidthAndState, measuredHeightAndState);
        int pagerWidth = viewPager.getMeasuredWidth();
        int pagerHeight = viewPager.getMeasuredHeight();
        int buttonWidthSpec = View.MeasureSpec.makeMeasureSpec(pagerWidth, Integer.MIN_VALUE);
        int buttonHeightSpec = View.MeasureSpec.makeMeasureSpec(pagerHeight, Integer.MIN_VALUE);
        this.mPrevButton.measure(buttonWidthSpec, buttonHeightSpec);
        this.mNextButton.measure(buttonWidthSpec, buttonHeightSpec);
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ImageButton leftButton;
        ImageButton rightButton;
        if (isLayoutRtl()) {
            leftButton = this.mNextButton;
            rightButton = this.mPrevButton;
        } else {
            leftButton = this.mPrevButton;
            rightButton = this.mNextButton;
        }
        int width = right - left;
        int height = bottom - top;
        this.mViewPager.layout(0, 0, width, height);
        SimpleMonthView monthView = (SimpleMonthView) this.mViewPager.getChildAt(0);
        int monthHeight = monthView.getMonthHeight();
        int cellWidth = monthView.getCellWidth();
        int leftDW = leftButton.getMeasuredWidth();
        int leftDH = leftButton.getMeasuredHeight();
        int leftIconTop = monthView.getPaddingTop() + ((monthHeight - leftDH) / 2);
        int leftIconLeft = monthView.getPaddingLeft() + ((cellWidth - leftDW) / 2);
        leftButton.layout(leftIconLeft, leftIconTop, leftIconLeft + leftDW, leftIconTop + leftDH);
        int rightDW = rightButton.getMeasuredWidth();
        int rightDH = rightButton.getMeasuredHeight();
        int rightIconTop = monthView.getPaddingTop() + ((monthHeight - rightDH) / 2);
        int rightIconRight = (width - monthView.getPaddingRight()) - ((cellWidth - rightDW) / 2);
        rightButton.layout(rightIconRight - rightDW, rightIconTop, rightIconRight, rightIconTop + rightDH);
    }

    public void setDayOfWeekTextAppearance(int resId) {
        this.mAdapter.setDayOfWeekTextAppearance(resId);
    }

    public int getDayOfWeekTextAppearance() {
        return this.mAdapter.getDayOfWeekTextAppearance();
    }

    public void setDayTextAppearance(int resId) {
        this.mAdapter.setDayTextAppearance(resId);
    }

    public int getDayTextAppearance() {
        return this.mAdapter.getDayTextAppearance();
    }

    public void setDate(long timeInMillis) {
        setDate(timeInMillis, false);
    }

    public void setDate(long timeInMillis, boolean animate) {
        setDate(timeInMillis, animate, true);
    }

    private void setDate(long timeInMillis, boolean animate, boolean setSelected) {
        boolean dateClamped = false;
        if (timeInMillis < this.mMinDate.getTimeInMillis()) {
            timeInMillis = this.mMinDate.getTimeInMillis();
            dateClamped = true;
        } else if (timeInMillis > this.mMaxDate.getTimeInMillis()) {
            timeInMillis = this.mMaxDate.getTimeInMillis();
            dateClamped = true;
        }
        getTempCalendarForTime(timeInMillis);
        if (setSelected || dateClamped) {
            this.mSelectedDay.setTimeInMillis(timeInMillis);
        }
        int position = getPositionFromDay(timeInMillis);
        if (position != this.mViewPager.getCurrentItem()) {
            this.mViewPager.setCurrentItem(position, animate);
        }
        this.mAdapter.setSelectedDay(this.mTempCalendar);
    }

    public long getDate() {
        return this.mSelectedDay.getTimeInMillis();
    }

    public boolean getBoundsForDate(long timeInMillis, Rect outBounds) {
        int position = getPositionFromDay(timeInMillis);
        if (position != this.mViewPager.getCurrentItem()) {
            return false;
        }
        this.mTempCalendar.setTimeInMillis(timeInMillis);
        return this.mAdapter.getBoundsForDate(this.mTempCalendar, outBounds);
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.mAdapter.setFirstDayOfWeek(firstDayOfWeek);
    }

    public int getFirstDayOfWeek() {
        return this.mAdapter.getFirstDayOfWeek();
    }

    public void setMinDate(long timeInMillis) {
        this.mMinDate.setTimeInMillis(timeInMillis);
        onRangeChanged();
    }

    public long getMinDate() {
        return this.mMinDate.getTimeInMillis();
    }

    public void setMaxDate(long timeInMillis) {
        this.mMaxDate.setTimeInMillis(timeInMillis);
        onRangeChanged();
    }

    public long getMaxDate() {
        return this.mMaxDate.getTimeInMillis();
    }

    public void onRangeChanged() {
        this.mAdapter.setRange(this.mMinDate, this.mMaxDate);
        setDate(this.mSelectedDay.getTimeInMillis(), false, false);
        updateButtonVisibility(this.mViewPager.getCurrentItem());
    }

    public void setOnDaySelectedListener(OnDaySelectedListener listener) {
        this.mOnDaySelectedListener = listener;
    }

    private int getDiffMonths(Calendar start, Calendar end) {
        int diffYears = end.get(1) - start.get(1);
        return (end.get(2) - start.get(2)) + (diffYears * 12);
    }

    private int getPositionFromDay(long timeInMillis) {
        int diffMonthMax = getDiffMonths(this.mMinDate, this.mMaxDate);
        int diffMonth = getDiffMonths(this.mMinDate, getTempCalendarForTime(timeInMillis));
        return MathUtils.constrain(diffMonth, 0, diffMonthMax);
    }

    private Calendar getTempCalendarForTime(long timeInMillis) {
        if (this.mTempCalendar == null) {
            this.mTempCalendar = Calendar.getInstance();
        }
        this.mTempCalendar.setTimeInMillis(timeInMillis);
        return this.mTempCalendar;
    }

    public int getMostVisiblePosition() {
        return this.mViewPager.getCurrentItem();
    }

    public void setPosition(int position) {
        this.mViewPager.setCurrentItem(position, false);
    }
}
