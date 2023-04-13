package com.android.server.timezonedetector.location;

import java.time.Duration;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class TimeZoneProviderRequest {
    public static final TimeZoneProviderRequest STOP_UPDATES = new TimeZoneProviderRequest(false, null, null);
    public final Duration mEventFilteringAgeThreshold;
    public final Duration mInitializationTimeout;
    public final boolean mSendUpdates;

    public TimeZoneProviderRequest(boolean z, Duration duration, Duration duration2) {
        this.mSendUpdates = z;
        this.mInitializationTimeout = duration;
        this.mEventFilteringAgeThreshold = duration2;
    }

    public static TimeZoneProviderRequest createStartUpdatesRequest(Duration duration, Duration duration2) {
        Objects.requireNonNull(duration);
        Objects.requireNonNull(duration2);
        return new TimeZoneProviderRequest(true, duration, duration2);
    }

    public static TimeZoneProviderRequest createStopUpdatesRequest() {
        return STOP_UPDATES;
    }

    public boolean sendUpdates() {
        return this.mSendUpdates;
    }

    public Duration getInitializationTimeout() {
        return this.mInitializationTimeout;
    }

    public Duration getEventFilteringAgeThreshold() {
        return this.mEventFilteringAgeThreshold;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || TimeZoneProviderRequest.class != obj.getClass()) {
            return false;
        }
        TimeZoneProviderRequest timeZoneProviderRequest = (TimeZoneProviderRequest) obj;
        return this.mSendUpdates == timeZoneProviderRequest.mSendUpdates && Objects.equals(this.mInitializationTimeout, timeZoneProviderRequest.mInitializationTimeout) && Objects.equals(this.mEventFilteringAgeThreshold, timeZoneProviderRequest.mEventFilteringAgeThreshold);
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mSendUpdates), this.mInitializationTimeout, this.mEventFilteringAgeThreshold);
    }

    public String toString() {
        return "TimeZoneProviderRequest{mSendUpdates=" + this.mSendUpdates + ", mInitializationTimeout=" + this.mInitializationTimeout + ", mEventFilteringAgeThreshold=" + this.mEventFilteringAgeThreshold + "}";
    }
}
