package com.android.server.print;

import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.UserHandle;
import android.print.IPrintManager;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public final class PrintShellCommand extends ShellCommand {
    public final IPrintManager mService;

    public PrintShellCommand(IPrintManager iPrintManager) {
        this.mService = iPrintManager;
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
        try {
            getOutPrintWriter().println(Boolean.toString(this.mService.getBindInstantServiceAllowed(parseUserId.intValue())));
            return 0;
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return 0;
        }
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
        try {
            this.mService.setBindInstantServiceAllowed(parseUserId.intValue(), Boolean.parseBoolean(nextArgRequired));
            return 0;
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return 0;
        }
    }

    public final Integer parseUserId() {
        String nextOption = getNextOption();
        if (nextOption != null) {
            if (nextOption.equals("--user")) {
                return Integer.valueOf(UserHandle.parseUserArg(getNextArgRequired()));
            }
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Unknown option: " + nextOption);
            return null;
        }
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Print service commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("  set-bind-instant-service-allowed [--user <USER_ID>] true|false ");
        outPrintWriter.println("    Set whether binding to print services provided by instant apps is allowed.");
        outPrintWriter.println("  get-bind-instant-service-allowed [--user <USER_ID>]");
        outPrintWriter.println("    Get whether binding to print services provided by instant apps is allowed.");
    }
}
