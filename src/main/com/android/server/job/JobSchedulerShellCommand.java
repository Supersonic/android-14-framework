package com.android.server.job;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.content.pm.IPackageManager;
import android.net.util.NetworkConstants;
import android.os.Binder;
import android.os.UserHandle;
import com.android.modules.utils.BasicShellCommandHandler;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class JobSchedulerShellCommand extends BasicShellCommandHandler {
    public JobSchedulerService mInternal;
    public IPackageManager mPM = AppGlobals.getPackageManager();

    public JobSchedulerShellCommand(JobSchedulerService jobSchedulerService) {
        this.mInternal = jobSchedulerService;
    }

    public int onCommand(String str) {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String str2 = str != null ? str : "";
        try {
            switch (str2.hashCode()) {
                case -1894245460:
                    if (str2.equals("trigger-dock-state")) {
                        c = 18;
                        break;
                    }
                    c = 65535;
                    break;
                case -1845752298:
                    if (str2.equals("get-storage-seq")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case -1711668249:
                    if (str2.equals("get-estimated-upload-bytes")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case -1687551032:
                    if (str2.equals("get-battery-charging")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -1367724422:
                    if (str2.equals("cancel")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -1313911455:
                    if (str2.equals("timeout")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -1274672338:
                    if (str2.equals("get-estimated-download-bytes")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 113291:
                    if (str2.equals("run")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 3540994:
                    if (str2.equals("stop")) {
                        c = 17;
                        break;
                    }
                    c = 65535;
                    break;
                case 55361425:
                    if (str2.equals("get-storage-not-low")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case 200896764:
                    if (str2.equals("heartbeat")) {
                        c = 14;
                        break;
                    }
                    c = 65535;
                    break;
                case 298223069:
                    if (str2.equals("get-transferred-upload-bytes")) {
                        c = '\f';
                        break;
                    }
                    c = 65535;
                    break;
                case 703160488:
                    if (str2.equals("get-battery-seq")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 826231557:
                    if (str2.equals("reset-execution-quota")) {
                        c = 15;
                        break;
                    }
                    c = 65535;
                    break;
                case 859357184:
                    if (str2.equals("reset-schedule-quota")) {
                        c = 16;
                        break;
                    }
                    c = 65535;
                    break;
                case 1749711139:
                    if (str2.equals("get-battery-not-low")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 1790568356:
                    if (str2.equals("get-transferred-download-bytes")) {
                        c = 11;
                        break;
                    }
                    c = 65535;
                    break;
                case 1791471818:
                    if (str2.equals("get-job-state")) {
                        c = '\r';
                        break;
                    }
                    c = 65535;
                    break;
                case 1854493850:
                    if (str2.equals("monitor-battery")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    return runJob(outPrintWriter);
                case 1:
                    return timeout(outPrintWriter);
                case 2:
                    return cancelJob(outPrintWriter);
                case 3:
                    return monitorBattery(outPrintWriter);
                case 4:
                    return getBatterySeq(outPrintWriter);
                case 5:
                    return getBatteryCharging(outPrintWriter);
                case 6:
                    return getBatteryNotLow(outPrintWriter);
                case 7:
                    return getEstimatedNetworkBytes(outPrintWriter, 0);
                case '\b':
                    return getEstimatedNetworkBytes(outPrintWriter, 1);
                case '\t':
                    return getStorageSeq(outPrintWriter);
                case '\n':
                    return getStorageNotLow(outPrintWriter);
                case 11:
                    return getTransferredNetworkBytes(outPrintWriter, 0);
                case '\f':
                    return getTransferredNetworkBytes(outPrintWriter, 1);
                case '\r':
                    return getJobState(outPrintWriter);
                case 14:
                    return doHeartbeat(outPrintWriter);
                case 15:
                    return resetExecutionQuota(outPrintWriter);
                case 16:
                    return resetScheduleQuota(outPrintWriter);
                case 17:
                    return stop(outPrintWriter);
                case 18:
                    return triggerDockState(outPrintWriter);
                default:
                    return handleDefaultCommands(str);
            }
        } catch (Exception e) {
            outPrintWriter.println("Exception: " + e);
            return -1;
        }
    }

    public final void checkPermission(String str) throws Exception {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 0 || this.mPM.checkUidPermission("android.permission.CHANGE_APP_IDLE_STATE", callingUid) == 0) {
            return;
        }
        throw new SecurityException("Uid " + callingUid + " not permitted to " + str);
    }

    public final boolean printError(int i, String str, int i2, String str2, int i3) {
        switch (i) {
            case -1002:
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.print("Job ");
                errPrintWriter.print(i3);
                errPrintWriter.print(" in package ");
                errPrintWriter.print(str);
                if (str2 != null) {
                    errPrintWriter.print(" / namespace ");
                    errPrintWriter.print(str2);
                }
                errPrintWriter.print(" / user ");
                errPrintWriter.print(i2);
                errPrintWriter.println(" has functional constraints but --force not specified");
                return true;
            case -1001:
                PrintWriter errPrintWriter2 = getErrPrintWriter();
                errPrintWriter2.print("Could not find job ");
                errPrintWriter2.print(i3);
                errPrintWriter2.print(" in package ");
                errPrintWriter2.print(str);
                if (str2 != null) {
                    errPrintWriter2.print(" / namespace ");
                    errPrintWriter2.print(str2);
                }
                errPrintWriter2.print(" / user ");
                errPrintWriter2.println(i2);
                return true;
            case -1000:
                PrintWriter errPrintWriter3 = getErrPrintWriter();
                errPrintWriter3.print("Package not found: ");
                errPrintWriter3.print(str);
                errPrintWriter3.print(" / user ");
                errPrintWriter3.println(i2);
                return true;
            default:
                return false;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runJob(PrintWriter printWriter) throws Exception {
        char c;
        checkPermission("force scheduled jobs");
        String str = null;
        boolean z = false;
        int i = 0;
        boolean z2 = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                if (z2 && z) {
                    printWriter.println("Cannot specify both --force and --satisfied");
                    return -1;
                }
                String nextArgRequired = getNextArgRequired();
                int parseInt = Integer.parseInt(getNextArgRequired());
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    int executeRunCommand = this.mInternal.executeRunCommand(nextArgRequired, i, str, parseInt, z, z2);
                    if (printError(executeRunCommand, nextArgRequired, i, str, parseInt)) {
                        return executeRunCommand;
                    }
                    printWriter.print("Running job");
                    if (z2) {
                        printWriter.print(" [FORCED]");
                    }
                    printWriter.println();
                    return executeRunCommand;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            switch (nextOption.hashCode()) {
                case -1626076853:
                    if (nextOption.equals("--force")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -969907566:
                    if (nextOption.equals("--satisfied")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1497:
                    if (nextOption.equals("-f")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 1505:
                    if (nextOption.equals("-n")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 1510:
                    if (nextOption.equals("-s")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 1512:
                    if (nextOption.equals("-u")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 1333469547:
                    if (nextOption.equals("--user")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 1740612539:
                    if (nextOption.equals("--namespace")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                case 2:
                    z2 = true;
                    break;
                case 1:
                case 4:
                    z = true;
                    break;
                case 3:
                case 7:
                    str = getNextArgRequired();
                    break;
                case 5:
                case 6:
                    i = Integer.parseInt(getNextArgRequired());
                    break;
                default:
                    printWriter.println("Error: unknown option '" + nextOption + "'");
                    return -1;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0036, code lost:
        if (r2.equals("-u") == false) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int timeout(PrintWriter printWriter) throws Exception {
        checkPermission("force timeout jobs");
        String str = null;
        int i = -1;
        while (true) {
            String nextOption = getNextOption();
            char c = 1;
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case 1505:
                        if (nextOption.equals("-n")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512:
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1740612539:
                        if (nextOption.equals("--namespace")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                    case 3:
                        str = getNextArgRequired();
                        break;
                    case 1:
                    case 2:
                        i = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    default:
                        printWriter.println("Error: unknown option '" + nextOption + "'");
                        return -1;
                }
            } else {
                if (i == -2) {
                    i = ActivityManager.getCurrentUser();
                }
                int i2 = i;
                String nextArg = getNextArg();
                String nextArg2 = getNextArg();
                int parseInt = nextArg2 != null ? Integer.parseInt(nextArg2) : -1;
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return this.mInternal.executeStopCommand(printWriter, nextArg, i2, str, nextArg2 != null, parseInt, 3, 3);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0036, code lost:
        if (r1.equals("-u") == false) goto L7;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int cancelJob(PrintWriter printWriter) throws Exception {
        checkPermission("cancel jobs");
        String str = null;
        int i = 0;
        while (true) {
            String str2 = str;
            while (true) {
                String nextOption = getNextOption();
                char c = 1;
                if (nextOption == null) {
                    if (i < 0) {
                        printWriter.println("Error: must specify a concrete user ID");
                        return -1;
                    }
                    String nextArg = getNextArg();
                    String nextArg2 = getNextArg();
                    int parseInt = nextArg2 != null ? Integer.parseInt(nextArg2) : -1;
                    long clearCallingIdentity = Binder.clearCallingIdentity();
                    try {
                        return this.mInternal.executeCancelCommand(printWriter, nextArg, i, str2, nextArg2 != null, parseInt);
                    } finally {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                    }
                }
                switch (nextOption.hashCode()) {
                    case 1505:
                        if (nextOption.equals("-n")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512:
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1740612539:
                        if (nextOption.equals("--namespace")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                    case 3:
                        break;
                    case 1:
                    case 2:
                        i = UserHandle.parseUserArg(getNextArgRequired());
                    default:
                        printWriter.println("Error: unknown option '" + nextOption + "'");
                        return -1;
                }
            }
            str = getNextArgRequired();
        }
    }

    public final int monitorBattery(PrintWriter printWriter) throws Exception {
        checkPermission("change battery monitoring");
        String nextArgRequired = getNextArgRequired();
        boolean z = true;
        if (!"on".equals(nextArgRequired)) {
            if (!"off".equals(nextArgRequired)) {
                getErrPrintWriter().println("Error: unknown option " + nextArgRequired);
                return 1;
            }
            z = false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInternal.setMonitorBattery(z);
            if (z) {
                printWriter.println("Battery monitoring enabled");
            } else {
                printWriter.println("Battery monitoring disabled");
            }
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int getBatterySeq(PrintWriter printWriter) {
        printWriter.println(this.mInternal.getBatterySeq());
        return 0;
    }

    public final int getBatteryCharging(PrintWriter printWriter) {
        printWriter.println(this.mInternal.isBatteryCharging());
        return 0;
    }

    public final int getBatteryNotLow(PrintWriter printWriter) {
        printWriter.println(this.mInternal.isBatteryNotLow());
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int getEstimatedNetworkBytes(PrintWriter printWriter, int i) throws Exception {
        char c;
        checkPermission("get estimated bytes");
        String str = null;
        int i2 = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case 1505:
                        if (nextOption.equals("-n")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512:
                        if (nextOption.equals("-u")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1740612539:
                        if (nextOption.equals("--namespace")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                    case 3:
                        str = getNextArgRequired();
                        break;
                    case 1:
                    case 2:
                        i2 = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    default:
                        printWriter.println("Error: unknown option '" + nextOption + "'");
                        return -1;
                }
            } else {
                int currentUser = i2 == -2 ? ActivityManager.getCurrentUser() : i2;
                String nextArgRequired = getNextArgRequired();
                int parseInt = Integer.parseInt(getNextArgRequired());
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    int estimatedNetworkBytes = this.mInternal.getEstimatedNetworkBytes(printWriter, nextArgRequired, currentUser, str, parseInt, i);
                    printError(estimatedNetworkBytes, nextArgRequired, currentUser, str, parseInt);
                    return estimatedNetworkBytes;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    public final int getStorageSeq(PrintWriter printWriter) {
        printWriter.println(this.mInternal.getStorageSeq());
        return 0;
    }

    public final int getStorageNotLow(PrintWriter printWriter) {
        printWriter.println(this.mInternal.getStorageNotLow());
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int getTransferredNetworkBytes(PrintWriter printWriter, int i) throws Exception {
        char c;
        checkPermission("get transferred bytes");
        String str = null;
        int i2 = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case 1505:
                        if (nextOption.equals("-n")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512:
                        if (nextOption.equals("-u")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1740612539:
                        if (nextOption.equals("--namespace")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                    case 3:
                        str = getNextArgRequired();
                        break;
                    case 1:
                    case 2:
                        i2 = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    default:
                        printWriter.println("Error: unknown option '" + nextOption + "'");
                        return -1;
                }
            } else {
                int currentUser = i2 == -2 ? ActivityManager.getCurrentUser() : i2;
                String nextArgRequired = getNextArgRequired();
                int parseInt = Integer.parseInt(getNextArgRequired());
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    int transferredNetworkBytes = this.mInternal.getTransferredNetworkBytes(printWriter, nextArgRequired, currentUser, str, parseInt, i);
                    printError(transferredNetworkBytes, nextArgRequired, currentUser, str, parseInt);
                    return transferredNetworkBytes;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int getJobState(PrintWriter printWriter) throws Exception {
        char c;
        checkPermission("get job state");
        String str = null;
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case 1505:
                        if (nextOption.equals("-n")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512:
                        if (nextOption.equals("-u")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1740612539:
                        if (nextOption.equals("--namespace")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                    case 3:
                        str = getNextArgRequired();
                        break;
                    case 1:
                    case 2:
                        i = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    default:
                        printWriter.println("Error: unknown option '" + nextOption + "'");
                        return -1;
                }
            } else {
                int currentUser = i == -2 ? ActivityManager.getCurrentUser() : i;
                String nextArgRequired = getNextArgRequired();
                int parseInt = Integer.parseInt(getNextArgRequired());
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    int jobState = this.mInternal.getJobState(printWriter, nextArgRequired, currentUser, str, parseInt);
                    printError(jobState, nextArgRequired, currentUser, str, parseInt);
                    return jobState;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    public final int doHeartbeat(PrintWriter printWriter) throws Exception {
        checkPermission("manipulate scheduler heartbeat");
        printWriter.println("Heartbeat command is no longer supported");
        return -1;
    }

    public final int resetExecutionQuota(PrintWriter printWriter) throws Exception {
        checkPermission("reset execution quota");
        int i = 0;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("-u") || nextOption.equals("--user")) {
                    i = UserHandle.parseUserArg(getNextArgRequired());
                } else {
                    printWriter.println("Error: unknown option '" + nextOption + "'");
                    return -1;
                }
            } else {
                if (i == -2) {
                    i = ActivityManager.getCurrentUser();
                }
                String nextArgRequired = getNextArgRequired();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mInternal.resetExecutionQuota(nextArgRequired, i);
                    return 0;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    public final int resetScheduleQuota(PrintWriter printWriter) throws Exception {
        checkPermission("reset schedule quota");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInternal.resetScheduleQuota();
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x0067, code lost:
        if (r2.equals("-i") == false) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int stop(PrintWriter printWriter) throws Exception {
        checkPermission("stop jobs");
        int i = -1;
        String str = null;
        int i2 = 13;
        int i3 = -1;
        while (true) {
            String nextOption = getNextOption();
            char c = 1;
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case -1405909809:
                        if (nextOption.equals("--stop-reason")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case NetworkConstants.ETHER_MTU /* 1500 */:
                        break;
                    case 1505:
                        if (nextOption.equals("-n")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1510:
                        if (nextOption.equals("-s")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512:
                        if (nextOption.equals("-u")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 617801983:
                        if (nextOption.equals("--internal-stop-reason")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1740612539:
                        if (nextOption.equals("--namespace")) {
                            c = 7;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                    case 3:
                        i2 = Integer.parseInt(getNextArgRequired());
                        break;
                    case 1:
                    case 5:
                        i = Integer.parseInt(getNextArgRequired());
                        break;
                    case 2:
                    case 7:
                        str = getNextArgRequired();
                        break;
                    case 4:
                    case 6:
                        i3 = UserHandle.parseUserArg(getNextArgRequired());
                        break;
                    default:
                        printWriter.println("Error: unknown option '" + nextOption + "'");
                        return -1;
                }
            } else {
                if (i3 == -2) {
                    i3 = ActivityManager.getCurrentUser();
                }
                int i4 = i3;
                String nextArg = getNextArg();
                String nextArg2 = getNextArg();
                int parseInt = nextArg2 != null ? Integer.parseInt(nextArg2) : -1;
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return this.mInternal.executeStopCommand(printWriter, nextArg, i4, str, nextArg2 != null, parseInt, i2, i);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
    }

    public final int triggerDockState(PrintWriter printWriter) throws Exception {
        checkPermission("trigger wireless charging dock state");
        String nextArgRequired = getNextArgRequired();
        boolean z = true;
        if (!"idle".equals(nextArgRequired)) {
            if (!"active".equals(nextArgRequired)) {
                getErrPrintWriter().println("Error: unknown option " + nextArgRequired);
                return 1;
            }
            z = false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mInternal.triggerDockState(z);
            return 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Job scheduler (jobscheduler) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("  run [-f | --force] [-s | --satisfied] [-u | --user USER_ID] [-n | --namespace NAMESPACE] PACKAGE JOB_ID");
        outPrintWriter.println("    Trigger immediate execution of a specific scheduled job. For historical");
        outPrintWriter.println("    reasons, some constraints, such as battery, are ignored when this");
        outPrintWriter.println("    command is called. If you don't want any constraints to be ignored,");
        outPrintWriter.println("    include the -s flag.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -f or --force: run the job even if technical constraints such as");
        outPrintWriter.println("         connectivity are not currently met. This is incompatible with -f ");
        outPrintWriter.println("         and so an error will be reported if both are given.");
        outPrintWriter.println("      -n or --namespace: specify the namespace this job sits in; the default");
        outPrintWriter.println("         is null (no namespace).");
        outPrintWriter.println("      -s or --satisfied: run the job only if all constraints are met.");
        outPrintWriter.println("         This is incompatible with -f and so an error will be reported");
        outPrintWriter.println("         if both are given.");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         the primary or system user");
        outPrintWriter.println("  stop [-u | --user USER_ID] [-n | --namespace NAMESPACE] [-s | --stop-reason STOP_REASON] [-i | --internal-stop-reason STOP_REASON] [PACKAGE] [JOB_ID]");
        outPrintWriter.println("    Trigger immediate stop of currently executing jobs using the specified");
        outPrintWriter.println("    stop reasons.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         all users");
        outPrintWriter.println("      -n or --namespace: specify the namespace this job sits in; the default");
        outPrintWriter.println("         is null (no namespace).");
        outPrintWriter.println("      -s or --stop-reason: specify the stop reason given to the job.");
        outPrintWriter.println("         Valid values are those that can be returned from");
        outPrintWriter.println("         JobParameters.getStopReason().");
        outPrintWriter.println("          The default value is STOP_REASON_USER.");
        outPrintWriter.println("      -i or --internal-stop-reason: specify the internal stop reason.");
        outPrintWriter.println("         JobScheduler will use for internal processing.");
        outPrintWriter.println("         Valid values are those that can be returned from");
        outPrintWriter.println("         JobParameters.getInternalStopReason().");
        outPrintWriter.println("          The default value is INTERNAL_STOP_REASON_UNDEFINED.");
        outPrintWriter.println("  timeout [-u | --user USER_ID] [-n | --namespace NAMESPACE] [PACKAGE] [JOB_ID]");
        outPrintWriter.println("    Trigger immediate timeout of currently executing jobs, as if their");
        outPrintWriter.println("    execution timeout had expired.");
        outPrintWriter.println("    This is the equivalent of calling `stop -s 3 -i 3`.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         all users");
        outPrintWriter.println("      -n or --namespace: specify the namespace this job sits in; the default");
        outPrintWriter.println("         is null (no namespace).");
        outPrintWriter.println("  cancel [-u | --user USER_ID] [-n | --namespace NAMESPACE] PACKAGE [JOB_ID]");
        outPrintWriter.println("    Cancel a scheduled job.  If a job ID is not supplied, all jobs scheduled");
        outPrintWriter.println("    by that package will be canceled.  USE WITH CAUTION.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         the primary or system user");
        outPrintWriter.println("      -n or --namespace: specify the namespace this job sits in; the default");
        outPrintWriter.println("         is null (no namespace).");
        outPrintWriter.println("  heartbeat [num]");
        outPrintWriter.println("    No longer used.");
        outPrintWriter.println("  monitor-battery [on|off]");
        outPrintWriter.println("    Control monitoring of all battery changes.  Off by default.  Turning");
        outPrintWriter.println("    on makes get-battery-seq useful.");
        outPrintWriter.println("  get-battery-seq");
        outPrintWriter.println("    Return the last battery update sequence number that was received.");
        outPrintWriter.println("  get-battery-charging");
        outPrintWriter.println("    Return whether the battery is currently considered to be charging.");
        outPrintWriter.println("  get-battery-not-low");
        outPrintWriter.println("    Return whether the battery is currently considered to not be low.");
        outPrintWriter.println("  get-estimated-download-bytes [-u | --user USER_ID] [-n | --namespace NAMESPACE] PACKAGE JOB_ID");
        outPrintWriter.println("    Return the most recent estimated download bytes for the job.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         the primary or system user");
        outPrintWriter.println("  get-estimated-upload-bytes [-u | --user USER_ID] [-n | --namespace NAMESPACE] PACKAGE JOB_ID");
        outPrintWriter.println("    Return the most recent estimated upload bytes for the job.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         the primary or system user");
        outPrintWriter.println("  get-storage-seq");
        outPrintWriter.println("    Return the last storage update sequence number that was received.");
        outPrintWriter.println("  get-storage-not-low");
        outPrintWriter.println("    Return whether storage is currently considered to not be low.");
        outPrintWriter.println("  get-transferred-download-bytes [-u | --user USER_ID] [-n | --namespace NAMESPACE] PACKAGE JOB_ID");
        outPrintWriter.println("    Return the most recent transferred download bytes for the job.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         the primary or system user");
        outPrintWriter.println("  get-transferred-upload-bytes [-u | --user USER_ID] [-n | --namespace NAMESPACE] PACKAGE JOB_ID");
        outPrintWriter.println("    Return the most recent transferred upload bytes for the job.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         the primary or system user");
        outPrintWriter.println("  get-job-state [-u | --user USER_ID] [-n | --namespace NAMESPACE] PACKAGE JOB_ID");
        outPrintWriter.println("    Return the current state of a job, may be any combination of:");
        outPrintWriter.println("      pending: currently on the pending list, waiting to be active");
        outPrintWriter.println("      active: job is actively running");
        outPrintWriter.println("      user-stopped: job can't run because its user is stopped");
        outPrintWriter.println("      backing-up: job can't run because app is currently backing up its data");
        outPrintWriter.println("      no-component: job can't run because its component is not available");
        outPrintWriter.println("      ready: job is ready to run (all constraints satisfied or bypassed)");
        outPrintWriter.println("      waiting: if nothing else above is printed, job not ready to run");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's job is to be run; the default is");
        outPrintWriter.println("         the primary or system user");
        outPrintWriter.println("      -n or --namespace: specify the namespace this job sits in; the default");
        outPrintWriter.println("         is null (no namespace).");
        outPrintWriter.println("  trigger-dock-state [idle|active]");
        outPrintWriter.println("    Trigger wireless charging dock state.  Active by default.");
        outPrintWriter.println();
    }
}
