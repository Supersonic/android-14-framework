package com.android.server.timedetector;

import android.os.ShellCommand;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes2.dex */
public class GnssTimeUpdateServiceShellCommand extends ShellCommand {
    public final GnssTimeUpdateService mGnssTimeUpdateService;

    public GnssTimeUpdateServiceShellCommand(GnssTimeUpdateService gnssTimeUpdateService) {
        Objects.requireNonNull(gnssTimeUpdateService);
        this.mGnssTimeUpdateService = gnssTimeUpdateService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        if (str.equals("start_gnss_listening")) {
            return runStartGnssListening();
        }
        return handleDefaultCommands(str);
    }

    public final int runStartGnssListening() {
        getOutPrintWriter().println(this.mGnssTimeUpdateService.startGnssListening());
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.printf("Network Time Update Service (%s) commands:\n", "gnss_time_update_service");
        outPrintWriter.printf("  help\n", new Object[0]);
        outPrintWriter.printf("    Print this help text.\n", new Object[0]);
        outPrintWriter.printf("  %s\n", "start_gnss_listening");
        outPrintWriter.printf("    Forces the service in to GNSS listening mode (if it isn't already).\n", new Object[0]);
        outPrintWriter.printf("    Prints true if the service is listening after this command.\n", new Object[0]);
        outPrintWriter.println();
    }
}
