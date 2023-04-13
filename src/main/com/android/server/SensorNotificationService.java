package com.android.server;

import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorAdditionalInfo;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Slog;
/* loaded from: classes.dex */
public class SensorNotificationService extends SystemService implements SensorEventListener, LocationListener {
    public Context mContext;
    public long mLocalGeomagneticFieldUpdateTime;
    public LocationManager mLocationManager;
    public Sensor mMetaSensor;
    public SensorManager mSensorManager;

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // android.location.LocationListener
    public void onProviderDisabled(String str) {
    }

    @Override // android.location.LocationListener
    public void onProviderEnabled(String str) {
    }

    @Override // android.location.LocationListener
    public void onStatusChanged(String str, int i, Bundle bundle) {
    }

    public SensorNotificationService(Context context) {
        super(context.createAttributionContext("SensorNotificationService"));
        this.mLocalGeomagneticFieldUpdateTime = -1800000L;
        this.mContext = getContext();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        LocalServices.addService(SensorNotificationService.class, this);
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 600) {
            SensorManager sensorManager = (SensorManager) this.mContext.getSystemService("sensor");
            this.mSensorManager = sensorManager;
            Sensor defaultSensor = sensorManager.getDefaultSensor(32);
            this.mMetaSensor = defaultSensor;
            if (defaultSensor != null) {
                this.mSensorManager.registerListener(this, defaultSensor, 0);
            }
        }
        if (i == 1000) {
            LocationManager locationManager = (LocationManager) this.mContext.getSystemService("location");
            this.mLocationManager = locationManager;
            if (locationManager == null) {
                return;
            }
            locationManager.requestLocationUpdates("passive", 1800000L, 100000.0f, this);
        }
    }

    public final void broadcastDynamicSensorChanged() {
        Intent intent = new Intent("android.intent.action.DYNAMIC_SENSOR_CHANGED");
        intent.setFlags(1073741824);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == this.mMetaSensor) {
            broadcastDynamicSensorChanged();
        }
    }

    @Override // android.location.LocationListener
    public void onLocationChanged(Location location) {
        if (!(location.getLatitude() == 0.0d && location.getLongitude() == 0.0d) && SystemClock.elapsedRealtime() - this.mLocalGeomagneticFieldUpdateTime >= 600000) {
            long currentTimeMillis = System.currentTimeMillis();
            if (useMockedLocation() == location.isMock() || currentTimeMillis < 1262358000000L) {
                return;
            }
            GeomagneticField geomagneticField = new GeomagneticField((float) location.getLatitude(), (float) location.getLongitude(), (float) location.getAltitude(), currentTimeMillis);
            try {
                SensorAdditionalInfo createLocalGeomagneticField = SensorAdditionalInfo.createLocalGeomagneticField(geomagneticField.getFieldStrength() / 1000.0f, (float) ((geomagneticField.getDeclination() * 3.141592653589793d) / 180.0d), (float) ((geomagneticField.getInclination() * 3.141592653589793d) / 180.0d));
                if (createLocalGeomagneticField != null) {
                    this.mSensorManager.setOperationParameter(createLocalGeomagneticField);
                    this.mLocalGeomagneticFieldUpdateTime = SystemClock.elapsedRealtime();
                }
            } catch (IllegalArgumentException unused) {
                Slog.e("SensorNotificationService", "Invalid local geomagnetic field, ignore.");
            }
        }
    }

    public final boolean useMockedLocation() {
        return "false".equals(System.getProperty("sensor.notification.use_mocked", "false"));
    }
}
