package com.android.i18n.phonenumbers;

import com.android.i18n.phonenumbers.Phonemetadata;
import com.android.i18n.phonenumbers.internal.RegexCache;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ccil.cowan.tagsoup.Schema;
/* loaded from: classes.dex */
public class AsYouTypeFormatter {
    private static final int MIN_LEADING_DIGITS_LENGTH = 3;
    private static final char SEPARATOR_BEFORE_NATIONAL_NUMBER = ' ';
    private Phonemetadata.PhoneMetadata currentMetadata;
    private String defaultCountry;
    private Phonemetadata.PhoneMetadata defaultMetadata;
    private static final Phonemetadata.PhoneMetadata EMPTY_METADATA = Phonemetadata.PhoneMetadata.newBuilder().setId("<ignored>").setInternationalPrefix("NA").build();
    private static final Pattern ELIGIBLE_FORMAT_PATTERN = Pattern.compile("[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]*\\$1[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]*(\\$\\d[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]*)*");
    private static final Pattern NATIONAL_PREFIX_SEPARATORS_PATTERN = Pattern.compile("[- ]");
    private static final String DIGIT_PLACEHOLDER = "\u2008";
    private static final Pattern DIGIT_PATTERN = Pattern.compile(DIGIT_PLACEHOLDER);
    private String currentOutput = "";
    private StringBuilder formattingTemplate = new StringBuilder();
    private String currentFormattingPattern = "";
    private StringBuilder accruedInput = new StringBuilder();
    private StringBuilder accruedInputWithoutFormatting = new StringBuilder();
    private boolean ableToFormat = true;
    private boolean inputHasFormatting = false;
    private boolean isCompleteNumber = false;
    private boolean isExpectingCountryCallingCode = false;
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private int lastMatchPosition = 0;
    private int originalPosition = 0;
    private int positionToRemember = 0;
    private StringBuilder prefixBeforeNationalNumber = new StringBuilder();
    private boolean shouldAddSpaceAfterNationalPrefix = false;
    private String extractedNationalPrefix = "";
    private StringBuilder nationalNumber = new StringBuilder();
    private List<Phonemetadata.NumberFormat> possibleFormats = new ArrayList();
    private RegexCache regexCache = new RegexCache(64);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsYouTypeFormatter(String regionCode) {
        this.defaultCountry = regionCode;
        Phonemetadata.PhoneMetadata metadataForRegion = getMetadataForRegion(regionCode);
        this.currentMetadata = metadataForRegion;
        this.defaultMetadata = metadataForRegion;
    }

    private Phonemetadata.PhoneMetadata getMetadataForRegion(String regionCode) {
        int countryCallingCode = this.phoneUtil.getCountryCodeForRegion(regionCode);
        String mainCountry = this.phoneUtil.getRegionCodeForCountryCode(countryCallingCode);
        Phonemetadata.PhoneMetadata metadata = this.phoneUtil.getMetadataForRegion(mainCountry);
        if (metadata != null) {
            return metadata;
        }
        return EMPTY_METADATA;
    }

    private boolean maybeCreateNewTemplate() {
        Iterator<Phonemetadata.NumberFormat> it = this.possibleFormats.iterator();
        while (it.hasNext()) {
            Phonemetadata.NumberFormat numberFormat = it.next();
            String pattern = numberFormat.getPattern();
            if (this.currentFormattingPattern.equals(pattern)) {
                return false;
            }
            if (createFormattingTemplate(numberFormat)) {
                this.currentFormattingPattern = pattern;
                this.shouldAddSpaceAfterNationalPrefix = NATIONAL_PREFIX_SEPARATORS_PATTERN.matcher(numberFormat.getNationalPrefixFormattingRule()).find();
                this.lastMatchPosition = 0;
                return true;
            }
            it.remove();
        }
        this.ableToFormat = false;
        return false;
    }

