package com.android.server.net.watchlist;

import android.content.Context;
import android.os.Binder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.provider.Settings;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class NetworkWatchlistShellCommand extends ShellCommand {
    public final Context mContext;
    public final NetworkWatchlistService mService;

    public NetworkWatchlistShellCommand(NetworkWatchlistService networkWatchlistService, Context context) {
        this.mContext = context;
        this.mService = networkWatchlistService;
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0040 A[Catch: Exception -> 0x0045, TRY_LEAVE, TryCatch #0 {Exception -> 0x0045, blocks: (B:6:0x000c, B:20:0x0036, B:22:0x003b, B:24:0x0040, B:11:0x001c, B:14:0x0026), top: B:29:0x000c }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int onCommand(String str) {
        boolean z;
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            int hashCode = str.hashCode();
            if (hashCode != 1757613042) {
                if (hashCode == 1854202282 && str.equals("force-generate-report")) {
                    z = true;
                    if (z) {
                        return runSetTestConfig();
                    }
                    if (z) {
                        return runForceGenerateReport();
                    }
                    return handleDefaultCommands(str);
                }
                z = true;
                if (z) {
                }
            } else {
                if (str.equals("set-test-config")) {
                    z = false;
                    if (z) {
                    }
                }
                z = true;
                if (z) {
                }
            }
        } catch (Exception e) {
            outPrintWriter.println("Exception: " + e);
            return -1;
        }
    }

    public final int runSetTestConfig() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            String nextArgRequired = getNextArgRequired();
            ParcelFileDescriptor openFileForSystem = openFileForSystem(nextArgRequired, "r");
            if (openFileForSystem == null) {
                outPrintWriter.println("Error: can't open input file " + nextArgRequired);
                return -1;
            }
            ParcelFileDescriptor.AutoCloseInputStream autoCloseInputStream = new ParcelFileDescriptor.AutoCloseInputStream(openFileForSystem);
            WatchlistConfig.getInstance().setTestMode(autoCloseInputStream);
            autoCloseInputStream.close();
            outPrintWriter.println("Success!");
            return 0;
        } catch (Exception e) {
            outPrintWriter.println("Error: " + e.toString());
            return -1;
        }
    }

    public final int runForceGenerateReport() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (WatchlistConfig.getInstance().isConfigSecure()) {
                outPrintWriter.println("Error: Cannot force generate report under production config");
                return -1;
            }
            Settings.Global.putLong(this.mContext.getContentResolver(), "network_watchlist_last_report_time", 0L);
            this.mService.forceReportWatchlistForTest(System.currentTimeMillis());
            outPrintWriter.println("Success!");
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (Exception e) {
            outPrintWriter.println("Error: " + e);
            return -1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Network watchlist manager commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("  set-test-config your_watchlist_config.xml");
        outPrintWriter.println("    Set network watchlist test config file.");
        outPrintWriter.println("  force-generate-report");
        outPrintWriter.println("    Force generate watchlist test report.");
    }
}
