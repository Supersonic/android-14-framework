package android.telephony;

import android.annotation.SystemApi;
import android.app.PendingIntent;
import android.compat.Compatibility;
import android.content.Context;
import android.database.CursorWindow;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import com.android.internal.telephony.IIntegerConsumer;
import com.android.internal.telephony.IPhoneSubInfo;
import com.android.internal.telephony.ISms;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.SmsRawData;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public final class SmsManager {
    public static final int CDMA_SMS_RECORD_LENGTH = 255;
    public static final String EXTRA_MMS_DATA = "android.telephony.extra.MMS_DATA";
    public static final String EXTRA_MMS_HTTP_STATUS = "android.telephony.extra.MMS_HTTP_STATUS";
    public static final String EXTRA_SIM_SUBSCRIPTION_ID = "android.telephony.extra.SIM_SUBSCRIPTION_ID";
    public static final String EXTRA_SMS_MESSAGE = "android.telephony.extra.SMS_MESSAGE";
    public static final String EXTRA_STATUS = "android.telephony.extra.STATUS";
    private static final long GET_TARGET_SDK_VERSION_CODE_CHANGE = 145147528;
    public static final String MMS_CONFIG_ALIAS_ENABLED = "aliasEnabled";
    public static final String MMS_CONFIG_ALIAS_MAX_CHARS = "aliasMaxChars";
    public static final String MMS_CONFIG_ALIAS_MIN_CHARS = "aliasMinChars";
    public static final String MMS_CONFIG_ALLOW_ATTACH_AUDIO = "allowAttachAudio";
    public static final String MMS_CONFIG_APPEND_TRANSACTION_ID = "enabledTransID";
    public static final String MMS_CONFIG_CLOSE_CONNECTION = "mmsCloseConnection";
    public static final String MMS_CONFIG_EMAIL_GATEWAY_NUMBER = "emailGatewayNumber";
    public static final String MMS_CONFIG_GROUP_MMS_ENABLED = "enableGroupMms";
    public static final String MMS_CONFIG_HTTP_PARAMS = "httpParams";
    public static final String MMS_CONFIG_HTTP_SOCKET_TIMEOUT = "httpSocketTimeout";
    public static final String MMS_CONFIG_MAX_IMAGE_HEIGHT = "maxImageHeight";
    public static final String MMS_CONFIG_MAX_IMAGE_WIDTH = "maxImageWidth";
    public static final String MMS_CONFIG_MAX_MESSAGE_SIZE = "maxMessageSize";
    public static final String MMS_CONFIG_MESSAGE_TEXT_MAX_SIZE = "maxMessageTextSize";
    public static final String MMS_CONFIG_MMS_DELIVERY_REPORT_ENABLED = "enableMMSDeliveryReports";
    public static final String MMS_CONFIG_MMS_ENABLED = "enabledMMS";
    public static final String MMS_CONFIG_MMS_READ_REPORT_ENABLED = "enableMMSReadReports";
    public static final String MMS_CONFIG_MULTIPART_SMS_ENABLED = "enableMultipartSMS";
    public static final String MMS_CONFIG_NAI_SUFFIX = "naiSuffix";
    public static final String MMS_CONFIG_NOTIFY_WAP_MMSC_ENABLED = "enabledNotifyWapMMSC";
    public static final String MMS_CONFIG_RECIPIENT_LIMIT = "recipientLimit";
    public static final String MMS_CONFIG_SEND_MULTIPART_SMS_AS_SEPARATE_MESSAGES = "sendMultipartSmsAsSeparateMessages";
    public static final String MMS_CONFIG_SHOW_CELL_BROADCAST_APP_LINKS = "config_cellBroadcastAppLinks";
    public static final String MMS_CONFIG_SMS_DELIVERY_REPORT_ENABLED = "enableSMSDeliveryReports";
    public static final String MMS_CONFIG_SMS_TO_MMS_TEXT_LENGTH_THRESHOLD = "smsToMmsTextLengthThreshold";
    public static final String MMS_CONFIG_SMS_TO_MMS_TEXT_THRESHOLD = "smsToMmsTextThreshold";
    public static final String MMS_CONFIG_SUBJECT_MAX_LENGTH = "maxSubjectLength";
    public static final String MMS_CONFIG_SUPPORT_HTTP_CHARSET_HEADER = "supportHttpCharsetHeader";
    public static final String MMS_CONFIG_SUPPORT_MMS_CONTENT_DISPOSITION = "supportMmsContentDisposition";
    public static final String MMS_CONFIG_UA_PROF_TAG_NAME = "uaProfTagName";
    public static final String MMS_CONFIG_UA_PROF_URL = "uaProfUrl";
    public static final String MMS_CONFIG_USER_AGENT = "userAgent";
    public static final int MMS_ERROR_CONFIGURATION_ERROR = 7;
    public static final int MMS_ERROR_DATA_DISABLED = 11;
    public static final int MMS_ERROR_HTTP_FAILURE = 4;
    public static final int MMS_ERROR_INACTIVE_SUBSCRIPTION = 10;
    public static final int MMS_ERROR_INVALID_APN = 2;
    public static final int MMS_ERROR_INVALID_SUBSCRIPTION_ID = 9;
    public static final int MMS_ERROR_IO_ERROR = 5;
    public static final int MMS_ERROR_NO_DATA_NETWORK = 8;
    public static final int MMS_ERROR_RETRY = 6;
    public static final int MMS_ERROR_UNABLE_CONNECT_MMS = 3;
    public static final int MMS_ERROR_UNSPECIFIED = 1;
    @SystemApi
    public static final int PREMIUM_SMS_CONSENT_ALWAYS_ALLOW = 3;
    @SystemApi
    public static final int PREMIUM_SMS_CONSENT_ASK_USER = 1;
    @SystemApi
    public static final int PREMIUM_SMS_CONSENT_NEVER_ALLOW = 2;
    @SystemApi
    public static final int PREMIUM_SMS_CONSENT_UNKNOWN = 0;
    public static final String REGEX_PREFIX_DELIMITER = ",";
    public static final int RESULT_BLUETOOTH_DISCONNECTED = 27;
    public static final int RESULT_CANCELLED = 23;
    public static final int RESULT_ENCODING_ERROR = 18;
    public static final int RESULT_ERROR_FDN_CHECK_FAILURE = 6;
    public static final int RESULT_ERROR_GENERIC_FAILURE = 1;
    public static final int RESULT_ERROR_LIMIT_EXCEEDED = 5;
    public static final int RESULT_ERROR_NONE = 0;
    public static final int RESULT_ERROR_NO_SERVICE = 4;
    public static final int RESULT_ERROR_NULL_PDU = 3;
    public static final int RESULT_ERROR_RADIO_OFF = 2;
    public static final int RESULT_ERROR_SHORT_CODE_NEVER_ALLOWED = 8;
    public static final int RESULT_ERROR_SHORT_CODE_NOT_ALLOWED = 7;
    public static final int RESULT_INTERNAL_ERROR = 21;
    public static final int RESULT_INVALID_ARGUMENTS = 11;
    public static final int RESULT_INVALID_BLUETOOTH_ADDRESS = 26;
    public static final int RESULT_INVALID_SMSC_ADDRESS = 19;
    public static final int RESULT_INVALID_SMS_FORMAT = 14;
    public static final int RESULT_INVALID_STATE = 12;
    public static final int RESULT_MODEM_ERROR = 16;
    public static final int RESULT_NETWORK_ERROR = 17;
    public static final int RESULT_NETWORK_REJECT = 10;
    public static final int RESULT_NO_BLUETOOTH_SERVICE = 25;
    public static final int RESULT_NO_DEFAULT_SMS_APP = 32;
    public static final int RESULT_NO_MEMORY = 13;
    public static final int RESULT_NO_RESOURCES = 22;
    public static final int RESULT_OPERATION_NOT_ALLOWED = 20;
    public static final int RESULT_RADIO_NOT_AVAILABLE = 9;
    public static final int RESULT_RECEIVE_DISPATCH_FAILURE = 500;
    public static final int RESULT_RECEIVE_INJECTED_NULL_PDU = 501;
    public static final int RESULT_RECEIVE_NULL_MESSAGE_FROM_RIL = 503;
    public static final int RESULT_RECEIVE_RUNTIME_EXCEPTION = 502;
    public static final int RESULT_RECEIVE_SQL_EXCEPTION = 505;
    public static final int RESULT_RECEIVE_URI_EXCEPTION = 506;
    public static final int RESULT_RECEIVE_WHILE_ENCRYPTED = 504;
    public static final int RESULT_REMOTE_EXCEPTION = 31;
    public static final int RESULT_REQUEST_NOT_SUPPORTED = 24;
    public static final int RESULT_RIL_ABORTED = 137;
    public static final int RESULT_RIL_ACCESS_BARRED = 122;
    public static final int RESULT_RIL_BLOCKED_DUE_TO_CALL = 123;
    public static final int RESULT_RIL_CANCELLED = 119;
    public static final int RESULT_RIL_DEVICE_IN_USE = 136;
    public static final int RESULT_RIL_ENCODING_ERR = 109;
    public static final int RESULT_RIL_GENERIC_ERROR = 124;
    public static final int RESULT_RIL_INTERNAL_ERR = 113;
    public static final int RESULT_RIL_INVALID_ARGUMENTS = 104;
    public static final int RESULT_RIL_INVALID_MODEM_STATE = 115;
    public static final int RESULT_RIL_INVALID_RESPONSE = 125;
    public static final int RESULT_RIL_INVALID_SIM_STATE = 130;
    public static final int RESULT_RIL_INVALID_SMSC_ADDRESS = 110;
    public static final int RESULT_RIL_INVALID_SMS_FORMAT = 107;
    public static final int RESULT_RIL_INVALID_STATE = 103;
    public static final int RESULT_RIL_MODEM_ERR = 111;
    public static final int RESULT_RIL_NETWORK_ERR = 112;
    public static final int RESULT_RIL_NETWORK_NOT_READY = 116;
    public static final int RESULT_RIL_NETWORK_REJECT = 102;
    public static final int RESULT_RIL_NO_MEMORY = 105;
    public static final int RESULT_RIL_NO_NETWORK_FOUND = 135;
    public static final int RESULT_RIL_NO_RESOURCES = 118;
    public static final int RESULT_RIL_NO_SMS_TO_ACK = 131;
    public static final int RESULT_RIL_NO_SUBSCRIPTION = 134;
    public static final int RESULT_RIL_OPERATION_NOT_ALLOWED = 117;
    public static final int RESULT_RIL_RADIO_NOT_AVAILABLE = 100;
    public static final int RESULT_RIL_REQUEST_NOT_SUPPORTED = 114;
    public static final int RESULT_RIL_REQUEST_RATE_LIMITED = 106;
    public static final int RESULT_RIL_SIMULTANEOUS_SMS_AND_CALL_NOT_ALLOWED = 121;
    public static final int RESULT_RIL_SIM_ABSENT = 120;
    public static final int RESULT_RIL_SIM_BUSY = 132;
    public static final int RESULT_RIL_SIM_ERROR = 129;
    public static final int RESULT_RIL_SIM_FULL = 133;
    public static final int RESULT_RIL_SIM_PIN2 = 126;
    public static final int RESULT_RIL_SIM_PUK2 = 127;
    public static final int RESULT_RIL_SMS_SEND_FAIL_RETRY = 101;
    public static final int RESULT_RIL_SUBSCRIPTION_NOT_AVAILABLE = 128;
    public static final int RESULT_RIL_SYSTEM_ERR = 108;
    public static final int RESULT_SMS_BLOCKED_DURING_EMERGENCY = 29;
    public static final int RESULT_SMS_SEND_RETRY_FAILED = 30;
    public static final int RESULT_STATUS_SUCCESS = 0;
    public static final int RESULT_STATUS_TIMEOUT = 1;
    public static final int RESULT_SYSTEM_ERROR = 15;
    public static final int RESULT_UNEXPECTED_EVENT_STOP_SENDING = 28;
    public static final int RESULT_USER_NOT_ALLOWED = 33;
    public static final int SMS_CATEGORY_FREE_SHORT_CODE = 1;
    public static final int SMS_CATEGORY_NOT_SHORT_CODE = 0;
    public static final int SMS_CATEGORY_POSSIBLE_PREMIUM_SHORT_CODE = 3;
    public static final int SMS_CATEGORY_PREMIUM_SHORT_CODE = 4;
    public static final int SMS_CATEGORY_STANDARD_SHORT_CODE = 2;
    public static final int SMS_MESSAGE_PERIOD_NOT_SPECIFIED = -1;
    public static final int SMS_MESSAGE_PRIORITY_NOT_SPECIFIED = -1;
    public static final int SMS_RECORD_LENGTH = 176;
    public static final int SMS_RP_CAUSE_CALL_BARRING = 10;
    public static final int SMS_RP_CAUSE_CONGESTION = 42;
    public static final int SMS_RP_CAUSE_DESTINATION_OUT_OF_ORDER = 27;
    public static final int SMS_RP_CAUSE_FACILITY_NOT_IMPLEMENTED = 69;
    public static final int SMS_RP_CAUSE_FACILITY_NOT_SUBSCRIBED = 50;
    public static final int SMS_RP_CAUSE_FACILITY_REJECTED = 29;
    public static final int SMS_RP_CAUSE_INFORMATION_ELEMENT_NON_EXISTENT = 99;
    public static final int SMS_RP_CAUSE_INTERWORKING_UNSPECIFIED = 127;
    public static final int SMS_RP_CAUSE_INVALID_MANDATORY_INFORMATION = 96;
    public static final int SMS_RP_CAUSE_INVALID_MESSAGE_REFERENCE_VALUE = 81;
    public static final int SMS_RP_CAUSE_MESSAGE_INCOMPATIBLE_WITH_PROTOCOL_STATE = 98;
    public static final int SMS_RP_CAUSE_MESSAGE_TYPE_NON_EXISTENT = 97;
    public static final int SMS_RP_CAUSE_NETWORK_OUT_OF_ORDER = 38;
    public static final int SMS_RP_CAUSE_OPERATOR_DETERMINED_BARRING = 8;
    public static final int SMS_RP_CAUSE_PROTOCOL_ERROR = 111;
    public static final int SMS_RP_CAUSE_RESERVED = 11;
    public static final int SMS_RP_CAUSE_RESOURCES_UNAVAILABLE = 47;
    public static final int SMS_RP_CAUSE_SEMANTICALLY_INCORRECT_MESSAGE = 95;
    public static final int SMS_RP_CAUSE_SHORT_MESSAGE_TRANSFER_REJECTED = 21;
    public static final int SMS_RP_CAUSE_TEMPORARY_FAILURE = 41;
    public static final int SMS_RP_CAUSE_UNALLOCATED_NUMBER = 1;
    public static final int SMS_RP_CAUSE_UNIDENTIFIED_SUBSCRIBER = 28;
    public static final int SMS_RP_CAUSE_UNKNOWN_SUBSCRIBER = 30;
    public static final int STATUS_ON_ICC_FREE = 0;
    public static final int STATUS_ON_ICC_READ = 1;
    public static final int STATUS_ON_ICC_SENT = 5;
    public static final int STATUS_ON_ICC_UNREAD = 3;
    public static final int STATUS_ON_ICC_UNSENT = 7;
    private static final String TAG = "SmsManager";
    private final Context mContext;
    private int mSubId;
    private static final Object sLockObject = new Object();
    private static final Map<Pair<Context, Integer>, SmsManager> sSubInstances = new ArrayMap();
    private static final SmsManager DEFAULT_INSTANCE = getSmsManagerForContextAndSubscriptionId(null, Integer.MAX_VALUE);

    /* loaded from: classes3.dex */
    public static abstract class FinancialSmsCallback {
        public abstract void onFinancialSmsMessages(CursorWindow cursorWindow);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface PremiumSmsConsent {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Result {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SMS_RP_CAUSE {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface SmsShortCodeCategory {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface StatusOnIcc {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public interface SubscriptionResolverResult {
        void onFailure();

        void onSuccess(int i);
    }

    private String getOpPackageName() {
        Context context = this.mContext;
        if (context == null) {
            return null;
        }
        return context.getOpPackageName();
    }

    private String getAttributionTag() {
        Context context = this.mContext;
        if (context == null) {
            return null;
        }
        return context.getAttributionTag();
    }

    public void sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        sendTextMessageInternal(destinationAddress, scAddress, text, sentIntent, deliveryIntent, true, getOpPackageName(), getAttributionTag(), 0L);
    }

    public void sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, long messageId) {
        sendTextMessageInternal(destinationAddress, scAddress, text, sentIntent, deliveryIntent, true, getOpPackageName(), getAttributionTag(), messageId);
    }

    public void sendTextMessage(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent, int priority, boolean expectMore, int validityPeriod) {
        sendTextMessageInternal(destinationAddress, scAddress, text, sentIntent, deliveryIntent, true, priority, expectMore, validityPeriod);
    }

    private void sendTextMessageInternal(final String destinationAddress, final String scAddress, final String text, final PendingIntent sentIntent, final PendingIntent deliveryIntent, final boolean persistMessage, final String packageName, final String attributionTag, final long messageId) {
        if (TextUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("Invalid destinationAddress");
        }
        if (TextUtils.isEmpty(text)) {
            throw new IllegalArgumentException("Invalid message body");
        }
        if (persistMessage) {
            resolveSubscriptionForOperation(new SubscriptionResolverResult() { // from class: android.telephony.SmsManager.1
                @Override // android.telephony.SmsManager.SubscriptionResolverResult
                public void onSuccess(int subId) {
                    ISms iSms = SmsManager.getISmsServiceOrThrow();
                    try {
                        iSms.sendTextForSubscriber(subId, packageName, attributionTag, destinationAddress, scAddress, text, sentIntent, deliveryIntent, persistMessage, messageId);
                    } catch (RemoteException e) {
                        Log.m110e(SmsManager.TAG, "sendTextMessageInternal: Couldn't send SMS, exception - " + e.getMessage() + " " + SmsManager.formatCrossStackMessageId(messageId));
                        SmsManager.notifySmsError(sentIntent, 31);
                    }
                }

                @Override // android.telephony.SmsManager.SubscriptionResolverResult
                public void onFailure() {
                    SmsManager.notifySmsError(sentIntent, 32);
                }
            });
            return;
        }
        ISms iSms = getISmsServiceOrThrow();
        try {
            iSms.sendTextForSubscriber(getSubscriptionId(), packageName, attributionTag, destinationAddress, scAddress, text, sentIntent, deliveryIntent, persistMessage, messageId);
        } catch (RemoteException e) {
            Log.m110e(TAG, "sendTextMessageInternal (no persist): Couldn't send SMS, exception - " + e.getMessage() + " " + formatCrossStackMessageId(messageId));
            notifySmsError(sentIntent, 31);
        }
    }

    public void sendTextMessageWithoutPersisting(String destinationAddress, String scAddress, String text, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        sendTextMessageInternal(destinationAddress, scAddress, text, sentIntent, deliveryIntent, false, getOpPackageName(), getAttributionTag(), 0L);
    }

    private void sendTextMessageInternal(final String destinationAddress, final String scAddress, final String text, final PendingIntent sentIntent, final PendingIntent deliveryIntent, final boolean persistMessage, int priority, final boolean expectMore, int validityPeriod) {
        int priority2;
        int validityPeriod2 = validityPeriod;
        if (TextUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("Invalid destinationAddress");
        }
        if (TextUtils.isEmpty(text)) {
            throw new IllegalArgumentException("Invalid message body");
        }
        if (priority >= 0 && priority <= 3) {
            priority2 = priority;
        } else {
            Log.m110e(TAG, "Invalid Priority " + priority);
            priority2 = -1;
        }
        if (validityPeriod2 < 5 || validityPeriod2 > 635040) {
            Log.m110e(TAG, "Invalid Validity Period " + validityPeriod2);
            validityPeriod2 = -1;
        }
        final int finalPriority = priority2;
        final int finalValidity = validityPeriod2;
        if (persistMessage) {
            resolveSubscriptionForOperation(new SubscriptionResolverResult() { // from class: android.telephony.SmsManager.2
                @Override // android.telephony.SmsManager.SubscriptionResolverResult
                public void onSuccess(int subId) {
                    try {
                        ISms iSms = SmsManager.getISmsServiceOrThrow();
                        if (iSms != null) {
                            iSms.sendTextForSubscriberWithOptions(subId, null, null, destinationAddress, scAddress, text, sentIntent, deliveryIntent, persistMessage, finalPriority, expectMore, finalValidity);
                        }
                    } catch (RemoteException e) {
                        Log.m110e(SmsManager.TAG, "sendTextMessageInternal: Couldn't send SMS, exception - " + e.getMessage());
                        SmsManager.notifySmsError(sentIntent, 31);
                    }
                }

                @Override // android.telephony.SmsManager.SubscriptionResolverResult
                public void onFailure() {
                    SmsManager.notifySmsError(sentIntent, 32);
                }
            });
            return;
        }
        try {
            ISms iSms = getISmsServiceOrThrow();
            if (iSms != null) {
                iSms.sendTextForSubscriberWithOptions(getSubscriptionId(), null, null, destinationAddress, scAddress, text, sentIntent, deliveryIntent, persistMessage, finalPriority, expectMore, finalValidity);
            }
        } catch (RemoteException e) {
            Log.m110e(TAG, "sendTextMessageInternal(no persist): Couldn't send SMS, exception - " + e.getMessage());
            notifySmsError(sentIntent, 31);
        }
    }

    public void injectSmsPdu(byte[] pdu, String format, PendingIntent receivedIntent) {
        if (!format.equals("3gpp") && !format.equals("3gpp2")) {
            throw new IllegalArgumentException("Invalid pdu format. format must be either 3gpp or 3gpp2");
        }
        try {
            ISms iSms = TelephonyManager.getSmsService();
            if (iSms != null) {
                iSms.injectSmsPduForSubscriber(getSubscriptionId(), pdu, format, receivedIntent);
            }
        } catch (RemoteException e) {
            if (receivedIntent != null) {
                try {
                    receivedIntent.send(31);
                } catch (PendingIntent.CanceledException e2) {
                }
            }
        }
    }

    public ArrayList<String> divideMessage(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text is null");
        }
        return SmsMessage.fragmentText(text, getSubscriptionId());
    }

    public void sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents) {
        sendMultipartTextMessageInternal(destinationAddress, scAddress, (List<String>) parts, (List<PendingIntent>) sentIntents, (List<PendingIntent>) deliveryIntents, true, getOpPackageName(), getAttributionTag(), 0L);
    }

    public void sendMultipartTextMessage(String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents, long messageId) {
        sendMultipartTextMessageInternal(destinationAddress, scAddress, parts, sentIntents, deliveryIntents, true, getOpPackageName(), getAttributionTag(), messageId);
    }

    public void sendMultipartTextMessage(String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents, String packageName, String attributionTag) {
        sendMultipartTextMessageInternal(destinationAddress, scAddress, parts, sentIntents, deliveryIntents, true, packageName, attributionTag, 0L);
    }

    private void sendMultipartTextMessageInternal(final String destinationAddress, final String scAddress, final List<String> parts, final List<PendingIntent> sentIntents, final List<PendingIntent> deliveryIntents, final boolean persistMessage, final String packageName, final String attributionTag, final long messageId) {
        PendingIntent deliveryIntent;
        if (TextUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("Invalid destinationAddress");
        }
        if (parts == null || parts.size() < 1) {
            throw new IllegalArgumentException("Invalid message body");
        }
        if (parts.size() > 1) {
            if (persistMessage) {
                resolveSubscriptionForOperation(new SubscriptionResolverResult() { // from class: android.telephony.SmsManager.3
                    @Override // android.telephony.SmsManager.SubscriptionResolverResult
                    public void onSuccess(int subId) {
                        try {
                            ISms iSms = SmsManager.getISmsServiceOrThrow();
                            iSms.sendMultipartTextForSubscriber(subId, packageName, attributionTag, destinationAddress, scAddress, parts, sentIntents, deliveryIntents, persistMessage, messageId);
                        } catch (RemoteException e) {
                            Log.m110e(SmsManager.TAG, "sendMultipartTextMessageInternal: Couldn't send SMS - " + e.getMessage() + " " + SmsManager.formatCrossStackMessageId(messageId));
                            SmsManager.notifySmsError(sentIntents, 31);
                        }
                    }

                    @Override // android.telephony.SmsManager.SubscriptionResolverResult
                    public void onFailure() {
                        SmsManager.notifySmsError(sentIntents, 32);
                    }
                });
                return;
            }
            try {
                ISms iSms = getISmsServiceOrThrow();
                if (iSms != null) {
                    iSms.sendMultipartTextForSubscriber(getSubscriptionId(), packageName, attributionTag, destinationAddress, scAddress, parts, sentIntents, deliveryIntents, persistMessage, messageId);
                    return;
                }
                return;
            } catch (RemoteException e) {
                Log.m110e(TAG, "sendMultipartTextMessageInternal: Couldn't send SMS - " + e.getMessage() + " " + formatCrossStackMessageId(messageId));
                notifySmsError(sentIntents, 31);
                return;
            }
        }
        PendingIntent sentIntent = null;
        if (sentIntents != null && sentIntents.size() > 0) {
            sentIntent = sentIntents.get(0);
        }
        if (deliveryIntents != null && deliveryIntents.size() > 0) {
            PendingIntent deliveryIntent2 = deliveryIntents.get(0);
            deliveryIntent = deliveryIntent2;
        } else {
            deliveryIntent = null;
        }
        sendTextMessageInternal(destinationAddress, scAddress, parts.get(0), sentIntent, deliveryIntent, true, packageName, attributionTag, messageId);
    }

    @SystemApi
    public void sendMultipartTextMessageWithoutPersisting(String destinationAddress, String scAddress, List<String> parts, List<PendingIntent> sentIntents, List<PendingIntent> deliveryIntents) {
        sendMultipartTextMessageInternal(destinationAddress, scAddress, parts, sentIntents, deliveryIntents, false, getOpPackageName(), getAttributionTag(), 0L);
    }

    public void sendMultipartTextMessage(String destinationAddress, String scAddress, ArrayList<String> parts, ArrayList<PendingIntent> sentIntents, ArrayList<PendingIntent> deliveryIntents, int priority, boolean expectMore, int validityPeriod) {
        sendMultipartTextMessageInternal(destinationAddress, scAddress, (List<String>) parts, (List<PendingIntent>) sentIntents, (List<PendingIntent>) deliveryIntents, true, priority, expectMore, validityPeriod);
    }

    private void sendMultipartTextMessageInternal(final String destinationAddress, final String scAddress, final List<String> parts, final List<PendingIntent> sentIntents, final List<PendingIntent> deliveryIntents, final boolean persistMessage, int priority, final boolean expectMore, int validityPeriod) {
        int priority2;
        int validityPeriod2;
        List<PendingIntent> list;
        if (TextUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("Invalid destinationAddress");
        }
        if (parts == null || parts.size() < 1) {
            throw new IllegalArgumentException("Invalid message body");
        }
        if (priority < 0 || priority > 3) {
            Log.m110e(TAG, "Invalid Priority " + priority);
            priority2 = -1;
        } else {
            priority2 = priority;
        }
        if (validityPeriod < 5 || validityPeriod > 635040) {
            Log.m110e(TAG, "Invalid Validity Period " + validityPeriod);
            validityPeriod2 = -1;
        } else {
            validityPeriod2 = validityPeriod;
        }
        if (parts.size() > 1) {
            final int finalPriority = priority2;
            final int finalValidity = validityPeriod2;
            if (persistMessage) {
                resolveSubscriptionForOperation(new SubscriptionResolverResult() { // from class: android.telephony.SmsManager.4
                    @Override // android.telephony.SmsManager.SubscriptionResolverResult
                    public void onSuccess(int subId) {
                        try {
                            ISms iSms = SmsManager.getISmsServiceOrThrow();
                            if (iSms != null) {
                                iSms.sendMultipartTextForSubscriberWithOptions(subId, null, null, destinationAddress, scAddress, parts, sentIntents, deliveryIntents, persistMessage, finalPriority, expectMore, finalValidity);
                            }
                        } catch (RemoteException e) {
                            Log.m110e(SmsManager.TAG, "sendMultipartTextMessageInternal: Couldn't send SMS - " + e.getMessage());
                            SmsManager.notifySmsError(sentIntents, 31);
                        }
                    }

                    @Override // android.telephony.SmsManager.SubscriptionResolverResult
                    public void onFailure() {
                        SmsManager.notifySmsError(sentIntents, 32);
                    }
                });
                return;
            }
            try {
                ISms iSms = getISmsServiceOrThrow();
                if (iSms != null) {
                    list = sentIntents;
                    try {
                        iSms.sendMultipartTextForSubscriberWithOptions(getSubscriptionId(), null, null, destinationAddress, scAddress, parts, sentIntents, deliveryIntents, persistMessage, finalPriority, expectMore, finalValidity);
                    } catch (RemoteException e) {
                        e = e;
                        Log.m110e(TAG, "sendMultipartTextMessageInternal (no persist): Couldn't send SMS - " + e.getMessage());
                        notifySmsError(list, 31);
                    }
                }
            } catch (RemoteException e2) {
                e = e2;
                list = sentIntents;
            }
        } else {
            PendingIntent sentIntent = null;
            PendingIntent deliveryIntent = null;
            if (sentIntents != null && sentIntents.size() > 0) {
                sentIntent = sentIntents.get(0);
            }
            if (deliveryIntents != null && deliveryIntents.size() > 0) {
                deliveryIntent = deliveryIntents.get(0);
            }
            sendTextMessageInternal(destinationAddress, scAddress, parts.get(0), sentIntent, deliveryIntent, persistMessage, priority2, expectMore, validityPeriod2);
        }
    }

    public void sendDataMessage(final String destinationAddress, final String scAddress, final short destinationPort, final byte[] data, final PendingIntent sentIntent, final PendingIntent deliveryIntent) {
        if (TextUtils.isEmpty(destinationAddress)) {
            throw new IllegalArgumentException("Invalid destinationAddress");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Invalid message data");
        }
        resolveSubscriptionForOperation(new SubscriptionResolverResult() { // from class: android.telephony.SmsManager.5
            @Override // android.telephony.SmsManager.SubscriptionResolverResult
            public void onSuccess(int subId) {
                try {
                    ISms iSms = SmsManager.getISmsServiceOrThrow();
                    iSms.sendDataForSubscriber(subId, null, null, destinationAddress, scAddress, 65535 & destinationPort, data, sentIntent, deliveryIntent);
                } catch (RemoteException e) {
                    Log.m110e(SmsManager.TAG, "sendDataMessage: Couldn't send SMS - Exception: " + e.getMessage());
                    SmsManager.notifySmsError(sentIntent, 31);
                }
            }

            @Override // android.telephony.SmsManager.SubscriptionResolverResult
            public void onFailure() {
                SmsManager.notifySmsError(sentIntent, 32);
            }
        });
    }

    @Deprecated
    public static SmsManager getDefault() {
        return DEFAULT_INSTANCE;
    }

    public static SmsManager getSmsManagerForContextAndSubscriptionId(Context context, int subId) {
        SmsManager smsManager;
        synchronized (sLockObject) {
            Pair<Context, Integer> key = new Pair<>(context, Integer.valueOf(subId));
            Map<Pair<Context, Integer>, SmsManager> map = sSubInstances;
            smsManager = map.get(key);
            if (smsManager == null) {
                smsManager = new SmsManager(context, subId);
                map.put(key, smsManager);
            }
        }
        return smsManager;
    }

    @Deprecated
    public static SmsManager getSmsManagerForSubscriptionId(int subId) {
        return getSmsManagerForContextAndSubscriptionId(null, subId);
    }

    public SmsManager createForSubscriptionId(int subId) {
        return getSmsManagerForContextAndSubscriptionId(this.mContext, subId);
    }

    private SmsManager(Context context, int subId) {
        this.mContext = context;
        this.mSubId = subId;
    }

    public int getSubscriptionId() {
        try {
            int i = this.mSubId;
            if (i != Integer.MAX_VALUE) {
                return i;
            }
            return getISmsServiceOrThrow().getPreferredSmsSubscription();
        } catch (RemoteException e) {
            return -1;
        }
    }

    private void resolveSubscriptionForOperation(final SubscriptionResolverResult resolverResult) {
        int subId = getSubscriptionId();
        boolean isSmsSimPickActivityNeeded = false;
        try {
            ISms iSms = getISmsService();
            if (iSms != null) {
                isSmsSimPickActivityNeeded = iSms.isSmsSimPickActivityNeeded(subId);
            }
        } catch (RemoteException ex) {
            Log.m109e(TAG, "resolveSubscriptionForOperation", ex);
        }
        if (isSmsSimPickActivityNeeded) {
            Log.m112d(TAG, "resolveSubscriptionForOperation isSmsSimPickActivityNeeded is true for calling package. ");
            try {
                getITelephony().enqueueSmsPickResult(null, null, new IIntegerConsumer.Stub() { // from class: android.telephony.SmsManager.6
                    @Override // com.android.internal.telephony.IIntegerConsumer
                    public void accept(int subId2) {
                        SmsManager.this.sendResolverResult(resolverResult, subId2, true);
                    }
                });
                return;
            } catch (RemoteException ex2) {
                Log.m109e(TAG, "Unable to launch activity", ex2);
                sendResolverResult(resolverResult, subId, true);
                return;
            }
        }
        sendResolverResult(resolverResult, subId, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendResolverResult(SubscriptionResolverResult resolverResult, int subId, boolean pickActivityShown) {
        if (SubscriptionManager.isValidSubscriptionId(subId)) {
            resolverResult.onSuccess(subId);
        } else if (!Compatibility.isChangeEnabled((long) GET_TARGET_SDK_VERSION_CODE_CHANGE) && !pickActivityShown) {
            resolverResult.onSuccess(subId);
        } else {
            resolverResult.onFailure();
        }
    }

    private static ITelephony getITelephony() {
        ITelephony binder = ITelephony.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyServiceRegisterer().get());
        if (binder == null) {
            throw new RuntimeException("Could not find Telephony Service.");
        }
        return binder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void notifySmsError(PendingIntent pendingIntent, int error) {
        if (pendingIntent != null) {
            try {
                pendingIntent.send(error);
            } catch (PendingIntent.CanceledException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void notifySmsError(List<PendingIntent> pendingIntents, int error) {
        if (pendingIntents != null) {
            for (PendingIntent pendingIntent : pendingIntents) {
                notifySmsError(pendingIntent, error);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ISms getISmsServiceOrThrow() {
        ISms iSms = TelephonyManager.getSmsService();
        if (iSms == null) {
            throw new UnsupportedOperationException("Sms is not supported");
        }
        return iSms;
    }

    private static ISms getISmsService() {
        return TelephonyManager.getSmsService();
    }

    public boolean copyMessageToIcc(byte[] smsc, byte[] pdu, int status) {
        if (pdu == null) {
            throw new IllegalArgumentException("pdu is null");
        }
        try {
            ISms iSms = getISmsService();
            if (iSms == null) {
                return false;
            }
            boolean success = iSms.copyMessageToIccEfForSubscriber(getSubscriptionId(), null, status, pdu, smsc);
            return success;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean deleteMessageFromIcc(int messageIndex) {
        try {
            ISms iSms = getISmsService();
            if (iSms == null) {
                return false;
            }
            boolean success = iSms.updateMessageOnIccEfForSubscriber(getSubscriptionId(), null, messageIndex, 0, null);
            return success;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean updateMessageOnIcc(int messageIndex, int newStatus, byte[] pdu) {
        try {
            ISms iSms = getISmsService();
            if (iSms == null) {
                return false;
            }
            boolean success = iSms.updateMessageOnIccEfForSubscriber(getSubscriptionId(), null, messageIndex, newStatus, pdu);
            return success;
        } catch (RemoteException e) {
            return false;
        }
    }

    public List<SmsMessage> getMessagesFromIcc() {
        return getAllMessagesFromIcc();
    }

    public ArrayList<SmsMessage> getAllMessagesFromIcc() {
        List<SmsRawData> records = null;
        try {
            ISms iSms = getISmsService();
            if (iSms != null) {
                records = iSms.getAllMessagesFromIccEfForSubscriber(getSubscriptionId(), null);
            }
        } catch (RemoteException e) {
        }
        return createMessageListFromRawRecords(records);
    }

    @SystemApi
    @Deprecated
    public boolean enableCellBroadcastRange(int startMessageId, int endMessageId, int ranType) {
        boolean success = false;
        if (endMessageId < startMessageId) {
            throw new IllegalArgumentException("endMessageId < startMessageId");
        }
        try {
            ISms iSms = getISmsService();
            if (iSms != null) {
                int subId = getSubscriptionId();
                success = iSms.enableCellBroadcastRangeForSubscriber(subId, startMessageId, endMessageId, ranType);
                com.android.telephony.Rlog.m10d(TAG, "enableCellBroadcastRange: " + (success ? "succeeded" : "failed") + " at calling enableCellBroadcastRangeForSubscriber. subId = " + subId);
            }
        } catch (RemoteException ex) {
            com.android.telephony.Rlog.m9d(TAG, "enableCellBroadcastRange: ", ex);
        }
        return success;
    }

    @SystemApi
    @Deprecated
    public boolean disableCellBroadcastRange(int startMessageId, int endMessageId, int ranType) {
        boolean success = false;
        if (endMessageId < startMessageId) {
            throw new IllegalArgumentException("endMessageId < startMessageId");
        }
        try {
            ISms iSms = getISmsService();
            if (iSms != null) {
                int subId = getSubscriptionId();
                success = iSms.disableCellBroadcastRangeForSubscriber(subId, startMessageId, endMessageId, ranType);
                com.android.telephony.Rlog.m10d(TAG, "disableCellBroadcastRange: " + (success ? "succeeded" : "failed") + " at calling disableCellBroadcastRangeForSubscriber. subId = " + subId);
            }
        } catch (RemoteException ex) {
            com.android.telephony.Rlog.m9d(TAG, "disableCellBroadcastRange: ", ex);
        }
        return success;
    }

    private ArrayList<SmsMessage> createMessageListFromRawRecords(List<SmsRawData> records) {
        SmsMessage sms;
        ArrayList<SmsMessage> messages = new ArrayList<>();
        if (records != null) {
            int count = records.size();
            for (int i = 0; i < count; i++) {
                SmsRawData data = records.get(i);
                if (data != null && (sms = SmsMessage.createFromEfRecord(i + 1, data.getBytes(), getSubscriptionId())) != null) {
                    messages.add(sms);
                }
            }
        }
        return messages;
    }

    public boolean isImsSmsSupported() {
        try {
            ISms iSms = getISmsService();
            if (iSms == null) {
                return false;
            }
            boolean boSupported = iSms.isImsSmsSupportedForSubscriber(getSubscriptionId());
            return boSupported;
        } catch (RemoteException e) {
            return false;
        }
    }

    public String getImsSmsFormat() {
        try {
            ISms iSms = getISmsService();
            if (iSms == null) {
                return "unknown";
            }
            String format = iSms.getImsSmsFormatForSubscriber(getSubscriptionId());
            return format;
        } catch (RemoteException e) {
            return "unknown";
        }
    }

    public static int getDefaultSmsSubscriptionId() {
        try {
            return getISmsService().getPreferredSmsSubscription();
        } catch (RemoteException e) {
            return -1;
        } catch (NullPointerException e2) {
            return -1;
        }
    }

    public boolean isSMSPromptEnabled() {
        try {
            ISms iSms = TelephonyManager.getSmsService();
            return iSms.isSMSPromptEnabled();
        } catch (RemoteException e) {
            return false;
        } catch (NullPointerException e2) {
            return false;
        }
    }

    public int getSmsCapacityOnIcc() {
        try {
            ISms iccISms = getISmsService();
            if (iccISms == null) {
                return 0;
            }
            int ret = iccISms.getSmsCapacityOnIccForSubscriber(getSubscriptionId());
            return ret;
        } catch (RemoteException ex) {
            Log.m109e(TAG, "getSmsCapacityOnIcc() RemoteException", ex);
            return 0;
        }
    }

    public void sendMultimediaMessage(Context context, Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent) {
        sendMultimediaMessage(context, contentUri, locationUrl, configOverrides, sentIntent, 0L);
    }

    public void sendMultimediaMessage(Context context, final Uri contentUri, final String locationUrl, final Bundle configOverrides, final PendingIntent sentIntent, final long messageId) {
        if (contentUri == null) {
            throw new IllegalArgumentException("Uri contentUri null");
        }
        final MmsManager m = (MmsManager) context.getSystemService("mms");
        if (m != null) {
            resolveSubscriptionForOperation(new SubscriptionResolverResult() { // from class: android.telephony.SmsManager.7
                @Override // android.telephony.SmsManager.SubscriptionResolverResult
                public void onSuccess(int subId) {
                    m.sendMultimediaMessage(subId, contentUri, locationUrl, configOverrides, sentIntent, messageId);
                }

                @Override // android.telephony.SmsManager.SubscriptionResolverResult
                public void onFailure() {
                    SmsManager.notifySmsError(sentIntent, 32);
                }
            });
        }
    }

    public void downloadMultimediaMessage(Context context, String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent) {
        downloadMultimediaMessage(context, locationUrl, contentUri, configOverrides, downloadedIntent, 0L);
    }

    public void downloadMultimediaMessage(Context context, final String locationUrl, final Uri contentUri, final Bundle configOverrides, final PendingIntent downloadedIntent, final long messageId) {
        if (TextUtils.isEmpty(locationUrl)) {
            throw new IllegalArgumentException("Empty MMS location URL");
        }
        if (contentUri == null) {
            throw new IllegalArgumentException("Uri contentUri null");
        }
        final MmsManager m = (MmsManager) context.getSystemService("mms");
        if (m != null) {
            resolveSubscriptionForOperation(new SubscriptionResolverResult() { // from class: android.telephony.SmsManager.8
                @Override // android.telephony.SmsManager.SubscriptionResolverResult
                public void onSuccess(int subId) {
                    m.downloadMultimediaMessage(subId, locationUrl, contentUri, configOverrides, downloadedIntent, messageId);
                }

                @Override // android.telephony.SmsManager.SubscriptionResolverResult
                public void onFailure() {
                    SmsManager.notifySmsError(downloadedIntent, 32);
                }
            });
        }
    }

    public Bundle getCarrierConfigValues() {
        try {
            ISms iSms = getISmsService();
            if (iSms != null) {
                return iSms.getCarrierConfigValuesForSubscriber(getSubscriptionId());
            }
        } catch (RemoteException e) {
        }
        return new Bundle();
    }

    public String createAppSpecificSmsToken(PendingIntent intent) {
        try {
            ISms iccSms = getISmsServiceOrThrow();
            return iccSms.createAppSpecificSmsToken(getSubscriptionId(), null, intent);
        } catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
            return null;
        }
    }

    public void getSmsMessagesForFinancialApp(Bundle params, Executor executor, FinancialSmsCallback callback) {
    }

    public String createAppSpecificSmsTokenWithPackageInfo(String prefixes, PendingIntent intent) {
        try {
            ISms iccSms = getISmsServiceOrThrow();
            return iccSms.createAppSpecificSmsTokenWithPackageInfo(getSubscriptionId(), null, prefixes, intent);
        } catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
            return null;
        }
    }

    public void setStorageMonitorMemoryStatusOverride(boolean storageAvailable) {
        try {
            ISms iccISms = getISmsServiceOrThrow();
            if (iccISms != null) {
                iccISms.setStorageMonitorMemoryStatusOverride(getSubscriptionId(), storageAvailable);
            }
        } catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
        }
    }

    public void clearStorageMonitorMemoryStatusOverride() {
        try {
            ISms iccISms = getISmsServiceOrThrow();
            if (iccISms != null) {
                iccISms.clearStorageMonitorMemoryStatusOverride(getSubscriptionId());
            }
        } catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
        }
    }

    public int checkSmsShortCodeDestination(String destAddress, String countryIso) {
        try {
            ISms iccISms = getISmsServiceOrThrow();
            if (iccISms != null) {
                return iccISms.checkSmsShortCodeDestination(getSubscriptionId(), null, null, destAddress, countryIso);
            }
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "checkSmsShortCodeDestination() RemoteException", e);
            return 0;
        }
    }

    public String getSmscAddress() {
        try {
            ISms iSms = getISmsService();
            if (iSms == null) {
                return null;
            }
            String smsc = iSms.getSmscAddressFromIccEfForSubscriber(getSubscriptionId(), null);
            return smsc;
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean setSmscAddress(String smsc) {
        try {
            ISms iSms = getISmsService();
            if (iSms != null) {
                return iSms.setSmscAddressOnIccEfForSubscriber(smsc, getSubscriptionId(), null);
            }
            return false;
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SystemApi
    public int getPremiumSmsConsent(String packageName) {
        try {
            ISms iSms = getISmsService();
            if (iSms == null) {
                return 0;
            }
            int permission = iSms.getPremiumSmsPermission(packageName);
            return permission;
        } catch (RemoteException e) {
            Log.m109e(TAG, "getPremiumSmsPermission() RemoteException", e);
            return 0;
        }
    }

    @SystemApi
    public void setPremiumSmsConsent(String packageName, int permission) {
        try {
            ISms iSms = getISmsService();
            if (iSms != null) {
                iSms.setPremiumSmsPermission(packageName, permission);
            }
        } catch (RemoteException e) {
            Log.m109e(TAG, "setPremiumSmsPermission() RemoteException", e);
        }
    }

    @SystemApi
    @Deprecated
    public void resetAllCellBroadcastRanges() {
        try {
            ISms iSms = getISmsService();
            if (iSms != null) {
                iSms.resetAllCellBroadcastRanges(getSubscriptionId());
            }
        } catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String formatCrossStackMessageId(long id) {
        return "{x-message-id:" + id + "}";
    }

    @SystemApi
    public Uri getSmscIdentity() {
        IPhoneSubInfo info;
        Uri smscUri = Uri.EMPTY;
        try {
            info = TelephonyManager.getSubscriberInfoService();
        } catch (RemoteException ex) {
            com.android.telephony.Rlog.m8e(TAG, "getSmscIdentity(): Exception : " + ex);
            ex.rethrowAsRuntimeException();
        }
        if (info == null) {
            com.android.telephony.Rlog.m8e(TAG, "getSmscIdentity(): IPhoneSubInfo instance is NULL");
            throw new IllegalStateException("Telephony service is not available");
        }
        smscUri = info.getSmscIdentity(getSubscriptionId(), 5);
        if (Uri.EMPTY.equals(smscUri)) {
            smscUri = info.getSmscIdentity(getSubscriptionId(), 2);
        }
        return smscUri;
    }
}
