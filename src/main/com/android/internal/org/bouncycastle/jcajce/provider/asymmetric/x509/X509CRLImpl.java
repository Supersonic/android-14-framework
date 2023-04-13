package com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.util.ASN1Dump;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.CRLDistPoint;
import com.android.internal.org.bouncycastle.asn1.x509.CRLNumber;
import com.android.internal.org.bouncycastle.asn1.x509.CertificateList;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.internal.org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import com.android.internal.org.bouncycastle.asn1.x509.TBSCertList;
import com.android.internal.org.bouncycastle.asn1.x509.Time;
import com.android.internal.org.bouncycastle.jcajce.CompositePublicKey;
import com.android.internal.org.bouncycastle.jcajce.p021io.OutputStreamFactory;
import com.android.internal.org.bouncycastle.jcajce.util.JcaJceHelper;
import com.android.internal.org.bouncycastle.jce.X509Principal;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes4.dex */
abstract class X509CRLImpl extends X509CRL {
    protected JcaJceHelper bcHelper;

    /* renamed from: c */
    protected CertificateList f809c;
    protected boolean isIndirect;
    protected String sigAlgName;
    protected byte[] sigAlgParams;

    /* JADX INFO: Access modifiers changed from: package-private */
    public X509CRLImpl(JcaJceHelper bcHelper, CertificateList c, String sigAlgName, byte[] sigAlgParams, boolean isIndirect) {
        this.bcHelper = bcHelper;
        this.f809c = c;
        this.sigAlgName = sigAlgName;
        this.sigAlgParams = sigAlgParams;
        this.isIndirect = isIndirect;
    }

    @Override // java.security.cert.X509Extension
    public boolean hasUnsupportedCriticalExtension() {
        Set extns = getCriticalExtensionOIDs();
        if (extns == null) {
            return false;
        }
        extns.remove(Extension.issuingDistributionPoint.getId());
        extns.remove(Extension.deltaCRLIndicator.getId());
        return !extns.isEmpty();
    }

