package android.webkit;

import android.annotation.SystemApi;
import android.content.ContentResolver;
import android.graphics.Bitmap;
@Deprecated
/* loaded from: classes4.dex */
public abstract class WebIconDatabase {

    @Deprecated
    /* loaded from: classes4.dex */
    public interface IconListener {
        void onReceivedIcon(String str, Bitmap bitmap);
    }

    @SystemApi
    public abstract void bulkRequestIconForPageUrl(ContentResolver contentResolver, String str, IconListener iconListener);

    public abstract void close();

    public abstract void open(String str);

    public abstract void releaseIconForPageUrl(String str);

    public abstract void removeAllIcons();

    public abstract void requestIconForPageUrl(String str, IconListener iconListener);

    public abstract void retainIconForPageUrl(String str);

    public static WebIconDatabase getInstance() {
        return WebViewFactory.getProvider().getWebIconDatabase();
    }
}
