package com.android.internal.telephony.gsm;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncResult;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import com.android.ims.ImsManager;
import com.android.internal.telephony.InboundSmsHandler;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.SmsConstants;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.SmsStorageMonitor;
import com.android.internal.telephony.VisualVoicemailSmsFilter;
/* loaded from: classes.dex */
public class GsmInboundSmsHandler extends InboundSmsHandler {
    private static final boolean TEST_MODE;
    private final UsimDataDownloadHandler mDataDownloadHandler;
    private BroadcastReceiver mTestBroadcastReceiver;

    private static int resultToCause(int i) {
        if (i == -1 || i == 1) {
            return 0;
        }
        return i != 3 ? 255 : 211;
    }

    @Override // com.android.internal.telephony.InboundSmsHandler
    protected boolean is3gpp2() {
        return false;
    }

    static {
        TEST_MODE = SystemProperties.getInt("ro.debuggable", 0) == 1;
    }

    private GsmInboundSmsHandler(Context context, SmsStorageMonitor smsStorageMonitor, Phone phone, Looper looper) {
        super("GsmInboundSmsHandler", context, smsStorageMonitor, phone, looper);
        phone.mCi.setOnNewGsmSms(getHandler(), 1, null);
        this.mDataDownloadHandler = new UsimDataDownloadHandler(phone.mCi, phone.getPhoneId());
        this.mCellBroadcastServiceManager.enable();
        if (TEST_MODE && this.mTestBroadcastReceiver == null) {
            this.mTestBroadcastReceiver = new GsmCbTestBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.android.internal.telephony.gsm.TEST_TRIGGER_CELL_BROADCAST");
            context.registerReceiver(this.mTestBroadcastReceiver, intentFilter, 2);
        }
    }

    /* loaded from: classes.dex */
    private class GsmCbTestBroadcastReceiver extends InboundSmsHandler.CbTestBroadcastReceiver {
        GsmCbTestBroadcastReceiver() {
            super("com.android.internal.telephony.gsm.TEST_TRIGGER_CELL_BROADCAST");
        }

        @Override // com.android.internal.telephony.InboundSmsHandler.CbTestBroadcastReceiver
        protected void handleTestAction(Intent intent) {
            byte[] byteArrayExtra = intent.getByteArrayExtra("pdu");
            if (byteArrayExtra == null) {
                byteArrayExtra = GsmInboundSmsHandler.this.decodeHexString(intent.getStringExtra("pdu_string"));
            }
            if (byteArrayExtra == null) {
                GsmInboundSmsHandler.this.log("No pdu or pdu_string extra, ignoring CB test intent");
                return;
            }
            Message obtain = Message.obtain();
            AsyncResult.forMessage(obtain, byteArrayExtra, (Throwable) null);
            ((InboundSmsHandler) GsmInboundSmsHandler.this).mCellBroadcastServiceManager.sendGsmMessageToHandler(obtain);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.InboundSmsHandler, com.android.internal.telephony.StateMachine
    public void onQuitting() {
        this.mPhone.mCi.unSetOnNewGsmSms(getHandler());
        log("unregistered for 3GPP SMS");
        super.onQuitting();
    }

    public static GsmInboundSmsHandler makeInboundSmsHandler(Context context, SmsStorageMonitor smsStorageMonitor, Phone phone, Looper looper) {
        GsmInboundSmsHandler gsmInboundSmsHandler = new GsmInboundSmsHandler(context, smsStorageMonitor, phone, looper);
        gsmInboundSmsHandler.start();
        return gsmInboundSmsHandler;
    }

    @Override // com.android.internal.telephony.InboundSmsHandler
    protected int dispatchMessageRadioSpecific(SmsMessageBase smsMessageBase, int i, int i2) {
        boolean z;
        SmsHeader.PortAddrs portAddrs;
        SmsMessage smsMessage = (SmsMessage) smsMessageBase;
        if (smsMessage.isTypeZero()) {
            SmsHeader userDataHeader = smsMessage.getUserDataHeader();
            VisualVoicemailSmsFilter.filter(this.mContext, new byte[][]{smsMessage.getPdu()}, "3gpp", (userDataHeader == null || (portAddrs = userDataHeader.portAddrs) == null) ? -1 : portAddrs.destPort, this.mPhone.getSubId());
            log("Received short message type 0, Don't display or store it. Send Ack");
            addSmsTypeZeroToMetrics(i);
            return 1;
        } else if (smsMessage.isUsimDataDownload()) {
            return this.mDataDownloadHandler.handleUsimDataDownload(this.mPhone.getUsimServiceTable(), smsMessage, i, i2);
        } else {
            if (smsMessage.isMWISetMessage()) {
                updateMessageWaitingIndicator(smsMessage.getNumOfVoicemails());
                z = smsMessage.isMwiDontStore();
                StringBuilder sb = new StringBuilder();
                sb.append("Received voice mail indicator set SMS shouldStore=");
                sb.append(!z);
                log(sb.toString());
            } else if (smsMessage.isMWIClearMessage()) {
                updateMessageWaitingIndicator(0);
                z = smsMessage.isMwiDontStore();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Received voice mail indicator clear SMS shouldStore=");
                sb2.append(!z);
                log(sb2.toString());
            } else {
                z = false;
            }
            if (z) {
                addVoicemailSmsToMetrics(i);
                return 1;
            } else if (this.mStorageMonitor.isStorageAvailable() || smsMessage.getMessageClass() == SmsConstants.MessageClass.CLASS_0) {
                return dispatchNormalMessage(smsMessageBase, i);
            } else {
                return 3;
            }
        }
    }

    private void updateMessageWaitingIndicator(int i) {
        if (i < 0) {
            i = -1;
        } else if (i > 255) {
            i = 255;
        }
        this.mPhone.setVoiceMessageCount(i);
    }

    @Override // com.android.internal.telephony.InboundSmsHandler
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected void acknowledgeLastIncomingSms(boolean z, int i, Message message) {
        this.mPhone.mCi.acknowledgeLastIncomingGsmSms(z, resultToCause(i), message);
    }

    private void addSmsTypeZeroToMetrics(int i) {
        this.mMetrics.writeIncomingSmsTypeZero(this.mPhone.getPhoneId(), "3gpp");
        this.mPhone.getSmsStats().onIncomingSmsTypeZero(i);
    }

    private void addVoicemailSmsToMetrics(int i) {
        this.mMetrics.writeIncomingVoiceMailSms(this.mPhone.getPhoneId(), "3gpp");
        this.mPhone.getSmsStats().onIncomingSmsVoicemail(false, i);
    }

    public boolean setImsManager(ImsManager imsManager) {
        UsimDataDownloadHandler usimDataDownloadHandler = this.mDataDownloadHandler;
        if (usimDataDownloadHandler != null) {
            usimDataDownloadHandler.setImsManager(imsManager);
            return true;
        }
        return false;
    }
}
