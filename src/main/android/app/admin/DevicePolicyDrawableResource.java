package android.app.admin;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class DevicePolicyDrawableResource implements Parcelable {
    public static final Parcelable.Creator<DevicePolicyDrawableResource> CREATOR = new Parcelable.Creator<DevicePolicyDrawableResource>() { // from class: android.app.admin.DevicePolicyDrawableResource.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DevicePolicyDrawableResource createFromParcel(Parcel in) {
            String drawableId = in.readString();
            String drawableStyle = in.readString();
            String drawableSource = in.readString();
            int resourceIdInCallingPackage = in.readInt();
            ParcelableResource resource = (ParcelableResource) in.readTypedObject(ParcelableResource.CREATOR);
            return new DevicePolicyDrawableResource(drawableId, drawableStyle, drawableSource, resourceIdInCallingPackage, resource);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DevicePolicyDrawableResource[] newArray(int size) {
            return new DevicePolicyDrawableResource[size];
        }
    };
    private final String mDrawableId;
    private final String mDrawableSource;
    private final String mDrawableStyle;
    private ParcelableResource mResource;
    private final int mResourceIdInCallingPackage;

    public DevicePolicyDrawableResource(Context context, String drawableId, String drawableStyle, String drawableSource, int resourceIdInCallingPackage) {
        this(drawableId, drawableStyle, drawableSource, resourceIdInCallingPackage, new ParcelableResource(context, resourceIdInCallingPackage, 1));
    }

    private DevicePolicyDrawableResource(String drawableId, String drawableStyle, String drawableSource, int resourceIdInCallingPackage, ParcelableResource resource) {
        Objects.requireNonNull(drawableId);
        Objects.requireNonNull(drawableStyle);
        Objects.requireNonNull(drawableSource);
        Objects.requireNonNull(resource);
        this.mDrawableId = drawableId;
        this.mDrawableStyle = drawableStyle;
        this.mDrawableSource = drawableSource;
        this.mResourceIdInCallingPackage = resourceIdInCallingPackage;
        this.mResource = resource;
    }

    public DevicePolicyDrawableResource(Context context, String drawableId, String drawableStyle, int resourceIdInCallingPackage) {
        this(context, drawableId, drawableStyle, DevicePolicyResources.UNDEFINED, resourceIdInCallingPackage);
    }

    public String getDrawableId() {
        return this.mDrawableId;
    }

    public String getDrawableStyle() {
        return this.mDrawableStyle;
    }

    public String getDrawableSource() {
        return this.mDrawableSource;
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
        DevicePolicyDrawableResource other = (DevicePolicyDrawableResource) o;
        if (this.mDrawableId.equals(other.mDrawableId) && this.mDrawableStyle.equals(other.mDrawableStyle) && this.mDrawableSource.equals(other.mDrawableSource) && this.mResourceIdInCallingPackage == other.mResourceIdInCallingPackage && this.mResource.equals(other.mResource)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mDrawableId, this.mDrawableStyle, this.mDrawableSource, Integer.valueOf(this.mResourceIdInCallingPackage), this.mResource);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDrawableId);
        dest.writeString(this.mDrawableStyle);
        dest.writeString(this.mDrawableSource);
        dest.writeInt(this.mResourceIdInCallingPackage);
        dest.writeTypedObject(this.mResource, flags);
    }
}
