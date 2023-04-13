package com.android.internal.org.bouncycastle.jce.provider;

import com.android.internal.org.bouncycastle.jcajce.PKIXCertRevocationChecker;
import com.android.internal.org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
/* loaded from: classes4.dex */
class WrappedRevocationChecker implements PKIXCertRevocationChecker {
    private final PKIXCertPathChecker checker;

    public WrappedRevocationChecker(PKIXCertPathChecker checker) {
        this.checker = checker;
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.PKIXCertRevocationChecker
    public void setParameter(String name, Object value) {
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.PKIXCertRevocationChecker
    public void initialize(PKIXCertRevocationCheckerParameters params) throws CertPathValidatorException {
        this.checker.init(false);
    }

    @Override // com.android.internal.org.bouncycastle.jcajce.PKIXCertRevocationChecker
    public void check(Certificate cert) throws CertPathValidatorException {
        this.checker.check(cert);
    }
}
