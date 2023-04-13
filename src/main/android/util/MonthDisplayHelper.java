package android.util;

import java.util.Calendar;
/* loaded from: classes3.dex */
public class MonthDisplayHelper {
    private Calendar mCalendar;
    private int mNumDaysInMonth;
    private int mNumDaysInPrevMonth;
    private int mOffset;
    private final int mWeekStartDay;

    public MonthDisplayHelper(int year, int month, int weekStartDay) {
        if (weekStartDay < 1 || weekStartDay > 7) {
            throw new IllegalArgumentException();
        }
        this.mWeekStartDay = weekStartDay;
        Calendar calendar = Calendar.getInstance();
        this.mCalendar = calendar;
        calendar.set(1, year);
        this.mCalendar.set(2, month);
        this.mCalendar.set(5, 1);
        this.mCalendar.set(11, 0);
        this.mCalendar.set(12, 0);
        this.mCalendar.set(13, 0);
        this.mCalendar.getTimeInMillis();
        recalculate();
    }

    public MonthDisplayHelper(int year, int month) {
        this(year, month, 1);
    }

    public int getYear() {
        return this.mCalendar.get(1);
    }

    public int getMonth() {
        return this.mCalendar.get(2);
    }

    public int getWeekStartDay() {
        return this.mWeekStartDay;
    }

    public int getFirstDayOfMonth() {
        return this.mCalendar.get(7);
    }

    public int getNumberOfDaysInMonth() {
        return this.mNumDaysInMonth;
    }

    public int getOffset() {
        return this.mOffset;
    }

    public int[] getDigitsForRow(int row) {
        if (row < 0 || row > 5) {
            throw new IllegalArgumentException("row " + row + " out of range (0-5)");
        }
        int[] result = new int[7];
        for (int column = 0; column < 7; column++) {
            result[column] = getDayAt(row, column);
        }
        return result;
    }

    public int getDayAt(int row, int column) {
        int i;
        if (row == 0 && column < (i = this.mOffset)) {
            return ((this.mNumDaysInPrevMonth + column) - i) + 1;
        }
        int day = (((row * 7) + column) - this.mOffset) + 1;
        int i2 = this.mNumDaysInMonth;
        return day > i2 ? day - i2 : day;
    }

    public int getRowOf(int day) {
        return ((this.mOffset + day) - 1) / 7;
    }

    public int getColumnOf(int day) {
        return ((this.mOffset + day) - 1) % 7;
    }

    public void previousMonth() {
        this.mCalendar.add(2, -1);
        recalculate();
    }

    public void nextMonth() {
        this.mCalendar.add(2, 1);
        recalculate();
    }

    public boolean isWithinCurrentMonth(int row, int column) {
        if (row < 0 || column < 0 || row > 5 || column > 6) {
            return false;
        }
        if (row == 0 && column < this.mOffset) {
            return false;
        }
        int day = (((row * 7) + column) - this.mOffset) + 1;
        return day <= this.mNumDaysInMonth;
    }

    private void recalculate() {
        this.mNumDaysInMonth = this.mCalendar.getActualMaximum(5);
        this.mCalendar.add(2, -1);
        this.mNumDaysInPrevMonth = this.mCalendar.getActualMaximum(5);
        this.mCalendar.add(2, 1);
        int firstDayOfMonth = getFirstDayOfMonth();
        int offset = firstDayOfMonth - this.mWeekStartDay;
        if (offset < 0) {
            offset += 7;
        }
        this.mOffset = offset;
    }
}
