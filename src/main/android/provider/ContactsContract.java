package android.provider;

import android.accounts.Account;
import android.annotation.SystemApi;
import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorEntityIterator;
import android.content.Entity;
import android.content.EntityIterator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Rect;
import android.media.MediaFormat;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.provider.Contacts;
import android.provider.SyncStateContract;
import android.telephony.data.ApnSetting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import com.android.internal.C4057R;
import com.google.android.collect.Sets;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes3.dex */
public final class ContactsContract {
    public static final String AUTHORITY = "com.android.contacts";
    public static final Uri AUTHORITY_URI = Uri.parse("content://com.android.contacts");
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    public static final String DEFERRED_SNIPPETING = "deferred_snippeting";
    public static final String DEFERRED_SNIPPETING_QUERY = "deferred_snippeting_query";
    public static final String DIRECTORY_PARAM_KEY = "directory";
    public static final String HIDDEN_COLUMN_PREFIX = "x_";
    public static final String LIMIT_PARAM_KEY = "limit";
    public static final String PRIMARY_ACCOUNT_NAME = "name_for_primary_account";
    public static final String PRIMARY_ACCOUNT_TYPE = "type_for_primary_account";
    public static final String REMOVE_DUPLICATE_ENTRIES = "remove_duplicate_entries";
    public static final String STREQUENT_PHONE_ONLY = "strequent_phone_only";

    /* loaded from: classes3.dex */
    public static final class Authorization {
        public static final String AUTHORIZATION_METHOD = "authorize";
        public static final String KEY_AUTHORIZED_URI = "authorized_uri";
        public static final String KEY_URI_TO_AUTHORIZE = "uri_to_authorize";
    }

    /* loaded from: classes3.dex */
    protected interface BaseSyncColumns {
        public static final String SYNC1 = "sync1";
        public static final String SYNC2 = "sync2";
        public static final String SYNC3 = "sync3";
        public static final String SYNC4 = "sync4";
    }

    /* loaded from: classes3.dex */
    interface ContactCounts {
        public static final String EXTRA_ADDRESS_BOOK_INDEX = "android.provider.extra.ADDRESS_BOOK_INDEX";
        public static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "android.provider.extra.ADDRESS_BOOK_INDEX_COUNTS";
        public static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "android.provider.extra.ADDRESS_BOOK_INDEX_TITLES";
    }

    /* loaded from: classes3.dex */
    protected interface ContactNameColumns {
        public static final String DISPLAY_NAME_ALTERNATIVE = "display_name_alt";
        public static final String DISPLAY_NAME_PRIMARY = "display_name";
        public static final String DISPLAY_NAME_SOURCE = "display_name_source";
        public static final String PHONETIC_NAME = "phonetic_name";
        public static final String PHONETIC_NAME_STYLE = "phonetic_name_style";
        public static final String SORT_KEY_ALTERNATIVE = "sort_key_alt";
        public static final String SORT_KEY_PRIMARY = "sort_key";
    }

    /* loaded from: classes3.dex */
    protected interface ContactOptionsColumns {
        public static final String CUSTOM_RINGTONE = "custom_ringtone";
        @Deprecated
        public static final String LAST_TIME_CONTACTED = "last_time_contacted";
        public static final String LR_LAST_TIME_CONTACTED = "last_time_contacted";
        public static final String LR_TIMES_CONTACTED = "times_contacted";
        public static final String PINNED = "pinned";
        public static final String RAW_LAST_TIME_CONTACTED = "x_last_time_contacted";
        public static final String RAW_TIMES_CONTACTED = "x_times_contacted";
        public static final String SEND_TO_VOICEMAIL = "send_to_voicemail";
        public static final String STARRED = "starred";
        @Deprecated
        public static final String TIMES_CONTACTED = "times_contacted";
    }

    /* loaded from: classes3.dex */
    protected interface ContactStatusColumns {
        public static final String CONTACT_CHAT_CAPABILITY = "contact_chat_capability";
        public static final String CONTACT_PRESENCE = "contact_presence";
        public static final String CONTACT_STATUS = "contact_status";
        public static final String CONTACT_STATUS_ICON = "contact_status_icon";
        public static final String CONTACT_STATUS_LABEL = "contact_status_label";
        public static final String CONTACT_STATUS_RES_PACKAGE = "contact_status_res_package";
        public static final String CONTACT_STATUS_TIMESTAMP = "contact_status_ts";
    }

    /* loaded from: classes3.dex */
    protected interface ContactsColumns {
        public static final String CONTACT_LAST_UPDATED_TIMESTAMP = "contact_last_updated_timestamp";
        public static final String DISPLAY_NAME = "display_name";
        public static final String HAS_PHONE_NUMBER = "has_phone_number";
        public static final String IN_DEFAULT_DIRECTORY = "in_default_directory";
        public static final String IN_VISIBLE_GROUP = "in_visible_group";
        public static final String IS_USER_PROFILE = "is_user_profile";
        public static final String LOOKUP_KEY = "lookup";
        public static final String NAME_RAW_CONTACT_ID = "name_raw_contact_id";
        public static final String PHOTO_FILE_ID = "photo_file_id";
        public static final String PHOTO_ID = "photo_id";
        public static final String PHOTO_THUMBNAIL_URI = "photo_thumb_uri";
        public static final String PHOTO_URI = "photo_uri";
    }

    /* loaded from: classes3.dex */
    protected interface DataColumns {
        @Deprecated
        public static final String CARRIER_PRESENCE = "carrier_presence";
        @Deprecated
        public static final int CARRIER_PRESENCE_VT_CAPABLE = 1;
        public static final String DATA1 = "data1";
        public static final String DATA10 = "data10";
        public static final String DATA11 = "data11";
        public static final String DATA12 = "data12";
        public static final String DATA13 = "data13";
        public static final String DATA14 = "data14";
        public static final String DATA15 = "data15";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        public static final String DATA5 = "data5";
        public static final String DATA6 = "data6";
        public static final String DATA7 = "data7";
        public static final String DATA8 = "data8";
        public static final String DATA9 = "data9";
        public static final String DATA_VERSION = "data_version";
        @Deprecated
        public static final String HASH_ID = "hash_id";
        public static final String IS_PHONE_ACCOUNT_MIGRATION_PENDING = "is_preferred_phone_account_migration_pending";
        public static final String IS_PRIMARY = "is_primary";
        public static final String IS_READ_ONLY = "is_read_only";
        public static final String IS_SUPER_PRIMARY = "is_super_primary";
        public static final String MIMETYPE = "mimetype";
        public static final String PREFERRED_PHONE_ACCOUNT_COMPONENT_NAME = "preferred_phone_account_component_name";
        public static final String PREFERRED_PHONE_ACCOUNT_ID = "preferred_phone_account_id";
        public static final String RAW_CONTACT_ID = "raw_contact_id";
        public static final String RES_PACKAGE = "res_package";
        public static final String SYNC1 = "data_sync1";
        public static final String SYNC2 = "data_sync2";
        public static final String SYNC3 = "data_sync3";
        public static final String SYNC4 = "data_sync4";
    }

    /* loaded from: classes3.dex */
    protected interface DataColumnsWithJoins extends BaseColumns, DataColumns, StatusColumns, RawContactsColumns, ContactsColumns, ContactNameColumns, ContactOptionsColumns, ContactStatusColumns, DataUsageStatColumns {
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static final class DataUsageFeedback {
        public static final String USAGE_TYPE = "type";
        public static final String USAGE_TYPE_CALL = "call";
        public static final String USAGE_TYPE_LONG_TEXT = "long_text";
        public static final String USAGE_TYPE_SHORT_TEXT = "short_text";
        public static final Uri FEEDBACK_URI = Uri.withAppendedPath(Data.CONTENT_URI, "usagefeedback");
        public static final Uri DELETE_USAGE_URI = Uri.withAppendedPath(Contacts.CONTENT_URI, "delete_usage");
    }

    /* loaded from: classes3.dex */
    protected interface DataUsageStatColumns {
        @Deprecated
        public static final String LAST_TIME_USED = "last_time_used";
        public static final String LR_LAST_TIME_USED = "last_time_used";
        public static final String LR_TIMES_USED = "times_used";
        public static final String RAW_LAST_TIME_USED = "x_last_time_used";
        public static final String RAW_TIMES_USED = "x_times_used";
        @Deprecated
        public static final String TIMES_USED = "times_used";
    }

    /* loaded from: classes3.dex */
    protected interface DeletedContactsColumns {
        public static final String CONTACT_DELETED_TIMESTAMP = "contact_deleted_timestamp";
        public static final String CONTACT_ID = "contact_id";
    }

    /* loaded from: classes3.dex */
    public interface DisplayNameSources {
        public static final int EMAIL = 10;
        public static final int NICKNAME = 35;
        public static final int ORGANIZATION = 30;
        public static final int PHONE = 20;
        public static final int STRUCTURED_NAME = 40;
        public static final int STRUCTURED_PHONETIC_NAME = 37;
        public static final int UNDEFINED = 0;
    }

    /* loaded from: classes3.dex */
    public interface FullNameStyle {
        public static final int CHINESE = 3;
        public static final int CJK = 2;
        public static final int JAPANESE = 4;
        public static final int KOREAN = 5;
        public static final int UNDEFINED = 0;
        public static final int WESTERN = 1;
    }

