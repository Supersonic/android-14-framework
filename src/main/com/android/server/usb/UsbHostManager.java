package com.android.server.usb;

import android.content.ComponentName;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.dump.DumpUtils;
import com.android.server.usb.descriptors.UsbDescriptor;
import com.android.server.usb.descriptors.UsbDescriptorParser;
import com.android.server.usb.descriptors.UsbDeviceDescriptor;
import com.android.server.usb.descriptors.UsbInterfaceDescriptor;
import com.android.server.usb.descriptors.report.TextReportCanvas;
import com.android.server.usb.descriptors.tree.UsbDescriptorsTree;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public class UsbHostManager {
    public static final String TAG = "UsbHostManager";
    public static final SimpleDateFormat sFormat = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");
    public final Context mContext;
    @GuardedBy({"mSettingsLock"})
    public UsbProfileGroupSettingsManager mCurrentSettings;
    public final boolean mHasMidiFeature;
    public final String[] mHostDenyList;
    public ConnectionRecord mLastConnect;
    public int mNumConnects;
    public final UsbPermissionManager mPermissionManager;
    public final UsbAlsaManager mUsbAlsaManager;
    @GuardedBy({"mHandlerLock"})
    public ComponentName mUsbDeviceConnectionHandler;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final HashMap<String, UsbDevice> mDevices = new HashMap<>();
    public Object mSettingsLock = new Object();
    public Object mHandlerLock = new Object();
    public final LinkedList<ConnectionRecord> mConnections = new LinkedList<>();
    public final ArrayMap<String, ConnectionRecord> mConnected = new ArrayMap<>();
    public final HashMap<String, ArrayList<UsbDirectMidiDevice>> mMidiDevices = new HashMap<>();
    public final HashSet<String> mMidiUniqueCodes = new HashSet<>();
    public final Random mRandom = new Random();

    /* JADX INFO: Access modifiers changed from: private */
    public native void monitorUsbHostBus();

    private native ParcelFileDescriptor nativeOpenDevice(String str);

    public final boolean isDenyListed(int i, int i2) {
        if (i == 9) {
            return true;
        }
        return i == 3 && i2 == 1;
    }

    /* loaded from: classes2.dex */
    public class ConnectionRecord {
        public final byte[] mDescriptors;
        public String mDeviceAddress;
        public final int mMode;
        public long mTimestamp = System.currentTimeMillis();

        public ConnectionRecord(String str, int i, byte[] bArr) {
            this.mDeviceAddress = str;
            this.mMode = i;
            this.mDescriptors = bArr;
        }

        public final String formatTime() {
            return new StringBuilder(UsbHostManager.sFormat.format(new Date(this.mTimestamp))).toString();
        }

        public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
            long start = dualDumpOutputStream.start(str, j);
            dualDumpOutputStream.write("device_address", 1138166333441L, this.mDeviceAddress);
            dualDumpOutputStream.write("mode", 1159641169922L, this.mMode);
            dualDumpOutputStream.write("timestamp", 1112396529667L, this.mTimestamp);
            if (this.mMode != -1) {
                UsbDescriptorParser usbDescriptorParser = new UsbDescriptorParser(this.mDeviceAddress, this.mDescriptors);
                UsbDeviceDescriptor deviceDescriptor = usbDescriptorParser.getDeviceDescriptor();
                dualDumpOutputStream.write("manufacturer", 1120986464260L, deviceDescriptor.getVendorID());
                dualDumpOutputStream.write("product", 1120986464261L, deviceDescriptor.getProductID());
                long start2 = dualDumpOutputStream.start("is_headset", 1146756268038L);
                dualDumpOutputStream.write("in", 1133871366145L, usbDescriptorParser.isInputHeadset());
                dualDumpOutputStream.write("out", 1133871366146L, usbDescriptorParser.isOutputHeadset());
                dualDumpOutputStream.end(start2);
            }
            dualDumpOutputStream.end(start);
        }

        public void dumpShort(IndentingPrintWriter indentingPrintWriter) {
            if (this.mMode != -1) {
                indentingPrintWriter.println(formatTime() + " Connect " + this.mDeviceAddress + " mode:" + this.mMode);
                UsbDescriptorParser usbDescriptorParser = new UsbDescriptorParser(this.mDeviceAddress, this.mDescriptors);
                UsbDeviceDescriptor deviceDescriptor = usbDescriptorParser.getDeviceDescriptor();
                indentingPrintWriter.println("manfacturer:0x" + Integer.toHexString(deviceDescriptor.getVendorID()) + " product:" + Integer.toHexString(deviceDescriptor.getProductID()));
                indentingPrintWriter.println("isHeadset[in: " + usbDescriptorParser.isInputHeadset() + " , out: " + usbDescriptorParser.isOutputHeadset() + "], isDock: " + usbDescriptorParser.isDock());
                return;
            }
            indentingPrintWriter.println(formatTime() + " Disconnect " + this.mDeviceAddress);
        }

        public void dumpTree(IndentingPrintWriter indentingPrintWriter) {
            if (this.mMode != -1) {
                indentingPrintWriter.println(formatTime() + " Connect " + this.mDeviceAddress + " mode:" + this.mMode);
                UsbDescriptorParser usbDescriptorParser = new UsbDescriptorParser(this.mDeviceAddress, this.mDescriptors);
                StringBuilder sb = new StringBuilder();
                UsbDescriptorsTree usbDescriptorsTree = new UsbDescriptorsTree();
                usbDescriptorsTree.parse(usbDescriptorParser);
                usbDescriptorsTree.report(new TextReportCanvas(usbDescriptorParser, sb));
                sb.append("isHeadset[in: " + usbDescriptorParser.isInputHeadset() + " , out: " + usbDescriptorParser.isOutputHeadset() + "], isDock: " + usbDescriptorParser.isDock());
                indentingPrintWriter.println(sb.toString());
                return;
            }
            indentingPrintWriter.println(formatTime() + " Disconnect " + this.mDeviceAddress);
        }

        public void dumpList(IndentingPrintWriter indentingPrintWriter) {
            if (this.mMode != -1) {
                indentingPrintWriter.println(formatTime() + " Connect " + this.mDeviceAddress + " mode:" + this.mMode);
                UsbDescriptorParser usbDescriptorParser = new UsbDescriptorParser(this.mDeviceAddress, this.mDescriptors);
                StringBuilder sb = new StringBuilder();
                TextReportCanvas textReportCanvas = new TextReportCanvas(usbDescriptorParser, sb);
                Iterator<UsbDescriptor> it = usbDescriptorParser.getDescriptors().iterator();
                while (it.hasNext()) {
                    it.next().report(textReportCanvas);
                }
                indentingPrintWriter.println(sb.toString());
                indentingPrintWriter.println("isHeadset[in: " + usbDescriptorParser.isInputHeadset() + " , out: " + usbDescriptorParser.isOutputHeadset() + "], isDock: " + usbDescriptorParser.isDock());
                return;
            }
            indentingPrintWriter.println(formatTime() + " Disconnect " + this.mDeviceAddress);
        }

        public void dumpRaw(IndentingPrintWriter indentingPrintWriter) {
            if (this.mMode != -1) {
                indentingPrintWriter.println(formatTime() + " Connect " + this.mDeviceAddress + " mode:" + this.mMode);
                int length = this.mDescriptors.length;
                StringBuilder sb = new StringBuilder();
                sb.append("Raw Descriptors ");
                sb.append(length);
                sb.append(" bytes");
                indentingPrintWriter.println(sb.toString());
                int i = 0;
                for (int i2 = 0; i2 < length / 16; i2++) {
                    StringBuilder sb2 = new StringBuilder();
                    int i3 = 0;
                    while (i3 < 16) {
                        sb2.append("0x");
                        sb2.append(String.format("0x%02X", Byte.valueOf(this.mDescriptors[i])));
                        sb2.append(" ");
                        i3++;
                        i++;
                    }
                    indentingPrintWriter.println(sb2.toString());
                }
                StringBuilder sb3 = new StringBuilder();
                while (i < length) {
                    sb3.append("0x");
                    sb3.append(String.format("0x%02X", Byte.valueOf(this.mDescriptors[i])));
                    sb3.append(" ");
                    i++;
                }
                indentingPrintWriter.println(sb3.toString());
                return;
            }
            indentingPrintWriter.println(formatTime() + " Disconnect " + this.mDeviceAddress);
        }
    }

    public UsbHostManager(Context context, UsbAlsaManager usbAlsaManager, UsbPermissionManager usbPermissionManager) {
        this.mContext = context;
        this.mHostDenyList = context.getResources().getStringArray(17236160);
        this.mUsbAlsaManager = usbAlsaManager;
        this.mPermissionManager = usbPermissionManager;
        String string = context.getResources().getString(17039828);
        if (!TextUtils.isEmpty(string)) {
            setUsbDeviceConnectionHandler(ComponentName.unflattenFromString(string));
        }
        this.mHasMidiFeature = context.getPackageManager().hasSystemFeature("android.software.midi");
    }

    public void setCurrentUserSettings(UsbProfileGroupSettingsManager usbProfileGroupSettingsManager) {
        synchronized (this.mSettingsLock) {
            this.mCurrentSettings = usbProfileGroupSettingsManager;
        }
    }

    public final UsbProfileGroupSettingsManager getCurrentUserSettings() {
        UsbProfileGroupSettingsManager usbProfileGroupSettingsManager;
        synchronized (this.mSettingsLock) {
            usbProfileGroupSettingsManager = this.mCurrentSettings;
        }
        return usbProfileGroupSettingsManager;
    }

    public void setUsbDeviceConnectionHandler(ComponentName componentName) {
        synchronized (this.mHandlerLock) {
            this.mUsbDeviceConnectionHandler = componentName;
        }
    }

    public final ComponentName getUsbDeviceConnectionHandler() {
        ComponentName componentName;
        synchronized (this.mHandlerLock) {
            componentName = this.mUsbDeviceConnectionHandler;
        }
        return componentName;
    }

    public final boolean isDenyListed(String str) {
        int length = this.mHostDenyList.length;
        for (int i = 0; i < length; i++) {
            if (str.startsWith(this.mHostDenyList[i])) {
                return true;
            }
        }
        return false;
    }

    public final void addConnectionRecord(String str, int i, byte[] bArr) {
        this.mNumConnects++;
        while (this.mConnections.size() >= 32) {
            this.mConnections.removeFirst();
        }
        ConnectionRecord connectionRecord = new ConnectionRecord(str, i, bArr);
        this.mConnections.add(connectionRecord);
        if (i != -1) {
            this.mLastConnect = connectionRecord;
        }
        if (i == 0) {
            this.mConnected.put(str, connectionRecord);
        } else if (i == -1) {
            this.mConnected.remove(str);
        }
    }

    public final void logUsbDevice(UsbDescriptorParser usbDescriptorParser) {
        int i;
        String str;
        int i2;
        String str2;
        String str3;
        String str4;
        UsbDeviceDescriptor deviceDescriptor = usbDescriptorParser.getDeviceDescriptor();
        if (deviceDescriptor != null) {
            i = deviceDescriptor.getVendorID();
            i2 = deviceDescriptor.getProductID();
            str = deviceDescriptor.getMfgString(usbDescriptorParser);
            str3 = deviceDescriptor.getProductString(usbDescriptorParser);
            str4 = deviceDescriptor.getDeviceReleaseString();
            str2 = deviceDescriptor.getSerialString(usbDescriptorParser);
        } else {
            i = 0;
            str = "<unknown>";
            i2 = 0;
            str2 = "<unknown>";
            str3 = str2;
            str4 = str3;
        }
        if (i == 7531) {
            return;
        }
        boolean hasAudioInterface = usbDescriptorParser.hasAudioInterface();
        boolean hasHIDInterface = usbDescriptorParser.hasHIDInterface();
        boolean hasStorageInterface = usbDescriptorParser.hasStorageInterface();
        Slog.d(TAG, (("USB device attached: " + String.format("vidpid %04x:%04x", Integer.valueOf(i), Integer.valueOf(i2))) + String.format(" mfg/product/ver/serial %s/%s/%s/%s", str, str3, str4, str2)) + String.format(" hasAudio/HID/Storage: %b/%b/%b", Boolean.valueOf(hasAudioInterface), Boolean.valueOf(hasHIDInterface), Boolean.valueOf(hasStorageInterface)));
    }

    public final boolean usbDeviceAdded(String str, int i, int i2, byte[] bArr) {
        if (isDenyListed(str) || isDenyListed(i, i2)) {
            return false;
        }
        UsbDescriptorParser usbDescriptorParser = new UsbDescriptorParser(str, bArr);
        if (i != 0 || checkUsbInterfacesDenyListed(usbDescriptorParser)) {
            logUsbDevice(usbDescriptorParser);
            synchronized (this.mLock) {
                if (this.mDevices.get(str) != null) {
                    String str2 = TAG;
                    Slog.w(str2, "device already on mDevices list: " + str);
                    return false;
                }
                UsbDevice.Builder androidUsbDeviceBuilder = usbDescriptorParser.toAndroidUsbDeviceBuilder();
                if (androidUsbDeviceBuilder == null) {
                    Slog.e(TAG, "Couldn't create UsbDevice object.");
                    addConnectionRecord(str, 2, usbDescriptorParser.getRawDescriptors());
                } else {
                    UsbSerialReader usbSerialReader = new UsbSerialReader(this.mContext, this.mPermissionManager, androidUsbDeviceBuilder.serialNumber);
                    UsbDevice build = androidUsbDeviceBuilder.build(usbSerialReader);
                    usbSerialReader.setDevice(build);
                    this.mDevices.put(str, build);
                    String str3 = TAG;
                    Slog.d(str3, "Added device " + build);
                    ComponentName usbDeviceConnectionHandler = getUsbDeviceConnectionHandler();
                    if (usbDeviceConnectionHandler == null) {
                        getCurrentUserSettings().deviceAttached(build);
                    } else {
                        getCurrentUserSettings().deviceAttachedForFixedHandler(build, usbDeviceConnectionHandler);
                    }
                    this.mUsbAlsaManager.usbDeviceAdded(str, build, usbDescriptorParser);
                    if (this.mHasMidiFeature) {
                        String generateNewUsbDeviceIdentifier = generateNewUsbDeviceIdentifier();
                        ArrayList<UsbDirectMidiDevice> arrayList = new ArrayList<>();
                        if (usbDescriptorParser.containsUniversalMidiDeviceEndpoint()) {
                            UsbDirectMidiDevice create = UsbDirectMidiDevice.create(this.mContext, build, usbDescriptorParser, true, generateNewUsbDeviceIdentifier);
                            if (create != null) {
                                arrayList.add(create);
                            } else {
                                Slog.e(str3, "Universal Midi Device is null.");
                            }
                            if (usbDescriptorParser.containsLegacyMidiDeviceEndpoint()) {
                                UsbDirectMidiDevice create2 = UsbDirectMidiDevice.create(this.mContext, build, usbDescriptorParser, false, generateNewUsbDeviceIdentifier);
                                if (create2 != null) {
                                    arrayList.add(create2);
                                } else {
                                    Slog.e(str3, "Legacy Midi Device is null.");
                                }
                            }
                        }
                        if (!arrayList.isEmpty()) {
                            this.mMidiDevices.put(str, arrayList);
                        }
                    }
                    addConnectionRecord(str, 0, usbDescriptorParser.getRawDescriptors());
                    FrameworkStatsLog.write(77, build.getVendorId(), build.getProductId(), usbDescriptorParser.hasAudioInterface(), usbDescriptorParser.hasHIDInterface(), usbDescriptorParser.hasStorageInterface(), 1, 0L);
                }
                return true;
            }
        }
        return false;
    }

    public final void usbDeviceRemoved(String str) {
        synchronized (this.mLock) {
            UsbDevice remove = this.mDevices.remove(str);
            if (remove != null) {
                String str2 = TAG;
                Slog.d(str2, "Removed device at " + str + ": " + remove.getProductName());
                this.mUsbAlsaManager.usbDeviceRemoved(str);
                this.mPermissionManager.usbDeviceRemoved(remove);
                ArrayList<UsbDirectMidiDevice> remove2 = this.mMidiDevices.remove(str);
                if (remove2 != null) {
                    Iterator<UsbDirectMidiDevice> it = remove2.iterator();
                    while (it.hasNext()) {
                        UsbDirectMidiDevice next = it.next();
                        if (next != null) {
                            IoUtils.closeQuietly(next);
                        }
                    }
                    String str3 = TAG;
                    Slog.i(str3, "USB MIDI Devices Removed: " + str);
                }
                getCurrentUserSettings().usbDeviceRemoved(remove);
                ConnectionRecord connectionRecord = this.mConnected.get(str);
                addConnectionRecord(str, -1, null);
                if (connectionRecord != null) {
                    UsbDescriptorParser usbDescriptorParser = new UsbDescriptorParser(str, connectionRecord.mDescriptors);
                    FrameworkStatsLog.write(77, remove.getVendorId(), remove.getProductId(), usbDescriptorParser.hasAudioInterface(), usbDescriptorParser.hasHIDInterface(), usbDescriptorParser.hasStorageInterface(), 0, System.currentTimeMillis() - connectionRecord.mTimestamp);
                }
            } else {
                String str4 = TAG;
                Slog.d(str4, "Removed device at " + str + " was already gone");
            }
        }
    }

    public void systemReady() {
        synchronized (this.mLock) {
            new Thread(null, new Runnable() { // from class: com.android.server.usb.UsbHostManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UsbHostManager.this.monitorUsbHostBus();
                }
            }, "UsbService host thread").start();
        }
    }

    public void getDeviceList(Bundle bundle) {
        synchronized (this.mLock) {
            for (String str : this.mDevices.keySet()) {
                bundle.putParcelable(str, this.mDevices.get(str));
            }
        }
    }

    public ParcelFileDescriptor openDevice(String str, UsbUserPermissionManager usbUserPermissionManager, String str2, int i, int i2) {
        ParcelFileDescriptor nativeOpenDevice;
        synchronized (this.mLock) {
            if (isDenyListed(str)) {
                throw new SecurityException("USB device is on a restricted bus");
            }
            UsbDevice usbDevice = this.mDevices.get(str);
            if (usbDevice == null) {
                throw new IllegalArgumentException("device " + str + " does not exist or is restricted");
            }
            usbUserPermissionManager.checkPermission(usbDevice, str2, i, i2);
            nativeOpenDevice = nativeOpenDevice(str);
        }
        return nativeOpenDevice;
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
        long start = dualDumpOutputStream.start(str, j);
        synchronized (this.mHandlerLock) {
            ComponentName componentName = this.mUsbDeviceConnectionHandler;
            if (componentName != null) {
                DumpUtils.writeComponentName(dualDumpOutputStream, "default_usb_host_connection_handler", 1146756268033L, componentName);
            }
        }
        synchronized (this.mLock) {
            for (String str2 : this.mDevices.keySet()) {
                com.android.internal.usb.DumpUtils.writeDevice(dualDumpOutputStream, "devices", 2246267895810L, this.mDevices.get(str2));
            }
            dualDumpOutputStream.write("num_connects", 1120986464259L, this.mNumConnects);
            Iterator<ConnectionRecord> it = this.mConnections.iterator();
            while (it.hasNext()) {
                it.next().dump(dualDumpOutputStream, "connections", 2246267895812L);
            }
            for (ArrayList<UsbDirectMidiDevice> arrayList : this.mMidiDevices.values()) {
                Iterator<UsbDirectMidiDevice> it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    it2.next().dump(dualDumpOutputStream, "midi_devices", 2246267895813L);
                }
            }
        }
        dualDumpOutputStream.end(start);
    }

    public void dumpDescriptors(IndentingPrintWriter indentingPrintWriter, String[] strArr) {
        if (this.mLastConnect != null) {
            indentingPrintWriter.println("Last Connected USB Device:");
            if (strArr.length <= 1 || strArr[1].equals("-dump-short")) {
                this.mLastConnect.dumpShort(indentingPrintWriter);
                return;
            } else if (strArr[1].equals("-dump-tree")) {
                this.mLastConnect.dumpTree(indentingPrintWriter);
                return;
            } else if (strArr[1].equals("-dump-list")) {
                this.mLastConnect.dumpList(indentingPrintWriter);
                return;
            } else if (strArr[1].equals("-dump-raw")) {
                this.mLastConnect.dumpRaw(indentingPrintWriter);
                return;
            } else {
                return;
            }
        }
        indentingPrintWriter.println("No USB Devices have been connected.");
    }

    public final boolean checkUsbInterfacesDenyListed(UsbDescriptorParser usbDescriptorParser) {
        Iterator<UsbDescriptor> it = usbDescriptorParser.getDescriptors().iterator();
        boolean z = false;
        while (it.hasNext()) {
            UsbDescriptor next = it.next();
            if (next instanceof UsbInterfaceDescriptor) {
                UsbInterfaceDescriptor usbInterfaceDescriptor = (UsbInterfaceDescriptor) next;
                z = isDenyListed(usbInterfaceDescriptor.getUsbClass(), usbInterfaceDescriptor.getUsbSubclass());
                if (!z) {
                    break;
                }
            }
        }
        return !z;
    }

    public final String generateNewUsbDeviceIdentifier() {
        String str;
        int i = 0;
        do {
            if (i > 10) {
                Slog.w(TAG, "MIDI unique code array resetting");
                this.mMidiUniqueCodes.clear();
                i = 0;
            }
            str = "";
            for (int i2 = 0; i2 < 3; i2++) {
                str = str + this.mRandom.nextInt(10);
            }
            i++;
        } while (this.mMidiUniqueCodes.contains(str));
        this.mMidiUniqueCodes.add(str);
        return str;
    }
}
