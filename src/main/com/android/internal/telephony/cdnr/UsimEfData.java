package com.android.internal.telephony.cdnr;

import android.text.TextUtils;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.SIMRecords;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public final class UsimEfData implements EfData {
    private final SIMRecords mUsim;

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<IccRecords.OperatorPlmnInfo> getOperatorPlmnList() {
        return null;
    }

    public UsimEfData(SIMRecords sIMRecords) {
        this.mUsim = sIMRecords;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public String getServiceProviderName() {
        String serviceProviderName = this.mUsim.getServiceProviderName();
        if (TextUtils.isEmpty(serviceProviderName)) {
            return null;
        }
        return serviceProviderName;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public int getServiceProviderNameDisplayCondition(boolean z) {
        if (z) {
            return this.mUsim.getCarrierNameDisplayCondition() | 1;
        }
        return this.mUsim.getCarrierNameDisplayCondition() | 2;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<String> getServiceProviderDisplayInformation() {
        String[] serviceProviderDisplayInformation = this.mUsim.getServiceProviderDisplayInformation();
        if (serviceProviderDisplayInformation != null) {
            return Arrays.asList(serviceProviderDisplayInformation);
        }
        return null;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<String> getEhplmnList() {
        String[] ehplmns = this.mUsim.getEhplmns();
        if (ehplmns != null) {
            return Arrays.asList(ehplmns);
        }
        return null;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<IccRecords.PlmnNetworkName> getPlmnNetworkNameList() {
        String pnnHomeName = this.mUsim.getPnnHomeName();
        if (TextUtils.isEmpty(pnnHomeName)) {
            return null;
        }
        return Arrays.asList(new IccRecords.PlmnNetworkName(pnnHomeName, PhoneConfigurationManager.SSSS));
    }
}
