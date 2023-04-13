package com.android.internal.telephony.phonenumbers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Phonemetadata$PhoneMetadataCollection implements Externalizable {
    private static final long serialVersionUID = 1;
    private List<Phonemetadata$PhoneMetadata> metadata_ = new ArrayList();

    /* loaded from: classes.dex */
    public static final class Builder extends Phonemetadata$PhoneMetadataCollection {
        public Phonemetadata$PhoneMetadataCollection build() {
            return this;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public List<Phonemetadata$PhoneMetadata> getMetadataList() {
        return this.metadata_;
    }

    public int getMetadataCount() {
        return this.metadata_.size();
    }

    public Phonemetadata$PhoneMetadataCollection addMetadata(Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata) {
        phonemetadata$PhoneMetadata.getClass();
        this.metadata_.add(phonemetadata$PhoneMetadata);
        return this;
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        int metadataCount = getMetadataCount();
        objectOutput.writeInt(metadataCount);
        for (int i = 0; i < metadataCount; i++) {
            this.metadata_.get(i).writeExternal(objectOutput);
        }
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput objectInput) throws IOException {
        int readInt = objectInput.readInt();
        for (int i = 0; i < readInt; i++) {
            Phonemetadata$PhoneMetadata phonemetadata$PhoneMetadata = new Phonemetadata$PhoneMetadata();
            phonemetadata$PhoneMetadata.readExternal(objectInput);
            this.metadata_.add(phonemetadata$PhoneMetadata);
        }
    }

    public Phonemetadata$PhoneMetadataCollection clear() {
        this.metadata_.clear();
        return this;
    }
}
