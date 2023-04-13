package android.security.keystore2;

import android.p008os.storage.VolumeInfo;
import android.security.KeyStore;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
/* loaded from: classes3.dex */
public class AndroidKeyStoreKeyFactorySpi extends KeyFactorySpi {
    private final KeyStore mKeyStore = KeyStore.getInstance();

    @Override // java.security.KeyFactorySpi
    protected <T extends KeySpec> T engineGetKeySpec(Key key, Class<T> keySpecClass) throws InvalidKeySpecException {
        if (key == null) {
            throw new InvalidKeySpecException("key == null");
        }
        if (!(key instanceof AndroidKeyStorePrivateKey) && !(key instanceof AndroidKeyStorePublicKey)) {
            throw new InvalidKeySpecException("Unsupported key type: " + key.getClass().getName() + ". This KeyFactory supports only Android Keystore asymmetric keys");
        }
        if (keySpecClass == null) {
            throw new InvalidKeySpecException("keySpecClass == null");
        }
        if (KeyInfo.class.equals(keySpecClass)) {
            if (!(key instanceof AndroidKeyStorePrivateKey)) {
                throw new InvalidKeySpecException("Unsupported key type: " + key.getClass().getName() + ". KeyInfo can be obtained only for Android Keystore private keys");
            }
            AndroidKeyStorePrivateKey keystorePrivateKey = (AndroidKeyStorePrivateKey) key;
            T result = AndroidKeyStoreSecretKeyFactorySpi.getKeyInfo(keystorePrivateKey);
            return result;
        } else if (X509EncodedKeySpec.class.equals(keySpecClass)) {
            if (!(key instanceof AndroidKeyStorePublicKey)) {
                throw new InvalidKeySpecException("Unsupported key type: " + key.getClass().getName() + ". X509EncodedKeySpec can be obtained only for Android Keystore public keys");
            }
            T result2 = new X509EncodedKeySpec(((AndroidKeyStorePublicKey) key).getEncoded());
            return result2;
        } else if (PKCS8EncodedKeySpec.class.equals(keySpecClass)) {
            if (key instanceof AndroidKeyStorePrivateKey) {
                throw new InvalidKeySpecException("Key material export of Android Keystore private keys is not supported");
            }
            throw new InvalidKeySpecException("Cannot export key material of public key in PKCS#8 format. Only X.509 format (X509EncodedKeySpec) supported for public keys.");
        } else {
            boolean equals = RSAPublicKeySpec.class.equals(keySpecClass);
            String str = VolumeInfo.ID_PRIVATE_INTERNAL;
            if (equals) {
                if (key instanceof AndroidKeyStoreRSAPublicKey) {
                    AndroidKeyStoreRSAPublicKey rsaKey = (AndroidKeyStoreRSAPublicKey) key;
                    T result3 = new RSAPublicKeySpec(rsaKey.getModulus(), rsaKey.getPublicExponent());
                    return result3;
                }
                StringBuilder append = new StringBuilder().append("Obtaining RSAPublicKeySpec not supported for ").append(key.getAlgorithm()).append(" ");
                if (!(key instanceof AndroidKeyStorePrivateKey)) {
                    str = "public";
                }
                throw new InvalidKeySpecException(append.append(str).append(" key").toString());
            } else if (ECPublicKeySpec.class.equals(keySpecClass)) {
                if (key instanceof AndroidKeyStoreECPublicKey) {
                    AndroidKeyStoreECPublicKey ecKey = (AndroidKeyStoreECPublicKey) key;
                    T result4 = new ECPublicKeySpec(ecKey.getW(), ecKey.getParams());
                    return result4;
                }
                StringBuilder append2 = new StringBuilder().append("Obtaining ECPublicKeySpec not supported for ").append(key.getAlgorithm()).append(" ");
                if (!(key instanceof AndroidKeyStorePrivateKey)) {
                    str = "public";
                }
                throw new InvalidKeySpecException(append2.append(str).append(" key").toString());
            } else {
                throw new InvalidKeySpecException("Unsupported key spec: " + keySpecClass.getName());
            }
        }
    }

    @Override // java.security.KeyFactorySpi
    protected PrivateKey engineGeneratePrivate(KeySpec spec) throws InvalidKeySpecException {
        throw new InvalidKeySpecException("To generate a key pair in Android Keystore, use KeyPairGenerator initialized with " + KeyGenParameterSpec.class.getName());
    }

    @Override // java.security.KeyFactorySpi
    protected PublicKey engineGeneratePublic(KeySpec spec) throws InvalidKeySpecException {
        throw new InvalidKeySpecException("To generate a key pair in Android Keystore, use KeyPairGenerator initialized with " + KeyGenParameterSpec.class.getName());
    }

    @Override // java.security.KeyFactorySpi
    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        }
        if (!(key instanceof AndroidKeyStorePrivateKey) && !(key instanceof AndroidKeyStorePublicKey)) {
            throw new InvalidKeyException("To import a key into Android Keystore, use KeyStore.setEntry");
        }
        return key;
    }
}
