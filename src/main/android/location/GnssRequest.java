package android.location;

import android.annotation.SystemApi;
import android.location.GnssMeasurementRequest;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes2.dex */
public final class GnssRequest implements Parcelable {
    public static final Parcelable.Creator<GnssRequest> CREATOR = new Parcelable.Creator<GnssRequest>() { // from class: android.location.GnssRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssRequest createFromParcel(Parcel parcel) {
            return new GnssRequest(parcel.readBoolean());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GnssRequest[] newArray(int i) {
            return new GnssRequest[i];
        }
    };
    private final boolean mFullTracking;

    private GnssRequest(boolean fullTracking) {
        this.mFullTracking = fullTracking;
    }

    public boolean isFullTracking() {
        return this.mFullTracking;
    }

    public GnssMeasurementRequest toGnssMeasurementRequest() {
        return new GnssMeasurementRequest.Builder().setFullTracking(isFullTracking()).build();
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("GnssRequest[");
        if (this.mFullTracking) {
            s.append("FullTracking");
        }
        s.append(']');
        return s.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof GnssRequest)) {
            return false;
        }
        GnssRequest other = (GnssRequest) obj;
        if (this.mFullTracking == other.mFullTracking) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.mFullTracking ? 1 : 0;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBoolean(this.mFullTracking);
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private boolean mFullTracking;

        public Builder() {
        }

        public Builder(GnssRequest request) {
            this.mFullTracking = request.isFullTracking();
        }

        public Builder setFullTracking(boolean value) {
            this.mFullTracking = value;
            return this;
        }

        public GnssRequest build() {
            return new GnssRequest(this.mFullTracking);
        }
    }
}
