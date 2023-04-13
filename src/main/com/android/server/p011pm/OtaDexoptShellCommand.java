package com.android.server.p011pm;

import android.content.pm.IOtaDexopt;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;
import java.util.Locale;
/* renamed from: com.android.server.pm.OtaDexoptShellCommand */
/* loaded from: classes2.dex */
public class OtaDexoptShellCommand extends ShellCommand {
    public final IOtaDexopt mInterface;

    public OtaDexoptShellCommand(OtaDexoptService otaDexoptService) {
        this.mInterface = otaDexoptService;
    }

    public int onCommand(String str) {
        boolean z;
        if (str == null) {
            return handleDefaultCommands((String) null);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            switch (str.hashCode()) {
                case -1001078227:
                    if (str.equals("progress")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case -318370553:
                    if (str.equals("prepare")) {
                        z = false;
                        break;
                    }
                    z = true;
                    break;
                case 3089282:
                    if (str.equals("done")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 3377907:
                    if (str.equals("next")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 3540684:
                    if (str.equals("step")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 856774308:
                    if (str.equals("cleanup")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                default:
                    z = true;
                    break;
            }
            if (z) {
                if (!z) {
                    if (!z) {
                        if (!z) {
                            if (!z) {
                                if (z) {
                                    return runOtaProgress();
                                }
                                return handleDefaultCommands(str);
                            }
                            return runOtaNext();
                        }
                        return runOtaStep();
                    }
                    return runOtaDone();
                }
                return runOtaCleanup();
            }
            return runOtaPrepare();
        } catch (RemoteException e) {
            outPrintWriter.println("Remote exception: " + e);
            return -1;
        }
    }

    public final int runOtaPrepare() throws RemoteException {
        this.mInterface.prepare();
        getOutPrintWriter().println("Success");
        return 0;
    }

    public final int runOtaCleanup() throws RemoteException {
        this.mInterface.cleanup();
        return 0;
    }

    public final int runOtaDone() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        if (this.mInterface.isDone()) {
            outPrintWriter.println("OTA complete.");
            return 0;
        }
        outPrintWriter.println("OTA incomplete.");
        return 0;
    }

    public final int runOtaStep() throws RemoteException {
        this.mInterface.dexoptNextPackage();
        return 0;
    }

    public final int runOtaNext() throws RemoteException {
        getOutPrintWriter().println(this.mInterface.nextDexoptCommand());
        return 0;
    }

    public final int runOtaProgress() throws RemoteException {
        getOutPrintWriter().format(Locale.ROOT, "%.2f", Float.valueOf(this.mInterface.getProgress()));
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("OTA Dexopt (ota) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("");
        outPrintWriter.println("  prepare");
        outPrintWriter.println("    Prepare an OTA dexopt pass, collecting all packages.");
        outPrintWriter.println("  done");
        outPrintWriter.println("    Replies whether the OTA is complete or not.");
        outPrintWriter.println("  step");
        outPrintWriter.println("    OTA dexopt the next package.");
        outPrintWriter.println("  next");
        outPrintWriter.println("    Get parameters for OTA dexopt of the next package.");
        outPrintWriter.println("  cleanup");
        outPrintWriter.println("    Clean up internal states. Ends an OTA session.");
    }
}
