package com.android.server.contentsuggestions;

import android.os.ShellCommand;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class ContentSuggestionsManagerServiceShellCommand extends ShellCommand {
    public static final String TAG = ContentSuggestionsManagerServiceShellCommand.class.getSimpleName();
    public final ContentSuggestionsManagerService mService;

    public ContentSuggestionsManagerServiceShellCommand(ContentSuggestionsManagerService contentSuggestionsManagerService) {
        this.mService = contentSuggestionsManagerService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        if (str.equals("get")) {
            return requestGet(outPrintWriter);
        }
        if (str.equals("set")) {
            return requestSet(outPrintWriter);
        }
        return handleDefaultCommands(str);
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            outPrintWriter.println("ContentSuggestionsManagerService commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Prints this help text.");
            outPrintWriter.println("");
            outPrintWriter.println("  set temporary-service USER_ID [COMPONENT_NAME DURATION]");
            outPrintWriter.println("    Temporarily (for DURATION ms) changes the service implementation.");
            outPrintWriter.println("    To reset, call with just the USER_ID argument.");
            outPrintWriter.println("");
            outPrintWriter.println("  set default-service-enabled USER_ID [true|false]");
            outPrintWriter.println("    Enable / disable the default service for the user.");
            outPrintWriter.println("");
            outPrintWriter.println("  get default-service-enabled USER_ID");
            outPrintWriter.println("    Checks whether the default service is enabled for the user.");
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

    public final int requestSet(PrintWriter printWriter) {
        String nextArgRequired = getNextArgRequired();
        nextArgRequired.hashCode();
        if (nextArgRequired.equals("default-service-enabled")) {
            return setDefaultServiceEnabled();
        }
        if (nextArgRequired.equals("temporary-service")) {
            return setTemporaryService(printWriter);
        }
        printWriter.println("Invalid set: " + nextArgRequired);
        return -1;
    }

    public final int requestGet(PrintWriter printWriter) {
        String nextArgRequired = getNextArgRequired();
        nextArgRequired.hashCode();
        if (nextArgRequired.equals("default-service-enabled")) {
            return getDefaultServiceEnabled(printWriter);
        }
        printWriter.println("Invalid get: " + nextArgRequired);
        return -1;
    }

    public final int setTemporaryService(PrintWriter printWriter) {
        int parseInt = Integer.parseInt(getNextArgRequired());
        String nextArg = getNextArg();
        if (nextArg == null) {
            this.mService.resetTemporaryService(parseInt);
            return 0;
        }
        int parseInt2 = Integer.parseInt(getNextArgRequired());
        this.mService.setTemporaryService(parseInt, nextArg, parseInt2);
        printWriter.println("ContentSuggestionsService temporarily set to " + nextArg + " for " + parseInt2 + "ms");
        return 0;
    }

    public final int setDefaultServiceEnabled() {
        this.mService.setDefaultServiceEnabled(getNextIntArgRequired(), Boolean.parseBoolean(getNextArg()));
        return 0;
    }

    public final int getDefaultServiceEnabled(PrintWriter printWriter) {
        printWriter.println(this.mService.isDefaultServiceEnabled(getNextIntArgRequired()));
        return 0;
    }

    public final int getNextIntArgRequired() {
        return Integer.parseInt(getNextArgRequired());
    }
}
