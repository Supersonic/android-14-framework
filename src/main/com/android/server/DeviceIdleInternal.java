package com.android.server;

import com.android.server.deviceidle.IDeviceIdleConstraint;
/* loaded from: classes5.dex */
public interface DeviceIdleInternal {

    /* loaded from: classes5.dex */
    public interface StationaryListener {
        void onDeviceStationaryChanged(boolean z);
    }

    void addPowerSaveTempWhitelistApp(int i, String str, long j, int i2, int i3, boolean z, int i4, String str2);

    void addPowerSaveTempWhitelistApp(int i, String str, long j, int i2, boolean z, int i3, String str2);

    void addPowerSaveTempWhitelistAppDirect(int i, long j, int i2, boolean z, int i3, String str, int i4);

    void exitIdle(String str);

    long getNotificationAllowlistDuration();

    int[] getPowerSaveTempWhitelistAppIds();

    int[] getPowerSaveWhitelistUserAppIds();

    int getTempAllowListType(int i, int i2);

    boolean isAppOnWhitelist(int i);

    void onConstraintStateChanged(IDeviceIdleConstraint iDeviceIdleConstraint, boolean z);

    void registerDeviceIdleConstraint(IDeviceIdleConstraint iDeviceIdleConstraint, String str, int i);

    void registerStationaryListener(StationaryListener stationaryListener);

    void setAlarmsActive(boolean z);

    void setJobsActive(boolean z);

    void unregisterDeviceIdleConstraint(IDeviceIdleConstraint iDeviceIdleConstraint);

    void unregisterStationaryListener(StationaryListener stationaryListener);
}
