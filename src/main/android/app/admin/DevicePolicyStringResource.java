package android.app.admin;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class DevicePolicyStringResource implements Parcelable {
    public static final Parcelable.Creator<DevicePolicyStringResource> CREATOR = new Parcelable.Creator<DevicePolicyStringResource>() { // from class: android.app.admin.DevicePolicyStringResource.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DevicePolicyStringResource createFromParcel(Parcel in) {
            String stringId = in.readString();
            int resourceIdInCallingPackage = in.readInt();
            ParcelableResource resource = (ParcelableResource) in.readTypedObject(ParcelableResource.CREATOR);
            return new DevicePolicyStringResource(stringId, resourceIdInCallingPackage, resource);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DevicePolicyStringResource[] newArray(int size) {
            return new DevicePolicyStringResource[size];
        }
    };
    private ParcelableResource mResource;
    private final int mResourceIdInCallingPackage;
    private final String mStringId;

    public DevicePolicyStringResource(Context context, String stringId, int resourceIdInCallingPackage) {
        this(stringId, resourceIdInCallingPackage, new ParcelableResource(context, resourceIdInCallingPackage, 2));
    }

    private DevicePolicyStringResource(String stringId, int resourceIdInCallingPackage, ParcelableResource resource) {
        Objects.requireNonNull(stringId, "stringId must be provided.");
        Objects.requireNonNull(resource, "ParcelableResource must be provided.");
        this.mStringId = stringId;
        this.mResourceIdInCallingPackage = resourceIdInCallingPackage;
        this.mResource = resource;
    }

    public String getStringId() {
        return this.mStringId;
    }

    public int getResourceIdInCallingPackage() {
        return this.mResourceIdInCallingPackage;
    }

    public ParcelableResource getResource() {
        return this.mResource;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DevicePolicyStringResource other = (DevicePolicyStringResource) o;
        if (this.mStringId == other.mStringId && this.mResourceIdInCallingPackage == other.mResourceIdInCallingPackage && this.mResource.equals(other.mResource)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mStringId, Integer.valueOf(this.mResourceIdInCallingPackage), this.mResource);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mStringId);
        dest.writeInt(this.mResourceIdInCallingPackage);
        dest.writeTypedObject(this.mResource, flags);
    }
}
