package com.android.ims.rcs.uce.eab;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Contact;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Presence;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class EabProvider extends ContentProvider {
    public static final String AUTHORITY = "eab";
    private static final int DATABASE_VERSION = 4;
    public static final String EAB_COMMON_TABLE_NAME = "eab_common";
    public static final String EAB_CONTACT_TABLE_NAME = "eab_contact";
    public static final String EAB_OPTIONS_TABLE_NAME = "eab_options";
    public static final String EAB_PRESENCE_TUPLE_TABLE_NAME = "eab_presence";
    private static final String JOIN_ALL_TABLES = " INNER JOIN eab_common ON eab_contact._id=eab_common.eab_contact_id LEFT JOIN eab_options ON eab_common._id=eab_options.eab_common_id LEFT JOIN eab_presence ON eab_common._id=eab_presence.eab_common_id";
    private static final String QUERY_CONTACT_TABLE = " SELECT * FROM eab_contact";
    private static final String TAG = "EabProvider";
    private static final UriMatcher URI_MATCHER;
    private static final int URL_ALL = 5;
    private static final int URL_ALL_WITH_SUB_ID_AND_PHONE_NUMBER = 6;
    private static final int URL_COMMON = 2;
    private static final int URL_CONTACT = 1;
    private static final int URL_OPTIONS = 4;
    private static final int URL_PRESENCE = 3;
    private EabDatabaseHelper mOpenHelper;
    public static final Uri CONTACT_URI = Uri.parse("content://eab/contact");
    public static final Uri COMMON_URI = Uri.parse("content://eab/common");
    public static final Uri PRESENCE_URI = Uri.parse("content://eab/presence");
    public static final Uri OPTIONS_URI = Uri.parse("content://eab/options");
    public static final Uri ALL_DATA_URI = Uri.parse("content://eab/all");

    /* loaded from: classes.dex */
    public static class ContactColumns implements BaseColumns {
        public static final String CONTACT_ID = "contact_id";
        public static final String DATA_ID = "data_id";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String RAW_CONTACT_ID = "raw_contact_id";
    }

    /* loaded from: classes.dex */
    public static class EabCommonColumns implements BaseColumns {
        public static final String EAB_CONTACT_ID = "eab_contact_id";
        public static final String ENTITY_URI = "entity_uri";
        public static final String MECHANISM = "mechanism";
        public static final String REQUEST_RESULT = "request_result";
        public static final String SUBSCRIPTION_ID = "subscription_id";
    }

    /* loaded from: classes.dex */
    public static class OptionsColumns implements BaseColumns {
        public static final String EAB_COMMON_ID = "eab_common_id";
        public static final String FEATURE_TAG = "feature_tag";
        public static final String REQUEST_TIMESTAMP = "options_request_timestamp";
    }

    /* loaded from: classes.dex */
    public static class PresenceTupleColumns implements BaseColumns {
        public static final String AUDIO_CAPABLE = "audio_capable";
        public static final String BASIC_STATUS = "basic_status";
        public static final String CONTACT_URI = "contact_uri";
        public static final String DESCRIPTION = "description";
        public static final String DUPLEX_MODE = "duplex_mode";
        public static final String EAB_COMMON_ID = "eab_common_id";
        public static final String REQUEST_TIMESTAMP = "presence_request_timestamp";
        public static final String SERVICE_ID = "service_id";
        public static final String SERVICE_VERSION = "service_version";
        public static final String UNSUPPORTED_DUPLEX_MODE = "unsupported_duplex_mode";
        public static final String VIDEO_CAPABLE = "video_capable";
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        URI_MATCHER = uriMatcher;
        uriMatcher.addURI(AUTHORITY, Contact.ELEMENT_NAME, 1);
        uriMatcher.addURI(AUTHORITY, "common", 2);
        uriMatcher.addURI(AUTHORITY, Presence.ELEMENT_NAME, 3);
        uriMatcher.addURI(AUTHORITY, "options", 4);
        uriMatcher.addURI(AUTHORITY, "all", 5);
        uriMatcher.addURI(AUTHORITY, "all/#/*", 6);
    }

    /* loaded from: classes.dex */
    public static final class EabDatabaseHelper extends SQLiteOpenHelper {
        private static final List<String> COMMON_UNIQUE_FIELDS;
        private static final List<String> CONTACT_UNIQUE_FIELDS;
        private static final String DB_NAME = "EabDatabase";
        public static final String SQL_CREATE_COMMON_TABLE = "CREATE TABLE eab_common (_id INTEGER PRIMARY KEY, eab_contact_id INTEGER DEFAULT -1, mechanism INTEGER DEFAULT NULL, request_result INTEGER DEFAULT -1, subscription_id INTEGER DEFAULT -1, entity_uri TEXT DEFAULT NULL );";
        public static final String SQL_CREATE_CONTACT_TABLE;
        public static final String SQL_CREATE_OPTIONS_TABLE = "CREATE TABLE eab_options (_id INTEGER PRIMARY KEY, eab_common_id INTEGER DEFAULT -1, options_request_timestamp LONG DEFAULT NULL, feature_tag TEXT DEFAULT NULL );";
        public static final String SQL_CREATE_PRESENCE_TUPLE_TABLE = "CREATE TABLE eab_presence (_id INTEGER PRIMARY KEY, eab_common_id INTEGER DEFAULT -1, basic_status TEXT DEFAULT NULL, service_id TEXT DEFAULT NULL, service_version TEXT DEFAULT NULL, description TEXT DEFAULT NULL, presence_request_timestamp LONG DEFAULT NULL, contact_uri TEXT DEFAULT NULL, duplex_mode TEXT DEFAULT NULL, unsupported_duplex_mode TEXT DEFAULT NULL, audio_capable BOOLEAN DEFAULT NULL, video_capable BOOLEAN DEFAULT NULL);";

        static {
            ArrayList arrayList = new ArrayList();
            CONTACT_UNIQUE_FIELDS = arrayList;
            COMMON_UNIQUE_FIELDS = new ArrayList();
            arrayList.add(ContactColumns.PHONE_NUMBER);
            SQL_CREATE_CONTACT_TABLE = "CREATE TABLE eab_contact (_id INTEGER PRIMARY KEY, phone_number Text DEFAULT NULL, contact_id INTEGER DEFAULT -1, raw_contact_id INTEGER DEFAULT -1, data_id INTEGER DEFAULT -1, UNIQUE (" + TextUtils.join(", ", arrayList) + "));";
        }

        EabDatabaseHelper(Context context) {
            super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, 4);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_CONTACT_TABLE);
            db.execSQL(SQL_CREATE_COMMON_TABLE);
            db.execSQL(SQL_CREATE_PRESENCE_TUPLE_TABLE);
            db.execSQL(SQL_CREATE_OPTIONS_TABLE);
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            Log.d(EabProvider.TAG, "DB upgrade from " + oldVersion + " to " + newVersion);
            if (oldVersion < 2) {
                sqLiteDatabase.execSQL("ALTER TABLE eab_contact ADD COLUMN contact_id INTEGER DEFAULT -1;");
                oldVersion = 2;
            }
            if (oldVersion < 3) {
                sqLiteDatabase.execSQL("CREATE TABLE temp (_id INTEGER PRIMARY KEY, eab_contact_id INTEGER DEFAULT -1, mechanism INTEGER DEFAULT NULL, request_result INTEGER DEFAULT -1, subscription_id INTEGER DEFAULT -1 );");
                sqLiteDatabase.execSQL("INSERT INTO temp (_id, eab_contact_id, mechanism, request_result, subscription_id)  SELECT _id, eab_contact_id, mechanism, request_result, subscription_id  FROM eab_common;");
                sqLiteDatabase.execSQL("DROP TABLE eab_common;");
                sqLiteDatabase.execSQL("ALTER TABLE temp RENAME TO eab_common;");
                oldVersion = 3;
            }
            if (oldVersion < 4) {
                sqLiteDatabase.execSQL("ALTER TABLE eab_common ADD COLUMN entity_uri Text DEFAULT NULL;");
            }
        }
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mOpenHelper = new EabDatabaseHelper(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = getReadableDatabase();
        int match = URI_MATCHER.match(uri);
        Log.d(TAG, "Query URI: " + match);
        switch (match) {
            case 1:
                qb.setTables(EAB_CONTACT_TABLE_NAME);
                break;
            case 2:
                qb.setTables(EAB_COMMON_TABLE_NAME);
                break;
            case 3:
                qb.setTables(EAB_PRESENCE_TUPLE_TABLE_NAME);
                break;
            case 4:
                qb.setTables(EAB_OPTIONS_TABLE_NAME);
                break;
            case 5:
                qb.setTables("( SELECT * FROM eab_contact INNER JOIN eab_common ON eab_contact._id=eab_common.eab_contact_id LEFT JOIN eab_options ON eab_common._id=eab_options.eab_common_id LEFT JOIN eab_presence ON eab_common._id=eab_presence.eab_common_id)");
                break;
            case 6:
                List<String> pathSegment = uri.getPathSegments();
                String subIdString = pathSegment.get(1);
                try {
                    int subId = Integer.parseInt(subIdString);
                    qb.appendWhereStandalone("subscription_id=" + subId);
                    String phoneNumber = pathSegment.get(2);
                    if (TextUtils.isEmpty(phoneNumber)) {
                        Log.e(TAG, "phone number is null");
                        return null;
                    }
                    String whereClause = " where phone_number='" + phoneNumber + "' ";
                    qb.setTables("(( SELECT * FROM eab_contact" + whereClause + ") AS " + EAB_CONTACT_TABLE_NAME + JOIN_ALL_TABLES + ")");
                    break;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "NumberFormatException" + e);
                    return null;
                }
            default:
                Log.d(TAG, "Query failed. Not support URL.");
                return null;
        }
        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        int match = URI_MATCHER.match(uri);
        long result = 0;
        String tableName = "";
        switch (match) {
            case 1:
                tableName = EAB_CONTACT_TABLE_NAME;
                break;
            case 2:
                tableName = EAB_COMMON_TABLE_NAME;
                break;
            case 3:
                tableName = EAB_PRESENCE_TUPLE_TABLE_NAME;
                break;
            case 4:
                tableName = EAB_OPTIONS_TABLE_NAME;
                break;
        }
        if (!TextUtils.isEmpty(tableName)) {
            result = db.insertWithOnConflict(tableName, null, contentValues, 5);
            Log.d(TAG, "Insert uri: " + match + " ID: " + result);
            if (result > 0) {
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null, 4);
            }
        } else {
            Log.d(TAG, "Insert. Not support URI.");
        }
        return Uri.withAppendedPath(uri, String.valueOf(result));
    }

    @Override // android.content.ContentProvider
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = getWritableDatabase();
        int match = URI_MATCHER.match(uri);
        int result = 0;
        String tableName = "";
        switch (match) {
            case 1:
                tableName = EAB_CONTACT_TABLE_NAME;
                break;
            case 2:
                tableName = EAB_COMMON_TABLE_NAME;
                break;
            case 3:
                tableName = EAB_PRESENCE_TUPLE_TABLE_NAME;
                break;
            case 4:
                tableName = EAB_OPTIONS_TABLE_NAME;
                break;
        }
        if (TextUtils.isEmpty(tableName)) {
            Log.d(TAG, "bulkInsert. Not support URI.");
            return 0;
        }
        try {
            db.beginTransaction();
            for (ContentValues contentValue : values) {
                if (contentValue != null) {
                    db.insertWithOnConflict(tableName, null, contentValue, 5);
                    result++;
                }
            }
            db.setTransactionSuccessful();
            if (result > 0) {
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null, 4);
            }
            Log.d(TAG, "bulkInsert uri: " + match + " count: " + result);
            return result;
        } finally {
            db.endTransaction();
        }
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int match = URI_MATCHER.match(uri);
        int result = 0;
        String tableName = "";
        switch (match) {
            case 1:
                tableName = EAB_CONTACT_TABLE_NAME;
                break;
            case 2:
                tableName = EAB_COMMON_TABLE_NAME;
                break;
            case 3:
                tableName = EAB_PRESENCE_TUPLE_TABLE_NAME;
                break;
            case 4:
                tableName = EAB_OPTIONS_TABLE_NAME;
                break;
        }
        if (!TextUtils.isEmpty(tableName)) {
            result = db.delete(tableName, selection, selectionArgs);
            if (result > 0) {
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null, 16);
            }
            Log.d(TAG, "Delete uri: " + match + " result: " + result);
        } else {
            Log.d(TAG, "Delete. Not support URI.");
        }
        return result;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        String tableName;
        SQLiteDatabase db = getWritableDatabase();
        int match = URI_MATCHER.match(uri);
        int result = 0;
        switch (match) {
            case 1:
                tableName = EAB_CONTACT_TABLE_NAME;
                break;
            case 2:
                tableName = EAB_COMMON_TABLE_NAME;
                break;
            case 3:
                tableName = EAB_PRESENCE_TUPLE_TABLE_NAME;
                break;
            case 4:
                tableName = EAB_OPTIONS_TABLE_NAME;
                break;
            default:
                tableName = "";
                break;
        }
        if (!TextUtils.isEmpty(tableName)) {
            result = db.updateWithOnConflict(tableName, contentValues, selection, selectionArgs, 5);
            if (result > 0) {
                getContext().getContentResolver().notifyChange(uri, (ContentObserver) null, 8);
            }
            Log.d(TAG, "Update uri: " + match + " result: " + result);
        } else {
            Log.d(TAG, "Update. Not support URI.");
        }
        return result;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    public SQLiteDatabase getWritableDatabase() {
        return this.mOpenHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDatabase() {
        return this.mOpenHelper.getReadableDatabase();
    }
}
