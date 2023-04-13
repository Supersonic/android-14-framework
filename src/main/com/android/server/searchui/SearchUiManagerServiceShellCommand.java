package com.android.server.searchui;

import android.os.ShellCommand;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class SearchUiManagerServiceShellCommand extends ShellCommand {
    public static final String TAG = SearchUiManagerServiceShellCommand.class.getSimpleName();
    public final SearchUiManagerService mService;

    public SearchUiManagerServiceShellCommand(SearchUiManagerService searchUiManagerService) {
        this.mService = searchUiManagerService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        if (str.equals("set")) {
            String nextArgRequired = getNextArgRequired();
            nextArgRequired.hashCode();
            if (nextArgRequired.equals("temporary-service")) {
                int parseInt = Integer.parseInt(getNextArgRequired());
                String nextArg = getNextArg();
                if (nextArg == null) {
                    this.mService.resetTemporaryService(parseInt);
                    outPrintWriter.println("SearchUiService temporarily reset. ");
                    return 0;
                }
                int parseInt2 = Integer.parseInt(getNextArgRequired());
                this.mService.setTemporaryService(parseInt, nextArg, parseInt2);
                outPrintWriter.println("SearchUiService temporarily set to " + nextArg + " for " + parseInt2 + "ms");
            }
            return 0;
        }
        return handleDefaultCommands(str);
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            outPrintWriter.println("SearchUiManagerService commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Prints this help text.");
            outPrintWriter.println("");
            outPrintWriter.println("  set temporary-service USER_ID [COMPONENT_NAME DURATION]");
            outPrintWriter.println("    Temporarily (for DURATION ms) changes the service implemtation.");
            outPrintWriter.println("    To reset, call with just the USER_ID argument.");
            outPrintWriter.println("");
            outPrintWriter.close();
        } catch (Throwable th) {
            if (outPrintWriter != null) {
                try {
                    outPrintWriter.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }
}
