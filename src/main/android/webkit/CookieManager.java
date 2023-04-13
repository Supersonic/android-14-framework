package android.webkit;

import android.annotation.SystemApi;
import android.net.WebAddress;
/* loaded from: classes4.dex */
public abstract class CookieManager {
    public abstract boolean acceptCookie();

    public abstract boolean acceptThirdPartyCookies(WebView webView);

    @SystemApi
    protected abstract boolean allowFileSchemeCookiesImpl();

    public abstract void flush();

    public abstract String getCookie(String str);

    @SystemApi
    public abstract String getCookie(String str, boolean z);

    public abstract boolean hasCookies();

    @SystemApi
    public abstract boolean hasCookies(boolean z);

    @Deprecated
    public abstract void removeAllCookie();

    public abstract void removeAllCookies(ValueCallback<Boolean> valueCallback);

    @Deprecated
    public abstract void removeExpiredCookie();

    @Deprecated
    public abstract void removeSessionCookie();

    public abstract void removeSessionCookies(ValueCallback<Boolean> valueCallback);

    public abstract void setAcceptCookie(boolean z);

    @SystemApi
    protected abstract void setAcceptFileSchemeCookiesImpl(boolean z);

    public abstract void setAcceptThirdPartyCookies(WebView webView, boolean z);

    public abstract void setCookie(String str, String str2);

    public abstract void setCookie(String str, String str2, ValueCallback<Boolean> valueCallback);

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("doesn't implement Cloneable");
    }

    public static CookieManager getInstance() {
        return WebViewFactory.getProvider().getCookieManager();
    }

    @SystemApi
    public synchronized String getCookie(WebAddress uri) {
        return getCookie(uri.toString());
    }

    public static boolean allowFileSchemeCookies() {
        return getInstance().allowFileSchemeCookiesImpl();
    }

    @Deprecated
    public static void setAcceptFileSchemeCookies(boolean accept) {
        getInstance().setAcceptFileSchemeCookiesImpl(accept);
    }
}
