package com.android.server.usb.descriptors;

import android.util.Log;
import com.android.server.usb.descriptors.report.ReportCanvas;
import com.android.server.usb.descriptors.report.UsbStrings;
/* loaded from: classes2.dex */
public abstract class UsbACInterface extends UsbDescriptor {
    public final int mSubclass;
    public final byte mSubtype;

    public UsbACInterface(int i, byte b, byte b2, int i2) {
        super(i, b);
        this.mSubtype = b2;
        this.mSubclass = i2;
    }

    public byte getSubtype() {
        return this.mSubtype;
    }

    public int getSubclass() {
        return this.mSubclass;
    }

    public static UsbDescriptor allocAudioControlDescriptor(UsbDescriptorParser usbDescriptorParser, ByteStream byteStream, int i, byte b, byte b2, int i2) {
        switch (b2) {
            case 1:
                int unpackUsbShort = byteStream.unpackUsbShort();
                usbDescriptorParser.setACInterfaceSpec(unpackUsbShort);
                if (unpackUsbShort == 512) {
                    return new Usb20ACHeader(i, b, b2, i2, unpackUsbShort);
                }
                return new Usb10ACHeader(i, b, b2, i2, unpackUsbShort);
            case 2:
                if (usbDescriptorParser.getACInterfaceSpec() == 512) {
                    return new Usb20ACInputTerminal(i, b, b2, i2);
                }
                return new Usb10ACInputTerminal(i, b, b2, i2);
            case 3:
                if (usbDescriptorParser.getACInterfaceSpec() == 512) {
                    return new Usb20ACOutputTerminal(i, b, b2, i2);
                }
                return new Usb10ACOutputTerminal(i, b, b2, i2);
            case 4:
                if (usbDescriptorParser.getACInterfaceSpec() == 512) {
                    return new Usb20ACMixerUnit(i, b, b2, i2);
                }
                return new Usb10ACMixerUnit(i, b, b2, i2);
            case 5:
                return new UsbACSelectorUnit(i, b, b2, i2);
            case 6:
                return new UsbACFeatureUnit(i, b, b2, i2);
            default:
                Log.w("UsbACInterface", "Unknown Audio Class Interface subtype:0x" + Integer.toHexString(b2));
                return new UsbACInterfaceUnparsed(i, b, b2, i2);
        }
    }

    public static UsbDescriptor allocAudioStreamingDescriptor(UsbDescriptorParser usbDescriptorParser, ByteStream byteStream, int i, byte b, byte b2, int i2) {
        int aCInterfaceSpec = usbDescriptorParser.getACInterfaceSpec();
        if (b2 == 1) {
            if (aCInterfaceSpec == 512) {
                return new Usb20ASGeneral(i, b, b2, i2);
            }
            return new Usb10ASGeneral(i, b, b2, i2);
        } else if (b2 == 2) {
            return UsbASFormat.allocDescriptor(usbDescriptorParser, byteStream, i, b, b2, i2);
        } else {
            Log.w("UsbACInterface", "Unknown Audio Streaming Interface subtype:0x" + Integer.toHexString(b2));
            return null;
        }
    }

    public static UsbDescriptor allocMidiStreamingDescriptor(int i, byte b, byte b2, int i2) {
        if (b2 != 1) {
            if (b2 != 2) {
                if (b2 == 3) {
                    return new UsbMSMidiOutputJack(i, b, b2, i2);
                }
                Log.w("UsbACInterface", "Unknown MIDI Streaming Interface subtype:0x" + Integer.toHexString(b2));
                return null;
            }
            return new UsbMSMidiInputJack(i, b, b2, i2);
        }
        return new UsbMSMidiHeader(i, b, b2, i2);
    }

    public static UsbDescriptor allocDescriptor(UsbDescriptorParser usbDescriptorParser, ByteStream byteStream, int i, byte b) {
        byte b2 = byteStream.getByte();
        int usbSubclass = usbDescriptorParser.getCurInterface().getUsbSubclass();
        if (usbSubclass != 1) {
            if (usbSubclass != 2) {
                if (usbSubclass == 3) {
                    return allocMidiStreamingDescriptor(i, b, b2, usbSubclass);
                }
                Log.w("UsbACInterface", "Unknown Audio Class Interface Subclass: 0x" + Integer.toHexString(usbSubclass));
                return null;
            }
            return allocAudioStreamingDescriptor(usbDescriptorParser, byteStream, i, b, b2, usbSubclass);
        }
        return allocAudioControlDescriptor(usbDescriptorParser, byteStream, i, b, b2, usbSubclass);
    }

    @Override // com.android.server.usb.descriptors.UsbDescriptor
    public void report(ReportCanvas reportCanvas) {
        super.report(reportCanvas);
        int subclass = getSubclass();
        String aCInterfaceSubclassName = UsbStrings.getACInterfaceSubclassName(subclass);
        byte subtype = getSubtype();
        String aCControlInterfaceName = UsbStrings.getACControlInterfaceName(subtype);
        reportCanvas.openList();
        reportCanvas.writeListItem("Subclass: " + ReportCanvas.getHexString(subclass) + " " + aCInterfaceSubclassName);
        reportCanvas.writeListItem("Subtype: " + ReportCanvas.getHexString(subtype) + " " + aCControlInterfaceName);
        reportCanvas.closeList();
    }
}
