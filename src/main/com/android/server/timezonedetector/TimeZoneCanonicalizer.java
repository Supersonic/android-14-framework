package com.android.server.timezonedetector;

import com.android.i18n.timezone.TimeZoneFinder;
import java.util.function.Function;
/* loaded from: classes2.dex */
public final class TimeZoneCanonicalizer implements Function<String, String> {
    @Override // java.util.function.Function
    public String apply(String str) {
        String findCanonicalTimeZoneId = TimeZoneFinder.getInstance().getCountryZonesFinder().findCanonicalTimeZoneId(str);
        return findCanonicalTimeZoneId == null ? str : findCanonicalTimeZoneId;
    }
}
