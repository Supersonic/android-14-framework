package android.net;

import android.util.Log;
import java.io.IOException;
import java.net.InetSocketAddress;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
/* loaded from: classes2.dex */
public class PrivateDnsConnectivityChecker {
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int PRIVATE_DNS_PORT = 853;
    private static final String TAG = "NetworkUtils";

    private PrivateDnsConnectivityChecker() {
    }

    public static boolean canConnectToPrivateDnsServer(String hostname) {
        SocketFactory factory = SSLSocketFactory.getDefault();
        TrafficStats.setThreadStatsTagApp();
        try {
            SSLSocket socket = (SSLSocket) factory.createSocket();
            socket.setSoTimeout(5000);
            socket.connect(new InetSocketAddress(hostname, 853));
            if (!socket.isConnected()) {
                Log.m104w(TAG, String.format("Connection to %s failed.", hostname));
                if (socket != null) {
                    socket.close();
                }
                return false;
            }
            socket.startHandshake();
            Log.m104w(TAG, String.format("TLS handshake to %s succeeded.", hostname));
            if (socket != null) {
                socket.close();
                return true;
            }
            return true;
        } catch (IOException e) {
            Log.m103w(TAG, String.format("TLS handshake to %s failed.", hostname), e);
            return false;
        }
    }
}
