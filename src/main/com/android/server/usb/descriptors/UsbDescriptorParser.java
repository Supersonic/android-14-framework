package com.android.server.usb.descriptors;

import android.hardware.usb.UsbDevice;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public final class UsbDescriptorParser {
    public UsbConfigDescriptor mCurConfigDescriptor;
    public UsbEndpointDescriptor mCurEndpointDescriptor;
    public UsbInterfaceDescriptor mCurInterfaceDescriptor;
    public final String mDeviceAddr;
    public UsbDeviceDescriptor mDeviceDescriptor;
    public int mACInterfacesSpec = 256;
    public int mVCInterfacesSpec = 256;
    public final ArrayList<UsbDescriptor> mDescriptors = new ArrayList<>(128);

    private native String getDescriptorString_native(String str, int i);

    private native byte[] getRawDescriptors_native(String str);

    public UsbDescriptorParser(String str, byte[] bArr) {
        this.mDeviceAddr = str;
        parseDescriptors(bArr);
    }

    public String getDeviceAddr() {
        return this.mDeviceAddr;
    }

    public void setACInterfaceSpec(int i) {
        this.mACInterfacesSpec = i;
    }

    public int getACInterfaceSpec() {
        return this.mACInterfacesSpec;
    }

    public void setVCInterfaceSpec(int i) {
        this.mVCInterfacesSpec = i;
    }

    /* loaded from: classes2.dex */
    public class UsbDescriptorsStreamFormatException extends Exception {
        String mMessage;

        public UsbDescriptorsStreamFormatException(String str) {
            this.mMessage = str;
        }

        @Override // java.lang.Throwable
        public String toString() {
            return "Descriptor Stream Format Exception: " + this.mMessage;
        }
    }

    public final UsbDescriptor allocDescriptor(ByteStream byteStream) throws UsbDescriptorsStreamFormatException {
        UsbEndpointDescriptor usbEndpointDescriptor;
        UsbInterfaceDescriptor usbInterfaceDescriptor;
        UsbDescriptor allocDescriptor;
        UsbEndpointDescriptor usbEndpointDescriptor2;
        byteStream.resetReadCount();
        int unsignedByte = byteStream.getUnsignedByte();
        byte b = byteStream.getByte();
        UsbDescriptor.logDescriptorName(b, unsignedByte);
        if (b == 1) {
            UsbDeviceDescriptor usbDeviceDescriptor = new UsbDeviceDescriptor(unsignedByte, b);
            this.mDeviceDescriptor = usbDeviceDescriptor;
            usbEndpointDescriptor = usbDeviceDescriptor;
        } else if (b == 2) {
            UsbConfigDescriptor usbConfigDescriptor = new UsbConfigDescriptor(unsignedByte, b);
            this.mCurConfigDescriptor = usbConfigDescriptor;
            UsbDeviceDescriptor usbDeviceDescriptor2 = this.mDeviceDescriptor;
            if (usbDeviceDescriptor2 != null) {
                usbDeviceDescriptor2.addConfigDescriptor(usbConfigDescriptor);
                usbEndpointDescriptor = usbConfigDescriptor;
            } else {
                Log.e("UsbDescriptorParser", "Config Descriptor found with no associated Device Descriptor!");
                throw new UsbDescriptorsStreamFormatException("Config Descriptor found with no associated Device Descriptor!");
            }
        } else if (b == 4) {
            UsbInterfaceDescriptor usbInterfaceDescriptor2 = new UsbInterfaceDescriptor(unsignedByte, b);
            this.mCurInterfaceDescriptor = usbInterfaceDescriptor2;
            UsbConfigDescriptor usbConfigDescriptor2 = this.mCurConfigDescriptor;
            if (usbConfigDescriptor2 != null) {
                usbConfigDescriptor2.addInterfaceDescriptor(usbInterfaceDescriptor2);
                usbEndpointDescriptor = usbInterfaceDescriptor2;
            } else {
                Log.e("UsbDescriptorParser", "Interface Descriptor found with no associated Config Descriptor!");
                throw new UsbDescriptorsStreamFormatException("Interface Descriptor found with no associated Config Descriptor!");
            }
        } else if (b == 5) {
            UsbEndpointDescriptor usbEndpointDescriptor3 = new UsbEndpointDescriptor(unsignedByte, b);
            this.mCurEndpointDescriptor = usbEndpointDescriptor3;
            UsbInterfaceDescriptor usbInterfaceDescriptor3 = this.mCurInterfaceDescriptor;
            if (usbInterfaceDescriptor3 != null) {
                usbInterfaceDescriptor3.addEndpointDescriptor(usbEndpointDescriptor3);
                usbEndpointDescriptor = usbEndpointDescriptor3;
            } else {
                Log.e("UsbDescriptorParser", "Endpoint Descriptor found with no associated Interface Descriptor!");
                throw new UsbDescriptorsStreamFormatException("Endpoint Descriptor found with no associated Interface Descriptor!");
            }
        } else if (b == 11) {
            usbEndpointDescriptor = new UsbInterfaceAssoc(unsignedByte, b);
        } else if (b != 33) {
            usbEndpointDescriptor = null;
            r7 = null;
            UsbDescriptor usbDescriptor = null;
            usbEndpointDescriptor = null;
            usbEndpointDescriptor = null;
            usbEndpointDescriptor = null;
            usbEndpointDescriptor = null;
            if (b == 36) {
                UsbInterfaceDescriptor usbInterfaceDescriptor4 = this.mCurInterfaceDescriptor;
                if (usbInterfaceDescriptor4 != null) {
                    int usbClass = usbInterfaceDescriptor4.getUsbClass();
                    if (usbClass == 1) {
                        UsbDescriptor allocDescriptor2 = UsbACInterface.allocDescriptor(this, byteStream, unsignedByte, b);
                        boolean z = allocDescriptor2 instanceof UsbMSMidiHeader;
                        usbEndpointDescriptor = allocDescriptor2;
                        if (z) {
                            this.mCurInterfaceDescriptor.setMidiHeaderInterfaceDescriptor(allocDescriptor2);
                            usbEndpointDescriptor = allocDescriptor2;
                        }
                    } else if (usbClass == 14) {
                        usbEndpointDescriptor = UsbVCInterface.allocDescriptor(this, byteStream, unsignedByte, b);
                    } else if (usbClass != 16) {
                        Log.w("UsbDescriptorParser", "  Unparsed Class-specific");
                    }
                }
            } else if (b == 37 && (usbInterfaceDescriptor = this.mCurInterfaceDescriptor) != null) {
                int usbClass2 = usbInterfaceDescriptor.getUsbClass();
                if (usbClass2 == 1) {
                    allocDescriptor = UsbACEndpoint.allocDescriptor(this, unsignedByte, b, byteStream.getByte());
                } else if (usbClass2 == 14) {
                    allocDescriptor = UsbVCEndpoint.allocDescriptor(this, unsignedByte, b, byteStream.getByte());
                } else {
                    if (usbClass2 != 16) {
                        Log.w("UsbDescriptorParser", "  Unparsed Class-specific Endpoint:0x" + Integer.toHexString(usbClass2));
                    }
                    usbEndpointDescriptor2 = this.mCurEndpointDescriptor;
                    usbEndpointDescriptor = usbDescriptor;
                    usbEndpointDescriptor = usbDescriptor;
                    if (usbEndpointDescriptor2 != null && usbDescriptor != null) {
                        usbEndpointDescriptor2.setClassSpecificEndpointDescriptor(usbDescriptor);
                        usbEndpointDescriptor = usbDescriptor;
                    }
                }
                usbDescriptor = allocDescriptor;
                usbEndpointDescriptor2 = this.mCurEndpointDescriptor;
                usbEndpointDescriptor = usbDescriptor;
                usbEndpointDescriptor = usbDescriptor;
                if (usbEndpointDescriptor2 != null) {
                    usbEndpointDescriptor2.setClassSpecificEndpointDescriptor(usbDescriptor);
                    usbEndpointDescriptor = usbDescriptor;
                }
            }
        } else {
            usbEndpointDescriptor = new UsbHIDDescriptor(unsignedByte, b);
        }
        return usbEndpointDescriptor == null ? new UsbUnknown(unsignedByte, b) : usbEndpointDescriptor;
    }

    public UsbDeviceDescriptor getDeviceDescriptor() {
        return this.mDeviceDescriptor;
    }

    public UsbInterfaceDescriptor getCurInterface() {
        return this.mCurInterfaceDescriptor;
    }

    public void parseDescriptors(byte[] bArr) {
        UsbDescriptor usbDescriptor;
        ByteStream byteStream = new ByteStream(bArr);
        while (byteStream.available() > 0) {
            try {
                usbDescriptor = allocDescriptor(byteStream);
            } catch (Exception e) {
                Log.e("UsbDescriptorParser", "Exception allocating USB descriptor.", e);
                usbDescriptor = null;
            }
            if (usbDescriptor != null) {
                try {
                    try {
                        usbDescriptor.parseRawDescriptors(byteStream);
                        usbDescriptor.postParse(byteStream);
                    } catch (Exception e2) {
                        usbDescriptor.postParse(byteStream);
                        Log.w("UsbDescriptorParser", "Exception parsing USB descriptors. type:0x" + ((int) usbDescriptor.getType()) + " status:" + usbDescriptor.getStatus());
                        StackTraceElement[] stackTrace = e2.getStackTrace();
                        if (stackTrace.length > 0) {
                            Log.i("UsbDescriptorParser", "  class:" + stackTrace[0].getClassName() + " @ " + stackTrace[0].getLineNumber());
                        }
                        if (stackTrace.length > 1) {
                            Log.i("UsbDescriptorParser", "  class:" + stackTrace[1].getClassName() + " @ " + stackTrace[1].getLineNumber());
                        }
                        usbDescriptor.setStatus(4);
                    }
                } finally {
                    this.mDescriptors.add(usbDescriptor);
                }
            }
        }
    }

    public byte[] getRawDescriptors() {
        return getRawDescriptors_native(this.mDeviceAddr);
    }

    public String getDescriptorString(int i) {
        return getDescriptorString_native(this.mDeviceAddr, i);
    }

    public ArrayList<UsbDescriptor> getDescriptors() {
        return this.mDescriptors;
    }

    public UsbDevice.Builder toAndroidUsbDeviceBuilder() {
        UsbDeviceDescriptor usbDeviceDescriptor = this.mDeviceDescriptor;
        if (usbDeviceDescriptor == null) {
            Log.e("UsbDescriptorParser", "toAndroidUsbDevice() ERROR - No Device Descriptor");
            return null;
        }
        UsbDevice.Builder android2 = usbDeviceDescriptor.toAndroid(this);
        if (android2 == null) {
            Log.e("UsbDescriptorParser", "toAndroidUsbDevice() ERROR Creating Device");
        }
        return android2;
    }

    public ArrayList<UsbDescriptor> getInterfaceDescriptorsForClass(int i) {
        ArrayList<UsbDescriptor> arrayList = new ArrayList<>();
        Iterator<UsbDescriptor> it = this.mDescriptors.iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next.getType() == 4) {
                if (next instanceof UsbInterfaceDescriptor) {
                    if (((UsbInterfaceDescriptor) next).getUsbClass() == i) {
                        arrayList.add(next);
                    }
                } else {
                    Log.w("UsbDescriptorParser", "Unrecognized Interface l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
                }
            }
        }
        return arrayList;
    }

    public ArrayList<UsbDescriptor> getACInterfaceDescriptors(byte b, int i) {
        ArrayList<UsbDescriptor> arrayList = new ArrayList<>();
        Iterator<UsbDescriptor> it = this.mDescriptors.iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next.getType() == 36) {
                if (next instanceof UsbACInterface) {
                    UsbACInterface usbACInterface = (UsbACInterface) next;
                    if (usbACInterface.getSubtype() == b && usbACInterface.getSubclass() == i) {
                        arrayList.add(next);
                    }
                } else {
                    Log.w("UsbDescriptorParser", "Unrecognized Audio Interface len: " + next.getLength() + " type:0x" + Integer.toHexString(next.getType()));
                }
            }
        }
        return arrayList;
    }

    public boolean hasInput() {
        Iterator<UsbDescriptor> it = getACInterfaceDescriptors((byte) 2, 1).iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbACTerminal) {
                int terminalType = ((UsbACTerminal) next).getTerminalType() & (-256);
                if (terminalType != 256 && terminalType != 768) {
                    return true;
                }
            } else {
                Log.w("UsbDescriptorParser", "Undefined Audio Input terminal l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
            }
        }
        return false;
    }

    public boolean hasOutput() {
        Iterator<UsbDescriptor> it = getACInterfaceDescriptors((byte) 3, 1).iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbACTerminal) {
                int terminalType = ((UsbACTerminal) next).getTerminalType() & (-256);
                if (terminalType != 256 && terminalType != 512) {
                    return true;
                }
            } else {
                Log.w("UsbDescriptorParser", "Undefined Audio Input terminal l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
            }
        }
        return false;
    }

    public boolean hasMic() {
        Iterator<UsbDescriptor> it = getACInterfaceDescriptors((byte) 2, 1).iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbACTerminal) {
                UsbACTerminal usbACTerminal = (UsbACTerminal) next;
                if (usbACTerminal.getTerminalType() == 513 || usbACTerminal.getTerminalType() == 1026 || usbACTerminal.getTerminalType() == 1024 || usbACTerminal.getTerminalType() == 1539) {
                    return true;
                }
            } else {
                Log.w("UsbDescriptorParser", "Undefined Audio Input terminal l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
            }
        }
        return false;
    }

    public boolean hasSpeaker() {
        Iterator<UsbDescriptor> it = getACInterfaceDescriptors((byte) 3, 1).iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbACTerminal) {
                UsbACTerminal usbACTerminal = (UsbACTerminal) next;
                if (usbACTerminal.getTerminalType() == 769 || usbACTerminal.getTerminalType() == 770 || usbACTerminal.getTerminalType() == 1026) {
                    return true;
                }
            } else {
                Log.w("UsbDescriptorParser", "Undefined Audio Output terminal l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
            }
        }
        return false;
    }

    public boolean hasAudioInterface() {
        return !getInterfaceDescriptorsForClass(1).isEmpty();
    }

    public boolean hasAudioTerminal(int i) {
        Iterator<UsbDescriptor> it = this.mDescriptors.iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbACInterface) {
                UsbACInterface usbACInterface = (UsbACInterface) next;
                if (usbACInterface.getSubclass() == 1 && usbACInterface.getSubtype() == i) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAudioPlayback() {
        return hasAudioTerminal(3);
    }

    public boolean hasAudioCapture() {
        return hasAudioTerminal(2);
    }

    public boolean hasVideoCapture() {
        Iterator<UsbDescriptor> it = this.mDescriptors.iterator();
        while (it.hasNext()) {
            if (it.next() instanceof UsbVCInputTerminal) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVideoPlayback() {
        Iterator<UsbDescriptor> it = this.mDescriptors.iterator();
        while (it.hasNext()) {
            if (it.next() instanceof UsbVCOutputTerminal) {
                return true;
            }
        }
        return false;
    }

    public boolean hasHIDInterface() {
        return !getInterfaceDescriptorsForClass(3).isEmpty();
    }

    public boolean hasStorageInterface() {
        return !getInterfaceDescriptorsForClass(8).isEmpty();
    }

    public boolean hasMIDIInterface() {
        Iterator<UsbDescriptor> it = getInterfaceDescriptorsForClass(1).iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbInterfaceDescriptor) {
                if (((UsbInterfaceDescriptor) next).getUsbSubclass() == 3) {
                    return true;
                }
            } else {
                Log.w("UsbDescriptorParser", "Undefined Audio Class Interface l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
            }
        }
        return false;
    }

    public boolean containsUniversalMidiDeviceEndpoint() {
        return doesInterfaceContainEndpoint(findUniversalMidiInterfaceDescriptors());
    }

    public boolean containsLegacyMidiDeviceEndpoint() {
        return doesInterfaceContainEndpoint(findLegacyMidiInterfaceDescriptors());
    }

    public boolean doesInterfaceContainEndpoint(ArrayList<UsbInterfaceDescriptor> arrayList) {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            UsbInterfaceDescriptor usbInterfaceDescriptor = arrayList.get(i3);
            for (int i4 = 0; i4 < usbInterfaceDescriptor.getNumEndpoints(); i4++) {
                if (usbInterfaceDescriptor.getEndpointDescriptor(i4).getDirection() == 0) {
                    i++;
                } else {
                    i2++;
                }
            }
        }
        return i > 0 || i2 > 0;
    }

    public ArrayList<UsbInterfaceDescriptor> findUniversalMidiInterfaceDescriptors() {
        return findMidiInterfaceDescriptors(512);
    }

    public ArrayList<UsbInterfaceDescriptor> findLegacyMidiInterfaceDescriptors() {
        return findMidiInterfaceDescriptors(256);
    }

    public final ArrayList<UsbInterfaceDescriptor> findMidiInterfaceDescriptors(int i) {
        UsbDescriptor midiHeaderInterfaceDescriptor;
        ArrayList<UsbDescriptor> interfaceDescriptorsForClass = getInterfaceDescriptorsForClass(1);
        ArrayList<UsbInterfaceDescriptor> arrayList = new ArrayList<>();
        Iterator<UsbDescriptor> it = interfaceDescriptorsForClass.iterator();
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbInterfaceDescriptor) {
                UsbInterfaceDescriptor usbInterfaceDescriptor = (UsbInterfaceDescriptor) next;
                if (usbInterfaceDescriptor.getUsbSubclass() == 3 && (midiHeaderInterfaceDescriptor = usbInterfaceDescriptor.getMidiHeaderInterfaceDescriptor()) != null && (midiHeaderInterfaceDescriptor instanceof UsbMSMidiHeader) && ((UsbMSMidiHeader) midiHeaderInterfaceDescriptor).getMidiStreamingClass() == i) {
                    arrayList.add(usbInterfaceDescriptor);
                }
            } else {
                Log.w("UsbDescriptorParser", "Undefined Audio Class Interface l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
            }
        }
        return arrayList;
    }

    public int calculateMidiInterfaceDescriptorsCount() {
        UsbDescriptor midiHeaderInterfaceDescriptor;
        Iterator<UsbDescriptor> it = getInterfaceDescriptorsForClass(1).iterator();
        int i = 0;
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbInterfaceDescriptor) {
                UsbInterfaceDescriptor usbInterfaceDescriptor = (UsbInterfaceDescriptor) next;
                if (usbInterfaceDescriptor.getUsbSubclass() == 3 && (midiHeaderInterfaceDescriptor = usbInterfaceDescriptor.getMidiHeaderInterfaceDescriptor()) != null && (midiHeaderInterfaceDescriptor instanceof UsbMSMidiHeader)) {
                    UsbMSMidiHeader usbMSMidiHeader = (UsbMSMidiHeader) midiHeaderInterfaceDescriptor;
                    i++;
                }
            } else {
                Log.w("UsbDescriptorParser", "Undefined Audio Class Interface l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
            }
        }
        return i;
    }

    public final int calculateNumLegacyMidiPorts(boolean z) {
        UsbConfigDescriptor usbConfigDescriptor;
        UsbDescriptor classSpecificEndpointDescriptor;
        UsbDescriptor midiHeaderInterfaceDescriptor;
        Iterator<UsbDescriptor> it = this.mDescriptors.iterator();
        while (true) {
            if (!it.hasNext()) {
                usbConfigDescriptor = null;
                break;
            }
            UsbDescriptor next = it.next();
            if (next.getType() == 2) {
                if (next instanceof UsbConfigDescriptor) {
                    usbConfigDescriptor = (UsbConfigDescriptor) next;
                    break;
                }
                Log.w("UsbDescriptorParser", "Unrecognized Config l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
            }
        }
        if (usbConfigDescriptor == null) {
            Log.w("UsbDescriptorParser", "Config not found");
            return 0;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<UsbInterfaceDescriptor> it2 = usbConfigDescriptor.getInterfaceDescriptors().iterator();
        while (it2.hasNext()) {
            UsbInterfaceDescriptor next2 = it2.next();
            if (next2.getUsbClass() == 1 && next2.getUsbSubclass() == 3 && (midiHeaderInterfaceDescriptor = next2.getMidiHeaderInterfaceDescriptor()) != null && (midiHeaderInterfaceDescriptor instanceof UsbMSMidiHeader) && ((UsbMSMidiHeader) midiHeaderInterfaceDescriptor).getMidiStreamingClass() == 256) {
                arrayList.add(next2);
            }
        }
        Iterator it3 = arrayList.iterator();
        int i = 0;
        while (it3.hasNext()) {
            UsbInterfaceDescriptor usbInterfaceDescriptor = (UsbInterfaceDescriptor) it3.next();
            for (int i2 = 0; i2 < usbInterfaceDescriptor.getNumEndpoints(); i2++) {
                UsbEndpointDescriptor endpointDescriptor = usbInterfaceDescriptor.getEndpointDescriptor(i2);
                if ((endpointDescriptor.getDirection() == 0) == z && (classSpecificEndpointDescriptor = endpointDescriptor.getClassSpecificEndpointDescriptor()) != null && (classSpecificEndpointDescriptor instanceof UsbACMidi10Endpoint)) {
                    i += ((UsbACMidi10Endpoint) classSpecificEndpointDescriptor).getNumJacks();
                }
            }
        }
        return i;
    }

    public int calculateNumLegacyMidiInputs() {
        return calculateNumLegacyMidiPorts(false);
    }

    public int calculateNumLegacyMidiOutputs() {
        return calculateNumLegacyMidiPorts(true);
    }

    public float getInputHeadsetProbability() {
        float f = 0.0f;
        if (hasMIDIInterface()) {
            return 0.0f;
        }
        boolean hasMic = hasMic();
        boolean hasSpeaker = hasSpeaker();
        if (hasMic && hasSpeaker) {
            f = 0.75f;
        }
        return (hasMic && hasHIDInterface()) ? f + 0.25f : f;
    }

    public boolean isInputHeadset() {
        return getInputHeadsetProbability() >= 0.75f;
    }

    public final int getMaximumChannelCount() {
        Iterator<UsbDescriptor> it = this.mDescriptors.iterator();
        int i = 0;
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbAudioChannelCluster) {
                i = Math.max(i, (int) ((UsbAudioChannelCluster) next).getChannelCount());
            }
        }
        return i;
    }

    public float getOutputHeadsetLikelihood() {
        boolean z;
        float f = 0.0f;
        if (hasMIDIInterface()) {
            return 0.0f;
        }
        Iterator<UsbDescriptor> it = getACInterfaceDescriptors((byte) 3, 1).iterator();
        boolean z2 = false;
        boolean z3 = false;
        loop0: while (true) {
            z = z3;
            while (it.hasNext()) {
                UsbDescriptor next = it.next();
                if (next instanceof UsbACTerminal) {
                    UsbACTerminal usbACTerminal = (UsbACTerminal) next;
                    if (usbACTerminal.getTerminalType() == 769) {
                        if (usbACTerminal.getAssocTerminal() != 0) {
                            break;
                        }
                        z3 = true;
                    } else if (usbACTerminal.getTerminalType() == 770 || usbACTerminal.getTerminalType() == 1026) {
                        z2 = true;
                    }
                } else {
                    Log.w("UsbDescriptorParser", "Undefined Audio Output terminal l: " + next.getLength() + " t:0x" + Integer.toHexString(next.getType()));
                }
            }
            z3 = true;
        }
        if (z2) {
            f = 0.75f;
        } else if (z3) {
            f = z ? 0.75f : 0.5f;
            if (getMaximumChannelCount() > 2) {
                f -= 0.25f;
            }
        }
        return ((z2 || z3) && hasHIDInterface()) ? f + 0.25f : f;
    }

    public boolean isOutputHeadset() {
        return getOutputHeadsetLikelihood() >= 0.75f;
    }

    public boolean isDock() {
        if (!hasMIDIInterface() && !hasHIDInterface()) {
            ArrayList<UsbDescriptor> aCInterfaceDescriptors = getACInterfaceDescriptors((byte) 3, 1);
            if (aCInterfaceDescriptors.size() != 1) {
                return false;
            }
            if (aCInterfaceDescriptors.get(0) instanceof UsbACTerminal) {
                if (((UsbACTerminal) aCInterfaceDescriptors.get(0)).getTerminalType() == 1538) {
                    return true;
                }
            } else {
                Log.w("UsbDescriptorParser", "Undefined Audio Output terminal l: " + aCInterfaceDescriptors.get(0).getLength() + " t:0x" + Integer.toHexString(aCInterfaceDescriptors.get(0).getType()));
            }
        }
        return false;
    }
}
