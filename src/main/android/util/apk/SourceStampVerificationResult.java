package android.util.apk;

import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class SourceStampVerificationResult {
    private final Certificate mCertificate;
    private final List<? extends Certificate> mCertificateLineage;
    private final boolean mPresent;
    private final boolean mVerified;

    private SourceStampVerificationResult(boolean present, boolean verified, Certificate certificate, List<? extends Certificate> certificateLineage) {
        this.mPresent = present;
        this.mVerified = verified;
        this.mCertificate = certificate;
        this.mCertificateLineage = certificateLineage;
    }

    public boolean isPresent() {
        return this.mPresent;
    }

    public boolean isVerified() {
        return this.mVerified;
    }

    public Certificate getCertificate() {
        return this.mCertificate;
    }

    public List<? extends Certificate> getCertificateLineage() {
        return this.mCertificateLineage;
    }

    public static SourceStampVerificationResult notPresent() {
        return new SourceStampVerificationResult(false, false, null, Collections.emptyList());
    }

    public static SourceStampVerificationResult verified(Certificate certificate, List<? extends Certificate> certificateLineage) {
        return new SourceStampVerificationResult(true, true, certificate, certificateLineage);
    }

    public static SourceStampVerificationResult notVerified() {
        return new SourceStampVerificationResult(true, false, null, Collections.emptyList());
    }
}
