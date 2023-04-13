package android.media;

import android.media.IMediaHTTPService;
import android.p008os.IBinder;
import android.util.Log;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.List;
/* loaded from: classes2.dex */
public class MediaHTTPService extends IMediaHTTPService.Stub {
    private static final String TAG = "MediaHTTPService";
    private List<HttpCookie> mCookies;
    private final Object mCookieStoreInitializedLock = new Object();
    private boolean mCookieStoreInitialized = false;

    public MediaHTTPService(List<HttpCookie> cookies) {
        this.mCookies = cookies;
        Log.m106v(TAG, "MediaHTTPService(" + this + "): Cookies: " + cookies);
    }

    @Override // android.media.IMediaHTTPService
    public IMediaHTTPConnection makeHTTPConnection() {
        synchronized (this.mCookieStoreInitializedLock) {
            if (!this.mCookieStoreInitialized) {
                CookieHandler cookieHandler = CookieHandler.getDefault();
                if (cookieHandler == null) {
                    cookieHandler = new CookieManager();
                    CookieHandler.setDefault(cookieHandler);
                    Log.m106v(TAG, "makeHTTPConnection: CookieManager created: " + cookieHandler);
                } else {
                    Log.m106v(TAG, "makeHTTPConnection: CookieHandler (" + cookieHandler + ") exists.");
                }
                if (this.mCookies != null) {
                    if (cookieHandler instanceof CookieManager) {
                        CookieManager cookieManager = (CookieManager) cookieHandler;
                        CookieStore store = cookieManager.getCookieStore();
                        for (HttpCookie cookie : this.mCookies) {
                            try {
                                store.add(null, cookie);
                            } catch (Exception e) {
                                Log.m106v(TAG, "makeHTTPConnection: CookieStore.add" + e);
                            }
                        }
                    } else {
                        Log.m104w(TAG, "makeHTTPConnection: The installed CookieHandler is not a CookieManager. Can’t add the provided cookies to the cookie store.");
                    }
                }
                this.mCookieStoreInitialized = true;
                Log.m106v(TAG, "makeHTTPConnection(" + this + "): cookieHandler: " + cookieHandler + " Cookies: " + this.mCookies);
            }
        }
        return new MediaHTTPConnection();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IBinder createHttpServiceBinderIfNecessary(String path) {
        return createHttpServiceBinderIfNecessary(path, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static IBinder createHttpServiceBinderIfNecessary(String path, List<HttpCookie> cookies) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return new MediaHTTPService(cookies).asBinder();
        }
        if (path.startsWith("widevine://")) {
            Log.m112d(TAG, "Widevine classic is no longer supported");
            return null;
        }
        return null;
    }
}
