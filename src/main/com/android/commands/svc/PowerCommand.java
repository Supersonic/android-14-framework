package com.android.commands.svc;

import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.sysprop.InitProperties;
import com.android.commands.svc.Svc;
/* loaded from: classes.dex */
public class PowerCommand extends Svc.Command {
    private static final int FORCE_SUSPEND_DELAY_DEFAULT_MILLIS = 0;

    public PowerCommand() {
        super("power");
    }

    @Override // com.android.commands.svc.Svc.Command
    public String shortHelp() {
        return "Control the power manager";
    }

    @Override // com.android.commands.svc.Svc.Command
    public String longHelp() {
        return shortHelp() + "\n\nusage: svc power stayon [true|false|usb|ac|wireless]\n         Set the 'keep awake while plugged in' setting.\n       svc power reboot [reason]\n         Perform a runtime shutdown and reboot device with specified reason.\n       svc power shutdown\n         Perform a runtime shutdown and power off the device.\n       svc power forcesuspend [t]\n         Force the system into suspend, ignoring all wakelocks.\n         t - Number of milliseconds to wait before issuing force-suspend.\n             Helps with devices that can't suspend while plugged in.\n             Defaults to 0.\n             When using a delay, you must use the nohup shell modifier:\n             'adb shell nohup svc power forcesuspend [time]'\n         Use caution; this is dangerous. It puts the device to sleep\n         immediately without giving apps or the system an opportunity to\n         save their state.\n";
    }

    @Override // com.android.commands.svc.Svc.Command
    public void run(String[] args) {
        int val;
        if (args.length >= 2) {
            IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
            if ("stayon".equals(args[1]) && args.length == 3) {
                if ("true".equals(args[2])) {
                    val = 7;
                } else if ("false".equals(args[2])) {
                    val = 0;
                } else if ("usb".equals(args[2])) {
                    val = 2;
                } else if ("ac".equals(args[2])) {
                    val = 1;
                } else if ("wireless".equals(args[2])) {
                    val = 4;
                }
                if (val != 0) {
                    try {
                        pm.wakeUp(SystemClock.uptimeMillis(), 0, "PowerCommand", (String) null);
                    } catch (RemoteException e) {
                        System.err.println("Faild to set setting: " + e);
                        return;
                    }
                }
                pm.setStayOnSetting(val);
                return;
            }
            if ("reboot".equals(args[1])) {
                String mode = null;
                if (args.length == 3) {
                    mode = args[2];
                }
                try {
                    pm.reboot(false, mode, true);
                    return;
                } catch (RemoteException e2) {
                    maybeLogRemoteException("Failed to reboot.");
                    return;
                } catch (Exception e3) {
                    System.err.println("Failed to reboot: " + e3.getMessage());
                    return;
                }
            } else if ("shutdown".equals(args[1])) {
                try {
                    pm.shutdown(false, (String) null, true);
                    return;
                } catch (RemoteException e4) {
                    maybeLogRemoteException("Failed to shutdown.");
                    return;
                }
            } else if ("forcesuspend".equals(args[1])) {
                int delayMillis = args.length > 2 ? Integer.parseInt(args[2]) : 0;
                try {
                    Thread.sleep(delayMillis);
                    if (!pm.forceSuspend()) {
                        System.err.println("Failed to force suspend.");
                        return;
                    }
                    return;
                } catch (RemoteException e5) {
                    maybeLogRemoteException("Failed to force-suspend with exception: " + e5);
                    return;
                } catch (InterruptedException e6) {
                    System.err.println("Failed to force suspend: " + e6);
                    return;
                }
            }
        }
        System.err.println(longHelp());
    }

    private void maybeLogRemoteException(String msg) {
        String powerProp = SystemProperties.get("sys.powerctl");
        if (powerProp.isEmpty() && !((Boolean) InitProperties.userspace_reboot_in_progress().orElse(false)).booleanValue()) {
            System.err.println(msg);
        }
    }
}
