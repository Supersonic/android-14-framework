package android.app.admin;

import android.app.admin.SystemUpdatePolicy;
import android.util.Log;
import android.util.Pair;
import com.android.internal.content.NativeLibraryHelper;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class FreezePeriod {
    static final int DAYS_IN_YEAR = 365;
    private static final int SENTINEL_YEAR = 2001;
    private static final String TAG = "FreezePeriod";
    private final MonthDay mEnd;
    private final int mEndDay;
    private final MonthDay mStart;
    private final int mStartDay;

    public FreezePeriod(MonthDay start, MonthDay end) {
        this.mStart = start;
        this.mStartDay = start.atYear(2001).getDayOfYear();
        this.mEnd = end;
        this.mEndDay = end.atYear(2001).getDayOfYear();
    }

    public MonthDay getStart() {
        return this.mStart;
    }

    public MonthDay getEnd() {
        return this.mEnd;
    }

    private FreezePeriod(int startDay, int endDay) {
        this.mStartDay = startDay;
        this.mStart = dayOfYearToMonthDay(startDay);
        this.mEndDay = endDay;
        this.mEnd = dayOfYearToMonthDay(endDay);
    }

    int getLength() {
        return (getEffectiveEndDay() - this.mStartDay) + 1;
    }

    boolean isWrapped() {
        return this.mEndDay < this.mStartDay;
    }

    int getEffectiveEndDay() {
        if (!isWrapped()) {
            return this.mEndDay;
        }
        return this.mEndDay + 365;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean contains(LocalDate localDate) {
        int daysOfYear = dayOfYearDisregardLeapYear(localDate);
        return !isWrapped() ? this.mStartDay <= daysOfYear && daysOfYear <= this.mEndDay : this.mStartDay <= daysOfYear || daysOfYear <= this.mEndDay;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean after(LocalDate localDate) {
        return this.mStartDay > dayOfYearDisregardLeapYear(localDate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v5 */
    /* JADX WARN: Type inference failed for: r2v6 */
    public Pair<LocalDate, LocalDate> toCurrentOrFutureRealDates(LocalDate now) {
        int startYearAdjustment;
        int endYearAdjustment;
        int nowDays = dayOfYearDisregardLeapYear(now);
        if (contains(now)) {
            if (this.mStartDay <= nowDays) {
                startYearAdjustment = 0;
                endYearAdjustment = isWrapped();
            } else {
                startYearAdjustment = -1;
                endYearAdjustment = 0;
            }
        } else {
            int startYearAdjustment2 = this.mStartDay;
            if (startYearAdjustment2 > nowDays) {
                startYearAdjustment = 0;
                endYearAdjustment = isWrapped();
            } else {
                startYearAdjustment = 1;
                endYearAdjustment = 1;
            }
        }
        LocalDate startDate = LocalDate.ofYearDay(2001, this.mStartDay).withYear(now.getYear() + startYearAdjustment);
        LocalDate endDate = LocalDate.ofYearDay(2001, this.mEndDay).withYear(now.getYear() + endYearAdjustment);
        return new Pair<>(startDate, endDate);
    }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        return LocalDate.ofYearDay(2001, this.mStartDay).format(formatter) + " - " + LocalDate.ofYearDay(2001, this.mEndDay).format(formatter);
    }

    private static MonthDay dayOfYearToMonthDay(int dayOfYear) {
        LocalDate date = LocalDate.ofYearDay(2001, dayOfYear);
        return MonthDay.of(date.getMonth(), date.getDayOfMonth());
    }

    private static int dayOfYearDisregardLeapYear(LocalDate date) {
        return date.withYear(2001).getDayOfYear();
    }

    public static int distanceWithoutLeapYear(LocalDate first, LocalDate second) {
        return (dayOfYearDisregardLeapYear(first) - dayOfYearDisregardLeapYear(second)) + ((first.getYear() - second.getYear()) * 365);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<FreezePeriod> canonicalizePeriods(List<FreezePeriod> intervals) {
        boolean[] taken = new boolean[365];
        for (FreezePeriod interval : intervals) {
            for (int i = interval.mStartDay; i <= interval.getEffectiveEndDay(); i++) {
                taken[(i - 1) % 365] = true;
            }
        }
        List<FreezePeriod> result = new ArrayList<>();
        int i2 = 0;
        while (i2 < 365) {
            if (!taken[i2]) {
                i2++;
            } else {
                int intervalStart = i2 + 1;
                while (i2 < 365 && taken[i2]) {
                    i2++;
                }
                result.add(new FreezePeriod(intervalStart, i2));
            }
        }
        int lastIndex = result.size() - 1;
        if (lastIndex > 0 && result.get(lastIndex).mEndDay == 365 && result.get(0).mStartDay == 1) {
            FreezePeriod wrappedInterval = new FreezePeriod(result.get(lastIndex).mStartDay, result.get(0).mEndDay);
            result.set(lastIndex, wrappedInterval);
            result.remove(0);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void validatePeriods(List<FreezePeriod> periods) {
        int separation;
        List<FreezePeriod> allPeriods = canonicalizePeriods(periods);
        if (allPeriods.size() != periods.size()) {
            throw SystemUpdatePolicy.ValidationFailedException.duplicateOrOverlapPeriods();
        }
        int i = 0;
        while (i < allPeriods.size()) {
            FreezePeriod current = allPeriods.get(i);
            if (current.getLength() > 90) {
                throw SystemUpdatePolicy.ValidationFailedException.freezePeriodTooLong("Freeze period " + current + " is too long: " + current.getLength() + " days");
            }
            FreezePeriod previous = i > 0 ? allPeriods.get(i - 1) : allPeriods.get(allPeriods.size() - 1);
            if (previous != current) {
                if (i == 0 && !previous.isWrapped()) {
                    separation = (current.mStartDay + (365 - previous.mEndDay)) - 1;
                } else {
                    int separation2 = current.mStartDay;
                    separation = (separation2 - previous.mEndDay) - 1;
                }
                if (separation < 60) {
                    throw SystemUpdatePolicy.ValidationFailedException.freezePeriodTooClose("Freeze periods " + previous + " and " + current + " are too close together: " + separation + " days apart");
                }
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0059  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void validateAgainstPreviousFreezePeriod(List<FreezePeriod> periods, LocalDate prevPeriodStart, LocalDate prevPeriodEnd, LocalDate now) {
        if (periods.size() == 0 || prevPeriodStart == null || prevPeriodEnd == null) {
            return;
        }
        if (prevPeriodStart.isAfter(now) || prevPeriodEnd.isAfter(now)) {
            Log.m104w(TAG, "Previous period (" + prevPeriodStart + "," + prevPeriodEnd + ") is after current date " + now);
        }
        List<FreezePeriod> allPeriods = canonicalizePeriods(periods);
        FreezePeriod curOrNextFreezePeriod = allPeriods.get(0);
        for (FreezePeriod interval : allPeriods) {
            if (interval.contains(now) || interval.mStartDay > dayOfYearDisregardLeapYear(now)) {
                curOrNextFreezePeriod = interval;
                break;
            }
            while (r3.hasNext()) {
            }
        }
        Pair<LocalDate, LocalDate> curOrNextFreezeDates = curOrNextFreezePeriod.toCurrentOrFutureRealDates(now);
        if (now.isAfter(curOrNextFreezeDates.first)) {
            curOrNextFreezeDates = new Pair<>(now, curOrNextFreezeDates.second);
        }
        if (curOrNextFreezeDates.first.isAfter(curOrNextFreezeDates.second)) {
            throw new IllegalStateException("Current freeze dates inverted: " + curOrNextFreezeDates.first + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + curOrNextFreezeDates.second);
        }
        String periodsDescription = "Prev: " + prevPeriodStart + "," + prevPeriodEnd + "; cur: " + curOrNextFreezeDates.first + "," + curOrNextFreezeDates.second;
        long separation = distanceWithoutLeapYear(curOrNextFreezeDates.first, prevPeriodEnd) - 1;
        if (separation > 0) {
            if (separation < 60) {
                throw SystemUpdatePolicy.ValidationFailedException.combinedPeriodTooClose("Previous freeze period too close to new period: " + separation + ", " + periodsDescription);
            }
            return;
        }
        long length = distanceWithoutLeapYear(curOrNextFreezeDates.second, prevPeriodStart) + 1;
        if (length > 90) {
            throw SystemUpdatePolicy.ValidationFailedException.combinedPeriodTooLong("Combined freeze period exceeds maximum days: " + length + ", " + periodsDescription);
        }
    }
}
