package com.android.i18n.phonenumbers;
/* loaded from: classes.dex */
public class NumberParseException extends Exception {
    private ErrorType errorType;
    private String message;

    /* loaded from: classes.dex */
    public enum ErrorType {
        INVALID_COUNTRY_CODE,
        NOT_A_NUMBER,
        TOO_SHORT_AFTER_IDD,
        TOO_SHORT_NSN,
        TOO_LONG
    }

    public NumberParseException(ErrorType errorType, String message) {
        super(message);
        this.message = message;
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return "Error type: " + this.errorType + ". " + this.message;
    }
}
