package android.text.format;

import android.app.blob.XmlTags;
import android.hardware.gnss.GnssSignalType;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
import android.icu.util.ULocale;
/* loaded from: classes3.dex */
public final class DateUtilsBridge {
    public static TimeZone icuTimeZone(java.util.TimeZone tz) {
        TimeZone icuTimeZone = TimeZone.getTimeZone(tz.getID());
        icuTimeZone.freeze();
        return icuTimeZone;
    }

    public static Calendar createIcuCalendar(TimeZone icuTimeZone, ULocale icuLocale, long timeInMillis) {
        Calendar calendar = new GregorianCalendar(icuTimeZone, icuLocale);
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }

    public static String toSkeleton(Calendar calendar, int flags) {
        return toSkeleton(calendar, calendar, flags);
    }

    public static String toSkeleton(Calendar startCalendar, Calendar endCalendar, int flags) {
        if ((524288 & flags) != 0) {
            flags |= 114688;
        }
        String monthPart = "MMMM";
        if ((131072 & flags) != 0) {
            monthPart = GnssSignalType.CODE_TYPE_M;
        } else if ((65536 & flags) != 0) {
            monthPart = "MMM";
        }
        String weekPart = "EEEE";
        if ((32768 & flags) != 0) {
            weekPart = "EEE";
        }
        String timePart = "j";
        if ((flags & 128) != 0) {
            timePart = "H";
        } else if ((flags & 64) != 0) {
            timePart = "h";
        }
        if ((flags & 16384) == 0 || (flags & 128) != 0) {
            timePart = timePart + "m";
        } else if (!onTheHour(startCalendar) || !onTheHour(endCalendar)) {
            timePart = timePart + "m";
        }
        if (fallOnDifferentDates(startCalendar, endCalendar)) {
            flags |= 16;
        }
        if (fallInSameMonth(startCalendar, endCalendar) && (flags & 32) != 0) {
            flags = flags & (-3) & (-2);
        }
        if ((flags & 19) == 0) {
            flags |= 16;
        }
        if ((flags & 16) != 0 && (flags & 4) == 0 && (flags & 8) == 0 && (!fallInSameYear(startCalendar, endCalendar) || !isThisYear(startCalendar))) {
            flags |= 4;
        }
        StringBuilder builder = new StringBuilder();
        if ((flags & 48) != 0) {
            if ((flags & 4) != 0) {
                builder.append("y");
            }
            builder.append(monthPart);
            if ((flags & 32) == 0) {
                builder.append(XmlTags.ATTR_DESCRIPTION);
            }
        }
        if ((flags & 2) != 0) {
            builder.append(weekPart);
        }
        if ((flags & 1) != 0) {
            builder.append(timePart);
        }
        return builder.toString();
    }

    public static int dayDistance(Calendar c1, Calendar c2) {
        return c2.get(20) - c1.get(20);
    }

    public static boolean isDisplayMidnightUsingSkeleton(Calendar c) {
        return c.get(11) == 0 && c.get(12) == 0;
    }

    private static boolean onTheHour(Calendar c) {
        return c.get(12) == 0 && c.get(13) == 0;
    }

    private static boolean fallOnDifferentDates(Calendar c1, Calendar c2) {
        return (c1.get(1) == c2.get(1) && c1.get(2) == c2.get(2) && c1.get(5) == c2.get(5)) ? false : true;
    }

    private static boolean fallInSameMonth(Calendar c1, Calendar c2) {
        return c1.get(2) == c2.get(2);
    }

    private static boolean fallInSameYear(Calendar c1, Calendar c2) {
        return c1.get(1) == c2.get(1);
    }

    private static boolean isThisYear(Calendar c) {
        Calendar now = (Calendar) c.clone();
        now.setTimeInMillis(System.currentTimeMillis());
        return c.get(1) == now.get(1);
    }
}
