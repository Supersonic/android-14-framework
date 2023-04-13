package android.location;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.TimeUtils;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class GnssMeasurementRequest implements Parcelable {
    public static final Parcelable.Creator<GnssMeasurementRequest> CREATOR = new Parcelable.Creator<GnssMeasurementRequest>() { // from class: android.location.GnssMeasurementRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssMeasurementRequest createFromParcel(Parcel parcel) {
            return new GnssMeasurementRequest(parcel.readBoolean(), parcel.readBoolean(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssMeasurementRequest[] newArray(int i) {
            return new GnssMeasurementRequest[i];
        }
    };
    public static final int PASSIVE_INTERVAL = Integer.MAX_VALUE;
    private final boolean mCorrelationVectorOutputsEnabled;
    private final boolean mFullTracking;
    private final int mIntervalMillis;

    private GnssMeasurementRequest(boolean fullTracking, boolean correlationVectorOutputsEnabled, int intervalMillis) {
        this.mFullTracking = fullTracking;
        this.mCorrelationVectorOutputsEnabled = correlationVectorOutputsEnabled;
        this.mIntervalMillis = intervalMillis;
    }

    @SystemApi
    public boolean isCorrelationVectorOutputsEnabled() {
        return this.mCorrelationVectorOutputsEnabled;
    }

    public boolean isFullTracking() {
        return this.mFullTracking;
    }

    public int getIntervalMillis() {
        return this.mIntervalMillis;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBoolean(this.mFullTracking);
        parcel.writeBoolean(this.mCorrelationVectorOutputsEnabled);
        parcel.writeInt(this.mIntervalMillis);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("GnssMeasurementRequest[");
        s.append("@");
        TimeUtils.formatDuration(this.mIntervalMillis, s);
        if (this.mFullTracking) {
            s.append(", FullTracking");
        }
        if (this.mCorrelationVectorOutputsEnabled) {
            s.append(", CorrelationVectorOutputs");
        }
        s.append(']');
        return s.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof GnssMeasurementRequest)) {
            return false;
        }
        GnssMeasurementRequest other = (GnssMeasurementRequest) obj;
        if (this.mFullTracking == other.mFullTracking && this.mCorrelationVectorOutputsEnabled == other.mCorrelationVectorOutputsEnabled && this.mIntervalMillis == other.mIntervalMillis) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mFullTracking), Boolean.valueOf(this.mCorrelationVectorOutputsEnabled), Integer.valueOf(this.mIntervalMillis));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private boolean mCorrelationVectorOutputsEnabled;
        private boolean mFullTracking;
        private int mIntervalMillis;

        public Builder() {
        }

        public Builder(GnssMeasurementRequest request) {
            this.mCorrelationVectorOutputsEnabled = request.isCorrelationVectorOutputsEnabled();
            this.mFullTracking = request.isFullTracking();
            this.mIntervalMillis = request.getIntervalMillis();
        }

        @SystemApi
        public Builder setCorrelationVectorOutputsEnabled(boolean value) {
            this.mCorrelationVectorOutputsEnabled = value;
            return this;
        }

        public Builder setFullTracking(boolean value) {
            this.mFullTracking = value;
            return this;
        }

        public Builder setIntervalMillis(int value) {
            this.mIntervalMillis = Preconditions.checkArgumentInRange(value, 0, Integer.MAX_VALUE, "intervalMillis");
            return this;
        }

        public GnssMeasurementRequest build() {
            return new GnssMeasurementRequest(this.mFullTracking, this.mCorrelationVectorOutputsEnabled, this.mIntervalMillis);
        }
    }
}