    private Set getExtensionOIDs(boolean critical) {
        Extensions extensions;
        if (getVersion() == 2 && (extensions = this.f809c.getTBSCertList().getExtensions()) != null) {
            Set set = new HashSet();
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (critical == ext.isCritical()) {
                    set.add(oid.getId());
                }
            }
            return set;
        }
        return null;
    }

    @Override // java.security.cert.X509Extension
    public Set getCriticalExtensionOIDs() {
        return getExtensionOIDs(true);
    }

    @Override // java.security.cert.X509Extension
    public Set getNonCriticalExtensionOIDs() {
        return getExtensionOIDs(false);
    }

    @Override // java.security.cert.X509Extension
    public byte[] getExtensionValue(String oid) {
        ASN1OctetString extValue = getExtensionValue(this.f809c, oid);
        if (extValue != null) {
            try {
                return extValue.getEncoded();
            } catch (Exception e) {
                throw new IllegalStateException("error parsing " + e.toString());
            }
        }
        return null;
    }

    @Override // java.security.cert.X509CRL
    public byte[] getEncoded() throws CRLException {
        try {
            return this.f809c.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey key) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        doVerify(key, new SignatureCreator() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLImpl.1
            @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator
            public Signature createSignature(String sigName) throws NoSuchAlgorithmException, NoSuchProviderException {
                try {
                    return X509CRLImpl.this.bcHelper.createSignature(sigName);
                } catch (Exception e) {
                    return Signature.getInstance(sigName);
                }
            }
        });
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey key, final String sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        doVerify(key, new SignatureCreator() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLImpl.2
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

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey key, final Provider sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        try {
            doVerify(key, new SignatureCreator() { // from class: com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.X509CRLImpl.3
                @Override // com.android.internal.org.bouncycastle.jcajce.provider.asymmetric.x509.SignatureCreator
                public Signature createSignature(String sigName) throws NoSuchAlgorithmException, NoSuchProviderException {
                    if (sigProvider != null) {
                        return Signature.getInstance(X509CRLImpl.this.getSigAlgName(), sigProvider);
                    }
                    return Signature.getInstance(X509CRLImpl.this.getSigAlgName());
                }
            });
        } catch (NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("provider issue: " + e.getMessage());
        }
    }

    private void doVerify(PublicKey key, SignatureCreator sigCreator) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        if (!this.f809c.getSignatureAlgorithm().equals(this.f809c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
        }
        if ((key instanceof CompositePublicKey) && X509SignatureUtil.isCompositeAlgorithm(this.f809c.getSignatureAlgorithm())) {
            List<PublicKey> pubKeys = ((CompositePublicKey) key).getPublicKeys();
            ASN1Sequence keySeq = ASN1Sequence.getInstance(this.f809c.getSignatureAlgorithm().getParameters());
            ASN1Sequence sigSeq = ASN1Sequence.getInstance(DERBitString.getInstance(this.f809c.getSignature()).getBytes());
            boolean success = false;
            for (int i = 0; i != pubKeys.size(); i++) {
                if (pubKeys.get(i) != null) {
                    AlgorithmIdentifier sigAlg = AlgorithmIdentifier.getInstance(keySeq.getObjectAt(i));
                    String sigName = X509SignatureUtil.getSignatureName(sigAlg);
                    Signature signature = sigCreator.createSignature(sigName);
                    SignatureException sigExc = null;
                    try {
                        checkSignature(pubKeys.get(i), signature, sigAlg.getParameters(), DERBitString.getInstance(sigSeq.getObjectAt(i)).getBytes());
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
        } else if (X509SignatureUtil.isCompositeAlgorithm(this.f809c.getSignatureAlgorithm())) {
            ASN1Sequence keySeq2 = ASN1Sequence.getInstance(this.f809c.getSignatureAlgorithm().getParameters());
            ASN1Sequence sigSeq2 = ASN1Sequence.getInstance(DERBitString.getInstance(this.f809c.getSignature()).getBytes());
            boolean success2 = false;
            for (int i2 = 0; i2 != sigSeq2.size(); i2++) {
                AlgorithmIdentifier sigAlg2 = AlgorithmIdentifier.getInstance(keySeq2.getObjectAt(i2));
                String sigName2 = X509SignatureUtil.getSignatureName(sigAlg2);
                SignatureException sigExc2 = null;
                try {
                    Signature signature2 = sigCreator.createSignature(sigName2);
                    checkSignature(key, signature2, sigAlg2.getParameters(), DERBitString.getInstance(sigSeq2.getObjectAt(i2)).getBytes());
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
            Signature sig = sigCreator.createSignature(getSigAlgName());
            byte[] bArr = this.sigAlgParams;
            if (bArr == null) {
                checkSignature(key, sig, null, getSignature());
                return;
            }
            try {
                checkSignature(key, sig, ASN1Primitive.fromByteArray(bArr), getSignature());
            } catch (IOException e5) {
                throw new SignatureException("cannot decode signature parameters: " + e5.getMessage());
            }
        }
    }

    private void checkSignature(PublicKey key, Signature sig, ASN1Encodable sigAlgParams, byte[] encSig) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CRLException {
        if (sigAlgParams != null) {
            X509SignatureUtil.setSignatureParameters(sig, sigAlgParams);
        }
        sig.initVerify(key);
        try {
            OutputStream sigOut = new BufferedOutputStream(OutputStreamFactory.createStream(sig), 512);
            this.f809c.getTBSCertList().encodeTo(sigOut, ASN1Encoding.DER);
            sigOut.close();
            if (!sig.verify(encSig)) {
                throw new SignatureException("CRL does not verify with supplied public key.");
            }
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override // java.security.cert.X509CRL
    public int getVersion() {
        return this.f809c.getVersionNumber();
    }

    @Override // java.security.cert.X509CRL
    public Principal getIssuerDN() {
        return new X509Principal(X500Name.getInstance(this.f809c.getIssuer().toASN1Primitive()));
    }

    @Override // java.security.cert.X509CRL
    public X500Principal getIssuerX500Principal() {
        try {
            return new X500Principal(this.f809c.getIssuer().getEncoded());
        } catch (IOException e) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override // java.security.cert.X509CRL
    public Date getThisUpdate() {
        return this.f809c.getThisUpdate().getDate();
    }

    @Override // java.security.cert.X509CRL
    public Date getNextUpdate() {
        Time nextUpdate = this.f809c.getNextUpdate();
        if (nextUpdate == null) {
            return null;
        }
        return nextUpdate.getDate();
    }

    private Set loadCRLEntries() {
        Extension currentCaName;
        Set entrySet = new HashSet();
        Enumeration certs = this.f809c.getRevokedCertificateEnumeration();
        X500Name previousCertificateIssuer = null;
        while (certs.hasMoreElements()) {
            TBSCertList.CRLEntry entry = (TBSCertList.CRLEntry) certs.nextElement();
            X509CRLEntryObject crlEntry = new X509CRLEntryObject(entry, this.isIndirect, previousCertificateIssuer);
            entrySet.add(crlEntry);
            if (this.isIndirect && entry.hasExtensions() && (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                previousCertificateIssuer = X500Name.getInstance(GeneralNames.getInstance(currentCaName.getParsedValue()).getNames()[0].getName());
            }
        }
        return entrySet;
    }

    @Override // java.security.cert.X509CRL
    public X509CRLEntry getRevokedCertificate(BigInteger serialNumber) {
        Extension currentCaName;
        Enumeration certs = this.f809c.getRevokedCertificateEnumeration();
        X500Name previousCertificateIssuer = null;
        while (certs.hasMoreElements()) {
            TBSCertList.CRLEntry entry = (TBSCertList.CRLEntry) certs.nextElement();
            if (entry.getUserCertificate().hasValue(serialNumber)) {
                return new X509CRLEntryObject(entry, this.isIndirect, previousCertificateIssuer);
            }
            if (this.isIndirect && entry.hasExtensions() && (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                previousCertificateIssuer = X500Name.getInstance(GeneralNames.getInstance(currentCaName.getParsedValue()).getNames()[0].getName());
            }
        }
        return null;
    }

    @Override // java.security.cert.X509CRL
    public Set getRevokedCertificates() {
        Set entrySet = loadCRLEntries();
        if (!entrySet.isEmpty()) {
            return Collections.unmodifiableSet(entrySet);
        }
        return null;
    }

    @Override // java.security.cert.X509CRL
    public byte[] getTBSCertList() throws CRLException {
        try {
            return this.f809c.getTBSCertList().getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override // java.security.cert.X509CRL
    public byte[] getSignature() {
        return this.f809c.getSignature().getOctets();
    }

    @Override // java.security.cert.X509CRL
    public String getSigAlgName() {
        return this.sigAlgName;
    }

    @Override // java.security.cert.X509CRL
    public String getSigAlgOID() {
        return this.f809c.getSignatureAlgorithm().getAlgorithm().getId();
    }

    @Override // java.security.cert.X509CRL
    public byte[] getSigAlgParams() {
        return Arrays.clone(this.sigAlgParams);
    }

    @Override // java.security.cert.CRL
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("              Version: ").append(getVersion()).append(nl);
        buf.append("             IssuerDN: ").append(getIssuerDN()).append(nl);
        buf.append("          This update: ").append(getThisUpdate()).append(nl);
        buf.append("          Next update: ").append(getNextUpdate()).append(nl);
        buf.append("  Signature Algorithm: ").append(getSigAlgName()).append(nl);
        X509SignatureUtil.prettyPrintSignature(getSignature(), buf, nl);
        Extensions extensions = this.f809c.getTBSCertList().getExtensions();
        if (extensions != null) {
            Enumeration e = extensions.oids();
            if (e.hasMoreElements()) {
                buf.append("           Extensions: ").append(nl);
            }
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (ext.getExtnValue() != null) {
                    byte[] octs = ext.getExtnValue().getOctets();
                    ASN1InputStream dIn = new ASN1InputStream(octs);
                    buf.append("                       critical(").append(ext.isCritical()).append(") ");
                    try {
                        if (oid.equals((ASN1Primitive) Extension.cRLNumber)) {
                            buf.append(new CRLNumber(ASN1Integer.getInstance(dIn.readObject()).getPositiveValue())).append(nl);
                        } else if (oid.equals((ASN1Primitive) Extension.deltaCRLIndicator)) {
                            buf.append("Base CRL: " + new CRLNumber(ASN1Integer.getInstance(dIn.readObject()).getPositiveValue())).append(nl);
                        } else if (oid.equals((ASN1Primitive) Extension.issuingDistributionPoint)) {
                            buf.append(IssuingDistributionPoint.getInstance(dIn.readObject())).append(nl);
                        } else if (oid.equals((ASN1Primitive) Extension.cRLDistributionPoints)) {
                            buf.append(CRLDistPoint.getInstance(dIn.readObject())).append(nl);
                        } else if (oid.equals((ASN1Primitive) Extension.freshestCRL)) {
                            buf.append(CRLDistPoint.getInstance(dIn.readObject())).append(nl);
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
        Set<Object> set = getRevokedCertificates();
        if (set != null) {
            for (Object obj : set) {
                buf.append(obj);
                buf.append(nl);
            }
        }
        return buf.toString();
    }

    @Override // java.security.cert.CRL
    public boolean isRevoked(Certificate cert) {
        X500Name issuer;
        Extension currentCaName;
        if (!cert.getType().equals("X.509")) {
            throw new IllegalArgumentException("X.509 CRL used with non X.509 Cert");
        }
        Enumeration certs = this.f809c.getRevokedCertificateEnumeration();
        X500Name caName = this.f809c.getIssuer();
        if (certs.hasMoreElements()) {
            BigInteger serial = ((X509Certificate) cert).getSerialNumber();
            while (certs.hasMoreElements()) {
                TBSCertList.CRLEntry entry = TBSCertList.CRLEntry.getInstance(certs.nextElement());
                if (this.isIndirect && entry.hasExtensions() && (currentCaName = entry.getExtensions().getExtension(Extension.certificateIssuer)) != null) {
                    caName = X500Name.getInstance(GeneralNames.getInstance(currentCaName.getParsedValue()).getNames()[0].getName());
                }
                if (entry.getUserCertificate().hasValue(serial)) {
                    if (cert instanceof X509Certificate) {
                        issuer = X500Name.getInstance(((X509Certificate) cert).getIssuerX500Principal().getEncoded());
                    } else {
                        try {
                            issuer = com.android.internal.org.bouncycastle.asn1.x509.Certificate.getInstance(cert.getEncoded()).getIssuer();
                        } catch (CertificateEncodingException e) {
                            throw new IllegalArgumentException("Cannot process certificate: " + e.getMessage());
                        }
                    }
                    return caName.equals(issuer);
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static byte[] getExtensionOctets(CertificateList c, String oid) {
        ASN1OctetString extValue = getExtensionValue(c, oid);
        if (extValue != null) {
            return extValue.getOctets();
        }
        return null;
    }

    protected static ASN1OctetString getExtensionValue(CertificateList c, String oid) {
        Extension ext;
        Extensions exts = c.getTBSCertList().getExtensions();
        if (exts != null && (ext = exts.getExtension(new ASN1ObjectIdentifier(oid))) != null) {
            return ext.getExtnValue();
        }
        return null;
    }
}
