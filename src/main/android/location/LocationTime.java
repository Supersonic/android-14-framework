package android.location;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.time.Duration;
import java.time.Instant;
/* loaded from: classes2.dex */
public final class LocationTime implements Parcelable {
    public static final Parcelable.Creator<LocationTime> CREATOR = new Parcelable.Creator<LocationTime>() { // from class: android.location.LocationTime.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocationTime createFromParcel(Parcel in) {
            long time = in.readLong();
            long elapsedRealtimeNanos = in.readLong();
            return new LocationTime(time, elapsedRealtimeNanos);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocationTime[] newArray(int size) {
            return new LocationTime[size];
        }
    };
    private final long mElapsedRealtimeNanos;
    private final long mUnixEpochTimeMillis;

    public LocationTime(long unixEpochTimeMillis, long elapsedRealtimeNanos) {
        this.mUnixEpochTimeMillis = unixEpochTimeMillis;
        this.mElapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public long getUnixEpochTimeMillis() {
        return this.mUnixEpochTimeMillis;
    }

    public long getElapsedRealtimeNanos() {
        return this.mElapsedRealtimeNanos;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mUnixEpochTimeMillis);
        out.writeLong(this.mElapsedRealtimeNanos);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "LocationTime{mUnixEpochTimeMillis=" + Instant.ofEpochMilli(this.mUnixEpochTimeMillis) + NavigationBarInflaterView.KEY_CODE_START + this.mUnixEpochTimeMillis + "), mElapsedRealtimeNanos=" + Duration.ofNanos(this.mElapsedRealtimeNanos) + NavigationBarInflaterView.KEY_CODE_START + this.mElapsedRealtimeNanos + NavigationBarInflaterView.KEY_CODE_END + '}';
    }
}
