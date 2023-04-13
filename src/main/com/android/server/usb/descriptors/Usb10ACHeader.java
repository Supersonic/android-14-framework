package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class Usb10ACHeader extends UsbACHeaderInterface {
    public byte mControls;
    public byte[] mInterfaceNums;
    public byte mNumInterfaces;

    public Usb10ACHeader(int i, byte b, byte b2, int i2, int i3) {
        super(i, b, b2, i2, i3);
        this.mNumInterfaces = (byte) 0;
        this.mInterfaceNums = null;
    }

    public byte getNumInterfaces() {
        return this.mNumInterfaces;
    }

    public byte[] getInterfaceNums() {
        return this.mInterfaceNums;
    }

    public byte getControls() {
        return this.mControls;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mTotalLength = byteStream.unpackUsbShort();
        if (this.mADCRelease >= 512) {
            this.mControls = byteStream.getByte();
        } else {
            int i = byteStream.getByte();
            this.mNumInterfaces = i;
            this.mInterfaceNums = new byte[i];
            for (int i2 = 0; i2 < this.mNumInterfaces; i2++) {
                this.mInterfaceNums[i2] = byteStream.getByte();
            }
        }
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACHeaderInterface, com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        byte numInterfaces = getNumInterfaces();
        StringBuilder sb = new StringBuilder();
        sb.append("" + ((int) numInterfaces) + " Interfaces");
        if (numInterfaces > 0) {
            sb.append(" [");
            byte[] interfaceNums = getInterfaceNums();
            if (interfaceNums != null) {
                for (int i = 0; i < numInterfaces; i++) {
                    sb.append("" + ((int) interfaceNums[i]));
                    if (i < numInterfaces - 1) {
                        sb.append(" ");
                    }
                }
            }
            sb.append("]");
        }
        reportCanvas.writeListItem(sb.toString());
        reportCanvas.writeListItem("Controls: " + ReportCanvas.getHexString(getControls()));
        reportCanvas.closeList();
    }
}
