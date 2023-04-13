package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.icu.util.Calendar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleMonthView;
import com.android.internal.widget.PagerAdapter;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class DayPickerPagerAdapter extends PagerAdapter {
    private static final int MONTHS_IN_YEAR = 12;
    private ColorStateList mCalendarTextColor;
    private final int mCalendarViewId;
    private int mCount;
    private ColorStateList mDayHighlightColor;
    private int mDayOfWeekTextAppearance;
    private ColorStateList mDaySelectorColor;
    private int mDayTextAppearance;
    private int mFirstDayOfWeek;
    private final LayoutInflater mInflater;
    private final int mLayoutResId;
    private int mMonthTextAppearance;
    private OnDaySelectedListener mOnDaySelectedListener;
    private final Calendar mMinDate = Calendar.getInstance();
    private final Calendar mMaxDate = Calendar.getInstance();
    private final SparseArray<ViewHolder> mItems = new SparseArray<>();
    private Calendar mSelectedDay = null;
    private final SimpleMonthView.OnDayClickListener mOnDayClickListener = new SimpleMonthView.OnDayClickListener() { // from class: android.widget.DayPickerPagerAdapter.1
        @Override // android.widget.SimpleMonthView.OnDayClickListener
        public void onDayClick(SimpleMonthView view, Calendar day) {
            if (day != null) {
                DayPickerPagerAdapter.this.setSelectedDay(day);
                if (DayPickerPagerAdapter.this.mOnDaySelectedListener != null) {
                    DayPickerPagerAdapter.this.mOnDaySelectedListener.onDaySelected(DayPickerPagerAdapter.this, day);
                }
            }
        }
    };

    /* loaded from: classes4.dex */
    public interface OnDaySelectedListener {
        void onDaySelected(DayPickerPagerAdapter dayPickerPagerAdapter, Calendar calendar);
    }

    public DayPickerPagerAdapter(Context context, int layoutResId, int calendarViewId) {
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutResId = layoutResId;
        this.mCalendarViewId = calendarViewId;
        TypedArray ta = context.obtainStyledAttributes(new int[]{16843820});
        this.mDayHighlightColor = ta.getColorStateList(0);
        ta.recycle();
    }

    public void setRange(Calendar min, Calendar max) {
        this.mMinDate.setTimeInMillis(min.getTimeInMillis());
        this.mMaxDate.setTimeInMillis(max.getTimeInMillis());
        int diffYear = this.mMaxDate.get(1) - this.mMinDate.get(1);
        int diffMonth = this.mMaxDate.get(2) - this.mMinDate.get(2);
        this.mCount = (diffYear * 12) + diffMonth + 1;
        notifyDataSetChanged();
    }

    public void setFirstDayOfWeek(int weekStart) {
        this.mFirstDayOfWeek = weekStart;
        int count = this.mItems.size();
        for (int i = 0; i < count; i++) {
            SimpleMonthView monthView = this.mItems.valueAt(i).calendar;
            monthView.setFirstDayOfWeek(weekStart);
        }
    }

    public int getFirstDayOfWeek() {
        return this.mFirstDayOfWeek;
    }

    public boolean getBoundsForDate(Calendar day, Rect outBounds) {
        int position = getPositionForDay(day);
        ViewHolder monthView = this.mItems.get(position, null);
        if (monthView == null) {
            return false;
        }
        int dayOfMonth = day.get(5);
        return monthView.calendar.getBoundsForDay(dayOfMonth, outBounds);
    }

    public void setSelectedDay(Calendar day) {
        ViewHolder newMonthView;
        ViewHolder oldMonthView;
        int oldPosition = getPositionForDay(this.mSelectedDay);
        int newPosition = getPositionForDay(day);
        if (oldPosition != newPosition && oldPosition >= 0 && (oldMonthView = this.mItems.get(oldPosition, null)) != null) {
            oldMonthView.calendar.setSelectedDay(-1);
        }
        if (newPosition >= 0 && (newMonthView = this.mItems.get(newPosition, null)) != null) {
            int dayOfMonth = day.get(5);
            newMonthView.calendar.setSelectedDay(dayOfMonth);
        }
        this.mSelectedDay = day;
    }

    public void setOnDaySelectedListener(OnDaySelectedListener listener) {
        this.mOnDaySelectedListener = listener;
    }

    void setCalendarTextColor(ColorStateList calendarTextColor) {
        this.mCalendarTextColor = calendarTextColor;
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDaySelectorColor(ColorStateList selectorColor) {
        this.mDaySelectorColor = selectorColor;
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMonthTextAppearance(int resId) {
        this.mMonthTextAppearance = resId;
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDayOfWeekTextAppearance(int resId) {
        this.mDayOfWeekTextAppearance = resId;
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDayOfWeekTextAppearance() {
        return this.mDayOfWeekTextAppearance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDayTextAppearance(int resId) {
        this.mDayTextAppearance = resId;
        notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getDayTextAppearance() {
        return this.mDayTextAppearance;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public int getCount() {
        return this.mCount;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object object) {
        ViewHolder holder = (ViewHolder) object;
        return view == holder.container;
    }

    private int getMonthForPosition(int position) {
        return (this.mMinDate.get(2) + position) % 12;
    }

    private int getYearForPosition(int position) {
        int yearOffset = (this.mMinDate.get(2) + position) / 12;
        return this.mMinDate.get(1) + yearOffset;
    }

    private int getPositionForDay(Calendar day) {
        if (day == null) {
            return -1;
        }
        int yearOffset = day.get(1) - this.mMinDate.get(1);
        int monthOffset = day.get(2) - this.mMinDate.get(2);
        int position = (yearOffset * 12) + monthOffset;
        return position;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public Object instantiateItem(ViewGroup container, int position) {
        int selectedDay;
        int enabledDayRangeStart;
        int enabledDayRangeEnd;
        View itemView = this.mInflater.inflate(this.mLayoutResId, container, false);
        SimpleMonthView v = (SimpleMonthView) itemView.findViewById(this.mCalendarViewId);
        v.setOnDayClickListener(this.mOnDayClickListener);
        v.setMonthTextAppearance(this.mMonthTextAppearance);
        v.setDayOfWeekTextAppearance(this.mDayOfWeekTextAppearance);
        v.setDayTextAppearance(this.mDayTextAppearance);
        ColorStateList colorStateList = this.mDaySelectorColor;
        if (colorStateList != null) {
            v.setDaySelectorColor(colorStateList);
        }
        ColorStateList colorStateList2 = this.mDayHighlightColor;
        if (colorStateList2 != null) {
            v.setDayHighlightColor(colorStateList2);
        }
        ColorStateList colorStateList3 = this.mCalendarTextColor;
        if (colorStateList3 != null) {
            v.setMonthTextColor(colorStateList3);
            v.setDayOfWeekTextColor(this.mCalendarTextColor);
            v.setDayTextColor(this.mCalendarTextColor);
        }
        int month = getMonthForPosition(position);
        int year = getYearForPosition(position);
        Calendar calendar = this.mSelectedDay;
        if (calendar != null && calendar.get(2) == month && this.mSelectedDay.get(1) == year) {
            selectedDay = this.mSelectedDay.get(5);
        } else {
            selectedDay = -1;
        }
        if (this.mMinDate.get(2) == month && this.mMinDate.get(1) == year) {
            enabledDayRangeStart = this.mMinDate.get(5);
        } else {
            enabledDayRangeStart = 1;
        }
        if (this.mMaxDate.get(2) == month && this.mMaxDate.get(1) == year) {
            enabledDayRangeEnd = this.mMaxDate.get(5);
        } else {
            enabledDayRangeEnd = 31;
        }
        v.setMonthParams(selectedDay, month, year, this.mFirstDayOfWeek, enabledDayRangeStart, enabledDayRangeEnd);
        ViewHolder holder = new ViewHolder(position, itemView, v);
        this.mItems.put(position, holder);
        container.addView(itemView);
        return holder;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewHolder holder = (ViewHolder) object;
        container.removeView(holder.container);
        this.mItems.remove(position);
    }

    @Override // com.android.internal.widget.PagerAdapter
    public int getItemPosition(Object object) {
        ViewHolder holder = (ViewHolder) object;
        return holder.position;
    }

    @Override // com.android.internal.widget.PagerAdapter
    public CharSequence getPageTitle(int position) {
        SimpleMonthView v = this.mItems.get(position).calendar;
        if (v != null) {
            return v.getMonthYearLabel();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleMonthView getView(Object object) {
        if (object == null) {
            return null;
        }
        ViewHolder holder = (ViewHolder) object;
        return holder.calendar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ViewHolder {
        public final SimpleMonthView calendar;
        public final View container;
        public final int position;

        public ViewHolder(int position, View container, SimpleMonthView calendar) {
            this.position = position;
            this.container = container;
            this.calendar = calendar;
        }
    }
}
