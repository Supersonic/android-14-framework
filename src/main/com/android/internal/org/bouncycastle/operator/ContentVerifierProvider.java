package com.android.internal.org.bouncycastle.operator;

import com.android.internal.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import com.android.internal.org.bouncycastle.cert.X509CertificateHolder;
/* loaded from: classes4.dex */
public interface ContentVerifierProvider {
    ContentVerifier get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException;

    X509CertificateHolder getAssociatedCertificate();

    boolean hasAssociatedCertificate();
}
