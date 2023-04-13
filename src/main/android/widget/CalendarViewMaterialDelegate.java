package android.widget;

import android.content.Context;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.widget.CalendarView;
import android.widget.DayPickerView;
/* loaded from: classes4.dex */
class CalendarViewMaterialDelegate extends CalendarView.AbstractCalendarViewDelegate {
    private final DayPickerView mDayPickerView;
    private CalendarView.OnDateChangeListener mOnDateChangeListener;
    private final DayPickerView.OnDaySelectedListener mOnDaySelectedListener;

    public CalendarViewMaterialDelegate(CalendarView delegator, Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(delegator, context);
        DayPickerView.OnDaySelectedListener onDaySelectedListener = new DayPickerView.OnDaySelectedListener() { // from class: android.widget.CalendarViewMaterialDelegate.1
            @Override // android.widget.DayPickerView.OnDaySelectedListener
            public void onDaySelected(DayPickerView view, Calendar day) {
                if (CalendarViewMaterialDelegate.this.mOnDateChangeListener != null) {
                    int year = day.get(1);
                    int month = day.get(2);
                    int dayOfMonth = day.get(5);
                    CalendarViewMaterialDelegate.this.mOnDateChangeListener.onSelectedDayChange(CalendarViewMaterialDelegate.this.mDelegator, year, month, dayOfMonth);
                }
            }
        };
        this.mOnDaySelectedListener = onDaySelectedListener;
        DayPickerView dayPickerView = new DayPickerView(context, attrs, defStyleAttr, defStyleRes);
        this.mDayPickerView = dayPickerView;
        dayPickerView.setOnDaySelectedListener(onDaySelectedListener);
        delegator.addView(dayPickerView);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setWeekDayTextAppearance(int resId) {
        this.mDayPickerView.setDayOfWeekTextAppearance(resId);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public int getWeekDayTextAppearance() {
        return this.mDayPickerView.getDayOfWeekTextAppearance();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setDateTextAppearance(int resId) {
        this.mDayPickerView.setDayTextAppearance(resId);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public int getDateTextAppearance() {
        return this.mDayPickerView.getDayTextAppearance();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setMinDate(long minDate) {
        this.mDayPickerView.setMinDate(minDate);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public long getMinDate() {
        return this.mDayPickerView.getMinDate();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setMaxDate(long maxDate) {
        this.mDayPickerView.setMaxDate(maxDate);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public long getMaxDate() {
        return this.mDayPickerView.getMaxDate();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.mDayPickerView.setFirstDayOfWeek(firstDayOfWeek);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public int getFirstDayOfWeek() {
        return this.mDayPickerView.getFirstDayOfWeek();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setDate(long date) {
        this.mDayPickerView.setDate(date, true);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setDate(long date, boolean animate, boolean center) {
        this.mDayPickerView.setDate(date, animate);
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public long getDate() {
        return this.mDayPickerView.getDate();
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public void setOnDateChangeListener(CalendarView.OnDateChangeListener listener) {
        this.mOnDateChangeListener = listener;
    }

    @Override // android.widget.CalendarView.CalendarViewDelegate
    public boolean getBoundsForDate(long date, Rect outBounds) {
        boolean result = this.mDayPickerView.getBoundsForDate(date, outBounds);
        if (result) {
            int[] dayPickerPositionOnScreen = new int[2];
            int[] delegatorPositionOnScreen = new int[2];
            this.mDayPickerView.getLocationOnScreen(dayPickerPositionOnScreen);
            this.mDelegator.getLocationOnScreen(delegatorPositionOnScreen);
            int extraVerticalOffset = dayPickerPositionOnScreen[1] - delegatorPositionOnScreen[1];
            outBounds.top += extraVerticalOffset;
            outBounds.bottom += extraVerticalOffset;
            return true;
        }
        return false;
    }
}
