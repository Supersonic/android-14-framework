package com.android.internal.telephony.uicc.euicc.apdu;

import android.os.Handler;
import android.os.Looper;
import android.telephony.IccOpenLogicalChannelResponse;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.uicc.IccIoResult;
import com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback;
import com.android.internal.telephony.uicc.euicc.async.AsyncResultHelper;
import com.android.internal.telephony.util.DnsPacket;
import com.android.telephony.Rlog;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
/* loaded from: classes.dex */
public class ApduSender {
    private final String mAid;
    private final Object mChannelLock = new Object();
    private boolean mChannelOpened;
    private final CloseLogicalChannelInvocation mCloseChannel;
    private final OpenLogicalChannelInvocation mOpenChannel;
    private final boolean mSupportExtendedApdu;
    private final TransmitApduLogicalChannelInvocation mTransmitApdu;

    /* JADX INFO: Access modifiers changed from: private */
    public static void logv(String str) {
        Rlog.v("ApduSender", str);
    }

    private static void logd(String str) {
        Rlog.d("ApduSender", str);
    }

    public ApduSender(CommandsInterface commandsInterface, String str, boolean z) {
        this.mAid = str;
        this.mSupportExtendedApdu = z;
        this.mOpenChannel = new OpenLogicalChannelInvocation(commandsInterface);
        this.mCloseChannel = new CloseLogicalChannelInvocation(commandsInterface);
        this.mTransmitApdu = new TransmitApduLogicalChannelInvocation(commandsInterface);
    }

