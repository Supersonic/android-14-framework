package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb20ACHeader extends UsbACHeaderInterface {
    public byte mCategory;
    public byte mControls;

    public Usb20ACHeader(int i, byte b, byte b2, int i2, int i3) {
        super(i, b, b2, i2, i3);
    }

    public byte getCategory() {
        return this.mCategory;
    }

    public byte getControls() {
        return this.mControls;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mCategory = byteStream.getByte();
        this.mTotalLength = byteStream.unpackUsbShort();
        this.mControls = byteStream.getByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACHeaderInterface, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Category: " + ReportCanvas.getHexString(getCategory()));
        reportCanvas.writeListItem("Controls: " + ReportCanvas.getHexString(getControls()));
        reportCanvas.closeList();
    }
}
