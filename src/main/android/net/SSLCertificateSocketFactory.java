package android.net;

import android.p008os.SystemProperties;
import android.util.Log;
import com.android.internal.p028os.RoSystemProperties;
import com.android.org.conscrypt.OpenSSLSocketImpl;
import com.android.org.conscrypt.SSLClientSessionCache;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
@Deprecated
/* loaded from: classes2.dex */
public class SSLCertificateSocketFactory extends SSLSocketFactory {
    private static final TrustManager[] INSECURE_TRUST_MANAGER = {new X509TrustManager() { // from class: android.net.SSLCertificateSocketFactory.1
        @Override // javax.net.ssl.X509TrustManager
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }};
    private static final String TAG = "SSLCertificateSocketFactory";
    private byte[] mAlpnProtocols;
    private PrivateKey mChannelIdPrivateKey;
    private final int mHandshakeTimeoutMillis;
    private SSLSocketFactory mInsecureFactory;
    private KeyManager[] mKeyManagers;
    private byte[] mNpnProtocols;
    private final boolean mSecure;
    private SSLSocketFactory mSecureFactory;
    private final SSLClientSessionCache mSessionCache;
    private TrustManager[] mTrustManagers;

    @Deprecated
    public SSLCertificateSocketFactory(int handshakeTimeoutMillis) {
        this(handshakeTimeoutMillis, null, true);
    }

    private SSLCertificateSocketFactory(int handshakeTimeoutMillis, SSLSessionCache cache, boolean secure) {
        this.mInsecureFactory = null;
        this.mSecureFactory = null;
        this.mTrustManagers = null;
        this.mKeyManagers = null;
        this.mNpnProtocols = null;
        this.mAlpnProtocols = null;
        this.mChannelIdPrivateKey = null;
        this.mHandshakeTimeoutMillis = handshakeTimeoutMillis;
        this.mSessionCache = cache != null ? cache.mSessionCache : null;
        this.mSecure = secure;
    }

    public static SocketFactory getDefault(int handshakeTimeoutMillis) {
        return new SSLCertificateSocketFactory(handshakeTimeoutMillis, null, true);
    }

    public static SSLSocketFactory getDefault(int handshakeTimeoutMillis, SSLSessionCache cache) {
        return new SSLCertificateSocketFactory(handshakeTimeoutMillis, cache, true);
    }

    public static SSLSocketFactory getInsecure(int handshakeTimeoutMillis, SSLSessionCache cache) {
        return new SSLCertificateSocketFactory(handshakeTimeoutMillis, cache, false);
    }

    @Deprecated
    public static org.apache.http.conn.ssl.SSLSocketFactory getHttpSocketFactory(int handshakeTimeoutMillis, SSLSessionCache cache) {
        return new org.apache.http.conn.ssl.SSLSocketFactory(new SSLCertificateSocketFactory(handshakeTimeoutMillis, cache, true));
    }

    public static void verifyHostname(Socket socket, String hostname) throws IOException {
        if (!(socket instanceof SSLSocket)) {
            throw new IllegalArgumentException("Attempt to verify non-SSL socket");
        }
        if (!isSslCheckRelaxed()) {
            SSLSocket ssl = (SSLSocket) socket;
            ssl.startHandshake();
            SSLSession session = ssl.getSession();
            if (session == null) {
                throw new SSLException("Cannot verify SSL socket without session");
            }
            if (!HttpsURLConnection.getDefaultHostnameVerifier().verify(hostname, session)) {
                throw new SSLPeerUnverifiedException("Cannot verify hostname: " + hostname);
            }
        }
    }

