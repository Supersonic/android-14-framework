package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
import com.android.server.usb.descriptors.report.UsbStrings;
/* loaded from: classes2.dex */
public abstract class UsbACTerminal extends UsbACInterface {
    public byte mAssocTerminal;
    public byte mTerminalID;
    public int mTerminalType;

    public UsbACTerminal(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    public byte getTerminalID() {
        return this.mTerminalID;
    }

    public int getTerminalType() {
        return this.mTerminalType;
    }

    public byte getAssocTerminal() {
        return this.mAssocTerminal;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mTerminalID = byteStream.getByte();
        this.mTerminalType = byteStream.unpackUsbShort();
        this.mAssocTerminal = byteStream.getByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        int terminalType = getTerminalType();
        reportCanvas.writeListItem("Type: " + ReportCanvas.getHexString(terminalType) + ": " + UsbStrings.getTerminalName(terminalType));
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ");
        sb.append(ReportCanvas.getHexString(getTerminalID()));
        reportCanvas.writeListItem(sb.toString());
        reportCanvas.writeListItem("Associated terminal: " + ReportCanvas.getHexString(getAssocTerminal()));
        reportCanvas.closeList();
    }
}
