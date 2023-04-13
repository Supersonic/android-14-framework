package android.webkit;

import android.net.Uri;
import java.util.Map;
/* loaded from: classes4.dex */
public interface WebResourceRequest {
    String getMethod();

    Map<String, String> getRequestHeaders();

    Uri getUrl();

    boolean hasGesture();

    boolean isForMainFrame();

    boolean isRedirect();
}
