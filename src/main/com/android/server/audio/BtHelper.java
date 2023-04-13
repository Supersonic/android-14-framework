package com.android.server.audio;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothLeAudio;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioDeviceAttributes;
import android.media.AudioSystem;
import android.media.BluetoothProfileConnectionInfo;
import android.os.Binder;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.audio.AudioDeviceBroker;
import com.android.server.utils.EventLogger;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class BtHelper {
    public BluetoothA2dp mA2dp;
    public BluetoothHeadset mBluetoothHeadset;
    public BluetoothDevice mBluetoothHeadsetDevice;
    public final AudioDeviceBroker mDeviceBroker;
    public BluetoothHearingAid mHearingAid;
    public BluetoothLeAudio mLeAudio;
    public int mScoAudioMode;
    public int mScoAudioState;
    public int mScoConnectionState;
    public boolean mAvrcpAbsVolSupported = false;
    public BluetoothProfile.ServiceListener mBluetoothProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.server.audio.BtHelper.1
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            if (i == 1 || i == 2 || i == 21 || i == 22) {
                EventLogger eventLogger = AudioService.sDeviceLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("BT profile service: connecting " + BluetoothProfile.getProfileName(i) + " profile"));
                BtHelper.this.mDeviceBroker.postBtProfileConnected(i, bluetoothProfile);
            }
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
            if (i == 1 || i == 2 || i == 21 || i == 22) {
                EventLogger eventLogger = AudioService.sDeviceLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("BT profile service: disconnecting " + BluetoothProfile.getProfileName(i) + " profile"));
                BtHelper.this.mDeviceBroker.postBtProfileDisconnected(i);
            }
        }
    };

    public BtHelper(AudioDeviceBroker audioDeviceBroker) {
        this.mDeviceBroker = audioDeviceBroker;
    }

    public static String scoAudioModeToString(int i) {
        if (i != -1) {
            if (i != 0) {
                if (i != 2) {
                    return "SCO_MODE_(" + i + ")";
                }
                return "SCO_MODE_VR";
            }
            return "SCO_MODE_VIRTUAL_CALL";
        }
        return "SCO_MODE_UNDEFINED";
    }

    public static String scoAudioStateToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 5) {
                            return "SCO_STATE_(" + i + ")";
                        }
                        return "SCO_STATE_DEACTIVATING";
                    }
                    return "SCO_STATE_ACTIVE_INTERNAL";
                }
                return "SCO_STATE_ACTIVE_EXTERNAL";
            }
            return "SCO_STATE_ACTIVATE_REQ";
        }
        return "SCO_STATE_INACTIVE";
    }

    /* loaded from: classes.dex */
    public static class BluetoothA2dpDeviceInfo {
        public final BluetoothDevice mBtDevice;
        public final int mCodec;
        public final int mVolume;

        public BluetoothA2dpDeviceInfo(BluetoothDevice bluetoothDevice, int i, int i2) {
            this.mBtDevice = bluetoothDevice;
            this.mVolume = i;
            this.mCodec = i2;
        }

        public BluetoothDevice getBtDevice() {
            return this.mBtDevice;
        }

        public int getVolume() {
            return this.mVolume;
        }

        public int getCodec() {
            return this.mCodec;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (obj instanceof BluetoothA2dpDeviceInfo) {
                return this.mBtDevice.equals(((BluetoothA2dpDeviceInfo) obj).getBtDevice());
            }
            return false;
        }
    }

    public static String a2dpDeviceEventToString(int i) {
        if (i != 0) {
            if (i != 1) {
                return new String("invalid event:" + i);
            }
            return "ACTIVE_DEVICE_CHANGE";
        }
        return "DEVICE_CONFIG_CHANGE";
    }

    public static String getName(BluetoothDevice bluetoothDevice) {
        String name = bluetoothDevice.getName();
        return name == null ? "" : name;
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void onSystemReady() {
        this.mScoConnectionState = -1;
        resetBluetoothSco();
        getBluetoothHeadset();
        Intent intent = new Intent("android.media.SCO_AUDIO_STATE_CHANGED");
        intent.putExtra("android.media.extra.SCO_AUDIO_STATE", 0);
        sendStickyBroadcastToAll(intent);
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            defaultAdapter.getProfileProxy(this.mDeviceBroker.getContext(), this.mBluetoothProfileServiceListener, 2);
            defaultAdapter.getProfileProxy(this.mDeviceBroker.getContext(), this.mBluetoothProfileServiceListener, 21);
            defaultAdapter.getProfileProxy(this.mDeviceBroker.getContext(), this.mBluetoothProfileServiceListener, 22);
        }
    }

    public synchronized void onAudioServerDiedRestoreA2dp() {
        this.mDeviceBroker.setForceUse_Async(1, this.mDeviceBroker.getBluetoothA2dpEnabled() ? 0 : 10, "onAudioServerDied()");
    }

    public synchronized void setAvrcpAbsoluteVolumeSupported(boolean z) {
        this.mAvrcpAbsVolSupported = z;
        Log.i("AS.BtHelper", "setAvrcpAbsoluteVolumeSupported supported=" + z);
    }

    public synchronized void setAvrcpAbsoluteVolumeIndex(int i) {
        if (!this.mAvrcpAbsVolSupported) {
            AudioService.sVolumeLogger.enqueue(new EventLogger.StringEvent("setAvrcpAbsoluteVolumeIndex: abs vol not supported ").printLog("AS.BtHelper"));
            return;
        }
        AudioService.sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(4, i));
        try {
            this.mA2dp.setAvrcpAbsoluteVolume(i);
        } catch (Exception e) {
            Log.e("AS.BtHelper", "Exception while changing abs volume", e);
        }
    }

    public synchronized int getA2dpCodec(BluetoothDevice bluetoothDevice) {
        BluetoothCodecStatus bluetoothCodecStatus;
        BluetoothA2dp bluetoothA2dp = this.mA2dp;
        if (bluetoothA2dp == null) {
            return 0;
        }
        try {
            bluetoothCodecStatus = bluetoothA2dp.getCodecStatus(bluetoothDevice);
        } catch (Exception e) {
            Log.e("AS.BtHelper", "Exception while getting status of " + bluetoothDevice, e);
            bluetoothCodecStatus = null;
        }
        if (bluetoothCodecStatus == null) {
            return 0;
        }
        BluetoothCodecConfig codecConfig = bluetoothCodecStatus.getCodecConfig();
        if (codecConfig == null) {
            return 0;
        }
        return AudioSystem.bluetoothCodecToAudioFormat(codecConfig.getCodecType());
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void receiveBtEvent(Intent intent) {
        String action = intent.getAction();
        Log.i("AS.BtHelper", "receiveBtEvent action: " + action + " mScoAudioState: " + this.mScoAudioState);
        if (action.equals("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED")) {
            setBtScoActiveDevice((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE", BluetoothDevice.class));
        } else if (action.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
            int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
            Log.i("AS.BtHelper", "receiveBtEvent ACTION_AUDIO_STATE_CHANGED: " + intExtra);
            this.mDeviceBroker.postScoAudioStateChanged(intExtra);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public void onScoAudioStateChanged(int i) {
        BluetoothHeadset bluetoothHeadset;
        BluetoothDevice bluetoothDevice;
        int i2 = 2;
        boolean z = false;
        switch (i) {
            case 10:
                this.mDeviceBroker.setBluetoothScoOn(false, "BtHelper.receiveBtEvent");
                if (this.mScoAudioState == 1 && (bluetoothHeadset = this.mBluetoothHeadset) != null && (bluetoothDevice = this.mBluetoothHeadsetDevice) != null && connectBluetoothScoAudioHelper(bluetoothHeadset, bluetoothDevice, this.mScoAudioMode)) {
                    this.mScoAudioState = 3;
                    break;
                } else {
                    r4 = this.mScoAudioState != 2;
                    this.mScoAudioState = 0;
                    i2 = 0;
                    break;
                }
                break;
            case 11:
                int i3 = this.mScoAudioState;
                if (i3 != 3 && i3 != 4) {
                    this.mScoAudioState = 2;
                }
                i2 = -1;
                r4 = z;
                break;
            case 12:
                int i4 = this.mScoAudioState;
                if (i4 != 3 && i4 != 4) {
                    this.mScoAudioState = 2;
                } else if (this.mDeviceBroker.isBluetoothScoRequested()) {
                    z = true;
                }
                this.mDeviceBroker.setBluetoothScoOn(true, "BtHelper.receiveBtEvent");
                i2 = 1;
                r4 = z;
                break;
            default:
                i2 = -1;
                r4 = z;
                break;
        }
        if (r4) {
            broadcastScoConnectionState(i2);
            Intent intent = new Intent("android.media.SCO_AUDIO_STATE_CHANGED");
            intent.putExtra("android.media.extra.SCO_AUDIO_STATE", i2);
            sendStickyBroadcastToAll(intent);
        }
    }

    public synchronized boolean isBluetoothScoOn() {
        BluetoothDevice bluetoothDevice;
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
        if (bluetoothHeadset != null && (bluetoothDevice = this.mBluetoothHeadsetDevice) != null) {
            return bluetoothHeadset.getAudioState(bluetoothDevice) == 12;
        }
        return false;
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized boolean startBluetoothSco(int i, String str) {
        AudioService.sDeviceLogger.enqueue(new EventLogger.StringEvent(str));
        return requestScoState(12, i);
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized boolean stopBluetoothSco(String str) {
        AudioService.sDeviceLogger.enqueue(new EventLogger.StringEvent(str));
        return requestScoState(10, 0);
    }

    public synchronized void setLeAudioVolume(int i, int i2, int i3) {
        if (this.mLeAudio == null) {
            return;
        }
        int round = (int) Math.round((i * 255.0d) / i2);
        AudioService.sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(10, i, i2));
        try {
            this.mLeAudio.setVolume(round);
        } catch (Exception e) {
            Log.e("AS.BtHelper", "Exception while setting LE volume", e);
        }
    }

    public synchronized void setHearingAidVolume(int i, int i2, boolean z) {
        if (this.mHearingAid == null) {
            return;
        }
        int streamVolumeDB = (int) AudioSystem.getStreamVolumeDB(i2, i / 10, 134217728);
        if (streamVolumeDB < -128) {
            streamVolumeDB = -128;
        }
        if (z) {
            AudioService.sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(3, i, streamVolumeDB));
        }
        try {
            this.mHearingAid.setVolume(streamVolumeDB);
        } catch (Exception e) {
            Log.i("AS.BtHelper", "Exception while setting hearing aid volume", e);
        }
    }

    public synchronized void onBroadcastScoConnectionState(int i) {
        if (i == this.mScoConnectionState) {
            return;
        }
        Intent intent = new Intent("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
        intent.putExtra("android.media.extra.SCO_AUDIO_STATE", i);
        intent.putExtra("android.media.extra.SCO_AUDIO_PREVIOUS_STATE", this.mScoConnectionState);
        sendStickyBroadcastToAll(intent);
        this.mScoConnectionState = i;
    }

    public synchronized void disconnectAllBluetoothProfiles() {
        this.mDeviceBroker.postBtProfileDisconnected(2);
        this.mDeviceBroker.postBtProfileDisconnected(11);
        this.mDeviceBroker.postBtProfileDisconnected(1);
        this.mDeviceBroker.postBtProfileDisconnected(21);
        this.mDeviceBroker.postBtProfileDisconnected(22);
        this.mDeviceBroker.postBtProfileDisconnected(26);
    }

    public synchronized void resetBluetoothSco() {
        this.mScoAudioState = 0;
        broadcastScoConnectionState(0);
        AudioSystem.setParameters("A2dpSuspended=false");
        this.mDeviceBroker.setBluetoothScoOn(false, "resetBluetoothSco");
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void disconnectHeadset() {
        setBtScoActiveDevice(null);
        this.mBluetoothHeadset = null;
    }

    public synchronized void onBtProfileDisconnected(int i) {
        if (i == 2) {
            this.mA2dp = null;
        } else if (i == 11 || i == 26) {
            Log.e("AS.BtHelper", "onBtProfileDisconnected: Not a profile handled by BtHelper " + BluetoothProfile.getProfileName(i));
        } else if (i == 21) {
            this.mHearingAid = null;
        } else if (i == 22) {
            this.mLeAudio = null;
        } else {
            Log.e("AS.BtHelper", "onBtProfileDisconnected: Not a valid profile to disconnect " + BluetoothProfile.getProfileName(i));
        }
    }

    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void onBtProfileConnected(int i, BluetoothProfile bluetoothProfile) {
        if (i == 1) {
            onHeadsetProfileConnected((BluetoothHeadset) bluetoothProfile);
            return;
        }
        if (i == 2) {
            this.mA2dp = (BluetoothA2dp) bluetoothProfile;
        } else if (i == 21) {
            this.mHearingAid = (BluetoothHearingAid) bluetoothProfile;
        } else if (i == 22) {
            this.mLeAudio = (BluetoothLeAudio) bluetoothProfile;
        }
        List<BluetoothDevice> connectedDevices = bluetoothProfile.getConnectedDevices();
        if (connectedDevices.isEmpty()) {
            return;
        }
        BluetoothDevice bluetoothDevice = connectedDevices.get(0);
        if (bluetoothProfile.getConnectionState(bluetoothDevice) == 2) {
            this.mDeviceBroker.queueOnBluetoothActiveDeviceChanged(new AudioDeviceBroker.BtDeviceChangedData(bluetoothDevice, null, new BluetoothProfileConnectionInfo(i), "mBluetoothProfileServiceListener"));
        } else {
            this.mDeviceBroker.queueOnBluetoothActiveDeviceChanged(new AudioDeviceBroker.BtDeviceChangedData(null, bluetoothDevice, new BluetoothProfileConnectionInfo(i), "mBluetoothProfileServiceListener"));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x005d A[Catch: all -> 0x0064, TRY_LEAVE, TryCatch #0 {, blocks: (B:3:0x0001, B:5:0x0013, B:6:0x0017, B:8:0x001e, B:10:0x0026, B:16:0x0035, B:18:0x0039, B:23:0x0042, B:25:0x004a, B:31:0x005d, B:26:0x004e, B:28:0x0056), top: B:37:0x0001 }] */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized void onHeadsetProfileConnected(BluetoothHeadset bluetoothHeadset) {
        boolean z;
        BluetoothDevice bluetoothDevice;
        this.mDeviceBroker.handleCancelFailureToConnectToBtHeadsetService();
        this.mBluetoothHeadset = bluetoothHeadset;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        List emptyList = Collections.emptyList();
        if (defaultAdapter != null) {
            emptyList = defaultAdapter.getActiveDevices(1);
        }
        setBtScoActiveDevice(emptyList.size() > 0 ? (BluetoothDevice) emptyList.get(0) : null);
        checkScoAudioState();
        int i = this.mScoAudioState;
        if (i == 1 || i == 4) {
            BluetoothHeadset bluetoothHeadset2 = this.mBluetoothHeadset;
            if (bluetoothHeadset2 != null && (bluetoothDevice = this.mBluetoothHeadsetDevice) != null) {
                if (i == 1) {
                    z = connectBluetoothScoAudioHelper(bluetoothHeadset2, bluetoothDevice, this.mScoAudioMode);
                    if (z) {
                        this.mScoAudioState = 3;
                    }
                } else if (i == 4) {
                    z = disconnectBluetoothScoAudioHelper(bluetoothHeadset2, bluetoothDevice, this.mScoAudioMode);
                    if (z) {
                        this.mScoAudioState = 5;
                    }
                }
                if (!z) {
                    this.mScoAudioState = 0;
                    broadcastScoConnectionState(0);
                }
            }
            z = false;
            if (!z) {
            }
        }
    }

    public final void broadcastScoConnectionState(int i) {
        this.mDeviceBroker.postBroadcastScoConnectionState(i);
    }

    public AudioDeviceAttributes getHeadsetAudioDevice() {
        BluetoothDevice bluetoothDevice = this.mBluetoothHeadsetDevice;
        if (bluetoothDevice == null) {
            return null;
        }
        return btHeadsetDeviceToAudioDevice(bluetoothDevice);
    }

    public final AudioDeviceAttributes btHeadsetDeviceToAudioDevice(BluetoothDevice bluetoothDevice) {
        int i = 16;
        if (bluetoothDevice == null) {
            return new AudioDeviceAttributes(16, "");
        }
        String address = bluetoothDevice.getAddress();
        String str = BluetoothAdapter.checkBluetoothAddress(address) ? address : "";
        BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
        if (bluetoothClass != null) {
            int deviceClass = bluetoothClass.getDeviceClass();
            if (deviceClass == 1028 || deviceClass == 1032) {
                i = 32;
            } else if (deviceClass == 1056) {
                i = 64;
            }
        }
        return new AudioDeviceAttributes(i, str);
    }

    public final boolean handleBtScoActiveDeviceChange(BluetoothDevice bluetoothDevice, boolean z) {
        boolean z2;
        if (bluetoothDevice == null) {
            return true;
        }
        AudioDeviceAttributes btHeadsetDeviceToAudioDevice = btHeadsetDeviceToAudioDevice(bluetoothDevice);
        String name = getName(bluetoothDevice);
        if (z) {
            z2 = this.mDeviceBroker.handleDeviceConnection(new AudioDeviceAttributes(btHeadsetDeviceToAudioDevice.getInternalType(), btHeadsetDeviceToAudioDevice.getAddress(), name), z) | false;
        } else {
            int[] iArr = {16, 32, 64};
            boolean z3 = false;
            for (int i = 0; i < 3; i++) {
                z3 |= this.mDeviceBroker.handleDeviceConnection(new AudioDeviceAttributes(iArr[i], btHeadsetDeviceToAudioDevice.getAddress(), name), z);
            }
            z2 = z3;
        }
        return this.mDeviceBroker.handleDeviceConnection(new AudioDeviceAttributes(-2147483640, btHeadsetDeviceToAudioDevice.getAddress(), name), z) && z2;
    }

    public final String getAnonymizedAddress(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice == null ? "(null)" : bluetoothDevice.getAnonymizedAddress();
    }

    @GuardedBy({"BtHelper.this"})
    public final void setBtScoActiveDevice(BluetoothDevice bluetoothDevice) {
        Log.i("AS.BtHelper", "setBtScoActiveDevice: " + getAnonymizedAddress(this.mBluetoothHeadsetDevice) + " -> " + getAnonymizedAddress(bluetoothDevice));
        BluetoothDevice bluetoothDevice2 = this.mBluetoothHeadsetDevice;
        if (Objects.equals(bluetoothDevice, bluetoothDevice2)) {
            return;
        }
        if (!handleBtScoActiveDeviceChange(bluetoothDevice2, false)) {
            Log.w("AS.BtHelper", "setBtScoActiveDevice() failed to remove previous device " + getAnonymizedAddress(bluetoothDevice2));
        }
        if (!handleBtScoActiveDeviceChange(bluetoothDevice, true)) {
            Log.e("AS.BtHelper", "setBtScoActiveDevice() failed to add new device " + getAnonymizedAddress(bluetoothDevice));
            bluetoothDevice = null;
        }
        this.mBluetoothHeadsetDevice = bluetoothDevice;
        if (bluetoothDevice == null) {
            resetBluetoothSco();
        }
    }

    @GuardedBy({"BtHelper.this"})
    public final boolean requestScoState(int i, int i2) {
        checkScoAudioState();
        if (i == 12) {
            broadcastScoConnectionState(2);
            int i3 = this.mScoAudioState;
            if (i3 == 0) {
                this.mScoAudioMode = i2;
                if (i2 == -1) {
                    this.mScoAudioMode = 0;
                    if (this.mBluetoothHeadsetDevice != null) {
                        ContentResolver contentResolver = this.mDeviceBroker.getContentResolver();
                        int i4 = Settings.Global.getInt(contentResolver, "bluetooth_sco_channel_" + this.mBluetoothHeadsetDevice.getAddress(), 0);
                        this.mScoAudioMode = i4;
                        if (i4 > 2 || i4 < 0) {
                            this.mScoAudioMode = 0;
                        }
                    }
                }
                BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
                if (bluetoothHeadset == null) {
                    if (getBluetoothHeadset()) {
                        this.mScoAudioState = 1;
                    } else {
                        Log.w("AS.BtHelper", "requestScoState: getBluetoothHeadset failed during connection, mScoAudioMode=" + this.mScoAudioMode);
                        broadcastScoConnectionState(0);
                        return false;
                    }
                } else {
                    BluetoothDevice bluetoothDevice = this.mBluetoothHeadsetDevice;
                    if (bluetoothDevice == null) {
                        Log.w("AS.BtHelper", "requestScoState: no active device while connecting, mScoAudioMode=" + this.mScoAudioMode);
                        broadcastScoConnectionState(0);
                        return false;
                    } else if (connectBluetoothScoAudioHelper(bluetoothHeadset, bluetoothDevice, this.mScoAudioMode)) {
                        this.mScoAudioState = 3;
                    } else {
                        Log.w("AS.BtHelper", "requestScoState: connect to " + getAnonymizedAddress(this.mBluetoothHeadsetDevice) + " failed, mScoAudioMode=" + this.mScoAudioMode);
                        broadcastScoConnectionState(0);
                        return false;
                    }
                }
            } else if (i3 == 2) {
                broadcastScoConnectionState(1);
            } else if (i3 == 3) {
                Log.w("AS.BtHelper", "requestScoState: already in ACTIVE mode, simply return");
            } else if (i3 == 4) {
                this.mScoAudioState = 3;
                broadcastScoConnectionState(1);
            } else if (i3 == 5) {
                this.mScoAudioState = 1;
            } else {
                Log.w("AS.BtHelper", "requestScoState: failed to connect in state " + this.mScoAudioState + ", scoAudioMode=" + i2);
                broadcastScoConnectionState(0);
                return false;
            }
        } else if (i == 10) {
            int i5 = this.mScoAudioState;
            if (i5 == 1) {
                this.mScoAudioState = 0;
                broadcastScoConnectionState(0);
            } else if (i5 == 3) {
                BluetoothHeadset bluetoothHeadset2 = this.mBluetoothHeadset;
                if (bluetoothHeadset2 == null) {
                    if (getBluetoothHeadset()) {
                        this.mScoAudioState = 4;
                    } else {
                        Log.w("AS.BtHelper", "requestScoState: getBluetoothHeadset failed during disconnection, mScoAudioMode=" + this.mScoAudioMode);
                        this.mScoAudioState = 0;
                        broadcastScoConnectionState(0);
                        return false;
                    }
                } else {
                    BluetoothDevice bluetoothDevice2 = this.mBluetoothHeadsetDevice;
                    if (bluetoothDevice2 == null) {
                        this.mScoAudioState = 0;
                        broadcastScoConnectionState(0);
                    } else if (disconnectBluetoothScoAudioHelper(bluetoothHeadset2, bluetoothDevice2, this.mScoAudioMode)) {
                        this.mScoAudioState = 5;
                    } else {
                        this.mScoAudioState = 0;
                        broadcastScoConnectionState(0);
                    }
                }
            } else {
                Log.w("AS.BtHelper", "requestScoState: failed to disconnect in state " + this.mScoAudioState + ", scoAudioMode=" + i2);
                broadcastScoConnectionState(0);
                return false;
            }
        }
        return true;
    }

    public final void sendStickyBroadcastToAll(Intent intent) {
        intent.addFlags(268435456);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mDeviceBroker.getContext().sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static boolean disconnectBluetoothScoAudioHelper(BluetoothHeadset bluetoothHeadset, BluetoothDevice bluetoothDevice, int i) {
        if (i != 0) {
            if (i != 2) {
                return false;
            }
            return bluetoothHeadset.stopVoiceRecognition(bluetoothDevice);
        }
        return bluetoothHeadset.stopScoUsingVirtualVoiceCall();
    }

    public static boolean connectBluetoothScoAudioHelper(BluetoothHeadset bluetoothHeadset, BluetoothDevice bluetoothDevice, int i) {
        if (i != 0) {
            if (i != 2) {
                return false;
            }
            return bluetoothHeadset.startVoiceRecognition(bluetoothDevice);
        }
        return bluetoothHeadset.startScoUsingVirtualVoiceCall();
    }

    public final void checkScoAudioState() {
        BluetoothDevice bluetoothDevice;
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
        if (bluetoothHeadset == null || (bluetoothDevice = this.mBluetoothHeadsetDevice) == null || this.mScoAudioState != 0 || bluetoothHeadset.getAudioState(bluetoothDevice) == 10) {
            return;
        }
        this.mScoAudioState = 2;
    }

    public final boolean getBluetoothHeadset() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean profileProxy = defaultAdapter != null ? defaultAdapter.getProfileProxy(this.mDeviceBroker.getContext(), this.mBluetoothProfileServiceListener, 1) : false;
        this.mDeviceBroker.handleFailureToConnectToBtHeadsetService(profileProxy ? 3000 : 0);
        return profileProxy;
    }

    public static String btDeviceClassToString(int i) {
        switch (i) {
            case 1024:
                return "AUDIO_VIDEO_UNCATEGORIZED";
            case 1028:
                return "AUDIO_VIDEO_WEARABLE_HEADSET";
            case 1032:
                return "AUDIO_VIDEO_HANDSFREE";
            case 1036:
                return "AUDIO_VIDEO_RESERVED_0x040C";
            case 1040:
                return "AUDIO_VIDEO_MICROPHONE";
            case 1044:
                return "AUDIO_VIDEO_LOUDSPEAKER";
            case 1048:
                return "AUDIO_VIDEO_HEADPHONES";
            case 1052:
                return "AUDIO_VIDEO_PORTABLE_AUDIO";
            case 1056:
                return "AUDIO_VIDEO_CAR_AUDIO";
            case 1060:
                return "AUDIO_VIDEO_SET_TOP_BOX";
            case 1064:
                return "AUDIO_VIDEO_HIFI_AUDIO";
            case 1068:
                return "AUDIO_VIDEO_VCR";
            case 1072:
                return "AUDIO_VIDEO_VIDEO_CAMERA";
            case 1076:
                return "AUDIO_VIDEO_CAMCORDER";
            case 1080:
                return "AUDIO_VIDEO_VIDEO_MONITOR";
            case 1084:
                return "AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER";
            case 1088:
                return "AUDIO_VIDEO_VIDEO_CONFERENCING";
            case 1092:
                return "AUDIO_VIDEO_RESERVED_0x0444";
            case 1096:
                return "AUDIO_VIDEO_VIDEO_GAMING_TOY";
            default:
                return TextUtils.formatSimple("0x%04x", new Object[]{Integer.valueOf(i)});
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        BluetoothClass bluetoothClass;
        printWriter.println("\n" + str + "mBluetoothHeadset: " + this.mBluetoothHeadset);
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("mBluetoothHeadsetDevice: ");
        sb.append(this.mBluetoothHeadsetDevice);
        printWriter.println(sb.toString());
        BluetoothDevice bluetoothDevice = this.mBluetoothHeadsetDevice;
        if (bluetoothDevice != null && (bluetoothClass = bluetoothDevice.getBluetoothClass()) != null) {
            printWriter.println(str + "mBluetoothHeadsetDevice.DeviceClass: " + btDeviceClassToString(bluetoothClass.getDeviceClass()));
        }
        printWriter.println(str + "mScoAudioState: " + scoAudioStateToString(this.mScoAudioState));
        printWriter.println(str + "mScoAudioMode: " + scoAudioModeToString(this.mScoAudioMode));
        printWriter.println("\n" + str + "mHearingAid: " + this.mHearingAid);
        printWriter.println("\n" + str + "mLeAudio: " + this.mLeAudio);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append("mA2dp: ");
        sb2.append(this.mA2dp);
        printWriter.println(sb2.toString());
        printWriter.println(str + "mAvrcpAbsVolSupported: " + this.mAvrcpAbsVolSupported);
    }
}