    private SSLSocketFactory makeSocketFactory(KeyManager[] keyManagers, TrustManager[] trustManagers) {
        try {
            SSLContext sslContext = SSLContext.getInstance(org.apache.http.conn.ssl.SSLSocketFactory.TLS, "AndroidOpenSSL");
            sslContext.init(keyManagers, trustManagers, null);
            sslContext.getClientSessionContext().setPersistentCache(this.mSessionCache);
            return sslContext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.wtf(TAG, e);
            return (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
    }

    private static boolean isSslCheckRelaxed() {
        return RoSystemProperties.DEBUGGABLE && SystemProperties.getBoolean("socket.relaxsslcheck", false);
    }

    private synchronized SSLSocketFactory getDelegate() {
        if (this.mSecure && !isSslCheckRelaxed()) {
            if (this.mSecureFactory == null) {
                this.mSecureFactory = makeSocketFactory(this.mKeyManagers, this.mTrustManagers);
            }
            return this.mSecureFactory;
        }
        if (this.mInsecureFactory == null) {
            if (this.mSecure) {
                Log.m104w(TAG, "*** BYPASSING SSL SECURITY CHECKS (socket.relaxsslcheck=yes) ***");
            } else {
                Log.m104w(TAG, "Bypassing SSL security checks at caller's request");
            }
            this.mInsecureFactory = makeSocketFactory(this.mKeyManagers, INSECURE_TRUST_MANAGER);
        }
        return this.mInsecureFactory;
    }

    public void setTrustManagers(TrustManager[] trustManager) {
        this.mTrustManagers = trustManager;
        this.mSecureFactory = null;
    }

    public void setNpnProtocols(byte[][] npnProtocols) {
        this.mNpnProtocols = toLengthPrefixedList(npnProtocols);
    }

    public void setAlpnProtocols(byte[][] protocols) {
        this.mAlpnProtocols = toLengthPrefixedList(protocols);
    }

    public static byte[] toLengthPrefixedList(byte[]... items) {
        if (items.length == 0) {
            throw new IllegalArgumentException("items.length == 0");
        }
        int totalLength = 0;
        for (byte[] s : items) {
            if (s.length == 0 || s.length > 255) {
                throw new IllegalArgumentException("s.length == 0 || s.length > 255: " + s.length);
            }
            totalLength += s.length + 1;
        }
        byte[] result = new byte[totalLength];
        int pos = 0;
        int length = items.length;
        int i = 0;
        while (i < length) {
            byte[] s2 = items[i];
            int pos2 = pos + 1;
            result[pos] = (byte) s2.length;
            int length2 = s2.length;
            int i2 = 0;
            while (i2 < length2) {
                byte b = s2[i2];
                result[pos2] = b;
                i2++;
                pos2++;
            }
            i++;
            pos = pos2;
        }
        return result;
    }

    public byte[] getNpnSelectedProtocol(Socket socket) {
        return castToOpenSSLSocket(socket).getNpnSelectedProtocol();
    }

    public byte[] getAlpnSelectedProtocol(Socket socket) {
        return castToOpenSSLSocket(socket).getAlpnSelectedProtocol();
    }

    public void setKeyManagers(KeyManager[] keyManagers) {
        this.mKeyManagers = keyManagers;
        this.mSecureFactory = null;
        this.mInsecureFactory = null;
    }

    public void setChannelIdPrivateKey(PrivateKey privateKey) {
        this.mChannelIdPrivateKey = privateKey;
    }

    public void setUseSessionTickets(Socket socket, boolean useSessionTickets) {
        castToOpenSSLSocket(socket).setUseSessionTickets(useSessionTickets);
    }

    public void setHostname(Socket socket, String hostName) {
        castToOpenSSLSocket(socket).setHostname(hostName);
    }

    public void setSoWriteTimeout(Socket socket, int writeTimeoutMilliseconds) throws SocketException {
        castToOpenSSLSocket(socket).setSoWriteTimeout(writeTimeoutMilliseconds);
    }

    private static OpenSSLSocketImpl castToOpenSSLSocket(Socket socket) {
        if (!(socket instanceof OpenSSLSocketImpl)) {
            throw new IllegalArgumentException("Socket not created by this factory: " + socket);
        }
        return (OpenSSLSocketImpl) socket;
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public Socket createSocket(Socket k, String host, int port, boolean close) throws IOException {
        OpenSSLSocketImpl s = getDelegate().createSocket(k, host, port, close);
        s.setNpnProtocols(this.mNpnProtocols);
        s.setAlpnProtocols(this.mAlpnProtocols);
        s.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
        s.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
        if (this.mSecure) {
            verifyHostname(s, host);
        }
        return s;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket() throws IOException {
        OpenSSLSocketImpl s = getDelegate().createSocket();
        s.setNpnProtocols(this.mNpnProtocols);
        s.setAlpnProtocols(this.mAlpnProtocols);
        s.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
        s.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
        return s;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress addr, int port, InetAddress localAddr, int localPort) throws IOException {
        OpenSSLSocketImpl s = getDelegate().createSocket(addr, port, localAddr, localPort);
        s.setNpnProtocols(this.mNpnProtocols);
        s.setAlpnProtocols(this.mAlpnProtocols);
        s.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
        s.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
        return s;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress addr, int port) throws IOException {
        OpenSSLSocketImpl s = getDelegate().createSocket(addr, port);
        s.setNpnProtocols(this.mNpnProtocols);
        s.setAlpnProtocols(this.mAlpnProtocols);
        s.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
        s.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
        return s;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port, InetAddress localAddr, int localPort) throws IOException {
        OpenSSLSocketImpl s = getDelegate().createSocket(host, port, localAddr, localPort);
        s.setNpnProtocols(this.mNpnProtocols);
        s.setAlpnProtocols(this.mAlpnProtocols);
        s.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
        s.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
        if (this.mSecure) {
            verifyHostname(s, host);
        }
        return s;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port) throws IOException {
        OpenSSLSocketImpl s = getDelegate().createSocket(host, port);
        s.setNpnProtocols(this.mNpnProtocols);
        s.setAlpnProtocols(this.mAlpnProtocols);
        s.setHandshakeTimeout(this.mHandshakeTimeoutMillis);
        s.setChannelIdPrivateKey(this.mChannelIdPrivateKey);
        if (this.mSecure) {
            verifyHostname(s, host);
        }
        return s;
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public String[] getDefaultCipherSuites() {
        return getDelegate().getDefaultCipherSuites();
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public String[] getSupportedCipherSuites() {
        return getDelegate().getSupportedCipherSuites();
    }
}
