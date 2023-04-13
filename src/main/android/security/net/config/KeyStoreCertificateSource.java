package android.security.net.config;

import android.util.ArraySet;
import com.android.org.conscrypt.TrustedCertificateIndex;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
/* loaded from: classes3.dex */
class KeyStoreCertificateSource implements CertificateSource {
    private Set<X509Certificate> mCertificates;
    private TrustedCertificateIndex mIndex;
    private final KeyStore mKeyStore;
    private final Object mLock = new Object();

    public KeyStoreCertificateSource(KeyStore ks) {
        this.mKeyStore = ks;
    }

    @Override // android.security.net.config.CertificateSource
    public Set<X509Certificate> getCertificates() {
        ensureInitialized();
        return this.mCertificates;
    }

    private void ensureInitialized() {
        synchronized (this.mLock) {
            if (this.mCertificates != null) {
                return;
            }
            try {
                TrustedCertificateIndex localIndex = new TrustedCertificateIndex();
                Set<X509Certificate> certificates = new ArraySet<>(this.mKeyStore.size());
                Enumeration<String> en = this.mKeyStore.aliases();
                while (en.hasMoreElements()) {
                    String alias = en.nextElement();
                    X509Certificate cert = (X509Certificate) this.mKeyStore.getCertificate(alias);
                    if (cert != null) {
                        certificates.add(cert);
                        localIndex.index(cert);
                    }
                }
                this.mIndex = localIndex;
                this.mCertificates = certificates;
            } catch (KeyStoreException e) {
                throw new RuntimeException("Failed to load certificates from KeyStore", e);
            }
        }
    }

    @Override // android.security.net.config.CertificateSource
    public X509Certificate findBySubjectAndPublicKey(X509Certificate cert) {
        ensureInitialized();
        java.security.cert.TrustAnchor anchor = this.mIndex.findBySubjectAndPublicKey(cert);
        if (anchor == null) {
            return null;
        }
        return anchor.getTrustedCert();
    }

    @Override // android.security.net.config.CertificateSource
    public X509Certificate findByIssuerAndSignature(X509Certificate cert) {
        ensureInitialized();
        java.security.cert.TrustAnchor anchor = this.mIndex.findByIssuerAndSignature(cert);
        if (anchor == null) {
            return null;
        }
        return anchor.getTrustedCert();
    }

    @Override // android.security.net.config.CertificateSource
    public Set<X509Certificate> findAllByIssuerAndSignature(X509Certificate cert) {
        ensureInitialized();
        Set<java.security.cert.TrustAnchor> anchors = this.mIndex.findAllByIssuerAndSignature(cert);
        if (anchors.isEmpty()) {
            return Collections.emptySet();
        }
        Set<X509Certificate> certs = new ArraySet<>(anchors.size());
        for (java.security.cert.TrustAnchor anchor : anchors) {
            certs.add(anchor.getTrustedCert());
        }
        return certs;
    }

    @Override // android.security.net.config.CertificateSource
    public void handleTrustStorageUpdate() {
    }
}
