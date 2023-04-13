package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityTrace;
import android.os.Binder;
import android.os.ShellCommand;
import android.p005os.IInstalld;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class AccessibilityTraceManager implements AccessibilityTrace {
    public static AccessibilityTraceManager sInstance;
    public final WindowManagerInternal.AccessibilityControllerInternal mA11yController;
    public final Object mA11yMSLock;
    public volatile long mEnabledLoggingFlags = 0;
    public final AccessibilityManagerService mService;

    public static AccessibilityTraceManager getInstance(WindowManagerInternal.AccessibilityControllerInternal accessibilityControllerInternal, AccessibilityManagerService accessibilityManagerService, Object obj) {
        if (sInstance == null) {
            sInstance = new AccessibilityTraceManager(accessibilityControllerInternal, accessibilityManagerService, obj);
        }
        return sInstance;
    }

    public AccessibilityTraceManager(WindowManagerInternal.AccessibilityControllerInternal accessibilityControllerInternal, AccessibilityManagerService accessibilityManagerService, Object obj) {
        this.mA11yController = accessibilityControllerInternal;
        this.mService = accessibilityManagerService;
        this.mA11yMSLock = obj;
    }

    public boolean isA11yTracingEnabled() {
        return this.mEnabledLoggingFlags != 0;
    }

    public boolean isA11yTracingEnabledForTypes(long j) {
        return (j & this.mEnabledLoggingFlags) != 0;
    }

    public int getTraceStateForAccessibilityManagerClientState() {
        int i = isA11yTracingEnabledForTypes(16L) ? 256 : 0;
        if (isA11yTracingEnabledForTypes(32L)) {
            i |= 512;
        }
        if (isA11yTracingEnabledForTypes(262144L)) {
            i |= 1024;
        }
        return isA11yTracingEnabledForTypes(16384L) ? i | IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES : i;
    }

    public void startTrace(long j) {
        if (j == 0) {
            return;
        }
        long j2 = this.mEnabledLoggingFlags;
        this.mEnabledLoggingFlags = j;
        if (needToNotifyClients(j2)) {
            synchronized (this.mA11yMSLock) {
                AccessibilityManagerService accessibilityManagerService = this.mService;
                accessibilityManagerService.scheduleUpdateClientsIfNeededLocked(accessibilityManagerService.getCurrentUserState());
            }
        }
        this.mA11yController.startTrace(j);
    }

    public void stopTrace() {
        boolean isA11yTracingEnabled = isA11yTracingEnabled();
        long j = this.mEnabledLoggingFlags;
        this.mEnabledLoggingFlags = 0L;
        if (needToNotifyClients(j)) {
            synchronized (this.mA11yMSLock) {
                AccessibilityManagerService accessibilityManagerService = this.mService;
                accessibilityManagerService.scheduleUpdateClientsIfNeededLocked(accessibilityManagerService.getCurrentUserState());
            }
        }
        if (isA11yTracingEnabled) {
            this.mA11yController.stopTrace();
        }
    }

    public void logTrace(String str, long j) {
        logTrace(str, j, "");
    }

    public void logTrace(String str, long j, String str2) {
        if (isA11yTracingEnabledForTypes(j)) {
            this.mA11yController.logTrace(str, j, str2, "".getBytes(), Binder.getCallingUid(), Thread.currentThread().getStackTrace(), new HashSet(Arrays.asList("logTrace")));
        }
    }

    public void logTrace(long j, String str, long j2, String str2, int i, long j3, int i2, StackTraceElement[] stackTraceElementArr, Set<String> set) {
        if (isA11yTracingEnabledForTypes(j2)) {
            this.mA11yController.logTrace(str, j2, str2, "".getBytes(), i2, stackTraceElementArr, j, i, j3, set == null ? new HashSet() : set);
        }
    }

    public final boolean needToNotifyClients(long j) {
        return (this.mEnabledLoggingFlags & 278576) != (j & 278576);
    }

    public int onShellCommand(String str, ShellCommand shellCommand) {
        str.hashCode();
        if (!str.equals("start-trace")) {
            if (str.equals("stop-trace")) {
                stopTrace();
                return 0;
            }
            return -1;
        }
        String nextOption = shellCommand.getNextOption();
        if (nextOption == null) {
            startTrace(-1L);
            return 0;
        }
        ArrayList arrayList = new ArrayList();
        while (nextOption != null) {
            if (nextOption.equals("-t")) {
                String nextArg = shellCommand.getNextArg();
                while (nextArg != null) {
                    arrayList.add(nextArg);
                    nextArg = shellCommand.getNextArg();
                }
                nextOption = shellCommand.getNextOption();
            } else {
                PrintWriter errPrintWriter = shellCommand.getErrPrintWriter();
                errPrintWriter.println("Error: option not recognized " + nextOption);
                stopTrace();
                return -1;
            }
        }
        startTrace(AccessibilityTrace.getLoggingFlagsFromNames(arrayList));
        return 0;
    }

    public void onHelp(PrintWriter printWriter) {
        printWriter.println("  start-trace [-t LOGGING_TYPE [LOGGING_TYPE...]]");
        printWriter.println("    Start the debug tracing. If no option is present, full trace will be");
        printWriter.println("    generated. Options are:");
        printWriter.println("      -t: Only generate tracing for the logging type(s) specified here.");
        printWriter.println("          LOGGING_TYPE can be any one of below:");
        printWriter.println("            IAccessibilityServiceConnection");
        printWriter.println("            IAccessibilityServiceClient");
        printWriter.println("            IAccessibilityManager");
        printWriter.println("            IAccessibilityManagerClient");
        printWriter.println("            IAccessibilityInteractionConnection");
        printWriter.println("            IAccessibilityInteractionConnectionCallback");
        printWriter.println("            IRemoteMagnificationAnimationCallback");
        printWriter.println("            IWindowMagnificationConnection");
        printWriter.println("            IWindowMagnificationConnectionCallback");
        printWriter.println("            WindowManagerInternal");
        printWriter.println("            WindowsForAccessibilityCallback");
        printWriter.println("            MagnificationCallbacks");
        printWriter.println("            InputFilter");
        printWriter.println("            Gesture");
        printWriter.println("            AccessibilityService");
        printWriter.println("            PMBroadcastReceiver");
        printWriter.println("            UserBroadcastReceiver");
        printWriter.println("            FingerprintGesture");
        printWriter.println("  stop-trace");
        printWriter.println("    Stop the debug tracing.");
    }
}
