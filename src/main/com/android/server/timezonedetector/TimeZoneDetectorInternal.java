package com.android.server.timezonedetector;
/* loaded from: classes2.dex */
public interface TimeZoneDetectorInternal {
    MetricsTimeZoneDetectorState generateMetricsState();

    void handleLocationAlgorithmEvent(LocationAlgorithmEvent locationAlgorithmEvent);
}
