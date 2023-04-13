package com.android.internal.org.bouncycastle.cert;

import com.android.internal.org.bouncycastle.asn1.ASN1Encodable;
import com.android.internal.org.bouncycastle.asn1.ASN1EncodableVector;
import com.android.internal.org.bouncycastle.asn1.ASN1Encoding;
import com.android.internal.org.bouncycastle.asn1.ASN1Integer;
import com.android.internal.org.bouncycastle.asn1.ASN1Object;
import com.android.internal.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.android.internal.org.bouncycastle.asn1.DERBitString;
import com.android.internal.org.bouncycastle.asn1.DERSequence;
import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.Certificate;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.Extensions;
import com.android.internal.org.bouncycastle.asn1.x509.ExtensionsGenerator;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.TBSCertificate;
import com.android.internal.org.bouncycastle.asn1.x509.Time;
import com.android.internal.org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import com.android.internal.org.bouncycastle.operator.ContentSigner;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
/* loaded from: classes4.dex */
public class X509v3CertificateBuilder {
    private ExtensionsGenerator extGenerator;
    private V3TBSCertificateGenerator tbsGen;

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore), new Time(notAfter), subject, publicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Date notBefore, Date notAfter, Locale dateLocale, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this(issuer, serial, new Time(notBefore, dateLocale), new Time(notAfter, dateLocale), subject, publicKeyInfo);
    }

    public X509v3CertificateBuilder(X500Name issuer, BigInteger serial, Time notBefore, Time notAfter, X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        V3TBSCertificateGenerator v3TBSCertificateGenerator = new V3TBSCertificateGenerator();
        this.tbsGen = v3TBSCertificateGenerator;
        v3TBSCertificateGenerator.setSerialNumber(new ASN1Integer(serial));
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setStartDate(notBefore);
        this.tbsGen.setEndDate(notAfter);
        this.tbsGen.setSubject(subject);
        this.tbsGen.setSubjectPublicKeyInfo(publicKeyInfo);
        this.extGenerator = new ExtensionsGenerator();
    }

    public X509v3CertificateBuilder(X509CertificateHolder template) {
        V3TBSCertificateGenerator v3TBSCertificateGenerator = new V3TBSCertificateGenerator();
        this.tbsGen = v3TBSCertificateGenerator;
        v3TBSCertificateGenerator.setSerialNumber(new ASN1Integer(template.getSerialNumber()));
        this.tbsGen.setIssuer(template.getIssuer());
        this.tbsGen.setStartDate(new Time(template.getNotBefore()));
        this.tbsGen.setEndDate(new Time(template.getNotAfter()));
        this.tbsGen.setSubject(template.getSubject());
        this.tbsGen.setSubjectPublicKeyInfo(template.getSubjectPublicKeyInfo());
        this.extGenerator = new ExtensionsGenerator();
        Extensions exts = template.getExtensions();
        Enumeration en = exts.oids();
        while (en.hasMoreElements()) {
            this.extGenerator.addExtension(exts.getExtension((ASN1ObjectIdentifier) en.nextElement()));
        }
    }

    public boolean hasExtension(ASN1ObjectIdentifier oid) {
        return doGetExtension(oid) != null;
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        return doGetExtension(oid);
    }

    private Extension doGetExtension(ASN1ObjectIdentifier oid) {
        Extensions exts = this.extGenerator.generate();
        return exts.getExtension(oid);
    }

    public X509v3CertificateBuilder setSubjectUniqueID(boolean[] uniqueID) {
        this.tbsGen.setSubjectUniqueID(booleanToBitString(uniqueID));
        return this;
    }

    public X509v3CertificateBuilder setIssuerUniqueID(boolean[] uniqueID) {
        this.tbsGen.setIssuerUniqueID(booleanToBitString(uniqueID));
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        try {
            this.extGenerator.addExtension(oid, isCritical, value);
            return this;
        } catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
    }

    public X509v3CertificateBuilder addExtension(Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }

    public X509v3CertificateBuilder addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator.addExtension(oid, isCritical, encodedValue);
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws CertIOException {
        try {
            this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(oid, isCritical, value.toASN1Primitive().getEncoded(ASN1Encoding.DER)));
            return this;
        } catch (IOException e) {
            throw new CertIOException("cannot encode extension: " + e.getMessage(), e);
        }
    }

    public X509v3CertificateBuilder replaceExtension(Extension extension) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, extension);
        return this;
    }

    public X509v3CertificateBuilder replaceExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] encodedValue) throws CertIOException {
        this.extGenerator = CertUtils.doReplaceExtension(this.extGenerator, new Extension(oid, isCritical, encodedValue));
        return this;
    }

    public X509v3CertificateBuilder removeExtension(ASN1ObjectIdentifier oid) {
        this.extGenerator = CertUtils.doRemoveExtension(this.extGenerator, oid);
        return this;
    }

    public X509v3CertificateBuilder copyAndAddExtension(ASN1ObjectIdentifier oid, boolean isCritical, X509CertificateHolder certHolder) {
        Certificate cert = certHolder.toASN1Structure();
        Extension extension = cert.getTBSCertificate().getExtensions().getExtension(oid);
        if (extension == null) {
            throw new NullPointerException("extension " + oid + " not present");
        }
        this.extGenerator.addExtension(oid, isCritical, extension.getExtnValue().getOctets());
        return this;
    }

    public X509CertificateHolder build(ContentSigner signer) {
        this.tbsGen.setSignature(signer.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        try {
            TBSCertificate tbsCert = this.tbsGen.generateTBSCertificate();
            return new X509CertificateHolder(generateStructure(tbsCert, signer.getAlgorithmIdentifier(), generateSig(signer, tbsCert)));
        } catch (IOException e) {
            throw new IllegalArgumentException("cannot produce certificate signature");
        }
    }

    private static byte[] generateSig(ContentSigner signer, ASN1Object tbsObj) throws IOException {
        OutputStream sOut = signer.getOutputStream();
        tbsObj.encodeTo(sOut, ASN1Encoding.DER);
        sOut.close();
        return signer.getSignature();
    }

    private static Certificate generateStructure(TBSCertificate tbsCert, AlgorithmIdentifier sigAlgId, byte[] signature) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(tbsCert);
        v.add(sigAlgId);
        v.add(new DERBitString(signature));
        return Certificate.getInstance(new DERSequence(v));
    }

    static DERBitString booleanToBitString(boolean[] id) {
        byte[] bytes = new byte[(id.length + 7) / 8];
        for (int i = 0; i != id.length; i++) {
            int i2 = i / 8;
            bytes[i2] = (byte) (bytes[i2] | (id[i] ? 1 << (7 - (i % 8)) : 0));
        }
        int i3 = id.length;
        int pad = i3 % 8;
        if (pad == 0) {
            return new DERBitString(bytes);
        }
        return new DERBitString(bytes, 8 - pad);
    }
}
