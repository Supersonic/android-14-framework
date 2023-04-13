package com.android.server.p011pm.verify.domain;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IndentingPrintWriter;
import com.android.modules.utils.BasicShellCommandHandler;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
/* renamed from: com.android.server.pm.verify.domain.DomainVerificationShell */
/* loaded from: classes2.dex */
public class DomainVerificationShell {
    public final Callback mCallback;

    /* renamed from: com.android.server.pm.verify.domain.DomainVerificationShell$Callback */
    /* loaded from: classes2.dex */
    public interface Callback {
        void clearDomainVerificationState(List<String> list);

        void clearUserStates(List<String> list, int i);

        void printOwnersForDomains(IndentingPrintWriter indentingPrintWriter, List<String> list, Integer num);

        void printOwnersForPackage(IndentingPrintWriter indentingPrintWriter, String str, Integer num) throws PackageManager.NameNotFoundException;

        void printState(IndentingPrintWriter indentingPrintWriter, String str, Integer num) throws PackageManager.NameNotFoundException;

        void setDomainVerificationLinkHandlingAllowedInternal(String str, boolean z, int i) throws PackageManager.NameNotFoundException;

        void setDomainVerificationStatusInternal(String str, int i, ArraySet<String> arraySet) throws PackageManager.NameNotFoundException;

        void setDomainVerificationUserSelectionInternal(int i, String str, boolean z, ArraySet<String> arraySet) throws PackageManager.NameNotFoundException;

        void verifyPackages(List<String> list, boolean z);
    }

    public DomainVerificationShell(Callback callback) {
        this.mCallback = callback;
    }

