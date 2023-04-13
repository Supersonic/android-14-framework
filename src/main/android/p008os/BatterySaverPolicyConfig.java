package android.p008os;

import android.annotation.SystemApi;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
@SystemApi
/* renamed from: android.os.BatterySaverPolicyConfig */
/* loaded from: classes3.dex */
public final class BatterySaverPolicyConfig implements Parcelable {
    public static final Parcelable.Creator<BatterySaverPolicyConfig> CREATOR = new Parcelable.Creator<BatterySaverPolicyConfig>() { // from class: android.os.BatterySaverPolicyConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BatterySaverPolicyConfig createFromParcel(Parcel in) {
            return new BatterySaverPolicyConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BatterySaverPolicyConfig[] newArray(int size) {
            return new BatterySaverPolicyConfig[size];
        }
    };
    private final float mAdjustBrightnessFactor;
    private final boolean mAdvertiseIsEnabled;
    private final boolean mDeferFullBackup;
    private final boolean mDeferKeyValueBackup;
    private final Map<String, String> mDeviceSpecificSettings;
    private final boolean mDisableAnimation;
    private final boolean mDisableAod;
    private final boolean mDisableLaunchBoost;
    private final boolean mDisableOptionalSensors;
    private final boolean mDisableVibration;
    private final boolean mEnableAdjustBrightness;
    private final boolean mEnableDataSaver;
    private final boolean mEnableFirewall;
    private final boolean mEnableNightMode;
    private final boolean mEnableQuickDoze;
    private final boolean mForceAllAppsStandby;
    private final boolean mForceBackgroundCheck;
    private final int mLocationMode;
    private final int mSoundTriggerMode;

    private BatterySaverPolicyConfig(Builder in) {
        this.mAdjustBrightnessFactor = Math.max(0.0f, Math.min(in.mAdjustBrightnessFactor, 1.0f));
        this.mAdvertiseIsEnabled = in.mAdvertiseIsEnabled;
        this.mDeferFullBackup = in.mDeferFullBackup;
        this.mDeferKeyValueBackup = in.mDeferKeyValueBackup;
        this.mDeviceSpecificSettings = Collections.unmodifiableMap(new ArrayMap(in.mDeviceSpecificSettings));
        this.mDisableAnimation = in.mDisableAnimation;
        this.mDisableAod = in.mDisableAod;
        this.mDisableLaunchBoost = in.mDisableLaunchBoost;
        this.mDisableOptionalSensors = in.mDisableOptionalSensors;
        this.mDisableVibration = in.mDisableVibration;
        this.mEnableAdjustBrightness = in.mEnableAdjustBrightness;
        this.mEnableDataSaver = in.mEnableDataSaver;
        this.mEnableFirewall = in.mEnableFirewall;
        this.mEnableNightMode = in.mEnableNightMode;
        this.mEnableQuickDoze = in.mEnableQuickDoze;
        this.mForceAllAppsStandby = in.mForceAllAppsStandby;
        this.mForceBackgroundCheck = in.mForceBackgroundCheck;
        this.mLocationMode = Math.max(0, Math.min(in.mLocationMode, 4));
        this.mSoundTriggerMode = Math.max(0, Math.min(in.mSoundTriggerMode, 2));
    }

