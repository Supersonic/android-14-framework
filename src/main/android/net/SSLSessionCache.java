package android.net;

import android.content.Context;
import android.util.Log;
import com.android.org.conscrypt.ClientSessionContext;
import com.android.org.conscrypt.FileClientSessionCache;
import com.android.org.conscrypt.SSLClientSessionCache;
import java.io.File;
import java.io.IOException;
import javax.net.ssl.SSLContext;
/* loaded from: classes2.dex */
public final class SSLSessionCache {
    private static final String TAG = "SSLSessionCache";
    final SSLClientSessionCache mSessionCache;

    public static void install(SSLSessionCache cache, SSLContext context) {
        ClientSessionContext clientSessionContext = context.getClientSessionContext();
        if (clientSessionContext instanceof ClientSessionContext) {
            clientSessionContext.setPersistentCache(cache == null ? null : cache.mSessionCache);
            return;
        }
        throw new IllegalArgumentException("Incompatible SSLContext: " + context);
    }

    public SSLSessionCache(Object cache) {
        this.mSessionCache = (SSLClientSessionCache) cache;
    }

    public SSLSessionCache(File dir) throws IOException {
        this.mSessionCache = FileClientSessionCache.usingDirectory(dir);
    }

    public SSLSessionCache(Context context) {
        File dir = context.getDir("sslcache", 0);
        SSLClientSessionCache cache = null;
        try {
            cache = FileClientSessionCache.usingDirectory(dir);
        } catch (IOException e) {
            Log.m103w(TAG, "Unable to create SSL session cache in " + dir, e);
        }
        this.mSessionCache = cache;
    }
}
