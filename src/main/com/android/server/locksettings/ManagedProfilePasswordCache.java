package com.android.server.locksettings;

import android.content.pm.UserInfo;
import android.os.UserManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.LockscreenCredential;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
@VisibleForTesting
/* loaded from: classes2.dex */
public class ManagedProfilePasswordCache {
    public static final int CACHE_TIMEOUT_SECONDS = (int) TimeUnit.DAYS.toSeconds(7);
    public final SparseArray<byte[]> mEncryptedPasswords = new SparseArray<>();
    public final KeyStore mKeyStore;
    public final UserManager mUserManager;

    public ManagedProfilePasswordCache(KeyStore keyStore, UserManager userManager) {
        this.mKeyStore = keyStore;
        this.mUserManager = userManager;
    }

    public void storePassword(int i, LockscreenCredential lockscreenCredential) {
        synchronized (this.mEncryptedPasswords) {
            if (this.mEncryptedPasswords.contains(i)) {
                return;
            }
            UserInfo profileParent = this.mUserManager.getProfileParent(i);
            if (profileParent != null && profileParent.id == 0) {
                String encryptionKeyName = getEncryptionKeyName(i);
                try {
                    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", this.mKeyStore.getProvider());
                    keyGenerator.init(new KeyGenParameterSpec.Builder(encryptionKeyName, 3).setKeySize(256).setBlockModes("GCM").setNamespace(SyntheticPasswordCrypto.keyNamespace()).setEncryptionPaddings("NoPadding").setUserAuthenticationRequired(true).setUserAuthenticationValidityDurationSeconds(CACHE_TIMEOUT_SECONDS).build());
                    SecretKey generateKey = keyGenerator.generateKey();
                    try {
                        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                        cipher.init(1, generateKey);
                        this.mEncryptedPasswords.put(i, ArrayUtils.concat(new byte[][]{cipher.getIV(), cipher.doFinal(lockscreenCredential.getCredential())}));
                    } catch (GeneralSecurityException e) {
                        Slog.d("ManagedProfilePasswordCache", "Cannot encrypt", e);
                    }
                } catch (GeneralSecurityException e2) {
                    Slog.e("ManagedProfilePasswordCache", "Cannot generate key", e2);
                }
            }
        }
    }

    public LockscreenCredential retrievePassword(int i) {
        synchronized (this.mEncryptedPasswords) {
            byte[] bArr = this.mEncryptedPasswords.get(i);
            if (bArr == null) {
                return null;
            }
            try {
                Key key = this.mKeyStore.getKey(getEncryptionKeyName(i), null);
                if (key == null) {
                    return null;
                }
                byte[] copyOf = Arrays.copyOf(bArr, 12);
                byte[] copyOfRange = Arrays.copyOfRange(bArr, 12, bArr.length);
                try {
                    try {
                        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                        cipher.init(2, key, new GCMParameterSpec(128, copyOf));
                        byte[] doFinal = cipher.doFinal(copyOfRange);
                        LockscreenCredential createManagedPassword = LockscreenCredential.createManagedPassword(doFinal);
                        Arrays.fill(doFinal, (byte) 0);
                        return createManagedPassword;
                    } catch (UserNotAuthenticatedException unused) {
                        Slog.i("ManagedProfilePasswordCache", "Device not unlocked for more than 7 days");
                        return null;
                    }
                } catch (GeneralSecurityException e) {
                    Slog.d("ManagedProfilePasswordCache", "Cannot decrypt", e);
                    return null;
                }
            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e2) {
                Slog.d("ManagedProfilePasswordCache", "Cannot get key", e2);
                return null;
            }
        }
    }

    public void removePassword(int i) {
        synchronized (this.mEncryptedPasswords) {
            String encryptionKeyName = getEncryptionKeyName(i);
            String legacyEncryptionKeyName = getLegacyEncryptionKeyName(i);
            try {
                if (this.mKeyStore.containsAlias(encryptionKeyName)) {
                    this.mKeyStore.deleteEntry(encryptionKeyName);
                }
                if (this.mKeyStore.containsAlias(legacyEncryptionKeyName)) {
                    this.mKeyStore.deleteEntry(legacyEncryptionKeyName);
                }
            } catch (KeyStoreException e) {
                Slog.d("ManagedProfilePasswordCache", "Cannot delete key", e);
            }
            if (this.mEncryptedPasswords.contains(i)) {
                Arrays.fill(this.mEncryptedPasswords.get(i), (byte) 0);
                this.mEncryptedPasswords.remove(i);
            }
        }
    }

    public static String getEncryptionKeyName(int i) {
        return "com.android.server.locksettings.unified_profile_cache_v2_" + i;
    }

    public static String getLegacyEncryptionKeyName(int i) {
        return "com.android.server.locksettings.unified_profile_cache_" + i;
    }
}
