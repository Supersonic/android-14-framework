package com.android.server.location.countrydetector;

import android.content.Context;
import android.location.Country;
import android.location.CountryListener;
import android.location.Geocoder;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Slog;
import com.android.server.backup.BackupManagerConstants;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
/* loaded from: classes.dex */
public class ComprehensiveCountryDetector extends CountryDetectorBase {
    public int mCountServiceStateChanges;
    public Country mCountry;
    public Country mCountryFromLocation;
    public final ConcurrentLinkedQueue<Country> mDebugLogs;
    public Country mLastCountryAddedToLogs;
    public CountryListener mLocationBasedCountryDetectionListener;
    public CountryDetectorBase mLocationBasedCountryDetector;
    public Timer mLocationRefreshTimer;
    public final Object mObject;
    public PhoneStateListener mPhoneStateListener;
    public long mStartTime;
    public long mStopTime;
    public boolean mStopped;
    public final TelephonyManager mTelephonyManager;
    public int mTotalCountServiceStateChanges;
    public long mTotalTime;

    public ComprehensiveCountryDetector(Context context) {
        super(context);
        this.mStopped = false;
        this.mDebugLogs = new ConcurrentLinkedQueue<>();
        this.mObject = new Object();
        this.mLocationBasedCountryDetectionListener = new CountryListener() { // from class: com.android.server.location.countrydetector.ComprehensiveCountryDetector.1
            public void onCountryDetected(Country country) {
                ComprehensiveCountryDetector.this.mCountryFromLocation = country;
                ComprehensiveCountryDetector.this.detectCountry(true, false);
                ComprehensiveCountryDetector.this.stopLocationBasedDetector();
            }
        };
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
    }

    @Override // com.android.server.location.countrydetector.CountryDetectorBase
    public Country detectCountry() {
        return detectCountry(false, !this.mStopped);
    }

    @Override // com.android.server.location.countrydetector.CountryDetectorBase
    public void stop() {
        Slog.i("CountryDetector", "Stop the detector.");
        cancelLocationRefresh();
        removePhoneStateListener();
        stopLocationBasedDetector();
        this.mListener = null;
        this.mStopped = true;
    }

    public final Country getCountry() {
        Country networkBasedCountry = getNetworkBasedCountry();
        if (networkBasedCountry == null) {
            networkBasedCountry = getLastKnownLocationBasedCountry();
        }
        if (networkBasedCountry == null) {
            networkBasedCountry = getSimBasedCountry();
        }
        if (networkBasedCountry == null) {
            networkBasedCountry = getLocaleCountry();
        }
        addToLogs(networkBasedCountry);
        return networkBasedCountry;
    }

    public final void addToLogs(Country country) {
        if (country == null) {
            return;
        }
        synchronized (this.mObject) {
            Country country2 = this.mLastCountryAddedToLogs;
            if (country2 == null || !country2.equals(country)) {
                this.mLastCountryAddedToLogs = country;
                if (this.mDebugLogs.size() >= 20) {
                    this.mDebugLogs.poll();
                }
                this.mDebugLogs.add(country);
            }
        }
    }

    public final boolean isNetworkCountryCodeAvailable() {
        return this.mTelephonyManager.getPhoneType() == 1;
    }

    public Country getNetworkBasedCountry() {
        if (isNetworkCountryCodeAvailable()) {
            String networkCountryIso = this.mTelephonyManager.getNetworkCountryIso();
            if (TextUtils.isEmpty(networkCountryIso)) {
                return null;
            }
            return new Country(networkCountryIso, 0);
        }
        return null;
    }

    public Country getLastKnownLocationBasedCountry() {
        return this.mCountryFromLocation;
    }

    public Country getSimBasedCountry() {
        String simCountryIso = this.mTelephonyManager.getSimCountryIso();
        if (TextUtils.isEmpty(simCountryIso)) {
            return null;
        }
        return new Country(simCountryIso, 2);
    }

    public Country getLocaleCountry() {
        Locale locale = Locale.getDefault();
        if (locale != null) {
            return new Country(locale.getCountry(), 3);
        }
        return null;
    }

    public final Country detectCountry(boolean z, boolean z2) {
        Country country = getCountry();
        Country country2 = this.mCountry;
        if (country2 != null) {
            country2 = new Country(this.mCountry);
        }
        runAfterDetectionAsync(country2, country, z, z2);
        this.mCountry = country;
        return country;
    }

