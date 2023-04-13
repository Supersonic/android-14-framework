package android.text.format;

import android.icu.text.DisplayContext;
import android.icu.text.RelativeDateTimeFormatter;
import android.icu.util.Calendar;
import android.icu.util.ULocale;
import android.util.LruCache;
import java.util.Locale;
import java.util.TimeZone;
/* loaded from: classes3.dex */
public final class RelativeDateTimeFormatter {
    private static final FormatterCache CACHED_FORMATTERS = new FormatterCache();
    public static final long DAY_IN_MILLIS = 86400000;
    private static final int DAY_IN_MS = 86400000;
    private static final int EPOCH_JULIAN_DAY = 2440588;
    public static final long HOUR_IN_MILLIS = 3600000;
    public static final long MINUTE_IN_MILLIS = 60000;
    public static final long SECOND_IN_MILLIS = 1000;
    public static final long WEEK_IN_MILLIS = 604800000;
    public static final long YEAR_IN_MILLIS = 31449600000L;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static class FormatterCache extends LruCache<String, android.icu.text.RelativeDateTimeFormatter> {
        FormatterCache() {
            super(8);
        }
    }

    private RelativeDateTimeFormatter() {
    }

    public static String getRelativeTimeSpanString(Locale locale, TimeZone tz, long time, long now, long minResolution, int flags) {
        DisplayContext displayContext = DisplayContext.CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE;
        return getRelativeTimeSpanString(locale, tz, time, now, minResolution, flags, displayContext);
    }

    public static String getRelativeTimeSpanString(Locale locale, TimeZone tz, long time, long now, long minResolution, int flags, DisplayContext displayContext) {
        if (locale == null) {
            throw new NullPointerException("locale == null");
        }
        if (tz == null) {
            throw new NullPointerException("tz == null");
        }
        ULocale icuLocale = ULocale.forLocale(locale);
        android.icu.util.TimeZone icuTimeZone = DateUtilsBridge.icuTimeZone(tz);
        return getRelativeTimeSpanString(icuLocale, icuTimeZone, time, now, minResolution, flags, displayContext);
    }

