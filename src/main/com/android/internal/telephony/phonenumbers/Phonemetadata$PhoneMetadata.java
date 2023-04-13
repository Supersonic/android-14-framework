package com.android.internal.telephony.phonenumbers;

import com.android.internal.telephony.PhoneConfigurationManager;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Phonemetadata$PhoneMetadata implements Externalizable {
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
    private Phonemetadata$PhoneNumberDesc generalDesc_ = null;
    private Phonemetadata$PhoneNumberDesc fixedLine_ = null;
    private Phonemetadata$PhoneNumberDesc mobile_ = null;
    private Phonemetadata$PhoneNumberDesc tollFree_ = null;
    private Phonemetadata$PhoneNumberDesc premiumRate_ = null;
    private Phonemetadata$PhoneNumberDesc sharedCost_ = null;
    private Phonemetadata$PhoneNumberDesc personalNumber_ = null;
    private Phonemetadata$PhoneNumberDesc voip_ = null;
    private Phonemetadata$PhoneNumberDesc pager_ = null;
    private Phonemetadata$PhoneNumberDesc uan_ = null;
    private Phonemetadata$PhoneNumberDesc emergency_ = null;
    private Phonemetadata$PhoneNumberDesc voicemail_ = null;
    private Phonemetadata$PhoneNumberDesc shortCode_ = null;
    private Phonemetadata$PhoneNumberDesc standardRate_ = null;
    private Phonemetadata$PhoneNumberDesc carrierSpecific_ = null;
    private Phonemetadata$PhoneNumberDesc smsServices_ = null;
    private Phonemetadata$PhoneNumberDesc noInternationalDialling_ = null;
    private String id_ = PhoneConfigurationManager.SSSS;
    private int countryCode_ = 0;
    private String internationalPrefix_ = PhoneConfigurationManager.SSSS;
    private String preferredInternationalPrefix_ = PhoneConfigurationManager.SSSS;
    private String nationalPrefix_ = PhoneConfigurationManager.SSSS;
    private String preferredExtnPrefix_ = PhoneConfigurationManager.SSSS;
    private String nationalPrefixForParsing_ = PhoneConfigurationManager.SSSS;
    private String nationalPrefixTransformRule_ = PhoneConfigurationManager.SSSS;
    private boolean sameMobileAndFixedLinePattern_ = false;
    private List<Phonemetadata$NumberFormat> numberFormat_ = new ArrayList();
    private List<Phonemetadata$NumberFormat> intlNumberFormat_ = new ArrayList();
    private boolean mainCountryForCode_ = false;
    private String leadingDigits_ = PhoneConfigurationManager.SSSS;
    private boolean mobileNumberPortableRegion_ = false;

    /* loaded from: classes.dex */
    public static final class Builder extends Phonemetadata$PhoneMetadata {
        public Phonemetadata$PhoneMetadata build() {
            return this;
        }

        @Override // com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata
        public Builder setId(String str) {
            super.setId(str);
            return this;
        }

        @Override // com.android.internal.telephony.phonenumbers.Phonemetadata$PhoneMetadata
        public Builder setInternationalPrefix(String str) {
            super.setInternationalPrefix(str);
            return this;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean hasGeneralDesc() {
        return this.hasGeneralDesc;
    }

    public Phonemetadata$PhoneNumberDesc getGeneralDesc() {
        return this.generalDesc_;
    }

    public Phonemetadata$PhoneNumberDesc getGeneralDescBuilder() {
        if (this.generalDesc_ == null) {
            this.generalDesc_ = new Phonemetadata$PhoneNumberDesc();
        }
        return this.generalDesc_;
    }

    public Phonemetadata$PhoneMetadata setGeneralDesc(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasGeneralDesc = true;
        this.generalDesc_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasFixedLine() {
        return this.hasFixedLine;
    }

    public Phonemetadata$PhoneNumberDesc getFixedLine() {
        return this.fixedLine_;
    }

    public Phonemetadata$PhoneMetadata setFixedLine(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasFixedLine = true;
        this.fixedLine_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasMobile() {
        return this.hasMobile;
    }

    public Phonemetadata$PhoneNumberDesc getMobile() {
        return this.mobile_;
    }

    public Phonemetadata$PhoneMetadata setMobile(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasMobile = true;
        this.mobile_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasTollFree() {
        return this.hasTollFree;
    }

    public Phonemetadata$PhoneNumberDesc getTollFree() {
        return this.tollFree_;
    }

    public Phonemetadata$PhoneMetadata setTollFree(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasTollFree = true;
        this.tollFree_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasPremiumRate() {
        return this.hasPremiumRate;
    }

    public Phonemetadata$PhoneNumberDesc getPremiumRate() {
        return this.premiumRate_;
    }

    public Phonemetadata$PhoneMetadata setPremiumRate(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasPremiumRate = true;
        this.premiumRate_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasSharedCost() {
        return this.hasSharedCost;
    }

    public Phonemetadata$PhoneNumberDesc getSharedCost() {
        return this.sharedCost_;
    }

    public Phonemetadata$PhoneMetadata setSharedCost(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasSharedCost = true;
        this.sharedCost_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasPersonalNumber() {
        return this.hasPersonalNumber;
    }

    public Phonemetadata$PhoneNumberDesc getPersonalNumber() {
        return this.personalNumber_;
    }

    public Phonemetadata$PhoneMetadata setPersonalNumber(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasPersonalNumber = true;
        this.personalNumber_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasVoip() {
        return this.hasVoip;
    }

    public Phonemetadata$PhoneNumberDesc getVoip() {
        return this.voip_;
    }

    public Phonemetadata$PhoneMetadata setVoip(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasVoip = true;
        this.voip_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasPager() {
        return this.hasPager;
    }

    public Phonemetadata$PhoneNumberDesc getPager() {
        return this.pager_;
    }

    public Phonemetadata$PhoneMetadata setPager(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasPager = true;
        this.pager_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasUan() {
        return this.hasUan;
    }

    public Phonemetadata$PhoneNumberDesc getUan() {
        return this.uan_;
    }

    public Phonemetadata$PhoneMetadata setUan(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasUan = true;
        this.uan_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasEmergency() {
        return this.hasEmergency;
    }

    public Phonemetadata$PhoneNumberDesc getEmergency() {
        return this.emergency_;
    }

    public Phonemetadata$PhoneMetadata setEmergency(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasEmergency = true;
        this.emergency_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasVoicemail() {
        return this.hasVoicemail;
    }

    public Phonemetadata$PhoneNumberDesc getVoicemail() {
        return this.voicemail_;
    }

    public Phonemetadata$PhoneMetadata setVoicemail(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasVoicemail = true;
        this.voicemail_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasShortCode() {
        return this.hasShortCode;
    }

    public Phonemetadata$PhoneNumberDesc getShortCode() {
        return this.shortCode_;
    }

    public Phonemetadata$PhoneMetadata setShortCode(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasShortCode = true;
        this.shortCode_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasStandardRate() {
        return this.hasStandardRate;
    }

    public Phonemetadata$PhoneNumberDesc getStandardRate() {
        return this.standardRate_;
    }

    public Phonemetadata$PhoneMetadata setStandardRate(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasStandardRate = true;
        this.standardRate_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasCarrierSpecific() {
        return this.hasCarrierSpecific;
    }

    public Phonemetadata$PhoneNumberDesc getCarrierSpecific() {
        return this.carrierSpecific_;
    }

    public Phonemetadata$PhoneMetadata setCarrierSpecific(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasCarrierSpecific = true;
        this.carrierSpecific_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasSmsServices() {
        return this.hasSmsServices;
    }

    public Phonemetadata$PhoneNumberDesc getSmsServices() {
        return this.smsServices_;
    }

    public Phonemetadata$PhoneMetadata setSmsServices(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasSmsServices = true;
        this.smsServices_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasNoInternationalDialling() {
        return this.hasNoInternationalDialling;
    }

    public Phonemetadata$PhoneNumberDesc getNoInternationalDialling() {
        return this.noInternationalDialling_;
    }

    public Phonemetadata$PhoneMetadata setNoInternationalDialling(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        phonemetadata$PhoneNumberDesc.getClass();
        this.hasNoInternationalDialling = true;
        this.noInternationalDialling_ = phonemetadata$PhoneNumberDesc;
        return this;
    }

    public boolean hasId() {
        return this.hasId;
    }

    public String getId() {
        return this.id_;
    }

    public Phonemetadata$PhoneMetadata setId(String str) {
        this.hasId = true;
        this.id_ = str;
        return this;
    }

    public boolean hasCountryCode() {
        return this.hasCountryCode;
    }

    public int getCountryCode() {
        return this.countryCode_;
    }

    public Phonemetadata$PhoneMetadata setCountryCode(int i) {
        this.hasCountryCode = true;
        this.countryCode_ = i;
        return this;
    }

    public boolean hasInternationalPrefix() {
        return this.hasInternationalPrefix;
    }

    public String getInternationalPrefix() {
        return this.internationalPrefix_;
    }

    public Phonemetadata$PhoneMetadata setInternationalPrefix(String str) {
        this.hasInternationalPrefix = true;
        this.internationalPrefix_ = str;
        return this;
    }

    public boolean hasPreferredInternationalPrefix() {
        return this.hasPreferredInternationalPrefix;
    }

    public String getPreferredInternationalPrefix() {
        return this.preferredInternationalPrefix_;
    }

    public Phonemetadata$PhoneMetadata setPreferredInternationalPrefix(String str) {
        this.hasPreferredInternationalPrefix = true;
        this.preferredInternationalPrefix_ = str;
        return this;
    }

    public Phonemetadata$PhoneMetadata clearPreferredInternationalPrefix() {
        this.hasPreferredInternationalPrefix = false;
        this.preferredInternationalPrefix_ = PhoneConfigurationManager.SSSS;
        return this;
    }

    public boolean hasNationalPrefix() {
        return this.hasNationalPrefix;
    }

    public String getNationalPrefix() {
        return this.nationalPrefix_;
    }

    public Phonemetadata$PhoneMetadata setNationalPrefix(String str) {
        this.hasNationalPrefix = true;
        this.nationalPrefix_ = str;
        return this;
    }

    public Phonemetadata$PhoneMetadata clearNationalPrefix() {
        this.hasNationalPrefix = false;
        this.nationalPrefix_ = PhoneConfigurationManager.SSSS;
        return this;
    }

    public boolean hasPreferredExtnPrefix() {
        return this.hasPreferredExtnPrefix;
    }

    public String getPreferredExtnPrefix() {
        return this.preferredExtnPrefix_;
    }

    public Phonemetadata$PhoneMetadata setPreferredExtnPrefix(String str) {
        this.hasPreferredExtnPrefix = true;
        this.preferredExtnPrefix_ = str;
        return this;
    }

    public Phonemetadata$PhoneMetadata clearPreferredExtnPrefix() {
        this.hasPreferredExtnPrefix = false;
        this.preferredExtnPrefix_ = PhoneConfigurationManager.SSSS;
        return this;
    }

    public boolean hasNationalPrefixForParsing() {
        return this.hasNationalPrefixForParsing;
    }

    public String getNationalPrefixForParsing() {
        return this.nationalPrefixForParsing_;
    }

    public Phonemetadata$PhoneMetadata setNationalPrefixForParsing(String str) {
        this.hasNationalPrefixForParsing = true;
        this.nationalPrefixForParsing_ = str;
        return this;
    }

    public boolean hasNationalPrefixTransformRule() {
        return this.hasNationalPrefixTransformRule;
    }

    public String getNationalPrefixTransformRule() {
        return this.nationalPrefixTransformRule_;
    }

    public Phonemetadata$PhoneMetadata setNationalPrefixTransformRule(String str) {
        this.hasNationalPrefixTransformRule = true;
        this.nationalPrefixTransformRule_ = str;
        return this;
    }

    public Phonemetadata$PhoneMetadata clearNationalPrefixTransformRule() {
        this.hasNationalPrefixTransformRule = false;
        this.nationalPrefixTransformRule_ = PhoneConfigurationManager.SSSS;
        return this;
    }

    public boolean hasSameMobileAndFixedLinePattern() {
        return this.hasSameMobileAndFixedLinePattern;
    }

    public boolean getSameMobileAndFixedLinePattern() {
        return this.sameMobileAndFixedLinePattern_;
    }

    public Phonemetadata$PhoneMetadata setSameMobileAndFixedLinePattern(boolean z) {
        this.hasSameMobileAndFixedLinePattern = true;
        this.sameMobileAndFixedLinePattern_ = z;
        return this;
    }

    public Phonemetadata$PhoneMetadata clearSameMobileAndFixedLinePattern() {
        this.hasSameMobileAndFixedLinePattern = false;
        this.sameMobileAndFixedLinePattern_ = false;
        return this;
    }

    @Deprecated
    public List<Phonemetadata$NumberFormat> numberFormats() {
        return getNumberFormatList();
    }

    public List<Phonemetadata$NumberFormat> getNumberFormatList() {
        return this.numberFormat_;
    }

    @Deprecated
    public int numberFormatSize() {
        return getNumberFormatCount();
    }

    public int getNumberFormatCount() {
        return this.numberFormat_.size();
    }

    public Phonemetadata$NumberFormat getNumberFormat(int i) {
        return this.numberFormat_.get(i);
    }

    public Phonemetadata$PhoneMetadata addNumberFormat(Phonemetadata$NumberFormat phonemetadata$NumberFormat) {
        phonemetadata$NumberFormat.getClass();
        this.numberFormat_.add(phonemetadata$NumberFormat);
        return this;
    }

    @Deprecated
    public List<Phonemetadata$NumberFormat> intlNumberFormats() {
        return getIntlNumberFormatList();
    }

    public List<Phonemetadata$NumberFormat> getIntlNumberFormatList() {
        return this.intlNumberFormat_;
    }

    @Deprecated
    public int intlNumberFormatSize() {
        return getIntlNumberFormatCount();
    }

    public int getIntlNumberFormatCount() {
        return this.intlNumberFormat_.size();
    }

    public Phonemetadata$NumberFormat getIntlNumberFormat(int i) {
        return this.intlNumberFormat_.get(i);
    }

    public Phonemetadata$PhoneMetadata addIntlNumberFormat(Phonemetadata$NumberFormat phonemetadata$NumberFormat) {
        phonemetadata$NumberFormat.getClass();
        this.intlNumberFormat_.add(phonemetadata$NumberFormat);
        return this;
    }

    public Phonemetadata$PhoneMetadata clearIntlNumberFormat() {
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

    public Phonemetadata$PhoneMetadata setMainCountryForCode(boolean z) {
        this.hasMainCountryForCode = true;
        this.mainCountryForCode_ = z;
        return this;
    }

    public Phonemetadata$PhoneMetadata clearMainCountryForCode() {
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

    public Phonemetadata$PhoneMetadata setLeadingDigits(String str) {
        this.hasLeadingDigits = true;
        this.leadingDigits_ = str;
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

    public Phonemetadata$PhoneMetadata setMobileNumberPortableRegion(boolean z) {
        this.hasMobileNumberPortableRegion = true;
        this.mobileNumberPortableRegion_ = z;
        return this;
    }

    public Phonemetadata$PhoneMetadata clearMobileNumberPortableRegion() {
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
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc.readExternal(objectInput);
            setGeneralDesc(phonemetadata$PhoneNumberDesc);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc2 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc2.readExternal(objectInput);
            setFixedLine(phonemetadata$PhoneNumberDesc2);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc3 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc3.readExternal(objectInput);
            setMobile(phonemetadata$PhoneNumberDesc3);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc4 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc4.readExternal(objectInput);
            setTollFree(phonemetadata$PhoneNumberDesc4);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc5 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc5.readExternal(objectInput);
            setPremiumRate(phonemetadata$PhoneNumberDesc5);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc6 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc6.readExternal(objectInput);
            setSharedCost(phonemetadata$PhoneNumberDesc6);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc7 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc7.readExternal(objectInput);
            setPersonalNumber(phonemetadata$PhoneNumberDesc7);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc8 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc8.readExternal(objectInput);
            setVoip(phonemetadata$PhoneNumberDesc8);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc9 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc9.readExternal(objectInput);
            setPager(phonemetadata$PhoneNumberDesc9);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc10 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc10.readExternal(objectInput);
            setUan(phonemetadata$PhoneNumberDesc10);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc11 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc11.readExternal(objectInput);
            setEmergency(phonemetadata$PhoneNumberDesc11);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc12 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc12.readExternal(objectInput);
            setVoicemail(phonemetadata$PhoneNumberDesc12);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc13 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc13.readExternal(objectInput);
            setShortCode(phonemetadata$PhoneNumberDesc13);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc14 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc14.readExternal(objectInput);
            setStandardRate(phonemetadata$PhoneNumberDesc14);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc15 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc15.readExternal(objectInput);
            setCarrierSpecific(phonemetadata$PhoneNumberDesc15);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc16 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc16.readExternal(objectInput);
            setSmsServices(phonemetadata$PhoneNumberDesc16);
        }
        if (objectInput.readBoolean()) {
            Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc17 = new Phonemetadata$PhoneNumberDesc();
            phonemetadata$PhoneNumberDesc17.readExternal(objectInput);
            setNoInternationalDialling(phonemetadata$PhoneNumberDesc17);
        }
        setId(objectInput.readUTF());
        setCountryCode(objectInput.readInt());
        setInternationalPrefix(objectInput.readUTF());
        if (objectInput.readBoolean()) {
            setPreferredInternationalPrefix(objectInput.readUTF());
        }
        if (objectInput.readBoolean()) {
            setNationalPrefix(objectInput.readUTF());
        }
        if (objectInput.readBoolean()) {
            setPreferredExtnPrefix(objectInput.readUTF());
        }
        if (objectInput.readBoolean()) {
            setNationalPrefixForParsing(objectInput.readUTF());
        }
        if (objectInput.readBoolean()) {
            setNationalPrefixTransformRule(objectInput.readUTF());
        }
        setSameMobileAndFixedLinePattern(objectInput.readBoolean());
        int readInt = objectInput.readInt();
        for (int i = 0; i < readInt; i++) {
            Phonemetadata$NumberFormat phonemetadata$NumberFormat = new Phonemetadata$NumberFormat();
            phonemetadata$NumberFormat.readExternal(objectInput);
            this.numberFormat_.add(phonemetadata$NumberFormat);
        }
        int readInt2 = objectInput.readInt();
        for (int i2 = 0; i2 < readInt2; i2++) {
            Phonemetadata$NumberFormat phonemetadata$NumberFormat2 = new Phonemetadata$NumberFormat();
            phonemetadata$NumberFormat2.readExternal(objectInput);
            this.intlNumberFormat_.add(phonemetadata$NumberFormat2);
        }
        setMainCountryForCode(objectInput.readBoolean());
        if (objectInput.readBoolean()) {
            setLeadingDigits(objectInput.readUTF());
        }
        setMobileNumberPortableRegion(objectInput.readBoolean());
    }
}
