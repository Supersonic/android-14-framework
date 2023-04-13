package com.android.internal.telephony.cdnr;

import android.text.TextUtils;
import com.android.internal.telephony.uicc.RuimRecords;
/* loaded from: classes.dex */
public final class RuimEfData implements EfData {
    private final RuimRecords mRuim;

    public RuimEfData(RuimRecords ruimRecords) {
        this.mRuim = ruimRecords;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public String getServiceProviderName() {
        String serviceProviderName = this.mRuim.getServiceProviderName();
        if (TextUtils.isEmpty(serviceProviderName)) {
            return null;
        }
        return serviceProviderName;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public int getServiceProviderNameDisplayCondition(boolean z) {
        return this.mRuim.getCsimSpnDisplayCondition() ? 2 : 0;
    }
}
