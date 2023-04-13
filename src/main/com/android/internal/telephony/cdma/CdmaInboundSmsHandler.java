package com.android.internal.telephony.cdma;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallback;
import android.os.SystemProperties;
import android.telephony.PhoneNumberUtils;
import com.android.internal.telephony.HexDump;
import com.android.internal.telephony.InboundSmsHandler;
import com.android.internal.telephony.InboundSmsTracker;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.SmsConstants;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.SmsStorageMonitor;
import com.android.internal.telephony.TelephonyComponentFactory;
import com.android.internal.telephony.WspTypeDecoder;
import com.android.internal.telephony.cdma.sms.BearerData;
import com.android.internal.telephony.cdma.sms.CdmaSmsAddress;
import com.android.internal.telephony.cdma.sms.SmsEnvelope;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class CdmaInboundSmsHandler extends InboundSmsHandler {
    private static final boolean TEST_MODE;
    private static CdmaCbTestBroadcastReceiver sTestBroadcastReceiver;
    private static CdmaScpTestBroadcastReceiver sTestScpBroadcastReceiver;
    private final boolean mCheckForDuplicatePortsInOmadmWapPush;
    private byte[] mLastAcknowledgedSmsFingerprint;
    private byte[] mLastDispatchedSmsFingerprint;
    private RemoteCallback mScpCallback;
    private final CdmaSMSDispatcher mSmsDispatcher;

    private static int resultToCause(int i) {
        if (i == -1 || i == 1) {
            return 0;
        }
        if (i != 3) {
            return i != 4 ? 39 : 4;
        }
        return 35;
    }

    @Override // com.android.internal.telephony.InboundSmsHandler
    protected boolean is3gpp2() {
        return true;
    }

    static {
        TEST_MODE = SystemProperties.getInt("ro.debuggable", 0) == 1;
    }

    private CdmaInboundSmsHandler(Context context, SmsStorageMonitor smsStorageMonitor, Phone phone, CdmaSMSDispatcher cdmaSMSDispatcher, Looper looper) {
        super("CdmaInboundSmsHandler", context, smsStorageMonitor, phone, looper);
        this.mCheckForDuplicatePortsInOmadmWapPush = Resources.getSystem().getBoolean(17891634);
        this.mSmsDispatcher = cdmaSMSDispatcher;
        phone.mCi.setOnNewCdmaSms(getHandler(), 1, null);
        this.mCellBroadcastServiceManager.enable();
        this.mScpCallback = new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: com.android.internal.telephony.cdma.CdmaInboundSmsHandler$$ExternalSyntheticLambda0
            public final void onResult(Bundle bundle) {
                CdmaInboundSmsHandler.this.lambda$new$0(bundle);
            }
        });
        if (TEST_MODE) {
            if (sTestBroadcastReceiver == null) {
                sTestBroadcastReceiver = new CdmaCbTestBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("com.android.internal.telephony.cdma.TEST_TRIGGER_CELL_BROADCAST");
                context.registerReceiver(sTestBroadcastReceiver, intentFilter, 2);
            }
            if (sTestScpBroadcastReceiver == null) {
                sTestScpBroadcastReceiver = new CdmaScpTestBroadcastReceiver();
                IntentFilter intentFilter2 = new IntentFilter();
                intentFilter2.addAction("com.android.internal.telephony.cdma.TEST_TRIGGER_SCP_MESSAGE");
                context.registerReceiver(sTestScpBroadcastReceiver, intentFilter2, 2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Bundle bundle) {
        if (bundle == null) {
            loge("SCP results error: missing extras");
            return;
        }
        String string = bundle.getString("sender");
        if (string == null) {
            loge("SCP results error: missing sender extra.");
            return;
        }
        ArrayList parcelableArrayList = bundle.getParcelableArrayList("results");
        if (parcelableArrayList == null) {
            loge("SCP results error: missing results extra.");
            return;
        }
        BearerData bearerData = new BearerData();
        bearerData.messageType = 2;
        bearerData.messageId = SmsMessage.getNextMessageId();
        bearerData.serviceCategoryProgramResults = parcelableArrayList;
        byte[] encode = BearerData.encode(bearerData);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(100);
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            try {
                dataOutputStream.writeInt(4102);
                dataOutputStream.writeInt(0);
                dataOutputStream.writeInt(0);
                CdmaSmsAddress parse = CdmaSmsAddress.parse(PhoneNumberUtils.cdmaCheckAndProcessPlusCodeForSms(string));
                dataOutputStream.write(parse.digitMode);
                dataOutputStream.write(parse.numberMode);
                dataOutputStream.write(parse.ton);
                dataOutputStream.write(parse.numberPlan);
                dataOutputStream.write(parse.numberOfDigits);
                byte[] bArr = parse.origBytes;
                dataOutputStream.write(bArr, 0, bArr.length);
                dataOutputStream.write(0);
                dataOutputStream.write(0);
                dataOutputStream.write(0);
                dataOutputStream.write(encode.length);
                dataOutputStream.write(encode, 0, encode.length);
                this.mPhone.mCi.sendCdmaSms(byteArrayOutputStream.toByteArray(), null);
            } catch (IOException e) {
                loge("exception creating SCP results PDU", e);
            }
            try {
                dataOutputStream.close();
            } catch (IOException unused) {
            }
        } catch (Throwable th) {
            try {
                dataOutputStream.close();
            } catch (IOException unused2) {
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.InboundSmsHandler, com.android.internal.telephony.StateMachine
    public void onQuitting() {
        this.mPhone.mCi.unSetOnNewCdmaSms(getHandler());
        log("unregistered for 3GPP2 SMS");
        super.onQuitting();
    }

    public static CdmaInboundSmsHandler makeInboundSmsHandler(Context context, SmsStorageMonitor smsStorageMonitor, Phone phone, CdmaSMSDispatcher cdmaSMSDispatcher, Looper looper) {
        CdmaInboundSmsHandler cdmaInboundSmsHandler = new CdmaInboundSmsHandler(context, smsStorageMonitor, phone, cdmaSMSDispatcher, looper);
        cdmaInboundSmsHandler.start();
        return cdmaInboundSmsHandler;
    }

    @Override // com.android.internal.telephony.InboundSmsHandler
    protected int dispatchMessageRadioSpecific(SmsMessageBase smsMessageBase, int i, int i2) {
        SmsMessage smsMessage = (SmsMessage) smsMessageBase;
        if (1 == smsMessage.getMessageType()) {
            log("Broadcast type message");
            this.mCellBroadcastServiceManager.sendCdmaMessageToHandler(smsMessage);
            return 1;
        }
        byte[] incomingSmsFingerprint = smsMessage.getIncomingSmsFingerprint();
        this.mLastDispatchedSmsFingerprint = incomingSmsFingerprint;
        byte[] bArr = this.mLastAcknowledgedSmsFingerprint;
        if (bArr == null || !Arrays.equals(incomingSmsFingerprint, bArr)) {
            smsMessage.parseSms();
            int teleService = smsMessage.getTeleService();
            if (teleService != 65002) {
                if (teleService != 262144) {
                    switch (teleService) {
                        case 4098:
                        case 4101:
                            if (smsMessage.isStatusReportMessage()) {
                                this.mSmsDispatcher.sendStatusReportMessage(smsMessage);
                                return 1;
                            }
                            break;
                        case 4099:
                            break;
                        case 4100:
                            break;
                        case 4102:
                            this.mCellBroadcastServiceManager.sendCdmaScpMessageToHandler(smsMessage, this.mScpCallback);
                            return 1;
                        default:
                            loge("unsupported teleservice 0x" + Integer.toHexString(teleService));
                            return 4;
                    }
                }
                handleVoicemailTeleservice(smsMessage, i);
                return 1;
            } else if (!smsMessage.preprocessCdmaFdeaWap()) {
                return 1;
            } else {
                teleService = 4100;
            }
            if (this.mStorageMonitor.isStorageAvailable() || smsMessage.getMessageClass() == SmsConstants.MessageClass.CLASS_0) {
                if (4100 == teleService) {
                    return processCdmaWapPdu(smsMessage.getUserData(), smsMessage.mMessageRef, smsMessage.getOriginatingAddress(), smsMessage.getDisplayOriginatingAddress(), smsMessage.getTimestampMillis(), i);
                }
                return dispatchNormalMessage(smsMessageBase, i);
            }
            return 3;
        }
        return 1;
    }

    @Override // com.android.internal.telephony.InboundSmsHandler
    protected void acknowledgeLastIncomingSms(boolean z, int i, Message message) {
        int resultToCause = resultToCause(i);
        this.mPhone.mCi.acknowledgeLastIncomingCdmaSms(z, resultToCause, message);
        if (resultToCause == 0) {
            this.mLastAcknowledgedSmsFingerprint = this.mLastDispatchedSmsFingerprint;
        }
        this.mLastDispatchedSmsFingerprint = null;
    }

    private void handleVoicemailTeleservice(SmsMessage smsMessage, int i) {
        int numOfVoicemails = smsMessage.getNumOfVoicemails();
        log("Voicemail count=" + numOfVoicemails);
        if (numOfVoicemails < 0) {
            numOfVoicemails = -1;
        } else if (numOfVoicemails > 99) {
            numOfVoicemails = 99;
        }
        this.mPhone.setVoiceMessageCount(numOfVoicemails);
        addVoicemailSmsToMetrics(i);
    }

    private int processCdmaWapPdu(byte[] bArr, int i, String str, String str2, long j, int i2) {
        int i3;
        int i4;
        int i5;
        int i6 = bArr[0] & 255;
        if (i6 != 0) {
            log("Received a WAP SMS which is not WDP. Discard.");
            return 1;
        }
        int i7 = bArr[1] & 255;
        int i8 = bArr[2] & 255;
        if (i8 >= i7) {
            loge("WDP bad segment #" + i8 + " expecting 0-" + (i7 - 1));
            return 1;
        }
        if (i8 == 0) {
            i5 = ((bArr[3] & 255) << 8) | (bArr[4] & 255);
            int i9 = ((bArr[5] & 255) << 8) | (bArr[6] & 255);
            if (this.mCheckForDuplicatePortsInOmadmWapPush && checkDuplicatePortOmadmWapPush(bArr, 7)) {
                i4 = 11;
                i3 = i9;
            } else {
                i3 = i9;
                i4 = 7;
            }
        } else {
            i3 = 0;
            i4 = 3;
            i5 = 0;
        }
        log("Received WAP PDU. Type = " + i6 + ", originator = " + str + ", src-port = " + i5 + ", dst-port = " + i3 + ", ID = " + i + ", segment# = " + i8 + '/' + i7);
        byte[] bArr2 = new byte[bArr.length - i4];
        System.arraycopy(bArr, i4, bArr2, 0, bArr.length - i4);
        return addTrackerToRawTableAndSendMessage(TelephonyComponentFactory.getInstance().inject(InboundSmsTracker.class.getName()).makeInboundSmsTracker(this.mContext, bArr2, j, i3, true, str, str2, i, i8, i7, true, HexDump.toHexString(bArr2), false, this.mPhone.getSubId(), i2), false);
    }

    private static boolean checkDuplicatePortOmadmWapPush(byte[] bArr, int i) {
        int i2 = i + 4;
        int length = bArr.length - i2;
        byte[] bArr2 = new byte[length];
        System.arraycopy(bArr, i2, bArr2, 0, length);
        WspTypeDecoder wspTypeDecoder = new WspTypeDecoder(bArr2);
        if (wspTypeDecoder.decodeUintvarInteger(2) && wspTypeDecoder.decodeContentType(wspTypeDecoder.getDecodedDataLength() + 2)) {
            return WspTypeDecoder.CONTENT_TYPE_B_PUSH_SYNCML_NOTI.equals(wspTypeDecoder.getValueString());
        }
        return false;
    }

    private void addVoicemailSmsToMetrics(int i) {
        this.mMetrics.writeIncomingVoiceMailSms(this.mPhone.getPhoneId(), "3gpp2");
        this.mPhone.getSmsStats().onIncomingSmsVoicemail(true, i);
    }

    /* loaded from: classes.dex */
    private class CdmaCbTestBroadcastReceiver extends InboundSmsHandler.CbTestBroadcastReceiver {
        CdmaCbTestBroadcastReceiver() {
            super("com.android.internal.telephony.cdma.TEST_TRIGGER_CELL_BROADCAST");
        }

        @Override // com.android.internal.telephony.InboundSmsHandler.CbTestBroadcastReceiver
        protected void handleTestAction(Intent intent) {
            SmsEnvelope smsEnvelope = new SmsEnvelope();
            CdmaSmsAddress cdmaSmsAddress = new CdmaSmsAddress();
            cdmaSmsAddress.origBytes = new byte[]{-1};
            smsEnvelope.origAddress = cdmaSmsAddress;
            int intExtra = intent.getIntExtra("service_category", -1);
            smsEnvelope.serviceCategory = intExtra;
            if (intExtra == -1) {
                CdmaInboundSmsHandler.this.log("No service category, ignoring CB test intent");
                return;
            }
            byte[] decodeHexString = CdmaInboundSmsHandler.this.decodeHexString(intent.getStringExtra("bearer_data_string"));
            smsEnvelope.bearerData = decodeHexString;
            if (decodeHexString == null) {
                CdmaInboundSmsHandler.this.log("No bearer data, ignoring CB test intent");
            } else {
                ((InboundSmsHandler) CdmaInboundSmsHandler.this).mCellBroadcastServiceManager.sendCdmaMessageToHandler(new SmsMessage(new CdmaSmsAddress(), smsEnvelope));
            }
        }
    }

    /* loaded from: classes.dex */
    private class CdmaScpTestBroadcastReceiver extends InboundSmsHandler.CbTestBroadcastReceiver {
        CdmaScpTestBroadcastReceiver() {
            super("com.android.internal.telephony.cdma.TEST_TRIGGER_SCP_MESSAGE");
        }

        @Override // com.android.internal.telephony.InboundSmsHandler.CbTestBroadcastReceiver
        protected void handleTestAction(Intent intent) {
            SmsEnvelope smsEnvelope = new SmsEnvelope();
            CdmaSmsAddress cdmaSmsAddress = new CdmaSmsAddress();
            cdmaSmsAddress.origBytes = new byte[]{-1};
            smsEnvelope.origAddress = cdmaSmsAddress;
            byte[] decodeHexString = CdmaInboundSmsHandler.this.decodeHexString(intent.getStringExtra("bearer_data_string"));
            smsEnvelope.bearerData = decodeHexString;
            if (decodeHexString == null) {
                CdmaInboundSmsHandler.this.log("No bearer data, ignoring SCP test intent");
                return;
            }
            CdmaSmsAddress cdmaSmsAddress2 = new CdmaSmsAddress();
            byte[] decodeHexString2 = CdmaInboundSmsHandler.this.decodeHexString(intent.getStringExtra("originating_address_string"));
            cdmaSmsAddress2.origBytes = decodeHexString2;
            if (decodeHexString2 == null) {
                CdmaInboundSmsHandler.this.log("No address data, ignoring SCP test intent");
                return;
            }
            SmsMessage smsMessage = new SmsMessage(cdmaSmsAddress2, smsEnvelope);
            smsMessage.parseSms();
            ((InboundSmsHandler) CdmaInboundSmsHandler.this).mCellBroadcastServiceManager.sendCdmaScpMessageToHandler(smsMessage, CdmaInboundSmsHandler.this.mScpCallback);
        }
    }
}
