package android.location;

import android.annotation.SystemApi;
import android.content.p001pm.PackageManager;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
import android.util.Printer;
import android.util.TimeUtils;
import com.android.internal.accessibility.common.ShortcutConstants;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public class Location implements Parcelable {
    @SystemApi
    @Deprecated
    public static final String EXTRA_NO_GPS_LOCATION = "noGPSLocation";
    public static final int FORMAT_DEGREES = 0;
    public static final int FORMAT_MINUTES = 1;
    public static final int FORMAT_SECONDS = 2;
    private static final int HAS_ALTITUDE_ACCURACY_MASK = 32;
    private static final int HAS_ALTITUDE_MASK = 1;
    private static final int HAS_BEARING_ACCURACY_MASK = 128;
    private static final int HAS_BEARING_MASK = 4;
    private static final int HAS_ELAPSED_REALTIME_UNCERTAINTY_MASK = 256;
    private static final int HAS_HORIZONTAL_ACCURACY_MASK = 8;
    private static final int HAS_MOCK_PROVIDER_MASK = 16;
    private static final int HAS_MSL_ALTITUDE_ACCURACY_MASK = 1024;
    private static final int HAS_MSL_ALTITUDE_MASK = 512;
    private static final int HAS_SPEED_ACCURACY_MASK = 64;
    private static final int HAS_SPEED_MASK = 2;
    private float mAltitudeAccuracyMeters;
    private double mAltitudeMeters;
    private float mBearingAccuracyDegrees;
    private float mBearingDegrees;
    private long mElapsedRealtimeNs;
    private double mElapsedRealtimeUncertaintyNs;
    private float mHorizontalAccuracyMeters;
    private double mLatitudeDegrees;
    private double mLongitudeDegrees;
    private float mMslAltitudeAccuracyMeters;
    private double mMslAltitudeMeters;
    private String mProvider;
    private float mSpeedAccuracyMetersPerSecond;
    private float mSpeedMetersPerSecond;
    private long mTimeMs;
    private static final ThreadLocal<BearingDistanceCache> sBearingDistanceCache = ThreadLocal.withInitial(new Supplier() { // from class: android.location.Location$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            return Location.$r8$lambda$AN3VJCcYjECjC7u5pDkntGBLoAg();
        }
    });
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() { // from class: android.location.Location.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Location createFromParcel(Parcel in) {
            Location l = new Location(in.readString8());
            l.mFieldsMask = in.readInt();
            l.mTimeMs = in.readLong();
            l.mElapsedRealtimeNs = in.readLong();
            if (l.hasElapsedRealtimeUncertaintyNanos()) {
                l.mElapsedRealtimeUncertaintyNs = in.readDouble();
            }
            l.mLatitudeDegrees = in.readDouble();
            l.mLongitudeDegrees = in.readDouble();
            if (l.hasAltitude()) {
                l.mAltitudeMeters = in.readDouble();
            }
            if (l.hasSpeed()) {
                l.mSpeedMetersPerSecond = in.readFloat();
            }
            if (l.hasBearing()) {
                l.mBearingDegrees = in.readFloat();
            }
            if (l.hasAccuracy()) {
                l.mHorizontalAccuracyMeters = in.readFloat();
            }
            if (l.hasVerticalAccuracy()) {
                l.mAltitudeAccuracyMeters = in.readFloat();
            }
            if (l.hasSpeedAccuracy()) {
                l.mSpeedAccuracyMetersPerSecond = in.readFloat();
            }
            if (l.hasBearingAccuracy()) {
                l.mBearingAccuracyDegrees = in.readFloat();
            }
            if (l.hasMslAltitude()) {
                l.mMslAltitudeMeters = in.readDouble();
            }
            if (l.hasMslAltitudeAccuracy()) {
                l.mMslAltitudeAccuracyMeters = in.readFloat();
            }
            l.mExtras = Bundle.setDefusable(in.readBundle(), true);
            return l;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
    private int mFieldsMask = 0;
    private Bundle mExtras = null;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Format {
    }

    public static /* synthetic */ BearingDistanceCache $r8$lambda$AN3VJCcYjECjC7u5pDkntGBLoAg() {
        return new BearingDistanceCache();
    }

    public Location(String provider) {
        this.mProvider = provider;
    }

    public Location(Location location) {
        set(location);
    }

    public void set(Location location) {
        this.mFieldsMask = location.mFieldsMask;
        this.mProvider = location.mProvider;
        this.mTimeMs = location.mTimeMs;
        this.mElapsedRealtimeNs = location.mElapsedRealtimeNs;
        this.mElapsedRealtimeUncertaintyNs = location.mElapsedRealtimeUncertaintyNs;
        this.mLatitudeDegrees = location.mLatitudeDegrees;
        this.mLongitudeDegrees = location.mLongitudeDegrees;
        this.mHorizontalAccuracyMeters = location.mHorizontalAccuracyMeters;
        this.mAltitudeMeters = location.mAltitudeMeters;
        this.mAltitudeAccuracyMeters = location.mAltitudeAccuracyMeters;
        this.mSpeedMetersPerSecond = location.mSpeedMetersPerSecond;
        this.mSpeedAccuracyMetersPerSecond = location.mSpeedAccuracyMetersPerSecond;
        this.mBearingDegrees = location.mBearingDegrees;
        this.mBearingAccuracyDegrees = location.mBearingAccuracyDegrees;
        this.mMslAltitudeMeters = location.mMslAltitudeMeters;
        this.mMslAltitudeAccuracyMeters = location.mMslAltitudeAccuracyMeters;
        this.mExtras = location.mExtras == null ? null : new Bundle(location.mExtras);
    }

    public void reset() {
        this.mProvider = null;
        this.mTimeMs = 0L;
        this.mElapsedRealtimeNs = 0L;
        this.mElapsedRealtimeUncertaintyNs = 0.0d;
        this.mFieldsMask = 0;
        this.mLatitudeDegrees = 0.0d;
        this.mLongitudeDegrees = 0.0d;
        this.mAltitudeMeters = 0.0d;
        this.mSpeedMetersPerSecond = 0.0f;
        this.mBearingDegrees = 0.0f;
        this.mHorizontalAccuracyMeters = 0.0f;
        this.mAltitudeAccuracyMeters = 0.0f;
        this.mSpeedAccuracyMetersPerSecond = 0.0f;
        this.mBearingAccuracyDegrees = 0.0f;
        this.mMslAltitudeMeters = 0.0d;
        this.mMslAltitudeAccuracyMeters = 0.0f;
        this.mExtras = null;
    }

    public float distanceTo(Location dest) {
        BearingDistanceCache cache = sBearingDistanceCache.get();
        if (this.mLatitudeDegrees != cache.mLat1 || this.mLongitudeDegrees != cache.mLon1 || dest.mLatitudeDegrees != cache.mLat2 || dest.mLongitudeDegrees != cache.mLon2) {
            computeDistanceAndBearing(this.mLatitudeDegrees, this.mLongitudeDegrees, dest.mLatitudeDegrees, dest.mLongitudeDegrees, cache);
        }
        return cache.mDistance;
    }

    public float bearingTo(Location dest) {
        BearingDistanceCache cache = sBearingDistanceCache.get();
        if (this.mLatitudeDegrees != cache.mLat1 || this.mLongitudeDegrees != cache.mLon1 || dest.mLatitudeDegrees != cache.mLat2 || dest.mLongitudeDegrees != cache.mLon2) {
            computeDistanceAndBearing(this.mLatitudeDegrees, this.mLongitudeDegrees, dest.mLatitudeDegrees, dest.mLongitudeDegrees, cache);
        }
        return cache.mInitialBearing;
    }

    public String getProvider() {
        return this.mProvider;
    }

    public void setProvider(String provider) {
        this.mProvider = provider;
    }

    public long getTime() {
        return this.mTimeMs;
    }

    public void setTime(long timeMs) {
        this.mTimeMs = timeMs;
    }

    public long getElapsedRealtimeNanos() {
        return this.mElapsedRealtimeNs;
    }

    public long getElapsedRealtimeMillis() {
        return TimeUnit.NANOSECONDS.toMillis(this.mElapsedRealtimeNs);
    }

    public long getElapsedRealtimeAgeMillis() {
        return getElapsedRealtimeAgeMillis(SystemClock.elapsedRealtime());
    }

    public long getElapsedRealtimeAgeMillis(long referenceRealtimeMs) {
        return referenceRealtimeMs - getElapsedRealtimeMillis();
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNs) {
        this.mElapsedRealtimeNs = elapsedRealtimeNs;
    }

    public double getElapsedRealtimeUncertaintyNanos() {
        return this.mElapsedRealtimeUncertaintyNs;
    }

    public void setElapsedRealtimeUncertaintyNanos(double elapsedRealtimeUncertaintyNs) {
        this.mElapsedRealtimeUncertaintyNs = elapsedRealtimeUncertaintyNs;
        this.mFieldsMask |= 256;
    }

    public boolean hasElapsedRealtimeUncertaintyNanos() {
        return (this.mFieldsMask & 256) != 0;
    }

    public void removeElapsedRealtimeUncertaintyNanos() {
        this.mFieldsMask &= -257;
    }

    public double getLatitude() {
        return this.mLatitudeDegrees;
    }

    public void setLatitude(double latitudeDegrees) {
        this.mLatitudeDegrees = latitudeDegrees;
    }

    public double getLongitude() {
        return this.mLongitudeDegrees;
    }

    public void setLongitude(double longitudeDegrees) {
        this.mLongitudeDegrees = longitudeDegrees;
    }

    public float getAccuracy() {
        return this.mHorizontalAccuracyMeters;
    }

    public void setAccuracy(float horizontalAccuracyMeters) {
        this.mHorizontalAccuracyMeters = horizontalAccuracyMeters;
        this.mFieldsMask |= 8;
    }

    public boolean hasAccuracy() {
        return (this.mFieldsMask & 8) != 0;
    }

    public void removeAccuracy() {
        this.mFieldsMask &= -9;
    }

    public double getAltitude() {
        return this.mAltitudeMeters;
    }

    public void setAltitude(double altitudeMeters) {
        this.mAltitudeMeters = altitudeMeters;
        this.mFieldsMask |= 1;
    }

    public boolean hasAltitude() {
        return (this.mFieldsMask & 1) != 0;
    }

    public void removeAltitude() {
        this.mFieldsMask &= -2;
    }

    public float getVerticalAccuracyMeters() {
        return this.mAltitudeAccuracyMeters;
    }

    public void setVerticalAccuracyMeters(float altitudeAccuracyMeters) {
        this.mAltitudeAccuracyMeters = altitudeAccuracyMeters;
        this.mFieldsMask |= 32;
    }

    public boolean hasVerticalAccuracy() {
        return (this.mFieldsMask & 32) != 0;
    }

    public void removeVerticalAccuracy() {
        this.mFieldsMask &= -33;
    }

    public float getSpeed() {
        return this.mSpeedMetersPerSecond;
    }

    public void setSpeed(float speedMetersPerSecond) {
        this.mSpeedMetersPerSecond = speedMetersPerSecond;
        this.mFieldsMask |= 2;
    }

    public boolean hasSpeed() {
        return (this.mFieldsMask & 2) != 0;
    }

    public void removeSpeed() {
        this.mFieldsMask &= -3;
    }

    public float getSpeedAccuracyMetersPerSecond() {
        return this.mSpeedAccuracyMetersPerSecond;
    }

    public void setSpeedAccuracyMetersPerSecond(float speedAccuracyMeterPerSecond) {
        this.mSpeedAccuracyMetersPerSecond = speedAccuracyMeterPerSecond;
        this.mFieldsMask |= 64;
    }

    public boolean hasSpeedAccuracy() {
        return (this.mFieldsMask & 64) != 0;
    }

    public void removeSpeedAccuracy() {
        this.mFieldsMask &= -65;
    }

    public float getBearing() {
        return this.mBearingDegrees;
    }

    public void setBearing(float bearingDegrees) {
        Preconditions.checkArgument(Float.isFinite(bearingDegrees));
        float modBearing = (bearingDegrees % 360.0f) + 0.0f;
        if (modBearing < 0.0f) {
            modBearing += 360.0f;
        }
        this.mBearingDegrees = modBearing;
        this.mFieldsMask |= 4;
    }

    public boolean hasBearing() {
        return (this.mFieldsMask & 4) != 0;
    }

    public void removeBearing() {
        this.mFieldsMask &= -5;
    }

    public float getBearingAccuracyDegrees() {
        return this.mBearingAccuracyDegrees;
    }

    public void setBearingAccuracyDegrees(float bearingAccuracyDegrees) {
        this.mBearingAccuracyDegrees = bearingAccuracyDegrees;
        this.mFieldsMask |= 128;
    }

    public boolean hasBearingAccuracy() {
        return (this.mFieldsMask & 128) != 0;
    }

    public void removeBearingAccuracy() {
        this.mFieldsMask &= PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
    }

    public double getMslAltitudeMeters() {
        Preconditions.checkState(hasMslAltitude(), "The Mean Sea Level altitude of this location is not set.");
        return this.mMslAltitudeMeters;
    }

    public void setMslAltitudeMeters(double mslAltitudeMeters) {
        this.mMslAltitudeMeters = mslAltitudeMeters;
        this.mFieldsMask |= 512;
    }

    public boolean hasMslAltitude() {
        return (this.mFieldsMask & 512) != 0;
    }

    public void removeMslAltitude() {
        this.mFieldsMask &= -513;
    }

    public float getMslAltitudeAccuracyMeters() {
        Preconditions.checkState(hasMslAltitudeAccuracy(), "The Mean Sea Level altitude accuracy of this location is not set.");
        return this.mMslAltitudeAccuracyMeters;
    }

    public void setMslAltitudeAccuracyMeters(float mslAltitudeAccuracyMeters) {
        this.mMslAltitudeAccuracyMeters = mslAltitudeAccuracyMeters;
        this.mFieldsMask |= 1024;
    }

    public boolean hasMslAltitudeAccuracy() {
        return (this.mFieldsMask & 1024) != 0;
    }

    public void removeMslAltitudeAccuracy() {
        this.mFieldsMask &= -1025;
    }

    @Deprecated
    public boolean isFromMockProvider() {
        return isMock();
    }

    @SystemApi
    @Deprecated
    public void setIsFromMockProvider(boolean isFromMockProvider) {
        setMock(isFromMockProvider);
    }

    public boolean isMock() {
        return (this.mFieldsMask & 16) != 0;
    }

    public void setMock(boolean mock) {
        if (mock) {
            this.mFieldsMask |= 16;
        } else {
            this.mFieldsMask &= -17;
        }
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public void setExtras(Bundle extras) {
        this.mExtras = extras == null ? null : new Bundle(extras);
    }

    public boolean isComplete() {
        return (this.mProvider == null || !hasAccuracy() || this.mTimeMs == 0 || this.mElapsedRealtimeNs == 0) ? false : true;
    }

    @SystemApi
    public void makeComplete() {
        if (this.mProvider == null) {
            this.mProvider = "";
        }
        if (!hasAccuracy()) {
            this.mFieldsMask |= 8;
            this.mHorizontalAccuracyMeters = 100.0f;
        }
        if (this.mTimeMs == 0) {
            this.mTimeMs = System.currentTimeMillis();
        }
        if (this.mElapsedRealtimeNs == 0) {
            this.mElapsedRealtimeNs = SystemClock.elapsedRealtimeNanos();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Location) {
            Location location = (Location) o;
            return this.mTimeMs == location.mTimeMs && this.mElapsedRealtimeNs == location.mElapsedRealtimeNs && hasElapsedRealtimeUncertaintyNanos() == location.hasElapsedRealtimeUncertaintyNanos() && (!hasElapsedRealtimeUncertaintyNanos() || Double.compare(location.mElapsedRealtimeUncertaintyNs, this.mElapsedRealtimeUncertaintyNs) == 0) && Double.compare(location.mLatitudeDegrees, this.mLatitudeDegrees) == 0 && Double.compare(location.mLongitudeDegrees, this.mLongitudeDegrees) == 0 && hasAltitude() == location.hasAltitude() && ((!hasAltitude() || Double.compare(location.mAltitudeMeters, this.mAltitudeMeters) == 0) && hasSpeed() == location.hasSpeed() && ((!hasSpeed() || Float.compare(location.mSpeedMetersPerSecond, this.mSpeedMetersPerSecond) == 0) && hasBearing() == location.hasBearing() && ((!hasBearing() || Float.compare(location.mBearingDegrees, this.mBearingDegrees) == 0) && hasAccuracy() == location.hasAccuracy() && ((!hasAccuracy() || Float.compare(location.mHorizontalAccuracyMeters, this.mHorizontalAccuracyMeters) == 0) && hasVerticalAccuracy() == location.hasVerticalAccuracy() && ((!hasVerticalAccuracy() || Float.compare(location.mAltitudeAccuracyMeters, this.mAltitudeAccuracyMeters) == 0) && hasSpeedAccuracy() == location.hasSpeedAccuracy() && ((!hasSpeedAccuracy() || Float.compare(location.mSpeedAccuracyMetersPerSecond, this.mSpeedAccuracyMetersPerSecond) == 0) && hasBearingAccuracy() == location.hasBearingAccuracy() && ((!hasBearingAccuracy() || Float.compare(location.mBearingAccuracyDegrees, this.mBearingAccuracyDegrees) == 0) && hasMslAltitude() == location.hasMslAltitude() && ((!hasMslAltitude() || Double.compare(location.mMslAltitudeMeters, this.mMslAltitudeMeters) == 0) && hasMslAltitudeAccuracy() == location.hasMslAltitudeAccuracy() && ((!hasMslAltitudeAccuracy() || Float.compare(location.mMslAltitudeAccuracyMeters, this.mMslAltitudeAccuracyMeters) == 0) && Objects.equals(this.mProvider, location.mProvider) && areExtrasEqual(this.mExtras, location.mExtras))))))))));
        }
        return false;
    }

    private static boolean areExtrasEqual(Bundle extras1, Bundle extras2) {
        if ((extras1 == null || extras1.isEmpty()) && (extras2 == null || extras2.isEmpty())) {
            return true;
        }
        if (extras1 == null || extras2 == null) {
            return false;
        }
        return extras1.kindofEquals(extras2);
    }

    public int hashCode() {
        return Objects.hash(this.mProvider, Long.valueOf(this.mElapsedRealtimeNs), Double.valueOf(this.mLatitudeDegrees), Double.valueOf(this.mLongitudeDegrees));
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Location[");
        s.append(this.mProvider);
        s.append(" ").append(String.format(Locale.ROOT, "%.6f,%.6f", Double.valueOf(this.mLatitudeDegrees), Double.valueOf(this.mLongitudeDegrees)));
        if (hasAccuracy()) {
            s.append(" hAcc=").append(this.mHorizontalAccuracyMeters);
        }
        s.append(" et=");
        TimeUtils.formatDuration(getElapsedRealtimeMillis(), s);
        if (hasAltitude()) {
            s.append(" alt=").append(this.mAltitudeMeters);
            if (hasVerticalAccuracy()) {
                s.append(" vAcc=").append(this.mAltitudeAccuracyMeters);
            }
        }
        if (hasMslAltitude()) {
            s.append(" mslAlt=").append(this.mMslAltitudeMeters);
            if (hasMslAltitudeAccuracy()) {
                s.append(" mslAltAcc=").append(this.mMslAltitudeAccuracyMeters);
            }
        }
        if (hasSpeed()) {
            s.append(" vel=").append(this.mSpeedMetersPerSecond);
            if (hasSpeedAccuracy()) {
                s.append(" sAcc=").append(this.mSpeedAccuracyMetersPerSecond);
            }
        }
        if (hasBearing()) {
            s.append(" bear=").append(this.mBearingDegrees);
            if (hasBearingAccuracy()) {
                s.append(" bAcc=").append(this.mBearingAccuracyDegrees);
            }
        }
        if (isMock()) {
            s.append(" mock");
        }
        Bundle bundle = this.mExtras;
        if (bundle != null && !bundle.isEmpty()) {
            s.append(" {").append(this.mExtras).append('}');
        }
        s.append(']');
        return s.toString();
    }

    @Deprecated
    public void dump(Printer pw, String prefix) {
        pw.println(prefix + this);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString8(this.mProvider);
        parcel.writeInt(this.mFieldsMask);
        parcel.writeLong(this.mTimeMs);
        parcel.writeLong(this.mElapsedRealtimeNs);
        if (hasElapsedRealtimeUncertaintyNanos()) {
            parcel.writeDouble(this.mElapsedRealtimeUncertaintyNs);
        }
        parcel.writeDouble(this.mLatitudeDegrees);
        parcel.writeDouble(this.mLongitudeDegrees);
        if (hasAltitude()) {
            parcel.writeDouble(this.mAltitudeMeters);
        }
        if (hasSpeed()) {
            parcel.writeFloat(this.mSpeedMetersPerSecond);
        }
        if (hasBearing()) {
            parcel.writeFloat(this.mBearingDegrees);
        }
        if (hasAccuracy()) {
            parcel.writeFloat(this.mHorizontalAccuracyMeters);
        }
        if (hasVerticalAccuracy()) {
            parcel.writeFloat(this.mAltitudeAccuracyMeters);
        }
        if (hasSpeedAccuracy()) {
            parcel.writeFloat(this.mSpeedAccuracyMetersPerSecond);
        }
        if (hasBearingAccuracy()) {
            parcel.writeFloat(this.mBearingAccuracyDegrees);
        }
        if (hasMslAltitude()) {
            parcel.writeDouble(this.mMslAltitudeMeters);
        }
        if (hasMslAltitudeAccuracy()) {
            parcel.writeFloat(this.mMslAltitudeAccuracyMeters);
        }
        parcel.writeBundle(this.mExtras);
    }

    public static String convert(double coordinate, int outputType) {
        Preconditions.checkArgumentInRange(coordinate, -180.0d, 180.0d, "coordinate");
        Preconditions.checkArgument(outputType == 0 || outputType == 1 || outputType == 2, "%d is an unrecognized format", Integer.valueOf(outputType));
        StringBuilder sb = new StringBuilder();
        if (coordinate < 0.0d) {
            sb.append('-');
            coordinate = -coordinate;
        }
        DecimalFormat df = new DecimalFormat("###.#####");
        if (outputType == 1 || outputType == 2) {
            int degrees = (int) Math.floor(coordinate);
            sb.append(degrees);
            sb.append(ShortcutConstants.SERVICES_SEPARATOR);
            coordinate = (coordinate - degrees) * 60.0d;
            if (outputType == 2) {
                int minutes = (int) Math.floor(coordinate);
                sb.append(minutes);
                sb.append(ShortcutConstants.SERVICES_SEPARATOR);
                coordinate = (coordinate - minutes) * 60.0d;
            }
        }
        sb.append(df.format(coordinate));
        return sb.toString();
    }

    public static double convert(String coordinate) {
        boolean negative;
        String coordinate2;
        double min;
        Objects.requireNonNull(coordinate);
        if (coordinate.charAt(0) != '-') {
            negative = false;
            coordinate2 = coordinate;
        } else {
            negative = true;
            coordinate2 = coordinate.substring(1);
        }
        StringTokenizer st = new StringTokenizer(coordinate2, ":");
        int tokens = st.countTokens();
        if (tokens < 1) {
            throw new IllegalArgumentException("coordinate=" + coordinate2);
        }
        try {
            String degrees = st.nextToken();
            try {
                if (tokens == 1) {
                    double val = Double.parseDouble(degrees);
                    return negative ? -val : val;
                }
                String minutes = st.nextToken();
                int deg = Integer.parseInt(degrees);
                double sec = 0.0d;
                boolean secPresent = false;
                if (st.hasMoreTokens()) {
                    min = Integer.parseInt(minutes);
                    String seconds = st.nextToken();
                    sec = Double.parseDouble(seconds);
                    secPresent = true;
                } else {
                    min = Double.parseDouble(minutes);
                }
                boolean isNegative180 = negative && deg == 180 && min == 0.0d && sec == 0.0d;
                try {
                    if (deg >= 0.0d && (deg <= 179 || isNegative180)) {
                        if (min >= 0.0d && min < 60.0d) {
                            if (!secPresent || min <= 59.0d) {
                                if (sec < 0.0d || sec >= 60.0d) {
                                    throw new IllegalArgumentException("coordinate=" + coordinate2);
                                }
                                double val2 = (((deg * 3600.0d) + (60.0d * min)) + sec) / 3600.0d;
                                return negative ? -val2 : val2;
                            }
                        }
                        throw new IllegalArgumentException("coordinate=" + coordinate2);
                    }
                    throw new IllegalArgumentException("coordinate=" + coordinate2);
                } catch (NumberFormatException e) {
                    e = e;
                    throw new IllegalArgumentException("coordinate=" + coordinate2, e);
                }
            } catch (NumberFormatException e2) {
                e = e2;
            }
        } catch (NumberFormatException e3) {
            e = e3;
        }
    }

    private static void computeDistanceAndBearing(double lat1, double lon1, double lat2, double lon2, BearingDistanceCache results) {
        double lat22;
        double lon12;
        double sinAlpha;
        double lat12 = lat1 * 0.017453292519943295d;
        double cosSigma = lat2 * 0.017453292519943295d;
        double sinSigma = lon1 * 0.017453292519943295d;
        double lon22 = 0.017453292519943295d * lon2;
        double f = (6378137.0d - 6356752.3142d) / 6378137.0d;
        double aSqMinusBSqOverBSq = ((6378137.0d * 6378137.0d) - (6356752.3142d * 6356752.3142d)) / (6356752.3142d * 6356752.3142d);
        double l = lon22 - sinSigma;
        double aA = 0.0d;
        double u1 = Math.atan((1.0d - f) * Math.tan(lat12));
        double u2 = Math.atan((1.0d - f) * Math.tan(cosSigma));
        double cosU1 = Math.cos(u1);
        double cosU2 = Math.cos(u2);
        double sinU1 = Math.sin(u1);
        double sinU2 = Math.sin(u2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;
        double sigma = 0.0d;
        double deltaSigma = 0.0d;
        double cosLambda = 0.0d;
        double sinLambda = 0.0d;
        double lambda = l;
        int iter = 0;
        while (true) {
            if (iter >= 20) {
                lat22 = cosSigma;
                lon12 = sinSigma;
                break;
            }
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = (cosU1 * sinU2) - ((sinU1 * cosU2) * cosLambda);
            double sinSqSigma = (t1 * t1) + (t2 * t2);
            lon12 = sinSigma;
            double sinSigma2 = Math.sqrt(sinSqSigma);
            lat22 = cosSigma;
            double lat23 = sinU1sinU2 + (cosU1cosU2 * cosLambda);
            sigma = Math.atan2(sinSigma2, lat23);
            if (sinSigma2 == 0.0d) {
                sinAlpha = 0.0d;
            } else {
                sinAlpha = (cosU1cosU2 * sinLambda) / sinSigma2;
            }
            double cosSqAlpha = 1.0d - (sinAlpha * sinAlpha);
            double cos2SM = cosSqAlpha != 0.0d ? lat23 - ((sinU1sinU2 * 2.0d) / cosSqAlpha) : 0.0d;
            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq;
            aA = ((uSquared / 16384.0d) * (((((320.0d - (175.0d * uSquared)) * uSquared) - 768.0d) * uSquared) + 4096.0d)) + 1.0d;
            double bB = (uSquared / 1024.0d) * (((((74.0d - (47.0d * uSquared)) * uSquared) - 128.0d) * uSquared) + 256.0d);
            double cC = (f / 16.0d) * cosSqAlpha * (((4.0d - (3.0d * cosSqAlpha)) * f) + 4.0d);
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = bB * sinSigma2 * (cos2SM + ((bB / 4.0d) * ((((cos2SMSq * 2.0d) - 1.0d) * lat23) - ((((bB / 6.0d) * cos2SM) * (((sinSigma2 * 4.0d) * sinSigma2) - 3.0d)) * ((4.0d * cos2SMSq) - 3.0d)))));
            lambda = l + ((1.0d - cC) * f * sinAlpha * (sigma + (cC * sinSigma2 * (cos2SM + (cC * lat23 * (((2.0d * cos2SM) * cos2SM) - 1.0d))))));
            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0E-12d) {
                break;
            }
            iter++;
            sinSigma = lon12;
            cosSigma = lat22;
        }
        results.mDistance = (float) (6356752.3142d * aA * (sigma - deltaSigma));
        float initialBearing = (float) Math.atan2(cosU2 * sinLambda, (cosU1 * sinU2) - ((sinU1 * cosU2) * cosLambda));
        results.mInitialBearing = (float) (initialBearing * 57.29577951308232d);
        float finalBearing = (float) Math.atan2(cosU1 * sinLambda, ((-sinU1) * cosU2) + (cosU1 * sinU2 * cosLambda));
        results.mFinalBearing = (float) (finalBearing * 57.29577951308232d);
        results.mLat1 = lat12;
        results.mLat2 = lat22;
        results.mLon1 = lon12;
        results.mLon2 = lon22;
    }

    public static void distanceBetween(double startLatitude, double startLongitude, double endLatitude, double endLongitude, float[] results) {
        if (results == null || results.length < 1) {
            throw new IllegalArgumentException("results is null or has length < 1");
        }
        BearingDistanceCache cache = sBearingDistanceCache.get();
        computeDistanceAndBearing(startLatitude, startLongitude, endLatitude, endLongitude, cache);
        results[0] = cache.mDistance;
        if (results.length > 1) {
            results[1] = cache.mInitialBearing;
            if (results.length > 2) {
                results[2] = cache.mFinalBearing;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class BearingDistanceCache {
        float mDistance;
        float mFinalBearing;
        float mInitialBearing;
        double mLat1;
        double mLat2;
        double mLon1;
        double mLon2;

        private BearingDistanceCache() {
            this.mLat1 = 0.0d;
            this.mLon1 = 0.0d;
            this.mLat2 = 0.0d;
            this.mLon2 = 0.0d;
            this.mDistance = 0.0f;
            this.mInitialBearing = 0.0f;
            this.mFinalBearing = 0.0f;
        }
    }
}
