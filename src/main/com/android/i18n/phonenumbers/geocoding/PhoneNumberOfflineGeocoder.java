package com.android.i18n.phonenumbers.geocoding;

import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.i18n.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
import com.android.i18n.phonenumbers.prefixmapper.PrefixFileReader;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class PhoneNumberOfflineGeocoder {
    private static PhoneNumberOfflineGeocoder instance = null;
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private final PrefixFileReader prefixFileReader;

    PhoneNumberOfflineGeocoder(String phonePrefixDataDirectory) {
        this.prefixFileReader = new PrefixFileReader(phonePrefixDataDirectory);
    }

    public static synchronized PhoneNumberOfflineGeocoder getInstance() {
        PhoneNumberOfflineGeocoder phoneNumberOfflineGeocoder;
        synchronized (PhoneNumberOfflineGeocoder.class) {
            if (instance == null) {
                instance = new PhoneNumberOfflineGeocoder(DefaultMetadataDependenciesProvider.getInstance().getGeocodingDataDirectory());
            }
            phoneNumberOfflineGeocoder = instance;
        }
        return phoneNumberOfflineGeocoder;
    }

    private String getCountryNameForNumber(Phonenumber.PhoneNumber number, Locale language) {
        List<String> regionCodes = this.phoneUtil.getRegionCodesForCountryCode(number.getCountryCode());
        if (regionCodes.size() == 1) {
            return getRegionDisplayName(regionCodes.get(0), language);
        }
        String regionWhereNumberIsValid = "ZZ";
        for (String regionCode : regionCodes) {
            if (this.phoneUtil.isValidNumberForRegion(number, regionCode)) {
                if (!regionWhereNumberIsValid.equals("ZZ")) {
                    return "";
                }
                regionWhereNumberIsValid = regionCode;
            }
        }
        return getRegionDisplayName(regionWhereNumberIsValid, language);
    }

    private String getRegionDisplayName(String regionCode, Locale language) {
        if (regionCode == null || regionCode.equals("ZZ") || regionCode.equals("001")) {
            return "";
        }
        return new Locale("", regionCode).getDisplayCountry(language);
    }

    public String getDescriptionForValidNumber(Phonenumber.PhoneNumber number, Locale languageCode) {
        String region;
        Phonenumber.PhoneNumber copiedNumber;
        String langStr = languageCode.getLanguage();
        String regionStr = languageCode.getCountry();
        String mobileToken = PhoneNumberUtil.getCountryMobileToken(number.getCountryCode());
        String nationalNumber = this.phoneUtil.getNationalSignificantNumber(number);
        if (mobileToken.equals("") || !nationalNumber.startsWith(mobileToken)) {
            region = this.prefixFileReader.getDescriptionForNumber(number, langStr, "", regionStr);
        } else {
            String nationalNumber2 = nationalNumber.substring(mobileToken.length());
            String region2 = this.phoneUtil.getRegionCodeForCountryCode(number.getCountryCode());
            try {
                copiedNumber = this.phoneUtil.parse(nationalNumber2, region2);
            } catch (NumberParseException e) {
                copiedNumber = number;
            }
            region = this.prefixFileReader.getDescriptionForNumber(copiedNumber, langStr, "", regionStr);
        }
        return region.length() > 0 ? region : getCountryNameForNumber(number, languageCode);
    }

    public String getDescriptionForValidNumber(Phonenumber.PhoneNumber number, Locale languageCode, String userRegion) {
        String regionCode = this.phoneUtil.getRegionCodeForNumber(number);
        if (userRegion.equals(regionCode)) {
            return getDescriptionForValidNumber(number, languageCode);
        }
        return getRegionDisplayName(regionCode, languageCode);
    }

    public String getDescriptionForNumber(Phonenumber.PhoneNumber number, Locale languageCode) {
        PhoneNumberUtil.PhoneNumberType numberType = this.phoneUtil.getNumberType(number);
        if (numberType == PhoneNumberUtil.PhoneNumberType.UNKNOWN) {
            return "";
        }
        if (!this.phoneUtil.isNumberGeographical(numberType, number.getCountryCode())) {
            return getCountryNameForNumber(number, languageCode);
        }
        return getDescriptionForValidNumber(number, languageCode);
    }

    public String getDescriptionForNumber(Phonenumber.PhoneNumber number, Locale languageCode, String userRegion) {
        PhoneNumberUtil.PhoneNumberType numberType = this.phoneUtil.getNumberType(number);
        if (numberType == PhoneNumberUtil.PhoneNumberType.UNKNOWN) {
            return "";
        }
        if (!this.phoneUtil.isNumberGeographical(numberType, number.getCountryCode())) {
            return getCountryNameForNumber(number, languageCode);
        }
        return getDescriptionForValidNumber(number, languageCode, userRegion);
    }
}
