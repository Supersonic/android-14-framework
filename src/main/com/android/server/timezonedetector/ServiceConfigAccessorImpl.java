package com.android.server.timezonedetector;

import android.app.ActivityManagerInternal;
import android.app.time.TimeZoneConfiguration;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import com.android.internal.annotations.GuardedBy;
import com.android.server.LocalServices;
import com.android.server.timedetector.ServerFlags;
import com.android.server.timezonedetector.ConfigurationInternal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
/* loaded from: classes2.dex */
public final class ServiceConfigAccessorImpl implements ServiceConfigAccessor {
    @GuardedBy({"SLOCK"})
    public static ServiceConfigAccessor sInstance;
    @GuardedBy({"this"})
    public final List<StateChangeListener> mConfigurationInternalListeners = new ArrayList();
    public final Context mContext;
    public final ContentResolver mCr;
    public final LocationManager mLocationManager;
    @GuardedBy({"this"})
    public boolean mRecordStateChangesForTests;
    public final ServerFlags mServerFlags;
    @GuardedBy({"this"})
    public String mTestPrimaryLocationTimeZoneProviderMode;
    @GuardedBy({"this"})
    public String mTestPrimaryLocationTimeZoneProviderPackageName;
    @GuardedBy({"this"})
    public String mTestSecondaryLocationTimeZoneProviderMode;
    @GuardedBy({"this"})
    public String mTestSecondaryLocationTimeZoneProviderPackageName;
    public final UserManager mUserManager;
    public static final Set<String> CONFIGURATION_INTERNAL_SERVER_FLAGS_KEYS_TO_WATCH = Set.of("location_time_zone_detection_feature_supported", "primary_location_time_zone_provider_mode_override", "secondary_location_time_zone_provider_mode_override", "location_time_zone_detection_run_in_background_enabled", "enhanced_metrics_collection_enabled", "location_time_zone_detection_setting_enabled_default", "location_time_zone_detection_setting_enabled_override", "time_zone_detector_telephony_fallback_supported");
    public static final Set<String> LOCATION_TIME_ZONE_MANAGER_SERVER_FLAGS_KEYS_TO_WATCH = Set.of("location_time_zone_detection_feature_supported", "location_time_zone_detection_run_in_background_enabled", "location_time_zone_detection_setting_enabled_default", "location_time_zone_detection_setting_enabled_override", "primary_location_time_zone_provider_mode_override", "secondary_location_time_zone_provider_mode_override", "ltzp_init_timeout_millis", "ltzp_init_timeout_fuzz_millis", "ltzp_event_filtering_age_threshold_millis", "location_time_zone_detection_uncertainty_delay_millis");
    public static final Duration DEFAULT_LTZP_INITIALIZATION_TIMEOUT = Duration.ofMinutes(5);
    public static final Duration DEFAULT_LTZP_INITIALIZATION_TIMEOUT_FUZZ = Duration.ofMinutes(1);
    public static final Duration DEFAULT_LTZP_UNCERTAINTY_DELAY = Duration.ofMinutes(5);
    public static final Duration DEFAULT_LTZP_EVENT_FILTER_AGE_THRESHOLD = Duration.ofMinutes(1);
    public static final Object SLOCK = new Object();

