package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb20ACOutputTerminal extends UsbACTerminal {
    public byte mClkSoureID;
    public int mControls;
    public byte mSourceID;
    public byte mTerminalID;

    public Usb20ACOutputTerminal(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    public byte getSourceID() {
        return this.mSourceID;
    }

    public byte getClkSourceID() {
        return this.mClkSoureID;
    }

    public int getControls() {
        return this.mControls;
    }

    @Override // com.android.server.usb.descriptors.UsbACTerminal
    public byte getTerminalID() {
        return this.mTerminalID;
    }

    @Override // com.android.server.usb.descriptors.UsbACTerminal, com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        super.parseRawDescriptors(byteStream);
        this.mSourceID = byteStream.getByte();
        this.mClkSoureID = byteStream.getByte();
        this.mControls = byteStream.unpackUsbShort();
        this.mTerminalID = byteStream.getByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACTerminal, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Source ID:" + ((int) getSourceID()));
        reportCanvas.writeListItem("Clock Source ID: " + ((int) getClkSourceID()));
        reportCanvas.writeListItem("Controls: " + ReportCanvas.getHexString(getControls()));
        reportCanvas.writeListItem("Terminal Name ID: " + ((int) getTerminalID()));
        reportCanvas.closeList();
    }
}
