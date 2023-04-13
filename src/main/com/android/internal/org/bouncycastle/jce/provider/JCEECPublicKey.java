package com.android.internal.org.bouncycastle.jce.provider;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Null;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.p018x9.X962Parameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ECParameters;
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
import com.android.internal.org.bouncycastle.jce.interfaces.ECPointEncoder;
import com.android.internal.org.bouncycastle.jce.spec.ECNamedCurveSpec;
import com.android.internal.org.bouncycastle.math.p025ec.ECCurve;
import com.android.internal.org.bouncycastle.math.p025ec.ECPoint;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
/* loaded from: classes4.dex */
public class JCEECPublicKey implements ECPublicKey, com.android.internal.org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
    private String algorithm;
    private ECParameterSpec ecSpec;

    /* renamed from: q */
    private ECPoint f821q;
    private boolean withCompression;

    public JCEECPublicKey(String algorithm, JCEECPublicKey key) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        this.f821q = key.f821q;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
    }

    public JCEECPublicKey(String algorithm, ECPublicKeySpec spec) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        ECParameterSpec params = spec.getParams();
        this.ecSpec = params;
        this.f821q = EC5Util.convertPoint(params, spec.getW());
    }

    public JCEECPublicKey(String algorithm, com.android.internal.org.bouncycastle.jce.spec.ECPublicKeySpec spec) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        this.f821q = spec.getQ();
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
            return;
        }
        if (this.f821q.getCurve() == null) {
            com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec s = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            this.f821q = s.getCurve().createPoint(this.f821q.getAffineXCoord().toBigInteger(), this.f821q.getAffineYCoord().toBigInteger());
        }
        this.ecSpec = null;
    }

    public JCEECPublicKey(String algorithm, ECPublicKeyParameters params, ECParameterSpec spec) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.f821q = params.getQ();
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = createSpec(ellipticCurve, dp);
            return;
        }
        this.ecSpec = spec;
    }

    public JCEECPublicKey(String algorithm, ECPublicKeyParameters params, com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec spec) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.f821q = params.getQ();
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = createSpec(ellipticCurve, dp);
            return;
        }
        EllipticCurve ellipticCurve2 = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
        this.ecSpec = EC5Util.convertSpec(ellipticCurve2, spec);
    }

    public JCEECPublicKey(String algorithm, ECPublicKeyParameters params) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = algorithm;
        this.f821q = params.getQ();
        this.ecSpec = null;
    }

    private ECParameterSpec createSpec(EllipticCurve ellipticCurve, ECDomainParameters dp) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(dp.getG()), dp.getN(), dp.getH().intValue());
    }

    public JCEECPublicKey(ECPublicKey key) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        this.algorithm = key.getAlgorithm();
        ECParameterSpec params = key.getParams();
        this.ecSpec = params;
        this.f821q = EC5Util.convertPoint(params, key.getW());
    }

    JCEECPublicKey(SubjectPublicKeyInfo info) {
        this.algorithm = KeyProperties.KEY_ALGORITHM_EC;
        populateFromPubKeyInfo(info);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void populateFromPubKeyInfo(SubjectPublicKeyInfo info) {
        ECCurve curve;
        AlgorithmIdentifier algID = info.getAlgorithm();
        X962Parameters params = X962Parameters.getInstance(algID.getParameters());
        if (params.isNamedCurve()) {
            ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) params.getParameters();
            X9ECParameters ecP = ECUtil.getNamedCurveByOid(oid);
            curve = ecP.getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecP.getSeed());
            this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(oid), ellipticCurve, EC5Util.convertPoint(ecP.getG()), ecP.getN(), ecP.getH());
        } else if (params.isImplicitlyCA()) {
            this.ecSpec = null;
            curve = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve();
        } else {
            X9ECParameters ecP2 = X9ECParameters.getInstance(params.getParameters());
            curve = ecP2.getCurve();
            EllipticCurve ellipticCurve2 = EC5Util.convertCurve(curve, ecP2.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve2, EC5Util.convertPoint(ecP2.getG()), ecP2.getN(), ecP2.getH().intValue());
        }
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
        this.f821q = derQ.getPoint();
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
        ASN1Encodable params;
        ECParameterSpec eCParameterSpec = this.ecSpec;
        if (eCParameterSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec) eCParameterSpec).getName());
            if (curveOid == null) {
                curveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec) this.ecSpec).getName());
            }
            params = new X962Parameters(curveOid);
        } else if (eCParameterSpec == null) {
            params = new X962Parameters((ASN1Null) DERNull.INSTANCE);
        } else {
            ECCurve curve = EC5Util.convertCurve(eCParameterSpec.getCurve());
            X9ECParameters ecP = new X9ECParameters(curve, new X9ECPoint(EC5Util.convertPoint(curve, this.ecSpec.getGenerator()), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
            params = new X962Parameters(ecP);
        }
        byte[] pubKeyOctets = getQ().getEncoded(this.withCompression);
        SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), pubKeyOctets);
        return KeyUtil.getEncodedSubjectPublicKeyInfo(info);
    }

    private void extractBytes(byte[] encKey, int offSet, BigInteger bI) {
        byte[] val = bI.toByteArray();
        if (val.length < 32) {
            byte[] tmp = new byte[32];
            System.arraycopy(val, 0, tmp, tmp.length - val.length, val.length);
            val = tmp;
        }
        for (int i = 0; i != 32; i++) {
            encKey[offSet + i] = val[(val.length - 1) - i];
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

    @Override // java.security.interfaces.ECPublicKey
    public java.security.spec.ECPoint getW() {
        return EC5Util.convertPoint(this.f821q);
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.ECPublicKey
    public ECPoint getQ() {
        if (this.ecSpec == null) {
            return this.f821q.getDetachedPoint();
        }
        return this.f821q;
    }

    public ECPoint engineGetQ() {
        return this.f821q;
    }

    com.android.internal.org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        ECParameterSpec eCParameterSpec = this.ecSpec;
        if (eCParameterSpec != null) {
            return EC5Util.convertSpec(eCParameterSpec);
        }
        return BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("EC Public Key").append(nl);
        buf.append("            X: ").append(this.f821q.getAffineXCoord().toBigInteger().toString(16)).append(nl);
        buf.append("            Y: ").append(this.f821q.getAffineYCoord().toBigInteger().toString(16)).append(nl);
        return buf.toString();
    }

    @Override // com.android.internal.org.bouncycastle.jce.interfaces.ECPointEncoder
    public void setPointFormat(String style) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(style);
    }

    public boolean equals(Object o) {
        if (o instanceof JCEECPublicKey) {
            JCEECPublicKey other = (JCEECPublicKey) o;
            return engineGetQ().equals(other.engineGetQ()) && engineGetSpec().equals(other.engineGetSpec());
        }
        return false;
    }

    public int hashCode() {
        return engineGetQ().hashCode() ^ engineGetSpec().hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        byte[] enc = (byte[]) in.readObject();
        populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
        this.algorithm = (String) in.readObject();
        this.withCompression = in.readBoolean();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(getEncoded());
        out.writeObject(this.algorithm);
        out.writeBoolean(this.withCompression);
    }
}
