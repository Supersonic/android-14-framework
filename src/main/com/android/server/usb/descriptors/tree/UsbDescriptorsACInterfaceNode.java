package com.android.server.usb.descriptors.tree;

import com.android.server.usb.descriptors.UsbACInterface;
import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class UsbDescriptorsACInterfaceNode extends UsbDescriptorsTreeNode {
    public final UsbACInterface mACInterface;

    public void report(ReportCanvas reportCanvas) {
        reportCanvas.writeListItem("AC Interface type: 0x" + Integer.toHexString(this.mACInterface.getSubtype()));
        reportCanvas.openList();
        this.mACInterface.report(reportCanvas);
        reportCanvas.closeList();
    }
}
