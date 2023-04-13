package com.android.internal.telephony.cdnr;

import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public final class BrandOverrideEfData implements EfData {
    private final String mRegisteredPlmn;
    private final String mSpn;

    @Override // com.android.internal.telephony.cdnr.EfData
    public int getServiceProviderNameDisplayCondition(boolean z) {
        return 2;
    }

    public BrandOverrideEfData(String str, String str2) {
        this.mSpn = str;
        this.mRegisteredPlmn = str2;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public String getServiceProviderName() {
        return this.mSpn;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<String> getServiceProviderDisplayInformation() {
        return Arrays.asList(this.mRegisteredPlmn);
    }
}
