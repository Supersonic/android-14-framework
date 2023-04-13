package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.asn1.ASN1BitString;
import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.ASN1String;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERIA5String;
import com.android.internal.org.bouncycastle.asn1.DERNull;
import com.android.internal.org.bouncycastle.asn1.DEROctetString;
import com.android.internal.org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import com.android.internal.org.bouncycastle.asn1.misc.NetscapeCertType;
import com.android.internal.org.bouncycastle.asn1.misc.NetscapeRevocationURL;
import com.android.internal.org.bouncycastle.asn1.misc.VerisignCzagExtension;
import com.android.internal.org.bouncycastle.asn1.util.ASN1Dump;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.BasicConstraints;
import com.android.internal.org.bouncycastle.asn1.x509.Certificate;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralName;
import com.android.internal.org.bouncycastle.asn1.x509.KeyUsage;
import com.android.internal.org.bouncycastle.asn1.x509.X509Name;
import com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import com.android.internal.org.bouncycastle.jce.X509Principal;
import com.android.internal.org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Integers;
import com.android.internal.org.bouncycastle.util.Strings;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes4.dex */
public class X509CertificateObject extends X509Certificate implements PKCS12BagAttributeCarrier {
    private PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();
    private BasicConstraints basicConstraints;

    /* renamed from: c */
    private Certificate f826c;
    private byte[] encoded;
    private int hashValue;
    private boolean hashValueSet;
    private boolean[] keyUsage;

    public X509CertificateObject(Certificate c) throws CertificateParsingException {
        this.f826c = c;
        try {
            byte[] bytes = getExtensionBytes("2.5.29.19");
            if (bytes != null) {
                this.basicConstraints = BasicConstraints.getInstance(ASN1Primitive.fromByteArray(bytes));
            }
            try {
                byte[] bytes2 = getExtensionBytes("2.5.29.15");
                if (bytes2 != null) {
                    ASN1BitString bits = DERBitString.getInstance(ASN1Primitive.fromByteArray(bytes2));
                    byte[] bytes3 = bits.getBytes();
                    int length = (bytes3.length * 8) - bits.getPadBits();
                    int i = 9;
                    if (length >= 9) {
                        i = length;
                    }
                    this.keyUsage = new boolean[i];
                    for (int i2 = 0; i2 != length; i2++) {
                        this.keyUsage[i2] = (bytes3[i2 / 8] & (128 >>> (i2 % 8))) != 0;
                    }
                    return;
                }
                this.keyUsage = null;
            } catch (Exception e) {
                throw new CertificateParsingException("cannot construct KeyUsage: " + e);
            }
        } catch (Exception e2) {
            throw new CertificateParsingException("cannot construct BasicConstraints: " + e2);
        }
    }

