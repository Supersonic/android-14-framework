package android.text.format;

import android.app.blob.XmlTags;
import android.app.compat.CompatChanges;
import android.content.Context;
import android.icu.text.DateFormatSymbols;
import android.icu.text.DateTimePatternGenerator;
import android.icu.util.ULocale;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import com.android.internal.content.NativeLibraryHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
/* loaded from: classes3.dex */
public class DateFormat {
    @Deprecated
    public static final char AM_PM = 'a';
    @Deprecated
    public static final char CAPITAL_AM_PM = 'A';
    @Deprecated
    public static final char DATE = 'd';
    @Deprecated
    public static final char DAY = 'E';
    static final long DISALLOW_DUPLICATE_FIELD_IN_SKELETON = 170233598;
    @Deprecated
    public static final char HOUR = 'h';
    @Deprecated
    public static final char HOUR_OF_DAY = 'k';
    @Deprecated
    public static final char MINUTE = 'm';
    @Deprecated
    public static final char MONTH = 'M';
    @Deprecated
    public static final char QUOTE = '\'';
    @Deprecated
    public static final char SECONDS = 's';
    @Deprecated
    public static final char STANDALONE_MONTH = 'L';
    @Deprecated
    public static final char TIME_ZONE = 'z';
    @Deprecated
    public static final char YEAR = 'y';
    private static boolean sIs24Hour;
    private static Locale sIs24HourLocale;
    private static final Object sLocaleLock = new Object();

    public static boolean is24HourFormat(Context context) {
        return is24HourFormat(context, context.getUserId());
    }

    public static boolean is24HourFormat(Context context, int userHandle) {
        String value = Settings.System.getStringForUser(context.getContentResolver(), Settings.System.TIME_12_24, userHandle);
        if (value != null) {
            return value.equals("24");
        }
        return is24HourLocale(context.getResources().getConfiguration().locale);
    }

    public static boolean is24HourLocale(Locale locale) {
        boolean is24Hour;
        Object obj = sLocaleLock;
        synchronized (obj) {
            Locale locale2 = sIs24HourLocale;
            if (locale2 != null && locale2.equals(locale)) {
                return sIs24Hour;
            }
            java.text.DateFormat natural = java.text.DateFormat.getTimeInstance(1, locale);
            if (natural instanceof SimpleDateFormat) {
                SimpleDateFormat sdf = (SimpleDateFormat) natural;
                String pattern = sdf.toPattern();
                is24Hour = hasDesignator(pattern, 'H');
            } else {
                is24Hour = false;
            }
            synchronized (obj) {
                sIs24HourLocale = locale;
                sIs24Hour = is24Hour;
            }
            return is24Hour;
        }
    }

    public static String getBestDateTimePattern(Locale locale, String skeleton) {
        ULocale uLocale = ULocale.forLocale(locale);
        DateTimePatternGenerator dtpg = DateTimePatternGenerator.getInstance(uLocale);
        boolean allowDuplicateFields = !CompatChanges.isChangeEnabled(DISALLOW_DUPLICATE_FIELD_IN_SKELETON);
        String pattern = dtpg.getBestPattern(skeleton, 0, allowDuplicateFields);
        return getCompatibleEnglishPattern(uLocale, pattern);
    }

    public static java.text.DateFormat getTimeFormat(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return new SimpleDateFormat(getTimeFormatString(context), locale);
    }

    public static String getTimeFormatString(Context context) {
        return getTimeFormatString(context, context.getUserId());
    }

    public static String getTimeFormatString(Context context, int userHandle) {
        ULocale uLocale = ULocale.forLocale(context.getResources().getConfiguration().locale);
        DateTimePatternGenerator dtpg = DateTimePatternGenerator.getInstance(uLocale);
        String pattern = is24HourFormat(context, userHandle) ? dtpg.getBestPattern("Hm") : dtpg.getBestPattern("hm");
        return getCompatibleEnglishPattern(uLocale, pattern);
    }

    public static java.text.DateFormat getDateFormat(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return java.text.DateFormat.getDateInstance(3, locale);
    }

    public static java.text.DateFormat getLongDateFormat(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return java.text.DateFormat.getDateInstance(1, locale);
    }

