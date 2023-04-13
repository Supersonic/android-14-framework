package com.android.server.locksettings.recoverablekeystore.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.server.locksettings.recoverablekeystore.TestOnlyInsecureCertificateHelper;
import com.android.server.locksettings.recoverablekeystore.WrappedKey;
import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.IntConsumer;
/* loaded from: classes2.dex */
public class RecoverableKeyStoreDb {
    public final RecoverableKeyStoreDbHelper mKeyStoreDbHelper;
    public final TestOnlyInsecureCertificateHelper mTestOnlyInsecureCertificateHelper = new TestOnlyInsecureCertificateHelper();

    public static RecoverableKeyStoreDb newInstance(Context context) {
        RecoverableKeyStoreDbHelper recoverableKeyStoreDbHelper = new RecoverableKeyStoreDbHelper(context);
        recoverableKeyStoreDbHelper.setWriteAheadLoggingEnabled(true);
        recoverableKeyStoreDbHelper.setIdleConnectionTimeout(30L);
        return new RecoverableKeyStoreDb(recoverableKeyStoreDbHelper);
    }

    public static RecoverableKeyStoreDb newInstance(Context context, int i) {
        RecoverableKeyStoreDbHelper recoverableKeyStoreDbHelper = new RecoverableKeyStoreDbHelper(context, i);
        recoverableKeyStoreDbHelper.setWriteAheadLoggingEnabled(true);
        recoverableKeyStoreDbHelper.setIdleConnectionTimeout(30L);
        return new RecoverableKeyStoreDb(recoverableKeyStoreDbHelper);
    }

    public RecoverableKeyStoreDb(RecoverableKeyStoreDbHelper recoverableKeyStoreDbHelper) {
        this.mKeyStoreDbHelper = recoverableKeyStoreDbHelper;
    }

