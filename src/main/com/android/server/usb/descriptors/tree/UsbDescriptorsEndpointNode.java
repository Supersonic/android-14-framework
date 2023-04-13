package com.android.server.usb.descriptors.tree;

import com.android.server.usb.descriptors.UsbEndpointDescriptor;
import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public final class UsbDescriptorsEndpointNode extends UsbDescriptorsTreeNode {
    public final UsbEndpointDescriptor mEndpointDescriptor;

    public UsbDescriptorsEndpointNode(UsbEndpointDescriptor usbEndpointDescriptor) {
        this.mEndpointDescriptor = usbEndpointDescriptor;
    }

    public void report(ReportCanvas reportCanvas) {
        this.mEndpointDescriptor.report(reportCanvas);
    }
}