    /* loaded from: classes3.dex */
    protected interface GroupsColumns {
        public static final String ACCOUNT_TYPE_AND_DATA_SET = "account_type_and_data_set";
        public static final String AUTO_ADD = "auto_add";
        public static final String DATA_SET = "data_set";
        public static final String DELETED = "deleted";
        public static final String FAVORITES = "favorites";
        public static final String GROUP_IS_READ_ONLY = "group_is_read_only";
        public static final String GROUP_VISIBLE = "group_visible";
        public static final String NOTES = "notes";
        public static final String PARAM_RETURN_GROUP_COUNT_PER_ACCOUNT = "return_group_count_per_account";
        public static final String RES_PACKAGE = "res_package";
        public static final String SHOULD_SYNC = "should_sync";
        public static final String SUMMARY_COUNT = "summ_count";
        public static final String SUMMARY_GROUP_COUNT_PER_ACCOUNT = "group_count_per_account";
        public static final String SUMMARY_WITH_PHONES = "summ_phones";
        public static final String SYSTEM_ID = "system_id";
        public static final String TITLE = "title";
        public static final String TITLE_RES = "title_res";
    }

    /* loaded from: classes3.dex */
    public static final class Intents {
        public static final String ACTION_GET_MULTIPLE_PHONES = "com.android.contacts.action.GET_MULTIPLE_PHONES";
        public static final String ACTION_PROFILE_CHANGED = "android.provider.Contacts.PROFILE_CHANGED";
        public static final String ACTION_VOICE_SEND_MESSAGE_TO_CONTACTS = "android.provider.action.VOICE_SEND_MESSAGE_TO_CONTACTS";
        public static final String ATTACH_IMAGE = "com.android.contacts.action.ATTACH_IMAGE";
        public static final String CONTACTS_DATABASE_CREATED = "android.provider.Contacts.DATABASE_CREATED";
        public static final String EXTRA_CREATE_DESCRIPTION = "com.android.contacts.action.CREATE_DESCRIPTION";
        @Deprecated
        public static final String EXTRA_EXCLUDE_MIMES = "exclude_mimes";
        public static final String EXTRA_FORCE_CREATE = "com.android.contacts.action.FORCE_CREATE";
        @Deprecated
        public static final String EXTRA_MODE = "mode";
        public static final String EXTRA_PHONE_URIS = "com.android.contacts.extra.PHONE_URIS";
        public static final String EXTRA_RECIPIENT_CONTACT_CHAT_ID = "android.provider.extra.RECIPIENT_CONTACT_CHAT_ID";
        public static final String EXTRA_RECIPIENT_CONTACT_NAME = "android.provider.extra.RECIPIENT_CONTACT_NAME";
        public static final String EXTRA_RECIPIENT_CONTACT_URI = "android.provider.extra.RECIPIENT_CONTACT_URI";
        @Deprecated
        public static final String EXTRA_TARGET_RECT = "target_rect";
        public static final String INVITE_CONTACT = "com.android.contacts.action.INVITE_CONTACT";
        public static final String METADATA_ACCOUNT_TYPE = "android.provider.account_type";
        public static final String METADATA_MIMETYPE = "android.provider.mimetype";
        @Deprecated
        public static final int MODE_LARGE = 3;
        @Deprecated
        public static final int MODE_MEDIUM = 2;
        @Deprecated
        public static final int MODE_SMALL = 1;
        public static final String SEARCH_SUGGESTION_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_CLICKED";
        public static final String SEARCH_SUGGESTION_CREATE_CONTACT_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_CREATE_CONTACT_CLICKED";
        public static final String SEARCH_SUGGESTION_DIAL_NUMBER_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_DIAL_NUMBER_CLICKED";
        public static final String SHOW_OR_CREATE_CONTACT = "com.android.contacts.action.SHOW_OR_CREATE_CONTACT";

        /* loaded from: classes3.dex */
        public static final class Insert {
            public static final String ACTION = "android.intent.action.INSERT";
            public static final String COMPANY = "company";
            public static final String DATA = "data";
            public static final String EMAIL = "email";
            public static final String EMAIL_ISPRIMARY = "email_isprimary";
            public static final String EMAIL_TYPE = "email_type";
            public static final String EXTRA_ACCOUNT = "android.provider.extra.ACCOUNT";
            public static final String EXTRA_DATA_SET = "android.provider.extra.DATA_SET";
            public static final String FULL_MODE = "full_mode";
            public static final String IM_HANDLE = "im_handle";
            public static final String IM_ISPRIMARY = "im_isprimary";
            public static final String IM_PROTOCOL = "im_protocol";
            public static final String JOB_TITLE = "job_title";
            public static final String NAME = "name";
            public static final String NOTES = "notes";
            public static final String PHONE = "phone";
            public static final String PHONETIC_NAME = "phonetic_name";
            public static final String PHONE_ISPRIMARY = "phone_isprimary";
            public static final String PHONE_TYPE = "phone_type";
            public static final String POSTAL = "postal";
            public static final String POSTAL_ISPRIMARY = "postal_isprimary";
            public static final String POSTAL_TYPE = "postal_type";
            public static final String SECONDARY_EMAIL = "secondary_email";
            public static final String SECONDARY_EMAIL_TYPE = "secondary_email_type";
            public static final String SECONDARY_PHONE = "secondary_phone";
            public static final String SECONDARY_PHONE_TYPE = "secondary_phone_type";
            public static final String TERTIARY_EMAIL = "tertiary_email";
            public static final String TERTIARY_EMAIL_TYPE = "tertiary_email_type";
            public static final String TERTIARY_PHONE = "tertiary_phone";
            public static final String TERTIARY_PHONE_TYPE = "tertiary_phone_type";
        }
    }

    @SystemApi
    @Deprecated
    /* loaded from: classes3.dex */
    protected interface MetadataSyncColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String DATA = "data";
        public static final String DATA_SET = "data_set";
        public static final String DELETED = "deleted";
        public static final String RAW_CONTACT_BACKUP_ID = "raw_contact_backup_id";
    }

