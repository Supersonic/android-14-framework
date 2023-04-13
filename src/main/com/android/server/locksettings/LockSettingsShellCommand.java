package com.android.server.locksettings;

import android.app.ActivityManager;
import android.app.admin.PasswordMetrics;
import android.content.Context;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes2.dex */
public class LockSettingsShellCommand extends ShellCommand {
    public final int mCallingPid;
    public final int mCallingUid;
    public final Context mContext;
    public int mCurrentUserId;
    public final LockPatternUtils mLockPatternUtils;
    public String mOld = "";
    public String mNew = "";

    public LockSettingsShellCommand(LockPatternUtils lockPatternUtils, Context context, int i, int i2) {
        this.mLockPatternUtils = lockPatternUtils;
        this.mCallingPid = i;
        this.mCallingUid = i2;
        this.mContext = context;
    }

    public int onCommand(String str) {
        boolean z;
        boolean z2;
        if (str == null) {
            return handleDefaultCommands(str);
        }
        try {
            this.mCurrentUserId = ActivityManager.getService().getCurrentUser().id;
            parseArgs();
            char c = 3;
            boolean z3 = true;
            if (!this.mLockPatternUtils.hasSecureLockScreen()) {
                switch (str.hashCode()) {
                    case -1473704173:
                        if (str.equals("get-disabled")) {
                            z2 = true;
                            break;
                        }
                        z2 = true;
                        break;
                    case 3198785:
                        if (str.equals("help")) {
                            z2 = false;
                            break;
                        }
                        z2 = true;
                        break;
                    case 75288455:
                        if (str.equals("set-disabled")) {
                            z2 = true;
                            break;
                        }
                        z2 = true;
                        break;
                    case 1062640281:
                        if (str.equals("set-resume-on-reboot-provider-package")) {
                            z2 = true;
                            break;
                        }
                        z2 = true;
                        break;
                    default:
                        z2 = true;
                        break;
                }
                if (z2 && !z2 && !z2 && !z2) {
                    getErrPrintWriter().println("The device does not support lock screen - ignoring the command.");
                    return -1;
                }
            }
            switch (str.hashCode()) {
                case -1957541639:
                    if (str.equals("remove-cache")) {
                        z = false;
                        break;
                    }
                    z = true;
                    break;
                case -868666698:
                    if (str.equals("require-strong-auth")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 3198785:
                    if (str.equals("help")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 1062640281:
                    if (str.equals("set-resume-on-reboot-provider-package")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                default:
                    z = true;
                    break;
            }
            if (!z) {
                runRemoveCache();
                return 0;
            } else if (z) {
                runSetResumeOnRebootProviderPackage();
                return 0;
            } else if (z) {
                runRequireStrongAuth();
                return 0;
            } else if (z) {
                onHelp();
                return 0;
            } else if (checkCredential()) {
                switch (str.hashCode()) {
                    case -2044327643:
                        if (str.equals("set-pattern")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1473704173:
                        if (str.equals("get-disabled")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case -819951495:
                        if (str.equals("verify")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 75288455:
                        if (str.equals("set-disabled")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 94746189:
                        if (str.equals("clear")) {
                            break;
                        }
                        c = 65535;
                        break;
                    case 1021333414:
                        if (str.equals("set-password")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1983832490:
                        if (str.equals("set-pin")) {
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
                        z3 = runSetPattern();
                        break;
                    case 1:
                        z3 = runSetPassword();
                        break;
                    case 2:
                        z3 = runSetPin();
                        break;
                    case 3:
                        z3 = runClear();
                        break;
                    case 4:
                        runSetDisabled();
                        break;
                    case 5:
                        runVerify();
                        break;
                    case 6:
                        runGetDisabled();
                        break;
                    default:
                        getErrPrintWriter().println("Unknown command: " + str);
                        break;
                }
                return z3 ? 0 : -1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            getErrPrintWriter().println("Error while executing command: " + str);
            e.printStackTrace(getErrPrintWriter());
            return -1;
        }
    }

    public final void runVerify() {
        getOutPrintWriter().println("Lock credential verified successfully");
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            outPrintWriter.println("lockSettings service commands:");
            outPrintWriter.println("");
            outPrintWriter.println("NOTE: when lock screen is set, all commands require the --old <CREDENTIAL> argument.");
            outPrintWriter.println("");
            outPrintWriter.println("  help");
            outPrintWriter.println("    Prints this help text.");
            outPrintWriter.println("");
            outPrintWriter.println("  get-disabled [--old <CREDENTIAL>] [--user USER_ID]");
            outPrintWriter.println("    Checks whether lock screen is disabled.");
            outPrintWriter.println("");
            outPrintWriter.println("  set-disabled [--old <CREDENTIAL>] [--user USER_ID] <true|false>");
            outPrintWriter.println("    When true, disables lock screen.");
            outPrintWriter.println("");
            outPrintWriter.println("  set-pattern [--old <CREDENTIAL>] [--user USER_ID] <PATTERN>");
            outPrintWriter.println("    Sets the lock screen as pattern, using the given PATTERN to unlock.");
            outPrintWriter.println("");
            outPrintWriter.println("  set-pin [--old <CREDENTIAL>] [--user USER_ID] <PIN>");
            outPrintWriter.println("    Sets the lock screen as PIN, using the given PIN to unlock.");
            outPrintWriter.println("");
            outPrintWriter.println("  set-password [--old <CREDENTIAL>] [--user USER_ID] <PASSWORD>");
            outPrintWriter.println("    Sets the lock screen as password, using the given PASSOWRD to unlock.");
            outPrintWriter.println("");
            outPrintWriter.println("  clear [--old <CREDENTIAL>] [--user USER_ID]");
            outPrintWriter.println("    Clears the lock credentials.");
            outPrintWriter.println("");
            outPrintWriter.println("  verify [--old <CREDENTIAL>] [--user USER_ID]");
            outPrintWriter.println("    Verifies the lock credentials.");
            outPrintWriter.println("");
            outPrintWriter.println("  remove-cache [--user USER_ID]");
            outPrintWriter.println("    Removes cached unified challenge for the managed profile.");
            outPrintWriter.println("");
            outPrintWriter.println("  set-resume-on-reboot-provider-package <package_name>");
            outPrintWriter.println("    Sets the package name for server based resume on reboot service provider.");
            outPrintWriter.println("");
            outPrintWriter.println("  require-strong-auth [--user USER_ID] <reason>");
            outPrintWriter.println("    Requires the strong authentication. The current supported reasons: STRONG_AUTH_REQUIRED_AFTER_USER_LOCKDOWN.");
            outPrintWriter.println("");
            outPrintWriter.close();
        } catch (Throwable th) {
            if (outPrintWriter != null) {
                try {
                    outPrintWriter.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public final void parseArgs() {
        while (true) {
            String nextOption = getNextOption();
            if (nextOption != null) {
                if ("--old".equals(nextOption)) {
                    this.mOld = getNextArgRequired();
                } else if ("--user".equals(nextOption)) {
                    int parseUserArg = UserHandle.parseUserArg(getNextArgRequired());
                    this.mCurrentUserId = parseUserArg;
                    if (parseUserArg == -2) {
                        this.mCurrentUserId = ActivityManager.getCurrentUser();
                    }
                } else {
                    PrintWriter errPrintWriter = getErrPrintWriter();
                    errPrintWriter.println("Unknown option: " + nextOption);
                    throw new IllegalArgumentException();
                }
            } else {
                this.mNew = getNextArg();
                return;
            }
        }
    }

    public final LockscreenCredential getOldCredential() {
        if (TextUtils.isEmpty(this.mOld)) {
            return LockscreenCredential.createNone();
        }
        if (this.mLockPatternUtils.isLockPasswordEnabled(this.mCurrentUserId)) {
            if (LockPatternUtils.isQualityAlphabeticPassword(this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mCurrentUserId))) {
                return LockscreenCredential.createPassword(this.mOld);
            }
            return LockscreenCredential.createPin(this.mOld);
        } else if (this.mLockPatternUtils.isLockPatternEnabled(this.mCurrentUserId)) {
            return LockscreenCredential.createPattern(LockPatternUtils.byteArrayToPattern(this.mOld.getBytes()));
        } else {
            return LockscreenCredential.createPassword(this.mOld);
        }
    }

    public final boolean runSetPattern() {
        LockscreenCredential createPattern = LockscreenCredential.createPattern(LockPatternUtils.byteArrayToPattern(this.mNew.getBytes()));
        if (isNewCredentialSufficient(createPattern)) {
            this.mLockPatternUtils.setLockCredential(createPattern, getOldCredential(), this.mCurrentUserId);
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Pattern set to '" + this.mNew + "'");
            return true;
        }
        return false;
    }

    public final boolean runSetPassword() {
        LockscreenCredential createPassword = LockscreenCredential.createPassword(this.mNew);
        if (isNewCredentialSufficient(createPassword)) {
            this.mLockPatternUtils.setLockCredential(createPassword, getOldCredential(), this.mCurrentUserId);
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Password set to '" + this.mNew + "'");
            return true;
        }
        return false;
    }

    public final boolean runSetPin() {
        LockscreenCredential createPin = LockscreenCredential.createPin(this.mNew);
        if (isNewCredentialSufficient(createPin)) {
            this.mLockPatternUtils.setLockCredential(createPin, getOldCredential(), this.mCurrentUserId);
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Pin set to '" + this.mNew + "'");
            return true;
        }
        return false;
    }

    public final boolean runSetResumeOnRebootProviderPackage() {
        String str = this.mNew;
        Slog.i("ShellCommand", "Setting persist.sys.resume_on_reboot_provider_package to " + str);
        this.mContext.enforcePermission("android.permission.BIND_RESUME_ON_REBOOT_SERVICE", this.mCallingPid, this.mCallingUid, "ShellCommand");
        SystemProperties.set("persist.sys.resume_on_reboot_provider_package", str);
        return true;
    }

    public final boolean runRequireStrongAuth() {
        String str = this.mNew;
        str.hashCode();
        if (str.equals("STRONG_AUTH_REQUIRED_AFTER_USER_LOCKDOWN")) {
            this.mCurrentUserId = -1;
            this.mLockPatternUtils.requireStrongAuth(32, -1);
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.println("Require strong auth for USER_ID " + this.mCurrentUserId + " because of " + this.mNew);
            return true;
        }
        PrintWriter errPrintWriter = getErrPrintWriter();
        errPrintWriter.println("Unsupported reason: " + str);
        return false;
    }

    public final boolean runClear() {
        LockscreenCredential createNone = LockscreenCredential.createNone();
        if (isNewCredentialSufficient(createNone)) {
            this.mLockPatternUtils.setLockCredential(createNone, getOldCredential(), this.mCurrentUserId);
            getOutPrintWriter().println("Lock credential cleared");
            return true;
        }
        return false;
    }

    public final boolean isNewCredentialSufficient(LockscreenCredential lockscreenCredential) {
        List validatePassword;
        PasswordMetrics requestedPasswordMetrics = this.mLockPatternUtils.getRequestedPasswordMetrics(this.mCurrentUserId);
        int requestedPasswordComplexity = this.mLockPatternUtils.getRequestedPasswordComplexity(this.mCurrentUserId);
        if (lockscreenCredential.isPassword() || lockscreenCredential.isPin()) {
            validatePassword = PasswordMetrics.validatePassword(requestedPasswordMetrics, requestedPasswordComplexity, lockscreenCredential.isPin(), lockscreenCredential.getCredential());
        } else {
            validatePassword = PasswordMetrics.validatePasswordMetrics(requestedPasswordMetrics, requestedPasswordComplexity, new PasswordMetrics(lockscreenCredential.isPattern() ? 1 : -1));
        }
        if (validatePassword.isEmpty()) {
            return true;
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("New credential doesn't satisfy admin policies: " + validatePassword.get(0));
        return false;
    }

    public final void runSetDisabled() {
        boolean parseBoolean = Boolean.parseBoolean(this.mNew);
        this.mLockPatternUtils.setLockScreenDisabled(parseBoolean, this.mCurrentUserId);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Lock screen disabled set to " + parseBoolean);
    }

    public final void runGetDisabled() {
        getOutPrintWriter().println(this.mLockPatternUtils.isLockScreenDisabled(this.mCurrentUserId));
    }

    public final boolean checkCredential() {
        if (this.mLockPatternUtils.isSecure(this.mCurrentUserId)) {
            if (this.mLockPatternUtils.isManagedProfileWithUnifiedChallenge(this.mCurrentUserId)) {
                getOutPrintWriter().println("Profile uses unified challenge");
                return false;
            }
            try {
                boolean checkCredential = this.mLockPatternUtils.checkCredential(getOldCredential(), this.mCurrentUserId, (LockPatternUtils.CheckCredentialProgressCallback) null);
                if (!checkCredential) {
                    if (!this.mLockPatternUtils.isManagedProfileWithUnifiedChallenge(this.mCurrentUserId)) {
                        this.mLockPatternUtils.reportFailedPasswordAttempt(this.mCurrentUserId);
                    }
                    PrintWriter outPrintWriter = getOutPrintWriter();
                    outPrintWriter.println("Old password '" + this.mOld + "' didn't match");
                } else {
                    this.mLockPatternUtils.reportSuccessfulPasswordAttempt(this.mCurrentUserId);
                }
                return checkCredential;
            } catch (LockPatternUtils.RequestThrottledException unused) {
                getOutPrintWriter().println("Request throttled");
                return false;
            }
        } else if (this.mOld.isEmpty()) {
            return true;
        } else {
            getOutPrintWriter().println("Old password provided but user has no password");
            return false;
        }
    }

    public final void runRemoveCache() {
        this.mLockPatternUtils.removeCachedUnifiedChallenge(this.mCurrentUserId);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Password cached removed for user " + this.mCurrentUserId);
    }
}
