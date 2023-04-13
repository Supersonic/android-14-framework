package android.security.net.config;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509ExtendedTrustManager;
/* loaded from: classes3.dex */
public class RootTrustManager extends X509ExtendedTrustManager {
    private final ApplicationConfig mConfig;

    public RootTrustManager(ApplicationConfig config) {
        if (config == null) {
            throw new NullPointerException("config must not be null");
        }
        this.mConfig = config;
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        NetworkSecurityConfig config = this.mConfig.getConfigForHostname("");
        config.getTrustManager().checkClientTrusted(chain, authType);
    }

    @Override // javax.net.ssl.X509ExtendedTrustManager
    public void checkClientTrusted(X509Certificate[] certs, String authType, Socket socket) throws CertificateException {
        NetworkSecurityConfig config = this.mConfig.getConfigForHostname("");
        config.getTrustManager().checkClientTrusted(certs, authType, socket);
    }

    @Override // javax.net.ssl.X509ExtendedTrustManager
    public void checkClientTrusted(X509Certificate[] certs, String authType, SSLEngine engine) throws CertificateException {
        NetworkSecurityConfig config = this.mConfig.getConfigForHostname("");
        config.getTrustManager().checkClientTrusted(certs, authType, engine);
    }

    @Override // javax.net.ssl.X509ExtendedTrustManager
    public void checkServerTrusted(X509Certificate[] certs, String authType, Socket socket) throws CertificateException {
        if (socket instanceof SSLSocket) {
            SSLSocket sslSocket = (SSLSocket) socket;
            SSLSession session = sslSocket.getHandshakeSession();
            if (session == null) {
                throw new CertificateException("Not in handshake; no session available");
            }
            String host = session.getPeerHost();
            NetworkSecurityConfig config = this.mConfig.getConfigForHostname(host);
            config.getTrustManager().checkServerTrusted(certs, authType, socket);
            return;
        }
        checkServerTrusted(certs, authType);
    }

    @Override // javax.net.ssl.X509ExtendedTrustManager
    public void checkServerTrusted(X509Certificate[] certs, String authType, SSLEngine engine) throws CertificateException {
        SSLSession session = engine.getHandshakeSession();
        if (session == null) {
            throw new CertificateException("Not in handshake; no session available");
        }
        String host = session.getPeerHost();
        NetworkSecurityConfig config = this.mConfig.getConfigForHostname(host);
        config.getTrustManager().checkServerTrusted(certs, authType, engine);
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        if (this.mConfig.hasPerDomainConfigs()) {
            throw new CertificateException("Domain specific configurations require that hostname aware checkServerTrusted(X509Certificate[], String, String) is used");
        }
        NetworkSecurityConfig config = this.mConfig.getConfigForHostname("");
        config.getTrustManager().checkServerTrusted(certs, authType);
    }

    public List<X509Certificate> checkServerTrusted(X509Certificate[] certs, String authType, String hostname) throws CertificateException {
        if (hostname == null && this.mConfig.hasPerDomainConfigs()) {
            throw new CertificateException("Domain specific configurations require that the hostname be provided");
        }
        NetworkSecurityConfig config = this.mConfig.getConfigForHostname(hostname);
        return config.getTrustManager().checkServerTrusted(certs, authType, hostname);
    }

    @Override // javax.net.ssl.X509TrustManager
    public X509Certificate[] getAcceptedIssuers() {
        NetworkSecurityConfig config = this.mConfig.getConfigForHostname("");
        return config.getTrustManager().getAcceptedIssuers();
    }

    public boolean isSameTrustConfiguration(String hostname1, String hostname2) {
        return this.mConfig.getConfigForHostname(hostname1).equals(this.mConfig.getConfigForHostname(hostname2));
    }
}
