package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public abstract class UsbACHeaderInterface extends UsbACInterface {
    public int mADCRelease;
    public int mTotalLength;

    public UsbACHeaderInterface(int i, byte b, byte b2, int i2, int i3) {
        super(i, b, b2, i2);
        this.mADCRelease = i3;
    }

    public int getADCRelease() {
        return this.mADCRelease;
    }

    public int getTotalLength() {
        return this.mTotalLength;
    }

    @Override // com.android.server.usb.descriptors.UsbACInterface, com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Release: " + ReportCanvas.getBCDString(getADCRelease()));
        reportCanvas.writeListItem("Total Length: " + getTotalLength());
        reportCanvas.closeList();
    }
}
