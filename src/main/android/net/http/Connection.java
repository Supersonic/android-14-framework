package android.net.http;

import android.content.Context;
import android.os.SystemClock;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
/* loaded from: classes.dex */
public abstract class Connection {
    private static final int DONE = 3;
    private static final int DRAIN = 2;
    private static final String HTTP_CONNECTION = "http.connection";
    private static final int MAX_PIPE = 3;
    private static final int MIN_PIPE = 2;
    private static final int READ = 1;
    private static final int RETRY_REQUEST_LIMIT = 2;
    private static final int SEND = 0;
    static final int SOCKET_TIMEOUT = 60000;
    private byte[] mBuf;
    Context mContext;
    HttpHost mHost;
    RequestFeeder mRequestFeeder;
    private static final String[] states = {"SEND", "READ", "DRAIN", "DONE"};
    private static int STATE_NORMAL = 0;
    private static int STATE_CANCEL_REQUESTED = 1;
    AndroidHttpClientConnection mHttpClientConnection = null;
    SslCertificate mCertificate = null;
    private int mActive = STATE_NORMAL;
    private boolean mCanPersist = false;
    private HttpContext mHttpContext = new BasicHttpContext(null);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void closeConnection();

    abstract String getScheme();

    abstract AndroidHttpClientConnection openConnection(Request request) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Connection(Context context, HttpHost host, RequestFeeder requestFeeder) {
        this.mContext = context;
        this.mHost = host;
        this.mRequestFeeder = requestFeeder;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpHost getHost() {
        return this.mHost;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Connection getConnection(Context context, HttpHost host, HttpHost proxy, RequestFeeder requestFeeder) {
        if (host.getSchemeName().equals(HttpHost.DEFAULT_SCHEME_NAME)) {
            return new HttpConnection(context, host, requestFeeder);
        }
        return new HttpsConnection(context, host, proxy, requestFeeder);
    }

    SslCertificate getCertificate() {
        return this.mCertificate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancel() {
        this.mActive = STATE_CANCEL_REQUESTED;
        closeConnection();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0044, code lost:
        if (r10 == false) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0047, code lost:
        r7 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0048, code lost:
        r6 = r7;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void processRequests(Request firstRequest) {
        Request req;
        int error = 0;
        Exception exception = null;
        LinkedList<Request> pipe = new LinkedList<>();
        int minPipe = 2;
        int maxPipe = 3;
        int state = 0;
        while (true) {
            int i = 3;
            if (state != 3) {
                if (this.mActive == STATE_CANCEL_REQUESTED) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                    }
                    this.mActive = STATE_NORMAL;
                }
                switch (state) {
                    case 0:
                        if (pipe.size() == maxPipe) {
                            state = 1;
                            break;
                        } else {
                            if (firstRequest == null) {
                                req = this.mRequestFeeder.getRequest(this.mHost);
                            } else {
                                req = firstRequest;
                                firstRequest = null;
                            }
                            if (req == null) {
                                state = 2;
                                break;
                            } else {
                                req.setConnection(this);
                                if (req.mCancelled) {
                                    req.complete();
                                    break;
                                } else {
                                    AndroidHttpClientConnection androidHttpClientConnection = this.mHttpClientConnection;
                                    if ((androidHttpClientConnection == null || !androidHttpClientConnection.isOpen()) && !openHttpConnection(req)) {
                                        state = 3;
                                        break;
                                    } else {
                                        req.mEventHandler.certificate(this.mCertificate);
                                        try {
                                            req.sendRequest(this.mHttpClientConnection);
                                        } catch (IOException e2) {
                                            exception = e2;
                                            error = -7;
                                        } catch (IllegalStateException e3) {
                                            exception = e3;
                                            error = -7;
                                        } catch (HttpException e4) {
                                            exception = e4;
                                            error = -1;
                                        }
                                        if (exception != null) {
                                            if (httpFailure(req, error, exception) && !req.mCancelled) {
                                                pipe.addLast(req);
                                            }
                                            exception = null;
                                            if (!clearPipe(pipe)) {
                                                i = 0;
                                            }
                                            state = i;
                                            maxPipe = 1;
                                            minPipe = 1;
                                            break;
                                        } else {
                                            pipe.addLast(req);
                                            if (!this.mCanPersist) {
                                                state = 1;
                                                break;
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    case 1:
                    case 2:
                        boolean empty = !this.mRequestFeeder.haveRequest(this.mHost);
                        int pipeSize = pipe.size();
                        if (state != 2 && pipeSize < minPipe && !empty && this.mCanPersist) {
                            state = 0;
                            break;
                        } else {
                            Request req2 = pipe.removeFirst();
                            Request req3 = req2;
                            try {
                                req3.readResponse(this.mHttpClientConnection);
                            } catch (IOException e5) {
                                exception = e5;
                                error = -7;
                            } catch (IllegalStateException e6) {
                                exception = e6;
                                error = -7;
                            } catch (ParseException e7) {
                                exception = e7;
                                error = -7;
                            }
                            if (exception != null) {
                                if (httpFailure(req3, error, exception) && !req3.mCancelled) {
                                    req3.reset();
                                    pipe.addFirst(req3);
                                }
                                exception = null;
                                this.mCanPersist = false;
                            }
                            if (!this.mCanPersist) {
                                closeConnection();
                                this.mHttpContext.removeAttribute("http.connection");
                                clearPipe(pipe);
                                maxPipe = 1;
                                minPipe = 1;
                                state = 0;
                                break;
                            } else {
                                break;
                            }
                        }
                }
            } else {
                return;
            }
        }
    }

    private boolean clearPipe(LinkedList<Request> pipe) {
        boolean empty = true;
        synchronized (this.mRequestFeeder) {
            while (!pipe.isEmpty()) {
                Request tReq = pipe.removeLast();
                this.mRequestFeeder.requeueRequest(tReq);
                empty = false;
            }
            if (empty) {
                empty = !this.mRequestFeeder.haveRequest(this.mHost);
            }
        }
        return empty;
    }

    private boolean openHttpConnection(Request req) {
        AndroidHttpClientConnection openConnection;
        SystemClock.uptimeMillis();
        int error = 0;
        Exception exception = null;
        try {
            this.mCertificate = null;
            openConnection = openConnection(req);
            this.mHttpClientConnection = openConnection;
        } catch (SSLConnectionClosedByUserException e) {
            req.mFailCount = 2;
            return false;
        } catch (IllegalArgumentException e2) {
            error = -6;
            req.mFailCount = 2;
            exception = e2;
        } catch (UnknownHostException e3) {
            error = -2;
            exception = e3;
        } catch (SSLHandshakeException e4) {
            req.mFailCount = 2;
            error = -11;
            exception = e4;
        } catch (IOException e5) {
            error = -6;
            exception = e5;
        }
        if (openConnection != null) {
            openConnection.setSocketTimeout(SOCKET_TIMEOUT);
            this.mHttpContext.setAttribute("http.connection", this.mHttpClientConnection);
            if (error == 0) {
                return true;
            }
            if (req.mFailCount < 2) {
                this.mRequestFeeder.requeueRequest(req);
                req.mFailCount++;
            } else {
                httpFailure(req, error, exception);
            }
            return error == 0;
        }
        req.mFailCount = 2;
        return false;
    }

    private boolean httpFailure(Request req, int errorId, Exception e) {
        String error;
        boolean ret = true;
        int i = req.mFailCount + 1;
        req.mFailCount = i;
        if (i >= 2) {
            ret = false;
            if (errorId < 0) {
                error = getEventHandlerErrorString(errorId);
            } else {
                Throwable cause = e.getCause();
                error = cause != null ? cause.toString() : e.getMessage();
            }
            req.mEventHandler.error(errorId, error);
            req.complete();
        }
        closeConnection();
        this.mHttpContext.removeAttribute("http.connection");
        return ret;
    }

    private static String getEventHandlerErrorString(int errorId) {
        switch (errorId) {
            case EventHandler.TOO_MANY_REQUESTS_ERROR /* -15 */:
                return "TOO_MANY_REQUESTS_ERROR";
            case EventHandler.FILE_NOT_FOUND_ERROR /* -14 */:
                return "FILE_NOT_FOUND_ERROR";
            case EventHandler.FILE_ERROR /* -13 */:
                return "FILE_ERROR";
            case EventHandler.ERROR_BAD_URL /* -12 */:
                return "ERROR_BAD_URL";
            case EventHandler.ERROR_FAILED_SSL_HANDSHAKE /* -11 */:
                return "ERROR_FAILED_SSL_HANDSHAKE";
            case EventHandler.ERROR_UNSUPPORTED_SCHEME /* -10 */:
                return "ERROR_UNSUPPORTED_SCHEME";
            case EventHandler.ERROR_REDIRECT_LOOP /* -9 */:
                return "ERROR_REDIRECT_LOOP";
            case EventHandler.ERROR_TIMEOUT /* -8 */:
                return "ERROR_TIMEOUT";
            case EventHandler.ERROR_IO /* -7 */:
                return "ERROR_IO";
            case EventHandler.ERROR_CONNECT /* -6 */:
                return "ERROR_CONNECT";
            case EventHandler.ERROR_PROXYAUTH /* -5 */:
                return "ERROR_PROXYAUTH";
            case EventHandler.ERROR_AUTH /* -4 */:
                return "ERROR_AUTH";
            case EventHandler.ERROR_UNSUPPORTED_AUTH_SCHEME /* -3 */:
                return "ERROR_UNSUPPORTED_AUTH_SCHEME";
            case -2:
                return "ERROR_LOOKUP";
            case -1:
                return "ERROR";
            case 0:
                return "OK";
            default:
                return "UNKNOWN_ERROR";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpContext getHttpContext() {
        return this.mHttpContext;
    }

    private boolean keepAlive(HttpEntity entity, ProtocolVersion ver, int connType, HttpContext context) {
        org.apache.http.HttpConnection conn = (org.apache.http.HttpConnection) context.getAttribute("http.connection");
        if (conn != null && !conn.isOpen()) {
            return false;
        }
        if ((entity != null && entity.getContentLength() < 0 && (!entity.isChunked() || ver.lessEquals(HttpVersion.HTTP_1_0))) || connType == 1) {
            return false;
        }
        if (connType == 2) {
            return true;
        }
        return !ver.lessEquals(HttpVersion.HTTP_1_0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCanPersist(HttpEntity entity, ProtocolVersion ver, int connType) {
        this.mCanPersist = keepAlive(entity, ver, connType, this.mHttpContext);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCanPersist(boolean canPersist) {
        this.mCanPersist = canPersist;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getCanPersist() {
        return this.mCanPersist;
    }

    public synchronized String toString() {
        return this.mHost.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getBuf() {
        if (this.mBuf == null) {
            this.mBuf = new byte[8192];
        }
        return this.mBuf;
    }
}
