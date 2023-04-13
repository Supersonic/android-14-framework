package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;
import java.util.Objects;
import java.util.regex.Pattern;
@SystemApi
/* loaded from: classes3.dex */
public final class PhoneNumberRange implements Parcelable {
    public static final Parcelable.Creator<PhoneNumberRange> CREATOR = new Parcelable.Creator<PhoneNumberRange>() { // from class: android.telephony.PhoneNumberRange.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhoneNumberRange createFromParcel(Parcel in) {
            return new PhoneNumberRange(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PhoneNumberRange[] newArray(int size) {
            return new PhoneNumberRange[size];
        }
    };
    private final String mCountryCode;
    private final String mLowerBound;
    private final String mPrefix;
    private final String mUpperBound;

    public PhoneNumberRange(String countryCode, String prefix, String lowerBound, String upperBound) {
        validateLowerAndUpperBounds(lowerBound, upperBound);
        if (!Pattern.matches("[0-9]*", countryCode)) {
            throw new IllegalArgumentException("Country code must be all numeric");
        }
        if (!Pattern.matches("[0-9]*", prefix)) {
            throw new IllegalArgumentException("Prefix must be all numeric");
        }
        this.mCountryCode = countryCode;
        this.mPrefix = prefix;
        this.mLowerBound = lowerBound;
        this.mUpperBound = upperBound;
    }

    private PhoneNumberRange(Parcel in) {
        this.mCountryCode = in.readString();
        this.mPrefix = in.readString();
        this.mLowerBound = in.readString();
        this.mUpperBound = in.readString();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCountryCode);
        dest.writeString(this.mPrefix);
        dest.writeString(this.mLowerBound);
        dest.writeString(this.mUpperBound);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhoneNumberRange that = (PhoneNumberRange) o;
        if (Objects.equals(this.mCountryCode, that.mCountryCode) && Objects.equals(this.mPrefix, that.mPrefix) && Objects.equals(this.mLowerBound, that.mLowerBound) && Objects.equals(this.mUpperBound, that.mUpperBound)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mCountryCode, this.mPrefix, this.mLowerBound, this.mUpperBound);
    }

    public String toString() {
        return "PhoneNumberRange{mCountryCode='" + this.mCountryCode + DateFormat.QUOTE + ", mPrefix='" + this.mPrefix + DateFormat.QUOTE + ", mLowerBound='" + this.mLowerBound + DateFormat.QUOTE + ", mUpperBound='" + this.mUpperBound + DateFormat.QUOTE + '}';
    }

    private void validateLowerAndUpperBounds(String lowerBound, String upperBound) {
        if (lowerBound.length() != upperBound.length()) {
            throw new IllegalArgumentException("Lower and upper bounds must have the same length");
        }
        if (!Pattern.matches("[0-9]*", lowerBound)) {
            throw new IllegalArgumentException("Lower bound must be all numeric");
        }
        if (!Pattern.matches("[0-9]*", upperBound)) {
            throw new IllegalArgumentException("Upper bound must be all numeric");
        }
        if (Integer.parseInt(lowerBound) > Integer.parseInt(upperBound)) {
            throw new IllegalArgumentException("Lower bound must be lower than upper bound");
        }
    }

    public boolean matches(String number) {
        String numberPostfix;
        String normalizedNumber = number.replaceAll("[^0-9]", "");
        String prefixWithCountryCode = this.mCountryCode + this.mPrefix;
        if (normalizedNumber.startsWith(prefixWithCountryCode)) {
            numberPostfix = normalizedNumber.substring(prefixWithCountryCode.length());
        } else {
            String numberPostfix2 = this.mPrefix;
            if (!normalizedNumber.startsWith(numberPostfix2)) {
                return false;
            }
            numberPostfix = normalizedNumber.substring(this.mPrefix.length());
        }
        try {
            int lower = Integer.parseInt(this.mLowerBound);
            int upper = Integer.parseInt(this.mUpperBound);
            int numberToCheck = Integer.parseInt(numberPostfix);
            return numberToCheck <= upper && numberToCheck >= lower;
        } catch (NumberFormatException e) {
            Log.m109e(PhoneNumberRange.class.getSimpleName(), "Invalid bounds or number.", e);
            return false;
        }
    }
}
