package com.android.server.usb.descriptors.report;

import android.p005os.IInstalld;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.XmlUtils;
import java.util.HashMap;
/* loaded from: classes2.dex */
public final class UsbStrings {
    public static HashMap<Byte, String> sACControlInterfaceNames;
    public static HashMap<Byte, String> sACStreamingInterfaceNames;
    public static HashMap<Integer, String> sAudioEncodingNames;
    public static HashMap<Integer, String> sAudioSubclassNames;
    public static HashMap<Integer, String> sClassNames;
    public static HashMap<Byte, String> sDescriptorNames;
    public static HashMap<Integer, String> sFormatNames;
    public static HashMap<Integer, String> sTerminalNames;

    public static String getACInterfaceSubclassName(int i) {
        return i == 1 ? "AC Control" : "AC Streaming";
    }

    static {
        allocUsbStrings();
    }

    public static void initDescriptorNames() {
        HashMap<Byte, String> hashMap = new HashMap<>();
        sDescriptorNames = hashMap;
        hashMap.put((byte) 1, "Device");
        sDescriptorNames.put((byte) 2, "Config");
        sDescriptorNames.put((byte) 3, "String");
        sDescriptorNames.put((byte) 4, "Interface");
        sDescriptorNames.put((byte) 5, "Endpoint");
        sDescriptorNames.put((byte) 15, "BOS (whatever that means)");
        sDescriptorNames.put((byte) 11, "Interface Association");
        sDescriptorNames.put((byte) 16, "Capability");
        sDescriptorNames.put((byte) 33, "HID");
        sDescriptorNames.put((byte) 34, "Report");
        sDescriptorNames.put((byte) 35, "Physical");
        sDescriptorNames.put((byte) 36, "Class-specific Interface");
        sDescriptorNames.put((byte) 37, "Class-specific Endpoint");
        sDescriptorNames.put((byte) 41, "Hub");
        sDescriptorNames.put((byte) 42, "Superspeed Hub");
        sDescriptorNames.put((byte) 48, "Endpoint Companion");
    }

    public static void initACControlInterfaceNames() {
        HashMap<Byte, String> hashMap = new HashMap<>();
        sACControlInterfaceNames = hashMap;
        hashMap.put((byte) 0, "Undefined");
        sACControlInterfaceNames.put((byte) 1, "Header");
        sACControlInterfaceNames.put((byte) 2, "Input Terminal");
        sACControlInterfaceNames.put((byte) 3, "Output Terminal");
        sACControlInterfaceNames.put((byte) 4, "Mixer Unit");
        sACControlInterfaceNames.put((byte) 5, "Selector Unit");
        sACControlInterfaceNames.put((byte) 6, "Feature Unit");
        sACControlInterfaceNames.put((byte) 7, "Processing Unit");
        sACControlInterfaceNames.put((byte) 8, "Extension Unit");
        sACControlInterfaceNames.put((byte) 10, "Clock Source");
        sACControlInterfaceNames.put((byte) 11, "Clock Selector");
        sACControlInterfaceNames.put((byte) 12, "Clock Multiplier");
        sACControlInterfaceNames.put((byte) 13, "Sample Rate Converter");
    }

    public static void initACStreamingInterfaceNames() {
        HashMap<Byte, String> hashMap = new HashMap<>();
        sACStreamingInterfaceNames = hashMap;
        hashMap.put((byte) 0, "Undefined");
        sACStreamingInterfaceNames.put((byte) 1, "General");
        sACStreamingInterfaceNames.put((byte) 2, "Format Type");
        sACStreamingInterfaceNames.put((byte) 3, "Format Specific");
    }

    public static void initClassNames() {
        HashMap<Integer, String> hashMap = new HashMap<>();
        sClassNames = hashMap;
        hashMap.put(0, "Device");
        sClassNames.put(1, "Audio");
        sClassNames.put(2, "Communications");
        sClassNames.put(3, "HID");
        sClassNames.put(5, "Physical");
        sClassNames.put(6, "Image");
        sClassNames.put(7, "Printer");
        sClassNames.put(8, "Storage");
        sClassNames.put(9, "Hub");
        sClassNames.put(10, "CDC Control");
        sClassNames.put(11, "Smart Card");
        sClassNames.put(13, "Security");
        sClassNames.put(14, "Video");
        sClassNames.put(15, "Healthcare");
        sClassNames.put(16, "Audio/Video");
        sClassNames.put(17, "Billboard");
        sClassNames.put(18, "Type C Bridge");
        sClassNames.put(220, "Diagnostic");
        sClassNames.put(224, "Wireless");
        sClassNames.put(Integer.valueOf((int) FrameworkStatsLog.BOOT_TIME_EVENT_DURATION_REPORTED), "Misc");
        sClassNames.put(Integer.valueOf((int) FrameworkStatsLog.APP_FREEZE_CHANGED), "Application Specific");
        sClassNames.put(255, "Vendor Specific");
    }

