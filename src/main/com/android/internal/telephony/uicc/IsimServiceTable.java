package com.android.internal.telephony.uicc;
/* loaded from: classes.dex */
public final class IsimServiceTable extends IccServiceTable {

    /* loaded from: classes.dex */
    public enum IsimService {
        PCSCF_ADDRESS,
        GBA,
        HTTP_DIGEST,
        GBA_LOCALKEY_ESTABLISHMENT,
        PCSCF_DISCOVERY_FOR_IMS,
        SMS,
        SMSR,
        SM_OVERIP_AND_DATA_DL_VIA_SMS_PP,
        COMMUNICATION_CONTROL_FOR_IMS_BY_ISIM,
        UICC_ACCESS_TO_IMS
    }

    @Override // com.android.internal.telephony.uicc.IccServiceTable
    protected String getTag() {
        return "IsimServiceTable";
    }

    public IsimServiceTable(byte[] bArr) {
        super(bArr);
    }

    public boolean isAvailable(IsimService isimService) {
        return super.isAvailable(isimService.ordinal());
    }

    @Override // com.android.internal.telephony.uicc.IccServiceTable
    protected Object[] getValues() {
        return IsimService.values();
    }

    public byte[] getISIMServiceTable() {
        return this.mServiceTable;
    }
}
