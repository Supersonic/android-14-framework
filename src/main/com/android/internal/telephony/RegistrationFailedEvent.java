package com.android.internal.telephony;

import android.telephony.CellIdentity;
/* loaded from: classes.dex */
public class RegistrationFailedEvent {
    public final int additionalCauseCode;
    public final int causeCode;
    public final CellIdentity cellIdentity;
    public final String chosenPlmn;
    public final int domain;

    public RegistrationFailedEvent(CellIdentity cellIdentity, String str, int i, int i2, int i3) {
        this.cellIdentity = cellIdentity;
        this.chosenPlmn = str;
        this.domain = i;
        this.causeCode = i2;
        this.additionalCauseCode = i3;
    }

    public String toString() {
        return "{CellIdentity=" + this.cellIdentity + ", chosenPlmn=" + this.chosenPlmn + ", domain=" + this.domain + ", causeCode=" + this.causeCode + ", additionalCauseCode=" + this.additionalCauseCode;
    }
}
