package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes3.dex */
public final class ImsRegistrationAttributes implements Parcelable {
    public static final int ATTR_EPDG_OVER_CELL_INTERNET = 1;
    public static final Parcelable.Creator<ImsRegistrationAttributes> CREATOR = new Parcelable.Creator<ImsRegistrationAttributes>() { // from class: android.telephony.ims.ImsRegistrationAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsRegistrationAttributes createFromParcel(Parcel source) {
            return new ImsRegistrationAttributes(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ImsRegistrationAttributes[] newArray(int size) {
            return new ImsRegistrationAttributes[size];
        }
    };
    private final ArrayList<String> mFeatureTags;
    private final int mImsAttributeFlags;
    private final int mRegistrationTech;
    private final SipDetails mSipDetails;
    private final int mTransportType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ImsAttributeFlag {
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static final class Builder {
        private Set<String> mFeatureTags = Collections.emptySet();
        private final int mRegistrationTech;
        private SipDetails mSipDetails;

        public Builder(int registrationTech) {
            this.mRegistrationTech = registrationTech;
        }

        public Builder setFeatureTags(Set<String> tags) {
            if (tags == null) {
                throw new IllegalArgumentException("feature tag set must not be null");
            }
            this.mFeatureTags = new ArraySet(tags);
            return this;
        }

        public Builder setSipDetails(SipDetails details) {
            this.mSipDetails = details;
            return this;
        }

        public ImsRegistrationAttributes build() {
            int i = this.mRegistrationTech;
            return new ImsRegistrationAttributes(i, RegistrationManager.getAccessType(i), getAttributeFlags(this.mRegistrationTech), this.mFeatureTags, this.mSipDetails);
        }

        private static int getAttributeFlags(int imsRadioTech) {
            if (imsRadioTech != 2) {
                return 0;
            }
            int attributes = 0 | 1;
            return attributes;
        }
    }

    public ImsRegistrationAttributes(int registrationTech, int transportType, int imsAttributeFlags, Set<String> featureTags) {
        this.mRegistrationTech = registrationTech;
        this.mTransportType = transportType;
        this.mImsAttributeFlags = imsAttributeFlags;
        this.mFeatureTags = new ArrayList<>(featureTags);
        this.mSipDetails = null;
    }

    public ImsRegistrationAttributes(int registrationTech, int transportType, int imsAttributeFlags, Set<String> featureTags, SipDetails details) {
        this.mRegistrationTech = registrationTech;
        this.mTransportType = transportType;
        this.mImsAttributeFlags = imsAttributeFlags;
        this.mFeatureTags = new ArrayList<>(featureTags);
        this.mSipDetails = details;
    }

    public ImsRegistrationAttributes(Parcel source) {
        this.mRegistrationTech = source.readInt();
        this.mTransportType = source.readInt();
        this.mImsAttributeFlags = source.readInt();
        ArrayList<String> arrayList = new ArrayList<>();
        this.mFeatureTags = arrayList;
        source.readList(arrayList, null, String.class);
        this.mSipDetails = (SipDetails) source.readParcelable(null, SipDetails.class);
    }

    @SystemApi
    public int getRegistrationTechnology() {
        return this.mRegistrationTech;
    }

    public int getTransportType() {
        return this.mTransportType;
    }

    public int getAttributeFlags() {
        return this.mImsAttributeFlags;
    }

    public Set<String> getFeatureTags() {
        if (this.mFeatureTags == null) {
            return Collections.emptySet();
        }
        return new ArraySet(this.mFeatureTags);
    }

    public SipDetails getSipDetails() {
        return this.mSipDetails;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRegistrationTech);
        dest.writeInt(this.mTransportType);
        dest.writeInt(this.mImsAttributeFlags);
        dest.writeList(this.mFeatureTags);
        dest.writeParcelable(this.mSipDetails, flags);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImsRegistrationAttributes that = (ImsRegistrationAttributes) o;
        if (this.mRegistrationTech == that.mRegistrationTech && this.mTransportType == that.mTransportType && this.mImsAttributeFlags == that.mImsAttributeFlags && Objects.equals(this.mFeatureTags, that.mFeatureTags) && Objects.equals(this.mSipDetails, that.mSipDetails)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mRegistrationTech), Integer.valueOf(this.mTransportType), Integer.valueOf(this.mImsAttributeFlags), this.mFeatureTags, this.mSipDetails);
    }

    public String toString() {
        return "ImsRegistrationAttributes { transportType= " + this.mTransportType + ", attributeFlags=" + this.mImsAttributeFlags + ", featureTags=[" + this.mFeatureTags + "],SipDetails=" + this.mSipDetails + "}";
    }
}
