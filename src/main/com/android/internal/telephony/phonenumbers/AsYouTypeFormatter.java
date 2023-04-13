package com.android.internal.telephony.phonenumbers;

import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.phonenumbers.internal.RegexCache;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class AsYouTypeFormatter {
    private Phonemetadata$PhoneMetadata currentMetadata;
    private String defaultCountry;
    private Phonemetadata$PhoneMetadata defaultMetadata;
    private static final Phonemetadata$PhoneMetadata EMPTY_METADATA = Phonemetadata$PhoneMetadata.newBuilder().setId("<ignored>").setInternationalPrefix("NA").build();
    private static final Pattern ELIGIBLE_FORMAT_PATTERN = Pattern.compile("[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]*\\$1[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]*(\\$\\d[-x‐-―−ー－-／  \u00ad\u200b\u2060\u3000()（）［］.\\[\\]/~⁓∼～]*)*");
    private static final Pattern NATIONAL_PREFIX_SEPARATORS_PATTERN = Pattern.compile("[- ]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\u2008");
    private String currentOutput = PhoneConfigurationManager.SSSS;
    private StringBuilder formattingTemplate = new StringBuilder();
    private String currentFormattingPattern = PhoneConfigurationManager.SSSS;
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
    private String extractedNationalPrefix = PhoneConfigurationManager.SSSS;
    private StringBuilder nationalNumber = new StringBuilder();
    private List<Phonemetadata$NumberFormat> possibleFormats = new ArrayList();
    private RegexCache regexCache = new RegexCache(64);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsYouTypeFormatter(String str) {
        this.defaultCountry = str;
        Phonemetadata$PhoneMetadata metadataForRegion = getMetadataForRegion(str);
        this.currentMetadata = metadataForRegion;
        this.defaultMetadata = metadataForRegion;
    }

    private Phonemetadata$PhoneMetadata getMetadataForRegion(String str) {
        Phonemetadata$PhoneMetadata metadataForRegion = this.phoneUtil.getMetadataForRegion(this.phoneUtil.getRegionCodeForCountryCode(this.phoneUtil.getCountryCodeForRegion(str)));
        return metadataForRegion != null ? metadataForRegion : EMPTY_METADATA;
    }

    private boolean maybeCreateNewTemplate() {
        Iterator<Phonemetadata$NumberFormat> it = this.possibleFormats.iterator();
        while (it.hasNext()) {
            Phonemetadata$NumberFormat next = it.next();
            String pattern = next.getPattern();
            if (this.currentFormattingPattern.equals(pattern)) {
                return false;
            }
            if (createFormattingTemplate(next)) {
                this.currentFormattingPattern = pattern;
                this.shouldAddSpaceAfterNationalPrefix = NATIONAL_PREFIX_SEPARATORS_PATTERN.matcher(next.getNationalPrefixFormattingRule()).find();
                this.lastMatchPosition = 0;
                return true;
            }
            it.remove();
        }
        this.ableToFormat = false;
        return false;
    }

    private void getAvailableFormats(String str) {
        List<Phonemetadata$NumberFormat> numberFormatList;
        if ((this.isCompleteNumber && this.extractedNationalPrefix.length() == 0) && this.currentMetadata.getIntlNumberFormatCount() > 0) {
            numberFormatList = this.currentMetadata.getIntlNumberFormatList();
        } else {
            numberFormatList = this.currentMetadata.getNumberFormatList();
        }
        for (Phonemetadata$NumberFormat phonemetadata$NumberFormat : numberFormatList) {
            if (this.extractedNationalPrefix.length() <= 0 || !PhoneNumberUtil.formattingRuleHasFirstGroupOnly(phonemetadata$NumberFormat.getNationalPrefixFormattingRule()) || phonemetadata$NumberFormat.getNationalPrefixOptionalWhenFormatting() || phonemetadata$NumberFormat.hasDomesticCarrierCodeFormattingRule()) {
                if (this.extractedNationalPrefix.length() != 0 || this.isCompleteNumber || PhoneNumberUtil.formattingRuleHasFirstGroupOnly(phonemetadata$NumberFormat.getNationalPrefixFormattingRule()) || phonemetadata$NumberFormat.getNationalPrefixOptionalWhenFormatting()) {
                    if (ELIGIBLE_FORMAT_PATTERN.matcher(phonemetadata$NumberFormat.getFormat()).matches()) {
                        this.possibleFormats.add(phonemetadata$NumberFormat);
                    }
                }
            }
        }
        narrowDownPossibleFormats(str);
    }

    private void narrowDownPossibleFormats(String str) {
        int length = str.length() - 3;
        Iterator<Phonemetadata$NumberFormat> it = this.possibleFormats.iterator();
        while (it.hasNext()) {
            Phonemetadata$NumberFormat next = it.next();
            if (next.getLeadingDigitsPatternCount() != 0) {
                if (!this.regexCache.getPatternForRegex(next.getLeadingDigitsPattern(Math.min(length, next.getLeadingDigitsPatternCount() - 1))).matcher(str).lookingAt()) {
                    it.remove();
                }
            }
        }
    }

    private boolean createFormattingTemplate(Phonemetadata$NumberFormat phonemetadata$NumberFormat) {
        String pattern = phonemetadata$NumberFormat.getPattern();
        this.formattingTemplate.setLength(0);
        String formattingTemplate = getFormattingTemplate(pattern, phonemetadata$NumberFormat.getFormat());
        if (formattingTemplate.length() > 0) {
            this.formattingTemplate.append(formattingTemplate);
            return true;
        }
        return false;
    }

    private String getFormattingTemplate(String str, String str2) {
        Matcher matcher = this.regexCache.getPatternForRegex(str).matcher("999999999999999");
        matcher.find();
        String group = matcher.group();
        return group.length() < this.nationalNumber.length() ? PhoneConfigurationManager.SSSS : group.replaceAll(str, str2).replaceAll("9", "\u2008");
    }

    public void clear() {
        this.currentOutput = PhoneConfigurationManager.SSSS;
        this.accruedInput.setLength(0);
        this.accruedInputWithoutFormatting.setLength(0);
        this.formattingTemplate.setLength(0);
        this.lastMatchPosition = 0;
        this.currentFormattingPattern = PhoneConfigurationManager.SSSS;
        this.prefixBeforeNationalNumber.setLength(0);
        this.extractedNationalPrefix = PhoneConfigurationManager.SSSS;
        this.nationalNumber.setLength(0);
        this.ableToFormat = true;
        this.inputHasFormatting = false;
        this.positionToRemember = 0;
        this.originalPosition = 0;
        this.isCompleteNumber = false;
        this.isExpectingCountryCallingCode = false;
        this.possibleFormats.clear();
        this.shouldAddSpaceAfterNationalPrefix = false;
        if (this.currentMetadata.equals(this.defaultMetadata)) {
            return;
        }
        this.currentMetadata = getMetadataForRegion(this.defaultCountry);
    }

    public String inputDigit(char c) {
        String inputDigitWithOptionToRememberPosition = inputDigitWithOptionToRememberPosition(c, false);
        this.currentOutput = inputDigitWithOptionToRememberPosition;
        return inputDigitWithOptionToRememberPosition;
    }

    public String inputDigitAndRememberPosition(char c) {
        String inputDigitWithOptionToRememberPosition = inputDigitWithOptionToRememberPosition(c, true);
        this.currentOutput = inputDigitWithOptionToRememberPosition;
        return inputDigitWithOptionToRememberPosition;
    }

    private String inputDigitWithOptionToRememberPosition(char c, boolean z) {
        this.accruedInput.append(c);
        if (z) {
            this.originalPosition = this.accruedInput.length();
        }
        if (!isDigitOrLeadingPlusSign(c)) {
            this.ableToFormat = false;
            this.inputHasFormatting = true;
        } else {
            c = normalizeAndAccrueDigitsAndPlusSign(c, z);
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
                this.prefixBeforeNationalNumber.append(' ');
                return attemptToChoosePatternWithPrefixExtracted();
            }
            return this.accruedInput.toString();
        }
        int length = this.accruedInputWithoutFormatting.length();
        if (length == 0 || length == 1 || length == 2) {
            return this.accruedInput.toString();
        }
        if (length == 3) {
            if (attemptToExtractIdd()) {
                this.isExpectingCountryCallingCode = true;
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
            String inputDigitHelper = inputDigitHelper(c);
            String attemptToFormatAccruedDigits = attemptToFormatAccruedDigits();
            if (attemptToFormatAccruedDigits.length() > 0) {
                return attemptToFormatAccruedDigits;
            }
            narrowDownPossibleFormats(this.nationalNumber.toString());
            if (maybeCreateNewTemplate()) {
                return inputAccruedNationalNumber();
            }
            if (this.ableToFormat) {
                return appendNationalNumber(inputDigitHelper);
            }
            return this.accruedInput.toString();
        } else {
            return attemptToChooseFormattingPattern();
        }
    }

    private String attemptToChoosePatternWithPrefixExtracted() {
        this.ableToFormat = true;
        this.isExpectingCountryCallingCode = false;
        this.possibleFormats.clear();
        this.lastMatchPosition = 0;
        this.formattingTemplate.setLength(0);
        this.currentFormattingPattern = PhoneConfigurationManager.SSSS;
        return attemptToChooseFormattingPattern();
    }

    private boolean ableToExtractLongerNdd() {
        if (this.extractedNationalPrefix.length() > 0) {
            this.nationalNumber.insert(0, this.extractedNationalPrefix);
            this.prefixBeforeNationalNumber.setLength(this.prefixBeforeNationalNumber.lastIndexOf(this.extractedNationalPrefix));
        }
        return !this.extractedNationalPrefix.equals(removeNationalPrefixFromNationalNumber());
    }

    private boolean isDigitOrLeadingPlusSign(char c) {
        if (Character.isDigit(c)) {
            return true;
        }
        return this.accruedInput.length() == 1 && PhoneNumberUtil.PLUS_CHARS_PATTERN.matcher(Character.toString(c)).matches();
    }

    String attemptToFormatAccruedDigits() {
        for (Phonemetadata$NumberFormat phonemetadata$NumberFormat : this.possibleFormats) {
            Matcher matcher = this.regexCache.getPatternForRegex(phonemetadata$NumberFormat.getPattern()).matcher(this.nationalNumber);
            if (matcher.matches()) {
                this.shouldAddSpaceAfterNationalPrefix = NATIONAL_PREFIX_SEPARATORS_PATTERN.matcher(phonemetadata$NumberFormat.getNationalPrefixFormattingRule()).find();
                String appendNationalNumber = appendNationalNumber(matcher.replaceAll(phonemetadata$NumberFormat.getFormat()));
                if (PhoneNumberUtil.normalizeDiallableCharsOnly(appendNationalNumber).contentEquals(this.accruedInputWithoutFormatting)) {
                    return appendNationalNumber;
                }
            }
        }
        return PhoneConfigurationManager.SSSS;
    }

    public int getRememberedPosition() {
        if (!this.ableToFormat) {
            return this.originalPosition;
        }
        int i = 0;
        int i2 = 0;
        while (i < this.positionToRemember && i2 < this.currentOutput.length()) {
            if (this.accruedInputWithoutFormatting.charAt(i) == this.currentOutput.charAt(i2)) {
                i++;
            }
            i2++;
        }
        return i2;
    }

    private String appendNationalNumber(String str) {
        int length = this.prefixBeforeNationalNumber.length();
        if (this.shouldAddSpaceAfterNationalPrefix && length > 0 && this.prefixBeforeNationalNumber.charAt(length - 1) != ' ') {
            return new String(this.prefixBeforeNationalNumber) + ' ' + str;
        }
        return ((Object) this.prefixBeforeNationalNumber) + str;
    }

    private String attemptToChooseFormattingPattern() {
        if (this.nationalNumber.length() >= 3) {
            getAvailableFormats(this.nationalNumber.toString());
            String attemptToFormatAccruedDigits = attemptToFormatAccruedDigits();
            return attemptToFormatAccruedDigits.length() > 0 ? attemptToFormatAccruedDigits : maybeCreateNewTemplate() ? inputAccruedNationalNumber() : this.accruedInput.toString();
        }
        return appendNationalNumber(this.nationalNumber.toString());
    }

    private String inputAccruedNationalNumber() {
        int length = this.nationalNumber.length();
        if (length > 0) {
            String str = PhoneConfigurationManager.SSSS;
            for (int i = 0; i < length; i++) {
                str = inputDigitHelper(this.nationalNumber.charAt(i));
            }
            return this.ableToFormat ? appendNationalNumber(str) : this.accruedInput.toString();
        }
        return this.prefixBeforeNationalNumber.toString();
    }

    private boolean isNanpaNumberWithNationalPrefix() {
        return this.currentMetadata.getCountryCode() == 1 && this.nationalNumber.charAt(0) == '1' && this.nationalNumber.charAt(1) != '0' && this.nationalNumber.charAt(1) != '1';
    }

    private String removeNationalPrefixFromNationalNumber() {
        int i = 1;
        if (isNanpaNumberWithNationalPrefix()) {
            StringBuilder sb = this.prefixBeforeNationalNumber;
            sb.append('1');
            sb.append(' ');
            this.isCompleteNumber = true;
        } else {
            if (this.currentMetadata.hasNationalPrefixForParsing()) {
                Matcher matcher = this.regexCache.getPatternForRegex(this.currentMetadata.getNationalPrefixForParsing()).matcher(this.nationalNumber);
                if (matcher.lookingAt() && matcher.end() > 0) {
                    this.isCompleteNumber = true;
                    i = matcher.end();
                    this.prefixBeforeNationalNumber.append(this.nationalNumber.substring(0, i));
                }
            }
            i = 0;
        }
        String substring = this.nationalNumber.substring(0, i);
        this.nationalNumber.delete(0, i);
        return substring;
    }

    private boolean attemptToExtractIdd() {
        RegexCache regexCache = this.regexCache;
        Matcher matcher = regexCache.getPatternForRegex("\\+|" + this.currentMetadata.getInternationalPrefix()).matcher(this.accruedInputWithoutFormatting);
        if (matcher.lookingAt()) {
            this.isCompleteNumber = true;
            int end = matcher.end();
            this.nationalNumber.setLength(0);
            this.nationalNumber.append(this.accruedInputWithoutFormatting.substring(end));
            this.prefixBeforeNationalNumber.setLength(0);
            this.prefixBeforeNationalNumber.append(this.accruedInputWithoutFormatting.substring(0, end));
            if (this.accruedInputWithoutFormatting.charAt(0) != '+') {
                this.prefixBeforeNationalNumber.append(' ');
            }
            return true;
        }
        return false;
    }

    private boolean attemptToExtractCountryCallingCode() {
        StringBuilder sb;
        int extractCountryCode;
        if (this.nationalNumber.length() == 0 || (extractCountryCode = this.phoneUtil.extractCountryCode(this.nationalNumber, (sb = new StringBuilder()))) == 0) {
            return false;
        }
        this.nationalNumber.setLength(0);
        this.nationalNumber.append((CharSequence) sb);
        String regionCodeForCountryCode = this.phoneUtil.getRegionCodeForCountryCode(extractCountryCode);
        if ("001".equals(regionCodeForCountryCode)) {
            this.currentMetadata = this.phoneUtil.getMetadataForNonGeographicalRegion(extractCountryCode);
        } else if (!regionCodeForCountryCode.equals(this.defaultCountry)) {
            this.currentMetadata = getMetadataForRegion(regionCodeForCountryCode);
        }
        String num = Integer.toString(extractCountryCode);
        StringBuilder sb2 = this.prefixBeforeNationalNumber;
        sb2.append(num);
        sb2.append(' ');
        this.extractedNationalPrefix = PhoneConfigurationManager.SSSS;
        return true;
    }

    private char normalizeAndAccrueDigitsAndPlusSign(char c, boolean z) {
        if (c == '+') {
            this.accruedInputWithoutFormatting.append(c);
        } else {
            c = Character.forDigit(Character.digit(c, 10), 10);
            this.accruedInputWithoutFormatting.append(c);
            this.nationalNumber.append(c);
        }
        if (z) {
            this.positionToRemember = this.accruedInputWithoutFormatting.length();
        }
        return c;
    }

    private String inputDigitHelper(char c) {
        Matcher matcher = DIGIT_PATTERN.matcher(this.formattingTemplate);
        if (matcher.find(this.lastMatchPosition)) {
            String replaceFirst = matcher.replaceFirst(Character.toString(c));
            this.formattingTemplate.replace(0, replaceFirst.length(), replaceFirst);
            int start = matcher.start();
            this.lastMatchPosition = start;
            return this.formattingTemplate.substring(0, start + 1);
        }
        if (this.possibleFormats.size() == 1) {
            this.ableToFormat = false;
        }
        this.currentFormattingPattern = PhoneConfigurationManager.SSSS;
        return this.accruedInput.toString();
    }
}
