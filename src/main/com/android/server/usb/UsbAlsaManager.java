package com.android.server.usb;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.media.IAudioService;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.alsa.AlsaCardsParser;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.server.usb.descriptors.UsbDescriptorParser;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public final class UsbAlsaManager {
    public static final String TAG = "UsbAlsaManager";
    public static final List<DenyListEntry> sDeviceDenylist = Arrays.asList(new DenyListEntry(1356, 1476, 1), new DenyListEntry(1356, 2508, 1));
    public IAudioService mAudioService;
    public final Context mContext;
    public final boolean mHasMidiFeature;
    public UsbAlsaDevice mSelectedDevice;
    public final AlsaCardsParser mCardsParser = new AlsaCardsParser();
    public final ArrayList<UsbAlsaDevice> mAlsaDevices = new ArrayList<>();
    public final HashMap<String, UsbAlsaMidiDevice> mMidiDevices = new HashMap<>();
    public UsbAlsaMidiDevice mPeripheralMidiDevice = null;
    public final HashSet<Integer> mAlsaCards = new HashSet<>();
    public final FileObserver mAlsaObserver = new FileObserver(new File("/dev/snd/"), FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_USAGE) { // from class: com.android.server.usb.UsbAlsaManager.1
        @Override // android.os.FileObserver
        public void onEvent(int i, String str) {
            if (i == 256) {
                UsbAlsaManager.this.alsaFileAdded(str);
            } else if (i != 512) {
            } else {
                UsbAlsaManager.this.alsaFileRemoved(str);
            }
        }
    };

    public void logDevices(String str) {
    }

    /* loaded from: classes2.dex */
    public static class DenyListEntry {
        public final int mFlags;
        public final int mProductId;
        public final int mVendorId;

        public DenyListEntry(int i, int i2, int i3) {
            this.mVendorId = i;
            this.mProductId = i2;
            this.mFlags = i3;
        }
    }

    public static boolean isDeviceDenylisted(int i, int i2, int i3) {
        for (DenyListEntry denyListEntry : sDeviceDenylist) {
            if (denyListEntry.mVendorId == i && denyListEntry.mProductId == i2) {
                return (denyListEntry.mFlags & i3) != 0;
            }
        }
        return false;
    }

    public UsbAlsaManager(Context context) {
        this.mContext = context;
        this.mHasMidiFeature = context.getPackageManager().hasSystemFeature("android.software.midi");
    }

    public void systemReady() {
        this.mAudioService = IAudioService.Stub.asInterface(ServiceManager.getService("audio"));
        this.mAlsaObserver.startWatching();
    }

    public final synchronized void selectAlsaDevice(UsbAlsaDevice usbAlsaDevice) {
        if (this.mSelectedDevice != null) {
            deselectAlsaDevice();
        }
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "usb_audio_automatic_routing_disabled", 0) != 0) {
            return;
        }
        this.mSelectedDevice = usbAlsaDevice;
        usbAlsaDevice.start();
    }

    public final synchronized void deselectAlsaDevice() {
        UsbAlsaDevice usbAlsaDevice = this.mSelectedDevice;
        if (usbAlsaDevice != null) {
            usbAlsaDevice.stop();
            this.mSelectedDevice = null;
        }
    }

    public final int getAlsaDeviceListIndexFor(String str) {
        for (int i = 0; i < this.mAlsaDevices.size(); i++) {
            if (this.mAlsaDevices.get(i).getDeviceAddress().equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public final UsbAlsaDevice removeAlsaDeviceFromList(String str) {
        int alsaDeviceListIndexFor = getAlsaDeviceListIndexFor(str);
        if (alsaDeviceListIndexFor > -1) {
            return this.mAlsaDevices.remove(alsaDeviceListIndexFor);
        }
        return null;
    }

    public UsbAlsaDevice selectDefaultDevice() {
        if (this.mAlsaDevices.size() > 0) {
            UsbAlsaDevice usbAlsaDevice = this.mAlsaDevices.get(0);
            if (usbAlsaDevice != null) {
                selectAlsaDevice(usbAlsaDevice);
            }
            return usbAlsaDevice;
        }
        return null;
    }

    public void usbDeviceAdded(String str, UsbDevice usbDevice, UsbDescriptorParser usbDescriptorParser) {
        this.mCardsParser.scan();
        AlsaCardsParser.AlsaCardRecord findCardNumFor = this.mCardsParser.findCardNumFor(str);
        if (findCardNumFor == null) {
            return;
        }
        waitForAlsaDevice(findCardNumFor.getCardNum(), true);
        boolean z = usbDescriptorParser.hasInput() && !isDeviceDenylisted(usbDevice.getVendorId(), usbDevice.getProductId(), 2);
        boolean z2 = usbDescriptorParser.hasOutput() && !isDeviceDenylisted(usbDevice.getVendorId(), usbDevice.getProductId(), 1);
        if (z || z2) {
            boolean isInputHeadset = usbDescriptorParser.isInputHeadset();
            boolean isOutputHeadset = usbDescriptorParser.isOutputHeadset();
            boolean isDock = usbDescriptorParser.isDock();
            IAudioService iAudioService = this.mAudioService;
            if (iAudioService == null) {
                Slog.e(TAG, "no AudioService");
                return;
            }
            UsbAlsaDevice usbAlsaDevice = new UsbAlsaDevice(iAudioService, findCardNumFor.getCardNum(), 0, str, z2, z, isInputHeadset, isOutputHeadset, isDock);
            usbAlsaDevice.setDeviceNameAndDescription(findCardNumFor.getCardName(), findCardNumFor.getCardDescription());
            this.mAlsaDevices.add(0, usbAlsaDevice);
            selectAlsaDevice(usbAlsaDevice);
        }
        addMidiDevice(str, usbDevice, usbDescriptorParser, findCardNumFor);
        logDevices("deviceAdded()");
    }

    public final void addMidiDevice(String str, UsbDevice usbDevice, UsbDescriptorParser usbDescriptorParser, AlsaCardsParser.AlsaCardRecord alsaCardRecord) {
        String str2;
        boolean hasMIDIInterface = usbDescriptorParser.hasMIDIInterface();
        boolean containsUniversalMidiDeviceEndpoint = usbDescriptorParser.containsUniversalMidiDeviceEndpoint();
        if (this.mHasMidiFeature && hasMIDIInterface && !containsUniversalMidiDeviceEndpoint) {
            Bundle bundle = new Bundle();
            String manufacturerName = usbDevice.getManufacturerName();
            String productName = usbDevice.getProductName();
            String version = usbDevice.getVersion();
            if (manufacturerName == null || manufacturerName.isEmpty()) {
                str2 = productName;
            } else if (productName == null || productName.isEmpty()) {
                str2 = manufacturerName;
            } else {
                str2 = manufacturerName + " " + productName;
            }
            bundle.putString("name", str2);
            bundle.putString("manufacturer", manufacturerName);
            bundle.putString("product", productName);
            bundle.putString("version", version);
            bundle.putString("serial_number", usbDevice.getSerialNumber());
            bundle.putInt("alsa_card", alsaCardRecord.getCardNum());
            bundle.putInt("alsa_device", 0);
            bundle.putParcelable("usb_device", usbDevice);
            UsbAlsaMidiDevice create = UsbAlsaMidiDevice.create(this.mContext, bundle, alsaCardRecord.getCardNum(), 0, usbDescriptorParser.calculateNumLegacyMidiInputs(), usbDescriptorParser.calculateNumLegacyMidiOutputs());
            if (create != null) {
                this.mMidiDevices.put(str, create);
            }
        }
    }

    public synchronized void usbDeviceRemoved(String str) {
        UsbAlsaDevice removeAlsaDeviceFromList = removeAlsaDeviceFromList(str);
        String str2 = TAG;
        Slog.i(str2, "USB Audio Device Removed: " + removeAlsaDeviceFromList);
        if (removeAlsaDeviceFromList != null && removeAlsaDeviceFromList == this.mSelectedDevice) {
            waitForAlsaDevice(removeAlsaDeviceFromList.getCardNum(), false);
            deselectAlsaDevice();
            selectDefaultDevice();
        }
        UsbAlsaMidiDevice remove = this.mMidiDevices.remove(str);
        if (remove != null) {
            Slog.i(str2, "USB MIDI Device Removed: " + str);
            IoUtils.closeQuietly(remove);
        }
        logDevices("usbDeviceRemoved()");
    }

    public void setPeripheralMidiState(boolean z, int i, int i2) {
        UsbAlsaMidiDevice usbAlsaMidiDevice;
        if (this.mHasMidiFeature) {
            if (!z || this.mPeripheralMidiDevice != null) {
                if (z || (usbAlsaMidiDevice = this.mPeripheralMidiDevice) == null) {
                    return;
                }
                IoUtils.closeQuietly(usbAlsaMidiDevice);
                this.mPeripheralMidiDevice = null;
                return;
            }
            Bundle bundle = new Bundle();
            Resources resources = this.mContext.getResources();
            bundle.putString("name", resources.getString(17041687));
            bundle.putString("manufacturer", resources.getString(17041686));
            bundle.putString("product", resources.getString(17041688));
            bundle.putInt("alsa_card", i);
            bundle.putInt("alsa_device", i2);
            this.mPeripheralMidiDevice = UsbAlsaMidiDevice.create(this.mContext, bundle, i, i2, 1, 1);
        }
    }

    public final boolean waitForAlsaDevice(int i, boolean z) {
        boolean contains;
        synchronized (this.mAlsaCards) {
            long elapsedRealtime = SystemClock.elapsedRealtime() + 2500;
            while ((this.mAlsaCards.contains(Integer.valueOf(i)) ^ z) && elapsedRealtime > SystemClock.elapsedRealtime()) {
                long elapsedRealtime2 = elapsedRealtime - SystemClock.elapsedRealtime();
                if (elapsedRealtime2 > 0) {
                    try {
                        this.mAlsaCards.wait(elapsedRealtime2);
                    } catch (InterruptedException unused) {
                        Slog.d(TAG, "usb: InterruptedException while waiting for ALSA file.");
                    }
                }
            }
            contains = this.mAlsaCards.contains(Integer.valueOf(i));
            if ((z ^ contains) && elapsedRealtime > SystemClock.elapsedRealtime()) {
                String str = TAG;
                Slog.e(str, "waitForAlsaDevice(" + i + ") timeout");
            } else {
                String str2 = TAG;
                Slog.i(str2, "waitForAlsaDevice for device card=" + i + ", isAdded=" + z + ", found=" + contains);
            }
        }
        return contains;
    }

    public final int getCardNumberFromAlsaFilePath(String str) {
        char c;
        if (str.startsWith("pcmC")) {
            if (str.endsWith("p")) {
                c = 1;
            } else {
                if (str.endsWith("c")) {
                    c = 2;
                }
                c = 0;
            }
        } else {
            if (str.startsWith("midiC")) {
                c = 3;
            }
            c = 0;
        }
        if (c == 0) {
            Slog.i(TAG, "Unknown type file(" + str + ") added.");
            return -1;
        }
        try {
            return Integer.parseInt(str.substring(str.indexOf(67) + 1, str.indexOf(68)));
        } catch (Exception e) {
            Slog.e(TAG, "Could not parse ALSA file name " + str, e);
            return -1;
        }
    }

    public final void alsaFileAdded(String str) {
        String str2 = TAG;
        Slog.i(str2, "alsaFileAdded(" + str + ")");
        int cardNumberFromAlsaFilePath = getCardNumberFromAlsaFilePath(str);
        if (cardNumberFromAlsaFilePath == -1) {
            return;
        }
        synchronized (this.mAlsaCards) {
            if (!this.mAlsaCards.contains(Integer.valueOf(cardNumberFromAlsaFilePath))) {
                Slog.d(str2, "Adding ALSA device card=" + cardNumberFromAlsaFilePath);
                this.mAlsaCards.add(Integer.valueOf(cardNumberFromAlsaFilePath));
                this.mAlsaCards.notifyAll();
            }
        }
    }

    public final void alsaFileRemoved(String str) {
        int cardNumberFromAlsaFilePath = getCardNumberFromAlsaFilePath(str);
        if (cardNumberFromAlsaFilePath == -1) {
            return;
        }
        synchronized (this.mAlsaCards) {
            this.mAlsaCards.remove(Integer.valueOf(cardNumberFromAlsaFilePath));
        }
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
        long start = dualDumpOutputStream.start(str, j);
        dualDumpOutputStream.write("cards_parser", 1120986464257L, this.mCardsParser.getScanStatus());
        Iterator<UsbAlsaDevice> it = this.mAlsaDevices.iterator();
        while (it.hasNext()) {
            it.next().dump(dualDumpOutputStream, "alsa_devices", 2246267895810L);
        }
        for (String str2 : this.mMidiDevices.keySet()) {
            this.mMidiDevices.get(str2).dump(str2, dualDumpOutputStream, "alsa_midi_devices", 2246267895812L);
        }
        dualDumpOutputStream.end(start);
    }
}
