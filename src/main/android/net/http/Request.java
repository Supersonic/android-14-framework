package android.net.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.RequestContent;
/* loaded from: classes.dex */
public class Request {
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String CONTENT_LENGTH_HEADER = "content-length";
    private static final String HOST_HEADER = "Host";
    private static RequestContent requestContentProcessor = new RequestContent();
    private int mBodyLength;
    private InputStream mBodyProvider;
    private Connection mConnection;
    EventHandler mEventHandler;
    HttpHost mHost;
    BasicHttpRequest mHttpRequest;
    String mPath;
    HttpHost mProxyHost;
    volatile boolean mCancelled = false;
    int mFailCount = 0;
    private int mReceivedBytes = 0;
    private final Object mClientResource = new Object();
    private boolean mLoadingPaused = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Request(String method, HttpHost host, HttpHost proxyHost, String path, InputStream bodyProvider, int bodyLength, EventHandler eventHandler, Map<String, String> headers) {
        this.mEventHandler = eventHandler;
        this.mHost = host;
        this.mProxyHost = proxyHost;
        this.mPath = path;
        this.mBodyProvider = bodyProvider;
        this.mBodyLength = bodyLength;
        if (bodyProvider == null && !HttpPost.METHOD_NAME.equalsIgnoreCase(method)) {
            this.mHttpRequest = new BasicHttpRequest(method, getUri());
        } else {
            this.mHttpRequest = new BasicHttpEntityEnclosingRequest(method, getUri());
            if (bodyProvider != null) {
                setBodyProvider(bodyProvider, bodyLength);
            }
        }
        addHeader("Host", getHostPort());
        addHeader(ACCEPT_ENCODING_HEADER, "gzip");
        addHeaders(headers);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void setLoadingPaused(boolean pause) {
        this.mLoadingPaused = pause;
        if (!pause) {
            notify();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setConnection(Connection connection) {
        this.mConnection = connection;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EventHandler getEventHandler() {
        return this.mEventHandler;
    }

    void addHeader(String name, String value) {
        if (name == null) {
            HttpLog.m3e("Null http header name");
            throw new NullPointerException("Null http header name");
        } else if (value == null || value.length() == 0) {
            String damage = "Null or empty value for header \"" + name + "\"";
            HttpLog.m3e(damage);
            throw new RuntimeException(damage);
        } else {
            this.mHttpRequest.addHeader(name, value);
        }
    }

    void addHeaders(Map<String, String> headers) {
        if (headers == null) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void sendRequest(AndroidHttpClientConnection httpClientConnection) throws HttpException, IOException {
        if (this.mCancelled) {
            return;
        }
        requestContentProcessor.process(this.mHttpRequest, this.mConnection.getHttpContext());
        httpClientConnection.sendRequestHeader(this.mHttpRequest);
        BasicHttpRequest basicHttpRequest = this.mHttpRequest;
        if (basicHttpRequest instanceof HttpEntityEnclosingRequest) {
            httpClientConnection.sendRequestEntity((HttpEntityEnclosingRequest) basicHttpRequest);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't wrap try/catch for region: R(4:(3:29|30|32)|33|26|27) */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x015a, code lost:
        if (r13 == null) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x015c, code lost:
        r13.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00fc, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0138, code lost:
        if (r13 != null) goto L50;
     */
    /* JADX WARN: Removed duplicated region for block: B:101:0x0156  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x009f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:125:0x011c A[EDGE_INSN: B:125:0x011c->B:75:0x011c ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0120  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x014d A[Catch: all -> 0x0153, TRY_LEAVE, TryCatch #11 {all -> 0x0153, blocks: (B:91:0x013f, B:93:0x0145, B:97:0x014d), top: B:111:0x0061 }] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:110:? -> B:67:0x010b). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void readResponse(AndroidHttpClientConnection httpClientConnection) throws IOException, ParseException {
        StatusLine statusLine;
        int statusCode;
        HttpEntity entity;
        int len;
        ProtocolVersion v;
        AndroidHttpClientConnection androidHttpClientConnection = httpClientConnection;
        if (this.mCancelled) {
            return;
        }
        httpClientConnection.flush();
        Headers header = new Headers();
        while (true) {
            statusLine = androidHttpClientConnection.parseResponseHeader(header);
            statusCode = statusLine.getStatusCode();
            if (statusCode >= 200) {
                break;
            }
            androidHttpClientConnection = httpClientConnection;
        }
        ProtocolVersion v2 = statusLine.getProtocolVersion();
        this.mEventHandler.status(v2.getMajor(), v2.getMinor(), statusCode, statusLine.getReasonPhrase());
        this.mEventHandler.headers(header);
        boolean hasBody = canResponseHaveBody(this.mHttpRequest, statusCode);
        if (hasBody) {
            HttpEntity entity2 = androidHttpClientConnection.receiveResponseEntity(header);
            entity = entity2;
        } else {
            entity = null;
        }
        boolean supportPartialContent = "bytes".equalsIgnoreCase(header.getAcceptRanges());
        if (entity != null) {
            InputStream is = entity.getContent();
            Header contentEncoding = entity.getContentEncoding();
            InputStream nis = null;
            byte[] buf = null;
            int count = 0;
            try {
                try {
                    if (contentEncoding != null) {
                        try {
                            if (contentEncoding.getValue().equals("gzip")) {
                                nis = new GZIPInputStream(is);
                                buf = this.mConnection.getBuf();
                                int lowWater = buf.length / 2;
                                int count2 = 0;
                                len = 0;
                                while (true) {
                                    boolean hasBody2 = hasBody;
                                    if (len != -1) {
                                        break;
                                    }
                                    try {
                                        synchronized (this) {
                                            while (this.mLoadingPaused) {
                                                try {
                                                    wait();
                                                } catch (InterruptedException e) {
                                                    ProtocolVersion v3 = v2;
                                                    HttpLog.m3e("Interrupted exception whilst network thread paused at WebCore's request. " + e.getMessage());
                                                    v2 = v3;
                                                } catch (Throwable th) {
                                                    th = th;
                                                    throw th;
                                                }
                                            }
                                            v = v2;
                                            try {
                                            } catch (Throwable th2) {
                                                th = th2;
                                                throw th;
                                            }
                                        }
                                        try {
                                            len = nis.read(buf, count2, buf.length - count2);
                                            if (len != -1) {
                                                count2 += len;
                                                if (supportPartialContent) {
                                                    this.mReceivedBytes += len;
                                                }
                                            }
                                            if (len != -1 && count2 < lowWater) {
                                                hasBody = hasBody2;
                                                v2 = v;
                                            }
                                            this.mEventHandler.data(buf, count2);
                                            count2 = 0;
                                            hasBody = hasBody2;
                                            v2 = v;
                                        } catch (EOFException e2) {
                                            count = count2;
                                            if (count > 0) {
                                                this.mEventHandler.data(buf, count);
                                            }
                                        } catch (IOException e3) {
                                            e = e3;
                                            count = count2;
                                            if (statusCode != 200 || statusCode == 206) {
                                                if (supportPartialContent && count > 0) {
                                                    this.mEventHandler.data(buf, count);
                                                }
                                                throw e;
                                            }
                                        } catch (Throwable th3) {
                                            e = th3;
                                            if (nis != null) {
                                                nis.close();
                                            }
                                            throw e;
                                        }
                                    } catch (EOFException e4) {
                                        count = count2;
                                    } catch (IOException e5) {
                                        e = e5;
                                        count = count2;
                                    } catch (Throwable th4) {
                                        e = th4;
                                    }
                                }
                                if (nis != null) {
                                    nis.close();
                                }
                            }
                        } catch (EOFException e6) {
                            if (count > 0) {
                            }
                        } catch (IOException e7) {
                            e = e7;
                            if (statusCode != 200) {
                            }
                            if (supportPartialContent) {
                                this.mEventHandler.data(buf, count);
                            }
                            throw e;
                        } catch (Throwable th5) {
                            e = th5;
                            if (nis != null) {
                            }
                            throw e;
                        }
                    }
                    buf = this.mConnection.getBuf();
                    int lowWater2 = buf.length / 2;
                    int count22 = 0;
                    len = 0;
                    while (true) {
                        boolean hasBody22 = hasBody;
                        if (len != -1) {
                        }
                    }
                    if (nis != null) {
                    }
                } catch (EOFException e8) {
                } catch (IOException e9) {
                    e = e9;
                } catch (Throwable th6) {
                    e = th6;
                }
                nis = is;
            } catch (Throwable th7) {
                e = th7;
            }
        }
        this.mConnection.setCanPersist(entity, statusLine.getProtocolVersion(), header.getConnectionType());
        this.mEventHandler.endData();
        complete();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void cancel() {
        this.mLoadingPaused = false;
        notify();
        this.mCancelled = true;
        Connection connection = this.mConnection;
        if (connection != null) {
            connection.cancel();
        }
    }

    String getHostPort() {
        String myScheme = this.mHost.getSchemeName();
        int myPort = this.mHost.getPort();
        if ((myPort != 80 && myScheme.equals(HttpHost.DEFAULT_SCHEME_NAME)) || (myPort != 443 && myScheme.equals("https"))) {
            return this.mHost.toHostString();
        }
        return this.mHost.getHostName();
    }

    String getUri() {
        if (this.mProxyHost == null || this.mHost.getSchemeName().equals("https")) {
            return this.mPath;
        }
        return this.mHost.getSchemeName() + "://" + getHostPort() + this.mPath;
    }

    public String toString() {
        return this.mPath;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reset() {
        this.mHttpRequest.removeHeaders("content-length");
        InputStream inputStream = this.mBodyProvider;
        if (inputStream != null) {
            try {
                inputStream.reset();
            } catch (IOException e) {
            }
            setBodyProvider(this.mBodyProvider, this.mBodyLength);
        }
        if (this.mReceivedBytes > 0) {
            this.mFailCount = 0;
            HttpLog.m2v("*** Request.reset() to range:" + this.mReceivedBytes);
            this.mHttpRequest.setHeader("Range", "bytes=" + this.mReceivedBytes + "-");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void waitUntilComplete() {
        synchronized (this.mClientResource) {
            try {
                this.mClientResource.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void complete() {
        synchronized (this.mClientResource) {
            this.mClientResource.notifyAll();
        }
    }

    private static boolean canResponseHaveBody(HttpRequest request, int status) {
        return (HttpHead.METHOD_NAME.equalsIgnoreCase(request.getRequestLine().getMethod()) || status < 200 || status == 204 || status == 304) ? false : true;
    }

    private void setBodyProvider(InputStream bodyProvider, int bodyLength) {
        if (!bodyProvider.markSupported()) {
            throw new IllegalArgumentException("bodyProvider must support mark()");
        }
        bodyProvider.mark(Integer.MAX_VALUE);
        ((BasicHttpEntityEnclosingRequest) this.mHttpRequest).setEntity(new InputStreamEntity(bodyProvider, bodyLength));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void handleSslErrorResponse(boolean proceed) {
        HttpsConnection connection = (HttpsConnection) this.mConnection;
        if (connection != null) {
            connection.restartConnection(proceed);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void error(int errorId, String errorMessage) {
        this.mEventHandler.error(errorId, errorMessage);
    }
}
