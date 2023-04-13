package com.android.server.locksettings;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
/* loaded from: classes2.dex */
public class RebootEscrowKeyStoreManager {
    public final Object mKeyStoreLock = new Object();

    @GuardedBy({"mKeyStoreLock"})
    public final SecretKey getKeyStoreEncryptionKeyLocked() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeystore");
            keyStore.load(new AndroidKeyStoreLoadStoreParameter(120));
            return (SecretKey) keyStore.getKey("reboot_escrow_key_store_encryption_key", null);
        } catch (IOException | GeneralSecurityException e) {
            Slog.e("RebootEscrowKeyStoreManager", "Unable to get encryption key from keystore.", e);
            return null;
        }
    }

    public SecretKey getKeyStoreEncryptionKey() {
        SecretKey keyStoreEncryptionKeyLocked;
        synchronized (this.mKeyStoreLock) {
            keyStoreEncryptionKeyLocked = getKeyStoreEncryptionKeyLocked();
        }
        return keyStoreEncryptionKeyLocked;
    }

    public void clearKeyStoreEncryptionKey() {
        synchronized (this.mKeyStoreLock) {
            try {
                KeyStore keyStore = KeyStore.getInstance("AndroidKeystore");
                keyStore.load(new AndroidKeyStoreLoadStoreParameter(120));
                keyStore.deleteEntry("reboot_escrow_key_store_encryption_key");
            } catch (IOException | GeneralSecurityException e) {
                Slog.e("RebootEscrowKeyStoreManager", "Unable to delete encryption key in keystore.", e);
            }
        }
    }

    public SecretKey generateKeyStoreEncryptionKeyIfNeeded() {
        synchronized (this.mKeyStoreLock) {
            SecretKey keyStoreEncryptionKeyLocked = getKeyStoreEncryptionKeyLocked();
            if (keyStoreEncryptionKeyLocked != null) {
                return keyStoreEncryptionKeyLocked;
            }
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
                KeyGenParameterSpec.Builder encryptionPaddings = new KeyGenParameterSpec.Builder("reboot_escrow_key_store_encryption_key", 3).setKeySize(256).setBlockModes("GCM").setEncryptionPaddings("NoPadding");
                encryptionPaddings.setNamespace(120);
                keyGenerator.init(encryptionPaddings.build());
                return keyGenerator.generateKey();
            } catch (GeneralSecurityException e) {
                Slog.e("RebootEscrowKeyStoreManager", "Unable to generate key from keystore.", e);
                return null;
            }
        }
    }
}
