package android.media;

import android.content.IntentFilter;
import android.media.IMediaHTTPConnection;
import android.media.MediaMetrics;
import android.net.InetAddresses;
import android.p008os.IBinder;
import android.p008os.StrictMode;
import android.util.Log;
import com.android.internal.content.NativeLibraryHelper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownServiceException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes2.dex */
public class MediaHTTPConnection extends IMediaHTTPConnection.Stub {
    private static final int CONNECT_TIMEOUT_MS = 30000;
    private static final int HTTP_TEMP_REDIRECT = 307;
    private static final int MAX_REDIRECTS = 20;
    private static final String TAG = "MediaHTTPConnection";
    private static final boolean VERBOSE = false;
    private long mNativeContext;
    private long mCurrentOffset = -1;
    private URL mURL = null;
    private Map<String, String> mHeaders = null;
    private volatile HttpURLConnection mConnection = null;
    private long mTotalSize = -1;
    private InputStream mInputStream = null;
    private boolean mAllowCrossDomainRedirect = true;
    private boolean mAllowCrossProtocolRedirect = true;
    private final AtomicInteger mNumDisconnectingThreads = new AtomicInteger(0);

    private final native void native_finalize();

    private final native IBinder native_getIMemory();

    private static final native void native_init();

    private final native int native_readAt(long j, int i);

    private final native void native_setup();

    public MediaHTTPConnection() {
        CookieHandler cookieHandler = CookieHandler.getDefault();
        if (cookieHandler == null) {
            Log.m104w(TAG, "MediaHTTPConnection: Unexpected. No CookieHandler found.");
        }
        native_setup();
    }

    @Override // android.media.IMediaHTTPConnection
    public synchronized IBinder connect(String uri, String headers) {
        try {
            disconnect();
            try {
                this.mAllowCrossDomainRedirect = true;
                this.mURL = new URL(uri);
                this.mHeaders = convertHeaderStringToMap(headers);
                return native_getIMemory();
            } catch (MalformedURLException e) {
                return null;
            }
        } catch (MalformedURLException e2) {
        }
    }

    private static boolean parseBoolean(String val) {
        try {
            return Long.parseLong(val) != 0;
        } catch (NumberFormatException e) {
            return "true".equalsIgnoreCase(val) || MediaMetrics.Value.YES.equalsIgnoreCase(val);
        }
    }

    private synchronized boolean filterOutInternalHeaders(String key, String val) {
        if ("android-allow-cross-domain-redirect".equalsIgnoreCase(key)) {
            boolean parseBoolean = parseBoolean(val);
            this.mAllowCrossDomainRedirect = parseBoolean;
            this.mAllowCrossProtocolRedirect = parseBoolean;
            return true;
        }
        return false;
    }

    private synchronized Map<String, String> convertHeaderStringToMap(String headers) {
        HashMap<String, String> map;
        map = new HashMap<>();
        String[] pairs = headers.split("\r\n");
        for (String pair : pairs) {
            int colonPos = pair.indexOf(":");
            if (colonPos >= 0) {
                String key = pair.substring(0, colonPos);
                String val = pair.substring(colonPos + 1);
                if (!filterOutInternalHeaders(key, val)) {
                    map.put(key, val);
                }
            }
        }
        return map;
    }

    @Override // android.media.IMediaHTTPConnection
    public void disconnect() {
        this.mNumDisconnectingThreads.incrementAndGet();
        try {
            HttpURLConnection connectionToDisconnect = this.mConnection;
            if (connectionToDisconnect != null) {
                connectionToDisconnect.disconnect();
            }
            synchronized (this) {
                teardownConnection();
                this.mHeaders = null;
                this.mURL = null;
            }
        } finally {
            this.mNumDisconnectingThreads.decrementAndGet();
        }
    }

