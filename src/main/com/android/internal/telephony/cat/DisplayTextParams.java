package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.graphics.Bitmap;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: CommandParams.java */
/* loaded from: classes.dex */
public class DisplayTextParams extends CommandParams {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    TextMessage mTextMsg;

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public DisplayTextParams(CommandDetails commandDetails, TextMessage textMessage) {
        super(commandDetails);
        this.mTextMsg = textMessage;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.telephony.cat.CommandParams
    public boolean setIcon(Bitmap bitmap) {
        TextMessage textMessage;
        if (bitmap == null || (textMessage = this.mTextMsg) == null) {
            return false;
        }
        textMessage.icon = bitmap;
        return true;
    }

    @Override // com.android.internal.telephony.cat.CommandParams
    public String toString() {
        return "TextMessage=" + this.mTextMsg + " " + super.toString();
    }
}
