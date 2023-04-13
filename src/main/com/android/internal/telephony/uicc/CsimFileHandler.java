package com.android.internal.telephony.uicc;

import com.android.internal.telephony.CommandsInterface;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public final class CsimFileHandler extends IccFileHandler {
    public CsimFileHandler(UiccCardApplication uiccCardApplication, String str, CommandsInterface commandsInterface) {
        super(uiccCardApplication, str, commandsInterface);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected String getEFPath(int i) {
        if (i == 20256 || i == 20257) {
            return "3F007F105F3C";
        }
        if (i == 28450 || i == 28456 || i == 28464 || i == 28466 || i == 28484 || i == 28493 || i == 28506 || i == 28480 || i == 28481) {
            return "3F007FFF";
        }
        switch (i) {
            case 28474:
            case IccConstants.EF_FDN /* 28475 */:
            case IccConstants.EF_SMS /* 28476 */:
                return "3F007FFF";
            default:
                String commonIccEFPath = getCommonIccEFPath(i);
                return commonIccEFPath == null ? "3F007F105F3A" : commonIccEFPath;
        }
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void logd(String str) {
        Rlog.d("CsimFH", str);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void loge(String str) {
        Rlog.e("CsimFH", str);
    }
}
