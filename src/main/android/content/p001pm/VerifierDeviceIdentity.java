package android.content.p001pm;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;
/* renamed from: android.content.pm.VerifierDeviceIdentity */
/* loaded from: classes.dex */
public class VerifierDeviceIdentity implements Parcelable {
    private static final int GROUP_SIZE = 4;
    private static final int LONG_SIZE = 13;
    private static final char SEPARATOR = '-';
    private final long mIdentity;
    private final String mIdentityString;
    private static final char[] ENCODE = {DateFormat.CAPITAL_AM_PM, 'B', 'C', 'D', DateFormat.DAY, 'F', 'G', 'H', 'I', 'J', 'K', DateFormat.STANDALONE_MONTH, DateFormat.MONTH, PhoneNumberUtils.WILD, 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7'};
    public static final Parcelable.Creator<VerifierDeviceIdentity> CREATOR = new Parcelable.Creator<VerifierDeviceIdentity>() { // from class: android.content.pm.VerifierDeviceIdentity.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifierDeviceIdentity createFromParcel(Parcel source) {
            return new VerifierDeviceIdentity(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifierDeviceIdentity[] newArray(int size) {
            return new VerifierDeviceIdentity[size];
        }
    };

    public VerifierDeviceIdentity(long identity) {
        this.mIdentity = identity;
        this.mIdentityString = encodeBase32(identity);
    }

    private VerifierDeviceIdentity(Parcel source) {
        long identity = source.readLong();
        this.mIdentity = identity;
        this.mIdentityString = encodeBase32(identity);
    }

    public static VerifierDeviceIdentity generate() {
        SecureRandom sr = new SecureRandom();
        return generate(sr);
    }

    public static VerifierDeviceIdentity generate(Random rng) {
        long identity = rng.nextLong();
        return new VerifierDeviceIdentity(identity);
    }

    private static final String encodeBase32(long input) {
        char[] alphabet = ENCODE;
        char[] encoded = new char[16];
        int index = encoded.length;
        for (int i = 0; i < 13; i++) {
            if (i > 0 && i % 4 == 1) {
                index--;
                encoded[index] = SEPARATOR;
            }
            int group = (int) (31 & input);
            input >>>= 5;
            index--;
            encoded[index] = alphabet[group];
        }
        return String.valueOf(encoded);
    }

    private static final long decodeBase32(byte[] input) throws IllegalArgumentException {
        int value;
        long output = 0;
        int numParsed = 0;
        for (int group : input) {
            if (65 <= group && group <= 90) {
                value = group - 65;
            } else if (50 <= group && group <= 55) {
                value = group - 24;
            } else if (group == 45) {
                continue;
            } else if (97 <= group && group <= 122) {
                value = group - 97;
            } else if (group == 48) {
                value = 14;
            } else if (group == 49) {
                value = 8;
            } else {
                throw new IllegalArgumentException("base base-32 character: " + group);
            }
            output = (output << 5) | value;
            numParsed++;
            if (numParsed == 1) {
                if ((value & 15) != value) {
                    throw new IllegalArgumentException("illegal start character; will overflow");
                }
            } else if (numParsed > 13) {
                throw new IllegalArgumentException("too long; should have 13 characters");
            }
        }
        if (numParsed != 13) {
            throw new IllegalArgumentException("too short; should have 13 characters");
        }
        return output;
    }

    public int hashCode() {
        return (int) this.mIdentity;
    }

    public boolean equals(Object other) {
        if (other instanceof VerifierDeviceIdentity) {
            VerifierDeviceIdentity o = (VerifierDeviceIdentity) other;
            return this.mIdentity == o.mIdentity;
        }
        return false;
    }

    public String toString() {
        return this.mIdentityString;
    }

    public static VerifierDeviceIdentity parse(String deviceIdentity) throws IllegalArgumentException {
        try {
            byte[] input = deviceIdentity.getBytes("US-ASCII");
            return new VerifierDeviceIdentity(decodeBase32(input));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("bad base-32 characters in input");
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mIdentity);
    }
}
