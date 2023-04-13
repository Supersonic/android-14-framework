package android.net;

import android.annotation.SystemApi;
import android.content.Context;
import android.text.TextUtils;
import android.util.NtpTrustedTime;
import com.android.net.module.util.ProxyUtils;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
/* loaded from: classes2.dex */
public final class Proxy {
    @Deprecated
    public static final String EXTRA_PROXY_INFO = "android.intent.extra.PROXY_INFO";
    public static final String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE";
    private static final String TAG = "Proxy";
    private static ConnectivityManager sConnectivityManager = null;
    private static final ProxySelector sDefaultProxySelector = ProxySelector.getDefault();

    public static final java.net.Proxy getProxy(Context ctx, String url) {
        if (url != null && !isLocalHost("")) {
            URI uri = URI.create(url);
            ProxySelector proxySelector = ProxySelector.getDefault();
            List<java.net.Proxy> proxyList = proxySelector.select(uri);
            if (proxyList.size() > 0) {
                return proxyList.get(0);
            }
        }
        return java.net.Proxy.NO_PROXY;
    }

    @Deprecated
    public static final String getHost(Context ctx) {
        java.net.Proxy proxy = getProxy(ctx, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return null;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getHostName();
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public static final int getPort(Context ctx) {
        java.net.Proxy proxy = getProxy(ctx, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return -1;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getPort();
        } catch (Exception e) {
            return -1;
        }
    }

    @Deprecated
    public static final String getDefaultHost() {
        String host = System.getProperty("http.proxyHost");
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        return host;
    }

    @Deprecated
    public static final int getDefaultPort() {
        if (getDefaultHost() == null) {
            return -1;
        }
        try {
            return Integer.parseInt(System.getProperty("http.proxyPort"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static final boolean isLocalHost(String host) {
        if (host != null && host != null) {
            try {
                if (host.equalsIgnoreCase("localhost")) {
                    return true;
                }
                if (InetAddresses.parseNumericAddress(host).isLoopbackAddress()) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
            }
        }
        return false;
    }

    @Deprecated
    public static void setHttpProxySystemProperty(ProxyInfo p) {
        setHttpProxyConfiguration(p);
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static void setHttpProxyConfiguration(ProxyInfo p) {
        String host = null;
        String port = null;
        String exclList = null;
        Uri pacFileUrl = Uri.EMPTY;
        if (p != null) {
            host = p.getHost();
            port = Integer.toString(p.getPort());
            exclList = ProxyUtils.exclusionListAsString(p.getExclusionList());
            pacFileUrl = p.getPacFileUrl();
        }
        setHttpProxyConfiguration(host, port, exclList, pacFileUrl);
    }

    public static void setHttpProxyConfiguration(String host, String port, String exclList, Uri pacFileUrl) {
        if (exclList != null) {
            exclList = exclList.replace(",", NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
        }
        if (host != null) {
            System.setProperty("http.proxyHost", host);
            System.setProperty("https.proxyHost", host);
        } else {
            System.clearProperty("http.proxyHost");
            System.clearProperty("https.proxyHost");
        }
        if (port != null) {
            System.setProperty("http.proxyPort", port);
            System.setProperty("https.proxyPort", port);
        } else {
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyPort");
        }
        if (exclList != null) {
            System.setProperty("http.nonProxyHosts", exclList);
            System.setProperty("https.nonProxyHosts", exclList);
        } else {
            System.clearProperty("http.nonProxyHosts");
            System.clearProperty("https.nonProxyHosts");
        }
        if (!Uri.EMPTY.equals(pacFileUrl)) {
            ProxySelector.setDefault(new PacProxySelector());
        } else {
            ProxySelector.setDefault(sDefaultProxySelector);
        }
    }
}
