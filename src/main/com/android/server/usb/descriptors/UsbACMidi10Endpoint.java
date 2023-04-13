package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class UsbACMidi10Endpoint extends UsbACEndpoint {
    public byte[] mJackIds;
    public byte mNumJacks;

    public UsbACMidi10Endpoint(int i, byte b, int i2, byte b2) {
        super(i, b, i2, b2);
        this.mJackIds = new byte[0];
    }

    public byte getNumJacks() {
        return this.mNumJacks;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.usb.descriptors.UsbACEndpoint, com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        super.parseRawDescriptors(byteStream);
        int i = byteStream.getByte();
        this.mNumJacks = i;
        if (i > 0) {
            this.mJackIds = new byte[i];
            for (int i2 = 0; i2 < this.mNumJacks; i2++) {
                this.mJackIds[i2] = byteStream.getByte();
            }
        }
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.writeHeader(3, "ACMidi10Endpoint: " + ReportCanvas.getHexString(getType()) + " Length: " + getLength());
        reportCanvas.openList();
        reportCanvas.writeListItem("" + ((int) getNumJacks()) + " Jacks.");
        for (int i = 0; i < getNumJacks(); i++) {
            reportCanvas.writeListItem("Jack " + i + ": " + ((int) this.mJackIds[i]));
        }
        reportCanvas.closeList();
    }
}
