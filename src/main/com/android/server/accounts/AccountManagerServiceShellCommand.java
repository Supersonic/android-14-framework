package com.android.server.accounts;

import android.app.ActivityManager;
import android.os.ShellCommand;
import android.os.UserHandle;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class AccountManagerServiceShellCommand extends ShellCommand {
    public final AccountManagerService mService;

    public AccountManagerServiceShellCommand(AccountManagerService accountManagerService) {
        this.mService = accountManagerService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        if (str.equals("get-bind-instant-service-allowed")) {
            return runGetBindInstantServiceAllowed();
        }
        if (str.equals("set-bind-instant-service-allowed")) {
            return runSetBindInstantServiceAllowed();
        }
        return -1;
    }

    public final int runGetBindInstantServiceAllowed() {
        Integer parseUserId = parseUserId();
        if (parseUserId == null) {
            return -1;
        }
        getOutPrintWriter().println(Boolean.toString(this.mService.getBindInstantServiceAllowed(parseUserId.intValue())));
        return 0;
    }

    public final int runSetBindInstantServiceAllowed() {
        Integer parseUserId = parseUserId();
        if (parseUserId == null) {
            return -1;
        }
        String nextArgRequired = getNextArgRequired();
        if (nextArgRequired == null) {
            getErrPrintWriter().println("Error: no true/false specified");
            return -1;
        }
        this.mService.setBindInstantServiceAllowed(parseUserId.intValue(), Boolean.parseBoolean(nextArgRequired));
        return 0;
    }

    public final Integer parseUserId() {
        String nextOption = getNextOption();
        if (nextOption != null) {
            if (nextOption.equals("--user")) {
                int parseUserArg = UserHandle.parseUserArg(getNextArgRequired());
                if (parseUserArg == -2) {
                    return Integer.valueOf(ActivityManager.getCurrentUser());
                }
                if (parseUserArg == -1) {
                    getErrPrintWriter().println("USER_ALL not supported. Specify a user.");
                    return null;
                } else if (parseUserArg < 0) {
                    PrintWriter errPrintWriter = getErrPrintWriter();
                    errPrintWriter.println("Invalid user: " + parseUserArg);
                    return null;
                } else {
                    return Integer.valueOf(parseUserArg);
                }
            }
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Unknown option: " + nextOption);
            return null;
        }
        return Integer.valueOf(ActivityManager.getCurrentUser());
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Account manager service commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("  set-bind-instant-service-allowed [--user <USER_ID> (current user if not specified)] true|false ");
        outPrintWriter.println("    Set whether binding to services provided by instant apps is allowed.");
        outPrintWriter.println("  get-bind-instant-service-allowed [--user <USER_ID> (current user if not specified)]");
        outPrintWriter.println("    Get whether binding to services provided by instant apps is allowed.");
    }
}