    private BatterySaverPolicyConfig(Parcel in) {
        this.mAdjustBrightnessFactor = Math.max(0.0f, Math.min(in.readFloat(), 1.0f));
        this.mAdvertiseIsEnabled = in.readBoolean();
        this.mDeferFullBackup = in.readBoolean();
        this.mDeferKeyValueBackup = in.readBoolean();
        int size = in.readInt();
        Map<String, String> deviceSpecificSettings = new ArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            String key = TextUtils.emptyIfNull(in.readString());
            String val = TextUtils.emptyIfNull(in.readString());
            if (!key.trim().isEmpty()) {
                deviceSpecificSettings.put(key, val);
            }
        }
        this.mDeviceSpecificSettings = Collections.unmodifiableMap(deviceSpecificSettings);
        this.mDisableAnimation = in.readBoolean();
        this.mDisableAod = in.readBoolean();
        this.mDisableLaunchBoost = in.readBoolean();
        this.mDisableOptionalSensors = in.readBoolean();
        this.mDisableVibration = in.readBoolean();
        this.mEnableAdjustBrightness = in.readBoolean();
        this.mEnableDataSaver = in.readBoolean();
        this.mEnableFirewall = in.readBoolean();
        this.mEnableNightMode = in.readBoolean();
        this.mEnableQuickDoze = in.readBoolean();
        this.mForceAllAppsStandby = in.readBoolean();
        this.mForceBackgroundCheck = in.readBoolean();
        this.mLocationMode = Math.max(0, Math.min(in.readInt(), 4));
        this.mSoundTriggerMode = Math.max(0, Math.min(in.readInt(), 2));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mAdjustBrightnessFactor);
        dest.writeBoolean(this.mAdvertiseIsEnabled);
        dest.writeBoolean(this.mDeferFullBackup);
        dest.writeBoolean(this.mDeferKeyValueBackup);
        Set<Map.Entry<String, String>> entries = this.mDeviceSpecificSettings.entrySet();
        int size = entries.size();
        dest.writeInt(size);
        for (Map.Entry<String, String> entry : entries) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeBoolean(this.mDisableAnimation);
        dest.writeBoolean(this.mDisableAod);
        dest.writeBoolean(this.mDisableLaunchBoost);
        dest.writeBoolean(this.mDisableOptionalSensors);
        dest.writeBoolean(this.mDisableVibration);
        dest.writeBoolean(this.mEnableAdjustBrightness);
        dest.writeBoolean(this.mEnableDataSaver);
        dest.writeBoolean(this.mEnableFirewall);
        dest.writeBoolean(this.mEnableNightMode);
        dest.writeBoolean(this.mEnableQuickDoze);
        dest.writeBoolean(this.mForceAllAppsStandby);
        dest.writeBoolean(this.mForceBackgroundCheck);
        dest.writeInt(this.mLocationMode);
        dest.writeInt(this.mSoundTriggerMode);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : this.mDeviceSpecificSettings.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        return "adjust_brightness_disabled=" + (!this.mEnableAdjustBrightness) + ",adjust_brightness_factor=" + this.mAdjustBrightnessFactor + ",advertise_is_enabled=" + this.mAdvertiseIsEnabled + ",animation_disabled=" + this.mDisableAnimation + ",aod_disabled=" + this.mDisableAod + ",datasaver_disabled=" + (!this.mEnableDataSaver) + ",enable_night_mode=" + this.mEnableNightMode + ",firewall_disabled=" + (!this.mEnableFirewall) + ",force_all_apps_standby=" + this.mForceAllAppsStandby + ",force_background_check=" + this.mForceBackgroundCheck + ",fullbackup_deferred=" + this.mDeferFullBackup + ",gps_mode=" + this.mLocationMode + ",keyvaluebackup_deferred=" + this.mDeferKeyValueBackup + ",launch_boost_disabled=" + this.mDisableLaunchBoost + ",optional_sensors_disabled=" + this.mDisableOptionalSensors + ",quick_doze_enabled=" + this.mEnableQuickDoze + ",soundtrigger_mode=" + this.mSoundTriggerMode + ",vibration_disabled=" + this.mDisableVibration + "," + sb.toString();
    }

    public float getAdjustBrightnessFactor() {
        return this.mAdjustBrightnessFactor;
    }

    public boolean getAdvertiseIsEnabled() {
        return this.mAdvertiseIsEnabled;
    }

    public boolean getDeferFullBackup() {
        return this.mDeferFullBackup;
    }

    public boolean getDeferKeyValueBackup() {
        return this.mDeferKeyValueBackup;
    }

    public Map<String, String> getDeviceSpecificSettings() {
        return this.mDeviceSpecificSettings;
    }

    public boolean getDisableAnimation() {
        return this.mDisableAnimation;
    }

    public boolean getDisableAod() {
        return this.mDisableAod;
    }

    public boolean getDisableLaunchBoost() {
        return this.mDisableLaunchBoost;
    }

    public boolean getDisableOptionalSensors() {
        return this.mDisableOptionalSensors;
    }

    public int getSoundTriggerMode() {
        return this.mSoundTriggerMode;
    }

    @Deprecated
    public boolean getDisableSoundTrigger() {
        return this.mSoundTriggerMode == 2;
    }

    public boolean getDisableVibration() {
        return this.mDisableVibration;
    }

    public boolean getEnableAdjustBrightness() {
        return this.mEnableAdjustBrightness;
    }

    public boolean getEnableDataSaver() {
        return this.mEnableDataSaver;
    }

    public boolean getEnableFirewall() {
        return this.mEnableFirewall;
    }

    public boolean getEnableNightMode() {
        return this.mEnableNightMode;
    }

    public boolean getEnableQuickDoze() {
        return this.mEnableQuickDoze;
    }

    public boolean getForceAllAppsStandby() {
        return this.mForceAllAppsStandby;
    }

    public boolean getForceBackgroundCheck() {
        return this.mForceBackgroundCheck;
    }

    public int getLocationMode() {
        return this.mLocationMode;
    }

    /* renamed from: android.os.BatterySaverPolicyConfig$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private float mAdjustBrightnessFactor;
        private boolean mAdvertiseIsEnabled;
        private boolean mDeferFullBackup;
        private boolean mDeferKeyValueBackup;
        private final ArrayMap<String, String> mDeviceSpecificSettings;
        private boolean mDisableAnimation;
        private boolean mDisableAod;
        private boolean mDisableLaunchBoost;
        private boolean mDisableOptionalSensors;
        private boolean mDisableVibration;
        private boolean mEnableAdjustBrightness;
        private boolean mEnableDataSaver;
        private boolean mEnableFirewall;
        private boolean mEnableNightMode;
        private boolean mEnableQuickDoze;
        private boolean mForceAllAppsStandby;
        private boolean mForceBackgroundCheck;
        private int mLocationMode;
        private int mSoundTriggerMode;

        public Builder() {
            this.mAdjustBrightnessFactor = 1.0f;
            this.mAdvertiseIsEnabled = false;
            this.mDeferFullBackup = false;
            this.mDeferKeyValueBackup = false;
            this.mDeviceSpecificSettings = new ArrayMap<>();
            this.mDisableAnimation = false;
            this.mDisableAod = false;
            this.mDisableLaunchBoost = false;
            this.mDisableOptionalSensors = false;
            this.mDisableVibration = false;
            this.mEnableAdjustBrightness = false;
            this.mEnableDataSaver = false;
            this.mEnableFirewall = false;
            this.mEnableNightMode = false;
            this.mEnableQuickDoze = false;
            this.mForceAllAppsStandby = false;
            this.mForceBackgroundCheck = false;
            this.mLocationMode = 0;
            this.mSoundTriggerMode = 0;
        }

        public Builder(BatterySaverPolicyConfig batterySaverPolicyConfig) {
            this.mAdjustBrightnessFactor = 1.0f;
            this.mAdvertiseIsEnabled = false;
            this.mDeferFullBackup = false;
            this.mDeferKeyValueBackup = false;
            this.mDeviceSpecificSettings = new ArrayMap<>();
            this.mDisableAnimation = false;
            this.mDisableAod = false;
            this.mDisableLaunchBoost = false;
            this.mDisableOptionalSensors = false;
            this.mDisableVibration = false;
            this.mEnableAdjustBrightness = false;
            this.mEnableDataSaver = false;
            this.mEnableFirewall = false;
            this.mEnableNightMode = false;
            this.mEnableQuickDoze = false;
            this.mForceAllAppsStandby = false;
            this.mForceBackgroundCheck = false;
            this.mLocationMode = 0;
            this.mSoundTriggerMode = 0;
            this.mAdjustBrightnessFactor = batterySaverPolicyConfig.getAdjustBrightnessFactor();
            this.mAdvertiseIsEnabled = batterySaverPolicyConfig.getAdvertiseIsEnabled();
            this.mDeferFullBackup = batterySaverPolicyConfig.getDeferFullBackup();
            this.mDeferKeyValueBackup = batterySaverPolicyConfig.getDeferKeyValueBackup();
            for (String key : batterySaverPolicyConfig.getDeviceSpecificSettings().keySet()) {
                this.mDeviceSpecificSettings.put(key, batterySaverPolicyConfig.getDeviceSpecificSettings().get(key));
            }
            this.mDisableAnimation = batterySaverPolicyConfig.getDisableAnimation();
            this.mDisableAod = batterySaverPolicyConfig.getDisableAod();
            this.mDisableLaunchBoost = batterySaverPolicyConfig.getDisableLaunchBoost();
            this.mDisableOptionalSensors = batterySaverPolicyConfig.getDisableOptionalSensors();
            this.mDisableVibration = batterySaverPolicyConfig.getDisableVibration();
            this.mEnableAdjustBrightness = batterySaverPolicyConfig.getEnableAdjustBrightness();
            this.mEnableDataSaver = batterySaverPolicyConfig.getEnableDataSaver();
            this.mEnableFirewall = batterySaverPolicyConfig.getEnableFirewall();
            this.mEnableNightMode = batterySaverPolicyConfig.getEnableNightMode();
            this.mEnableQuickDoze = batterySaverPolicyConfig.getEnableQuickDoze();
            this.mForceAllAppsStandby = batterySaverPolicyConfig.getForceAllAppsStandby();
            this.mForceBackgroundCheck = batterySaverPolicyConfig.getForceBackgroundCheck();
            this.mLocationMode = batterySaverPolicyConfig.getLocationMode();
            this.mSoundTriggerMode = batterySaverPolicyConfig.getSoundTriggerMode();
        }

        public Builder setAdjustBrightnessFactor(float adjustBrightnessFactor) {
            this.mAdjustBrightnessFactor = adjustBrightnessFactor;
            return this;
        }

        public Builder setAdvertiseIsEnabled(boolean advertiseIsEnabled) {
            this.mAdvertiseIsEnabled = advertiseIsEnabled;
            return this;
        }

        public Builder setDeferFullBackup(boolean deferFullBackup) {
            this.mDeferFullBackup = deferFullBackup;
            return this;
        }

        public Builder setDeferKeyValueBackup(boolean deferKeyValueBackup) {
            this.mDeferKeyValueBackup = deferKeyValueBackup;
            return this;
        }

        public Builder addDeviceSpecificSetting(String key, String value) {
            if (key == null) {
                throw new IllegalArgumentException("Key cannot be null");
            }
            String key2 = key.trim();
            if (TextUtils.isEmpty(key2)) {
                throw new IllegalArgumentException("Key cannot be empty");
            }
            this.mDeviceSpecificSettings.put(key2, TextUtils.emptyIfNull(value));
            return this;
        }

        public Builder setDisableAnimation(boolean disableAnimation) {
            this.mDisableAnimation = disableAnimation;
            return this;
        }

        public Builder setDisableAod(boolean disableAod) {
            this.mDisableAod = disableAod;
            return this;
        }

        public Builder setDisableLaunchBoost(boolean disableLaunchBoost) {
            this.mDisableLaunchBoost = disableLaunchBoost;
            return this;
        }

        public Builder setDisableOptionalSensors(boolean disableOptionalSensors) {
            this.mDisableOptionalSensors = disableOptionalSensors;
            return this;
        }

        @Deprecated
        public Builder setDisableSoundTrigger(boolean disableSoundTrigger) {
            if (disableSoundTrigger) {
                this.mSoundTriggerMode = 2;
            } else {
                this.mSoundTriggerMode = 0;
            }
            return this;
        }

        public Builder setSoundTriggerMode(int soundTriggerMode) {
            this.mSoundTriggerMode = soundTriggerMode;
            return this;
        }

        public Builder setDisableVibration(boolean disableVibration) {
            this.mDisableVibration = disableVibration;
            return this;
        }

        public Builder setEnableAdjustBrightness(boolean enableAdjustBrightness) {
            this.mEnableAdjustBrightness = enableAdjustBrightness;
            return this;
        }

        public Builder setEnableDataSaver(boolean enableDataSaver) {
            this.mEnableDataSaver = enableDataSaver;
            return this;
        }

        public Builder setEnableFirewall(boolean enableFirewall) {
            this.mEnableFirewall = enableFirewall;
            return this;
        }

        public Builder setEnableNightMode(boolean enableNightMode) {
            this.mEnableNightMode = enableNightMode;
            return this;
        }

        public Builder setEnableQuickDoze(boolean enableQuickDoze) {
            this.mEnableQuickDoze = enableQuickDoze;
            return this;
        }

        public Builder setForceAllAppsStandby(boolean forceAllAppsStandby) {
            this.mForceAllAppsStandby = forceAllAppsStandby;
            return this;
        }

        public Builder setForceBackgroundCheck(boolean forceBackgroundCheck) {
            this.mForceBackgroundCheck = forceBackgroundCheck;
            return this;
        }

        public Builder setLocationMode(int locationMode) {
            this.mLocationMode = locationMode;
            return this;
        }

        public BatterySaverPolicyConfig build() {
            return new BatterySaverPolicyConfig(this);
        }
    }
}
