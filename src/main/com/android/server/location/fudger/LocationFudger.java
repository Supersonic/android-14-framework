package com.android.server.location.fudger;

import android.location.Location;
import android.location.LocationResult;
import android.os.SystemClock;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Random;
import java.util.function.Function;
/* loaded from: classes.dex */
public class LocationFudger {
    @VisibleForTesting
    static final long OFFSET_UPDATE_INTERVAL_MS = 3600000;
    public static final double OLD_WEIGHT = Math.sqrt(0.9991d);
    public final float mAccuracyM;
    @GuardedBy({"this"})
    public Location mCachedCoarseLocation;
    @GuardedBy({"this"})
    public LocationResult mCachedCoarseLocationResult;
    @GuardedBy({"this"})
    public Location mCachedFineLocation;
    @GuardedBy({"this"})
    public LocationResult mCachedFineLocationResult;
    public final Clock mClock;
    @GuardedBy({"this"})
    public double mLatitudeOffsetM;
    @GuardedBy({"this"})
    public double mLongitudeOffsetM;
    @GuardedBy({"this"})
    public long mNextUpdateRealtimeMs;
    public final Random mRandom;

    public static double metersToDegreesLatitude(double d) {
        return d / 111000.0d;
    }

    public static double wrapLatitude(double d) {
        if (d > 89.999990990991d) {
            d = 89.999990990991d;
        }
        if (d < -89.999990990991d) {
            return -89.999990990991d;
        }
        return d;
    }

    public static double wrapLongitude(double d) {
        double d2 = d % 360.0d;
        if (d2 >= 180.0d) {
            d2 -= 360.0d;
        }
        return d2 < -180.0d ? d2 + 360.0d : d2;
    }

    public LocationFudger(float f) {
        this(f, SystemClock.elapsedRealtimeClock(), new SecureRandom());
    }

    @VisibleForTesting
    public LocationFudger(float f, Clock clock, Random random) {
        this.mClock = clock;
        this.mRandom = random;
        this.mAccuracyM = Math.max(f, 200.0f);
        resetOffsets();
    }

    public void resetOffsets() {
        this.mLatitudeOffsetM = nextRandomOffset();
        this.mLongitudeOffsetM = nextRandomOffset();
        this.mNextUpdateRealtimeMs = this.mClock.millis() + 3600000;
    }

    public LocationResult createCoarse(LocationResult locationResult) {
        synchronized (this) {
            if (locationResult != this.mCachedFineLocationResult && locationResult != this.mCachedCoarseLocationResult) {
                LocationResult map = locationResult.map(new Function() { // from class: com.android.server.location.fudger.LocationFudger$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return LocationFudger.this.createCoarse((Location) obj);
                    }
                });
                synchronized (this) {
                    this.mCachedFineLocationResult = locationResult;
                    this.mCachedCoarseLocationResult = map;
                }
                return map;
            }
            return this.mCachedCoarseLocationResult;
        }
    }

    public Location createCoarse(Location location) {
        synchronized (this) {
            if (location != this.mCachedFineLocation && location != this.mCachedCoarseLocation) {
                updateOffsets();
                Location location2 = new Location(location);
                location2.removeBearing();
                location2.removeSpeed();
                location2.removeAltitude();
                location2.setExtras(null);
                double wrapLatitude = wrapLatitude(location2.getLatitude());
                double wrapLongitude = wrapLongitude(location2.getLongitude()) + wrapLongitude(metersToDegreesLongitude(this.mLongitudeOffsetM, wrapLatitude));
                double wrapLatitude2 = wrapLatitude + wrapLatitude(metersToDegreesLatitude(this.mLatitudeOffsetM));
                double metersToDegreesLatitude = metersToDegreesLatitude(this.mAccuracyM);
                double wrapLatitude3 = wrapLatitude(Math.round(wrapLatitude2 / metersToDegreesLatitude) * metersToDegreesLatitude);
                double metersToDegreesLongitude = metersToDegreesLongitude(this.mAccuracyM, wrapLatitude3);
                double wrapLongitude2 = wrapLongitude(Math.round(wrapLongitude / metersToDegreesLongitude) * metersToDegreesLongitude);
                location2.setLatitude(wrapLatitude3);
                location2.setLongitude(wrapLongitude2);
                location2.setAccuracy(Math.max(this.mAccuracyM, location2.getAccuracy()));
                synchronized (this) {
                    this.mCachedFineLocation = location;
                    this.mCachedCoarseLocation = location2;
                }
                return location2;
            }
            return this.mCachedCoarseLocation;
        }
    }

    public final synchronized void updateOffsets() {
        long millis = this.mClock.millis();
        if (millis < this.mNextUpdateRealtimeMs) {
            return;
        }
        double d = OLD_WEIGHT;
        this.mLatitudeOffsetM = (this.mLatitudeOffsetM * d) + (nextRandomOffset() * 0.03d);
        this.mLongitudeOffsetM = (d * this.mLongitudeOffsetM) + (nextRandomOffset() * 0.03d);
        this.mNextUpdateRealtimeMs = millis + 3600000;
    }

    public final double nextRandomOffset() {
        return this.mRandom.nextGaussian() * (this.mAccuracyM / 4.0d);
    }

    public static double metersToDegreesLongitude(double d, double d2) {
        return (d / 111000.0d) / Math.cos(Math.toRadians(d2));
    }
}
