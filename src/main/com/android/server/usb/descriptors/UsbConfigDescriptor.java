package com.android.server.usb.descriptors;

import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbInterface;
import com.android.server.usb.descriptors.report.ReportCanvas;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public final class UsbConfigDescriptor extends UsbDescriptor {
    public int mAttribs;
    public boolean mBlockAudio;
    public byte mConfigIndex;
    public int mConfigValue;
    public ArrayList<UsbInterfaceDescriptor> mInterfaceDescriptors;
    public int mMaxPower;
    public byte mNumInterfaces;
    public int mTotalLength;

    public UsbConfigDescriptor(int i, byte b) {
        super(i, b);
        this.mInterfaceDescriptors = new ArrayList<>();
        this.mHierarchyLevel = 2;
    }

    public byte getNumInterfaces() {
        return this.mNumInterfaces;
    }

    public int getConfigValue() {
        return this.mConfigValue;
    }

    public int getAttribs() {
        return this.mAttribs;
    }

    public void addInterfaceDescriptor(UsbInterfaceDescriptor usbInterfaceDescriptor) {
        this.mInterfaceDescriptors.add(usbInterfaceDescriptor);
    }

    public ArrayList<UsbInterfaceDescriptor> getInterfaceDescriptors() {
        return this.mInterfaceDescriptors;
    }

    public final boolean isAudioInterface(UsbInterfaceDescriptor usbInterfaceDescriptor) {
        return usbInterfaceDescriptor.getUsbClass() == 1 && usbInterfaceDescriptor.getUsbSubclass() == 2;
    }

    public UsbConfiguration toAndroid(UsbDescriptorParser usbDescriptorParser) {
        UsbConfiguration usbConfiguration = new UsbConfiguration(this.mConfigValue, usbDescriptorParser.getDescriptorString(this.mConfigIndex), this.mAttribs, this.mMaxPower);
        ArrayList arrayList = new ArrayList();
        Iterator<UsbInterfaceDescriptor> it = this.mInterfaceDescriptors.iterator();
        while (it.hasNext()) {
            UsbInterfaceDescriptor next = it.next();
            if (!this.mBlockAudio || !isAudioInterface(next)) {
                arrayList.add(next.toAndroid(usbDescriptorParser));
            }
        }
        usbConfiguration.setInterfaces((UsbInterface[]) arrayList.toArray(new UsbInterface[0]));
        return usbConfiguration;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public int parseRawDescriptors(ByteStream byteStream) {
        this.mTotalLength = byteStream.unpackUsbShort();
        this.mNumInterfaces = byteStream.getByte();
        this.mConfigValue = byteStream.getUnsignedByte();
        this.mConfigIndex = byteStream.getByte();
        this.mAttribs = byteStream.getUnsignedByte();
        this.mMaxPower = byteStream.getUnsignedByte();
        return this.mLength;
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        reportCanvas.openList();
        reportCanvas.writeListItem("Config # " + getConfigValue());
        reportCanvas.writeListItem(((int) getNumInterfaces()) + " Interfaces.");
        reportCanvas.writeListItem("Attributes: " + ReportCanvas.getHexString(getAttribs()));
        reportCanvas.closeList();
    }
}
