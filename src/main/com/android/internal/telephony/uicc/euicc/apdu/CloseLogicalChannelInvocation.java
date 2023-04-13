package com.android.internal.telephony.uicc.euicc.apdu;

import android.os.AsyncResult;
import android.os.Message;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
class CloseLogicalChannelInvocation extends AsyncMessageInvocation<Integer, Boolean> {
    private final CommandsInterface mCi;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CloseLogicalChannelInvocation(CommandsInterface commandsInterface) {
        this.mCi = commandsInterface;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation
    public void sendRequestMessage(Integer num, Message message) {
        Rlog.v("CloseChan", "Channel: " + num);
        this.mCi.iccCloseLogicalChannel(num.intValue(), true, message);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation
    public Boolean parseResult(AsyncResult asyncResult) {
        Throwable th = asyncResult.exception;
        if (th == null) {
            return Boolean.TRUE;
        }
        if (th instanceof CommandException) {
            Rlog.e("CloseChan", "CommandException", th);
        } else {
            Rlog.e("CloseChan", "Unknown exception", th);
        }
        return Boolean.FALSE;
    }
}
