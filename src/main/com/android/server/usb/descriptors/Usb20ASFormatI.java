package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb20ASFormatI extends UsbASFormat {
    public byte mBitResolution;
    public byte mSubSlotSize;

    public Usb20ASFormatI(int i, byte b, byte b2, byte b3, int i2) {
        super(i, b, b2, b3, i2);
    }

    public byte getSubSlotSize() {
        return this.mSubSlotSize;
    }

    public byte getBitResolution() {
        return this.mBitResolution;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mSubSlotSize = byteStream.getByte();
        this.mBitResolution = byteStream.getByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbASFormat, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Subslot Size: " + ((int) getSubSlotSize()));
        reportCanvas.writeListItem("Bit Resolution: " + ((int) getBitResolution()));
        reportCanvas.closeList();
    }
}
