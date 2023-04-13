package android.security.net.config;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Set;
/* loaded from: classes3.dex */
public final class WfaCertificateSource extends DirectoryCertificateSource {
    private static final String CACERTS_WFA_PATH = "/apex/com.android.wifi/etc/security/cacerts_wfa";

    @Override // android.security.net.config.DirectoryCertificateSource, android.security.net.config.CertificateSource
    public /* bridge */ /* synthetic */ Set findAllByIssuerAndSignature(X509Certificate x509Certificate) {
        return super.findAllByIssuerAndSignature(x509Certificate);
    }

    @Override // android.security.net.config.DirectoryCertificateSource, android.security.net.config.CertificateSource
    public /* bridge */ /* synthetic */ X509Certificate findByIssuerAndSignature(X509Certificate x509Certificate) {
        return super.findByIssuerAndSignature(x509Certificate);
    }

    @Override // android.security.net.config.DirectoryCertificateSource, android.security.net.config.CertificateSource
    public /* bridge */ /* synthetic */ X509Certificate findBySubjectAndPublicKey(X509Certificate x509Certificate) {
        return super.findBySubjectAndPublicKey(x509Certificate);
    }

    @Override // android.security.net.config.DirectoryCertificateSource, android.security.net.config.CertificateSource
    public /* bridge */ /* synthetic */ Set getCertificates() {
        return super.getCertificates();
    }

    @Override // android.security.net.config.DirectoryCertificateSource, android.security.net.config.CertificateSource
    public /* bridge */ /* synthetic */ void handleTrustStorageUpdate() {
        super.handleTrustStorageUpdate();
    }

    /* loaded from: classes3.dex */
    private static class NoPreloadHolder {
        private static final WfaCertificateSource INSTANCE = new WfaCertificateSource();

        private NoPreloadHolder() {
        }
    }

    private WfaCertificateSource() {
        super(new File(CACERTS_WFA_PATH));
    }

    public static WfaCertificateSource getInstance() {
        return NoPreloadHolder.INSTANCE;
    }

    @Override // android.security.net.config.DirectoryCertificateSource
    protected boolean isCertMarkedAsRemoved(String caFile) {
        return false;
    }
}
