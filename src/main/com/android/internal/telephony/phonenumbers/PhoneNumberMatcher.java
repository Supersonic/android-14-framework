package com.android.internal.telephony.phonenumbers;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.phonenumbers.PhoneNumberUtil;
import com.android.internal.telephony.phonenumbers.Phonenumber$PhoneNumber;
import com.android.internal.telephony.phonenumbers.internal.RegexCache;
import com.android.internal.telephony.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
import java.lang.Character;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
final class PhoneNumberMatcher implements Iterator<PhoneNumberMatch> {
    private static final Pattern LEAD_CLASS;
    private static final Pattern MATCHING_BRACKETS;
    private static final Pattern PATTERN;
    private final PhoneNumberUtil.Leniency leniency;
    private long maxTries;
    private final PhoneNumberUtil phoneUtil;
    private final String preferredRegion;
    private final CharSequence text;
    private static final Pattern PUB_PAGES = Pattern.compile("\\d{1,5}-+\\d{1,5}\\s{0,4}\\(\\d{1,4}");
    private static final Pattern SLASH_SEPARATED_DATES = Pattern.compile("(?:(?:[0-3]?\\d/[01]?\\d)|(?:[01]?\\d/[0-3]?\\d))/(?:[12]\\d)?\\d{2}");
    private static final Pattern TIME_STAMPS = Pattern.compile("[12]\\d{3}[-/]?[01]\\d[-/]?[0-3]\\d +[0-2]\\d$");
    private static final Pattern TIME_STAMPS_SUFFIX = Pattern.compile(":[0-5]\\d");
    private static final Pattern[] INNER_MATCHES = {Pattern.compile("/+(.*)"), Pattern.compile("(\\([^(]*)"), Pattern.compile("(?:\\p{Z}-|-\\p{Z})\\p{Z}*(.+)"), Pattern.compile("[‒-―－]\\p{Z}*(.+)"), Pattern.compile("\\.+\\p{Z}*([^.]+)"), Pattern.compile("\\p{Z}+(\\P{Z}+)")};
    private State state = State.NOT_READY;
    private PhoneNumberMatch lastMatch = null;
    private int searchIndex = 0;
    private final RegexCache regexCache = new RegexCache(32);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface NumberGroupingChecker {
        boolean checkGroups(PhoneNumberUtil phoneNumberUtil, Phonenumber$PhoneNumber phonenumber$PhoneNumber, StringBuilder sb, String[] strArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum State {
        NOT_READY,
        READY,
        DONE
    }

    static {
        String str = "[^(\\[（［)\\]）］]";
        MATCHING_BRACKETS = Pattern.compile("(?:[(\\[（［])?(?:" + str + "+[)\\]）］])?" + str + "+(?:[(\\[（［]" + str + "+[)\\]）］])" + limit(0, 3) + str + "*");
        String limit = limit(0, 2);
        String limit2 = limit(0, 4);
        String limit3 = limit(0, 20);
        String str2 = "[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]" + limit2;
        String str3 = "\\p{Nd}" + limit(1, 20);
        String str4 = "[" + ("(\\[（［+＋") + "]";
        LEAD_CLASS = Pattern.compile(str4);
        PATTERN = Pattern.compile("(?:" + str4 + str2 + ")" + limit + str3 + "(?:" + str2 + str3 + ")" + limit3 + "(?:" + PhoneNumberUtil.EXTN_PATTERNS_FOR_MATCHING + ")?", 66);
    }

    private static String limit(int i, int i2) {
        if (i < 0 || i2 <= 0 || i2 < i) {
            throw new IllegalArgumentException();
        }
        return "{" + i + "," + i2 + "}";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PhoneNumberMatcher(PhoneNumberUtil phoneNumberUtil, CharSequence charSequence, String str, PhoneNumberUtil.Leniency leniency, long j) {
        if (phoneNumberUtil == null || leniency == null) {
            throw null;
        }
        if (j < 0) {
            throw new IllegalArgumentException();
        }
        this.phoneUtil = phoneNumberUtil;
        this.text = charSequence == null ? PhoneConfigurationManager.SSSS : charSequence;
        this.preferredRegion = str;
        this.leniency = leniency;
        this.maxTries = j;
    }

    private PhoneNumberMatch find(int i) {
        Matcher matcher = PATTERN.matcher(this.text);
        while (this.maxTries > 0 && matcher.find(i)) {
            int start = matcher.start();
            CharSequence trimAfterFirstMatch = trimAfterFirstMatch(PhoneNumberUtil.SECOND_NUMBER_START_PATTERN, this.text.subSequence(start, matcher.end()));
            PhoneNumberMatch extractMatch = extractMatch(trimAfterFirstMatch, start);
            if (extractMatch != null) {
                return extractMatch;
            }
            i = start + trimAfterFirstMatch.length();
            this.maxTries--;
        }
        return null;
    }

    private static CharSequence trimAfterFirstMatch(Pattern pattern, CharSequence charSequence) {
        Matcher matcher = pattern.matcher(charSequence);
        return matcher.find() ? charSequence.subSequence(0, matcher.start()) : charSequence;
    }

    static boolean isLatinLetter(char c) {
        if (Character.isLetter(c) || Character.getType(c) == 6) {
            Character.UnicodeBlock of = Character.UnicodeBlock.of(c);
            return of.equals(Character.UnicodeBlock.BASIC_LATIN) || of.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) || of.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) || of.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) || of.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) || of.equals(Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS);
        }
        return false;
    }

    private static boolean isInvalidPunctuationSymbol(char c) {
        return c == '%' || Character.getType(c) == 26;
    }

    private PhoneNumberMatch extractMatch(CharSequence charSequence, int i) {
        if (SLASH_SEPARATED_DATES.matcher(charSequence).find()) {
            return null;
        }
        if (TIME_STAMPS.matcher(charSequence).find()) {
            if (TIME_STAMPS_SUFFIX.matcher(this.text.toString().substring(charSequence.length() + i)).lookingAt()) {
                return null;
            }
        }
        PhoneNumberMatch parseAndVerify = parseAndVerify(charSequence, i);
        return parseAndVerify != null ? parseAndVerify : extractInnerMatch(charSequence, i);
    }

    private PhoneNumberMatch extractInnerMatch(CharSequence charSequence, int i) {
        for (Pattern pattern : INNER_MATCHES) {
            Matcher matcher = pattern.matcher(charSequence);
            boolean z = true;
            while (matcher.find() && this.maxTries > 0) {
                if (z) {
                    PhoneNumberMatch parseAndVerify = parseAndVerify(trimAfterFirstMatch(PhoneNumberUtil.UNWANTED_END_CHAR_PATTERN, charSequence.subSequence(0, matcher.start())), i);
                    if (parseAndVerify != null) {
                        return parseAndVerify;
                    }
                    this.maxTries--;
                    z = false;
                }
                PhoneNumberMatch parseAndVerify2 = parseAndVerify(trimAfterFirstMatch(PhoneNumberUtil.UNWANTED_END_CHAR_PATTERN, matcher.group(1)), matcher.start(1) + i);
                if (parseAndVerify2 != null) {
                    return parseAndVerify2;
                }
                this.maxTries--;
            }
        }
        return null;
    }

    private PhoneNumberMatch parseAndVerify(CharSequence charSequence, int i) {
        try {
            if (MATCHING_BRACKETS.matcher(charSequence).matches() && !PUB_PAGES.matcher(charSequence).find()) {
                if (this.leniency.compareTo(PhoneNumberUtil.Leniency.VALID) >= 0) {
                    if (i > 0 && !LEAD_CLASS.matcher(charSequence).lookingAt()) {
                        char charAt = this.text.charAt(i - 1);
                        if (isInvalidPunctuationSymbol(charAt) || isLatinLetter(charAt)) {
                            return null;
                        }
                    }
                    int length = charSequence.length() + i;
                    if (length < this.text.length()) {
                        char charAt2 = this.text.charAt(length);
                        if (isInvalidPunctuationSymbol(charAt2) || isLatinLetter(charAt2)) {
                            return null;
                        }
                    }
                }
                Phonenumber$PhoneNumber parseAndKeepRawInput = this.phoneUtil.parseAndKeepRawInput(charSequence, this.preferredRegion);
                if (this.leniency.verify(parseAndKeepRawInput, charSequence, this.phoneUtil, this)) {
                    parseAndKeepRawInput.clearCountryCodeSource();
                    parseAndKeepRawInput.clearRawInput();
                    parseAndKeepRawInput.clearPreferredDomesticCarrierCode();
                    return new PhoneNumberMatch(i, charSequence.toString(), parseAndKeepRawInput);
                }
            }
        } catch (NumberParseException unused) {
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean allNumberGroupsRemainGrouped(PhoneNumberUtil phoneNumberUtil, Phonenumber$PhoneNumber phonenumber$PhoneNumber, StringBuilder sb, String[] strArr) {
        int i;
        if (phonenumber$PhoneNumber.getCountryCodeSource() != Phonenumber$PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY) {
            String num = Integer.toString(phonenumber$PhoneNumber.getCountryCode());
            i = sb.indexOf(num) + num.length();
        } else {
            i = 0;
        }
        for (int i2 = 0; i2 < strArr.length; i2++) {
            int indexOf = sb.indexOf(strArr[i2], i);
            if (indexOf < 0) {
                return false;
            }
            i = indexOf + strArr[i2].length();
            if (i2 == 0 && i < sb.length() && phoneNumberUtil.getNddPrefixForRegion(phoneNumberUtil.getRegionCodeForCountryCode(phonenumber$PhoneNumber.getCountryCode()), true) != null && Character.isDigit(sb.charAt(i))) {
                return sb.substring(i - strArr[i2].length()).startsWith(phoneNumberUtil.getNationalSignificantNumber(phonenumber$PhoneNumber));
            }
        }
        return sb.substring(i).contains(phonenumber$PhoneNumber.getExtension());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean allNumberGroupsAreExactlyPresent(PhoneNumberUtil phoneNumberUtil, Phonenumber$PhoneNumber phonenumber$PhoneNumber, StringBuilder sb, String[] strArr) {
        String[] split = PhoneNumberUtil.NON_DIGITS_PATTERN.split(sb.toString());
        int length = phonenumber$PhoneNumber.hasExtension() ? split.length - 2 : split.length - 1;
        if (split.length == 1 || split[length].contains(phoneNumberUtil.getNationalSignificantNumber(phonenumber$PhoneNumber))) {
            return true;
        }
        int length2 = strArr.length - 1;
        while (length2 > 0 && length >= 0) {
            if (!split[length].equals(strArr[length2])) {
                return false;
            }
            length2--;
            length--;
        }
        return length >= 0 && split[length].endsWith(strArr[0]);
    }

    private static String[] getNationalNumberGroups(PhoneNumberUtil phoneNumberUtil, Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        String format = phoneNumberUtil.format(phonenumber$PhoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        int indexOf = format.indexOf(59);
        if (indexOf < 0) {
            indexOf = format.length();
        }
        return format.substring(format.indexOf(45) + 1, indexOf).split("-");
    }

    private static String[] getNationalNumberGroups(PhoneNumberUtil phoneNumberUtil, Phonenumber$PhoneNumber phonenumber$PhoneNumber, Phonemetadata$NumberFormat phonemetadata$NumberFormat) {
        return phoneNumberUtil.formatNsnUsingPattern(phoneNumberUtil.getNationalSignificantNumber(phonenumber$PhoneNumber), phonemetadata$NumberFormat, PhoneNumberUtil.PhoneNumberFormat.RFC3966).split("-");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean checkNumberGroupingIsValid(Phonenumber$PhoneNumber phonenumber$PhoneNumber, CharSequence charSequence, PhoneNumberUtil phoneNumberUtil, NumberGroupingChecker numberGroupingChecker) {
        StringBuilder normalizeDigits = PhoneNumberUtil.normalizeDigits(charSequence, true);
        if (numberGroupingChecker.checkGroups(phoneNumberUtil, phonenumber$PhoneNumber, normalizeDigits, getNationalNumberGroups(phoneNumberUtil, phonenumber$PhoneNumber))) {
            return true;
        }
        Phonemetadata$PhoneMetadata formattingMetadataForCountryCallingCode = DefaultMetadataDependenciesProvider.getInstance().getAlternateFormatsMetadataSource().getFormattingMetadataForCountryCallingCode(phonenumber$PhoneNumber.getCountryCode());
        String nationalSignificantNumber = phoneNumberUtil.getNationalSignificantNumber(phonenumber$PhoneNumber);
        if (formattingMetadataForCountryCallingCode != null) {
            for (Phonemetadata$NumberFormat phonemetadata$NumberFormat : formattingMetadataForCountryCallingCode.getNumberFormatList()) {
                if (phonemetadata$NumberFormat.getLeadingDigitsPatternCount() <= 0 || this.regexCache.getPatternForRegex(phonemetadata$NumberFormat.getLeadingDigitsPattern(0)).matcher(nationalSignificantNumber).lookingAt()) {
                    if (numberGroupingChecker.checkGroups(phoneNumberUtil, phonenumber$PhoneNumber, normalizeDigits, getNationalNumberGroups(phoneNumberUtil, phonenumber$PhoneNumber, phonemetadata$NumberFormat))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean containsMoreThanOneSlashInNationalNumber(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str) {
        int indexOf;
        int indexOf2 = str.indexOf(47);
        if (indexOf2 >= 0 && (indexOf = str.indexOf(47, indexOf2 + 1)) >= 0) {
            if ((phonenumber$PhoneNumber.getCountryCodeSource() == Phonenumber$PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN || phonenumber$PhoneNumber.getCountryCodeSource() == Phonenumber$PhoneNumber.CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN) && PhoneNumberUtil.normalizeDigitsOnly(str.substring(0, indexOf2)).equals(Integer.toString(phonenumber$PhoneNumber.getCountryCode()))) {
                return str.substring(indexOf + 1).contains("/");
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean containsOnlyValidXChars(Phonenumber$PhoneNumber phonenumber$PhoneNumber, String str, PhoneNumberUtil phoneNumberUtil) {
        int i = 0;
        while (i < str.length() - 1) {
            char charAt = str.charAt(i);
            if (charAt == 'x' || charAt == 'X') {
                int i2 = i + 1;
                char charAt2 = str.charAt(i2);
                if (charAt2 == 'x' || charAt2 == 'X') {
                    if (phoneNumberUtil.isNumberMatch(phonenumber$PhoneNumber, str.substring(i2)) != PhoneNumberUtil.MatchType.NSN_MATCH) {
                        return false;
                    }
                    i = i2;
                } else if (!PhoneNumberUtil.normalizeDigitsOnly(str.substring(i)).equals(phonenumber$PhoneNumber.getExtension())) {
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isNationalPrefixPresentIfRequired(Phonenumber$PhoneNumber phonenumber$PhoneNumber, PhoneNumberUtil phoneNumberUtil) {
        Phonemetadata$PhoneMetadata metadataForRegion;
        if (phonenumber$PhoneNumber.getCountryCodeSource() == Phonenumber$PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY && (metadataForRegion = phoneNumberUtil.getMetadataForRegion(phoneNumberUtil.getRegionCodeForCountryCode(phonenumber$PhoneNumber.getCountryCode()))) != null) {
            Phonemetadata$NumberFormat chooseFormattingPatternForNumber = phoneNumberUtil.chooseFormattingPatternForNumber(metadataForRegion.getNumberFormatList(), phoneNumberUtil.getNationalSignificantNumber(phonenumber$PhoneNumber));
            if (chooseFormattingPatternForNumber == null || chooseFormattingPatternForNumber.getNationalPrefixFormattingRule().length() <= 0 || chooseFormattingPatternForNumber.getNationalPrefixOptionalWhenFormatting() || PhoneNumberUtil.formattingRuleHasFirstGroupOnly(chooseFormattingPatternForNumber.getNationalPrefixFormattingRule())) {
                return true;
            }
            return phoneNumberUtil.maybeStripNationalPrefixAndCarrierCode(new StringBuilder(PhoneNumberUtil.normalizeDigitsOnly(phonenumber$PhoneNumber.getRawInput())), metadataForRegion, null);
        }
        return true;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (this.state == State.NOT_READY) {
            PhoneNumberMatch find = find(this.searchIndex);
            this.lastMatch = find;
            if (find == null) {
                this.state = State.DONE;
            } else {
                this.searchIndex = find.end();
                this.state = State.READY;
            }
        }
        return this.state == State.READY;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public PhoneNumberMatch next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        PhoneNumberMatch phoneNumberMatch = this.lastMatch;
        this.lastMatch = null;
        this.state = State.NOT_READY;
        return phoneNumberMatch;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
