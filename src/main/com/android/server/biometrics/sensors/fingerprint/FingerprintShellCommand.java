package com.android.server.biometrics.sensors.fingerprint;

import android.content.Context;
import android.os.ShellCommand;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class FingerprintShellCommand extends ShellCommand {
    public final Context mContext;
    public final FingerprintService mService;

    public FingerprintShellCommand(Context context, FingerprintService fingerprintService) {
        this.mContext = context;
        this.mService = fingerprintService;
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x002f  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x004f A[Catch: Exception -> 0x0054, TRY_LEAVE, TryCatch #0 {Exception -> 0x0054, blocks: (B:7:0x0008, B:21:0x0031, B:22:0x004a, B:24:0x004f, B:12:0x0017, B:15:0x0022), top: B:29:0x0008 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int onCommand(String str) {
        int hashCode;
        boolean z;
        if (str == null) {
            onHelp();
            return 1;
        }
        try {
            hashCode = str.hashCode();
        } catch (Exception e) {
            getOutPrintWriter().println("Exception: " + e);
        }
        if (hashCode != 3198785) {
            if (hashCode == 3545755 && str.equals("sync")) {
                z = true;
                if (z) {
                    return doHelp();
                }
                if (z) {
                    return doSync();
                }
                getOutPrintWriter().println("Unrecognized command: " + str);
                return -1;
            }
            z = true;
            if (z) {
            }
        } else {
            if (str.equals("help")) {
                z = false;
                if (z) {
                }
            }
            z = true;
            if (z) {
            }
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Fingerprint Service commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("      Print this help text.");
        outPrintWriter.println("  sync");
        outPrintWriter.println("      Sync enrollments now (virtualized sensors only).");
    }

    public final int doHelp() {
        onHelp();
        return 0;
    }

    public final int doSync() {
        this.mService.syncEnrollmentsNow();
        return 0;
    }
}
