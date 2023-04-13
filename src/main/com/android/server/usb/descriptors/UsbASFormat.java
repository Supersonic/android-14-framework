package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
import com.android.server.usb.descriptors.report.UsbStrings;
/* loaded from: classes2.dex */
public class UsbASFormat extends UsbACInterface {
    public final byte mFormatType;

    public UsbASFormat(int i, byte b, byte b2, byte b3, int i2) {
        super(i, b, b2, i2);
        this.mFormatType = b3;
    }

    public byte getFormatType() {
        return this.mFormatType;
    }

    public static UsbDescriptor allocDescriptor(UsbDescriptorParser usbDescriptorParser, ByteStream byteStream, int i, byte b, byte b2, int i2) {
        byte b3 = byteStream.getByte();
        int aCInterfaceSpec = usbDescriptorParser.getACInterfaceSpec();
        if (b3 == 1) {
            if (aCInterfaceSpec == 512) {
                return new Usb20ASFormatI(i, b, b2, b3, i2);
            }
            return new Usb10ASFormatI(i, b, b2, b3, i2);
        } else if (b3 != 2) {
            if (b3 == 3) {
                return new Usb20ASFormatIII(i, b, b2, b3, i2);
            }
            return new UsbASFormat(i, b, b2, b3, i2);
        } else if (aCInterfaceSpec == 512) {
            return new Usb20ASFormatII(i, b, b2, b3, i2);
        } else {
            return new Usb10ASFormatII(i, b, b2, b3, i2);
        }
    }

    @Override // com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.writeParagraph(UsbStrings.getFormatName(getFormatType()), false);
    }
}
