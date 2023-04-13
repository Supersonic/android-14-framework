package com.ibm.icu.impl;

import com.android.server.backup.BackupManagerConstants;
import com.android.server.clipboard.ClipboardService;
/* loaded from: classes2.dex */
public class CalendarAstronomer {
    public transient double eclipObliquity;
    public long fGmtOffset;
    public double fLatitude;
    public double fLongitude;
    public transient double julianCentury;
    public transient double julianDay;
    public transient double meanAnomalySun;
    public transient double moonEclipLong;
    public transient double moonLongitude;
    public transient Equatorial moonPosition;
    public transient double siderealT0;
    public transient double siderealTime;
    public transient double sunLongitude;
    public long time;
    public static final SolarLongitude VERNAL_EQUINOX = new SolarLongitude(0.0d);
    public static final SolarLongitude SUMMER_SOLSTICE = new SolarLongitude(1.5707963267948966d);
    public static final SolarLongitude AUTUMN_EQUINOX = new SolarLongitude(3.141592653589793d);
    public static final SolarLongitude WINTER_SOLSTICE = new SolarLongitude(4.71238898038469d);
    public static final MoonAge NEW_MOON = new MoonAge(0.0d);
    public static final MoonAge FIRST_QUARTER = new MoonAge(1.5707963267948966d);
    public static final MoonAge FULL_MOON = new MoonAge(3.141592653589793d);
    public static final MoonAge LAST_QUARTER = new MoonAge(4.71238898038469d);

    /* loaded from: classes2.dex */
    public interface CoordFunc {
        Equatorial eval();
    }

    public CalendarAstronomer() {
        this(System.currentTimeMillis());
    }

    public CalendarAstronomer(long j) {
        this.fLongitude = 0.0d;
        this.fLatitude = 0.0d;
        this.fGmtOffset = 0L;
        this.julianDay = Double.MIN_VALUE;
        this.julianCentury = Double.MIN_VALUE;
        this.sunLongitude = Double.MIN_VALUE;
        this.meanAnomalySun = Double.MIN_VALUE;
        this.moonLongitude = Double.MIN_VALUE;
        this.moonEclipLong = Double.MIN_VALUE;
        this.eclipObliquity = Double.MIN_VALUE;
        this.siderealT0 = Double.MIN_VALUE;
        this.siderealTime = Double.MIN_VALUE;
        this.moonPosition = null;
        this.time = j;
    }

    public CalendarAstronomer(double d, double d2) {
        this();
        this.fLongitude = normPI(d * 0.017453292519943295d);
        this.fLatitude = normPI(d2 * 0.017453292519943295d);
        this.fGmtOffset = (long) (((this.fLongitude * 24.0d) * 3600000.0d) / 6.283185307179586d);
    }

    public void setTime(long j) {
        this.time = j;
        clearCache();
    }

    public double getJulianDay() {
        if (this.julianDay == Double.MIN_VALUE) {
            this.julianDay = (this.time - (-210866760000000L)) / 8.64E7d;
        }
        return this.julianDay;
    }

    public final double getSiderealOffset() {
        if (this.siderealT0 == Double.MIN_VALUE) {
            double floor = ((Math.floor(getJulianDay() - 0.5d) + 0.5d) - 2451545.0d) / 36525.0d;
            this.siderealT0 = normalize((2400.051336d * floor) + 6.697374558d + (2.5862E-5d * floor * floor), 24.0d);
        }
        return this.siderealT0;
    }

    public final long lstToUT(double d) {
        double normalize = normalize((d - getSiderealOffset()) * 0.9972695663d, 24.0d);
        long j = this.time;
        long j2 = this.fGmtOffset;
        return ((((j + j2) / BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) * BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) - j2) + ((long) (normalize * 3600000.0d));
    }

    public final Equatorial eclipticToEquatorial(double d, double d2) {
        double eclipticObliquity = eclipticObliquity();
        double sin = Math.sin(eclipticObliquity);
        double cos = Math.cos(eclipticObliquity);
        double sin2 = Math.sin(d);
        return new Equatorial(Math.atan2((sin2 * cos) - (Math.tan(d2) * sin), Math.cos(d)), Math.asin((Math.sin(d2) * cos) + (Math.cos(d2) * sin * sin2)));
    }

    public double getSunLongitude() {
        if (this.sunLongitude == Double.MIN_VALUE) {
            double[] sunLongitude = getSunLongitude(getJulianDay());
            this.sunLongitude = sunLongitude[0];
            this.meanAnomalySun = sunLongitude[1];
        }
        return this.sunLongitude;
    }

