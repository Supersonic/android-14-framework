package com.android.server.locksettings.recoverablekeystore;

import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.security.SecureBox;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.AEADBadTagException;
import javax.crypto.SecretKey;
/* loaded from: classes2.dex */
public class KeySyncUtils {
    public static final byte[] THM_ENCRYPTED_RECOVERY_KEY_HEADER = "V1 THM_encrypted_recovery_key".getBytes(StandardCharsets.UTF_8);
    public static final byte[] LOCALLY_ENCRYPTED_RECOVERY_KEY_HEADER = "V1 locally_encrypted_recovery_key".getBytes(StandardCharsets.UTF_8);
    public static final byte[] ENCRYPTED_APPLICATION_KEY_HEADER = "V1 encrypted_application_key".getBytes(StandardCharsets.UTF_8);
    public static final byte[] RECOVERY_CLAIM_HEADER = "V1 KF_claim".getBytes(StandardCharsets.UTF_8);
    public static final byte[] RECOVERY_RESPONSE_HEADER = "V1 reencrypted_recovery_key".getBytes(StandardCharsets.UTF_8);
    public static final byte[] THM_KF_HASH_PREFIX = "THM_KF_hash".getBytes(StandardCharsets.UTF_8);

    public static byte[] thmEncryptRecoveryKey(PublicKey publicKey, byte[] bArr, byte[] bArr2, SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        return SecureBox.encrypt(publicKey, calculateThmKfHash(bArr), ArrayUtils.concat(new byte[][]{THM_ENCRYPTED_RECOVERY_KEY_HEADER, bArr2}), locallyEncryptRecoveryKey(bArr, secretKey));
    }

    public static byte[] calculateThmKfHash(byte[] bArr) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(THM_KF_HASH_PREFIX);
        messageDigest.update(bArr);
        return messageDigest.digest();
    }

    @VisibleForTesting
    public static byte[] locallyEncryptRecoveryKey(byte[] bArr, SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        return SecureBox.encrypt(null, bArr, LOCALLY_ENCRYPTED_RECOVERY_KEY_HEADER, secretKey.getEncoded());
    }

    public static Map<String, byte[]> encryptKeysWithRecoveryKey(SecretKey secretKey, Map<String, Pair<SecretKey, byte[]>> map) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] concat;
        HashMap hashMap = new HashMap();
        for (String str : map.keySet()) {
            SecretKey secretKey2 = (SecretKey) map.get(str).first;
            byte[] bArr = (byte[]) map.get(str).second;
            if (bArr == null) {
                concat = ENCRYPTED_APPLICATION_KEY_HEADER;
            } else {
                concat = ArrayUtils.concat(new byte[][]{ENCRYPTED_APPLICATION_KEY_HEADER, bArr});
            }
            hashMap.put(str, SecureBox.encrypt(null, secretKey.getEncoded(), concat, secretKey2.getEncoded()));
        }
        return hashMap;
    }

    public static byte[] generateKeyClaimant() {
        byte[] bArr = new byte[16];
        new SecureRandom().nextBytes(bArr);
        return bArr;
    }

    public static byte[] encryptRecoveryClaim(PublicKey publicKey, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) throws NoSuchAlgorithmException, InvalidKeyException {
        return SecureBox.encrypt(publicKey, null, ArrayUtils.concat(new byte[][]{RECOVERY_CLAIM_HEADER, bArr, bArr2}), ArrayUtils.concat(new byte[][]{bArr3, bArr4}));
    }

    public static byte[] decryptRecoveryClaimResponse(byte[] bArr, byte[] bArr2, byte[] bArr3) throws NoSuchAlgorithmException, InvalidKeyException, AEADBadTagException {
        return SecureBox.decrypt(null, bArr, ArrayUtils.concat(new byte[][]{RECOVERY_RESPONSE_HEADER, bArr2}), bArr3);
    }

    public static byte[] decryptRecoveryKey(byte[] bArr, byte[] bArr2) throws NoSuchAlgorithmException, InvalidKeyException, AEADBadTagException {
        return SecureBox.decrypt(null, bArr, LOCALLY_ENCRYPTED_RECOVERY_KEY_HEADER, bArr2);
    }

    public static byte[] decryptApplicationKey(byte[] bArr, byte[] bArr2, byte[] bArr3) throws NoSuchAlgorithmException, InvalidKeyException, AEADBadTagException {
        byte[] concat;
        if (bArr3 == null) {
            concat = ENCRYPTED_APPLICATION_KEY_HEADER;
        } else {
            concat = ArrayUtils.concat(new byte[][]{ENCRYPTED_APPLICATION_KEY_HEADER, bArr3});
        }
        return SecureBox.decrypt(null, bArr, concat, bArr2);
    }

    public static PublicKey deserializePublicKey(byte[] bArr) throws InvalidKeySpecException {
        try {
            return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(bArr));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] packVaultParams(PublicKey publicKey, long j, int i, byte[] bArr) {
        return ByteBuffer.allocate(bArr.length + 77).order(ByteOrder.LITTLE_ENDIAN).put(SecureBox.encodePublicKey(publicKey)).putLong(j).putInt(i).put(bArr).array();
    }
}
