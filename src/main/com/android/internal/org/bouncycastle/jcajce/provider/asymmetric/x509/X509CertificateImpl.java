package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
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
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x500.style.RFC4519Style;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.BasicConstraints;
import com.android.internal.org.bouncycastle.asn1.x509.Certificate;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralName;
import com.android.internal.org.bouncycastle.asn1.x509.KeyUsage;
import com.android.internal.org.bouncycastle.asn1.x509.TBSCertificate;
import com.android.internal.org.bouncycastle.jcajce.CompositePublicKey;
import com.android.internal.org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import com.android.internal.org.bouncycastle.jcajce.p021io.OutputStreamFactory;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.jce.X509Principal;
import com.android.internal.org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Integers;
import com.android.internal.org.bouncycastle.util.Properties;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
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
abstract class X509CertificateImpl extends X509Certificate implements BCX509Certificate {
    protected BasicConstraints basicConstraints;
    protected JcaJceHelper bcHelper;

    /* renamed from: c */
    protected Certificate f810c;
    protected boolean[] keyUsage;
    protected String sigAlgName;
    protected byte[] sigAlgParams;

    /* JADX INFO: Access modifiers changed from: package-private */
    public X509CertificateImpl(JcaJceHelper bcHelper, Certificate c, BasicConstraints basicConstraints, boolean[] keyUsage, String sigAlgName, byte[] sigAlgParams) {
        this.bcHelper = bcHelper;
        this.f810c = c;
        this.basicConstraints = basicConstraints;
        this.keyUsage = keyUsage;
        this.sigAlgName = sigAlgName;
        this.sigAlgParams = sigAlgParams;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.interfaces.BCX509Certificate
    public X500Name getIssuerX500Name() {
        return this.f810c.getIssuer();
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.interfaces.BCX509Certificate
    public TBSCertificate getTBSCertificateNative() {
        return this.f810c.getTBSCertificate();
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.interfaces.BCX509Certificate
    public X500Name getSubjectX500Name() {
        return this.f810c.getSubject();
    }

    @Override // java.security.cert.X509Certificate
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        checkValidity(new Date());
    }

    @Override // java.security.cert.X509Certificate
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        if (date.getTime() > getNotAfter().getTime()) {
            throw new CertificateExpiredException("certificate expired on " + this.f810c.getEndDate().getTime());
        }
        if (date.getTime() < getNotBefore().getTime()) {
            throw new CertificateNotYetValidException("certificate not valid till " + this.f810c.getStartDate().getTime());
        }
    }

    @Override // java.security.cert.X509Certificate
    public int getVersion() {
        return this.f810c.getVersionNumber();
    }

    @Override // java.security.cert.X509Certificate
    public BigInteger getSerialNumber() {
        return this.f810c.getSerialNumber().getValue();
    }

    @Override // java.security.cert.X509Certificate
    public Principal getIssuerDN() {
        return new X509Principal(this.f810c.getIssuer());
    }

    @Override // java.security.cert.X509Certificate
    public X500Principal getIssuerX500Principal() {
        try {
            byte[] encoding = this.f810c.getIssuer().getEncoded(ASN1Encoding.DER);
            return new X500Principal(encoding);
        } catch (IOException e) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override // java.security.cert.X509Certificate
    public Principal getSubjectDN() {
        return new X509Principal(this.f810c.getSubject());
    }

    @Override // java.security.cert.X509Certificate
    public X500Principal getSubjectX500Principal() {
        try {
            byte[] encoding = this.f810c.getSubject().getEncoded(ASN1Encoding.DER);
            return new X500Principal(encoding);
        } catch (IOException e) {
            throw new IllegalStateException("can't encode subject DN");
        }
    }

    @Override // java.security.cert.X509Certificate
    public Date getNotBefore() {
        return this.f810c.getStartDate().getDate();
    }

    @Override // java.security.cert.X509Certificate
    public Date getNotAfter() {
        return this.f810c.getEndDate().getDate();
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        try {
            return this.f810c.getTBSCertificate().getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getSignature() {
        return this.f810c.getSignature().getOctets();
    }

    @Override // java.security.cert.X509Certificate
    public String getSigAlgName() {
        return this.sigAlgName;
    }

    @Override // java.security.cert.X509Certificate
    public String getSigAlgOID() {
        return this.f810c.getSignatureAlgorithm().getAlgorithm().getId();
    }

    @Override // java.security.cert.X509Certificate
    public byte[] getSigAlgParams() {
        return Arrays.clone(this.sigAlgParams);
    }

    @Override // java.security.cert.X509Certificate
    public boolean[] getIssuerUniqueID() {
        DERBitString id = this.f810c.getTBSCertificate().getIssuerUniqueId();
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
        DERBitString id = this.f810c.getTBSCertificate().getSubjectUniqueId();
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
        return Arrays.clone(this.keyUsage);
    }

    @Override // java.security.cert.X509Certificate
    public List getExtendedKeyUsage() throws CertificateParsingException {
        byte[] extOctets = getExtensionOctets(this.f810c, "2.5.29.37");
        if (extOctets == null) {
            return null;
        }
        try {
            ASN1Sequence seq = ASN1Sequence.getInstance(ASN1Primitive.fromByteArray(extOctets));
            List list = new ArrayList();
            for (int i = 0; i != seq.size(); i++) {
                list.add(((ASN1ObjectIdentifier) seq.getObjectAt(i)).getId());
            }
            return Collections.unmodifiableList(list);
        } catch (Exception e) {
            throw new CertificateParsingException("error processing extended key usage extension");
        }
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
        return getAlternativeNames(this.f810c, Extension.subjectAlternativeName.getId());
    }

    @Override // java.security.cert.X509Certificate
    public Collection getIssuerAlternativeNames() throws CertificateParsingException {
        return getAlternativeNames(this.f810c, Extension.issuerAlternativeName.getId());
    }

    @Override // java.security.cert.X509Extension
    public Set getCriticalExtensionOIDs() {
        if (getVersion() == 3) {
            Set set = new HashSet();
            Extensions extensions = this.f810c.getTBSCertificate().getExtensions();
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

    @Override // java.security.cert.X509Extension
    public byte[] getExtensionValue(String oid) {
        ASN1OctetString extValue = getExtensionValue(this.f810c, oid);
        if (extValue != null) {
            try {
                return extValue.getEncoded();
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
            Extensions extensions = this.f810c.getTBSCertificate().getExtensions();
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
        if (getVersion() == 3 && (extensions = this.f810c.getTBSCertificate().getExtensions()) != null) {
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                if (!oid.equals((ASN1Primitive) Extension.keyUsage) && !oid.equals((ASN1Primitive) Extension.certificatePolicies) && !oid.equals((ASN1Primitive) Extension.policyMappings) && !oid.equals((ASN1Primitive) Extension.inhibitAnyPolicy) && !oid.equals((ASN1Primitive) Extension.cRLDistributionPoints) && !oid.equals((ASN1Primitive) Extension.issuingDistributionPoint) && !oid.equals((ASN1Primitive) Extension.deltaCRLIndicator) && !oid.equals((ASN1Primitive) Extension.policyConstraints) && !oid.equals((ASN1Primitive) Extension.basicConstraints) && !oid.equals((ASN1Primitive) Extension.subjectAlternativeName) && !oid.equals((ASN1Primitive) Extension.nameConstraints)) {
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
            return BouncyCastleProvider.getPublicKey(this.f810c.getSubjectPublicKeyInfo());
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.cert.Certificate
    public byte[] getEncoded() throws CertificateEncodingException {
        try {
            return this.f810c.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
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
        X509SignatureUtil.prettyPrintSignature(getSignature(), buf, nl);
        Extensions extensions = this.f810c.getTBSCertificate().getExtensions();
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
                            buf.append(new NetscapeCertType(DERBitString.getInstance(dIn.readObject()))).append(nl);
                        } else if (oid.equals((ASN1Primitive) MiscObjectIdentifiers.netscapeRevocationURL)) {
                            buf.append(new NetscapeRevocationURL(DERIA5String.getInstance(dIn.readObject()))).append(nl);
                        } else if (oid.equals((ASN1Primitive) MiscObjectIdentifiers.verisignCzagExtension)) {
                            buf.append(new VerisignCzagExtension(DERIA5String.getInstance(dIn.readObject()))).append(nl);
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
        doVerify(key, new SignatureCreator() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.X509CertificateImpl.1
            @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator
            public Signature createSignature(String sigName) throws NoSuchAlgorithmException {
                try {
                    return X509CertificateImpl.this.bcHelper.createSignature(sigName);
                } catch (Exception e) {
                    return Signature.getInstance(sigName);
                }
            }
        });
    }

    @Override // java.security.cert.Certificate
    public final void verify(PublicKey key, final String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        doVerify(key, new SignatureCreator() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.X509CertificateImpl.2
            @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator
            public Signature createSignature(String sigName) throws NoSuchAlgorithmException, NoSuchProviderException {
                String str = sigProvider;
                if (str != null) {
                    return Signature.getInstance(sigName, str);
                }
                return Signature.getInstance(sigName);
            }
        });
    }

    @Override // java.security.cert.X509Certificate, java.security.cert.Certificate
    public final void verify(PublicKey key, final Provider sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {
            doVerify(key, new SignatureCreator() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.X509CertificateImpl.3
                @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator
                public Signature createSignature(String sigName) throws NoSuchAlgorithmException {
                    Provider provider = sigProvider;
                    if (provider != null) {
                        return Signature.getInstance(sigName, provider);
                    }
                    return Signature.getInstance(sigName);
                }
            });
        } catch (NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("provider issue: " + e.getMessage());
        }
    }

    private void doVerify(PublicKey key, SignatureCreator signatureCreator) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        if ((key instanceof CompositePublicKey) && X509SignatureUtil.isCompositeAlgorithm(this.f810c.getSignatureAlgorithm())) {
            List<PublicKey> pubKeys = ((CompositePublicKey) key).getPublicKeys();
            ASN1Sequence keySeq = ASN1Sequence.getInstance(this.f810c.getSignatureAlgorithm().getParameters());
            ASN1Sequence sigSeq = ASN1Sequence.getInstance(DERBitString.getInstance(this.f810c.getSignature()).getBytes());
            boolean success = false;
            for (int i = 0; i != pubKeys.size(); i++) {
                if (pubKeys.get(i) != null) {
                    AlgorithmIdentifier sigAlg = AlgorithmIdentifier.getInstance(keySeq.getObjectAt(i));
                    String sigName = X509SignatureUtil.getSignatureName(sigAlg);
                    SignatureException sigExc = null;
                    try {
                        checkSignature(pubKeys.get(i), signatureCreator.createSignature(sigName), sigAlg.getParameters(), DERBitString.getInstance(sigSeq.getObjectAt(i)).getBytes());
                        success = true;
                    } catch (SignatureException e) {
                        sigExc = e;
                    }
                    if (sigExc != null) {
                        throw sigExc;
                    }
                }
            }
            if (!success) {
                throw new InvalidKeyException("no matching key found");
            }
        } else if (X509SignatureUtil.isCompositeAlgorithm(this.f810c.getSignatureAlgorithm())) {
            ASN1Sequence keySeq2 = ASN1Sequence.getInstance(this.f810c.getSignatureAlgorithm().getParameters());
            ASN1Sequence sigSeq2 = ASN1Sequence.getInstance(DERBitString.getInstance(this.f810c.getSignature()).getBytes());
            boolean success2 = false;
            for (int i2 = 0; i2 != sigSeq2.size(); i2++) {
                AlgorithmIdentifier sigAlg2 = AlgorithmIdentifier.getInstance(keySeq2.getObjectAt(i2));
                String sigName2 = X509SignatureUtil.getSignatureName(sigAlg2);
                SignatureException sigExc2 = null;
                try {
                    checkSignature(key, signatureCreator.createSignature(sigName2), sigAlg2.getParameters(), DERBitString.getInstance(sigSeq2.getObjectAt(i2)).getBytes());
                    success2 = true;
                } catch (InvalidKeyException e2) {
                } catch (NoSuchAlgorithmException e3) {
                } catch (SignatureException e4) {
                    sigExc2 = e4;
                }
                if (sigExc2 != null) {
                    throw sigExc2;
                }
            }
            if (!success2) {
                throw new InvalidKeyException("no matching key found");
            }
        } else {
            String sigName3 = X509SignatureUtil.getSignatureName(this.f810c.getSignatureAlgorithm());
            Signature signature = signatureCreator.createSignature(sigName3);
            if (key instanceof CompositePublicKey) {
                List<PublicKey> keys = ((CompositePublicKey) key).getPublicKeys();
                for (int i3 = 0; i3 != keys.size(); i3++) {
                    try {
                        checkSignature(keys.get(i3), signature, this.f810c.getSignatureAlgorithm().getParameters(), getSignature());
                        return;
                    } catch (InvalidKeyException e5) {
                    }
                }
                throw new InvalidKeyException("no matching signature found");
            }
            checkSignature(key, signature, this.f810c.getSignatureAlgorithm().getParameters(), getSignature());
        }
    }

    private void checkSignature(PublicKey key, Signature signature, ASN1Encodable params, byte[] sigBytes) throws CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (!isAlgIdEqual(this.f810c.getSignatureAlgorithm(), this.f810c.getTBSCertificate().getSignature())) {
            throw new CertificateException("signature algorithm in TBS cert not same as outer cert");
        }
        X509SignatureUtil.setSignatureParameters(signature, params);
        signature.initVerify(key);
        try {
            OutputStream sigOut = new BufferedOutputStream(OutputStreamFactory.createStream(signature), 512);
            this.f810c.getTBSCertificate().encodeTo(sigOut, ASN1Encoding.DER);
            sigOut.close();
            if (!signature.verify(sigBytes)) {
                throw new SignatureException("certificate does not verify with supplied key");
            }
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    private boolean isAlgIdEqual(AlgorithmIdentifier id1, AlgorithmIdentifier id2) {
        if (id1.getAlgorithm().equals((ASN1Primitive) id2.getAlgorithm())) {
            if (Properties.isOverrideSet("com.android.internal.org.bouncycastle.x509.allow_absent_equiv_NULL")) {
                if (id1.getParameters() == null) {
                    return id2.getParameters() == null || id2.getParameters().equals(DERNull.INSTANCE);
                } else if (id2.getParameters() == null) {
                    return id1.getParameters() == null || id1.getParameters().equals(DERNull.INSTANCE);
                }
            }
            if (id1.getParameters() != null) {
                return id1.getParameters().equals(id2.getParameters());
            }
            if (id2.getParameters() != null) {
                return id2.getParameters().equals(id1.getParameters());
            }
            return true;
        }
        return false;
    }

    private static Collection getAlternativeNames(Certificate c, String oid) throws CertificateParsingException {
        byte[] extOctets = getExtensionOctets(c, oid);
        if (extOctets == null) {
            return null;
        }
        try {
            Collection temp = new ArrayList();
            Enumeration it = ASN1Sequence.getInstance(extOctets).getObjects();
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
                        list.add(X500Name.getInstance(RFC4519Style.INSTANCE, genName.getName()).toString());
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

    /* JADX INFO: Access modifiers changed from: protected */
    public static byte[] getExtensionOctets(Certificate c, String oid) {
        ASN1OctetString extValue = getExtensionValue(c, oid);
        if (extValue != null) {
            return extValue.getOctets();
        }
        return null;
    }

    protected static ASN1OctetString getExtensionValue(Certificate c, String oid) {
        Extension ext;
        Extensions exts = c.getTBSCertificate().getExtensions();
        if (exts != null && (ext = exts.getExtension(new ASN1ObjectIdentifier(oid))) != null) {
            return ext.getExtnValue();
        }
        return null;
    }
}