    @SystemApi
    @Deprecated
    /* loaded from: classes3.dex */
    protected interface MetadataSyncStateColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String DATA_SET = "data_set";
        public static final String STATE = "state";
    }

    /* loaded from: classes3.dex */
    protected interface PhoneLookupColumns {
        public static final String CONTACT_ID = "contact_id";
        public static final String DATA_ID = "data_id";
        public static final String LABEL = "label";
        public static final String NORMALIZED_NUMBER = "normalized_number";
        public static final String NUMBER = "number";
        public static final String TYPE = "type";
    }

    /* loaded from: classes3.dex */
    public interface PhoneticNameStyle {
        public static final int JAPANESE = 4;
        public static final int KOREAN = 5;
        public static final int PINYIN = 3;
        public static final int UNDEFINED = 0;
    }

    /* loaded from: classes3.dex */
    protected interface PhotoFilesColumns {
        public static final String FILESIZE = "filesize";
        public static final String HEIGHT = "height";
        public static final String WIDTH = "width";
    }

    /* loaded from: classes3.dex */
    protected interface PresenceColumns {
        public static final String CUSTOM_PROTOCOL = "custom_protocol";
        public static final String DATA_ID = "presence_data_id";
        public static final String IM_ACCOUNT = "im_account";
        public static final String IM_HANDLE = "im_handle";
        public static final String PROTOCOL = "protocol";
    }

    /* loaded from: classes3.dex */
    protected interface RawContactsColumns {
        public static final String ACCOUNT_TYPE_AND_DATA_SET = "account_type_and_data_set";
        public static final String AGGREGATION_MODE = "aggregation_mode";
        public static final String BACKUP_ID = "backup_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String DATA_SET = "data_set";
        public static final String DELETED = "deleted";
        @Deprecated
        public static final String METADATA_DIRTY = "metadata_dirty";
        public static final String RAW_CONTACT_IS_READ_ONLY = "raw_contact_is_read_only";
        public static final String RAW_CONTACT_IS_USER_PROFILE = "raw_contact_is_user_profile";
    }

    /* loaded from: classes3.dex */
    public static class SearchSnippets {
        public static final String DEFERRED_SNIPPETING_KEY = "deferred_snippeting";
        public static final String SNIPPET = "snippet";
        public static final String SNIPPET_ARGS_PARAM_KEY = "snippet_args";
    }

    /* loaded from: classes3.dex */
    protected interface SettingsColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String ANY_UNSYNCED = "any_unsynced";
        public static final String DATA_SET = "data_set";
        public static final String IS_DEFAULT = "x_is_default";
        public static final String SHOULD_SYNC = "should_sync";
        public static final String UNGROUPED_COUNT = "summ_count";
        public static final String UNGROUPED_VISIBLE = "ungrouped_visible";
        public static final String UNGROUPED_WITH_PHONES = "summ_phones";
    }

    /* loaded from: classes3.dex */
    protected interface StatusColumns {
        public static final int AVAILABLE = 5;
        public static final int AWAY = 2;
        public static final int CAPABILITY_HAS_CAMERA = 4;
        public static final int CAPABILITY_HAS_VIDEO = 2;
        public static final int CAPABILITY_HAS_VOICE = 1;
        public static final String CHAT_CAPABILITY = "chat_capability";
        public static final int DO_NOT_DISTURB = 4;
        public static final int IDLE = 3;
        public static final int INVISIBLE = 1;
        public static final int OFFLINE = 0;
        public static final String PRESENCE = "mode";
        @Deprecated
        public static final String PRESENCE_CUSTOM_STATUS = "status";
        @Deprecated
        public static final String PRESENCE_STATUS = "mode";
        public static final String STATUS = "status";
        public static final String STATUS_ICON = "status_icon";
        public static final String STATUS_LABEL = "status_label";
        public static final String STATUS_RES_PACKAGE = "status_res_package";
        public static final String STATUS_TIMESTAMP = "status_ts";
    }

    @Deprecated
    /* loaded from: classes3.dex */
    protected interface StreamItemPhotosColumns {
        @Deprecated
        public static final String PHOTO_FILE_ID = "photo_file_id";
        @Deprecated
        public static final String PHOTO_URI = "photo_uri";
        @Deprecated
        public static final String SORT_INDEX = "sort_index";
        @Deprecated
        public static final String STREAM_ITEM_ID = "stream_item_id";
        @Deprecated
        public static final String SYNC1 = "stream_item_photo_sync1";
        @Deprecated
        public static final String SYNC2 = "stream_item_photo_sync2";
        @Deprecated
        public static final String SYNC3 = "stream_item_photo_sync3";
        @Deprecated
        public static final String SYNC4 = "stream_item_photo_sync4";
    }

    @Deprecated
    /* loaded from: classes3.dex */
    protected interface StreamItemsColumns {
        @Deprecated
        public static final String ACCOUNT_NAME = "account_name";
        @Deprecated
        public static final String ACCOUNT_TYPE = "account_type";
        @Deprecated
        public static final String COMMENTS = "comments";
        @Deprecated
        public static final String CONTACT_ID = "contact_id";
        @Deprecated
        public static final String CONTACT_LOOKUP_KEY = "contact_lookup";
        @Deprecated
        public static final String DATA_SET = "data_set";
        @Deprecated
        public static final String RAW_CONTACT_ID = "raw_contact_id";
        @Deprecated
        public static final String RAW_CONTACT_SOURCE_ID = "raw_contact_source_id";
        @Deprecated
        public static final String RES_ICON = "icon";
        @Deprecated
        public static final String RES_LABEL = "label";
        @Deprecated
        public static final String RES_PACKAGE = "res_package";
        @Deprecated
        public static final String SYNC1 = "stream_item_sync1";
        @Deprecated
        public static final String SYNC2 = "stream_item_sync2";
        @Deprecated
        public static final String SYNC3 = "stream_item_sync3";
        @Deprecated
        public static final String SYNC4 = "stream_item_sync4";
        @Deprecated
        public static final String TEXT = "text";
        @Deprecated
        public static final String TIMESTAMP = "timestamp";
    }

    /* loaded from: classes3.dex */
    protected interface SyncColumns extends BaseSyncColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String DIRTY = "dirty";
        public static final String SOURCE_ID = "sourceid";
        public static final String VERSION = "version";
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public interface SyncStateColumns extends SyncStateContract.Columns {
    }

    /* loaded from: classes3.dex */
    public static final class Directory implements BaseColumns {
        public static final String ACCOUNT_NAME = "accountName";
        public static final String ACCOUNT_TYPE = "accountType";
        public static final String CALLER_PACKAGE_PARAM_KEY = "callerPackage";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact_directory";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact_directories";
        public static final long DEFAULT = 0;
        public static final String DIRECTORY_AUTHORITY = "authority";
        public static final String DISPLAY_NAME = "displayName";
        public static final long ENTERPRISE_DEFAULT = 1000000000;
        public static final long ENTERPRISE_DIRECTORY_ID_BASE = 1000000000;
        public static final long ENTERPRISE_LOCAL_INVISIBLE = 1000000001;
        public static final String EXPORT_SUPPORT = "exportSupport";
        public static final int EXPORT_SUPPORT_ANY_ACCOUNT = 2;
        public static final int EXPORT_SUPPORT_NONE = 0;
        public static final int EXPORT_SUPPORT_SAME_ACCOUNT_ONLY = 1;
        public static final long LOCAL_INVISIBLE = 1;
        public static final String PACKAGE_NAME = "packageName";
        public static final String PHOTO_SUPPORT = "photoSupport";
        public static final int PHOTO_SUPPORT_FULL = 3;
        public static final int PHOTO_SUPPORT_FULL_SIZE_ONLY = 2;
        public static final int PHOTO_SUPPORT_NONE = 0;
        public static final int PHOTO_SUPPORT_THUMBNAIL_ONLY = 1;
        public static final String SHORTCUT_SUPPORT = "shortcutSupport";
        public static final int SHORTCUT_SUPPORT_DATA_ITEMS_ONLY = 1;
        public static final int SHORTCUT_SUPPORT_FULL = 2;
        public static final int SHORTCUT_SUPPORT_NONE = 0;
        public static final String TYPE_RESOURCE_ID = "typeResourceId";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "directories");
        public static final Uri ENTERPRISE_CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "directories_enterprise");
        public static final Uri ENTERPRISE_FILE_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "directory_file_enterprise");

        private Directory() {
        }

        public static boolean isRemoteDirectoryId(long directoryId) {
            return (directoryId == 0 || directoryId == 1 || directoryId == 1000000000 || directoryId == ENTERPRISE_LOCAL_INVISIBLE) ? false : true;
        }

        public static boolean isRemoteDirectory(long directoryId) {
            return isRemoteDirectoryId(directoryId);
        }

        public static boolean isEnterpriseDirectoryId(long directoryId) {
            return directoryId >= 1000000000;
        }

        public static void notifyDirectoryChange(ContentResolver resolver) {
            ContentValues contentValues = new ContentValues();
            resolver.update(CONTENT_URI, contentValues, null, null);
        }
    }

    /* loaded from: classes3.dex */
    public static final class SyncState implements SyncStateContract.Columns {
        public static final String CONTENT_DIRECTORY = "syncstate";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "syncstate");

        private SyncState() {
        }

        public static byte[] get(ContentProviderClient provider, Account account) throws RemoteException {
            return SyncStateContract.Helpers.get(provider, CONTENT_URI, account);
        }

        public static Pair<Uri, byte[]> getWithUri(ContentProviderClient provider, Account account) throws RemoteException {
            return SyncStateContract.Helpers.getWithUri(provider, CONTENT_URI, account);
        }

        public static void set(ContentProviderClient provider, Account account, byte[] data) throws RemoteException {
            SyncStateContract.Helpers.set(provider, CONTENT_URI, account, data);
        }

        public static ContentProviderOperation newSetOperation(Account account, byte[] data) {
            return SyncStateContract.Helpers.newSetOperation(CONTENT_URI, account, data);
        }
    }

    /* loaded from: classes3.dex */
    public static final class ProfileSyncState implements SyncStateContract.Columns {
        public static final String CONTENT_DIRECTORY = "syncstate";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(Profile.CONTENT_URI, "syncstate");

        private ProfileSyncState() {
        }

        public static byte[] get(ContentProviderClient provider, Account account) throws RemoteException {
            return SyncStateContract.Helpers.get(provider, CONTENT_URI, account);
        }

        public static Pair<Uri, byte[]> getWithUri(ContentProviderClient provider, Account account) throws RemoteException {
            return SyncStateContract.Helpers.getWithUri(provider, CONTENT_URI, account);
        }

        public static void set(ContentProviderClient provider, Account account, byte[] data) throws RemoteException {
            SyncStateContract.Helpers.set(provider, CONTENT_URI, account, data);
        }

        public static ContentProviderOperation newSetOperation(Account account, byte[] data) {
            return SyncStateContract.Helpers.newSetOperation(CONTENT_URI, account, data);
        }
    }

    /* loaded from: classes3.dex */
    public static class Contacts implements BaseColumns, ContactsColumns, ContactOptionsColumns, ContactNameColumns, ContactStatusColumns, ContactCounts {
        public static final Uri CONTENT_FILTER_URI;
        @Deprecated
        public static final Uri CONTENT_FREQUENT_URI;
        public static final Uri CONTENT_GROUP_URI;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact";
        public static final Uri CONTENT_LOOKUP_URI;
        public static final Uri CONTENT_MULTI_VCARD_URI;
        public static final Uri CONTENT_STREQUENT_FILTER_URI;
        public static final Uri CONTENT_STREQUENT_URI;
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact";
        public static final Uri CONTENT_URI;
        public static final String CONTENT_VCARD_TYPE = "text/x-vcard";
        public static final Uri CONTENT_VCARD_URI;
        public static final Uri CORP_CONTENT_URI;
        public static long ENTERPRISE_CONTACT_ID_BASE = 0;
        public static String ENTERPRISE_CONTACT_LOOKUP_PREFIX = null;
        public static final Uri ENTERPRISE_CONTENT_FILTER_URI;
        public static final Uri ENTERPRISE_CONTENT_URI;
        public static final String QUERY_PARAMETER_VCARD_NO_PHOTO = "no_photo";

        private Contacts() {
        }

        static {
            Uri withAppendedPath = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, android.provider.Contacts.AUTHORITY);
            CONTENT_URI = withAppendedPath;
            ENTERPRISE_CONTENT_URI = Uri.withAppendedPath(withAppendedPath, ApnSetting.TYPE_ENTERPRISE_STRING);
            CORP_CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "contacts_corp");
            CONTENT_LOOKUP_URI = Uri.withAppendedPath(withAppendedPath, "lookup");
            CONTENT_VCARD_URI = Uri.withAppendedPath(withAppendedPath, "as_vcard");
            CONTENT_MULTI_VCARD_URI = Uri.withAppendedPath(withAppendedPath, "as_multi_vcard");
            CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter");
            ENTERPRISE_CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter_enterprise");
            Uri withAppendedPath2 = Uri.withAppendedPath(withAppendedPath, "strequent");
            CONTENT_STREQUENT_URI = withAppendedPath2;
            CONTENT_FREQUENT_URI = Uri.withAppendedPath(withAppendedPath, "frequent");
            CONTENT_STREQUENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath2, "filter");
            CONTENT_GROUP_URI = Uri.withAppendedPath(withAppendedPath, "group");
            ENTERPRISE_CONTACT_ID_BASE = 1000000000L;
            ENTERPRISE_CONTACT_LOOKUP_PREFIX = "c-";
        }

        public static Uri getLookupUri(ContentResolver resolver, Uri contactUri) {
            Cursor c = resolver.query(contactUri, new String[]{"lookup", "_id"}, null, null, null);
            if (c == null) {
                return null;
            }
            try {
                if (!c.moveToFirst()) {
                    return null;
                }
                String lookupKey = c.getString(0);
                long contactId = c.getLong(1);
                return getLookupUri(contactId, lookupKey);
            } finally {
                c.close();
            }
        }

        public static Uri getLookupUri(long contactId, String lookupKey) {
            if (TextUtils.isEmpty(lookupKey)) {
                return null;
            }
            return ContentUris.withAppendedId(Uri.withAppendedPath(CONTENT_LOOKUP_URI, lookupKey), contactId);
        }

        public static Uri lookupContact(ContentResolver resolver, Uri lookupUri) {
            Cursor c;
            if (lookupUri == null || (c = resolver.query(lookupUri, new String[]{"_id"}, null, null, null)) == null) {
                return null;
            }
            try {
                if (!c.moveToFirst()) {
                    return null;
                }
                long contactId = c.getLong(0);
                return ContentUris.withAppendedId(CONTENT_URI, contactId);
            } finally {
                c.close();
            }
        }

        @Deprecated
        public static void markAsContacted(ContentResolver resolver, long contactId) {
        }

        public static boolean isEnterpriseContactId(long contactId) {
            return contactId >= ENTERPRISE_CONTACT_ID_BASE && contactId < Profile.MIN_ID;
        }

        /* loaded from: classes3.dex */
        public static final class Data implements BaseColumns, DataColumns {
            public static final String CONTENT_DIRECTORY = "data";

            private Data() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Entity implements BaseColumns, ContactsColumns, ContactNameColumns, RawContactsColumns, BaseSyncColumns, SyncColumns, DataColumns, StatusColumns, ContactOptionsColumns, ContactStatusColumns, DataUsageStatColumns {
            public static final String CONTENT_DIRECTORY = "entities";
            public static final String DATA_ID = "data_id";
            public static final String RAW_CONTACT_ID = "raw_contact_id";

            private Entity() {
            }
        }

        @Deprecated
        /* loaded from: classes3.dex */
        public static final class StreamItems implements StreamItemsColumns {
            @Deprecated
            public static final String CONTENT_DIRECTORY = "stream_items";

            @Deprecated
            private StreamItems() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class AggregationSuggestions implements BaseColumns, ContactsColumns, ContactOptionsColumns, ContactStatusColumns {
            public static final String CONTENT_DIRECTORY = "suggestions";
            public static final String PARAMETER_MATCH_NAME = "name";

            private AggregationSuggestions() {
            }

            /* loaded from: classes3.dex */
            public static final class Builder {
                private long mContactId;
                private int mLimit;
                private final ArrayList<String> mValues = new ArrayList<>();

                public Builder setContactId(long contactId) {
                    this.mContactId = contactId;
                    return this;
                }

                public Builder addNameParameter(String name) {
                    this.mValues.add(name);
                    return this;
                }

                public Builder setLimit(int limit) {
                    this.mLimit = limit;
                    return this;
                }

                public Uri build() {
                    Uri.Builder builder = Contacts.CONTENT_URI.buildUpon();
                    builder.appendEncodedPath(String.valueOf(this.mContactId));
                    builder.appendPath(AggregationSuggestions.CONTENT_DIRECTORY);
                    int i = this.mLimit;
                    if (i != 0) {
                        builder.appendQueryParameter("limit", String.valueOf(i));
                    }
                    int count = this.mValues.size();
                    for (int i2 = 0; i2 < count; i2++) {
                        builder.appendQueryParameter("query", "name:" + this.mValues.get(i2));
                    }
                    return builder.build();
                }
            }

            public static final Builder builder() {
                return new Builder();
            }
        }

        /* loaded from: classes3.dex */
        public static final class Photo implements BaseColumns, DataColumnsWithJoins {
            public static final String CONTENT_DIRECTORY = "photo";
            public static final String DISPLAY_PHOTO = "display_photo";
            public static final String PHOTO = "data15";
            public static final String PHOTO_FILE_ID = "data14";

            private Photo() {
            }
        }

        public static InputStream openContactPhotoInputStream(ContentResolver cr, Uri contactUri, boolean preferHighres) {
            if (preferHighres) {
                Uri displayPhotoUri = Uri.withAppendedPath(contactUri, "display_photo");
                try {
                    AssetFileDescriptor fd = cr.openAssetFileDescriptor(displayPhotoUri, "r");
                    if (fd != null) {
                        return fd.createInputStream();
                    }
                } catch (IOException e) {
                }
            }
            Uri photoUri = Uri.withAppendedPath(contactUri, "photo");
            if (photoUri == null) {
                return null;
            }
            Cursor cursor = cr.query(photoUri, new String[]{"data15"}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToNext()) {
                        byte[] data = cursor.getBlob(0);
                        if (data == null) {
                            return null;
                        }
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return byteArrayInputStream;
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        public static InputStream openContactPhotoInputStream(ContentResolver cr, Uri contactUri) {
            return openContactPhotoInputStream(cr, contactUri, false);
        }

        public static Uri createCorpLookupUriFromEnterpriseLookupUri(Uri enterpriseLookupUri) {
            List<String> pathSegments = enterpriseLookupUri.getPathSegments();
            if (pathSegments == null || pathSegments.size() <= 2) {
                return null;
            }
            String key = pathSegments.get(2);
            if (TextUtils.isEmpty(key) || !key.startsWith(ENTERPRISE_CONTACT_LOOKUP_PREFIX)) {
                return null;
            }
            String actualKey = key.substring(ENTERPRISE_CONTACT_LOOKUP_PREFIX.length());
            return Uri.withAppendedPath(CONTENT_LOOKUP_URI, actualKey);
        }
    }

    /* loaded from: classes3.dex */
    public static final class Profile implements BaseColumns, ContactsColumns, ContactOptionsColumns, ContactNameColumns, ContactStatusColumns {
        public static final Uri CONTENT_RAW_CONTACTS_URI;
        public static final Uri CONTENT_URI;
        public static final Uri CONTENT_VCARD_URI;
        public static final long MIN_ID = 9223372034707292160L;

        private Profile() {
        }

        static {
            Uri withAppendedPath = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, MediaFormat.KEY_PROFILE);
            CONTENT_URI = withAppendedPath;
            CONTENT_VCARD_URI = Uri.withAppendedPath(withAppendedPath, "as_vcard");
            CONTENT_RAW_CONTACTS_URI = Uri.withAppendedPath(withAppendedPath, "raw_contacts");
        }
    }

    public static boolean isProfileId(long id) {
        return id >= Profile.MIN_ID;
    }

    /* loaded from: classes3.dex */
    public static final class DeletedContacts implements DeletedContactsColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "deleted_contacts");
        private static final int DAYS_KEPT = 30;
        public static final long DAYS_KEPT_MILLISECONDS = 2592000000L;

        private DeletedContacts() {
        }
    }

    /* loaded from: classes3.dex */
    public static final class RawContacts implements BaseColumns, RawContactsColumns, ContactOptionsColumns, ContactNameColumns, SyncColumns {
        public static final int AGGREGATION_MODE_DEFAULT = 0;
        public static final int AGGREGATION_MODE_DISABLED = 3;
        @Deprecated
        public static final int AGGREGATION_MODE_IMMEDIATE = 1;
        public static final int AGGREGATION_MODE_SUSPENDED = 2;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/raw_contact";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/raw_contact";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "raw_contacts");

        private RawContacts() {
        }

        public static Uri getContactLookupUri(ContentResolver resolver, Uri rawContactUri) {
            Uri dataUri = Uri.withAppendedPath(rawContactUri, "data");
            Cursor cursor = resolver.query(dataUri, new String[]{"contact_id", "lookup"}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        long contactId = cursor.getLong(0);
                        String lookupKey = cursor.getString(1);
                        return Contacts.getLookupUri(contactId, lookupKey);
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        public static String getLocalAccountName(Context context) {
            return TextUtils.nullIfEmpty(context.getString(C4057R.string.config_rawContactsLocalAccountName));
        }

        public static String getLocalAccountType(Context context) {
            return TextUtils.nullIfEmpty(context.getString(C4057R.string.config_rawContactsLocalAccountType));
        }

        /* loaded from: classes3.dex */
        public static final class Data implements BaseColumns, DataColumns {
            public static final String CONTENT_DIRECTORY = "data";

            private Data() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Entity implements BaseColumns, DataColumns {
            public static final String CONTENT_DIRECTORY = "entity";
            public static final String DATA_ID = "data_id";

            private Entity() {
            }
        }

        @Deprecated
        /* loaded from: classes3.dex */
        public static final class StreamItems implements BaseColumns, StreamItemsColumns {
            @Deprecated
            public static final String CONTENT_DIRECTORY = "stream_items";

            @Deprecated
            private StreamItems() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class DisplayPhoto {
            public static final String CONTENT_DIRECTORY = "display_photo";

            private DisplayPhoto() {
            }
        }

        public static EntityIterator newEntityIterator(Cursor cursor) {
            return new EntityIteratorImpl(cursor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class EntityIteratorImpl extends CursorEntityIterator {
            private static final String[] DATA_KEYS = {"data1", "data2", "data3", "data4", "data5", "data6", "data7", "data8", "data9", "data10", "data11", DataColumns.DATA12, DataColumns.DATA13, "data14", "data15", DataColumns.SYNC1, DataColumns.SYNC2, DataColumns.SYNC3, DataColumns.SYNC4};

            public EntityIteratorImpl(Cursor cursor) {
                super(cursor);
            }

            @Override // android.content.CursorEntityIterator
            public android.content.Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException {
                String[] strArr;
                int columnRawContactId = cursor.getColumnIndexOrThrow("_id");
                long rawContactId = cursor.getLong(columnRawContactId);
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "account_name");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "account_type");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "data_set");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "_id");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "dirty");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "version");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sourceid");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sync1");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sync2");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sync3");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sync4");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "deleted");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "contact_id");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "starred");
                android.content.Entity contact = new android.content.Entity(cv);
                while (rawContactId == cursor.getLong(columnRawContactId)) {
                    ContentValues cv2 = new ContentValues();
                    cv2.put("_id", Long.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow("data_id"))));
                    DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv2, "res_package");
                    DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv2, "mimetype");
                    DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv2, DataColumns.IS_PRIMARY);
                    DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv2, DataColumns.IS_SUPER_PRIMARY);
                    DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv2, DataColumns.DATA_VERSION);
                    DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv2, CommonDataKinds.GroupMembership.GROUP_SOURCE_ID);
                    DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv2, DataColumns.DATA_VERSION);
                    for (String key : DATA_KEYS) {
                        int columnIndex = cursor.getColumnIndexOrThrow(key);
                        switch (cursor.getType(columnIndex)) {
                            case 0:
                                break;
                            case 1:
                            case 2:
                            case 3:
                                cv2.put(key, cursor.getString(columnIndex));
                                break;
                            case 4:
                                cv2.put(key, cursor.getBlob(columnIndex));
                                break;
                            default:
                                throw new IllegalStateException("Invalid or unhandled data type");
                        }
                    }
                    contact.addSubValue(Data.CONTENT_URI, cv2);
                    if (!cursor.moveToNext()) {
                        return contact;
                    }
                }
                return contact;
            }
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static final class StreamItems implements BaseColumns, StreamItemsColumns {
        @Deprecated
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/stream_item";
        @Deprecated
        public static final Uri CONTENT_LIMIT_URI;
        @Deprecated
        public static final Uri CONTENT_PHOTO_URI;
        @Deprecated
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/stream_item";
        @Deprecated
        public static final Uri CONTENT_URI;
        @Deprecated
        public static final String MAX_ITEMS = "max_items";

        @Deprecated
        private StreamItems() {
        }

        static {
            Uri withAppendedPath = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "stream_items");
            CONTENT_URI = withAppendedPath;
            CONTENT_PHOTO_URI = Uri.withAppendedPath(withAppendedPath, "photo");
            CONTENT_LIMIT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "stream_items_limit");
        }

        @Deprecated
        /* loaded from: classes3.dex */
        public static final class StreamItemPhotos implements BaseColumns, StreamItemPhotosColumns {
            @Deprecated
            public static final String CONTENT_DIRECTORY = "photo";
            @Deprecated
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/stream_item_photo";
            @Deprecated
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/stream_item_photo";

            @Deprecated
            private StreamItemPhotos() {
            }
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static final class StreamItemPhotos implements BaseColumns, StreamItemPhotosColumns {
        @Deprecated
        public static final String PHOTO = "photo";

        @Deprecated
        private StreamItemPhotos() {
        }
    }

    /* loaded from: classes3.dex */
    public static final class PhotoFiles implements BaseColumns, PhotoFilesColumns {
        private PhotoFiles() {
        }
    }

    /* loaded from: classes3.dex */
    public static final class Data implements DataColumnsWithJoins, ContactCounts {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/data";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "data");
        static final Uri ENTERPRISE_CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "data_enterprise");
        public static final String VISIBLE_CONTACTS_ONLY = "visible_contacts_only";

        private Data() {
        }

        public static Uri getContactLookupUri(ContentResolver resolver, Uri dataUri) {
            Cursor cursor = resolver.query(dataUri, new String[]{"contact_id", "lookup"}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        long contactId = cursor.getLong(0);
                        String lookupKey = cursor.getString(1);
                        return Contacts.getLookupUri(contactId, lookupKey);
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static final class RawContactsEntity implements BaseColumns, DataColumns, RawContactsColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/raw_contact_entity";
        public static final String DATA_ID = "data_id";
        public static final String FOR_EXPORT_ONLY = "for_export_only";
        private static final String TAG = "ContactsContract.RawContactsEntity";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "raw_contact_entities");
        public static final Uri CORP_CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "raw_contact_entities_corp");
        public static final Uri PROFILE_CONTENT_URI = Uri.withAppendedPath(Profile.CONTENT_URI, "raw_contact_entities");

        private RawContactsEntity() {
        }

        @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
        public static Map<String, List<ContentValues>> queryRawContactEntity(ContentResolver contentResolver, long contactId) {
            long realContactId;
            Uri uri;
            Uri uri2 = CONTENT_URI;
            if (Contacts.isEnterpriseContactId(contactId)) {
                Uri uri3 = CORP_CONTENT_URI;
                long realContactId2 = contactId - Contacts.ENTERPRISE_CONTACT_ID_BASE;
                realContactId = realContactId2;
                uri = uri3;
            } else {
                realContactId = contactId;
                uri = uri2;
            }
            Map<String, List<ContentValues>> contentValuesListMap = new HashMap<>();
            EntityIterator entityIterator = null;
            try {
                String[] selectionArgs = {String.valueOf(realContactId)};
                entityIterator = RawContacts.newEntityIterator(contentResolver.query(uri, null, "contact_id=?", selectionArgs, null));
                if (entityIterator == null) {
                    Log.m110e(TAG, "EntityIterator is null");
                    return contentValuesListMap;
                }
                if (!entityIterator.hasNext()) {
                    Log.m104w(TAG, "Data does not exist. contactId: " + realContactId);
                    if (entityIterator != null) {
                        entityIterator.close();
                    }
                    return contentValuesListMap;
                }
                while (entityIterator.hasNext()) {
                    Entity entity = entityIterator.next();
                    Iterator<Entity.NamedContentValues> it = entity.getSubValues().iterator();
                    while (it.hasNext()) {
                        Entity.NamedContentValues namedContentValues = it.next();
                        ContentValues contentValues = namedContentValues.values;
                        String key = contentValues.getAsString("mimetype");
                        if (key != null) {
                            List<ContentValues> contentValuesList = contentValuesListMap.get(key);
                            if (contentValuesList == null) {
                                contentValuesList = new ArrayList<>();
                                contentValuesListMap.put(key, contentValuesList);
                            }
                            contentValuesList.add(contentValues);
                        }
                    }
                }
                if (entityIterator != null) {
                    entityIterator.close();
                }
                return contentValuesListMap;
            } finally {
                if (entityIterator != null) {
                    entityIterator.close();
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class PhoneLookup implements BaseColumns, PhoneLookupColumns, ContactsColumns, ContactOptionsColumns, ContactNameColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/phone_lookup";
        public static final String QUERY_PARAMETER_SIP_ADDRESS = "sip";
        public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "phone_lookup");
        public static final Uri ENTERPRISE_CONTENT_FILTER_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "phone_lookup_enterprise");

        private PhoneLookup() {
        }
    }

    /* loaded from: classes3.dex */
    public static class StatusUpdates implements StatusColumns, PresenceColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/status-update";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/status-update";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "status_updates");
        public static final Uri PROFILE_CONTENT_URI = Uri.withAppendedPath(Profile.CONTENT_URI, "status_updates");

        private StatusUpdates() {
        }

        public static final int getPresenceIconResourceId(int status) {
            switch (status) {
                case 1:
                    return 17301609;
                case 2:
                case 3:
                    return 17301607;
                case 4:
                    return 17301608;
                case 5:
                    return 17301611;
                default:
                    return 17301610;
            }
        }

        public static final int getPresencePrecedence(int status) {
            return status;
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static final class Presence extends StatusUpdates {
        public Presence() {
            super();
        }
    }

    /* loaded from: classes3.dex */
    public static final class CommonDataKinds {
        public static final String PACKAGE_COMMON = "common";

        /* loaded from: classes3.dex */
        public interface BaseTypes {
            public static final int TYPE_CUSTOM = 0;
        }

        /* loaded from: classes3.dex */
        protected interface CommonColumns extends BaseTypes {
            public static final String DATA = "data1";
            public static final String LABEL = "data3";
            public static final String TYPE = "data2";
        }

        private CommonDataKinds() {
        }

        /* loaded from: classes3.dex */
        public static final class StructuredName implements DataColumnsWithJoins, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/name";
            public static final String DISPLAY_NAME = "data1";
            public static final String FAMILY_NAME = "data3";
            public static final String FULL_NAME_STYLE = "data10";
            public static final String GIVEN_NAME = "data2";
            public static final String MIDDLE_NAME = "data5";
            public static final String PHONETIC_FAMILY_NAME = "data9";
            public static final String PHONETIC_GIVEN_NAME = "data7";
            public static final String PHONETIC_MIDDLE_NAME = "data8";
            public static final String PHONETIC_NAME_STYLE = "data11";
            public static final String PREFIX = "data4";
            public static final String SUFFIX = "data6";

            private StructuredName() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Nickname implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/nickname";
            public static final String NAME = "data1";
            public static final int TYPE_DEFAULT = 1;
            public static final int TYPE_INITIALS = 5;
            public static final int TYPE_MAIDEN_NAME = 3;
            @Deprecated
            public static final int TYPE_MAINDEN_NAME = 3;
            public static final int TYPE_OTHER_NAME = 2;
            public static final int TYPE_SHORT_NAME = 4;

            private Nickname() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Phone implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final Uri CONTENT_FILTER_URI;
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/phone_v2";
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/phone_v2";
            public static final Uri CONTENT_URI;
            public static final Uri ENTERPRISE_CONTENT_FILTER_URI;
            public static final Uri ENTERPRISE_CONTENT_URI;
            public static final String NORMALIZED_NUMBER = "data4";
            public static final String NUMBER = "data1";
            public static final String SEARCH_DISPLAY_NAME_KEY = "search_display_name";
            public static final String SEARCH_PHONE_NUMBER_KEY = "search_phone_number";
            public static final int TYPE_ASSISTANT = 19;
            public static final int TYPE_CALLBACK = 8;
            public static final int TYPE_CAR = 9;
            public static final int TYPE_COMPANY_MAIN = 10;
            public static final int TYPE_FAX_HOME = 5;
            public static final int TYPE_FAX_WORK = 4;
            public static final int TYPE_HOME = 1;
            public static final int TYPE_ISDN = 11;
            public static final int TYPE_MAIN = 12;
            public static final int TYPE_MMS = 20;
            public static final int TYPE_MOBILE = 2;
            public static final int TYPE_OTHER = 7;
            public static final int TYPE_OTHER_FAX = 13;
            public static final int TYPE_PAGER = 6;
            public static final int TYPE_RADIO = 14;
            public static final int TYPE_TELEX = 15;
            public static final int TYPE_TTY_TDD = 16;
            public static final int TYPE_WORK = 3;
            public static final int TYPE_WORK_MOBILE = 17;
            public static final int TYPE_WORK_PAGER = 18;

            private Phone() {
            }

            static {
                Uri withAppendedPath = Uri.withAppendedPath(Data.CONTENT_URI, Contacts.People.Phones.CONTENT_DIRECTORY);
                CONTENT_URI = withAppendedPath;
                ENTERPRISE_CONTENT_URI = Uri.withAppendedPath(Data.ENTERPRISE_CONTENT_URI, Contacts.People.Phones.CONTENT_DIRECTORY);
                CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter");
                ENTERPRISE_CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter_enterprise");
            }

            @Deprecated
            public static final CharSequence getDisplayLabel(Context context, int type, CharSequence label, CharSequence[] labelArray) {
                return getTypeLabel(context.getResources(), type, label);
            }

            @Deprecated
            public static final CharSequence getDisplayLabel(Context context, int type, CharSequence label) {
                return getTypeLabel(context.getResources(), type, label);
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return C4057R.string.phoneTypeHome;
                    case 2:
                        return C4057R.string.phoneTypeMobile;
                    case 3:
                        return C4057R.string.phoneTypeWork;
                    case 4:
                        return C4057R.string.phoneTypeFaxWork;
                    case 5:
                        return C4057R.string.phoneTypeFaxHome;
                    case 6:
                        return C4057R.string.phoneTypePager;
                    case 7:
                        return C4057R.string.phoneTypeOther;
                    case 8:
                        return C4057R.string.phoneTypeCallback;
                    case 9:
                        return C4057R.string.phoneTypeCar;
                    case 10:
                        return C4057R.string.phoneTypeCompanyMain;
                    case 11:
                        return C4057R.string.phoneTypeIsdn;
                    case 12:
                        return C4057R.string.phoneTypeMain;
                    case 13:
                        return C4057R.string.phoneTypeOtherFax;
                    case 14:
                        return C4057R.string.phoneTypeRadio;
                    case 15:
                        return C4057R.string.phoneTypeTelex;
                    case 16:
                        return C4057R.string.phoneTypeTtyTdd;
                    case 17:
                        return C4057R.string.phoneTypeWorkMobile;
                    case 18:
                        return C4057R.string.phoneTypeWorkPager;
                    case 19:
                        return C4057R.string.phoneTypeAssistant;
                    case 20:
                        return C4057R.string.phoneTypeMms;
                    default:
                        return C4057R.string.phoneTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if ((type == 0 || type == 19) && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Email implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String ADDRESS = "data1";
            public static final Uri CONTENT_FILTER_URI;
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/email_v2";
            public static final Uri CONTENT_LOOKUP_URI;
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/email_v2";
            public static final Uri CONTENT_URI;
            public static final String DISPLAY_NAME = "data4";
            public static final Uri ENTERPRISE_CONTENT_FILTER_URI;
            public static final Uri ENTERPRISE_CONTENT_LOOKUP_URI;
            public static final int TYPE_HOME = 1;
            public static final int TYPE_MOBILE = 4;
            public static final int TYPE_OTHER = 3;
            public static final int TYPE_WORK = 2;

            private Email() {
            }

            static {
                Uri withAppendedPath = Uri.withAppendedPath(Data.CONTENT_URI, "emails");
                CONTENT_URI = withAppendedPath;
                CONTENT_LOOKUP_URI = Uri.withAppendedPath(withAppendedPath, "lookup");
                ENTERPRISE_CONTENT_LOOKUP_URI = Uri.withAppendedPath(withAppendedPath, "lookup_enterprise");
                CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter");
                ENTERPRISE_CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter_enterprise");
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return C4057R.string.emailTypeHome;
                    case 2:
                        return C4057R.string.emailTypeWork;
                    case 3:
                        return C4057R.string.emailTypeOther;
                    case 4:
                        return C4057R.string.emailTypeMobile;
                    default:
                        return C4057R.string.emailTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: classes3.dex */
        public static final class StructuredPostal implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String CITY = "data7";
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/postal-address_v2";
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/postal-address_v2";
            public static final Uri CONTENT_URI = Uri.withAppendedPath(Data.CONTENT_URI, "postals");
            public static final String COUNTRY = "data10";
            public static final String FORMATTED_ADDRESS = "data1";
            public static final String NEIGHBORHOOD = "data6";
            public static final String POBOX = "data5";
            public static final String POSTCODE = "data9";
            public static final String REGION = "data8";
            public static final String STREET = "data4";
            public static final int TYPE_HOME = 1;
            public static final int TYPE_OTHER = 3;
            public static final int TYPE_WORK = 2;

            private StructuredPostal() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return C4057R.string.postalTypeHome;
                    case 2:
                        return C4057R.string.postalTypeWork;
                    case 3:
                        return C4057R.string.postalTypeOther;
                    default:
                        return C4057R.string.postalTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* renamed from: android.provider.ContactsContract$CommonDataKinds$Im */
        /* loaded from: classes3.dex */
        public static final class C2358Im implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/im";
            public static final String CUSTOM_PROTOCOL = "data6";
            public static final String PROTOCOL = "data5";
            @Deprecated
            public static final int PROTOCOL_AIM = 0;
            public static final int PROTOCOL_CUSTOM = -1;
            @Deprecated
            public static final int PROTOCOL_GOOGLE_TALK = 5;
            @Deprecated
            public static final int PROTOCOL_ICQ = 6;
            @Deprecated
            public static final int PROTOCOL_JABBER = 7;
            @Deprecated
            public static final int PROTOCOL_MSN = 1;
            @Deprecated
            public static final int PROTOCOL_NETMEETING = 8;
            @Deprecated
            public static final int PROTOCOL_QQ = 4;
            @Deprecated
            public static final int PROTOCOL_SKYPE = 3;
            @Deprecated
            public static final int PROTOCOL_YAHOO = 2;
            public static final int TYPE_HOME = 1;
            public static final int TYPE_OTHER = 3;
            public static final int TYPE_WORK = 2;

            private C2358Im() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return C4057R.string.imTypeHome;
                    case 2:
                        return C4057R.string.imTypeWork;
                    case 3:
                        return C4057R.string.imTypeOther;
                    default:
                        return C4057R.string.imTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }

            public static final int getProtocolLabelResource(int type) {
                switch (type) {
                    case 0:
                        return C4057R.string.imProtocolAim;
                    case 1:
                        return C4057R.string.imProtocolMsn;
                    case 2:
                        return C4057R.string.imProtocolYahoo;
                    case 3:
                        return C4057R.string.imProtocolSkype;
                    case 4:
                        return C4057R.string.imProtocolQq;
                    case 5:
                        return C4057R.string.imProtocolGoogleTalk;
                    case 6:
                        return C4057R.string.imProtocolIcq;
                    case 7:
                        return C4057R.string.imProtocolJabber;
                    case 8:
                        return C4057R.string.imProtocolNetMeeting;
                    default:
                        return C4057R.string.imProtocolCustom;
                }
            }

            public static final CharSequence getProtocolLabel(Resources res, int type, CharSequence label) {
                if (type == -1 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getProtocolLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Organization implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String COMPANY = "data1";
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/organization";
            public static final String DEPARTMENT = "data5";
            public static final String JOB_DESCRIPTION = "data6";
            public static final String OFFICE_LOCATION = "data9";
            public static final String PHONETIC_NAME = "data8";
            public static final String PHONETIC_NAME_STYLE = "data10";
            public static final String SYMBOL = "data7";
            public static final String TITLE = "data4";
            public static final int TYPE_OTHER = 2;
            public static final int TYPE_WORK = 1;

            private Organization() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return C4057R.string.orgTypeWork;
                    case 2:
                        return C4057R.string.orgTypeOther;
                    default:
                        return C4057R.string.orgTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Relation implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/relation";
            public static final String NAME = "data1";
            public static final int TYPE_ASSISTANT = 1;
            public static final int TYPE_BROTHER = 2;
            public static final int TYPE_CHILD = 3;
            public static final int TYPE_DOMESTIC_PARTNER = 4;
            public static final int TYPE_FATHER = 5;
            public static final int TYPE_FRIEND = 6;
            public static final int TYPE_MANAGER = 7;
            public static final int TYPE_MOTHER = 8;
            public static final int TYPE_PARENT = 9;
            public static final int TYPE_PARTNER = 10;
            public static final int TYPE_REFERRED_BY = 11;
            public static final int TYPE_RELATIVE = 12;
            public static final int TYPE_SISTER = 13;
            public static final int TYPE_SPOUSE = 14;

            private Relation() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return C4057R.string.relationTypeAssistant;
                    case 2:
                        return C4057R.string.relationTypeBrother;
                    case 3:
                        return C4057R.string.relationTypeChild;
                    case 4:
                        return C4057R.string.relationTypeDomesticPartner;
                    case 5:
                        return C4057R.string.relationTypeFather;
                    case 6:
                        return C4057R.string.relationTypeFriend;
                    case 7:
                        return C4057R.string.relationTypeManager;
                    case 8:
                        return C4057R.string.relationTypeMother;
                    case 9:
                        return C4057R.string.relationTypeParent;
                    case 10:
                        return C4057R.string.relationTypePartner;
                    case 11:
                        return C4057R.string.relationTypeReferredBy;
                    case 12:
                        return C4057R.string.relationTypeRelative;
                    case 13:
                        return C4057R.string.relationTypeSister;
                    case 14:
                        return C4057R.string.relationTypeSpouse;
                    default:
                        return C4057R.string.orgTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Event implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact_event";
            public static final String START_DATE = "data1";
            public static final int TYPE_ANNIVERSARY = 1;
            public static final int TYPE_BIRTHDAY = 3;
            public static final int TYPE_OTHER = 2;

            private Event() {
            }

            public static int getTypeResource(Integer type) {
                if (type == null) {
                    return C4057R.string.eventTypeOther;
                }
                switch (type.intValue()) {
                    case 1:
                        return C4057R.string.eventTypeAnniversary;
                    case 2:
                        return C4057R.string.eventTypeOther;
                    case 3:
                        return C4057R.string.eventTypeBirthday;
                    default:
                        return C4057R.string.eventTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeResource(Integer.valueOf(type));
                return res.getText(labelRes);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Photo implements DataColumnsWithJoins, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/photo";
            public static final String PHOTO = "data15";
            public static final String PHOTO_FILE_ID = "data14";

            private Photo() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Note implements DataColumnsWithJoins, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/note";
            public static final String NOTE = "data1";

            private Note() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class GroupMembership implements DataColumnsWithJoins, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/group_membership";
            public static final String GROUP_ROW_ID = "data1";
            public static final String GROUP_SOURCE_ID = "group_sourceid";

            private GroupMembership() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Website implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/website";
            public static final int TYPE_BLOG = 2;
            public static final int TYPE_FTP = 6;
            public static final int TYPE_HOME = 4;
            public static final int TYPE_HOMEPAGE = 1;
            public static final int TYPE_OTHER = 7;
            public static final int TYPE_PROFILE = 3;
            public static final int TYPE_WORK = 5;
            public static final String URL = "data1";

            private Website() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class SipAddress implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/sip_address";
            public static final String SIP_ADDRESS = "data1";
            public static final int TYPE_HOME = 1;
            public static final int TYPE_OTHER = 3;
            public static final int TYPE_WORK = 2;

            private SipAddress() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return C4057R.string.sipAddressTypeHome;
                    case 2:
                        return C4057R.string.sipAddressTypeWork;
                    case 3:
                        return C4057R.string.sipAddressTypeOther;
                    default:
                        return C4057R.string.sipAddressTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: classes3.dex */
        public static final class Identity implements DataColumnsWithJoins, ContactCounts {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/identity";
            public static final String IDENTITY = "data1";
            public static final String NAMESPACE = "data2";

            private Identity() {
            }
        }

        /* loaded from: classes3.dex */
        public static final class Callable implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final Uri CONTENT_FILTER_URI;
            public static final Uri CONTENT_URI;
            public static final Uri ENTERPRISE_CONTENT_FILTER_URI;

            static {
                Uri withAppendedPath = Uri.withAppendedPath(Data.CONTENT_URI, "callables");
                CONTENT_URI = withAppendedPath;
                CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter");
                ENTERPRISE_CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter_enterprise");
            }
        }

        /* loaded from: classes3.dex */
        public static final class Contactables implements DataColumnsWithJoins, CommonColumns, ContactCounts {
            public static final Uri CONTENT_FILTER_URI;
            public static final Uri CONTENT_URI;
            public static final String VISIBLE_CONTACTS_ONLY = "visible_contacts_only";

            static {
                Uri withAppendedPath = Uri.withAppendedPath(Data.CONTENT_URI, "contactables");
                CONTENT_URI = withAppendedPath;
                CONTENT_FILTER_URI = Uri.withAppendedPath(withAppendedPath, "filter");
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class Groups implements BaseColumns, GroupsColumns, SyncColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/group";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/group";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "groups");
        public static final Uri CONTENT_SUMMARY_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "groups_summary");

        private Groups() {
        }

        public static EntityIterator newEntityIterator(Cursor cursor) {
            return new EntityIteratorImpl(cursor);
        }

        /* loaded from: classes3.dex */
        private static class EntityIteratorImpl extends CursorEntityIterator {
            public EntityIteratorImpl(Cursor cursor) {
                super(cursor);
            }

            @Override // android.content.CursorEntityIterator
            public Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException {
                ContentValues values = new ContentValues();
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, "_id");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "account_name");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "account_type");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, "dirty");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, "version");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sourceid");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "res_package");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "title");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, GroupsColumns.TITLE_RES);
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, GroupsColumns.GROUP_VISIBLE);
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sync1");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sync2");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sync3");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sync4");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "system_id");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, "deleted");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "notes");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "should_sync");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, GroupsColumns.FAVORITES);
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, GroupsColumns.AUTO_ADD);
                cursor.moveToNext();
                return new Entity(values);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class AggregationExceptions implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/aggregation_exception";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/aggregation_exception";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "aggregation_exceptions");
        public static final String RAW_CONTACT_ID1 = "raw_contact_id1";
        public static final String RAW_CONTACT_ID2 = "raw_contact_id2";
        public static final String TYPE = "type";
        public static final int TYPE_AUTOMATIC = 0;
        public static final int TYPE_KEEP_SEPARATE = 2;
        public static final int TYPE_KEEP_TOGETHER = 1;

        private AggregationExceptions() {
        }
    }

    /* loaded from: classes3.dex */
    public static final class SimContacts {
        public static final String ACTION_SIM_ACCOUNTS_CHANGED = "android.provider.action.SIM_ACCOUNTS_CHANGED";
        public static final String ADD_SIM_ACCOUNT_METHOD = "addSimAccount";
        public static final String KEY_ACCOUNT_NAME = "key_sim_account_name";
        public static final String KEY_ACCOUNT_TYPE = "key_sim_account_type";
        public static final String KEY_SIM_ACCOUNTS = "key_sim_accounts";
        public static final String KEY_SIM_EF_TYPE = "key_sim_ef_type";
        public static final String KEY_SIM_SLOT_INDEX = "key_sim_slot_index";
        public static final String QUERY_SIM_ACCOUNTS_METHOD = "querySimAccounts";
        public static final String REMOVE_SIM_ACCOUNT_METHOD = "removeSimAccount";

        private SimContacts() {
        }

        @SystemApi
        public static void addSimAccount(ContentResolver contentResolver, String accountName, String accountType, int simSlotIndex, int efType) {
            if (simSlotIndex < 0) {
                throw new IllegalArgumentException("Sim slot is negative");
            }
            if (!SimAccount.getValidEfTypes().contains(Integer.valueOf(efType))) {
                throw new IllegalArgumentException("Invalid EF type");
            }
            if (TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountType)) {
                throw new IllegalArgumentException("Account name or type is empty");
            }
            Bundle extras = new Bundle();
            extras.putInt(KEY_SIM_SLOT_INDEX, simSlotIndex);
            extras.putInt(KEY_SIM_EF_TYPE, efType);
            extras.putString(KEY_ACCOUNT_NAME, accountName);
            extras.putString(KEY_ACCOUNT_TYPE, accountType);
            contentResolver.call(ContactsContract.AUTHORITY_URI, ADD_SIM_ACCOUNT_METHOD, (String) null, extras);
        }

        @SystemApi
        public static void removeSimAccounts(ContentResolver contentResolver, int simSlotIndex) {
            if (simSlotIndex < 0) {
                throw new IllegalArgumentException("Sim slot is negative");
            }
            Bundle extras = new Bundle();
            extras.putInt(KEY_SIM_SLOT_INDEX, simSlotIndex);
            contentResolver.call(ContactsContract.AUTHORITY_URI, REMOVE_SIM_ACCOUNT_METHOD, (String) null, extras);
        }

        public static List<SimAccount> getSimAccounts(ContentResolver contentResolver) {
            Bundle response = contentResolver.call(ContactsContract.AUTHORITY_URI, QUERY_SIM_ACCOUNTS_METHOD, (String) null, (Bundle) null);
            List<SimAccount> result = response.getParcelableArrayList(KEY_SIM_ACCOUNTS, SimAccount.class);
            if (result == null) {
                return new ArrayList<>();
            }
            return result;
        }
    }

    /* loaded from: classes3.dex */
    public static final class SimAccount implements Parcelable {
        public static final int ADN_EF_TYPE = 1;
        public static final Parcelable.Creator<SimAccount> CREATOR = new Parcelable.Creator<SimAccount>() { // from class: android.provider.ContactsContract.SimAccount.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SimAccount createFromParcel(Parcel source) {
                String accountName = source.readString();
                String accountType = source.readString();
                int simSlot = source.readInt();
                int efType = source.readInt();
                SimAccount simAccount = new SimAccount(accountName, accountType, simSlot, efType);
                return simAccount;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SimAccount[] newArray(int size) {
                return new SimAccount[size];
            }
        };
        public static final int FDN_EF_TYPE = 2;
        public static final int SDN_EF_TYPE = 3;
        public static final int UNKNOWN_EF_TYPE = 0;
        private final String mAccountName;
        private final String mAccountType;
        private final int mEfType;
        private final int mSimSlotIndex;

        public static Set<Integer> getValidEfTypes() {
            return Sets.newArraySet(1, 3, 2);
        }

        public SimAccount(String accountName, String accountType, int simSlotIndex, int efType) {
            this.mAccountName = accountName;
            this.mAccountType = accountType;
            this.mSimSlotIndex = simSlotIndex;
            this.mEfType = efType;
        }

        public String getAccountName() {
            return this.mAccountName;
        }

        public String getAccountType() {
            return this.mAccountType;
        }

        public int getSimSlotIndex() {
            return this.mSimSlotIndex;
        }

        public int getEfType() {
            return this.mEfType;
        }

        public int hashCode() {
            return Objects.hash(this.mAccountName, this.mAccountType, Integer.valueOf(this.mSimSlotIndex), Integer.valueOf(this.mEfType));
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            try {
                SimAccount toCompare = (SimAccount) obj;
                if (this.mSimSlotIndex != toCompare.mSimSlotIndex || this.mEfType != toCompare.mEfType || !Objects.equals(this.mAccountName, toCompare.mAccountName) || !Objects.equals(this.mAccountType, toCompare.mAccountType)) {
                    return false;
                }
                return true;
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mAccountName);
            dest.writeString(this.mAccountType);
            dest.writeInt(this.mSimSlotIndex);
            dest.writeInt(this.mEfType);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }
    }

    /* loaded from: classes3.dex */
    public static final class Settings implements SettingsColumns {
        public static final String ACTION_SET_DEFAULT_ACCOUNT = "android.provider.action.SET_DEFAULT_ACCOUNT";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/setting";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/setting";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "settings");
        public static final String KEY_DEFAULT_ACCOUNT = "key_default_account";
        public static final String QUERY_DEFAULT_ACCOUNT_METHOD = "queryDefaultAccount";
        public static final String SET_DEFAULT_ACCOUNT_METHOD = "setDefaultAccount";

        private Settings() {
        }

        public static Account getDefaultAccount(ContentResolver resolver) {
            Bundle response = resolver.call(ContactsContract.AUTHORITY_URI, QUERY_DEFAULT_ACCOUNT_METHOD, (String) null, (Bundle) null);
            return (Account) response.getParcelable(KEY_DEFAULT_ACCOUNT, Account.class);
        }

        @SystemApi
        public static void setDefaultAccount(ContentResolver resolver, Account account) {
            Bundle extras = new Bundle();
            if (account != null) {
                extras.putString("account_name", account.name);
                extras.putString("account_type", account.type);
            }
            resolver.call(ContactsContract.AUTHORITY_URI, SET_DEFAULT_ACCOUNT_METHOD, (String) null, extras);
        }
    }

    /* loaded from: classes3.dex */
    public static final class ProviderStatus {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/provider_status";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "provider_status");
        public static final String DATABASE_CREATION_TIMESTAMP = "database_creation_timestamp";
        public static final String STATUS = "status";
        public static final int STATUS_BUSY = 1;
        public static final int STATUS_EMPTY = 2;
        public static final int STATUS_NORMAL = 0;

        private ProviderStatus() {
        }
    }

    /* loaded from: classes3.dex */
    public static final class PinnedPositions {
        public static final int DEMOTED = -1;
        public static final String UNDEMOTE_METHOD = "undemote";
        public static final int UNPINNED = 0;

        public static void undemote(ContentResolver contentResolver, long contactId) {
            contentResolver.call(ContactsContract.AUTHORITY_URI, UNDEMOTE_METHOD, String.valueOf(contactId), (Bundle) null);
        }

        public static void pin(ContentResolver contentResolver, long contactId, int pinnedPosition) {
            Uri uri = Uri.withAppendedPath(Contacts.CONTENT_URI, String.valueOf(contactId));
            ContentValues values = new ContentValues();
            values.put(ContactOptionsColumns.PINNED, Integer.valueOf(pinnedPosition));
            contentResolver.update(uri, values, null, null);
        }
    }

    /* loaded from: classes3.dex */
    public static final class QuickContact {
        public static final String ACTION_QUICK_CONTACT = "android.provider.action.QUICK_CONTACT";
        public static final String EXTRA_EXCLUDE_MIMES = "android.provider.extra.EXCLUDE_MIMES";
        public static final String EXTRA_MODE = "android.provider.extra.MODE";
        public static final String EXTRA_PRIORITIZED_MIMETYPE = "android.provider.extra.PRIORITIZED_MIMETYPE";
        @Deprecated
        public static final String EXTRA_TARGET_RECT = "android.provider.extra.TARGET_RECT";
        public static final int MODE_DEFAULT = 3;
        public static final int MODE_LARGE = 3;
        public static final int MODE_MEDIUM = 2;
        public static final int MODE_SMALL = 1;

        public static Intent composeQuickContactsIntent(Context context, View target, Uri lookupUri, int mode, String[] excludeMimes) {
            float appScale = context.getResources().getCompatibilityInfo().applicationScale;
            int[] pos = new int[2];
            target.getLocationOnScreen(pos);
            Rect rect = new Rect();
            rect.left = (int) ((pos[0] * appScale) + 0.5f);
            rect.top = (int) ((pos[1] * appScale) + 0.5f);
            rect.right = (int) (((pos[0] + target.getWidth()) * appScale) + 0.5f);
            rect.bottom = (int) (((pos[1] + target.getHeight()) * appScale) + 0.5f);
            return composeQuickContactsIntent(context, rect, lookupUri, mode, excludeMimes);
        }

        public static Intent composeQuickContactsIntent(Context context, Rect target, Uri lookupUri, int mode, String[] excludeMimes) {
            Context actualContext = context;
            while ((actualContext instanceof ContextWrapper) && !(actualContext instanceof Activity)) {
                actualContext = ((ContextWrapper) actualContext).getBaseContext();
            }
            int intentFlags = (actualContext instanceof Activity ? 0 : 268468224) | 536870912;
            Intent intent = new Intent(ACTION_QUICK_CONTACT).addFlags(intentFlags);
            intent.setData(lookupUri);
            intent.setSourceBounds(target);
            intent.putExtra(EXTRA_MODE, mode);
            intent.putExtra(EXTRA_EXCLUDE_MIMES, excludeMimes);
            return intent;
        }

        public static Intent rebuildManagedQuickContactsIntent(String lookupKey, long contactId, boolean isContactIdIgnored, long directoryId, Intent originalIntent) {
            Uri lookupUri;
            Intent intent = new Intent(ACTION_QUICK_CONTACT);
            Uri uri = null;
            if (!TextUtils.isEmpty(lookupKey)) {
                if (isContactIdIgnored) {
                    lookupUri = Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey);
                } else {
                    lookupUri = Contacts.getLookupUri(contactId, lookupKey);
                }
                uri = lookupUri;
            }
            if (uri != null && directoryId != 0) {
                uri = uri.buildUpon().appendQueryParameter("directory", String.valueOf(directoryId)).build();
            }
            intent.setData(uri);
            intent.setFlags(originalIntent.getFlags() | 268435456);
            intent.setSourceBounds(originalIntent.getSourceBounds());
            intent.putExtra(EXTRA_MODE, originalIntent.getIntExtra(EXTRA_MODE, 3));
            intent.putExtra(EXTRA_EXCLUDE_MIMES, originalIntent.getStringArrayExtra(EXTRA_EXCLUDE_MIMES));
            return intent;
        }

        public static void showQuickContact(Context context, View target, Uri lookupUri, int mode, String[] excludeMimes) {
            Intent intent = composeQuickContactsIntent(context, target, lookupUri, mode, excludeMimes);
            ContactsInternal.startQuickContactWithErrorToast(context, intent);
        }

        public static void showQuickContact(Context context, Rect target, Uri lookupUri, int mode, String[] excludeMimes) {
            Intent intent = composeQuickContactsIntent(context, target, lookupUri, mode, excludeMimes);
            ContactsInternal.startQuickContactWithErrorToast(context, intent);
        }

        public static void showQuickContact(Context context, View target, Uri lookupUri, String[] excludeMimes, String prioritizedMimeType) {
            Intent intent = composeQuickContactsIntent(context, target, lookupUri, 3, excludeMimes);
            intent.putExtra(EXTRA_PRIORITIZED_MIMETYPE, prioritizedMimeType);
            ContactsInternal.startQuickContactWithErrorToast(context, intent);
        }

        public static void showQuickContact(Context context, Rect target, Uri lookupUri, String[] excludeMimes, String prioritizedMimeType) {
            Intent intent = composeQuickContactsIntent(context, target, lookupUri, 3, excludeMimes);
            intent.putExtra(EXTRA_PRIORITIZED_MIMETYPE, prioritizedMimeType);
            ContactsInternal.startQuickContactWithErrorToast(context, intent);
        }
    }

    /* loaded from: classes3.dex */
    public static final class DisplayPhoto {
        public static final String DISPLAY_MAX_DIM = "display_max_dim";
        public static final String THUMBNAIL_MAX_DIM = "thumbnail_max_dim";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "display_photo");
        public static final Uri CONTENT_MAX_DIMENSIONS_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "photo_dimensions");

        private DisplayPhoto() {
        }
    }

    @SystemApi
    @Deprecated
    /* loaded from: classes3.dex */
    public static final class MetadataSync implements BaseColumns, MetadataSyncColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact_metadata";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact_metadata";
        public static final Uri CONTENT_URI;
        public static final String METADATA_AUTHORITY = "com.android.contacts.metadata";
        public static final Uri METADATA_AUTHORITY_URI;

        static {
            Uri parse = Uri.parse("content://com.android.contacts.metadata");
            METADATA_AUTHORITY_URI = parse;
            CONTENT_URI = Uri.withAppendedPath(parse, "metadata_sync");
        }

        private MetadataSync() {
        }
    }

    @SystemApi
    @Deprecated
    /* loaded from: classes3.dex */
    public static final class MetadataSyncState implements BaseColumns, MetadataSyncStateColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact_metadata_sync_state";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact_metadata_sync_state";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(MetadataSync.METADATA_AUTHORITY_URI, "metadata_sync_state");

        private MetadataSyncState() {
        }
    }
}
