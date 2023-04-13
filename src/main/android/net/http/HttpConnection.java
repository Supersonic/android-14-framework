package android.net.http;

import android.content.Context;
import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.params.BasicHttpParams;
/* loaded from: classes.dex */
class HttpConnection extends Connection {
    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpConnection(Context context, HttpHost host, RequestFeeder requestFeeder) {
        super(context, host, requestFeeder);
    }

    @Override // android.net.http.Connection
    AndroidHttpClientConnection openConnection(Request req) throws IOException {
        EventHandler eventHandler = req.getEventHandler();
        this.mCertificate = null;
        eventHandler.certificate(this.mCertificate);
        AndroidHttpClientConnection conn = new AndroidHttpClientConnection();
        BasicHttpParams params = new BasicHttpParams();
        Socket sock = new Socket(this.mHost.getHostName(), this.mHost.getPort());
        params.setIntParameter("http.socket.buffer-size", 8192);
        conn.bind(sock, params);
        return conn;
    }

    @Override // android.net.http.Connection
    void closeConnection() {
        try {
            if (this.mHttpClientConnection != null && this.mHttpClientConnection.isOpen()) {
                this.mHttpClientConnection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void restartConnection(boolean abort) {
    }

    @Override // android.net.http.Connection
    String getScheme() {
        return HttpHost.DEFAULT_SCHEME_NAME;
    }
}
