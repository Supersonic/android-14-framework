package android.location;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.WorkSource;
import android.util.TimeUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class LocationRequest implements Parcelable {
    @SystemApi
    @Deprecated
    public static final int ACCURACY_BLOCK = 102;
    @SystemApi
    @Deprecated
    public static final int ACCURACY_CITY = 104;
    @SystemApi
    @Deprecated
    public static final int ACCURACY_FINE = 100;
    public static final Parcelable.Creator<LocationRequest> CREATOR = new Parcelable.Creator<LocationRequest>() { // from class: android.location.LocationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocationRequest createFromParcel(Parcel in) {
            return new LocationRequest(in.readString(), in.readLong(), in.readInt(), in.readLong(), in.readLong(), in.readInt(), in.readLong(), in.readFloat(), in.readLong(), in.readBoolean(), in.readBoolean(), in.readBoolean(), in.readBoolean(), (WorkSource) in.readTypedObject(WorkSource.CREATOR));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LocationRequest[] newArray(int size) {
            return new LocationRequest[size];
        }
    };
    private static final long IMPLICIT_MIN_UPDATE_INTERVAL = -1;
    private static final double IMPLICIT_MIN_UPDATE_INTERVAL_FACTOR = 0.16666666666666666d;
    public static final long LOW_POWER_EXCEPTIONS = 168936375;
    public static final long PASSIVE_INTERVAL = Long.MAX_VALUE;
    @SystemApi
    @Deprecated
    public static final int POWER_HIGH = 203;
    @SystemApi
    @Deprecated
    public static final int POWER_LOW = 201;
    @SystemApi
    @Deprecated
    public static final int POWER_NONE = 200;
    public static final int QUALITY_BALANCED_POWER_ACCURACY = 102;
    public static final int QUALITY_HIGH_ACCURACY = 100;
    public static final int QUALITY_LOW_POWER = 104;
    private final boolean mAdasGnssBypass;
    private boolean mBypass;
    private long mDurationMillis;
    private long mExpireAtRealtimeMillis;
    private boolean mHideFromAppOps;
    private long mIntervalMillis;
    private boolean mLowPower;
    private final long mMaxUpdateDelayMillis;
    private int mMaxUpdates;
    private float mMinUpdateDistanceMeters;
    private long mMinUpdateIntervalMillis;
    private String mProvider;
    private int mQuality;
    private WorkSource mWorkSource;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Quality {
    }

    @SystemApi
    @Deprecated
    public static LocationRequest create() {
        return new Builder(3600000L).build();
    }

    @SystemApi
    @Deprecated
    public static LocationRequest createFromDeprecatedProvider(String provider, long intervalMillis, float minUpdateDistanceMeters, boolean singleShot) {
        int quality;
        Preconditions.checkArgument(provider != null, "invalid null provider");
        if (intervalMillis < 0) {
            intervalMillis = 0;
        } else if (intervalMillis == Long.MAX_VALUE) {
            intervalMillis = 9223372036854775806L;
        }
        if (minUpdateDistanceMeters < 0.0f) {
            minUpdateDistanceMeters = 0.0f;
        }
        if (LocationManager.PASSIVE_PROVIDER.equals(provider)) {
            quality = 200;
        } else if (LocationManager.GPS_PROVIDER.equals(provider)) {
            quality = 100;
        } else {
            quality = 102;
        }
        return new Builder(intervalMillis).setMinUpdateIntervalMillis(intervalMillis).setMinUpdateDistanceMeters(minUpdateDistanceMeters).setMaxUpdates(singleShot ? 1 : Integer.MAX_VALUE).build().setProvider(provider).setQuality(quality);
    }

    @SystemApi
    @Deprecated
    public static LocationRequest createFromDeprecatedCriteria(Criteria criteria, long intervalMillis, float minUpdateDistanceMeters, boolean singleShot) {
        Preconditions.checkArgument(criteria != null, "invalid null criteria");
        if (intervalMillis < 0) {
            intervalMillis = 0;
        } else if (intervalMillis == Long.MAX_VALUE) {
            intervalMillis = 9223372036854775806L;
        }
        if (minUpdateDistanceMeters < 0.0f) {
            minUpdateDistanceMeters = 0.0f;
        }
        return new Builder(intervalMillis).setQuality(criteria).setMinUpdateIntervalMillis(intervalMillis).setMinUpdateDistanceMeters(minUpdateDistanceMeters).setMaxUpdates(singleShot ? 1 : Integer.MAX_VALUE).build();
    }

    private LocationRequest(String provider, long intervalMillis, int quality, long expireAtRealtimeMillis, long durationMillis, int maxUpdates, long minUpdateIntervalMillis, float minUpdateDistanceMeters, long maxUpdateDelayMillis, boolean hiddenFromAppOps, boolean adasGnssBypass, boolean bypass, boolean lowPower, WorkSource workSource) {
        this.mProvider = provider;
        this.mIntervalMillis = intervalMillis;
        this.mQuality = quality;
        this.mMinUpdateIntervalMillis = minUpdateIntervalMillis;
        this.mExpireAtRealtimeMillis = expireAtRealtimeMillis;
        this.mDurationMillis = durationMillis;
        this.mMaxUpdates = maxUpdates;
        this.mMinUpdateDistanceMeters = minUpdateDistanceMeters;
        this.mMaxUpdateDelayMillis = maxUpdateDelayMillis;
        this.mHideFromAppOps = hiddenFromAppOps;
        this.mAdasGnssBypass = adasGnssBypass;
        this.mBypass = bypass;
        this.mLowPower = lowPower;
        this.mWorkSource = (WorkSource) Objects.requireNonNull(workSource);
    }

    @SystemApi
    @Deprecated
    public LocationRequest setProvider(String provider) {
        Preconditions.checkArgument(provider != null);
        this.mProvider = provider;
        return this;
    }

    @SystemApi
    @Deprecated
    public String getProvider() {
        String str = this.mProvider;
        return str != null ? str : LocationManager.FUSED_PROVIDER;
    }

    @SystemApi
    @Deprecated
    public LocationRequest setQuality(int quality) {
        switch (quality) {
            case 100:
            case 203:
                this.mQuality = 100;
                break;
            case 102:
                this.mQuality = 102;
                break;
            case 104:
            case 201:
                this.mQuality = 104;
                break;
            case 200:
                this.mIntervalMillis = Long.MAX_VALUE;
                break;
            default:
                throw new IllegalArgumentException("invalid quality: " + quality);
        }
        return this;
    }

    public int getQuality() {
        return this.mQuality;
    }

    @SystemApi
    @Deprecated
    public LocationRequest setInterval(long millis) {
        Preconditions.checkArgument(millis >= 0);
        if (millis == Long.MAX_VALUE) {
            millis = 9223372036854775806L;
        }
        this.mIntervalMillis = millis;
        if (this.mMinUpdateIntervalMillis > millis) {
            this.mMinUpdateIntervalMillis = millis;
        }
        return this;
    }

    @SystemApi
    @Deprecated
    public long getInterval() {
        return getIntervalMillis();
    }

    public long getIntervalMillis() {
        return this.mIntervalMillis;
    }

    @SystemApi
    @Deprecated
    public LocationRequest setFastestInterval(long millis) {
        Preconditions.checkArgument(millis >= 0);
        this.mMinUpdateIntervalMillis = millis;
        return this;
    }

    @SystemApi
    @Deprecated
    public long getFastestInterval() {
        return getMinUpdateIntervalMillis();
    }

    @SystemApi
    @Deprecated
    public LocationRequest setExpireAt(long millis) {
        this.mExpireAtRealtimeMillis = Math.max(millis, 0L);
        return this;
    }

    @SystemApi
    @Deprecated
    public long getExpireAt() {
        return this.mExpireAtRealtimeMillis;
    }

    @SystemApi
    @Deprecated
    public LocationRequest setExpireIn(long millis) {
        this.mDurationMillis = millis;
        return this;
    }

    @SystemApi
    @Deprecated
    public long getExpireIn() {
        return getDurationMillis();
    }

    public long getDurationMillis() {
        return this.mDurationMillis;
    }

    public long getExpirationRealtimeMs(long startRealtimeMs) {
        long expirationRealtimeMs;
        long expirationRealtimeMs2 = this.mDurationMillis;
        if (expirationRealtimeMs2 > Long.MAX_VALUE - startRealtimeMs) {
            expirationRealtimeMs = Long.MAX_VALUE;
        } else {
            expirationRealtimeMs = expirationRealtimeMs2 + startRealtimeMs;
        }
        return Math.min(expirationRealtimeMs, this.mExpireAtRealtimeMillis);
    }

    @SystemApi
    @Deprecated
    public LocationRequest setNumUpdates(int numUpdates) {
        if (numUpdates <= 0) {
            throw new IllegalArgumentException("invalid numUpdates: " + numUpdates);
        }
        this.mMaxUpdates = numUpdates;
        return this;
    }

    @SystemApi
    @Deprecated
    public int getNumUpdates() {
        return getMaxUpdates();
    }

    public int getMaxUpdates() {
        return this.mMaxUpdates;
    }

    public long getMinUpdateIntervalMillis() {
        long j = this.mMinUpdateIntervalMillis;
        if (j == -1) {
            return (long) (this.mIntervalMillis * IMPLICIT_MIN_UPDATE_INTERVAL_FACTOR);
        }
        return Math.min(j, this.mIntervalMillis);
    }

    @SystemApi
    @Deprecated
    public LocationRequest setSmallestDisplacement(float minDisplacementMeters) {
        this.mMinUpdateDistanceMeters = Preconditions.checkArgumentInRange(minDisplacementMeters, 0.0f, Float.MAX_VALUE, "minDisplacementMeters");
        return this;
    }

    @SystemApi
    @Deprecated
    public float getSmallestDisplacement() {
        return getMinUpdateDistanceMeters();
    }

    public float getMinUpdateDistanceMeters() {
        return this.mMinUpdateDistanceMeters;
    }

    public long getMaxUpdateDelayMillis() {
        return this.mMaxUpdateDelayMillis;
    }

    @SystemApi
    @Deprecated
    public void setHideFromAppOps(boolean hiddenFromAppOps) {
        this.mHideFromAppOps = hiddenFromAppOps;
    }

    @SystemApi
    @Deprecated
    public boolean getHideFromAppOps() {
        return isHiddenFromAppOps();
    }

    @SystemApi
    public boolean isHiddenFromAppOps() {
        return this.mHideFromAppOps;
    }

    @SystemApi
    public boolean isAdasGnssBypass() {
        return this.mAdasGnssBypass;
    }

    @SystemApi
    @Deprecated
    public LocationRequest setLocationSettingsIgnored(boolean locationSettingsIgnored) {
        this.mBypass = locationSettingsIgnored;
        return this;
    }

    @SystemApi
    public boolean isLocationSettingsIgnored() {
        return this.mBypass;
    }

    public boolean isBypass() {
        return this.mAdasGnssBypass || this.mBypass;
    }

    @SystemApi
    @Deprecated
    public LocationRequest setLowPowerMode(boolean enabled) {
        this.mLowPower = enabled;
        return this;
    }

    @SystemApi
    @Deprecated
    public boolean isLowPowerMode() {
        return isLowPower();
    }

    @SystemApi
    public boolean isLowPower() {
        return this.mLowPower;
    }

    @SystemApi
    @Deprecated
    public void setWorkSource(WorkSource workSource) {
        if (workSource == null) {
            workSource = new WorkSource();
        }
        this.mWorkSource = workSource;
    }

    @SystemApi
    public WorkSource getWorkSource() {
        return this.mWorkSource;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mProvider);
        parcel.writeLong(this.mIntervalMillis);
        parcel.writeInt(this.mQuality);
        parcel.writeLong(this.mExpireAtRealtimeMillis);
        parcel.writeLong(this.mDurationMillis);
        parcel.writeInt(this.mMaxUpdates);
        parcel.writeLong(this.mMinUpdateIntervalMillis);
        parcel.writeFloat(this.mMinUpdateDistanceMeters);
        parcel.writeLong(this.mMaxUpdateDelayMillis);
        parcel.writeBoolean(this.mHideFromAppOps);
        parcel.writeBoolean(this.mAdasGnssBypass);
        parcel.writeBoolean(this.mBypass);
        parcel.writeBoolean(this.mLowPower);
        parcel.writeTypedObject(this.mWorkSource, 0);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationRequest that = (LocationRequest) o;
        if (this.mIntervalMillis == that.mIntervalMillis && this.mQuality == that.mQuality && this.mExpireAtRealtimeMillis == that.mExpireAtRealtimeMillis && this.mDurationMillis == that.mDurationMillis && this.mMaxUpdates == that.mMaxUpdates && this.mMinUpdateIntervalMillis == that.mMinUpdateIntervalMillis && Float.compare(that.mMinUpdateDistanceMeters, this.mMinUpdateDistanceMeters) == 0 && this.mMaxUpdateDelayMillis == that.mMaxUpdateDelayMillis && this.mHideFromAppOps == that.mHideFromAppOps && this.mAdasGnssBypass == that.mAdasGnssBypass && this.mBypass == that.mBypass && this.mLowPower == that.mLowPower && Objects.equals(this.mProvider, that.mProvider) && Objects.equals(this.mWorkSource, that.mWorkSource)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mProvider, Long.valueOf(this.mIntervalMillis), this.mWorkSource);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Request[");
        String str = this.mProvider;
        if (str != null) {
            s.append(str).append(" ");
        }
        if (this.mIntervalMillis != Long.MAX_VALUE) {
            s.append("@");
            TimeUtils.formatDuration(this.mIntervalMillis, s);
            switch (this.mQuality) {
                case 100:
                    s.append(" HIGH_ACCURACY");
                    break;
                case 102:
                    s.append(" BALANCED");
                    break;
                case 104:
                    s.append(" LOW_POWER");
                    break;
            }
        } else {
            s.append("PASSIVE");
        }
        if (this.mExpireAtRealtimeMillis != Long.MAX_VALUE) {
            s.append(", expireAt=").append(TimeUtils.formatRealtime(this.mExpireAtRealtimeMillis));
        }
        if (this.mDurationMillis != Long.MAX_VALUE) {
            s.append(", duration=");
            TimeUtils.formatDuration(this.mDurationMillis, s);
        }
        if (this.mMaxUpdates != Integer.MAX_VALUE) {
            s.append(", maxUpdates=").append(this.mMaxUpdates);
        }
        long j = this.mMinUpdateIntervalMillis;
        if (j != -1 && j < this.mIntervalMillis) {
            s.append(", minUpdateInterval=");
            TimeUtils.formatDuration(this.mMinUpdateIntervalMillis, s);
        }
        if (this.mMinUpdateDistanceMeters > 0.0d) {
            s.append(", minUpdateDistance=").append(this.mMinUpdateDistanceMeters);
        }
        if (this.mMaxUpdateDelayMillis / 2 > this.mIntervalMillis) {
            s.append(", maxUpdateDelay=");
            TimeUtils.formatDuration(this.mMaxUpdateDelayMillis, s);
        }
        if (this.mLowPower) {
            s.append(", lowPower");
        }
        if (this.mHideFromAppOps) {
            s.append(", hiddenFromAppOps");
        }
        if (this.mAdasGnssBypass) {
            s.append(", adasGnssBypass");
        }
        if (this.mBypass) {
            s.append(", bypass");
        }
        WorkSource workSource = this.mWorkSource;
        if (workSource != null && !workSource.isEmpty()) {
            s.append(", ").append(this.mWorkSource);
        }
        s.append(']');
        return s.toString();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private boolean mAdasGnssBypass;
        private boolean mBypass;
        private long mDurationMillis;
        private boolean mHiddenFromAppOps;
        private long mIntervalMillis;
        private boolean mLowPower;
        private long mMaxUpdateDelayMillis;
        private int mMaxUpdates;
        private float mMinUpdateDistanceMeters;
        private long mMinUpdateIntervalMillis;
        private int mQuality;
        private WorkSource mWorkSource;

        public Builder(long intervalMillis) {
            setIntervalMillis(intervalMillis);
            this.mQuality = 102;
            this.mDurationMillis = Long.MAX_VALUE;
            this.mMaxUpdates = Integer.MAX_VALUE;
            this.mMinUpdateIntervalMillis = -1L;
            this.mMinUpdateDistanceMeters = 0.0f;
            this.mMaxUpdateDelayMillis = 0L;
            this.mHiddenFromAppOps = false;
            this.mAdasGnssBypass = false;
            this.mBypass = false;
            this.mLowPower = false;
            this.mWorkSource = null;
        }

        public Builder(LocationRequest locationRequest) {
            this.mIntervalMillis = locationRequest.mIntervalMillis;
            this.mQuality = locationRequest.mQuality;
            this.mDurationMillis = locationRequest.mDurationMillis;
            this.mMaxUpdates = locationRequest.mMaxUpdates;
            this.mMinUpdateIntervalMillis = locationRequest.mMinUpdateIntervalMillis;
            this.mMinUpdateDistanceMeters = locationRequest.mMinUpdateDistanceMeters;
            this.mMaxUpdateDelayMillis = locationRequest.mMaxUpdateDelayMillis;
            this.mHiddenFromAppOps = locationRequest.mHideFromAppOps;
            this.mAdasGnssBypass = locationRequest.mAdasGnssBypass;
            this.mBypass = locationRequest.mBypass;
            this.mLowPower = locationRequest.mLowPower;
            this.mWorkSource = locationRequest.mWorkSource;
            if (this.mIntervalMillis == Long.MAX_VALUE && this.mMinUpdateIntervalMillis == -1) {
                this.mMinUpdateIntervalMillis = 600000L;
            }
        }

        public Builder setIntervalMillis(long intervalMillis) {
            this.mIntervalMillis = Preconditions.checkArgumentInRange(intervalMillis, 0L, Long.MAX_VALUE, "intervalMillis");
            return this;
        }

        public Builder setQuality(int quality) {
            Preconditions.checkArgument(quality == 104 || quality == 102 || quality == 100, "quality must be a defined QUALITY constant, not %d", Integer.valueOf(quality));
            this.mQuality = quality;
            return this;
        }

        public Builder setQuality(Criteria criteria) {
            switch (criteria.getAccuracy()) {
                case 1:
                    this.mQuality = 100;
                    break;
                case 2:
                    this.mQuality = 102;
                    break;
                default:
                    if (criteria.getPowerRequirement() == 3) {
                        this.mQuality = 203;
                        break;
                    } else {
                        this.mQuality = 201;
                        break;
                    }
            }
            return this;
        }

        public Builder setDurationMillis(long durationMillis) {
            this.mDurationMillis = Preconditions.checkArgumentInRange(durationMillis, 1L, Long.MAX_VALUE, "durationMillis");
            return this;
        }

        public Builder setMaxUpdates(int maxUpdates) {
            this.mMaxUpdates = Preconditions.checkArgumentInRange(maxUpdates, 1, Integer.MAX_VALUE, "maxUpdates");
            return this;
        }

        public Builder setMinUpdateIntervalMillis(long minUpdateIntervalMillis) {
            this.mMinUpdateIntervalMillis = Preconditions.checkArgumentInRange(minUpdateIntervalMillis, 0L, Long.MAX_VALUE, "minUpdateIntervalMillis");
            return this;
        }

        public Builder clearMinUpdateIntervalMillis() {
            this.mMinUpdateIntervalMillis = -1L;
            return this;
        }

        public Builder setMinUpdateDistanceMeters(float minUpdateDistanceMeters) {
            this.mMinUpdateDistanceMeters = Preconditions.checkArgumentInRange(minUpdateDistanceMeters, 0.0f, Float.MAX_VALUE, "minUpdateDistanceMeters");
            return this;
        }

        public Builder setMaxUpdateDelayMillis(long maxUpdateDelayMillis) {
            this.mMaxUpdateDelayMillis = Preconditions.checkArgumentInRange(maxUpdateDelayMillis, 0L, Long.MAX_VALUE, "maxUpdateDelayMillis");
            return this;
        }

        @SystemApi
        public Builder setHiddenFromAppOps(boolean hiddenFromAppOps) {
            this.mHiddenFromAppOps = hiddenFromAppOps;
            return this;
        }

        @SystemApi
        public Builder setAdasGnssBypass(boolean adasGnssBypass) {
            this.mAdasGnssBypass = adasGnssBypass;
            return this;
        }

        @SystemApi
        public Builder setLocationSettingsIgnored(boolean locationSettingsIgnored) {
            this.mBypass = locationSettingsIgnored;
            return this;
        }

        @SystemApi
        public Builder setLowPower(boolean lowPower) {
            this.mLowPower = lowPower;
            return this;
        }

        @SystemApi
        public Builder setWorkSource(WorkSource workSource) {
            this.mWorkSource = workSource;
            return this;
        }

        public LocationRequest build() {
            Preconditions.checkState((this.mIntervalMillis == Long.MAX_VALUE && this.mMinUpdateIntervalMillis == -1) ? false : true, "passive location requests must have an explicit minimum update interval");
            long j = this.mIntervalMillis;
            return new LocationRequest(null, j, this.mQuality, Long.MAX_VALUE, this.mDurationMillis, this.mMaxUpdates, Math.min(this.mMinUpdateIntervalMillis, j), this.mMinUpdateDistanceMeters, this.mMaxUpdateDelayMillis, this.mHiddenFromAppOps, this.mAdasGnssBypass, this.mBypass, this.mLowPower, new WorkSource(this.mWorkSource));
        }
    }
}
