package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class UsbMSMidiHeader extends UsbACInterface {
    public int mMidiStreamingClass;

    public UsbMSMidiHeader(int i, byte b, byte b2, int i2) {
        super(i, b, b2, i2);
    }

    public int getMidiStreamingClass() {
        return this.mMidiStreamingClass;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mMidiStreamingClass = byteStream.unpackUsbShort();
        byteStream.advance(this.mLength - byteStream.getReadCount());
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.writeHeader(3, "MS Midi Header: " + ReportCanvas.getHexString(getType()) + " SubType: " + ReportCanvas.getHexString(getSubclass()) + " Length: " + getLength() + " MidiStreamingClass :" + getMidiStreamingClass());
    }
}
