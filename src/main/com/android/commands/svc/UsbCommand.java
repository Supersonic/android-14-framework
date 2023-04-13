package com.android.commands.svc;

import android.app.ActivityThread;
import android.app.ContextImpl;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.commands.svc.Svc;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class UsbCommand extends Svc.Command {
    private static final AtomicInteger sUsbOperationCount = new AtomicInteger();

    public UsbCommand() {
        super("usb");
    }

    @Override // com.android.commands.svc.Svc.Command
    public String shortHelp() {
        return "Control Usb state";
    }

    @Override // com.android.commands.svc.Svc.Command
    public String longHelp() {
        return shortHelp() + "\n\nusage: svc usb setFunctions [function]\n         Set the current usb function. If function is blank, sets to charging.\n       svc usb setScreenUnlockedFunctions [function]\n         Sets the functions which, if the device was charging,\n         become current on screen unlock.\n         If function is blank, turn off this feature.\n       svc usb getFunctions\n         Gets the list of currently enabled functions\n         possible values of [function] are any of 'mtp', 'ptp', 'rndis',\n         'midi', 'ncm (if supporting gadget hal v1.2)'\n       svc usb resetUsbGadget\n         Reset usb gadget\n       svc usb getUsbSpeed\n         Gets current USB speed\n         possible values of USB speed are any of 'low speed', 'full speed',\n         'high speed', 'super speed', 'super speed (10G)',\n         'super speed (20G)', or higher (future extension)\n       svc usb getGadgetHalVersion\n         Gets current Gadget Hal Version\n         possible values of Hal version are any of 'unknown', 'V1_0', 'V1_1',\n         'V1_2'\n       svc usb getUsbHalVersion\n         Gets current USB Hal Version\n         possible values of Hal version are any of 'unknown', 'V1_0', 'V1_1',\n         'V1_2', 'V1_3'\n       svc usb resetUsbPort [port number]\n         Reset the specified connected usb port\n         default: the first connected usb port\n";
    }

    @Override // com.android.commands.svc.Svc.Command
    public void run(String[] args) {
        if (args.length >= 2) {
            Looper.prepareMainLooper();
            ContextImpl systemContext = ActivityThread.systemMain().getSystemContext();
            UsbManager usbManager = (UsbManager) systemContext.getSystemService(UsbManager.class);
            IUsbManager usbMgr = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
            Executor executor = systemContext.getMainExecutor();
            Consumer<Integer> consumer = new Consumer<Integer>() { // from class: com.android.commands.svc.UsbCommand.1
                @Override // java.util.function.Consumer
                public void accept(Integer status) {
                    System.out.println("Consumer status: " + status);
                }
            };
            if ("setFunctions".equals(args[1])) {
                try {
                    int operationId = sUsbOperationCount.incrementAndGet();
                    System.out.println("setCurrentFunctions opId:" + operationId);
                    usbMgr.setCurrentFunctions(UsbManager.usbFunctionsFromString(args.length >= 3 ? args[2] : ""), operationId);
                    return;
                } catch (RemoteException e) {
                    System.err.println("Error communicating with UsbManager: " + e);
                    return;
                }
            } else if ("getFunctions".equals(args[1])) {
                try {
                    System.err.println(UsbManager.usbFunctionsToString(usbMgr.getCurrentFunctions()));
                    return;
                } catch (RemoteException e2) {
                    System.err.println("Error communicating with UsbManager: " + e2);
                    return;
                }
            } else if ("setScreenUnlockedFunctions".equals(args[1])) {
                try {
                    usbMgr.setScreenUnlockedFunctions(UsbManager.usbFunctionsFromString(args.length >= 3 ? args[2] : ""));
                    return;
                } catch (RemoteException e3) {
                    System.err.println("Error communicating with UsbManager: " + e3);
                    return;
                }
            } else if ("resetUsbGadget".equals(args[1])) {
                try {
                    usbMgr.resetUsbGadget();
                    return;
                } catch (RemoteException e4) {
                    System.err.println("Error communicating with UsbManager: " + e4);
                    return;
                }
            } else if ("getUsbSpeed".equals(args[1])) {
                try {
                    System.err.println(UsbManager.usbSpeedToBandwidth(usbMgr.getCurrentUsbSpeed()));
                    return;
                } catch (RemoteException e5) {
                    System.err.println("Error communicating with UsbManager: " + e5);
                    return;
                }
            } else if ("getGadgetHalVersion".equals(args[1])) {
                try {
                    System.err.println(UsbManager.usbGadgetHalVersionToString(usbMgr.getGadgetHalVersion()));
                    return;
                } catch (RemoteException e6) {
                    System.err.println("Error communicating with UsbManager: " + e6);
                    return;
                }
            } else if ("getUsbHalVersion".equals(args[1])) {
                try {
                    int version = usbMgr.getUsbHalVersion();
                    if (version == 13) {
                        System.err.println("V1_3");
                    } else if (version == 12) {
                        System.err.println("V1_2");
                    } else if (version == 11) {
                        System.err.println("V1_1");
                    } else if (version == 10) {
                        System.err.println("V1_0");
                    } else {
                        System.err.println("unknown");
                    }
                    return;
                } catch (RemoteException e7) {
                    System.err.println("Error communicating with UsbManager: " + e7);
                    return;
                }
            } else if ("resetUsbPort".equals(args[1])) {
                try {
                    int portNum = args.length >= 3 ? Integer.parseInt(args[2]) : -1;
                    UsbPort port = null;
                    UsbPortStatus portStatus = null;
                    List<UsbPort> ports = usbManager.getPorts();
                    int numPorts = ports.size();
                    if (numPorts > 0) {
                        if (portNum != -1 && portNum < numPorts) {
                            portStatus = ports.get(portNum).getStatus();
                            if (portStatus.isConnected()) {
                                port = ports.get(portNum);
                                System.err.println("Get the USB port: port" + portNum);
                            }
                        } else {
                            portNum = 0;
                            while (true) {
                                if (portNum >= numPorts) {
                                    break;
                                }
                                UsbPortStatus status = ports.get(portNum).getStatus();
                                if (!status.isConnected()) {
                                    portNum++;
                                } else {
                                    port = ports.get(portNum);
                                    portStatus = status;
                                    System.err.println("Use the default USB port: port" + portNum);
                                    break;
                                }
                            }
                        }
                        if (port != null && portStatus.isConnected()) {
                            System.err.println("Reset the USB port: port" + portNum);
                            port.resetUsbPort(executor, consumer);
                            return;
                        }
                        System.err.println("There is no available reset USB port");
                        return;
                    }
                    System.err.println("No USB ports");
                    return;
                } catch (Exception e8) {
                    System.err.println("Error communicating with UsbManager: " + e8);
                    return;
                }
            }
        }
        System.err.println(longHelp());
    }
}
