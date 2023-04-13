package com.android.server.timezonedetector;

import android.app.time.TimeZoneCapabilities;
import android.app.time.TimeZoneConfiguration;
import android.os.UserHandle;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class ConfigurationInternal {
    public final boolean mAutoDetectionEnabledSetting;
    public final boolean mEnhancedMetricsCollectionEnabled;
    public final boolean mGeoDetectionEnabledSetting;
    public final boolean mGeoDetectionRunInBackgroundEnabled;
    public final boolean mGeoDetectionSupported;
    public final boolean mLocationEnabledSetting;
    public final boolean mTelephonyDetectionSupported;
    public final boolean mTelephonyFallbackSupported;
    public final boolean mUserConfigAllowed;
    public final int mUserId;

    public ConfigurationInternal(Builder builder) {
        this.mTelephonyDetectionSupported = builder.mTelephonyDetectionSupported;
        this.mGeoDetectionSupported = builder.mGeoDetectionSupported;
        this.mTelephonyFallbackSupported = builder.mTelephonyFallbackSupported;
        this.mGeoDetectionRunInBackgroundEnabled = builder.mGeoDetectionRunInBackgroundEnabled;
        this.mEnhancedMetricsCollectionEnabled = builder.mEnhancedMetricsCollectionEnabled;
        this.mAutoDetectionEnabledSetting = builder.mAutoDetectionEnabledSetting;
        Integer num = builder.mUserId;
        Objects.requireNonNull(num, "userId must be set");
        this.mUserId = num.intValue();
        this.mUserConfigAllowed = builder.mUserConfigAllowed;
        this.mLocationEnabledSetting = builder.mLocationEnabledSetting;
        this.mGeoDetectionEnabledSetting = builder.mGeoDetectionEnabledSetting;
    }

    public boolean isAutoDetectionSupported() {
        return this.mTelephonyDetectionSupported || this.mGeoDetectionSupported;
    }

    public boolean isTelephonyDetectionSupported() {
        return this.mTelephonyDetectionSupported;
    }

    public boolean isGeoDetectionSupported() {
        return this.mGeoDetectionSupported;
    }

    public boolean isTelephonyFallbackSupported() {
        return this.mTelephonyFallbackSupported;
    }

    public boolean getGeoDetectionRunInBackgroundEnabledSetting() {
        return this.mGeoDetectionRunInBackgroundEnabled;
    }

    public boolean isEnhancedMetricsCollectionEnabled() {
        return this.mEnhancedMetricsCollectionEnabled;
    }

    public boolean getAutoDetectionEnabledSetting() {
        return this.mAutoDetectionEnabledSetting;
    }

    public boolean getAutoDetectionEnabledBehavior() {
        return isAutoDetectionSupported() && getAutoDetectionEnabledSetting();
    }

    public int getUserId() {
        return this.mUserId;
    }

    public boolean isUserConfigAllowed() {
        return this.mUserConfigAllowed;
    }

    public boolean getLocationEnabledSetting() {
        return this.mLocationEnabledSetting;
    }

    public boolean getGeoDetectionEnabledSetting() {
        return this.mGeoDetectionEnabledSetting;
    }

    public int getDetectionMode() {
        if (isAutoDetectionSupported() && getAutoDetectionEnabledSetting()) {
            if (getGeoDetectionEnabledBehavior()) {
                return 2;
            }
            return isTelephonyDetectionSupported() ? 3 : 0;
        }
        return 1;
    }

    public final boolean getGeoDetectionEnabledBehavior() {
        if (isGeoDetectionSupported() && getLocationEnabledSetting()) {
            if (isTelephonyDetectionSupported()) {
                return getGeoDetectionEnabledSetting();
            }
            return true;
        }
        return false;
    }

    public boolean isGeoDetectionExecutionEnabled() {
        return getDetectionMode() == 2 || getGeoDetectionRunInBackgroundEnabledBehavior();
    }

    public final boolean getGeoDetectionRunInBackgroundEnabledBehavior() {
        return isGeoDetectionSupported() && getLocationEnabledSetting() && getAutoDetectionEnabledSetting() && getGeoDetectionRunInBackgroundEnabledSetting();
    }

    public TimeZoneCapabilities asCapabilities(boolean z) {
        TimeZoneCapabilities.Builder builder = new TimeZoneCapabilities.Builder(UserHandle.of(this.mUserId));
        boolean z2 = isUserConfigAllowed() || z;
        int i = 20;
        int i2 = 10;
        builder.setConfigureAutoDetectionEnabledCapability(!isAutoDetectionSupported() ? 10 : !z2 ? 20 : 40);
        builder.setUseLocationEnabled(this.mLocationEnabledSetting);
        boolean isGeoDetectionSupported = isGeoDetectionSupported();
        boolean isTelephonyDetectionSupported = isTelephonyDetectionSupported();
        if (isGeoDetectionSupported && isTelephonyDetectionSupported) {
            i2 = (this.mAutoDetectionEnabledSetting && getLocationEnabledSetting()) ? 40 : 30;
        }
        builder.setConfigureGeoDetectionEnabledCapability(i2);
        if (z2) {
            i = getAutoDetectionEnabledBehavior() ? 30 : 40;
        }
        builder.setSetManualTimeZoneCapability(i);
        return builder.build();
    }

    public TimeZoneConfiguration asConfiguration() {
        return new TimeZoneConfiguration.Builder().setAutoDetectionEnabled(getAutoDetectionEnabledSetting()).setGeoDetectionEnabled(getGeoDetectionEnabledSetting()).build();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || ConfigurationInternal.class != obj.getClass()) {
            return false;
        }
        ConfigurationInternal configurationInternal = (ConfigurationInternal) obj;
        return this.mUserId == configurationInternal.mUserId && this.mUserConfigAllowed == configurationInternal.mUserConfigAllowed && this.mTelephonyDetectionSupported == configurationInternal.mTelephonyDetectionSupported && this.mGeoDetectionSupported == configurationInternal.mGeoDetectionSupported && this.mTelephonyFallbackSupported == configurationInternal.mTelephonyFallbackSupported && this.mGeoDetectionRunInBackgroundEnabled == configurationInternal.mGeoDetectionRunInBackgroundEnabled && this.mEnhancedMetricsCollectionEnabled == configurationInternal.mEnhancedMetricsCollectionEnabled && this.mAutoDetectionEnabledSetting == configurationInternal.mAutoDetectionEnabledSetting && this.mLocationEnabledSetting == configurationInternal.mLocationEnabledSetting && this.mGeoDetectionEnabledSetting == configurationInternal.mGeoDetectionEnabledSetting;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mUserId), Boolean.valueOf(this.mUserConfigAllowed), Boolean.valueOf(this.mTelephonyDetectionSupported), Boolean.valueOf(this.mGeoDetectionSupported), Boolean.valueOf(this.mTelephonyFallbackSupported), Boolean.valueOf(this.mGeoDetectionRunInBackgroundEnabled), Boolean.valueOf(this.mEnhancedMetricsCollectionEnabled), Boolean.valueOf(this.mAutoDetectionEnabledSetting), Boolean.valueOf(this.mLocationEnabledSetting), Boolean.valueOf(this.mGeoDetectionEnabledSetting));
    }

    public String toString() {
        return "ConfigurationInternal{mUserId=" + this.mUserId + ", mUserConfigAllowed=" + this.mUserConfigAllowed + ", mTelephonyDetectionSupported=" + this.mTelephonyDetectionSupported + ", mGeoDetectionSupported=" + this.mGeoDetectionSupported + ", mTelephonyFallbackSupported=" + this.mTelephonyFallbackSupported + ", mGeoDetectionRunInBackgroundEnabled=" + this.mGeoDetectionRunInBackgroundEnabled + ", mEnhancedMetricsCollectionEnabled=" + this.mEnhancedMetricsCollectionEnabled + ", mAutoDetectionEnabledSetting=" + this.mAutoDetectionEnabledSetting + ", mLocationEnabledSetting=" + this.mLocationEnabledSetting + ", mGeoDetectionEnabledSetting=" + this.mGeoDetectionEnabledSetting + '}';
    }

    /* loaded from: classes2.dex */
    public static class Builder {
        public boolean mAutoDetectionEnabledSetting;
        public boolean mEnhancedMetricsCollectionEnabled;
        public boolean mGeoDetectionEnabledSetting;
        public boolean mGeoDetectionRunInBackgroundEnabled;
        public boolean mGeoDetectionSupported;
        public boolean mLocationEnabledSetting;
        public boolean mTelephonyDetectionSupported;
        public boolean mTelephonyFallbackSupported;
        public boolean mUserConfigAllowed;
        public Integer mUserId;

        public Builder setUserId(int i) {
            this.mUserId = Integer.valueOf(i);
            return this;
        }

        public Builder setUserConfigAllowed(boolean z) {
            this.mUserConfigAllowed = z;
            return this;
        }

        public Builder setTelephonyDetectionFeatureSupported(boolean z) {
            this.mTelephonyDetectionSupported = z;
            return this;
        }

        public Builder setGeoDetectionFeatureSupported(boolean z) {
            this.mGeoDetectionSupported = z;
            return this;
        }

        public Builder setTelephonyFallbackSupported(boolean z) {
            this.mTelephonyFallbackSupported = z;
            return this;
        }

        public Builder setGeoDetectionRunInBackgroundEnabled(boolean z) {
            this.mGeoDetectionRunInBackgroundEnabled = z;
            return this;
        }

        public Builder setEnhancedMetricsCollectionEnabled(boolean z) {
            this.mEnhancedMetricsCollectionEnabled = z;
            return this;
        }

        public Builder setAutoDetectionEnabledSetting(boolean z) {
            this.mAutoDetectionEnabledSetting = z;
            return this;
        }

        public Builder setLocationEnabledSetting(boolean z) {
            this.mLocationEnabledSetting = z;
            return this;
        }

        public Builder setGeoDetectionEnabledSetting(boolean z) {
            this.mGeoDetectionEnabledSetting = z;
            return this;
        }

        public ConfigurationInternal build() {
            return new ConfigurationInternal(this);
        }
    }
}
