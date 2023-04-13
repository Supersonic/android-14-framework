package com.android.server.timezonedetector.location;

import android.service.timezone.TimeZoneProviderEvent;
import android.service.timezone.TimeZoneProviderStatus;
import com.android.i18n.timezone.ZoneInfoDb;
/* loaded from: classes2.dex */
public class ZoneInfoDbTimeZoneProviderEventPreProcessor implements TimeZoneProviderEventPreProcessor {
    @Override // com.android.server.timezonedetector.location.TimeZoneProviderEventPreProcessor
    public TimeZoneProviderEvent preProcess(TimeZoneProviderEvent timeZoneProviderEvent) {
        if (timeZoneProviderEvent.getSuggestion() == null || timeZoneProviderEvent.getSuggestion().getTimeZoneIds().isEmpty() || !hasInvalidZones(timeZoneProviderEvent)) {
            return timeZoneProviderEvent;
        }
        return TimeZoneProviderEvent.createUncertainEvent(timeZoneProviderEvent.getCreationElapsedMillis(), new TimeZoneProviderStatus.Builder(timeZoneProviderEvent.getTimeZoneProviderStatus()).setTimeZoneResolutionOperationStatus(3).build());
    }

    public static boolean hasInvalidZones(TimeZoneProviderEvent timeZoneProviderEvent) {
        for (String str : timeZoneProviderEvent.getSuggestion().getTimeZoneIds()) {
            if (!ZoneInfoDb.getInstance().hasTimeZone(str)) {
                LocationTimeZoneManagerService.infoLog("event=" + timeZoneProviderEvent + " has unsupported zone(" + str + ")");
                return true;
            }
        }
        return false;
    }
}
