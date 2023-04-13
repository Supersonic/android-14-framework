package com.android.internal.telephony;

import android.os.SystemClock;
import com.android.i18n.timezone.CountryTimeZones;
import com.android.i18n.timezone.TimeZoneFinder;
import com.android.i18n.timezone.ZoneInfoDb;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
/* loaded from: classes.dex */
public class TimeUtils {
    public static final int HUNDRED_DAY_FIELD_LEN = 19;
    public static final long NANOS_PER_MS = 1000000;
    private static final SimpleDateFormat sLoggingFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat sDumpDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static final Instant MIN_USE_DATE_OF_TIMEZONE = Instant.ofEpochMilli(1546300800000L);
    private static final Object sFormatSync = new Object();
    private static char[] sFormatStr = new char[29];
    private static char[] sTmpFormatStr = new char[29];

    public static TimeZone getTimeZone(int i, boolean z, long j, String str) {
        android.icu.util.TimeZone icuTimeZone = getIcuTimeZone(i, z, j, str);
        if (icuTimeZone != null) {
            return TimeZone.getTimeZone(icuTimeZone.getID());
        }
        return null;
    }

    private static android.icu.util.TimeZone getIcuTimeZone(int i, boolean z, long j, String str) {
        CountryTimeZones.OffsetResult lookupByOffsetWithBias;
        if (str == null) {
            return null;
        }
        android.icu.util.TimeZone timeZone = android.icu.util.TimeZone.getDefault();
        CountryTimeZones lookupCountryTimeZones = TimeZoneFinder.getInstance().lookupCountryTimeZones(str);
        if (lookupCountryTimeZones == null || (lookupByOffsetWithBias = lookupCountryTimeZones.lookupByOffsetWithBias(j, timeZone, i, z)) == null) {
            return null;
        }
        return lookupByOffsetWithBias.getTimeZone();
    }

