package com.google.android.mms.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;
/* loaded from: classes5.dex */
public final class SqliteWrapper {
    private static final String TAG = "SqliteWrapper";

    private SqliteWrapper() {
    }

    public static Cursor query(Context context, ContentResolver resolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        try {
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (SQLiteException e) {
            Log.m109e(TAG, "Catch a SQLiteException when query: ", e);
            return null;
        }
    }

    public static int update(Context context, ContentResolver resolver, Uri uri, ContentValues values, String where, String[] selectionArgs) {
        try {
            return resolver.update(uri, values, where, selectionArgs);
        } catch (SQLiteException e) {
            Log.m109e(TAG, "Catch a SQLiteException when update: ", e);
            return -1;
        }
    }

    public static int delete(Context context, ContentResolver resolver, Uri uri, String where, String[] selectionArgs) {
        try {
            return resolver.delete(uri, where, selectionArgs);
        } catch (SQLiteException e) {
            Log.m109e(TAG, "Catch a SQLiteException when delete: ", e);
            return -1;
        }
    }

    public static Uri insert(Context context, ContentResolver resolver, Uri uri, ContentValues values) {
        try {
            return resolver.insert(uri, values);
        } catch (SQLiteException e) {
            Log.m109e(TAG, "Catch a SQLiteException when insert: ", e);
            return null;
        }
    }
}
