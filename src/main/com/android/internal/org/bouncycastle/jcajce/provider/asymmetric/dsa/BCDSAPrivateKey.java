package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dsa;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.DSAParameter;
import com.android.internal.org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPrivateKeySpec;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class BCDSAPrivateKey implements DSAPrivateKey, PKCS12BagAttributeCarrier {
    private static final long serialVersionUID = -4677259546958385734L;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();
    private transient DSAParams dsaSpec;

    /* renamed from: x */
    private BigInteger f804x;

    protected BCDSAPrivateKey() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDSAPrivateKey(DSAPrivateKey key) {
        this.f804x = key.getX();
        this.dsaSpec = key.getParams();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDSAPrivateKey(DSAPrivateKeySpec spec) {
        this.f804x = spec.getX();
        this.dsaSpec = new DSAParameterSpec(spec.getP(), spec.getQ(), spec.getG());
    }

    public BCDSAPrivateKey(PrivateKeyInfo info) throws IOException {
        DSAParameter params = DSAParameter.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        ASN1Integer derX = (ASN1Integer) info.parsePrivateKey();
        this.f804x = derX.getValue();
        this.dsaSpec = new DSAParameterSpec(params.getP(), params.getQ(), params.getG());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDSAPrivateKey(DSAPrivateKeyParameters params) {
        this.f804x = params.getX();
        this.dsaSpec = new DSAParameterSpec(params.getParameters().getP(), params.getParameters().getQ(), params.getParameters().getG());
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "DSA";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG()).toASN1Primitive()), new ASN1Integer(getX()));
    }

    @Override // java.security.interfaces.DSAKey
    public DSAParams getParams() {
        return this.dsaSpec;
    }

    @Override // java.security.interfaces.DSAPrivateKey
    public BigInteger getX() {
        return this.f804x;
    }

    public boolean equals(Object o) {
        if (o instanceof DSAPrivateKey) {
            DSAPrivateKey other = (DSAPrivateKey) o;
            return getX().equals(other.getX()) && getParams().getG().equals(other.getParams().getG()) && getParams().getP().equals(other.getParams().getP()) && getParams().getQ().equals(other.getParams().getQ());
        }
        return false;
    }

    public int hashCode() {
        return ((getX().hashCode() ^ getParams().getG().hashCode()) ^ getParams().getP().hashCode()) ^ getParams().getQ().hashCode();
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

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.dsaSpec = new DSAParameterSpec((BigInteger) in.readObject(), (BigInteger) in.readObject(), (BigInteger) in.readObject());
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.dsaSpec.getP());
        out.writeObject(this.dsaSpec.getQ());
        out.writeObject(this.dsaSpec.getG());
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        BigInteger y = getParams().getG().modPow(this.f804x, getParams().getP());
        buf.append("DSA Private Key [").append(DSAUtil.generateKeyFingerprint(y, getParams())).append(NavigationBarInflaterView.SIZE_MOD_END).append(nl);
        buf.append("            Y: ").append(y.toString(16)).append(nl);
        return buf.toString();
    }
}
