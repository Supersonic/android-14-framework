package com.android.server.coverage;

import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import org.jacoco.agent.rt.RT;
/* loaded from: classes.dex */
public class CoverageService extends Binder {
    public static final boolean ENABLED;

    static {
        boolean z;
        try {
            Class.forName("org.jacoco.agent.rt.RT");
            z = true;
        } catch (ClassNotFoundException unused) {
            z = false;
        }
        ENABLED = z;
    }

    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new CoverageCommand().exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    /* loaded from: classes.dex */
    public static class CoverageCommand extends ShellCommand {
        public CoverageCommand() {
        }

        public int onCommand(String str) {
            if ("dump".equals(str)) {
                return onDump();
            }
            if ("reset".equals(str)) {
                return onReset();
            }
            return handleDefaultCommands(str);
        }

        public void onHelp() {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Coverage commands:");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Print this help text.");
            outPrintWriter.println("  dump [FILE]");
            outPrintWriter.println("    Dump code coverage to FILE.");
            outPrintWriter.println("  reset");
            outPrintWriter.println("    Reset coverage information.");
        }

        public final int onDump() {
            String nextArg = getNextArg();
            if (nextArg == null) {
                nextArg = "/data/local/tmp/coverage.ec";
            } else {
                File file = new File(nextArg);
                if (file.isDirectory()) {
                    nextArg = new File(file, "coverage.ec").getAbsolutePath();
                }
            }
            ParcelFileDescriptor openFileForSystem = openFileForSystem(nextArg, "w");
            if (openFileForSystem == null) {
                return -1;
            }
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new ParcelFileDescriptor.AutoCloseOutputStream(openFileForSystem));
                bufferedOutputStream.write(RT.getAgent().getExecutionData(false));
                bufferedOutputStream.flush();
                getOutPrintWriter().println(String.format("Dumped coverage data to %s", nextArg));
                bufferedOutputStream.close();
                return 0;
            } catch (IOException e) {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Failed to dump coverage data: " + e.getMessage());
                return -1;
            }
        }

        public final int onReset() {
            RT.getAgent().reset();
            getOutPrintWriter().println("Reset coverage data");
            return 0;
        }
    }
}
