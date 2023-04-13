package com.android.internal.telephony;
/* loaded from: classes.dex */
public class LastCallFailCause {
    public int causeCode;
    public String vendorCause;

    public String toString() {
        return super.toString() + " causeCode: " + this.causeCode + " vendorCause: " + this.vendorCause;
    }
}
