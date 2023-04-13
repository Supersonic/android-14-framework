package com.android.internal.telephony.uicc;

import com.android.internal.telephony.CommandsInterface;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public final class SIMFileHandler extends IccFileHandler {
    public SIMFileHandler(UiccCardApplication uiccCardApplication, String str, CommandsInterface commandsInterface) {
        super(uiccCardApplication, str, commandsInterface);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected String getEFPath(int i) {
        if (i == 28433 || i == 28472) {
            return "3F007F20";
        }
        if (i != 28476) {
            if (i == 28486 || i == 28589 || i == 28613 || i == 28621 || i == 28478 || i == 28479) {
                return "3F007F20";
            }
            switch (i) {
                case IccConstants.EF_CFF_CPHS /* 28435 */:
                case IccConstants.EF_SPN_CPHS /* 28436 */:
                case IccConstants.EF_CSP_CPHS /* 28437 */:
                case IccConstants.EF_INFO_CPHS /* 28438 */:
                case IccConstants.EF_MAILBOX_CPHS /* 28439 */:
                case IccConstants.EF_SPN_SHORT_CPHS /* 28440 */:
                    return "3F007F20";
                default:
                    switch (i) {
                        case IccConstants.EF_MBDN /* 28615 */:
                        case IccConstants.EF_EXT6 /* 28616 */:
                        case IccConstants.EF_MBI /* 28617 */:
                        case IccConstants.EF_MWIS /* 28618 */:
                        case IccConstants.EF_CFIS /* 28619 */:
                            return "3F007F20";
                        default:
                            String commonIccEFPath = getCommonIccEFPath(i);
                            if (commonIccEFPath == null) {
                                Rlog.e("SIMFileHandler", "Error: EF Path being returned in null");
                            }
                            return commonIccEFPath;
                    }
            }
        }
        return "3F007F10";
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void logd(String str) {
        Rlog.d("SIMFileHandler", str);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void loge(String str) {
        Rlog.e("SIMFileHandler", str);
    }
}
