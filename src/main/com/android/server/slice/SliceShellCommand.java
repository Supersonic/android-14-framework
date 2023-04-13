package com.android.server.slice;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.ShellCommand;
import android.util.ArraySet;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class SliceShellCommand extends ShellCommand {
    public final SliceManagerService mService;

    public SliceShellCommand(SliceManagerService sliceManagerService) {
        this.mService = sliceManagerService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        if (str.equals("get-permissions")) {
            return runGetPermissions(getNextArgRequired());
        }
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Status bar commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("");
        outPrintWriter.println("  get-permissions <authority>");
        outPrintWriter.println("    List the pkgs that have permission to an authority.");
        outPrintWriter.println("");
    }

    public final int runGetPermissions(String str) {
        String[] allPackagesGranted;
        if (Binder.getCallingUid() != 2000 && Binder.getCallingUid() != 0) {
            getOutPrintWriter().println("Only shell can get permissions");
            return -1;
        }
        Context context = this.mService.getContext();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Uri build = new Uri.Builder().scheme("content").authority(str).build();
            if (!"vnd.android.slice".equals(context.getContentResolver().getType(build))) {
                getOutPrintWriter().println(str + " is not a slice provider");
                return -1;
            }
            Bundle call = context.getContentResolver().call(build, "get_permissions", (String) null, (Bundle) null);
            if (call == null) {
                getOutPrintWriter().println("An error occurred getting permissions");
                return -1;
            }
            String[] stringArray = call.getStringArray("result");
            PrintWriter outPrintWriter = getOutPrintWriter();
            ArraySet arraySet = new ArraySet();
            if (stringArray != null && stringArray.length != 0) {
                for (PackageInfo packageInfo : context.getPackageManager().getPackagesHoldingPermissions(stringArray, 0)) {
                    outPrintWriter.println(packageInfo.packageName);
                    arraySet.add(packageInfo.packageName);
                }
            }
            for (String str2 : this.mService.getAllPackagesGranted(str)) {
                if (!arraySet.contains(str2)) {
                    outPrintWriter.println(str2);
                    arraySet.add(str2);
                }
            }
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
