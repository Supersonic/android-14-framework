package com.android.internal.telephony.phonenumbers;

import com.android.internal.telephony.PhoneConfigurationManager;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Phonemetadata$PhoneNumberDesc implements Externalizable {
    private static final long serialVersionUID = 1;
    private boolean hasExampleNumber;
    private boolean hasNationalNumberPattern;
    private String nationalNumberPattern_ = PhoneConfigurationManager.SSSS;
    private List<Integer> possibleLength_ = new ArrayList();
    private List<Integer> possibleLengthLocalOnly_ = new ArrayList();
    private String exampleNumber_ = PhoneConfigurationManager.SSSS;

    /* loaded from: classes.dex */
    public static final class Builder extends Phonemetadata$PhoneNumberDesc {
        public Phonemetadata$PhoneNumberDesc build() {
            return this;
        }

        public Builder mergeFrom(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
            if (phonemetadata$PhoneNumberDesc.hasNationalNumberPattern()) {
                setNationalNumberPattern(phonemetadata$PhoneNumberDesc.getNationalNumberPattern());
            }
            for (int i = 0; i < phonemetadata$PhoneNumberDesc.getPossibleLengthCount(); i++) {
                addPossibleLength(phonemetadata$PhoneNumberDesc.getPossibleLength(i));
            }
            for (int i2 = 0; i2 < phonemetadata$PhoneNumberDesc.getPossibleLengthLocalOnlyCount(); i2++) {
                addPossibleLengthLocalOnly(phonemetadata$PhoneNumberDesc.getPossibleLengthLocalOnly(i2));
            }
            if (phonemetadata$PhoneNumberDesc.hasExampleNumber()) {
                setExampleNumber(phonemetadata$PhoneNumberDesc.getExampleNumber());
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

    public Phonemetadata$PhoneNumberDesc setNationalNumberPattern(String str) {
        this.hasNationalNumberPattern = true;
        this.nationalNumberPattern_ = str;
        return this;
    }

    public Phonemetadata$PhoneNumberDesc clearNationalNumberPattern() {
        this.hasNationalNumberPattern = false;
        this.nationalNumberPattern_ = PhoneConfigurationManager.SSSS;
        return this;
    }

    public List<Integer> getPossibleLengthList() {
        return this.possibleLength_;
    }

    public int getPossibleLengthCount() {
        return this.possibleLength_.size();
    }

    public int getPossibleLength(int i) {
        return this.possibleLength_.get(i).intValue();
    }

    public Phonemetadata$PhoneNumberDesc addPossibleLength(int i) {
        this.possibleLength_.add(Integer.valueOf(i));
        return this;
    }

    public Phonemetadata$PhoneNumberDesc clearPossibleLength() {
        this.possibleLength_.clear();
        return this;
    }

    public List<Integer> getPossibleLengthLocalOnlyList() {
        return this.possibleLengthLocalOnly_;
    }

    public int getPossibleLengthLocalOnlyCount() {
        return this.possibleLengthLocalOnly_.size();
    }

    public int getPossibleLengthLocalOnly(int i) {
        return this.possibleLengthLocalOnly_.get(i).intValue();
    }

    public Phonemetadata$PhoneNumberDesc addPossibleLengthLocalOnly(int i) {
        this.possibleLengthLocalOnly_.add(Integer.valueOf(i));
        return this;
    }

    public Phonemetadata$PhoneNumberDesc clearPossibleLengthLocalOnly() {
        this.possibleLengthLocalOnly_.clear();
        return this;
    }

    public boolean hasExampleNumber() {
        return this.hasExampleNumber;
    }

    public String getExampleNumber() {
        return this.exampleNumber_;
    }

    public Phonemetadata$PhoneNumberDesc setExampleNumber(String str) {
        this.hasExampleNumber = true;
        this.exampleNumber_ = str;
        return this;
    }

    public Phonemetadata$PhoneNumberDesc clearExampleNumber() {
        this.hasExampleNumber = false;
        this.exampleNumber_ = PhoneConfigurationManager.SSSS;
        return this;
    }

    public boolean exactlySameAs(Phonemetadata$PhoneNumberDesc phonemetadata$PhoneNumberDesc) {
        return this.nationalNumberPattern_.equals(phonemetadata$PhoneNumberDesc.nationalNumberPattern_) && this.possibleLength_.equals(phonemetadata$PhoneNumberDesc.possibleLength_) && this.possibleLengthLocalOnly_.equals(phonemetadata$PhoneNumberDesc.possibleLengthLocalOnly_) && this.exampleNumber_.equals(phonemetadata$PhoneNumberDesc.exampleNumber_);
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeBoolean(this.hasNationalNumberPattern);
        if (this.hasNationalNumberPattern) {
            objectOutput.writeUTF(this.nationalNumberPattern_);
        }
        int possibleLengthCount = getPossibleLengthCount();
        objectOutput.writeInt(possibleLengthCount);
        for (int i = 0; i < possibleLengthCount; i++) {
            objectOutput.writeInt(this.possibleLength_.get(i).intValue());
        }
        int possibleLengthLocalOnlyCount = getPossibleLengthLocalOnlyCount();
        objectOutput.writeInt(possibleLengthLocalOnlyCount);
        for (int i2 = 0; i2 < possibleLengthLocalOnlyCount; i2++) {
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
        int readInt = objectInput.readInt();
        for (int i = 0; i < readInt; i++) {
            this.possibleLength_.add(Integer.valueOf(objectInput.readInt()));
        }
        int readInt2 = objectInput.readInt();
        for (int i2 = 0; i2 < readInt2; i2++) {
            this.possibleLengthLocalOnly_.add(Integer.valueOf(objectInput.readInt()));
        }
        if (objectInput.readBoolean()) {
            setExampleNumber(objectInput.readUTF());
        }
    }
}
