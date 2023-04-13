package com.android.server.usb.hal.port;

import android.hardware.usb.IUsbOperationInternal;
import android.hardware.usb.UsbManager;
import android.os.RemoteException;
/* loaded from: classes2.dex */
public interface UsbPortHal {
    void enableContaminantPresenceDetection(String str, boolean z, long j);

    void enableLimitPowerTransfer(String str, boolean z, long j, IUsbOperationInternal iUsbOperationInternal);

    boolean enableUsbData(String str, boolean z, long j, IUsbOperationInternal iUsbOperationInternal);

    void enableUsbDataWhileDocked(String str, long j, IUsbOperationInternal iUsbOperationInternal);

    @UsbManager.UsbHalVersion
    int getUsbHalVersion() throws RemoteException;

    void queryPortStatus(long j);

    void resetUsbPort(String str, long j, IUsbOperationInternal iUsbOperationInternal);

    void switchDataRole(String str, int i, long j);

    void switchMode(String str, int i, long j);

    void switchPowerRole(String str, int i, long j);

    void systemReady();
}
