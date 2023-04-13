package com.android.internal.telephony.cdnr;
/* loaded from: classes.dex */
public final class EriEfData implements EfData {
    private final String mEriText;

    public EriEfData(String str) {
        this.mEriText = str;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public String getServiceProviderName() {
        return this.mEriText;
    }
}
