package android.app.admin;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import java.util.Locale;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class FullyManagedDeviceProvisioningParams implements Parcelable {
    private static final String CAN_DEVICE_OWNER_GRANT_SENSOR_PERMISSIONS_PARAM = "CAN_DEVICE_OWNER_GRANT_SENSOR_PERMISSIONS";
    public static final Parcelable.Creator<FullyManagedDeviceProvisioningParams> CREATOR = new Parcelable.Creator<FullyManagedDeviceProvisioningParams>() { // from class: android.app.admin.FullyManagedDeviceProvisioningParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FullyManagedDeviceProvisioningParams createFromParcel(Parcel in) {
            ComponentName componentName = (ComponentName) in.readTypedObject(ComponentName.CREATOR);
            String ownerName = in.readString();
            boolean leaveAllSystemAppsEnabled = in.readBoolean();
            String timeZone = in.readString();
            long localtime = in.readLong();
            String locale = in.readString();
            boolean deviceOwnerCanGrantSensorsPermissions = in.readBoolean();
            PersistableBundle adminExtras = in.readPersistableBundle();
            boolean demoDevice = in.readBoolean();
            return new FullyManagedDeviceProvisioningParams(componentName, ownerName, leaveAllSystemAppsEnabled, timeZone, localtime, locale, deviceOwnerCanGrantSensorsPermissions, adminExtras, demoDevice);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FullyManagedDeviceProvisioningParams[] newArray(int size) {
            return new FullyManagedDeviceProvisioningParams[size];
        }
    };
    private static final String DEMO_DEVICE = "DEMO_DEVICE";
    private static final String LEAVE_ALL_SYSTEM_APPS_ENABLED_PARAM = "LEAVE_ALL_SYSTEM_APPS_ENABLED";
    private static final String LOCALE_PROVIDED_PARAM = "LOCALE_PROVIDED";
    private static final String TIME_ZONE_PROVIDED_PARAM = "TIME_ZONE_PROVIDED";
    private final PersistableBundle mAdminExtras;
    private final boolean mDemoDevice;
    private final ComponentName mDeviceAdminComponentName;
    private final boolean mDeviceOwnerCanGrantSensorsPermissions;
    private final boolean mLeaveAllSystemAppsEnabled;
    private final long mLocalTime;
    private final Locale mLocale;
    private final String mOwnerName;
    private final String mTimeZone;

    private FullyManagedDeviceProvisioningParams(ComponentName deviceAdminComponentName, String ownerName, boolean leaveAllSystemAppsEnabled, String timeZone, long localTime, Locale locale, boolean deviceOwnerCanGrantSensorsPermissions, PersistableBundle adminExtras, boolean demoDevice) {
        this.mDeviceAdminComponentName = (ComponentName) Objects.requireNonNull(deviceAdminComponentName);
        this.mOwnerName = (String) Objects.requireNonNull(ownerName);
        this.mLeaveAllSystemAppsEnabled = leaveAllSystemAppsEnabled;
        this.mTimeZone = timeZone;
        this.mLocalTime = localTime;
        this.mLocale = locale;
        this.mDeviceOwnerCanGrantSensorsPermissions = deviceOwnerCanGrantSensorsPermissions;
        this.mAdminExtras = adminExtras;
        this.mDemoDevice = demoDevice;
    }

    private FullyManagedDeviceProvisioningParams(ComponentName deviceAdminComponentName, String ownerName, boolean leaveAllSystemAppsEnabled, String timeZone, long localTime, String localeStr, boolean deviceOwnerCanGrantSensorsPermissions, PersistableBundle adminExtras, boolean demoDevice) {
        this(deviceAdminComponentName, ownerName, leaveAllSystemAppsEnabled, timeZone, localTime, getLocale(localeStr), deviceOwnerCanGrantSensorsPermissions, adminExtras, demoDevice);
    }

    private static Locale getLocale(String localeStr) {
        if (localeStr == null) {
            return null;
        }
        return Locale.forLanguageTag(localeStr);
    }

    public ComponentName getDeviceAdminComponentName() {
        return this.mDeviceAdminComponentName;
    }

    public String getOwnerName() {
        return this.mOwnerName;
    }

    public boolean isLeaveAllSystemAppsEnabled() {
        return this.mLeaveAllSystemAppsEnabled;
    }

    public String getTimeZone() {
        return this.mTimeZone;
    }

    public long getLocalTime() {
        return this.mLocalTime;
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public boolean canDeviceOwnerGrantSensorsPermissions() {
        return this.mDeviceOwnerCanGrantSensorsPermissions;
    }

    public PersistableBundle getAdminExtras() {
        return new PersistableBundle(this.mAdminExtras);
    }

    public boolean isDemoDevice() {
        return this.mDemoDevice;
    }

    public void logParams(String callerPackage) {
        Objects.requireNonNull(callerPackage);
        logParam(callerPackage, LEAVE_ALL_SYSTEM_APPS_ENABLED_PARAM, this.mLeaveAllSystemAppsEnabled);
        logParam(callerPackage, CAN_DEVICE_OWNER_GRANT_SENSOR_PERMISSIONS_PARAM, this.mDeviceOwnerCanGrantSensorsPermissions);
        logParam(callerPackage, TIME_ZONE_PROVIDED_PARAM, this.mTimeZone != null);
        logParam(callerPackage, LOCALE_PROVIDED_PARAM, this.mLocale != null);
        logParam(callerPackage, DEMO_DEVICE, this.mDemoDevice);
    }

    private void logParam(String callerPackage, String param, boolean value) {
        DevicePolicyEventLogger.createEvent(197).setStrings(callerPackage).setAdmin(this.mDeviceAdminComponentName).setStrings(param).setBoolean(value).write();
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private PersistableBundle mAdminExtras;
        private final ComponentName mDeviceAdminComponentName;
        private boolean mLeaveAllSystemAppsEnabled;
        private long mLocalTime;
        private Locale mLocale;
        private final String mOwnerName;
        private String mTimeZone;
        boolean mDeviceOwnerCanGrantSensorsPermissions = true;
        boolean mDemoDevice = false;

        public Builder(ComponentName deviceAdminComponentName, String ownerName) {
            this.mDeviceAdminComponentName = (ComponentName) Objects.requireNonNull(deviceAdminComponentName);
            this.mOwnerName = (String) Objects.requireNonNull(ownerName);
        }

        public Builder setLeaveAllSystemAppsEnabled(boolean leaveAllSystemAppsEnabled) {
            this.mLeaveAllSystemAppsEnabled = leaveAllSystemAppsEnabled;
            return this;
        }

        public Builder setTimeZone(String timeZone) {
            this.mTimeZone = timeZone;
            return this;
        }

        public Builder setLocalTime(long localTime) {
            this.mLocalTime = localTime;
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.mLocale = locale;
            return this;
        }

        public Builder setCanDeviceOwnerGrantSensorsPermissions(boolean mayGrant) {
            this.mDeviceOwnerCanGrantSensorsPermissions = mayGrant;
            return this;
        }

        public Builder setAdminExtras(PersistableBundle adminExtras) {
            PersistableBundle persistableBundle;
            if (adminExtras != null) {
                persistableBundle = new PersistableBundle(adminExtras);
            } else {
                persistableBundle = new PersistableBundle();
            }
            this.mAdminExtras = persistableBundle;
            return this;
        }

        public Builder setDemoDevice(boolean demoDevice) {
            this.mDemoDevice = demoDevice;
            return this;
        }

        public FullyManagedDeviceProvisioningParams build() {
            ComponentName componentName = this.mDeviceAdminComponentName;
            String str = this.mOwnerName;
            boolean z = this.mLeaveAllSystemAppsEnabled;
            String str2 = this.mTimeZone;
            long j = this.mLocalTime;
            Locale locale = this.mLocale;
            boolean z2 = this.mDeviceOwnerCanGrantSensorsPermissions;
            PersistableBundle persistableBundle = this.mAdminExtras;
            if (persistableBundle == null) {
                persistableBundle = new PersistableBundle();
            }
            return new FullyManagedDeviceProvisioningParams(componentName, str, z, str2, j, locale, z2, persistableBundle, this.mDemoDevice);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder append = new StringBuilder().append("FullyManagedDeviceProvisioningParams{mDeviceAdminComponentName=").append(this.mDeviceAdminComponentName).append(", mOwnerName=").append(this.mOwnerName).append(", mLeaveAllSystemAppsEnabled=").append(this.mLeaveAllSystemAppsEnabled).append(", mTimeZone=");
        String str = this.mTimeZone;
        if (str == null) {
            str = "null";
        }
        StringBuilder append2 = append.append(str).append(", mLocalTime=").append(this.mLocalTime).append(", mLocale=");
        Locale locale = this.mLocale;
        return append2.append(locale != null ? locale : "null").append(", mDeviceOwnerCanGrantSensorsPermissions=").append(this.mDeviceOwnerCanGrantSensorsPermissions).append(", mAdminExtras=").append(this.mAdminExtras).append(", mDemoDevice=").append(this.mDemoDevice).append('}').toString();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mDeviceAdminComponentName, flags);
        dest.writeString(this.mOwnerName);
        dest.writeBoolean(this.mLeaveAllSystemAppsEnabled);
        dest.writeString(this.mTimeZone);
        dest.writeLong(this.mLocalTime);
        Locale locale = this.mLocale;
        dest.writeString(locale == null ? null : locale.toLanguageTag());
        dest.writeBoolean(this.mDeviceOwnerCanGrantSensorsPermissions);
        dest.writePersistableBundle(this.mAdminExtras);
        dest.writeBoolean(this.mDemoDevice);
    }
}
