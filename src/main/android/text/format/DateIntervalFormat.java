package android.text.format;

import android.icu.util.Calendar;
import android.icu.util.ULocale;
import android.util.LruCache;
import java.text.FieldPosition;
import java.util.TimeZone;
/* loaded from: classes3.dex */
public final class DateIntervalFormat {
    private static final LruCache<String, android.icu.text.DateIntervalFormat> CACHED_FORMATTERS = new LruCache<>(8);

    private DateIntervalFormat() {
    }

    public static String formatDateRange(long startMs, long endMs, int flags, String olsonId) {
        if ((flags & 8192) != 0) {
            olsonId = Time.TIMEZONE_UTC;
        }
        TimeZone tz = olsonId != null ? TimeZone.getTimeZone(olsonId) : TimeZone.getDefault();
        android.icu.util.TimeZone icuTimeZone = DateUtilsBridge.icuTimeZone(tz);
        ULocale icuLocale = ULocale.getDefault();
        return formatDateRange(icuLocale, icuTimeZone, startMs, endMs, flags);
    }

    public static String formatDateRange(ULocale icuLocale, android.icu.util.TimeZone icuTimeZone, long startMs, long endMs, int flags) {
        Calendar endCalendar;
        String stringBuffer;
        Calendar startCalendar = DateUtilsBridge.createIcuCalendar(icuTimeZone, icuLocale, startMs);
        if (startMs == endMs) {
            endCalendar = startCalendar;
        } else {
            endCalendar = DateUtilsBridge.createIcuCalendar(icuTimeZone, icuLocale, endMs);
        }
        if (isExactlyMidnight(endCalendar)) {
            boolean showTime = (flags & 1) == 1;
            boolean endsDayAfterStart = DateUtilsBridge.dayDistance(startCalendar, endCalendar) == 1;
            if ((!showTime && startMs != endMs) || (endsDayAfterStart && !DateUtilsBridge.isDisplayMidnightUsingSkeleton(startCalendar))) {
                endCalendar.add(5, -1);
            }
        }
        String skeleton = DateUtilsBridge.toSkeleton(startCalendar, endCalendar, flags);
        synchronized (CACHED_FORMATTERS) {
            android.icu.text.DateIntervalFormat formatter = getFormatter(skeleton, icuLocale, icuTimeZone);
            stringBuffer = formatter.format(startCalendar, endCalendar, new StringBuffer(), new FieldPosition(0)).toString();
        }
        return stringBuffer;
    }

    private static android.icu.text.DateIntervalFormat getFormatter(String skeleton, ULocale locale, android.icu.util.TimeZone icuTimeZone) {
        String key = skeleton + "\t" + locale + "\t" + icuTimeZone;
        LruCache<String, android.icu.text.DateIntervalFormat> lruCache = CACHED_FORMATTERS;
        android.icu.text.DateIntervalFormat formatter = lruCache.get(key);
        if (formatter != null) {
            return formatter;
        }
        android.icu.text.DateIntervalFormat formatter2 = android.icu.text.DateIntervalFormat.getInstance(skeleton, locale);
        formatter2.setTimeZone(icuTimeZone);
        lruCache.put(key, formatter2);
        return formatter2;
    }

    private static boolean isExactlyMidnight(Calendar c) {
        return c.get(11) == 0 && c.get(12) == 0 && c.get(13) == 0 && c.get(14) == 0;
    }
}
