package android.companion;

import android.annotation.NonNull;
import android.annotation.UserIdInt;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.provider.OneTimeUseBuilder;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.CollectionUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AssociationRequest implements Parcelable {
    public static final Parcelable.Creator<AssociationRequest> CREATOR = new Parcelable.Creator<AssociationRequest>() { // from class: android.companion.AssociationRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AssociationRequest[] newArray(int size) {
            return new AssociationRequest[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AssociationRequest createFromParcel(Parcel in) {
            return new AssociationRequest(in);
        }
    };
    public static final String DEVICE_PROFILE_APP_STREAMING = "android.app.role.COMPANION_DEVICE_APP_STREAMING";
    public static final String DEVICE_PROFILE_AUTOMOTIVE_PROJECTION = "android.app.role.SYSTEM_AUTOMOTIVE_PROJECTION";
    public static final String DEVICE_PROFILE_COMPUTER = "android.app.role.COMPANION_DEVICE_COMPUTER";
    public static final String DEVICE_PROFILE_GLASSES = "android.app.role.COMPANION_DEVICE_GLASSES";
    public static final String DEVICE_PROFILE_NEARBY_DEVICE_STREAMING = "android.app.role.COMPANION_DEVICE_NEARBY_DEVICE_STREAMING";
    public static final String DEVICE_PROFILE_WATCH = "android.app.role.COMPANION_DEVICE_WATCH";
    private AssociatedDevice mAssociatedDevice;
    private final long mCreationTime;
    private final List<DeviceFilter<?>> mDeviceFilters;
    private final String mDeviceProfile;
    private String mDeviceProfilePrivilegesDescription;
    private CharSequence mDisplayName;
    private final boolean mForceConfirmation;
    private String mPackageName;
    private final boolean mSelfManaged;
    private final boolean mSingleDevice;
    private boolean mSkipPrompt;
    private int mUserId;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DeviceProfile {
    }

    private AssociationRequest(boolean singleDevice, List<DeviceFilter<?>> deviceFilters, String deviceProfile, CharSequence displayName, boolean selfManaged, boolean forceConfirmation) {
        this.mSingleDevice = singleDevice;
        this.mDeviceFilters = (List) Objects.requireNonNull(deviceFilters);
        this.mDeviceProfile = deviceProfile;
        this.mDisplayName = displayName;
        this.mSelfManaged = selfManaged;
        this.mForceConfirmation = forceConfirmation;
        this.mCreationTime = System.currentTimeMillis();
    }

    public String getDeviceProfile() {
        return this.mDeviceProfile;
    }

    public CharSequence getDisplayName() {
        return this.mDisplayName;
    }

    public boolean isSelfManaged() {
        return this.mSelfManaged;
    }

    public boolean isForceConfirmation() {
        return this.mForceConfirmation;
    }

    public boolean isSingleDevice() {
        return this.mSingleDevice;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public void setUserId(int userId) {
        this.mUserId = userId;
    }

    public void setDeviceProfilePrivilegesDescription(String desc) {
        this.mDeviceProfilePrivilegesDescription = desc;
    }

    public void setSkipPrompt(boolean value) {
        this.mSkipPrompt = value;
    }

    public void setDisplayName(CharSequence displayName) {
        this.mDisplayName = displayName;
    }

    public void setAssociatedDevice(AssociatedDevice associatedDevice) {
        this.mAssociatedDevice = associatedDevice;
    }

    public List<DeviceFilter<?>> getDeviceFilters() {
        return this.mDeviceFilters;
    }

    /* loaded from: classes.dex */
    public static final class Builder extends OneTimeUseBuilder<AssociationRequest> {
        private String mDeviceProfile;
        private CharSequence mDisplayName;
        private boolean mSingleDevice = false;
        private ArrayList<DeviceFilter<?>> mDeviceFilters = null;
        private boolean mSelfManaged = false;
        private boolean mForceConfirmation = false;

        public Builder setSingleDevice(boolean singleDevice) {
            checkNotUsed();
            this.mSingleDevice = singleDevice;
            return this;
        }

        public Builder addDeviceFilter(DeviceFilter<?> deviceFilter) {
            checkNotUsed();
            if (deviceFilter != null) {
                this.mDeviceFilters = ArrayUtils.add(this.mDeviceFilters, deviceFilter);
            }
            return this;
        }

        public Builder setDeviceProfile(String deviceProfile) {
            checkNotUsed();
            this.mDeviceProfile = deviceProfile;
            return this;
        }

        public Builder setDisplayName(CharSequence displayName) {
            checkNotUsed();
            this.mDisplayName = (CharSequence) Objects.requireNonNull(displayName);
            return this;
        }

        public Builder setSelfManaged(boolean selfManaged) {
            checkNotUsed();
            this.mSelfManaged = selfManaged;
            return this;
        }

        public Builder setForceConfirmation(boolean forceConfirmation) {
            checkNotUsed();
            this.mForceConfirmation = forceConfirmation;
            return this;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.provider.OneTimeUseBuilder
        public AssociationRequest build() {
            markUsed();
            if (this.mSelfManaged && this.mDisplayName == null) {
                throw new IllegalStateException("Request for a self-managed association MUST provide the display name of the device");
            }
            return new AssociationRequest(this.mSingleDevice, CollectionUtils.emptyIfNull(this.mDeviceFilters), this.mDeviceProfile, this.mDisplayName, this.mSelfManaged, this.mForceConfirmation);
        }
    }

    public AssociatedDevice getAssociatedDevice() {
        return this.mAssociatedDevice;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public String getDeviceProfilePrivilegesDescription() {
        return this.mDeviceProfilePrivilegesDescription;
    }

    public long getCreationTime() {
        return this.mCreationTime;
    }

    public boolean isSkipPrompt() {
        return this.mSkipPrompt;
    }

    public String toString() {
        return "AssociationRequest { singleDevice = " + this.mSingleDevice + ", deviceFilters = " + this.mDeviceFilters + ", deviceProfile = " + this.mDeviceProfile + ", displayName = " + ((Object) this.mDisplayName) + ", associatedDevice = " + this.mAssociatedDevice + ", selfManaged = " + this.mSelfManaged + ", forceConfirmation = " + this.mForceConfirmation + ", packageName = " + this.mPackageName + ", userId = " + this.mUserId + ", deviceProfilePrivilegesDescription = " + this.mDeviceProfilePrivilegesDescription + ", creationTime = " + this.mCreationTime + ", skipPrompt = " + this.mSkipPrompt + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssociationRequest that = (AssociationRequest) o;
        if (this.mSingleDevice == that.mSingleDevice && Objects.equals(this.mDeviceFilters, that.mDeviceFilters) && Objects.equals(this.mDeviceProfile, that.mDeviceProfile) && Objects.equals(this.mDisplayName, that.mDisplayName) && Objects.equals(this.mAssociatedDevice, that.mAssociatedDevice) && this.mSelfManaged == that.mSelfManaged && this.mForceConfirmation == that.mForceConfirmation && Objects.equals(this.mPackageName, that.mPackageName) && this.mUserId == that.mUserId && Objects.equals(this.mDeviceProfilePrivilegesDescription, that.mDeviceProfilePrivilegesDescription) && this.mCreationTime == that.mCreationTime && this.mSkipPrompt == that.mSkipPrompt) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Boolean.hashCode(this.mSingleDevice);
        return (((((((((((((((((((((_hash * 31) + Objects.hashCode(this.mDeviceFilters)) * 31) + Objects.hashCode(this.mDeviceProfile)) * 31) + Objects.hashCode(this.mDisplayName)) * 31) + Objects.hashCode(this.mAssociatedDevice)) * 31) + Boolean.hashCode(this.mSelfManaged)) * 31) + Boolean.hashCode(this.mForceConfirmation)) * 31) + Objects.hashCode(this.mPackageName)) * 31) + this.mUserId) * 31) + Objects.hashCode(this.mDeviceProfilePrivilegesDescription)) * 31) + Long.hashCode(this.mCreationTime)) * 31) + Boolean.hashCode(this.mSkipPrompt);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int flg = this.mSingleDevice ? 0 | 1 : 0;
        if (this.mSelfManaged) {
            flg |= 2;
        }
        if (this.mForceConfirmation) {
            flg |= 4;
        }
        if (this.mSkipPrompt) {
            flg |= 8;
        }
        if (this.mDeviceProfile != null) {
            flg |= 16;
        }
        if (this.mDisplayName != null) {
            flg |= 32;
        }
        if (this.mAssociatedDevice != null) {
            flg |= 64;
        }
        if (this.mPackageName != null) {
            flg |= 128;
        }
        if (this.mDeviceProfilePrivilegesDescription != null) {
            flg |= 256;
        }
        dest.writeInt(flg);
        dest.writeParcelableList(this.mDeviceFilters, flags);
        String str = this.mDeviceProfile;
        if (str != null) {
            dest.writeString(str);
        }
        CharSequence charSequence = this.mDisplayName;
        if (charSequence != null) {
            dest.writeCharSequence(charSequence);
        }
        AssociatedDevice associatedDevice = this.mAssociatedDevice;
        if (associatedDevice != null) {
            dest.writeTypedObject(associatedDevice, flags);
        }
        String str2 = this.mPackageName;
        if (str2 != null) {
            dest.writeString(str2);
        }
        dest.writeInt(this.mUserId);
        String str3 = this.mDeviceProfilePrivilegesDescription;
        if (str3 != null) {
            dest.writeString8(str3);
        }
        dest.writeLong(this.mCreationTime);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    AssociationRequest(Parcel in) {
        int flg = in.readInt();
        boolean singleDevice = (flg & 1) != 0;
        boolean selfManaged = (flg & 2) != 0;
        boolean forceConfirmation = (flg & 4) != 0;
        boolean skipPrompt = (flg & 8) != 0;
        ArrayList arrayList = new ArrayList();
        in.readParcelableList(arrayList, DeviceFilter.class.getClassLoader(), DeviceFilter.class);
        String deviceProfile = (flg & 16) == 0 ? null : in.readString();
        CharSequence displayName = (flg & 32) == 0 ? null : in.readCharSequence();
        AssociatedDevice associatedDevice = (flg & 64) == 0 ? null : (AssociatedDevice) in.readTypedObject(AssociatedDevice.CREATOR);
        String packageName = (flg & 128) == 0 ? null : in.readString();
        int userId = in.readInt();
        String deviceProfilePrivilegesDescription = (flg & 256) == 0 ? null : in.readString8();
        long creationTime = in.readLong();
        this.mSingleDevice = singleDevice;
        this.mDeviceFilters = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
        this.mDeviceProfile = deviceProfile;
        this.mDisplayName = displayName;
        this.mAssociatedDevice = associatedDevice;
        this.mSelfManaged = selfManaged;
        this.mForceConfirmation = forceConfirmation;
        this.mPackageName = packageName;
        this.mUserId = userId;
        AnnotationValidations.validate((Class<UserIdInt>) UserIdInt.class, (UserIdInt) null, userId);
        this.mDeviceProfilePrivilegesDescription = deviceProfilePrivilegesDescription;
        this.mCreationTime = creationTime;
        this.mSkipPrompt = skipPrompt;
    }
}
