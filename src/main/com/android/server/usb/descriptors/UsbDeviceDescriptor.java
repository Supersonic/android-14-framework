package com.android.server.usb.descriptors;

import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.usb.descriptors.report.ReportCanvas;
import com.android.server.usb.descriptors.report.UsbStrings;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class UsbDeviceDescriptor extends UsbDescriptor {
    public ArrayList<UsbConfigDescriptor> mConfigDescriptors;
    public int mDevClass;
    public int mDevSubClass;
    public int mDeviceRelease;
    public byte mMfgIndex;
    public byte mNumConfigs;
    public byte mPacketSize;
    public int mProductID;
    public byte mProductIndex;
    public int mProtocol;
    public byte mSerialIndex;
    public int mSpec;
    public int mVendorID;

    public UsbDeviceDescriptor(int i, byte b) {
        super(i, b);
        this.mConfigDescriptors = new ArrayList<>();
        this.mHierarchyLevel = 1;
    }

    public int getSpec() {
        return this.mSpec;
    }

    public int getDevClass() {
        return this.mDevClass;
    }

    public int getDevSubClass() {
        return this.mDevSubClass;
    }

    public int getVendorID() {
        return this.mVendorID;
    }

    public int getProductID() {
        return this.mProductID;
    }

    public int getDeviceRelease() {
        return this.mDeviceRelease;
    }

    public String getDeviceReleaseString() {
        int i = this.mDeviceRelease;
        int i2 = i & 15;
        return String.format("%d.%d%d", Integer.valueOf((((i & 61440) >> 12) * 10) + ((i & 3840) >> 8)), Integer.valueOf((i & FrameworkStatsLog.BOOT_TIME_EVENT_ELAPSED_TIME_REPORTED) >> 4), Integer.valueOf(i2));
    }

    public byte getMfgIndex() {
        return this.mMfgIndex;
    }

    public String getMfgString(UsbDescriptorParser usbDescriptorParser) {
        return usbDescriptorParser.getDescriptorString(this.mMfgIndex);
    }

    public byte getProductIndex() {
        return this.mProductIndex;
    }

    public String getProductString(UsbDescriptorParser usbDescriptorParser) {
        return usbDescriptorParser.getDescriptorString(this.mProductIndex);
    }

    public String getSerialString(UsbDescriptorParser usbDescriptorParser) {
        return usbDescriptorParser.getDescriptorString(this.mSerialIndex);
    }

    public void addConfigDescriptor(UsbConfigDescriptor usbConfigDescriptor) {
        this.mConfigDescriptors.add(usbConfigDescriptor);
    }

    public UsbDevice.Builder toAndroid(UsbDescriptorParser usbDescriptorParser) {
        String mfgString = getMfgString(usbDescriptorParser);
        String productString = getProductString(usbDescriptorParser);
        String deviceReleaseString = getDeviceReleaseString();
        String serialString = getSerialString(usbDescriptorParser);
        int size = this.mConfigDescriptors.size();
        UsbConfiguration[] usbConfigurationArr = new UsbConfiguration[size];
        Log.d("UsbDeviceDescriptor", "  " + size + " configs");
        for (int i = 0; i < this.mConfigDescriptors.size(); i++) {
            usbConfigurationArr[i] = this.mConfigDescriptors.get(i).toAndroid(usbDescriptorParser);
        }
        return new UsbDevice.Builder(usbDescriptorParser.getDeviceAddr(), this.mVendorID, this.mProductID, this.mDevClass, this.mDevSubClass, this.mProtocol, mfgString, productString, deviceReleaseString, usbConfigurationArr, serialString, usbDescriptorParser.hasAudioPlayback(), usbDescriptorParser.hasAudioCapture(), usbDescriptorParser.hasMIDIInterface(), usbDescriptorParser.hasVideoPlayback(), usbDescriptorParser.hasVideoCapture());
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mSpec = byteStream.unpackUsbShort();
        this.mDevClass = byteStream.getUnsignedByte();
        this.mDevSubClass = byteStream.getUnsignedByte();
        this.mProtocol = byteStream.getUnsignedByte();
        this.mPacketSize = byteStream.getByte();
        this.mVendorID = byteStream.unpackUsbShort();
        this.mProductID = byteStream.unpackUsbShort();
        this.mDeviceRelease = byteStream.unpackUsbShort();
        this.mMfgIndex = byteStream.getByte();
        this.mProductIndex = byteStream.getByte();
        this.mSerialIndex = byteStream.getByte();
        this.mNumConfigs = byteStream.getByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        int spec = getSpec();
        reportCanvas.writeListItem("Spec: " + ReportCanvas.getBCDString(spec));
        int devClass = getDevClass();
        String className = UsbStrings.getClassName(devClass);
        int devSubClass = getDevSubClass();
        String className2 = UsbStrings.getClassName(devSubClass);
        reportCanvas.writeListItem("Class " + devClass + ": " + className + " Subclass" + devSubClass + ": " + className2);
        StringBuilder sb = new StringBuilder();
        sb.append("Vendor ID: ");
        sb.append(ReportCanvas.getHexString(getVendorID()));
        sb.append(" Product ID: ");
        sb.append(ReportCanvas.getHexString(getProductID()));
        sb.append(" Product Release: ");
        sb.append(ReportCanvas.getBCDString(getDeviceRelease()));
        reportCanvas.writeListItem(sb.toString());
        UsbDescriptorParser parser = reportCanvas.getParser();
        byte mfgIndex = getMfgIndex();
        String descriptorString = parser.getDescriptorString(mfgIndex);
        byte productIndex = getProductIndex();
        String descriptorString2 = parser.getDescriptorString(productIndex);
        reportCanvas.writeListItem("Manufacturer " + ((int) mfgIndex) + ": " + descriptorString + " Product " + ((int) productIndex) + ": " + descriptorString2);
        reportCanvas.closeList();
    }
}
