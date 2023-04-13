package com.android.server.usb.descriptors;

import android.hardware.usb.UsbEndpoint;
import com.android.server.usb.descriptors.report.ReportCanvas;
/* loaded from: classes2.dex */
public class UsbEndpointDescriptor extends UsbDescriptor {
    public int mAttributes;
    public UsbDescriptor mClassSpecificEndpointDescriptor;
    public int mEndpointAddress;
    public int mInterval;
    public int mPacketSize;
    public byte mRefresh;
    public byte mSyncAddress;

    public UsbEndpointDescriptor(int i, byte b) {
        super(i, b);
        this.mHierarchyLevel = 4;
    }

    public int getEndpointAddress() {
        return this.mEndpointAddress & 15;
    }

    public int getAttributes() {
        return this.mAttributes;
    }

    public int getPacketSize() {
        return this.mPacketSize;
    }

    public int getInterval() {
        return this.mInterval;
    }

    public int getDirection() {
        return this.mEndpointAddress & (-128);
    }

    public void setClassSpecificEndpointDescriptor(UsbDescriptor usbDescriptor) {
        this.mClassSpecificEndpointDescriptor = usbDescriptor;
    }

    public UsbDescriptor getClassSpecificEndpointDescriptor() {
        return this.mClassSpecificEndpointDescriptor;
    }

    public UsbEndpoint toAndroid(UsbDescriptorParser usbDescriptorParser) {
        return new UsbEndpoint(this.mEndpointAddress, this.mAttributes, this.mPacketSize, this.mInterval);
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mEndpointAddress = byteStream.getUnsignedByte();
        this.mAttributes = byteStream.getUnsignedByte();
        this.mPacketSize = byteStream.unpackUsbShort();
        this.mInterval = byteStream.getUnsignedByte();
        if (this.mLength == 9) {
            this.mRefresh = byteStream.getByte();
            this.mSyncAddress = byteStream.getByte();
        }
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        StringBuilder sb = new StringBuilder();
        sb.append("Address: ");
        sb.append(ReportCanvas.getHexString(getEndpointAddress()));
        sb.append(getDirection() == 0 ? " [out]" : " [in]");
        reportCanvas.writeListItem(sb.toString());
        int attributes = getAttributes();
        reportCanvas.openListItem();
        reportCanvas.write("Attributes: " + ReportCanvas.getHexString(attributes) + " ");
        int i = attributes & 3;
        if (i == 0) {
            reportCanvas.write("Control");
        } else if (i == 1) {
            reportCanvas.write("Iso");
        } else if (i == 2) {
            reportCanvas.write("Bulk");
        } else if (i == 3) {
            reportCanvas.write("Interrupt");
        }
        reportCanvas.closeListItem();
        if (i == 1) {
            reportCanvas.openListItem();
            reportCanvas.write("Aync: ");
            int i2 = attributes & 12;
            if (i2 == 0) {
                reportCanvas.write("NONE");
            } else if (i2 == 4) {
                reportCanvas.write("ASYNC");
            } else if (i2 == 8) {
                reportCanvas.write("ADAPTIVE ASYNC");
            }
            reportCanvas.closeListItem();
            reportCanvas.openListItem();
            reportCanvas.write("Useage: ");
            int i3 = attributes & 48;
            if (i3 == 0) {
                reportCanvas.write("DATA");
            } else if (i3 == 16) {
                reportCanvas.write("FEEDBACK");
            } else if (i3 == 32) {
                reportCanvas.write("EXPLICIT FEEDBACK");
            } else if (i3 == 48) {
                reportCanvas.write("RESERVED");
            }
            reportCanvas.closeListItem();
        }
        reportCanvas.writeListItem("Package Size: " + getPacketSize());
        reportCanvas.writeListItem("Interval: " + getInterval());
        reportCanvas.closeList();
    }
}
