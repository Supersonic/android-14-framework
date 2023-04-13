package android.hardware.location;

import android.annotation.SystemApi;
import android.location.Location;
@SystemApi
/* loaded from: classes2.dex */
public abstract class GeofenceHardwareMonitorCallback {
    @Deprecated
    public void onMonitoringSystemChange(int monitoringType, boolean available, Location location) {
    }

    public void onMonitoringSystemChange(GeofenceHardwareMonitorEvent event) {
    }
}