    private void getAvailableFormats(String leadingDigits) {
        List<Phonemetadata.NumberFormat> formatList;
        boolean isInternationalNumber = this.isCompleteNumber && this.extractedNationalPrefix.length() == 0;
        if (isInternationalNumber && this.currentMetadata.getIntlNumberFormatCount() > 0) {
            formatList = this.currentMetadata.getIntlNumberFormatList();
        } else {
            formatList = this.currentMetadata.getNumberFormatList();
        }
        for (Phonemetadata.NumberFormat format : formatList) {
            if (this.extractedNationalPrefix.length() <= 0 || !PhoneNumberUtil.formattingRuleHasFirstGroupOnly(format.getNationalPrefixFormattingRule()) || format.getNationalPrefixOptionalWhenFormatting() || format.hasDomesticCarrierCodeFormattingRule()) {
                if (this.extractedNationalPrefix.length() != 0 || this.isCompleteNumber || PhoneNumberUtil.formattingRuleHasFirstGroupOnly(format.getNationalPrefixFormattingRule()) || format.getNationalPrefixOptionalWhenFormatting()) {
                    if (ELIGIBLE_FORMAT_PATTERN.matcher(format.getFormat()).matches()) {
                        this.possibleFormats.add(format);
                    }
                }
            }
        }
        narrowDownPossibleFormats(leadingDigits);
    }

    private void narrowDownPossibleFormats(String leadingDigits) {
        int indexOfLeadingDigitsPattern = leadingDigits.length() - 3;
        Iterator<Phonemetadata.NumberFormat> it = this.possibleFormats.iterator();
        while (it.hasNext()) {
            Phonemetadata.NumberFormat format = it.next();
            if (format.getLeadingDigitsPatternCount() != 0) {
                int lastLeadingDigitsPattern = Math.min(indexOfLeadingDigitsPattern, format.getLeadingDigitsPatternCount() - 1);
                Pattern leadingDigitsPattern = this.regexCache.getPatternForRegex(format.getLeadingDigitsPattern(lastLeadingDigitsPattern));
                Matcher m = leadingDigitsPattern.matcher(leadingDigits);
                if (!m.lookingAt()) {
                    it.remove();
                }
            }
        }
    }

    private boolean createFormattingTemplate(Phonemetadata.NumberFormat format) {
        String numberPattern = format.getPattern();
        this.formattingTemplate.setLength(0);
        String tempTemplate = getFormattingTemplate(numberPattern, format.getFormat());
        if (tempTemplate.length() > 0) {
            this.formattingTemplate.append(tempTemplate);
            return true;
        }
        return false;
    }

    private String getFormattingTemplate(String numberPattern, String numberFormat) {
        Matcher m = this.regexCache.getPatternForRegex(numberPattern).matcher("999999999999999");
        m.find();
        String aPhoneNumber = m.group();
        if (aPhoneNumber.length() < this.nationalNumber.length()) {
            return "";
        }
        String template = aPhoneNumber.replaceAll(numberPattern, numberFormat);
        return template.replaceAll("9", DIGIT_PLACEHOLDER);
    }

    public void clear() {
        this.currentOutput = "";
        this.accruedInput.setLength(0);
        this.accruedInputWithoutFormatting.setLength(0);
        this.formattingTemplate.setLength(0);
        this.lastMatchPosition = 0;
        this.currentFormattingPattern = "";
        this.prefixBeforeNationalNumber.setLength(0);
        this.extractedNationalPrefix = "";
        this.nationalNumber.setLength(0);
        this.ableToFormat = true;
        this.inputHasFormatting = false;
        this.positionToRemember = 0;
        this.originalPosition = 0;
        this.isCompleteNumber = false;
        this.isExpectingCountryCallingCode = false;
        this.possibleFormats.clear();
        this.shouldAddSpaceAfterNationalPrefix = false;
        if (!this.currentMetadata.equals(this.defaultMetadata)) {
            this.currentMetadata = getMetadataForRegion(this.defaultCountry);
        }
    }

    public String inputDigit(char nextChar) {
        String inputDigitWithOptionToRememberPosition = inputDigitWithOptionToRememberPosition(nextChar, false);
        this.currentOutput = inputDigitWithOptionToRememberPosition;
        return inputDigitWithOptionToRememberPosition;
    }

