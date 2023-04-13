package com.android.internal.telephony.uicc;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class AdnCapacity implements Parcelable {
    public static final Parcelable.Creator<AdnCapacity> CREATOR = new Parcelable.Creator<AdnCapacity>() { // from class: com.android.internal.telephony.uicc.AdnCapacity.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AdnCapacity createFromParcel(Parcel parcel) {
            return new AdnCapacity(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AdnCapacity[] newArray(int i) {
            return new AdnCapacity[i];
        }
    };
    private int mHashCode;
    private int mMaxAdnCount;
    private int mMaxAnrCount;
    private int mMaxAnrLength;
    private int mMaxEmailCount;
    private int mMaxEmailLength;
    private int mMaxNameLength;
    private int mMaxNumberLength;
    private int mUsedAdnCount;
    private int mUsedAnrCount;
    private int mUsedEmailCount;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public AdnCapacity(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
        this.mHashCode = 0;
        this.mMaxAdnCount = i;
        this.mUsedAdnCount = i2;
        this.mMaxEmailCount = i3;
        this.mUsedEmailCount = i4;
        this.mMaxAnrCount = i5;
        this.mUsedAnrCount = i6;
        this.mMaxNameLength = i7;
        this.mMaxNumberLength = i8;
        this.mMaxEmailLength = i9;
        this.mMaxAnrLength = i10;
    }

    public AdnCapacity() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public int getMaxAdnCount() {
        return this.mMaxAdnCount;
    }

    public int getUsedAdnCount() {
        return this.mUsedAdnCount;
    }

    public int getMaxEmailCount() {
        return this.mMaxEmailCount;
    }

    public int getUsedEmailCount() {
        return this.mUsedEmailCount;
    }

    public int getMaxAnrCount() {
        return this.mMaxAnrCount;
    }

    public int getUsedAnrCount() {
        return this.mUsedAnrCount;
    }

    public int getMaxNameLength() {
        return this.mMaxNameLength;
    }

    public int getMaxNumberLength() {
        return this.mMaxNumberLength;
    }

    public int getMaxEmailLength() {
        return this.mMaxEmailLength;
    }

    public int getMaxAnrLength() {
        return this.mMaxAnrLength;
    }

    public boolean isSimFull() {
        return this.mMaxAdnCount == this.mUsedAdnCount;
    }

    public boolean isSimEmpty() {
        return this.mUsedAdnCount == 0;
    }

    public boolean isSimValid() {
        return this.mMaxAdnCount > 0;
    }

    public String toString() {
        return "getAdnRecordsCapacity : max adn=" + this.mMaxAdnCount + ", used adn=" + this.mUsedAdnCount + ", max email=" + this.mMaxEmailCount + ", used email=" + this.mUsedEmailCount + ", max anr=" + this.mMaxAnrCount + ", used anr=" + this.mUsedAnrCount + ", max name length=" + this.mMaxNameLength + ", max number length =" + this.mMaxNumberLength + ", max email length =" + this.mMaxEmailLength + ", max anr length =" + this.mMaxAnrLength;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mMaxAdnCount);
        parcel.writeInt(this.mUsedAdnCount);
        parcel.writeInt(this.mMaxEmailCount);
        parcel.writeInt(this.mUsedEmailCount);
        parcel.writeInt(this.mMaxAnrCount);
        parcel.writeInt(this.mUsedAnrCount);
        parcel.writeInt(this.mMaxNameLength);
        parcel.writeInt(this.mMaxNumberLength);
        parcel.writeInt(this.mMaxEmailLength);
        parcel.writeInt(this.mMaxAnrLength);
    }

    public boolean equals(Object obj) {
        if (obj instanceof AdnCapacity) {
            AdnCapacity adnCapacity = (AdnCapacity) obj;
            return adnCapacity.getMaxAdnCount() == this.mMaxAdnCount && adnCapacity.getUsedAdnCount() == this.mUsedAdnCount && adnCapacity.getMaxEmailCount() == this.mMaxEmailCount && adnCapacity.getUsedEmailCount() == this.mUsedEmailCount && adnCapacity.getMaxAnrCount() == this.mMaxAnrCount && adnCapacity.getUsedAnrCount() == this.mUsedAnrCount && adnCapacity.getMaxNameLength() == this.mMaxNameLength && adnCapacity.getMaxNumberLength() == this.mMaxNumberLength && adnCapacity.getMaxEmailLength() == this.mMaxEmailLength && adnCapacity.getMaxAnrLength() == this.mMaxAnrLength;
        }
        return false;
    }

    public int hashCode() {
        if (this.mHashCode == 0) {
            this.mHashCode = (((((((((((((((((this.mMaxAdnCount * 31) + this.mUsedAdnCount) * 31) + this.mMaxEmailCount) * 31) + this.mUsedEmailCount) * 31) + this.mMaxAnrCount) * 31) + this.mUsedAnrCount) * 31) + this.mMaxNameLength) * 31) + this.mMaxNumberLength) * 31) + this.mMaxEmailLength) * 31) + this.mMaxAnrLength;
        }
        return this.mHashCode;
    }
}
