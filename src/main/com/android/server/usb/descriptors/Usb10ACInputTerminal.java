package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb10ACInputTerminal extends UsbACTerminal implements UsbAudioChannelCluster {
    public int mChannelConfig;
    public byte mChannelNames;
    public byte mNrChannels;
    public byte mTerminal;

    public Usb10ACInputTerminal(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    @Override // com.android.server.usb.descriptors.UsbAudioChannelCluster
    public byte getChannelCount() {
        return this.mNrChannels;
    }

    public int getChannelConfig() {
        return this.mChannelConfig;
    }

    @Override // com.android.server.usb.descriptors.UsbACTerminal, com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        super.parseRawDescriptors(byteStream);
        this.mNrChannels = byteStream.getByte();
        this.mChannelConfig = byteStream.unpackUsbShort();
        this.mChannelNames = byteStream.getByte();
        this.mTerminal = byteStream.getByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACTerminal, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("" + ((int) getChannelCount()) + " Chans. Config: " + ReportCanvas.getHexString(getChannelConfig()));
        reportCanvas.closeList();
    }
}
