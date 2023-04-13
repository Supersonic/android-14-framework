package com.android.i18n.phonenumbers.internal;

import com.android.i18n.phonenumbers.CountryCodeToRegionCodeMap;
import java.util.List;
/* loaded from: classes.dex */
public final class GeoEntityUtility {
    public static final String REGION_CODE_FOR_NON_GEO_ENTITIES = "001";

    public static boolean isGeoEntity(String regionCode) {
        return !regionCode.equals("001");
    }

    public static boolean isGeoEntity(int countryCallingCode) {
        List<String> regionCodesForCountryCallingCode = CountryCodeToRegionCodeMap.getCountryCodeToRegionCodeMap().get(Integer.valueOf(countryCallingCode));
        return (regionCodesForCountryCallingCode == null || regionCodesForCountryCallingCode.contains("001")) ? false : true;
    }

    private GeoEntityUtility() {
    }
}
