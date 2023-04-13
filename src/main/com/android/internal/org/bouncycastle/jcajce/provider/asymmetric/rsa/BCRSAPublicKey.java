package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.rsa;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.security.keystore.KeyProperties;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.crypto.params.RSAKeyParameters;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
/* loaded from: classes4.dex */
public class BCRSAPublicKey implements RSAPublicKey {
    static final AlgorithmIdentifier DEFAULT_ALGORITHM_IDENTIFIER = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
    static final long serialVersionUID = 2675817738516720772L;
    private transient AlgorithmIdentifier algorithmIdentifier;
    private BigInteger modulus;
    private BigInteger publicExponent;
    private transient RSAKeyParameters rsaPublicKey;

    BCRSAPublicKey(RSAKeyParameters key) {
        this(DEFAULT_ALGORITHM_IDENTIFIER, key);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPublicKey(AlgorithmIdentifier algId, RSAKeyParameters key) {
        this.algorithmIdentifier = algId;
        this.modulus = key.getModulus();
        this.publicExponent = key.getExponent();
        this.rsaPublicKey = key;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPublicKey(RSAPublicKeySpec spec) {
        this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = spec.getModulus();
        this.publicExponent = spec.getPublicExponent();
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPublicKey(RSAPublicKey key) {
        this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = key.getModulus();
        this.publicExponent = key.getPublicExponent();
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BCRSAPublicKey(SubjectPublicKeyInfo info) {
        populateFromPublicKeyInfo(info);
    }

    private void populateFromPublicKeyInfo(SubjectPublicKeyInfo info) {
        try {
            com.android.internal.org.bouncycastle.asn1.pkcs.RSAPublicKey pubKey = com.android.internal.org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(info.parsePublicKey());
            this.algorithmIdentifier = info.getAlgorithm();
            this.modulus = pubKey.getModulus();
            this.publicExponent = pubKey.getPublicExponent();
            this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in RSA public key");
        }
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        return this.modulus;
    }

    @Override // java.security.interfaces.RSAPublicKey
    public BigInteger getPublicExponent() {
        return this.publicExponent;
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
        return "X.509";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(this.algorithmIdentifier, new com.android.internal.org.bouncycastle.asn1.pkcs.RSAPublicKey(getModulus(), getPublicExponent()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RSAKeyParameters engineGetKeyParameters() {
        return this.rsaPublicKey;
    }

    public int hashCode() {
        return getModulus().hashCode() ^ getPublicExponent().hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RSAPublicKey) {
            RSAPublicKey key = (RSAPublicKey) o;
            return getModulus().equals(key.getModulus()) && getPublicExponent().equals(key.getPublicExponent());
        }
        return false;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("RSA Public Key [").append(RSAUtil.generateKeyFingerprint(getModulus())).append(NavigationBarInflaterView.SIZE_MOD_END).append(",[").append(RSAUtil.generateExponentFingerprint(getPublicExponent())).append(NavigationBarInflaterView.SIZE_MOD_END).append(nl);
        buf.append("        modulus: ").append(getModulus().toString(16)).append(nl);
        buf.append("public exponent: ").append(getPublicExponent().toString(16)).append(nl);
        return buf.toString();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            this.algorithmIdentifier = AlgorithmIdentifier.getInstance(in.readObject());
        } catch (Exception e) {
            this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        }
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (!this.algorithmIdentifier.equals(DEFAULT_ALGORITHM_IDENTIFIER)) {
            out.writeObject(this.algorithmIdentifier.getEncoded());
        }
    }
}
