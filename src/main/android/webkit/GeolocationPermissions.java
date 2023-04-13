package android.webkit;

import java.util.Set;
/* loaded from: classes4.dex */
public class GeolocationPermissions {

    /* loaded from: classes4.dex */
    public interface Callback {
        void invoke(String str, boolean z, boolean z2);
    }

    public static GeolocationPermissions getInstance() {
        return WebViewFactory.getProvider().getGeolocationPermissions();
    }

    public void getOrigins(ValueCallback<Set<String>> callback) {
    }

    public void getAllowed(String origin, ValueCallback<Boolean> callback) {
    }

    public void clear(String origin) {
    }

    public void allow(String origin) {
    }

    public void clearAll() {
    }
}
