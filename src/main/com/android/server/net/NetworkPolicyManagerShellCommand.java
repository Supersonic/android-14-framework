package com.android.server.net;

import android.content.Context;
import android.net.NetworkPolicyManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class NetworkPolicyManagerShellCommand extends ShellCommand {
    public final NetworkPolicyManagerService mInterface;
    public final WifiManager mWifiManager;

    public static String overrideToString(int i) {
        return i != 1 ? i != 2 ? "none" : "false" : "true";
    }

    public NetworkPolicyManagerShellCommand(Context context, NetworkPolicyManagerService networkPolicyManagerService) {
        this.mInterface = networkPolicyManagerService;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public int onCommand(String str) {
        char c;
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            switch (str.hashCode()) {
                case -1544226370:
                    if (str.equals("start-watching")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -934610812:
                    if (str.equals("remove")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 96417:
                    if (str.equals("add")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 102230:
                    if (str.equals("get")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 113762:
                    if (str.equals("set")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 3322014:
                    if (str.equals("list")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 1093388830:
                    if (str.equals("stop-watching")) {
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
                    return runGet();
                case 1:
                    return runSet();
                case 2:
                    return runList();
                case 3:
                    return runAdd();
                case 4:
                    return runRemove();
                case 5:
                    return runStartWatching();
                case 6:
                    return runStopWatching();
                default:
                    return handleDefaultCommands(str);
            }
        } catch (RemoteException e) {
            outPrintWriter.println("Remote exception: " + e);
            return -1;
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Network policy manager (netpolicy) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("");
        outPrintWriter.println("  add restrict-background-whitelist UID");
        outPrintWriter.println("    Adds a UID to the whitelist for restrict background usage.");
        outPrintWriter.println("  add restrict-background-blacklist UID");
        outPrintWriter.println("    Adds a UID to the blacklist for restrict background usage.");
        outPrintWriter.println("  add app-idle-whitelist UID");
        outPrintWriter.println("    Adds a UID to the temporary app idle whitelist.");
        outPrintWriter.println("  get restrict-background");
        outPrintWriter.println("    Gets the global restrict background usage status.");
        outPrintWriter.println("  list wifi-networks [true|false]");
        outPrintWriter.println("    Lists all saved wifi networks and whether they are metered or not.");
        outPrintWriter.println("    If a boolean argument is passed, filters just the metered (or unmetered)");
        outPrintWriter.println("    networks.");
        outPrintWriter.println("  list restrict-background-whitelist");
        outPrintWriter.println("    Lists UIDs that are whitelisted for restrict background usage.");
        outPrintWriter.println("  list restrict-background-blacklist");
        outPrintWriter.println("    Lists UIDs that are blacklisted for restrict background usage.");
        outPrintWriter.println("  remove restrict-background-whitelist UID");
        outPrintWriter.println("    Removes a UID from the whitelist for restrict background usage.");
        outPrintWriter.println("  remove restrict-background-blacklist UID");
        outPrintWriter.println("    Removes a UID from the blacklist for restrict background usage.");
        outPrintWriter.println("  remove app-idle-whitelist UID");
        outPrintWriter.println("    Removes a UID from the temporary app idle whitelist.");
        outPrintWriter.println("  set metered-network ID [undefined|true|false]");
        outPrintWriter.println("    Toggles whether the given wi-fi network is metered.");
        outPrintWriter.println("  set restrict-background BOOLEAN");
        outPrintWriter.println("    Sets the global restrict background usage status.");
        outPrintWriter.println("  set sub-plan-owner subId [packageName]");
        outPrintWriter.println("    Sets the data plan owner package for subId.");
    }

    public final int runGet() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify type of data to get");
            return -1;
        } else if (nextArg.equals("restrict-background")) {
            return getRestrictBackground();
        } else {
            if (nextArg.equals("restricted-mode")) {
                return getRestrictedModeState();
            }
            outPrintWriter.println("Error: unknown get type '" + nextArg + "'");
            return -1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runSet() throws RemoteException {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify type of data to set");
            return -1;
        }
        switch (nextArg.hashCode()) {
            case -983249079:
                if (nextArg.equals("metered-network")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -747095841:
                if (nextArg.equals("restrict-background")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1846940860:
                if (nextArg.equals("sub-plan-owner")) {
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
                return setMeteredWifiNetwork();
            case 1:
                return setRestrictBackground();
            case 2:
                return setSubPlanOwner();
            default:
                outPrintWriter.println("Error: unknown set type '" + nextArg + "'");
                return -1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runList() throws RemoteException {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify type of data to list");
            return -1;
        }
        switch (nextArg.hashCode()) {
            case -1683867974:
                if (nextArg.equals("app-idle-whitelist")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -668534353:
                if (nextArg.equals("restrict-background-blacklist")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -363534403:
                if (nextArg.equals("wifi-networks")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 639570137:
                if (nextArg.equals("restrict-background-whitelist")) {
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
                return listAppIdleWhitelist();
            case 1:
                return listRestrictBackgroundBlacklist();
            case 2:
                return listWifiNetworks();
            case 3:
                return listRestrictBackgroundWhitelist();
            default:
                outPrintWriter.println("Error: unknown list type '" + nextArg + "'");
                return -1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runAdd() throws RemoteException {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify type of data to add");
            return -1;
        }
        switch (nextArg.hashCode()) {
            case -1683867974:
                if (nextArg.equals("app-idle-whitelist")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -668534353:
                if (nextArg.equals("restrict-background-blacklist")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 639570137:
                if (nextArg.equals("restrict-background-whitelist")) {
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
                return addAppIdleWhitelist();
            case 1:
                return addRestrictBackgroundBlacklist();
            case 2:
                return addRestrictBackgroundWhitelist();
            default:
                outPrintWriter.println("Error: unknown add type '" + nextArg + "'");
                return -1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runRemove() throws RemoteException {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify type of data to remove");
            return -1;
        }
        switch (nextArg.hashCode()) {
            case -1683867974:
                if (nextArg.equals("app-idle-whitelist")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -668534353:
                if (nextArg.equals("restrict-background-blacklist")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 639570137:
                if (nextArg.equals("restrict-background-whitelist")) {
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
                return removeAppIdleWhitelist();
            case 1:
                return removeRestrictBackgroundBlacklist();
            case 2:
                return removeRestrictBackgroundWhitelist();
            default:
                outPrintWriter.println("Error: unknown remove type '" + nextArg + "'");
                return -1;
        }
    }

    public final int runStartWatching() {
        int parseInt = Integer.parseInt(getNextArgRequired());
        if (parseInt < 0) {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.print("Invalid UID: ");
            outPrintWriter.println(parseInt);
            return -1;
        }
        this.mInterface.setDebugUid(parseInt);
        return 0;
    }

    public final int runStopWatching() {
        this.mInterface.setDebugUid(-1);
        return 0;
    }

    public final int listUidPolicies(String str, int i) throws RemoteException {
        return listUidList(str, this.mInterface.getUidsWithPolicy(i));
    }

    public final int listUidList(String str, int[] iArr) {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.print(str);
        outPrintWriter.print(": ");
        if (iArr.length == 0) {
            outPrintWriter.println("none");
        } else {
            for (int i : iArr) {
                outPrintWriter.print(i);
                outPrintWriter.print(' ');
            }
        }
        outPrintWriter.println();
        return 0;
    }

    public final int listRestrictBackgroundWhitelist() throws RemoteException {
        return listUidPolicies("Restrict background whitelisted UIDs", 4);
    }

    public final int listRestrictBackgroundBlacklist() throws RemoteException {
        return listUidPolicies("Restrict background blacklisted UIDs", 1);
    }

    public final int listAppIdleWhitelist() throws RemoteException {
        getOutPrintWriter();
        return listUidList("App Idle whitelisted UIDs", this.mInterface.getAppIdleWhitelist());
    }

    public final int getRestrictedModeState() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.print("Restricted mode status: ");
        outPrintWriter.println(this.mInterface.isRestrictedModeEnabled() ? "enabled" : "disabled");
        return 0;
    }

    public final int getRestrictBackground() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.print("Restrict background status: ");
        outPrintWriter.println(this.mInterface.getRestrictBackground() ? "enabled" : "disabled");
        return 0;
    }

    public final int setRestrictBackground() throws RemoteException {
        int nextBooleanArg = getNextBooleanArg();
        if (nextBooleanArg < 0) {
            return nextBooleanArg;
        }
        this.mInterface.setRestrictBackground(nextBooleanArg > 0);
        return 0;
    }

    public final int setSubPlanOwner() throws RemoteException {
        this.mInterface.setSubscriptionPlansOwner(Integer.parseInt(getNextArgRequired()), getNextArg());
        return 0;
    }

    public final int setUidPolicy(int i) throws RemoteException {
        int uidFromNextArg = getUidFromNextArg();
        if (uidFromNextArg < 0) {
            return uidFromNextArg;
        }
        this.mInterface.setUidPolicy(uidFromNextArg, i);
        return 0;
    }

    public final int resetUidPolicy(String str, int i) throws RemoteException {
        int uidFromNextArg = getUidFromNextArg();
        if (uidFromNextArg < 0) {
            return uidFromNextArg;
        }
        if (this.mInterface.getUidPolicy(uidFromNextArg) != i) {
            PrintWriter outPrintWriter = getOutPrintWriter();
            outPrintWriter.print("Error: UID ");
            outPrintWriter.print(uidFromNextArg);
            outPrintWriter.print(' ');
            outPrintWriter.println(str);
            return -1;
        }
        this.mInterface.setUidPolicy(uidFromNextArg, 0);
        return 0;
    }

    public final int addRestrictBackgroundWhitelist() throws RemoteException {
        return setUidPolicy(4);
    }

    public final int removeRestrictBackgroundWhitelist() throws RemoteException {
        return resetUidPolicy("not whitelisted", 4);
    }

    public final int addRestrictBackgroundBlacklist() throws RemoteException {
        return setUidPolicy(1);
    }

    public final int removeRestrictBackgroundBlacklist() throws RemoteException {
        return resetUidPolicy("not blacklisted", 1);
    }

    public final int setAppIdleWhitelist(boolean z) {
        int uidFromNextArg = getUidFromNextArg();
        if (uidFromNextArg < 0) {
            return uidFromNextArg;
        }
        this.mInterface.setAppIdleWhitelist(uidFromNextArg, z);
        return 0;
    }

    public final int addAppIdleWhitelist() throws RemoteException {
        return setAppIdleWhitelist(true);
    }

    public final int removeAppIdleWhitelist() throws RemoteException {
        return setAppIdleWhitelist(false);
    }

    public final int listWifiNetworks() {
        int i;
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            i = 0;
        } else {
            i = Boolean.parseBoolean(nextArg) ? 1 : 2;
        }
        for (WifiConfiguration wifiConfiguration : this.mWifiManager.getConfiguredNetworks()) {
            if (nextArg == null || wifiConfiguration.meteredOverride == i) {
                outPrintWriter.print(NetworkPolicyManager.resolveNetworkId(wifiConfiguration));
                outPrintWriter.print(';');
                outPrintWriter.println(overrideToString(wifiConfiguration.meteredOverride));
            }
        }
        return 0;
    }

    public final int setMeteredWifiNetwork() throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify networkId");
            return -1;
        }
        String nextArg2 = getNextArg();
        if (nextArg2 == null) {
            outPrintWriter.println("Error: didn't specify meteredOverride");
            return -1;
        }
        this.mInterface.setWifiMeteredOverride(NetworkPolicyManager.resolveNetworkId(nextArg), stringToOverride(nextArg2));
        return -1;
    }

    public static int stringToOverride(String str) {
        str.hashCode();
        if (str.equals("true")) {
            return 1;
        }
        return !str.equals("false") ? 0 : 2;
    }

    public final int getNextBooleanArg() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify BOOLEAN");
            return -1;
        }
        return Boolean.valueOf(nextArg).booleanValue() ? 1 : 0;
    }

    public final int getUidFromNextArg() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        String nextArg = getNextArg();
        if (nextArg == null) {
            outPrintWriter.println("Error: didn't specify UID");
            return -1;
        }
        try {
            return Integer.parseInt(nextArg);
        } catch (NumberFormatException unused) {
            outPrintWriter.println("Error: UID (" + nextArg + ") should be a number");
            return -2;
        }
    }
}