    public void printHelp(PrintWriter printWriter) {
        printWriter.println("  get-app-links [--user <USER_ID>] [<PACKAGE>]");
        printWriter.println("    Prints the domain verification state for the given package, or for all");
        printWriter.println("    packages if none is specified. State codes are defined as follows:");
        printWriter.println("        - none: nothing has been recorded for this domain");
        printWriter.println("        - verified: the domain has been successfully verified");
        printWriter.println("        - approved: force approved, usually through shell");
        printWriter.println("        - denied: force denied, usually through shell");
        printWriter.println("        - migrated: preserved verification from a legacy response");
        printWriter.println("        - restored: preserved verification from a user data restore");
        printWriter.println("        - legacy_failure: rejected by a legacy verifier, unknown reason");
        printWriter.println("        - system_configured: automatically approved by the device config");
        printWriter.println("        - >= 1024: Custom error code which is specific to the device verifier");
        printWriter.println("      --user <USER_ID>: include user selections (includes all domains, not");
        printWriter.println("        just autoVerify ones)");
        printWriter.println("  reset-app-links [--user <USER_ID>] [<PACKAGE>]");
        printWriter.println("    Resets domain verification state for the given package, or for all");
        printWriter.println("    packages if none is specified.");
        printWriter.println("      --user <USER_ID>: clear user selection state instead; note this means");
        printWriter.println("        domain verification state will NOT be cleared");
        printWriter.println("      <PACKAGE>: the package to reset, or \"all\" to reset all packages");
        printWriter.println("  verify-app-links [--re-verify] [<PACKAGE>]");
        printWriter.println("    Broadcasts a verification request for the given package, or for all");
        printWriter.println("    packages if none is specified. Only sends if the package has previously");
        printWriter.println("    not recorded a response.");
        printWriter.println("      --re-verify: send even if the package has recorded a response");
        printWriter.println("  set-app-links [--package <PACKAGE>] <STATE> <DOMAINS>...");
        printWriter.println("    Manually set the state of a domain for a package. The domain must be");
        printWriter.println("    declared by the package as autoVerify for this to work. This command");
        printWriter.println("    will not report a failure for domains that could not be applied.");
        printWriter.println("      --package <PACKAGE>: the package to set, or \"all\" to set all packages");
        printWriter.println("      <STATE>: the code to set the domains to, valid values are:");
        printWriter.println("        STATE_NO_RESPONSE (0): reset as if no response was ever recorded.");
        printWriter.println("        STATE_SUCCESS (1): treat domain as successfully verified by domain.");
        printWriter.println("          verification agent. Note that the domain verification agent can");
        printWriter.println("          override this.");
        printWriter.println("        STATE_APPROVED (2): treat domain as always approved, preventing the");
        printWriter.println("           domain verification agent from changing it.");
        printWriter.println("        STATE_DENIED (3): treat domain as always denied, preveting the domain");
        printWriter.println("          verification agent from changing it.");
        printWriter.println("      <DOMAINS>: space separated list of domains to change, or \"all\" to");
        printWriter.println("        change every domain.");
        printWriter.println("  set-app-links-user-selection --user <USER_ID> [--package <PACKAGE>]");
        printWriter.println("      <ENABLED> <DOMAINS>...");
        printWriter.println("    Manually set the state of a host user selection for a package. The domain");
        printWriter.println("    must be declared by the package for this to work. This command will not");
        printWriter.println("    report a failure for domains that could not be applied.");
        printWriter.println("      --user <USER_ID>: the user to change selections for");
        printWriter.println("      --package <PACKAGE>: the package to set");
        printWriter.println("      <ENABLED>: whether or not to approve the domain");
        printWriter.println("      <DOMAINS>: space separated list of domains to change, or \"all\" to");
        printWriter.println("        change every domain.");
        printWriter.println("  set-app-links-allowed --user <USER_ID> [--package <PACKAGE>] <ALLOWED>");
        printWriter.println("    Toggle the auto verified link handling setting for a package.");
        printWriter.println("      --user <USER_ID>: the user to change selections for");
        printWriter.println("      --package <PACKAGE>: the package to set, or \"all\" to set all packages");
        printWriter.println("        packages will be reset if no one package is specified.");
        printWriter.println("      <ALLOWED>: true to allow the package to open auto verified links, false");
        printWriter.println("        to disable");
        printWriter.println("  get-app-link-owners [--user <USER_ID>] [--package <PACKAGE>] [<DOMAINS>]");
        printWriter.println("    Print the owners for a specific domain for a given user in low to high");
        printWriter.println("    priority order.");
        printWriter.println("      --user <USER_ID>: the user to query for");
        printWriter.println("      --package <PACKAGE>: optionally also print for all web domains declared");
        printWriter.println("        by a package, or \"all\" to print all packages");
        printWriter.println("      --<DOMAINS>: space separated list of domains to query for");
    }

