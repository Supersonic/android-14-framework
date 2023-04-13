package android.text.format;

import android.content.Context;
import android.content.res.Resources;
import android.icu.text.DecimalFormat;
import android.icu.text.MeasureFormat;
import android.icu.text.NumberFormat;
import android.icu.text.UnicodeSet;
import android.icu.text.UnicodeSetSpanner;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.text.BidiFormatter;
import android.text.TextUtils;
import com.android.internal.C4057R;
import com.android.net.module.util.Inet4AddressUtils;
import java.util.Locale;
/* loaded from: classes3.dex */
public final class Formatter {
    public static final int FLAG_CALCULATE_ROUNDED = 2;
    public static final int FLAG_IEC_UNITS = 8;
    public static final int FLAG_SHORTER = 1;
    public static final int FLAG_SI_UNITS = 4;
    private static final int MILLIS_PER_MINUTE = 60000;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final UnicodeSetSpanner SPACES_AND_CONTROLS = new UnicodeSetSpanner(new UnicodeSet("[[:Zs:][:Cf:]]").freeze());

    /* loaded from: classes3.dex */
    public static class BytesResult {
        public final long roundedBytes;
        public final String units;
        public final String value;

        public BytesResult(String value, String units, long roundedBytes) {
            this.value = value;
            this.units = units;
            this.roundedBytes = roundedBytes;
        }
    }

    private static Locale localeFromContext(Context context) {
        return context.getResources().getConfiguration().getLocales().get(0);
    }

    private static String bidiWrap(Context context, String source) {
        Locale locale = localeFromContext(context);
        if (TextUtils.getLayoutDirectionFromLocale(locale) == 1) {
            return BidiFormatter.getInstance(true).unicodeWrap(source);
        }
        return source;
    }

    public static String formatFileSize(Context context, long sizeBytes) {
        return formatFileSize(context, sizeBytes, 4);
    }

    public static String formatFileSize(Context context, long sizeBytes, int flags) {
        if (context == null) {
            return "";
        }
        RoundedBytesResult res = RoundedBytesResult.roundBytes(sizeBytes, flags);
        return bidiWrap(context, formatRoundedBytesResult(context, res));
    }

    public static String formatShortFileSize(Context context, long sizeBytes) {
        return formatFileSize(context, sizeBytes, 5);
    }

    private static String getByteSuffixOverride(Resources res) {
        return res.getString(C4057R.string.byteShort);
    }

    private static NumberFormat getNumberFormatter(Locale locale, int fractionDigits) {
        NumberFormat numberFormatter = NumberFormat.getInstance(locale);
        numberFormatter.setMinimumFractionDigits(fractionDigits);
        numberFormatter.setMaximumFractionDigits(fractionDigits);
        numberFormatter.setGroupingUsed(false);
        if (numberFormatter instanceof DecimalFormat) {
            numberFormatter.setRoundingMode(4);
        }
        return numberFormatter;
    }

    private static String deleteFirstFromString(String source, String toDelete) {
        int location = source.indexOf(toDelete);
        if (location == -1) {
            return source;
        }
        return source.substring(0, location) + source.substring(toDelete.length() + location, source.length());
    }

    private static String formatMeasureShort(Locale locale, NumberFormat numberFormatter, float value, MeasureUnit units) {
        MeasureFormat measureFormatter = MeasureFormat.getInstance(locale, MeasureFormat.FormatWidth.SHORT, numberFormatter);
        return measureFormatter.format(new Measure(Float.valueOf(value), units));
    }

    private static String formatRoundedBytesResult(Context context, RoundedBytesResult input) {
        Locale locale = localeFromContext(context);
        NumberFormat numberFormatter = getNumberFormatter(locale, input.fractionDigits);
        if (input.units == MeasureUnit.BYTE) {
            String formattedNumber = numberFormatter.format(input.value);
            return context.getString(C4057R.string.fileSizeSuffix, formattedNumber, getByteSuffixOverride(context.getResources()));
        }
        return formatMeasureShort(locale, numberFormatter, input.value, input.units);
    }

    /* loaded from: classes3.dex */
    public static class RoundedBytesResult {
        public final int fractionDigits;
        public final long roundedBytes;
        public final MeasureUnit units;
        public final float value;

        private RoundedBytesResult(float value, MeasureUnit units, int fractionDigits, long roundedBytes) {
            this.value = value;
            this.units = units;
            this.fractionDigits = fractionDigits;
            this.roundedBytes = roundedBytes;
        }

