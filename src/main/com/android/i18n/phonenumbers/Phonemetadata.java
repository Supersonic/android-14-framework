package com.android.i18n.phonenumbers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class Phonemetadata {
    private Phonemetadata() {
    }

    /* loaded from: classes.dex */
    public static class NumberFormat implements Externalizable {
        private static final long serialVersionUID = 1;
        private boolean hasDomesticCarrierCodeFormattingRule;
        private boolean hasFormat;
        private boolean hasNationalPrefixFormattingRule;
        private boolean hasNationalPrefixOptionalWhenFormatting;
        private boolean hasPattern;
        private String pattern_ = "";
        private String format_ = "";
        private List<String> leadingDigitsPattern_ = new ArrayList();
        private String nationalPrefixFormattingRule_ = "";
        private boolean nationalPrefixOptionalWhenFormatting_ = false;
        private String domesticCarrierCodeFormattingRule_ = "";

        /* loaded from: classes.dex */
        public static final class Builder extends NumberFormat {
            public NumberFormat build() {
                return this;
            }

            public Builder mergeFrom(NumberFormat other) {
                if (other.hasPattern()) {
                    setPattern(other.getPattern());
                }
                if (other.hasFormat()) {
                    setFormat(other.getFormat());
                }
                for (int i = 0; i < other.leadingDigitsPatternSize(); i++) {
                    addLeadingDigitsPattern(other.getLeadingDigitsPattern(i));
                }
                if (other.hasNationalPrefixFormattingRule()) {
                    setNationalPrefixFormattingRule(other.getNationalPrefixFormattingRule());
                }
                if (other.hasDomesticCarrierCodeFormattingRule()) {
                    setDomesticCarrierCodeFormattingRule(other.getDomesticCarrierCodeFormattingRule());
                }
                if (other.hasNationalPrefixOptionalWhenFormatting()) {
                    setNationalPrefixOptionalWhenFormatting(other.getNationalPrefixOptionalWhenFormatting());
                }
                return this;
            }
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public boolean hasPattern() {
            return this.hasPattern;
        }

        public String getPattern() {
            return this.pattern_;
        }

        public NumberFormat setPattern(String value) {
            this.hasPattern = true;
            this.pattern_ = value;
            return this;
        }

        public boolean hasFormat() {
            return this.hasFormat;
        }

        public String getFormat() {
            return this.format_;
        }

        public NumberFormat setFormat(String value) {
            this.hasFormat = true;
            this.format_ = value;
            return this;
        }

        public List<String> leadingDigitPatterns() {
            return this.leadingDigitsPattern_;
        }

        @Deprecated
        public int leadingDigitsPatternSize() {
            return getLeadingDigitsPatternCount();
        }

        public int getLeadingDigitsPatternCount() {
            return this.leadingDigitsPattern_.size();
        }

        public String getLeadingDigitsPattern(int index) {
            return this.leadingDigitsPattern_.get(index);
        }

        public NumberFormat addLeadingDigitsPattern(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.leadingDigitsPattern_.add(value);
            return this;
        }

        public boolean hasNationalPrefixFormattingRule() {
            return this.hasNationalPrefixFormattingRule;
        }

        public String getNationalPrefixFormattingRule() {
            return this.nationalPrefixFormattingRule_;
        }

        public NumberFormat setNationalPrefixFormattingRule(String value) {
            this.hasNationalPrefixFormattingRule = true;
            this.nationalPrefixFormattingRule_ = value;
            return this;
        }

        public NumberFormat clearNationalPrefixFormattingRule() {
            this.hasNationalPrefixFormattingRule = false;
            this.nationalPrefixFormattingRule_ = "";
            return this;
        }

        public boolean hasNationalPrefixOptionalWhenFormatting() {
            return this.hasNationalPrefixOptionalWhenFormatting;
        }

        public boolean getNationalPrefixOptionalWhenFormatting() {
            return this.nationalPrefixOptionalWhenFormatting_;
        }

        public NumberFormat setNationalPrefixOptionalWhenFormatting(boolean value) {
            this.hasNationalPrefixOptionalWhenFormatting = true;
            this.nationalPrefixOptionalWhenFormatting_ = value;
            return this;
        }

        public boolean hasDomesticCarrierCodeFormattingRule() {
            return this.hasDomesticCarrierCodeFormattingRule;
        }

        public String getDomesticCarrierCodeFormattingRule() {
            return this.domesticCarrierCodeFormattingRule_;
        }

        public NumberFormat setDomesticCarrierCodeFormattingRule(String value) {
            this.hasDomesticCarrierCodeFormattingRule = true;
            this.domesticCarrierCodeFormattingRule_ = value;
            return this;
        }

        @Override // java.io.Externalizable
        public void writeExternal(ObjectOutput objectOutput) throws IOException {
            objectOutput.writeUTF(this.pattern_);
            objectOutput.writeUTF(this.format_);
            int leadingDigitsPatternSize = leadingDigitsPatternSize();
            objectOutput.writeInt(leadingDigitsPatternSize);
            for (int i = 0; i < leadingDigitsPatternSize; i++) {
                objectOutput.writeUTF(this.leadingDigitsPattern_.get(i));
            }
            objectOutput.writeBoolean(this.hasNationalPrefixFormattingRule);
            if (this.hasNationalPrefixFormattingRule) {
                objectOutput.writeUTF(this.nationalPrefixFormattingRule_);
            }
            objectOutput.writeBoolean(this.hasDomesticCarrierCodeFormattingRule);
            if (this.hasDomesticCarrierCodeFormattingRule) {
                objectOutput.writeUTF(this.domesticCarrierCodeFormattingRule_);
            }
            objectOutput.writeBoolean(this.nationalPrefixOptionalWhenFormatting_);
        }

        @Override // java.io.Externalizable
        public void readExternal(ObjectInput objectInput) throws IOException {
            setPattern(objectInput.readUTF());
            setFormat(objectInput.readUTF());
            int leadingDigitsPatternSize = objectInput.readInt();
            for (int i = 0; i < leadingDigitsPatternSize; i++) {
                this.leadingDigitsPattern_.add(objectInput.readUTF());
            }
            if (objectInput.readBoolean()) {
                setNationalPrefixFormattingRule(objectInput.readUTF());
            }
            if (objectInput.readBoolean()) {
                setDomesticCarrierCodeFormattingRule(objectInput.readUTF());
            }
            setNationalPrefixOptionalWhenFormatting(objectInput.readBoolean());
        }
    }

    /* loaded from: classes.dex */
    public static class PhoneNumberDesc implements Externalizable {
        private static final long serialVersionUID = 1;
        private boolean hasExampleNumber;
        private boolean hasNationalNumberPattern;
        private String nationalNumberPattern_ = "";
        private List<Integer> possibleLength_ = new ArrayList();
        private List<Integer> possibleLengthLocalOnly_ = new ArrayList();
        private String exampleNumber_ = "";

        /* loaded from: classes.dex */
        public static final class Builder extends PhoneNumberDesc {
            public PhoneNumberDesc build() {
                return this;
            }

            public Builder mergeFrom(PhoneNumberDesc other) {
                if (other.hasNationalNumberPattern()) {
                    setNationalNumberPattern(other.getNationalNumberPattern());
                }
                for (int i = 0; i < other.getPossibleLengthCount(); i++) {
                    addPossibleLength(other.getPossibleLength(i));
                }
                for (int i2 = 0; i2 < other.getPossibleLengthLocalOnlyCount(); i2++) {
                    addPossibleLengthLocalOnly(other.getPossibleLengthLocalOnly(i2));
                }
                if (other.hasExampleNumber()) {
                    setExampleNumber(other.getExampleNumber());
                }
                return this;
            }
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public boolean hasNationalNumberPattern() {
            return this.hasNationalNumberPattern;
        }

        public String getNationalNumberPattern() {
            return this.nationalNumberPattern_;
        }

        public PhoneNumberDesc setNationalNumberPattern(String value) {
            this.hasNationalNumberPattern = true;
            this.nationalNumberPattern_ = value;
            return this;
        }

        public PhoneNumberDesc clearNationalNumberPattern() {
            this.hasNationalNumberPattern = false;
            this.nationalNumberPattern_ = "";
            return this;
        }

        public List<Integer> getPossibleLengthList() {
            return this.possibleLength_;
        }

        public int getPossibleLengthCount() {
            return this.possibleLength_.size();
        }

        public int getPossibleLength(int index) {
            return this.possibleLength_.get(index).intValue();
        }

        public PhoneNumberDesc addPossibleLength(int value) {
            this.possibleLength_.add(Integer.valueOf(value));
            return this;
        }

        public PhoneNumberDesc clearPossibleLength() {
            this.possibleLength_.clear();
            return this;
        }

        public List<Integer> getPossibleLengthLocalOnlyList() {
            return this.possibleLengthLocalOnly_;
        }

        public int getPossibleLengthLocalOnlyCount() {
            return this.possibleLengthLocalOnly_.size();
        }

        public int getPossibleLengthLocalOnly(int index) {
            return this.possibleLengthLocalOnly_.get(index).intValue();
        }

        public PhoneNumberDesc addPossibleLengthLocalOnly(int value) {
            this.possibleLengthLocalOnly_.add(Integer.valueOf(value));
            return this;
        }

        public PhoneNumberDesc clearPossibleLengthLocalOnly() {
            this.possibleLengthLocalOnly_.clear();
            return this;
        }

        public boolean hasExampleNumber() {
            return this.hasExampleNumber;
        }

        public String getExampleNumber() {
            return this.exampleNumber_;
        }

        public PhoneNumberDesc setExampleNumber(String value) {
            this.hasExampleNumber = true;
            this.exampleNumber_ = value;
            return this;
        }

        public PhoneNumberDesc clearExampleNumber() {
            this.hasExampleNumber = false;
            this.exampleNumber_ = "";
            return this;
        }

        public boolean exactlySameAs(PhoneNumberDesc other) {
            return this.nationalNumberPattern_.equals(other.nationalNumberPattern_) && this.possibleLength_.equals(other.possibleLength_) && this.possibleLengthLocalOnly_.equals(other.possibleLengthLocalOnly_) && this.exampleNumber_.equals(other.exampleNumber_);
        }

        @Override // java.io.Externalizable
        public void writeExternal(ObjectOutput objectOutput) throws IOException {
            objectOutput.writeBoolean(this.hasNationalNumberPattern);
            if (this.hasNationalNumberPattern) {
                objectOutput.writeUTF(this.nationalNumberPattern_);
            }
            int possibleLengthSize = getPossibleLengthCount();
            objectOutput.writeInt(possibleLengthSize);
            for (int i = 0; i < possibleLengthSize; i++) {
                objectOutput.writeInt(this.possibleLength_.get(i).intValue());
            }
            int possibleLengthLocalOnlySize = getPossibleLengthLocalOnlyCount();
            objectOutput.writeInt(possibleLengthLocalOnlySize);
            for (int i2 = 0; i2 < possibleLengthLocalOnlySize; i2++) {
                objectOutput.writeInt(this.possibleLengthLocalOnly_.get(i2).intValue());
            }
            objectOutput.writeBoolean(this.hasExampleNumber);
            if (this.hasExampleNumber) {
                objectOutput.writeUTF(this.exampleNumber_);
            }
        }

        @Override // java.io.Externalizable
        public void readExternal(ObjectInput objectInput) throws IOException {
            if (objectInput.readBoolean()) {
                setNationalNumberPattern(objectInput.readUTF());
            }
            int possibleLengthSize = objectInput.readInt();
            for (int i = 0; i < possibleLengthSize; i++) {
                this.possibleLength_.add(Integer.valueOf(objectInput.readInt()));
            }
            int possibleLengthLocalOnlySize = objectInput.readInt();
            for (int i2 = 0; i2 < possibleLengthLocalOnlySize; i2++) {
                this.possibleLengthLocalOnly_.add(Integer.valueOf(objectInput.readInt()));
            }
            if (objectInput.readBoolean()) {
                setExampleNumber(objectInput.readUTF());
            }
        }
    }

    /* loaded from: classes.dex */
    public static class PhoneMetadata implements Externalizable {
        private static final long serialVersionUID = 1;
        private boolean hasCarrierSpecific;
        private boolean hasCountryCode;
        private boolean hasEmergency;
        private boolean hasFixedLine;
        private boolean hasGeneralDesc;
        private boolean hasId;
        private boolean hasInternationalPrefix;
        private boolean hasLeadingDigits;
        private boolean hasMainCountryForCode;
        private boolean hasMobile;
        private boolean hasMobileNumberPortableRegion;
        private boolean hasNationalPrefix;
        private boolean hasNationalPrefixForParsing;
        private boolean hasNationalPrefixTransformRule;
        private boolean hasNoInternationalDialling;
        private boolean hasPager;
        private boolean hasPersonalNumber;
        private boolean hasPreferredExtnPrefix;
        private boolean hasPreferredInternationalPrefix;
        private boolean hasPremiumRate;
        private boolean hasSameMobileAndFixedLinePattern;
        private boolean hasSharedCost;
        private boolean hasShortCode;
        private boolean hasSmsServices;
        private boolean hasStandardRate;
        private boolean hasTollFree;
        private boolean hasUan;
        private boolean hasVoicemail;
        private boolean hasVoip;
        private PhoneNumberDesc generalDesc_ = null;
        private PhoneNumberDesc fixedLine_ = null;
        private PhoneNumberDesc mobile_ = null;
        private PhoneNumberDesc tollFree_ = null;
        private PhoneNumberDesc premiumRate_ = null;
        private PhoneNumberDesc sharedCost_ = null;
        private PhoneNumberDesc personalNumber_ = null;
        private PhoneNumberDesc voip_ = null;
        private PhoneNumberDesc pager_ = null;
        private PhoneNumberDesc uan_ = null;
        private PhoneNumberDesc emergency_ = null;
        private PhoneNumberDesc voicemail_ = null;
        private PhoneNumberDesc shortCode_ = null;
        private PhoneNumberDesc standardRate_ = null;
        private PhoneNumberDesc carrierSpecific_ = null;
        private PhoneNumberDesc smsServices_ = null;
        private PhoneNumberDesc noInternationalDialling_ = null;
        private String id_ = "";
        private int countryCode_ = 0;
        private String internationalPrefix_ = "";
        private String preferredInternationalPrefix_ = "";
        private String nationalPrefix_ = "";
        private String preferredExtnPrefix_ = "";
        private String nationalPrefixForParsing_ = "";
        private String nationalPrefixTransformRule_ = "";
        private boolean sameMobileAndFixedLinePattern_ = false;
        private List<NumberFormat> numberFormat_ = new ArrayList();
        private List<NumberFormat> intlNumberFormat_ = new ArrayList();
        private boolean mainCountryForCode_ = false;
        private String leadingDigits_ = "";
        private boolean mobileNumberPortableRegion_ = false;

        /* loaded from: classes.dex */
        public static final class Builder extends PhoneMetadata {
            public PhoneMetadata build() {
                return this;
            }

            @Override // com.android.i18n.phonenumbers.Phonemetadata.PhoneMetadata
            public Builder setId(String value) {
                super.setId(value);
                return this;
            }

            @Override // com.android.i18n.phonenumbers.Phonemetadata.PhoneMetadata
            public Builder setInternationalPrefix(String value) {
                super.setInternationalPrefix(value);
                return this;
            }
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public boolean hasGeneralDesc() {
            return this.hasGeneralDesc;
        }

        public PhoneNumberDesc getGeneralDesc() {
            return this.generalDesc_;
        }

        public PhoneNumberDesc getGeneralDescBuilder() {
            if (this.generalDesc_ == null) {
                this.generalDesc_ = new PhoneNumberDesc();
            }
            return this.generalDesc_;
        }

        public PhoneMetadata setGeneralDesc(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasGeneralDesc = true;
            this.generalDesc_ = value;
            return this;
        }

        public boolean hasFixedLine() {
            return this.hasFixedLine;
        }

        public PhoneNumberDesc getFixedLine() {
            return this.fixedLine_;
        }

        public PhoneMetadata setFixedLine(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasFixedLine = true;
            this.fixedLine_ = value;
            return this;
        }

        public boolean hasMobile() {
            return this.hasMobile;
        }

        public PhoneNumberDesc getMobile() {
            return this.mobile_;
        }

        public PhoneMetadata setMobile(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasMobile = true;
            this.mobile_ = value;
            return this;
        }

        public boolean hasTollFree() {
            return this.hasTollFree;
        }

        public PhoneNumberDesc getTollFree() {
            return this.tollFree_;
        }

        public PhoneMetadata setTollFree(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasTollFree = true;
            this.tollFree_ = value;
            return this;
        }

        public boolean hasPremiumRate() {
            return this.hasPremiumRate;
        }

        public PhoneNumberDesc getPremiumRate() {
            return this.premiumRate_;
        }

        public PhoneMetadata setPremiumRate(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasPremiumRate = true;
            this.premiumRate_ = value;
            return this;
        }

        public boolean hasSharedCost() {
            return this.hasSharedCost;
        }

        public PhoneNumberDesc getSharedCost() {
            return this.sharedCost_;
        }

        public PhoneMetadata setSharedCost(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasSharedCost = true;
            this.sharedCost_ = value;
            return this;
        }

        public boolean hasPersonalNumber() {
            return this.hasPersonalNumber;
        }

        public PhoneNumberDesc getPersonalNumber() {
            return this.personalNumber_;
        }

        public PhoneMetadata setPersonalNumber(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasPersonalNumber = true;
            this.personalNumber_ = value;
            return this;
        }

        public boolean hasVoip() {
            return this.hasVoip;
        }

        public PhoneNumberDesc getVoip() {
            return this.voip_;
        }

        public PhoneMetadata setVoip(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasVoip = true;
            this.voip_ = value;
            return this;
        }

        public boolean hasPager() {
            return this.hasPager;
        }

        public PhoneNumberDesc getPager() {
            return this.pager_;
        }

        public PhoneMetadata setPager(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasPager = true;
            this.pager_ = value;
            return this;
        }

        public boolean hasUan() {
            return this.hasUan;
        }

        public PhoneNumberDesc getUan() {
            return this.uan_;
        }

        public PhoneMetadata setUan(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasUan = true;
            this.uan_ = value;
            return this;
        }

        public boolean hasEmergency() {
            return this.hasEmergency;
        }

        public PhoneNumberDesc getEmergency() {
            return this.emergency_;
        }

        public PhoneMetadata setEmergency(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasEmergency = true;
            this.emergency_ = value;
            return this;
        }

        public boolean hasVoicemail() {
            return this.hasVoicemail;
        }

        public PhoneNumberDesc getVoicemail() {
            return this.voicemail_;
        }

        public PhoneMetadata setVoicemail(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasVoicemail = true;
            this.voicemail_ = value;
            return this;
        }

        public boolean hasShortCode() {
            return this.hasShortCode;
        }

        public PhoneNumberDesc getShortCode() {
            return this.shortCode_;
        }

        public PhoneMetadata setShortCode(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasShortCode = true;
            this.shortCode_ = value;
            return this;
        }

        public boolean hasStandardRate() {
            return this.hasStandardRate;
        }

        public PhoneNumberDesc getStandardRate() {
            return this.standardRate_;
        }

        public PhoneMetadata setStandardRate(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasStandardRate = true;
            this.standardRate_ = value;
            return this;
        }

        public boolean hasCarrierSpecific() {
            return this.hasCarrierSpecific;
        }

        public PhoneNumberDesc getCarrierSpecific() {
            return this.carrierSpecific_;
        }

        public PhoneMetadata setCarrierSpecific(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasCarrierSpecific = true;
            this.carrierSpecific_ = value;
            return this;
        }

        public boolean hasSmsServices() {
            return this.hasSmsServices;
        }

        public PhoneNumberDesc getSmsServices() {
            return this.smsServices_;
        }

        public PhoneMetadata setSmsServices(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasSmsServices = true;
            this.smsServices_ = value;
            return this;
        }

        public boolean hasNoInternationalDialling() {
            return this.hasNoInternationalDialling;
        }

        public PhoneNumberDesc getNoInternationalDialling() {
            return this.noInternationalDialling_;
        }

        public PhoneMetadata setNoInternationalDialling(PhoneNumberDesc value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hasNoInternationalDialling = true;
            this.noInternationalDialling_ = value;
            return this;
        }

        public boolean hasId() {
            return this.hasId;
        }

        public String getId() {
            return this.id_;
        }

        public PhoneMetadata setId(String value) {
            this.hasId = true;
            this.id_ = value;
            return this;
        }

        public boolean hasCountryCode() {
            return this.hasCountryCode;
        }

        public int getCountryCode() {
            return this.countryCode_;
        }

        public PhoneMetadata setCountryCode(int value) {
            this.hasCountryCode = true;
            this.countryCode_ = value;
            return this;
        }

        public boolean hasInternationalPrefix() {
            return this.hasInternationalPrefix;
        }

        public String getInternationalPrefix() {
            return this.internationalPrefix_;
        }

        public PhoneMetadata setInternationalPrefix(String value) {
            this.hasInternationalPrefix = true;
            this.internationalPrefix_ = value;
            return this;
        }

        public boolean hasPreferredInternationalPrefix() {
            return this.hasPreferredInternationalPrefix;
        }

        public String getPreferredInternationalPrefix() {
            return this.preferredInternationalPrefix_;
        }

        public PhoneMetadata setPreferredInternationalPrefix(String value) {
            this.hasPreferredInternationalPrefix = true;
            this.preferredInternationalPrefix_ = value;
            return this;
        }

        public PhoneMetadata clearPreferredInternationalPrefix() {
            this.hasPreferredInternationalPrefix = false;
            this.preferredInternationalPrefix_ = "";
            return this;
        }

        public boolean hasNationalPrefix() {
            return this.hasNationalPrefix;
        }

        public String getNationalPrefix() {
            return this.nationalPrefix_;
        }

        public PhoneMetadata setNationalPrefix(String value) {
            this.hasNationalPrefix = true;
            this.nationalPrefix_ = value;
            return this;
        }

        public PhoneMetadata clearNationalPrefix() {
            this.hasNationalPrefix = false;
            this.nationalPrefix_ = "";
            return this;
        }

        public boolean hasPreferredExtnPrefix() {
            return this.hasPreferredExtnPrefix;
        }

        public String getPreferredExtnPrefix() {
            return this.preferredExtnPrefix_;
        }

        public PhoneMetadata setPreferredExtnPrefix(String value) {
            this.hasPreferredExtnPrefix = true;
            this.preferredExtnPrefix_ = value;
            return this;
        }

        public PhoneMetadata clearPreferredExtnPrefix() {
            this.hasPreferredExtnPrefix = false;
            this.preferredExtnPrefix_ = "";
            return this;
        }

        public boolean hasNationalPrefixForParsing() {
            return this.hasNationalPrefixForParsing;
        }

        public String getNationalPrefixForParsing() {
            return this.nationalPrefixForParsing_;
        }

        public PhoneMetadata setNationalPrefixForParsing(String value) {
            this.hasNationalPrefixForParsing = true;
            this.nationalPrefixForParsing_ = value;
            return this;
        }

        public boolean hasNationalPrefixTransformRule() {
            return this.hasNationalPrefixTransformRule;
        }

        public String getNationalPrefixTransformRule() {
            return this.nationalPrefixTransformRule_;
        }

        public PhoneMetadata setNationalPrefixTransformRule(String value) {
            this.hasNationalPrefixTransformRule = true;
            this.nationalPrefixTransformRule_ = value;
            return this;
        }

        public PhoneMetadata clearNationalPrefixTransformRule() {
            this.hasNationalPrefixTransformRule = false;
            this.nationalPrefixTransformRule_ = "";
            return this;
        }

        public boolean hasSameMobileAndFixedLinePattern() {
            return this.hasSameMobileAndFixedLinePattern;
        }

        public boolean getSameMobileAndFixedLinePattern() {
            return this.sameMobileAndFixedLinePattern_;
        }

        public PhoneMetadata setSameMobileAndFixedLinePattern(boolean value) {
            this.hasSameMobileAndFixedLinePattern = true;
            this.sameMobileAndFixedLinePattern_ = value;
            return this;
        }

        public PhoneMetadata clearSameMobileAndFixedLinePattern() {
            this.hasSameMobileAndFixedLinePattern = false;
            this.sameMobileAndFixedLinePattern_ = false;
            return this;
        }

        @Deprecated
        public List<NumberFormat> numberFormats() {
            return getNumberFormatList();
        }

        public List<NumberFormat> getNumberFormatList() {
            return this.numberFormat_;
        }

        @Deprecated
        public int numberFormatSize() {
            return getNumberFormatCount();
        }

        public int getNumberFormatCount() {
            return this.numberFormat_.size();
        }

        public NumberFormat getNumberFormat(int index) {
            return this.numberFormat_.get(index);
        }

        public PhoneMetadata addNumberFormat(NumberFormat value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.numberFormat_.add(value);
            return this;
        }

        @Deprecated
        public List<NumberFormat> intlNumberFormats() {
            return getIntlNumberFormatList();
        }

        public List<NumberFormat> getIntlNumberFormatList() {
            return this.intlNumberFormat_;
        }

        @Deprecated
        public int intlNumberFormatSize() {
            return getIntlNumberFormatCount();
        }

        public int getIntlNumberFormatCount() {
            return this.intlNumberFormat_.size();
        }

        public NumberFormat getIntlNumberFormat(int index) {
            return this.intlNumberFormat_.get(index);
        }

        public PhoneMetadata addIntlNumberFormat(NumberFormat value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.intlNumberFormat_.add(value);
            return this;
        }

        public PhoneMetadata clearIntlNumberFormat() {
            this.intlNumberFormat_.clear();
            return this;
        }

        public boolean hasMainCountryForCode() {
            return this.hasMainCountryForCode;
        }

        public boolean isMainCountryForCode() {
            return this.mainCountryForCode_;
        }

        public boolean getMainCountryForCode() {
            return this.mainCountryForCode_;
        }

        public PhoneMetadata setMainCountryForCode(boolean value) {
            this.hasMainCountryForCode = true;
            this.mainCountryForCode_ = value;
            return this;
        }

        public PhoneMetadata clearMainCountryForCode() {
            this.hasMainCountryForCode = false;
            this.mainCountryForCode_ = false;
            return this;
        }

        public boolean hasLeadingDigits() {
            return this.hasLeadingDigits;
        }

        public String getLeadingDigits() {
            return this.leadingDigits_;
        }

        public PhoneMetadata setLeadingDigits(String value) {
            this.hasLeadingDigits = true;
            this.leadingDigits_ = value;
            return this;
        }

        public boolean hasMobileNumberPortableRegion() {
            return this.hasMobileNumberPortableRegion;
        }

        @Deprecated
        public boolean isMobileNumberPortableRegion() {
            return getMobileNumberPortableRegion();
        }

        public boolean getMobileNumberPortableRegion() {
            return this.mobileNumberPortableRegion_;
        }

        public PhoneMetadata setMobileNumberPortableRegion(boolean value) {
            this.hasMobileNumberPortableRegion = true;
            this.mobileNumberPortableRegion_ = value;
            return this;
        }

        public PhoneMetadata clearMobileNumberPortableRegion() {
            this.hasMobileNumberPortableRegion = false;
            this.mobileNumberPortableRegion_ = false;
            return this;
        }

        @Override // java.io.Externalizable
        public void writeExternal(ObjectOutput objectOutput) throws IOException {
            objectOutput.writeBoolean(this.hasGeneralDesc);
            if (this.hasGeneralDesc) {
                this.generalDesc_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasFixedLine);
            if (this.hasFixedLine) {
                this.fixedLine_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasMobile);
            if (this.hasMobile) {
                this.mobile_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasTollFree);
            if (this.hasTollFree) {
                this.tollFree_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasPremiumRate);
            if (this.hasPremiumRate) {
                this.premiumRate_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasSharedCost);
            if (this.hasSharedCost) {
                this.sharedCost_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasPersonalNumber);
            if (this.hasPersonalNumber) {
                this.personalNumber_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasVoip);
            if (this.hasVoip) {
                this.voip_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasPager);
            if (this.hasPager) {
                this.pager_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasUan);
            if (this.hasUan) {
                this.uan_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasEmergency);
            if (this.hasEmergency) {
                this.emergency_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasVoicemail);
            if (this.hasVoicemail) {
                this.voicemail_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasShortCode);
            if (this.hasShortCode) {
                this.shortCode_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasStandardRate);
            if (this.hasStandardRate) {
                this.standardRate_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasCarrierSpecific);
            if (this.hasCarrierSpecific) {
                this.carrierSpecific_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasSmsServices);
            if (this.hasSmsServices) {
                this.smsServices_.writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.hasNoInternationalDialling);
            if (this.hasNoInternationalDialling) {
                this.noInternationalDialling_.writeExternal(objectOutput);
            }
            objectOutput.writeUTF(this.id_);
            objectOutput.writeInt(this.countryCode_);
            objectOutput.writeUTF(this.internationalPrefix_);
            objectOutput.writeBoolean(this.hasPreferredInternationalPrefix);
            if (this.hasPreferredInternationalPrefix) {
                objectOutput.writeUTF(this.preferredInternationalPrefix_);
            }
            objectOutput.writeBoolean(this.hasNationalPrefix);
            if (this.hasNationalPrefix) {
                objectOutput.writeUTF(this.nationalPrefix_);
            }
            objectOutput.writeBoolean(this.hasPreferredExtnPrefix);
            if (this.hasPreferredExtnPrefix) {
                objectOutput.writeUTF(this.preferredExtnPrefix_);
            }
            objectOutput.writeBoolean(this.hasNationalPrefixForParsing);
            if (this.hasNationalPrefixForParsing) {
                objectOutput.writeUTF(this.nationalPrefixForParsing_);
            }
            objectOutput.writeBoolean(this.hasNationalPrefixTransformRule);
            if (this.hasNationalPrefixTransformRule) {
                objectOutput.writeUTF(this.nationalPrefixTransformRule_);
            }
            objectOutput.writeBoolean(this.sameMobileAndFixedLinePattern_);
            int numberFormatSize = numberFormatSize();
            objectOutput.writeInt(numberFormatSize);
            for (int i = 0; i < numberFormatSize; i++) {
                this.numberFormat_.get(i).writeExternal(objectOutput);
            }
            int intlNumberFormatSize = intlNumberFormatSize();
            objectOutput.writeInt(intlNumberFormatSize);
            for (int i2 = 0; i2 < intlNumberFormatSize; i2++) {
                this.intlNumberFormat_.get(i2).writeExternal(objectOutput);
            }
            objectOutput.writeBoolean(this.mainCountryForCode_);
            objectOutput.writeBoolean(this.hasLeadingDigits);
            if (this.hasLeadingDigits) {
                objectOutput.writeUTF(this.leadingDigits_);
            }
            objectOutput.writeBoolean(this.mobileNumberPortableRegion_);
        }

        @Override // java.io.Externalizable
        public void readExternal(ObjectInput objectInput) throws IOException {
            boolean hasDesc = objectInput.readBoolean();
            if (hasDesc) {
                PhoneNumberDesc desc = new PhoneNumberDesc();
                desc.readExternal(objectInput);
                setGeneralDesc(desc);
            }
            boolean hasDesc2 = objectInput.readBoolean();
            if (hasDesc2) {
                PhoneNumberDesc desc2 = new PhoneNumberDesc();
                desc2.readExternal(objectInput);
                setFixedLine(desc2);
            }
            boolean hasDesc3 = objectInput.readBoolean();
            if (hasDesc3) {
                PhoneNumberDesc desc3 = new PhoneNumberDesc();
                desc3.readExternal(objectInput);
                setMobile(desc3);
            }
            boolean hasDesc4 = objectInput.readBoolean();
            if (hasDesc4) {
                PhoneNumberDesc desc4 = new PhoneNumberDesc();
                desc4.readExternal(objectInput);
                setTollFree(desc4);
            }
            boolean hasDesc5 = objectInput.readBoolean();
            if (hasDesc5) {
                PhoneNumberDesc desc5 = new PhoneNumberDesc();
                desc5.readExternal(objectInput);
                setPremiumRate(desc5);
            }
            boolean hasDesc6 = objectInput.readBoolean();
            if (hasDesc6) {
                PhoneNumberDesc desc6 = new PhoneNumberDesc();
                desc6.readExternal(objectInput);
                setSharedCost(desc6);
            }
            boolean hasDesc7 = objectInput.readBoolean();
            if (hasDesc7) {
                PhoneNumberDesc desc7 = new PhoneNumberDesc();
                desc7.readExternal(objectInput);
                setPersonalNumber(desc7);
            }
            boolean hasDesc8 = objectInput.readBoolean();
            if (hasDesc8) {
                PhoneNumberDesc desc8 = new PhoneNumberDesc();
                desc8.readExternal(objectInput);
                setVoip(desc8);
            }
            boolean hasDesc9 = objectInput.readBoolean();
            if (hasDesc9) {
                PhoneNumberDesc desc9 = new PhoneNumberDesc();
                desc9.readExternal(objectInput);
                setPager(desc9);
            }
            boolean hasDesc10 = objectInput.readBoolean();
            if (hasDesc10) {
                PhoneNumberDesc desc10 = new PhoneNumberDesc();
                desc10.readExternal(objectInput);
                setUan(desc10);
            }
            boolean hasDesc11 = objectInput.readBoolean();
            if (hasDesc11) {
                PhoneNumberDesc desc11 = new PhoneNumberDesc();
                desc11.readExternal(objectInput);
                setEmergency(desc11);
            }
            boolean hasDesc12 = objectInput.readBoolean();
            if (hasDesc12) {
                PhoneNumberDesc desc12 = new PhoneNumberDesc();
                desc12.readExternal(objectInput);
                setVoicemail(desc12);
            }
            boolean hasDesc13 = objectInput.readBoolean();
            if (hasDesc13) {
                PhoneNumberDesc desc13 = new PhoneNumberDesc();
                desc13.readExternal(objectInput);
                setShortCode(desc13);
            }
            boolean hasDesc14 = objectInput.readBoolean();
            if (hasDesc14) {
                PhoneNumberDesc desc14 = new PhoneNumberDesc();
                desc14.readExternal(objectInput);
                setStandardRate(desc14);
            }
            boolean hasDesc15 = objectInput.readBoolean();
            if (hasDesc15) {
                PhoneNumberDesc desc15 = new PhoneNumberDesc();
                desc15.readExternal(objectInput);
                setCarrierSpecific(desc15);
            }
            boolean hasDesc16 = objectInput.readBoolean();
            if (hasDesc16) {
                PhoneNumberDesc desc16 = new PhoneNumberDesc();
                desc16.readExternal(objectInput);
                setSmsServices(desc16);
            }
            boolean hasDesc17 = objectInput.readBoolean();
            if (hasDesc17) {
                PhoneNumberDesc desc17 = new PhoneNumberDesc();
                desc17.readExternal(objectInput);
                setNoInternationalDialling(desc17);
            }
            setId(objectInput.readUTF());
            setCountryCode(objectInput.readInt());
            setInternationalPrefix(objectInput.readUTF());
            boolean hasString = objectInput.readBoolean();
            if (hasString) {
                setPreferredInternationalPrefix(objectInput.readUTF());
            }
            boolean hasString2 = objectInput.readBoolean();
            if (hasString2) {
                setNationalPrefix(objectInput.readUTF());
            }
            boolean hasString3 = objectInput.readBoolean();
            if (hasString3) {
                setPreferredExtnPrefix(objectInput.readUTF());
            }
            boolean hasString4 = objectInput.readBoolean();
            if (hasString4) {
                setNationalPrefixForParsing(objectInput.readUTF());
            }
            boolean hasString5 = objectInput.readBoolean();
            if (hasString5) {
                setNationalPrefixTransformRule(objectInput.readUTF());
            }
            setSameMobileAndFixedLinePattern(objectInput.readBoolean());
            int nationalFormatSize = objectInput.readInt();
            for (int i = 0; i < nationalFormatSize; i++) {
                NumberFormat numFormat = new NumberFormat();
                numFormat.readExternal(objectInput);
                this.numberFormat_.add(numFormat);
            }
            int intlNumberFormatSize = objectInput.readInt();
            for (int i2 = 0; i2 < intlNumberFormatSize; i2++) {
                NumberFormat numFormat2 = new NumberFormat();
                numFormat2.readExternal(objectInput);
                this.intlNumberFormat_.add(numFormat2);
            }
            setMainCountryForCode(objectInput.readBoolean());
            boolean hasString6 = objectInput.readBoolean();
            if (hasString6) {
                setLeadingDigits(objectInput.readUTF());
            }
            setMobileNumberPortableRegion(objectInput.readBoolean());
        }
    }

    /* loaded from: classes.dex */
    public static class PhoneMetadataCollection implements Externalizable {
        private static final long serialVersionUID = 1;
        private List<PhoneMetadata> metadata_ = new ArrayList();

        /* loaded from: classes.dex */
        public static final class Builder extends PhoneMetadataCollection {
            public PhoneMetadataCollection build() {
                return this;
            }
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public List<PhoneMetadata> getMetadataList() {
            return this.metadata_;
        }

        public int getMetadataCount() {
            return this.metadata_.size();
        }

        public PhoneMetadataCollection addMetadata(PhoneMetadata value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.metadata_.add(value);
            return this;
        }

        @Override // java.io.Externalizable
        public void writeExternal(ObjectOutput objectOutput) throws IOException {
            int size = getMetadataCount();
            objectOutput.writeInt(size);
            for (int i = 0; i < size; i++) {
                this.metadata_.get(i).writeExternal(objectOutput);
            }
        }

        @Override // java.io.Externalizable
        public void readExternal(ObjectInput objectInput) throws IOException {
            int size = objectInput.readInt();
            for (int i = 0; i < size; i++) {
                PhoneMetadata metadata = new PhoneMetadata();
                metadata.readExternal(objectInput);
                this.metadata_.add(metadata);
            }
        }

        public PhoneMetadataCollection clear() {
            this.metadata_.clear();
            return this;
        }
    }
}