    public String inputDigitAndRememberPosition(char nextChar) {
        String inputDigitWithOptionToRememberPosition = inputDigitWithOptionToRememberPosition(nextChar, true);
        this.currentOutput = inputDigitWithOptionToRememberPosition;
        return inputDigitWithOptionToRememberPosition;
    }

    private String inputDigitWithOptionToRememberPosition(char nextChar, boolean rememberPosition) {
        this.accruedInput.append(nextChar);
        if (rememberPosition) {
            this.originalPosition = this.accruedInput.length();
        }
        if (!isDigitOrLeadingPlusSign(nextChar)) {
            this.ableToFormat = false;
            this.inputHasFormatting = true;
        } else {
            nextChar = normalizeAndAccrueDigitsAndPlusSign(nextChar, rememberPosition);
        }
        if (!this.ableToFormat) {
            if (this.inputHasFormatting) {
                return this.accruedInput.toString();
            }
            if (attemptToExtractIdd()) {
                if (attemptToExtractCountryCallingCode()) {
                    return attemptToChoosePatternWithPrefixExtracted();
                }
            } else if (ableToExtractLongerNdd()) {
                this.prefixBeforeNationalNumber.append(SEPARATOR_BEFORE_NATIONAL_NUMBER);
                return attemptToChoosePatternWithPrefixExtracted();
            }
            return this.accruedInput.toString();
        }
        switch (this.accruedInputWithoutFormatting.length()) {
            case Schema.M_EMPTY /* 0 */:
            case Schema.F_RESTART /* 1 */:
            case 2:
                return this.accruedInput.toString();
            case MIN_LEADING_DIGITS_LENGTH /* 3 */:
                if (attemptToExtractIdd()) {
                    this.isExpectingCountryCallingCode = true;
                    break;
                } else {
                    this.extractedNationalPrefix = removeNationalPrefixFromNationalNumber();
                    return attemptToChooseFormattingPattern();
                }
        }
        if (this.isExpectingCountryCallingCode) {
            if (attemptToExtractCountryCallingCode()) {
                this.isExpectingCountryCallingCode = false;
            }
            return ((Object) this.prefixBeforeNationalNumber) + this.nationalNumber.toString();
        } else if (this.possibleFormats.size() > 0) {
            String tempNationalNumber = inputDigitHelper(nextChar);
            String formattedNumber = attemptToFormatAccruedDigits();
            if (formattedNumber.length() > 0) {
                return formattedNumber;
            }
            narrowDownPossibleFormats(this.nationalNumber.toString());
            if (maybeCreateNewTemplate()) {
                return inputAccruedNationalNumber();
            }
            if (this.ableToFormat) {
                return appendNationalNumber(tempNationalNumber);
            }
            return this.accruedInput.toString();
        } else {
            String tempNationalNumber2 = attemptToChooseFormattingPattern();
            return tempNationalNumber2;
        }
    }

    private String attemptToChoosePatternWithPrefixExtracted() {
        this.ableToFormat = true;
        this.isExpectingCountryCallingCode = false;
        this.possibleFormats.clear();
        this.lastMatchPosition = 0;
        this.formattingTemplate.setLength(0);
        this.currentFormattingPattern = "";
        return attemptToChooseFormattingPattern();
    }

    String getExtractedNationalPrefix() {
        return this.extractedNationalPrefix;
    }

    private boolean ableToExtractLongerNdd() {
        if (this.extractedNationalPrefix.length() > 0) {
            this.nationalNumber.insert(0, this.extractedNationalPrefix);
            int indexOfPreviousNdd = this.prefixBeforeNationalNumber.lastIndexOf(this.extractedNationalPrefix);
            this.prefixBeforeNationalNumber.setLength(indexOfPreviousNdd);
        }
        return !this.extractedNationalPrefix.equals(removeNationalPrefixFromNationalNumber());
    }

    private boolean isDigitOrLeadingPlusSign(char nextChar) {
        if (Character.isDigit(nextChar)) {
            return true;
        }
        return this.accruedInput.length() == 1 && PhoneNumberUtil.PLUS_CHARS_PATTERN.matcher(Character.toString(nextChar)).matches();
    }

