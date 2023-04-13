package com.android.internal.telephony.cat;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SendSMSParams extends CommandParams {
    TextMessage mDestAddress;
    DisplayTextParams mDisplayText;
    TextMessage mTextSmsMsg;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SendSMSParams(CommandDetails commandDetails, TextMessage textMessage, TextMessage textMessage2, DisplayTextParams displayTextParams) {
        super(commandDetails);
        this.mTextSmsMsg = textMessage;
        this.mDestAddress = textMessage2;
        this.mDisplayText = displayTextParams;
    }
}
