package com.android.server.statusbar;

import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.service.quicksettings.TileService;
import android.util.Pair;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class StatusBarShellCommand extends ShellCommand {
    public static final IBinder sToken = new StatusBarShellCommandToken();
    public final Context mContext;
    public final StatusBarManagerService mInterface;

    public StatusBarShellCommand(StatusBarManagerService statusBarManagerService, Context context) {
        this.mInterface = statusBarManagerService;
        this.mContext = context;
    }

    public int onCommand(String str) {
        char c = 1;
        if (str == null) {
            onHelp();
            return 1;
        }
        try {
            switch (str.hashCode()) {
                case -1282000806:
                    if (str.equals("add-tile")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -1239176554:
                    if (str.equals("get-status-icons")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case -1067396926:
                    if (str.equals("tracing")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case -1052548778:
                    if (str.equals("send-disable-flag")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case -919868578:
                    if (str.equals("run-gc")) {
                        c = 11;
                        break;
                    }
                    c = 65535;
                    break;
                case -823073837:
                    if (str.equals("click-tile")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -632085587:
                    if (str.equals("collapse")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -339726761:
                    if (str.equals("remove-tile")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 1499:
                    if (str.equals("-h")) {
                        c = '\f';
                        break;
                    }
                    c = 65535;
                    break;
                case 3095028:
                    if (str.equals("dump")) {
                        c = 14;
                        break;
                    }
                    c = 65535;
                    break;
                case 3198785:
                    if (str.equals("help")) {
                        c = '\r';
                        break;
                    }
                    c = 65535;
                    break;
                case 901899220:
                    if (str.equals("disable-for-setup")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case 1612300298:
                    if (str.equals("check-support")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 1629310709:
                    if (str.equals("expand-notifications")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 1672031734:
                    if (str.equals("expand-settings")) {
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
                    return runExpandNotifications();
                case 1:
                    return runExpandSettings();
                case 2:
                    return runCollapse();
                case 3:
                    return runAddTile();
                case 4:
                    return runRemoveTile();
                case 5:
                    return runClickTile();
                case 6:
                    getOutPrintWriter().println(String.valueOf(TileService.isQuickSettingsSupported()));
                    return 0;
                case 7:
                    return runGetStatusIcons();
                case '\b':
                    return runDisableForSetup();
                case '\t':
                    return runSendDisableFlag();
                case '\n':
                    return runTracing();
                case 11:
                    return runGc();
                case '\f':
                case '\r':
                    onHelp();
                    return 0;
                case 14:
                    return super.handleDefaultCommands(str);
                default:
                    return runPassArgsToStatusBar();
            }
        } catch (RemoteException e) {
            getOutPrintWriter().println("Remote exception: " + e);
            return -1;
        }
    }

    public final int runAddTile() throws RemoteException {
        this.mInterface.addTile(ComponentName.unflattenFromString(getNextArgRequired()));
        return 0;
    }

    public final int runRemoveTile() throws RemoteException {
        this.mInterface.remTile(ComponentName.unflattenFromString(getNextArgRequired()));
        return 0;
    }

    public final int runClickTile() throws RemoteException {
        this.mInterface.clickTile(ComponentName.unflattenFromString(getNextArgRequired()));
        return 0;
    }

    public final int runCollapse() throws RemoteException {
        this.mInterface.collapsePanels();
        return 0;
    }

    public final int runExpandSettings() throws RemoteException {
        this.mInterface.expandSettingsPanel(null);
        return 0;
    }

    public final int runExpandNotifications() throws RemoteException {
        this.mInterface.expandNotificationsPanel();
        return 0;
    }

    public final int runGetStatusIcons() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        for (String str : this.mInterface.getStatusBarIcons()) {
            outPrintWriter.println(str);
        }
        return 0;
    }

    public final int runDisableForSetup() {
        String nextArgRequired = getNextArgRequired();
        String packageName = this.mContext.getPackageName();
        if (Boolean.parseBoolean(nextArgRequired)) {
            StatusBarManagerService statusBarManagerService = this.mInterface;
            IBinder iBinder = sToken;
            statusBarManagerService.disable(61145088, iBinder, packageName);
            this.mInterface.disable2(0, iBinder, packageName);
        } else {
            StatusBarManagerService statusBarManagerService2 = this.mInterface;
            IBinder iBinder2 = sToken;
            statusBarManagerService2.disable(0, iBinder2, packageName);
            this.mInterface.disable2(0, iBinder2, packageName);
        }
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0075, code lost:
        if (r2.equals("system-icons") == false) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int runSendDisableFlag() {
        String packageName = this.mContext.getPackageName();
        StatusBarManager.DisableInfo disableInfo = new StatusBarManager.DisableInfo();
        String nextArg = getNextArg();
        while (true) {
            char c = 0;
            if (nextArg != null) {
                switch (nextArg.hashCode()) {
                    case -1786496516:
                        break;
                    case -906336856:
                        if (nextArg.equals("search")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case -755976775:
                        if (nextArg.equals("notification-alerts")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 3208415:
                        if (nextArg.equals("home")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 94755854:
                        if (nextArg.equals("clock")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1011652819:
                        if (nextArg.equals("statusbar-expansion")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1082295672:
                        if (nextArg.equals("recents")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1368216504:
                        if (nextArg.equals("notification-icons")) {
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
                        disableInfo.setSystemIconsDisabled(true);
                        break;
                    case 1:
                        disableInfo.setSearchDisabled(true);
                        break;
                    case 2:
                        disableInfo.setNotificationPeekingDisabled(true);
                        break;
                    case 3:
                        disableInfo.setNagivationHomeDisabled(true);
                        break;
                    case 4:
                        disableInfo.setClockDisabled(true);
                        break;
                    case 5:
                        disableInfo.setStatusBarExpansionDisabled(true);
                        break;
                    case 6:
                        disableInfo.setRecentsDisabled(true);
                        break;
                    case 7:
                        disableInfo.setNotificationIconsDisabled(true);
                        break;
                }
                nextArg = getNextArg();
            } else {
                Pair flags = disableInfo.toFlags();
                StatusBarManagerService statusBarManagerService = this.mInterface;
                int intValue = ((Integer) flags.first).intValue();
                IBinder iBinder = sToken;
                statusBarManagerService.disable(intValue, iBinder, packageName);
                this.mInterface.disable2(((Integer) flags.second).intValue(), iBinder, packageName);
                return 0;
            }
        }
    }

    public final int runPassArgsToStatusBar() {
        this.mInterface.passThroughShellCommand(getAllArgs(), getOutFileDescriptor());
        return 0;
    }

    public final int runTracing() {
        String nextArg = getNextArg();
        nextArg.hashCode();
        if (nextArg.equals("stop")) {
            this.mInterface.stopTracing();
            return 0;
        } else if (nextArg.equals("start")) {
            this.mInterface.startTracing();
            return 0;
        } else {
            return 0;
        }
    }

    public final int runGc() {
        this.mInterface.runGcForTest();
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Status bar commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("");
        outPrintWriter.println("  expand-notifications");
        outPrintWriter.println("    Open the notifications panel.");
        outPrintWriter.println("");
        outPrintWriter.println("  expand-settings");
        outPrintWriter.println("    Open the notifications panel and expand quick settings if present.");
        outPrintWriter.println("");
        outPrintWriter.println("  collapse");
        outPrintWriter.println("    Collapse the notifications and settings panel.");
        outPrintWriter.println("");
        outPrintWriter.println("  add-tile COMPONENT");
        outPrintWriter.println("    Add a TileService of the specified component");
        outPrintWriter.println("");
        outPrintWriter.println("  remove-tile COMPONENT");
        outPrintWriter.println("    Remove a TileService of the specified component");
        outPrintWriter.println("");
        outPrintWriter.println("  click-tile COMPONENT");
        outPrintWriter.println("    Click on a TileService of the specified component");
        outPrintWriter.println("");
        outPrintWriter.println("  check-support");
        outPrintWriter.println("    Check if this device supports QS + APIs");
        outPrintWriter.println("");
        outPrintWriter.println("  get-status-icons");
        outPrintWriter.println("    Print the list of status bar icons and the order they appear in");
        outPrintWriter.println("");
        outPrintWriter.println("  disable-for-setup DISABLE");
        outPrintWriter.println("    If true, disable status bar components unsuitable for device setup");
        outPrintWriter.println("");
        outPrintWriter.println("  send-disable-flag FLAG...");
        outPrintWriter.println("    Send zero or more disable flags (parsed individually) to StatusBarManager");
        outPrintWriter.println("    Valid options:");
        outPrintWriter.println("        <blank>             - equivalent to \"none\"");
        outPrintWriter.println("        none                - re-enables all components");
        outPrintWriter.println("        search              - disable search");
        outPrintWriter.println("        home                - disable naviagation home");
        outPrintWriter.println("        recents             - disable recents/overview");
        outPrintWriter.println("        notification-peek   - disable notification peeking");
        outPrintWriter.println("        statusbar-expansion - disable status bar expansion");
        outPrintWriter.println("        system-icons        - disable system icons appearing in status bar");
        outPrintWriter.println("        clock               - disable clock appearing in status bar");
        outPrintWriter.println("        notification-icons  - disable notification icons from status bar");
        outPrintWriter.println("");
        outPrintWriter.println("  tracing (start | stop)");
        outPrintWriter.println("    Start or stop SystemUI tracing");
        outPrintWriter.println("");
        outPrintWriter.println("  NOTE: any command not listed here will be passed through to IStatusBar");
        outPrintWriter.println("");
        outPrintWriter.println("  Commands implemented in SystemUI:");
        outPrintWriter.flush();
        this.mInterface.passThroughShellCommand(new String[0], getOutFileDescriptor());
    }

    /* loaded from: classes2.dex */
    public static final class StatusBarShellCommandToken extends Binder {
        public StatusBarShellCommandToken() {
        }
    }
}
