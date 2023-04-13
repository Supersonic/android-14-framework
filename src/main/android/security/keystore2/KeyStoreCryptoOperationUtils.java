package android.security.keystore2;

import android.app.ActivityThread;
import android.hardware.biometrics.BiometricManager;
import android.security.GateKeeper;
import android.security.KeyStoreException;
import android.security.KeyStoreOperation;
import android.security.keystore.KeyExpiredException;
import android.security.keystore.KeyNotYetValidException;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.UserNotAuthenticatedException;
import android.system.keystore2.Authorization;
import android.util.Log;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import libcore.util.EmptyArray;
/* loaded from: classes3.dex */
abstract class KeyStoreCryptoOperationUtils {
    private static final Random sRandom = new Random();
    private static volatile SecureRandom sRng;

    private KeyStoreCryptoOperationUtils() {
    }

    public static boolean canUserAuthorizationSucceed(AndroidKeyStoreKey key) {
        Authorization[] authorizations;
        List<Long> keySids = new ArrayList<>();
        for (Authorization p : key.getAuthorizations()) {
            switch (p.keyParameter.tag) {
                case -1610612234:
                    keySids.add(Long.valueOf(p.keyParameter.value.getLongInteger()));
                    break;
            }
        }
        if (keySids.isEmpty()) {
            return false;
        }
        long rootSid = GateKeeper.getSecureUserId();
        if (rootSid == 0 || !keySids.contains(Long.valueOf(rootSid))) {
            long[] biometricSids = ((BiometricManager) ActivityThread.currentApplication().getSystemService(BiometricManager.class)).getAuthenticatorIds();
            boolean canUnlockViaBiometrics = biometricSids.length > 0;
            int length = biometricSids.length;
            int i = 0;
            while (true) {
                if (i < length) {
                    long sid = biometricSids[i];
                    if (keySids.contains(Long.valueOf(sid))) {
                        i++;
                    } else {
                        canUnlockViaBiometrics = false;
                    }
                }
            }
            return canUnlockViaBiometrics;
        }
        return true;
    }

    public static InvalidKeyException getInvalidKeyException(AndroidKeyStoreKey key, KeyStoreException e) {
        switch (e.getErrorCode()) {
            case -26:
            case 2:
            case 3:
                return new UserNotAuthenticatedException();
            case -25:
                return new KeyExpiredException();
            case -24:
                return new KeyNotYetValidException();
            case 7:
            case 17:
                return new KeyPermanentlyInvalidatedException();
            default:
                return new InvalidKeyException("Keystore operation failed", e);
        }
    }

    public static GeneralSecurityException getExceptionForCipherInit(AndroidKeyStoreKey key, KeyStoreException e) {
        if (e.getErrorCode() == 1) {
            return null;
        }
        switch (e.getErrorCode()) {
            case -55:
                return new InvalidAlgorithmParameterException("Caller-provided IV not permitted");
            case -52:
                return new InvalidAlgorithmParameterException("Invalid IV");
            default:
                return getInvalidKeyException(key, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] getRandomBytesToMixIntoKeystoreRng(SecureRandom rng, int sizeBytes) {
        if (sizeBytes <= 0) {
            return EmptyArray.BYTE;
        }
        if (rng == null) {
            rng = getRng();
        }
        byte[] result = new byte[sizeBytes];
        rng.nextBytes(result);
        return result;
    }

    private static SecureRandom getRng() {
        if (sRng == null) {
            sRng = new SecureRandom();
        }
        return sRng;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void abortOperation(KeyStoreOperation operation) {
        if (operation != null) {
            try {
                operation.abort();
            } catch (KeyStoreException e) {
                if (e.getErrorCode() != -28) {
                    Log.m103w("KeyStoreCryptoOperationUtils", "Encountered error trying to abort a keystore operation.", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long getOrMakeOperationChallenge(KeyStoreOperation operation, AndroidKeyStoreKey key) throws KeyPermanentlyInvalidatedException {
        if (operation.getChallenge() != null) {
            if (!canUserAuthorizationSucceed(key)) {
                throw new KeyPermanentlyInvalidatedException();
            }
            return operation.getChallenge().longValue();
        }
        return sRandom.nextLong();
    }
}
