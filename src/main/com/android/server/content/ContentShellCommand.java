package com.android.server.content;

import android.content.IContentService;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class ContentShellCommand extends ShellCommand {
    public final IContentService mInterface;

    public ContentShellCommand(IContentService iContentService) {
        this.mInterface = iContentService;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0024 A[Catch: RemoteException -> 0x002e, TryCatch #0 {RemoteException -> 0x002e, blocks: (B:6:0x000c, B:14:0x0024, B:16:0x0029, B:9:0x0016), top: B:21:0x000c }] */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0029 A[Catch: RemoteException -> 0x002e, TRY_LEAVE, TryCatch #0 {RemoteException -> 0x002e, blocks: (B:6:0x000c, B:14:0x0024, B:16:0x0029, B:9:0x0016), top: B:21:0x000c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int onCommand(String str) {
        boolean z;
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            if (str.hashCode() == -796331115 && str.equals("reset-today-stats")) {
                z = false;
                if (z) {
                    return runResetTodayStats();
                }
                return handleDefaultCommands(str);
            }
            z = true;
            if (z) {
            }
        } catch (RemoteException e) {
            outPrintWriter.println("Remote exception: " + e);
            return -1;
        }
    }

    public final int runResetTodayStats() throws RemoteException {
        this.mInterface.resetTodayStats();
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Content service commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("");
        outPrintWriter.println("  reset-today-stats");
        outPrintWriter.println("    Reset 1-day sync stats.");
        outPrintWriter.println();
    }
}
