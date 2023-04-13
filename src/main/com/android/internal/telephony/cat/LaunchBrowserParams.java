package com.android.internal.telephony.cat;

import android.graphics.Bitmap;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: CommandParams.java */
/* loaded from: classes.dex */
public class LaunchBrowserParams extends CommandParams {
    TextMessage mConfirmMsg;
    LaunchBrowserMode mMode;
    String mUrl;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LaunchBrowserParams(CommandDetails commandDetails, TextMessage textMessage, String str, LaunchBrowserMode launchBrowserMode) {
        super(commandDetails);
        this.mConfirmMsg = textMessage;
        this.mMode = launchBrowserMode;
        this.mUrl = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.telephony.cat.CommandParams
    public boolean setIcon(Bitmap bitmap) {
        TextMessage textMessage;
        if (bitmap == null || (textMessage = this.mConfirmMsg) == null) {
            return false;
        }
        textMessage.icon = bitmap;
        return true;
    }

    @Override // com.android.internal.telephony.cat.CommandParams
    public String toString() {
        return "TextMessage=" + this.mConfirmMsg + " " + super.toString();
    }
}
