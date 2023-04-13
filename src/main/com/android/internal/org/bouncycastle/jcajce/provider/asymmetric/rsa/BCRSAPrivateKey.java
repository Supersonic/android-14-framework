package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa;

import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Enumeration;
/* loaded from: classes4.dex */
public class BCRSAPrivateKey implements RSAPrivateKey, PKCS12BagAttributeCarrier {
    private static BigInteger ZERO = BigInteger.valueOf(0);
    static final long serialVersionUID = 5110188922551353628L;
    protected transient AlgorithmIdentifier algorithmIdentifier;
    private byte[] algorithmIdentifierEnc;
    protected transient PKCS12BagAttributeCarrierImpl attrCarrier;
    protected BigInteger modulus;
    protected BigInteger privateExponent;
    protected transient RSAKeyParameters rsaPrivateKey;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateKey(RSAKeyParameters key) {
        this.algorithmIdentifierEnc = getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
        this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.modulus = key.getModulus();
        this.privateExponent = key.getExponent();
        this.rsaPrivateKey = key;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateKey(AlgorithmIdentifier algID, RSAKeyParameters key) {
        this.algorithmIdentifierEnc = getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
        this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithmIdentifier = algID;
        this.algorithmIdentifierEnc = getEncoding(algID);
        this.modulus = key.getModulus();
        this.privateExponent = key.getExponent();
        this.rsaPrivateKey = key;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateKey(RSAPrivateKeySpec spec) {
        this.algorithmIdentifierEnc = getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
        this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.modulus = spec.getModulus();
        this.privateExponent = spec.getPrivateExponent();
        this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateKey(RSAPrivateKey key) {
        this.algorithmIdentifierEnc = getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
        this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.modulus = key.getModulus();
        this.privateExponent = key.getPrivateExponent();
        this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateKey(AlgorithmIdentifier algID, com.android.internal.org.bouncycastle.asn1.pkcs.RSAPrivateKey key) {
        this.algorithmIdentifierEnc = getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
        this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithmIdentifier = algID;
        this.algorithmIdentifierEnc = getEncoding(algID);
        this.modulus = key.getModulus();
        this.privateExponent = key.getPrivateExponent();
        this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        return this.modulus;
    }

    @Override // java.security.interfaces.RSAPrivateKey
    public BigInteger getPrivateExponent() {
        return this.privateExponent;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        if (this.algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            return "RSASSA-PSS";
        }
        return KeyProperties.KEY_ALGORITHM_RSA;
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RSAKeyParameters engineGetKeyParameters() {
        return this.rsaPrivateKey;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        AlgorithmIdentifier algorithmIdentifier = this.algorithmIdentifier;
        BigInteger modulus = getModulus();
        BigInteger bigInteger = ZERO;
        BigInteger privateExponent = getPrivateExponent();
        BigInteger bigInteger2 = ZERO;
        return KeyUtil.getEncodedPrivateKeyInfo(algorithmIdentifier, new com.android.internal.org.bouncycastle.asn1.pkcs.RSAPrivateKey(modulus, bigInteger, privateExponent, bigInteger2, bigInteger2, bigInteger2, bigInteger2, bigInteger2));
    }

    public boolean equals(Object o) {
        if (o instanceof RSAPrivateKey) {
            if (o == this) {
                return true;
            }
            RSAPrivateKey key = (RSAPrivateKey) o;
            return getModulus().equals(key.getModulus()) && getPrivateExponent().equals(key.getPrivateExponent());
        }
        return false;
    }

    public int hashCode() {
        return getModulus().hashCode() ^ getPrivateExponent().hashCode();
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
        if (this.algorithmIdentifierEnc == null) {
            this.algorithmIdentifierEnc = getEncoding(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER);
        }
        this.algorithmIdentifier = AlgorithmIdentifier.getInstance(this.algorithmIdentifierEnc);
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.rsaPrivateKey = new RSAKeyParameters(true, this.modulus, this.privateExponent);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("RSA Private Key [").append(RSAUtil.generateKeyFingerprint(getModulus())).append("],[]").append(nl);
        buf.append("            modulus: ").append(getModulus().toString(16)).append(nl);
        return buf.toString();
    }

    private static byte[] getEncoding(AlgorithmIdentifier algorithmIdentifier) {
        try {
            return algorithmIdentifier.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }
}