    public static java.text.DateFormat getMediumDateFormat(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return java.text.DateFormat.getDateInstance(2, locale);
    }

    public static char[] getDateFormatOrder(Context context) {
        return getDateFormatOrder(getDateFormatString(context));
    }

    public static char[] getDateFormatOrder(String pattern) {
        char[] result = new char[3];
        int resultIndex = 0;
        boolean sawDay = false;
        boolean sawMonth = false;
        boolean sawYear = false;
        int i = 0;
        while (i < pattern.length()) {
            char ch = pattern.charAt(i);
            if (ch == 'd' || ch == 'L' || ch == 'M' || ch == 'y') {
                if (ch == 'd' && !sawDay) {
                    result[resultIndex] = DATE;
                    sawDay = true;
                    resultIndex++;
                } else if ((ch == 'L' || ch == 'M') && !sawMonth) {
                    result[resultIndex] = MONTH;
                    sawMonth = true;
                    resultIndex++;
                } else if (ch == 'y' && !sawYear) {
                    result[resultIndex] = 'y';
                    sawYear = true;
                    resultIndex++;
                }
            } else if (ch == 'G') {
                continue;
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                throw new IllegalArgumentException("Bad pattern character '" + ch + "' in " + pattern);
            } else {
                if (ch != '\'') {
                    continue;
                } else if (i >= pattern.length() - 1 || pattern.charAt(i + 1) != '\'') {
                    int i2 = pattern.indexOf(39, i + 1);
                    if (i2 == -1) {
                        throw new IllegalArgumentException("Bad quoting in " + pattern);
                    }
                    i = i2 + 1;
                } else {
                    i++;
                }
            }
            i++;
        }
        return result;
    }