    String attemptToFormatAccruedDigits() {
        for (Phonemetadata.NumberFormat numberFormat : this.possibleFormats) {
            Matcher m = this.regexCache.getPatternForRegex(numberFormat.getPattern()).matcher(this.nationalNumber);
            if (m.matches()) {
                this.shouldAddSpaceAfterNationalPrefix = NATIONAL_PREFIX_SEPARATORS_PATTERN.matcher(numberFormat.getNationalPrefixFormattingRule()).find();
                String formattedNumber = m.replaceAll(numberFormat.getFormat());
                String fullOutput = appendNationalNumber(formattedNumber);
                String formattedNumberDigitsOnly = PhoneNumberUtil.normalizeDiallableCharsOnly(fullOutput);
                if (formattedNumberDigitsOnly.contentEquals(this.accruedInputWithoutFormatting)) {
                    return fullOutput;
                }
            }
        }
        return "";
    }

    public int getRememberedPosition() {
        if (!this.ableToFormat) {
            return this.originalPosition;
        }
        int accruedInputIndex = 0;
        int currentOutputIndex = 0;
        while (accruedInputIndex < this.positionToRemember && currentOutputIndex < this.currentOutput.length()) {
            if (this.accruedInputWithoutFormatting.charAt(accruedInputIndex) == this.currentOutput.charAt(currentOutputIndex)) {
                accruedInputIndex++;
            }
            currentOutputIndex++;
        }
        return currentOutputIndex;
    }

    private String appendNationalNumber(String nationalNumber) {
        int prefixBeforeNationalNumberLength = this.prefixBeforeNationalNumber.length();
        if (this.shouldAddSpaceAfterNationalPrefix && prefixBeforeNationalNumberLength > 0 && this.prefixBeforeNationalNumber.charAt(prefixBeforeNationalNumberLength - 1) != ' ') {
            return new String(this.prefixBeforeNationalNumber) + SEPARATOR_BEFORE_NATIONAL_NUMBER + nationalNumber;
        }
        return ((Object) this.prefixBeforeNationalNumber) + nationalNumber;
    }

    private String attemptToChooseFormattingPattern() {
        if (this.nationalNumber.length() >= MIN_LEADING_DIGITS_LENGTH) {
            getAvailableFormats(this.nationalNumber.toString());
            String formattedNumber = attemptToFormatAccruedDigits();
            if (formattedNumber.length() > 0) {
                return formattedNumber;
            }
            return maybeCreateNewTemplate() ? inputAccruedNationalNumber() : this.accruedInput.toString();
        }
        return appendNationalNumber(this.nationalNumber.toString());
    }

    private String inputAccruedNationalNumber() {
        int lengthOfNationalNumber = this.nationalNumber.length();
        if (lengthOfNationalNumber > 0) {
            String tempNationalNumber = "";
            for (int i = 0; i < lengthOfNationalNumber; i++) {
                tempNationalNumber = inputDigitHelper(this.nationalNumber.charAt(i));
            }
            return this.ableToFormat ? appendNationalNumber(tempNationalNumber) : this.accruedInput.toString();
        }
        return this.prefixBeforeNationalNumber.toString();
    }

    private boolean isNanpaNumberWithNationalPrefix() {
        return this.currentMetadata.getCountryCode() == 1 && this.nationalNumber.charAt(0) == '1' && this.nationalNumber.charAt(1) != '0' && this.nationalNumber.charAt(1) != '1';
    }

    private String removeNationalPrefixFromNationalNumber() {
        int startOfNationalNumber = 0;
        if (isNanpaNumberWithNationalPrefix()) {
            startOfNationalNumber = 1;
            this.prefixBeforeNationalNumber.append('1').append(SEPARATOR_BEFORE_NATIONAL_NUMBER);
            this.isCompleteNumber = true;
        } else if (this.currentMetadata.hasNationalPrefixForParsing()) {
            Pattern nationalPrefixForParsing = this.regexCache.getPatternForRegex(this.currentMetadata.getNationalPrefixForParsing());
            Matcher m = nationalPrefixForParsing.matcher(this.nationalNumber);
            if (m.lookingAt() && m.end() > 0) {
                this.isCompleteNumber = true;
                startOfNationalNumber = m.end();
                this.prefixBeforeNationalNumber.append(this.nationalNumber.substring(0, startOfNationalNumber));
            }
        }
        String nationalPrefix = this.nationalNumber.substring(0, startOfNationalNumber);
        this.nationalNumber.delete(0, startOfNationalNumber);
        return nationalPrefix;
    }

