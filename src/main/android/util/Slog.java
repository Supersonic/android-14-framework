package android.util;

import android.annotation.SystemApi;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* loaded from: classes3.dex */
public final class Slog {
    private Slog() {
    }

    /* renamed from: v */
    public static int m92v(String tag, String msg) {
        return Log.println_native(3, 2, tag, msg);
    }

    /* renamed from: v */
    public static int m91v(String tag, String msg, Throwable tr) {
        return Log.println_native(3, 2, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /* renamed from: d */
    public static int m98d(String tag, String msg) {
        return Log.println_native(3, 3, tag, msg);
    }

    /* renamed from: d */
    public static int m97d(String tag, String msg, Throwable tr) {
        return Log.println_native(3, 3, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /* renamed from: i */
    public static int m94i(String tag, String msg) {
        return Log.println_native(3, 4, tag, msg);
    }

    /* renamed from: i */
    public static int m93i(String tag, String msg, Throwable tr) {
        return Log.println_native(3, 4, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /* renamed from: w */
    public static int m90w(String tag, String msg) {
        return Log.println_native(3, 5, tag, msg);
    }

    /* renamed from: w */
    public static int m89w(String tag, String msg, Throwable tr) {
        return Log.println_native(3, 5, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    /* renamed from: w */
    public static int m88w(String tag, Throwable tr) {
        return Log.println_native(3, 5, tag, Log.getStackTraceString(tr));
    }

    /* renamed from: e */
    public static int m96e(String tag, String msg) {
        return Log.println_native(3, 6, tag, msg);
    }

    /* renamed from: e */
    public static int m95e(String tag, String msg, Throwable tr) {
        return Log.println_native(3, 6, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    public static int wtf(String tag, String msg) {
        return Log.wtf(3, tag, msg, null, false, true);
    }

    public static void wtfQuiet(String tag, String msg) {
        Log.wtfQuiet(3, tag, msg, true);
    }

    public static int wtfStack(String tag, String msg) {
        return Log.wtf(3, tag, msg, null, true, true);
    }

    public static int wtf(String tag, Throwable tr) {
        return Log.wtf(3, tag, tr.getMessage(), tr, false, true);
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        return Log.wtf(3, tag, msg, tr, false, true);
    }

    public static int println(int priority, String tag, String msg) {
        return Log.println_native(3, priority, tag, msg);
    }
}
