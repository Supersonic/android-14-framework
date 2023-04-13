package com.android.server.location.countrydetector;

import android.content.Context;
import android.location.Address;
import android.location.Country;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.util.Slog;
import com.android.server.backup.BackupAgentTimeoutParameters;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/* loaded from: classes.dex */
public class LocationBasedCountryDetector extends CountryDetectorBase {
    public List<String> mEnabledProviders;
    public List<LocationListener> mLocationListeners;
    public LocationManager mLocationManager;
    public Thread mQueryThread;
    public Timer mTimer;

    public long getQueryLocationTimeout() {
        return BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS;
    }

    public LocationBasedCountryDetector(Context context) {
        super(context);
        this.mLocationManager = (LocationManager) context.getSystemService("location");
    }

    public String getCountryFromLocation(Location location) {
        try {
            List<Address> fromLocation = new Geocoder(this.mContext).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (fromLocation == null || fromLocation.size() <= 0) {
                return null;
            }
            return fromLocation.get(0).getCountryCode();
        } catch (IOException unused) {
            Slog.w("LocationBasedCountryDetector", "Exception occurs when getting country from location");
            return null;
        }
    }

    public boolean isAcceptableProvider(String str) {
        return "passive".equals(str);
    }

    public void registerListener(String str, LocationListener locationListener) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mLocationManager.requestLocationUpdates(str, 0L, 0.0f, locationListener);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void unregisterListener(LocationListener locationListener) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mLocationManager.removeUpdates(locationListener);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public Location getLastKnownLocation() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Location location = null;
            for (String str : this.mLocationManager.getAllProviders()) {
                Location lastKnownLocation = this.mLocationManager.getLastKnownLocation(str);
                if (lastKnownLocation != null && (location == null || location.getElapsedRealtimeNanos() < lastKnownLocation.getElapsedRealtimeNanos())) {
                    location = lastKnownLocation;
                }
            }
            return location;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<String> getEnabledProviders() {
        if (this.mEnabledProviders == null) {
            this.mEnabledProviders = this.mLocationManager.getProviders(true);
        }
        return this.mEnabledProviders;
    }

    @Override // com.android.server.location.countrydetector.CountryDetectorBase
    public synchronized Country detectCountry() {
        if (this.mLocationListeners != null) {
            throw new IllegalStateException();
        }
        List<String> enabledProviders = getEnabledProviders();
        int size = enabledProviders.size();
        if (size > 0) {
            this.mLocationListeners = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                String str = enabledProviders.get(i);
                if (isAcceptableProvider(str)) {
                    LocationListener locationListener = new LocationListener() { // from class: com.android.server.location.countrydetector.LocationBasedCountryDetector.1
                        @Override // android.location.LocationListener
                        public void onProviderDisabled(String str2) {
                        }

                        @Override // android.location.LocationListener
                        public void onProviderEnabled(String str2) {
                        }

                        @Override // android.location.LocationListener
                        public void onStatusChanged(String str2, int i2, Bundle bundle) {
                        }

                        @Override // android.location.LocationListener
                        public void onLocationChanged(Location location) {
                            if (location != null) {
                                LocationBasedCountryDetector.this.stop();
                                LocationBasedCountryDetector.this.queryCountryCode(location);
                            }
                        }
                    };
                    this.mLocationListeners.add(locationListener);
                    registerListener(str, locationListener);
                }
            }
            Timer timer = new Timer();
            this.mTimer = timer;
            timer.schedule(new TimerTask() { // from class: com.android.server.location.countrydetector.LocationBasedCountryDetector.2
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    LocationBasedCountryDetector locationBasedCountryDetector = LocationBasedCountryDetector.this;
                    locationBasedCountryDetector.mTimer = null;
                    locationBasedCountryDetector.stop();
                    LocationBasedCountryDetector locationBasedCountryDetector2 = LocationBasedCountryDetector.this;
                    locationBasedCountryDetector2.queryCountryCode(locationBasedCountryDetector2.getLastKnownLocation());
                }
            }, getQueryLocationTimeout());
        } else {
            queryCountryCode(getLastKnownLocation());
        }
        return this.mDetectedCountry;
    }

    @Override // com.android.server.location.countrydetector.CountryDetectorBase
    public synchronized void stop() {
        List<LocationListener> list = this.mLocationListeners;
        if (list != null) {
            for (LocationListener locationListener : list) {
                unregisterListener(locationListener);
            }
            this.mLocationListeners = null;
        }
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
            this.mTimer = null;
        }
    }

    public final synchronized void queryCountryCode(final Location location) {
        if (this.mQueryThread != null) {
            return;
        }
        Thread thread = new Thread(new Runnable() { // from class: com.android.server.location.countrydetector.LocationBasedCountryDetector.3
            @Override // java.lang.Runnable
            public void run() {
                Location location2 = location;
                if (location2 == null) {
                    LocationBasedCountryDetector.this.notifyListener(null);
                    return;
                }
                String countryFromLocation = LocationBasedCountryDetector.this.getCountryFromLocation(location2);
                if (countryFromLocation != null) {
                    LocationBasedCountryDetector.this.mDetectedCountry = new Country(countryFromLocation, 1);
                } else {
                    LocationBasedCountryDetector.this.mDetectedCountry = null;
                }
                LocationBasedCountryDetector locationBasedCountryDetector = LocationBasedCountryDetector.this;
                locationBasedCountryDetector.notifyListener(locationBasedCountryDetector.mDetectedCountry);
                LocationBasedCountryDetector.this.mQueryThread = null;
            }
        });
        this.mQueryThread = thread;
        thread.start();
    }
}
