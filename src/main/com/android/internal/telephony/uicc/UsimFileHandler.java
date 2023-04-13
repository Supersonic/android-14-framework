package com.android.internal.telephony.uicc;

import com.android.internal.telephony.CommandsInterface;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public final class UsimFileHandler extends IccFileHandler {
    public UsimFileHandler(UiccCardApplication uiccCardApplication, String str, CommandsInterface commandsInterface) {
        super(uiccCardApplication, str, commandsInterface);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected String getEFPath(int i) {
        if (i == 28475 || i == 28476 || i == 28491 || i == 28492) {
            return "3F007FFF";
        }
        switch (i) {
            case IccConstants.EF_PBR /* 20272 */:
                return "3F007F105F3A";
            case IccConstants.EF_LI /* 28421 */:
            case IccConstants.EF_VOICE_MAIL_INDICATOR_CPHS /* 28433 */:
            case IccConstants.EF_HPPLMN /* 28465 */:
            case IccConstants.EF_SST /* 28472 */:
            case IccConstants.EF_SMSS /* 28483 */:
            case IccConstants.EF_SPN /* 28486 */:
            case IccConstants.EF_SDN /* 28489 */:
            case IccConstants.EF_EXT5 /* 28494 */:
            case IccConstants.EF_FPLMN /* 28539 */:
            case IccConstants.EF_AD /* 28589 */:
            case IccConstants.EF_SPDI /* 28621 */:
            case IccConstants.EF_EHPLMN /* 28633 */:
            case IccConstants.EF_LRPLMNSI /* 28636 */:
                return "3F007FFF";
            default:
                switch (i) {
                    case IccConstants.EF_CFF_CPHS /* 28435 */:
                    case IccConstants.EF_SPN_CPHS /* 28436 */:
                    case IccConstants.EF_CSP_CPHS /* 28437 */:
                    case IccConstants.EF_INFO_CPHS /* 28438 */:
                    case IccConstants.EF_MAILBOX_CPHS /* 28439 */:
                    case IccConstants.EF_SPN_SHORT_CPHS /* 28440 */:
                        return "3F007FFF";
                    default:
                        switch (i) {
                            case IccConstants.EF_GID1 /* 28478 */:
                            case IccConstants.EF_GID2 /* 28479 */:
                            case IccConstants.EF_MSISDN /* 28480 */:
                                return "3F007FFF";
                            default:
                                switch (i) {
                                    case IccConstants.EF_PLMN_W_ACT /* 28512 */:
                                    case IccConstants.EF_OPLMN_W_ACT /* 28513 */:
                                    case IccConstants.EF_HPLMN_W_ACT /* 28514 */:
                                        return "3F007FFF";
                                    default:
                                        switch (i) {
                                            case IccConstants.EF_PNN /* 28613 */:
                                            case IccConstants.EF_OPL /* 28614 */:
                                            case IccConstants.EF_MBDN /* 28615 */:
                                            case IccConstants.EF_EXT6 /* 28616 */:
                                            case IccConstants.EF_MBI /* 28617 */:
                                            case IccConstants.EF_MWIS /* 28618 */:
                                            case IccConstants.EF_CFIS /* 28619 */:
                                                return "3F007FFF";
                                            default:
                                                String commonIccEFPath = getCommonIccEFPath(i);
                                                return commonIccEFPath == null ? "3F007F105F3A" : commonIccEFPath;
                                        }
                                }
                        }
                }
        }
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void logd(String str) {
        Rlog.d("UsimFH", str);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void loge(String str) {
        Rlog.e("UsimFH", str);
    }
}
