package com.android.server.usb.hal.gadget;

import com.android.internal.util.IndentingPrintWriter;
import com.android.server.usb.UsbDeviceManager;
import com.android.server.usb.UsbPortManager;
/* loaded from: classes2.dex */
public final class UsbGadgetHalInstance {
    public static UsbGadgetHal getInstance(UsbDeviceManager usbDeviceManager, IndentingPrintWriter indentingPrintWriter) {
        UsbPortManager.logAndPrint(3, indentingPrintWriter, "Querying USB Gadget HAL version");
        if (UsbGadgetAidl.isServicePresent(null)) {
            UsbPortManager.logAndPrint(4, indentingPrintWriter, "USB Gadget HAL AIDL present");
            return new UsbGadgetAidl(usbDeviceManager, indentingPrintWriter);
        } else if (UsbGadgetHidl.isServicePresent(null)) {
            UsbPortManager.logAndPrint(4, indentingPrintWriter, "USB Gadget HAL HIDL present");
            return new UsbGadgetHidl(usbDeviceManager, indentingPrintWriter);
        } else {
            UsbPortManager.logAndPrint(6, indentingPrintWriter, "USB Gadget HAL AIDL/HIDL not present");
            return null;
        }
    }
}
