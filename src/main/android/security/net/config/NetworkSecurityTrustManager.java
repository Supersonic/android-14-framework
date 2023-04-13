package android.security.net.config;

import android.util.ArrayMap;
import com.android.org.conscrypt.CertPinManager;
import com.android.org.conscrypt.TrustManagerImpl;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
/* loaded from: classes3.dex */
public class NetworkSecurityTrustManager extends X509ExtendedTrustManager {
    private final TrustManagerImpl mDelegate;
    private X509Certificate[] mIssuers;
    private final Object mIssuersLock = new Object();
    private final NetworkSecurityConfig mNetworkSecurityConfig;

    public NetworkSecurityTrustManager(NetworkSecurityConfig config) {
        if (config == null) {
            throw new NullPointerException("config must not be null");
        }
        this.mNetworkSecurityConfig = config;
        try {
            TrustedCertificateStoreAdapter certStore = new TrustedCertificateStoreAdapter(config);
            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(null);
            this.mDelegate = new TrustManagerImpl(store, (CertPinManager) null, certStore);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        this.mDelegate.checkClientTrusted(chain, authType);
    }

    @Override // javax.net.ssl.X509ExtendedTrustManager
    public void checkClientTrusted(X509Certificate[] certs, String authType, Socket socket) throws CertificateException {
        this.mDelegate.checkClientTrusted(certs, authType, socket);
    }

    @Override // javax.net.ssl.X509ExtendedTrustManager
    public void checkClientTrusted(X509Certificate[] certs, String authType, SSLEngine engine) throws CertificateException {
        this.mDelegate.checkClientTrusted(certs, authType, engine);
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        checkServerTrusted(certs, authType, (String) null);
    }

    @Override // javax.net.ssl.X509ExtendedTrustManager
    public void checkServerTrusted(X509Certificate[] certs, String authType, Socket socket) throws CertificateException {
        List<X509Certificate> trustedChain = this.mDelegate.getTrustedChainForServer(certs, authType, socket);
        checkPins(trustedChain);
    }

    @Override // javax.net.ssl.X509ExtendedTrustManager
    public void checkServerTrusted(X509Certificate[] certs, String authType, SSLEngine engine) throws CertificateException {
        List<X509Certificate> trustedChain = this.mDelegate.getTrustedChainForServer(certs, authType, engine);
        checkPins(trustedChain);
    }

    public List<X509Certificate> checkServerTrusted(X509Certificate[] certs, String authType, String host) throws CertificateException {
        List<X509Certificate> trustedChain = this.mDelegate.checkServerTrusted(certs, authType, host);
        checkPins(trustedChain);
        return trustedChain;
    }

    private void checkPins(List<X509Certificate> chain) throws CertificateException {
        PinSet pinSet = this.mNetworkSecurityConfig.getPins();
        if (pinSet.pins.isEmpty() || System.currentTimeMillis() > pinSet.expirationTime || !isPinningEnforced(chain)) {
            return;
        }
        Set<String> pinAlgorithms = pinSet.getPinAlgorithms();
        Map<String, MessageDigest> digestMap = new ArrayMap<>(pinAlgorithms.size());
        for (int i = chain.size() - 1; i >= 0; i--) {
            X509Certificate cert = chain.get(i);
            byte[] encodedSPKI = cert.getPublicKey().getEncoded();
            for (String algorithm : pinAlgorithms) {
                MessageDigest md = digestMap.get(algorithm);
                if (md == null) {
                    try {
                        md = MessageDigest.getInstance(algorithm);
                        digestMap.put(algorithm, md);
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (pinSet.pins.contains(new Pin(algorithm, md.digest(encodedSPKI)))) {
                    return;
                }
            }
        }
        throw new CertificateException("Pin verification failed");
    }

    private boolean isPinningEnforced(List<X509Certificate> chain) throws CertificateException {
        if (chain.isEmpty()) {
            return false;
        }
        X509Certificate anchorCert = chain.get(chain.size() - 1);
        TrustAnchor chainAnchor = this.mNetworkSecurityConfig.findTrustAnchorBySubjectAndPublicKey(anchorCert);
        if (chainAnchor == null) {
            throw new CertificateException("Trusted chain does not end in a TrustAnchor");
        }
        return !chainAnchor.overridesPins;
    }

    @Override // javax.net.ssl.X509TrustManager
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] x509CertificateArr;
        synchronized (this.mIssuersLock) {
            if (this.mIssuers == null) {
                Set<TrustAnchor> anchors = this.mNetworkSecurityConfig.getTrustAnchors();
                X509Certificate[] issuers = new X509Certificate[anchors.size()];
                int i = 0;
                for (TrustAnchor anchor : anchors) {
                    issuers[i] = anchor.certificate;
                    i++;
                }
                this.mIssuers = issuers;
            }
            x509CertificateArr = (X509Certificate[]) this.mIssuers.clone();
        }
        return x509CertificateArr;
    }

    public void handleTrustStorageUpdate() {
        synchronized (this.mIssuersLock) {
            this.mIssuers = null;
            this.mDelegate.handleTrustStorageUpdate();
        }
    }
}
