package com.android.internal.telephony.gsm;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.AsyncResult;
import android.os.Message;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.SMSDispatcher;
import com.android.internal.telephony.SmsController;
import com.android.internal.telephony.SmsDispatchersController;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccCardApplication;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.util.SMSDispatcherUtil;
import com.android.telephony.Rlog;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public final class GsmSMSDispatcher extends SMSDispatcher {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private GsmInboundSmsHandler mGsmInboundSmsHandler;
    private AtomicReference<IccRecords> mIccRecords;
    private AtomicReference<UiccCardApplication> mUiccApplication;
    protected UiccController mUiccController;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getFormat() {
        return "3gpp";
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected boolean shouldBlockSmsForEcbm() {
        return false;
    }

    public GsmSMSDispatcher(Phone phone, SmsDispatchersController smsDispatchersController, GsmInboundSmsHandler gsmInboundSmsHandler) {
        super(phone, smsDispatchersController);
        this.mUiccController = null;
        this.mIccRecords = new AtomicReference<>();
        this.mUiccApplication = new AtomicReference<>();
        this.mCi.setOnSmsStatus(this, 10, null);
        this.mGsmInboundSmsHandler = gsmInboundSmsHandler;
        UiccController uiccController = UiccController.getInstance();
        this.mUiccController = uiccController;
        uiccController.registerForIccChanged(this, 15, null);
        Rlog.d("GsmSMSDispatcher", "GsmSMSDispatcher created");
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public void dispose() {
        super.dispose();
        this.mCi.unSetOnSmsStatus(this);
        this.mUiccController.unregisterForIccChanged(this);
    }

    @Override // com.android.internal.telephony.SMSDispatcher, android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 14) {
            this.mGsmInboundSmsHandler.sendMessage(1, message.obj);
        } else if (i == 15) {
            onUpdateIccAvailability();
        } else {
            super.handleMessage(message);
        }
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i, int i2) {
        return SMSDispatcherUtil.getSubmitPduGsm(str, str2, str3, z, i2);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, int i, byte[] bArr, boolean z) {
        return SMSDispatcherUtil.getSubmitPduGsm(str, str2, i, bArr, z);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i, int i2, int i3) {
        return SMSDispatcherUtil.getSubmitPduGsm(str, str2, str3, z, i2, i3);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, int i, byte[] bArr, boolean z, int i2) {
        return SMSDispatcherUtil.getSubmitPduGsm(str, str2, i, bArr, z, i2);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected GsmAlphabet.TextEncodingDetails calculateLength(CharSequence charSequence, boolean z) {
        return SMSDispatcherUtil.calculateLengthGsm(charSequence, z);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected void handleStatusReport(Object obj) {
        if (obj instanceof AsyncResult) {
            this.mSmsDispatchersController.handleSmsStatusReport("3gpp", (byte[]) ((AsyncResult) obj).result);
            this.mCi.acknowledgeLastIncomingGsmSms(true, 0, null);
            return;
        }
        Rlog.e("GsmSMSDispatcher", "handleStatusReport() called for object type " + obj.getClass().getName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.SMSDispatcher
    @UnsupportedAppUsage
    public void sendSms(SMSDispatcher.SmsTracker smsTracker) {
        int state = this.mPhone.getServiceState().getState();
        Rlog.d("GsmSMSDispatcher", "sendSms:  isIms()=" + isIms() + " mRetryCount=" + smsTracker.mRetryCount + " mImsRetry=" + smsTracker.mImsRetry + " mMessageRef=" + smsTracker.mMessageRef + " mUsesImsServiceForIms=" + smsTracker.mUsesImsServiceForIms + " SS=" + state + " " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
        if (!isIms() && state != 0 && this.mPhone.getServiceState().getRilDataRadioTechnology() != 20) {
            smsTracker.onFailed(this.mContext, SMSDispatcher.getNotInServiceError(state), -1);
            return;
        }
        Message obtainMessage = obtainMessage(2, smsTracker);
        HashMap<String, Object> data = smsTracker.getData();
        byte[] bArr = (byte[]) data.get("pdu");
        byte[] bArr2 = (byte[]) data.get("smsc");
        if (smsTracker.mRetryCount > 0) {
            byte b = bArr[0];
            if ((b & 1) == 1) {
                bArr[0] = (byte) (b | 4);
                bArr[1] = (byte) smsTracker.mMessageRef;
            }
        }
        if ((smsTracker.mImsRetry == 0 && !isIms()) || smsTracker.mUsesImsServiceForIms) {
            if (smsTracker.mRetryCount == 0 && smsTracker.mExpectMore) {
                this.mCi.sendSMSExpectMore(IccUtils.bytesToHexString(bArr2), IccUtils.bytesToHexString(bArr), obtainMessage);
                return;
            } else {
                this.mCi.sendSMS(IccUtils.bytesToHexString(bArr2), IccUtils.bytesToHexString(bArr), obtainMessage);
                return;
            }
        }
        this.mCi.sendImsGsmSms(IccUtils.bytesToHexString(bArr2), IccUtils.bytesToHexString(bArr), smsTracker.mImsRetry, smsTracker.mMessageRef, obtainMessage);
        smsTracker.mImsRetry++;
    }

    protected UiccCardApplication getUiccCardApplication() {
        Rlog.d("GsmSMSDispatcher", "GsmSMSDispatcher: subId = " + this.mPhone.getSubId() + " slotId = " + this.mPhone.getPhoneId());
        return this.mUiccController.getUiccCardApplication(this.mPhone.getPhoneId(), 1);
    }

    private void onUpdateIccAvailability() {
        UiccCardApplication uiccCardApplication;
        UiccCardApplication uiccCardApplication2;
        if (this.mUiccController == null || (uiccCardApplication2 = this.mUiccApplication.get()) == (uiccCardApplication = getUiccCardApplication())) {
            return;
        }
        if (uiccCardApplication2 != null) {
            Rlog.d("GsmSMSDispatcher", "Removing stale icc objects.");
            if (this.mIccRecords.get() != null) {
                this.mIccRecords.get().unregisterForNewSms(this);
            }
            this.mIccRecords.set(null);
            this.mUiccApplication.set(null);
        }
        if (uiccCardApplication != null) {
            Rlog.d("GsmSMSDispatcher", "New Uicc application found");
            this.mUiccApplication.set(uiccCardApplication);
            this.mIccRecords.set(uiccCardApplication.getIccRecords());
            if (this.mIccRecords.get() != null) {
                this.mIccRecords.get().registerForNewSms(this, 14, null);
            }
        }
    }
}
