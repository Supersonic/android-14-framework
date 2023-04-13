package android.webkit;

import android.content.Context;
import android.p008os.Handler;
@Deprecated
/* loaded from: classes4.dex */
abstract class WebSyncManager implements Runnable {
    protected static final String LOGTAG = "websync";
    protected WebViewDatabase mDataBase;
    protected Handler mHandler;

    abstract void syncFromRamToFlash();

    /* JADX INFO: Access modifiers changed from: protected */
    public WebSyncManager(Context context, String name) {
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("doesn't implement Cloneable");
    }

    @Override // java.lang.Runnable
    public void run() {
    }

    public void sync() {
    }

    public void resetSync() {
    }

    public void startSync() {
    }

    public void stopSync() {
    }

    protected void onSyncInit() {
    }
}
