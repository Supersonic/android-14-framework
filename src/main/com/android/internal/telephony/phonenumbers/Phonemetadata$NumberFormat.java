package com.android.internal.telephony.phonenumbers;

import com.android.internal.telephony.PhoneConfigurationManager;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Phonemetadata$NumberFormat implements Externalizable {
    private static final long serialVersionUID = 1;
    private boolean hasDomesticCarrierCodeFormattingRule;
    private boolean hasFormat;
    private boolean hasNationalPrefixFormattingRule;
    private boolean hasNationalPrefixOptionalWhenFormatting;
    private boolean hasPattern;
    private String pattern_ = PhoneConfigurationManager.SSSS;
    private String format_ = PhoneConfigurationManager.SSSS;
    private List<String> leadingDigitsPattern_ = new ArrayList();
    private String nationalPrefixFormattingRule_ = PhoneConfigurationManager.SSSS;
    private boolean nationalPrefixOptionalWhenFormatting_ = false;
    private String domesticCarrierCodeFormattingRule_ = PhoneConfigurationManager.SSSS;

    /* loaded from: classes.dex */
    public static final class Builder extends Phonemetadata$NumberFormat {
        public Phonemetadata$NumberFormat build() {
            return this;
        }

        public Builder mergeFrom(Phonemetadata$NumberFormat phonemetadata$NumberFormat) {
            if (phonemetadata$NumberFormat.hasPattern()) {
                setPattern(phonemetadata$NumberFormat.getPattern());
            }
            if (phonemetadata$NumberFormat.hasFormat()) {
                setFormat(phonemetadata$NumberFormat.getFormat());
            }
            for (int i = 0; i < phonemetadata$NumberFormat.leadingDigitsPatternSize(); i++) {
                addLeadingDigitsPattern(phonemetadata$NumberFormat.getLeadingDigitsPattern(i));
            }
            if (phonemetadata$NumberFormat.hasNationalPrefixFormattingRule()) {
                setNationalPrefixFormattingRule(phonemetadata$NumberFormat.getNationalPrefixFormattingRule());
            }
            if (phonemetadata$NumberFormat.hasDomesticCarrierCodeFormattingRule()) {
                setDomesticCarrierCodeFormattingRule(phonemetadata$NumberFormat.getDomesticCarrierCodeFormattingRule());
            }
            if (phonemetadata$NumberFormat.hasNationalPrefixOptionalWhenFormatting()) {
                setNationalPrefixOptionalWhenFormatting(phonemetadata$NumberFormat.getNationalPrefixOptionalWhenFormatting());
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

    public Phonemetadata$NumberFormat setPattern(String str) {
        this.hasPattern = true;
        this.pattern_ = str;
        return this;
    }

    public boolean hasFormat() {
        return this.hasFormat;
    }

    public String getFormat() {
        return this.format_;
    }

    public Phonemetadata$NumberFormat setFormat(String str) {
        this.hasFormat = true;
        this.format_ = str;
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

    public String getLeadingDigitsPattern(int i) {
        return this.leadingDigitsPattern_.get(i);
    }

    public Phonemetadata$NumberFormat addLeadingDigitsPattern(String str) {
        str.getClass();
        this.leadingDigitsPattern_.add(str);
        return this;
    }

    public boolean hasNationalPrefixFormattingRule() {
        return this.hasNationalPrefixFormattingRule;
    }

    public String getNationalPrefixFormattingRule() {
        return this.nationalPrefixFormattingRule_;
    }

    public Phonemetadata$NumberFormat setNationalPrefixFormattingRule(String str) {
        this.hasNationalPrefixFormattingRule = true;
        this.nationalPrefixFormattingRule_ = str;
        return this;
    }

    public Phonemetadata$NumberFormat clearNationalPrefixFormattingRule() {
        this.hasNationalPrefixFormattingRule = false;
        this.nationalPrefixFormattingRule_ = PhoneConfigurationManager.SSSS;
        return this;
    }

    public boolean hasNationalPrefixOptionalWhenFormatting() {
        return this.hasNationalPrefixOptionalWhenFormatting;
    }

    public boolean getNationalPrefixOptionalWhenFormatting() {
        return this.nationalPrefixOptionalWhenFormatting_;
    }

    public Phonemetadata$NumberFormat setNationalPrefixOptionalWhenFormatting(boolean z) {
        this.hasNationalPrefixOptionalWhenFormatting = true;
        this.nationalPrefixOptionalWhenFormatting_ = z;
        return this;
    }

    public boolean hasDomesticCarrierCodeFormattingRule() {
        return this.hasDomesticCarrierCodeFormattingRule;
    }

    public String getDomesticCarrierCodeFormattingRule() {
        return this.domesticCarrierCodeFormattingRule_;
    }

    public Phonemetadata$NumberFormat setDomesticCarrierCodeFormattingRule(String str) {
        this.hasDomesticCarrierCodeFormattingRule = true;
        this.domesticCarrierCodeFormattingRule_ = str;
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
        int readInt = objectInput.readInt();
        for (int i = 0; i < readInt; i++) {
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
