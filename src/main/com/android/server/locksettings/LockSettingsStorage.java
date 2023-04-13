package com.android.server.locksettings;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.UserInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.LocalServices;
import com.android.server.PersistentDataBlockManagerInternal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes2.dex */
public class LockSettingsStorage {
    public final Context mContext;
    public final DatabaseHelper mOpenHelper;
    public PersistentDataBlockManagerInternal mPersistentDataBlockManagerInternal;
    public static final String[] COLUMNS_FOR_QUERY = {"value"};
    public static final String[] COLUMNS_FOR_PREFETCH = {"name", "value"};
    public static final Object DEFAULT = new Object();
    public static final String[] SETTINGS_TO_BACKUP = {"lock_screen_owner_info_enabled", "lock_screen_owner_info", "lock_pattern_visible_pattern", "lockscreen.power_button_instantly_locks"};
    public final Cache mCache = new Cache();
    public final Object mFileWriteLock = new Object();

    /* loaded from: classes2.dex */
    public interface Callback {
        void initialize(SQLiteDatabase sQLiteDatabase);
    }

    public LockSettingsStorage(Context context) {
        this.mContext = context;
        this.mOpenHelper = new DatabaseHelper(context);
    }

    public void setDatabaseOnCreateCallback(Callback callback) {
        this.mOpenHelper.setCallback(callback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void writeKeyValue(String str, String str2, int i) {
        writeKeyValue(this.mOpenHelper.getWritableDatabase(), str, str2, i);
    }

    @VisibleForTesting
    public void writeKeyValue(SQLiteDatabase sQLiteDatabase, String str, String str2, int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", str);
        contentValues.put("user", Integer.valueOf(i));
        contentValues.put("value", str2);
        sQLiteDatabase.beginTransaction();
        try {
            sQLiteDatabase.delete("locksettings", "name=? AND user=?", new String[]{str, Integer.toString(i)});
            sQLiteDatabase.insert("locksettings", null, contentValues);
            sQLiteDatabase.setTransactionSuccessful();
            this.mCache.putKeyValue(str, str2, i);
        } finally {
            sQLiteDatabase.endTransaction();
        }
    }

    @VisibleForTesting
    public String readKeyValue(String str, String str2, int i) {
        String str3;
        synchronized (this.mCache) {
            if (this.mCache.hasKeyValue(str, i)) {
                return this.mCache.peekKeyValue(str, str2, i);
            }
            int version = this.mCache.getVersion();
            Object obj = DEFAULT;
            Cursor query = this.mOpenHelper.getReadableDatabase().query("locksettings", COLUMNS_FOR_QUERY, "user=? AND name=?", new String[]{Integer.toString(i), str}, null, null, null);
            if (query != null) {
                str3 = query.moveToFirst() ? query.getString(0) : obj;
                query.close();
            } else {
                str3 = obj;
            }
            this.mCache.putKeyValueIfUnchanged(str, str3, i, version);
            return str3 == obj ? str2 : (String) str3;
        }
    }

    @VisibleForTesting
    public boolean isKeyValueCached(String str, int i) {
        return this.mCache.hasKeyValue(str, i);
    }

    @VisibleForTesting
    public boolean isUserPrefetched(int i) {
        return this.mCache.isFetched(i);
    }

    @VisibleForTesting
    public void removeKey(String str, int i) {
        removeKey(this.mOpenHelper.getWritableDatabase(), str, i);
    }

    public final void removeKey(SQLiteDatabase sQLiteDatabase, String str, int i) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", str);
        contentValues.put("user", Integer.valueOf(i));
        sQLiteDatabase.beginTransaction();
        try {
            sQLiteDatabase.delete("locksettings", "name=? AND user=?", new String[]{str, Integer.toString(i)});
            sQLiteDatabase.setTransactionSuccessful();
            this.mCache.removeKey(str, i);
        } finally {
            sQLiteDatabase.endTransaction();
        }
    }

    public void prefetchUser(int i) {
        synchronized (this.mCache) {
            if (this.mCache.isFetched(i)) {
                return;
            }
            this.mCache.setFetched(i);
            int version = this.mCache.getVersion();
            Cursor query = this.mOpenHelper.getReadableDatabase().query("locksettings", COLUMNS_FOR_PREFETCH, "user=?", new String[]{Integer.toString(i)}, null, null, null);
            if (query != null) {
                while (query.moveToNext()) {
                    this.mCache.putKeyValueIfUnchanged(query.getString(0), query.getString(1), i, version);
                }
                query.close();
            }
        }
    }

    public void removeChildProfileLock(int i) {
        deleteFile(getChildProfileLockFile(i));
    }

    public void writeChildProfileLock(int i, byte[] bArr) {
        writeFile(getChildProfileLockFile(i), bArr);
    }

    public byte[] readChildProfileLock(int i) {
        return readFile(getChildProfileLockFile(i));
    }

    public boolean hasChildProfileLock(int i) {
        return hasFile(getChildProfileLockFile(i));
    }

    public void writeRebootEscrow(int i, byte[] bArr) {
        writeFile(getRebootEscrowFile(i), bArr);
    }

    public byte[] readRebootEscrow(int i) {
        return readFile(getRebootEscrowFile(i));
    }

    public boolean hasRebootEscrow(int i) {
        return hasFile(getRebootEscrowFile(i));
    }

    public void removeRebootEscrow(int i) {
        deleteFile(getRebootEscrowFile(i));
    }

    public void writeRebootEscrowServerBlob(byte[] bArr) {
        writeFile(getRebootEscrowServerBlobFile(), bArr);
    }

    public byte[] readRebootEscrowServerBlob() {
        return readFile(getRebootEscrowServerBlobFile());
    }

    public void removeRebootEscrowServerBlob() {
        deleteFile(getRebootEscrowServerBlobFile());
    }

    public final boolean hasFile(File file) {
        byte[] readFile = readFile(file);
        return readFile != null && readFile.length > 0;
    }

    public final byte[] readFile(File file) {
        synchronized (this.mCache) {
            if (this.mCache.hasFile(file)) {
                return this.mCache.peekFile(file);
            }
            int version = this.mCache.getVersion();
            byte[] bArr = null;
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                try {
                    int length = (int) randomAccessFile.length();
                    bArr = new byte[length];
                    randomAccessFile.readFully(bArr, 0, length);
                    randomAccessFile.close();
                    randomAccessFile.close();
                } catch (Throwable th) {
                    try {
                        randomAccessFile.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (FileNotFoundException unused) {
            } catch (IOException e) {
                Slog.e("LockSettingsStorage", "Cannot read file " + e);
            }
            this.mCache.putFileIfUnchanged(file, bArr, version);
            return bArr;
        }
    }

    public final void fsyncDirectory(File file) {
        try {
            FileChannel open = FileChannel.open(file.toPath(), StandardOpenOption.READ);
            open.force(true);
            open.close();
        } catch (IOException e) {
            Slog.e("LockSettingsStorage", "Error syncing directory: " + file, e);
        }
    }

    public final void writeFile(File file, byte[] bArr) {
        writeFile(file, bArr, true);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003d A[Catch: all -> 0x004f, TryCatch #4 {, blocks: (B:4:0x0003, B:8:0x0013, B:21:0x003d, B:22:0x0044, B:23:0x0049, B:25:0x004b, B:26:0x004e, B:19:0x0038), top: B:33:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void writeFile(File file, byte[] bArr, boolean z) {
        FileOutputStream fileOutputStream;
        IOException e;
        synchronized (this.mFileWriteLock) {
            AtomicFile atomicFile = new AtomicFile(file);
            FileOutputStream fileOutputStream2 = null;
            try {
                fileOutputStream = atomicFile.startWrite();
                try {
                    try {
                        fileOutputStream.write(bArr);
                        atomicFile.finishWrite(fileOutputStream);
                        atomicFile.failWrite(null);
                    } catch (IOException e2) {
                        e = e2;
                        Slog.e("LockSettingsStorage", "Error writing file " + file, e);
                        atomicFile.failWrite(fileOutputStream);
                        if (z) {
                        }
                        this.mCache.putFile(file, bArr);
                    }
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream2 = fileOutputStream;
                    atomicFile.failWrite(fileOutputStream2);
                    throw th;
                }
            } catch (IOException e3) {
                fileOutputStream = null;
                e = e3;
            } catch (Throwable th2) {
                th = th2;
                atomicFile.failWrite(fileOutputStream2);
                throw th;
            }
            if (z) {
                fsyncDirectory(file.getParentFile());
            }
            this.mCache.putFile(file, bArr);
        }
    }

    public final void deleteFile(File file) {
        synchronized (this.mFileWriteLock) {
            if (file.exists()) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
                    try {
                        randomAccessFile.write(new byte[(int) randomAccessFile.length()]);
                        randomAccessFile.close();
                    } catch (Throwable th) {
                        try {
                            randomAccessFile.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                } catch (Exception e) {
                    Slog.w("LockSettingsStorage", "Failed to zeroize " + file, e);
                }
            }
            new AtomicFile(file).delete();
            this.mCache.putFile(file, null);
        }
    }

    @VisibleForTesting
    public File getChildProfileLockFile(int i) {
        return getLockCredentialFileForUser(i, "gatekeeper.profile.key");
    }

    @VisibleForTesting
    public File getRebootEscrowFile(int i) {
        return getLockCredentialFileForUser(i, "reboot.escrow.key");
    }

    @VisibleForTesting
    public File getRebootEscrowServerBlobFile() {
        return getLockCredentialFileForUser(0, "reboot.escrow.server.blob.key");
    }

    public final File getLockCredentialFileForUser(int i, String str) {
        if (i == 0) {
            return new File(Environment.getDataSystemDirectory(), str);
        }
        return new File(Environment.getUserSystemDirectory(i), str);
    }

    public void writeSyntheticPasswordState(int i, long j, String str, byte[] bArr) {
        ensureSyntheticPasswordDirectoryForUser(i);
        writeFile(getSyntheticPasswordStateFileForUser(i, j, str), bArr, false);
    }

    public byte[] readSyntheticPasswordState(int i, long j, String str) {
        return readFile(getSyntheticPasswordStateFileForUser(i, j, str));
    }

    public void deleteSyntheticPasswordState(int i, long j, String str) {
        deleteFile(getSyntheticPasswordStateFileForUser(i, j, str));
    }

    public void syncSyntheticPasswordState(int i) {
        fsyncDirectory(getSyntheticPasswordDirectoryForUser(i));
    }

    public Map<Integer, List<Long>> listSyntheticPasswordProtectorsForAllUsers(String str) {
        ArrayMap arrayMap = new ArrayMap();
        for (UserInfo userInfo : UserManager.get(this.mContext).getUsers()) {
            arrayMap.put(Integer.valueOf(userInfo.id), listSyntheticPasswordProtectorsForUser(str, userInfo.id));
        }
        return arrayMap;
    }

    public List<Long> listSyntheticPasswordProtectorsForUser(String str, int i) {
        File syntheticPasswordDirectoryForUser = getSyntheticPasswordDirectoryForUser(i);
        ArrayList arrayList = new ArrayList();
        File[] listFiles = syntheticPasswordDirectoryForUser.listFiles();
        if (listFiles == null) {
            return arrayList;
        }
        for (File file : listFiles) {
            String[] split = file.getName().split("\\.");
            if (split.length == 2 && split[1].equals(str)) {
                try {
                    arrayList.add(Long.valueOf(Long.parseUnsignedLong(split[0], 16)));
                } catch (NumberFormatException unused) {
                    Slog.e("LockSettingsStorage", "Failed to parse protector ID " + split[0]);
                }
            }
        }
        return arrayList;
    }

    @VisibleForTesting
    public File getSyntheticPasswordDirectoryForUser(int i) {
        return new File(Environment.getDataSystemDeDirectory(i), "spblob/");
    }

    public final void ensureSyntheticPasswordDirectoryForUser(int i) {
        File syntheticPasswordDirectoryForUser = getSyntheticPasswordDirectoryForUser(i);
        if (syntheticPasswordDirectoryForUser.exists()) {
            return;
        }
        syntheticPasswordDirectoryForUser.mkdir();
    }

    public final File getSyntheticPasswordStateFileForUser(int i, long j, String str) {
        return new File(getSyntheticPasswordDirectoryForUser(i), TextUtils.formatSimple("%016x.%s", new Object[]{Long.valueOf(j), str}));
    }

    public void removeUser(int i) {
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        if (((UserManager) this.mContext.getSystemService("user")).getProfileParent(i) == null) {
            deleteFile(getRebootEscrowFile(i));
        } else {
            removeChildProfileLock(i);
        }
        File syntheticPasswordDirectoryForUser = getSyntheticPasswordDirectoryForUser(i);
        try {
            writableDatabase.beginTransaction();
            writableDatabase.delete("locksettings", "user='" + i + "'", null);
            writableDatabase.setTransactionSuccessful();
            this.mCache.removeUser(i);
            this.mCache.purgePath(syntheticPasswordDirectoryForUser);
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void setBoolean(String str, boolean z, int i) {
        setString(str, z ? "1" : "0", i);
    }

    public void setLong(String str, long j, int i) {
        setString(str, Long.toString(j), i);
    }

    public void setInt(String str, int i, int i2) {
        setString(str, Integer.toString(i), i2);
    }

    public void setString(String str, String str2, int i) {
        Preconditions.checkArgument(i != -9999, "cannot store lock settings for FRP user");
        writeKeyValue(str, str2, i);
        if (ArrayUtils.contains(SETTINGS_TO_BACKUP, str)) {
            BackupManager.dataChanged("com.android.providers.settings");
        }
    }

    public boolean getBoolean(String str, boolean z, int i) {
        String string = getString(str, null, i);
        return TextUtils.isEmpty(string) ? z : string.equals("1") || string.equals("true");
    }

    public long getLong(String str, long j, int i) {
        String string = getString(str, null, i);
        return TextUtils.isEmpty(string) ? j : Long.parseLong(string);
    }

    public int getInt(String str, int i, int i2) {
        String string = getString(str, null, i2);
        return TextUtils.isEmpty(string) ? i : Integer.parseInt(string);
    }

    public String getString(String str, String str2, int i) {
        if (i == -9999) {
            return null;
        }
        return readKeyValue(str, str2, i);
    }

    @VisibleForTesting
    public void closeDatabase() {
        this.mOpenHelper.close();
    }

    @VisibleForTesting
    public void clearCache() {
        this.mCache.clear();
    }

    public PersistentDataBlockManagerInternal getPersistentDataBlockManager() {
        if (this.mPersistentDataBlockManagerInternal == null) {
            this.mPersistentDataBlockManagerInternal = (PersistentDataBlockManagerInternal) LocalServices.getService(PersistentDataBlockManagerInternal.class);
        }
        return this.mPersistentDataBlockManagerInternal;
    }

    public void writePersistentDataBlock(int i, int i2, int i3, byte[] bArr) {
        PersistentDataBlockManagerInternal persistentDataBlockManager = getPersistentDataBlockManager();
        if (persistentDataBlockManager == null) {
            return;
        }
        persistentDataBlockManager.setFrpCredentialHandle(PersistentData.toBytes(i, i2, i3, bArr));
    }

    public PersistentData readPersistentDataBlock() {
        PersistentDataBlockManagerInternal persistentDataBlockManager = getPersistentDataBlockManager();
        if (persistentDataBlockManager == null) {
            return PersistentData.NONE;
        }
        try {
            return PersistentData.fromBytes(persistentDataBlockManager.getFrpCredentialHandle());
        } catch (IllegalStateException e) {
            Slog.e("LockSettingsStorage", "Error reading persistent data block", e);
            return PersistentData.NONE;
        }
    }

    /* loaded from: classes2.dex */
    public static class PersistentData {
        public static final PersistentData NONE = new PersistentData(0, -10000, 0, null);
        public final byte[] payload;
        public final int qualityForUi;
        public final int type;
        public final int userId;

        public PersistentData(int i, int i2, int i3, byte[] bArr) {
            this.type = i;
            this.userId = i2;
            this.qualityForUi = i3;
            this.payload = bArr;
        }

        public static PersistentData fromBytes(byte[] bArr) {
            if (bArr == null || bArr.length == 0) {
                return NONE;
            }
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
            try {
                byte readByte = dataInputStream.readByte();
                if (readByte == 1) {
                    int readInt = dataInputStream.readInt();
                    int readInt2 = dataInputStream.readInt();
                    int length = bArr.length - 10;
                    byte[] bArr2 = new byte[length];
                    System.arraycopy(bArr, 10, bArr2, 0, length);
                    return new PersistentData(dataInputStream.readByte() & 255, readInt, readInt2, bArr2);
                }
                Slog.wtf("LockSettingsStorage", "Unknown PersistentData version code: " + ((int) readByte));
                return NONE;
            } catch (IOException e) {
                Slog.wtf("LockSettingsStorage", "Could not parse PersistentData", e);
                return NONE;
            }
        }

        public static byte[] toBytes(int i, int i2, int i3, byte[] bArr) {
            if (i == 0) {
                Preconditions.checkArgument(bArr == null, "TYPE_NONE must have empty payload");
                return null;
            }
            if (bArr != null && bArr.length > 0) {
                r0 = true;
            }
            Preconditions.checkArgument(r0, "empty payload must only be used with TYPE_NONE");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bArr.length + 10);
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            try {
                dataOutputStream.writeByte(1);
                dataOutputStream.writeByte(i);
                dataOutputStream.writeInt(i2);
                dataOutputStream.writeInt(i3);
                dataOutputStream.write(bArr);
                return byteArrayOutputStream.toByteArray();
            } catch (IOException unused) {
                throw new IllegalStateException("ByteArrayOutputStream cannot throw IOException");
            }
        }
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        for (UserInfo userInfo : UserManager.get(this.mContext).getUsers()) {
            File syntheticPasswordDirectoryForUser = getSyntheticPasswordDirectoryForUser(userInfo.id);
            indentingPrintWriter.println(TextUtils.formatSimple("User %d [%s]:", new Object[]{Integer.valueOf(userInfo.id), syntheticPasswordDirectoryForUser}));
            indentingPrintWriter.increaseIndent();
            File[] listFiles = syntheticPasswordDirectoryForUser.listFiles();
            if (listFiles != null) {
                Arrays.sort(listFiles);
                for (File file : listFiles) {
                    indentingPrintWriter.println(TextUtils.formatSimple("%6d %s %s", new Object[]{Long.valueOf(file.length()), LockSettingsService.timestampToString(file.lastModified()), file.getName()}));
                }
            } else {
                indentingPrintWriter.println("[Not found]");
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    /* loaded from: classes2.dex */
    public static class DatabaseHelper extends SQLiteOpenHelper {
        public Callback mCallback;

        public DatabaseHelper(Context context) {
            super(context, "locksettings.db", (SQLiteDatabase.CursorFactory) null, 2);
            setWriteAheadLoggingEnabled(false);
            setIdleConnectionTimeout(30000L);
        }

        public void setCallback(Callback callback) {
            this.mCallback = callback;
        }

        public final void createTable(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE locksettings (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,user INTEGER,value TEXT);");
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            createTable(sQLiteDatabase);
            Callback callback = this.mCallback;
            if (callback != null) {
                callback.initialize(sQLiteDatabase);
            }
        }

        @Override // android.database.sqlite.SQLiteOpenHelper
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            if (i == 1) {
                i = 2;
            }
            if (i != 2) {
                Slog.w("LockSettingsDB", "Failed to upgrade database!");
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class Cache {
        public final ArrayMap<CacheKey, Object> mCache;
        public final CacheKey mCacheKey;
        public int mVersion;

        public Cache() {
            this.mCache = new ArrayMap<>();
            this.mCacheKey = new CacheKey();
            this.mVersion = 0;
        }

        public String peekKeyValue(String str, String str2, int i) {
            Object peek = peek(0, str, i);
            return peek == LockSettingsStorage.DEFAULT ? str2 : (String) peek;
        }

        public boolean hasKeyValue(String str, int i) {
            return contains(0, str, i);
        }

        public void putKeyValue(String str, String str2, int i) {
            put(0, str, str2, i);
        }

        public void putKeyValueIfUnchanged(String str, Object obj, int i, int i2) {
            putIfUnchanged(0, str, obj, i, i2);
        }

        public void removeKey(String str, int i) {
            remove(0, str, i);
        }

        public byte[] peekFile(File file) {
            return copyOf((byte[]) peek(1, file.toString(), -1));
        }

        public boolean hasFile(File file) {
            return contains(1, file.toString(), -1);
        }

        public void putFile(File file, byte[] bArr) {
            put(1, file.toString(), copyOf(bArr), -1);
        }

        public void putFileIfUnchanged(File file, byte[] bArr, int i) {
            putIfUnchanged(1, file.toString(), copyOf(bArr), -1, i);
        }

        public void setFetched(int i) {
            put(2, "", "true", i);
        }

        public boolean isFetched(int i) {
            return contains(2, "", i);
        }

        public final synchronized void remove(int i, String str, int i2) {
            this.mCache.remove(this.mCacheKey.set(i, str, i2));
            this.mVersion++;
        }

        public final synchronized void put(int i, String str, Object obj, int i2) {
            this.mCache.put(new CacheKey().set(i, str, i2), obj);
            this.mVersion++;
        }

        public final synchronized void putIfUnchanged(int i, String str, Object obj, int i2, int i3) {
            if (!contains(i, str, i2) && this.mVersion == i3) {
                this.mCache.put(new CacheKey().set(i, str, i2), obj);
            }
        }

        public final synchronized boolean contains(int i, String str, int i2) {
            return this.mCache.containsKey(this.mCacheKey.set(i, str, i2));
        }

        public final synchronized Object peek(int i, String str, int i2) {
            return this.mCache.get(this.mCacheKey.set(i, str, i2));
        }

        public final synchronized int getVersion() {
            return this.mVersion;
        }

        public synchronized void removeUser(int i) {
            for (int size = this.mCache.size() - 1; size >= 0; size--) {
                if (this.mCache.keyAt(size).userId == i) {
                    this.mCache.removeAt(size);
                }
            }
            this.mVersion++;
        }

        public final byte[] copyOf(byte[] bArr) {
            if (bArr != null) {
                return Arrays.copyOf(bArr, bArr.length);
            }
            return null;
        }

        public synchronized void purgePath(File file) {
            String file2 = file.toString();
            for (int size = this.mCache.size() - 1; size >= 0; size--) {
                CacheKey keyAt = this.mCache.keyAt(size);
                if (keyAt.type == 1 && keyAt.key.startsWith(file2)) {
                    this.mCache.removeAt(size);
                }
            }
            this.mVersion++;
        }

        public synchronized void clear() {
            this.mCache.clear();
            this.mVersion++;
        }

        /* loaded from: classes2.dex */
        public static final class CacheKey {
            public String key;
            public int type;
            public int userId;

            public CacheKey() {
            }

            public CacheKey set(int i, String str, int i2) {
                this.type = i;
                this.key = str;
                this.userId = i2;
                return this;
            }

            public boolean equals(Object obj) {
                if (obj instanceof CacheKey) {
                    CacheKey cacheKey = (CacheKey) obj;
                    return this.userId == cacheKey.userId && this.type == cacheKey.type && Objects.equals(this.key, cacheKey.key);
                }
                return false;
            }

            public int hashCode() {
                return (((Objects.hashCode(this.key) * 31) + this.userId) * 31) + this.type;
            }
        }
    }
}
