package com.android.server.hdmi;

import android.hardware.hdmi.IHdmiControlCallback;
import android.hardware.hdmi.IHdmiControlService;
import android.net.util.NetworkConstants;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.util.Slog;
import com.android.internal.util.jobs.XmlUtils;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public final class HdmiControlShellCommand extends ShellCommand {
    public final IHdmiControlService.Stub mBinderService;
    public final CountDownLatch mLatch = new CountDownLatch(1);
    public AtomicInteger mCecResult = new AtomicInteger();
    public IHdmiControlCallback.Stub mHdmiControlCallback = new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiControlShellCommand.1
        public void onComplete(int i) {
            PrintWriter outPrintWriter = HdmiControlShellCommand.this.getOutPrintWriter();
            outPrintWriter.println(" done (" + HdmiControlShellCommand.this.getResultString(i) + ")");
            HdmiControlShellCommand.this.mCecResult.set(i);
            HdmiControlShellCommand.this.mLatch.countDown();
        }
    };

    public HdmiControlShellCommand(IHdmiControlService.Stub stub) {
        this.mBinderService = stub;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands(str);
        }
        try {
            return handleShellCommand(str);
        } catch (Exception e) {
            PrintWriter errPrintWriter = this.getErrPrintWriter();
            errPrintWriter.println("Caught error for command '" + str + "': " + e.getMessage());
            StringBuilder sb = new StringBuilder();
            sb.append("Error handling hdmi_control shell command: ");
            sb.append(str);
            Slog.e("HdmiShellCommand", sb.toString(), e);
            return 1;
        }
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("HdmiControlManager (hdmi_control) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("      Print this help text.");
        outPrintWriter.println("  onetouchplay, otp");
        outPrintWriter.println("      Send the \"One Touch Play\" feature from a source to the TV");
        outPrintWriter.println("  vendorcommand --device_type <originating device type>");
        outPrintWriter.println("                --destination <destination device>");
        outPrintWriter.println("                --args <vendor specific arguments>");
        outPrintWriter.println("                [--id <true if vendor command should be sent with vendor id>]");
        outPrintWriter.println("      Send a Vendor Command to the given target device");
        outPrintWriter.println("  cec_setting get <setting name>");
        outPrintWriter.println("      Get the current value of a CEC setting");
        outPrintWriter.println("  cec_setting set <setting name> <value>");
        outPrintWriter.println("      Set the value of a CEC setting");
        outPrintWriter.println("  setsystemaudiomode, setsam [on|off]");
        outPrintWriter.println("      Sets the System Audio Mode feature on or off on TV devices");
        outPrintWriter.println("  setarc [on|off]");
        outPrintWriter.println("      Sets the ARC feature on or off on TV devices");
        outPrintWriter.println("  deviceselect <device id>");
        outPrintWriter.println("      Switch to device with given id");
        outPrintWriter.println("      The device's id is represented by its logical address.");
        outPrintWriter.println("  history_size get");
        outPrintWriter.println("      Gets the number of messages that can be stored in dumpsys history");
        outPrintWriter.println("  history_size set <new_size>");
        outPrintWriter.println("      Changes the number of messages that can be stored in dumpsys history to new_size");
    }

    public final int handleShellCommand(String str) throws RemoteException {
        PrintWriter outPrintWriter = getOutPrintWriter();
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1962118964:
                if (str.equals("history_size")) {
                    c = 0;
                    break;
                }
                break;
            case -956246195:
                if (str.equals("onetouchplay")) {
                    c = 1;
                    break;
                }
                break;
            case -905786704:
                if (str.equals("setarc")) {
                    c = 2;
                    break;
                }
                break;
            case -905769923:
                if (str.equals("setsam")) {
                    c = 3;
                    break;
                }
                break;
            case -467124088:
                if (str.equals("setsystemaudiomode")) {
                    c = 4;
                    break;
                }
                break;
            case -25969966:
                if (str.equals("deviceselect")) {
                    c = 5;
                    break;
                }
                break;
            case 110379:
                if (str.equals("otp")) {
                    c = 6;
                    break;
                }
                break;
            case 621295875:
                if (str.equals("vendorcommand")) {
                    c = 7;
                    break;
                }
                break;
            case 1322280018:
                if (str.equals("cec_setting")) {
                    c = '\b';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return historySize(outPrintWriter);
            case 1:
            case 6:
                return oneTouchPlay(outPrintWriter);
            case 2:
                return setArcMode(outPrintWriter);
            case 3:
            case 4:
                return setSystemAudioMode(outPrintWriter);
            case 5:
                return deviceSelect(outPrintWriter);
            case 7:
                return vendorCommand(outPrintWriter);
            case '\b':
                return cecSetting(outPrintWriter);
            default:
                getErrPrintWriter().println("Unhandled command: " + str);
                return 1;
        }
    }

    public final int deviceSelect(PrintWriter printWriter) throws RemoteException {
        if (getRemainingArgsCount() != 1) {
            throw new IllegalArgumentException("Expected exactly 1 argument.");
        }
        int parseInt = Integer.parseInt(getNextArg());
        printWriter.print("Sending Device Select...");
        this.mBinderService.deviceSelect(parseInt, this.mHdmiControlCallback);
        return (receiveCallback("Device Select") && this.mCecResult.get() == 0) ? 0 : 1;
    }

    public final int oneTouchPlay(PrintWriter printWriter) throws RemoteException {
        printWriter.print("Sending One Touch Play...");
        this.mBinderService.oneTouchPlay(this.mHdmiControlCallback);
        return (receiveCallback("One Touch Play") && this.mCecResult.get() == 0) ? 0 : 1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int vendorCommand(PrintWriter printWriter) throws RemoteException {
        char c;
        if (6 > getRemainingArgsCount()) {
            throw new IllegalArgumentException("Expected 3 arguments.");
        }
        String nextOption = getNextOption();
        String str = "";
        int i = -1;
        int i2 = -1;
        boolean z = false;
        while (nextOption != null) {
            switch (nextOption.hashCode()) {
                case -347347485:
                    if (nextOption.equals("--device_type")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -234325394:
                    if (nextOption.equals("--destination")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1492:
                    if (nextOption.equals("-a")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 1495:
                    if (nextOption.equals("-d")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case NetworkConstants.ETHER_MTU /* 1500 */:
                    if (nextOption.equals("-i")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 1511:
                    if (nextOption.equals("-t")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 1387195:
                    if (nextOption.equals("--id")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 1332872829:
                    if (nextOption.equals("--args")) {
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
                case 5:
                    i = Integer.parseInt(getNextArgRequired());
                    break;
                case 1:
                case 3:
                    i2 = Integer.parseInt(getNextArgRequired());
                    break;
                case 2:
                case 7:
                    str = getNextArgRequired();
                    break;
                case 4:
                case 6:
                    z = Boolean.parseBoolean(getNextArgRequired());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument: " + nextOption);
            }
            nextOption = getNextArg();
        }
        String[] split = str.split(XmlUtils.STRING_ARRAY_SEPARATOR);
        int length = split.length;
        byte[] bArr = new byte[length];
        for (int i3 = 0; i3 < length; i3++) {
            bArr[i3] = (byte) Integer.parseInt(split[i3], 16);
        }
        printWriter.println("Sending <Vendor Command>");
        this.mBinderService.sendVendorCommand(i, i2, bArr, z);
        return 0;
    }

    public final int cecSetting(PrintWriter printWriter) throws RemoteException {
        if (getRemainingArgsCount() < 1) {
            throw new IllegalArgumentException("Expected at least 1 argument (operation).");
        }
        String nextArgRequired = getNextArgRequired();
        nextArgRequired.hashCode();
        if (nextArgRequired.equals("get")) {
            String nextArgRequired2 = getNextArgRequired();
            try {
                String cecSettingStringValue = this.mBinderService.getCecSettingStringValue(nextArgRequired2);
                printWriter.println(nextArgRequired2 + " = " + cecSettingStringValue);
            } catch (IllegalArgumentException unused) {
                int cecSettingIntValue = this.mBinderService.getCecSettingIntValue(nextArgRequired2);
                printWriter.println(nextArgRequired2 + " = " + cecSettingIntValue);
            }
            return 0;
        } else if (nextArgRequired.equals("set")) {
            String nextArgRequired3 = getNextArgRequired();
            String nextArgRequired4 = getNextArgRequired();
            try {
                this.mBinderService.setCecSettingStringValue(nextArgRequired3, nextArgRequired4);
                printWriter.println(nextArgRequired3 + " = " + nextArgRequired4);
            } catch (IllegalArgumentException unused2) {
                int parseInt = Integer.parseInt(nextArgRequired4);
                this.mBinderService.setCecSettingIntValue(nextArgRequired3, parseInt);
                printWriter.println(nextArgRequired3 + " = " + parseInt);
            }
            return 0;
        } else {
            throw new IllegalArgumentException("Unknown operation: " + nextArgRequired);
        }
    }

    public final int setSystemAudioMode(PrintWriter printWriter) throws RemoteException {
        if (1 > getRemainingArgsCount()) {
            throw new IllegalArgumentException("Please indicate if System Audio Mode should be turned \"on\" or \"off\".");
        }
        String nextArg = getNextArg();
        if (nextArg.equals("on")) {
            printWriter.println("Setting System Audio Mode on");
            this.mBinderService.setSystemAudioMode(true, this.mHdmiControlCallback);
        } else if (nextArg.equals("off")) {
            printWriter.println("Setting System Audio Mode off");
            this.mBinderService.setSystemAudioMode(false, this.mHdmiControlCallback);
        } else {
            throw new IllegalArgumentException("Please indicate if System Audio Mode should be turned \"on\" or \"off\".");
        }
        return (receiveCallback("Set System Audio Mode") && this.mCecResult.get() == 0) ? 0 : 1;
    }

    public final int setArcMode(PrintWriter printWriter) throws RemoteException {
        if (1 > getRemainingArgsCount()) {
            throw new IllegalArgumentException("Please indicate if ARC mode should be turned \"on\" or \"off\".");
        }
        String nextArg = getNextArg();
        if (nextArg.equals("on")) {
            printWriter.println("Setting ARC mode on");
            this.mBinderService.setArcMode(true);
        } else if (nextArg.equals("off")) {
            printWriter.println("Setting ARC mode off");
            this.mBinderService.setArcMode(false);
        } else {
            throw new IllegalArgumentException("Please indicate if ARC mode should be turned \"on\" or \"off\".");
        }
        return 0;
    }

    public final int historySize(PrintWriter printWriter) throws RemoteException {
        if (1 > getRemainingArgsCount()) {
            throw new IllegalArgumentException("Use 'set' or 'get' for the command action");
        }
        String nextArgRequired = getNextArgRequired();
        nextArgRequired.hashCode();
        if (nextArgRequired.equals("get")) {
            int messageHistorySize = this.mBinderService.getMessageHistorySize();
            printWriter.println("CEC dumpsys message history size = " + messageHistorySize);
            return 0;
        } else if (nextArgRequired.equals("set")) {
            String nextArgRequired2 = getNextArgRequired();
            try {
                int parseInt = Integer.parseInt(nextArgRequired2);
                if (this.mBinderService.setMessageHistorySize(parseInt)) {
                    printWriter.println("Setting CEC dumpsys message history size to " + parseInt);
                } else {
                    printWriter.println("Message history size not changed, was it lower than the minimum size?");
                }
                return 0;
            } catch (NumberFormatException unused) {
                printWriter.println("Cannot set CEC dumpsys message history size to " + nextArgRequired2);
                return 1;
            }
        } else {
            throw new IllegalArgumentException("Unknown operation: " + nextArgRequired);
        }
    }

    public final boolean receiveCallback(String str) {
        try {
            if (this.mLatch.await(2000L, TimeUnit.MILLISECONDS)) {
                return true;
            }
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println(str + " timed out.");
            return false;
        } catch (InterruptedException unused) {
            getErrPrintWriter().println("Caught InterruptedException");
            Thread.currentThread().interrupt();
            return true;
        }
    }

    public final String getResultString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 5 ? i != 6 ? i != 7 ? Integer.toString(i) : "Communication Failed" : "Incorrect mode" : "Exception" : "Target not available" : "Source not available" : "Timeout" : "Success";
    }
}
