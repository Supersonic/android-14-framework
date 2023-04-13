package com.android.server.locksettings.recoverablekeystore;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.RemoteException;
import android.security.GateKeeper;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProtection;
import android.service.gatekeeper.IGateKeeperService;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.locksettings.recoverablekeystore.storage.RecoverableKeyStoreDb;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
/* loaded from: classes2.dex */
public class PlatformKeyManager {
    public static final byte[] GCM_INSECURE_NONCE_BYTES = new byte[12];
    public final Context mContext;
    public final RecoverableKeyStoreDb mDatabase;
    public final KeyStoreProxy mKeyStore;

    public static PlatformKeyManager getInstance(Context context, RecoverableKeyStoreDb recoverableKeyStoreDb) throws KeyStoreException, NoSuchAlgorithmException {
        return new PlatformKeyManager(context.getApplicationContext(), new KeyStoreProxyImpl(getAndLoadAndroidKeyStore()), recoverableKeyStoreDb);
    }

    @VisibleForTesting
    public PlatformKeyManager(Context context, KeyStoreProxy keyStoreProxy, RecoverableKeyStoreDb recoverableKeyStoreDb) {
        this.mKeyStore = keyStoreProxy;
        this.mContext = context;
        this.mDatabase = recoverableKeyStoreDb;
    }

    public int getGenerationId(int i) {
        return this.mDatabase.getPlatformKeyGenerationId(i);
    }

    public boolean isDeviceLocked(int i) {
        return ((KeyguardManager) this.mContext.getSystemService(KeyguardManager.class)).isDeviceLocked(i);
    }

    public void invalidatePlatformKey(int i, int i2) {
        if (i2 != -1) {
            try {
                this.mKeyStore.deleteEntry(getEncryptAlias(i, i2));
                this.mKeyStore.deleteEntry(getDecryptAlias(i, i2));
            } catch (KeyStoreException unused) {
            }
        }
    }

    @VisibleForTesting
    public void regenerate(int i) throws NoSuchAlgorithmException, KeyStoreException, IOException, RemoteException {
        int generationId = getGenerationId(i);
        int i2 = 1;
        if (generationId != -1) {
            invalidatePlatformKey(i, generationId);
            i2 = 1 + generationId;
        }
        generateAndLoadKey(i, i2);
    }

    public PlatformEncryptionKey getEncryptKey(int i) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, RemoteException {
        init(i);
        try {
            getDecryptKeyInternal(i);
            return getEncryptKeyInternal(i);
        } catch (UnrecoverableKeyException unused) {
            Log.i("PlatformKeyManager", String.format(Locale.US, "Regenerating permanently invalid Platform key for user %d.", Integer.valueOf(i)));
            this.regenerate(i);
            return this.getEncryptKeyInternal(i);
        }
    }

