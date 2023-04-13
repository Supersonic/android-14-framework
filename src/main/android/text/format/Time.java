package android.text.format;

import android.app.settings.SettingsEnums;
import android.util.TimeFormatException;
import com.android.i18n.timezone.WallTime;
import com.android.i18n.timezone.ZoneInfoData;
import com.android.i18n.timezone.ZoneInfoDb;
import com.android.internal.accessibility.common.ShortcutConstants;
import com.android.internal.content.NativeLibraryHelper;
import java.util.Locale;
import java.util.TimeZone;
@Deprecated
/* loaded from: classes3.dex */
public class Time {
    public static final int EPOCH_JULIAN_DAY = 2440588;
    public static final int FRIDAY = 5;
    public static final int HOUR = 3;
    public static final int MINUTE = 2;
    public static final int MONDAY = 1;
    public static final int MONDAY_BEFORE_JULIAN_EPOCH = 2440585;
    public static final int MONTH = 5;
    public static final int MONTH_DAY = 4;
    public static final int SATURDAY = 6;
    public static final int SECOND = 1;
    public static final int SUNDAY = 0;
    public static final int THURSDAY = 4;
    public static final String TIMEZONE_UTC = "UTC";
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int WEEK_DAY = 7;
    public static final int WEEK_NUM = 9;
    public static final int YEAR = 6;
    public static final int YEAR_DAY = 8;
    private static final String Y_M_D = "%Y-%m-%d";
    private static final String Y_M_D_T_H_M_S_000 = "%Y-%m-%dT%H:%M:%S.000";
    private static final String Y_M_D_T_H_M_S_000_Z = "%Y-%m-%dT%H:%M:%S.000Z";
    public boolean allDay;
    private TimeCalculator calculator;
    public long gmtoff;
    public int hour;
    public int isDst;
    public int minute;
    public int month;
    public int monthDay;
    public int second;
    public String timezone;
    public int weekDay;
    public int year;
    public int yearDay;
    private static final int[] DAYS_PER_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] sThursdayOffset = {-3, 3, 2, 1, 0, -1, -2};

    public Time(String timezoneId) {
        if (timezoneId == null) {
            throw new NullPointerException("timezoneId is null!");
        }
        initialize(timezoneId);
    }

    public Time() {
        initialize(TimeZone.getDefault().getID());
    }

    public Time(Time other) {
        initialize(other.timezone);
        set(other);
    }

    private void initialize(String timezoneId) {
        this.timezone = timezoneId;
        this.year = SettingsEnums.ACTION_MOBILE_NETWORK_DB_GET_UICC_INFO;
        this.monthDay = 1;
        this.isDst = -1;
        this.calculator = new TimeCalculator(timezoneId);
    }

    public long normalize(boolean ignoreDst) {
        this.calculator.copyFieldsFromTime(this);
        long timeInMillis = this.calculator.toMillis(ignoreDst);
        this.calculator.copyFieldsToTime(this);
        return timeInMillis;
    }

    public void switchTimezone(String timezone) {
        this.calculator.copyFieldsFromTime(this);
        this.calculator.switchTimeZone(timezone);
        this.calculator.copyFieldsToTime(this);
        this.timezone = timezone;
    }

    public int getActualMaximum(int field) {
        switch (field) {
            case 1:
                return 59;
            case 2:
                return 59;
            case 3:
                return 23;
            case 4:
                int n = DAYS_PER_MONTH[this.month];
                if (n != 28) {
                    return n;
                }
                int y = this.year;
                if (y % 4 == 0) {
                    return (y % 100 != 0 || y % 400 == 0) ? 29 : 28;
                }
                return 28;
            case 5:
                return 11;
            case 6:
                return 2037;
            case 7:
                return 6;
            case 8:
                int y2 = this.year;
                return (y2 % 4 != 0 || (y2 % 100 == 0 && y2 % 400 != 0)) ? 364 : 365;
            case 9:
                throw new RuntimeException("WEEK_NUM not implemented");
            default:
                throw new RuntimeException("bad field=" + field);
        }
    }

    public void clear(String timezoneId) {
        if (timezoneId == null) {
            throw new NullPointerException("timezone is null!");
        }
        this.timezone = timezoneId;
        this.allDay = false;
        this.second = 0;
        this.minute = 0;
        this.hour = 0;
        this.monthDay = 0;
        this.month = 0;
        this.year = 0;
        this.weekDay = 0;
        this.yearDay = 0;
        this.gmtoff = 0L;
        this.isDst = -1;
    }

    public static int compare(Time a, Time b) {
        if (a == null) {
            throw new NullPointerException("a == null");
        }
        if (b == null) {
            throw new NullPointerException("b == null");
        }
        a.calculator.copyFieldsFromTime(a);
        b.calculator.copyFieldsFromTime(b);
        return TimeCalculator.compare(a.calculator, b.calculator);
    }

    public String format(String format) {
        this.calculator.copyFieldsFromTime(this);
        return this.calculator.format(format);
    }

    public String toString() {
        TimeCalculator calculator = new TimeCalculator(this.timezone);
        calculator.copyFieldsFromTime(this);
        return calculator.toStringInternal();
    }

    public boolean parse(String s) {
        if (s == null) {
            throw new NullPointerException("time string is null");
        }
        if (parseInternal(s)) {
            this.timezone = TIMEZONE_UTC;
            return true;
        }
        return false;
    }

    private boolean parseInternal(String s) {
        int len = s.length();
        if (len < 8) {
            throw new TimeFormatException("String is too short: \"" + s + "\" Expected at least 8 characters.");
        }
        boolean inUtc = false;
        int n = getChar(s, 0, 1000);
        this.year = n + getChar(s, 1, 100) + getChar(s, 2, 10) + getChar(s, 3, 1);
        int n2 = getChar(s, 4, 10);
        this.month = (n2 + getChar(s, 5, 1)) - 1;
        int n3 = getChar(s, 6, 10);
        this.monthDay = n3 + getChar(s, 7, 1);
        if (len > 8) {
            if (len < 15) {
                throw new TimeFormatException("String is too short: \"" + s + "\" If there are more than 8 characters there must be at least 15.");
            }
            checkChar(s, 8, 'T');
            this.allDay = false;
            int n4 = getChar(s, 9, 10);
            this.hour = n4 + getChar(s, 10, 1);
            int n5 = getChar(s, 11, 10);
            this.minute = n5 + getChar(s, 12, 1);
            int n6 = getChar(s, 13, 10);
            this.second = n6 + getChar(s, 14, 1);
            if (len > 15) {
                checkChar(s, 15, 'Z');
                inUtc = true;
            }
        } else {
            this.allDay = true;
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
        }
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0L;
        return inUtc;
    }

    private void checkChar(String s, int spos, char expected) {
        char c = s.charAt(spos);
        if (c != expected) {
            throw new TimeFormatException(String.format("Unexpected character 0x%02d at pos=%d.  Expected 0x%02d ('%c').", Integer.valueOf(c), Integer.valueOf(spos), Integer.valueOf(expected), Character.valueOf(expected)));
        }
    }

    private static int getChar(String s, int spos, int mul) {
        char c = s.charAt(spos);
        if (Character.isDigit(c)) {
            return Character.getNumericValue(c) * mul;
        }
        throw new TimeFormatException("Parse error at pos=" + spos);
    }

    public boolean parse3339(String s) {
        if (s == null) {
            throw new NullPointerException("time string is null");
        }
        if (parse3339Internal(s)) {
            this.timezone = TIMEZONE_UTC;
            return true;
        }
        return false;
    }

    private boolean parse3339Internal(String s) {
        int len = s.length();
        if (len < 10) {
            throw new TimeFormatException("String too short --- expected at least 10 characters.");
        }
        boolean inUtc = false;
        int n = getChar(s, 0, 1000);
        this.year = n + getChar(s, 1, 100) + getChar(s, 2, 10) + getChar(s, 3, 1);
        checkChar(s, 4, '-');
        int n2 = getChar(s, 5, 10);
        this.month = (n2 + getChar(s, 6, 1)) - 1;
        checkChar(s, 7, '-');
        int n3 = getChar(s, 8, 10);
        this.monthDay = n3 + getChar(s, 9, 1);
        if (len >= 19) {
            checkChar(s, 10, 'T');
            this.allDay = false;
            int n4 = getChar(s, 11, 10);
            int hour = n4 + getChar(s, 12, 1);
            checkChar(s, 13, ShortcutConstants.SERVICES_SEPARATOR);
            int n5 = getChar(s, 14, 10);
            int minute = n5 + getChar(s, 15, 1);
            checkChar(s, 16, ShortcutConstants.SERVICES_SEPARATOR);
            int n6 = getChar(s, 17, 10);
            this.second = n6 + getChar(s, 18, 1);
            int tzIndex = 19;
            if (19 < len && s.charAt(19) == '.') {
                do {
                    tzIndex++;
                    if (tzIndex >= len) {
                        break;
                    }
                } while (Character.isDigit(s.charAt(tzIndex)));
            }
            int offset = 0;
            if (len > tzIndex) {
                char c = s.charAt(tzIndex);
                switch (c) {
                    case '+':
                        offset = -1;
                        break;
                    case '-':
                        offset = 1;
                        break;
                    case 'Z':
                        offset = 0;
                        break;
                    default:
                        throw new TimeFormatException(String.format("Unexpected character 0x%02d at position %d.  Expected + or -", Integer.valueOf(c), Integer.valueOf(tzIndex)));
                }
                inUtc = true;
                if (offset != 0) {
                    if (len >= tzIndex + 6) {
                        int n7 = getChar(s, tzIndex + 1, 10);
                        hour += (n7 + getChar(s, tzIndex + 2, 1)) * offset;
                        int n8 = getChar(s, tzIndex + 4, 10);
                        int n9 = tzIndex + 5;
                        minute += (n8 + getChar(s, n9, 1)) * offset;
                    } else {
                        throw new TimeFormatException(String.format("Unexpected length; should be %d characters", Integer.valueOf(tzIndex + 6)));
                    }
                }
            }
            this.hour = hour;
            this.minute = minute;
            if (offset != 0) {
                normalize(false);
            }
        } else {
            this.allDay = true;
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
        }
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0L;
        return inUtc;
    }

    public static String getCurrentTimezone() {
        return TimeZone.getDefault().getID();
    }

    public void setToNow() {
        set(System.currentTimeMillis());
    }

    public long toMillis(boolean ignoreDst) {
        this.calculator.copyFieldsFromTime(this);
        return this.calculator.toMillis(ignoreDst);
    }

    public void set(long millis) {
        this.allDay = false;
        this.calculator.timezone = this.timezone;
        this.calculator.setTimeInMillis(millis);
        this.calculator.copyFieldsToTime(this);
    }

    public String format2445() {
        this.calculator.copyFieldsFromTime(this);
        return this.calculator.format2445(!this.allDay);
    }

    public void set(Time that) {
        this.timezone = that.timezone;
        this.allDay = that.allDay;
        this.second = that.second;
        this.minute = that.minute;
        this.hour = that.hour;
        this.monthDay = that.monthDay;
        this.month = that.month;
        this.year = that.year;
        this.weekDay = that.weekDay;
        this.yearDay = that.yearDay;
        this.isDst = that.isDst;
        this.gmtoff = that.gmtoff;
    }

    public void set(int second, int minute, int hour, int monthDay, int month, int year) {
        this.allDay = false;
        this.second = second;
        this.minute = minute;
        this.hour = hour;
        this.monthDay = monthDay;
        this.month = month;
        this.year = year;
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0L;
    }

    public void set(int monthDay, int month, int year) {
        this.allDay = true;
        this.second = 0;
        this.minute = 0;
        this.hour = 0;
        this.monthDay = monthDay;
        this.month = month;
        this.year = year;
        this.weekDay = 0;
        this.yearDay = 0;
        this.isDst = -1;
        this.gmtoff = 0L;
    }

    public boolean before(Time that) {
        return compare(this, that) < 0;
    }

    public boolean after(Time that) {
        return compare(this, that) > 0;
    }

    public int getWeekNumber() {
        int i = this.yearDay;
        int[] iArr = sThursdayOffset;
        int closestThursday = i + iArr[this.weekDay];
        if (closestThursday >= 0 && closestThursday <= 364) {
            return (closestThursday / 7) + 1;
        }
        Time temp = new Time(this);
        temp.monthDay += iArr[this.weekDay];
        temp.normalize(true);
        return (temp.yearDay / 7) + 1;
    }

    public String format3339(boolean allDay) {
        if (allDay) {
            return format(Y_M_D);
        }
        if (TIMEZONE_UTC.equals(this.timezone)) {
            return format(Y_M_D_T_H_M_S_000_Z);
        }
        String base = format(Y_M_D_T_H_M_S_000);
        long j = this.gmtoff;
        String sign = j < 0 ? NativeLibraryHelper.CLEAR_ABI_OVERRIDE : "+";
        int offset = (int) Math.abs(j);
        int minutes = (offset % 3600) / 60;
        int hours = offset / 3600;
        return String.format(Locale.US, "%s%s%02d:%02d", base, sign, Integer.valueOf(hours), Integer.valueOf(minutes));
    }

    public static boolean isEpoch(Time time) {
        long millis = time.toMillis(true);
        return getJulianDay(millis, 0L) == 2440588;
    }

    @Deprecated
    public static int getJulianDay(long millis, long gmtoffSeconds) {
        long offsetMillis = 1000 * gmtoffSeconds;
        long adjustedMillis = millis + offsetMillis;
        long julianDay = adjustedMillis / 86400000;
        if (adjustedMillis < 0 && adjustedMillis % 86400000 != 0) {
            julianDay--;
        }
        return (int) (2440588 + julianDay);
    }

    public long setJulianDay(int julianDay) {
        long millis = (julianDay - EPOCH_JULIAN_DAY) * 86400000;
        set(millis);
        int approximateDay = getJulianDay(millis, this.gmtoff);
        int diff = julianDay - approximateDay;
        this.monthDay += diff;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        return normalize(true);
    }

    public static int getWeeksSinceEpochFromJulianDay(int julianDay, int firstDayOfWeek) {
        int diff = 4 - firstDayOfWeek;
        if (diff < 0) {
            diff += 7;
        }
        int refDay = EPOCH_JULIAN_DAY - diff;
        return (julianDay - refDay) / 7;
    }

    public static int getJulianMondayFromWeeksSinceEpoch(int week) {
        return (week * 7) + MONDAY_BEFORE_JULIAN_EPOCH;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class TimeCalculator {
        private ZoneInfoData mZoneInfoData;
        public String timezone;
        public final WallTime wallTime = new WallTime();

        public TimeCalculator(String timezoneId) {
            this.mZoneInfoData = lookupZoneInfoData(timezoneId);
        }

        public long toMillis(boolean ignoreDst) {
            if (ignoreDst) {
                this.wallTime.setIsDst(-1);
            }
            int r = this.wallTime.mktime(this.mZoneInfoData);
            if (r == -1) {
                return -1L;
            }
            return r * 1000;
        }

        public void setTimeInMillis(long millis) {
            int intSeconds = (int) (millis / 1000);
            updateZoneInfoFromTimeZone();
            this.wallTime.localtime(intSeconds, this.mZoneInfoData);
        }

        public String format(String format) {
            if (format == null) {
                format = "%c";
            }
            TimeFormatter formatter = new TimeFormatter();
            return formatter.format(format, this.wallTime, this.mZoneInfoData);
        }

        private void updateZoneInfoFromTimeZone() {
            if (!this.mZoneInfoData.getID().equals(this.timezone)) {
                this.mZoneInfoData = lookupZoneInfoData(this.timezone);
            }
        }

        private static ZoneInfoData lookupZoneInfoData(String timezoneId) {
            ZoneInfoData zoneInfoData = ZoneInfoDb.getInstance().makeZoneInfoData(timezoneId);
            if (zoneInfoData == null) {
                zoneInfoData = ZoneInfoDb.getInstance().makeZoneInfoData("GMT");
            }
            if (zoneInfoData == null) {
                throw new AssertionError("GMT not found: \"" + timezoneId + "\"");
            }
            return zoneInfoData;
        }

        public void switchTimeZone(String timezone) {
            int seconds = this.wallTime.mktime(this.mZoneInfoData);
            this.timezone = timezone;
            updateZoneInfoFromTimeZone();
            this.wallTime.localtime(seconds, this.mZoneInfoData);
        }

        public String format2445(boolean hasTime) {
            char[] buf = new char[hasTime ? 16 : 8];
            int n = this.wallTime.getYear();
            buf[0] = toChar(n / 1000);
            int n2 = n % 1000;
            buf[1] = toChar(n2 / 100);
            int n3 = n2 % 100;
            buf[2] = toChar(n3 / 10);
            buf[3] = toChar(n3 % 10);
            int n4 = this.wallTime.getMonth() + 1;
            buf[4] = toChar(n4 / 10);
            buf[5] = toChar(n4 % 10);
            int n5 = this.wallTime.getMonthDay();
            buf[6] = toChar(n5 / 10);
            buf[7] = toChar(n5 % 10);
            if (!hasTime) {
                return new String(buf, 0, 8);
            }
            buf[8] = 'T';
            int n6 = this.wallTime.getHour();
            buf[9] = toChar(n6 / 10);
            buf[10] = toChar(n6 % 10);
            int n7 = this.wallTime.getMinute();
            buf[11] = toChar(n7 / 10);
            buf[12] = toChar(n7 % 10);
            int n8 = this.wallTime.getSecond();
            buf[13] = toChar(n8 / 10);
            buf[14] = toChar(n8 % 10);
            if (Time.TIMEZONE_UTC.equals(this.timezone)) {
                buf[15] = 'Z';
                return new String(buf, 0, 16);
            }
            return new String(buf, 0, 15);
        }

        private char toChar(int n) {
            if (n < 0 || n > 9) {
                return ' ';
            }
            return (char) (n + 48);
        }

        public String toStringInternal() {
            return String.format("%04d%02d%02dT%02d%02d%02d%s(%d,%d,%d,%d,%d)", Integer.valueOf(this.wallTime.getYear()), Integer.valueOf(this.wallTime.getMonth() + 1), Integer.valueOf(this.wallTime.getMonthDay()), Integer.valueOf(this.wallTime.getHour()), Integer.valueOf(this.wallTime.getMinute()), Integer.valueOf(this.wallTime.getSecond()), this.timezone, Integer.valueOf(this.wallTime.getWeekDay()), Integer.valueOf(this.wallTime.getYearDay()), Integer.valueOf(this.wallTime.getGmtOffset()), Integer.valueOf(this.wallTime.getIsDst()), Long.valueOf(toMillis(false) / 1000));
        }

        public static int compare(TimeCalculator aObject, TimeCalculator bObject) {
            if (aObject.timezone.equals(bObject.timezone)) {
                int diff = aObject.wallTime.getYear() - bObject.wallTime.getYear();
                if (diff != 0) {
                    return diff;
                }
                int diff2 = aObject.wallTime.getMonth() - bObject.wallTime.getMonth();
                if (diff2 != 0) {
                    return diff2;
                }
                int diff3 = aObject.wallTime.getMonthDay() - bObject.wallTime.getMonthDay();
                if (diff3 != 0) {
                    return diff3;
                }
                int diff4 = aObject.wallTime.getHour() - bObject.wallTime.getHour();
                if (diff4 != 0) {
                    return diff4;
                }
                int diff5 = aObject.wallTime.getMinute() - bObject.wallTime.getMinute();
                if (diff5 != 0) {
                    return diff5;
                }
                int diff6 = aObject.wallTime.getSecond() - bObject.wallTime.getSecond();
                if (diff6 != 0) {
                    return diff6;
                }
                return 0;
            }
            long am = aObject.toMillis(false);
            long bm = bObject.toMillis(false);
            long diff7 = am - bm;
            if (diff7 < 0) {
                return -1;
            }
            return diff7 > 0 ? 1 : 0;
        }

        public void copyFieldsToTime(Time time) {
            time.second = this.wallTime.getSecond();
            time.minute = this.wallTime.getMinute();
            time.hour = this.wallTime.getHour();
            time.monthDay = this.wallTime.getMonthDay();
            time.month = this.wallTime.getMonth();
            time.year = this.wallTime.getYear();
            time.weekDay = this.wallTime.getWeekDay();
            time.yearDay = this.wallTime.getYearDay();
            time.isDst = this.wallTime.getIsDst();
            time.gmtoff = this.wallTime.getGmtOffset();
        }

        public void copyFieldsFromTime(Time time) {
            this.wallTime.setSecond(time.second);
            this.wallTime.setMinute(time.minute);
            this.wallTime.setHour(time.hour);
            this.wallTime.setMonthDay(time.monthDay);
            this.wallTime.setMonth(time.month);
            this.wallTime.setYear(time.year);
            this.wallTime.setWeekDay(time.weekDay);
            this.wallTime.setYearDay(time.yearDay);
            this.wallTime.setIsDst(time.isDst);
            this.wallTime.setGmtOffset((int) time.gmtoff);
            if (time.allDay && (time.second != 0 || time.minute != 0 || time.hour != 0)) {
                throw new IllegalArgumentException("allDay is true but sec, min, hour are not 0.");
            }
            this.timezone = time.timezone;
            updateZoneInfoFromTimeZone();
        }
    }
}
