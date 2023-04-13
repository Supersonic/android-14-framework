package com.android.commands.telecom;

import android.app.ActivityThread;
import android.content.ComponentName;
import android.net.Uri;
import android.os.IUserManager;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.sysprop.TelephonyProperties;
import android.telecom.Log;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.internal.os.BaseCommand;
import com.android.internal.telecom.ITelecomService;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public final class Telecom extends BaseCommand {
    private static final String CALLING_PACKAGE = Telecom.class.getPackageName();
    private static final String COMMAND_ADD_OR_REMOVE_CALL_COMPANION_APP = "add-or-remove-call-companion-app";
    private static final String COMMAND_CLEANUP_ORPHAN_PHONE_ACCOUNTS = "cleanup-orphan-phone-accounts";
    private static final String COMMAND_CLEANUP_STUCK_CALLS = "cleanup-stuck-calls";
    private static final String COMMAND_GET_DEFAULT_DIALER = "get-default-dialer";
    private static final String COMMAND_GET_MAX_PHONES = "get-max-phones";
    private static final String COMMAND_GET_SIM_CONFIG = "get-sim-config";
    private static final String COMMAND_GET_SYSTEM_DIALER = "get-system-dialer";
    private static final String COMMAND_LOG_MARK = "log-mark";
    private static final String COMMAND_REGISTER_PHONE_ACCOUNT = "register-phone-account";
    private static final String COMMAND_REGISTER_SIM_PHONE_ACCOUNT = "register-sim-phone-account";
    private static final String COMMAND_RESET_CAR_MODE = "reset-car-mode";
    private static final String COMMAND_SET_CALL_DIAGNOSTIC_SERVICE = "set-call-diagnostic-service";
    private static final String COMMAND_SET_DEFAULT_DIALER = "set-default-dialer";
    private static final String COMMAND_SET_PHONE_ACCOUNT_DISABLED = "set-phone-account-disabled";
    private static final String COMMAND_SET_PHONE_ACCOUNT_ENABLED = "set-phone-account-enabled";
    private static final String COMMAND_SET_PHONE_ACCOUNT_SUGGESTION_COMPONENT = "set-phone-acct-suggestion-component";
    private static final String COMMAND_SET_SIM_COUNT = "set-sim-count";
    private static final String COMMAND_SET_SYSTEM_DIALER = "set-system-dialer";
    private static final String COMMAND_SET_TEST_CALL_REDIRECTION_APP = "set-test-call-redirection-app";
    private static final String COMMAND_SET_TEST_CALL_SCREENING_APP = "set-test-call-screening-app";
    private static final String COMMAND_SET_TEST_EMERGENCY_PHONE_ACCOUNT_PACKAGE_FILTER = "set-test-emergency-phone-account-package-filter";
    private static final String COMMAND_SET_USER_SELECTED_OUTGOING_PHONE_ACCOUNT = "set-user-selected-outgoing-phone-account";
    private static final String COMMAND_STOP_BLOCK_SUPPRESSION = "stop-block-suppression";
    private static final String COMMAND_UNREGISTER_PHONE_ACCOUNT = "unregister-phone-account";
    private static final String COMMAND_WAIT_ON_HANDLERS = "wait-on-handlers";
    private String mAccountId;
    private ComponentName mComponent;
    private ITelecomService mTelecomService;
    private TelephonyManager mTelephonyManager;
    private IUserManager mUserManager;

    public static void main(String[] args) {
        ActivityThread.initializeMainlineModules();
        new Telecom().run(args);
    }

    public void onShowUsage(PrintStream out) {
        out.println("usage: telecom [subcommand] [options]\nusage: telecom set-phone-account-enabled <COMPONENT> <ID> <USER_SN>\nusage: telecom set-phone-account-disabled <COMPONENT> <ID> <USER_SN>\nusage: telecom register-phone-account <COMPONENT> <ID> <USER_SN> <LABEL>\nusage: telecom register-sim-phone-account [-e] <COMPONENT> <ID> <USER_SN> <LABEL>: registers a PhoneAccount with CAPABILITY_SIM_SUBSCRIPTION and optionally CAPABILITY_PLACE_EMERGENCY_CALLS if \"-e\" is provided\nusage: telecom set-user-selected-outgoing-phone-account [-e] <COMPONENT> <ID> <USER_SN>\nusage: telecom set-test-call-redirection-app <PACKAGE>\nusage: telecom set-test-call-screening-app <PACKAGE>\nusage: telecom set-phone-acct-suggestion-component <COMPONENT>\nusage: telecom add-or-remove-call-companion-app <PACKAGE> <1/0>\nusage: telecom register-sim-phone-account <COMPONENT> <ID> <USER_SN> <LABEL> <ADDRESS>\nusage: telecom unregister-phone-account <COMPONENT> <ID> <USER_SN>\nusage: telecom set-call-diagnostic-service <PACKAGE>\nusage: telecom set-default-dialer <PACKAGE>\nusage: telecom get-default-dialer\nusage: telecom get-system-dialer\nusage: telecom wait-on-handlers\nusage: telecom set-sim-count <COUNT>\nusage: telecom get-sim-config\nusage: telecom get-max-phones\nusage: telecom stop-block-suppression: Stop suppressing the blocked number provider after a call to emergency services.\nusage: telecom cleanup-stuck-calls: Clear any disconnected calls that have gotten wedged in Telecom.\nusage: telecom cleanup-orphan-phone-accounts: remove any phone accounts that no longer have a valid UserHandle or accounts that no longer belongs to an installed package.\nusage: telecom set-emer-phone-account-filter <PACKAGE>\n\ntelecom set-phone-account-enabled: Enables the given phone account, if it has already been registered with Telecom.\n\ntelecom set-phone-account-disabled: Disables the given phone account, if it has already been registered with telecom.\n\ntelecom set-call-diagnostic-service: overrides call diagnostic service.\ntelecom set-default-dialer: Sets the override default dialer to the given component; this will override whatever the dialer role is set to.\n\ntelecom get-default-dialer: Displays the current default dialer.\n\ntelecom get-system-dialer: Displays the current system dialer.\ntelecom set-system-dialer: Set the override system dialer to the given component. To remove the override, send \"default\"\n\ntelecom wait-on-handlers: Wait until all handlers finish their work.\n\ntelecom set-sim-count: Set num SIMs (2 for DSDS, 1 for single SIM. This may restart the device.\n\ntelecom get-sim-config: Get the mSIM config string. \"DSDS\" for DSDS mode, or \"\" for single SIM\n\ntelecom get-max-phones: Get the max supported phones from the modem.\ntelecom set-test-emergency-phone-account-package-filter <PACKAGE>: sets a package name that will be used for test emergency calls. To clear, send an empty package name. Real emergency calls will still be placed over Telephony.\ntelecom log-mark <MESSAGE>: emits a message into the telecom logs.  Useful for testers to indicate where in the logs various test steps take place.\n");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void onRun() throws Exception {
        char c;
        ITelecomService asInterface = ITelecomService.Stub.asInterface(ServiceManager.getService("telecom"));
        this.mTelecomService = asInterface;
        if (asInterface == null) {
            Log.w(this, "onRun: Can't access telecom manager.", new Object[0]);
            showError("Error: Could not access the Telecom Manager. Is the system running?");
            return;
        }
        Looper.prepareMainLooper();
        TelephonyManager telephonyManager = (TelephonyManager) ActivityThread.systemMain().getSystemContext().getSystemService(TelephonyManager.class);
        this.mTelephonyManager = telephonyManager;
        if (telephonyManager == null) {
            Log.w(this, "onRun: Can't access telephony service.", new Object[0]);
            showError("Error: Could not access the Telephony Service. Is the system running?");
            return;
        }
        IUserManager asInterface2 = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
        this.mUserManager = asInterface2;
        if (asInterface2 == null) {
            Log.w(this, "onRun: Can't access user manager.", new Object[0]);
            showError("Error: Could not access the User Manager. Is the system running?");
            return;
        }
        Log.i(this, "onRun: parsing command.", new Object[0]);
        String command = nextArgRequired();
        switch (command.hashCode()) {
            case -2056063960:
                if (command.equals(COMMAND_SET_USER_SELECTED_OUTGOING_PHONE_ACCOUNT)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -2025240323:
                if (command.equals(COMMAND_UNREGISTER_PHONE_ACCOUNT)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1889448385:
                if (command.equals(COMMAND_WAIT_ON_HANDLERS)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -1763366875:
                if (command.equals(COMMAND_GET_MAX_PHONES)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -1763082020:
                if (command.equals(COMMAND_ADD_OR_REMOVE_CALL_COMPANION_APP)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1721798936:
                if (command.equals(COMMAND_STOP_BLOCK_SUPPRESSION)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1621979462:
                if (command.equals(COMMAND_RESET_CAR_MODE)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -1525813010:
                if (command.equals(COMMAND_SET_SIM_COUNT)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -1447595602:
                if (command.equals(COMMAND_REGISTER_SIM_PHONE_ACCOUNT)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -1441744159:
                if (command.equals(COMMAND_CLEANUP_ORPHAN_PHONE_ACCOUNTS)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -1441042957:
                if (command.equals(COMMAND_SET_CALL_DIAGNOSTIC_SERVICE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1190343856:
                if (command.equals(COMMAND_SET_SYSTEM_DIALER)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -853897535:
                if (command.equals(COMMAND_SET_TEST_CALL_REDIRECTION_APP)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -645705193:
                if (command.equals(COMMAND_SET_PHONE_ACCOUNT_ENABLED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -529505461:
                if (command.equals(COMMAND_SET_TEST_CALL_SCREENING_APP)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -250191036:
                if (command.equals(COMMAND_GET_SYSTEM_DIALER)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -55640960:
                if (command.equals(COMMAND_GET_DEFAULT_DIALER)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 86724198:
                if (command.equals(COMMAND_SET_PHONE_ACCOUNT_DISABLED)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 864392692:
                if (command.equals(COMMAND_SET_DEFAULT_DIALER)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 1039065275:
                if (command.equals(COMMAND_CLEANUP_STUCK_CALLS)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 1367516458:
                if (command.equals(COMMAND_SET_TEST_EMERGENCY_PHONE_ACCOUNT_PACKAGE_FILTER)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 1715956687:
                if (command.equals(COMMAND_GET_SIM_CONFIG)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 1967321014:
                if (command.equals(COMMAND_LOG_MARK)) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case 2034443044:
                if (command.equals(COMMAND_REGISTER_PHONE_ACCOUNT)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 2081437924:
                if (command.equals(COMMAND_SET_PHONE_ACCOUNT_SUGGESTION_COMPONENT)) {
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
                runSetPhoneAccountEnabled(true);
                return;
            case 1:
                runSetPhoneAccountEnabled(false);
                return;
            case 2:
                runRegisterPhoneAccount();
                return;
            case 3:
                runSetTestCallRedirectionApp();
                return;
            case 4:
                runSetTestCallScreeningApp();
                return;
            case 5:
                runAddOrRemoveCallCompanionApp();
                return;
            case 6:
                runSetTestPhoneAcctSuggestionComponent();
                return;
            case 7:
                runSetCallDiagnosticService();
                return;
            case '\b':
                runRegisterSimPhoneAccount();
                return;
            case '\t':
                runSetUserSelectedOutgoingPhoneAccount();
                return;
            case '\n':
                runUnregisterPhoneAccount();
                return;
            case 11:
                runStopBlockSuppression();
                return;
            case '\f':
                runCleanupStuckCalls();
                return;
            case '\r':
                runCleanupOrphanPhoneAccounts();
                return;
            case 14:
                runResetCarMode();
                return;
            case 15:
                runSetDefaultDialer();
                return;
            case 16:
                runGetDefaultDialer();
                return;
            case 17:
                runSetSystemDialer();
                return;
            case 18:
                runGetSystemDialer();
                return;
            case 19:
                runWaitOnHandler();
                return;
            case 20:
                runSetSimCount();
                return;
            case 21:
                runGetSimConfig();
                return;
            case 22:
                runGetMaxPhones();
                return;
            case 23:
                runSetEmergencyPhoneAccountPackageFilter();
                return;
            case 24:
                runLogMark();
                return;
            default:
                Log.w(this, "onRun: unknown command: %s", new Object[]{command});
                throw new IllegalArgumentException("unknown command '" + command + "'");
        }
    }

    private void runSetPhoneAccountEnabled(boolean enabled) throws RemoteException {
        PhoneAccountHandle handle = getPhoneAccountHandleFromArgs();
        boolean success = this.mTelecomService.enablePhoneAccount(handle, enabled);
        if (success) {
            System.out.println("Success - " + handle + (enabled ? " enabled." : " disabled."));
        } else {
            System.out.println("Error - is " + handle + " a valid PhoneAccount?");
        }
    }

    private void runRegisterPhoneAccount() throws RemoteException {
        PhoneAccountHandle handle = getPhoneAccountHandleFromArgs();
        String label = nextArgRequired();
        PhoneAccount account = PhoneAccount.builder(handle, label).setCapabilities(2).build();
        this.mTelecomService.registerPhoneAccount(account, CALLING_PACKAGE);
        System.out.println("Success - " + handle + " registered.");
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x0017, code lost:
        if (r1.equals("-e") != false) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void runRegisterSimPhoneAccount() throws RemoteException {
        boolean isEmergencyAccount = false;
        while (true) {
            String opt = nextOption();
            if (opt != null) {
                switch (opt.hashCode()) {
                    case 1496:
                        break;
                    default:
                        r3 = -1;
                        break;
                }
                switch (r3) {
                    case 0:
                        isEmergencyAccount = true;
                        break;
                }
            } else {
                PhoneAccountHandle handle = getPhoneAccountHandleFromArgs();
                String label = nextArgRequired();
                String address = nextArgRequired();
                int capabilities = (isEmergencyAccount ? 16 : 0) | 6;
                PhoneAccount account = PhoneAccount.builder(handle, label).setAddress(Uri.parse(address)).setSubscriptionAddress(Uri.parse(address)).setCapabilities(capabilities).setShortDescription(label).addSupportedUriScheme("tel").addSupportedUriScheme("voicemail").build();
                this.mTelecomService.registerPhoneAccount(account, CALLING_PACKAGE);
                System.out.println("Success - " + handle + " registered.");
                return;
            }
        }
    }

    private void runSetTestCallRedirectionApp() throws RemoteException {
        String packageName = nextArg();
        this.mTelecomService.setTestDefaultCallRedirectionApp(packageName);
    }

    private void runSetTestCallScreeningApp() throws RemoteException {
        String packageName = nextArg();
        this.mTelecomService.setTestDefaultCallScreeningApp(packageName);
    }

    private void runAddOrRemoveCallCompanionApp() throws RemoteException {
        String packageName = nextArgRequired();
        String isAdded = nextArgRequired();
        boolean isAddedBool = "1".equals(isAdded);
        this.mTelecomService.addOrRemoveTestCallCompanionApp(packageName, isAddedBool);
    }

    private void runSetCallDiagnosticService() throws RemoteException {
        String packageName = nextArg();
        if ("default".equals(packageName)) {
            packageName = null;
        }
        this.mTelecomService.setTestCallDiagnosticService(packageName);
        System.out.println("Success - " + packageName + " set as call diagnostic service.");
    }

    private void runSetTestPhoneAcctSuggestionComponent() throws RemoteException {
        String componentName = nextArg();
        this.mTelecomService.setTestPhoneAcctSuggestionComponent(componentName);
    }

    private void runSetUserSelectedOutgoingPhoneAccount() throws RemoteException {
        Log.i(this, "runSetUserSelectedOutgoingPhoneAccount", new Object[0]);
        PhoneAccountHandle handle = getPhoneAccountHandleFromArgs();
        this.mTelecomService.setUserSelectedOutgoingPhoneAccount(handle);
        System.out.println("Success - " + handle + " set as default outgoing account.");
    }

    private void runUnregisterPhoneAccount() throws RemoteException {
        PhoneAccountHandle handle = getPhoneAccountHandleFromArgs();
        this.mTelecomService.unregisterPhoneAccount(handle, CALLING_PACKAGE);
        System.out.println("Success - " + handle + " unregistered.");
    }

    private void runStopBlockSuppression() throws RemoteException {
        this.mTelecomService.stopBlockSuppression();
    }

    private void runCleanupStuckCalls() throws RemoteException {
        this.mTelecomService.cleanupStuckCalls();
    }

    private void runCleanupOrphanPhoneAccounts() throws RemoteException {
        System.out.println("Success - cleaned up " + this.mTelecomService.cleanupOrphanPhoneAccounts() + "  phone accounts.");
    }

    private void runResetCarMode() throws RemoteException {
        this.mTelecomService.resetCarMode();
    }

    private void runSetDefaultDialer() throws RemoteException {
        String packageName = nextArg();
        if ("default".equals(packageName)) {
            packageName = null;
        }
        this.mTelecomService.setTestDefaultDialer(packageName);
        System.out.println("Success - " + packageName + " set as override default dialer.");
    }

    private void runSetSystemDialer() throws RemoteException {
        String flatComponentName = nextArg();
        ComponentName componentName = flatComponentName.equals("default") ? null : parseComponentName(flatComponentName);
        this.mTelecomService.setSystemDialer(componentName);
        System.out.println("Success - " + componentName + " set as override system dialer.");
    }

    private void runGetDefaultDialer() throws RemoteException {
        System.out.println(this.mTelecomService.getDefaultDialerPackage(CALLING_PACKAGE));
    }

    private void runGetSystemDialer() throws RemoteException {
        System.out.println(this.mTelecomService.getSystemDialerPackage(CALLING_PACKAGE));
    }

    private void runWaitOnHandler() throws RemoteException {
    }

    private void runSetSimCount() throws RemoteException {
        if (!callerIsRoot()) {
            System.out.println("set-sim-count requires adb root");
            return;
        }
        int numSims = Integer.parseInt(nextArgRequired());
        System.out.println("Setting sim count to " + numSims + ". Device may reboot");
        this.mTelephonyManager.switchMultiSimConfig(numSims);
    }

    private void runGetSimConfig() throws RemoteException {
        System.out.println((String) TelephonyProperties.multi_sim_config().orElse(""));
    }

    private void runGetMaxPhones() throws RemoteException {
        System.out.println(this.mTelephonyManager.getSupportedModemCount());
    }

    private void runSetEmergencyPhoneAccountPackageFilter() throws RemoteException {
        String packageName = this.mArgs.getNextArg();
        if (TextUtils.isEmpty(packageName)) {
            this.mTelecomService.setTestEmergencyPhoneAccountPackageNameFilter((String) null);
            System.out.println("Success - filter cleared");
            return;
        }
        this.mTelecomService.setTestEmergencyPhoneAccountPackageNameFilter(packageName);
        System.out.println("Success = filter set to " + packageName);
    }

    private void runLogMark() throws RemoteException {
        String message = (String) Arrays.stream(this.mArgs.peekRemainingArgs()).collect(Collectors.joining(" "));
        this.mTelecomService.requestLogMark(message);
    }

    private PhoneAccountHandle getPhoneAccountHandleFromArgs() throws RemoteException {
        if (TextUtils.isEmpty(this.mArgs.peekNextArg())) {
            return null;
        }
        ComponentName component = parseComponentName(nextArgRequired());
        String accountId = nextArgRequired();
        String userSnInStr = nextArgRequired();
        try {
            int userSn = Integer.parseInt(userSnInStr);
            UserHandle userHandle = UserHandle.of(this.mUserManager.getUserHandle(userSn));
            return new PhoneAccountHandle(component, accountId, userHandle);
        } catch (NumberFormatException e) {
            Log.w(this, "getPhoneAccountHandleFromArgs - invalid user %s", new Object[]{userSnInStr});
            throw new IllegalArgumentException("Invalid user serial number " + userSnInStr);
        }
    }

    private boolean callerIsRoot() {
        return Process.myUid() == 0;
    }

    private ComponentName parseComponentName(String component) {
        ComponentName cn = ComponentName.unflattenFromString(component);
        if (cn == null) {
            throw new IllegalArgumentException("Invalid component " + component);
        }
        return cn;
    }
}
