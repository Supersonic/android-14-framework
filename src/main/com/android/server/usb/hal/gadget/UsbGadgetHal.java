package com.android.server.usb.hal.gadget;

import android.hardware.usb.UsbManager;
import android.os.RemoteException;
/* loaded from: classes2.dex */
public interface UsbGadgetHal {
    void getCurrentUsbFunctions(long j);

    @UsbManager.UsbHalVersion
    int getGadgetHalVersion() throws RemoteException;

    void getUsbSpeed(long j);

    void reset(long j);

    void setCurrentUsbFunctions(int i, long j, boolean z, int i2, long j2);
}
