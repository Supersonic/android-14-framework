package com.android.server.location.injector;

import com.android.internal.annotations.VisibleForTesting;
import com.android.server.location.settings.LocationSettings;
@VisibleForTesting
/* loaded from: classes.dex */
public interface Injector {
    AlarmHelper getAlarmHelper();

    AppForegroundHelper getAppForegroundHelper();

    AppOpsHelper getAppOpsHelper();

    DeviceIdleHelper getDeviceIdleHelper();

    DeviceStationaryHelper getDeviceStationaryHelper();

    EmergencyHelper getEmergencyHelper();

    LocationPermissionsHelper getLocationPermissionsHelper();

    LocationPowerSaveModeHelper getLocationPowerSaveModeHelper();

    LocationSettings getLocationSettings();

    LocationUsageLogger getLocationUsageLogger();

    PackageResetHelper getPackageResetHelper();

    ScreenInteractiveHelper getScreenInteractiveHelper();

    SettingsHelper getSettingsHelper();

    UserInfoHelper getUserInfoHelper();
}
