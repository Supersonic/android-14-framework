package com.android.internal.org.bouncycastle.jce.provider;

import java.util.Date;
/* loaded from: classes4.dex */
class CertStatus {
    public static final int UNDETERMINED = 12;
    public static final int UNREVOKED = 11;
    int certStatus = 11;
    Date revocationDate = null;

    public Date getRevocationDate() {
        return this.revocationDate;
    }

    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public int getCertStatus() {
        return this.certStatus;
    }

    public void setCertStatus(int certStatus) {
        this.certStatus = certStatus;
    }
}