    private static String getRelativeTimeSpanString(ULocale icuLocale, android.icu.util.TimeZone icuTimeZone, long time, long now, long minResolution, int flags, DisplayContext displayContext) {
        RelativeDateTimeFormatter.Style style;
        RelativeDateTimeFormatter.Direction direction;
        int flags2;
        int count;
        RelativeDateTimeFormatter.RelativeUnit unit;
        RelativeDateTimeFormatter.AbsoluteUnit aunit;
        RelativeDateTimeFormatter.Direction direction2;
        boolean relative;
        RelativeDateTimeFormatter.RelativeUnit unit2;
        String str;
        long duration = Math.abs(now - time);
        boolean past = now >= time;
        if ((flags & 786432) != 0) {
            style = RelativeDateTimeFormatter.Style.SHORT;
        } else {
            RelativeDateTimeFormatter.Style style2 = RelativeDateTimeFormatter.Style.LONG;
            style = style2;
        }
        if (past) {
            direction = RelativeDateTimeFormatter.Direction.LAST;
        } else {
            RelativeDateTimeFormatter.Direction direction3 = RelativeDateTimeFormatter.Direction.NEXT;
            direction = direction3;
        }
        if (duration >= 60000 || minResolution >= 60000) {
            RelativeDateTimeFormatter.Direction direction4 = direction;
            if (duration < 3600000 && minResolution < 3600000) {
                int count2 = (int) (duration / 60000);
                count = count2;
                unit = RelativeDateTimeFormatter.RelativeUnit.MINUTES;
                aunit = null;
                direction2 = direction4;
                relative = true;
            } else if (duration < 86400000 && minResolution < 86400000) {
                int count3 = (int) (duration / 3600000);
                count = count3;
                unit = RelativeDateTimeFormatter.RelativeUnit.HOURS;
                aunit = null;
                direction2 = direction4;
                relative = true;
            } else if (duration < 604800000 && minResolution < 604800000) {
                count = Math.abs(dayDistance(icuTimeZone, time, now));
                RelativeDateTimeFormatter.RelativeUnit unit3 = RelativeDateTimeFormatter.RelativeUnit.DAYS;
                if (count == 2) {
                    if (past) {
                        synchronized (CACHED_FORMATTERS) {
                            try {
                                try {
                                } catch (Throwable th) {
                                    th = th;
                                }
                                try {
                                    unit2 = unit3;
                                    str = getFormatter(icuLocale, style, displayContext).format(RelativeDateTimeFormatter.Direction.LAST_2, RelativeDateTimeFormatter.AbsoluteUnit.DAY);
                                } catch (Throwable th2) {
                                    th = th2;
                                    throw th;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                            }
                        }
                    } else {
                        unit2 = unit3;
                        synchronized (CACHED_FORMATTERS) {
                            str = getFormatter(icuLocale, style, displayContext).format(RelativeDateTimeFormatter.Direction.NEXT_2, RelativeDateTimeFormatter.AbsoluteUnit.DAY);
                        }
                    }
                    if (str != null && !str.isEmpty()) {
                        return str;
                    }
                } else {
                    unit2 = unit3;
                    if (count == 1) {
                        RelativeDateTimeFormatter.AbsoluteUnit aunit2 = RelativeDateTimeFormatter.AbsoluteUnit.DAY;
                        aunit = aunit2;
                        direction2 = direction4;
                        relative = false;
                        unit = unit2;
                    } else if (count == 0) {
                        RelativeDateTimeFormatter.AbsoluteUnit aunit3 = RelativeDateTimeFormatter.AbsoluteUnit.DAY;
                        RelativeDateTimeFormatter.Direction direction5 = RelativeDateTimeFormatter.Direction.THIS;
                        direction2 = direction5;
                        aunit = aunit3;
                        relative = false;
                        unit = unit2;
                    }
                }
                aunit = null;
                unit = unit2;
                direction2 = direction4;
                relative = true;
            } else if (minResolution != 604800000) {
                Calendar timeCalendar = DateUtilsBridge.createIcuCalendar(icuTimeZone, icuLocale, time);
                if ((flags & 12) != 0) {
                    flags2 = flags;
                } else {
                    Calendar nowCalendar = DateUtilsBridge.createIcuCalendar(icuTimeZone, icuLocale, now);
                    if (timeCalendar.get(1) != nowCalendar.get(1)) {
                        flags2 = flags | 4;
                    } else {
                        flags2 = flags | 8;
                    }
                }
                return DateTimeFormat.format(icuLocale, timeCalendar, flags2, displayContext);
            } else {
                int count4 = (int) (duration / 604800000);
                count = count4;
                unit = RelativeDateTimeFormatter.RelativeUnit.WEEKS;
                aunit = null;
                direction2 = direction4;
                relative = true;
            }
        } else {
            RelativeDateTimeFormatter.Direction direction6 = direction;
            int count5 = (int) (duration / 1000);
            count = count5;
            unit = RelativeDateTimeFormatter.RelativeUnit.SECONDS;
            aunit = null;
            direction2 = direction6;
            relative = true;
        }
        synchronized (CACHED_FORMATTERS) {
            try {
                try {
                    android.icu.text.RelativeDateTimeFormatter formatter = getFormatter(icuLocale, style, displayContext);
                    if (relative) {
                        return formatter.format(count, direction2, unit);
                    }
                    return formatter.format(direction2, aunit);
                } catch (Throwable th4) {
                    th = th4;
                    throw th;
                }
            } catch (Throwable th5) {
                th = th5;
            }
        }
    }

    public static String getRelativeDateTimeString(Locale locale, TimeZone tz, long time, long now, long minResolution, long transitionResolution, int flags) {
        long transitionResolution2;
        RelativeDateTimeFormatter.Style style;
        int i;
        Calendar timeCalendar;
        RelativeDateTimeFormatter.Style style2;
        int flags2;
        String dateClause;
        String combineDateAndTime;
        long minResolution2;
        if (locale == null) {
            throw new NullPointerException("locale == null");
        }
        if (tz == null) {
            throw new NullPointerException("tz == null");
        }
        ULocale icuLocale = ULocale.forLocale(locale);
        android.icu.util.TimeZone icuTimeZone = DateUtilsBridge.icuTimeZone(tz);
        long duration = Math.abs(now - time);
        if (transitionResolution <= 604800000) {
            transitionResolution2 = transitionResolution;
        } else {
            transitionResolution2 = 604800000;
        }
        if ((flags & 786432) != 0) {
            style = RelativeDateTimeFormatter.Style.SHORT;
        } else {
            RelativeDateTimeFormatter.Style style3 = RelativeDateTimeFormatter.Style.LONG;
            style = style3;
        }
        Calendar timeCalendar2 = DateUtilsBridge.createIcuCalendar(icuTimeZone, icuLocale, time);
        Calendar nowCalendar = DateUtilsBridge.createIcuCalendar(icuTimeZone, icuLocale, now);
        int days = Math.abs(DateUtilsBridge.dayDistance(timeCalendar2, nowCalendar));
        if (duration < transitionResolution2) {
            if (days > 0 && minResolution < 86400000) {
                minResolution2 = 86400000;
            } else {
                minResolution2 = minResolution;
            }
            i = 1;
            timeCalendar = timeCalendar2;
            style2 = style;
            dateClause = getRelativeTimeSpanString(icuLocale, icuTimeZone, time, now, minResolution2, flags, DisplayContext.CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE);
        } else {
            i = 1;
            timeCalendar = timeCalendar2;
            style2 = style;
            if (timeCalendar.get(1) != nowCalendar.get(1)) {
                flags2 = 131092;
            } else {
                flags2 = 65560;
            }
            dateClause = DateTimeFormat.format(icuLocale, timeCalendar, flags2, DisplayContext.CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE);
        }
        String timeClause = DateTimeFormat.format(icuLocale, timeCalendar, i, DisplayContext.CAPITALIZATION_NONE);
        DisplayContext capitalizationContext = DisplayContext.CAPITALIZATION_NONE;
        synchronized (CACHED_FORMATTERS) {
            combineDateAndTime = getFormatter(icuLocale, style2, capitalizationContext).combineDateAndTime(dateClause, timeClause);
        }
        return combineDateAndTime;
    }

    private static android.icu.text.RelativeDateTimeFormatter getFormatter(ULocale locale, RelativeDateTimeFormatter.Style style, DisplayContext displayContext) {
        String key = locale + "\t" + style + "\t" + displayContext;
        FormatterCache formatterCache = CACHED_FORMATTERS;
        android.icu.text.RelativeDateTimeFormatter formatter = formatterCache.get(key);
        if (formatter == null) {
            android.icu.text.RelativeDateTimeFormatter formatter2 = android.icu.text.RelativeDateTimeFormatter.getInstance(locale, null, style, displayContext);
            formatterCache.put(key, formatter2);
            return formatter2;
        }
        return formatter;
    }

    private static int dayDistance(android.icu.util.TimeZone icuTimeZone, long startTime, long endTime) {
        return julianDay(icuTimeZone, endTime) - julianDay(icuTimeZone, startTime);
    }

    private static int julianDay(android.icu.util.TimeZone icuTimeZone, long time) {
        long utcMs = icuTimeZone.getOffset(time) + time;
        return ((int) (utcMs / 86400000)) + 2440588;
    }
}
