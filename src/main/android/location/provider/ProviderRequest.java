package android.location.provider;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.WorkSource;
import android.util.TimeUtils;
import com.android.internal.util.Preconditions;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class ProviderRequest implements Parcelable {
    public static final long INTERVAL_DISABLED = Long.MAX_VALUE;
    private final boolean mAdasGnssBypass;
    private final long mIntervalMillis;
    private final boolean mLocationSettingsIgnored;
    private final boolean mLowPower;
    private final long mMaxUpdateDelayMillis;
    private final int mQuality;
    private final WorkSource mWorkSource;
    public static final ProviderRequest EMPTY_REQUEST = new ProviderRequest(Long.MAX_VALUE, 102, 0, false, false, false, new WorkSource());
    public static final Parcelable.Creator<ProviderRequest> CREATOR = new Parcelable.Creator<ProviderRequest>() { // from class: android.location.provider.ProviderRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProviderRequest createFromParcel(Parcel in) {
            long intervalMillis = in.readLong();
            if (intervalMillis == Long.MAX_VALUE) {
                return ProviderRequest.EMPTY_REQUEST;
            }
            return new ProviderRequest(intervalMillis, in.readInt(), in.readLong(), in.readBoolean(), in.readBoolean(), in.readBoolean(), (WorkSource) in.readTypedObject(WorkSource.CREATOR));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProviderRequest[] newArray(int size) {
            return new ProviderRequest[size];
        }
    };

    /* loaded from: classes2.dex */
    public interface ChangedListener {
        void onProviderRequestChanged(String str, ProviderRequest providerRequest);
    }

    private ProviderRequest(long intervalMillis, int quality, long maxUpdateDelayMillis, boolean lowPower, boolean adasGnssBypass, boolean locationSettingsIgnored, WorkSource workSource) {
        this.mIntervalMillis = intervalMillis;
        this.mQuality = quality;
        this.mMaxUpdateDelayMillis = maxUpdateDelayMillis;
        this.mLowPower = lowPower;
        this.mAdasGnssBypass = adasGnssBypass;
        this.mLocationSettingsIgnored = locationSettingsIgnored;
        this.mWorkSource = (WorkSource) Objects.requireNonNull(workSource);
    }

    public boolean isActive() {
        return this.mIntervalMillis != Long.MAX_VALUE;
    }

    public long getIntervalMillis() {
        return this.mIntervalMillis;
    }

    public int getQuality() {
        return this.mQuality;
    }

    public long getMaxUpdateDelayMillis() {
        return this.mMaxUpdateDelayMillis;
    }

    public boolean isLowPower() {
        return this.mLowPower;
    }

    public boolean isAdasGnssBypass() {
        return this.mAdasGnssBypass;
    }

    public boolean isLocationSettingsIgnored() {
        return this.mLocationSettingsIgnored;
    }

    public boolean isBypass() {
        return this.mAdasGnssBypass || this.mLocationSettingsIgnored;
    }

    public WorkSource getWorkSource() {
        return this.mWorkSource;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.mIntervalMillis);
        if (this.mIntervalMillis != Long.MAX_VALUE) {
            parcel.writeInt(this.mQuality);
            parcel.writeLong(this.mMaxUpdateDelayMillis);
            parcel.writeBoolean(this.mLowPower);
            parcel.writeBoolean(this.mAdasGnssBypass);
            parcel.writeBoolean(this.mLocationSettingsIgnored);
            parcel.writeTypedObject(this.mWorkSource, flags);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProviderRequest that = (ProviderRequest) o;
        long j = this.mIntervalMillis;
        if (j == Long.MAX_VALUE) {
            if (that.mIntervalMillis == Long.MAX_VALUE) {
                return true;
            }
            return false;
        } else if (j == that.mIntervalMillis && this.mQuality == that.mQuality && this.mMaxUpdateDelayMillis == that.mMaxUpdateDelayMillis && this.mLowPower == that.mLowPower && this.mAdasGnssBypass == that.mAdasGnssBypass && this.mLocationSettingsIgnored == that.mLocationSettingsIgnored && this.mWorkSource.equals(that.mWorkSource)) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mIntervalMillis), Integer.valueOf(this.mQuality), this.mWorkSource);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("ProviderRequest[");
        if (this.mIntervalMillis != Long.MAX_VALUE) {
            s.append("@");
            TimeUtils.formatDuration(this.mIntervalMillis, s);
            int i = this.mQuality;
            if (i != 102) {
                if (i == 100) {
                    s.append(", HIGH_ACCURACY");
                } else if (i == 104) {
                    s.append(", LOW_POWER");
                }
            }
            if (this.mMaxUpdateDelayMillis / 2 > this.mIntervalMillis) {
                s.append(", maxUpdateDelay=");
                TimeUtils.formatDuration(this.mMaxUpdateDelayMillis, s);
            }
            if (this.mLowPower) {
                s.append(", lowPower");
            }
            if (this.mAdasGnssBypass) {
                s.append(", adasGnssBypass");
            }
            if (this.mLocationSettingsIgnored) {
                s.append(", settingsBypass");
            }
            if (!this.mWorkSource.isEmpty()) {
                s.append(", ").append(this.mWorkSource);
            }
        } else {
            s.append("OFF");
        }
        s.append(']');
        return s.toString();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private boolean mAdasGnssBypass;
        private boolean mLocationSettingsIgnored;
        private boolean mLowPower;
        private long mIntervalMillis = Long.MAX_VALUE;
        private int mQuality = 102;
        private long mMaxUpdateDelayMillis = 0;
        private WorkSource mWorkSource = new WorkSource();

        public Builder setIntervalMillis(long intervalMillis) {
            this.mIntervalMillis = Preconditions.checkArgumentInRange(intervalMillis, 0L, Long.MAX_VALUE, "intervalMillis");
            return this;
        }

        public Builder setQuality(int quality) {
            Preconditions.checkArgument(quality == 104 || quality == 102 || quality == 100);
            this.mQuality = quality;
            return this;
        }

        public Builder setMaxUpdateDelayMillis(long maxUpdateDelayMillis) {
            this.mMaxUpdateDelayMillis = Preconditions.checkArgumentInRange(maxUpdateDelayMillis, 0L, Long.MAX_VALUE, "maxUpdateDelayMillis");
            return this;
        }

        public Builder setLowPower(boolean lowPower) {
            this.mLowPower = lowPower;
            return this;
        }

        public Builder setAdasGnssBypass(boolean adasGnssBypass) {
            this.mAdasGnssBypass = adasGnssBypass;
            return this;
        }

        public Builder setLocationSettingsIgnored(boolean locationSettingsIgnored) {
            this.mLocationSettingsIgnored = locationSettingsIgnored;
            return this;
        }

        public Builder setWorkSource(WorkSource workSource) {
            this.mWorkSource = (WorkSource) Objects.requireNonNull(workSource);
            return this;
        }

        public ProviderRequest build() {
            if (this.mIntervalMillis == Long.MAX_VALUE) {
                return ProviderRequest.EMPTY_REQUEST;
            }
            return new ProviderRequest(this.mIntervalMillis, this.mQuality, this.mMaxUpdateDelayMillis, this.mLowPower, this.mAdasGnssBypass, this.mLocationSettingsIgnored, this.mWorkSource);
        }
    }
}
