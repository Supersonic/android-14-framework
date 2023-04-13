package android.view.textclassifier;
/* loaded from: classes4.dex */
public final class Log {
    static final boolean ENABLE_FULL_LOGGING = android.util.Log.isLoggable(TextClassifier.LOG_TAG, 2);

    private Log() {
    }

    /* renamed from: v */
    public static void m77v(String tag, String msg) {
        if (ENABLE_FULL_LOGGING) {
            android.util.Log.m106v(tag, msg);
        }
    }

    /* renamed from: d */
    public static void m79d(String tag, String msg) {
        android.util.Log.m112d(tag, msg);
    }

    /* renamed from: w */
    public static void m76w(String tag, String msg) {
        android.util.Log.m104w(tag, msg);
    }

    /* renamed from: e */
    public static void m78e(String tag, String msg, Throwable tr) {
        if (ENABLE_FULL_LOGGING) {
            android.util.Log.m109e(tag, msg, tr);
            return;
        }
        String trString = tr != null ? tr.getClass().getSimpleName() : "??";
        android.util.Log.m112d(tag, String.format("%s (%s)", msg, trString));
    }
}
