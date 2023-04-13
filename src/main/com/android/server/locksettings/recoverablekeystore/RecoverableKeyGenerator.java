package com.android.server.locksettings.recoverablekeystore;

import android.util.Log;
import com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes2.dex */
public class RecoverableKeyGenerator {
    public final RecoverableKeyStoreDb mDatabase;
    public final KeyGenerator mKeyGenerator;

    public static RecoverableKeyGenerator newInstance(RecoverableKeyStoreDb recoverableKeyStoreDb) throws NoSuchAlgorithmException {
        return new RecoverableKeyGenerator(KeyGenerator.getInstance("AES"), recoverableKeyStoreDb);
    }

    public RecoverableKeyGenerator(KeyGenerator keyGenerator, RecoverableKeyStoreDb recoverableKeyStoreDb) {
        this.mKeyGenerator = keyGenerator;
        this.mDatabase = recoverableKeyStoreDb;
    }

    public byte[] generateAndStoreKey(PlatformEncryptionKey platformEncryptionKey, int i, int i2, String str, byte[] bArr) throws RecoverableKeyStorageException, KeyStoreException, InvalidKeyException {
        this.mKeyGenerator.init(256);
        SecretKey generateKey = this.mKeyGenerator.generateKey();
        if (this.mDatabase.insertKey(i, i2, str, WrappedKey.fromSecretKey(platformEncryptionKey, generateKey, bArr)) == -1) {
            throw new RecoverableKeyStorageException(String.format(Locale.US, "Failed writing (%d, %s) to database.", Integer.valueOf(i2), str));
        }
        if (this.mDatabase.setShouldCreateSnapshot(i, i2, true) < 0) {
            Log.e("PlatformKeyGen", "Failed to set the shoudCreateSnapshot flag in the local DB.");
        }
        return generateKey.getEncoded();
    }

    public void importKey(PlatformEncryptionKey platformEncryptionKey, int i, int i2, String str, byte[] bArr, byte[] bArr2) throws RecoverableKeyStorageException, KeyStoreException, InvalidKeyException {
        if (this.mDatabase.insertKey(i, i2, str, WrappedKey.fromSecretKey(platformEncryptionKey, new SecretKeySpec(bArr, "AES"), bArr2)) == -1) {
            throw new RecoverableKeyStorageException(String.format(Locale.US, "Failed writing (%d, %s) to database.", Integer.valueOf(i2), str));
        }
        this.mDatabase.setShouldCreateSnapshot(i, i2, true);
    }
}
