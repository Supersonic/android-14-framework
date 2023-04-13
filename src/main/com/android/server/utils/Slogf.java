package com.android.server.utils;

import android.util.Slog;
import android.util.TimingsTraceLog;
import com.android.internal.annotations.GuardedBy;
import java.util.Formatter;
import java.util.Locale;
/* loaded from: classes2.dex */
public final class Slogf {
    @GuardedBy({"sMessageBuilder"})
    private static final Formatter sFormatter;
    @GuardedBy({"sMessageBuilder"})
    private static final StringBuilder sMessageBuilder;

    static {
        TimingsTraceLog timingsTraceLog = new TimingsTraceLog("SLog", 524288L);
        timingsTraceLog.traceBegin("static_init");
        StringBuilder sb = new StringBuilder();
        sMessageBuilder = sb;
        sFormatter = new Formatter(sb, Locale.ENGLISH);
        timingsTraceLog.traceEnd();
    }

    private Slogf() {
        throw new UnsupportedOperationException("provides only static methods");
    }

    /* renamed from: v */
    public static int m18v(String str, String str2) {
        return Slog.v(str, str2);
    }

    /* renamed from: v */
    public static int m17v(String str, String str2, Throwable th) {
        return Slog.v(str, str2, th);
    }

    /* renamed from: d */
    public static int m30d(String str, String str2) {
        return Slog.d(str, str2);
    }

    /* renamed from: d */
    public static int m29d(String str, String str2, Throwable th) {
        return Slog.d(str, str2, th);
    }

    /* renamed from: i */
    public static int m22i(String str, String str2) {
        return Slog.i(str, str2);
    }

    /* renamed from: i */
    public static int m21i(String str, String str2, Throwable th) {
        return Slog.i(str, str2, th);
    }

    /* renamed from: w */
    public static int m14w(String str, String str2) {
        return Slog.w(str, str2);
    }

    /* renamed from: w */
    public static int m13w(String str, String str2, Throwable th) {
        return Slog.w(str, str2, th);
    }

    /* renamed from: w */
    public static int m11w(String str, Throwable th) {
        return Slog.w(str, th);
    }

    /* renamed from: e */
    public static int m26e(String str, String str2) {
        return Slog.e(str, str2);
    }

    /* renamed from: e */
    public static int m25e(String str, String str2, Throwable th) {
        return Slog.e(str, str2, th);
    }

    public static int wtf(String str, String str2) {
        return Slog.wtf(str, str2);
    }

    public static void wtfQuiet(String str, String str2) {
        Slog.wtfQuiet(str, str2);
    }

    public static int wtfStack(String str, String str2) {
        return Slog.wtfStack(str, str2);
    }

    public static int wtf(String str, Throwable th) {
        return Slog.wtf(str, th);
    }

    public static int wtf(String str, String str2, Throwable th) {
        return Slog.wtf(str, str2, th);
    }

    public static int println(int i, String str, String str2) {
        return Slog.println(i, str, str2);
    }

    /* renamed from: v */
    public static void m16v(String str, String str2, Object... objArr) {
        m18v(str, getMessage(str2, objArr));
    }

    /* renamed from: v */
    public static void m15v(String str, Throwable th, String str2, Object... objArr) {
        m17v(str, getMessage(str2, objArr), th);
    }

    /* renamed from: d */
    public static void m28d(String str, String str2, Object... objArr) {
        m30d(str, getMessage(str2, objArr));
    }

    /* renamed from: d */
    public static void m27d(String str, Throwable th, String str2, Object... objArr) {
        m29d(str, getMessage(str2, objArr), th);
    }

    /* renamed from: i */
    public static void m20i(String str, String str2, Object... objArr) {
        m22i(str, getMessage(str2, objArr));
    }

    /* renamed from: i */
    public static void m19i(String str, Throwable th, String str2, Object... objArr) {
        m21i(str, getMessage(str2, objArr), th);
    }

    /* renamed from: w */
    public static void m12w(String str, String str2, Object... objArr) {
        m14w(str, getMessage(str2, objArr));
    }

    /* renamed from: w */
    public static void m10w(String str, Throwable th, String str2, Object... objArr) {
        m13w(str, getMessage(str2, objArr), th);
    }

    /* renamed from: e */
    public static void m24e(String str, String str2, Object... objArr) {
        m26e(str, getMessage(str2, objArr));
    }

    /* renamed from: e */
    public static void m23e(String str, Throwable th, String str2, Object... objArr) {
        m25e(str, getMessage(str2, objArr), th);
    }

    public static void wtf(String str, String str2, Object... objArr) {
        wtf(str, getMessage(str2, objArr));
    }

    public static void wtf(String str, Throwable th, String str2, Object... objArr) {
        wtf(str, getMessage(str2, objArr), th);
    }

    private static String getMessage(String str, Object... objArr) {
        String sb;
        StringBuilder sb2 = sMessageBuilder;
        synchronized (sb2) {
            sFormatter.format(str, objArr);
            sb = sb2.toString();
            sb2.setLength(0);
        }
        return sb;
    }
}