        public static RoundedBytesResult roundBytes(long sizeBytes, int flags) {
            MeasureUnit units;
            long mult;
            int roundFactor;
            int roundDigits;
            long roundedBytes;
            int unit = (flags & 8) != 0 ? 1024 : 1000;
            boolean isNegative = sizeBytes < 0;
            float result = isNegative ? (float) (-sizeBytes) : (float) sizeBytes;
            MeasureUnit units2 = MeasureUnit.BYTE;
            long mult2 = 1;
            if (result > 900.0f) {
                units2 = MeasureUnit.KILOBYTE;
                mult2 = unit;
                result /= unit;
            }
            if (result > 900.0f) {
                units2 = MeasureUnit.MEGABYTE;
                mult2 *= unit;
                result /= unit;
            }
            if (result > 900.0f) {
                units2 = MeasureUnit.GIGABYTE;
                mult2 *= unit;
                result /= unit;
            }
            if (result > 900.0f) {
                units2 = MeasureUnit.TERABYTE;
                mult2 *= unit;
                result /= unit;
            }
            if (result <= 900.0f) {
                units = units2;
                mult = mult2;
            } else {
                MeasureUnit units3 = MeasureUnit.PETABYTE;
                result /= unit;
                units = units3;
                mult = mult2 * unit;
            }
            if (mult == 1 || result >= 100.0f) {
                roundFactor = 1;
                roundDigits = 0;
            } else if (result < 1.0f) {
                roundFactor = 100;
                roundDigits = 2;
            } else if (result < 10.0f) {
                if ((flags & 1) != 0) {
                    roundFactor = 10;
                    roundDigits = 1;
                } else {
                    roundFactor = 100;
                    roundDigits = 2;
                }
            } else {
                int roundFactor2 = flags & 1;
                if (roundFactor2 != 0) {
                    roundFactor = 1;
                    roundDigits = 0;
                } else {
                    roundFactor = 100;
                    roundDigits = 2;
                }
            }
            if (isNegative) {
                result = -result;
            }
            if ((flags & 2) == 0) {
                roundedBytes = 0;
            } else {
                roundedBytes = (Math.round(roundFactor * result) * mult) / roundFactor;
            }
            int roundFactor3 = roundDigits;
            return new RoundedBytesResult(result, units, roundFactor3, roundedBytes);
        }
    }

    public static BytesResult formatBytes(Resources res, long sizeBytes, int flags) {
        String formattedMeasure;
        RoundedBytesResult rounded = RoundedBytesResult.roundBytes(sizeBytes, flags);
        Locale locale = res.getConfiguration().getLocales().get(0);
        NumberFormat numberFormatter = getNumberFormatter(locale, rounded.fractionDigits);
        String formattedNumber = numberFormatter.format(rounded.value);
        if (rounded.units == MeasureUnit.BYTE) {
            formattedMeasure = getByteSuffixOverride(res);
        } else {
            String formattedMeasure2 = formatMeasureShort(locale, numberFormatter, rounded.value, rounded.units);
            String numberRemoved = deleteFirstFromString(formattedMeasure2, formattedNumber);
            formattedMeasure = SPACES_AND_CONTROLS.trim(numberRemoved).toString();
        }
        return new BytesResult(formattedNumber, formattedMeasure, rounded.roundedBytes);
    }

    @Deprecated
    public static String formatIpAddress(int ipv4Address) {
        return Inet4AddressUtils.intToInet4AddressHTL(ipv4Address).getHostAddress();
    }

    public static String formatShortElapsedTime(Context context, long millis) {
        long secondsLong = millis / 1000;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        if (secondsLong >= 86400) {
            days = (int) (secondsLong / 86400);
            secondsLong -= SECONDS_PER_DAY * days;
        }
        if (secondsLong >= 3600) {
            hours = (int) (secondsLong / 3600);
            secondsLong -= hours * 3600;
        }
        if (secondsLong >= 60) {
            minutes = (int) (secondsLong / 60);
            secondsLong -= minutes * 60;
        }
        int seconds = (int) secondsLong;
        Locale locale = localeFromContext(context);
        MeasureFormat measureFormat = MeasureFormat.getInstance(locale, MeasureFormat.FormatWidth.SHORT);
        if (days >= 2 || (days > 0 && hours == 0)) {
            return measureFormat.format(new Measure(Integer.valueOf(days + ((hours + 12) / 24)), MeasureUnit.DAY));
        }
        if (days > 0) {
            return measureFormat.formatMeasures(new Measure(Integer.valueOf(days), MeasureUnit.DAY), new Measure(Integer.valueOf(hours), MeasureUnit.HOUR));
        }
        if (hours >= 2 || (hours > 0 && minutes == 0)) {
            return measureFormat.format(new Measure(Integer.valueOf(hours + ((minutes + 30) / 60)), MeasureUnit.HOUR));
        }
        if (hours > 0) {
            return measureFormat.formatMeasures(new Measure(Integer.valueOf(hours), MeasureUnit.HOUR), new Measure(Integer.valueOf(minutes), MeasureUnit.MINUTE));
        }
        if (minutes >= 2 || (minutes > 0 && seconds == 0)) {
            return measureFormat.format(new Measure(Integer.valueOf(minutes + ((seconds + 30) / 60)), MeasureUnit.MINUTE));
        }
        if (minutes > 0) {
            return measureFormat.formatMeasures(new Measure(Integer.valueOf(minutes), MeasureUnit.MINUTE), new Measure(Integer.valueOf(seconds), MeasureUnit.SECOND));
        }
        return measureFormat.format(new Measure(Integer.valueOf(seconds), MeasureUnit.SECOND));
    }

    public static String formatShortElapsedTimeRoundingUpToMinutes(Context context, long millis) {
        long minutesRoundedUp = ((millis + 60000) - 1) / 60000;
        if (minutesRoundedUp == 0 || minutesRoundedUp == 1) {
            Locale locale = localeFromContext(context);
            MeasureFormat measureFormat = MeasureFormat.getInstance(locale, MeasureFormat.FormatWidth.SHORT);
            return measureFormat.format(new Measure(Long.valueOf(minutesRoundedUp), MeasureUnit.MINUTE));
        }
        return formatShortElapsedTime(context, 60000 * minutesRoundedUp);
    }
}
