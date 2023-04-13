package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.util.ASN1Dump;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x509.CRLDistPoint;
import com.android.internal.org.bouncycastle.asn1.x509.CRLNumber;
import com.android.internal.org.bouncycastle.asn1.x509.CertificateList;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.internal.org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import com.android.internal.org.bouncycastle.asn1.x509.TBSCertList;
import com.android.internal.org.bouncycastle.jce.X509Principal;
import com.android.internal.org.bouncycastle.util.Strings;
import com.android.internal.org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
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
import java.util.Set;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes4.dex */
public class X509CRLObject extends X509CRL {

    /* renamed from: c */
    private CertificateList f825c;
    private int hashCodeValue;
    private boolean isHashCodeSet = false;
    private boolean isIndirect;
    private String sigAlgName;
    private byte[] sigAlgParams;

    public static boolean isIndirectCRL(X509CRL crl) throws CRLException {
        try {
            byte[] idp = crl.getExtensionValue(Extension.issuingDistributionPoint.getId());
            if (idp != null) {
                if (IssuingDistributionPoint.getInstance(ASN1OctetString.getInstance(idp).getOctets()).isIndirectCRL()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new ExtCRLException("Exception reading IssuingDistributionPoint", e);
        }
    }

    public X509CRLObject(CertificateList c) throws CRLException {
        this.f825c = c;
        try {
            this.sigAlgName = X509SignatureUtil.getSignatureName(c.getSignatureAlgorithm());
            if (c.getSignatureAlgorithm().getParameters() != null) {
                this.sigAlgParams = c.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded(ASN1Encoding.DER);
            } else {
                this.sigAlgParams = null;
            }
            this.isIndirect = isIndirectCRL(this);
        } catch (Exception e) {
            throw new CRLException("CRL contents invalid: " + e);
        }
    }

    @Override // java.security.cert.X509Extension
    public boolean hasUnsupportedCriticalExtension() {
        Set extns = getCriticalExtensionOIDs();
        if (extns == null) {
            return false;
        }
        extns.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
        extns.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
        return !extns.isEmpty();
    }

    private Set getExtensionOIDs(boolean critical) {
        Extensions extensions;
        if (getVersion() == 2 && (extensions = this.f825c.getTBSCertList().getExtensions()) != null) {
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
        Extension ext;
        Extensions exts = this.f825c.getTBSCertList().getExtensions();
        if (exts != null && (ext = exts.getExtension(new ASN1ObjectIdentifier(oid))) != null) {
            try {
                return ext.getExtnValue().getEncoded();
            } catch (Exception e) {
                throw new IllegalStateException("error parsing " + e.toString());
            }
        }
        return null;
    }

    @Override // java.security.cert.X509CRL
    public byte[] getEncoded() throws CRLException {
        try {
            return this.f825c.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey key) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature sig;
        try {
            sig = Signature.getInstance(getSigAlgName(), BouncyCastleProvider.PROVIDER_NAME);
        } catch (Exception e) {
            sig = Signature.getInstance(getSigAlgName());
        }
        doVerify(key, sig);
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey key, String sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        Signature sig;
        if (sigProvider != null) {
            sig = Signature.getInstance(getSigAlgName(), sigProvider);
        } else {
            sig = Signature.getInstance(getSigAlgName());
        }
        doVerify(key, sig);
    }

    @Override // java.security.cert.X509CRL
    public void verify(PublicKey key, Provider sigProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig;
        if (sigProvider != null) {
            sig = Signature.getInstance(getSigAlgName(), sigProvider);
        } else {
            sig = Signature.getInstance(getSigAlgName());
        }
        doVerify(key, sig);
    }

    private void doVerify(PublicKey key, Signature sig) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (!this.f825c.getSignatureAlgorithm().equals(this.f825c.getTBSCertList().getSignature())) {
            throw new CRLException("Signature algorithm on CertificateList does not match TBSCertList.");
        }
        sig.initVerify(key);
        sig.update(getTBSCertList());
        if (!sig.verify(getSignature())) {
            throw new SignatureException("CRL does not verify with supplied public key.");
        }
    }

    @Override // java.security.cert.X509CRL
    public int getVersion() {
        return this.f825c.getVersionNumber();
    }

    @Override // java.security.cert.X509CRL
    public Principal getIssuerDN() {
        return new X509Principal(X500Name.getInstance(this.f825c.getIssuer().toASN1Primitive()));
    }

    @Override // java.security.cert.X509CRL
    public X500Principal getIssuerX500Principal() {
        try {
            return new X500Principal(this.f825c.getIssuer().getEncoded());
        } catch (IOException e) {
            throw new IllegalStateException("can't encode issuer DN");
        }
    }

    @Override // java.security.cert.X509CRL
    public Date getThisUpdate() {
        return this.f825c.getThisUpdate().getDate();
    }

    @Override // java.security.cert.X509CRL
    public Date getNextUpdate() {
        if (this.f825c.getNextUpdate() != null) {
            return this.f825c.getNextUpdate().getDate();
        }
        return null;
    }

    private Set loadCRLEntries() {
        Extension currentCaName;
        Set entrySet = new HashSet();
        Enumeration certs = this.f825c.getRevokedCertificateEnumeration();
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
        Enumeration certs = this.f825c.getRevokedCertificateEnumeration();
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
            return this.f825c.getTBSCertList().getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }

    @Override // java.security.cert.X509CRL
    public byte[] getSignature() {
        return this.f825c.getSignature().getOctets();
    }

    @Override // java.security.cert.X509CRL
    public String getSigAlgName() {
        return this.sigAlgName;
    }

    @Override // java.security.cert.X509CRL
    public String getSigAlgOID() {
        return this.f825c.getSignatureAlgorithm().getAlgorithm().getId();
    }

    @Override // java.security.cert.X509CRL
    public byte[] getSigAlgParams() {
        byte[] bArr = this.sigAlgParams;
        if (bArr != null) {
            byte[] tmp = new byte[bArr.length];
            System.arraycopy(bArr, 0, tmp, 0, tmp.length);
            return tmp;
        }
        return null;
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
        byte[] sig = getSignature();
        buf.append("            Signature: ").append(new String(Hex.encode(sig, 0, 20))).append(nl);
        for (int i = 20; i < sig.length; i += 20) {
            if (i < sig.length - 20) {
                buf.append("                       ").append(new String(Hex.encode(sig, i, 20))).append(nl);
            } else {
                buf.append("                       ").append(new String(Hex.encode(sig, i, sig.length - i))).append(nl);
            }
        }
        Extensions extensions = this.f825c.getTBSCertList().getExtensions();
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
            throw new RuntimeException("X.509 CRL used with non X.509 Cert");
        }
        Enumeration certs = this.f825c.getRevokedCertificateEnumeration();
        X500Name caName = this.f825c.getIssuer();
        if (certs != null) {
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
                            throw new RuntimeException("Cannot process certificate");
                        }
                    }
                    if (!caName.equals(issuer)) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override // java.security.cert.X509CRL
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof X509CRL) {
            if (other instanceof X509CRLObject) {
                X509CRLObject crlObject = (X509CRLObject) other;
                if (this.isHashCodeSet) {
                    boolean otherIsHashCodeSet = crlObject.isHashCodeSet;
                    if (otherIsHashCodeSet && crlObject.hashCodeValue != this.hashCodeValue) {
                        return false;
                    }
                }
                return this.f825c.equals(crlObject.f825c);
            }
            return super.equals(other);
        }
        return false;
    }

    @Override // java.security.cert.X509CRL
    public int hashCode() {
        if (!this.isHashCodeSet) {
            this.isHashCodeSet = true;
            this.hashCodeValue = super.hashCode();
        }
        return this.hashCodeValue;
    }
}
