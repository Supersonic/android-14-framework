package com.android.i18n.phonenumbers;

import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.i18n.phonenumbers.internal.MatcherApi;
import com.android.i18n.phonenumbers.internal.RegexBasedMatcher;
import com.android.i18n.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
import com.android.i18n.phonenumbers.metadata.source.RegionMetadataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccil.cowan.tagsoup.Schema;
/* loaded from: classes.dex */
public class ShortNumberInfo {
    private static final Set<String> REGIONS_WHERE_EMERGENCY_NUMBERS_MUST_BE_EXACT;
    private final Map<Integer, List<String>> countryCallingCodeToRegionCodeMap = CountryCodeToRegionCodeMap.getCountryCodeToRegionCodeMap();
    private final MatcherApi matcherApi;
    private final RegionMetadataSource shortNumberMetadataSource;
    private static final Logger logger = Logger.getLogger(ShortNumberInfo.class.getName());
    private static final ShortNumberInfo INSTANCE = new ShortNumberInfo(RegexBasedMatcher.create(), DefaultMetadataDependenciesProvider.getInstance().getShortNumberMetadataSource());

    /* loaded from: classes.dex */
    public enum ShortNumberCost {
        TOLL_FREE,
        STANDARD_RATE,
        PREMIUM_RATE,
        UNKNOWN_COST
    }

    static {
        HashSet hashSet = new HashSet();
        REGIONS_WHERE_EMERGENCY_NUMBERS_MUST_BE_EXACT = hashSet;
        hashSet.add("BR");
        hashSet.add("CL");
        hashSet.add("NI");
    }

    public static ShortNumberInfo getInstance() {
        return INSTANCE;
    }

    ShortNumberInfo(MatcherApi matcherApi, RegionMetadataSource shortNumberMetadataSource) {
        this.matcherApi = matcherApi;
        this.shortNumberMetadataSource = shortNumberMetadataSource;
    }

    private List<String> getRegionCodesForCountryCode(int countryCallingCode) {
        List<String> regionCodes = this.countryCallingCodeToRegionCodeMap.get(Integer.valueOf(countryCallingCode));
        return Collections.unmodifiableList(regionCodes == null ? new ArrayList<>(0) : regionCodes);
    }

    private boolean regionDialingFromMatchesNumber(Phonenumber.PhoneNumber number, String regionDialingFrom) {
        List<String> regionCodes = getRegionCodesForCountryCode(number.getCountryCode());
        return regionCodes.contains(regionDialingFrom);
    }

