package com.android.internal.telephony.cdnr;

import com.android.internal.telephony.uicc.IccRecords;
import java.util.List;
/* loaded from: classes.dex */
public interface EfData {
    public static final int EF_SOURCE_CARRIER_API = 2;
    public static final int EF_SOURCE_CARRIER_CONFIG = 1;
    public static final int EF_SOURCE_CSIM = 5;
    public static final int EF_SOURCE_DATA_OPERATOR_SIGNALLING = 8;
    public static final int EF_SOURCE_ERI = 10;
    public static final int EF_SOURCE_MODEM_CONFIG = 9;
    public static final int EF_SOURCE_RUIM = 6;
    public static final int EF_SOURCE_SIM = 4;
    public static final int EF_SOURCE_USIM = 3;
    public static final int EF_SOURCE_VOICE_OPERATOR_SIGNALLING = 7;

    default List<String> getEhplmnList() {
        return null;
    }

    default List<IccRecords.OperatorPlmnInfo> getOperatorPlmnList() {
        return null;
    }

    default List<IccRecords.PlmnNetworkName> getPlmnNetworkNameList() {
        return null;
    }

    default List<String> getServiceProviderDisplayInformation() {
        return null;
    }

    default String getServiceProviderName() {
        return null;
    }

    default int getServiceProviderNameDisplayCondition(boolean z) {
        return -1;
    }
}
