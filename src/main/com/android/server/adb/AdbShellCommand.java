package com.android.server.adb;

import com.android.modules.utils.BasicShellCommandHandler;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes.dex */
public class AdbShellCommand extends BasicShellCommandHandler {
    public final AdbService mService;

    public AdbShellCommand(AdbService adbService) {
        Objects.requireNonNull(adbService);
        this.mService = adbService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands((String) null);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        if (str.equals("is-wifi-qr-supported")) {
            outPrintWriter.println(Boolean.toString(this.mService.isAdbWifiQrSupported()));
            return 0;
        } else if (str.equals("is-wifi-supported")) {
            outPrintWriter.println(Boolean.toString(this.mService.isAdbWifiSupported()));
            return 0;
        } else {
            return handleDefaultCommands(str);
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Adb service commands:");
        outPrintWriter.println("  help or -h");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("  is-wifi-supported");
        outPrintWriter.println("    Returns \"true\" if adb over wifi is supported.");
        outPrintWriter.println("  is-wifi-qr-supported");
        outPrintWriter.println("    Returns \"true\" if adb over wifi + QR pairing is supported.");
        outPrintWriter.println();
    }
}
