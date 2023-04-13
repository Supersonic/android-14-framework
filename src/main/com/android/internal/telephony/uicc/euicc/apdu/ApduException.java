package com.android.internal.telephony.uicc.euicc.apdu;
/* loaded from: classes.dex */
public class ApduException extends Exception {
    private final int mApduStatus;

    public ApduException(int i) {
        this.mApduStatus = i;
    }

    public ApduException(String str) {
        super(str);
        this.mApduStatus = 0;
    }

    public int getApduStatus() {
        return this.mApduStatus;
    }

    public String getStatusHex() {
        return Integer.toHexString(this.mApduStatus);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return super.getMessage() + " (apduStatus=" + getStatusHex() + ")";
    }
}
