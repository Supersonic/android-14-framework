package com.android.server.location.injector;

import com.android.server.DeviceIdleInternal;
/* loaded from: classes.dex */
public abstract class DeviceStationaryHelper {
    public abstract void addListener(DeviceIdleInternal.StationaryListener stationaryListener);

    public abstract void removeListener(DeviceIdleInternal.StationaryListener stationaryListener);
}
