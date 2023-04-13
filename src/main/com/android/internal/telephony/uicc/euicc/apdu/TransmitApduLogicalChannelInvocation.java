package com.android.internal.telephony.uicc.euicc.apdu;

import android.os.AsyncResult;
import android.os.Message;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.uicc.IccIoResult;
import com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class TransmitApduLogicalChannelInvocation extends AsyncMessageInvocation<ApduCommand, IccIoResult> {
    private final CommandsInterface mCi;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TransmitApduLogicalChannelInvocation(CommandsInterface commandsInterface) {
        this.mCi = commandsInterface;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation
    public void sendRequestMessage(ApduCommand apduCommand, Message message) {
        Rlog.v("TransApdu", "Send: " + apduCommand);
        CommandsInterface commandsInterface = this.mCi;
        int i = apduCommand.channel;
        commandsInterface.iccTransmitApduLogicalChannel(i, apduCommand.cla | i, apduCommand.ins, apduCommand.f27p1, apduCommand.f28p2, apduCommand.f29p3, apduCommand.cmdHex, apduCommand.isEs10, message);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncMessageInvocation
    public IccIoResult parseResult(AsyncResult asyncResult) {
        IccIoResult iccIoResult;
        Object obj;
        Throwable th = asyncResult.exception;
        if (th == null && (obj = asyncResult.result) != null) {
            iccIoResult = (IccIoResult) obj;
        } else {
            if (asyncResult.result == null) {
                Rlog.e("TransApdu", "Empty response");
            } else if (th instanceof CommandException) {
                Rlog.e("TransApdu", "CommandException", th);
            } else {
                Rlog.e("TransApdu", "CommandException", th);
            }
            iccIoResult = new IccIoResult(111, 0, (byte[]) null);
        }
        Rlog.v("TransApdu", "Response: " + iccIoResult);
        return iccIoResult;
    }
}