    public double[] getSunLongitude(double d) {
        double norm2PI = norm2PI((norm2PI((d - 2447891.5d) * 0.017202791632524146d) + 4.87650757829735d) - 4.935239984568769d);
        return new double[]{norm2PI(trueAnomaly(norm2PI, 0.016713d) + 4.935239984568769d), norm2PI};
    }

    public Equatorial getSunPosition() {
        return eclipticToEquatorial(getSunLongitude(), 0.0d);
    }

    /* loaded from: classes2.dex */
    public static class SolarLongitude {
        public double value;

        public SolarLongitude(double d) {
            this.value = d;
        }
    }

    public long getSunRiseSet(boolean z) {
        long j = this.time;
        long j2 = this.fGmtOffset;
        setTime(((((j + j2) / BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) * BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS) - j2) + 43200000 + ((z ? -6L : 6L) * ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS));
        long riseOrSet = riseOrSet(new CoordFunc() { // from class: com.ibm.icu.impl.CalendarAstronomer.2
            @Override // com.ibm.icu.impl.CalendarAstronomer.CoordFunc
            public Equatorial eval() {
                return CalendarAstronomer.this.getSunPosition();
            }
        }, z, 0.009302604913129777d, 0.009890199094634533d, 5000L);
        setTime(j);
        return riseOrSet;
    }

    /* loaded from: classes2.dex */
    public static class MoonAge {
        public double value;

        public MoonAge(double d) {
            this.value = d;
        }
    }

    public final long riseOrSet(CoordFunc coordFunc, boolean z, double d, double d2, long j) {
        Equatorial eval;
        long j2;
        double tan = Math.tan(this.fLatitude);
        int i = 0;
        do {
            eval = coordFunc.eval();
            double acos = Math.acos((-tan) * Math.tan(eval.declination));
            if (z) {
                acos = 6.283185307179586d - acos;
            }
            long lstToUT = lstToUT(((acos + eval.ascension) * 24.0d) / 6.283185307179586d);
            j2 = lstToUT - this.time;
            setTime(lstToUT);
            i++;
            if (i >= 5) {
                break;
            }
        } while (Math.abs(j2) > j);
        double cos = Math.cos(eval.declination);
        long asin = (long) ((((Math.asin(Math.sin((d / 2.0d) + d2) / Math.sin(Math.acos(Math.sin(this.fLatitude) / cos))) * 240.0d) * 57.29577951308232d) / cos) * 1000.0d);
        long j3 = this.time;
        if (z) {
            asin = -asin;
        }
        return j3 + asin;
    }

    public static final double normalize(double d, double d2) {
        return d - (d2 * Math.floor(d / d2));
    }

    public static final double norm2PI(double d) {
        return normalize(d, 6.283185307179586d);
    }

    public static final double normPI(double d) {
        return normalize(d + 3.141592653589793d, 6.283185307179586d) - 3.141592653589793d;
    }

    public final double trueAnomaly(double d, double d2) {
        double sin;
        double d3 = d;
        do {
            sin = (d3 - (Math.sin(d3) * d2)) - d;
            d3 -= sin / (1.0d - (Math.cos(d3) * d2));
        } while (Math.abs(sin) > 1.0E-5d);
        return Math.atan(Math.tan(d3 / 2.0d) * Math.sqrt((d2 + 1.0d) / (1.0d - d2))) * 2.0d;
    }

    public final double eclipticObliquity() {
        if (this.eclipObliquity == Double.MIN_VALUE) {
            double julianDay = (getJulianDay() - 2451545.0d) / 36525.0d;
            this.eclipObliquity = (((23.439292d - (0.013004166666666666d * julianDay)) - ((1.6666666666666665E-7d * julianDay) * julianDay)) + (5.027777777777778E-7d * julianDay * julianDay * julianDay)) * 0.017453292519943295d;
        }
        return this.eclipObliquity;
    }

    public final void clearCache() {
        this.julianDay = Double.MIN_VALUE;
        this.julianCentury = Double.MIN_VALUE;
        this.sunLongitude = Double.MIN_VALUE;
        this.meanAnomalySun = Double.MIN_VALUE;
        this.moonLongitude = Double.MIN_VALUE;
        this.moonEclipLong = Double.MIN_VALUE;
        this.eclipObliquity = Double.MIN_VALUE;
        this.siderealTime = Double.MIN_VALUE;
        this.siderealT0 = Double.MIN_VALUE;
        this.moonPosition = null;
    }

    /* loaded from: classes2.dex */
    public static final class Equatorial {
        public final double ascension;
        public final double declination;

        public Equatorial(double d, double d2) {
            this.ascension = d;
            this.declination = d2;
        }

        public String toString() {
            return Double.toString(this.ascension * 57.29577951308232d) + "," + (this.declination * 57.29577951308232d);
        }
    }
}
