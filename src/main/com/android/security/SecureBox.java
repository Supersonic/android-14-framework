package com.android.security;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
/* loaded from: classes.dex */
public class SecureBox {
    public static final BigInteger EC_PARAM_A;
    public static final BigInteger EC_PARAM_B;
    public static final BigInteger EC_PARAM_P;
    @VisibleForTesting
    static final ECParameterSpec EC_PARAM_SPEC;
    public static final byte[] HKDF_SALT;
    public static final byte[] VERSION;
    public static final byte[] HKDF_INFO_WITH_PUBLIC_KEY = "P256 HKDF-SHA-256 AES-128-GCM".getBytes(StandardCharsets.UTF_8);
    public static final byte[] HKDF_INFO_WITHOUT_PUBLIC_KEY = "SHARED HKDF-SHA-256 AES-128-GCM".getBytes(StandardCharsets.UTF_8);
    public static final byte[] CONSTANT_01 = {1};
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final BigInteger BIG_INT_02 = BigInteger.valueOf(2);

    /* loaded from: classes.dex */
    public enum AesGcmOperation {
        ENCRYPT,
        DECRYPT
    }

    static {
        byte[] bArr = {2, 0};
        VERSION = bArr;
        HKDF_SALT = ArrayUtils.concat(new byte[][]{"SECUREBOX".getBytes(StandardCharsets.UTF_8), bArr});
        BigInteger bigInteger = new BigInteger("ffffffff00000001000000000000000000000000ffffffffffffffffffffffff", 16);
        EC_PARAM_P = bigInteger;
        BigInteger subtract = bigInteger.subtract(new BigInteger("3"));
        EC_PARAM_A = subtract;
        BigInteger bigInteger2 = new BigInteger("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b", 16);
        EC_PARAM_B = bigInteger2;
        EC_PARAM_SPEC = new ECParameterSpec(new EllipticCurve(new ECFieldFp(bigInteger), subtract, bigInteger2), new ECPoint(new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16), new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16)), new BigInteger("ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551", 16), 1);
    }

    public static KeyPair genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        try {
            try {
                keyPairGenerator.initialize(new ECGenParameterSpec("prime256v1"));
                return keyPairGenerator.generateKeyPair();
            } catch (InvalidAlgorithmParameterException e) {
                throw new NoSuchAlgorithmException("Unable to find the NIST P-256 curve", e);
            }
        } catch (InvalidAlgorithmParameterException unused) {
            keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
            return keyPairGenerator.generateKeyPair();
        }
    }

    public static byte[] encrypt(PublicKey publicKey, byte[] bArr, byte[] bArr2, byte[] bArr3) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyPair genKeyPair;
        byte[] dhComputeSecret;
        byte[] bArr4;
        byte[] emptyByteArrayIfNull = emptyByteArrayIfNull(bArr);
        if (publicKey == null && emptyByteArrayIfNull.length == 0) {
            throw new IllegalArgumentException("Both the public key and shared secret are empty");
        }
        byte[] emptyByteArrayIfNull2 = emptyByteArrayIfNull(bArr2);
        byte[] emptyByteArrayIfNull3 = emptyByteArrayIfNull(bArr3);
        if (publicKey == null) {
            dhComputeSecret = EMPTY_BYTE_ARRAY;
            bArr4 = HKDF_INFO_WITHOUT_PUBLIC_KEY;
            genKeyPair = null;
        } else {
            genKeyPair = genKeyPair();
            dhComputeSecret = dhComputeSecret(genKeyPair.getPrivate(), publicKey);
            bArr4 = HKDF_INFO_WITH_PUBLIC_KEY;
        }
        byte[] genRandomNonce = genRandomNonce();
        byte[] aesGcmEncrypt = aesGcmEncrypt(hkdfDeriveKey(ArrayUtils.concat(new byte[][]{dhComputeSecret, emptyByteArrayIfNull}), HKDF_SALT, bArr4), genRandomNonce, emptyByteArrayIfNull3, emptyByteArrayIfNull2);
        if (genKeyPair == null) {
            return ArrayUtils.concat(new byte[][]{VERSION, genRandomNonce, aesGcmEncrypt});
        }
        return ArrayUtils.concat(new byte[][]{VERSION, encodePublicKey(genKeyPair.getPublic()), genRandomNonce, aesGcmEncrypt});
    }

    public static byte[] decrypt(PrivateKey privateKey, byte[] bArr, byte[] bArr2, byte[] bArr3) throws NoSuchAlgorithmException, InvalidKeyException, AEADBadTagException {
        byte[] dhComputeSecret;
        byte[] bArr4;
        byte[] emptyByteArrayIfNull = emptyByteArrayIfNull(bArr);
        if (privateKey == null && emptyByteArrayIfNull.length == 0) {
            throw new IllegalArgumentException("Both the private key and shared secret are empty");
        }
        byte[] emptyByteArrayIfNull2 = emptyByteArrayIfNull(bArr2);
        if (bArr3 == null) {
            throw new NullPointerException("Encrypted payload must not be null.");
        }
        ByteBuffer wrap = ByteBuffer.wrap(bArr3);
        byte[] bArr5 = VERSION;
        if (!Arrays.equals(readEncryptedPayload(wrap, bArr5.length), bArr5)) {
            throw new AEADBadTagException("The payload was not encrypted by SecureBox v2");
        }
        if (privateKey == null) {
            dhComputeSecret = EMPTY_BYTE_ARRAY;
            bArr4 = HKDF_INFO_WITHOUT_PUBLIC_KEY;
        } else {
            dhComputeSecret = dhComputeSecret(privateKey, decodePublicKey(readEncryptedPayload(wrap, 65)));
            bArr4 = HKDF_INFO_WITH_PUBLIC_KEY;
        }
        return aesGcmDecrypt(hkdfDeriveKey(ArrayUtils.concat(new byte[][]{dhComputeSecret, emptyByteArrayIfNull}), HKDF_SALT, bArr4), readEncryptedPayload(wrap, 12), readEncryptedPayload(wrap, wrap.remaining()), emptyByteArrayIfNull2);
    }

    public static byte[] readEncryptedPayload(ByteBuffer byteBuffer, int i) throws AEADBadTagException {
        byte[] bArr = new byte[i];
        try {
            byteBuffer.get(bArr);
            return bArr;
        } catch (BufferUnderflowException unused) {
            throw new AEADBadTagException("The encrypted payload is too short");
        }
    }

    public static byte[] dhComputeSecret(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        try {
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
            return keyAgreement.generateSecret();
        } catch (RuntimeException e) {
            throw new InvalidKeyException(e);
        }
    }

    public static SecretKey hkdfDeriveKey(byte[] bArr, byte[] bArr2, byte[] bArr3) throws NoSuchAlgorithmException {
        Mac mac = Mac.getInstance("HmacSHA256");
        try {
            mac.init(new SecretKeySpec(bArr2, "HmacSHA256"));
            try {
                mac.init(new SecretKeySpec(mac.doFinal(bArr), "HmacSHA256"));
                mac.update(bArr3);
                return new SecretKeySpec(Arrays.copyOf(mac.doFinal(CONSTANT_01), 16), "AES");
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        } catch (InvalidKeyException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static byte[] aesGcmEncrypt(SecretKey secretKey, byte[] bArr, byte[] bArr2, byte[] bArr3) throws NoSuchAlgorithmException, InvalidKeyException {
        try {
            return aesGcmInternal(AesGcmOperation.ENCRYPT, secretKey, bArr, bArr2, bArr3);
        } catch (AEADBadTagException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] aesGcmDecrypt(SecretKey secretKey, byte[] bArr, byte[] bArr2, byte[] bArr3) throws NoSuchAlgorithmException, InvalidKeyException, AEADBadTagException {
        return aesGcmInternal(AesGcmOperation.DECRYPT, secretKey, bArr, bArr2, bArr3);
    }

    public static byte[] aesGcmInternal(AesGcmOperation aesGcmOperation, SecretKey secretKey, byte[] bArr, byte[] bArr2, byte[] bArr3) throws NoSuchAlgorithmException, InvalidKeyException, AEADBadTagException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gCMParameterSpec = new GCMParameterSpec(128, bArr);
            try {
                if (aesGcmOperation == AesGcmOperation.DECRYPT) {
                    cipher.init(2, secretKey, gCMParameterSpec);
                } else {
                    cipher.init(1, secretKey, gCMParameterSpec);
                }
                try {
                    cipher.updateAAD(bArr3);
                    return cipher.doFinal(bArr2);
                } catch (AEADBadTagException e) {
                    throw e;
                } catch (BadPaddingException | IllegalBlockSizeException e2) {
                    throw new RuntimeException(e2);
                }
            } catch (InvalidAlgorithmParameterException e3) {
                throw new RuntimeException(e3);
            }
        } catch (NoSuchPaddingException e4) {
            throw new RuntimeException(e4);
        }
    }

    public static byte[] encodePublicKey(PublicKey publicKey) {
        ECPoint w = ((ECPublicKey) publicKey).getW();
        byte[] byteArray = w.getAffineX().toByteArray();
        byte[] byteArray2 = w.getAffineY().toByteArray();
        byte[] bArr = new byte[65];
        System.arraycopy(byteArray2, 0, bArr, 65 - byteArray2.length, byteArray2.length);
        System.arraycopy(byteArray, 0, bArr, 33 - byteArray.length, byteArray.length);
        bArr[0] = 4;
        return bArr;
    }

    public static PublicKey decodePublicKey(byte[] bArr) throws NoSuchAlgorithmException, InvalidKeyException {
        BigInteger bigInteger = new BigInteger(1, Arrays.copyOfRange(bArr, 1, 33));
        BigInteger bigInteger2 = new BigInteger(1, Arrays.copyOfRange(bArr, 33, 65));
        validateEcPoint(bigInteger, bigInteger2);
        try {
            return KeyFactory.getInstance("EC").generatePublic(new ECPublicKeySpec(new ECPoint(bigInteger, bigInteger2), EC_PARAM_SPEC));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static void validateEcPoint(BigInteger bigInteger, BigInteger bigInteger2) throws InvalidKeyException {
        BigInteger bigInteger3 = EC_PARAM_P;
        if (bigInteger.compareTo(bigInteger3) >= 0 || bigInteger2.compareTo(bigInteger3) >= 0 || bigInteger.signum() == -1 || bigInteger2.signum() == -1) {
            throw new InvalidKeyException("Point lies outside of the expected curve");
        }
        BigInteger bigInteger4 = BIG_INT_02;
        if (!bigInteger2.modPow(bigInteger4, bigInteger3).equals(bigInteger.modPow(bigInteger4, bigInteger3).add(EC_PARAM_A).mod(bigInteger3).multiply(bigInteger).add(EC_PARAM_B).mod(bigInteger3))) {
            throw new InvalidKeyException("Point lies outside of the expected curve");
        }
    }

    public static byte[] genRandomNonce() throws NoSuchAlgorithmException {
        byte[] bArr = new byte[12];
        new SecureRandom().nextBytes(bArr);
        return bArr;
    }

    public static byte[] emptyByteArrayIfNull(byte[] bArr) {
        return bArr == null ? EMPTY_BYTE_ARRAY : bArr;
    }
}
