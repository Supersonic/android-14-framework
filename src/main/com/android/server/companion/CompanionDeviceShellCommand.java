package com.android.server.companion;

import android.companion.AssociationInfo;
import android.net.MacAddress;
import android.os.Binder;
import android.os.ShellCommand;
import com.android.internal.util.FunctionalUtils;
import com.android.server.companion.presence.CompanionDevicePresenceMonitor;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes.dex */
public class CompanionDeviceShellCommand extends ShellCommand {
    public final AssociationStore mAssociationStore;
    public final CompanionDevicePresenceMonitor mDevicePresenceMonitor;
    public final CompanionDeviceManagerService mService;

    public CompanionDeviceShellCommand(CompanionDeviceManagerService companionDeviceManagerService, AssociationStore associationStore, CompanionDevicePresenceMonitor companionDevicePresenceMonitor) {
        this.mService = companionDeviceManagerService;
        this.mAssociationStore = associationStore;
        this.mDevicePresenceMonitor = companionDevicePresenceMonitor;
    }

    public int onCommand(String str) {
        char c;
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            switch (str.hashCode()) {
                case -2105020158:
                    if (str.equals("clear-association-memory-cache")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -1855910485:
                    if (str.equals("remove-inactive-associations")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case -191868716:
                    if (str.equals("simulate-device-disappeared")) {
                        c = 5;
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
                case 784321104:
                    if (str.equals("disassociate")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 1586499358:
                    if (str.equals("associate")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 2001610978:
                    if (str.equals("simulate-device-appeared")) {
                        c = 4;
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
                    for (AssociationInfo associationInfo : this.mAssociationStore.getAssociationsForUser(getNextIntArgRequired())) {
                        outPrintWriter.println(associationInfo.getPackageName() + " " + associationInfo.getDeviceMacAddress());
                    }
                    break;
                case 1:
                    this.mService.createNewAssociation(getNextIntArgRequired(), getNextArgRequired(), MacAddress.fromString(getNextArgRequired()), null, null, false);
                    break;
                case 2:
                    AssociationInfo associationWithCallerChecks = this.mService.getAssociationWithCallerChecks(getNextIntArgRequired(), getNextArgRequired(), getNextArgRequired());
                    if (associationWithCallerChecks != null) {
                        this.mService.disassociateInternal(associationWithCallerChecks.getId());
                        break;
                    }
                    break;
                case 3:
                    this.mService.persistState();
                    this.mService.loadAssociationsFromDisk();
                    break;
                case 4:
                    this.mDevicePresenceMonitor.simulateDeviceAppeared(getNextIntArgRequired());
                    break;
                case 5:
                    this.mDevicePresenceMonitor.simulateDeviceDisappeared(getNextIntArgRequired());
                    break;
                case 6:
                    final CompanionDeviceManagerService companionDeviceManagerService = this.mService;
                    Objects.requireNonNull(companionDeviceManagerService);
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: com.android.server.companion.CompanionDeviceShellCommand$$ExternalSyntheticLambda0
                        public final void runOrThrow() {
                            CompanionDeviceManagerService.this.removeInactiveSelfManagedAssociations();
                        }
                    });
                    break;
                default:
                    return handleDefaultCommands(str);
            }
            return 0;
        } catch (Throwable th) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println();
            errPrintWriter.println("Exception occurred while executing '" + str + "':");
            th.printStackTrace(errPrintWriter);
            return 1;
        }
    }

    public final int getNextIntArgRequired() {
        return Integer.parseInt(getNextArgRequired());
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Companion Device Manager (companiondevice) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("      Print this help text.");
        outPrintWriter.println("  list USER_ID");
        outPrintWriter.println("      List all Associations for a user.");
        outPrintWriter.println("  associate USER_ID PACKAGE MAC_ADDRESS");
        outPrintWriter.println("      Create a new Association.");
        outPrintWriter.println("  disassociate USER_ID PACKAGE MAC_ADDRESS");
        outPrintWriter.println("      Remove an existing Association.");
        outPrintWriter.println("  clear-association-memory-cache");
        outPrintWriter.println("      Clear the in-memory association cache and reload all association ");
        outPrintWriter.println("      information from persistent storage. USE FOR DEBUGGING PURPOSES ONLY.");
        outPrintWriter.println("      USE FOR DEBUGGING AND/OR TESTING PURPOSES ONLY.");
        outPrintWriter.println("  simulate-device-appeared ASSOCIATION_ID");
        outPrintWriter.println("      Make CDM act as if the given companion device has appeared.");
        outPrintWriter.println("      I.e. bind the associated companion application's");
        outPrintWriter.println("      CompanionDeviceService(s) and trigger onDeviceAppeared() callback.");
        outPrintWriter.println("      The CDM will consider the devices as present for 60 seconds and then");
        outPrintWriter.println("      will act as if device disappeared, unless 'simulate-device-disappeared'");
        outPrintWriter.println("      or 'simulate-device-appeared' is called again before 60 seconds run out.");
        outPrintWriter.println("      USE FOR DEBUGGING AND/OR TESTING PURPOSES ONLY.");
        outPrintWriter.println("  simulate-device-disappeared ASSOCIATION_ID");
        outPrintWriter.println("      Make CDM act as if the given companion device has disappeared.");
        outPrintWriter.println("      I.e. unbind the associated companion application's");
        outPrintWriter.println("      CompanionDeviceService(s) and trigger onDeviceDisappeared() callback.");
        outPrintWriter.println("      NOTE: This will only have effect if 'simulate-device-appeared' was");
        outPrintWriter.println("      invoked for the same device (same ASSOCIATION_ID) no longer than");
        outPrintWriter.println("      60 seconds ago.");
        outPrintWriter.println("      USE FOR DEBUGGING AND/OR TESTING PURPOSES ONLY.");
        outPrintWriter.println("  remove-inactive-associations");
        outPrintWriter.println("      Remove self-managed associations that have not been active ");
        outPrintWriter.println("      for a long time (90 days or as configured via ");
        outPrintWriter.println("      \"debug.cdm.cdmservice.cleanup_time_window\" system property). ");
        outPrintWriter.println("      USE FOR DEBUGGING AND/OR TESTING PURPOSES ONLY.");
    }
}
