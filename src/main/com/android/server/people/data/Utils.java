package com.android.server.people.data;

import android.content.Context;
import android.location.Country;
import android.location.CountryDetector;
import java.util.Locale;
/* loaded from: classes2.dex */
public class Utils {
    public static String getCurrentCountryIso(Context context) {
        Country detectCountry;
        CountryDetector countryDetector = (CountryDetector) context.getSystemService("country_detector");
        String countryIso = (countryDetector == null || (detectCountry = countryDetector.detectCountry()) == null) ? null : detectCountry.getCountryIso();
        return countryIso == null ? Locale.getDefault().getCountry() : countryIso;
    }
}