    public ServiceConfigAccessorImpl(Context context) {
        Objects.requireNonNull(context);
        this.mContext = context;
        this.mCr = context.getContentResolver();
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mLocationManager = (LocationManager) context.getSystemService(LocationManager.class);
        ServerFlags serverFlags = ServerFlags.getInstance(context);
        this.mServerFlags = serverFlags;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.location.MODE_CHANGED");
        context.registerReceiverForAllUsers(new BroadcastReceiver() { // from class: com.android.server.timezonedetector.ServiceConfigAccessorImpl.1
            {
                ServiceConfigAccessorImpl.this = this;
            }

            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                ServiceConfigAccessorImpl.this.handleConfigurationInternalChangeOnMainThread();
            }
        }, intentFilter, null, null);
        ContentResolver contentResolver = context.getContentResolver();
        ContentObserver contentObserver = new ContentObserver(context.getMainThreadHandler()) { // from class: com.android.server.timezonedetector.ServiceConfigAccessorImpl.2
            {
                ServiceConfigAccessorImpl.this = this;
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                ServiceConfigAccessorImpl.this.handleConfigurationInternalChangeOnMainThread();
            }
        };
        contentResolver.registerContentObserver(Settings.Global.getUriFor("auto_time_zone"), true, contentObserver);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("location_time_zone_detection_enabled"), true, contentObserver, -1);
        serverFlags.addListener(new StateChangeListener() { // from class: com.android.server.timezonedetector.ServiceConfigAccessorImpl$$ExternalSyntheticLambda0
            @Override // com.android.server.timezonedetector.StateChangeListener
            public final void onChange() {
                ServiceConfigAccessorImpl.this.handleConfigurationInternalChangeOnMainThread();
            }
        }, CONFIGURATION_INTERNAL_SERVER_FLAGS_KEYS_TO_WATCH);
    }

    public static ServiceConfigAccessor getInstance(Context context) {
        ServiceConfigAccessor serviceConfigAccessor;
        synchronized (SLOCK) {
            if (sInstance == null) {
                sInstance = new ServiceConfigAccessorImpl(context);
            }
            serviceConfigAccessor = sInstance;
        }
        return serviceConfigAccessor;
    }

    public final void handleConfigurationInternalChangeOnMainThread() {
        ArrayList<StateChangeListener> arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.mConfigurationInternalListeners);
        }
        for (StateChangeListener stateChangeListener : arrayList) {
            stateChangeListener.onChange();
        }
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized void addConfigurationInternalChangeListener(StateChangeListener stateChangeListener) {
        List<StateChangeListener> list = this.mConfigurationInternalListeners;
        Objects.requireNonNull(stateChangeListener);
        list.add(stateChangeListener);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized void removeConfigurationInternalChangeListener(StateChangeListener stateChangeListener) {
        List<StateChangeListener> list = this.mConfigurationInternalListeners;
        Objects.requireNonNull(stateChangeListener);
        list.remove(stateChangeListener);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized ConfigurationInternal getCurrentUserConfigurationInternal() {
        return getConfigurationInternal(((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getCurrentUserId());
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized boolean updateConfiguration(int i, TimeZoneConfiguration timeZoneConfiguration, boolean z) {
        Objects.requireNonNull(timeZoneConfiguration);
        ConfigurationInternal configurationInternal = getConfigurationInternal(i);
        TimeZoneConfiguration tryApplyConfigChanges = configurationInternal.asCapabilities(z).tryApplyConfigChanges(configurationInternal.asConfiguration(), timeZoneConfiguration);
        if (tryApplyConfigChanges == null) {
            return false;
        }
        storeConfiguration(i, tryApplyConfigChanges);
        return true;
    }

    @GuardedBy({"this"})
    public final void storeConfiguration(int i, TimeZoneConfiguration timeZoneConfiguration) {
        Objects.requireNonNull(timeZoneConfiguration);
        if (isAutoDetectionFeatureSupported()) {
            setAutoDetectionEnabledIfRequired(timeZoneConfiguration.isAutoDetectionEnabled());
            if (!getGeoDetectionSettingEnabledOverride().isPresent() && isGeoTimeZoneDetectionFeatureSupported() && isTelephonyTimeZoneDetectionFeatureSupported()) {
                setGeoDetectionEnabledSettingIfRequired(i, timeZoneConfiguration.isGeoDetectionEnabled());
            }
        }
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized ConfigurationInternal getConfigurationInternal(int i) {
        return new ConfigurationInternal.Builder().setUserId(i).setTelephonyDetectionFeatureSupported(isTelephonyTimeZoneDetectionFeatureSupported()).setGeoDetectionFeatureSupported(isGeoTimeZoneDetectionFeatureSupported()).setTelephonyFallbackSupported(isTelephonyFallbackSupported()).setGeoDetectionRunInBackgroundEnabled(getGeoDetectionRunInBackgroundEnabled()).setEnhancedMetricsCollectionEnabled(isEnhancedMetricsCollectionEnabled()).setAutoDetectionEnabledSetting(getAutoDetectionEnabledSetting()).setUserConfigAllowed(isUserConfigAllowed(i)).setLocationEnabledSetting(getLocationEnabledSetting(i)).setGeoDetectionEnabledSetting(getGeoDetectionEnabledSetting(i)).build();
    }

    public final void setAutoDetectionEnabledIfRequired(boolean z) {
        if (getAutoDetectionEnabledSetting() != z) {
            Settings.Global.putInt(this.mCr, "auto_time_zone", z ? 1 : 0);
        }
    }

    public final boolean getLocationEnabledSetting(int i) {
        return this.mLocationManager.isLocationEnabledForUser(UserHandle.of(i));
    }

    public final boolean isUserConfigAllowed(int i) {
        return !this.mUserManager.hasUserRestriction("no_config_date_time", UserHandle.of(i));
    }

    public final boolean getAutoDetectionEnabledSetting() {
        return Settings.Global.getInt(this.mCr, "auto_time_zone", 1) > 0;
    }

    public final boolean getGeoDetectionEnabledSetting(int i) {
        Optional<Boolean> geoDetectionSettingEnabledOverride = getGeoDetectionSettingEnabledOverride();
        if (geoDetectionSettingEnabledOverride.isPresent()) {
            return geoDetectionSettingEnabledOverride.get().booleanValue();
        }
        return Settings.Secure.getIntForUser(this.mCr, "location_time_zone_detection_enabled", isGeoDetectionEnabledForUsersByDefault() ? 1 : 0, i) != 0;
    }

    public final void setGeoDetectionEnabledSettingIfRequired(int i, boolean z) {
        if (getGeoDetectionEnabledSetting(i) != z) {
            Settings.Secure.putIntForUser(this.mCr, "location_time_zone_detection_enabled", z ? 1 : 0, i);
        }
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public void addLocationTimeZoneManagerConfigListener(StateChangeListener stateChangeListener) {
        this.mServerFlags.addListener(stateChangeListener, LOCATION_TIME_ZONE_MANAGER_SERVER_FLAGS_KEYS_TO_WATCH);
    }

    public final boolean isAutoDetectionFeatureSupported() {
        return isTelephonyTimeZoneDetectionFeatureSupported() || isGeoTimeZoneDetectionFeatureSupported();
    }

    public boolean isTelephonyTimeZoneDetectionFeatureSupported() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public boolean isGeoTimeZoneDetectionFeatureSupportedInConfig() {
        return this.mContext.getResources().getBoolean(17891652);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public boolean isGeoTimeZoneDetectionFeatureSupported() {
        return isGeoTimeZoneDetectionFeatureSupportedInConfig() && isGeoTimeZoneDetectionFeatureSupportedInternal() && atLeastOneProviderIsEnabled();
    }

    public final boolean atLeastOneProviderIsEnabled() {
        return (Objects.equals(getPrimaryLocationTimeZoneProviderMode(), "disabled") && Objects.equals(getSecondaryLocationTimeZoneProviderMode(), "disabled")) ? false : true;
    }

    public final boolean isGeoTimeZoneDetectionFeatureSupportedInternal() {
        return this.mServerFlags.getBoolean("location_time_zone_detection_feature_supported", true);
    }

    public final boolean getGeoDetectionRunInBackgroundEnabled() {
        return this.mServerFlags.getBoolean("location_time_zone_detection_run_in_background_enabled", false);
    }

    public final boolean isEnhancedMetricsCollectionEnabled() {
        return this.mServerFlags.getBoolean("enhanced_metrics_collection_enabled", false);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized String getPrimaryLocationTimeZoneProviderPackageName() {
        if (this.mTestPrimaryLocationTimeZoneProviderMode != null) {
            return this.mTestPrimaryLocationTimeZoneProviderPackageName;
        }
        return this.mContext.getResources().getString(17039980);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized void setTestPrimaryLocationTimeZoneProviderPackageName(String str) {
        this.mTestPrimaryLocationTimeZoneProviderPackageName = str;
        this.mTestPrimaryLocationTimeZoneProviderMode = str == null ? "disabled" : "enabled";
        this.mContext.getMainThreadHandler().post(new ServiceConfigAccessorImpl$$ExternalSyntheticLambda1(this));
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized boolean isTestPrimaryLocationTimeZoneProvider() {
        return this.mTestPrimaryLocationTimeZoneProviderMode != null;
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized String getSecondaryLocationTimeZoneProviderPackageName() {
        if (this.mTestSecondaryLocationTimeZoneProviderMode != null) {
            return this.mTestSecondaryLocationTimeZoneProviderPackageName;
        }
        return this.mContext.getResources().getString(17040001);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized void setTestSecondaryLocationTimeZoneProviderPackageName(String str) {
        this.mTestSecondaryLocationTimeZoneProviderPackageName = str;
        this.mTestSecondaryLocationTimeZoneProviderMode = str == null ? "disabled" : "enabled";
        this.mContext.getMainThreadHandler().post(new ServiceConfigAccessorImpl$$ExternalSyntheticLambda1(this));
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized boolean isTestSecondaryLocationTimeZoneProvider() {
        return this.mTestSecondaryLocationTimeZoneProviderMode != null;
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized void setRecordStateChangesForTests(boolean z) {
        this.mRecordStateChangesForTests = z;
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized boolean getRecordStateChangesForTests() {
        return this.mRecordStateChangesForTests;
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized String getPrimaryLocationTimeZoneProviderMode() {
        String str = this.mTestPrimaryLocationTimeZoneProviderMode;
        if (str != null) {
            return str;
        }
        return this.mServerFlags.getOptionalString("primary_location_time_zone_provider_mode_override").orElse(getPrimaryLocationTimeZoneProviderModeFromConfig());
    }

    public final synchronized String getPrimaryLocationTimeZoneProviderModeFromConfig() {
        return getConfigBoolean(17891664) ? "enabled" : "disabled";
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized String getSecondaryLocationTimeZoneProviderMode() {
        String str = this.mTestSecondaryLocationTimeZoneProviderMode;
        if (str != null) {
            return str;
        }
        return this.mServerFlags.getOptionalString("secondary_location_time_zone_provider_mode_override").orElse(getSecondaryLocationTimeZoneProviderModeFromConfig());
    }

    public final synchronized String getSecondaryLocationTimeZoneProviderModeFromConfig() {
        return getConfigBoolean(17891667) ? "enabled" : "disabled";
    }

    public boolean isGeoDetectionEnabledForUsersByDefault() {
        return this.mServerFlags.getBoolean("location_time_zone_detection_setting_enabled_default", false);
    }

    public Optional<Boolean> getGeoDetectionSettingEnabledOverride() {
        return this.mServerFlags.getOptionalBoolean("location_time_zone_detection_setting_enabled_override");
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public Duration getLocationTimeZoneProviderInitializationTimeout() {
        return this.mServerFlags.getDurationFromMillis("ltzp_init_timeout_millis", DEFAULT_LTZP_INITIALIZATION_TIMEOUT);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public Duration getLocationTimeZoneProviderInitializationTimeoutFuzz() {
        return this.mServerFlags.getDurationFromMillis("ltzp_init_timeout_fuzz_millis", DEFAULT_LTZP_INITIALIZATION_TIMEOUT_FUZZ);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public Duration getLocationTimeZoneUncertaintyDelay() {
        return this.mServerFlags.getDurationFromMillis("location_time_zone_detection_uncertainty_delay_millis", DEFAULT_LTZP_UNCERTAINTY_DELAY);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public Duration getLocationTimeZoneProviderEventFilteringAgeThreshold() {
        return this.mServerFlags.getDurationFromMillis("ltzp_event_filtering_age_threshold_millis", DEFAULT_LTZP_EVENT_FILTER_AGE_THRESHOLD);
    }

    @Override // com.android.server.timezonedetector.ServiceConfigAccessor
    public synchronized void resetVolatileTestConfig() {
        this.mTestPrimaryLocationTimeZoneProviderPackageName = null;
        this.mTestPrimaryLocationTimeZoneProviderMode = null;
        this.mTestSecondaryLocationTimeZoneProviderPackageName = null;
        this.mTestSecondaryLocationTimeZoneProviderMode = null;
        this.mRecordStateChangesForTests = false;
        this.mContext.getMainThreadHandler().post(new ServiceConfigAccessorImpl$$ExternalSyntheticLambda1(this));
    }

    public final boolean isTelephonyFallbackSupported() {
        return this.mServerFlags.getBoolean("time_zone_detector_telephony_fallback_supported", getConfigBoolean(17891824));
    }

    public final boolean getConfigBoolean(int i) {
        return this.mContext.getResources().getBoolean(i);
    }
}
