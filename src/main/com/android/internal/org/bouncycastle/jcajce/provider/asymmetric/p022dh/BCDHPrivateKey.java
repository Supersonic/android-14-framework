package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.p022dh;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.p018x9.DomainParameters;
import com.android.internal.org.bouncycastle.asn1.p018x9.ValidationParams;
import com.android.internal.org.bouncycastle.asn1.p018x9.X9ObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.DHParameter;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.crypto.params.DHParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import com.android.internal.org.bouncycastle.crypto.params.DHValidationParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import com.android.internal.org.bouncycastle.jcajce.spec.DHDomainParameterSpec;
import com.android.internal.org.bouncycastle.jcajce.spec.DHExtendedPrivateKeySpec;
import com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
/* renamed from: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPrivateKey */
/* loaded from: classes4.dex */
public class BCDHPrivateKey implements DHPrivateKey, PKCS12BagAttributeCarrier {
    static final long serialVersionUID = 311058815616901812L;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier = new PKCS12BagAttributeCarrierImpl();
    private transient DHPrivateKeyParameters dhPrivateKey;
    private transient DHParameterSpec dhSpec;
    private transient PrivateKeyInfo info;

    /* renamed from: x */
    private BigInteger f799x;

    protected BCDHPrivateKey() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDHPrivateKey(DHPrivateKey key) {
        this.f799x = key.getX();
        this.dhSpec = key.getParams();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDHPrivateKey(DHPrivateKeySpec spec) {
        this.f799x = spec.getX();
        if (spec instanceof DHExtendedPrivateKeySpec) {
            this.dhSpec = ((DHExtendedPrivateKeySpec) spec).getParams();
        } else {
            this.dhSpec = new DHParameterSpec(spec.getP(), spec.getG());
        }
    }

    public BCDHPrivateKey(PrivateKeyInfo info) throws IOException {
        ASN1Sequence seq = ASN1Sequence.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        ASN1Integer derX = (ASN1Integer) info.parsePrivateKey();
        ASN1ObjectIdentifier id = info.getPrivateKeyAlgorithm().getAlgorithm();
        this.info = info;
        this.f799x = derX.getValue();
        if (id.equals((ASN1Primitive) PKCSObjectIdentifiers.dhKeyAgreement)) {
            DHParameter params = DHParameter.getInstance(seq);
            if (params.getL() != null) {
                this.dhSpec = new DHParameterSpec(params.getP(), params.getG(), params.getL().intValue());
                this.dhPrivateKey = new DHPrivateKeyParameters(this.f799x, new DHParameters(params.getP(), params.getG(), null, params.getL().intValue()));
                return;
            }
            this.dhSpec = new DHParameterSpec(params.getP(), params.getG());
            this.dhPrivateKey = new DHPrivateKeyParameters(this.f799x, new DHParameters(params.getP(), params.getG()));
        } else if (id.equals((ASN1Primitive) X9ObjectIdentifiers.dhpublicnumber)) {
            DomainParameters params2 = DomainParameters.getInstance(seq);
            this.dhSpec = new DHDomainParameterSpec(params2.getP(), params2.getQ(), params2.getG(), params2.getJ(), 0);
            this.dhPrivateKey = new DHPrivateKeyParameters(this.f799x, new DHParameters(params2.getP(), params2.getG(), params2.getQ(), params2.getJ(), (DHValidationParameters) null));
        } else {
            throw new IllegalArgumentException("unknown algorithm type: " + id);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCDHPrivateKey(DHPrivateKeyParameters params) {
        this.f799x = params.getX();
        this.dhSpec = new DHDomainParameterSpec(params.getParameters());
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "DH";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        PrivateKeyInfo info;
        ValidationParams vParams;
        try {
            PrivateKeyInfo privateKeyInfo = this.info;
            if (privateKeyInfo != null) {
                return privateKeyInfo.getEncoded(ASN1Encoding.DER);
            }
            DHParameterSpec dHParameterSpec = this.dhSpec;
            if ((dHParameterSpec instanceof DHDomainParameterSpec) && ((DHDomainParameterSpec) dHParameterSpec).getQ() != null) {
                DHParameters params = ((DHDomainParameterSpec) this.dhSpec).getDomainParameters();
                DHValidationParameters validationParameters = params.getValidationParameters();
                if (validationParameters == null) {
                    vParams = null;
                } else {
                    ValidationParams vParams2 = new ValidationParams(validationParameters.getSeed(), validationParameters.getCounter());
                    vParams = vParams2;
                }
                info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.dhpublicnumber, new DomainParameters(params.getP(), params.getG(), params.getQ(), params.getJ(), vParams).toASN1Primitive()), new ASN1Integer(getX()));
            } else {
                info = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL()).toASN1Primitive()), new ASN1Integer(getX()));
            }
            return info.getEncoded(ASN1Encoding.DER);
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        return DHUtil.privateKeyToString("DH", this.f799x, new DHParameters(this.dhSpec.getP(), this.dhSpec.getG()));
    }

    @Override // javax.crypto.interfaces.DHKey
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }

    @Override // javax.crypto.interfaces.DHPrivateKey
    public BigInteger getX() {
        return this.f799x;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DHPrivateKeyParameters engineGetKeyParameters() {
        DHPrivateKeyParameters dHPrivateKeyParameters = this.dhPrivateKey;
        if (dHPrivateKeyParameters != null) {
            return dHPrivateKeyParameters;
        }
        DHParameterSpec dHParameterSpec = this.dhSpec;
        if (dHParameterSpec instanceof DHDomainParameterSpec) {
            return new DHPrivateKeyParameters(this.f799x, ((DHDomainParameterSpec) dHParameterSpec).getDomainParameters());
        }
        return new DHPrivateKeyParameters(this.f799x, new DHParameters(dHParameterSpec.getP(), this.dhSpec.getG(), null, this.dhSpec.getL()));
    }

    public boolean equals(Object o) {
        if (o instanceof DHPrivateKey) {
            DHPrivateKey other = (DHPrivateKey) o;
            return getX().equals(other.getX()) && getParams().getG().equals(other.getParams().getG()) && getParams().getP().equals(other.getParams().getP()) && getParams().getL() == other.getParams().getL();
        }
        return false;
    }

    public int hashCode() {
        return ((getX().hashCode() ^ getParams().getG().hashCode()) ^ getParams().getP().hashCode()) ^ getParams().getL();
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
        this.dhSpec = new DHParameterSpec((BigInteger) in.readObject(), (BigInteger) in.readObject(), in.readInt());
        this.info = null;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.dhSpec.getP());
        out.writeObject(this.dhSpec.getG());
        out.writeInt(this.dhSpec.getL());
    }
}
