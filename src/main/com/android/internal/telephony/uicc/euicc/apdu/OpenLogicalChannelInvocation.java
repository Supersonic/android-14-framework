package com.android.internal.telephony.uicc.euicc.apdu;

import android.os.AsyncResult;
import android.os.Message;
import android.telephony.IccOpenLogicalChannelResponse;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
class OpenLogicalChannelInvocation extends AsyncMessageInvocation<String, IccOpenLogicalChannelResponse> {
    private final CommandsInterface mCi;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenLogicalChannelInvocation(CommandsInterface commandsInterface) {
        this.mCi = commandsInterface;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation
    public void sendRequestMessage(String str, Message message) {
        this.mCi.iccOpenLogicalChannel(str, 0, message);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation
    public IccOpenLogicalChannelResponse parseResult(AsyncResult asyncResult) {
        int i;
        IccOpenLogicalChannelResponse iccOpenLogicalChannelResponse;
        Object obj;
        byte[] bArr = null;
        if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
            int[] iArr = (int[]) obj;
            int i2 = iArr[0];
            if (iArr.length > 1) {
                bArr = new byte[iArr.length - 1];
                for (int i3 = 1; i3 < iArr.length; i3++) {
                    bArr[i3 - 1] = (byte) iArr[i3];
                }
            }
            iccOpenLogicalChannelResponse = new IccOpenLogicalChannelResponse(i2, 1, bArr);
        } else {
            if (asyncResult.result == null) {
                Rlog.e("OpenChan", "Empty response");
            }
            Throwable th = asyncResult.exception;
            if (th != null) {
                Rlog.e("OpenChan", "Exception", th);
            }
            Throwable th2 = asyncResult.exception;
            if (th2 instanceof CommandException) {
                CommandException.Error commandError = ((CommandException) th2).getCommandError();
                if (commandError == CommandException.Error.MISSING_RESOURCE) {
                    i = 2;
                } else if (commandError == CommandException.Error.NO_SUCH_ELEMENT) {
                    i = 3;
                }
                iccOpenLogicalChannelResponse = new IccOpenLogicalChannelResponse(-1, i, null);
            }
            i = 4;
            iccOpenLogicalChannelResponse = new IccOpenLogicalChannelResponse(-1, i, null);
        }
        Rlog.v("OpenChan", "Response: " + iccOpenLogicalChannelResponse);
        return iccOpenLogicalChannelResponse;
    }
}
