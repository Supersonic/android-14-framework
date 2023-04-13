package android.security.net.config;

import android.media.MediaMetrics;
import android.security.keystore.KeyProperties;
import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import libcore.io.IoUtils;
/* loaded from: classes3.dex */
abstract class DirectoryCertificateSource implements CertificateSource {
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f'};
    private static final String LOG_TAG = "DirectoryCertificateSrc";
    private final CertificateFactory mCertFactory;
    private Set<X509Certificate> mCertificates;
    private final File mDir;
    private final Object mLock = new Object();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public interface CertSelector {
        boolean match(X509Certificate x509Certificate);
    }

    protected abstract boolean isCertMarkedAsRemoved(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public DirectoryCertificateSource(File caDir) {
        this.mDir = caDir;
        try {
            this.mCertFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException("Failed to obtain X.509 CertificateFactory", e);
        }
    }

    @Override // android.security.net.config.CertificateSource
    public Set<X509Certificate> getCertificates() {
        String[] list;
        X509Certificate cert;
        synchronized (this.mLock) {
            Set<X509Certificate> set = this.mCertificates;
            if (set != null) {
                return set;
            }
            Set<X509Certificate> certs = new ArraySet<>();
            if (this.mDir.isDirectory()) {
                for (String caFile : this.mDir.list()) {
                    if (!isCertMarkedAsRemoved(caFile) && (cert = readCertificate(caFile)) != null) {
                        certs.add(cert);
                    }
                }
            }
            this.mCertificates = certs;
            return certs;
        }
    }

    @Override // android.security.net.config.CertificateSource
    public X509Certificate findBySubjectAndPublicKey(final X509Certificate cert) {
        return findCert(cert.getSubjectX500Principal(), new CertSelector() { // from class: android.security.net.config.DirectoryCertificateSource.1
            @Override // android.security.net.config.DirectoryCertificateSource.CertSelector
            public boolean match(X509Certificate ca) {
                return ca.getPublicKey().equals(cert.getPublicKey());
            }
        });
    }

    @Override // android.security.net.config.CertificateSource
    public X509Certificate findByIssuerAndSignature(final X509Certificate cert) {
        return findCert(cert.getIssuerX500Principal(), new CertSelector() { // from class: android.security.net.config.DirectoryCertificateSource.2
            @Override // android.security.net.config.DirectoryCertificateSource.CertSelector
            public boolean match(X509Certificate ca) {
                try {
                    cert.verify(ca.getPublicKey());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    @Override // android.security.net.config.CertificateSource
    public Set<X509Certificate> findAllByIssuerAndSignature(final X509Certificate cert) {
        return findCerts(cert.getIssuerX500Principal(), new CertSelector() { // from class: android.security.net.config.DirectoryCertificateSource.3
            @Override // android.security.net.config.DirectoryCertificateSource.CertSelector
            public boolean match(X509Certificate ca) {
                try {
                    cert.verify(ca.getPublicKey());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    @Override // android.security.net.config.CertificateSource
    public void handleTrustStorageUpdate() {
        synchronized (this.mLock) {
            this.mCertificates = null;
        }
    }

    private Set<X509Certificate> findCerts(X500Principal subj, CertSelector selector) {
        X509Certificate cert;
        String hash = getHash(subj);
        Set<X509Certificate> certs = null;
        for (int index = 0; index >= 0; index++) {
            String fileName = hash + MediaMetrics.SEPARATOR + index;
            if (!new File(this.mDir, fileName).exists()) {
                break;
            }
            if (!isCertMarkedAsRemoved(fileName) && (cert = readCertificate(fileName)) != null && subj.equals(cert.getSubjectX500Principal()) && selector.match(cert)) {
                if (certs == null) {
                    certs = new ArraySet<>();
                }
                certs.add(cert);
            }
        }
        return certs != null ? certs : Collections.emptySet();
    }

    private X509Certificate findCert(X500Principal subj, CertSelector selector) {
        X509Certificate cert;
        String hash = getHash(subj);
        for (int index = 0; index >= 0; index++) {
            String fileName = hash + MediaMetrics.SEPARATOR + index;
            if (new File(this.mDir, fileName).exists()) {
                if (!isCertMarkedAsRemoved(fileName) && (cert = readCertificate(fileName)) != null && subj.equals(cert.getSubjectX500Principal()) && selector.match(cert)) {
                    return cert;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    private String getHash(X500Principal name) {
        int hash = hashName(name);
        return intToHexString(hash, 8);
    }

    private static String intToHexString(int i, int minWidth) {
        char[] buf = new char[8];
        int cursor = 8;
        while (true) {
            cursor--;
            buf[cursor] = DIGITS[i & 15];
            int i2 = i >>> 4;
            i = i2;
            if (i2 == 0 && 8 - cursor >= minWidth) {
                return new String(buf, cursor, 8 - cursor);
            }
        }
    }

    private static int hashName(X500Principal principal) {
        try {
            byte[] digest = MessageDigest.getInstance(KeyProperties.DIGEST_MD5).digest(principal.getEncoded());
            int offset = 0 + 1;
            int offset2 = offset + 1;
            int i = ((digest[0] & 255) << 0) | ((digest[offset] & 255) << 8);
            int offset3 = offset2 + 1;
            return i | ((digest[offset2] & 255) << 16) | ((digest[offset3] & 255) << 24);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    private X509Certificate readCertificate(String file) {
        InputStream is = null;
        try {
            try {
                is = new BufferedInputStream(new FileInputStream(new File(this.mDir, file)));
                return (X509Certificate) this.mCertFactory.generateCertificate(is);
            } catch (IOException | CertificateException e) {
                Log.m109e(LOG_TAG, "Failed to read certificate from " + file, e);
                IoUtils.closeQuietly(is);
                return null;
            }
        } finally {
            IoUtils.closeQuietly(is);
        }
    }
}
