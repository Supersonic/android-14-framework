package com.android.server.timezonedetector;

import android.app.time.TimeZoneCapabilitiesAndConfig;
import android.app.time.TimeZoneConfiguration;
import android.app.time.TimeZoneState;
import android.app.timezonedetector.ManualTimeZoneSuggestion;
import android.app.timezonedetector.TelephonyTimeZoneSuggestion;
/* loaded from: classes2.dex */
public interface TimeZoneDetectorStrategy extends Dumpable {
    void addChangeListener(StateChangeListener stateChangeListener);

    boolean confirmTimeZone(String str);

    void enableTelephonyTimeZoneFallback(String str);

    MetricsTimeZoneDetectorState generateMetricsState();

    TimeZoneCapabilitiesAndConfig getCapabilitiesAndConfig(int i, boolean z);

    TimeZoneState getTimeZoneState();

    void handleLocationAlgorithmEvent(LocationAlgorithmEvent locationAlgorithmEvent);

    boolean isGeoTimeZoneDetectionSupported();

    boolean isTelephonyTimeZoneDetectionSupported();

    void setTimeZoneState(TimeZoneState timeZoneState);

    boolean suggestManualTimeZone(int i, ManualTimeZoneSuggestion manualTimeZoneSuggestion, boolean z);

    void suggestTelephonyTimeZone(TelephonyTimeZoneSuggestion telephonyTimeZoneSuggestion);

    boolean updateConfiguration(int i, TimeZoneConfiguration timeZoneConfiguration, boolean z);
}
