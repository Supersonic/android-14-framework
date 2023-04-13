package com.android.server.p011pm;

import android.annotation.RequiresPermission;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.os.RoSystemProperties;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.UiThread;
import com.android.server.power.ShutdownThread;
import com.android.server.utils.Slogf;
import java.io.PrintWriter;
import java.util.List;
/* renamed from: com.android.server.pm.UserManagerServiceShellCommand */
/* loaded from: classes2.dex */
public class UserManagerServiceShellCommand extends ShellCommand {
    public final Context mContext;
    public final LockPatternUtils mLockPatternUtils;
    public final UserManagerService mService;
    public final UserSystemPackageInstaller mSystemPackageInstaller;

    public UserManagerServiceShellCommand(UserManagerService userManagerService, UserSystemPackageInstaller userSystemPackageInstaller, LockPatternUtils lockPatternUtils, Context context) {
        this.mService = userManagerService;
        this.mSystemPackageInstaller = userSystemPackageInstaller;
        this.mLockPatternUtils = lockPatternUtils;
        this.mContext = context;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("User manager (user) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Prints this help text.");
        outPrintWriter.println();
        outPrintWriter.println("  list [-v | --verbose] [--all]");
        outPrintWriter.println("    Prints all users on the system.");
        outPrintWriter.println();
        outPrintWriter.println("  report-system-user-package-whitelist-problems [-v | --verbose] [--critical-only] [--mode MODE]");
        outPrintWriter.println("    Reports all issues on user-type package allowlist XML files. Options:");
        outPrintWriter.println("    -v | --verbose: shows extra info, like number of issues");
        outPrintWriter.println("    --critical-only: show only critical issues, excluding warnings");
        outPrintWriter.println("    --mode MODE: shows what errors would be if device used mode MODE");
        outPrintWriter.println("      (where MODE is the allowlist mode integer as defined by config_userTypePackageWhitelistMode)");
        outPrintWriter.println();
        outPrintWriter.println("  set-system-user-mode-emulation [--reboot | --no-restart] <headless | full | default>");
        outPrintWriter.println("    Changes whether the system user is headless, full, or default (as defined by OEM).");
        outPrintWriter.println("    WARNING: this command is meant just for development and debugging purposes.");
        outPrintWriter.println("             It should NEVER be used on automated tests.");
        outPrintWriter.println("    NOTE: by default it restarts the Android runtime, unless called with");
        outPrintWriter.println("          --reboot (which does a full reboot) or");
        outPrintWriter.println("          --no-restart (which requires a manual restart)");
        outPrintWriter.println();
        outPrintWriter.println("  is-headless-system-user-mode [-v | --verbose]");
        outPrintWriter.println("    Checks whether the device uses headless system user mode.");
        outPrintWriter.println("  is-visible-background-users-on-default-display-supported [-v | --verbose]");
        outPrintWriter.println("    Checks whether the device allows users to be start visible on background in the default display.");
        outPrintWriter.println("    It returns the effective mode, even when using emulation");
        outPrintWriter.println("    (to get the real mode as well, use -v or --verbose)");
        outPrintWriter.println();
        outPrintWriter.println("  is-visible-background-users-supported [-v | --verbose]");
        outPrintWriter.println("    Checks whether the device allows users to be start visible on background.");
        outPrintWriter.println("    It returns the effective mode, even when using emulation");
        outPrintWriter.println("    (to get the real mode as well, use -v or --verbose)");
        outPrintWriter.println();
        outPrintWriter.println("  is-user-visible [--display DISPLAY_ID] <USER_ID>");
        outPrintWriter.println("    Checks if the given user is visible in the given display.");
        outPrintWriter.println("    If the display option is not set, it uses the user's context to check");
        outPrintWriter.println("    (so it emulates what apps would get from UserManager.isUserVisible())");
        outPrintWriter.println();
        outPrintWriter.println("  get-main-user ");
        outPrintWriter.println("    Displays main user id or message if there is no main user");
        outPrintWriter.println();
    }

