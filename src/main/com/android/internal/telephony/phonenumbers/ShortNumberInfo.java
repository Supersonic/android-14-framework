package com.android.internal.telephony.phonenumbers;

import com.android.internal.telephony.phonenumbers.internal.MatcherApi;
import com.android.internal.telephony.phonenumbers.internal.RegexBasedMatcher;
import com.android.internal.telephony.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
import com.android.internal.telephony.phonenumbers.metadata.source.RegionMetadataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    ShortNumberInfo(MatcherApi matcherApi, RegionMetadataSource regionMetadataSource) {
        this.matcherApi = matcherApi;
        this.shortNumberMetadataSource = regionMetadataSource;
    }

    private List<String> getRegionCodesForCountryCode(int i) {
        List<String> list = this.countryCallingCodeToRegionCodeMap.get(Integer.valueOf(i));
        if (list == null) {
            list = new ArrayList<>(0);
        }
        return Collections.unmodifiableList(list);
    }

    private boolean regionDialingFromMatchesNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        return getRegionCodesForCountryCode(phonenumber$PhoneNumber.getCountryCode()).contains(str);
    }

    private Phonemetadata$PhoneMetadata getShortNumberMetadataForRegion(String str) {
        if (str == null) {
            return null;
        }
        try {
            return this.shortNumberMetadataSource.getMetadataForRegion(str);
        } catch (IllegalArgumentException unused) {
            return null;
        }
    }

    public boolean isPossibleShortNumberForRegion(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        Phonemetadata$PhoneMetadata shortNumberMetadataForRegion;
        if (regionDialingFromMatchesNumber(phonenumber$PhoneNumber, str) && (shortNumberMetadataForRegion = getShortNumberMetadataForRegion(str)) != null) {
            return shortNumberMetadataForRegion.getGeneralDesc().getPossibleLengthList().contains(Integer.valueOf(getNationalSignificantNumber(phonenumber$PhoneNumber).length()));
        }
        return false;
    }

    public boolean isPossibleShortNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        List<String> regionCodesForCountryCode = getRegionCodesForCountryCode(phonenumber$PhoneNumber.getCountryCode());
        int length = getNationalSignificantNumber(phonenumber$PhoneNumber).length();
        for (String str : regionCodesForCountryCode) {
            Phonemetadata$PhoneMetadata shortNumberMetadataForRegion = getShortNumberMetadataForRegion(str);
            if (shortNumberMetadataForRegion != null && shortNumberMetadataForRegion.getGeneralDesc().getPossibleLengthList().contains(Integer.valueOf(length))) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidShortNumberForRegion(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        Phonemetadata$PhoneMetadata shortNumberMetadataForRegion;
        if (regionDialingFromMatchesNumber(phonenumber$PhoneNumber, str) && (shortNumberMetadataForRegion = getShortNumberMetadataForRegion(str)) != null) {
            String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
            if (matchesPossibleNumberAndNationalNumber(nationalSignificantNumber, shortNumberMetadataForRegion.getGeneralDesc())) {
                return matchesPossibleNumberAndNationalNumber(nationalSignificantNumber, shortNumberMetadataForRegion.getShortCode());
            }
            return false;
        }
        return false;
    }

    public boolean isValidShortNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        List<String> regionCodesForCountryCode = getRegionCodesForCountryCode(phonenumber$PhoneNumber.getCountryCode());
        String regionCodeForShortNumberFromRegionList = getRegionCodeForShortNumberFromRegionList(phonenumber$PhoneNumber, regionCodesForCountryCode);
        if (regionCodesForCountryCode.size() <= 1 || regionCodeForShortNumberFromRegionList == null) {
            return isValidShortNumberForRegion(phonenumber$PhoneNumber, regionCodeForShortNumberFromRegionList);
        }
        return true;
    }

    public ShortNumberCost getExpectedCostForRegion(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        if (!regionDialingFromMatchesNumber(phonenumber$PhoneNumber, str)) {
            return ShortNumberCost.UNKNOWN_COST;
        }
        Phonemetadata$PhoneMetadata shortNumberMetadataForRegion = getShortNumberMetadataForRegion(str);
        if (shortNumberMetadataForRegion == null) {
            return ShortNumberCost.UNKNOWN_COST;
        }
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        if (!shortNumberMetadataForRegion.getGeneralDesc().getPossibleLengthList().contains(Integer.valueOf(nationalSignificantNumber.length()))) {
            return ShortNumberCost.UNKNOWN_COST;
        }
        if (matchesPossibleNumberAndNationalNumber(nationalSignificantNumber, shortNumberMetadataForRegion.getPremiumRate())) {
            return ShortNumberCost.PREMIUM_RATE;
        }
        if (matchesPossibleNumberAndNationalNumber(nationalSignificantNumber, shortNumberMetadataForRegion.getStandardRate())) {
            return ShortNumberCost.STANDARD_RATE;
        }
        if (matchesPossibleNumberAndNationalNumber(nationalSignificantNumber, shortNumberMetadataForRegion.getTollFree())) {
            return ShortNumberCost.TOLL_FREE;
        }
        if (isEmergencyNumber(nationalSignificantNumber, str)) {
            return ShortNumberCost.TOLL_FREE;
        }
        return ShortNumberCost.UNKNOWN_COST;
    }

    public ShortNumberCost getExpectedCost(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        List<String> regionCodesForCountryCode = getRegionCodesForCountryCode(phonenumber$PhoneNumber.getCountryCode());
        if (regionCodesForCountryCode.size() == 0) {
            return ShortNumberCost.UNKNOWN_COST;
        }
        if (regionCodesForCountryCode.size() == 1) {
            return getExpectedCostForRegion(phonenumber$PhoneNumber, regionCodesForCountryCode.get(0));
        }
        ShortNumberCost shortNumberCost = ShortNumberCost.TOLL_FREE;
        for (String str : regionCodesForCountryCode) {
            ShortNumberCost expectedCostForRegion = getExpectedCostForRegion(phonenumber$PhoneNumber, str);
            int i = C02761.f21xe1b2aad7[expectedCostForRegion.ordinal()];
            if (i == 1) {
                return ShortNumberCost.PREMIUM_RATE;
            }
            if (i == 2) {
                shortNumberCost = ShortNumberCost.UNKNOWN_COST;
            } else if (i != 3) {
                if (i != 4) {
                    Logger logger2 = logger;
                    Level level = Level.SEVERE;
                    logger2.log(level, "Unrecognised cost for region: " + expectedCostForRegion);
                }
            } else if (shortNumberCost != ShortNumberCost.UNKNOWN_COST) {
                shortNumberCost = ShortNumberCost.STANDARD_RATE;
            }
        }
        return shortNumberCost;
    }

    /* renamed from: com.android.internal.telephony.phonenumbers.ShortNumberInfo$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C02761 {

        /* renamed from: $SwitchMap$com$google$i18n$phonenumbers$ShortNumberInfo$ShortNumberCost */
        static final /* synthetic */ int[] f21xe1b2aad7;

        static {
            int[] iArr = new int[ShortNumberCost.values().length];
            f21xe1b2aad7 = iArr;
            try {
                iArr[ShortNumberCost.PREMIUM_RATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f21xe1b2aad7[ShortNumberCost.UNKNOWN_COST.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f21xe1b2aad7[ShortNumberCost.STANDARD_RATE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f21xe1b2aad7[ShortNumberCost.TOLL_FREE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private String getRegionCodeForShortNumberFromRegionList(Phonenumber$PhoneNumber phonenumber$PhoneNumber, List<String> list) {
        if (list.size() == 0) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        for (String str : list) {
            Phonemetadata$PhoneMetadata shortNumberMetadataForRegion = getShortNumberMetadataForRegion(str);
            if (shortNumberMetadataForRegion != null && matchesPossibleNumberAndNationalNumber(nationalSignificantNumber, shortNumberMetadataForRegion.getShortCode())) {
                return str;
            }
        }
        return null;
    }

    public boolean connectsToEmergencyNumber(String str, String str2) {
        return matchesEmergencyNumberHelper(str, str2, true);
    }

    public boolean isEmergencyNumber(CharSequence charSequence, String str) {
        return matchesEmergencyNumberHelper(charSequence, str, false);
    }

    private boolean matchesEmergencyNumberHelper(CharSequence charSequence, String str, boolean z) {
        Phonemetadata$PhoneMetadata shortNumberMetadataForRegion;
        CharSequence extractPossibleNumber = PhoneNumberUtil.extractPossibleNumber(charSequence);
        boolean z2 = false;
        if (PhoneNumberUtil.PLUS_CHARS_PATTERN.matcher(extractPossibleNumber).lookingAt() || (shortNumberMetadataForRegion = getShortNumberMetadataForRegion(str)) == null || !shortNumberMetadataForRegion.hasEmergency()) {
            return false;
        }
        String normalizeDigitsOnly = PhoneNumberUtil.normalizeDigitsOnly(extractPossibleNumber);
        if (z && !REGIONS_WHERE_EMERGENCY_NUMBERS_MUST_BE_EXACT.contains(str)) {
            z2 = true;
        }
        return this.matcherApi.matchNationalNumber(normalizeDigitsOnly, shortNumberMetadataForRegion.getEmergency(), z2);
    }

    public boolean isCarrierSpecific(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        String regionCodeForShortNumberFromRegionList = getRegionCodeForShortNumberFromRegionList(phonenumber$PhoneNumber, getRegionCodesForCountryCode(phonenumber$PhoneNumber.getCountryCode()));
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        Phonemetadata$PhoneMetadata shortNumberMetadataForRegion = getShortNumberMetadataForRegion(regionCodeForShortNumberFromRegionList);
        return shortNumberMetadataForRegion != null && matchesPossibleNumberAndNationalNumber(nationalSignificantNumber, shortNumberMetadataForRegion.getCarrierSpecific());
    }

    public boolean isCarrierSpecificForRegion(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        if (regionDialingFromMatchesNumber(phonenumber$PhoneNumber, str)) {
            String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
            Phonemetadata$PhoneMetadata shortNumberMetadataForRegion = getShortNumberMetadataForRegion(str);
            return shortNumberMetadataForRegion != null && matchesPossibleNumberAndNationalNumber(nationalSignificantNumber, shortNumberMetadataForRegion.getCarrierSpecific());
        }
        return false;
    }

    public boolean isSmsServiceForRegion(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        Phonemetadata$PhoneMetadata shortNumberMetadataForRegion;
        return regionDialingFromMatchesNumber(phonenumber$PhoneNumber, str) && (shortNumberMetadataForRegion = getShortNumberMetadataForRegion(str)) != null && matchesPossibleNumberAndNationalNumber(getNationalSignificantNumber(phonenumber$PhoneNumber), shortNumberMetadataForRegion.getSmsServices());
    }

    private static String getNationalSignificantNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        StringBuilder sb = new StringBuilder();
        if (phonenumber$PhoneNumber.isItalianLeadingZero()) {
            char[] cArr = new char[phonenumber$PhoneNumber.getNumberOfLeadingZeros()];
            Arrays.fill(cArr, '0');
            sb.append(new String(cArr));
        }
        sb.append(phonenumber$PhoneNumber.getNationalNumber());
        return sb.toString();
    }

    private boolean matchesPossibleNumberAndNationalNumber(String str, Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        if (phonemetadata$PhoneNumberDesc.getPossibleLengthCount() <= 0 || phonemetadata$PhoneNumberDesc.getPossibleLengthList().contains(Integer.valueOf(str.length()))) {
            return this.matcherApi.matchNationalNumber(str, phonemetadata$PhoneNumberDesc, false);
        }
        return false;
    }
}
