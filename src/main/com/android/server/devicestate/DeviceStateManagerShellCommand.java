package com.android.server.devicestate;

import android.hardware.devicestate.DeviceStateManager;
import android.hardware.devicestate.DeviceStateRequest;
import android.os.Binder;
import android.os.ShellCommand;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class DeviceStateManagerShellCommand extends ShellCommand {
    public static DeviceStateRequest sLastBaseStateRequest;
    public static DeviceStateRequest sLastRequest;
    public final DeviceStateManager mClient;
    public final DeviceStateManagerService mService;

    public DeviceStateManagerShellCommand(DeviceStateManagerService deviceStateManagerService) {
        this.mService = deviceStateManagerService;
        this.mClient = (DeviceStateManager) deviceStateManagerService.getContext().getSystemService(DeviceStateManager.class);
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        char c = 65535;
        switch (str.hashCode()) {
            case -1906524523:
                if (str.equals("base-state")) {
                    c = 0;
                    break;
                }
                break;
            case -1422060175:
                if (str.equals("print-state")) {
                    c = 1;
                    break;
                }
                break;
            case -1134192350:
                if (str.equals("print-states")) {
                    c = 2;
                    break;
                }
                break;
            case -295380803:
                if (str.equals("print-states-simple")) {
                    c = 3;
                    break;
                }
                break;
            case 109757585:
                if (str.equals("state")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return runBaseState(outPrintWriter);
            case 1:
                return runPrintState(outPrintWriter);
            case 2:
                return runPrintStates(outPrintWriter);
            case 3:
                return runPrintStatesSimple(outPrintWriter);
            case 4:
                return runState(outPrintWriter);
            default:
                return handleDefaultCommands(str);
        }
    }

    public final void printAllStates(PrintWriter printWriter) {
        Optional<DeviceState> committedState = this.mService.getCommittedState();
        Optional<DeviceState> baseState = this.mService.getBaseState();
        Optional<DeviceState> overrideState = this.mService.getOverrideState();
        printWriter.println("Committed state: " + toString(committedState));
        if (overrideState.isPresent()) {
            printWriter.println("----------------------");
            printWriter.println("Base state: " + toString(baseState));
            printWriter.println("Override state: " + overrideState.get());
        }
    }

    public final int runState(PrintWriter printWriter) {
        String nextArg = getNextArg();
        if (nextArg == null) {
            printAllStates(printWriter);
            return 0;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if ("reset".equals(nextArg)) {
                if (sLastRequest != null) {
                    this.mClient.cancelStateRequest();
                    sLastRequest = null;
                }
            } else {
                DeviceStateRequest build = DeviceStateRequest.newBuilder(Integer.parseInt(nextArg)).build();
                this.mClient.requestState(build, (Executor) null, (DeviceStateRequest.Callback) null);
                sLastRequest = build;
            }
            return 0;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: requested state should be an integer");
            return -1;
        } catch (IllegalArgumentException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: " + e.getMessage());
            getErrPrintWriter().println("-------------------");
            getErrPrintWriter().println("Run:");
            getErrPrintWriter().println("");
            getErrPrintWriter().println("    print-states");
            getErrPrintWriter().println("");
            getErrPrintWriter().println("to get the list of currently supported device states");
            return -1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int runBaseState(PrintWriter printWriter) {
        String nextArg = getNextArg();
        if (nextArg == null) {
            printAllStates(printWriter);
            return 0;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if ("reset".equals(nextArg)) {
                if (sLastBaseStateRequest != null) {
                    this.mClient.cancelBaseStateOverride();
                    sLastBaseStateRequest = null;
                }
            } else {
                DeviceStateRequest build = DeviceStateRequest.newBuilder(Integer.parseInt(nextArg)).build();
                this.mClient.requestBaseStateOverride(build, (Executor) null, (DeviceStateRequest.Callback) null);
                sLastBaseStateRequest = build;
            }
            return 0;
        } catch (NumberFormatException unused) {
            getErrPrintWriter().println("Error: requested state should be an integer");
            return -1;
        } catch (IllegalArgumentException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: " + e.getMessage());
            getErrPrintWriter().println("-------------------");
            getErrPrintWriter().println("Run:");
            getErrPrintWriter().println("");
            getErrPrintWriter().println("    print-states");
            getErrPrintWriter().println("");
            getErrPrintWriter().println("to get the list of currently supported device states");
            return -1;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int runPrintState(PrintWriter printWriter) {
        Optional<DeviceState> committedState = this.mService.getCommittedState();
        if (committedState.isPresent()) {
            printWriter.println(committedState.get().getIdentifier());
            return 0;
        }
        getErrPrintWriter().println("Error: device state not available.");
        return 1;
    }

    public final int runPrintStates(PrintWriter printWriter) {
        DeviceState[] supportedStates = this.mService.getSupportedStates();
        printWriter.print("Supported states: [\n");
        for (int i = 0; i < supportedStates.length; i++) {
            printWriter.print("  " + supportedStates[i] + ",\n");
        }
        printWriter.println("]");
        return 0;
    }

    public final int runPrintStatesSimple(PrintWriter printWriter) {
        printWriter.print((String) Arrays.stream(this.mService.getSupportedStates()).map(new Function() { // from class: com.android.server.devicestate.DeviceStateManagerShellCommand$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((DeviceState) obj).getIdentifier());
            }
        }).map(new Function() { // from class: com.android.server.devicestate.DeviceStateManagerShellCommand$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((Integer) obj).toString();
            }
        }).collect(Collectors.joining(",")));
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Device state manager (device_state) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println("  state [reset|OVERRIDE_DEVICE_STATE]");
        outPrintWriter.println("    Return or override device state.");
        outPrintWriter.println("  print-state");
        outPrintWriter.println("    Return the current device state.");
        outPrintWriter.println("  print-states");
        outPrintWriter.println("    Return list of currently supported device states.");
        outPrintWriter.println("  print-states-simple");
        outPrintWriter.println("    Return the currently supported device states in comma separated format.");
    }

    public static String toString(Optional<DeviceState> optional) {
        return optional.isPresent() ? optional.get().toString() : "(none)";
    }
}
