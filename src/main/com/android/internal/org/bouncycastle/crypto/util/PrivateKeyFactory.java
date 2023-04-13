package com.android.internal.org.bouncycastle.crypto.util;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.p018x9.ECNamedCurveTable;
import com.android.internal.org.bouncycastle.asn1.p018x9.X962Parameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.DHParameter;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import com.android.internal.org.bouncycastle.asn1.sec.ECPrivateKey;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.DSAParameter;
import com.android.internal.org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.p019ec.CustomNamedCurves;
import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.DHParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECNamedDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
/* loaded from: classes4.dex */
public class PrivateKeyFactory {
    public static AsymmetricKeyParameter createKey(byte[] privateKeyInfoData) throws IOException {
        return createKey(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(privateKeyInfoData)));
    }

    public static AsymmetricKeyParameter createKey(InputStream inStr) throws IOException {
        return createKey(PrivateKeyInfo.getInstance(new ASN1InputStream(inStr).readObject()));
    }

    public static AsymmetricKeyParameter createKey(PrivateKeyInfo keyInfo) throws IOException {
        ECDomainParameters dParams;
        AlgorithmIdentifier algId = keyInfo.getPrivateKeyAlgorithm();
        ASN1ObjectIdentifier algOID = algId.getAlgorithm();
        if (algOID.equals((ASN1Primitive) PKCSObjectIdentifiers.rsaEncryption) || algOID.equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS) || algOID.equals((ASN1Primitive) X509ObjectIdentifiers.id_ea_rsa)) {
            RSAPrivateKey keyStructure = RSAPrivateKey.getInstance(keyInfo.parsePrivateKey());
            return new RSAPrivateCrtKeyParameters(keyStructure.getModulus(), keyStructure.getPublicExponent(), keyStructure.getPrivateExponent(), keyStructure.getPrime1(), keyStructure.getPrime2(), keyStructure.getExponent1(), keyStructure.getExponent2(), keyStructure.getCoefficient());
        } else if (algOID.equals((ASN1Primitive) PKCSObjectIdentifiers.dhKeyAgreement)) {
            DHParameter params = DHParameter.getInstance(algId.getParameters());
            ASN1Integer derX = (ASN1Integer) keyInfo.parsePrivateKey();
            BigInteger lVal = params.getL();
            int l = lVal == null ? 0 : lVal.intValue();
            DHParameters dhParams = new DHParameters(params.getP(), params.getG(), null, l);
            return new DHPrivateKeyParameters(derX.getValue(), dhParams);
        } else if (algOID.equals((ASN1Primitive) X9ObjectIdentifiers.id_dsa)) {
            ASN1Integer derX2 = (ASN1Integer) keyInfo.parsePrivateKey();
            ASN1Encodable de = algId.getParameters();
            DSAParameters parameters = null;
            if (de != null) {
                DSAParameter params2 = DSAParameter.getInstance(de.toASN1Primitive());
                parameters = new DSAParameters(params2.getP(), params2.getQ(), params2.getG());
            }
            return new DSAPrivateKeyParameters(derX2.getValue(), parameters);
        } else if (algOID.equals((ASN1Primitive) X9ObjectIdentifiers.id_ecPublicKey)) {
            X962Parameters params3 = X962Parameters.getInstance(algId.getParameters());
            if (params3.isNamedCurve()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) params3.getParameters();
                X9ECParameters x9 = CustomNamedCurves.getByOID(oid);
                if (x9 == null) {
                    x9 = ECNamedCurveTable.getByOID(oid);
                }
                dParams = new ECNamedDomainParameters(oid, x9);
            } else {
                X9ECParameters x92 = X9ECParameters.getInstance(params3.getParameters());
                dParams = new ECDomainParameters(x92.getCurve(), x92.getG(), x92.getN(), x92.getH(), x92.getSeed());
            }
            ECPrivateKey ec = ECPrivateKey.getInstance(keyInfo.parsePrivateKey());
            BigInteger d = ec.getKey();
            return new ECPrivateKeyParameters(d, dParams);
        } else {
            throw new RuntimeException("algorithm identifier in private key not recognised");
        }
    }

    private static byte[] getRawKey(PrivateKeyInfo keyInfo, int expectedSize) throws IOException {
        byte[] result = ASN1OctetString.getInstance(keyInfo.parsePrivateKey()).getOctets();
        if (expectedSize != result.length) {
            throw new RuntimeException("private key encoding has incorrect length");
        }
        return result;
    }
}
