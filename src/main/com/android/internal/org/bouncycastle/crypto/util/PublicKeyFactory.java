package com.android.internal.org.bouncycastle.crypto.util;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.p018x9.DHPublicKey;
import com.android.internal.org.bouncycastle.asn1.p018x9.DomainParameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.ECNamedCurveTable;
import com.android.internal.org.bouncycastle.asn1.p018x9.ValidationParams;
import com.android.internal.org.bouncycastle.asn1.p018x9.X962Parameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECPoint;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9IntegerConverter;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.DHParameter;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.RSAPublicKey;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.DSAParameter;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import com.android.internal.org.bouncycastle.crypto.p019ec.CustomNamedCurves;
import com.android.internal.org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.android.internal.org.bouncycastle.crypto.params.DHParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHPublicKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHValidationParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAParameters;
import com.android.internal.org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECNamedDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyParameters;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class PublicKeyFactory {
    private static Map converters;

    static {
        HashMap hashMap = new HashMap();
        converters = hashMap;
        hashMap.put(PKCSObjectIdentifiers.rsaEncryption, new RSAConverter());
        converters.put(PKCSObjectIdentifiers.id_RSASSA_PSS, new RSAConverter());
        converters.put(X509ObjectIdentifiers.id_ea_rsa, new RSAConverter());
        converters.put(X9ObjectIdentifiers.dhpublicnumber, new DHPublicNumberConverter());
        converters.put(PKCSObjectIdentifiers.dhKeyAgreement, new DHAgreementConverter());
        converters.put(X9ObjectIdentifiers.id_dsa, new DSAConverter());
        converters.put(OIWObjectIdentifiers.dsaWithSHA1, new DSAConverter());
        converters.put(X9ObjectIdentifiers.id_ecPublicKey, new ECConverter());
    }

    public static AsymmetricKeyParameter createKey(byte[] keyInfoData) throws IOException {
        return createKey(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(keyInfoData)));
    }

    public static AsymmetricKeyParameter createKey(InputStream inStr) throws IOException {
        return createKey(SubjectPublicKeyInfo.getInstance(new ASN1InputStream(inStr).readObject()));
    }

    public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo keyInfo) throws IOException {
        return createKey(keyInfo, null);
    }

    public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo keyInfo, Object defaultParams) throws IOException {
        AlgorithmIdentifier algID = keyInfo.getAlgorithm();
        SubjectPublicKeyInfoConverter converter = (SubjectPublicKeyInfoConverter) converters.get(algID.getAlgorithm());
        if (converter == null) {
            throw new IOException("algorithm identifier in public key not recognised: " + algID.getAlgorithm());
        }
        return converter.getPublicKeyParameters(keyInfo, defaultParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static abstract class SubjectPublicKeyInfoConverter {
        abstract AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo, Object obj) throws IOException;

        private SubjectPublicKeyInfoConverter() {
        }
    }

    /* loaded from: classes4.dex */
    private static class RSAConverter extends SubjectPublicKeyInfoConverter {
        private RSAConverter() {
            super();
        }

        @Override // com.android.internal.org.bouncycastle.crypto.util.PublicKeyFactory.SubjectPublicKeyInfoConverter
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo keyInfo, Object defaultParams) throws IOException {
            RSAPublicKey pubKey = RSAPublicKey.getInstance(keyInfo.parsePublicKey());
            return new RSAKeyParameters(false, pubKey.getModulus(), pubKey.getPublicExponent());
        }
    }

    /* loaded from: classes4.dex */
    private static class DHPublicNumberConverter extends SubjectPublicKeyInfoConverter {
        private DHPublicNumberConverter() {
            super();
        }

        @Override // com.android.internal.org.bouncycastle.crypto.util.PublicKeyFactory.SubjectPublicKeyInfoConverter
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo keyInfo, Object defaultParams) throws IOException {
            BigInteger j;
            DHValidationParameters validation;
            DHPublicKey dhPublicKey = DHPublicKey.getInstance(keyInfo.parsePublicKey());
            BigInteger y = dhPublicKey.getY();
            DomainParameters dhParams = DomainParameters.getInstance(keyInfo.getAlgorithm().getParameters());
            BigInteger p = dhParams.getP();
            BigInteger g = dhParams.getG();
            BigInteger q = dhParams.getQ();
            if (dhParams.getJ() == null) {
                j = null;
            } else {
                BigInteger j2 = dhParams.getJ();
                j = j2;
            }
            ValidationParams dhValidationParms = dhParams.getValidationParams();
            if (dhValidationParms == null) {
                validation = null;
            } else {
                byte[] seed = dhValidationParms.getSeed();
                BigInteger pgenCounter = dhValidationParms.getPgenCounter();
                DHValidationParameters validation2 = new DHValidationParameters(seed, pgenCounter.intValue());
                validation = validation2;
            }
            return new DHPublicKeyParameters(y, new DHParameters(p, g, q, j, validation));
        }
    }

    /* loaded from: classes4.dex */
    private static class DHAgreementConverter extends SubjectPublicKeyInfoConverter {
        private DHAgreementConverter() {
            super();
        }

        @Override // com.android.internal.org.bouncycastle.crypto.util.PublicKeyFactory.SubjectPublicKeyInfoConverter
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo keyInfo, Object defaultParams) throws IOException {
            DHParameter params = DHParameter.getInstance(keyInfo.getAlgorithm().getParameters());
            ASN1Integer derY = (ASN1Integer) keyInfo.parsePublicKey();
            BigInteger lVal = params.getL();
            int l = lVal == null ? 0 : lVal.intValue();
            DHParameters dhParams = new DHParameters(params.getP(), params.getG(), null, l);
            return new DHPublicKeyParameters(derY.getValue(), dhParams);
        }
    }

    /* loaded from: classes4.dex */
    private static class DSAConverter extends SubjectPublicKeyInfoConverter {
        private DSAConverter() {
            super();
        }

        @Override // com.android.internal.org.bouncycastle.crypto.util.PublicKeyFactory.SubjectPublicKeyInfoConverter
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo keyInfo, Object defaultParams) throws IOException {
            ASN1Integer derY = (ASN1Integer) keyInfo.parsePublicKey();
            ASN1Encodable de = keyInfo.getAlgorithm().getParameters();
            DSAParameters parameters = null;
            if (de != null) {
                DSAParameter params = DSAParameter.getInstance(de.toASN1Primitive());
                parameters = new DSAParameters(params.getP(), params.getQ(), params.getG());
            }
            return new DSAPublicKeyParameters(derY.getValue(), parameters);
        }
    }

    /* loaded from: classes4.dex */
    private static class ECConverter extends SubjectPublicKeyInfoConverter {
        private ECConverter() {
            super();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.android.internal.org.bouncycastle.crypto.util.PublicKeyFactory.SubjectPublicKeyInfoConverter
        AsymmetricKeyParameter getPublicKeyParameters(SubjectPublicKeyInfo keyInfo, Object defaultParams) {
            ECDomainParameters dParams;
            X962Parameters params = X962Parameters.getInstance(keyInfo.getAlgorithm().getParameters());
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) params.getParameters();
                X9ECParameters x9 = CustomNamedCurves.getByOID(oid);
                if (x9 == null) {
                    x9 = ECNamedCurveTable.getByOID(oid);
                }
                dParams = new ECNamedDomainParameters(oid, x9);
            } else if (params.isImplicitlyCA()) {
                dParams = (ECDomainParameters) defaultParams;
            } else {
                dParams = new ECDomainParameters(X9ECParameters.getInstance(params.getParameters()));
            }
            DERBitString bits = keyInfo.getPublicKeyData();
            byte[] data = bits.getBytes();
            ASN1OctetString key = new DEROctetString(data);
            if (data[0] == 4 && data[1] == data.length - 2 && (data[2] == 2 || data[2] == 3)) {
                int qLength = new X9IntegerConverter().getByteLength(dParams.getCurve());
                if (qLength >= data.length - 3) {
                    try {
                        key = (ASN1OctetString) ASN1Primitive.fromByteArray(data);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("error recovering public key");
                    }
                }
            }
            X9ECPoint derQ = new X9ECPoint(dParams.getCurve(), key);
            return new ECPublicKeyParameters(derQ.getPoint(), dParams);
        }
    }

    private static byte[] getRawKey(SubjectPublicKeyInfo keyInfo, Object defaultParams, int expectedSize) {
        byte[] result = keyInfo.getPublicKeyData().getOctets();
        if (expectedSize != result.length) {
            throw new RuntimeException("public key encoding has incorrect length");
        }
        return result;
    }
}
