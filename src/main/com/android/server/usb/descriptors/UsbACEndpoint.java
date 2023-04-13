package com.android.server.usb.descriptors;

import android.util.Log;
/* loaded from: classes2.dex */
public abstract class UsbACEndpoint extends UsbDescriptor {
    public final int mSubclass;
    public final byte mSubtype;

    public UsbACEndpoint(int i, byte b, int i2, byte b2) {
        super(i, b);
        this.mSubclass = i2;
        this.mSubtype = b2;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        return this.mLength;
    }

    public static UsbDescriptor allocDescriptor(UsbDescriptorParser usbDescriptorParser, int i, byte b, byte b2) {
        int usbSubclass = usbDescriptorParser.getCurInterface().getUsbSubclass();
        if (usbSubclass != 1) {
            if (usbSubclass != 2) {
                if (usbSubclass != 3) {
                    Log.w("UsbACEndpoint", "Unknown Audio Class Endpoint id:0x" + Integer.toHexString(usbSubclass));
                    return null;
                } else if (b2 != 1) {
                    if (b2 == 2) {
                        return new UsbACMidi20Endpoint(i, b, usbSubclass, b2);
                    }
                    Log.w("UsbACEndpoint", "Unknown Midi Endpoint id:0x" + Integer.toHexString(b2));
                    return null;
                } else {
                    return new UsbACMidi10Endpoint(i, b, usbSubclass, b2);
                }
            }
            return new UsbACAudioStreamEndpoint(i, b, usbSubclass, b2);
        }
        return new UsbACAudioControlEndpoint(i, b, usbSubclass, b2);
    }
}
