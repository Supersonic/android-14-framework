package android.test.mock;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IContentProvider;
import android.database.ContentObserver;
import android.net.Uri;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class MockContentResolver extends ContentResolver {
    Map<String, ContentProvider> mProviders;

    public MockContentResolver() {
        this(null);
    }

    public MockContentResolver(Context context) {
        super(context);
        this.mProviders = new HashMap();
    }

    public void addProvider(String name, ContentProvider provider) {
        this.mProviders.put(name, provider);
    }

    protected IContentProvider acquireProvider(Context context, String name) {
        return acquireExistingProvider(context, name);
    }

    protected IContentProvider acquireExistingProvider(Context context, String name) {
        ContentProvider provider = this.mProviders.get(name);
        if (provider != null) {
            return provider.getIContentProvider();
        }
        return null;
    }

    public boolean releaseProvider(IContentProvider provider) {
        return true;
    }

    protected IContentProvider acquireUnstableProvider(Context c, String name) {
        return acquireProvider(c, name);
    }

    public boolean releaseUnstableProvider(IContentProvider icp) {
        return releaseProvider(icp);
    }

    public void unstableProviderDied(IContentProvider icp) {
    }

    @Override // android.content.ContentResolver
    public void notifyChange(Uri uri, ContentObserver observer) {
    }

    @Override // android.content.ContentResolver
    @Deprecated
    public void notifyChange(Uri uri, ContentObserver observer, boolean syncToNetwork) {
    }

    @Override // android.content.ContentResolver
    public void notifyChange(Uri uri, ContentObserver observer, int flags) {
    }

    @Override // android.content.ContentResolver
    public void notifyChange(Collection<Uri> uris, ContentObserver observer, int flags) {
    }
}
