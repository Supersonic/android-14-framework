package com.android.i18n.phonenumbers;

import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberMatcher;
import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.i18n.phonenumbers.internal.MatcherApi;
import com.android.i18n.phonenumbers.internal.RegexBasedMatcher;
import com.android.i18n.phonenumbers.internal.RegexCache;
import com.android.i18n.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
import com.android.i18n.phonenumbers.metadata.source.MetadataSource;
import com.android.i18n.phonenumbers.metadata.source.MetadataSourceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class PhoneNumberUtil {
    private static final Map<Character, Character> ALL_PLUS_NUMBER_GROUPING_SYMBOLS;
    private static final Map<Character, Character> ALPHA_MAPPINGS;
    private static final Map<Character, Character> ALPHA_PHONE_MAPPINGS;
    private static final Pattern CAPTURING_DIGIT_PATTERN;
    private static final String CC_STRING = "$CC";
    private static final String DEFAULT_EXTN_PREFIX = " ext. ";
    private static final Map<Character, Character> DIALLABLE_CHAR_MAPPINGS;
    private static final String DIGITS = "\\p{Nd}";
    private static final Pattern EXTN_PATTERN;
    static final String EXTN_PATTERNS_FOR_MATCHING;
    private static final String EXTN_PATTERNS_FOR_PARSING;
    private static final String FG_STRING = "$FG";
    private static final Pattern FIRST_GROUP_ONLY_PREFIX_PATTERN;
    private static final Pattern FIRST_GROUP_PATTERN;
    private static final Set<Integer> GEO_MOBILE_COUNTRIES;
    private static final Set<Integer> GEO_MOBILE_COUNTRIES_WITHOUT_MOBILE_AREA_CODES;
    private static final int MAX_INPUT_STRING_LENGTH = 250;
    static final int MAX_LENGTH_COUNTRY_CODE = 3;
    static final int MAX_LENGTH_FOR_NSN = 17;
    private static final int MIN_LENGTH_FOR_NSN = 2;
    private static final Map<Integer, String> MOBILE_TOKEN_MAPPINGS;
    private static final int NANPA_COUNTRY_CODE = 1;
    static final Pattern NON_DIGITS_PATTERN;
    private static final String NP_STRING = "$NP";
    static final String PLUS_CHARS = "+＋";
    static final Pattern PLUS_CHARS_PATTERN;
    static final char PLUS_SIGN = '+';
    static final int REGEX_FLAGS = 66;
    public static final String REGION_CODE_FOR_NON_GEO_ENTITY = "001";
    private static final String RFC3966_EXTN_PREFIX = ";ext=";
    private static final String RFC3966_ISDN_SUBADDRESS = ";isub=";
    private static final String RFC3966_PHONE_CONTEXT = ";phone-context=";
    private static final String RFC3966_PREFIX = "tel:";
    private static final String SECOND_NUMBER_START = "[\\\\/] *x";
    static final Pattern SECOND_NUMBER_START_PATTERN;
    private static final Pattern SEPARATOR_PATTERN;
    private static final Pattern SINGLE_INTERNATIONAL_PREFIX;
    private static final char STAR_SIGN = '*';
    private static final String UNKNOWN_REGION = "ZZ";
    private static final String UNWANTED_END_CHARS = "[[\\P{N}&&\\P{L}]&&[^#]]+$";
    static final Pattern UNWANTED_END_CHAR_PATTERN;
    private static final String VALID_ALPHA;
    private static final Pattern VALID_ALPHA_PHONE_PATTERN;
    private static final String VALID_PHONE_NUMBER;
    private static final Pattern VALID_PHONE_NUMBER_PATTERN;
    static final String VALID_PUNCTUATION = "-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～";
    private static final String VALID_START_CHAR = "[+＋\\p{Nd}]";
    private static final Pattern VALID_START_CHAR_PATTERN;
    private static PhoneNumberUtil instance;
    private static final Logger logger = Logger.getLogger(PhoneNumberUtil.class.getName());
    private final Map<Integer, List<String>> countryCallingCodeToRegionCodeMap;
    private final MetadataSource metadataSource;
    private final MatcherApi matcherApi = RegexBasedMatcher.create();
    private final Set<String> nanpaRegions = new HashSet(35);
    private final RegexCache regexCache = new RegexCache(100);
    private final Set<String> supportedRegions = new HashSet(320);
    private final Set<Integer> countryCodesForNonGeographicalRegion = new HashSet();

    /* loaded from: classes.dex */
    public enum MatchType {
        NOT_A_NUMBER,
        NO_MATCH,
        SHORT_NSN_MATCH,
        NSN_MATCH,
        EXACT_MATCH
    }

    /* loaded from: classes.dex */
    public enum PhoneNumberFormat {
        E164,
        INTERNATIONAL,
        NATIONAL,
        RFC3966
    }

    /* loaded from: classes.dex */
    public enum PhoneNumberType {
        FIXED_LINE,
        MOBILE,
        FIXED_LINE_OR_MOBILE,
        TOLL_FREE,
        PREMIUM_RATE,
        SHARED_COST,
        VOIP,
        PERSONAL_NUMBER,
        PAGER,
        UAN,
        VOICEMAIL,
        UNKNOWN
    }

    /* loaded from: classes.dex */
    public enum ValidationResult {
        IS_POSSIBLE,
        IS_POSSIBLE_LOCAL_ONLY,
        INVALID_COUNTRY_CODE,
        TOO_SHORT,
        INVALID_LENGTH,
        TOO_LONG
    }

    static {
        HashMap<Integer, String> mobileTokenMap = new HashMap<>();
        mobileTokenMap.put(54, "9");
        MOBILE_TOKEN_MAPPINGS = Collections.unmodifiableMap(mobileTokenMap);
        HashSet<Integer> geoMobileCountriesWithoutMobileAreaCodes = new HashSet<>();
        geoMobileCountriesWithoutMobileAreaCodes.add(86);
        GEO_MOBILE_COUNTRIES_WITHOUT_MOBILE_AREA_CODES = Collections.unmodifiableSet(geoMobileCountriesWithoutMobileAreaCodes);
        HashSet<Integer> geoMobileCountries = new HashSet<>();
        geoMobileCountries.add(52);
        geoMobileCountries.add(54);
        geoMobileCountries.add(55);
        geoMobileCountries.add(62);
        geoMobileCountries.addAll(geoMobileCountriesWithoutMobileAreaCodes);
        GEO_MOBILE_COUNTRIES = Collections.unmodifiableSet(geoMobileCountries);
        HashMap<Character, Character> asciiDigitMappings = new HashMap<>();
        asciiDigitMappings.put('0', '0');
        asciiDigitMappings.put('1', '1');
        asciiDigitMappings.put('2', '2');
        asciiDigitMappings.put('3', '3');
        asciiDigitMappings.put('4', '4');
        asciiDigitMappings.put('5', '5');
        asciiDigitMappings.put('6', '6');
        asciiDigitMappings.put('7', '7');
        asciiDigitMappings.put('8', '8');
        asciiDigitMappings.put('9', '9');
        HashMap<Character, Character> alphaMap = new HashMap<>(40);
        alphaMap.put('A', '2');
        alphaMap.put('B', '2');
        alphaMap.put('C', '2');
        alphaMap.put('D', '3');
        alphaMap.put('E', '3');
        alphaMap.put('F', '3');
        alphaMap.put('G', '4');
        alphaMap.put('H', '4');
        alphaMap.put('I', '4');
        alphaMap.put('J', '5');
        alphaMap.put('K', '5');
        alphaMap.put('L', '5');
        alphaMap.put('M', '6');
        alphaMap.put('N', '6');
        alphaMap.put('O', '6');
        alphaMap.put('P', '7');
        alphaMap.put('Q', '7');
        alphaMap.put('R', '7');
        alphaMap.put('S', '7');
        alphaMap.put('T', '8');
        alphaMap.put('U', '8');
        alphaMap.put('V', '8');
        alphaMap.put('W', '9');
        alphaMap.put('X', '9');
        alphaMap.put('Y', '9');
        alphaMap.put('Z', '9');
        Map<Character, Character> unmodifiableMap = Collections.unmodifiableMap(alphaMap);
        ALPHA_MAPPINGS = unmodifiableMap;
        HashMap<Character, Character> combinedMap = new HashMap<>(100);
        combinedMap.putAll(unmodifiableMap);
        combinedMap.putAll(asciiDigitMappings);
        ALPHA_PHONE_MAPPINGS = Collections.unmodifiableMap(combinedMap);
        HashMap<Character, Character> diallableCharMap = new HashMap<>();
        diallableCharMap.putAll(asciiDigitMappings);
        Character valueOf = Character.valueOf(PLUS_SIGN);
        diallableCharMap.put(valueOf, valueOf);
        Character valueOf2 = Character.valueOf(STAR_SIGN);
        diallableCharMap.put(valueOf2, valueOf2);
        diallableCharMap.put('#', '#');
        DIALLABLE_CHAR_MAPPINGS = Collections.unmodifiableMap(diallableCharMap);
        HashMap<Character, Character> allPlusNumberGroupings = new HashMap<>();
        for (Character ch : unmodifiableMap.keySet()) {
            char c = ch.charValue();
            allPlusNumberGroupings.put(Character.valueOf(Character.toLowerCase(c)), Character.valueOf(c));
            allPlusNumberGroupings.put(Character.valueOf(c), Character.valueOf(c));
        }
        allPlusNumberGroupings.putAll(asciiDigitMappings);
        allPlusNumberGroupings.put('-', '-');
        allPlusNumberGroupings.put((char) 65293, '-');
        allPlusNumberGroupings.put((char) 8208, '-');
        allPlusNumberGroupings.put((char) 8209, '-');
        allPlusNumberGroupings.put((char) 8210, '-');
        allPlusNumberGroupings.put((char) 8211, '-');
        allPlusNumberGroupings.put((char) 8212, '-');
        allPlusNumberGroupings.put((char) 8213, '-');
        allPlusNumberGroupings.put((char) 8722, '-');
        allPlusNumberGroupings.put('/', '/');
        allPlusNumberGroupings.put((char) 65295, '/');
        allPlusNumberGroupings.put(' ', ' ');
        allPlusNumberGroupings.put((char) 12288, ' ');
        allPlusNumberGroupings.put((char) 8288, ' ');
        allPlusNumberGroupings.put('.', '.');
        allPlusNumberGroupings.put((char) 65294, '.');
        ALL_PLUS_NUMBER_GROUPING_SYMBOLS = Collections.unmodifiableMap(allPlusNumberGroupings);
        SINGLE_INTERNATIONAL_PREFIX = Pattern.compile("[\\d]+(?:[~⁓∼～][\\d]+)?");
        StringBuilder sb = new StringBuilder();
        Map<Character, Character> map = ALPHA_MAPPINGS;
        String sb2 = sb.append(Arrays.toString(map.keySet().toArray()).replaceAll("[, \\[\\]]", "")).append(Arrays.toString(map.keySet().toArray()).toLowerCase().replaceAll("[, \\[\\]]", "")).toString();
        VALID_ALPHA = sb2;
        PLUS_CHARS_PATTERN = Pattern.compile("[+＋]+");
        SEPARATOR_PATTERN = Pattern.compile("[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]+");
        CAPTURING_DIGIT_PATTERN = Pattern.compile("(\\p{Nd})");
        VALID_START_CHAR_PATTERN = Pattern.compile(VALID_START_CHAR);
        SECOND_NUMBER_START_PATTERN = Pattern.compile(SECOND_NUMBER_START);
        UNWANTED_END_CHAR_PATTERN = Pattern.compile(UNWANTED_END_CHARS);
        VALID_ALPHA_PHONE_PATTERN = Pattern.compile("(?:.*?[A-Za-z]){3}.*");
        String str = "\\p{Nd}{2}|[+＋]*+(?:[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～*]*\\p{Nd}){3,}[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～*" + sb2 + DIGITS + "]*";
        VALID_PHONE_NUMBER = str;
        String createExtnPattern = createExtnPattern(true);
        EXTN_PATTERNS_FOR_PARSING = createExtnPattern;
        EXTN_PATTERNS_FOR_MATCHING = createExtnPattern(false);
        EXTN_PATTERN = Pattern.compile("(?:" + createExtnPattern + ")$", REGEX_FLAGS);
        VALID_PHONE_NUMBER_PATTERN = Pattern.compile(str + "(?:" + createExtnPattern + ")?", REGEX_FLAGS);
        NON_DIGITS_PATTERN = Pattern.compile("(\\D+)");
        FIRST_GROUP_PATTERN = Pattern.compile("(\\$\\d)");
        FIRST_GROUP_ONLY_PREFIX_PATTERN = Pattern.compile("\\(?\\$1\\)?");
        instance = null;
    }

    private static String extnDigits(int maxLength) {
        return "(\\p{Nd}{1," + maxLength + "})";
    }

    private static String createExtnPattern(boolean forParsing) {
        String rfcExtn = RFC3966_EXTN_PREFIX + extnDigits(20);
        String explicitExtn = "[  \\t,]*(?:e?xt(?:ensi(?:ó?|ó))?n?|ｅ?ｘｔｎ?|доб|anexo)[:\\.．]?[  \\t,-]*" + extnDigits(20) + "#?";
        String ambiguousExtn = "[  \\t,]*(?:[xｘ#＃~～]|int|ｉｎｔ)[:\\.．]?[  \\t,-]*" + extnDigits(9) + "#?";
        String americanStyleExtnWithSuffix = "[- ]+" + extnDigits(6) + "#";
        String extensionPattern = rfcExtn + "|" + explicitExtn + "|" + ambiguousExtn + "|" + americanStyleExtnWithSuffix;
        if (forParsing) {
            StringBuilder append = new StringBuilder().append("[  \\t]*").append("(?:,{2}|;)").append("[:\\.．]?[  \\t,-]*");
            String autoDiallingAndExtLabelsFound = extnDigits(15);
            String autoDiallingExtn = append.append(autoDiallingAndExtLabelsFound).append("#?").toString();
            String onlyCommasExtn = "[  \\t]*(?:,)+[:\\.．]?[  \\t,-]*" + extnDigits(9) + "#?";
            return extensionPattern + "|" + autoDiallingExtn + "|" + onlyCommasExtn;
        }
        return extensionPattern;
    }

    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: classes.dex */
    public static abstract class Leniency {
        public static final Leniency POSSIBLE = new C00021("POSSIBLE", 0);
        public static final Leniency VALID = new C00032("VALID", 1);
        public static final Leniency STRICT_GROUPING = new C00043("STRICT_GROUPING", 2);
        public static final Leniency EXACT_GROUPING = new C00064("EXACT_GROUPING", PhoneNumberUtil.MAX_LENGTH_COUNTRY_CODE);
        private static final /* synthetic */ Leniency[] $VALUES = $values();

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract boolean verify(Phonenumber.PhoneNumber phoneNumber, CharSequence charSequence, PhoneNumberUtil phoneNumberUtil, PhoneNumberMatcher phoneNumberMatcher);

        private static /* synthetic */ Leniency[] $values() {
            return new Leniency[]{POSSIBLE, VALID, STRICT_GROUPING, EXACT_GROUPING};
        }

        private Leniency(String str, int i) {
        }

        public static Leniency valueOf(String name) {
            return (Leniency) Enum.valueOf(Leniency.class, name);
        }

        public static Leniency[] values() {
            return (Leniency[]) $VALUES.clone();
        }

        /* renamed from: com.android.i18n.phonenumbers.PhoneNumberUtil$Leniency$1 */
        /* loaded from: classes.dex */
        enum C00021 extends Leniency {
            private C00021(String str, int i) {
                super(str, i);
            }

            @Override // com.android.i18n.phonenumbers.PhoneNumberUtil.Leniency
            boolean verify(Phonenumber.PhoneNumber number, CharSequence candidate, PhoneNumberUtil util, PhoneNumberMatcher matcher) {
                return util.isPossibleNumber(number);
            }
        }

        /* renamed from: com.android.i18n.phonenumbers.PhoneNumberUtil$Leniency$2 */
        /* loaded from: classes.dex */
        enum C00032 extends Leniency {
            private C00032(String str, int i) {
                super(str, i);
            }

            @Override // com.android.i18n.phonenumbers.PhoneNumberUtil.Leniency
            boolean verify(Phonenumber.PhoneNumber number, CharSequence candidate, PhoneNumberUtil util, PhoneNumberMatcher matcher) {
                if (!util.isValidNumber(number) || !PhoneNumberMatcher.containsOnlyValidXChars(number, candidate.toString(), util)) {
                    return false;
                }
                return PhoneNumberMatcher.isNationalPrefixPresentIfRequired(number, util);
            }
        }

        /* renamed from: com.android.i18n.phonenumbers.PhoneNumberUtil$Leniency$3 */
        /* loaded from: classes.dex */
        enum C00043 extends Leniency {
            private C00043(String str, int i) {
                super(str, i);
            }

            @Override // com.android.i18n.phonenumbers.PhoneNumberUtil.Leniency
            boolean verify(Phonenumber.PhoneNumber number, CharSequence candidate, PhoneNumberUtil util, PhoneNumberMatcher matcher) {
                String candidateString = candidate.toString();
                if (!util.isValidNumber(number) || !PhoneNumberMatcher.containsOnlyValidXChars(number, candidateString, util) || PhoneNumberMatcher.containsMoreThanOneSlashInNationalNumber(number, candidateString) || !PhoneNumberMatcher.isNationalPrefixPresentIfRequired(number, util)) {
                    return false;
                }
                return matcher.checkNumberGroupingIsValid(number, candidate, util, new PhoneNumberMatcher.NumberGroupingChecker() { // from class: com.android.i18n.phonenumbers.PhoneNumberUtil.Leniency.3.1
                    @Override // com.android.i18n.phonenumbers.PhoneNumberMatcher.NumberGroupingChecker
                    public boolean checkGroups(PhoneNumberUtil util2, Phonenumber.PhoneNumber number2, StringBuilder normalizedCandidate, String[] expectedNumberGroups) {
                        return PhoneNumberMatcher.allNumberGroupsRemainGrouped(util2, number2, normalizedCandidate, expectedNumberGroups);
                    }
                });
            }
        }

        /* renamed from: com.android.i18n.phonenumbers.PhoneNumberUtil$Leniency$4 */
        /* loaded from: classes.dex */
        enum C00064 extends Leniency {
            private C00064(String str, int i) {
                super(str, i);
            }

            @Override // com.android.i18n.phonenumbers.PhoneNumberUtil.Leniency
            boolean verify(Phonenumber.PhoneNumber number, CharSequence candidate, PhoneNumberUtil util, PhoneNumberMatcher matcher) {
                String candidateString = candidate.toString();
                if (!util.isValidNumber(number) || !PhoneNumberMatcher.containsOnlyValidXChars(number, candidateString, util) || PhoneNumberMatcher.containsMoreThanOneSlashInNationalNumber(number, candidateString) || !PhoneNumberMatcher.isNationalPrefixPresentIfRequired(number, util)) {
                    return false;
                }
                return matcher.checkNumberGroupingIsValid(number, candidate, util, new PhoneNumberMatcher.NumberGroupingChecker() { // from class: com.android.i18n.phonenumbers.PhoneNumberUtil.Leniency.4.1
                    @Override // com.android.i18n.phonenumbers.PhoneNumberMatcher.NumberGroupingChecker
                    public boolean checkGroups(PhoneNumberUtil util2, Phonenumber.PhoneNumber number2, StringBuilder normalizedCandidate, String[] expectedNumberGroups) {
                        return PhoneNumberMatcher.allNumberGroupsAreExactlyPresent(util2, number2, normalizedCandidate, expectedNumberGroups);
                    }
                });
            }
        }
    }

    PhoneNumberUtil(MetadataSource metadataSource, Map<Integer, List<String>> countryCallingCodeToRegionCodeMap) {
        this.metadataSource = metadataSource;
        this.countryCallingCodeToRegionCodeMap = countryCallingCodeToRegionCodeMap;
        for (Map.Entry<Integer, List<String>> entry : countryCallingCodeToRegionCodeMap.entrySet()) {
            List<String> regionCodes = entry.getValue();
            if (regionCodes.size() == 1 && "001".equals(regionCodes.get(0))) {
                this.countryCodesForNonGeographicalRegion.add(entry.getKey());
            } else {
                this.supportedRegions.addAll(regionCodes);
            }
        }
        if (this.supportedRegions.remove("001")) {
            logger.log(Level.WARNING, "invalid metadata (country calling code was mapped to the non-geo entity as well as specific region(s))");
        }
        this.nanpaRegions.addAll(countryCallingCodeToRegionCodeMap.get(1));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence extractPossibleNumber(CharSequence number) {
        Matcher m = VALID_START_CHAR_PATTERN.matcher(number);
        if (m.find()) {
            CharSequence number2 = number.subSequence(m.start(), number.length());
            Matcher trailingCharsMatcher = UNWANTED_END_CHAR_PATTERN.matcher(number2);
            if (trailingCharsMatcher.find()) {
                number2 = number2.subSequence(0, trailingCharsMatcher.start());
            }
            Matcher secondNumber = SECOND_NUMBER_START_PATTERN.matcher(number2);
            if (secondNumber.find()) {
                return number2.subSequence(0, secondNumber.start());
            }
            return number2;
        }
        return "";
    }

    static boolean isViablePhoneNumber(CharSequence number) {
        if (number.length() < 2) {
            return false;
        }
        Matcher m = VALID_PHONE_NUMBER_PATTERN.matcher(number);
        return m.matches();
    }

    static StringBuilder normalize(StringBuilder number) {
        Matcher m = VALID_ALPHA_PHONE_PATTERN.matcher(number);
        if (m.matches()) {
            number.replace(0, number.length(), normalizeHelper(number, ALPHA_PHONE_MAPPINGS, true));
        } else {
            number.replace(0, number.length(), normalizeDigitsOnly(number));
        }
        return number;
    }

    public static String normalizeDigitsOnly(CharSequence number) {
        return normalizeDigits(number, false).toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static StringBuilder normalizeDigits(CharSequence number, boolean keepNonDigits) {
        StringBuilder normalizedDigits = new StringBuilder(number.length());
        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                normalizedDigits.append(digit);
            } else if (keepNonDigits) {
                normalizedDigits.append(c);
            }
        }
        return normalizedDigits;
    }

    public static String normalizeDiallableCharsOnly(CharSequence number) {
        return normalizeHelper(number, DIALLABLE_CHAR_MAPPINGS, true);
    }

    public static String convertAlphaCharactersInNumber(CharSequence number) {
        return normalizeHelper(number, ALPHA_PHONE_MAPPINGS, false);
    }

    public int getLengthOfGeographicalAreaCode(Phonenumber.PhoneNumber number) {
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegion(getRegionCodeForNumber(number));
        if (metadata == null) {
            return 0;
        }
        if (!metadata.hasNationalPrefix() && !number.isItalianLeadingZero()) {
            return 0;
        }
        PhoneNumberType type = getNumberType(number);
        int countryCallingCode = number.getCountryCode();
        if ((type == PhoneNumberType.MOBILE && GEO_MOBILE_COUNTRIES_WITHOUT_MOBILE_AREA_CODES.contains(Integer.valueOf(countryCallingCode))) || !isNumberGeographical(type, countryCallingCode)) {
            return 0;
        }
        return getLengthOfNationalDestinationCode(number);
    }

    public int getLengthOfNationalDestinationCode(Phonenumber.PhoneNumber number) {
        Phonenumber.PhoneNumber copiedProto;
        if (number.hasExtension()) {
            copiedProto = new Phonenumber.PhoneNumber();
            copiedProto.mergeFrom(number);
            copiedProto.clearExtension();
        } else {
            copiedProto = number;
        }
        String nationalSignificantNumber = format(copiedProto, PhoneNumberFormat.INTERNATIONAL);
        String[] numberGroups = NON_DIGITS_PATTERN.split(nationalSignificantNumber);
        if (numberGroups.length <= MAX_LENGTH_COUNTRY_CODE) {
            return 0;
        }
        if (getNumberType(number) == PhoneNumberType.MOBILE) {
            String mobileToken = getCountryMobileToken(number.getCountryCode());
            if (!mobileToken.equals("")) {
                return numberGroups[2].length() + numberGroups[MAX_LENGTH_COUNTRY_CODE].length();
            }
        }
        String mobileToken2 = numberGroups[2];
        return mobileToken2.length();
    }

    public static String getCountryMobileToken(int countryCallingCode) {
        Map<Integer, String> map = MOBILE_TOKEN_MAPPINGS;
        if (map.containsKey(Integer.valueOf(countryCallingCode))) {
            return map.get(Integer.valueOf(countryCallingCode));
        }
        return "";
    }

    private static String normalizeHelper(CharSequence number, Map<Character, Character> normalizationReplacements, boolean removeNonMatches) {
        StringBuilder normalizedNumber = new StringBuilder(number.length());
        for (int i = 0; i < number.length(); i++) {
            char character = number.charAt(i);
            Character newDigit = normalizationReplacements.get(Character.valueOf(Character.toUpperCase(character)));
            if (newDigit != null) {
                normalizedNumber.append(newDigit);
            } else if (!removeNonMatches) {
                normalizedNumber.append(character);
            }
        }
        return normalizedNumber.toString();
    }

    static synchronized void setInstance(PhoneNumberUtil util) {
        synchronized (PhoneNumberUtil.class) {
            instance = util;
        }
    }

    public Set<String> getSupportedRegions() {
        return Collections.unmodifiableSet(this.supportedRegions);
    }

    public Set<Integer> getSupportedGlobalNetworkCallingCodes() {
        return Collections.unmodifiableSet(this.countryCodesForNonGeographicalRegion);
    }

    public Set<Integer> getSupportedCallingCodes() {
        return Collections.unmodifiableSet(this.countryCallingCodeToRegionCodeMap.keySet());
    }

    private static boolean descHasPossibleNumberData(Phonemetadata.PhoneNumberDesc desc) {
        return (desc.getPossibleLengthCount() == 1 && desc.getPossibleLength(0) == -1) ? false : true;
    }

    private static boolean descHasData(Phonemetadata.PhoneNumberDesc desc) {
        return desc.hasExampleNumber() || descHasPossibleNumberData(desc) || desc.hasNationalNumberPattern();
    }

    private Set<PhoneNumberType> getSupportedTypesForMetadata(Phonemetadata.PhoneMetadata metadata) {
        PhoneNumberType[] values;
        Set<PhoneNumberType> types = new TreeSet<>();
        for (PhoneNumberType type : PhoneNumberType.values()) {
            if (type != PhoneNumberType.FIXED_LINE_OR_MOBILE && type != PhoneNumberType.UNKNOWN && descHasData(getNumberDescByType(metadata, type))) {
                types.add(type);
            }
        }
        return Collections.unmodifiableSet(types);
    }

    public Set<PhoneNumberType> getSupportedTypesForRegion(String regionCode) {
        if (!isValidRegionCode(regionCode)) {
            logger.log(Level.WARNING, "Invalid or unknown region code provided: " + regionCode);
            return Collections.unmodifiableSet(new TreeSet());
        }
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegion(regionCode);
        return getSupportedTypesForMetadata(metadata);
    }

    public Set<PhoneNumberType> getSupportedTypesForNonGeoEntity(int countryCallingCode) {
        Phonemetadata.PhoneMetadata metadata = getMetadataForNonGeographicalRegion(countryCallingCode);
        if (metadata == null) {
            logger.log(Level.WARNING, "Unknown country calling code for a non-geographical entity provided: " + countryCallingCode);
            return Collections.unmodifiableSet(new TreeSet());
        }
        return getSupportedTypesForMetadata(metadata);
    }

    public static synchronized PhoneNumberUtil getInstance() {
        PhoneNumberUtil phoneNumberUtil;
        synchronized (PhoneNumberUtil.class) {
            if (instance == null) {
                MetadataLoader metadataLoader = DefaultMetadataDependenciesProvider.getInstance().getMetadataLoader();
                setInstance(createInstance(metadataLoader));
            }
            phoneNumberUtil = instance;
        }
        return phoneNumberUtil;
    }

    public static PhoneNumberUtil createInstance(MetadataLoader metadataLoader) {
        if (metadataLoader == null) {
            throw new IllegalArgumentException("metadataLoader could not be null.");
        }
        return createInstance(new MetadataSourceImpl(DefaultMetadataDependenciesProvider.getInstance().getPhoneNumberMetadataFileNameProvider(), metadataLoader, DefaultMetadataDependenciesProvider.getInstance().getMetadataParser()));
    }

    private static PhoneNumberUtil createInstance(MetadataSource metadataSource) {
        if (metadataSource == null) {
            throw new IllegalArgumentException("metadataSource could not be null.");
        }
        return new PhoneNumberUtil(metadataSource, CountryCodeToRegionCodeMap.getCountryCodeToRegionCodeMap());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean formattingRuleHasFirstGroupOnly(String nationalPrefixFormattingRule) {
        return nationalPrefixFormattingRule.length() == 0 || FIRST_GROUP_ONLY_PREFIX_PATTERN.matcher(nationalPrefixFormattingRule).matches();
    }

    public boolean isNumberGeographical(Phonenumber.PhoneNumber phoneNumber) {
        return isNumberGeographical(getNumberType(phoneNumber), phoneNumber.getCountryCode());
    }

    public boolean isNumberGeographical(PhoneNumberType phoneNumberType, int countryCallingCode) {
        return phoneNumberType == PhoneNumberType.FIXED_LINE || phoneNumberType == PhoneNumberType.FIXED_LINE_OR_MOBILE || (GEO_MOBILE_COUNTRIES.contains(Integer.valueOf(countryCallingCode)) && phoneNumberType == PhoneNumberType.MOBILE);
    }

    private boolean isValidRegionCode(String regionCode) {
        return regionCode != null && this.supportedRegions.contains(regionCode);
    }

    private boolean hasValidCountryCallingCode(int countryCallingCode) {
        return this.countryCallingCodeToRegionCodeMap.containsKey(Integer.valueOf(countryCallingCode));
    }

    public String format(Phonenumber.PhoneNumber number, PhoneNumberFormat numberFormat) {
        if (number.getNationalNumber() == 0 && number.hasRawInput()) {
            String rawInput = number.getRawInput();
            if (rawInput.length() > 0) {
                return rawInput;
            }
        }
        StringBuilder formattedNumber = new StringBuilder(20);
        format(number, numberFormat, formattedNumber);
        return formattedNumber.toString();
    }

    public void format(Phonenumber.PhoneNumber number, PhoneNumberFormat numberFormat, StringBuilder formattedNumber) {
        formattedNumber.setLength(0);
        int countryCallingCode = number.getCountryCode();
        String nationalSignificantNumber = getNationalSignificantNumber(number);
        if (numberFormat == PhoneNumberFormat.E164) {
            formattedNumber.append(nationalSignificantNumber);
            prefixNumberWithCountryCallingCode(countryCallingCode, PhoneNumberFormat.E164, formattedNumber);
        } else if (!hasValidCountryCallingCode(countryCallingCode)) {
            formattedNumber.append(nationalSignificantNumber);
        } else {
            String regionCode = getRegionCodeForCountryCode(countryCallingCode);
            Phonemetadata.PhoneMetadata metadata = getMetadataForRegionOrCallingCode(countryCallingCode, regionCode);
            formattedNumber.append(formatNsn(nationalSignificantNumber, metadata, numberFormat));
            maybeAppendFormattedExtension(number, metadata, numberFormat, formattedNumber);
            prefixNumberWithCountryCallingCode(countryCallingCode, numberFormat, formattedNumber);
        }
    }

    public String formatByPattern(Phonenumber.PhoneNumber number, PhoneNumberFormat numberFormat, List<Phonemetadata.NumberFormat> userDefinedFormats) {
        int countryCallingCode = number.getCountryCode();
        String nationalSignificantNumber = getNationalSignificantNumber(number);
        if (!hasValidCountryCallingCode(countryCallingCode)) {
            return nationalSignificantNumber;
        }
        String regionCode = getRegionCodeForCountryCode(countryCallingCode);
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegionOrCallingCode(countryCallingCode, regionCode);
        StringBuilder formattedNumber = new StringBuilder(20);
        Phonemetadata.NumberFormat formattingPattern = chooseFormattingPatternForNumber(userDefinedFormats, nationalSignificantNumber);
        if (formattingPattern == null) {
            formattedNumber.append(nationalSignificantNumber);
        } else {
            Phonemetadata.NumberFormat.Builder numFormatCopy = Phonemetadata.NumberFormat.newBuilder();
            numFormatCopy.mergeFrom(formattingPattern);
            String nationalPrefixFormattingRule = formattingPattern.getNationalPrefixFormattingRule();
            if (nationalPrefixFormattingRule.length() > 0) {
                String nationalPrefix = metadata.getNationalPrefix();
                if (nationalPrefix.length() > 0) {
                    numFormatCopy.setNationalPrefixFormattingRule(nationalPrefixFormattingRule.replace(NP_STRING, nationalPrefix).replace(FG_STRING, "$1"));
                } else {
                    numFormatCopy.clearNationalPrefixFormattingRule();
                }
            }
            formattedNumber.append(formatNsnUsingPattern(nationalSignificantNumber, numFormatCopy.build(), numberFormat));
        }
        maybeAppendFormattedExtension(number, metadata, numberFormat, formattedNumber);
        prefixNumberWithCountryCallingCode(countryCallingCode, numberFormat, formattedNumber);
        return formattedNumber.toString();
    }

    public String formatNationalNumberWithCarrierCode(Phonenumber.PhoneNumber number, CharSequence carrierCode) {
        int countryCallingCode = number.getCountryCode();
        String nationalSignificantNumber = getNationalSignificantNumber(number);
        if (!hasValidCountryCallingCode(countryCallingCode)) {
            return nationalSignificantNumber;
        }
        String regionCode = getRegionCodeForCountryCode(countryCallingCode);
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegionOrCallingCode(countryCallingCode, regionCode);
        StringBuilder formattedNumber = new StringBuilder(20);
        formattedNumber.append(formatNsn(nationalSignificantNumber, metadata, PhoneNumberFormat.NATIONAL, carrierCode));
        maybeAppendFormattedExtension(number, metadata, PhoneNumberFormat.NATIONAL, formattedNumber);
        prefixNumberWithCountryCallingCode(countryCallingCode, PhoneNumberFormat.NATIONAL, formattedNumber);
        return formattedNumber.toString();
    }

    private Phonemetadata.PhoneMetadata getMetadataForRegionOrCallingCode(int countryCallingCode, String regionCode) {
        if ("001".equals(regionCode)) {
            return getMetadataForNonGeographicalRegion(countryCallingCode);
        }
        return getMetadataForRegion(regionCode);
    }

    public String formatNationalNumberWithPreferredCarrierCode(Phonenumber.PhoneNumber number, CharSequence fallbackCarrierCode) {
        CharSequence charSequence;
        if (number.getPreferredDomesticCarrierCode().length() > 0) {
            charSequence = number.getPreferredDomesticCarrierCode();
        } else {
            charSequence = fallbackCarrierCode;
        }
        return formatNationalNumberWithCarrierCode(number, charSequence);
    }

    public String formatNumberForMobileDialing(Phonenumber.PhoneNumber number, String regionCallingFrom, boolean withFormatting) {
        int countryCallingCode = number.getCountryCode();
        if (!hasValidCountryCallingCode(countryCallingCode)) {
            return number.hasRawInput() ? number.getRawInput() : "";
        }
        String formattedNumber = "";
        Phonenumber.PhoneNumber numberNoExt = new Phonenumber.PhoneNumber().mergeFrom(number).clearExtension();
        String regionCode = getRegionCodeForCountryCode(countryCallingCode);
        PhoneNumberType numberType = getNumberType(numberNoExt);
        boolean isFixedLineOrMobile = false;
        boolean isValidNumber = numberType != PhoneNumberType.UNKNOWN;
        if (regionCallingFrom.equals(regionCode)) {
            if (numberType == PhoneNumberType.FIXED_LINE || numberType == PhoneNumberType.MOBILE || numberType == PhoneNumberType.FIXED_LINE_OR_MOBILE) {
                isFixedLineOrMobile = true;
            }
            if (regionCode.equals("BR") && isFixedLineOrMobile) {
                formattedNumber = numberNoExt.getPreferredDomesticCarrierCode().length() > 0 ? formatNationalNumberWithPreferredCarrierCode(numberNoExt, "") : "";
            } else if (countryCallingCode == 1) {
                Phonemetadata.PhoneMetadata regionMetadata = getMetadataForRegion(regionCallingFrom);
                formattedNumber = (!canBeInternationallyDialled(numberNoExt) || testNumberLength(getNationalSignificantNumber(numberNoExt), regionMetadata) == ValidationResult.TOO_SHORT) ? format(numberNoExt, PhoneNumberFormat.NATIONAL) : format(numberNoExt, PhoneNumberFormat.INTERNATIONAL);
            } else {
                formattedNumber = ((regionCode.equals("001") || ((regionCode.equals("MX") || regionCode.equals("CL") || regionCode.equals("UZ")) && isFixedLineOrMobile)) && canBeInternationallyDialled(numberNoExt)) ? format(numberNoExt, PhoneNumberFormat.INTERNATIONAL) : format(numberNoExt, PhoneNumberFormat.NATIONAL);
            }
        } else if (isValidNumber && canBeInternationallyDialled(numberNoExt)) {
            return withFormatting ? format(numberNoExt, PhoneNumberFormat.INTERNATIONAL) : format(numberNoExt, PhoneNumberFormat.E164);
        }
        return withFormatting ? formattedNumber : normalizeDiallableCharsOnly(formattedNumber);
    }

    public String formatOutOfCountryCallingNumber(Phonenumber.PhoneNumber number, String regionCallingFrom) {
        if (!isValidRegionCode(regionCallingFrom)) {
            logger.log(Level.WARNING, "Trying to format number from invalid region " + regionCallingFrom + ". International formatting applied.");
            return format(number, PhoneNumberFormat.INTERNATIONAL);
        }
        int countryCallingCode = number.getCountryCode();
        String nationalSignificantNumber = getNationalSignificantNumber(number);
        if (!hasValidCountryCallingCode(countryCallingCode)) {
            return nationalSignificantNumber;
        }
        if (countryCallingCode == 1) {
            if (isNANPACountry(regionCallingFrom)) {
                return countryCallingCode + " " + format(number, PhoneNumberFormat.NATIONAL);
            }
        } else if (countryCallingCode == getCountryCodeForValidRegion(regionCallingFrom)) {
            return format(number, PhoneNumberFormat.NATIONAL);
        }
        Phonemetadata.PhoneMetadata metadataForRegionCallingFrom = getMetadataForRegion(regionCallingFrom);
        String internationalPrefix = metadataForRegionCallingFrom.getInternationalPrefix();
        String internationalPrefixForFormatting = "";
        if (metadataForRegionCallingFrom.hasPreferredInternationalPrefix()) {
            internationalPrefixForFormatting = metadataForRegionCallingFrom.getPreferredInternationalPrefix();
        } else if (SINGLE_INTERNATIONAL_PREFIX.matcher(internationalPrefix).matches()) {
            internationalPrefixForFormatting = internationalPrefix;
        }
        String regionCode = getRegionCodeForCountryCode(countryCallingCode);
        Phonemetadata.PhoneMetadata metadataForRegion = getMetadataForRegionOrCallingCode(countryCallingCode, regionCode);
        String formattedNationalNumber = formatNsn(nationalSignificantNumber, metadataForRegion, PhoneNumberFormat.INTERNATIONAL);
        StringBuilder formattedNumber = new StringBuilder(formattedNationalNumber);
        maybeAppendFormattedExtension(number, metadataForRegion, PhoneNumberFormat.INTERNATIONAL, formattedNumber);
        if (internationalPrefixForFormatting.length() > 0) {
            formattedNumber.insert(0, " ").insert(0, countryCallingCode).insert(0, " ").insert(0, internationalPrefixForFormatting);
        } else {
            prefixNumberWithCountryCallingCode(countryCallingCode, PhoneNumberFormat.INTERNATIONAL, formattedNumber);
        }
        return formattedNumber.toString();
    }

    public String formatInOriginalFormat(Phonenumber.PhoneNumber number, String regionCallingFrom) {
        String regionCode;
        if (number.hasRawInput() && !hasFormattingPatternForNumber(number)) {
            return number.getRawInput();
        }
        if (!number.hasCountryCodeSource()) {
            return format(number, PhoneNumberFormat.NATIONAL);
        }
        switch (C00012.f2xc1c7dc0a[number.getCountryCodeSource().ordinal()]) {
            case 1:
                regionCode = format(number, PhoneNumberFormat.INTERNATIONAL);
                break;
            case 2:
                regionCode = formatOutOfCountryCallingNumber(number, regionCallingFrom);
                break;
            case MAX_LENGTH_COUNTRY_CODE /* 3 */:
                regionCode = format(number, PhoneNumberFormat.INTERNATIONAL).substring(1);
                break;
            default:
                String regionCode2 = getRegionCodeForCountryCode(number.getCountryCode());
                String nationalPrefix = getNddPrefixForRegion(regionCode2, true);
                String nationalFormat = format(number, PhoneNumberFormat.NATIONAL);
                if (nationalPrefix == null || nationalPrefix.length() == 0) {
                    regionCode = nationalFormat;
                    break;
                } else if (rawInputContainsNationalPrefix(number.getRawInput(), nationalPrefix, regionCode2)) {
                    regionCode = nationalFormat;
                    break;
                } else {
                    Phonemetadata.PhoneMetadata metadata = getMetadataForRegion(regionCode2);
                    String nationalNumber = getNationalSignificantNumber(number);
                    Phonemetadata.NumberFormat formatRule = chooseFormattingPatternForNumber(metadata.getNumberFormatList(), nationalNumber);
                    if (formatRule == null) {
                        regionCode = nationalFormat;
                        break;
                    } else {
                        String candidateNationalPrefixRule = formatRule.getNationalPrefixFormattingRule();
                        int indexOfFirstGroup = candidateNationalPrefixRule.indexOf("$1");
                        if (indexOfFirstGroup <= 0) {
                            regionCode = nationalFormat;
                            break;
                        } else if (normalizeDigitsOnly(candidateNationalPrefixRule.substring(0, indexOfFirstGroup)).length() == 0) {
                            regionCode = nationalFormat;
                            break;
                        } else {
                            Phonemetadata.NumberFormat.Builder numFormatCopy = Phonemetadata.NumberFormat.newBuilder();
                            numFormatCopy.mergeFrom(formatRule);
                            numFormatCopy.clearNationalPrefixFormattingRule();
                            List<Phonemetadata.NumberFormat> numberFormats = new ArrayList<>(1);
                            numberFormats.add(numFormatCopy.build());
                            regionCode = formatByPattern(number, PhoneNumberFormat.NATIONAL, numberFormats);
                            break;
                        }
                    }
                }
                break;
        }
        String rawInput = number.getRawInput();
        if (regionCode != null && rawInput.length() > 0) {
            String normalizedFormattedNumber = normalizeDiallableCharsOnly(regionCode);
            String normalizedRawInput = normalizeDiallableCharsOnly(rawInput);
            if (!normalizedFormattedNumber.equals(normalizedRawInput)) {
                return rawInput;
            }
            return regionCode;
        }
        return regionCode;
    }

    private boolean rawInputContainsNationalPrefix(String rawInput, String nationalPrefix, String regionCode) {
        String normalizedNationalNumber = normalizeDigitsOnly(rawInput);
        if (normalizedNationalNumber.startsWith(nationalPrefix)) {
            try {
                return isValidNumber(parse(normalizedNationalNumber.substring(nationalPrefix.length()), regionCode));
            } catch (NumberParseException e) {
                return false;
            }
        }
        return false;
    }

    private boolean hasFormattingPatternForNumber(Phonenumber.PhoneNumber number) {
        int countryCallingCode = number.getCountryCode();
        String phoneNumberRegion = getRegionCodeForCountryCode(countryCallingCode);
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegionOrCallingCode(countryCallingCode, phoneNumberRegion);
        if (metadata == null) {
            return false;
        }
        String nationalNumber = getNationalSignificantNumber(number);
        Phonemetadata.NumberFormat formatRule = chooseFormattingPatternForNumber(metadata.getNumberFormatList(), nationalNumber);
        return formatRule != null;
    }

    public String formatOutOfCountryKeepingAlphaChars(Phonenumber.PhoneNumber number, String regionCallingFrom) {
        String preferredInternationalPrefix;
        int firstNationalNumberDigit;
        String rawInput = number.getRawInput();
        if (rawInput.length() == 0) {
            return formatOutOfCountryCallingNumber(number, regionCallingFrom);
        }
        int countryCode = number.getCountryCode();
        if (!hasValidCountryCallingCode(countryCode)) {
            return rawInput;
        }
        String rawInput2 = normalizeHelper(rawInput, ALL_PLUS_NUMBER_GROUPING_SYMBOLS, true);
        String nationalNumber = getNationalSignificantNumber(number);
        if (nationalNumber.length() > MAX_LENGTH_COUNTRY_CODE && (firstNationalNumberDigit = rawInput2.indexOf(nationalNumber.substring(0, MAX_LENGTH_COUNTRY_CODE))) != -1) {
            rawInput2 = rawInput2.substring(firstNationalNumberDigit);
        }
        Phonemetadata.PhoneMetadata metadataForRegionCallingFrom = getMetadataForRegion(regionCallingFrom);
        if (countryCode == 1) {
            if (isNANPACountry(regionCallingFrom)) {
                return countryCode + " " + rawInput2;
            }
        } else if (metadataForRegionCallingFrom != null && countryCode == getCountryCodeForValidRegion(regionCallingFrom)) {
            Phonemetadata.NumberFormat formattingPattern = chooseFormattingPatternForNumber(metadataForRegionCallingFrom.getNumberFormatList(), nationalNumber);
            if (formattingPattern == null) {
                return rawInput2;
            }
            Phonemetadata.NumberFormat.Builder newFormat = Phonemetadata.NumberFormat.newBuilder();
            newFormat.mergeFrom(formattingPattern);
            newFormat.setPattern("(\\d+)(.*)");
            newFormat.setFormat("$1$2");
            return formatNsnUsingPattern(rawInput2, newFormat.build(), PhoneNumberFormat.NATIONAL);
        }
        String internationalPrefixForFormatting = "";
        if (metadataForRegionCallingFrom != null) {
            String internationalPrefix = metadataForRegionCallingFrom.getInternationalPrefix();
            if (SINGLE_INTERNATIONAL_PREFIX.matcher(internationalPrefix).matches()) {
                preferredInternationalPrefix = internationalPrefix;
            } else {
                preferredInternationalPrefix = metadataForRegionCallingFrom.getPreferredInternationalPrefix();
            }
            internationalPrefixForFormatting = preferredInternationalPrefix;
        }
        StringBuilder formattedNumber = new StringBuilder(rawInput2);
        String regionCode = getRegionCodeForCountryCode(countryCode);
        Phonemetadata.PhoneMetadata metadataForRegion = getMetadataForRegionOrCallingCode(countryCode, regionCode);
        maybeAppendFormattedExtension(number, metadataForRegion, PhoneNumberFormat.INTERNATIONAL, formattedNumber);
        if (internationalPrefixForFormatting.length() > 0) {
            formattedNumber.insert(0, " ").insert(0, countryCode).insert(0, " ").insert(0, internationalPrefixForFormatting);
        } else {
            if (!isValidRegionCode(regionCallingFrom)) {
                logger.log(Level.WARNING, "Trying to format number from invalid region " + regionCallingFrom + ". International formatting applied.");
            }
            prefixNumberWithCountryCallingCode(countryCode, PhoneNumberFormat.INTERNATIONAL, formattedNumber);
        }
        return formattedNumber.toString();
    }

    public String getNationalSignificantNumber(Phonenumber.PhoneNumber number) {
        StringBuilder nationalNumber = new StringBuilder();
        if (number.isItalianLeadingZero() && number.getNumberOfLeadingZeros() > 0) {
            char[] zeros = new char[number.getNumberOfLeadingZeros()];
            Arrays.fill(zeros, '0');
            nationalNumber.append(new String(zeros));
        }
        nationalNumber.append(number.getNationalNumber());
        return nationalNumber.toString();
    }

    private void prefixNumberWithCountryCallingCode(int countryCallingCode, PhoneNumberFormat numberFormat, StringBuilder formattedNumber) {
        switch (C00012.f0xfe38ec89[numberFormat.ordinal()]) {
            case 1:
                formattedNumber.insert(0, countryCallingCode).insert(0, PLUS_SIGN);
                return;
            case 2:
                formattedNumber.insert(0, " ").insert(0, countryCallingCode).insert(0, PLUS_SIGN);
                return;
            case MAX_LENGTH_COUNTRY_CODE /* 3 */:
                formattedNumber.insert(0, "-").insert(0, countryCallingCode).insert(0, PLUS_SIGN).insert(0, RFC3966_PREFIX);
                return;
            default:
                return;
        }
    }

    private String formatNsn(String number, Phonemetadata.PhoneMetadata metadata, PhoneNumberFormat numberFormat) {
        return formatNsn(number, metadata, numberFormat, null);
    }

    private String formatNsn(String number, Phonemetadata.PhoneMetadata metadata, PhoneNumberFormat numberFormat, CharSequence carrierCode) {
        List<Phonemetadata.NumberFormat> availableFormats;
        List<Phonemetadata.NumberFormat> intlNumberFormats = metadata.getIntlNumberFormatList();
        if (intlNumberFormats.size() == 0 || numberFormat == PhoneNumberFormat.NATIONAL) {
            availableFormats = metadata.getNumberFormatList();
        } else {
            availableFormats = metadata.getIntlNumberFormatList();
        }
        Phonemetadata.NumberFormat formattingPattern = chooseFormattingPatternForNumber(availableFormats, number);
        if (formattingPattern == null) {
            return number;
        }
        return formatNsnUsingPattern(number, formattingPattern, numberFormat, carrierCode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata.NumberFormat chooseFormattingPatternForNumber(List<Phonemetadata.NumberFormat> availableFormats, String nationalNumber) {
        for (Phonemetadata.NumberFormat numFormat : availableFormats) {
            int size = numFormat.getLeadingDigitsPatternCount();
            if (size == 0 || this.regexCache.getPatternForRegex(numFormat.getLeadingDigitsPattern(size - 1)).matcher(nationalNumber).lookingAt()) {
                Matcher m = this.regexCache.getPatternForRegex(numFormat.getPattern()).matcher(nationalNumber);
                if (m.matches()) {
                    return numFormat;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String formatNsnUsingPattern(String nationalNumber, Phonemetadata.NumberFormat formattingPattern, PhoneNumberFormat numberFormat) {
        return formatNsnUsingPattern(nationalNumber, formattingPattern, numberFormat, null);
    }

    private String formatNsnUsingPattern(String nationalNumber, Phonemetadata.NumberFormat formattingPattern, PhoneNumberFormat numberFormat, CharSequence carrierCode) {
        String formattedNationalNumber;
        String numberFormatRule = formattingPattern.getFormat();
        Matcher m = this.regexCache.getPatternForRegex(formattingPattern.getPattern()).matcher(nationalNumber);
        if (numberFormat == PhoneNumberFormat.NATIONAL && carrierCode != null && carrierCode.length() > 0 && formattingPattern.getDomesticCarrierCodeFormattingRule().length() > 0) {
            String carrierCodeFormattingRule = formattingPattern.getDomesticCarrierCodeFormattingRule();
            formattedNationalNumber = m.replaceAll(FIRST_GROUP_PATTERN.matcher(numberFormatRule).replaceFirst(carrierCodeFormattingRule.replace(CC_STRING, carrierCode)));
        } else {
            String nationalPrefixFormattingRule = formattingPattern.getNationalPrefixFormattingRule();
            if (numberFormat == PhoneNumberFormat.NATIONAL && nationalPrefixFormattingRule != null && nationalPrefixFormattingRule.length() > 0) {
                Matcher firstGroupMatcher = FIRST_GROUP_PATTERN.matcher(numberFormatRule);
                formattedNationalNumber = m.replaceAll(firstGroupMatcher.replaceFirst(nationalPrefixFormattingRule));
            } else {
                formattedNationalNumber = m.replaceAll(numberFormatRule);
            }
        }
        if (numberFormat == PhoneNumberFormat.RFC3966) {
            Matcher matcher = SEPARATOR_PATTERN.matcher(formattedNationalNumber);
            if (matcher.lookingAt()) {
                formattedNationalNumber = matcher.replaceFirst("");
            }
            return matcher.reset(formattedNationalNumber).replaceAll("-");
        }
        return formattedNationalNumber;
    }

    public Phonenumber.PhoneNumber getExampleNumber(String regionCode) {
        return getExampleNumberForType(regionCode, PhoneNumberType.FIXED_LINE);
    }

    public Phonenumber.PhoneNumber getInvalidExampleNumber(String regionCode) {
        Phonenumber.PhoneNumber possiblyValidNumber;
        if (!isValidRegionCode(regionCode)) {
            logger.log(Level.WARNING, "Invalid or unknown region code provided: " + regionCode);
            return null;
        }
        Phonemetadata.PhoneNumberDesc desc = getNumberDescByType(getMetadataForRegion(regionCode), PhoneNumberType.FIXED_LINE);
        if (!desc.hasExampleNumber()) {
            return null;
        }
        String exampleNumber = desc.getExampleNumber();
        int phoneNumberLength = exampleNumber.length();
        while (true) {
            phoneNumberLength--;
            if (phoneNumberLength < 2) {
                return null;
            }
            String numberToTry = exampleNumber.substring(0, phoneNumberLength);
            try {
                possiblyValidNumber = parse(numberToTry, regionCode);
            } catch (NumberParseException e) {
            }
            if (!isValidNumber(possiblyValidNumber)) {
                return possiblyValidNumber;
            }
        }
    }

    public Phonenumber.PhoneNumber getExampleNumberForType(String regionCode, PhoneNumberType type) {
        if (!isValidRegionCode(regionCode)) {
            logger.log(Level.WARNING, "Invalid or unknown region code provided: " + regionCode);
            return null;
        }
        Phonemetadata.PhoneNumberDesc desc = getNumberDescByType(getMetadataForRegion(regionCode), type);
        try {
            if (desc.hasExampleNumber()) {
                return parse(desc.getExampleNumber(), regionCode);
            }
        } catch (NumberParseException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return null;
    }

    public Phonenumber.PhoneNumber getExampleNumberForType(PhoneNumberType type) {
        for (String regionCode : getSupportedRegions()) {
            Phonenumber.PhoneNumber exampleNumber = getExampleNumberForType(regionCode, type);
            if (exampleNumber != null) {
                return exampleNumber;
            }
        }
        Iterator<Integer> it = getSupportedGlobalNetworkCallingCodes().iterator();
        while (it.hasNext()) {
            int countryCallingCode = it.next().intValue();
            Phonemetadata.PhoneNumberDesc desc = getNumberDescByType(getMetadataForNonGeographicalRegion(countryCallingCode), type);
            try {
            } catch (NumberParseException e) {
                logger.log(Level.SEVERE, e.toString());
            }
            if (desc.hasExampleNumber()) {
                return parse("+" + countryCallingCode + desc.getExampleNumber(), UNKNOWN_REGION);
            }
            continue;
        }
        return null;
    }

    public Phonenumber.PhoneNumber getExampleNumberForNonGeoEntity(int countryCallingCode) {
        Phonemetadata.PhoneMetadata metadata = getMetadataForNonGeographicalRegion(countryCallingCode);
        if (metadata != null) {
            Iterator it = Arrays.asList(metadata.getMobile(), metadata.getTollFree(), metadata.getSharedCost(), metadata.getVoip(), metadata.getVoicemail(), metadata.getUan(), metadata.getPremiumRate()).iterator();
            while (it.hasNext()) {
                Phonemetadata.PhoneNumberDesc desc = (Phonemetadata.PhoneNumberDesc) it.next();
                if (desc != null) {
                    try {
                        if (desc.hasExampleNumber()) {
                            return parse("+" + countryCallingCode + desc.getExampleNumber(), UNKNOWN_REGION);
                        }
                        continue;
                    } catch (NumberParseException e) {
                        logger.log(Level.SEVERE, e.toString());
                    }
                }
            }
            return null;
        }
        logger.log(Level.WARNING, "Invalid or unknown country calling code provided: " + countryCallingCode);
        return null;
    }

    private void maybeAppendFormattedExtension(Phonenumber.PhoneNumber number, Phonemetadata.PhoneMetadata metadata, PhoneNumberFormat numberFormat, StringBuilder formattedNumber) {
        if (number.hasExtension() && number.getExtension().length() > 0) {
            if (numberFormat == PhoneNumberFormat.RFC3966) {
                formattedNumber.append(RFC3966_EXTN_PREFIX).append(number.getExtension());
            } else if (metadata.hasPreferredExtnPrefix()) {
                formattedNumber.append(metadata.getPreferredExtnPrefix()).append(number.getExtension());
            } else {
                formattedNumber.append(DEFAULT_EXTN_PREFIX).append(number.getExtension());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.i18n.phonenumbers.PhoneNumberUtil$2 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00012 {

        /* renamed from: $SwitchMap$com$android$i18n$phonenumbers$PhoneNumberUtil$PhoneNumberFormat */
        static final /* synthetic */ int[] f0xfe38ec89;

        /* renamed from: $SwitchMap$com$android$i18n$phonenumbers$PhoneNumberUtil$PhoneNumberType */
        static final /* synthetic */ int[] f1x9a87804c;

        /* renamed from: $SwitchMap$com$android$i18n$phonenumbers$Phonenumber$PhoneNumber$CountryCodeSource */
        static final /* synthetic */ int[] f2xc1c7dc0a;

        static {
            int[] iArr = new int[PhoneNumberType.values().length];
            f1x9a87804c = iArr;
            try {
                iArr[PhoneNumberType.PREMIUM_RATE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f1x9a87804c[PhoneNumberType.TOLL_FREE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f1x9a87804c[PhoneNumberType.MOBILE.ordinal()] = PhoneNumberUtil.MAX_LENGTH_COUNTRY_CODE;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f1x9a87804c[PhoneNumberType.FIXED_LINE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f1x9a87804c[PhoneNumberType.FIXED_LINE_OR_MOBILE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f1x9a87804c[PhoneNumberType.SHARED_COST.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f1x9a87804c[PhoneNumberType.VOIP.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f1x9a87804c[PhoneNumberType.PERSONAL_NUMBER.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f1x9a87804c[PhoneNumberType.PAGER.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f1x9a87804c[PhoneNumberType.UAN.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                f1x9a87804c[PhoneNumberType.VOICEMAIL.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            int[] iArr2 = new int[PhoneNumberFormat.values().length];
            f0xfe38ec89 = iArr2;
            try {
                iArr2[PhoneNumberFormat.E164.ordinal()] = 1;
            } catch (NoSuchFieldError e12) {
            }
            try {
                f0xfe38ec89[PhoneNumberFormat.INTERNATIONAL.ordinal()] = 2;
            } catch (NoSuchFieldError e13) {
            }
            try {
                f0xfe38ec89[PhoneNumberFormat.RFC3966.ordinal()] = PhoneNumberUtil.MAX_LENGTH_COUNTRY_CODE;
            } catch (NoSuchFieldError e14) {
            }
            try {
                f0xfe38ec89[PhoneNumberFormat.NATIONAL.ordinal()] = 4;
            } catch (NoSuchFieldError e15) {
            }
            int[] iArr3 = new int[Phonenumber.PhoneNumber.CountryCodeSource.values().length];
            f2xc1c7dc0a = iArr3;
            try {
                iArr3[Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN.ordinal()] = 1;
            } catch (NoSuchFieldError e16) {
            }
            try {
                f2xc1c7dc0a[Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_IDD.ordinal()] = 2;
            } catch (NoSuchFieldError e17) {
            }
            try {
                f2xc1c7dc0a[Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN.ordinal()] = PhoneNumberUtil.MAX_LENGTH_COUNTRY_CODE;
            } catch (NoSuchFieldError e18) {
            }
            try {
                f2xc1c7dc0a[Phonenumber.PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY.ordinal()] = 4;
            } catch (NoSuchFieldError e19) {
            }
        }
    }

    Phonemetadata.PhoneNumberDesc getNumberDescByType(Phonemetadata.PhoneMetadata metadata, PhoneNumberType type) {
        switch (C00012.f1x9a87804c[type.ordinal()]) {
            case 1:
                return metadata.getPremiumRate();
            case 2:
                return metadata.getTollFree();
            case MAX_LENGTH_COUNTRY_CODE /* 3 */:
                return metadata.getMobile();
            case 4:
            case 5:
                return metadata.getFixedLine();
            case 6:
                return metadata.getSharedCost();
            case 7:
                return metadata.getVoip();
            case 8:
                return metadata.getPersonalNumber();
            case 9:
                return metadata.getPager();
            case 10:
                return metadata.getUan();
            case 11:
                return metadata.getVoicemail();
            default:
                return metadata.getGeneralDesc();
        }
    }

    public PhoneNumberType getNumberType(Phonenumber.PhoneNumber number) {
        String regionCode = getRegionCodeForNumber(number);
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegionOrCallingCode(number.getCountryCode(), regionCode);
        if (metadata == null) {
            return PhoneNumberType.UNKNOWN;
        }
        String nationalSignificantNumber = getNationalSignificantNumber(number);
        return getNumberTypeHelper(nationalSignificantNumber, metadata);
    }

    private PhoneNumberType getNumberTypeHelper(String nationalNumber, Phonemetadata.PhoneMetadata metadata) {
        if (!isNumberMatchingDesc(nationalNumber, metadata.getGeneralDesc())) {
            return PhoneNumberType.UNKNOWN;
        }
        if (isNumberMatchingDesc(nationalNumber, metadata.getPremiumRate())) {
            return PhoneNumberType.PREMIUM_RATE;
        }
        if (isNumberMatchingDesc(nationalNumber, metadata.getTollFree())) {
            return PhoneNumberType.TOLL_FREE;
        }
        if (isNumberMatchingDesc(nationalNumber, metadata.getSharedCost())) {
            return PhoneNumberType.SHARED_COST;
        }
        if (isNumberMatchingDesc(nationalNumber, metadata.getVoip())) {
            return PhoneNumberType.VOIP;
        }
        if (isNumberMatchingDesc(nationalNumber, metadata.getPersonalNumber())) {
            return PhoneNumberType.PERSONAL_NUMBER;
        }
        if (isNumberMatchingDesc(nationalNumber, metadata.getPager())) {
            return PhoneNumberType.PAGER;
        }
        if (isNumberMatchingDesc(nationalNumber, metadata.getUan())) {
            return PhoneNumberType.UAN;
        }
        if (isNumberMatchingDesc(nationalNumber, metadata.getVoicemail())) {
            return PhoneNumberType.VOICEMAIL;
        }
        boolean isFixedLine = isNumberMatchingDesc(nationalNumber, metadata.getFixedLine());
        if (isFixedLine) {
            if (metadata.getSameMobileAndFixedLinePattern()) {
                return PhoneNumberType.FIXED_LINE_OR_MOBILE;
            }
            if (isNumberMatchingDesc(nationalNumber, metadata.getMobile())) {
                return PhoneNumberType.FIXED_LINE_OR_MOBILE;
            }
            return PhoneNumberType.FIXED_LINE;
        } else if (!metadata.getSameMobileAndFixedLinePattern() && isNumberMatchingDesc(nationalNumber, metadata.getMobile())) {
            return PhoneNumberType.MOBILE;
        } else {
            return PhoneNumberType.UNKNOWN;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata.PhoneMetadata getMetadataForRegion(String regionCode) {
        if (!isValidRegionCode(regionCode)) {
            return null;
        }
        Phonemetadata.PhoneMetadata phoneMetadata = this.metadataSource.getMetadataForRegion(regionCode);
        ensureMetadataIsNonNull(phoneMetadata, "Missing metadata for region code " + regionCode);
        return phoneMetadata;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata.PhoneMetadata getMetadataForNonGeographicalRegion(int countryCallingCode) {
        if (!this.countryCodesForNonGeographicalRegion.contains(Integer.valueOf(countryCallingCode))) {
            return null;
        }
        Phonemetadata.PhoneMetadata phoneMetadata = this.metadataSource.getMetadataForNonGeographicalRegion(countryCallingCode);
        ensureMetadataIsNonNull(phoneMetadata, "Missing metadata for country code " + countryCallingCode);
        return phoneMetadata;
    }

    private static void ensureMetadataIsNonNull(Phonemetadata.PhoneMetadata phoneMetadata, String message) {
        if (phoneMetadata == null) {
            throw new MissingMetadataException(message);
        }
    }

    boolean isNumberMatchingDesc(String nationalNumber, Phonemetadata.PhoneNumberDesc numberDesc) {
        int actualLength = nationalNumber.length();
        List<Integer> possibleLengths = numberDesc.getPossibleLengthList();
        if (possibleLengths.size() <= 0 || possibleLengths.contains(Integer.valueOf(actualLength))) {
            return this.matcherApi.matchNationalNumber(nationalNumber, numberDesc, false);
        }
        return false;
    }

    public boolean isValidNumber(Phonenumber.PhoneNumber number) {
        String regionCode = getRegionCodeForNumber(number);
        return isValidNumberForRegion(number, regionCode);
    }

    public boolean isValidNumberForRegion(Phonenumber.PhoneNumber number, String regionCode) {
        int countryCode = number.getCountryCode();
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegionOrCallingCode(countryCode, regionCode);
        if (metadata == null || (!"001".equals(regionCode) && countryCode != getCountryCodeForValidRegion(regionCode))) {
            return false;
        }
        String nationalSignificantNumber = getNationalSignificantNumber(number);
        return getNumberTypeHelper(nationalSignificantNumber, metadata) != PhoneNumberType.UNKNOWN;
    }

    public String getRegionCodeForNumber(Phonenumber.PhoneNumber number) {
        int countryCode = number.getCountryCode();
        List<String> regions = this.countryCallingCodeToRegionCodeMap.get(Integer.valueOf(countryCode));
        if (regions == null) {
            logger.log(Level.INFO, "Missing/invalid country_code (" + countryCode + ")");
            return null;
        } else if (regions.size() == 1) {
            return regions.get(0);
        } else {
            return getRegionCodeForNumberFromRegionList(number, regions);
        }
    }

    private String getRegionCodeForNumberFromRegionList(Phonenumber.PhoneNumber number, List<String> regionCodes) {
        String nationalNumber = getNationalSignificantNumber(number);
        for (String regionCode : regionCodes) {
            Phonemetadata.PhoneMetadata metadata = getMetadataForRegion(regionCode);
            if (metadata.hasLeadingDigits()) {
                if (this.regexCache.getPatternForRegex(metadata.getLeadingDigits()).matcher(nationalNumber).lookingAt()) {
                    return regionCode;
                }
            } else if (getNumberTypeHelper(nationalNumber, metadata) != PhoneNumberType.UNKNOWN) {
                return regionCode;
            }
        }
        return null;
    }

    public String getRegionCodeForCountryCode(int countryCallingCode) {
        List<String> regionCodes = this.countryCallingCodeToRegionCodeMap.get(Integer.valueOf(countryCallingCode));
        return regionCodes == null ? UNKNOWN_REGION : regionCodes.get(0);
    }

    public List<String> getRegionCodesForCountryCode(int countryCallingCode) {
        List<String> regionCodes = this.countryCallingCodeToRegionCodeMap.get(Integer.valueOf(countryCallingCode));
        return Collections.unmodifiableList(regionCodes == null ? new ArrayList<>(0) : regionCodes);
    }

    public int getCountryCodeForRegion(String regionCode) {
        if (!isValidRegionCode(regionCode)) {
            logger.log(Level.WARNING, "Invalid or missing region code (" + (regionCode == null ? "null" : regionCode) + ") provided.");
            return 0;
        }
        return getCountryCodeForValidRegion(regionCode);
    }

    private int getCountryCodeForValidRegion(String regionCode) {
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegion(regionCode);
        if (metadata == null) {
            throw new IllegalArgumentException("Invalid region code: " + regionCode);
        }
        return metadata.getCountryCode();
    }

    public String getNddPrefixForRegion(String regionCode, boolean stripNonDigits) {
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegion(regionCode);
        if (metadata == null) {
            logger.log(Level.WARNING, "Invalid or missing region code (" + (regionCode == null ? "null" : regionCode) + ") provided.");
            return null;
        }
        String nationalPrefix = metadata.getNationalPrefix();
        if (nationalPrefix.length() == 0) {
            return null;
        }
        if (stripNonDigits) {
            return nationalPrefix.replace("~", "");
        }
        return nationalPrefix;
    }

    public boolean isNANPACountry(String regionCode) {
        return this.nanpaRegions.contains(regionCode);
    }

    public boolean isAlphaNumber(CharSequence number) {
        if (!isViablePhoneNumber(number)) {
            return false;
        }
        StringBuilder strippedNumber = new StringBuilder(number);
        maybeStripExtension(strippedNumber);
        return VALID_ALPHA_PHONE_PATTERN.matcher(strippedNumber).matches();
    }

    public boolean isPossibleNumber(Phonenumber.PhoneNumber number) {
        ValidationResult result = isPossibleNumberWithReason(number);
        return result == ValidationResult.IS_POSSIBLE || result == ValidationResult.IS_POSSIBLE_LOCAL_ONLY;
    }

    public boolean isPossibleNumberForType(Phonenumber.PhoneNumber number, PhoneNumberType type) {
        ValidationResult result = isPossibleNumberForTypeWithReason(number, type);
        return result == ValidationResult.IS_POSSIBLE || result == ValidationResult.IS_POSSIBLE_LOCAL_ONLY;
    }

    private ValidationResult testNumberLength(CharSequence number, Phonemetadata.PhoneMetadata metadata) {
        return testNumberLength(number, metadata, PhoneNumberType.UNKNOWN);
    }

    private ValidationResult testNumberLength(CharSequence number, Phonemetadata.PhoneMetadata metadata, PhoneNumberType type) {
        Collection<? extends Integer> possibleLengthList;
        Phonemetadata.PhoneNumberDesc descForType = getNumberDescByType(metadata, type);
        List<Integer> possibleLengths = descForType.getPossibleLengthList().isEmpty() ? metadata.getGeneralDesc().getPossibleLengthList() : descForType.getPossibleLengthList();
        List<Integer> localLengths = descForType.getPossibleLengthLocalOnlyList();
        if (type == PhoneNumberType.FIXED_LINE_OR_MOBILE) {
            if (!descHasPossibleNumberData(getNumberDescByType(metadata, PhoneNumberType.FIXED_LINE))) {
                return testNumberLength(number, metadata, PhoneNumberType.MOBILE);
            }
            Phonemetadata.PhoneNumberDesc mobileDesc = getNumberDescByType(metadata, PhoneNumberType.MOBILE);
            if (descHasPossibleNumberData(mobileDesc)) {
                possibleLengths = new ArrayList<>(possibleLengths);
                if (mobileDesc.getPossibleLengthCount() == 0) {
                    possibleLengthList = metadata.getGeneralDesc().getPossibleLengthList();
                } else {
                    possibleLengthList = mobileDesc.getPossibleLengthList();
                }
                possibleLengths.addAll(possibleLengthList);
                Collections.sort(possibleLengths);
                if (localLengths.isEmpty()) {
                    localLengths = mobileDesc.getPossibleLengthLocalOnlyList();
                } else {
                    localLengths = new ArrayList(localLengths);
                    localLengths.addAll(mobileDesc.getPossibleLengthLocalOnlyList());
                    Collections.sort(localLengths);
                }
            }
        }
        if (possibleLengths.get(0).intValue() == -1) {
            return ValidationResult.INVALID_LENGTH;
        }
        int actualLength = number.length();
        if (localLengths.contains(Integer.valueOf(actualLength))) {
            return ValidationResult.IS_POSSIBLE_LOCAL_ONLY;
        }
        int minimumLength = possibleLengths.get(0).intValue();
        if (minimumLength == actualLength) {
            return ValidationResult.IS_POSSIBLE;
        }
        if (minimumLength > actualLength) {
            return ValidationResult.TOO_SHORT;
        }
        if (possibleLengths.get(possibleLengths.size() - 1).intValue() < actualLength) {
            return ValidationResult.TOO_LONG;
        }
        return possibleLengths.subList(1, possibleLengths.size()).contains(Integer.valueOf(actualLength)) ? ValidationResult.IS_POSSIBLE : ValidationResult.INVALID_LENGTH;
    }

    public ValidationResult isPossibleNumberWithReason(Phonenumber.PhoneNumber number) {
        return isPossibleNumberForTypeWithReason(number, PhoneNumberType.UNKNOWN);
    }

    public ValidationResult isPossibleNumberForTypeWithReason(Phonenumber.PhoneNumber number, PhoneNumberType type) {
        String nationalNumber = getNationalSignificantNumber(number);
        int countryCode = number.getCountryCode();
        if (!hasValidCountryCallingCode(countryCode)) {
            return ValidationResult.INVALID_COUNTRY_CODE;
        }
        String regionCode = getRegionCodeForCountryCode(countryCode);
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegionOrCallingCode(countryCode, regionCode);
        return testNumberLength(nationalNumber, metadata, type);
    }

    public boolean isPossibleNumber(CharSequence number, String regionDialingFrom) {
        try {
            return isPossibleNumber(parse(number, regionDialingFrom));
        } catch (NumberParseException e) {
            return false;
        }
    }

    public boolean truncateTooLongNumber(Phonenumber.PhoneNumber number) {
        if (isValidNumber(number)) {
            return true;
        }
        Phonenumber.PhoneNumber numberCopy = new Phonenumber.PhoneNumber();
        numberCopy.mergeFrom(number);
        long nationalNumber = number.getNationalNumber();
        do {
            nationalNumber /= 10;
            numberCopy.setNationalNumber(nationalNumber);
            if (isPossibleNumberWithReason(numberCopy) == ValidationResult.TOO_SHORT || nationalNumber == 0) {
                return false;
            }
        } while (!isValidNumber(numberCopy));
        number.setNationalNumber(nationalNumber);
        return true;
    }

    public AsYouTypeFormatter getAsYouTypeFormatter(String regionCode) {
        return new AsYouTypeFormatter(regionCode);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int extractCountryCode(StringBuilder fullNumber, StringBuilder nationalNumber) {
        if (fullNumber.length() == 0 || fullNumber.charAt(0) == '0') {
            return 0;
        }
        int numberLength = fullNumber.length();
        for (int i = 1; i <= MAX_LENGTH_COUNTRY_CODE && i <= numberLength; i++) {
            int potentialCountryCode = Integer.parseInt(fullNumber.substring(0, i));
            if (this.countryCallingCodeToRegionCodeMap.containsKey(Integer.valueOf(potentialCountryCode))) {
                nationalNumber.append(fullNumber.substring(i));
                return potentialCountryCode;
            }
        }
        return 0;
    }

    int maybeExtractCountryCode(CharSequence number, Phonemetadata.PhoneMetadata defaultRegionMetadata, StringBuilder nationalNumber, boolean keepRawInput, Phonenumber.PhoneNumber phoneNumber) throws NumberParseException {
        if (number.length() == 0) {
            return 0;
        }
        StringBuilder fullNumber = new StringBuilder(number);
        String possibleCountryIddPrefix = "NonMatch";
        if (defaultRegionMetadata != null) {
            possibleCountryIddPrefix = defaultRegionMetadata.getInternationalPrefix();
        }
        Phonenumber.PhoneNumber.CountryCodeSource countryCodeSource = maybeStripInternationalPrefixAndNormalize(fullNumber, possibleCountryIddPrefix);
        if (keepRawInput) {
            phoneNumber.setCountryCodeSource(countryCodeSource);
        }
        if (countryCodeSource != Phonenumber.PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY) {
            if (fullNumber.length() <= 2) {
                throw new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_AFTER_IDD, "Phone number had an IDD, but after this was not long enough to be a viable phone number.");
            }
            int potentialCountryCode = extractCountryCode(fullNumber, nationalNumber);
            if (potentialCountryCode != 0) {
                phoneNumber.setCountryCode(potentialCountryCode);
                return potentialCountryCode;
            }
            throw new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE, "Country calling code supplied was not recognised.");
        }
        if (defaultRegionMetadata != null) {
            int defaultCountryCode = defaultRegionMetadata.getCountryCode();
            String defaultCountryCodeString = String.valueOf(defaultCountryCode);
            String normalizedNumber = fullNumber.toString();
            if (normalizedNumber.startsWith(defaultCountryCodeString)) {
                StringBuilder potentialNationalNumber = new StringBuilder(normalizedNumber.substring(defaultCountryCodeString.length()));
                Phonemetadata.PhoneNumberDesc generalDesc = defaultRegionMetadata.getGeneralDesc();
                maybeStripNationalPrefixAndCarrierCode(potentialNationalNumber, defaultRegionMetadata, null);
                if ((!this.matcherApi.matchNationalNumber(fullNumber, generalDesc, false) && this.matcherApi.matchNationalNumber(potentialNationalNumber, generalDesc, false)) || testNumberLength(fullNumber, defaultRegionMetadata) == ValidationResult.TOO_LONG) {
                    nationalNumber.append((CharSequence) potentialNationalNumber);
                    if (keepRawInput) {
                        phoneNumber.setCountryCodeSource(Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN);
                    }
                    phoneNumber.setCountryCode(defaultCountryCode);
                    return defaultCountryCode;
                }
            }
        }
        phoneNumber.setCountryCode(0);
        return 0;
    }

    private boolean parsePrefixAsIdd(Pattern iddPattern, StringBuilder number) {
        Matcher m = iddPattern.matcher(number);
        if (m.lookingAt()) {
            int matchEnd = m.end();
            Matcher digitMatcher = CAPTURING_DIGIT_PATTERN.matcher(number.substring(matchEnd));
            if (digitMatcher.find()) {
                String normalizedGroup = normalizeDigitsOnly(digitMatcher.group(1));
                if (normalizedGroup.equals("0")) {
                    return false;
                }
            }
            number.delete(0, matchEnd);
            return true;
        }
        return false;
    }

    Phonenumber.PhoneNumber.CountryCodeSource maybeStripInternationalPrefixAndNormalize(StringBuilder number, String possibleIddPrefix) {
        if (number.length() == 0) {
            return Phonenumber.PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY;
        }
        Matcher m = PLUS_CHARS_PATTERN.matcher(number);
        if (m.lookingAt()) {
            number.delete(0, m.end());
            normalize(number);
            return Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN;
        }
        Pattern iddPattern = this.regexCache.getPatternForRegex(possibleIddPrefix);
        normalize(number);
        if (parsePrefixAsIdd(iddPattern, number)) {
            return Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_IDD;
        }
        return Phonenumber.PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean maybeStripNationalPrefixAndCarrierCode(StringBuilder number, Phonemetadata.PhoneMetadata metadata, StringBuilder carrierCode) {
        int numberLength = number.length();
        String possibleNationalPrefix = metadata.getNationalPrefixForParsing();
        if (numberLength == 0 || possibleNationalPrefix.length() == 0) {
            return false;
        }
        Matcher prefixMatcher = this.regexCache.getPatternForRegex(possibleNationalPrefix).matcher(number);
        if (!prefixMatcher.lookingAt()) {
            return false;
        }
        Phonemetadata.PhoneNumberDesc generalDesc = metadata.getGeneralDesc();
        boolean isViableOriginalNumber = this.matcherApi.matchNationalNumber(number, generalDesc, false);
        int numOfGroups = prefixMatcher.groupCount();
        String transformRule = metadata.getNationalPrefixTransformRule();
        if (transformRule == null || transformRule.length() == 0 || prefixMatcher.group(numOfGroups) == null) {
            if (isViableOriginalNumber && !this.matcherApi.matchNationalNumber(number.substring(prefixMatcher.end()), generalDesc, false)) {
                return false;
            }
            if (carrierCode != null && numOfGroups > 0 && prefixMatcher.group(numOfGroups) != null) {
                carrierCode.append(prefixMatcher.group(1));
            }
            number.delete(0, prefixMatcher.end());
            return true;
        }
        StringBuilder transformedNumber = new StringBuilder(number);
        transformedNumber.replace(0, numberLength, prefixMatcher.replaceFirst(transformRule));
        if (isViableOriginalNumber && !this.matcherApi.matchNationalNumber(transformedNumber.toString(), generalDesc, false)) {
            return false;
        }
        if (carrierCode != null && numOfGroups > 1) {
            carrierCode.append(prefixMatcher.group(1));
        }
        number.replace(0, number.length(), transformedNumber.toString());
        return true;
    }

    String maybeStripExtension(StringBuilder number) {
        Matcher m = EXTN_PATTERN.matcher(number);
        if (m.find() && isViablePhoneNumber(number.substring(0, m.start()))) {
            int length = m.groupCount();
            for (int i = 1; i <= length; i++) {
                if (m.group(i) != null) {
                    String extension = m.group(i);
                    number.delete(m.start(), number.length());
                    return extension;
                }
            }
            return "";
        }
        return "";
    }

    private boolean checkRegionForParsing(CharSequence numberToParse, String defaultRegion) {
        if (!isValidRegionCode(defaultRegion)) {
            if (numberToParse == null || numberToParse.length() == 0 || !PLUS_CHARS_PATTERN.matcher(numberToParse).lookingAt()) {
                return false;
            }
            return true;
        }
        return true;
    }

    public Phonenumber.PhoneNumber parse(CharSequence numberToParse, String defaultRegion) throws NumberParseException {
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        parse(numberToParse, defaultRegion, phoneNumber);
        return phoneNumber;
    }

    public void parse(CharSequence numberToParse, String defaultRegion, Phonenumber.PhoneNumber phoneNumber) throws NumberParseException {
        parseHelper(numberToParse, defaultRegion, false, true, phoneNumber);
    }

    public Phonenumber.PhoneNumber parseAndKeepRawInput(CharSequence numberToParse, String defaultRegion) throws NumberParseException {
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        parseAndKeepRawInput(numberToParse, defaultRegion, phoneNumber);
        return phoneNumber;
    }

    public void parseAndKeepRawInput(CharSequence numberToParse, String defaultRegion, Phonenumber.PhoneNumber phoneNumber) throws NumberParseException {
        parseHelper(numberToParse, defaultRegion, true, true, phoneNumber);
    }

    public Iterable<PhoneNumberMatch> findNumbers(CharSequence text, String defaultRegion) {
        return findNumbers(text, defaultRegion, Leniency.VALID, Long.MAX_VALUE);
    }

    public Iterable<PhoneNumberMatch> findNumbers(final CharSequence text, final String defaultRegion, final Leniency leniency, final long maxTries) {
        return new Iterable<PhoneNumberMatch>() { // from class: com.android.i18n.phonenumbers.PhoneNumberUtil.1
            @Override // java.lang.Iterable
            public Iterator<PhoneNumberMatch> iterator() {
                return new PhoneNumberMatcher(PhoneNumberUtil.this, text, defaultRegion, leniency, maxTries);
            }
        };
    }

    static void setItalianLeadingZerosForPhoneNumber(CharSequence nationalNumber, Phonenumber.PhoneNumber phoneNumber) {
        if (nationalNumber.length() > 1 && nationalNumber.charAt(0) == '0') {
            phoneNumber.setItalianLeadingZero(true);
            int numberOfLeadingZeros = 1;
            while (numberOfLeadingZeros < nationalNumber.length() - 1 && nationalNumber.charAt(numberOfLeadingZeros) == '0') {
                numberOfLeadingZeros++;
            }
            if (numberOfLeadingZeros != 1) {
                phoneNumber.setNumberOfLeadingZeros(numberOfLeadingZeros);
            }
        }
    }

    private void parseHelper(CharSequence numberToParse, String defaultRegion, boolean keepRawInput, boolean checkRegion, Phonenumber.PhoneNumber phoneNumber) throws NumberParseException {
        int countryCode;
        if (numberToParse == null) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "The phone number supplied was null.");
        }
        if (numberToParse.length() > MAX_INPUT_STRING_LENGTH) {
            throw new NumberParseException(NumberParseException.ErrorType.TOO_LONG, "The string supplied was too long to parse.");
        }
        StringBuilder nationalNumber = new StringBuilder();
        String numberBeingParsed = numberToParse.toString();
        buildNationalNumberForParsing(numberBeingParsed, nationalNumber);
        if (!isViablePhoneNumber(nationalNumber)) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "The string supplied did not seem to be a phone number.");
        }
        if (checkRegion && !checkRegionForParsing(nationalNumber, defaultRegion)) {
            throw new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE, "Missing or invalid default region.");
        }
        if (keepRawInput) {
            phoneNumber.setRawInput(numberBeingParsed);
        }
        String extension = maybeStripExtension(nationalNumber);
        if (extension.length() > 0) {
            phoneNumber.setExtension(extension);
        }
        Phonemetadata.PhoneMetadata regionMetadata = getMetadataForRegion(defaultRegion);
        StringBuilder normalizedNationalNumber = new StringBuilder();
        try {
            countryCode = maybeExtractCountryCode(nationalNumber, regionMetadata, normalizedNationalNumber, keepRawInput, phoneNumber);
        } catch (NumberParseException e) {
            Matcher matcher = PLUS_CHARS_PATTERN.matcher(nationalNumber);
            if (e.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE && matcher.lookingAt()) {
                int countryCode2 = maybeExtractCountryCode(nationalNumber.substring(matcher.end()), regionMetadata, normalizedNationalNumber, keepRawInput, phoneNumber);
                if (countryCode2 == 0) {
                    throw new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE, "Could not interpret numbers after plus-sign.");
                }
                countryCode = countryCode2;
            } else {
                throw new NumberParseException(e.getErrorType(), e.getMessage());
            }
        }
        if (countryCode != 0) {
            String phoneNumberRegion = getRegionCodeForCountryCode(countryCode);
            if (!phoneNumberRegion.equals(defaultRegion)) {
                regionMetadata = getMetadataForRegionOrCallingCode(countryCode, phoneNumberRegion);
            }
        } else {
            normalizedNationalNumber.append(normalize(nationalNumber));
            if (defaultRegion != null) {
                phoneNumber.setCountryCode(regionMetadata.getCountryCode());
            } else if (keepRawInput) {
                phoneNumber.clearCountryCodeSource();
            }
        }
        if (normalizedNationalNumber.length() < 2) {
            throw new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_NSN, "The string supplied is too short to be a phone number.");
        }
        if (regionMetadata != null) {
            StringBuilder carrierCode = new StringBuilder();
            StringBuilder potentialNationalNumber = new StringBuilder(normalizedNationalNumber);
            maybeStripNationalPrefixAndCarrierCode(potentialNationalNumber, regionMetadata, carrierCode);
            ValidationResult validationResult = testNumberLength(potentialNationalNumber, regionMetadata);
            if (validationResult != ValidationResult.TOO_SHORT && validationResult != ValidationResult.IS_POSSIBLE_LOCAL_ONLY && validationResult != ValidationResult.INVALID_LENGTH) {
                normalizedNationalNumber = potentialNationalNumber;
                if (keepRawInput && carrierCode.length() > 0) {
                    phoneNumber.setPreferredDomesticCarrierCode(carrierCode.toString());
                }
            }
        }
        int lengthOfNationalNumber = normalizedNationalNumber.length();
        if (lengthOfNationalNumber < 2) {
            throw new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_NSN, "The string supplied is too short to be a phone number.");
        }
        if (lengthOfNationalNumber > MAX_LENGTH_FOR_NSN) {
            throw new NumberParseException(NumberParseException.ErrorType.TOO_LONG, "The string supplied is too long to be a phone number.");
        }
        setItalianLeadingZerosForPhoneNumber(normalizedNationalNumber, phoneNumber);
        phoneNumber.setNationalNumber(Long.parseLong(normalizedNationalNumber.toString()));
    }

    private void buildNationalNumberForParsing(String numberToParse, StringBuilder nationalNumber) {
        int indexOfPhoneContext = numberToParse.indexOf(RFC3966_PHONE_CONTEXT);
        if (indexOfPhoneContext >= 0) {
            int phoneContextStart = RFC3966_PHONE_CONTEXT.length() + indexOfPhoneContext;
            if (phoneContextStart < numberToParse.length() - 1 && numberToParse.charAt(phoneContextStart) == '+') {
                int phoneContextEnd = numberToParse.indexOf(59, phoneContextStart);
                if (phoneContextEnd > 0) {
                    nationalNumber.append(numberToParse.substring(phoneContextStart, phoneContextEnd));
                } else {
                    nationalNumber.append(numberToParse.substring(phoneContextStart));
                }
            }
            int indexOfRfc3966Prefix = numberToParse.indexOf(RFC3966_PREFIX);
            int indexOfNationalNumber = indexOfRfc3966Prefix >= 0 ? RFC3966_PREFIX.length() + indexOfRfc3966Prefix : 0;
            nationalNumber.append(numberToParse.substring(indexOfNationalNumber, indexOfPhoneContext));
        } else {
            nationalNumber.append(extractPossibleNumber(numberToParse));
        }
        int indexOfIsdn = nationalNumber.indexOf(RFC3966_ISDN_SUBADDRESS);
        if (indexOfIsdn > 0) {
            nationalNumber.delete(indexOfIsdn, nationalNumber.length());
        }
    }

    private static Phonenumber.PhoneNumber copyCoreFieldsOnly(Phonenumber.PhoneNumber phoneNumberIn) {
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(phoneNumberIn.getCountryCode());
        phoneNumber.setNationalNumber(phoneNumberIn.getNationalNumber());
        if (phoneNumberIn.getExtension().length() > 0) {
            phoneNumber.setExtension(phoneNumberIn.getExtension());
        }
        if (phoneNumberIn.isItalianLeadingZero()) {
            phoneNumber.setItalianLeadingZero(true);
            phoneNumber.setNumberOfLeadingZeros(phoneNumberIn.getNumberOfLeadingZeros());
        }
        return phoneNumber;
    }

    public MatchType isNumberMatch(Phonenumber.PhoneNumber firstNumberIn, Phonenumber.PhoneNumber secondNumberIn) {
        Phonenumber.PhoneNumber firstNumber = copyCoreFieldsOnly(firstNumberIn);
        Phonenumber.PhoneNumber secondNumber = copyCoreFieldsOnly(secondNumberIn);
        if (firstNumber.hasExtension() && secondNumber.hasExtension() && !firstNumber.getExtension().equals(secondNumber.getExtension())) {
            return MatchType.NO_MATCH;
        }
        int firstNumberCountryCode = firstNumber.getCountryCode();
        int secondNumberCountryCode = secondNumber.getCountryCode();
        if (firstNumberCountryCode != 0 && secondNumberCountryCode != 0) {
            if (firstNumber.exactlySameAs(secondNumber)) {
                return MatchType.EXACT_MATCH;
            }
            if (firstNumberCountryCode == secondNumberCountryCode && isNationalNumberSuffixOfTheOther(firstNumber, secondNumber)) {
                return MatchType.SHORT_NSN_MATCH;
            }
            return MatchType.NO_MATCH;
        }
        firstNumber.setCountryCode(secondNumberCountryCode);
        if (firstNumber.exactlySameAs(secondNumber)) {
            return MatchType.NSN_MATCH;
        }
        if (isNationalNumberSuffixOfTheOther(firstNumber, secondNumber)) {
            return MatchType.SHORT_NSN_MATCH;
        }
        return MatchType.NO_MATCH;
    }

    private boolean isNationalNumberSuffixOfTheOther(Phonenumber.PhoneNumber firstNumber, Phonenumber.PhoneNumber secondNumber) {
        String firstNumberNationalNumber = String.valueOf(firstNumber.getNationalNumber());
        String secondNumberNationalNumber = String.valueOf(secondNumber.getNationalNumber());
        return firstNumberNationalNumber.endsWith(secondNumberNationalNumber) || secondNumberNationalNumber.endsWith(firstNumberNationalNumber);
    }

    public MatchType isNumberMatch(CharSequence firstNumber, CharSequence secondNumber) {
        try {
            Phonenumber.PhoneNumber firstNumberAsProto = parse(firstNumber, UNKNOWN_REGION);
            return isNumberMatch(firstNumberAsProto, secondNumber);
        } catch (NumberParseException e) {
            if (e.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE) {
                try {
                    Phonenumber.PhoneNumber secondNumberAsProto = parse(secondNumber, UNKNOWN_REGION);
                    return isNumberMatch(secondNumberAsProto, firstNumber);
                } catch (NumberParseException e2) {
                    if (e2.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE) {
                        try {
                            Phonenumber.PhoneNumber firstNumberProto = new Phonenumber.PhoneNumber();
                            Phonenumber.PhoneNumber secondNumberProto = new Phonenumber.PhoneNumber();
                            parseHelper(firstNumber, null, false, false, firstNumberProto);
                            parseHelper(secondNumber, null, false, false, secondNumberProto);
                            return isNumberMatch(firstNumberProto, secondNumberProto);
                        } catch (NumberParseException e3) {
                            return MatchType.NOT_A_NUMBER;
                        }
                    }
                    return MatchType.NOT_A_NUMBER;
                }
            }
            return MatchType.NOT_A_NUMBER;
        }
    }

    public MatchType isNumberMatch(Phonenumber.PhoneNumber firstNumber, CharSequence secondNumber) {
        try {
            Phonenumber.PhoneNumber secondNumberAsProto = parse(secondNumber, UNKNOWN_REGION);
            return isNumberMatch(firstNumber, secondNumberAsProto);
        } catch (NumberParseException e) {
            if (e.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE) {
                String firstNumberRegion = getRegionCodeForCountryCode(firstNumber.getCountryCode());
                try {
                    if (!firstNumberRegion.equals(UNKNOWN_REGION)) {
                        Phonenumber.PhoneNumber secondNumberWithFirstNumberRegion = parse(secondNumber, firstNumberRegion);
                        MatchType match = isNumberMatch(firstNumber, secondNumberWithFirstNumberRegion);
                        if (match == MatchType.EXACT_MATCH) {
                            return MatchType.NSN_MATCH;
                        }
                        return match;
                    }
                    Phonenumber.PhoneNumber secondNumberProto = new Phonenumber.PhoneNumber();
                    parseHelper(secondNumber, null, false, false, secondNumberProto);
                    return isNumberMatch(firstNumber, secondNumberProto);
                } catch (NumberParseException e2) {
                    return MatchType.NOT_A_NUMBER;
                }
            }
            return MatchType.NOT_A_NUMBER;
        }
    }

    public boolean canBeInternationallyDialled(Phonenumber.PhoneNumber number) {
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegion(getRegionCodeForNumber(number));
        if (metadata == null) {
            return true;
        }
        String nationalSignificantNumber = getNationalSignificantNumber(number);
        return true ^ isNumberMatchingDesc(nationalSignificantNumber, metadata.getNoInternationalDialling());
    }

    public boolean isMobileNumberPortableRegion(String regionCode) {
        Phonemetadata.PhoneMetadata metadata = getMetadataForRegion(regionCode);
        if (metadata == null) {
            logger.log(Level.WARNING, "Invalid or unknown region code provided: " + regionCode);
            return false;
        }
        return metadata.getMobileNumberPortableRegion();
    }
}
