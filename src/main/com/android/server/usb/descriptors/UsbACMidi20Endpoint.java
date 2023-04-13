package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class UsbACMidi20Endpoint extends UsbACEndpoint {
    public byte[] mBlockIds;
    public byte mNumGroupTerminals;

    public UsbACMidi20Endpoint(int i, byte b, int i2, byte b2) {
        super(i, b, i2, b2);
        this.mBlockIds = new byte[0];
    }

    public byte getNumGroupTerminals() {
        return this.mNumGroupTerminals;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.usb.descriptors.UsbACEndpoint, com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        super.parseRawDescriptors(byteStream);
        int i = byteStream.getByte();
        this.mNumGroupTerminals = i;
        if (i > 0) {
            this.mBlockIds = new byte[i];
            for (int i2 = 0; i2 < this.mNumGroupTerminals; i2++) {
                this.mBlockIds[i2] = byteStream.getByte();
            }
        }
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.writeHeader(3, "AC Midi20 Endpoint: " + ReportCanvas.getHexString(getType()) + " Length: " + getLength());
        reportCanvas.openList();
        reportCanvas.writeListItem("" + ((int) getNumGroupTerminals()) + " Group Terminals.");
        for (int i = 0; i < getNumGroupTerminals(); i++) {
            reportCanvas.writeListItem("Group Terminal " + i + ": " + ((int) this.mBlockIds[i]));
        }
        reportCanvas.closeList();
    }
}
