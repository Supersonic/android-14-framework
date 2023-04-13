package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import com.android.internal.org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPrivateCrtKeySpec;
/* loaded from: classes4.dex */
public class BCRSAPrivateCrtKey extends BCRSAPrivateKey implements RSAPrivateCrtKey {
    static final long serialVersionUID = 7834723820638524718L;
    private BigInteger crtCoefficient;
    private BigInteger primeExponentP;
    private BigInteger primeExponentQ;
    private BigInteger primeP;
    private BigInteger primeQ;
    private BigInteger publicExponent;

    BCRSAPrivateCrtKey(RSAPrivateCrtKeyParameters key) {
        super(key);
        this.publicExponent = key.getPublicExponent();
        this.primeP = key.getP();
        this.primeQ = key.getQ();
        this.primeExponentP = key.getDP();
        this.primeExponentQ = key.getDQ();
        this.crtCoefficient = key.getQInv();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateCrtKey(AlgorithmIdentifier algorithmIdentifier, RSAPrivateCrtKeyParameters key) {
        super(algorithmIdentifier, key);
        this.publicExponent = key.getPublicExponent();
        this.primeP = key.getP();
        this.primeQ = key.getQ();
        this.primeExponentP = key.getDP();
        this.primeExponentQ = key.getDQ();
        this.crtCoefficient = key.getQInv();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateCrtKey(RSAPrivateCrtKeySpec spec) {
        super(new RSAPrivateCrtKeyParameters(spec.getModulus(), spec.getPublicExponent(), spec.getPrivateExponent(), spec.getPrimeP(), spec.getPrimeQ(), spec.getPrimeExponentP(), spec.getPrimeExponentQ(), spec.getCrtCoefficient()));
        this.modulus = spec.getModulus();
        this.publicExponent = spec.getPublicExponent();
        this.privateExponent = spec.getPrivateExponent();
        this.primeP = spec.getPrimeP();
        this.primeQ = spec.getPrimeQ();
        this.primeExponentP = spec.getPrimeExponentP();
        this.primeExponentQ = spec.getPrimeExponentQ();
        this.crtCoefficient = spec.getCrtCoefficient();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateCrtKey(RSAPrivateCrtKey key) {
        super(new RSAPrivateCrtKeyParameters(key.getModulus(), key.getPublicExponent(), key.getPrivateExponent(), key.getPrimeP(), key.getPrimeQ(), key.getPrimeExponentP(), key.getPrimeExponentQ(), key.getCrtCoefficient()));
        this.modulus = key.getModulus();
        this.publicExponent = key.getPublicExponent();
        this.privateExponent = key.getPrivateExponent();
        this.primeP = key.getPrimeP();
        this.primeQ = key.getPrimeQ();
        this.primeExponentP = key.getPrimeExponentP();
        this.primeExponentQ = key.getPrimeExponentQ();
        this.crtCoefficient = key.getCrtCoefficient();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateCrtKey(PrivateKeyInfo info) throws IOException {
        this(info.getPrivateKeyAlgorithm(), RSAPrivateKey.getInstance(info.parsePrivateKey()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPrivateCrtKey(RSAPrivateKey key) {
        this(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER, key);
    }

    BCRSAPrivateCrtKey(AlgorithmIdentifier algorithmIdentifier, RSAPrivateKey key) {
        super(algorithmIdentifier, new RSAPrivateCrtKeyParameters(key.getModulus(), key.getPublicExponent(), key.getPrivateExponent(), key.getPrime1(), key.getPrime2(), key.getExponent1(), key.getExponent2(), key.getCoefficient()));
        this.modulus = key.getModulus();
        this.publicExponent = key.getPublicExponent();
        this.privateExponent = key.getPrivateExponent();
        this.primeP = key.getPrime1();
        this.primeQ = key.getPrime2();
        this.primeExponentP = key.getExponent1();
        this.primeExponentQ = key.getExponent2();
        this.crtCoefficient = key.getCoefficient();
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey, java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey, java.security.Key
    public byte[] getEncoded() {
        return KeyUtil.getEncodedPrivateKeyInfo(this.algorithmIdentifier, new RSAPrivateKey(getModulus(), getPublicExponent(), getPrivateExponent(), getPrimeP(), getPrimeQ(), getPrimeExponentP(), getPrimeExponentQ(), getCrtCoefficient()));
    }

    @Override // java.security.interfaces.RSAPrivateCrtKey
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }

    @Override // java.security.interfaces.RSAPrivateCrtKey
    public BigInteger getPrimeP() {
        return this.primeP;
    }

    @Override // java.security.interfaces.RSAPrivateCrtKey
    public BigInteger getPrimeQ() {
        return this.primeQ;
    }

    @Override // java.security.interfaces.RSAPrivateCrtKey
    public BigInteger getPrimeExponentP() {
        return this.primeExponentP;
    }

    @Override // java.security.interfaces.RSAPrivateCrtKey
    public BigInteger getPrimeExponentQ() {
        return this.primeExponentQ;
    }

    @Override // java.security.interfaces.RSAPrivateCrtKey
    public BigInteger getCrtCoefficient() {
        return this.crtCoefficient;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey
    public int hashCode() {
        return (getModulus().hashCode() ^ getPublicExponent().hashCode()) ^ getPrivateExponent().hashCode();
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RSAPrivateCrtKey) {
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) o;
            return getModulus().equals(key.getModulus()) && getPublicExponent().equals(key.getPublicExponent()) && getPrivateExponent().equals(key.getPrivateExponent()) && getPrimeP().equals(key.getPrimeP()) && getPrimeQ().equals(key.getPrimeQ()) && getPrimeExponentP().equals(key.getPrimeExponentP()) && getPrimeExponentQ().equals(key.getPrimeExponentQ()) && getCrtCoefficient().equals(key.getCrtCoefficient());
        }
        return false;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.rsaPrivateKey = new RSAPrivateCrtKeyParameters(getModulus(), getPublicExponent(), getPrivateExponent(), getPrimeP(), getPrimeQ(), getPrimeExponentP(), getPrimeExponentQ(), getCrtCoefficient());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateKey
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("RSA Private CRT Key [").append(RSAUtil.generateKeyFingerprint(getModulus())).append(NavigationBarInflaterView.SIZE_MOD_END).append(",[").append(RSAUtil.generateExponentFingerprint(getPublicExponent())).append(NavigationBarInflaterView.SIZE_MOD_END).append(nl);
        buf.append("             modulus: ").append(getModulus().toString(16)).append(nl);
        buf.append("     public exponent: ").append(getPublicExponent().toString(16)).append(nl);
        return buf.toString();
    }
}
