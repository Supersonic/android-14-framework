package com.android.internal.telephony.uicc;

import com.android.internal.telephony.CommandsInterface;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public final class IsimFileHandler extends IccFileHandler {
    public IsimFileHandler(UiccCardApplication uiccCardApplication, String str, CommandsInterface commandsInterface) {
        super(uiccCardApplication, str, commandsInterface);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected String getEFPath(int i) {
        if (i == 28423 || i == 28425 || i == 28483) {
            return "3F007FFF";
        }
        switch (i) {
            case IccConstants.EF_IMPI /* 28418 */:
            case IccConstants.EF_DOMAIN /* 28419 */:
            case IccConstants.EF_IMPU /* 28420 */:
                return "3F007FFF";
            default:
                return getCommonIccEFPath(i);
        }
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void logd(String str) {
        Rlog.d("IsimFH", str);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void loge(String str) {
        Rlog.e("IsimFH", str);
    }
}
