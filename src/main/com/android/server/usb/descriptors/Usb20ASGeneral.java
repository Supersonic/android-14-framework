package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb20ASGeneral extends UsbACInterface implements UsbAudioChannelCluster {
    public int mChannelConfig;
    public byte mChannelNames;
    public byte mControls;
    public byte mFormatType;
    public int mFormats;
    public byte mNumChannels;
    public byte mTerminalLink;

    public Usb20ASGeneral(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    public byte getTerminalLink() {
        return this.mTerminalLink;
    }

    public byte getControls() {
        return this.mControls;
    }

    public byte getFormatType() {
        return this.mFormatType;
    }

    public int getFormats() {
        return this.mFormats;
    }

    @Override // com.android.server.usb.descriptors.UsbAudioChannelCluster
    public byte getChannelCount() {
        return this.mNumChannels;
    }

    public int getChannelConfig() {
        return this.mChannelConfig;
    }

    public byte getChannelNames() {
        return this.mChannelNames;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mTerminalLink = byteStream.getByte();
        this.mControls = byteStream.getByte();
        this.mFormatType = byteStream.getByte();
        this.mFormats = byteStream.unpackUsbInt();
        this.mNumChannels = byteStream.getByte();
        this.mChannelConfig = byteStream.unpackUsbInt();
        this.mChannelNames = byteStream.getByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Terminal Link: " + ((int) getTerminalLink()));
        reportCanvas.writeListItem("Controls: " + ReportCanvas.getHexString(getControls()));
        reportCanvas.writeListItem("Format Type: " + ReportCanvas.getHexString(getFormatType()));
        reportCanvas.writeListItem("Formats: " + ReportCanvas.getHexString(getFormats()));
        reportCanvas.writeListItem("Channel Count: " + ((int) getChannelCount()));
        reportCanvas.writeListItem("Channel Config: " + ReportCanvas.getHexString(getChannelConfig()));
        reportCanvas.writeListItem("Channel Names String ID: " + ((int) getChannelNames()));
        reportCanvas.closeList();
    }
}
