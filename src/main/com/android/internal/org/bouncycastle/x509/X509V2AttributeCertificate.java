package com.android.internal.org.bouncycastle.x509;

import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1InputStream;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.x509.AttributeCertificate;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes4.dex */
public class X509V2AttributeCertificate implements X509AttributeCertificate {
    private AttributeCertificate cert;
    private Date notAfter;
    private Date notBefore;

    private static AttributeCertificate getObject(InputStream in) throws IOException {
        try {
            return AttributeCertificate.getInstance(new ASN1InputStream(in).readObject());
        } catch (IOException e) {
            throw e;
        } catch (Exception e2) {
            throw new IOException("exception decoding certificate structure: " + e2.toString());
        }
    }

    public X509V2AttributeCertificate(InputStream encIn) throws IOException {
        this(getObject(encIn));
    }

    public X509V2AttributeCertificate(byte[] encoded) throws IOException {
        this(new ByteArrayInputStream(encoded));
    }

    X509V2AttributeCertificate(AttributeCertificate cert) throws IOException {
        this.cert = cert;
        try {
            this.notAfter = cert.getAcinfo().getAttrCertValidityPeriod().getNotAfterTime().getDate();
            this.notBefore = cert.getAcinfo().getAttrCertValidityPeriod().getNotBeforeTime().getDate();
        } catch (ParseException e) {
            throw new IOException("invalid data structure in certificate!");
        }
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public int getVersion() {
        return this.cert.getAcinfo().getVersion().intValueExact() + 1;
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public BigInteger getSerialNumber() {
        return this.cert.getAcinfo().getSerialNumber().getValue();
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public AttributeCertificateHolder getHolder() {
        return new AttributeCertificateHolder((ASN1Sequence) this.cert.getAcinfo().getHolder().toASN1Primitive());
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public AttributeCertificateIssuer getIssuer() {
        return new AttributeCertificateIssuer(this.cert.getAcinfo().getIssuer());
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public Date getNotBefore() {
        return this.notBefore;
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public Date getNotAfter() {
        return this.notAfter;
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public boolean[] getIssuerUniqueID() {
        DERBitString id = this.cert.getAcinfo().getIssuerUniqueID();
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

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        checkValidity(new Date());
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        if (date.after(getNotAfter())) {
            throw new CertificateExpiredException("certificate expired on " + getNotAfter());
        }
        if (date.before(getNotBefore())) {
            throw new CertificateNotYetValidException("certificate not valid till " + getNotBefore());
        }
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public byte[] getSignature() {
        return this.cert.getSignatureValue().getOctets();
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public final void verify(PublicKey key, String provider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        if (!this.cert.getSignatureAlgorithm().equals(this.cert.getAcinfo().getSignature())) {
            throw new CertificateException("Signature algorithm in certificate info not same as outer certificate");
        }
        Signature signature = Signature.getInstance(this.cert.getSignatureAlgorithm().getAlgorithm().getId(), provider);
        signature.initVerify(key);
        try {
            signature.update(this.cert.getAcinfo().getEncoded());
            if (!signature.verify(getSignature())) {
                throw new InvalidKeyException("Public key presented not for certificate signature");
            }
        } catch (IOException e) {
            throw new SignatureException("Exception encoding certificate info object");
        }
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public byte[] getEncoded() throws IOException {
        return this.cert.getEncoded();
    }

    @Override // java.security.cert.X509Extension
    public byte[] getExtensionValue(String oid) {
        Extension ext;
        Extensions extensions = this.cert.getAcinfo().getExtensions();
        if (extensions != null && (ext = extensions.getExtension(new ASN1ObjectIdentifier(oid))) != null) {
            try {
                return ext.getExtnValue().getEncoded(ASN1Encoding.DER);
            } catch (Exception e) {
                throw new RuntimeException("error encoding " + e.toString());
            }
        }
        return null;
    }

    private Set getExtensionOIDs(boolean critical) {
        Extensions extensions = this.cert.getAcinfo().getExtensions();
        if (extensions != null) {
            Set set = new HashSet();
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) e.nextElement();
                Extension ext = extensions.getExtension(oid);
                if (ext.isCritical() == critical) {
                    set.add(oid.getId());
                }
            }
            return set;
        }
        return null;
    }

    @Override // java.security.cert.X509Extension
    public Set getNonCriticalExtensionOIDs() {
        return getExtensionOIDs(false);
    }

    @Override // java.security.cert.X509Extension
    public Set getCriticalExtensionOIDs() {
        return getExtensionOIDs(true);
    }

    @Override // java.security.cert.X509Extension
    public boolean hasUnsupportedCriticalExtension() {
        Set extensions = getCriticalExtensionOIDs();
        return (extensions == null || extensions.isEmpty()) ? false : true;
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public X509Attribute[] getAttributes() {
        ASN1Sequence seq = this.cert.getAcinfo().getAttributes();
        X509Attribute[] attrs = new X509Attribute[seq.size()];
        for (int i = 0; i != seq.size(); i++) {
            attrs[i] = new X509Attribute(seq.getObjectAt(i));
        }
        return attrs;
    }

    @Override // com.android.internal.org.bouncycastle.x509.X509AttributeCertificate
    public X509Attribute[] getAttributes(String oid) {
        ASN1Sequence seq = this.cert.getAcinfo().getAttributes();
        List list = new ArrayList();
        for (int i = 0; i != seq.size(); i++) {
            X509Attribute attr = new X509Attribute(seq.getObjectAt(i));
            if (attr.getOID().equals(oid)) {
                list.add(attr);
            }
        }
        int i2 = list.size();
        if (i2 == 0) {
            return null;
        }
        return (X509Attribute[]) list.toArray(new X509Attribute[list.size()]);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof X509AttributeCertificate) {
            X509AttributeCertificate other = (X509AttributeCertificate) o;
            try {
                byte[] b1 = getEncoded();
                byte[] b2 = other.getEncoded();
                return Arrays.areEqual(b1, b2);
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public int hashCode() {
        try {
            return Arrays.hashCode(getEncoded());
        } catch (IOException e) {
            return 0;
        }
    }
}
