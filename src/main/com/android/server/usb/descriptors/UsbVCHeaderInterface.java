package com.android.server.usb.descriptors;

import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public abstract class UsbVCHeaderInterface extends UsbVCInterface {
    public int mTotalLength;
    public int mVDCRelease;

    public UsbVCHeaderInterface(int i, byte b, byte b2, int i2) {
        super(i, b, b2);
        this.mVDCRelease = i2;
    }

    public int getVDCRelease() {
        return this.mVDCRelease;
    }

    public int getTotalLength() {
        return this.mTotalLength;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Release: " + ReportCanvas.getBCDString(getVDCRelease()));
        reportCanvas.writeListItem("Total Length: " + getTotalLength());
        reportCanvas.closeList();
    }
}
