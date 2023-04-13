package com.android.server.locksettings;

import android.security.AndroidKeyStoreMaintenance;
import android.security.keystore.KeyProtection;
import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import android.system.keystore2.KeyDescriptor;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes2.dex */
public class SyntheticPasswordCrypto {
    public static final byte[] PROTECTOR_SECRET_PERSONALIZATION = "application-id".getBytes();

    public static String androidKeystoreProviderName() {
        return "AndroidKeyStore";
    }

    public static int keyNamespace() {
        return 103;
    }

    public static byte[] decrypt(SecretKey secretKey, byte[] bArr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (bArr == null) {
            return null;
        }
        byte[] copyOfRange = Arrays.copyOfRange(bArr, 0, 12);
        byte[] copyOfRange2 = Arrays.copyOfRange(bArr, 12, bArr.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(2, secretKey, new GCMParameterSpec(128, copyOfRange));
        return cipher.doFinal(copyOfRange2);
    }

    public static byte[] encrypt(SecretKey secretKey, byte[] bArr) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException {
        if (bArr == null) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(1, secretKey);
        byte[] doFinal = cipher.doFinal(bArr);
        byte[] iv = cipher.getIV();
        if (iv.length != 12) {
            throw new IllegalArgumentException("Invalid iv length: " + iv.length + " bytes");
        }
        GCMParameterSpec gCMParameterSpec = (GCMParameterSpec) cipher.getParameters().getParameterSpec(GCMParameterSpec.class);
        if (gCMParameterSpec.getTLen() != 128) {
            throw new IllegalArgumentException("Invalid tag length: " + gCMParameterSpec.getTLen() + " bits");
        }
        return ArrayUtils.concat(new byte[][]{iv, doFinal});
    }

    public static byte[] encrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        try {
            return encrypt(new SecretKeySpec(Arrays.copyOf(personalizedHash(bArr2, bArr), 32), "AES"), bArr3);
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InvalidParameterSpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            Slog.e("SyntheticPasswordCrypto", "Failed to encrypt", e);
            return null;
        }
    }

    public static byte[] decrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        try {
            return decrypt(new SecretKeySpec(Arrays.copyOf(personalizedHash(bArr2, bArr), 32), "AES"), bArr3);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            Slog.e("SyntheticPasswordCrypto", "Failed to decrypt", e);
            return null;
        }
    }

    public static byte[] decryptBlobV1(String str, byte[] bArr, byte[] bArr2) {
        try {
            SecretKey secretKey = (SecretKey) getKeyStore().getKey(str, null);
            if (secretKey == null) {
                throw new IllegalStateException("SP protector key is missing: " + str);
            }
            return decrypt(secretKey, decrypt(bArr2, PROTECTOR_SECRET_PERSONALIZATION, bArr));
        } catch (Exception e) {
            Slog.e("SyntheticPasswordCrypto", "Failed to decrypt V1 blob", e);
            throw new IllegalStateException("Failed to decrypt blob", e);
        }
    }

    public static KeyStore getKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(androidKeystoreProviderName());
        keyStore.load(new AndroidKeyStoreLoadStoreParameter(keyNamespace()));
        return keyStore;
    }

    public static byte[] decryptBlob(String str, byte[] bArr, byte[] bArr2) {
        try {
            SecretKey secretKey = (SecretKey) getKeyStore().getKey(str, null);
            if (secretKey == null) {
                throw new IllegalStateException("SP protector key is missing: " + str);
            }
            return decrypt(bArr2, PROTECTOR_SECRET_PERSONALIZATION, decrypt(secretKey, bArr));
        } catch (IOException | InvalidAlgorithmParameterException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            Slog.e("SyntheticPasswordCrypto", "Failed to decrypt blob", e);
            throw new IllegalStateException("Failed to decrypt blob", e);
        }
    }

    public static byte[] createBlob(String str, byte[] bArr, byte[] bArr2, long j) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom());
            SecretKey generateKey = keyGenerator.generateKey();
            KeyStore keyStore = getKeyStore();
            KeyProtection.Builder criticalToDeviceEncryption = new KeyProtection.Builder(2).setBlockModes("GCM").setEncryptionPaddings("NoPadding").setCriticalToDeviceEncryption(true);
            if (j != 0) {
                criticalToDeviceEncryption.setUserAuthenticationRequired(true).setBoundToSpecificSecureUserId(j).setUserAuthenticationValidityDurationSeconds(15);
            }
            KeyProtection build = criticalToDeviceEncryption.build();
            criticalToDeviceEncryption.setRollbackResistant(true);
            KeyProtection build2 = criticalToDeviceEncryption.build();
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(generateKey);
            try {
                keyStore.setEntry(str, secretKeyEntry, build2);
                Slog.i("SyntheticPasswordCrypto", "Using rollback-resistant key");
            } catch (KeyStoreException unused) {
                Slog.w("SyntheticPasswordCrypto", "Rollback-resistant keys unavailable.  Falling back to non-rollback-resistant key");
                keyStore.setEntry(str, secretKeyEntry, build);
            }
            return encrypt(generateKey, encrypt(bArr2, PROTECTOR_SECRET_PERSONALIZATION, bArr));
        } catch (IOException | InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException | InvalidParameterSpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            Slog.e("SyntheticPasswordCrypto", "Failed to create blob", e);
            throw new IllegalStateException("Failed to encrypt blob", e);
        }
    }

    public static void destroyProtectorKey(String str) {
        try {
            getKeyStore().deleteEntry(str);
            Slog.i("SyntheticPasswordCrypto", "Deleted SP protector key " + str);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            Slog.e("SyntheticPasswordCrypto", "Failed to delete SP protector key " + str, e);
        }
    }

    public static byte[] personalizedHash(byte[] bArr, byte[]... bArr2) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            if (bArr.length > 128) {
                throw new IllegalArgumentException("Personalization too long");
            }
            messageDigest.update(Arrays.copyOf(bArr, 128));
            for (byte[] bArr3 : bArr2) {
                messageDigest.update(bArr3);
            }
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("NoSuchAlgorithmException for SHA-512", e);
        }
    }

    public static boolean migrateLockSettingsKey(String str) {
        KeyDescriptor keyDescriptor = new KeyDescriptor();
        keyDescriptor.domain = 0;
        keyDescriptor.nspace = -1L;
        keyDescriptor.alias = str;
        KeyDescriptor keyDescriptor2 = new KeyDescriptor();
        keyDescriptor2.domain = 2;
        keyDescriptor2.nspace = keyNamespace();
        keyDescriptor2.alias = str;
        Slog.i("SyntheticPasswordCrypto", "Migrating key " + str);
        int migrateKeyNamespace = AndroidKeyStoreMaintenance.migrateKeyNamespace(keyDescriptor, keyDescriptor2);
        if (migrateKeyNamespace == 0) {
            return true;
        }
        if (migrateKeyNamespace == 7) {
            Slog.i("SyntheticPasswordCrypto", "Key does not exist");
            return true;
        } else if (migrateKeyNamespace == 20) {
            Slog.i("SyntheticPasswordCrypto", "Key already exists");
            return true;
        } else {
            Slog.e("SyntheticPasswordCrypto", TextUtils.formatSimple("Failed to migrate key: %d", new Object[]{Integer.valueOf(migrateKeyNamespace)}));
            return false;
        }
    }
}