    private boolean attemptToExtractIdd() {
        Pattern internationalPrefix = this.regexCache.getPatternForRegex("\\+|" + this.currentMetadata.getInternationalPrefix());
        Matcher iddMatcher = internationalPrefix.matcher(this.accruedInputWithoutFormatting);
        if (iddMatcher.lookingAt()) {
            this.isCompleteNumber = true;
            int startOfCountryCallingCode = iddMatcher.end();
            this.nationalNumber.setLength(0);
            this.nationalNumber.append(this.accruedInputWithoutFormatting.substring(startOfCountryCallingCode));
            this.prefixBeforeNationalNumber.setLength(0);
            this.prefixBeforeNationalNumber.append(this.accruedInputWithoutFormatting.substring(0, startOfCountryCallingCode));
            if (this.accruedInputWithoutFormatting.charAt(0) != '+') {
                this.prefixBeforeNationalNumber.append(SEPARATOR_BEFORE_NATIONAL_NUMBER);
            }
            return true;
        }
        return false;
    }

    private boolean attemptToExtractCountryCallingCode() {
        StringBuilder numberWithoutCountryCallingCode;
        int countryCode;
        if (this.nationalNumber.length() == 0 || (countryCode = this.phoneUtil.extractCountryCode(this.nationalNumber, (numberWithoutCountryCallingCode = new StringBuilder()))) == 0) {
            return false;
        }
        this.nationalNumber.setLength(0);
        this.nationalNumber.append((CharSequence) numberWithoutCountryCallingCode);
        String newRegionCode = this.phoneUtil.getRegionCodeForCountryCode(countryCode);
        if ("001".equals(newRegionCode)) {
            this.currentMetadata = this.phoneUtil.getMetadataForNonGeographicalRegion(countryCode);
        } else if (!newRegionCode.equals(this.defaultCountry)) {
            this.currentMetadata = getMetadataForRegion(newRegionCode);
        }
        String countryCodeString = Integer.toString(countryCode);
        this.prefixBeforeNationalNumber.append(countryCodeString).append(SEPARATOR_BEFORE_NATIONAL_NUMBER);
        this.extractedNationalPrefix = "";
        return true;
    }

    private char normalizeAndAccrueDigitsAndPlusSign(char nextChar, boolean rememberPosition) {
        char normalizedChar;
        if (nextChar == '+') {
            normalizedChar = nextChar;
            this.accruedInputWithoutFormatting.append(nextChar);
        } else {
            char normalizedChar2 = Character.forDigit(Character.digit(nextChar, 10), 10);
            this.accruedInputWithoutFormatting.append(normalizedChar2);
            this.nationalNumber.append(normalizedChar2);
            normalizedChar = normalizedChar2;
        }
        if (rememberPosition) {
            this.positionToRemember = this.accruedInputWithoutFormatting.length();
        }
        return normalizedChar;
    }

    private String inputDigitHelper(char nextChar) {
        Matcher digitMatcher = DIGIT_PATTERN.matcher(this.formattingTemplate);
        if (digitMatcher.find(this.lastMatchPosition)) {
            String tempTemplate = digitMatcher.replaceFirst(Character.toString(nextChar));
            this.formattingTemplate.replace(0, tempTemplate.length(), tempTemplate);
            int start = digitMatcher.start();
            this.lastMatchPosition = start;
            return this.formattingTemplate.substring(0, start + 1);
        }
        if (this.possibleFormats.size() == 1) {
            this.ableToFormat = false;
        }
        this.currentFormattingPattern = "";
        return this.accruedInput.toString();
    }
}
