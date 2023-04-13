package com.android.server.usb.descriptors;

import android.util.Log;
/* loaded from: classes2.dex */
public abstract class UsbVCEndpoint extends UsbDescriptor {
    public static UsbDescriptor allocDescriptor(UsbDescriptorParser usbDescriptorParser, int i, byte b, byte b2) {
        usbDescriptorParser.getCurInterface();
        if (b2 != 0 && b2 != 1 && b2 != 2 && b2 != 3) {
            Log.w("UsbVCEndpoint", "Unknown Video Class Endpoint id:0x" + Integer.toHexString(b2));
        }
        return null;
    }
}
