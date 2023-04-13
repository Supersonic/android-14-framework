package com.android.server.timezonedetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class GeolocationTimeZoneSuggestion {
    public final long mEffectiveFromElapsedMillis;
    public final List<String> mZoneIds;

    public GeolocationTimeZoneSuggestion(long j, List<String> list) {
        this.mEffectiveFromElapsedMillis = j;
        if (list == null) {
            this.mZoneIds = null;
        } else {
            this.mZoneIds = Collections.unmodifiableList(new ArrayList(list));
        }
    }

    public static GeolocationTimeZoneSuggestion createUncertainSuggestion(long j) {
        return new GeolocationTimeZoneSuggestion(j, null);
    }

    public static GeolocationTimeZoneSuggestion createCertainSuggestion(long j, List<String> list) {
        return new GeolocationTimeZoneSuggestion(j, list);
    }

    public long getEffectiveFromElapsedMillis() {
        return this.mEffectiveFromElapsedMillis;
    }

    public List<String> getZoneIds() {
        return this.mZoneIds;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || GeolocationTimeZoneSuggestion.class != obj.getClass()) {
            return false;
        }
        GeolocationTimeZoneSuggestion geolocationTimeZoneSuggestion = (GeolocationTimeZoneSuggestion) obj;
        return this.mEffectiveFromElapsedMillis == geolocationTimeZoneSuggestion.mEffectiveFromElapsedMillis && Objects.equals(this.mZoneIds, geolocationTimeZoneSuggestion.mZoneIds);
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mEffectiveFromElapsedMillis), this.mZoneIds);
    }

    public String toString() {
        return "GeolocationTimeZoneSuggestion{mEffectiveFromElapsedMillis=" + this.mEffectiveFromElapsedMillis + ", mZoneIds=" + this.mZoneIds + '}';
    }
}