    private synchronized void teardownConnection() {
        if (this.mConnection != null) {
            InputStream inputStream = this.mInputStream;
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
                this.mInputStream = null;
            }
            this.mConnection.disconnect();
            this.mConnection = null;
            this.mCurrentOffset = -1L;
        }
    }

    private static final boolean isLocalHost(URL url) {
        String host;
        if (url == null || (host = url.getHost()) == null) {
            return false;
        }
        if (host.equalsIgnoreCase("localhost")) {
            return true;
        }
        return InetAddresses.parseNumericAddress(host).isLoopbackAddress();
    }

    /* JADX WARN: Code restructure failed: missing block: B:94:0x01b0, code lost:
        r16.mURL = r8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private synchronized void seekTo(long offset) throws IOException {
        int lastSlashPos;
        teardownConnection();
        long j = -1;
        try {
            URL url = this.mURL;
            boolean noProxy = isLocalHost(url);
            URL url2 = url;
            int redirectCount = 0;
            while (this.mNumDisconnectingThreads.get() <= 0) {
                if (noProxy) {
                    this.mConnection = (HttpURLConnection) url2.openConnection(Proxy.NO_PROXY);
                } else {
                    this.mConnection = (HttpURLConnection) url2.openConnection();
                }
                if (this.mNumDisconnectingThreads.get() > 0) {
                    throw new IOException("concurrently disconnecting");
                }
                this.mConnection.setConnectTimeout(30000);
                this.mConnection.setInstanceFollowRedirects(this.mAllowCrossDomainRedirect);
                Map<String, String> map = this.mHeaders;
                if (map != null) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        this.mConnection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                if (offset > 0) {
                    this.mConnection.setRequestProperty("Range", "bytes=" + offset + NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                }
                int response = this.mConnection.getResponseCode();
                if (response == 300 || response == 301 || response == 302 || response == 303 || response == 307) {
                    redirectCount++;
                    if (redirectCount > 20) {
                        throw new NoRouteToHostException("Too many redirects: " + redirectCount);
                    }
                    String method = this.mConnection.getRequestMethod();
                    if (response == 307 && !method.equals("GET") && !method.equals("HEAD")) {
                        throw new NoRouteToHostException("Invalid redirect");
                    }
                    String location = this.mConnection.getHeaderField("Location");
                    if (location == null) {
                        throw new NoRouteToHostException("Invalid redirect");
                    }
                    url2 = new URL(this.mURL, location);
                    if (!url2.getProtocol().equals(IntentFilter.SCHEME_HTTPS) && !url2.getProtocol().equals(IntentFilter.SCHEME_HTTP)) {
                        throw new NoRouteToHostException("Unsupported protocol redirect");
                    }
                    boolean sameProtocol = this.mURL.getProtocol().equals(url2.getProtocol());
                    if (!this.mAllowCrossProtocolRedirect && !sameProtocol) {
                        throw new NoRouteToHostException("Cross-protocol redirects are disallowed");
                    }
                    boolean sameHost = this.mURL.getHost().equals(url2.getHost());
                    if (!this.mAllowCrossDomainRedirect && !sameHost) {
                        throw new NoRouteToHostException("Cross-domain redirects are disallowed");
                    }
                    j = -1;
                } else {
                    if (this.mAllowCrossDomainRedirect) {
                        this.mURL = this.mConnection.getURL();
                    }
                    if (response == 206) {
                        String contentRange = this.mConnection.getHeaderField("Content-Range");
                        this.mTotalSize = j;
                        if (contentRange != null && (lastSlashPos = contentRange.lastIndexOf(47)) >= 0) {
                            String total = contentRange.substring(lastSlashPos + 1);
                            try {
                                this.mTotalSize = Long.parseLong(total);
                            } catch (NumberFormatException e) {
                            }
                        }
                    } else if (response != 200) {
                        throw new IOException();
                    } else {
                        this.mTotalSize = this.mConnection.getContentLength();
                    }
                    if (offset > 0 && response != 206) {
                        throw new ProtocolException();
                    }
                    this.mInputStream = new BufferedInputStream(this.mConnection.getInputStream());
                    this.mCurrentOffset = offset;
                }
            }
            throw new IOException("concurrently disconnecting");
        } catch (IOException e2) {
            this.mTotalSize = -1L;
            teardownConnection();
            this.mCurrentOffset = -1L;
            throw e2;
        }
    }

    @Override // android.media.IMediaHTTPConnection
    public synchronized int readAt(long offset, int size) {
        return native_readAt(offset, size);
    }

    private synchronized int readAt(long offset, byte[] data, int size) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            try {
                if (offset != this.mCurrentOffset) {
                    try {
                        seekTo(offset);
                    } catch (UnknownServiceException e) {
                        e = e;
                        Log.m104w(TAG, "readAt " + offset + " / " + size + " => " + e);
                        return MediaPlayer.MEDIA_ERROR_UNSUPPORTED;
                    } catch (IOException e2) {
                        return -1;
                    }
                }
                int n = this.mInputStream.read(data, 0, size);
                if (n == -1) {
                    n = 0;
                }
                this.mCurrentOffset += n;
                return n;
            } catch (UnknownServiceException e3) {
                e = e3;
            } catch (IOException e4) {
            }
        } catch (NoRouteToHostException e5) {
            Log.m104w(TAG, "readAt " + offset + " / " + size + " => " + e5);
            return MediaPlayer.MEDIA_ERROR_UNSUPPORTED;
        } catch (ProtocolException e6) {
            Log.m104w(TAG, "readAt " + offset + " / " + size + " => " + e6);
            return MediaPlayer.MEDIA_ERROR_UNSUPPORTED;
        } catch (Exception e7) {
            return -1;
        }
    }

    @Override // android.media.IMediaHTTPConnection
    public synchronized long getSize() {
        if (this.mConnection == null) {
            try {
                seekTo(0L);
            } catch (IOException e) {
                return -1L;
            }
        }
        return this.mTotalSize;
    }

    @Override // android.media.IMediaHTTPConnection
    public synchronized String getMIMEType() {
        if (this.mConnection == null) {
            try {
                seekTo(0L);
            } catch (IOException e) {
                return "application/octet-stream";
            }
        }
        return this.mConnection.getContentType();
    }

    @Override // android.media.IMediaHTTPConnection
    public synchronized String getUri() {
        return this.mURL.toString();
    }

    protected void finalize() {
        native_finalize();
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}
