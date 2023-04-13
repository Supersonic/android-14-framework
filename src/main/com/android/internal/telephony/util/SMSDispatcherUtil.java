package com.android.internal.telephony.util;

import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.SmsHeader;
import com.android.internal.telephony.SmsMessageBase;
import com.android.internal.telephony.gsm.SmsMessage;
/* loaded from: classes.dex */
public final class SMSDispatcherUtil {
    private SMSDispatcherUtil() {
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPdu(boolean z, String str, String str2, String str3, boolean z2, SmsHeader smsHeader) {
        if (z) {
            return getSubmitPduCdma(str, str2, str3, z2, smsHeader);
        }
        return getSubmitPduGsm(str, str2, str3, z2);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPdu(boolean z, String str, String str2, String str3, boolean z2, SmsHeader smsHeader, int i, int i2) {
        if (z) {
            return getSubmitPduCdma(str, str2, str3, z2, smsHeader, i);
        }
        return getSubmitPduGsm(str, str2, str3, z2, i2);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPdu(boolean z, String str, String str2, String str3, boolean z2, SmsHeader smsHeader, int i, int i2, int i3) {
        if (z) {
            return getSubmitPduCdma(str, str2, str3, z2, smsHeader, i);
        }
        return getSubmitPduGsm(str, str2, str3, z2, i2, i3);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPduGsm(String str, String str2, String str3, boolean z) {
        return SmsMessage.getSubmitPdu(str, str2, str3, z);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPduGsm(String str, String str2, String str3, boolean z, int i) {
        return SmsMessage.getSubmitPdu(str, str2, str3, z, i);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPduGsm(String str, String str2, String str3, boolean z, int i, int i2) {
        return SmsMessage.getSubmitPdu(str, str2, str3, z, (byte[]) null, 0, 0, 0, i, i2);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPduCdma(String str, String str2, String str3, boolean z, SmsHeader smsHeader) {
        return com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(str, str2, str3, z, smsHeader);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPduCdma(String str, String str2, String str3, boolean z, SmsHeader smsHeader, int i) {
        return com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(str, str2, str3, z, smsHeader, i);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPdu(boolean z, String str, String str2, int i, byte[] bArr, boolean z2) {
        if (z) {
            return getSubmitPduCdma(str, str2, i, bArr, z2);
        }
        return getSubmitPduGsm(str, str2, i, bArr, z2);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPdu(boolean z, String str, String str2, int i, byte[] bArr, boolean z2, int i2) {
        if (z) {
            return getSubmitPduCdma(str, str2, i, bArr, z2);
        }
        return getSubmitPduGsm(str, str2, i, bArr, z2, i2);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPduCdma(String str, String str2, int i, byte[] bArr, boolean z) {
        return com.android.internal.telephony.cdma.SmsMessage.getSubmitPdu(str, str2, i, bArr, z);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPduGsm(String str, String str2, int i, byte[] bArr, boolean z) {
        return SmsMessage.getSubmitPdu(str, str2, i, bArr, z);
    }

    public static SmsMessageBase.SubmitPduBase getSubmitPduGsm(String str, String str2, int i, byte[] bArr, boolean z, int i2) {
        return SmsMessage.getSubmitPdu(str, str2, i, bArr, z, i2);
    }

    public static GsmAlphabet.TextEncodingDetails calculateLength(boolean z, CharSequence charSequence, boolean z2) {
        if (z) {
            return calculateLengthCdma(charSequence, z2);
        }
        return calculateLengthGsm(charSequence, z2);
    }

    public static GsmAlphabet.TextEncodingDetails calculateLengthGsm(CharSequence charSequence, boolean z) {
        return SmsMessage.calculateLength(charSequence, z);
    }

    public static GsmAlphabet.TextEncodingDetails calculateLengthCdma(CharSequence charSequence, boolean z) {
        return com.android.internal.telephony.cdma.SmsMessage.calculateLength(charSequence, z, false);
    }
}
