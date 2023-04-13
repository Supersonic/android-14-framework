package android.provider;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SqliteWrapper;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.telephony.Rlog;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsConferenceState;
import android.text.TextUtils;
import android.util.Patterns;
import com.android.internal.telephony.SmsApplication;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public final class Telephony {
    private static final String TAG = "Telephony";

    /* loaded from: classes3.dex */
    public interface BaseMmsColumns extends BaseColumns {
        @Deprecated
        public static final String ADAPTATION_ALLOWED = "adp_a";
        @Deprecated
        public static final String APPLIC_ID = "apl_id";
        @Deprecated
        public static final String AUX_APPLIC_ID = "aux_apl_id";
        @Deprecated
        public static final String CANCEL_ID = "cl_id";
        @Deprecated
        public static final String CANCEL_STATUS = "cl_st";
        public static final String CONTENT_CLASS = "ct_cls";
        public static final String CONTENT_LOCATION = "ct_l";
        public static final String CONTENT_TYPE = "ct_t";
        public static final String CREATOR = "creator";
        public static final String DATE = "date";
        public static final String DATE_SENT = "date_sent";
        public static final String DELIVERY_REPORT = "d_rpt";
        public static final String DELIVERY_TIME = "d_tm";
        @Deprecated
        public static final String DELIVERY_TIME_TOKEN = "d_tm_tok";
        @Deprecated
        public static final String DISTRIBUTION_INDICATOR = "d_ind";
        @Deprecated
        public static final String DRM_CONTENT = "drm_c";
        @Deprecated
        public static final String ELEMENT_DESCRIPTOR = "e_des";
        public static final String EXPIRY = "exp";
        @Deprecated
        public static final String LIMIT = "limit";
        public static final String LOCKED = "locked";
        @Deprecated
        public static final String MBOX_QUOTAS = "mb_qt";
        @Deprecated
        public static final String MBOX_QUOTAS_TOKEN = "mb_qt_tok";
        @Deprecated
        public static final String MBOX_TOTALS = "mb_t";
        @Deprecated
        public static final String MBOX_TOTALS_TOKEN = "mb_t_tok";
        public static final String MESSAGE_BOX = "msg_box";
        public static final int MESSAGE_BOX_ALL = 0;
        public static final int MESSAGE_BOX_DRAFTS = 3;
        public static final int MESSAGE_BOX_FAILED = 5;
        public static final int MESSAGE_BOX_INBOX = 1;
        public static final int MESSAGE_BOX_OUTBOX = 4;
        public static final int MESSAGE_BOX_SENT = 2;
        public static final String MESSAGE_CLASS = "m_cls";
        @Deprecated
        public static final String MESSAGE_COUNT = "m_cnt";
        public static final String MESSAGE_ID = "m_id";
        public static final String MESSAGE_SIZE = "m_size";
        public static final String MESSAGE_TYPE = "m_type";
        public static final String MMS_VERSION = "v";
        @Deprecated
        public static final String MM_FLAGS = "mm_flg";
        @Deprecated
        public static final String MM_FLAGS_TOKEN = "mm_flg_tok";
        @Deprecated
        public static final String MM_STATE = "mm_st";
        @Deprecated
        public static final String PREVIOUSLY_SENT_BY = "p_s_by";
        @Deprecated
        public static final String PREVIOUSLY_SENT_DATE = "p_s_d";
        public static final String PRIORITY = "pri";
        @Deprecated
        public static final String QUOTAS = "qt";
        public static final String READ = "read";
        public static final String READ_REPORT = "rr";
        public static final String READ_STATUS = "read_status";
        @Deprecated
        public static final String RECOMMENDED_RETRIEVAL_MODE = "r_r_mod";
        @Deprecated
        public static final String RECOMMENDED_RETRIEVAL_MODE_TEXT = "r_r_mod_txt";
        @Deprecated
        public static final String REPLACE_ID = "repl_id";
        @Deprecated
        public static final String REPLY_APPLIC_ID = "r_apl_id";
        @Deprecated
        public static final String REPLY_CHARGING = "r_chg";
        @Deprecated
        public static final String REPLY_CHARGING_DEADLINE = "r_chg_dl";
        @Deprecated
        public static final String REPLY_CHARGING_DEADLINE_TOKEN = "r_chg_dl_tok";
        @Deprecated
        public static final String REPLY_CHARGING_ID = "r_chg_id";
        @Deprecated
        public static final String REPLY_CHARGING_SIZE = "r_chg_sz";
        public static final String REPORT_ALLOWED = "rpt_a";
        public static final String RESPONSE_STATUS = "resp_st";
        public static final String RESPONSE_TEXT = "resp_txt";
        public static final String RETRIEVE_STATUS = "retr_st";
        public static final String RETRIEVE_TEXT = "retr_txt";
        public static final String RETRIEVE_TEXT_CHARSET = "retr_txt_cs";
        public static final String SEEN = "seen";
        @Deprecated
        public static final String SENDER_VISIBILITY = "s_vis";
        @Deprecated
        public static final String START = "start";
        public static final String STATUS = "st";
        @Deprecated
        public static final String STATUS_TEXT = "st_txt";
        @Deprecated
        public static final String STORE = "store";
        @Deprecated
        public static final String STORED = "stored";
        @Deprecated
        public static final String STORE_STATUS = "store_st";
        @Deprecated
        public static final String STORE_STATUS_TEXT = "store_st_txt";
        public static final String SUBJECT = "sub";
        public static final String SUBJECT_CHARSET = "sub_cs";
        public static final String SUBSCRIPTION_ID = "sub_id";
        public static final String TEXT_ONLY = "text_only";
        public static final String THREAD_ID = "thread_id";
        @Deprecated
        public static final String TOTALS = "totals";
        public static final String TRANSACTION_ID = "tr_id";
    }

    /* loaded from: classes3.dex */
    public interface CanonicalAddressesColumns extends BaseColumns {
        public static final String ADDRESS = "address";
    }

    /* loaded from: classes3.dex */
    public interface CarrierColumns extends BaseColumns {
        public static final String CARRIER_ID = "carrier_id";
        public static final Uri CONTENT_URI = Uri.parse("content://carrier_information/carrier");
        public static final String EXPIRATION_TIME = "expiration_time";
        public static final String KEY_IDENTIFIER = "key_identifier";
        public static final String KEY_TYPE = "key_type";
        public static final String LAST_MODIFIED = "last_modified";
        public static final String MCC = "mcc";
        public static final String MNC = "mnc";
        public static final String PUBLIC_KEY = "public_key";
    }

    /* loaded from: classes3.dex */
    public interface TextBasedSmsChangesColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://sms-changes");

        /* renamed from: ID */
        public static final String f340ID = "_id";
        public static final String NEW_READ_STATUS = "new_read_status";
        public static final String ORIG_ROW_ID = "orig_rowid";
        public static final String SUB_ID = "sub_id";
        public static final String TYPE = "type";
        public static final int TYPE_DELETE = 1;
        public static final int TYPE_UPDATE = 0;
    }

    /* loaded from: classes3.dex */
    public interface TextBasedSmsColumns {
        public static final String ADDRESS = "address";
        public static final String BODY = "body";
        public static final String CREATOR = "creator";
        public static final String DATE = "date";
        public static final String DATE_SENT = "date_sent";
        public static final String ERROR_CODE = "error_code";
        public static final String LOCKED = "locked";
        public static final int MESSAGE_TYPE_ALL = 0;
        public static final int MESSAGE_TYPE_DRAFT = 3;
        public static final int MESSAGE_TYPE_FAILED = 5;
        public static final int MESSAGE_TYPE_INBOX = 1;
        public static final int MESSAGE_TYPE_OUTBOX = 4;
        public static final int MESSAGE_TYPE_QUEUED = 6;
        public static final int MESSAGE_TYPE_SENT = 2;
        public static final String MTU = "mtu";
        public static final String PERSON = "person";
        public static final String PROTOCOL = "protocol";
        public static final String READ = "read";
        public static final String REPLY_PATH_PRESENT = "reply_path_present";
        public static final String SEEN = "seen";
        public static final String SERVICE_CENTER = "service_center";
        public static final String STATUS = "status";
        public static final int STATUS_COMPLETE = 0;
        public static final int STATUS_FAILED = 64;
        public static final int STATUS_NONE = -1;
        public static final int STATUS_PENDING = 32;
        public static final String SUBJECT = "subject";
        public static final String SUBSCRIPTION_ID = "sub_id";
        public static final String THREAD_ID = "thread_id";
        public static final String TYPE = "type";
    }

    /* loaded from: classes3.dex */
    public interface ThreadsColumns extends BaseColumns {
        public static final String ARCHIVED = "archived";
        public static final String DATE = "date";
        public static final String ERROR = "error";
        public static final String HAS_ATTACHMENT = "has_attachment";
        public static final String MESSAGE_COUNT = "message_count";
        public static final String READ = "read";
        public static final String RECIPIENT_IDS = "recipient_ids";
        public static final String SNIPPET = "snippet";
        public static final String SNIPPET_CHARSET = "snippet_cs";
        public static final String TYPE = "type";
    }

    private Telephony() {
    }

    /* loaded from: classes3.dex */
    public static final class Sms implements BaseColumns, TextBasedSmsColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://sms");
        public static final String DEFAULT_SORT_ORDER = "date DESC";

        private Sms() {
        }

        public static String getDefaultSmsPackage(Context context) {
            ComponentName component = SmsApplication.getDefaultSmsApplication(context, false);
            if (component != null) {
                return component.getPackageName();
            }
            return null;
        }

        public static Cursor query(ContentResolver cr, String[] projection) {
            return cr.query(CONTENT_URI, projection, null, null, "date DESC");
        }

        public static Cursor query(ContentResolver cr, String[] projection, String where, String orderBy) {
            return cr.query(CONTENT_URI, projection, where, null, orderBy == null ? "date DESC" : orderBy);
        }

        public static Uri addMessageToUri(ContentResolver resolver, Uri uri, String address, String body, String subject, Long date, boolean read, boolean deliveryReport) {
            return addMessageToUri(SubscriptionManager.getDefaultSmsSubscriptionId(), resolver, uri, address, body, subject, date, read, deliveryReport, -1L);
        }

        public static Uri addMessageToUri(int subId, ContentResolver resolver, Uri uri, String address, String body, String subject, Long date, boolean read, boolean deliveryReport) {
            return addMessageToUri(subId, resolver, uri, address, body, subject, date, read, deliveryReport, -1L);
        }

        public static Uri addMessageToUri(ContentResolver resolver, Uri uri, String address, String body, String subject, Long date, boolean read, boolean deliveryReport, long threadId) {
            return addMessageToUri(SubscriptionManager.getDefaultSmsSubscriptionId(), resolver, uri, address, body, subject, date, read, deliveryReport, threadId);
        }

        public static Uri addMessageToUri(int subId, ContentResolver resolver, Uri uri, String address, String body, String subject, Long date, boolean read, boolean deliveryReport, long threadId) {
            ContentValues values = new ContentValues(8);
            Rlog.m123v(Telephony.TAG, "Telephony addMessageToUri sub id: " + subId);
            values.put("sub_id", Integer.valueOf(subId));
            values.put("address", address);
            if (date != null) {
                values.put("date", date);
            }
            values.put("read", Integer.valueOf(read ? 1 : 0));
            values.put("subject", subject);
            values.put("body", body);
            if (deliveryReport) {
                values.put("status", (Integer) 32);
            }
            if (threadId != -1) {
                values.put("thread_id", Long.valueOf(threadId));
            }
            return resolver.insert(uri, values);
        }

        public static boolean moveMessageToFolder(Context context, Uri uri, int folder, int error) {
            if (uri == null) {
                return false;
            }
            boolean markAsUnread = false;
            boolean markAsRead = false;
            switch (folder) {
                case 1:
                case 3:
                    break;
                case 2:
                case 4:
                    markAsRead = true;
                    break;
                case 5:
                case 6:
                    markAsUnread = true;
                    break;
                default:
                    return false;
            }
            ContentValues values = new ContentValues(3);
            values.put("type", Integer.valueOf(folder));
            if (markAsUnread) {
                values.put("read", (Integer) 0);
            } else if (markAsRead) {
                values.put("read", (Integer) 1);
            }
            values.put(TextBasedSmsColumns.ERROR_CODE, Integer.valueOf(error));
            return 1 == SqliteWrapper.update(context, context.getContentResolver(), uri, values, null, null);
        }

        public static boolean isOutgoingFolder(int messageType) {
            return messageType == 5 || messageType == 4 || messageType == 2 || messageType == 6;
        }

        /* loaded from: classes3.dex */
        public static final class Inbox implements BaseColumns, TextBasedSmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://sms/inbox");
            public static final String DEFAULT_SORT_ORDER = "date DESC";

            private Inbox() {
            }

            public static Uri addMessage(ContentResolver resolver, String address, String body, String subject, Long date, boolean read) {
                return Sms.addMessageToUri(SubscriptionManager.getDefaultSmsSubscriptionId(), resolver, CONTENT_URI, address, body, subject, date, read, false);
            }

            public static Uri addMessage(int subId, ContentResolver resolver, String address, String body, String subject, Long date, boolean read) {
                return Sms.addMessageToUri(subId, resolver, CONTENT_URI, address, body, subject, date, read, false);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Sent implements BaseColumns, TextBasedSmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://sms/sent");
            public static final String DEFAULT_SORT_ORDER = "date DESC";

            private Sent() {
            }

            public static Uri addMessage(ContentResolver resolver, String address, String body, String subject, Long date) {
                return Sms.addMessageToUri(SubscriptionManager.getDefaultSmsSubscriptionId(), resolver, CONTENT_URI, address, body, subject, date, true, false);
            }

            public static Uri addMessage(int subId, ContentResolver resolver, String address, String body, String subject, Long date) {
                return Sms.addMessageToUri(subId, resolver, CONTENT_URI, address, body, subject, date, true, false);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Draft implements BaseColumns, TextBasedSmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://sms/draft");
            public static final String DEFAULT_SORT_ORDER = "date DESC";

            private Draft() {
            }

            public static Uri addMessage(ContentResolver resolver, String address, String body, String subject, Long date) {
                return Sms.addMessageToUri(SubscriptionManager.getDefaultSmsSubscriptionId(), resolver, CONTENT_URI, address, body, subject, date, true, false);
            }

            public static Uri addMessage(int subId, ContentResolver resolver, String address, String body, String subject, Long date) {
                return Sms.addMessageToUri(subId, resolver, CONTENT_URI, address, body, subject, date, true, false);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Outbox implements BaseColumns, TextBasedSmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://sms/outbox");
            public static final String DEFAULT_SORT_ORDER = "date DESC";

            private Outbox() {
            }

            public static Uri addMessage(ContentResolver resolver, String address, String body, String subject, Long date, boolean deliveryReport, long threadId) {
                return Sms.addMessageToUri(SubscriptionManager.getDefaultSmsSubscriptionId(), resolver, CONTENT_URI, address, body, subject, date, true, deliveryReport, threadId);
            }

            public static Uri addMessage(int subId, ContentResolver resolver, String address, String body, String subject, Long date, boolean deliveryReport, long threadId) {
                return Sms.addMessageToUri(subId, resolver, CONTENT_URI, address, body, subject, date, true, deliveryReport, threadId);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Conversations implements BaseColumns, TextBasedSmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://sms/conversations");
            public static final String DEFAULT_SORT_ORDER = "date DESC";
            public static final String MESSAGE_COUNT = "msg_count";
            public static final String SNIPPET = "snippet";

            private Conversations() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Intents {
            public static final String ACTION_CHANGE_DEFAULT = "android.provider.Telephony.ACTION_CHANGE_DEFAULT";
            public static final String ACTION_DEFAULT_SMS_PACKAGE_CHANGED = "android.provider.action.DEFAULT_SMS_PACKAGE_CHANGED";
            public static final String ACTION_EXTERNAL_PROVIDER_CHANGE = "android.provider.action.EXTERNAL_PROVIDER_CHANGE";
            @SystemApi
            public static final String ACTION_SMS_EMERGENCY_CB_RECEIVED = "android.provider.action.SMS_EMERGENCY_CB_RECEIVED";
            public static final String ACTION_SMS_MMS_DB_CREATED = "android.provider.action.SMS_MMS_DB_CREATED";
            public static final String ACTION_SMS_MMS_DB_LOST = "android.provider.action.SMS_MMS_DB_LOST";
            public static final String DATA_SMS_RECEIVED_ACTION = "android.intent.action.DATA_SMS_RECEIVED";
            public static final String EXTRA_IS_CORRUPTED = "android.provider.extra.IS_CORRUPTED";
            public static final String EXTRA_IS_DEFAULT_SMS_APP = "android.provider.extra.IS_DEFAULT_SMS_APP";
            public static final String EXTRA_IS_INITIAL_CREATE = "android.provider.extra.IS_INITIAL_CREATE";
            public static final String EXTRA_PACKAGE_NAME = "package";
            public static final String MMS_DOWNLOADED_ACTION = "android.provider.Telephony.MMS_DOWNLOADED";
            public static final int RESULT_SMS_DATABASE_ERROR = 10;
            public static final int RESULT_SMS_DISPATCH_FAILURE = 6;
            public static final int RESULT_SMS_DUPLICATED = 5;
            public static final int RESULT_SMS_GENERIC_ERROR = 2;
            public static final int RESULT_SMS_HANDLED = 1;
            public static final int RESULT_SMS_INVALID_URI = 11;
            public static final int RESULT_SMS_NULL_MESSAGE = 8;
            public static final int RESULT_SMS_NULL_PDU = 7;
            public static final int RESULT_SMS_OUT_OF_MEMORY = 3;
            public static final int RESULT_SMS_RECEIVED_WHILE_ENCRYPTED = 9;
            public static final int RESULT_SMS_UNSUPPORTED = 4;
            @Deprecated
            public static final String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";
            public static final String SIM_FULL_ACTION = "android.provider.Telephony.SIM_FULL";
            public static final String SMS_CARRIER_PROVISION_ACTION = "android.provider.Telephony.SMS_CARRIER_PROVISION";
            public static final String SMS_CB_RECEIVED_ACTION = "android.provider.Telephony.SMS_CB_RECEIVED";
            public static final String SMS_DELIVER_ACTION = "android.provider.Telephony.SMS_DELIVER";
            public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
            public static final String SMS_REJECTED_ACTION = "android.provider.Telephony.SMS_REJECTED";
            public static final String SMS_SERVICE_CATEGORY_PROGRAM_DATA_RECEIVED_ACTION = "android.provider.Telephony.SMS_SERVICE_CATEGORY_PROGRAM_DATA_RECEIVED";
            public static final String WAP_PUSH_DELIVER_ACTION = "android.provider.Telephony.WAP_PUSH_DELIVER";
            public static final String WAP_PUSH_RECEIVED_ACTION = "android.provider.Telephony.WAP_PUSH_RECEIVED";

            private Intents() {
            }

            public static SmsMessage[] getMessagesFromIntent(Intent intent) {
                try {
                    Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
                    if (messages == null) {
                        Rlog.m127e(Telephony.TAG, "pdus does not exist in the intent");
                        return null;
                    }
                    String format = intent.getStringExtra(CellBroadcasts.MESSAGE_FORMAT);
                    int subId = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
                    if (subId != -1) {
                        Rlog.m123v(Telephony.TAG, "getMessagesFromIntent with valid subId : " + subId);
                    } else {
                        Rlog.m123v(Telephony.TAG, "getMessagesFromIntent");
                    }
                    int pduCount = messages.length;
                    SmsMessage[] msgs = new SmsMessage[pduCount];
                    for (int i = 0; i < pduCount; i++) {
                        byte[] pdu = (byte[]) messages[i];
                        msgs[i] = SmsMessage.createFromPdu(pdu, format);
                    }
                    return msgs;
                } catch (ClassCastException e) {
                    Rlog.m127e(Telephony.TAG, "getMessagesFromIntent: " + e);
                    return null;
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Threads implements ThreadsColumns {
        public static final int BROADCAST_THREAD = 1;
        public static final int COMMON_THREAD = 0;
        public static final Uri CONTENT_URI;
        public static final Uri OBSOLETE_THREADS_URI;
        private static final String[] ID_PROJECTION = {"_id"};
        private static final Uri THREAD_ID_CONTENT_URI = Uri.parse("content://mms-sms/threadID");

        static {
            Uri withAppendedPath = Uri.withAppendedPath(MmsSms.CONTENT_URI, "conversations");
            CONTENT_URI = withAppendedPath;
            OBSOLETE_THREADS_URI = Uri.withAppendedPath(withAppendedPath, "obsolete");
        }

        private Threads() {
        }

        public static long getOrCreateThreadId(Context context, String recipient) {
            Set<String> recipients = new HashSet<>();
            recipients.add(recipient);
            return getOrCreateThreadId(context, recipients);
        }

        public static long getOrCreateThreadId(Context context, Set<String> recipients) {
            Uri.Builder uriBuilder = THREAD_ID_CONTENT_URI.buildUpon();
            for (String recipient : recipients) {
                if (Mms.isEmailAddress(recipient)) {
                    recipient = Mms.extractAddrSpec(recipient);
                }
                uriBuilder.appendQueryParameter("recipient", recipient);
            }
            Uri uri = uriBuilder.build();
            Cursor cursor = SqliteWrapper.query(context, context.getContentResolver(), uri, ID_PROJECTION, null, null, null);
            if (cursor != null) {
                try {
                    if (!cursor.moveToFirst()) {
                        Rlog.m127e(Telephony.TAG, "getOrCreateThreadId returned no rows!");
                    } else {
                        return cursor.getLong(0);
                    }
                } finally {
                    cursor.close();
                }
            }
            Rlog.m127e(Telephony.TAG, "getOrCreateThreadId failed with " + recipients.size() + " recipients");
            throw new IllegalArgumentException("Unable to find or allocate a thread ID.");
        }
    }

    /* loaded from: classes3.dex */
    public static final class Mms implements BaseMmsColumns {
        public static final Uri CONTENT_URI;
        public static final String DEFAULT_SORT_ORDER = "date DESC";
        public static final Pattern NAME_ADDR_EMAIL_PATTERN;
        public static final Uri REPORT_REQUEST_URI;
        public static final Uri REPORT_STATUS_URI;

        private Mms() {
        }

        static {
            Uri parse = Uri.parse("content://mms");
            CONTENT_URI = parse;
            REPORT_REQUEST_URI = Uri.withAppendedPath(parse, "report-request");
            REPORT_STATUS_URI = Uri.withAppendedPath(parse, "report-status");
            NAME_ADDR_EMAIL_PATTERN = Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");
        }

        public static Cursor query(ContentResolver cr, String[] projection) {
            return cr.query(CONTENT_URI, projection, null, null, "date DESC");
        }

        public static Cursor query(ContentResolver cr, String[] projection, String where, String orderBy) {
            return cr.query(CONTENT_URI, projection, where, null, orderBy == null ? "date DESC" : orderBy);
        }

        public static String extractAddrSpec(String address) {
            Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(address);
            if (match.matches()) {
                return match.group(2);
            }
            return address;
        }

        public static boolean isEmailAddress(String address) {
            if (TextUtils.isEmpty(address)) {
                return false;
            }
            String s = extractAddrSpec(address);
            Matcher match = Patterns.EMAIL_ADDRESS.matcher(s);
            return match.matches();
        }

        public static boolean isPhoneNumber(String number) {
            if (TextUtils.isEmpty(number)) {
                return false;
            }
            Matcher match = Patterns.PHONE.matcher(number);
            return match.matches();
        }

        /* loaded from: classes3.dex */
        public static final class Inbox implements BaseMmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://mms/inbox");
            public static final String DEFAULT_SORT_ORDER = "date DESC";

            private Inbox() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Sent implements BaseMmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://mms/sent");
            public static final String DEFAULT_SORT_ORDER = "date DESC";

            private Sent() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Draft implements BaseMmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://mms/drafts");
            public static final String DEFAULT_SORT_ORDER = "date DESC";

            private Draft() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Outbox implements BaseMmsColumns {
            public static final Uri CONTENT_URI = Uri.parse("content://mms/outbox");
            public static final String DEFAULT_SORT_ORDER = "date DESC";

            private Outbox() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Addr implements BaseColumns {
            public static final String ADDRESS = "address";
            public static final String CHARSET = "charset";
            public static final String CONTACT_ID = "contact_id";
            public static final String MSG_ID = "msg_id";
            public static final String TYPE = "type";

            private Addr() {
            }

            public static Uri getAddrUriForMessage(String messageId) {
                Uri addrUri = Mms.CONTENT_URI.buildUpon().appendPath(String.valueOf(messageId)).appendPath("addr").build();
                return addrUri;
            }
        }

        /* loaded from: classes3.dex */
        public static final class Part implements BaseColumns {
            public static final String CHARSET = "chset";
            public static final String CONTENT_DISPOSITION = "cd";
            public static final String CONTENT_ID = "cid";
            public static final String CONTENT_LOCATION = "cl";
            public static final String CONTENT_TYPE = "ct";
            public static final String CT_START = "ctt_s";
            public static final String CT_TYPE = "ctt_t";
            public static final String FILENAME = "fn";
            public static final String MSG_ID = "mid";
            public static final String NAME = "name";
            public static final String SEQ = "seq";
            public static final String TEXT = "text";
            public static final String _DATA = "_data";
            private static final String TABLE_PART = "part";
            public static final Uri CONTENT_URI = Uri.withAppendedPath(Mms.CONTENT_URI, TABLE_PART);

            private Part() {
            }

            public static Uri getPartUriForMessage(String messageId) {
                Uri partUri = Mms.CONTENT_URI.buildUpon().appendPath(String.valueOf(messageId)).appendPath(TABLE_PART).build();
                return partUri;
            }
        }

        /* loaded from: classes3.dex */
        public static final class Rate {
            public static final Uri CONTENT_URI = Uri.withAppendedPath(Mms.CONTENT_URI, TextToSpeech.Engine.KEY_PARAM_RATE);
            public static final String SENT_TIME = "sent_time";

            private Rate() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Intents {
            public static final String CONTENT_CHANGED_ACTION = "android.intent.action.CONTENT_CHANGED";
            public static final String DELETED_CONTENTS = "deleted_contents";

            private Intents() {
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class MmsSms implements BaseColumns {
        public static final int ERR_TYPE_GENERIC = 1;
        public static final int ERR_TYPE_GENERIC_PERMANENT = 10;
        public static final int ERR_TYPE_MMS_PROTO_PERMANENT = 12;
        public static final int ERR_TYPE_MMS_PROTO_TRANSIENT = 3;
        public static final int ERR_TYPE_SMS_PROTO_PERMANENT = 11;
        public static final int ERR_TYPE_SMS_PROTO_TRANSIENT = 2;
        public static final int ERR_TYPE_TRANSPORT_FAILURE = 4;
        public static final int MMS_PROTO = 1;
        public static final int NO_ERROR = 0;
        public static final int SMS_PROTO = 0;
        public static final String TYPE_DISCRIMINATOR_COLUMN = "transport_type";
        public static final Uri CONTENT_URI = Uri.parse("content://mms-sms/");
        public static final Uri CONTENT_CONVERSATIONS_URI = Uri.parse("content://mms-sms/conversations");
        public static final Uri CONTENT_FILTER_BYPHONE_URI = Uri.parse("content://mms-sms/messages/byphone");
        public static final Uri CONTENT_UNDELIVERED_URI = Uri.parse("content://mms-sms/undelivered");
        public static final Uri CONTENT_DRAFT_URI = Uri.parse("content://mms-sms/draft");
        public static final Uri CONTENT_LOCKED_URI = Uri.parse("content://mms-sms/locked");
        public static final Uri SEARCH_URI = Uri.parse("content://mms-sms/search");

        private MmsSms() {
        }

        /* loaded from: classes3.dex */
        public static final class PendingMessages implements BaseColumns {
            public static final Uri CONTENT_URI = Uri.withAppendedPath(MmsSms.CONTENT_URI, ImsConferenceState.STATUS_PENDING);
            public static final String DUE_TIME = "due_time";
            public static final String ERROR_CODE = "err_code";
            public static final String ERROR_TYPE = "err_type";
            public static final String LAST_TRY = "last_try";
            public static final String MSG_ID = "msg_id";
            public static final String MSG_TYPE = "msg_type";
            public static final String PROTO_TYPE = "proto_type";
            public static final String RETRY_INDEX = "retry_index";
            public static final String SUBSCRIPTION_ID = "pending_sub_id";

            private PendingMessages() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class WordsTable {

            /* renamed from: ID */
            public static final String f339ID = "_id";
            public static final String INDEXED_TEXT = "index_text";
            public static final String SOURCE_ROW_ID = "source_id";
            public static final String TABLE_ID = "table_to_use";

            private WordsTable() {
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Carriers implements BaseColumns {
        public static final String ALWAYS_ON = "always_on";
        public static final String APN = "apn";
        public static final String APN_ID = "apn_id";
        public static final long APN_READING_PERMISSION_CHANGE_ID = 124107808;
        @SystemApi
        public static final String APN_SET_ID = "apn_set_id";
        public static final String AUTH_TYPE = "authtype";
        @Deprecated
        public static final String BEARER = "bearer";
        @Deprecated
        public static final String BEARER_BITMASK = "bearer_bitmask";
        public static final int CARRIER_DELETED = 5;
        public static final int CARRIER_DELETED_BUT_PRESENT_IN_XML = 6;
        @SystemApi
        public static final int CARRIER_EDITED = 4;
        public static final String CARRIER_ENABLED = "carrier_enabled";
        public static final String CARRIER_ID = "carrier_id";
        public static final String CURRENT = "current";
        public static final String DEFAULT_SORT_ORDER = "name ASC";
        @SystemApi
        public static final String EDITED_STATUS = "edited";
        public static final String ENFORCE_KEY = "enforced";
        public static final int INVALID_APN_ID = -1;
        public static final String LINGERING_NETWORK_TYPE_BITMASK = "lingering_network_type_bitmask";
        @SystemApi
        public static final int MATCH_ALL_APN_SET_ID = -1;
        @SystemApi
        public static final String MAX_CONNECTIONS = "max_conns";
        public static final String MCC = "mcc";
        public static final String MMSC = "mmsc";
        public static final String MMSPORT = "mmsport";
        public static final String MMSPROXY = "mmsproxy";
        public static final String MNC = "mnc";
        @SystemApi
        public static final String MODEM_PERSIST = "modem_cognitive";
        @SystemApi
        @Deprecated
        public static final String MTU = "mtu";
        @SystemApi
        public static final String MTU_V4 = "mtu_v4";
        @SystemApi
        public static final String MTU_V6 = "mtu_v6";
        public static final String MVNO_MATCH_DATA = "mvno_match_data";
        public static final String MVNO_TYPE = "mvno_type";
        public static final String NAME = "name";
        public static final String NETWORK_TYPE_BITMASK = "network_type_bitmask";
        @SystemApi
        public static final int NO_APN_SET_ID = 0;
        public static final String NUMERIC = "numeric";
        public static final String OWNED_BY = "owned_by";
        public static final int OWNED_BY_DPC = 0;
        public static final int OWNED_BY_OTHERS = 1;
        public static final String PASSWORD = "password";
        public static final String PORT = "port";
        public static final String PROFILE_ID = "profile_id";
        public static final String PROTOCOL = "protocol";
        public static final String PROXY = "proxy";
        public static final String ROAMING_PROTOCOL = "roaming_protocol";
        public static final String SERVER = "server";
        public static final String SKIP_464XLAT = "skip_464xlat";
        public static final int SKIP_464XLAT_DEFAULT = -1;
        public static final int SKIP_464XLAT_DISABLE = 0;
        public static final int SKIP_464XLAT_ENABLE = 1;
        public static final String SUBSCRIPTION_ID = "sub_id";
        @SystemApi
        public static final String TIME_LIMIT_FOR_MAX_CONNECTIONS = "max_conns_time";
        public static final String TYPE = "type";
        @SystemApi
        public static final int UNEDITED = 0;
        public static final String USER = "user";
        @SystemApi
        public static final int USER_DELETED = 2;
        public static final int USER_DELETED_BUT_PRESENT_IN_XML = 3;
        @SystemApi
        public static final String USER_EDITABLE = "user_editable";
        @SystemApi
        public static final int USER_EDITED = 1;
        @SystemApi
        public static final String USER_VISIBLE = "user_visible";
        @SystemApi
        public static final String WAIT_TIME_RETRY = "wait_time";
        public static final Uri CONTENT_URI = Uri.parse("content://telephony/carriers");
        public static final Uri SIM_APN_URI = Uri.parse("content://telephony/carriers/sim_apn_list");
        public static final Uri DPC_URI = Uri.parse("content://telephony/carriers/dpc");
        public static final Uri FILTERED_URI = Uri.parse("content://telephony/carriers/filtered");
        public static final Uri ENFORCE_MANAGED_URI = Uri.parse("content://telephony/carriers/enforce_managed");
        public static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn/subId");
        public static final Uri PREFERRED_APN_SET_URI = Uri.parse("content://telephony/carriers/preferapnset/subId");

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface EditStatus {
        }

        private Carriers() {
        }
    }

    @SystemApi
    /* loaded from: classes3.dex */
    public static final class CellBroadcasts implements BaseColumns {
        @SystemApi
        public static final String AUTHORITY_LEGACY = "cellbroadcast-legacy";
        @SystemApi
        public static final String CALL_METHOD_GET_PREFERENCE = "get_preference";
        public static final String CID = "cid";
        public static final String DATA_CODING_SCHEME = "dcs";
        public static final String DEFAULT_SORT_ORDER = "date DESC";
        public static final String DELIVERY_TIME = "date";
        public static final String ETWS_IS_PRIMARY = "etws_is_primary";
        public static final String GEOMETRIES = "geometries";
        public static final String LANGUAGE_CODE = "language";
        public static final String LOCATION_CHECK_TIME = "location_check_time";
        public static final String MAXIMUM_WAIT_TIME = "maximum_wait_time";
        public static final String MESSAGE_BODY = "body";
        public static final String MESSAGE_BROADCASTED = "message_broadcasted";
        public static final String MESSAGE_DISPLAYED = "message_displayed";
        public static final String MESSAGE_PRIORITY = "priority";
        public static final String MESSAGE_READ = "read";
        public static final String PLMN = "plmn";
        public static final String RECEIVED_TIME = "received_time";
        public static final String SERIAL_NUMBER = "serial_number";
        public static final String SLOT_INDEX = "slot_index";
        public static final String SUBSCRIPTION_ID = "sub_id";
        public static final Uri CONTENT_URI = Uri.parse("content://cellbroadcasts");
        public static final Uri MESSAGE_HISTORY_URI = Uri.parse("content://cellbroadcasts/history");
        @SystemApi
        public static final Uri AUTHORITY_LEGACY_URI = Uri.parse("content://cellbroadcast-legacy");
        public static final String GEOGRAPHICAL_SCOPE = "geo_scope";
        public static final String LAC = "lac";
        public static final String SERVICE_CATEGORY = "service_category";
        public static final String MESSAGE_FORMAT = "format";
        public static final String ETWS_WARNING_TYPE = "etws_warning_type";
        public static final String CMAS_MESSAGE_CLASS = "cmas_message_class";
        public static final String CMAS_CATEGORY = "cmas_category";
        public static final String CMAS_RESPONSE_TYPE = "cmas_response_type";
        public static final String CMAS_SEVERITY = "cmas_severity";
        public static final String CMAS_URGENCY = "cmas_urgency";
        public static final String CMAS_CERTAINTY = "cmas_certainty";
        public static final String[] QUERY_COLUMNS = {"_id", GEOGRAPHICAL_SCOPE, "plmn", LAC, "cid", "serial_number", SERVICE_CATEGORY, "language", "body", "date", "read", MESSAGE_FORMAT, "priority", ETWS_WARNING_TYPE, CMAS_MESSAGE_CLASS, CMAS_CATEGORY, CMAS_RESPONSE_TYPE, CMAS_SEVERITY, CMAS_URGENCY, CMAS_CERTAINTY};

        private CellBroadcasts() {
        }

        @SystemApi
        /* loaded from: classes3.dex */
        public static final class Preference {
            public static final String ENABLE_ALERT_VIBRATION_PREF = "enable_alert_vibrate";
            public static final String ENABLE_AREA_UPDATE_INFO_PREF = "enable_area_update_info_alerts";
            public static final String ENABLE_CMAS_AMBER_PREF = "enable_cmas_amber_alerts";
            public static final String ENABLE_CMAS_EXTREME_THREAT_PREF = "enable_cmas_extreme_threat_alerts";
            public static final String ENABLE_CMAS_IN_SECOND_LANGUAGE_PREF = "receive_cmas_in_second_language";
            public static final String ENABLE_CMAS_PRESIDENTIAL_PREF = "enable_cmas_presidential_alerts";
            public static final String ENABLE_CMAS_SEVERE_THREAT_PREF = "enable_cmas_severe_threat_alerts";
            public static final String ENABLE_EMERGENCY_PERF = "enable_emergency_alerts";
            public static final String ENABLE_PUBLIC_SAFETY_PREF = "enable_public_safety_messages";
            public static final String ENABLE_STATE_LOCAL_TEST_PREF = "enable_state_local_test_alerts";
            public static final String ENABLE_TEST_ALERT_PREF = "enable_test_alerts";

            private Preference() {
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class ServiceStateTable {
        public static final String AUTHORITY = "service-state";
        public static final Uri CONTENT_URI = Uri.parse("content://service-state/");
        public static final String DATA_NETWORK_TYPE = "data_network_type";
        public static final String DATA_REG_STATE = "data_reg_state";
        public static final String DUPLEX_MODE = "duplex_mode";
        public static final String IS_MANUAL_NETWORK_SELECTION = "is_manual_network_selection";
        public static final String VOICE_OPERATOR_NUMERIC = "voice_operator_numeric";
        public static final String VOICE_REG_STATE = "voice_reg_state";

        private ServiceStateTable() {
        }

        public static Uri getUriForSubscriptionIdAndField(int subscriptionId, String field) {
            return CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(subscriptionId)).appendEncodedPath(field).build();
        }

        public static Uri getUriForSubscriptionId(int subscriptionId) {
            return CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(subscriptionId)).build();
        }
    }

    /* loaded from: classes3.dex */
    public static final class CarrierId implements BaseColumns {
        public static final String AUTHORITY = "carrier_id";
        public static final String CARRIER_ID = "carrier_id";
        public static final String CARRIER_NAME = "carrier_name";
        public static final Uri CONTENT_URI = Uri.parse("content://carrier_id");
        public static final String PARENT_CARRIER_ID = "parent_carrier_id";
        public static final String SPECIFIC_CARRIER_ID = "specific_carrier_id";
        public static final String SPECIFIC_CARRIER_ID_NAME = "specific_carrier_id_name";

        private CarrierId() {
        }

        public static Uri getUriForSubscriptionId(int subscriptionId) {
            return CONTENT_URI.buildUpon().appendEncodedPath(String.valueOf(subscriptionId)).build();
        }

        public static Uri getSpecificCarrierIdUriForSubscriptionId(int subscriptionId) {
            return Uri.withAppendedPath(Uri.withAppendedPath(CONTENT_URI, "specific"), String.valueOf(subscriptionId));
        }

        /* loaded from: classes3.dex */
        public static final class All implements BaseColumns {
            public static final String APN = "apn";
            public static final Uri CONTENT_URI = Uri.parse("content://carrier_id/all");
            public static final String GID1 = "gid1";
            public static final String GID2 = "gid2";
            public static final String ICCID_PREFIX = "iccid_prefix";
            public static final String IMSI_PREFIX_XPATTERN = "imsi_prefix_xpattern";
            public static final String MCCMNC = "mccmnc";
            public static final String PLMN = "plmn";
            public static final String PRIVILEGE_ACCESS_RULE = "privilege_access_rule";
            public static final String SPN = "spn";

            private All() {
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class SimInfo {
        public static final int COLOR_DEFAULT = 0;
        public static final String COLUMN_ACCESS_RULES = "access_rules";
        public static final String COLUMN_ACCESS_RULES_FROM_CARRIER_CONFIGS = "access_rules_from_carrier_configs";
        public static final String COLUMN_ALLOWED_NETWORK_TYPES = "allowed_network_types";
        public static final String COLUMN_ALLOWED_NETWORK_TYPES_FOR_REASONS = "allowed_network_types_for_reasons";
        public static final String COLUMN_CARD_ID = "card_id";
        public static final String COLUMN_CARRIER_ID = "carrier_id";
        public static final String COLUMN_CARRIER_NAME = "carrier_name";
        public static final String COLUMN_CB_ALERT_REMINDER_INTERVAL = "alert_reminder_interval";
        public static final String COLUMN_CB_ALERT_SOUND_DURATION = "alert_sound_duration";
        public static final String COLUMN_CB_ALERT_SPEECH = "enable_alert_speech";
        public static final String COLUMN_CB_ALERT_VIBRATE = "enable_alert_vibrate";
        public static final String COLUMN_CB_AMBER_ALERT = "enable_cmas_amber_alerts";
        public static final String COLUMN_CB_CHANNEL_50_ALERT = "enable_channel_50_alerts";
        public static final String COLUMN_CB_CMAS_TEST_ALERT = "enable_cmas_test_alerts";
        public static final String COLUMN_CB_EMERGENCY_ALERT = "enable_emergency_alerts";
        public static final String COLUMN_CB_ETWS_TEST_ALERT = "enable_etws_test_alerts";
        public static final String COLUMN_CB_EXTREME_THREAT_ALERT = "enable_cmas_extreme_threat_alerts";
        public static final String COLUMN_CB_OPT_OUT_DIALOG = "show_cmas_opt_out_dialog";
        public static final String COLUMN_CB_SEVERE_THREAT_ALERT = "enable_cmas_severe_threat_alerts";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_CROSS_SIM_CALLING_ENABLED = "cross_sim_calling_enabled";
        public static final String COLUMN_D2D_STATUS_SHARING = "d2d_sharing_status";
        public static final String COLUMN_D2D_STATUS_SHARING_SELECTED_CONTACTS = "d2d_sharing_contacts";
        public static final String COLUMN_DATA_ROAMING = "data_roaming";
        public static final String COLUMN_DISPLAY_NAME = "display_name";
        public static final String COLUMN_EHPLMNS = "ehplmns";
        public static final String COLUMN_ENABLED_MOBILE_DATA_POLICIES = "enabled_mobile_data_policies";
        public static final String COLUMN_ENHANCED_4G_MODE_ENABLED = "volte_vt_enabled";
        public static final String COLUMN_GROUP_OWNER = "group_owner";
        public static final String COLUMN_GROUP_UUID = "group_uuid";
        public static final String COLUMN_HPLMNS = "hplmns";
        public static final String COLUMN_ICC_ID = "icc_id";
        public static final String COLUMN_IMSI = "imsi";
        public static final String COLUMN_IMS_RCS_UCE_ENABLED = "ims_rcs_uce_enabled";
        public static final String COLUMN_ISO_COUNTRY_CODE = "iso_country_code";
        public static final String COLUMN_IS_EMBEDDED = "is_embedded";
        public static final String COLUMN_IS_OPPORTUNISTIC = "is_opportunistic";
        public static final String COLUMN_IS_REMOVABLE = "is_removable";
        public static final String COLUMN_MCC = "mcc";
        public static final String COLUMN_MCC_STRING = "mcc_string";
        public static final String COLUMN_MNC = "mnc";
        public static final String COLUMN_MNC_STRING = "mnc_string";
        public static final String COLUMN_NAME_SOURCE = "name_source";
        public static final String COLUMN_NR_ADVANCED_CALLING_ENABLED = "nr_advanced_calling_enabled";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_PORT_INDEX = "port_index";
        public static final String COLUMN_PROFILE_CLASS = "profile_class";
        public static final String COLUMN_SATELLITE_ENABLED = "satellite_enabled";
        public static final String COLUMN_SIM_SLOT_INDEX = "sim_id";
        public static final String COLUMN_SUBSCRIPTION_TYPE = "subscription_type";
        public static final String COLUMN_TP_MESSAGE_REF = "tp_message_ref";
        public static final String COLUMN_UICC_APPLICATIONS_ENABLED = "uicc_applications_enabled";
        public static final String COLUMN_UNIQUE_KEY_SUBSCRIPTION_ID = "_id";
        public static final String COLUMN_USAGE_SETTING = "usage_setting";
        public static final String COLUMN_USER_HANDLE = "user_handle";
        public static final String COLUMN_VOIMS_OPT_IN_STATUS = "voims_opt_in_status";
        public static final String COLUMN_VT_IMS_ENABLED = "vt_ims_enabled";
        public static final String COLUMN_WFC_IMS_ENABLED = "wfc_ims_enabled";
        public static final String COLUMN_WFC_IMS_MODE = "wfc_ims_mode";
        public static final String COLUMN_WFC_IMS_ROAMING_ENABLED = "wfc_ims_roaming_enabled";
        public static final String COLUMN_WFC_IMS_ROAMING_MODE = "wfc_ims_roaming_mode";
        public static final int DATA_ROAMING_DISABLE = 0;
        public static final int DATA_ROAMING_ENABLE = 1;
        public static final int DISPLAY_NUMBER_DEFAULT = 1;
        public static final int NAME_SOURCE_CARRIER = 3;
        public static final int NAME_SOURCE_CARRIER_ID = 0;
        public static final int NAME_SOURCE_SIM_PNN = 4;
        public static final int NAME_SOURCE_SIM_SPN = 1;
        public static final int NAME_SOURCE_UNKNOWN = -1;
        public static final int NAME_SOURCE_USER_INPUT = 2;
        public static final int PROFILE_CLASS_OPERATIONAL = 2;
        public static final int PROFILE_CLASS_PROVISIONING = 1;
        public static final int PROFILE_CLASS_TESTING = 0;
        public static final int PROFILE_CLASS_UNSET = -1;
        public static final int SIM_NOT_INSERTED = -1;
        public static final int SIM_PROVISIONED = 0;
        public static final int SUBSCRIPTION_TYPE_LOCAL_SIM = 0;
        public static final int SUBSCRIPTION_TYPE_REMOTE_SIM = 1;
        public static final Uri CONTENT_URI = Uri.parse("content://telephony/siminfo");
        public static final String COLUMN_DISPLAY_NUMBER_FORMAT = "display_number_format";
        public static final String COLUMN_SIM_PROVISIONING_STATUS = "sim_provisioning_status";
        public static final String COLUMN_IS_METERED = "is_metered";
        @Deprecated
        public static final String COLUMN_DATA_ENABLED_OVERRIDE_RULES = "data_enabled_override_rules";
        public static final String COLUMN_RCS_CONFIG = "rcs_config";
        public static final String COLUMN_PHONE_NUMBER_SOURCE_CARRIER = "phone_number_source_carrier";
        public static final String COLUMN_PHONE_NUMBER_SOURCE_IMS = "phone_number_source_ims";
        private static final List<String> ALL_COLUMNS = List.of((Object[]) new String[]{"_id", "icc_id", "sim_id", "display_name", "carrier_name", "name_source", "color", "number", COLUMN_DISPLAY_NUMBER_FORMAT, "data_roaming", "mcc", "mnc", "mcc_string", "mnc_string", "ehplmns", "hplmns", COLUMN_SIM_PROVISIONING_STATUS, "is_embedded", "card_id", "access_rules", "access_rules_from_carrier_configs", "is_removable", "enable_cmas_extreme_threat_alerts", "enable_cmas_severe_threat_alerts", "enable_cmas_amber_alerts", "enable_emergency_alerts", "alert_sound_duration", "alert_reminder_interval", "enable_alert_vibrate", "enable_alert_speech", "enable_etws_test_alerts", "enable_channel_50_alerts", "enable_cmas_test_alerts", "show_cmas_opt_out_dialog", "volte_vt_enabled", "vt_ims_enabled", "wfc_ims_enabled", "wfc_ims_mode", "wfc_ims_roaming_mode", "wfc_ims_roaming_enabled", "is_opportunistic", "group_uuid", COLUMN_IS_METERED, "iso_country_code", "carrier_id", "profile_class", "subscription_type", "group_owner", COLUMN_DATA_ENABLED_OVERRIDE_RULES, "enabled_mobile_data_policies", "imsi", "uicc_applications_enabled", "allowed_network_types", "ims_rcs_uce_enabled", "cross_sim_calling_enabled", COLUMN_RCS_CONFIG, "allowed_network_types_for_reasons", "d2d_sharing_status", "voims_opt_in_status", "d2d_sharing_contacts", "nr_advanced_calling_enabled", COLUMN_PHONE_NUMBER_SOURCE_CARRIER, COLUMN_PHONE_NUMBER_SOURCE_IMS, "port_index", "usage_setting", "tp_message_ref", "user_handle", "satellite_enabled"});

        private SimInfo() {
        }

        public static List<String> getAllColumns() {
            return ALL_COLUMNS;
        }
    }

    /* loaded from: classes3.dex */
    public static final class SatelliteDatagrams {
        public static final String PROVIDER_NAME = "satellite";
        public static final String TABLE_NAME = "incoming_datagrams";
        private static final String URL = "content://satellite/incoming_datagrams";
        public static final Uri CONTENT_URI = Uri.parse(URL);
        public static final String COLUMN_UNIQUE_KEY_DATAGRAM_ID = "datagram_id";
        public static final String COLUMN_DATAGRAM = "datagram";
        private static final List<String> ALL_COLUMNS = List.of(COLUMN_UNIQUE_KEY_DATAGRAM_ID, COLUMN_DATAGRAM);

        private SatelliteDatagrams() {
        }

        public static List<String> getAllColumns() {
            return ALL_COLUMNS;
        }
    }
}