    public static void initAudioSubclassNames() {
        HashMap<Integer, String> hashMap = new HashMap<>();
        sAudioSubclassNames = hashMap;
        hashMap.put(0, "Undefinded");
        sAudioSubclassNames.put(1, "Audio Control");
        sAudioSubclassNames.put(2, "Audio Streaming");
        sAudioSubclassNames.put(3, "MIDI Streaming");
    }

    public static void initAudioEncodingNames() {
        HashMap<Integer, String> hashMap = new HashMap<>();
        sAudioEncodingNames = hashMap;
        hashMap.put(0, "Format I Undefined");
        sAudioEncodingNames.put(1, "Format I PCM");
        sAudioEncodingNames.put(2, "Format I PCM8");
        sAudioEncodingNames.put(3, "Format I FLOAT");
        sAudioEncodingNames.put(4, "Format I ALAW");
        sAudioEncodingNames.put(5, "Format I MuLAW");
        sAudioEncodingNames.put(Integer.valueOf((int) IInstalld.FLAG_USE_QUOTA), "FORMAT_II Undefined");
        sAudioEncodingNames.put(4097, "FORMAT_II MPEG");
        sAudioEncodingNames.put(4098, "FORMAT_II AC3");
        sAudioEncodingNames.put(Integer.valueOf((int) IInstalld.FLAG_FORCE), "FORMAT_III Undefined");
        sAudioEncodingNames.put(8193, "FORMAT_III IEC1937 AC3");
        sAudioEncodingNames.put(8194, "FORMAT_III MPEG1 Layer 1");
        sAudioEncodingNames.put(8195, "FORMAT_III MPEG1 Layer 2");
        sAudioEncodingNames.put(8196, "FORMAT_III MPEG2 EXT");
        sAudioEncodingNames.put(8197, "FORMAT_III MPEG2 Layer1LS");
    }

    public static void initTerminalNames() {
        HashMap<Integer, String> hashMap = new HashMap<>();
        sTerminalNames = hashMap;
        hashMap.put(Integer.valueOf((int) FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP), "USB Streaming");
        sTerminalNames.put(512, "Undefined");
        sTerminalNames.put(Integer.valueOf((int) FrameworkStatsLog.HEARING_AID_INFO_REPORTED), "Microphone");
        sTerminalNames.put(Integer.valueOf((int) FrameworkStatsLog.DEVICE_WIDE_JOB_CONSTRAINT_CHANGED), "Desktop Microphone");
        sTerminalNames.put(Integer.valueOf((int) FrameworkStatsLog.AMBIENT_MODE_CHANGED), "Personal (headset) Microphone");
        sTerminalNames.put(Integer.valueOf((int) FrameworkStatsLog.ANR_LATENCY_REPORTED), "Omni Microphone");
        sTerminalNames.put(Integer.valueOf((int) FrameworkStatsLog.RESOURCE_API_INFO), "Microphone Array");
        sTerminalNames.put(518, "Proecessing Microphone Array");
        sTerminalNames.put(Integer.valueOf((int) FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_USAGE), "Undefined");
        sTerminalNames.put(769, "Speaker");
        sTerminalNames.put(770, "Headphones");
        sTerminalNames.put(771, "Head Mounted Speaker");
        sTerminalNames.put(772, "Desktop Speaker");
        sTerminalNames.put(773, "Room Speaker");
        sTerminalNames.put(774, "Communications Speaker");
        sTerminalNames.put(775, "Low Frequency Speaker");
        sTerminalNames.put(1024, "Undefined");
        sTerminalNames.put(1025, "Handset");
        sTerminalNames.put(1026, "Headset");
        sTerminalNames.put(1027, "Speaker Phone");
        sTerminalNames.put(1028, "Speaker Phone (echo supressing)");
        sTerminalNames.put(1029, "Speaker Phone (echo canceling)");
        sTerminalNames.put(1280, "Undefined");
        sTerminalNames.put(1281, "Phone Line");
        sTerminalNames.put(1282, "Telephone");
        sTerminalNames.put(1283, "Down Line Phone");
        sTerminalNames.put(Integer.valueOf((int) FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM), "Undefined");
        sTerminalNames.put(1537, "Analog Connector");
        sTerminalNames.put(1538, "Digital Connector");
        sTerminalNames.put(1539, "Line Connector");
        sTerminalNames.put(1540, "Legacy Audio Connector");
        sTerminalNames.put(1541, "S/PIDF Interface");
        sTerminalNames.put(1542, "1394 Audio");
        sTerminalNames.put(1543, "1394 Audio/Video");
        sTerminalNames.put(1792, "Undefined");
        sTerminalNames.put(1793, "Calibration Nose");
        sTerminalNames.put(1794, "EQ Noise");
        sTerminalNames.put(1795, "CD Player");
        sTerminalNames.put(1796, "DAT");
        sTerminalNames.put(1797, "DCC");
        sTerminalNames.put(1798, "Mini Disk");
        sTerminalNames.put(1799, "Analog Tap");
        sTerminalNames.put(1800, "Phonograph");
        sTerminalNames.put(1801, "VCR Audio");
        sTerminalNames.put(1802, "Video Disk Audio");
        sTerminalNames.put(1803, "DVD Audio");
        sTerminalNames.put(1804, "TV Audio");
        sTerminalNames.put(1805, "Satellite Audio");
        sTerminalNames.put(1806, "Cable Tuner Audio");
        sTerminalNames.put(1807, "DSS Audio");
        sTerminalNames.put(1809, "Radio Transmitter");
        sTerminalNames.put(1810, "Multitrack Recorder");
        sTerminalNames.put(1811, "Synthesizer");
    }

