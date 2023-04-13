package com.android.location.provider;

import android.hardware.location.GeofenceHardware;
import android.hardware.location.IGeofenceHardware;
import android.location.IGeofenceProvider;
import android.os.IBinder;
/* loaded from: classes.dex */
public abstract class GeofenceProvider {
    private GeofenceHardware mGeofenceHardware;
    private IGeofenceProvider.Stub mProvider = new IGeofenceProvider.Stub() { // from class: com.android.location.provider.GeofenceProvider.1
        public void setGeofenceHardware(IGeofenceHardware hardwareProxy) {
            GeofenceProvider.this.mGeofenceHardware = new GeofenceHardware(hardwareProxy);
            GeofenceProvider geofenceProvider = GeofenceProvider.this;
            geofenceProvider.onGeofenceHardwareChange(geofenceProvider.mGeofenceHardware);
        }
    };

    public abstract void onGeofenceHardwareChange(GeofenceHardware geofenceHardware);

    public IBinder getBinder() {
        return this.mProvider;
    }
}