    @Override // java.security.cert.X509Certificate
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        checkValidity(new Date());
    }

    @Override // java.security.cert.X509Certificate
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        if (date.getTime() > getNotAfter().getTime()) {
            throw new CertificateExpiredException("certificate expired on " + this.f826c.getEndDate().getTime());
        }
        if (date.getTime() < getNotBefore().getTime()) {
            throw new CertificateNotYetValidException("certificate not valid till " + this.f826c.getStartDate().getTime());
        }
    }

    @Override // java.security.cert.X509Certificate
    public int getVersion() {
        return this.f826c.getVersionNumber();
    }

    @Override // java.security.cert.X509Certificate
    public BigInteger getSerialNumber() {
        return this.f826c.getSerialNumber().getValue();
    }

    @Override // java.security.cert.X509Certificate
    public Principal getIssuerDN() {
        return new X509Principal(this.f826c.getIssuer());
    }

    @Override // java.security.cert.X509Certificate
    public X500Principal getIssuerX500Principal() {
        try {
            return new X500Principal(this.f826c.getIssuer().getEncoded());
        } catch (IOException e) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override // java.security.cert.X509Certificate
    public Principal getSubjectDN() {
        return new X509Principal(this.f826c.getSubject());
    }

    @Override // java.security.cert.X509Certificate
    public X500Principal getSubjectX500Principal() {
        try {
            return new X500Principal(this.f826c.getSubject().getEncoded());
        } catch (IOException e) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override // java.security.cert.X509Certificate
    public Date getNotBefore() {
        return this.f826c.getStartDate().getDate();
    }

    @Override // java.security.cert.X509Certificate
    public Date getNotAfter() {
        return this.f826c.getEndDate().getDate();
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        try {
            return this.f826c.getTBSCertificate().getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getSignature() {
        return this.f826c.getSignature().getOctets();
    }

    @Override // java.security.cert.X509Certificate
    public String getSigAlgName() {
        String algName;
        Provider prov = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (prov != null && (algName = prov.getProperty("Alg.Alias.Signature." + getSigAlgOID())) != null) {
            return algName;
        }
        Provider[] provs = Security.getProviders();
        for (int i = 0; i != provs.length; i++) {
            String algName2 = provs[i].getProperty("Alg.Alias.Signature." + getSigAlgOID());
            if (algName2 != null) {
                return algName2;
            }
        }
        return getSigAlgOID();
    }

    @Override // java.security.cert.X509Certificate
    public String getSigAlgOID() {
        return this.f826c.getSignatureAlgorithm().getAlgorithm().getId();
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getSigAlgParams() {
        if (this.f826c.getSignatureAlgorithm().getParameters() != null) {
            try {
                return this.f826c.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded(ASN1Encoding.DER);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getIssuerUniqueID() {
        DERBitString id = this.f826c.getTBSCertificate().getIssuerUniqueId();
        if (id != null) {
            byte[] bytes = id.getBytes();
            boolean[] boolId = new boolean[(bytes.length * 8) - id.getPadBits()];
            for (int i = 0; i != boolId.length; i++) {
                boolId[i] = (bytes[i / 8] & (128 >>> (i % 8))) != 0;
            }
            return boolId;
        }
        return null;
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getSubjectUniqueID() {
        DERBitString id = this.f826c.getTBSCertificate().getSubjectUniqueId();
        if (id != null) {
            byte[] bytes = id.getBytes();
            boolean[] boolId = new boolean[(bytes.length * 8) - id.getPadBits()];
            for (int i = 0; i != boolId.length; i++) {
                boolId[i] = (bytes[i / 8] & (128 >>> (i % 8))) != 0;
            }
            return boolId;
        }
        return null;
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getKeyUsage() {
        return this.keyUsage;
    }

    @Override // java.security.cert.X509Certificate
    public List getExtendedKeyUsage() throws CertificateParsingException {
        byte[] bytes = getExtensionBytes("2.5.29.37");
        if (bytes != null) {
            try {
                ASN1InputStream dIn = new ASN1InputStream(bytes);
                ASN1Sequence seq = (ASN1Sequence) dIn.readObject();
                List list = new ArrayList();
                for (int i = 0; i != seq.size(); i++) {
                    list.add(((ASN1ObjectIdentifier) seq.getObjectAt(i)).getId());
                }
                return Collections.unmodifiableList(list);
            } catch (Exception e) {
                throw new CertificateParsingException("error processing extended key usage extension");
            }
        }
        return null;
    }

    @Override // java.security.cert.X509Certificate
    public int getBasicConstraints() {
        BasicConstraints basicConstraints = this.basicConstraints;
        if (basicConstraints == null || !basicConstraints.isCA()) {
            return -1;
        }
        if (this.basicConstraints.getPathLenConstraint() == null) {
            return Integer.MAX_VALUE;
        }
        return this.basicConstraints.getPathLenConstraint().intValue();
    }

    @Override // java.security.cert.X509Certificate
    public Collection getSubjectAlternativeNames() throws CertificateParsingException {
        return getAlternativeNames(getExtensionBytes(Extension.subjectAlternativeName.getId()));
    }

    @Override // java.security.cert.X509Certificate
    public Collection getIssuerAlternativeNames() throws CertificateParsingException {
        return getAlternativeNames(getExtensionBytes(Extension.issuerAlternativeName.getId()));
    }

    @Override // java.security.cert.X509Extension
    public Set getCriticalExtensionOIDs() {
        if (getVersion() == 3) {
            Set set = new HashSet();
            Extensions extensions = this.f826c.getTBSCertificate().getExtensions();
            if (extensions != null) {
                Enumeration e = extensions.oids();
                while (e.hasMoreElements()) {
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                    Extension ext = extensions.getExtension(oid);
                    if (ext.isCritical()) {
                        set.add(oid.getId());
                    }
                }
                return set;
            }
            return null;
        }
        return null;
    }

    private byte[] getExtensionBytes(String oid) {
        Extension ext;
        Extensions exts = this.f826c.getTBSCertificate().getExtensions();
        if (exts != null && (ext = exts.getExtension(new ASN1ObjectIdentifier(oid))) != null) {
            return ext.getExtnValue().getOctets();
        }
        return null;
    }

    @Override // java.security.cert.X509Extension
    public byte[] getExtensionValue(String oid) {
        Extension ext;
        Extensions exts = this.f826c.getTBSCertificate().getExtensions();
        if (exts != null && (ext = exts.getExtension(new ASN1ObjectIdentifier(oid))) != null) {
            try {
                return ext.getExtnValue().getEncoded();
            } catch (Exception e) {
                throw new IllegalStateException("error parsing " + e.toString());
            }
        }
        return null;
    }

    @Override // java.security.cert.X509Extension
    public Set getNonCriticalExtensionOIDs() {
        if (getVersion() == 3) {
            Set set = new HashSet();
            Extensions extensions = this.f826c.getTBSCertificate().getExtensions();
            if (extensions != null) {
                Enumeration e = extensions.oids();
                while (e.hasMoreElements()) {
                    ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                    Extension ext = extensions.getExtension(oid);
                    if (!ext.isCritical()) {
                        set.add(oid.getId());
                    }
                }
                return set;
            }
            return null;
        }
        return null;
    }

    @Override // java.security.cert.X509Extension
    public boolean hasUnsupportedCriticalExtension() {
        Extensions extensions;
        if (getVersion() == 3 && (extensions = this.f826c.getTBSCertificate().getExtensions()) != null) {
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                String oidId = oid.getId();
                if (!oidId.equals(RFC3280CertPathUtilities.KEY_USAGE) && !oidId.equals(RFC3280CertPathUtilities.CERTIFICATE_POLICIES) && !oidId.equals(RFC3280CertPathUtilities.POLICY_MAPPINGS) && !oidId.equals(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY) && !oidId.equals(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS) && !oidId.equals(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT) && !oidId.equals(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR) && !oidId.equals(RFC3280CertPathUtilities.POLICY_CONSTRAINTS) && !oidId.equals(RFC3280CertPathUtilities.BASIC_CONSTRAINTS) && !oidId.equals(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME) && !oidId.equals(RFC3280CertPathUtilities.NAME_CONSTRAINTS)) {
                    Extension ext = extensions.getExtension(oid);
                    if (ext.isCritical()) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override // java.security.cert.Certificate
    public PublicKey getPublicKey() {
        try {
            return BouncyCastleProvider.getPublicKey(this.f826c.getSubjectPublicKeyInfo());
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.cert.Certificate
    public byte[] getEncoded() throws CertificateEncodingException {
        try {
            if (this.encoded == null) {
                this.encoded = this.f826c.getEncoded(ASN1Encoding.DER);
            }
            return this.encoded;
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    @Override // java.security.cert.Certificate
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof java.security.cert.Certificate) {
            java.security.cert.Certificate other = (java.security.cert.Certificate) o;
            try {
                byte[] b1 = getEncoded();
                byte[] b2 = other.getEncoded();
                return Arrays.areEqual(b1, b2);
            } catch (CertificateEncodingException e) {
                return false;
            }
        }
        return false;
    }

    @Override // java.security.cert.Certificate
    public synchronized int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = calculateHashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }

    private int calculateHashCode() {
        int hashCode = 0;
        try {
            byte[] certData = getEncoded();
            for (int i = 1; i < certData.length; i++) {
                hashCode += certData[i] * i;
            }
            return hashCode;
        } catch (CertificateEncodingException e) {
            return 0;
        }
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

    @Override // java.security.cert.Certificate
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("  [0]         Version: ").append(getVersion()).append(nl);
        buf.append("         SerialNumber: ").append(getSerialNumber()).append(nl);
        buf.append("             IssuerDN: ").append(getIssuerDN()).append(nl);
        buf.append("           Start Date: ").append(getNotBefore()).append(nl);
        buf.append("           Final Date: ").append(getNotAfter()).append(nl);
        buf.append("            SubjectDN: ").append(getSubjectDN()).append(nl);
        buf.append("           Public Key: ").append(getPublicKey()).append(nl);
        buf.append("  Signature Algorithm: ").append(getSigAlgName()).append(nl);
        byte[] sig = getSignature();
        buf.append("            Signature: ").append(new String(Hex.encode(sig, 0, 20))).append(nl);
        for (int i = 20; i < sig.length; i += 20) {
            if (i < sig.length - 20) {
                buf.append("                       ").append(new String(Hex.encode(sig, i, 20))).append(nl);
            } else {
                buf.append("                       ").append(new String(Hex.encode(sig, i, sig.length - i))).append(nl);
            }
        }
        Extensions extensions = this.f826c.getTBSCertificate().getExtensions();
        if (extensions != null) {
            Enumeration e = extensions.oids();
            if (e.hasMoreElements()) {
                buf.append("       Extensions: \n");
            }
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (ext.getExtnValue() != null) {
                    byte[] octs = ext.getExtnValue().getOctets();
                    ASN1InputStream dIn = new ASN1InputStream(octs);
                    buf.append("                       critical(").append(ext.isCritical()).append(") ");
                    try {
                        if (oid.equals((ASN1Primitive) Extension.basicConstraints)) {
                            buf.append(BasicConstraints.getInstance(dIn.readObject())).append(nl);
                        } else if (oid.equals((ASN1Primitive) Extension.keyUsage)) {
                            buf.append(KeyUsage.getInstance(dIn.readObject())).append(nl);
                        } else if (oid.equals((ASN1Primitive) MiscObjectIdentifiers.netscapeCertType)) {
                            buf.append(new NetscapeCertType((DERBitString) dIn.readObject())).append(nl);
                        } else if (oid.equals((ASN1Primitive) MiscObjectIdentifiers.netscapeRevocationURL)) {
                            buf.append(new NetscapeRevocationURL((DERIA5String) dIn.readObject())).append(nl);
                        } else if (oid.equals((ASN1Primitive) MiscObjectIdentifiers.verisignCzagExtension)) {
                            buf.append(new VerisignCzagExtension((DERIA5String) dIn.readObject())).append(nl);
                        } else {
                            buf.append(oid.getId());
                            buf.append(" value = ").append(ASN1Dump.dumpAsString(dIn.readObject())).append(nl);
                        }
                    } catch (Exception e2) {
                        buf.append(oid.getId());
                        buf.append(" value = ").append("*****").append(nl);
                    }
                } else {
                    buf.append(nl);
                }
            }
        }
        return buf.toString();
    }

    @Override // java.security.cert.Certificate
    public final void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature;
        String sigName = X509SignatureUtil.getSignatureName(this.f826c.getSignatureAlgorithm());
        try {
            signature = Signature.getInstance(sigName, BouncyCastleProvider.PROVIDER_NAME);
        } catch (Exception e) {
            signature = Signature.getInstance(sigName);
        }
        checkSignature(key, signature);
    }

    @Override // java.security.cert.Certificate
    public final void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature signature;
        String sigName = X509SignatureUtil.getSignatureName(this.f826c.getSignatureAlgorithm());
        if (sigProvider != null) {
            signature = Signature.getInstance(sigName, sigProvider);
        } else {
            signature = Signature.getInstance(sigName);
        }
        checkSignature(key, signature);
    }

    @Override // java.security.cert.X509Certificate, java.security.cert.Certificate
    public final void verify(PublicKey key, Provider sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature;
        String sigName = X509SignatureUtil.getSignatureName(this.f826c.getSignatureAlgorithm());
        if (sigProvider != null) {
            signature = Signature.getInstance(sigName, sigProvider);
        } else {
            signature = Signature.getInstance(sigName);
        }
        checkSignature(key, signature);
    }

    private void checkSignature(PublicKey key, Signature signature) throws CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (!isAlgIdEqual(this.f826c.getSignatureAlgorithm(), this.f826c.getTBSCertificate().getSignature())) {
            throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
        }
        ASN1Encodable params = this.f826c.getSignatureAlgorithm().getParameters();
        X509SignatureUtil.setSignatureParameters(signature, params);
        signature.initVerify(key);
        signature.update(getTBSCertificate());
        if (!signature.verify(getSignature())) {
            throw new SignatureException("certificate does not verify with supplied key");
        }
    }

    private boolean isAlgIdEqual(AlgorithmIdentifier id1, AlgorithmIdentifier id2) {
        if (id1.getAlgorithm().equals((ASN1Primitive) id2.getAlgorithm())) {
            if (id1.getParameters() == null) {
                return id2.getParameters() == null || id2.getParameters().equals(DERNull.INSTANCE);
            } else if (id2.getParameters() == null) {
                return id1.getParameters() == null || id1.getParameters().equals(DERNull.INSTANCE);
            } else {
                return id1.getParameters().equals(id2.getParameters());
            }
        }
        return false;
    }

    private static Collection getAlternativeNames(byte[] extVal) throws CertificateParsingException {
        if (extVal == null) {
            return null;
        }
        try {
            Collection temp = new ArrayList();
            Enumeration it = ASN1Sequence.getInstance(extVal).getObjects();
            while (it.hasMoreElements()) {
                GeneralName genName = GeneralName.getInstance(it.nextElement());
                List list = new ArrayList();
                list.add(Integers.valueOf(genName.getTagNo()));
                switch (genName.getTagNo()) {
                    case 0:
                    case 3:
                    case 5:
                        list.add(genName.getEncoded());
                        break;
                    case 1:
                    case 2:
                    case 6:
                        list.add(((ASN1String) genName.getName()).getString());
                        break;
                    case 4:
                        list.add(X509Name.getInstance(genName.getName()).toString(true, X509Name.DefaultSymbols));
                        break;
                    case 7:
                        byte[] addrBytes = DEROctetString.getInstance(genName.getName()).getOctets();
                        try {
                            String addr = InetAddress.getByAddress(addrBytes).getHostAddress();
                            list.add(addr);
                            break;
                        } catch (UnknownHostException e) {
                            break;
                        }
                    case 8:
                        list.add(ASN1ObjectIdentifier.getInstance(genName.getName()).getId());
                        break;
                    default:
                        throw new IOException("Bad tag number: " + genName.getTagNo());
                }
                temp.add(Collections.unmodifiableList(list));
            }
            if (temp.size() == 0) {
                return null;
            }
            return Collections.unmodifiableCollection(temp);
        } catch (Exception e2) {
            throw new CertificateParsingException(e2.getMessage());
        }
    }
}
