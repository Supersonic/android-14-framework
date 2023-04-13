package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p023ec;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.p018x9.X962Parameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECPoint;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9IntegerConverter;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.crypto.params.ECDomainParameters;
import com.android.internal.org.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import com.android.internal.org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import com.android.internal.org.bouncycastle.jce.interfaces.ECPointEncoder;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.util.Properties;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey */
/* loaded from: classes4.dex */
public class BCECPublicKey implements ECPublicKey, com.android.internal.org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
    static final long serialVersionUID = 2422789860422731812L;
    private String algorithm;
    private transient ProviderConfiguration configuration;
    private transient ECPublicKeyParameters ecPublicKey;
    private transient ECParameterSpec ecSpec;
    private boolean withCompression;

    public BCECPublicKey(String algorithm, BCECPublicKey key) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        this.ecPublicKey = key.ecPublicKey;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.configuration = key.configuration;
    }

    public BCECPublicKey(String algorithm, ECPublicKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        ECParameterSpec params = spec.getParams();
        this.ecSpec = params;
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(params, spec.getW()), EC5Util.getDomainParameters(configuration, spec.getParams()));
        this.configuration = configuration;
    }

    public BCECPublicKey(String algorithm, com.android.internal.org.bouncycastle.jce.spec.ECPublicKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecPublicKey = new ECPublicKeyParameters(spec.getQ(), ECUtil.getDomainParameters(configuration, spec.getParams()));
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
        } else {
            com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec s = configuration.getEcImplicitlyCa();
            this.ecPublicKey = new ECPublicKeyParameters(s.getCurve().createPoint(spec.getQ().getAffineXCoord().toBigInteger(), spec.getQ().getAffineYCoord().toBigInteger()), EC5Util.getDomainParameters(configuration, null));
            this.ecSpec = null;
        }
        this.configuration = configuration;
    }

    public BCECPublicKey(String algorithm, ECPublicKeyParameters params, ECParameterSpec spec, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = createSpec(ellipticCurve, dp);
        } else {
            this.ecSpec = spec;
        }
        this.configuration = configuration;
    }

    public BCECPublicKey(String algorithm, ECPublicKeyParameters params, com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec spec, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = createSpec(ellipticCurve, dp);
        } else {
            EllipticCurve ellipticCurve2 = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve2, spec);
        }
        this.ecPublicKey = params;
        this.configuration = configuration;
    }

    public BCECPublicKey(String algorithm, ECPublicKeyParameters params, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        this.ecSpec = null;
        this.configuration = configuration;
    }

    public BCECPublicKey(ECPublicKey key, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = key.getAlgorithm();
        ECParameterSpec params = key.getParams();
        this.ecSpec = params;
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(params, key.getW()), EC5Util.getDomainParameters(configuration, key.getParams()));
        this.configuration = configuration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCECPublicKey(String algorithm, SubjectPublicKeyInfo info, ProviderConfiguration configuration) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        this.configuration = configuration;
        populateFromPubKeyInfo(info);
    }

    private ECParameterSpec createSpec(EllipticCurve ellipticCurve, ECDomainParameters dp) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void populateFromPubKeyInfo(SubjectPublicKeyInfo info) {
        X962Parameters params = X962Parameters.getInstance(info.getAlgorithm().getParameters());
        ECCurve curve = EC5Util.getCurve(this.configuration, params);
        this.ecSpec = EC5Util.convertToSpec(params, curve);
        DERBitString bits = info.getPublicKeyData();
        byte[] data = bits.getBytes();
        ASN1OctetString key = new DEROctetString(data);
        if (data[0] == 4 && data[1] == data.length - 2 && (data[2] == 2 || data[2] == 3)) {
            int qLength = new X9IntegerConverter().getByteLength(curve);
            if (qLength >= data.length - 3) {
                try {
                    key = (ASN1OctetString) ASN1Primitive.fromByteArray(data);
                } catch (IOException e) {
                    throw new IllegalArgumentException("error recovering public key");
                }
            }
        }
        X9ECPoint derQ = new X9ECPoint(curve, key);
        this.ecPublicKey = new ECPublicKeyParameters(derQ.getPoint(), ECUtil.getDomainParameters(this.configuration, params));
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return "X.509";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        boolean compress = this.withCompression || Properties.isOverrideSet("com.android.internal.org.bouncycastle.ec.enable_pc");
        AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, ECUtils.getDomainParametersFromName(this.ecSpec, compress));
        byte[] pubKeyOctets = this.ecPublicKey.getQ().getEncoded(compress);
        return KeyUtil.getEncodedSubjectPublicKeyInfo(algId, pubKeyOctets);
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

    @Override // java.security.interfaces.ECPublicKey
    public ECPoint getW() {
        return EC5Util.convertPoint(this.ecPublicKey.getQ());
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.ECPublicKey
    public com.android.internal.org.bouncycastle.math.p025ec.ECPoint getQ() {
        com.android.internal.org.bouncycastle.math.p025ec.ECPoint q = this.ecPublicKey.getQ();
        if (this.ecSpec == null) {
            return q.getDetachedPoint();
        }
        return q;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ECPublicKeyParameters engineGetKeyParameters() {
        return this.ecPublicKey;
    }

    com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        ECParameterSpec eCParameterSpec = this.ecSpec;
        if (eCParameterSpec != null) {
            return EC5Util.convertSpec(eCParameterSpec);
        }
        return this.configuration.getEcImplicitlyCa();
    }

    public String toString() {
        return ECUtil.publicKeyToString(KeyProperties.KEY_ALGORITHM_EC, this.ecPublicKey.getQ(), engineGetSpec());
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.ECPointEncoder
    public void setPointFormat(String style) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(style);
    }

    public boolean equals(Object o) {
        if (o instanceof BCECPublicKey) {
            BCECPublicKey other = (BCECPublicKey) o;
            return this.ecPublicKey.getQ().equals(other.ecPublicKey.getQ()) && engineGetSpec().equals(other.engineGetSpec());
        }
        return false;
    }

    public int hashCode() {
        return this.ecPublicKey.getQ().hashCode() ^ engineGetSpec().hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        byte[] enc = (byte[]) in.readObject();
        this.configuration = BouncyCastleProvider.CONFIGURATION;
        populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getEncoded());
    }
}
