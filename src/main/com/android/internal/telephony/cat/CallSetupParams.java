package com.android.internal.telephony.cat;

import android.graphics.Bitmap;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: CommandParams.java */
/* loaded from: classes.dex */
public class CallSetupParams extends CommandParams {
    TextMessage mCallMsg;
    TextMessage mConfirmMsg;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CallSetupParams(CommandDetails commandDetails, TextMessage textMessage, TextMessage textMessage2) {
        super(commandDetails);
        this.mConfirmMsg = textMessage;
        this.mCallMsg = textMessage2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.telephony.cat.CommandParams
    public boolean setIcon(Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }
        TextMessage textMessage = this.mConfirmMsg;
        if (textMessage != null && textMessage.icon == null) {
            textMessage.icon = bitmap;
            return true;
        }
        TextMessage textMessage2 = this.mCallMsg;
        if (textMessage2 == null || textMessage2.icon != null) {
            return false;
        }
        textMessage2.icon = bitmap;
        return true;
    }
}
