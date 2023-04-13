package com.android.server.usb.descriptors.tree;

import com.android.server.usb.descriptors.UsbDeviceDescriptor;
import com.android.server.usb.descriptors.report.ReportCanvas;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public final class UsbDescriptorsDeviceNode extends UsbDescriptorsTreeNode {
    public final ArrayList<UsbDescriptorsConfigNode> mConfigNodes = new ArrayList<>();
    public final UsbDeviceDescriptor mDeviceDescriptor;

    public UsbDescriptorsDeviceNode(UsbDeviceDescriptor usbDeviceDescriptor) {
        this.mDeviceDescriptor = usbDeviceDescriptor;
    }

    public void addConfigDescriptorNode(UsbDescriptorsConfigNode usbDescriptorsConfigNode) {
        this.mConfigNodes.add(usbDescriptorsConfigNode);
    }

    public void report(ReportCanvas reportCanvas) {
        this.mDeviceDescriptor.report(reportCanvas);
        Iterator<UsbDescriptorsConfigNode> it = this.mConfigNodes.iterator();
        while (it.hasNext()) {
            it.next().report(reportCanvas);
        }
    }
}