    public long insertKey(int i, int i2, String str, WrappedKey wrappedKey) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", Integer.valueOf(i));
        contentValues.put("uid", Integer.valueOf(i2));
        contentValues.put("alias", str);
        contentValues.put("nonce", wrappedKey.getNonce());
        contentValues.put("wrapped_key", wrappedKey.getKeyMaterial());
        contentValues.put("last_synced_at", (Integer) (-1));
        contentValues.put("platform_key_generation_id", Integer.valueOf(wrappedKey.getPlatformKeyGenerationId()));
        contentValues.put("recovery_status", Integer.valueOf(wrappedKey.getRecoveryStatus()));
        byte[] keyMetadata = wrappedKey.getKeyMetadata();
        if (keyMetadata == null) {
            contentValues.putNull("key_metadata");
        } else {
            contentValues.put("key_metadata", keyMetadata);
        }
        return writableDatabase.replace("keys", null, contentValues);
    }

    public boolean removeKey(int i, String str) {
        return this.mKeyStoreDbHelper.getWritableDatabase().delete("keys", "uid = ? AND alias = ?", new String[]{Integer.toString(i), str}) > 0;
    }

    public Map<String, Integer> getStatusForAllKeys(int i) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("keys", new String[]{"_id", "alias", "recovery_status"}, "uid = ?", new String[]{Integer.toString(i)}, null, null, null);
        try {
            HashMap hashMap = new HashMap();
            while (query.moveToNext()) {
                hashMap.put(query.getString(query.getColumnIndexOrThrow("alias")), Integer.valueOf(query.getInt(query.getColumnIndexOrThrow("recovery_status"))));
            }
            query.close();
            return hashMap;
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

    public int setRecoveryStatus(int i, String str, int i2) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("recovery_status", Integer.valueOf(i2));
        return writableDatabase.update("keys", contentValues, "uid = ? AND alias = ?", new String[]{String.valueOf(i), str});
    }

    public Map<String, WrappedKey> getAllKeys(int i, int i2, int i3) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("keys", new String[]{"_id", "nonce", "wrapped_key", "alias", "recovery_status", "key_metadata"}, "user_id = ? AND uid = ? AND platform_key_generation_id = ?", new String[]{Integer.toString(i), Integer.toString(i2), Integer.toString(i3)}, null, null, null);
        try {
            HashMap hashMap = new HashMap();
            while (query.moveToNext()) {
                byte[] blob = query.getBlob(query.getColumnIndexOrThrow("nonce"));
                byte[] blob2 = query.getBlob(query.getColumnIndexOrThrow("wrapped_key"));
                String string = query.getString(query.getColumnIndexOrThrow("alias"));
                int i4 = query.getInt(query.getColumnIndexOrThrow("recovery_status"));
                int columnIndexOrThrow = query.getColumnIndexOrThrow("key_metadata");
                hashMap.put(string, new WrappedKey(blob, blob2, query.isNull(columnIndexOrThrow) ? null : query.getBlob(columnIndexOrThrow), i3, i4));
            }
            query.close();
            return hashMap;
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

    public long setPlatformKeyGenerationId(int i, int i2) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", Integer.valueOf(i));
        contentValues.put("platform_key_generation_id", Integer.valueOf(i2));
        ensureUserMetadataEntryExists(i);
        invalidateKeysForUser(i);
        return writableDatabase.update("user_metadata", contentValues, "user_id = ?", new String[]{String.valueOf(i)});
    }

    public Map<Integer, Long> getUserSerialNumbers() {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("user_metadata", new String[]{"user_id", "user_serial_number"}, null, new String[0], null, null, null);
        try {
            ArrayMap arrayMap = new ArrayMap();
            while (query.moveToNext()) {
                arrayMap.put(Integer.valueOf(query.getInt(query.getColumnIndexOrThrow("user_id"))), Long.valueOf(query.getLong(query.getColumnIndexOrThrow("user_serial_number"))));
            }
            query.close();
            return arrayMap;
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

    public long setUserSerialNumber(int i, long j) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", Integer.valueOf(i));
        contentValues.put("user_serial_number", Long.valueOf(j));
        ensureUserMetadataEntryExists(i);
        return writableDatabase.update("user_metadata", contentValues, "user_id = ?", new String[]{String.valueOf(i)});
    }

    public long setBadRemoteGuessCounter(int i, int i2) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", Integer.valueOf(i));
        contentValues.put("bad_remote_guess_counter", Integer.valueOf(i2));
        ensureUserMetadataEntryExists(i);
        return writableDatabase.update("user_metadata", contentValues, "user_id = ?", new String[]{String.valueOf(i)});
    }

    public int getBadRemoteGuessCounter(int i) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("user_metadata", new String[]{"bad_remote_guess_counter"}, "user_id = ?", new String[]{Integer.toString(i)}, null, null, null);
        try {
            if (query.getCount() != 0) {
                query.moveToFirst();
                int i2 = query.getInt(query.getColumnIndexOrThrow("bad_remote_guess_counter"));
                query.close();
                return i2;
            }
            query.close();
            return 0;
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

    public void invalidateKeysForUser(int i) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("recovery_status", (Integer) 3);
        writableDatabase.update("keys", contentValues, "user_id = ?", new String[]{String.valueOf(i)});
    }

    public void invalidateKeysForUserIdOnCustomScreenLock(int i) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("recovery_status", (Integer) 3);
        writableDatabase.update("keys", contentValues, "user_id = ?", new String[]{String.valueOf(i)});
    }

    public int getPlatformKeyGenerationId(int i) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("user_metadata", new String[]{"platform_key_generation_id"}, "user_id = ?", new String[]{Integer.toString(i)}, null, null, null);
        try {
            if (query.getCount() != 0) {
                query.moveToFirst();
                int i2 = query.getInt(query.getColumnIndexOrThrow("platform_key_generation_id"));
                query.close();
                return i2;
            }
            query.close();
            return -1;
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

    public Long getRecoveryServiceCertSerial(int i, int i2, String str) {
        return getLong(i, i2, str, "cert_serial");
    }

    public long setRecoveryServiceCertSerial(int i, int i2, String str, long j) {
        return setLong(i, i2, str, "cert_serial", j);
    }

    public CertPath getRecoveryServiceCertPath(int i, int i2, String str) {
        byte[] bytes = getBytes(i, i2, str, "cert_path");
        if (bytes == null) {
            return null;
        }
        try {
            return decodeCertPath(bytes);
        } catch (CertificateException e) {
            Log.wtf("RecoverableKeyStoreDb", String.format(Locale.US, "Recovery service CertPath entry cannot be decoded for userId=%d uid=%d.", Integer.valueOf(i), Integer.valueOf(i2)), e);
            return null;
        }
    }

    public long setRecoveryServiceCertPath(int i, int i2, String str, CertPath certPath) throws CertificateEncodingException {
        if (certPath.getCertificates().size() == 0) {
            throw new CertificateEncodingException("No certificate contained in the cert path.");
        }
        return setBytes(i, i2, str, "cert_path", certPath.getEncoded("PkiPath"));
    }

    public List<Integer> getRecoveryAgents(int i) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"uid"}, "user_id = ?", new String[]{Integer.toString(i)}, null, null, null);
        try {
            ArrayList arrayList = new ArrayList(query.getCount());
            while (query.moveToNext()) {
                arrayList.add(Integer.valueOf(query.getInt(query.getColumnIndexOrThrow("uid"))));
            }
            query.close();
            return arrayList;
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

    public PublicKey getRecoveryServicePublicKey(int i, int i2) {
        byte[] bytes = getBytes(i, i2, "public_key");
        if (bytes == null) {
            return null;
        }
        try {
            return decodeX509Key(bytes);
        } catch (InvalidKeySpecException unused) {
            Log.wtf("RecoverableKeyStoreDb", String.format(Locale.US, "Recovery service public key entry cannot be decoded for userId=%d uid=%d.", Integer.valueOf(i), Integer.valueOf(i2)));
            return null;
        }
    }

    public long setRecoverySecretTypes(int i, int i2, int[] iArr) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        final StringJoiner stringJoiner = new StringJoiner(",");
        Arrays.stream(iArr).forEach(new IntConsumer() { // from class: com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb$$ExternalSyntheticLambda0
            @Override // java.util.function.IntConsumer
            public final void accept(int i3) {
                RecoverableKeyStoreDb.lambda$setRecoverySecretTypes$0(stringJoiner, i3);
            }
        });
        contentValues.put("secret_types", stringJoiner.toString());
        ensureRecoveryServiceMetadataEntryExists(i, i2);
        return writableDatabase.update("recovery_service_metadata", contentValues, "user_id = ? AND uid = ?", new String[]{String.valueOf(i), String.valueOf(i2)});
    }

    public static /* synthetic */ void lambda$setRecoverySecretTypes$0(StringJoiner stringJoiner, int i) {
        stringJoiner.add(Integer.toString(i));
    }

    public int[] getRecoverySecretTypes(int i, int i2) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"_id", "user_id", "uid", "secret_types"}, "user_id = ? AND uid = ?", new String[]{Integer.toString(i), Integer.toString(i2)}, null, null, null);
        try {
            int count = query.getCount();
            if (count == 0) {
                int[] iArr = new int[0];
                query.close();
                return iArr;
            } else if (count > 1) {
                Log.wtf("RecoverableKeyStoreDb", String.format(Locale.US, "%d deviceId entries found for userId=%d uid=%d. Should only ever be 0 or 1.", Integer.valueOf(count), Integer.valueOf(i), Integer.valueOf(i2)));
                int[] iArr2 = new int[0];
                query.close();
                return iArr2;
            } else {
                query.moveToFirst();
                int columnIndexOrThrow = query.getColumnIndexOrThrow("secret_types");
                if (query.isNull(columnIndexOrThrow)) {
                    int[] iArr3 = new int[0];
                    query.close();
                    return iArr3;
                }
                String string = query.getString(columnIndexOrThrow);
                if (TextUtils.isEmpty(string)) {
                    int[] iArr4 = new int[0];
                    query.close();
                    return iArr4;
                }
                String[] split = string.split(",");
                int[] iArr5 = new int[split.length];
                for (int i3 = 0; i3 < split.length; i3++) {
                    try {
                        iArr5[i3] = Integer.parseInt(split[i3]);
                    } catch (NumberFormatException e) {
                        Log.wtf("RecoverableKeyStoreDb", "String format error " + e);
                    }
                }
                query.close();
                return iArr5;
            }
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

    public long setActiveRootOfTrust(int i, int i2, String str) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("active_root_of_trust", str);
        ensureRecoveryServiceMetadataEntryExists(i, i2);
        return writableDatabase.update("recovery_service_metadata", contentValues, "user_id = ? AND uid = ?", new String[]{String.valueOf(i), String.valueOf(i2)});
    }

    public String getActiveRootOfTrust(int i, int i2) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"_id", "user_id", "uid", "active_root_of_trust"}, "user_id = ? AND uid = ?", new String[]{Integer.toString(i), Integer.toString(i2)}, null, null, null);
        try {
            int count = query.getCount();
            if (count == 0) {
                query.close();
                return null;
            } else if (count > 1) {
                Log.wtf("RecoverableKeyStoreDb", String.format(Locale.US, "%d deviceId entries found for userId=%d uid=%d. Should only ever be 0 or 1.", Integer.valueOf(count), Integer.valueOf(i), Integer.valueOf(i2)));
                query.close();
                return null;
            } else {
                query.moveToFirst();
                int columnIndexOrThrow = query.getColumnIndexOrThrow("active_root_of_trust");
                if (query.isNull(columnIndexOrThrow)) {
                    query.close();
                    return null;
                }
                String string = query.getString(columnIndexOrThrow);
                if (TextUtils.isEmpty(string)) {
                    query.close();
                    return null;
                }
                query.close();
                return string;
            }
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

    public long setCounterId(int i, int i2, long j) {
        return setLong(i, i2, "counter_id", j);
    }

    public Long getCounterId(int i, int i2) {
        return getLong(i, i2, "counter_id");
    }

    public long setServerParams(int i, int i2, byte[] bArr) {
        return setBytes(i, i2, "server_params", bArr);
    }

    public byte[] getServerParams(int i, int i2) {
        return getBytes(i, i2, "server_params");
    }

    public long setSnapshotVersion(int i, int i2, long j) {
        return setLong(i, i2, "snapshot_version", j);
    }

    public Long getSnapshotVersion(int i, int i2) {
        return getLong(i, i2, "snapshot_version");
    }

    public long setShouldCreateSnapshot(int i, int i2, boolean z) {
        return setLong(i, i2, "should_create_snapshot", z ? 1L : 0L);
    }

    public boolean getShouldCreateSnapshot(int i, int i2) {
        Long l = getLong(i, i2, "should_create_snapshot");
        return (l == null || l.longValue() == 0) ? false : true;
    }

    public final Long getLong(int i, int i2, String str) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"_id", "user_id", "uid", str}, "user_id = ? AND uid = ?", new String[]{Integer.toString(i), Integer.toString(i2)}, null, null, null);
        try {
            int count = query.getCount();
            if (count == 0) {
                query.close();
                return null;
            } else if (count > 1) {
                Log.wtf("RecoverableKeyStoreDb", String.format(Locale.US, "%d entries found for userId=%d uid=%d. Should only ever be 0 or 1.", Integer.valueOf(count), Integer.valueOf(i), Integer.valueOf(i2)));
                query.close();
                return null;
            } else {
                query.moveToFirst();
                int columnIndexOrThrow = query.getColumnIndexOrThrow(str);
                if (query.isNull(columnIndexOrThrow)) {
                    query.close();
                    return null;
                }
                Long valueOf = Long.valueOf(query.getLong(columnIndexOrThrow));
                query.close();
                return valueOf;
            }
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

    public final long setLong(int i, int i2, String str, long j) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(str, Long.valueOf(j));
        ensureRecoveryServiceMetadataEntryExists(i, i2);
        return writableDatabase.update("recovery_service_metadata", contentValues, "user_id = ? AND uid = ?", new String[]{Integer.toString(i), Integer.toString(i2)});
    }

    public final byte[] getBytes(int i, int i2, String str) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("recovery_service_metadata", new String[]{"_id", "user_id", "uid", str}, "user_id = ? AND uid = ?", new String[]{Integer.toString(i), Integer.toString(i2)}, null, null, null);
        try {
            int count = query.getCount();
            if (count == 0) {
                query.close();
                return null;
            } else if (count > 1) {
                Log.wtf("RecoverableKeyStoreDb", String.format(Locale.US, "%d entries found for userId=%d uid=%d. Should only ever be 0 or 1.", Integer.valueOf(count), Integer.valueOf(i), Integer.valueOf(i2)));
                query.close();
                return null;
            } else {
                query.moveToFirst();
                int columnIndexOrThrow = query.getColumnIndexOrThrow(str);
                if (query.isNull(columnIndexOrThrow)) {
                    query.close();
                    return null;
                }
                byte[] blob = query.getBlob(columnIndexOrThrow);
                query.close();
                return blob;
            }
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

    public final long setBytes(int i, int i2, String str, byte[] bArr) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(str, bArr);
        ensureRecoveryServiceMetadataEntryExists(i, i2);
        return writableDatabase.update("recovery_service_metadata", contentValues, "user_id = ? AND uid = ?", new String[]{Integer.toString(i), Integer.toString(i2)});
    }

    public final byte[] getBytes(int i, int i2, String str, String str2) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("root_of_trust", new String[]{"_id", "user_id", "uid", "root_alias", str2}, "user_id = ? AND uid = ? AND root_alias = ?", new String[]{Integer.toString(i), Integer.toString(i2), this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(str)}, null, null, null);
        try {
            int count = query.getCount();
            if (count == 0) {
                query.close();
                return null;
            } else if (count > 1) {
                Log.wtf("RecoverableKeyStoreDb", String.format(Locale.US, "%d entries found for userId=%d uid=%d. Should only ever be 0 or 1.", Integer.valueOf(count), Integer.valueOf(i), Integer.valueOf(i2)));
                query.close();
                return null;
            } else {
                query.moveToFirst();
                int columnIndexOrThrow = query.getColumnIndexOrThrow(str2);
                if (query.isNull(columnIndexOrThrow)) {
                    query.close();
                    return null;
                }
                byte[] blob = query.getBlob(columnIndexOrThrow);
                query.close();
                return blob;
            }
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

    public final long setBytes(int i, int i2, String str, String str2, byte[] bArr) {
        String defaultCertificateAliasIfEmpty = this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(str);
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(str2, bArr);
        ensureRootOfTrustEntryExists(i, i2, defaultCertificateAliasIfEmpty);
        return writableDatabase.update("root_of_trust", contentValues, "user_id = ? AND uid = ? AND root_alias = ?", new String[]{Integer.toString(i), Integer.toString(i2), defaultCertificateAliasIfEmpty});
    }

    public final Long getLong(int i, int i2, String str, String str2) {
        Cursor query = this.mKeyStoreDbHelper.getReadableDatabase().query("root_of_trust", new String[]{"_id", "user_id", "uid", "root_alias", str2}, "user_id = ? AND uid = ? AND root_alias = ?", new String[]{Integer.toString(i), Integer.toString(i2), this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(str)}, null, null, null);
        try {
            int count = query.getCount();
            if (count == 0) {
                query.close();
                return null;
            } else if (count > 1) {
                Log.wtf("RecoverableKeyStoreDb", String.format(Locale.US, "%d entries found for userId=%d uid=%d. Should only ever be 0 or 1.", Integer.valueOf(count), Integer.valueOf(i), Integer.valueOf(i2)));
                query.close();
                return null;
            } else {
                query.moveToFirst();
                int columnIndexOrThrow = query.getColumnIndexOrThrow(str2);
                if (query.isNull(columnIndexOrThrow)) {
                    query.close();
                    return null;
                }
                Long valueOf = Long.valueOf(query.getLong(columnIndexOrThrow));
                query.close();
                return valueOf;
            }
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

    public final long setLong(int i, int i2, String str, String str2, long j) {
        String defaultCertificateAliasIfEmpty = this.mTestOnlyInsecureCertificateHelper.getDefaultCertificateAliasIfEmpty(str);
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(str2, Long.valueOf(j));
        ensureRootOfTrustEntryExists(i, i2, defaultCertificateAliasIfEmpty);
        return writableDatabase.update("root_of_trust", contentValues, "user_id = ? AND uid = ? AND root_alias = ?", new String[]{Integer.toString(i), Integer.toString(i2), defaultCertificateAliasIfEmpty});
    }

    public void removeUserFromAllTables(int i) {
        removeUserFromKeysTable(i);
        removeUserFromUserMetadataTable(i);
        removeUserFromRecoveryServiceMetadataTable(i);
        removeUserFromRootOfTrustTable(i);
    }

    public final boolean removeUserFromKeysTable(int i) {
        return this.mKeyStoreDbHelper.getWritableDatabase().delete("keys", "user_id = ?", new String[]{Integer.toString(i)}) > 0;
    }

    public final boolean removeUserFromUserMetadataTable(int i) {
        return this.mKeyStoreDbHelper.getWritableDatabase().delete("user_metadata", "user_id = ?", new String[]{Integer.toString(i)}) > 0;
    }

    public final boolean removeUserFromRecoveryServiceMetadataTable(int i) {
        return this.mKeyStoreDbHelper.getWritableDatabase().delete("recovery_service_metadata", "user_id = ?", new String[]{Integer.toString(i)}) > 0;
    }

    public final boolean removeUserFromRootOfTrustTable(int i) {
        return this.mKeyStoreDbHelper.getWritableDatabase().delete("root_of_trust", "user_id = ?", new String[]{Integer.toString(i)}) > 0;
    }

    public final void ensureRecoveryServiceMetadataEntryExists(int i, int i2) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", Integer.valueOf(i));
        contentValues.put("uid", Integer.valueOf(i2));
        writableDatabase.insertWithOnConflict("recovery_service_metadata", null, contentValues, 4);
    }

    public final void ensureRootOfTrustEntryExists(int i, int i2, String str) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", Integer.valueOf(i));
        contentValues.put("uid", Integer.valueOf(i2));
        contentValues.put("root_alias", str);
        writableDatabase.insertWithOnConflict("root_of_trust", null, contentValues, 4);
    }

    public final void ensureUserMetadataEntryExists(int i) {
        SQLiteDatabase writableDatabase = this.mKeyStoreDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", Integer.valueOf(i));
        writableDatabase.insertWithOnConflict("user_metadata", null, contentValues, 4);
    }

    public static PublicKey decodeX509Key(byte[] bArr) throws InvalidKeySpecException {
        try {
            return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(bArr));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static CertPath decodeCertPath(byte[] bArr) throws CertificateException {
        try {
            return CertificateFactory.getInstance("X.509").generateCertPath(new ByteArrayInputStream(bArr), "PkiPath");
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }
}
