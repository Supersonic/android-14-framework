package com.android.internal.telephony.gsm;

import android.content.res.Resources;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.telephony.gsm.SmsMessage;
import com.android.ims.ImsException;
import com.android.ims.ImsManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.cat.ComprehensionTlvTag;
import com.android.internal.telephony.metrics.TelephonyMetrics;
import com.android.internal.telephony.uicc.IccIoResult;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UsimServiceTable;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class UsimDataDownloadHandler extends Handler {
    private final CommandsInterface mCi;
    private final int mPhoneId;
    private ImsManager mImsManager = null;
    Resources mResource = Resources.getSystem();

    private static int getEnvelopeBodyLength(int i, int i2) {
        int i3 = i2 + 5 + (i2 > 127 ? 2 : 1);
        return i != 0 ? i3 + 2 + i : i3;
    }

    private static boolean is7bitDcs(int i) {
        return (i & SmsMessage.MAX_USER_DATA_BYTES) == 0 || (i & CallFailCause.DIAL_MODIFIED_TO_USSD) == 240;
    }

    public UsimDataDownloadHandler(CommandsInterface commandsInterface, int i) {
        this.mCi = commandsInterface;
        this.mPhoneId = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int handleUsimDataDownload(UsimServiceTable usimServiceTable, SmsMessage smsMessage, int i, int i2) {
        if (usimServiceTable != null && usimServiceTable.isAvailable(UsimServiceTable.UsimService.DATA_DL_VIA_SMS_PP)) {
            Rlog.d("UsimDataDownloadHandler", "Received SMS-PP data download, sending to UICC.");
            return startDataDownload(smsMessage, i, i2);
        }
        Rlog.d("UsimDataDownloadHandler", "DATA_DL_VIA_SMS_PP service not available, storing message to UICC.");
        this.mCi.writeSmsToSim(3, IccUtils.bytesToHexString(PhoneNumberUtils.networkPortionToCalledPartyBCDWithLength(smsMessage.getServiceCenterAddress())), IccUtils.bytesToHexString(smsMessage.getPdu()), obtainMessage(3, new int[]{i, smsMessage.mMessageRef, i2}));
        addUsimDataDownloadToMetrics(false, i);
        return -1;
    }

    public int startDataDownload(SmsMessage smsMessage, int i, int i2) {
        if (sendMessage(obtainMessage(1, i, i2, smsMessage))) {
            return -1;
        }
        Rlog.e("UsimDataDownloadHandler", "startDataDownload failed to send message to start data download.");
        return 2;
    }

    private void handleDataDownload(SmsMessage smsMessage, int i, int i2) {
        int i3;
        int dataCodingScheme = smsMessage.getDataCodingScheme();
        int protocolIdentifier = smsMessage.getProtocolIdentifier();
        byte[] pdu = smsMessage.getPdu();
        int i4 = pdu[0] & 255;
        int i5 = i4 + 1;
        int length = pdu.length - i5;
        int envelopeBodyLength = getEnvelopeBodyLength(i4, length);
        int i6 = envelopeBodyLength + 1 + (envelopeBodyLength > 127 ? 2 : 1);
        byte[] bArr = new byte[i6];
        Rlog.d("UsimDataDownloadHandler", "smsSource: " + i + "Token: " + i2);
        bArr[0] = -47;
        if (envelopeBodyLength > 127) {
            bArr[1] = -127;
            i3 = 2;
        } else {
            i3 = 1;
        }
        int i7 = i3 + 1;
        bArr[i3] = (byte) envelopeBodyLength;
        int i8 = i7 + 1;
        bArr[i7] = (byte) (ComprehensionTlvTag.DEVICE_IDENTITIES.value() | 128);
        int i9 = i8 + 1;
        bArr[i8] = 2;
        int i10 = i9 + 1;
        bArr[i9] = -125;
        int i11 = i10 + 1;
        bArr[i10] = -127;
        if (i4 != 0) {
            int i12 = i11 + 1;
            bArr[i11] = (byte) ComprehensionTlvTag.ADDRESS.value();
            int i13 = i12 + 1;
            bArr[i12] = (byte) i4;
            System.arraycopy(pdu, 1, bArr, i13, i4);
            i11 = i13 + i4;
        }
        int i14 = i11 + 1;
        bArr[i11] = (byte) (ComprehensionTlvTag.SMS_TPDU.value() | 128);
        if (length > 127) {
            bArr[i14] = -127;
            i14++;
        }
        int i15 = i14 + 1;
        bArr[i14] = (byte) length;
        System.arraycopy(pdu, i5, bArr, i15, length);
        if (i15 + length != i6) {
            Rlog.e("UsimDataDownloadHandler", "startDataDownload() calculated incorrect envelope length, aborting.");
            acknowledgeSmsWithError(255, i, i2, smsMessage.mMessageRef);
            addUsimDataDownloadToMetrics(false, i);
            return;
        }
        this.mCi.sendEnvelopeWithStatus(IccUtils.bytesToHexString(bArr), obtainMessage(2, new int[]{dataCodingScheme, protocolIdentifier, i, smsMessage.mMessageRef, i2}));
        addUsimDataDownloadToMetrics(true, i);
    }

    private void sendSmsAckForEnvelopeResponse(IccIoResult iccIoResult, int i, int i2, int i3, int i4, int i5) {
        boolean z;
        byte[] bArr;
        int i6;
        int i7 = iccIoResult.sw1;
        int i8 = iccIoResult.sw2;
        if ((i7 == 144 && i8 == 0) || i7 == 145) {
            Rlog.d("UsimDataDownloadHandler", "USIM data download succeeded: " + iccIoResult.toString());
            z = true;
        } else if (i7 == 147 && i8 == 0) {
            Rlog.e("UsimDataDownloadHandler", "USIM data download failed: Toolkit busy");
            acknowledgeSmsWithError(CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_APP_TOOLKIT_BUSY, i3, i4, i5);
            return;
        } else {
            if (i7 == 98 || i7 == 99) {
                Rlog.e("UsimDataDownloadHandler", "USIM data download failed: " + iccIoResult.toString());
            } else {
                Rlog.e("UsimDataDownloadHandler", "Unexpected SW1/SW2 response from UICC: " + iccIoResult.toString());
            }
            z = false;
        }
        byte[] bArr2 = iccIoResult.payload;
        if (bArr2 == null || bArr2.length == 0) {
            if (z) {
                acknowledgeSmsWithSuccess(0, i3, i4, i5);
                return;
            } else {
                acknowledgeSmsWithError(CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_DATA_DOWNLOAD_ERROR, i3, i4, i5);
                return;
            }
        }
        int i9 = 2;
        if (z) {
            bArr = new byte[bArr2.length + 5];
            bArr[0] = 0;
            bArr[1] = 7;
        } else {
            bArr = new byte[bArr2.length + 6];
            bArr[0] = 0;
            bArr[1] = -43;
            bArr[2] = 7;
            i9 = 3;
        }
        int i10 = i9 + 1;
        bArr[i9] = (byte) i2;
        int i11 = i10 + 1;
        bArr[i10] = (byte) i;
        if (is7bitDcs(i)) {
            i6 = i11 + 1;
            bArr[i11] = (byte) ((bArr2.length * 8) / 7);
        } else {
            i6 = i11 + 1;
            bArr[i11] = (byte) bArr2.length;
        }
        System.arraycopy(bArr2, 0, bArr, i6, bArr2.length);
        if (i3 == 1 && ackViaIms()) {
            acknowledgeImsSms(i4, i5, true, bArr);
        } else {
            this.mCi.acknowledgeIncomingGsmSmsWithPdu(z, IccUtils.bytesToHexString(bArr), null);
        }
    }

    private void acknowledgeSmsWithSuccess(int i, int i2, int i3, int i4) {
        Rlog.d("UsimDataDownloadHandler", "acknowledgeSmsWithSuccess- cause: " + i + " smsSource: " + i2 + " token: " + i3 + " messageRef: " + i4);
        if (i2 == 1 && ackViaIms()) {
            acknowledgeImsSms(i3, i4, true, null);
        } else {
            this.mCi.acknowledgeLastIncomingGsmSms(true, i, null);
        }
    }

    private void acknowledgeSmsWithError(int i, int i2, int i3, int i4) {
        Rlog.d("UsimDataDownloadHandler", "acknowledgeSmsWithError- cause: " + i + " smsSource: " + i2 + " token: " + i3 + " messageRef: " + i4);
        if (i2 == 1 && ackViaIms()) {
            acknowledgeImsSms(i3, i4, false, null);
        } else {
            this.mCi.acknowledgeLastIncomingGsmSms(false, i, null);
        }
    }

    private void addUsimDataDownloadToMetrics(boolean z, int i) {
        TelephonyMetrics.getInstance().writeIncomingSMSPP(this.mPhoneId, "3gpp", z);
        PhoneFactory.getPhone(this.mPhoneId).getSmsStats().onIncomingSmsPP(i, z);
    }

    private boolean ackViaIms() {
        boolean z;
        try {
            z = this.mResource.getBoolean(17891802);
        } catch (Resources.NotFoundException unused) {
            z = false;
        }
        Rlog.d("UsimDataDownloadHandler", "ackViaIms : " + z);
        return z;
    }

    private void acknowledgeImsSms(int i, int i2, boolean z, byte[] bArr) {
        int i3 = z ? 1 : 2;
        Rlog.d("UsimDataDownloadHandler", "sending result via acknowledgeImsSms: " + i3 + " token: " + i);
        try {
            ImsManager imsManager = this.mImsManager;
            if (imsManager != null) {
                if (bArr != null && bArr.length > 0) {
                    imsManager.acknowledgeSms(i, i2, i3, bArr);
                } else {
                    imsManager.acknowledgeSms(i, i2, i3);
                }
            }
        } catch (ImsException e) {
            Rlog.e("UsimDataDownloadHandler", "Failed to acknowledgeSms(). Error: " + e.getMessage());
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            Rlog.d("UsimDataDownloadHandler", "EVENT_START_DATA_DOWNLOAD");
            handleDataDownload((SmsMessage) message.obj, message.arg1, message.arg2);
        } else if (i == 2) {
            AsyncResult asyncResult = (AsyncResult) message.obj;
            int[] iArr = (int[]) asyncResult.userObj;
            int i2 = iArr[2];
            int i3 = iArr[3];
            int i4 = iArr[4];
            Rlog.d("UsimDataDownloadHandler", "Received EVENT_SEND_ENVELOPE_RESPONSE from source : " + i2);
            if (asyncResult.exception != null) {
                Rlog.e("UsimDataDownloadHandler", "UICC Send Envelope failure, exception: " + asyncResult.exception);
                acknowledgeSmsWithError(CommandsInterface.GSM_SMS_FAIL_CAUSE_USIM_DATA_DOWNLOAD_ERROR, i2, i4, i3);
                return;
            }
            Rlog.d("UsimDataDownloadHandler", "Successful in sending envelope response");
            sendSmsAckForEnvelopeResponse((IccIoResult) asyncResult.result, iArr[0], iArr[1], i2, i4, i3);
        } else if (i == 3) {
            AsyncResult asyncResult2 = (AsyncResult) message.obj;
            int[] iArr2 = (int[]) asyncResult2.userObj;
            int i5 = iArr2[0];
            int i6 = iArr2[1];
            int i7 = iArr2[2];
            Rlog.d("UsimDataDownloadHandler", "Received EVENT_WRITE_SMS_COMPLETE from source : " + i5);
            Throwable th = asyncResult2.exception;
            if (th == null) {
                Rlog.d("UsimDataDownloadHandler", "Successfully wrote SMS-PP message to UICC");
                acknowledgeSmsWithSuccess(0, i5, i7, i6);
                return;
            }
            Rlog.d("UsimDataDownloadHandler", "Failed to write SMS-PP message to UICC", th);
            acknowledgeSmsWithError(255, i5, i7, i6);
        } else {
            Rlog.e("UsimDataDownloadHandler", "Ignoring unexpected message, what=" + message.what);
        }
    }

    public void setImsManager(ImsManager imsManager) {
        this.mImsManager = imsManager;
    }

    @VisibleForTesting
    public void setResourcesForTest(Resources resources) {
        this.mResource = resources;
        Rlog.d("UsimDataDownloadHandler", "setResourcesForTest");
    }
}
