package com.android.server.apphibernation;

import android.os.ShellCommand;
import android.os.UserHandle;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class AppHibernationShellCommand extends ShellCommand {
    public final AppHibernationService mService;

    public AppHibernationShellCommand(AppHibernationService appHibernationService) {
        this.mService = appHibernationService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        if (str.equals("set-state")) {
            return runSetState();
        }
        if (str.equals("get-state")) {
            return runGetState();
        }
        return handleDefaultCommands(str);
    }

    public final int runSetState() {
        int i = -2;
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                break;
            } else if (nextOption.equals("--global")) {
                z = true;
            } else if (nextOption.equals("--user")) {
                i = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                getErrPrintWriter().println("Error: Unknown option: " + nextOption);
            }
        }
        String nextArgRequired = getNextArgRequired();
        if (nextArgRequired == null) {
            getErrPrintWriter().println("Error: no package specified");
            return -1;
        }
        String nextArgRequired2 = getNextArgRequired();
        if (nextArgRequired2 == null) {
            getErrPrintWriter().println("Error: No state to set specified");
            return -1;
        }
        boolean parseBoolean = Boolean.parseBoolean(nextArgRequired2);
        if (z) {
            this.mService.setHibernatingGlobally(nextArgRequired, parseBoolean);
        } else {
            this.mService.setHibernatingForUser(nextArgRequired, i, parseBoolean);
        }
        return 0;
    }

    public final int runGetState() {
        int i = -2;
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                break;
            } else if (nextOption.equals("--global")) {
                z = true;
            } else if (nextOption.equals("--user")) {
                i = UserHandle.parseUserArg(getNextArgRequired());
            } else {
                getErrPrintWriter().println("Error: Unknown option: " + nextOption);
            }
        }
        String nextArgRequired = getNextArgRequired();
        if (nextArgRequired == null) {
            getErrPrintWriter().println("Error: No package specified");
            return -1;
        }
        getOutPrintWriter().println(z ? this.mService.isHibernatingGlobally(nextArgRequired) : this.mService.isHibernatingForUser(nextArgRequired, i));
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("App hibernation (app_hibernation) commands: ");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("");
        outPrintWriter.println("  set-state [--user USER_ID] [--global] PACKAGE true|false");
        outPrintWriter.println("    Sets the hibernation state of the package to value specified. Optionally");
        outPrintWriter.println("    may specify a user id or set global hibernation state.");
        outPrintWriter.println("");
        outPrintWriter.println("  get-state [--user USER_ID] [--global] PACKAGE");
        outPrintWriter.println("    Gets the hibernation state of the package. Optionally may specify a user");
        outPrintWriter.println("    id or request global hibernation state.");
        outPrintWriter.println("");
    }
}
