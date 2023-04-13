package com.android.server.locksettings.recoverablekeystore.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/* loaded from: classes2.dex */
public class RecoverableKeyStoreDbHelper extends SQLiteOpenHelper {
    public static int getDbVersion(Context context) {
        return 6;
    }

    public RecoverableKeyStoreDbHelper(Context context) {
        super(context, "recoverablekeystore.db", (SQLiteDatabase.CursorFactory) null, getDbVersion(context));
    }

    public RecoverableKeyStoreDbHelper(Context context, int i) {
        super(context, "recoverablekeystore.db", (SQLiteDatabase.CursorFactory) null, i);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE keys( _id INTEGER PRIMARY KEY,user_id INTEGER,uid INTEGER,alias TEXT,nonce BLOB,wrapped_key BLOB,platform_key_generation_id INTEGER,last_synced_at INTEGER,recovery_status INTEGER,key_metadata BLOB,UNIQUE(uid,alias))");
        if (sQLiteDatabase.getVersion() == 6) {
            sQLiteDatabase.execSQL("CREATE TABLE user_metadata( _id INTEGER PRIMARY KEY,user_id INTEGER UNIQUE,platform_key_generation_id INTEGER,user_serial_number INTEGER DEFAULT -1)");
        } else {
            sQLiteDatabase.execSQL("CREATE TABLE user_metadata( _id INTEGER PRIMARY KEY,user_id INTEGER UNIQUE,platform_key_generation_id INTEGER,user_serial_number INTEGER DEFAULT -1,bad_remote_guess_counter INTEGER DEFAULT 0)");
        }
        sQLiteDatabase.execSQL("CREATE TABLE recovery_service_metadata (_id INTEGER PRIMARY KEY,user_id INTEGER,uid INTEGER,snapshot_version INTEGER,should_create_snapshot INTEGER,active_root_of_trust TEXT,public_key BLOB,cert_path BLOB,cert_serial INTEGER,secret_types TEXT,counter_id INTEGER,server_params BLOB,UNIQUE(user_id,uid))");
        sQLiteDatabase.execSQL("CREATE TABLE root_of_trust (_id INTEGER PRIMARY KEY,user_id INTEGER,uid INTEGER,root_alias TEXT,cert_path BLOB,cert_serial INTEGER,UNIQUE(user_id,uid,root_alias))");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.e("RecoverableKeyStoreDbHp", "Recreating recoverablekeystore after unexpected version downgrade.");
        dropAllKnownTables(sQLiteDatabase);
        onCreate(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i < 2) {
            dropAllKnownTables(sQLiteDatabase);
            onCreate(sQLiteDatabase);
            return;
        }
        if (i < 3 && i2 >= 3) {
            upgradeDbForVersion3(sQLiteDatabase);
            i = 3;
        }
        if (i < 4 && i2 >= 4) {
            upgradeDbForVersion4(sQLiteDatabase);
            i = 4;
        }
        if (i < 5 && i2 >= 5) {
            upgradeDbForVersion5(sQLiteDatabase);
            i = 5;
        }
        if (i < 6 && i2 >= 6) {
            upgradeDbForVersion6(sQLiteDatabase);
            i = 6;
        }
        if (i < 7 && i2 >= 7) {
            upgradeDbForVersion7(sQLiteDatabase);
            i = 7;
        }
        if (i != i2) {
            Log.e("RecoverableKeyStoreDbHp", "Failed to update recoverablekeystore database to the most recent version");
        }
    }

    public final void dropAllKnownTables(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS keys");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS user_metadata");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS recovery_service_metadata");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS root_of_trust");
    }

    public final void upgradeDbForVersion3(SQLiteDatabase sQLiteDatabase) {
        addColumnToTable(sQLiteDatabase, "recovery_service_metadata", "cert_path", "BLOB", null);
        addColumnToTable(sQLiteDatabase, "recovery_service_metadata", "cert_serial", "INTEGER", null);
    }

    public final void upgradeDbForVersion4(SQLiteDatabase sQLiteDatabase) {
        Log.d("RecoverableKeyStoreDbHp", "Updating recoverable keystore database to version 4");
        sQLiteDatabase.execSQL("CREATE TABLE root_of_trust (_id INTEGER PRIMARY KEY,user_id INTEGER,uid INTEGER,root_alias TEXT,cert_path BLOB,cert_serial INTEGER,UNIQUE(user_id,uid,root_alias))");
        addColumnToTable(sQLiteDatabase, "recovery_service_metadata", "active_root_of_trust", "TEXT", null);
    }

    public final void upgradeDbForVersion5(SQLiteDatabase sQLiteDatabase) {
        Log.d("RecoverableKeyStoreDbHp", "Updating recoverable keystore database to version 5");
        addColumnToTable(sQLiteDatabase, "keys", "key_metadata", "BLOB", null);
    }

    public final void upgradeDbForVersion6(SQLiteDatabase sQLiteDatabase) {
        Log.d("RecoverableKeyStoreDbHp", "Updating recoverable keystore database to version 6");
        addColumnToTable(sQLiteDatabase, "user_metadata", "user_serial_number", "INTEGER DEFAULT -1", null);
    }

    public final void upgradeDbForVersion7(SQLiteDatabase sQLiteDatabase) {
        Log.d("RecoverableKeyStoreDbHp", "Updating recoverable keystore database to version 7");
        addColumnToTable(sQLiteDatabase, "user_metadata", "bad_remote_guess_counter", "INTEGER DEFAULT 0", null);
    }

    public static void addColumnToTable(SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, String str4) {
        Log.d("RecoverableKeyStoreDbHp", "Adding column " + str2 + " to " + str + ".");
        String str5 = "ALTER TABLE " + str + " ADD COLUMN " + str2 + " " + str3;
        if (str4 != null && !str4.isEmpty()) {
            str5 = str5 + " DEFAULT " + str4;
        }
        sQLiteDatabase.execSQL(str5 + ";");
    }
}
