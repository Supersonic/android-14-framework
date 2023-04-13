package android.net.wifi;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.Environment;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.provider.Settings;
import android.util.AtomicFile;
import android.util.SparseArray;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class WifiMigration {
    private static final String LEGACY_WIFI_STORE_DIRECTORY_NAME = "wifi";
    public static final int STORE_FILE_SHARED_GENERAL = 0;
    public static final int STORE_FILE_SHARED_SOFTAP = 1;
    public static final int STORE_FILE_USER_GENERAL = 2;
    public static final int STORE_FILE_USER_NETWORK_SUGGESTIONS = 3;
    private static final SparseArray<String> STORE_ID_TO_FILE_NAME = new SparseArray<String>() { // from class: android.net.wifi.WifiMigration.1
        {
            put(0, "WifiConfigStore.xml");
            put(1, "WifiConfigStoreSoftAp.xml");
            put(2, "WifiConfigStore.xml");
            put(3, "WifiConfigStoreNetworkSuggestions.xml");
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SharedStoreFileId {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface UserStoreFileId {
    }

    private static File getLegacyWifiSharedDirectory() {
        return new File(Environment.getDataMiscDirectory(), "wifi");
    }

    private static File getLegacyWifiUserDirectory(int userId) {
        return new File(Environment.getDataMiscCeDirectory(userId), "wifi");
    }

    private static AtomicFile getSharedAtomicFile(int storeFileId) {
        return new AtomicFile(new File(getLegacyWifiSharedDirectory(), STORE_ID_TO_FILE_NAME.get(storeFileId)));
    }

    private static AtomicFile getUserAtomicFile(int storeFileId, int userId) {
        return new AtomicFile(new File(getLegacyWifiUserDirectory(userId), STORE_ID_TO_FILE_NAME.get(storeFileId)));
    }

    private WifiMigration() {
    }

    public static InputStream convertAndRetrieveSharedConfigStoreFile(int storeFileId) {
        if (storeFileId != 0 && storeFileId != 1) {
            throw new IllegalArgumentException("Invalid shared store file id");
        }
        try {
            return getSharedAtomicFile(storeFileId).openRead();
        } catch (FileNotFoundException e) {
            if (storeFileId == 1) {
                return SoftApConfToXmlMigrationUtil.convert();
            }
            return null;
        }
    }

    public static void removeSharedConfigStoreFile(int storeFileId) {
        if (storeFileId != 0 && storeFileId != 1) {
            throw new IllegalArgumentException("Invalid shared store file id");
        }
        AtomicFile file = getSharedAtomicFile(storeFileId);
        if (file.exists()) {
            file.delete();
        } else if (storeFileId == 1) {
            SoftApConfToXmlMigrationUtil.remove();
        }
    }

    public static InputStream convertAndRetrieveUserConfigStoreFile(int storeFileId, UserHandle userHandle) {
        if (storeFileId != 2 && storeFileId != 3) {
            throw new IllegalArgumentException("Invalid user store file id");
        }
        Objects.requireNonNull(userHandle);
        try {
            return getUserAtomicFile(storeFileId, userHandle.getIdentifier()).openRead();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void removeUserConfigStoreFile(int storeFileId, UserHandle userHandle) {
        if (storeFileId != 2 && storeFileId != 3) {
            throw new IllegalArgumentException("Invalid user store file id");
        }
        Objects.requireNonNull(userHandle);
        AtomicFile file = getUserAtomicFile(storeFileId, userHandle.getIdentifier());
        if (file.exists()) {
            file.delete();
        }
    }

    /* loaded from: classes2.dex */
    public static final class SettingsMigrationData implements Parcelable {
        public static final Parcelable.Creator<SettingsMigrationData> CREATOR = new Parcelable.Creator<SettingsMigrationData>() { // from class: android.net.wifi.WifiMigration.SettingsMigrationData.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SettingsMigrationData createFromParcel(Parcel in) {
                boolean scanAlwaysAvailable = in.readBoolean();
                boolean p2pFactoryResetPending = in.readBoolean();
                String p2pDeviceName = in.readString();
                boolean softApTimeoutEnabled = in.readBoolean();
                boolean wakeupEnabled = in.readBoolean();
                boolean scanThrottleEnabled = in.readBoolean();
                boolean verboseLoggingEnabled = in.readBoolean();
                return new SettingsMigrationData(scanAlwaysAvailable, p2pFactoryResetPending, p2pDeviceName, softApTimeoutEnabled, wakeupEnabled, scanThrottleEnabled, verboseLoggingEnabled);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SettingsMigrationData[] newArray(int size) {
                return new SettingsMigrationData[size];
            }
        };
        private final String mP2pDeviceName;
        private final boolean mP2pFactoryResetPending;
        private final boolean mScanAlwaysAvailable;
        private final boolean mScanThrottleEnabled;
        private final boolean mSoftApTimeoutEnabled;
        private final boolean mVerboseLoggingEnabled;
        private final boolean mWakeupEnabled;

        private SettingsMigrationData(boolean scanAlwaysAvailable, boolean p2pFactoryResetPending, String p2pDeviceName, boolean softApTimeoutEnabled, boolean wakeupEnabled, boolean scanThrottleEnabled, boolean verboseLoggingEnabled) {
            this.mScanAlwaysAvailable = scanAlwaysAvailable;
            this.mP2pFactoryResetPending = p2pFactoryResetPending;
            this.mP2pDeviceName = p2pDeviceName;
            this.mSoftApTimeoutEnabled = softApTimeoutEnabled;
            this.mWakeupEnabled = wakeupEnabled;
            this.mScanThrottleEnabled = scanThrottleEnabled;
            this.mVerboseLoggingEnabled = verboseLoggingEnabled;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeBoolean(this.mScanAlwaysAvailable);
            dest.writeBoolean(this.mP2pFactoryResetPending);
            dest.writeString(this.mP2pDeviceName);
            dest.writeBoolean(this.mSoftApTimeoutEnabled);
            dest.writeBoolean(this.mWakeupEnabled);
            dest.writeBoolean(this.mScanThrottleEnabled);
            dest.writeBoolean(this.mVerboseLoggingEnabled);
        }

        public boolean isScanAlwaysAvailable() {
            return this.mScanAlwaysAvailable;
        }

        public boolean isP2pFactoryResetPending() {
            return this.mP2pFactoryResetPending;
        }

        public String getP2pDeviceName() {
            return this.mP2pDeviceName;
        }

        public boolean isSoftApTimeoutEnabled() {
            return this.mSoftApTimeoutEnabled;
        }

        public boolean isWakeUpEnabled() {
            return this.mWakeupEnabled;
        }

        public boolean isScanThrottleEnabled() {
            return this.mScanThrottleEnabled;
        }

        public boolean isVerboseLoggingEnabled() {
            return this.mVerboseLoggingEnabled;
        }

        /* loaded from: classes2.dex */
        public static final class Builder {
            private String mP2pDeviceName;
            private boolean mP2pFactoryResetPending;
            private boolean mScanAlwaysAvailable;
            private boolean mScanThrottleEnabled;
            private boolean mSoftApTimeoutEnabled;
            private boolean mVerboseLoggingEnabled;
            private boolean mWakeupEnabled;

            public Builder setScanAlwaysAvailable(boolean available) {
                this.mScanAlwaysAvailable = available;
                return this;
            }

            public Builder setP2pFactoryResetPending(boolean pending) {
                this.mP2pFactoryResetPending = pending;
                return this;
            }

            public Builder setP2pDeviceName(String name) {
                this.mP2pDeviceName = name;
                return this;
            }

            public Builder setSoftApTimeoutEnabled(boolean enabled) {
                this.mSoftApTimeoutEnabled = enabled;
                return this;
            }

            public Builder setWakeUpEnabled(boolean enabled) {
                this.mWakeupEnabled = enabled;
                return this;
            }

            public Builder setScanThrottleEnabled(boolean enabled) {
                this.mScanThrottleEnabled = enabled;
                return this;
            }

            public Builder setVerboseLoggingEnabled(boolean enabled) {
                this.mVerboseLoggingEnabled = enabled;
                return this;
            }

            public SettingsMigrationData build() {
                return new SettingsMigrationData(this.mScanAlwaysAvailable, this.mP2pFactoryResetPending, this.mP2pDeviceName, this.mSoftApTimeoutEnabled, this.mWakeupEnabled, this.mScanThrottleEnabled, this.mVerboseLoggingEnabled);
            }
        }
    }

    public static SettingsMigrationData loadFromSettings(Context context) {
        if (Settings.Global.getInt(context.getContentResolver(), Settings.Global.WIFI_MIGRATION_COMPLETED, 0) == 1) {
            return null;
        }
        SettingsMigrationData data = new SettingsMigrationData.Builder().setScanAlwaysAvailable(Settings.Global.getInt(context.getContentResolver(), Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE, 0) == 1).setP2pFactoryResetPending(Settings.Global.getInt(context.getContentResolver(), Settings.Global.WIFI_P2P_PENDING_FACTORY_RESET, 0) == 1).setP2pDeviceName(Settings.Global.getString(context.getContentResolver(), Settings.Global.WIFI_P2P_DEVICE_NAME)).setSoftApTimeoutEnabled(Settings.Global.getInt(context.getContentResolver(), Settings.Global.SOFT_AP_TIMEOUT_ENABLED, 1) == 1).setWakeUpEnabled(Settings.Global.getInt(context.getContentResolver(), Settings.Global.WIFI_WAKEUP_ENABLED, 0) == 1).setScanThrottleEnabled(Settings.Global.getInt(context.getContentResolver(), Settings.Global.WIFI_SCAN_THROTTLE_ENABLED, 1) == 1).setVerboseLoggingEnabled(Settings.Global.getInt(context.getContentResolver(), Settings.Global.WIFI_VERBOSE_LOGGING_ENABLED, 0) == 1).build();
        Settings.Global.putInt(context.getContentResolver(), Settings.Global.WIFI_MIGRATION_COMPLETED, 1);
        return data;
    }
}
