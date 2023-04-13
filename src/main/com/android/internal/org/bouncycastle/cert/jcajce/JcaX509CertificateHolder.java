package com.android.internal.org.bouncycastle.cert.jcajce;

import com.android.internal.org.bouncycastle.asn1.x509.Certificate;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
/* loaded from: classes4.dex */
public class JcaX509CertificateHolder extends X509CertificateHolder {
    public JcaX509CertificateHolder(X509Certificate cert) throws CertificateEncodingException {
        super(Certificate.getInstance(cert.getEncoded()));
    }
}
