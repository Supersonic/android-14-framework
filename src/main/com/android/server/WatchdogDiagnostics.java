package com.android.server;

import android.util.LogWriter;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.Watchdog;
import dalvik.system.AnnotatedStackTraceElement;
import dalvik.system.VMStack;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
/* loaded from: classes.dex */
public class WatchdogDiagnostics {
    public static String getBlockedOnString(Object obj) {
        return String.format("- waiting to lock <0x%08x> (a %s)", Integer.valueOf(System.identityHashCode(obj)), obj.getClass().getName());
    }

    public static String getLockedString(Object obj) {
        return String.format("- locked <0x%08x> (a %s)", Integer.valueOf(System.identityHashCode(obj)), obj.getClass().getName());
    }

    @VisibleForTesting
    public static boolean printAnnotatedStack(Thread thread, PrintWriter printWriter) {
        Object[] heldLocks;
        AnnotatedStackTraceElement[] annotatedThreadStackTrace = VMStack.getAnnotatedThreadStackTrace(thread);
        if (annotatedThreadStackTrace == null) {
            return false;
        }
        printWriter.println(thread.getName() + " annotated stack trace:");
        int length = annotatedThreadStackTrace.length;
        for (int i = 0; i < length; i++) {
            AnnotatedStackTraceElement annotatedStackTraceElement = annotatedThreadStackTrace[i];
            printWriter.println("    at " + annotatedStackTraceElement.getStackTraceElement());
            if (annotatedStackTraceElement.getBlockedOn() != null) {
                printWriter.println("    " + getBlockedOnString(annotatedStackTraceElement.getBlockedOn()));
            }
            if (annotatedStackTraceElement.getHeldLocks() != null) {
                for (Object obj : annotatedStackTraceElement.getHeldLocks()) {
                    printWriter.println("    " + getLockedString(obj));
                }
            }
        }
        return true;
    }

    public static void diagnoseCheckers(List<Watchdog.HandlerChecker> list) {
        PrintWriter printWriter = new PrintWriter((Writer) new LogWriter(5, "Watchdog", 3), true);
        for (int i = 0; i < list.size(); i++) {
            Thread thread = list.get(i).getThread();
            if (!printAnnotatedStack(thread, printWriter)) {
                Slog.w("Watchdog", thread.getName() + " stack trace:");
                StackTraceElement[] stackTrace = thread.getStackTrace();
                int length = stackTrace.length;
                for (int i2 = 0; i2 < length; i2++) {
                    Slog.w("Watchdog", "    at " + stackTrace[i2]);
                }
            }
        }
    }
}
