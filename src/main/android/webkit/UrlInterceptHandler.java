package android.webkit;

import android.webkit.CacheManager;
import java.util.Map;
@Deprecated
/* loaded from: classes4.dex */
public interface UrlInterceptHandler {
    @Deprecated
    PluginData getPluginData(String str, Map<String, String> map);

    @Deprecated
    CacheManager.CacheResult service(String str, Map<String, String> map);
}
