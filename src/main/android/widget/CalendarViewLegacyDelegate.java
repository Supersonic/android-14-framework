package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CalendarView;
import com.android.internal.C4057R;
import java.util.Locale;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class CalendarViewLegacyDelegate extends CalendarView.AbstractCalendarViewDelegate {
    private static final int ADJUSTMENT_SCROLL_DURATION = 500;
    private static final int DAYS_PER_WEEK = 7;
    private static final int DEFAULT_DATE_TEXT_SIZE = 14;
    private static final int DEFAULT_SHOWN_WEEK_COUNT = 6;
    private static final boolean DEFAULT_SHOW_WEEK_NUMBER = true;
    private static final int DEFAULT_WEEK_DAY_TEXT_APPEARANCE_RES_ID = -1;
    private static final int GOTO_SCROLL_DURATION = 1000;
    private static final long MILLIS_IN_DAY = 86400000;
    private static final long MILLIS_IN_WEEK = 604800000;
    private static final int SCROLL_CHANGE_DELAY = 40;
    private static final int SCROLL_HYST_WEEKS = 2;
    private static final int UNSCALED_BOTTOM_BUFFER = 20;
    private static final int UNSCALED_LIST_SCROLL_TOP_OFFSET = 2;
    private static final int UNSCALED_SELECTED_DATE_VERTICAL_BAR_WIDTH = 6;
    private static final int UNSCALED_WEEK_MIN_VISIBLE_HEIGHT = 12;
    private static final int UNSCALED_WEEK_SEPARATOR_LINE_WIDTH = 1;
    private WeeksAdapter mAdapter;
    private int mBottomBuffer;
    private int mCurrentMonthDisplayed;
    private int mCurrentScrollState;
    private int mDateTextAppearanceResId;
    private int mDateTextSize;
    private ViewGroup mDayNamesHeader;
    private String[] mDayNamesLong;
    private String[] mDayNamesShort;
    private int mDaysPerWeek;
    private Calendar mFirstDayOfMonth;
    private int mFirstDayOfWeek;
    private int mFocusedMonthDateColor;
    private float mFriction;
    private boolean mIsScrollingUp;
    private int mListScrollTopOffset;
    private ListView mListView;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private TextView mMonthName;
    private CalendarView.OnDateChangeListener mOnDateChangeListener;
    private long mPreviousScrollPosition;
    private int mPreviousScrollState;
    private ScrollStateRunnable mScrollStateChangedRunnable;
    private Drawable mSelectedDateVerticalBar;
    private final int mSelectedDateVerticalBarWidth;
    private int mSelectedWeekBackgroundColor;
    private boolean mShowWeekNumber;
    private int mShownWeekCount;
    private Calendar mTempDate;
    private int mUnfocusedMonthDateColor;
    private float mVelocityScale;
    private int mWeekDayTextAppearanceResId;
    private int mWeekMinVisibleHeight;
    private int mWeekNumberColor;
    private int mWeekSeparatorLineColor;
    private final int mWeekSeparatorLineWidth;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CalendarViewLegacyDelegate(CalendarView delegator, Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(delegator, context);
        this.mListScrollTopOffset = 2;
        this.mWeekMinVisibleHeight = 12;
        this.mBottomBuffer = 20;
        this.mDaysPerWeek = 7;
        this.mFriction = 0.05f;
        this.mVelocityScale = 0.333f;
        this.mCurrentMonthDisplayed = -1;
        this.mIsScrollingUp = false;
        this.mPreviousScrollState = 0;
        this.mCurrentScrollState = 0;
        this.mScrollStateChangedRunnable = new ScrollStateRunnable();
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.CalendarView, defStyleAttr, defStyleRes);
        this.mShowWeekNumber = a.getBoolean(1, true);
        this.mFirstDayOfWeek = a.getInt(0, Calendar.getInstance().getFirstDayOfWeek());
        String minDate = a.getString(2);
        if (!CalendarView.parseDate(minDate, this.mMinDate)) {
            CalendarView.parseDate("01/01/1900", this.mMinDate);
        }
        String maxDate = a.getString(3);
        if (!CalendarView.parseDate(maxDate, this.mMaxDate)) {
            CalendarView.parseDate("01/01/2100", this.mMaxDate);
        }
        if (!this.mMaxDate.before(this.mMinDate)) {
            this.mShownWeekCount = a.getInt(4, 6);
            this.mSelectedWeekBackgroundColor = a.getColor(5, 0);
            this.mFocusedMonthDateColor = a.getColor(6, 0);
            this.mUnfocusedMonthDateColor = a.getColor(7, 0);
            this.mWeekSeparatorLineColor = a.getColor(9, 0);
            this.mWeekNumberColor = a.getColor(8, 0);
            this.mSelectedDateVerticalBar = a.getDrawable(10);
            this.mDateTextAppearanceResId = a.getResourceId(12, 16973894);
            updateDateTextSize();
            this.mWeekDayTextAppearanceResId = a.getResourceId(11, -1);
            a.recycle();
            DisplayMetrics displayMetrics = this.mDelegator.getResources().getDisplayMetrics();
            this.mWeekMinVisibleHeight = (int) TypedValue.applyDimension(1, 12.0f, displayMetrics);
            this.mListScrollTopOffset = (int) TypedValue.applyDimension(1, 2.0f, displayMetrics);
            this.mBottomBuffer = (int) TypedValue.applyDimension(1, 20.0f, displayMetrics);
            this.mSelectedDateVerticalBarWidth = (int) TypedValue.applyDimension(1, 6.0f, displayMetrics);
            this.mWeekSeparatorLineWidth = (int) TypedValue.applyDimension(1, 1.0f, displayMetrics);
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = layoutInflater.inflate(C4057R.layout.calendar_view, (ViewGroup) null, false);
            this.mDelegator.addView(content);
            this.mListView = (ListView) this.mDelegator.findViewById(16908298);
            this.mDayNamesHeader = (ViewGroup) content.findViewById(C4057R.C4059id.day_names);
            this.mMonthName = (TextView) content.findViewById(C4057R.C4059id.month_name);
            setUpHeader();
            setUpListView();
            setUpAdapter();
            this.mTempDate.setTimeInMillis(System.currentTimeMillis());
            if (this.mTempDate.before(this.mMinDate)) {
                goTo(this.mMinDate, false, true, true);
            } else if (this.mMaxDate.before(this.mTempDate)) {
                goTo(this.mMaxDate, false, true, true);
            } else {
                goTo(this.mTempDate, false, true, true);
            }
            this.mDelegator.invalidate();
            return;
        }
        throw new IllegalArgumentException("Max date cannot be before min date.");
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setShownWeekCount(int count) {
        if (this.mShownWeekCount != count) {
            this.mShownWeekCount = count;
            this.mDelegator.invalidate();
        }
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public int getShownWeekCount() {
        return this.mShownWeekCount;
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setSelectedWeekBackgroundColor(int color) {
        if (this.mSelectedWeekBackgroundColor != color) {
            this.mSelectedWeekBackgroundColor = color;
            int childCount = this.mListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                WeekView weekView = (WeekView) this.mListView.getChildAt(i);
                if (weekView.mHasSelectedDay) {
                    weekView.invalidate();
                }
            }
        }
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public int getSelectedWeekBackgroundColor() {
        return this.mSelectedWeekBackgroundColor;
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setFocusedMonthDateColor(int color) {
        if (this.mFocusedMonthDateColor != color) {
            this.mFocusedMonthDateColor = color;
            int childCount = this.mListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                WeekView weekView = (WeekView) this.mListView.getChildAt(i);
                if (weekView.mHasFocusedDay) {
                    weekView.invalidate();
                }
            }
        }
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public int getFocusedMonthDateColor() {
        return this.mFocusedMonthDateColor;
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setUnfocusedMonthDateColor(int color) {
        if (this.mUnfocusedMonthDateColor != color) {
            this.mUnfocusedMonthDateColor = color;
            int childCount = this.mListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                WeekView weekView = (WeekView) this.mListView.getChildAt(i);
                if (weekView.mHasUnfocusedDay) {
                    weekView.invalidate();
                }
            }
        }
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public int getUnfocusedMonthDateColor() {
        return this.mUnfocusedMonthDateColor;
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setWeekNumberColor(int color) {
        if (this.mWeekNumberColor != color) {
            this.mWeekNumberColor = color;
            if (this.mShowWeekNumber) {
                invalidateAllWeekViews();
            }
        }
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public int getWeekNumberColor() {
        return this.mWeekNumberColor;
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setWeekSeparatorLineColor(int color) {
        if (this.mWeekSeparatorLineColor != color) {
            this.mWeekSeparatorLineColor = color;
            invalidateAllWeekViews();
        }
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public int getWeekSeparatorLineColor() {
        return this.mWeekSeparatorLineColor;
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setSelectedDateVerticalBar(int resourceId) {
        Drawable drawable = this.mDelegator.getContext().getDrawable(resourceId);
        setSelectedDateVerticalBar(drawable);
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setSelectedDateVerticalBar(Drawable drawable) {
        if (this.mSelectedDateVerticalBar != drawable) {
            this.mSelectedDateVerticalBar = drawable;
            int childCount = this.mListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                WeekView weekView = (WeekView) this.mListView.getChildAt(i);
                if (weekView.mHasSelectedDay) {
                    weekView.invalidate();
                }
            }
        }
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public Drawable getSelectedDateVerticalBar() {
        return this.mSelectedDateVerticalBar;
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setWeekDayTextAppearance(int resourceId) {
        if (this.mWeekDayTextAppearanceResId != resourceId) {
            this.mWeekDayTextAppearanceResId = resourceId;
            setUpHeader();
        }
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public int getWeekDayTextAppearance() {
        return this.mWeekDayTextAppearanceResId;
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setDateTextAppearance(int resourceId) {
        if (this.mDateTextAppearanceResId != resourceId) {
            this.mDateTextAppearanceResId = resourceId;
            updateDateTextSize();
            invalidateAllWeekViews();
        }
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public int getDateTextAppearance() {
        return this.mDateTextAppearanceResId;
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setMinDate(long minDate) {
        this.mTempDate.setTimeInMillis(minDate);
        if (isSameDate(this.mTempDate, this.mMinDate)) {
            return;
        }
        this.mMinDate.setTimeInMillis(minDate);
        Calendar date = this.mAdapter.mSelectedDate;
        if (date.before(this.mMinDate)) {
            this.mAdapter.setSelectedDay(this.mMinDate);
        }
        this.mAdapter.init();
        if (date.before(this.mMinDate)) {
            setDate(this.mTempDate.getTimeInMillis());
        } else {
            goTo(date, false, true, false);
        }
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public long getMinDate() {
        return this.mMinDate.getTimeInMillis();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setMaxDate(long maxDate) {
        this.mTempDate.setTimeInMillis(maxDate);
        if (isSameDate(this.mTempDate, this.mMaxDate)) {
            return;
        }
        this.mMaxDate.setTimeInMillis(maxDate);
        this.mAdapter.init();
        Calendar date = this.mAdapter.mSelectedDate;
        if (date.after(this.mMaxDate)) {
            setDate(this.mMaxDate.getTimeInMillis());
        } else {
            goTo(date, false, true, false);
        }
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public long getMaxDate() {
        return this.mMaxDate.getTimeInMillis();
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void setShowWeekNumber(boolean showWeekNumber) {
        if (this.mShowWeekNumber == showWeekNumber) {
            return;
        }
        this.mShowWeekNumber = showWeekNumber;
        this.mAdapter.notifyDataSetChanged();
        setUpHeader();
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public boolean getShowWeekNumber() {
        return this.mShowWeekNumber;
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (this.mFirstDayOfWeek == firstDayOfWeek) {
            return;
        }
        this.mFirstDayOfWeek = firstDayOfWeek;
        this.mAdapter.init();
        this.mAdapter.notifyDataSetChanged();
        setUpHeader();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public int getFirstDayOfWeek() {
        return this.mFirstDayOfWeek;
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setDate(long date) {
        setDate(date, false, false);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setDate(long date, boolean animate, boolean center) {
        this.mTempDate.setTimeInMillis(date);
        if (isSameDate(this.mTempDate, this.mAdapter.mSelectedDate)) {
            return;
        }
        goTo(this.mTempDate, animate, true, center);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public long getDate() {
        return this.mAdapter.mSelectedDate.getTimeInMillis();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setOnDateChangeListener(CalendarView.OnDateChangeListener listener) {
        this.mOnDateChangeListener = listener;
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public boolean getBoundsForDate(long date, Rect outBounds) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTimeInMillis(date);
        int listViewEntryCount = this.mListView.getCount();
        for (int i = 0; i < listViewEntryCount; i++) {
            WeekView currWeekView = (WeekView) this.mListView.getChildAt(i);
            if (currWeekView.getBoundsForDate(calendarDate, outBounds)) {
                int[] weekViewPositionOnScreen = new int[2];
                int[] delegatorPositionOnScreen = new int[2];
                currWeekView.getLocationOnScreen(weekViewPositionOnScreen);
                this.mDelegator.getLocationOnScreen(delegatorPositionOnScreen);
                int extraVerticalOffset = weekViewPositionOnScreen[1] - delegatorPositionOnScreen[1];
                outBounds.top += extraVerticalOffset;
                outBounds.bottom += extraVerticalOffset;
                return true;
            }
        }
        return false;
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate, android.widget.CalendarView.CalendarViewDelegate
    public void onConfigurationChanged(Configuration newConfig) {
        setCurrentLocale(newConfig.locale);
    }

    @Override // android.widget.CalendarView.AbstractCalendarViewDelegate
    protected void setCurrentLocale(Locale locale) {
        super.setCurrentLocale(locale);
        this.mTempDate = getCalendarForLocale(this.mTempDate, locale);
        this.mFirstDayOfMonth = getCalendarForLocale(this.mFirstDayOfMonth, locale);
        this.mMinDate = getCalendarForLocale(this.mMinDate, locale);
        this.mMaxDate = getCalendarForLocale(this.mMaxDate, locale);
    }

    private void updateDateTextSize() {
        TypedArray dateTextAppearance = this.mDelegator.getContext().obtainStyledAttributes(this.mDateTextAppearanceResId, C4057R.styleable.TextAppearance);
        this.mDateTextSize = dateTextAppearance.getDimensionPixelSize(0, 14);
        dateTextAppearance.recycle();
    }

    private void invalidateAllWeekViews() {
        int childCount = this.mListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = this.mListView.getChildAt(i);
            view.invalidate();
        }
    }

    private static Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        }
        long currentTimeMillis = oldCalendar.getTimeInMillis();
        Calendar newCalendar = Calendar.getInstance(locale);
        newCalendar.setTimeInMillis(currentTimeMillis);
        return newCalendar;
    }

    private static boolean isSameDate(Calendar firstDate, Calendar secondDate) {
        return firstDate.get(6) == secondDate.get(6) && firstDate.get(1) == secondDate.get(1);
    }

    private void setUpAdapter() {
        if (this.mAdapter == null) {
            WeeksAdapter weeksAdapter = new WeeksAdapter(this.mContext);
            this.mAdapter = weeksAdapter;
            weeksAdapter.registerDataSetObserver(new DataSetObserver() { // from class: android.widget.CalendarViewLegacyDelegate.1
                @Override // android.database.DataSetObserver
                public void onChanged() {
                    if (CalendarViewLegacyDelegate.this.mOnDateChangeListener != null) {
                        Calendar selectedDay = CalendarViewLegacyDelegate.this.mAdapter.getSelectedDay();
                        CalendarViewLegacyDelegate.this.mOnDateChangeListener.onSelectedDayChange(CalendarViewLegacyDelegate.this.mDelegator, selectedDay.get(1), selectedDay.get(2), selectedDay.get(5));
                    }
                }
            });
            this.mListView.setAdapter((ListAdapter) this.mAdapter);
        }
        this.mAdapter.notifyDataSetChanged();
    }

    private void setUpHeader() {
        int i = this.mDaysPerWeek;
        this.mDayNamesShort = new String[i];
        this.mDayNamesLong = new String[i];
        int i2 = this.mFirstDayOfWeek;
        int count = this.mFirstDayOfWeek + i;
        while (i2 < count) {
            int calendarDay = i2 > 7 ? i2 - 7 : i2;
            this.mDayNamesShort[i2 - this.mFirstDayOfWeek] = DateUtils.getDayOfWeekString(calendarDay, 50);
            this.mDayNamesLong[i2 - this.mFirstDayOfWeek] = DateUtils.getDayOfWeekString(calendarDay, 10);
            i2++;
        }
        TextView label = (TextView) this.mDayNamesHeader.getChildAt(0);
        if (this.mShowWeekNumber) {
            label.setVisibility(0);
        } else {
            label.setVisibility(8);
        }
        int count2 = this.mDayNamesHeader.getChildCount();
        for (int i3 = 1; i3 < count2; i3++) {
            TextView label2 = (TextView) this.mDayNamesHeader.getChildAt(i3);
            int i4 = this.mWeekDayTextAppearanceResId;
            if (i4 > -1) {
                label2.setTextAppearance(i4);
            }
            if (i3 < this.mDaysPerWeek + 1) {
                label2.setText(this.mDayNamesShort[i3 - 1]);
                label2.setContentDescription(this.mDayNamesLong[i3 - 1]);
                label2.setVisibility(0);
            } else {
                label2.setVisibility(8);
            }
        }
        this.mDayNamesHeader.invalidate();
    }

    private void setUpListView() {
        this.mListView.setDivider(null);
        this.mListView.setItemsCanFocus(true);
        this.mListView.setVerticalScrollBarEnabled(false);
        this.mListView.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: android.widget.CalendarViewLegacyDelegate.2
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                CalendarViewLegacyDelegate.this.onScrollStateChanged(view, scrollState);
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                CalendarViewLegacyDelegate.this.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
        this.mListView.setFriction(this.mFriction);
        this.mListView.setVelocityScale(this.mVelocityScale);
    }

    private void goTo(Calendar date, boolean animate, boolean setSelected, boolean forceScroll) {
        int position;
        if (date.before(this.mMinDate) || date.after(this.mMaxDate)) {
            throw new IllegalArgumentException("timeInMillis must be between the values of getMinDate() and getMaxDate()");
        }
        int firstFullyVisiblePosition = this.mListView.getFirstVisiblePosition();
        View firstChild = this.mListView.getChildAt(0);
        if (firstChild != null && firstChild.getTop() < 0) {
            firstFullyVisiblePosition++;
        }
        int lastFullyVisiblePosition = (this.mShownWeekCount + firstFullyVisiblePosition) - 1;
        if (firstChild != null && firstChild.getTop() > this.mBottomBuffer) {
            lastFullyVisiblePosition--;
        }
        if (setSelected) {
            this.mAdapter.setSelectedDay(date);
        }
        int position2 = getWeeksSinceMinDate(date);
        if (position2 < firstFullyVisiblePosition || position2 > lastFullyVisiblePosition || forceScroll) {
            this.mFirstDayOfMonth.setTimeInMillis(date.getTimeInMillis());
            this.mFirstDayOfMonth.set(5, 1);
            setMonthDisplayed(this.mFirstDayOfMonth);
            if (this.mFirstDayOfMonth.before(this.mMinDate)) {
                position = 0;
            } else {
                position = getWeeksSinceMinDate(this.mFirstDayOfMonth);
            }
            this.mPreviousScrollState = 2;
            if (animate) {
                this.mListView.smoothScrollToPositionFromTop(position, this.mListScrollTopOffset, 1000);
                return;
            }
            this.mListView.setSelectionFromTop(position, this.mListScrollTopOffset);
            onScrollStateChanged(this.mListView, 0);
        } else if (setSelected) {
            setMonthDisplayed(date);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int month;
        int monthDiff;
        WeekView child = (WeekView) view.getChildAt(0);
        if (child == null) {
            return;
        }
        long currScroll = (view.getFirstVisiblePosition() * child.getHeight()) - child.getBottom();
        long j = this.mPreviousScrollPosition;
        if (currScroll < j) {
            this.mIsScrollingUp = true;
        } else if (currScroll > j) {
            this.mIsScrollingUp = false;
        } else {
            return;
        }
        int offset = child.getBottom() < this.mWeekMinVisibleHeight ? 1 : 0;
        if (this.mIsScrollingUp) {
            child = (WeekView) view.getChildAt(offset + 2);
        } else if (offset != 0) {
            child = (WeekView) view.getChildAt(offset);
        }
        if (child != null) {
            if (this.mIsScrollingUp) {
                month = child.getMonthOfFirstWeekDay();
            } else {
                month = child.getMonthOfLastWeekDay();
            }
            int monthDiff2 = this.mCurrentMonthDisplayed;
            if (monthDiff2 == 11 && month == 0) {
                monthDiff = 1;
            } else if (monthDiff2 == 0 && month == 11) {
                monthDiff = -1;
            } else {
                monthDiff = month - monthDiff2;
            }
            boolean z = this.mIsScrollingUp;
            if ((!z && monthDiff > 0) || (z && monthDiff < 0)) {
                Calendar firstDay = child.getFirstDay();
                if (this.mIsScrollingUp) {
                    firstDay.add(5, -7);
                } else {
                    firstDay.add(5, 7);
                }
                setMonthDisplayed(firstDay);
            }
        }
        this.mPreviousScrollPosition = currScroll;
        this.mPreviousScrollState = this.mCurrentScrollState;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMonthDisplayed(Calendar calendar) {
        int i = calendar.get(2);
        this.mCurrentMonthDisplayed = i;
        this.mAdapter.setFocusMonth(i);
        long millis = calendar.getTimeInMillis();
        String newMonthName = DateUtils.formatDateRange(this.mContext, millis, millis, 52);
        this.mMonthName.setText(newMonthName);
        this.mMonthName.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWeeksSinceMinDate(Calendar date) {
        if (date.before(this.mMinDate)) {
            throw new IllegalArgumentException("fromDate: " + this.mMinDate.getTime() + " does not precede toDate: " + date.getTime());
        }
        long endTimeMillis = date.getTimeInMillis() + date.getTimeZone().getOffset(date.getTimeInMillis());
        long startTimeMillis = this.mMinDate.getTimeInMillis() + this.mMinDate.getTimeZone().getOffset(this.mMinDate.getTimeInMillis());
        long dayOffsetMillis = (this.mMinDate.get(7) - this.mFirstDayOfWeek) * 86400000;
        return (int) (((endTimeMillis - startTimeMillis) + dayOffsetMillis) / 604800000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ScrollStateRunnable implements Runnable {
        private int mNewState;
        private AbsListView mView;

        private ScrollStateRunnable() {
        }

        public void doScrollStateChange(AbsListView view, int scrollState) {
            this.mView = view;
            this.mNewState = scrollState;
            CalendarViewLegacyDelegate.this.mDelegator.removeCallbacks(this);
            CalendarViewLegacyDelegate.this.mDelegator.postDelayed(this, 40L);
        }

        @Override // java.lang.Runnable
        public void run() {
            CalendarViewLegacyDelegate.this.mCurrentScrollState = this.mNewState;
            if (this.mNewState == 0 && CalendarViewLegacyDelegate.this.mPreviousScrollState != 0) {
                View child = this.mView.getChildAt(0);
                if (child == null) {
                    return;
                }
                int dist = child.getBottom() - CalendarViewLegacyDelegate.this.mListScrollTopOffset;
                if (dist > CalendarViewLegacyDelegate.this.mListScrollTopOffset) {
                    if (CalendarViewLegacyDelegate.this.mIsScrollingUp) {
                        this.mView.smoothScrollBy(dist - child.getHeight(), 500);
                    } else {
                        this.mView.smoothScrollBy(dist, 500);
                    }
                }
            }
            CalendarViewLegacyDelegate.this.mPreviousScrollState = this.mNewState;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class WeeksAdapter extends BaseAdapter implements View.OnTouchListener {
        private int mFocusedMonth;
        private GestureDetector mGestureDetector;
        private final Calendar mSelectedDate = Calendar.getInstance();
        private int mSelectedWeek;
        private int mTotalWeekCount;

        public WeeksAdapter(Context context) {
            CalendarViewLegacyDelegate.this.mContext = context;
            this.mGestureDetector = new GestureDetector(CalendarViewLegacyDelegate.this.mContext, new CalendarGestureListener());
            init();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void init() {
            this.mSelectedWeek = CalendarViewLegacyDelegate.this.getWeeksSinceMinDate(this.mSelectedDate);
            CalendarViewLegacyDelegate calendarViewLegacyDelegate = CalendarViewLegacyDelegate.this;
            this.mTotalWeekCount = calendarViewLegacyDelegate.getWeeksSinceMinDate(calendarViewLegacyDelegate.mMaxDate);
            if (CalendarViewLegacyDelegate.this.mMinDate.get(7) != CalendarViewLegacyDelegate.this.mFirstDayOfWeek || CalendarViewLegacyDelegate.this.mMaxDate.get(7) != CalendarViewLegacyDelegate.this.mFirstDayOfWeek) {
                this.mTotalWeekCount++;
            }
            notifyDataSetChanged();
        }

        public void setSelectedDay(Calendar selectedDay) {
            if (selectedDay.get(6) == this.mSelectedDate.get(6) && selectedDay.get(1) == this.mSelectedDate.get(1)) {
                return;
            }
            this.mSelectedDate.setTimeInMillis(selectedDay.getTimeInMillis());
            this.mSelectedWeek = CalendarViewLegacyDelegate.this.getWeeksSinceMinDate(this.mSelectedDate);
            this.mFocusedMonth = this.mSelectedDate.get(2);
            notifyDataSetChanged();
        }

        public Calendar getSelectedDay() {
            return this.mSelectedDate;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.mTotalWeekCount;
        }

        @Override // android.widget.Adapter
        public Object getItem(int position) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }

        @Override // android.widget.Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            WeekView weekView;
            if (convertView != null) {
                weekView = (WeekView) convertView;
            } else {
                CalendarViewLegacyDelegate calendarViewLegacyDelegate = CalendarViewLegacyDelegate.this;
                weekView = new WeekView(calendarViewLegacyDelegate.mContext);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(-2, -2);
                weekView.setLayoutParams(params);
                weekView.setClickable(true);
                weekView.setOnTouchListener(this);
            }
            int selectedWeekDay = this.mSelectedWeek == position ? this.mSelectedDate.get(7) : -1;
            weekView.init(position, selectedWeekDay, this.mFocusedMonth);
            return weekView;
        }

        public void setFocusMonth(int month) {
            if (this.mFocusedMonth == month) {
                return;
            }
            this.mFocusedMonth = month;
            notifyDataSetChanged();
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (CalendarViewLegacyDelegate.this.mListView.isEnabled() && this.mGestureDetector.onTouchEvent(event)) {
                WeekView weekView = (WeekView) v;
                if (!weekView.getDayFromLocation(event.getX(), CalendarViewLegacyDelegate.this.mTempDate) || CalendarViewLegacyDelegate.this.mTempDate.before(CalendarViewLegacyDelegate.this.mMinDate) || CalendarViewLegacyDelegate.this.mTempDate.after(CalendarViewLegacyDelegate.this.mMaxDate)) {
                    return true;
                }
                onDateTapped(CalendarViewLegacyDelegate.this.mTempDate);
                return true;
            }
            return false;
        }

        private void onDateTapped(Calendar day) {
            setSelectedDay(day);
            CalendarViewLegacyDelegate.this.setMonthDisplayed(day);
        }

        /* loaded from: classes4.dex */
        class CalendarGestureListener extends GestureDetector.SimpleOnGestureListener {
            CalendarGestureListener() {
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class WeekView extends View {
        private String[] mDayNumbers;
        private final Paint mDrawPaint;
        private Calendar mFirstDay;
        private boolean[] mFocusDay;
        private boolean mHasFocusedDay;
        private boolean mHasSelectedDay;
        private boolean mHasUnfocusedDay;
        private int mHeight;
        private int mLastWeekDayMonth;
        private final Paint mMonthNumDrawPaint;
        private int mMonthOfFirstWeekDay;
        private int mNumCells;
        private int mSelectedDay;
        private int mSelectedLeft;
        private int mSelectedRight;
        private final Rect mTempRect;
        private int mWeek;
        private int mWidth;

        public WeekView(Context context) {
            super(context);
            this.mTempRect = new Rect();
            this.mDrawPaint = new Paint();
            this.mMonthNumDrawPaint = new Paint();
            this.mMonthOfFirstWeekDay = -1;
            this.mLastWeekDayMonth = -1;
            this.mWeek = -1;
            this.mHasSelectedDay = false;
            this.mSelectedDay = -1;
            this.mSelectedLeft = -1;
            this.mSelectedRight = -1;
            initializePaints();
        }

        public void init(int weekNumber, int selectedWeekDay, int focusedMonth) {
            this.mSelectedDay = selectedWeekDay;
            this.mHasSelectedDay = selectedWeekDay != -1;
            this.mNumCells = CalendarViewLegacyDelegate.this.mShowWeekNumber ? CalendarViewLegacyDelegate.this.mDaysPerWeek + 1 : CalendarViewLegacyDelegate.this.mDaysPerWeek;
            this.mWeek = weekNumber;
            CalendarViewLegacyDelegate.this.mTempDate.setTimeInMillis(CalendarViewLegacyDelegate.this.mMinDate.getTimeInMillis());
            CalendarViewLegacyDelegate.this.mTempDate.add(3, this.mWeek);
            CalendarViewLegacyDelegate.this.mTempDate.setFirstDayOfWeek(CalendarViewLegacyDelegate.this.mFirstDayOfWeek);
            int i = this.mNumCells;
            this.mDayNumbers = new String[i];
            this.mFocusDay = new boolean[i];
            int i2 = 0;
            if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
                this.mDayNumbers[0] = String.format(Locale.getDefault(), "%d", Integer.valueOf(CalendarViewLegacyDelegate.this.mTempDate.get(3)));
                i2 = 0 + 1;
            }
            int diff = CalendarViewLegacyDelegate.this.mFirstDayOfWeek - CalendarViewLegacyDelegate.this.mTempDate.get(7);
            CalendarViewLegacyDelegate.this.mTempDate.add(5, diff);
            this.mFirstDay = (Calendar) CalendarViewLegacyDelegate.this.mTempDate.clone();
            this.mMonthOfFirstWeekDay = CalendarViewLegacyDelegate.this.mTempDate.get(2);
            this.mHasUnfocusedDay = true;
            while (i2 < this.mNumCells) {
                boolean isFocusedDay = CalendarViewLegacyDelegate.this.mTempDate.get(2) == focusedMonth;
                this.mFocusDay[i2] = isFocusedDay;
                this.mHasFocusedDay |= isFocusedDay;
                this.mHasUnfocusedDay &= !isFocusedDay;
                if (CalendarViewLegacyDelegate.this.mTempDate.before(CalendarViewLegacyDelegate.this.mMinDate) || CalendarViewLegacyDelegate.this.mTempDate.after(CalendarViewLegacyDelegate.this.mMaxDate)) {
                    this.mDayNumbers[i2] = "";
                } else {
                    this.mDayNumbers[i2] = String.format(Locale.getDefault(), "%d", Integer.valueOf(CalendarViewLegacyDelegate.this.mTempDate.get(5)));
                }
                CalendarViewLegacyDelegate.this.mTempDate.add(5, 1);
                i2++;
            }
            if (CalendarViewLegacyDelegate.this.mTempDate.get(5) == 1) {
                CalendarViewLegacyDelegate.this.mTempDate.add(5, -1);
            }
            this.mLastWeekDayMonth = CalendarViewLegacyDelegate.this.mTempDate.get(2);
            updateSelectionPositions();
        }

        private void initializePaints() {
            this.mDrawPaint.setFakeBoldText(false);
            this.mDrawPaint.setAntiAlias(true);
            this.mDrawPaint.setStyle(Paint.Style.FILL);
            this.mMonthNumDrawPaint.setFakeBoldText(true);
            this.mMonthNumDrawPaint.setAntiAlias(true);
            this.mMonthNumDrawPaint.setStyle(Paint.Style.FILL);
            this.mMonthNumDrawPaint.setTextAlign(Paint.Align.CENTER);
            this.mMonthNumDrawPaint.setTextSize(CalendarViewLegacyDelegate.this.mDateTextSize);
        }

        public int getMonthOfFirstWeekDay() {
            return this.mMonthOfFirstWeekDay;
        }

        public int getMonthOfLastWeekDay() {
            return this.mLastWeekDayMonth;
        }

        public Calendar getFirstDay() {
            return this.mFirstDay;
        }

        public boolean getDayFromLocation(float x, Calendar outCalendar) {
            int start;
            int end;
            boolean isLayoutRtl = isLayoutRtl();
            if (isLayoutRtl) {
                start = 0;
                if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
                    int i = this.mWidth;
                    end = i - (i / this.mNumCells);
                } else {
                    end = this.mWidth;
                }
            } else {
                start = CalendarViewLegacyDelegate.this.mShowWeekNumber ? this.mWidth / this.mNumCells : 0;
                end = this.mWidth;
            }
            if (x < start || x > end) {
                outCalendar.clear();
                return false;
            }
            int dayPosition = (int) (((x - start) * CalendarViewLegacyDelegate.this.mDaysPerWeek) / (end - start));
            if (isLayoutRtl) {
                dayPosition = (CalendarViewLegacyDelegate.this.mDaysPerWeek - 1) - dayPosition;
            }
            outCalendar.setTimeInMillis(this.mFirstDay.getTimeInMillis());
            outCalendar.add(5, dayPosition);
            return true;
        }

        public boolean getBoundsForDate(Calendar date, Rect outBounds) {
            Calendar currDay = Calendar.getInstance();
            currDay.setTime(this.mFirstDay.getTime());
            for (int i = 0; i < CalendarViewLegacyDelegate.this.mDaysPerWeek; i++) {
                if (date.get(1) == currDay.get(1) && date.get(2) == currDay.get(2) && date.get(5) == currDay.get(5)) {
                    int cellSize = this.mWidth / this.mNumCells;
                    if (isLayoutRtl()) {
                        outBounds.left = (CalendarViewLegacyDelegate.this.mShowWeekNumber ? (this.mNumCells - i) - 2 : (this.mNumCells - i) - 1) * cellSize;
                    } else {
                        outBounds.left = (CalendarViewLegacyDelegate.this.mShowWeekNumber ? i + 1 : i) * cellSize;
                    }
                    outBounds.top = 0;
                    outBounds.right = outBounds.left + cellSize;
                    outBounds.bottom = getHeight();
                    return true;
                }
                currDay.add(5, 1);
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            drawBackground(canvas);
            drawWeekNumbersAndDates(canvas);
            drawWeekSeparators(canvas);
            drawSelectedDateVerticalBars(canvas);
        }

        private void drawBackground(Canvas canvas) {
            int i;
            if (!this.mHasSelectedDay) {
                return;
            }
            this.mDrawPaint.setColor(CalendarViewLegacyDelegate.this.mSelectedWeekBackgroundColor);
            this.mTempRect.top = CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth;
            this.mTempRect.bottom = this.mHeight;
            boolean isLayoutRtl = isLayoutRtl();
            if (isLayoutRtl) {
                this.mTempRect.left = 0;
                this.mTempRect.right = this.mSelectedLeft - 2;
            } else {
                this.mTempRect.left = CalendarViewLegacyDelegate.this.mShowWeekNumber ? this.mWidth / this.mNumCells : 0;
                this.mTempRect.right = this.mSelectedLeft - 2;
            }
            canvas.drawRect(this.mTempRect, this.mDrawPaint);
            if (isLayoutRtl) {
                this.mTempRect.left = this.mSelectedRight + 3;
                Rect rect = this.mTempRect;
                if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
                    int i2 = this.mWidth;
                    i = i2 - (i2 / this.mNumCells);
                } else {
                    i = this.mWidth;
                }
                rect.right = i;
            } else {
                this.mTempRect.left = this.mSelectedRight + 3;
                this.mTempRect.right = this.mWidth;
            }
            canvas.drawRect(this.mTempRect, this.mDrawPaint);
        }

        private void drawWeekNumbersAndDates(Canvas canvas) {
            float textHeight = this.mDrawPaint.getTextSize();
            int y = ((int) ((this.mHeight + textHeight) / 2.0f)) - CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth;
            int nDays = this.mNumCells;
            int divisor = nDays * 2;
            this.mDrawPaint.setTextAlign(Paint.Align.CENTER);
            this.mDrawPaint.setTextSize(CalendarViewLegacyDelegate.this.mDateTextSize);
            int i = 0;
            if (isLayoutRtl()) {
                while (i < nDays - 1) {
                    this.mMonthNumDrawPaint.setColor(this.mFocusDay[i] ? CalendarViewLegacyDelegate.this.mFocusedMonthDateColor : CalendarViewLegacyDelegate.this.mUnfocusedMonthDateColor);
                    int x = (((i * 2) + 1) * this.mWidth) / divisor;
                    canvas.drawText(this.mDayNumbers[(nDays - 1) - i], x, y, this.mMonthNumDrawPaint);
                    i++;
                }
                if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
                    this.mDrawPaint.setColor(CalendarViewLegacyDelegate.this.mWeekNumberColor);
                    int i2 = this.mWidth;
                    int x2 = i2 - (i2 / divisor);
                    canvas.drawText(this.mDayNumbers[0], x2, y, this.mDrawPaint);
                    return;
                }
                return;
            }
            if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
                this.mDrawPaint.setColor(CalendarViewLegacyDelegate.this.mWeekNumberColor);
                int x3 = this.mWidth / divisor;
                canvas.drawText(this.mDayNumbers[0], x3, y, this.mDrawPaint);
                i = 0 + 1;
            }
            while (i < nDays) {
                this.mMonthNumDrawPaint.setColor(this.mFocusDay[i] ? CalendarViewLegacyDelegate.this.mFocusedMonthDateColor : CalendarViewLegacyDelegate.this.mUnfocusedMonthDateColor);
                int x4 = (((i * 2) + 1) * this.mWidth) / divisor;
                canvas.drawText(this.mDayNumbers[i], x4, y, this.mMonthNumDrawPaint);
                i++;
            }
        }

        private void drawWeekSeparators(Canvas canvas) {
            float startX;
            float stopX;
            int i;
            int firstFullyVisiblePosition = CalendarViewLegacyDelegate.this.mListView.getFirstVisiblePosition();
            if (CalendarViewLegacyDelegate.this.mListView.getChildAt(0).getTop() < 0) {
                firstFullyVisiblePosition++;
            }
            if (firstFullyVisiblePosition == this.mWeek) {
                return;
            }
            this.mDrawPaint.setColor(CalendarViewLegacyDelegate.this.mWeekSeparatorLineColor);
            this.mDrawPaint.setStrokeWidth(CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth);
            if (isLayoutRtl()) {
                startX = 0.0f;
                if (CalendarViewLegacyDelegate.this.mShowWeekNumber) {
                    int i2 = this.mWidth;
                    i = i2 - (i2 / this.mNumCells);
                } else {
                    i = this.mWidth;
                }
                stopX = i;
            } else {
                startX = CalendarViewLegacyDelegate.this.mShowWeekNumber ? this.mWidth / this.mNumCells : 0.0f;
                stopX = this.mWidth;
            }
            canvas.drawLine(startX, 0.0f, stopX, 0.0f, this.mDrawPaint);
        }

        private void drawSelectedDateVerticalBars(Canvas canvas) {
            if (!this.mHasSelectedDay) {
                return;
            }
            CalendarViewLegacyDelegate.this.mSelectedDateVerticalBar.setBounds(this.mSelectedLeft - (CalendarViewLegacyDelegate.this.mSelectedDateVerticalBarWidth / 2), CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth, this.mSelectedLeft + (CalendarViewLegacyDelegate.this.mSelectedDateVerticalBarWidth / 2), this.mHeight);
            CalendarViewLegacyDelegate.this.mSelectedDateVerticalBar.draw(canvas);
            CalendarViewLegacyDelegate.this.mSelectedDateVerticalBar.setBounds(this.mSelectedRight - (CalendarViewLegacyDelegate.this.mSelectedDateVerticalBarWidth / 2), CalendarViewLegacyDelegate.this.mWeekSeparatorLineWidth, this.mSelectedRight + (CalendarViewLegacyDelegate.this.mSelectedDateVerticalBarWidth / 2), this.mHeight);
            CalendarViewLegacyDelegate.this.mSelectedDateVerticalBar.draw(canvas);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            this.mWidth = w;
            updateSelectionPositions();
        }

        private void updateSelectionPositions() {
            if (this.mHasSelectedDay) {
                boolean isLayoutRtl = isLayoutRtl();
                int selectedPosition = this.mSelectedDay - CalendarViewLegacyDelegate.this.mFirstDayOfWeek;
                if (selectedPosition < 0) {
                    selectedPosition += 7;
                }
                if (CalendarViewLegacyDelegate.this.mShowWeekNumber && !isLayoutRtl) {
                    selectedPosition++;
                }
                if (isLayoutRtl) {
                    this.mSelectedLeft = (((CalendarViewLegacyDelegate.this.mDaysPerWeek - 1) - selectedPosition) * this.mWidth) / this.mNumCells;
                } else {
                    this.mSelectedLeft = (this.mWidth * selectedPosition) / this.mNumCells;
                }
                this.mSelectedRight = this.mSelectedLeft + (this.mWidth / this.mNumCells);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.mHeight = ((CalendarViewLegacyDelegate.this.mListView.getHeight() - CalendarViewLegacyDelegate.this.mListView.getPaddingTop()) - CalendarViewLegacyDelegate.this.mListView.getPaddingBottom()) / CalendarViewLegacyDelegate.this.mShownWeekCount;
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), this.mHeight);
        }
    }
}
