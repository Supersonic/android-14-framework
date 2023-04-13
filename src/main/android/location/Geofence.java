package android.location;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
import android.provider.CallLog;
import android.util.TimeUtils;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class Geofence implements Parcelable {
    public static final Parcelable.Creator<Geofence> CREATOR = new Parcelable.Creator<Geofence>() { // from class: android.location.Geofence.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Geofence createFromParcel(Parcel in) {
            return new Geofence(in.readDouble(), in.readDouble(), in.readFloat(), in.readLong());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Geofence[] newArray(int size) {
            return new Geofence[size];
        }
    };
    private long mExpirationRealtimeMs;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;

    public static Geofence createCircle(double latitude, double longitude, float radius, long expirationRealtimeMs) {
        return new Geofence(latitude, longitude, radius, expirationRealtimeMs);
    }

    Geofence(double latitude, double longitude, float radius, long expirationRealtimeMs) {
        Preconditions.checkArgumentInRange(latitude, -90.0d, 90.0d, CallLog.Locations.LATITUDE);
        Preconditions.checkArgumentInRange(longitude, -180.0d, 180.0d, CallLog.Locations.LATITUDE);
        Preconditions.checkArgument(radius > 0.0f, "invalid radius: %f", Float.valueOf(radius));
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationRealtimeMs = expirationRealtimeMs;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public boolean isExpired() {
        return isExpired(SystemClock.elapsedRealtime());
    }

    public boolean isExpired(long referenceRealtimeMs) {
        return referenceRealtimeMs >= this.mExpirationRealtimeMs;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(this.mLatitude);
        parcel.writeDouble(this.mLongitude);
        parcel.writeFloat(this.mRadius);
        parcel.writeLong(this.mExpirationRealtimeMs);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Geofence) {
            Geofence geofence = (Geofence) o;
            return Double.compare(geofence.mLatitude, this.mLatitude) == 0 && Double.compare(geofence.mLongitude, this.mLongitude) == 0 && Float.compare(geofence.mRadius, this.mRadius) == 0 && this.mExpirationRealtimeMs == geofence.mExpirationRealtimeMs;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Double.valueOf(this.mLatitude), Double.valueOf(this.mLongitude), Float.valueOf(this.mRadius));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Geofence[(").append(this.mLatitude).append(", ").append(this.mLongitude).append(NavigationBarInflaterView.KEY_CODE_END);
        builder.append(" ").append(this.mRadius).append("m");
        if (this.mExpirationRealtimeMs < Long.MAX_VALUE) {
            if (isExpired()) {
                builder.append(" expired");
            } else {
                builder.append(" expires=");
                TimeUtils.formatDuration(this.mExpirationRealtimeMs, builder);
            }
        }
        builder.append(NavigationBarInflaterView.SIZE_MOD_END);
        return builder.toString();
    }
}
