package com.android.i18n.phonenumbers;

import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.i18n.phonenumbers.internal.RegexCache;
import com.android.i18n.phonenumbers.metadata.DefaultMetadataDependenciesProvider;
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
        boolean checkGroups(PhoneNumberUtil phoneNumberUtil, Phonenumber.PhoneNumber phoneNumber, StringBuilder sb, String[] strArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum State {
        NOT_READY,
        READY,
        DONE
    }

    static {
        String nonParens = "[^(\\[（［)\\]）］]";
        String bracketPairLimit = limit(0, 3);
        MATCHING_BRACKETS = Pattern.compile("(?:[(\\[（［])?(?:" + nonParens + "+[)\\]）］])?" + nonParens + "+(?:[(\\[（［]" + nonParens + "+[)\\]）］])" + bracketPairLimit + nonParens + "*");
        String leadLimit = limit(0, 2);
        String punctuationLimit = limit(0, 4);
        String blockLimit = limit(0, 20);
        String punctuation = "[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]" + punctuationLimit;
        String digitSequence = "\\p{Nd}" + limit(1, 20);
        String leadClassChars = "(\\[（［+＋";
        String leadClass = "[" + leadClassChars + "]";
        LEAD_CLASS = Pattern.compile(leadClass);
        PATTERN = Pattern.compile("(?:" + leadClass + punctuation + ")" + leadLimit + digitSequence + "(?:" + punctuation + digitSequence + ")" + blockLimit + "(?:" + PhoneNumberUtil.EXTN_PATTERNS_FOR_MATCHING + ")?", 66);
    }

    private static String limit(int lower, int upper) {
        if (lower < 0 || upper <= 0 || upper < lower) {
            throw new IllegalArgumentException();
        }
        return "{" + lower + "," + upper + "}";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PhoneNumberMatcher(PhoneNumberUtil util, CharSequence text, String country, PhoneNumberUtil.Leniency leniency, long maxTries) {
        if (util == null || leniency == null) {
            throw new NullPointerException();
        }
        if (maxTries < 0) {
            throw new IllegalArgumentException();
        }
        this.phoneUtil = util;
        this.text = text != null ? text : "";
        this.preferredRegion = country;
        this.leniency = leniency;
        this.maxTries = maxTries;
    }

    private PhoneNumberMatch find(int index) {
        Matcher matcher = PATTERN.matcher(this.text);
        while (this.maxTries > 0 && matcher.find(index)) {
            int start = matcher.start();
            CharSequence candidate = trimAfterFirstMatch(PhoneNumberUtil.SECOND_NUMBER_START_PATTERN, this.text.subSequence(start, matcher.end()));
            PhoneNumberMatch match = extractMatch(candidate, start);
            if (match != null) {
                return match;
            }
            index = start + candidate.length();
            this.maxTries--;
        }
        return null;
    }

    private static CharSequence trimAfterFirstMatch(Pattern pattern, CharSequence candidate) {
        Matcher trailingCharsMatcher = pattern.matcher(candidate);
        if (trailingCharsMatcher.find()) {
            return candidate.subSequence(0, trailingCharsMatcher.start());
        }
        return candidate;
    }

    static boolean isLatinLetter(char letter) {
        if (Character.isLetter(letter) || Character.getType(letter) == 6) {
            Character.UnicodeBlock block = Character.UnicodeBlock.of(letter);
            return block.equals(Character.UnicodeBlock.BASIC_LATIN) || block.equals(Character.UnicodeBlock.LATIN_1_SUPPLEMENT) || block.equals(Character.UnicodeBlock.LATIN_EXTENDED_A) || block.equals(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) || block.equals(Character.UnicodeBlock.LATIN_EXTENDED_B) || block.equals(Character.UnicodeBlock.COMBINING_DIACRITICAL_MARKS);
        }
        return false;
    }

    private static boolean isInvalidPunctuationSymbol(char character) {
        return character == '%' || Character.getType(character) == 26;
    }

    private PhoneNumberMatch extractMatch(CharSequence candidate, int offset) {
        if (SLASH_SEPARATED_DATES.matcher(candidate).find()) {
            return null;
        }
        if (TIME_STAMPS.matcher(candidate).find()) {
            String followingText = this.text.toString().substring(candidate.length() + offset);
            if (TIME_STAMPS_SUFFIX.matcher(followingText).lookingAt()) {
                return null;
            }
        }
        PhoneNumberMatch match = parseAndVerify(candidate, offset);
        if (match != null) {
            return match;
        }
        return extractInnerMatch(candidate, offset);
    }

    private PhoneNumberMatch extractInnerMatch(CharSequence candidate, int offset) {
        Pattern[] patternArr;
        for (Pattern possibleInnerMatch : INNER_MATCHES) {
            Matcher groupMatcher = possibleInnerMatch.matcher(candidate);
            boolean isFirstMatch = true;
            while (groupMatcher.find() && this.maxTries > 0) {
                if (isFirstMatch) {
                    CharSequence group = trimAfterFirstMatch(PhoneNumberUtil.UNWANTED_END_CHAR_PATTERN, candidate.subSequence(0, groupMatcher.start()));
                    PhoneNumberMatch match = parseAndVerify(group, offset);
                    if (match != null) {
                        return match;
                    }
                    this.maxTries--;
                    isFirstMatch = false;
                }
                CharSequence group2 = trimAfterFirstMatch(PhoneNumberUtil.UNWANTED_END_CHAR_PATTERN, groupMatcher.group(1));
                PhoneNumberMatch match2 = parseAndVerify(group2, groupMatcher.start(1) + offset);
                if (match2 != null) {
                    return match2;
                }
                this.maxTries--;
            }
        }
        return null;
    }

    private PhoneNumberMatch parseAndVerify(CharSequence candidate, int offset) {
        if (MATCHING_BRACKETS.matcher(candidate).matches() && !PUB_PAGES.matcher(candidate).find()) {
            if (this.leniency.compareTo(PhoneNumberUtil.Leniency.VALID) >= 0) {
                if (offset > 0 && !LEAD_CLASS.matcher(candidate).lookingAt()) {
                    char previousChar = this.text.charAt(offset - 1);
                    if (isInvalidPunctuationSymbol(previousChar) || isLatinLetter(previousChar)) {
                        return null;
                    }
                }
                int lastCharIndex = candidate.length() + offset;
                if (lastCharIndex < this.text.length()) {
                    char nextChar = this.text.charAt(lastCharIndex);
                    if (isInvalidPunctuationSymbol(nextChar) || isLatinLetter(nextChar)) {
                        return null;
                    }
                }
            }
            Phonenumber.PhoneNumber number = this.phoneUtil.parseAndKeepRawInput(candidate, this.preferredRegion);
            if (this.leniency.verify(number, candidate, this.phoneUtil, this)) {
                number.clearCountryCodeSource();
                number.clearRawInput();
                number.clearPreferredDomesticCarrierCode();
                return new PhoneNumberMatch(offset, candidate.toString(), number);
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean allNumberGroupsRemainGrouped(PhoneNumberUtil util, Phonenumber.PhoneNumber number, StringBuilder normalizedCandidate, String[] formattedNumberGroups) {
        int fromIndex = 0;
        if (number.getCountryCodeSource() != Phonenumber.PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY) {
            String countryCode = Integer.toString(number.getCountryCode());
            fromIndex = normalizedCandidate.indexOf(countryCode) + countryCode.length();
        }
        for (int i = 0; i < formattedNumberGroups.length; i++) {
            int fromIndex2 = normalizedCandidate.indexOf(formattedNumberGroups[i], fromIndex);
            if (fromIndex2 < 0) {
                return false;
            }
            fromIndex = fromIndex2 + formattedNumberGroups[i].length();
            if (i == 0 && fromIndex < normalizedCandidate.length()) {
                String region = util.getRegionCodeForCountryCode(number.getCountryCode());
                if (util.getNddPrefixForRegion(region, true) != null && Character.isDigit(normalizedCandidate.charAt(fromIndex))) {
                    String nationalSignificantNumber = util.getNationalSignificantNumber(number);
                    return normalizedCandidate.substring(fromIndex - formattedNumberGroups[i].length()).startsWith(nationalSignificantNumber);
                }
            }
        }
        return normalizedCandidate.substring(fromIndex).contains(number.getExtension());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean allNumberGroupsAreExactlyPresent(PhoneNumberUtil util, Phonenumber.PhoneNumber number, StringBuilder normalizedCandidate, String[] formattedNumberGroups) {
        String[] candidateGroups = PhoneNumberUtil.NON_DIGITS_PATTERN.split(normalizedCandidate.toString());
        int candidateNumberGroupIndex = number.hasExtension() ? candidateGroups.length - 2 : candidateGroups.length - 1;
        if (candidateGroups.length == 1 || candidateGroups[candidateNumberGroupIndex].contains(util.getNationalSignificantNumber(number))) {
            return true;
        }
        int formattedNumberGroupIndex = formattedNumberGroups.length - 1;
        while (formattedNumberGroupIndex > 0 && candidateNumberGroupIndex >= 0) {
            if (!candidateGroups[candidateNumberGroupIndex].equals(formattedNumberGroups[formattedNumberGroupIndex])) {
                return false;
            }
            formattedNumberGroupIndex--;
            candidateNumberGroupIndex--;
        }
        return candidateNumberGroupIndex >= 0 && candidateGroups[candidateNumberGroupIndex].endsWith(formattedNumberGroups[0]);
    }

    private static String[] getNationalNumberGroups(PhoneNumberUtil util, Phonenumber.PhoneNumber number) {
        String rfc3966Format = util.format(number, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        int endIndex = rfc3966Format.indexOf(59);
        if (endIndex < 0) {
            endIndex = rfc3966Format.length();
        }
        int startIndex = rfc3966Format.indexOf(45) + 1;
        return rfc3966Format.substring(startIndex, endIndex).split("-");
    }

    private static String[] getNationalNumberGroups(PhoneNumberUtil util, Phonenumber.PhoneNumber number, Phonemetadata.NumberFormat formattingPattern) {
        String nationalSignificantNumber = util.getNationalSignificantNumber(number);
        return util.formatNsnUsingPattern(nationalSignificantNumber, formattingPattern, PhoneNumberUtil.PhoneNumberFormat.RFC3966).split("-");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean checkNumberGroupingIsValid(Phonenumber.PhoneNumber number, CharSequence candidate, PhoneNumberUtil util, NumberGroupingChecker checker) {
        StringBuilder normalizedCandidate = PhoneNumberUtil.normalizeDigits(candidate, true);
        String[] formattedNumberGroups = getNationalNumberGroups(util, number);
        if (checker.checkGroups(util, number, normalizedCandidate, formattedNumberGroups)) {
            return true;
        }
        Phonemetadata.PhoneMetadata alternateFormats = DefaultMetadataDependenciesProvider.getInstance().getAlternateFormatsMetadataSource().getFormattingMetadataForCountryCallingCode(number.getCountryCode());
        String nationalSignificantNumber = util.getNationalSignificantNumber(number);
        if (alternateFormats != null) {
            for (Phonemetadata.NumberFormat alternateFormat : alternateFormats.getNumberFormatList()) {
                if (alternateFormat.getLeadingDigitsPatternCount() > 0) {
                    Pattern pattern = this.regexCache.getPatternForRegex(alternateFormat.getLeadingDigitsPattern(0));
                    if (!pattern.matcher(nationalSignificantNumber).lookingAt()) {
                        continue;
                    }
                }
                String[] formattedNumberGroups2 = getNationalNumberGroups(util, number, alternateFormat);
                if (checker.checkGroups(util, number, normalizedCandidate, formattedNumberGroups2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean containsMoreThanOneSlashInNationalNumber(Phonenumber.PhoneNumber number, String candidate) {
        int secondSlashInBodyIndex;
        int firstSlashInBodyIndex = candidate.indexOf(47);
        if (firstSlashInBodyIndex < 0 || (secondSlashInBodyIndex = candidate.indexOf(47, firstSlashInBodyIndex + 1)) < 0) {
            return false;
        }
        boolean candidateHasCountryCode = number.getCountryCodeSource() == Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN || number.getCountryCodeSource() == Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITHOUT_PLUS_SIGN;
        if (candidateHasCountryCode && PhoneNumberUtil.normalizeDigitsOnly(candidate.substring(0, firstSlashInBodyIndex)).equals(Integer.toString(number.getCountryCode()))) {
            return candidate.substring(secondSlashInBodyIndex + 1).contains("/");
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean containsOnlyValidXChars(Phonenumber.PhoneNumber number, String candidate, PhoneNumberUtil util) {
        int index = 0;
        while (index < candidate.length() - 1) {
            char charAtIndex = candidate.charAt(index);
            if (charAtIndex == 'x' || charAtIndex == 'X') {
                char charAtNextIndex = candidate.charAt(index + 1);
                if (charAtNextIndex == 'x' || charAtNextIndex == 'X') {
                    index++;
                    if (util.isNumberMatch(number, candidate.substring(index)) != PhoneNumberUtil.MatchType.NSN_MATCH) {
                        return false;
                    }
                } else if (!PhoneNumberUtil.normalizeDigitsOnly(candidate.substring(index)).equals(number.getExtension())) {
                    return false;
                }
            }
            index++;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isNationalPrefixPresentIfRequired(Phonenumber.PhoneNumber number, PhoneNumberUtil util) {
        if (number.getCountryCodeSource() != Phonenumber.PhoneNumber.CountryCodeSource.FROM_DEFAULT_COUNTRY) {
            return true;
        }
        String phoneNumberRegion = util.getRegionCodeForCountryCode(number.getCountryCode());
        Phonemetadata.PhoneMetadata metadata = util.getMetadataForRegion(phoneNumberRegion);
        if (metadata == null) {
            return true;
        }
        String nationalNumber = util.getNationalSignificantNumber(number);
        Phonemetadata.NumberFormat formatRule = util.chooseFormattingPatternForNumber(metadata.getNumberFormatList(), nationalNumber);
        if (formatRule == null || formatRule.getNationalPrefixFormattingRule().length() <= 0 || formatRule.getNationalPrefixOptionalWhenFormatting() || PhoneNumberUtil.formattingRuleHasFirstGroupOnly(formatRule.getNationalPrefixFormattingRule())) {
            return true;
        }
        String rawInputCopy = PhoneNumberUtil.normalizeDigitsOnly(number.getRawInput());
        StringBuilder rawInput = new StringBuilder(rawInputCopy);
        return util.maybeStripNationalPrefixAndCarrierCode(rawInput, metadata, null);
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
        PhoneNumberMatch result = this.lastMatch;
        this.lastMatch = null;
        this.state = State.NOT_READY;
        return result;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
