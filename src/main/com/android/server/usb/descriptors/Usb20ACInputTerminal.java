package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb20ACInputTerminal extends UsbACTerminal implements UsbAudioChannelCluster {
    public int mChanConfig;
    public byte mChanNames;
    public byte mClkSourceID;
    public int mControls;
    public byte mNumChannels;
    public byte mTerminalName;

    public Usb20ACInputTerminal(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    public byte getClkSourceID() {
        return this.mClkSourceID;
    }

    @Override // com.android.server.usb.descriptors.UsbAudioChannelCluster
    public byte getChannelCount() {
        return this.mNumChannels;
    }

    public int getChannelConfig() {
        return this.mChanConfig;
    }

    @Override // com.android.server.usb.descriptors.UsbACTerminal, com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        super.parseRawDescriptors(byteStream);
        this.mClkSourceID = byteStream.getByte();
        this.mNumChannels = byteStream.getByte();
        this.mChanConfig = byteStream.unpackUsbInt();
        this.mChanNames = byteStream.getByte();
        this.mControls = byteStream.unpackUsbShort();
        this.mTerminalName = byteStream.getByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACTerminal, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Clock Source: " + ((int) getClkSourceID()));
        reportCanvas.writeListItem("" + ((int) getChannelCount()) + " Channels. Config: " + ReportCanvas.getHexString(getChannelConfig()));
        reportCanvas.closeList();
    }
}
