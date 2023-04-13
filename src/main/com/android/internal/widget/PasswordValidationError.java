package com.android.internal.widget;
/* loaded from: classes5.dex */
public class PasswordValidationError {
    public static final int CONTAINS_INVALID_CHARACTERS = 2;
    public static final int CONTAINS_SEQUENCE = 6;
    public static final int NOT_ENOUGH_DIGITS = 10;
    public static final int NOT_ENOUGH_LETTERS = 7;
    public static final int NOT_ENOUGH_LOWER_CASE = 9;
    public static final int NOT_ENOUGH_NON_DIGITS = 13;
    public static final int NOT_ENOUGH_NON_LETTER = 12;
    public static final int NOT_ENOUGH_SYMBOLS = 11;
    public static final int NOT_ENOUGH_UPPER_CASE = 8;
    public static final int RECENTLY_USED = 14;
    public static final int TOO_LONG = 5;
    public static final int TOO_SHORT = 3;
    public static final int TOO_SHORT_WHEN_ALL_NUMERIC = 4;
    public static final int WEAK_CREDENTIAL_TYPE = 1;
    public final int errorCode;
    public final int requirement;

    public PasswordValidationError(int errorCode) {
        this(errorCode, 0);
    }

    public PasswordValidationError(int errorCode, int requirement) {
        this.errorCode = errorCode;
        this.requirement = requirement;
    }

    public String toString() {
        return errorCodeToString(this.errorCode) + (this.requirement > 0 ? "; required: " + this.requirement : "");
    }

    private static String errorCodeToString(int error) {
        switch (error) {
            case 1:
                return "Weak credential type";
            case 2:
                return "Contains an invalid character";
            case 3:
                return "Password too short";
            case 4:
                return "Password too short";
            case 5:
                return "Password too long";
            case 6:
                return "Sequence too long";
            case 7:
                return "Too few letters";
            case 8:
                return "Too few upper case letters";
            case 9:
                return "Too few lower case letters";
            case 10:
                return "Too few numeric characters";
            case 11:
                return "Too few symbols";
            case 12:
                return "Too few non-letter characters";
            case 13:
                return "Too few non-numeric characters";
            case 14:
                return "Pin or password was recently used";
            default:
                return "Unknown error " + error;
        }
    }
}
