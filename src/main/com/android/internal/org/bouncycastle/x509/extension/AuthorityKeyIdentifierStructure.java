package com.android.internal.org.bouncycastle.x509.extension;

import com.android.internal.org.bouncycastle.asn1.ASN1OctetString;
import com.android.internal.org.bouncycastle.asn1.ASN1Sequence;
import com.android.internal.org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import com.android.internal.org.bouncycastle.asn1.x509.Extension;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralName;
import com.android.internal.org.bouncycastle.asn1.x509.GeneralNames;
import com.android.internal.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.android.internal.org.bouncycastle.asn1.x509.X509Extension;
import com.android.internal.org.bouncycastle.jce.PrincipalUtil;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
/* loaded from: classes4.dex */
public class AuthorityKeyIdentifierStructure extends AuthorityKeyIdentifier {
    public AuthorityKeyIdentifierStructure(byte[] encodedValue) throws IOException {
        super((ASN1Sequence) X509ExtensionUtil.fromExtensionValue(encodedValue));
    }

    public AuthorityKeyIdentifierStructure(X509Extension extension) {
        super((ASN1Sequence) extension.getParsedValue());
    }

    public AuthorityKeyIdentifierStructure(Extension extension) {
        super((ASN1Sequence) extension.getParsedValue());
    }

    private static ASN1Sequence fromCertificate(X509Certificate certificate) throws CertificateParsingException {
        try {
            if (certificate.getVersion() != 3) {
                GeneralName genName = new GeneralName(PrincipalUtil.getIssuerX509Principal(certificate));
                SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(certificate.getPublicKey().getEncoded());
                return (ASN1Sequence) new AuthorityKeyIdentifier(info, new GeneralNames(genName), certificate.getSerialNumber()).toASN1Primitive();
            }
            GeneralName genName2 = new GeneralName(PrincipalUtil.getIssuerX509Principal(certificate));
            byte[] ext = certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
            if (ext != null) {
                ASN1OctetString str = (ASN1OctetString) X509ExtensionUtil.fromExtensionValue(ext);
                return (ASN1Sequence) new AuthorityKeyIdentifier(str.getOctets(), new GeneralNames(genName2), certificate.getSerialNumber()).toASN1Primitive();
            }
            SubjectPublicKeyInfo info2 = SubjectPublicKeyInfo.getInstance(certificate.getPublicKey().getEncoded());
            return (ASN1Sequence) new AuthorityKeyIdentifier(info2, new GeneralNames(genName2), certificate.getSerialNumber()).toASN1Primitive();
        } catch (Exception e) {
            throw new CertificateParsingException("Exception extracting certificate details: " + e.toString());
        }
    }

    private static ASN1Sequence fromKey(PublicKey pubKey) throws InvalidKeyException {
        try {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(pubKey.getEncoded());
            return (ASN1Sequence) new AuthorityKeyIdentifier(info).toASN1Primitive();
        } catch (Exception e) {
            throw new InvalidKeyException("can't process key: " + e);
        }
    }

    public AuthorityKeyIdentifierStructure(X509Certificate certificate) throws CertificateParsingException {
        super(fromCertificate(certificate));
    }

    public AuthorityKeyIdentifierStructure(PublicKey pubKey) throws InvalidKeyException {
        super(fromKey(pubKey));
    }
}
