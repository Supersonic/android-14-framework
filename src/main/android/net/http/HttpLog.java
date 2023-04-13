package android.net.http;

import android.os.SystemClock;
import android.util.Log;
/* loaded from: classes.dex */
class HttpLog {
    private static final boolean DEBUG = false;
    private static final String LOGTAG = "http";
    static final boolean LOGV = false;

    HttpLog() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: v */
    public static void m2v(String logMe) {
        Log.v("http", SystemClock.uptimeMillis() + " " + Thread.currentThread().getName() + " " + logMe);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: e */
    public static void m3e(String logMe) {
        Log.e("http", logMe);
    }
}
