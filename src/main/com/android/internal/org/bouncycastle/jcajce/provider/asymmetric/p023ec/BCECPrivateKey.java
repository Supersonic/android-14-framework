package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p023ec;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.p018x9.X962Parameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import com.android.internal.org.bouncycastle.jce.interfaces.ECPointEncoder;
import com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.jce.spec.ECPrivateKeySpec;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey */
/* loaded from: classes4.dex */
public class BCECPrivateKey implements ECPrivateKey, com.android.internal.org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
    static final long serialVersionUID = 994553197664784084L;
    private String algorithm;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    private transient ProviderConfiguration configuration;

    /* renamed from: d */
    private transient BigInteger f806d;
    private transient ECParameterSpec ecSpec;
    private transient DERBitString publicKey;
    private boolean withCompression;

    protected BCECPrivateKey() {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    public BCECPrivateKey(ECPrivateKey key, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.f806d = key.getS();
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
        this.configuration = configuration;
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.f806d = spec.getD();
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
        } else {
            this.ecSpec = null;
        }
        this.configuration = configuration;
    }

    public BCECPrivateKey(String algorithm, java.security.spec.ECPrivateKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.f806d = spec.getS();
        this.ecSpec = spec.getParams();
        this.configuration = configuration;
    }

    public BCECPrivateKey(String algorithm, BCECPrivateKey key) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.f806d = key.f806d;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.attrCarrier = key.attrCarrier;
        this.publicKey = key.publicKey;
        this.configuration = key.configuration;
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, BCECPublicKey pubKey, ECParameterSpec spec, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.f806d = params.getD();
        this.configuration = configuration;
        if (spec == null) {
            ECDomainParameters dp = params.getParameters();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
        } else {
            this.ecSpec = spec;
        }
        this.publicKey = getPublicKeyDetails(pubKey);
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, BCECPublicKey pubKey, com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec spec, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.f806d = params.getD();
        this.configuration = configuration;
        if (spec == null) {
            ECDomainParameters dp = params.getParameters();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
        } else {
            EllipticCurve ellipticCurve2 = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve2, spec);
        }
        try {
            this.publicKey = getPublicKeyDetails(pubKey);
        } catch (Exception e) {
            this.publicKey = null;
        }
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.f806d = params.getD();
        this.ecSpec = null;
        this.configuration = configuration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCECPrivateKey(String algorithm, PrivateKeyInfo info, ProviderConfiguration configuration) throws IOException {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.configuration = configuration;
        populateFromPrivKeyInfo(info);
    }

    private void populateFromPrivKeyInfo(PrivateKeyInfo info) throws IOException {
        X962Parameters params = X962Parameters.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        ECCurve curve = EC5Util.getCurve(this.configuration, params);
        this.ecSpec = EC5Util.convertToSpec(params, curve);
        ASN1Encodable privKey = info.parsePrivateKey();
        if (privKey instanceof ASN1Integer) {
            ASN1Integer derD = ASN1Integer.getInstance(privKey);
            this.f806d = derD.getValue();
            return;
        }
        com.android.internal.org.bouncycastle.asn1.sec.ECPrivateKey ec = com.android.internal.org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(privKey);
        this.f806d = ec.getKey();
        this.publicKey = ec.getPublicKey();
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        int orderBitLength;
        com.android.internal.org.bouncycastle.asn1.sec.ECPrivateKey keyStructure;
        X962Parameters params = ECUtils.getDomainParametersFromName(this.ecSpec, this.withCompression);
        ECParameterSpec eCParameterSpec = this.ecSpec;
        if (eCParameterSpec == null) {
            orderBitLength = ECUtil.getOrderBitLength(this.configuration, null, getS());
        } else {
            orderBitLength = ECUtil.getOrderBitLength(this.configuration, eCParameterSpec.getOrder(), getS());
        }
        if (this.publicKey != null) {
            keyStructure = new com.android.internal.org.bouncycastle.asn1.sec.ECPrivateKey(orderBitLength, getS(), this.publicKey, params);
        } else {
            keyStructure = new com.android.internal.org.bouncycastle.asn1.sec.ECPrivateKey(orderBitLength, getS(), params);
        }
        try {
            PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), keyStructure);
            return info.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.interfaces.ECKey
    public ECParameterSpec getParams() {
        return this.ecSpec;
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.ECKey
    public com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec getParameters() {
        ECParameterSpec eCParameterSpec = this.ecSpec;
        if (eCParameterSpec == null) {
            return null;
        }
        return EC5Util.convertSpec(eCParameterSpec);
    }

    com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        ECParameterSpec eCParameterSpec = this.ecSpec;
        if (eCParameterSpec != null) {
            return EC5Util.convertSpec(eCParameterSpec);
        }
        return this.configuration.getEcImplicitlyCa();
    }

    @Override // java.security.interfaces.ECPrivateKey
    public BigInteger getS() {
        return this.f806d;
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.ECPrivateKey
    public BigInteger getD() {
        return this.f806d;
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public void setBagAttribute(ASN1ObjectIdentifier oid, ASN1Encodable attribute) {
        this.attrCarrier.setBagAttribute(oid, attribute);
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier oid) {
        return this.attrCarrier.getBagAttribute(oid);
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public Enumeration getBagAttributeKeys() {
        return this.attrCarrier.getBagAttributeKeys();
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.ECPointEncoder
    public void setPointFormat(String style) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(style);
    }

    public boolean equals(Object o) {
        if (o instanceof BCECPrivateKey) {
            BCECPrivateKey other = (BCECPrivateKey) o;
            return getD().equals(other.getD()) && engineGetSpec().equals(other.engineGetSpec());
        }
        return false;
    }

    public int hashCode() {
        return getD().hashCode() ^ engineGetSpec().hashCode();
    }

    public String toString() {
        return ECUtil.privateKeyToString(KeyProperties.KEY_ALGORITHM_EC, this.f806d, engineGetSpec());
    }

    private DERBitString getPublicKeyDetails(BCECPublicKey pub) {
        try {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(pub.getEncoded()));
            return info.getPublicKeyData();
        } catch (IOException e) {
            return null;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        byte[] enc = (byte[]) in.readObject();
        this.configuration = BouncyCastleProvider.CONFIGURATION;
        populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getEncoded());
    }
}