    public Boolean runCommand(BasicShellCommandHandler basicShellCommandHandler, String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2140094634:
                if (str.equals("get-app-links")) {
                    c = 0;
                    break;
                }
                break;
            case -2092945963:
                if (str.equals("set-app-links-user-selection")) {
                    c = 1;
                    break;
                }
                break;
            case -1850904515:
                if (str.equals("set-app-links-allowed")) {
                    c = 2;
                    break;
                }
                break;
            case -1365963422:
                if (str.equals("set-app-links")) {
                    c = 3;
                    break;
                }
                break;
            case -825562609:
                if (str.equals("reset-app-links")) {
                    c = 4;
                    break;
                }
                break;
            case 1161008944:
                if (str.equals("get-app-link-owners")) {
                    c = 5;
                    break;
                }
                break;
            case 1328605369:
                if (str.equals("verify-app-links")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return Boolean.valueOf(runGetAppLinks(basicShellCommandHandler));
            case 1:
                return Boolean.valueOf(runSetAppLinksUserState(basicShellCommandHandler));
            case 2:
                return Boolean.valueOf(runSetAppLinksAllowed(basicShellCommandHandler));
            case 3:
                return Boolean.valueOf(runSetAppLinks(basicShellCommandHandler));
            case 4:
                return Boolean.valueOf(runResetAppLinks(basicShellCommandHandler));
            case 5:
                return Boolean.valueOf(runGetAppLinkOwners(basicShellCommandHandler));
            case 6:
                return Boolean.valueOf(runVerifyAppLinks(basicShellCommandHandler));
            default:
                return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean runSetAppLinks(BasicShellCommandHandler basicShellCommandHandler) {
        ArraySet arraySet = null;
        String str = null;
        while (true) {
            String nextOption = basicShellCommandHandler.getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--package")) {
                    str = basicShellCommandHandler.getNextArgRequired();
                } else {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: unknown option: " + nextOption);
                    return false;
                }
            } else if (TextUtils.isEmpty(str)) {
                basicShellCommandHandler.getErrPrintWriter().println("Error: no package specified");
                return false;
            } else {
                if (str.equalsIgnoreCase("all")) {
                    str = null;
                }
                String nextArgRequired = basicShellCommandHandler.getNextArgRequired();
                nextArgRequired.hashCode();
                int i = 3;
                char c = 65535;
                switch (nextArgRequired.hashCode()) {
                    case -1618466107:
                        if (nextArgRequired.equals("STATE_APPROVED")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 48:
                        if (nextArgRequired.equals("0")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 49:
                        if (nextArgRequired.equals("1")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 50:
                        if (nextArgRequired.equals("2")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 51:
                        if (nextArgRequired.equals("3")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 259145237:
                        if (nextArgRequired.equals("STATE_SUCCESS")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 482582065:
                        if (nextArgRequired.equals("STATE_NO_RESPONSE")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 534310697:
                        if (nextArgRequired.equals("STATE_DENIED")) {
                            c = 7;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case 3:
                        i = 2;
                        break;
                    case 1:
                    case 6:
                        i = 0;
                        break;
                    case 2:
                    case 5:
                        i = 1;
                        break;
                    case 4:
                    case 7:
                        break;
                    default:
                        basicShellCommandHandler.getErrPrintWriter().println("Invalid state option: " + nextArgRequired);
                        return false;
                }
                ArraySet arraySet2 = new ArraySet(getRemainingArgs(basicShellCommandHandler));
                if (arraySet2.isEmpty()) {
                    basicShellCommandHandler.getErrPrintWriter().println("No domains specified");
                    return false;
                }
                if (arraySet2.size() != 1 || !arraySet2.contains("all")) {
                    arraySet = arraySet2;
                }
                try {
                    this.mCallback.setDomainVerificationStatusInternal(str, i, arraySet);
                    return true;
                } catch (PackageManager.NameNotFoundException unused) {
                    basicShellCommandHandler.getErrPrintWriter().println("Package not found: " + str);
                    return false;
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean runSetAppLinksUserState(BasicShellCommandHandler basicShellCommandHandler) {
        ArraySet arraySet = null;
        String str = null;
        Integer num = null;
        while (true) {
            String nextOption = basicShellCommandHandler.getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--package")) {
                    str = basicShellCommandHandler.getNextArgRequired();
                } else if (nextOption.equals("--user")) {
                    num = Integer.valueOf(UserHandle.parseUserArg(basicShellCommandHandler.getNextArgRequired()));
                } else {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: unknown option: " + nextOption);
                    return false;
                }
            } else if (TextUtils.isEmpty(str)) {
                basicShellCommandHandler.getErrPrintWriter().println("Error: no package specified");
                return false;
            } else if (num == null) {
                basicShellCommandHandler.getErrPrintWriter().println("Error: User ID not specified");
                return false;
            } else {
                Integer valueOf = Integer.valueOf(translateUserId(num.intValue(), "runSetAppLinksUserState"));
                String nextArg = basicShellCommandHandler.getNextArg();
                if (TextUtils.isEmpty(nextArg)) {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: enabled param not specified");
                    return false;
                }
                try {
                    boolean parseEnabled = parseEnabled(nextArg);
                    ArraySet arraySet2 = new ArraySet(getRemainingArgs(basicShellCommandHandler));
                    if (arraySet2.isEmpty()) {
                        basicShellCommandHandler.getErrPrintWriter().println("No domains specified");
                        return false;
                    }
                    if (arraySet2.size() != 1 || !arraySet2.contains("all")) {
                        arraySet = arraySet2;
                    }
                    try {
                        this.mCallback.setDomainVerificationUserSelectionInternal(valueOf.intValue(), str, parseEnabled, arraySet);
                        return true;
                    } catch (PackageManager.NameNotFoundException unused) {
                        basicShellCommandHandler.getErrPrintWriter().println("Package not found: " + str);
                        return false;
                    }
                } catch (IllegalArgumentException e) {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: invalid enabled param: " + e.getMessage());
                    return false;
                }
            }
        }
    }

    public final boolean runGetAppLinks(BasicShellCommandHandler basicShellCommandHandler) {
        Integer num = null;
        while (true) {
            String nextOption = basicShellCommandHandler.getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--user")) {
                    num = Integer.valueOf(UserHandle.parseUserArg(basicShellCommandHandler.getNextArgRequired()));
                } else {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: unknown option: " + nextOption);
                    return false;
                }
            } else {
                Integer valueOf = num != null ? Integer.valueOf(translateUserId(num.intValue(), "runGetAppLinks")) : null;
                String nextArg = basicShellCommandHandler.getNextArg();
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(basicShellCommandHandler.getOutPrintWriter(), "  ", 120);
                try {
                    indentingPrintWriter.increaseIndent();
                    try {
                        this.mCallback.printState(indentingPrintWriter, nextArg, valueOf);
                        indentingPrintWriter.decreaseIndent();
                        indentingPrintWriter.close();
                        return true;
                    } catch (PackageManager.NameNotFoundException unused) {
                        basicShellCommandHandler.getErrPrintWriter().println("Error: package " + nextArg + " unavailable");
                        indentingPrintWriter.close();
                        return false;
                    }
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

    public final boolean runResetAppLinks(BasicShellCommandHandler basicShellCommandHandler) {
        Integer num = null;
        while (true) {
            String nextOption = basicShellCommandHandler.getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--user")) {
                    num = Integer.valueOf(UserHandle.parseUserArg(basicShellCommandHandler.getNextArgRequired()));
                } else {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: unknown option: " + nextOption);
                    return false;
                }
            } else {
                Integer valueOf = num == null ? null : Integer.valueOf(translateUserId(num.intValue(), "runResetAppLinks"));
                String peekNextArg = basicShellCommandHandler.peekNextArg();
                if (TextUtils.isEmpty(peekNextArg)) {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: no package specified");
                    return false;
                }
                List<String> asList = peekNextArg.equalsIgnoreCase("all") ? null : Arrays.asList(basicShellCommandHandler.peekRemainingArgs());
                if (valueOf != null) {
                    this.mCallback.clearUserStates(asList, valueOf.intValue());
                    return true;
                }
                this.mCallback.clearDomainVerificationState(asList);
                return true;
            }
        }
    }

    public final boolean runVerifyAppLinks(BasicShellCommandHandler basicShellCommandHandler) {
        boolean z = false;
        while (true) {
            String nextOption = basicShellCommandHandler.getNextOption();
            if (nextOption != null) {
                if (!nextOption.equals("--re-verify")) {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: unknown option: " + nextOption);
                    return false;
                }
                z = true;
            } else {
                String nextArg = basicShellCommandHandler.getNextArg();
                this.mCallback.verifyPackages(!TextUtils.isEmpty(nextArg) ? Collections.singletonList(nextArg) : null, z);
                return true;
            }
        }
    }

    public final boolean runSetAppLinksAllowed(BasicShellCommandHandler basicShellCommandHandler) {
        String str = null;
        Integer num = null;
        while (true) {
            String nextOption = basicShellCommandHandler.getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--package")) {
                    str = basicShellCommandHandler.getNextArg();
                } else if (nextOption.equals("--user")) {
                    num = Integer.valueOf(UserHandle.parseUserArg(basicShellCommandHandler.getNextArgRequired()));
                } else {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: unexpected option: " + nextOption);
                    return false;
                }
            } else if (TextUtils.isEmpty(str)) {
                basicShellCommandHandler.getErrPrintWriter().println("Error: no package specified");
                return false;
            } else {
                String str2 = str.equalsIgnoreCase("all") ? null : str;
                if (num == null) {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: user ID not specified");
                    return false;
                }
                String nextArg = basicShellCommandHandler.getNextArg();
                if (TextUtils.isEmpty(nextArg)) {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: allowed setting not specified");
                    return false;
                }
                try {
                    try {
                        this.mCallback.setDomainVerificationLinkHandlingAllowedInternal(str2, parseEnabled(nextArg), Integer.valueOf(translateUserId(num.intValue(), "runSetAppLinksAllowed")).intValue());
                        return true;
                    } catch (PackageManager.NameNotFoundException unused) {
                        basicShellCommandHandler.getErrPrintWriter().println("Package not found: " + str2);
                        return false;
                    }
                } catch (IllegalArgumentException e) {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: invalid allowed setting: " + e.getMessage());
                    return false;
                }
            }
        }
    }

    public final boolean runGetAppLinkOwners(BasicShellCommandHandler basicShellCommandHandler) {
        String str = null;
        Integer num = null;
        while (true) {
            String nextOption = basicShellCommandHandler.getNextOption();
            if (nextOption != null) {
                if (nextOption.equals("--package")) {
                    str = basicShellCommandHandler.getNextArgRequired();
                    if (TextUtils.isEmpty(str)) {
                        basicShellCommandHandler.getErrPrintWriter().println("Error: no package specified");
                        return false;
                    }
                } else if (nextOption.equals("--user")) {
                    num = Integer.valueOf(UserHandle.parseUserArg(basicShellCommandHandler.getNextArgRequired()));
                } else {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: unexpected option: " + nextOption);
                    return false;
                }
            } else {
                ArrayList<String> remainingArgs = getRemainingArgs(basicShellCommandHandler);
                if (remainingArgs.isEmpty() && TextUtils.isEmpty(str)) {
                    basicShellCommandHandler.getErrPrintWriter().println("Error: no package name or domain specified");
                    return false;
                }
                if (num != null) {
                    num = Integer.valueOf(translateUserId(num.intValue(), "runSetAppLinksAllowed"));
                }
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(basicShellCommandHandler.getOutPrintWriter(), "  ", 120);
                try {
                    indentingPrintWriter.increaseIndent();
                    if (str != null) {
                        String str2 = str.equals("all") ? null : str;
                        try {
                            this.mCallback.printOwnersForPackage(indentingPrintWriter, str2, num);
                        } catch (PackageManager.NameNotFoundException unused) {
                            basicShellCommandHandler.getErrPrintWriter().println("Error: package not found: " + str2);
                            indentingPrintWriter.close();
                            return false;
                        }
                    }
                    if (!remainingArgs.isEmpty()) {
                        this.mCallback.printOwnersForDomains(indentingPrintWriter, remainingArgs, num);
                    }
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.close();
                    return true;
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

    public final ArrayList<String> getRemainingArgs(BasicShellCommandHandler basicShellCommandHandler) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (true) {
            String nextArg = basicShellCommandHandler.getNextArg();
            if (nextArg == null) {
                return arrayList;
            }
            arrayList.add(nextArg);
        }
    }

    public final int translateUserId(int i, String str) {
        return ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), i, true, true, str, "pm command");
    }

    public final boolean parseEnabled(String str) throws IllegalArgumentException {
        String lowerCase = str.toLowerCase(Locale.US);
        lowerCase.hashCode();
        if (lowerCase.equals("true")) {
            return true;
        }
        if (lowerCase.equals("false")) {
            return false;
        }
        throw new IllegalArgumentException(str + " is not a valid boolean");
    }
}