    public static List<String> getTimeZoneIdsForCountryCode(String str) {
        if (str == null) {
            throw new NullPointerException("countryCode == null");
        }
        CountryTimeZones lookupCountryTimeZones = TimeZoneFinder.getInstance().lookupCountryTimeZones(str.toLowerCase());
        if (lookupCountryTimeZones == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (CountryTimeZones.TimeZoneMapping timeZoneMapping : lookupCountryTimeZones.getTimeZoneMappings()) {
            if (timeZoneMapping.isShownInPickerAt(MIN_USE_DATE_OF_TIMEZONE)) {
                arrayList.add(timeZoneMapping.getTimeZoneId());
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    public static String getTimeZoneDatabaseVersion() {
        return ZoneInfoDb.getInstance().getVersion();
    }

    private static int accumField(int i, int i2, boolean z, int i3) {
        int i4 = 0;
        if (i > 999) {
            while (i != 0) {
                i4++;
                i /= 10;
            }
            return i4 + i2;
        } else if (i > 99 || (z && i3 >= 3)) {
            return i2 + 3;
        } else {
            if (i > 9 || (z && i3 >= 2)) {
                return i2 + 2;
            }
            if (z || i > 0) {
                return i2 + 1;
            }
            return 0;
        }
    }

    private static int printFieldLocked(char[] cArr, int i, char c, int i2, boolean z, int i3) {
        int i4;
        if (z || i > 0) {
            if (i > 999) {
                int i5 = 0;
                while (i != 0) {
                    char[] cArr2 = sTmpFormatStr;
                    if (i5 >= cArr2.length) {
                        break;
                    }
                    cArr2[i5] = (char) ((i % 10) + 48);
                    i5++;
                    i /= 10;
                }
                while (true) {
                    i5--;
                    if (i5 < 0) {
                        break;
                    }
                    cArr[i2] = sTmpFormatStr[i5];
                    i2++;
                }
            } else {
                if ((!z || i3 < 3) && i <= 99) {
                    i4 = i2;
                } else {
                    int i6 = i / 100;
                    cArr[i2] = (char) (i6 + 48);
                    i4 = i2 + 1;
                    i -= i6 * 100;
                }
                if ((z && i3 >= 2) || i > 9 || i2 != i4) {
                    int i7 = i / 10;
                    cArr[i4] = (char) (i7 + 48);
                    i4++;
                    i -= i7 * 10;
                }
                cArr[i4] = (char) (i + 48);
                i2 = i4 + 1;
            }
            cArr[i2] = c;
            return i2 + 1;
        }
        return i2;
    }

    private static int formatDurationLocked(long j, int i) {
        char c;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        long j2 = j;
        if (sFormatStr.length < i) {
            sFormatStr = new char[i];
        }
        char[] cArr = sFormatStr;
        int i7 = (j2 > 0L ? 1 : (j2 == 0L ? 0 : -1));
        int i8 = 0;
        if (i7 == 0) {
            int i9 = i - 1;
            while (i8 < i9) {
                cArr[i8] = ' ';
                i8++;
            }
            cArr[i8] = '0';
            return i8 + 1;
        }
        if (i7 > 0) {
            c = '+';
        } else {
            j2 = -j2;
            c = '-';
        }
        int i10 = (int) (j2 % 1000);
        int floor = (int) Math.floor(j2 / 1000);
        if (floor >= 86400) {
            i2 = floor / 86400;
            floor -= 86400 * i2;
        } else {
            i2 = 0;
        }
        if (floor >= 3600) {
            i3 = floor / 3600;
            floor -= i3 * 3600;
        } else {
            i3 = 0;
        }
        if (floor >= 60) {
            int i11 = floor / 60;
            i4 = floor - (i11 * 60);
            i5 = i11;
        } else {
            i4 = floor;
            i5 = 0;
        }
        if (i != 0) {
            int accumField = accumField(i2, 1, false, 0);
            int accumField2 = accumField + accumField(i3, 1, accumField > 0, 2);
            int accumField3 = accumField2 + accumField(i5, 1, accumField2 > 0, 2);
            int accumField4 = accumField3 + accumField(i4, 1, accumField3 > 0, 2);
            i6 = 0;
            for (int accumField5 = accumField4 + accumField(i10, 2, true, accumField4 > 0 ? 3 : 0) + 1; accumField5 < i; accumField5++) {
                cArr[i6] = ' ';
                i6++;
            }
        } else {
            i6 = 0;
        }
        cArr[i6] = c;
        int i12 = i6 + 1;
        boolean z = i != 0;
        int printFieldLocked = printFieldLocked(cArr, i2, 'd', i12, false, 0);
        int printFieldLocked2 = printFieldLocked(cArr, i3, 'h', printFieldLocked, printFieldLocked != i12, z ? 2 : 0);
        int printFieldLocked3 = printFieldLocked(cArr, i5, 'm', printFieldLocked2, printFieldLocked2 != i12, z ? 2 : 0);
        int printFieldLocked4 = printFieldLocked(cArr, i4, 's', printFieldLocked3, printFieldLocked3 != i12, z ? 2 : 0);
        int printFieldLocked5 = printFieldLocked(cArr, i10, 'm', printFieldLocked4, true, (!z || printFieldLocked4 == i12) ? 0 : 3);
        cArr[printFieldLocked5] = 's';
        return printFieldLocked5 + 1;
    }

    public static void formatDuration(long j, StringBuilder sb) {
        synchronized (sFormatSync) {
            sb.append(sFormatStr, 0, formatDurationLocked(j, 0));
        }
    }

    public static void formatDuration(long j, StringBuilder sb, int i) {
        synchronized (sFormatSync) {
            sb.append(sFormatStr, 0, formatDurationLocked(j, i));
        }
    }

    public static void formatDuration(long j, PrintWriter printWriter, int i) {
        synchronized (sFormatSync) {
            printWriter.print(new String(sFormatStr, 0, formatDurationLocked(j, i)));
        }
    }

    public static String formatDuration(long j) {
        String str;
        synchronized (sFormatSync) {
            str = new String(sFormatStr, 0, formatDurationLocked(j, 0));
        }
        return str;
    }

    public static void formatDuration(long j, PrintWriter printWriter) {
        formatDuration(j, printWriter, 0);
    }

    public static void formatDuration(long j, long j2, PrintWriter printWriter) {
        if (j == 0) {
            printWriter.print("--");
        } else {
            formatDuration(j - j2, printWriter, 0);
        }
    }

    public static String formatUptime(long j) {
        return formatTime(j, SystemClock.uptimeMillis());
    }

    public static String formatRealtime(long j) {
        return formatTime(j, SystemClock.elapsedRealtime());
    }

    public static String formatTime(long j, long j2) {
        long j3 = j - j2;
        int i = (j3 > 0L ? 1 : (j3 == 0L ? 0 : -1));
        if (i > 0) {
            return j + " (in " + j3 + " ms)";
        } else if (i < 0) {
            return j + " (" + (-j3) + " ms ago)";
        } else {
            return j + " (now)";
        }
    }

    public static String logTimeOfDay(long j) {
        Calendar calendar = Calendar.getInstance();
        if (j >= 0) {
            calendar.setTimeInMillis(j);
            return String.format("%tm-%td %tH:%tM:%tS.%tL", calendar, calendar, calendar, calendar, calendar, calendar);
        }
        return Long.toString(j);
    }

    public static String formatForLogging(long j) {
        return j <= 0 ? "unknown" : sLoggingFormat.format(new Date(j));
    }

    public static void dumpTime(PrintWriter printWriter, long j) {
        printWriter.print(sDumpDateFormat.format(new Date(j)));
    }

    public static boolean isTimeBetween(LocalTime localTime, LocalTime localTime2, LocalTime localTime3) {
        if (localTime.isBefore(localTime2) && localTime.isAfter(localTime3)) {
            return false;
        }
        if (localTime.isBefore(localTime3) && localTime.isBefore(localTime2) && localTime2.isBefore(localTime3)) {
            return false;
        }
        return (localTime.isAfter(localTime3) && localTime.isAfter(localTime2) && localTime2.isBefore(localTime3)) ? false : true;
    }

    public static void dumpTimeWithDelta(PrintWriter printWriter, long j, long j2) {
        printWriter.print(sDumpDateFormat.format(new Date(j)));
        if (j == j2) {
            printWriter.print(" (now)");
            return;
        }
        printWriter.print(" (");
        formatDuration(j, j2, printWriter);
        printWriter.print(")");
    }
}
