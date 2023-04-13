package com.android.internal.telephony.uicc;

import android.os.Message;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.util.DnsPacket;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public final class RuimFileHandler extends IccFileHandler {
    public RuimFileHandler(UiccCardApplication uiccCardApplication, String str, CommandsInterface commandsInterface) {
        super(uiccCardApplication, str, commandsInterface);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    public void loadEFImgTransparent(int i, int i2, int i3, int i4, Message message) {
        this.mCi.iccIOForApp(DnsPacket.DnsRecord.NAME_COMPRESSION, i, getEFPath(20256), 0, 0, 10, null, null, this.mAid, obtainMessage(10, i, 0, message));
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected String getEFPath(int i) {
        return (i == 20256 || i == 20257) ? "3F007F105F3C" : (i == 28450 || i == 28456 || i == 28464 || i == 28466 || i == 28474 || i == 28476 || i == 28481 || i == 28484 || i == 28493 || i == 28506) ? "3F007F25" : getCommonIccEFPath(i);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void logd(String str) {
        Rlog.d("RuimFH", "[RuimFileHandler] " + str);
    }

    @Override // com.android.internal.telephony.uicc.IccFileHandler
    protected void loge(String str) {
        Rlog.e("RuimFH", "[RuimFileHandler] " + str);
    }
}