    public int onCommand(String str) {
        char c;
        if (str == null) {
            return handleDefaultCommands((String) null);
        }
        try {
            switch (str.hashCode()) {
                case -1698126264:
                    if (str.equals("is-visible-background-users-supported")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case -66900680:
                    if (str.equals("is-headless-system-user-mode")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 3322014:
                    if (str.equals("list")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 340621931:
                    if (str.equals("can-switch-to-headless-system-user")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case 681635871:
                    if (str.equals("is-main-user-permanent-admin")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case 980857487:
                    if (str.equals("is-visible-background-users-on-default-display-supported")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 1085270974:
                    if (str.equals("report-system-user-package-whitelist-problems")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1453420968:
                    if (str.equals("get-main-user")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 1605325659:
                    if (str.equals("set-system-user-mode-emulation")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 1802440755:
                    if (str.equals("is-user-visible")) {
                        c = 6;
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
                    return runList();
                case 1:
                    return runReportPackageAllowlistProblems();
                case 2:
                    return runSetSystemUserModeEmulation();
                case 3:
                    return runIsHeadlessSystemUserMode();
                case 4:
                    return runIsVisibleBackgroundUserSupported();
                case 5:
                    return runIsVisibleBackgroundUserOnDefaultDisplaySupported();
                case 6:
                    return runIsUserVisible();
                case 7:
                    return runGetMainUserId();
                case '\b':
                    return canSwitchToHeadlessSystemUser();
                case '\t':
                    return isMainUserPermanentAdmin();
                default:
                    return handleDefaultCommands(str);
            }
        } catch (RemoteException e) {
            getOutPrintWriter().println("Remote exception: " + e);
            return -1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runList() throws RemoteException {
        int i;
        List<UserInfo> list;
        boolean z;
        String str;
        String str2;
        String str3;
        char c;
        UserManagerServiceShellCommand userManagerServiceShellCommand = this;
        PrintWriter outPrintWriter = getOutPrintWriter();
        int i2 = 0;
        boolean z2 = false;
        boolean z3 = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case 1513:
                        if (nextOption.equals("-v")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 42995713:
                        if (nextOption.equals("--all")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1737088994:
                        if (nextOption.equals("--verbose")) {
                            c = 2;
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
                        z3 = true;
                        break;
                    case 1:
                        z2 = true;
                        break;
                    default:
                        outPrintWriter.println("Invalid option: " + nextOption);
                        return -1;
                }
            } else {
                IActivityManager service = ActivityManager.getService();
                List<UserInfo> users = userManagerServiceShellCommand.mService.getUsers(!z2, false, !z2);
                if (users == null) {
                    outPrintWriter.println("Error: couldn't get users");
                    return 1;
                }
                int size = users.size();
                int i3 = -10000;
                if (z3) {
                    outPrintWriter.printf("%d users:\n\n", Integer.valueOf(size));
                    i = service.getCurrentUser().id;
                } else {
                    outPrintWriter.println("Users:");
                    i = -10000;
                }
                int i4 = 0;
                while (i4 < size) {
                    UserInfo userInfo = users.get(i4);
                    boolean isUserRunning = service.isUserRunning(userInfo.id, i2);
                    if (z3) {
                        DevicePolicyManagerInternal devicePolicyManagerInternal = (DevicePolicyManagerInternal) LocalServices.getService(DevicePolicyManagerInternal.class);
                        if (devicePolicyManagerInternal != null) {
                            long clearCallingIdentity = Binder.clearCallingIdentity();
                            try {
                                int deviceOwnerUserId = devicePolicyManagerInternal.getDeviceOwnerUserId();
                                int i5 = userInfo.id;
                                str = deviceOwnerUserId == i5 ? " (device-owner)" : "";
                                str2 = devicePolicyManagerInternal.getProfileOwnerAsUser(i5) != null ? " (profile-owner)" : "";
                            } finally {
                                Binder.restoreCallingIdentity(clearCallingIdentity);
                            }
                        } else {
                            str = "";
                            str2 = str;
                        }
                        int i6 = userInfo.id;
                        int i7 = i6 == i ? 1 : i2;
                        int i8 = userInfo.profileGroupId;
                        boolean z4 = (i8 == i6 || i8 == i3) ? false : true;
                        boolean isUserVisible = userManagerServiceShellCommand.mService.isUserVisible(i6);
                        Object[] objArr = new Object[14];
                        objArr[0] = Integer.valueOf(i4);
                        objArr[1] = Integer.valueOf(userInfo.id);
                        objArr[2] = userInfo.name;
                        list = users;
                        objArr[3] = userInfo.userType.replace("android.os.usertype.", "");
                        objArr[4] = UserInfo.flagsToString(userInfo.flags);
                        if (z4) {
                            str3 = " (parentId=" + userInfo.profileGroupId + ")";
                        } else {
                            str3 = "";
                        }
                        objArr[5] = str3;
                        objArr[6] = isUserRunning ? " (running)" : "";
                        objArr[7] = userInfo.partial ? " (partial)" : "";
                        objArr[8] = userInfo.preCreated ? " (pre-created)" : "";
                        objArr[9] = userInfo.convertedFromPreCreated ? " (converted)" : "";
                        objArr[10] = str;
                        objArr[11] = str2;
                        objArr[12] = i7 != 0 ? " (current)" : "";
                        objArr[13] = isUserVisible ? " (visible)" : "";
                        outPrintWriter.printf("%d: id=%d, name=%s, type=%s, flags=%s%s%s%s%s%s%s%s%s%s\n", objArr);
                        z = true;
                    } else {
                        list = users;
                        Object[] objArr2 = new Object[2];
                        objArr2[0] = userInfo;
                        z = true;
                        objArr2[1] = isUserRunning ? " running" : "";
                        outPrintWriter.printf("\t%s%s\n", objArr2);
                    }
                    i4++;
                    userManagerServiceShellCommand = this;
                    users = list;
                    i2 = 0;
                    i3 = -10000;
                }
                return i2;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runReportPackageAllowlistProblems() {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        int i = -1000;
        boolean z = false;
        boolean z2 = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case -1362766982:
                        if (nextOption.equals("--critical-only")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1513:
                        if (nextOption.equals("-v")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333227331:
                        if (nextOption.equals("--mode")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1737088994:
                        if (nextOption.equals("--verbose")) {
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
                        z2 = true;
                        break;
                    case 1:
                    case 3:
                        z = true;
                        break;
                    case 2:
                        i = Integer.parseInt(getNextArgRequired());
                        break;
                    default:
                        outPrintWriter.println("Invalid option: " + nextOption);
                        return -1;
                }
            } else {
                Slog.d("UserManagerServiceShellCommand", "runReportPackageAllowlistProblems(): verbose=" + z + ", criticalOnly=" + z2 + ", mode=" + UserSystemPackageInstaller.modeToString(i));
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(outPrintWriter, "  ");
                try {
                    this.mSystemPackageInstaller.dumpPackageWhitelistProblems(indentingPrintWriter, i, z, z2);
                    indentingPrintWriter.close();
                    return 0;
                } catch (Throwable th) {
                    try {
                        indentingPrintWriter.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runSetSystemUserModeEmulation() {
        char c;
        boolean z;
        if (!confirmBuildIsDebuggable() || !confirmIsCalledByRoot()) {
            return -1;
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        if (this.mLockPatternUtils.isSecure(0)) {
            outPrintWriter.println("Cannot change system user mode when it has a credential");
            return -1;
        }
        boolean z2 = true;
        boolean z3 = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption == null) {
                if (z3 && !z2) {
                    getErrPrintWriter().println("You can use --reboot or --no-restart, but not both");
                    return -1;
                }
                String nextArgRequired = getNextArgRequired();
                boolean isHeadlessSystemUserMode = UserManager.isHeadlessSystemUserMode();
                nextArgRequired.hashCode();
                switch (nextArgRequired.hashCode()) {
                    case -1115062407:
                        if (nextArgRequired.equals("headless")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case 3154575:
                        if (nextArgRequired.equals("full")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1544803905:
                        if (nextArgRequired.equals("default")) {
                            c = 2;
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
                        z = !isHeadlessSystemUserMode;
                        break;
                    case 1:
                        z = isHeadlessSystemUserMode;
                        break;
                    case 2:
                        z = true;
                        break;
                    default:
                        getErrPrintWriter().printf("Invalid arg: %s\n", nextArgRequired);
                        return -1;
                }
                if (!z) {
                    Object[] objArr = new Object[1];
                    objArr[0] = isHeadlessSystemUserMode ? "headless" : "full";
                    outPrintWriter.printf("No change needed, system user is already %s\n", objArr);
                    return 0;
                }
                Slogf.m28d("UserManagerServiceShellCommand", "Updating system property %s to %s", "persist.debug.user_mode_emulation", nextArgRequired);
                SystemProperties.set("persist.debug.user_mode_emulation", nextArgRequired);
                if (z3) {
                    Slog.i("UserManagerServiceShellCommand", "Rebooting to finalize the changes");
                    outPrintWriter.println("Rebooting to finalize changes");
                    UiThread.getHandler().post(new Runnable() { // from class: com.android.server.pm.UserManagerServiceShellCommand$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            UserManagerServiceShellCommand.lambda$runSetSystemUserModeEmulation$0();
                        }
                    });
                } else if (z2) {
                    Slog.i("UserManagerServiceShellCommand", "Shutting PackageManager down");
                    ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).shutdown();
                    IActivityManager service = ActivityManager.getService();
                    if (service != null) {
                        try {
                            Slog.i("UserManagerServiceShellCommand", "Shutting ActivityManager down");
                            service.shutdown((int) FrameworkStatsLog.WIFI_BYTES_TRANSFER);
                        } catch (RemoteException e) {
                            Slog.e("UserManagerServiceShellCommand", "Failed to shut down ActivityManager" + e);
                        }
                    }
                    int myPid = Process.myPid();
                    Slogf.m20i("UserManagerServiceShellCommand", "Restarting Android runtime(PID=%d) to finalize changes", Integer.valueOf(myPid));
                    outPrintWriter.println("Restarting Android runtime to finalize changes");
                    outPrintWriter.flush();
                    Process.killProcess(myPid);
                } else {
                    outPrintWriter.println("System user mode changed - please reboot (or restart Android runtime) to continue");
                    outPrintWriter.println("NOTICE: after restart, some apps might be uninstalled (and their data will be lost)");
                }
                return 0;
            } else if (nextOption.equals("--no-restart")) {
                z2 = false;
            } else if (!nextOption.equals("--reboot")) {
                outPrintWriter.println("Invalid option: " + nextOption);
                return -1;
            } else {
                z3 = true;
            }
        }
    }

    public static /* synthetic */ void lambda$runSetSystemUserModeEmulation$0() {
        ShutdownThread.reboot(ActivityThread.currentActivityThread().getSystemUiContext(), "To switch headless / full system user mode", false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0048, code lost:
        if (r2 != (-1)) goto L19;
     */
    @RequiresPermission(anyOf = {"android.permission.INTERACT_ACROSS_USERS", "android.permission.INTERACT_ACROSS_USERS_FULL"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int runIsUserVisible() {
        boolean isUserVisible;
        PrintWriter outPrintWriter = getOutPrintWriter();
        Integer num = null;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--display")) {
                    num = Integer.valueOf(Integer.parseInt(getNextArgRequired()));
                } else {
                    outPrintWriter.println("Invalid option: " + nextOption);
                    return -1;
                }
            } else {
                int parseUserArg = UserHandle.parseUserArg(getNextArgRequired());
                if (parseUserArg != -10000 && parseUserArg != -3) {
                    if (parseUserArg == -2) {
                        parseUserArg = ActivityManager.getCurrentUser();
                    }
                    if (num != null) {
                        isUserVisible = this.mService.isUserVisibleOnDisplay(parseUserArg, num.intValue());
                    } else {
                        isUserVisible = getUserManagerForUser(parseUserArg).isUserVisible();
                    }
                    outPrintWriter.println(isUserVisible);
                    return 0;
                }
                outPrintWriter.printf("invalid value (%d) for --user option\n", Integer.valueOf(parseUserArg));
                return -1;
            }
        }
    }

    public final int runIsHeadlessSystemUserMode() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (!nextOption.equals("-v") && !nextOption.equals("--verbose")) {
                    outPrintWriter.println("Invalid option: " + nextOption);
                    return -1;
                }
                z = true;
            } else {
                boolean isHeadlessSystemUserMode = this.mService.isHeadlessSystemUserMode();
                if (!z) {
                    outPrintWriter.println(isHeadlessSystemUserMode);
                } else {
                    outPrintWriter.printf("effective=%b real=%b\n", Boolean.valueOf(isHeadlessSystemUserMode), Boolean.valueOf(RoSystemProperties.MULTIUSER_HEADLESS_SYSTEM_USER));
                }
                return 0;
            }
        }
    }

    public final int runIsVisibleBackgroundUserSupported() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (!nextOption.equals("-v") && !nextOption.equals("--verbose")) {
                    outPrintWriter.println("Invalid option: " + nextOption);
                    return -1;
                }
                z = true;
            } else {
                boolean isVisibleBackgroundUsersEnabled = UserManager.isVisibleBackgroundUsersEnabled();
                if (!z) {
                    outPrintWriter.println(isVisibleBackgroundUsersEnabled);
                } else {
                    outPrintWriter.printf("effective=%b real=%b\n", Boolean.valueOf(isVisibleBackgroundUsersEnabled), Boolean.valueOf(Resources.getSystem().getBoolean(17891743)));
                }
                return 0;
            }
        }
    }

    public final int runIsVisibleBackgroundUserOnDefaultDisplaySupported() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        boolean z = false;
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if (!nextOption.equals("-v") && !nextOption.equals("--verbose")) {
                    outPrintWriter.println("Invalid option: " + nextOption);
                    return -1;
                }
                z = true;
            } else {
                boolean isVisibleBackgroundUsersOnDefaultDisplayEnabled = UserManager.isVisibleBackgroundUsersOnDefaultDisplayEnabled();
                if (!z) {
                    outPrintWriter.println(isVisibleBackgroundUsersOnDefaultDisplayEnabled);
                } else {
                    outPrintWriter.printf("effective=%b real=%b\n", Boolean.valueOf(isVisibleBackgroundUsersOnDefaultDisplayEnabled), Boolean.valueOf(Resources.getSystem().getBoolean(17891744)));
                }
                return 0;
            }
        }
    }

    public final int runGetMainUserId() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        int mainUserId = this.mService.getMainUserId();
        if (mainUserId == -10000) {
            outPrintWriter.println("Couldn't get main user.");
            return 1;
        }
        outPrintWriter.println("Main user id: " + mainUserId);
        return 0;
    }

    public final int canSwitchToHeadlessSystemUser() {
        getOutPrintWriter().println(this.mService.canSwitchToHeadlessSystemUser());
        return 0;
    }

    public final int isMainUserPermanentAdmin() {
        getOutPrintWriter().println(this.mService.isMainUserPermanentAdmin());
        return 0;
    }

    public final UserManager getUserManagerForUser(int i) {
        return (UserManager) this.mContext.createContextAsUser(UserHandle.of(i), 0).getSystemService(UserManager.class);
    }

    public final boolean confirmBuildIsDebuggable() {
        if (Build.isDebuggable()) {
            return true;
        }
        getErrPrintWriter().println("Command not available on user builds");
        return false;
    }

    public final boolean confirmIsCalledByRoot() {
        if (Binder.getCallingUid() == 0) {
            return true;
        }
        getErrPrintWriter().println("Command only available on root user");
        return false;
    }
}
