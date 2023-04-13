package com.android.internal.org.bouncycastle.jce.provider;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
/* loaded from: classes4.dex */
class RecoverableCertPathValidatorException extends CertPathValidatorException {
    public RecoverableCertPathValidatorException(String msg, Throwable cause, CertPath certPath, int index) {
        super(msg, cause, certPath, index);
    }
}
