package com.android.internal.org.bouncycastle.jce;

import com.android.internal.org.bouncycastle.asn1.ASN1Primitive;
import com.android.internal.org.bouncycastle.asn1.x509.TBSCertList;
import com.android.internal.org.bouncycastle.asn1.x509.TBSCertificateStructure;
import com.android.internal.org.bouncycastle.asn1.x509.X509Name;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
/* loaded from: classes4.dex */
public class PrincipalUtil {
    public static X509Principal getIssuerX509Principal(X509Certificate cert) throws CertificateEncodingException {
        try {
            TBSCertificateStructure tbsCert = TBSCertificateStructure.getInstance(ASN1Primitive.fromByteArray(cert.getTBSCertificate()));
            return new X509Principal(X509Name.getInstance(tbsCert.getIssuer()));
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    public static X509Principal getSubjectX509Principal(X509Certificate cert) throws CertificateEncodingException {
        try {
            TBSCertificateStructure tbsCert = TBSCertificateStructure.getInstance(ASN1Primitive.fromByteArray(cert.getTBSCertificate()));
            return new X509Principal(X509Name.getInstance(tbsCert.getSubject()));
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    public static X509Principal getIssuerX509Principal(X509CRL crl) throws CRLException {
        try {
            TBSCertList tbsCertList = TBSCertList.getInstance(ASN1Primitive.fromByteArray(crl.getTBSCertList()));
            return new X509Principal(X509Name.getInstance(tbsCertList.getIssuer()));
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }
}