    public static String getTerminalName(int i) {
        String str = sTerminalNames.get(Integer.valueOf(i));
        if (str != null) {
            return str;
        }
        return "Unknown Terminal Type 0x" + Integer.toHexString(i);
    }

    public static void initFormatNames() {
        HashMap<Integer, String> hashMap = new HashMap<>();
        sFormatNames = hashMap;
        hashMap.put(1, "FORMAT_TYPE_I");
        sFormatNames.put(2, "FORMAT_TYPE_II");
        sFormatNames.put(3, "FORMAT_TYPE_III");
        sFormatNames.put(4, "FORMAT_TYPE_IV");
        sFormatNames.put(-127, "EXT_FORMAT_TYPE_I");
        sFormatNames.put(-126, "EXT_FORMAT_TYPE_II");
        sFormatNames.put(-125, "EXT_FORMAT_TYPE_III");
    }

    public static String getFormatName(int i) {
        String str = sFormatNames.get(Integer.valueOf(i));
        if (str != null) {
            return str;
        }
        return "Unknown Format Type 0x" + Integer.toHexString(i);
    }

    public static void allocUsbStrings() {
        initDescriptorNames();
        initACControlInterfaceNames();
        initACStreamingInterfaceNames();
        initClassNames();
        initAudioSubclassNames();
        initAudioEncodingNames();
        initTerminalNames();
        initFormatNames();
    }

    public static String getDescriptorName(byte b) {
        String str = sDescriptorNames.get(Byte.valueOf(b));
        int i = b & 255;
        if (str != null) {
            return str;
        }
        return "Unknown Descriptor [0x" + Integer.toHexString(i) + XmlUtils.STRING_ARRAY_SEPARATOR + i + "]";
    }

    public static String getACControlInterfaceName(byte b) {
        String str = sACControlInterfaceNames.get(Byte.valueOf(b));
        int i = b & 255;
        if (str != null) {
            return str;
        }
        return "Unknown subtype [0x" + Integer.toHexString(i) + XmlUtils.STRING_ARRAY_SEPARATOR + i + "]";
    }

    public static String getClassName(int i) {
        String str = sClassNames.get(Integer.valueOf(i));
        int i2 = i & 255;
        if (str != null) {
            return str;
        }
        return "Unknown Class ID [0x" + Integer.toHexString(i2) + XmlUtils.STRING_ARRAY_SEPARATOR + i2 + "]";
    }

    public static String getAudioSubclassName(int i) {
        String str = sAudioSubclassNames.get(Integer.valueOf(i));
        int i2 = i & 255;
        if (str != null) {
            return str;
        }
        return "Unknown Audio Subclass [0x" + Integer.toHexString(i2) + XmlUtils.STRING_ARRAY_SEPARATOR + i2 + "]";
    }

    public static String getAudioFormatName(int i) {
        String str = sAudioEncodingNames.get(Integer.valueOf(i));
        if (str != null) {
            return str;
        }
        return "Unknown Format (encoding) ID [0x" + Integer.toHexString(i) + XmlUtils.STRING_ARRAY_SEPARATOR + i + "]";
    }
}
