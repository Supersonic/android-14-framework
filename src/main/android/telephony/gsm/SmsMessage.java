package android.telephony.gsm;

import android.telephony.TelephonyManager;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.cdma.SmsMessage;
import java.util.Arrays;
@Deprecated
/* loaded from: classes.dex */
public class SmsMessage {
    @Deprecated
    public static final int ENCODING_16BIT = 3;
    @Deprecated
    public static final int ENCODING_7BIT = 1;
    @Deprecated
    public static final int ENCODING_8BIT = 2;
    @Deprecated
    public static final int ENCODING_UNKNOWN = 0;
    @Deprecated
    public static final int MAX_USER_DATA_BYTES = 140;
    @Deprecated
    public static final int MAX_USER_DATA_BYTES_WITH_HEADER = 134;
    @Deprecated
    public static final int MAX_USER_DATA_SEPTETS = 160;
    @Deprecated
    public static final int MAX_USER_DATA_SEPTETS_WITH_HEADER = 153;
    @Deprecated
    public SmsMessageBase mWrappedSmsMessage;

    @Deprecated
    /* loaded from: classes.dex */
    public enum MessageClass {
        UNKNOWN,
        CLASS_0,
        CLASS_1,
        CLASS_2,
        CLASS_3
    }

    @Deprecated
    /* loaded from: classes.dex */
    public static class SubmitPdu {
        @Deprecated
        public byte[] encodedMessage;
        @Deprecated
        public byte[] encodedScAddress;

        @Deprecated
        public SubmitPdu() {
        }

        @Deprecated
        protected SubmitPdu(SmsMessageBase.SubmitPduBase submitPduBase) {
            this.encodedMessage = submitPduBase.encodedMessage;
            this.encodedScAddress = submitPduBase.encodedScAddress;
        }

        @Deprecated
        public String toString() {
            return "SubmitPdu: encodedScAddress = " + Arrays.toString(this.encodedScAddress) + ", encodedMessage = " + Arrays.toString(this.encodedMessage);
        }
    }

    @Deprecated
    public SmsMessage() {
        this(getSmsFacility());
    }

    private SmsMessage(SmsMessageBase smsMessageBase) {
        this.mWrappedSmsMessage = smsMessageBase;
    }

    @Deprecated
    public static SmsMessage createFromPdu(byte[] bArr) {
        com.android.internal.telephony.cdma.SmsMessage createFromPdu;
        if (2 == TelephonyManager.getDefault().getCurrentPhoneType()) {
            createFromPdu = com.android.internal.telephony.cdma.SmsMessage.createFromPdu(bArr);
        } else {
            createFromPdu = com.android.internal.telephony.gsm.SmsMessage.createFromPdu(bArr);
        }
        return new SmsMessage(createFromPdu);
    }

    @Deprecated
    public static int getTPLayerLengthForPDU(String str) {
        if (2 == TelephonyManager.getDefault().getCurrentPhoneType()) {
            return com.android.internal.telephony.cdma.SmsMessage.getTPLayerLengthForPDU(str);
        }
        return com.android.internal.telephony.gsm.SmsMessage.getTPLayerLengthForPDU(str);
    }

    @Deprecated
    public static int[] calculateLength(CharSequence charSequence, boolean z) {
        GsmAlphabet.TextEncodingDetails calculateLength = com.android.internal.telephony.gsm.SmsMessage.calculateLength(charSequence, z);
        return new int[]{calculateLength.msgCount, calculateLength.codeUnitCount, calculateLength.codeUnitsRemaining, calculateLength.codeUnitSize};
    }

    @Deprecated
    public static int[] calculateLength(String str, boolean z) {
        return calculateLength((CharSequence) str, z);
    }

    @Deprecated
    public static SubmitPdu getSubmitPdu(String str, String str2, String str3, boolean z, byte[] bArr) {
        SmsMessage.SubmitPdu submitPdu;
        if (2 == TelephonyManager.getDefault().getCurrentPhoneType()) {
            submitPdu = com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(str, str2, str3, z, SmsHeader.fromByteArray(bArr));
        } else {
            submitPdu = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(str, str2, str3, z, bArr);
        }
        return new SubmitPdu(submitPdu);
    }

    @Deprecated
    public static SubmitPdu getSubmitPdu(String str, String str2, String str3, boolean z) {
        SmsMessage.SubmitPdu submitPdu;
        if (2 == TelephonyManager.getDefault().getCurrentPhoneType()) {
            submitPdu = com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(str, str2, str3, z, (SmsHeader) null);
        } else {
            submitPdu = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(str, str2, str3, z);
        }
        return new SubmitPdu(submitPdu);
    }

