package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class UsbHIDDescriptor extends UsbDescriptor {
    public byte mCountryCode;
    public int mDescriptorLen;
    public byte mDescriptorType;
    public byte mNumDescriptors;
    public int mRelease;

    public UsbHIDDescriptor(int i, byte b) {
        super(i, b);
        this.mHierarchyLevel = 3;
    }

    public int getRelease() {
        return this.mRelease;
    }

    public byte getNumDescriptors() {
        return this.mNumDescriptors;
    }

    public byte getDescriptorType() {
        return this.mDescriptorType;
    }

    public int getDescriptorLen() {
        return this.mDescriptorLen;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mRelease = byteStream.unpackUsbShort();
        this.mCountryCode = byteStream.getByte();
        this.mNumDescriptors = byteStream.getByte();
        this.mDescriptorType = byteStream.getByte();
        this.mDescriptorLen = byteStream.unpackUsbShort();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Spec: " + ReportCanvas.getBCDString(getRelease()));
        reportCanvas.writeListItem("Type: " + ReportCanvas.getBCDString(getDescriptorType()));
        reportCanvas.writeListItem("" + ((int) getNumDescriptors()) + " Descriptors Len: " + getDescriptorLen());
        reportCanvas.closeList();
    }
}
