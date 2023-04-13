package com.android.internal.telephony.cdma;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.Message;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.GsmCdmaPhone;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.SMSDispatcher;
import com.android.internal.telephony.SmsController;
import com.android.internal.telephony.SmsDispatchersController;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.util.SMSDispatcherUtil;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class CdmaSMSDispatcher extends SMSDispatcher {
    @Override // com.android.internal.telephony.SMSDispatcher
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public String getFormat() {
        return "3gpp2";
    }

    public CdmaSMSDispatcher(Phone phone, SmsDispatchersController smsDispatchersController) {
        super(phone, smsDispatchersController);
        Rlog.d("CdmaSMSDispatcher", "CdmaSMSDispatcher created");
    }

    public void sendStatusReportMessage(SmsMessage smsMessage) {
        sendMessage(obtainMessage(10, smsMessage));
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected void handleStatusReport(Object obj) {
        if (obj instanceof SmsMessage) {
            this.mSmsDispatchersController.handleSmsStatusReport("3gpp2", ((SmsMessage) obj).getPdu());
            return;
        }
        Rlog.e("CdmaSMSDispatcher", "handleStatusReport() called for object type " + obj.getClass().getName());
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected boolean shouldBlockSmsForEcbm() {
        return this.mPhone.isInEcm() && isCdmaMo() && !isIms();
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i, int i2) {
        return SMSDispatcherUtil.getSubmitPduCdma(str, str2, str3, z, smsHeader, i);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, int i, byte[] bArr, boolean z) {
        return SMSDispatcherUtil.getSubmitPduCdma(str, str2, i, bArr, z);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i, int i2, int i3) {
        return SMSDispatcherUtil.getSubmitPduCdma(str, str2, str3, z, smsHeader, i);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected SmsMessageBase.SubmitPduBase getSubmitPdu(String str, String str2, int i, byte[] bArr, boolean z, int i2) {
        return SMSDispatcherUtil.getSubmitPduCdma(str, str2, i, bArr, z);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    protected GsmAlphabet.TextEncodingDetails calculateLength(CharSequence charSequence, boolean z) {
        return SMSDispatcherUtil.calculateLengthCdma(charSequence, z);
    }

    @Override // com.android.internal.telephony.SMSDispatcher
    public void sendSms(SMSDispatcher.SmsTracker smsTracker) {
        int state = this.mPhone.getServiceState().getState();
        Rlog.d("CdmaSMSDispatcher", "sendSms:  isIms()=" + isIms() + " mRetryCount=" + smsTracker.mRetryCount + " mImsRetry=" + smsTracker.mImsRetry + " mMessageRef=" + smsTracker.mMessageRef + " mUsesImsServiceForIms=" + smsTracker.mUsesImsServiceForIms + " SS=" + state + " " + SmsController.formatCrossStackMessageId(smsTracker.mMessageId));
        if (!isIms() && state != 0) {
            smsTracker.onFailed(this.mContext, SMSDispatcher.getNotInServiceError(state), -1);
            return;
        }
        Message obtainMessage = obtainMessage(2, smsTracker);
        byte[] bArr = (byte[]) smsTracker.getData().get("pdu");
        int dataNetworkType = this.mPhone.getServiceState().getDataNetworkType();
        boolean z = (dataNetworkType == 14 || ((dataNetworkType == 13 || dataNetworkType == 19 || dataNetworkType == 20) && !this.mPhone.getServiceStateTracker().isConcurrentVoiceAndDataAllowed())) && this.mPhone.getServiceState().getVoiceNetworkType() == 7 && ((GsmCdmaPhone) this.mPhone).mCT.mState != PhoneConstants.State.IDLE;
        if ((smsTracker.mImsRetry == 0 && !isIms()) || z || smsTracker.mUsesImsServiceForIms) {
            if (smsTracker.mRetryCount == 0 && smsTracker.mExpectMore) {
                this.mCi.sendCdmaSMSExpectMore(bArr, obtainMessage);
                return;
            } else {
                this.mCi.sendCdmaSms(bArr, obtainMessage);
                return;
            }
        }
        this.mCi.sendImsCdmaSms(bArr, smsTracker.mImsRetry, smsTracker.mMessageRef, obtainMessage);
        smsTracker.mImsRetry++;
    }
}
