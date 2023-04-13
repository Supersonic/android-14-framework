package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb20ASFormatII extends UsbASFormat {
    public int mMaxBitRate;
    public int mSlotsPerFrame;

    public Usb20ASFormatII(int i, byte b, byte b2, byte b3, int i2) {
        super(i, b, b2, b3, i2);
    }

    public int getmaxBitRate() {
        return this.mMaxBitRate;
    }

    public int getSlotsPerFrame() {
        return this.mSlotsPerFrame;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mMaxBitRate = byteStream.unpackUsbShort();
        this.mSlotsPerFrame = byteStream.unpackUsbShort();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbASFormat, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Max Bit Rate: " + getmaxBitRate());
        reportCanvas.writeListItem("slots Per Frame: " + getSlotsPerFrame());
        reportCanvas.closeList();
    }
}
