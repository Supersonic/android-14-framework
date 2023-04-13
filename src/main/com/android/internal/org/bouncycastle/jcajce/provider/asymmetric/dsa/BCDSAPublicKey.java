package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dsa;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.DSAParameter;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
/* loaded from: classes4.dex */
public class BCDSAPublicKey implements DSAPublicKey {
    private static BigInteger ZERO = BigInteger.valueOf(0);
    private static final long serialVersionUID = 1752452449903495175L;
    private transient DSAParams dsaSpec;
    private transient DSAPublicKeyParameters lwKeyParams;

    /* renamed from: y */
    private BigInteger f805y;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDSAPublicKey(DSAPublicKeySpec spec) {
        this.f805y = spec.getY();
        this.dsaSpec = new DSAParameterSpec(spec.getP(), spec.getQ(), spec.getG());
        this.lwKeyParams = new DSAPublicKeyParameters(this.f805y, DSAUtil.toDSAParameters(this.dsaSpec));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDSAPublicKey(DSAPublicKey key) {
        this.f805y = key.getY();
        this.dsaSpec = key.getParams();
        this.lwKeyParams = new DSAPublicKeyParameters(this.f805y, DSAUtil.toDSAParameters(this.dsaSpec));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDSAPublicKey(DSAPublicKeyParameters params) {
        this.f805y = params.getY();
        if (params.getParameters() != null) {
            this.dsaSpec = new DSAParameterSpec(params.getParameters().getP(), params.getParameters().getQ(), params.getParameters().getG());
        } else {
            this.dsaSpec = null;
        }
        this.lwKeyParams = params;
    }

    public BCDSAPublicKey(SubjectPublicKeyInfo info) {
        try {
            ASN1Integer derY = (ASN1Integer) info.parsePublicKey();
            this.f805y = derY.getValue();
            if (isNotNull(info.getAlgorithm().getParameters())) {
                DSAParameter params = DSAParameter.getInstance(info.getAlgorithm().getParameters());
                this.dsaSpec = new DSAParameterSpec(params.getP(), params.getQ(), params.getG());
            } else {
                this.dsaSpec = null;
            }
            this.lwKeyParams = new DSAPublicKeyParameters(this.f805y, DSAUtil.toDSAParameters(this.dsaSpec));
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in DSA public key");
        }
    }

    private boolean isNotNull(ASN1Encodable parameters) {
        return (parameters == null || DERNull.INSTANCE.equals(parameters.toASN1Primitive())) ? false : true;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "DSA";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "X.509";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DSAPublicKeyParameters engineGetKeyParameters() {
        return this.lwKeyParams;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        if (this.dsaSpec == null) {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), new ASN1Integer(this.f805y));
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG()).toASN1Primitive()), new ASN1Integer(this.f805y));
    }

    @Override // java.security.interfaces.DSAKey
    public DSAParams getParams() {
        return this.dsaSpec;
    }

    @Override // java.security.interfaces.DSAPublicKey
    public BigInteger getY() {
        return this.f805y;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("DSA Public Key [").append(DSAUtil.generateKeyFingerprint(this.f805y, getParams())).append(NavigationBarInflaterView.SIZE_MOD_END).append(nl);
        buf.append("            Y: ").append(getY().toString(16)).append(nl);
        return buf.toString();
    }

    public int hashCode() {
        if (this.dsaSpec != null) {
            return ((getY().hashCode() ^ getParams().getG().hashCode()) ^ getParams().getP().hashCode()) ^ getParams().getQ().hashCode();
        }
        return getY().hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof DSAPublicKey) {
            DSAPublicKey other = (DSAPublicKey) o;
            return this.dsaSpec != null ? getY().equals(other.getY()) && other.getParams() != null && getParams().getG().equals(other.getParams().getG()) && getParams().getP().equals(other.getParams().getP()) && getParams().getQ().equals(other.getParams().getQ()) : getY().equals(other.getY()) && other.getParams() == null;
        }
        return false;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        BigInteger p = (BigInteger) in.readObject();
        if (p.equals(ZERO)) {
            this.dsaSpec = null;
        } else {
            this.dsaSpec = new DSAParameterSpec(p, (BigInteger) in.readObject(), (BigInteger) in.readObject());
        }
        this.lwKeyParams = new DSAPublicKeyParameters(this.f805y, DSAUtil.toDSAParameters(this.dsaSpec));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        DSAParams dSAParams = this.dsaSpec;
        if (dSAParams == null) {
            out.writeObject(ZERO);
            return;
        }
        out.writeObject(dSAParams.getP());
        out.writeObject(this.dsaSpec.getQ());
        out.writeObject(this.dsaSpec.getG());
    }
}
