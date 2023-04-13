package com.android.internal.telephony;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.Rlog;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/* loaded from: classes.dex */
public class MissedIncomingCallSmsFilter {
    private static final ComponentName PSTN_CONNECTION_SERVICE_COMPONENT = new ComponentName("com.android.phone", "com.android.services.telephony.TelephonyConnectionService");
    private static final String TAG = "MissedIncomingCallSmsFilter";
    private PersistableBundle mCarrierConfig;
    private final Phone mPhone;

    public MissedIncomingCallSmsFilter(Phone phone) {
        this.mPhone = phone;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) phone.getContext().getSystemService("carrier_config");
        if (carrierConfigManager != null) {
            this.mCarrierConfig = carrierConfigManager.getConfigForSubId(phone.getSubId());
        }
    }

    public boolean filter(byte[][] bArr, String str) {
        PersistableBundle persistableBundle;
        String[] stringArray;
        SmsMessage createFromPdu;
        if (bArr.length != 1 || (persistableBundle = this.mCarrierConfig) == null || (stringArray = persistableBundle.getStringArray("missed_incoming_call_sms_originator_string_array")) == null || (createFromPdu = SmsMessage.createFromPdu(bArr[0], str)) == null || TextUtils.isEmpty(createFromPdu.getOriginatingAddress()) || !Arrays.asList(stringArray).contains(createFromPdu.getOriginatingAddress())) {
            return false;
        }
        return processSms(createFromPdu);
    }

    private long getEpochTime(String str, String str2, String str3, String str4, String str5) {
        LocalDateTime parse;
        LocalDateTime now = LocalDateTime.now();
        if (TextUtils.isEmpty(str)) {
            str = Integer.toString(now.getYear());
        }
        do {
            DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            parse = LocalDateTime.parse(str + str2 + str3 + str4 + str5, ofPattern);
            str = Integer.toString(Integer.parseInt(str) + (-1));
        } while (parse.isAfter(now));
        return parse.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(13:12|13|(2:14|15)|(7:17|18|19|20|21|22|23)|24|25|26|27|(4:29|30|31|(1:33))|36|37|38|39) */
    /* JADX WARN: Can't wrap try/catch for region: R(14:12|13|14|15|(7:17|18|19|20|21|22|23)|24|25|26|27|(4:29|30|31|(1:33))|36|37|38|39) */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0062, code lost:
        r9 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x008a, code lost:
        android.telephony.Rlog.d(com.android.internal.telephony.MissedIncomingCallSmsFilter.TAG, "Caller id is not provided or can't be parsed.");
     */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0067  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean processSms(SmsMessage smsMessage) {
        Matcher matcher;
        String str;
        String str2;
        String str3;
        long currentTimeMillis;
        String str4;
        String str5;
        String str6;
        String str7;
        String[] stringArray = this.mCarrierConfig.getStringArray("missed_incoming_call_sms_pattern_string_array");
        if (stringArray == null || stringArray.length == 0) {
            Rlog.w(TAG, "Missed incoming call SMS pattern is not configured!");
            return false;
        }
        for (String str8 : stringArray) {
            try {
                matcher = Pattern.compile(str8, 33).matcher(smsMessage.getMessageBody());
            } catch (PatternSyntaxException e) {
                Rlog.w(TAG, "Configuration error. Unexpected missed incoming call sms pattern: " + str8 + ", e=" + e);
            }
            if (matcher.find()) {
                String str9 = null;
                try {
                    str = matcher.group("month");
                } catch (IllegalArgumentException unused) {
                    str = null;
                    str2 = null;
                }
                try {
                    str2 = matcher.group("day");
                    try {
                        str3 = matcher.group("hour");
                        try {
                            str5 = str;
                            str6 = str2;
                            str7 = str3;
                            str4 = matcher.group("minute");
                            currentTimeMillis = 0;
                        } catch (IllegalArgumentException unused2) {
                            currentTimeMillis = System.currentTimeMillis();
                            str4 = null;
                            str5 = str;
                            str6 = str2;
                            str7 = str3;
                            String str10 = matcher.group("year");
                            if (currentTimeMillis == 0) {
                            }
                            str9 = matcher.group("callerId");
                            createMissedIncomingCallEvent(currentTimeMillis, str9);
                            return true;
                        }
                    } catch (IllegalArgumentException unused3) {
                        str3 = null;
                    }
                } catch (IllegalArgumentException unused4) {
                    str2 = null;
                    str3 = str2;
                    currentTimeMillis = System.currentTimeMillis();
                    str4 = null;
                    str5 = str;
                    str6 = str2;
                    str7 = str3;
                    String str102 = matcher.group("year");
                    if (currentTimeMillis == 0) {
                    }
                    str9 = matcher.group("callerId");
                    createMissedIncomingCallEvent(currentTimeMillis, str9);
                    return true;
                }
                String str1022 = matcher.group("year");
                if (currentTimeMillis == 0) {
                    try {
                        currentTimeMillis = getEpochTime(str1022, str5, str6, str7, str4);
                        if (currentTimeMillis == 0) {
                            Rlog.e(TAG, "Can't get the time. Use the current time.");
                            currentTimeMillis = System.currentTimeMillis();
                        }
                    } catch (Exception unused5) {
                        Rlog.e(TAG, "Can't get the time for missed incoming call");
                    }
                }
                str9 = matcher.group("callerId");
                createMissedIncomingCallEvent(currentTimeMillis, str9);
                return true;
            }
        }
        Rlog.d(TAG, "SMS did not match any missed incoming call SMS pattern.");
        return false;
    }

    private static PhoneAccountHandle makePstnPhoneAccountHandle(Phone phone) {
        return new PhoneAccountHandle(PSTN_CONNECTION_SERVICE_COMPONENT, String.valueOf(phone.getSubId()), phone.getUserHandle());
    }

    private void createMissedIncomingCallEvent(long j, String str) {
        TelecomManager telecomManager = (TelecomManager) this.mPhone.getContext().getSystemService("telecom");
        if (telecomManager != null) {
            Bundle bundle = new Bundle();
            if (str != null) {
                bundle.putParcelable("android.telecom.extra.INCOMING_CALL_ADDRESS", Uri.fromParts("tel", str, null));
            }
            bundle.putLong("android.telecom.extra.CALL_CREATED_EPOCH_TIME_MILLIS", j);
            telecomManager.addNewIncomingCall(makePstnPhoneAccountHandle(this.mPhone), bundle);
        }
    }
}