    private Phonemetadata.PhoneMetadata getShortNumberMetadataForRegion(String regionCode) {
        if (regionCode == null) {
            return null;
        }
        try {
            return this.shortNumberMetadataSource.getMetadataForRegion(regionCode);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isPossibleShortNumberForRegion(Phonenumber.PhoneNumber number, String regionDialingFrom) {
        Phonemetadata.PhoneMetadata phoneMetadata;
        if (regionDialingFromMatchesNumber(number, regionDialingFrom) && (phoneMetadata = getShortNumberMetadataForRegion(regionDialingFrom)) != null) {
            int numberLength = getNationalSignificantNumber(number).length();
            return phoneMetadata.getGeneralDesc().getPossibleLengthList().contains(Integer.valueOf(numberLength));
        }
        return false;
    }

    public boolean isPossibleShortNumber(Phonenumber.PhoneNumber number) {
        List<String> regionCodes = getRegionCodesForCountryCode(number.getCountryCode());
        int shortNumberLength = getNationalSignificantNumber(number).length();
        for (String region : regionCodes) {
            Phonemetadata.PhoneMetadata phoneMetadata = getShortNumberMetadataForRegion(region);
            if (phoneMetadata != null && phoneMetadata.getGeneralDesc().getPossibleLengthList().contains(Integer.valueOf(shortNumberLength))) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidShortNumberForRegion(Phonenumber.PhoneNumber number, String regionDialingFrom) {
        Phonemetadata.PhoneMetadata phoneMetadata;
        if (regionDialingFromMatchesNumber(number, regionDialingFrom) && (phoneMetadata = getShortNumberMetadataForRegion(regionDialingFrom)) != null) {
            String shortNumber = getNationalSignificantNumber(number);
            Phonemetadata.PhoneNumberDesc generalDesc = phoneMetadata.getGeneralDesc();
            if (matchesPossibleNumberAndNationalNumber(shortNumber, generalDesc)) {
                Phonemetadata.PhoneNumberDesc shortNumberDesc = phoneMetadata.getShortCode();
                return matchesPossibleNumberAndNationalNumber(shortNumber, shortNumberDesc);
            }
            return false;
        }
        return false;
    }

    public boolean isValidShortNumber(Phonenumber.PhoneNumber number) {
        List<String> regionCodes = getRegionCodesForCountryCode(number.getCountryCode());
        String regionCode = getRegionCodeForShortNumberFromRegionList(number, regionCodes);
        if (regionCodes.size() <= 1 || regionCode == null) {
            return isValidShortNumberForRegion(number, regionCode);
        }
        return true;
    }

    public ShortNumberCost getExpectedCostForRegion(Phonenumber.PhoneNumber number, String regionDialingFrom) {
        if (!regionDialingFromMatchesNumber(number, regionDialingFrom)) {
            return ShortNumberCost.UNKNOWN_COST;
        }
        Phonemetadata.PhoneMetadata phoneMetadata = getShortNumberMetadataForRegion(regionDialingFrom);
        if (phoneMetadata == null) {
            return ShortNumberCost.UNKNOWN_COST;
        }
        String shortNumber = getNationalSignificantNumber(number);
        if (!phoneMetadata.getGeneralDesc().getPossibleLengthList().contains(Integer.valueOf(shortNumber.length()))) {
            return ShortNumberCost.UNKNOWN_COST;
        }
        if (matchesPossibleNumberAndNationalNumber(shortNumber, phoneMetadata.getPremiumRate())) {
            return ShortNumberCost.PREMIUM_RATE;
        }
        if (matchesPossibleNumberAndNationalNumber(shortNumber, phoneMetadata.getStandardRate())) {
            return ShortNumberCost.STANDARD_RATE;
        }
        if (matchesPossibleNumberAndNationalNumber(shortNumber, phoneMetadata.getTollFree())) {
            return ShortNumberCost.TOLL_FREE;
        }
        if (isEmergencyNumber(shortNumber, regionDialingFrom)) {
            return ShortNumberCost.TOLL_FREE;
        }
        return ShortNumberCost.UNKNOWN_COST;
    }

    public ShortNumberCost getExpectedCost(Phonenumber.PhoneNumber number) {
        List<String> regionCodes = getRegionCodesForCountryCode(number.getCountryCode());
        if (regionCodes.size() == 0) {
            return ShortNumberCost.UNKNOWN_COST;
        }
        if (regionCodes.size() == 1) {
            return getExpectedCostForRegion(number, regionCodes.get(0));
        }
        ShortNumberCost cost = ShortNumberCost.TOLL_FREE;
        for (String regionCode : regionCodes) {
            ShortNumberCost costForRegion = getExpectedCostForRegion(number, regionCode);
            switch (C00081.f3xd4b0fca7[costForRegion.ordinal()]) {
                case Schema.F_RESTART /* 1 */:
                    return ShortNumberCost.PREMIUM_RATE;
                case 2:
                    cost = ShortNumberCost.UNKNOWN_COST;
                    break;
                case 3:
                    if (cost != ShortNumberCost.UNKNOWN_COST) {
                        cost = ShortNumberCost.STANDARD_RATE;
                        break;
                    } else {
                        break;
                    }
                case 4:
                    break;
                default:
                    logger.log(Level.SEVERE, "Unrecognised cost for region: " + costForRegion);
                    break;
            }
        }
        return cost;
    }

    /* renamed from: com.android.i18n.phonenumbers.ShortNumberInfo$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C00081 {

        /* renamed from: $SwitchMap$com$android$i18n$phonenumbers$ShortNumberInfo$ShortNumberCost */
        static final /* synthetic */ int[] f3xd4b0fca7;

        static {
            int[] iArr = new int[ShortNumberCost.values().length];
            f3xd4b0fca7 = iArr;
            try {
                iArr[ShortNumberCost.PREMIUM_RATE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f3xd4b0fca7[ShortNumberCost.UNKNOWN_COST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f3xd4b0fca7[ShortNumberCost.STANDARD_RATE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f3xd4b0fca7[ShortNumberCost.TOLL_FREE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private String getRegionCodeForShortNumberFromRegionList(Phonenumber.PhoneNumber number, List<String> regionCodes) {
        if (regionCodes.size() == 0) {
            return null;
        }
        if (regionCodes.size() == 1) {
            return regionCodes.get(0);
        }
        String nationalNumber = getNationalSignificantNumber(number);
        for (String regionCode : regionCodes) {
            Phonemetadata.PhoneMetadata phoneMetadata = getShortNumberMetadataForRegion(regionCode);
            if (phoneMetadata != null && matchesPossibleNumberAndNationalNumber(nationalNumber, phoneMetadata.getShortCode())) {
                return regionCode;
            }
        }
        return null;
    }

    String getExampleShortNumber(String regionCode) {
        Phonemetadata.PhoneMetadata phoneMetadata = getShortNumberMetadataForRegion(regionCode);
        if (phoneMetadata == null) {
            return "";
        }
        Phonemetadata.PhoneNumberDesc desc = phoneMetadata.getShortCode();
        if (!desc.hasExampleNumber()) {
            return "";
        }
        return desc.getExampleNumber();
    }

    String getExampleShortNumberForCost(String regionCode, ShortNumberCost cost) {
        Phonemetadata.PhoneMetadata phoneMetadata = getShortNumberMetadataForRegion(regionCode);
        if (phoneMetadata == null) {
            return "";
        }
        Phonemetadata.PhoneNumberDesc desc = null;
        switch (C00081.f3xd4b0fca7[cost.ordinal()]) {
            case Schema.F_RESTART /* 1 */:
                desc = phoneMetadata.getPremiumRate();
                break;
            case 3:
                desc = phoneMetadata.getStandardRate();
                break;
            case 4:
                desc = phoneMetadata.getTollFree();
                break;
        }
        if (desc == null || !desc.hasExampleNumber()) {
            return "";
        }
        return desc.getExampleNumber();
    }

    public boolean connectsToEmergencyNumber(String number, String regionCode) {
        return matchesEmergencyNumberHelper(number, regionCode, true);
    }

    public boolean isEmergencyNumber(CharSequence number, String regionCode) {
        return matchesEmergencyNumberHelper(number, regionCode, false);
    }

    private boolean matchesEmergencyNumberHelper(CharSequence number, String regionCode, boolean allowPrefixMatch) {
        Phonemetadata.PhoneMetadata metadata;
        CharSequence possibleNumber = PhoneNumberUtil.extractPossibleNumber(number);
        boolean allowPrefixMatchForRegion = false;
        if (PhoneNumberUtil.PLUS_CHARS_PATTERN.matcher(possibleNumber).lookingAt() || (metadata = getShortNumberMetadataForRegion(regionCode)) == null || !metadata.hasEmergency()) {
            return false;
        }
        String normalizedNumber = PhoneNumberUtil.normalizeDigitsOnly(possibleNumber);
        if (allowPrefixMatch && !REGIONS_WHERE_EMERGENCY_NUMBERS_MUST_BE_EXACT.contains(regionCode)) {
            allowPrefixMatchForRegion = true;
        }
        return this.matcherApi.matchNationalNumber(normalizedNumber, metadata.getEmergency(), allowPrefixMatchForRegion);
    }

    public boolean isCarrierSpecific(Phonenumber.PhoneNumber number) {
        List<String> regionCodes = getRegionCodesForCountryCode(number.getCountryCode());
        String regionCode = getRegionCodeForShortNumberFromRegionList(number, regionCodes);
        String nationalNumber = getNationalSignificantNumber(number);
        Phonemetadata.PhoneMetadata phoneMetadata = getShortNumberMetadataForRegion(regionCode);
        return phoneMetadata != null && matchesPossibleNumberAndNationalNumber(nationalNumber, phoneMetadata.getCarrierSpecific());
    }

    public boolean isCarrierSpecificForRegion(Phonenumber.PhoneNumber number, String regionDialingFrom) {
        if (regionDialingFromMatchesNumber(number, regionDialingFrom)) {
            String nationalNumber = getNationalSignificantNumber(number);
            Phonemetadata.PhoneMetadata phoneMetadata = getShortNumberMetadataForRegion(regionDialingFrom);
            return phoneMetadata != null && matchesPossibleNumberAndNationalNumber(nationalNumber, phoneMetadata.getCarrierSpecific());
        }
        return false;
    }

    public boolean isSmsServiceForRegion(Phonenumber.PhoneNumber number, String regionDialingFrom) {
        Phonemetadata.PhoneMetadata phoneMetadata;
        return regionDialingFromMatchesNumber(number, regionDialingFrom) && (phoneMetadata = getShortNumberMetadataForRegion(regionDialingFrom)) != null && matchesPossibleNumberAndNationalNumber(getNationalSignificantNumber(number), phoneMetadata.getSmsServices());
    }

    private static String getNationalSignificantNumber(Phonenumber.PhoneNumber number) {
        StringBuilder nationalNumber = new StringBuilder();
        if (number.isItalianLeadingZero()) {
            char[] zeros = new char[number.getNumberOfLeadingZeros()];
            Arrays.fill(zeros, '0');
            nationalNumber.append(new String(zeros));
        }
        nationalNumber.append(number.getNationalNumber());
        return nationalNumber.toString();
    }

    private boolean matchesPossibleNumberAndNationalNumber(String number, Phonemetadata.PhoneNumberDesc numberDesc) {
        if (numberDesc.getPossibleLengthCount() <= 0 || numberDesc.getPossibleLengthList().contains(Integer.valueOf(number.length()))) {
            return this.matcherApi.matchNationalNumber(number, numberDesc, false);
        }
        return false;
    }
}
