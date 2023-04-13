package com.android.internal.telephony.cat;

import android.graphics.Bitmap;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: CommandParams.java */
/* loaded from: classes.dex */
public class BIPClientParams extends CommandParams {
    boolean mHasAlphaId;
    TextMessage mTextMsg;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BIPClientParams(CommandDetails commandDetails, TextMessage textMessage, boolean z) {
        super(commandDetails);
        this.mTextMsg = textMessage;
        this.mHasAlphaId = z;
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
}
