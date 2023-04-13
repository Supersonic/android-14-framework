package com.android.server.usb;

import android.media.AudioDeviceAttributes;
import android.media.IAudioService;
import android.os.RemoteException;
import android.p005os.IInstalld;
import android.util.Slog;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.server.audio.AudioService;
/* loaded from: classes2.dex */
public final class UsbAlsaDevice {
    public IAudioService mAudioService;
    public final int mCardNum;
    public final String mDeviceAddress;
    public final int mDeviceNum;
    public final boolean mHasInput;
    public final boolean mHasOutput;
    public int mInputState;
    public final boolean mIsDock;
    public final boolean mIsInputHeadset;
    public final boolean mIsOutputHeadset;
    public UsbAlsaJackDetector mJackDetector;
    public int mOutputState;
    public boolean mSelected = false;
    public String mDeviceName = "";
    public String mDeviceDescription = "";

    public UsbAlsaDevice(IAudioService iAudioService, int i, int i2, String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        this.mAudioService = iAudioService;
        this.mCardNum = i;
        this.mDeviceNum = i2;
        this.mDeviceAddress = str;
        this.mHasOutput = z;
        this.mHasInput = z2;
        this.mIsInputHeadset = z3;
        this.mIsOutputHeadset = z4;
        this.mIsDock = z5;
    }

    public int getCardNum() {
        return this.mCardNum;
    }

    public String getDeviceAddress() {
        return this.mDeviceAddress;
    }

    public String getAlsaCardDeviceString() {
        int i;
        int i2 = this.mCardNum;
        if (i2 < 0 || (i = this.mDeviceNum) < 0) {
            Slog.e("UsbAlsaDevice", "Invalid alsa card or device alsaCard: " + this.mCardNum + " alsaDevice: " + this.mDeviceNum);
            return null;
        }
        return AudioService.makeAlsaAddressString(i2, i);
    }

    public final synchronized boolean isInputJackConnected() {
        UsbAlsaJackDetector usbAlsaJackDetector = this.mJackDetector;
        if (usbAlsaJackDetector == null) {
            return true;
        }
        return usbAlsaJackDetector.isInputJackConnected();
    }

    public final synchronized boolean isOutputJackConnected() {
        UsbAlsaJackDetector usbAlsaJackDetector = this.mJackDetector;
        if (usbAlsaJackDetector == null) {
            return true;
        }
        return usbAlsaJackDetector.isOutputJackConnected();
    }

    public final synchronized void startJackDetect() {
        this.mJackDetector = UsbAlsaJackDetector.startJackDetect(this);
    }

    public final synchronized void stopJackDetect() {
        UsbAlsaJackDetector usbAlsaJackDetector = this.mJackDetector;
        if (usbAlsaJackDetector != null) {
            usbAlsaJackDetector.pleaseStop();
        }
        this.mJackDetector = null;
    }

    public synchronized void start() {
        this.mSelected = true;
        this.mInputState = 0;
        this.mOutputState = 0;
        startJackDetect();
        updateWiredDeviceConnectionState(true);
    }

    public synchronized void stop() {
        stopJackDetect();
        updateWiredDeviceConnectionState(false);
        this.mSelected = false;
    }

    public synchronized void updateWiredDeviceConnectionState(boolean z) {
        int i;
        if (!this.mSelected) {
            Slog.e("UsbAlsaDevice", "updateWiredDeviceConnectionState on unselected AlsaDevice!");
            return;
        }
        String alsaCardDeviceString = getAlsaCardDeviceString();
        if (alsaCardDeviceString == null) {
            return;
        }
        try {
            int i2 = 1;
            if (this.mHasOutput) {
                if (this.mIsDock) {
                    i = IInstalld.FLAG_USE_QUOTA;
                } else {
                    i = this.mIsOutputHeadset ? 67108864 : 16384;
                }
                boolean isOutputJackConnected = isOutputJackConnected();
                Slog.i("UsbAlsaDevice", "OUTPUT JACK connected: " + isOutputJackConnected);
                int i3 = (z && isOutputJackConnected) ? 1 : 0;
                if (i3 != this.mOutputState) {
                    this.mOutputState = i3;
                    this.mAudioService.setWiredDeviceConnectionState(new AudioDeviceAttributes(i, alsaCardDeviceString, this.mDeviceName), i3, "UsbAlsaDevice");
                }
            }
            if (this.mHasInput) {
                int i4 = this.mIsInputHeadset ? -2113929216 : -2147479552;
                boolean isInputJackConnected = isInputJackConnected();
                Slog.i("UsbAlsaDevice", "INPUT JACK connected: " + isInputJackConnected);
                if (!z || !isInputJackConnected) {
                    i2 = 0;
                }
                if (i2 != this.mInputState) {
                    this.mInputState = i2;
                    this.mAudioService.setWiredDeviceConnectionState(new AudioDeviceAttributes(i4, alsaCardDeviceString, this.mDeviceName), i2, "UsbAlsaDevice");
                }
            }
        } catch (RemoteException unused) {
            Slog.e("UsbAlsaDevice", "RemoteException in setWiredDeviceConnectionState");
        }
    }

    public synchronized String toString() {
        return "UsbAlsaDevice: [card: " + this.mCardNum + ", device: " + this.mDeviceNum + ", name: " + this.mDeviceName + ", hasOutput: " + this.mHasOutput + ", hasInput: " + this.mHasInput + "]";
    }

    public synchronized void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
        long start = dualDumpOutputStream.start(str, j);
        dualDumpOutputStream.write("card", 1120986464257L, this.mCardNum);
        dualDumpOutputStream.write("device", 1120986464258L, this.mDeviceNum);
        dualDumpOutputStream.write("name", 1138166333443L, this.mDeviceName);
        dualDumpOutputStream.write("has_output", 1133871366148L, this.mHasOutput);
        dualDumpOutputStream.write("has_input", 1133871366149L, this.mHasInput);
        dualDumpOutputStream.write("address", 1138166333446L, this.mDeviceAddress);
        dualDumpOutputStream.end(start);
    }

    public synchronized void setDeviceNameAndDescription(String str, String str2) {
        this.mDeviceName = str;
        this.mDeviceDescription = str2;
    }

    public boolean equals(Object obj) {
        if (obj instanceof UsbAlsaDevice) {
            UsbAlsaDevice usbAlsaDevice = (UsbAlsaDevice) obj;
            return this.mCardNum == usbAlsaDevice.mCardNum && this.mDeviceNum == usbAlsaDevice.mDeviceNum && this.mHasOutput == usbAlsaDevice.mHasOutput && this.mHasInput == usbAlsaDevice.mHasInput && this.mIsInputHeadset == usbAlsaDevice.mIsInputHeadset && this.mIsOutputHeadset == usbAlsaDevice.mIsOutputHeadset && this.mIsDock == usbAlsaDevice.mIsDock;
        }
        return false;
    }

    public int hashCode() {
        return ((((((((((((this.mCardNum + 31) * 31) + this.mDeviceNum) * 31) + (!this.mHasOutput ? 1 : 0)) * 31) + (!this.mHasInput ? 1 : 0)) * 31) + (!this.mIsInputHeadset ? 1 : 0)) * 31) + (!this.mIsOutputHeadset ? 1 : 0)) * 31) + (!this.mIsDock ? 1 : 0);
    }
}