    @Deprecated
    public static SubmitPdu getSubmitPdu(String str, String str2, short s, byte[] bArr, boolean z) {
        SmsMessage.SubmitPdu submitPdu;
        if (2 == TelephonyManager.getDefault().getCurrentPhoneType()) {
            submitPdu = com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(str, str2, s, bArr, z);
        } else {
            submitPdu = com.android.internal.telephony.gsm.SmsMessage.getSubmitPdu(str, str2, s, bArr, z);
        }
        return new SubmitPdu(submitPdu);
    }

    @Deprecated
    public String getServiceCenterAddress() {
        return this.mWrappedSmsMessage.getServiceCenterAddress();
    }

    @Deprecated
    public String getOriginatingAddress() {
        return this.mWrappedSmsMessage.getOriginatingAddress();
    }

    @Deprecated
    public String getDisplayOriginatingAddress() {
        return this.mWrappedSmsMessage.getDisplayOriginatingAddress();
    }

    @Deprecated
    public String getMessageBody() {
        return this.mWrappedSmsMessage.getMessageBody();
    }

    @Deprecated
    public MessageClass getMessageClass() {
        return MessageClass.values()[this.mWrappedSmsMessage.getMessageClass().ordinal()];
    }

    @Deprecated
    public String getDisplayMessageBody() {
        return this.mWrappedSmsMessage.getDisplayMessageBody();
    }

    @Deprecated
    public String getPseudoSubject() {
        return this.mWrappedSmsMessage.getPseudoSubject();
    }

    @Deprecated
    public long getTimestampMillis() {
        return this.mWrappedSmsMessage.getTimestampMillis();
    }

    @Deprecated
    public boolean isEmail() {
        return this.mWrappedSmsMessage.isEmail();
    }

    @Deprecated
    public String getEmailBody() {
        return this.mWrappedSmsMessage.getEmailBody();
    }

    @Deprecated
    public String getEmailFrom() {
        return this.mWrappedSmsMessage.getEmailFrom();
    }

    @Deprecated
    public int getProtocolIdentifier() {
        return this.mWrappedSmsMessage.getProtocolIdentifier();
    }

    @Deprecated
    public boolean isReplace() {
        return this.mWrappedSmsMessage.isReplace();
    }

    @Deprecated
    public boolean isCphsMwiMessage() {
        return this.mWrappedSmsMessage.isCphsMwiMessage();
    }

    @Deprecated
    public boolean isMWIClearMessage() {
        return this.mWrappedSmsMessage.isMWIClearMessage();
    }

    @Deprecated
    public boolean isMWISetMessage() {
        return this.mWrappedSmsMessage.isMWISetMessage();
    }

    @Deprecated
    public boolean isMwiDontStore() {
        return this.mWrappedSmsMessage.isMwiDontStore();
    }

    @Deprecated
    public byte[] getUserData() {
        return this.mWrappedSmsMessage.getUserData();
    }

    @Deprecated
    public byte[] getPdu() {
        return this.mWrappedSmsMessage.getPdu();
    }

    @Deprecated
    public int getStatusOnSim() {
        return this.mWrappedSmsMessage.getStatusOnIcc();
    }

    @Deprecated
    public int getStatusOnIcc() {
        return this.mWrappedSmsMessage.getStatusOnIcc();
    }

    @Deprecated
    public int getIndexOnSim() {
        return this.mWrappedSmsMessage.getIndexOnIcc();
    }

    @Deprecated
    public int getIndexOnIcc() {
        return this.mWrappedSmsMessage.getIndexOnIcc();
    }

    @Deprecated
    public int getStatus() {
        return this.mWrappedSmsMessage.getStatus();
    }

    @Deprecated
    public boolean isStatusReportMessage() {
        return this.mWrappedSmsMessage.isStatusReportMessage();
    }

    @Deprecated
    public boolean isReplyPathPresent() {
        return this.mWrappedSmsMessage.isReplyPathPresent();
    }

    @Deprecated
    private static final SmsMessageBase getSmsFacility() {
        if (2 == TelephonyManager.getDefault().getCurrentPhoneType()) {
            return new com.android.internal.telephony.cdma.SmsMessage();
        }
        return new com.android.internal.telephony.gsm.SmsMessage();
    }
}
