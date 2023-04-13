package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class RcsClientConfiguration implements Parcelable {
    public static final Parcelable.Creator<RcsClientConfiguration> CREATOR = new Parcelable.Creator<RcsClientConfiguration>() { // from class: android.telephony.ims.RcsClientConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsClientConfiguration createFromParcel(Parcel in) {
            String rcsVersion = in.readString();
            String rcsProfile = in.readString();
            String clientVendor = in.readString();
            String clientVersion = in.readString();
            Boolean rcsEnabledByUser = Boolean.valueOf(in.readBoolean());
            return new RcsClientConfiguration(rcsVersion, rcsProfile, clientVendor, clientVersion, rcsEnabledByUser.booleanValue());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsClientConfiguration[] newArray(int size) {
            return new RcsClientConfiguration[size];
        }
    };
    public static final String RCS_PROFILE_1_0 = "UP_1.0";
    public static final String RCS_PROFILE_2_3 = "UP_2.3";
    public static final String RCS_PROFILE_2_4 = "UP_2.4";
    private String mClientVendor;
    private String mClientVersion;
    private boolean mRcsEnabledByUser;
    private String mRcsProfile;
    private String mRcsVersion;

    /* loaded from: classes3.dex */
    public @interface StringRcsProfile {
    }

    @Deprecated
    public RcsClientConfiguration(String rcsVersion, String rcsProfile, String clientVendor, String clientVersion) {
        this(rcsVersion, rcsProfile, clientVendor, clientVersion, true);
    }

    public RcsClientConfiguration(String rcsVersion, String rcsProfile, String clientVendor, String clientVersion, boolean isRcsEnabledByUser) {
        this.mRcsVersion = rcsVersion;
        this.mRcsProfile = rcsProfile;
        this.mClientVendor = clientVendor;
        this.mClientVersion = clientVersion;
        this.mRcsEnabledByUser = isRcsEnabledByUser;
    }

    public String getRcsVersion() {
        return this.mRcsVersion;
    }

    public String getRcsProfile() {
        return this.mRcsProfile;
    }

    public String getClientVendor() {
        return this.mClientVendor;
    }

    public String getClientVersion() {
        return this.mClientVersion;
    }

    public boolean isRcsEnabledByUser() {
        return this.mRcsEnabledByUser;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mRcsVersion);
        out.writeString(this.mRcsProfile);
        out.writeString(this.mClientVendor);
        out.writeString(this.mClientVersion);
        out.writeBoolean(this.mRcsEnabledByUser);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof RcsClientConfiguration) {
            RcsClientConfiguration other = (RcsClientConfiguration) obj;
            return this.mRcsVersion.equals(other.mRcsVersion) && this.mRcsProfile.equals(other.mRcsProfile) && this.mClientVendor.equals(other.mClientVendor) && this.mClientVersion.equals(other.mClientVersion) && this.mRcsEnabledByUser == other.mRcsEnabledByUser;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mRcsVersion, this.mRcsProfile, this.mClientVendor, this.mClientVersion, Boolean.valueOf(this.mRcsEnabledByUser));
    }
}
