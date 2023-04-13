package android.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.SntpClient;
import android.p008os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.C4057R;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public abstract class NtpTrustedTime implements TrustedTime {
    private static final boolean LOGD = false;
    public static final String NTP_SETTING_SERVER_NAME_DELIMITER = "|";
    private static final String NTP_SETTING_SERVER_NAME_DELIMITER_REGEXP = "\\|";
    private static final String TAG = "NtpTrustedTime";
    private static final String URI_SCHEME_NTP = "ntp";
    private static NtpTrustedTime sSingleton;
    private URI mLastSuccessfulNtpServerUri;
    private NtpConfig mNtpConfigForTests;
    private volatile TimeResult mTimeResult;

    public abstract Network getDefaultNetwork();

    public abstract NtpConfig getNtpConfigInternal();

    public abstract boolean isNetworkConnected(Network network);

    public abstract TimeResult queryNtpServer(Network network, URI uri, java.time.Duration duration);

    /* loaded from: classes3.dex */
    public static final class NtpConfig {
        private final List<URI> mServerUris;
        private final java.time.Duration mTimeout;

        public NtpConfig(List<URI> serverUris, java.time.Duration timeout) throws IllegalArgumentException {
            Objects.requireNonNull(serverUris);
            if (serverUris.isEmpty()) {
                throw new IllegalArgumentException("Server URIs is empty");
            }
            List<URI> validatedServerUris = new ArrayList<>();
            for (URI serverUri : serverUris) {
                try {
                    URI validatedServerUri = NtpTrustedTime.validateNtpServerUri((URI) Objects.requireNonNull(serverUri));
                    validatedServerUris.add(validatedServerUri);
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Bad server URI", e);
                }
            }
            this.mServerUris = Collections.unmodifiableList(validatedServerUris);
            if (timeout.isNegative() || timeout.isZero()) {
                throw new IllegalArgumentException("timeout < 0");
            }
            this.mTimeout = timeout;
        }

        public List<URI> getServerUris() {
            return this.mServerUris;
        }

        public java.time.Duration getTimeout() {
            return this.mTimeout;
        }

        public String toString() {
            return "NtpConnectionInfo{mServerUris=" + this.mServerUris + ", mTimeout=" + this.mTimeout + '}';
        }
    }

    /* loaded from: classes3.dex */
    public static class TimeResult {
        private final long mElapsedRealtimeMillis;
        private final InetSocketAddress mNtpServerSocketAddress;
        private final int mUncertaintyMillis;
        private final long mUnixEpochTimeMillis;

        public TimeResult(long unixEpochTimeMillis, long elapsedRealtimeMillis, int uncertaintyMillis, InetSocketAddress ntpServerSocketAddress) {
            this.mUnixEpochTimeMillis = unixEpochTimeMillis;
            this.mElapsedRealtimeMillis = elapsedRealtimeMillis;
            this.mUncertaintyMillis = uncertaintyMillis;
            this.mNtpServerSocketAddress = (InetSocketAddress) Objects.requireNonNull(ntpServerSocketAddress);
        }

        public long getTimeMillis() {
            return this.mUnixEpochTimeMillis;
        }

        public long getElapsedRealtimeMillis() {
            return this.mElapsedRealtimeMillis;
        }

        public int getUncertaintyMillis() {
            return this.mUncertaintyMillis;
        }

        public long currentTimeMillis() {
            return this.mUnixEpochTimeMillis + getAgeMillis();
        }

        public long getAgeMillis() {
            return getAgeMillis(SystemClock.elapsedRealtime());
        }

        public long getAgeMillis(long currentElapsedRealtimeMillis) {
            return currentElapsedRealtimeMillis - this.mElapsedRealtimeMillis;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof TimeResult) {
                TimeResult that = (TimeResult) o;
                return this.mUnixEpochTimeMillis == that.mUnixEpochTimeMillis && this.mElapsedRealtimeMillis == that.mElapsedRealtimeMillis && this.mUncertaintyMillis == that.mUncertaintyMillis && this.mNtpServerSocketAddress.equals(that.mNtpServerSocketAddress);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Long.valueOf(this.mUnixEpochTimeMillis), Long.valueOf(this.mElapsedRealtimeMillis), Integer.valueOf(this.mUncertaintyMillis), this.mNtpServerSocketAddress);
        }

        public String toString() {
            return "TimeResult{unixEpochTime=" + Instant.ofEpochMilli(this.mUnixEpochTimeMillis) + ", elapsedRealtime=" + java.time.Duration.ofMillis(this.mElapsedRealtimeMillis) + ", mUncertaintyMillis=" + this.mUncertaintyMillis + ", mNtpServerSocketAddress=" + this.mNtpServerSocketAddress + '}';
        }
    }

    protected NtpTrustedTime() {
    }

    public static synchronized NtpTrustedTime getInstance(Context context) {
        NtpTrustedTime ntpTrustedTime;
        synchronized (NtpTrustedTime.class) {
            if (sSingleton == null) {
                Context appContext = context.getApplicationContext();
                sSingleton = new NtpTrustedTimeImpl(appContext);
            }
            ntpTrustedTime = sSingleton;
        }
        return ntpTrustedTime;
    }

    public void setServerConfigForTests(NtpConfig ntpConfig) {
        synchronized (this) {
            this.mNtpConfigForTests = ntpConfig;
        }
    }

    @Override // android.util.TrustedTime
    public boolean forceRefresh() {
        synchronized (this) {
            Network network = getDefaultNetwork();
            if (network == null) {
                return false;
            }
            return forceRefreshLocked(network);
        }
    }

    public boolean forceRefresh(Network network) {
        boolean forceRefreshLocked;
        Objects.requireNonNull(network);
        synchronized (this) {
            forceRefreshLocked = forceRefreshLocked(network);
        }
        return forceRefreshLocked;
    }

    private boolean forceRefreshLocked(Network network) {
        NtpConfig ntpConfig;
        Objects.requireNonNull(network);
        if (isNetworkConnected(network) && (ntpConfig = getNtpConfig()) != null) {
            List<URI> unorderedServerUris = ntpConfig.getServerUris();
            List<URI> orderedServerUris = new ArrayList<>();
            for (URI serverUri : unorderedServerUris) {
                if (serverUri.equals(this.mLastSuccessfulNtpServerUri)) {
                    orderedServerUris.add(0, serverUri);
                } else {
                    orderedServerUris.add(serverUri);
                }
            }
            for (URI serverUri2 : orderedServerUris) {
                TimeResult timeResult = queryNtpServer(network, serverUri2, ntpConfig.getTimeout());
                if (timeResult != null) {
                    this.mLastSuccessfulNtpServerUri = serverUri2;
                    this.mTimeResult = timeResult;
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private NtpConfig getNtpConfig() {
        NtpConfig ntpConfig = this.mNtpConfigForTests;
        if (ntpConfig != null) {
            return ntpConfig;
        }
        return getNtpConfigInternal();
    }

    @Override // android.util.TrustedTime
    @Deprecated
    public boolean hasCache() {
        return this.mTimeResult != null;
    }

    @Override // android.util.TrustedTime
    @Deprecated
    public long getCacheAge() {
        TimeResult timeResult = this.mTimeResult;
        if (timeResult != null) {
            return SystemClock.elapsedRealtime() - timeResult.getElapsedRealtimeMillis();
        }
        return Long.MAX_VALUE;
    }

    @Override // android.util.TrustedTime
    @Deprecated
    public long currentTimeMillis() {
        TimeResult timeResult = this.mTimeResult;
        if (timeResult == null) {
            throw new IllegalStateException("Missing authoritative time source");
        }
        return timeResult.currentTimeMillis();
    }

    @Deprecated
    public long getCachedNtpTime() {
        TimeResult timeResult = this.mTimeResult;
        if (timeResult == null) {
            return 0L;
        }
        return timeResult.getTimeMillis();
    }

    @Deprecated
    public long getCachedNtpTimeReference() {
        TimeResult timeResult = this.mTimeResult;
        if (timeResult == null) {
            return 0L;
        }
        return timeResult.getElapsedRealtimeMillis();
    }

    public TimeResult getCachedTimeResult() {
        return this.mTimeResult;
    }

    public void setCachedTimeResult(TimeResult timeResult) {
        synchronized (this) {
            this.mTimeResult = timeResult;
        }
    }

    public void clearCachedTimeResult() {
        synchronized (this) {
            this.mTimeResult = null;
        }
    }

    public static URI parseNtpUriStrict(String ntpServerUriString) throws URISyntaxException {
        URI unvalidatedUri = new URI(ntpServerUriString);
        return validateNtpServerUri(unvalidatedUri);
    }

    public static List<URI> parseNtpServerSetting(String ntpServerSetting) {
        if (TextUtils.isEmpty(ntpServerSetting)) {
            return null;
        }
        String[] values = ntpServerSetting.split(NTP_SETTING_SERVER_NAME_DELIMITER_REGEXP);
        if (values.length == 0) {
            return null;
        }
        List<URI> uris = new ArrayList<>();
        for (String value : values) {
            if (value.startsWith("ntp:")) {
                try {
                    uris.add(parseNtpUriStrict(value));
                } catch (URISyntaxException e) {
                    Log.m103w(TAG, "Rejected NTP uri setting=" + ntpServerSetting, e);
                    return null;
                }
            } else {
                try {
                    URI uri = new URI(URI_SCHEME_NTP, value, null, null);
                    URI validatedUri = validateNtpServerUri(uri);
                    uris.add(validatedUri);
                } catch (URISyntaxException e2) {
                    Log.m103w(TAG, "Rejected NTP legacy setting=" + ntpServerSetting, e2);
                    return null;
                }
            }
        }
        return uris;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static URI validateNtpServerUri(URI uri) throws URISyntaxException {
        if (!uri.isAbsolute()) {
            throw new URISyntaxException(uri.toString(), "Relative URI not supported");
        }
        if (!URI_SCHEME_NTP.equals(uri.getScheme())) {
            throw new URISyntaxException(uri.toString(), "Unrecognized scheme");
        }
        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            throw new URISyntaxException(uri.toString(), "Missing host");
        }
        return uri;
    }

    public void dump(PrintWriter pw) {
        synchronized (this) {
            pw.println("getNtpConfig()=" + getNtpConfig());
            pw.println("mNtpConfigForTests=" + this.mNtpConfigForTests);
            pw.println("mLastSuccessfulNtpServerUri=" + this.mLastSuccessfulNtpServerUri);
            pw.println("mTimeResult=" + this.mTimeResult);
            if (this.mTimeResult != null) {
                pw.println("mTimeResult.getAgeMillis()=" + java.time.Duration.ofMillis(this.mTimeResult.getAgeMillis()));
            }
        }
    }

    /* loaded from: classes3.dex */
    private static final class NtpTrustedTimeImpl extends NtpTrustedTime {
        private ConnectivityManager mConnectivityManager;
        private final Context mContext;

        private NtpTrustedTimeImpl(Context context) {
            this.mContext = (Context) Objects.requireNonNull(context);
        }

        @Override // android.util.NtpTrustedTime
        public NtpConfig getNtpConfigInternal() {
            List<URI> ntpServerUris;
            ContentResolver resolver = this.mContext.getContentResolver();
            Resources res = this.mContext.getResources();
            String serverGlobalSetting = Settings.Global.getString(resolver, Settings.Global.NTP_SERVER);
            List<URI> settingsServerUris = parseNtpServerSetting(serverGlobalSetting);
            if (settingsServerUris != null) {
                ntpServerUris = settingsServerUris;
            } else {
                String[] configValues = res.getStringArray(C4057R.array.config_ntpServers);
                try {
                    List<URI> ntpServerUris2 = new ArrayList<>();
                    for (String configValue : configValues) {
                        ntpServerUris2.add(parseNtpUriStrict(configValue));
                    }
                    ntpServerUris = ntpServerUris2;
                } catch (URISyntaxException e) {
                    ntpServerUris = null;
                }
            }
            int defaultTimeoutMillis = res.getInteger(C4057R.integer.config_ntpTimeout);
            java.time.Duration timeout = java.time.Duration.ofMillis(Settings.Global.getInt(resolver, Settings.Global.NTP_TIMEOUT, defaultTimeoutMillis));
            if (ntpServerUris == null) {
                return null;
            }
            return new NtpConfig(ntpServerUris, timeout);
        }

        @Override // android.util.NtpTrustedTime
        public Network getDefaultNetwork() {
            ConnectivityManager connectivityManager = getConnectivityManager();
            if (connectivityManager == null) {
                return null;
            }
            return connectivityManager.getActiveNetwork();
        }

        @Override // android.util.NtpTrustedTime
        public boolean isNetworkConnected(Network network) {
            NetworkInfo ni;
            ConnectivityManager connectivityManager = getConnectivityManager();
            if (connectivityManager == null || (ni = connectivityManager.getNetworkInfo(network)) == null || !ni.isConnected()) {
                return false;
            }
            return true;
        }

        private synchronized ConnectivityManager getConnectivityManager() {
            if (this.mConnectivityManager == null) {
                this.mConnectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
            }
            return this.mConnectivityManager;
        }

        @Override // android.util.NtpTrustedTime
        public TimeResult queryNtpServer(Network network, URI ntpServerUri, java.time.Duration timeout) {
            SntpClient client = new SntpClient();
            String serverName = ntpServerUri.getHost();
            int port = ntpServerUri.getPort() == -1 ? 123 : ntpServerUri.getPort();
            int timeoutMillis = saturatedCast(timeout.toMillis());
            if (client.requestTime(serverName, port, timeoutMillis, network)) {
                int ntpUncertaintyMillis = saturatedCast(client.getRoundTripTime() / 2);
                InetSocketAddress ntpServerSocketAddress = client.getServerSocketAddress();
                return new TimeResult(client.getNtpTime(), client.getNtpTimeReference(), ntpUncertaintyMillis, ntpServerSocketAddress);
            }
            return null;
        }

        private static int saturatedCast(long longValue) {
            if (longValue > 2147483647L) {
                return Integer.MAX_VALUE;
            }
            if (longValue < -2147483648L) {
                return Integer.MIN_VALUE;
            }
            return (int) longValue;
        }
    }
}
