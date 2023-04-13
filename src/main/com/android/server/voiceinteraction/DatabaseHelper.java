package com.android.server.voiceinteraction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.soundtrigger.SoundTrigger;
import android.text.TextUtils;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
/* loaded from: classes2.dex */
public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "sound_model.db", (SQLiteDatabase.CursorFactory) null, 7);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE sound_model(model_uuid TEXT,vendor_uuid TEXT,keyphrase_id INTEGER,type INTEGER,data BLOB,recognition_modes INTEGER,locale TEXT,hint_text TEXT,users TEXT,model_version INTEGER,PRIMARY KEY (keyphrase_id,locale,users))");
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x005a  */
    @Override // android.database.sqlite.SQLiteOpenHelper
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i < 4) {
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS sound_model");
            onCreate(sQLiteDatabase);
        } else if (i == 4) {
            Slog.d("SoundModelDBHelper", "Adding vendor UUID column");
            sQLiteDatabase.execSQL("ALTER TABLE sound_model ADD COLUMN vendor_uuid TEXT");
            i++;
        }
        if (i == 5) {
            Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT * FROM sound_model", null);
            ArrayList<SoundModelRecord> arrayList = new ArrayList();
            try {
                if (rawQuery.moveToFirst()) {
                    do {
                        try {
                            arrayList.add(new SoundModelRecord(5, rawQuery));
                        } catch (Exception e) {
                            Slog.e("SoundModelDBHelper", "Failed to extract V5 record", e);
                        }
                    } while (rawQuery.moveToNext());
                    rawQuery.close();
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS sound_model");
                    onCreate(sQLiteDatabase);
                    for (SoundModelRecord soundModelRecord : arrayList) {
                        if (soundModelRecord.ifViolatesV6PrimaryKeyIsFirstOfAnyDuplicates(arrayList)) {
                            try {
                                long writeToDatabase = soundModelRecord.writeToDatabase(6, sQLiteDatabase);
                                if (writeToDatabase == -1) {
                                    Slog.e("SoundModelDBHelper", "Database write failed " + soundModelRecord.modelUuid + ": " + writeToDatabase);
                                }
                            } catch (Exception e2) {
                                Slog.e("SoundModelDBHelper", "Failed to update V6 record " + soundModelRecord.modelUuid, e2);
                            }
                        }
                    }
                    i++;
                } else {
                    rawQuery.close();
                    sQLiteDatabase.execSQL("DROP TABLE IF EXISTS sound_model");
                    onCreate(sQLiteDatabase);
                    while (r7.hasNext()) {
                    }
                    i++;
                }
            } catch (Throwable th) {
                rawQuery.close();
                throw th;
            }
        }
        if (i == 6) {
            Slog.d("SoundModelDBHelper", "Adding model version column");
            sQLiteDatabase.execSQL("ALTER TABLE sound_model ADD COLUMN model_version INTEGER DEFAULT -1");
        }
    }

    public boolean updateKeyphraseSoundModel(SoundTrigger.KeyphraseSoundModel keyphraseSoundModel) {
        synchronized (this) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("model_uuid", keyphraseSoundModel.getUuid().toString());
            if (keyphraseSoundModel.getVendorUuid() != null) {
                contentValues.put("vendor_uuid", keyphraseSoundModel.getVendorUuid().toString());
            }
            contentValues.put("type", (Integer) 0);
            contentValues.put("data", keyphraseSoundModel.getData());
            contentValues.put("model_version", Integer.valueOf(keyphraseSoundModel.getVersion()));
            if (keyphraseSoundModel.getKeyphrases() == null || keyphraseSoundModel.getKeyphrases().length != 1) {
                return false;
            }
            contentValues.put("keyphrase_id", Integer.valueOf(keyphraseSoundModel.getKeyphrases()[0].getId()));
            contentValues.put("recognition_modes", Integer.valueOf(keyphraseSoundModel.getKeyphrases()[0].getRecognitionModes()));
            contentValues.put("users", getCommaSeparatedString(keyphraseSoundModel.getKeyphrases()[0].getUsers()));
            contentValues.put("locale", keyphraseSoundModel.getKeyphrases()[0].getLocale().toLanguageTag());
            contentValues.put("hint_text", keyphraseSoundModel.getKeyphrases()[0].getText());
            boolean z = writableDatabase.insertWithOnConflict("sound_model", null, contentValues, 5) != -1;
            writableDatabase.close();
            return z;
        }
    }

    public boolean deleteKeyphraseSoundModel(int i, int i2, String str) {
        String languageTag = Locale.forLanguageTag(str).toLanguageTag();
        synchronized (this) {
            SoundTrigger.KeyphraseSoundModel keyphraseSoundModel = getKeyphraseSoundModel(i, i2, languageTag);
            if (keyphraseSoundModel == null) {
                return false;
            }
            SQLiteDatabase writableDatabase = getWritableDatabase();
            StringBuilder sb = new StringBuilder();
            sb.append("model_uuid='");
            sb.append(keyphraseSoundModel.getUuid().toString());
            sb.append("'");
            boolean z = writableDatabase.delete("sound_model", sb.toString(), null) != 0;
            writableDatabase.close();
            return z;
        }
    }

    public SoundTrigger.KeyphraseSoundModel getKeyphraseSoundModel(int i, int i2, String str) {
        SoundTrigger.KeyphraseSoundModel validKeyphraseSoundModelForUser;
        String languageTag = Locale.forLanguageTag(str).toLanguageTag();
        synchronized (this) {
            validKeyphraseSoundModelForUser = getValidKeyphraseSoundModelForUser("SELECT  * FROM sound_model WHERE keyphrase_id= '" + i + "' AND locale='" + languageTag + "'", i2);
        }
        return validKeyphraseSoundModelForUser;
    }

    public SoundTrigger.KeyphraseSoundModel getKeyphraseSoundModel(String str, int i, String str2) {
        SoundTrigger.KeyphraseSoundModel validKeyphraseSoundModelForUser;
        String languageTag = Locale.forLanguageTag(str2).toLanguageTag();
        synchronized (this) {
            validKeyphraseSoundModelForUser = getValidKeyphraseSoundModelForUser("SELECT  * FROM sound_model WHERE hint_text= '" + str + "' AND locale='" + languageTag + "'", i);
        }
        return validKeyphraseSoundModelForUser;
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x00be, code lost:
        r13 = new android.hardware.soundtrigger.SoundTrigger.Keyphrase[]{new android.hardware.soundtrigger.SoundTrigger.Keyphrase(r8, r9, r10, r11, r12)};
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00c9, code lost:
        if (r5 == null) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00cb, code lost:
        r11 = java.util.UUID.fromString(r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00d1, code lost:
        r11 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00e3, code lost:
        return new android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel(java.util.UUID.fromString(r3), r11, r6, r13, r14);
     */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00bb A[LOOP:0: B:5:0x0011->B:31:0x00bb, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00ba A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final SoundTrigger.KeyphraseSoundModel getValidKeyphraseSoundModelForUser(String str, int i) {
        boolean z;
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String str2 = null;
        Cursor rawQuery = readableDatabase.rawQuery(str, null);
        try {
            if (rawQuery.moveToFirst()) {
                while (true) {
                    if (rawQuery.getInt(rawQuery.getColumnIndex("type")) == 0) {
                        String string = rawQuery.getString(rawQuery.getColumnIndex("model_uuid"));
                        if (string == null) {
                            Slog.w("SoundModelDBHelper", "Ignoring SoundModel since it doesn't specify an ID");
                        } else {
                            int columnIndex = rawQuery.getColumnIndex("vendor_uuid");
                            String string2 = columnIndex != -1 ? rawQuery.getString(columnIndex) : str2;
                            int i2 = rawQuery.getInt(rawQuery.getColumnIndex("keyphrase_id"));
                            byte[] blob = rawQuery.getBlob(rawQuery.getColumnIndex("data"));
                            int i3 = rawQuery.getInt(rawQuery.getColumnIndex("recognition_modes"));
                            int[] arrayForCommaSeparatedString = getArrayForCommaSeparatedString(rawQuery.getString(rawQuery.getColumnIndex("users")));
                            Locale forLanguageTag = Locale.forLanguageTag(rawQuery.getString(rawQuery.getColumnIndex("locale")));
                            String string3 = rawQuery.getString(rawQuery.getColumnIndex("hint_text"));
                            int i4 = rawQuery.getInt(rawQuery.getColumnIndex("model_version"));
                            if (arrayForCommaSeparatedString == null) {
                                Slog.w("SoundModelDBHelper", "Ignoring SoundModel since it doesn't specify users");
                            } else {
                                int length = arrayForCommaSeparatedString.length;
                                int i5 = 0;
                                while (true) {
                                    if (i5 >= length) {
                                        z = false;
                                        break;
                                    } else if (i == arrayForCommaSeparatedString[i5]) {
                                        z = true;
                                        break;
                                    } else {
                                        i5++;
                                    }
                                }
                                if (z) {
                                    break;
                                }
                                if (rawQuery.moveToNext()) {
                                    break;
                                }
                                str2 = null;
                            }
                        }
                    }
                    if (rawQuery.moveToNext()) {
                    }
                }
            }
            rawQuery.close();
            readableDatabase.close();
            return null;
        } finally {
            rawQuery.close();
            readableDatabase.close();
        }
    }

    public static String getCommaSeparatedString(int[] iArr) {
        if (iArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iArr.length; i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append(iArr[i]);
        }
        return sb.toString();
    }

    public static int[] getArrayForCommaSeparatedString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] split = str.split(",");
        int[] iArr = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            iArr[i] = Integer.parseInt(split[i]);
        }
        return iArr;
    }

    /* loaded from: classes2.dex */
    public static class SoundModelRecord {
        public final byte[] data;
        public final String hintText;
        public final int keyphraseId;
        public final String locale;
        public final String modelUuid;
        public final int recognitionModes;
        public final int type;
        public final String users;
        public final String vendorUuid;

        public SoundModelRecord(int i, Cursor cursor) {
            this.modelUuid = cursor.getString(cursor.getColumnIndex("model_uuid"));
            if (i >= 5) {
                this.vendorUuid = cursor.getString(cursor.getColumnIndex("vendor_uuid"));
            } else {
                this.vendorUuid = null;
            }
            this.keyphraseId = cursor.getInt(cursor.getColumnIndex("keyphrase_id"));
            this.type = cursor.getInt(cursor.getColumnIndex("type"));
            this.data = cursor.getBlob(cursor.getColumnIndex("data"));
            this.recognitionModes = cursor.getInt(cursor.getColumnIndex("recognition_modes"));
            this.locale = cursor.getString(cursor.getColumnIndex("locale"));
            this.hintText = cursor.getString(cursor.getColumnIndex("hint_text"));
            this.users = cursor.getString(cursor.getColumnIndex("users"));
        }

        public final boolean V6PrimaryKeyMatches(SoundModelRecord soundModelRecord) {
            return this.keyphraseId == soundModelRecord.keyphraseId && stringComparisonHelper(this.locale, soundModelRecord.locale) && stringComparisonHelper(this.users, soundModelRecord.users);
        }

        public boolean ifViolatesV6PrimaryKeyIsFirstOfAnyDuplicates(List<SoundModelRecord> list) {
            for (SoundModelRecord soundModelRecord : list) {
                if (this != soundModelRecord && V6PrimaryKeyMatches(soundModelRecord) && !Arrays.equals(this.data, soundModelRecord.data)) {
                    return false;
                }
            }
            Iterator<SoundModelRecord> it = list.iterator();
            while (it.hasNext()) {
                SoundModelRecord next = it.next();
                if (V6PrimaryKeyMatches(next)) {
                    return this == next;
                }
            }
            return true;
        }

        public long writeToDatabase(int i, SQLiteDatabase sQLiteDatabase) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("model_uuid", this.modelUuid);
            if (i >= 5) {
                contentValues.put("vendor_uuid", this.vendorUuid);
            }
            contentValues.put("keyphrase_id", Integer.valueOf(this.keyphraseId));
            contentValues.put("type", Integer.valueOf(this.type));
            contentValues.put("data", this.data);
            contentValues.put("recognition_modes", Integer.valueOf(this.recognitionModes));
            contentValues.put("locale", this.locale);
            contentValues.put("hint_text", this.hintText);
            contentValues.put("users", this.users);
            return sQLiteDatabase.insertWithOnConflict("sound_model", null, contentValues, 5);
        }

        public static boolean stringComparisonHelper(String str, String str2) {
            if (str != null) {
                return str.equals(str2);
            }
            return str == str2;
        }
    }

    public void dump(PrintWriter printWriter) {
        synchronized (this) {
            SQLiteDatabase readableDatabase = getReadableDatabase();
            Cursor rawQuery = readableDatabase.rawQuery("SELECT  * FROM sound_model", null);
            printWriter.println("  Enrolled KeyphraseSoundModels:");
            if (rawQuery.moveToFirst()) {
                String[] columnNames = rawQuery.getColumnNames();
                do {
                    for (String str : columnNames) {
                        int columnIndex = rawQuery.getColumnIndex(str);
                        int type = rawQuery.getType(columnIndex);
                        if (type == 0) {
                            printWriter.printf("    %s: null\n", str);
                        } else if (type == 1) {
                            printWriter.printf("    %s: %d\n", str, Integer.valueOf(rawQuery.getInt(columnIndex)));
                        } else if (type == 2) {
                            printWriter.printf("    %s: %f\n", str, Float.valueOf(rawQuery.getFloat(columnIndex)));
                        } else if (type == 3) {
                            printWriter.printf("    %s: %s\n", str, rawQuery.getString(columnIndex));
                        } else if (type == 4) {
                            printWriter.printf("    %s: data blob\n", str);
                        }
                    }
                    printWriter.println();
                } while (rawQuery.moveToNext());
                rawQuery.close();
                readableDatabase.close();
            } else {
                rawQuery.close();
                readableDatabase.close();
            }
        }
    }
}
