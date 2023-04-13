package com.android.server;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.UEventObserver;
import android.p005os.IInstalld;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import com.android.server.ExtconUEventObserver;
import com.android.server.input.InputManagerService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public final class WiredAccessoryManager implements InputManagerService.WiredAccessoryCallbacks {
    public static final String TAG = "WiredAccessoryManager";
    public final AudioManager mAudioManager;
    public final WiredAccessoryExtconObserver mExtconObserver;
    public int mHeadsetState;
    public final InputManagerService mInputManager;
    public final WiredAccessoryObserver mObserver;
    public int mSwitchValues;
    public final boolean mUseDevInputEventForAudioJack;
    public final PowerManager.WakeLock mWakeLock;
    public final Object mLock = new Object();
    public final Handler mHandler = new Handler(Looper.myLooper(), null, true) { // from class: com.android.server.WiredAccessoryManager.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                WiredAccessoryManager.this.setDevicesState(message.arg1, message.arg2, (String) message.obj);
                WiredAccessoryManager.this.mWakeLock.release();
            } else if (i != 2) {
            } else {
                WiredAccessoryManager.this.onSystemReady();
                WiredAccessoryManager.this.mWakeLock.release();
            }
        }
    };

    public WiredAccessoryManager(Context context, InputManagerService inputManagerService) {
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, TAG);
        this.mWakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mInputManager = inputManagerService;
        this.mUseDevInputEventForAudioJack = context.getResources().getBoolean(17891859);
        this.mExtconObserver = new WiredAccessoryExtconObserver();
        this.mObserver = new WiredAccessoryObserver();
    }

    public final void onSystemReady() {
        if (this.mUseDevInputEventForAudioJack) {
            int i = this.mInputManager.getSwitchState(-1, -256, 2) == 1 ? 4 : 0;
            if (this.mInputManager.getSwitchState(-1, -256, 4) == 1) {
                i |= 16;
            }
            if (this.mInputManager.getSwitchState(-1, -256, 6) == 1) {
                i |= 64;
            }
            notifyWiredAccessoryChanged(0L, i, 84);
        }
        if (ExtconUEventObserver.extconExists()) {
            if (this.mUseDevInputEventForAudioJack) {
                Log.w(TAG, "Both input event and extcon are used for audio jack, please just choose one.");
            }
            this.mExtconObserver.init();
            return;
        }
        this.mObserver.init();
    }

    @Override // com.android.server.input.InputManagerService.WiredAccessoryCallbacks
    public void notifyWiredAccessoryChanged(long j, int i, int i2) {
        synchronized (this.mLock) {
            int i3 = (this.mSwitchValues & (~i2)) | i;
            this.mSwitchValues = i3;
            int i4 = i3 & 84;
            int i5 = 0;
            if (i4 != 0) {
                if (i4 == 4) {
                    i5 = 2;
                } else if (i4 == 16 || i4 == 20) {
                    i5 = 1;
                } else if (i4 == 64) {
                    i5 = 32;
                }
            }
            updateLocked("h2w", i5 | (this.mHeadsetState & (-36)));
        }
    }

    @Override // com.android.server.input.InputManagerService.WiredAccessoryCallbacks
    public void systemReady() {
        synchronized (this.mLock) {
            this.mWakeLock.acquire();
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, 0, 0, null));
        }
    }

    public final void updateLocked(String str, int i) {
        boolean z;
        int i2 = i & 63;
        int i3 = i2 & 4;
        int i4 = i2 & 8;
        int i5 = i2 & 35;
        if (this.mHeadsetState == i2) {
            Log.e(TAG, "No state change.");
            return;
        }
        boolean z2 = false;
        if (i5 == 35) {
            Log.e(TAG, "Invalid combination, unsetting h2w flag");
            z = false;
        } else {
            z = true;
        }
        if (i3 == 4 && i4 == 8) {
            Log.e(TAG, "Invalid combination, unsetting usb flag");
        } else {
            z2 = true;
        }
        if (!z && !z2) {
            Log.e(TAG, "invalid transition, returning ...");
            return;
        }
        this.mWakeLock.acquire();
        Log.i(TAG, "MSG_NEW_DEVICE_STATE");
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, i2, this.mHeadsetState, ""));
        this.mHeadsetState = i2;
    }

    public final void setDevicesState(int i, int i2, String str) {
        synchronized (this.mLock) {
            int i3 = 1;
            int i4 = 63;
            while (i4 != 0) {
                if ((i3 & i4) != 0) {
                    setDeviceStateLocked(i3, i, i2, str);
                    i4 &= ~i3;
                }
                i3 <<= 1;
            }
        }
    }

    public final void setDeviceStateLocked(int i, int i2, int i3, String str) {
        int i4 = i2 & i;
        if (i4 != (i3 & i)) {
            int i5 = 0;
            int i6 = i4 != 0 ? 1 : 0;
            int i7 = 4;
            if (i == 1) {
                i5 = -2147483632;
            } else if (i == 2) {
                i7 = 8;
            } else if (i == 32) {
                i7 = IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES;
            } else if (i == 4) {
                i7 = IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES;
            } else if (i == 8) {
                i7 = IInstalld.FLAG_USE_QUOTA;
            } else if (i != 16) {
                Slog.e(TAG, "setDeviceState() invalid headset type: " + i);
                return;
            } else {
                i7 = 1024;
            }
            if (i7 != 0) {
                this.mAudioManager.setWiredDeviceConnectionState(i7, i6, "", str);
            }
            if (i5 != 0) {
                this.mAudioManager.setWiredDeviceConnectionState(i5, i6, "", str);
            }
        }
    }

    /* loaded from: classes.dex */
    public class WiredAccessoryObserver extends UEventObserver {
        public final List<UEventInfo> mUEventInfo = makeObservedUEventList();

        public WiredAccessoryObserver() {
        }

        public void init() {
            int i;
            synchronized (WiredAccessoryManager.this.mLock) {
                char[] cArr = new char[1024];
                for (int i2 = 0; i2 < this.mUEventInfo.size(); i2++) {
                    UEventInfo uEventInfo = this.mUEventInfo.get(i2);
                    try {
                        try {
                            FileReader fileReader = new FileReader(uEventInfo.getSwitchStatePath());
                            int read = fileReader.read(cArr, 0, 1024);
                            fileReader.close();
                            int parseInt = Integer.parseInt(new String(cArr, 0, read).trim());
                            if (parseInt > 0) {
                                updateStateLocked(uEventInfo.getDevPath(), uEventInfo.getDevName(), parseInt);
                            }
                        } catch (Exception e) {
                            Slog.e(WiredAccessoryManager.TAG, "Error while attempting to determine initial switch state for " + uEventInfo.getDevName(), e);
                        }
                    } catch (FileNotFoundException unused) {
                        Slog.w(WiredAccessoryManager.TAG, uEventInfo.getSwitchStatePath() + " not found while attempting to determine initial switch state");
                    }
                }
            }
            for (i = 0; i < this.mUEventInfo.size(); i++) {
                startObserving("DEVPATH=" + this.mUEventInfo.get(i).getDevPath());
            }
        }

        public final List<UEventInfo> makeObservedUEventList() {
            ArrayList arrayList = new ArrayList();
            if (!WiredAccessoryManager.this.mUseDevInputEventForAudioJack) {
                UEventInfo uEventInfo = new UEventInfo("h2w", 1, 2, 32);
                if (uEventInfo.checkSwitchExists()) {
                    arrayList.add(uEventInfo);
                } else {
                    Slog.w(WiredAccessoryManager.TAG, "This kernel does not have wired headset support");
                }
            }
            UEventInfo uEventInfo2 = new UEventInfo("usb_audio", 4, 8, 0);
            if (uEventInfo2.checkSwitchExists()) {
                arrayList.add(uEventInfo2);
            } else {
                Slog.w(WiredAccessoryManager.TAG, "This kernel does not have usb audio support");
            }
            UEventInfo uEventInfo3 = new UEventInfo("hdmi_audio", 16, 0, 0);
            if (uEventInfo3.checkSwitchExists()) {
                arrayList.add(uEventInfo3);
            } else {
                UEventInfo uEventInfo4 = new UEventInfo("hdmi", 16, 0, 0);
                if (uEventInfo4.checkSwitchExists()) {
                    arrayList.add(uEventInfo4);
                } else {
                    Slog.w(WiredAccessoryManager.TAG, "This kernel does not have HDMI audio support");
                }
            }
            return arrayList;
        }

        public void onUEvent(UEventObserver.UEvent uEvent) {
            try {
                String str = uEvent.get("DEVPATH");
                String str2 = uEvent.get("SWITCH_NAME");
                int parseInt = Integer.parseInt(uEvent.get("SWITCH_STATE"));
                synchronized (WiredAccessoryManager.this.mLock) {
                    updateStateLocked(str, str2, parseInt);
                }
            } catch (NumberFormatException unused) {
                String str3 = WiredAccessoryManager.TAG;
                Slog.e(str3, "Could not parse switch state from event " + uEvent);
            }
        }

        public final void updateStateLocked(String str, String str2, int i) {
            for (int i2 = 0; i2 < this.mUEventInfo.size(); i2++) {
                UEventInfo uEventInfo = this.mUEventInfo.get(i2);
                if (str.equals(uEventInfo.getDevPath())) {
                    WiredAccessoryManager wiredAccessoryManager = WiredAccessoryManager.this;
                    wiredAccessoryManager.updateLocked(str2, uEventInfo.computeNewHeadsetState(wiredAccessoryManager.mHeadsetState, i));
                    return;
                }
            }
        }

        /* loaded from: classes.dex */
        public final class UEventInfo {
            public final String mDevName;
            public final int mState1Bits;
            public final int mState2Bits;
            public final int mStateNbits;

            public UEventInfo(String str, int i, int i2, int i3) {
                this.mDevName = str;
                this.mState1Bits = i;
                this.mState2Bits = i2;
                this.mStateNbits = i3;
            }

            public String getDevName() {
                return this.mDevName;
            }

            public String getDevPath() {
                return String.format(Locale.US, "/devices/virtual/switch/%s", this.mDevName);
            }

            public String getSwitchStatePath() {
                return String.format(Locale.US, "/sys/class/switch/%s/state", this.mDevName);
            }

            public boolean checkSwitchExists() {
                return new File(getSwitchStatePath()).exists();
            }

            public int computeNewHeadsetState(int i, int i2) {
                int i3 = this.mState1Bits;
                int i4 = this.mState2Bits;
                int i5 = this.mStateNbits;
                int i6 = ~(i3 | i4 | i5);
                if (i2 != 1) {
                    i3 = i2 == 2 ? i4 : i2 == i5 ? i5 : 0;
                }
                return (i & i6) | i3;
            }
        }
    }

    /* loaded from: classes.dex */
    public class WiredAccessoryExtconObserver extends ExtconStateObserver<Pair<Integer, Integer>> {
        public final List<ExtconUEventObserver.ExtconInfo> mExtconInfos = ExtconUEventObserver.ExtconInfo.getExtconInfoForTypes(new String[]{"HEADPHONE", "MICROPHONE", "HDMI", "LINE-OUT"});

        public WiredAccessoryExtconObserver() {
        }

        /* JADX WARN: Removed duplicated region for block: B:14:0x005c  */
        /* JADX WARN: Removed duplicated region for block: B:21:0x0063 A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final void init() {
            Pair<Integer, Integer> pair;
            for (ExtconUEventObserver.ExtconInfo extconInfo : this.mExtconInfos) {
                try {
                    pair = parseStateFromFile(extconInfo);
                } catch (FileNotFoundException e) {
                    String str = WiredAccessoryManager.TAG;
                    Slog.w(str, extconInfo.getStatePath() + " not found while attempting to determine initial state", e);
                    pair = null;
                    if (pair != null) {
                    }
                    startObserving(extconInfo);
                } catch (IOException e2) {
                    String str2 = WiredAccessoryManager.TAG;
                    Slog.e(str2, "Error reading " + extconInfo.getStatePath() + " while attempting to determine initial state", e2);
                    pair = null;
                    if (pair != null) {
                    }
                    startObserving(extconInfo);
                }
                if (pair != null) {
                    updateState(extconInfo, extconInfo.getName(), pair);
                }
                startObserving(extconInfo);
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.ExtconStateObserver
        public Pair<Integer, Integer> parseState(ExtconUEventObserver.ExtconInfo extconInfo, String str) {
            int[] iArr = {0, 0};
            if (extconInfo.hasCableType("HEADPHONE")) {
                WiredAccessoryManager.updateBit(iArr, 2, str, "HEADPHONE");
            }
            if (extconInfo.hasCableType("MICROPHONE")) {
                WiredAccessoryManager.updateBit(iArr, 1, str, "MICROPHONE");
            }
            if (extconInfo.hasCableType("HDMI")) {
                WiredAccessoryManager.updateBit(iArr, 16, str, "HDMI");
            }
            if (extconInfo.hasCableType("LINE-OUT")) {
                WiredAccessoryManager.updateBit(iArr, 32, str, "LINE-OUT");
            }
            return Pair.create(Integer.valueOf(iArr[0]), Integer.valueOf(iArr[1]));
        }

        @Override // com.android.server.ExtconStateObserver
        public void updateState(ExtconUEventObserver.ExtconInfo extconInfo, String str, Pair<Integer, Integer> pair) {
            synchronized (WiredAccessoryManager.this.mLock) {
                int intValue = ((Integer) pair.first).intValue();
                int intValue2 = ((Integer) pair.second).intValue();
                WiredAccessoryManager wiredAccessoryManager = WiredAccessoryManager.this;
                wiredAccessoryManager.updateLocked(str, (intValue2 & intValue) | (wiredAccessoryManager.mHeadsetState & (~((~intValue2) & intValue))));
            }
        }
    }

    public static void updateBit(int[] iArr, int i, String str, String str2) {
        iArr[0] = iArr[0] | i;
        if (str.contains(str2 + "=1")) {
            iArr[0] = iArr[0] | i;
            iArr[1] = i | iArr[1];
            return;
        }
        if (str.contains(str2 + "=0")) {
            iArr[0] = iArr[0] | i;
            iArr[1] = (~i) & iArr[1];
        }
    }
}
