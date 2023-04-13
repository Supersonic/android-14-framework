package com.android.server.usb.descriptors.tree;

import com.android.server.usb.descriptors.UsbConfigDescriptor;
import com.android.server.usb.descriptors.UsbDescriptor;
import com.android.server.usb.descriptors.UsbDescriptorParser;
import com.android.server.usb.descriptors.UsbDeviceDescriptor;
import com.android.server.usb.descriptors.UsbEndpointDescriptor;
import com.android.server.usb.descriptors.UsbInterfaceDescriptor;
import com.android.server.usb.descriptors.report.ReportCanvas;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class UsbDescriptorsTree {
    public UsbDescriptorsConfigNode mConfigNode;
    public UsbDescriptorsDeviceNode mDeviceNode;
    public UsbDescriptorsInterfaceNode mInterfaceNode;

    public final void addDeviceDescriptor(UsbDeviceDescriptor usbDeviceDescriptor) {
        this.mDeviceNode = new UsbDescriptorsDeviceNode(usbDeviceDescriptor);
    }

    public final void addConfigDescriptor(UsbConfigDescriptor usbConfigDescriptor) {
        UsbDescriptorsConfigNode usbDescriptorsConfigNode = new UsbDescriptorsConfigNode(usbConfigDescriptor);
        this.mConfigNode = usbDescriptorsConfigNode;
        this.mDeviceNode.addConfigDescriptorNode(usbDescriptorsConfigNode);
    }

    public final void addInterfaceDescriptor(UsbInterfaceDescriptor usbInterfaceDescriptor) {
        UsbDescriptorsInterfaceNode usbDescriptorsInterfaceNode = new UsbDescriptorsInterfaceNode(usbInterfaceDescriptor);
        this.mInterfaceNode = usbDescriptorsInterfaceNode;
        this.mConfigNode.addInterfaceNode(usbDescriptorsInterfaceNode);
    }

    public final void addEndpointDescriptor(UsbEndpointDescriptor usbEndpointDescriptor) {
        this.mInterfaceNode.addEndpointNode(new UsbDescriptorsEndpointNode(usbEndpointDescriptor));
    }

    public void parse(UsbDescriptorParser usbDescriptorParser) {
        ArrayList<UsbDescriptor> descriptors = usbDescriptorParser.getDescriptors();
        for (int i = 0; i < descriptors.size(); i++) {
            UsbDescriptor usbDescriptor = descriptors.get(i);
            byte type = usbDescriptor.getType();
            if (type == 1) {
                addDeviceDescriptor((UsbDeviceDescriptor) usbDescriptor);
            } else if (type == 2) {
                addConfigDescriptor((UsbConfigDescriptor) usbDescriptor);
            } else if (type == 4) {
                addInterfaceDescriptor((UsbInterfaceDescriptor) usbDescriptor);
            } else if (type == 5) {
                addEndpointDescriptor((UsbEndpointDescriptor) usbDescriptor);
            }
        }
    }

    public void report(ReportCanvas reportCanvas) {
        this.mDeviceNode.report(reportCanvas);
    }
}
