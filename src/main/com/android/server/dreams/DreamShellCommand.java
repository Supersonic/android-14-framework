package com.android.server.dreams;

import android.os.Binder;
import android.os.ShellCommand;
import android.util.Slog;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class DreamShellCommand extends ShellCommand {
    public final DreamManagerService mService;

    public DreamShellCommand(DreamManagerService dreamManagerService) {
        this.mService = dreamManagerService;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0050 A[Catch: SecurityException -> 0x0058, TRY_LEAVE, TryCatch #0 {SecurityException -> 0x0058, blocks: (B:3:0x0018, B:17:0x0043, B:19:0x0048, B:21:0x0050, B:8:0x0028, B:11:0x0033), top: B:26:0x0018 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int onCommand(String str) {
        boolean z;
        Slog.d("DreamShellCommand", "onCommand:" + str);
        try {
            int hashCode = str.hashCode();
            if (hashCode != -183711126) {
                if (hashCode == 1473640970 && str.equals("start-dreaming")) {
                    z = false;
                    if (z) {
                        enforceCallerIsRoot();
                        return startDreaming();
                    } else if (z) {
                        enforceCallerIsRoot();
                        return stopDreaming();
                    } else {
                        return super.handleDefaultCommands(str);
                    }
                }
                z = true;
                if (z) {
                }
            } else {
                if (str.equals("stop-dreaming")) {
                    z = true;
                    if (z) {
                    }
                }
                z = true;
                if (z) {
                }
            }
        } catch (SecurityException e) {
            getOutPrintWriter().println(e);
            return -1;
        }
    }

    public final int startDreaming() {
        this.mService.requestStartDreamFromShell();
        return 0;
    }

    public final int stopDreaming() {
        this.mService.requestStopDreamFromShell();
        return 0;
    }

    public final void enforceCallerIsRoot() {
        if (Binder.getCallingUid() != 0) {
            throw new SecurityException("Must be root to call Dream shell commands");
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Dream manager (dreams) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("      Print this help text.");
        outPrintWriter.println("  start-dreaming");
        outPrintWriter.println("      Start the currently configured dream.");
        outPrintWriter.println("  stop-dreaming");
        outPrintWriter.println("      Stops any active dream");
    }
}
