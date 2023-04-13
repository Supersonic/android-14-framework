package com.android.server.people.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Slog;
/* loaded from: classes2.dex */
public class ContactsQueryHelper {
    public Uri mContactUri;
    public final Context mContext;
    public boolean mIsStarred;
    public long mLastUpdatedTimestamp;
    public String mPhoneNumber;

    public ContactsQueryHelper(Context context) {
        this.mContext = context;
    }

    public boolean query(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Uri parse = Uri.parse(str);
        if ("tel".equals(parse.getScheme())) {
            return queryWithPhoneNumber(parse.getSchemeSpecificPart());
        }
        if ("mailto".equals(parse.getScheme())) {
            return queryWithEmail(parse.getSchemeSpecificPart());
        }
        if (str.startsWith(ContactsContract.Contacts.CONTENT_LOOKUP_URI.toString())) {
            return queryWithUri(parse);
        }
        return false;
    }

    public boolean querySince(long j) {
        return queryContact(ContactsContract.Contacts.CONTENT_URI, new String[]{"_id", "lookup", "starred", "has_phone_number", "contact_last_updated_timestamp"}, "contact_last_updated_timestamp > ?", new String[]{Long.toString(j)});
    }

    public Uri getContactUri() {
        return this.mContactUri;
    }

    public boolean isStarred() {
        return this.mIsStarred;
    }

    public String getPhoneNumber() {
        return this.mPhoneNumber;
    }

    public long getLastUpdatedTimestamp() {
        return this.mLastUpdatedTimestamp;
    }

    public final boolean queryWithPhoneNumber(String str) {
        return queryWithUri(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(str)));
    }

    public final boolean queryWithEmail(String str) {
        return queryWithUri(Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(str)));
    }

    public final boolean queryWithUri(Uri uri) {
        return queryContact(uri, new String[]{"_id", "lookup", "starred", "has_phone_number"}, null, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x00a6 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean queryContact(Uri uri, String[] strArr, String str, String[] strArr2) {
        boolean z;
        Cursor query;
        String str2 = null;
        boolean z2 = false;
        try {
            query = this.mContext.getContentResolver().query(uri, strArr, str, strArr2, null);
        } catch (SQLiteException e) {
            e = e;
            z = false;
        } catch (IllegalArgumentException e2) {
            e = e2;
            z = false;
        }
        if (query == null) {
            try {
                Slog.w("ContactsQueryHelper", "Cursor is null when querying contact.");
                if (query != null) {
                    query.close();
                }
                return false;
            } catch (Throwable th) {
                th = th;
            }
        } else {
            boolean z3 = false;
            z = false;
            while (query.moveToNext()) {
                try {
                    long j = query.getLong(query.getColumnIndex("_id"));
                    str2 = query.getString(query.getColumnIndex("lookup"));
                    this.mContactUri = ContactsContract.Contacts.getLookupUri(j, str2);
                    this.mIsStarred = query.getInt(query.getColumnIndex("starred")) != 0;
                    z3 = query.getInt(query.getColumnIndex("has_phone_number")) != 0;
                    int columnIndex = query.getColumnIndex("contact_last_updated_timestamp");
                    if (columnIndex >= 0) {
                        this.mLastUpdatedTimestamp = query.getLong(columnIndex);
                    }
                    z = true;
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            try {
                query.close();
            } catch (SQLiteException e3) {
                e = e3;
                z2 = z3;
                Slog.w("SQLite exception when querying contacts.", e);
                z3 = z2;
                if (z) {
                }
            } catch (IllegalArgumentException e4) {
                e = e4;
                z2 = z3;
                Slog.w("Illegal Argument exception when querying contacts.", e);
                z3 = z2;
                if (z) {
                }
            }
            return (z || str2 == null || !z3) ? z : queryPhoneNumber(str2);
        }
        if (query != null) {
            try {
                query.close();
            }
        }
        throw th;
    }

    public final boolean queryPhoneNumber(String str) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor query = contentResolver.query(uri, new String[]{"data4"}, "lookup = ?", new String[]{str}, null);
        try {
            if (query == null) {
                Slog.w("ContactsQueryHelper", "Cursor is null when querying contact phone number.");
                if (query != null) {
                    query.close();
                    return false;
                }
                return false;
            }
            while (query.moveToNext()) {
                int columnIndex = query.getColumnIndex("data4");
                if (columnIndex >= 0) {
                    this.mPhoneNumber = query.getString(columnIndex);
                }
            }
            query.close();
            return true;
        } catch (Throwable th) {
            if (query != null) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}
