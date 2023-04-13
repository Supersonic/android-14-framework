package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x500.X500NameStyle;
import com.android.internal.org.bouncycastle.x509.X509AttributeCertificate;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes4.dex */
class PrincipalUtils {
    PrincipalUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X500Name getCA(TrustAnchor trustAnchor) {
        return getX500Name(notNull(trustAnchor).getCA());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X500Name getEncodedIssuerPrincipal(Object cert) {
        if (cert instanceof X509Certificate) {
            return getIssuerPrincipal((X509Certificate) cert);
        }
        return getX500Name((X500Principal) ((X509AttributeCertificate) cert).getIssuer().getPrincipals()[0]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X500Name getIssuerPrincipal(X509Certificate certificate) {
        return getX500Name(notNull(certificate).getIssuerX500Principal());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X500Name getIssuerPrincipal(X509CRL crl) {
        return getX500Name(notNull(crl).getIssuerX500Principal());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X500Name getSubjectPrincipal(X509Certificate certificate) {
        return getX500Name(notNull(certificate).getSubjectX500Principal());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static X500Name getX500Name(X500Principal principal) {
        X500Name name = X500Name.getInstance(getEncoded(principal));
        return notNull(name);
    }

    static X500Name getX500Name(X500NameStyle style, X500Principal principal) {
        X500Name name = X500Name.getInstance(style, getEncoded(principal));
        return notNull(name);
    }

    private static byte[] getEncoded(X500Principal principal) {
        byte[] encoding = notNull(principal).getEncoded();
        return notNull(encoding);
    }

    private static byte[] notNull(byte[] encoding) {
        if (encoding == null) {
            throw new IllegalStateException();
        }
        return encoding;
    }

    private static TrustAnchor notNull(TrustAnchor trustAnchor) {
        if (trustAnchor == null) {
            throw new IllegalStateException();
        }
        return trustAnchor;
    }

    private static X509Certificate notNull(X509Certificate certificate) {
        if (certificate == null) {
            throw new IllegalStateException();
        }
        return certificate;
    }

    private static X509CRL notNull(X509CRL crl) {
        if (crl == null) {
            throw new IllegalStateException();
        }
        return crl;
    }

    private static X500Name notNull(X500Name name) {
        if (name == null) {
            throw new IllegalStateException();
        }
        return name;
    }

    private static X500Principal notNull(X500Principal principal) {
        if (principal == null) {
            throw new IllegalStateException();
        }
        return principal;
    }
}
