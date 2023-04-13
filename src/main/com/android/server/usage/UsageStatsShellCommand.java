package com.android.server.usage;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.os.ShellCommand;
import android.os.UserHandle;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class UsageStatsShellCommand extends ShellCommand {
    public final UsageStatsService mService;

    public UsageStatsShellCommand(UsageStatsService usageStatsService) {
        this.mService = usageStatsService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands((String) null);
        }
        if (str.equals("clear-last-used-timestamps")) {
            return runClearLastUsedTimestamps();
        }
        return handleDefaultCommands(str);
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("UsageStats service (usagestats) commands:");
        outPrintWriter.println("help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println();
        outPrintWriter.println("clear-last-used-timestamps PACKAGE_NAME [-u | --user USER_ID]");
        outPrintWriter.println("    Clears any existing usage data for the given package.");
        outPrintWriter.println();
    }

    @SuppressLint({"AndroidFrameworkRequiresPermission"})
    public final int runClearLastUsedTimestamps() {
        String nextArgRequired = getNextArgRequired();
        int i = -2;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if ("-u".equals(nextOption) || "--user".equals(nextOption)) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    getErrPrintWriter().println("Error: unknown option: " + nextOption);
                    return -1;
                }
            } else {
                if (i == -2) {
                    i = ActivityManager.getCurrentUser();
                }
                this.mService.clearLastUsedTimestamps(nextArgRequired, i);
                return 0;
            }
        }
    }
}
