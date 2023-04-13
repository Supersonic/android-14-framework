package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa;

import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import com.android.internal.org.bouncycastle.util.Fingerprint;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
/* loaded from: classes4.dex */
public class RSAUtil {
    public static final ASN1ObjectIdentifier[] rsaOids = {PKCSObjectIdentifiers.rsaEncryption, X509ObjectIdentifiers.id_ea_rsa, PKCSObjectIdentifiers.id_RSAES_OAEP, PKCSObjectIdentifiers.id_RSASSA_PSS};

    public static boolean isRsaOid(ASN1ObjectIdentifier algOid) {
        int i = 0;
        while (true) {
            ASN1ObjectIdentifier[] aSN1ObjectIdentifierArr = rsaOids;
            if (i != aSN1ObjectIdentifierArr.length) {
                if (!algOid.equals((ASN1Primitive) aSN1ObjectIdentifierArr[i])) {
                    i++;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RSAKeyParameters generatePublicKeyParameter(RSAPublicKey key) {
        if (key instanceof BCRSAPublicKey) {
            return ((BCRSAPublicKey) key).engineGetKeyParameters();
        }
        return new RSAKeyParameters(false, key.getModulus(), key.getPublicExponent());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static RSAKeyParameters generatePrivateKeyParameter(RSAPrivateKey key) {
        if (key instanceof BCRSAPrivateKey) {
            return ((BCRSAPrivateKey) key).engineGetKeyParameters();
        }
        if (key instanceof RSAPrivateCrtKey) {
            RSAPrivateCrtKey k = (RSAPrivateCrtKey) key;
            return new RSAPrivateCrtKeyParameters(k.getModulus(), k.getPublicExponent(), k.getPrivateExponent(), k.getPrimeP(), k.getPrimeQ(), k.getPrimeExponentP(), k.getPrimeExponentQ(), k.getCrtCoefficient());
        }
        return new RSAKeyParameters(true, key.getModulus(), key.getPrivateExponent());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateKeyFingerprint(BigInteger modulus) {
        return new Fingerprint(modulus.toByteArray()).toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateExponentFingerprint(BigInteger exponent) {
        return new Fingerprint(exponent.toByteArray(), 32).toString();
    }
}
