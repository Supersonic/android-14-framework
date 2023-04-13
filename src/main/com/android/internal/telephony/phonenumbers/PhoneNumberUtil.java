package com.android.internal.telephony.phonenumbers;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.d2d.DtmfTransport;
import com.android.internal.telephony.phonenumbers.NumberParseException;
import com.android.internal.telephony.phonenumbers.PhoneNumberMatcher;
import com.android.internal.telephony.phonenumbers.Phonemetadata$NumberFormat;
import com.android.internal.telephony.phonenumbers.Phonenumber$PhoneNumber;
import com.android.internal.telephony.phonenumbers.internal.MatcherApi;
import com.android.internal.telephony.phonenumbers.internal.RegexBasedMatcher;
import com.android.internal.telephony.phonenumbers.internal.RegexCache;
import com.android.internal.telephony.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
import com.android.internal.telephony.phonenumbers.metadata.source.MetadataSource;
import com.android.internal.telephony.phonenumbers.metadata.source.MetadataSourceImpl;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final Map<Character, Character> DIALLABLE_CHAR_MAPPINGS;
    private static final Pattern EXTN_PATTERN;
    static final String EXTN_PATTERNS_FOR_MATCHING;
    private static final String EXTN_PATTERNS_FOR_PARSING;
    private static final Pattern FIRST_GROUP_ONLY_PREFIX_PATTERN;
    private static final Pattern FIRST_GROUP_PATTERN;
    private static final Set<Integer> GEO_MOBILE_COUNTRIES;
    private static final Set<Integer> GEO_MOBILE_COUNTRIES_WITHOUT_MOBILE_AREA_CODES;
    private static final Map<Integer, String> MOBILE_TOKEN_MAPPINGS;
    static final Pattern NON_DIGITS_PATTERN;
    static final Pattern PLUS_CHARS_PATTERN;
    public static final String REGION_CODE_FOR_NON_GEO_ENTITY = "001";
    static final Pattern SECOND_NUMBER_START_PATTERN;
    private static final Pattern SEPARATOR_PATTERN;
    private static final Pattern SINGLE_INTERNATIONAL_PREFIX;
    static final Pattern UNWANTED_END_CHAR_PATTERN;
    private static final String VALID_ALPHA;
    private static final Pattern VALID_ALPHA_PHONE_PATTERN;
    private static final String VALID_PHONE_NUMBER;
    private static final Pattern VALID_PHONE_NUMBER_PATTERN;
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
    public enum Leniency {
        POSSIBLE { // from class: com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency.1
            @Override // com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency
            boolean verify(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence, PhoneNumberUtil phoneNumberUtil, PhoneNumberMatcher phoneNumberMatcher) {
                return phoneNumberUtil.isPossibleNumber(phonenumber$PhoneNumber);
            }
        },
        VALID { // from class: com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency.2
            @Override // com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency
            boolean verify(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence, PhoneNumberUtil phoneNumberUtil, PhoneNumberMatcher phoneNumberMatcher) {
                if (phoneNumberUtil.isValidNumber(phonenumber$PhoneNumber) && PhoneNumberMatcher.containsOnlyValidXChars(phonenumber$PhoneNumber, charSequence.toString(), phoneNumberUtil)) {
                    return PhoneNumberMatcher.isNationalPrefixPresentIfRequired(phonenumber$PhoneNumber, phoneNumberUtil);
                }
                return false;
            }
        },
        STRICT_GROUPING { // from class: com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency.3
            @Override // com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency
            boolean verify(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence, PhoneNumberUtil phoneNumberUtil, PhoneNumberMatcher phoneNumberMatcher) {
                String charSequence2 = charSequence.toString();
                if (phoneNumberUtil.isValidNumber(phonenumber$PhoneNumber) && PhoneNumberMatcher.containsOnlyValidXChars(phonenumber$PhoneNumber, charSequence2, phoneNumberUtil) && !PhoneNumberMatcher.containsMoreThanOneSlashInNationalNumber(phonenumber$PhoneNumber, charSequence2) && PhoneNumberMatcher.isNationalPrefixPresentIfRequired(phonenumber$PhoneNumber, phoneNumberUtil)) {
                    return phoneNumberMatcher.checkNumberGroupingIsValid(phonenumber$PhoneNumber, charSequence, phoneNumberUtil, new PhoneNumberMatcher.NumberGroupingChecker() { // from class: com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency.3.1
                        @Override // com.android.internal.telephony.phonenumbers.PhoneNumberMatcher.NumberGroupingChecker
                        public boolean checkGroups(PhoneNumberUtil phoneNumberUtil2, Phonenumber$PhoneNumber phonenumber$PhoneNumber2, StringBuilder sb, String[] strArr) {
                            return PhoneNumberMatcher.allNumberGroupsRemainGrouped(phoneNumberUtil2, phonenumber$PhoneNumber2, sb, strArr);
                        }
                    });
                }
                return false;
            }
        },
        EXACT_GROUPING { // from class: com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency.4
            @Override // com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency
            boolean verify(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence, PhoneNumberUtil phoneNumberUtil, PhoneNumberMatcher phoneNumberMatcher) {
                String charSequence2 = charSequence.toString();
                if (phoneNumberUtil.isValidNumber(phonenumber$PhoneNumber) && PhoneNumberMatcher.containsOnlyValidXChars(phonenumber$PhoneNumber, charSequence2, phoneNumberUtil) && !PhoneNumberMatcher.containsMoreThanOneSlashInNationalNumber(phonenumber$PhoneNumber, charSequence2) && PhoneNumberMatcher.isNationalPrefixPresentIfRequired(phonenumber$PhoneNumber, phoneNumberUtil)) {
                    return phoneNumberMatcher.checkNumberGroupingIsValid(phonenumber$PhoneNumber, charSequence, phoneNumberUtil, new PhoneNumberMatcher.NumberGroupingChecker() { // from class: com.android.internal.telephony.phonenumbers.PhoneNumberUtil.Leniency.4.1
                        @Override // com.android.internal.telephony.phonenumbers.PhoneNumberMatcher.NumberGroupingChecker
                        public boolean checkGroups(PhoneNumberUtil phoneNumberUtil2, Phonenumber$PhoneNumber phonenumber$PhoneNumber2, StringBuilder sb, String[] strArr) {
                            return PhoneNumberMatcher.allNumberGroupsAreExactlyPresent(phoneNumberUtil2, phonenumber$PhoneNumber2, sb, strArr);
                        }
                    });
                }
                return false;
            }
        };

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract boolean verify(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence, PhoneNumberUtil phoneNumberUtil, PhoneNumberMatcher phoneNumberMatcher);
    }

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
        HashMap hashMap = new HashMap();
        hashMap.put(54, "9");
        MOBILE_TOKEN_MAPPINGS = Collections.unmodifiableMap(hashMap);
        HashSet hashSet = new HashSet();
        hashSet.add(86);
        GEO_MOBILE_COUNTRIES_WITHOUT_MOBILE_AREA_CODES = Collections.unmodifiableSet(hashSet);
        HashSet hashSet2 = new HashSet();
        hashSet2.add(52);
        hashSet2.add(54);
        hashSet2.add(55);
        hashSet2.add(62);
        hashSet2.addAll(hashSet);
        GEO_MOBILE_COUNTRIES = Collections.unmodifiableSet(hashSet2);
        HashMap hashMap2 = new HashMap();
        hashMap2.put('0', '0');
        hashMap2.put('1', '1');
        hashMap2.put('2', '2');
        hashMap2.put('3', '3');
        hashMap2.put('4', '4');
        hashMap2.put('5', '5');
        hashMap2.put('6', '6');
        hashMap2.put('7', '7');
        hashMap2.put('8', '8');
        hashMap2.put('9', '9');
        HashMap hashMap3 = new HashMap(40);
        hashMap3.put(Character.valueOf(DtmfTransport.DTMF_MESSAGE_START), '2');
        hashMap3.put('B', '2');
        hashMap3.put('C', '2');
        hashMap3.put(Character.valueOf(DtmfTransport.DTMF_MESSAGE_DELIMITER), '3');
        hashMap3.put('E', '3');
        hashMap3.put('F', '3');
        hashMap3.put('G', '4');
        hashMap3.put('H', '4');
        hashMap3.put('I', '4');
        hashMap3.put('J', '5');
        hashMap3.put('K', '5');
        hashMap3.put('L', '5');
        hashMap3.put('M', '6');
        hashMap3.put('N', '6');
        hashMap3.put('O', '6');
        hashMap3.put('P', '7');
        hashMap3.put('Q', '7');
        hashMap3.put('R', '7');
        hashMap3.put('S', '7');
        hashMap3.put('T', '8');
        hashMap3.put('U', '8');
        hashMap3.put('V', '8');
        hashMap3.put('W', '9');
        hashMap3.put('X', '9');
        hashMap3.put('Y', '9');
        hashMap3.put('Z', '9');
        Map<Character, Character> unmodifiableMap = Collections.unmodifiableMap(hashMap3);
        ALPHA_MAPPINGS = unmodifiableMap;
        HashMap hashMap4 = new HashMap(100);
        hashMap4.putAll(unmodifiableMap);
        hashMap4.putAll(hashMap2);
        ALPHA_PHONE_MAPPINGS = Collections.unmodifiableMap(hashMap4);
        HashMap hashMap5 = new HashMap();
        hashMap5.putAll(hashMap2);
        hashMap5.put('+', '+');
        hashMap5.put('*', '*');
        hashMap5.put('#', '#');
        DIALLABLE_CHAR_MAPPINGS = Collections.unmodifiableMap(hashMap5);
        HashMap hashMap6 = new HashMap();
        for (Character ch : unmodifiableMap.keySet()) {
            char charValue = ch.charValue();
            hashMap6.put(Character.valueOf(Character.toLowerCase(charValue)), Character.valueOf(charValue));
            hashMap6.put(Character.valueOf(charValue), Character.valueOf(charValue));
        }
        hashMap6.putAll(hashMap2);
        hashMap6.put('-', '-');
        hashMap6.put((char) 65293, '-');
        hashMap6.put((char) 8208, '-');
        hashMap6.put((char) 8209, '-');
        hashMap6.put((char) 8210, '-');
        hashMap6.put((char) 8211, '-');
        hashMap6.put((char) 8212, '-');
        hashMap6.put((char) 8213, '-');
        hashMap6.put((char) 8722, '-');
        hashMap6.put('/', '/');
        hashMap6.put((char) 65295, '/');
        hashMap6.put(' ', ' ');
        hashMap6.put((char) 12288, ' ');
        hashMap6.put((char) 8288, ' ');
        hashMap6.put('.', '.');
        hashMap6.put((char) 65294, '.');
        ALL_PLUS_NUMBER_GROUPING_SYMBOLS = Collections.unmodifiableMap(hashMap6);
        SINGLE_INTERNATIONAL_PREFIX = Pattern.compile("[\\d]+(?:[~⁓∼～][\\d]+)?");
        StringBuilder sb = new StringBuilder();
        Map<Character, Character> map = ALPHA_MAPPINGS;
        sb.append(Arrays.toString(map.keySet().toArray()).replaceAll("[, \\[\\]]", PhoneConfigurationManager.SSSS));
        sb.append(Arrays.toString(map.keySet().toArray()).toLowerCase().replaceAll("[, \\[\\]]", PhoneConfigurationManager.SSSS));
        String sb2 = sb.toString();
        VALID_ALPHA = sb2;
        PLUS_CHARS_PATTERN = Pattern.compile("[+＋]+");
        SEPARATOR_PATTERN = Pattern.compile("[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]+");
        CAPTURING_DIGIT_PATTERN = Pattern.compile("(\\p{Nd})");
        VALID_START_CHAR_PATTERN = Pattern.compile("[+＋\\p{Nd}]");
        SECOND_NUMBER_START_PATTERN = Pattern.compile("[\\\\/] *x");
        UNWANTED_END_CHAR_PATTERN = Pattern.compile("[[\\P{N}&&\\P{L}]&&[^#]]+$");
        VALID_ALPHA_PHONE_PATTERN = Pattern.compile("(?:.*?[A-Za-z]){3}.*");
        String str = "\\p{Nd}{2}|[+＋]*+(?:[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～*]*\\p{Nd}){3,}[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～*" + sb2 + "\\p{Nd}]*";
        VALID_PHONE_NUMBER = str;
        String createExtnPattern = createExtnPattern(true);
        EXTN_PATTERNS_FOR_PARSING = createExtnPattern;
        EXTN_PATTERNS_FOR_MATCHING = createExtnPattern(false);
        EXTN_PATTERN = Pattern.compile("(?:" + createExtnPattern + ")$", 66);
        VALID_PHONE_NUMBER_PATTERN = Pattern.compile(str + "(?:" + createExtnPattern + ")?", 66);
        NON_DIGITS_PATTERN = Pattern.compile("(\\D+)");
        FIRST_GROUP_PATTERN = Pattern.compile("(\\$\\d)");
        FIRST_GROUP_ONLY_PREFIX_PATTERN = Pattern.compile("\\(?\\$1\\)?");
        instance = null;
    }

    private static String extnDigits(int i) {
        return "(\\p{Nd}{1," + i + "})";
    }

    private static String createExtnPattern(boolean z) {
        String str = (";ext=" + extnDigits(20)) + "|" + ("[  \\t,]*(?:e?xt(?:ensi(?:ó?|ó))?n?|ｅ?ｘｔｎ?|доб|anexo)[:\\.．]?[  \\t,-]*" + extnDigits(20) + "#?") + "|" + ("[  \\t,]*(?:[xｘ#＃~～]|int|ｉｎｔ)[:\\.．]?[  \\t,-]*" + extnDigits(9) + "#?") + "|" + ("[- ]+" + extnDigits(6) + "#");
        if (z) {
            return str + "|" + ("[  \\t]*(?:,{2}|;)[:\\.．]?[  \\t,-]*" + extnDigits(15) + "#?") + "|" + ("[  \\t]*(?:,)+[:\\.．]?[  \\t,-]*" + extnDigits(9) + "#?");
        }
        return str;
    }

    PhoneNumberUtil(MetadataSource metadataSource, Map<Integer, List<String>> map) {
        this.metadataSource = metadataSource;
        this.countryCallingCodeToRegionCodeMap = map;
        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
            List<String> value = entry.getValue();
            if (value.size() == 1 && "001".equals(value.get(0))) {
                this.countryCodesForNonGeographicalRegion.add(entry.getKey());
            } else {
                this.supportedRegions.addAll(value);
            }
        }
        if (this.supportedRegions.remove("001")) {
            logger.log(Level.WARNING, "invalid metadata (country calling code was mapped to the non-geo entity as well as specific region(s))");
        }
        this.nanpaRegions.addAll(map.get(1));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CharSequence extractPossibleNumber(CharSequence charSequence) {
        Matcher matcher = VALID_START_CHAR_PATTERN.matcher(charSequence);
        if (matcher.find()) {
            CharSequence subSequence = charSequence.subSequence(matcher.start(), charSequence.length());
            Matcher matcher2 = UNWANTED_END_CHAR_PATTERN.matcher(subSequence);
            if (matcher2.find()) {
                subSequence = subSequence.subSequence(0, matcher2.start());
            }
            Matcher matcher3 = SECOND_NUMBER_START_PATTERN.matcher(subSequence);
            return matcher3.find() ? subSequence.subSequence(0, matcher3.start()) : subSequence;
        }
        return PhoneConfigurationManager.SSSS;
    }

    static boolean isViablePhoneNumber(CharSequence charSequence) {
        if (charSequence.length() < 2) {
            return false;
        }
        return VALID_PHONE_NUMBER_PATTERN.matcher(charSequence).matches();
    }

    static StringBuilder normalize(StringBuilder sb) {
        if (VALID_ALPHA_PHONE_PATTERN.matcher(sb).matches()) {
            sb.replace(0, sb.length(), normalizeHelper(sb, ALPHA_PHONE_MAPPINGS, true));
        } else {
            sb.replace(0, sb.length(), normalizeDigitsOnly(sb));
        }
        return sb;
    }

    public static String normalizeDigitsOnly(CharSequence charSequence) {
        return normalizeDigits(charSequence, false).toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static StringBuilder normalizeDigits(CharSequence charSequence, boolean z) {
        StringBuilder sb = new StringBuilder(charSequence.length());
        for (int i = 0; i < charSequence.length(); i++) {
            char charAt = charSequence.charAt(i);
            int digit = Character.digit(charAt, 10);
            if (digit != -1) {
                sb.append(digit);
            } else if (z) {
                sb.append(charAt);
            }
        }
        return sb;
    }

    public static String normalizeDiallableCharsOnly(CharSequence charSequence) {
        return normalizeHelper(charSequence, DIALLABLE_CHAR_MAPPINGS, true);
    }

    public static String convertAlphaCharactersInNumber(CharSequence charSequence) {
        return normalizeHelper(charSequence, ALPHA_PHONE_MAPPINGS, false);
    }

    public int getLengthOfGeographicalAreaCode(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(getRegionCodeForNumber(phonenumber$PhoneNumber));
        if (metadataForRegion == null) {
            return 0;
        }
        if (metadataForRegion.hasNationalPrefix() || phonenumber$PhoneNumber.isItalianLeadingZero()) {
            PhoneNumberType numberType = getNumberType(phonenumber$PhoneNumber);
            int countryCode = phonenumber$PhoneNumber.getCountryCode();
            if (!(numberType == PhoneNumberType.MOBILE && GEO_MOBILE_COUNTRIES_WITHOUT_MOBILE_AREA_CODES.contains(Integer.valueOf(countryCode))) && isNumberGeographical(numberType, countryCode)) {
                return getLengthOfNationalDestinationCode(phonenumber$PhoneNumber);
            }
            return 0;
        }
        return 0;
    }

    public int getLengthOfNationalDestinationCode(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        Phonenumber$PhoneNumber phonenumber$PhoneNumber2;
        if (phonenumber$PhoneNumber.hasExtension()) {
            phonenumber$PhoneNumber2 = new Phonenumber$PhoneNumber();
            phonenumber$PhoneNumber2.mergeFrom(phonenumber$PhoneNumber);
            phonenumber$PhoneNumber2.clearExtension();
        } else {
            phonenumber$PhoneNumber2 = phonenumber$PhoneNumber;
        }
        String[] split = NON_DIGITS_PATTERN.split(format(phonenumber$PhoneNumber2, PhoneNumberFormat.INTERNATIONAL));
        if (split.length <= 3) {
            return 0;
        }
        if (getNumberType(phonenumber$PhoneNumber) == PhoneNumberType.MOBILE && !getCountryMobileToken(phonenumber$PhoneNumber.getCountryCode()).equals(PhoneConfigurationManager.SSSS)) {
            return split[2].length() + split[3].length();
        }
        return split[2].length();
    }

    public static String getCountryMobileToken(int i) {
        Map<Integer, String> map = MOBILE_TOKEN_MAPPINGS;
        return map.containsKey(Integer.valueOf(i)) ? map.get(Integer.valueOf(i)) : PhoneConfigurationManager.SSSS;
    }

    private static String normalizeHelper(CharSequence charSequence, Map<Character, Character> map, boolean z) {
        StringBuilder sb = new StringBuilder(charSequence.length());
        for (int i = 0; i < charSequence.length(); i++) {
            char charAt = charSequence.charAt(i);
            Character ch = map.get(Character.valueOf(Character.toUpperCase(charAt)));
            if (ch != null) {
                sb.append(ch);
            } else if (!z) {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    static synchronized void setInstance(PhoneNumberUtil phoneNumberUtil) {
        synchronized (PhoneNumberUtil.class) {
            instance = phoneNumberUtil;
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

    private static boolean descHasPossibleNumberData(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        return (phonemetadata$PhoneNumberDesc.getPossibleLengthCount() == 1 && phonemetadata$PhoneNumberDesc.getPossibleLength(0) == -1) ? false : true;
    }

    private static boolean descHasData(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        return phonemetadata$PhoneNumberDesc.hasExampleNumber() || descHasPossibleNumberData(phonemetadata$PhoneNumberDesc) || phonemetadata$PhoneNumberDesc.hasNationalNumberPattern();
    }

    private Set<PhoneNumberType> getSupportedTypesForMetadata(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata) {
        PhoneNumberType[] values;
        TreeSet treeSet = new TreeSet();
        for (PhoneNumberType phoneNumberType : PhoneNumberType.values()) {
            if (phoneNumberType != PhoneNumberType.FIXED_LINE_OR_MOBILE && phoneNumberType != PhoneNumberType.UNKNOWN && descHasData(getNumberDescByType(phonemetadata$PhoneMetadata, phoneNumberType))) {
                treeSet.add(phoneNumberType);
            }
        }
        return Collections.unmodifiableSet(treeSet);
    }

    public Set<PhoneNumberType> getSupportedTypesForRegion(String str) {
        if (!isValidRegionCode(str)) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            logger2.log(level, "Invalid or unknown region code provided: " + str);
            return Collections.unmodifiableSet(new TreeSet());
        }
        return getSupportedTypesForMetadata(getMetadataForRegion(str));
    }

    public Set<PhoneNumberType> getSupportedTypesForNonGeoEntity(int i) {
        Phonemetadata$PhoneMetadata metadataForNonGeographicalRegion = getMetadataForNonGeographicalRegion(i);
        if (metadataForNonGeographicalRegion == null) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            logger2.log(level, "Unknown country calling code for a non-geographical entity provided: " + i);
            return Collections.unmodifiableSet(new TreeSet());
        }
        return getSupportedTypesForMetadata(metadataForNonGeographicalRegion);
    }

    public static synchronized PhoneNumberUtil getInstance() {
        PhoneNumberUtil phoneNumberUtil;
        synchronized (PhoneNumberUtil.class) {
            if (instance == null) {
                setInstance(createInstance(DefaultMetadataDependenciesProvider.getInstance().getMetadataLoader()));
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
    public static boolean formattingRuleHasFirstGroupOnly(String str) {
        return str.length() == 0 || FIRST_GROUP_ONLY_PREFIX_PATTERN.matcher(str).matches();
    }

    public boolean isNumberGeographical(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        return isNumberGeographical(getNumberType(phonenumber$PhoneNumber), phonenumber$PhoneNumber.getCountryCode());
    }

    public boolean isNumberGeographical(PhoneNumberType phoneNumberType, int i) {
        return phoneNumberType == PhoneNumberType.FIXED_LINE || phoneNumberType == PhoneNumberType.FIXED_LINE_OR_MOBILE || (GEO_MOBILE_COUNTRIES.contains(Integer.valueOf(i)) && phoneNumberType == PhoneNumberType.MOBILE);
    }

    private boolean isValidRegionCode(String str) {
        return str != null && this.supportedRegions.contains(str);
    }

    private boolean hasValidCountryCallingCode(int i) {
        return this.countryCallingCodeToRegionCodeMap.containsKey(Integer.valueOf(i));
    }

    public String format(Phonenumber$PhoneNumber phonenumber$PhoneNumber, PhoneNumberFormat phoneNumberFormat) {
        if (phonenumber$PhoneNumber.getNationalNumber() == 0 && phonenumber$PhoneNumber.hasRawInput()) {
            String rawInput = phonenumber$PhoneNumber.getRawInput();
            if (rawInput.length() > 0) {
                return rawInput;
            }
        }
        StringBuilder sb = new StringBuilder(20);
        format(phonenumber$PhoneNumber, phoneNumberFormat, sb);
        return sb.toString();
    }

    public void format(Phonenumber$PhoneNumber phonenumber$PhoneNumber, PhoneNumberFormat phoneNumberFormat, StringBuilder sb) {
        sb.setLength(0);
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        PhoneNumberFormat phoneNumberFormat2 = PhoneNumberFormat.E164;
        if (phoneNumberFormat == phoneNumberFormat2) {
            sb.append(nationalSignificantNumber);
            prefixNumberWithCountryCallingCode(countryCode, phoneNumberFormat2, sb);
        } else if (!hasValidCountryCallingCode(countryCode)) {
            sb.append(nationalSignificantNumber);
        } else {
            Phonemetadata$PhoneMetadata metadataForRegionOrCallingCode = getMetadataForRegionOrCallingCode(countryCode, getRegionCodeForCountryCode(countryCode));
            sb.append(formatNsn(nationalSignificantNumber, metadataForRegionOrCallingCode, phoneNumberFormat));
            maybeAppendFormattedExtension(phonenumber$PhoneNumber, metadataForRegionOrCallingCode, phoneNumberFormat, sb);
            prefixNumberWithCountryCallingCode(countryCode, phoneNumberFormat, sb);
        }
    }

    public String formatByPattern(Phonenumber$PhoneNumber phonenumber$PhoneNumber, PhoneNumberFormat phoneNumberFormat, List<Phonemetadata$NumberFormat> list) {
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        if (hasValidCountryCallingCode(countryCode)) {
            Phonemetadata$PhoneMetadata metadataForRegionOrCallingCode = getMetadataForRegionOrCallingCode(countryCode, getRegionCodeForCountryCode(countryCode));
            StringBuilder sb = new StringBuilder(20);
            Phonemetadata$NumberFormat chooseFormattingPatternForNumber = chooseFormattingPatternForNumber(list, nationalSignificantNumber);
            if (chooseFormattingPatternForNumber == null) {
                sb.append(nationalSignificantNumber);
            } else {
                Phonemetadata$NumberFormat.Builder newBuilder = Phonemetadata$NumberFormat.newBuilder();
                newBuilder.mergeFrom(chooseFormattingPatternForNumber);
                String nationalPrefixFormattingRule = chooseFormattingPatternForNumber.getNationalPrefixFormattingRule();
                if (nationalPrefixFormattingRule.length() > 0) {
                    String nationalPrefix = metadataForRegionOrCallingCode.getNationalPrefix();
                    if (nationalPrefix.length() > 0) {
                        newBuilder.setNationalPrefixFormattingRule(nationalPrefixFormattingRule.replace("$NP", nationalPrefix).replace("$FG", "$1"));
                    } else {
                        newBuilder.clearNationalPrefixFormattingRule();
                    }
                }
                sb.append(formatNsnUsingPattern(nationalSignificantNumber, newBuilder.build(), phoneNumberFormat));
            }
            maybeAppendFormattedExtension(phonenumber$PhoneNumber, metadataForRegionOrCallingCode, phoneNumberFormat, sb);
            prefixNumberWithCountryCallingCode(countryCode, phoneNumberFormat, sb);
            return sb.toString();
        }
        return nationalSignificantNumber;
    }

    public String formatNationalNumberWithCarrierCode(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence) {
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        if (hasValidCountryCallingCode(countryCode)) {
            Phonemetadata$PhoneMetadata metadataForRegionOrCallingCode = getMetadataForRegionOrCallingCode(countryCode, getRegionCodeForCountryCode(countryCode));
            StringBuilder sb = new StringBuilder(20);
            PhoneNumberFormat phoneNumberFormat = PhoneNumberFormat.NATIONAL;
            sb.append(formatNsn(nationalSignificantNumber, metadataForRegionOrCallingCode, phoneNumberFormat, charSequence));
            maybeAppendFormattedExtension(phonenumber$PhoneNumber, metadataForRegionOrCallingCode, phoneNumberFormat, sb);
            prefixNumberWithCountryCallingCode(countryCode, phoneNumberFormat, sb);
            return sb.toString();
        }
        return nationalSignificantNumber;
    }

    private Phonemetadata$PhoneMetadata getMetadataForRegionOrCallingCode(int i, String str) {
        if ("001".equals(str)) {
            return getMetadataForNonGeographicalRegion(i);
        }
        return getMetadataForRegion(str);
    }

    public String formatNationalNumberWithPreferredCarrierCode(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence) {
        if (phonenumber$PhoneNumber.getPreferredDomesticCarrierCode().length() > 0) {
            charSequence = phonenumber$PhoneNumber.getPreferredDomesticCarrierCode();
        }
        return formatNationalNumberWithCarrierCode(phonenumber$PhoneNumber, charSequence);
    }

    public String formatNumberForMobileDialing(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str, boolean z) {
        String format;
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        boolean hasValidCountryCallingCode = hasValidCountryCallingCode(countryCode);
        String str2 = PhoneConfigurationManager.SSSS;
        if (!hasValidCountryCallingCode) {
            return phonenumber$PhoneNumber.hasRawInput() ? phonenumber$PhoneNumber.getRawInput() : PhoneConfigurationManager.SSSS;
        }
        Phonenumber$PhoneNumber clearExtension = new Phonenumber$PhoneNumber().mergeFrom(phonenumber$PhoneNumber).clearExtension();
        String regionCodeForCountryCode = getRegionCodeForCountryCode(countryCode);
        PhoneNumberType numberType = getNumberType(clearExtension);
        boolean z2 = false;
        boolean z3 = numberType != PhoneNumberType.UNKNOWN;
        if (str.equals(regionCodeForCountryCode)) {
            if (numberType == PhoneNumberType.FIXED_LINE || numberType == PhoneNumberType.MOBILE || numberType == PhoneNumberType.FIXED_LINE_OR_MOBILE) {
                z2 = true;
            }
            if (!regionCodeForCountryCode.equals("BR") || !z2) {
                if (countryCode == 1) {
                    Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
                    if (canBeInternationallyDialled(clearExtension) && testNumberLength(getNationalSignificantNumber(clearExtension), metadataForRegion) != ValidationResult.TOO_SHORT) {
                        format = format(clearExtension, PhoneNumberFormat.INTERNATIONAL);
                    } else {
                        format = format(clearExtension, PhoneNumberFormat.NATIONAL);
                    }
                } else if ((regionCodeForCountryCode.equals("001") || ((regionCodeForCountryCode.equals("MX") || regionCodeForCountryCode.equals("CL") || regionCodeForCountryCode.equals("UZ")) && z2)) && canBeInternationallyDialled(clearExtension)) {
                    format = format(clearExtension, PhoneNumberFormat.INTERNATIONAL);
                } else {
                    format = format(clearExtension, PhoneNumberFormat.NATIONAL);
                }
                str2 = format;
            } else if (clearExtension.getPreferredDomesticCarrierCode().length() > 0) {
                str2 = formatNationalNumberWithPreferredCarrierCode(clearExtension, PhoneConfigurationManager.SSSS);
            }
        } else if (z3 && canBeInternationallyDialled(clearExtension)) {
            if (z) {
                return format(clearExtension, PhoneNumberFormat.INTERNATIONAL);
            }
            return format(clearExtension, PhoneNumberFormat.E164);
        }
        return z ? str2 : normalizeDiallableCharsOnly(str2);
    }

    public String formatOutOfCountryCallingNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        if (!isValidRegionCode(str)) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            logger2.log(level, "Trying to format number from invalid region " + str + ". International formatting applied.");
            return format(phonenumber$PhoneNumber, PhoneNumberFormat.INTERNATIONAL);
        }
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        if (hasValidCountryCallingCode(countryCode)) {
            if (countryCode == 1) {
                if (isNANPACountry(str)) {
                    return countryCode + " " + format(phonenumber$PhoneNumber, PhoneNumberFormat.NATIONAL);
                }
            } else if (countryCode == getCountryCodeForValidRegion(str)) {
                return format(phonenumber$PhoneNumber, PhoneNumberFormat.NATIONAL);
            }
            Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
            String internationalPrefix = metadataForRegion.getInternationalPrefix();
            if (metadataForRegion.hasPreferredInternationalPrefix()) {
                internationalPrefix = metadataForRegion.getPreferredInternationalPrefix();
            } else if (!SINGLE_INTERNATIONAL_PREFIX.matcher(internationalPrefix).matches()) {
                internationalPrefix = PhoneConfigurationManager.SSSS;
            }
            Phonemetadata$PhoneMetadata metadataForRegionOrCallingCode = getMetadataForRegionOrCallingCode(countryCode, getRegionCodeForCountryCode(countryCode));
            PhoneNumberFormat phoneNumberFormat = PhoneNumberFormat.INTERNATIONAL;
            StringBuilder sb = new StringBuilder(formatNsn(nationalSignificantNumber, metadataForRegionOrCallingCode, phoneNumberFormat));
            maybeAppendFormattedExtension(phonenumber$PhoneNumber, metadataForRegionOrCallingCode, phoneNumberFormat, sb);
            if (internationalPrefix.length() > 0) {
                sb.insert(0, " ").insert(0, countryCode).insert(0, " ").insert(0, internationalPrefix);
            } else {
                prefixNumberWithCountryCallingCode(countryCode, phoneNumberFormat, sb);
            }
            return sb.toString();
        }
        return nationalSignificantNumber;
    }

    public String formatInOriginalFormat(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        String format;
        String nationalPrefixFormattingRule;
        int indexOf;
        if (phonenumber$PhoneNumber.hasRawInput() && !hasFormattingPatternForNumber(phonenumber$PhoneNumber)) {
            return phonenumber$PhoneNumber.getRawInput();
        }
        if (!phonenumber$PhoneNumber.hasCountryCodeSource()) {
            return format(phonenumber$PhoneNumber, PhoneNumberFormat.NATIONAL);
        }
        int i = C02692.f20x9de98e3a[phonenumber$PhoneNumber.getCountryCodeSource().ordinal()];
        if (i == 1) {
            format = format(phonenumber$PhoneNumber, PhoneNumberFormat.INTERNATIONAL);
        } else if (i == 2) {
            format = formatOutOfCountryCallingNumber(phonenumber$PhoneNumber, str);
        } else if (i == 3) {
            format = format(phonenumber$PhoneNumber, PhoneNumberFormat.INTERNATIONAL).substring(1);
        } else {
            String regionCodeForCountryCode = getRegionCodeForCountryCode(phonenumber$PhoneNumber.getCountryCode());
            String nddPrefixForRegion = getNddPrefixForRegion(regionCodeForCountryCode, true);
            PhoneNumberFormat phoneNumberFormat = PhoneNumberFormat.NATIONAL;
            format = format(phonenumber$PhoneNumber, phoneNumberFormat);
            if (nddPrefixForRegion != null && nddPrefixForRegion.length() != 0 && !rawInputContainsNationalPrefix(phonenumber$PhoneNumber.getRawInput(), nddPrefixForRegion, regionCodeForCountryCode)) {
                Phonemetadata$NumberFormat chooseFormattingPatternForNumber = chooseFormattingPatternForNumber(getMetadataForRegion(regionCodeForCountryCode).getNumberFormatList(), getNationalSignificantNumber(phonenumber$PhoneNumber));
                if (chooseFormattingPatternForNumber != null && (indexOf = (nationalPrefixFormattingRule = chooseFormattingPatternForNumber.getNationalPrefixFormattingRule()).indexOf("$1")) > 0 && normalizeDigitsOnly(nationalPrefixFormattingRule.substring(0, indexOf)).length() != 0) {
                    Phonemetadata$NumberFormat.Builder newBuilder = Phonemetadata$NumberFormat.newBuilder();
                    newBuilder.mergeFrom(chooseFormattingPatternForNumber);
                    newBuilder.clearNationalPrefixFormattingRule();
                    ArrayList arrayList = new ArrayList(1);
                    arrayList.add(newBuilder.build());
                    format = formatByPattern(phonenumber$PhoneNumber, phoneNumberFormat, arrayList);
                }
            }
        }
        String rawInput = phonenumber$PhoneNumber.getRawInput();
        return (format == null || rawInput.length() <= 0 || normalizeDiallableCharsOnly(format).equals(normalizeDiallableCharsOnly(rawInput))) ? format : rawInput;
    }

    private boolean rawInputContainsNationalPrefix(String str, String str2, String str3) {
        String normalizeDigitsOnly = normalizeDigitsOnly(str);
        if (normalizeDigitsOnly.startsWith(str2)) {
            try {
                return isValidNumber(parse(normalizeDigitsOnly.substring(str2.length()), str3));
            } catch (NumberParseException unused) {
            }
        }
        return false;
    }

    private boolean hasFormattingPatternForNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        Phonemetadata$PhoneMetadata metadataForRegionOrCallingCode = getMetadataForRegionOrCallingCode(countryCode, getRegionCodeForCountryCode(countryCode));
        if (metadataForRegionOrCallingCode == null) {
            return false;
        }
        return chooseFormattingPatternForNumber(metadataForRegionOrCallingCode.getNumberFormatList(), getNationalSignificantNumber(phonenumber$PhoneNumber)) != null;
    }

    public String formatOutOfCountryKeepingAlphaChars(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        String str2;
        int indexOf;
        String rawInput = phonenumber$PhoneNumber.getRawInput();
        if (rawInput.length() == 0) {
            return formatOutOfCountryCallingNumber(phonenumber$PhoneNumber, str);
        }
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        if (hasValidCountryCallingCode(countryCode)) {
            String normalizeHelper = normalizeHelper(rawInput, ALL_PLUS_NUMBER_GROUPING_SYMBOLS, true);
            String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
            if (nationalSignificantNumber.length() > 3 && (indexOf = normalizeHelper.indexOf(nationalSignificantNumber.substring(0, 3))) != -1) {
                normalizeHelper = normalizeHelper.substring(indexOf);
            }
            Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
            if (countryCode == 1) {
                if (isNANPACountry(str)) {
                    return countryCode + " " + normalizeHelper;
                }
            } else if (metadataForRegion != null && countryCode == getCountryCodeForValidRegion(str)) {
                Phonemetadata$NumberFormat chooseFormattingPatternForNumber = chooseFormattingPatternForNumber(metadataForRegion.getNumberFormatList(), nationalSignificantNumber);
                if (chooseFormattingPatternForNumber == null) {
                    return normalizeHelper;
                }
                Phonemetadata$NumberFormat.Builder newBuilder = Phonemetadata$NumberFormat.newBuilder();
                newBuilder.mergeFrom(chooseFormattingPatternForNumber);
                newBuilder.setPattern("(\\d+)(.*)");
                newBuilder.setFormat("$1$2");
                return formatNsnUsingPattern(normalizeHelper, newBuilder.build(), PhoneNumberFormat.NATIONAL);
            }
            if (metadataForRegion != null) {
                str2 = metadataForRegion.getInternationalPrefix();
                if (!SINGLE_INTERNATIONAL_PREFIX.matcher(str2).matches()) {
                    str2 = metadataForRegion.getPreferredInternationalPrefix();
                }
            } else {
                str2 = PhoneConfigurationManager.SSSS;
            }
            StringBuilder sb = new StringBuilder(normalizeHelper);
            Phonemetadata$PhoneMetadata metadataForRegionOrCallingCode = getMetadataForRegionOrCallingCode(countryCode, getRegionCodeForCountryCode(countryCode));
            PhoneNumberFormat phoneNumberFormat = PhoneNumberFormat.INTERNATIONAL;
            maybeAppendFormattedExtension(phonenumber$PhoneNumber, metadataForRegionOrCallingCode, phoneNumberFormat, sb);
            if (str2.length() > 0) {
                sb.insert(0, " ").insert(0, countryCode).insert(0, " ").insert(0, str2);
            } else {
                if (!isValidRegionCode(str)) {
                    Logger logger2 = logger;
                    Level level = Level.WARNING;
                    logger2.log(level, "Trying to format number from invalid region " + str + ". International formatting applied.");
                }
                prefixNumberWithCountryCallingCode(countryCode, phoneNumberFormat, sb);
            }
            return sb.toString();
        }
        return rawInput;
    }

    public String getNationalSignificantNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        StringBuilder sb = new StringBuilder();
        if (phonenumber$PhoneNumber.isItalianLeadingZero() && phonenumber$PhoneNumber.getNumberOfLeadingZeros() > 0) {
            char[] cArr = new char[phonenumber$PhoneNumber.getNumberOfLeadingZeros()];
            Arrays.fill(cArr, '0');
            sb.append(new String(cArr));
        }
        sb.append(phonenumber$PhoneNumber.getNationalNumber());
        return sb.toString();
    }

    private void prefixNumberWithCountryCallingCode(int i, PhoneNumberFormat phoneNumberFormat, StringBuilder sb) {
        int i2 = C02692.f18xd187ceb9[phoneNumberFormat.ordinal()];
        if (i2 == 1) {
            sb.insert(0, i).insert(0, '+');
        } else if (i2 == 2) {
            sb.insert(0, " ").insert(0, i).insert(0, '+');
        } else if (i2 != 3) {
        } else {
            sb.insert(0, "-").insert(0, i).insert(0, '+').insert(0, "tel:");
        }
    }

    private String formatNsn(String str, Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata, PhoneNumberFormat phoneNumberFormat) {
        return formatNsn(str, phonemetadata$PhoneMetadata, phoneNumberFormat, null);
    }

    private String formatNsn(String str, Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata, PhoneNumberFormat phoneNumberFormat, CharSequence charSequence) {
        List<Phonemetadata$NumberFormat> numberFormatList;
        if (phonemetadata$PhoneMetadata.getIntlNumberFormatList().size() == 0 || phoneNumberFormat == PhoneNumberFormat.NATIONAL) {
            numberFormatList = phonemetadata$PhoneMetadata.getNumberFormatList();
        } else {
            numberFormatList = phonemetadata$PhoneMetadata.getIntlNumberFormatList();
        }
        Phonemetadata$NumberFormat chooseFormattingPatternForNumber = chooseFormattingPatternForNumber(numberFormatList, str);
        return chooseFormattingPatternForNumber == null ? str : formatNsnUsingPattern(str, chooseFormattingPatternForNumber, phoneNumberFormat, charSequence);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata$NumberFormat chooseFormattingPatternForNumber(List<Phonemetadata$NumberFormat> list, String str) {
        for (Phonemetadata$NumberFormat phonemetadata$NumberFormat : list) {
            int leadingDigitsPatternCount = phonemetadata$NumberFormat.getLeadingDigitsPatternCount();
            if (leadingDigitsPatternCount == 0 || this.regexCache.getPatternForRegex(phonemetadata$NumberFormat.getLeadingDigitsPattern(leadingDigitsPatternCount - 1)).matcher(str).lookingAt()) {
                if (this.regexCache.getPatternForRegex(phonemetadata$NumberFormat.getPattern()).matcher(str).matches()) {
                    return phonemetadata$NumberFormat;
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String formatNsnUsingPattern(String str, Phonemetadata$NumberFormat phonemetadata$NumberFormat, PhoneNumberFormat phoneNumberFormat) {
        return formatNsnUsingPattern(str, phonemetadata$NumberFormat, phoneNumberFormat, null);
    }

    private String formatNsnUsingPattern(String str, Phonemetadata$NumberFormat phonemetadata$NumberFormat, PhoneNumberFormat phoneNumberFormat, CharSequence charSequence) {
        String replaceAll;
        String format = phonemetadata$NumberFormat.getFormat();
        Matcher matcher = this.regexCache.getPatternForRegex(phonemetadata$NumberFormat.getPattern()).matcher(str);
        PhoneNumberFormat phoneNumberFormat2 = PhoneNumberFormat.NATIONAL;
        if (phoneNumberFormat == phoneNumberFormat2 && charSequence != null && charSequence.length() > 0 && phonemetadata$NumberFormat.getDomesticCarrierCodeFormattingRule().length() > 0) {
            replaceAll = matcher.replaceAll(FIRST_GROUP_PATTERN.matcher(format).replaceFirst(phonemetadata$NumberFormat.getDomesticCarrierCodeFormattingRule().replace("$CC", charSequence)));
        } else {
            String nationalPrefixFormattingRule = phonemetadata$NumberFormat.getNationalPrefixFormattingRule();
            if (phoneNumberFormat == phoneNumberFormat2 && nationalPrefixFormattingRule != null && nationalPrefixFormattingRule.length() > 0) {
                replaceAll = matcher.replaceAll(FIRST_GROUP_PATTERN.matcher(format).replaceFirst(nationalPrefixFormattingRule));
            } else {
                replaceAll = matcher.replaceAll(format);
            }
        }
        if (phoneNumberFormat == PhoneNumberFormat.RFC3966) {
            Matcher matcher2 = SEPARATOR_PATTERN.matcher(replaceAll);
            if (matcher2.lookingAt()) {
                replaceAll = matcher2.replaceFirst(PhoneConfigurationManager.SSSS);
            }
            return matcher2.reset(replaceAll).replaceAll("-");
        }
        return replaceAll;
    }

    public Phonenumber$PhoneNumber getExampleNumber(String str) {
        return getExampleNumberForType(str, PhoneNumberType.FIXED_LINE);
    }

    /*  JADX ERROR: NullPointerException in pass: BlockProcessor
        java.lang.NullPointerException
        	at jadx.core.dex.visitors.blocks.BlockSplitter.connect(BlockSplitter.java:150)
        	at jadx.core.dex.visitors.blocks.BlockExceptionHandler.connectSplittersAndHandlers(BlockExceptionHandler.java:457)
        	at jadx.core.dex.visitors.blocks.BlockExceptionHandler.wrapBlocksWithTryCatch(BlockExceptionHandler.java:358)
        	at jadx.core.dex.visitors.blocks.BlockExceptionHandler.connectExcHandlers(BlockExceptionHandler.java:84)
        	at jadx.core.dex.visitors.blocks.BlockExceptionHandler.process(BlockExceptionHandler.java:59)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.independentBlockTreeMod(BlockProcessor.java:318)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:46)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    public com.android.internal.telephony.phonenumbers.Phonenumber$PhoneNumber getInvalidExampleNumber(java.lang.String r6) {
        /*
            r5 = this;
            boolean r0 = r5.isValidRegionCode(r6)
            r1 = 0
            if (r0 != 0) goto L20
            java.util.logging.Logger r5 = com.android.internal.telephony.phonenumbers.PhoneNumberUtil.logger
            java.util.logging.Level r0 = java.util.logging.Level.WARNING
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Invalid or unknown region code provided: "
            r2.append(r3)
            r2.append(r6)
            java.lang.String r6 = r2.toString()
            r5.log(r0, r6)
            return r1
        L20:
            com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata r0 = r5.getMetadataForRegion(r6)
            com.android.internal.telephony.phonenumbers.PhoneNumberUtil$PhoneNumberType r2 = com.android.internal.telephony.phonenumbers.PhoneNumberUtil.PhoneNumberType.FIXED_LINE
            com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneNumberDesc r0 = r5.getNumberDescByType(r0, r2)
            boolean r2 = r0.hasExampleNumber()
            if (r2 != 0) goto L31
            return r1
        L31:
            java.lang.String r0 = r0.getExampleNumber()
            int r2 = r0.length()
            int r2 = r2 + (-1)
        L3b:
            r3 = 2
            if (r2 < r3) goto L51
            r3 = 0
            java.lang.String r3 = r0.substring(r3, r2)
            com.android.internal.telephony.phonenumbers.Phonenumber$PhoneNumber r3 = r5.parse(r3, r6)     // Catch: com.android.internal.telephony.phonenumbers.NumberParseException -> L4e
            boolean r4 = r5.isValidNumber(r3)     // Catch: com.android.internal.telephony.phonenumbers.NumberParseException -> L4e
            if (r4 != 0) goto L4e
            return r3
        L4e:
            int r2 = r2 + (-1)
            goto L3b
        L51:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.phonenumbers.PhoneNumberUtil.getInvalidExampleNumber(java.lang.String):com.android.internal.telephony.phonenumbers.Phonenumber$PhoneNumber");
    }

    public Phonenumber$PhoneNumber getExampleNumberForType(String str, PhoneNumberType phoneNumberType) {
        if (!isValidRegionCode(str)) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            logger2.log(level, "Invalid or unknown region code provided: " + str);
            return null;
        }
        Phonemetadata$PhoneNumberDesc numberDescByType = getNumberDescByType(getMetadataForRegion(str), phoneNumberType);
        try {
            if (numberDescByType.hasExampleNumber()) {
                return parse(numberDescByType.getExampleNumber(), str);
            }
        } catch (NumberParseException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return null;
    }

    public Phonenumber$PhoneNumber getExampleNumberForType(PhoneNumberType phoneNumberType) {
        for (String str : getSupportedRegions()) {
            Phonenumber$PhoneNumber exampleNumberForType = getExampleNumberForType(str, phoneNumberType);
            if (exampleNumberForType != null) {
                return exampleNumberForType;
            }
        }
        for (Integer num : getSupportedGlobalNetworkCallingCodes()) {
            int intValue = num.intValue();
            Phonemetadata$PhoneNumberDesc numberDescByType = this.getNumberDescByType(this.getMetadataForNonGeographicalRegion(intValue), phoneNumberType);
            try {
            } catch (NumberParseException e) {
                logger.log(Level.SEVERE, e.toString());
            }
            if (numberDescByType.hasExampleNumber()) {
                return this.parse("+" + intValue + numberDescByType.getExampleNumber(), "ZZ");
            }
            continue;
        }
        return null;
    }

    public Phonenumber$PhoneNumber getExampleNumberForNonGeoEntity(int i) {
        Phonemetadata$PhoneMetadata metadataForNonGeographicalRegion = getMetadataForNonGeographicalRegion(i);
        if (metadataForNonGeographicalRegion != null) {
            for (Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc : Arrays.asList(metadataForNonGeographicalRegion.getMobile(), metadataForNonGeographicalRegion.getTollFree(), metadataForNonGeographicalRegion.getSharedCost(), metadataForNonGeographicalRegion.getVoip(), metadataForNonGeographicalRegion.getVoicemail(), metadataForNonGeographicalRegion.getUan(), metadataForNonGeographicalRegion.getPremiumRate())) {
                if (phonemetadata$PhoneNumberDesc != null) {
                    try {
                        if (phonemetadata$PhoneNumberDesc.hasExampleNumber()) {
                            return this.parse("+" + i + phonemetadata$PhoneNumberDesc.getExampleNumber(), "ZZ");
                        }
                        continue;
                    } catch (NumberParseException e) {
                        logger.log(Level.SEVERE, e.toString());
                    }
                }
            }
            return null;
        }
        Logger logger2 = logger;
        Level level = Level.WARNING;
        logger2.log(level, "Invalid or unknown country calling code provided: " + i);
        return null;
    }

    private void maybeAppendFormattedExtension(Phonenumber$PhoneNumber phonenumber$PhoneNumber, Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata, PhoneNumberFormat phoneNumberFormat, StringBuilder sb) {
        if (!phonenumber$PhoneNumber.hasExtension() || phonenumber$PhoneNumber.getExtension().length() <= 0) {
            return;
        }
        if (phoneNumberFormat == PhoneNumberFormat.RFC3966) {
            sb.append(";ext=");
            sb.append(phonenumber$PhoneNumber.getExtension());
        } else if (phonemetadata$PhoneMetadata.hasPreferredExtnPrefix()) {
            sb.append(phonemetadata$PhoneMetadata.getPreferredExtnPrefix());
            sb.append(phonenumber$PhoneNumber.getExtension());
        } else {
            sb.append(" ext. ");
            sb.append(phonenumber$PhoneNumber.getExtension());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.phonenumbers.PhoneNumberUtil$2 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C02692 {

        /* renamed from: $SwitchMap$com$google$i18n$phonenumbers$PhoneNumberUtil$PhoneNumberFormat */
        static final /* synthetic */ int[] f18xd187ceb9;

        /* renamed from: $SwitchMap$com$google$i18n$phonenumbers$PhoneNumberUtil$PhoneNumberType */
        static final /* synthetic */ int[] f19xa7892e7c;

        /* renamed from: $SwitchMap$com$google$i18n$phonenumbers$Phonenumber$PhoneNumber$CountryCodeSource */
        static final /* synthetic */ int[] f20x9de98e3a;

        static {
            int[] iArr = new int[PhoneNumberType.values().length];
            f19xa7892e7c = iArr;
            try {
                iArr[PhoneNumberType.PREMIUM_RATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.TOLL_FREE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.MOBILE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.FIXED_LINE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.FIXED_LINE_OR_MOBILE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.SHARED_COST.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.VOIP.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.PERSONAL_NUMBER.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.PAGER.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.UAN.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                f19xa7892e7c[PhoneNumberType.VOICEMAIL.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            int[] iArr2 = new int[PhoneNumberFormat.values().length];
            f18xd187ceb9 = iArr2;
            try {
                iArr2[PhoneNumberFormat.E164.ordinal()] = 1;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                f18xd187ceb9[PhoneNumberFormat.INTERNATIONAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                f18xd187ceb9[PhoneNumberFormat.RFC3966.ordinal()] = 3;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                f18xd187ceb9[PhoneNumberFormat.NATIONAL.ordinal()] = 4;
            } catch (NoSuchFieldError unused15) {
            }
            int[] iArr3 = new int[Phonenumber$PhoneNumber.CountryCodeSource.values().length];
            f20x9de98e3a = iArr3;
            try {
                iArr3[Phonenumber$PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN.ordinal()] = 1;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                f20x9de98e3a[Phonenumber$PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_IDD.ordinal()] = 2;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                f20x9de98e3a[Phonenumber$PhoneNumber.CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN.ordinal()] = 3;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                f20x9de98e3a[Phonenumber$PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY.ordinal()] = 4;
            } catch (NoSuchFieldError unused19) {
            }
        }
    }

    Phonemetadata$PhoneNumberDesc getNumberDescByType(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata, PhoneNumberType phoneNumberType) {
        switch (C02692.f19xa7892e7c[phoneNumberType.ordinal()]) {
            case 1:
                return phonemetadata$PhoneMetadata.getPremiumRate();
            case 2:
                return phonemetadata$PhoneMetadata.getTollFree();
            case 3:
                return phonemetadata$PhoneMetadata.getMobile();
            case 4:
            case 5:
                return phonemetadata$PhoneMetadata.getFixedLine();
            case 6:
                return phonemetadata$PhoneMetadata.getSharedCost();
            case 7:
                return phonemetadata$PhoneMetadata.getVoip();
            case 8:
                return phonemetadata$PhoneMetadata.getPersonalNumber();
            case 9:
                return phonemetadata$PhoneMetadata.getPager();
            case 10:
                return phonemetadata$PhoneMetadata.getUan();
            case 11:
                return phonemetadata$PhoneMetadata.getVoicemail();
            default:
                return phonemetadata$PhoneMetadata.getGeneralDesc();
        }
    }

    public PhoneNumberType getNumberType(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        Phonemetadata$PhoneMetadata metadataForRegionOrCallingCode = getMetadataForRegionOrCallingCode(phonenumber$PhoneNumber.getCountryCode(), getRegionCodeForNumber(phonenumber$PhoneNumber));
        if (metadataForRegionOrCallingCode == null) {
            return PhoneNumberType.UNKNOWN;
        }
        return getNumberTypeHelper(getNationalSignificantNumber(phonenumber$PhoneNumber), metadataForRegionOrCallingCode);
    }

    private PhoneNumberType getNumberTypeHelper(String str, Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata) {
        if (!isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getGeneralDesc())) {
            return PhoneNumberType.UNKNOWN;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getPremiumRate())) {
            return PhoneNumberType.PREMIUM_RATE;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getTollFree())) {
            return PhoneNumberType.TOLL_FREE;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getSharedCost())) {
            return PhoneNumberType.SHARED_COST;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getVoip())) {
            return PhoneNumberType.VOIP;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getPersonalNumber())) {
            return PhoneNumberType.PERSONAL_NUMBER;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getPager())) {
            return PhoneNumberType.PAGER;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getUan())) {
            return PhoneNumberType.UAN;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getVoicemail())) {
            return PhoneNumberType.VOICEMAIL;
        }
        if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getFixedLine())) {
            if (phonemetadata$PhoneMetadata.getSameMobileAndFixedLinePattern()) {
                return PhoneNumberType.FIXED_LINE_OR_MOBILE;
            }
            if (isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getMobile())) {
                return PhoneNumberType.FIXED_LINE_OR_MOBILE;
            }
            return PhoneNumberType.FIXED_LINE;
        } else if (!phonemetadata$PhoneMetadata.getSameMobileAndFixedLinePattern() && isNumberMatchingDesc(str, phonemetadata$PhoneMetadata.getMobile())) {
            return PhoneNumberType.MOBILE;
        } else {
            return PhoneNumberType.UNKNOWN;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata$PhoneMetadata getMetadataForRegion(String str) {
        if (isValidRegionCode(str)) {
            Phonemetadata$PhoneMetadata metadataForRegion = this.metadataSource.getMetadataForRegion(str);
            ensureMetadataIsNonNull(metadataForRegion, "Missing metadata for region code " + str);
            return metadataForRegion;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Phonemetadata$PhoneMetadata getMetadataForNonGeographicalRegion(int i) {
        if (this.countryCodesForNonGeographicalRegion.contains(Integer.valueOf(i))) {
            Phonemetadata$PhoneMetadata metadataForNonGeographicalRegion = this.metadataSource.getMetadataForNonGeographicalRegion(i);
            ensureMetadataIsNonNull(metadataForNonGeographicalRegion, "Missing metadata for country code " + i);
            return metadataForNonGeographicalRegion;
        }
        return null;
    }

    private static void ensureMetadataIsNonNull(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata, String str) {
        if (phonemetadata$PhoneMetadata == null) {
            throw new MissingMetadataException(str);
        }
    }

    boolean isNumberMatchingDesc(String str, Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        int length = str.length();
        List<Integer> possibleLengthList = phonemetadata$PhoneNumberDesc.getPossibleLengthList();
        if (possibleLengthList.size() <= 0 || possibleLengthList.contains(Integer.valueOf(length))) {
            return this.matcherApi.matchNationalNumber(str, phonemetadata$PhoneNumberDesc, false);
        }
        return false;
    }

    public boolean isValidNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        return isValidNumberForRegion(phonenumber$PhoneNumber, getRegionCodeForNumber(phonenumber$PhoneNumber));
    }

    public boolean isValidNumberForRegion(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        Phonemetadata$PhoneMetadata metadataForRegionOrCallingCode = getMetadataForRegionOrCallingCode(countryCode, str);
        if (metadataForRegionOrCallingCode != null) {
            return ("001".equals(str) || countryCode == getCountryCodeForValidRegion(str)) && getNumberTypeHelper(getNationalSignificantNumber(phonenumber$PhoneNumber), metadataForRegionOrCallingCode) != PhoneNumberType.UNKNOWN;
        }
        return false;
    }

    public String getRegionCodeForNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        List<String> list = this.countryCallingCodeToRegionCodeMap.get(Integer.valueOf(countryCode));
        if (list == null) {
            Logger logger2 = logger;
            Level level = Level.INFO;
            logger2.log(level, "Missing/invalid country_code (" + countryCode + ")");
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            return getRegionCodeForNumberFromRegionList(phonenumber$PhoneNumber, list);
        }
    }

    private String getRegionCodeForNumberFromRegionList(Phonenumber$PhoneNumber phonenumber$PhoneNumber, List<String> list) {
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        for (String str : list) {
            Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
            if (metadataForRegion.hasLeadingDigits()) {
                if (this.regexCache.getPatternForRegex(metadataForRegion.getLeadingDigits()).matcher(nationalSignificantNumber).lookingAt()) {
                    return str;
                }
            } else if (getNumberTypeHelper(nationalSignificantNumber, metadataForRegion) != PhoneNumberType.UNKNOWN) {
                return str;
            }
        }
        return null;
    }

    public String getRegionCodeForCountryCode(int i) {
        List<String> list = this.countryCallingCodeToRegionCodeMap.get(Integer.valueOf(i));
        return list == null ? "ZZ" : list.get(0);
    }

    public List<String> getRegionCodesForCountryCode(int i) {
        List<String> list = this.countryCallingCodeToRegionCodeMap.get(Integer.valueOf(i));
        if (list == null) {
            list = new ArrayList<>(0);
        }
        return Collections.unmodifiableList(list);
    }

    public int getCountryCodeForRegion(String str) {
        if (!isValidRegionCode(str)) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid or missing region code (");
            if (str == null) {
                str = "null";
            }
            sb.append(str);
            sb.append(") provided.");
            logger2.log(level, sb.toString());
            return 0;
        }
        return getCountryCodeForValidRegion(str);
    }

    private int getCountryCodeForValidRegion(String str) {
        Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
        if (metadataForRegion == null) {
            throw new IllegalArgumentException("Invalid region code: " + str);
        }
        return metadataForRegion.getCountryCode();
    }

    public String getNddPrefixForRegion(String str, boolean z) {
        Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
        if (metadataForRegion == null) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid or missing region code (");
            if (str == null) {
                str = "null";
            }
            sb.append(str);
            sb.append(") provided.");
            logger2.log(level, sb.toString());
            return null;
        }
        String nationalPrefix = metadataForRegion.getNationalPrefix();
        if (nationalPrefix.length() == 0) {
            return null;
        }
        return z ? nationalPrefix.replace("~", PhoneConfigurationManager.SSSS) : nationalPrefix;
    }

    public boolean isNANPACountry(String str) {
        return this.nanpaRegions.contains(str);
    }

    public boolean isAlphaNumber(CharSequence charSequence) {
        if (isViablePhoneNumber(charSequence)) {
            StringBuilder sb = new StringBuilder(charSequence);
            maybeStripExtension(sb);
            return VALID_ALPHA_PHONE_PATTERN.matcher(sb).matches();
        }
        return false;
    }

    public boolean isPossibleNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        ValidationResult isPossibleNumberWithReason = isPossibleNumberWithReason(phonenumber$PhoneNumber);
        return isPossibleNumberWithReason == ValidationResult.IS_POSSIBLE || isPossibleNumberWithReason == ValidationResult.IS_POSSIBLE_LOCAL_ONLY;
    }

    public boolean isPossibleNumberForType(Phonenumber$PhoneNumber phonenumber$PhoneNumber, PhoneNumberType phoneNumberType) {
        ValidationResult isPossibleNumberForTypeWithReason = isPossibleNumberForTypeWithReason(phonenumber$PhoneNumber, phoneNumberType);
        return isPossibleNumberForTypeWithReason == ValidationResult.IS_POSSIBLE || isPossibleNumberForTypeWithReason == ValidationResult.IS_POSSIBLE_LOCAL_ONLY;
    }

    private ValidationResult testNumberLength(CharSequence charSequence, Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata) {
        return testNumberLength(charSequence, phonemetadata$PhoneMetadata, PhoneNumberType.UNKNOWN);
    }

    private ValidationResult testNumberLength(CharSequence charSequence, Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata, PhoneNumberType phoneNumberType) {
        List<Integer> possibleLengthList;
        Phonemetadata$PhoneNumberDesc numberDescByType = getNumberDescByType(phonemetadata$PhoneMetadata, phoneNumberType);
        ArrayList possibleLengthList2 = numberDescByType.getPossibleLengthList().isEmpty() ? phonemetadata$PhoneMetadata.getGeneralDesc().getPossibleLengthList() : numberDescByType.getPossibleLengthList();
        List<Integer> possibleLengthLocalOnlyList = numberDescByType.getPossibleLengthLocalOnlyList();
        if (phoneNumberType == PhoneNumberType.FIXED_LINE_OR_MOBILE) {
            if (!descHasPossibleNumberData(getNumberDescByType(phonemetadata$PhoneMetadata, PhoneNumberType.FIXED_LINE))) {
                return testNumberLength(charSequence, phonemetadata$PhoneMetadata, PhoneNumberType.MOBILE);
            }
            Phonemetadata$PhoneNumberDesc numberDescByType2 = getNumberDescByType(phonemetadata$PhoneMetadata, PhoneNumberType.MOBILE);
            if (descHasPossibleNumberData(numberDescByType2)) {
                ArrayList arrayList = new ArrayList(possibleLengthList2);
                if (numberDescByType2.getPossibleLengthCount() == 0) {
                    possibleLengthList = phonemetadata$PhoneMetadata.getGeneralDesc().getPossibleLengthList();
                } else {
                    possibleLengthList = numberDescByType2.getPossibleLengthList();
                }
                arrayList.addAll(possibleLengthList);
                Collections.sort(arrayList);
                if (possibleLengthLocalOnlyList.isEmpty()) {
                    possibleLengthLocalOnlyList = numberDescByType2.getPossibleLengthLocalOnlyList();
                } else {
                    ArrayList arrayList2 = new ArrayList(possibleLengthLocalOnlyList);
                    arrayList2.addAll(numberDescByType2.getPossibleLengthLocalOnlyList());
                    Collections.sort(arrayList2);
                    possibleLengthLocalOnlyList = arrayList2;
                }
                possibleLengthList2 = arrayList;
            }
        }
        if (possibleLengthList2.get(0).intValue() == -1) {
            return ValidationResult.INVALID_LENGTH;
        }
        int length = charSequence.length();
        if (possibleLengthLocalOnlyList.contains(Integer.valueOf(length))) {
            return ValidationResult.IS_POSSIBLE_LOCAL_ONLY;
        }
        int intValue = possibleLengthList2.get(0).intValue();
        if (intValue == length) {
            return ValidationResult.IS_POSSIBLE;
        }
        if (intValue > length) {
            return ValidationResult.TOO_SHORT;
        }
        if (possibleLengthList2.get(possibleLengthList2.size() - 1).intValue() < length) {
            return ValidationResult.TOO_LONG;
        }
        return possibleLengthList2.subList(1, possibleLengthList2.size()).contains(Integer.valueOf(length)) ? ValidationResult.IS_POSSIBLE : ValidationResult.INVALID_LENGTH;
    }

    public ValidationResult isPossibleNumberWithReason(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        return isPossibleNumberForTypeWithReason(phonenumber$PhoneNumber, PhoneNumberType.UNKNOWN);
    }

    public ValidationResult isPossibleNumberForTypeWithReason(Phonenumber$PhoneNumber phonenumber$PhoneNumber, PhoneNumberType phoneNumberType) {
        String nationalSignificantNumber = getNationalSignificantNumber(phonenumber$PhoneNumber);
        int countryCode = phonenumber$PhoneNumber.getCountryCode();
        if (!hasValidCountryCallingCode(countryCode)) {
            return ValidationResult.INVALID_COUNTRY_CODE;
        }
        return testNumberLength(nationalSignificantNumber, getMetadataForRegionOrCallingCode(countryCode, getRegionCodeForCountryCode(countryCode)), phoneNumberType);
    }

    public boolean isPossibleNumber(CharSequence charSequence, String str) {
        try {
            return isPossibleNumber(parse(charSequence, str));
        } catch (NumberParseException unused) {
            return false;
        }
    }

    public boolean truncateTooLongNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        if (isValidNumber(phonenumber$PhoneNumber)) {
            return true;
        }
        Phonenumber$PhoneNumber phonenumber$PhoneNumber2 = new Phonenumber$PhoneNumber();
        phonenumber$PhoneNumber2.mergeFrom(phonenumber$PhoneNumber);
        long nationalNumber = phonenumber$PhoneNumber.getNationalNumber();
        do {
            nationalNumber /= 10;
            phonenumber$PhoneNumber2.setNationalNumber(nationalNumber);
            if (isPossibleNumberWithReason(phonenumber$PhoneNumber2) == ValidationResult.TOO_SHORT || nationalNumber == 0) {
                return false;
            }
        } while (!isValidNumber(phonenumber$PhoneNumber2));
        phonenumber$PhoneNumber.setNationalNumber(nationalNumber);
        return true;
    }

    public AsYouTypeFormatter getAsYouTypeFormatter(String str) {
        return new AsYouTypeFormatter(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int extractCountryCode(StringBuilder sb, StringBuilder sb2) {
        if (sb.length() != 0 && sb.charAt(0) != '0') {
            int length = sb.length();
            for (int i = 1; i <= 3 && i <= length; i++) {
                int parseInt = Integer.parseInt(sb.substring(0, i));
                if (this.countryCallingCodeToRegionCodeMap.containsKey(Integer.valueOf(parseInt))) {
                    sb2.append(sb.substring(i));
                    return parseInt;
                }
            }
        }
        return 0;
    }

    int maybeExtractCountryCode(CharSequence charSequence, Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata, StringBuilder sb, boolean z, Phonenumber$PhoneNumber phonenumber$PhoneNumber) throws NumberParseException {
        if (charSequence.length() == 0) {
            return 0;
        }
        StringBuilder sb2 = new StringBuilder(charSequence);
        Phonenumber$PhoneNumber.CountryCodeSource maybeStripInternationalPrefixAndNormalize = maybeStripInternationalPrefixAndNormalize(sb2, phonemetadata$PhoneMetadata != null ? phonemetadata$PhoneMetadata.getInternationalPrefix() : "NonMatch");
        if (z) {
            phonenumber$PhoneNumber.setCountryCodeSource(maybeStripInternationalPrefixAndNormalize);
        }
        if (maybeStripInternationalPrefixAndNormalize != Phonenumber$PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY) {
            if (sb2.length() <= 2) {
                throw new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_AFTER_IDD, "Phone number had an IDD, but after this was not long enough to be a viable phone number.");
            }
            int extractCountryCode = extractCountryCode(sb2, sb);
            if (extractCountryCode != 0) {
                phonenumber$PhoneNumber.setCountryCode(extractCountryCode);
                return extractCountryCode;
            }
            throw new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE, "Country calling code supplied was not recognised.");
        }
        if (phonemetadata$PhoneMetadata != null) {
            int countryCode = phonemetadata$PhoneMetadata.getCountryCode();
            String valueOf = String.valueOf(countryCode);
            String sb3 = sb2.toString();
            if (sb3.startsWith(valueOf)) {
                StringBuilder sb4 = new StringBuilder(sb3.substring(valueOf.length()));
                Phonemetadata$PhoneNumberDesc generalDesc = phonemetadata$PhoneMetadata.getGeneralDesc();
                maybeStripNationalPrefixAndCarrierCode(sb4, phonemetadata$PhoneMetadata, null);
                if ((!this.matcherApi.matchNationalNumber(sb2, generalDesc, false) && this.matcherApi.matchNationalNumber(sb4, generalDesc, false)) || testNumberLength(sb2, phonemetadata$PhoneMetadata) == ValidationResult.TOO_LONG) {
                    sb.append((CharSequence) sb4);
                    if (z) {
                        phonenumber$PhoneNumber.setCountryCodeSource(Phonenumber$PhoneNumber.CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN);
                    }
                    phonenumber$PhoneNumber.setCountryCode(countryCode);
                    return countryCode;
                }
            }
        }
        phonenumber$PhoneNumber.setCountryCode(0);
        return 0;
    }

    private boolean parsePrefixAsIdd(Pattern pattern, StringBuilder sb) {
        Matcher matcher = pattern.matcher(sb);
        if (matcher.lookingAt()) {
            int end = matcher.end();
            Matcher matcher2 = CAPTURING_DIGIT_PATTERN.matcher(sb.substring(end));
            if (matcher2.find() && normalizeDigitsOnly(matcher2.group(1)).equals("0")) {
                return false;
            }
            sb.delete(0, end);
            return true;
        }
        return false;
    }

    Phonenumber$PhoneNumber.CountryCodeSource maybeStripInternationalPrefixAndNormalize(StringBuilder sb, String str) {
        if (sb.length() == 0) {
            return Phonenumber$PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY;
        }
        Matcher matcher = PLUS_CHARS_PATTERN.matcher(sb);
        if (matcher.lookingAt()) {
            sb.delete(0, matcher.end());
            normalize(sb);
            return Phonenumber$PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN;
        }
        Pattern patternForRegex = this.regexCache.getPatternForRegex(str);
        normalize(sb);
        if (parsePrefixAsIdd(patternForRegex, sb)) {
            return Phonenumber$PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_IDD;
        }
        return Phonenumber$PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean maybeStripNationalPrefixAndCarrierCode(StringBuilder sb, Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata, StringBuilder sb2) {
        int length = sb.length();
        String nationalPrefixForParsing = phonemetadata$PhoneMetadata.getNationalPrefixForParsing();
        if (length != 0 && nationalPrefixForParsing.length() != 0) {
            Matcher matcher = this.regexCache.getPatternForRegex(nationalPrefixForParsing).matcher(sb);
            if (matcher.lookingAt()) {
                Phonemetadata$PhoneNumberDesc generalDesc = phonemetadata$PhoneMetadata.getGeneralDesc();
                boolean matchNationalNumber = this.matcherApi.matchNationalNumber(sb, generalDesc, false);
                int groupCount = matcher.groupCount();
                String nationalPrefixTransformRule = phonemetadata$PhoneMetadata.getNationalPrefixTransformRule();
                if (nationalPrefixTransformRule == null || nationalPrefixTransformRule.length() == 0 || matcher.group(groupCount) == null) {
                    if (!matchNationalNumber || this.matcherApi.matchNationalNumber(sb.substring(matcher.end()), generalDesc, false)) {
                        if (sb2 != null && groupCount > 0 && matcher.group(groupCount) != null) {
                            sb2.append(matcher.group(1));
                        }
                        sb.delete(0, matcher.end());
                        return true;
                    }
                    return false;
                }
                StringBuilder sb3 = new StringBuilder(sb);
                sb3.replace(0, length, matcher.replaceFirst(nationalPrefixTransformRule));
                if (!matchNationalNumber || this.matcherApi.matchNationalNumber(sb3.toString(), generalDesc, false)) {
                    if (sb2 != null && groupCount > 1) {
                        sb2.append(matcher.group(1));
                    }
                    sb.replace(0, sb.length(), sb3.toString());
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    String maybeStripExtension(StringBuilder sb) {
        Matcher matcher = EXTN_PATTERN.matcher(sb);
        if (matcher.find() && isViablePhoneNumber(sb.substring(0, matcher.start()))) {
            int groupCount = matcher.groupCount();
            for (int i = 1; i <= groupCount; i++) {
                if (matcher.group(i) != null) {
                    String group = matcher.group(i);
                    sb.delete(matcher.start(), sb.length());
                    return group;
                }
            }
            return PhoneConfigurationManager.SSSS;
        }
        return PhoneConfigurationManager.SSSS;
    }

    private boolean checkRegionForParsing(CharSequence charSequence, String str) {
        if (isValidRegionCode(str)) {
            return true;
        }
        return (charSequence == null || charSequence.length() == 0 || !PLUS_CHARS_PATTERN.matcher(charSequence).lookingAt()) ? false : true;
    }

    public Phonenumber$PhoneNumber parse(CharSequence charSequence, String str) throws NumberParseException {
        Phonenumber$PhoneNumber phonenumber$PhoneNumber = new Phonenumber$PhoneNumber();
        parse(charSequence, str, phonenumber$PhoneNumber);
        return phonenumber$PhoneNumber;
    }

    public void parse(CharSequence charSequence, String str, Phonenumber$PhoneNumber phonenumber$PhoneNumber) throws NumberParseException {
        parseHelper(charSequence, str, false, true, phonenumber$PhoneNumber);
    }

    public Phonenumber$PhoneNumber parseAndKeepRawInput(CharSequence charSequence, String str) throws NumberParseException {
        Phonenumber$PhoneNumber phonenumber$PhoneNumber = new Phonenumber$PhoneNumber();
        parseAndKeepRawInput(charSequence, str, phonenumber$PhoneNumber);
        return phonenumber$PhoneNumber;
    }

    public void parseAndKeepRawInput(CharSequence charSequence, String str, Phonenumber$PhoneNumber phonenumber$PhoneNumber) throws NumberParseException {
        parseHelper(charSequence, str, true, true, phonenumber$PhoneNumber);
    }

    public Iterable<PhoneNumberMatch> findNumbers(CharSequence charSequence, String str) {
        return findNumbers(charSequence, str, Leniency.VALID, Long.MAX_VALUE);
    }

    public Iterable<PhoneNumberMatch> findNumbers(final CharSequence charSequence, final String str, final Leniency leniency, final long j) {
        return new Iterable<PhoneNumberMatch>() { // from class: com.android.internal.telephony.phonenumbers.PhoneNumberUtil.1
            @Override // java.lang.Iterable
            public Iterator<PhoneNumberMatch> iterator() {
                return new PhoneNumberMatcher(PhoneNumberUtil.this, charSequence, str, leniency, j);
            }
        };
    }

    static void setItalianLeadingZerosForPhoneNumber(CharSequence charSequence, Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        if (charSequence.length() <= 1 || charSequence.charAt(0) != '0') {
            return;
        }
        phonenumber$PhoneNumber.setItalianLeadingZero(true);
        int i = 1;
        while (i < charSequence.length() - 1 && charSequence.charAt(i) == '0') {
            i++;
        }
        if (i != 1) {
            phonenumber$PhoneNumber.setNumberOfLeadingZeros(i);
        }
    }

    private void parseHelper(CharSequence charSequence, String str, boolean z, boolean z2, Phonenumber$PhoneNumber phonenumber$PhoneNumber) throws NumberParseException {
        int maybeExtractCountryCode;
        if (charSequence == null) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "The phone number supplied was null.");
        }
        if (charSequence.length() > 250) {
            throw new NumberParseException(NumberParseException.ErrorType.TOO_LONG, "The string supplied was too long to parse.");
        }
        StringBuilder sb = new StringBuilder();
        String charSequence2 = charSequence.toString();
        buildNationalNumberForParsing(charSequence2, sb);
        if (!isViablePhoneNumber(sb)) {
            throw new NumberParseException(NumberParseException.ErrorType.NOT_A_NUMBER, "The string supplied did not seem to be a phone number.");
        }
        if (z2 && !checkRegionForParsing(sb, str)) {
            throw new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE, "Missing or invalid default region.");
        }
        if (z) {
            phonenumber$PhoneNumber.setRawInput(charSequence2);
        }
        String maybeStripExtension = maybeStripExtension(sb);
        if (maybeStripExtension.length() > 0) {
            phonenumber$PhoneNumber.setExtension(maybeStripExtension);
        }
        Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
        StringBuilder sb2 = new StringBuilder();
        try {
            maybeExtractCountryCode = maybeExtractCountryCode(sb, metadataForRegion, sb2, z, phonenumber$PhoneNumber);
        } catch (NumberParseException e) {
            Matcher matcher = PLUS_CHARS_PATTERN.matcher(sb);
            if (e.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE && matcher.lookingAt()) {
                maybeExtractCountryCode = maybeExtractCountryCode(sb.substring(matcher.end()), metadataForRegion, sb2, z, phonenumber$PhoneNumber);
                if (maybeExtractCountryCode == 0) {
                    throw new NumberParseException(NumberParseException.ErrorType.INVALID_COUNTRY_CODE, "Could not interpret numbers after plus-sign.");
                }
            } else {
                throw new NumberParseException(e.getErrorType(), e.getMessage());
            }
        }
        if (maybeExtractCountryCode != 0) {
            String regionCodeForCountryCode = getRegionCodeForCountryCode(maybeExtractCountryCode);
            if (!regionCodeForCountryCode.equals(str)) {
                metadataForRegion = getMetadataForRegionOrCallingCode(maybeExtractCountryCode, regionCodeForCountryCode);
            }
        } else {
            sb2.append(normalize(sb));
            if (str != null) {
                phonenumber$PhoneNumber.setCountryCode(metadataForRegion.getCountryCode());
            } else if (z) {
                phonenumber$PhoneNumber.clearCountryCodeSource();
            }
        }
        if (sb2.length() < 2) {
            throw new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_NSN, "The string supplied is too short to be a phone number.");
        }
        if (metadataForRegion != null) {
            StringBuilder sb3 = new StringBuilder();
            StringBuilder sb4 = new StringBuilder(sb2);
            maybeStripNationalPrefixAndCarrierCode(sb4, metadataForRegion, sb3);
            ValidationResult testNumberLength = testNumberLength(sb4, metadataForRegion);
            if (testNumberLength != ValidationResult.TOO_SHORT && testNumberLength != ValidationResult.IS_POSSIBLE_LOCAL_ONLY && testNumberLength != ValidationResult.INVALID_LENGTH) {
                if (z && sb3.length() > 0) {
                    phonenumber$PhoneNumber.setPreferredDomesticCarrierCode(sb3.toString());
                }
                sb2 = sb4;
            }
        }
        int length = sb2.length();
        if (length < 2) {
            throw new NumberParseException(NumberParseException.ErrorType.TOO_SHORT_NSN, "The string supplied is too short to be a phone number.");
        }
        if (length > 17) {
            throw new NumberParseException(NumberParseException.ErrorType.TOO_LONG, "The string supplied is too long to be a phone number.");
        }
        setItalianLeadingZerosForPhoneNumber(sb2, phonenumber$PhoneNumber);
        phonenumber$PhoneNumber.setNationalNumber(Long.parseLong(sb2.toString()));
    }

    private void buildNationalNumberForParsing(String str, StringBuilder sb) {
        int indexOf = str.indexOf(";phone-context=");
        if (indexOf >= 0) {
            int i = indexOf + 15;
            if (i < str.length() - 1 && str.charAt(i) == '+') {
                int indexOf2 = str.indexOf(59, i);
                if (indexOf2 > 0) {
                    sb.append(str.substring(i, indexOf2));
                } else {
                    sb.append(str.substring(i));
                }
            }
            int indexOf3 = str.indexOf("tel:");
            sb.append(str.substring(indexOf3 >= 0 ? indexOf3 + 4 : 0, indexOf));
        } else {
            sb.append(extractPossibleNumber(str));
        }
        int indexOf4 = sb.indexOf(";isub=");
        if (indexOf4 > 0) {
            sb.delete(indexOf4, sb.length());
        }
    }

    private static Phonenumber$PhoneNumber copyCoreFieldsOnly(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        Phonenumber$PhoneNumber phonenumber$PhoneNumber2 = new Phonenumber$PhoneNumber();
        phonenumber$PhoneNumber2.setCountryCode(phonenumber$PhoneNumber.getCountryCode());
        phonenumber$PhoneNumber2.setNationalNumber(phonenumber$PhoneNumber.getNationalNumber());
        if (phonenumber$PhoneNumber.getExtension().length() > 0) {
            phonenumber$PhoneNumber2.setExtension(phonenumber$PhoneNumber.getExtension());
        }
        if (phonenumber$PhoneNumber.isItalianLeadingZero()) {
            phonenumber$PhoneNumber2.setItalianLeadingZero(true);
            phonenumber$PhoneNumber2.setNumberOfLeadingZeros(phonenumber$PhoneNumber.getNumberOfLeadingZeros());
        }
        return phonenumber$PhoneNumber2;
    }

    public MatchType isNumberMatch(Phonenumber$PhoneNumber phonenumber$PhoneNumber, Phonenumber$PhoneNumber phonenumber$PhoneNumber2) {
        Phonenumber$PhoneNumber copyCoreFieldsOnly = copyCoreFieldsOnly(phonenumber$PhoneNumber);
        Phonenumber$PhoneNumber copyCoreFieldsOnly2 = copyCoreFieldsOnly(phonenumber$PhoneNumber2);
        if (copyCoreFieldsOnly.hasExtension() && copyCoreFieldsOnly2.hasExtension() && !copyCoreFieldsOnly.getExtension().equals(copyCoreFieldsOnly2.getExtension())) {
            return MatchType.NO_MATCH;
        }
        int countryCode = copyCoreFieldsOnly.getCountryCode();
        int countryCode2 = copyCoreFieldsOnly2.getCountryCode();
        if (countryCode != 0 && countryCode2 != 0) {
            if (copyCoreFieldsOnly.exactlySameAs(copyCoreFieldsOnly2)) {
                return MatchType.EXACT_MATCH;
            }
            if (countryCode == countryCode2 && isNationalNumberSuffixOfTheOther(copyCoreFieldsOnly, copyCoreFieldsOnly2)) {
                return MatchType.SHORT_NSN_MATCH;
            }
            return MatchType.NO_MATCH;
        }
        copyCoreFieldsOnly.setCountryCode(countryCode2);
        if (copyCoreFieldsOnly.exactlySameAs(copyCoreFieldsOnly2)) {
            return MatchType.NSN_MATCH;
        }
        if (isNationalNumberSuffixOfTheOther(copyCoreFieldsOnly, copyCoreFieldsOnly2)) {
            return MatchType.SHORT_NSN_MATCH;
        }
        return MatchType.NO_MATCH;
    }

    private boolean isNationalNumberSuffixOfTheOther(Phonenumber$PhoneNumber phonenumber$PhoneNumber, Phonenumber$PhoneNumber phonenumber$PhoneNumber2) {
        String valueOf = String.valueOf(phonenumber$PhoneNumber.getNationalNumber());
        String valueOf2 = String.valueOf(phonenumber$PhoneNumber2.getNationalNumber());
        return valueOf.endsWith(valueOf2) || valueOf2.endsWith(valueOf);
    }

    public MatchType isNumberMatch(CharSequence charSequence, CharSequence charSequence2) {
        try {
            return isNumberMatch(parse(charSequence, "ZZ"), charSequence2);
        } catch (NumberParseException e) {
            if (e.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE) {
                try {
                    return this.isNumberMatch(this.parse(charSequence2, "ZZ"), charSequence);
                } catch (NumberParseException e2) {
                    if (e2.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE) {
                        try {
                            Phonenumber$PhoneNumber phonenumber$PhoneNumber = new Phonenumber$PhoneNumber();
                            Phonenumber$PhoneNumber phonenumber$PhoneNumber2 = new Phonenumber$PhoneNumber();
                            this.parseHelper(charSequence, null, false, false, phonenumber$PhoneNumber);
                            this.parseHelper(charSequence2, null, false, false, phonenumber$PhoneNumber2);
                            return this.isNumberMatch(phonenumber$PhoneNumber, phonenumber$PhoneNumber2);
                        } catch (NumberParseException unused) {
                            return MatchType.NOT_A_NUMBER;
                        }
                    }
                    return MatchType.NOT_A_NUMBER;
                }
            }
            return MatchType.NOT_A_NUMBER;
        }
    }

    public MatchType isNumberMatch(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence) {
        try {
            return isNumberMatch(phonenumber$PhoneNumber, parse(charSequence, "ZZ"));
        } catch (NumberParseException e) {
            if (e.getErrorType() == NumberParseException.ErrorType.INVALID_COUNTRY_CODE) {
                String regionCodeForCountryCode = this.getRegionCodeForCountryCode(phonenumber$PhoneNumber.getCountryCode());
                try {
                    if (!regionCodeForCountryCode.equals("ZZ")) {
                        MatchType isNumberMatch = this.isNumberMatch(phonenumber$PhoneNumber, this.parse(charSequence, regionCodeForCountryCode));
                        return isNumberMatch == MatchType.EXACT_MATCH ? MatchType.NSN_MATCH : isNumberMatch;
                    }
                    Phonenumber$PhoneNumber phonenumber$PhoneNumber2 = new Phonenumber$PhoneNumber();
                    this.parseHelper(charSequence, null, false, false, phonenumber$PhoneNumber2);
                    return this.isNumberMatch(phonenumber$PhoneNumber, phonenumber$PhoneNumber2);
                } catch (NumberParseException unused) {
                    return MatchType.NOT_A_NUMBER;
                }
            }
            return MatchType.NOT_A_NUMBER;
        }
    }

    public boolean canBeInternationallyDialled(Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(getRegionCodeForNumber(phonenumber$PhoneNumber));
        if (metadataForRegion == null) {
            return true;
        }
        return !isNumberMatchingDesc(getNationalSignificantNumber(phonenumber$PhoneNumber), metadataForRegion.getNoInternationalDialling());
    }

    public boolean isMobileNumberPortableRegion(String str) {
        Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
        if (metadataForRegion == null) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            logger2.log(level, "Invalid or unknown region code provided: " + str);
            return false;
        }
        return metadataForRegion.getMobileNumberPortableRegion();
    }
}
