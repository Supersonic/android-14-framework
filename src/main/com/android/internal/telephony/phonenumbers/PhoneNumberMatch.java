package com.android.internal.telephony.phonenumbers;

import java.util.Arrays;
/* loaded from: classes.dex */
public final class PhoneNumberMatch {
    private final Phonenumber$PhoneNumber number;
    private final String rawString;
    private final int start;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PhoneNumberMatch(int i, String str, Phonenumber$PhoneNumber phonenumber$PhoneNumber) {
        if (i < 0) {
            throw new IllegalArgumentException("Start index must be >= 0.");
        }
        if (str == null || phonenumber$PhoneNumber == null) {
            throw null;
        }
        this.start = i;
        this.rawString = str;
        this.number = phonenumber$PhoneNumber;
    }

    public Phonenumber$PhoneNumber number() {
        return this.number;
    }

    public int start() {
        return this.start;
    }

    public int end() {
        return this.start + this.rawString.length();
    }

    public String rawString() {
        return this.rawString;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.start), this.rawString, this.number});
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PhoneNumberMatch) {
            PhoneNumberMatch phoneNumberMatch = (PhoneNumberMatch) obj;
            return this.rawString.equals(phoneNumberMatch.rawString) && this.start == phoneNumberMatch.start && this.number.equals(phoneNumberMatch.number);
        }
        return false;
    }

    public String toString() {
        return "PhoneNumberMatch [" + start() + "," + end() + ") " + this.rawString;
    }
}
