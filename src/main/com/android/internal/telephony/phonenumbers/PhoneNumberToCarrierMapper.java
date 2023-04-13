package com.android.internal.telephony.phonenumbers;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.phonenumbers.PhoneNumberUtil;
import com.android.internal.telephony.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
import com.android.internal.telephony.phonenumbers.prefixmapper.PrefixFileReader;
import java.util.Locale;
/* loaded from: classes.dex */
public class PhoneNumberToCarrierMapper {
    private static PhoneNumberToCarrierMapper instance;
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private final PrefixFileReader prefixFileReader;

    PhoneNumberToCarrierMapper(String str) {
        this.prefixFileReader = new PrefixFileReader(str);
    }

    public static synchronized PhoneNumberToCarrierMapper getInstance() {
        PhoneNumberToCarrierMapper phoneNumberToCarrierMapper;
        synchronized (PhoneNumberToCarrierMapper.class) {
            if (instance == null) {
                instance = new PhoneNumberToCarrierMapper(DefaultMetadataDependenciesProvider.getInstance().getCarrierDataDirectory());
            }
            phoneNumberToCarrierMapper = instance;
        }
        return phoneNumberToCarrierMapper;
    }

    public String getNameForValidNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber, Locale locale) {
        return this.prefixFileReader.getDescriptionForNumber(phonenumber$PhoneNumber, locale.getLanguage(), PhoneConfigurationManager.SSSS, locale.getCountry());
    }

    public String getNameForNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber, Locale locale) {
        return isMobile(this.phoneUtil.getNumberType(phonenumber$PhoneNumber)) ? getNameForValidNumber(phonenumber$PhoneNumber, locale) : PhoneConfigurationManager.SSSS;
    }

    public String getSafeDisplayName(Phonenumber$PhoneNumber phonenumber$PhoneNumber, Locale locale) {
        PhoneNumberUtil phoneNumberUtil = this.phoneUtil;
        return phoneNumberUtil.isMobileNumberPortableRegion(phoneNumberUtil.getRegionCodeForNumber(phonenumber$PhoneNumber)) ? PhoneConfigurationManager.SSSS : getNameForNumber(phonenumber$PhoneNumber, locale);
    }

    private boolean isMobile(PhoneNumberUtil.PhoneNumberType phoneNumberType) {
        return phoneNumberType == PhoneNumberUtil.PhoneNumberType.MOBILE || phoneNumberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE || phoneNumberType == PhoneNumberUtil.PhoneNumberType.PAGER;
    }
}