    public void runAfterDetectionAsync(final Country country, final Country country2, final boolean z, final boolean z2) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.location.countrydetector.ComprehensiveCountryDetector.2
            @Override // java.lang.Runnable
            public void run() {
                ComprehensiveCountryDetector.this.runAfterDetection(country, country2, z, z2);
            }
        });
    }

    @Override // com.android.server.location.countrydetector.CountryDetectorBase
    public void setCountryListener(CountryListener countryListener) {
        CountryListener countryListener2 = this.mListener;
        this.mListener = countryListener;
        if (countryListener == null) {
            removePhoneStateListener();
            stopLocationBasedDetector();
            cancelLocationRefresh();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.mStopTime = elapsedRealtime;
            this.mTotalTime += elapsedRealtime;
        } else if (countryListener2 == null) {
            addPhoneStateListener();
            detectCountry(false, true);
            this.mStartTime = SystemClock.elapsedRealtime();
            this.mStopTime = 0L;
            this.mCountServiceStateChanges = 0;
        }
    }

    public void runAfterDetection(Country country, Country country2, boolean z, boolean z2) {
        if (z) {
            notifyIfCountryChanged(country, country2);
        }
        if (z2 && ((country2 == null || country2.getSource() > 1) && ((isAirplaneModeOff() || isWifiOn()) && this.mListener != null && isGeoCoderImplemented()))) {
            startLocationBasedDetector(this.mLocationBasedCountryDetectionListener);
        }
        if (country2 == null || country2.getSource() >= 1) {
            scheduleLocationRefresh();
            return;
        }
        cancelLocationRefresh();
        stopLocationBasedDetector();
    }

    public final synchronized void startLocationBasedDetector(CountryListener countryListener) {
        if (this.mLocationBasedCountryDetector != null) {
            return;
        }
        CountryDetectorBase createLocationBasedCountryDetector = createLocationBasedCountryDetector();
        this.mLocationBasedCountryDetector = createLocationBasedCountryDetector;
        createLocationBasedCountryDetector.setCountryListener(countryListener);
        this.mLocationBasedCountryDetector.detectCountry();
    }

    public final synchronized void stopLocationBasedDetector() {
        CountryDetectorBase countryDetectorBase = this.mLocationBasedCountryDetector;
        if (countryDetectorBase != null) {
            countryDetectorBase.stop();
            this.mLocationBasedCountryDetector = null;
        }
    }

    public CountryDetectorBase createLocationBasedCountryDetector() {
        return new LocationBasedCountryDetector(this.mContext);
    }

    public boolean isAirplaneModeOff() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 0;
    }

    public boolean isWifiOn() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_on", 0) != 0;
    }

    public final void notifyIfCountryChanged(Country country, Country country2) {
        if (country2 == null || this.mListener == null) {
            return;
        }
        if (country == null || !country.equals(country2)) {
            notifyListener(country2);
        }
    }

    public final synchronized void scheduleLocationRefresh() {
        if (this.mLocationRefreshTimer != null) {
            return;
        }
        Timer timer = new Timer();
        this.mLocationRefreshTimer = timer;
        timer.schedule(new TimerTask() { // from class: com.android.server.location.countrydetector.ComprehensiveCountryDetector.3
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                ComprehensiveCountryDetector comprehensiveCountryDetector = ComprehensiveCountryDetector.this;
                comprehensiveCountryDetector.mLocationRefreshTimer = null;
                comprehensiveCountryDetector.detectCountry(false, true);
            }
        }, BackupManagerConstants.DEFAULT_FULL_BACKUP_INTERVAL_MILLISECONDS);
    }

    public final synchronized void cancelLocationRefresh() {
        Timer timer = this.mLocationRefreshTimer;
        if (timer != null) {
            timer.cancel();
            this.mLocationRefreshTimer = null;
        }
    }

    public synchronized void addPhoneStateListener() {
        if (this.mPhoneStateListener == null) {
            PhoneStateListener phoneStateListener = new PhoneStateListener() { // from class: com.android.server.location.countrydetector.ComprehensiveCountryDetector.4
                @Override // android.telephony.PhoneStateListener
                public void onServiceStateChanged(ServiceState serviceState) {
                    ComprehensiveCountryDetector.this.mCountServiceStateChanges++;
                    ComprehensiveCountryDetector.this.mTotalCountServiceStateChanges++;
                    if (ComprehensiveCountryDetector.this.isNetworkCountryCodeAvailable()) {
                        ComprehensiveCountryDetector.this.detectCountry(true, true);
                    }
                }
            };
            this.mPhoneStateListener = phoneStateListener;
            this.mTelephonyManager.listen(phoneStateListener, 1);
        }
    }

    public synchronized void removePhoneStateListener() {
        PhoneStateListener phoneStateListener = this.mPhoneStateListener;
        if (phoneStateListener != null) {
            this.mTelephonyManager.listen(phoneStateListener, 0);
            this.mPhoneStateListener = null;
        }
    }

    public boolean isGeoCoderImplemented() {
        return Geocoder.isPresent();
    }

    public String toString() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        StringBuilder sb = new StringBuilder();
        sb.append("ComprehensiveCountryDetector{");
        long j = 0;
        if (this.mStopTime == 0) {
            j = elapsedRealtime - this.mStartTime;
            sb.append("timeRunning=" + j + ", ");
        } else {
            sb.append("lastRunTimeLength=" + (this.mStopTime - this.mStartTime) + ", ");
        }
        sb.append("totalCountServiceStateChanges=" + this.mTotalCountServiceStateChanges + ", ");
        sb.append("currentCountServiceStateChanges=" + this.mCountServiceStateChanges + ", ");
        sb.append("totalTime=" + (this.mTotalTime + j) + ", ");
        sb.append("currentTime=" + elapsedRealtime + ", ");
        sb.append("countries=");
        Iterator<Country> it = this.mDebugLogs.iterator();
        while (it.hasNext()) {
            sb.append("\n   " + it.next().toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