    public void send(final RequestProvider requestProvider, final ApduSenderResultCallback apduSenderResultCallback, final Handler handler) {
        synchronized (this.mChannelLock) {
            if (this.mChannelOpened) {
                if (!Looper.getMainLooper().equals(Looper.myLooper())) {
                    logd("Logical channel has already been opened. Wait.");
                    try {
                        this.mChannelLock.wait(2000L);
                    } catch (InterruptedException unused) {
                    }
                    if (this.mChannelOpened) {
                        AsyncResultHelper.throwException(new ApduException("The logical channel is still in use."), apduSenderResultCallback, handler);
                        return;
                    }
                } else {
                    AsyncResultHelper.throwException(new ApduException("The logical channel is in use."), apduSenderResultCallback, handler);
                    return;
                }
            }
            this.mChannelOpened = true;
            this.mOpenChannel.invoke(this.mAid, new AsyncResultCallback<IccOpenLogicalChannelResponse>() { // from class: com.android.internal.telephony.uicc.euicc.apdu.ApduSender.1
                @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                public void onResult(IccOpenLogicalChannelResponse iccOpenLogicalChannelResponse) {
                    int channel = iccOpenLogicalChannelResponse.getChannel();
                    int status = iccOpenLogicalChannelResponse.getStatus();
                    if (channel == -1 || status != 1) {
                        synchronized (ApduSender.this.mChannelLock) {
                            ApduSender.this.mChannelOpened = false;
                            ApduSender.this.mChannelLock.notify();
                        }
                        ApduSenderResultCallback apduSenderResultCallback2 = apduSenderResultCallback;
                        apduSenderResultCallback2.onException(new ApduException("Failed to open logical channel opened for AID: " + ApduSender.this.mAid + ", with status: " + status));
                        return;
                    }
                    RequestBuilder requestBuilder = new RequestBuilder(channel, ApduSender.this.mSupportExtendedApdu);
                    try {
                        requestProvider.buildRequest(iccOpenLogicalChannelResponse.getSelectResponse(), requestBuilder);
                        th = null;
                    } catch (Throwable th) {
                        th = th;
                    }
                    if (requestBuilder.getCommands().isEmpty() || th != null) {
                        ApduSender.this.closeAndReturn(channel, null, th, apduSenderResultCallback, handler);
                        return;
                    }
                    ApduSender.this.sendCommand(requestBuilder.getCommands(), 0, apduSenderResultCallback, handler);
                }
            }, handler);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCommand(final List<ApduCommand> list, final int i, final ApduSenderResultCallback apduSenderResultCallback, final Handler handler) {
        final ApduCommand apduCommand = list.get(i);
        this.mTransmitApdu.invoke(apduCommand, new AsyncResultCallback<IccIoResult>() { // from class: com.android.internal.telephony.uicc.euicc.apdu.ApduSender.2
            @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
            public void onResult(IccIoResult iccIoResult) {
                ApduSender.this.getCompleteResponse(apduCommand.channel, iccIoResult, null, new AsyncResultCallback<IccIoResult>() { // from class: com.android.internal.telephony.uicc.euicc.apdu.ApduSender.2.1
                    @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
                    public void onResult(IccIoResult iccIoResult2) {
                        ApduSender.logv("Full APDU response: " + iccIoResult2);
                        int i2 = iccIoResult2.sw1;
                        int i3 = (i2 << 8) | iccIoResult2.sw2;
                        if (i3 != 36864 && i2 != 145) {
                            C03432 c03432 = C03432.this;
                            ApduSender apduSender = ApduSender.this;
                            int i4 = apduCommand.channel;
                            ApduException apduException = new ApduException(i3);
                            C03432 c034322 = C03432.this;
                            apduSender.closeAndReturn(i4, null, apduException, apduSenderResultCallback, handler);
                            return;
                        }
                        C03432 c034323 = C03432.this;
                        if (i < list.size() - 1 && apduSenderResultCallback.shouldContinueOnIntermediateResult(iccIoResult2)) {
                            C03432 c034324 = C03432.this;
                            ApduSender.this.sendCommand(list, i + 1, apduSenderResultCallback, handler);
                            return;
                        }
                        C03432 c034325 = C03432.this;
                        ApduSender.this.closeAndReturn(apduCommand.channel, iccIoResult2.payload, null, apduSenderResultCallback, handler);
                    }
                }, handler);
            }
        }, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getCompleteResponse(final int i, IccIoResult iccIoResult, ByteArrayOutputStream byteArrayOutputStream, final AsyncResultCallback<IccIoResult> asyncResultCallback, final Handler handler) {
        final ByteArrayOutputStream byteArrayOutputStream2 = byteArrayOutputStream == null ? new ByteArrayOutputStream() : byteArrayOutputStream;
        byte[] bArr = iccIoResult.payload;
        if (bArr != null) {
            try {
                byteArrayOutputStream2.write(bArr);
            } catch (IOException unused) {
            }
        }
        if (iccIoResult.sw1 != 97) {
            iccIoResult.payload = byteArrayOutputStream2.toByteArray();
            asyncResultCallback.onResult(iccIoResult);
            return;
        }
        this.mTransmitApdu.invoke(new ApduCommand(i, 0, DnsPacket.DnsRecord.NAME_COMPRESSION, 0, 0, iccIoResult.sw2, PhoneConfigurationManager.SSSS), new AsyncResultCallback<IccIoResult>() { // from class: com.android.internal.telephony.uicc.euicc.apdu.ApduSender.3
            @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
            public void onResult(IccIoResult iccIoResult2) {
                ApduSender.this.getCompleteResponse(i, iccIoResult2, byteArrayOutputStream2, asyncResultCallback, handler);
            }
        }, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeAndReturn(int i, final byte[] bArr, final Throwable th, final ApduSenderResultCallback apduSenderResultCallback, Handler handler) {
        this.mCloseChannel.invoke(Integer.valueOf(i), new AsyncResultCallback<Boolean>() { // from class: com.android.internal.telephony.uicc.euicc.apdu.ApduSender.4
            @Override // com.android.internal.telephony.uicc.euicc.async.AsyncResultCallback
            public void onResult(Boolean bool) {
                synchronized (ApduSender.this.mChannelLock) {
                    ApduSender.this.mChannelOpened = false;
                    ApduSender.this.mChannelLock.notify();
                }
                Throwable th2 = th;
                if (th2 == null) {
                    apduSenderResultCallback.onResult(bArr);
                } else {
                    apduSenderResultCallback.onException(th2);
                }
            }
        }, handler);
    }
}