    public final PlatformEncryptionKey getEncryptKeyInternal(int i) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        int generationId = getGenerationId(i);
        String encryptAlias = getEncryptAlias(i, generationId);
        if (!isKeyLoaded(i, generationId)) {
            throw new UnrecoverableKeyException("KeyStore doesn't contain key " + encryptAlias);
        }
        return new PlatformEncryptionKey(generationId, (SecretKey) this.mKeyStore.getKey(encryptAlias, null));
    }

    public PlatformDecryptionKey getDecryptKey(int i) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, RemoteException {
        init(i);
        try {
            PlatformDecryptionKey decryptKeyInternal = getDecryptKeyInternal(i);
            ensureDecryptionKeyIsValid(i, decryptKeyInternal);
            return decryptKeyInternal;
        } catch (UnrecoverableKeyException unused) {
            Log.i("PlatformKeyManager", String.format(Locale.US, "Regenerating permanently invalid Platform key for user %d.", Integer.valueOf(i)));
            regenerate(i);
            return getDecryptKeyInternal(i);
        }
    }

    public final PlatformDecryptionKey getDecryptKeyInternal(int i) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        int generationId = getGenerationId(i);
        String decryptAlias = getDecryptAlias(i, generationId);
        if (!isKeyLoaded(i, generationId)) {
            throw new UnrecoverableKeyException("KeyStore doesn't contain key " + decryptAlias);
        }
        return new PlatformDecryptionKey(generationId, (SecretKey) this.mKeyStore.getKey(decryptAlias, null));
    }

    public final void ensureDecryptionKeyIsValid(int i, PlatformDecryptionKey platformDecryptionKey) throws UnrecoverableKeyException {
        try {
            Cipher.getInstance("AES/GCM/NoPadding").init(4, platformDecryptionKey.getKey(), new GCMParameterSpec(128, GCM_INSECURE_NONCE_BYTES));
        } catch (KeyPermanentlyInvalidatedException e) {
            Log.e("PlatformKeyManager", String.format(Locale.US, "The platform key for user %d became invalid.", Integer.valueOf(i)));
            throw new UnrecoverableKeyException(e.getMessage());
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException unused) {
        }
    }

    public void init(int i) throws KeyStoreException, NoSuchAlgorithmException, IOException, RemoteException {
        int generationId = getGenerationId(i);
        if (isKeyLoaded(i, generationId)) {
            Log.i("PlatformKeyManager", String.format(Locale.US, "Platform key generation %d exists already.", Integer.valueOf(generationId)));
            return;
        }
        int i2 = 1;
        if (generationId == -1) {
            Log.i("PlatformKeyManager", "Generating initial platform key generation ID.");
        } else {
            Log.w("PlatformKeyManager", String.format(Locale.US, "Platform generation ID was %d but no entry was present in AndroidKeyStore. Generating fresh key.", Integer.valueOf(generationId)));
            i2 = 1 + generationId;
        }
        generateAndLoadKey(i, Math.max(i2, 1001000));
    }

    public final String getEncryptAlias(int i, int i2) {
        return "com.android.server.locksettings.recoverablekeystore/platform/" + i + "/" + i2 + "/encrypt";
    }

    public final String getDecryptAlias(int i, int i2) {
        return "com.android.server.locksettings.recoverablekeystore/platform/" + i + "/" + i2 + "/decrypt";
    }

    public final void setGenerationId(int i, int i2) throws IOException {
        this.mDatabase.setPlatformKeyGenerationId(i, i2);
    }

    public final boolean isKeyLoaded(int i, int i2) throws KeyStoreException {
        return this.mKeyStore.containsAlias(getEncryptAlias(i, i2)) && this.mKeyStore.containsAlias(getDecryptAlias(i, i2));
    }

    @VisibleForTesting
    public IGateKeeperService getGateKeeperService() {
        return GateKeeper.getService();
    }

    public final void generateAndLoadKey(int i, int i2) throws NoSuchAlgorithmException, KeyStoreException, IOException, RemoteException {
        String encryptAlias = getEncryptAlias(i, i2);
        String decryptAlias = getDecryptAlias(i, i2);
        SecretKey generateAesKey = generateAesKey();
        KeyProtection.Builder encryptionPaddings = new KeyProtection.Builder(2).setBlockModes("GCM").setEncryptionPaddings("NoPadding");
        if (i == 0) {
            encryptionPaddings.setUnlockedDeviceRequired(true);
        }
        this.mKeyStore.setEntry(decryptAlias, new KeyStore.SecretKeyEntry(generateAesKey), encryptionPaddings.build());
        this.mKeyStore.setEntry(encryptAlias, new KeyStore.SecretKeyEntry(generateAesKey), new KeyProtection.Builder(1).setBlockModes("GCM").setEncryptionPaddings("NoPadding").build());
        setGenerationId(i, i2);
    }

    public static SecretKey generateAesKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static KeyStore getAndLoadAndroidKeyStore() throws KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        try {
            keyStore.load(null);
            return keyStore;
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new KeyStoreException("Unable to load keystore.", e);
        }
    }
}