    private static String getDateFormatString(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        java.text.DateFormat df = java.text.DateFormat.getDateInstance(3, locale);
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) df).toPattern();
        }
        throw new AssertionError("!(df instanceof SimpleDateFormat)");
    }

    public static CharSequence format(CharSequence inFormat, long inTimeInMillis) {
        return format(inFormat, new Date(inTimeInMillis));
    }

    public static CharSequence format(CharSequence inFormat, Date inDate) {
        Calendar c = new GregorianCalendar();
        c.setTime(inDate);
        return format(inFormat, c);
    }

    public static boolean hasSeconds(CharSequence inFormat) {
        return hasDesignator(inFormat, 's');
    }

    public static boolean hasDesignator(CharSequence inFormat, char designator) {
        if (inFormat == null) {
            return false;
        }
        int length = inFormat.length();
        boolean insideQuote = false;
        for (int i = 0; i < length; i++) {
            char c = inFormat.charAt(i);
            if (c == '\'') {
                insideQuote = insideQuote ? false : true;
            } else if (!insideQuote && c == designator) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence format(CharSequence inFormat, Calendar inDate) {
        int count;
        String replacement;
        SpannableStringBuilder s = new SpannableStringBuilder(inFormat);
        DateFormatSymbols dfs = getIcuDateFormatSymbols(Locale.getDefault());
        String[] amPm = dfs.getAmPmStrings();
        int len = inFormat.length();
        for (int i = 0; i < len; i += count) {
            count = 1;
            int c = s.charAt(i);
            if (c == 39) {
                count = appendQuotedText(s, i);
                len = s.length();
            } else {
                while (i + count < len && s.charAt(i + count) == c) {
                    count++;
                }
                switch (c) {
                    case 65:
                    case 97:
                        replacement = amPm[inDate.get(9) + 0];
                        break;
                    case 69:
                    case 99:
                        replacement = getDayOfWeekString(dfs, inDate.get(7), count, c);
                        break;
                    case 72:
                    case 107:
                        replacement = zeroPad(inDate.get(11), count);
                        break;
                    case 75:
                    case 104:
                        int hour = inDate.get(10);
                        if (c == 104 && hour == 0) {
                            hour = 12;
                        }
                        replacement = zeroPad(hour, count);
                        break;
                    case 76:
                    case 77:
                        replacement = getMonthString(dfs, inDate.get(2), count, c);
                        break;
                    case 100:
                        replacement = zeroPad(inDate.get(5), count);
                        break;
                    case 109:
                        replacement = zeroPad(inDate.get(12), count);
                        break;
                    case 115:
                        replacement = zeroPad(inDate.get(13), count);
                        break;
                    case 121:
                        replacement = getYearString(inDate.get(1), count);
                        break;
                    case 122:
                        replacement = getTimeZoneString(inDate, count);
                        break;
                    default:
                        replacement = null;
                        break;
                }
                if (replacement != null) {
                    s.replace(i, i + count, (CharSequence) replacement);
                    count = replacement.length();
                    len = s.length();
                }
            }
        }
        if (inFormat instanceof Spanned) {
            return new SpannedString(s);
        }
        return s.toString();
    }

    private static String getDayOfWeekString(DateFormatSymbols dfs, int day, int count, int kind) {
        int width;
        boolean standalone = kind == 99;
        int context = standalone ? 1 : 0;
        if (count == 5) {
            width = 2;
        } else if (count == 4) {
            width = 1;
        } else {
            width = 0;
        }
        return dfs.getWeekdays(context, width)[day];
    }

    private static String getMonthString(DateFormatSymbols dfs, int month, int count, int kind) {
        boolean standalone = kind == 76;
        int monthContext = standalone ? 1 : 0;
        if (count == 5) {
            return dfs.getMonths(monthContext, 2)[month];
        }
        if (count == 4) {
            return dfs.getMonths(monthContext, 1)[month];
        }
        if (count == 3) {
            return dfs.getMonths(monthContext, 0)[month];
        }
        return zeroPad(month + 1, count);
    }

    private static String getTimeZoneString(Calendar inDate, int count) {
        TimeZone tz = inDate.getTimeZone();
        if (count < 2) {
            return formatZoneOffset(inDate.get(16) + inDate.get(15), count);
        }
        boolean dst = inDate.get(16) != 0;
        return tz.getDisplayName(dst, 0);
    }

    private static String formatZoneOffset(int offset, int count) {
        int offset2 = offset / 1000;
        StringBuilder tb = new StringBuilder();
        if (offset2 < 0) {
            tb.insert(0, NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            offset2 = -offset2;
        } else {
            tb.insert(0, "+");
        }
        int hours = offset2 / 3600;
        int minutes = (offset2 % 3600) / 60;
        tb.append(zeroPad(hours, 2));
        tb.append(zeroPad(minutes, 2));
        return tb.toString();
    }

    private static String getYearString(int year, int count) {
        if (count <= 2) {
            return zeroPad(year % 100, 2);
        }
        return String.format(Locale.getDefault(), "%d", Integer.valueOf(year));
    }

    public static int appendQuotedText(SpannableStringBuilder formatString, int index) {
        int length = formatString.length();
        if (index + 1 < length && formatString.charAt(index + 1) == '\'') {
            formatString.delete(index, index + 1);
            return 1;
        }
        int count = 0;
        formatString.delete(index, index + 1);
        int length2 = length - 1;
        while (index < length2) {
            char c = formatString.charAt(index);
            if (c == '\'') {
                if (index + 1 < length2 && formatString.charAt(index + 1) == '\'') {
                    formatString.delete(index, index + 1);
                    length2--;
                    count++;
                    index++;
                } else {
                    formatString.delete(index, index + 1);
                    break;
                }
            } else {
                index++;
                count++;
            }
        }
        return count;
    }

    private static String zeroPad(int inValue, int inMinDigits) {
        return String.format(Locale.getDefault(), "%0" + inMinDigits + XmlTags.ATTR_DESCRIPTION, Integer.valueOf(inValue));
    }

    public static DateFormatSymbols getIcuDateFormatSymbols(Locale locale) {
        return new DateFormatSymbols(android.icu.util.GregorianCalendar.class, locale);
    }

    private static String getCompatibleEnglishPattern(ULocale locale, String pattern) {
        if (pattern == null || locale == null || !"en".equals(locale.getLanguage())) {
            return pattern;
        }
        String region = locale.getCountry();
        if (region != null && !region.isEmpty() && !"US".equals(region)) {
            return pattern;
        }
        return pattern.replace((char) 8239, ' ');
    }
}
