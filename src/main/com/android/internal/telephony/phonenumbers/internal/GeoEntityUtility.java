package com.android.internal.telephony.phonenumbers.internal;

import com.android.internal.telephony.phonenumbers.CountryCodeToRegionCodeMap;
import java.util.List;
/* loaded from: classes.dex */
public final class GeoEntityUtility {
    public static final String REGION_CODE_FOR_NON_GEO_ENTITIES = "001";

    public static boolean isGeoEntity(String str) {
        return !str.equals("001");
    }

    public static boolean isGeoEntity(int i) {
        List<String> list = CountryCodeToRegionCodeMap.getCountryCodeToRegionCodeMap().get(Integer.valueOf(i));
        return (list == null || list.contains("001")) ? false : true;
    }

    private GeoEntityUtility() {
    }
}
