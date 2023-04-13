package com.android.internal.org.bouncycastle.jcajce.interfaces;

import com.android.internal.org.bouncycastle.asn1.x500.X500Name;
import com.android.internal.org.bouncycastle.asn1.x509.TBSCertificate;
/* loaded from: classes4.dex */
public interface BCX509Certificate {
    X500Name getIssuerX500Name();

    X500Name getSubjectX500Name();

    TBSCertificate getTBSCertificateNative();
}
