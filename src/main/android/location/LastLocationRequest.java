package android.location;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class LastLocationRequest implements Parcelable {
    public static final Parcelable.Creator<LastLocationRequest> CREATOR = new Parcelable.Creator<LastLocationRequest>() { // from class: android.location.LastLocationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LastLocationRequest createFromParcel(Parcel in) {
            return new LastLocationRequest(in.readBoolean(), in.readBoolean(), in.readBoolean());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LastLocationRequest[] newArray(int size) {
            return new LastLocationRequest[size];
        }
    };
    private final boolean mAdasGnssBypass;
    private final boolean mHiddenFromAppOps;
    private final boolean mLocationSettingsIgnored;

    private LastLocationRequest(boolean hiddenFromAppOps, boolean adasGnssBypass, boolean locationSettingsIgnored) {
        this.mHiddenFromAppOps = hiddenFromAppOps;
        this.mAdasGnssBypass = adasGnssBypass;
        this.mLocationSettingsIgnored = locationSettingsIgnored;
    }

    @SystemApi
    public boolean isHiddenFromAppOps() {
        return this.mHiddenFromAppOps;
    }

    @SystemApi
    public boolean isAdasGnssBypass() {
        return this.mAdasGnssBypass;
    }

    @SystemApi
    public boolean isLocationSettingsIgnored() {
        return this.mLocationSettingsIgnored;
    }

    public boolean isBypass() {
        return this.mAdasGnssBypass || this.mLocationSettingsIgnored;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBoolean(this.mHiddenFromAppOps);
        parcel.writeBoolean(this.mAdasGnssBypass);
        parcel.writeBoolean(this.mLocationSettingsIgnored);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LastLocationRequest that = (LastLocationRequest) o;
        if (this.mHiddenFromAppOps == that.mHiddenFromAppOps && this.mAdasGnssBypass == that.mAdasGnssBypass && this.mLocationSettingsIgnored == that.mLocationSettingsIgnored) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mHiddenFromAppOps), Boolean.valueOf(this.mAdasGnssBypass), Boolean.valueOf(this.mLocationSettingsIgnored));
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("LastLocationRequest[");
        if (this.mHiddenFromAppOps) {
            s.append("hiddenFromAppOps, ");
        }
        if (this.mAdasGnssBypass) {
            s.append("adasGnssBypass, ");
        }
        if (this.mLocationSettingsIgnored) {
            s.append("settingsBypass, ");
        }
        if (s.length() > "LastLocationRequest[".length()) {
            s.setLength(s.length() - 2);
        }
        s.append(']');
        return s.toString();
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private boolean mAdasGnssBypass;
        private boolean mHiddenFromAppOps;
        private boolean mLocationSettingsIgnored;

        public Builder() {
            this.mHiddenFromAppOps = false;
            this.mAdasGnssBypass = false;
            this.mLocationSettingsIgnored = false;
        }

        public Builder(LastLocationRequest lastLocationRequest) {
            this.mHiddenFromAppOps = lastLocationRequest.mHiddenFromAppOps;
            this.mAdasGnssBypass = lastLocationRequest.mAdasGnssBypass;
            this.mLocationSettingsIgnored = lastLocationRequest.mLocationSettingsIgnored;
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
            this.mLocationSettingsIgnored = locationSettingsIgnored;
            return this;
        }

        public LastLocationRequest build() {
            return new LastLocationRequest(this.mHiddenFromAppOps, this.mAdasGnssBypass, this.mLocationSettingsIgnored);
        }
    }
}
